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
import database.DataCellTO;
import database.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetCell;

/**
 *
 */
public class ChangeValCellEdit extends ChangeCellEdit {
    private static final Logger LOGGER = UserMetrix.getLogger(ChangeValCellEdit.class);
    /** offset of cell */
    private DataCellTO dcTO= null;
  
    public ChangeValCellEdit(DataCell c, Granularity granularity) {
        super(c, granularity);
        dcTO = c.getDataCellData();
    }

    public ChangeValCellEdit(DataCell c) {
        this(c, Granularity.COARSEGRAINED);
    }
    
    @Override
    public String getPresentationName() {
        return super.getPresentationName() + "Val Cell (" + columnName + "," + rowIndex + ") to " + dcTO.argListToString();
    }
   
    @Override
    protected void updateCell(DataCell cell) {
        try {
            DataCellTO currentTO = cell.getDataCellData();
            cell.setDataCellData(dcTO);
            this.dcTO= currentTO;
            db.replaceCell(cell); 
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to get", e);
        }      
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
        return super.equals(obj) && this.dcTO.equals(((ChangeValCellEdit)t).dcTO);
    }
    
}
