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
package org.datavyu.models.component;

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
