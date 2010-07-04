package org.openshapa.uitests;

import javax.swing.text.JTextComponent;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Bug695 Description: Open VocEd and create a new matrix. Click OK Delete
 * matrix column from spreadsheet. Reopen VocEd and create a new matrix. Click
 * Apply. Expect: New martix created Actual: "ve name in use" warning
 */
public final class UIBug695Test extends OpenSHAPATestClass {

    /** Matrix element name. */
    public static final String MATELNAME = "matrix1";

    /**
     * Test Bug 695.
     */
    @Test public void testBug695() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Add matrix with vocab editor

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        DialogFixture vocabEditor = mainFrameFixture.dialog();

        vocabEditor.button("addMatrixButton").click();

        // 2. Confirm matrix exists in vocab editor and spreadsheet
        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
                @Override protected boolean isMatching(
                    final JTextComponent vocEl) {
                    return vocEl.getText().startsWith("matrix1");
                }
            });

        vocabEditor.button("okButton").click();

        spreadsheet = mainFrameFixture.getSpreadsheet();
        Assert.assertNotNull(spreadsheet.column(MATELNAME));

        // 3. Delete matrix column in spreadsheet
        spreadsheet.column(MATELNAME).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
            "Delete Variable");

        // 4. Confirm matrix deleted in vocab editor and spreadsheet
        boolean doesNotExist = false;

        try {
            Assert.assertFalse(spreadsheet.panel("headerView").label().text()
                .startsWith(MATELNAME));
        } catch (Exception e) {
            doesNotExist = true;
        }

        Assert.assertTrue(doesNotExist);

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        vocabEditor = mainFrameFixture.dialog();
        doesNotExist = false;

        try {
            vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                    JTextComponent.class) {
                    @Override protected boolean isMatching(
                        final JTextComponent vocEl) {
                        return vocEl.getText().startsWith(MATELNAME);
                    }
                });
        } catch (Exception e) {
            doesNotExist = true;
        }

        Assert.assertTrue(doesNotExist);

        // 5. Add matrix again with vocab editor
        vocabEditor.button("addMatrixButton").click();

        // 6. Confirm matrix in spreadsheet and vocab editorwill fail
        // if can not find
        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
                @Override protected boolean isMatching(
                    final JTextComponent vocEl) {
                    return vocEl.getText().startsWith(MATELNAME);
                }
            });
        vocabEditor.button("okButton").click();

        spreadsheet = mainFrameFixture.getSpreadsheet();
        Assert.assertNotNull(spreadsheet.column(MATELNAME));
    }
}
