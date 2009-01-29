package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.util.UIConfiguration;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * Label view of the Matrix (database cell) data
 *
 * @author swhitcher
*/
public class MatrixViewLabel extends DataValueView {

    /** Matrix that is to be displayed. */
    private Matrix mat = null;

    /** Configuration font information etc. */
    private static UIConfiguration uiconfig = new UIConfiguration();

    private Vector<DataValueView> argViews;

    /** The logger for MatrixViewLabel. */
    private static Logger logger = Logger.getLogger(MatrixViewLabel.class);

    /**
     * Creates a new instance of MatrixViewLabel.
     *
     * @param m The Matrix to display.
    */
    public MatrixViewLabel(final Matrix m) {
        super(true);
        setMatrix(m);
    }

    /**
     * @param m The Matrix to display.
     */
    public final void setMatrix(final Matrix m) {
        mat = m;
        argViews = new Vector<DataValueView>();

        try {
            if (m != null) {
                for (int i = 0; i < m.getNumArgs(); i++) {
                    argViews.add(DataValueViewFactory.buildDVView(m.getArgCopy(i)));
                }
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to set Matrix for MatrixViewLabel.", e);
        }

        this.updateStrings();
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
        this.setFont(UIConfiguration.spreadsheetDataFont);
        this.setForeground(UIConfiguration.spreadsheetForegroundColor);

        super.paintComponent(g);
    }

    /**
     * Calculate and set what to display.
     */
    @Override
    public void updateStrings() {
        if (mat != null) {
            String t = mat.toCellValueString();
            this.setText(t);
            this.setToolTipText(t); // TODO: should tooltip be any different?
        }
    }

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