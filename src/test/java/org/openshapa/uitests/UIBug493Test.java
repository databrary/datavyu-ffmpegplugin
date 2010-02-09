package org.openshapa.uitests;

import org.fest.swing.util.Platform;
import java.awt.event.KeyEvent;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.core.matcher.JTextComponentMatcher;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.openshapa.util.UIUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for Bug 493: Pasting and pressing Enter used to create 2 new lines
 * instead of one.
 */
public class UIBug493Test extends OpenSHAPATestClass {

    /**
     * Bug 493 test.
     */
    @Test
    public void testBug493() {
        System.err.println("testBug493");
        String varName = "textVar";
        String varType = "TEXT";
        String varRadio = varType.toLowerCase() + "TypeButton";

        String[] testInput = {"Subject stands up ", "$10,432",
            "Hand me the manual!", "Tote_that_bale", "Jeune fille celebre",
            "If x?7 then x? 2"};

        String[] expectedTestOutput = testInput;

        //1. Create new TEXT variable
        UIUtils.createNewVariable(mainFrameFixture, varName, varRadio);

        // 2. Check that a column has been created
        JPanelFixture ssPanel = UIUtils.getSpreadsheet(mainFrameFixture);

        // Find our new column header
        JLabelFixture column = ssPanel.panel("headerView").label();

        //3. Create cell, paste text and press enter, for each testInput
        for (int i = 0; i < testInput.length; i++) {
            //a. Create cell
            column.click();
            mainFrameFixture.menuItemWithPath("Spreadsheet", "New Cell").click();

            //b. Paste text
            UIUtils.setClipboard(testInput[i]);
            JTextComponentFixture cell = mainFrameFixture.textBox(JTextComponentMatcher.withText("<val>"));
            cell.click();
            cell.pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_V).modifiers(Platform.controlOrCommandMask()));

            //c. Press Enter
            mainFrameFixture.robot.pressKey(KeyEvent.VK_ENTER);

            //d. Check text
            Assert.assertEquals(cell.text(), expectedTestOutput[i] + "\n");
        }
    }
}
