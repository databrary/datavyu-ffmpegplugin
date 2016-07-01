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
 * Display the video frames read and converted into the correct color model with ffmpeg in c/c++. 
 * @author Florian Raudies
 * @date 06/27/2016
 *
 */
public class DisplayImageFromVideo extends Canvas {
	
	static {
		// Make sure to place the ffmpeg libraries (dll's) in the java library path. I use '.'
		System.loadLibrary("./lib/DisplayImageFromVideo");
	}
	
	private static final long serialVersionUID = -6199180436635445511L;
	
	ColorSpace cs 							= ColorSpace.getInstance(ColorSpace.CS_sRGB);
	ComponentColorModel cm 					= new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	Hashtable<String, String> properties 	= new Hashtable<String, String>();
	
	int 			nChannel = 3;
	BufferedImage 	image = null;
	ByteBuffer 		buffer = null;
	byte[] 			data = null;
	DataBufferByte 	dataBuffer = null;
	
	private native ByteBuffer getFrameBuffer();
	
	private native void loadNextFrame(); // loop the video, or stop at the end?
	
	private native void loadMovie(String fileName);
	
	private native int getMovieHeight();
	
	private native int getMovieWidth();
	
	private native void release();
	
	public void update(Graphics g){
	    paint(g); // instead of resetting just paint directly 
	}
	
	private void getNextFrame(int width, int height) {
		loadNextFrame();
		buffer = getFrameBuffer();
		data = new byte[width*height*nChannel];
		buffer.get(data); // Unsure how to get rid of that copy!!
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
		image = new BufferedImage(cm, raster, false, properties);
	}
	
	public DisplayImageFromVideo() {}
	
	public void setImgeBuffer(int width, int height) {
		buffer = getFrameBuffer();
		System.out.println("This buffer has the capacity " + buffer.capacity() + " bytes.");
		data = new byte[width*height*nChannel];
		buffer.get(data); // Unsure how to get rid of that copy!!
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
		image = new BufferedImage(cm, raster, false, properties);
	}
	
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}	
	
	public static void main(String[] args) {
		//String fileName = "C:\\Users\\Florian\\test.h264";
		String fileName = "C:\\Users\\Florian\\SleepingBag.MP4"; // put your video file here
		final DisplayImageFromVideo display = new DisplayImageFromVideo();
		display.loadMovie(fileName);
		int width = display.getMovieWidth();
		int height = display.getMovieHeight();		
		display.setImgeBuffer(width, height);		
		Frame f = new Frame();
        f.setBounds(0, 0, width, height);
        f.add(display);
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
            	display.release();
                System.exit(0);
            }
        } );        
        f.setVisible(true);
        long t0 = System.nanoTime();
        int nFrame = 150;
        for (int iFrame = 0; iFrame < nFrame; ++iFrame) {
        	display.getNextFrame(width, height);
        	display.repaint();
        }
        long t1 = System.nanoTime();
		System.out.println("width = " + width + " pixels.");
		System.out.println("height = " + height + " pixels.");
        System.out.println("Frame rate = " +  ((double)nFrame)/(t1-t0)*1e9f + " frames per second.");
	}
}
