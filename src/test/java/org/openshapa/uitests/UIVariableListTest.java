package org.openshapa.uitests;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.File;



import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.data.TableCell;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.util.Platform;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.openshapa.OpenSHAPA;

import org.openshapa.views.NewProjectV;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test the creation of a new database.
 */
public final class UIVariableListTest extends OpenSHAPATestClass {

    /**
     * Resource map to access error messages in resources.
     */
    private ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
        .getContext().getResourceMap(NewProjectV.class);

    /**
     * Test adding new variables with a script.
     */
    @Test public void testAddingVariablesWithScript() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        
        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsole();

        // 2. Check that variable list is populated with correct data
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Variable List");

        DialogFixture vlDialog = mainFrameFixture.dialog();

        spreadsheet = mainFrameFixture.getSpreadsheet();

        Assert.assertEquals(vlDialog.table().rowCount(),
            spreadsheet.allColumns().size());

        for (int i = 0; i < spreadsheet.allColumns().size(); i++) {
            Assert.assertTrue(inTable(
                    spreadsheet.allColumns().elementAt(i).getColumnName(),
                    vlDialog.table(), 1));
            Assert.assertTrue(inTable(
                    spreadsheet.allColumns().elementAt(i).getColumnType(),
                    vlDialog.table(), 2));
        }
    }

    /**
     * Test adding new variables manually.
     */
    @Test public void testAddingVariablesManually() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String[] varNames = {"t", "p", "i", "n", "m", "f"};
        String[] varTypes = {
                "text", "predicate", "integer", "nominal", "matrix", "float"
            };

        // 1. Create a new variable, then check that variable list is
        // populated with correct data
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Variable List");

        DialogFixture vlDialog = mainFrameFixture.dialog();

        spreadsheet = mainFrameFixture.getSpreadsheet();

        int numCols = 0;

        for (int i = 0; i < varNames.length; i++) {
            mainFrameFixture.createNewVariable(varNames[i], varTypes[i]);
            numCols++;

            // Check that variable list is populated with correct data
            Assert.assertEquals(spreadsheet.allColumns().size(), numCols);
            Assert.assertEquals(vlDialog.table().rowCount(),
                spreadsheet.allColumns().size());

            for (int j = 0; j < spreadsheet.allColumns().size(); j++) {
                Assert.assertTrue(inTable(
                        spreadsheet.allColumns().elementAt(j).getColumnName(),
                        vlDialog.table(), 1));
                Assert.assertTrue(inTable(
                        spreadsheet.allColumns().elementAt(j).getColumnType(),
                        vlDialog.table(), 2));
            }
        }
    }

    /**
     * Test adding new variables with a script.
     */
    @Test public void testRemovalWithNewDatabase() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        
        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsole();

        // 2. Check that variable list is populated
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Variable List");

        DialogFixture vlDialog = mainFrameFixture.dialog();

        spreadsheet = mainFrameFixture.getSpreadsheet();

        Assert.assertEquals(vlDialog.table().rowCount(),
            spreadsheet.allColumns().size());

        // 3. Create new database (and discard unsaved changes)
        if (Platform.isOSX()) {
            mainFrameFixture.pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_N).modifiers(InputEvent.META_MASK));
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "New");
        }

        try {
            JOptionPaneFixture warning = mainFrameFixture.optionPane();
            warning.requireTitle("Unsaved changes");
            warning.buttonWithText("OK").click();
        } catch (Exception e) {
            // Do nothing
        }

        DialogFixture newProjectDialog = mainFrameFixture.dialog("NewProjectV");
        
        newProjectDialog.textBox("nameField").enterText("n");

        newProjectDialog.button("okButton").click();

        // 4. Check that variable list is empty
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Variable List");

        vlDialog = mainFrameFixture.dialog();
        Assert.assertTrue(vlDialog.table().rowCount() == 0);

    }

    /**
     * Because variable list is not in order, checks if String is in a Table
     * column.
     *
     * @param item
     *            String to find
     * @param t
     *            Table to look in
     * @param col
     *            Column number
     * @return true if found, else false
     */
    private Boolean inTable(final String item, final JTableFixture t,
        final int col) {

        for (int i = 0; i < t.rowCount(); i++) {

            if (item.equals(t.valueAt(TableCell.row(i).column(col)))) {
                return true;
            }
        }

        return false;
    }
}
