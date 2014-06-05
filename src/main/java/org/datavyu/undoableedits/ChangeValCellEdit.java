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
import org.datavyu.util.Constants;
import org.datavyu.views.discrete.SpreadsheetCell;

/**
 * An undoable edit for changing the onset of a cell.
 */
public class ChangeValCellEdit extends ChangeCellEdit {
    /** The value held by the cell. */
    String cellValue = null;
    String oldValue = null;

    public ChangeValCellEdit(Cell c, String oldValue, Granularity granularity) {
        super(c, granularity);
        cellValue = oldValue;
    }

    public ChangeValCellEdit(Cell c, String oldValue) {
        this(c, oldValue, Granularity.COARSEGRAINED);
    }

    @Override
    public String getPresentationName() {
        return super.getPresentationName() + "Val Cell (" + columnName + ") to " + cellValue;
    }

    @Override
    protected void updateCell(Cell cell) {
        String currentCellValue = cell.getValueAsString();

        cell.getValue().set(cellValue);
        this.cellValue = currentCellValue;
        Datavyu.getProjectController().getSpreadsheetPanel().redrawCells();
    }

    @Override
    protected void selectField(SpreadsheetCell sCell) {
        sCell.selectVal();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        // Must be this class to be here
        ChangeValCellEdit t = (ChangeValCellEdit) obj;
        return super.equals(obj) && cellValue.equals(t.cellValue);
    }

    @Override
    public int hashCode() {
        double hash = super.hashCode();
        hash += cellValue.hashCode() * Constants.SEED1;
        long val = Double.doubleToLongBits(hash);

        return (int) (val ^ (val >>> 32));
    }
}
