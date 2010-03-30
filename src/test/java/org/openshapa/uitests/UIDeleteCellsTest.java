package org.openshapa.uitests;

import java.io.File;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;
import org.openshapa.util.UIUtils;
import org.openshapa.views.discrete.SpreadsheetPanel;
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
    /*@Test*/
    public void testBug713() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data_small.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        if (Platform.isOSX()) {
            UIUtils.runScript(demoFile);
        } else {
            mainFrameFixture.clickMenuItemWithPath("Script", "Run script");

            JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
            jfcf.selectFile(demoFile).approve();
        }

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog(Timeout.timeout(1000));
        while (!scriptConsole.textBox().text().endsWith("Finished\n")) {
            Thread.yield();
        }
        scriptConsole.button("closeButton").click();

        // 2. Get the spreadsheet, check that cells do exist
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

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
            for (SpreadsheetCellFixture cell : spreadsheet.column(
                    numColumns - 1).allCells()) {
                cell.fillSelectCell(true);
            }
            // Delete selected cells
            mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
                    "Delete Cells");

            // Verify all cells in the column are deleted
            jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
            spreadsheet =
                    new SpreadsheetPanelFixture(mainFrameFixture.robot,
                            (SpreadsheetPanel) jPanel.component());

            Assert.assertFalse(
                    spreadsheet.column(numColumns - 1).numOfCells() > 0,
                    "Expecting no cells in the column.");
            numColumns--;
        }

        // 4. Verify that all cells have been deleted
        jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertFalse(column.numOfCells() > 0,
                    "Expecting no cells to exist.");
        }
    }

    /**
     * Select one cell, delete, repeat.
     */
    /*@Test*/
    public void testDeleteSingleCells() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data_small.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        if (Platform.isOSX()) {
            UIUtils.runScript(demoFile);
        } else {
            mainFrameFixture.clickMenuItemWithPath("Script", "Run script");

            JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
            jfcf.selectFile(demoFile).approve();
        }

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog(Timeout.timeout(1000));
        while (!scriptConsole.textBox().text().endsWith("Finished\n")) {
            Thread.yield();
        }
        scriptConsole.button("closeButton").click();

        // 2. Get the spreadsheet, check that cells do exist
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        Assert.assertTrue(spreadsheet.allColumns().size() > 0,
                "Expecting columns to exist.");
        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertTrue(column.numOfCells() > 0,
                    "Expecting cells to exist.");
        }

        // 3. Delete every cell
        int numColumns = spreadsheet.numOfColumns();
        while (numColumns > 0) {
            jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
            spreadsheet =
                    new SpreadsheetPanelFixture(mainFrameFixture.robot,
                            (SpreadsheetPanel) jPanel.component());
            int numCells = spreadsheet.column(numColumns - 1).allCells().size();
            while (numCells > 0) {
                jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
                spreadsheet =
                        new SpreadsheetPanelFixture(mainFrameFixture.robot,
                                (SpreadsheetPanel) jPanel.component());
                SpreadsheetCellFixture cell =
                        spreadsheet.column(numColumns - 1).cell(1);
                cell.fillSelectCell(true);
                mainFrameFixture.menuItemWithPath("Spreadsheet").click();
                mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
                        "Delete Cell");
                numCells--;
            }
            numColumns--;
        }

        // 4. Verify that all cells have been deleted
        jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertFalse(column.numOfCells() > 0,
                    "Expecting no cells to exist.");
        }
    }

    /**
     * Select all cells, then delete.
     */
    /*@Test*/
    public void testDeleteAllCellsInSpreadsheet() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data_small.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        if (Platform.isOSX()) {
            UIUtils.runScript(demoFile);
        } else {
            mainFrameFixture.clickMenuItemWithPath("Script", "Run script");

            JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
            jfcf.selectFile(demoFile).approve();
        }

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog(Timeout.timeout(1000));
        while (!scriptConsole.textBox().text().endsWith("Finished\n")) {
            Thread.yield();
        }
        scriptConsole.button("closeButton").click();

        // 2. Get the spreadsheet, check that cells do exist
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

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
        jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertFalse(column.numOfCells() > 0,
                    "Expecting no cells to exist.");
        }
    }
}
