package org.openshapa.uitests;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.openshapa.util.UIUtils;
import org.openshapa.util.FileFilters.CSVFilter;
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
     * Tests modifiying the spreadsheet with a script.
     * 
     * @throws IOException
     *             if file read issues.
     */
    @Test
    public void testModifySpreadsheet() throws IOException {
        // 1. Open and run script to populate database
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/demo_data.rb");
        Assert
                .assertTrue(demoFile.exists(),
                        "Expecting demo_data.rb to exist.");
        final File modifyFile = new File(root + "/ui/find_and_replace.rb");
        Assert.assertTrue(modifyFile.exists(),
                "Expecting find_and_replace.rb to exist.");

        mainFrameFixture.clickMenuItemWithPath("Script", "Run script");
        mainFrameFixture.fileChooser().selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // 1a. Check that database is populated
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());
        Assert.assertTrue(spreadsheet.numOfColumns() > 0,
                "Expecting spreadsheet to be populated.");

        /*
         * 2. Perform a find and replace; replace all instances of "moo" with
         * "frog"
         */
        mainFrameFixture.clickMenuItemWithPath("Script", "Run script");
        mainFrameFixture.fileChooser().selectFile(modifyFile).approve();

        // Close script console
        scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // 3. Save the database
        final String tempFolder = System.getProperty("java.io.tmpdir");

        mainFrameFixture.clickMenuItemWithPath("File", "Save As...");
        mainFrameFixture.fileChooser().component().setFileFilter(
                new CSVFilter());
        File savedCSV = new File(tempFolder + "/" + "savedCSV.csv");
        mainFrameFixture.fileChooser().selectFile(savedCSV).approve();

        // 4. - compare it to the reference .csv
        File testCSV = new File(root + "/ui/modify-test-out.csv");
        Assert.assertTrue(UIUtils.areFilesSame(testCSV, savedCSV));
    }

}
