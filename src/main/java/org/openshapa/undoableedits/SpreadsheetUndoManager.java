/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.undoableedits;

import java.util.Vector;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import org.openshapa.undoableedits.ChangeCellEdit.Granularity;

/**
 *
 * @author harold
 */
public class SpreadsheetUndoManager  extends UndoManager{

    public SpreadsheetUndoManager() {
    }

    @Override
    public synchronized boolean addEdit(UndoableEdit ue) {
        boolean addEdit = true;
        boolean result;
        boolean cond;
        if ((edits != null) && (edits.size() > 0)) {
            UndoableEdit prevEdit = edits.lastElement();
            // Do not insert duplicated edits
            if ((ue instanceof ChangeCellEdit) && 
                (ue.getClass() == prevEdit.getClass())) {
                ChangeCellEdit cce = (ChangeCellEdit) ue;
                if (!prevEdit.equals(cce)) {
                   // Remove previous finegrained ChangeCellEdit of the same type
                   if (cce.getGranularity() == Granularity.COARSEGRAINED) {
                       // Take the last element
                       do {
                            Object o = edits.lastElement();
                            cond =  (ue.getClass() == o.getClass()) && 
                                    (((ChangeCellEdit)o).granularity == Granularity.FINEGRAINED);
                            if (cond) {
                                edits.removeElement(o);
                            }
                       } while (cond && edits.size() > 0);
                   }    
               } else { // the new one is equals to the previous one
                   addEdit = false;
               }
            }
        }
        
        if (addEdit) {
            result = super.addEdit(ue);
        } else {
            result = false;
        }
        //printEdits();
        return result;
    }

    
/*
    @Override
    public synchronized boolean addEdit(UndoableEdit ue) {
        boolean result = super.addEdit(ue);
        printEdits();
        return result;
    }
*/    
      
  // Return the complete list of edits in an array.
  public synchronized UndoableEdit[] getEdits() {
    UndoableEdit[] array = new UndoableEdit[edits.size()];
    edits.copyInto(array);
    return array;
  }

  // Return all currently significant undoable edits. The first edit is the
  // next one to be undone.
  public synchronized UndoableEdit[] getUndoableEdits() {
    int size = edits.size();
    Vector v = new Vector(size);
    for (int i=size-1;i>=0;i--) {
      UndoableEdit u = (UndoableEdit)edits.elementAt(i);
      if (u.canUndo() && u.isSignificant())
        v.addElement(u);
    }
    UndoableEdit[] array = new UndoableEdit[v.size()];
    v.copyInto(array);
    return array;
  }

  // Return all currently significant redoable edits. The first edit is the
  // next one to be redone.
  public synchronized UndoableEdit[] getRedoableEdits() {
    int size = edits.size();
    Vector v = new Vector(size);
    for (int i=0; i<size; i++) {
      UndoableEdit u = (UndoableEdit)edits.elementAt(i);
      if (u.canRedo() && u.isSignificant())
        v.addElement(u);
    }
    UndoableEdit[] array = new UndoableEdit[v.size()];
    v.copyInto(array);
    return array;
  }    
  
  private void printEdits() {
      UndoableEdit[] editsV = getEdits();
      System.out.println("Edit List");
      for (UndoableEdit ue : editsV) {
          System.out.println(ue.getPresentationName());
      }
      System.out.println();
  }
    
}
