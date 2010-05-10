package org.openshapa.uitests;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static org.fest.reflect.core.Reflection.method;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;

import org.openshapa.OpenSHAPA;

import org.openshapa.controllers.RunScriptC;
import org.openshapa.controllers.SaveC;
import org.openshapa.controllers.project.OpenSHAPAProjectRepresenter;
import org.openshapa.controllers.project.ProjectController;

import org.openshapa.models.db.LogicErrorException;
import org.openshapa.models.project.Project;

import org.openshapa.util.UIUtils;
import org.openshapa.util.FileFilters.CSVFilter;
import org.openshapa.util.FileFilters.MODBFilter;
import org.openshapa.util.FileFilters.SHAPAFilter;
import org.openshapa.util.FileFilters.OPFFilter;

import org.openshapa.views.NewProjectV;
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
 * Note: Loading is tested within the save tests.
 */
public final class UISaveLoadTest extends OpenSHAPATestClass {

    /**
     * Initialiser called before each unit test.
     */
    @AfterMethod @BeforeMethod protected void deleteFiles() {

        /*
         * Deleting these temp files before and after tests because Java does
         * not always delete them during the test case. Doing the deletes here
         * has resulted in consistent behaviour.
         */
        final String tempFolder = System.getProperty("java.io.tmpdir");

        // Delete temporary CSV and SHAPA files
        FilenameFilter ff = new FilenameFilter() {
                public boolean accept(final File dir, final String name) {
                    return (name.endsWith(".csv") || name.endsWith(".shapa")
                            || name.endsWith(".opf"));
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
        File demoFile = new File(root + "/ui/demo_data_to_csv2.rb");
        File toSave = null;
        Assert.assertTrue(demoFile.exists(),
            "Expecting demo_data_to_csv.rb to exist");

        if (Platform.isOSX()) {
            UIUtils.runScript(demoFile);
        } else {
            mainFrameFixture.clickMenuItemWithPath("Script", "Run script");

            JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
            jfcf.selectFile(demoFile).approve();
        }

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // Check that asterisk is present
        Assert.assertTrue(mainFrameFixture.getTitle().endsWith("*"));

        // 2. Save the file
        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);

            if (extension.equals("opf")) {
                fc.setFileFilter(new OPFFilter());
            } else if (extension.equals("csv")) {
                fc.setFileFilter(new CSVFilter());
            }

            toSave = new File(tempFolder + "/" + fileName);

            fc.setSelectedFile(toSave);

            method("save").withParameterTypes(OpenSHAPAFileChooser.class).in(
                OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save As...");

            if (extension.equals("opf")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    new OPFFilter());
            } else if (extension.equals("csv")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    new CSVFilter());
            }

            toSave = new File(tempFolder + "/" + fileName);
            mainFrameFixture.fileChooser().selectFile(toSave).approve();
        }

        String justSavedPath = tempFolder + "/" + fileName;

        if (!justSavedPath.endsWith(extension)) {
            justSavedPath = justSavedPath + "." + extension;
        }

        File justSaved = new File(justSavedPath);
        Assert.assertTrue(justSaved.exists(), "Expecting saved file to exist.");

        // 3. Check that the generated file is correct
        ProjectController pc = OpenSHAPA.getProjectController();
        File outputFile = null;
        File expectedOutputFile = null;

        if (extension.equals("csv")) {
            expectedOutputFile = new File(root + "/ui/demo_data_to_csv2.csv");
            loadFile(new File(pc.getProjectDirectory(),
                    pc.getDatabaseFileName()));
            outputFile = saveAsCSV(fileName + "new");
        } else if (extension.equals("opf")) {

            // Open the opf and save it as a csv
            // This will also test opf opening. Later this can be refactored to
            // its own test.
            loadFile(justSaved);
            outputFile = saveAsCSV(fileName);

            expectedOutputFile = new File(root + "/ui/demo_data_to_csv2.csv");
        }

        Assert.assertTrue(outputFile.exists(),
            "Expecting output CSV to exist.");
        Assert.assertTrue(expectedOutputFile.exists(),
            "Expecting reference output to exist.");

        Assert.assertTrue(UIUtils.areFilesSameLineComp(outputFile,
                expectedOutputFile), "Expecting files to be the same.");
    }

    /**
     * Saves current data as a CSV.
     * @param fileName csv file name
     * @return CSV file that was just saved.
     */
    private File saveAsCSV(String fileName) {
        final String tempFolder = System.getProperty("java.io.tmpdir");
        File toSave;
        String csvFileName = fileName + ".csv";

        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);
            fc.setFileFilter(new CSVFilter());

            toSave = new File(tempFolder + "/" + csvFileName);

            fc.setSelectedFile(toSave);

            method("save").withParameterTypes(OpenSHAPAFileChooser.class).in(
                OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save As...");

            mainFrameFixture.fileChooser().component().setFileFilter(
                new CSVFilter());

            toSave = new File(tempFolder + "/" + csvFileName);
            mainFrameFixture.fileChooser().selectFile(toSave).approve();
        }

        return toSave;
    }

    /**
     * Loads file after creating a new project.
     * @param file opf file to load
     */
    private void loadFile(File file) {

        // Create a new project, this is for the discard changes dialog.
        if (Platform.isOSX()) {
            mainFrameFixture.pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_N).modifiers(InputEvent.META_MASK));
        } else {
            mainFrameFixture.menuItemWithPath("File", "New").click();
        }

        try {
            JOptionPaneFixture warning = mainFrameFixture.optionPane();
            warning.requireTitle("Unsaved changes");
            warning.buttonWithText("OK").click();
        } catch (Exception e) {
            // Do nothing
        }

        // Get New Database dialog
        DialogFixture newDatabaseDialog = mainFrameFixture.dialog(
                new GenericTypeMatcher<JDialog>(JDialog.class) {
                    @Override protected boolean isMatching(
                        final JDialog dialog) {
                        return dialog.getClass().equals(NewProjectV.class);
                    }
                }, Timeout.timeout(10, TimeUnit.SECONDS));

        newDatabaseDialog.textBox("nameField").enterText("n");

        newDatabaseDialog.button("okButton").click();

        // Open file
        Assert.assertTrue(file.exists());

        // 1. Load  File
        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);

            String ext = file.getName().substring(file.getName().length() - 3,
                    file.getName().length());

            if (ext.equalsIgnoreCase("csv")) {
                fc.setFileFilter(new CSVFilter());
            } else if (ext.equalsIgnoreCase("opf")) {
                fc.setFileFilter(new OPFFilter());
            } else if (ext.equalsIgnoreCase("odb")) {
                fc.setFileFilter(new MODBFilter());
            } else {
                Assert.assertTrue(false, "Bad file extension");
            }

            fc.setSelectedFile(file);

            method("open").withParameterTypes(OpenSHAPAFileChooser.class).in(
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

            String ext = file.getName().substring(file.getName().length() - 3,
                    file.getName().length());

            if (ext.equalsIgnoreCase("csv")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    new CSVFilter());
            } else if (ext.equalsIgnoreCase("opf")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    new OPFFilter());
            } else if (ext.equalsIgnoreCase("odb")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    new MODBFilter());
            } else {
                Assert.assertTrue(false, "Bad file extension");
            }

            mainFrameFixture.fileChooser().selectFile(file).approve();
        }
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
        // Check that asterisk is present
        Assert.assertTrue(mainFrameFixture.getTitle().endsWith("*"));

        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);

            if (extension.equals("opf")) {
                fc.setFileFilter(new OPFFilter());
            } else if (extension.equals("csv")) {
                fc.setFileFilter(new CSVFilter());
            }

            File toSave = new File(tempFolder + "/" + fileName);

            fc.setSelectedFile(toSave);

            method("save").withParameterTypes(OpenSHAPAFileChooser.class).in(
                OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save");

            if (extension.equals("opf")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    new OPFFilter());
            } else if (extension.equals("csv")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    new CSVFilter());
            }

            File toSave = new File(tempFolder + "/" + fileName);
            mainFrameFixture.fileChooser().selectFile(toSave).approve();
        }

        // Check that no asterisk is present
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data_to_csv2.rb");
        Assert.assertTrue(demoFile.exists(),
            "Expecting demo_data_to_csv2.rb to exist");

        String justSavedPath = tempFolder + "/" + fileName;

        if (!justSavedPath.endsWith(extension)) {
            justSavedPath = justSavedPath + "." + extension;
        }

        File justSaved = new File(justSavedPath);
        Assert.assertTrue(justSaved.exists(), "Expecting saved file to exist.");

        // 2. Run script to populate database
        if (Platform.isOSX()) {
            UIUtils.runScript(demoFile);
        } else {
            mainFrameFixture.clickMenuItemWithPath("Script", "Run script");

            JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
            jfcf.selectFile(demoFile).approve();
        }

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        long currentTime = System.currentTimeMillis();
        long maxTime = currentTime + UIUtils.SCRIPT_LOAD_TIMEOUT; // timeout

        while ((System.currentTimeMillis() < maxTime)
                && (!scriptConsole.textBox().text().contains("Finished"))) {
            Thread.yield();
        }

        scriptConsole.button("closeButton").click();

        // Check the title - asterisk present
        Assert.assertTrue(mainFrameFixture.getTitle().endsWith("*"));

        // 3. Save project file. Not expecting anything except a save
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());
        spreadsheet.pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_S)
            .modifiers(Platform.controlOrCommandMask()));

        // Check the title - asterisk not present
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));

        // 4. Check that the generated CSV file is correct
        ProjectController pc = OpenSHAPA.getProjectController();
        File outputFile = null;
        File expectedOutputFile = null;

        if (extension.equals("csv")) {
            expectedOutputFile = new File(root + "/ui/demo_data_to_csv2.csv");
            loadFile(new File(pc.getProjectDirectory(),
                    pc.getDatabaseFileName()));
            outputFile = saveAsCSV(fileName + "new");

        } else if (extension.equals("opf")) {

            // Open the opf and save it as a csv
            // This will also test opf opening. Later this can be refactored to
            // its own test.
            loadFile(justSaved);
            outputFile = saveAsCSV(fileName);

            expectedOutputFile = new File(root + "/ui/demo_data_to_csv2.csv");
        }

        Assert.assertTrue(outputFile.exists(),
            "Expecting output CSV to exist.");
        Assert.assertTrue(expectedOutputFile.exists(),
            "Expecting reference output to exist.");

        Assert.assertTrue(UIUtils.areFilesSameLineComp(outputFile,
                expectedOutputFile), "Expecting files to be the same.");
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
        final String expectedOutputFile) throws IOException,
        LogicErrorException {
        final String root = System.getProperty("testPath") + "/ui/";
        final String tempFolder = System.getProperty("java.io.tmpdir");

        File testCSV = new File(tempFolder + inputFile);

        // Copy file to new project location
        UIUtils.copy(new File(root + inputFile), testCSV);

        Assert.assertTrue(testCSV.exists(), "Expecting input file to exist.");

        // 1. Make a new project, set it up
        Project loadedProject = new Project();
        loadedProject.setDatabaseFileName(inputFile);
        loadedProject.setProjectName("newSHAPA");

        // Check that title has asterisk
        Assert.assertTrue(mainFrameFixture.getTitle().endsWith("*"));

        // 2. Write the project out
        File newSHAPA = new File(tempFolder + "/newSHAPA.shapa");
        Assert.assertFalse(newSHAPA.exists());

        Dumper dumper = new Dumper(new OpenSHAPAProjectRepresenter(),
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

            method("open").withParameterTypes(OpenSHAPAFileChooser.class).in(
                OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Open...");

            // This will fail if a dialog is not shown.
            JOptionPaneFixture warning = mainFrameFixture.optionPane();
            warning.requireTitle("Unsaved changes");
            warning.buttonWithText("OK").click();

            mainFrameFixture.fileChooser().component().setFileFilter(
                new SHAPAFilter());
            mainFrameFixture.fileChooser().selectFile(newSHAPA).approve();
        }

        // Check that the title bar file name does not have an asterix
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));

        // Check that something has been loaded
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        Assert.assertNotNull(ssPanel.allColumns());

        // 4. Save the contents as a separate project file
        File savedSHAPA = new File(tempFolder + "/savedSHAPA.shapa");

        if (Platform.isOSX()) {
            SaveC saveController = new SaveC();

            saveController.saveProject(savedSHAPA,
                OpenSHAPA.getProjectController().getProject(),
                OpenSHAPA.getProjectController().getDB());
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save As...");
            mainFrameFixture.fileChooser().component().setFileFilter(
                new OPFFilter());
            mainFrameFixture.fileChooser().selectFile(savedSHAPA).approve();
        }

        // 5. Check that CSV file is correct
        File testOutputCSV = new File(root + expectedOutputFile);
        Assert.assertTrue(testOutputCSV.exists(),
            "Expected output reference file missing.");

        ProjectController pc = OpenSHAPA.getProjectController();
        File savedDB = new File(pc.getProjectDirectory(),
                pc.getDatabaseFileName());

        Assert.assertTrue(UIUtils.areFilesSameLineComp(testOutputCSV, savedDB),
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
    private void legacyFileLoadTest(final String inputFile,
        final String expectedOutputFile) throws IOException {
        String root = System.getProperty("testPath");

        File iFile = new File(root + "/ui/" + inputFile);
        File eoFile = new File(root + "/ui/" + expectedOutputFile);
        Assert.assertTrue(iFile.exists());

        // 1. Load ODB File
        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);
            if (inputFile.endsWith("odb")) {
                fc.setFileFilter(new MODBFilter());
            } else if (inputFile.endsWith("shapa")) {
                fc.setFileFilter(new SHAPAFilter());
            }
            fc.setSelectedFile(iFile);

            method("open").withParameterTypes(OpenSHAPAFileChooser.class).in(
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

            if (inputFile.endsWith("odb")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                new MODBFilter());
            } else if (inputFile.endsWith("shapa")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                new SHAPAFilter());
            }

            mainFrameFixture.fileChooser().selectFile(iFile).approve();
        }

        // Check that the title bar file name does not have an asterix
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));

        // Check that something has been loaded
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        Assert.assertNotNull(ssPanel.allColumns());

        //Save file as a CSV and compare to reference file
        File outputFile = saveAsCSV(expectedOutputFile + "new.csv");

        Assert.assertTrue(outputFile.exists(),
            "Expecting output CSV to exist.");
        Assert.assertTrue(eoFile.exists(),
            "Expecting reference output to exist.");

        Assert.assertTrue(UIUtils.areFilesSameLineComp(outputFile, eoFile),
                "Expecting files to be the same.");
    }

    /**
     * Test loading a database from a version 1 CSV file.
     * @throws java.lang.Exception
     *             on any error
     */
    /*BugzID1648:@Test public void testLoadingSHAPA1() throws Exception {
     *  System.err.println(new Exception().getStackTrace()[0].getMethodName());
     * loadTest("test-v1-in.csv", "test-v1-out.csv");}*/

    /**
     * Test loading a database from a version 2 CSV file.
     * @throws java.lang.Exception
     *             on any error
     */
    /*BugzID1648:@Test public void testLoadingSHAPA2() throws Exception {
     *  System.err.println(new Exception().getStackTrace()[0].getMethodName());
     * loadTest("test-v2-in.csv", "test-v2-out.csv");}*/

    /**
     * Test loading a database from version 3 CSV file.
     * @throws Exception on any error
     */
    /*BugzID1648:@Test public void testLoadingSHAPA3() throws Exception {
     *  System.err.println(new Exception().getStackTrace()[0].getMethodName());
     * loadTest("test-v3-in.csv", "test-v3-out.csv");}*/

    /**
     * Test loading an ODB file.
     * @throws Exception on any error.
     */
    @Test public void testLoadingODB1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        legacyFileLoadTest("macshapa-file.odb", "odfCSV.csv");
    }

     /**
     * Test loading an SHAPA file.
     * @throws Exception on any error.
     */
    @Test public void testLoadingSHAPA1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        legacyFileLoadTest("test.shapa", "modify-test-out.csv");
    }

    /**
     * Test saving a CSV database with Save As, no extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    @Test public void testSaveAsCSV1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveAsTest("savedCSV", "csv");
    }

    /**
     * Test saving a CSV database with Save As, extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    @Test public void testSaveAsCSV2() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveAsTest("savedCSV.csv", "csv");
    }

    /**
    * Test saving a SHAPA database with Save As, no extension in file name.
    * @throws java.lang.Exception
    *             on any error
    */
    @Test public void testSaveAsOPF1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // This will also test file loading
        saveAsTest("savedSHAPA.opf", "opf");
    }

    /**
     * Test saving a SHAPA database with Save As, no extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    @Test public void testSaveOPF1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // This will also test file loading
        saveTest("savedSHAPA.opf", "opf");
    }

    /**
     * Test saving a SHAPA database with Save As, extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    @Test public void testSaveAsOPF2() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // This will also test file loading
        saveAsTest("savedSHAPA.shapa", "opf");
    }

    /**
     * Test saving a SHAPA database with Save As, extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    @Test public void testSaveOPF2() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // This will also test file loading
        saveTest("savedSHAPA.shapa", "opf");
    }

    /**
     * Test saving a SHAPA database with Save As, wrong extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    @Test public void testSaveAsOPF3() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // This will also test file loading
        saveAsTest("savedSHAPA.csv", "opf");
    }

    /**
     * Test saving a SHAPA database with Save As, wrong extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    @Test public void testSaveOPF3() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // This will also test file loading
        saveTest("savedSHAPA.csv", "opf");
    }

    /**
     * Test saving a CSV database with Save As, wrong entension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    @Test public void testSaveAsCSV3() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveAsTest("savedCSV.shapa", "csv");
    }

    /*********************** SAVE TESTS ***************************/

    /**
     * Test saving a CSV database with Save, no extension in file name.
     * @throws java.lang.Exception
     *             on any error
     */
    @Test public void testSaveCSV1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveTest("savedCSV", "csv");
    }

    /**
    * Test saving a CSV database with Save, extension in file name.
    * @throws java.lang.Exception
    *             on any error
    */
    @Test public void testSaveCSV2() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveTest("savedCSV.csv", "csv");
    }

    /**
    * Test saving a CSV database with Save, wrong entension in file name.
    * @throws java.lang.Exception
    *             on any error
    */
    @Test public void testSaveCSV3() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        saveTest("savedCSV.shapa", "csv");
    }

    /**
     * Open a file and maybe check what open dialog opens to.
     *
     * @param openFile
     *            file to open
     * @param currDirectory
     *            currDirectory or null if not testing
     */
    private void fileLocationTest(final String openFile,
        final String currDirectory) {
        String root = System.getProperty("testPath") + "ui/";
        File openCSV = new File(root + openFile);
        Assert.assertTrue(openCSV.exists());

        // Open file if we're not checking anything, else check we're in the
        // right directory, by saving and checking file
        if (currDirectory == null) {
            mainFrameFixture.clickMenuItemWithPath("File", "Open...");

            try {
                JOptionPaneFixture warning = mainFrameFixture.optionPane();
                warning.requireTitle("Unsaved changes");
                warning.buttonWithText("OK").click();
            } catch (Exception e) {
                // Do nothing
            }

            JFileChooserFixture openDialog = mainFrameFixture.fileChooser();

            openDialog.selectFile(openCSV).approve();
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save As...");

            JFileChooserFixture saveDialog = mainFrameFixture.fileChooser();
            saveDialog.fileNameTextBox().enterText(currDirectory);
            saveDialog.approve();

            // This is the location check
            mainFrameFixture.optionPane().requireTitle("Confirm overwrite")
                .buttonWithText("Overwrite").click();

            // Open location2
            mainFrameFixture.clickMenuItemWithPath("File", "Open...");

            try {
                JOptionPaneFixture warning = mainFrameFixture.optionPane();
                warning.requireTitle("Unsaved changes");
                warning.buttonWithText("OK").click();
            } catch (Exception e) {
                // Do nothing
            }

            JFileChooserFixture openDialog = mainFrameFixture.fileChooser();
            openDialog.selectFile(openCSV).approve();
        }
    }

    /**
     * Bug 661 Test Make sure that the open dialog remembers previous open
     * location.
     * In the FEST port, this was changed to a generic file dialog location test
     */
    @Test public void testDialogLocation() {

        if (Platform.isOSX()) {
            return;
        }

        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // Delete confounding files from previous test
        String root = System.getProperty("testPath");
        File location1 = new File(root + "ui/location1/location2.opf");
        File location2 = new File(root + "ui/location2/location1.opf");
        location1.delete();
        location2.delete();
        Assert.assertFalse(location1.exists());
        Assert.assertFalse(location2.exists());

        fileLocationTest("location1/test.shapa", null);

        // At this point it should remember location1
        fileLocationTest("location2/location2.opf", "location1");

        // At this point it should remember location2
        fileLocationTest("location1/location1.opf", "location2");
    }

    /**
     * Test for saving into a directory where user does not have write 
     * permissions.
     */
    /*@Test*/ public void saveToDirectoryWithoutPermissions() {
        final String root = System.getProperty("testPath") + "/ui/";
        final String tempFolder = System.getProperty("java.io.tmpdir");

        //Check if directory exists and you do not have permission
        File noWrite = new File(root + "/noWrite/");
        Assert.assertTrue(noWrite.exists());
        Assert.assertTrue(noWrite.isDirectory());

        //Try to save to directory and confirm that warning dialog pops up
        // Check that asterisk is present
        Assert.assertTrue(mainFrameFixture.getTitle().endsWith("*"));

        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);

            File toSave = new File(noWrite.getAbsolutePath() + "/" + "test.opf");

            fc.setSelectedFile(toSave);

            method("save").withParameterTypes(OpenSHAPAFileChooser.class).in(
                OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save");

            File toSave = new File(noWrite.getAbsolutePath() + "/" + "test.opf");
            mainFrameFixture.fileChooser().selectFile(toSave).approve();
        }

        JOptionPaneFixture warning = mainFrameFixture.optionPane();
        warning.requireTitle("Warning:");
        warning.requireWarningMessage();
        warning.buttonWithText("OK").click();

        // Check that asterisk is present
        Assert.assertTrue(mainFrameFixture.getTitle().endsWith("*"));
    }
}
