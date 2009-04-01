package au.com.nicta.openshapa.views.discrete.datavalues;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.DataValue;
import au.com.nicta.openshapa.db.FormalArgument;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.MatrixVocabElement;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.util.UIConfiguration;
import au.com.nicta.openshapa.views.discrete.Editor;
import au.com.nicta.openshapa.views.discrete.Selector;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import org.apache.log4j.Logger;

/**
 * This abstract view is a representation of database DataValues, concrete views
 * for each of the concrete DataValues exist.
 *
 * @author cfreeman
 */
public abstract class DataValueView extends Editor
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
        initDataValueView(editable);
    }

    /**
     * Override to address bug(?) in JTextField see java bug id 4446522
     * for discussion. Probably not the final answer but resolves the
     * clipping of first character displayed.
     * @return the dimension of this textfield
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width += 1;
        return size;
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
        if (this.value != null && !this.value.isEmpty()) {
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

        // Only select all if the data value view is a placeholder.
        if (value.isEmpty()) {
            this.selectAll();
        }
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
     * The action to invoke when a key is released.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyReleased(KeyEvent e) {
        // Ignore key release.
    }

    /**
     * The action to invoke when a key is pressed.
     *
     * @param e The KeyEvent that triggered this action.
     */
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                // Ignore - handled when the key is typed.
                e.consume();
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                // If the underlying datavalue is null - we disable the left and
                // right arrow keys. So prevent users from being able to 'edit'
                // the placeholder title.
                if (value == null || value.isEmpty()) {
                    e.consume();
                }

                // Move caret left and right (underlying text field handles
                // this).
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
}
