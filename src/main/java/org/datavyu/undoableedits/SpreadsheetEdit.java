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


import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.datavyu.Datavyu;
import org.datavyu.controllers.project.ProjectController;
import org.datavyu.models.db.Datastore;
import org.datavyu.models.db.Cell;
import org.datavyu.views.DatavyuView;
import org.datavyu.views.discrete.SpreadsheetCell;
import org.datavyu.views.discrete.SpreadsheetColumn;
import org.datavyu.views.discrete.SpreadsheetPanel;

/**
 * An undoable edit for altering the contents of a spreadsheet.
 */
public abstract class SpreadsheetEdit extends AbstractUndoableEdit {    
    private Date timestamp; // when the action was done
    
    protected ProjectController controller;
    protected Datastore model;    
    protected DatavyuView view;

    public SpreadsheetEdit() {
        super();
        timestamp = new Date();
        controller = Datavyu.getProjectController();
        model = controller.getDB();        
        view = Datavyu.getView();
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

    protected SpreadsheetCell getSpreadsheetCell(Cell cell) {
        for (SpreadsheetColumn sCol : getSpreadsheet().getColumns()) {
            for (SpreadsheetCell sCell : sCol.getCells()) {
                if (sCell.getCell().equals(cell)) {
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
    
    protected String getStyle() {
        String style;
        if (this.canUndo()) {
            style = "color='#000000'";
        } else {
            style = "color='#C0C0C0'";            
        }
        return style;
    }
    
    @Override
    public String toString() {        
        String msg;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");    
        if (this instanceof AddCellEdit) {
            msg = ((AddCellEdit)this).getPresentationName();                   
        }else if (this instanceof AddVariableEdit) {
            msg = ((AddVariableEdit)this).getPresentationName();                   
        }else if (this instanceof ChangeNameVariableEdit) {
            msg = ((ChangeNameVariableEdit)this).getPresentationName();                   
        }else if (this instanceof ChangeOffsetCellEdit) {
            msg = ((ChangeOffsetCellEdit)this).getPresentationName();                   
        }else if (this instanceof ChangeOnsetCellEdit) {
            msg = ((ChangeOnsetCellEdit)this).getPresentationName();                   
        }else if (this instanceof ChangeValCellEdit) {
            msg = ((ChangeValCellEdit)this).getPresentationName();                   
        }else if (this instanceof RemoveCellEdit) {
            msg = ((RemoveCellEdit)this).getPresentationName();                   
        }else if (this instanceof RemoveVariableEdit) {
            msg = ((RemoveVariableEdit)this).getPresentationName();                   
        }else if (this instanceof RunScriptEdit) {
            msg = ((RunScriptEdit)this).getPresentationName();                   
        }else if (this instanceof VocabEditorEdit) {
            msg = ((VocabEditorEdit)this).getPresentationName();        
        }else {
            msg = "";   
        }
        return "<html><font " + getStyle() + ">" + formatter.format(this.timestamp) 
                + "\t" + msg + "</font></html>";
    }
 
}
