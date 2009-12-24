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
 * Test for the Deletion of columns.
 *
 */
public final class UIDeleteColumnsTest extends OpenSHAPAUISpecTestCase {
    /**
     * Test for deletion of columns.
     * Delete columns one by one.
     * @throws java.lang.Exception on any error
     */
    public void testDeleteSingleColumns() throws Exception {
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

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


        // 2. Sequentially select each column and delete.
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        Vector<Column> cols = ss.getColumns();

        int numOfCols = cols.size();
        for (int i = 0; i < numOfCols; i++) {
            Column col = cols.lastElement();
            String colName = col.getHeaderName();
            col.select();
            menuBar.getMenu("Spreadsheet").getSubMenu("Delete Variable")
                    .click();

            //Confirm that this particular column no longer exists
            cols = ss.getColumns();
            for (Column c : cols) {
                assertFalse(c.getHeaderName().equals(colName));
            }
        }
        assertTrue(cols.isEmpty());
    }

    /**
     * Test for deletion of columns.
     * Delete all columns at once.
     * @throws java.lang.Exception on any error
     */
    public void testDeleteMultipleColumns() throws Exception {
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

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


        // 2. Sequentially select each column and delete.
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        Vector<Column> cols = ss.getColumns();

        for (Column col : cols) {
            col.select();
        }

        menuBar.getMenu("Spreadsheet").getSubMenu("Delete Variable").click();

        cols = ss.getColumns();
        assertTrue(cols.isEmpty());
    }
}