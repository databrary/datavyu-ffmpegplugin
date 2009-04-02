package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.db.PredicateVocabElement;
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
public class PredicateVEV extends VocabElementV {
    private PredicateVocabElement pveModel;

    private static Logger logger = Logger.getLogger(PredicateVEV.class);

    public PredicateVEV(PredicateVocabElement pve, VocabEditorV vev) {
        super(pve, vev);
        pveModel = pve;
        URL iconURL = getClass().getResource("/icons/p_16.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setTypeIcon(icon);
    }

    public PredicateVEV(PredicateVEV pvev) {
        super(pvev);
    }

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
            setHasChanged(true);
            rebuildContents();


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
            
                pveModel.setName(cValue.toString());
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
