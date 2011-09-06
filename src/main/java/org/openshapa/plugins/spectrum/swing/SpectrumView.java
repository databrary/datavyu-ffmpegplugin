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
package org.openshapa.plugins.spectrum.swing;

/**
 * Provides an interface to a spectrum viewer.
 */
public interface SpectrumView {

    /**
     * Set the magnitude values to display on the viewer.
     *
     * @param dbVals
     *            Relative magnitude values, where the peak value is 0 dB.
     */
    void setMagnitudelVals(final double[] dbVals);

    /**
     * Set the frequency values corresponding to the magnitude values being
     * displayed on the viewer.
     *
     * @param freqVals
     *            Frequency values.
     */
    void setFreqVals(final double[] freqVals);
}
