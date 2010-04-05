package org.openshapa.views.continuous.quicktime;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.Plugin;


public final class QTPlugin implements Plugin {

    /** The filter to use when looking for files that this plugin supports. */
    private QTFilter filter;

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
     *         plugin supports.
     */
    public FileFilter getFileFilter() {
        return filter;
    }

    /**
     * @return icon representing this plugin.
     */
    public ImageIcon getTypeIcon() {
        URL typeIconURL = getClass().getResource("/icons/qtplugin-icon.png");

        return new ImageIcon(typeIconURL);
    }

}
