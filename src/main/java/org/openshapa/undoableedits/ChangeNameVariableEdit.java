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
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import database.DataColumn;
import database.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetColumn;

/**
 *
 */
public class ChangeNameVariableEdit extends SpreadsheetEdit {
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(ChangeNameVariableEdit.class);  
    private String oldVarName;
    private String newVarName;
    
    public ChangeNameVariableEdit(String oldVarName, String newVarName) {
        super();
        this.oldVarName = oldVarName;
        this.newVarName = newVarName;
    }

    @Override
    public String getPresentationName() {
        String msg = "";

        if (canUndo()) {
            msg = "Change Variable Name from \"" + newVarName +  "\"" + " to " 
                                  + "\"" + oldVarName +  "\"";
        } else {
            msg = "Change Variable Name from \"" + oldVarName +  "\"" + " to " 
                                  + "\"" + newVarName +  "\"";            
        }
        return msg;
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        try {
            DataColumn dc = db.getDataColumn(oldVarName);
            dc.setName(newVarName);
            db.replaceColumn(dc);            
            unselectAll();
            selectVarName(newVarName);
        } catch (SystemErrorException e) {
             LOGGER.error("Unable to redo Change Variable Name.", e);
        }

    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        try {
            DataColumn dc = db.getDataColumn(newVarName);
            dc.setName(oldVarName);
            db.replaceColumn(dc);            
            unselectAll();
            selectVarName(oldVarName);
        } catch (SystemErrorException e) {
             LOGGER.error("Unable to undo Change Variable Name.", e);
        }
    }
    
    private void selectVarName(String varName) {
        SpreadsheetColumn sCol = this.getSpreadsheetColumn(varName);
        if (sCol != null) sCol.setSelected(true);
    } 
}
