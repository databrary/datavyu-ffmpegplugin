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


import java.io.IOException;

import java.awt.Point;
import java.awt.image.BufferedImage;

import java.io.File;

import java.util.ArrayList;

import org.fest.swing.fixture.DataControllerFixture;
import org.fest.swing.fixture.DialogFixture;

import org.openshapa.util.UIImageUtils;
import org.openshapa.util.UIUtils;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Bug733 Description: test that aspect ratio of window remains the same after
 * resize.
 */
public final class UIVideoAspectRatioTest extends OpenSHAPATestClass {

    /**
     * Test Bug 733.
     * @throws IOException on file open error
     */
    @Test public void testBug733() throws IOException {
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

        vidWindows.get(0).moveTo(new Point(dcf.getWidth() + 10,
                100));

        vidWindows.get(0).resizeHeightTo(600
            + vidWindows.get(0).component().getInsets().bottom
            + vidWindows.get(0).component().getInsets().top);
        vidWindows.get(0).component().setAlwaysOnTop(true);

        File refImageFile = new File(testFolder + "/ui/head_turns600h0t.png");
        vidWindows.get(0).component().toFront();

        BufferedImage vidImage = UIImageUtils.captureAsScreenshot(
                vidWindows.get(0).component());
        Assert.assertTrue(UIImageUtils.areImagesEqual(vidImage,
                refImageFile));

        // 3. Get aspect video dimensions
        double beforeResizeWidth = UIImageUtils.getInternalRectangle(
                vidWindows.get(0).component()).getWidth();
        double beforeResizeHeight = UIImageUtils.getInternalRectangle(
                vidWindows.get(0).component()).getHeight();

        // 4. Make window a quarter height
        vidWindows.get(0).resizeHeightTo((int) (beforeResizeHeight / 4)
            + vidWindows.get(0).component().getInsets().bottom
            + vidWindows.get(0).component().getInsets().top);

        // a. Check that ratio remains the same
        refImageFile = new File(testFolder + "/ui/head_turns150h0t.png");
        vidWindows.get(0).component().toFront();
        vidImage = UIImageUtils.captureAsScreenshot(
                vidWindows.get(0).component());
        Assert.assertTrue(UIImageUtils.areImagesEqual(vidImage,
                refImageFile));

        Assert.assertTrue(Math.abs(
                UIImageUtils.getInternalRectangle(
                    vidWindows.get(0).component()).getWidth()
                - (beforeResizeWidth / 4)) < 3,
            ""
            + Math.abs(
                UIImageUtils.getInternalRectangle(
                    vidWindows.get(0).component()).getWidth()
                - (beforeResizeWidth / 4)));

        // 5. Make window a triple height
        beforeResizeWidth = UIImageUtils.getInternalRectangle(vidWindows.get(0)
                .component()).getWidth();
        beforeResizeHeight = UIImageUtils.getInternalRectangle(vidWindows.get(
                    0).component()).getHeight();
        vidWindows.get(0).resizeHeightTo((int) (beforeResizeHeight * 3)
            + vidWindows.get(0).component().getInsets().bottom
            + vidWindows.get(0).component().getInsets().top);

        // a. Check that ratio remains the same
        refImageFile = new File(testFolder + "/ui/head_turns450h0t.png");
        vidWindows.get(0).component().toFront();
        vidImage = UIImageUtils.captureAsScreenshot(
                vidWindows.get(0).component());
        Assert.assertTrue(UIImageUtils.areImagesEqual(vidImage,
                refImageFile));

        Assert.assertTrue(Math.abs(
                UIImageUtils.getInternalRectangle(
                    vidWindows.get(0).component()).getWidth()
                - (beforeResizeWidth * 3)) < 3,
            ""
            + Math.abs(
                UIImageUtils.getInternalRectangle(
                    vidWindows.get(0).component()).getWidth()
                - (beforeResizeWidth * 3)));

        /* BugzID:1452
         * //6. Make window half the width beforeResizeWidth =
         * vidWindow.component().getWidth(); beforeResizeHeight =
         * vidWindow.component().getHeight();
         * vidWindow.resizeWidthTo(beforeResizeWidth / 2); //a. Check that ratio
         * remains the same Assert.assertTrue(Math.abs(
         * vidWindow.component().getHeight() - beforeResizeHeight / 2) < 3);
         *
         * //7. Make window double the width beforeResizeWidth =
         * vidWindow.component().getWidth(); beforeResizeHeight =
         * vidWindow.component().getHeight();
         * vidWindow.resizeWidthTo(beforeResizeWidth * 2); //a. Check that ratio
         * remains the same Assert.assertTrue(Math.abs(
         * vidWindow.component().getHeight() - beforeResizeHeight * 2) < 3);
         */
    }
}
