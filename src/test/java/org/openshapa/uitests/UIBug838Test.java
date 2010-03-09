package org.openshapa.uitests;

import java.awt.event.KeyEvent;
import static org.fest.reflect.core.Reflection.method;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.util.Platform;
import org.openshapa.OpenSHAPA;
import org.openshapa.controllers.RunScriptC;
import org.openshapa.controllers.SaveC;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.project.OpenSHAPAProjectRepresenter;
import org.openshapa.models.project.Project;
import org.openshapa.util.UIUtils;
import org.openshapa.util.FileFilters.CSVFilter;
import org.openshapa.util.FileFilters.SHAPAFilter;
import org.openshapa.views.OpenSHAPAFileChooser;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Bug838: File extension not shown in spreadsheet header when
 * using apple+s to save to disk
 */
public final class UIBug838Test extends OpenSHAPATestClass {

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

        if (Platform.isOSX()) {
            //Can't really test this properly on OSX.
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
            mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_S,
                    Platform.controlOrCommandMask());
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

        //Check that title has extension
        Assert.assertTrue(mainFrameFixture.component()
                .getTitle().endsWith("." + extension) ||
                mainFrameFixture.component()
                .getTitle().endsWith("." + extension + "*"));

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

        // TODO Need to check the title - asterisk present

        // 3. Save project file. Not expecting anything except a save
        mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_S,
                    Platform.controlOrCommandKey());

        //Check that title has extension
        Assert.assertTrue(mainFrameFixture.component()
                .getTitle().endsWith("." + extension) ||
                mainFrameFixture.component()
                .getTitle().endsWith("." + extension + "*"));

        // 4. Check that the generated CSV file is correct
        Project project = OpenSHAPA.getProject();
        File outputCSV =
                new File(project.getDatabaseDir(), project.getDatabaseFile());
        Assert.assertTrue(outputCSV.exists(), "Expecting output CSV to exist.");

        File expectedOutputCSV = new File(root + "/ui/test-v2-out.csv");
        Assert.assertTrue(expectedOutputCSV.exists(),
                "Expecting reference output CSV to exist.");

        Assert.assertTrue(UIUtils.areFilesSame(outputCSV, expectedOutputCSV),
                "Expecting CSV files to be the same.");
    }

    
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
