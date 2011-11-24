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
    private long onset = -1;

    /** string representation of onset. */
    private String onsetString = null;

    public ChangeOnsetCellEdit(Cell c, Granularity granularity) {
        super(c, granularity);
        this.onset = c.getOnset();
        this.onsetString = c.getOnsetString();
    }

    public ChangeOnsetCellEdit(Cell c) {
        this(c, Granularity.COARSEGRAINED);
    }

    @Override
    public String getPresentationName() {
        return super.getPresentationName() + "Onset Cell (" + columnName + ") to " + onsetString;
    }

    @Override
    protected void updateCell(Cell cell) {
        long currentOnset = cell.getOnset();
        String currentOnsetString = cell.getOnsetString();
        
        cell.setOnset(this.onset);
        this.onset = currentOnset;
        this.onsetString = currentOnsetString;
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

        return ((this.onset == t.onset)
                && (this.onsetString.equals(t.onsetString)));
    }

    @Override
    public int hashCode() {
        double hash = super.hashCode();
        hash += onset * Constants.SEED1;
        hash += onsetString.hashCode() * Constants.SEED2;
        long val = Double.doubleToLongBits(hash);

        return (int) (val ^ (val >>> 32));
    }
}
