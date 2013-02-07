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
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;
import org.datavyu.models.db.Argument;
import org.datavyu.views.discrete.EditorComponent;

/**
 * This class is the character editor of a NominalDataValue.
 */
public final class FormalArgTypeEditor extends EditorComponent {

    /** Parent Vocab Element. */
    private Argument parentArgument;

    /** Index of the formal arg. */
    private int argIndex;

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(FormalArgTypeEditor.class);

    public FormalArgTypeEditor(final JTextComponent ta,
                               final Argument pa,
                               final int index,
                               final VocabElementV pv) {
        super(ta);
        argIndex = index;
        resetValue(pa);
    }

    public final void resetValue(final Argument pa) {
        parentArgument = pa;

        String argType = "";
        Argument model = parentArgument.childArguments.get(argIndex);
        if (model != null) {
            argType = model.type.toString().substring(0, 1);
        }

        setText(argType);
    }

    /**
     * The action to invoke when focus is gained. NOTE: This is an uneditable
     * editor - so this method should never be called.
     *
     * @param fe The event that triggered this action.
     */
    @Override
    public void focusGained(final FocusEvent fe) {
    }

    /**
     * The action to invoke when a key is typed. NOTE: this is an uneditable
     * editor - so this method should never be called.
     *
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
    }

    /**
     * Action to take by this editor when a key is pressed. NOTE: This is an
     * uneditable editor - so this method should never be called.
     *
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
    }

    /**
     * Action to take by this editor when a key is released. NOTE: This is an
     * uneditable editor - so this method should never be called.
     *
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyReleased(final KeyEvent e) {
    }
}
