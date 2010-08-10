package org.openshapa.plugins.spectrum;

import java.awt.Frame;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.Plugin;


/**
 * Plugin for viewing power spectrum density.
 */
public class SpectrumPlugin implements Plugin {

    /** Filter for supported files. */
    private static final FileFilter SUPPORTED_FILES = new SpectrumFileFilter();

    @Override public DataViewer getNewDataViewer(final Frame parent,
        final boolean modal) {

        return new SpectrumDataViewer(parent, modal);
    }

    @Override public FileFilter getFileFilter() {
        return SUPPORTED_FILES;
    }

    @Override public ImageIcon getTypeIcon() {
        URL typeIconURL = getClass().getResource(
                "/icons/spectrumplugin-icon.png");

        return new ImageIcon(typeIconURL);
    }

    @Override public boolean isActionSupported1() {
        return false;
    }

    @Override public boolean isActionSupported2() {
        return false;
    }

    @Override public boolean isActionSupported3() {
        return false;
    }

    @Override public String getClassifier() {
        return "openshapa.audio";
    }

}
