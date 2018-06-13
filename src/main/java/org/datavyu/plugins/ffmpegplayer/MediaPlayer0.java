package org.datavyu.plugins.ffmpegplayer;


import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;

public abstract class MediaPlayer0 implements VideoStream, AudioStream {

    /*
     * Load the native library that interfaces to ffmpeg.
     *
     * This load assumes that dependent dll's are in the JVM's classpath. Here, this is the directory '.'
     */
    static {
        System.loadLibrary("MediaPlayer0");
    }

    protected static native int[] open0(String fileName, String version, AudioFormat audioFormat);

    protected static native boolean hasVideoStream0(int streamId);
    protected static native boolean hasAudioStream0(int streamId);

    protected static native void play0(int streamId);
    protected static native void stop0(int streamId);
    protected static native void pause0(int streamId);
    protected static native void close0(int streamId);
    protected static native void reset0(int streamId);

    protected static native ByteBuffer getImageBuffer0(int streamId);
    protected static native int loadNextImageFrame0(int streamId);
    protected static native int getWidth0(int streamId);
    protected static native int getHeight0(int streamId);
    protected static native int getNumberOfColorChannels0(int streamId);

    protected static native double getStartTime0(int streamId);
    protected static native double getEndTime0(int streamId);
    protected static native double getDuration0(int streamId);
    protected static native double getCurrentTime0(int streamId);
    protected static native double getAverageFrameRate0(int streamId);
    protected static native void seek0(int streamId, double time);
    protected static native void setSpeed0(int streamId, float speed);

    protected static native ByteBuffer getAudioBuffer0(int streamId, int nByte);
    protected static native boolean loadNextAudioData0(int streamId);
    protected static native void setAudioSyncDelay0(int streamId, long delay);
    protected static native String getCodecName0(int streamId);
    protected static native float getSampleRate0(int streamId);
    protected static native int getSampleSizeInBits0(int streamId);
    protected static native int getNumberOfSoundChannels0(int streamId);
    protected static native int getFrameSize0(int streamId);
    protected static native float getFrameRate0(int streamId);
}
