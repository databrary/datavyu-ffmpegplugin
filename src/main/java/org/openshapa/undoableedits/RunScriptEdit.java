/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.undoableedits;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openshapa.controllers.DeleteColumnC;
import org.openshapa.controllers.RunScriptC;
import org.openshapa.models.db.DeprecatedDatabase;
import org.openshapa.models.db.DeprecatedVariable;
import org.openshapa.models.db.Variable;
import org.openshapa.models.db.legacy.Cell;
import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.DataCellTO;
import org.openshapa.models.db.legacy.DataColumn;
import org.openshapa.models.db.legacy.DataColumnTO;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetColumn;


/**
 *
 * @author harold
 */
public class RunScriptEdit extends SpreadsheetEdit {    
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(RunScriptEdit.class);
    //private      
    private String scriptPath;
    private Vector<DataColumnTO> colsTO; // DataColumn relevant values 
            
    public RunScriptEdit(String scriptPath) { 
        super();
        this.scriptPath = scriptPath;            
        colsTO = getSpreadsheetState();

    }

    @Override
    public String getPresentationName() {
        /*
        File file = new File(scriptPath);
        String fileName = file.getName();        
        return "Run Script \"" + fileName + "\"";
         * 
         */
        return "Changes due to \"" + this.scriptPath + "\"";
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
        Vector<DataColumnTO> tempColsTO = this.getSpreadsheetState();
        setSpreadsheetState(colsTO);
        colsTO = tempColsTO;       
    }
    
    private Vector<DataColumnTO> getSpreadsheetState() {
         Vector<DataColumnTO> colsTO = new Vector<DataColumnTO>();
         try {            
            List<Variable> vars = model.getAllVariables();
            Vector<DataColumn> colsToDelete = new Vector<DataColumn>();
            for (Variable var : vars) {
                String name = var.getName();
                for (DataColumn dc : db.getDataColumns()) {
                    if (name.equals(dc.getName())) {
                        colsToDelete.add(dc);    
                    }
                }
            }
     
            // Add the cells to each Column
            for (DataColumn col : colsToDelete) {
                DataColumnTO colTO = new DataColumnTO(col);               
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
            LOGGER.error("Unable to getSpreadsheetState.", e);
        } finally {
            return colsTO;
        }       
    }

    private void setSpreadsheetState(Vector<DataColumnTO> colsTO) {
        try {           
            Vector<DataColumn> colsToDelete;
            colsToDelete = db.getDataColumns();  
            new DeleteColumnC(colsToDelete);
            for (DataColumnTO colTO : colsTO) {    
                DataColumn dc = new DataColumn(db, colTO.name, colTO.itsMveType);                             
                DeprecatedVariable var = new DeprecatedVariable(dc); 
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
        } catch (SystemErrorException e) {
                LOGGER.error("Unable to setSpreadsheetState.", e);
        }        
    }
    
}
