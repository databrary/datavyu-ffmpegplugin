package org.datavyu.plugins.ffmpegplayer.prototypes;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Display an image using the writable raster in java
 */
public class DisplayImageWithRaster extends Canvas {

	private static final long serialVersionUID = 4847026109565928971L;

	private BufferedImage image;
	
	private DisplayImageWithRaster(int width, int height) {
		// Fill the buffered image.
		//image = new BufferedImage(100, 80, BufferedImage.TYPE_BYTE_INDEXED);
		image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		//image = new BufferedImage(100, 80, BufferedImage.TYPE_4BYTE_ABGR);
		WritableRaster raster = image.getRaster();
		int[] data = new int[width*height*3];
		for (int iData = 0; iData < width*height*3; ++iData) {
			data[iData] = iData % 255;
		}
		raster.setPixels(0, 0, width, height, data);
	}
	
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
	public static void main(String[] args) {
		Frame f = new Frame();
        f.setBounds(0, 0, 1200, 700);
        f.add( new DisplayImageWithRaster(1000, 600) );
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        } );
        f.setVisible(true);
	}
}
