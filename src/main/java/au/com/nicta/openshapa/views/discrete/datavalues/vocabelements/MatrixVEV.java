package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.db.MatrixVocabElement;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author cfreeman
 */
public class MatrixVEV extends VocabElementV {
    public MatrixVEV(final MatrixVocabElement pve) {
        super(pve);
        URL iconURL = getClass().getResource("/icons/m_16.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setTypeIcon(icon);
    }

    public MatrixVEV(MatrixVEV mvev) {
        super(mvev);
    }

    @Override
    public Object clone() {
        return new MatrixVEV(this);
    }
}
