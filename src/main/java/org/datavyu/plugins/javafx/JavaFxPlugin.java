package org.datavyu.plugins.javafx;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.datavyu.plugins.DataViewer;
import org.datavyu.plugins.Filter;
import org.datavyu.plugins.FilterNames;
import org.datavyu.plugins.Plugin;

import javax.swing.*;
import java.awt.*;
import java.io.FileFilter;
import java.net.URL;
import java.util.List;


public class JavaFxPlugin implements Plugin {

    private static final Filter VIDEO_FILTER = new Filter() {
        final SuffixFileFilter ff;
        final List<String> ext;

        {
            ext = Lists.newArrayList(".mp4", ".m4v");
            ff = new SuffixFileFilter(ext, IOCase.INSENSITIVE);
        }

        @Override
        public FileFilter getFileFilter() {
            return ff;
        }

        @Override
        public String getName() {
            return FilterNames.MP4.getFilterName();
        }

        @Override
        public Iterable<String> getExtensions() {
            return ext;
        }
    };

    private static final Filter[] FILTERS = new Filter[]{VIDEO_FILTER};

    @Override
    public String getClassifier() {
        return "datavyu.video";
    }

    @Override
    public Filter[] getFilters() {
        return FILTERS;
    }

    @Override
    public DataViewer getNewDataViewer(final Frame parent,
                                       final boolean modal) {
        return new JavaFXDataViewer(parent, modal);
    }

    @Override
    public String getPluginName() {
        return "JavaFX Video";
    }

    @Override
    public ImageIcon getTypeIcon() {
        URL typeIconURL = getClass().getResource(
                "/icons/vlc_cone.png");

        return new ImageIcon(typeIconURL);
    }

    @Override
    public Class<? extends DataViewer> getViewerClass() {
        return JavaFXDataViewer.class;
    }

}
