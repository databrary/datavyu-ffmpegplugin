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

import java.awt.event.InputEvent;

import static org.fest.reflect.core.Reflection.method;

import java.awt.event.KeyEvent;

import java.io.File;
import java.io.FilenameFilter;

import java.util.Vector;

import javax.swing.text.BadLocationException;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.util.Platform;

import org.openshapa.OpenSHAPA;

import org.openshapa.util.FileFilters.OPFFilter;
import org.openshapa.util.UIUtils;

import org.openshapa.views.OpenSHAPAFileChooser;

import org.testng.Assert;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * Test for the New Cells.
 */
public final class UIDeleteCellValueTest extends OpenSHAPATestClass {

    /**
    * Deleting these temp files before and after tests because Java does
    * not always delete them during the test case. Doing the deletes here
    * has resulted in consistent behaviour.
    */
    @AfterMethod @BeforeMethod protected void deleteFiles() {


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
     * Tests for deleting the cell value.
     *
     * @param type
     *            type of column
     * @throws BadLocationException
     *             if can't click on a particular caret pos
     */
    private void testDeleteCellValue(final String type)
        throws BadLocationException {
        File demoFile = new File(testFolder + "/ui/all_column_types.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Open spreadsheet and check that script has data
        spreadsheet = mainFrameFixture.getSpreadsheet();

        Vector<SpreadsheetColumnFixture> cols = spreadsheet.allColumns();
        Assert.assertTrue(cols.size() > 0);
        highlightAndBackspaceTest(spreadsheet, 1, type);
        highlightAndDeleteTest(spreadsheet, 2, type);
        backSpaceAllTest(spreadsheet, 3, type);
        deleteAllTest(spreadsheet, 4, type);
    }

    /**
     * Test deleting values from nominal cells.
     *
     * @throws java.lang.Exception
     *             on any error
     */
    @Test public void testDeleteNominalCell() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String type = "NOMINAL";
        testDeleteCellValue(type);
    }

    /**
     * Test deleting values from float cells.
     *
     * @throws java.lang.Exception
     *             on any error
     */
    @Test public void testDeleteFloatCell() throws Exception {
        String type = "FLOAT";
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        testDeleteCellValue(type);
    }

    /**
     * Test deleting values from integer cells.
     *
     * @throws java.lang.Exception
     *             on any error
     */
    @Test public void testDeleteIntCell() throws Exception {
        String type = "INTEGER";
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        testDeleteCellValue(type);
    }

    /**
     * Test deleting values from text cells.
     *
     * @throws java.lang.Exception
     *             on any error
     */
    @Test public void testDeleteTextCell() throws Exception {
        String type = "TEXT";

        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        testDeleteCellValue(type);
    }

    /**
     * Tests deletion by selecting all and pressing backspace.
     *
     * @param ss
     *            Spreadsheet
     * @param type
     *            column type to test
     */
    private void highlightAndBackspaceTest(final SpreadsheetPanelFixture ss,
        final int cellWithID, final String type) throws BadLocationException {
        SpreadsheetCellFixture cell = null;

        // 1. Get cell for test type
        for (SpreadsheetColumnFixture col : ss.allColumns()) {

            if (col.getColumnType().equalsIgnoreCase(type)) {
                cell = col.cell(cellWithID);

                break;
            }
        }

        // 2. Test different inputs as per specifications
        cell.select(SpreadsheetCellFixture.VALUE, 0,
            cell.cellValue().text().length());
        cell.cellValue().pressAndReleaseKey(KeyPressInfo.keyCode(
                KeyEvent.VK_BACK_SPACE));
        Assert.assertEquals(cell.cellValue().text(), "<val>");
    }

    /**
     * Tests deletion by selecting all and pressing delete.
     *
     * @param ss
     *            Spreadsheet
     * @param type
     *            column type to test
     */
    private void highlightAndDeleteTest(final SpreadsheetPanelFixture ss,
        final int cellWithID, final String type) throws BadLocationException {
        SpreadsheetCellFixture cell = null;

        // 1. Get cell for test type
        for (SpreadsheetColumnFixture col : ss.allColumns()) {

            if (col.getColumnType().equalsIgnoreCase(type)) {
                cell = col.cell(cellWithID);

                break;
            }
        }

        // 2. Test different inputs as per specifications
        cell.select(SpreadsheetCellFixture.VALUE, 0,
            cell.cellValue().text().length());
        cell.cellValue().pressAndReleaseKey(KeyPressInfo.keyCode(
                KeyEvent.VK_DELETE));
        Assert.assertEquals(cell.cellValue().text(), "<val>");
    }

    /**
     * Tests deletion by backspacing all.
     *
     * @param ss
     *            Spreadsheet
     * @param type
     *            column type to test
     */
    private void backSpaceAllTest(final SpreadsheetPanelFixture ss,
        final int cellWithID, final String type) throws BadLocationException {
        SpreadsheetCellFixture cell = null;

        // 1. Get cell for test type
        for (SpreadsheetColumnFixture col : ss.allColumns()) {

            if (col.getColumnType().equalsIgnoreCase(type)) {
                cell = col.cell(cellWithID);

                break;
            }
        }

        // 2. Test different input as per specifications
        int strLen = cell.cellValue().text().length();

        cell.clickToCharPos(SpreadsheetCellFixture.VALUE, strLen, 1);

        for (int i = 0; i < strLen; i++) {
            cell.cellValue().pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_BACK_SPACE));
        }

        if (type.equalsIgnoreCase("FLOAT")) {
            Assert.assertTrue(UIUtils.equalValues(cell.cellValue().text(),
                    "0.0"));
        } else {
            Assert.assertEquals(cell.cellValue().text(), "<val>");
        }
    }

    /**
     * Tests deletion by pressing delete.
     *
     * @param ss
     *            Spreadsheet
     * @param type
     *            column type to test
     */
    private void deleteAllTest(final SpreadsheetPanelFixture ss,
        final int cellWithID, final String type) throws BadLocationException {
        SpreadsheetCellFixture cell = null;

        // 1. Get cell for test type
        for (SpreadsheetColumnFixture col : ss.allColumns()) {

            if (col.getColumnType().equalsIgnoreCase(type)) {
                cell = col.cell(cellWithID);

                break;
            }
        }

        // 2. Test different input as per specifications
        int strLen = cell.cellValue().text().length();

        cell.clickToCharPos(SpreadsheetCellFixture.VALUE, 0, 1);

        for (int i = 0; i < strLen; i++) {
            cell.cellValue().pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_DELETE));
        }

        if (type.equalsIgnoreCase("FLOAT")) {
            Assert.assertTrue(UIUtils.equalValues(cell.cellValue().text(),
                    "0.0"));
        } else {
            Assert.assertEquals(cell.cellValue().text(), "<val>");
        }
    }

    /**
     * Test for bug 1914.
     * For a text variable create a cell, add a value and save the project.
     * (Not sure if app reload required at this stage.) Remove some but not all
     * of the text in the cell value, without adding new text (though the
     * text-is-added-as-well permeation has not been investigated).  The app
     * will/may not detect that a change has occurred (i.e. not * added to the
     * file name in the title bar), but force a Save anyway. Quit and restart
     * the app and reload the project.  If the error persists, then the deleted
     * text will have returned...
     */
    /*BugzID1914:@Test*/ public void partialDeletionTest() throws BadLocationException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String varName = "t";
        String varType = "text";

        final String originalText = "Hello world";
        final String afterDeleteText = "Hello wo";

        // Create new text cell and input data
        spreadsheet = mainFrameFixture.getSpreadsheet();

        mainFrameFixture.createNewVariable(varName, varType);
        spreadsheet.column(0).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        SpreadsheetCellFixture cell = spreadsheet.column(varName).cell(1);
        cell.cellValue().enterText(originalText);

        // Save project
        File tempFile = new File(tempFolder + "/partialDeletionTest.opf");

        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);
            fc.setFileFilter(OPFFilter.INSTANCE);

            fc.setSelectedFile(tempFile);

            method("save").withParameterTypes(OpenSHAPAFileChooser.class).in(
                OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Save As...");

            mainFrameFixture.fileChooser().component().setFileFilter(
                OPFFilter.INSTANCE);
            mainFrameFixture.fileChooser().selectFile(tempFile).approve();
        }

        // Delete last few characters and confirm deleted
        cell.select(SpreadsheetCellFixture.VALUE, 8, originalText.length());
        cell.cellValue().pressAndReleaseKey(KeyPressInfo.keyCode(
                KeyEvent.VK_BACK_SPACE));
        cell.cellValue().requireText(afterDeleteText);

        // Save project
        mainFrameFixture.clickMenuItemWithPath("File", "Save");

        // Reopen project
        // 3. Create a new database (and discard unsaved changes)
        if (Platform.isOSX()) {
            mainFrameFixture.pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_N).modifiers(InputEvent.META_MASK));
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "New");
        }

        DialogFixture newProjectDialog = mainFrameFixture.dialog("NewProjectV");

        newProjectDialog.textBox("nameField").enterText("n");

        newProjectDialog.button("okButton").click();

        // Check that no cells exist
        Assert.assertEquals(spreadsheet.allColumns().size(), 0);

        // Reopen project
        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);

            fc.setFileFilter(OPFFilter.INSTANCE);
            fc.setSelectedFile(tempFile);

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

            mainFrameFixture.fileChooser().component().setFileFilter(
                OPFFilter.INSTANCE);

            mainFrameFixture.fileChooser().selectFile(tempFile).approve();
        }

        // Check that characters are still deleted.
        SpreadsheetPanelFixture ssPanel2 = mainFrameFixture.getSpreadsheet();

        SpreadsheetCellFixture cell2 = ssPanel2.column(0).cell(1);
        cell2.cellValue().requireText(afterDeleteText);
    }

    /**
    * Test for bug 690.
    * Pressing backspace in cell value starts to delete characters once it
    * reaches the end.
    * Example value = 12345
    * IP at: 123|45
    * press backspace 3 times
    * IP and value = |45
    * press backspace again
    * EXPECT: nothing to happen
    * ACTUAL: deletes 4, i.e. IP & Value = |5
    * @throws BadLocationException on clicking on char position
    */
    @Test public void testBug690() throws BadLocationException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String varName = "t";
        String varType = "text";

        final String originalText = "Hello world";
        final String afterDeleteText = "o world";

        // Create new text cell and input data
        spreadsheet = mainFrameFixture.getSpreadsheet();

        mainFrameFixture.createNewVariable(varName, varType);
        spreadsheet.column(0).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        SpreadsheetCellFixture cell = spreadsheet.column(varName).cell(1);
        cell.cellValue().enterText(originalText);

        // Backspace "Hell"
        cell.clickToCharPos(SpreadsheetCellFixture.VALUE, 4, 1);

        for (int i = 0; i < 10; i++) {
            cell.cellValue().pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_BACK_SPACE));
        }

        cell.cellValue().requireText(afterDeleteText);
    }
}
