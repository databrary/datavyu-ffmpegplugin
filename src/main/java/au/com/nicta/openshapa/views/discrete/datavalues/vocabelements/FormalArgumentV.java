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
 * This is a view of a Vocab Element Formal Argument.
 *
 * @author cfreeman
 */
public final class FormalArgumentV extends Editor
implements FocusListener, KeyListener {
    /** The underlying model that this view represents. */
    private FormalArgument model;

    /** The parent vocab element view that this argument belongs too. */
    private VocabElementV parentElementV;

    /** The parent editor window that this argument belongs too. */
    private VocabEditorV parentV;

    /** The error logger for this class. */
    private static Logger logger = Logger.getLogger(FormalArgumentV.class);

    /** Is this the initial selection of the formal argument view? */
    private boolean initialSelection;

    /** The position of the model within the parent vocab element. */
    private int argumentPos;

    /**
     * Constructor.
     *
     * @param formalArg The formal argument that this view will represent.
     * @param n The position of the formal argument in the parent element.
     * @param parentElement The parent vocab element for the argument.
     * @param parent The parent vocab editor for the formal argument.
     */
    public FormalArgumentV(final FormalArgument formalArg,
                           final int n,
                           final VocabElementV parentElement,
                           final VocabEditorV parent) {
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
        setText(fargName);
        addFocusListener(this);
        addKeyListener(this);
    }

    /**
     * @return The model of the formal argument that this view represents.
     */
    public FormalArgument getModel() {
        return model;
    }

    /**
     * @return The position of this formal argument within its parent vocab
     * element.
     */
    public int getArgPos() {
        return argumentPos;
    }

    /**
     * The action to invoke if the focus is gained by this component.
     *
     * @param fe The Focus Event that triggered this action.
     */
    public void focusGained(FocusEvent fe) {
        if (initialSelection) {
            selectAll();
            initialSelection = false;
        } else {
            restoreCaretPosition();
        }

        parentV.updateDialogState();
    }

    /**
     * The action to invoke if the focus is lost from this component.
     *
     * @param fe The FocusEvent that triggered this action.
     */
    public void focusLost(FocusEvent fe) {
    }

    /**
     * The action to inovke when a key is typed in this component.
     *
     * @param e The event that triggered this action.
     */
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
        } else if (e.getKeyChar() != '<' && e.getKeyChar() != '>'
                   && e.getKeyChar() != '(' && e.getKeyChar() != ')'
                   && e.getKeyChar() != ',' && e.getKeyChar() != '"') {

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

    /**
     * The action to invoke when a key is pressed within the FormalArgumentV.
     *
     * @param e The event that triggered this action.
     */
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

    /**
     * The action to invoke when a key is released within the FormalArgumentV.
     *
     * @param e The event that triggered this action.
     */
    final public void keyReleased(KeyEvent e) {
        // Ignore key release
    }
}
