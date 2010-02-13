package org.openshapa.uitests;

import java.io.File;
import junitx.util.PrivateAccessor;
import org.openshapa.Configuration;
import org.openshapa.util.ConfigProperties;
import org.openshapa.views.OpenSHAPAView;
import org.uispec4j.MenuBar;
import org.uispec4j.OpenSHAPAUISpecTestCase;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

/**
 * Test the ability to zoom in on the contents of a cell.
 */
public final class UIZoomTest extends OpenSHAPAUISpecTestCase {

    static {
        try {
            ConfigProperties p = (ConfigProperties) PrivateAccessor.getField(Configuration.getInstance(), "properties");
            p.setCanSendLogs(false);
        } catch (Exception e) {
            System.err.println("Unable to overide sending usage logs");
        }
        UISpec4J.init();
    }


//    public void testTrue() {
//        assertTrue(true);
//    }

    /**
     * Test zooming in and reset.
     */
//   public void testZoomingIn() {
//        //Preparation
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//        int startFontSize = 0;
//
//        // 1. Open and run script to populate database
//        String root = System.getProperty("testPath");
//        File demoFile = new File(root + "/ui/demo_data.rb");
//        assertTrue(demoFile.exists());
//
//        WindowInterceptor
//                .init(menuBar.getMenu("Script").getSubMenu("Run script")
//                    .triggerClick())
//                .process(FileChooserHandler.init()
//                    .assertIsOpenDialog()
//                    .assertAcceptsFilesOnly()
//                    .select(demoFile.getAbsolutePath()))
//                .process(new WindowHandler() {
//                    public Trigger process(Window console) {
//                        return console.getButton("Close").triggerClick();
//                    }
//                })
//                .run();
//
//        // 1a. Reset zoom and get start font size
//        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
//                window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//        menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
//            .getSubMenu("Reset Zoom").click();
//        startFontSize = ss.getColumns().elementAt(0).getCells().elementAt(0)
//                .getValueFont().getSize();
//
//        SpreadsheetPanel sp = (SpreadsheetPanel)window.getUIComponents(Spreadsheet.class)[0].getAwtComponent();
//        SpreadsheetCell sc = (SpreadsheetCell)sp.getColumns().elementAt(0).getCells().elementAt(0);
//        JTextArea textArea = sc.getDataView();
//        startFontSize = textArea.getFont().getSize();
//
//        // 2. Test zooming in from start
//        int currFontSize = 0;
//        int numberOfZooms = 0;
//
//        int firstCellFontSize = ss.getColumns().elementAt(0).getCells()
//                .elementAt(0).getValueFont().getSize();
//
//        while (currFontSize < firstCellFontSize) {
//            Font f = Configuration.getInstance().getSSDataFont();
//            currFontSize = firstCellFontSize;
//            //Zoom in
//            menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
//                    .getSubMenu("Zoom In").click();
//
//
//            f = Configuration.getInstance().getSSDataFont();
//
////            menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
////                    .getSubMenu("Zoom In").click();
////            menuBar.getMenu("Spreadsheet").getSubMenu("Show Spreadsheet").click();
////            ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
////                    Spreadsheet.class)[0].getAwtComponent()));
//            numberOfZooms++;
//            firstCellFontSize = ss.getColumns().elementAt(0).getCells()
//                    .elementAt(0).getValueFont().getSize();
//
//
//            //Check that all font sizes have increased
//            for (Column col : ss.getColumns()) {
//                for (Cell cell : col.getCells()) {
//                    assertTrue(cell.getValueFont().getSize()
//                            == firstCellFontSize);
//                }
//            }
////            assertTrue((firstCellFontSize > currFontSize) || (numberOfZooms > 1));
//        }
//
////        assertTrue(numberOfZooms > 1);
//
//        // 3. Reset zoom and check reset
//        menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
//                    .getSubMenu("Reset Zoom").click();
////        ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
////                    Spreadsheet.class)[0].getAwtComponent()));
//        assertTrue(startFontSize == ss.getColumns().elementAt(0).getCells()
//                    .elementAt(0).getValueFont().getSize());
//
//        currFontSize = Integer.MAX_VALUE;
//        numberOfZooms = 0;
//
//        firstCellFontSize = ss.getColumns().elementAt(0).getCells()
//                .elementAt(0).getValueFont().getSize();
//
//        // 4. Zoom out fully
//         while (currFontSize > firstCellFontSize) {
//            currFontSize = firstCellFontSize;
//            //Zoom out
//            menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
//                    .getSubMenu("Zoom Out").click();
////            ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
////                    Spreadsheet.class)[0].getAwtComponent()));
//            numberOfZooms++;
//            firstCellFontSize = ss.getColumns().elementAt(0).getCells()
//                    .elementAt(0).getValueFont().getSize();
//         }
//
//        assertTrue(currFontSize < OpenSHAPAView.ZOOM_DEFAULT_SIZE);
////        assertTrue(currFontSize < startFontSize);
//
//        //5. Test zoom in from smallest
//        currFontSize = 0;
//        numberOfZooms = 0;
//
//        firstCellFontSize = ss.getColumns().elementAt(0).getCells()
//                .elementAt(0).getValueFont().getSize();
//
//        while (currFontSize < firstCellFontSize) {
//            currFontSize = firstCellFontSize;
//            //Zoom in
//            menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
//                    .getSubMenu("Zoom In").click();
////            ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
////                    Spreadsheet.class)[0].getAwtComponent()));
//            numberOfZooms++;
//            firstCellFontSize = ss.getColumns().elementAt(0).getCells()
//                    .elementAt(0).getValueFont().getSize();
//
//            //Check that all font sizes have increased
//            for (Column col : ss.getColumns()) {
//                for (Cell cell : col.getCells()) {
//                    assertTrue(cell.getValueFont().getSize()
//                            == firstCellFontSize);
//                }
//            }
////            assertTrue((firstCellFontSize > currFontSize)
////                    || (numberOfZooms > 1));
//            assertTrue(firstCellFontSize > currFontSize);
////                    || (numberOfZooms > 1));
//        }
//
//        assertTrue(numberOfZooms > 1);
//    }

    /**
     * The aim of this test is to:
     *  1. Test the zoom reset functionality
     *  2. Test if zoom in is zooming in by the correct amount
     *  3. Test if zooming in is bounded by the minimum and maximum zoom size
     * 
     * The correctness of the zoom behaviour is tested against the constants
     * defined in OpenSHAPAView, namely ZOOM_DEFAULT_SIZE for the reset
     * functionality, and ZOOM_INTERVAL for zooming in using the correct
     * increment.
     */
    public void testZoomingIn2() {
         //Preparation
        final Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();


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
        menuBar.getMenu("Spreadsheet")
               .getSubMenu("Zoom")
               .getSubMenu("Reset Zoom").click();

        /* 1b. Get start font size, this is the size being currently displayed
         * by the spreadsheet.
         */
//        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
//                (window.getUIComponents(Spreadsheet.class)[0]
//                        .getAwtComponent()));
//
//        SpreadsheetPanel sp = (SpreadsheetPanel)window.getUIComponents(Spreadsheet.class)[0].getAwtComponent();
//        SpreadsheetCell sc = (SpreadsheetCell)sp.getColumns().elementAt(0).getCells().elementAt(0);
//        JTextArea textArea = sc.getDataView();

//        FontSizeSeeker fss = new FontSizeSeeker();
//        fss.setWindow(window);
//
//        SwingUtilities.invokeAndWait(fss);
//
//        final int startSSFontSize = fss.getFontSize();


//        startSSFontSize = ss.getColumns().elementAt(0).getCells().elementAt(0)
//                .getValueFont().getSize();

        // 1c. The stored configuration value should have reset to default
        int startConfigFontSize = Configuration.getInstance().getSSDataFont().getSize();
//        System.out.println("Reset zoom size: Config file - " + startConfigFontSize + ", Spreadsheet - " + startSSFontSize);
        assertEquals(OpenSHAPAView.ZOOM_DEFAULT_SIZE, startConfigFontSize);

        // 1d. The starting font size should be the default font size
//        assertEquals(OpenSHAPAView.ZOOM_DEFAULT_SIZE, startSSFontSize);

        // 2. Zoom in once
        menuBar.getMenu("Spreadsheet")
               .getSubMenu("Zoom")
               .getSubMenu("Zoom In").click();

        // 2a. Verify that we have zoomed in.
        int newConfigFontSize = Configuration.getInstance()
                .getSSDataFont().getSize();
        assertTrue(newConfigFontSize > startConfigFontSize);

//        int newSSFontSize = ss.getColumns().elementAt(0).getCells().elementAt(0)
//                .getValueFont().getSize();
//        int newSSFontSize = sc.getDataView().getFont().getSize();
//        sp = (SpreadsheetPanel)window.getUIComponents(Spreadsheet.class)[0].getAwtComponent();
//        sc = (SpreadsheetCell)sp.getColumns().elementAt(0).getCells().elementAt(0);
//        textArea = sc.getDataView();

//        assertTrue(newSSFontSize > startSSFontSize);

        // 2b. Verify that we have zoomed in with the correct stepping
//        assertEquals(newSSFontSize - startSSFontSize, OpenSHAPAView.ZOOM_INTERVAL);
        assertEquals(newConfigFontSize - startConfigFontSize,
                OpenSHAPAView.ZOOM_INTERVAL);

        /* 2c. Verify that the zoom value is bounded between the minimum and
         * maximum zoom size.
         */
//        assertTrue(OpenSHAPAView.ZOOM_MIN_SIZE <= newSSFontSize);
//        assertTrue(newSSFontSize <= OpenSHAPAView.ZOOM_MAX_SIZE);
        assertTrue(OpenSHAPAView.ZOOM_MIN_SIZE <= newConfigFontSize);
        assertTrue(newConfigFontSize <= OpenSHAPAView.ZOOM_MAX_SIZE);
    }

    /**
     * These inner classes were created because I thought telling the
     * application to perform zooms in the event dispatcher thread would solve
     * the problem of not being able to retrieve the expected zoom value.
     * However, this approach did not work.
     */
//    private class ZoomInHelper implements Runnable {
//        MenuBar menuBar;
//
//        public void setMenuBar(MenuBar menuBar) {
//            this.menuBar = menuBar;
//        }
//
//        public void run() {
//            System.out.println("In the AWT-EDT? " + SwingUtilities.isEventDispatchThread());
//            menuBar.getMenu("Spreadsheet")
//               .getSubMenu("Zoom")
//               .getSubMenu("Zoom In").click();
//        }
//
//    }

//    private class FontSizeSeeker implements Runnable {
//        int fontSize;
//        Window window;
//
//
//        public int getFontSize() {
//            return fontSize;
//        }
//
//        public void setWindow(Window window) {
//            this.window = window;
//        }
//
//        public void run() {
//            System.out.println("In the AWT-EDT? " + SwingUtilities.isEventDispatchThread());
//            SpreadsheetPanel sp = (SpreadsheetPanel)window
//                    .getUIComponents(Spreadsheet.class)[0]
//                    .getAwtComponent();
//            SpreadsheetCell sc = (SpreadsheetCell)sp
//                    .getColumns()
//                    .elementAt(0)
//                    .getCells()
//                    .elementAt(0);
//            JTextArea textArea = sc.getDataView();
//            fontSize = textArea.getFont().getSize();
//            System.out.println("FSS: " + fontSize);
//        }
//
//    }

    /**
     * The aim of this test is to:
     *  1. Test the zoom reset functionality
     *  2. Test if zoom out is zooming out by the correct amount
     *  3. Test if zooming out is bounded by the minimum and maximum zoom size
     *
     * The correctness of the zoom behaviour is tested against the constants
     * defined in OpenSHAPAView, namely ZOOM_DEFAULT_SIZE for the reset
     * functionality, and ZOOM_INTERVAL for zooming in using the correct
     * increment.
     */
    public void testZoomingOut2() {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        int startSSFontSize = 0;

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
        menuBar.getMenu("Spreadsheet")
               .getSubMenu("Zoom")
               .getSubMenu("Reset Zoom").click();

        /* 1b. Get start font size, this is the size being currently displayed
         * by the spreadsheet.
         */
//        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
//                (window.getUIComponents(Spreadsheet.class)[0]
//                        .getAwtComponent()));

//        startSSFontSize = ss.getColumns().elementAt(0).getCells().elementAt(0)
//                .getValueFont().getSize();


        // 1c. The stored configuration value should have reset to default
        int startConfigFontSize = Configuration.getInstance().getSSDataFont().getSize();
        assertEquals(OpenSHAPAView.ZOOM_DEFAULT_SIZE, startConfigFontSize);

        // 1d. The starting font size should be the default font size
//        assertEquals(OpenSHAPAView.ZOOM_DEFAULT_SIZE, startSSFontSize);

        // 2. Zoom in once
        menuBar.getMenu("Spreadsheet")
               .getSubMenu("Zoom")
               .getSubMenu("Zoom Out").click();

        // 2a. Verify that we have zoomed out.
        int newConfigFontSize = Configuration.getInstance()
                .getSSDataFont().getSize();
        assertTrue(newConfigFontSize < startConfigFontSize);

//        int newSSFontSize = ss.getColumns().elementAt(0).getCells().elementAt(0)
//                .getValueFont().getSize();
//        assertTrue(newSSFontSize < startSSFontSize);

        // 2b. Verify that we have zoomed out with the correct stepping
//        assertEquals(startSSFontSize - newSSFontSize, OpenSHAPAView.ZOOM_INTERVAL);

        /* 2c. Verify that the zoom value is bounded between the minimum and
         * maximum zoom size.
         */
//        assertTrue(OpenSHAPAView.ZOOM_MIN_SIZE <= newSSFontSize);
//        assertTrue(newSSFontSize <= OpenSHAPAView.ZOOM_MAX_SIZE);
        assertTrue(OpenSHAPAView.ZOOM_MIN_SIZE <= newConfigFontSize);
        assertTrue(newConfigFontSize <= OpenSHAPAView.ZOOM_MAX_SIZE);
    }

    /**
     * Test zooming out and reset.
     */
//    public void testZoomingOut() {
//        //Preparation
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//        int startFontSize = 0;
//
//        // 1. Open and run script to populate database
//        String root = System.getProperty("testPath");
//        File demoFile = new File(root + "/ui/demo_data.rb");
//        assertTrue(demoFile.exists());
//
//        WindowInterceptor
//                .init(menuBar.getMenu("Script").getSubMenu("Run script")
//                    .triggerClick())
//                .process(FileChooserHandler.init()
//                    .assertIsOpenDialog()
//                    .assertAcceptsFilesOnly()
//                    .select(demoFile.getAbsolutePath()))
//                .process(new WindowHandler() {
//                    public Trigger process(Window console) {
//                        return console.getButton("Close").triggerClick();
//                    }
//                })
//                .run();
//
//        // 1a. Reset zoom and get start font size
//        menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
//            .getSubMenu("Reset Zoom").click();
//        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
//                window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//        startFontSize = ss.getColumns().elementAt(0).getCells().elementAt(0)
//                .getValueFont().getSize();
//
//        // 2. Test zooming out from start
//        int currFontSize = Integer.MAX_VALUE;
//        int numberOfZooms = 0;
//
//        int firstCellFontSize = ss.getColumns().elementAt(0).getCells()
//                .elementAt(0).getValueFont().getSize();
//
//        while (currFontSize > firstCellFontSize) {
//            currFontSize = firstCellFontSize;
//            //Zoom in
//            menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
//                    .getSubMenu("Zoom Out").click();
//            ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
//                    Spreadsheet.class)[0].getAwtComponent()));
//            numberOfZooms++;
//            firstCellFontSize = ss.getColumns().elementAt(0).getCells()
//                    .elementAt(0).getValueFont().getSize();
//
//            //Check that all font sizes have decreased
//            for (Column col : ss.getColumns()) {
//                for (Cell cell : col.getCells()) {
//                    assertTrue(cell.getValueFont().getSize()
//                            == firstCellFontSize);
//                }
//            }
//            assertTrue((firstCellFontSize < currFontSize)
//                    || (numberOfZooms > 1));
//        }
//
//        assertTrue(numberOfZooms > 1);
//
//        // 3. Reset zoom and check reset
//        menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
//                    .getSubMenu("Reset Zoom").click();
//        ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
//                    Spreadsheet.class)[0].getAwtComponent()));
//        assertTrue(startFontSize == ss.getColumns().elementAt(0).getCells()
//                    .elementAt(0).getValueFont().getSize());
//
//        currFontSize = 0;
//        numberOfZooms = 0;
//
//        firstCellFontSize = ss.getColumns().elementAt(0).getCells()
//                .elementAt(0).getValueFont().getSize();
//
//        // 4. Zoom in fully
//         while (currFontSize < firstCellFontSize) {
//            currFontSize = firstCellFontSize;
//            //Zoom out
//            menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
//                    .getSubMenu("Zoom In").click();
//            ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
//                    Spreadsheet.class)[0].getAwtComponent()));
//            numberOfZooms++;
//            firstCellFontSize = ss.getColumns().elementAt(0).getCells()
//                    .elementAt(0).getValueFont().getSize();
//         }
//
//        assertTrue(currFontSize > startFontSize);
//
//        //5. Test zoom out from largest
//        currFontSize = Integer.MAX_VALUE;
//        numberOfZooms = 0;
//
//        firstCellFontSize = ss.getColumns().elementAt(0).getCells()
//                .elementAt(0).getValueFont().getSize();
//
//        while (currFontSize > firstCellFontSize) {
//            currFontSize = firstCellFontSize;
//            //Zoom out
//            menuBar.getMenu("Spreadsheet").getSubMenu("Zoom")
//                    .getSubMenu("Zoom Out").click();
//            ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(
//                    Spreadsheet.class)[0].getAwtComponent()));
//            numberOfZooms++;
//            firstCellFontSize = ss.getColumns().elementAt(0).getCells()
//                    .elementAt(0).getValueFont().getSize();
//
//            //Check that all font sizes have increased
//            for (Column col : ss.getColumns()) {
//                for (Cell cell : col.getCells()) {
//                    assertTrue(cell.getValueFont().getSize()
//                            == firstCellFontSize);
//                }
//            }
//            assertTrue((firstCellFontSize < currFontSize)
//                    || (numberOfZooms > 1));
//        }
//
//        assertTrue(numberOfZooms > 1);
//    }
}