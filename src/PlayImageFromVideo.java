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
	
	protected ColorSpace cs 						= ColorSpace.getInstance(ColorSpace.CS_sRGB);
	protected ComponentColorModel cm 				= new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	protected Hashtable<String, String> properties 	= new Hashtable<String, String>();
	
	protected int 				nChannel 	= 0;
	protected int 				width 		= 0; // Store width and height to allocate buffers without native calls.
	protected int 				height 		= 0;
	protected BufferedImage 	image 		= null;
	protected ByteBuffer 		buffer 		= null;
	protected byte[] 			data 		= null;
	protected DataBufferByte 	dataBuffer 	= null;
	protected SampleModel 		sm 			= null;
	protected boolean			loaded 		= false;
	
	/**
	 * Get the frame buffer.
	 * If no movie was loaded this method returns null.
	 * @return A frame buffer object.
	 */
	protected native ByteBuffer getFrameBuffer();
	
	/**
	 * Load the next frame.
	 * If no movie was loaded this method returns -1.
	 * @return The number of frames loaded. This could be larger than one if frames are skipped.
	 */
	protected native int loadNextFrame(); // loop the video, or stop at the end?
	
	/**
	 * Load the movie with the file name.
	 * If this method is called, multiple times, the under laying implementation
	 * releases resources and re-allocates them.
	 * @param fileName The file name.
	 */
	protected native void loadMovie(String fileName);
	
	/**
	 * Get the number of color channels for the movie.
	 * @return The number of channels.
	 */
	public native int getMovieColorChannels();

	/**
	 * Get the height of the movie frames.
	 * Returns 0 if no movie was loaded.
	 * @return Height.
	 */
	public native int getMovieHeight();
	
	/**
	 * Get the width of the movie frames.
	 * Returns 0 if no movie was loaded.
	 * @return Width.
	 */
	public native int getMovieWidth();
	
	/**
	 * Get the duration of the movie in seconds.
	 * Returns 0 if no movie was loaded.
	 * @return Duration in SECONDS.
	 */
	public native double getMovieDuration();
	
	/**
	 * Get the number of frames if known, otherwise 0.
	 * Returns 0 if no movie was loaded.
	 * @return Number of frames.
	 */
	public native long getMovieNumberOfFrames();
	
	/**
	 * Get the current time of the movie in seconds.
	 * Returns 0 if no movie was loaded.
	 * @return Current time in seconds.
	 */
	public native double getMovieTimeInSeconds();
	
	/**
	 * Get the current time of the movie in frames.
	 * Returns 0 if no movie was loaded.
	 * @return Current time in frames.
	 */
	public native long getMovieTimeInFrames();
	
	/**
	 * Get the current time of the movie in the AV's stream time base.
	 * Returns 0 if no movie was loaded.
	 * @return Get time in stream's time base.
	 */
	public native long getMoveTimeInStreamUnits();
	
	/**
	 * Release all resources that have been allocated when loading the move.
	 * If this method is called when no movie was loaded no resources are
	 * de-allocated.
	 */
	public native void releaseMovie();
	
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
	public native void setTimeInSeconds(double time);
	
	/**
	 * Set the time within the movie through a frame number.
	 * @param frameNo Frame number to set to.
	 */
	public native void setTimeInFrames(long frameNo);
	
	
	public void update(Graphics g){
	    paint(g); // Instead of resetting, paint directly. 
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
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0)); // Create writable raster.
		image = new BufferedImage(cm, raster, false, properties); // Create buffered image.
		return nFrame; // Return the number of frames.
	}

	/**
	 * Set a movie with the file name for this player.
	 * @param fileName Name of the movie file.
	 */
	public void setMovie(String fileName) {
		if (loaded) {
			releaseMovie();
		}
		loadMovie(fileName);
		nChannel = getMovieColorChannels();
		width = getMovieWidth();
		height = getMovieHeight();
		loaded = true;
		sm = cm.createCompatibleSampleModel(width, height); // Create sampling model.
		setBounds(0, 0, width, height);
	}
		
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
	public PlayImageFromVideo() {
		// Initialize with an empty image.
		nChannel = 3;
		width = 640;
		height = 480;
		data = new byte[width*height*nChannel];	// Allocate the bytes in java.
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		sm = cm.createCompatibleSampleModel(width, height);
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
		image = new BufferedImage(cm, raster, false, properties); // Create buffered image.
		setBounds(0, 0, width, height);
	}
	
	/**
	 * An example program.
	 * @param args Arguments for the program.
	 */
	public static void main(String[] args) {
		//String fileName = "C:\\Users\\Florian\\test.mpg";
		//String fileName = "C:\\Users\\Florian\\SleepingBag.MP4"; // put your video file here
		String fileName = "C:\\Users\\Florian\\WalkingVideo.mov";
		//String fileName = "C:\\Users\\Florian\\TurkishManGaitClip_KEATalk.mov";
		//String fileName = "C:\\Users\\Florian\\video_1080p.mp4";
		//String fileName = "C:\\Users\\Florian\\video_h264ntscdvw.mp4";
		final PlayImageFromVideo player = new PlayImageFromVideo();
		player.setMovie(fileName);
		int width = player.getMovieWidth();
		int height = player.getMovieHeight();
		double duration = player.getMovieDuration();
		long nFrameMovie = player.getMovieNumberOfFrames();
		//player.setTimeInSeconds(8);
		//player.setPlaybackSpeed(-1f);
		Frame f = new Frame();
        f.setBounds(0, 0, width, height);
        f.add(player);
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
            	player.releaseMovie();
                System.exit(0);
            }
        } );        
        f.setVisible(true);
        long t0 = System.nanoTime();
        int nFrameReq = 1; // Played number of frames.
        int nFrameDec = 0; // Decoded number of frames.
        int nFrameSkip = 0; // Skipped number of frames.
        for (int iFrame = 0; iFrame < nFrameReq; ++iFrame) {
        	int nFrame = player.getNextFrame();
        	nFrameSkip += nFrame > 1 ? 1 : 0;
        	nFrameDec += nFrame;
        	player.repaint();
        }
        long t1 = System.nanoTime();
		System.out.println("width = " + width + " pixels.");
		System.out.println("height = " + height + " pixels.");
		System.out.println("duration = " + duration + " seconds.");
		System.out.println("duration = " + nFrameMovie + " frames");
        System.out.println("Decoded rate = " +  ((double)nFrameDec)/(t1-t0)*1e9f + " frames per second.");
        System.out.println("Skipped " + nFrameSkip + " frames.");
	}	
}
