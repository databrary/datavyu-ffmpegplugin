package org.openshapa.views.discrete.datavalues.vocabelements;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.VocabElement;
import org.openshapa.views.discrete.EditorComponent;

/**
 * This class is the character editor of a NominalDataValue.
 */
public final class VENameEditor extends EditorComponent {

    /** Parent Vocab Element. */
    private VocabElement vocabElement;

    /** Text when editor gained focus (became current editor). */
    private String textOnFocus;

    /** String holding the reserved characters. */
    private static final String RESERVED_CHARS = ")(<>|,;\t\r\n";

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(VENameEditor.class);

    /** The parent editor window that this argument belongs too. */
    private VocabElementV parentV;

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param ve The parent VocabElement the editor is in.
     * @param pv The parent VocabElementV the editor is in.
     */
    public VENameEditor(final JTextComponent ta,
                        final VocabElement ve,
                        final VocabElementV pv) {
        super(ta);
        setEditable(true);
        parentV = pv;
        resetValue(ve);
    }

    /**
     * Reset the values by retrieving from the database.
     * @param ve The parent VocabElement the editor is in.
     */
    public void resetValue(final VocabElement ve) {
        vocabElement = ve;
        setText(vocabElement.getName());
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
        parentV.getParentDialog().updateDialogState();
    }

    /**
     * Action to take when focus is lost for this editor.
     * @param fe Focus Event
     */
    @Override
    public void focusLost(final FocusEvent fe) {
        if (!getText().equals(textOnFocus)) {
            updateDatabase();
        }
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
    public void updateDatabase() {
        // update the model.
        System.out.println("VENameEditor updatedatabase called");
    }

}