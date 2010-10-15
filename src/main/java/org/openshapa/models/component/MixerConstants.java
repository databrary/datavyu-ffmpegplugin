package org.openshapa.models.component;

import java.util.concurrent.TimeUnit;


/**
 * Constants for the mixer interface.
 */
public interface MixerConstants {

    static final long DEFAULT_DURATION = TimeUnit.MILLISECONDS.convert(1,
            TimeUnit.MINUTES);
    static final double DEFAULT_ZOOM = 0.0;
    static final int VSCROLL_WIDTH = 17;
    static final int HSCROLL_HEIGHT = 17;
    static final int R_EDGE_PAD = 5;
    static final int MIXER_MIN_WIDTH = 785;

    static final int FILLER_ZORDER = 0;
    static final int TIMESCALE_ZORDER = 5;
    static final int TRACKS_ZORDER = 10;
    static final int REGION_ZORDER = 20;
    static final int NEEDLE_ZORDER = 30;
    static final int NEEDLE_ZOOMWINDOW_ZORDER = 31;
    static final int MARKER_ZORDER = 50;
    static final int TRACKS_SB_ZORDER = 60;

}
