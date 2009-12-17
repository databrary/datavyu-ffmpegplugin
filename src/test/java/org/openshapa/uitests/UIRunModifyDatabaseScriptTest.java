package org.openshapa.uitests;

import java.io.File;
import java.io.FilenameFilter;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.project.Project;
import org.openshapa.util.UIUtils;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.uispec4j.MenuBar;
import org.uispec4j.OpenSHAPAUISpecTestCase;
import org.uispec4j.Spreadsheet;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;

/**
 * Test the creation of a new database.
 *
 */
public final class UIRunModifyDatabaseScriptTest
        extends OpenSHAPAUISpecTestCase {

    /**
     * Initialiser called before each unit test.
     *
     * @throws java.lang.Exception When unable to initialise test
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setAdapter(new MainClassAdapter(OpenSHAPA.class, new String[0]));

        final String tempFolder = System.getProperty("java.io.tmpdir");

        // Delete other temporary CSV and SHAPA files
        FilenameFilter ff  = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".csv") || name.endsWith(".shapa"));
            }
        };
        File tempDirectory = new File(tempFolder);
        String[] files = tempDirectory.list(ff);
        for (int i = 0; i < files.length; i++) {
            File file = new File(tempFolder + "/" + files[i]);
            file.deleteOnExit();
            file.delete();
        }
    }

    static {
        UISpec4J.setWindowInterceptionTimeLimit(120000);
        UISpec4J.init();
    }

    @Override
    protected void tearDown() throws Exception {
        final String tempFolder = System.getProperty("java.io.tmpdir");

        // Delete other temporary CSV and SHAPA files
        FilenameFilter ff  = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".csv") || name.endsWith(".shapa"));
            }
        };
        File tempDirectory = new File(tempFolder);
        String[] files = tempDirectory.list(ff);
        for (int i = 0; i < files.length; i++) {
            File file = new File(tempFolder + "/" + files[i]);
            file.deleteOnExit();
            file.delete();
        }

        getMainWindow().dispose();
        super.tearDown();
    }

    /**
     * Test new spreadsheet.
     *
     * @throws java.lang.Exception on any error
     */
    public void testModifySpreadsheet() throws Exception {
        //Preparation
        final Window window = getMainWindow();
        final MenuBar menuBar = window.getMenuBar();

        // 1. Open and run script to populate database
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());
        final File modifyFile = new File(root + "/ui/find_and_replace.rb");
        assertTrue(modifyFile.exists());

        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertAcceptsFilesOnly()
                    .select(demoFile.getAbsolutePath()))
                .process(new WindowHandler() {
                    public Trigger process(final Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        // 1a. Check that database is populated
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
              (window.getUIComponents(Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(ss.getColumns().size() > 0);


        // 2. Perform a find and replace; replace all instances of "moo"
        // with "frog"
        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertAcceptsFilesOnly()
                    .select(modifyFile.getAbsolutePath()))
                .process(new WindowHandler() {
                    public Trigger process(final Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        // 3. Save the database- compare it to the reference .csv

        String tempFolder = System.getProperty("java.io.tmpdir");
 
        WindowInterceptor
                .init(menuBar.getMenu("File").getSubMenu("Save As...")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsSaveDialog()
                    .assertAcceptsFilesOnly()
                    .select(tempFolder + "/savedSHAPA.shapa"))
                .run();

        Project project = OpenSHAPA.getProject();
        File savedCSV = new File(project.getDatabaseDir(),
                                       project.getDatabaseFile());
        File testCSV = new File(root + "/ui/modify-test-out.csv");

        assertTrue(UIUtils.areFilesSame(testCSV, savedCSV));
    }
}
