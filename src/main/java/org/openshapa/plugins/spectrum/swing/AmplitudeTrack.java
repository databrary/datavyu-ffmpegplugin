package org.openshapa.plugins.spectrum.swing;

import java.awt.Color;
import java.awt.Graphics;

import org.openshapa.views.component.TrackPainter;


/**
 * Used to plot amplitude data over the time domain.
 *
 * @author Douglas Teoh
 */
public final class AmplitudeTrack extends TrackPainter {

    private static final Color DATA_COLOR = new Color(0, 0, 0, 100);

    @Override protected void paintCustom(final Graphics g) {


        // Calculate carriage start and end pixel positions
        final int startXPos = computePixelXCoord(trackModel.getOffset());

        final int endXPos = computePixelXCoord(trackModel.getDuration()
                + trackModel.getOffset());

        final int midYPos = (int) (getHeight() * 11D / 20D);

        g.setColor(DATA_COLOR);

        g.drawLine(startXPos, midYPos, endXPos, midYPos);
    }

}
