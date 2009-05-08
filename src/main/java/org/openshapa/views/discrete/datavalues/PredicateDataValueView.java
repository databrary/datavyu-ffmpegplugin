package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.Predicate;
import org.openshapa.db.SystemErrorException;
import org.openshapa.util.UIConfiguration;
import org.openshapa.views.discrete.Selector;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import org.apache.log4j.Logger;

/**
 *
 * @author cfreeman
 */
public final class PredicateDataValueView extends DataValueV {

    /** The selection (used for cells) for the parent spreadsheet. */
    private Selector sheetSelection;

    /** The parent cell for this JPanel. */
    private DataCell parentCell = null;

    /** The name of the predicate. */
    private PredicateNameV predicateName;

    /** The parent matrix for this predicate. */
    private Matrix parentMatrix;

    /** The index of the parent within the database. */
    private int parentIndex;

    /** The data views used for each of the arguments. */
    private Vector<DataValueV> argViews;

    /** The logger for this class. */
    private static Logger logger = Logger
                                   .getLogger(PredicateDataValueView.class);


    public PredDataValue getPredDataValue() {
        try {
            return (PredDataValue) parentMatrix.getArgCopy(parentIndex);
        } catch (SystemErrorException se) {
            return null;
        }
    }

    public Predicate getPredicate() {
        try {
            PredDataValue pdv = (PredDataValue) parentMatrix
                                                .getArgCopy(parentIndex);
            return pdv.getItsValue();
        } catch (SystemErrorException se) {
            return null;
        }
    }

    /**
     * Constructor.
     *
     * @param cellSelection The parent cell selection.
     * @param cell The parent cell for this value timestamp.
     * @param matrix The parent matrix for which this is will act as a view for
     * one of its formal arguments.
     * @param matrixIndex The index of the formal argument within the parent
     * matrix that this will act as a view for.
     * @param editable Is the datavalue editable or not - true if it is editable
     * false otherwise.
     */
    public PredicateDataValueView(final Selector cellSelection,
                                  final DataCell cell,
                                  final Matrix matrix,
                                  final int matrixIndex,
                                  final boolean editable) {
        super(cellSelection, cell, matrix, matrixIndex);
        argViews = new Vector<DataValueV>();
        sheetSelection = cellSelection;
        parentCell = cell;
        parentMatrix = matrix;
        parentIndex = matrixIndex;
        predicateName = new PredicateNameV(this);
        this.add(predicateName);
        this.setPredicate(this.getPredicate());
    }

    public void clearPreds() {
        while(this.getComponentCount() > 1) {
            this.remove(1);
        }
    }

    public void setPredicate(Predicate p) {
        try {
            PredDataValue pdv = (PredDataValue) parentMatrix
                                                .getArgCopy(parentIndex);
            pdv.setItsValue(p);           

            // If this matrixView does not contain any components build up
            // view representations for each of the arguments.
            if (p != null && getComponentCount() <= 1) {

                // For each of the matrix arguments, build a view representation
                for (int i = 0; i < p.getNumArgs(); i++) {
                    argViews.add(DataValueViewFactory.build(sheetSelection,
                                                            parentCell, pdv, i,
                                                            parentMatrix,
                                                            parentIndex));
                }
            }

            // If this predicate View does not contain any components. Insert
            // the components for each of the view repsentations.
            if (getComponentCount() <= 1) {

                if (argViews.size() > 1) {
                    JLabel label = new JLabel("(");
                    label.setBorder(new EmptyBorder(0, 0, 0, 0));
                    label.setFont(UIConfiguration.spreadsheetDataFont);
                    this.add(label);
                }

                // Build the visual representation of this matrix.
                for (int i = 0; i < argViews.size(); i++) {
                    DataValueV dv = argViews.get(i);

                    if (dv != null) {
                        dv.setBorder(new EmptyBorder(0, 0, 0, 0));
                        this.add(dv);
                    }

                    if (argViews.size() > 1 && i < (argViews.size() - 1)) {
                        JLabel label = new JLabel(",");
                        label.setBorder(new EmptyBorder(0, 0, 0, 5));
                        label.setFont(UIConfiguration.spreadsheetDataFont);
                        this.add(label);
                    }
                }

                // If we have more than one argument in the matrix - then we
                // need to stack in some additional labels.
                if (argViews.size() > 1) {
                    JLabel label = new JLabel(")");
                    label.setBorder(new EmptyBorder(0, 0, 0, 0));
                    label.setFont(UIConfiguration.spreadsheetDataFont);
                    this.add(label);
                }

            // The matrixView does contain components, alter the contents of
            // what already exists.
            } else {
                for (int i = 0; i < argViews.size(); i++) {
                    if (argViews.get(i) != null) {
                        argViews.get(i).setValue(parentCell, pdv, i,
                                                 parentMatrix,
                                                 parentIndex);
                    }
                }
            }

            this.setBorder(new EmptyBorder(0, 0, 0, 0));
            this.revalidate();
            this.repaint();

        } catch (SystemErrorException se) {
            logger.error("Unable to set predicate.", se);
        }
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
    @Override
    public void setValue(final DataCell dataCell,
                         final Matrix matrix,
                         final int matrixIndex) {
        super.setValue(dataCell, matrix, matrixIndex);
        this.setPredicate(this.getPredicate());
        updateStrings();
    }

    /**
     * The action to invoke when a mouse button is clicked.
     *
     * @param me The mouse event that triggered this action.
     */
    @Override
    public void mouseClicked(final MouseEvent me) {
    }

    @Override
    public void updateStrings() {
    }
}
