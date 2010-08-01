package org.openshapa.uitests;

import static org.fest.reflect.core.Reflection.method;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.swing.JMenuItem;

import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.util.Platform;

import org.openshapa.OpenSHAPA;

import org.openshapa.util.UIUtils;
import org.openshapa.util.FileFilters.CSVFilter;

import org.openshapa.views.OpenSHAPAFileChooser;

import org.testng.Assert;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * Test the creation of a new database.
 */
public final class UIScriptsTest extends OpenSHAPATestClass {

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
     * Tests modifying the spreadsheet with a script.
     *
     * @throws IOException
     *             if file read issues.
     */
    @Test public void testModifySpreadsheet() throws IOException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Open and run script to populate database        
        final File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists(),
            "Expecting demo_data.rb to exist.");

        final File modifyFile = new File(testFolder + "/ui/find_and_replace.rb");
        Assert.assertTrue(modifyFile.exists(),
            "Expecting find_and_replace.rb to exist.");

        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 1a. Check that database is populated
        spreadsheet = mainFrameFixture.getSpreadsheet();
        Assert.assertTrue(spreadsheet.numOfColumns() > 0,
            "Expecting spreadsheet to be populated.");

        /*
         * 2. Perform a find and replace; replace all instances of "moo" with
         * "frog"
         */
        mainFrameFixture.runScript(modifyFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 3. Save the database
        
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
        File testCSV = new File(testFolder + "/ui/modify-test-out.csv");
        Assert.assertTrue(UIUtils.areFilesSameLineComp(testCSV, savedCSV));
    }

    /**
     * Test for BugzID:1770: Recent scripts contains duplicates
     * Run OpenSHAPA, run the demodata script.
     * Now go to script > Run Recent Script and click the demodata script in
     * here again. Now if you open the recent scripts list, demodata will appear
     * twice (and in fact, can appear arbitrarily many times upon reselection,
     * i.e. duplicates are not being checked).
     */
    @Test public void testDuplicateRecentScripts() {
        final File script1 = new File(testFolder + "/ui/script1.rb");
        Assert.assertTrue(script1.exists(),
            "Expecting script1.rb to exist.");

        final File script2 = new File(testFolder + "/ui/script2.rb");
        Assert.assertTrue(script2.exists(),
            "Expecting script2.rb to exist.");

        //Run script
        mainFrameFixture.runScript(script1);
        mainFrameFixture.closeScriptConsoleOnFinish();

        //Check recent scripts
        JMenuItemFixture recentScriptMenu = mainFrameFixture.menuItemWithPath("Script", "Run recent script").click();
        JPopupMenu recentScripts = (JPopupMenu)recentScriptMenu.component().getSubElements()[0];

        Assert.assertTrue(recentScripts.getSubElements().length > 1);
        int count1 = 0;
        for (MenuElement me : recentScripts.getSubElements()) {
            if (((JMenuItem)me).getText().endsWith(script1.getName())) {
                count1++;
            }
        }
        Assert.assertEquals(count1, 1);
        
        //Run script again
        mainFrameFixture.runScript(script1);
        mainFrameFixture.closeScriptConsoleOnFinish();

        //Check recent scripts
        recentScriptMenu = mainFrameFixture.menuItemWithPath("Script", "Run recent script").click();
        recentScripts = (JPopupMenu)recentScriptMenu.component().getSubElements()[0];

        Assert.assertTrue(recentScripts.getSubElements().length > 1);
        count1 = 0;
        for (MenuElement me : recentScripts.getSubElements()) {
            if (((JMenuItem)me).getText().endsWith(script1.getName())) {
                count1++;
            }
        }
        Assert.assertEquals(count1, 1);
        
        //Run different script
        mainFrameFixture.runScript(script2);
        mainFrameFixture.closeScriptConsoleOnFinish();

        //Check recent scripts
        recentScriptMenu = mainFrameFixture.menuItemWithPath("Script", "Run recent script").click();
        recentScripts = (JPopupMenu)recentScriptMenu.component().getSubElements()[0];

        Assert.assertTrue(recentScripts.getSubElements().length > 2);
        count1 = 0;
        int count2 = 0;
        for (MenuElement me : recentScripts.getSubElements()) {
            if (((JMenuItem)me).getText().endsWith(script1.getName())) {
                count1++;
            }

            if (((JMenuItem)me).getText().endsWith(script2.getName())) {
                count2++;
            }
        }
        Assert.assertEquals(count1, 1);
        Assert.assertEquals(count2, 1);

        //Click spreadsheet to unfocus from menu
        mainFrameFixture.getSpreadsheet().click();
    }

}
