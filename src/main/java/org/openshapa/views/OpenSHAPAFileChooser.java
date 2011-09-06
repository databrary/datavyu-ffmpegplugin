/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    @Override public int showOpenDialog(final Component parent) {
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
    @Override public int showSaveDialog(final Component parent) {
        this.setCurrentDirectory(Configuration.getInstance().getLCDirectory());

        int result = super.showSaveDialog(parent);
        Configuration.getInstance().setLCDirectory(this.getCurrentDirectory());

        return result;
    }
}
