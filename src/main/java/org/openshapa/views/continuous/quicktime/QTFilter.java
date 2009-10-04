package org.openshapa.views.continuous.quicktime;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * A file filter for movie files.
 */
public class QTFilter extends FileFilter {
    /**
     * @return The discription of the file filter.
     */
    public String getDescription() {
        return new String("Movie files");
    }

    /**
     * Determines if the file filter will accept the supplied file.
     *
     * @param file The file to check if this file will accept.
     *
     * @return true if the file is to be accepted, false otherwise.
     */
    public boolean accept(File file) {
        return (file.getName().endsWith(".mov") 
                || file.getName().endsWith(".avi")
                || file.getName().endsWith(".mpg")
                || file.getName().endsWith(".mp4")
                || file.isDirectory());
    }
}
