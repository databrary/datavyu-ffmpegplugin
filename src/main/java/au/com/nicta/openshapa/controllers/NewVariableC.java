package au.com.nicta.openshapa.controllers;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.views.NewVariableV;
import javax.swing.JFrame;

/**
 * Controller for creating new variables.
 *
 * @author cfreeman (refactored into seperate controller class.)
 * @author switcher (logic of controller - pulled from spreadsheet panel.)
 */
public class NewVariableC {
    /**
     * Constructor, creates the new variable controller.
     */
    public NewVariableC() {
        // Create the view, and display it.
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        NewVariableV view = new NewVariableV(mainFrame, false);
        OpenSHAPA.getApplication().show(view);
    }
}
