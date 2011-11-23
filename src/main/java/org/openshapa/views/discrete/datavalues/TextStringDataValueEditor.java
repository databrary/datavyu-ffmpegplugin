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
package org.openshapa.views.discrete.datavalues;

import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;

import org.openshapa.models.db.TextValue;

/**
 * This class is the character editor of a TextStringDataValue.
 */
public final class TextStringDataValueEditor extends DataValueEditor {

    /**
     * String holding the reserved characters - these are characters that are
     * users are unable to enter into a text field.
     *
     * BugzID:524 - If Character is an escape key - ignore it.
     */
    private static final String RESERVED_CHARS = "\u001B\t";

    /** The model that this editor is manipulating */
    TextValue model;

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param tv The value this editor manipulates
     */
    public TextStringDataValueEditor(final JTextComponent ta,
                                     final TextValue tv) {
        super(ta, tv);
        setAcceptReturnKey(true);
        model = tv;
    }

    /**
     * @param aChar Character to test
     *
     * @return true if the character is a reserved character.
     */
    public boolean isReserved(final char aChar) {
        return (RESERVED_CHARS.indexOf(aChar) >= 0);
    }

    // *************************************************************************
    // Parent Class Overrides
    // *************************************************************************
    @Override
    public void keyTyped(final KeyEvent e) {
        super.keyTyped(e);

        // Just a regular vanilla keystroke - insert it into text field.
        if (!e.isConsumed() && !e.isMetaDown() && !e.isControlDown()
            && !isReserved(e.getKeyChar())) {
            this.removeSelectedText();
            StringBuilder currentValue = new StringBuilder(getText());

            // If we have a delete or backspace key - do not insert.
            if (!(e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                  && e.getKeyChar() == '\u007F') &&
                !(e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                  && e.getKeyChar() == '\u0008')) {
                currentValue.insert(getCaretPosition(), e.getKeyChar());
            }

            // Advance caret over the top of the new char.
            int pos = this.getCaretPosition() + 1;
            this.setText(currentValue.toString());
            this.setCaretPosition(pos);
            e.consume();

            // Push the character changes into the database.
            // BugzID:668 - The user is reverting back to a 'placeholder' state.
            if (this.getText().equals("")) {
                model.clear();
            } else {
                model.set(this.getText());
            }

        // All other key strokes are consumed.
        } else {
            e.consume();
        }        
    }
}