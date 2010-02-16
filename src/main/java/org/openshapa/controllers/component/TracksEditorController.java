package org.openshapa.controllers.component;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.openshapa.event.CarriageEventListener;
import org.openshapa.models.component.ViewableModel;

/**
 * Tracks editor controller is responsible for managing multiple TrackController
 * instances.
 * 
 * @author dteoh
 */
public class TracksEditorController {

    private JPanel editingPanel;

    private List<Track> tracks;

    private ViewableModel viewableModel;

    public TracksEditorController() {
        tracks = new LinkedList<Track>();
        viewableModel = new ViewableModel();
        initView();
    }

    private void initView() {
        editingPanel = new JPanel();
        editingPanel.setLayout(new MigLayout("wrap, ins 0", "[685]", "[70]"));
    }

    public JComponent getView() {
        return editingPanel;
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
                allTracks.remove();
                editingPanel.validate();
                return true;
            }
        }
        return false;
    }

    public void removeAllTracks() {
        tracks.clear();
        editingPanel.removeAll();
    }

    public boolean setTrackOffset(String mediaPath, long newOffset) {
        Iterator<Track> allTracks = tracks.iterator();
        while (allTracks.hasNext()) {
            Track track = allTracks.next();
            if (track.mediaPath.equals(mediaPath)) {
                track.trackController.setTrackOffset(newOffset);
                return true;
            }
        }
        return false;
    }

    public void addTemporalBookmarkToSelected(final long position) {
        Iterator<Track> allTracks = tracks.iterator();
        while (allTracks.hasNext()) {
            TrackController track = allTracks.next().trackController;
            if (track.isSelected()) {
                track.addTemporalBookmark(position);
            }
        }
    }

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

    private class Track {
        public String mediaPath;
        public TrackController trackController;
    }

}
