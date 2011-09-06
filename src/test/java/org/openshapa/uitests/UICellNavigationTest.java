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

import java.awt.event.KeyEvent;

import java.io.File;

import java.util.Vector;

import javax.swing.text.BadLocationException;

import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.util.Platform;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test for the Deletion of cells.
 */
public final class UICellNavigationTest extends OpenSHAPATestClass {

    /**
     * Test movement left to right using key presses.
     */
    @Test public void testLeftRightCellNavigation() {
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

        // 3. Add cells to a vector
        Vector<SpreadsheetCellFixture> firstRowOfCells =
            new Vector<SpreadsheetCellFixture>();

        for (int i = 0; i < spreadsheet.numOfColumns(); i++) {
            firstRowOfCells.add(spreadsheet.column(i).cell(1));
        }

        // 4. Click on 1st cell in 3rd column (6 columns in total)
        firstRowOfCells.elementAt(2).borderSelectCell(true);

        // 5. Move all the way LEFT, then try to go further
        int keyModifier = KeyEvent.CTRL_MASK;

        if (Platform.isMacintosh()) {
            keyModifier = KeyEvent.ALT_MASK;
        }

        int currSelCol = 2;

        for (int i = 0; i < 4; i++) {
            spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_LEFT, keyModifier);

            int expectedCol = Math.max(0, currSelCol - 1);
            Assert.assertTrue(spreadsheet.column(expectedCol).cell(1)
                .isSelected());
            currSelCol--;
        }

        // 6. Move all the way RIGHT, then try to go further
        currSelCol = 0;

        for (int i = 0; i < 8; i++) {
            spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_RIGHT,
                keyModifier);

            int expectedCol = Math.min(5, currSelCol + 1);
            Assert.assertTrue(spreadsheet.column(expectedCol).cell(1)
                .isSelected());
            currSelCol++;
        }
    }

    /**
     * Test movement up down for text cells with multiple lines.
     */
    @Test public void testUpDownMultilineCells() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        File demoFile = new File(testFolder + "/ui/multiline-text.rb");
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

        // 3. Get column
        SpreadsheetColumnFixture col = spreadsheet.column(0);

        int numOfCells = col.numOfCells();

        // 4. Select the 6th cell (10 cells in total)
        col.cell(numOfCells - 4).borderSelectCell(true);

        // 5. Move all the way DOWN, then try to go further
        int currSelCell = numOfCells - 4;

        while (!col.cell(numOfCells).isSelected()) {

            if ((currSelCell % 2) == 0) {
                spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_DOWN);
            } else {

                for (int i = 0; i < 5; i++) {
                    spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_DOWN);
                }
            }

            int expectedCell = Math.min(numOfCells, currSelCell + 1);
            Assert.assertTrue(col.cell(expectedCell).isSelected());
            currSelCell++;
        }

        // 6. Move all the way UP, then try to go further
        currSelCell = numOfCells;

        while (!col.cell(1).isSelected()) {

            if ((currSelCell % 2) == 0) {
                spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_UP);
            } else {

                for (int i = 0; i < 5; i++) {
                    spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_UP);
                }
            }

            int expectedCell = Math.max(1, currSelCell - 1);
            Assert.assertTrue(col.cell(expectedCell).isSelected());
            currSelCell--;
        }
    }

    /**
     * Test that caret remains in the same position for up down movement in
     * cells.
     */
    @Test public void testUpDownCellsCaretPosition()
        throws BadLocationException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        File demoFile = new File(testFolder + "/ui/demo_data_caret_movement.rb");
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

        // For each column (except matrix and predicates) we set the caret to
        // the beginning and ensure it remains there.
        // For all columns, we start in the 6th cell
        for (SpreadsheetColumnFixture col : spreadsheet.allColumns()) {

            // Skip predicates and matrices
            if (col.getColumnType().equalsIgnoreCase("MATRIX")
                    || col.getColumnType().equalsIgnoreCase("PREDICATE")) {
                continue;
            }

            int numOfCells = col.numOfCells();

            col.cell(numOfCells - 4).clickToCharPos(SpreadsheetCellFixture.VALUE, 0, 1);

            Assert.assertEquals(col.cell(numOfCells - 4).cellValue().component()
                .getCaretPosition(), 0);

            // Move up cell by cell and confirm that the caret remains in
            // the same position
            for (int i = numOfCells - 5; i >= 1; i--) {
                spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_UP);
                Assert.assertEquals(col.cell(i).cellValue().component()
                    .getCaretPosition(), 0);
            }

            // Move down cell by cell and confirm that caret remains in the
            // same position
            for (int i = 2; i <= numOfCells; i++) {
                spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_DOWN);
                Assert.assertEquals(col.cell(i).cellValue().component()
                    .getCaretPosition(), 0);
            }

            // Move back up cell by cell and confirm that the caret remains in
            // the same position
            for (int i = numOfCells - 1; i >= 1; i--) {
                spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_UP);
                Assert.assertEquals(col.cell(i).cellValue().component()
                    .getCaretPosition(), 0);
            }
        }

        // For each column (except matrix and predicates) we set the caret to
        // the 3rd position and ensure that it remains there or its moves
        // appropriately if the value has less than 3 characters
        // For all columns, we start in the 6th cell
        for (SpreadsheetColumnFixture col : spreadsheet.allColumns()) {

            // Skip predicates and matrices
            if (col.getColumnType().equalsIgnoreCase("MATRIX")
                    || col.getColumnType().equalsIgnoreCase("PREDICATE")) {
                continue;
            }

            // Calculations assume number of cells is 10. If script is changed
            // they need to change.
            int numOfCells = col.numOfCells();

            int defaultPos = 3;
            col.cell(numOfCells - 4).clickToCharPos(
                SpreadsheetCellFixture.VALUE, defaultPos,
                1);

            if (col.cell(numOfCells - 4).cellValue().text().length()
                    < defaultPos) {
                defaultPos = col.cell(1).cellValue().text().length();
            }

            Assert.assertEquals(col.cell(numOfCells - 4).cellValue().component()
                .getCaretPosition(), defaultPos);

            // Move up cell by cell and confirm that caret remains in
            // the same position
            for (int i = numOfCells - 5; i >= 1; i--) {
                spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_UP);

                if (col.cell(i).cellValue().text().length() < defaultPos) {
                    defaultPos = col.cell(i).cellValue().text().length();
                }

                Assert.assertEquals(col.cell(i).cellValue().component()
                    .getCaretPosition(), defaultPos);
            }

            // Move down cell by cell and confirm that caret remains in the
            // same position
            for (int i = 2; i <= numOfCells; i++) {
                spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_DOWN);

                if (col.cell(i).cellValue().text().length() < defaultPos) {
                    defaultPos = col.cell(i).cellValue().text().length();
                }

                Assert.assertEquals(col.cell(i).cellValue().component()
                    .getCaretPosition(), defaultPos);
            }

            // Move back up cell by cell and confirm that caret remains in
            // the same position
            for (int i = numOfCells - 1; i >= 1; i--) {
                spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_UP);

                if (col.cell(i).cellValue().text().length() < defaultPos) {
                    defaultPos = col.cell(i).cellValue().text().length();
                }

                Assert.assertEquals(col.cell(i).cellValue().component()
                    .getCaretPosition(), defaultPos);
            }
        }
    }
}
