package org.openshapa.controllers;

import com.usermetrix.jclient.UserMetrix;
import org.openshapa.OpenSHAPA;
import org.jdesktop.swingworker.SwingWorker;
import org.openshapa.models.db.Column;
import org.openshapa.models.db.DataColumn;
import org.openshapa.models.db.LogicErrorException;
import org.openshapa.models.db.MacshapaDatabase;
import org.openshapa.models.db.MatrixVocabElement;
import org.openshapa.models.db.NominalFormalArg;
import org.openshapa.models.db.SystemErrorException;

/**
 * Controller for creating new variables.
 */
public class NewVariableC extends SwingWorker<Object, String> {

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(NewVariableC.class);

    /** The name of the new variable to add to the database. */
    private String newVariableName;

    /** The type of the new variable to add to the database. */
    private MatrixVocabElement.MatrixType newVariableType;

    /** The model (db) that this controller manipulates. */
    private MacshapaDatabase model;

    /**
     * Constructor, creates the new variable controller.
     *
     * @param varName The name of the new variable to add to the database.
     *
     * @param varType The type of the new variable to use when adding stuff to
     * the database.
     */
    public NewVariableC(final String varName,
                        final MatrixVocabElement.MatrixType varType) {
        newVariableName = varName;
        newVariableType = varType;
        model = OpenSHAPA.getProjectController().getDB();
    }

    /**
     * The task to perform in the background behind the EDT.
     *
     * @return always null.
     */
    @Override protected Object doInBackground() {
        try {
            Column.isValidColumnName(OpenSHAPA.getProjectController().getDB(),
                                     newVariableName);
            DataColumn dc = new DataColumn(model,
                                           newVariableName,
                                           newVariableType);
            long id = model.addColumn(dc);
            dc = model.getDataColumn(id);

            // If the column is a matrix - default to a single nominal variable
            // rather than untyped.
            if (newVariableType.equals(MatrixVocabElement.MatrixType.MATRIX)) {
                MatrixVocabElement mve = model.getMatrixVE(dc.getItsMveID());
                mve.deleteFormalArg(0);
                mve.appendFormalArg(new NominalFormalArg(model, "<arg0>"));
                model.replaceMatrixVE(mve);
            }

            // Whoops, user has done something strange - show warning dialog.
        } catch (LogicErrorException fe) {
            OpenSHAPA.getApplication().showWarningDialog(fe);

            // Whoops, programmer has done something strange - show error
            // message.
        } catch (SystemErrorException e) {
            logger.error("Unable to add variable to database", e);
            OpenSHAPA.getApplication().showErrorDialog();

            // Whoops, unable to destroy dialog correctly.
        } catch (Throwable e) {
            logger.error("Unable to release window NewVariableV.", e);
        }

        return null;
    }
}
