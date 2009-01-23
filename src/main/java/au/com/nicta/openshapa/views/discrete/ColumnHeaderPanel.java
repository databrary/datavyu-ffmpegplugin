package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.util.UIConfiguration;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * ColumnHeaderPanel displays the column variable name.
 * @author swhitcher
 */
class ColumnHeaderPanel extends JLabel implements Selectable {

    /** Selected state. */
    private boolean selected = false;

    /**
     * Creates new ColumnHeaderPanel.
     * @param text String to display
     */
    public ColumnHeaderPanel(final String text) {
        super(text);

        setOpaque(true);
        setHorizontalAlignment(JLabel.CENTER);
        setBorder(BorderFactory.createLineBorder(Color.black));
        setMinimumSize(new Dimension(202,16));
        setPreferredSize(new Dimension(202,16));
        setMaximumSize(new Dimension(202,16));
    }

    /** set the selected state.
     * @param sel selected state.
     */
    public void setSelected(final boolean sel) {
        selected = sel;
        repaint();
    }

    /**
     * @return selected state.
     */
    public boolean isSelected() {
        return selected;
    }

    /** Toggle the selected state. */
    public void toggleSelected() {
        selected = !selected;
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
}
