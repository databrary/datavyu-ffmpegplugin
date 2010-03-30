package org.openshapa.uitests;

import java.awt.event.KeyEvent;

import java.io.File;

import java.util.Vector;

import javax.swing.text.BadLocationException;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;

import org.openshapa.util.UIUtils;

import org.openshapa.views.discrete.SpreadsheetPanel;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test for the New Cells.
 */
public final class UIDeleteCellValueTest extends OpenSHAPATestClass {

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
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/all_column_types.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        if (Platform.isOSX()) {
            UIUtils.runScript(demoFile);
        } else {
            mainFrameFixture.clickMenuItemWithPath("Script", "Run script");

            JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
            jfcf.selectFile(demoFile).approve();
        }

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog(Timeout.timeout(
                    1000));

        long currentTime = System.currentTimeMillis();
        long maxTime = currentTime + 5000; // 5 second timeout
        while ((System.currentTimeMillis() < maxTime) &&
                (!scriptConsole.textBox().text().contains("Finished"))) {
            Thread.yield();
        }

        scriptConsole.button("closeButton").click();

        // 2. Open spreadsheet and check that script has data
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());
        Vector<SpreadsheetColumnFixture> cols = ssPanel.allColumns();
        Assert.assertTrue(cols.size() > 0);
        highlightAndBackspaceTest(ssPanel, 1, type);
        highlightAndDeleteTest(ssPanel, 2, type);
        backSpaceAllTest(ssPanel, 3, type);
        deleteAllTest(ssPanel, 4, type);
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
    // BugzID:1351
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

        // Forced to do this because of BugzID:1350
        for (int i = 0; i < strLen; i++) {
            cell.cellValue().pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_RIGHT));
        }

        for (int i = 0; i < strLen; i++) {
            cell.cellValue().pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_BACK_SPACE));
        }

        Assert.assertEquals(cell.cellValue().text(), "<val>");
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

        Assert.assertEquals(cell.cellValue().text(), "<val>");
    }

}
