package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.views.discrete.Editor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 *
 * @author cfreeman
 */
public class VocabElementNameV extends Editor implements FocusListener {

    private boolean initialSelection;

    public VocabElementNameV() {
        this.addFocusListener(this);
        this.initialSelection = true;
    }

    /**
     * The action to invoke if the focus is gained by this DataValueView.
     *
     * @param fe The Focus Event that triggered this action.
     */
    public void focusGained(FocusEvent fe) {
        // Only select all if the data value view is a placeholder.
        if (this.initialSelection) {
            this.selectAll();
            this.initialSelection = false;
        } else {
            this.restoreCaretPosition();
        }
    }

    /**
     * The action to invoke if the focus is lost from this DataValueView.
     *
     * @param fe The FocusEvent that triggered this action.
     */
    public void focusLost(FocusEvent fe) {
    }

}
