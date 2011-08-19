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
