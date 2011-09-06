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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.util.Platform;
import org.openshapa.util.UIUtils;
import org.testng.annotations.Test;

/**
 * Tests creating cells to left and right of a cell.
 */
public final class UICreateCellTest extends OpenSHAPATestClass {

    /**
     * Test creating new cells with the new cell button.
     */
    @Test
    public void testCreateNewCellWithButton() {
        printTestName();

        // Create column of each type
        for (String var : UIUtils.VAR_TYPES) {
            mainFrameFixture.createNewVariable(var.substring(0, 1), var);
        }

        spreadsheet = mainFrameFixture.getSpreadsheet();
        assertEquals(UIUtils.VAR_TYPES.length, spreadsheet.allColumns().size());

        // Create cell for each column
        for (SpreadsheetColumnFixture col : spreadsheet.allColumns()) {
            col.pressNewCellButton();
            assertEquals(1, col.allCells().size());
        }
    }

    /**
     * Test creating cells to the left and right.
     */
    @Test
    public void testCreateCellLeftRight() {
        printTestName();

        // Left column
        mainFrameFixture.createNewVariable("L", "TEXT");
        validateVariable("L", "TEXT");

        // Center column
        mainFrameFixture.createNewVariable("C", "TEXT");
        validateVariable("C", "TEXT");

        // Right column
        mainFrameFixture.createNewVariable("R", "TEXT");
        validateVariable("R", "TEXT");

        spreadsheet = mainFrameFixture.getSpreadsheet();

        /*
         * Create a cell in the center column
         */
        // 1. Click on the center column
        spreadsheet.column("C").click();

        // 2. Make a new cell - using menu because numpad enter doesn't work
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        // 3. Check if the new cell has been created.
        assertTrue("Expecting centre cell to be created.", cellExists("C", 1));

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
        assertTrue("Expecting left cell to be created.", cellExists("L", 1));

        // 4. Check that onset and offset values have been inherited
        assertTrue("Expecting left cell to inherit centre cell onset value.",
                cellHasOnset("L", 1, centerOnset));
        assertTrue("Expecting left cell to inherit centre cell offset value.",
                cellHasOffset("L", 1, centerOffset));

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
        assertTrue("Expecting right cell to be created.", cellExists("R", 1));

        // 4. Check that onset and offset values have been inherited
        assertTrue("Expecting right cell to inherit centre cell onset value.",
                cellHasOnset("R", 1, centerOnset));
        assertTrue("Expecting right cell to inherit centre cell offset value.",
                cellHasOffset("R", 1, centerOffset));
    }

    /**
     * Test for bug 698.
     */
    @Test
    public void testBug698() {
        printTestName();

        // Left column
        mainFrameFixture.createNewVariable("L", "TEXT");
        validateVariable("L", "TEXT");

        // Center column
        mainFrameFixture.createNewVariable("C", "TEXT");
        validateVariable("C", "TEXT");

        // Right column
        mainFrameFixture.createNewVariable("R", "TEXT");
        validateVariable("R", "TEXT");

        spreadsheet = mainFrameFixture.getSpreadsheet();

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
        assertEquals(2, spreadsheet.column("L").allCells().size());
        assertEquals(2, spreadsheet.column("C").allCells().size());
        assertEquals(1, spreadsheet.column("R").allCells().size());

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
        assertEquals(2, spreadsheet.column("L").allCells().size());
        assertEquals(4, spreadsheet.column("C").allCells().size());
        assertEquals(3, spreadsheet.column("R").allCells().size());
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
        spreadsheet = mainFrameFixture.getSpreadsheet();

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
        spreadsheet = mainFrameFixture.getSpreadsheet();

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
        spreadsheet = mainFrameFixture.getSpreadsheet();

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
        spreadsheet = mainFrameFixture.getSpreadsheet();

        spreadsheet.column(varName).cell(id).offsetTimestamp()
                .enterText(offset);
    }

    /**
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     */
    private void clickCell(final String varName, final int id) {
        spreadsheet = mainFrameFixture.getSpreadsheet();

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
        spreadsheet = mainFrameFixture.getSpreadsheet();

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
        spreadsheet = mainFrameFixture.getSpreadsheet();

        // 1. Check that the column exists.
        SpreadsheetColumnFixture col = spreadsheet.column(varName);
        assertNotNull("Expecting column to exist.", col);
        assertEquals(varType, col.getColumnType());

        // 2. Check that column has no cells
        assertTrue("Expecting no cells.", col.numOfCells() == 0);
    }
}
