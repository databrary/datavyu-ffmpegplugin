/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.undoableedits;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.Vector;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openshapa.controllers.DeleteColumnC;
import org.openshapa.models.db.DeprecatedDatabase;
import org.openshapa.models.db.DeprecatedVariable;
import org.openshapa.models.db.legacy.DataColumn;
import org.openshapa.models.db.legacy.MatrixVocabElement;
import org.openshapa.models.db.legacy.NominalFormalArg;
import org.openshapa.models.db.legacy.SystemErrorException;

/**
 *
 * @author harold
 */
public class AddVariableEdit extends SpreadsheetEdit {
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(AddVariableEdit.class);  
    private String varName;
    private MatrixVocabElement.MatrixType varType;
    private boolean matType;
    
    public AddVariableEdit(String variableName, MatrixVocabElement.MatrixType variableType, boolean matrixType) {
        super();
        this.varName = variableName;
        this.varType = variableType;
        this.matType = matrixType;
    }

    @Override
    public String getPresentationName() {
        return "New Variable \"" + varName + "\"";
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        try {
            DataColumn dc = new DataColumn(db, varName, varType);        
            DeprecatedVariable var = new DeprecatedVariable(dc);
            model.addVariable(var);
            // If the column is a matrix - default to a single nominal variable
            // rather than untyped.
            if (matType) {
                MatrixVocabElement mve = db.getMatrixVE(var.getLegacyVariable().getItsMveID());
                mve.deleteFormalArg(0);
                mve.appendFormalArg(new NominalFormalArg(db, "<arg0>"));
                db.replaceMatrixVE(mve);                
            }         
            unselectAll();
            var.setSelected(true);
        } catch (SystemErrorException e) {
             LOGGER.error("Unable to redo New Variable.", e);
        }

    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        Vector<DataColumn> cols = new Vector<DataColumn>();
        DeprecatedVariable v = (DeprecatedVariable) getVariable(varName);
        cols.add(v.getLegacyColumn());
        new DeleteColumnC(cols);
        //((DeprecatedDatabase)model).removeVariable(v);
    }
    
}
