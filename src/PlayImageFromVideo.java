import java.nio.ByteBuffer;


public class PlayImageFromVideo {
	
	private native ByteBuffer getFrameBuffer();
	
	private native void setPlaybackSpeed(int speed); // 1x, 2x, 4x, -1x, -2x, -4x
	
	private native void loadNextFrame(); // loop the video, or stop at the end?
	
	private native void loadMovie(String fileName);
	
	private native int getMovieHeight();
	
	private native int getMovieWidth();
	
	private native void release();
	
}
