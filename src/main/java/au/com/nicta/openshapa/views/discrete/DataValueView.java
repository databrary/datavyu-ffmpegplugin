package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.DataValue;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.util.UIConfiguration;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
        } catch (SystemErrorException ex) {
            logger.error("Unable to create DataValue View: ", ex);
        }

        initDataValueView(editable);
    }

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param dataValue The dataValue that this view represents.
     * @param editable Is the dataValueView editable by the user? True if the
     * value is permitted to be altered by the user. False otherwise.
     */
    public DataValueView(final Selector cellSelection,
                         final DataValue dataValue,
                         final boolean editable) {
        spreadsheetSelection = cellSelection;
        parentMatrix = null;
        index = -1;
        value = dataValue;
        oldCaretPosition = 0;
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

        oldCaretPosition = Math.min(oldCaretPosition, getText().length());
        setCaretPosition(oldCaretPosition);
    }

    /**
     *
     * @param editable
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
     * Updates the database with the latest value from this DataValueView (i.e.
     * after the user has altered it).
     */
    protected void updateDatabase() {
        try {
            oldCaretPosition = getCaretPosition();
            parentMatrix.replaceArg(index, value);
            parentCell.setVal(parentMatrix);
            parentCell.getDB().replaceCell(parentCell);
        } catch (SystemErrorException ex) {
            logger.error("Unable to update Database: ", ex);
        }
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
        if (this.value != null) {
            String t = value.toString();
            setText(t);
            setToolTipText(value.toString());
        }
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
