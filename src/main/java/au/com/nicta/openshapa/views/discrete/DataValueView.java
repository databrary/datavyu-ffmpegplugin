package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataValue;
import au.com.nicta.openshapa.util.UIConfiguration;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTextField;

/**
 *
 * @author cfreeman
 */
public abstract class DataValueView extends JTextField
implements MouseListener, KeyListener {

    /** The DataValue that this view represents. **/
    private DataValue value = null;

    public DataValueView(final boolean editable) {
        super();
        initDataValueView(editable);
    }

    /**
     * Constructor.
     *
     * @param datavalue the DataValue that this class represents.
     * @param editable Is this DataValueView editable?
     */
    public DataValueView(final DataValue datavalue, final boolean editable) {
        super();
        value = datavalue;
        initDataValueView(editable);
    }

    private void initDataValueView(final boolean editable) {
        setEditable(editable);

        // Add listeners.
        addMouseListener(this);
        addKeyListener(this);

        // Set visual appearance.
        setBorder(null);
        setOpaque(false);

        // Set the content.
        updateStrings();
    }

    /**
     * @return The DataValue that this view represents.
     */
    public DataValue getValue() {
        return value;
    }

    /**
     * Paints the view to the nominated Graphics context.
     *
     * @param g The graphics context to which this view will be painted.
     */
    @Override
    public void paintComponent(Graphics g) {
        setFont(UIConfiguration.spreadsheetDataFont);
        setForeground(UIConfiguration.spreadsheetForegroundColor);

        //TODO: Editable fonts, sizes and general spreadsheet apperance.
        //if (this.isdata) {
        //    this.setFont(uiconfig.spreadsheetDataFont);
        //} else {
        //    this.setFont(uiconfig.spreadsheetTimeStampFont);
        //}
        //this.setForeground(uiconfig.spreadsheetForegroundColor);
        //

        //updateStrings();
        super.paintComponent(g);
    }

    public void updateStrings() {
        if (this.value != null) {
            String t = value.toString();
            setText(t);
            setToolTipText(value.toString());
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
            for (int i = 0; i < list.length && !me.isConsumed(); i++) {
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

        if (!me.isConsumed()) {
            me.translatePoint(this.getX(), this.getY());
            this.getParent().dispatchEvent(me);
        }
    }

    public boolean isKeyStrokeNumeric(KeyEvent e) {
        switch (e.getKeyChar()) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return true;

            default:
                return false;
        }
    }
}
