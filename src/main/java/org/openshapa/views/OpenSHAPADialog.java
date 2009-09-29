package org.openshapa.views;

import javax.swing.JDialog;

/**
 * Generic OpenSHAPA dialog - handles work common to all dialogs (handling of
 * keystrokes, etc).
 */
public abstract class OpenSHAPADialog extends JDialog {

    /**
     * Constructor. Creates a new OpenSHAPADialog.
     *
     * @param parent The parent of this form.
     * @param modal Should the dialog be modal or not?
     */
    public OpenSHAPADialog(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
}
