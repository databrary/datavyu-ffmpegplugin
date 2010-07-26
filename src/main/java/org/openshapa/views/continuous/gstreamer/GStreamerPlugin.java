package org.openshapa.views.continuous.gstreamer;

import java.awt.Frame;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.Plugin;

public class GStreamerPlugin implements Plugin {

    private final FileFilter filter = new FileFilter() {
        /**
         * @return The description of the file filter.
         */
        public String getDescription() {
            return new String("Movie files");
        }

        /**
         * Determines if the file filter will accept the supplied file.
         *
         * @param file The file to check if this file will accept.
         *
         * @return true if the file is to be accepted, false otherwise.
         */
        @Override
        public boolean accept(File file) {
            return (file.getName().endsWith(".mov")
                    || file.getName().endsWith(".avi")
                    || file.getName().endsWith(".mpg")
                    || file.getName().endsWith(".mpeg")
                    || file.getName().endsWith(".mp4")
                    || file.isDirectory());
            }
    };

    @Override public DataViewer getNewDataViewer(Frame parent, boolean modal) {
        // TODO Auto-generated method stub
        return new GStreamerDataViewer(parent, modal);
    }

    @Override public FileFilter getFileFilter() {
        return filter;
    }

    @Override public ImageIcon getTypeIcon() {
        URL typeIconURL = getClass().getResource("/icons/gstreamerplugin-icon.png");
        return new ImageIcon(typeIconURL);
    }

    @Override public boolean isActionSupported1() {
        return true;
    }

    @Override public boolean isActionSupported2() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override public boolean isActionSupported3() {
        // TODO Auto-generated method stub
        return false;
    }
}
