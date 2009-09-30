package org.openshapa.uitests;

import java.io.File;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
import java.util.Vector;
import org.uispec4j.Cell;
import org.uispec4j.Column;
import org.uispec4j.Key;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;

/**
 * Test for the New Cells.
 */
public final class UIDeleteCellValueTest extends UISpecTestCase {

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

    /**
     * Different cell variable types.
     */
    private static final String[] VAR_TYPES = {"TEXT", "PREDICATE", "INTEGER",
        "NOMINAL", "MATRIX", "FLOAT"
    };


    static {
      UISpec4J.init();
    }



    /**
     * Test deleting values from nominal cells.
     * @throws java.lang.Exception on any error
     */
    public void testDeleteNominalCell() throws Exception {
        String type = "NOMINAL";

        /*BugzID629:highlightAndBackspaceTest(type);
        highlightAndDeleteTest(type);
        backSpaceAllTest(type); */
        deleteAllTest(type);
    }

    /**
     * Test deleting values from float cells.
     * @throws java.lang.Exception on any error
     */
    public void testDeleteFloatCell() throws Exception {
        String type = "FLOAT";

        highlightAndBackspaceTest(type);
        highlightAndDeleteTest(type);
        backSpaceAllTest(type);
        deleteAllTest(type);
    }

     /**
     * Test deleting values from integer cells.
     * @throws java.lang.Exception on any error
     */
    public void testDeleteIntCell() throws Exception {
        String type = "INTEGER";

        highlightAndBackspaceTest(type);
        highlightAndDeleteTest(type);
        backSpaceAllTest(type);
        deleteAllTest(type);
    }

         /**
     * Test deleting values from text cells.
     * @throws java.lang.Exception on any error
     */
    public void testDeleteTextCell() throws Exception {
        String type = "INTEGER";

        highlightAndBackspaceTest(type);
        highlightAndDeleteTest(type);
        backSpaceAllTest(type);
        deleteAllTest(type);
    }

    /**
     * Tests deletion by selecting all and pressing backspace.
     * @param type column type to test
     */
    private void highlightAndBackspaceTest(final String type) {
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/all_column_types.rb");
        assertTrue(demoFile.exists());

        String expectedTestOutput = "<val>";

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Open and run script to populate database
        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsOpenDialog()
                    .assertAcceptsFilesOnly()
                    .select(demoFile))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        //2. Open spreadsheet and check that script has data
        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel) (
                window.getUIComponents(
                Spreadsheet.class)[0].getAwtComponent())));
        assertTrue(ss.getColumns().size() > 0);

        Vector<Cell> cells = null;

        //3. Get cells for test type
        for (Column col : ss.getColumns()) {
            if (col.getHeaderType().equalsIgnoreCase(type)) {
                cells = col.getCells();
                break;
            }
        }

        //4. Test different inputs as per specifications
        Cell c = cells.elementAt(0);
        TextBox t = c.getValue();
        t.selectAll();
        c.selectAllAndTypeKey(Cell.VALUE, Key.BACKSPACE);
        assertTrue(t.getText().equals(expectedTestOutput));
    }

     /**
     * Tests deletion by selecting all and pressing delete.
     * @param type column type to test
     */
    private void highlightAndDeleteTest(final String type) {
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/all_column_types.rb");
        assertTrue(demoFile.exists());

        String expectedTestOutput = "<val>";

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Open and run script to populate database
        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsOpenDialog()
                    .assertAcceptsFilesOnly()
                    .select(demoFile))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        //2. Open spreadsheet and check that script has data
        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel) (
                window.getUIComponents(
                Spreadsheet.class)[0].getAwtComponent())));
        assertTrue(ss.getColumns().size() > 0);

        Vector<Cell> cells = null;

        //3. Get cells for test type
        for (Column col : ss.getColumns()) {
            if (col.getHeaderType().equalsIgnoreCase(type)) {
                cells = col.getCells();
                break;
            }
        }

        //4. Test different inputs as per specifications
        Cell c = cells.elementAt(1);
        TextBox t = c.getValue();
        t.selectAll();
        c.selectAllAndTypeKey(Cell.VALUE, Key.DELETE);
        assertTrue(t.getText().equals(expectedTestOutput));
    }

     /**
     * Tests deletion by backspacing all.
     * @param type column type to test
     */
    private void backSpaceAllTest(final String type) {
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/all_column_types.rb");
        assertTrue(demoFile.exists());

        String expectedTestOutput = "<val>";

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Open and run script to populate database
        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsOpenDialog()
                    .assertAcceptsFilesOnly()
                    .select(demoFile))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        //2. Open spreadsheet and check that script has data
        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel) (
                window.getUIComponents(
                Spreadsheet.class)[0].getAwtComponent())));
        assertTrue(ss.getColumns().size() > 0);

        Vector<Cell> cells = null;

        //3. Get cells for test type
        for (Column col : ss.getColumns()) {
            if (col.getHeaderType().equalsIgnoreCase(type)) {
                cells = col.getCells();
                break;
            }
        }

        //4. Test different inputs as per specifications
        Cell c = cells.elementAt(2);
        TextBox t = c.getValue();
        c.pressKeys(Cell.VALUE, new Key [] {Key.END});
        int temp = c.getValueText().length();
        for (int i = 0; i < temp + 1; i++) {
            c.enterText(Cell.VALUE, "\u0008");
        }
        assertTrue(t.getText().equals(expectedTestOutput));
    }

     /**
     * Tests deletion by pressing delete.
     * @param type column type to test
     */
    private void deleteAllTest (final String type) {
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/all_column_types.rb");
        assertTrue(demoFile.exists());

        String expectedTestOutput = "<val>";

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Open and run script to populate database
        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsOpenDialog()
                    .assertAcceptsFilesOnly()
                    .select(demoFile))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        //2. Open spreadsheet and check that script has data
        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel) (
                window.getUIComponents(
                Spreadsheet.class)[0].getAwtComponent())));
        assertTrue(ss.getColumns().size() > 0);

        Vector<Cell> cells = null;

        //3. Get cells for test type
        for (Column col : ss.getColumns()) {
            if (col.getHeaderType().equalsIgnoreCase(type)) {
                cells = col.getCells();
                break;
            }
        }

        //4. Test different inputs as per specifications
        Cell c = cells.elementAt(2);
        TextBox t = c.getValue();
        c.pressKeys(Cell.VALUE, new Key [] {Key.HOME});
        int temp = c.getValueText().length();
        for (int i = 0; i < temp + 1; i++) {
            c.enterText(Cell.VALUE, "\u007f");
        }
        assertTrue(t.getText().equals(expectedTestOutput));
        }
}



