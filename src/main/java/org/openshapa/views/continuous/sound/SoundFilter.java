package org.openshapa.views.continuous.sound;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class SoundFilter extends FileFilter {
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
        return (file.getName().endsWith(".wav")
                || file.getName().endsWith(".mp3")
                || file.getName().endsWith(".mov") // Movie files
                || file.getName().endsWith(".avi") // ..
                || file.getName().endsWith(".mpg") // ..
                || file.getName().endsWith(".mp4") // ..
                || file.isDirectory());
    }
}
