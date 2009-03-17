package au.com.nicta.openshapa.controllers;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.LogicErrorException;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.views.NewVariableV;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import org.apache.log4j.Logger;

/**
 * Controller for creating new variables.
 *
 * @author cfreeman
 */
public class NewVariableC implements ActionListener {
    /** The that this controller alters. */
    private Database model;

    /** The view that this controller gets information from. */
    private NewVariableV view;

    /** The logger for OpenSHAPA. */
    private static Logger logger = Logger.getLogger(NewVariableC.class);

    /**
     * Constructor, creates the new variable controller
     */
    public NewVariableC() {
        model = OpenSHAPA.getDatabase();

        // Create the view, register this controller with it and display it.
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        view = new NewVariableV(mainFrame, false, this);
        OpenSHAPA.getApplication().show(view);
    }

    /**
     * Action to invoke when a new variable is added to the database.
     *
     * @param evt The event that triggered this action.
     */
    public void actionPerformed(final ActionEvent evt) {
        try {
            DataColumn dc = new DataColumn(model, view.getVariableName(),
                                                  view.getVariableType());
            model.addColumn(dc);

        // Whoops, user has done something strange - show warning dialog.
        } catch (LogicErrorException fe) {
            OpenSHAPA.getApplication().showWarningDialog(fe);

        // Whoops, programmer has done something strange - show error
        // message.
        } catch (SystemErrorException e) {
            logger.error("Unable to add variable to database", e);
            OpenSHAPA.getApplication().showErrorDialog();
        }
    }
}
