package org.openshapa.views.discrete.datavalues;

import com.usermetrix.jclient.Logger;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.text.JTextComponent;

import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.legacy.DBIndex;
import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.Database;
import org.openshapa.models.db.legacy.Matrix;
import org.openshapa.models.db.legacy.PredDataValue;
import org.openshapa.models.db.legacy.Predicate;
import org.openshapa.models.db.legacy.PredicateVocabElement;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.views.discrete.EditorComponent;
import org.openshapa.views.discrete.EditorTracker;

import com.usermetrix.jclient.UserMetrix;

/**
 * This class is the character editor of a Predicate name. DataValueEditor
 * issues.
 */
public final class PredicateNameEditor extends DataValueEditor {

    /** The matrixRootView in which this editor belongs. */
    private MatrixRootView matrixRootView;

    /** Vector of the editors that make up the predicate args. */
    private Vector<EditorComponent> argsEditors;

    /** String holding the reserved characters. */
    static final String PREDNAME_RESERVED_CHARS = ")(<>|,;\t\n";

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(PredicateNameEditor.class);

    /**
     * Constructor.
     * 
     * @param ta
     *            The parent JTextComponent the editor is in.
     * @param cell
     *            The parent data cell this editor resides within.
     * @param matrix
     *            Matrix holding the datavalue this editor will represent.
     * @param matrixIndex
     *            The index of the datavalue within the matrix.
     * @param eds
     *            The vector of predicate argument editors.
     */
    public PredicateNameEditor(final JTextComponent ta, final DataCell cell,
            final Matrix matrix, final int matrixIndex,
            final Vector<EditorComponent> eds) {

        super(ta, cell, matrix, matrixIndex);

        matrixRootView = (MatrixRootView) ta;
        argsEditors = eds;
    }

    /**
     * Recalculate the string for this editor. Overrides because toString for a
     * Predicate includes all the args We just want the predicate name in this
     * case.
     */
    @Override
    public void updateStrings() {
        try {
            String newText = getText();
            PredDataValue pdv = (PredDataValue) getModel();
            if (!pdv.isEmpty()) {
                newText = pdv.getItsValue().getPredName();
            } else if (getText().length() == 0) {
                newText = getNullArg();
            }
            resetText(newText);
        } catch (SystemErrorException e) {
            LOGGER.error("Problem getting predicate.", e);
        }
    }

    /**
     * The action to invoke when a key is typed.
     * 
     * @param e
     *            The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        int pos = getCaretPosition();

        // The backspace key removes characters from behind the caret.
        if (!e.isConsumed()
                && e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                && e.getKeyChar() == '\u0008') {

            removeBehindCaret();
            pos = getCaretPosition();
            searchForPredicate(getText());
            setCaretPosition(pos);
            e.consume();

            // The delete key removes characters ahead of the caret.
        } else if (!e.isConsumed()
                && e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                && e.getKeyChar() == '\u007F') {

            removeAheadOfCaret();
            pos = getCaretPosition();
            searchForPredicate(getText());
            setCaretPosition(pos);
            e.consume();

            // If the character is not reserved - add it to the name of the pred
        } else if (!e.isConsumed() && !isReserved(e.getKeyChar())) {
            removeSelectedText();
            StringBuilder cValue = new StringBuilder(getText());
            cValue.insert(getCaretPosition(), e.getKeyChar());
            pos = getCaretPosition() + 1;
            setText(cValue.toString());
            searchForPredicate(getText());
            setCaretPosition(pos);
            e.consume();
        }
    }

    /**
     * @param aChar
     *            Character to test
     * @return true if the character is a reserved character.
     */
    private boolean isReserved(final char aChar) {
        return (PREDNAME_RESERVED_CHARS.indexOf(aChar) >= 0);
    }

    /**
     * Searches for a predicate in the database, whose name matches the supplied
     * argument.
     * 
     * @param text
     *            The name of the predicate you are looking for in the database.
     */
    public void searchForPredicate(final String text) {
        try {
            // reset the predicate argument editors
            EditorTracker edTracker = matrixRootView.getEdTracker();
            if (argsEditors.size() > 0) {
                edTracker.removeEditors(argsEditors);
                argsEditors.clear();
            }

            long newPredID = DBIndex.INVALID_ID;
            Database db = getModel().getDB();
            for (PredicateVocabElement pve : db.getPredVEs()) {
                if (pve.getName().equals(getText())) {
                    newPredID = pve.getID();
                    updateModelValue(newPredID);
                    break;
                }
            }

            PredDataValue pdv = (PredDataValue) getModel();
            if (!pdv.isEmpty() && newPredID == DBIndex.INVALID_ID) {
                updateModelValue(newPredID);
            }

        } catch (SystemErrorException se) {
            LOGGER.error("Unable to search vocab.", se);
        }
    }

    /**
     * Update the model to reflect the value represented by the editor's text
     * representation.
     * 
     * @param newPredID
     *            the id of the new predicate data value to use for this
     *            predicate.
     */
    public void updateModelValue(final long newPredID) {
        try {
            // Make a new predicate data value
            PredDataValue pdv =
                    new PredDataValue(OpenSHAPA.getProjectController().getLegacyDB().getDatabase());
            if (newPredID == DBIndex.INVALID_ID) {
                pdv.clearValue();
            } else {
                Predicate pred = pdv.getItsValue();
                pred.setPredID(newPredID, true);
                pdv.setItsValue(pred);
            }
            setModel(pdv);

            // Update database and arguments with latest model.
            updateDatabase();

        } catch (SystemErrorException e) {
            LOGGER.error("Unable to edit value", e);
        }
    }
}