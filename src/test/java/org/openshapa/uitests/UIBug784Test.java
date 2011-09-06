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

import org.fest.swing.fixture.DataControllerFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;

import org.openshapa.util.UIUtils;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test for Bug 784. When using the alternative find (shift + find) it doesn't
 * work when hitting: shift + clicking with the mouse.
 * Should behave the same as shift and hitting the '+' key on the numpad.
 */
public final class UIBug784Test extends OpenSHAPATestClass {

    /**
     * Test for Bug784.
     */
    @Test public void testBug784() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String varName = "v";
        String varType =
            UIUtils.VAR_TYPES[(int) (Math.random() * UIUtils.VAR_TYPES.length)];
        String varRadio = varType.toLowerCase() + "TypeButton";
        mainFrameFixture.createNewVariable(varName, varRadio);

        // 2. Check that a column has been created
        spreadsheet = mainFrameFixture.getSpreadsheet();
        Assert.assertNotNull(spreadsheet.column(varName));

        // 3. Create cell
        spreadsheet.column(varName).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        // 4. Set onset and offset
        SpreadsheetCellFixture cell = spreadsheet.column(varName).cell(1);
        cell.onsetTimestamp().enterText("111111111");
        cell.offsetTimestamp().enterText("222222222");

        // 5. Open Dataviewer Controller
        DataControllerFixture dcf = mainFrameFixture.openDataController(300,
                300);

        // *. Highlight select cell because of BugzID:1430
        cell.fillSelectCell(true);

        // 6. Test Find by clicking button
        Assert.assertEquals("00:00:00:000", dcf.getCurrentTime());
        dcf.pressFindButton();
        Assert.assertEquals("11:11:11:111", dcf.getCurrentTime());

        // 7. Test Shift-Find by clicking button
        mainFrameFixture.pressKey(KeyEvent.VK_SHIFT);
        dcf.pressFindButton();
        mainFrameFixture.releaseKey(KeyEvent.VK_SHIFT);
        Assert.assertEquals("22:22:22:222", dcf.getCurrentTime());

        // 8. Test Find again by clicking button
        dcf.pressFindButton();
        Assert.assertEquals("11:11:11:111", dcf.getCurrentTime());
    }
}
