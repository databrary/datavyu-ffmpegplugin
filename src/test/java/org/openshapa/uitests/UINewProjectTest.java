package org.openshapa.uitests;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.File;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.VocabEditorDialogFixture;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;

import org.openshapa.views.ListVariables;
import org.openshapa.views.NewProjectV;
import org.openshapa.views.VocabEditorV;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test the creation of a new database. This is now synonomous with the creation
 * of a New Project.
 */
public final class UINewProjectTest extends OpenSHAPATestClass {

    /**
     * Test new spreadsheet.
     */
    @Test public void testNewSpreadsheet() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsole();

        // 2. Check that the database is populated
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Vector<SpreadsheetColumnFixture> cols = spreadsheet.allColumns();
        Assert.assertTrue(cols.size() != 0);

        for (SpreadsheetColumnFixture col : cols) {
            Assert.assertTrue(col.numOfCells() != 0);
        }

        // 3. Create a new database (and discard unsaved changes)
        if (Platform.isOSX()) {
            mainFrameFixture.pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_N).modifiers(InputEvent.META_MASK));
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "New");
        }

        JOptionPaneFixture warning = mainFrameFixture.optionPane();
        warning.requireTitle("Unsaved changes");
        warning.buttonWithText("OK").click();

        DialogFixture newProjectDialog = mainFrameFixture.dialog("NewProjectV");

        newProjectDialog.textBox("nameField").enterText("n");

        newProjectDialog.button("okButton").click();

        // 4a. Check that all data is cleared
        Assert.assertTrue(spreadsheet.numOfColumns() == 0);

        // 4b. Check that variable list is empty
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Variable List");

        // Get VariableList dialog
        DialogFixture varListDialog = mainFrameFixture.dialog(
                new GenericTypeMatcher<JDialog>(JDialog.class) {
                    @Override protected boolean isMatching(
                        final JDialog dialog) {
                        return dialog.getClass().equals(ListVariables.class);
                    }
                }, Timeout.timeout(5, TimeUnit.SECONDS));

        varListDialog.table().requireRowCount(0);
        varListDialog.close();

        // 4c. Check that vocab editor is empty
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        VocabEditorDialogFixture veDialog = new VocabEditorDialogFixture(
                mainFrameFixture.robot,
                (VocabEditorV) mainFrameFixture.dialog().component());
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.close();
    }

    /**
     * Should display warning if database has no name and database window should
     * remain open.
     */
    @Test public void testBug938() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsole();

        // 2. Sequentially select each column and delete
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Vector<SpreadsheetColumnFixture> cols = spreadsheet.allColumns();
        Assert.assertTrue(cols.size() != 0);

        for (SpreadsheetColumnFixture col : cols) {
            Assert.assertTrue(col.numOfCells() != 0);
        }

        // Record number of columns
        int numOfCols = cols.size();

        // Create a new database (and discard unsaved changes)
        if (Platform.isOSX()) {
            mainFrameFixture.pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_N).modifiers(KeyEvent.META_MASK));
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "New");
        }

        JOptionPaneFixture warning = mainFrameFixture.optionPane();
        warning.requireTitle("Unsaved changes");
        warning.buttonWithText("OK").click();


        // Get New Project dialog and click "OK" without entering a name
        DialogFixture newProjectDialog = mainFrameFixture.dialog("NewProjectV");

        newProjectDialog.button("okButton").click();

        JOptionPaneFixture noNameWarning = mainFrameFixture.optionPane();
        noNameWarning.requireTitle("Warning:");
        noNameWarning.requireWarningMessage();
        noNameWarning.buttonWithText("OK").click();

        // Check that window remains open
        newProjectDialog.requireVisible();

        // Close window
        newProjectDialog.button("cancelButton").click();

        // 4a. Check that all data remains the same
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Vector<SpreadsheetColumnFixture> cols2 = spreadsheet.allColumns();

        Assert.assertTrue(cols2.size() == numOfCols);

        if (Platform.isOSX()) {
            mainFrameFixture.pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_N).modifiers(KeyEvent.META_MASK));
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "New");
        }

        warning = mainFrameFixture.optionPane();
        warning.requireTitle("Unsaved changes");
        warning.buttonWithText("OK").click();

        DialogFixture df = mainFrameFixture.dialog();

        df.textBox("nameField").enterText("n");

        df.button("okButton").click();
    }
}
