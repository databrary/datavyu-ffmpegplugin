package org.openshapa.util.FileFilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * A file filter for legacy MacSHAPA database files.
 */
public class MODBFilter extends FileFilter {
    /**
     * @return The discription of the file filter.
     */
    public String getDescription() {
        return new String("MacSHAPA database file");
    }

    /**
     * Determines if the file filter will accept the supplied file.
     *
     * @param file The file to check if this file will accept.
     *
     * @return true if the file is to be accepted, false otherwise.
     */
    public boolean accept(File file) {
        return (file.getName().endsWith(".db") || file.isDirectory());
    }
}
