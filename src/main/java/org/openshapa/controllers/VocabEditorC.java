package org.openshapa.controllers;

import org.openshapa.OpenSHAPA;
import org.openshapa.views.VocabEditorV;
import javax.swing.JFrame;

/**
 * A controller for invoking the vocab editor.
 */
public class VocabEditorC {

    /**
     * Constructor.
     */
    public VocabEditorC() {
        // Create the view, register this controller with it and display it.
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        VocabEditorV view = new VocabEditorV(mainFrame, false);
        OpenSHAPA.getApplication().show(view);
    }
}
