package org.datavyu.plugins.ffmpeg;

//import sun.awt.windows.WComponentPeer;
import com.sun.javafx.tk.TKStage;
import javafx.stage.Stage;
import org.datavyu.util.NativeLibraryLoader;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.net.URI;


public class MpvMediaPlayer extends FfmpegMediaPlayer{

    static {
        try {
            if(!NativeLibraryLoader.isMacOs) {
                System.out.println("Extracting Windows libraries for ffmpeg and MPV.");
                NativeLibraryLoader.extract("avutil-56");
                NativeLibraryLoader.extract("swscale-5");
                NativeLibraryLoader.extract("swresample-3");
                NativeLibraryLoader.extract("avcodec-58");
                NativeLibraryLoader.extract("avformat-58");
                NativeLibraryLoader.extract("avfilter-7");
                NativeLibraryLoader.extract("avdevice-58");
                NativeLibraryLoader.extract("postproc-55");
                NativeLibraryLoader.extract("mpv-1");
            } else {
                System.out.println("Extracting Mac OS libraries for ffmpeg and MPV.");
                NativeLibraryLoader.extract("avutil.56");
                NativeLibraryLoader.extract("swscale.5");
                NativeLibraryLoader.extract("swresample.3");
                NativeLibraryLoader.extract("avcodec.58");
                NativeLibraryLoader.extract("avformat.58");
                NativeLibraryLoader.extract("avfilter.7");
                NativeLibraryLoader.extract("avdevice.58");
                NativeLibraryLoader.extract("postproc.55");
                NativeLibraryLoader.extract("mpv.1");
            }
            NativeLibraryLoader.extractAndLoad("MpvMediaPlayer");
        } catch (Exception e) {
            System.out.println("Failed loading libraries due to error: "+ e);
        }
    }

    private Stage stage;

    private Container container;
    private long windowID;

    private PlayerStateListener stateListener;

    /**
     * Create an MPV media player instance and play through java
     * framework
     *
     * @param mediaPath The media path
     * @param container The container to display the frame in
     */
    public MpvMediaPlayer(URI mediaPath, Container container) {
        super(mediaPath);
        this.container = container;
    }

    public MpvMediaPlayer(URI mediaPath, Stage stage) {
        super(mediaPath);
        this.stage = stage;
    }

    /**
     * Create an MPV media player instance and play through java
     * framework
     *
     * @param mediaPath The media path
     */
    public MpvMediaPlayer(URI mediaPath) {
        this(mediaPath, new JDialog());
    }

    @Override
    public void init() {
        initNative(); // start the event queue, make sure to register all state/error listeners before

        stateListener = new _PlayerStateListener();
        this.addMediaPlayerStateListener(stateListener);

        long[] newNativeMediaRef = new long[1];

        if(container != null)
            initContainer();
        else
            initStage();

        int rc = mpvInitPlayer(newNativeMediaRef, mediaPath, windowID);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }

        nativeMediaRef = newNativeMediaRef[0];
    }

    private void initStage() {
        stage.show();

        windowID = getWindowId(stage);
        if (windowID == 0){
            throw new IllegalStateException("Need a valid WID for the MPV Player");
        }

    }

    private void initContainer(){
        container.setVisible(true);
        // Container need to be visible in order to get a valid HWND

        // TODO(Reda):find alternative for deprecated getPeer() method
//        windowID = container.getPeer() != null ? ((WComponentPeer) container.getPeer()).getHWnd() : 0;
//        if (windowID == 0){
//            throw new IllegalStateException("Need a valid WID for the MPV Player");
//        }
    }

    private static long getWindowId(Stage stage) {
        try {
            Method tkStageGetter;
            try {
                // java 9
                tkStageGetter = stage.getClass().getSuperclass().getDeclaredMethod("getPeer");
            } catch (NoSuchMethodException ex) {
                // java 8
                tkStageGetter = stage.getClass().getMethod("impl_getPeer");
            }
            tkStageGetter.setAccessible(true);
            TKStage tkStage = (TKStage) tkStageGetter.invoke(stage);
            Method getPlatformWindow = tkStage.getClass().getDeclaredMethod("getPlatformWindow");
            getPlatformWindow.setAccessible(true);
            Object platformWindow = getPlatformWindow.invoke(tkStage);
            Method getNativeHandle = platformWindow.getClass().getMethod("getNativeHandle");
            getNativeHandle.setAccessible(true);
            Object nativeHandle = getNativeHandle.invoke(platformWindow);
            return (long) nativeHandle;
        } catch (Throwable e) {
            System.err.println("Error getting Window Pointer");
            e.printStackTrace();
            return 0;
        }
    }
    @Override
    protected long playerGetAudioSyncDelay() throws MediaException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void playerSetAudioSyncDelay(long delay) throws MediaException {
        throw new UnsupportedOperationException();
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
        // MPV Volume range is from 0 to 100 and in order to much
        // the java native plugin range we divide by 100
        return volume[0] / 100;
    }

    @Override
    protected void playerSetVolume(float volume) throws MediaException {
        if (!muteEnabled) {
            // MPV Volume range is from 0 to 100
            int rc = mpvSetVolume(getNativeMediaRef(), volume * 100);

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
        throw new UnsupportedOperationException();
    }

    @Override
    protected void playerSetBalance(float balance) throws MediaException {
        throw new UnsupportedOperationException();
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
    protected void playerSeek(double streamTime, int seekFlags) throws MediaException {
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

    class _PlayerStateListener implements PlayerStateListener {

        @Override
        public void onReady(PlayerStateEvent evt) {
            container.setSize(getImageWidth(), getImageHeight());
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
    }

    @Override
    protected void playerDispose() {
        mpvDisposePlayer(getNativeMediaRef());
    }


    protected native int mpvInitPlayer(long[] newNativeMedia,
                                       String sourcePath,
                                       long windowID);

    protected native int mpvDisposePlayer(long refNativeMedia);

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
    protected native int mpvGetDuration(long refNativeMedia, double[] duration);
    protected native int mpvSeek(long refNativeMedia, double streamTime);
    protected native int mpvGetImageWidth(long refNativeMedia, int[] width);
    protected native int mpvGetImageHeight(long refNativeMedia, int[] height);
    protected native int mpvGetVolume(long refNativeMedia, float[] volume);
    protected native int mpvSetVolume(long refNativeMedia, float volume);
}
