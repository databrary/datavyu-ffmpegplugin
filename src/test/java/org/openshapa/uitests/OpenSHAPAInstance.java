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
package org.openshapa.uitests;

import org.fest.swing.fixture.OpenSHAPAFrameFixture;


/**
 * Singleton of OpenSHAPA to be used for all Fest tests.
 */
public final class OpenSHAPAInstance {

    /** Singleton. */
    private static OpenSHAPAInstance instance;

    /** Main Frame fixture. */
    private OpenSHAPAFrameFixture mainFrameFixture;

    /** Empty constructor. */
    private OpenSHAPAInstance() {
    }

    /**
     * Set fixture for instance.
     *
     * @param fixture
     *            FrameFixture
     */
    static void setFixture(final OpenSHAPAFrameFixture fixture) {

        if (instance == null) {
            instance = new OpenSHAPAInstance();
        }

        instance.mainFrameFixture = fixture;
    }

    /**
     * @return FrameFixture of OpenSHAPA instance.
     */
    static OpenSHAPAFrameFixture getFixture() {

        if (instance == null) {
            instance = new OpenSHAPAInstance();
        }

        return instance.mainFrameFixture;
    }
}
