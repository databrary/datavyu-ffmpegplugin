package org.openshapa.uitests;

import java.io.File;

import java.util.Vector;

import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.util.Platform;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test for the Deletion of columns.
 */
public final class UIDeleteColumnsTest extends OpenSHAPATestClass {

    /**
     * Test for deletion of columns. Delete columns one by one.
     */
    @Test public void testDeleteSingleColumns() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsole();

        // 2. Sequentially select each column and delete
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Vector<SpreadsheetColumnFixture> cols = spreadsheet.allColumns();

        for (SpreadsheetColumnFixture col : cols) {
            col.click();
            mainFrameFixture.menuItemWithPath("Spreadsheet").click();
            mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
                "Delete Variable");

            // Confirm column no longer exists
            Assert.assertNull(spreadsheet.column(col.getColumnName()));
        }

        Assert.assertTrue(spreadsheet.numOfColumns() == 0);
    }

    /**
     * Test for deletion of columns. Delete all columns at once
     */
    @Test public void testDeleteMultipleColumns() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsole();

        // 2. Select all columns and delete
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Vector<SpreadsheetColumnFixture> cols = spreadsheet.allColumns();

        for (SpreadsheetColumnFixture col : cols) {
// mainFrameFixture.pressKey(KeyEvent.VK_CONTROL);
            mainFrameFixture.pressKey(Platform.controlOrCommandKey());
            col.click();
        }
// mainFrameFixture.releaseKey(KeyEvent.VK_CONTROL);

        mainFrameFixture.releaseKey(Platform.controlOrCommandKey());
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
            "Delete Variables");
        Assert.assertTrue(spreadsheet.numOfColumns() == 0);
    }
}
