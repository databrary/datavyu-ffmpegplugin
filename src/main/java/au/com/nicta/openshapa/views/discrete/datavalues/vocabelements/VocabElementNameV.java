package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.views.VocabEditorV;
import au.com.nicta.openshapa.views.discrete.Editor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * A view element for a vocab element name (i.e. the 'GOAL' of GOAL().
 *=
 * @author cfreeman
 */
public class VocabElementNameV extends Editor implements FocusListener {

    /** The parent vocab editor that this view resides within. */
    private VocabEditorV parent;

    /** The initial selection of the vocab element name, true yes. false no */
    private boolean initialSelection;

    /**
     * Contructor.
     *
     * @param p The parent vocab editor that this view resides within.
     */
    public VocabElementNameV(final VocabEditorV p) {
        this.addFocusListener(this);
        this.parent = p;
        this.initialSelection = true;
    }

    /**
     * The action to invoke if the focus is gained by this component.
     *
     * @param fe The Focus Event that triggered this action.
     */
    public void focusGained(FocusEvent fe) {
        if (this.initialSelection) {
            this.selectAll();
            this.initialSelection = false;
        } else {
            this.restoreCaretPosition();
        }

        this.parent.updateDialogState();
    }

    /**
     * The action to invoke if the focus is lost from this component.
     *
     * @param fe The FocusEvent that triggered this action.
     */
    public void focusLost(FocusEvent fe) {
    }
}
