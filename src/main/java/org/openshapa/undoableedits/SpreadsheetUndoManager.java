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

import java.util.ArrayList;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import org.openshapa.undoableedits.ChangeCellEdit.Granularity;

/**
 *
 */
public class SpreadsheetUndoManager  extends UndoManager implements ListModel {

    List<ListDataListener> listeners;

    public SpreadsheetUndoManager() {
        listeners = new ArrayList<ListDataListener>();
    }

    @Override
    public synchronized void undo() throws CannotUndoException {
        super.undo();
    }

    @Override
    public synchronized void redo() throws CannotRedoException {
        super.redo();
    }

    @Override
    public synchronized boolean addEdit(UndoableEdit ue) {
        boolean addEdit = true;
        boolean result;
        boolean cond;
        if ((edits != null) && (edits.size() > 0)) {
            UndoableEdit prevEdit = edits.lastElement();
            // Do not insert duplicated edits
            if ((ue instanceof ChangeCellEdit)
                    && (ue.getClass() == prevEdit.getClass())) {
                ChangeCellEdit cce = (ChangeCellEdit) ue;
                // Remove previous finegrained ChangeCellEdit of the same type
                if (cce.getGranularity() == Granularity.COARSEGRAINED) {
                    // Take the last element
                    do {
                        Object o = edits.lastElement();
                        cond = (ue.getClass() == o.getClass())
                                && (((ChangeCellEdit) o).granularity == Granularity.FINEGRAINED);
                        if (cond) {
                            edits.removeElementAt(edits.size()-1);
                        }
                    } while (cond && edits.size() > 0);
                } else if (prevEdit.equals(cce)) { // FINEGRAINED
                    addEdit = false;
                }
            }
        }
        if (addEdit) {
            result = super.addEdit(ue);
        } else {
            result = false;
        }

        for (ListDataListener l : listeners) {
            l.contentsChanged(null);
        }
        return result;
    }

    // Return the complete list of edits in an array.
    public synchronized UndoableEdit[] getEdits() {
        UndoableEdit[] array = new UndoableEdit[edits.size()];
        edits.copyInto(array);
        return array;
    }

    public synchronized List<UndoableEdit> getUndoableEdits() {
        int size = edits.size();
        List<UndoableEdit> v = new ArrayList<UndoableEdit>(size);

        for (int i=size-1; i>=0; i--) {
            UndoableEdit u = (UndoableEdit)edits.elementAt(i);
            if (u.canUndo() && u.isSignificant()) {
                v.add(u);
            }
        }

        return v;
    }

    // Return all currently significant redoable edits. The first edit is the
    // next one to be redone.
    public synchronized List<UndoableEdit> getRedoableEdits() {
        int size = edits.size();
        List<UndoableEdit> v = new ArrayList<UndoableEdit>(size);
        for (int i=0; i<size; i++) {
            UndoableEdit u = (UndoableEdit)edits.elementAt(i);
            if (u.canRedo() && u.isSignificant()) {
                v.add(u);
            }
        }

        return v;
    }

    // ListModel Methods
    @Override
    public Object getElementAt(int index) {
        return edits.get(index);
    }

    @Override
    public int getSize() {
        return edits.size();
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    public void goTo(SpreadsheetEdit edit) {
        if (edit.canUndo()) {
            this.undoTo(edit);
        } else if (edit.canRedo()) {
            this.redoTo(edit);
        }
    }
}
