package org.openshapa.views.discrete;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;

/**
 * EditorComponent - Abstract class for editing a segment of text within a
 * JTextComponent.
 * Subclasses of this abstract class are combined and used by an
 * EditorTracker to manage editing of the JTextComponent.
 */
public abstract class EditorComponent {

    /** JTextComponent containing this EditorComponent. */
    private JTextComponent textComp;

    /** Character position in the JTextComponent where this editor begins. */
    private int startPos;

    /** Local copy of this editor's text. */
    private String editorText;

    /** Is the editorComponent editable?  Used by EditorTracker. */
    private boolean editable;

    /** Does the editorComponent allow Return characters to be input? */
    private boolean acceptsReturnKey;

    /**
     * Subclass overrides to handle keyPressed events.
     * @param e KeyEvent details.
     */
    public abstract void keyPressed(final KeyEvent e);

    /**
     * Subclass overrides to handle keyTyped events.
     * @param e KeyEvent details.
     */
    public abstract void keyTyped(final KeyEvent e);

    /**
     * Subclass overrides to handle keyReleased events.
     * @param e KeyEvent details.
     */
    public abstract void keyReleased(final KeyEvent e);

    /**
     * Subclass overrides to handle focusLost events.
      * @param fe FocusEvent details.
    */
    public abstract void focusLost(final FocusEvent fe);
    
    /**
     * Default Constructor.
     */
    public EditorComponent() {
        startPos = 0;
        editorText = "";
        editable = false;
        textComp = null;
    }

    /**
     * Constructor.
     * @param tc JTextComponent this editor works with.
     */
    public EditorComponent(JTextComponent tc) {
        this();
        textComp = tc;
    }

    /**
     * Constructor.
     * @param tc JTextComponent this editor works with.
     * @param text text to initialise the editor to.
     */
    public EditorComponent(JTextComponent tc, String text) {
        this(tc);
        editorText = text;
    }

    /**
     * @param canEdit set true if the editorcomponent is editable.
     */
    public final void setEditable(final boolean canEdit) {
        editable = canEdit;
    }

    /**
     * @return is the editorcomponent "editable".
     */
    public final boolean isEditable() {
        return editable;
    }

    /**
     * @param canAccept set true if the editorcomponent uses return character.
     */
    public final void setAcceptReturnKey(final boolean canAccept) {
        acceptsReturnKey = canAccept;
    }

    /**
     * @return is the return character used by this editor.
     */
    public final boolean isReturnKeyAccepted() {
        return acceptsReturnKey;
    }

    /**
     * @param pos the start location in the JTextComponent for this editor.
     */
    public final void setStartPos(final int pos) {
        startPos = pos;
    }

    /**
     * @return the current text of this editor.
     */
    public final String getText() {
        return editorText;
    }

    /**
     * Set the text without updating the associated JTextComponent.
     * @param text new text to set.
     */
    public final void resetText(final String text) {
        editorText = text;
    }

    /**
     * Set the text of the editorcomponent and update the text segment of the
     * JTextComponent.
     * @param text new text to set.
     */
    public final void setText(final String text) {
        int prevlength = editorText.length();
        editorText = text;
        int localPos = getCaretPositionLocal();
        replaceRange(text, startPos, startPos + prevlength);
        setCaretPosition(localPos);
    }

    /**
     * Utility function for replacing a segment of a string with another.
     * @param text new segment of text to set.
     * @param start start position of the new text.
     * @param end length of the segment being replaced.
     */
    private void replaceRange(String text, int start, int end) {
        String fullText = textComp.getText();
        textComp.setText(fullText.substring(0, start) + text
                                                    + fullText.substring(end));
    }

    /**
     * @return the caret location within the text segment as a local value.
     */
    public int getCaretPositionLocal() {
        int pos = Math.max(0, textComp.getCaretPosition() - startPos);
        pos = Math.min(pos, editorText.length());
        return pos;
    }

    /**
     * @return the selection start within the segment as a local value.
     */
    public int getSelectionStartLocal() {
        int pos = Math.max(0, textComp.getSelectionStart() - startPos);
        pos = Math.min(pos, editorText.length());
        return pos;
    }

    /**
     * @return the selection end within the segment as a local value.
     */
    public int getSelectionEndLocal() {
        int pos = Math.max(0, textComp.getSelectionEnd() - startPos);
        pos = Math.min(pos, editorText.length());
        return pos;
    }

    /**
     * Set the caret position of the JTextComponent given a local value.
     */
    public void setCaretPosition(final int localPos) {
        int pos = Math.max(0, localPos);
        pos = Math.min(pos, editorText.length());
        textComp.setCaretPosition(startPos + pos);
    }

    /**
     * Select all of this segments text in the JTextComponent.
     */
    public void selectAll() {
        textComp.select(startPos, startPos + editorText.length());
    }

    /**
     * Given a startClick position and endClick position, select the text in
     * the JTextComponent.
     * @param startClick character position of the start of the click.
     * @param endClick character position of the end of the click.
     */
    public void select(final int startClick, final int endClick) {
        int start = Math.max(startPos, startClick);
        start = Math.min(startPos + editorText.length(), start);
        int end = Math.max(startPos, endClick);
        end = Math.min(startPos + editorText.length(), end);
        if (start < end) {
            textComp.select(start, end);
        } else {
            textComp.select(end, start);
        }
    }
}
