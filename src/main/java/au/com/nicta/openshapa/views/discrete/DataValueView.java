package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.DataValue;
import au.com.nicta.openshapa.db.FormalArgument;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.MatrixVocabElement;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.util.UIConfiguration;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import javax.swing.JTextField;
import org.apache.log4j.Logger;

/**
 * This abstract view is a representation of database DataValues, concrete views
 * for each of the concrete DataValues exist.
 *
 * @author cfreeman
 */
public abstract class DataValueView extends JTextField
implements MouseListener, KeyListener, FocusListener {

    /** The selection (used for cells) for the parent spreadsheet. */
    private Selector spreadsheetSelection;

    /** The parent matrix for the DataValue that this view represents.*/
    private Matrix parentMatrix;

    /** The DataValue that this view represents. **/
    private DataValue value = null;

    /** The parent datacell for the DataValue that this view represents. */
    private DataCell parentCell;

    /** The index of the datavalue within its parent matrix. */
    private int index;

    /** The last caret position. */
    private int oldCaretPosition;

    /** Should the oldCaretPosition be advanced by a single position? */
    private boolean advanceCaret;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(DataValueView.class);

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param dataCell The parent dataCell for this dataValueView.
     * @param matrix The parent matrix for this dataValueView.
     * @param matrixIndex The index of the DataValue within the parent matrix
     * that we want this view to represent.
     * @param editable Is the dataValueView editable by the user? True if the
     * value is permitted to be altered by the user. False otherwise.
     */
    public DataValueView(final Selector cellSelection,
                         final DataCell dataCell,
                         final Matrix matrix,
                         final int matrixIndex,
                         final boolean editable) {
        super();
        try {
            spreadsheetSelection = cellSelection;
            parentMatrix = matrix;
            parentCell = dataCell;
            index = matrixIndex;
            value = matrix.getArgCopy(index);
            oldCaretPosition = 0;
            advanceCaret = false;
        } catch (SystemErrorException ex) {
            logger.error("Unable to create DataValue View: ", ex);
        }

        initDataValueView(editable);
    }

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param dataCell The parent dataCell for this dataValueView.
     * @param dataValue The dataValue that this view represents.
     * @param editable Is the dataValueView editable by the user? True if the
     * value is permitted to be altered by the user. False otherwise.
     */
    public DataValueView(final Selector cellSelection,
                         final DataCell dataCell,
                         final DataValue dataValue,
                         final boolean editable) {
        spreadsheetSelection = cellSelection;
        parentMatrix = null;
        parentCell = dataCell;
        index = -1;
        value = dataValue;
        oldCaretPosition = 0;
        advanceCaret = false;
        initDataValueView(editable);
    }

    /**
     * Sets the value of this view, i.e. the DataValue that this view will
     * represent.
     *
     * @param dataCell The parent dataCell for the DataValue that this view
     * represents.
     * @param matrix The parent matrix for the DataValue that this view
     * represents.
     * @param matrixIndex The index of the dataValue we wish to have this view
     * represent within the parent matrix.
     */
    public void setValue(final DataCell dataCell,
                         final Matrix matrix,
                         final int matrixIndex) {
        parentCell = dataCell;
        parentMatrix = matrix;
        index = matrixIndex;

        updateStrings();
        restoreCaretPosition();
    }

    /**
     * Restores the caret position to the last stored position. Use
     * storeCaretPosition() before calling this method.
     */
    protected void restoreCaretPosition() {
        oldCaretPosition = Math.min(oldCaretPosition, getText().length());
        setCaretPosition(oldCaretPosition);
        advanceCaret = false;   // reset the advance caret flag - only applies
                                // once per database update. Database update
                                // triggers this method via a listener.
    }

    /**
     * Initalises the datavalue view by registering listeners and setting the
     * appearance.
     *
     * @param editable is this view editable or not, true if hte data view is
     * editable, false otherwise.
     */
    private void initDataValueView(final boolean editable) {        
        setEditable(editable);

        // Add listeners.
        addMouseListener(this);
        addKeyListener(this);
        addFocusListener(this);

        // Set visual appearance.
        setBorder(null);
        setOpaque(false);

        // Set the content.
        updateStrings();
    }

    /**
     * Set a flag to advanceCaret, when updateDatabase is called the
     * oldCaretPosition will be advanced by one position. When set value is
     * called back by the listeners it will reset this flag.
     */
    protected void advanceCaret() {
        advanceCaret = true;
    }

    /**
     * Stores the currentCaretPosition, a call to restoreCaretPosition() can be
     * used to restore the caret position to the save point generated by this
     * method.
     */
    protected void storeCaretPosition() {
        // Character inserted - advance the caret position.
        oldCaretPosition = getCaretPosition();
        if (advanceCaret) {
            oldCaretPosition++;
        }
    }

    /**
     * Updates the database with the latest value from this DataValueView (i.e.
     * after the user has altered it).
     */
    protected void updateDatabase() {
        try {
            storeCaretPosition();

            // Update the OpenSHAPA database with the latest values.
            parentMatrix.replaceArg(index, value);
            parentCell.setVal(parentMatrix);
            parentCell.getDB().replaceCell(parentCell);
        } catch (SystemErrorException ex) {
            logger.error("Unable to update Database: ", ex);
        }
    }

    /**
     * @return The parent cell that this view represents some element of.
     */
    public DataCell getParentCell() {
        return parentCell;
    }

    /**
     * @return The DataValue that this view represents.
     */
    public DataValue getValue() {
        return value;
    }

    /**
     * Paints the view to the nominated Graphics context.
     *
     * @param g The graphics context to which this view will be painted.
     */
    @Override
    public void paintComponent(Graphics g) {
        setFont(UIConfiguration.spreadsheetDataFont);
        setForeground(UIConfiguration.spreadsheetForegroundColor);

        //TODO: Editable fonts, sizes and general spreadsheet apperance.
        //if (this.isdata) {
        //    this.setFont(uiconfig.spreadsheetDataFont);
        //} else {
        //    this.setFont(uiconfig.spreadsheetTimeStampFont);
        //}
        //this.setForeground(uiconfig.spreadsheetForegroundColor);
        //

        //updateStrings();
        super.paintComponent(g);
    }

    /**
     * Updates the content of this DataValueView as displayed to the user.
     */
    public void updateStrings() {
        String t = "";
        if (this.value != null && !this.value.isDefault()) {
            t = value.toString();
        } else if (parentMatrix != null) {
            t = getNullArg();
        }
        setText(t);
    }

    private String getNullArg() {
        String t = "";
        try {
            long mveid = parentMatrix.getMveID();
            MatrixVocabElement mve = parentMatrix.getDB().getMatrixVE(mveid);
            FormalArgument fa = mve.getFormalArg(index);
            t = fa.toString();
        } catch (SystemErrorException e) {
            logger.error("Unable to get NULL arg", e);

        }
        return t;
    }

    /**
     * The action to invoke if the focus is gained by this DataValueView.
     *
     * @param fe The Focus Event that triggered this action.
     */
    public void focusGained(FocusEvent fe) {
        // Deselect all cells before selecting the contents of a cell.
        // BugzID:230
        spreadsheetSelection.deselectAll();
        spreadsheetSelection.deselectOthers();
        this.selectAll();
    }

    /**
     * The action to invoke if the focus is lost from this DataValueView.
     *
     * @param fe The FocusEvent that triggered this action.
     */
    public void focusLost(FocusEvent fe) {
    }

    /**
     * The action to invoke when the mouse enters this component.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseEntered(MouseEvent me) {
    }

    /**
     * The action to invoke when the mouse exits this component.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseExited(MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is pressed.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mousePressed(MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is released.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseReleased(MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is clicked.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseClicked(MouseEvent me) {
    }

    /**
     * Process key events that have been dispatched to this component, pass them
     * through to all listeners, and then if they are not consumed pass it onto
     * the parent of this component.
     *
     * @param ke They keyboard event that was dispatched to this component.
     */
    @Override
    public void processKeyEvent(KeyEvent ke) {
        super.processKeyEvent(ke);

        if (!ke.isConsumed() || ke.getKeyCode() == KeyEvent.VK_UP
            || ke.getKeyCode() == KeyEvent.VK_DOWN) {
            this.getParent().dispatchEvent(ke);
        }
    }

    /**
     * Processes Mouse Events that have been dispatched this component.
     *
     * @param me The mouse event that was dispatched to this component.
     */
    @Override
    public void processMouseEvent(MouseEvent me) {
        super.processMouseEvent(me);

        if (!this.isEditable() && !me.isConsumed()) {
            me.translatePoint(this.getX(), this.getY());
            this.getParent().dispatchEvent(me);
        }
    }

    /**
     * Removes characters from ahead of the caret if they are not in the
     * preservedChars parameter. If the character is to be preserved, this
     * method will simple shift the caret forward one spot.
     *
     * @param preservedChars A list of characters that are to be preserved,
     * these characters will NOT be removed from ahead of the caret regardless
     * of if they have been selected or not.
     */
    protected void removeAheadOfCaret(Vector<Character> preservedChars) {
        // Underlying text field has selection no caret, remove everything that
        // is selected.
        if ((getSelectionEnd() - getSelectionStart()) > 0) {
            removeSelectedText(preservedChars);

        // Underlying Text field has no selection, just a caret. Go ahead and
        // manipulate it as such.
        } else {
            boolean deleteChar = true;

            for (int i = 0; i < preservedChars.size(); i++) {
                if (getText().charAt(getCaretPosition())
                    == preservedChars.get(i)) {
                    setCaretPosition(getCaretPosition() + 1);
                    deleteChar = false;
                    break;
                }
            }

            // Character ahead of caret is not preserved. Delete it.
            if (deleteChar) {
                StringBuffer currentValue = new StringBuffer(getText());
                currentValue.delete(getCaretPosition(), getCaretPosition() + 1);
                int cPosition = getCaretPosition();
                this.setText(currentValue.toString());
                setCaretPosition(cPosition);
            }
        }
    }

    /**
     * Removes characters from behind the caret if they are not in the
     * preservedChars parameter. If the character is to be preserved, this
     * method will simply shift the caret back one spot.
     *
     * @param preservedChars A list of characters that are to be preserved,
     * these characters will NOT be removed from the behind the caret regardless
     * of if they have been selected or not.
     */
    protected void removeBehindCaret(Vector<Character> preservedChars) {
        // Underlying text field has selection and no carret, simply remove
        // everything that is selected.
        if ((getSelectionEnd() - getSelectionStart()) > 0) {
            removeSelectedText(preservedChars);

        // Underlying text field has no selection, just a caret. Go ahead and
        // manipulate it as such.
        } else {
            boolean deleteChar = true;

            // Check behind the caret to see if it is a preserved character. If
            // the character is preserved - simply move the caret back one spot
            // and leave the preserved character untouched.
            for (int i = 0; i < preservedChars.size(); i++) {
                if (getText().charAt(getCaretPosition() - 1)
                    == preservedChars.get(i)) {
                    setCaretPosition(getCaretPosition() - 1);
                    deleteChar = false;
                    break;
                }
            }

            // Character behind caret is not preserved. Delete it.
            if (deleteChar) {
                StringBuffer currentValue = new StringBuffer(getText());
                currentValue.delete(getCaretPosition() - 1, getCaretPosition());
                int cPosition = getCaretPosition() - 1;
                this.setText(currentValue.toString());
                setCaretPosition(cPosition);
            }

            
        }
    }

    /**
     * This method will remove any characters that have been selected in the
     * underlying text field and that don't exist in the preservedChars
     * parameter. If no characters have been selected, the underlying text field
     * is unchanged.
     *
     * @param preservedChars A list of characters that are to be preserved,
     * these characters will NOT be removed from the underlying text field
     * regardless of if they have been selected or not.
     */
    protected void removeSelectedText(Vector<Character> preservedChars) {
        Vector<Character> foundChars = new Vector<Character>();

        // Get the current value of the visual representation of this DataValue.
        String cValue = this.getText();

        // Obtain the start and finish of the selected text.
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();

        for (int i = 0; i < preservedChars.size(); i++) {
            int pIndex = cValue.lastIndexOf(preservedChars.get(i));
            if (pIndex < end && pIndex > start) {
                // A preserved character exists - ensure that it remains.
                foundChars.add(preservedChars.get(i));
            }
        }

        // Create a new value by removing the selected text from current value
        // of the DataValue.
        String nValue = cValue.substring(0, start);
        for (int i = 0; i < foundChars.size(); i++) {
            nValue = nValue.concat(foundChars.get(i).toString());
        }
        nValue = nValue.concat(cValue.substring(end, cValue.length()));

        // Set the text for this data value to the new string.
        this.setText(nValue);
        this.setCaretPosition(start);
    }

    /**
     * Utility method for checking if a keystroke is a number.
     *
     * @param e The key event to check if it is a numeric keystroke.
     *
     * @return true if the keystroke is numeric, false otherwise.
     */
    public boolean isKeyStrokeNumeric(KeyEvent e) {
        switch (e.getKeyChar()) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return true;

            default:
                return false;
        }
    }
}
