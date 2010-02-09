package org.openshapa.uitests;

import java.io.File;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.views.discrete.SpreadsheetPanel;
import java.util.Vector;
import org.uispec4j.Cell;
import org.uispec4j.Column;
import org.uispec4j.Key;
import org.uispec4j.MenuBar;
import org.uispec4j.OpenSHAPAUISpecTestCase;
import org.uispec4j.Spreadsheet;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;

/**
 * Test for the New Cells.
 */
public final class UIDeleteCellValueTest extends OpenSHAPAUISpecTestCase {
//    static {
//      UISpec4J.setWindowInterceptionTimeLimit(120000);
//      UISpec4J.init();
//    }
//
//
//
//    /**
//     * Test deleting values from nominal cells.
//     * @throws java.lang.Exception on any error
//     */
//    public void testDeleteNominalCell() throws Exception {
//        String type = "NOMINAL";
//
//        String root = System.getProperty("testPath");
//        File demoFile = new File(root + "/ui/all_column_types.rb");
//        assertTrue(demoFile.exists());
//
//        // Retrieve the components
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//
//        // 1. Open and run script to populate database
//        WindowInterceptor.init(menuBar.getMenu("Script").getSubMenu(
//                "Run script").triggerClick()).process(FileChooserHandler.init()
//                .assertIsOpenDialog().assertAcceptsFilesOnly().select(demoFile))
//                .process(new WindowHandler() {
//
//            public Trigger process(Window console) {
//                return console.getButton("Close").triggerClick();
//            }
//        }).run();
//
//        //2. Open spreadsheet and check that script has data
//        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel) (
//                window.getUIComponents(
//                Spreadsheet.class)[0].getAwtComponent())));
//        assertTrue(ss.getColumns().size() > 0);
//
//        highlightAndBackspaceTest(ss, type);
//        highlightAndDeleteTest(ss, type);
//        backSpaceAllTest(ss, type);
//        deleteAllTest(ss, type);
//    }
//
//    /**
//     * Test deleting values from float cells.
//     * @throws java.lang.Exception on any error
//     */
//    public void testDeleteFloatCell() throws Exception {
//        String type = "FLOAT";
//
//        String root = System.getProperty("testPath");
//        File demoFile = new File(root + "/ui/all_column_types.rb");
//        assertTrue(demoFile.exists());
//
//        // Retrieve the components
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//
//        // 1. Open and run script to populate database
//        WindowInterceptor.init(menuBar.getMenu("Script").getSubMenu(
//                "Run script").triggerClick()).process(FileChooserHandler.init().
//                assertIsOpenDialog().assertAcceptsFilesOnly().select(demoFile)).
//                process(new WindowHandler() {
//
//            public Trigger process(Window console) {
//                return console.getButton("Close").triggerClick();
//            }
//        }).run();
//
//        //2. Open spreadsheet and check that script has data
//        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel) (
//                window.getUIComponents(
//                Spreadsheet.class)[0].getAwtComponent())));
//        assertTrue(ss.getColumns().size() > 0);
//
//        highlightAndBackspaceTest(ss, type);
//        highlightAndDeleteTest(ss, type);
//        backSpaceAllTest(ss, type);
//        deleteAllTest(ss, type);
//    }
//
//    /**
//     * Test deleting values from integer cells.
//     * @throws java.lang.Exception on any error
//     */
//    public void testDeleteIntCell() throws Exception {
//        String type = "INTEGER";
//
//        String root = System.getProperty("testPath");
//        File demoFile = new File(root + "/ui/all_column_types.rb");
//        assertTrue(demoFile.exists());
//
//        // Retrieve the components
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//
//        // 1. Open and run script to populate database
//        WindowInterceptor.init(menuBar.getMenu("Script").getSubMenu(
//                "Run script").triggerClick()).process(FileChooserHandler.init().
//                assertIsOpenDialog().assertAcceptsFilesOnly().select(demoFile)).
//                process(new WindowHandler() {
//
//            public Trigger process(Window console) {
//                return console.getButton("Close").triggerClick();
//            }
//        }).run();
//
//        //2. Open spreadsheet and check that script has data
//        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel) (
//                window.getUIComponents(
//                Spreadsheet.class)[0].getAwtComponent())));
//        assertTrue(ss.getColumns().size() > 0);
//
//        highlightAndBackspaceTest(ss, type);
//        highlightAndDeleteTest(ss, type);
//        backSpaceAllTest(ss, type);
//        deleteAllTest(ss, type);
//    }
//
//    /**
//     * Test deleting values from text cells.
//     * @throws java.lang.Exception on any error
//     */
//    public void testDeleteTextCell() throws Exception {
//        String type = "TEXT";
//
//        String root = System.getProperty("testPath");
//        File demoFile = new File(root + "/ui/all_column_types.rb");
//        assertTrue(demoFile.exists());
//
//        // Retrieve the components
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//
//        // 1. Open and run script to populate database
//        WindowInterceptor.init(menuBar.getMenu("Script").getSubMenu(
//                "Run script").triggerClick()).process(FileChooserHandler.init().
//                assertIsOpenDialog().assertAcceptsFilesOnly().select(demoFile)).
//                process(new WindowHandler() {
//
//            public Trigger process(Window console) {
//                return console.getButton("Close").triggerClick();
//            }
//        }).run();
//
//        //2. Open spreadsheet and check that script has data
//        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel) (
//                window.getUIComponents(
//                Spreadsheet.class)[0].getAwtComponent())));
//        assertTrue(ss.getColumns().size() > 0);
//
//    /*BugzID629:highlightAndBackspaceTest(ss, type);
//    highlightAndDeleteTest(ss, type);
//    backSpaceAllTest(ss, type);
//    deleteAllTest(ss, type);*/
//    }
//
//
//
//    /**
//     * Tests deletion by selecting all and pressing backspace.
//     * @param ss Spreadsheet
//     * @param type column type to test
//     */
//    private void highlightAndBackspaceTest(final Spreadsheet ss,
//            final String type) {
//        Vector<Cell> cells = null;
//        //1. Get cells for test type
//        for (Column col : ss.getColumns()) {
//            if (col.getHeaderType().equalsIgnoreCase(type)) {
//                cells = col.getCells();
//                break;
//            }
//        }
//        //2. Test different inputs as per specifications
//        Cell c = cells.elementAt(0);
//        TextBox t = c.getValue();
//        t.selectAll();
//        c.selectAllAndTypeKey(Cell.VALUE, Key.BACKSPACE);
//        assertTrue(t.getText().equals("<val>"));
//    }
//
//     /**
//     * Tests deletion by selecting all and pressing delete.
//     * @param ss Spreadsheet
//     * @param type column type to test
//     */
//    private void highlightAndDeleteTest(final Spreadsheet ss,
//            final String type) {
//        Vector<Cell> cells = null;
//
//        //1. Get cells for test type
//        for (Column col : ss.getColumns()) {
//            if (col.getHeaderType().equalsIgnoreCase(type)) {
//                cells = col.getCells();
//                break;
//            }
//        }
//
//        //2. Test different inputs as per specifications
//        Cell c = cells.elementAt(1);
//        TextBox t = c.getValue();
//        t.selectAll();
//        c.selectAllAndTypeKey(Cell.VALUE, Key.DELETE);
//        assertTrue(t.getText().equals("<val>"));
//    }
//
//    /**
//     * Tests deletion by backspacing all.
//     * @param ss Spreadsheet
//     * @param type column type to test
//     */
//    private void backSpaceAllTest(final Spreadsheet ss, final String type) {
//        Vector<Cell> cells = null;
//
//        //1. Get cells for test type
//        for (Column col : ss.getColumns()) {
//            if (col.getHeaderType().equalsIgnoreCase(type)) {
//                cells = col.getCells();
//                break;
//            }
//        }
//
//        //2. Test different inputs as per specifications
//        Cell c = cells.elementAt(2);
//        TextBox t = c.getValue();
//        c.pressKeys(Cell.VALUE, new Key [] {Key.END});
//        int temp = c.getValueText().length();
//        for (int i = 0; i < temp + 1; i++) {
//            c.enterTextKeepFocus(Cell.VALUE, "\u0008");
//        }
//        assertTrue(t.getText().equals("<val>"));
//    }
//
//    /**
//     * Tests deletion by pressing delete.
//     * @param ss Spreadsheet
//     * @param type column type to test
//     */
//    private void deleteAllTest (final Spreadsheet ss, final String type) {
//
//        Vector<Cell> cells = null;
//
//        //1. Get cells for test type
//        for (Column col : ss.getColumns()) {
//            if (col.getHeaderType().equalsIgnoreCase(type)) {
//                cells = col.getCells();
//                break;
//            }
//        }
//
//        //2. Test different inputs as per specifications
//        Cell c = cells.elementAt(2);
//        TextBox t = c.getValue();
//        c.pressKeys(Cell.VALUE, new Key [] {Key.HOME});
//        int temp = c.getValueText().length();
//        for (int i = 0; i < temp + 1; i++) {
//            c.enterTextKeepFocus(Cell.VALUE, "\u007f");
//        }
//        assertTrue(t.getText().equals("<val>"));
//    }

}



