package org.openshapa.uitests;


import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.util.Platform;

import org.openshapa.util.UIImageUtils;
import org.openshapa.util.UIUtils;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test the visual aspects of cells.
 */
public final class UIVisualCellTest extends OpenSHAPATestClass {

    /**
     * Checks if multiline text wrapping is working.
     * @throws IOException if can't load image file
     */
    @Test public void testMultilineTextWrapping() throws IOException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String varName = "t";
        String varType = "text";

        // Create new text cell
        spreadsheet = mainFrameFixture.getSpreadsheet();

        mainFrameFixture.createNewVariable(varName, varType);
        spreadsheet.column(varName).click();

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        SpreadsheetCellFixture cell = spreadsheet.column(0).cell(1);

        // Reset text zoom
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Zoom",
            "Reset Zoom");

        // Add long text that should wrap on multiple lines
        UIUtils.setClipboard(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus at dolor at velit placerat convallis. Cras sit amet dolor vitae tellus egestas ullamcorper ut non risus. Sed ac justo elit, sed cursus ligula. Nullam nec urna ac tortor accumsan placerat. Nam vestibulum, mauris vitae gravida suscipit, magna mauris interdum ligula, vel pulvinar eros arcu sed quam. Vestibulum eget tristique velit. Nulla facilisi. Integer lectus erat, rhoncus sed sagittis vestibulum, vehicula quis turpis. Fusce malesuada tristique massa, in iaculis magna accumsan vel.");

        cell.cellValue().pressAndReleaseKey(KeyPressInfo.keyCode(
                KeyEvent.VK_V).modifiers(
                Platform.controlOrCommandMask()));

        
        File refImageFile = new File(testFolder + "/ui/multiline.png");

        BufferedImage componentImage = UIImageUtils.captureAsScreenshot(
                cell.component());

        Assert.assertTrue(UIImageUtils.areImagesEqual(componentImage,
                refImageFile));
    }

}
