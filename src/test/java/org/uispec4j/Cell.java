package org.uispec4j;

import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.datavalues.DataValueElementV;
import org.openshapa.views.discrete.datavalues.DataValueElementV.
        DataValueEditor;
import org.openshapa.views.discrete.datavalues.DataValueV;
import java.awt.Component;
import java.util.Vector;
import junit.framework.Assert;
import org.openshapa.views.discrete.Editor;
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

    /**
     * Spreadsheet constructor.
     * @param SpreadsheetCell actual SpreadsheetCell class being adapted
     */
    public Cell(final SpreadsheetCell spreadsheetCell) {
        Assert.assertNotNull(spreadsheetCell);
        this.ssCell = spreadsheetCell;
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
        return ssCell.getOrdinal().getItsValue();
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
     * returns the value, which is a Vector of DataValueView.
     * This is a matrix, which may change in the future, but right now,
     * it is not easy to hide this implementation.
     * @return Vector<DataValueView> value as a vector of DataValueView
     */
    public final Vector <DataValueV> getValue() {
        return ssCell.getDataValueV().getChildren();
    }

    /**
     * returns the DataValueElement View.
     * @param part int section of the value
     * @return DataValueElementV DataValueElement View
     */
    public final DataValueElementV getView(final int part) {
        DataValueV v = getValue().elementAt(part);
        if (v instanceof DataValueElementV) {
            return (DataValueElementV) v;

        // Can't build DataValueElementV predicate or matrix.
        } else {
            return null;
        }
    }

     /**
     * returns the DataValueEditor for the cell value.
     * @param part int section of the value
     * @return DataValueEditor of the value of the cell
     */
    public final DataValueEditor getEditor(final int part) {
        DataValueElementV v = getView(part);
        if (v != null) {
            return (DataValueEditor) v.getEditor();
        } else {
            return null;
        }
    }

     /**
     * types text into the cell value.
     * @param part int section of the value
     * @param s String to type
     */
    public final void enterEditorText(final int part, final String s) {
        requestEditorFocus(VALUE, part);

        KeyUtils.enterString(getEditorByType(VALUE, part), s);
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
        requestEditorFocus(VALUE, part);

        KeyUtils.enterString(getEditorByType(VALUE, part), s1);
        KeyUtils.enterKeys(getEditorByType(VALUE, part), keys);
        KeyUtils.enterString(getEditorByType(VALUE, part), s2);
    }

     /**
     * types text into the onset timestamp.
     * @param s String to type
     */
    public final void enterOnsetText(final String s) {
        requestEditorFocus(ONSET);
        KeyUtils.enterString(getEditorByType(ONSET, 0), s);
    }

     /**
     * types text into the offset timestamp.
     * @param s String to type
     */
    public final void enterOffsetText(final String s) {
        requestEditorFocus(OFFSET);
        KeyUtils.enterString(getEditorByType(OFFSET, 0), s);
    }

    /**
     * sets the focus to a particular component of cell.
     * @param component to gain focus
     * @param i Int of value part, if value
     */
    public final void requestEditorFocus(final int component, final int i) {
        DataValueEditor e;
        e = getEditorByType(component, i);
        e.focusGained(null);
    }

    /**
     * sets the focus to a particular component of cell.
     * @param component to gain focus
     */
    public final void requestEditorFocus(final int component) {
        DataValueEditor e;
        e = getEditorByType(component, 0);
        e.focusGained(null);
    }

    /**
     * type a single key into a particular component in the cell.
     * @param component to type into
     * @param i section of value, if value
     * @param k Key to type
     */
    public final void typeEditorKey(final int component, final int i,
            final Key k) {
        DataValueEditor e;
        e = getEditorByType(component, i);
        KeyUtils.typeKey(e, k);
    }

    /**
     * returns the DataValueEditor of the component of the cell.
     * @param type cell component type
     * @param i int of value section if value
     * @return DataValueEditor of particular component of cell
     */
    public final DataValueEditor getEditorByType(final int type, final int i) {
        DataValueEditor e;
        switch (type) {
            case VALUE:
                return getEditor(i);
            case ONSET:
                return (DataValueEditor) ((DataValueElementV) ssCell.
                        getOnset()).getEditor();
            case OFFSET:
                return (DataValueEditor) ((DataValueElementV) ssCell.
                        getOffset()).getEditor();
            default:
                return getEditor(i);
        }
    }

     /**
     * type a single key into a particular component in the cell.
     * @param component to type into
     * @param k Key to type
     */
    public final void typeEditorKey(final int component, final Key k) {
        requestEditorFocus(component, 0);
        typeEditorKey(component, 0, k);
    }

     /**
     * presses a single key into a particular component in the cell.
     * @param component to type into
     * @param i section of value, if value
     * @param k Key to type
     */
    public final void pressEditorKey(final int component, final int i,
            final Key k) {
        DataValueEditor e;
        e = getEditorByType(component, i);
        KeyUtils.pressKey(e, k);
    }

     /**
     * presses a single key into a particular component in the cell.
     * @param component to type into
     * @param k Key to type
     */
    public final void pressEditorKey(final int component, final Key k) {
        pressEditorKey(component, 0, k);
    }

    /**
     * returns a Textbox for the value component of a cell.
     * @param part section of the value of which to turn the Textbox
     * @return Textbox of section of the value component
     */
    public final TextBox getValueTextBox(final int part) {
        DataValueElementV view = getView(part);

        if (view != null) {
            return new TextBox(view.getEditor());

        } else {
            return null;
        }
    }
}
