package org.openshapa.uitests;


import java.io.File;
import org.uispec4j.interception.MainClassAdapter;
import org.openshapa.OpenSHAPA;
import org.openshapa.util.UiUtil;
import org.uispec4j.MenuBar;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.BasicHandler;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowInterceptor;

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
        UISpec4J.setWindowInterceptionTimeLimit(120000);
        UISpec4J.init();
    }

    /**
     * Test saving a database to a CSV file.
     *
     * @throws java.lang.Exception on any error
     */
    /*

    Comment test see BugzID:842 for details.

    public void testSavingCSV() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data_to_csv.rb");
        assertTrue(demoFile.exists());

        File testCSV = new File(root + "/ui/test-v2-out.csv");
        assertTrue(testCSV.exists());

        String tempFolder = System.getProperty("java.io.tmpdir");
        File savedCSV = new File(tempFolder + "/savedCSV.csv");
        savedCSV.deleteOnExit();
        if (savedCSV.exists()) {
            savedCSV.delete();
        }
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
        File bug541SavedCSV = new File(savedCSV.getAbsolutePath());
        assertTrue(UiUtil.areFilesSame(testCSV, bug541SavedCSV));
    }*/

    /**
     * Run a load test for specified input and expected output files.
     *
     * @param inputFile The input CSV file to open before saving.
     * @param expectedOutputFile The expected output of saving the above file.
     *
     * @throws Exception If unable to save file.
     */
    private void testLoad(final String inputFile,
                         final String expectedOutputFile) throws Exception {
        //Preparation
        Window window = getMainWindow();
        final MenuBar menuBar = window.getMenuBar();

        String root = System.getProperty("testPath");
        File testCSV = new File(root + inputFile);
        assertTrue(testCSV.exists());

        File testOutputCSV = new File(root + expectedOutputFile);
        assertTrue(testOutputCSV.exists());

        String tempFolder = System.getProperty("java.io.tmpdir");
        final File savedCSV = new File(tempFolder + "/savedCSV.csv");
        //savedCSV.deleteOnExit();
        // The file already exists - created in the last test.

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
        if (savedCSV.exists()) {
            WindowInterceptor
                .init(menuBar.getMenu("File").getSubMenu("Save As...").triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsSaveDialog()
                    .assertAcceptsFilesOnly()
                    .select(savedCSV))
                .process(BasicHandler.init().triggerButtonClick("Overwrite"))
                .run();
        } else {
            WindowInterceptor
                .init(menuBar.getMenu("File").getSubMenu("Save As...").triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsSaveDialog()
                    .assertAcceptsFilesOnly()
                    .select(savedCSV))
                .run();
        }

        // 3. Check that CSV file is correct
        assertTrue(UiUtil.areFilesSame(testOutputCSV, savedCSV));
        window.dispose();
    }

    /**
     * Test loading a database from a version 1 CSV file.
     *
     * @throws java.lang.Exception on any error
     */

    public void testLoadingCSVv1() throws Exception {
        // this.testLoad("/ui/test-v1-in.csv", "/ui/test-v1-out.csv");
    }

    /**
     * Test loading a database from a version 2 CSV file.
     *
     * @throws java.lang.Exception on any error
     */

    public void testLoadingCSVv2() throws Exception {
        // this.testLoad("/ui/test-v2-in.csv", "/ui/test-v2-out.csv");
    }

}
