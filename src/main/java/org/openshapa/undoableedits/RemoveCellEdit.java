/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.undoableedits;


import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.List;
import java.util.Vector;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openshapa.controllers.CreateNewCellC;
import org.openshapa.controllers.DeleteCellC;
import org.openshapa.models.db.DeprecatedDatabase;
import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.DataCellTO;
import org.openshapa.models.db.legacy.DataColumn;
import org.openshapa.models.db.legacy.MatrixVocabElement;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetCell;


/**
 *
 * @author harold
 */
public class RemoveCellEdit extends SpreadsheetEdit {
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(RemoveCellEdit.class);
    
    private Vector<CellPos> cellPosV;
    private Vector<DataCellTO> cellTOV;
    
    public RemoveCellEdit(Vector<DataCell> cells) {
      super();
      cellPosV = new Vector<CellPos>();
      cellTOV = new Vector<DataCellTO>();
      for (DataCell cell : cells) {
          cellPosV.add(getCellPos(cell)); 
          cellTOV.add(cell.getDataCellData());
      }

    }

    @Override
    public String getPresentationName() {
        String msg;
        if ((cellPosV != null) && (cellPosV.size() > 1)) {
            msg = "Delete " + cellPosV.size() + " Cells";
        }
        else { // one cell
            msg = "Delete Cell (" + cellPosV.get(0).varName + "," + cellPosV.get(0).ord + ")";
        }
        return msg;
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        List<DataCell> cellsToDelete = new Vector<DataCell>();
        for (CellPos cellPos : cellPosV) {
            cellsToDelete.add(getDataCell(cellPos));
        }
        new DeleteCellC(cellsToDelete);
    }          

    @Override
    public void undo() throws CannotUndoException {  
        super.undo();
        unselectAll();
        for (int i = 0; i < cellPosV.size(); i++) {
            try {
                CellPos cellPos = cellPosV.get(i);
                DataColumn col = db.getDataColumn(cellPos.varName);
                long colID = col.getID();
                long mveID = col.getItsMveID();
                DataCell newCell = new DataCell(db, colID, mveID);
                long cellID = db.insertdCell(newCell, cellPos.ord);
                newCell = (DataCell)db.getCell(cellID);                
                DataCellTO cellTO = cellTOV.get(i);
                newCell.setDataCellData(cellTO);
                db.replaceCell(newCell);               
                SpreadsheetCell sCell = getSpreadsheetCell(newCell);
                sCell.requestFocus();
                sCell.setSelected(true);        
            } catch (SystemErrorException e) {
                LOGGER.error("Unable to undo Remove Cell.", e);
            }
        }          

    }
    
}
