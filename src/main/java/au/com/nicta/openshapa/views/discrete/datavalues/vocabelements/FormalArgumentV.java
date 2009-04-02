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

        // Formal argument name contains "<" and ">" characters which we don't
        // actually want.
        String fargName = model.getFargName()
                               .substring(1, model.getFargName().length() - 1);
        this.setText(fargName);
    }
}
