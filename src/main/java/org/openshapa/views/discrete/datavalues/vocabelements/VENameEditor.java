package org.openshapa.views.discrete.datavalues.vocabelements;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.VocabElement;
import org.openshapa.views.discrete.EditorComponent;

/**
 * This class is the character editor of a NominalDataValue.
 */
public final class VENameEditor extends EditorComponent {

    /** Parent Vocab Element. */
    private VocabElement model;

    /** String holding the reserved characters. */
    private static final String RESERVED_CHARS = ")(<>|,;\t\r\n";

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(VENameEditor.class);

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
    }

    /**
     * Action to invoke when focus is lost.
     *
     * @param e The FocusEvent that triggered this action.
     */
    @Override
    public void focusLost(final FocusEvent e) {
    }

    /**
     * The action to invoke when a key is typed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        // The backspace key removes digits from behind the caret.
        if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
            && e.getKeyChar() == '\u0008') {

            try {
                removeBehindCaret();
                model.setName(getText());

                parentView.setHasChanged(true);
            } catch (SystemErrorException se) {
                logger.error("Unable to backspace from predicate name", se);
            }

        // The delete key removes digits ahead of the caret.
        } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                   && e.getKeyChar() == '\u007F') {

            try {
                removeAheadOfCaret();
                model.setName(getText());

                parentView.setHasChanged(true);
            } catch (SystemErrorException se) {
                logger.error("Unable to delete from predicate name", se);
            }

        // If the character is not reserved - add it to the name of the pred
        } else if (!this.isReserved(e.getKeyChar())) {

            try {
                removeSelectedText();
                StringBuffer currentValue = new StringBuffer(getText());
                currentValue.insert(getCaretPosition(), e.getKeyChar());
                model.setName(currentValue.toString());

                // Advance caret over the top of the new char.
                int pos = this.getCaretPosition() + 1;
                this.setText(currentValue.toString());
                this.setCaretPosition(pos);

                parentView.setHasChanged(true);
            } catch (SystemErrorException se) {
                logger.error("Unable to set new predicate name", se);
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