package org.openshapa.views.discrete.datavalues;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import org.openshapa.views.discrete.EditorComponent;

/**
 * NoEditor is used in EditorTracker when there is no sensible current Editor.
 */
public final class NoEditor extends EditorComponent {

    /**
     * Constructor.
     */
    public NoEditor() {
    }

    /**
     * Subclass overrides to handle keyPressed events.
     * @param e KeyEvent details.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
    }

    /**
     * Subclass overrides to handle keyTyped events.
     * @param e KeyEvent details.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
    }

    /**
     * Subclass overrides to handle keyReleased events.
     * @param e KeyEvent details.
     */
    @Override
    public void keyReleased(final KeyEvent e) {
    }

    /**
     * Subclass overrides to handle focusSet state.
     * @param fe FocusEvent details.
     */
    @Override
    public void focusGained(final FocusEvent fe) {
    }

    /**
     * Subclass overrides to handle focusLost events.
     * @param fe FocusEvent details.
     */
    @Override
    public void focusLost(final FocusEvent fe) {
    }
}