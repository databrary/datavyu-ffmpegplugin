package org.openshapa.uitests;

import java.io.File;

import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.VocabEditorDialogFixture;
import org.fest.swing.fixture.VocabElementFixture;

import org.openshapa.views.OpenSHAPAView;
import org.openshapa.views.VocabEditorV;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test the ability to zoom in on the contents of a cell.
 */
public final class UIZoomTest extends OpenSHAPATestClass {

    /** Min font size. */
    public static final int MIN_FONT_SIZE = 10;

    /** Max font size. */
    public static final int MAX_FONT_SIZE = 40;

    /**
     * Test zooming in and reset.
     */
    @Test public void testZoomingIn() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        
        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get the spreadsheet, check that cells do exist
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Assert.assertTrue(spreadsheet.allColumns().size() > 0,
            "Expecting columns to exist.");

        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertTrue(column.numOfCells() > 0,
                "Expecting cells to exist.");
        }

        // 3. Reset the zoom size, and get the initial zoom size
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Zoom",
            "Reset Zoom");

        final int initialSize = spreadsheet.column(1).cell(1).cellValue().font()
            .target().getSize();

        Assert.assertTrue(initialSize == OpenSHAPAView.ZOOM_DEFAULT_SIZE,
            "Initial zoom size incorrect.");

        int previousSize = initialSize;

        // 4. Zoom in
        while (previousSize < OpenSHAPAView.ZOOM_MAX_SIZE) {
            mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Zoom",
                "Zoom In");

            /*
             * Test the font sizes of the first cell only because as the zoom
             * size increases, cells may be pushed out of view. When this
             * happens, Swing does not update the hidden cells, causing tests to
             * fail.
             */
            spreadsheet.column(1).cell(1).cellValue().font().requireSize(
                previousSize + OpenSHAPAView.ZOOM_INTERVAL);

            previousSize = spreadsheet.column(1).cell(1).cellValue().font()
                .target().getSize();
        }

        Assert.assertTrue(previousSize == OpenSHAPAView.ZOOM_MAX_SIZE,
            "Maximum zoom size not achieved.");

        // 5. Reset zoom, check reset.
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Zoom",
            "Reset Zoom");

        final int resetSize = spreadsheet.column(1).cell(1).cellValue().font()
            .target().getSize();

        Assert.assertTrue(resetSize == OpenSHAPAView.ZOOM_DEFAULT_SIZE,
            "Reset zoom size incorrect.");

        // 6. Zoom out all the way
        previousSize = resetSize;

        while (previousSize > OpenSHAPAView.ZOOM_MIN_SIZE) {
            mainFrameFixture.menuItemWithPath("Spreadsheet", "Zoom", "Zoom Out")
                .click();

            previousSize = spreadsheet.column(1).cell(1).cellValue().font()
                .target().getSize();
        }

        Assert.assertTrue(previousSize == OpenSHAPAView.ZOOM_MIN_SIZE,
            "Minimum zoom size not achieved.");

        // 7. Zoom in
        while (previousSize < MAX_FONT_SIZE) {
            mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Zoom",
                "Zoom In");

            /*
             * Test the font sizes of the first cell only because as the zoom
             * size increases, cells may be pushed out of view. When this
             * happens, Swing does not update the hidden cells, causing tests to
             * fail.
             */
            spreadsheet.column(1).cell(1).cellValue().font().requireSize(
                previousSize + OpenSHAPAView.ZOOM_INTERVAL);

            previousSize = spreadsheet.column(1).cell(1).cellValue().font()
                .target().getSize();
        }

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Zoom",
            "Zoom In");

        previousSize = spreadsheet.column(1).cell(1).cellValue().font().target()
            .getSize();

        Assert.assertTrue(previousSize == OpenSHAPAView.ZOOM_MAX_SIZE,
            "Maximum zoom size not achieved.");

    }

    /**
     * Test zooming out and reset.
     */
    @Test public void testZoomingOut() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        
        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get the spreadsheet, check that cells do exist
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Assert.assertTrue(spreadsheet.allColumns().size() > 0,
            "Expecting columns to exist.");

        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertTrue(column.numOfCells() > 0,
                "Expecting cells to exist.");
        }

        // 3. Reset the zoom size, and get the initial zoom size
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Zoom",
            "Reset Zoom");

        final int initialSize = spreadsheet.column(1).cell(1).cellValue().font()
            .target().getSize();

        Assert.assertTrue(initialSize == OpenSHAPAView.ZOOM_DEFAULT_SIZE,
            "Initial zoom size incorrect.");

        int previousSize = initialSize;

        // 4. Zoom out
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Zoom",
            "Zoom Out");
        previousSize = spreadsheet.column(1).cell(1).cellValue().font().target()
            .getSize();
        Assert.assertTrue(previousSize
            == (OpenSHAPAView.ZOOM_DEFAULT_SIZE - OpenSHAPAView.ZOOM_INTERVAL));

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Zoom",
            "Zoom Out");

        previousSize = spreadsheet.column(1).cell(1).cellValue().font().target()
            .getSize();
        Assert.assertTrue(previousSize == OpenSHAPAView.ZOOM_MIN_SIZE);

        // 5. Zoom in all the way
        while (previousSize < OpenSHAPAView.ZOOM_MAX_SIZE) {
            mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Zoom",
                "Zoom In");

            previousSize = spreadsheet.column(1).cell(1).cellValue().font()
                .target().getSize();
        }

        Assert.assertTrue(previousSize == OpenSHAPAView.ZOOM_MAX_SIZE,
            "Maximum zoom size not achieved.");

        // 6. Zoom out from max
        while (previousSize > MIN_FONT_SIZE) {
            mainFrameFixture.menuItemWithPath("Spreadsheet", "Zoom", "Zoom Out")
                .click();

            spreadsheet.column(1).cell(1).cellValue().font().requireSize(
                previousSize - OpenSHAPAView.ZOOM_INTERVAL);

            previousSize = spreadsheet.column(1).cell(1).cellValue().font()
                .target().getSize();
        }

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Zoom",
            "Zoom Out");
        previousSize = spreadsheet.column(1).cell(1).cellValue().font().target()
            .getSize();
        Assert.assertTrue(previousSize == OpenSHAPAView.ZOOM_MIN_SIZE);

    }

    /**
     * Test to ensure vocab editor contents is not being zoomed.
     */
    @Test public void testBug635() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        
        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get the spreadsheet, check that cells do exist
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Assert.assertTrue(spreadsheet.allColumns().size() > 0,
            "Expecting columns to exist.");

        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertTrue(column.numOfCells() > 0,
                "Expecting cells to exist.");
        }

        // 3. Reset the zoom size, and get the initial zoom size
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Zoom",
            "Reset Zoom");

        final int initialSize = spreadsheet.column(1).cell(1).cellValue().font()
            .target().getSize();

        Assert.assertTrue(initialSize == OpenSHAPAView.ZOOM_DEFAULT_SIZE,
            "Initial zoom size incorrect.");

        int previousSize = initialSize;

        // 3a. Get initial zoom size of vocab editor window
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        int veFontSize = veDialog.allVocabElements().firstElement().value()
            .font().target().getSize();

        // Confirm that all VEs are the same size.
        for (VocabElementFixture v : veDialog.allVocabElements()) {
            Assert.assertEquals(veFontSize,
                v.value().font().target().getSize());
        }

        // 4. Zoom in
        previousSize = spreadsheet.column(1).cell(1).cellValue().font().target()
            .getSize();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Zoom",
            "Zoom In");

        /*
         * Test the font sizes of the first cell only because as the zoom size
         * increases, cells may be pushed out of view. When this happens, Swing
         * does not update the hidden cells, causing tests to fail.
         */
        spreadsheet.column(1).cell(1).cellValue().font().requireSize(
            previousSize + OpenSHAPAView.ZOOM_INTERVAL);

        for (VocabElementFixture v : veDialog.allVocabElements()) {
            Assert.assertEquals(veFontSize,
                v.value().font().target().getSize());
        }

        // 5. Zoom out
        previousSize = spreadsheet.column(1).cell(1).cellValue().font().target()
            .getSize();

        mainFrameFixture.menuItemWithPath("Spreadsheet", "Zoom", "Zoom Out")
            .click();

        spreadsheet.column(1).cell(1).cellValue().font().requireSize(
            previousSize - OpenSHAPAView.ZOOM_INTERVAL);

        for (VocabElementFixture v : veDialog.allVocabElements()) {
            Assert.assertEquals(veFontSize,
                v.value().font().target().getSize());
        }
    }
}
