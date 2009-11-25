package org.openshapa.views.continuous.sound;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.apache.log4j.Logger;
import org.openshapa.OpenSHAPA;
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
    /** The audio media for the above audio track. */
    private Media audioMedia;
    /** The media handler for the audio media. */
    private AudioMediaHandler audioMH;

    /** Preprocessing audio file. */
    // private Movie audio;
    /** Preprocessing audio track. */
    // private Track audioTrack;
    /** Preprocessing audio media. */
    // private Media audioMedia;
    /** Preprocessing media handler. */
    // private AudioMediaHandler audioMH;

    /** An instance of the class LevelMeter, which draws the meter bars for the
     * sound levels. */
    private LevelMeter meter;
    /** The number of milliseconds between each redraw of the LevelMeter canvas.
     */
    /** This is the preprocessing thread which generates teh audioData array. */
    private PreProcess p;
    /** The number of milliseconds to wait between painting operations. */
    private static final int REPAINTDELAY = 20;
    /** Constant value used to calculate percentages. */
    private static final int CENT = 100;
    /** Time to restore after painting the display on a seek operation. */
    private float playRate;
    /** Frames per second. */
    private float fps;
    /** This is the timer for repainting the equaliser (LevelMeter). */
    private Timer t;
    /** This is the second timer for repainting the equaliser (LevelMeter). */
    private Timer t2;
    /** parent controller. */
    private DataController parent;
    /** Number of millis to jump back upon losing sync during preprocessing. */
    private static final int JUMPFIX = 20;
    /** Massive array with all the audio intensity data stored. */
    private int[] audioData;
    /** Maximum filesize in milliseconds that will be preprocessed. */
    private static final int MAXFILESIZE = 6 /* <-secs */ * 1000;
    /** Previous window title. */
    private String wTitle;
    /** Prefix attached to preprocessing window title information. */
    private String pfix = "";
    /** Suffix for preprocessing window title information. */
    private String sfix = "Finished preprocessing. ";


    /** The visual track for the above quicktime movie. */
    private Track visualTrack;

    /** The visual media for the above visual track. */
    private Media visualMedia;

    private Component comp;

    private JFrame equal;

    /** The preprocessing rate.
     * Of the entire playback time,
     * 1F takes 100% (safe but slow)
     * 2F takes 83%         (appears to corrupt data ...
     * 4F takes 70%         ...
     * 8F takes 73%         ...
     * 16F takes >> 100%    ...
     * 32F doesn't work at all.
     *
     * This quadratic effect (local maximum in performance at 4F) comes about
     * as a result of the syncronisation method, which stops the audio file from
     * dropping frames. Removing this would allow for much faster preprocessing,
     * but at the cost of increasingly high fidelity loss at high preprocessing
     * rates.
     */
    private static final float PREPROCESSRATE = 1F;


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

    /** Preprocessing class. Grabs all the audio data at the start. */
    class PreProcess implements Runnable {

        /** The number of frequency bands used. */
        private int numBands;
        /** The amount of milliseconds audio processing left to do. */
        private int work = 0;
        /** A temporary array populated with the intensity data. */
        private int[] tempLevels;
        /** The time recorded on the last loop call. */
        private long oldTime = 0;
        /** Stores the previous volume level. */
        private float volume;
        /** Constructor for the preprocessor.
         *  @param bands The number of frequency bands used.
         */
        private boolean terminate = false;

        /**
         * This is a separate thread which runs the preprocessing.
         * @param bands The number of frequency bands to be used.
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

        /**
         * Asks the thread to terminate.
         * @throws QTException if audio file couldn't be stopped.
         */
        public void die() throws QTException {
            /*
            audio.stop();
            audio.disposeQTObject();
            */
            terminate = true;
        }

        /** Grabs all the data from the audio stream. */
        public void run() {
            wTitle = getTitle();
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
                audio.setVolume(0F);
                audio.setRate(PREPROCESSRATE);
            } catch (QTException e) {
                logger.error("Can't set rate", e);
            }
            int i = 0;
            int check = 0;
            boolean success = true;
            while (i < (work - 1)) {
                try {
                    long atime;
                    try {
                    atime = getCurrentTime();
                    } catch (QTException e) {
                        logger.error("Couldn't get time", e);
                        atime = 0;
                    }
                    if (atime != oldTime) { // We have a new timecode
                        oldTime = atime;
                        while ((atime - 1) * numBands != i) {
                            // If we fell out of sync...
                            long fixedJ = Math.max(1, atime - JUMPFIX);
                            if ((atime - 1) * numBands > i) {
                                // Rewind if we went too far,
                                TimeRecord fixtime = new TimeRecord(
                                        Constants.TICKS_PER_SECOND, fixedJ);
                                audio.setTime(fixtime);
                            }
                            atime = getCurrentTime();
                            // Else keep waiting until the movie catches up.
                        } try {
                            tempLevels =
                            audioMH.getSoundEqualizerBandLevels(numBands);
                        } catch (QTException e) {
                            String s = i + " Levels unavailable.";
                            success = false;
                            logger.error(s, e);
                            tempLevels = new int[numBands];
                            for (int k = 0; k < numBands; k++) {
                                tempLevels[k] = 0;
                            }
                            throw new Exception("Bad sound level");
                        }
                        for (int j = 0; j < numBands; j++) {
                            audioData[i] = tempLevels[j];
                            if (tempLevels[j] != 0) {
                                check++;
                            }
                            i++;
                        }
                        setTitle(pfix
                                + "Preprocessing... "
                                + i * CENT / work + "% "
                                + wTitle);
                    }
                } catch (QTException e) {
                    String s = i + " Quicktime error!";
                    success = false;
                    logger.error(s, e);
                    i = work - 1;
                    pfix = "PREPROCESS FAILED!";
                    sfix = " ";
                } catch (Exception f) {
                    logger.error(f.toString(), f);
                    i = work - 1;
                    pfix = "PREPROCESS FAILED!";
                    sfix = " ";
                }
                if (terminate) {
                    return;
                }
            }
            System.out.println("Check = " + check);
            meter.setAudioData(audioData);
            setTitle(pfix + sfix + wTitle);
            try {
                audio.stop();
                audio.setRate(0F);
                audio.setVolume(volume);
                audio.setTime(new
                        TimeRecord(Constants.TICKS_PER_SECOND, 0));
            } catch (QTException e) {
                logger.error("Couldn't reset audio", e);
            }
            if (!success) {
                JLabel failMsg = new JLabel("         "
                        + " Couldn't get sound intensity data."
                        + " Try converting to .mov first.            ");
                getContentPane().setLayout(new FlowLayout());
                getContentPane().add(failMsg);
                remove(meter);
                pack();
            }
            // equal.setVisible(false);
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
                meter.setAudioTime(getCurrentTime());
                meter.repaint();
                } catch (QTException e) {
                    logger.error("Couldn't get time", e);
                }
            }

        }
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


            visualTrack = audio.getIndTrackType(1,
                                       StdQTConstants.visualMediaCharacteristic,
                                       StdQTConstants.movieTrackCharacteristic);

            audio.getIndTrackType(1,
                                    StdQTConstants.visualMediaCharacteristic,
                                    StdQTConstants.movieTrackCharacteristic);

            // Initialise the video to be no bigger than a quarter of the screen
            int hScrnWidth = Toolkit.getDefaultToolkit()
                                    .getScreenSize().width / 2;
            if (audio.getBounds().getWidth() > hScrnWidth) {
                float aspectRatio = audio.getBounds().getWidthF()
                                    / audio.getBounds().getHeightF();
                visualTrack.setSize(new QDDimension(hScrnWidth,
                                                    hScrnWidth / aspectRatio));
            }

            // Add the component that "renders" the audio.
            meter = new LevelMeter();

            equal = new JFrame("Equaliser");


            //equal.add(meter);
            add(meter);
            OpenSHAPA.getApplication().show(equal);


            comp = QTFactory.makeQTComponent(audio).asComponent();
            equal.add(comp);


            // set up repainting timer
            t = new Timer();

            t.schedule(new PaintTask(), 0, REPAINTDELAY);

            this.invalidate();

            p = new PreProcess(meter.getNumBands());

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

        try {
            p.die();
            Thread.sleep(1);
            audio.stop();
            removeAll();
            t.cancel();
        } catch (QTException e) {
            logger.error("Couldn't kill file", e);
        } catch (InterruptedException f) {
            logger.error("Couldn't sleep", f);
        }
        equal.dispose();
        this.parent.shutdown(this);
    }//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
