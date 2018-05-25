package org.datavyu.plugins.ffmpegplayer;

public interface ImageStreamListener extends StreamListener {

    void streamImageSize(int width, int height);
}
