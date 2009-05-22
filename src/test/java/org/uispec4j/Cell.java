package org.uispec4j;

import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.datavalues.DataValueElementV;
import org.openshapa.views.discrete.datavalues.DataValueElementV.DataValueEditor;
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
    public final Vector<DataValueV> getValue() {
        return ssCell.getDataValueV().getChildren();
    }

    public final DataValueElementV getView(int part) {
        DataValueV v = getValue().elementAt(part);
        if (v instanceof DataValueElementV) {
            return (DataValueElementV) v;

            // Can't build DataValueElementV predicate or matrix.
        } else {
            return null;
        }
    }

    public final DataValueEditor getEditor(int part) {
        DataValueElementV v = getView(part);
        if (v != null) {
            return (DataValueEditor) v.getEditor();
        } else {
            return null;
        }
    }

    public final void enterEditorText(int part, String s) {
        requestEditorFocus(VALUE, part);
        for (int i = 0; i < s.length(); i++) {
            typeEditorKey(VALUE, part, new Key(s.charAt(i)));
        }
    }

    public final void enterEditorText(int part, String s1, Key[] keys, String s2) {
        requestEditorFocus(VALUE, part);
        for (int i = 0; i < s1.length(); i++) {
            typeEditorKey(VALUE, part, new Key(s1.charAt(i)));
        }

        for (int i = 0; i < keys.length; i++) {
            if (keys[i].getChar() != null) {
                typeEditorKey(VALUE, part, keys[i]);
            } else {
                pressEditorKey(VALUE, part, keys[i]);
            }
        }
        for (int i = 0; i < s2.length(); i++) {
            typeEditorKey(VALUE, part, new Key(s2.charAt(i)));
        }
    }

    public final void enterOnsetText(String s) {
        requestEditorFocus(ONSET, 0);
        for (int i = 0; i < s.length(); i++) {
            typeEditorKey(ONSET, 0, new Key(s.charAt(i)));
        }
    }

    public final void enterOffsetText(String s) {
        requestEditorFocus(OFFSET, 0);
        for (int i = 0; i < s.length(); i++) {
            typeEditorKey(OFFSET, 0, new Key(s.charAt(i)));
        }
    }

    public final void requestEditorFocus(int component, int i) {
        DataValueEditor e;
        switch (component) {
            case VALUE:
                e = getEditor(i);
                break;
            case ONSET:
                e = (DataValueEditor) ((DataValueElementV) ssCell.getOnset()).getEditor();
                break;
            case OFFSET:
                e = (DataValueEditor) ((DataValueElementV) ssCell.getOffset()).getEditor();
                break;
            default:
                e = getEditor(i);
        }

        e.focusGained(null);
    }

    public final void typeEditorKey(int component, int i, Key k) {
        DataValueEditor e;
        switch (component) {
            case VALUE:
                e = getEditor(i);
                break;
            case ONSET:
                e = (DataValueEditor) ((DataValueElementV) ssCell.getOnset()).getEditor();
                break;
            case OFFSET:
                e = (DataValueEditor) ((DataValueElementV) ssCell.getOffset()).getEditor();
                break;
            default:
                e = getEditor(i);
        }

        KeyUtils.typeKey(e, k);
    }

    public final void typeEditorKey(int component, Key k) {
        requestEditorFocus(component, 0);
        typeEditorKey(component, 0, k);
    }

    public final void pressEditorKey(int component, int i, Key k) {
        DataValueEditor e;
        switch (component) {
            case VALUE:
                e = getEditor(i);
                break;
            case ONSET:
                e = (DataValueEditor) ((DataValueElementV) ssCell.getOnset()).getEditor();
                break;
            case OFFSET:
                e = (DataValueEditor) ((DataValueElementV) ssCell.getOffset()).getEditor();
                break;
            default:
                e = getEditor(i);
        }

        KeyUtils.pressKey(e, k);
    }

    public final void pressEditorKey(int component, Key k) {
        pressEditorKey(component, 0, k);
    }

    public final TextBox getValueTextBox(int part) {
        DataValueElementV view = getView(part);

        if (view != null) {
            return new TextBox(view.getEditor());

        } else {
            return null;
        }
    }
}
