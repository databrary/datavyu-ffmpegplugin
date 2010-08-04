package org.openshapa.models.component;

import java.util.concurrent.TimeUnit;


/**
 * Constants for the mixer interface.
 */
public final class MixerConstants {

    public static final long DEFAULT_DURATION = TimeUnit.MILLISECONDS.convert(1,
            TimeUnit.MINUTES);

    public static final double DEFAULT_ZOOM = 0.0;

    public static final int VSCROLL_WIDTH = 17;

    public static final int HSCROLL_HEIGHT = 17;

    public static final int R_EDGE_PAD = 5;

    public static final int MIXER_MIN_WIDTH = 785;

}
