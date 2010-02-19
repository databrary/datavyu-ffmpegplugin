package org.openshapa.controllers.component;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.openshapa.event.CarriageEventListener;
import org.openshapa.event.TrackMouseEventListener;
import org.openshapa.models.component.ViewableModel;

/**
 * Tracks editor controller is responsible for managing multiple TrackController
 * instances.
 * 
 * @author dteoh
 */
public class TracksEditorController implements TrackMouseEventListener {

    private JPanel editingPanel;

    private List<Track> tracks;

    private ViewableModel viewableModel;

    private boolean allowSnap;

    private final SnapMarkerController snapMarkerController;

    public TracksEditorController() {
        tracks = new LinkedList<Track>();
        viewableModel = new ViewableModel();
        snapMarkerController = new SnapMarkerController();
        allowSnap = false;
        initView();
    }

    private void initView() {
        editingPanel = new JPanel();
        editingPanel.setLayout(new MigLayout("wrap, ins 0", "[685]", "[70]"));
    }

    public JComponent getView() {
        return editingPanel;
    }

    public JComponent getMarkerView() {
        return snapMarkerController.getView();
    }

    public void setViewableModel(ViewableModel viewableModel) {
        /*
         * Just copy the values, do not spread references all over the place to
         * avoid model tainting.
         */
        this.viewableModel.setEnd(viewableModel.getEnd());
        this.viewableModel.setIntervalTime(viewableModel.getIntervalTime());
        this.viewableModel.setIntervalWidth(viewableModel.getIntervalWidth());
        this.viewableModel.setZoomWindowEnd(viewableModel.getZoomWindowEnd());
        this.viewableModel.setZoomWindowStart(viewableModel
                .getZoomWindowStart());

        for (Track track : tracks) {
            track.trackController.setViewableModel(viewableModel);
        }

        snapMarkerController.setViewableModel(viewableModel);
    }

    public void addNewTrack(String mediaPath, String trackName, long duration,
            long offset, CarriageEventListener listener) {
        // TrackController
        TrackController trackController = new TrackController();
        trackController.setViewableModel(viewableModel);
        trackController.setTrackInformation(trackName, mediaPath, duration,
                offset);
        if (duration < 0) {
            trackController.setErroneous(true);
        }

        if (listener != null) {
            trackController.addCarriageEventListener(listener);
        }

        trackController.addTrackMouseEventListener(this);

        Track track = new Track();
        track.mediaPath = mediaPath;
        track.trackController = trackController;

        tracks.add(track);

        editingPanel.add(trackController.getView());
        editingPanel.validate();
    }

    public boolean removeTrack(String mediaPath, CarriageEventListener listener) {
        Iterator<Track> allTracks = tracks.iterator();
        while (allTracks.hasNext()) {
            Track track = allTracks.next();
            if (track.mediaPath.equals(mediaPath)) {
                editingPanel.remove(track.trackController.getView());
                track.trackController.removeCarriageEventListener(listener);
                track.trackController.removeTrackMouseEventListener(this);
                allTracks.remove();
                editingPanel.validate();
                return true;
            }
        }
        return false;
    }

    public void removeAllTracks() {
        for (Track track : tracks) {
            track.trackController.removeTrackMouseEventListener(this);
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
    public boolean setTrackOffset(final String mediaPath, final long newOffset,
            final long snapTemporalPosition) {
        Iterator<Track> allTracks = tracks.iterator();
        while (allTracks.hasNext()) {
            Track track = allTracks.next();
            if (track.mediaPath.equals(mediaPath)) {
                TrackController tc = track.trackController;
                if (allowSnap) {
                    SnapPoint snapPoint =
                            snapOffset(mediaPath, snapTemporalPosition);
                    tc.setMoveable(snapPoint == null);
                    if (snapPoint == null) {
                        tc.setNormalState();
                        tc.setTrackOffset(newOffset);
                        snapMarkerController.setMarkerTime(-1);
                    } else {
                        snapMarkerController
                                .setMarkerTime(snapPoint.snapMarkerPosition);
                        tc.setTrackOffset(snapPoint.snapOffset + newOffset);
                    }
                } else {
                    tc.setTrackOffset(newOffset);
                    tc.setMoveable(true);
                    snapMarkerController.setMarkerTime(-1);
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
     * <li>If no snap points are found, then return 0</li>
     * </ol>
     * 
     * @param mediaPath
     * @param temporalSnapPosition
     * @return
     */
    private SnapPoint snapOffset(final String mediaPath,
            final long temporalSnapPosition) {
        List<Long> snapCandidates = new ArrayList<Long>();
        List<Long> snapPoints = new LinkedList<Long>();
        Iterator<Track> allTracks = tracks.iterator();

        long longestDuration = 0;

        // Compile track and candidate snap points
        while (allTracks.hasNext()) {
            Track track = allTracks.next();
            TrackController trackController = track.trackController;
            long offset = trackController.getOffset();
            long bookmark = trackController.getBookmark();
            long duration = trackController.getDuration();

            if (track.mediaPath.equals(mediaPath)) {
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

        // Calculate the snap threshold as a % of the longest track duration
        final long threshold = (long) (0.02F * longestDuration);

        // Sort the candidate snap points
        Collections.sort(snapCandidates);

        // Search for a snap position nearest to temporalSnapPosition
        int nearestIndex =
                Math.abs(Collections.binarySearch(snapPoints,
                        temporalSnapPosition));
        if (nearestIndex >= snapPoints.size()) {
            nearestIndex = snapPoints.size() - 1;
        }
        long rightSnapTime = snapPoints.get(nearestIndex);
        long leftSnapTime = rightSnapTime;
        if (nearestIndex > 0) {
            leftSnapTime = snapPoints.get(nearestIndex - 1);
        }

        // Add the closest snap point as first search position
        if (Math.abs(rightSnapTime - temporalSnapPosition) < Math
                .abs(temporalSnapPosition - leftSnapTime)) {
            snapPoints.add(0, rightSnapTime);
        } else {
            snapPoints.add(0, leftSnapTime);
        }

        // Search for snap position
        for (Long snapPoint : snapPoints) {
            int candidateIndex =
                    Collections.binarySearch(snapCandidates, snapPoint);
            candidateIndex = Math.abs(candidateIndex);
            if (candidateIndex >= snapCandidates.size()) {
                candidateIndex = snapCandidates.size() - 1;
            }

            long upperSnapTime = snapCandidates.get(candidateIndex);
            long lowerSnapTime = upperSnapTime;
            if (candidateIndex > 0) {
                lowerSnapTime = snapCandidates.get(candidateIndex - 1);
            }

            // Check if the candidate snap points can be used
            if (Math.abs(snapPoint - lowerSnapTime) <= threshold) {
                SnapPoint sp = new SnapPoint();
                sp.snapOffset = lowerSnapTime - snapPoint;
                sp.snapMarkerPosition = lowerSnapTime;
                return sp;
            }
            if (Math.abs(upperSnapTime - snapPoint) <= threshold) {
                SnapPoint sp = new SnapPoint();
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
        Iterator<Track> allTracks = tracks.iterator();
        while (allTracks.hasNext()) {
            TrackController track = allTracks.next().trackController;
            if (track.isSelected()) {
                track.addTemporalBookmark(position);
            }
        }
    }

    /**
     * @return True if at least one track is selected, false otherwise.s
     */
    public boolean hasSelectedTracks() {
        Iterator<Track> allTracks = tracks.iterator();
        while (allTracks.hasNext()) {
            TrackController track = allTracks.next().trackController;
            if (track.isSelected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param allowSnap
     */
    public void setAllowSnap(boolean allowSnap) {
        this.allowSnap = allowSnap;
    }

    // Handles a mouse released event on a track.
    public void mouseReleased(MouseEvent e) {
        snapMarkerController.setMarkerTime(-1);
    }

    private static class Track {
        public String mediaPath;
        public TrackController trackController;
    }

    private static class SnapPoint {
        public long snapOffset;
        public long snapMarkerPosition;
    }

}
