/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.undoableedits;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.List;
import java.util.logging.Level;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openshapa.OpenSHAPA;
import org.openshapa.controllers.CreateNewCellC;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.DeprecatedCell;
import org.openshapa.models.db.Variable;
import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetCell;


/**
 *
 * @author harold
 */
public class AddCellEdit extends SpreadsheetEdit {
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(AddCellEdit.class);
    /** Variable name **/
    private String varName;
    
    public AddCellEdit(String varName) {
        super();
        this.varName = varName;
    }

    @Override
    public String getPresentationName() {
        return "New Cell to " + varName;
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        CreateNewCellC newCellController = new CreateNewCellC();
        Variable var = getVariable(varName);
        newCellController.createCell(var);
        unselectAll();
        DataCell cell = (DataCell) var.getCells().get(var.getCells().size()-1);
        SpreadsheetCell sCell = getSpreadsheetCell(cell);
        sCell.requestFocusInWindow();
        sCell.setSelected(true);
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        try {
            db.removeCell(getLastCellID());
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to removeCell", e);
        }
    }    
    
    private long getLastCellID() {
        Variable v = getVariable(varName);
        List<Cell> cells = v.getCells();
        int numCells = v.getCells().size();
        DeprecatedCell cell = (DeprecatedCell)cells.get(numCells - 1);
        long cellID = cell.getLegacyCell().getID();
        return cellID;
    }
}
