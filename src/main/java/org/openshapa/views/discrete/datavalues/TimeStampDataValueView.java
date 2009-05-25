package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.TimeStamp;
import org.openshapa.db.TimeStampDataValue;
import org.openshapa.OpenSHAPA;
import org.openshapa.db.PredDataValue;
import org.openshapa.views.discrete.Editor;
import org.openshapa.views.discrete.Selector;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import org.apache.log4j.Logger;

/**
 * An abstract view for TimeStampDataValues.
 */
public abstract class TimeStampDataValueView extends DataValueElementV {

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
                                   .getLogger(TimeStampDataValueView.class);

    /**
     * Constructor.
     *
     * @param cellSelection The parent cell selection that may contain this
     * view.
     * @param cell The parent cell that this view partially represents.
     * @param matrix The parent matrix containing the formal argument that this
     * view represents.
     * @param matrixIndex The index of the formal argument that this view
     * represents.
     * @param editable Is this view editable or not, true if the view is
     * editable. False otherwise.
     */
    public TimeStampDataValueView(final Selector cellSelection,
                                  final DataCell cell,
                                  final Matrix matrix,
                                  final int matrixIndex,
                                  final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);
    }

    /**
     * Constructor.
     *
     * @param cellSelection The parent cell selection that may contain the view.
     * @param cell The parent cell that this view partially represents.
     * @param predicate The parent predicate containing the formal argument that
     * this view represents.
     * @param predicateIndex the index of the formal argument that this view
     * represents.
     * @param editable Is this view editable or not, true if the view is
     * editable. False otherwise.
     */
    public TimeStampDataValueView(final Selector cellSelection,
                                  final DataCell cell,
                                  final PredDataValue predicate,
                                  final int predicateIndex,
                                  final Matrix matrix,
                                  final int matrixIndex,
                                  final boolean editable) {
        super(cellSelection, cell, predicate, predicateIndex,
              matrix, matrixIndex, editable);
    }

    /**
     * Constructor.
     *
     * @param cellSelection The parent cell selection that may contain this view
     * @param cell The parent cell that this view partially represents.
     * @param timeStampDataValue The wrapper timeStampDataValue that this view
     * will represent.
     * @param editable Is this view editable or not, true if the view is
     * editable. False otherwise.
     */
    public TimeStampDataValueView(final Selector cellSelection,
                                  final DataCell cell,
                                  final TimeStampDataValue timeStampDataValue,
                                  final boolean editable) {
        super(cellSelection, cell, timeStampDataValue, editable);
    }

    /**
     * @return Builds the editor to be used for this data value.
     */
    protected final Editor buildEditor() {
        TimeStampEditor tse = new TimeStampEditor();
        tse.addPreservedChar(new Character(':'));
        tse.setDeleteChar('0');

        return tse;
    }

    /**
     * Sets the value of that this TimeStampDataValueView represents.
     *
     * @param ts The new value of that this TimeStampDataValueView will
     * represent.
     */
    public final void setValue(final TimeStamp ts) {
        try {
            TimeStampDataValue tsdv = (TimeStampDataValue) getModel();
            tsdv.setItsValue(ts);
            updateStrings();
            this.getEditor().restoreCaretPosition();
        } catch (SystemErrorException se) {
            logger.error("Sets the value for the TimeStampDataValue", se);
        }

    }

    /**
     * Editor to use for the time stamp data value.
     */
    class TimeStampEditor extends DataValueElementV.DataValueEditor {

        /**
         * Attempt to paste the contents of the clipboard into this timestamp.
         */
        @Override
        public final void paste() {
            // Get the contents of the clipboard.
            Clipboard clipboard = Toolkit.getDefaultToolkit()
                                         .getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            boolean hasText = (contents != null)
                     && contents.isDataFlavorSupported(DataFlavor.stringFlavor);

            // No valid text in clipboard. Bail.
            if (!hasText) {
                return;
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
                        ts = buildValue(v.toString());

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
                    updateStrings();
                    restoreCaretPosition();
                }
            } catch (Exception ex) {
                logger.error("Unable to get clipboard contents", ex);
            }
        }

        /**
         * The action to invoke when a key is typed.
         *
         * @param e The KeyEvent that triggered this action.
         */
        public void keyTyped(final KeyEvent e) {
            try {
                TimeStampDataValue tdv = (TimeStampDataValue) getModel();

                // The backspace key removes digits from behind the caret.
                if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                           && e.getKeyChar() == '\u0008') {

                    // Can't delete empty time stamp data value.
                    if (!tdv.isEmpty()) {
                        this.removeBehindCaret();
                        tdv.setItsValue(buildValue(getText()));
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
                        tdv.setItsValue(buildValue(getText()));
                        e.consume();
                    }

                // Key stoke is number - insert stroke at current caret position
                } else if (Character.isDigit(e.getKeyChar())) {
                    this.removeAheadOfCaret();
                    StringBuffer currentValue = new StringBuffer(getText());
                    currentValue.deleteCharAt(getCaretPosition());
                    currentValue.insert(getCaretPosition(), e.getKeyChar());
                    advanceCaret();
                    tdv.setItsValue(buildValue(currentValue.toString()));
                    e.consume();

                // Every other key stroke is ignored by the float editor.
                } else {
                    e.consume();
                }

                // Push the value back into the database.
                updateDatabase();

                // Update the strings just in case we don't change the value.
                updateStrings();
                restoreCaretPosition();
            } catch (SystemErrorException se) {
                logger.error("Unable to update TimeStampDataValue", se);
            }
        }

        /**
         * Builds a new Double value from a string.
         *
         * @param textField The String that you want to create a Double from.
         *
         * @return A Double value that can be used setting the database.
         */
        public final TimeStamp buildValue(final String textField) {
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
    }
}
