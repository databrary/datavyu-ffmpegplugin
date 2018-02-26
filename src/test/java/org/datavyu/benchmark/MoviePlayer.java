package org.datavyu.benchmark;

import javax.sound.sampled.AudioFormat;
import java.awt.color.ColorSpace;
import java.io.IOException;

public interface MoviePlayer {
    enum PlayerType {
        AWT_TYPE, JFX_TYPE
    }
    void setScale(float scale);
    void setRate(float rate);
    void start();
    void stop();
    void stepForward();
    void openFile(String file, String version) throws IOException;
    void closeFile() throws IOException;
    double getTimeInSeconds();
    void setTimeInSeconds(double timeInSeconds);
    static MoviePlayer createMoviePlayer(PlayerType type, ColorSpace colorSpace, AudioFormat audioFormat) {
        switch (type) {
            case AWT_TYPE:
                return new MoviePlayerFrame(colorSpace, audioFormat);
            case JFX_TYPE:
                return new MoviePlayerStage(colorSpace, audioFormat);
        }
        throw new IllegalArgumentException("Unsupported player type: " + type);
    }
}
