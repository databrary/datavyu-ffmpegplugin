package org.datavyu.plugins.vlcoop;

import com.google.common.collect.Lists;
import java.awt.Frame;

import java.io.FileFilter;

import java.util.List;

import javax.swing.ImageIcon;
import org.apache.commons.io.IOCase;

import org.apache.commons.io.filefilter.SuffixFileFilter;

import org.datavyu.plugins.DataViewer;
import org.datavyu.plugins.Filter;
import org.datavyu.plugins.FilterNames;
import org.datavyu.plugins.Plugin;


public class VLCOOPPlugin implements Plugin {

    private static final Filter VIDEO_FILTER = new Filter() {
            final SuffixFileFilter ff;
            final List<String> ext;

            {
                ext = Lists.newArrayList(".avi", ".mov", ".mpg", ".mp4", ".mkv");
                ff = new SuffixFileFilter(ext, IOCase.INSENSITIVE);
            }

            @Override public FileFilter getFileFilter() {
                return ff;
            }

            @Override public String getName() {
                return FilterNames.VIDEO.getFilterName();
            }

            @Override public Iterable<String> getExtensions() {
                return ext;
            }
        };

    private static final Filter[] FILTERS = new Filter[] { VIDEO_FILTER };

    @Override public String getClassifier() {
        return "vlcoopplugin";
    }

    @Override public Filter[] getFilters() {
        return FILTERS;
    }

    @Override public DataViewer getNewDataViewer(final Frame parent,
        final boolean modal) {
        return new VLCOOPDataViewer(parent, modal);
    }

    @Override public String getPluginName() {
        return "VLC OOP Video";
    }

    @Override public ImageIcon getTypeIcon() {
        return null;
    }

    @Override public Class<? extends DataViewer> getViewerClass() {
        return VLCOOPDataViewer.class;
    }

}
