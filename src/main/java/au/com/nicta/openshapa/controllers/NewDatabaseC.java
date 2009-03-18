package au.com.nicta.openshapa.controllers;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.MacshapaDatabase;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.util.Constants;
import au.com.nicta.openshapa.views.NewDatabaseV;
import au.com.nicta.openshapa.views.OpenSHAPAView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

/**
 * Controller for creating new databases.
 *
 * @author cfreeman
 */
public class NewDatabaseC implements ActionListener {
    /** The model that this controller alters. */
    private Database model;

    /** The view that this controller gets information from. */
    private NewDatabaseV view;

    /** The logger for OpenSHAPA. */
    private static Logger logger = Logger.getLogger(NewDatabaseC.class);

    /**
     * Constructor, creates the New Database controller.
     *
     * @param model The database (model) that this controller manipulates
     */
    public NewDatabaseC() {
        this.model = OpenSHAPA.getDatabase();

        // Create the view, register this controller with it and display it.
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        view = new NewDatabaseV(mainFrame, false, this);
        OpenSHAPA.getApplication().show(view);
    }

    /**
     * Action to invoke when a new database is created.
     *
     * @param evt The event that triggered this action.
     */
    public void actionPerformed(final ActionEvent evt) {
        try {
            model = new MacshapaDatabase();
            model.setName(view.getDatabaseName());
            model.setDescription(view.getDatabaseDescription());

            OpenSHAPA.setDatabase(model);
            OpenSHAPAView s = (OpenSHAPAView) OpenSHAPA.getApplication()
                                                       .getMainView();
            s.showSpreadsheet();

            // Update the name of the window to include the name of the new
            // database.
            JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
            ResourceMap rMap = OpenSHAPA.getApplication()
                                        .getContext()
                                        .getResourceMap(OpenSHAPA.class);

            mainFrame.setTitle(rMap.getString("Application.title")
                               + " - " + view.getDatabaseName());

            // TODO- BugzID:79 This needs to move above showSpreadsheet,
            // when setTicks is fully implemented.
            model.setTicks(Constants.TICKS_PER_SECOND);            
        } catch (SystemErrorException e) {
            logger.error("Unable to create new database", e);
        }
    }

}