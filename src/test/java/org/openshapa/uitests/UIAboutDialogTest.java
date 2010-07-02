package org.openshapa.uitests;

import java.awt.image.BufferedImage;

import java.io.File;

import org.fest.swing.fixture.DialogFixture;
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


            String root = System.getProperty("testPath");

            mainFrameFixture.clickMenuItemWithPath("Help", "About");

            DialogFixture about = mainFrameFixture.dialog();

            // Check that image is roughly correct
            BufferedImage aboutBI = UIImageUtils.captureAsScreenshot(
                    about.component());

            Assert.assertTrue(UIImageUtils.areImagesEqual(aboutBI,
                    new File(root + "/ui/aboutDialog.png")));

            // Close and ensure it closes
            about.close();
            about.requireNotVisible();
        }

        Assert.assertTrue(true);
    }
}
