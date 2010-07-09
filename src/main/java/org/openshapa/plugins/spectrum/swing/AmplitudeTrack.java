package org.openshapa.plugins.spectrum.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import java.util.concurrent.TimeUnit;

import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;

import org.openshapa.views.component.TrackPainter;


/**
 * Used to plot amplitude data over the time domain.
 *
 * @author Douglas Teoh
 */
public final class AmplitudeTrack extends TrackPainter {

    private static final Color DATA_COLOR = new Color(0, 0, 0, 100);

    private StereoAmplitudeData data;

    public void setData(final StereoAmplitudeData data) {
        this.data = data;
    }

    @Override protected void paintCustom(final Graphics g) {

        // Calculate carriage start and end pixel positions
        final int startXPos = computePixelXCoord(trackModel.getOffset());

        final int endXPos = computePixelXCoord(trackModel.getDuration()
                + trackModel.getOffset());

        final int carriageHeight = (int) (getHeight() * 7D / 10D);
        final int carriageYOffset = (int) (getHeight() * 2D / 10D);

        final int midYLeftPos = carriageYOffset + (carriageHeight / 4);
        final int midYRightPos = carriageYOffset + (3 * carriageHeight / 4);

        g.setColor(DATA_COLOR);

        g.drawLine(startXPos, midYLeftPos, endXPos, midYLeftPos);
        g.drawLine(startXPos, midYRightPos, endXPos, midYRightPos);

        if (data == null) {
            return;
        }

        final int ampHeight = carriageHeight / 4;

        // Draw left channel data
        int offsetCounter = 1;

        int xLineStart = startXPos;
        int yLineStart = midYLeftPos;

        for (Double amp : data.getDataL()) {
            int offset = computePixelXCoord(TimeUnit.MILLISECONDS.convert(
                        data.getTimeInterval() * offsetCounter,
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
            int offset = computePixelXCoord(TimeUnit.MILLISECONDS.convert(
                        data.getTimeInterval() * offsetCounter,
                        data.getTimeUnit()));
            offsetCounter++;

            g.drawLine(xLineStart, yLineStart, startXPos + offset,
                midYRightPos + (int) (-amp * ampHeight));

            xLineStart = startXPos + offset;
            yLineStart = midYRightPos + (int) (-amp * ampHeight);
        }
    }

}
