package org.datavyu.plugins.ffmpegplayer;

public interface ImageStreamListener extends StreamListener {

    void streamNewImageSize(int newWidth, int newHeight);
}
