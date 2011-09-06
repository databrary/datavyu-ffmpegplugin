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
package org.openshapa.views.discrete.datavalues;

import com.usermetrix.jclient.Logger;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.text.JTextComponent;

import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.EditorComponent;

import com.usermetrix.jclient.UserMetrix;
import javax.swing.undo.UndoableEdit;

import database.DataCell;
import database.DataValue;
import database.FloatDataValue;
import database.FormalArgument;
import database.IntDataValue;
import database.Matrix;
import database.MatrixVocabElement;
import database.NominalDataValue;
import database.PredDataValue;
import database.Predicate;
import database.PredicateVocabElement;
import database.QuoteStringDataValue;
import database.SystemErrorException;
import database.TextStringDataValue;
import org.openshapa.undoableedits.ChangeCellEdit.Granularity;
import org.openshapa.undoableedits.ChangeValCellEdit;

/**
 * DataValueEditor - abstract class extending EditorComponent. Adds
 * functionality specific to DataValues being edited.
 */
public abstract class DataValueEditor extends EditorComponent {

    /** The parent matrix for the DataValue that this view represents. */
    private Matrix parentMatrix;

    /** The parent predicate for the DataValue that this view represents. */
    private PredDataValue parentPredicate;

    /** The DataValue that this view represents. **/
    private DataValue model = null;

    /** The parent datacell for the DataValue that this view represents. */
    private long parentCell;

    /** The index of the datavalue within its parent matrix. */
    private int mIndex;

    /** The index of the data value within its parent predicate. */
    private int pIndex;

    /** Text when editor gained focus (became current editor). */
    private String textOnFocus;

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(DataValueEditor.class);
    
    /** the previous version of the Cell. */
    private DataCell cb = null;       
    private DataCell ca_old = null;       
    private DataCell cb_old = null;       
     
    /**
     * Constructor.
     * 
     * @param tc
     *            JTextComponent this editor works with.
     * @param cell
     *            The Parent cell that holds the matrix.
     * @param matrix
     *            The parent matrix that holds the DataValue.
     * @param matrixIndex
     *            The index of the data value within the above matrix that this
     *            view is to represent.
     */
    public DataValueEditor(final JTextComponent tc, final DataCell cell,
            final Matrix matrix, final int matrixIndex) {
        super(tc);
        init(cell, null, 0, matrix, matrixIndex);
    }

    /**
     * Constructor.
     * 
     * @param ta
     *            The parent JTextComponent that this editor is nested within.
     * @param cell
     *            The Parent cell that holds the matrix.
     * @param predicate
     *            The parent predicate.
     * @param predicateIndex
     *            The index of the data value within the above predicate that
     *            this view is to represent.
     * @param matrix
     *            The parent matrix that holds the DataValue.
     * @param matrixIndex
     *            The index of the data value within the above matrix that this
     *            view is to represent.
     */
    public DataValueEditor(final JTextComponent ta, final DataCell cell,
            final PredDataValue predicate, final int predicateIndex,
            final Matrix matrix, final int matrixIndex) {
        super(ta);
        init(cell, predicate, predicateIndex, matrix, matrixIndex);
    }

    /**
     * Initialise internal values.
     * 
     * @param cell
     *            The Parent cell that holds the matrix.
     * @param predicate
     *            The parent predicate.
     * @param predicateIndex
     *            The index of the data value within the above predicate that
     *            this view is to represent.
     * @param matrix
     *            The parent matrix that holds the DataValue.
     * @param matrixIndex
     *            The index of the data value within the above matrix that this
     *            view is to represent.
     */
    private void init(final DataCell cell, final PredDataValue predicate,
            final int predicateIndex, final Matrix matrix,
            final int matrixIndex) {
        // so far all DataValueEditors are editable
        setEditable(true);
        try {
            parentCell = cell.getID();
            parentPredicate = predicate;
            pIndex = predicateIndex;
            parentMatrix = matrix;
            mIndex = matrixIndex;

            if (predicate == null) {
                model = matrix.getArgCopy(mIndex);
            } else {
                Predicate p = predicate.getItsValue();
                model = p.getArgCopy(pIndex);
            }

        } catch (SystemErrorException ex) {
            LOGGER.error("Unable to create DataValueEditor: ", ex);
        }

        updateStrings();
    }

    /**
     * Reset the values by retrieving from the database.
     * 
     * @param cell
     *            The Parent cell that holds the matrix.
     * @param matrix
     *            The parent matrix that holds the DataValue.
     */
    @Override
    public final void resetValue(final DataCell cell, final Matrix matrix) {
        try {
            parentMatrix = matrix;
            parentCell = cell.getID();
            if (parentPredicate == null) {
                model = matrix.getArgCopy(mIndex);
            } else {
                parentPredicate = (PredDataValue) matrix.getArgCopy(mIndex);
                Predicate p = parentPredicate.getItsValue();
                model = p.getArgCopy(pIndex);
            }
        } catch (SystemErrorException ex) {
            LOGGER.error("Unable to create DataValue View: ", ex);
        }

        updateStrings();
    }

    /**
     * Recalculate the string for this editor. In particular check if it is
     * "null" and display the appropriate FormalArg.
     */
    public void updateStrings() {
        String t = "";
        if (!getModel().isEmpty()) {
            t = getModel().toString();
        } else {
            t = getNullArg();
        }

        resetText(t);
    }

    /**
     * @return The displayable version of the null argument. Changes: Replace
     *         call to vocabElement.getFormalArg() with call to
     *         vocabElement.getFormalArgCopy().
     */
    public final String getNullArg() {
        String t = "";
        try {
            if (parentMatrix != null && parentPredicate == null) {
                long mveid = parentMatrix.getMveID();
                MatrixVocabElement mve =
                        parentMatrix.getDB().getMatrixVE(mveid);
                FormalArgument fa = mve.getFormalArgCopy(mIndex);
                t = fa.toString();
            } else if (parentMatrix != null && parentPredicate != null) {
                Predicate p = parentPredicate.getItsValue();
                PredicateVocabElement pve =
                        parentMatrix.getDB().getPredVE(p.getPveID());
                FormalArgument fa = pve.getFormalArgCopy(pIndex);
                t = fa.toString();
            }
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to get NULL arg", e);
        }
        return t;
    }

    /**
     * focusSet is the signal that this editor has become "current".
     * 
     * @param fe
     *            Focus Event
     */
    @Override
    public void focusGained(final FocusEvent fe) {        
        cb = null;
        textOnFocus = getText();
    }

    /**
     * Action to take when focus is lost for this editor.
     * @param fe
     *            Focus Event
     */
    @Override
    public void focusLost(final FocusEvent fe) {
        super.focusLost(fe);
        if (!getText().equals(textOnFocus)) {
//            updateDatabase();
            if (cb != null) {
                try {
                    DataCell c = (DataCell) OpenSHAPA.getProjectController().getLegacyDB().getDatabase().getCell(parentCell);
                    // record the effect
                    if (!(cb.getVal().toString().equals(c.getVal().toString()))) {
                       if (((ca_old == null)&&(cb_old == null)) ||
                           (
                            (ca_old != null)&&(cb_old != null) && 
                            (!ca_old.getVal().toString().equals(c.getVal().toString())) &&
                            (!cb_old.getVal().toString().equals(cb.getVal().toString())) 
                           )     
                          ) {    
                                UndoableEdit edit = new ChangeValCellEdit(cb);                        
                                // notify the listeners
                                OpenSHAPA.getView().getUndoSupport().postEdit(edit);
                                ca_old = new DataCell(c);
                                cb_old = new DataCell(cb);
                       }         
                     }            
            
                } catch (SystemErrorException e) {
                    LOGGER.error("Unable to create DataCell", e);
                }          
            }            
        }
    }

    /**
     * Action to take by this editor when a key is pressed.
     * @param e
     *            The KeyEvent that triggered this action.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_BACK_SPACE:
            if (!getModel().isEmpty()) {
                removeBehindCaret();
                updateModelText();
            }
            e.consume();
            break;
        case KeyEvent.VK_DELETE:
            if (!getModel().isEmpty()) {
                removeAheadOfCaret();
                updateModelText();
            }
            e.consume();
            break;

        case KeyEvent.VK_LEFT:
            int selectStart = getSelectionStart();
            int selectEnd = getSelectionEnd();

            // Move caret to the left.
            int c = Math.max(0, getCaretPosition() - 1);
            setCaretPosition(c);

            // If after the move, we have a character to the left is
            // preserved character we need to skip one before passing
            // the key event down to skip again (effectively skipping
            // the preserved character).
            int b = Math.max(0, getCaretPosition());
            c = Math.max(0, getCaretPosition() - 1);
            if (isPreserved(getText().charAt(b))
                    || isPreserved(getText().charAt(c))) {
                setCaretPosition(Math.max(0, getCaretPosition() - 1));
            }
            e.consume();

            // If the user is holding down shift - alter the selection as
            // well as the caret position.
            if (e.getModifiers() == InputEvent.SHIFT_MASK) {
                // Shrink selection left - removed entire selection.
                if (getCaretPosition() == selectStart) {
                    select(selectStart, selectStart);
                    // Grow selection left.
                } else if (getCaretPosition() < selectStart) {
                    select(selectEnd, getCaretPosition());
                    // Shrink selection left.
                } else {
                    select(selectStart, getCaretPosition());
                }
            }

            break;

        case KeyEvent.VK_RIGHT:
            selectStart = getSelectionStart();
            selectEnd = getSelectionEnd();

            // Move caret to the right.
            c = Math.min(getText().length(), getCaretPosition() + 1);
            setCaretPosition(c);

            // If after the move, we have a character to the right that
            // is a preserved character, we need to skip one before
            // passing the key event down to skip again (effectively
            // skipping the preserved character)
            b = Math.min(getText().length() - 1, getCaretPosition());
            c = Math.min(getText().length() - 1, getCaretPosition() + 1);
            if (c < getText().length()
                    && (isPreserved(getText().charAt(c)) || isPreserved(getText()
                            .charAt(b)))) {
                setCaretPosition(Math.min(getText().length() - 1,
                        getCaretPosition() + 1));
            }
            e.consume();

            // If the user is holding down shift - alter the selection as
            // well as the caret position.
            if (e.getModifiers() == InputEvent.SHIFT_MASK) {
                // Shrink selection right - removed entire selection.
                if (getCaretPosition() == selectEnd) {
                    select(selectEnd, selectEnd);
                    // Grow selection right.
                } else if (getCaretPosition() > selectEnd) {
                    select(selectStart, getCaretPosition());
                    // Shrink select right.
                } else {
                    select(getCaretPosition(), selectEnd);
                }
            }

            break;

        default:
            break;
        }
    }

    /**
     * Action to take by this editor when a key is typed.
     * 
     * @param e
     *            KeyEvent
     */
    @Override
    public void keyTyped(final KeyEvent e) {

    }

    /**
     * Action to take by this editor when a key is released.
     * 
     * @param e
     *            KeyEvent
     */
    @Override
    public void keyReleased(final KeyEvent e) {
        boolean argIsNull = true;

        if (getText().length() == 0) {
            setText(getNullArg());
        } else {
            argIsNull = (getText().equals(getNullArg()));
        }

        if (argIsNull) {
            selectAll();
        }
    }

    /**
     * Update the database with the model value.
     */
    public void updateDatabase() {
        try {
            // reget the parentCell in case onset or offset have been changed.
            DataCell c =
                    (DataCell) OpenSHAPA.getProjectController().getLegacyDB().getDatabase()
                            .getCell(parentCell);

            if (cb == null) {
                cb = (DataCell) OpenSHAPA.getProjectController().getLegacyDB().getDatabase().getCell(parentCell);
            }
            
            // Update the OpenSHAPA database with the latest values.
            if (parentMatrix != null && parentPredicate == null) {
                parentMatrix.replaceArg(mIndex, model);
            } else if (parentMatrix != null && parentPredicate != null) {

                Predicate p = parentPredicate.getItsValue();
                p.replaceArg(pIndex, model);
                parentPredicate.setItsValue(p);
                parentMatrix.replaceArg(mIndex, parentPredicate);
            }
            // Record the effect
            UndoableEdit edit = new ChangeValCellEdit(c, Granularity.FINEGRAINED);                        
            // perform the action            
            c.setVal(parentMatrix);
            c.getDB().replaceCell(c);
            // notify the listeners
            OpenSHAPA.getView().getUndoSupport().postEdit(edit);            
            
        } catch (SystemErrorException ex) {
            LOGGER.error("Unable to update Database: ", ex);
        }
    }

    /**
     * Determine if the editor tracker permits sub selections within the
     * component.
     * 
     * @return True if permitted to perform sub selection, false otherwise.
     */
    @Override
    public boolean canSubSelect() {
        return (!getModel().isEmpty());
    }

    /**
     * Override selection to catch if the value is null.
     * 
     * @param startClick
     *            Start character of the selection.
     * @param endClick
     *            End character of the selection.
     */
    @Override
    public final void select(final int startClick, final int endClick) {
        if (getModel().isEmpty()) {
            selectAll();
        } else {
            super.select(startClick, endClick);
        }
    }

    /**
     * Set the model data value.
     * 
     * @param dv
     *            The datavalue to set.
     */
    public final void setModel(final DataValue dv) {
        model = dv;
    }

    /**
     * @return The model that this data value view represents.
     */
    public final DataValue getModel() {
        return model;
    }

    /**
     * @return The matrix this datavalue is in.
     */
    public final Matrix getMatrix() {
        return parentMatrix;
    }

    /**
     * @return The index within the matrix where this datavalue exists.
     */
    public final int getmIndex() {
        return mIndex;
    }

    private void updateModelText() {
        switch (model.getItsFargType()) {
            case COL_PREDICATE:
                // This should never execute
                LOGGER.error("Predicate name went through DataValueEditor!");
                break;
            case FLOAT:
                FloatDataValue fdv = (FloatDataValue) model;
                try {
                    Float.parseFloat(getText());
                    fdv.setItsValue(getText());
                } catch (NumberFormatException nfe) {
                    // We have an empty or unusable string
                    fdv.clearValue();
                }
                break;
            case INTEGER:
                IntDataValue idv = (IntDataValue) model;
                try {
                    Integer.parseInt(getText());
                    idv.setItsValue(getText());
                } catch (NumberFormatException nfe) {
                    // We have an empty or unusable string
                    idv.clearValue();
                }
                break;
            case NOMINAL:
                NominalDataValue ndv = (NominalDataValue) model;
                try {
                    if (getText() == null || getText().equals("")) {
                        ndv.clearValue();
                    } else {
                        ndv.setItsValue(getText());
                    }
                } catch (SystemErrorException sysErr) {
                    LOGGER.error("Couldn't set nominal value", sysErr);
                    return;
                }
                break;
            case PREDICATE:
                // This should never execute
                LOGGER.error("Predicate field went through DataValueEditor!");
                break;
            case QUOTE_STRING:
                QuoteStringDataValue qsdv = (QuoteStringDataValue) model;
                try {
                    if (getText() == null || getText().equals("")) {
                        qsdv.clearValue();
                    } else {
                        qsdv.setItsValue(getText());
                    }
                } catch (SystemErrorException sysErr) {
                    LOGGER.error("Couldn't set quote string value", sysErr);
                    return;
                }
                break;
            case TEXT:
                TextStringDataValue tsdv = (TextStringDataValue) model;
                try {
                    if (getText() == null || getText().equals("")) {
                        tsdv.clearValue();
                    } else {
                        tsdv.setItsValue(getText());
                    }
                } catch (SystemErrorException sysErr) {
                    LOGGER.error("Couldn't set text string value", sysErr);
                    return;
                }
                break;
            case TIME_STAMP:
                // This should never execute
                LOGGER.error("Timestamp field went through DataValueEditor!");
                break;
            case UNDEFINED:
                // This should never execute
                LOGGER.error("Undefined DataValue!");
                break;
            case UNTYPED:
                // This should never execute
                LOGGER.error("Untyped DataValue!");
                break;
            default:
                // This should never execute
                LOGGER.error("Untyped DataValue!");
        }
        updateDatabase();
    }
}
