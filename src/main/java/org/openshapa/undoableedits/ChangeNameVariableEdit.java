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
import org.openshapa.views.discrete.SpreadsheetColumn;

/**
 *
 * @author harold
 */
public class ChangeNameVariableEdit extends SpreadsheetEdit {
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(ChangeNameVariableEdit.class);  
    private String oldVarName;
    private String newVarName;
    
    public ChangeNameVariableEdit(String oldVarName, String newVarName) {
        super();
        this.oldVarName = oldVarName;
        this.newVarName = newVarName;
    }

    @Override
    public String getPresentationName() {
        String msg = "";

        if (canUndo()) {
            msg = "Change Variable Name from \"" + newVarName +  "\"" + " to " 
                                  + "\"" + oldVarName +  "\"";
        } else {
            msg = "Change Variable Name from \"" + oldVarName +  "\"" + " to " 
                                  + "\"" + newVarName +  "\"";            
        }
        return msg;
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        try {
            DataColumn dc = db.getDataColumn(oldVarName);
            dc.setName(newVarName);
            db.replaceColumn(dc);            
            unselectAll();
            selectVarName(newVarName);
        } catch (SystemErrorException e) {
             LOGGER.error("Unable to redo Change Variable Name.", e);
        }

    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        try {
            DataColumn dc = db.getDataColumn(newVarName);
            dc.setName(oldVarName);
            db.replaceColumn(dc);            
            unselectAll();
            selectVarName(oldVarName);
        } catch (SystemErrorException e) {
             LOGGER.error("Unable to undo Change Variable Name.", e);
        }
    }
    
    private void selectVarName(String varName) {
        SpreadsheetColumn sCol = this.getSpreadsheetColumn(varName);
        if (sCol != null) sCol.setSelected(true);
    } 
}
