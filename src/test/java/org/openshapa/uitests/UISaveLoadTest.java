/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.uitests;

import java.awt.event.KeyEvent;

import static org.fest.reflect.core.Reflection.method;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.Vector;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.DataControllerFixture;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.util.Platform;

import org.openshapa.OpenSHAPA;

import org.openshapa.controllers.SaveC;
import org.openshapa.controllers.project.OpenSHAPAProjectRepresenter;
import org.openshapa.controllers.project.ProjectController;

import org.openshapa.models.db.UserWarningException;
import org.openshapa.models.project.Project;

import org.openshapa.util.UIUtils;
import org.openshapa.util.FileFilters.CSVFilter;
import org.openshapa.util.FileFilters.OPFFilter;
import org.openshapa.util.UIFileUtils;

import org.openshapa.views.DataControllerV;
import org.openshapa.views.OpenSHAPAFileChooser;

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

        File demoFile = new File(testFolder + "/ui/demo_data_to_csv2.rb");
        File toSave = new File(tempFolder + "/" + fileName);
        ;
        Assert.assertTrue(demoFile.exists(),
            "Expecting demo_data_to_csv.rb to exist");

        mainFrameFixture.runScript(demoFile);
        mainFrameFixture.closeScriptConsoleOnFinish();

        // Check that asterisk is present
        Assert.assertTrue(mainFrameFixture.getTitle().endsWith("*"));

        //Not using a function, because we're using the short cut here.
        // 2. Save the file
        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);

            if (extension.equals("opf")) {
                fc.setFileFilter(OPFFilter.INSTANCE);
            } else if (extension.equals("csv")) {
                fc.setFileFilter(CSVFilter.INSTANCE);
            }

            fc.setSelectedFile(toSave);

            method("save").withParameterTypes(OpenSHAPAFileChooser.class).in(
                OpenSHAPA.getView()).invoke(fc);
        } else {

            // mainFrameFixture.clickMenuItemWithPath("File", "Save As...");
            // Use shortcut instead. Menu item is tested elsewhere
            spreadsheet = mainFrameFixture.getSpreadsheet();
            spreadsheet.pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_S)
                .modifiers(Platform.controlOrCommandMask(), KeyEvent.VK_SHIFT));

            if (extension.equals("opf")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    OPFFilter.INSTANCE);
            } else if (extension.equals("csv")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    CSVFilter.INSTANCE);
            }

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
            expectedOutputFile = new File(testFolder
                    + "/ui/demo_data_to_csv2.csv");
            UIFileUtils.loadFile(mainFrameFixture,
                new File(pc.getProjectDirectory(), pc.getDatabaseFileName()));
            outputFile = saveAsCSV(fileName + "new");
        } else if (extension.equals("opf")) {

            // Open the opf and save it as a csv
            // This will also test opf opening. Later this can be refactored to
            // its own test.
            UIFileUtils.loadFile(mainFrameFixture, justSaved);
            outputFile = saveAsCSV(fileName);

            expectedOutputFile = new File(testFolder
                    + "/ui/demo_data_to_csv2.csv");
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
    private File saveAsCSV(final String fileName) {

        File toSave;
        String csvFileName = fileName + ".csv";

        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);
            fc.setFileFilter(CSVFilter.INSTANCE);

            toSave = new File(tempFolder + "/" + csvFileName);

            fc.setSelectedFile(toSave);

            method("save").withParameterTypes(OpenSHAPAFileChooser.class).in(
                OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save As...");

            mainFrameFixture.fileChooser().component().setFileFilter(
                CSVFilter.INSTANCE);

            toSave = new File(tempFolder + "/" + csvFileName);
            mainFrameFixture.fileChooser().selectFile(toSave).approve();
        }

        return toSave;
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

        File toSave = new File(tempFolder + "/" + fileName + "." + extension);

        // 1. Click save on empty project. Expecting it to act like Save As
        Assert.assertFalse(toSave.exists());

        UIFileUtils.saveFile(mainFrameFixture, toSave);

        // Check that no asterisk is present
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));


        File demoFile = new File(testFolder + "/ui/demo_data_to_csv2.rb");
        Assert.assertTrue(demoFile.exists(),
            "Expecting demo_data_to_csv2.rb to exist");

        Assert.assertTrue(toSave.exists(), "Expecting saved file to exist.");

        // 2. Run script to populate database
        mainFrameFixture.runScript(demoFile);

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
        spreadsheet = mainFrameFixture.getSpreadsheet();
        spreadsheet.pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_S)
            .modifiers(Platform.controlOrCommandMask()));

        // Check the title - asterisk not present
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));

        // 4. Check that the generated CSV file is correct
        ProjectController pc = OpenSHAPA.getProjectController();
        File outputFile = null;
        File expectedOutputFile = null;

        if (extension.equals("csv")) {
            expectedOutputFile = new File(testFolder
                    + "/ui/demo_data_to_csv2.csv");
            UIFileUtils.loadFile(mainFrameFixture,
                new File(pc.getProjectDirectory(), pc.getDatabaseFileName()));
            outputFile = saveAsCSV(fileName + "new");

        } else if (extension.equals("opf")) {

            // Open the opf and save it as a csv
            // This will also test opf opening. Later this can be refactored to
            // its own test.
            UIFileUtils.loadFile(mainFrameFixture, toSave);
            outputFile = saveAsCSV(fileName);

            expectedOutputFile = new File(testFolder
                    + "/ui/demo_data_to_csv2.csv");
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
     * @throws LogicalErrorException on SaveC exception
     */
    private void loadTest(final String inputFile,
                          final String expectedOutputFile)
    throws IOException, UserWarningException {
        final String root = testFolder + "/ui/";


        File testCSV = new File(tempFolder + inputFile);

        // Copy file to new project location
        UIUtils.copy(new File(testFolder + inputFile), testCSV);

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
        UIFileUtils.loadFile(mainFrameFixture, newSHAPA);

        // Check that the title bar file name does not have an asterisk
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));

        // Check that something has been loaded
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Assert.assertTrue(spreadsheet.allColumns().size() > 0);

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
                OPFFilter.INSTANCE);
            mainFrameFixture.fileChooser().selectFile(savedSHAPA).approve();
        }

        // 5. Check that CSV file is correct
        File testOutputCSV = new File(testFolder + expectedOutputFile);
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


        File iFile = new File(testFolder + "/ui/" + inputFile);
        File eoFile = new File(testFolder + "/ui/" + expectedOutputFile);
        Assert.assertTrue(iFile.exists());

        // 1. Load ODB File
        UIFileUtils.loadFile(mainFrameFixture, iFile);

        // Check that the title bar file name does not have an asterix
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));

        // Check that something has been loaded
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Assert.assertTrue(spreadsheet.allColumns().size() > 0);

        // Save file as a CSV and compare to reference file
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
     * Test ODB to OPF conversion.
     * Note: Uses an oracle ODB CSV, rather than actual CSV save, which differs
     * slightly (does not affect function)
     * @throws Exception on any error.
     */
    @Test public void testODBtoOPF() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String inputFile = "macshapa-file.odb";
        String odbOutputCSV = "odbCSV.csv";
        String opfOutputCSV = "opfCSV.csv";

        File iFile = new File(testFolder + "/ui/" + inputFile);
        File odbCSV = new File(testFolder + "/ui/" + odbOutputCSV);
        Assert.assertTrue(iFile.exists());

        // 1. Load ODB File
        UIFileUtils.loadFile(mainFrameFixture, iFile);

        // Check that the title bar file name does not have an asterix
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));

        // Check that something has been loaded
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Assert.assertTrue(spreadsheet.allColumns().size() > 0);

        // Save file as a OPF
        File toSave = new File(tempFolder + "/odbOPF.opf");

        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);

            fc.setSelectedFile(toSave);

            method("save").withParameterTypes(OpenSHAPAFileChooser.class).in(
                OpenSHAPA.getView()).invoke(fc);
        } else {

            mainFrameFixture.clickMenuItemWithPath("File", "Save As...");

            mainFrameFixture.fileChooser().selectFile(toSave).approve();
        }

        // Close and reopen OPF
        UIFileUtils.loadFile(mainFrameFixture, toSave);

        // Save as CSV file
        File opfCSV = saveAsCSV("opf");

        // Compare CSV files
        Assert.assertTrue(odbCSV.exists(), "Expecting odb CSV to exist.");
        Assert.assertTrue(opfCSV.exists(),
            "Expecting reference output to exist.");

        Assert.assertTrue(UIUtils.areFilesSameLineComp(odbCSV, opfCSV),
            "Expecting files to be the same.");
    }

    /**
     * Test loading the old OPF format
     * @throws Exception on any error.
     */
    /*BugzID:1941@Test*/ public void testLoadOPFv1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String inputFilename = "oldProjectFormat.opf";
        File inputFile = new File(testFolder + "/ui/" + inputFilename);
        Assert.assertTrue(inputFile.exists());

        //Load file
        UIFileUtils.loadFile(mainFrameFixture, inputFile);

        // Check that the title bar file name does not have an asterix
        Assert.assertFalse(mainFrameFixture.getTitle().endsWith("*"));

        // Check that something has been loaded
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Assert.assertTrue(spreadsheet.allColumns().size() > 0);
    }

    /**
     * Test loading an ODB file.
     * @throws Exception on any error.
     */
    @Test public void testLoadingODB1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        legacyFileLoadTest("macshapa-file.odb", "odbCSV.csv");
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
        String root = testFolder + "ui/";
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

        File location1 = new File(testFolder + "ui/location1/location2.opf");
        File location2 = new File(testFolder + "ui/location2/location1.opf");
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
    // Need to figure out to how to set readonly permissions on file across
    // operating systems.
    @Test public void saveWithoutPermissions() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        final String root = testFolder + "/ui/";


        // Check if file exists and you do not have permission
        File noWrite = new File(root + "/noWrite/test.opf");
        noWrite.setReadOnly();
        Assert.assertTrue(noWrite.exists());
        Assert.assertTrue(noWrite.isFile());
        Assert.assertFalse(noWrite.canWrite());

        // Try to save to directory and confirm that warning dialog pops up

        if (Platform.isOSX()) {
            return;
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save");

            File toSave = new File(noWrite.getAbsolutePath() + "/"
                    + "test.opf");
            mainFrameFixture.fileChooser().selectFile(toSave).approve();
        }

        // Should try to overwrite
        mainFrameFixture.optionPane().requireTitle("Confirm overwrite")
            .buttonWithText("Overwrite").click();

        // Test for this error message
        JOptionPaneFixture warning = mainFrameFixture.optionPane();
        warning.requireTitle("Warning:");
        warning.requireWarningMessage();
        warning.buttonWithText("OK").click();
    }

    /**
     *  Having \ as the last character in a text field breaks the file.
     */
    @Test public void testBug1568() throws IOException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());


        File toSave = null;

        // Create new text variable
        mainFrameFixture.createNewVariable("t", "text");

        // Create new cells
        spreadsheet = mainFrameFixture.getSpreadsheet();

        spreadsheet.column("t").click();

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        spreadsheet.column("t").click();

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        // Check that asterisk is present
        Assert.assertTrue(mainFrameFixture.getTitle().endsWith("*"));

        // Add text to cells
        Vector<SpreadsheetCellFixture> cells = spreadsheet.column(0).allCells();

        spreadsheet.column(0).cell(1).cellValue().enterText("this ends in \\");
        spreadsheet.column(0).cell(1).cellValue().requireText(
            "this ends in \\");

        spreadsheet.column(0).cell(2).cellValue().enterText(
            "this also ends in \\");
        spreadsheet.column(0).cell(2).cellValue().requireText(
            "this also ends in \\");


        // 2. Save the file
        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);

            fc.setFileFilter(OPFFilter.INSTANCE);

            toSave = new File(tempFolder + "/slashFile.opf");

            fc.setSelectedFile(toSave);

            method("save").withParameterTypes(OpenSHAPAFileChooser.class).in(
                OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save As...");

            mainFrameFixture.fileChooser().component().setFileFilter(
                OPFFilter.INSTANCE);

            toSave = new File(tempFolder + "/slashFile.opf");
            mainFrameFixture.fileChooser().selectFile(toSave).approve();
        }

        String justSavedPath = tempFolder + "/slashFile.opf";

        File justSaved = new File(justSavedPath);
        Assert.assertTrue(justSaved.exists(), "Expecting saved file to exist.");

        // 3. Check that the generated file is correct
        UIFileUtils.loadFile(mainFrameFixture, toSave);

        // Check loaded project
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Vector<SpreadsheetCellFixture> cs = spreadsheet.column(0).allCells();

        spreadsheet.column(0).cell(1).cellValue().requireText(
            "this ends in \\");

        spreadsheet.column(0).cell(2).cellValue().requireText(
            "this also ends in \\");
    }

    /**
     * Bug 1694 - Test handling of missing video file.
     * @throws IOException on file reading error
     */
    @Test public void testMissingVideoFiles() throws IOException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String root = testFolder + "/ui/";
        File missingVidFile = new File(root + "/missingVideo.opf");
        Assert.assertTrue(missingVidFile.exists(),
            "Expecting demo_data_to_csv.rb to exist");

        // 1. Load  File
        if (Platform.isOSX()) {
            return;
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
                OPFFilter.INSTANCE);

            mainFrameFixture.fileChooser().selectFile(missingVidFile).approve();
        }

        // Expecting warning
        mainFrameFixture.optionPane().requireTitle("Missing files")
            .buttonWithText("OK").click();

        // Check that data controller is opened
        final DataControllerFixture dcf = new DataControllerFixture(
                mainFrameFixture.robot,
                (DataControllerV) mainFrameFixture.dialog().component());
    }
}
