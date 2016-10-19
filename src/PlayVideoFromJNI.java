import java.nio.ByteBuffer;


public class PlayVideoFromJNI {
	
	private native void loadMovie(String fileName);
	
	
	
	private native ByteBuffer getFrameBuffer();
	
	private native void loadNextVideoFrame(); // loop the video, or stop at the end?
		
	private native int getMovieHeight();
	
	private native int getMovieWidth();
	

	
	private native boolean hasAudio();

	private native ByteBuffer getAudioBuffer(int nByte); // provides pointer to stream.
	
	private native boolean loadNextAudioFrame(); // frees memory advances pointer
	
	private native String getSampleFormat();
	
	private native float getSampleRate();
	
	private native int getSampleSizeInBits();
	
	private native int getNumberOfChannels();
	
	private native int getFrameSizeInBy();
	
	private native float getFramesPerSecond();
	
	private native boolean bigEndian();
		
	private native void release();	
	
}
