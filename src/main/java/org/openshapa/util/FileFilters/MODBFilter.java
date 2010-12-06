package org.openshapa.util.FileFilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * A file filter for legacy MacSHAPA database files.
 */
public class MODBFilter extends FileFilter {

    public static final MODBFilter INSTANCE = new MODBFilter();

    private MODBFilter() {
        ;
    }

    /**
     * @return The discription of the file filter.
     */
    @Override public String getDescription() {
        return "MacSHAPA open database file (*.odb)";
    }

    /**
     * Determines if the file filter will accept the supplied file.
     *
     * @param file
     *            The file to check if this file will accept.
     * @return true if the file is to be accepted, false otherwise.
     */
    @Override public boolean accept(final File file) {
        return (file.getName().endsWith(".odb") || file.isDirectory());
    }
}
