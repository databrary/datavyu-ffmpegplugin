package org.openshapa.uitests;


import java.io.File;
import java.util.Vector;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.uispec4j.Cell;
import org.uispec4j.Column;
import org.uispec4j.MenuBar;
import org.uispec4j.OpenSHAPAUISpecTestCase;
import org.uispec4j.Spreadsheet;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;




/**
 * Test for the Deletion of cells.
 *
 */
public final class UIDeleteCellsTest extends OpenSHAPAUISpecTestCase {
//    /**
//     * Test for Bug 713.
//     * Bug 713: If an extended selection includes the last (bottom) cell
//     * and Delete Cells is selected:
//     * Expected: All cells are deleted and all related graphics are removed
//     * Actual: All cells appear to be deleted but the cell graphics for
//     * the last cell are not removed.
//     * @throws java.lang.Exception on any error
//     */
//    public void testBug713() throws Exception {
//        String root = System.getProperty("testPath");
//        File demoFile = new File(root + "/ui/demo_data.rb");
//        assertTrue(demoFile.exists());
//
//         // Retrieve the components
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//
//        // 1. Open and run script to populate database
//        WindowInterceptor
//                .init(menuBar.getMenu("Script").getSubMenu("Run script")
//                    .triggerClick())
//                .process(FileChooserHandler.init()
//                    .assertIsOpenDialog()
//                    .assertAcceptsFilesOnly()
//                    .select(demoFile))
//                .process(new WindowHandler() {
//                    public Trigger process(Window console) {
//                        return console.getButton("Close").triggerClick();
//                    }
//                })
//                .run();
//
//
//        // 2. For each column, select all cells and press delete
//        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
//                (window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//
//        Vector<Column> cols = ss.getColumns();
//
//
//        //Hack algorithm to get around UISpec4J not detecting deleted cells
//        for (int i = 0; i < cols.size(); i++) {
//            ss = new Spreadsheet((SpreadsheetPanel)
//                (window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//
//            cols = ss.getColumns();
//            Column col = cols.elementAt(i);
//            for (Cell cell : col.getCells()) {
//                cell.setSelected(true);
//            }
//            menuBar.getMenu("Spreadsheet").getSubMenu("Delete Cell").click();
//
//            Spreadsheet ss2 = null;
//            ss2 = new Spreadsheet((SpreadsheetPanel)
//                (window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//            String colName = col.getHeaderName();
//            Column col2 = null;
//            col2 = ss2.getSpreadsheetColumn(colName);
//
//            assertTrue(col2.getCells().isEmpty());
//
//        }
//
//        //Proper algorithm, but doesn't work because of UISpec4J problems
////        for (Column col : cols) {
////            for (Cell cell : col.getCells()) {
////                cell.setSelected(true);
////            }
////            //Delete
////            menuBar.getMenu("Spreadsheet").getSubMenu("Delete Cell").click();
////            //Assert deleted
////            assertTrue(col.getCells().isEmpty());
////        }
//    }
//
//    /**
//     * Select all cells in spreadsheet and delete at once.
//     * @throws java.lang.Exception on any error
//     */
//    public void testDeleteAllCellsInSpreadsheet() throws Exception {
//        String root = System.getProperty("testPath");
//        File demoFile = new File(root + "/ui/demo_data.rb");
//        assertTrue(demoFile.exists());
//
//         // Retrieve the components
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//
//        // 1. Open and run script to populate database
//        WindowInterceptor
//                .init(menuBar.getMenu("Script").getSubMenu("Run script")
//                    .triggerClick())
//                .process(FileChooserHandler.init()
//                    .assertIsOpenDialog()
//                    .assertAcceptsFilesOnly()
//                    .select(demoFile))
//                .process(new WindowHandler() {
//                    public Trigger process(Window console) {
//                        return console.getButton("Close").triggerClick();
//                    }
//                })
//                .run();
//
//
//        // 2. Select all cells and press delete
//        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
//                (window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//
//        Vector<Column> cols = ss.getColumns();
//
//
//        //Hack algorithm to get around UISpec4J not detecting deleted cells
//        for (int i = 0; i < cols.size(); i++) {
//            ss = new Spreadsheet((SpreadsheetPanel)
//                (window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//
//            cols = ss.getColumns();
//            Column col = cols.elementAt(i);
//            for (Cell cell : col.getCells()) {
//                cell.setSelected(true);
//            }
//        }
//        menuBar.getMenu("Spreadsheet").getSubMenu("Delete Cell").click();
//
//        Spreadsheet ss2 = null;
//        ss2 = new Spreadsheet((SpreadsheetPanel)
//                (window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//        Vector<Column> cols2 = ss2.getColumns();
//        for (Column col : cols2) {
//            assertTrue(col.getCells().isEmpty());
//        }
//    }
//
//    /**
//     * Delete all cells one at a time.
//     * @throws java.lang.Exception on any error
//     */
//    public void testDeleteSingleCells() throws Exception {
//        String root = System.getProperty("testPath");
//        File demoFile = new File(root + "/ui/demo_data.rb");
//        assertTrue(demoFile.exists());
//
//         // Retrieve the components
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//
//        // 1. Open and run script to populate database
//        WindowInterceptor
//                .init(menuBar.getMenu("Script").getSubMenu("Run script")
//                    .triggerClick())
//                .process(FileChooserHandler.init()
//                    .assertIsOpenDialog()
//                    .assertAcceptsFilesOnly()
//                    .select(demoFile))
//                .process(new WindowHandler() {
//                    public Trigger process(Window console) {
//                        return console.getButton("Close").triggerClick();
//                    }
//                })
//                .run();
//
//
//        // 2. For each column, select all cells and press delete
//        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
//                (window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//
//        Vector<Column> cols = ss.getColumns();
//
//
//        //Hack algorithm to get around UISpec4J not detecting deleted cells
//        for (int i = 0; i < cols.size(); i++) {
//            ss = new Spreadsheet((SpreadsheetPanel)
//                (window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//
//            cols = ss.getColumns();
//            Column col = cols.elementAt(i);
//            int numOfCells = col.getCells().size();
//            for (int j = 0; j < numOfCells; j++) {
//                Cell cell = col.getCells().elementAt(j);
//                cell.setSelected(true);
//                menuBar.getMenu("Spreadsheet").getSubMenu("Delete Cell")
//                        .click();
//                Spreadsheet ss2 = null;
//                ss2 = new Spreadsheet((SpreadsheetPanel)
//                (window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//                Column col2 = ss2.getColumns().elementAt(i);
//                assertTrue(col2.getCells().size()
//                        == (col.getCells().size() - (j + 1)));
//            }
//        }
//    }
}