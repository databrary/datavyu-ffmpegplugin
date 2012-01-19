/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.uitests;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JTextComponentFixture;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.Variable;

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
        .getContext().getResourceMap(Variable.class);

    /**
     * Test creating a variable with the same name. Type is selected randomly
     * since it should not affect this
     */
    @Test public void testDuplicateName() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String varName = "v";
        String varType = VAR_TYPES[(int) (Math.random() * VAR_TYPES.length)];
        String varRadio = varType.toLowerCase() + "TypeButton";
        mainFrameFixture.createNewVariable(varName, varRadio);

        spreadsheet = mainFrameFixture.getSpreadsheet();

        // 2. Check that a column has been created
        Assert.assertEquals(spreadsheet.allColumns().size(), 1);
        Assert.assertEquals(spreadsheet.column(0).getColumnName(), varName);

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
