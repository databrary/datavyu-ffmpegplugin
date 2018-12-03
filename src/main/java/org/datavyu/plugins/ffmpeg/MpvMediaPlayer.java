package org.datavyu.plugins.ffmpeg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.util.LibraryLoader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public abstract class MpvMediaPlayer extends FfmpegMediaPlayer {
    /**
     * Holds the library name and version
     */
    private final static class LibraryDependency {
        LibraryDependency(String name, String version) {
            this.name = name;
            this.version = version;
        }
        public String getFullName() {
            return name + SEPARATOR + version;
        }
        private static final char SEPARATOR = LibraryLoader.isMacOs ? '.' : '-';
        private String name;
        private String version;
    }

    private static final Logger LOGGER = LogManager.getFormatterLogger(MpvMediaPlayer.class);
    private static final List<LibraryDependency> librariesToExtract = new ArrayList<LibraryDependency>() {{
        add(new LibraryDependency("avutil", "56"));
        add(new LibraryDependency("swscale", "5"));
        add(new LibraryDependency("swresample", "3"));
        add(new LibraryDependency("avcodec", "58"));
        add(new LibraryDependency("avformat", "58"));
        add(new LibraryDependency("avfilter", "7"));
        add(new LibraryDependency("avdevice", "58"));
        add(new LibraryDependency("postproc", "55"));
        add(new LibraryDependency("mpv", "1"));
    }};

    static {
        try {
            for (LibraryDependency libraryDependency : librariesToExtract) {
                LibraryLoader.extract(libraryDependency.getFullName());
            }
            LibraryLoader.extractAndLoad("MpvMediaPlayer");
        } catch (Exception e) {
            LOGGER.error("Loading libraries failed due to: " + e);
        }
    }

    protected MpvMediaPlayer(URI mediaPath) {
        super(mediaPath);
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
