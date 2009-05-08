package org.openshapa.views.discrete.datavalues;

import org.openshapa.OpenSHAPA;
import org.openshapa.db.Database;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.Predicate;
import org.openshapa.db.PredicateVocabElement;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.discrete.Editor;
import java.awt.event.KeyEvent;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 
 *
 * @author cfreeman
 */
public final class PredicateNameV extends DataValueElementV {

    /** The parent component that this name resides within. */
    private PredicateDataValueView parentComponent;

    /** The database this predicate name belongs too. */
    private Database db;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(PredicateNameV.class);

    /**
     * Constructor.
     *
     * @param parent The parent data value view for the predicate name.
     */
    public PredicateNameV(final PredicateDataValueView parent) {
        super(parent.getPredDataValue(), true);
        parentComponent = parent;
        db = OpenSHAPA.getDatabase();

        PredDataValue pdv = parent.getPredDataValue();
        Predicate p = parent.getPredicate();
        if (pdv.isEmpty()) {
            this.getEditor().setText(parent.getNullArg());
        } else {
            this.getEditor().setText(p.getPredName());
        }
    }

    /**
     * Updates the database with the latest value.
     */
    @Override
    public void updateDatabase() {
        // Match the predicate name and update as needed.
        try {
            Vector<PredicateVocabElement> pves = db.getPredVEs();

            boolean found = false;
            for (int i = 0; i < pves.size(); i++) {
                if (pves.get(i).getName().equals(this.getText())) {

                    Predicate p = new Predicate(db, pves.elementAt(i).getID());
                    parentComponent.setPredicate(p);
                    found = true;
                    break;
                }
            }

            if (!found) {
                parentComponent.clearPreds();
                parentComponent.invalidate();
                parentComponent.repaint();
            }

            this.getEditor().storeCaretPosition();
            this.getEditor().restoreCaretPosition();
        } catch (SystemErrorException se) {
            logger.error("Unable to set Predicate.", se);
        }
    }

    @Override
    protected Editor buildEditor() {
        return new PredicateEditor();
    }

    /**
     * @return The text of the predicate name.
     */
    public String getText() {
        return this.getEditor().getText();
    }

    /**
     * The editor for the name of the predicate datavalue.
     */
    class PredicateEditor extends DataValueElementV.DataValueEditor {

        /**
         * The action to invoke when a key is typed.
         *
         * @param e The KeyEvent that triggered this action.
         */
        public void keyTyped(final KeyEvent e) {

            // The backspace key removes digits from behind the caret.
            if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                && e.getKeyChar() == '\u0008') {

                // Can't delete empty int datavalue.
                this.removeBehindCaret();

            // The delete key removes digits ahead of the caret.
            } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                       && e.getKeyChar() == '\u007F') {

                // Can't delete empty int datavalue.
                this.removeAheadOfCaret();

            // If the character is not reserved - add it to the name of the pred
            } else if (e.getKeyChar() != '<' && e.getKeyChar() != '>'
                       && e.getKeyChar() != '(' && e.getKeyChar() != ')'
                       && e.getKeyChar() != ',' && e.getKeyChar() != '"') {

                this.removeSelectedText();
                StringBuffer cValue = new StringBuffer(this.getText());
                cValue.insert(this.getCaretPosition(), e.getKeyChar());
                this.setText(cValue.toString());
            }

            updateDatabase();
            e.consume();
        }
    }

}
