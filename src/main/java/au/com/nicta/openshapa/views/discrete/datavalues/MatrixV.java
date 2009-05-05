package au.com.nicta.openshapa.views.discrete.datavalues;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.util.UIConfiguration;
import au.com.nicta.openshapa.views.discrete.Selector;
import au.com.nicta.openshapa.views.discrete.SpreadsheetElementPanel;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import org.apache.log4j.Logger;

/**
 * Label view of the Matrix (database cell) data.
 *
 * @author swhitcher
*/
public class MatrixV extends SpreadsheetElementPanel {

    /** The selection (used for cells) for the parent spreadsheet. */
    private Selector sheetSelection;

    /** The parent cell for this JPanel. */
    private DataCell parentCell = null;

    /** The data views used for each of the arguments. */
    private Vector<DataValueV> argViews;

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(MatrixV.class);

    /**
     * Creates a new instance of MatrixV.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param cell The parent datacell for this spreadsheet cell.
     * @param matrix The Matrix holding datavalues that this view label will
     * represent.
     */
    public MatrixV(final Selector cellSelection,
                   final DataCell cell,
                   final Matrix matrix) {
        super();

        //FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        //this.setLayout(layout);


        sheetSelection = cellSelection;
        parentCell = cell;
        argViews = new Vector<DataValueV>();
        setMatrix(matrix);
        
        
    }

    /**
     * @return True if this matrix view is the current focus owner, false
     * otherwise.
     */
    @Override
    public final boolean isFocusOwner() {
        for (int i = 0; i < argViews.size(); i++) {
            if (this.argViews.get(i).isFocusOwner()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Request to focus this matrix view label, focus will be set to the first
     * element in the formal argument list.
     */
    @Override
    public final void requestFocus() {
        if (this.argViews.firstElement() != null) {
            this.argViews.firstElement().requestFocus();
        }
    }

    /**
     * Sets the matrix that this MatrixView will represent.
     *
     * @param m The Matrix to display.
     */
    public final void setMatrix(final Matrix m) {
        try {
            // If this matrixView does not contain any components build up
            // view representations for each of the arguments.
            if (m != null && getComponentCount() == 0) {
                // For each of the matrix arguments, build a view representation
                for (int i = 0; i < m.getNumArgs(); i++) {
                    argViews.add(DataValueViewFactory.build(sheetSelection,
                                                            parentCell, m, i));
                }
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to set Matrix for MatrixViewLabel.", e);
        }

        // If this matrixView does not contain any components. Insert the
        // components for each of the view repsentations.
        if (getComponentCount() == 0) {
            // If we have more than one argument in the matrix - then we need to
            // stack in some additional labels.
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

            // If we have more than one argument in the matrix - then we need to
            // stack in some additional labels.
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
                argViews.get(i).setValue(parentCell, m, i);
            }
        }

        this.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.repaint();
    }

    /**
     * @return The child views of this composite view.
     */
    public final Vector<DataValueV> getChildren() {
        return argViews;
    }
}