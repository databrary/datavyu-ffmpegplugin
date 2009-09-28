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
    private VocabElementV parentV;

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public FormalArgEditor(final JTextComponent ta, final VocabElement ve,
                                final int index, final VocabElementV pv) {
        super(ta);
        setEditable(true);
        argIndex = index;
        parentV = pv;
        resetValue(ve);
    }

    /**
     * Reset the values by retrieving from the database.
     * @param cell The Parent cell that holds the matrix.
     * @param matrix The parent matrix that holds the DataValue.
     *
     * Changes: Replace call to vocabElement.getFormalArg() with call
     *          to vocabElement.getFormalArgCopy().
     *                                              9/15/09
     */
    public final void resetValue(final VocabElement ve) {
        vocabElement = ve;

        String fargName = "";
        try {
            model = vocabElement.getFormalArgCopy(argIndex);
        } catch (SystemErrorException e) {

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
     * The action to invoke when a key is typed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        if (!e.isConsumed()) {

            if (isReserved(e.getKeyChar())) {
                // Ignore reserved characters.
                e.consume();
            }
        }
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
        parentV.getParentDialog().updateDialogState();
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
        return edHasFocus;
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
            parentV.setHasChanged(true);
            parentV.getParentDialog().updateDialogState();
        }
    }

    /**
     * Update the database with the model value.
     */
    public final void updateDatabase() {
        // update the model.
        System.out.println("FormalArgEditor updatedatabase called");
    }
}