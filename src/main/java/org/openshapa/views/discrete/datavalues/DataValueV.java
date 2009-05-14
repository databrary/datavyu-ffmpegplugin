package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.DataValue;
import org.openshapa.db.FormalArgument;
import org.openshapa.db.Matrix;
import org.openshapa.db.MatrixVocabElement;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.Predicate;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.discrete.Selector;
import org.openshapa.views.discrete.SpreadsheetElementPanel;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import org.apache.log4j.Logger;
import org.openshapa.Configuration;

/**
 * This abstract view is a representation of database DataValues, concrete views
 * for each of the concrete DataValues exist.
 */
public abstract class DataValueV extends SpreadsheetElementPanel
implements MouseListener {

    /** The selection (used for cells) for the parent spreadsheet. */
    private Selector spreadsheetSelection;

    /** The parent matrix for the DataValue that this view represents.*/
    private Matrix parentMatrix;

    /** The parent predicate for the DataValue that this view represents. */
    private PredDataValue parentPredicate;

    /** The DataValue that this view represents. **/
    private DataValue model = null;

    /** The parent datacell for the DataValue that this view represents. */
    private DataCell parentCell;

    /** The index of the datavalue within its parent matrix. */
    private int mIndex;

    /** The index of the data value within its parent predicate. */
    private int pIndex;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(DataValueV.class);

    /**
     * Constructor.
     */
    public DataValueV(final DataValue value) {
        spreadsheetSelection = null;
        parentMatrix = null;
        parentPredicate = null;
        parentCell = null;
        mIndex = 0;
        model = value;

        initDataValueView();
    }

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param dataCell The parent dataCell for this dataValueView.
     * @param matrix The parent matrix for this dataValueView.
     * @param matrixIndex The index of the DataValue within the parent matrix
     * that we want this view to represent.
     */
    public DataValueV(final Selector cellSelection,
                      final DataCell dataCell,
                      final Matrix matrix,
                      final int matrixIndex) {
        super();
        try {
            spreadsheetSelection = cellSelection;
            parentMatrix = matrix;
            parentPredicate = null;
            parentCell = dataCell;
            mIndex = matrixIndex;
            model = matrix.getArgCopy(mIndex);
        } catch (SystemErrorException ex) {
            logger.error("Unable to create DataValue View: ", ex);
        }

        initDataValueView();
    }

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param dataCell The parent dataCell for this dataValueView.
     * @param predicate The parent predicate for this dataValueView.
     * @param predicateIndex The index of the DataValue within the parent
     * predicate that we want this view to represent.
     */
    public DataValueV(final Selector cellSelection,
                      final DataCell dataCell,
                      final PredDataValue predicate,
                      final int predicateIndex,
                      final Matrix matrix,
                      final int matrixIndex) {
        super();
        try {
            spreadsheetSelection = cellSelection;
            parentMatrix = matrix;
            mIndex = matrixIndex;
            parentPredicate = predicate;
            parentCell = dataCell;
            pIndex = predicateIndex;

            Predicate p = predicate.getItsValue();
            model = p.getArgCopy(pIndex);
        } catch (SystemErrorException ex) {
            logger.error("Unable to create DataValue view: ", ex);
        }
    }

    /**
     * Constructor.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param dataCell The parent dataCell for this dataValueView.
     * @param dataValue The dataValue that this view represents.
     */
    public DataValueV(final Selector cellSelection,
                      final DataCell dataCell,
                      final DataValue dataValue) {
        spreadsheetSelection = cellSelection;
        parentMatrix = null;
        parentPredicate = null;
        parentCell = dataCell;
        mIndex = -1;
        model = dataValue;

        initDataValueView();
    }

    /**
     * Override to address bug(?) in JTextField see java bug id 4446522
     * for discussion. Probably not the final answer but resolves the
     * clipping of first character displayed.
     * @return the dimension of this textfield
     */
    @Override
    public final Dimension getPreferredSize() {
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
     * @param predicate The parent predicate for the datavalue that this view
     * represents.
     * @param predicateIndex The index of the data value in the above predicate
     * that this view represents.
     * @param matrix The parent matrix for the data value that this view
     * represents.
     * @param matrixIndex The index of the data value we wish to have this view
     * represent within the parent matrix.
     */
    public void setValue(final DataCell dataCell,
                         final PredDataValue predicate,
                         final int predicateIndex,
                         final Matrix matrix,
                         final int matrixIndex) {
        parentCell = dataCell;
        parentPredicate = predicate;
        pIndex = predicateIndex;
        parentMatrix = matrix;
        mIndex = matrixIndex;

        updateStrings();
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
        mIndex = matrixIndex;

        updateStrings();
    }

    /**
     * Initalises the datavalue view by registering listeners and setting the
     * appearance.
     */
    private void initDataValueView() {
        // Add listeners.
        addMouseListener(this);

        // Set visual appearance.
        setBorder(null);
        setOpaque(false);

        setFont(Configuration.getInstance().getSSDataFont());
        setForeground(Configuration.getInstance().getSSForegroundColour());
    }

    /**
     * Updates the database with the latest value from this DataValueV (i.e.
     * after the user has altered it).
     */
    public void updateDatabase() {
        try {
            // Update the OpenSHAPA database with the latest values.
            if (parentMatrix != null && parentPredicate == null) {
                parentMatrix.replaceArg(mIndex, model);
            } else if (parentMatrix != null && parentPredicate != null) {

                Predicate p = parentPredicate.getItsValue();
                p.replaceArg(pIndex, model);
                parentPredicate.setItsValue(p);
                parentMatrix.replaceArg(mIndex, parentPredicate);
            }

            parentCell.setVal(parentMatrix);
            parentCell.getDB().replaceCell(parentCell);
        } catch (SystemErrorException ex) {
            logger.error("Unable to update Database: ", ex);
        }
    }

    /**
     * Updates the content of this DataValueV as displayed to the user.
     */
    public abstract void updateStrings();

    /**
     * @return The parent cell that this view represents some element of.
     */
    public final DataCell getParentCell() {
        return parentCell;
    }

    /**
     * @return The model that this data value view represents.
     */
    public final DataValue getModel() {
        return this.model;
    }

    /**
     * @return The parent matrix for this data value view.
     */
    public final Matrix getParentMatrix() {
        return this.parentMatrix;
    }

    /**
     * @return The spreadsheet selector used for this data value view.
     */
    public final Selector getSelector() {
        return spreadsheetSelection;
    }

    /**
     * @return The displayable version of the null argument.
     */
    public final String getNullArg() {
        String t = "";
        try {
            long mveid = parentMatrix.getMveID();
            MatrixVocabElement mve = parentMatrix.getDB().getMatrixVE(mveid);
            FormalArgument fa = mve.getFormalArg(mIndex);
            t = fa.toString();
        } catch (SystemErrorException e) {
            logger.error("Unable to get NULL arg", e);

        }
        return t;
    }

    /**
     * The action to invoke when the mouse enters this component.
     *
     * @param me The mouse event that triggered this action.
     */
    @Override
    public final void mouseEntered(final MouseEvent me) {
        // Currently we do nothing with the mouse entered event.
    }

    /**
     * The action to invoke when the mouse exits this component.
     *
     * @param me The mouse event that triggered this action.
     */
    @Override
    public final void mouseExited(final MouseEvent me) {
        // Currently we do nothing with the mouse exited event.
    }

    /**
     * The action to invoke when a mouse button is pressed.
     *
     * @param me The mouse event that triggered this action.
     */
    @Override
    public final void mousePressed(final MouseEvent me) {
        // Currently we do nothing with the mouse pressed event.
    }

    /**
     * The action to invoke when a mouse button is released.
     *
     * @param me The mouse event that triggered this action.
     */
    @Override
    public final void mouseReleased(final MouseEvent me) {
        // Currently we do nothing with the mouse released event.
    }

    /**
     * The action to invoke when a mouse button is clicked.
     *
     * @param me The mouse event that triggered this action.
     */
    @Override
    public abstract void mouseClicked(MouseEvent me);
}
