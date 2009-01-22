package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.util.JMultilineLabel;
import au.com.nicta.openshapa.util.UIConfiguration;
import java.awt.Graphics;

/**
 * Label view of the Matrix (database cell) data
 *
 * @author swhitcher
*/
public class MatrixViewLabel extends JMultilineLabel {

    /** Matrix that is to be displayed. */
    private Matrix mat     = null;

    /** Configuration font information etc. */
    private static UIConfiguration uiconfig = new UIConfiguration();

    /**
     * Creates a new instance of MatrixViewLabel.
     *
     * @param m The Matrix to display.
    */
    public MatrixViewLabel(final Matrix  m) {
        setMatrix(m);
    }

    /**
     * @param m The Matrix to display.
     */
    public final void setMatrix(final Matrix m) {
        mat = m;

        this.updateStrings();
        this.repaint();
    }

    /**
     * @param width Wrapwidth.
     */
    public final void setWrapWidth(final int width) {
        setMaxWidth(width);
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
    private void updateStrings() {
        if (mat != null) {
            String t = mat.toCellValueString();
            this.setText(t);
            this.setToolTipText(t); // TODO: should tooltip be any different?
        }
    }

}