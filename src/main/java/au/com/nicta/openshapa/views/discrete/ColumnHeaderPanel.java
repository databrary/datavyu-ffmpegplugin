package au.com.nicta.openshapa.views.discrete;

import java.awt.Dimension;
import javax.swing.JLabel;

/**
 * ColumnHeaderPanel utility class will need extension to a "ColumnHeader"
 * Provides JLabel that knows the colID it comes from in the db.
 * @author swhitcher
 */
class ColumnHeaderPanel extends JLabel {

    /**
     * Creates new ColumnHeaderPanelold.
     * @param text String to display
     */
    public ColumnHeaderPanel(final String text) {
        super(text);

        setOpaque(true);
       // nameLabel.setHorizontalTextPosition(JLabel.CENTER);
        setHorizontalAlignment(JLabel.CENTER);
        // nameLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        setMinimumSize(new Dimension(200,14));
        setPreferredSize(new Dimension(200,14));
        setMaximumSize(new Dimension(200,14));
    }

}
