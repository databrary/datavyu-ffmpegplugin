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
package org.datavyu.util;

import java.awt.geom.GeneralPath;

/**
 * Helper functions for drawing primitive shapes using Graphics2D.
 * 
 * @see java.awt.Graphics2D
 */
public class G2DUtils {
	private G2DUtils() {}
	
	public static GeneralPath rect(double x, double y, double width, double height) {
		final GeneralPath rectangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
		rectangle.moveTo((float) x, (float) y);
		rectangle.lineTo((float) (x + width), (float) y);
		rectangle.lineTo((float) (x + width), (float) (y + height));
		rectangle.lineTo((float) x, (float) (y + height));
		rectangle.closePath();
		return rectangle;
	}
}
