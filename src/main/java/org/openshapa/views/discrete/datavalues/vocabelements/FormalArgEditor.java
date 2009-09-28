package org.openshapa.views.discrete.datavalues.vocabelements;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.FormalArgument;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.VocabElement;
import org.openshapa.views.discrete.EditorComponent;

/**
 * This class is the character editor of a NominalDataValue.
 */
public final class FormalArgEditor extends EditorComponent {

    /** Parent Vocab Element. */
    private VocabElement vocabElement;

    /** Index of the formal arg. */
    private int argIndex;

    /** Model this editor represents. */
    private FormalArgument model = null;

    /** Text when editor gained focus (became current editor). */
    private String textOnFocus;

    /** true if the editor has the focus. */
    private boolean edHasFocus;

    /** String holding the reserved characters. */
    private static final String RESERVED_CHARS = ")(<>|,;\t\r\n";

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(FormalArgEditor.class);

    /** The parent editor window that this argument belongs too. */
    private VocabElementV parentView;

    public FormalArgEditor(final JTextComponent ta,
                           final VocabElement ve,
                           final int index,
                           final VocabElementV pv) {
        super(ta);
        setEditable(true);
        argIndex = index;
        parentView = pv;
        resetValue(ve);
    }

    public void resetValue(final VocabElement ve) {
        vocabElement = ve;

        String fargName = "";
        try {
            model = vocabElement.getFormalArg(argIndex);
        } catch (SystemErrorException se) {
            logger.error("Unable to resetValue", se);
        }

        // Formal argument name contains "<" and ">" characters which we don't
        // actually want.
        if (model != null) {
            fargName = model.getFargName()
                            .substring(1, model.getFargName().length() - 1);
        }
        setText(fargName);
    }

    /**
     * @return the model.
     */
    public FormalArgument getModel() {
        return model;
    }

    /**
     * @return the argument index.
     */
    public int getArgPos() {
        return argIndex;
    }

    /**
     * @param aChar Character to test
     * @return true if the character is a reserved character.
     */
    public boolean isReserved(final char aChar) {
        return (RESERVED_CHARS.indexOf(aChar) >= 0);
    }

    /**
     * focusSet is the signal that this editor has become "current".
     * @param fe Focus Event
     */
    @Override
    public void focusGained(final FocusEvent fe) {
        textOnFocus = getText();
        edHasFocus = true;
        parentView.getParentDialog().updateDialogState();
    }

    /**
     * Action to take when focus is lost for this editor.
     * @param fe Focus Event
     */
    @Override
    public void focusLost(final FocusEvent fe) {
        edHasFocus = false;
        if (!getText().equals(textOnFocus)) {
            updateDatabase();
        }
    }

    /**
     * @return true if this editor has the focus.
     */
    public boolean hasFocus() {
        return this.getParentComponent().hasFocus();
    }

    /**
     * The action to invoke when a key is typed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {

        // The backspace key removes digits from behind the caret.
        if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
            && e.getKeyChar() == '\u0008') {

            removeBehindCaret();
            try {
                model.setFargName("<" + getText() + ">");
            } catch (SystemErrorException se) {
                logger.error("Unable to delete from predicate name", se);
            }

        // The delete key removes digits ahead of the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u007F') {

            removeAheadOfCaret();
            try {
                model.setFargName("<" + getText() + ">");
            } catch (SystemErrorException se) {
                logger.error("Unable to delete from predicate name", se);
            }

        // If the character is not reserved - add it to the name of the pred
        } else if (!this.isReserved(e.getKeyChar())) {

            try {
                removeSelectedText();

                StringBuffer currentValue = new StringBuffer(getText());
                currentValue.insert(getCaretPosition(), e.getKeyChar());
                model.setFargName("<" + currentValue.toString() + ">");

                // Advance caret over the top of the new char.
                int pos = this.getCaretPosition() + 1;
                this.setText(currentValue.toString());
                this.setCaretPosition(pos);

            } catch (SystemErrorException se) {
                logger.error("Unable to set new predicate name", se);
            }
        }

        parentView.setHasChanged(true);
        //parentElementV.rebuildContents();
        e.consume();

        /*
        if (!e.isConsumed()) {
            if (isReserved(e.getKeyChar())) {
                // Ignore reserved characters.
                e.consume();
            }
        }*/
    }

    /**
     * Action to take by this editor when a key is pressed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
    }

    /**
     * Action to take by this editor when a key is released.
     * @param e KeyEvent
     */
    @Override
    public void keyReleased(final KeyEvent e) {
        if (!getText().equals(textOnFocus)) {
            parentView.setHasChanged(true);
            parentView.getParentDialog().updateDialogState();
        }
    }

    /**
     * Update the database with the model value.
     */
    public void updateDatabase() {
        // update the model.
        System.out.println("FormalArgEditor updatedatabase called");
    }
}