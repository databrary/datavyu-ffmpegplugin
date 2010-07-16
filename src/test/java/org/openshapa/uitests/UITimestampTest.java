package org.openshapa.uitests;

import java.awt.event.KeyEvent;

import java.util.Vector;

import javax.swing.text.BadLocationException;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.util.Platform;

import org.openshapa.util.UIUtils;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test for the New Cells.
 *
 * @todo After bugs resolved, add more advanced cell tests involving left/right
 *       caret movement
 */
public final class UITimestampTest extends OpenSHAPATestClass {

    /**
     * Test editing the onset and offset timestamps.
     */
    @Test public void testTimestampEditing() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        JTextComponentFixture onset, offset;

        String[] testInput = {
                "123456789", "6789", "a13", "12:34:56:789", "4.43", "127893999",
                "12:78:93:999", "12:34", "12:34:56"
            };

        int numOfTests = testInput.length;

        String[] expectedTestOutput = {
                "12:34:56:789", "68:29:00:000", "13:00:00:000", "12:34:56:789",
                "44:30:00:000", "13:19:33:999", "13:19:33:999", "12:34:00:000",
                "12:34:56:000"
            };

        Vector<SpreadsheetCellFixture> c = createNewCells(numOfTests);

        for (int i = 0; i < numOfTests; i++) {
            onset = c.elementAt(i).onsetTimestamp();
            offset = c.elementAt(i).offsetTimestamp();
            onset.enterText(testInput[i]);
            offset.enterText(testInput[i]);

            Assert.assertEquals(onset.text(), expectedTestOutput[i]);
            Assert.assertEquals(offset.text(), expectedTestOutput[i]);
        }
    }

    /**
     * Test advanced editing the onset and offset timestamps.
     *
     * @throws java.lang.Exception
     *             on any error NEEDS TO BE REWRITTEN FOR FEST AFTER BUG IS
     *             FIXED.
     */
    /*
     * BugzID:540 public void testTimestampAdvancedEditing() throws Exception {
     * String[] testInput = {"123456789", "1234", "a13", "12:34:56:789", "4.43",
     * "12:34", "12:78:93:999", "12:34"}; int numOfTests = testInput.length;
     * //advanced Input will be provided between testInput Key[][] advancedInput
     * = {{Key.LEFT, Key.LEFT}, {Key.LEFT, Key.LEFT, Key.RIGHT}, {Key.BACKSPACE,
     * Key.LEFT}, {Key.BACKSPACE, Key.LEFT, Key.LEFT, Key.LEFT, Key.DELETE,
     * Key.RIGHT}, {Key.LEFT, Key.RIGHT}, {Key.BACKSPACE, Key.BACKSPACE,
     * Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE}, {Key.LEFT, Key.LEFT,
     * Key.LEFT, Key.LEFT}}; String[] expectedTestOutput = {"12:34:56:712",
     * "12:31:30:000", "12:34:56:789", "12:34:50:744", "44:31:23:400",
     * "12:78:93:999", "12:78:91:234"}; Vector<Cell> c =
     * createNewCells(numOfTests); for (int i = 0; i < numOfTests - 1; i++) {
     * TextBox onset = c.elementAt(i).getOnset(); TextBox offset =
     * c.elementAt(i).getOffset(); c.elementAt(i).enterText(Cell.ONSET,
     * testInput[i], advancedInput[i], testInput[i+1]);
     * c.elementAt(i).enterText(Cell.OFFSET, testInput[i], advancedInput[i],
     * testInput[i+1]); assertTrue(c.elementAt(i).getOnset().getText().equals(
     * expectedTestOutput[i]));
     * assertTrue(c.elementAt(i).getOffset().getText().equals(
     * expectedTestOutput[i])); } }
     */

    /**
     * Test pasting the onset and offset timestamps.
     */
    @Test public void testTimestampPasting() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        JTextComponentFixture onset, offset;

        String[] testInput = {
                "123456789", "6789", "a13", "12:34:56:789", "4.43", "127893999",
                "12:78:93:999", "12:34", "12:34:56"
            };

        int numOfTests = testInput.length;

        String[] expectedTestOutput = {
                "12:34:56:789", "68:29:00:000", "13:00:00:000", "12:34:56:789",
                "44:30:00:000", "13:19:33:999", "13:19:33:999", "12:34:00:000",
                "12:34:56:000"
            };

        Vector<SpreadsheetCellFixture> c = createNewCells(numOfTests);

        for (int i = 0; i < numOfTests; i++) {
            onset = c.elementAt(i).onsetTimestamp();
            offset = c.elementAt(i).offsetTimestamp();
            UIUtils.setClipboard(testInput[i]);

            // Select all and paste
            onset.selectAll();
            onset.pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_V)
                .modifiers(Platform.controlOrCommandMask()));

            offset.selectAll();
            offset.pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_V)
                .modifiers(Platform.controlOrCommandMask()));

            Assert.assertEquals(onset.text(), expectedTestOutput[i]);
            Assert.assertEquals(offset.text(), expectedTestOutput[i]);
        }
    }

    /**
     * Test deleting the onset and offset timestamps.
     */
    @Test public void testTimestampDeletion() throws BadLocationException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String[] testInput = {
                "123456789", "12:34:56:789", "127893999", "12:78:93:999"
            };

        int numOfTests = testInput.length;

        Vector<SpreadsheetCellFixture> cells = createNewCells(numOfTests);

        for (int i = 0; i < numOfTests; i++) {
            cells.elementAt(i).onsetTimestamp().enterText(testInput[i]);
            cells.elementAt(i).offsetTimestamp().enterText(testInput[i]);
        }

        // highlight and backspace test
        SpreadsheetCellFixture c = cells.elementAt(0);
        c.onsetTimestamp().selectAll();
        mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_BACK_SPACE);
        Assert.assertEquals(c.onsetTimestamp().text(), "00:00:00:000");

        // Try multiple deletes on empty
        mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_BACK_SPACE);
        Assert.assertEquals(c.onsetTimestamp().text(), "00:00:00:000");
        c.onsetTimestamp().selectAll();
        mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_BACK_SPACE);
        Assert.assertEquals(c.onsetTimestamp().text(), "00:00:00:000");

        c.offsetTimestamp().selectAll();
        mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_BACK_SPACE);
        Assert.assertEquals(c.offsetTimestamp().text(), "00:00:00:000");

        // Try multiple deletes on empty
        mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_BACK_SPACE);
        Assert.assertEquals(c.offsetTimestamp().text(), "00:00:00:000");
        c.offsetTimestamp().selectAll();
        mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_BACK_SPACE);
        Assert.assertEquals(c.offsetTimestamp().text(), "00:00:00:000");

        // highlight and delete test
        c = cells.elementAt(1);
        c.onsetTimestamp().selectAll();
        mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_DELETE);
        Assert.assertEquals(c.onsetTimestamp().text(), "00:00:00:000");

        // Try multiple deletes on empty
        mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_DELETE);
        Assert.assertEquals(c.onsetTimestamp().text(), "00:00:00:000");
        c.onsetTimestamp().selectAll();
        mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_DELETE);
        Assert.assertEquals(c.onsetTimestamp().text(), "00:00:00:000");

        c.offsetTimestamp().selectAll();
        mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_DELETE);
        Assert.assertEquals(c.offsetTimestamp().text(), "00:00:00:000");

        // Try multiple deletes on empty
        mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_DELETE);
        Assert.assertEquals(c.offsetTimestamp().text(), "00:00:00:000");
        c.offsetTimestamp().selectAll();
        mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_DELETE);
        Assert.assertEquals(c.offsetTimestamp().text(), "00:00:00:000");

        // backspace all, plus extra to test backspacing on empty
        c = cells.elementAt(2);

        int tsLength = c.onsetTimestamp().text().length();
        c.onsetTimestamp().pressAndReleaseKey(KeyPressInfo.keyCode(
                KeyEvent.VK_END));

        for (int i = 0; i < (tsLength + 5); i++) {
            mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_BACK_SPACE);
        }

        Assert.assertEquals(c.onsetTimestamp().text(), "00:00:00:000");

        c.offsetTimestamp().pressAndReleaseKey(KeyPressInfo.keyCode(
                KeyEvent.VK_END));

        for (int i = 0; i < (tsLength + 5); i++) {
            mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_BACK_SPACE);
        }

        Assert.assertEquals(c.offsetTimestamp().text(), "00:00:00:000");

        // delete key all
        c = cells.elementAt(3);
        c.onsetTimestamp().pressAndReleaseKey(KeyPressInfo.keyCode(
                KeyEvent.VK_HOME));

        for (int i = 0; i < (tsLength + 5); i++) {
            mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_DELETE);
        }

        Assert.assertEquals(c.onsetTimestamp().text(), "00:00:00:000");

        c.offsetTimestamp().pressAndReleaseKey(KeyPressInfo.keyCode(
                KeyEvent.VK_HOME));

        for (int i = 0; i < (tsLength + 5); i++) {
            mainFrameFixture.robot.pressAndReleaseKey(KeyEvent.VK_DELETE);
        }

        Assert.assertEquals(c.offsetTimestamp().text(), "00:00:00:000");
    }

    @Test public void highlightAndDeleteTest() throws BadLocationException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        
        final String originalText = "123456789";
        final String afterDeleteText = "00:00:00:000";

        // Create new text cell and input data
        spreadsheet = mainFrameFixture.getSpreadsheet();

        String varType =
            UIUtils.VAR_TYPES[(int) (Math.random() * UIUtils.VAR_TYPES.length)];

        mainFrameFixture.createNewVariable("v", varType);
        spreadsheet.column(0).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        SpreadsheetCellFixture cell = spreadsheet.column("v").cell(1);
        cell.onsetTimestamp().enterText(originalText);
        cell.offsetTimestamp().enterText(originalText);

        Assert.assertEquals(cell.onsetTimestamp().text(), "12:34:56:789");
        Assert.assertEquals(cell.offsetTimestamp().text(), "12:34:56:789");

        // Select all and backspace
        cell.select(SpreadsheetCellFixture.ONSET, 0,
            cell.onsetTimestamp().text().length());
        cell.onsetTimestamp().pressAndReleaseKey(KeyPressInfo.keyCode(
                KeyEvent.VK_BACK_SPACE));
        cell.onsetTimestamp().requireText(afterDeleteText);

        // Select all and delete
        cell.select(SpreadsheetCellFixture.OFFSET, 0,
            cell.offsetTimestamp().text().length());
        cell.offsetTimestamp().pressAndReleaseKey(KeyPressInfo.keyCode(
                KeyEvent.VK_DELETE));
        cell.offsetTimestamp().requireText(afterDeleteText);
    }

    @Test public void partialHighlightAndDeleteTest()
        throws BadLocationException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        
        final String originalText = "123456789";
        final String afterDeleteText = "12:00:00:789";

        // Create new text cell and input data
        spreadsheet = mainFrameFixture.getSpreadsheet();

        String varType =
            UIUtils.VAR_TYPES[(int) (Math.random() * UIUtils.VAR_TYPES.length)];

        mainFrameFixture.createNewVariable("v", varType);
        spreadsheet.column(0).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        SpreadsheetCellFixture cell = spreadsheet.column("v").cell(1);
        cell.onsetTimestamp().enterText(originalText);
        cell.offsetTimestamp().enterText(originalText);

        Assert.assertEquals(cell.onsetTimestamp().text(), "12:34:56:789");
        Assert.assertEquals(cell.offsetTimestamp().text(), "12:34:56:789");

        // Select all and backspace
        cell.select(SpreadsheetCellFixture.ONSET, 3, 8);
        cell.onsetTimestamp().pressAndReleaseKey(KeyPressInfo.keyCode(
                KeyEvent.VK_BACK_SPACE));
        cell.onsetTimestamp().requireText(afterDeleteText);

        // Select all and delete
        cell.select(SpreadsheetCellFixture.OFFSET, 3, 8);
        cell.offsetTimestamp().pressAndReleaseKey(KeyPressInfo.keyCode(
                KeyEvent.VK_DELETE));
        cell.offsetTimestamp().requireText(afterDeleteText);
    }

    @Test public void partialHighlightAndChangeTest()
        throws BadLocationException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        
        final String originalText = "123456789";
        final String changeText1 = "98a27";
        final String changeText2 = "98a27123";
        final String afterDeleteText1 = "13:38:27:789";
        final String afterDeleteText2 = "13:38:27:123";

        // Create new text cell and input data
        spreadsheet = mainFrameFixture.getSpreadsheet();

        String varType =
            UIUtils.VAR_TYPES[(int) (Math.random() * UIUtils.VAR_TYPES.length)];

        mainFrameFixture.createNewVariable("v", varType);
        spreadsheet.column(0).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        SpreadsheetCellFixture cell = spreadsheet.column("v").cell(1);
        cell.onsetTimestamp().enterText(originalText);
        cell.offsetTimestamp().enterText(originalText);

        Assert.assertEquals(cell.onsetTimestamp().text(), "12:34:56:789");
        Assert.assertEquals(cell.offsetTimestamp().text(), "12:34:56:789");

        // Select all and edit
        cell.select(SpreadsheetCellFixture.ONSET, 3, 8);
        cell.onsetTimestamp().enterText(changeText1);
        cell.onsetTimestamp().requireText(afterDeleteText1);

        // Select all and delete
        cell.select(SpreadsheetCellFixture.OFFSET, 3, 8);
        cell.offsetTimestamp().enterText(changeText2);
        cell.offsetTimestamp().requireText(afterDeleteText2);
    }

    /**
     * Create new cells.
     */
    private Vector<SpreadsheetCellFixture> createNewCells(final int amount) {
        String varName = "t";
        String varType =
            UIUtils.VAR_TYPES[(int) (Math.random() * UIUtils.VAR_TYPES.length)];
        String varRadio = varType.toLowerCase();

        // 1. Create new variable
        mainFrameFixture.createNewVariable(varName, varRadio);

        spreadsheet = mainFrameFixture.getSpreadsheet();
        Assert.assertNotNull(spreadsheet.column(varName));
        spreadsheet.column(varName).click();

        // 2. Create new cells
        for (int i = 0; i < amount; i++) {
            mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");
        }

        return spreadsheet.column(varName).allCells();
    }
}
