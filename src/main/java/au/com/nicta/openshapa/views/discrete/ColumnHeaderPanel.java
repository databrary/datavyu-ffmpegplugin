package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.util.UIConfiguration;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * ColumnHeaderPanel displays the column variable name.
 * @author swhitcher
 */
class ColumnHeaderPanel extends JLabel implements Selectable, MouseListener {

    /** Selected state. */
    private boolean selected = false;

    /** SpreadsheetColumn this header is part of. */
    private SpreadsheetColumn parentCol;

    /** The current column selection. */
    private Selector selection;

    /**
     * Creates new ColumnHeaderPanel.
     * @param col SpreadsheetColumn this header is part of.
     * @param text String to display
     * @param selector The selection for all columns.
     */
    public ColumnHeaderPanel(final SpreadsheetColumn col,
                             final String text,
                             final Selector selector) {
        super(text);

        parentCol = col;

        setOpaque(true);
        setHorizontalAlignment(JLabel.CENTER);
        setBorder(BorderFactory.createLineBorder(Color.black));
        setMinimumSize(new Dimension(202,16));
        setPreferredSize(new Dimension(202,16));
        setMaximumSize(new Dimension(202,16));
        this.addMouseListener(this);
        selection = selector;
    }

    /**
     * Selectable Implementation.
     */

    /** set the selected state.
     * @param sel selected state.
     */
    public void setSelected(final boolean sel) {
        selected = sel;
        parentCol.setSelected(selected);
        repaint();
    }

    /**
     * @return selected state.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Paint the SpreadsheetCell.
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    public void paintComponent(final Graphics g) {
        if (selected) {
            setBackground(UIConfiguration.spreadsheetSelectedColor);
        } else {
            setBackground(UIConfiguration.spreadsheetBackgroundColor);
        }
        super.paintComponent(g);
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
        selection.addToSelection(me, this);
    }
}
