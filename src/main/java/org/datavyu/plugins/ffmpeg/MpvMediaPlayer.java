package org.datavyu.plugins.ffmpeg;

import sun.awt.windows.WComponentPeer;

import java.awt.*;
import java.net.URI;


//TODO(Reda): add a dedicated event loop for the presentation and player events
// Note should be in the native side
public class MpvMediaPlayer extends FfmpegMediaPlayer{

    static {
        System.loadLibrary("MpvMediaPlayer");
    }

    private Container container;
    private long windowID;

    public MpvMediaPlayer(URI mediaPath, Container container) {
        super(mediaPath);
        this.container = container;
    }

    @Override
    public void init() {
        initNative(); // start the event queue, make sure to register all state/error listeners before
        long[] newNativeMediaRef = new long[1];

        // Container need to be visible in order to get a valid HWND
        container.setVisible(true);

        // TODO(Reda):find alternative for deprecated getPeer() method
        windowID = container.getPeer() != null ? ((WComponentPeer) container.getPeer()).getHWnd() : 0;
        if (windowID == 0){
            throw new IllegalStateException("Need a valid WID for the MPV Player");
        }

        int rc = mpvInitPlayer(newNativeMediaRef, mediaPath, windowID);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }

        nativeMediaRef = newNativeMediaRef[0];
    }

    @Override
    protected long playerGetAudioSyncDelay() throws MediaException {
        long[] audioSyncDelay = new long[1];
        int rc = mpvGetAudioSyncDelay(getNativeMediaRef(), audioSyncDelay);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return audioSyncDelay[0];
    }

    @Override
    protected void playerSetAudioSyncDelay(long delay) throws MediaException {
        int rc = mpvSetAudioSyncDelay(getNativeMediaRef(), delay);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerPlay() throws MediaException {
        int rc = mpvPlay(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerStop() throws MediaException {
        int rc = mpvStop(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerPause() throws MediaException {
        int rc = mpvPause(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerStepForward() throws MediaException {
        int rc = mpvStepForward(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerStepBackward() throws MediaException {
        int rc = mpvStepBackward(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerFinish() throws MediaException {
        int rc = mpvFinish(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected float playerGetRate() throws MediaException {
        float[] rate = new float[1];
        int rc = mpvGetRate(getNativeMediaRef(), rate);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return rate[0];
    }

    @Override
    protected void playerSetRate(float rate) throws MediaException {
        int rc = mpvSetRate(getNativeMediaRef(), rate);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected double playerGetPresentationTime() throws MediaException {
        double[] presentationTime = new double[1];
        int rc = mpvGetPresentationTime(getNativeMediaRef(), presentationTime);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return presentationTime[0];
    }

    @Override
    protected double playerGetFps() throws MediaException {
        double[] framePerSecond = new double[1];
        int rc = mpvGetFps(getNativeMediaRef(), framePerSecond);
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
        int rc = mpvGetVolume(getNativeMediaRef(), volume);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return volume[0];
    }

    @Override
    protected synchronized void playerSetVolume(float volume) throws MediaException {
        if (!muteEnabled) {
            int rc = mpvSetVolume(getNativeMediaRef(), volume);

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
        int rc = mpvGetBalance(getNativeMediaRef(), balance);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return balance[0];
    }

    @Override
    protected void playerSetBalance(float balance) throws MediaException {
        int rc = mpvSetBalance(getNativeMediaRef(), balance);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected double playerGetDuration() throws MediaException {
        double[] duration = new double[1];
        int rc = mpvGetDuration(getNativeMediaRef(), duration);
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
        int rc = mpvSeek(getNativeMediaRef(), streamTime);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    public int getImageWidth() {
        int[] width = new int[1];
        int rc = mpvGetImageWidth(getNativeMediaRef(), width);
        if (rc != 0) {
            throwMediaErrorException(rc, null);
        }
        return width[0];
    }

    @Override
    public int getImageHeight() {
        int[] height = new int[1];
        int rc = mpvGetImageHeight(getNativeMediaRef(), height);
        if (rc != 0) {
            throwMediaErrorException(rc, null);
        }
        return height[0];
    }

    @Override
    protected void playerDispose() {
        mpvDisposePlayer(getNativeMediaRef());
    }


    protected native int mpvInitPlayer(long[] newNativeMedia,
                                       String sourcePath,
                                       long windowID);

    protected native int mpvDisposePlayer(long refNativeMedia);

    protected native int mpvGetAudioSyncDelay(long refNativeMedia, long[] syncDelay);
    protected native int mpvSetAudioSyncDelay(long refNativeMedia, long delay);
    protected native int mpvPlay(long refNativeMedia);
    protected native int mpvPause(long refNativeMedia);
    protected native int mpvStop(long refNativeMedia);
    protected native int mpvStepForward(long refNativeMedia);
    protected native int mpvStepBackward(long refNativeMedia);
    protected native int mpvFinish(long refNativeMedia);
    protected native int mpvGetRate(long refNativeMedia, float[] rate);
    protected native int mpvSetRate(long refNativeMedia, float rate);
    protected native int mpvGetPresentationTime(long refNativeMedia, double[] time);
    protected native int mpvGetFps(long refNativeMedia, double[] fps);
    protected native int mpvGetBalance(long refNativeMedia, float[] balance);
    protected native int mpvSetBalance(long refNativeMedia, float balance);
    protected native int mpvGetDuration(long refNativeMedia, double[] duration);
    protected native int mpvSeek(long refNativeMedia, double streamTime);
    protected native int mpvGetImageWidth(long refNativeMedia, int[] width);
    protected native int mpvGetImageHeight(long refNativeMedia, int[] height);
    protected native int mpvGetVolume(long refNativeMedia, float[] volume);
    protected native int mpvSetVolume(long refNativeMedia, float volume);
}
