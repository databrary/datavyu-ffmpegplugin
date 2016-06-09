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


public class DisplayImageFromVideo extends Canvas {
	
	static {
		// Make sure to place the ffmpeg libraries (dll's) in the library path I use '.'
		System.loadLibrary("./lib/DisplayImageFromVideo");
	}
	
	private static final long serialVersionUID = -6199180436635445511L;
	
	BufferedImage image;
	int nChannel = 3;
	
	private native ByteBuffer getFrameBuffer();
	
	private native void loadNextFrame(); // loop the video
	
	private native void loadMovie(String fileName);
	
	private native int getMovieHeight();
	
	private native int getMovieWidth();
	
	private native void release();
	
	public DisplayImageFromVideo() {}
	
	public void setImgeBuffer(int width, int height) {
		ByteBuffer buffer = getFrameBuffer();
		System.out.println("This buffer has the capacity " + buffer.capacity() + " bytes.");
		byte[] data = new byte[width*height*nChannel];
		buffer.get(data); // Unsure how to get rid of that copy!!
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
		//String fileName = args[1];
		//String fileName = "C:\\Users\\Florian\\test.h264";
		String fileName = "C:\\Users\\Florian\\SleepingBag.MP4";
		final DisplayImageFromVideo display = new DisplayImageFromVideo();
		display.loadMovie(fileName);
		int width = display.getMovieWidth();
		int height = display.getMovieHeight();		
		display.setImgeBuffer(width, height);		
		Frame f = new Frame();
        f.setBounds(0, 0, 1200, 700);
        f.add(display);
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
            	display.release();
                System.exit(0);
            }
        } );        
        f.setVisible(true);
        long t0 = System.nanoTime();
        int nFrame = 50;
        for (int iFrame = 0; iFrame < nFrame; ++iFrame) {
        	display.loadNextFrame();
        	display.validate();
        	display.repaint();
//        	try {
//            	Thread.sleep(100);
//        	} catch (InterruptedException ie) {}
        }
        long t1 = System.nanoTime();
		System.out.println("width = " + width + " pixels.");
		System.out.println("height = " + height + " pixels.");
        System.out.println("Frame rate = " +  ((double)nFrame)/(t1-t0)*1e9f + " frames per second.");
	}
}
