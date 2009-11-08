package org.openshapa.views;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SimpleTimeZone;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.openshapa.OpenSHAPA;
import org.openshapa.controllers.CreateNewCellC;
import org.openshapa.controllers.SetNewCellStopTimeC;
import org.openshapa.controllers.SetSelectedCellStartTimeC;
import org.openshapa.controllers.SetSelectedCellStopTimeC;
import org.openshapa.util.FloatUtils;
import org.openshapa.util.ClockTimer;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.PluginManager;

/**
 * Quicktime video controller.
 */
public final class DataController
        extends OpenSHAPADialog
        implements org.openshapa.util.ClockTimer.Listener {

    //--------------------------------------------------------------------------
    // [static]
    //

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(DataController.class);

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

    // Initialize SHUTTLE_RATES
    // values: [ (2^-5), ..., (2^0), ..., (2^5) ]
    static {
        int POWER = 5;
        SHUTTLE_RATES = new float[2 * POWER + 1];
        float value = 1;
        SHUTTLE_RATES[POWER] = value;
        for (int i = 1; i <= POWER; ++i) {
            value *= 2;
            SHUTTLE_RATES[POWER + i] = value;
            SHUTTLE_RATES[POWER - i] = 1F / value;
        }
    }

    /**
     * Enumeration of shuttle directions.
     */
    enum ShuttleDirection {
        BACKWARDS(-1),
        UNDEFINED(0),
        FORWARDS(1);

        private int parameter;

        ShuttleDirection(final int p) { this.parameter = p; }

        public int getParameter() { return parameter; }
    }

    /** Format for representing time. */
    private static final DateFormat CLOCK_FORMAT;

    // initialize standard date format for clock display.
    static {
        CLOCK_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");
        CLOCK_FORMAT.setTimeZone(new SimpleTimeZone(0, "NO_ZONE"));
    }


    //--------------------------------------------------------------------------
    //
    //

    /** The list of viewers associated with this controller. */
    private Set<DataViewer> viewers;

    /** Stores the highest frame rate for all available viewers. */
    private float currentFPS = 1F;

    /** Shuttle status flag. */
    private ShuttleDirection shuttleDirection = ShuttleDirection.UNDEFINED;

    /** Index of current shuttle rate. */
    private int shuttleRate;

    /** The rate to use when resumed from pause. */
    private float pauseRate;

    /** The time the last sync was performed. */
    private long lastSync;

    /** Clock timer. */
    private ClockTimer clock = new ClockTimer();


    //--------------------------------------------------------------------------
    // [initialization]
    //

    /**
     * Constructor. Creates a new DataController.
     *
     * @param parent The parent of this form.
     * @param modal Should the dialog be modal or not?
     */
    public DataController(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);

        clock.registerListener(this);

        initComponents();
        setName(this.getClass().getSimpleName());
        viewers = new HashSet<DataViewer>();
        pauseRate = 0;
        lastSync = 0;
    }

    //--------------------------------------------------------------------------
    // [interface] org.openshapa.util.ClockTimer.Listener
    //

    /**
     * @param time Current clock time in milliseconds.
     */
    public void clockStart(final long time) {
        setCurrentTime(time);
        for (DataViewer viewer : viewers) {
            viewer.seekTo(time);
            viewer.play();
        }
    }

    /**
     * @param time Current clock time in milliseconds.
     */
    public void clockTick(final long time) {
        setCurrentTime(time);

        if (time - this.lastSync > 500) {
            for (DataViewer viewer : viewers) {
                viewer.seekTo(time);
            }

            lastSync = time;
        }
    }

    /**
     * @param time Current clock time in milliseconds.
     */
    public void clockStop(final long time) {
        setCurrentTime(time);
        for (DataViewer viewer : viewers) {
            viewer.stop();
            viewer.seekTo(time);
        }
    }

    /**
     * @param rate Current (updated) clock rate.
     */
    public void clockRate(final float rate) {
        lblSpeed.setText(FloatUtils.doubleToFractionStr(new Double(rate)));
        for (DataViewer viewer : viewers) {
            viewer.setPlaybackSpeed(rate);
            if (!clock.isStopped()) {
                viewer.play();
            }
        }
    }

    /**
     * @param time Current clock time in milliseconds.
     */
    public void clockStep(final long time) {
        setCurrentTime(time);
        for (DataViewer viewer : viewers) { viewer.seekTo(time); }
    }

    //--------------------------------------------------------------------------
    //
    //

    /**
     * Set time location for data streams.
     *
     * @param milliseconds The millisecond time.
     */
    public void setCurrentTime(final long milliseconds) {
        timestampLabel.setText(CLOCK_FORMAT.format(milliseconds));
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
     * Remove the specifed viewer form the controller.
     *
     * @param viewer The viewer to shutdown.
     * @return True if the controller contained this viewer.
     */
    public boolean shutdown(final DataViewer viewer) {
        return viewers.remove(viewer);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

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
        createNewCellButton = new javax.swing.JButton();
        jogForwardButton = new javax.swing.JButton();
        setNewCellOnsetButton = new javax.swing.JButton();
        goBackTextField = new javax.swing.JTextField();
        findTextField = new javax.swing.JTextField();
        syncVideoButton = new javax.swing.JButton();
        openVideoButton = new javax.swing.JButton();
        timestampLabel = new javax.swing.JLabel();
        topPanel = new javax.swing.JPanel();
        lblSpeed = new javax.swing.JLabel();
        createNewCell = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(org.openshapa.OpenSHAPA.class).getContext().getResourceMap(DataController.class);
        setTitle(resourceMap.getString("title")); // NOI18N
        setName(""); // NOI18N
        setResizable(false);

        gridButtonPanel.setBackground(new java.awt.Color(255, 255, 255));
        gridButtonPanel.setLayout(new java.awt.GridBagLayout());

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(org.openshapa.OpenSHAPA.class).getContext().getActionMap(DataController.class, this);
        syncCtrlButton.setAction(actionMap.get("syncCtrlAction")); // NOI18N
        syncCtrlButton.setMaximumSize(new java.awt.Dimension(45, 45));
        syncCtrlButton.setMinimumSize(new java.awt.Dimension(45, 45));
        syncCtrlButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(syncCtrlButton, gridBagConstraints);

        syncButton.setAction(actionMap.get("syncAction")); // NOI18N
        syncButton.setMaximumSize(new java.awt.Dimension(45, 45));
        syncButton.setMinimumSize(new java.awt.Dimension(45, 45));
        syncButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(syncButton, gridBagConstraints);

        setCellOnsetButton.setAction(actionMap.get("setCellOnsetAction")); // NOI18N
        setCellOnsetButton.setIcon(resourceMap.getIcon("setCellOnsetButton.icon")); // NOI18N
        setCellOnsetButton.setMaximumSize(new java.awt.Dimension(45, 45));
        setCellOnsetButton.setMinimumSize(new java.awt.Dimension(45, 45));
        setCellOnsetButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(setCellOnsetButton, gridBagConstraints);

        setCellOffsetButton.setAction(actionMap.get("setCellOffsetAction")); // NOI18N
        setCellOffsetButton.setIcon(resourceMap.getIcon("setCellOffsetButton.icon")); // NOI18N
        setCellOffsetButton.setMaximumSize(new java.awt.Dimension(45, 45));
        setCellOffsetButton.setMinimumSize(new java.awt.Dimension(45, 45));
        setCellOffsetButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 1);
        gridButtonPanel.add(setCellOffsetButton, gridBagConstraints);

        rewindButton.setAction(actionMap.get("rewindAction")); // NOI18N
        rewindButton.setIcon(resourceMap.getIcon("rewindButton.icon")); // NOI18N
        rewindButton.setMaximumSize(new java.awt.Dimension(45, 45));
        rewindButton.setMinimumSize(new java.awt.Dimension(45, 45));
        rewindButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(rewindButton, gridBagConstraints);

        playButton.setAction(actionMap.get("playAction")); // NOI18N
        playButton.setIcon(resourceMap.getIcon("playButton.icon")); // NOI18N
        playButton.setMaximumSize(new java.awt.Dimension(45, 45));
        playButton.setMinimumSize(new java.awt.Dimension(45, 45));
        playButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(playButton, gridBagConstraints);

        forwardButton.setAction(actionMap.get("forwardAction")); // NOI18N
        forwardButton.setIcon(resourceMap.getIcon("forwardButton.icon")); // NOI18N
        forwardButton.setMaximumSize(new java.awt.Dimension(45, 45));
        forwardButton.setMinimumSize(new java.awt.Dimension(45, 45));
        forwardButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(forwardButton, gridBagConstraints);

        goBackButton.setAction(actionMap.get("goBackAction")); // NOI18N
        goBackButton.setIcon(resourceMap.getIcon("goBackButton.icon")); // NOI18N
        goBackButton.setMaximumSize(new java.awt.Dimension(45, 45));
        goBackButton.setMinimumSize(new java.awt.Dimension(45, 45));
        goBackButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(goBackButton, gridBagConstraints);

        shuttleBackButton.setAction(actionMap.get("shuttleBackAction")); // NOI18N
        shuttleBackButton.setIcon(resourceMap.getIcon("shuttleBackButton.icon")); // NOI18N
        shuttleBackButton.setMaximumSize(new java.awt.Dimension(45, 45));
        shuttleBackButton.setMinimumSize(new java.awt.Dimension(45, 45));
        shuttleBackButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(shuttleBackButton, gridBagConstraints);

        pauseButton.setAction(actionMap.get("pauseAction")); // NOI18N
        pauseButton.setIcon(resourceMap.getIcon("pauseButton.icon")); // NOI18N
        pauseButton.setMaximumSize(new java.awt.Dimension(45, 45));
        pauseButton.setMinimumSize(new java.awt.Dimension(45, 45));
        pauseButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(pauseButton, gridBagConstraints);

        shuttleForwardButton.setAction(actionMap.get("shuttleForwardAction")); // NOI18N
        shuttleForwardButton.setIcon(resourceMap.getIcon("shuttleForwardButton.icon")); // NOI18N
        shuttleForwardButton.setMaximumSize(new java.awt.Dimension(45, 45));
        shuttleForwardButton.setMinimumSize(new java.awt.Dimension(45, 45));
        shuttleForwardButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(shuttleForwardButton, gridBagConstraints);

        findButton.setAction(actionMap.get("findAction")); // NOI18N
        findButton.setIcon(resourceMap.getIcon("findButton.icon")); // NOI18N
        findButton.setMaximumSize(new java.awt.Dimension(45, 45));
        findButton.setMinimumSize(new java.awt.Dimension(45, 45));
        findButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(findButton, gridBagConstraints);

        jogBackButton.setAction(actionMap.get("jogBackAction")); // NOI18N
        jogBackButton.setIcon(resourceMap.getIcon("jogBackButton.icon")); // NOI18N
        jogBackButton.setMaximumSize(new java.awt.Dimension(45, 45));
        jogBackButton.setMinimumSize(new java.awt.Dimension(45, 45));
        jogBackButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(jogBackButton, gridBagConstraints);

        stopButton.setAction(actionMap.get("stopAction")); // NOI18N
        stopButton.setIcon(resourceMap.getIcon("stopButton.icon")); // NOI18N
        stopButton.setMaximumSize(new java.awt.Dimension(45, 45));
        stopButton.setMinimumSize(new java.awt.Dimension(45, 45));
        stopButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(stopButton, gridBagConstraints);

        createNewCellButton.setAction(actionMap.get("createNewCellAction")); // NOI18N
        createNewCellButton.setIcon(resourceMap.getIcon("createNewCellButton.icon")); // NOI18N
        createNewCellButton.setMaximumSize(new java.awt.Dimension(90, 45));
        createNewCellButton.setMinimumSize(new java.awt.Dimension(90, 45));
        createNewCellButton.setPreferredSize(new java.awt.Dimension(90, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(createNewCellButton, gridBagConstraints);

        jogForwardButton.setAction(actionMap.get("jogForwardAction")); // NOI18N
        jogForwardButton.setIcon(resourceMap.getIcon("jogForwardButton.icon")); // NOI18N
        jogForwardButton.setMaximumSize(new java.awt.Dimension(45, 45));
        jogForwardButton.setMinimumSize(new java.awt.Dimension(45, 45));
        jogForwardButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(jogForwardButton, gridBagConstraints);

        setNewCellOnsetButton.setAction(actionMap.get("setNewCellStopTime")); // NOI18N
        setNewCellOnsetButton.setIcon(resourceMap.getIcon("setNewCellOnsetButton.icon")); // NOI18N
        setNewCellOnsetButton.setMaximumSize(new java.awt.Dimension(45, 45));
        setNewCellOnsetButton.setMinimumSize(new java.awt.Dimension(45, 45));
        setNewCellOnsetButton.setPreferredSize(new java.awt.Dimension(45, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(setNewCellOnsetButton, gridBagConstraints);

        goBackTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        goBackTextField.setText("00:00:05:000");
        goBackTextField.setMaximumSize(new java.awt.Dimension(80, 45));
        goBackTextField.setMinimumSize(new java.awt.Dimension(80, 45));
        goBackTextField.setPreferredSize(new java.awt.Dimension(80, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(goBackTextField, gridBagConstraints);

        findTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        findTextField.setText("00:00:00:000");
        findTextField.setMaximumSize(new java.awt.Dimension(80, 45));
        findTextField.setMinimumSize(new java.awt.Dimension(80, 45));
        findTextField.setPreferredSize(new java.awt.Dimension(80, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(findTextField, gridBagConstraints);

        syncVideoButton.setEnabled(false);
        syncVideoButton.setMaximumSize(new java.awt.Dimension(80, 45));
        syncVideoButton.setMinimumSize(new java.awt.Dimension(80, 45));
        syncVideoButton.setPreferredSize(new java.awt.Dimension(80, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(syncVideoButton, gridBagConstraints);

        openVideoButton.setText(resourceMap.getString("openVideoButton.text")); // NOI18N
        openVideoButton.setMaximumSize(new java.awt.Dimension(90, 25));
        openVideoButton.setMinimumSize(new java.awt.Dimension(90, 25));
        openVideoButton.setPreferredSize(new java.awt.Dimension(90, 25));
        openVideoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openVideoButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(openVideoButton, gridBagConstraints);

        timestampLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        timestampLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timestampLabel.setText("00:00:00:000");
        timestampLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(timestampLabel, gridBagConstraints);

        topPanel.setBackground(java.awt.Color.white);
        topPanel.setLayout(new java.awt.BorderLayout());

        lblSpeed.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSpeed.setText("0");
        lblSpeed.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 2));
        topPanel.add(lblSpeed, java.awt.BorderLayout.LINE_END);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        gridButtonPanel.add(topPanel, gridBagConstraints);

        createNewCell.setAction(actionMap.get("createCellAction")); // NOI18N
        createNewCell.setIcon(resourceMap.getIcon("createNewCell.icon")); // NOI18N
        createNewCell.setAlignmentY(0.0F);
        createNewCell.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        createNewCell.setMaximumSize(new java.awt.Dimension(45, 90));
        createNewCell.setMinimumSize(new java.awt.Dimension(45, 90));
        createNewCell.setPreferredSize(new java.awt.Dimension(45, 90));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridButtonPanel.add(createNewCell, gridBagConstraints);

        jLabel1.setText("@");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        gridButtonPanel.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("x");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridButtonPanel.add(jLabel2, gridBagConstraints);

        getContentPane().add(gridButtonPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Action to invoke when the user clicks on the open button.
     *
     * @param evt The event that triggered this action.
     */
    private void openVideoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openVideoButtonActionPerformed
        JFileChooser jd = new JFileChooser();

        // Add file filters for each of the supported plugins.
        List<FileFilter> filters = PluginManager.getInstance()
                                                .getPluginFileFilters();
        for (FileFilter f : filters) {
            jd.addChoosableFileFilter(f);
        }
        int result = jd.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File f = jd.getSelectedFile();

            // Build the data viewer for the file.
            DataViewer viewer = PluginManager.getInstance()
                                             .buildViewerFromFile(f);
            if (viewer == null) {
                logger.error("No DataViewer available.");
                return;
            }

            viewer.setDataFeed(f);
            OpenSHAPA.getApplication().show(viewer.getParentJFrame());

            // adjust the overall frame rate.
            float fps = viewer.getFrameRate();
            if (fps > currentFPS) { currentFPS = fps; }

            // Add the QTDataViewer to the list of viewers we are controlling.
            this.viewers.add(viewer);
        }
    }//GEN-LAST:event_openVideoButtonActionPerformed

    /**
     * Action to invoke when the user clicks on the sync ctrl button.
     */
    @Action
    public void syncCtrlAction() {
        //for (DataViewer viewer : viewers) { /* @todo */; }
    }

    /**
     * Action to invoke when the user clicks on the sync button.
     */
    @Action
    public void syncAction() {
        //for (DataViewer viewer : viewers) { viewer.seekTo(getCurrentTime()); }
    }

    /**
     * Action to invoke when the user clicks the set cell onset button.
     */
    @Action
    public void setCellOnsetAction() {
        new SetSelectedCellStartTimeC(getCurrentTime());
    }

    /**
     * Action to invoke when the user clicks on the set cell offest button.
     */
    @Action
    public void setCellOffsetAction() {
        new SetSelectedCellStopTimeC(getCurrentTime());
    }


    //--------------------------------------------------------------------------
    // Playback actions
    //

    /**
     * Action to invoke when the user clicks on the play button.
     */
    @Action
    public void playAction() {
        playAt(PLAY_RATE);
    }

    /**
     * Action to invoke when the user clicks on the fast foward button.
     */
    @Action
    public void forwardAction() {
        playAt(FFORWARD_RATE);
    }

    /**
     * Action to invoke when the user clicks on the rewind button.
     */
    @Action
    public void rewindAction() {
        playAt(REWIND_RATE);
    }

    /**
     * Action to invoke when the user clicks on the pause button.
     */
    @Action
    public void pauseAction() {
        // Resume from pause at playback rate prior to pause.
        if (clock.isStopped()) {
            shuttleAt(pauseRate);

        // Pause views - store current playback rate.
        } else {
            pauseRate = clock.getRate();
            clock.stop();
            lblSpeed.setText("["
                     + FloatUtils.doubleToFractionStr(new Double(pauseRate))
                     + "]");
        }
    }

    /**
     * Action to invoke when the user clicks on the stop button.
     */
    @Action
    public void stopAction() {
        clock.stop();
        shuttleDirection = ShuttleDirection.UNDEFINED;
    }

    /**
     * Action to invoke when the user clicks on the shuttle forward button.
     *
     * @todo proper behaviour for reversing shuttle direction?
     */
    @Action
    public void shuttleForwardAction() {
        shuttle(ShuttleDirection.FORWARDS);
    }

    /**
     * Action to inovke when the user clicks on the shuttle back button.
     */
    @Action
    public void shuttleBackAction() {
        shuttle(ShuttleDirection.BACKWARDS);
    }

    /**
     * Populates the find time in the controller.
     *
     * @param milliseconds The time to use when populating the find field.
     */
    public void setFindTime(final long milliseconds) {
        this.findTextField.setText(CLOCK_FORMAT.format(milliseconds));
    }

    /**
     * Action to invoke when the user clicks on the find button.
     */
    @Action
    public void findAction() {
        try {
            jumpTo(CLOCK_FORMAT.parse(this.findTextField.getText()).getTime());

        } catch (ParseException e) {
            logger.error("unable to find within video", e);
        }
    }

    /**
     * Action to invoke when the user clicks on the go back button.
     */
    @Action
    public void goBackAction() {
        try {
            long j = -CLOCK_FORMAT.parse(this.goBackTextField.getText())
                                  .getTime();
            jump(Math.min(j, 0));

            // BugzID:721 - After going back - start playing again.
            playAt(PLAY_RATE);

        } catch (ParseException e) {
            logger.error("unable to find within video", e);
        }
    }


    /**
     * Action to invoke when the user clicks on the jog backwards button.
     */
    @Action
    public void jogBackAction() {
        jump((long) (-ONE_SECOND / currentFPS));
    }

    /**
     * Action to invoke when the user clicks on the jog forwards button.
     */
    @Action
    public void jogForwardAction() {
        jump((long) (ONE_SECOND / currentFPS));
    }


    //--------------------------------------------------------------------------
    // [private] play back action helper functions
    //

    /**
     *
     * @param rate Rate of play.
     */
    private void playAt(final float rate) {
        shuttleDirection = ShuttleDirection.UNDEFINED;
        shuttleAt(rate);
    }

    /**
     *
     * @param direction The required direction of the shuttle.
     */
    private void shuttle(final ShuttleDirection direction) {
        float rate = SHUTTLE_RATES[shuttleRate];
        if (ShuttleDirection.UNDEFINED == shuttleDirection) {
            shuttleDirection = direction;
            rate = SHUTTLE_RATES[0];

        } else if (direction == shuttleDirection) {
            if (shuttleRate < (SHUTTLE_RATES.length - 1)) {
                rate = SHUTTLE_RATES[++shuttleRate];
            }

        } else {
            if (shuttleRate > 0) {
                rate = SHUTTLE_RATES[--shuttleRate];

            // BugzID: 676 - Shuttle speed transitions between zero.
            } else {
                rate = 0;
                shuttleDirection = ShuttleDirection.UNDEFINED;
            }
        }

        shuttleAt(shuttleDirection.getParameter() * rate);
    }

    /**
     *
     * @param rate Rate of shuttle.
     */
    private void shuttleAt(final float rate) {
        clock.setRate(rate);
        clock.start();
    }

    /**
     * @param step Milliseconds to jump.
     */
    private void jump(final long step) {
        clock.stop();
        clock.setRate(PLAY_RATE);
        clock.stepTime(step);
    }

    /**
     * @param time Absolute time to jump to.
     */
    private void jumpTo(final long time) {
        clock.stop();
        clock.setRate(PLAY_RATE);
        clock.setTime(time);
    }


    //--------------------------------------------------------------------------
    //
    //

    /**
     * Action to invoke when the user clicks on the create new cell button.
     */
    @Action
    public void createCellAction() {
        new CreateNewCellC();
    }

    /**
     * Action to invoke when the user clicks on the new cell button.
     */
    @Action
    public void createNewCellAction() {
        new CreateNewCellC(getCurrentTime());
    }

    /**
     * Action to invoke when the user clicks on the new cell onset button.
     */
    @Action
    public void setNewCellStopTime() {
        new SetNewCellStopTimeC(getCurrentTime());
    }

    /**
     * Action to invoke when the user clicks on the sync video button.
     */
    @Action
    public void syncVideoAction() {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createNewCell;
    private javax.swing.JButton createNewCellButton;
    private javax.swing.JButton findButton;
    private javax.swing.JTextField findTextField;
    private javax.swing.JButton forwardButton;
    private javax.swing.JButton goBackButton;
    private javax.swing.JTextField goBackTextField;
    private javax.swing.JPanel gridButtonPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton jogBackButton;
    private javax.swing.JButton jogForwardButton;
    private javax.swing.JLabel lblSpeed;
    private javax.swing.JButton openVideoButton;
    private javax.swing.JButton pauseButton;
    private javax.swing.JButton playButton;
    private javax.swing.JButton rewindButton;
    private javax.swing.JButton setCellOffsetButton;
    private javax.swing.JButton setCellOnsetButton;
    private javax.swing.JButton setNewCellOnsetButton;
    private javax.swing.JButton shuttleBackButton;
    private javax.swing.JButton shuttleForwardButton;
    private javax.swing.JButton stopButton;
    private javax.swing.JButton syncButton;
    private javax.swing.JButton syncCtrlButton;
    private javax.swing.JButton syncVideoButton;
    private javax.swing.JLabel timestampLabel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

}
