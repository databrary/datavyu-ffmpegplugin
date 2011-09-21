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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openshapa.controllers.DeleteColumnC;
import org.openshapa.models.db.DeprecatedDatabase;
import org.openshapa.models.db.DeprecatedVariable;
import org.openshapa.models.db.Variable;
import database.Cell;
import database.DataCell;
import database.DataCellTO;
import database.DataColumn;
import database.DataColumnTO;
import database.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetColumn;


/**
 *
 */
public class RemoveVariableEdit extends SpreadsheetEdit {    
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(RemoveVariableEdit.class);
    //private      
    private Vector<DataColumnTO> colsTO; // DataColumn relevant values
    private Vector<Long> colOrderVec;    // Order Vector before deleting the variables
    private List<Variable> vars;         // Vars vector   
    private List<Variable> varsTo;       // Variables to delete.
    private Vector<Integer> indexV;      // indexes of the columns to delete on the spreadsheet 
    
    public RemoveVariableEdit(List<Variable> varsToDelete) {
        super();
        try {
            varsTo = varsToDelete;
            colsTO = new Vector<DataColumnTO>();
            indexV = new Vector<Integer>(); 
            colOrderVec = db.getColOrderVector();            
            vars = new ArrayList<Variable>(model.getAllVariables());

            Vector<DataColumn> colsToDelete = new Vector<DataColumn>();
            for (Variable var : varsToDelete) {
                colsToDelete.add(((DeprecatedVariable) var).getLegacyVariable());
            }

            // Add the cells to each Column
            for (DataColumn col : colsToDelete) {
                DataColumnTO colTO = new DataColumnTO(col);  
                List<SpreadsheetColumn> columns = getSpreadsheet().getColumns();
                // What index does the given column sit at
                int columnIndex = -1;
                for (int i = 0; i < columns.size(); i++) {
                    if (columns.get(i).getColID() == col.getID()) {
                        columnIndex = i;
                        indexV.add(columnIndex);
                        break;
                    }
                }                
                int numCells = col.getNumCells(); 
                colTO.dataCellsTO.clear();
                for (int i = 0; i < numCells; i++) {
                        Cell c = db.getCell(col.getID(), i+1);
                        DataCellTO cTO = new DataCellTO((DataCell)c);
                        colTO.dataCellsTO.add(cTO);
                }
                colsTO.add(colTO);                    
            }
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to construct RemoveVariableEdit.", e);
        } finally {

        }
    }

    @Override
    public String getPresentationName() {
        String msg;
        if (colsTO.size() == 1) {
            msg = "Delete Variable \"" + colsTO.elementAt(0).name + "\""; 
        }
        else { // > 1
            msg = "Delete " + colsTO.size() + " Variables";
        }
        return msg;
    }

    @Override
    public void undo() throws CannotRedoException {
        super.undo();
        try {
            int j = 0;
            for (DataColumnTO colTO : colsTO) {    
                DataColumn dc = new DataColumn(db, colTO.name, colTO.itsMveType);                             
                DeprecatedVariable var = new DeprecatedVariable(dc); 
                model.addVariable(var);
                vars.set(indexV.get(j), var);
                dc = db.getDataColumn(dc.getName()); 
                for (DataCellTO cellTO : colTO.dataCellsTO) {                 
                    DataCell newCell = new DataCell(db,dc.getID(), dc.getItsMveID());
                    long cellID = ((DeprecatedDatabase) model).getDatabase().appendCell(newCell);
                    newCell = (DataCell)((DeprecatedDatabase) model).getDatabase().getCell(cellID); 
                    newCell.setDataCellData(cellTO);                   
                    db.replaceCell(newCell);  
                }                   
                colOrderVec.setElementAt(dc.getID(), indexV.get(j++));
            }
            getSpreadsheet().reorderColumns(colOrderVec); 
            unselectAll();
            for (DataColumnTO colTO : colsTO) {
                SpreadsheetColumn sCol = this.getSpreadsheetColumn(colTO.name);
                sCol.setSelected(true);
            }
            ((DeprecatedDatabase)model).setVariables(vars);
            
        } catch (SystemErrorException e) {
                LOGGER.error("Unable to undo.", e);
        }
    }

    @Override 
    public void redo() throws CannotUndoException {        
        super.redo();

        new DeleteColumnC(varsTo);
        unselectAll();
    }   
}
