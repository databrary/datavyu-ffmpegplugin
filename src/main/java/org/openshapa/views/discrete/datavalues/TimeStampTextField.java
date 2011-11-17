/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.views.discrete.datavalues;

import com.usermetrix.jclient.Logger;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.datavalues.TimeStampDataValueEditor.TimeStampSource;

import com.usermetrix.jclient.UserMetrix;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.Variable;

/**
 * JTextArea view of the Matrix (database cell) data.
 */
public final class TimeStampTextField extends JTextField
implements FocusListener, KeyListener {

    /** The parent cell for this JPanel. */
    private Cell parentCell = null;

    /** The editors that make up the representation of the data. */
    private TimeStampDataValueEditor myEditor;

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(TimeStampTextField.class);

    /**
     * Creates a new instance of MatrixV.
     *
     * @param cell The parent datacell for this spreadsheet cell.
     * @param tsType Which TimeStamp of the cell to display. represent.
     */
    public TimeStampTextField(final Cell cell, final TimeStampSource tsType) {
        super();

        parentCell = cell;
        myEditor = new TimeStampDataValueEditor(this, cell, tsType);

        setValue();
        // Set visual appearance.
        setBorder(null);
        setOpaque(false);

        addFocusListener(this);
        addKeyListener(this);
    }

    /**
     * Sets the value to be displayed.
     */
    public void setValue() {
        myEditor.resetValue();

        rebuildText();
    }

    /**
     * Recalculates and sets the text to display.
     */
    public void rebuildText() {
        int pos = getCaretPosition();
        setText(myEditor.getText());
        setCaretPosition(pos);
    }

    /**
     * Calculate the currentEditor's text and call it's resetText method.
     */
    public void resetEditorText() {
        myEditor.resetText(getText());
    }

    // *************************************************************************
    // Parent Class Overrides
    // *************************************************************************
    @Override
    public void cut() {
        myEditor.cut();
    }

    @Override
    public void paste() {
        myEditor.paste();
    }

    /**
     * Override to address bug(?) in JTextField see java bug id 4446522 for
     * discussion. Probably not the final answer but resolves the clipping of
     * first character displayed.
     * 
     * @return the dimension of this textfield
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension size = new Dimension(super.getPreferredSize());
        size.width += 2;
        return size;
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension mysize = super.getPreferredSize();
        return new Dimension(mysize.width, mysize.height);
    }

    // *************************************************************************
    // FocusListener Overrides
    // *************************************************************************
    @Override
    public void focusGained(final FocusEvent fe) {
        // We need to remember which cell should be duplicated if the user
        // presses the enter key or selects New Cell from the menu.
        if (parentCell != null) {
            // method names don't reflect usage - we didn't really create this
            // cell just now.
            Variable var = OpenSHAPA.getProjectController().getDB()
                                    .getVariable(parentCell);

            OpenSHAPA.getProjectController().setLastCreatedVariable(var);
            OpenSHAPA.getProjectController().setLastSelectedCell(parentCell);
        }

        myEditor.focusGained(fe);
    }


    @Override
    public void focusLost(final FocusEvent fe) {
        myEditor.focusLost(fe);
    }

    // *************************************************************************
    // KeyListener Overrides
    // *************************************************************************
    @Override
    public void processKeyEvent(final KeyEvent ke) {
        super.processKeyEvent(ke);

        if (!ke.isConsumed() || ke.getKeyCode() == KeyEvent.VK_UP
                || ke.getKeyCode() == KeyEvent.VK_DOWN) {
            getParent().dispatchEvent(ke);
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        resetEditorText();
        myEditor.keyReleased(e);
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        myEditor.keyTyped(e);
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (!myEditor.isReturnKeyAccepted()) {
                    // help out the editors that don't want the return key
                    if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_STANDARD) {
                        e.consume();
                    }
                }
                break;
            case KeyEvent.VK_TAB:
                myEditor.selectAll();
                e.consume();
                break;

            default:
                break;
        }

        if (!e.isConsumed()) {
            myEditor.keyPressed(e);
        }
    }
}