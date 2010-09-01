package org.openshapa.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.SystemErrorException;
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
        
        try {
            OpenSHAPA.getProjectController().getDB().registerVocabListListener(view);
        } catch (SystemErrorException ex) {
            Logger.getLogger(VocabEditorC.class.getName()).log(Level.SEVERE, null, ex);
        }
        OpenSHAPA.getApplication().show(view);
    }
}
