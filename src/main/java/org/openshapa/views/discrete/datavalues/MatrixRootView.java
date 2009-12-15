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
import org.openshapa.db.FormalArgument.FArgType;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.Predicate;
import org.openshapa.db.VocabElement;
import org.openshapa.views.discrete.EditorComponent;
import org.openshapa.views.discrete.EditorTracker;

/**
 * JTextArea view of the Matrix (database cell) data.
 */
public final class MatrixRootView extends JTextArea implements FocusListener {

    /** The selection (used for cells) for the parent spreadsheet. */
    private Selector sheetSelection;

    /** The parent cell for this JPanel. */
    private long parentCell = -1;

    /** All the editors that make up the representation of the data. */
    private Vector<EditorComponent> allEditors;

    /** The editor tracker responsible for the editor components. */
    private EditorTracker edTracker;

    /** The current vocab used for this matrix root view. */
    private VocabElement ve;

    /**
     * The current number of predicate arguments used for this matrix root
     * view... -1 If not a predicate.
     */
    private int numPredArgs;

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
        parentCell = cell.getID();
        allEditors = new Vector<EditorComponent>();
        edTracker = new EditorTracker(this, allEditors);
        ve = null;
        numPredArgs = -1;

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
     *
     * @param m The Matrix to display.
     */
    public void setMatrix(final Matrix m) {
        // Determine selected editor, and internal caret position.
        int pos = this.getCaretPosition();
        EditorComponent comp = edTracker.findEditor(pos);
        int edPos = comp.getCaretPosition();

        try {
            if (m != null) {
                // The vocab element changes before the matrix. We have no idea
                // if the vocab element has changed ahead of time, so basically
                // we need to store a local copy of the vocab element and
                // locally determine if the vocab element has changed and update
                // the editors accordingly.
                VocabElement newVE = m.getDB().getVocabElement(m.getMveID());
                boolean hasPredChanged = hasPredicateVocabChanged(m);

                DataCell c = (DataCell) OpenSHAPA.getDB().getCell(parentCell);

                // No editors exist yet - build some to begin with.
                if (allEditors.size() == 0) {
                    allEditors.addAll(DataValueEditorFactory
                                      .buildMatrix(this, c, m));
                    ve = newVE;

                // Check to see if the vocab for the matrix has changed - if so
                // clear the current editors and start afresh.
                } else if (ve != null && !ve.equals(newVE) || hasPredChanged) {
                    if (hasPredChanged) {
                        edPos = 0;
                    }
                    allEditors.clear();
                    allEditors.addAll(DataValueEditorFactory
                                      .buildMatrix(this, c, m));
                    ve = newVE;

                // Vocab hasn't changed - only values for the matrix. Simply
                // update the values.
                } else {
                    for (EditorComponent ed : allEditors) {
                        ed.resetValue(c, m);
                    }
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
     * Returns true if the vocab has changed for a predicate.
     *
     * @param m The matrix containing the predicate to alter.
     *
     * @return True if the predicate vocab has changed, false otherwise.
     *
     * @throws SystemErrorException If unable to determine if the predicate
     * vocab has changed.
     */
    public boolean hasPredicateVocabChanged(final Matrix m)
    throws SystemErrorException {
        boolean result = false;

        if (m.getNumArgs() == 1
            && m.getArgCopy(0).getItsFargType() == FArgType.PREDICATE) {
            PredDataValue pdv = (PredDataValue) m.getArgCopy(0);
            Predicate p = pdv.getItsValue();

            result = (pdv.isEmpty() || p.getNumArgs() != numPredArgs);
            numPredArgs = p.getNumArgs();
        }

        return result;
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

    /**
     * The action to invoke if the focus is gained by this MatrixRootView.
     *
     * @param fe The Focus Event that triggered this action.
     */
    public void focusGained(final FocusEvent fe) {
        try {
            // BugzID:320 Deselect Cells before selecting cell contents.
            if (sheetSelection != null) {
                sheetSelection.deselectAll();
                sheetSelection.deselectOthers();
            }

            // We need to remember which cell should be duplicated if the user
            // presses the enter key or selects New Cell from the menu.
            if (parentCell != -1) {
                // method names don't reflect usage - we didn't really create this
                // cell just now.
                DataCell c = (DataCell) OpenSHAPA.getDB().getCell(parentCell);
                OpenSHAPA.setLastCreatedColId(c.getItsColID());
                OpenSHAPA.setLastCreatedCellId(parentCell);
            }
        } catch (SystemErrorException se) {
            logger.error("Unable to gain focus", se);
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