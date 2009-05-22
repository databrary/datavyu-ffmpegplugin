package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.DataValue;
import org.openshapa.db.Matrix;
import org.openshapa.db.PredDataValue;
import org.openshapa.views.discrete.Editor;
import org.openshapa.views.discrete.Selector;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import javax.swing.border.EmptyBorder;

/**
 * A DataValueElementView - i.e. A leaf view of a data value.
 */
public abstract class DataValueElementV extends DataValueV {

    /** The editor to use for this data value element view. */
    private Editor elementEditor;

    /**
     * Constructor.
     *
     * @param model The data value that this view will represent.
     * @param editable Is this data value element editable, true if it is, false
     * otherwise.
     */
    DataValueElementV(final DataValue model, final boolean editable) {
        super(model);
        initDataValueElementV(editable);
    }

    /**
     * Constructor.
     *
     * @param cellSelection The selection to use for this data value element
     * view.
     * @param cell The Parent cell that holds this DataValueElementV.
     * @param matrix The parent matrix for this Data value element view.
     * @param matrixIndex The index of the data value within the above matrix
     * that this view is to represent.
     * @param editable Is this data value element editable, true if it is, false
     * otherwise.
     */
    DataValueElementV(final Selector cellSelection,
                      final DataCell cell,
                      final Matrix matrix,
                      final int matrixIndex,
                      final boolean editable) {

        super(cellSelection, cell, matrix, matrixIndex);
        initDataValueElementV(editable);
    }

    /**
     * Constructor.
     *
     * @param cellSelection The selection to use for this data value element
     * view.
     * @param cell The parent cell that holds this DataValueElementv.
     * @param predicate The parent matrix for this data value element view.
     * @param predicateIndex The index of the data value within the above
     * predicate that this view is to represent.
     * @param matrix The parent matrix for this data value element view.
     * @param matrixIndex The index of the data value within the above matrix
     * that this view is to represent.
     * @param editable Is this data value element editable, true if it is, false
     * otherwise.
     */
    DataValueElementV(final Selector cellSelection,
                      final DataCell cell,
                      final PredDataValue predicate,
                      final int predicateIndex,
                      final Matrix matrix,
                      final int matrixIndex,
                      final boolean editable) {

        super(cellSelection, cell, predicate,
              predicateIndex, matrix, matrixIndex);
        initDataValueElementV(editable);
    }

    /**
     * Constructor.
     *
     * @param cellSelection The selection to use for this data value element
     * view.
     * @param cell The parent cell that holds this DataValueElementV.
     * @param dataValue The data value that this view element will represent.
     * @param editable Is this data value element editable, true if it is, false
     * otherwise.
     */
    public DataValueElementV(final Selector cellSelection,
                             final DataCell cell,
                             final DataValue dataValue,
                             final boolean editable) {

        super(cellSelection, cell, dataValue);
        initDataValueElementV(editable);
    }

    /**
     * Performs initalisation of this data value element view.
     *
     * @param editable Is the data value element view editable, true if it is,
     * false otherwise.
     */
    private void initDataValueElementV(final boolean editable) {
        FlowLayout l = new FlowLayout(FlowLayout.LEFT, 0, 0);
        this.setLayout(l);

        elementEditor = this.buildEditor();
        this.elementEditor.setEditable(editable);
        this.elementEditor.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.add(elementEditor);
        this.updateStrings();
    }

    /**
     * @return Builds the editor to be used for this data value.
     */
    protected abstract Editor buildEditor();

    @Override
    public void setValue(final DataCell dataCell,
                         final PredDataValue predicate,
                         final int predicateIndex,
                         final Matrix matrix,
                         final int matrixIndex) {
        super.setValue(dataCell, predicate,
                       predicateIndex, matrix, matrixIndex);
        this.elementEditor.restoreCaretPosition();
    }

    @Override
    public void setValue(final DataCell dataCell,
                         final Matrix matrix,
                         final int matrixIndex) {
        super.setValue(dataCell, matrix, matrixIndex);
        this.elementEditor.restoreCaretPosition();
    }

    @Override
    public void updateDatabase() {
        this.elementEditor.storeCaretPosition();
        super.updateDatabase();
    }

    @Override
    public String toString() {
        return this.elementEditor.getText();
    }

    @Override
    public void updateStrings() {
        String t = "";
        if (this.getModel() != null && !this.getModel().isEmpty()) {
            t = this.getModel().toString();
        } else if (this.getParentMatrix() != null) {
            t = getNullArg();
        }

        this.elementEditor.setText(t);
    }

    /**
     * The action to invoke when a mouse button is clicked.
     *
     * @param me The mouse event that triggered this action.
     */
    @Override
    public final void mouseClicked(final MouseEvent me) {
        // Select all if the data value view is a placeholder.
        if (this.getModel().isEmpty()) {
            this.elementEditor.selectAll();
        }
    }

    /**
     * Requests that this component gets the input focus.
     */
    @Override
    public final void requestFocus() {
        this.elementEditor.requestFocus();
    }

    /**
     * @return True if this component is currently focused, false otherwise.
     */
    @Override
    public final boolean hasFocus() {
        return this.elementEditor.hasFocus();
    }

    /**
     * @return The editor used for this Data Value Element View.
     */
    public final Editor getEditor() {
        return this.elementEditor;
    }

    /**
     * @return The parent container for this Data Value Element View.
     */
    public final Container getValueParent() {
        return this.getParent();
    }

    /**
     * The editor for the int data value.
     */
    public abstract class DataValueEditor extends Editor
    implements FocusListener, KeyListener {

        /**
         * Default constructor.
         */
        public DataValueEditor() {
            super();
            this.addFocusListener(this);
            this.addKeyListener(this);
        }

        /**
         * The action to invoke if the focus is gained by this DataValueV.
         *
         * @param fe The Focus Event that triggered this action.
         */
        public void focusGained(final FocusEvent fe) {
            // BugzID:320 Deselect Cells before selecting cell contents.
            Selector s = getSelector();

            if (s != null) {
                s.deselectAll();
                s.deselectOthers();
            }

            // Only select all if the data value view is a placeholder.
            DataValue d = getModel();
            if (d != null && d.isEmpty()) {
                this.selectAll();
            }
        }

        /**
         * The action to invoke if the focus is lost from this DataValueV.
         *
         * @param fe The FocusEvent that triggered this action.
         */
        public void focusLost(final FocusEvent fe) {
            if (this.getText() == null || this.getText().equals("")) {

                getModel().clearValue();
                updateDatabase();
            }
        }

        /**
         * Process key events that have been dispatched to this component, pass
         * them through to all listeners, and then if they are not consumed pass
         * it onto the parent of this component.
         *
         * @param k They keyboard event that was dispatched to this component.
         */
        @Override
        public void processKeyEvent(final KeyEvent k) {
            if (!k.isConsumed() && k.getID() == KeyEvent.KEY_PRESSED) {
                this.keyPressed(k);
            } else if (!k.isConsumed() && k.getID() == KeyEvent.KEY_TYPED) {
                this.keyTyped(k);
            }

            if (!k.isConsumed() || k.getKeyCode() == KeyEvent.VK_UP
                || k.getKeyCode() == KeyEvent.VK_DOWN) {
                getValueParent().dispatchEvent(k);
            }
        }

        /**
         * The action to invoke when a key is released.
         *
         * @param e The KeyEvent that triggered this action.
         */
        public void keyReleased(final KeyEvent e) {
            // Ignore key release.
        }

        /**
         * The action to invoke when a key is pressed.
         *
         * @param e The KeyEvent that triggered this action.
         */
        public void keyPressed(final KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DELETE:
                    // Ignore - handled when the key is typed.
                    e.consume();
                    break;

                // Handle left key, move caret position one space to the left.
                case KeyEvent.VK_LEFT:
                    int c = Math.max(0, this.getCaretPosition() - 1);
                    this.setCaretPosition(c);
                    e.consume();
                    break;

                // Handle right key, move caret position one space to the right.
                case KeyEvent.VK_RIGHT:
                    c = Math.min(this.getText().length(),
                                 this.getCaretPosition() + 1);
                    this.setCaretPosition(c);
                    e.consume();
                    break;

                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_UP:
                    // Key stroke gets passed up a parent element to navigate
                    // cells up and down.
                    break;
                default:
                    break;
            }
        }
    }
}
