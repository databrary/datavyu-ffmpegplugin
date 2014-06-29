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

import org.datavyu.models.db.Cell;
import org.datavyu.models.db.Variable;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * An undoable edit for adding a cell to the spreadsheet.
 */
public class AddCellEdit extends SpreadsheetEdit {
    /**
     * Variable name *
     */
    private String varName;
    private Cell addedCell;

    public AddCellEdit(String varName, Cell c) {
        super();
        this.varName = varName;
        this.addedCell = c;
    }

    @Override
    public String getPresentationName() {
        return "New Cell to " + varName;
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        Variable var = model.getVariable(varName);
        var.addCell(addedCell);
        unselectAll();
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        Variable var = model.getVariable(varName);
        var.removeCell(addedCell);
    }
}
