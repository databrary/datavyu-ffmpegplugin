package org.openshapa.uitests;

import java.io.File;
import java.util.Vector;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.views.discrete.SpreadsheetPanel;
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
 * Bug 65 Test
 * Columns should always stay in the order they are
 * inserted regardless of show spreadsheet is invoked.
 */
public final class UIBug65Test extends OpenSHAPAUISpecTestCase {

    /**
     * Initialiser called before each unit test.
     *
     * @throws java.lang.Exception When unable to initialise test
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

     /**
     * Called after each test.
     * @throws Exception on any exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    static {
        UISpec4J.setWindowInterceptionTimeLimit(4000000);
        UISpec4J.setAssertionTimeLimit(4000);
        UISpec4J.init();
    }

    /**
     * Test that the order of columns remains the same.
     *
     * @throws java.lang.Exception on any error
     */
    public void testColumnOrder() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

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

        // 2. Save column vector
        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel) (
                window.getUIComponents(
                Spreadsheet.class)[0].getAwtComponent())));
        assertTrue(ss.getColumns().size() > 0);

        Vector<Column> originalColumns = ss.getColumns();

        //3. Press "Show spreadsheet"
        menuBar.getMenu("Spreadsheet").getSubMenu("Show Spreadsheet").click();

        //3. Check that columns are the same
        Spreadsheet ss2 = new Spreadsheet(((SpreadsheetPanel) (
                window.getUIComponents(
                Spreadsheet.class)[0].getAwtComponent())));

        Vector<Column> newColumns = ss2.getColumns();

        assertTrue(newColumns.size() == originalColumns.size());

        for (int i = 0; i < newColumns.size(); i++) {
            assertTrue(newColumns.elementAt(i).getHeaderName()
                    .equals(originalColumns.elementAt(i).getHeaderName()));
            assertTrue(newColumns.elementAt(i).getHeaderType()
                    .equals(originalColumns.elementAt(i).getHeaderType()));
        }
    }
}