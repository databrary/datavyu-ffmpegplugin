package org.openshapa.models.component;

import java.awt.Color;


/**
 * Constants relating to the timescale.
 */
public interface TimescaleConstants {

    static final Color HOURS_COLOR = Color.RED.darker();

    static final Color MINUTES_COLOR = Color.GREEN.darker().darker();

    static final Color SECONDS_COLOR = Color.BLUE.darker().darker();

    static final Color MILLISECONDS_COLOR = Color.GRAY.darker();

    static final int XPOS_ABS = TrackConstants.HEADER_WIDTH + 1;

}
