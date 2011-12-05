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


import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openshapa.OpenSHAPA;
import org.openshapa.controllers.project.ProjectController;
import org.openshapa.models.db.Datastore;
import org.openshapa.models.db.Cell;
import org.openshapa.views.OpenSHAPAView;
import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.SpreadsheetPanel;

/**
 * An undoable edit for altering the contents of a spreadsheet.
 */
public abstract class SpreadsheetEdit extends AbstractUndoableEdit {
    protected ProjectController controller;
    protected Datastore model;    
    protected OpenSHAPAView view;

    public SpreadsheetEdit() {
        super();
        controller = OpenSHAPA.getProjectController();
        model = controller.getDB();        
        view = OpenSHAPA.getView();
    }

    @Override
    public String getPresentationName() {
        return super.getPresentationName();
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
    }

    protected SpreadsheetCell getSpreadsheetCell(Cell cell) {
        for (SpreadsheetColumn sCol : getSpreadsheet().getColumns()) {
            for (SpreadsheetCell sCell : sCol.getCells()) {
                if (sCell.getCell().equals(cell)) {
                    return sCell;
                }
            }
        }
        return null;
    }

    protected SpreadsheetColumn getSpreadsheetColumn(String columnName) {
        for (SpreadsheetColumn sCol : getSpreadsheet().getColumns()) {
            if (sCol.getColumnName().equals(columnName)) {
                    return sCol;
            }
        }
        return null;
    }

    protected void unselectAll() {
        view.getSpreadsheetPanel().clearColumnSelection();
        view.getSpreadsheetPanel().clearCellSelection();
    }

    protected class CellPos {
        public String varName; // Column
        public int ord;      // Row
        public CellPos(String varName, int index) {
            this.varName = varName;
            this.ord = index;
        }
    }

    protected SpreadsheetPanel getSpreadsheet() {
        return view.getSpreadsheetPanel();
    }
}
