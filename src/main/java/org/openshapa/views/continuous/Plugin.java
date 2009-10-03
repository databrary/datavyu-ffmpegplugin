package org.openshapa.views.continuous;

import java.io.FileFilter;

public interface Plugin {
    DataViewer getDataViewer();

    FileFilter getFileFilter();
}
