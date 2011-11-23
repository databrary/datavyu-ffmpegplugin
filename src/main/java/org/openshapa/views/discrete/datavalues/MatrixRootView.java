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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;

import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.EditorComponent;
import org.openshapa.views.discrete.EditorTracker;

import java.util.ArrayList;
import java.util.List;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.Value;
import org.openshapa.models.db.Variable;

/**
 * JTextArea view of the Matrix (database cell) data.
 */
public final class MatrixRootView extends JTextArea implements FocusListener {

    /** The parent cell for this JPanel. */
    private Cell parentCell = null;

    /** All the editors that make up the representation of the data. */
    private List<EditorComponent> allEditors;

    /** The editor tracker responsible for the editor components. */
    private EditorTracker edTracker;

    /**
     * Creates a new instance of MatrixV.
     * 
     * @param cell The parent cell for this spreadsheet cell.
     * @param matrix The Matrix holding datavalues that this view label will
     * represent.
     */
    public MatrixRootView(final Cell cell, final Value value) {
        super();

        setLineWrap(true);
        setWrapStyleWord(true);

        parentCell = cell;
        allEditors = new ArrayList<EditorComponent>();
        edTracker = new EditorTracker(this, allEditors);
        
        setMatrix(value);

        addFocusListener(this);
        addFocusListener(edTracker);
        addKeyListener(edTracker);
        addMouseListener(edTracker);
    }

    /**
     * @return true if a viewport should force the Scrollables width to match
     * its own.
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    /**
     * Sets the value that this MatrixRootView will represent.
     * 
     * @param v The Value to display.
     */
    public void setMatrix(final Value v) {
        // Determine selected editor, and internal caret position.
        int pos = getCaretPosition();
        EditorComponent comp = edTracker.findEditor(pos);
        int edPos = comp.getCaretPosition();

        if (v != null) {
            // No editors exist yet - build some to begin with.
            allEditors.clear();
            allEditors.addAll(DataValueEditorFactory.buildMatrix(this, parentCell));
        }

        rebuildText();

        // restore caret position inside current editor.
        comp.setCaretPosition(edPos);
    }

    /**
     * Recalculates and sets the text to display.
     */
    public void rebuildText() {
        String ans = "";
        for (EditorComponent item : allEditors) {
            ans += item.getText();
        }
        setText(ans);
    }

    /**
     * Used in the UISpec4j tests.
     * 
     * @return The editor tracker for this MatrixRootView.
     */
    public EditorTracker getEdTracker() {
        return edTracker;
    }

    // *************************************************************************
    // Focus Listener Overrides
    // *************************************************************************
    @Override
    public void focusGained(final FocusEvent fe) {
        // We need to remember which cell should be duplicated if the user
        // presses the enter key or selects New Cell from the menu.
        if (parentCell != null) {
            // method names don't reflect usage - we didn't really create
            // this column just now.
            Variable v = OpenSHAPA.getProjectController().getDB().getVariable(parentCell);
            OpenSHAPA.getProjectController().setLastCreatedVariable(v);
            OpenSHAPA.getProjectController().setLastSelectedCell(parentCell);
        }
    }

    @Override
    public void focusLost(final FocusEvent fe) {
        // do nothing
    }

    // *************************************************************************
    // Parent Class Overrides
    // *************************************************************************
    @Override
    public void paste() {
        edTracker.paste();
    }

    @Override
    public void cut() {
        edTracker.cut();
    }
}