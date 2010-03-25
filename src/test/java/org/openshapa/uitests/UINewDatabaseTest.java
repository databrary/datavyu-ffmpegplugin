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
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.fixture.VocabEditorDialogFixture;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;
import org.openshapa.controllers.RunScriptC;
import org.openshapa.util.UIUtils;
import org.openshapa.views.ListVariables;
import org.openshapa.views.NewProjectV;
import org.openshapa.views.VocabEditorV;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the creation of a new database. This is now synonomous with the creation
 * of a New Project.
 */
public final class UINewDatabaseTest extends OpenSHAPATestClass {
    /**
     * Test new spreadsheet.
     */
    //@Test
    public void testNewSpreadsheet() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
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

        // 2. Check that the database is populated
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);

        SpreadsheetPanelFixture ssPanel =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        Vector<SpreadsheetColumnFixture> cols = ssPanel.allColumns();
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

        try {
            JOptionPaneFixture warning = mainFrameFixture.optionPane();
            warning.requireTitle("Unsaved changes");
            warning.buttonWithText("OK").click();
        } catch (Exception e) {
            // Do nothing
        }

        // DialogFixture newDatabaseDialog = mainFrameFixture.dialog();

        // Get New Database dialog
        DialogFixture newDatabaseDialog =
                mainFrameFixture.dialog(new GenericTypeMatcher<JDialog>(
                        JDialog.class) {
                    @Override
                    protected boolean isMatching(final JDialog dialog) {
                        return dialog.getClass().equals(NewProjectV.class);
                    }
                }, Timeout.timeout(5, TimeUnit.SECONDS));

        newDatabaseDialog.textBox("nameField").enterText("n");

        newDatabaseDialog.button("okButton").click();

        // 4a. Check that all data is cleared
        Assert.assertTrue(ssPanel.numOfColumns() == 0);

        // 4b. Check that variable list is empty
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Variable List");
        // Get VariableList dialog
        DialogFixture varListDialog =
                mainFrameFixture.dialog(new GenericTypeMatcher<JDialog>(
                        JDialog.class) {
                    @Override
                    protected boolean isMatching(final JDialog dialog) {
                        return dialog.getClass().equals(ListVariables.class);
                    }
                }, Timeout.timeout(5, TimeUnit.SECONDS));

        varListDialog.table().requireRowCount(0);
        varListDialog.close();

        // 4c. Check that vocab editor is empty
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");
        VocabEditorDialogFixture veDialog =
                new VocabEditorDialogFixture(mainFrameFixture.robot,
                        (VocabEditorV) mainFrameFixture.dialog().component());
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.close();
    }

    /**
     * Should display warning if database has no name.
     */
    // BugzID:938 - Expecting the new database dialog to remain open.
    // Must write test to check that this is true.
    // //@Test
    // public void testBug576() {
    // System.err.println("testBug576");
    // String root = System.getProperty("testPath");
    // File demoFile = new File(root + "/ui/demo_data.rb");
    // Assert.assertTrue(demoFile.exists());
    //
    // //1. Run script to populate
    // mainFrameFixture.clickMenuItemWithPath("Script", "Run script");
    //
    // JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
    // jfcf.selectFile(demoFile).approve();
    //
    // //Close script console
    // DialogFixture scriptConsole = mainFrameFixture.dialog();
    // scriptConsole.button("closeButton").click();
    //
    // //2. Check that the database is populated
    // JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
    //
    // SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
    // mainFrameFixture.robot, (SpreadsheetPanel)jPanel.component());
    //
    // Vector<SpreadsheetColumnFixture> cols = ssPanel.allColumns();
    // Assert.assertTrue(cols.size() != 0);
    // for (SpreadsheetColumnFixture col : cols) {
    // Assert.assertTrue(col.numOfCells() != 0);
    // }
    //
    // //Record number of columns
    // int numOfCols = cols.size();
    //
    // //3. Create a new database (and discard unsaved changes)
    // if (Platform.isOSX()) {
    // mainFrameFixture.pressAndReleaseKey(KeyPressInfo.keyCode(
    // KeyEvent.VK_N).modifiers(KeyEvent.META_MASK));
    // } else {
    // mainFrameFixture.clickMenuItemWithPath("File", "New");
    // }
    //
    // try {
    // JOptionPaneFixture warning = mainFrameFixture.optionPane();
    // warning.requireTitle("Unsaved changes");
    // warning.buttonWithText("OK").click();
    // } catch (Exception e) {
    // // Do nothing
    // }
    //
    // // Get New Database dialog and click "OK" without entering a name
    // DialogFixture newDatabaseDialog = mainFrameFixture.dialog(
    // new GenericTypeMatcher<JDialog>(JDialog.class) {
    // @Override
    // protected boolean isMatching(JDialog dialog) {
    // return dialog.getClass().equals(NewProjectV.class);
    // }
    // }, Timeout.timeout(5, TimeUnit.SECONDS));
    //
    // newDatabaseDialog.button("okButton").click();
    //
    // JOptionPaneFixture noNameWarning = mainFrameFixture.optionPane();
    // noNameWarning.requireTitle("Warning:");
    // noNameWarning.buttonWithText("OK").click();
    //
    // //4a. Check that all data remains the same
    // JPanelFixture jPanel2 = UIUtils.getSpreadsheet(mainFrameFixture);
    //
    // SpreadsheetPanelFixture ssPanel2 = new SpreadsheetPanelFixture(
    // mainFrameFixture.robot, (SpreadsheetPanel)jPanel.component());
    //
    // Vector<SpreadsheetColumnFixture> cols2 = ssPanel.allColumns();
    //
    // Assert.assertTrue(cols2.size() == numOfCols);
    // }
}
