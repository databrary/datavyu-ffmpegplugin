package org.openshapa.views.discrete.datavalues;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.DataCell;
import org.openshapa.db.Database;
import org.openshapa.db.Matrix;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.Predicate;
import org.openshapa.db.PredicateVocabElement;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.discrete.EditorComponent;
import org.openshapa.views.discrete.EditorTracker;

/**
 * This class is the character editor of a Predicate name.
 * DataValueEditor issues.
 */
public final class PredicateNameEditor extends DataValueEditor {

    /** The matrixRootView in which this editor belongs. */
    private MatrixRootView matrixRootView;

    private DataCell parentCell;
    private Matrix parentMatrix;
    private int matIndex;
    private String lastSearch = "";
    private boolean editing = false;

    /** Vector of the editors that make up the predicate. */
    private Vector<EditorComponent> argsEditors;

    /** String holding the reserved characters. */
    static final String PREDNAME_RESERVED_CHARS = ")(<>|,;";

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(PredicateNameEditor.class);

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public PredicateNameEditor(final JTextComponent ta,
                            final DataCell cell,
                            final Matrix matrix,
                            final int matrixIndex,
                            final Vector<EditorComponent> eds) {

        super(ta, cell, matrix, matrixIndex);

        matrixRootView = (MatrixRootView) ta;
        parentCell = cell;
        parentMatrix = matrix;
        matIndex = matrixIndex;
        argsEditors = eds;
    }

    /**
     * Recalculate the string for this editor.
     * Overrides because toString for a Predicate includes all the args
     * We just want the predicate name in this case.
     */
    @Override
    public void updateStrings() {
        if (!editing) {
            String t = "";
            if (!isNullArg()) {
                try {
                    PredDataValue pdv = (PredDataValue) getModel();
                    Predicate pred = pdv.getItsValue();
                    t = pred.getPredName();
                } catch (SystemErrorException e) {

                }
            } else {
                t = getNullArg();
            }

            this.resetText(t);
        }
    }

    /**
     * focusSet is the signal that this editor has become "current".
    */
    @Override
    public void focusSet() {
        super.focusSet();
        lastSearch = getText();
        editing = true;
    }

    /**
     * Action to take when focus is lost for this editor.
     * @param fe Focus Event
     */
    @Override
    public void focusLost(final FocusEvent fe) {
        editing = false;
        super.focusLost(fe);
    }
    
    /**
     * The action to invoke when a key is typed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        super.keyTyped(e);

        if (e.isConsumed()) {
            return;
        }

        if (isReserved(e.getKeyChar())) {
            // Ignore reserved characters.
            e.consume();
            return;
        }
    }

    /**
     * Action to take by this editor when a key is released.
     * @param e KeyEvent
     */
    @Override
    public void keyReleased(final KeyEvent e) {
        super.keyReleased(e);

        if (isNullArg()) {
            return;
        }

        // check if no change since last search for a predicate match
        if (getText().equals(lastSearch)) {
            return;
        }

        // else look for the predicate matching the current text
        lastSearch = getText();
        boolean found = false;
        Predicate pred = null;
        // Match the predicate name and update as needed.
        try {
            Database db = this.getModel().getDB();
            Vector<PredicateVocabElement> pves = db.getPredVEs();
            for (int i = 0; i < pves.size(); i++) {
                if (pves.get(i).getName().equals(this.getText())) {

                    pred = new Predicate(db, pves.elementAt(i).getID());
                    // to get around an issue with the cellID set in the
                    // Predicate, I call the following:
                    pred.setCellID(parentCell.getID());

                    found = true;
                    break;
                }
            }
        } catch (SystemErrorException se) {
            logger.error("Unable to set Predicate.", se);
        }

        EditorTracker edTracker = matrixRootView.getEdTracker();
        if (!found) {
            if (argsEditors.size() > 0) {
                edTracker.removeEditors(argsEditors);
                argsEditors.clear();
                matrixRootView.rebuildText();
            }
            setPredicate(null);
        } else {
            edTracker.removeEditors(argsEditors);
            argsEditors.clear();
            // the next statement will cause a cascade through spreadsheetcell
			// but the arg value editors are removed
            setPredicate(pred);
			// now put the new arg editors back in
            try {
                Vector<EditorComponent> newArgs = DataValueEditorFactory
              .buildPredicate(matrixRootView, parentCell, parentMatrix, matIndex);

                // take out the predname editor
                newArgs.remove(0);
                argsEditors.addAll(newArgs);
            } catch (SystemErrorException ex) {
                logger.error("Unable to build new predicate editors", ex);
            }
            int caret = getCaretPosition();
            edTracker.addEditors(argsEditors);
            matrixRootView.rebuildText();
            setCaretPosition(caret);
        }
    }

    private void setPredicate(Predicate pred) {
        PredDataValue pdv = (PredDataValue) getModel();
        try {
            if (pred != null) {
                pdv.setItsValue(pred);
            } else {
                if (!pdv.isEmpty()) {
                    // following is to address issue with cellID
                    Predicate nullPred = new Predicate(this.getModel().getDB());
                    nullPred.setCellID(parentCell.getID());
                    pdv.setItsValue(nullPred);
                    // -----------------------------------------
                    pdv.clearValue();
                }
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to edit value", e);
        }

        try {
            // Update the OpenSHAPA database with the latest values.
            if (parentMatrix != null) {
                parentMatrix.replaceArg(matIndex, getModel());
            }
            parentCell.setVal(parentMatrix);
            parentCell.getDB().replaceCell(parentCell);
        } catch (SystemErrorException ex) {
            logger.error("Unable to update Database: ", ex);
        }
    }

    /**
     * Update the database with the model value.
     */
    public void updateDatabase() {
        // update the model.
        if (isNullArg()) {
            updateModelNull();
        }
		// rest of the update functionality is in keyreleased and has already occurred.
    }

    /**
     * @param aChar Character to test
     * @return true if the character is a reserved character.
     */
    public boolean isReserved(char aChar) {
        return (PREDNAME_RESERVED_CHARS.indexOf(aChar) >= 0);
    }

    /**
     * Update the model to reflect the value represented by the
     * editor's text representation.
    */
    @Override
    public void updateModelValue() {
        // handled for now in keyReleased
    }

    /**
     * Sanity check the current text of the editor and return a boolean.
     * @return true if the text is an okay representation for this DataValue.
     */
    @Override
    public boolean sanityCheck() {
        boolean res = true;
        // could call a subRange test for this dataval
        return res;
    }
}