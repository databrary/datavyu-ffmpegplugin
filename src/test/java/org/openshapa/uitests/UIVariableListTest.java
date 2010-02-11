package org.openshapa.uitests;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.concurrent.TimeUnit;
import javax.swing.JDialog;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.data.TableCell;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.openshapa.OpenSHAPA;
import org.openshapa.util.UIUtils;
import org.openshapa.views.NewDatabaseV;
import org.openshapa.views.NewProjectV;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the creation of a new database.
 *
 */
public final class UIVariableListTest extends OpenSHAPATestClass {
    /**
     * Resource map to access error messages in resources.
     */
    private ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(NewDatabaseV.class);

    /**
     * Test adding new variables with a script.
     */
    @Test
    public void testAddingVariablesWithScript() {
        System.err.println("testAddingVariablesWithScript");
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        //1. Run script to populate
        mainFrameFixture.menuItemWithPath("Script", "Run script").click();

        JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
        jfcf.selectFile(demoFile).approve();

        //Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        //2. Check that variable list is populated with correct data
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Variable List").click();

        DialogFixture vlDialog = mainFrameFixture.dialog();

        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);

        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        Assert.assertEquals(vlDialog.table().rowCount(), ssPanel.allColumns().size());

        for (int i = 0; i < ssPanel.allColumns().size(); i++) {
            Assert.assertTrue(inTable(ssPanel.allColumns().elementAt(i).getColumnName(), vlDialog.table(), 1));
            Assert.assertTrue(inTable(ssPanel.allColumns().elementAt(i).getColumnType(), vlDialog.table(), 2));
        }
    }

    /**
     * Test adding new variables manually.
     */
    @Test
    public void testAddingVariablesManually() {
        System.err.println("testAddingVariablesManually");
        String [] varNames = {"t", "p", "i", "n", "m", "f"};
        String [] varTypes = {"text", "predicate", "integer", "nominal",
        "matrix", "float"};

        //1. Create a new variable, then check that variable list is populated with correct data
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Variable List").click();

        DialogFixture vlDialog = mainFrameFixture.dialog();

        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);

        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());
        
        int numCols = 0;

        for (int i = 0; i < varNames.length; i++) {
            UIUtils.createNewVariable(mainFrameFixture, varNames[i], varTypes[i]);
            numCols++;
            //Check that variable list is populated with correct data
            Assert.assertEquals(ssPanel.allColumns().size(), numCols);
            Assert.assertEquals(vlDialog.table().rowCount(), ssPanel.allColumns().size());

            for (int j = 0; j < ssPanel.allColumns().size(); j++) {
                Assert.assertTrue(inTable(ssPanel.allColumns().elementAt(j).getColumnName(), vlDialog.table(), 1));
                Assert.assertTrue(inTable(ssPanel.allColumns().elementAt(j).getColumnType(), vlDialog.table(), 2));
            }
        }

        
    }

    /**
     * Test adding new variables with a script.
     */
    @Test
    public void testRemovalWithNewDatabase() {
        System.err.println("testRemovalWithNewDatabase");
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        //1. Run script to populate
        mainFrameFixture.menuItemWithPath("Script", "Run script").click();

        JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
        jfcf.selectFile(demoFile).approve();

        //Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        //2. Check that variable list is populated
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Variable List").click();

        DialogFixture vlDialog = mainFrameFixture.dialog();

        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);

        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        Assert.assertEquals(vlDialog.table().rowCount(), ssPanel.allColumns().size());

        //3. Create new database (and discard unsaved changes)
        if (Platform.isOSX()) {
            mainFrameFixture.pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_N).modifiers(KeyEvent.META_MASK));
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

        DialogFixture newDatabaseDialog = mainFrameFixture.dialog(
                new GenericTypeMatcher<JDialog>(JDialog.class) {
                    @Override
                    protected boolean isMatching(JDialog dialog) {
                        return dialog.getClass().equals(NewProjectV.class);
                    }
                }, Timeout.timeout(5, TimeUnit.SECONDS));

        newDatabaseDialog.textBox("nameField").enterText("n");

        newDatabaseDialog.button("okButton").click();

        //4. Check that variable list is empty
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Variable List").click();

        vlDialog = mainFrameFixture.dialog();
        Assert.assertTrue(vlDialog.table().rowCount() == 0);


    }

    /**
     * Because variable list is not in order, checks if String is in a Table column.
     * @param item String to find
     * @param t Table to look in
     * @param col Column number
     * @return true if found, else false
     */
    private Boolean inTable(final String item, final JTableFixture t,
            final int col) {
        for (int i = 0; i < t.rowCount(); i++) {
            if (item.equals(t.valueAt(TableCell.row(i).column(col)))) {
                return true;
            }
        }
        return false;
    }
}
