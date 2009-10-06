package org.uispec4j;

import java.awt.Component;
import java.util.Vector;
import javax.swing.JLabel;
import junit.framework.Assert;
import org.openshapa.views.discrete.datavalues.vocabelements.VocabElementRootView;
import org.openshapa.views.discrete.datavalues.vocabelements.VocabElementV;
import org.uispec4j.utils.KeyUtils;

/**
 *
 */
public class VocabElement extends AbstractUIComponent {

    /**
     * UISpec4J convention to declare type.
     */
    public static final String TYPE_NAME = "VocabElementV";
    /**
     * UISpec4J convention to declare associated class.
     */
    public static final Class[] SWING_CLASSES = {VocabElementV.class};
    /**
     * Since this is an Adaptor class, this is the class being adapted.
     */
    private VocabElementV ve;

    /**
     * Spreadsheet constructor.
     * @param vocabElementV actual VocabElementV class being adapted
     */
    public VocabElement(final VocabElementV vocabElementV) {
        Assert.assertNotNull(vocabElementV);
        this.ve = vocabElementV;
    }

    public Component getAwtComponent() {
        return ve;
    }

    public String getDescriptionTypeName() {
        return TYPE_NAME;
    }

    /**
     * returns the changed status.
     * @return boolean has element changed?
     */
    public final boolean getChangedStatus() {
        return ve.hasChanged();
    }

    /**
     * returns the deletion status.
     * @return boolean has element changed?
     */
    public final boolean getDeletionStatus() {
        return ve.isDeletable();
    }

    /**
     * Returns the delta icon label.
     * @return JLabel delta icon
     */
    public final JLabel getChangedIcon() {
        return ve.getChangedIcon();
    }

    /**
     * Returns the delta icon label.
     * @return JLabel delta icon
     */
    public final JLabel getTypeIcon() {
        return ve.getTypeIcon();
    }

    /**
     * returns the value, which is a JTextArea (MatrixRootView).
     * returns as a TextBox
     * @return TextBox value
     */
    public final TextBox getValue() {
        VocabElementRootView verv = ve.getDataView();
        return new TextBox(verv);
    }

    /**
     * returns the text of the value.
     * returns as a String
     * @return String value
     */
    public final String getValueText() {
        return ve.getDataView().getText();
    }

    /**
     * types text into a cell element.
     * @param s String to type
     */
    public final void enterText(final String s) {
        //requestFocus(element);
        KeyUtils.enterString(ve.getDataView(), s);
    }

     /**
     * types text into the cell element.
     * @param s1 String to type first
     * @param keys Keys to type next
     * @param s2 String to add at the end
     */
    public final void enterText(final String s1,
            final Key[] keys, final String s2) {
        //requestFocus(element);

        KeyUtils.enterString(ve.getDataView(), s1);
        KeyUtils.enterKeys(ve.getDataView(), keys);
        KeyUtils.enterString(ve.getDataView(), s2);
    }

     /**
     * types text into a cell element using a vector of TextItem.
     * @param vti vector of TextItems.
     */
    public final void enterText(final Vector<TextItem> vti) {
        this.requestFocus();

        for (TextItem t : vti) {
            t.enterItem(ve.getDataView());
        }
    }

    /**
     * types text into vocab argument.
     * @param arg Argument number
     * @param vti vector of TextItems.
     */
    public final void enterTextInArg(final int arg, final Vector<TextItem> vti)
    {
        this.requestFocus();

        KeyUtils.enterKey(ve.getDataView(), Key.HOME);

        for (int i = 0; i <= arg; i++) {
            KeyUtils.enterKey(ve.getDataView(), Key.TAB);
        }

        //Deselect argument
        KeyUtils.enterKey(ve.getDataView(), Key.LEFT);
        KeyUtils.enterKey(ve.getDataView(), Key.RIGHT);

        for (TextItem t : vti) {
            t.enterItem(ve.getDataView());
        }
    }

     /**
     * replaces text in vocab argument.
     * @param arg Argument number
     * @param vti vector of TextItems.
     */
    public final void replaceTextInArg(final int arg,
            final Vector<TextItem> vti) {
        this.requestFocus();

        KeyUtils.enterKey(ve.getDataView(), Key.HOME);

        for (int i = 0; i <= arg; i++) {
            KeyUtils.enterKey(ve.getDataView(), Key.TAB);
        }

        for (TextItem t : vti) {
            t.enterItem(ve.getDataView());
        }
    }

         /**
     * replaces text in vocab name.
     * @param vti vector of TextItems.
     */
    public final void replaceTextInName(final Vector<TextItem> vti) {
        this.requestFocus();

        KeyUtils.enterKey(ve.getDataView(), Key.HOME);

        ve.getDataView().selectAll();

        for (TextItem t : vti) {
            t.enterItem(ve.getDataView());
        }
    }

     /**
     * presses keys in a cell element.
     * @param keys Keys to type next
     */
    public final void pressKeys(final Key[] keys) {
        //requestFocus(element);

        KeyUtils.enterKeys(ve.getDataView(), keys);
    }

    /**
     * sets the focus to the element.
     */
    public final void requestFocus() {
       ve.getDataView().getEdTracker().focusGained(null);
    }

    /**
     * Returns the Vocab Element name.
     * @return vocab element name
     */
    public final String getVEName() {
        return ve.getDataView().getEditors().elementAt(0).getText();
    }

    /**
     * Returns argument name.
     * @param arg argument number
     * @return argument name
     */
    public final String getArgument(final int arg) {
        int editorNum = arg * 6 + 3;
        return ve.getDataView().getEditors().elementAt(editorNum).getText();
    }

}
