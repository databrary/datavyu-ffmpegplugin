package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.db.PredicateVocabElement;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author cfreeman
 */
public class PredicateVEV extends VocabElementV {
    public PredicateVEV(final PredicateVocabElement pve) {
        super(pve);
        URL iconURL = getClass().getResource("/icons/p_16.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setTypeIcon(icon);
    }

    public PredicateVEV(PredicateVEV pvev) {
        super(pvev);
    }

    @Override
    public Object clone() {
        return new PredicateVEV(this);
    }
}
