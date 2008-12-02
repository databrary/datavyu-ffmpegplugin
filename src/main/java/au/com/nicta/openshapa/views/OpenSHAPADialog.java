package au.com.nicta.openshapa.views;

import au.com.nicta.openshapa.actions.KeySwitchBoard;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import javax.swing.JDialog;

/**
 * Generic OpenSHAPA dialog - handles work common to all dialogs (handling of
 * keystrokes, etc).
 *
 * @author cfreeman
 */
public abstract class OpenSHAPADialog extends JDialog
implements KeyEventDispatcher {

    /**
     * Constructor. Creates a new OpenSHAPADialog.
     *
     * @param parent The parent of this form.
     * @param modal Should the dialog be modal or not?
     */
    public OpenSHAPADialog(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        KeyboardFocusManager key = KeyboardFocusManager
                                   .getCurrentKeyboardFocusManager();
        key.addKeyEventDispatcher(this);
    }

    /**
     * Dispatches the keystroke to the correct action.
     *
     * @param evt The event that triggered this action.
     *
     * @return true if the KeyboardFocusManager should take no further action
     * with regard to the KeyEvent; false  otherwise
     */
    @Override
    public boolean dispatchKeyEvent(java.awt.event.KeyEvent evt) {
        // Pass the keyevent onto the keyswitchboard so that it can route it
        // to the correct action.
        return KeySwitchBoard.getKeySwitchBoard().dispatchKeyEvent(evt);
    }
}
