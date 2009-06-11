package org.openshapa.views.discrete.datavalues;

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

    /** Is the data value now null? */
    private boolean argIsNull;

    /** Previous text during edits. */
    private String prevText;

    /** Previous caret location during edits. */
    private int prevCaret;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(DataValueEditor.class);

    /**
     * Function that must be provided by subclasses of this class.
     * Update the model to reflect the value represented by the 
     * editor's text representation.
     */
    public abstract void updateModelValue();

    /**
     * Function that must be provided by subclasses of this class.
     * Sanity check the current text of the editor and return a boolean.
     * @return true if the text is an okay representation for this DataValue.
     */
    public abstract boolean sanityCheck();

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
     * @param tc JTextComponent this editor works with.
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

        argIsNull = (this.getModel() == null || this.getModel().isEmpty());

        updateStrings();
    }

    /**
     * Reset the values by retrieving from the database.
     * @param cell The Parent cell that holds the matrix.
     * @param matrix The parent matrix that holds the DataValue.
     */
    public void resetValue(final DataCell cell, final Matrix matrix) {
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

        argIsNull = (this.getModel() == null || this.getModel().isEmpty());

        updateStrings();
    }

    /**
     * Recalculate the string for this editor.  In particular check if it
     * is "null" and display the appropriate FormalArg.
     */
    public void updateStrings() {
        String t = "";
        if (!isNullArg()) {
            t = this.getModel().toString();
        } else {
            t = getNullArg();
        }

        this.resetText(t);
    }

    /**
     * @return The displayable version of the null argument.
     */
    public final String getNullArg() {
        String t = "";
        try {
            if (parentMatrix != null) {
                long mveid = parentMatrix.getMveID();
                MatrixVocabElement mve = parentMatrix.getDB().getMatrixVE(mveid);
                FormalArgument fa = mve.getFormalArg(mIndex);
                t = fa.toString();
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to get NULL arg", e);
        }
        return t;
    }

    /**
     * Action to take when focus is lost for this editor.
     * @param fe Focus Event
     */
    @Override
    public void focusLost(final FocusEvent fe) {
        updateDatabase();
    }

    /**
     * Action to take by this editor when a key is pressed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
        checkNullArgKeyTyped(e);
    }

    /**
     * Action to take by this editor when a key is typed.
     * @param e KeyEvent
     */
    @Override
    public void keyTyped(KeyEvent e) {
        prevText = getText();
        prevCaret = getCaretPositionLocal();
        checkNullArgKeyTyped(e);
        if (!e.isConsumed()) {
            if (isNullArg()) {
                setText("");
            }
        }
    }

    /**
     * Action to take by this editor when a key is released.
     * @param e KeyEvent
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (getText().length() == 0) {
            argIsNull = true;
            setText(getNullArg());
        } else {
            argIsNull = (getText().equals(getNullArg()));
        }
        if (argIsNull) {
            selectAll();
        } else {
            if (!sanityCheck()) {
                setText(prevText);
                setCaretPosition(prevCaret);
            }
        }
    }

    /**
     * Determine if the next key action is going to create a null arg or
     * if the editor is already effectively a null arg.
     * @param e KeyEvent
     */
    public void checkNullArgKeyTyped(KeyEvent e) {
        // goal is to decide if we are currently a "null" arg
        // or about to become one (delete/backspace key hit).

        // if we are a null arg already
        // consume chars that do not make sense
        // delete, backspace, arrow keys and tab
        int loc = e.getKeyLocation();
        char ch = e.getKeyChar();
        // found this next code on the web - something about
        // not being able to fully consume backspace.  They get around it
        // by using finds in paramString but this seems hacky
        // - need to review and refactor one day
        String paramS = e.paramString();
        if ((loc == KeyEvent.KEY_LOCATION_UNKNOWN && ch == '\u0008')
            || (loc == KeyEvent.KEY_LOCATION_UNKNOWN && ch == '\u007F')
            || (paramS.indexOf("Backspace") != -1)
            || (paramS.indexOf("Delete") != -1)
            || (paramS.indexOf("Tab") != -1)) {
            // The getKeyCode function always returns VK_UNDEFINED for
            // keyTyped events, so backspace is not fully consumed.
            if (isNullArg()) {
                e.consume();
            }
        }
    }

    /**
     * @return true if the editor is currently displaying a "null" arg.
     */
    public boolean isNullArg() {
        return argIsNull;
    }

    /**
     * @return The model that this data value view represents.
     */
    public final DataValue getModel() {
        return this.model;
    }

    /**
     * Update the database with the model value.
     */
    public void updateDatabase() {
        // update the model.
        if (isNullArg()) {
            updateModelNull();
        } else {
            // call the subclass (template pattern)
            updateModelValue();
        }

        try {
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
     * Update the model to reflect a null value.
     */
    public void updateModelNull() {
        DataValue dv = (DataValue) getModel();
        dv.clearValue();
    }

    /**
     * Override selection to catch if the value is null.
     * @param startClick Start character of the selection.
     * @param endClick End character of the selection.
     */
    @Override
    public void select(int startClick, int endClick) {
        if (!isNullArg()) {
            super.select(startClick, endClick);
        } else {
            super.select(0, Integer.MAX_VALUE);
        }
    }

}
