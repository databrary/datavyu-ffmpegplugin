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
package org.openshapa.plugins.spectrum.swing;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;


/**
 * Dialog containing the spectrum analyzer.
 */
public final class SpectrumDialog extends JDialog {

    public SpectrumDialog(final Dialog owner, final boolean modal) {
        super(owner, modal);
        initView();
    }

    public SpectrumDialog(final Dialog owner, final String title,
        final boolean modal, final GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initView();
    }

    public SpectrumDialog(final Dialog owner, final String title,
        final boolean modal) {
        super(owner, title, modal);
        initView();
    }

    public SpectrumDialog(final Dialog owner, final String title) {
        super(owner, title);
        initView();
    }

    public SpectrumDialog(final Dialog owner) {
        super(owner);
        initView();
    }

    public SpectrumDialog(final Frame owner, final boolean modal) {
        super(owner, modal);
        initView();
    }

    public SpectrumDialog(final Frame owner, final String title,
        final boolean modal, final GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initView();
    }

    public SpectrumDialog(final Frame owner, final String title,
        final boolean modal) {
        super(owner, title, modal);
        initView();
    }

    public SpectrumDialog(final Frame owner, final String title) {
        super(owner, title);
        initView();
    }

    public SpectrumDialog(final Frame owner) {
        super(owner);
        initView();
    }

    public SpectrumDialog(final Window owner, final ModalityType modalityType) {
        super(owner, modalityType);
        initView();
    }

    public SpectrumDialog(final Window owner, final String title,
        final ModalityType modalityType, final GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        initView();
    }

    public SpectrumDialog(final Window owner, final String title,
        final ModalityType modalityType) {
        super(owner, title, modalityType);
        initView();
    }

    public SpectrumDialog(final Window owner, final String title) {
        super(owner, title);
        initView();
    }

    public SpectrumDialog(final Window owner) {
        super(owner);
        initView();
    }

    /**
     * Initalize this dialog's view.
     */
    private void initView() {
        setMinimumSize(new Dimension(800, 480));
        setName("SpectrumDialog");
        setTitle("Spectrum");

        setBackground(Color.BLACK);

        getContentPane().setLayout(new MigLayout("ins 0", "[grow]", "[grow]"));
    }

    /**
     * Set the spectrum viewer to display.
     *
     * @param spectrum
     *            Spectrum viewer.
     */
    public void setSpectrum(final Spectrum spectrum) {

        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Should be called from EDT");
        }

        getContentPane().add(spectrum, "grow");
        validate();
    }

}
