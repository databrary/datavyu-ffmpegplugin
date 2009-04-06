package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.db.FormalArgument;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.views.VocabEditorV;
import au.com.nicta.openshapa.views.discrete.Editor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.apache.log4j.Logger;

/**
 *
 * @author cfreeman
 */
public final class FormalArgumentV extends Editor
implements FocusListener, KeyListener {
    private FormalArgument model;

    private VocabElementV parentElementV;

    private VocabEditorV parentV;

    private static Logger logger = Logger.getLogger(FormalArgumentV.class);

    private boolean initialSelection;

    private int argumentPos;

    public FormalArgumentV(FormalArgument formalArg,
                           int n,
                           VocabElementV parentElement,
                           VocabEditorV parent) {
        super();
        model = formalArg;
        parentV = parent;
        parentElementV = parentElement;
        initialSelection = true;
        argumentPos = n;

        this.setBorder(null);

        // Formal argument name contains "<" and ">" characters which we don't
        // actually want.
        String fargName = model.getFargName()
                               .substring(1, model.getFargName().length() - 1);
        this.setText(fargName);
        this.addFocusListener(this);
        this.addKeyListener(this);
    }

    public FormalArgument getModel() {
        return model;
    }

    public int getArgPos() {
        return argumentPos;
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

        this.parentV.updateDialogState();
    }

    /**
     * The action to invoke if the focus is lost from this component.
     *
     * @param fe The FocusEvent that triggered this action.
     */
    public void focusLost(FocusEvent fe) {
    }

    final public void keyTyped(KeyEvent e) {
                // The backspace key removes digits from behind the caret.
        if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
            && e.getKeyChar() == '\u0008') {

            // Can't delete empty int datavalue.
            removeBehindCaret();
            try {
                model.setFargName("<" + getText() + ">");
            } catch (SystemErrorException se) {
                logger.error("Unable to delete from predicate name", se);
            }

        // The delete key removes digits ahead of the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u007F') {

            // Can't delete empty int datavalue.
            removeAheadOfCaret();
            try {
                model.setFargName("<" + getText() + ">");
            } catch (SystemErrorException se) {
                logger.error("Unable to delete from predicate name", se);
            }

        // If the character is not reserved - add it to the name of the pred
        } else if (e.getKeyChar() != '<' && e.getKeyChar() != '>' &&
                   e.getKeyChar() != '(' && e.getKeyChar() != ')' &&
                   e.getKeyChar() != ',' && e.getKeyChar() != '"') {

            try {
                removeSelectedText();

                StringBuffer cValue = new StringBuffer(getText());
                cValue.insert(getCaretPosition(), e.getKeyChar());

                model.setFargName("<" + cValue.toString() + ">");
                advanceCaret();
            } catch (SystemErrorException se) {
                logger.error("Unable to set new predicate name", se);
            }
        }

        parentElementV.setHasChanged(true);
        parentElementV.rebuildContents();
        e.consume();
    }

    final public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                // Ignore - handled when the key is typed.
                e.consume();
                break;
            default:
                break;
        }
    }

    final public void keyReleased(KeyEvent e) {
        // Ignore key release
    }
}
