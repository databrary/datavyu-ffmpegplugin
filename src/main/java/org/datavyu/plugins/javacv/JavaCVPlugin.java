package org.datavyu.plugins.javacv;

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


public class JavaCVPlugin implements Plugin {

    private static final Filter VIDEO_FILTER = new Filter() {
        final SuffixFileFilter ff;
        final List<String> ext;

        {
            ext = Lists.newArrayList(".avi", ".mov", ".mpg", ".mp4", ".mkv", ".m4v");
            ff = new SuffixFileFilter(ext, IOCase.INSENSITIVE);
        }

        @Override
        public FileFilter getFileFilter() {
            return ff;
        }

        @Override
        public String getName() {
            return FilterNames.VIDEO.getFilterName();
        }

        @Override
        public Iterable<String> getExtensions() {
            return ext;
        }
    };

    private static final Filter[] FILTERS = new Filter[]{VIDEO_FILTER};

    @Override
    public String getClassifier() {
        return "javacvplugin";
    }

    @Override
    public Filter[] getFilters() {
        return FILTERS;
    }

    @Override
    public DataViewer getNewDataViewer(final Frame parent,
                                       final boolean modal) {
        return new JavaCVDataViewer(parent, modal);
    }

    @Override
    public String getPluginName() {
        return "JavaCV Video";
    }

    @Override
    public ImageIcon getTypeIcon() {
        URL typeIconURL = getClass().getResource(
                "/icons/vlc_cone.png");

        return new ImageIcon(typeIconURL);
    }

    @Override
    public Class<? extends DataViewer> getViewerClass() {
        return JavaCVDataViewer.class;
    }

}
