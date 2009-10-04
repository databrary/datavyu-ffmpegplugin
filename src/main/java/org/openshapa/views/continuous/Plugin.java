package org.openshapa.views.continuous;

import javax.swing.filechooser.FileFilter;

public interface Plugin {
    DataViewer getNewDataViewer();

    FileFilter getFileFilter();
}
