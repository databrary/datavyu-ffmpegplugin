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

import java.io.File;

import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test for the Deletion of cells.
 */
public final class UIDeleteCellsTest extends OpenSHAPATestClass {

    /**
     * Test for Bug 713. Bug 713: If an extended selection includes the last
     * (bottom) cell and Delete Cells is selected: Expected: All cells are
     * deleted and all related graphics are removed Actual: All cells appear to
     * be deleted but the cell graphics for the last cell are not removed.
     * Select cells in one column, delete, repeat.
     */
    @Test public void testBug713() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        File demoFile = new File(testFolder + "/ui/demo_data_small.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get the spreadsheet, check that cells do exist
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Assert.assertTrue(spreadsheet.allColumns().size() > 0,
            "Expecting columns to exist.");

        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertTrue(column.numOfCells() > 0,
                "Expecting cells to exist.");
        }

        // 3. Delete cells one column at a time
        int numColumns = spreadsheet.numOfColumns();

        while (numColumns > 0) {

            // Select all cells in a column
            for (SpreadsheetCellFixture cell
                : spreadsheet.column(numColumns - 1).allCells()) {
                cell.fillSelectCell(true);
            }

            // Delete selected cells
            mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
                "Delete Cells");

            // Verify all cells in the column are deleted
            spreadsheet = mainFrameFixture.getSpreadsheet();

            Assert.assertFalse(spreadsheet.column(numColumns - 1).numOfCells()
                > 0, "Expecting no cells in the column.");
            numColumns--;
        }

        // 4. Verify that all cells have been deleted
        spreadsheet = mainFrameFixture.getSpreadsheet();

        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertFalse(column.numOfCells() > 0,
                "Expecting no cells to exist.");
        }
    }

    /**
     * Select one cell, delete, repeat.
     */
    @Test public void testDeleteSingleCells() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        File demoFile = new File(testFolder + "/ui/demo_data_small.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get the spreadsheet, check that cells do exist
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Assert.assertTrue(spreadsheet.allColumns().size() > 0,
            "Expecting columns to exist.");

        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertTrue(column.numOfCells() > 0,
                "Expecting cells to exist.");
        }

        // 3. Delete every cell
        int numColumns = spreadsheet.numOfColumns();

        while (numColumns > 0) {
            spreadsheet = mainFrameFixture.getSpreadsheet();

            int numCells = spreadsheet.column(numColumns - 1).allCells().size();

            while (numCells > 0) {
                spreadsheet = mainFrameFixture.getSpreadsheet();

                SpreadsheetCellFixture cell = spreadsheet.column(numColumns - 1)
                    .cell(1);
                cell.fillSelectCell(true);
                mainFrameFixture.menuItemWithPath("Spreadsheet").click();
                mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
                    "Delete Cell");
                numCells--;
            }

            numColumns--;
        }

        // 4. Verify that all cells have been deleted
        spreadsheet = mainFrameFixture.getSpreadsheet();

        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertFalse(column.numOfCells() > 0,
                "Expecting no cells to exist.");
        }
    }

    /**
     * Select all cells, then delete.
     */
    @Test public void testDeleteAllCellsInSpreadsheet() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        File demoFile = new File(testFolder + "/ui/demo_data_small.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get the spreadsheet, check that cells do exist
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Assert.assertTrue(spreadsheet.allColumns().size() > 0,
            "Expecting columns to exist.");

        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertTrue(column.numOfCells() > 0,
                "Expecting cells to exist.");
        }

        // 3. Select every cell
        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {

            for (SpreadsheetCellFixture cell : column.allCells()) {
                cell.fillSelectCell(true);
            }
        }

        // 4. Delete all cells
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Delete Cells");

        // 5. Verify that all cells have been deleted
        spreadsheet = mainFrameFixture.getSpreadsheet();

        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertFalse(column.numOfCells() > 0,
                "Expecting no cells to exist.");
        }
    }
}
