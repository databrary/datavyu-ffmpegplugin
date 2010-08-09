package org.openshapa.controllers.component;

import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openshapa.event.component.CarriageEvent;
import org.openshapa.event.component.CarriageEventAdapter;
import org.openshapa.event.component.CarriageEventListener;
import org.openshapa.event.component.TrackMouseEventListener;

import org.openshapa.models.component.MixerView;
import org.openshapa.models.component.RegionConstants;
import org.openshapa.models.component.TrackModel;
import org.openshapa.models.component.Viewport;
import org.openshapa.models.id.Identifier;

import org.openshapa.views.component.TrackPainter;
import org.openshapa.views.component.TracksEditorPainter;
import org.openshapa.views.continuous.CustomActionListener;


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
    private final List<Track> tracks;

    private final MixerView mixer;

    /** Handles the selection model for tracks. */
    private final CarriageSelection selectionHandler;

    /**
     * Create a new tracks editor controller.
     */
    public TracksEditorController(final MixerView mixer) {
        tracks = new LinkedList<Track>();
        this.mixer = mixer;
        snapMarkerController = new SnapMarkerController(mixer);
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
        final TrackController trackController = new TrackController(mixer,
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

        final Track track = new Track();
        track.trackId = trackId;
        track.trackController = trackController;

        tracks.add(track);

        editingPanel.add(trackController.getView(),
            "pad 0 0 0 " + -RegionConstants.RMARKER_WIDTH + ", growx");
        editingPanel.invalidate();
    }

    /**
     * Bind track actions to a data viewer.
     *
     * @param mediaPath Absolute path to the media file
     * @param dataViewer Viewer to bind
     * @param actionSupported1 is the first custom action supported
     * @param actionIcon1 icon associated with the first custom action
     * @param actionSupported2 is the second custom action supported
     * @param actionIcon2 icon associated with the second custom action
     * @param actionSupported3 is the third custom action supported
     * @param actionIcon3 icon associated with the third custom action
     */
    public void bindTrackActions(final Identifier trackId,
        final CustomActionListener dataViewer, final boolean actionSupported1,
        final ImageIcon actionIcon1, final boolean actionSupported2,
        final ImageIcon actionIcon2, final boolean actionSupported3,
        final ImageIcon actionIcon3) {

        for (Track track : tracks) {

            if (track.trackId.equals(trackId)) {
                TrackController tc = track.trackController;
                tc.addCustomActionListener(dataViewer);
                tc.setActionButtonUI1(actionSupported1, actionIcon1);
                tc.setActionButtonUI2(actionSupported2, actionIcon2);
                tc.setActionButtonUI3(actionSupported3, actionIcon3);
            }
        }
    }

    /**
     * Unbind track actions from a dataviewer.
     *
     * @param trackId
     *            Track identifier
     * @param dataViewer
     *            Viewer to unbind
     */
    public void unbindTrackActions(final Identifier trackId,
        final CustomActionListener dataViewer) {

        for (Track track : tracks) {

            if (track.trackId.equals(trackId)) {
                TrackController tc = track.trackController;
                tc.removeCustomActionListener(dataViewer);
            }
        }
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
        final Iterator<Track> allTracks = tracks.iterator();

        while (allTracks.hasNext()) {
            final Track track = allTracks.next();

            if (track.trackId.equals(trackId)) {
                editingPanel.remove(track.trackController.getView());
                track.trackController.removeCarriageEventListener(listener);
                track.trackController.removeCarriageEventListener(
                    selectionHandler);
                track.trackController.removeTrackMouseEventListener(this);
                allTracks.remove();
                editingPanel.validate();

                return true;
            }
        }

        return false;
    }

    /**
     * Remove all tracks from the controller.
     */
    public void removeAllTracks() {

        for (Track track : tracks) {
            track.trackController.removeTrackMouseEventListener(this);
            track.trackController.removeCarriageEventListener(selectionHandler);
        }

        tracks.clear();
        editingPanel.removeAll();
    }

    /**
     * Sets the track offset for the given media path if it exists. If offset
     * snapping is enabled through {@link #setAllowSnap(boolean)}, then this
     * function will attempt to synchronize the track position with every other
     * track's position of interest. A position of interest includes the start
     * of a track, bookmarked positions, and end of a track.
     *
     * @param mediaPath
     *            Identifies a track through its path on the file system
     * @param newOffset
     *            New track offset position
     * @param snapTemporalPosition
     *            If snapping is enabled, the closest position of interest to
     *            snapTemporalPosition will be used as the first candidate for
     *            synchonization.
     * @return true if the offset was set, false otherwise.
     */
    public boolean setTrackOffset(final Identifier trackId,
        final long newOffset, final long snapTemporalPosition) {
        final Iterator<Track> allTracks = tracks.iterator();

        while (allTracks.hasNext()) {
            final Track track = allTracks.next();

            if (track.trackId.equals(trackId)) {
                final TrackController tc = track.trackController;
                tc.setTrackOffset(newOffset);
                snapMarkerController.setMarkerTime(-1);

                final SnapPoint snapPoint = snapOffset(trackId,
                        snapTemporalPosition);
                tc.setMoveable(snapPoint == null);

                if (snapPoint == null) {
                    snapMarkerController.setMarkerTime(-1);
                } else {
                    snapMarkerController.setMarkerTime(
                        snapPoint.snapMarkerPosition);
                    tc.setTrackOffset(newOffset + snapPoint.snapOffset);
                }

                return true;
            }
        }

        return false;
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
        final List<Long> snapCandidates = new ArrayList<Long>();
        final List<Long> snapPoints = new LinkedList<Long>();
        final Iterator<Track> allTracks = tracks.iterator();

        long longestDuration = 0;

        // Compile track and candidate snap points
        while (allTracks.hasNext()) {
            final Track track = allTracks.next();
            final TrackController trackController = track.trackController;
            final long offset = trackController.getOffset();
            final long bookmark = trackController.getBookmark();
            final long duration = trackController.getDuration();

            if (track.trackId.equals(trackId)) {
                snapPoints.add(offset);

                if (bookmark > 0) {
                    snapPoints.add(offset + bookmark);
                }

                snapPoints.add(offset + duration);
            } else {
                snapCandidates.add(offset);

                if (bookmark > 0) {
                    snapCandidates.add(offset + bookmark);
                }

                snapCandidates.add(offset + duration);
            }

            if (duration > longestDuration) {
                longestDuration = duration;
            }
        }

        // If there are no snap candidates just exit immediately.
        if (snapCandidates.isEmpty()) {
            return null;
        }

        Viewport viewport = mixer.getViewport();

        // Calculate the snap threshold as a % of the longest track duration
        final long threshold = (long) (0.01F * viewport.getViewDuration());

        // Sort the candidate snap points
        Collections.sort(snapCandidates);

        // Search for a snap position nearest to temporalSnapPosition
        int nearestIndex = Math.abs(Collections.binarySearch(snapPoints,
                    temporalSnapPosition));

        if (nearestIndex >= snapPoints.size()) {
            nearestIndex = snapPoints.size() - 1;
        }

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

        // Search for snap position
        for (Long snapPoint : snapPoints) {
            int candidateIndex = Collections.binarySearch(snapCandidates,
                    snapPoint);
            candidateIndex = Math.abs(candidateIndex);

            if (candidateIndex >= snapCandidates.size()) {
                candidateIndex = snapCandidates.size() - 1;
            }

            final long upperSnapTime = snapCandidates.get(candidateIndex);
            long lowerSnapTime = upperSnapTime;

            if (candidateIndex > 0) {
                lowerSnapTime = snapCandidates.get(candidateIndex - 1);
            }

            if ((lowerSnapTime < snapPoint)
                    && (Math.abs(snapPoint - lowerSnapTime) <= threshold)) {
                final SnapPoint sp = new SnapPoint();
                sp.snapOffset = lowerSnapTime - snapPoint;
                sp.snapMarkerPosition = lowerSnapTime;

                return sp;
            }

            // Check if the candidate snap points can be used
            if (Math.abs(upperSnapTime - snapPoint) <= threshold) {
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
        final Iterator<Track> allTracks = tracks.iterator();

        while (allTracks.hasNext()) {
            final TrackController track = allTracks.next().trackController;

            if (track.isSelected()) {
                track.addTemporalBookmark(position);
                track.saveBookmark();
            }
        }
    }

    /**
     * @return True if at least one track is selected, false otherwise.s
     */
    public boolean hasSelectedTracks() {
        final Iterator<Track> allTracks = tracks.iterator();

        while (allTracks.hasNext()) {
            final TrackController track = allTracks.next().trackController;

            if (track.isSelected()) {
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

        for (Track track : tracks) {
            track.trackController.setLocked(lockState);
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
     *            Position of the bookmark in milliseconds.
     */
    public void setBookmarkPosition(final Identifier trackId,
        final long position) {

        for (Track track : tracks) {

            if (track.trackId.equals(trackId)) {
                track.trackController.addBookmark(position);
            }
        }
    }

    /**
     * Set the bookmark for the given track. For backwards compatibility only.
     *
     * @param mediaPath
     *            Absolute path to the media file represented by the
     *            track.
     * @param position
     *            Position of the bookmark in milliseconds.
     */
    @Deprecated public void setBookmarkPosition(final String mediaPath,
        final long position) {

        for (Track track : tracks) {

            if (track.trackController.getTrackModel().getMediaPath().equals(
                        mediaPath)) {
                track.trackController.addBookmark(position);

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

        for (Track track : tracks) {

            if (track.trackId.equals(trackId)) {
                track.trackController.setLocked(lock);
            }
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

        for (Track track : tracks) {

            if (track.trackController.getTrackModel().getMediaPath().equals(
                        mediaPath)) {
                track.trackController.setLocked(lock);

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

        for (Track track : tracks) {

            if (track.trackId.equals(trackId)) {
                assert track.trackController.getTrackModel().getId().equals(
                        trackId);

                return track.trackController.getTrackModel();
            }
        }

        return null;
    }

    /**
     * @return A clone of all track models currently in uses.
     */
    public Iterable<TrackModel> getAllTrackModels() {
        final List<TrackModel> models = new LinkedList<TrackModel>();

        for (Track track : tracks) {
            models.add(track.trackController.getTrackModel());
        }

        return models;
    }

    /**
     * @return All track controllers.
     */
    private List<TrackController> getAllTrackControllers() {
        final List<TrackController> controllers =
            new ArrayList<TrackController>();

        for (Track track : tracks) {
            controllers.add(track.trackController);
        }

        return controllers;
    }

    /**
     * Deselect all tracks except for the given track.
     * @param selected
     */
    private void deselectExcept(final TrackController selected) {

        for (Track track : tracks) {

            if (track.trackController != selected) {
                track.trackController.deselect();
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
     * Inner class for associating track identifier to a
     * track controller.
     */
    private static class Track {

        /** Track identifier. */
        public Identifier trackId;

        /** The controller associated with the track. */
        public TrackController trackController;
    }

    /**
     * Inner class for packaging snap information.
     */
    private static class SnapPoint {

        /** The new snap offset position in milliseconds. */
        public long snapOffset;

        /** The snap marker position to paint. */
        public long snapMarkerPosition;
    }

}
