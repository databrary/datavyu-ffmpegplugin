package au.com.nicta.openshapa.views;

import au.com.nicta.openshapa.cont.ContinuousDataController;
import au.com.nicta.openshapa.cont.ContinuousDataViewer;
import au.com.nicta.openshapa.db.TimeStamp;
import java.awt.FileDialog;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.JButton;
import org.apache.log4j.Logger;

/**
 * Quicktime video controller.
 *
 * @author cfreeman
 */
public final class QTVideoController extends javax.swing.JDialog
implements ContinuousDataController /*, ExecutiveKeyListener*/ {
    
    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(QTVideoController.class);

    //protected Executive parent = null;
    private JButton lastButton = null;
    private TimeStamp currentTimestamp = null;

    /** The list of viewers associated with this controller. */
    private Vector<QTVideoViewer> viewers;

    /** The dialog to present to the user when they desire to load a file. */
    private FileDialog jfc;

    /**
     * Constructor. Creates a new QTVideoController.
     *
     * @param parent The parent of this form.
     * @param modal Should the dialog be modal or not?
     */
    public QTVideoController(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        initComponents();
        setName(this.getClass().getSimpleName());
        viewers = new Vector<QTVideoViewer>();
    }

    /*
    public QTVideoController(Executive exec)
    {
        this.parent = exec;
        initComponents();
        this.setSize(270, 300);
        if (this.parent != null) {
            this.parent.addExecutiveKeyListener(this);
            this.parent.setActiveExecutiveKeyListener(this);
        }
    }
     */

    public void setCurrentLocation(TimeStamp ts) {
        this.currentTimestamp = ts;
    }

    public TimeStamp getCurrentLocation() {
        return (this.currentTimestamp);
    }

    /**
     * Shutdowns the specified viewer.
     *
     * @param viewer The viewer to shutdown.
     */
    @Override
    public void shutdown(final ContinuousDataViewer viewer) {
        for (int i = 0; i < this.viewers.size(); i++) {
            if (viewer == this.viewers.elementAt(i)) {
                this.viewers.elementAt(i).dispose();
                this.viewers.remove(viewer);
            }
        }
    }

    /*
    public void executiveKeyPressed(KeyEvent ke)
    {
        switch (ke.getKeyCode()) {
              case 96: { // 0
                this.lastButton = this.createNewCellButton;
                this.createNewCellButtonActionPerformed(null);
                break;
              }
              case 97: { // 1
                this.lastButton = this.jogBackButton;
                this.jogBackButtonActionPerformed(null);
                break;
              }
              case 98: { // 2
                this.lastButton = this.stopButton;
                this.stopButtonActionPerformed(null);
                break;
              }
              case 99: { // 3
                this.lastButton = this.jogForwardButton;
                this.jogBackButtonActionPerformed(null);
                break;
              }
              case 100: { // 4
                this.lastButton = this.shuttleBackButton;
                this.shuttleBackButtonActionPerformed(null);
                break;
              }
              case 101: { // 5
                this.lastButton = this.pauseButton;
                this.pauseButtonActionPerformed(null);
                break;
              }
              case 102: { // 6
                this.lastButton = this.shuttleForwardButton;
                this.shuttleForwardButtonActionPerformed(null);
                break;
              }
              case 103: { // 7
                this.lastButton = this.rewindButton;
                this.rewindButtonActionPerformed(null);
                break;
              }
              case 104: { // 8
                this.lastButton = this.playButton;
                this.playButtonActionPerformed(null);
                break;
              }
              case 105: { // 9
                this.lastButton = this.forwardButton;
                this.forwardButtonActionPerformed(null);
                break;
              }
              case 106: { // *
                this.lastButton = this.setCellOffsetButton;
                this.setCellOffsetButtonActionPerformed(null);
                break;
              }
              case 107: { // +
                this.lastButton = this.findButton;
                this.findButtonActionPerformed(null);
                break;
              }
              case 109: { // -
                this.lastButton = this.goBackButton;
                this.goBackButtonActionPerformed(null);
                break;
              }
              case 110: { // .
                this.lastButton = this.setNewCellOnsetButton;
                this.setNewCellOnsetButtonActionPerformed(null);
                break;
              }
              case 12: { // clear
                this.lastButton = this.syncCtrlButton;
                this.syncCtrlButtonActionPerformed(null);
                break;
              }
              case 61: { // /
                this.lastButton = this.setCellOnsetButton;
                this.setCellOnsetButtonActionPerformed(null);
                break;
              }
              default: {
                //System.out.println("KeyCode: " + ke.getKeyCode());
                return;
              }
            } // End Switch
    }

    public void executiveKeyReleased(KeyEvent ke)
    {
    }

    public void executiveKeyTyped(KeyEvent ke)
    {

    }

    public void executiveKeyControlGained()
    {
    }

    public void executiveKeyControlLost()
    {
    }
     */


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        timestampLabel = new javax.swing.JLabel();
        openVideoButton = new javax.swing.JButton();
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
        jogForwardButton = new javax.swing.JButton();
        rightTimePanel = new javax.swing.JPanel();
        syncVideoButton = new javax.swing.JButton();
        goBackTextField = new javax.swing.JTextField();
        findTextField = new javax.swing.JTextField();
        bottomPanel = new javax.swing.JPanel();
        leftButtonPanel = new javax.swing.JPanel();
        createNewCellButton = new javax.swing.JButton();
        setNewCellOnsetButton = new javax.swing.JButton();
        fillerPanel = new javax.swing.JPanel();
        timestampSetupButton = new javax.swing.JButton();
        videoProgressBar = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quicktime Video Controller");
        setBackground(java.awt.Color.white);
        setName("qtVideoController"); // NOI18N
        setResizable(false);

        mainPanel.setBackground(java.awt.Color.white);
        mainPanel.setLayout(new java.awt.BorderLayout(2, 0));

        topPanel.setBackground(java.awt.Color.white);
        topPanel.setLayout(new java.awt.BorderLayout());

        timestampLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timestampLabel.setText("00:00:00:000");
        timestampLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        topPanel.add(timestampLabel, java.awt.BorderLayout.CENTER);

        openVideoButton.setBackground(java.awt.Color.white);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(au.com.nicta.openshapa.OpenSHAPA.class).getContext().getResourceMap(QTVideoController.class);
        openVideoButton.setText(resourceMap.getString("openVideoButton.text")); // NOI18N
        openVideoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openVideoButtonActionPerformed(evt);
            }
        });
        topPanel.add(openVideoButton, java.awt.BorderLayout.LINE_START);

        mainPanel.add(topPanel, java.awt.BorderLayout.NORTH);

        gridButtonPanel.setBackground(java.awt.Color.white);
        gridButtonPanel.setLayout(new java.awt.GridLayout(4, 4));

        syncCtrlButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/syncCtrlButton.png"))); // NOI18N
        syncCtrlButton.setMaximumSize(new java.awt.Dimension(32, 32));
        syncCtrlButton.setMinimumSize(new java.awt.Dimension(32, 32));
        syncCtrlButton.setPreferredSize(new java.awt.Dimension(32, 32));
        syncCtrlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syncCtrlButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(syncCtrlButton);

        syncButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/syncButton.png"))); // NOI18N
        syncButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syncButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(syncButton);

        setCellOnsetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/cellOnsetButton.png"))); // NOI18N
        setCellOnsetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setCellOnsetButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(setCellOnsetButton);

        setCellOffsetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/cellOffsetButton.png"))); // NOI18N
        setCellOffsetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setCellOffsetButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(setCellOffsetButton);

        rewindButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/rewindButton.png"))); // NOI18N
        rewindButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rewindButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(rewindButton);

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/playButton.png"))); // NOI18N
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(playButton);

        forwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/forwardButton.png"))); // NOI18N
        forwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(forwardButton);

        goBackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/goBackButton.png"))); // NOI18N
        goBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goBackButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(goBackButton);

        shuttleBackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/shuttleLeftButton.png"))); // NOI18N
        shuttleBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shuttleBackButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(shuttleBackButton);

        pauseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/pauseButton.png"))); // NOI18N
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(pauseButton);

        shuttleForwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/shuttleRightButton.png"))); // NOI18N
        shuttleForwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shuttleForwardButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(shuttleForwardButton);

        findButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/findButton.png"))); // NOI18N
        findButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(findButton);

        jogBackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/jogLeftButton.png"))); // NOI18N
        jogBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jogBackButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(jogBackButton);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/stopButton.png"))); // NOI18N
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(stopButton);

        jogForwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/jogRightButton.png"))); // NOI18N
        jogForwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jogForwardButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(jogForwardButton);

        mainPanel.add(gridButtonPanel, java.awt.BorderLayout.CENTER);

        rightTimePanel.setBackground(java.awt.Color.white);
        rightTimePanel.setLayout(new java.awt.GridLayout(4, 1));

        syncVideoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/syncVideoButton.png"))); // NOI18N
        syncVideoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syncVideoButtonActionPerformed(evt);
            }
        });
        rightTimePanel.add(syncVideoButton);

        goBackTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        goBackTextField.setText("00:00:00:000");
        rightTimePanel.add(goBackTextField);

        findTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        findTextField.setText("00:00:00:000");
        rightTimePanel.add(findTextField);

        mainPanel.add(rightTimePanel, java.awt.BorderLayout.EAST);

        bottomPanel.setBackground(java.awt.Color.white);
        bottomPanel.setLayout(new java.awt.BorderLayout());

        leftButtonPanel.setBackground(java.awt.Color.white);
        leftButtonPanel.setLayout(new java.awt.GridBagLayout());

        createNewCellButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/newCellButton.png"))); // NOI18N
        createNewCellButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewCellButtonActionPerformed(evt);
            }
        });
        leftButtonPanel.add(createNewCellButton, new java.awt.GridBagConstraints());

        setNewCellOnsetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/newCellOffsetButton.png"))); // NOI18N
        setNewCellOnsetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setNewCellOnsetButtonActionPerformed(evt);
            }
        });
        leftButtonPanel.add(setNewCellOnsetButton, new java.awt.GridBagConstraints());

        bottomPanel.add(leftButtonPanel, java.awt.BorderLayout.WEST);

        fillerPanel.setBackground(java.awt.Color.white);
        fillerPanel.setLayout(new java.awt.BorderLayout());

        timestampSetupButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/QTVideoController/eng/timestampSetupButton.png"))); // NOI18N
        timestampSetupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timestampSetupButtonActionPerformed(evt);
            }
        });
        fillerPanel.add(timestampSetupButton, java.awt.BorderLayout.CENTER);

        bottomPanel.add(fillerPanel, java.awt.BorderLayout.EAST);

        videoProgressBar.setBackground(java.awt.Color.white);
        videoProgressBar.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                videoProgressBarStateChanged(evt);
            }
        });
        bottomPanel.add(videoProgressBar, java.awt.BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openVideoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openVideoButtonActionPerformed
        jfc = new FileDialog(this, "Select QuickTime Video File",
                             FileDialog.LOAD);
        jfc.setVisible(true);

        QTVideoViewer viewer = new QTVideoViewer(this);
        File f = new File(jfc.getDirectory(), jfc.getFile());
        viewer.setVideoFile(f);
        viewer.setVisible(true);

        // Add the QTVideoViewer to the list of viewers we are controlling.
        this.viewers.add(viewer);
    }//GEN-LAST:event_openVideoButtonActionPerformed

    private void syncCtrlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_syncCtrlButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).syncCtrl();
        }
    }//GEN-LAST:event_syncCtrlButtonActionPerformed

    private void syncButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_syncButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).sync();
        }
    }//GEN-LAST:event_syncButtonActionPerformed

    private void setCellOnsetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setCellOnsetButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).setCellStartTime();
        }
    }//GEN-LAST:event_setCellOnsetButtonActionPerformed

    private void setCellOffsetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setCellOffsetButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).setCellStopTime();
        }
    }//GEN-LAST:event_setCellOffsetButtonActionPerformed

    private void rewindButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rewindButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).rewind();
        }
    }//GEN-LAST:event_rewindButtonActionPerformed

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).play();
        }
    }//GEN-LAST:event_playButtonActionPerformed

    private void forwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forwardButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).forward();
        }
    }//GEN-LAST:event_forwardButtonActionPerformed

    private void goBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goBackButtonActionPerformed
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
            Date videoDate = format.parse(this.goBackTextField.getText());
            Date originDate = format.parse("00:00:00:000");

            // Determine the time in milliseconds.
            long milli = videoDate.getTime() - originDate.getTime();

            for (int i = 0; i < this.viewers.size(); i++) {
                this.viewers.elementAt(i).goBack(milli);
            }
        } catch (ParseException e) {
            logger.error("unable to find within video", e);
        }
    }//GEN-LAST:event_goBackButtonActionPerformed

    private void shuttleBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shuttleBackButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).shuttleBack();
        }
    }//GEN-LAST:event_shuttleBackButtonActionPerformed

    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).pause();
        }
    }//GEN-LAST:event_pauseButtonActionPerformed

    private void shuttleForwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shuttleForwardButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).shuttleForward();
        }
    }//GEN-LAST:event_shuttleForwardButtonActionPerformed

    private void findButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findButtonActionPerformed
        //Date seekTime = DateFormat.getInstance().parse();
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
            Date videoDate = format.parse(this.findTextField.getText());
            Date originDate = format.parse("00:00:00:000");

            // Determine the time in milliseconds.
            long milli = videoDate.getTime() - originDate.getTime();

            for (int i = 0; i < this.viewers.size(); i++) {
                this.viewers.elementAt(i).find(milli);
            }
        } catch (ParseException e) {
            logger.error("unable to find within video", e);
        }
    }//GEN-LAST:event_findButtonActionPerformed

    private void jogBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jogBackButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).jogBack();
        }
    }//GEN-LAST:event_jogBackButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).stop();
        }
    }//GEN-LAST:event_stopButtonActionPerformed

    private void jogForwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jogForwardButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).jogForward();
        }
    }//GEN-LAST:event_jogForwardButtonActionPerformed

    private void createNewCellButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewCellButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).createNewCell();
        }
    }//GEN-LAST:event_createNewCellButtonActionPerformed

    private void setNewCellOnsetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setNewCellOnsetButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).setNewCellOnset();
        }
    }//GEN-LAST:event_setNewCellOnsetButtonActionPerformed

    private void syncVideoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_syncVideoButtonActionPerformed
        for (int i = 0; i < this.viewers.size(); i++) {
            this.viewers.elementAt(i).sync();
        }
    }//GEN-LAST:event_syncVideoButtonActionPerformed

    private void timestampSetupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timestampSetupButtonActionPerformed
    }//GEN-LAST:event_timestampSetupButtonActionPerformed

    private void videoProgressBarStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_videoProgressBarStateChanged
        if (!this.videoProgressBar.getValueIsAdjusting()) {
        }
    }//GEN-LAST:event_videoProgressBarStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton createNewCellButton;
    private javax.swing.JPanel fillerPanel;
    private javax.swing.JButton findButton;
    private javax.swing.JTextField findTextField;
    private javax.swing.JButton forwardButton;
    private javax.swing.JButton goBackButton;
    private javax.swing.JTextField goBackTextField;
    private javax.swing.JPanel gridButtonPanel;
    private javax.swing.JButton jogBackButton;
    private javax.swing.JButton jogForwardButton;
    private javax.swing.JPanel leftButtonPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton openVideoButton;
    private javax.swing.JButton pauseButton;
    private javax.swing.JButton playButton;
    private javax.swing.JButton rewindButton;
    private javax.swing.JPanel rightTimePanel;
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
    private javax.swing.JButton timestampSetupButton;
    private javax.swing.JPanel topPanel;
    private javax.swing.JSlider videoProgressBar;
    // End of variables declaration//GEN-END:variables
}
