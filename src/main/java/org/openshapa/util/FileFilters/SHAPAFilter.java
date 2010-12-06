package org.openshapa.util.FileFilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * File filter for OpenSHAPA project files
 */
public class SHAPAFilter extends FileFilter {

    public static final SHAPAFilter INSTANCE = new SHAPAFilter();

    private SHAPAFilter() {
        ;
    }

    @Override public boolean accept(final File file) {
        return (file.getName().endsWith(".shapa") || file.isDirectory());
    }

    @Override public String getDescription() {
        return "OpenSHAPA project files (*.shapa)";
    }

}
