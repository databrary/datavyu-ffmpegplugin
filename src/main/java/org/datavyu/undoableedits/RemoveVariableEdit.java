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
import org.datavyu.controllers.DeleteColumnC;
import org.datavyu.models.db.Variable;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.List;

/**
 * Undoable edit for removing cells from the spreadsheet.
 */
public class RemoveVariableEdit extends VocabEditorEdit {
    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LogManager.getLogger(RemoveVariableEdit.class);

    private List<Variable> deletedVariables;

    public RemoveVariableEdit(List<Variable> varsToDelete) {
        deletedVariables = varsToDelete;
    }

    @Override
    public String getPresentationName() {
        String msg;
        if (deletedVariables.size() == 1) {
            msg = "Delete Variable \"" + deletedVariables.get(0).getName() + "\"";
        } else {
            msg = "Delete " + deletedVariables.size() + " Variables";
        }
        return msg;
    }

    @Override
    public void undo() throws CannotRedoException {
        super.undo();
        for (Variable var : deletedVariables) {
            model.addVariable(var);
        }
    }

    @Override
    public void redo() throws CannotUndoException {
        super.redo();

        new DeleteColumnC(deletedVariables);
        unselectAll();
    }
}
