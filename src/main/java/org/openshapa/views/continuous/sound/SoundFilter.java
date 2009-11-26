package org.openshapa.views.continuous.sound;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/** Filter to ensure the SoundDataViewer is passed only compatible files. */
public class SoundFilter extends FileFilter {

    /** The accepted audio file type extensions. */
    private static final String[] AUDIO_EXTS = {
        ".wav", ".mp3"
    };

    /** The accepted video file type extensions. */
    private static final String[] VIDEO_EXTS = {
        ".mov", ".avi", ".mpg", ".mp4"
    };

    /**
     * @return The discription of the file filter.
     */
    public String getDescription() {
        return new String("Audio files");
    }

    /**
     * Determines if the file filter will accept the supplied file.
     *
     * @param file The file to check if this file will accept.
     *
     * @return true if the file is to be accepted, false otherwise.
     */
    public boolean accept(File file) {
        for (int i = 0; i < AUDIO_EXTS.length; i++) {
            if (file.getName().endsWith(AUDIO_EXTS[i])) {
                return true;
            }
        } for (int i = 0; i < VIDEO_EXTS.length; i++) {
            if (file.getName().endsWith(VIDEO_EXTS[i])) {
                return true;
            }
        }
        return file.isDirectory();
    }

    public String[] getAudioExtensions() {
        return AUDIO_EXTS;
    }

    public String[] getVideoExtensions() {
        return VIDEO_EXTS;
    }
}
