package org.openshapa.views.discrete.datavalues.vocabelements;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.models.db.FormalArgument;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.db.VocabElement;
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

    /** String holding the reserved characters. */
    private static final String RESERVED_CHARS = ")(<>|,;\t\r\n";

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(FormalArgEditor.class);

    /** The parent editor window that this argument belongs too. */
    private VocabElementV parentView;

    /**
     * @param ta The JTextComponent that this virtual editor floats ontop.
     * @param ve The parent vocab element that this argument belongs too.
     * @param index The index of the argument within the parent vocabelement
     * that this Editor will represent.
     * @param pv The parent vocab element view that this editor belongs too.
     */
    public FormalArgEditor(final JTextComponent ta,
                           final VocabElement ve,
                           final int index,
                           final VocabElementV pv) {
        super(ta);
        setEditable(true);
        argIndex = index;
        parentView = pv;
        vocabElement = ve;
        resetValue();
    }

    /**
     * Resets the text value of this editor.
     */
    public void resetValue() {
        try {
            model = vocabElement.getFormalArgCopy(argIndex);

            // Formal argument name contains "<" and ">" characters which we
            // don't actually want.
            String fargName = "";
            if (model != null) {
                fargName = model.getFargName()
                                .substring(1, model.getFargName().length() - 1);
            }
            setText(fargName);
        } catch (SystemErrorException se) {
            logger.error("Unable to resetValue", se);
        }
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
     * Action to invoke when focus is gained.
     *
     * @param e The FocusEvent that triggered this action.
     */
    @Override
    public void focusGained(final FocusEvent e) {
        this.parentView.getParentDialog().updateDialogState();
    }

    /**
     * The action to invoke when a key is typed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(KeyEvent e) {

        // The backspace key removes digits from behind the caret.
        if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
            && e.getKeyChar() == '\u0008') {

            try {
                removeBehindCaret();
                model.setFargName("<" + getText() + ">");
                vocabElement.replaceFormalArg(model, argIndex);
                parentView.setHasChanged(true);
                parentView.getParentDialog().updateDialogState();
            } catch (SystemErrorException se) {
                logger.error("Unable to backspace from predicate name", se);
            }

        // The delete key removes digits ahead of the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u007F') {

            try {
                removeAheadOfCaret();
                model.setFargName("<" + getText() + ">");
                vocabElement.replaceFormalArg(model, argIndex);
                parentView.setHasChanged(true);
                parentView.getParentDialog().updateDialogState();
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
                vocabElement.replaceFormalArg(model, argIndex);

                // Advance caret over the top of the new char.
                int pos = this.getCaretPosition() + 1;
                this.setText(currentValue.toString());
                this.setCaretPosition(pos);

                parentView.setHasChanged(true);
                parentView.getParentDialog().updateDialogState();
            } catch (SystemErrorException se) {
                logger.error("Unable to set new predicate name", se);
            }
        }

        e.consume();
    }

    /**
     * @param aChar Character to test
     *
     * @return true if the character is a reserved character.
     */
    public boolean isReserved(final char aChar) {
        return (RESERVED_CHARS.indexOf(aChar) >= 0);
    }

    /**
     * Action to take by this editor when a key is pressed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
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
     * Action to take by this editor when a key is released.
     *
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyReleased(final KeyEvent e) {
    }
}
