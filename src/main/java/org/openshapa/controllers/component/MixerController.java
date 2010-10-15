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

import org.openshapa.models.component.MixerConstants;
import org.openshapa.models.component.MixerModel;
import org.openshapa.models.component.NeedleConstants;
import org.openshapa.models.component.RegionConstants;
import org.openshapa.models.component.TimescaleConstants;
import org.openshapa.models.component.TrackConstants;
import org.openshapa.models.component.TrackModel;
import org.openshapa.models.component.Viewport;
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
    NeedleEventListener, MarkerEventListener, CarriageEventListener,
    AdjustmentListener, TimescaleListener {

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
    private final MixerModel masterMixer;

    /** Listens and processes gestures on Mac OS X. */
    private final OSXGestureListener osxGestureListener = Platform.isMac()
        ? new OSXGestureListener() : null;

    /**
     * Create a new MixerController.
     */
    public MixerController() {
        masterMixer = new MixerModel();
        masterMixer.addPropertyChangeListener(this);

        Runnable edtTask = new Runnable() {
                @Override public void run() {
                    initView();
                }
            };

        if (SwingUtilities.isEventDispatchThread()) {
            edtTask.run();
        } else {
            SwingUtilities.invokeLater(edtTask);
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

        zoomSlide = new JSlider(JSlider.HORIZONTAL, 1, 1000, 1);
        zoomSlide.addChangeListener(new ChangeListener() {
                public void stateChanged(final ChangeEvent e) {

                    if (!isUpdatingZoomSlide) {
                        zoomScale(e);
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

        timescaleController = new TimescaleController(masterMixer);
        timescaleController.addTimescaleEventListener(this);
        needleController = new NeedleController(masterMixer);
        regionController = new RegionController(masterMixer);
        tracksEditorController = new TracksEditorController(masterMixer);

        needleController.setTimescaleTransitionHeight(timescaleController.getTimescaleModel().getZoomWindowToTrackTransitionHeight());
        needleController.setZoomIndicatorHeight(timescaleController.getTimescaleModel().getZoomWindowIndicatorHeight());
        
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

            regionController.setPlaybackRegion(minStart,
                MixerConstants.DEFAULT_DURATION);
            regionController.addMarkerEventListener(this);

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
                Integer.toString(needleAndRegionMarkerHeight + timescaleController.getTimescaleModel().getZoomWindowToTrackTransitionHeight() + timescaleController.getTimescaleModel().getZoomWindowIndicatorHeight() - 1));

            String template = "pos ${x} ${y} ${x2} n, h ${height}::";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            needleController.addNeedleEventListener(this);

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
     * @return the new maximum end time in milliseconds
     */
    public long setMaxEnd(final long newMaxEnd) {
        masterMixer.setViewportMaxEnd(newMaxEnd);

        return masterMixer.getViewport().getMaxEnd();
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
    	final Viewport viewport = masterMixer.getViewport();
        if ((trackEnd > viewport.getMaxEnd())
                || ((tracksEditorController.numberOfTracks() == 0) && (trackEnd > 0))) {
            regionController.setPlaybackRegion(0, trackEnd);
           	masterMixer.setViewportMaxEnd(trackEnd);
        }

        tracksEditorController.addNewTrack(id, icon, trackName, mediaPath,
            duration, offset, this, trackPainter);

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

        Runnable edtTask = new Runnable() {
                @Override public void run() {
                    tracksEditorController.bindTrackActions(trackId, actions);
                }
            };

        if (SwingUtilities.isEventDispatchThread()) {
            edtTask.run();
        } else {
            SwingUtilities.invokeLater(edtTask);
        }

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
        final long bookmark, final boolean lock) {
        Runnable edtTask = new Runnable() {
                @Override public void run() {
                    tracksEditorController.setBookmarkPosition(trackId,
                        bookmark);
                    tracksEditorController.setMovementLock(trackId, lock);
                }
            };

        if (SwingUtilities.isEventDispatchThread()) {
            edtTask.run();
        } else {
            SwingUtilities.invokeLater(edtTask);
        }
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
        final long bookmark, final boolean lock) {
        Runnable edtTask = new Runnable() {
                @Override public void run() {
                    tracksEditorController.setBookmarkPosition(mediaPath,
                        bookmark);
                    tracksEditorController.setMovementLock(mediaPath, lock);
                }
            };

        if (SwingUtilities.isEventDispatchThread()) {
            edtTask.run();
        } else {
            SwingUtilities.invokeLater(edtTask);
        }

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


        Viewport viewport = masterMixer.getViewport();

        if (displayedAreaEnd > viewport.getMaxEnd()) {
            displayedAreaEnd = viewport.getMaxEnd();
        }

        masterMixer.setViewportWindow(displayedAreaStart, displayedAreaEnd);

        needleController.setCurrentTime(regionController.getRegionModel()
            .getRegionStart());
    }

    /**
    * Zooms into the displayed scale and re-adjusts the timing needle
    * accordingly.
    *
    * @param evt
    */
    public void zoomScale(final ChangeEvent evt) {

        if (!isUpdatingZoomSlide) {
            zoomSetting =
                (double) (zoomSlide.getValue() - zoomSlide.getMinimum())
                / (zoomSlide.getMaximum() - zoomSlide.getMinimum() + 1);

            masterMixer.setViewportZoom(zoomSetting,
                needleController.getCurrentTime());
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
    }

    /**
     * Removes all track components from this controller and resets components.
     */
    public void removeAll() {
        tracksEditorController.removeAllTracks();

        masterMixer.resetViewport();

        regionController.setPlaybackRegion(0,
            masterMixer.getViewport().getMaxEnd());
        needleController.setCurrentTime(0);

        tracksScrollPane.validate();

        tracksPanel.validate();
        tracksPanel.repaint();
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

    private void updateZoomSlide(final Viewport viewport) {
        assert SwingUtilities.isEventDispatchThread();

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
    private void updateTracksScrollBar(final Viewport viewport) {
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
        Viewport viewport = masterMixer.getViewport();

        setPlayRegionStart(minStart);
        setPlayRegionEnd(viewport.getMaxEnd());
        fireTracksControllerEvent(TracksEvent.MARKER_EVENT,
            new MarkerEvent(this, Marker.START_MARKER, minStart));
        fireTracksControllerEvent(TracksEvent.MARKER_EVENT,
            new MarkerEvent(this, Marker.END_MARKER, viewport.getMaxEnd()));
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

        Viewport viewport = masterMixer.getViewport();

        final int startValue = tracksScrollBar.getValue();
        final int endValue = startValue + tracksScrollBar.getVisibleAmount();

        assert tracksScrollBar.getMinimum() == 0;

        final long newWindowStart = (long) Math.floor((double) startValue
                / tracksScrollBar.getMaximum() * viewport.getMaxEnd());
        final long newWindowEnd = (long) Math.ceil((double) endValue
                / tracksScrollBar.getMaximum() * viewport.getMaxEnd());

        masterMixer.setViewportWindow(newWindowStart, newWindowEnd);

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

    private void handleViewportChanged() {
        final Viewport viewport = masterMixer.getViewport();

        if (SwingUtilities.isEventDispatchThread()) {
            updateZoomSlide(viewport);
            updateTracksScrollBar(viewport);
            tracksScrollPane.repaint();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        updateZoomSlide(viewport);
                        updateTracksScrollBar(viewport);
                        tracksScrollPane.repaint();
                    }
                });
        }
    }

    @Override public void propertyChange(final PropertyChangeEvent evt) {

        if (Viewport.NAME.equals(evt.getPropertyName())) {
            handleViewportChanged();
        }
    }

    private void handleResize() {
        Viewport viewport = masterMixer.getViewport();

        if (Double.isNaN(viewport.getResolution())) {
            masterMixer.setViewport(viewport.getViewStart(),
                viewport.getViewEnd(), viewport.getMaxEnd(),
                timescaleController.getView().getWidth());

        } else {
            masterMixer.resizeViewport(masterMixer.getViewport().getViewStart(),
                timescaleController.getView().getWidth());
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

            masterMixer.setViewportZoom(newZoomSetting,
                needleController.getCurrentTime());
        }

        /**
         * Indicates that the user has started performing a gesture on Mac OS X.
         */
        @Override public void gestureBegan(final GesturePhaseEvent e) {
            osxMagnificationGestureSum = 0;
            osxMagnificationGestureInitialZoomSetting =
                masterMixer.getViewport().getZoomLevel();
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
