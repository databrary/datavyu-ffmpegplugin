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
  
    public ChangeValCellEdit(DataCell c) {
        super(c);
        dcTO = c.getDataCellData();

    }

    @Override
    public String getPresentationName() {
        return super.getPresentationName() + "Val Cell (" + columnName + "," + rowIndex + ")";
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
    
}
