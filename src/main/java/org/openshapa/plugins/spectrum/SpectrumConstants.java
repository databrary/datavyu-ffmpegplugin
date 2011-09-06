/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.plugins.spectrum;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


/**
 * OpenSHAPA and GStreamer spectrum plugin constants.
 */
public class SpectrumConstants {

    /**
     * Precalculate sweet spot. The first frequency value is 10Hz, incrementing
     * by 20Hz.
     */
    public static final int FFT_BANDS = 1200;

    /**
     * Number of spectrum bands to display.
     */
    public static final int SPECTRUM_BANDS = 40;

    /**
     * Minimum magnitude value to display.
     */
    public static final int MIN_MAGNITUDE = -80;

    /**
     * Resample all audio input to this rate.
     */
    public static final int SAMPLE_RATE = 48000;

    /**
     * Number of spectrum updates to display in a second. Must divide 1000
     * evenly. Only used for the jog functionality. This value was chosen
     * through trial-and-error.
     */
    public static final int FPS = 8;

    /**
     * GStreamer spectrum plugin message interval. This is only used for the
     * jog functionality.
     */
    public static final long SPECTRUM_MSG_INTERVAL = NANOSECONDS.convert(1000
            / FPS, MILLISECONDS);

}
