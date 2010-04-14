package org.openshapa.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.LinkedList;
import java.util.List;
import java.util.SimpleTimeZone;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.application.ResourceMap;

import org.openshapa.OpenSHAPA;

import org.openshapa.OpenSHAPA.Platform;

import org.openshapa.event.PlaybackEvent;
import org.openshapa.event.PlaybackListener;
import org.openshapa.event.PlaybackEvent.PlaybackType;


/**
 * Playback UI.
 */
public final class PlaybackV extends OpenSHAPADialog {

    /** Format for representing time. */
    private static final DateFormat CLOCK_FORMAT;

    // initialize standard date format for clock display.
    static {
        CLOCK_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");
        CLOCK_FORMAT.setTimeZone(new SimpleTimeZone(0, "NO_ZONE"));
    }

    /** */
    private javax.swing.JButton createNewCell;

    /** */
    private javax.swing.JButton createNewCellSetOnset;

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
    private javax.swing.JLabel atLabel;

    /** */
    private javax.swing.JLabel multiplierLabel;

    /** */
    private javax.swing.JButton jogBackButton;

    /** */
    private javax.swing.JButton jogForwardButton;

    /** */
    private javax.swing.JLabel speedLabel;

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
    private javax.swing.JPanel tracksPanel;

    /** Contains the list of listeners interested in playback UI events. */
    private List<PlaybackListener> listeners;

    private long goTime;

    private long onsetTime;

    private long offsetTime;

    /**
     * Constructor. Creates the playback UI.
     *
     * @param parent
     *            The parent of this form.
     * @param modal
     *            Should the dialog be modal or not?
     * @param tracksPanel
     *            The panel containing the tracks interface.
     */
    public PlaybackV(final JFrame parent, final boolean modal,
        final JPanel tracksPanel) {
        super(parent, modal);

        listeners = new LinkedList<PlaybackListener>();

        if (OpenSHAPA.getPlatform() == Platform.MAC) {
            initComponentsMac();
        } else {
            initComponents();
        }

        setName(this.getClass().getSimpleName());

        if (tracksPanel != null) {
            this.tracksPanel.add(tracksPanel);
            showTracksPanel(false);
        }

    }

    public static void main(final String[] args) {
        PlaybackV test = new PlaybackV(null, false, null);
        test.show();
    }

    /**
     * Add listener interested in playback UI events.
     *
     * @param listener Listener to add.
     */
    public void addPlaybackListener(final PlaybackListener listener) {

        synchronized (this) {
            listeners.add(listener);
        }
    }

    /**
     * Remove the listener for the playback events list.
     *
     * @param listener Remove the given listener.
     */
    public void removePlaybackListener(final PlaybackListener listener) {

        synchronized (this) {
            listeners.remove(listener);
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

    /**
     * Populates the find time in the controller.
     *
     * @param milliseconds
     *            The time to use when populating the find field.
     */
    public void setFindTime(final long milliseconds) {
        assert SwingUtilities.isEventDispatchThread();
        findTextField.setText(CLOCK_FORMAT.format(milliseconds));
        onsetTime = milliseconds;
    }

    public long getFindTime() {
        return onsetTime;
    }

    /**
     * Populates the find offset time in the controller.
     *
     * @param milliseconds
     *            The time to use when populating the find field.
     */
    public void setFindOffsetField(final long milliseconds) {
        assert SwingUtilities.isEventDispatchThread();
        findOffsetField.setText(CLOCK_FORMAT.format(milliseconds));
        offsetTime = milliseconds;
    }

    public long getFindOffsetTime() {
        return offsetTime;
    }

    public void setTimestampLabelText(final String text) {
        assert SwingUtilities.isEventDispatchThread();
        timestampLabel.setText(text);
    }

    public void setSpeedLabel(final String text) {
        assert SwingUtilities.isEventDispatchThread();
        speedLabel.setText(text);
    }

    public void setShowTracksButtonIcon(final Icon icon) {
        assert SwingUtilities.isEventDispatchThread();
        showTracksButton.setIcon(icon);
    }

    /**
     * @param show
     *            true to show the tracks layout, false otherwise.
     */
    public void showTracksPanel(final boolean show) {
        assert SwingUtilities.isEventDispatchThread();
        tracksPanel.setVisible(show);
        tracksPanel.repaint();
        pack();
        validate();
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
        createNewCellSetOnset = new javax.swing.JButton();
        jogForwardButton = new javax.swing.JButton();
        setNewCellOffsetButton = new javax.swing.JButton();
        goBackTextField = new javax.swing.JTextField();
        findTextField = new javax.swing.JTextField();
        syncVideoButton = new javax.swing.JButton();
        addDataButton = new javax.swing.JButton();
        timestampLabel = new javax.swing.JLabel();
        speedLabel = new javax.swing.JLabel();
        createNewCell = new javax.swing.JButton();
        atLabel = new javax.swing.JLabel();
        multiplierLabel = new javax.swing.JLabel();
        findOffsetField = new javax.swing.JTextField();
        showTracksButton = new javax.swing.JButton();
        tracksPanel = new javax.swing.JPanel();

        final int fontSize = 11;

        setLayout(new MigLayout("hidemode 3"));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        ResourceMap resourceMap = org.jdesktop.application.Application
            .getInstance(org.openshapa.OpenSHAPA.class).getContext()
            .getResourceMap(PlaybackV.class);
        setTitle(resourceMap.getString("title"));
        setName(this.getClass().getSimpleName());
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

        atLabel.setText("@");
        timestampPanel.add(atLabel);

        speedLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        speedLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1,
                1, 2));
        speedLabel.setName("lblSpeed");
        speedLabel.setText("0");
        timestampPanel.add(speedLabel);

        multiplierLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        multiplierLabel.setText("x");
        timestampPanel.add(multiplierLabel);

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
        setCellOnsetButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    setCellOnsetAction(e);
                }
            });
        setCellOnsetButton.setIcon(resourceMap.getIcon(
                "setCellOnsetButton.icon"));
        setCellOnsetButton.setFocusPainted(false);
        setCellOnsetButton.setName("setCellOnsetButton");
        setCellOnsetButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/set-cell-onset-selected.png")));
        gridButtonPanel.add(setCellOnsetButton, "w 45!, h 45!");

        // Set cell offset button
        setCellOffsetButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    setCellOffsetAction(e);
                }
            });
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
        rewindButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    rewindAction(e);
                }
            });
        rewindButton.setIcon(resourceMap.getIcon("rewindButton.icon"));
        rewindButton.setFocusPainted(false);
        rewindButton.setName("rewindButton");
        rewindButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/rewind-selected.png")));
        gridButtonPanel.add(rewindButton, "w 45!, h 45!");

        // Play video button
        playButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    playAction(e);
                }
            });
        playButton.setIcon(resourceMap.getIcon("playButton.icon"));
        playButton.setFocusPainted(false);
        playButton.setName("playButton");
        playButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/play-selected.png")));
        playButton.setRequestFocusEnabled(false);
        gridButtonPanel.add(playButton, "w 45!, h 45!");

        // Fast forward button
        forwardButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    forwardAction(e);
                }
            });
        forwardButton.setIcon(resourceMap.getIcon("forwardButton.icon"));
        forwardButton.setFocusPainted(false);
        forwardButton.setName("forwardButton");
        forwardButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/fast-forward-selected.png")));
        gridButtonPanel.add(forwardButton, "w 45!, h 45!");

        // Go back button
        goBackButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    goBackAction(e);
                }
            });
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
        shuttleBackButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    shuttleBackAction(e);
                }
            });
        shuttleBackButton.setIcon(resourceMap.getIcon(
                "shuttleBackButton.icon"));
        shuttleBackButton.setFocusPainted(false);
        shuttleBackButton.setName("shuttleBackButton");
        shuttleBackButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/shuttle-backward-selected.png")));
        gridButtonPanel.add(shuttleBackButton, "w 45!, h 45!");

        // Stop button
        stopButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    stopAction(e);
                }
            });
        stopButton.setIcon(resourceMap.getIcon("stopButton.icon"));
        stopButton.setFocusPainted(false);
        stopButton.setName("stopButton");
        stopButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/stop-selected.png")));
        gridButtonPanel.add(stopButton, "w 45!, h 45!");

        // Shuttle forward button
        shuttleForwardButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    shuttleForwardAction(e);
                }
            });
        shuttleForwardButton.setIcon(resourceMap.getIcon(
                "shuttleForwardButton.icon"));
        shuttleForwardButton.setFocusPainted(false);
        shuttleForwardButton.setName("shuttleForwardButton");
        shuttleForwardButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/shuttle-forward-selected.png")));
        gridButtonPanel.add(shuttleForwardButton, "w 45!, h 45!");

        // Find button
        findButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    findAction(e);
                }
            });
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
        jogBackButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    jogBackAction(e);
                }
            });
        jogBackButton.setIcon(resourceMap.getIcon("jogBackButton.icon"));
        jogBackButton.setFocusPainted(false);
        jogBackButton.setName("jogBackButton");
        jogBackButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/jog-backward-selected.png")));
        gridButtonPanel.add(jogBackButton, "w 45!, h 45!");

        // Pause button
        pauseButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    pauseAction(e);
                }
            });
        pauseButton.setIcon(resourceMap.getIcon("pauseButton.icon"));
        pauseButton.setFocusPainted(false);
        pauseButton.setName("pauseButton");
        pauseButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/pause-selected.png")));
        gridButtonPanel.add(pauseButton, "w 45!, h 45!");

        // Jog forward button
        jogForwardButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    jogForwardAction(e);
                }
            });
        jogForwardButton.setIcon(resourceMap.getIcon("jogForwardButton.icon"));
        jogForwardButton.setFocusPainted(false);
        jogForwardButton.setName("jogForwardButton");
        jogForwardButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/jog-forward-selected.png")));
        gridButtonPanel.add(jogForwardButton, "w 45!, h 45!");

        // Create new cell button
        createNewCell.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    createCellAction(e);
                }
            });
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
        createNewCellSetOnset.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    createNewCellSetOnsetAction(e);
                }
            });
        createNewCellSetOnset.setIcon(resourceMap.getIcon(
                "createNewCellButton.icon"));
        createNewCellSetOnset.setFocusPainted(false);
        createNewCellSetOnset.setName("newCellAndOnsetButton");
        createNewCellSetOnset.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/"
                    + "create-new-cell-and-set-onset-selected.png")));
        gridButtonPanel.add(createNewCellSetOnset, "span 2, w 92!, h 45!");

        // Set new cell offset button
        setNewCellOffsetButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    setNewCellOffsetTime(e);
                }
            });
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
        createNewCellSetOnset = new javax.swing.JButton();
        jogForwardButton = new javax.swing.JButton();
        setNewCellOffsetButton = new javax.swing.JButton();
        goBackTextField = new javax.swing.JTextField();
        findTextField = new javax.swing.JTextField();
        syncVideoButton = new javax.swing.JButton();
        addDataButton = new javax.swing.JButton();
        timestampLabel = new javax.swing.JLabel();
        speedLabel = new javax.swing.JLabel();
        createNewCell = new javax.swing.JButton();
        atLabel = new javax.swing.JLabel();
        multiplierLabel = new javax.swing.JLabel();
        findOffsetField = new javax.swing.JTextField();
        showTracksButton = new javax.swing.JButton();
        tracksPanel = new javax.swing.JPanel();

        final int fontSize = 11;

        setLayout(new MigLayout("hidemode 3"));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.jdesktop.application.ResourceMap resourceMap =
            org.jdesktop.application.Application.getInstance(
                org.openshapa.OpenSHAPA.class).getContext().getResourceMap(
                PlaybackV.class);
        setTitle(resourceMap.getString("title"));
        setName(this.getClass().getSimpleName());
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

        atLabel.setText("@");
        timestampPanel.add(atLabel);

        speedLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        speedLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1,
                1, 2));
        speedLabel.setName("lblSpeed");
        speedLabel.setText("0");
        timestampPanel.add(speedLabel);

        multiplierLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        multiplierLabel.setText("x");
        timestampPanel.add(multiplierLabel);

        gridButtonPanel.add(timestampPanel, "span 3, pushx, growx");

        // Sync control button
        syncCtrlButton.setEnabled(false);
        syncCtrlButton.setFocusPainted(false);
        gridButtonPanel.add(syncCtrlButton, "w 45!, h 45!");

        // Sync button
        syncButton.setEnabled(false);

        // Set cell onset button
        setCellOnsetButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    setCellOnsetAction(e);
                }
            });
        setCellOnsetButton.setIcon(resourceMap.getIcon(
                "setCellOnsetButton.icon"));
        setCellOnsetButton.setFocusPainted(false);
        setCellOnsetButton.setName("setCellOnsetButton");
        setCellOnsetButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/set-cell-onset-selected.png")));
        gridButtonPanel.add(setCellOnsetButton, "w 45!, h 45!");

        // Set cell offset button
        setCellOffsetButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    setCellOffsetAction(e);
                }
            });
        setCellOffsetButton.setIcon(resourceMap.getIcon(
                "setCellOffsetButton.icon"));
        setCellOffsetButton.setFocusPainted(false);
        setCellOffsetButton.setName("setCellOffsetButton");
        setCellOffsetButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/set-cell-offset-selected.png")));
        gridButtonPanel.add(setCellOffsetButton, "w 45!, h 45!");

        // Go back button
        goBackButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    goBackAction(e);
                }
            });
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
        rewindButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    rewindAction(e);
                }
            });
        rewindButton.setIcon(resourceMap.getIcon("rewindButton.icon"));
        rewindButton.setFocusPainted(false);
        rewindButton.setName("rewindButton");
        rewindButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/rewind-selected.png")));
        gridButtonPanel.add(rewindButton, "w 45!, h 45!");

        // Play video button
        playButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    playAction(e);
                }
            });
        playButton.setIcon(resourceMap.getIcon("playButton.icon"));
        playButton.setFocusPainted(false);
        playButton.setName("playButton");
        playButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/play-selected.png")));
        playButton.setRequestFocusEnabled(false);
        gridButtonPanel.add(playButton, "w 45!, h 45!");

        // Fast forward button
        forwardButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    forwardAction(e);
                }
            });
        forwardButton.setIcon(resourceMap.getIcon("forwardButton.icon"));
        forwardButton.setFocusPainted(false);
        forwardButton.setName("forwardButton");
        forwardButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/fast-forward-selected.png")));
        gridButtonPanel.add(forwardButton, "w 45!, h 45!");

        // Find button
        findButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    findAction(e);
                }
            });
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
        goBackTextField.addKeyListener(new KeyAdapter() {
                @Override public void keyReleased(final KeyEvent e) {
                    goBackTextFieldEvent(e);
                }
            });
        gridButtonPanel.add(goBackTextField, "w 80!, h 45!");

        // Shuttle back button
        shuttleBackButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    shuttleBackAction(e);
                }
            });
        shuttleBackButton.setIcon(resourceMap.getIcon(
                "shuttleBackButton.icon"));
        shuttleBackButton.setFocusPainted(false);
        shuttleBackButton.setName("shuttleBackButton");
        shuttleBackButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/shuttle-backward-selected.png")));
        gridButtonPanel.add(shuttleBackButton, "w 45!, h 45!");

        // Stop button
        stopButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    stopAction(e);
                }
            });
        stopButton.setIcon(resourceMap.getIcon("stopButton.icon"));
        stopButton.setFocusPainted(false);
        stopButton.setName("stopButton");
        stopButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/stop-selected.png")));
        gridButtonPanel.add(stopButton, "w 45!, h 45!");

        // Shuttle forward button
        shuttleForwardButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    shuttleForwardAction(e);
                }
            });
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
        jogBackButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    jogBackAction(e);
                }
            });
        jogBackButton.setIcon(resourceMap.getIcon("jogBackButton.icon"));
        jogBackButton.setFocusPainted(false);
        jogBackButton.setName("jogBackButton");
        jogBackButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/jog-backward-selected.png")));
        gridButtonPanel.add(jogBackButton, "w 45!, h 45!");

        // Pause button
        pauseButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    pauseAction(e);
                }
            });
        pauseButton.setIcon(resourceMap.getIcon("pauseButton.icon"));
        pauseButton.setFocusPainted(false);
        pauseButton.setName("pauseButton");
        pauseButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/pause-selected.png")));
        gridButtonPanel.add(pauseButton, "w 45!, h 45!");

        // Jog forward button
        jogForwardButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    jogForwardAction(e);
                }
            });
        jogForwardButton.setIcon(resourceMap.getIcon("jogForwardButton.icon"));
        jogForwardButton.setFocusPainted(false);
        jogForwardButton.setName("jogForwardButton");
        jogForwardButton.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/jog-forward-selected.png")));
        gridButtonPanel.add(jogForwardButton, "w 45!, h 45!");

        // Create new cell button
        createNewCell.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    createCellAction(e);
                }
            });
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

        // Create new cell set onset button
        createNewCellSetOnset.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    createNewCellSetOnsetAction(e);
                }
            });
        createNewCellSetOnset.setIcon(resourceMap.getIcon(
                "createNewCellButton.icon"));
        createNewCellSetOnset.setFocusPainted(false);
        createNewCellSetOnset.setName("newCellAndOnsetButton");
        createNewCellSetOnset.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource(
                    "/icons/DataController/eng/"
                    + "create-new-cell-and-set-onset-selected.png")));
        gridButtonPanel.add(createNewCellSetOnset, "span 2, w 95!, h 45!");

        // Set new cell offset button
        setNewCellOffsetButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    setNewCellOffsetTime(e);
                }
            });
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
        createNewCellSetOnset.doClick();
    }

    /** Simulates sync button clicked. */
    public void pressSyncButton() {
        syncButton.doClick();
    }

    /** Simulates jog back button click. */
    public void pressJogBackButton() {
        jogBackButton.doClick();
    }

    /** Simulates jog forward button click. */
    public void pressJogForwardButton() {
        jogForwardButton.doClick();
    }

    /**
     * Handle the go back text field text entry event.
     *
     * @param evt The event to handle.
     */
    private void goBackTextFieldEvent(final KeyEvent evt) {

        try {
            goTime = -CLOCK_FORMAT.parse(goBackTextField.getText()).getTime();
        } catch (ParseException e) {
            ;
        }
    }

    /**
     * Action to invoke when the user clicks on the open button.
     *
     * @param evt
     *            The event that triggered this action.
     */
    private void openVideoButtonActionPerformed(final ActionEvent evt) {

        PlaybackEvent event = new PlaybackEvent(this, PlaybackType.ADD_DATA,
                goTime, onsetTime, offsetTime, evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.addDataEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks the show tracks button.
     *
     * @param evt
     *            The event that triggered this action.
     */
    private void showTracksButtonActionPerformed(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this, PlaybackType.SHOW_TRACKS,
                goTime, onsetTime, offsetTime, evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.showTracksEvent(event);
        }
    }


    /**
     * Action to invoke when the user clicks the set cell onset button.
     */
    private void setCellOnsetAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this,
                PlaybackType.SET_CELL_ONSET, goTime, onsetTime, offsetTime,
                evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.setCellOnsetEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks on the set cell offest button.
     */
    private void setCellOffsetAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this,
                PlaybackType.SET_CELL_OFFSET, goTime, onsetTime, offsetTime,
                evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.setCellOffsetEvent(event);
        }
    }


    /**
     * Action to invoke when the user clicks on the play button.
     */
    private void playAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this, PlaybackType.PLAY, goTime,
                onsetTime, offsetTime, evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.playEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks on the fast foward button.
     */
    private void forwardAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this, PlaybackType.FORWARD,
                goTime, onsetTime, offsetTime, evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.forwardEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks on the rewind button.
     */
    private void rewindAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this, PlaybackType.REWIND,
                goTime, onsetTime, offsetTime, evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.rewindEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks on the pause button.
     */
    private void pauseAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this, PlaybackType.PAUSE,
                goTime, onsetTime, offsetTime, evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.pauseEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks on the stop button.
     */
    private void stopAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this, PlaybackType.STOP, goTime,
                onsetTime, offsetTime, evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.stopEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks on the shuttle forward button.
     *
     * @todo proper behaviour for reversing shuttle direction?
     */
    private void shuttleForwardAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this,
                PlaybackType.SHUTTLE_FORWARD, goTime, onsetTime, offsetTime,
                evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.shuttleForwardEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks on the shuttle back button.
     */
    private void shuttleBackAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this, PlaybackType.SHUTTLE_BACK,
                goTime, onsetTime, offsetTime, evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.shuttleBackEvent(event);
        }
    }


    /**
     * Action to invoke when the user clicks on the find button.
     */
    private void findAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this, PlaybackType.FIND, goTime,
                onsetTime, offsetTime, evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.findEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks on the go back button.
     */
    private void goBackAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this, PlaybackType.GO_BACK,
                goTime, onsetTime, offsetTime, evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.goBackEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks on the jog backwards button.
     */
    private void jogBackAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this, PlaybackType.JOG_BACK,
                goTime, onsetTime, offsetTime, evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.jogBackEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks on the jog forwards button.
     */
    private void jogForwardAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this, PlaybackType.JOG_FORWARD,
                goTime, onsetTime, offsetTime, evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.jogForwardEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks on the create new cell button.
     */
    private void createCellAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this, PlaybackType.NEW_CELL,
                goTime, onsetTime, offsetTime, evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.newCellEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks on the new cell set offset button.
     */
    private void createNewCellSetOnsetAction(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this,
                PlaybackType.NEW_CELL_OFFSET, goTime, onsetTime, offsetTime,
                evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.newCellSetOnsetEvent(event);
        }
    }

    /**
     * Action to invoke when the user clicks on the new cell onset button.
     */
    private void setNewCellOffsetTime(final ActionEvent evt) {
        PlaybackEvent event = new PlaybackEvent(this,
                PlaybackType.NEW_CELL_SET_ONSET, goTime, onsetTime, offsetTime,
                evt.getModifiers());

        for (PlaybackListener listener : listeners) {
            listener.setNewCellOffsetEvent(event);
        }
    }

}
