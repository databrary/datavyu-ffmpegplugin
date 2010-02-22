package org.openshapa.uitests;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.openshapa.util.UIUtils;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Fest test for Bug 308. When an error occurs (such as a duplicate variable
 * name), the New Variable window just disappears rather than allowing the user
 * to fix the problem.
 */
public final class UIBug308Test extends OpenSHAPATestClass {

    /**
     * Different cell variable types.
     */
    private static final String[] VAR_TYPES =
            { "TEXT", "PREDICATE", "INTEGER", "NOMINAL", "MATRIX", "FLOAT" };

    /**
     * Test creating a new variable. Then try to create variable with same name.
     * Type is selected randomly since it should not affect this.
     */
    @Test
    public void testDuplicateName() {
        System.err.println("testDuplicateName");
        String varName = "v";
        String varType = VAR_TYPES[(int) (Math.random() * VAR_TYPES.length)];
        String varRadio = varType.toLowerCase() + "TypeButton";
        UIUtils.createNewVariable(mainFrameFixture, varName, varRadio);

        // 2. Check that a column has been created
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);

        // Find our new column header
        SpreadsheetPanelFixture ssPanel =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());
        Assert.assertNotNull(ssPanel.column(varName));

        // 3. Create variable with same name
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Variable");
        // Find the new variable dialog
        DialogFixture newVariableDialog = mainFrameFixture.dialog();
        // Check if the new variable dialog is actually visible
        newVariableDialog.requireVisible();
        // Get the variable value text box
        JTextComponentFixture variableValueTextBox =
                newVariableDialog.textBox();
        // The variable value box should have no text in it
        variableValueTextBox.requireEmpty();
        // It should be editable
        variableValueTextBox.requireEditable();
        // Type in some text.
        variableValueTextBox.enterText(varName);
        // Get the radio button for text variables
        newVariableDialog.radioButton(varRadio).click();
        // Check that it is selected
        newVariableDialog.radioButton(varRadio).requireSelected();
        // Click "OK"
        newVariableDialog.button("okButton").click();

        JOptionPaneFixture warning = newVariableDialog.optionPane();
        warning.requireTitle("Warning:");
        warning.buttonWithText("OK").click();
    }
}
