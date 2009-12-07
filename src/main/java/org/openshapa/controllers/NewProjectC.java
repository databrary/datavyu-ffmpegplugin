package org.openshapa.controllers;

import javax.swing.JFrame;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.NewProjectV;

/**
 * Controller for creating a new project.
 */
public class NewProjectC {

    public NewProjectC() {
        // Create the view, register this controller with it and display it.
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        NewProjectV view = new NewProjectV(mainFrame, true);
        OpenSHAPA.getApplication().show(view);
    }

}
