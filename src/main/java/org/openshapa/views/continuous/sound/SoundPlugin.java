package org.openshapa.views.continuous.sound;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.Plugin;


public class SoundPlugin implements Plugin {

    /** The filter to use when looking for files that this plugin supports. */
    private SoundFilter filter;

    /**
     * Default Constructor.
     */
    public SoundPlugin() {
        filter = new SoundFilter();
    }

    /**
     * @return A New instance of the Sound data viewer.
     */
    public DataViewer getNewDataViewer(java.awt.Frame parent, boolean modal) {
        return new SoundDataViewer(parent, modal);
    }

    /**
     * @return The file filter to use when looking for files that the Quicktime
     *         plugin supports.
     */
    public FileFilter getFileFilter() {
        return filter;
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#getTypeIcon()
     */
    public ImageIcon getTypeIcon() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#getActionButtonIcon1()
     */
    public ImageIcon getActionButtonIcon1() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#getActionButtonIcon2()
     */
    public ImageIcon getActionButtonIcon2() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#getActionButtonIcon3()
     */
    public ImageIcon getActionButtonIcon3() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#isActionSupported1()
     */
    public boolean isActionSupported1() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#isActionSupported2()
     */
    public boolean isActionSupported2() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.openshapa.views.continuous.Plugin#isActionSupported3()
     */
    public boolean isActionSupported3() {
        return false;
    }

}
