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

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import database.DataCell;
import database.SystemErrorException;
import database.TimeStamp;
import org.openshapa.views.discrete.SpreadsheetCell;

/**
 *
 */
public class ChangeOffsetCellEdit extends ChangeCellEdit {
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(ChangeOffsetCellEdit.class);
    
    /** offset of cell */
    private TimeStamp offset = null;
  
    public ChangeOffsetCellEdit(DataCell c, Granularity granularity) {
        super(c, granularity);
        try {
            this.offset = c.getOffset();
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to getOnset", e);
        } finally {

        }
    }

    public ChangeOffsetCellEdit(DataCell c) {
        this(c,Granularity.COARSEGRAINED);
    }
    
    @Override
    public String getPresentationName() {
        return super.getPresentationName() + "Offset Cell (" + columnName + "," + rowIndex + ") to " + offset.toHMSFString();
    }
    
    @Override
    protected void updateCell(DataCell cell) {
        try {
            TimeStamp currentOffset = cell.getOffset();
            cell.setOffset(this.offset);
            this.offset = currentOffset;
            db.replaceCell(cell); 
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to getOnset", e);
        }      
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
        return this.offset.equals(((ChangeOffsetCellEdit)t).offset);
    }
}
