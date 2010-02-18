package org.openshapa.uitests;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;
import javax.swing.text.BadLocationException;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.openshapa.util.UIUtils;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for the New Cells.
 */
public final class UIDeleteCellValueTest extends OpenSHAPATestClass {
    /**
     * Test deleting values from nominal cells.
     * @throws java.lang.Exception on any error
     */
    @Test
    public void testDeleteNominalCell() throws Exception {
        System.err.println("testDeleteNominalCell");
        String type = "NOMINAL";

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/all_column_types.rb");
        Assert.assertTrue(demoFile.exists());

        //1. Run script to populate
        mainFrameFixture.menuItemWithPath("Script", "Run script").click();

        JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
        jfcf.selectFile(demoFile).approve();

        //Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        //2. Open spreadsheet and check that script has data
         JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);

        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        Vector<SpreadsheetColumnFixture> cols = ssPanel.allColumns();
        Assert.assertTrue(cols.size() > 0);


//        highlightAndBackspaceTest(ssPanel, type);
        highlightAndDeleteTest(ssPanel, type);
//        backSpaceAllTest(ss, type);
//        deleteAllTest(ss, type);
    }
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
    /**
     * Tests deletion by selecting all and pressing backspace.
     * @param ss Spreadsheet
     * @param type column type to test
     */
    private void highlightAndBackspaceTest(final SpreadsheetPanelFixture ss,
            final String type) throws BadLocationException {
        Vector<SpreadsheetCellFixture> cells = null;
        //1. Get cells for test type
        for (SpreadsheetColumnFixture col : ss.allColumns()) {
            if (col.getColumnType().equalsIgnoreCase(type)) {
                cells = col.allCells();
                break;
            }
        }

        //2. Test different inputs as per specifications
        SpreadsheetCellFixture c = cells.elementAt(0);
        c.select(SpreadsheetCellFixture.VALUE, 0, c.cellValue().text().length());
        c.pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_BACK_SPACE));
        Assert.assertEquals(c.cellValue().text(),"<val>");
    }

     /**
     * Tests deletion by selecting all and pressing delete.
     * @param ss Spreadsheet
     * @param type column type to test
     */
    private void highlightAndDeleteTest(final SpreadsheetPanelFixture ss,
            final String type) throws BadLocationException {
        Vector<SpreadsheetCellFixture> cells = null;
        //1. Get cells for test type
        for (SpreadsheetColumnFixture col : ss.allColumns()) {
            if (col.getColumnType().equalsIgnoreCase(type)) {
                cells = col.allCells();
                break;
            }
        }

        //2. Test different inputs as per specifications
        SpreadsheetCellFixture c = cells.elementAt(0);
        c.select(SpreadsheetCellFixture.VALUE, 0, c.cellValue().text().length());
        c.pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_DELETE));
        Assert.assertEquals(c.cellValue().text(),"<val>");
    }
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



