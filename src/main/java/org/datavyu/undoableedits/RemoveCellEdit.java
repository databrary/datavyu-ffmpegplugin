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
import org.datavyu.controllers.DeleteCellC;
import org.datavyu.models.db.Cell;
import org.datavyu.models.db.Variable;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.ArrayList;
import java.util.List;

/**
 * Undoable edit for removing cells.
 */
public class RemoveCellEdit extends SpreadsheetEdit {
    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LogManager.getLogger(RemoveCellEdit.class);

    private List<CellTO> cellTOV;

    /**
     * Constructor.
     *
     * @param cells The cells that are being removed.
     */
    public RemoveCellEdit(List<Cell> cells) {
        super();
        cellTOV = new ArrayList<CellTO>();
        for (Cell cell : cells) {
            cellTOV.add(new CellTO(cell, model.getVariable(cell)));
        }
    }

    @Override
    public String getPresentationName() {
        String msg = "nothing";

        if ((cellTOV != null) && (cellTOV.size() > 1)) {
            msg = "Delete " + cellTOV.size() + " Cells";
        } else {
            msg = "Delete Cell (" + cellTOV.get(0).getValue() + ")";
        }

        return msg;
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        LOGGER.info("Redoing remove cells");

        List<Cell> cellsToDelete = new ArrayList<Cell>();

        for (CellTO cellTO : cellTOV) {
            Variable var = model.getVariable(cellTO.getParentVariableName());

            for (Cell cell : var.getCells()) {
                if (cell.getOnset() == cellTO.getOnset() &&
                        cell.getOffset() == cellTO.getOffset() &&
                        cell.getValueAsString().equals(cellTO.getValue())) {
                    cellsToDelete.add(cell);
                    break;
                }
            }
        }
        new DeleteCellC(cellsToDelete);
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        LOGGER.info("Undoing remove cells");
        for (CellTO cellTO : cellTOV) {
            Variable var = model.getVariable(cellTO.getParentVariableName());
            var.addCell(cellTO.getCell());
        }
    }
}
