package org.openshapa.views.continuous.quicktime;

import com.usermetrix.jclient.Logger;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.openshapa.util.Constants;

import org.openshapa.views.component.DefaultTrackPainter;
import org.openshapa.views.component.TrackPainter;
import org.openshapa.views.continuous.DataController;
import org.openshapa.views.continuous.DataViewer;

import quicktime.QTException;
import quicktime.QTSession;

import quicktime.app.view.QTFactory;

import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;

import quicktime.qd.QDDimension;

import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;

import quicktime.std.clocks.TimeRecord;

import quicktime.std.movies.Movie;
import quicktime.std.movies.TimeInfo;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.Media;

import com.usermetrix.jclient.UserMetrix;

import java.awt.event.ActionListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.openshapa.views.OpenSHAPADialog;
import org.openshapa.views.continuous.ViewerStateListener;


/**
 * The viewer for a quicktime video file.
 */
public final class QTDataViewer extends OpenSHAPADialog implements DataViewer {

    /** How many milliseconds in a second? */
    private static final int MILLI = 1000;

    /** How many frames to check when correcting the FPS. */
    private static final int CORRECTIONFRAMES = 5;

    // ------------------------------------------------------------------------
    //
    //

    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(QTDataViewer.class);

    // ------------------------------------------------------------------------
    //
    //

    /** The quicktime movie this viewer is displaying. */
    private Movie movie;

    /** The visual track for the above quicktime movie. */
    private Track visualTrack;

    /** The visual media for the above visual track. */
    private Media visualMedia;

    /** Rate for playback. */
    private float playRate;

    /** Frames per second. */
    private float fps;

    /** parent controller. */
    private DataController parent;

    /** The playback offset of the movie in milliseconds. */
    private long offset;

    /** Is the movie currently playing? */
    private boolean playing;

    /** The current video file that this viewer is representing. */
    private File videoFile;

    /** The aspect ratio of the opened video. */
    private double aspectRatio;

    /** Volume slider. */
    private JSlider volumeSlider;

    /** Dialog containing volume slider. */
    private JDialog volumeDialog;

    /** Stores the desired volume the plugin should play at. */
    private float volume = 1f;

    /** Is the plugin visible? */
    private boolean isVisible = true;

    /** The default height of the movie when first loaded. */
    private int fullHeight;

    /** A context menu for resizing the video. */
    private JPopupMenu menuContext = new JPopupMenu();

    /** Menu item for quarter size. */
    private JMenuItem menuItemQuarter;

    /** Menu item for half size. */
    private JMenuItem menuItemHalf;

    /** Menu item for three quarters size. */
    private JMenuItem menuItemThreeQuarters;

    /** Menu item for full size. */
    private JMenuItem menuItemFull;

    /** Icon for displaying volume slider. */
    private final ImageIcon volumeIcon =
            new ImageIcon(getClass().getResource("/icons/audio-volume.png"));

    /** Volume slider icon for when the video is hidden (volume is muted). */
    private final ImageIcon mutedIcon =
            new ImageIcon(getClass().getResource("/icons/volume-muted.png"));

    /** Icon for hiding the video. */
    private final ImageIcon eyeIcon =
            new ImageIcon(getClass().getResource("/icons/eye.png"));

    /** Icon for showing the video. */
    private final ImageIcon hiddenIcon =
            new ImageIcon(getClass().getResource("/icons/eye-shut.png"));

    /** Icon for resizing the video. */
    private final ImageIcon resizeIcon =
            new ImageIcon(getClass().getResource("/icons/resize.png"));

    /** The list of listeners interested in changes made to the project. */
    private final List<ViewerStateListener> viewerListeners =
            new LinkedList<ViewerStateListener>();


    // ------------------------------------------------------------------------
    // [initialization]
    //

    /**
     * Constructor - creates new video viewer.
     */
    public QTDataViewer(java.awt.Frame parent, boolean modal) {

        super(parent, modal);

        try {
            movie = null;
            offset = 0;
            playing = false;
            aspectRatio = 0.0f;

            // Initalise QTJava.
            QTSession.open();

        } catch (Throwable e) {
            logger.error("Unable to create QTVideoViewer", e);
        }

        volumeSlider = new JSlider(JSlider.VERTICAL, 0, 100, 70);
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setName("volumeSlider");
        volumeSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(final ChangeEvent e) {
                    handleVolumeSliderEvent(e);
                }
            });

        volumeDialog = new JDialog(parent, false);
        volumeDialog.setUndecorated(true);
        volumeDialog.setVisible(false);
        volumeDialog.setLayout(new MigLayout("", "[center]", ""));
        volumeDialog.setSize(50, 125);
        volumeDialog.setName("volumeDialog");
        volumeDialog.getContentPane().add(volumeSlider, "pushx, pushy");
        volumeDialog.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(final MouseEvent e) {
                    volumeDialog.setVisible(false);
                }
            });
        volumeDialog.addWindowFocusListener(new WindowAdapter() {
                @Override public void windowLostFocus(final WindowEvent e) {
                    volumeDialog.setVisible(false);
                }
            });

        menuItemQuarter = new JMenuItem("25% size");
        menuItemQuarter.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    scaleVideo(0.25f);
                }
            });
        menuItemHalf = new JMenuItem("50% size");
        menuItemHalf.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    scaleVideo(0.5f);
                }
            });
        menuItemThreeQuarters = new JMenuItem("75% size");
        menuItemThreeQuarters.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    scaleVideo(0.75f);
                }
            });
        menuItemFull = new JMenuItem("100% size");
        menuItemFull.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    scaleVideo(1);
                }
            });
        menuContext.add(menuItemQuarter);
        menuContext.add(menuItemHalf);
        menuContext.add(menuItemThreeQuarters);
        menuContext.add(menuItemFull);
        menuContext.setName("menuContext");

        initComponents();
    }

    private void handleVolumeSliderEvent(final ChangeEvent e) {

        if (movie != null) {
            volume = volumeSlider.getValue() / 100F;
            setVolume();
            notifyChange();
        }

    }

    /**
     * Sets the volume of the movie to the level of the slider bar, or to 0
     * if the track is hidden from view (this means hiding the track mutes
     * the volume).
     */
    private void setVolume() {

        try {

            if (isVisible) {
                movie.setVolume(volume);
            } else {
                movie.setVolume(0F);
            }
        } catch (StdQTException ex) {
            logger.error("Unable to set volume", ex);
        }
    }

    // ------------------------------------------------------------------------
    // [interface] org.openshapa.views.continuous.DataViewer
    //

    /**
     * @return The duration of the movie in milliseconds. If -1 is returned, the
     *         movie's duration cannot be determined.
     */
    public long getDuration() {

        try {

            if (movie != null) {
                return (long) Constants.TICKS_PER_SECOND
                    * (long) movie.getDuration() / movie.getTimeScale();
            }
        } catch (StdQTException ex) {
            logger.error("Unable to determine QT movie duration", ex);
        }

        return -1;
    }

    @Override public void validate() {

        // BugzID:753 - Locks the window to the videos aspect ratio.
        int newHeight = getHeight();
        int newWidth = (int) (getVideoHeight() * aspectRatio)
                + getInsets().left + getInsets().right;
        setSize(newWidth, newHeight);

        super.validate();
    }

    /**
     * Scales the video to the desired percentage.
     * @param scale The new % to scale to, as a float.
     */
    private void scaleVideo(final float scale) {

        int scaleHeight = (int) (fullHeight * scale);

        // BugzID:753 - Locks the window to the videos aspect ratio.
        if ((aspectRatio > 0.0)) {
            int newWidth = (int) (scaleHeight * aspectRatio) + getInsets().left
                + getInsets().right;

            int newHeight = scaleHeight + getInsets().bottom + getInsets().top;

            setSize(newWidth, newHeight);
            this.validate();
        }

        notifyChange();
    }

    public int getVideoHeight() {
        return getHeight() - getInsets().bottom - getInsets().top;
    }

    public int getVideoWidth() {
        return getWidth() - getInsets().left - getInsets().right;
    }

    private void setVideoHeight(int height) {
        if ((aspectRatio > 0.0)) {
            int newWidth = (int) (height * aspectRatio) + getInsets().left
                + getInsets().right;

            int newHeight = height + getInsets().bottom + getInsets().top;

            setSize(newWidth, newHeight);
            this.validate();
        }
    }

    /**
     * @return The playback offset of the movie in milliseconds.
     */
    public long getOffset() {
        return offset;
    }

    /**
     * @param offset
     *            The playback offset of the movie in milliseconds.
     */
    public void setOffset(final long offset) {
        this.offset = offset;
    }

    /**
     * @return The parent JDialog that this data viewer resides within.
     */
    public JDialog getParentJDialog() {
        return this;
    }

    /**
     * Method to open a video file for playback.
     *
     * @param videoFile
     *            The video file that this viewer is going to display to the
     *            user.
     */
    public void setDataFeed(final File videoFile) {
        this.videoFile = videoFile;

        try {
            setTitle(videoFile.getName());

            OpenMovieFile omf = OpenMovieFile.asRead(new QTFile(videoFile));
            movie = Movie.fromFile(omf);
            movie.setVolume(0.7F);

            // Set the time scale for our movie to milliseconds (i.e. 1000 ticks
            // per second.
            movie.setTimeScale(Constants.TICKS_PER_SECOND);

            visualTrack = movie.getIndTrackType(1,
                    StdQTConstants.visualMediaCharacteristic,
                    StdQTConstants.movieTrackCharacteristic);

            // BugzID:1910 - % size calculations should be based on the original
            // movie's size, rather than the quarter-screen restricted size.
            fullHeight = Math.max(movie.getBox().getHeight(),
                                    movie.getBounds().getHeight());

            // Initialise the video to be no bigger than a quarter of the screen
            int hScrnWidth = Toolkit.getDefaultToolkit().getScreenSize().width
                / 2;
            aspectRatio = ((double) movie.getBounds().getWidthF())
                / ((double) movie.getBounds().getHeightF());

            if (movie.getBounds().getWidth() > hScrnWidth) {
                visualTrack.setSize(new QDDimension(hScrnWidth,
                        (int) (hScrnWidth / aspectRatio)));
            }

            visualMedia = visualTrack.getMedia();
            this.add(QTFactory.makeQTComponent(movie).asComponent());

            setName(getClass().getSimpleName() + "-" + videoFile.getName());
            pack();

            // Prevent initial white frame for video on OSX.
            setVisible(true);


            // Set the size of the window to be the same as the incoming video.
            this.setBounds(getX(), getY(), movie.getBox().getWidth(),
                movie.getBox().getHeight());

            // BugzID:928 - FPS calculations will fail when using H264.
            // Apparently the Quicktime for Java API does not support a whole
            // bunch of methods with H264.
            fps = (float) visualMedia.getSampleCount()
                / visualMedia.getDuration() * visualMedia.getTimeScale();

            if ((visualMedia.getSampleCount() == 1.0)
                    || (visualMedia.getSampleCount() == 1)) {
                fps = correctFPS();
            }
        } catch (QTException e) {
            logger.error("Unable to setVideoFile", e);
        }
    }

    /**
     * @return The file used to display this data feed.
     */
    public File getDataFeed() {
        return videoFile;
    }

    /**
     * If there was a problem getting the fps, we use this method to fix it. The
     * first few frames (number of which is specified by CORRECTIONFRAMES) are
     * inspected, with the delay between each measured; the two frames with the
     * smallest delay between them are assumed to represent the fps of the
     * entire movie.
     *
     * @return The best fps found in the first few frames.
     */
    private float correctFPS() {
        float minFrameLength = MILLI; // Set this to one second, as the "worst"
        float curFrameLen = 0;
        int curTime = 0;

        for (int i = 0; i < CORRECTIONFRAMES; i++) {

            try {
                TimeInfo timeObj = visualTrack.getNextInterestingTime(
                        StdQTConstants.nextTimeStep, curTime, 1);
                float candidateFrameLen = timeObj.time - curFrameLen;
                curFrameLen = timeObj.time;
                curTime += curFrameLen;

                if (candidateFrameLen < minFrameLength) {
                    minFrameLength = candidateFrameLen;
                }
            } catch (QTException e) {
                logger.error("Error getting time", e);
            }
        }

        return MILLI / minFrameLength;
    }

    /**
     * Sets parent data controller.
     *
     * @param dataController
     *            The data controller to be set as parent.
     */
    public void setParentController(final DataController dataController) {
        parent = dataController;
    }

    /**
     * @return The frames per second.
     */
    public float getFrameRate() {
        return fps;
    }

    /**
     * @param rate
     *            The playback rate.
     */
    public void setPlaybackSpeed(final float rate) {
        playRate = rate;
    }

    /**
     * Plays the continuous data stream at the current playback rate.
     */
    public void play() {

        try {

            if (movie != null) {
                movie.setRate(playRate);
                playing = true;
            }
        } catch (QTException e) {
            logger.error("Unable to play", e);
        }
    }

    /**
     * Stops the playback of the continuous data stream.
     */
    public void stop() {

        try {

            if (movie != null) {
                movie.stop();
                playing = false;
            }
        } catch (QTException e) {
            logger.error("Unable to stop", e);
        }
    }

    /**
     * @return Is this dataviewer playing the data feed.
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * @param position
     *            Millisecond absolute position for track.
     */
    public void seekTo(final long position) {

        try {

            if (movie != null) {
                TimeRecord time = new TimeRecord(Constants.TICKS_PER_SECOND,
                        position);
                movie.setTime(time);
            }
        } catch (QTException e) {
            logger.error("Unable to find", e);
        }
    }

    /**
     * @return Current time in milliseconds.
     * @throws QTException
     *             If error occurs accessing underlying implementation.
     */
    public long getCurrentTime() throws QTException {
        return movie.getTime();
    }

    /**
     * Get track painter.
     */
    public TrackPainter getTrackPainter() {
        return new DefaultTrackPainter();
    }

    /**
     * Shows an interface for toggling the playback volume.
     *
     * @see org.openshapa.views.continuous.CustomActionListener
     *      #handleActionButtonEvent1(java.awt.event.ActionEvent)
     */
    public void handleActionButtonEvent1(final ActionEvent event) {

        JButton button = (JButton) event.getSource();

        // BugzID:1400 - We don't allow volume changes while the track is
        // hidden from view.
        if (isVisible) {

            // Show the volume frame.
            volumeDialog.setVisible(true);
            volumeDialog.setLocation(button.getLocationOnScreen());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.CustomActionListener
     * #handleActionButtonEvent2(java.awt.event.ActionEvent)
     */
    public void handleActionButtonEvent2(final ActionEvent event) {
        isVisible = !isVisible;
        this.setVisible(isVisible);
        setVolume();
        notifyChange();
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.CustomActionListener
     * #handleActionButtonEvent3(java.awt.event.ActionEvent)
     */
    public void handleActionButtonEvent3(final ActionEvent event) {
        JButton button = (JButton) event.getSource();

        if (isVisible) {
            menuContext.show(button.getParent(), button.getX(), button.getY());
        }
    }

    /** Notifies listeners that a change to the project has occurred. */
    private void notifyChange() {
        for (ViewerStateListener listener : viewerListeners) {
            listener.notifyStateChanged();
        }
    }

    public void loadSettings(final InputStream is) {
        Properties settings = new Properties();

        try {
            settings.load(is);
            String property = settings.getProperty("offset");
            if (property != null & !property.equals("")) {
                setOffset(Long.parseLong(property));
            }
            property = settings.getProperty("volume");
            if (property != null & !property.equals("")) {
                volume = Float.parseFloat(property);
                volumeSlider.setValue((int) (volume * 100));
            }
            property = settings.getProperty("visible");
            if (property != null & !property.equals("")) {
                isVisible = Boolean.parseBoolean(property);
                this.setVisible(isVisible);
                setVolume();
            }
            property = settings.getProperty("height");
            if (property != null & !property.equals("")) {
                setVideoHeight(Integer.parseInt(property));
            }


        } catch (IOException e) {
            logger.error("Error loading settings", e);
        }
    }

    public void storeSettings(final OutputStream os) {
        Properties settings = new Properties();
        settings.setProperty("offset", Long.toString(getOffset()));
        settings.setProperty("volume", Float.toString(volume));
        settings.setProperty("visible", Boolean.toString(isVisible));
        settings.setProperty("height", Integer.toString(getVideoHeight()));

        try {
            settings.store(os, null);
        } catch (IOException e) {
            logger.error("Error saving settings", e);
        }
    }

    public void addViewerStateListener(ViewerStateListener vsl) {
        viewerListeners.add(vsl);
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#getActionButtonIcon1()
     */
    public ImageIcon getActionButtonIcon1() {
        if (isVisible && volume > 0) {
            return volumeIcon;
        } else {
            return mutedIcon;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#getActionButtonIcon2()
     */
    public ImageIcon getActionButtonIcon2() {
        if (isVisible) {
            return eyeIcon;
        } else {
            return hiddenIcon;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#getActionButtonIcon3()
     */
    public ImageIcon getActionButtonIcon3() {
        return resizeIcon;
    }

    // ------------------------------------------------------------------------
    // [generated]
    //

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("QTDataViewerDialog"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(
                    final java.awt.event.WindowEvent evt) {
                    formWindowClosing(evt);
                }
            });

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * Action to invoke when the QTDataViewer window is closing (clean itself
     * up).
     *
     * @param evt
     *            The event that triggered this action.
     */
    private void formWindowClosing(final java.awt.event.WindowEvent evt) { // GEN-FIRST:event_formWindowClosing

        scaleVideo(1);

        try {
            movie.stop();
        } catch (QTException e) {
            logger.error("Couldn't kill", e);
        }

        volumeDialog.setVisible(false);

        parent.shutdown(this);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
