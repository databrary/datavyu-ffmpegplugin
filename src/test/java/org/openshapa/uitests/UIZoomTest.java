package org.openshapa.uitests;

import java.io.File;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.openshapa.util.UIUtils;
import org.openshapa.views.OpenSHAPAView;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the ability to zoom in on the contents of a cell.
 */
public class UIZoomTest extends OpenSHAPATestClass {

    /**
     * Test zooming in and reset.
     */
    @Test
    public void testZoomingIn() {
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.menuItemWithPath("Script", "Run script").click();

        JFileChooserFixture scriptFileChooser = mainFrameFixture.fileChooser();
        scriptFileChooser.selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // 2. Get the spreadsheet, check that cells do exist
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        Assert.assertTrue(spreadsheet.allColumns().size() > 0,
                "Expecting columns to exist.");
        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertTrue(column.numOfCells() > 0,
                    "Expecting cells to exist.");
        }

        // 3. Reset the zoom size, and get the initial zoom size
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Zoom", "Reset Zoom")
                .click();

        final int initialSize =
                spreadsheet.column(1).cell(1).cellValue().font().target()
                        .getSize();

        Assert.assertTrue(initialSize == OpenSHAPAView.ZOOM_DEFAULT_SIZE,
                "Initial zoom size incorrect.");

        int previousSize = initialSize;

        // 4. Zoom in
        while (previousSize < OpenSHAPAView.ZOOM_MAX_SIZE) {
            mainFrameFixture.menuItemWithPath("Spreadsheet", "Zoom", "Zoom In")
                    .click();
            /*
             * Test the font sizes of the first cell only because as the zoom
             * size increases, cells may be pushed out of view. When this
             * happens, Swing does not update the hidden cells, causing tests to
             * fail.
             */
            spreadsheet.column(1).cell(1).cellValue().font().requireSize(
                    previousSize + OpenSHAPAView.ZOOM_INTERVAL);

            previousSize =
                    spreadsheet.column(1).cell(1).cellValue().font().target()
                            .getSize();
        }

        Assert.assertTrue(previousSize == OpenSHAPAView.ZOOM_MAX_SIZE,
                "Maximum zoom size not achieved.");

        // 5. Reset zoom, check reset.
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Zoom", "Reset Zoom")
                .click();

        final int resetSize =
                spreadsheet.column(1).cell(1).cellValue().font().target()
                        .getSize();

        Assert.assertTrue(resetSize == OpenSHAPAView.ZOOM_DEFAULT_SIZE,
                "Reset zoom size incorrect.");

        // 6. Zoom out all the way
        previousSize = resetSize;

        while (previousSize > OpenSHAPAView.ZOOM_MIN_SIZE) {
            mainFrameFixture
                    .menuItemWithPath("Spreadsheet", "Zoom", "Zoom Out")
                    .click();

            previousSize =
                    spreadsheet.column(1).cell(1).cellValue().font().target()
                            .getSize();
        }

        Assert.assertTrue(previousSize == OpenSHAPAView.ZOOM_MIN_SIZE,
                "Minimum zoom size not achieved.");

        // 7. Zoom in
        while (previousSize < 40) {
            mainFrameFixture.menuItemWithPath("Spreadsheet", "Zoom", "Zoom In")
                    .click();
            /*
             * Test the font sizes of the first cell only because as the zoom
             * size increases, cells may be pushed out of view. When this
             * happens, Swing does not update the hidden cells, causing tests to
             * fail.
             */
            spreadsheet.column(1).cell(1).cellValue().font().requireSize(
                    previousSize + OpenSHAPAView.ZOOM_INTERVAL);

            previousSize =
                    spreadsheet.column(1).cell(1).cellValue().font().target()
                            .getSize();
        }
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Zoom", "Zoom In")
                .click();

        previousSize =
                spreadsheet.column(1).cell(1).cellValue().font().target()
                        .getSize();

        Assert.assertTrue(previousSize == OpenSHAPAView.ZOOM_MAX_SIZE,
                "Maximum zoom size not achieved.");

    }

    /**
     * Test zooming out and reset.
     */
    @Test
    public void testZoomingOut() {
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.menuItemWithPath("Script", "Run script").click();

        JFileChooserFixture scriptFileChooser = mainFrameFixture.fileChooser();
        scriptFileChooser.selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // 2. Get the spreadsheet, check that cells do exist
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        Assert.assertTrue(spreadsheet.allColumns().size() > 0,
                "Expecting columns to exist.");
        for (SpreadsheetColumnFixture column : spreadsheet.allColumns()) {
            Assert.assertTrue(column.numOfCells() > 0,
                    "Expecting cells to exist.");
        }

        // 3. Reset the zoom size, and get the initial zoom size
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Zoom", "Reset Zoom")
                .click();

        final int initialSize =
                spreadsheet.column(1).cell(1).cellValue().font().target()
                        .getSize();

        Assert.assertTrue(initialSize == OpenSHAPAView.ZOOM_DEFAULT_SIZE,
                "Initial zoom size incorrect.");

        int previousSize = initialSize;

        // 4. Zoom out
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Zoom", "Zoom Out")
                .click();
        previousSize =
                spreadsheet.column(1).cell(1).cellValue().font().target()
                        .getSize();
        Assert.assertTrue(previousSize == OpenSHAPAView.ZOOM_DEFAULT_SIZE
                - OpenSHAPAView.ZOOM_INTERVAL);

        mainFrameFixture.menuItemWithPath("Spreadsheet", "Zoom", "Zoom Out")
                .click();
        previousSize =
                spreadsheet.column(1).cell(1).cellValue().font().target()
                        .getSize();
        Assert.assertTrue(previousSize == OpenSHAPAView.ZOOM_MIN_SIZE);

        // 5. Zoom in all the way
        while (previousSize < OpenSHAPAView.ZOOM_MAX_SIZE) {
            mainFrameFixture.menuItemWithPath("Spreadsheet", "Zoom", "Zoom In")
                    .click();

            previousSize =
                    spreadsheet.column(1).cell(1).cellValue().font().target()
                            .getSize();
        }
        Assert.assertTrue(previousSize == OpenSHAPAView.ZOOM_MAX_SIZE,
                "Maximum zoom size not achieved.");

        // 6. Zoom out from max
        while (previousSize > 10) {
            mainFrameFixture
                    .menuItemWithPath("Spreadsheet", "Zoom", "Zoom Out")
                    .click();

            spreadsheet.column(1).cell(1).cellValue().font().requireSize(
                    previousSize - OpenSHAPAView.ZOOM_INTERVAL);

            previousSize =
                    spreadsheet.column(1).cell(1).cellValue().font().target()
                            .getSize();
        }

        mainFrameFixture.menuItemWithPath("Spreadsheet", "Zoom", "Zoom Out")
                .click();
        previousSize =
                spreadsheet.column(1).cell(1).cellValue().font().target()
                        .getSize();
        Assert.assertTrue(previousSize == OpenSHAPAView.ZOOM_MIN_SIZE);

    }
}
