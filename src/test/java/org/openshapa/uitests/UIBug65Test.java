package org.openshapa.uitests;

import java.io.File;

import java.util.Vector;

import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;

import org.openshapa.util.UIUtils;

import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.SpreadsheetPanel;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Bug 65 Test Columns should always stay in the order they are inserted
 * regardless of show spreadsheet is invoked.
 */
public final class UIBug65Test extends OpenSHAPATestClass {

    /**
     * Test that the order of columns remains the same.
     */
    @Test public void testColumnOrder() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Save column vector
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Vector<SpreadsheetColumnFixture> vecSCBefore = spreadsheet.allColumns();

        // 3. Press "Show spreadsheet"
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
            "Show Spreadsheet");

        // 4. Get columns again and confirm unchanged
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Vector<SpreadsheetColumnFixture> vecSCAfter = spreadsheet.allColumns();

        Assert.assertEquals(vecSCBefore.size(), vecSCAfter.size());

        for (int i = 0; i < vecSCBefore.size(); i++) {
            Assert.assertEquals(vecSCBefore.elementAt(i).getColumnName(),
                vecSCAfter.elementAt(i).getColumnName());
            Assert.assertEquals(vecSCBefore.elementAt(i).getColumnType(),
                vecSCAfter.elementAt(i).getColumnType());
        }
    }
}
