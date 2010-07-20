package org.openshapa.plugins.spectrum.swing;

import java.awt.Color;
import java.awt.Graphics;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;

import org.openshapa.views.component.TrackPainter;


/**
 * Used to plot amplitude data over the time domain.
 */
public final class AmplitudeTrack extends TrackPainter {

    /** Color used to paint the amplitude data. */
    private static final Color DATA_COLOR = new Color(0, 0, 0, 100);

    /** Contains the amplitude data to visualize. */
    private StereoAmplitudeData data;

    /**
     * Set the amplitude data to visualize.
     *
     * @param data
     *            amplitude data to visualize.
     */
    public void setData(final StereoAmplitudeData data) {
        this.data = data;
    }

    @Override protected void paintCustom(final Graphics g) {

        // Calculate carriage start and end pixel positions.
        final int startXPos = computePixelXCoord(trackModel.getOffset());
        final int endXPos = computePixelXCoord(trackModel.getDuration()
                + trackModel.getOffset());

        // Carriage height.
        final int carriageHeight = (int) (getHeight() * 7D / 10D);

        // Carriage offset from top of panel.
        final int carriageYOffset = (int) (getHeight() * 2D / 10D);

        // Y-coordinate for left channel.
        final int midYLeftPos = carriageYOffset + (carriageHeight / 4);

        // Y-coordinate for right channel.
        final int midYRightPos = carriageYOffset + (3 * carriageHeight / 4);

        g.setColor(DATA_COLOR);

        // Draw the baseline zero amplitude.
        g.drawLine(startXPos, midYLeftPos, endXPos, midYLeftPos);
        g.drawLine(startXPos, midYRightPos, endXPos, midYRightPos);

        if (data == null) {
            return;
        }

        // Height for 100% amplitude.
        final int ampHeight = carriageHeight / 4;

        // Draw left channel data
        int offsetCounter = 1;

        int xLineStart = startXPos;
        int yLineStart = midYLeftPos;

        for (Double amp : data.getDataL()) {
            long interval = data.getTimeInterval() * offsetCounter;
            int offset = computePixelXCoord(MILLISECONDS.convert(interval,
                        data.getTimeUnit()));
            offsetCounter++;

            g.drawLine(xLineStart, yLineStart, startXPos + offset,
                midYLeftPos + (int) (-amp * ampHeight));

            xLineStart = startXPos + offset;
            yLineStart = midYLeftPos + (int) (-amp * ampHeight);
        }

        // Draw right channel data
        offsetCounter = 1;

        xLineStart = startXPos;
        yLineStart = midYRightPos;

        for (Double amp : data.getDataR()) {
            long interval = data.getTimeInterval() * offsetCounter;
            int offset = computePixelXCoord(MILLISECONDS.convert(interval,
                        data.getTimeUnit()));
            offsetCounter++;

            g.drawLine(xLineStart, yLineStart, startXPos + offset,
                midYRightPos + (int) (-amp * ampHeight));

            xLineStart = startXPos + offset;
            yLineStart = midYRightPos + (int) (-amp * ampHeight);
        }
    }

}
