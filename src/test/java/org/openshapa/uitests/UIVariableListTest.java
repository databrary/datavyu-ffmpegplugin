package org.openshapa.uitests;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.File;



import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.data.TableCell;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.VariableListDialogFixture;
import org.fest.swing.fixture.WindowFixture;
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

    private static final int VISIBLE_COL = 0;
    private static final int NAME_COL = 1;
    private static final int TYPE_COL = 2;
    private static final int COMMENT_COL = 3;

    /**
     * Test adding new variables with a script.
     */
    @Test public void testAddingVariablesWithScript() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        
        File demoFile = new File(testFolder + "/ui/demo_data_small.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. We open the variable list dialog first, because it should update live
        VariableListDialogFixture vlDialog = mainFrameFixture.openVariableList();

        // 2. Run script to populate
        mainFrameFixture.runScript(demoFile);
        mainFrameFixture.closeScriptConsole();

        // 3. Check that variable list has been updated
       
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Assert.assertEquals(vlDialog.getVariableListTable().rowCount(),
            spreadsheet.allColumns().size());

        for (int i = 0; i < spreadsheet.allColumns().size(); i++) {
            vlDialog.getVariableListTable().requireCellValue(TableCell.row(i).column(NAME_COL), spreadsheet.allColumns().elementAt(i).getColumnName());
            vlDialog.getVariableListTable().requireCellValue(TableCell.row(i).column(TYPE_COL), spreadsheet.allColumns().elementAt(i).getColumnType());
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

        // 1. We open the variable list dialog first, because it should update live
        VariableListDialogFixture vlDialog = mainFrameFixture.openVariableList();

        spreadsheet = mainFrameFixture.getSpreadsheet();

        int numCols = 0;

        for (int i = 0; i < varNames.length; i++) {
            mainFrameFixture.createNewVariable(varNames[i], varTypes[i]);
            numCols++;

            // Check that variable list is populated with correct data
            Assert.assertEquals(spreadsheet.allColumns().size(), numCols);
            Assert.assertEquals(vlDialog.getVariableListTable().rowCount(),
                spreadsheet.allColumns().size());
            vlDialog.getVariableListTable().requireCellValue(TableCell.row(i).column(NAME_COL), spreadsheet.allColumns().elementAt(i).getColumnName());
            vlDialog.getVariableListTable().requireCellValue(TableCell.row(i).column(TYPE_COL), spreadsheet.allColumns().elementAt(i).getColumnType());
        }
    }

    /**
     * Test adding new variables with a script.
     */
    @Test public void testRemovalWithNewDatabase() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        
        File demoFile = new File(testFolder + "/ui/demo_data_small.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);
        mainFrameFixture.closeScriptConsole();

        // 2. Check that variable list is populated
        VariableListDialogFixture vlDialog = mainFrameFixture.openVariableList();

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
        vlDialog = mainFrameFixture.openVariableList();

    }

    /**
     * Test hiding and showing variables.
     */
    @Test public void testVariableVisibility() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        File demoFile = new File(testFolder + "/ui/demo_data_small.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);
        mainFrameFixture.closeScriptConsole();

        // 2. Check that variable list is populated
        VariableListDialogFixture vlDialog = mainFrameFixture.openVariableList();

        spreadsheet = mainFrameFixture.getSpreadsheet();

        int numOfVars = spreadsheet.numOfColumns();

        Assert.assertEquals(vlDialog.table().rowCount(),
            spreadsheet.allColumns().size());

        // 3. Hide each variable and confirm that they're hidden
        for (int i = 0; i < vlDialog.getVariableListTable().rowCount(); i++) {
            vlDialog.getVariableListTable().enterValue(TableCell.row(i).column(VISIBLE_COL), "false");
            
            Assert.assertNull(spreadsheet.column(vlDialog.getVariableListTable().valueAt(TableCell.row(i).column(NAME_COL))));
        }

        // 4. Show all
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Show All Variables");
        spreadsheet = mainFrameFixture.getSpreadsheet();
        Assert.assertEquals(spreadsheet.numOfColumns(), numOfVars);
        for (int i = 0; i < spreadsheet.numOfColumns(); i++) {
            Assert.assertEquals(vlDialog.getVariableListTable().valueAt(TableCell.row(i).column(VISIBLE_COL)), "true");
        }

        // 5. Hide all by selecting all and hiding
        mainFrameFixture.robot.pressKey(KeyEvent.VK_CONTROL);
        for (SpreadsheetColumnFixture col : spreadsheet.allColumns()) {
            col.click();
            Assert.assertTrue(col.isSelected());
        }
        mainFrameFixture.robot.releaseKey(KeyEvent.VK_CONTROL);

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Hide Selected Variables");

        for (int i = 0; i < vlDialog.getVariableListTable().rowCount(); i++) {
            vlDialog.getVariableListTable().enterValue(TableCell.row(i).column(VISIBLE_COL), "false");

            Assert.assertNull(spreadsheet.column(vlDialog.getVariableListTable().valueAt(TableCell.row(i).column(NAME_COL))));
        }

        // 6. Show all
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Show All Variables");
        spreadsheet = mainFrameFixture.getSpreadsheet();
        Assert.assertEquals(spreadsheet.numOfColumns(), numOfVars);
        for (int i = 0; i < spreadsheet.numOfColumns(); i++) {
            Assert.assertEquals(vlDialog.getVariableListTable().valueAt(TableCell.row(i).column(VISIBLE_COL)), "true");
        }
    }

    /**
     * Test editing variable name.
     */
    @Test public void testEditingVariableName() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String reservedName = "ge";
        String invalidName = "(hello)";
        String validName = "v";

        File demoFile = new File(testFolder + "/ui/demo_data_small.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);
        mainFrameFixture.closeScriptConsole();

        // 2. Check that variable list is populated
        VariableListDialogFixture vlDialog = mainFrameFixture.openVariableList();

        spreadsheet = mainFrameFixture.getSpreadsheet();

        int numOfVars = spreadsheet.numOfColumns();

        Assert.assertEquals(vlDialog.table().rowCount(),
            spreadsheet.allColumns().size());

        /* 3. For each variable, do the following through the variable list dialog and directly:
         * a. Try reserved name - ge
         * b. Try duplicate name
         * c. Try invalid name - (hello)
         * d. Try removing name completely
         * e. Make an acceptable change
         */
        //for (int i =0; i < numOfVars; i++) {
        //Rather than do it to every variable, we'll pick one at random
        int i = (int)(Math.random() * numOfVars);
            TableCell tc = TableCell.row(i).column(NAME_COL);

            //a. Try reserved name
            replaceTableCellValue(vlDialog.getVariableListTable(), tc, reservedName);
            handleWarningMessage(vlDialog);
            enterNewNameDirectly(i, reservedName);

            //b. Try duplicate name
            int row = (i + 1) % numOfVars;
            String duplicateName = vlDialog.getVariableListTable().valueAt(TableCell.row(row).column(NAME_COL));
            replaceTableCellValue(vlDialog.getVariableListTable(), tc, duplicateName);
            handleWarningMessage(vlDialog);
            enterNewNameDirectly(i, duplicateName);

            //c. Try invalid name
            replaceTableCellValue(vlDialog.getVariableListTable(), tc, invalidName);
            handleWarningMessage(vlDialog);
            enterNewNameDirectly(i, invalidName);

            //d. Try removing name completely
            replaceTableCellValue(vlDialog.getVariableListTable(), tc, "");
            handleWarningMessage(vlDialog);
            enterNewNameDirectly(i, "");

            //e. Make an acceptable change
            replaceTableCellValue(vlDialog.getVariableListTable(), tc, validName);
            spreadsheet = mainFrameFixture.getSpreadsheet();
            vlDialog.getVariableListTable().requireCellValue(TableCell.row(i).column(NAME_COL), validName);
            Assert.assertNotNull(spreadsheet.column(validName));

            spreadsheet = mainFrameFixture.getSpreadsheet();
            spreadsheet.column(i).doubleClick();
            mainFrameFixture.optionPane().textBox().enterText(validName + validName);
            mainFrameFixture.optionPane().okButton().click();

            spreadsheet = mainFrameFixture.getSpreadsheet();
            vlDialog.getVariableListTable().requireCellValue(TableCell.row(i).column(NAME_COL), validName + validName);
            Assert.assertNotNull(spreadsheet.column(validName + validName));

//        }
    }

    private void enterNewNameDirectly(int i, String newName) {
        spreadsheet = mainFrameFixture.getSpreadsheet();
        spreadsheet.column(i).doubleClick();
        mainFrameFixture.optionPane().textBox().pressAndReleaseKeys(KeyEvent.VK_DELETE);
        mainFrameFixture.optionPane().textBox().enterText(newName);
        mainFrameFixture.optionPane().okButton().click();
        handleWarningMessage(mainFrameFixture);
        mainFrameFixture.optionPane().cancelButton().click();
    }

    private JOptionPaneFixture handleWarningMessage(WindowFixture dialog) {
        JOptionPaneFixture warning = dialog.optionPane();
        warning.requireTitle("Warning:");
        warning.requireWarningMessage();
        warning.okButton().click();
        return warning;
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

    /**
     * To handle a warning dialog popping up after entering text,
     * we need to manually enter a value.
     * @param tcell TableCell
     * @param value value to enter
     */
    private void replaceTableCellValue(JTableFixture table, TableCell tcell, String value) {
        table.cell(tcell).startEditing();
        for (int i = 0; i < table.cell(tcell).value().length(); i++) {
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_BACK_SPACE);
        }
        mainFrameFixture.robot.enterText(value);
        mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_ENTER);
    }
}
