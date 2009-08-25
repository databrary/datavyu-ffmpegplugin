package org.openshapa.uitests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.NewDatabaseV;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;

/**
 * Test the creation of a new database.
 *
 */
public final class UISaveLoadCSVTest extends UISpecTestCase {

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
     * Resource map to access error messages in resources.
     */
    private ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(NewDatabaseV.class);


    /**
     * Test saving a database to a CSV file.
     * @throws java.lang.Exception on any error
     */
    public void testSavingCSV() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

        File testCSV = new File(root + "/ui/test.csv");
        assertTrue(testCSV.exists());

        String tempFolder = System.getProperty("java.io.tmpdir");
        File savedCSV = new File(/*tempFolder +*/ "/savedCSV.csv");
        //savedCSV.deleteOnExit();
        assertFalse(savedCSV.exists());

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

        // 2. Save CSV file
        WindowInterceptor
                .init(menuBar.getMenu("File").getSubMenu("Save As...")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsSaveDialog()
                    .assertAcceptsFilesOnly()
                    .select(savedCSV))
                .run();

        // 3. Check that CSV file is correct
        // Please note: This assumes that saving was working on 05-Aug-2009
        // Also, this currently does a byte by byte check. This may not be
        // correct for all platforms and true parsing may need to be
        // implemented.
        /*BugzID:451 - I am compensating for this bug because it is minor*/
        File bug451SavedCSV = new File(savedCSV.getAbsolutePath() + ".csv");
        assertTrue(areFilesSame(testCSV, bug451SavedCSV));
    }

    /**
     * Test saving a database to a CSV file.
     * @throws java.lang.Exception on any error
     */
    public void testLoadingCSV() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

        File testCSV = new File(root + "/ui/test.csv");
        assertTrue(testCSV.exists());

        // 1. Open and run script to populate database & create Spreadsheet
        // variable
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

        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                    window.getUIComponents(Spreadsheet.class)[0]
                    .getAwtComponent()));

        // 2. Clear spreadsheet by creating new database and make sure oldSS
        // still has old data
        WindowInterceptor
                .init(menuBar.getMenu("File").getSubMenu("New").triggerClick())
                .process(new WindowHandler() {
                    public Trigger process (Window newDBWindow) {
                        newDBWindow.getTextBox("nameField").setText("newDB");
                        return newDBWindow.getButton("Ok").triggerClick();
                    }
                 })
                 .run();

        // 2a. Check that all data is cleared
        Spreadsheet emptySS = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
        assertTrue(emptySS.getColumns().size() == 0);

        // 2b. Check that oldSS still has data
        assertTrue(ss.getColumns().size() > 0);

        // 3. Load CSV file
        WindowInterceptor
                .init(menuBar.getMenu("File").getSubMenu("Open...")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsOpenDialog()
                    .assertAcceptsFilesOnly()
                    .select(testCSV))
                .run();

        // 4. Check that Spreadsheet correctly loaded data
        // Please note: This assumes that saving was working on 05-Aug-2009
        // Also, this currently does a byte by byte check. This may not be
        // correct for all platforms and true parsing may need to be
        // implemented.
        Spreadsheet loadedSS = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
        //BugzID542: Isn't loading correctly
        assertTrue(ss == loadedSS);
    }


    /**
     * Checks if two text files are equal.
     * @param file1 First file
     * @param file2 Second file
     * @throws IOException on file read error
     * @return true if equal, else false
     */
    private Boolean areFilesSame(final File file1, final File file2)
    throws IOException {
        FileInputStream f1 = new FileInputStream(file1);
        FileInputStream f2 = new FileInputStream(file2);


        int b1, b2;
        while ((b1 = f1.read()) != -1) {
            if (((b2 = f2.read()) == -1) || (b2 != b1)) {
                return false;
            }
        }
        if ((b2 = f2.read()) != -1) {
            return false;
        }

        return true;

    }
}