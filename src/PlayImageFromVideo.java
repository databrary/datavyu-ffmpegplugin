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


public class PlayImageFromVideo extends Canvas {	
	static {
		// Make sure to place the ffmpeg libraries (dll's) in the java library path. I use '.'
		System.loadLibrary("./lib/PlayImageFromVideo");
	}
	
	private static final long serialVersionUID = -6199180436635445511L;
	
	ColorSpace cs 							= ColorSpace.getInstance(ColorSpace.CS_sRGB);
	ComponentColorModel cm 					= new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	Hashtable<String, String> properties 	= new Hashtable<String, String>();
	
	int 			nChannel = 3;
	int 			width;
	int 			height;
	BufferedImage 	image = null;
	ByteBuffer 		buffer = null;
	byte[] 			data = null;
	DataBufferByte 	dataBuffer = null;
	
	private native ByteBuffer getFrameBuffer();
	
	private native int loadNextFrame(); // loop the video, or stop at the end?
	
	private native void loadMovie(String fileName);
	
	private native int getMovieHeight();
	
	private native int getMovieWidth();
	
	private native void release();
	
	public native void setPlaybackSpeed(float speed); // +-0.25x, +-0.5x, +-1x, +-2x, +-4x
	
	private native void setTime(float time); // time in us // setTime(0) restarts
	
	public void update(Graphics g){
	    paint(g); // instead of resetting just paint directly 
	}
	
	public int getNextFrame() {
		int nFrame = loadNextFrame();
		buffer = getFrameBuffer();
		data = new byte[width*height*nChannel];		
		buffer.get(data); // Unsure how to get rid of that copy!!
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
		image = new BufferedImage(cm, raster, false, properties);
		return nFrame;
	}
	
	public PlayImageFromVideo() {}
	
	public void setMovie(String fileName) {
		loadMovie(fileName);
		width = getMovieWidth();
		height = getMovieHeight();
	}
	
	public int getFrameWidth() {
		return width;
	}
	
	public int getFrameHeight() {
		return height;
	}
	
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}	
	
	public static void main(String[] args) {
		//String fileName = "C:\\Users\\Florian\\test.mpg";
		//String fileName = "C:\\Users\\Florian\\SleepingBag.MP4"; // put your video file here
		String fileName = "C:\\Users\\Florian\\WalkingVideo.mov";
		final PlayImageFromVideo display = new PlayImageFromVideo();
		display.setMovie(fileName);
		int width = display.getFrameWidth();
		int height = display.getFrameHeight();
		display.setPlaybackSpeed(4f);
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
        int nFrameReq = 20; // Displayed number of frames.
        int nFrameDec = 0; // Decoded number of frames.
        int nFrameSkip = 0; // Skipped number of frames.
        for (int iFrame = 0; iFrame < nFrameReq; ++iFrame) {
        	int nFrame = display.getNextFrame();
        	nFrameSkip += nFrame > 1 ? 1 : 0;
        	nFrameDec += nFrame;
        	display.repaint();
        }
        long t1 = System.nanoTime();
		System.out.println("width = " + width + " pixels.");
		System.out.println("height = " + height + " pixels.");
        System.out.println("Decoded rate = " +  ((double)nFrameDec)/(t1-t0)*1e9f + " frames per second.");
        System.out.println("Skipped " + nFrameSkip + " frames.");
	}	
}
