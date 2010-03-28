package org.openshapa.views.continuous.quicktime;

import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFrame;

import org.openshapa.util.Constants;
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

/**
 * The viewer for a quicktime video file.
 */
public final class QTDataViewer extends JFrame implements DataViewer {

    // --------------------------------------------------------------------------
    //
    //

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(QTDataViewer.class);

    /** How many milliseconds in a second? */
    private static final int MILLI = 1000;

    /** How many frames to check when correcting the FPS. */
    private static final int CORRECTIONFRAMES = 5;

    // --------------------------------------------------------------------------
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
    private float aspectRatio;

    /** Has the size of the window had its aspect ratio corrected? */
    private boolean updatedAspect;

    // --------------------------------------------------------------------------
    // [initialization]
    //

    /**
     * Constructor - creates new video viewer.
     */
    public QTDataViewer() {
        try {
            movie = null;
            offset = 0;
            playing = false;
            aspectRatio = 0.0f;
            updatedAspect = false;

            // Initalise QTJava.
            QTSession.open();

        } catch (Throwable e) {
            logger.error("Unable to create QTVideoViewer", e);
        }
        initComponents();
    }

    // --------------------------------------------------------------------------
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

    @Override
    public void validate() {
        // BugzID:753 - Locks the window to the videos aspect ratio.
        if (aspectRatio > 0.0 && !updatedAspect) {
            setSize((int) (getHeight() * aspectRatio), getHeight());
            invalidate();
            updatedAspect = true;
        } else {
            updatedAspect = false;
        }
        super.validate();
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
     * @return The parent JFrame that this data viewer resides within.
     */
    public JFrame getParentJFrame() {
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

            // Set the time scale for our movie to milliseconds (i.e. 1000 ticks
            // per second.
            movie.setTimeScale(Constants.TICKS_PER_SECOND);

            visualTrack = movie.getIndTrackType(1,
                    StdQTConstants.visualMediaCharacteristic,
                    StdQTConstants.movieTrackCharacteristic);

            // Initialise the video to be no bigger than a quarter of the screen
            int hScrnWidth = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
            aspectRatio = movie.getBounds().getWidthF()
                    / movie.getBounds().getHeightF();

            if (movie.getBounds().getWidth() > hScrnWidth) {
                visualTrack.setSize(new QDDimension(hScrnWidth, hScrnWidth
                        / aspectRatio));
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

            if (visualMedia.getSampleCount() == 1.0
                    || visualMedia.getSampleCount() == 1) {
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
     * Plays the continous data stream at the current playback rate..
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
     * Stops the playback of the continous data stream.
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

    // --------------------------------------------------------------------------
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
            @Override
            public void windowClosing(final java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Action to invoke when the QTDataViewer window is closing (clean itself
     * up).
     *
     * @param evt
     *            The event that triggered this action.
     */
    private void formWindowClosing(final java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosing
        try {
            movie.stop();
        } catch (QTException e) {
            logger.error("Couldn't kill", e);
        }
        parent.shutdown(this);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
