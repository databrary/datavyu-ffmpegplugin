package org.openshapa.views.continuous;

import javax.swing.filechooser.FileFilter;

/**
 * Plugin interface - specifies the methods an OpenSHAPA plugin must implement.
 */
public interface Plugin {
    /**
     * @return A new instance of the plugins data viewer.
     */
    DataViewer getNewDataViewer();

    /**
     * @return The filter to use when looking for files that the plugins data
     * viewer supports.
     */
    FileFilter getFileFilter();
}
