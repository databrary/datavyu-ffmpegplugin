package org.openshapa.views.continuous.sound;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import quicktime.QTException;
import quicktime.std.movies.media.AudioMediaHandler;
import quicktime.std.movies.media.MediaEQSpectrumBands;

/**
 *
 */
class LevelMeter extends Canvas {

    /** Defines the minimum size of the sound level meter. */
    private static final Dimension METERMINSIZE = new Dimension(300, 150);
    /** Array of equaliser levels, as used by QT player. */
    private static final int[] EQLEVELS = {
        200, 400, 800, 1600, 3200, 6400, 12800, 21000
    };
    /** Duplicate of the SoundDataViewer variable. */
    private AudioMediaHandler audioMediaHandler;

    /**
     * Returns the preferred size of the LevelMeter.
     *
     * @return The minimum size the meter can be.
     */
    @Override
    public Dimension getPreferredSize() {
        return METERMINSIZE;
    }

    /**
     * Returns the minimum size of the LevelMeter.
     *
     * @return The minimum size the meter can be.
     */
    @Override
    public Dimension getMinimumSize() {
        return METERMINSIZE;
    }

    /**
     * Constructor for the LevelMeter.
     *
     * @param a The audio media handler to be passed in.
     * @throws QTException QuickTimeException
     */
    public LevelMeter(final AudioMediaHandler a) throws QTException {
        audioMediaHandler = a;
        MediaEQSpectrumBands bands = new MediaEQSpectrumBands(EQLEVELS.length);
        for (int i = 0; i < EQLEVELS.length; i++) {
            bands.setFrequency(i, EQLEVELS[i]);
            audioMediaHandler.setSoundEqualizerBands(bands);
            audioMediaHandler.setSoundLevelMeteringEnabled(true);
        }
    }

    /**
     * Jelo
     */

    /**
     * Refreshes the canvas with new equaliser data.
     * @param g The graphics to be painted.
     */
    @Override
    public void paint(final Graphics g) {
        int gHeight = this.getHeight();
        int gWidth = this.getWidth();

        // draw baseline
        g.drawLine(0, gHeight, gWidth, gHeight);
        try {
            if (audioMediaHandler != null) {
                int[] levels =
                        audioMediaHandler.getSoundEqualizerBandLevels(
                        EQLEVELS.length);
                int maxHeight = gHeight - 1;
                int barWidth = gWidth / levels.length;
                int segInterval = gHeight / 20;
                for (int i = 0; i < levels.length; i++) {
                    // calculate height of each set of boxes,
                    // proportional to level
                    float levPct = ((float) levels[i]) / 255.0f;
                    // math is a little weird here; y axis has 0 at top,
                    // but we have 0 at bottom of this graph
                    int barHeight = (int) (levPct * maxHeight);
                    // draw the bar as set of 0-20 rectangles
                    int barCount = 0;
                    for (int j = maxHeight; j > (maxHeight - barHeight);
                            j -= segInterval) {
                        switch (barCount) {
                            case 20:
                            case 19:
                            case 18:
                                g.setColor(Color.red);
                                break;
                            case 17:
                            case 16:
                            case 15:
                                g.setColor(Color.yellow);
                                break;
                            default:
                                g.setColor(Color.green);
                        }
                        g.fillRect(i * barWidth,
                                j - segInterval,
                                barWidth - 1,
                                segInterval - 1);
                        barCount++;
                    }
                }

            }
        } catch (QTException qte) {
            qte.printStackTrace();
        }

    }
}

