package org.openshapa.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * This class is used to paint a track and its information
 */
public class TrackPainter extends Component {

    // The displayed start time of the track in milliseconds
    private long start;
    // The displayed end time of the track in milliseconds
    private long end;

    @Override
    public void paint(Graphics g) {
        Dimension size = getSize();

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, size.width, size.height);
    }

}
