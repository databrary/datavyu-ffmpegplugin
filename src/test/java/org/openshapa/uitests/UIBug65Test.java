package org.openshapa.uitests;

import org.fest.swing.util.Platform;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.core.matcher.JTextComponentMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.openshapa.util.UIUtils;
import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Bug 65 Test
 * Columns should always stay in the order they are
 * inserted regardless of show spreadsheet is invoked.
 */
public class UIBug65Test extends OpenSHAPATestClass {

    /**
     * Test that the order of columns remains the same.
     */
    @Test
    public void testColumnOrder() {
        System.err.println("testBug493");
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());
        
        //1. Run script to populate
        mainFrameFixture.menuItemWithPath("Script", "Run script").click();

        JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
        jfcf.selectFile(demoFile).approve();

        //Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();
        
        //2. Save column vector
        JPanelFixture ssPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanel sp = (SpreadsheetPanel)ssPanel.component();
        Vector<SpreadsheetColumn> vecSCBefore = sp.getColumns();
        
        //3. Press "Show spreadsheet"
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Show Spreadsheet").click();
        
        //4. Get columns again and confirm unchanged
        JPanelFixture ssPanel2 = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanel sp2 = (SpreadsheetPanel)ssPanel.component();
        Vector<SpreadsheetColumn> vecSCAfter = sp.getColumns();
        
        Assert.assertEquals(vecSCBefore, vecSCAfter);
    }
}
