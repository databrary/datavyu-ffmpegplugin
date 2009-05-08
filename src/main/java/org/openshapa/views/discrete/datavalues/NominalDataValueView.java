package org.openshapa.views.discrete.datavalues;

import org.openshapa.OpenSHAPA;
import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import org.openshapa.db.NominalDataValue;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.discrete.Editor;
import org.openshapa.views.discrete.Selector;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * This class is the view representation of a NominalDataValue as stored within
 * the database.
 *
 * @author cfreeman
 */
public final class NominalDataValueView extends DataValueElementV {

    /** The logger for NominalDataValueView. */
    private static Logger logger = Logger.getLogger(NominalDataValueView.class);

    /** Reserved characters are not permitted in a nominal value at all. */
    private HashMap<Character, Character> reservedChars;

    /** The reserved replacement is a character that replaces reserved
     *  characters pasted into nominal views. */
    private static final Character RESERVED_REPLACEMENT = '_';

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param cell The parent datacell for the nominal data value that this view
     * represents.
     * @param matrix The parent matrix for the nominal data value that this view
     * represents.
     * @param matrixIndex The index of the NominalDataValue within the above
     * parent matrix that this view represents.
     * @param editable Is the dataValueView editable by the user? True if the
     * value is permitted to be altered by the user. False otherwise.
     */
    public NominalDataValueView(final Selector cellSelection,
                                final DataCell cell,
                                final Matrix matrix,
                                final int matrixIndex,
                                final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);
        this.initNominalDataValueView();
    }

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param cell The parent datacell for the nominal data value that this view
     * represents.
     * @param predicate The parent predicate for the nominal data value that
     * this view represents.
     * @param predicateIndex The index of the nominal data value within the
     * above predicate that this view represents.
     * @param editable Is teh data value view editable by the user? True if the
     * value is permitted to be altered by teh user. False otherwise.
     */
    public NominalDataValueView(final Selector cellSelection,
                                final DataCell cell,
                                final PredDataValue predicate,
                                final int predicateIndex,
                                final Matrix matrix,
                                final int matrixIndex,
                                final boolean editable) {
        super(cellSelection, cell, predicate, predicateIndex,
              matrix, matrixIndex, editable);
        this.initNominalDataValueView();
    }

    /**
     * Initalises the nominal data value view.
     */
    private void initNominalDataValueView() {
        reservedChars = new HashMap<Character, Character>();
        reservedChars.put(')', ')');
        reservedChars.put('(', '(');
        reservedChars.put('<', '<');
        reservedChars.put('>', '>');
        reservedChars.put('|', '|');
        reservedChars.put(',', ',');
        reservedChars.put(';', ';');
    }

    /**
     * @return Builds the editor to be used for this data value.
     */
    protected Editor buildEditor() {
        return new NominalEditor();
    }

    /**
     * The editor to use with nominal data value element.
     */
    class NominalEditor extends DataValueElementV.DataValueEditor {

        /**
         * The action to invoke when a key is pressed.
         *
         * @param e The key event that triggered this action.
         */
        @Override
        public void keyPressed(final KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DELETE:
                    // Ignore - handled when the key is typed.
                    e.consume();
                    break;

                case KeyEvent.VK_LEFT:
                    if (getModel().isEmpty()) {
                        e.consume();
                        break;
                    }

                    // If the character two steps to the left is a preserved
                    // character we need to skip one before passing the key
                    // event down to skip again (effectively skipping the
                    // preserved character).
                    for (int i = 0; i < getPreservedChars().size(); i++) {
                        int c = Math.max(0, getCaretPosition() - 2);

                        if (getText().charAt(c) == getPreservedChars().get(i)) {
                            setCaretPosition(Math.max(0,
                                                      getCaretPosition() - 1));
                            break;
                        }
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    // Can't use arrow keys on an empty
                    if (getModel().isEmpty()) {
                        e.consume();
                        break;
                    }

                    // If the character to the right is a preserved character,
                    // we need to skip one before passing the key event down to
                    // skip again (effectively skipping the preserved character)
                    for (int i = 0; i < getPreservedChars().size(); i++) {
                        int c = Math.min(getText().length() - 1,
                                         getCaretPosition() + 1);
                        if (getText().charAt(c) == getPreservedChars().get(i)) {
                            setCaretPosition(Math.min(getText().length() - 1,
                                                      getCaretPosition() + 1));
                            break;
                        }
                    }
                    break;

                case KeyEvent.VK_V:
                    // Depending on platform, check appropriate modifier keys
                    // and paste into timestamp.
                    switch (OpenSHAPA.getPlatform()) {
                        case MAC:
                            if (e.isMetaDown()) {
                                pasteNominal(e);
                            }
                            break;
                        default:
                            if (e.isControlDown()) {
                                pasteNominal(e);
                            }
                            break;
                    }
                    break;

                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_UP:
                    // Key stroke gets passed up a parent element to navigate
                    // cells up and down.
                    break;
                default:
                    break;
            }
        }

        /**
         * Pastes contents of the clipboard into the nominal data value view.
         *
         * @param e The key event that trigged this paste nominal action.
         */
        public final void pasteNominal(final KeyEvent e) {
            // Consume the paste event.
            e.consume();

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

                // Replace reserved characters with a suitable replacement.
                Set<Character> reservedSet = reservedChars.keySet();
                for (Character reservedChar : reservedSet) {
                    text = text.replace(reservedChar, RESERVED_REPLACEMENT);
                }

                // Update the text field and set the new caret position.
                removeSelectedText();
                StringBuffer fieldContents = new StringBuffer(this.getText());
                fieldContents.insert(getCaretPosition(), text);
                storeCaretPosition();
                setText(fieldContents.toString());
                restoreCaretPosition();
                setCaretPosition(getCaretPosition() + text.length());

                // Push the character changes into the database.
                NominalDataValue ndv = (NominalDataValue) getModel();
                ndv.setItsValue(fieldContents.toString());
                updateDatabase();

            } catch (SystemErrorException se) {
                logger.error("Unable to edit text string", se);
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
            NominalDataValue ndv = (NominalDataValue) getModel();

            // The backspace key removes digits from behind the caret.
            if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                       && e.getKeyChar() == '\u0008') {

                // Can't delete an empty nominal data value.
                if (!ndv.isEmpty()) {
                    this.removeBehindCaret();
                    e.consume();
                }

            // The delete key removes digits ahead of the caret.
            } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                       && e.getKeyChar() == '\u007F') {

                // Can't delete an empty nominal data value.
                if (!ndv.isEmpty()) {
                    this.removeAheadOfCaret();
                    e.consume();
                }

            // Just a regular vanilla keystroke - insert it into text field.
            } else if (!e.isMetaDown() && !e.isControlDown()
                       && !reservedChars.containsKey(e.getKeyChar())) {
                this.removeSelectedText();
                StringBuffer currentValue = new StringBuffer(getText());
                currentValue.insert(getCaretPosition(), e.getKeyChar());
                advanceCaret(); // Advance caret over the top of the new char.
                storeCaretPosition();
                this.setText(currentValue.toString());
                restoreCaretPosition();
                e.consume();

            } else {
                e.consume();
            }

            updateDatabase();

            // Push the character changes into the database.
            try {
                ndv.setItsValue(this.getText());
            } catch (SystemErrorException se) {
                logger.error("Unable to edit text string", se);
            }
        }
    }
}
