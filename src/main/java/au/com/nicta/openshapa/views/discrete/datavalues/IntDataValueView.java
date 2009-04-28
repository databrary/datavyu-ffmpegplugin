package au.com.nicta.openshapa.views.discrete.datavalues;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.IntDataValue;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.views.discrete.Editor;
import au.com.nicta.openshapa.views.discrete.Selector;
import java.awt.event.KeyEvent;

/**
 * This class is the view representation of a IntDataValue as stored within the
 * database.
 *
 * @author cfreeman
 */
public final class IntDataValueView extends DataValueElementV {

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param cell The parent datacell for the int data value that this view
     * represents.
     * @param matrix The parent matrix for the int data value that this view
     * represents.
     * @param matrixIndex The index of the IntDataValue within the above parent
     * matrix that this view represents.
     * @param editable Is the dataValueView editable by the user? True if the
     * value is permitted to be altered by the user. False otherwise.
     */
    public IntDataValueView(final Selector cellSelection,
                            final DataCell cell,
                            final Matrix matrix,
                            final int matrixIndex,
                            final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex, editable);
    }

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param cell The parent cell for the int datavalue view.
     * @param intDataValue The intDataValue that this view represents.
     * @param editable Is this DataValueV editable by the user? True if the
     * value is permitted to be altered by the user. False otherwise.
     */
    public IntDataValueView(final Selector cellSelection,
                            final DataCell cell,
                            final IntDataValue intDataValue,
                            final boolean editable) {
        super(cellSelection, cell, intDataValue, editable);
    }

    /**
     * @return Builds the editor to be used for this data value.
     */
    @Override
    protected Editor buildEditor() {
        return new IntEditor();
    }

    /**
     * The editor for the int data value.
     */
    class IntEditor extends DataValueElementV.DataValueEditor {

        /**
         * The action to invoke when a key is typed.
         *
         * @param e The KeyEvent that triggered this action.
         */
        public void keyTyped(final KeyEvent e) {
            IntDataValue idv = (IntDataValue) getValue();

            // '-' key toggles the state of a negative / positive number.
            if ((e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD
                || e.getKeyCode() == KeyEvent.KEY_LOCATION_UNKNOWN)
                && e.getKeyChar() == '-') {

                // Move the caret to behind the - sign, or the front of the
                // number.
                if (idv.getItsValue() < 0) {
                    setCaretPosition(0);
                } else {
                    setCaretPosition(1);
                }

                // Toggle state of a negative / positive number.
                idv.setItsValue(-idv.getItsValue());
                e.consume();

            // The backspace key removes digits from behind the caret.
            } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                       && e.getKeyChar() == '\u0008') {

                // Can't delete empty int datavalue.
                if (!idv.isEmpty()) {
                    this.removeBehindCaret();

                    // Allow the provision of a 'null' value - that will permit
                    // users to transition the cell contents to a '<val>' state.
                    Integer newI = buildValue(this.getText());
                    if (newI != null) {
                        idv.setItsValue(newI);
                    }
                    e.consume();
                }

            // The delete key removes digits ahead of the caret.
            } else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                       && e.getKeyChar() == '\u007F') {

                // Can't delete empty int datavalue.
                if (!idv.isEmpty()) {
                    this.removeAheadOfCaret();

                    // Allow the provision of a 'null' value - that will permit
                    // users to transition the cell contents to a '<val>' state.
                    Integer newI = buildValue(this.getText());
                    if (newI != null) {
                        idv.setItsValue(newI);
                    }
                    e.consume();
                }

            // Key stoke is number - insert number at current caret position.
            } else if (Character.isDigit(e.getKeyChar())) {
                this.removeSelectedText();
                StringBuffer currentValue = new StringBuffer(getText());
                currentValue.insert(getCaretPosition(), e.getKeyChar());
                advanceCaret(); // Advance caret over the top of the new char.
                idv.setItsValue(buildValue(currentValue.toString()));
                e.consume();

            // Every other key stroke is ignored by the float editor.
            } else {
                e.consume();
            }

            updateDatabase();
        }

        /**
         * Builds a new Integer value from a string.
         *
         * @param textField The String that you want to create an Integer from.
         *
         * @return An Integer value that can be used setting the database.
         */
        public Integer buildValue(final String textField) {
            if (textField == null || textField.equals("")) {
                return null;
            } else {
                return new Integer(textField);
            }
        }
    }
}