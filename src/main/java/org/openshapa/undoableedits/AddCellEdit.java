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

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openshapa.controllers.CreateNewCellC;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.Variable;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetCell;

/**
 * An undoable edit for adding a cell to the spreadsheet.
 */
public class AddCellEdit extends SpreadsheetEdit {
    /** Variable name **/
    private String varName;

    public AddCellEdit(String varName) {
        super();
        this.varName = varName;
    }

    @Override
    public String getPresentationName() {
        return "New Cell to " + varName;
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        CreateNewCellC newCellController = new CreateNewCellC();
        Variable var = model.getVariable(varName);
        newCellController.createCell(var);
        unselectAll();
        if ((var.getCells() != null) && (var.getCells().size() > 0)) {
            Cell cell = var.getCells().get(var.getCells().size()-1);
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        Variable var = model.getVariable(varName);
        Cell cellToRemove = var.getCells().get(var.getCells().size() - 1);
        OpenSHAPA.getProjectController().getDB().removeCell(cellToRemove);
    }
}
