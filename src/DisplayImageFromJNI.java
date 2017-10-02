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
 * Display a byte buffer that has been created in c/c++ inside a java Canvas
 */
public class DisplayImageFromJNI extends Canvas {
	
	static {
		System.loadLibrary("./lib/DisplayImageFromJNI");
	}
	
	private static final long serialVersionUID = -1946946053851857166L;
	private BufferedImage image = null;
	private int nChannel = 3;
	
	// This returns a pinned byte buffer that is outside of Java garbage collection.
	private native ByteBuffer getByteBuffer(int width, int height, int nChannel);
	
	public DisplayImageFromJNI(int width, int height) {
		ByteBuffer buffer = getByteBuffer(width, height, nChannel);
		System.out.println("The size of the buffer is " + buffer.capacity() + " bytes.");
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
        f.add( new DisplayImageFromJNI(1000, 600) );
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        } );
        f.setVisible(true);
	}
}
