package org.openshapa.views.continuous.sound;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JDialog;
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
import quicktime.std.movies.media.MediaEQSpectrumBands;


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
    private AudioMediaHandler audioMH;
    /** An instance of the class LevelMeter, which draws the meter bars for the
     * sound levels.
     */
    private LevelMeter meter;
    /** The number of milliseconds between each redraw of the LevelMeter canvas.
     */
    private static final int REPAINTDELAY = 20;
    /** Constant value used to calculate percentages. */
    private static final int CENT = 100;
    /** Time to restore after painting the display on a seek operation. */
    private float playRate;
    /** Frames per second. */
    private float fps;
    /** This is the timer for repainting the equaliser (LevelMeter). */
    private Timer t;
    /** parent controller. */
    private DataController parent;
    /** Number of millis to jump back upon losing sync during preprocessing. */
    private static final int JUMPFIX = 20;
    /** Massive array with all the audio intensity data stored. */
    private int[] audioData;
    /** Maximum filesize in milliseconds that will be preprocessed. */
    private static final int MAXFILESIZE = 6000;
    /** Prefix attached to preprocessing window title information. */
    private String pfix = "";


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
            audioMH = (AudioMediaHandler) audioMedia.getHandler();
            // Add the component that "renders" the audio.
            meter = new LevelMeter();
            add(meter);

            /** Preprocessing class. Grabs all the audio data at the start. */
            class PreProcess implements Runnable {

                private int numBands;
                private int work = 0;
                private int[] tempLevels;
                private long oldTime = 0;
                private int load = 0;
                private float volume;

                /** Constructor for the preprocessor.
                 *  @param bands The number of frequency bands used.
                 */
                public PreProcess(final int bands) {
                    numBands = bands;

                    try {
                        if (audio.getDuration() < MAXFILESIZE) {
                            work = audio.getDuration() * numBands;
                        } else {
                            pfix = "WARNING: File too large! ";
                            work = MAXFILESIZE * numBands;
                        }

                    } catch (QTException e) {
                        logger.error("Couldn't get duration", e);
                    }
                    Thread thread = new Thread(this);
                    thread.start();
                }

                /** Grabs all the data from the audio stream. */
                public void run() {
                    audioData = new int[work];
                    MediaEQSpectrumBands bands =
                            new MediaEQSpectrumBands(numBands);
                    try {
                        for (int i = 0; i < numBands; i++) {
                        bands.setFrequency(i, 0); // Not actually zero!
                        audioMH.setSoundEqualizerBands(bands);
                        audioMH.setSoundLevelMeteringEnabled(true);
                        }
                        volume = audio.getVolume();
                        // audio.setVolume(0F);
                        audio.setRate(1F);
                    } catch (QTException e) {
                        logger.error("Can't set rate", e);
                    }
                    int i = 0;
                    while (i < work - 1) {
                        try {
                            long atime;
                            try {
                            atime = getCurrentTime();
                            } catch (QTException e) {
                                atime = 0;
                            }
                            if (atime != oldTime) {
                                oldTime = atime;
                                while ((atime - 1) * numBands != i) {
                                    load++;
                                    long fixedJ = Math.max(1, atime - JUMPFIX);
                                    if ((atime - 1) * numBands > i) {
                                        TimeRecord fixtime = new TimeRecord(
                                                Constants.TICKS_PER_SECOND,
                                                fixedJ);
                                        audio.setTime(fixtime);
                                    }
                                    atime = getCurrentTime();
                                } try {
                                    tempLevels =
                                    audioMH.
                                       getSoundEqualizerBandLevels(numBands);
                                } catch (QTException e) {
                                    String s = i + " Levels unavailable.";
                                    logger.error(s, e);
                                    tempLevels = new int[numBands];
                                    for (int k = 0; k < numBands; k++) {
                                        tempLevels[k] = 0;
                                    }

                                }

                                for (int j = 0; j < numBands; j++) {
                                    audioData[i] = tempLevels[j];
                                    i++;
                                }
                                setTitle(pfix
                                        + "Preprocessing... "
                                        + i * CENT / work + "%");
                            }
                        } catch (QTException e) {
                            // logger.error("Couldn't get levels", e);
                            String s = i + " Unknown quicktime error!";
                            logger.error(s, e);
                            i = work;
                            pfix = "PREPROCESS FAILED! ";
                        }
                    }
                    meter.setAudioData(audioData);
                    setTitle(pfix + "Finished preprocessing audio.");
                    try {
                        audio.stop();
                        audio.setRate(0F);
                        audio.setVolume(volume);
                        audio.setTime(new
                                TimeRecord(Constants.TICKS_PER_SECOND, 0));
                    } catch (QTException e) {
                        logger.error("Couldn't reset audio", e);
                    }

                }

            }

            /**
             * Handles the repainting of the LevelMeter class.
             */
            class PaintTask extends TimerTask {

                /**
                 * Paints the level meter if it has data to draw.
                 */
                public void run() {
                    if (meter.isReady()) {
                        try {
                        // meter.setNeedNew((playRate != 0));
                        meter.setAudioTime(getCurrentTime());
                        meter.repaint();
                        } catch (QTException e) {
                            logger.error("Couldn't get time", e);
                        }
                    }

                }
            }

            // set up repainting timer
            t = new Timer();

            t.schedule(new PaintTask(), 0, REPAINTDELAY);

            PreProcess p = new PreProcess(meter.getNumBands());

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
     * @param position Millisecond absolute position for track.
     */
    public void sync(final long position) {
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
