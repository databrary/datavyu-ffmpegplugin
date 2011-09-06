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

import java.awt.Point;

import java.io.File;

import java.util.ArrayList;

import org.fest.swing.fixture.DataControllerFixture;
import org.fest.swing.fixture.DialogFixture;

import org.openshapa.util.UIUtils;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Bug686 Description: Jog doesn't move a single frame at a time.
 */
public final class UIBug686Test extends OpenSHAPATestClass {

    /**
     * Test Bug 686.
     */
    @Test public void testBug686() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Open video
        // a. Get Spreadsheet
        spreadsheet = mainFrameFixture.getSpreadsheet();

        // b. Open Data Viewer Controller
        final DataControllerFixture dcf = mainFrameFixture.openDataController();

        // c. Open video
        final File videoFile = new File(testFolder + "/ui/head_turns.mov");
        Assert.assertTrue(videoFile.exists());

        UIUtils.openData(videoFile, dcf);

        // 2. Get window
        ArrayList<DialogFixture> vidWindows = dcf.getVideoWindows();
        Assert.assertEquals(vidWindows.size(), 1);

        vidWindows.get(0).moveTo(new Point(dcf.getWidth() + 10,
                100));

        // 2. Jog forward and check
        dcf.pressJogForwardButton();
        Assert.assertEquals("00:00:00:040", dcf.getCurrentTime());
        dcf.pressJogForwardButton();
        Assert.assertEquals("00:00:00:080", dcf.getCurrentTime());

        // 3. Jog back and check
        dcf.pressJogBackButton();
        Assert.assertEquals("00:00:00:040", dcf.getCurrentTime());
        dcf.pressJogBackButton();
        Assert.assertEquals("00:00:00:000", dcf.getCurrentTime());
    }
}
