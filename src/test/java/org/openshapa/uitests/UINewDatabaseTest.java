package org.openshapa.uitests;

import java.io.File;
import javax.swing.Box;
import junitx.util.PrivateAccessor;
import org.openshapa.Configuration;
import org.openshapa.util.ConfigProperties;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.uispec4j.MenuBar;
import org.uispec4j.OpenSHAPAUISpecTestCase;
import org.uispec4j.Spreadsheet;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.BasicHandler;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;

/**
 * Test the creation of a new database.
 *
 */
public final class UINewDatabaseTest extends OpenSHAPAUISpecTestCase {

    static {
        try {
            ConfigProperties p = (ConfigProperties) PrivateAccessor.getField(Configuration.getInstance(), "properties");
            p.setCanSendLogs(false);
        } catch (Exception e) {
            System.err.println("Unable to overide sending usage logs");
        }

        UISpec4J.setWindowInterceptionTimeLimit(120000);
        UISpec4J.init();
    }

    /**
     * Test new spreadsheet.
     *
     * @throws java.lang.Exception on any error
     */
    public void testNewSpreadsheet() throws Exception {
        //Preparation
        final Window window = getMainWindow();
        final MenuBar menuBar = window.getMenuBar();

        // 1. Open and run script to populate database
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertAcceptsFilesOnly()
                    .select(demoFile.getAbsolutePath()))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        // 1a. Check that database is populated
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
              (window.getUIComponents(Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(ss.getColumns().size() > 0);

        // 2. Create new database (and discard unsaved changes)

        WindowInterceptor
            .init(menuBar.getMenu("File").getSubMenu("New").triggerClick())
            .process(BasicHandler.init()
                    .triggerButtonClick("OK"))
            .process(new WindowHandler() {
                public Trigger process(final Window newDBWindow) {
                    newDBWindow.
                            getTextBox("nameField").setText("newDB");
                    return newDBWindow.getButton("Ok").triggerClick();
                }
             })
            .run();

        // 2a. Check that all data is cleared
        Spreadsheet ss2 = new Spreadsheet((SpreadsheetPanel)
              (window.getUIComponents(Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(ss2.getColumns().size() == 0);

        // 2b. Check that variable list is empty
        Window varListWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Variable List").triggerClick());
        assertTrue(varListWindow.getTable().getRowCount() == 0);

        // 2c. Check that vocab editor is empty
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());
        assertTrue((vocEdWindow.getPanel("currentVocabList")
                               .getSwingComponents(Box.class)).length == 0);
    }

    /**
     * Should display warning if database has no name.
     *
     * @throws java.lang.Exception on any error
     */
    public void testBug576() throws Exception {
        //Preparation
        final Window window = getMainWindow();
        final MenuBar menuBar = window.getMenuBar();

        // 1. Open and run script to populate database
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertAcceptsFilesOnly()
                    .select(demoFile.getAbsolutePath()))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        // 1a. Check that database is populated
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
              (window.getUIComponents(Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(ss.getColumns().size() > 0);

        // 2. Create new database (and discard unsaved changes)

        WindowInterceptor
            .init(menuBar.getMenu("File").getSubMenu("New").triggerClick())
            .process(BasicHandler.init()
                    .triggerButtonClick("OK"))
            .process(new WindowHandler() {
                public Trigger process(final Window newDBWindow) {
                    return newDBWindow.getButton("Ok").triggerClick();
                }
             })
            .process(BasicHandler.init()
                     .triggerButtonClick("OK"))
            .run();

        //BugzID938 - Expecting the new database dialog to remain open.
        //Must write test to check that this is true.

        // 2a. Check that all data is unchanged
        Spreadsheet ss2 = new Spreadsheet((SpreadsheetPanel)
              (window.getUIComponents(Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(ss2.equals(ss));
    }
}