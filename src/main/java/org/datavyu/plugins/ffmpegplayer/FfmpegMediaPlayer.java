package org.datavyu.plugins.ffmpegplayer;

import com.sun.media.jfxmedia.MediaException;
import com.sun.media.jfxmedia.effects.AudioEqualizer;
import com.sun.media.jfxmedia.effects.AudioSpectrum;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmediaimpl.NativeMediaPlayer;

final class FfmpegMediaPlayer extends NativeMediaPlayer {

    public FfmpegMediaPlayer(Locator source) {
        super(new FfmpegNativeMedia(source));

    }

    @Override
    public AudioEqualizer getEqualizer() {
        return null;
    }

    @Override
    public AudioSpectrum getAudioSpectrum() {
        return null;
    }

    @Override
    protected long playerGetAudioSyncDelay() throws MediaException {
        return 0;
    }

    @Override
    protected void playerSetAudioSyncDelay(long delay) throws MediaException {

    }

    @Override
    protected void playerPlay() throws MediaException {

    }

    @Override
    protected void playerStop() throws MediaException {

    }

    @Override
    protected void playerPause() throws MediaException {

    }

    @Override
    protected void playerFinish() throws MediaException {

    }

    @Override
    protected float playerGetRate() throws MediaException {
        return 0;
    }

    @Override
    protected void playerSetRate(float rate) throws MediaException {

    }

    @Override
    protected double playerGetPresentationTime() throws MediaException {
        return 0;
    }

    @Override
    protected boolean playerGetMute() throws MediaException {
        return false;
    }

    @Override
    protected void playerSetMute(boolean state) throws MediaException {

    }

    @Override
    protected float playerGetVolume() throws MediaException {
        return 0;
    }

    @Override
    protected void playerSetVolume(float volume) throws MediaException {

    }

    @Override
    protected float playerGetBalance() throws MediaException {
        return 0;
    }

    @Override
    protected void playerSetBalance(float balance) throws MediaException {

    }

    @Override
    protected double playerGetDuration() throws MediaException {
        return 0;
    }

    @Override
    protected void playerSeek(double streamTime) throws MediaException {

    }

    @Override
    protected void playerInit() throws MediaException {

    }

    @Override
    protected void playerDispose() {

    }

    // Native methods
    private native int ffmpegInitPlayer(long refNativeMedia);
    private native long ffmpegGetAudioEqualizer(long refNativeMedia);
    private native long ffmpegGetAudioSpectrum(long refNativeMedia);
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
}
