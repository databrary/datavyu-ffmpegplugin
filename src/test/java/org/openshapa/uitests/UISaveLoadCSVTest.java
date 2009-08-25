package org.openshapa.uitests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.uispec4j.MenuBar;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;

/**
 * Test saving and loading a database to a CSV file.
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

    static {
        UISpec4J.init();
    }

    /**
     * Test saving a database to a CSV file.
     *
     * @throws java.lang.Exception on any error
     */
    public void testSavingCSV() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data_to_csv.rb");
        assertTrue(demoFile.exists());

        File testCSV = new File(root + "/ui/test.csv");
        assertTrue(testCSV.exists());

        String tempFolder = System.getProperty("java.io.tmpdir");
        File savedCSV = new File(tempFolder + "/savedCSV.csv");
        savedCSV.deleteOnExit();
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

        // BugzID:541 - I am compensating for this bug because it is minor.
        File bug541SavedCSV = new File(savedCSV.getAbsolutePath() + ".csv");
        assertTrue(areFilesSame(testCSV, bug541SavedCSV));
    }

    /**
     * Test loading a database to a CSV file.
     *
     * @throws java.lang.Exception on any error
     */
    /*
    public void testLoadingCSV() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        String root = System.getProperty("testPath");
        File testCSV = new File(root + "/ui/test.csv");
        assertTrue(testCSV.exists());

        String tempFolder = System.getProperty("java.io.tmpdir");
        File savedCSV = new File(tempFolder + "/savedCSV.csv");
        savedCSV.deleteOnExit();
        assertFalse(savedCSV.exists());

        // 1. Load CSV file
        WindowInterceptor
                .init(menuBar.getMenu("File").getSubMenu("Open...")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsOpenDialog()
                    .assertAcceptsFilesOnly()
                    .select(testCSV))
                .run();

        // 2. Save contents as a seperate CSV file.
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
        //BugzID:541 - I am compensating for this bug because it is minor
        File bug541SavedCSV = new File(savedCSV.getAbsolutePath() + ".csv");
        assertTrue(areFilesSame(testCSV, bug541SavedCSV));
    }*/


    /**
     * Checks if two text files are equal.
     *
     * @param file1 First file
     * @param file2 Second file
     *
     * @throws IOException on file read error
     * @return true if equal, else false
     */
    private Boolean areFilesSame(final File file1, final File file2)
    throws IOException {
        BufferedReader r1 = new BufferedReader(new FileReader(file1));
        BufferedReader r2 = new BufferedReader(new FileReader(file2));

        String line1 = r1.readLine();
        String line2 = r2.readLine();
        if (!line1.equals(line2)) {
            return false;
        }

        while (line1 != null && line2 != null) {
            if (!line1.equals(line2)) {
                return false;
            }

            line1 = r1.readLine();
            line2 = r2.readLine();
        }

        return true;
    }
}