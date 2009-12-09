package org.openshapa.util.FileFilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 * File filter for OpenSHAPA project files
 */
public class SHAPAFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return (f.getName().endsWith(".shapa") || f.isDirectory());
    }

    @Override
    public String getDescription() {
        return "OpenSHAPA project files (*.shapa)";
    }
    
}
