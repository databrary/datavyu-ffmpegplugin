package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.db.MatrixVocabElement;
import au.com.nicta.openshapa.views.VocabEditorV;
import java.awt.event.KeyEvent;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author cfreeman
 */
public class MatrixVEV extends VocabElementV {
    private MatrixVocabElement mveModel;

    public MatrixVEV(MatrixVocabElement mve, VocabEditorV vev) {
        super(mve, vev);
        mveModel = mve;
        URL iconURL = getClass().getResource("/icons/m_16.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setTypeIcon(icon);
    }

    public MatrixVEV(MatrixVEV mvev) {
        super(mvev);
    }

    final public void keyTyped(KeyEvent e) {
        //this.veModel.
    }
}
