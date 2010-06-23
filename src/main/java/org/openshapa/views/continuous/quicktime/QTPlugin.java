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

    public DataViewer getNewDataViewer(java.awt.Frame parent, boolean modal) {
        return new QTDataViewer(parent, modal);
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

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#getActionButtonIcon1()
     */
    public ImageIcon getActionButtonIcon1() {
        URL typeIconURL = getClass().getResource("/icons/audio-volume.png");
        return new ImageIcon(typeIconURL);
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#getActionButtonIcon2()
     */
    public ImageIcon getActionButtonIcon2() {
        URL typeIconURL = getClass().getResource("/icons/eye.png");

        return new ImageIcon(typeIconURL);
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#getActionButtonIcon3()
     */
    public ImageIcon getActionButtonIcon3() {
        URL typeIconURL = getClass().getResource("/icons/resize.png");

        return new ImageIcon(typeIconURL);
    }

    /**
     * This action is associated with the volume control option.
     *
     * @see org.openshapa.views.continuous.Plugin#isActionSupported1()
     */
    public boolean isActionSupported1() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#isActionSupported2()
     */
    public boolean isActionSupported2() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#isActionSupported3()
     */
    public boolean isActionSupported3() {
        return true;
    }

}
