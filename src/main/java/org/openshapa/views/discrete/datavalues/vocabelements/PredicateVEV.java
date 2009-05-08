package org.openshapa.views.discrete.datavalues.vocabelements;

import org.openshapa.db.PredicateVocabElement;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.VocabEditorV;
import org.openshapa.views.discrete.Editor;
import java.awt.event.KeyEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;

/**
 * A view for a predicate vocab element.
 *
 * @author cfreeman
 */
public class PredicateVEV extends VocabElementV {

    /** The model that this view represents. */
    private PredicateVocabElement pveModel;

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(PredicateVEV.class);

    /**
     * Constructor.
     *
     * @param pve The predicate vocab element that this view represents.
     * @param vev The parent vocab editor that this view will reside within.
     */
    public PredicateVEV(PredicateVocabElement pve, VocabEditorV vev) {
        super(pve, vev);
        pveModel = pve;
        URL iconURL = getClass().getResource("/icons/p_16.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setTypeIcon(icon);
    }

    /**
     * The action to invoke when the user types a key.
     *
     * @param e The event that triggered this action.
     */
    final public void keyTyped(KeyEvent e) {
        // The backspace key removes digits from behind the caret.
        if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
            && e.getKeyChar() == '\u0008') {

            // Can't delete empty int datavalue.
            this.getNameComponent().removeBehindCaret();
            try {
                pveModel.setName(getNameComponent().getText());
            } catch (SystemErrorException se) {
                logger.error("Unable to delete from predicate name", se);
            }

        // The delete key removes digits ahead of the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u007F') {

            // Can't delete empty int datavalue.
            this.getNameComponent().removeAheadOfCaret();
            try {
                pveModel.setName(getNameComponent().getText());
            } catch (SystemErrorException se) {
                logger.error("Unable to delete from predicate name", se);
            }

        // If the character is not reserved - add it to the name of the pred
        } else if (e.getKeyChar() != '<' && e.getKeyChar() != '>'
                   && e.getKeyChar() != '(' && e.getKeyChar() != ')'
                   && e.getKeyChar() != ',' && e.getKeyChar() != '"') {

            try {
                Editor field = getNameComponent();
                field.removeSelectedText();

                StringBuffer cValue = new StringBuffer(field.getText());
                cValue.insert(field.getCaretPosition(), e.getKeyChar());

                pveModel.setName(cValue.toString());
                field.advanceCaret();
            } catch (SystemErrorException se) {
                logger.error("Unable to set new predicate name", se);
            }
        }

        setHasChanged(true);
        rebuildContents();
        e.consume();
    }
}
