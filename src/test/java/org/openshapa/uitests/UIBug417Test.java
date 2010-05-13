package org.openshapa.uitests;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.openshapa.OpenSHAPA;

import org.openshapa.models.db.Column;

import org.openshapa.util.UIUtils;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Bug 417 Test Check that reserved vocab variable names give a different error
 * message to already existing variables. Also make sure variations of reserved
 * vocabulary are allowed.
 */
public final class UIBug417Test extends OpenSHAPATestClass {

    /**
     * Different cell variable types.
     */
    private static final String[] VAR_TYPES = {
            "TEXT", "PREDICATE", "INTEGER", "NOMINAL", "MATRIX", "FLOAT"
        };

    /**
     * Resource map to access error messages in resources.
     */
    private ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
        .getContext().getResourceMap(Column.class);

    /**
     * Test creating a variable with the same name. Type is selected randomly
     * since it should not affect this
     */
    @Test public void testDuplicateName() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String varName = "v";
        String varType = VAR_TYPES[(int) (Math.random() * VAR_TYPES.length)];
        String varRadio = varType.toLowerCase() + "TypeButton";
        UIUtils.createNewVariable(mainFrameFixture, varName, varRadio);

        // 2. Check that a column has been created
        JPanelFixture ssPanel = UIUtils.getSpreadsheet(mainFrameFixture);

        // Find our new column header
        ssPanel.panel("headerView").label().text().startsWith(varName);

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
        Assert.assertNotNull(warning.label("OptionPane.label").text());
        Assert.assertTrue(warning.label("OptionPane.label").text().length()
            > 1);
        warning.requireMessage(rMap.getString("Error.exists", varName));
        warning.buttonWithText("OK").click();
    }

    /**
     * Test creating a variable with a reserved name.
     */
    @Test public void testReservedName() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String varName = "ge";
        String varType = VAR_TYPES[(int) (Math.random() * VAR_TYPES.length)];
        String varRadio = varType.toLowerCase() + "TypeButton";

        // 1. Create new variable
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
        Assert.assertNotNull(warning.label("OptionPane.label").text());
        Assert.assertTrue(warning.label("OptionPane.label").text().length()
            > 1);
        warning.requireMessage(rMap.getString("Error.system", varName));
        warning.buttonWithText("OK").click();
    }

    /**
     * Test invalid column name.
     */
    @Test public void testInvalidColumnName() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String varName = "(hello)";
        String varType = VAR_TYPES[(int) (Math.random() * VAR_TYPES.length)];
        String varRadio = varType.toLowerCase() + "TypeButton";

        // 1. Create new variable
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
        Assert.assertNotNull(warning.label("OptionPane.label").text());
        Assert.assertTrue(warning.label("OptionPane.label").text().length()
            > 1);
        warning.requireMessage(rMap.getString("Error.invalid", varName));
        warning.buttonWithText("OK").click();
    }
}
