package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.db.PredicateVocabElement;
import au.com.nicta.openshapa.db.SystemErrorException;
import java.awt.event.KeyEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import org.apache.log4j.Logger;

/**
 *
 * @author cfreeman
 */
public class PredicateVEV extends VocabElementV {
    private PredicateVocabElement pveModel;

    private static Logger logger = Logger.getLogger(PredicateVEV.class);

    public PredicateVEV(final PredicateVocabElement pve) {
        super(pve);
        pveModel = pve;
        URL iconURL = getClass().getResource("/icons/p_16.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setTypeIcon(icon);
    }

    public PredicateVEV(PredicateVEV pvev) {
        super(pvev);
    }

    final public void keyTyped(KeyEvent e) {

        // If the character is not reserved - add it to the name of the pred
        if (e.getKeyChar() != '<' && e.getKeyChar() != '>' &&
            e.getKeyChar() != '(' && e.getKeyChar() != ')' &&
            e.getKeyChar() != ',' && e.getKeyChar() != '"') {

            JTextField field = getNameComonent();
            StringBuffer cValue = new StringBuffer(field.getText());
            cValue.insert(field.getCaretPosition(), e.getKeyChar());

            try {
                pveModel.setName(cValue.toString());
                this.setHasChanged(true);
                this.rebuildContents();
            } catch (SystemErrorException se) {
                logger.error("Unable to set new predicate name", se);
            }
        }

        e.consume();
    }
}
