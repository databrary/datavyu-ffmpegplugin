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

import com.usermetrix.jclient.Logger;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.text.JTextComponent;

import org.openshapa.views.discrete.EditorComponent;

import com.usermetrix.jclient.UserMetrix;
import org.openshapa.models.db.Cell;

/**
 * This class is the character editor of a TimeStampDataValues.
 */
public final class TimeStampDataValueEditor extends EditorComponent {

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(TimeStampDataValueEditor.class);

    /** The parent datacell for the TimeStamp that this view represents. */
    private Cell parentCell;

    /** The source of the TimeStampDataValue being edited. */
    private TimeStampSource dataSourceType;

    public enum TimeStampSource {
        /** Timestamp is the Onset of the datacell associated. */
        Onset,
        /** Timestamp is the Offset of the datacell associated. */
        Offset
    }

    /**
     * Constructor.
     * 
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param sourceType What timestamp are we displaying.
     */
    public TimeStampDataValueEditor(final JTextComponent ta,
                                    final Cell cell,
                                    final TimeStampSource sourceType) {
        super(ta);
        setEditable(true);
        parentCell = cell;
        dataSourceType = sourceType;
        addPreservedChars(":");
        setDeleteChar('0');
        resetValue();
    }

    /**
     * Reset the values by retrieving from the database.
     */
    public void resetValue() {
        // reget the parentCell in case other data items have changed
        switch (dataSourceType) {
            case Onset:
                setText(parentCell.getOnsetString());
                break;
            default:
                setText(parentCell.getOffsetString());
                break;
        }
    }

    private void setTimeStamp(final String value) {
        switch (dataSourceType) {
            case Onset:
                parentCell.setOnset(value);
                break;
            default:
                parentCell.setOffset(value);
                break;
        }
    }

    /**
     * Action to take by this editor when a key is pressed.
     * 
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
        //TimeStampDataValue tdv = (TimeStampDataValue) getModel();
        switch (e.getKeyCode()) {
        // BugzID:708 - Force the Home key to behave correctly on OSX 10.4
        case KeyEvent.VK_HOME:
            setCaretPosition(0);
            e.consume();
            break;

        // BugzID:708 - Force the End key to behave correctly on OSX 10.4
        case KeyEvent.VK_END:
            setCaretPosition(getText().length());
            e.consume();
            break;

        case KeyEvent.VK_BACK_SPACE:
            removeBehindCaret();
            setTimeStamp(getText());
            e.consume();
            break;
        case KeyEvent.VK_DELETE:
            int caret = getSelectionEnd();
            removeAheadOfCaret();
            setCaretPosition(caret);
            if (caret < getText().length()
                    && isPreserved(getText().charAt(caret))) {
                setCaretPosition(getCaretPosition() + 1);
            }
            setCaretPosition(getCaretPosition() + 1);
            //Move an extra caret position if the next char is a ":"
            int c = Math.min(getText().length() - 1, getCaretPosition());
            if (isPreserved(getText().charAt(c))) {
                setCaretPosition(getCaretPosition() + 1);
            }

            setTimeStamp(getText());
            e.consume();
            break;

        case KeyEvent.VK_LEFT:
            int selectStart = getSelectionStart();
            int selectEnd = getSelectionEnd();

            // Move caret to the left.
            c = Math.max(0, getCaretPosition() - 1);
            setCaretPosition(c);

            // If after the move, we have a character to the left is
            // preserved character we need to skip one before passing
            // the key event down to skip again (effectively skipping
            // the preserved character).
            int b = Math.max(0, getCaretPosition());
            c = Math.max(0, getCaretPosition() - 1);
            if (isPreserved(getText().charAt(b))) {
                setCaretPosition(Math.max(0, getCaretPosition() - 1));
            }
            e.consume();

            // If the user is holding down shift - alter the selection as
            // well as the caret position.
            if (e.getModifiers() == InputEvent.SHIFT_MASK) {
                // Shrink selection left - removed entire selection.
                if (getCaretPosition() == selectStart) {
                    select(selectStart, selectStart);
                    // Grow selection left.
                } else if (getCaretPosition() < selectStart) {
                    select(selectEnd, getCaretPosition());
                    // Shrink selection left.
                } else {
                    select(selectStart, getCaretPosition());
                }
            }

            break;

        case KeyEvent.VK_RIGHT:
            selectStart = getSelectionStart();
            selectEnd = getSelectionEnd();

            // Move caret to the right.
            c = Math.min(getText().length(), getCaretPosition() + 1);
            setCaretPosition(c);

            // If after the move, we have a character to the right that
            // is a preserved character, we need to skip one before
            // passing the key event down to skip again (effectively
            // skipping the preserved character)
            b = Math.min(getText().length() - 1, getCaretPosition());
            c = Math.min(getText().length() - 1, getCaretPosition() + 1);
            if (c < getText().length()
                    && (isPreserved(getText().charAt(b)))) {
                setCaretPosition(c);
            }
            e.consume();

            // If the user is holding down shift - alter the selection as
            // well as the caret position.
            if (e.getModifiers() == InputEvent.SHIFT_MASK) {
                // Shrink selection right - removed entire selection.
                if (getCaretPosition() == selectEnd) {
                    select(selectEnd, selectEnd);
                    // Grow selection right.
                } else if (getCaretPosition() > selectEnd) {
                    select(selectStart, getCaretPosition());
                    // Shrink select right.
                } else {
                    select(getCaretPosition(), selectEnd);
                }
            }
            break;

        default:
            break;
        }
    }

    /**
     * The action to invoke when a key is typed.
     * 
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        // Key stoke is number - insert stroke at current caret position
        // but only if their is room in the editor for the new digit.
        if (Character.isDigit(e.getKeyChar()) && getCaretPosition() <= getText().length()) {
            removeAheadOfCaret();
            StringBuilder currentValue = new StringBuilder(getText());
            currentValue.deleteCharAt(getCaretPosition());
            currentValue.insert(getCaretPosition(), e.getKeyChar());
            setCaretPosition(getCaretPosition() + 1);
            //Move an extra caret position if the next char is a ":"
            int c = Math.min(getText().length() - 1, getCaretPosition());
            if (isPreserved(getText().charAt(c))) {
                setCaretPosition(getCaretPosition() + 1);
            }

            setTimeStamp(currentValue.toString());
            e.consume();

        // Every other key stroke is ignored by the float editor.
        } else {
            e.consume();

            // Nothing has changed - skip updating the database.
            return;
        }
    }
 
    /**
     * focusSet is the signal that this editor has become "current".
     * 
     * @param fe Focus Event
     */
    @Override
    public void focusGained(final FocusEvent fe) {
        //cb = null;
    }
    
    @Override
    public void focusLost(FocusEvent fe) {
        super.focusLost(fe);
        /*
        if (cb != null) {
            try {
                DataCell c = (DataCell) OpenSHAPA.getProjectController().getLegacyDB().getDatabase().getCell(parentCell);
                // record the effect
                if (cb.getOnset().getTime()  != c.getOnset().getTime()) {
                   UndoableEdit edit = new ChangeOnsetCellEdit(cb);                        
                   
                   // notify the listeners
                   OpenSHAPA.getView().getUndoSupport().postEdit(edit);
                } else if (cb.getOffset().getTime() != c.getOffset().getTime())   {
                   UndoableEdit edit = new ChangeOffsetCellEdit(cb);                       
                   // notify the listeners
                   OpenSHAPA.getView().getUndoSupport().postEdit(edit);                    
                }        
            
            } catch (SystemErrorException e) {
                LOGGER.error("Unable to create DataCell", e);
            }          
        }*/
    }

    /**
     * Action to take by this editor when a key is released.
     * 
     * @param e KeyEvent
     */
    @Override
    public void keyReleased(final KeyEvent e) {
    }
}
