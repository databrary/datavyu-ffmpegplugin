package org.openshapa.views.discrete.datavalues;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.discrete.Selector;
import java.util.Vector;
import javax.swing.JTextArea;
import org.apache.log4j.Logger;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.EditorComponent;
import org.openshapa.views.discrete.EditorTracker;

/**
 * JTextArea view of the Matrix (database cell) data.
 */
public final class MatrixRootView extends JTextArea implements FocusListener {

    /** The selection (used for cells) for the parent spreadsheet. */
    private Selector sheetSelection;

    /** The parent cell for this JPanel. */
    private DataCell parentCell = null;

    /** The editors that make up the representation of the data. */
    private Vector<EditorComponent> editors;

    /** The editor tracker responsible for the editor components. */
    private EditorTracker edTracker;

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(MatrixRootView.class);

    /**
     * Creates a new instance of MatrixV.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param cell The parent datacell for this spreadsheet cell.
     * @param matrix The Matrix holding datavalues that this view label will
     * represent.
     */
    public MatrixRootView(final Selector cellSelection,
                          final DataCell cell,
                          final Matrix matrix) {
        super();
        setLineWrap(true);
        setWrapStyleWord(true);

        sheetSelection = cellSelection;
        parentCell = cell;
        editors = new Vector<EditorComponent>();
        edTracker = new EditorTracker(this, editors);

        setMatrix(matrix);

        this.addFocusListener(this);
        this.addFocusListener(edTracker);
        this.addKeyListener(edTracker);
        this.addMouseListener(edTracker);
    }

    /**
     * @return true if a viewport should force the Scrollables width
     * to match its own.
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    /**
     * Sets the matrix that this MatrixRootView will represent.
     * @param m The Matrix to display.
     */
    public void setMatrix(final Matrix m) {
        // Determine selected editor, and internal caret position.
        int pos = this.getCaretPosition();
        EditorComponent comp = edTracker.findEditor(pos);
        int edPos = comp.getCaretPosition();

        try {
            if (editors.size() == 0) {
                editors.addAll(DataValueEditorFactory.
                                             buildMatrix(this, parentCell, m));
            } else {
                for (EditorComponent ed : editors) {
                    // BugzID:503 - This needs to be refactored. Should not be
                    // doing reflection to determine type. Should boil down to
                    // a simple ed.resetValue(parentCell, m);
                    DataValueEditorFactory.resetValue(ed, parentCell, m);
                }
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to set/reset Matrix for MatrixRootView.", e);
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
        for (EditorComponent item : editors) {
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

    /**
     * The action to invoke if the focus is gained by this MatrixRootView.
     * @param fe The Focus Event that triggered this action.
     */
    public void focusGained(final FocusEvent fe) {
        // BugzID:320 Deselect Cells before selecting cell contents.
        if (sheetSelection != null) {
            sheetSelection.deselectAll();
            sheetSelection.deselectOthers();
        }

        // We need to remember which cell should be duplicated if the user
        // presses the enter key or selects New Cell from the menu.
        if (parentCell != null) {
            // method names don't reflect usage - we didn't really create this
            // cell just now.
            OpenSHAPA.setLastCreatedColId(parentCell.getItsColID());
            OpenSHAPA.setLastCreatedCellId(parentCell.getID());
        }
    }

    /**
     * The action to invoke if the focus is lost.
     * @param fe The FocusEvent that triggered this action.
     */
    public void focusLost(final FocusEvent fe) {
        // do nothing
    }

    /**
     * Pastes contents of the clipboard into the editor tracker.
     */
    @Override
    public void paste() {
        edTracker.paste();
    }

    /**
     * Copies the current selection into the clipboard and then deletes the
     * selection.
     */
    @Override
    public void cut() {
        edTracker.cut();
    }
}