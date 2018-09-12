package org.datavyu.plugins.ffmpeg;

import javax.swing.*;
import java.io.File;
import java.net.URI;

public class MediaPlayerBuilder {
    public enum PlayerType {
        SDL,
        JAVA_JFRAME,
        JAVA_JDIALOG
    }

    static MediaPlayer build(String movieFile, PlayerType type) {
        URI movie = new File(movieFile).toURI();
        switch (type) {
            case SDL:
                return new FfmpegSdlMediaPlayer(movie);
            case JAVA_JFRAME:
                return new FfmpegJavaMediaPlayer(movie, new JFrame());
            case JAVA_JDIALOG:
                return new FfmpegJavaMediaPlayer(movie, new JDialog());
            default:
                throw new IllegalArgumentException("Could not build player for type " + type);
        }
    }
}
