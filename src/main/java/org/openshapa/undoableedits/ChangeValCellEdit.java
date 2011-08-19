/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.undoableedits;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.DataCellTO;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetCell;

/**
 *
 * @author harold
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
