package org.openshapa.views.continuous.quicktime;

import java.io.FileFilter;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.Plugin;

public final class QTPlugin implements Plugin {

    public DataViewer getDataViewer() {
        return new QTDataViewer();
    }

    public FileFilter getFileFilter() {
        return null;
    }
}
