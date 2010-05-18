package org.openshapa.views.continuous.sound;

import java.awt.Component;
import java.awt.event.ActionEvent;

import java.io.File;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.openshapa.util.Constants;

import org.openshapa.views.component.DefaultTrackPainter;
import org.openshapa.views.component.TrackPainter;
import org.openshapa.views.continuous.DataController;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.quicktime.QTFilter;

import quicktime.QTException;
import quicktime.QTSession;

import quicktime.app.view.QTFactory;

import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;

import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;

import quicktime.std.clocks.TimeRecord;

import quicktime.std.movies.Movie;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.AudioMediaHandler;
import quicktime.std.movies.media.Media;
import quicktime.std.movies.media.MediaEQSpectrumBands;

import com.usermetrix.jclient.UserMetrix;


/**
 * The viewer for an audio file.
 */
public final class SoundDataViewer extends JFrame implements DataViewer {

    /** The scaling factor for audio preprocessing; # of samples is divided by
     *  this factor. */
    private static final int SCALING = 1;

    /** The number of milliseconds to wait between painting operations. */
    private static final int REPAINTDELAY = 100;

    /** Constant value used to calculate percentages. */
    private static final int CENT = 100;

    /** Number of millis to jump back upon losing sync during preprocessing. */
    private static final int JUMPFIX = 1000;

    /** Maximum filesize in milliseconds that will be preprocessed.
     * For some reason, it is now unsafe to make this as high as 120, although
     * with the advent of the preprocessing window this shouldn't matter. */
    private static final int MAXFILESIZE = 40 /* <-secs */ * 1000 / SCALING;

    /** The time in milliseconds to wait for the video to draw. */
    private static final int VIDEO_DRAW_DELAY = 100;

    /** The amount of extra window size to give the window to hide the movie. */
    private static final int EXTRA_SIZE = 300;

    /** The offset used to hide the movie. */
    private static final int HIDDEN_OFFSET = 200;

    /** The threshold in millis for the movie to be considered out of sync. */
    private static final int SYNCTHRESH = 100;

    /** The fixed window width. */
    private static final int WIN_X = 400;

    /** The fixed window height. */
    private static final int WIN_Y = 400;

    /** The error window height. */
    private static final int ERROR_HEIGHT = 200;

    /** The number of times the delay sequence should be called; this allows
     * the preprocessing movie to start playing. */
    private static final int DELAYTICKS = 3;

    /** This is the number of delay calls that we make each tick; higher values
     * result in a longer initial delay to wait for the movie. */
    private static final int DELAYVAL = 1000000;

    /** The preprocessing rate. */
    private static final float PREPROCESSRATE = 1F;

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(SoundDataViewer.class);

    /** The quicktime movie this viewer is displaying. */
    private Movie audio;

    /** The audio track for the above quicktime movie. */
    private Track audioTrack;

    /** The audio media for the above audio track. */
    private Media audioMedia;

    /** Preprocessing audio file. */
    private Movie paudio;

    /** Preprocessing audio track. */
    private Track paudioTrack;

    /** Preprocessing audio media. */
    private Media paudioMedia;

    /** Preprocessing media handler. */
    private AudioMediaHandler paudioMH;

    /** An instance of the class LevelMeter, which draws the meter bars for the
     * sound levels. */
    private LevelMeter meter;

    /** This is the preprocessing thread which generates the audioData array. */
    private PreProcess preThread;

    /** Time to natural after painting the display on a seek operation. */
    private float playRate;

    /** Frames per second. */
    private float fps;

    /** This is the timer for repainting the equaliser (LevelMeter). */
    private Timer t;

    /** parent controller. */
    private DataController parent;

    /** Massive array with all the audio intensity data stored. */
    private int[] audioData;

    /** Previous window title. */
    private String wTitle;

    /** Prefix attached to preprocessing window title information. */
    private String pfix = "";

    /** Suffix for preprocessing window title information. */
    private String sfix = "Finished preprocessing. ";

    /** The component holding the movie being played. */
    private Component comp;

    /** The component holding the preprocess duplicate of the movie. */
    private Component preComp;

    /** A collection of internal frames, used to hide the movie. */
    private JDesktopPane superDesk;

    /** The frame holding the movie component. */
    private MyInternalFrame movieFrame;

    /** The frame holding the preprocess component. */
    private MyInternalFrame preFrame;

    /** The frame holding the equaliser. */
    private MyInternalFrame equalFrame;

    /** Determines whether or not preprocessing has finished successfully. */
    private boolean finishedPreprocess = false;

    /** Records whether or not we have opened a movie. */
    private boolean isMovie;

    /** Playback offset. */
    private long offset;

    private boolean playing;

    private File audioFile;

    /**
     * Constructor - creates new audio viewer.
     */
    public SoundDataViewer() {

        try {
            audio = null;
            offset = 0;
            playing = false;

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

    /**
     * @return The duration of the audio in milliseconds. If -1 is returned, the
     * audio's duration cannot be determined.
     */
    public long getDuration() {

        try {

            if (audio != null) {
                return (long) Constants.TICKS_PER_SECOND
                    * (long) audio.getDuration() / audio.getTimeScale();
            }
        } catch (StdQTException ex) {
            logger.error("Unable to determine QT audio duration", ex);
        }

        return -1;
    }

    /**
     * @return The duration of the audio in milliseconds. If -1 is returned, the
     * audio's duration cannot be determined.
     */
    public long getPreDuration() {

        try {

            if (paudio != null) {
                return Constants.TICKS_PER_SECOND * paudio.getDuration()
                    / paudio.getTimeScale();
            }
        } catch (StdQTException ex) {
            logger.error("Unable to determine QT paudio duration", ex);
        }

        return -1;
    }

    /**
     * @return The playback offset of the audio in milliseconds.
     */
    public long getOffset() {
        return offset;
    }

    /**
     * @param newOffset The playback offset of the audio in milliseconds.
     */
    public void setOffset(final long newOffset) {
        assert (newOffset >= 0);
        offset = newOffset;
    }

    /** @return This frame. */
    public JFrame getParentJFrame() {
        return this;
    }

    /**
     * Method to open a video file for playback.
     *
     * @param newAudioFile The audio file the viewer is going to display to
     * the user.
     */
    public void setDataFeed(final File newAudioFile) {
        audioFile = newAudioFile;

        try {
            setSize(WIN_X, WIN_Y);

            /* Set resizable false here to avoid nasty QuickTime movie frames
            being drawn when the user resizes the window. Other workarounds
            possible, but most are messy. */
            setResizable(false);
            setTitle(audioFile.getName());

            QTFilter movieTest = new QTFilter();
            isMovie = movieTest.accept(audioFile);

            OpenMovieFile omf = OpenMovieFile.asRead(new QTFile(audioFile));
            audio = Movie.fromFile(omf);


            /* Set the time scale for the movie to milliseconds (i.e. 1000 ticks
            per second. */
            audio.setTimeScale(Constants.TICKS_PER_SECOND);


            audioTrack = audio.getIndTrackType(1,
                    StdQTConstants.audioMediaCharacteristic,
                    StdQTConstants.movieTrackCharacteristic);
            audioMedia = audioTrack.getMedia();


            // Calculate frames per second for the audio data.
            fps = (float) audioMedia.getSampleCount() / audioMedia
                .getDuration() * audioMedia.getTimeScale();

            /* Additional movie, used for preprocessing independently of the
            playback movie. */
            OpenMovieFile omf2 = OpenMovieFile.asRead(new QTFile(audioFile));
            paudio = Movie.fromFile(omf2);

            paudio.setTimeScale(Constants.TICKS_PER_SECOND);


            paudioTrack = paudio.getIndTrackType(1,
                    StdQTConstants.audioMediaCharacteristic,
                    StdQTConstants.movieTrackCharacteristic);
            paudioMedia = paudioTrack.getMedia();

            paudioMH = (AudioMediaHandler) paudioMedia.getHandler();


            // Add the component that "renders" the audio.
            meter = new LevelMeter();

            /* The JDesktopPane is used here as a workaround for hiding the
            movie. QuickTime is clever enough to not allow audio to be played
            if it believes it is not being drawn, so we use this JDesktopPane
            to dupe QuickTime into thinking it is visible, allowing us to play
            and preprocess the audio from the movie. */
            superDesk = new JDesktopPane();
            setContentPane(superDesk);


            movieFrame = new MyInternalFrame(true, getSize());
            movieFrame.setVisible(true);

            preFrame = new MyInternalFrame(true, getSize());
            preFrame.setVisible(true);


            equalFrame = new MyInternalFrame();
            equalFrame.setVisible(true);
            equalFrame.add(meter);

            superDesk.add(preFrame);
            superDesk.add(movieFrame);
            superDesk.add(equalFrame);

            comp = QTFactory.makeQTComponent(audio).asComponent();
            movieFrame.add(comp);


            preComp = QTFactory.makeQTComponent(paudio).asComponent();
            preFrame.add(preComp);

            // set up repainting timer
            t = new Timer();

            t.schedule(new PaintTask(), 0, REPAINTDELAY);

            preThread = new PreProcess(meter.getNumBands());

            Thread thread = new Thread(preThread);
            thread.start();

            setName(getClass().getSimpleName() + "-" + audioFile.getName());

            invalidate();

            setVisible(true);

        } catch (QTException e) {
            logger.error("Unable to set audioFile", e);
        }
    }

    /**
     * @return The file used to display this data feed.
     */
    public File getDataFeed() {
        return audioFile;
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

            if ((audio != null) && finishedPreprocess) {
                audio.setRate(playRate);
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

            if ((audio != null) && finishedPreprocess) {
                audio.stop();
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
     * @param position Millisecond absolute position for track.
     */
    public void seekTo(final long position) {

        try {

            if ((audio != null) && finishedPreprocess) {
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

    /**
     * @return Current preprocess time in milliseconds.
     *
     * @throws QTException If error occurs accessing underlying implementation.
     */
    public long getCurrentPreprocessTime() throws QTException {
        return paudio.getTime() / SCALING;
    }

    /**
     * Get track painter.
     */
    public TrackPainter getTrackPainter() {
        return new DefaultTrackPainter();
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.CustomActionListener#handleActionButtonEvent1(java.awt.event.ActionEvent)
     */
    public void handleActionButtonEvent1(final ActionEvent event) {
        // Do nothing; not supported.
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.CustomActionListener#handleActionButtonEvent2(java.awt.event.ActionEvent)
     */
    public void handleActionButtonEvent2(final ActionEvent event) {
        // Do nothing; not supported.
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.CustomActionListener#handleActionButtonEvent3(java.awt.event.ActionEvent)
     */
    public void handleActionButtonEvent3(final ActionEvent event) {
        // Do nothing; not supported.
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
                @Override public void windowClosing(
                    final java.awt.event.WindowEvent evt) {
                    formWindowClosing(evt);
                }
            });

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
            .getScreenSize();
        setBounds((screenSize.width - 500) / 2, (screenSize.height - 500) / 2,
            500, 500);
    } // </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(final java.awt.event.WindowEvent evt) { //GEN-FIRST:event_formWindowClosing

        try {
            preThread.die();
            audio.stop();
            paudio.stop();
            removeAll();
            t.cancel();
        } catch (QTException e) {
            logger.error("Couldn't kill file", e);
        }

        movieFrame.dispose();
        preFrame.dispose();
        this.parent.shutdown(this);
    } //GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

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

        /** Ask preprocess to die. */
        private boolean terminate = false;

        /**
         * This is a separate thread which runs the preprocessing.
         * @param bands The number of frequency bands to be used.
         */
        public PreProcess(final int bands) {
            numBands = bands;

            if ((getPreDuration() / SCALING) < MAXFILESIZE) {
                work = (int) (getPreDuration() / SCALING) * numBands;
            } else {
                pfix = ""; // The file was bigger than our threshold.
                work = MAXFILESIZE * numBands;
            }
        }

        /**
         * Asks the preprocessing thread to terminate.
         */
        public void die() {
            terminate = true;
        }

        /** Grabs all the data from the audio stream. */
        public void run() {
            int check = 0;

            try {
                wTitle = getTitle();
                audioData = new int[work];

                MediaEQSpectrumBands bands = new MediaEQSpectrumBands(numBands);

                for (int i = 0; i < numBands; i++) {
                    bands.setFrequency(i, 0); // Not actually zero!
                    paudioMH.setSoundEqualizerBands(bands);
                    paudioMH.setSoundLevelMeteringEnabled(true);
                }

                volume = paudio.getVolume();
                paudio.setVolume(0F);
                paudio.setRate(PREPROCESSRATE);

                int i = 0;
                int delayTicks = DELAYTICKS;

                while (i < (work - 1)) {
                    long atime = getCurrentPreprocessTime();

                    if (atime != oldTime) { // We have a new timecode
                        oldTime = atime;

                        while ((((atime - 1) * numBands) > (i + SYNCTHRESH))
                                || (((atime - 1) * numBands)
                                    < (i - SYNCTHRESH))) {

                            // If we fell out of sync...
                            long fixedJ = Math.max(1,
                                    (atime * SCALING) - JUMPFIX);

                            if (((atime - 1) * numBands) > i) {

                                // Rewind if we went too far,
                                TimeRecord fixtime = new TimeRecord(
                                        Constants.TICKS_PER_SECOND, fixedJ);
                                paudio.setTime(fixtime);
                            }

                            atime = getCurrentPreprocessTime();

                            if (delayTicks > 0) {
                                delayTicks--;

                                for (int waste = 0; waste < DELAYVAL; waste++) {
                                    Math.random();
                                }
                            }
                            // Else keep waiting until the movie catches up.
                        }

                        tempLevels = paudioMH.getSoundEqualizerBandLevels(
                                numBands);
                    }

                    if (tempLevels != null) {

                        for (int j = 0; j < numBands; j++) {
                            audioData[i] = tempLevels[j];

                            if (tempLevels[j] != 0) {
                                check++;
                            }

                            i++;
                        }
                    }

                    if ((i == numBands) && isMovie) {
                        setSize(WIN_X + 1, WIN_Y);
                        setSize(WIN_X, WIN_Y);
                    }

                    setTitle(pfix + "Preprocessing... " + (i * CENT / work)
                        + "% " + wTitle);
                }
            } catch (Exception f) {
                logger.error(f.toString(), f);
                movieFrame.dispose();
                preFrame.dispose();
                superDesk.removeAll();
                pfix = "PREPROCESS FAILED! ";
                sfix = " ";
                setTitle(pfix + wTitle);

                JLabel failMsg = new JLabel(
                        " Couldn't get sound intensity data."
                        + " Try converting to .mov first.");
                setSize(WIN_X, ERROR_HEIGHT);

                MyInternalFrame error = new MyInternalFrame(1);
                error.setVisible(true);
                error.add(failMsg);
                superDesk.add(error);

                return;
            } finally {
                finishedPreprocess = true;
            }

            if (terminate) {
                return;
            }

            // Sound level could be checked here.
            meter.setAudioData(audioData);
            setTitle(pfix + sfix + wTitle);

            try {
                paudio.stop();
                paudio.setRate(0F);
                paudio.setVolume(volume);
                paudio.setTime(new TimeRecord(Constants.TICKS_PER_SECOND, 0));
            } catch (QTException e) {
                logger.error("Couldn't reset audio", e);
            }

            int oldWidth = getWidth();
            int oldHeight = getHeight();
            superDesk.setSize(oldWidth + EXTRA_SIZE, oldHeight + EXTRA_SIZE);
            preFrame.shove(oldWidth + HIDDEN_OFFSET, oldHeight + HIDDEN_OFFSET);
            movieFrame.shove(oldWidth + HIDDEN_OFFSET,
                oldHeight + HIDDEN_OFFSET);
            setSize(oldWidth + EXTRA_SIZE, oldHeight + EXTRA_SIZE);

            try {
                Thread.sleep(VIDEO_DRAW_DELAY);
            } catch (InterruptedException ex) {
                logger.error("Couldn't sleep", ex);
            } finally {
                finishedPreprocess = true;
            }

            superDesk.setSize(oldWidth, oldHeight);
            setSize(oldWidth, oldHeight);

        }

    }

    /**
     * Handles the repainting of the LevelMeter class.
     */
    class PaintTask extends TimerTask {

        /**
         * Paints the level meter if it has data to draw.
         */
        @Override public synchronized void run() {

            if (meter.isReady()) {

                try {
                    meter.setAudioTime(getCurrentTime() / SCALING);
                    meter.repaint();
                } catch (QTException e) {
                    logger.error("Couldn't get time", e);
                }
            }

        }
    }

}
