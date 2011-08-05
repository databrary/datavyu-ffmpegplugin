/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.undoableedits;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.models.db.legacy.TimeStamp;
import org.openshapa.views.discrete.SpreadsheetCell;

/**
 *
 * @author harold
 */
public class ChangeOnsetCellEdit extends ChangeCellEdit { 
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(ChangeOnsetCellEdit.class);
    
    /** onset of cell */
    private TimeStamp onset = null;
  
    public ChangeOnsetCellEdit(DataCell c) {
        super(c);

        try {
            this.onset = c.getOnset();
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to getOnset", e);
        } finally {

        }
    }

    @Override
    public String getPresentationName() {
        return super.getPresentationName() + "Onset Cell (" + columnName + "," + rowIndex + ")";
    }
   
    @Override
    protected void updateCell(DataCell cell) {
        try {
            TimeStamp currentOnset = cell.getOnset();
            cell.setOnset(this.onset);
            this.onset = currentOnset;
            db.replaceCell(cell); 
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to getOnset", e);
        }      
    }
    
    @Override
    protected void selectField(SpreadsheetCell sCell) {
        sCell.selectOnset();
    }
    
}
