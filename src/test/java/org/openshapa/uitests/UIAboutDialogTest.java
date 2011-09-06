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

import java.awt.image.BufferedImage;

import java.io.File;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;

import org.openshapa.util.UIImageUtils;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test the About Dialog.
 */
public final class UIAboutDialogTest extends OpenSHAPATestClass {

    /**
     * Test that the About Dialog opens and displays correct image and can be
     * closed.
     */
    @Test public void testAboutDialog() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // Not testing on OSX because the About dialog is not in the help menu
        // and not sure how to click the other menu item.
        if (!Platform.isOSX()) {
            mainFrameFixture.clickMenuItemWithPath("Help", "About");

            DialogFixture about = mainFrameFixture.dialog(Timeout.timeout(5000));

            // Check that image is roughly correct
            BufferedImage aboutBI = UIImageUtils.captureAsScreenshot(
                    about.component());

            Assert.assertTrue(UIImageUtils.areImagesEqual(aboutBI,
                    new File(testFolder + "/ui/aboutDialog.png")));

            // Close and ensure it closes
            about.close();
            about.requireNotVisible();
        }

        Assert.assertTrue(true);
    }
}
