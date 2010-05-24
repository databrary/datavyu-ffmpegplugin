package org.openshapa.views;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;


import java.util.EventObject;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import net.miginfocom.swing.MigLayout;

import org.openshapa.OpenSHAPA;

import org.openshapa.controllers.component.NeedleController;
import org.openshapa.controllers.component.RegionController;
import org.openshapa.controllers.component.TimescaleController;
import org.openshapa.controllers.component.TrackController;
import org.openshapa.controllers.component.TracksEditorController;

import org.openshapa.event.component.CarriageEvent;
import org.openshapa.event.component.CarriageEvent.EventType;
import org.openshapa.event.component.CarriageEventListener;
import org.openshapa.event.component.MarkerEvent;
import org.openshapa.event.component.MarkerEventListener;
import org.openshapa.event.component.NeedleEvent;
import org.openshapa.event.component.NeedleEventListener;
import org.openshapa.event.component.TracksControllerEvent;
import org.openshapa.event.component.TracksControllerEvent.TracksEvent;
import org.openshapa.event.component.TracksControllerListener;

import org.openshapa.models.component.TrackModel;
import org.openshapa.models.component.ViewableModel;

import org.openshapa.views.component.TrackPainter;
import org.openshapa.views.continuous.CustomActionListener;


/**
 * This class manages the tracks information interface.
 */
public final class MixerControllerV implements NeedleEventListener,
    MarkerEventListener, CarriageEventListener, AdjustmentListener {

    /** Root interface panel. */
    private JPanel tracksPanel;

    /** Scroll pane that holds track information. */
    private JScrollPane tracksScrollPane;

    /** This layered pane holds the needle painter. */
    private JLayeredPane layeredPane;

    /**
     * Zoomed into the display by how much, as a percentage.
     * Ranges from 1 to 3200% (32x).
     */
    private int zoomSetting = 100;

    /**
     * The value of the longest video's time length in milliseconds.
     */
    private long maxEnd;

    /**
     * The value of the earliest video's start time in milliseconds.
     */
    private long minStart;

    /** Listeners interested in tracks controller events. */
    private EventListenerList listenerList;

    /** Controller responsible for managing the time scale. */
    private TimescaleController timescaleController;

    /** Controller responsible for managing the timing needle. */
    private NeedleController needleController;

    /** Controller responsible for managing a selected region. */
    private RegionController regionController;

    /** Controller responsible for managing tracks. */
    private TracksEditorController tracksEditorController;

    private JButton bookmarkButton;

    private JScrollBar tracksScrollBar;

    private JSlider zoomSlide;

    /** Zoom icon. */
    private final ImageIcon zoomIcon = new ImageIcon(getClass().getResource(
                "/icons/magnifier.png"));

    /**
     * Create a new MixerController.
     */
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
        JToggleButton lockToggle = new JToggleButton("Lock");
        lockToggle.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    lockToggleHandler(e);
                }
            });
        lockToggle.setName("lockToggleButton");

        bookmarkButton = new JButton("Add Bookmark");
        bookmarkButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    addBookmarkHandler();
                }
            });
        bookmarkButton.setEnabled(false);
        bookmarkButton.setName("bookmarkButton");

        JButton snapRegion = new JButton("Snap Region");
        snapRegion.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    snapRegionHandler(e);
                }
            });
        snapRegion.setName("snapRegionButton");

        zoomSlide = new JSlider(JSlider.HORIZONTAL, 100, 3200, 100);
        zoomSlide.setToolTipText("1.00x");
        zoomSlide.addChangeListener(new ChangeListener() {
                public void stateChanged(final ChangeEvent e) {
                    zoomScale(e);

                    if (!zoomSlide.getValueIsAdjusting()) {
                        zoomSlide.setToolTipText(
                            ((float) zoomSlide.getValue() / 100) + "x");
                    }
                }
            });
        zoomSlide.setName("zoomSlider");
        zoomSlide.setBackground(tracksPanel.getBackground());

        JButton zoomRegionButton = new JButton("", zoomIcon);
        zoomRegionButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    zoomToRegion(e);
                    zoomTracks(e);
                }
            });
        zoomRegionButton.setName("zoomRegionButton");

        tracksPanel.add(lockToggle);
        tracksPanel.add(bookmarkButton);
        tracksPanel.add(snapRegion);
        tracksPanel.add(zoomRegionButton);
        tracksPanel.add(zoomSlide, "wrap");

        // Add the timescale
        timescaleController = new TimescaleController();

        JComponent timescaleView = timescaleController.getView();

        {
            Dimension size = new Dimension();
            size.setSize(785, 35);
            timescaleView.setSize(size);
            timescaleView.setPreferredSize(size);
            timescaleController.setConstraints(minStart, maxEnd,
                zoomIntervals(1));

            ViewableModel vm = timescaleController.getViewableModel();
            vm.setEnd(maxEnd);
            timescaleController.setViewableModel(vm);
        }

        layeredPane.add(timescaleView, Integer.valueOf(0));

        // Add the scroll pane
        tracksEditorController = new TracksEditorController();
        tracksEditorController.setViewableModel(
            timescaleController.getViewableModel());

        tracksScrollPane = new JScrollPane(tracksEditorController.getView());
        tracksScrollPane.setVerticalScrollBarPolicy(
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        tracksScrollPane.setHorizontalScrollBarPolicy(
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tracksScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tracksScrollPane.setName("jScrollPane");

        // Set an explicit size of the scroll pane
        {
            Dimension size = new Dimension();
            size.setSize(785, 195);
            tracksScrollPane.setSize(size);
            tracksScrollPane.setPreferredSize(size);
            tracksScrollPane.setLocation(0, 39);
        }

        layeredPane.add(tracksScrollPane, Integer.valueOf(0));

        // Create the region markers
        regionController = new RegionController();

        JComponent regionView = regionController.getView();

        {
            Dimension size = new Dimension();
            size.setSize(785, 234);
            regionView.setSize(size);
            regionView.setPreferredSize(size);

            regionController.setViewableModel(
                timescaleController.getViewableModel());
            regionController.setPlaybackRegion(minStart, maxEnd);
        }

        regionController.addMarkerEventListener(this);

        layeredPane.add(regionView, Integer.valueOf(20));

        // Create the timing needle
        needleController = new NeedleController();

        JComponent needleView = needleController.getView();

        {
            Dimension size = new Dimension();
            size.setSize(785, 234);
            needleView.setSize(size);
            needleView.setPreferredSize(size);

            // Values determined through trial-and-error.
            needleController.setViewableModel(
                timescaleController.getViewableModel());
        }

        needleController.addNeedleEventListener(this);
        layeredPane.add(needleView, Integer.valueOf(30));

        // Create the snap marker
        JComponent markerView = tracksEditorController.getMarkerView();

        {
            Dimension size = new Dimension();
            size.setSize(785, 234);
            markerView.setSize(size);
            markerView.setPreferredSize(size);
        }

        layeredPane.add(markerView, Integer.valueOf(5));

        tracksScrollBar = new JScrollBar(Adjustable.HORIZONTAL);

        {
            Dimension size = new Dimension();
            size.setSize(700, 17);
            tracksScrollBar.setSize(size);
            tracksScrollBar.setPreferredSize(size);
            tracksScrollBar.setLocation(85, 234);
        }

        tracksScrollBar.setValues(0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
        tracksScrollBar.setUnitIncrement(Integer.MAX_VALUE / 100);
        tracksScrollBar.setBlockIncrement(Integer.MAX_VALUE / 10);
        tracksScrollBar.addAdjustmentListener(this);
        tracksScrollBar.setValueIsAdjusting(false);
        tracksScrollBar.setName("horizontalScrollBar");

        layeredPane.add(tracksScrollBar, Integer.valueOf(100));

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
     * Sets the longest data feed duration.
     *
     * @param newMaxEnd
     *            duration in milliseconds
     */
    public void setMaxEnd(final long newMaxEnd) {
        maxEnd = newMaxEnd;

        ViewableModel model = timescaleController.getViewableModel();
        model.setEnd(newMaxEnd);
        timescaleController.setViewableModel(model);
        regionController.setViewableModel(model);
        needleController.setViewableModel(model);
        tracksEditorController.setViewableModel(model);
        updateTracksScrollBar();
    }

    /**
     * Add a new track to the interface.
     *
     * @param icon Icon associated with the track.
     * @param mediaPath Absolute path to the media file.
     * @param trackName Name of the track.
     * @param duration The total duration of the track in milliseconds.
     * @param offset The amount of playback offset in milliseconds.
     * @param trackPainter The track painter to use.
     */
    public void addNewTrack(final ImageIcon icon, final String mediaPath,
        final String trackName, final long duration, final long offset,
        final TrackPainter trackPainter) {

        // Check if the scale needs to be updated.
        if ((duration + offset) > maxEnd) {
            maxEnd = duration + offset;

            ViewableModel model = timescaleController.getViewableModel();
            model.setEnd(maxEnd);
            timescaleController.setViewableModel(model);
            regionController.setViewableModel(model);
            needleController.setViewableModel(model);
            tracksEditorController.setViewableModel(model);
            rescale();
            updateTracksScrollBar();
        }

        tracksEditorController.addNewTrack(icon, mediaPath, trackName, duration,
            offset, this, trackPainter);

        tracksScrollPane.validate();
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
    public void bindTrackActions(final String mediaPath,
        final CustomActionListener dataViewer, final boolean actionSupported1,
        final ImageIcon actionIcon1, final boolean actionSupported2,
        final ImageIcon actionIcon2, final boolean actionSupported3,
        final ImageIcon actionIcon3) {
        tracksEditorController.bindTrackActions(mediaPath, dataViewer,
            actionSupported1, actionIcon1, actionSupported2, actionIcon2,
            actionSupported3, actionIcon3);
    }

    /**
     * Used to set up the track interface.
     *
     * @param mediaPath Absolute path to the media file the track is
     * representing.
     * @param bookmark Bookmark position in milliseconds.
     * @param lock True if track movement is locked, false otherwise.
     */
    public void setTrackInterfaceSettings(final String mediaPath,
        final long bookmark, final boolean lock) {
        tracksEditorController.setBookmarkPosition(mediaPath, bookmark);
        tracksEditorController.setMovementLock(mediaPath, lock);
    }

    /**
     * @param time
     *            Set the current time in milliseconds to use.
     */
    public void setCurrentTime(final long time) {
        needleController.setCurrentTime(time);
    }

    /**
     * @return Current time, in milliseconds, that is being used.
     */
    public long getCurrentTime() {
        return needleController.getCurrentTime();
    }

    /**
     * Set the start of the new playback region.
     *
     * @param time
     *            time in milliseconds
     */
    public void setPlayRegionStart(final long time) {
        regionController.setPlaybackRegionStart(time);
    }

    /**
     * Set the end of the new playback region.
     *
     * @param time
     *            time in milliseconds
     */
    public void setPlayRegionEnd(final long time) {
        regionController.setPlaybackRegionEnd(time);
    }

    /**
    * Zooms into the displayed scale and re-adjusts the timing needle
    * accordingly.
    *
    * @param evt
    */
    public void zoomToRegion(final ActionEvent evt) {
        long regionSize = regionController.getRegionModel().getRegionEnd()
            - regionController.getRegionModel().getRegionStart();

        if (regionSize <= 0) {
            return;
        }

        long totalSize = timescaleController.getViewableModel().getEnd();

        long zoom = 100 * totalSize / regionSize;


        if (zoom < 100) {
            zoom = 100;
        } else if (zoom > 3200) {
            zoom = 3200;
        }

        zoomSetting = (int) zoom;
        zoomSlide.setValue(zoomSetting);
        rescale();
        updateTracksScrollBar();

    }

    /**
     * Smooth animation for zoom.
     * @param goalZoom goal zoom value for animation
     */
    private void smoothZoom(final int goalZoom) {

        // Newton motion equations
        int v = 0;
        int a = 5;
        int currZoom = zoomSlide.getValue();
        int startingZoom = zoomSlide.getValue();
        Timer timer = new Timer();

        while (currZoom < goalZoom) {
            currZoom++;
            zoomSetting = currZoom;
            zoomSlide.setValue(zoomSetting);
            rescale();
            updateTracksScrollBar();
        }

        while (currZoom > goalZoom) {
            currZoom--;
            zoomSetting = currZoom;
            zoomSlide.setValue(zoomSetting);
            rescale();
            updateTracksScrollBar();
        }

// while (zoomSetting != goalZoom) { timer.schedule(new TimerTask() {
//
// public void run() { //less than midpoint, accelerate if (Math.abs(currZoom -
// startingZoom) < Math.abs(currZoom - goalZoom)) { v = v + a; if (currZoom <
// goalZoom) { currZoom = currZoom + v; } else if (currZoom > goalZoom) {
// currZoom = currZoom - v; }
//
// zoomSetting = currZoom; zoomSlide.setValue(zoomSetting); rescale();
// updateTracksScrollBar(); } else { v = v - a; if (currZoom < goalZoom) { if
// (goalZoom > currZoom + v) { currZoom = goalZoom; } else { currZoom = currZoom
// + v; } } else if (currZoom > goalZoom) { if (goalZoom < currZoom - v) {
// currZoom = goalZoom; } else { currZoom = currZoom - v; } }
//
//
// zoomSetting = currZoom; zoomSlide.setValue(zoomSetting); rescale();
// updateTracksScrollBar(); } } }, 100); }
    }

    /**
    * Zooms into the displayed scale and re-adjusts the timing needle
    * accordingly.
    *
    * @param evt
    */
    public void zoomScale(final ChangeEvent evt) {
        zoomSetting = zoomSlide.getValue();

        rescale();
        updateTracksScrollBar();
    }

    /**
     * Update the track display after a zoom.
     *
     * @param evt The event to handle.
     */
    public void zoomTracks(final ActionEvent evt) {
        ViewableModel model = timescaleController.getViewableModel();
        tracksEditorController.setViewableModel(model);
        updateTracksScrollBar();
    }

    /**
     * Deregister track and its viewer from track panel.
     *
     * @param mediaPath
     * @param dataViewer
     */
    public void deregisterTrack(final String mediaPath,
        final CustomActionListener dataViewer) {
        tracksEditorController.unbindTrackActions(mediaPath, dataViewer);

        tracksEditorController.removeTrack(mediaPath, this);

        // If there are no more tracks, reset.
        if (maxEnd == 0) {
            maxEnd = 60000;
            zoomSetting = 100;
        }

        // Update zoom window scale
        rescale();

        // Update zoomed tracks
        zoomTracks(null);

        updateTracksScrollBar();

        // Update tracks panel display
        tracksScrollPane.validate();
    }

    /**
     * Removes all track components from this controller and resets components.
     */
    public void removeAll() {
        tracksEditorController.removeAllTracks();

        maxEnd = 60000;
        zoomSetting = 100;
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

        updateTracksScrollBar();
    }

    /**
     * @return all track models used to represent the UI.
     */
    public Iterable<TrackModel> getAllTrackModels() {
        return tracksEditorController.getAllTrackModels();
    }

    /**
     * @return NeedleController.
     */
    public NeedleController getNeedleController() {
        return needleController;
    }

    /**
     * @return RegionController.
     */
    public RegionController getRegionController() {
        return regionController;
    }

    /**
     * @return TimescaleController.
     */
    public TimescaleController getTimescaleController() {
        return timescaleController;
    }

    /**
     * @return TracksEditorController.
     */
    public TracksEditorController getTracksEditorController() {
        return tracksEditorController;
    }

    /**
     * @param zoomValue
     *            supports 1x, 2x, 4x, 8x, 16x, 32x
     * @return the amount of intervals to show given a zoom value
     */
    private int zoomIntervals(final int zoomValue) {
        assert (zoomValue >= 1);
        assert (zoomValue <= 3200);

        if (zoomValue <= 200) {
            return 20;
        }

        if (zoomValue <= 500) {
            return 15;
        }

        if (zoomValue <= 1200) {
            return 10;
        }

        if (zoomValue <= 3200) {
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
        long newStart = regionController.getRegionModel().getRegionStart();
        long newEnd = regionController.getRegionModel().getRegionStart()
            + (range / (zoomSetting / 100));

        if (zoomSetting == 100) {
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
     * Update scroll bar values.
     */
    private void updateTracksScrollBar() {
        ViewableModel model = timescaleController.getViewableModel();

        /*
         * Doing these calculations because setValues uses integers but our
         * video lengths are longs.
         */
        int startValue = (int) (((float) model.getZoomWindowStart()
                    / (float) model.getEnd()) * Integer.MAX_VALUE);
        int extentValue = (int)
            (((float) (model.getZoomWindowEnd() - model.getZoomWindowStart())
                    / (float) model.getEnd()) * Integer.MAX_VALUE);

        tracksScrollBar.setValues(startValue, extentValue, 0,
            Integer.MAX_VALUE);
    }

    /**
     * Handles the event for adding a temporal bookmark to selected tracks.
     */
    private void addBookmarkHandler() {
        tracksEditorController.addTemporalBookmarkToSelected(
            needleController.getCurrentTime());
    }

    /**
     * Handles the event for toggling the snap functionality on and off.
     *
     * @param e
     *            expecting the event to be generated from a JToggleButton
     */
    private void snapRegionHandler(final ActionEvent e) {
        OpenSHAPA.getDataController().setRegionOfInterestAction();

    }

    /**
     * Handles the event for toggling movement of tracks on and off.
     *
     * @param e the event to handle
     */
    private void lockToggleHandler(final ActionEvent e) {
        JToggleButton toggle = (JToggleButton) e.getSource();
        tracksEditorController.setLockedState(toggle.isSelected());
    }

    /**
     * Handles the event for scrolling the tracks interface horizontally.
     *
     * @param e the event to handle
     */
    public void adjustmentValueChanged(final AdjustmentEvent e) {

        int startValue = tracksScrollBar.getValue();
        int endValue = startValue + tracksScrollBar.getVisibleAmount();

        ViewableModel model = timescaleController.getViewableModel();

        /*
         *  Calculate the new window start and end only if the bar is being
         * scrolled.
         */
        if (e.getValueIsAdjusting()) {
            long newWindowStart = (long) ((startValue
                        / (float) Integer.MAX_VALUE) * model.getEnd());
            long newWindowEnd = (long) ((endValue / (float) Integer.MAX_VALUE)
                    * model.getEnd());
            model.setZoomWindowStart(newWindowStart);
            model.setZoomWindowEnd(newWindowEnd);
        }

        timescaleController.setViewableModel(model);
        regionController.setViewableModel(model);
        needleController.setViewableModel(model);
        tracksEditorController.setViewableModel(model);

        tracksPanel.repaint();
    }

    /**
     * NeedlePainter needle was moved using the mouse.
     *
     * @param e
     *            needle event from the NeedlePainter
     */
    public void needleMoved(final NeedleEvent e) {
        fireTracksControllerEvent(TracksEvent.NEEDLE_EVENT, e);
    }

    /**
     * RegionPainter region markers were moved using the mouse.
     *
     * @param e the event to handle
     */
    public void markerMoved(final MarkerEvent e) {
        fireTracksControllerEvent(TracksEvent.MARKER_EVENT, e);
    }

    /**
     * TrackPainter recorded a change in the track's offset using the mouse.
     *
     * @param e the event to handle
     */
    public void offsetChanged(final CarriageEvent e) {
        tracksEditorController.setTrackOffset(e.getTrackId(), e.getOffset(),
            e.getTemporalPosition());
        fireTracksControllerEvent(TracksEvent.CARRIAGE_EVENT, e);
        tracksPanel.invalidate();
        tracksPanel.repaint();
    }

    /**
     * Track is requesting current temporal position to create a bookmark.
     *
     * @param e the event to handle
     */
    public void requestBookmark(final CarriageEvent e) {
        TrackController trackController = (TrackController) e.getSource();
        trackController.addTemporalBookmark(needleController.getCurrentTime());

        CarriageEvent newEvent = new CarriageEvent(e.getSource(),
                e.getTrackId(), e.getOffset(), trackController.getBookmark(),
                e.getDuration(), e.getTemporalPosition(),
                EventType.BOOKMARK_CHANGED, e.hasModifiers());

        fireTracksControllerEvent(TracksEvent.CARRIAGE_EVENT, newEvent);
    }

    /**
     * Track is requesting for bookmark to be saved.
     *
     * @param e the event to handle
     */
    public void saveBookmark(final CarriageEvent e) {
        fireTracksControllerEvent(TracksEvent.CARRIAGE_EVENT, e);
    }

    /**
     * A track's selection state was changed.
     *
     * @param e the event to handle
     */
    public void selectionChanged(final CarriageEvent e) {
        bookmarkButton.setEnabled(tracksEditorController.hasSelectedTracks());
    }

    /**
     * Register listeners who are interested in events from this class.
     *
     * @param listener the listener to register
     */
    public void addTracksControllerListener(
        final TracksControllerListener listener) {

        synchronized (this) {
            listenerList.add(TracksControllerListener.class, listener);
        }
    }

    /**
     * De-register listeners from receiving events from this class.
     *
     * @param listener the listener to remove
     */
    public void removeTracksControllerListener(
        final TracksControllerListener listener) {

        synchronized (this) {
            listenerList.remove(TracksControllerListener.class, listener);
        }
    }

    /**
     * Used to fire a new event informing listeners about new child component
     * events.
     *
     * @param tracksEvent The event to handle
     * @param eventObject The event object to repackage
     */
    private void fireTracksControllerEvent(final TracksEvent tracksEvent,
        final EventObject eventObject) {
        TracksControllerEvent e = new TracksControllerEvent(this, tracksEvent,
                eventObject);
        Object[] listeners = listenerList.getListenerList();

        synchronized (this) {

            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {

                if (listeners[i] == TracksControllerListener.class) {
                    ((TracksControllerListener) listeners[i + 1])
                        .tracksControllerChanged(e);
                }
            }
        }
    }

}
