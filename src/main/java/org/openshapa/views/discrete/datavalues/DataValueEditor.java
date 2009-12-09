package org.openshapa.views.discrete.datavalues;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.DataCell;
import org.openshapa.db.DataValue;
import org.openshapa.db.FormalArgument;
import org.openshapa.db.Matrix;
import org.openshapa.db.MatrixVocabElement;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.Predicate;
import org.openshapa.db.PredicateVocabElement;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.discrete.EditorComponent;

/**
 * DataValueEditor - abstract class extending EditorComponent.
 * Adds functionality specific to DataValues being edited.
 */
public abstract class DataValueEditor extends EditorComponent {

    /** The parent matrix for the DataValue that this view represents.*/
    private Matrix parentMatrix;

    /** The parent predicate for the DataValue that this view represents. */
    private PredDataValue parentPredicate;

    /** The DataValue that this view represents. **/
    private DataValue model = null;

    /** The parent datacell for the DataValue that this view represents. */
    private DataCell parentCell;

    /** The index of the datavalue within its parent matrix. */
    private int mIndex;

    /** The index of the data value within its parent predicate. */
    private int pIndex;

    /** Text when editor gained focus (became current editor). */
    private String textOnFocus;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(DataValueEditor.class);

    /**
     * Constructor.
     *
     * @param tc JTextComponent this editor works with.
     * @param cell The Parent cell that holds the matrix.
     * @param matrix The parent matrix that holds the DataValue.
     * @param matrixIndex The index of the data value within the above matrix
     * that this view is to represent.
     */
    public DataValueEditor(final JTextComponent tc,
                           final DataCell cell,
                           final Matrix matrix,
                           final int matrixIndex) {
        super(tc);
        init(cell, null, 0, matrix, matrixIndex);
    }

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent that this editor is nested within.
     * @param cell The Parent cell that holds the matrix.
     * @param predicate The parent predicate.
     * @param predicateIndex The index of the data value within the above
     * predicate that this view is to represent.
     * @param matrix The parent matrix that holds the DataValue.
     * @param matrixIndex The index of the data value within the above matrix
     * that this view is to represent.
     */
    public DataValueEditor(final JTextComponent ta,
                           final DataCell cell,
                           final PredDataValue predicate,
                           final int predicateIndex,
                           final Matrix matrix,
                           final int matrixIndex) {
        super(ta);
        init(cell, predicate, predicateIndex, matrix, matrixIndex);
    }

    /**
     * Initialise internal values.
     *
     * @param cell The Parent cell that holds the matrix.
     * @param predicate The parent predicate.
     * @param predicateIndex The index of the data value within the above
     * predicate that this view is to represent.
     * @param matrix The parent matrix that holds the DataValue.
     * @param matrixIndex The index of the data value within the above matrix
     * that this view is to represent.
     */
    private void init(final DataCell cell,
                      final PredDataValue predicate,
                      final int predicateIndex,
                      final Matrix matrix,
                      final int matrixIndex) {
        // so far all DataValueEditors are editable
        setEditable(true);
        try {
            parentCell = cell;
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
            logger.error("Unable to create DataValueEditor: ", ex);
        }

        updateStrings();
    }

    /**
     * Reset the values by retrieving from the database.
     *
     * @param cell The Parent cell that holds the matrix.
     * @param matrix The parent matrix that holds the DataValue.
     */
    @Override
    public final void resetValue(final DataCell cell, final Matrix matrix) {
        try {
            parentMatrix = matrix;
            parentCell = cell;
            if (parentPredicate == null) {
                model = matrix.getArgCopy(mIndex);
            } else {
                parentPredicate = (PredDataValue) matrix.getArgCopy(mIndex);
                Predicate p = parentPredicate.getItsValue();
                model = p.getArgCopy(pIndex);
            }
        } catch (SystemErrorException ex) {
            logger.error("Unable to create DataValue View: ", ex);
        }

        updateStrings();
    }

    /**
     * Recalculate the string for this editor.  In particular check if it
     * is "null" and display the appropriate FormalArg.
     */
    public void updateStrings() {
        String t = "";
        if (!this.getModel().isEmpty()) {
            t = this.getModel().toString();
        } else {
            t = getNullArg();
        }

        this.resetText(t);
    }

    /**
     * @return The displayable version of the null argument.
     *
     * Changes: Replace call to vocabElement.getFormalArg() with call
     *          to vocabElement.getFormalArgCopy().
     */
    public final String getNullArg() {
        String t = "";
        try {
            if (parentMatrix != null && parentPredicate == null) {
                long mveid = parentMatrix.getMveID();
                MatrixVocabElement mve = parentMatrix.getDB()
                                                     .getMatrixVE(mveid);
                FormalArgument fa = mve.getFormalArgCopy(mIndex);
                t = fa.toString();
            } else if (parentMatrix != null && parentPredicate != null) {
                Predicate p = parentPredicate.getItsValue();
                PredicateVocabElement pve = parentMatrix.getDB()
                                                    .getPredVE(p.getPveID());
                FormalArgument fa = pve.getFormalArgCopy(pIndex);
                t = fa.toString();
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to get NULL arg", e);
        }
        return t;
    }

    /**
     * focusSet is the signal that this editor has become "current".
     * @param fe Focus Event
     */
    @Override
    public void focusGained(final FocusEvent fe) {
        textOnFocus = getText();
    }

    /**
     * Action to take when focus is lost for this editor.
     * @param fe Focus Event
     */
    @Override
    public void focusLost(final FocusEvent fe) {
        super.focusLost(fe);
        if (!getText().equals(textOnFocus)) {
            updateDatabase();
        }
    }

    /**
     * Action to take by this editor when a key is pressed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                // Ignore - handled when the key is typed.
                e.consume();
                break;

            case KeyEvent.VK_LEFT:
                int selectStart = this.getSelectionStart();
                int selectEnd = this.getSelectionEnd();

                // Move caret to the left.
                int c = Math.max(0, this.getCaretPosition() - 1);
                this.setCaretPosition(c);

                // If after the move, we have a character to the left is
                // preserved character we need to skip one before passing
                // the key event down to skip again (effectively skipping
                // the preserved character).
                int b = Math.max(0, getCaretPosition());
                c = Math.max(0, this.getCaretPosition() - 1);
                if (this.isPreserved(getText().charAt(b))
                    || this.isPreserved(getText().charAt(c))) {
                    setCaretPosition(Math.max(0, getCaretPosition() - 1));
                }
                e.consume();

                // If the user is holding down shift - alter the selection as
                // well as the caret position.
                if (e.getModifiers() == KeyEvent.SHIFT_MASK) {
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
                selectStart = this.getSelectionStart();
                selectEnd = this.getSelectionEnd();

                // Move caret to the right.
                c = Math.min(this.getText().length(),
                                 this.getCaretPosition() + 1);
                this.setCaretPosition(c);

                // If after the move, we have a character to the right that
                // is a preserved character, we need to skip one before
                // passing the key event down to skip again (effectively
                // skipping the preserved character)
                b = Math.min(getText().length() - 1, getCaretPosition());
                c = Math.min(getText().length() - 1, getCaretPosition() + 1);
                if (c < this.getText().length()
                    && (this.isPreserved(getText().charAt(c))
                        || this.isPreserved(getText().charAt(b)))) {
                    setCaretPosition(Math.min(getText().length() - 1,
                                              getCaretPosition() + 1));
                }
                e.consume();

                // If the user is holding down shift - alter the selection as
                // well as the caret position.
                if (e.getModifiers() == KeyEvent.SHIFT_MASK) {
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
     * @param e KeyEvent
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        // The backspace key removes digits from behind the caret.
        if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
            && e.getKeyChar() == '\u0008') {

            // Can't delete an empty data value.
            if (!this.getModel().isEmpty()) {
                this.removeBehindCaret();
                e.consume();
            }

        // The delete key removes digits ahead of the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u007F') {

            // Can't delete an empty data value.
            if (!this.getModel().isEmpty()) {
                this.removeAheadOfCaret();
                e.consume();
            }
        }
    }

    /**
     * Action to take by this editor when a key is released.
     * @param e KeyEvent
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
            parentCell = (DataCell) parentCell.getDB()
                                              .getCell(parentCell.getID());

            // Update the OpenSHAPA database with the latest values.
            if (parentMatrix != null && parentPredicate == null) {
                parentMatrix.replaceArg(mIndex, model);
            } else if (parentMatrix != null && parentPredicate != null) {

                Predicate p = parentPredicate.getItsValue();
                p.replaceArg(pIndex, model);
                parentPredicate.setItsValue(p);
                parentMatrix.replaceArg(mIndex, parentPredicate);
            }
            parentCell.setVal(parentMatrix);
            parentCell.getDB().replaceCell(parentCell);
        } catch (SystemErrorException ex) {
            logger.error("Unable to update Database: ", ex);
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
        return (!this.getModel().isEmpty());
    }

    /**
     * Override selection to catch if the value is null.
     * @param startClick Start character of the selection.
     * @param endClick End character of the selection.
     */
    @Override
    public final void select(final int startClick, final int endClick) {
        if (this.getModel().isEmpty()) {
            this.selectAll();
        } else {
            super.select(startClick, endClick);
        }
    }

    /**
     * Set the model data value.
     * @param dv The datavalue to set.
     */
    public final void setModel(final DataValue dv) {
        model = dv;
    }

    /**
     * @return The model that this data value view represents.
     */
    public final DataValue getModel() {
        return this.model;
    }

    /**
     * @return The datacell this datavlue is in.
     */
    public final DataCell getCell() {
        return this.parentCell;
    }

    /**
     * @return The matrix this datavalue is in.
     */
    public final Matrix getMatrix() {
        return this.parentMatrix;
    }

    /**
     * @return The index within the matrix where this datavalue exists.
     */
    public final int getmIndex() {
        return this.mIndex;
    }
}