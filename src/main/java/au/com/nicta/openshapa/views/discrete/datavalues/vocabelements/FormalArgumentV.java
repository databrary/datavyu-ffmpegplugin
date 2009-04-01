package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.db.FormalArgument;
import javax.swing.JTextField;

/**
 *
 * @author cfreeman
 */
public class FormalArgumentV extends JTextField {
    FormalArgument model;

    public FormalArgumentV(FormalArgument formalArg) {
        model = formalArg;

        this.setBorder(null);
        this.setText(model.getFargName());
    }
}
