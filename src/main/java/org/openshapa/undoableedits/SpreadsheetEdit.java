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
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openshapa.OpenSHAPA;
import org.openshapa.controllers.project.ProjectController;
import org.openshapa.models.db.Datastore;
import database.DataCell;
import database.DataColumn;
import database.Database;
import database.SystemErrorException;
import org.openshapa.views.OpenSHAPAView;
import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.SpreadsheetPanel;

/**
 *
 */
  abstract class SpreadsheetEdit extends AbstractUndoableEdit {
    private static final Logger LOGGER = UserMetrix.getLogger(SpreadsheetEdit.class);
    protected ProjectController controller;
    protected Datastore model;
    protected Database db;  
    protected OpenSHAPAView view;
    
    public SpreadsheetEdit() {
        super();
        controller = OpenSHAPA.getProjectController();
        model = controller.getDB();
        db = controller.getLegacyDB().getDatabase();
        view = OpenSHAPA.getView();
    }

    @Override
    public String getPresentationName() {
        return super.getPresentationName();
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
    }
 
    protected DataCell getDataCell(CellPos cellPos) {
        try {        
            DataColumn col = (DataColumn) db.getColumn(cellPos.varName);
            long ColID = col.getID();    
            DataCell cell = (DataCell)db.getCell(ColID, cellPos.ord);
            return cell;
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to getDataCell", e);
            return null;
        }
    } 
    
    protected CellPos getCellPos(DataCell cell) {
        try {
            long colID = cell.getItsColID();
            DataColumn col = (DataColumn)db.getColumn(colID);
            String varName = col.getName();
            int ord = cell.getOrd(); 
            CellPos cellPos = new CellPos(varName, ord);
            return cellPos;
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to getCellPos", e);
            return null;
        }
    }
    
    protected SpreadsheetCell getSpreadsheetCell(DataCell cell) {
        for (SpreadsheetColumn sCol : getSpreadsheet().getColumns()) {
            for (SpreadsheetCell sCell : sCol.getCells()) {
                if (sCell.getCellID() == cell.getID()) {   
                    return sCell;
                }
            }
        }  
        return null;
    }
    
    protected SpreadsheetColumn getSpreadsheetColumn(String columnName) {
        for (SpreadsheetColumn sCol : getSpreadsheet().getColumns()) {
            if (sCol.getColumnName().equals(columnName)) {   
                    return sCol;
            }
        }  
        return null;
    }    
    
    protected void unselectAll() {
        view.getSpreadsheetPanel().clearColumnSelection();
        view.getSpreadsheetPanel().clearCellSelection();        
    }
    
    protected class CellPos {
        public String varName; // Column
        public int ord;      // Row
        public CellPos(String varName, int index) {
            this.varName = varName;
            this.ord = index;
        }
        
    }
    
    protected SpreadsheetPanel getSpreadsheet() {
        return view.getSpreadsheetPanel();
    }
}
