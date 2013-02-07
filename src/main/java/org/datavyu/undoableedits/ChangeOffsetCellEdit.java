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
import org.datavyu.util.Constants;
import org.datavyu.views.discrete.SpreadsheetCell;

/**
 * An undoable edit for changing the offset of a cell.
 */
public class ChangeOffsetCellEdit extends ChangeCellEdit {
    /** offset of cell */
    private long oldOffset = -1;
    private long newOffset = -1;


    public ChangeOffsetCellEdit(Cell c, long oldOffset, long newOffset, Granularity granularity) {
        super(c, granularity);
        this.oldOffset = oldOffset;
        this.newOffset = newOffset;
    }

    @Override
    public String getPresentationName() {
        return "Changed the offset of a cell within '" + columnName + "' to " + this.newOffset;
    }

    @Override
    protected void updateCell(Cell cell) {
        long currentOffset = cell.getOffset();

        cell.setOffset(this.oldOffset);
        this.oldOffset = currentOffset;
    }

    @Override
    protected void selectField(SpreadsheetCell sCell) {
        sCell.selectOffset();
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
        ChangeOffsetCellEdit t = (ChangeOffsetCellEdit) obj;

        return ((this.newOffset == t.newOffset)
                && (this.oldOffset == t.oldOffset));
    }

    @Override
    public int hashCode() {
        double hash = super.hashCode();
        hash += newOffset * Constants.SEED1;
        hash += oldOffset * Constants.SEED2;
        long val = Double.doubleToLongBits(hash);

        return (int) (val ^ (val >>> 32));
    }
}
