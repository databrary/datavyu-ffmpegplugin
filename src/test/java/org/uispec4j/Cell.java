package org.uispec4j;

import org.openshapa.views.discrete.SpreadsheetCell;
import java.awt.Component;
import java.awt.Font;
import javax.swing.text.JTextComponent;
import junit.framework.Assert;
import org.openshapa.views.discrete.datavalues.MatrixRootView;
import org.openshapa.views.discrete.datavalues.TimeStampTextField;
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
    /**
     * Data value element.
     */
    public static final int VALUE = 0;
    /**
     * Onset Timestamp.
     */
    public static final int ONSET = 1;
    /**
     * Offset Timestamp.
     */
    public static final int OFFSET = 2;
    /**
     * Since this is an Adaptor class, this is the class being adapted.
     */
    private SpreadsheetCell ssCell;

    /**
     * Spreadsheet constructor.
     * @param spreadsheetCell actual SpreadsheetCell class being adapted
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
    public final Timestamp getOffsetTimestamp() {
        return new Timestamp(this.getOffset().getText());
    }

    /**
     * Returns TextBox of Onset so that components are easily accessible.
     * @return TextBox onset
     */
    public final TextBox getOnset() {
        return new TextBox(ssCell.getOnset());
    }

    /**
     * Returns TextBox of Offset so that components are easily accessible.
     * @return TextBox offset
     */
    public final TextBox getOffset() {
        return new TextBox(ssCell.getOffset());
    }

    /**
     * returns the value, which is a JTextArea (MatrixRootView).
     * returns as a TextBox
     * @return TextBox value
     */
    public final TextBox getValue() {
        MatrixRootView mrv = ssCell.getDataView();
        return new TextBox(mrv);
    }

    /**
     * returns the text of the value.
     * returns as a String
     * @return String value
     */
    public final String getValueText() {
        return ssCell.getDataView().getText();
    }

    /**
     * returns the font of the value.
     * @return Font font
     */
    public final Font getValueFont() {
        return ssCell.getDataView().getFont();
    }


    /**
     * types text into a cell element.
     * @param element element to type value into
     * @param s String to type
     */
    public final void enterText(final int element, final String s) {
        requestFocus(element);
        KeyUtils.enterString(getComponentByType(element), s);
    }

     /**
     * types text into the cell element.
     * @param element element to type value into
     * @param s1 String to type first
     * @param keys Keys to type next
     * @param s2 String to add at the end
     */
    public final void enterText(final int element, final String s1,
            final Key[] keys, final String s2) {
        requestFocus(element);

        KeyUtils.enterString(getComponentByType(element), s1);
        KeyUtils.enterKeys(getComponentByType(element), keys);
        KeyUtils.enterString(getComponentByType(element), s2);
    }

    public final void selectAllAndTypeKey(final int element, final Key key) {
        requestFocus(element);

        getComponentByType(element).selectAll();

        KeyUtils.typeKey(getComponentByType(element), key);
    }

     /**
     * sets the focus to a particular element of cell.
     * @param element to gain focus
     */
    public final void requestFocus(final int element) {
       if (element == VALUE) {
            ((MatrixRootView) getComponentByType(element)).focusGained(null);
             ((MatrixRootView) getComponentByType(element)).getEdTracker().focusGained(null);
        } else {
            ((TimeStampTextField) getComponentByType(element))
                    .focusGained(null);
        }

    }

    public final void select(){

    }

    /**
     * returns the text component by its type.
     * @param type cell component type
     * @return JTextComponent of particular component of cell
     */
    private JTextComponent getComponentByType(final int type) {
        switch (type) {
            case VALUE:
                return ssCell.getDataView();
            case ONSET:
                return ssCell.getOnset();
            case OFFSET:
                return ssCell.getOffset();
            default:
                return ssCell.getDataView();
        }
    }

}
