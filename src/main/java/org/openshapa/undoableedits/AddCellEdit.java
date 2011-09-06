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

import com.usermetrix.jclient.Logger;//
import com.usermetrix.jclient.UserMetrix;
import java.util.List;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openshapa.controllers.CreateNewCellC;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.DeprecatedCell;
import org.openshapa.models.db.Variable;
import database.DataCell;
import database.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetCell;


/**
 *
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
        if ((var.getCells() != null) && (var.getCells().size() > 0)) {
            DataCell cell = (DataCell) var.getCells().get(var.getCells().size()-1);
            SpreadsheetCell sCell = getSpreadsheetCell(cell);
            sCell.requestFocusInWindow();
            sCell.setSelected(true);
        }
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
