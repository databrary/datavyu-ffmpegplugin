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

import java.io.File;

import java.util.Vector;

import org.fest.swing.fixture.SpreadsheetColumnFixture;



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
