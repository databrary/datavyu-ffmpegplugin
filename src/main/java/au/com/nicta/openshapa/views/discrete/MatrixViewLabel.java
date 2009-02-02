package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.SystemErrorException;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 * Label view of the Matrix (database cell) data.
 *
 * @author swhitcher
*/
public class MatrixViewLabel extends JPanel /*SpreadsheetPanel*/ {

    /** Matrix that is to be displayed. */
    private Matrix mat = null;

    /** The data views used for each of the arguments. */
    private Vector<DataValueView> argViews;

    /** The logger for MatrixViewLabel. */
    private static Logger logger = Logger.getLogger(MatrixViewLabel.class);

    /**
     * Creates a new instance of MatrixViewLabel.
     *
     * @param m The Matrix to display.
    */
    public MatrixViewLabel(final Matrix m) {
        super();
        setMatrix(m);
    }

    /**
     * Sets the matrix that this MatrixView will represent.
     *
     * @param m The Matrix to display.
     */
    public final void setMatrix(final Matrix m) {
        mat = m;
        argViews = new Vector<DataValueView>();        

        try {
            if (m != null) {
                // For each of the matrix arguments, build a view representation
                for (int i = 0; i < m.getNumArgs(); i++) {
                    argViews.add(DataValueViewFactory.build(m.getArgCopy(i)));
                }
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to set Matrix for MatrixViewLabel.", e);
        }

        // If we have more than one argument in the matrix - then we need to
        // stack in some additional labels.
        if (argViews.size() > 1) {
            JLabel label = new JLabel("(");
            this.add(label);
        }

        // Build the visual representation of this matrix.
        for (int i = 0; i < argViews.size(); i++) {
            DataValueView dv = argViews.get(i);

            if (dv != null) {
                this.add(dv);
            }

            if (argViews.size() > 1 && i < (argViews.size() - 1)) {
                this.add(new JLabel(","));
            }
        }

        // If we have more than one argument in the matrix - then we need to
        // stack in some additional labels.
        if (argViews.size() > 1) {
            this.add(new JLabel(")"));
        }

        //this.setBorder(BorderFactory.createEtchedBorder());
        this.repaint();
    }

    /**
     * @return The Matrix being displayed.
     */
    public final Matrix getMatrix() {
        return (mat);
    }

    /**
     *
     * @param e
     */
    /*
    public void keyPressed(KeyEvent e) {
        int moo = 5;
        //this.handleKeyEvent(e);
    }*/

    /**
     *
     * @param e
     */
    /*
    public void keyTyped(KeyEvent e) {
        //this.handleKeyEvent(e);
    }
     */

    /**
     *
     * @param e
     */
    /*
    public void keyReleased(KeyEvent e) {
        //this.handleKeyEvent(e);
    }*/

}