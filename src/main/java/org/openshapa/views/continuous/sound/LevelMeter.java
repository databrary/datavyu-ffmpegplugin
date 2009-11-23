package org.openshapa.views.continuous.sound;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import org.apache.log4j.Logger;
import quicktime.QTException;
import quicktime.std.movies.media.AudioMediaHandler;
import quicktime.std.movies.media.MediaEQSpectrumBands;

/**
 * This class performs the functions of an equaliser, displaying the differing
 * intensities for each of the frequencies at the current point in time of the
 * audio file.
 */
class LevelMeter extends Canvas {

    /** Array of equaliser tempLevels, as used by QT player. */
    private static final int[] EQLEVELS = {
        200, 400, 800, 1600, 3200, 6400, 12800, 21000,
        9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20
    };

   /**
    * Desired number of frequency bands to be displayed- determined by width.
    * Old frequency bands were 200, 400, 800, 1600, 3200, 6400, 12800, 21000,
    * but these are only accurate for 8 bands. Actual frequencies are some
    * other figures for more or fewer than 8 bands.
    */
    private int numBands = MAXBANDS;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(LevelMeter.class);

    /** The minimum sound level to display a meter. */
    private static final int THRESHOLD = 10;

    /** The minimum window height to display a meter. */
    private static final int MINHEIGHT = 30;

    /** The maximum number of bars. */
    private static final int MAXBARS = 40;

    /** The maximum number of frequency bands that can be used. */
    private static final int MAXBANDS = 20;

    /** The divisor used to normalise sound level bars. */
    private static final float BARDIV = 255.0F;

    /** The colour cutoff for red colouring. */
    static final int REDCUT = (int) Math.floor(MAXBARS * 0.85);

    /** The colour cutoff for yellow colouring. */
    static final int YELLOWCUT = (int) Math.floor(MAXBARS * 0.7);

    /** Duplicate of the SoundDataViewer variable. */
    private AudioMediaHandler audioMH;

    /** Internal boolean to track whether the canvas is ready to be drawn to. */
    private boolean isReady = false;

    /** The recorded sound intensities. */
    private int[] levels;

    /** Determines whether or not new data needs to be loaded. */
    private boolean needNew = true;

    /** Massive array with all the audio intensity data stored. */
    private int[] audioData;




    /**
     * Constructor for the LevelMeter.
     *
     * @param a The audio media handler to be passed in.
     * @throws QTException QuickTimeException
     */
    public LevelMeter(final AudioMediaHandler a) throws QTException {
        // Check pre-conditions.
        if (a == null) {
            throw new NullPointerException();
        }

        audioMH = a;
        MediaEQSpectrumBands bands = new MediaEQSpectrumBands(EQLEVELS.length);
        for (int i = 0; i < EQLEVELS.length; i++) {
            bands.setFrequency(i, EQLEVELS[i]);
            audioMH.setSoundEqualizerBands(bands);
            audioMH.setSoundLevelMeteringEnabled(true);
        }
    }

    /** Returns the number of frequency bands for the equaliser.
     *  @return The number of bands.
     */
    public int getNumBands() {
        return numBands;
    }

    /**
     * Checks whether the canvas is ready to be painted to.
     * @return True if the canvas is ready, false otherwise.
     */
    public boolean isReady() {
        try {
            int[] tempLevels =
                    audioMH.getSoundEqualizerBandLevels(EQLEVELS.length);
            int soundCheck = 0;
            for (int i = 0; i < tempLevels.length; i++) {
                soundCheck += tempLevels[i];
            }
            if (soundCheck <= THRESHOLD) {
                isReady = false;
            }

            isReady = true;
        } catch (QTException qte) {
            logger.error("Unable to perform sound check", qte);
            isReady = false;
        }

        return isReady;
    }

    /** Sets the stored audioData to the passed in array. */
    public void setAudioData(int[] ad) {
        audioData = ad;
    }

    /**
     * Tracks whether the canvas is in the middle of a draw.
     */
    private boolean dirtyCanvas = true;

    @Override
    public synchronized void repaint() {
        dirtyCanvas = true;
        super.repaint();
    }

    /**
     * Returns whether or not the cancas is in the middle of a draw.
     * @return True if the canvas is busy, false otherwise.
     */
    public synchronized boolean isDirty() {
        return dirtyCanvas;
    }

    /**
     * Sets the needNew variable.
     * @param b The new value for needNew.
     */
    public void setNeedNew(final boolean b) {
        needNew = b;
    }

    /**
     * Refreshes the canvas with new equaliser data.
     * @param g The graphics to be painted.
     */
    @Override
    public synchronized void paint(final Graphics g) {
        // Check pre-conditions. Check isReady, return.
        if (!isReady) {
            return;
        }

        if (audioData == null) {
            System.out.println("Can't paint yet.");
            return;
        }

        int gHeight = this.getHeight();
        int gWidth = this.getWidth();


        try {
            if (gHeight > MINHEIGHT) {
                if (needNew || levels == null) {
                levels = audioMH.getSoundEqualizerBandLevels(
                                       EQLEVELS.length);
                }
                int maxHeight = gHeight - 1;
                int barWidth = gWidth / levels.length;
                int segInterval = gHeight / MAXBARS;
                for (int i = 0; i < levels.length; i++) {
                    // calculate height of each set of boxes,
                    // proportional to level
                    float levPct = ((float) levels[i]) / BARDIV;
                    // math is a little weird here; y axis has 0 at top,
                    // but we have 0 at bottom of this graph
                    int barHeight = (int) (levPct * maxHeight);
                    // draw the bar as set of 0-MAXBARS rectangles
                    int barCount = 0;
                    for (int j = maxHeight; j > (maxHeight - barHeight);
                            j -= segInterval) {

                        g.setColor(Color.green);
                        if (barCount > YELLOWCUT) {
                            g.setColor(Color.yellow);
                        }
                        if (barCount > REDCUT) {
                            g.setColor(Color.red);
                        }
                        g.fillRect(i * barWidth,
                                j - segInterval,
                                barWidth - 1,
                                segInterval - 1);
                        barCount++;
                    }
                }
                dirtyCanvas = false;
            }
        } catch (QTException qte) {
            logger.error("Unable to render sound.", qte);
        }
    }
}

