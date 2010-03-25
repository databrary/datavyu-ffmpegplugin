package org.openshapa.uitests;

import static org.fest.reflect.core.Reflection.method;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.util.Platform;
import org.openshapa.OpenSHAPA;
import org.openshapa.controllers.RunScriptC;
import org.openshapa.controllers.SaveC;
import org.openshapa.controllers.project.OpenSHAPAProjectRepresenter;
import org.openshapa.controllers.project.ProjectController;
import org.openshapa.models.db.LogicErrorException;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.project.Project;
import org.openshapa.util.UIUtils;
import org.openshapa.util.FileFilters.CSVFilter;
import org.openshapa.util.FileFilters.MODBFilter;
import org.openshapa.util.FileFilters.SHAPAFilter;
import org.openshapa.views.OpenSHAPAFileChooser;
import org.openshapa.views.discrete.SpreadsheetPanel;
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
     */
    @AfterMethod
    @BeforeMethod
    protected void deleteFiles() {
        /*
         * Deleting these temp files before and after tests because Java does
         * not always delete them during the test case. Doing the deletes here
         * has resulted in consistent behaviour.
         */
        final String tempFolder = System.getProperty("java.io.tmpdir");

        // Delete temporary CSV and SHAPA files
        FilenameFilter ff = new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
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

        if (Platform.isOSX()) {
            new RunScriptC(demoFile.toString());
        } else {
            mainFrameFixture.clickMenuItemWithPath("Script", "Run script");

            JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
            jfcf.selectFile(demoFile).approve();
        }

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        //Check that asterisk is present
        Assert.assertTrue(mainFrameFixture.getTitle().endsWith("*"));

        // 2. Save the file
        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);
            if (extension.equals("shapa")) {
                fc.setFileFilter(new SHAPAFilter());
            } else if (extension.equals("csv")) {
                fc.setFileFilter(new CSVFilter());
            }
            File toSave = new File(tempFolder + "/" + fileName);

            fc.setSelectedFile(toSave);

            method("save").withParameterTypes(OpenSHAPAFileChooser.class).in(
                    OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save As...");
            if (extension.equals("shapa")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                        new SHAPAFilter());
            } else if (extension.equals("csv")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                        new CSVFilter());
            }
            File toSave = new File(tempFolder + "/" + fileName);
            mainFrameFixture.fileChooser().selectFile(toSave).approve();
        }

        String justSavedPath = tempFolder + "/" + fileName;
        if (!justSavedPath.endsWith(extension)) {
            justSavedPath = justSavedPath + "." + extension;
        }

        File justSaved = new File(justSavedPath);
        Assert.assertTrue(justSaved.exists(), "Expecting saved file to exist.");

        // 3. Check that the generated CSV file is correct
        ProjectController pc = OpenSHAPA.getProjectController();
        File outputCSV =
                new File(pc.getProjectDirectory(), pc.getDatabaseFileName());
        Assert.assertTrue(outputCSV.exists(), "Expecting output CSV to exist.");

        File expectedOutputCSV = new File(root + "/ui/test-v2-out.csv");
        Assert.assertTrue(expectedOutputCSV.exists(),
                "Expecting reference output CSV to exist.");

        Assert.assertTrue(UIUtils.areFilesSame(outputCSV, expectedOutputCSV),
                "Expecting CSV files to be the same.");
    }

    /**
     * Test saving a database to a file with Save.
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
        //Check that asterisk is present
        Assert.assertTrue(mainFrameFixture.getTitle().endsWith("*"));

        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);
            if (extension.equals("shapa")) {
                fc.setFileFilter(new SHAPAFilter());
            } else if (extension.equals("csv")) {
                fc.setFileFilter(new CSVFilter());
            }
            File toSave = new File(tempFolder + "/" + fileName);

            fc.setSelectedFile(toSave);

            method("save").withParameterTypes(OpenSHAPAFileChooser.class).in(
                    OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save");
            if (extension.equals("shapa")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                        new SHAPAFilter());
            } else if (extension.equals("csv")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                        new CSVFilter());
            }
            File toSave = new File(tempFolder + "/" + fileName);
            mainFrameFixture.fileChooser().selectFile(toSave).approve();
        }

        //Check that no asterisk is present
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));

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
        if (Platform.isOSX()) {
            new RunScriptC(demoFile.toString());
        } else {
            mainFrameFixture.clickMenuItemWithPath("Script", "Run script");

            JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
            jfcf.selectFile(demoFile).approve();
        }

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // Check the title - asterisk present
        Assert.assertTrue(mainFrameFixture.getTitle().endsWith("*"));

        // 3. Save project file. Not expecting anything except a save
        mainFrameFixture.clickMenuItemWithPath("File", "Save");

        // Check the title - asterisk not present
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));

        // 4. Check that the generated CSV file is correct
        ProjectController pc = OpenSHAPA.getProjectController();
        File outputCSV =
                new File(pc.getProjectDirectory(), pc.getDatabaseFileName());
        Assert.assertTrue(outputCSV.exists(), "Expecting output CSV to exist.");

        File expectedOutputCSV = new File(root + "/ui/test-v2-out.csv");
        Assert.assertTrue(expectedOutputCSV.exists(),
                "Expecting reference output CSV to exist.");

        Assert.assertTrue(UIUtils.areFilesSame(outputCSV, expectedOutputCSV),
                "Expecting CSV files to be the same.");
    }

    /**
     * Run a load test for specified input and expected output files.
     * @param inputFile
     *            The input CSV file to open before saving.
     * @param expectedOutputFile
     *            The expected output of saving the above file.
     * @throws IOException
     *             If unable to save file.
     * @throws SystemErrorException
     */
    private void loadTest(final String inputFile,
                          final String expectedOutputFile)
    throws IOException, LogicErrorException {
        final String root = System.getProperty("testPath") + "/ui/";
        final String tempFolder = System.getProperty("java.io.tmpdir");

        File testCSV = new File(tempFolder + inputFile);
        //Copy file to new project location
        UIUtils.copy(new File(root + inputFile), testCSV);

        Assert.assertTrue(testCSV.exists(), "Expecting input file to exist.");

        // 1. Make a new project, set it up
        Project loadedProject = new Project();
        loadedProject.setDatabaseFileName(inputFile);
        loadedProject.setProjectName("newSHAPA");

        //Check that title has asterisk
        Assert.assertTrue(mainFrameFixture.getTitle().endsWith("*"));

        // 2. Write the project out
        File newSHAPA = new File(tempFolder + "/newSHAPA.shapa");
        Assert.assertFalse(newSHAPA.exists());

        Dumper dumper =
                new Dumper(new OpenSHAPAProjectRepresenter(),
                        new DumperOptions());
        Yaml yaml = new Yaml(dumper);
        FileWriter fileWriter = new FileWriter(newSHAPA);
        yaml.dump(loadedProject, fileWriter);
        fileWriter.close();

        // 3. Now load the newly created project in openshapa
        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);
            fc.setFileFilter(new SHAPAFilter());
            fc.setSelectedFile(newSHAPA);

            method("open")
                    .withParameterTypes(OpenSHAPAFileChooser.class).in(
                            OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Open...");

            //This will fail if a dialog is not shown.
            JOptionPaneFixture warning = mainFrameFixture.optionPane();
            warning.requireTitle("Unsaved changes");
            warning.buttonWithText("OK").click();

            mainFrameFixture.fileChooser().component().setFileFilter(
                    new SHAPAFilter());
            mainFrameFixture.fileChooser().selectFile(newSHAPA).approve();
        }

        //Check that the title bar file name does not have an asterix
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));

        //Check that something has been loaded
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        Assert.assertNotNull(ssPanel.allColumns());

        // 4. Save the contents as a separate project file
        File savedSHAPA = new File(tempFolder + "/savedSHAPA.shapa");

        if (Platform.isOSX()) {
            SaveC saveController = new SaveC();

            saveController.saveProject(tempFolder,
                                       "savedSHAPA.shapa",
                                       OpenSHAPA.getProjectController().getProject(),
                                       OpenSHAPA.getProjectController().getDB());
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save As...");
            mainFrameFixture.fileChooser().component().setFileFilter(
                    new SHAPAFilter());
            mainFrameFixture.fileChooser().selectFile(savedSHAPA).approve();
        }

        // 5. Check that CSV file is correct
        File testOutputCSV = new File(root + expectedOutputFile);
        Assert.assertTrue(testOutputCSV.exists(),
                "Expected output reference file missing.");

        ProjectController pc = OpenSHAPA.getProjectController();
        File savedDB =
                new File(pc.getProjectDirectory(), pc.getDatabaseFileName());

        Assert.assertTrue(UIUtils.areFilesSame(testOutputCSV, savedDB),
                "Expecting CSV files to be the same.");
    }

    /**
     * Run a load test for odb files.
     *
     * @param inputFile
     *            The input CSV file to open before saving.
     * @param expectedOutputFile
     *            The expected output of saving the above file.
     * @throws IOException
     *             If unable to save file.
     * @throws SystemErrorException
     */
    private void loadODBTest(final String inputFile,
            final String expectedOutputFile) throws IOException {
        //THIS TEST NEEDS TO BE MADE MORE RIGOROUS. RIGHT
        //NOW IT JUST OPENS A FILE AND CHECKS THERE IS SOME DATA

        String root = System.getProperty("testPath");

        File odbFile = new File(root + "/ui/" + inputFile);
        Assert.assertTrue(odbFile.exists());
        // 1. Load ODB File
        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);
            fc.setFileFilter(new MODBFilter());
            fc.setSelectedFile(odbFile);

            method("open")
                    .withParameterTypes(OpenSHAPAFileChooser.class).in(
                            OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Open...");

            try {
                JOptionPaneFixture warning = mainFrameFixture.optionPane();
                warning.requireTitle("Unsaved changes");
                warning.buttonWithText("OK").click();
            } catch (Exception e) {
                // Do nothing
            }

            mainFrameFixture.fileChooser().component().setFileFilter(
                    new MODBFilter());
            mainFrameFixture.fileChooser().selectFile(odbFile).approve();
        }

        //Check that the title bar file name does not have an asterix
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));

        //Check that something has been loaded
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        Assert.assertNotNull(ssPanel.allColumns());
    }

    /**
     * Test loading a database from a version 1 CSV file.
     * @throws java.lang.Exception
     *             on any error
     */
    //BugzID:1568//@Test
    public void testLoadingSHAPA1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        loadTest("test-v1-in.csv", "test-v1-out.csv");
    }

    /**
     * Test loading a database from a version 2 CSV file.
     * @throws java.lang.Exception
     *             on any error
     */
    //BugzID:1568//@Test
    public void testLoadingSHAPA2() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        loadTest("test-v2-in.csv", "test-v2-out.csv");
    }

    /**
     * Test loading a database from version 3 CSV file.
     * @throws Exception on any error
     */
    //@Test
    public void testLoadingSHAPA3() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        loadTest("test-v3-in.csv", "test-v3-out.csv");
    }

    /**
     * Test loading an ODB file.
     * @throws Exception on any error.
     */
    //@Test
    public void testLoadingODB1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        loadODBTest("macshapa-file.odb", null);
    }

    /**
     * Test saving a SHAPA database with Save As, no extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    //@Test
    public void testSaveAsSHAPA1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveAsTest("savedSHAPA", "shapa");
    }

    /**
     * Test saving a CSV database with Save As, no extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    //@Test
    public void testSaveAsCSV1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveAsTest("savedCSV", "csv");
    }

    /**
     * Test saving a SHAPA database with Save As, extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    //@Test
    public void testSaveAsSHAPA2() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveAsTest("savedSHAPA.shapa", "shapa");
    }

    /**
     * Test saving a CSV database with Save As, extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    //@Test
    public void testSaveAsCSV2() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveAsTest("savedCSV.csv", "csv");
    }

    /**
     * Test saving a SHAPA database with Save As, wrong extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    //@Test
    public void testSaveAsSHAPA3() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveAsTest("savedSHAPA.csv", "shapa");
    }

    /**
     * Test saving a CSV database with Save As, wrong entension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    //@Test
    public void testSaveAsCSV3() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveAsTest("savedCSV.shapa", "csv");
    }

    /*********************** SAVE TESTS ***************************/
    /**
     * Test saving a SHAPA database with Save, no extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    //@Test
    public void testSaveSHAPA1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveTest("savedSHAPA", "shapa");
    }

    /**
     * Test saving a CSV database with Save, no extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    //@Test
    public void testSaveCSV1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveTest("savedCSV", "csv");
    }

    /**
     * Test saving a SHAPA database with Save, extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    //@Test
    public void testSaveSHAPA2() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveTest("savedSHAPA.shapa", "shapa");
    }

    /**
     * Test saving a CSV database with Save, extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    //@Test
    public void testSaveCSV2() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveTest("savedCSV.csv", "csv");
    }

    /**
     * Test saving a SHAPA database with Save, wrong extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    //@Test
    public void testSaveSHAPA3() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveTest("savedSHAPA.csv", "shapa");
    }

    /**
     * Test saving a CSV database with Save, wrong entension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    //@Test
    public void testSaveCSV3() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveTest("savedCSV.shapa", "csv");
    }
}
