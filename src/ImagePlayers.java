import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.*;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

// To generate the header file go to folder ./out/production/datavyu-ffmpegplugin/
public class ImagePlayers extends Canvas {

	/**
	 * Make sure to place the ffmpeg libraries (dll's) in the java library
	 * path. I use '.' as library path.
	 */
	static {
		System.loadLibrary("./lib/ImagePlayers");
	}

	private static final long serialVersionUID = -6199180436635445511L;

	/** RGB sample model. */
	protected ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);

	/** Samples components without transparency using a byte format. */
	protected ComponentColorModel cm = new ComponentColorModel(cs,
			false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

	/** These properties are used to create the buffered image. */
	protected Hashtable<String, String> properties = new Hashtable<>();

	/** The number of channels, typically 3. */
	protected int nChannel = 0;

	/** The width of the current image in pixels. Changes with view. */
	protected int width = 0;

	/** The height of the current image in pixels. Changes with view. */
	protected int height = 0;

	/** This image buffer holds the image. */
	protected BufferedImage image = null;

	/** This byte buffer holds the raw data. */
	protected ByteBuffer buffer = null;

	/** A copy of the raw data used to be wrapped by the data byte buffer. */
	protected byte[] data = null;

	/** Used to create the buffered image. */
	protected SampleModel sm = null;

	private int playerId = 0;


	/**
	 * Load the movie with the file name.
	 *
	 * If this method is called multiple times the under laying implementation
	 * releases resources and re-allocates them.
	 *
	 * @param fileName The file name.
	 * @return An error no. This error no corresponds to the error codes defined
	 * here: https://github.com/FFmpeg/FFmpeg/blob/release/3.2/libavutil/error.h
	 * and here: https://github.com/FFmpeg/FFmpeg/blob/release/3.2/doc/errno.txt
	 */
	static native int[] openMovie0(String fileName);

	static native int openLogger(String version);

	static native void closeLogger();

	static native ByteBuffer getFrameBuffer0(int playerId);

	static native int loadNextFrame0(final int playerId);

	static native int getNumberOfChannels0(int playerId);

	static native int getHeight0(int playerId);

	static native int getWidth0(int playerId);

	static native double getStartTime0(int playerId);

	static native double getEndTime0(int playerId);

	static native double getDuration0(int playerId);

	static native double getCurrentTime0(int playerId);

	static native void rewind0(int playerId);

	static native boolean isForwardPlayback0(int playerId);

	static native boolean atStartForRead0(int playerId);

	static native boolean atEndForRead0(int playerId);

	static native void release0(int playerId);

	static native void setPlaybackSpeed0(int playerId, float speed);

	static native void setTime0(int playerId, double time);

	static native boolean hasNextFrame0(int playerId);

	static native boolean setView0(int playerId, int x0, int y0, int width, int height);

	/**
	 * Get the frame buffer.
	 *
	 * If no movie was loaded this method returns null.
	 *
	 * @return A frame buffer object or null.
	 */
	protected ByteBuffer getFrameBuffer() {
		return getFrameBuffer0(playerId);
	}

	/**
	 * Load the next frame.
	 *
	 * If no movie was loaded this method returns -1.
	 *
	 * ATTENTION: This method blocks if there is no next frame. This avoids
	 * active spinning of a display thread. However, when hooked up to a button
	 * you need to make sure that you don't call this method if there are no
	 * frames available otherwise your UI thread is blocked. The methods atStart
	 * or and atEnd can help decide if you can safely make the call.
	 *
	 * @return The loaded number of frames.
	 */
	protected int loadNextFrame() {
		return loadNextFrame0(playerId);
	}

	/**
	 * Get the number of color channels for the movie.
	 *
	 * @return The number of channels.
	 */
	public int getNumberOfChannels() {
		return getNumberOfChannels0(playerId);
	}

	/**
	 * Get the height of the movie frames. Returns 0 if no movie was loaded.
	 *
	 * @return Height of the image.
	 */
	public int getHeight() {
		return getHeight0(playerId);
	}

	/**
	 * Get the width of the movie frames. Returns 0 if no movie was loaded.
	 *
	 * @return Width of the image.
	 */
	public int getWidth() {
		return getWidth0(playerId);
	}


	/**
	 * Get the first time stamp of the movie in seconds.
	 *
	 * @return First time stamp in the stream in seconds.
	 */
	public double getStartTime() {
		return getStartTime0(playerId);
	}

	/**
	 * Get the last time stamp of the movie in seconds.
	 *
	 * ATTENTION: This is an estimate based on duration.
	 *
	 * @return ESTIMATED last time stamp in the stream in seconds.
	 */
	public double getEndTime() {
		return getEndTime0(playerId);
	}

	/**
	 * Get the duration of the movie in seconds.
	 *
	 * ATTENTION: This is a best effort estimate by ffmpeg and does not match
	 * the actual duration when decoding the movie. Usually the actual duration
	 * is shorter by less than one second.
	 *
	 * Returns 0 if no movie was loaded.
	 *
	 * @return Duration in SECONDS.
	 */
	public double getDuration() {
		return getDuration0(playerId);
	}

	/**
	 * Get the current time of the movie in seconds.
	 *
	 * Returns 0 if no movie was loaded.
	 *
	 * @return Current time in seconds.
	 */
	public double getCurrentTime() {
		return getCurrentTime0(playerId);
	}

	/**
	 * Resets the movie either to the front or end of the file depending
	 * on the play back direction.
	 */
	public void rewind() {
		rewind0(playerId);
	}


	/**
	 * Returns true if we are playing the video in forward direction and
	 * false otherwise.
	 *
	 * @return True if forward play back otherwise false.
	 */
	protected boolean isForwardPlayback() {
		return isForwardPlayback0(playerId);
	}

	/**
	 * True if we reached the start when reading this file. At this point any
	 * further loadNextFrame() will block.
	 *
	 * Blocking is intended to be used to stop any active pulling of frames when
	 * the start or end of the file is reached.
	 *
	 * @return True if start of file is reached.
	 */
	protected boolean atStartForRead0() {
		return atStartForRead0(playerId);
	}

	/**
	 * True if we reached the end while reading this file. At this point any
	 * further loadNextFrame() will block.
	 *
	 * @return True if the end of the file is reached.
	 */
	protected boolean atEndForRead() {
		return atStartForRead0(playerId);
	}

	/**
	 * Releases all resources that have been allocated when loading a movie.
	 * If this method is called when no movie was loaded nothing happens.
	 */
	public void release() {
		release0(playerId);
	}

	/**
	 * Set the play back speed.
	 *
	 * @param speed A floating point with the play back speed as factor of the
	 * 				original play back speed.
	 *
	 * For instance, for the value of -0.5x the video is played back at half the
	 * speed.
	 *
	 * We tested for the range -4x to +4x.
	 */
	public void setPlaybackSpeed(float speed) {
		setPlaybackSpeed0(playerId, speed);
	}

	/**
	 * Sets the time within the movie.
	 *
	 * @param time The time within the video in SECONDS.
	 *
	 * If the duration is set above the length of the video the video is set to
	 * the end.
	 */
	public void setTime(double time) {
		setTime0(playerId, time);
	}

	public void update(Graphics g){
	    paint(g); // Instead of resetting, paint directly.
	}

	/**
	 * If we are playing in forward mode and read the first frame or if we are
	 * in backward mode and read the last frame then, in both cases, there are
	 * no further frames.
	 *
	 * @return True if there is a next frame otherwise false.
	 */
	public boolean hasNextFrame() {
		return hasNextFrame0(playerId);
	}

	/**
	 * Set the viewing area to the rectangle area defined by the corner points
	 * (x0, y0) and (x0+width, y0+height).
	 *
	 * This method assumes that no showNextFrame is called!!!
	 *
	 * @param x0 The horizontally first coordinate in PIXELS.
	 * @param y0 The vertically first coordinate in PIXELS.
	 * @param width The width of the viewing window in PIXELS.
	 * @param height The height of the viewing window in PIXELS.
	 *
	 * @return True if we could set the window.
	 */
	public boolean setView(int x0, int y0, int width, int height) {
		boolean viewOk = setView0(playerId, x0, y0, width, height);

		// If we were able to set the viewing area adjust the height, width,
		// and sample model.
		if (viewOk) {
			this.width = width;
			this.height = height;

			// Update the sample model with the new width and height.
			sm = cm.createCompatibleSampleModel(width, height);
		}
		return viewOk;
	}

	/**
	 * Get the next frame and load it into the buffered image.
	 *
	 * Notice that the number of returned frames can be larger than one if
	 * frames are skipped. This happens during very fast play back where we load
	 * more than one frame from the buffer but only show the last frame taken
	 * from the buffer.
	 *
	 * @return The number of frames loaded.
	 */
	public int showNextFrame() {
		int nFrame = loadNextFrame0(playerId); // Load the next frame(s). May skip frames.
		if (nFrame == -1) {
			return nFrame;
		}
		buffer = getFrameBuffer0(playerId); // Get the buffer.
		data = new byte[width*height*nChannel];	// Allocate the bytes in java.
		buffer.get(data); // Copy from the native buffer into the java buffer.
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height); // Create data buffer.
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0)); // Create writable raster.
		image = new BufferedImage(cm, raster, false, properties); // Create buffered image.
		return nFrame; // Return the number of frames.
	}

	/**
	 * Sets a movie for this player.
	 *
	 * @param fileName Name of the movie file.
	 * @return Returns an error code. This error code is defined
	 * here: https://github.com/FFmpeg/FFmpeg/blob/release/3.2/libavutil/error.h
	 * and here: https://github.com/FFmpeg/FFmpeg/blob/release/3.2/doc/errno.txt
	 */
	public static ImagePlayers openMovie(String fileName) {
		int[] errNoAndPlayerId = openMovie0(fileName);
		int errNo = errNoAndPlayerId[0];
		int playerId = errNoAndPlayerId[1];
		int nChannel = getNumberOfChannels0(playerId);
		int width = getWidth0(playerId);
		int height = getHeight0(playerId);
		// TODO: Do the error handling here
		if (errNo != 0) return null;
		return new ImagePlayers(playerId, nChannel, width, height);
	}

	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

	/**
	 * Initializes this player for the specified size.
	 */
	private ImagePlayers(int playerId, int nChannel, int width, int height) {
		// Initialize with an empty image.
		this.playerId = playerId;
		this.nChannel = nChannel;
		this.width = width;
		this.height = height;
		data = new byte[width*height*nChannel];	// Allocate the bytes in java.
		DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
		sm = cm.createCompatibleSampleModel(width, height);
		WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
		image = new BufferedImage(cm, raster, false, properties); // Create buffered image.
		setBounds(0, 0, width, height);
	}

	final static class PlayerThread extends Thread {

		private ImagePlayers player;

		PlayerThread(ImagePlayers player, Frame frame) {
			this.player = player;
			frame.setBounds(0, 0, player.width, player.height);
			frame.add(player);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent ev) {
				player.release();
				}
			});
			frame.setVisible(true);
		}

		@Override
		public void run() {
			while (player.hasNextFrame()) {
				player.showNextFrame();
				player.repaint();
			}
			player.release();
		}
	}

	/**
	 * An example program on how to use this image player.
	 * 
	 * @param args Arguments for the program.
	 */
	public static void main(String[] args) {
		String folderName = "C:\\Users\\Florian";
        // TODO: ADD the true version string here.
        ImagePlayers.openLogger("0.0.1");
        List<String> fileNames = Arrays.asList(new String[]{"WalkingVideo.mov", "TurkishManGaitClip_KEATalk.mov"});
		int nPlayers = fileNames.size();
		List<Thread> playerThreads = new ArrayList<>(nPlayers);
		for (int iPlayers = 0; iPlayers < nPlayers; ++iPlayers) {
			ImagePlayers player = openMovie(new File(folderName, fileNames.get(iPlayers)).toString());
			playerThreads.add(new PlayerThread(player, new Frame()));
		}
		for (int iPlayers = 0; iPlayers < nPlayers; ++iPlayers) {
			playerThreads.get(iPlayers).start();
		}
	}
}
