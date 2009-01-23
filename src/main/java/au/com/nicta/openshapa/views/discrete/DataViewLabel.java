package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataValue;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author cfreeman
 */
public class DataViewLabel extends JTextField
implements MouseListener, KeyListener {

    /** The DataValue that this view represents. **/
    private DataValue value = null;

    /**
     * Constructor.
     *
     * @param datavalue the DataValue that this class represents.
     * @param editable Is this DataValueView editable?
     */
    public DataViewLabel(final DataValue datavalue, final boolean editable) {
        value = datavalue;
        this.setEditable(editable);
        this.addMouseListener(this);
        this.addKeyListener(this);

        // No border
        this.setBorder(null);
        this.setOpaque(false);
    }

    /**
     * @return The DataValue that this view represents.
     */
    public DataValue getValue() {
        return this.value;
    }

    /**
     * Paints the view to the nominated Graphics context.
     *
     * @param g The graphics context to which this view will be painted.
     */
    @Override
    public void paintComponent(Graphics g) {

      //TODO: Editable fonts, sizes and general spreadsheet apperance.
      //if (this.isdata) {
      //    this.setFont(uiconfig.spreadsheetDataFont);
      //} else {
      //    this.setFont(uiconfig.spreadsheetTimeStampFont);
      //}
      //this.setForeground(uiconfig.spreadsheetForegroundColor);
      //

        super.paintComponent(g);
    }

    public void updateStrings() {
        if (this.value != null) {
            String t = this.value.toString();
            this.setText(t);
            this.setToolTipText(this.value.toString());
        }
    }

    /**
     *
     * @param me
     */
    public void redispatchMouseEvent(MouseEvent me) {
        Container container = this.getParent().getParent();

        Point containerPoint =
            SwingUtilities.convertPoint(this,
                                        me.getPoint(),
                                        container);

        container.dispatchEvent(new MouseEvent(container,
                                               me.getID(),
                                               me.getWhen(),
                                               me.getModifiers(),
                                               containerPoint.x,
                                               containerPoint.y,
                                               me.getClickCount(),
                                               me.isPopupTrigger()));
    }

    public void keyPressed(KeyEvent e) {
        e.consume();
    }

    public void keyReleased(KeyEvent e) {
        e.consume();
    }

    public void keyTyped(KeyEvent e) {
        e.consume();
    }

    public void mouseEntered(MouseEvent me) {
        this.redispatchMouseEvent(me);
    }

    public void mouseExited(MouseEvent me) {
        this.redispatchMouseEvent(me);
    }

    public void mousePressed(MouseEvent me) {
        if (!this.isEditable()) {
            this.redispatchMouseEvent(me);
        }
    }

    public void mouseReleased(MouseEvent me) {
        if (!this.isEditable()) {
            this.redispatchMouseEvent(me);
        }
    }

    public void mouseClicked(MouseEvent me) {
        if (!this.isEditable()) {
            this.redispatchMouseEvent(me);
        }
    }
}
