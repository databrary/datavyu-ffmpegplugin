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
import database.Cell;
import java.util.List;
import java.util.logging.Level;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openshapa.models.db.UserWarningException;
import org.openshapa.models.db.Variable;
import database.DataCell;
import database.DataCellTO;
import database.DataColumn;
import database.DataColumnTO;
import database.Database;
import database.MatrixVocabElement;
import database.SystemErrorException;
import java.util.ArrayList;
import org.openshapa.controllers.DeleteColumnC;
import org.openshapa.models.db.DeprecatedDatabase;
import org.openshapa.models.db.DeprecatedVariable;

/**
 *
 */
public class VocabEditorEdit extends SpreadsheetEdit {    
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(VocabEditorEdit.class);
    //private      
    private Database db;
    private List<Variable> vars;
    
    public VocabEditorEdit() { 
        super();         
        vars = getSpreadsheetState();
    }

    @Override
    public String getPresentationName() {
        return "Vocab Editor actions";
    }

    @Override
    public void undo() throws CannotRedoException {
        super.undo();
        toogleSpreadsheetState();
    }

    @Override 
    public void redo() throws CannotUndoException {        
        super.redo();
        toogleSpreadsheetState();
    } 
    
    private void toogleSpreadsheetState() {
        List<Variable> tempVars = this.getSpreadsheetState();
        setSpreadsheetState(vars);
        vars = tempVars;       
    }
    
    private List<Variable> getSpreadsheetState() {           
            return new ArrayList(model.getAllVariables());     
    }

/*    
    private void setSpreadsheetState(ArrayList<DataColumnTO> colsTO) {          
            new DeleteColumnC(new ArrayList<Variable>(model.getAllVariables()));
            
            for (DataColumnTO colTO : colsTO) {                
            try {
                Variable.type newType;
                if (colTO.itsMveType == MatrixVocabElement.MatrixType.MATRIX) {
                    newType = Variable.type.MATRIX;
                } else if (colTO.itsMveType == MatrixVocabElement.MatrixType.NOMINAL) {
                    newType = Variable.type.NOMINAL;
                } else {
                    newType = Variable.type.TEXT;
                }
                Variable var = model.createVariable(colTO.name, newType);
           
                for (DataCellTO cellTO : colTO.dataCellsTO) {                 
                    Cell c = var.createCell();
                    c.setValue();
                    
                    //DataCell newCell = new DataCell(db,dc.getID(), dc.getItsMveID());
                    //long cellID = ((DeprecatedDatabase) model).getDatabase().appendCell(newCell);
                    //newCell = (DataCell)((DeprecatedDatabase) model).getDatabase().getCell(cellID); 
                    //newCell.setDataCellData(cellTO);                   
                    //db.replaceCell(newCell);  
                }
            } catch (UserWarningException ex) {
                LOGGER.error("Unable to setSpreadsheetState.", e);
            }
            }
            
/*
            for (DataColumnTO colTO : colsTO) {
                
                
                DataColumn dc = new DataColumn(db, colTO.name, colTO.itsMveType);

                Variable.type newType;
                if (colTO.itsMveType == MatrixVocabElement.MatrixType.MATRIX) {
                    newType = Variable.type.MATRIX;
                } else if (colTO.itsMveType == MatrixVocabElement.MatrixType.NOMINAL) {
                    newType = Variable.type.NOMINAL;
                } else {
                    newType = Variable.type.TEXT;
                }

                DeprecatedVariable var = new DeprecatedVariable(dc, newType);
                model.addVariable(var);
                dc = db.getDataColumn(dc.getName());             
                for (DataCellTO cellTO : colTO.dataCellsTO) {                 
                    DataCell newCell = new DataCell(db,dc.getID(), dc.getItsMveID());
                    long cellID = ((DeprecatedDatabase) model).getDatabase().appendCell(newCell);
                    newCell = (DataCell)((DeprecatedDatabase) model).getDatabase().getCell(cellID); 
                    newCell.setDataCellData(cellTO);                   
                    db.replaceCell(newCell);  
                }                   
            }

            unselectAll();              
    }
*/ 
    
    private void setSpreadsheetState(List<Variable> vars) {        
            new DeleteColumnC(new ArrayList(model.getAllVariables()));
            for (Variable variable : vars) {    
            try {
                //model.addVariable(variable);
               
                
              Variable newVar = model.createVariable(variable.getName(), variable.getVariableType());
/*                                 
               for (org.openshapa.models.db.Cell cell : variable.getCells()) {
                   org.openshapa.models.db.Cell newCell = newVar.createCell();
                   newCell.setOnset(cell.getOnset());
                   newCell.setOffset(cell.getOffset());
               }
 */
 
            } catch (UserWarningException e) {
                LOGGER.error("Unable to setSpreadsheetState.", e);
            }
            } 
            unselectAll();                   
    }
    
}

