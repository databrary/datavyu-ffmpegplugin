package org.openshapa.util.FileFilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * File filter for OpenSHAPA project archive files
 */
public class SHPFilter extends FileFilter {

    @Override
    public boolean accept(final File file) {
        return (file.getName().endsWith(".shp") || file.isDirectory());
    }

    @Override
    public String getDescription() {
        return "OpenSHAPA project archive (*.shp)";
    }

}
