package org.openshapa.uitests;

import java.io.File;
import java.util.Vector;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;
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
    /*@Test*/
    public void testColumnOrder() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
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

        // 2. Save column vector
        JPanelFixture ssPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanel sp = (SpreadsheetPanel) ssPanel.component();
        Vector<SpreadsheetColumn> vecSCBefore = sp.getColumns();

        // 3. Press "Show spreadsheet"
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
                "Show Spreadsheet");

        // 4. Get columns again and confirm unchanged
        JPanelFixture ssPanel2 = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanel sp2 = (SpreadsheetPanel) ssPanel.component();
        Vector<SpreadsheetColumn> vecSCAfter = sp.getColumns();

        Assert.assertEquals(vecSCBefore, vecSCAfter);
    }
}
