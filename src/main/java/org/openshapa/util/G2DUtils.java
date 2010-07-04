package org.openshapa.util;

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
