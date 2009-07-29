package org.openshapa.views.discrete.datavalues;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusEvent;
import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.TimeStamp;
import org.openshapa.db.PredDataValue;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.openshapa.db.DataValue;
import org.openshapa.db.TimeStampDataValue;
import org.openshapa.views.discrete.EditorComponent;

/**
 * This class is the character editor of a TimeStampDataValues.
 */
public final class TimeStampDataValueEditor extends EditorComponent {

    /** Conversion factor for converting hours to ticks. */
    private static final long HH_TO_TICKS = 3600000;

    /** Array index for hourse. */
    private static final int HH = 0;

    /** Array index for minutes.  */
    private static final int MM = 1;

    /** Array index for seconds. */
    private static final int SS = 2;

    /** Array index for milliseconds. */
    private static final int MMM = 3;

    /** Conversion factor for converting minutes to ticks. */
    private static final long MM_TO_TICKS = 60000;

    /** Conversion factor for converting seconds to ticks. */
    private static final int SS_TO_TICKS = 1000;

    /** Logger for this class. */
    private static Logger logger = Logger
                                   .getLogger(TimeStampDataValueEditor.class);

    /** Are we deleting characters, or replacing them with a substitute? */
    private boolean isDeletingChar;

    /** The character to use as a substitute if we are doing replacement. */
    private char replaceChar;

    /** The last caret position. */
    private int oldCaretPosition;

    /** Should the oldCaretPosition be advanced by a single position? */
    private boolean advanceCaret;

    /** A list of characters that can not be removed from this view. */
    private Vector<Character> preservedChars;

    /** The TimeStampDataValue that this view represents. **/
    private TimeStampDataValue model;

    /** The parent datacell for the TimeStamp that this view represents. */
    private DataCell parentCell;

    /** The source of the TimeStampDataValue being edited. */
    private TimeStampSource dataSourceType;

    /**
     *
     */
    public enum TimeStampSource {
        /**
         * Timestamp is the Onset of the datacell associated.
         */
        Onset,
        /**
         * Timestamp is the Offset of the datacell associated.
         */
        Offset,
        /**
         * Timestamp is an argument of a datacell's matrix.
         */
        MatrixArg,
        /**
         * Timestamp is an argument of a predicate within a datacell.
         */
        PredicateArg
    }

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param sourceType What timestamp are we displaying.
     */
    public TimeStampDataValueEditor(final JTextComponent ta,
                                  final DataCell cell,
                                  final TimeStampSource sourceType) {
        super(ta);
        setEditable(true);
        parentCell = cell;
        dataSourceType = sourceType;
        preservedChars = new Vector<Character>();
        addPreservedChar(new Character(':'));
        setDeleteChar('0');
        resetValue();
    }

    /**
     * @return The model that this data value view represents.
     */
    public DataValue getModel() {
        return this.model;
    }

    /**
     * Reset the values by retrieving from the database.
     */
    public void resetValue() {
        String t = "";
        try {
            // reget the parentCell in case other data items have changed
            parentCell = (DataCell) parentCell.getDB()
                                                   .getCell(parentCell.getID());
            switch (dataSourceType) {
                case Onset:
                    model = new TimeStampDataValue(parentCell.getDB());
                    model.setItsValue(parentCell.getOnset());
                    break;
                case Offset:
                    model = new TimeStampDataValue(parentCell.getDB());
                    model.setItsValue(parentCell.getOffset());
                    break;
                default:
                    break;
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to resetValue.", e);
        }
        t = this.getModel().toString();
        setText(t);
        restoreCaretPosition();
    }

    /**
     * Update the database with the model value.
     */
    public void updateDatabase() {
        try {
            storeCaretPosition();
            // reget the parentCell in case other data items have changed
            parentCell = (DataCell) parentCell.getDB()
                                                   .getCell(parentCell.getID());
            TimeStampDataValue tsdv = (TimeStampDataValue) this.getModel();
            switch (dataSourceType) {
                case Onset:
                    parentCell.setOnset(tsdv.getItsValue());
                    break;
                case Offset:
                    parentCell.setOffset(tsdv.getItsValue());
                    break;
                default:
                    break;
            }
            parentCell.getDB().replaceCell(parentCell);
        } catch (SystemErrorException se) {
            logger.error("Unable to update Database: ", se);
        }
    }

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param p The predicate holding the datavalue this editor will represent.
     * @param pi The index of the datavalue within the predicate.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public TimeStampDataValueEditor(final JTextComponent ta,
                                  final DataCell cell,
                                  final PredDataValue p,
                                  final int pi,
                                  final Matrix matrix,
                                  final int matrixIndex) {
        // TODO - Timestamps within datacell
        // could I hold a proxy here? to use the updatedatabase code
        // from inside DataValueEditor.
    }

    /**
     * @return The list of preserved characters.
     */
    public Vector<Character> getPreservedChars() {
        return preservedChars;
    }

    /**
     * The action to invoke when a key is pressed.
     * @param e The key event that triggered this action.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
        if (!e.isConsumed()) {
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
    }

    /**
     * The action to invoke when a key is typed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        try {
            TimeStampDataValue tdv = (TimeStampDataValue) getModel();

            // The backspace key removes digits from behind the caret.
            if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                       && e.getKeyChar() == '\u0008') {

                // Can't delete empty time stamp data value.
                if (!tdv.isEmpty()) {
                    this.removeBehindCaret();
                    tdv.setItsValue(new TimeStamp(getText()));
                    e.consume();
                }

            // The delete key removes digits ahead of the caret.
            } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                       && e.getKeyChar() == '\u007F') {

                // Can't delete empty time stamp data value.
                if (!tdv.isEmpty()) {
                    int caret = getSelectionEnd();
                    this.removeAheadOfCaret();

                    boolean nextIsReserved = false;

                    for (int i = 0;
                         i < this.getPreservedChars().size();
                         i++) {

                        if (getText().charAt(caret)
                            == this.getPreservedChars().get(i)) {
                            nextIsReserved = true;
                            break;
                        }
                    }

                    if (nextIsReserved) {
                        setCaretPosition(caret);
                    } else {
                        setCaretPosition(caret - 1);
                    }

                    advanceCaret();
                    tdv.setItsValue(new TimeStamp(getText()));
                    e.consume();
                }

            // Key stoke is number - insert stroke at current caret position
            } else if (Character.isDigit(e.getKeyChar())) {
                this.removeAheadOfCaret();
                StringBuffer currentValue = new StringBuffer(getText());
                currentValue.deleteCharAt(getCaretPosition());
                currentValue.insert(getCaretPosition(), e.getKeyChar());
                advanceCaret();
                tdv.setItsValue(new TimeStamp(currentValue.toString()));
                e.consume();

            // Every other key stroke is ignored by the float editor.
            } else {
                e.consume();
            }

            // Push the value back into the database.
            updateDatabase();

            // Update the strings just in case we don't change the value.
            setText(this.getModel().toString());
            restoreCaretPosition();
        } catch (SystemErrorException se) {
            logger.error("Unable to update TimeStampDataValue", se);
        }
    }

    /**
     * Sanitize the text in the clipboard.
     * @return true if it is okay to call the JTextComponent's paste command.
     */
    @Override
    public boolean prePasteCheck() {
        // Get the contents of the clipboard.
        Clipboard clipboard = Toolkit.getDefaultToolkit()
                                     .getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasText = (contents != null)
                 && contents.isDataFlavorSupported(DataFlavor.stringFlavor);

        // No valid text in clipboard. Bail.
        if (!hasText) {
            return false;
        }

        // Valid text in clipboard - attempt to copy it into timestamp.
        try {
            String text = (String) contents
                                  .getTransferData(DataFlavor.stringFlavor);

            // Validate clipboard contents - if it is invalid, don't attempt
            // to paste it into the timestamp. 1234 1234
            boolean reject = true;
            for (int i = 0; i < text.length(); i++) {
                if (Character.isDigit(text.charAt(i))) {
                    reject = false;
                }

                for (int j = 0;
                     reject && j < getPreservedChars().size();
                     j++) {
                    if (getPreservedChars().get(i) == text.charAt(i)) {
                        reject = false;
                    }
                }
            }

            // Contents of clipboard contain valid value - attempt to paste
            // it into this timestamp.
            if (!reject) {
                // Truncate any delimiters out of the timestamp datavalue.
                // and treat it as a continous stream of digits.
                for (int i = 0; i < getPreservedChars().size(); i++) {
                    char[] temp = new char[1];
                    temp[0] = getPreservedChars().get(i);
                    String chars = new String(temp);
                    text = text.replace(chars, "");
                }

                TimeStampDataValue tsdv = (TimeStampDataValue) getModel();
                TimeStamp ts = tsdv.getItsValue();

                // If the user has selected text - ensure that the paste
                // location is the start of the selection.
                if (getSelectionEnd() > getSelectionStart()) {
                    setCaretPosition(getSelectionStart());
                }

                // For each digit in the clipboard - add it to the timestamp
                // rebuilding the timestamp as we go (to get smart edits).
                for (int i = 0; i < text.length(); i++) {
                    // Build a string buffer for the timestamp we are
                    // copying digits into.
                    StringBuffer v = new StringBuffer(ts.toString());

                    // If the character in the current caret position of the
                    // destination time stamp is a preservedCharacter, skip
                    // over it.
                    for (int j = 0; j < getPreservedChars().size(); j++) {
                        if (v.charAt(getCaretPosition())
                            == getPreservedChars().get(j)) {
                            setCaretPosition(getCaretPosition() + 1);
                        }
                    }

                    // Replace the character in the current caret position
                    // with one from our clipboard and rebuild the timestamp
                    // to benefit from 'smart' edits.
                    v.deleteCharAt(getCaretPosition());
                    v.insert(getCaretPosition(), text.charAt(i));
                    ts = new TimeStamp(v.toString());

                    // If we have got no more room in the timestamp - stop
                    // copying values.
                    if (getCaretPosition() + 1 == ts.toString().length()) {
                        setCaretPosition(getCaretPosition() + 1);
                        break;
                    }

                    // Advance the caret position to the next available slot
                    // in the destination timestamp.
                    setCaretPosition(getCaretPosition() + 1);
                }

                // Push the value back into the database.
                tsdv.setItsValue(ts);
                updateDatabase();

                // Update the strings if we don't change the value.
                setText(this.getModel().toString());
                restoreCaretPosition();
            }
        } catch (Exception ex) {
            logger.error("Unable to get clipboard contents", ex);
        }
        // already handled so don't process any further
        return false;
    }

    /**
     * Builds a new value from a string.
     * @param textField The string that you want to create the value from.
     * @return A value that can be set into the database.
     */
    public TimeStamp buildValue(final String textField) {
        try {
            long ticks = 0;

            String[] timeChunks = textField.split(":");

            ticks += (new Long(timeChunks[HH]) * HH_TO_TICKS);
            ticks += (new Long(timeChunks[MM]) * MM_TO_TICKS);
            ticks += (new Long(timeChunks[SS]) * SS_TO_TICKS);
            ticks += (new Long(timeChunks[MMM]));

            return new TimeStamp(SS_TO_TICKS, ticks);
        } catch (SystemErrorException e) {
            logger.error("Unable to build TimeStamp value", e);
            return null;
        }
    }

    /**
     * Set a flag to advanceCaret, when updateDatabase is called the
     * oldCaretPosition will be advanced by one position. When set value is
     * called back by the listeners it will reset this flag.
     */
    public void advanceCaret() {
        advanceCaret = true;
    }

    /**
     * Stores the currentCaretPosition, a call to restoreCaretPosition() can be
     * used to restore the caret position to the save point generated by this
     * method.
     */
    public void storeCaretPosition() {
        // Character inserted - advance the caret position.
        oldCaretPosition = getCaretPosition();
        if (advanceCaret) {
            oldCaretPosition++;
        }
    }

    /**
     * Restores the caret position to the last stored position. Use
     * storeCaretPosition() before calling this method.
     */
    public void restoreCaretPosition() {
        oldCaretPosition = Math.min(oldCaretPosition, getText().length());
        setCaretPosition(oldCaretPosition);
        advanceCaret = false;   // reset the advance caret flag - only applies
                                // once per database update. Database update
                                // triggers this method via a listener.
    }

    /**
     * Removes characters from ahead of the caret if they are not in the
     * preservedChars parameter. If the character is to be preserved, this
     * method will simple shift the caret forward one spot.
     */
    public void removeAheadOfCaret() {
        // Underlying text field has selection no caret, remove everything that
        // is selected.
        if ((getSelectionEnd() - getSelectionStart()) > 0) {
            removeSelectedText();

        // Underlying Text field has no selection, just a caret. Go ahead and
        // manipulate it as such.
        } else if (getText() != null && getText().length() > 0) {
            // Check ahead of caret to see if it is a preserved character. If
            // the character is preserved - simply move the caret ahead one spot
            // and leave the preserved character untouched.
            for (int i = 0; i < preservedChars.size(); i++) {
                if (getText().charAt(getCaretPosition())
                    == preservedChars.get(i)) {
                    setCaretPosition(getCaretPosition() + 1);
                    break;
                }
            }

            // Delete next character.
            StringBuffer currentValue = new StringBuffer(getText());
            currentValue.deleteCharAt(getCaretPosition());

            if (!isDeletingChar) {
                currentValue.insert(getCaretPosition(), replaceChar);
            }

            int cPosition = getCaretPosition();
            this.setText(currentValue.toString());
            setCaretPosition(cPosition);
        }
    }

    /**
     * Removes characters from behind the caret if they are not in the
     * preservedChars parameter. If the character is to be preserved, this
     * method will simply shift the caret back one spot.
     */
    public void removeBehindCaret() {
        // Underlying text field has selection and no carret, simply remove
        // everything that is selected.
        if ((getSelectionEnd() - getSelectionStart()) > 0) {
            removeSelectedText();

        // Underlying text field has no selection, just a caret. Go ahead and
        // manipulate it as such.
        } else if (getText() != null && getText().length() > 0) {
            // Check behind the caret to see if it is a preserved character. If
            // the character is preserved - simply move the caret back one spot
            // and leave the preserved character untouched.
            int carPosMinusOne = Math.max(0, getCaretPosition() - 1);
            for (int i = 0; i < preservedChars.size(); i++) {
                if (getText().charAt(carPosMinusOne)
                    == preservedChars.get(i)) {
                    setCaretPosition(carPosMinusOne);
                    carPosMinusOne = Math.max(0, getCaretPosition() - 1);
                    break;
                }
            }

            // Delete previous character.
            StringBuffer currentValue = new StringBuffer(getText());
            currentValue.deleteCharAt(carPosMinusOne);
            if (!isDeletingChar) {
                currentValue.insert(carPosMinusOne, replaceChar);
            }

            int cPosition = carPosMinusOne;
            this.setText(currentValue.toString());
            setCaretPosition(cPosition);
        }
    }

    /**
     * This method will remove any characters that have been selected in the
     * underlying text field and that don't exist in the preservedChars
     * parameter. If no characters have been selected, the underlying text field
     * is unchanged.
     */
    public void removeSelectedText() {
        // Get the current value of the visual representation of this DataValue.
        StringBuffer cValue = new StringBuffer(getText());

        // Obtain the start and finish of the selected text.
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        int pos = start;

        for (int i = start; i < end; i++) {
            boolean found = false;

            // See if the character at the current position is reserved.
            for (int j = 0; j < preservedChars.size(); j++) {
                if (preservedChars.get(j) == cValue.charAt(pos)) {
                    found = true;
                    break;
                }
            }

            // Current character is not reserved - either delete or replace it.
            if (!found) {
                cValue.deleteCharAt(pos);

                // Replace the character rather than remove it, we then need to
                // skip to the next position to delete a character.
                if (!isDeletingChar) {
                    cValue.insert(pos, replaceChar);
                    pos++;
                }

            // Current character is reserved, skip over current position.
            } else {
                pos++;
            }
        }

        // Set the text for this data value to the new string.
        this.setText(cValue.toString());
        this.setCaretPosition(start);
    }

    /**
     * Rather than delete characters.
     *
     * @param c The character to use when deleting (rather than deleting - the
     * supplied character is used to replace).
     */
    public void setDeleteChar(final char c) {
        isDeletingChar = false;
        replaceChar = c;
    }

    /**
     * Adds a character to the list that must be preserved by the editor
     * (characters that can not be deleted).
     *
     * @param c The character to be preserved.
     */
    public void addPreservedChar(final Character c) {
        preservedChars.add(c);
    }

    /**
     * Update the model to reflect the value represented by the
     * editor's text representation.
     */
    public void updateModelValue() {
    }

    /**
     * focusSet is the signal that this editor has become "current".
     * @param fe Focus Event
     */
    @Override
    public void focusGained(final FocusEvent fe) {
    }

    /**
     * Action to take when focus is lost for this editor.
     * @param fe Focus Event
     */
    @Override
    public void focusLost(final FocusEvent fe) {
    }

    /**
     * Action to take by this editor when a key is released.
     * @param e KeyEvent
     */
    @Override
    public void keyReleased(final KeyEvent e) {
    }
}
