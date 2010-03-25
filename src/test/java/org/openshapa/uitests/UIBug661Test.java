package org.openshapa.uitests;

import java.io.File;

import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.util.Platform;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Bug 661 Test Make sure that the open dialog remembers previous open location.
 * In the FEST port, this was changed to a generic file dialog location test
 */
public final class UIBug661Test extends OpenSHAPATestClass {
    /**
     * Open a file and maybe check what open dialog opens to.
     * 
     * @param openFile
     *            file to open
     * @param currDirectory
     *            currDirectory or null if not testing
     */
    private void fileLocationTest(final String openFile,
            final String currDirectory) {
        String root = System.getProperty("testPath") + "ui/";
        File openCSV = new File(root + openFile);
        Assert.assertTrue(openCSV.exists());

        // Open file if we're not checking anything, else check we're in the
        // right directory, by saving and checking file
        if (currDirectory == null) {
            mainFrameFixture.clickMenuItemWithPath("File", "Open...");
            try {
                JOptionPaneFixture warning = mainFrameFixture.optionPane();
                warning.requireTitle("Unsaved changes");
                warning.buttonWithText("OK").click();
            } catch (Exception e) {
                // Do nothing
            }
            JFileChooserFixture openDialog = mainFrameFixture.fileChooser();

            openDialog.selectFile(openCSV).approve();
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save As...");
            JFileChooserFixture saveDialog = mainFrameFixture.fileChooser();
            saveDialog.fileNameTextBox().enterText(currDirectory);
            saveDialog.approve();
            // This is the location check
            mainFrameFixture.optionPane().requireTitle("Confirm overwrite")
                    .buttonWithText("Overwrite").click();

            // Open location2
            mainFrameFixture.clickMenuItemWithPath("File", "Open...");
            try {
                JOptionPaneFixture warning = mainFrameFixture.optionPane();
                warning.requireTitle("Unsaved changes");
                warning.buttonWithText("OK").click();
            } catch (Exception e) {
                // Do nothing
            }
            JFileChooserFixture openDialog = mainFrameFixture.fileChooser();
            openDialog.selectFile(openCSV).approve();
        }
    }

    /**
     * Tests open dialog location.
     */
    //@Test
    public void testDialogLocation() {
        if (Platform.isOSX()) {
            return;
        }

        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        // Delete confounding files from previous test
        String root = System.getProperty("testPath");
        File location1 = new File(root + "ui/location1/location2.shapa");
        File location2 = new File(root + "ui/location2/location1.shapa");
        location1.delete();
        location2.delete();
        Assert.assertFalse(location1.exists());
        Assert.assertFalse(location2.exists());

        fileLocationTest("location1/test.shapa", null);
        // At this point it should remember location1
        fileLocationTest("location2/location2.shapa", "location1");
        // At this point it should remember location2
        fileLocationTest("location1/location1.shapa", "location2");
    }
}
