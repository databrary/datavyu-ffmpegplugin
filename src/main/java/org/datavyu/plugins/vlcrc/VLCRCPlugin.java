package org.datavyu.plugins.vlcrc;

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


public class VLCRCPlugin implements Plugin {

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
        return "vlcrcplugin";
    }

    @Override public Filter[] getFilters() {
        return FILTERS;
    }

    @Override public DataViewer getNewDataViewer(final Frame parent,
        final boolean modal) {
        return new VLCRCDataViewer(parent, modal);
    }

    @Override public String getPluginName() {
        return "VLC RC Video";
    }

    @Override public ImageIcon getTypeIcon() {
        return null;
    }

    @Override public Class<? extends DataViewer> getViewerClass() {
        return VLCRCDataViewer.class;
    }

}
