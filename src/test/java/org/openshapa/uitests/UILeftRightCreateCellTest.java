package org.openshapa.uitests;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.util.Platform;

import org.openshapa.util.UIUtils;

import org.openshapa.views.discrete.SpreadsheetPanel;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Tests creating cells to left and right of a cell.
 */
public final class UILeftRightCreateCellTest extends OpenSHAPATestClass {

    @Test public void testCreateCellLeftRight() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // Left column
        createVariable("L", "TEXT");
        validateVariable("L", "TEXT");

        // Center column
        createVariable("C", "TEXT");
        validateVariable("C", "TEXT");

        // Right column
        createVariable("R", "TEXT");
        validateVariable("R", "TEXT");

        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        /*
         * Create a cell in the center column
         */
        // 1. Click on the center column
        spreadsheet.column("C").click();

        // 2. Make a new cell - using menu because numpad enter doesn't work
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        // 3. Check if the new cell has been created.
        Assert.assertTrue(cellExists("C", 1),
            "Expecting centre cell to be created.");

        final String centerOnset = "11:23:58:132";
        final String centerOffset = "12:34:56:789";
        changeOnset("C", 1, centerOnset);
        changeOffset("C", 1, centerOffset);

        /*
         * Create a cell to the left of the new center cell
         */
        // 1. Click on the center cell
        spreadsheet.column("C").click();
        clickCell("C", 1);

        // 2. Make a new cell to the left of the center cell
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
            "New Cell to Left");

        // 3. Check if the new cell has been created.
        Assert.assertTrue(cellExists("L", 1),
            "Expecting left cell to be created.");

        // 4. Check that onset and offset values have been inherited
        Assert.assertTrue(cellHasOnset("L", 1, centerOnset),
            "Expecting left cell to inherit centre cell onset value.");
        Assert.assertTrue(cellHasOffset("L", 1, centerOffset),
            "Expecting left cell to inherit centre cell offset value.");

        /*
         * Create a cell to the right of the center cell
         */
        // 1. Click on the center cell
        spreadsheet.column("C").click();
        clickCell("C", 1);

        // 2. Make a new cell to the left of the center cell
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
            "New Cell to Right");

        // 3. Check if the new cell has been created.
        Assert.assertTrue(cellExists("R", 1),
            "Expecting right cell to be created.");

        // 4. Check that onset and offset values have been inherited
        Assert.assertTrue(cellHasOnset("R", 1, centerOnset),
            "Expecting right cell to inherit centre cell onset value.");
        Assert.assertTrue(cellHasOffset("R", 1, centerOffset),
            "Expecting right cell to inherit centre cell offset value.");
    }

    @Test public void testBug698() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // Left column
        createVariable("L", "TEXT");
        validateVariable("L", "TEXT");

        // Center column
        createVariable("C", "TEXT");
        validateVariable("C", "TEXT");

        // Right column
        createVariable("R", "TEXT");
        validateVariable("R", "TEXT");

        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        /*
         * Create a cell in the each column column
         */
        for (SpreadsheetColumnFixture col : spreadsheet.allColumns()) {
            mainFrameFixture.pressKey(Platform.controlOrCommandKey());
            col.click();
        }

        mainFrameFixture.releaseKey(Platform.controlOrCommandKey());

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        /*
         * Select all cells and try to create a cell to the left.
         */
        spreadsheet.column("L").cell(1).fillSelectCell(true);
        spreadsheet.column("C").cell(1).fillSelectCell(true);
        spreadsheet.column("R").cell(1).fillSelectCell(true);

        // 2. Make a new cells to the left
        mainFrameFixture.menuItemWithPath("Spreadsheet").click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
            "New Cells to Left");

        // 3. Check that new cells have been created
        Assert.assertEquals(spreadsheet.column("L").allCells().size(), 2);
        Assert.assertEquals(spreadsheet.column("C").allCells().size(), 2);
        Assert.assertEquals(spreadsheet.column("R").allCells().size(), 1);

        /*
         * Select all cells and try to create a cell to the right.
         */
        spreadsheet.deselectAll();

        // 1. Select all cells
        spreadsheet.column("L").cell(1).fillSelectCell(true);
        spreadsheet.column("C").cell(1).fillSelectCell(true);
        spreadsheet.column("R").cell(1).fillSelectCell(true);
        spreadsheet.column("L").cell(2).fillSelectCell(true);
        spreadsheet.column("C").cell(2).fillSelectCell(true);

        // 2. Make a new cell to the right
        mainFrameFixture.menuItemWithPath("Spreadsheet").click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
            "New Cells to Right");

        // 3. Check that new cells have been created
        Assert.assertEquals(spreadsheet.column("L").allCells().size(), 2);
        Assert.assertEquals(spreadsheet.column("C").allCells().size(), 4);
        Assert.assertEquals(spreadsheet.column("R").allCells().size(), 3);
    }

    /**
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     * @param onset
     *            Should be in the format HH:mm:ss:SSS
     * @return boolean - true if has onset
     */
    private boolean cellHasOnset(final String varName, final int id,
        final String onset) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        return spreadsheet.column(varName).cell(id).onsetTimestamp().text()
            .equals(onset);
    }

    /**
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     * @param offset
     *            Should be in the format HH:mm:ss:SSS
     * @return boolean - true if has offset
     */
    private boolean cellHasOffset(final String varName, final int id,
        final String offset) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        return spreadsheet.column(varName).cell(id).offsetTimestamp().text()
            .equals(offset);
    }

    /**
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     * @param onset
     *            Should be in the format HH:mm:ss:SSS
     */
    private void changeOnset(final String varName, final int id,
        final String onset) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        spreadsheet.column(varName).cell(id).onsetTimestamp().enterText(onset);
    }

    /**
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     * @param offset
     *            Should be in the format HH:mm:ss:SSS
     */
    private void changeOffset(final String varName, final int id,
        final String offset) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        spreadsheet.column(varName).cell(id).offsetTimestamp().enterText(
            offset);
    }

    /**
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     */
    private void clickCell(final String varName, final int id) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        spreadsheet.column(varName).cell(id).fillSelectCell(true);
    }

    /**
     * @param varName
     *            column to test against, assumes that the column already exists
     * @param id
     *            cell ordinal value
     * @return true if the cell with ordinal 'id' exists, false otherwise
     */
    private boolean cellExists(final String varName, final int id) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        return id <= spreadsheet.column(varName).numOfCells();
    }

    /**
     * Creates a new variable and checks that it has been created.
     *
     * @param varName
     *            variable name
     * @param varType
     *            variable type
     */
    private void validateVariable(final String varName, final String varType) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        // 1. Check that the column exists.
        SpreadsheetColumnFixture col = ssPanel.column(varName);
        Assert.assertNotNull(col, "Expecting column to exist.");
        Assert.assertEquals(col.getColumnType(), varType);

        // 2. Check that column has no cells
        Assert.assertTrue(col.numOfCells() == 0, "Expecting no cells.");
    }

    /**
     * Creates a variable.
     *
     * @param varName
     *            variable name
     * @param varType
     *            variable type
     */
    private void createVariable(final String varName, final String varType) {

        // 1. Create new variable
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Variable");

        // 2. Enter variable name
        DialogFixture newVariableDialog = mainFrameFixture.dialog();
        newVariableDialog.requireVisible();

        JTextComponentFixture variableValueTextBox =
            newVariableDialog.textBox();
        variableValueTextBox.requireEmpty();
        variableValueTextBox.requireEditable();
        variableValueTextBox.enterText(varName);

        // 3. Choose variable type
        newVariableDialog.radioButton(varType.toLowerCase() + "TypeButton")
            .click();
        newVariableDialog.radioButton(varType.toLowerCase() + "TypeButton")
            .requireSelected();
        newVariableDialog.button("okButton").click();
    }

}
