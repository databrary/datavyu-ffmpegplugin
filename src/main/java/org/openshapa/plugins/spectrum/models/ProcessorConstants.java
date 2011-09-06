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
package org.openshapa.plugins.spectrum.models;

public interface ProcessorConstants {

    /** Total number of points to pick for each channel. */
    static final int NUM_POINTS = 5000;

    /** Size of a processed block. Must divide {@link #NUM_POINTS} evenly. */
    static final int BLOCK_SZ = 500;

    /** Unsigned bit depth of the audio to process. */
    static final int DEPTH = 16;

    /** Number of discretization levels. */
    static final int LEVELS = 1 << (DEPTH - 1);

    /** Zoom threshold at which audio data is processed again. */
    static final double ZOOM_REPROCESS_THRESHOLD = 0.05D;

}
