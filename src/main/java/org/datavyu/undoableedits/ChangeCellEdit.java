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

import org.datavyu.Datavyu;
import org.datavyu.models.db.Cell;
import org.datavyu.models.db.Variable;
import org.datavyu.util.Constants;
import org.datavyu.views.discrete.SpreadsheetCell;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * An undoable edit for altering the contents of a cell.
 */
abstract public class ChangeCellEdit extends SpreadsheetEdit {
    /**
     * Column's index
     */
    protected int colIndex = -1;

    /**
     * Index of this cell in its host column. Initially set to -1 as this is not
     * a valid index.
     */
    protected int ord = -1;

    protected String columnName;

    /**
     * The granularity of the edit - fine or coarse.
     */
    protected Granularity granularity;

    public Granularity getGranularity() {
        return granularity;
    }

    public enum Granularity {
        FINEGRAINED,
        COARSEGRAINED
    }

    public ChangeCellEdit(Cell c, Granularity granularity) {
        // New constructor.
        super();
        this.granularity = granularity;

        Variable var = Datavyu.getProjectController().getDB().getVariable(c);
        columnName = var.getName();

        for (Cell cell : var.getCells()) {
            ord++;

            if (cell.equals(c)) {
                return;
            }
        }
    }

    public ChangeCellEdit(Cell c) {
        this(c, Granularity.COARSEGRAINED);
    }

    @Override
    public String getPresentationName() {
        return "Change " + " ";
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        updateCell();
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        updateCell();
    }

    protected void updateCell() {
        Variable var = Datavyu.getProjectController().getDB().getVariable(columnName);
        Cell cell = var.getCells().get(ord);
        updateCell(cell);
/*
        if (this.granularity == Granularity.COARSEGRAINED) {
            updateSpreadsheetCell(cell);
        }
*/
    }

    abstract protected void updateCell(Cell cell);

    protected void updateSpreadsheetCell(Cell cell) {
        unselectAll();
        SpreadsheetCell sCell = getSpreadsheetCell(cell);
        cell.setHighlighted(true);
        sCell.requestFocusInWindow();
        selectField(sCell);
    }

    abstract protected void selectField(SpreadsheetCell sCell);

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ChangeCellEdit) &&
                ((ChangeCellEdit) obj).granularity == this.granularity;
    }

    @Override
    public int hashCode() {
        double hash = super.hashCode();
        hash += granularity.hashCode() * Constants.SEED1;
        long val = Double.doubleToLongBits(hash);
        return (int) (val ^ (val >>> 32));
    }
}
