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

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import org.datavyu.controllers.DeleteColumnC;
import org.datavyu.models.db.Cell;
import org.datavyu.models.db.UserWarningException;
import org.datavyu.models.db.Variable;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Undoable edit for removing cells from the spreadsheet.
 */
public class RemoveVariableEdit extends VocabEditorEdit {
    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = UserMetrix.getLogger(RemoveVariableEdit.class);

    private List<VariableTO> varToDeleteTOs;

    public RemoveVariableEdit(List<Variable> varsToDelete) {
        varToDeleteTOs = new ArrayList<VariableTO>();

        for (Variable var : varsToDelete) {
            int pos = 0;
            for (Variable var2 : model.getAllVariables()) {
                if (var2.getName().equals(var.getName())) {
                    break;
                } else {
                    pos++;
                }
            }
            varToDeleteTOs.add(new VariableTO(var, pos));
        }
        Collections.sort(varToDeleteTOs);
    }

    @Override
    public String getPresentationName() {
        String msg;
        if (varToDeleteTOs.size() == 1) {
            msg = "Delete Variable \"" + varToDeleteTOs.get(0).getName() + "\"";
        } else {
            msg = "Delete " + varToDeleteTOs.size() + " Variables";
        }
        return msg;
    }

    @Override
    public void undo() throws CannotRedoException {
        super.undo();
        for (VariableTO varTO : varToDeleteTOs) {
            try {
                Variable newVar = model.createVariable(varTO.getName(), varTO.getType().type);
                model.getAllVariables().remove(newVar);
                model.getAllVariables().add(varTO.getPosition(), newVar);

                for (CellTO c : varTO.getTOCells()) {
                    Cell newCell = newVar.createCell();
                    newCell.setOnset(c.getOnset());
                    newCell.setOffset(c.getOffset());
                    newCell.getValue().set(c.getValue());
                }

            } catch (UserWarningException e) {
                LOGGER.error("Unable to undo.", e);
            }
        }
    }

    @Override
    public void redo() throws CannotUndoException {
        super.redo();

        List<Variable> varsToDelete = new ArrayList<Variable>();
        for (VariableTO varTO : varToDeleteTOs) {
            varsToDelete.add(model.getVariable(varTO.getName()));
        }

        new DeleteColumnC(varsToDelete);
        unselectAll();
    }
}
