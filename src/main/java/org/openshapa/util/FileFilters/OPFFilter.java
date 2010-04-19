package org.openshapa.util.FileFilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * File filter for OpenSHAPA project archive files
 */
public class OPFFilter extends FileFilter {

    @Override
    public boolean accept(final File file) {
        return (file.getName().endsWith(".opf") || file.isDirectory());
    }

    @Override
    public String getDescription() {
        return "OpenSHAPA project file (*.opf)";
    }

}
