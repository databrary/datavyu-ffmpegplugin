/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.uitests;

import java.awt.event.KeyEvent;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.core.matcher.JTextComponentMatcher;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.util.Platform;

import org.openshapa.util.UIUtils;

import org.openshapa.views.discrete.SpreadsheetPanel;

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
    @Test public void testBug496() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String varName = "i";
        String varType = "INTEGER";
        String varRadio = varType.toLowerCase() + "TypeButton";
        String testInput = "2398392310820831";

        spreadsheet = mainFrameFixture.getSpreadsheet();

        // 1. Create new INTEGER variable, open spreadsheet and check that it's
        // there.
        mainFrameFixture.createNewVariable(varName, varRadio);
        Assert.assertNotNull(spreadsheet.column(varName));

        // Create new cell.
        spreadsheet.column(varName).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        // b. Paste text
        UIUtils.setClipboard(testInput);

        JTextComponentFixture cell = mainFrameFixture.textBox(
                JTextComponentMatcher.withText("<val>"));
        cell.click();
        cell.pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_V).modifiers(
                Platform.controlOrCommandMask()));
        Assert.assertEquals(cell.text(), testInput);
    }
}
