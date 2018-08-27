package org.datavyu.plugins.ffmpeg;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.io.File;

import static java.awt.color.ColorSpace.CS_sRGB;

/**
 * Uses Ffmpeg to decode images and sound from video files
 *
 * This class controls the the video playback and data playback
 *
 * The images are displayed in a Swing component using a BufferStrategy
 *
 * The audio is played using a DataLine from javax.sound.sampled
 */
public final class FfmpegJavaMediaPlayer extends FfmpegMediaPlayer implements MediaPlayerData {
    private AudioPlayerThread audioPlayerThread = null;
    private ImagePlayerThread imagePlayerThread = null;
    private ImageCanvasPlayerThread imageCanvasPlayerThread = null;
    private JFrame frame;
    private Container container;
    private static final int AUDIO_BUFFER_SIZE = 4*1024; // % 4 kB
    private AudioFormat audioFormat;
    private ColorSpace colorSpace;

    static {
        System.loadLibrary("FfmpegJavaMediaPlayer");
    }

    /**
     * Create an ffmpeg media player instance and play through java
     * framework
     *
     * @param sourceFile The File source
     * @param frame The frame to display
     * @param audioFormat The audio format used for playback
     * @param colorSpace The color space used for playback
     */
    public FfmpegJavaMediaPlayer(File sourceFile, JFrame frame, AudioFormat audioFormat, ColorSpace colorSpace) {
        super(sourceFile);
        this.frame = frame;
        this.audioFormat = audioFormat;
        this.colorSpace = colorSpace;
    }

    /**
     * Create an ffmpeg media player instance and play through java
     * Datavyu container
     *
     * @param sourceFile The File source
     * @param container The Container to display
     * @param audioFormat The audio format used for playback
     * @param colorSpace The color space used for playback
     */
    public FfmpegJavaMediaPlayer(File sourceFile, Container container, AudioFormat audioFormat, ColorSpace colorSpace) {
        this(sourceFile, null, audioFormat, colorSpace);
        this.container = container;
    }

    private void initAndStartAudioPlayer() {
        audioPlayerThread = new AudioPlayerThread(this);
        try {
            audioPlayerThread.init(getAudioFormat(), AUDIO_BUFFER_SIZE);
            audioPlayerThread.start();
        } catch (LineUnavailableException lu) {
            // TODO: Add correct media error
            throwMediaErrorException(MediaError.ERROR_GSTREAMER_ERROR.code(), lu.getMessage());
        }
    }

    private void initAndStartImagePlayer() {
        if (this.frame != null) {
            imagePlayerThread = new ImagePlayerThread(this);
            imagePlayerThread.init(getColorSpace(), getImageWidth(), getImageHeight(), frame);
            imagePlayerThread.start();
        } else {
            imageCanvasPlayerThread = new ImageCanvasPlayerThread(this);
            imageCanvasPlayerThread.init(getColorSpace(), getImageWidth(), getImageHeight(), container);
            imageCanvasPlayerThread.start();
        }
    }

    @Override
    public void init() {
        initNative(); // start the event queue, make sure to register all state/error listeners before
        long[] newNativeMediaRef = new long[1];
        String filename;

        if (protocol.equals("file"))
            filename = sourcePath;
        else
            filename = sourceURI.getPath();

        int rc = ffmpegInitPlayer(newNativeMediaRef, filename, audioFormat, colorSpace, AUDIO_BUFFER_SIZE);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }

        nativeMediaRef = newNativeMediaRef[0];

        // If we have audio data consume it
        if (hasAudioData()) {
            initAndStartAudioPlayer();
        }
        // If we have image data consume it
        if (hasImageData()) {
            initAndStartImagePlayer();
        }
    }

    @Override
    protected long playerGetAudioSyncDelay() throws MediaException {
        long[] audioSyncDelay = new long[1];
        int rc = ffmpegGetAudioSyncDelay(getNativeMediaRef(), audioSyncDelay);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return audioSyncDelay[0];
    }

    @Override
    protected void playerSetAudioSyncDelay(long delay) throws MediaException {
        int rc = ffmpegSetAudioSyncDelay(getNativeMediaRef(), delay);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerPlay() throws MediaException {
        int rc = ffmpegPlay(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerStop() throws MediaException {
        int rc = ffmpegStop(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerPause() throws MediaException {
        int rc = ffmpegPause(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerStepForward() throws MediaException {
        int rc = ffmpegStepForward(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerFinish() throws MediaException {
        int rc = ffmpegFinish(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected float playerGetRate() throws MediaException {
        float[] rate = new float[1];
        int rc = ffmpegGetRate(getNativeMediaRef(), rate);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return rate[0];
    }

    @Override
    protected void playerSetRate(float rate) throws MediaException {
        int rc = ffmpegSetRate(getNativeMediaRef(), rate);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected double playerGetPresentationTime() throws MediaException {
        double[] presentationTime = new double[1];
        int rc = ffmpegGetPresentationTime(getNativeMediaRef(), presentationTime);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return presentationTime[0];
    }

    @Override
    protected double playerGetFps() throws MediaException {
        double[] framePerSecond = new double[1];
        int rc = ffmpegGetFps(getNativeMediaRef(), framePerSecond);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return framePerSecond[0];
    }


    @Override
    protected float playerGetVolume() throws MediaException {
        synchronized(this) {
            if (muteEnabled)
                return mutedVolume;
        }
        float[] volume = new float[1];

        volume[0] = audioPlayerThread.getVolume();

        return volume[0];
    }

    @Override
    protected synchronized void playerSetVolume(float volume) throws MediaException {
        if (!muteEnabled) {
            //TODO(Reda:) remove audioPlayerThread.setVolume when ffmpegSetVolume will be fully implemented
            if (volume == 0 ) {
                audioPlayerThread.setMute(true);
            } else {
                if(audioPlayerThread.isMute()){
                    audioPlayerThread.setMute(false); // make sure that the audio is un muted
                }else{
                    audioPlayerThread.setVolume(volume); // Update the volume of the SoundDataLine
                }
                mutedVolume = volume;
            }
        } else {
            mutedVolume = volume;
        }
    }

    @Override
    protected float playerGetBalance() throws MediaException {
        // TODO(fraudies): Add balance adjustment to audio thread
        throw new NotImplementedException();
    }

    @Override
    protected void playerSetBalance(float balance) throws MediaException {
        // TODO(fraudies): Add balance adjustment to audio thread
        throw new NotImplementedException();
    }

    @Override
    protected double playerGetDuration() throws MediaException {
        double[] duration = new double[1];
        int rc = ffmpegGetDuration(getNativeMediaRef(), duration);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        if (duration[0] == -1.0) {
            return Double.POSITIVE_INFINITY;
        } else {
            return duration[0];
        }
    }

    @Override
    protected void playerSeek(double streamTime) throws MediaException {
        int rc = ffmpegSeek(getNativeMediaRef(), streamTime);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerDispose() {
        if (imagePlayerThread != null) {
            imagePlayerThread.terminte();
        }
        if (imageCanvasPlayerThread != null) {
            imageCanvasPlayerThread.terminte();
        }
        if (audioPlayerThread != null) {
            audioPlayerThread.terminate();
        }
        ffmpegDisposePlayer(getNativeMediaRef());
    }

    @Override
    public boolean hasAudioData() {
        boolean[] hasData = new boolean[1];
        int rc = ffmpegHasAudioData(getNativeMediaRef(), hasData);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return hasData[0];
    }

    @Override
    public boolean hasImageData() {
        boolean[] hasData = new boolean[1];
        int rc = ffmpegHasImageData(getNativeMediaRef(), hasData);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return hasData[0];
    }

    @Override
    public int getImageWidth() {
        int[] width = new int[1];
        int rc = ffmpegGetImageWidth(getNativeMediaRef(), width);
        if (rc != 0) {
            throwMediaErrorException(rc, null);
        }
        return width[0];
    }

    @Override
    public int getImageHeight() {
        int[] height = new int[1];
        int rc = ffmpegGetImageHeight(getNativeMediaRef(), height);
        if (rc != 0) {
            throwMediaErrorException(rc, null);
        }
        return height[0];
    }

    @Override
    public AudioFormat getAudioFormat() {
        // Add a dummy audio format object to the array because creating that object in JNI is a hassle
        // Note, that the values of the object will be filled by the native code
        // Why use the array at all? To be consistent with the other methods
        AudioFormat[] audioFormat = {
                new AudioFormat(
                        new AudioFormat.Encoding("none"),
                        0f,
                        0,
                        0,
                        0,
                        0f,
                        false)};
        int rc = ffmpegGetAudioFormat(getNativeMediaRef(), audioFormat);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return audioFormat[0];
    }

    @Override
    public ColorSpace getColorSpace() {
        // Add a dummy value, this value will be overwritten from the native code
        // We add the dummy here because creating an object instance is easier in java than native code
        ColorSpace[] colorSpace = {ColorSpace.getInstance(CS_sRGB)};
        int rc = ffmpegGetColorSpace(getNativeMediaRef(), colorSpace);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return colorSpace[0];
    }

    @Override
    public void updateAudioData(byte[] data) {
        int rc = ffmpegUpdateAudioData(getNativeMediaRef(), data);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    public void updateImageData(byte[] data) {
        int rc = ffmpegUpdateImageData(getNativeMediaRef(), data);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    // Native methods
    private native int ffmpegInitPlayer(long[] newNativeMedia,
                                        String sourcePath,
                                        AudioFormat requestedAudioFormat,
                                        ColorSpace requestedColorFormat,
                                        int audioBufferSizeInBy);

    private native int ffmpegDisposePlayer(long refNativeMedia);

    private native int ffmpegGetAudioSyncDelay(long refNativeMedia, long[] syncDelay);
    private native int ffmpegSetAudioSyncDelay(long refNativeMedia, long delay);
    private native int ffmpegPlay(long refNativeMedia);
    private native int ffmpegPause(long refNativeMedia);
    private native int ffmpegStop(long refNativeMedia);
    private native int ffmpegStepForward(long refNativeMedia);
    private native int ffmpegFinish(long refNativeMedia);
    private native int ffmpegGetRate(long refNativeMedia, float[] rate);
    private native int ffmpegSetRate(long refNativeMedia, float rate);
    private native int ffmpegGetPresentationTime(long refNativeMedia, double[] time);
    private native int ffmpegGetFps(long refNativeMedia, double[] fps);
    private native int ffmpegGetDuration(long refNativeMedia, double[] duration);
    private native int ffmpegSeek(long refNativeMedia, double streamTime);

    private native int ffmpegHasAudioData(long refNativeMedia, boolean[] hasData);
    private native int ffmpegHasImageData(long refNativeMedia, boolean[] hasData);
    private native int ffmpegGetImageWidth(long refNativeMedia, int[] width);
    private native int ffmpegGetImageHeight(long refNativeMedia, int [] height);
    private native int ffmpegGetAudioFormat(long refNativeMedia, AudioFormat[] audioFormat);
    private native int ffmpegGetColorSpace(long refNativeMedia, ColorSpace[] colorSpace);
    private native int ffmpegUpdateImageData(long refNativeMedia, byte[] data);
    private native int ffmpegUpdateAudioData(long refNativeMedia, byte[] data);
}