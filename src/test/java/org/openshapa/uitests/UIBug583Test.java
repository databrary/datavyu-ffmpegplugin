package org.openshapa.uitests;

import java.awt.event.KeyEvent;

import java.io.File;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.core.matcher.JTextComponentMatcher;
import org.fest.swing.fixture.JTextComponentFixture;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Bug 583. Highlighting float value and pressing zero changes to "."
 */
public final class UIBug583Test extends OpenSHAPATestClass {

    /**
     * Bug 583 test with a range of values, including 0.
     */
    @Test public void testBug583() throws InterruptedException {

        /**
         * Different cell variable types.
         */
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String[] floatCellValues = { /* BugzID:747-"0.000000", */
                "0.123400", "0.246800", "0.370200", "0.493600", "0.617000",
                "0.740400", "0.863800", "0.987200", "1.110600"
            };

        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get each float cell
        for (String floatVal : floatCellValues) {
            JTextComponentFixture cellValue = mainFrameFixture.textBox(
                    JTextComponentMatcher.withText(floatVal));
            cellValue.selectAll();
            cellValue.pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_0));
            Assert.assertEquals(cellValue.text(), "0.0");
        }
    }
}
