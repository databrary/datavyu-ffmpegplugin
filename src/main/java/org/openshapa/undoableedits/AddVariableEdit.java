/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.undoableedits;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.Vector;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openshapa.controllers.DeleteColumnC;
import org.openshapa.models.db.DeprecatedVariable;
import database.DataColumn;
import database.MatrixVocabElement;
import database.NominalFormalArg;
import database.SystemErrorException;

/**
 *
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
        cols.add(v.getLegacyVariable());
        new DeleteColumnC(cols);
        //((DeprecatedDatabase)model).removeVariable(v);
    }
    
}
