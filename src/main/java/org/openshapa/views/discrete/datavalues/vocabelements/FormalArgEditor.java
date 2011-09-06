/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.views.discrete.datavalues.vocabelements;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;

import database.FormalArgument;
import database.SystemErrorException;
import database.VocabElement;
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
    private static Logger LOGGER = UserMetrix.getLogger(FormalArgEditor.class);

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
            LOGGER.error("Unable to resetValue", se);
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
            if (!this.isReserved(e.getKeyChar())) {

            try {
                removeSelectedText();
                StringBuilder currentValue = new StringBuilder(getText());
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
                LOGGER.error("Unable to set new predicate name", se);
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
                try {
                removeBehindCaret();
                model.setFargName("<" + getText() + ">");
                vocabElement.replaceFormalArg(model, argIndex);
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
                model.setFargName("<" + getText() + ">");
                vocabElement.replaceFormalArg(model, argIndex);
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
