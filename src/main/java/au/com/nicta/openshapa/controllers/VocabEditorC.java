package au.com.nicta.openshapa.controllers;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.views.VocabEditorV;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import org.apache.log4j.Logger;

/**
 *
 * @author cfreeman
 */
public class VocabEditorC implements ActionListener {
    
    /** The that this controller alters. */
    private Database model;

    /** The view that this controller gets information from. */
    private VocabEditorV view;

    /** The logger for OpenSHAPA. */
    private static Logger logger = Logger.getLogger(NewVariableC.class);

    public VocabEditorC() {
        model = OpenSHAPA.getDatabase();

        // Create the view, register this controller with it and display it.
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        view = new VocabEditorV(mainFrame, false, this);
        OpenSHAPA.getApplication().show(view);
    }

    public void actionPerformed(final ActionEvent evt) {
        
    }
}
