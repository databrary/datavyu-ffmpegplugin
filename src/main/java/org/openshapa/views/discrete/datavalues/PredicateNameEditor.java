package org.openshapa.views.discrete.datavalues;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.DBIndex;
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

    /** The predicate name to search for in the vocab. */
    private String searchText = "";

    /** Set true while we are editing the predicate name. */
    private boolean editing = false;

    /** The new predicate ID corresponding to the edited text. */
    private long newPredID = DBIndex.INVALID_ID;

    /** Vector of the editors that make up the predicate args. */
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
     * @param eds The vector of predicate argument editors.
     */
    public PredicateNameEditor(final JTextComponent ta,
                            final DataCell cell,
                            final Matrix matrix,
                            final int matrixIndex,
                            final Vector<EditorComponent> eds) {

        super(ta, cell, matrix, matrixIndex);

        matrixRootView = (MatrixRootView) ta;
        argsEditors = eds;
    }

    /**
     * Recalculate the string for this editor.
     * Overrides because toString for a Predicate includes all the args
     * We just want the predicate name in this case.
     */
    @Override
    public void updateStrings() {
        // if we are editing the name, do not try to get it from the
        // database or else it will change to a null value.
        if (editing) {
            return;
        }

        String t = "";
        if (!isNullArg()) {
            try {
                PredDataValue pdv = (PredDataValue) getModel();
                Predicate pred = pdv.getItsValue();
                t = pred.getPredName();
            } catch (SystemErrorException e) {
                logger.error("Problem getting predicate.", e);
            }
        }
        if (t.length() == 0) {
            t = getNullArg();
        }
        this.resetText(t);
    }

    /**
     * focusSet is the signal that this editor has become "current".
     * @param fe Focus Event
     */
    @Override
    public void focusGained(final FocusEvent fe) {
        super.focusGained(fe);
        searchText = getText();
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
        updateStrings();
    }

    /**
     * The action to invoke when a key is typed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        super.keyTyped(e);

        if (!e.isConsumed() && isReserved(e.getKeyChar())) {
            // Ignore reserved characters.
            e.consume();
        }
    }

    /**
     * Action to take by this editor when a key is released.
     * @param e KeyEvent
     */
    @Override
    public void keyReleased(final KeyEvent e) {
        super.keyReleased(e);

        // return if no change since last search for a predicate match
        if (getText().equals(searchText)) {
            return;
        }

        int caret = getCaretPosition();

        // reset the predicate argument editors
        EditorTracker edTracker = matrixRootView.getEdTracker();
        if (argsEditors.size() > 0) {
            edTracker.removeEditors(argsEditors);
            argsEditors.clear();
        }

        // look for the predicate vocab element matching the current text
        searchText = getText();
        newPredID = DBIndex.INVALID_ID;
        if (!isNullArg()) {
            try {
                Database db = this.getModel().getDB();
                for (PredicateVocabElement pve : db.getPredVEs()) {
                    if (pve.getName().equals(searchText)) {
                        newPredID = pve.getID();
                        break;
                    }
                }
            } catch (SystemErrorException se) {
                logger.error("Unable to search vocab.", se);
            }
        }

        // set the new predicate
        updateDatabase();

        // Setting the predicate causes a reset of the predicate datavalue.
        // Build the new arg editors and add to the editor tracker
        argsEditors.addAll(buildArgEditors());

        if (argsEditors.size() > 0) {
            edTracker.addEditors(argsEditors);
        }
        matrixRootView.rebuildText();
        setCaretPosition(caret);
    }

    /**
     * @param aChar Character to test
     * @return true if the character is a reserved character.
     */
    private boolean isReserved(final char aChar) {
        return (PREDNAME_RESERVED_CHARS.indexOf(aChar) >= 0);
    }

    /**
     * Update the model to reflect the value represented by the
     * editor's text representation.
    */
    @Override
    public void updateModelValue() {
        try {
            PredDataValue pdv = (PredDataValue) getModel();
            // make a new predicate data value
            pdv = new PredDataValue(getCell().getDB());
            if (!editing && newPredID == DBIndex.INVALID_ID) {
                pdv.clearValue();
            } else {
                Predicate pred = pdv.getItsValue();
                pred.setPredID(newPredID, true);
                pdv.setItsValue(pred);
            }
            setModel(pdv);
        } catch (SystemErrorException e) {
            logger.error("Unable to edit value", e);
        }
    }

    /**
     * Builds the argument editors for this cells predicate.
     * @return Vector of the arg editors.
     */
    private Vector<EditorComponent> buildArgEditors() {
        Vector<EditorComponent> eds = new Vector<EditorComponent>();
        try {
            eds = DataValueEditorFactory.buildPredicateArgs(matrixRootView,
                                           getCell(), getMatrix(), getmIndex());
        } catch (SystemErrorException ex) {
            logger.error("Unable to build new predicate arg editors", ex);
        }
        return eds;
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