package org.openshapa.uitests;

import java.io.File;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.uispec4j.Cell;
import org.uispec4j.Column;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;

/**
 * Test the ability to zoom in on the contents of a cell.
 */
public final class UIZoomTest extends UISpecTestCase {

    /**
     * Initialiser called before each unit test.
     *
     * @throws java.lang.Exception When unable to initialise test
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setAdapter(new MainClassAdapter(OpenSHAPA.class, new String[0]));
    }

    static {
        UISpec4J.init();
    }

    /**
     * Test zooming in and reset.
     */
    public void testZoomingIn() {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        int startFontSize = 0;

        // 1. Open and run script to populate database
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsOpenDialog()
                    .assertAcceptsFilesOnly()
                    .select(demoFile.getAbsolutePath()))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        // 1a. Reset zoom and get start font size
        menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
            .getSubMenu("Reset Zoom").click();
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
        startFontSize = ss.getColumns().elementAt(0).getCells().elementAt(0)
                .getValueFont().getSize();

        // 2. Test zooming in from start
        int currFontSize = 0;
        int numberOfZooms = 0;

        int firstCellFontSize = ss.getColumns().elementAt(0).getCells()
                .elementAt(0).getValueFont().getSize();

        while (currFontSize < firstCellFontSize) {
            currFontSize = firstCellFontSize;
            //Zoom in
            menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
                    .getSubMenu("Zoom In").click();
            ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
                    Spreadsheet.class)[0].getAwtComponent()));
            numberOfZooms++;
            firstCellFontSize = ss.getColumns().elementAt(0).getCells()
                    .elementAt(0).getValueFont().getSize();



            //Check that all font sizes have increased
            for (Column col : ss.getColumns()) {
                for (Cell cell : col.getCells()) {
                    assertTrue(cell.getValueFont().getSize()
                            == firstCellFontSize);
                }
            }
            assertTrue((firstCellFontSize > currFontSize)
                    || (numberOfZooms > 1));
        }

        assertTrue(numberOfZooms > 1);

        // 3. Reset zoom and check reset
        menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
                    .getSubMenu("Reset Zoom").click();
        ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
                    Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(startFontSize == ss.getColumns().elementAt(0).getCells()
                    .elementAt(0).getValueFont().getSize());

        currFontSize = Integer.MAX_VALUE;
        numberOfZooms = 0;

        firstCellFontSize = ss.getColumns().elementAt(0).getCells()
                .elementAt(0).getValueFont().getSize();

        // 4. Zoom out fully
         while (currFontSize > firstCellFontSize) {
            currFontSize = firstCellFontSize;
            //Zoom out
            menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
                    .getSubMenu("Zoom Out").click();
            ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
                    Spreadsheet.class)[0].getAwtComponent()));
            numberOfZooms++;
            firstCellFontSize = ss.getColumns().elementAt(0).getCells()
                    .elementAt(0).getValueFont().getSize();
         }
        
        assertTrue(currFontSize < startFontSize);

        //5. Test zoom in from smallest
        currFontSize = 0;
        numberOfZooms = 0;

        firstCellFontSize = ss.getColumns().elementAt(0).getCells()
                .elementAt(0).getValueFont().getSize();

        while (currFontSize < firstCellFontSize) {
            currFontSize = firstCellFontSize;
            //Zoom in
            menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
                    .getSubMenu("Zoom In").click();
            ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
                    Spreadsheet.class)[0].getAwtComponent()));
            numberOfZooms++;
            firstCellFontSize = ss.getColumns().elementAt(0).getCells()
                    .elementAt(0).getValueFont().getSize();

            //Check that all font sizes have increased
            for (Column col : ss.getColumns()) {
                for (Cell cell : col.getCells()) {
                    assertTrue(cell.getValueFont().getSize()
                            == firstCellFontSize);
                }
            }
            assertTrue((firstCellFontSize > currFontSize)
                    || (numberOfZooms > 1));
        }

        assertTrue(numberOfZooms > 1);
    }
        

    /**
     * Test zooming out and reset.
     *//* BugzID:474 - running out of memory.
    public void testZoomingOut() {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        int startFontSize = 0;

        // 1. Open and run script to populate database
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsOpenDialog()
                    .assertAcceptsFilesOnly()
                    .select(demoFile.getAbsolutePath()))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        // 1a. Reset zoom and get start font size
        menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
            .getSubMenu("Reset Zoom").click();
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
        startFontSize = ss.getColumns().elementAt(0).getCells().elementAt(0)
                .getValueFont().getSize();

        // 2. Test zooming out from start
        int currFontSize = Integer.MAX_VALUE;
        int numberOfZooms = 0;

        int firstCellFontSize = ss.getColumns().elementAt(0).getCells()
                .elementAt(0).getValueFont().getSize();

        while (currFontSize > firstCellFontSize) {
            currFontSize = firstCellFontSize;
            //Zoom in
            menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
                    .getSubMenu("Zoom Out").click();
            ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
                    Spreadsheet.class)[0].getAwtComponent()));
            numberOfZooms++;
            firstCellFontSize = ss.getColumns().elementAt(0).getCells()
                    .elementAt(0).getValueFont().getSize();

            //Check that all font sizes have decreased
            for (Column col : ss.getColumns()) {
                for (Cell cell : col.getCells()) {
                    assertTrue(cell.getValueFont().getSize()
                            == firstCellFontSize);
                }
            }
            assertTrue((firstCellFontSize < currFontSize)
                    || (numberOfZooms > 1));
        }

        assertTrue(numberOfZooms > 1);

        // 3. Reset zoom and check reset
        menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
                    .getSubMenu("Reset Zoom").click();
        ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
                    Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(startFontSize == ss.getColumns().elementAt(0).getCells()
                    .elementAt(0).getValueFont().getSize());

        currFontSize = 0;
        numberOfZooms = 0;

        firstCellFontSize = ss.getColumns().elementAt(0).getCells()
                .elementAt(0).getValueFont().getSize();

        // 4. Zoom in fully
         while (currFontSize < firstCellFontSize) {
            currFontSize = firstCellFontSize;
            //Zoom out
            menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
                    .getSubMenu("Zoom In").click();
            ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
                    Spreadsheet.class)[0].getAwtComponent()));
            numberOfZooms++;
            firstCellFontSize = ss.getColumns().elementAt(0).getCells()
                    .elementAt(0).getValueFont().getSize();
         }

        assertTrue(currFontSize > startFontSize);

        //5. Test zoom out from largest
        currFontSize = Integer.MAX_VALUE;
        numberOfZooms = 0;

        firstCellFontSize = ss.getColumns().elementAt(0).getCells()
                .elementAt(0).getValueFont().getSize();

        while (currFontSize > firstCellFontSize) {
            currFontSize = firstCellFontSize;
            //Zoom out
            menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
                    .getSubMenu("Zoom Out").click();
            ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
                    Spreadsheet.class)[0].getAwtComponent()));
            numberOfZooms++;
            firstCellFontSize = ss.getColumns().elementAt(0).getCells()
                    .elementAt(0).getValueFont().getSize();

            //Check that all font sizes have increased
            for (Column col : ss.getColumns()) {
                for (Cell cell : col.getCells()) {
                    assertTrue(cell.getValueFont().getSize()
                            == firstCellFontSize);
                }
            }
            assertTrue((firstCellFontSize < currFontSize)
                    || (numberOfZooms > 1));
        }

        assertTrue(numberOfZooms > 1);
    }*/
}