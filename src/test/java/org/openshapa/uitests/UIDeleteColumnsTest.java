package org.openshapa.uitests;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.openshapa.util.UIUtils;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for the Deletion of columns.
 */
public final class UIDeleteColumnsTest extends OpenSHAPATestClass {
    /**
     * Test for deletion of columns. Delete columns one by one.
     */
    @Test
    public void testDeleteSingleColumns() {
        System.err.println("testDeleteSingleColumns");
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.clickMenuItemWithPath("Script", "Run script");

        JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
        jfcf.selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // 2. Sequentially select each column and delete
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);

        SpreadsheetPanelFixture ssPanel =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        Vector<SpreadsheetColumnFixture> cols = ssPanel.allColumns();
        for (SpreadsheetColumnFixture col : cols) {
            col.click();
            mainFrameFixture.clickMenuItemWithPath("Spreadsheet");
            mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
                    "Delete Variable");

            // Confirm column no longer exists
            Assert.assertNull(ssPanel.column(col.getColumnName()));
        }
        Assert.assertTrue(ssPanel.numOfColumns() == 0);
    }

    /**
     * Test for deletion of columns. Delete all columns at once
     */
    @Test
    public void testDeleteMultipleColumns() {
        System.err.println("testDeleteMultipleColumns");
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.clickMenuItemWithPath("Script", "Run script");

        JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
        jfcf.selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // 2. Select all columns and delete
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);

        SpreadsheetPanelFixture ssPanel =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        Vector<SpreadsheetColumnFixture> cols = ssPanel.allColumns();

        for (SpreadsheetColumnFixture col : cols) {
            mainFrameFixture.pressKey(KeyEvent.VK_CONTROL);
            col.click();
        }
        mainFrameFixture.releaseKey(KeyEvent.VK_CONTROL);
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
                "Delete Variables");
        Assert.assertTrue(ssPanel.numOfColumns() == 0);
    }
}
