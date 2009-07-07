package org.uispec4j;

import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.datavalues.DataValueElementV;
import org.openshapa.views.discrete.datavalues.DataValueElementV.
        DataValueEditorInner;
import java.awt.Component;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import junit.framework.Assert;
import org.openshapa.views.discrete.Editor;
import org.openshapa.views.discrete.datavalues.MatrixRootView;
import org.openshapa.views.discrete.datavalues.OffsetView;
import org.openshapa.views.discrete.datavalues.OnsetView;
import org.uispec4j.utils.KeyUtils;

/**
 *
 */
public class Cell extends AbstractUIComponent {

    /**
     * UISpec4J convention to declare type.
     */
    public static final String TYPE_NAME = "SpreadsheetCell";
    /**
     * UISpec4J convention to declare associated class.
     */
    public static final Class[] SWING_CLASSES = {SpreadsheetCell.class};
    public static final int VALUE = 0;
    public static final int ONSET = 1;
    public static final int OFFSET = 2;
    /**
     * Since this is an Adaptor class, this is the class being adapted.
     */
    private SpreadsheetCell ssCell;

    private MatrixRootView matrixRV;

    /**
     * Inner class for Cell to track changes to the MatrixRootView TextBox
     * and adjust the caret location accordingly.
     */
    private class DocListener implements DocumentListener {

        /**
         * Gives notification that there was an insert into the document.
         * The range given by the DocumentEvent bounds the freshly inserted
         * region.
         * @param e DocumentEvent
         */
        public void insertUpdate(DocumentEvent e) {
           setCaretPosition(matrixRV.getCaretPosition() + e.getLength());
        }

        /**
         * Gives notification that a portion of the document has been removed.
         * The range is given in terms of what the view last saw (that is,
         * before updating sticky positions).
         * @param e DocumentEvent
         */
        public void removeUpdate(DocumentEvent e) {
           setCaretPosition(matrixRV.getCaretPosition() - e.getLength());
        }

        /**
         * Not used currently.
         * @param e DocumentEvent
         */
        public void changedUpdate(DocumentEvent e) {
        }
    }
    /**
     * Spreadsheet constructor.
     * @param SpreadsheetCell actual SpreadsheetCell class being adapted
     */
    public Cell(final SpreadsheetCell spreadsheetCell) {
        Assert.assertNotNull(spreadsheetCell);
        ssCell = spreadsheetCell;
        matrixRV = ssCell.getDataView();
        matrixRV.getDocument().addDocumentListener(new DocListener());
    }

    public Component getAwtComponent() {
        return ssCell;
    }

    public String getDescriptionTypeName() {
        return TYPE_NAME;
    }

    /**
     * returns the ordinal column identifier.
     * @return long ordinal column identifier
     */
    public final long getOrd() {
        return ssCell.getOrdinal();
    }

    /**
     * Returns Timestamp of Onset so that components are easily accessible.
     * @return Timestamp onset timestamp
     */
    public final Timestamp getOnsetTime() {
        return new Timestamp(this.getOnset().getText());
    }

    /**
     * Returns Timestamp of Offset so that components are easily accessible.
     * @return Timestamp offset timestamp
     */
    public final Timestamp getOffsetTime() {
        return new Timestamp(this.getOffset().getText());
    }

    /**
     * Returns TextBox of Onset so that components are easily accessible.
     * @return TextBox onset
     */
    public final TextBox getOnset() {
        OnsetView v = ssCell.getOnset();
        Editor e = ((DataValueElementV) v).getEditor();
        return new TextBox(e);
    }

    /**
     * Returns TextBox of Offset so that components are easily accessible.
     * @return TextBox offset
     */
    public final TextBox getOffset() {
        OffsetView v = ssCell.getOffset();
        Editor e = ((DataValueElementV) v).getEditor();
        return new TextBox(e);
    }

    /**
     * This is a matrix, which may change in the future, but right now,
     * it is not easy to hide this implementation.
     * @return MatrixRootView of the cell
     */
    public final MatrixRootView getMatrixRootView() {
        return matrixRV;
    }

     /**
     * types text into the cell value.
     * @param part int section of the value
     * @param s String to type
     */
    public final void enterEditorText(final int part, final String s) {
        requestEditorFocus(VALUE);
        enterEditorTextNoFocus(part, s);
    }

    private void enterEditorTextNoFocus(final int part, final String s) {
        for (int i = 0; i < s.length(); i++) {
            Key k = new Key(s.charAt(i));
            KeyUtils.pressKey(matrixRV, k);
            KeyUtils.typeKey(matrixRV, k);
            KeyUtils.releaseKey(matrixRV, k);
        }
    }

     /**
     * types text into the cell value.
     * @param part int section of the value
     * @param s1 String to type first
     * @param keys Keys to type next
     * @param s2 String to add at the end
     */
    public final void enterEditorText(final int part, final String s1,
            final Key[] keys, final String s2) {
        requestEditorFocus(VALUE);
        enterEditorTextNoFocus(part, s1, keys, s2);
    }

    private final void enterEditorTextNoFocus(final int part, final String s1,
            final Key[] keys, final String s2) {
        enterEditorTextNoFocus(0, s1);
        enterEditorKeysNoFocus(0, keys);
        enterEditorTextNoFocus(0, s2);
    }

     /**
     * types text into the cell value.
     * @param part int section of the value
     * @param s1 String to type first
     * @param keys Keys to type next
     * @param s2 String to add at the end
     */
    public final void enterEditorKeys(final int part, final Key[] keys) {
        requestEditorFocus(VALUE);
        enterEditorKeysNoFocus(part, keys);
    }

    private void enterEditorKeysNoFocus(final int part, final Key[] keys) {
        for (Key k : keys) {
            int caret = matrixRV.getCaretPosition();
            if (k.getChar() != null) {
                KeyUtils.pressKey(matrixRV, k);
                KeyUtils.typeKey(matrixRV, k);
                KeyUtils.releaseKey(matrixRV, k);
            } else {
                KeyUtils.pressKey(matrixRV, k);
                KeyUtils.releaseKey(matrixRV, k);
            }

            if (k == Key.LEFT) {
                caret -= 1;
            } else if (k == Key.RIGHT) {
                caret += 1;
            } else if (k == Key.BACKSPACE) {
                caret -= 1;
            } else if (k == Key.DELETE) {
                caret += 0;
            } else {
                caret += 1;
            }
            // if there is a selection we do not need to update the caret.
            if (matrixRV.getCaret().getDot() == matrixRV.getCaret().getMark()) {
                setCaretPosition(caret);
            }
        }
    }

    private void setCaretPosition(int pos) {
        int caretPos = Math.max(pos, 0);
        caretPos = Math.min(caretPos, matrixRV.getText().length());
        matrixRV.setCaretPosition(caretPos);
    }

     /**
     * types text into the onset timestamp.
     * @param s String to type
     */
    public final void enterOnsetText(final String s) {
        requestEditorFocus(ONSET);
        KeyUtils.enterString(getDVEditorByType(ONSET, 0), s);
    }

     /**
     * types text into the offset timestamp.
     * @param s String to type
     */
    public final void enterOffsetText(final String s) {
        requestEditorFocus(OFFSET);
        KeyUtils.enterString(getDVEditorByType(OFFSET, 0), s);
    }

    /**
     * sets the focus to a particular component of cell.
     * @param component to gain focus
     * @param i Int of value part, if value
     */
    public final void requestEditorFocus(final int component) {
        DataValueEditorInner e;
        switch (component) {
            case VALUE:
                matrixRV.focusGained(null);
                matrixRV.getEdTracker().focusGained(null);
                break;
            case ONSET:
		e = (DataValueEditorInner) ssCell.getOnset().getEditor();
                e.focusGained(null);
                break;
            case OFFSET:
		e = (DataValueEditorInner) ssCell.getOffset().getEditor();
                e.focusGained(null);
                break;
        }
    }

    /**
     * type a single key into a particular component in the cell.
     * @param component to type into
     * @param i section of value, if value
     * @param k Key to type
     */
    public final void typeEditorKey(final int component, final Key k) {
        DataValueEditorInner e;
        switch (component) {
            case VALUE:
                KeyUtils.typeKey(this.getMatrixRootView(), k);
                break;
            case ONSET:
				e = (DataValueEditorInner) ssCell.getOnset().getEditor();
                KeyUtils.typeKey(e, k);
                break;
            case OFFSET:
				e = (DataValueEditorInner) ssCell.getOffset().getEditor();
                KeyUtils.typeKey(e, k);
                break;
        }
    }

    /**
     * returns the DataValueEditorInner of the component of the cell.
     * @param type cell component type
     * @param i int of value section if value
     * @return DataValueEditorInner of particular component of cell
     */
    public final DataValueEditorInner getDVEditorByType(final int type, final int i) {
        switch (type) {
            case ONSET:
                return (DataValueEditorInner) ((DataValueElementV) ssCell.
                        getOnset()).getEditor();
            case OFFSET:
                return (DataValueEditorInner) ((DataValueElementV) ssCell.
                        getOffset()).getEditor();
            default:
                return null;
        }
    }
    
    public final Component getMREditorByType(final int type, final int i) {
        switch (type) {
            case VALUE:
                return this.getMatrixRootView();
            default:
                return null;
        }

    }

     /**
     * presses a single key into a particular component in the cell.
     * @param component to type into
     * @param i section of value, if value
     * @param k Key to type
     */
    public final void pressEditorKey(final int component, final Key k) {
        switch (component) {
            case VALUE:
                KeyUtils.pressKey(this.getMatrixRootView(), k);
                break;
            case ONSET:
                KeyUtils.pressKey((DataValueEditorInner) ssCell.getOnset().getEditor(), k);
                break;
            case OFFSET:
                KeyUtils.pressKey((DataValueEditorInner) ssCell.getOffset().getEditor(), k);
                break;
        }
    }

     /**
     * returns a Textbox for the value component of a cell.
     * @param part section of the value of which to turn the Textbox
     * @return Textbox of section of the value component
     */
    public final TextBox getValueTextBox(final int part) {
        // this returns a TextBox for the whole MatrixRootView of the Cell
        // (an extended JTextArea now)

        // Currently returns the whole MatrixRootView where one day (soon)
        // we would want to be able to enter text into other editor components
        // in a matrix or predicate for instance.
        // The current tests all operate on single editors.

        return new TextBox(this.getMatrixRootView());
    }
}
