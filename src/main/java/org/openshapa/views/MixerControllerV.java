package org.openshapa.views;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import java.util.EventObject;

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
import org.openshapa.event.component.TimescaleEvent;
import org.openshapa.event.component.TimescaleListener;
import org.openshapa.event.component.TracksControllerEvent;
import org.openshapa.event.component.MarkerEvent.Marker;
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
    MarkerEventListener, CarriageEventListener, AdjustmentListener,
    TimescaleListener {

    /** Root interface panel. */
    private JPanel tracksPanel;

    /** Scroll pane that holds track information. */
    private JScrollPane tracksScrollPane;

    /** This layered pane holds the needle painter. */
    private JLayeredPane layeredPane;

    /**
     * Default zoom setting.
     */
    private final double DEFAULT_ZOOM_SETTING = 0.0;

    /**
     * Zoom setting in the interval (0, 1.0) where increasing values represent "zooming in".
     */
    private double zoomSetting = DEFAULT_ZOOM_SETTING;

    /**
     * The value of the longest video's time length in milliseconds.
     */
    private long maxEnd;

    /**
     * Default duration used when there are no videos or data tracks.
     */
    private final long DEFAULT_DURATION = 60 * 1000;

    private final int TRACKS_SCROLL_BAR_RANGE = 1000000;

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
    private boolean isUpdatingTracksScrollBar = false;

    private JSlider zoomSlide;

    private boolean isUpdatingZoomSlide = false;

    /** Zoom icon. */
    private final ImageIcon zoomIcon = new ImageIcon(getClass().getResource(
                "/icons/magnifier.png"));

    /**
     * Create a new MixerController.
     */
    public MixerControllerV() {

        // Set default scale values
        maxEnd = DEFAULT_DURATION;
        minStart = 0;

        listenerList = new EventListenerList();

        // Not using MigLayout with JLayeredPane because of layout issues
        layeredPane = new JLayeredPane();

        // Set up the root panel
        tracksPanel = new JPanel();
        tracksPanel.setLayout(new MigLayout("ins 0",
                "[left|left|left|left]rel push[right|right]", ""));
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

        JButton clearRegion = new JButton("Clear Region");
        clearRegion.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    clearRegionHandler(e);
                }
            });
        clearRegion.setName("clearRegionButton");

        zoomSlide = new JSlider(JSlider.HORIZONTAL, 1, 1000, 50);
//        zoomSlide.setToolTipText("1.00x");
        zoomSlide.addChangeListener(new ChangeListener() {
                public void stateChanged(final ChangeEvent e) {

                    if (!isUpdatingZoomSlide) {
                        zoomScale(e);
                    }

/*
                    if (!zoomSlide.getValueIsAdjusting()) {
                        zoomSlide.setToolTipText(
                            ((float) zoomSlide.getValue() / 100) + "x");
                    }
*/
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
        tracksPanel.add(clearRegion);
        tracksPanel.add(zoomRegionButton);
        tracksPanel.add(zoomSlide, "wrap");

        timescaleController = new TimescaleController();
        needleController = new NeedleController();

        final int layeredPaneHeight = 272;
        final int tracksScrollBarHeight = 17;
        final int timescaleViewHeight = timescaleController.getTimescaleModel()
            .getHeight();

        final int needleAndRegionMarkerTopY = 0;
        final int tracksScrollPaneY = needleAndRegionMarkerTopY
            + (int) Math.ceil(needleController.getNeedleModel()
                .getNeedleHeadHeight()) + 1;
        final int timescaleViewY = layeredPaneHeight - tracksScrollBarHeight
            - timescaleViewHeight;
        final int tracksScrollPaneHeight = timescaleViewY - tracksScrollPaneY;
        final int tracksScrollBarY = timescaleViewY + timescaleViewHeight;
        final int needleAndRegionMarkerHeight = (timescaleViewY
                + timescaleController.getTimescaleModel().getHeight()
                - timescaleController.getTimescaleModel()
                .getZoomWindowIndicatorHeight()
                - timescaleController.getTimescaleModel()
                .getZoomWindowToTrackTransitionHeight() + 1)
            - needleAndRegionMarkerTopY;

        // Add the timescale
        timescaleController.addTimescaleEventListener(this);

        JComponent timescaleView = timescaleController.getView();

        {
            Dimension size = new Dimension();
            size.setSize(785, timescaleViewHeight);
            timescaleView.setSize(size);
            timescaleView.setPreferredSize(size);
            timescaleView.setLocation(0, timescaleViewY);
            timescaleController.setConstraints(minStart, maxEnd);

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
            size.setSize(785, tracksScrollPaneHeight);
            tracksScrollPane.setSize(size);
            tracksScrollPane.setPreferredSize(size);
            tracksScrollPane.setLocation(0, tracksScrollPaneY);
        }

        layeredPane.add(tracksScrollPane, Integer.valueOf(0));

        // Create the region markers
        regionController = new RegionController();

        JComponent regionView = regionController.getView();

        {
            Dimension size = new Dimension();
            size.setSize(785, needleAndRegionMarkerHeight);
            regionView.setSize(size);
            regionView.setPreferredSize(size);

            regionController.setViewableModel(
                timescaleController.getViewableModel());
            regionController.setPlaybackRegion(minStart, maxEnd);
        }

        regionController.addMarkerEventListener(this);

        layeredPane.add(regionView, Integer.valueOf(20));

        // Create the timing needle
        JComponent needleView = needleController.getView();

        {
            Dimension size = new Dimension();
            size.setSize(785, needleAndRegionMarkerHeight);
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
            size.setSize(timescaleController.getTimescaleModel()
                .getEffectiveWidth(), tracksScrollBarHeight);
            tracksScrollBar.setSize(size);
            tracksScrollBar.setPreferredSize(size);
            tracksScrollBar.setLocation(timescaleController.getTimescaleModel()
                .getPaddingLeft(), tracksScrollBarY);
        }

        tracksScrollBar.setValues(0, TRACKS_SCROLL_BAR_RANGE, 0,
            TRACKS_SCROLL_BAR_RANGE);
        tracksScrollBar.setUnitIncrement(TRACKS_SCROLL_BAR_RANGE / 20);
        tracksScrollBar.setBlockIncrement(TRACKS_SCROLL_BAR_RANGE / 2);
        tracksScrollBar.addAdjustmentListener(this);
        tracksScrollBar.setValueIsAdjusting(false);
        tracksScrollBar.setVisible(false);
        tracksScrollBar.setName("horizontalScrollBar");

        layeredPane.add(tracksScrollBar, Integer.valueOf(100));

        tracksPanel.add(layeredPane,
            "span 6, w 785!, h " + layeredPaneHeight + "!, wrap");
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
        if (((duration + offset) > maxEnd)
                || ((tracksEditorController.numberOfTracks() == 0)
                    && ((duration + offset) > 0))) {
            maxEnd = duration + offset;

            ViewableModel model = timescaleController.getViewableModel();
            model.setEnd(maxEnd);
            timescaleController.setViewableModel(model);
            regionController.setViewableModel(model);
            regionController.setPlaybackRegion(0, maxEnd);
            needleController.setViewableModel(model);
            tracksEditorController.setViewableModel(model);
            rescale();
            updateTracksScrollBar();
        }

        tracksEditorController.addNewTrack(icon, mediaPath, trackName, duration,
            offset, this, trackPainter);

        tracksScrollPane.validate();
    }

    /** Clears the region of interest and zooms all the way out. */
    public void clearRegionAndZoomOut() {
        clearRegionOfInterest();
        zoomToRegion(null);
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
    * Zooms into the displayed region and re-adjusts the timing needle
    * accordingly.
    *
    * @param evt
    */
    public void zoomToRegion(final ActionEvent evt) {
        final long regionWidth =
            regionController.getRegionModel().getRegionEnd()
            - regionController.getRegionModel().getRegionStart() + 1;

        if (regionWidth <= 0) {
            return;
        }

        final int percentOfRegionToPadOutsideMarkers = 5;
        assert (percentOfRegionToPadOutsideMarkers >= 0)
            && (percentOfRegionToPadOutsideMarkers <= 100);

        long displayedAreaStart = regionController.getRegionModel()
            .getRegionStart();
        displayedAreaStart -= regionWidth * percentOfRegionToPadOutsideMarkers
            / 100;

        if (displayedAreaStart < 0) {
            displayedAreaStart = 0;
        }

        long displayedAreaEnd = regionController.getRegionModel()
            .getRegionEnd();
        displayedAreaEnd += regionWidth * percentOfRegionToPadOutsideMarkers
            / 100;

        if (displayedAreaEnd > maxEnd) {
            displayedAreaEnd = maxEnd;
        }

        timescaleController.setConstraints(displayedAreaStart,
            displayedAreaEnd);
        updateZoomSlide(displayedAreaStart, displayedAreaEnd);
        needleController.setCurrentTime(regionController.getRegionModel()
            .getRegionStart());

        rescale((displayedAreaStart + displayedAreaEnd) / 2, false);
        updateTracksScrollBar();
    }

    /**
    * Zooms into the displayed scale and re-adjusts the timing needle
    * accordingly.
    *
    * @param evt
    */
    public void zoomScale(final ChangeEvent evt) {
        zoomSetting = (double) (zoomSlide.getValue() - zoomSlide.getMinimum())
            / (zoomSlide.getMaximum() - zoomSlide.getMinimum() + 1);
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
            maxEnd = DEFAULT_DURATION;
            zoomSetting = DEFAULT_ZOOM_SETTING;
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

        maxEnd = DEFAULT_DURATION;
        zoomSetting = DEFAULT_ZOOM_SETTING;
        rescale();
        zoomTracks(null);

        ViewableModel model = timescaleController.getViewableModel();
        model.setZoomWindowStart(0);
        model.setZoomWindowEnd(DEFAULT_DURATION);

        regionController.setViewableModel(model);
        regionController.setPlaybackRegion(0, DEFAULT_DURATION);
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

    private void updateZoomSlide(final long startTime, final long endTime) {

        try {
            isUpdatingZoomSlide = true;

            final long displayedAreaWidth = endTime - startTime + 1;
            final double millisecondsPerPixel = (double) displayedAreaWidth
                / timescaleController.getTimescaleModel().getEffectiveWidth();
            zoomSetting = getZoomSettingFor(millisecondsPerPixel);
            zoomSlide.setValue((int) Math.round(
                    (zoomSetting
                        * (zoomSlide.getMaximum() - zoomSlide.getMinimum()
                            + 1)) + zoomSlide.getMinimum()));
        } finally {
            isUpdatingZoomSlide = false;
        }
    }

    private double getZoomSettingFor(final double millisecondsPerPixel) {

        if (millisecondsPerPixel
                >= ((double) maxEnd
                    / (timescaleController.getTimescaleModel()
                        .getEffectiveWidth() + 1))) {
            return 0;
        }

        final double value = 1
            - (Math.log(
                    millisecondsPerPixel / lowerMillisecondsPerPixelBounds())
                / Math.log(
                    upperMillisecondsPerPixelBounds()
                    / lowerMillisecondsPerPixelBounds()));

        return Math.min(Math.max(value, 0), 1.0);
    }

    private double getMillisecondsPerPixelFor(final double zoomSettingValue) {
        double value = (lowerMillisecondsPerPixelBounds()
                * Math.exp(
                    Math.log(
                        upperMillisecondsPerPixelBounds()
                        / lowerMillisecondsPerPixelBounds())
                    * (1.0 - zoomSettingValue)));

        return Math.min(Math.max(value, lowerMillisecondsPerPixelBounds()),
                upperMillisecondsPerPixelBounds());
    }

    private double lowerMillisecondsPerPixelBounds() {
        return 1.0;
    }

    private double upperMillisecondsPerPixelBounds() {
        final long maxTimeMilliseconds = (maxEnd > 0) ? maxEnd
                                                      : (24 * 60 * 60 * 1000);

        return Math.ceil((double) maxTimeMilliseconds
                / timescaleController.getTimescaleModel().getEffectiveWidth());
    }

    /**
     * Recalculates timing scale and needle constraints based on the minimum
     * track start time, longest track time, and current zoom setting.
     */
    private void rescale() {
        rescale(needleController.getCurrentTime(), false);
    }

    private void rescale(final long centerTime, final boolean center) {
        final double millisecondsPerPixel = getMillisecondsPerPixelFor(
                zoomSetting);

        // preserve the needle position
        long centerPositionTime = Math.min(Math.max(centerTime, minStart),
                maxEnd);

        long zoomCenterTime = 0;
        double dxZoomCenterRatio = 0.5;

        if ((centerTime
                    >= timescaleController.getViewableModel()
                    .getZoomWindowStart())
                && (centerTime
                    <= timescaleController.getViewableModel()
                    .getZoomWindowEnd())) {
            long zoomWindowRange = timescaleController.getViewableModel()
                .getZoomWindowEnd()
                - timescaleController.getViewableModel().getZoomWindowStart();
            long zoomWindowStart = timescaleController.getViewableModel()
                .getZoomWindowStart();

            if (!center) {
                dxZoomCenterRatio =
                    (double) (centerPositionTime - zoomWindowStart)
                    / zoomWindowRange;
            }

            zoomCenterTime = centerPositionTime;
        } else {
            zoomCenterTime = (timescaleController.getViewableModel()
                    .getZoomWindowStart()
                    + timescaleController.getViewableModel().getZoomWindowEnd())
                / 2;
        }

        dxZoomCenterRatio = Math.min(Math.max(dxZoomCenterRatio, 0.0), 1.0);

        long newZoomWindowTimeRange = Math.round(millisecondsPerPixel
                * timescaleController.getTimescaleModel().getEffectiveWidth());

        if (newZoomWindowTimeRange <= 0) {
            newZoomWindowTimeRange = 1;
        } else if (newZoomWindowTimeRange >= (maxEnd + 1)) {
            newZoomWindowTimeRange = maxEnd + 1;
        }

        assert (newZoomWindowTimeRange >= 1)
            && (newZoomWindowTimeRange <= (maxEnd + 1));

        long newStart = Math.round(zoomCenterTime
                - (dxZoomCenterRatio * newZoomWindowTimeRange));

        if ((newStart + newZoomWindowTimeRange) > maxEnd) {
            newStart = maxEnd - newZoomWindowTimeRange + 1;
        }

        if (newStart < 0) {
            newStart = 0;
        }

        assert (minStart <= newStart) && (newStart <= maxEnd);

        long newEnd = newStart + newZoomWindowTimeRange - 1;
        assert (minStart <= newEnd) && (newEnd <= maxEnd);

        timescaleController.setConstraints(newStart, newEnd);

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

        if (isUpdatingTracksScrollBar) {
            return;
        }

        try {
            isUpdatingTracksScrollBar = true;

            ViewableModel model = timescaleController.getViewableModel();

            final int startValue = (int) Math.round(
                    (double) model.getZoomWindowStart()
                    * TRACKS_SCROLL_BAR_RANGE / model.getEnd());
            final int extentValue = (int) Math.round(
                    (double) (model.getZoomWindowEnd()
                        - model.getZoomWindowStart() + 1)
                    * TRACKS_SCROLL_BAR_RANGE / model.getEnd());

            tracksScrollBar.setValues(startValue, extentValue, 0,
                TRACKS_SCROLL_BAR_RANGE);
            tracksScrollBar.setUnitIncrement(extentValue / 20);
            tracksScrollBar.setBlockIncrement(extentValue / 2);
            tracksScrollBar.setVisible((model.getZoomWindowEnd()
                    - model.getZoomWindowStart() + 1) < model.getEnd());

            updateZoomSlide(model.getZoomWindowStart(),
                model.getZoomWindowEnd());
        } finally {
            isUpdatingTracksScrollBar = false;
        }
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
     * Handles the event for clearing the snap region.
     *
     * @param e The event that triggered this action.
     */
    private void clearRegionHandler(final ActionEvent e) {
        clearRegionOfInterest();
    }

    /**
     * Clears the region of interest.
     */
    public void clearRegionOfInterest() {
        this.setPlayRegionStart(minStart);
        this.setPlayRegionEnd(maxEnd);
        fireTracksControllerEvent(TracksEvent.MARKER_EVENT,
            new MarkerEvent(this, Marker.START_MARKER, minStart));
        fireTracksControllerEvent(TracksEvent.MARKER_EVENT,
            new MarkerEvent(this, Marker.END_MARKER, maxEnd));
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

        if (isUpdatingTracksScrollBar) {
            return;
        }

        final ViewableModel model = timescaleController.getViewableModel();

        final int startValue = tracksScrollBar.getValue();
        final int endValue = startValue + tracksScrollBar.getVisibleAmount();

        assert tracksScrollBar.getMinimum() == 0;

        final long newWindowStart = (long) Math.floor((double) startValue
                / tracksScrollBar.getMaximum() * model.getEnd());
        final long newWindowEnd = (long) Math.ceil((double) endValue
                / tracksScrollBar.getMaximum() * model.getEnd());

        model.setZoomWindowStart(newWindowStart);
        model.setZoomWindowEnd(newWindowEnd);

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
     * Track lock state changed.
     *
     * @param e the event to handle
     */
    public void lockStateChanged(final CarriageEvent e) {
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

    public void jumpToTime(final TimescaleEvent e) {
        fireTracksControllerEvent(TracksEvent.TIMESCALE_EVENT, e);
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
