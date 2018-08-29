package org.datavyu.plugins.ffmpeg;

import java.io.File;

/**
 * Uses ffmpeg to decode and transcode (optional) image and audio data
 */
public abstract class FfmpegMediaPlayer extends NativeMediaPlayer {
    float mutedVolume = 1.0f;  // last volume before mute
    boolean muteEnabled = false;

    /**
     * Create an ffmpeg media player instance
     *
     * @param sourceFile The File source
     */
    public FfmpegMediaPlayer(File sourceFile) {
        super(sourceFile);
    }

    void throwMediaErrorException(int code, String message)
            throws MediaException {
        MediaError me = MediaError.getFromCode(code);
        throw new MediaException(message, null, me);
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
}
