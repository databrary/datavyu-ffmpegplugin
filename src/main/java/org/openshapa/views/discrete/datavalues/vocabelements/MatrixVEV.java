package org.openshapa.views.discrete.datavalues.vocabelements;

import org.openshapa.db.MatrixVocabElement;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.VocabEditorV;
import org.openshapa.views.discrete.Editor;
import java.awt.event.KeyEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;

/**
 * A view for a matrix vocab element.
 */
public class MatrixVEV extends VocabElementV {
    /** The model that this view represents. */
    private MatrixVocabElement mveModel;

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(MatrixVEV.class);

    /**
     * Constructor.
     *
     * @param mve The matrix vocab element that this view will represent.
     * @param vev The parent view for this vocab element view.
     */
    public MatrixVEV(MatrixVocabElement mve, VocabEditorV vev) {
        super(mve, vev);
        mveModel = mve;
        URL iconURL = getClass().getResource("/icons/m_16.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setTypeIcon(icon);
    }

    /**
     * The action to invoke whent the user types a key.
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
                mveModel.setName(getNameComponent().getText());
            } catch (SystemErrorException se) {
                logger.error("Unable to delete from predicate name", se);
            }

        // The delete key removes digits ahead of the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u007F') {

            // Can't delete empty int datavalue.
            this.getNameComponent().removeAheadOfCaret();
            try {
                mveModel.setName(getNameComponent().getText());
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

                mveModel.setName(cValue.toString());
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
