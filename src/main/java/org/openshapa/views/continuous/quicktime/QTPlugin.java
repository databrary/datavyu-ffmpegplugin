package org.openshapa.views.continuous.quicktime;

import javax.swing.filechooser.FileFilter;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.Plugin;

public final class QTPlugin implements Plugin {

    /**
     * Default Constructor.
     */
    public QTPlugin() {
        filter = new QTFilter();
    }

    /**
     * @return A New instance of the Quick time data viewer.
     */
    public DataViewer getNewDataViewer() {
        return new QTDataViewer();
    }

    /**
     * @return The file filter to use when looking for files that the Quicktime
     * plugin supports.
     */
    public FileFilter getFileFilter() {
        return filter;
    }

    /** The filter to use when looking for files that this plugin supports. */
    private QTFilter filter;
}
