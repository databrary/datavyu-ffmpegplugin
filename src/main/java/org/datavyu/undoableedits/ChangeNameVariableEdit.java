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
package org.datavyu.undoableedits;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.Datavyu;
import org.datavyu.models.db.UserWarningException;
import org.datavyu.models.db.Variable;
import org.datavyu.views.discrete.SpreadsheetColumn;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 */
public class ChangeNameVariableEdit extends SpreadsheetEdit {
    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LogManager.getLogger(ChangeNameVariableEdit.class);
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
            msg = "Change Variable Name from \"" + newVarName + "\"" + " to "
                    + "\"" + oldVarName + "\"";
        } else {
            msg = "Change Variable Name from \"" + oldVarName + "\"" + " to "
                    + "\"" + newVarName + "\"";
        }
        return msg;
    }

    @Override
    public void redo() throws CannotRedoException {
        LOGGER.info("REDO VariableNameEdit");
        super.redo();
        try {
            Variable var = model.getVariable(oldVarName);
            var.setName(newVarName);
            unselectAll();
            //selectVarName(newVarName);
        } catch (UserWarningException uwe) {
            Datavyu.getApplication().showWarningDialog(uwe);
            throw new CannotRedoException();
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        LOGGER.info("UNDO VariableNameEdit");
        super.undo();
        try {
            Variable var = model.getVariable(newVarName);
            var.setName(oldVarName);
            unselectAll();
            //selectVarName(oldVarName);
        } catch (UserWarningException uwe) {
            Datavyu.getApplication().showWarningDialog(uwe);
            throw new CannotRedoException();
        }
    }

    private void selectVarName(String varName) {
        SpreadsheetColumn sCol = this.getSpreadsheetColumn(varName);
        if (sCol != null) sCol.setSelected(true);
    }
}
