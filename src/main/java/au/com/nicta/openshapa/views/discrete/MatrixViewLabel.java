package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.SystemErrorException;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import org.apache.log4j.Logger;

/**
 * Label view of the Matrix (database cell) data.
 *
 * @author swhitcher
*/
public class MatrixViewLabel extends SpreadsheetPanel {

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
        //super(true);
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
            this.add(new JLabel("("));
            //this.add(new JLabel(""))
        }

        // Build the visual representation of this matrix.
        for (int i = 0; i < argViews.size(); i++) {
            DataValueView dv = argViews.get(i);

            if (dv != null) {
                this.add(dv);
                dv.updateStrings();
            }

            if (argViews.size() > 1 && i < argViews.size()) {
                this.add(new JLabel(","));
            }
        }

        // If we have more than one argument in the matrix - then we need to
        // stack in some additional labels.
        if (argViews.size() > 1) {
            this.add(new JLabel(")"));
        }

        this.setBorder(BorderFactory.createEtchedBorder());

        //this.updateStrings();
        this.repaint();
    }

    /**
     * @param width Wrapwidth.
     */
    public final void setWrapWidth(final int width) {
        //setMaxWidth(width);
    }

    /**
     * @return The Matrix being displayed.
     */
    public final Matrix getMatrix() {
        return (mat);
    }

    /**
     * Paint the MatrixViewLabel.
     *
     * @param g Graphics associated.
     */
    @Override
    public final void paintComponent(final Graphics g) {
        this.setFont(this.getParent().getFont());
        this.setForeground(this.getParent().getForeground());
        this.setBackground(this.getParent().getBackground());

        super.paintComponent(g);
    }

    /**
     * Calculate and set what to display.
     */
    //@Override
    /*
    public void updateStrings() {
        if (mat != null) {
            String t = mat.toCellValueString();
            this.setText(t);
            this.setToolTipText(t); // TODO: should tooltip be any different?
        }
    }
    */

    /**
     *
     * @param e
     */
    public void keyPressed(KeyEvent e) {
        //this.handleKeyEvent(e);
    }

    /**
     *
     * @param e
     */
    public void keyTyped(KeyEvent e) {
        //this.handleKeyEvent(e);
    }

    /**
     *
     * @param e
     */
    public void keyReleased(KeyEvent e) {
        //this.handleKeyEvent(e);
    }

}