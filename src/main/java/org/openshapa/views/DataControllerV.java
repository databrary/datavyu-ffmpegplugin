package org.openshapa.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Set;
import java.util.SimpleTimeZone;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.openshapa.OpenSHAPA;

import org.openshapa.OpenSHAPA.Platform;

import org.openshapa.controllers.CreateNewCellC;
import org.openshapa.controllers.SetNewCellStopTimeC;
import org.openshapa.controllers.SetSelectedCellStartTimeC;
import org.openshapa.controllers.SetSelectedCellStopTimeC;

import org.openshapa.event.component.CarriageEvent;
import org.openshapa.event.component.MarkerEvent;
import org.openshapa.event.component.NeedleEvent;
import org.openshapa.event.component.TimescaleEvent;
import org.openshapa.event.component.TracksControllerEvent;
import org.openshapa.event.component.TracksControllerListener;

import org.openshapa.models.PlaybackModel;

import org.openshapa.util.ClockTimer;
import org.openshapa.util.FloatUtils;
import org.openshapa.util.ClockTimer.ClockListener;

import org.openshapa.views.component.TrackPainter;
import org.openshapa.views.continuous.DataController;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.Plugin;
import org.openshapa.views.continuous.PluginManager;

import com.usermetrix.jclient.UserMetrix;

import java.util.LinkedHashSet;


/**
 * Quicktime video controller.
 */
public final class DataControllerV extends OpenSHAPADialog
    implements ClockListener, TracksControllerListener, DataController {

    /** One second in milliseconds. */
    private static final long ONE_SECOND = 1000L;

    /** Rate of playback for rewinding. */
    private static final float REWIND_RATE = -32F;

    /** Rate of normal playback. */
    private static final float PLAY_RATE = 1F;

    /** Rate of playback for fast forwarding. */
    private static final float FFORWARD_RATE = 32F;

    /** Sequence of allowable shuttle rates. */
    private static final float[] SHUTTLE_RATES;

    /**
     * The threshold to use while synchronising viewers (augmented by rate).
     */
    private static final long SYNC_THRESH = 50;

    /**
     * How often to synchronise the viewers with the master clock.
     */
    private static final long SYNC_PULSE = 500;

    // Initialize SHUTTLE_RATES
    // values: [ (2^-5), ..., (2^0), ..., (2^5) ]

    /** The max power used for playback rates; i.e. 2^POWER = max. */
    private static final int POWER = 5;

    static {
        SHUTTLE_RATES = new float[(2 * POWER) + 1];

        float value = 1;
        SHUTTLE_RATES[POWER] = value;

        for (int i = 1; i <= POWER; ++i) {
            value *= 2;
            SHUTTLE_RATES[POWER + i] = value;
            SHUTTLE_RATES[POWER - i] = 1F / value;
        }
    }

    /** The jump multiplier for shift-jogging. */
    private static final int SHIFTJOG = 5;

    /** The jump multiplier for ctrl-shift-jogging. */
    private static final int CTRLSHIFTJOG = 10;

    /** Format for representing time. */
    private static final DateFormat CLOCK_FORMAT;

    // initialize standard date format for clock display.
    static {
        CLOCK_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");
        CLOCK_FORMAT.setTimeZone(new SimpleTimeZone(0, "NO_ZONE"));
    }

    /**
     * Enumeration of shuttle directions.
     */
    enum ShuttleDirection {

        /** The backwards playrate. */
        BACKWARDS(-1),

        /** Playrate for undefined shuttle speeds. */
        UNDEFINED(0),

        /** The playrate for forwards (normal) playrate. */
        FORWARDS(1);

        /** Stores the shuttle direction. */
        private int parameter;

        /**
         * Sets the shuttle direction.
         *
         * @param p
         *            The new shuttle direction.
         */
        ShuttleDirection(final int p) {
            parameter = p;
        }

        /** @return The shuttle direction. */
        public int getParameter() {
            return parameter;
        }
    }

    // -------------------------------------------------------------------------
    // [static]
    //
    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(DataControllerV.class);

    /** Determines whether or not Shift is being held. */
    private boolean shiftMask = false;

    /** Determines whether or not Control is being held. */
    private boolean ctrlMask = false;

    // -------------------------------------------------------------------------
    //
    //
    /** The list of viewers associated with this controller. */
    private Set<DataViewer> viewers;

    /** Shuttle status flag. */
    private ShuttleDirection shuttleDirection = ShuttleDirection.UNDEFINED;

    /** Clock timer. */
    private ClockTimer clock = new ClockTimer();

    /** Is the tracks panel currently shown? */
    private boolean tracksPanelEnabled = false;

    /** The controller for manipulating tracks. */
    private MixerControllerV mixerControllerV;

    /** */
    private javax.swing.JButton createNewCell;

    /** */
    private javax.swing.JButton createNewCellSettingOffset;

    /** */
    private javax.swing.JButton findButton;

    /** */
    private javax.swing.JTextField findOffsetField;

    /** */
    private javax.swing.JTextField findTextField;

    /** */
    private javax.swing.JButton forwardButton;

    /** */
    private javax.swing.JButton goBackButton;

    /** */
    private javax.swing.JTextField goBackTextField;

    /** */
    private javax.swing.JPanel gridButtonPanel;

    /** */
    private javax.swing.JLabel jLabel1;

    /** */
    private javax.swing.JLabel jLabel2;

    /** */
    private javax.swing.JButton jogBackButton;

    /** */
    private javax.swing.JButton jogForwardButton;

    /** */
    private javax.swing.JLabel lblSpeed;

    /** */
    private javax.swing.JButton addDataButton;

    /** */
    private javax.swing.JButton pauseButton;

    /** */
    private javax.swing.JButton playButton;

    /** */
    private javax.swing.JButton rewindButton;

    /** */
    private javax.swing.JButton setCellOffsetButton;

    /** */
    private javax.swing.JButton setCellOnsetButton;

    /** */
    private javax.swing.JButton setNewCellOffsetButton;

    /** */
    private javax.swing.JButton showTracksButton;

    /** */
    private javax.swing.JButton shuttleBackButton;

    /** */
    private javax.swing.JButton shuttleForwardButton;

    /** */
    private javax.swing.JButton stopButton;

    /** */
    private javax.swing.JButton syncButton;

    /** */
    private javax.swing.JButton syncCtrlButton;

    /** */
    private javax.swing.JButton syncVideoButton;

    /** */
    private javax.swing.JLabel timestampLabel;

    /** */
    private javax.swing.JPanel topPanel;

    /** */
    private javax.swing.JPanel tracksPanel;

    /** Model containing playback information. */
    private PlaybackModel playbackModel;

    // -------------------------------------------------------------------------
    // [initialization]
    //
    /**
     * Constructor. Creates a new DataControllerV.
     *
     * @param parent
     *            The parent of this form.
     * @param modal
     *            Should the dialog be modal or not?
     */
    public DataControllerV(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);


        clock.registerListener(this);

        if (OpenSHAPA.getPlatform() == Platform.MAC) {
            initComponentsMac();
        } else {
            initComponents();
        }

        setName(this.getClass().getSimpleName());
        viewers = new LinkedHashSet<DataViewer>();

        playbackModel = new PlaybackModel();
        playbackModel.setPauseRate(0);
        playbackModel.setLastSync(0);
        playbackModel.setMaxDuration(0);

        final int defaultEndTime = 60000;

        // TODO This should really come from the region controller.
        playbackModel.setWindowPlayStart(0);
        playbackModel.setWindowPlayEnd(defaultEndTime);

        mixerControllerV = new MixerControllerV();
        tracksPanel.add(mixerControllerV.getTracksPanel());
        mixerControllerV.addTracksControllerListener(this);

        showTracksPanel(false);
    }

    /**
     * Handles opening a data source.
     *
     * @param jd The file chooser used to open the data source.
     */
    private void openVideo(final OpenSHAPAFileChooser jd) {
        PluginManager pm = PluginManager.getInstance();

        File f = jd.getSelectedFile();
        FileFilter ff = jd.getFileFilter();
        Plugin plugin = pm.getAssociatedPlugin(ff);

        if (plugin != null) {
            DataViewer dataViewer = plugin.getNewDataViewer();
            dataViewer.setDataFeed(f);
            addDataViewer(plugin.getTypeIcon(), dataViewer, f,
                dataViewer.getTrackPainter());
            mixerControllerV.bindTrackActions(f.getAbsolutePath(), dataViewer,
                plugin.isActionSupported1(), plugin.getActionButtonIcon1(),
                plugin.isActionSupported2(), plugin.getActionButtonIcon2(),
                plugin.isActionSupported3(), plugin.getActionButtonIcon3());
        }
    }

    /**
     * Tells the Data Controller if shift is being held or not.
     *
     * @param shift
     *            True for shift held; false otherwise.
     */
    public void setShiftMask(final boolean shift) {
        shiftMask = shift;
    }

    /**
     * Tells the Data Controller if ctrl is being held or not.
     *
     * @param ctrl
     *            True for ctrl held; false otherwise.
     */
    public void setCtrlMask(final boolean ctrl) {
        ctrlMask = ctrl;
    }

    // -------------------------------------------------------------------------
    // [interface] org.openshapa.util.ClockTimer.Listener
    //
    /**
     * @param time
     *            Current clock time in milliseconds.
     */
    public void clockStart(final long time) {
        resetSync();

        long playTime = time;
        final long windowPlayStart = playbackModel.getWindowPlayStart();

        if (playTime < windowPlayStart) {
            playTime = windowPlayStart;
            clockStep(playTime);

            float currentRate = clock.getRate();
            clock.stop();
            clock.setTime(playTime);
            clock.setRate(currentRate);
            clock.start();
        }

        setCurrentTime(playTime);
    }

    /**
     * Reset the sync.
     */
    private void resetSync() {
        playbackModel.setLastSync(0);
    }

    /**
     * @param time
     * \
     *            Current clock time in milliseconds.
     */
    public void clockTick(final long time) {

        try {
            setCurrentTime(time);

            // We are playing back at a rate which is too fast and probably
            // won't allow us to stream all the information at the file. We fake
            // playback by doing a bunch of seekTo's.
            if (playbackModel.isFakePlayback()) {

                for (DataViewer v : viewers) {

                    if ((time > v.getOffset()) && isWithinPlayRange(time, v)) {
                        v.seekTo(time - v.getOffset());
                    }
                }

                // DataViewer is responsible for playing video.
            } else {

                // Synchronise viewers only if we have exceeded our pulse time.
                if ((time - playbackModel.getLastSync())
                        > (SYNC_PULSE * clock.getRate())) {
                    long thresh = (long) (SYNC_THRESH
                            * Math.abs(clock.getRate()));
                    playbackModel.setLastSync(time);

                    for (DataViewer v : viewers) {

                        /*
                         * Use offsets to determine if the video file should
                         * start playing.
                         */

                        if (!v.isPlaying() && isWithinPlayRange(time, v)) {
                            v.seekTo(time - v.getOffset());
                            v.play();
                        }

                        // BugzID:1797 - Viewers who are "playing" outside their
                        // timeframe should be asked to stop.
                        if (v.isPlaying() && !isWithinPlayRange(time, v)) {
                            v.stop();
                        }


                        /*
                         * Only synchronise the data viewers if we have a
                         * noticable drift.
                         */
                        if (v.isPlaying()
                                && (Math.abs(
                                        v.getCurrentTime()
                                        - (time - v.getOffset())) > thresh)) {
                            v.seekTo(time - v.getOffset());
                        }
                    }
                }
            }

            // BugzID:466 - Prevent rewind wrapping the clock past the start
            // point of the view window.
            final long windowPlayStart = playbackModel.getWindowPlayStart();

            if (time < windowPlayStart) {
                setCurrentTime(windowPlayStart);
                clock.stop();
                clock.setTime(windowPlayStart);
                clockStop(windowPlayStart);
            }

            // BugzID:756 - don't play video once past the max duration.
            final long windowPlayEnd = playbackModel.getWindowPlayEnd();

            if ((time >= windowPlayEnd) && (clock.getRate() >= 0)) {
                setCurrentTime(windowPlayEnd);
                clock.stop();
                clock.setTime(windowPlayEnd);
                clockStop(windowPlayEnd);

                return;
            }
        } catch (Exception e) {
            logger.error("Unable to Sync viewers", e);
        }
    }

    /**
     * Determines whether a DataViewer has data for the desired time.
     * @param time The time we wish to play at or seek to.
     * @param view The DataViewer to check.
     * @return True if data exists at this time, and false otherwise.
     */
    private boolean isWithinPlayRange(final long time, final DataViewer view) {
        return (time >= view.getOffset())
            && (time < (view.getOffset() + view.getDuration()));
    }

    /**
     * @param time
     *            Current clock time in milliseconds.
     */
    public void clockStop(final long time) {
        resetSync();
        setCurrentTime(time);

        for (DataViewer viewer : viewers) {
            viewer.stop();

            if (isWithinPlayRange(time, viewer)) {
                viewer.seekTo(time - viewer.getOffset());
            }
        }
    }

    /**
     * @param rate
     *            Current (updated) clock rate.
     */
    public void clockRate(final float rate) {
        resetSync();
        lblSpeed.setText(FloatUtils.doubleToFractionStr(new Double(rate)));

        long time = getCurrentTime();

        // If rate is faster than two times - we need to fake playback to give
        // the illusion of 'smooth'. We do this by stopping the dataviewer and
        // doing many seekTo's to grab individual frames.
        if (Math.abs(rate) > 2.0) {
            playbackModel.setFakePlayback(true);

            for (DataViewer viewer : viewers) {
                viewer.stop();

                if (isWithinPlayRange(time, viewer)) {
                    viewer.setPlaybackSpeed(rate);
                }
            }

            // Rate is less than two times - use the data viewer internal code
            // to draw every frame.
        } else {
            playbackModel.setFakePlayback(false);

            for (DataViewer viewer : viewers) {
                viewer.setPlaybackSpeed(rate);

                if (!clock.isStopped()) {
                    viewer.play();
                }
            }
        }
    }

    /**
     * @param time
     *            Current clock time in milliseconds.
     */
    public void clockStep(final long time) {
        resetSync();
        setCurrentTime(time);

        for (DataViewer viewer : viewers) {

            if (isWithinPlayRange(time, viewer)) {
                viewer.seekTo(time - viewer.getOffset());
            }
        }
    }

    @Override public void dispose() {
        mixerControllerV.removeAll();
        super.dispose();
    }

    /**
     * @return the mixer controller.
     */
    public MixerControllerV getMixerController() {
        return mixerControllerV;
    }

    /**
     * Set time location for data streams.
     *
     * @param milliseconds
     *            The millisecond time.
     */
    public void setCurrentTime(final long milliseconds) {
        resetSync();
        timestampLabel.setText(CLOCK_FORMAT.format(milliseconds));
        mixerControllerV.setCurrentTime(milliseconds);
    }

    /**
     * Get the current master clock time for the controller.
     *
     * @return Time in milliseconds.
     */
    private long getCurrentTime() {
        return clock.getTime();
    }

    /**
     * Remove the specifed viewer from the controller.
     *
     * @param viewer
     *            The viewer to shutdown.
     * @return True if the controller contained this viewer.
     */
    public boolean shutdown(final DataViewer viewer) {

        // Was the viewer removed.
        boolean removed = viewers.remove(viewer);

        if (removed) {

            // Recalculate the maximum playback duration.
            long maxDuration = 0;
            Iterator<DataViewer> it = viewers.iterator();

            while (it.hasNext()) {
                DataViewer dv = it.next();

                if ((dv.getDuration() + dv.getOffset()) > maxDuration) {
                    maxDuration = dv.getDuration() + dv.getOffset();
                }
            }

            playbackModel.setMaxDuration(maxDuration);

            mixerControllerV.setMaxEnd(maxDuration);

            // Reset visualisation of playback regions.
            if (playbackModel.getWindowPlayEnd() > maxDuration) {
                playbackModel.setWindowPlayEnd(maxDuration);
                mixerControllerV.setPlayRegionEnd(maxDuration);
            }

            if (playbackModel.getWindowPlayStart()
                    > playbackModel.getWindowPlayEnd()) {
                playbackModel.setWindowPlayStart(0);
                mixerControllerV.setPlayRegionStart(
                    playbackModel.getWindowPlayStart());
            }

            // Reset visualisation of current playback time.
            long tracksTime = mixerControllerV.getCurrentTime();

            if (tracksTime < playbackModel.getWindowPlayStart()) {
                tracksTime = playbackModel.getWindowPlayStart();
            }

            if (tracksTime > playbackModel.getWindowPlayEnd()) {
                tracksTime = playbackModel.getWindowPlayEnd();
            }

            mixerControllerV.setCurrentTime(tracksTime);

            // Reset the clock.
            clock.setTime(tracksTime);
            clockStep(tracksTime);

            // Data viewer removed, mark project as changed.
            OpenSHAPA.getProjectController().projectChanged();

            // Remove the data viewer from the tracks panel.
            mixerControllerV.deregisterTrack(viewer.getDataFeed()
                .getAbsolutePath(), viewer);
            OpenSHAPA.getApplication().updateTitle();
        }

        return removed;
    }

    /**
     * Initialize the view for Macs.
     */
    private void initComponentsMac() {
        gridButtonPanel = new javax.swing.JPanel();
        syncCtrlButton = new javax.swing.JButton();
        syncButton = new javax.swing.JButton();
        setCellOnsetButton = new javax.swing.JButton();
        setCellOffsetButton = new javax.swing.JButton();
        rewindButton = new javax.swing.JButton();
        playButton = new javax.swing.JButton();
        forwardButton = new javax.swing.JButton();
        goBackButton = new javax.swing.JButton();
        shuttleBackButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        shuttleForwardButton = new javax.swing.JButton();
        findButton = new javax.swing.JButton();
        jogBackButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        createNewCellSettingOffset = new javax.swing.JButton();
        jogForwardButton = new javax.swing.JButton();
        setNewCellOffsetButton = new javax.swing.JButton();
        goBackTextField = new javax.swing.JTextField();
        findTextField = new javax.swing.JTextField();
        syncVideoButton = new javax.swing.JButton();
        addDataButton = new javax.swing.JButton();
        timestampLabel = new javax.swing.JLabel();
        topPanel = new javax.swing.JPanel();
        lblSpeed = new javax.swing.JLabel();
        createNewCell = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        findOffsetField = new javax.swing.JTextField();
        showTracksButton = new javax.swing.JButton();
        tracksPanel = new javax.swing.JPanel();

        final int fontSize = 11;

        setLayout(new MigLayout("hidemode 3"));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.jdesktop.application.ResourceMap resourceMap =
            org.jdesktop.application.Application.getInstance(
                org.openshapa.OpenSHAPA.class).getContext().getResourceMap(
                DataControllerV.class);
        setTitle(resourceMap.getString("title"));
        setName("");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(
                    final java.awt.event.WindowEvent evt) {
                    formWindowClosing(evt);
                }
            });

        gridButtonPanel.setBackground(Color.WHITE);
        gridButtonPanel.setLayout(new MigLayout("wrap 5, ins 15 2 15 2"));

        // Add data button
        addDataButton.setText(resourceMap.getString("addDataButton.text"));
        addDataButton.setFont(new Font("Tahoma", Font.PLAIN, fontSize));
        addDataButton.setFocusPainted(false);
        addDataButton.setName("addDataButton");
        addDataButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent evt) {
                    openVideoButtonActionPerformed(evt);
                }
            });
        gridButtonPanel.add(addDataButton, "span 2, w 90!, h 25!");

        // Timestamp panel
        JPanel timestampPanel = new JPanel(new MigLayout("",
                    "push[][][]0![]push"));
        timestampPanel.setOpaque(false);

        // Timestamp label
        timestampLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        timestampLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timestampLabel.setText("00:00:00:000");
        timestampLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        timestampLabel.setName("timestampLabel");
        timestampPanel.add(timestampLabel);

        jLabel1.setText("@");
        timestampPanel.add(jLabel1);

        lblSpeed.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        lblSpeed.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1,
                2));
        lblSpeed.setName("lblSpeed");
        lblSpeed.setText("0");
        timestampPanel.add(lblSpeed);

        jLabel2.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        jLabel2.setText("x");
        timestampPanel.add(jLabel2);

        gridButtonPanel.add(timestampPanel, "span 3, pushx, growx");

        // Sync control button
        syncCtrlButton.setEnabled(false);
        syncCtrlButton.setFocusPainted(false);
        gridButtonPanel.add(syncCtrlButton, "w 45!, h 45!");

        // Sync button
        syncButton.setEnabled(false);
        syncButton.setFocusPainted(false);
        gridButtonPanel.add(syncButton, "w 45!, h 45!");

        // Set cell onset button
        javax.swing.ActionMap actionMap = org.jdesktop.application.Application
            .getInstance(org.openshapa.OpenSHAPA.class).getContext()
            .getActionMap(DataControllerV.class, this);
        setCellOnsetButton.setAction(actionMap.get("setCellOnsetAction"));
        setCellOnsetButton.setIcon(resourceMap.getIcon(
                "setCellOnsetButton.icon"));
        setCellOnsetButton.setFocusPainted(false);
        setCellOnsetButton.setName("setCellOnsetButton");
        setCellOnsetButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/set-cell-onset-selected.png")));
        gridButtonPanel.add(setCellOnsetButton, "w 45!, h 45!");

        // Set cell offset button
        setCellOffsetButton.setAction(actionMap.get("setCellOffsetAction"));
        setCellOffsetButton.setIcon(resourceMap.getIcon(
                "setCellOffsetButton.icon"));
        setCellOffsetButton.setFocusPainted(false);
        setCellOffsetButton.setName("setCellOffsetButton");
        setCellOffsetButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/set-cell-offset-selected.png")));
        gridButtonPanel.add(setCellOffsetButton, "w 45!, h 45!");

        // Sync video button
        syncVideoButton.setEnabled(false);
        syncVideoButton.setFocusPainted(false);
        gridButtonPanel.add(syncVideoButton, "w 80!, h 45!");

        // Rewind video button
        rewindButton.setAction(actionMap.get("rewindAction"));
        rewindButton.setIcon(resourceMap.getIcon("rewindButton.icon"));
        rewindButton.setFocusPainted(false);
        rewindButton.setName("rewindButton");
        rewindButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/rewind-selected.png")));
        gridButtonPanel.add(rewindButton, "w 45!, h 45!");

        // Play video button
        playButton.setAction(actionMap.get("playAction"));
        playButton.setIcon(resourceMap.getIcon("playButton.icon"));
        playButton.setFocusPainted(false);
        playButton.setName("playButton");
        playButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/play-selected.png")));
        playButton.setRequestFocusEnabled(false);
        gridButtonPanel.add(playButton, "w 45!, h 45!");

        // Fast forward button
        forwardButton.setAction(actionMap.get("forwardAction"));
        forwardButton.setIcon(resourceMap.getIcon("forwardButton.icon"));
        forwardButton.setFocusPainted(false);
        forwardButton.setName("forwardButton");
        forwardButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/fast-forward-selected.png")));
        gridButtonPanel.add(forwardButton, "w 45!, h 45!");

        // Go back button
        goBackButton.setAction(actionMap.get("goBackAction"));
        goBackButton.setIcon(resourceMap.getIcon("goBackButton.icon"));
        goBackButton.setFocusPainted(false);
        goBackButton.setName("goBackButton");
        goBackButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/go-back-selected.png")));
        gridButtonPanel.add(goBackButton, "w 45!, h 45!");

        // Go back text field
        goBackTextField.setHorizontalAlignment(SwingConstants.CENTER);
        goBackTextField.setText("00:00:05:000");
        goBackTextField.setName("goBackTextField");
        gridButtonPanel.add(goBackTextField, "w 80!, h 45!");

        // Shuttle back button
        shuttleBackButton.setAction(actionMap.get("shuttleBackAction"));
        shuttleBackButton.setIcon(resourceMap.getIcon(
                "shuttleBackButton.icon"));
        shuttleBackButton.setFocusPainted(false);
        shuttleBackButton.setName("shuttleBackButton");
        shuttleBackButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/shuttle-backward-selected.png")));
        gridButtonPanel.add(shuttleBackButton, "w 45!, h 45!");

        // Stop button
        stopButton.setAction(actionMap.get("stopAction"));
        stopButton.setIcon(resourceMap.getIcon("stopButton.icon"));
        stopButton.setFocusPainted(false);
        stopButton.setName("stopButton");
        stopButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/stop-selected.png")));
        gridButtonPanel.add(stopButton, "w 45!, h 45!");

        // Shuttle forward button
        shuttleForwardButton.setAction(actionMap.get("shuttleForwardAction"));
        shuttleForwardButton.setIcon(resourceMap.getIcon(
                "shuttleForwardButton.icon"));
        shuttleForwardButton.setFocusPainted(false);
        shuttleForwardButton.setName("shuttleForwardButton");
        shuttleForwardButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/shuttle-forward-selected.png")));
        gridButtonPanel.add(shuttleForwardButton, "w 45!, h 45!");

        // Find button
        findButton.setAction(actionMap.get("findAction"));
        findButton.setIcon(resourceMap.getIcon("findButton.icon"));
        findButton.setFocusPainted(false);
        findButton.setName("findButton");
        findButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/find-selected.png")));
        gridButtonPanel.add(findButton, "w 45!, h 45!");

        // Find text field
        findTextField.setHorizontalAlignment(SwingConstants.CENTER);
        findTextField.setText("00:00:00:000");
        findTextField.setName("findOnsetLabel");
        gridButtonPanel.add(findTextField, "w 80!, h 45!");

        // Jog back button
        jogBackButton.setAction(actionMap.get("jogBackAction"));
        jogBackButton.setIcon(resourceMap.getIcon("jogBackButton.icon"));
        jogBackButton.setFocusPainted(false);
        jogBackButton.setName("jogBackButton");
        jogBackButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/jog-backward-selected.png")));
        gridButtonPanel.add(jogBackButton, "w 45!, h 45!");

        // Pause button
        pauseButton.setAction(actionMap.get("pauseAction"));
        pauseButton.setIcon(resourceMap.getIcon("pauseButton.icon"));
        pauseButton.setFocusPainted(false);
        pauseButton.setName("pauseButton");
        pauseButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/pause-selected.png")));
        gridButtonPanel.add(pauseButton, "w 45!, h 45!");

        // Jog forward button
        jogForwardButton.setAction(actionMap.get("jogForwardAction"));
        jogForwardButton.setIcon(resourceMap.getIcon("jogForwardButton.icon"));
        jogForwardButton.setFocusPainted(false);
        jogForwardButton.setName("jogForwardButton");
        jogForwardButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/jog-forward-selected.png")));
        gridButtonPanel.add(jogForwardButton, "w 45!, h 45!");

        // Create new cell button
        createNewCell.setAction(actionMap.get("createCellAction"));
        createNewCell.setIcon(resourceMap.getIcon("createNewCell.icon"));
        createNewCell.setText(resourceMap.getString("createNewCell.text"));
        createNewCell.setAlignmentY(0.0F);
        createNewCell.setFocusPainted(false);
        createNewCell.setHorizontalTextPosition(
            javax.swing.SwingConstants.CENTER);
        createNewCell.setName("createNewCellButton");
        createNewCell.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/create-new-cell-selected.png")));
        gridButtonPanel.add(createNewCell, "span 1 2, w 45!, h 92!");

        // Find offset field
        findOffsetField.setHorizontalAlignment(SwingConstants.CENTER);
        findOffsetField.setText("00:00:00:000");
        findOffsetField.setToolTipText(resourceMap.getString(
                "findOffsetField.toolTipText"));
        findOffsetField.setEnabled(false);
        findOffsetField.setName("findOffsetLabel");
        gridButtonPanel.add(findOffsetField, "w 80!, h 45!");

        // Create new cell setting offset button
        createNewCellSettingOffset.setAction(actionMap.get(
                "createNewCellAction"));
        createNewCellSettingOffset.setIcon(resourceMap.getIcon(
                "createNewCellButton.icon"));
        createNewCellSettingOffset.setFocusPainted(false);
        createNewCellSettingOffset.setName("newCellAndOnsetButton");
        createNewCellSettingOffset.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/"
                    + "create-new-cell-and-set-onset-selected.png")));
        gridButtonPanel.add(createNewCellSettingOffset, "span 2, w 92!, h 45!");

        // Set new cell offset button
        setNewCellOffsetButton.setAction(actionMap.get("setNewCellStopTime"));
        setNewCellOffsetButton.setIcon(resourceMap.getIcon(
                "setNewCellOnsetButton.icon"));
        setNewCellOffsetButton.setFocusPainted(false);
        setNewCellOffsetButton.setName("newCellOffsetButton");
        setNewCellOffsetButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/set-new-cell-offset-selected.png")));
        gridButtonPanel.add(setNewCellOffsetButton, "w 45!, h 45!");

        // Show tracks button
        showTracksButton.setIcon(resourceMap.getIcon(
                "showTracksButton.show.icon"));
        showTracksButton.setName("showTracksButton");
        showTracksButton.getAccessibleContext().setAccessibleName(
            "Show Tracks");
        showTracksButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent evt) {
                    showTracksButtonActionPerformed(evt);
                }
            });
        gridButtonPanel.add(showTracksButton, "w 80!, h 45!");

        getContentPane().add(gridButtonPanel, "west");

        tracksPanel.setBackground(Color.WHITE);
        tracksPanel.setVisible(false);
        getContentPane().add(tracksPanel, "east, w 800!");

        pack();
    }

    /**
     * Initialize the view for OS other than Macs.
     */
    private void initComponents() {
        gridButtonPanel = new javax.swing.JPanel();
        syncCtrlButton = new javax.swing.JButton();
        syncButton = new javax.swing.JButton();
        setCellOnsetButton = new javax.swing.JButton();
        setCellOffsetButton = new javax.swing.JButton();
        rewindButton = new javax.swing.JButton();
        playButton = new javax.swing.JButton();
        forwardButton = new javax.swing.JButton();
        goBackButton = new javax.swing.JButton();
        shuttleBackButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        shuttleForwardButton = new javax.swing.JButton();
        findButton = new javax.swing.JButton();
        jogBackButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        createNewCellSettingOffset = new javax.swing.JButton();
        jogForwardButton = new javax.swing.JButton();
        setNewCellOffsetButton = new javax.swing.JButton();
        goBackTextField = new javax.swing.JTextField();
        findTextField = new javax.swing.JTextField();
        syncVideoButton = new javax.swing.JButton();
        addDataButton = new javax.swing.JButton();
        timestampLabel = new javax.swing.JLabel();
        topPanel = new javax.swing.JPanel();
        lblSpeed = new javax.swing.JLabel();
        createNewCell = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        findOffsetField = new javax.swing.JTextField();
        showTracksButton = new javax.swing.JButton();
        tracksPanel = new javax.swing.JPanel();

        final int fontSize = 11;

        setLayout(new MigLayout("hidemode 3"));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.jdesktop.application.ResourceMap resourceMap =
            org.jdesktop.application.Application.getInstance(
                org.openshapa.OpenSHAPA.class).getContext().getResourceMap(
                DataControllerV.class);
        setTitle(resourceMap.getString("title"));
        setName("");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(
                    final java.awt.event.WindowEvent evt) {
                    formWindowClosing(evt);
                }
            });

        gridButtonPanel.setBackground(Color.WHITE);
        gridButtonPanel.setLayout(new MigLayout("wrap 5"));

        // Add data button
        addDataButton.setText(resourceMap.getString("addDataButton.text"));
        addDataButton.setFont(new Font("Tahoma", Font.PLAIN, fontSize));
        addDataButton.setFocusPainted(false);
        addDataButton.setName("addDataButton");
        addDataButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent evt) {
                    openVideoButtonActionPerformed(evt);
                }
            });
        gridButtonPanel.add(addDataButton, "span 2, w 90!, h 25!");

        // Timestamp panel
        JPanel timestampPanel = new JPanel(new MigLayout("",
                    "push[][][]0![]push"));
        timestampPanel.setOpaque(false);

        // Timestamp label
        timestampLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        timestampLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timestampLabel.setText("00:00:00:000");
        timestampLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        timestampLabel.setName("timestampLabel");
        timestampPanel.add(timestampLabel);

        jLabel1.setText("@");
        timestampPanel.add(jLabel1);

        lblSpeed.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        lblSpeed.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1,
                2));
        lblSpeed.setName("lblSpeed");
        lblSpeed.setText("0");
        timestampPanel.add(lblSpeed);

        jLabel2.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        jLabel2.setText("x");
        timestampPanel.add(jLabel2);

        gridButtonPanel.add(timestampPanel, "span 3, pushx, growx");

        // Sync control button
        syncCtrlButton.setEnabled(false);
        syncCtrlButton.setFocusPainted(false);
        gridButtonPanel.add(syncCtrlButton, "w 45!, h 45!");

        // Sync button
        syncButton.setEnabled(false);

        // Set cell onset button
        javax.swing.ActionMap actionMap = org.jdesktop.application.Application
            .getInstance(org.openshapa.OpenSHAPA.class).getContext()
            .getActionMap(DataControllerV.class, this);
        setCellOnsetButton.setAction(actionMap.get("setCellOnsetAction"));
        setCellOnsetButton.setIcon(resourceMap.getIcon(
                "setCellOnsetButton.icon"));
        setCellOnsetButton.setFocusPainted(false);
        setCellOnsetButton.setName("setCellOnsetButton");
        setCellOnsetButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/set-cell-onset-selected.png")));
        gridButtonPanel.add(setCellOnsetButton, "w 45!, h 45!");

        // Set cell offset button
        setCellOffsetButton.setAction(actionMap.get("setCellOffsetAction"));
        setCellOffsetButton.setIcon(resourceMap.getIcon(
                "setCellOffsetButton.icon"));
        setCellOffsetButton.setFocusPainted(false);
        setCellOffsetButton.setName("setCellOffsetButton");
        setCellOffsetButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/set-cell-offset-selected.png")));
        gridButtonPanel.add(setCellOffsetButton, "w 45!, h 45!");

        // Go back button
        goBackButton.setAction(actionMap.get("goBackAction"));
        goBackButton.setIcon(resourceMap.getIcon("goBackButton.icon"));
        goBackButton.setFocusPainted(false);
        goBackButton.setName("goBackButton");
        goBackButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/go-back-selected.png")));
        gridButtonPanel.add(goBackButton, "w 45!, h 45!");

        // Sync video button
        syncVideoButton.setEnabled(false);
        syncVideoButton.setFocusPainted(false);
        gridButtonPanel.add(syncVideoButton, "w 80!, h 45!");

        // Rewind video button
        rewindButton.setAction(actionMap.get("rewindAction"));
        rewindButton.setIcon(resourceMap.getIcon("rewindButton.icon"));
        rewindButton.setFocusPainted(false);
        rewindButton.setName("rewindButton");
        rewindButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/rewind-selected.png")));
        gridButtonPanel.add(rewindButton, "w 45!, h 45!");

        // Play video button
        playButton.setAction(actionMap.get("playAction"));
        playButton.setIcon(resourceMap.getIcon("playButton.icon"));
        playButton.setFocusPainted(false);
        playButton.setName("playButton");
        playButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/play-selected.png")));
        playButton.setRequestFocusEnabled(false);
        gridButtonPanel.add(playButton, "w 45!, h 45!");

        // Fast forward button
        forwardButton.setAction(actionMap.get("forwardAction"));
        forwardButton.setIcon(resourceMap.getIcon("forwardButton.icon"));
        forwardButton.setFocusPainted(false);
        forwardButton.setName("forwardButton");
        forwardButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/fast-forward-selected.png")));
        gridButtonPanel.add(forwardButton, "w 45!, h 45!");

        // Find button
        findButton.setAction(actionMap.get("findAction"));
        findButton.setIcon(new ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/find-win.png")));
        findButton.setFocusPainted(false);
        findButton.setName("findButton");
        findButton.setPressedIcon(new ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/find-win-selected.png")));
        gridButtonPanel.add(findButton, "span 1 2, w 45!, h 95!");

        // Go back text field
        goBackTextField.setHorizontalAlignment(SwingConstants.CENTER);
        goBackTextField.setText("00:00:05:000");
        goBackTextField.setName("goBackTextField");
        gridButtonPanel.add(goBackTextField, "w 80!, h 45!");

        // Shuttle back button
        shuttleBackButton.setAction(actionMap.get("shuttleBackAction"));
        shuttleBackButton.setIcon(resourceMap.getIcon(
                "shuttleBackButton.icon"));
        shuttleBackButton.setFocusPainted(false);
        shuttleBackButton.setName("shuttleBackButton");
        shuttleBackButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/shuttle-backward-selected.png")));
        gridButtonPanel.add(shuttleBackButton, "w 45!, h 45!");

        // Stop button
        stopButton.setAction(actionMap.get("stopAction"));
        stopButton.setIcon(resourceMap.getIcon("stopButton.icon"));
        stopButton.setFocusPainted(false);
        stopButton.setName("stopButton");
        stopButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/stop-selected.png")));
        gridButtonPanel.add(stopButton, "w 45!, h 45!");

        // Shuttle forward button
        shuttleForwardButton.setAction(actionMap.get("shuttleForwardAction"));
        shuttleForwardButton.setIcon(resourceMap.getIcon(
                "shuttleForwardButton.icon"));
        shuttleForwardButton.setFocusPainted(false);
        shuttleForwardButton.setName("shuttleForwardButton");
        shuttleForwardButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/shuttle-forward-selected.png")));
        gridButtonPanel.add(shuttleForwardButton, "w 45!, h 45!");

        // Find text field
        findTextField.setHorizontalAlignment(SwingConstants.CENTER);
        findTextField.setText("00:00:00:000");
        findTextField.setName("findOnsetLabel");
        gridButtonPanel.add(findTextField, "w 80!, h 45!");

        // Jog back button
        jogBackButton.setAction(actionMap.get("jogBackAction"));
        jogBackButton.setIcon(resourceMap.getIcon("jogBackButton.icon"));
        jogBackButton.setFocusPainted(false);
        jogBackButton.setName("jogBackButton");
        jogBackButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/jog-backward-selected.png")));
        gridButtonPanel.add(jogBackButton, "w 45!, h 45!");

        // Pause button
        pauseButton.setAction(actionMap.get("pauseAction"));
        pauseButton.setIcon(resourceMap.getIcon("pauseButton.icon"));
        pauseButton.setFocusPainted(false);
        pauseButton.setName("pauseButton");
        pauseButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/pause-selected.png")));
        gridButtonPanel.add(pauseButton, "w 45!, h 45!");

        // Jog forward button
        jogForwardButton.setAction(actionMap.get("jogForwardAction"));
        jogForwardButton.setIcon(resourceMap.getIcon("jogForwardButton.icon"));
        jogForwardButton.setFocusPainted(false);
        jogForwardButton.setName("jogForwardButton");
        jogForwardButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/jog-forward-selected.png")));
        gridButtonPanel.add(jogForwardButton, "w 45!, h 45!");

        // Create new cell button
        createNewCell.setAction(actionMap.get("createCellAction"));
        createNewCell.setIcon(resourceMap.getIcon("createNewCell.icon"));
        createNewCell.setText(resourceMap.getString("createNewCell.text"));
        createNewCell.setAlignmentY(0.0F);
        createNewCell.setFocusPainted(false);
        createNewCell.setHorizontalTextPosition(
            javax.swing.SwingConstants.CENTER);
        createNewCell.setName("createNewCellButton");
        createNewCell.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/create-new-cell-selected.png")));
        gridButtonPanel.add(createNewCell, "span 1 2, w 45!, h 95!");

        // Find offset field
        findOffsetField.setHorizontalAlignment(SwingConstants.CENTER);
        findOffsetField.setText("00:00:00:000");
        findOffsetField.setToolTipText(resourceMap.getString(
                "findOffsetField.toolTipText"));
        findOffsetField.setEnabled(false);
        findOffsetField.setName("findOffsetLabel");
        gridButtonPanel.add(findOffsetField, "w 80!, h 45!");

        // Create new cell setting offset button
        createNewCellSettingOffset.setAction(actionMap.get(
                "createNewCellAction"));
        createNewCellSettingOffset.setIcon(resourceMap.getIcon(
                "createNewCellButton.icon"));
        createNewCellSettingOffset.setFocusPainted(false);
        createNewCellSettingOffset.setName("newCellAndOnsetButton");
        createNewCellSettingOffset.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/"
                    + "create-new-cell-and-set-onset-selected.png")));
        gridButtonPanel.add(createNewCellSettingOffset, "span 2, w 95!, h 45!");

        // Set new cell offset button
        setNewCellOffsetButton.setAction(actionMap.get("setNewCellStopTime"));
        setNewCellOffsetButton.setIcon(resourceMap.getIcon(
                "setNewCellOnsetButton.icon"));
        setNewCellOffsetButton.setFocusPainted(false);
        setNewCellOffsetButton.setName("newCellOffsetButton");
        setNewCellOffsetButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/set-new-cell-offset-selected.png")));
        gridButtonPanel.add(setNewCellOffsetButton, "w 45!, h 45!");

        // Show tracks button
        showTracksButton.setIcon(resourceMap.getIcon(
                "showTracksButton.show.icon"));
        showTracksButton.setName("showTracksButton");
        showTracksButton.getAccessibleContext().setAccessibleName(
            "Show Tracks");
        showTracksButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent evt) {
                    showTracksButtonActionPerformed(evt);
                }
            });
        gridButtonPanel.add(showTracksButton, "w 80!, h 45!");

        getContentPane().add(gridButtonPanel, "west");

        tracksPanel.setBackground(Color.WHITE);
        tracksPanel.setVisible(false);
        getContentPane().add(tracksPanel, "east, w 800!");

        pack();
    }

    /**
     * Action to invoke when the user closes the data controller.
     *
     * @param evt
     *            The event that triggered this action.
     */
    private void formWindowClosing(final java.awt.event.WindowEvent evt) {
        setVisible(false);
    }

    /**
     * Action to invoke when the user clicks on the open button.
     *
     * @param evt
     *            The event that triggered this action.
     */
    private void openVideoButtonActionPerformed(
        final java.awt.event.ActionEvent evt) {
        logger.usage("Add data");

        OpenSHAPAFileChooser jd = new OpenSHAPAFileChooser();
        PluginManager pm = PluginManager.getInstance();

        // Add file filters for each of the supported plugins.
        for (FileFilter f : pm.getPluginFileFilters()) {
            jd.addChoosableFileFilter(f);
        }

        if (JFileChooser.APPROVE_OPTION == jd.showOpenDialog(this)) {
            openVideo(jd);
        }
    }

    /**
     * Action to invoke when the user clicks the show tracks button.
     *
     * @param evt
     *            The event that triggered this action.
     */
    private void showTracksButtonActionPerformed(
        final java.awt.event.ActionEvent evt) {
        assert (evt.getSource() instanceof JButton);

        JButton button = (JButton) evt.getSource();
        ResourceMap resourceMap = Application.getInstance(
                org.openshapa.OpenSHAPA.class).getContext().getResourceMap(
                DataControllerV.class);

        if (tracksPanelEnabled) {
            logger.usage("Show tracks");

            // Panel is being displayed, hide it
            button.setIcon(resourceMap.getIcon("showTracksButton.show.icon"));
        } else {
            logger.usage("Hide tracks");

            // Panel is hidden, show it
            button.setIcon(resourceMap.getIcon("showTracksButton.hide.icon"));
        }

        tracksPanelEnabled = !tracksPanelEnabled;
        showTracksPanel(tracksPanelEnabled);
    }

    /**
     * Adds a data viewer to this data controller.
     *
     * @param icon
     *            The icon associated with the data viewer.
     * @param viewer
     *            The new viewer that we are adding to the data controller.
     * @param f
     *            The parent file that the viewer represents.
     */
    private void addDataViewer(final ImageIcon icon, final DataViewer viewer,
        final File f, final TrackPainter trackPainter) {
        addViewer(viewer, 0);

        addDataViewerToProject(viewer.getClass().getName(),
            f.getAbsolutePath());

        // Add the file to the tracks information panel
        addTrack(icon, f.getAbsolutePath(), f.getName(), viewer.getDuration(),
            viewer.getOffset(), trackPainter);
    }

    /**
     * Returns set of dataviewers.
     *
     * @return set of dataviewers.
     */
    public Set<DataViewer> getDataViewers() {
        return viewers;
    }

    /**
     * Adds a track to the tracks panel.
     *
     * @param icon Icon associated with the track
     * @param mediaPath Absolute file path to the media file.
     * @param name The name of the track to add.
     * @param duration The duration of the data feed in milliseconds.
     * @param offset The time offset of the data feed in milliseconds.
     * @param trackPainter Track painter to use.
     */
    public void addTrack(final ImageIcon icon, final String mediaPath,
        final String name, final long duration, final long offset,
        final TrackPainter trackPainter) {
        mixerControllerV.addNewTrack(icon, mediaPath, name, duration, offset,
            trackPainter);
    }

    /**
     * Add the data viewer to the current project.
     *
     * @param pluginName
     *            Fully qualified plugin class name.
     * @param filePath
     *            Absolute file path to the data feed.
     */
    public void addDataViewerToProject(final String pluginName,
        final String filePath) {
        OpenSHAPA.getProjectController().projectChanged();
        OpenSHAPA.getApplication().updateTitle();
    }

    /**
     * Add a viewer to the data controller with the given offset.
     *
     * @param viewer The data viewer to add.
     * @param offset The offset value in milliseconds.
     */
    public void addViewer(final DataViewer viewer, final long offset) {

        // Add the QTDataViewer to the list of viewers we are controlling.
        viewers.add(viewer);
        viewer.setParentController(this);
        viewer.setOffset(offset);
        OpenSHAPA.getApplication().show(viewer.getParentJFrame());

        // adjust the overall frame rate.
        float fps = viewer.getFrameRate();

        if (fps > playbackModel.getCurrentFPS()) {
            playbackModel.setCurrentFPS(fps);
        }

        // Update track viewer.
        long maxDuration = playbackModel.getMaxDuration();

        if ((viewer.getOffset() + viewer.getDuration()) > maxDuration) {
            maxDuration = viewer.getOffset() + viewer.getDuration();
        }

        playbackModel.setMaxDuration(maxDuration);

        if (playbackModel.getWindowPlayEnd() < maxDuration) {
            playbackModel.setWindowPlayEnd(maxDuration);
            mixerControllerV.setPlayRegionEnd(maxDuration);
        }
    }

    /**
     * Action to invoke when the user clicks the set cell onset button.
     */
    @Action public void setCellOnsetAction() {
        logger.usage("Set cell onset");
        new SetSelectedCellStartTimeC(getCurrentTime());
    }

    /**
     * Action to invoke when the user clicks on the set cell offest button.
     */
    @Action public void setCellOffsetAction() {
        logger.usage("Set cell offset");
        new SetSelectedCellStopTimeC(getCurrentTime());
        setFindOffsetField(getCurrentTime());
    }

    /**
     * @param show
     *            true to show the tracks layout, false otherwise.
     */
    public void showTracksPanel(final boolean show) {
        tracksPanel.setVisible(show);
        tracksPanel.repaint();
        pack();
        validate();
    }

    /**
     * Handler for a TracksControllerEvent.
     *
     * @param e event
     */
    public void tracksControllerChanged(final TracksControllerEvent e) {

        switch (e.getTracksEvent()) {

        case NEEDLE_EVENT:
            handleNeedleEvent((NeedleEvent) e.getEventObject());

            break;

        case MARKER_EVENT:
            handleMarkerEvent((MarkerEvent) e.getEventObject());

            break;

        case CARRIAGE_EVENT:
            handleCarriageEvent((CarriageEvent) e.getEventObject());

            break;

        case TIMESCALE_EVENT:
            handleTimescaleEvent((TimescaleEvent) e.getEventObject());

            break;

        default:
            break;
        }
    }

    /**
     * Handles a NeedleEvent (when the timing needle changes due to user
     * interaction).
     *
     * @param e
     *            The Needle event that triggered this action.
     */
    private void handleNeedleEvent(final NeedleEvent e) {
        gotoTime(e.getTime());
    }

    /**
     * Handles a TimescaleEvent.
     * @param e The timescale event that triggered this action.
     */
    private void handleTimescaleEvent(final TimescaleEvent e) {
        gotoTime(e.getTime());
    }

    private void gotoTime(final long time) {
        long newTime = time;

        if (newTime < playbackModel.getWindowPlayStart()) {
            newTime = playbackModel.getWindowPlayStart();
        }

        if (newTime > playbackModel.getWindowPlayEnd()) {
            newTime = playbackModel.getWindowPlayEnd();
        }

        clockStop(newTime);
        clockStep(newTime);
        setCurrentTime(newTime);
        clock.setTime(newTime);
    }

    /**
     * Handles a MarkerEvent (when one of the region marker changes due to user
     * interaction).
     *
     * @param e
     *            The Marker Event that triggered this action.
     */
    private void handleMarkerEvent(final MarkerEvent e) {
        final long newWindowTime = e.getTime();
        final long tracksTime = mixerControllerV.getCurrentTime();

        switch (e.getMarker()) {

        case START_MARKER:
            handleStartMarkerEvent(newWindowTime, tracksTime);

            break;

        case END_MARKER:
            handleEndMarkerEvent(newWindowTime, tracksTime);

            break;

        default:
            throw new IllegalArgumentException("Unknown marker event.");
        }
    }

    /**
     * Helper method for handling the end region marker event.
     *
     * @param newWindowTime New region marker time.
     * @param tracksTime Current time.
     */
    private void handleEndMarkerEvent(final long newWindowTime,
        final long tracksTime) {

        final long maxDuration = playbackModel.getMaxDuration();
        long windowPlayEnd;

        if ((newWindowTime <= maxDuration)
                && (newWindowTime > playbackModel.getWindowPlayStart())) {
            windowPlayEnd = newWindowTime;
        } else if (newWindowTime > maxDuration) {
            windowPlayEnd = maxDuration;
        } else {
            windowPlayEnd = playbackModel.getWindowPlayStart();
        }

        playbackModel.setWindowPlayEnd(windowPlayEnd);

        mixerControllerV.setPlayRegionEnd(windowPlayEnd);

        if (tracksTime > windowPlayEnd) {
            mixerControllerV.setCurrentTime(windowPlayEnd);
            clock.setTime(windowPlayEnd);
            clockStep(windowPlayEnd);
        }
    }

    /**
     * Helper method for handling the start region marker event.
     *
     * @param newWindowTime New region marker time.
     * @param tracksTime Current time.
     */
    private void handleStartMarkerEvent(final long newWindowTime,
        final long tracksTime) {
        final long windowPlayEnd = playbackModel.getWindowPlayEnd();
        long windowPlayStart;

        if ((newWindowTime < playbackModel.getMaxDuration())
                && (newWindowTime < windowPlayEnd)) {
            windowPlayStart = newWindowTime;
        } else if (newWindowTime >= windowPlayEnd) {
            windowPlayStart = windowPlayEnd;
        } else {
            windowPlayStart = playbackModel.getMaxDuration();
        }

        playbackModel.setWindowPlayStart(windowPlayStart);

        mixerControllerV.setPlayRegionStart(windowPlayStart);

        if (tracksTime < windowPlayStart) {
            mixerControllerV.setCurrentTime(windowPlayStart);
            clock.setTime(windowPlayStart);
            clockStep(windowPlayStart);
        }
    }

    /**
     * Handles a CarriageEvent (when the carriage moves due to user
     * interaction).
     *
     * @param e
     *            The carriage event that triggered this action.
     */
    private void handleCarriageEvent(final CarriageEvent e) {

        switch (e.getEventType()) {

        case OFFSET_CHANGE:
            handleCarriageOffsetChangeEvent(e);

            break;

        case CARRIAGE_LOCK:
        case BOOKMARK_CHANGED:
        case BOOKMARK_SAVE:
            OpenSHAPA.getProjectController().projectChanged();
            OpenSHAPA.getApplication().updateTitle();

            break;

        default:
            throw new IllegalArgumentException("Unknown carriage event.");
        }
    }

    /**
     * @param e
     */
    private void handleCarriageOffsetChangeEvent(final CarriageEvent e) {

        // Look through our data viewers and update the offset
        for (DataViewer viewer : viewers) {
            File feed = viewer.getDataFeed();

            /*
             * Found our data viewer, update the DV offset and the settings
             * in the project file.
             */
            if (feed.getAbsolutePath().equals(e.getTrackId())) {
                viewer.setOffset(e.getOffset());
            }
        }

        OpenSHAPA.getProjectController().projectChanged();
        OpenSHAPA.getApplication().updateTitle();

        // Recalculate the maximum playback duration.
        long maxDuration = 0;

        for (DataViewer viewer : viewers) {

            if ((viewer.getDuration() + viewer.getOffset()) > maxDuration) {
                maxDuration = viewer.getDuration() + viewer.getOffset();
            }
        }

        playbackModel.setMaxDuration(maxDuration);

        mixerControllerV.setMaxEnd(maxDuration);

        // Reset our playback windows
        if (playbackModel.getWindowPlayEnd() > maxDuration) {
            playbackModel.setWindowPlayEnd(maxDuration);
            mixerControllerV.setPlayRegionEnd(maxDuration);
        }


        if (playbackModel.getWindowPlayStart()
                > playbackModel.getWindowPlayEnd()) {
            playbackModel.setWindowPlayStart(0);
            mixerControllerV.setPlayRegionStart(
                playbackModel.getWindowPlayStart());
        }

        // Reset the time if needed
        long tracksTime = mixerControllerV.getCurrentTime();

        if (tracksTime < playbackModel.getWindowPlayStart()) {
            tracksTime = playbackModel.getWindowPlayStart();
        }

        if (tracksTime > playbackModel.getWindowPlayEnd()) {
            tracksTime = playbackModel.getWindowPlayEnd();
        }

        mixerControllerV.setCurrentTime(tracksTime);

        clock.setTime(tracksTime);
        clockStep(tracksTime);
    }

    // -------------------------------------------------------------------------
    // Simulated clicks (for numpad calls)
    //

    /** Simulates play button clicked. */
    public void pressPlay() {
        playButton.doClick();
    }

    /** Simulates forward button clicked. */
    public void pressForward() {
        forwardButton.doClick();
    }

    /** Simulates rewind button clicked. */
    public void pressRewind() {
        rewindButton.doClick();
    }

    /** Simulates pause button clicked. */
    public void pressPause() {
        pauseButton.doClick();
    }

    /** Simulates stop button clicked. */
    public void pressStop() {
        stopButton.doClick();
    }

    /** Simulates shuttle forward button clicked. */
    public void pressShuttleForward() {
        shuttleForwardButton.doClick();
    }

    /** Simulates shuttle back button clicked. */
    public void pressShuttleBack() {
        shuttleBackButton.doClick();
    }

    /** Simulates find button clicked. */
    public void pressFind() {
        findButton.doClick();
    }

    /** Simulates set cell onset button clicked. */
    public void pressSetCellOnset() {
        setCellOnsetButton.doClick();
    }

    /** Simulates set cell offset button clicked. */
    public void pressSetCellOffset() {
        setCellOffsetButton.doClick();
    }

    /** Simulates set new cell onset button clicked. */
    public void pressSetNewCellOnset() {
        setNewCellOffsetButton.doClick();
    }

    /** Simulates go back button clicked. */
    public void pressGoBack() {
        goBackButton.doClick();
    }

    /** Simulates create new cell button clicked. */
    public void pressCreateNewCell() {
        createNewCell.doClick();
    }

    /** Simulates create new cell setting offset button clicked. */
    public void pressCreateNewCellSettingOffset() {
        createNewCellSettingOffset.doClick();
    }

    /** Simulates sync button clicked. */
    public void pressSyncButton() {
        syncButton.doClick();
    }

    /** Simulates sync button clicked. */
    public void pressSyncCtrlButton() {
        syncCtrlButton.doClick();
    }

    /** Simulates sync button clicked. */
    public void pressSyncVideoButton() {
        syncVideoButton.doClick();
    }

    // ------------------------------------------------------------------------
    // Playback actions
    //
    /**
     * Action to invoke when the user clicks on the play button.
     */
    @Action public void playAction() {
        logger.usage("Play");

        // BugzID:464 - When stopped at the end of the region of interest.
        // pressing play jumps the stream back to the start of the video before
        // starting to play again.
        if ((getCurrentTime() >= playbackModel.getWindowPlayEnd())
                && clock.isStopped()) {
            jumpTo(playbackModel.getWindowPlayStart());
        }

        playAt(PLAY_RATE);
    }

    /**
     * Action to invoke when the user clicks on the fast foward button.
     */
    @Action public void forwardAction() {
        logger.usage("Fast forward");
        playAt(FFORWARD_RATE);
    }

    /**
     * Action to invoke when the user clicks on the rewind button.
     */
    @Action public void rewindAction() {
        logger.usage("Rewind");
        playAt(REWIND_RATE);
    }

    /**
     * Action to invoke when the user clicks on the pause button.
     */
    @Action public void pauseAction() {
        logger.usage("Pause");

        // Resume from pause at playback rate prior to pause.
        if (clock.isStopped()) {
            shuttleAt(playbackModel.getPauseRate());

            // Pause views - store current playback rate.
        } else {
            playbackModel.setPauseRate(clock.getRate());
            clock.stop();
            lblSpeed.setText("["
                + FloatUtils.doubleToFractionStr(
                    Double.valueOf(playbackModel.getPauseRate())) + "]");
        }
    }

    /**
     * Action to invoke when the user clicks on the stop button.
     */
    @Action public void stopAction() {
        logger.usage("Stop event");
        clock.stop();
        clock.setRate(0);
        playbackModel.setShuttleRate(0);
        playbackModel.setPauseRate(0);
        shuttleDirection = ShuttleDirection.UNDEFINED;
    }

    /**
     * Action to invoke when the user clicks on the shuttle forward button.
     *
     * @todo proper behaviour for reversing shuttle direction?
     */
    @Action public void shuttleForwardAction() {
        logger.usage("Shuttle forward");

        if ((clock.getTime() <= 0)
                && ((playbackModel.getShuttleRate() != 0)
                    || (shuttleDirection != ShuttleDirection.UNDEFINED))) {
            playbackModel.setShuttleRate(0);
            playbackModel.setPauseRate(0);
            shuttleDirection = ShuttleDirection.UNDEFINED;
            shuttle(ShuttleDirection.FORWARDS);
        } else {

            // BugzID:794 - Previously ignored pauseRate if paused
            if (clock.isStopped()) {
                playbackModel.setShuttleRate(findShuttleIndex(
                        playbackModel.getPauseRate()));
                shuttle(ShuttleDirection.FORWARDS);
                // shuttle(ShuttleDirection.BACKWARDS);
                // This makes current tests fail, but may be the desired
                // functionality.
            } else {
                shuttle(ShuttleDirection.FORWARDS);
            }
        }
    }

    /**
     * Action to invoke when the user clicks on the shuttle back button.
     */
    @Action public void shuttleBackAction() {
        logger.usage("Shuttle back");

        if ((clock.getTime() <= 0)
                && ((playbackModel.getShuttleRate() != 0)
                    || (shuttleDirection != ShuttleDirection.UNDEFINED))) {
            playbackModel.setShuttleRate(0);
            playbackModel.setPauseRate(0);
            shuttleDirection = ShuttleDirection.UNDEFINED;
        } else {

            // BugzID:794 - Previously ignored pauseRate if paused
            if (clock.isStopped()) {
                playbackModel.setShuttleRate(findShuttleIndex(
                        playbackModel.getPauseRate()));
                shuttle(ShuttleDirection.BACKWARDS);
                // shuttle(ShuttleDirection.FORWARDS);
                // This makes current tests fail, but may be the desired
                // functionality.
            } else {
                shuttle(ShuttleDirection.BACKWARDS);
            }
        }
    }

    /**
     * Searches the shuttle rates array for the given rate, and returns the
     * index.
     *
     * @param pRate
     *            The rate to search for.
     * @return The index of the rate, or -1 if not found.
     */
    private int findShuttleIndex(final float pRate) {

        if (pRate == 0) {
            return 0;
        }

        for (int i = 0; i < SHUTTLE_RATES.length; i++) {

            if ((SHUTTLE_RATES[i] == pRate)
                    || (SHUTTLE_RATES[i] == (pRate * (-1)))) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Populates the find time in the controller.
     *
     * @param milliseconds
     *            The time to use when populating the find field.
     */
    public void setFindTime(final long milliseconds) {
        findTextField.setText(CLOCK_FORMAT.format(milliseconds));
    }

    /**
     * Populates the find offset time in the controller.
     *
     * @param milliseconds
     *            The time to use when populating the find field.
     */
    public void setFindOffsetField(final long milliseconds) {
        findOffsetField.setText(CLOCK_FORMAT.format(milliseconds));
    }

    /**
     * Action to invoke when the user clicks on the find button.
     */
    @Action public void findAction() {
        logger.usage("Find");

        if (shiftMask) {
            findOffsetAction();
        } else {

            try {
                jumpTo(CLOCK_FORMAT.parse(findTextField.getText()).getTime());
            } catch (ParseException e) {
                logger.error("unable to find within video", e);
            }
        }
    }

    /**
     * Action to invoke when the user holds shift down.
     */
    public void findOffsetAction() {

        try {
            jumpTo(CLOCK_FORMAT.parse(findOffsetField.getText()).getTime());
        } catch (ParseException e) {
            logger.error("unable to find within video", e);
        }
    }

    /**
     * Sets the playback region of interest to lie from the find time to offset
     * time.
     */
    public void setRegionOfInterestAction() {

        try {
            final long newWindowPlayStart = CLOCK_FORMAT.parse(
                    findTextField.getText()).getTime();
            final long newWindowPlayEnd = CLOCK_FORMAT.parse(
                    findOffsetField.getText()).getTime();

            playbackModel.setWindowPlayStart(newWindowPlayStart);
            mixerControllerV.setPlayRegionStart(newWindowPlayStart);

            if (newWindowPlayStart < newWindowPlayEnd) {
                playbackModel.setWindowPlayEnd(newWindowPlayEnd);
                mixerControllerV.setPlayRegionEnd(newWindowPlayEnd);
            } else {
                playbackModel.setWindowPlayEnd(newWindowPlayStart);
                mixerControllerV.setPlayRegionEnd(newWindowPlayStart);
            }

            final long currentTime = mixerControllerV.getCurrentTime();

            if (currentTime > newWindowPlayEnd) {
                mixerControllerV.setCurrentTime(newWindowPlayEnd);
                clock.setTime(newWindowPlayEnd);
            } else if (currentTime < newWindowPlayStart) {
                mixerControllerV.setCurrentTime(newWindowPlayStart);
                clock.setTime(newWindowPlayStart);
            }
        } catch (ParseException e) {
            logger.error("Unable to set playback region of interest", e);
        }
    }

    /**
     * Action to invoke when the user clicks on the go back button.
     */
    @Action public void goBackAction() {

        try {
            logger.usage("Go back");

            long j = -CLOCK_FORMAT.parse(goBackTextField.getText()).getTime();
            jump(j);

            // BugzID:721 - After going back - start playing again.
            playAt(PLAY_RATE);

        } catch (ParseException e) {
            logger.error("unable to find within video", e);
        }
    }

    /**
     * Action to invoke when the user clicks on the jog backwards button.
     */
    @Action public void jogBackAction() {
        logger.usage("Jog back");

        int mul = 1;

        if (shiftMask) {
            mul = SHIFTJOG;
        }

        if (ctrlMask) {
            mul = CTRLSHIFTJOG;
        }

        /* Bug1361: Do not allow jog to skip past the region boundaries. */
        long nextTime = (long) (mul * (-ONE_SECOND)
                / playbackModel.getCurrentFPS());

        if ((clock.getTime() + nextTime) > playbackModel.getWindowPlayStart()) {
            jump(nextTime);
        } else {
            jumpTo(playbackModel.getWindowPlayStart());
        }
    }

    /**
     * Action to invoke when the user clicks on the jog forwards button.
     */
    @Action public void jogForwardAction() {
        logger.usage("Jog forward");

        int mul = 1;

        if (shiftMask) {
            mul = SHIFTJOG;
        }

        if (ctrlMask) {
            mul = CTRLSHIFTJOG;
        }

        /* Bug1361: Do not allow jog to skip past the region boundaries. */
        long nextTime = (long) (mul * (ONE_SECOND)
                / playbackModel.getCurrentFPS());

        if ((clock.getTime() + nextTime) < playbackModel.getWindowPlayEnd()) {
            jump(nextTime);
        } else {
            jumpTo(playbackModel.getWindowPlayEnd());
        }
    }

    // ------------------------------------------------------------------------
    // [private] play back action helper functions
    //
    /**
     * @param rate
     *            Rate of play.
     */
    private void playAt(final float rate) {
        shuttleDirection = ShuttleDirection.UNDEFINED;
        playbackModel.setShuttleRate(0);
        playbackModel.setPauseRate(0);
        shuttleAt(rate);
    }

    /**
     * @param direction
     *            The required direction of the shuttle.
     */
    private void shuttle(final ShuttleDirection direction) {
        int shuttleRate = playbackModel.getShuttleRate();
        float rate = SHUTTLE_RATES[shuttleRate];

        if (ShuttleDirection.UNDEFINED == shuttleDirection) {
            shuttleDirection = direction;
            rate = SHUTTLE_RATES[0];

        } else if (direction == shuttleDirection) {

            if (shuttleRate < (SHUTTLE_RATES.length - 1)) {
                rate = SHUTTLE_RATES[++shuttleRate];
                playbackModel.setShuttleRate(shuttleRate);
            }

        } else {

            if (shuttleRate > 0) {
                rate = SHUTTLE_RATES[--shuttleRate];
                playbackModel.setShuttleRate(shuttleRate);

                // BugzID: 676 - Shuttle speed transitions between zero.
            } else {
                rate = 0;
                shuttleDirection = ShuttleDirection.UNDEFINED;
            }
        }

        shuttleAt(shuttleDirection.getParameter() * rate);
    }

    /**
     * @param rate
     *            Rate of shuttle.
     */
    private void shuttleAt(final float rate) {
        clock.setRate(rate);
        clock.start();
    }

    /**
     * @param step
     *            Milliseconds to jump.
     */
    private void jump(final long step) {
        clock.stop();
        clock.setRate(0);
        playbackModel.setShuttleRate(0);
        playbackModel.setPauseRate(0);
        shuttleDirection = ShuttleDirection.UNDEFINED;
        clock.stepTime(step);
    }

    /**
     * @param time
     *            Absolute time to jump to.
     */
    private void jumpTo(final long time) {

        clock.stop();
        clock.setTime(time);
    }

    // -------------------------------------------------------------------------
    //
    //
    /**
     * Action to invoke when the user clicks on the create new cell button.
     */
    @Action public void createCellAction() {
        logger.usage("New cell");
        new CreateNewCellC();
    }

    /**
     * Action to invoke when the user clicks on the new cell button.
     */
    @Action public void createNewCellAction() {
        logger.usage("New cell set onset");
        new CreateNewCellC(getCurrentTime());
    }

    /**
     * Action to invoke when the user clicks on the new cell onset button.
     */
    @Action public void setNewCellStopTime() {
        logger.usage("Set new cell offset");
        new SetNewCellStopTimeC(getCurrentTime());
        setFindOffsetField(getCurrentTime());
    }

    /**
     * Action to invoke when the user clicks on the sync video button.
     */
    @Action public void syncVideoAction() {
    }
}
