package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.db.FormalArgument;
import au.com.nicta.openshapa.views.VocabEditorV;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;

/**
 *
 * @author cfreeman
 */
public class FormalArgumentV extends JTextField implements FocusListener  {
    FormalArgument model;

    VocabEditorV parentV;

    public FormalArgumentV(FormalArgument formalArg, VocabEditorV parent) {
        model = formalArg;
        parentV = parent;

        this.setBorder(null);

        // Formal argument name contains "<" and ">" characters which we don't
        // actually want.
        String fargName = model.getFargName()
                               .substring(1, model.getFargName().length() - 1);
        this.setText(fargName);
        this.addFocusListener(this);
    }

    /**
     * The action to invoke if the focus is gained by this component.
     *
     * @param fe The Focus Event that triggered this action.
     */
    public void focusGained(FocusEvent fe) {
        this.parentV.updateDialogState();
    }

    /**
     * The action to invoke if the focus is lost from this component.
     *
     * @param fe The FocusEvent that triggered this action.
     */
    public void focusLost(FocusEvent fe) {
    }
}
