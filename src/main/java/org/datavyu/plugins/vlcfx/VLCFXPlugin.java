package org.datavyu.plugins.vlcfx;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.datavyu.Datavyu;
import org.datavyu.plugins.DataViewer;
import org.datavyu.plugins.Filter;
import org.datavyu.plugins.FilterNames;
import org.datavyu.plugins.Plugin;

import javax.swing.*;
import java.awt.*;
import java.io.FileFilter;
import java.net.URL;
import java.util.List;


public class VLCFXPlugin implements Plugin {

    private static final List<Datavyu.Platform> VALID_OPERATING_SYSTEMS = Lists.newArrayList(Datavyu.Platform.WINDOWS, Datavyu.Platform.MAC, Datavyu.Platform.LINUX);


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
        return "vlcfxplugin";
    }

    @Override
    public Filter[] getFilters() {
        return FILTERS;
    }

    @Override
    public DataViewer getNewDataViewer(final Frame parent,
                                       final boolean modal) {
        return new VLCFXDataViewer(parent, modal);
    }

    @Override
    public String getPluginName() {
        return "VLCFX Video";
    }

    @Override
    public ImageIcon getTypeIcon() {
        URL typeIconURL = getClass().getResource(
                "/icons/vlc_cone.png");

        return new ImageIcon(typeIconURL);
    }

    @Override
    public Class<? extends DataViewer> getViewerClass() {
        return VLCFXDataViewer.class;
    }

    @Override
    public List<Datavyu.Platform> getValidPlatforms() {
        return VALID_OPERATING_SYSTEMS;
    }
}
