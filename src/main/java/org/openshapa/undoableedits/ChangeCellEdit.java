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
import java.util.Vector;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import database.Column;
import database.DataCell;
import database.SystemErrorException;
import org.openshapa.views.discrete.SpreadsheetCell;

/**
 *
 * @author harold
 */
abstract public class ChangeCellEdit extends SpreadsheetEdit {
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(ChangeCellEdit.class);
    /** Column's index */
    protected int colIndex = -1;       
    /** Number of this cell in its host column. This number should be 1 + the
     *  index of the cell in the column's vector of cells. It is set to -1
     *  initially as it is an invalid value. */
    protected int ord = -1;
    /////
    
    protected String columnName;
    protected long rowIndex;
    
    protected Granularity granularity;

    public Granularity getGranularity() {
        return granularity;
    }
    
    public enum Granularity {
        FINEGRAINED,
        COARSEGRAINED
    }    
    
    public ChangeCellEdit(DataCell c, Granularity granularity) {
        super();
        this.granularity = granularity;
        try {
           Column col = c.getDB().getColumn(c.getItsColID());
           columnName = col.getName();
           rowIndex = col.getNumCells() - c.getOrd() + 1;           
           long colID = c.getItsColID();
           Vector<Long> orderVector = c.getDB().getColOrderVector();
           for (int i = 0; i < orderVector.size(); i++) {
               if (orderVector.get(i).longValue() == colID) {
                  this.colIndex = i;
                  break;
               }
           }
           this.ord = c.getOrd();                    
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to get DataCell", e);
        } 
    }
    
    public ChangeCellEdit(DataCell c) {
        this(c, Granularity.COARSEGRAINED);
    }

    @Override
    public String getPresentationName() {
        return "Change " /* + this.granularity */ + " ";
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        updateCell();
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        updateCell();
    }
   
    protected void updateCell() {
        try {
            long colID = db.getDataColumn(this.db.getColOrderVector().get(this.colIndex)).getID();
            // get a copy of the current cell
            DataCell cell = (DataCell) db.getCell(colID, ord);
            updateCell(cell);           
            if (this.granularity == Granularity.COARSEGRAINED) {
                updateSpreadsheetCell(cell);
            }
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to update Cell", e);
        }        
    }
    
    abstract protected void updateCell(DataCell cell); 
    
    protected void updateSpreadsheetCell(DataCell cell) {
        unselectAll();
        SpreadsheetCell sCell = getSpreadsheetCell(cell);
        sCell.setHighlighted(true);
        sCell.requestFocusInWindow();
        selectField(sCell);    
    }
    
    abstract protected void selectField(SpreadsheetCell sCell);

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ChangeCellEdit) && 
               ((ChangeCellEdit)obj).granularity == this.granularity;
    }
     
}
