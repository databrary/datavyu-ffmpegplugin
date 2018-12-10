package org.datavyu.plugins.ffmpeg;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import javax.swing.*;
import java.net.URI;

public class MediaPlayerSync {
    private final static Logger LOGGER = LogManager.getFormatterLogger(MediaInformation.class);
    static {
        Configurator.setRootLevel(Level.INFO);
    }

    private MediaPlayer mediaPlayer;
    private final Object readyLock = new Object();
    private final Object seekLock = new Object(); // note the end of seek may trigger a play, stop, or pause
    private final PlayerStateListener playerStateListener = new PlayerStateListener() {
        @Override
        public void onReady(PlayerStateEvent evt) {
            synchronized (readyLock) {
                readyLock.notify();
            }
        }

        @Override
        public void onPlaying(PlayerStateEvent evt) {
            synchronized (seekLock) {
                seekLock.notify();
            }
        }

        @Override
        public void onPause(PlayerStateEvent evt) {
            synchronized (seekLock) {
                seekLock.notify();
            }
        }

        @Override
        public void onStop(PlayerStateEvent evt) {
            synchronized (seekLock) {
                seekLock.notify();
            }
        }

        @Override
        public void onStall(PlayerStateEvent evt) { }

        @Override
        public void onFinish(PlayerStateEvent evt) { }

        @Override
        public void onHalt(PlayerStateEvent evt) { }
    };

    MediaPlayerSync(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        this.mediaPlayer.addMediaPlayerStateListener(playerStateListener);
        waitForInit();
    }

    void waitForInit() {
        synchronized (readyLock) {
            try {
                mediaPlayer.init();
                readyLock.wait();
            } catch (InterruptedException e) {
                // Happens if someone interrupts your thread
            }
        }
    }

    public void waitForSeek(double time) {
        synchronized (seekLock) {
            try {
                mediaPlayer.seek(time);
                seekLock.wait();
            } catch (InterruptedException e) {
                // Happens if someone interrupts your thread
            }
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static MediaPlayerSync createMediaPlayerSync(MediaPlayer mediaPlayer) {
        return new MediaPlayerSync(mediaPlayer);
    }
}
