package org.openshapa.views.continuous.quicktime;

import org.openshapa.views.continuous.*;
import java.io.File;
import javax.swing.JFrame;
import org.apache.log4j.Logger;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.app.view.QTFactory;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.std.StdQTConstants;
import quicktime.std.clocks.TimeRecord;
import quicktime.std.movies.Movie;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.Media;
import quicktime.std.movies.media.SampleTimeInfo;

/**
 * The viewer for a quicktime video file.
 */
public final class QTVideoViewer extends JFrame
implements DataViewer {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(QTVideoViewer.class);

    /** The quicktime movie this viewer is displaying. */
    private Movie movie;

    /** The visual track for the above quicktime movie. */
    private Track visualTrack;

    /** The visual media for the above visual track. */
    private Media visualMedia;

    /** The "normal" playback speed. */
    private static final float NORMAL_SPEED = 1.0f;

    /** Fastforward playback speed. */
    private static final float FFORWARD_SPEED = 32.0f;

    /** Rewind playback speed. */
    private static final float RWIND_SPEED = -32.0f;

    /** conversion factor for converting milliseconds to seconds. */
    private static final double MILLI_TO_SECONDS = 0.001;

    /** conversion factor for converting seconds to milliseconds. */
    private static final double SECONDS_TO_MILLI = 1000.0;

    /** The current shuttle speed. */
    private float shuttleSpeed;

    /**
     * Constructor - creates new video viewer.
     *
     * @param controller The controller invoking actions on this continous
     * data viewer.
     */
    public QTVideoViewer() {
        try {
            movie = null;
            shuttleSpeed = 0.0f;

            // Initalise QTJava.
            QTSession.open();
        } catch (QTException e) {
            logger.error("Unable to create QTVideoViewer", e);
        }
        initComponents();
    }

    public JFrame getParentJFrame() {
        return this;
    }

    /**
     * Method to open a video file for playback.
     *
     * @param videoFile The video file that this viewer is going to display to
     * the user.
     */
    public void setDataFeed(final File videoFile) {
        try {
            this.setTitle(videoFile.getName());
            OpenMovieFile omf = OpenMovieFile.asRead(new QTFile(videoFile));
            movie = Movie.fromFile(omf);
            visualTrack = movie.getIndTrackType(1,
                                       StdQTConstants.visualMediaCharacteristic,
                                       StdQTConstants.movieTrackCharacteristic);
            visualMedia = visualTrack.getMedia();

            this.add(QTFactory.makeQTComponent(movie).asComponent());
            // Set the size of the window to be the same as the incoming video.
            this.setBounds(this.getX(), this.getY(),
                           movie.getBox().getWidth(),
                           movie.getBox().getHeight());
            this.pack();
            this.invalidate();
            this.setVisible(true);

            setName(this.getClass().getSimpleName() + videoFile.getName());
        } catch (QTException e) {
            logger.error("Unable to setVideoFile", e);
        }
    }

    /**
     * Jogs the data stream backwards by a single unit (i.e. frame for movie)
     */
    public void jogBack() {
        try {
            this.setVisible(true);
            this.jog(-1);
        } catch (QTException e) {
            logger.error("Unable to jogBack", e);
        }
    }

    /**
     * Stops the playback of the continous data stream.
     */
    public void stop() {
        try {
            if (movie != null) {
                this.setVisible(true);
                shuttleSpeed = 0.0f;
                movie.stop();
            }
        } catch (QTException e) {
            logger.error("Unable to stop", e);
        }
    }

    /**
     * Jogs the data stream forwards by a single unit (i.e. frame for movie).
     */
    public void jogForward() {
        try {
            this.setVisible(true);
            this.jog(1);
        } catch (QTException e) {
            logger.error("Unable to jogForward", e);
        }
    }

    /**
     * Shuttles the video stream backwards by the current shuttle speed.
     * Repetative calls to shuttleBack increases the speed at which we reverse.
     */
    public void shuttleBack() {
        try {
            if (movie != null) {
                this.setVisible(true);
                if (shuttleSpeed == 0.0f) {
                    shuttleSpeed = 1.0f / RWIND_SPEED;
                } else {
                    shuttleSpeed = shuttleSpeed * 2;
                }
                movie.setRate(shuttleSpeed);
            }
        } catch (QTException e) {
            logger.error("Unable to shuttleBack", e);
        }
    }

    /**
     * Pauses the playback of the continous data stream.
     */
    public void pause() {
        try {
            if (movie != null) {
                this.setVisible(true);
                shuttleSpeed = 0.0f;
                movie.stop();
            }
        } catch (QTException e) {
            logger.error("pause", e);
        }
    }

    /**
     * Shuttles the video stream forwards by the current shuttle speed.
     * Repetative calls to shuttleFoward increases the speed at which we fast
     * forward.
     */
    public void shuttleForward() {
        try {
            if (movie != null) {
                this.setVisible(true);
                if (shuttleSpeed == 0.0f) {
                    shuttleSpeed = 1.0f / FFORWARD_SPEED;
                } else {
                    shuttleSpeed = shuttleSpeed * 2;
                }
                movie.setRate(shuttleSpeed);
            }
        } catch (QTException e) {
            logger.error("Unable to shuttleForward", e);
        }
    }

    /**
     * Rewinds the continous data stream at a speed 32x normal.
     */
    public void rewind() {
        try {
            if (movie != null) {
                this.setVisible(true);
                shuttleSpeed = 0.0f;
                movie.setRate(RWIND_SPEED);
            }
        } catch (QTException e) {
            logger.error("Unable to rewind", e);
        }
    }

    /**
     * Plays the continous data stream at a regular 1x normal speed.
     */
    public void play() {
        try {
            if (movie != null) {
                this.setVisible(true);
                shuttleSpeed = 0.0f;
                movie.setRate(NORMAL_SPEED);
            }
        } catch (QTException e) {
            logger.error("Unable to play", e);
        }
    }

    /**
     * Fast forwards a continous data stream at a speed 32x normal.
     */
    public void forward() {
        try {
            if (movie != null) {
                this.setVisible(true);
                shuttleSpeed = 0.0f;
                movie.setRate(FFORWARD_SPEED);
            }
        } catch (QTException e) {
            logger.error("Unable to forward", e);
        }
    }

    /**
     * Find can be used to seek within a continous data stream - allowing the
     * caller to jump to a specific time in the datastream.
     *
     * @param milliseconds The time within the continous data stream, specified
     * in milliseconds from the start of the stream.
     */
    public void find(final long milliseconds) {
        try {
            if (movie != null) {
                this.setVisible(true);
                shuttleSpeed = 0.0f;
                movie.stop();

                double seconds = milliseconds * MILLI_TO_SECONDS;
                long qtime = (long) seconds * movie.getTimeScale();

                TimeRecord time = new TimeRecord(movie.getTimeScale(), qtime);
                movie.setTime(time);
                pack();
            }
        } catch (QTException e) {
            logger.error("Unable to find", e);
        }
    }

    /**
     * Go back by the specified number of milliseconds and continue playing the
     * data stream.
     *
     * @param milliseconds The number of milliseconds to jump back by.
     */
    public void goBack(final long milliseconds) {
        try {
            if (movie != null) {
                this.setVisible(true);
                shuttleSpeed = 0.0f;
                movie.stop();

                double curTime = movie.getTime() / (float) movie.getTimeScale();
                double seconds = milliseconds * MILLI_TO_SECONDS;

                seconds = curTime - seconds;
                long qtime = (long) seconds * movie.getTimeScale();

                TimeRecord time = new TimeRecord(movie.getTimeScale(), qtime);
                movie.setTime(time);
                movie.start();
                pack();
            }
        } catch (QTException e) {
            logger.error("Unable to go back", e);
        }
    }

    public void syncCtrl() {
    }

    public void sync() {
    }

    /**
     * Jogs the movie by a specified number of frames.
     *
     * @param offset The number of frames to jog the movie by.
     *
     * @throws QTException If unable to jog the movie by the specified number
     * of frames.
     */
    public void jog(final int offset) throws QTException {
        if (movie != null) {
            this.setVisible(true);
            shuttleSpeed = 0.0f;
            movie.stop();

            // Get the current frame.
            SampleTimeInfo sTime = visualMedia.timeToSampleNum(movie.getTime());

            // Get the time of the next frame.
            int t = visualMedia
                    .sampleNumToMediaTime(sTime.sampleNum + offset).time;
            TimeRecord time = new TimeRecord(movie.getTimeScale(), t);

            // Advance the movie to the next frame.
            movie.setTime(time);
        }
    }

    public long getCurrentTime() throws QTException {
        double curTime = movie.getTime() / (double) movie.getTimeScale();
        curTime = curTime * SECONDS_TO_MILLI;
        return (long) curTime;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setName("Form"); // NOI18N

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The action to invoke when the user closes the viewer.
     *
     * @param evt The event that triggered this action.
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
