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
import java.util.List;
import java.util.Vector;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import database.DataCell;
import database.DataCellTO;
import database.DataColumn;
import database.SystemErrorException;
import java.util.ArrayList;
import org.openshapa.models.db.Cell;
import org.openshapa.views.discrete.SpreadsheetCell;


/**
 *
 */
public class RemoveCellEdit extends SpreadsheetEdit {
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(RemoveCellEdit.class);
    
    //private List<CellPos> cellPosV;
    private List<DataCellTO> cellTOV;
    
    public RemoveCellEdit(List<Cell> cells) {
      super();
      //cellPosV = new Vector<CellPos>();
      cellTOV = new ArrayList<DataCellTO>();
      /*
      for (DataCell cell : cells) {
          cellTOV.add(cell.getDataCellData());
      }*/

    }

    @Override
    public String getPresentationName() {
        String msg = "nothing";
        /*
        if ((cellPosV != null) && (cellPosV.size() > 1)) {
            msg = "Delete " + cellPosV.size() + " Cells";
        }
        else { // one cell
            msg = "Delete Cell (" + cellPosV.get(0).varName + "," + cellPosV.get(0).ord + ")";
        }*/
        return msg;
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        /*
        List<DataCell> cellsToDelete = new Vector<DataCell>();
        for (CellPos cellPos : cellPosV) {
            cellsToDelete.add(getDataCell(cellPos));
        }*/
        
        //
        // TODO.
        //new DeleteCellC(cellsToDelete);
    }          

    @Override
    public void undo() throws CannotUndoException {  
        super.undo();
        /*
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
        }*/          
    }
    
}
