package org.openshapa.uitests;

import java.awt.event.KeyEvent;

import java.io.File;

import java.util.Vector;

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
public final class UICellNavigationTest extends OpenSHAPATestClass {

    /**
     * Test movement left to right using key presses.
     */
    /*//@Test*/ public void testLeftRightCellNavigation() {
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
        DialogFixture scriptConsole = mainFrameFixture.dialog(Timeout.timeout(
                    1000));

        long currentTime = System.currentTimeMillis();
        long maxTime = currentTime + UIUtils.SCRIPT_LOAD_TIMEOUT; // timeout

        while ((System.currentTimeMillis() < maxTime) &&
                (!scriptConsole.textBox().text().contains("Finished"))) {
            Thread.yield();
        }

        scriptConsole.button("closeButton").click();

        // 2. Get the spreadsheet, check that cells do exist
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

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
    /*//@Test*/ public void testUpDownMultilineCells() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/multiline-text.rb");
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
        DialogFixture scriptConsole = mainFrameFixture.dialog(Timeout.timeout(
                    1000));

        long currentTime = System.currentTimeMillis();
        long maxTime = currentTime + UIUtils.SCRIPT_LOAD_TIMEOUT; // timeout

        while ((System.currentTimeMillis() < maxTime) &&
                (!scriptConsole.textBox().text().contains("Finished"))) {
            Thread.yield();
        }

        scriptConsole.button("closeButton").click();

        // 2. Get the spreadsheet, check that cells do exist
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        Assert.assertTrue(spreadsheet.allColumns().size() > 0,
            "Expecting columns to exist.");

        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertTrue(column.numOfCells() > 0,
                "Expecting cells to exist.");
        }

        // 3. Get column
        SpreadsheetColumnFixture col = spreadsheet.column(0);

        // 4. Select the 6th cell (10 cells in total)
        col.cell(6).borderSelectCell(true);

        // 5. Move all the way DOWN, then try to go further
        int currSelCell = 6;

        while (!col.cell(10).isSelected()) {

            if ((currSelCell % 2) == 0) {
                spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_DOWN);
            } else {

                for (int i = 0; i < 5; i++) {
                    spreadsheet.robot.pressAndReleaseKey(KeyEvent.VK_DOWN);
                }
            }

            int expectedCell = Math.min(10, currSelCell + 1);
            Assert.assertTrue(col.cell(expectedCell).isSelected());
            currSelCell++;
        }

        // 6. Move all the way UP, then try to go further
        currSelCell = 10;

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
}
