package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.db.MatrixVocabElement;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.views.VocabEditorV;
import au.com.nicta.openshapa.views.discrete.Editor;
import java.awt.event.KeyEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;

/**
 *
 * @author cfreeman
 */
public class MatrixVEV extends VocabElementV {
    private MatrixVocabElement mveModel;

    private static Logger logger = Logger.getLogger(MatrixVEV.class);

    public MatrixVEV(MatrixVocabElement mve, VocabEditorV vev) {
        super(mve, vev);
        mveModel = mve;
        URL iconURL = getClass().getResource("/icons/m_16.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setTypeIcon(icon);
    }

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
            setHasChanged(true);
            rebuildContents();


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
            setHasChanged(true);
            rebuildContents();

        // If the character is not reserved - add it to the name of the pred
        } else if (e.getKeyChar() != '<' && e.getKeyChar() != '>' &&
                   e.getKeyChar() != '(' && e.getKeyChar() != ')' &&
                   e.getKeyChar() != ',' && e.getKeyChar() != '"') {

            try {
                Editor field = getNameComponent();
                field.removeSelectedText();

                StringBuffer cValue = new StringBuffer(field.getText());
                cValue.insert(field.getCaretPosition(), e.getKeyChar());

                mveModel.setName(cValue.toString());
                field.advanceCaret();
                setHasChanged(true);
                rebuildContents();
            } catch (SystemErrorException se) {
                logger.error("Unable to set new predicate name", se);
            }
        }

        e.consume();
    }
}
