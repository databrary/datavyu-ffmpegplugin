package org.datavyu.util;

import org.datavyu.plugins.ffmpeg.MediaPlayer;
import org.datavyu.plugins.ffmpeg.NativeMediaPlayer;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class MediaTimerTask extends TimerTask {
    private Timer mediaTimer = null;
    public static final Object timerLock = new Object();
    private WeakReference<MediaPlayer> playerRef;

    public MediaTimerTask(MediaPlayer player) {
        playerRef = new WeakReference<MediaPlayer>(player);
    }

    public void start() {
        if (mediaTimer == null) {
            mediaTimer = new Timer(true);
            mediaTimer.scheduleAtFixedRate(this, 0, 100 /* period ms*/);
        }
    }

    public void stop() {
        if (mediaTimer != null) {
            mediaTimer.cancel();
            mediaTimer = null;
        }
    }

    @Override
    public void run() {
        synchronized (timerLock) {
            final MediaPlayer player = playerRef.get();
            if (player != null) {
                synchronized (timerLock) {
                    ((NativeMediaPlayer) player).updateCurrentTime();
                }
            } else {
                cancel();
            }
        }
    }
}
