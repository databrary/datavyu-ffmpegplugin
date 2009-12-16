package org.openshapa.uitests;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import org.openshapa.OpenSHAPA;
import org.openshapa.project.OpenSHAPAProjectRepresenter;
import org.openshapa.project.Project;
import org.openshapa.util.UIUtils;
import org.uispec4j.MenuBar;
import org.uispec4j.OpenSHAPAUISpecTestCase;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Test saving and loading a database to a SHAPA file.
 */
public final class UISaveLoadSHAPATest extends OpenSHAPAUISpecTestCase {

    /**
     * Initialiser called before each unit test.
     *
     * @throws java.lang.Exception When unable to initialise test
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        /* Deleting these temp files before and after tests because Java does
         * not always delete them during the test case. Doing the deletes here
         * has resulted in consistent behaviour.
         */
        final String tempFolder = System.getProperty("java.io.tmpdir");

        // Delete temporary CSV and SHAPA files
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

     /**
     * Called after each test.
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        /* Deleting these temp files before and after tests because Java does
         * not always delete them during the test case. Doing the deletes here
         * has resulted in consistent behaviour.
         */
        final String tempFolder = System.getProperty("java.io.tmpdir");

        // Delete temporary CSV and SHAPA files
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

        super.tearDown();
    }

    static {
        UISpec4J.setWindowInterceptionTimeLimit(120000);
        UISpec4J.init();
    }

    /**
     * Test saving a database to a SHAPA file with Save As.
     *
     * @throws java.lang.Exception on any error
     */
    public void testSaveAsSHAPA() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data_to_csv.rb");
        assertTrue(demoFile.exists());

        final String tempFolder = System.getProperty("java.io.tmpdir");

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

        // 2. Save SHAPA file
        File savedSHAPA = new File(tempFolder + "/savedSHAPA.shapa");
        if (savedSHAPA.exists()) {
            savedSHAPA.delete();
        }
        savedSHAPA.deleteOnExit();

        WindowInterceptor
                .init(menuBar.getMenu("File").getSubMenu("Save As...")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsSaveDialog()
                    .assertAcceptsFilesOnly()
                    .select(tempFolder + "/savedSHAPA.shapa"))
                .run();

        // 3. Check that the generated CSV file is correct
        Project project = OpenSHAPA.getProject();
        File outputCSV = new File(project.getDatabaseDir(), 
                project.getDatabaseFile());

        File expectedOutputCSV = new File(root + "/ui/test-v2-out.csv");
        assertTrue(expectedOutputCSV.exists());

        assertTrue(UIUtils.areFilesSame(outputCSV, expectedOutputCSV));
    }

    /**
     * Test saving a database to a SHAPA file with Save.
     *
     * @throws java.lang.Exception on any error
     */
    public void testSaveSHAPA() throws Exception {
        //TODO: Should be modified for other file types once they're ready
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        final String tempFolder = System.getProperty("java.io.tmpdir");

        final String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data_to_csv.rb");
        assertTrue(demoFile.exists());

        //1. Click save on empty project. Expecting it to act like Save As
        WindowInterceptor
                .init(menuBar.getMenu("File").getSubMenu("Save")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsSaveDialog()
                    .assertAcceptsFilesOnly()
                    .select(tempFolder + "/savedSHAPA.shapa"))
                .run();

        // 2. Open and run script to populate database
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

        // 2. Save project file. Not expecting anything except a save
        menuBar.getMenu("File").getSubMenu("Save").click();

        // 3. Check that the saved database file is correct
        Project project = OpenSHAPA.getProject();
        File savedDB = new File(project.getDatabaseDir(), project.getDatabaseFile());

        File testCSV = new File(root + "/ui/test-v2-out.csv");
        assertTrue(testCSV.exists());

        assertTrue(UIUtils.areFilesSame(testCSV, savedDB));
    }

    /**
     * Run a load test for specified input and expected output files.
     *
     * @param inputFile The input CSV file to open before saving.
     * @param expectedOutputFile The expected output of saving the above file.
     *
     * @throws Exception If unable to save file.
     */
    private void loadTest(final String inputFile,
                          final String expectedOutputFile) throws Exception {
        //Preparation
        Window window = getMainWindow();
        final MenuBar menuBar = window.getMenuBar();

        String root = System.getProperty("testPath");

        File testCSV = new File(root + inputFile);
        assertTrue(testCSV.exists());

        String tempFolder = System.getProperty("java.io.tmpdir");

        // 1. Make a new project, set it up
        Project loadedProject = new Project();
        loadedProject.setDatabaseDir(root);
        loadedProject.setDatabaseFile(root + inputFile);
        loadedProject.setProjectName("newSHAPA");

        // 2. Write the project out
        File newSHAPA = new File(tempFolder + "/newSHAPA.shapa");
        newSHAPA.deleteOnExit();

        Dumper dumper = new Dumper(new OpenSHAPAProjectRepresenter(),
                new DumperOptions());
        Yaml yaml = new Yaml(dumper);
        FileWriter fileWriter = new FileWriter(newSHAPA);
        yaml.dump(loadedProject, fileWriter);
        fileWriter.close();

        // 3. Now load the newly created project in openshapa
        WindowInterceptor
            .init(menuBar.getMenu("File").getSubMenu("Open...").triggerClick())
            .process(FileChooserHandler.init()
                .assertIsOpenDialog()
                .assertAcceptsFilesOnly()
                .select(newSHAPA))
            .run();

        // 4. Save the contents as a separate project file
        File savedSHAPA = new File(tempFolder + "/savedSHAPA.shapa");
        savedSHAPA.deleteOnExit();

        WindowInterceptor
            .init(menuBar.getMenu("File").getSubMenu("Save As...")
            .triggerClick())
            .process(FileChooserHandler.init()
                .assertIsSaveDialog()
                .assertAcceptsFilesOnly()
                .select(savedSHAPA))
            .run();

        

        // 5. Check that CSV file is correct
        File testOutputCSV = new File(root + expectedOutputFile);
        assertTrue(testOutputCSV.exists());

        Project project = OpenSHAPA.getProject();
        File savedDB = new File(project.getDatabaseDir(),
                                project.getDatabaseFile());

        assertTrue(UIUtils.areFilesSame(testOutputCSV, savedDB));
    }

    /**
     * Test loading a database from a version 1 CSV file.
     *
     * @throws java.lang.Exception on any error
     */
    public void testLoadingSHAPA1() throws Exception {
        this.loadTest("/ui/test-v1-in.csv", "/ui/test-v1-out.csv");
   }

    /**
     * Test loading a database from a version 2 CSV file.
     *
     * @throws java.lang.Exception on any error
     */
    public void testLoadingSHAPA2() throws Exception {
        this.loadTest("/ui/test-v2-in.csv", "/ui/test-v2-out.csv");
    }

}
