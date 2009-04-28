package au.com.nicta.openshapa.views.discrete.datavalues;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.PredDataValue;
import au.com.nicta.openshapa.db.Predicate;
import au.com.nicta.openshapa.db.PredicateVocabElement;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.views.discrete.Selector;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JTextField;
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
    private JTextField predicateName;

    private Matrix parentMatrix;

    private int parentIndex;

    /** The data views used for each of the arguments. */
    private Vector<DataValueV> argViews;

    /** The logger for this class. */
    private static Logger logger = Logger
                                   .getLogger(PredicateDataValueView.class);

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
        sheetSelection = cellSelection;
        parentCell = cell;
        parentMatrix = matrix;
        parentIndex = matrixIndex;
        predicateName = new JTextField();

        predicateName.addKeyListener(new KeyListener() {
            /**
             * The action to invoke when a key is released.
             *
             * @param e The KeyEvent that triggered this action.
             */
            public void keyReleased(final KeyEvent e) {
                matchPredicates();
                revalidate();
                repaint();
            }

            /**
             * The action to invoke when a key is typed.
             *
             * @param e The KeyEvent that triggered this action.
             */
            public void keyTyped(final KeyEvent e) {
                matchPredicates();
                revalidate();
                repaint();
            }

            /**
             * The action to invoke when a key is pressed.
             *
             * @param e The KeyEvent that triggered this action.
             */
            public void keyPressed(final KeyEvent e) {
                revalidate();
                repaint();
            }
        });
        this.setPredicate();
    }

    public void setPredicate() {
        try {
            PredDataValue pdv = (PredDataValue) parentMatrix.getArgCopy(parentIndex);
            Predicate p = pdv.getItsValue();

            /*
            // If this matrixView does not contain any components build up
            // view representations for each of the arguments.
            if (m != null && getComponentCount() == 0) {
                // For each of the matrix arguments, build a view representation
                for (int i = 0; i < m.getNumArgs(); i++) {
                    argViews.add(DataValueViewFactory.build(sheetSelection,
                                                            parentCell, m, i));
                }
            }*/

            predicateName.setText(p.getPredName());
            this.add(predicateName);
            this.revalidate();
            this.repaint();

        } catch (SystemErrorException se) {
            logger.error("Unable to set predicate.", se);
        }
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

    public void matchPredicates() {
        try {
            Database db = OpenSHAPA.getDatabase();
            Vector<PredicateVocabElement> pves = db.getPredVEs();

            for (int i = 0; i < pves.size(); i++) {
                if (pves.get(i).getName().equals(predicateName.getText())) {
                    // W00t we match - expand the arguments.
                    this.add(new JTextField("("));

                    predicateName.requestFocus();
                    return;
                }
            }

        } catch (SystemErrorException se) {
            logger.error("Unable to match predicate names", se);
        }
    }
}
