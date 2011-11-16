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
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openshapa.controllers.DeleteColumnC;
import java.util.ArrayList;
import java.util.List;
import org.openshapa.models.db.UserWarningException;
import org.openshapa.models.db.Variable;

/**
 *
 */
public class AddVariableEdit extends SpreadsheetEdit {
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(AddVariableEdit.class);  
    private String varName;
    private Variable.type varType;
    
    public AddVariableEdit(String variableName, Variable.type variableType) {
        super();
        this.varName = variableName;
        this.varType = variableType;
    }

    @Override
    public String getPresentationName() {
        return "New Variable \"" + varName + "\"";
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        try {
            unselectAll();
            model.createVariable(varName, varType);

        } catch (UserWarningException e) {
             LOGGER.error("Unable to redo New Variable.", e);
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        List<Variable> cols = new ArrayList<Variable>();
        cols.add(model.getVariable(varName));
        new DeleteColumnC(cols);
    }
    
}
