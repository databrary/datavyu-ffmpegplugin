package org.datavyu.benchmark;

import javax.sound.sampled.AudioFormat;
import java.awt.color.ColorSpace;
import java.io.IOException;

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
            case JFX_TYPE:
                return new MoviePlayerStage(colorSpace, audioFormat, movieFileName);
        }
        throw new IllegalArgumentException("Unsupported player type: " + type);
    }
}
