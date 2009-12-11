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
 * Keeps track of the current editor inside the JTextComponent.
 * Editors and FixedTexts are held in a vector that creates the overall
 * text displayed by the JTextComponent.
 */
public final class EditorTracker
implements FocusListener, KeyListener, MouseListener {

    /** The JTextComponent that contains the EditorComponents. */
    private JTextComponent textArea;
    /** Vector of the EditorComponents. */
    private Vector<EditorComponent> editors;
    /** Current EditorComponent. */
    private EditorComponent currentEditor = NO_EDITOR;
    /** Number of characters before of the current editor. */
    private int preCharCount;
    /** Number of characters after the current editor. */
    private int postCharCount;
    /** NoEditor used when there is no sensible current editor. */
    private static final EditorComponent NO_EDITOR = new NoEditor();
    /** Track the key up and down to avoid problem with key repeat. */
    private boolean gotKeyUp = true;
    /** How many clicks make up a triple click. */
    private static final int TRIPLE_CLICK_COUNT = 3;

    /**
     * Constructor.
     *
     * @param ta JTextComponent containing the editors to track.
     * @param eds Vector of the EditorComponents.
     */
    public EditorTracker(final JTextComponent ta,
                         final Vector<EditorComponent> eds) {
        textArea = ta;
        editors = eds;
    }

    /**
     * Set the currentEditor.
     *
     * @param newEd The new editor to set as the current.
     */
    public void setEditor(final EditorComponent newEd) {
        setEditor(newEd, 0, Integer.MAX_VALUE);
    }

    /**
     * Set the currentEditor.
     *
     * @param newEd The new editor to set as the current.
     * @param start Start character location to select.
     * @param end End character location to select.
     */
    public void setEditor(final EditorComponent newEd,
                          final int start,
                          final int end) {
        // Tell currentEditor to store its value back in the database
        currentEditor.focusLost(null);

        // change currentEditor
        currentEditor = newEd;

        // now calculate the pre and post char counts
        calculatePrePostCounts();

        currentEditor.focusGained(null);

        currentEditor.select(start, end);
    }

    /**
     * Calculate the preCharCount and postCharCount values.
     */
    private void calculatePrePostCounts() {
        boolean foundEd = false;
        preCharCount = 0;
        postCharCount = 0;
        if (currentEditor != NO_EDITOR) {
            for (EditorComponent ed : editors) {
                if (ed.equals(currentEditor)) {
                    foundEd = true;
                }
                if (!foundEd) {
                    preCharCount += ed.getText().length();
                } else if (!ed.equals(currentEditor)) {
                    postCharCount += ed.getText().length();
                }
            }
        }
        // Update the currentEditor with the new start position.
        currentEditor.setStartPos(preCharCount);
    }

    /**
     * Adds editors to the tracker on the end of the vector.
     *
     * @param eds The editors to add.
     */
    public void addEditors(final Vector<EditorComponent> eds) {
        if (editors.addAll(eds)) {
            calculatePrePostCounts();
        }
    }

    /**
     * Removes editors from the tracker.
     *
     * @param eds The editors to remove.
     */
    public void removeEditors(final Vector<EditorComponent> eds) {
        if (editors.removeAll(eds)) {
            calculatePrePostCounts();
        }
    }

    /**
     * Find an editor given a character position of a mouse click.
     *
     * @param charPos Character position to search for
     * @return The editor closest to the character position.
     */
    public EditorComponent findEditor(final int charPos) {
        // iterate over the editors and decide which one should get focus
        // based on a click at the charPos
        int preCount = 0;
        boolean foundCount = false;
        EditorComponent foundEd = NO_EDITOR;
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
     * @return the first editor if found or NO_EDITOR
     */
    public EditorComponent firstEditor() {
        EditorComponent foundEd = NO_EDITOR;
        for (EditorComponent ed : editors) {
            if (ed.isEditable()) {
                foundEd = ed;
                break;
            }
        }
        return foundEd;
    }

    /**
     * @return the next editor to the currentEditor if found or NO_EDITOR.
     */
    public EditorComponent nextEditor() {
        EditorComponent foundEd = NO_EDITOR;
        boolean currFound = false;
        for (EditorComponent ed : editors) {
            if (ed.equals(currentEditor)) {
                currFound = true;
            } else if (currFound && ed.isEditable()) {
                foundEd = ed;
                break;
            }
        }
        return foundEd;
    }

    /**
     * @return the previous editor to the currentEditor if found or NO_EDITOR.
     */
    public EditorComponent prevEditor() {
        EditorComponent prevEd = NO_EDITOR;
        for (EditorComponent ed : editors) {
            if (ed.equals(currentEditor)) {
                break;
            }
            if (ed.isEditable()) {
                prevEd = ed;
            }
        }
        return prevEd;
    }

    /**
     * @return the last editor if found or NO_EDITOR.
     */
    public EditorComponent lastEditor() {
        EditorComponent prevEd = NO_EDITOR;
        for (EditorComponent ed : editors) {
            if (ed.isEditable()) {
                prevEd = ed;
            }
        }
        return prevEd;
    }

    /**
     * Focus has been gained by the JTextComponent this tracker watches.
     *
     * @param fe The FocusEvent that triggered this action.
     */
    public void focusGained(final FocusEvent fe) {
        if (currentEditor.equals(NO_EDITOR)) {
            setEditor(findEditor(0), 0, 0);
        } else {
            setEditor(currentEditor,
                      currentEditor.getCaretPosition(),
                      currentEditor.getCaretPosition());
        }
    }

    /**
     * Focus has been lost by the JTextComponent this tracker watches.
     *
     * @param fe The FocusEvent that triggered this action.
     */
    public void focusLost(final FocusEvent fe) {
        currentEditor.focusLost(fe);
        currentEditor = NO_EDITOR;
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
    public void keyTyped(KeyEvent e) {
        currentEditor.keyTyped(e);
    }

    /**
     * The action to invoke when a key is pressed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {

            case KeyEvent.VK_BACK_SPACE:
                if (!gotKeyUp) {
                    resetEditorText();
                }
                if (currentEditor.getCaretPosition() == 0 && currentEditor.getSelectionStart() == currentEditor.getSelectionEnd()) {
                    e.consume();
                }
                break;

            case KeyEvent.VK_DELETE:
                if (!gotKeyUp) {
                    resetEditorText();
                }
                if (currentEditor.getCaretPosition() == currentEditor.getText().length() && currentEditor.getSelectionStart() == currentEditor.getSelectionEnd()) {
                    e.consume();
                }
                break;

            case KeyEvent.VK_LEFT:
                if ((currentEditor.getCaretPosition() == 0) || (currentEditor.getSelectionEnd() - currentEditor.getSelectionStart() == currentEditor.getText().length())) {
                    setEditor(prevEditor(), Integer.MAX_VALUE,
                            Integer.MAX_VALUE);
                    if (currentEditor == NO_EDITOR) {
                        // should jump to the cell above if there is one
                        // for now leaves caret at start of first editor
                        setEditor(firstEditor(), 0, 0);
                    }
                    e.consume();
                }
                break;

            case KeyEvent.VK_RIGHT:
                if (currentEditor.getCaretPosition() == currentEditor.getText().length()) {
                    setEditor(nextEditor(), 0, 0);
                    if (currentEditor == NO_EDITOR) {
                        // should jump to the cell below if there is one
                        setEditor(lastEditor(), Integer.MAX_VALUE,
                                Integer.MAX_VALUE);
                    }
                    e.consume();
                }
                break;

            case KeyEvent.VK_HOME:
            case KeyEvent.VK_PAGE_UP:
                setEditor(firstEditor(), 0, 0);
                e.consume();
                break;

            case KeyEvent.VK_END:
            case KeyEvent.VK_PAGE_DOWN:
                setEditor(lastEditor(), Integer.MAX_VALUE, Integer.MAX_VALUE);
                e.consume();
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
                if (currentEditor.getSelectionEnd() - currentEditor.getSelectionStart() != currentEditor.getText().length()) {
                    setEditor(nextEditor());
                } else if ((e.getModifiers() & InputEvent.SHIFT_MASK) > 0) {
                    setEditor(prevEditor());
                    if (currentEditor == NO_EDITOR) {
                        setEditor(lastEditor());
                    }
                } else {
                    setEditor(nextEditor());
                    if (currentEditor == NO_EDITOR) {
                        setEditor(firstEditor());
                    }
                }
                e.consume();
                break;

            default:
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
        int newLength = textArea.getText().length()
                        - (preCharCount + postCharCount);
        currentEditor.resetText(textArea.getText()
                     .substring(preCharCount, preCharCount + newLength));
    }

    /**
     * The action to invoke when a mouse button is pressed.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mousePressed(final MouseEvent me) {
        me.consume();

        // BugzID:629 - Prevent users from selecting place holders.
        if (!currentEditor.canSubSelect()) {
            currentEditor.selectAll();
        }
    }

    /**
     * The action to invoke when a mouse button is released.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseReleased(final MouseEvent me) {
        me.consume();

        // BugzID:629 - Prevent users from selecting place holders.
        if (!currentEditor.canSubSelect()) {
            currentEditor.selectAll();
        }
    }

    /**
     * The action to invoke when a mouse button is clicked.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseClicked(final MouseEvent me) {
        if (me.getClickCount() == TRIPLE_CLICK_COUNT) {
            // Triple click selects all text (feature of JTextArea).
            // detect and override behaviour
            currentEditor.selectAll();

        } else {
            int start = textArea.getCaret().getMark();
            int end = textArea.getCaret().getDot();
            EditorComponent ed = findEditor(start);
            this.setEditor(ed, start, end);
        }

        me.consume();
    }

    /**
     * The action to invoke when the mouse enters this component.
     *tEdi
     * @param me The mouse event that triggered this action.
     */
    public void mouseEntered(final MouseEvent me) {
        // Currently we do nothing with the mouse entered event.
    }

    /**
     * The action to invoke when the mouse exits this component.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseExited(final MouseEvent me) {
        // Currently we do nothing with the mouse exited event.
    }

    /**
     * Call the currentEditor to sanitize the text in the clipboard.
     */
    public void paste() {
        currentEditor.paste();
    }

    /**
     * Cut text from the current editor.
     */
    public void cut() {
        currentEditor.cut();
    }

    /**
     * @return The current editor for this editor tracker.
     */
    public EditorComponent getCurrentEditor() {
        return this.currentEditor;
    }
}
