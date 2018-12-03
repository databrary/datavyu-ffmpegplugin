package org.datavyu.plugins.ffmpeg;

import java.awt.*;
import java.net.URI;

public class MpvAwtMediaPlayer extends MpvMediaPlayer {
    private Container container;

    /**
     * Create an MPV media player instance and play through java
     * framework
     *
     * @param mediaPath The media path
     * @param container The container to display the frame in
     */
    public MpvAwtMediaPlayer(URI mediaPath, Container container) {
        super(mediaPath);
        this.container = container;
    }

    @Override
    public void init() {
        addMediaPlayerStateListener(new PlayerStateListenerImpl());
        initNative(); // starts the event queue, make sure to register all state/error listeners before
        container.setVisible(true);

        long[] newNativeMediaRef = new long[1];
        int rc = mpvInitPlayer(newNativeMediaRef, mediaPath, 0);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }

        nativeMediaRef = newNativeMediaRef[0];
    }

    private class PlayerStateListenerImpl implements PlayerStateListener {

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
}
