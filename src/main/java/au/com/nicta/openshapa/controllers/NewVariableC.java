package au.com.nicta.openshapa.controllers;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.LogicErrorException;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.views.NewVariableV;
import javax.swing.JFrame;

/**
 * Controller for creating new variables.
 *
 * @author cfreeman (refactored into seperate controller class.)
 * @author switcher (logic of controller - pulled from spreadsheet panel.)
 */
public class NewVariableC implements Controller {
    /** The that this controller alters. */
    private Database model;

    /** The view that this controller gets information from. */
    private NewVariableV view;

    /**
     * Constructor, creates the new variable controller.
     */
    public NewVariableC() {
        model = OpenSHAPA.getDatabase();

        // Create the view, register this controller with it and display it.
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        view = new NewVariableV(mainFrame, false, this);
        OpenSHAPA.getApplication().show(view);
    }

    /**
     * Execute controller - i.e. add a new variable to the database.
     */
    public void execute() throws SystemErrorException, LogicErrorException {
        DataColumn dc = new DataColumn(model,
                                       view.getVariableName(),
                                       view.getVariableType());
        model.addColumn(dc);
    }
}
