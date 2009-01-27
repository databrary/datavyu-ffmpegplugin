package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataValue;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author cfreeman
 */
public abstract class DataValueView extends JTextField
implements MouseListener, KeyListener {

    /** The DataValue that this view represents. **/
    private DataValue value = null;

    /**
     * Constructor.
     *
     * @param datavalue the DataValue that this class represents.
     * @param editable Is this DataValueView editable?
     */
    protected DataValueView(final DataValue datavalue, final boolean editable) {
        value = datavalue;
        this.setEditable(editable);
        this.addMouseListener(this);
        //this.addKeyListener(this);

        // No border
        //this.setBorder(null);
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
    }

    /**
     * Processes Mouse Events that have been dispatched this component.
     *
     * @param me The mouse event that was dispatched to this component.
     */
    @Override
    public void processMouseEvent(MouseEvent me) {
        MouseListener[] list = this.getMouseListeners();

        if (this.isEditable()) {
            for (int i = 0; i < list.length; i++) {
                switch (me.getID()) {
                    case MouseEvent.MOUSE_CLICKED:
                        list[i].mouseClicked(me);
                        break;
                    case MouseEvent.MOUSE_ENTERED:
                        list[i].mouseEntered(me);
                        break;
                    case MouseEvent.MOUSE_EXITED:
                        list[i].mouseExited(me);
                        break;
                    case MouseEvent.MOUSE_PRESSED:
                        list[i].mousePressed(me);
                        break;
                    default:
                        list[i].mouseReleased(me);
                }
            }
        }

        me.translatePoint(this.getX(), this.getY());
        
        this.getParent().dispatchEvent(me);
    }
}
