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

import database.DataCell;
import org.openshapa.models.db.Cell;
import org.openshapa.util.Constants;
import org.openshapa.views.discrete.SpreadsheetCell;

/**
 *
 */
public class ChangeOffsetCellEdit extends ChangeCellEdit {
    /** offset of cell */
    private long offset = -1;
    private String offsetString = null;

    public ChangeOffsetCellEdit(Cell c, Granularity granularity) {
        super(c, granularity);
        this.offset = c.getOffset();
        this.offsetString = c.getOffsetString();
    }

    public ChangeOffsetCellEdit(Cell c) {
        this(c,Granularity.COARSEGRAINED);
    }

    @Override
    public String getPresentationName() {
        return super.getPresentationName() + "Offset Cell (" + columnName + "," + rowIndex + ") to " + offsetString;
    }

    @Override
    protected void updateCell(DataCell cell) {        
        /*
         * TODO: Port method declaration to new API.
        long currentOffset = cell.getOffset();
        String currentOffsetString = cell.getOffsetString();

        cell.setOffset(this.offset);
        this.offset = currentOffset;
         */
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

        return ((this.offset == t.offset)
                && (this.offsetString.equals(t.offsetString)));
    }

    @Override
    public int hashCode() {
        double hash = super.hashCode();
        hash += offset * Constants.SEED1;
        hash += offsetString.hashCode() * Constants.SEED2;
        long val = Double.doubleToLongBits(hash);

        return (int) (val ^ (val >>> 32));
    }
}
