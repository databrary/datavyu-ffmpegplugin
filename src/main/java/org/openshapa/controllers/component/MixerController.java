package org.openshapa.controllers.component;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.EventObject;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import net.miginfocom.swing.MigLayout;

import org.openshapa.OpenSHAPA;

import org.openshapa.event.component.CarriageEvent;
import org.openshapa.event.component.CarriageEvent.EventType;
import org.openshapa.event.component.CarriageEventListener;
import org.openshapa.event.component.TimescaleEvent;
import org.openshapa.event.component.TimescaleListener;
import org.openshapa.event.component.TracksControllerEvent;
import org.openshapa.event.component.TracksControllerEvent.TracksEvent;
import org.openshapa.event.component.TracksControllerListener;

import org.openshapa.models.component.MixerConstants;
import org.openshapa.models.component.MixerModelImpl;
import org.openshapa.models.component.MixerModel;
import org.openshapa.models.component.NeedleConstants;
import org.openshapa.models.component.RegionModel;
import org.openshapa.models.component.RegionState;
import org.openshapa.models.component.RegionConstants;
import org.openshapa.models.component.TimescaleConstants;
import org.openshapa.models.component.TrackConstants;
import org.openshapa.models.component.TrackModel;
import org.openshapa.models.component.ViewportModel;
import org.openshapa.models.component.ViewportState;
import org.openshapa.models.id.Identifier;

import org.openshapa.views.component.TrackPainter;

import com.google.common.collect.Maps;

import org.apache.commons.lang.text.StrSubstitutor;

import com.apple.eawt.event.GesturePhaseEvent;
import com.apple.eawt.event.GesturePhaseListener;
import com.apple.eawt.event.GestureUtilities;
import com.apple.eawt.event.MagnificationEvent;
import com.apple.eawt.event.MagnificationListener;
import com.apple.eawt.event.SwipeEvent;
import com.apple.eawt.event.SwipeListener;

import com.sun.jna.Platform;

import org.openshapa.plugins.CustomActions;


/**
 * This class manages the tracks information interface.
 */
public final class MixerController implements PropertyChangeListener,
    CarriageEventListener, AdjustmentListener, TimescaleListener {

    /** Root interface panel. */
    private JPanel tracksPanel;

    /** Scroll pane that holds track information. */
    private JScrollPane tracksScrollPane;

    /** This layered pane holds the needle painter. */
    private JLayeredPane layeredPane;

    /**
     * Zoom setting in the interval (0, 1.0) where increasing values represent "zooming in".
     */
    private double zoomSetting = MixerConstants.DEFAULT_ZOOM;

    private final int TRACKS_SCROLL_BAR_RANGE = 1000000;

    /** The value of the earliest video's start time in milliseconds. */
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

    /** Bookmark (create snap point) button. */
    private JButton bookmarkButton;

    /** Button for locking and unlocking all tracks. */
    private JToggleButton lockToggle;

    /** Tracks horizontal scroll bar. */
    private JScrollBar tracksScrollBar;
    private boolean isUpdatingTracksScrollBar = false;

    /** Zoom slider. */
    private JSlider zoomSlide;
    private boolean isUpdatingZoomSlide = false;

    /** Zoom icon. */
    private final ImageIcon zoomIcon = new ImageIcon(getClass().getResource(
                "/icons/magnifier.png"));

    /** Master mixer to listen to. */
    private final MixerModelImpl mixerModel;
    private final ViewportModel viewportModel;
    private final RegionModel regionModel;

    /** Listens and processes gestures on Mac OS X. */
    private final OSXGestureListener osxGestureListener = Platform.isMac()
        ? new OSXGestureListener() : null;

    /**
     * Create a new MixerController.
     */
    public MixerController() {
        mixerModel = new MixerModelImpl();

        viewportModel = mixerModel.getViewportModel();
        viewportModel.addPropertyChangeListener(this);

        regionModel = mixerModel.getRegionModel();
        regionModel.addPropertyChangeListener(this);


        runInEDT(new Runnable() {
                @Override public void run() {
                    initView();
                }
            });
    }

    private static void runInEDT(final Runnable task) {

        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    private void initView() {

        // Set default scale values
        minStart = 0;

        listenerList = new EventListenerList();

        // Set up the root panel
        tracksPanel = new JPanel();
        tracksPanel.setLayout(new MigLayout("ins 0",
                "[left|left|left|left]rel push[right|right]", ""));
        tracksPanel.setBackground(Color.WHITE);

        if (Platform.isMac()) {
            osxGestureListener.register(tracksPanel);
        }

        // Menu buttons
        lockToggle = new JToggleButton("Lock all");
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

        zoomSlide = new JSlider(JSlider.HORIZONTAL, 1, 1000, 1);
        zoomSlide.addChangeListener(new ChangeListener() {
                public void stateChanged(final ChangeEvent e) {

                    if (!isUpdatingZoomSlide
                            && zoomSlide.getValueIsAdjusting()) {

                        try {
                            isUpdatingZoomSlide = true;
                            zoomSetting =
                                (double) (zoomSlide.getValue()
                                    - zoomSlide.getMinimum())
                                / (zoomSlide.getMaximum()
                                    - zoomSlide.getMinimum() + 1);
                            viewportModel.setViewportZoom(zoomSetting,
                                needleController.getNeedleModel()
                                    .getCurrentTime());
                        } finally {
                            isUpdatingZoomSlide = false;
                        }
                    }
                }
            });
        zoomSlide.setName("zoomSlider");
        zoomSlide.setBackground(tracksPanel.getBackground());

        JButton zoomRegionButton = new JButton("", zoomIcon);
        zoomRegionButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    zoomToRegion(e);
                }
            });
        zoomRegionButton.setName("zoomRegionButton");

        tracksPanel.add(lockToggle);
        tracksPanel.add(bookmarkButton);
        tracksPanel.add(snapRegion);
        tracksPanel.add(clearRegion);
        tracksPanel.add(zoomRegionButton);
        tracksPanel.add(zoomSlide, "wrap");

        timescaleController = new TimescaleController(mixerModel);
        timescaleController.addTimescaleEventListener(this);
        needleController = new NeedleController(this, mixerModel);
        regionController = new RegionController(mixerModel);
        tracksEditorController = new TracksEditorController(this, mixerModel);

        needleController.setTimescaleTransitionHeight(
            timescaleController.getTimescaleModel()
                .getZoomWindowToTrackTransitionHeight());
        needleController.setZoomIndicatorHeight(
            timescaleController.getTimescaleModel()
                .getZoomWindowIndicatorHeight());

        // Set up the layered pane
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new MigLayout("fillx, ins 0"));

        final int layeredPaneHeight = 272;
        final int timescaleViewHeight = timescaleController.getTimescaleModel()
            .getHeight();

        final int needleHeadHeight = (int) Math.ceil(
                NeedleConstants.NEEDLE_HEAD_HEIGHT);
        final int tracksScrollPaneY = needleHeadHeight + 1;
        final int timescaleViewY = layeredPaneHeight
            - MixerConstants.HSCROLL_HEIGHT - timescaleViewHeight;
        final int tracksScrollPaneHeight = timescaleViewY - tracksScrollPaneY;
        final int tracksScrollBarY = timescaleViewY + timescaleViewHeight;
        final int needleAndRegionMarkerHeight = (timescaleViewY
                + timescaleViewHeight
                - timescaleController.getTimescaleModel()
                .getZoomWindowIndicatorHeight()
                - timescaleController.getTimescaleModel()
                .getZoomWindowToTrackTransitionHeight() + 1);

        // Set up filler component responsible for horizontal resizing of the
        // layout.
        {

            // Null args; let layout manager handle sizes.
            Box.Filler filler = new Filler(null, null, null);
            filler.setName("Filler");
            filler.addComponentListener(new SizeHandler());

            Map<String, String> constraints = Maps.newHashMap();
            constraints.put("wmin",
                Integer.toString(MixerConstants.MIXER_MIN_WIDTH));

            // TODO Could probably use this same component to handle vertical
            // resizing...
            String template =
                "id filler, h 0!, grow 100 0, wmin ${wmin}, cell 0 0 ";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            layeredPane.setLayer(filler, MixerConstants.FILLER_ZORDER);
            layeredPane.add(filler, sub.replace(template),
                MixerConstants.FILLER_ZORDER);
        }

        // Set up the timescale layout
        {
            JComponent timescaleView = timescaleController.getView();

            Map<String, String> constraints = Maps.newHashMap();
            constraints.put("x", Integer.toString(TimescaleConstants.XPOS_ABS));
            constraints.put("y", Integer.toString(timescaleViewY));

            // Calculate padding from the right
            int rightPad = (int) (RegionConstants.RMARKER_WIDTH
                    + MixerConstants.VSCROLL_WIDTH + MixerConstants.R_EDGE_PAD);
            constraints.put("x2", "(filler.w-" + rightPad + ")");
            constraints.put("y2", "(tscale.y+${height})");
            constraints.put("height", Integer.toString(timescaleViewHeight));

            String template = "id tscale, pos ${x} ${y} ${x2} ${y2}";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            // Must call setLayer first.
            layeredPane.setLayer(timescaleView,
                MixerConstants.TIMESCALE_ZORDER);
            layeredPane.add(timescaleView, sub.replace(template),
                MixerConstants.TIMESCALE_ZORDER);
        }

        // Set up the scroll pane's layout.
        {
            tracksScrollPane = new JScrollPane(
                    tracksEditorController.getView());
            tracksScrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            tracksScrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            tracksScrollPane.setBorder(BorderFactory.createEmptyBorder());
            tracksScrollPane.setName("jScrollPane");

            Map<String, String> constraints = Maps.newHashMap();
            constraints.put("x", "0");
            constraints.put("y", Integer.toString(tracksScrollPaneY));
            constraints.put("x2",
                "(filler.w-" + MixerConstants.R_EDGE_PAD + ")");
            constraints.put("height", Integer.toString(tracksScrollPaneHeight));

            String template = "pos ${x} ${y} ${x2} n, h ${height}!";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            layeredPane.setLayer(tracksScrollPane,
                MixerConstants.TRACKS_ZORDER);
            layeredPane.add(tracksScrollPane, sub.replace(template),
                MixerConstants.TRACKS_ZORDER);
        }

        // Create the region markers and set up the layout.
        {
            JComponent regionView = regionController.getView();

            Map<String, String> constraints = Maps.newHashMap();

            int x = (int) (TrackConstants.HEADER_WIDTH
                    - RegionConstants.RMARKER_WIDTH);
            constraints.put("x", Integer.toString(x));
            constraints.put("y", "0");

            // Padding from the right
            int rightPad = MixerConstants.R_EDGE_PAD
                + MixerConstants.VSCROLL_WIDTH - 2;

            constraints.put("x2", "(filler.w-" + rightPad + ")");
            constraints.put("height",
                Integer.toString(needleAndRegionMarkerHeight));

            String template = "pos ${x} ${y} ${x2} n, h ${height}::";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            layeredPane.setLayer(regionView, MixerConstants.REGION_ZORDER);
            layeredPane.add(regionView, sub.replace(template),
                MixerConstants.REGION_ZORDER);
        }

        // Set up the timing needle's layout
        {
            JComponent needleView = needleController.getView();

            Map<String, String> constraints = Maps.newHashMap();

            int x = (int) (TrackConstants.HEADER_WIDTH
                    - NeedleConstants.NEEDLE_HEAD_WIDTH
                    + NeedleConstants.NEEDLE_WIDTH);
            constraints.put("x", Integer.toString(x));
            constraints.put("y", "0");

            // Padding from the right
            int rightPad = MixerConstants.R_EDGE_PAD
                + MixerConstants.VSCROLL_WIDTH - 1;

            constraints.put("x2", "(filler.w-" + rightPad + ")");
            constraints.put("height",
                Integer.toString(
                    needleAndRegionMarkerHeight
                    + timescaleController.getTimescaleModel()
                        .getZoomWindowToTrackTransitionHeight()
                    + timescaleController.getTimescaleModel()
                        .getZoomWindowIndicatorHeight() - 1));

            String template = "pos ${x} ${y} ${x2} n, h ${height}::";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            layeredPane.setLayer(needleView, MixerConstants.NEEDLE_ZORDER);
            layeredPane.add(needleView, sub.replace(template),
                MixerConstants.NEEDLE_ZORDER);
        }

        // Set up the snap marker's layout
        {
            JComponent markerView = tracksEditorController.getMarkerView();

            Map<String, String> constraints = Maps.newHashMap();
            constraints.put("x", Integer.toString(TimescaleConstants.XPOS_ABS));
            constraints.put("y", Integer.toString(needleHeadHeight + 1));

            // Padding from the right
            int rightPad = MixerConstants.R_EDGE_PAD
                + MixerConstants.VSCROLL_WIDTH - 1;

            constraints.put("x2", "(filler.w-" + rightPad + ")");
            constraints.put("height",
                Integer.toString(
                    needleAndRegionMarkerHeight - needleHeadHeight - 1));

            String template = "pos ${x} ${y} ${x2} n, h ${height}::";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            layeredPane.setLayer(markerView, MixerConstants.MARKER_ZORDER);
            layeredPane.add(markerView, sub.replace(template),
                MixerConstants.MARKER_ZORDER);
        }

        // Set up the tracks horizontal scroll bar
        {
            tracksScrollBar = new JScrollBar(Adjustable.HORIZONTAL);
            tracksScrollBar.setValues(0, TRACKS_SCROLL_BAR_RANGE, 0,
                TRACKS_SCROLL_BAR_RANGE);
            tracksScrollBar.setUnitIncrement(TRACKS_SCROLL_BAR_RANGE / 20);
            tracksScrollBar.setBlockIncrement(TRACKS_SCROLL_BAR_RANGE / 2);
            tracksScrollBar.addAdjustmentListener(this);
            tracksScrollBar.setValueIsAdjusting(false);
            tracksScrollBar.setVisible(false);
            tracksScrollBar.setName("horizontalScrollBar");

            Map<String, String> constraints = Maps.newHashMap();
            constraints.put("x", Integer.toString(TimescaleConstants.XPOS_ABS));
            constraints.put("y", Integer.toString(tracksScrollBarY));

            int rightPad = (int) (RegionConstants.RMARKER_WIDTH
                    + MixerConstants.VSCROLL_WIDTH + MixerConstants.R_EDGE_PAD);
            constraints.put("x2", "(filler.w-" + rightPad + ")");
            constraints.put("height",
                Integer.toString(MixerConstants.HSCROLL_HEIGHT));

            String template = "pos ${x} ${y} ${x2} n, h ${height}::";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            layeredPane.setLayer(tracksScrollBar,
                MixerConstants.TRACKS_SB_ZORDER);
            layeredPane.add(tracksScrollBar, sub.replace(template),
                MixerConstants.TRACKS_SB_ZORDER);
        }

        {
            Map<String, String> constraints = Maps.newHashMap();
            constraints.put("span", "6");
            constraints.put("width",
                Integer.toString(MixerConstants.MIXER_MIN_WIDTH));
            constraints.put("height", Integer.toString(layeredPaneHeight));

            String template =
                "growx, span ${span}, w ${width}::, h ${height}::, wrap";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            tracksPanel.add(layeredPane, sub.replace(template));
        }

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
    public void setMaxEnd(final long newMaxEnd,
        final boolean resetViewportWindow) {
        viewportModel.setViewportMaxEnd(newMaxEnd, resetViewportWindow);

        if (resetViewportWindow) {
            regionModel.resetPlaybackRegion();
        }
    }

    /**
     * Add a new track to the interface.
     *
     * @param id
     *            Identifier of the track.
     * @param icon
     *            Icon associated with the track.
     * @param mediaPath
     *            Absolute path to the media file.
     * @param trackName
     *            Name of the track.
     * @param duration
     *            The total duration of the track in milliseconds.
     * @param offset
     *            The amount of playback offset in milliseconds.
     * @param trackPainter
     *            The track painter to use.
     */
    public void addNewTrack(final Identifier id, final ImageIcon icon,
        final String mediaPath, final String trackName, final long duration,
        final long offset, final TrackPainter trackPainter) {

        // Check if the scale needs to be updated.
        final long trackEnd = duration + offset;
        final ViewportState viewport = viewportModel.getViewport();

        if ((trackEnd > viewport.getMaxEnd())
                || ((tracksEditorController.numberOfTracks() == 0)
                    && (trackEnd > 0))) {
            viewportModel.setViewportMaxEnd(trackEnd, true);
            regionModel.resetPlaybackRegion();
        }

        tracksEditorController.addNewTrack(id, icon, trackName, mediaPath,
            duration, offset, this, trackPainter);
        tracksScrollPane.validate();

        updateGlobalLockToggle();
    }

    /** Clears the region of interest and zooms all the way out. */
    public void clearRegionAndZoomOut() {
        clearRegionOfInterest();
        zoomToRegion(null);
    }

    /**
     * Bind track actions to a data viewer.
     *
     * @param trackId
     *            Identifier of the track
     * @param actions
     *            Actions to bind with
     */
    public void bindTrackActions(final Identifier trackId,
        final CustomActions actions) {

        if (actions == null) {
            return;
        }

        runInEDT(new Runnable() {
                @Override public void run() {
                    tracksEditorController.bindTrackActions(trackId, actions);
                }
            });
    }

    /**
     * Used to set up the track interface.
     *
     * @param trackId
     *            Track identifier.
     * @param bookmark
     *            Bookmark position in milliseconds.
     * @param lock
     *            True if track movement is locked, false otherwise.
     */
    public void setTrackInterfaceSettings(final Identifier trackId,
        final List<Long> bookmarks, final boolean lock) {
        runInEDT(new Runnable() {
                @Override public void run() {
                    tracksEditorController.setBookmarkPositions(trackId,
                        bookmarks);
                    tracksEditorController.setMovementLock(trackId, lock);
                }
            });
    }

    /**
     * For backwards compatibility; used the set up the track interface. If
     * there are multiple tracks identified by the same media path, only the
     * first track found is used.
     *
     * @param mediaPath
     *            Absolute path to the media file.
     * @param bookmark
     *            Bookmark position in milliseconds.
     * @param lock
     *            True if track movement is locked, false otherwise.
     */
    @Deprecated public void setTrackInterfaceSettings(final String mediaPath,
        final List<Long> bookmarks, final boolean lock) {
        runInEDT(new Runnable() {
                @Override public void run() {
                    tracksEditorController.setBookmarkPositions(mediaPath,
                        bookmarks);
                    tracksEditorController.setMovementLock(mediaPath, lock);
                }
            });
    }

    /**
     * Zooms into the displayed region and re-adjusts the timing needle
     * accordingly.
     *
     * @param evt
     */
    public void zoomToRegion(final ActionEvent evt) {
        final ViewportState viewport = viewportModel.getViewport();
        final RegionState region = regionModel.getRegion();

        if (region.getRegionDuration() >= 1) {
            final int percentOfRegionToPadOutsideMarkers = 5;
            assert (percentOfRegionToPadOutsideMarkers >= 0)
                && (percentOfRegionToPadOutsideMarkers <= 100);

            final long displayedAreaStart = Math.max(region.getRegionStart()
                    - (region.getRegionDuration()
                        * percentOfRegionToPadOutsideMarkers / 100), 0);
            final long displayedAreaEnd = Math.min(region.getRegionEnd()
                    + (region.getRegionDuration()
                        * percentOfRegionToPadOutsideMarkers / 100),
                    viewport.getMaxEnd());

            viewportModel.setViewportWindow(displayedAreaStart,
                displayedAreaEnd);
            needleController.setCurrentTime(region.getRegionStart());
        }
    }

    /**
     * Remove from track panel.
     *
     * @param trackId
     *            identifier of the track to remove.
     */
    public void deregisterTrack(final Identifier trackId) {
        tracksEditorController.removeTrack(trackId, this);

        // Update tracks panel display
        tracksScrollPane.validate();

        updateGlobalLockToggle();
    }

    /**
     * Removes all track components from this controller and resets components.
     */
    public void removeAll() {
        tracksEditorController.removeAllTracks();

        viewportModel.resetViewport();
        regionModel.resetPlaybackRegion();
        needleController.setCurrentTime(0);

        tracksScrollPane.validate();

        tracksPanel.validate();
        tracksPanel.repaint();

        updateGlobalLockToggle();
    }

    /**
     * @return all track models used to represent the UI.
     */
    public Iterable<TrackModel> getAllTrackModels() {
        return tracksEditorController.getAllTrackModels();
    }

    public TrackModel getTrackModel(final Identifier id) {
        return tracksEditorController.getTrackModel(id);
    }

    public MixerModel getMixerModel() {
        return mixerModel;
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

    private void updateZoomSlide(final ViewportState viewport) {
        assert SwingUtilities.isEventDispatchThread();

        if (isUpdatingZoomSlide) {
            return;
        }

        try {
            isUpdatingZoomSlide = true;

            zoomSlide.setValue((int) Math.round(
                    (viewport.getZoomLevel()
                        * (zoomSlide.getMaximum() - zoomSlide.getMinimum()
                            + 1)) + zoomSlide.getMinimum()));
        } finally {
            isUpdatingZoomSlide = false;
        }
    }

    /**
     * Update scroll bar values.
     */
    private void updateTracksScrollBar(final ViewportState viewport) {
        assert SwingUtilities.isEventDispatchThread();

        if (isUpdatingTracksScrollBar) {
            return;
        }

        try {
            isUpdatingTracksScrollBar = true;

            final int startValue = (int) Math.round(
                    (double) viewport.getViewStart() * TRACKS_SCROLL_BAR_RANGE
                    / viewport.getMaxEnd());
            final int extentValue = (int) Math.round(
                    (double) (viewport.getViewDuration())
                    * TRACKS_SCROLL_BAR_RANGE / viewport.getMaxEnd());

            tracksScrollBar.setValues(startValue, extentValue, 0,
                TRACKS_SCROLL_BAR_RANGE);
            tracksScrollBar.setUnitIncrement(extentValue / 20);
            tracksScrollBar.setBlockIncrement(extentValue / 2);
            tracksScrollBar.setVisible((viewport.getViewDuration())
                < viewport.getMaxEnd());
        } finally {
            isUpdatingTracksScrollBar = false;
            tracksPanel.validate();
        }
    }

    /**
     * Handles the event for adding a temporal bookmark to selected tracks.
     */
    private void addBookmarkHandler() {
        tracksEditorController.addTemporalBookmarkToSelected(
            needleController.getNeedleModel().getCurrentTime());
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
        regionModel.resetPlaybackRegion();
    }

    /**
     * Handles the event for toggling movement of tracks on and off.
     *
     * @param e the event to handle
     */
    private void lockToggleHandler(final ActionEvent e) {
        JToggleButton toggle = (JToggleButton) e.getSource();
        tracksEditorController.setLockedState(toggle.isSelected());
        updateGlobalLockToggle();
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

        final ViewportState viewport = viewportModel.getViewport();

        final int startValue = tracksScrollBar.getValue();

        assert tracksScrollBar.getMinimum() == 0;

        final long newWindowStart = (long) Math.round((double) startValue
                / tracksScrollBar.getMaximum() * viewport.getMaxEnd());
        final long newWindowEnd = newWindowStart + viewport.getViewDuration()
            - 1;

        viewportModel.setViewportWindow(newWindowStart, newWindowEnd);

        tracksPanel.repaint();
    }

    /**
     * TrackPainter recorded a change in the track's offset using the mouse.
     *
     * @param e the event to handle
     */
    public void offsetChanged(final CarriageEvent e) {
        final boolean wasOffsetChanged = tracksEditorController.setTrackOffset(
                e.getTrackId(), e.getOffset(), e.getTemporalPosition());
        final CarriageEvent newEvent;

        if (wasOffsetChanged) {
            final long newOffset = tracksEditorController.getTrackModel(
                    e.getTrackId()).getOffset();
            newEvent = new CarriageEvent(e.getSource(), e.getTrackId(),
                    newOffset, e.getBookmarks(), e.getDuration(),
                    e.getTemporalPosition(), e.getEventType(),
                    e.hasModifiers());
        } else {
            newEvent = e;
        }

        fireTracksControllerEvent(TracksEvent.CARRIAGE_EVENT, newEvent);
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
        trackController.addTemporalBookmark(needleController.getNeedleModel()
            .getCurrentTime());

        CarriageEvent newEvent = new CarriageEvent(e.getSource(),
                e.getTrackId(), e.getOffset(), trackController.getBookmarks(),
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
        updateGlobalLockToggle();
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

    private void updateGlobalLockToggle() {
        System.out.println(tracksEditorController.isAnyTrackUnlocked());

        if (tracksEditorController.isAnyTrackUnlocked()) {
            lockToggle.setSelected(false);
            lockToggle.setText("Lock all");
        } else {
            lockToggle.setSelected(true);
            lockToggle.setText("Unlock all");
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

    private void handleViewportChanged(final ViewportState oldViewport,
        final ViewportState newViewport) {
        runInEDT(new Runnable() {
                @Override public void run() {
                    updateZoomSlide(newViewport);
                    updateTracksScrollBar(newViewport);
                    tracksScrollPane.repaint();
                }
            });
    }

    @Override public void propertyChange(final PropertyChangeEvent evt) {

        if (evt.getSource() == mixerModel.getViewportModel()) {
            final ViewportState oldViewport =
                (evt.getOldValue() instanceof ViewportState)
                ? (ViewportState) evt.getOldValue() : null;
            final ViewportState newViewport =
                (evt.getNewValue() instanceof ViewportState)
                ? (ViewportState) evt.getNewValue() : null;
            handleViewportChanged(oldViewport, newViewport);
        }
    }

    private void handleResize() {
        final ViewportState viewport = viewportModel.getViewport();

        if (Double.isNaN(viewport.getResolution())) {
            viewportModel.setViewport(viewport.getViewStart(),
                viewport.getViewEnd(), viewport.getMaxEnd(),
                timescaleController.getView().getWidth());
        } else {
            viewportModel.resizeViewport(viewportModel.getViewport()
                .getViewStart(), timescaleController.getView().getWidth());
        }
    }

    /** Handles component resizing. */
    private final class SizeHandler extends ComponentAdapter {
        @Override public void componentResized(final ComponentEvent e) {
            handleResize();
        }
    }

    private class OSXGestureListener implements MagnificationListener,
        GesturePhaseListener, SwipeListener {

        /**
         * Cumulative sum of the current zoom gesture, where positive values
         * indicate zooming in (enlarging) and negative values indicate zooming
         * out (shrinking). On a 2009 MacBook Pro, pinch-and-zooming from
         * corner-to-corner of the trackpad will result in a total sum of
         * approximately +3.0 (zooming in) or -3.0 (zooming out).
         */
        private double osxMagnificationGestureSum = 0;

        /** Relative zoom when the magnification gesture began. */
        private double osxMagnificationGestureInitialZoomSetting;

        public void register(final JComponent component) {
            GestureUtilities.addGestureListenerTo(tracksPanel,
                osxGestureListener);
        }

        /**
         * Invoked when a magnification gesture ("pinch and squeeze") is performed by the user on Mac OS X.
         *
         * @param e contains the scale of the magnification
         */
        @Override public void magnify(final MagnificationEvent e) {
            osxMagnificationGestureSum += e.getMagnification();

            /** Amount of the pinch-and-squeeze gesture required to perform a full zoom in the mixer. */
            final double fullZoomMotion = 2.0;
            final double newZoomSetting = Math.min(Math.max(
                        osxMagnificationGestureInitialZoomSetting
                        + (osxMagnificationGestureSum / fullZoomMotion), 0.0),
                    1.0);

            viewportModel.setViewportZoom(newZoomSetting,
                needleController.getNeedleModel().getCurrentTime());
        }

        /**
         * Indicates that the user has started performing a gesture on Mac OS X.
         */
        @Override public void gestureBegan(final GesturePhaseEvent e) {
            osxMagnificationGestureSum = 0;
            osxMagnificationGestureInitialZoomSetting =
                viewportModel.getViewport().getZoomLevel();
        }

        /**
         * Indicates that the user has finished performing a gesture on Mac OS X.
         */
        @Override public void gestureEnded(final GesturePhaseEvent e) {
        }

        @Override public void swipedDown(final SwipeEvent e) {
        }

        @Override public void swipedLeft(final SwipeEvent e) {
            swipeHorizontal(false);
        }

        @Override public void swipedRight(final SwipeEvent e) {
            swipeHorizontal(true);
        }

        private void swipeHorizontal(final boolean swipeLeft) {

            /** The number of horizontal swipe actions needed to move the scroll bar along by the visible amount (i.e. a page left/right action) */
            final int swipesPerVisibleAmount = 5;
            final int newValue = tracksScrollBar.getValue()
                + ((swipeLeft ? -1 : 1) * tracksScrollBar.getVisibleAmount()
                    / swipesPerVisibleAmount);
            tracksScrollBar.setValue(Math.max(
                    Math.min(newValue,
                        tracksScrollBar.getMaximum()
                        - tracksScrollBar.getVisibleAmount()),
                    tracksScrollBar.getMinimum()));
        }

        @Override public void swipedUp(final SwipeEvent e) {
        }
    }
}
