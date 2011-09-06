/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.controllers.component;

import java.awt.event.MouseEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openshapa.event.component.CarriageEvent;
import org.openshapa.event.component.CarriageEventAdapter;
import org.openshapa.event.component.CarriageEventListener;
import org.openshapa.event.component.TrackMouseEventListener;

import org.openshapa.models.component.MixerModel;
import org.openshapa.models.component.RegionConstants;
import org.openshapa.models.component.RegionState;
import org.openshapa.models.component.TrackModel;
import org.openshapa.models.component.ViewportState;
import org.openshapa.models.id.Identifier;

import org.openshapa.plugins.CustomActions;
import org.openshapa.plugins.ViewerStateListener;

import org.openshapa.views.component.TrackPainter;
import org.openshapa.views.component.TracksEditorPainter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * Tracks editor controller is responsible for managing multiple TrackController
 * instances.
 */
public final class TracksEditorController implements TrackMouseEventListener {

    /** Main UI panel. */
    private JPanel editingPanel;

    /** UI component for displaying a snap position. */
    private final SnapMarkerController snapMarkerController;

    /** List of track controllers. */
    private final Map<Identifier, TrackController> tracks;

    private final MixerController mixerController;
    private final MixerModel mixerModel;

    /** Handles the selection model for tracks. */
    private final CarriageSelection selectionHandler;

    /**
     * Create a new tracks editor controller.
     */
    public TracksEditorController(final MixerController mixerController,
        final MixerModel mixerModel) {
        tracks = Maps.newLinkedHashMap();
        this.mixerController = mixerController;
        this.mixerModel = mixerModel;
        snapMarkerController = new SnapMarkerController(mixerModel);
        selectionHandler = new CarriageSelection();
        initView();
    }

    /**
     * Initialise UI elements.
     */
    private void initView() {
        editingPanel = new TracksEditorPainter();
    }

    /**
     * @return Main tracks editor view
     */
    public JComponent getView() {
        return editingPanel;
    }

    /**
     * @return The snap marker view
     */
    public JComponent getMarkerView() {
        return snapMarkerController.getView();
    }

    /**
     * Adds a new track to the interface.
     *
     * @param icon
     *            Icon associated with the track.
     * @param trackId
     *            Track identifier
     * @param trackName
     *            Name of the track.
     * @param duration
     *            Duration of the track in milliseconds.
     * @param offset
     *            Track offset in milliseconds.
     * @param listener
     *            Register the listener interested in {@link CarriageEvent}.
     *            Null if uninterested.
     * @param trackPainter
     *            The track painter to use.
     */
    public void addNewTrack(final Identifier trackId, final ImageIcon icon,
        final String trackName, final String mediaPath, final long duration,
        final long offset, final CarriageEventListener listener,
        final TrackPainter trackPainter) {

        // TrackController
        final TrackController trackController = new TrackController(mixerModel,
                trackPainter);
        trackController.setTrackInformation(trackId, icon, trackName, mediaPath,
            duration, offset);
        trackController.addBookmark(-1);

        if (duration < 0) {
            trackController.setErroneous(true);
        }

        if (listener != null) {
            trackController.addCarriageEventListener(listener);
        }

        trackController.addCarriageEventListener(selectionHandler);
        trackController.addTrackMouseEventListener(this);
        trackController.attachAsWindowListener();

        tracks.put(trackId, trackController);

        editingPanel.add(trackController.getView(),
            "pad 0 0 0 " + -RegionConstants.RMARKER_WIDTH + ", growx");
        editingPanel.invalidate();

        // BugzID:2391 - Make the newly added track visible.
        SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    editingPanel.scrollRectToVisible(
                        trackController.getView().getBounds());
                }
            });
    }

    /**
     * Bind track actions to a data viewer.
     *
     * @param trackId
     *            Track identifier.
     * @param actions
     *            Actions to bind with.
     */
    public void bindTrackActions(final Identifier trackId,
        final CustomActions actions) {

        TrackController tc = tracks.get(trackId);

        if (tc != null) {
            tc.bindTrackActions(actions);
        }
    }

    public ViewerStateListener getViewerStateListener(
        final Identifier trackId) {

        TrackController tc = tracks.get(trackId);

        return tc;
    }

    /**
     * Remove a specific track from the controller. Also deregisters the given
     * listener from the track.
     *
     * @param mediaPath absolute path to the media file.
     * @param listener listener to deregister, if any.
     * @return true if a track was removed, false otherwise.
     */
    public boolean removeTrack(final Identifier trackId,
        final CarriageEventListener listener) {

        if (tracks.containsKey(trackId)) {
            TrackController tc = tracks.remove(trackId);

            tc.removeCarriageEventListener(listener);
            tc.removeCarriageEventListener(selectionHandler);
            tc.removeTrackMouseEventListener(this);

            editingPanel.remove(tc.getView());
            editingPanel.validate();
            editingPanel.repaint();

            return true;

        } else {
            return false;
        }
    }

    /**
     * Remove all tracks from the controller.
     */
    public void removeAllTracks() {

        for (TrackController tc : tracks.values()) {
            tc.removeTrackMouseEventListener(this);
            tc.removeCarriageEventListener(selectionHandler);
        }

        tracks.clear();
        editingPanel.removeAll();
        editingPanel.repaint();
    }

    /**
     * Sets the track offset for the given media if it exists. If offset
     * snapping is enabled through {@link #setAllowSnap(boolean)}, then this
     * function will attempt to synchronize the track position with every other
     * track's position of interest. A position of interest includes time 0, start
     * of a track, bookmarked positions, end of a track, and the current needle
     * position.
     *
     * @param trackId
     *            Identifies a track
     * @param newOffset
     *            New track offset position
     * @param snapTemporalPosition
     *            If snapping is enabled, the closest position of interest to
     *            snapTemporalPosition will be used as the first candidate for
     *            synchronization.
     * @return true if the offset was set, false otherwise.
     */
    public boolean setTrackOffset(final Identifier trackId,
        final long newOffset, final long snapTemporalPosition) {

        TrackController tc = tracks.get(trackId);

        if (tc == null) {
            return false;
        }

        tc.setTrackOffset(newOffset);
        snapMarkerController.setMarkerTime(-1);

        SnapPoint snapPoint = snapOffset(trackId, snapTemporalPosition);
        tc.setMoveable(snapPoint == null);

        if (snapPoint == null) {
            snapMarkerController.setMarkerTime(-1);
        } else {
            snapMarkerController.setMarkerTime(snapPoint.snapMarkerPosition);
            tc.setTrackOffset(newOffset + snapPoint.snapOffset);
        }

        return true;
    }

    /**
     * Naive snap algorithm:
     * <ol>
     * <li>Compile a list of snap points for the given track.</li>
     * <li>Compile a list of candidate snap points from every other track.</li>
     * <li>Find a snap position by comparing the snap points for the given track
     * against every other track.</li>
     * <li>A candidate snap point is chosen as the new offset value if it is
     * within +/- 5 seconds of the snap point being compared against.</li>
     * <li>If no snap points are found, then return null.</li>
     * </ol>
     *
     * @param trackId
     *            Identifier of the track being moved.
     * @param temporalSnapPosition
     *            The snap position to start searching from.
     * @return see Javadoc for explanation.
     */
    private SnapPoint snapOffset(final Identifier trackId,
        final long temporalSnapPosition) {
        final ViewportState viewport = mixerModel.getViewportModel()
            .getViewport();

        // Points on other (non-selected) data tracks that can be used for
        // alignment.
        final List<Long> snapCandidates = Lists.newArrayList();

        // Points on the current/selected data track that may be used for
        // alignment against other data tracks.
        final List<Long> snapPoints = Lists.newArrayList();

        long longestDuration = 0;

        // add time zero as a candidate snap point
        if (viewport.isTimeInViewport(0)) {
            snapCandidates.add(0L);
        }

        // add region markers as snap candidates
        final RegionState region = mixerModel.getRegionModel().getRegion();

        if (viewport.isTimeInViewport(region.getRegionStart())) {
            snapCandidates.add(region.getRegionStart());
        }

        if (viewport.isTimeInViewport(region.getRegionEnd())) {
            snapCandidates.add(region.getRegionEnd());
        }

        // add the needle as a candidate snap point
        final long needlePosition = mixerController.getNeedleController()
            .getNeedleModel().getCurrentTime();

        if (viewport.isTimeInViewport(needlePosition)) {
            snapCandidates.add(needlePosition);
        }

        // Compile track and candidate snap points
        for (TrackController tc : tracks.values()) {
            final List<Long> snapList =
                tc.getTrackModel().getId().equals(trackId) ? snapPoints
                                                           : snapCandidates;

            // add the left side (start) of the track as a snap point
            final long startTime = tc.getOffset();

            if (startTime > 0) {
                snapList.add(startTime);
            }

            // add all of the bookmarks as snap points
            for (Long bookmark : tc.getBookmarks()) {
                final long time = startTime + bookmark;

                if (time > 0) {
                    snapList.add(time);
                }
            }

            // add the right side (end) of the track as a snap point
            final long duration = tc.getDuration();
            final long endTime = startTime + duration;

            if (endTime > 0) {
                snapList.add(endTime);
            }

            if (duration > longestDuration) {
                longestDuration = duration;
            }
        }

        // If there are no snap candidates just exit immediately.
        if (snapCandidates.isEmpty()) {
            return null;
        }

        final long snappingThreshold = TrackController
            .calculateSnappingThreshold(viewport);

        // Remove duplicate candidate snap points
        for (int i = snapCandidates.size() - 1; i > 0; i--) {

            if (snapCandidates.get(i).equals(snapCandidates.get(i - 1))) {
                snapCandidates.remove(i);
            }
        }

        // Sort the candidate snap points
        Collections.sort(snapCandidates);

        // Search for a snap position nearest to temporalSnapPosition
        int nearestIndex = Collections.binarySearch(snapPoints,
                temporalSnapPosition);

        if (nearestIndex < 0) {
            nearestIndex = -(nearestIndex + 1);
        }

        if (nearestIndex >= snapPoints.size()) {
            nearestIndex = snapPoints.size() - 1;
        }

        if ((nearestIndex >= 0) && (nearestIndex < snapPoints.size())) {
            final long rightSnapTime = snapPoints.get(nearestIndex);
            long leftSnapTime = rightSnapTime;

            if (nearestIndex > 0) {
                leftSnapTime = snapPoints.get(nearestIndex - 1);
            }

            // Add the closest snap point as first search position
            if (Math.abs(rightSnapTime - temporalSnapPosition)
                    < Math.abs(temporalSnapPosition - leftSnapTime)) {
                snapPoints.add(0, rightSnapTime);
            } else {
                snapPoints.add(0, leftSnapTime);
            }
        }

        // Search for snap position
        for (Long snapPoint : snapPoints) {
            int candidateIndex = Collections.binarySearch(snapCandidates,
                    snapPoint);

            if (candidateIndex < 0) {
                candidateIndex = -(candidateIndex + 1);
            }

            if (candidateIndex >= snapCandidates.size()) {
                candidateIndex = snapCandidates.size() - 1;
            }

            final long upperSnapTime = snapCandidates.get(candidateIndex);
            long lowerSnapTime = upperSnapTime;

            if (candidateIndex > 0) {
                lowerSnapTime = snapCandidates.get(candidateIndex - 1);
            }

            if ((lowerSnapTime < snapPoint)
                    && (Math.abs(snapPoint - lowerSnapTime)
                        < snappingThreshold)) {
                final SnapPoint sp = new SnapPoint();
                sp.snapOffset = lowerSnapTime - snapPoint;
                sp.snapMarkerPosition = lowerSnapTime;

                return sp;
            }

            // Check if the candidate snap points can be used
            if (Math.abs(upperSnapTime - snapPoint) < snappingThreshold) {
                final SnapPoint sp = new SnapPoint();
                sp.snapOffset = upperSnapTime - snapPoint;
                sp.snapMarkerPosition = upperSnapTime;

                return sp;
            }
        }

        return null;
    }

    /**
     * Adds the current temporal position as a bookmark to all selected tracks.
     *
     * @param position
     *            temporal position in milliseconds
     */
    public void addTemporalBookmarkToSelected(final long position) {

        for (TrackController tc : tracks.values()) {

            if (tc.isSelected()) {
                tc.addTemporalBookmark(position);
                tc.saveBookmark();
            }
        }
    }

    /**
     * @return True if at least one track is selected, false otherwise.
     */
    public boolean hasSelectedTracks() {

        for (TrackController tc : tracks.values()) {

            if (tc.isSelected()) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return number of tracks being managed by this controller
     */
    public int numberOfTracks() {
        return tracks.size();
    }

    /**
     * Sets the carriage movement locking state.
     *
     * @param lockState
     *            true if carriages are not allowed to move, false otherwise.
     */
    public void setLockedState(final boolean lockState) {

        for (TrackController tc : tracks.values()) {
            tc.setLocked(lockState);
        }
    }

    /**
     * Handles a mouse released event on a track.
     *
     * @param e The event to handle.
     */
    public void mouseReleased(final MouseEvent e) {
        snapMarkerController.setMarkerTime(-1);
    }

    /**
     * Set the bookmark for the given track.
     *
     * @param mediaPath
     *            Track identifier.
     * @param position
     *            Positions of the bookmarks in milliseconds.
     */
    public void setBookmarkPositions(final Identifier trackId,
        final List<Long> positions) {
        TrackController tc = tracks.get(trackId);

        if (tc != null) {
            tc.addBookmarks(positions);
        }
    }

    /**
     * Set the bookmark for the given track. For backwards compatibility only.
     *
     * @param mediaPath
     *            Absolute path to the media file represented by the
     *            track.
     * @param position
     *            Positions of the bookmarks in milliseconds.
     */
    @Deprecated public void setBookmarkPositions(final String mediaPath,
        final List<Long> positions) {

        for (TrackController tc : tracks.values()) {

            if (tc.getTrackModel().getMediaPath().equals(mediaPath)) {
                tc.addBookmarks(positions);

                return;
            }
        }
    }

    /**
     * Set the movement lock state for a given track.
     *
     * @param mediaPath
     *            Absolute path to the media file represented by the
     *            track.
     * @param lock
     *            true if the track's movement is locked, false otherwise.
     */
    public void setMovementLock(final Identifier trackId, final boolean lock) {
        TrackController tc = tracks.get(trackId);

        if (tc != null) {
            tc.setLocked(lock);
        }
    }

    /**
     * Set the movement lock state for a given track. For backwards
     * compatibility only.
     *
     * @param mediaPath
     *            Absolute path to the media file represented by the
     *            track.
     * @param lock
     *            true if the track's movement is locked, false otherwise.
     */
    @Deprecated public void setMovementLock(final String mediaPath,
        final boolean lock) {

        for (TrackController tc : tracks.values()) {

            if (tc.getTrackModel().getMediaPath().equals(mediaPath)) {
                tc.setLocked(lock);

                return;
            }
        }
    }


    /**
     * Get the track model for a given identifier.
     *
     * @param trackId
     *            identifier used to search for the track
     * @return null if there is no such track, the associated TrackModel
     *         otherwise.
     */
    public TrackModel getTrackModel(final Identifier trackId) {
        TrackController tc = tracks.get(trackId);

        return (tc != null) ? tc.getTrackModel() : null;
    }

    /**
     * @return A clone of all track models currently in uses.
     */
    public Iterable<TrackModel> getAllTrackModels() {
        List<TrackModel> models = Lists.newArrayList();

        for (TrackController tc : tracks.values()) {
            models.add(tc.getTrackModel());
        }

        return models;
    }

    public boolean isAnyTrackUnlocked() {

        for (TrackController tc : tracks.values()) {

            if (!tc.getTrackModel().isLocked()) {
                return true;
            }
        }

        return tracks.isEmpty();
    }

    /**
     * Used for tests through reflection.
     *
     * @return All track controllers.
     */
    @SuppressWarnings("unused")
    private List<TrackController> getAllTrackControllers() {
        return Lists.newArrayList(tracks.values());
    }

    /**
     * Deselect all tracks except for the given track.
     * @param selected
     */
    private void deselectExcept(final TrackController selected) {

        for (TrackController tc : tracks.values()) {

            if (tc != selected) {
                tc.deselect();
            }
        }
    }

    /**
     * Inner class for handling carriage selection.
     */
    private class CarriageSelection extends CarriageEventAdapter {


        @Override public void selectionChanged(final CarriageEvent e) {

            if (!e.hasModifiers()) {
                deselectExcept((TrackController) e.getSource());
            }
        }

    }

    /**
     * Inner class for packaging snap information.
     */
    private static class SnapPoint {

        /** The new snap offset position in milliseconds. */
        public long snapOffset;

        /** The snap marker position to paint. */
        public long snapMarkerPosition;

        public String toString() {
            return "[SnapPoint snapOffset=" + snapOffset
                + ", snapMarkerPosition=" + snapMarkerPosition + "]";
        }
    }

}
