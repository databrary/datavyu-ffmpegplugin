import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.util.Hashtable;

/**
 * Display an image from an pinned byte buffer inside java.
 */
public class DisplayImageFromByteBuffer extends Canvas {

	private static final long serialVersionUID = 3011662036905343969L;
	private ByteBuffer buffer = null;
	private BufferedImage image = null;
	private int blockSize = 50;
	private int nChannel = 3;
	
	public DisplayImageFromByteBuffer(int width, int height) {
		buffer = ByteBuffer.allocateDirect(width*height*nChannel);
		// Fill the buffer which would be done in native land.
		for (int iRow = 0; iRow < height; ++iRow) {
			for (int iCol = 0; iCol < width; ++iCol) {
				for (int iChannel = 0; iChannel < nChannel; ++iChannel) {
					byte b = (byte) (( ((iRow/blockSize % 2)==0) && ((iCol/blockSize % 2)==0)) ? 0xFF : 0x00);
					buffer.put((iRow*width+iCol)*nChannel+iChannel, b);					
				}
			}
		}
		byte[] data = new byte[width*height*nChannel];
		buffer.get(data); // Unsure how to remove this copy!
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ComponentColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
		Hashtable<String, String> properties = new Hashtable<String, String>();
		image = new BufferedImage(cm, raster, false, properties);
	}
	
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
	public static void main(String[] args) {
		Frame f = new Frame();
        f.setBounds(0, 0, 1200, 700);
        f.add( new DisplayImageFromByteBuffer(1000, 600) );
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        } );
        f.setVisible(true);
	}	
}
