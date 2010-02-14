package org.openshapa.views.discrete.datavalues;

import com.usermetrix.jclient.UserMetrix;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.openshapa.models.db.DataCell;
import org.openshapa.views.discrete.Selector;
import javax.swing.JTextField;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.views.discrete.datavalues.TimeStampDataValueEditor.TimeStampSource;

/**
 * JTextArea view of the Matrix (database cell) data.
 */
public final class TimeStampTextField extends JTextField
implements FocusListener, KeyListener {

    /** The selection (used for cells) for the parent spreadsheet. */
    private Selector sheetSelection;

    /** The parent cell for this JPanel. */
    private long parentCell = -1;

    /** The editors that make up the representation of the data. */
    private TimeStampDataValueEditor myEditor;

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix
            .getInstance(TimeStampTextField.class);

    /**
     * Creates a new instance of MatrixV.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param cell The parent datacell for this spreadsheet cell.
     * @param tsType Which TimeStamp of the cell to display.
     * represent.
     */
    public TimeStampTextField(final Selector cellSelection,
                   final DataCell cell,
                   final TimeStampSource tsType) {
        super();

        sheetSelection = cellSelection;
        parentCell = cell.getID();

        myEditor = new TimeStampDataValueEditor(this, cell, tsType);

        setValue();
        // Set visual appearance.
        setBorder(null);
        setOpaque(false);

        this.addFocusListener(this);
        this.addKeyListener(this);
    }

    /**
     * Sets the value to be displayed.
     */
    public void setValue() {
        myEditor.resetValue();

        rebuildText();
    }

    /**
     * Recalculates and sets the text to display.
     */
    public void rebuildText() {
        int pos = this.getCaretPosition();
        setText(myEditor.getText());
        this.setCaretPosition(pos);
    }

    /**
     * The action to invoke if the focus is gained by this MatrixRootView.
     * @param fe The Focus Event that triggered this action.
     */
    public void focusGained(final FocusEvent fe) {
        try {
            // BugzID:320 Deselect Cells before selecting cell contents.
            if (sheetSelection != null) {
                sheetSelection.deselectAll();
                sheetSelection.deselectOthers();
            }

            // We need to remember which cell should be duplicated if the user
            // presses the enter key or selects New Cell from the menu.
            if (parentCell != -1) {
                // method names don't reflect usage - we didn't really create this
                // cell just now.
                DataCell c = (DataCell) OpenSHAPA.getProject().getDB()
                                                 .getCell(parentCell);
                OpenSHAPA.getProject().setLastCreatedColId(c.getItsColID());
                OpenSHAPA.getProject().setLastSelectedCellId(parentCell);
            }

            myEditor.focusGained(fe);
        } catch (SystemErrorException se) {
            logger.error("Unable to gain focus", se);
        }
    }

    /**
     * The action to invoke if the focus is lost.
     * @param fe The FocusEvent that triggered this action.
     */
    public void focusLost(final FocusEvent fe) {
        myEditor.focusLost(fe);
    }

    /**
     * Process key events that have been dispatched to this component, pass
     * them through to all listeners, and then if they are not consumed pass
     * it onto the parent of this component.
     *
     * @param ke They keyboard event that was dispatched to this component.
     */
    @Override
    public void processKeyEvent(final KeyEvent ke) {

        super.processKeyEvent(ke);

        if (!ke.isConsumed() || ke.getKeyCode() == KeyEvent.VK_UP
            || ke.getKeyCode() == KeyEvent.VK_DOWN) {
            getParent().dispatchEvent(ke);
        }
    }

    /**
     * The action to invoke when a key is released.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyReleased(final KeyEvent e) {
        resetEditorText();
        myEditor.keyReleased(e);
    }

    /**
     * The action to invoke when a key is typed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyTyped(final KeyEvent e) {
        myEditor.keyTyped(e);
    }

    /**
     * The action to invoke when a key is pressed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {

            case KeyEvent.VK_ENTER:
                if (!myEditor.isReturnKeyAccepted()) {
                    // help out the editors that don't want the return key
                    if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_STANDARD) {
                        e.consume();
                    }
                }
                break;
            case KeyEvent.VK_TAB:
                myEditor.selectAll();
                e.consume();
                break;

            default:
                break;
        }

        if (!e.isConsumed()) {
            myEditor.keyPressed(e);
        }
    }

    /**
     * Calculate the currentEditor's text and call it's resetText method.
     */
    public void resetEditorText() {
        myEditor.resetText(getText());
    }

    /**
     * Cuts the current selection from the current time stam field into the
     * clipboard.
     */
    @Override
    public void cut() {
        myEditor.cut();
    }

    /**
     * Pastes contents of the clipboard into the time stamp text field.
     */
    @Override
    public void paste() {
        myEditor.paste();
    }

    /**
     * Override to address bug(?) in JTextField see java bug id 4446522
     * for discussion. Probably not the final answer but resolves the
     * clipping of first character displayed.
     * @return the dimension of this textfield
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension size = new Dimension(super.getPreferredSize());
        size.width += 2;
        return size;
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension mysize = super.getPreferredSize();
        return new Dimension(mysize.width, mysize.height);
    }

}