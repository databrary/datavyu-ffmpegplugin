package org.datavyu.plugins.ffmpeg;

import java.awt.Container;
import org.datavyu.util.LibraryLoader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses ffmpeg to decode and transcode (optional) image and audio data
 */
public abstract class FfmpegMediaPlayer extends NativeMediaPlayer {

    private PlayerStateListener stateListener;
    private Container mpvContainer;

    float mutedVolume = 1.0f;  // last volume before mute
    boolean muteEnabled = false;

    /** Library dependencies for ffmpeg */
    static final List<LibraryLoader.LibraryDependency> FFMPEG_DEPENDENCIES =
            new ArrayList<LibraryLoader.LibraryDependency>() {{
        add(new LibraryLoader.LibraryDependency("avutil", "56"));
        add(new LibraryLoader.LibraryDependency("swscale", "5"));
        add(new LibraryLoader.LibraryDependency("swresample", "3"));
        add(new LibraryLoader.LibraryDependency("avcodec", "58"));
        add(new LibraryLoader.LibraryDependency("avformat", "58"));
        add(new LibraryLoader.LibraryDependency("avfilter", "7"));
        add(new LibraryLoader.LibraryDependency("avdevice", "58"));
        add(new LibraryLoader.LibraryDependency("postproc", "55"));
    }};

    /**
     * Create an ffmpeg media player instance
     *
     * @param mediaPath The path to the media
     */
    public FfmpegMediaPlayer(URI mediaPath) {
        super(mediaPath);
        stateListener = new FfmpegPlayerStateListener();
        this.addMediaPlayerStateListener(stateListener);
    }

    //TODO: Use Java FX framework with MPV to remove this constructor
    /**
     * Create an ffmpeg media player instance, with an
     * mpv container, to be resized on ready.
     *
     * @param mediaPath The path to the media
     */
    public FfmpegMediaPlayer(URI mediaPath, Container mpvContainer) {
        this(mediaPath);
        this.mpvContainer = mpvContainer;
    }

    void throwMediaErrorException(int code, Throwable cause)
            throws MediaException {
        MediaError me = MediaError.getFromCode(code);
        throw new MediaException(me.description(), cause, me);
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

    class FfmpegPlayerStateListener implements PlayerStateListener {

        @Override
        public void onReady(PlayerStateEvent evt) {
            // Temporary for the MPV player
            if (mpvContainer != null) {
                mpvContainer.setSize(getImageWidth(), getImageHeight());
            }
        }

        @Override
        public void onPlaying(PlayerStateEvent evt) {
            isUpdateTimeEnabled = true;
        }

        @Override
        public void onPause(PlayerStateEvent evt) {
            isUpdateTimeEnabled = false;
        }

        @Override
        public void onStop(PlayerStateEvent evt) {
            isUpdateTimeEnabled = false;
        }

        @Override
        public void onStall(PlayerStateEvent evt) {
        }

        @Override
        public void onFinish(PlayerStateEvent evt) {
        }

        @Override
        public void onHalt(PlayerStateEvent evt) {
        }
    }
}
