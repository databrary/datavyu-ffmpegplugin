package au.com.nicta.openshapa.actions;

import java.awt.KeyEventDispatcher;

/**
 * This is a switchboard for incoming keypresses - this routes the key stroke
 * through to the correct action.
 *
 * @author cfreeman
 */
public class KeySwitchBoard implements KeyEventDispatcher {    

    /**
     * @return The single KeySwitchBoard for the OpenSHAPA application.
     */
	public static KeySwitchBoard getKeySwitchBoard() {
		if (singleton == null) {
			singleton = new KeySwitchBoard();
		}
		return singleton;
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
        int moo = 5;
        return true;
    }

    /**
     * Private constructor.
     */
    private KeySwitchBoard() {
    }

    /** The single keyswitchboard for the openshapa application. */
    private static KeySwitchBoard singleton;
}
