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
package org.datavyu.views.discrete.datavalues.vocabelements;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import org.datavyu.models.db.Argument;
import org.datavyu.models.db.UserWarningException;
import org.datavyu.models.db.Variable;
import org.datavyu.util.SequentialNumberGenerator;
import org.datavyu.views.discrete.EditorComponent;

import javax.swing.text.JTextComponent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;

/**
 * This class is the character editor of a NominalDataValue.
 */
public final class VENameEditor extends EditorComponent {

    /**
     * Parent Vocab Element.
     */
    private Argument model;

    /**
     * Parent Variable.
     */
    private Variable varModel;

    /**
     * String holding the reserved characters.
     */
    private static final String RESERVED_CHARS = ")(<>|,;\t\r\n";

    /**
     * The logger for this class.
     */
    private static Logger LOGGER = UserMetrix.getLogger(VENameEditor.class);

    /**
     * The parent editor window that this argument belongs too.
     */
    private VocabElementV parentView;

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param ve The parent Argument the editor is in.
     * @param pv The parent VocabElementV the editor is in.
     */
    public VENameEditor(final JTextComponent ta,
                        final Argument ve,
                        final Variable var,
                        final VocabElementV pv) {
        super(ta);
        setEditable(true);
        parentView = pv;
        model = ve;
        varModel = var;
        setText(varModel.getName());
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
            removeSelectedText();
            StringBuilder currentValue = new StringBuilder(getText());
            currentValue.insert(getCaretPosition(), e.getKeyChar());

            for (Argument arg : varModel.getVariableType().childArguments) {
                if (arg.name.equals(model.name)) {
                    arg.name = currentValue.toString();
                }
            }

            try {
                varModel.setName(currentValue.toString());
            } catch (UserWarningException ex) {
                java.util.logging.Logger.getLogger(VENameEditor.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Advance caret over the top of the new char.
            int pos = this.getCaretPosition() + 1;
            this.setText(currentValue.toString());
            this.setCaretPosition(pos);

            parentView.setHasChanged(true);
            parentView.getParentDialog().updateDialogState();
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
                removeBehindCaret();
                if (!getText().equals("")) {
                    model.name = getText();
                } else {
                    model.name = "unnamed" + Integer.toString(SequentialNumberGenerator.getNextSeqNum());
                }
                parentView.setHasChanged(true);
                parentView.getParentDialog().updateDialogState();

                e.consume();
                break;
            case KeyEvent.VK_DELETE:
                removeAheadOfCaret();
                if (!getText().equals("")) {
                    model.name = getText();
                } else {
                    model.name = "unnamed" + Integer.toString(SequentialNumberGenerator.getNextSeqNum());
                }
                parentView.setHasChanged(true);
                parentView.getParentDialog().updateDialogState();

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