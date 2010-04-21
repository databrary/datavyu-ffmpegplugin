package org.openshapa.uitests;

import static org.fest.reflect.core.Reflection.method;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.util.Platform;

import org.openshapa.OpenSHAPA;

import org.openshapa.controllers.RunScriptC;

import org.openshapa.util.UIUtils;
import org.openshapa.util.FileFilters.CSVFilter;

import org.openshapa.views.OpenSHAPAFileChooser;
import org.openshapa.views.discrete.SpreadsheetPanel;

import org.testng.Assert;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * Test the creation of a new database.
 */
public final class UIRunModifyDatabaseScriptTest extends OpenSHAPATestClass {

    /**
     * Initialiser called before each unit test.
     *
     * @throws java.lang.Exception
     *             When unable to initialise test
     */
    @AfterMethod @BeforeMethod protected void deleteFiles() throws Exception {

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
                            || name.endsWith("opf"));
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
     * Tests modifiying the spreadsheet with a script.
     *
     * @throws IOException
     *             if file read issues.
     */
    /*@Test*/ public void testModifySpreadsheet() throws IOException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Open and run script to populate database
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists(),
            "Expecting demo_data.rb to exist.");

        final File modifyFile = new File(root + "/ui/find_and_replace.rb");
        Assert.assertTrue(modifyFile.exists(),
            "Expecting find_and_replace.rb to exist.");

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

        while ((System.currentTimeMillis() < maxTime) &&
                (!scriptConsole.textBox().text().contains("Finished"))) {
            Thread.yield();
        }
        scriptConsole.button("closeButton").click();

        // 1a. Check that database is populated
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());
        Assert.assertTrue(spreadsheet.numOfColumns() > 0,
            "Expecting spreadsheet to be populated.");

        /*
         * 2. Perform a find and replace; replace all instances of "moo" with
         * "frog"
         */
        if (Platform.isOSX()) {
            UIUtils.runScript(modifyFile);
        } else {
            mainFrameFixture.clickMenuItemWithPath("Script", "Run script");

            JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
            jfcf.selectFile(modifyFile).approve();
        }

        // Close script console
        scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // 3. Save the database
        final String tempFolder = System.getProperty("java.io.tmpdir");
        File savedCSV = new File(tempFolder + "/" + "savedCSV.csv");

        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);
            fc.setFileFilter(new CSVFilter());
            fc.setSelectedFile(savedCSV);

            method("save").withParameterTypes(OpenSHAPAFileChooser.class).in(
                OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save As...");
            mainFrameFixture.fileChooser().component().setFileFilter(
                new CSVFilter());
            mainFrameFixture.fileChooser().selectFile(savedCSV).approve();
        }

        // 4. - compare it to the reference .csv
        File testCSV = new File(root + "/ui/modify-test-out.csv");
        Assert.assertTrue(UIUtils.areFilesSameLineComp(testCSV, savedCSV));
    }

}
