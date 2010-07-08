package org.openshapa.plugins.spectrum;

import java.awt.Frame;

import java.io.File;

import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;

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
        return null;
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

}
