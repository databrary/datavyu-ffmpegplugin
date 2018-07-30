package org.datavyu.plugins.ffmpeg;

import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Hashtable;

/**
 * This the implementation of the media player interface using ffmpeg to decode and
 * transcode (optional) image and audio data
 *
 * This player has two main options to display images and play sound:
 * 1) Use the java framework
 * 2) Use the native SDL framework
 */
public final class FfmpegMediaPlayer extends NativeMediaPlayer implements MediaPlayerData {
    private float mutedVolume = 1.0f;  // last volume before mute
    private boolean muteEnabled = false;
    private AudioPlayerThread audioPlayerThread = null;
    private ImagePlayerThread imagePlayerThread = null;
    private JFrame frame;
    private static final int AUDIO_BUFFER_SIZE = 4*1024; // % 4 kB

    static {
        System.loadLibrary("FfmpegMediaPlayer");
    }

    /**
     * Create an ffmpeg media player instance and play through java
     * framework
     *
     * @param source The source
     * @param frame The frame to display
     */
    public FfmpegMediaPlayer(URI source, JFrame frame) {
        super(source);
        this.frame = frame;
    }

    /**
     * Create an ffmpeg media player instance and play through
     * the native SDL framework
     *
     * @param source The source
     */
    public FfmpegMediaPlayer(URI source) {
        this(source, null);
    }

    private void initAndStartAudioPlayer() {
        audioPlayerThread = new AudioPlayerThread(this);
        try {
            // TODO(fraudies): Use getAudioFormat() once wired up
            AudioFormat audioFormat = AudioSoundStreamListener.getNewMonoFormat();
            audioPlayerThread.init(audioFormat, AUDIO_BUFFER_SIZE);
            audioPlayerThread.start();
        } catch (LineUnavailableException lu) {
            // TODO: Add correct media error
            throwMediaErrorException(MediaError.ERROR_GSTREAMER_ERROR.code(), lu.getMessage());
        }
    }

    private void initAndStartImagePlayer() {
        imagePlayerThread = new ImagePlayerThread(this);
        // TODO(fraudies): Use getColorSpace() once wired up
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        imagePlayerThread.init(colorSpace, getImageWidth(), getImageHeight(), frame);
        imagePlayerThread.start();
    }

    private void removeInitListener() {
        removeMediaPlayerStateListener(initListener);
    }

    private PlayerStateListener initListener = new PlayerStateListener() {
        @Override
        public void onReady(PlayerStateEvent evt) {
            // If we have audio data consume it
            if (hasAudioData()) {
                initAndStartAudioPlayer();
            }
            // If we have image data consume it
            if (hasImageData()) {
                initAndStartImagePlayer();
            }
            removeInitListener();
        }
        @Override
        public void onPlaying(PlayerStateEvent evt) { }
        @Override
        public void onPause(PlayerStateEvent evt) { }
        @Override
        public void onStop(PlayerStateEvent evt) { }
        @Override
        public void onStall(PlayerStateEvent evt) { }
        @Override
        public void onFinish(PlayerStateEvent evt) { }
        @Override
        public void onHalt(PlayerStateEvent evt) { }
    };

    @Override
    public void init(AudioFormat audioFormat, ColorSpace colorSpace) {
        initNative(); // start the event queue, make sure to register all state/error listeners before
        long[] newNativeMediaRef = new long[1];
        boolean streamData = frame != null;
        String filename = source.getPath();
        ffmpegInitPlayer(newNativeMediaRef, filename, audioFormat, colorSpace, streamData);
        nativeMediaRef = newNativeMediaRef[0];

        // If we have a frame to display we will use that one to playback alongside the javax.sound framework
        if (streamData) {
            addMediaPlayerStateListener(initListener);
        }
    }

    private void throwMediaErrorException(int code, String message)
            throws MediaException {
        MediaError me = MediaError.getFromCode(code);
        throw new MediaException(message, null, me);
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
    protected boolean playerGetMute() throws MediaException {
        return muteEnabled;
    }

    @Override
    protected synchronized void playerSetMute(boolean enable) throws MediaException {
        if (enable != muteEnabled) {
            if (enable) {
                // Cache the current volume.
                float currentVolume = getVolume();

                // Set the volume to zero.
                playerSetVolume(0);

                // Set the mute flag. It is necessary to do this after
                // calling setVolume() as otherwise the volume will not
                // be set to zero.
                muteEnabled = true;

                // Save the pre-mute volume.
                mutedVolume = currentVolume;
            } else {
                // Unset the mute flag. It is necessary to do this before
                // calling setVolume() as otherwise the volume will not
                // be set to the cached value.
                muteEnabled = false;

                // Set the volume to the cached value.
                playerSetVolume(mutedVolume);
            }
        }
    }

    @Override
    protected float playerGetVolume() throws MediaException {
        synchronized(this) {
            if (muteEnabled)
                return mutedVolume;
        }
        float[] volume = new float[1];
        int rc = ffmpegGetVolume(getNativeMediaRef(), volume);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return volume[0];
    }

    @Override
    protected synchronized void playerSetVolume(float volume) throws MediaException {
        if (!muteEnabled) {
            int rc = ffmpegSetVolume(getNativeMediaRef(), volume);
            if (0 != rc) {
                throwMediaErrorException(rc, null);
            } else {
                mutedVolume = volume;
            }
        } else {
            mutedVolume = volume;
        }
    }

    @Override
    protected float playerGetBalance() throws MediaException {
        float[] balance = new float[1];
        int rc = ffmpegGetBalance(getNativeMediaRef(), balance);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return balance[0];
    }

    @Override
    protected void playerSetBalance(float balance) throws MediaException {
        int rc = ffmpegSetBalance(getNativeMediaRef(), balance);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
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
        if (audioPlayerThread != null) {
            audioPlayerThread.terminate();
        }
        ffmpegDisposePlayer(getNativeMediaRef());
    }

    // ********** From the MediaPlayerData interface
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
        // TODO(fraudies): Change this to array of AudioFormat
        AudioFormat ref = null; //new AudioFormat(null, 0F, 0, 0, 0, 0F, false);
        int rc = ffmpegGetAudioFormat(getNativeMediaRef(), ref);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return ref;
    }

    @Override
    public ColorSpace getColorSpace() {
        // TODO(fraudies): Change this to array of ColorSpace
        ColorSpace ref = null; // filled by native side
        int rc = ffmpegGetColorSpace(getNativeMediaRef(), ref);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return ref;
    }

    @Override
    public ByteBuffer getAudioData() {
        ByteBuffer[] buffers = new ByteBuffer[1];
        int rc = ffmpegGetAudioBuffer(getNativeMediaRef(), buffers);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return buffers[0];
    }

    @Override
    public ByteBuffer getImageData() {
        ByteBuffer[] buffers = new ByteBuffer[1];
        int rc = ffmpegGetImageBuffer(getNativeMediaRef(), buffers);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return buffers[0];
    }

    // Native methods
    private native int ffmpegInitPlayer(long[] newNativeMedia,
                                        String sourcePath,
                                        AudioFormat requestedAudioFormat,
                                        ColorSpace requestedColorFormat,
                                        boolean streamData);

    private native int ffmpegDisposePlayer(long refNativeMedia);

    private native int ffmpegGetAudioSyncDelay(long refNativeMedia, long[] syncDelay);
    private native int ffmpegSetAudioSyncDelay(long refNativeMedia, long delay);
    private native int ffmpegPlay(long refNativeMedia);
    private native int ffmpegPause(long refNativeMedia);
    private native int ffmpegStop(long refNativeMedia);
    private native int ffmpegFinish(long refNativeMedia);
    private native int ffmpegGetRate(long refNativeMedia, float[] rate);
    private native int ffmpegSetRate(long refNativeMedia, float rate);
    private native int ffmpegGetPresentationTime(long refNativeMedia, double[] time);
    private native int ffmpegGetVolume(long refNativeMedia, float[] volume);
    private native int ffmpegSetVolume(long refNativeMedia, float volume);
    private native int ffmpegGetBalance(long refNativeMedia, float[] balance);
    private native int ffmpegSetBalance(long refNativeMedia, float balance);
    private native int ffmpegGetDuration(long refNativeMedia, double[] duration);
    private native int ffmpegSeek(long refNativeMedia, double streamTime);

    private native int ffmpegHasAudioData(long refNativeMedia, boolean[] hasData);
    private native int ffmpegHasImageData(long refNativeMedia, boolean[] hasData);
    private native int ffmpegGetImageWidth(long refNativeMedia, int[] width);
    private native int ffmpegGetImageHeight(long refNativeMedia, int [] height);
    private native int ffmpegGetAudioFormat(long refNativeMedia, AudioFormat refToAudioFormat);
    private native int ffmpegGetColorSpace(long refNativeMedia, ColorSpace refToColorSpace);
    private native int ffmpegGetImageBuffer(long refNativeMedia, ByteBuffer[] data);
    private native int ffmpegGetAudioBuffer(long refNativeMedia, ByteBuffer[] data);
}
