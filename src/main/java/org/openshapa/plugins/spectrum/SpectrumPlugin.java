package org.openshapa.plugins.spectrum;

import java.io.File;
import java.io.IOException;

import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.sun.jna.Platform;

import java.awt.Frame;

import java.io.FileFilter;

import java.net.URL;

import java.util.List;

import javax.swing.ImageIcon;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import org.gstreamer.Gst;

import com.google.common.collect.Lists;

import org.openshapa.plugins.DataViewer;
import org.openshapa.plugins.Filter;
import org.openshapa.plugins.FilterNames;
import org.openshapa.plugins.Plugin;


/**
 * Plugin for viewing power spectrum density.
 */
public class SpectrumPlugin implements Plugin {

    private static final Filter AUDIO_FILTER = new Filter() {
            final SuffixFileFilter ff;
            final List<String> ext;

            {
                ext = Lists.newArrayList(".wav", ".mp3", ".ogg");
                ff = new SuffixFileFilter(ext, IOCase.INSENSITIVE);
            }

            @Override public FileFilter getFileFilter() {
                return ff;
            }

            @Override public String getName() {
                return FilterNames.AUDIO.getFilterName();
            }

            @Override public Iterable<String> getExtensions() {
                return ext;
            }
        };

    private static final Filter VIDEO_FILTER = new Filter() {
            final SuffixFileFilter ff;
            final List<String> ext;

            {
                ext = Lists.newArrayList(".avi", ".mov", ".mpg", ".mp4");
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

    static {
        Gst.init();

        try {
            unpackAddons();
        } catch (IOException e) {
            e.printStackTrace();
        }
//TODO need to do this somewhere to balance out the init/deinit calls
//      Gst.deinit();
    }

    /** Audio plugin addon file. */
    private static File addonFile;

    /** Extract plugin addons to a temp location. */
    private static void unpackAddons() throws IOException {
        addonFile = File.createTempFile(UUID.randomUUID().toString(), ".exe");
        addonFile.deleteOnExit();

        if (Platform.isWindows()) {
            FileUtils.copyInputStreamToFile(SpectrumPlugin.class
                .getResourceAsStream(
                    "/org/openshapa/plugins/spectrum/audio-points.exe"),
                addonFile);
        }
    }

    /** Audio plugin addon file. */
    public static File getAddonFile() {
        return addonFile;
    }

    @Override public DataViewer getNewDataViewer(final Frame parent,
        final boolean modal) {

        return new SpectrumDataViewer(parent, modal);
    }

    @Override public ImageIcon getTypeIcon() {
        URL typeIconURL = getClass().getResource(
                "/icons/spectrum/spectrumplugin-icon.png");

        return new ImageIcon(typeIconURL);
    }

    @Override public String getClassifier() {
        return "openshapa.audio";
    }

    @Override public Filter[] getFilters() {
        return new Filter[] { AUDIO_FILTER, VIDEO_FILTER };
    }

    @Override public String getPluginName() {
        return "UNSTABLE: Audio Spectrum";
    }
}
