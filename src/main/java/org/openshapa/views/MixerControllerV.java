package org.openshapa.views;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.EventListenerList;

import net.miginfocom.swing.MigLayout;

import org.openshapa.controllers.component.NeedleController;
import org.openshapa.controllers.component.RegionController;
import org.openshapa.controllers.component.TimescaleController;
import org.openshapa.controllers.component.TrackController;
import org.openshapa.controllers.component.TracksEditorController;
import org.openshapa.event.CarriageEvent;
import org.openshapa.event.CarriageEventListener;
import org.openshapa.event.MarkerEvent;
import org.openshapa.event.MarkerEventListener;
import org.openshapa.event.NeedleEvent;
import org.openshapa.event.NeedleEventListener;
import org.openshapa.event.TracksControllerEvent;
import org.openshapa.event.TracksControllerListener;
import org.openshapa.event.TracksControllerEvent.TracksEvent;
import org.openshapa.models.component.ViewableModel;

/**
 * This class manages the tracks information interface
 */
public class MixerControllerV implements NeedleEventListener,
        MarkerEventListener, CarriageEventListener {

    /** Root interface panel */
    private JPanel tracksPanel;
    /** Scroll pane that holds track information */
    private JScrollPane tracksScrollPane;
    /** This layered pane holds the needle painter */
    private JLayeredPane layeredPane;
    /**
     * Zoomed into the display by how much. Values should only be 1, 2, 4, 8,
     * 16, 32
     */
    private int zoomSetting = 1;
    /**
     * The value of the longest video's time length in milliseconds
     */
    private long maxEnd;
    /**
     * The value of the earliest video's start time in milliseconds
     */
    private long minStart;
    /** Listeners interested in tracks controller events */
    private EventListenerList listenerList;
    /** Controller responsible for managing the time scale */
    private TimescaleController timescaleController;
    /** Controller responsible for managing the timing needle */
    private NeedleController needleController;
    /** Controller responsible for managing a selected region */
    private RegionController regionController;
    /** */
    private TracksEditorController tracksEditorController;

    private JButton bookmarkButton;

    private JScrollBar tracksScrollBar;

    public MixerControllerV() {
        // Set default scale values
        maxEnd = 60000;
        minStart = 0;

        listenerList = new EventListenerList();

        // Not using MigLayout with JLayeredPane because of layout issues
        layeredPane = new JLayeredPane();

        // Set up the root panel
        tracksPanel = new JPanel();
        tracksPanel.setLayout(new MigLayout("ins 0",
                "[left|left|left]unrel push[right|right]", ""));
        tracksPanel.setBackground(Color.WHITE);

        // Menu buttons
        JButton lockButton = new JButton("Lock");

        bookmarkButton = new JButton("Add Bookmark");
        bookmarkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addBookmarkHandler();
            }
        });
        bookmarkButton.setEnabled(false);

        JToggleButton snapToggleButton = new JToggleButton("Snap Off");
        snapToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                snapToggleHandler(e);
            }
        });

        JButton zoomInButton = new JButton("( + )");
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomInScale(e);
                zoomTracks(e);
            }
        });

        JButton zoomOutButton = new JButton("( - )");
        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomOutScale(e);
                zoomTracks(e);
            }
        });

        tracksPanel.add(lockButton);
        tracksPanel.add(bookmarkButton);
        tracksPanel.add(snapToggleButton);
        tracksPanel.add(zoomInButton);
        tracksPanel.add(zoomOutButton, "wrap");

        // Add the timescale
        timescaleController = new TimescaleController();
        Component timescaleView = timescaleController.getView();
        {
            Dimension size = new Dimension();
            size.setSize(785, 35);
            timescaleView.setSize(size);
            timescaleView.setPreferredSize(size);
            timescaleController.setConstraints(minStart, maxEnd,
                    zoomIntervals(1));
        }
        layeredPane.add(timescaleView, new Integer(10));

        // Add the scroll pane
        tracksEditorController = new TracksEditorController();
        tracksEditorController.setViewableModel(timescaleController
                .getViewableModel());

        tracksScrollPane = new JScrollPane(tracksEditorController.getView());
        tracksScrollPane
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        // tracksScrollPane
        // .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        tracksScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Set an explicit size of the scroll pane
        {
            Dimension size = new Dimension();
            size.setSize(785, 195);
            tracksScrollPane.setSize(size);
            tracksScrollPane.setPreferredSize(size);
            tracksScrollPane.setLocation(0, 39);
        }
        layeredPane.add(tracksScrollPane, new Integer(0));

        // Create the region markers
        regionController = new RegionController();
        Component regionView = regionController.getView();
        {
            Dimension size = new Dimension();
            size.setSize(785, 234);
            regionView.setSize(size);
            regionView.setPreferredSize(size);

            regionController.setViewableModel(timescaleController
                    .getViewableModel());
            regionController.setPlaybackRegion(minStart, maxEnd);
        }
        regionController.addMarkerEventListener(this);

        layeredPane.add(regionView, new Integer(20));

        // Create the timing needle
        needleController = new NeedleController();
        Component needleView = needleController.getView();
        {
            Dimension size = new Dimension();
            size.setSize(785, 234); // 765
            needleView.setSize(size);
            needleView.setPreferredSize(size);
            // Values determined through trial-and-error.
            needleController.setViewableModel(timescaleController
                    .getViewableModel());
        }
        needleController.addNeedleEventListener(this);
        layeredPane.add(needleView, new Integer(30));

        // Create the snap marker
        JComponent markerView = tracksEditorController.getMarkerView();
        {
            Dimension size = new Dimension();
            size.setSize(785, 234);
            markerView.setSize(size);
            markerView.setPreferredSize(size);
        }
        layeredPane.add(markerView, new Integer(5));

        tracksScrollBar = new JScrollBar(Adjustable.HORIZONTAL);
        {
            Dimension size = new Dimension();
            size.setSize(700, 17);
            tracksScrollBar.setSize(size);
            tracksScrollBar.setPreferredSize(size);
            tracksScrollBar.setLocation(85, 234);
        }
        tracksScrollBar.setValues(0, 100000, 0, 100000);

        layeredPane.add(tracksScrollBar, new Integer(100));

        tracksPanel.add(layeredPane, "span 5, w 785!, h 250!, wrap");

        tracksPanel.validate();
    }

    /**
     * @return the panel containing the tracks interface.
     */
    public JPanel getTracksPanel() {
        return tracksPanel;
    }

    /**
     * @return the longest data feed duration in milliseconds
     */
    public long getMaxEnd() {
        return maxEnd;
    }

    /**
     * Sets the longest data feed duration.
     * 
     * @param maxEnd
     *            duration in milliseconds
     */
    public void setMaxEnd(long maxEnd) {
        this.maxEnd = maxEnd;
        ViewableModel model = timescaleController.getViewableModel();
        model.setEnd(maxEnd);
        timescaleController.setViewableModel(model);
        regionController.setViewableModel(model);
        needleController.setViewableModel(model);
        tracksEditorController.setViewableModel(model);
    }

    /**
     * Add a new track to the interface.
     * 
     * @param trackName
     *            name of the track
     * @param duration
     *            the total duration of the track in milliseconds
     * @param offset
     *            the amount of playback offset in milliseconds
     */
    public void addNewTrack(String mediaPath, String trackName, long duration,
            long offset) {
        // Check if the scale needs to be updated.
        if (duration + offset > maxEnd) {
            maxEnd = duration + offset;
            ViewableModel model = timescaleController.getViewableModel();
            model.setEnd(maxEnd);
            timescaleController.setViewableModel(model);
            regionController.setViewableModel(model);
            needleController.setViewableModel(model);
            tracksEditorController.setViewableModel(model);
            rescale();
        }

        tracksEditorController.addNewTrack(mediaPath, trackName, duration,
                offset, this);

        tracksScrollPane.validate();
    }

    /**
     * @param time
     *            Set the current time in milliseconds to use.
     */
    public void setCurrentTime(long time) {
        needleController.setCurrentTime(time);
    }

    /**
     * @return Current time, in milliseconds, that is being used.
     */
    public long getCurrentTime() {
        return needleController.getCurrentTime();
    }

    /**
     * Set the start of the new playback region
     * 
     * @param time
     *            time in milliseconds
     */
    public void setPlayRegionStart(long time) {
        regionController.setPlaybackRegionStart(time);
    }

    /**
     * Set the end of the new playback region
     * 
     * @param time
     *            time in milliseconds
     */
    public void setPlayRegionEnd(long time) {
        regionController.setPlaybackRegionEnd(time);
    }

    /**
     * Zooms into the displayed scale and re-adjusts the timing needle
     * accordingly.
     * 
     * @param evt
     */
    public void zoomInScale(ActionEvent evt) {
        zoomSetting = zoomSetting * 2;
        if (zoomSetting > 32) {
            zoomSetting = 32;
        }

        rescale();
    }

    /**
     * Zooms out of the displayed scale and re-adjusts the timing needle
     * accordingly.
     * 
     * @param evt
     */
    public void zoomOutScale(ActionEvent evt) {
        zoomSetting = zoomSetting / 2;
        if (zoomSetting < 1) {
            zoomSetting = 1;
        }

        rescale();
    }

    /**
     * Update the track display after a zoom.
     * 
     * @param evt
     */
    public void zoomTracks(ActionEvent evt) {
        ViewableModel model = timescaleController.getViewableModel();
        tracksEditorController.setViewableModel(model);
    }

    /**
     * Remove a track from our tracks panel.
     * 
     * @param mediaPath
     *            the path to the media file
     */
    public void removeTrack(final String mediaPath) {
        tracksEditorController.removeTrack(mediaPath, this);

        // If there are no more tracks, reset.
        if (maxEnd == 0) {
            maxEnd = 60000;
            zoomSetting = 1;
        }
        // Update zoom window scale
        rescale();
        // Update zoomed tracks
        zoomTracks(null);
        // Update tracks panel display
        tracksScrollPane.validate();
    }

    /**
     * Removes all track components from this controller and resets components.
     */
    public void removeAll() {
        tracksEditorController.removeAllTracks();

        maxEnd = 60000;
        zoomSetting = 1;
        rescale();
        zoomTracks(null);

        ViewableModel model = timescaleController.getViewableModel();
        model.setZoomWindowStart(0);
        model.setZoomWindowEnd(60000);

        regionController.setViewableModel(model);
        regionController.setPlaybackRegion(0, 60000);
        needleController.setCurrentTime(0);
        needleController.setViewableModel(model);
        timescaleController.setViewableModel(model);
        tracksEditorController.setViewableModel(model);

        tracksScrollPane.validate();

        tracksPanel.validate();
        tracksPanel.repaint();
    }

    /**
     * @param zoomValue
     *            supports 1x, 2x, 4x, 8x, 16x, 32x
     * @return the amount of intervals to show given a zoom value
     */
    private int zoomIntervals(final int zoomValue) {
        assert (zoomValue >= 1);
        assert (zoomValue <= 32);
        if (zoomValue <= 2) {
            return 20;
        }
        if (zoomValue <= 8) {
            return 10;
        }
        if (zoomValue <= 32) {
            return 5;
        }
        // Default amount of zoom intervals
        return 20;
    }

    /**
     * Recalculates timing scale and needle constraints based on the minimum
     * track start time, longest track time, and current zoom setting.
     */
    private void rescale() {
        long range = maxEnd - minStart;
        long mid = range / 2;
        long newStart = mid - (range / zoomSetting / 2);
        long newEnd = mid + (range / zoomSetting / 2);

        if (zoomSetting == 1) {
            newStart = minStart;
            newEnd = maxEnd;
        }

        timescaleController.setConstraints(newStart, newEnd,
                zoomIntervals(zoomSetting));

        ViewableModel newModel = timescaleController.getViewableModel();
        newModel.setZoomWindowStart(newStart);
        newModel.setZoomWindowEnd(newEnd);

        needleController.setViewableModel(newModel);
        regionController.setViewableModel(newModel);

        tracksEditorController.setViewableModel(newModel);
    }

    /**
     * Handles the event for adding a temporal bookmark to selected tracks.
     */
    private void addBookmarkHandler() {
        tracksEditorController.addTemporalBookmarkToSelected(needleController
                .getCurrentTime());
    }

    /**
     * Handles the event for toggling the snap functionality on and off.
     * 
     * @param e
     *            expecting the event to be generated from a JToggleButton
     */
    private void snapToggleHandler(ActionEvent e) {
        JToggleButton toggle = (JToggleButton) e.getSource();
        if (toggle.isSelected()) {
            toggle.setText("Snap On");
            tracksEditorController.setAllowSnap(true);
        } else {
            toggle.setText("Snap Off");
            tracksEditorController.setAllowSnap(false);
        }
    }

    /**
     * NeedlePainter needle was moved using the mouse
     * 
     * @param e
     *            needle event from the NeedlePainter
     */
    public void needleMoved(NeedleEvent e) {
        fireTracksControllerEvent(TracksEvent.NEEDLE_EVENT, e);
    }

    /**
     * RegionPainter region markers were moved using the mouse
     * 
     * @param e
     */
    public void markerMoved(MarkerEvent e) {
        fireTracksControllerEvent(TracksEvent.MARKER_EVENT, e);
    }

    /**
     * TrackPainter recorded a change in the track's offset using the mouse
     * 
     * @param e
     */
    public void offsetChanged(CarriageEvent e) {
        tracksEditorController.setTrackOffset(e.getTrackId(), e.getOffset(), e
                .getTemporalPosition());
        fireTracksControllerEvent(TracksEvent.CARRIAGE_EVENT, e);
        tracksPanel.invalidate();
        tracksPanel.repaint();
    }

    /**
     * Track is requesting current temporal position to create a bookmark.
     * 
     * @param e
     */
    public void requestBookmark(CarriageEvent e) {
        TrackController trackController = (TrackController) e.getSource();
        trackController.addTemporalBookmark(needleController.getCurrentTime());
    }

    /**
     * A track's selection state was changed.
     */
    public void selectionChanged(CarriageEvent e) {
        bookmarkButton.setEnabled(tracksEditorController.hasSelectedTracks());
    }

    /**
     * Register listeners who are interested in events from this class.
     * 
     * @param listener
     */
    public synchronized void addTracksControllerListener(
            TracksControllerListener listener) {
        listenerList.add(TracksControllerListener.class, listener);
    }

    /**
     * De-register listeners from receiving events from this class.
     * 
     * @param listener
     */
    public synchronized void removeTracksControllerListener(
            TracksControllerListener listener) {
        listenerList.remove(TracksControllerListener.class, listener);
    }

    /**
     * Used to fire a new event informing listeners about new child component
     * events.
     * 
     * @param needleEvent
     */
    private synchronized void fireTracksControllerEvent(
            TracksEvent tracksEvent, EventObject eventObject) {
        TracksControllerEvent e =
                new TracksControllerEvent(this, tracksEvent, eventObject);
        Object[] listeners = listenerList.getListenerList();
        /*
         * The listener list contains the listening class and then the listener
         * instance.
         */
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == TracksControllerListener.class) {
                ((TracksControllerListener) listeners[i + 1])
                        .tracksControllerChanged(e);
            }
        }
    }

}
