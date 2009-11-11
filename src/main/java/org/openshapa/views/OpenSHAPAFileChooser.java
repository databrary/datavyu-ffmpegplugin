package org.openshapa.views;

import java.awt.Component;
import javax.swing.JFileChooser;
import org.openshapa.Configuration;

/**
 * A file chooser to use within OpenSHAPA - overides the general JFileChooser.
 */
public final class OpenSHAPAFileChooser extends JFileChooser {

    /**
     * Pops up an "Open File" file chooser dialog. Note that the text that
     * appears in the approve button is determined by the L&F.
     *
     * @param parent the parent component of the dialog, can be null; see
     * showDialog for details
     *
     * @return The return state of the file chooser on popdown:
     * JFileChooser.CANCEL_OPTION
     * JFileChooser.APPROVE_OPTION
     * JFileCHooser.ERROR_OPTION if an error occurs or the dialog is dismissed
     */
    @Override
    public int showOpenDialog(final Component parent) {
        this.setCurrentDirectory(Configuration.getInstance().getLCDirectory());
        int result = super.showOpenDialog(parent);
        Configuration.getInstance().setLCDirectory(this.getCurrentDirectory());

        return result;
    }

    /**
     * Pops up a "Save File" file chooser dialog. Note that the text that
     * appears in the approve button is determined by the L&F.
     *
     * @param parent the parent component of the dialog, can be null; see
     * showDialog for details
     *
     * @return The return state of the file chooser on popdown:
     * JFileChooser.CANCEL_OPTION
     * JFileChooser.APPROVE_OPTION
     * JFileCHooser.ERROR_OPTION if an error occurs or the dialog is dismissed
     */
    @Override
    public int showSaveDialog(final Component parent) {
        this.setCurrentDirectory(Configuration.getInstance().getLCDirectory());
        int result = super.showSaveDialog(parent);
        Configuration.getInstance().setLCDirectory(this.getCurrentDirectory());

        return result;
    }
}
