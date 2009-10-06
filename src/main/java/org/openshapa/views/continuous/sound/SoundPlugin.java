package org.openshapa.views.continuous.sound;

import javax.swing.filechooser.FileFilter;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.Plugin;
import org.openshapa.views.continuous.quicktime.QTDataViewer;


public class SoundPlugin implements Plugin {

    /**
     * Default Constructor.
     */
    public SoundPlugin() {
        filter = new SoundFilter();
    }

    /**
     * @return A New instance of the Quick time data viewer.
     */
    public DataViewer getNewDataViewer() {
        return new SoundDataViewer();
    }

    /**
     * @return The file filter to use when looking for files that the Quicktime
     * plugin supports.
     */
    public FileFilter getFileFilter() {
        return filter;
    }

    /** The filter to use when looking for files that this plugin supports. */
    private SoundFilter filter;
}
