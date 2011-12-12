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
import java.util.logging.Level;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openshapa.controllers.DeleteColumnC;
import org.openshapa.models.db.UserWarningException;
import org.openshapa.models.db.Variable;
import database.DataColumnTO;
import java.util.ArrayList;
import org.openshapa.models.db.Cell;


/**
 *
 */
public class RemoveVariableEdit extends SpreadsheetEdit {    
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(RemoveVariableEdit.class);
    //private      
    private List<Variable> varsToDelete;       // Variables to delete.
    private List<String>   varNames;           // Spreadsheet variable names.

    
    public RemoveVariableEdit(List<Variable> varsToDelete) {
        this.varsToDelete = varsToDelete;
        varNames = new ArrayList<String>();
        for (Variable var : model.getAllVariables()) {
            varNames.add(var.getName());
        }
    }

    @Override
    public String getPresentationName() {
        String msg;
        if (varsToDelete.size() == 1) {
            msg = "Delete Variable \"" + varsToDelete.get(0).getName() + "\""; 
        }
        else { // > 1
            msg = "Delete " + varsToDelete.size() + " Variables";
        }
        return msg;
    }

    @Override
    public void undo() throws CannotRedoException {
        super.undo();
        for (Variable var : varsToDelete) {
            try {
                
                Variable newVar = model.createVariable(var.getName(), var.getVariableType());
                //unimplemented 
                // how to set the variable's position back
                for (Cell cell : var.getCells()) {
                    Cell newCell = newVar.createCell();
                    newCell.setOnset(cell.getOnset());
                    newCell.setOffset(cell.getOffset());
                    //unimplemented
                    //newCell.setValue(cell.getValue());
                }
                
            } catch (UserWarningException e) {
                LOGGER.error("Unable to undo.", e);
            }
        }
        
        /*
        try {
            int j = 0;
            for (DataColumnTO colTO : colsTO) {    
                DataColumn dc = new DataColumn(db, colTO.name, colTO.itsMveType);
                VariableType.type newType;
                if (colTO.itsMveType == MatrixVocabElement.MatrixType.MATRIX) {
                    newType = VariableType.type.MATRIX;
                } else if (colTO.itsMveType == MatrixVocabElement.MatrixType.NOMINAL) {
                    newType = VariableType.type.NOMINAL;
                } else {
                    newType = VariableType.type.TEXT;
                }

                DeprecatedVariable var = new DeprecatedVariable(dc, newType);
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
         */
    }

    @Override 
    public void redo() throws CannotUndoException {        
        super.redo();
        new DeleteColumnC(varsToDelete);
        unselectAll();
    }   
}
