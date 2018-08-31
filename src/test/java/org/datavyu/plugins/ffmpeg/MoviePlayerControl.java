package org.datavyu.plugins.ffmpeg;

import javax.sound.sampled.AudioFormat;
import java.awt.color.ColorSpace;

public interface MoviePlayerControl {
    enum PlayerType {
        AWT_TYPE, JFX_TYPE
    }
    void setScale(float scale);
    void setRate(float rate);
    void start();
    void stop();
    void close();
    double getTimeInSeconds();
    void setTimeInSeconds(double timeInSeconds);
    static MoviePlayerControl createMoviePlayer(PlayerType type,
                                                ColorSpace colorSpace,
                                                AudioFormat audioFormat,
                                                String movieFileName) {
        switch (type) {
            case AWT_TYPE:
                return new MoviePlayerFrame(colorSpace, audioFormat, movieFileName);
        }
        throw new IllegalArgumentException("Unsupported player type: " + type);
    }
}
