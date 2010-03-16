package org.openshapa.uitests;

import java.awt.event.KeyEvent;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.core.matcher.JTextComponentMatcher;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.util.Platform;
import org.openshapa.util.UIUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Bug 496: Pasting "2398392310820831" into a new cell, shows 23983 as
 * highlighted. Expected: Nothing highlighted or entire value highlighted.
 */
public final class UIBug496Test extends OpenSHAPATestClass {

    /**
     * Bug 496 test.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testBug496() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        String varName = "i";
        String varType = "INTEGER";
        String varRadio = varType.toLowerCase() + "TypeButton";
        String testInput = "2398392310820831";

        // Retrieve the components
        JPanelFixture ssPanel = UIUtils.getSpreadsheet(mainFrameFixture);

        // 1. Create new INTEGER variable, open spreadsheet and check that it's
        // there.
        UIUtils.createNewVariable(mainFrameFixture, varName, varRadio);
        ssPanel.panel("headerView").label().text().startsWith(varName);

        // Create new variable.
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        // b. Paste text
        UIUtils.setClipboard(testInput);
        JTextComponentFixture cell =
                mainFrameFixture.textBox(JTextComponentMatcher
                        .withText("<val>"));
        cell.click();
        cell.pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_V).modifiers(
                Platform.controlOrCommandMask()));
        Assert.assertEquals(cell.text(), testInput);
    }
}
