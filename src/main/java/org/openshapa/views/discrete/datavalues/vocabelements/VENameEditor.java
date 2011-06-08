package org.openshapa.views.discrete.datavalues.vocabelements;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;

import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.models.db.legacy.VocabElement;
import org.openshapa.views.discrete.EditorComponent;
import org.openshapa.util.SequentialNumberGenerator;

/**
 * This class is the character editor of a NominalDataValue.
 */
public final class VENameEditor extends EditorComponent {

    /** Parent Vocab Element. */
    private VocabElement model;

    /** String holding the reserved characters. */
    private static final String RESERVED_CHARS = ")(<>|,;\t\r\n";

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(VENameEditor.class);

    /** The parent editor window that this argument belongs too. */
    private VocabElementV parentView;

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
        parentView = pv;
        model = ve;
        setText(model.getName());
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
    public void keyTyped(final KeyEvent e) {
        // The backspace key removes digits from behind the caret.
        if (!this.isReserved(e.getKeyChar())) {
            try {
                removeSelectedText();
                StringBuilder currentValue = new StringBuilder(getText());
                currentValue.insert(getCaretPosition(), e.getKeyChar());
                model.setName(currentValue.toString());

                // Advance caret over the top of the new char.
                int pos = this.getCaretPosition() + 1;
                this.setText(currentValue.toString());
                this.setCaretPosition(pos);

                parentView.setHasChanged(true);
                parentView.getParentDialog().updateDialogState();
            } catch (SystemErrorException se) {
                LOGGER.error("Unable to set new predicate name", se);
            }
        }

        e.consume();
    }

    /**
     * @param aChar Character to test
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
                try {
                    removeBehindCaret();
                    if(!getText().equals("")){
                        model.setName(getText());
                    }else{
                        model.setName("unnamed"+ Integer.toString(SequentialNumberGenerator.getNextSeqNum()));
                    }
                    parentView.setHasChanged(true);
                    parentView.getParentDialog().updateDialogState();
                } catch (SystemErrorException se) {
                    LOGGER.error("Unable to backspace from predicate name", se);
                }
                e.consume();
                break;
            case KeyEvent.VK_DELETE:
                try {
                    removeAheadOfCaret();
                    if(!getText().equals("")){
                        model.setName(getText());
                    }else{
                        model.setName("unnamed"+ Integer.toString(SequentialNumberGenerator.getNextSeqNum()));
                    }
                    parentView.setHasChanged(true);
                    parentView.getParentDialog().updateDialogState();
                } catch (SystemErrorException se) {
                    LOGGER.error("Unable to delete from predicate name", se);
                }
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