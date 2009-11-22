package org.openshapa.views.continuous.sound;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import org.apache.log4j.Logger;
import org.openshapa.util.Constants;
import org.openshapa.views.continuous.DataController;
import org.openshapa.views.continuous.DataViewer;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.std.StdQTConstants;
import quicktime.std.clocks.TimeRecord;
import quicktime.std.movies.Movie;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.AudioMediaHandler;
import quicktime.std.movies.media.Media;


/**
 * The viewer for an audio file.
 */
public final class SoundDataViewer extends JFrame
        implements DataViewer {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SoundDataViewer.class);
    /** The quicktime movie this viewer is displaying. */
    private Movie audio;
    /** The audio track for the above quicktime movie. */
    private Track audioTrack;
    /** The audio media for the above visual track. */
    private Media audioMedia;
    /** The media handler for the audio media. */
    private AudioMediaHandler audioMediaHandler;
    /** An instance of the class LevelMeter, which draws the meter bars for the
     * sound levels.
     */
    private LevelMeter meter;
    /** The number of milliseconds between each redraw of the LevelMeter canvas.
     */
    private static final int REPAINTDELAY = 20;
    /** Time to restore after painting the display on a seek operation. */
    private float playRate;
    /** Frames per second. */
    private float fps;
    /** This is the timer for repainting the equaliser (LevelMeter). */
    private Timer t;
    /** parent controller. */
    private DataController parent;


    /**
     * Constructor - creates new audio viewer.
     */
    public SoundDataViewer() {
        try {
            audio = null;

            // Initalise QTJava.
            QTSession.open();

        } catch (QTException e) {
            logger.error("Unable to create SoundViewer", e);
        }
        initComponents();
    }

    //--------------------------------------------------------------------------
    // [interface] org.openshapa.views.continuous.DataViewer
    //
    /**
     * @return The parent JFrame that this data viewer resides within.
     */
    public JFrame getParentJFrame() {
        return this;
    }

    /**
     * Method to open a video file for playback.
     *
     * @param audioFile The audio file that this viewer is going to display to
     * the user.
     */
    public void setDataFeed(final File audioFile) {
        try {
            this.setTitle(audioFile.getName());
            OpenMovieFile omf = OpenMovieFile.asRead(new QTFile(audioFile));
            audio = Movie.fromFile(omf);

            // Set the time scale for our movie to milliseconds (i.e. 1000 ticks
            // per second.
            audio.setTimeScale(Constants.TICKS_PER_SECOND);


            audioTrack = audio.getIndTrackType(1,
                    StdQTConstants.audioMediaCharacteristic,
                    StdQTConstants.movieTrackCharacteristic);
            audioMedia = audioTrack.getMedia();


            // Calculate frames per second for the audio data.
            fps = (float) audioMedia.getSampleCount() / audioMedia.getDuration()
                    * audioMedia.getTimeScale();


            audioMediaHandler = (AudioMediaHandler) audioMedia.getHandler();

            // Add the component that "renders" the audio.
            meter = new LevelMeter(audioMediaHandler);
            add(meter);

            /**
             * Handles the repainting of the LevelMeter class.
             */
            class PaintTask extends TimerTask {

                /**
                 * This is the standard run method, and simply paints.
                 */
                public void run() {
                    if (meter.isReady()) {
                        meter.setNeedNew((playRate != 0));
                        meter.repaint();
                    }

                }
            }

            // set up repainting timer
            t = new Timer();

            t.schedule(new PaintTask(), 0, REPAINTDELAY);



            setName(getClass().getSimpleName() + "-" + audioFile.getName());
            this.invalidate();
            this.setVisible(true);


        } catch (QTException e) {
            logger.error("Unable to set audioFile", e);
        }
    }

    /**
     * Sets the parent data controller.
     * @param dataController This is the passed in data controller (parent).
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
     * @param rate The playback rate.
     */
    public void setPlaybackSpeed(final float rate) {
        this.playRate = rate;
    }

    /**
     * Plays the continous data stream at the current playback rate..
     */
    public void play() {
        try {
            if (audio != null) {
                audio.setRate(playRate);
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
            if (audio != null) {
                audio.stop();
            }
        } catch (QTException e) {
            logger.error("Unable to stop", e);
        }
    }

    /**
     * @param position Millisecond absolute position for track.
     */
    public void seekTo(final long position) {
        try {
            if (audio != null) {
                TimeRecord time = new TimeRecord(Constants.TICKS_PER_SECOND,
                        position);
                audio.setTime(time);
            }
        } catch (QTException e) {
            logger.error("Unable to find", e);
        }
    }


    /**
     * @return Current time in milliseconds.
     *
     * @throws QTException If error occurs accessing underlying implementation.
     */
    public long getCurrentTime() throws QTException {
        return audio.getTime();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.parent.shutdown(this);
    }//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
