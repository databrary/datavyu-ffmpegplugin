package org.openshapa.views.discrete;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import javax.swing.text.JTextComponent;
import org.openshapa.views.discrete.datavalues.NoEditor;

/**
 * Keeps track of the current editor inside the JTextComponent
 */
public class EditorTracker
implements FocusListener, KeyListener, MouseListener {

    /** The JTextComponent that contains the EditorComponents. */
    private JTextComponent textArea;

    /** Vector of the EditorComponents. */
    private Vector<EditorComponent> editors;

    /** Current EditorComponent. */
    private EditorComponent currentEditor = noEditor;

    /** Number of characters before of the current editor. */
    private int preCharCount;

    /** Number of characters after the current editor. */
    private int postCharCount;

    /** Is the mouse down? */
    private boolean mouseDown = false;

    /** NoEditor used when there is no sensible current editor. */
    private static final EditorComponent noEditor = new NoEditor();
    
    /** Track the key up and down to avoid problem with key repeat. */
    private boolean gotKeyUp = true;

    /**
     * Constructor
     * @param ta JTextComponent containing the editors to track.
     * @param eds Vector of the EditorComponents.
     */
    public EditorTracker(JTextComponent ta, Vector<EditorComponent> eds) {
        textArea = ta;
        editors = eds;
    }

    /**
     * Set the currentEditor.
     * @param newEd The new editor to set as the current.
     */
    private void setEditor(EditorComponent newEd) {
        setEditor(newEd, 0, Integer.MAX_VALUE);
    }

    /**
     * Set the currentEditor.
     * @param newEd The new editor to set as the current.
     * @param start Start character location to select.
     * @param end End character location to select.
     */
    private void setEditor(EditorComponent newEd, int start, int end) {
        // Tell currentEditor to store its value back in the database
        currentEditor.focusLost(null);

        // change currentEditor
        currentEditor = newEd;
        // now calculate the pre and post char counts
        boolean foundEd = false;
        preCharCount = 0;
        postCharCount = 0;
        if (newEd != noEditor) {
            for (EditorComponent ed : editors) {
                if (ed == newEd) {
                    foundEd = true;
                }
                if (!foundEd) {
                    preCharCount += ed.getText().length();
                } else if (ed != newEd) {
                    postCharCount += ed.getText().length();
                }
            }
        }
        currentEditor.setStartPos(preCharCount);
        currentEditor.select(start, end);
    }

    /**
     * Find an editor given a character position of a mouse click.
     * @param charPos Character position to search for
     * @return The editor closest to the character position.
     */
    private EditorComponent findEditor(int charPos) {
        // iterate over the editors and decide which one should get focus
        // based on a click at the charPos
        int preCount = 0;
        boolean foundCount = false;
        EditorComponent foundEd = noEditor;
        for (EditorComponent ed : editors) {
            preCount += ed.getText().length();
            if (ed.isEditable()) {
                foundEd = ed;
            }
            if (preCount >= charPos) {
                foundCount = true;
            }
            if (foundCount && ed.isEditable()) {
                break;
            }
        }
        return foundEd;
    }

    /**
     * Set the current editor to the first editor.
     */
    public void firstEditor() {
        EditorComponent foundEd = noEditor;
        for (EditorComponent ed : editors) {
            if (ed.isEditable()) {
                foundEd = ed;
                break;
            }
        }
        setEditor(foundEd);
    }

    /**
     * Set the current editor to the editor after the current editor.
     */
    public void nextEditor() {
        EditorComponent foundEd = noEditor;
        boolean currFound = false;
        for (EditorComponent ed : editors) {
            if (ed == currentEditor) {
                currFound = true;
            } else if (currFound && ed.isEditable()) {
                foundEd = ed;
                break;
            }
        }
        setEditor(foundEd);
    }

    /**
     * Set the current editor to the editor before the current editor.
     */
    public void prevEditor() {
        EditorComponent prevEd = noEditor;
        for (EditorComponent ed : editors) {
            if (ed == currentEditor) {
                break;
            }
            if (ed.isEditable()) {
                prevEd = ed;
            }
        }
        setEditor(prevEd);
    }

    /**
     * Set the current editor to the last editor.
     */
    public void lastEditor() {
        EditorComponent prevEd = noEditor;
        for (EditorComponent ed : editors) {
            if (ed.isEditable()) {
                prevEd = ed;
            }
        }
        setEditor(prevEd);
    }

    /**
     * Focus has been gained by the JTextComponent this tracker watches.
     *
     * @param fe The FocusEvent that triggered this action.
     */
    public void focusGained(final FocusEvent fe) {
        if (!mouseDown) {
            EditorComponent ed = findEditor(0);
            this.setEditor(ed, 0, 0);
        }
    }

    /**
     * Focus has been lost by the JTextComponent this tracker watches.
     *
     * @param fe The FocusEvent that triggered this action.
     */
    public void focusLost(final FocusEvent fe) {
        currentEditor.focusLost(fe);
        currentEditor = noEditor;
    }

    /**
     * The action to invoke when a key is released.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyReleased(final KeyEvent e) {
        gotKeyUp = true;
        resetEditorText();
        currentEditor.keyReleased(e);
    }

    /**
     * The action to invoke when a key is typed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyTyped(final KeyEvent e) {
        currentEditor.keyTyped(e);
    }

    /**
     * The action to invoke when a key is pressed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyPressed(final KeyEvent e) {

        switch (e.getKeyCode()) {

            case KeyEvent.VK_BACK_SPACE:
                if (!gotKeyUp) {
                    resetEditorText();
                }
                if (currentEditor.getCaretPositionLocal() == 0) {
                    e.consume();
                }
                break;

            case KeyEvent.VK_DELETE:
                if (!gotKeyUp) {
                    resetEditorText();
                }
                if (currentEditor.getCaretPositionLocal()
                        == currentEditor.getText().length()
                    && currentEditor.getSelectionStartLocal()
                        == currentEditor.getSelectionEndLocal()) {
                    e.consume();
                }
                break;

            case KeyEvent.VK_LEFT:
                if (currentEditor.getCaretPositionLocal() == 0) {
                    prevEditor();
                    if (currentEditor == noEditor) {
                        // should jump to the cell above if there is one
                        firstEditor(); // hack for now recycles to first
                    }
                    e.consume();
                }
                break;

            case KeyEvent.VK_RIGHT:
                if (currentEditor.getCaretPositionLocal()
                        == currentEditor.getText().length()) {
                    nextEditor();
                    if (currentEditor == noEditor) {
                        // should jump to the cell below if there is one
                        lastEditor(); // hack for now recycles to last
                    }
                    e.consume();
                }
                break;

            case KeyEvent.VK_ENTER:
                if (!currentEditor.isReturnKeyAccepted()) {
                    // help out the editors that don't want the return key
                    if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_STANDARD) {
                        e.consume();
                    }
                }
                break;
            case KeyEvent.VK_TAB:
                if ((e.getModifiers() & InputEvent.SHIFT_MASK) > 0) {
                    prevEditor();
                    if (currentEditor == noEditor) {
                        lastEditor();
                    }
                } else {
                    nextEditor();
                    if (currentEditor == noEditor) {
                        firstEditor();
                    }
                }
                e.consume();
                break;
        }

        if (!e.isConsumed()) {
            currentEditor.keyPressed(e);
        }
	gotKeyUp = false;
    }

    /**
     * Calculate the currentEditor's text and call it's resetText method.
     */
    public void resetEditorText() {
        int newLength = textArea.getText().length() - (preCharCount + postCharCount);
        currentEditor.resetText(textArea.getText().substring(preCharCount, preCharCount + newLength));
    }


    /**
     * The action to invoke when a mouse button is pressed.
     *
     * @param me The mouse event that triggered this action.
     */
    public final void mousePressed(final MouseEvent me) {
        mouseDown = true;
    }

    /**
     * The action to invoke when a mouse button is released.
     *
     * @param me The mouse event that triggered this action.
     */
    public final void mouseReleased(final MouseEvent me) {
        mouseDown = false;
        int start = textArea.getCaret().getMark();
        int end = textArea.getCaret().getDot();
        EditorComponent ed = findEditor(start);
        this.setEditor(ed, start, end);
    }

    /**
     * The action to invoke when a mouse button is clicked.
     *
     * @param me The mouse event that triggered this action.
     */
    public final void mouseClicked(final MouseEvent me) {
        if (me.getClickCount() == 3) {
            // Triple click selects all text (feature of JTextArea).
            // detect and override behaviour
            currentEditor.selectAll();
        }
    }

    /**
     * The action to invoke when the mouse enters this component.
     *
     * @param me The mouse event that triggered this action.
     */
    public final void mouseEntered(final MouseEvent me) {
        // Currently we do nothing with the mouse entered event.
    }

    /**
     * The action to invoke when the mouse exits this component.
     *
     * @param me The mouse event that triggered this action.
     */
    public final void mouseExited(final MouseEvent me) {
        // Currently we do nothing with the mouse exited event.
    }
}
