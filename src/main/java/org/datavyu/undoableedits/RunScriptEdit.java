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
package org.datavyu.undoableedits;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.controllers.DeleteColumnC;
import org.datavyu.models.db.Cell;
import org.datavyu.models.db.UserWarningException;
import org.datavyu.models.db.Variable;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Undoable script edit.
 */
public class RunScriptEdit extends SpreadsheetEdit {
    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LogManager.getLogger(RunScriptEdit.class);

    private String scriptPath;
    private List<VariableTO> colsTO; // DataColumn relevant values

    public RunScriptEdit(String scriptPath) {
        super();
        this.scriptPath = scriptPath;
        colsTO = getSpreadsheetState();
    }

    @Override
    public String getPresentationName() {
        return "Run Script \"" + this.scriptPath + "\"";
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
        List<VariableTO> tempColsTO = this.getSpreadsheetState();
        setSpreadsheetState(colsTO);
        colsTO = tempColsTO;
    }

    private List<VariableTO> getSpreadsheetState() {
        List<VariableTO> varsTO = new ArrayList<VariableTO>();

        int pos = 0;
        for (Variable var : model.getAllVariables()) {
            varsTO.add(new VariableTO(var, pos));
            pos++;
        }

        return varsTO;
    }

    private void setSpreadsheetState(List<VariableTO> varsTO) {
        try {
            HashMap<String, Boolean> hiddenStates = new HashMap<String, Boolean>();
            for (Variable v : model.getAllVariables()) {
                hiddenStates.put(v.getName(), v.isHidden());
            }
            new DeleteColumnC(new ArrayList<Variable>(model.getAllVariables()));

            for (VariableTO varTO : varsTO) {
                Variable var = model.createVariable(varTO.getName(), varTO.getType().type);
                var.setRootNode(varTO.getType());

                for (CellTO cellTO : varTO.getTOCells()) {
                    Cell c = var.createCell();
                    c.setOnset(cellTO.getOnset());
                    c.setOffset(cellTO.getOffset());
                    c.getValue().set(cellTO.getValue());
                }
            }
            for (Variable v : model.getAllVariables()) {
                v.setHidden(hiddenStates.get(v.getName()));
            }
        } catch (UserWarningException uwe) {
            LOGGER.error("Unable to set spreadsheet state", uwe);
            uwe.printStackTrace();
        }

        unselectAll();
    }
}
