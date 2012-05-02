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

import org.openshapa.models.db.Cell;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.SpreadsheetCell;

/**
 * An undoable edit for changing the onset of a cell.
 */
public class ChangeOnsetCellEdit extends ChangeCellEdit { 
    /** onset of cell */
    private long oldOnset = -1;
    private long newOnset = -1;

    public ChangeOnsetCellEdit(Cell c, long oldOnset, long newOnset, Granularity granularity) {
        super(c, granularity);
        this.oldOnset = oldOnset;
        this.newOnset = newOnset;
    }

    @Override
    public String getPresentationName() {
        return "Changed the onset of a cell within '" + columnName + "' to " + newOnset;
    }

    @Override
    protected void updateCell(Cell cell) {
        long currentOnset = cell.getOnset();

        cell.setOnset(this.oldOnset);
        this.oldOnset = currentOnset;
    }

    @Override
    protected void selectField(SpreadsheetCell sCell) {
        sCell.selectOnset();
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
        ChangeOnsetCellEdit t = (ChangeOnsetCellEdit) obj; 

        return (this.oldOnset == t.oldOnset && this.newOnset == t.newOnset);
    }

    @Override
    public int hashCode() {
        double hash = super.hashCode();
        hash += this.oldOnset * Constants.SEED1;
        hash += this.newOnset * Constants.SEED2;
        long val = Double.doubleToLongBits(hash);

        return (int) (val ^ (val >>> 32));
    }
}
