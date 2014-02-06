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
package org.datavyu.views.discrete.datavalues;

import org.datavyu.models.db.NominalValue;

import javax.swing.text.JTextComponent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

/**
 * This class is the character editor of a NominalDataValue.
 */
public final class NominalDataValueEditor extends DataValueEditor {

    /**
     * String holding the reserved characters - these are characters that are
     * users are unable to enter into a nominal field.
     * <p/>
     * BugzID:524 - If Character is an escape key - ignore it.
     */
    private static final String RESERVED_CHARS = ")(<>|,;\t\r\n\"\u001B";

    /**
     * The model that this editor is manipulating
     */
    NominalValue model;

    /**
     * Constructor.
     *
     * @param ta           The parent JTextComponent the editor resides within.
     * @param NominalValue The value this editor manipulates.
     */
    public NominalDataValueEditor(final JTextComponent ta,
                                  final NominalValue nv) {
        super(ta, nv);
        model = nv;
    }

    /**
     * @param aChar Character to test
     * @return true if the character is a reserved character.
     */
    public boolean isReserved(final char aChar) {
        return (RESERVED_CHARS.indexOf(aChar) >= 0);
    }

    // *************************************************************************
    // Parent Class Overrides
    // *************************************************************************
    @Override
    public void focusLost(final FocusEvent fe) {
        // BugzID:581 - Trim trailing spaces from nominal (apparently they are
        // not permitted).
        super.focusLost(fe);
        if (!model.isValid(this.getText())) {
            this.setText(model.toString());
        }

        super.focusLost(fe);
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        super.keyTyped(e);

        // Just a regular vanilla keystroke - insert it into nominal field.
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

            // All other keystrokes are consumed.
        } else {
            e.consume();
        }

        // Push the character changes into the database.
        if (model.isValid(this.getText())) {
            model.set(this.getText());
            //ndv.setItsValue(this.getText());
            //updateDatabase();

            // BugzID:668 - The user is reverting back to a 'placeholder' state.
        } else if (this.getText().equals("")) {
            model.clear();
            //ndv.clearValue();
            //updateDatabase();
        }
    }
}