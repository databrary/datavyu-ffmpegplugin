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
	
	int 			nChannel 	= 3;
	int 			width 		= 0;
	int 			height 		= 0;
	BufferedImage 	image 		= null;
	ByteBuffer 		buffer 		= null;
	byte[] 			data 		= null;
	DataBufferByte 	dataBuffer 	= null;
	
	/**
	 * Get the frame buffer.
	 * @return A frame buffer object.
	 */
	protected native ByteBuffer getFrameBuffer();
	
	/**
	 * Load the next frame.
	 * @return The number of frames loaded. This could be larger than one if frames are skipped.
	 */
	protected native int loadNextFrame(); // loop the video, or stop at the end?
	
	/**
	 * Load the movie with the file name.
	 * @param fileName The file name.
	 */
	protected native void loadMovie(String fileName);

	/**
	 * Get the height of the movie frames.
	 * @return Height.
	 */
	protected native int getMovieHeight();
	
	/**
	 * Get the width of the movie frames.
	 * @return Width.
	 */
	protected native int getMovieWidth();
	
	/**
	 * Release all resources that have been allocated when loading the move.
	 */
	protected native void release();
	
	/**
	 * Set the play back speed.
	 * @param speed A floating point with the play back speed as factor of the original 
	 * 				play back speed. For instance, for the value of -0.5x the video 
	 * 				is played back at half the rate.
	 * Tested for the range -+0.25x to -+ 4x. 
	 */
	public native void setPlaybackSpeed(float speed);
	
	/**
	 * Set the time within the movie.
	 * @param time The time within the video in SECONDS.
	 * A negative value sets the video to the end. If the duration is set above the length
	 * of the video the video is set to the end.
	 */
	public native void setTime(float time);
	
	public void update(Graphics g){
	    paint(g); // instead of resetting just paint directly 
	}
	
	/**
	 * Get the next frame. With the 
	 * @return The number of frames loaded. This could be larger than one if frames are skipped.
	 */
	public int getNextFrame() {
		int nFrame = loadNextFrame(); // Load the next frame(s). May skip frames.
		buffer = getFrameBuffer(); // Get the buffer.
		data = new byte[width*height*nChannel];	// Allocate the bytes in java.
		buffer.get(data); // Copy from the native buffer into the java buffer.
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height); // Create data buffer.
		SampleModel sm = cm.createCompatibleSampleModel(width, height); // Create sampling model.
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0)); // Create writable raster.
		image = new BufferedImage(cm, raster, false, properties); // Create buffered image.
		return nFrame; // Return the number of frames.
	}

	/**
	 * Set a movie with the file name for this player.
	 * @param fileName Name of the movie file.
	 */
	public void setMovie(String fileName) {
		loadMovie(fileName);
		width = getMovieWidth();
		height = getMovieHeight();
	}
	
	/**
	 * Get the width of the frame.
	 * @return Width.
	 */
	public int getFrameWidth() {
		return width;
	}
	
	/**
	 * Get the height of the frame.
	 * @return Height.
	 */
	public int getFrameHeight() {
		return height;
	}
	
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}	
	
	/**
	 * An example program.
	 * @param args Arguments for the program.
	 */
	public static void main(String[] args) {
		//String fileName = "C:\\Users\\Florian\\test.mpg";
		//String fileName = "C:\\Users\\Florian\\SleepingBag.MP4"; // put your video file here
		String fileName = "C:\\Users\\Florian\\WalkingVideo.mov";
		final PlayImageFromVideo display = new PlayImageFromVideo();
		display.setMovie(fileName);
		int width = display.getFrameWidth();
		int height = display.getFrameHeight();
		display.setTime(8f);
		display.setPlaybackSpeed(-1f);
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
        int nFrameReq = 100; // Displayed number of frames.
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
