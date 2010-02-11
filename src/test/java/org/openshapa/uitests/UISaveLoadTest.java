package org.openshapa.uitests;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.project.OpenSHAPAProjectRepresenter;
import org.openshapa.models.project.Project;
import org.openshapa.util.UIUtils;
import org.openshapa.util.FileFilters.CSVFilter;
import org.openshapa.util.FileFilters.SHAPAFilter;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Test saving and loading a database file.
 */
public final class UISaveLoadTest extends OpenSHAPATestClass {

    /**
     * Initialiser called before each unit test.
     * 
     * @throws java.lang.Exception
     *             When unable to initialise test
     */
    @AfterMethod
    @BeforeMethod
    protected void deleteFiles() throws Exception {
        /*
         * Deleting these temp files before and after tests because Java does
         * not always delete them during the test case. Doing the deletes here
         * has resulted in consistent behaviour.
         */
        final String tempFolder = System.getProperty("java.io.tmpdir");

        // Delete temporary CSV and SHAPA files
        FilenameFilter ff = new FilenameFilter() {
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
     * Test saving a database with Save As.
     * 
     * @param fileName
     *            fileName to save as
     * @param extension
     *            extension to save as
     * @throws IOException
     *             on any error
     */
    private void saveAsTest(final String fileName, final String extension)
            throws IOException {
        final String tempFolder = System.getProperty("java.io.tmpdir");

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data_to_csv.rb");
        Assert.assertTrue(demoFile.exists(),
                "Expecting demo_data_to_csv.rb to exist");

        // 1. Run script to populate database
        mainFrameFixture.menuItemWithPath("Script", "Run script").click();

        JFileChooserFixture scriptFileChooser = mainFrameFixture.fileChooser();
        scriptFileChooser.selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // TODO Need to check the window title - asterisk present

        // 2. Save the file
        mainFrameFixture.menuItemWithPath("File", "Save As...").click();
        if (extension.equals("shapa")) {
            mainFrameFixture.fileChooser().component().setFileFilter(
                    new SHAPAFilter());
        } else if (extension.equals("csv")) {
            mainFrameFixture.fileChooser().component().setFileFilter(
                    new CSVFilter());
        }
        File toSave = new File(tempFolder + "/" + fileName);
        mainFrameFixture.fileChooser().selectFile(toSave).approve();

        String justSavedPath = tempFolder + "/" + fileName;
        if (!justSavedPath.endsWith(extension)) {
            justSavedPath = justSavedPath + "." + extension;
        }

        File justSaved = new File(justSavedPath);
        Assert.assertTrue(justSaved.exists(), "Expecting saved file to exist.");

        // 3. Check that the generated CSV file is correct
        Project project = OpenSHAPA.getProject();
        File outputCSV =
                new File(project.getDatabaseDir(), project.getDatabaseFile());

        File expectedOutputCSV = new File(root + "/ui/test-v2-out.csv");
        Assert.assertTrue(expectedOutputCSV.exists(), "");

        Assert.assertTrue(UIUtils.areFilesSame(outputCSV, expectedOutputCSV),
                "Expecting CSV files to be the same.");
    }

    /**
     * Test saving a database to a file with Save.
     * 
     * @param fileName
     *            file name to save
     * @param extension
     *            extension to save
     * @throws IOException
     *             on any error
     */
    private void saveTest(final String fileName, final String extension)
            throws IOException {
        final String tempFolder = System.getProperty("java.io.tmpdir");

        // 1. Click save on empty project. Expecting it to act like Save As
        mainFrameFixture.menuItemWithPath("File", "Save").click();

        if (extension.equals("shapa")) {
            mainFrameFixture.fileChooser().component().setFileFilter(
                    new SHAPAFilter());
        } else if (extension.equals("csv")) {
            mainFrameFixture.fileChooser().component().setFileFilter(
                    new CSVFilter());
        }
        File toSave = new File(tempFolder + "/" + fileName);
        mainFrameFixture.fileChooser().selectFile(toSave).approve();

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data_to_csv.rb");
        Assert.assertTrue(demoFile.exists(),
                "Expecting demo_data_to_csv.rb to exist");

        String justSavedPath = tempFolder + "/" + fileName;
        if (!justSavedPath.endsWith(extension)) {
            justSavedPath = justSavedPath + "." + extension;
        }

        File justSaved = new File(justSavedPath);
        Assert.assertTrue(justSaved.exists(), "Expecting saved file to exist.");

        // 2. Run script to populate database
        mainFrameFixture.menuItemWithPath("Script", "Run script").click();

        JFileChooserFixture scriptFileChooser = mainFrameFixture.fileChooser();
        scriptFileChooser.selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // TODO Need to check the title - asterisk present

        // 3. Save project file. Not expecting anything except a save
        mainFrameFixture.menuItemWithPath("File", "Save").click();

        // TODO Need to check the title - asterisk not present

        // 4. Check that the generated CSV file is correct
        Project project = OpenSHAPA.getProject();
        File outputCSV =
                new File(project.getDatabaseDir(), project.getDatabaseFile());

        File expectedOutputCSV = new File(root + "/ui/test-v2-out.csv");
        Assert.assertTrue(expectedOutputCSV.exists(), "");

        Assert.assertTrue(UIUtils.areFilesSame(outputCSV, expectedOutputCSV),
                "Expecting CSV files to be the same.");
    }

    /**
     * Run a load test for specified input and expected output files.
     * 
     * @param inputFile
     *            The input CSV file to open before saving.
     * @param expectedOutputFile
     *            The expected output of saving the above file.
     * @throws Exception
     *             If unable to save file.
     */
    private void loadTest(final String inputFile,
            final String expectedOutputFile) throws IOException {
        final String root = System.getProperty("testPath");
        final String tempFolder = System.getProperty("java.io.tmpdir");

        File testCSV = new File(root + inputFile);
        Assert.assertTrue(testCSV.exists(), "Expecting input file to e");

        // 1. Make a new project, set it up
        Project loadedProject = new Project();
        loadedProject.setDatabaseDir(root);
        loadedProject.setDatabaseFile(root + inputFile);
        loadedProject.setProjectName("newSHAPA");

        // 2. Write the project out
        File newSHAPA = new File(tempFolder + "/newSHAPA.shapa");

        Dumper dumper =
                new Dumper(new OpenSHAPAProjectRepresenter(),
                        new DumperOptions());
        Yaml yaml = new Yaml(dumper);
        FileWriter fileWriter = new FileWriter(newSHAPA);
        yaml.dump(loadedProject, fileWriter);
        fileWriter.close();

        // 3. Now load the newly created project in openshapa
        mainFrameFixture.menuItemWithPath("File", "Open...").click();

        try {
            JOptionPaneFixture warning = mainFrameFixture.optionPane();
            warning.requireTitle("Unsaved changes");
            warning.buttonWithText("OK").click();
        } catch (Exception e) {
            // Do nothing
        }

        mainFrameFixture.fileChooser().component().setFileFilter(
                new SHAPAFilter());
        mainFrameFixture.fileChooser().selectFile(newSHAPA).approve();

        // 4. Save the contents as a separate project file
        File savedSHAPA = new File(tempFolder + "/savedSHAPA.shapa");

        mainFrameFixture.menuItemWithPath("File", "Save As...").click();
        mainFrameFixture.fileChooser().component().setFileFilter(
                new SHAPAFilter());
        mainFrameFixture.fileChooser().selectFile(savedSHAPA).approve();

        // 5. Check that CSV file is correct
        File testOutputCSV = new File(root + expectedOutputFile);
        Assert.assertTrue(testOutputCSV.exists(),
                "Expected output reference file missing.");

        Project project = OpenSHAPA.getProject();
        File savedDB =
                new File(project.getDatabaseDir(), project.getDatabaseFile());

        Assert.assertTrue(UIUtils.areFilesSame(testOutputCSV, savedDB),
                "Expecting CSV files to be the same.");
    }

    /**
     * Test loading a database from a version 1 CSV file.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testLoadingSHAPA1() throws Exception {
        loadTest("/ui/test-v1-in.csv", "/ui/test-v1-out.csv");
    }

    /**
     * Test loading a database from a version 2 CSV file.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testLoadingSHAPA2() throws Exception {
        loadTest("/ui/test-v2-in.csv", "/ui/test-v2-out.csv");
    }

    /**
     * Test saving a SHAPA database with Save As, no extension in file name.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testSaveAsSHAPA1() throws Exception {
        saveAsTest("savedSHAPA", "shapa");
    }

    /**
     * Test saving a CSV database with Save As, no extension in file name.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testSaveAsCSV1() throws Exception {
        saveAsTest("savedCSV", "csv");
    }

    /**
     * Test saving a SHAPA database with Save As, extension in file name.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testSaveAsSHAPA2() throws Exception {
        saveAsTest("savedSHAPA.shapa", "shapa");
    }

    /**
     * Test saving a CSV database with Save As, extension in file name.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testSaveAsCSV2() throws Exception {
        saveAsTest("savedCSV.csv", "csv");
    }

    /**
     * Test saving a SHAPA database with Save As, wrong extension in file name.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testSaveAsSHAPA3() throws Exception {
        saveAsTest("savedSHAPA.csv", "shapa");
    }

    /**
     * Test saving a CSV database with Save As, wrong entension in file name.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testSaveAsCSV3() throws Exception {
        saveAsTest("savedCSV.shapa", "csv");
    }

    /*********************** SAVE TESTS ***************************/
    /**
     * Test saving a SHAPA database with Save, no extension in file name.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testSaveSHAPA1() throws Exception {
        saveTest("savedSHAPA", "shapa");
    }

    /**
     * Test saving a CSV database with Save, no extension in file name.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testSaveCSV1() throws Exception {
        saveTest("savedCSV", "csv");
    }

    /**
     * Test saving a SHAPA database with Save, extension in file name.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testSaveSHAPA2() throws Exception {
        saveTest("savedSHAPA.shapa", "shapa");
    }

    /**
     * Test saving a CSV database with Save, extension in file name.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testSaveCSV2() throws Exception {
        saveTest("savedCSV.csv", "csv");
    }

    /**
     * Test saving a SHAPA database with Save, wrong extension in file name.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testSaveSHAPA3() throws Exception {
        saveTest("savedSHAPA.csv", "shapa");
    }

    /**
     * Test saving a CSV database with Save, wrong entension in file name.
     * 
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testSaveCSV3() throws Exception {
        saveTest("savedCSV.shapa", "csv");
    }
}
