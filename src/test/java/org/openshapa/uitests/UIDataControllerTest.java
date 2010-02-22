package org.openshapa.uitests;

import java.awt.Point;
import java.awt.event.KeyEvent;

import org.fest.swing.fixture.DataControllerFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.openshapa.util.UIUtils;
import org.openshapa.views.DataControllerV;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for the DataController.
 */
public final class UIDataControllerTest extends OpenSHAPATestClass {
    /**
     * Nominal test input.
     */
    private String[] nominalTestInput = { "Subject stands )up ", "$10,432" };

    /**
     * Nominal test output.
     */
    private String[] expectedNominalTestOutput =
            { "Subject stands up", "$10432" };

    /**
     * Text test input.
     */
    private String[] textTestInput = { "Subject stands up ", "$10,432" };

    /**
     * Integer test input.
     */
    private String[] integerTestInput = { "1a9", "10-432" };

    /**
     * Integer test output.
     */
    private String[] expectedIntegerTestOutput = { "19", "-43210" };

    /**
     * Float test input.
     */
    private String[] floatTestInput = { "1a.9", "10-43.2" };

    /**
     * Float test output.
     */
    private String[] expectedFloatTestOutput = { "1.90", "-43.2100" };

    /**
     * Standard test sequence focussing on jogging.
     * 
     * @param varName
     *            variable name
     * @param varType
     *            variable type
     * @param testInputArray
     *            test input values as array
     * @param testExpectedArray
     *            test expected values as array
     */
    private void standardSequence1(final String varName, final String varType,
            final String[] testInputArray, final String[] testExpectedArray) {

        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());
        ssPanel.deselectAll();
        UIUtils.createNewVariable(mainFrameFixture, varName, varType);

        // 2. Open Data Viewer Controller and get starting time
        mainFrameFixture.menuItemWithPath("Controller",
                "Data Viewer Controller").click();
        mainFrameFixture.dialog().moveTo(new Point(300, 300));
        DataControllerFixture fix =
                new DataControllerFixture(mainFrameFixture.robot,
                        (DataControllerV) mainFrameFixture.dialog().component());

        // 3. Create new cell - so we have something to send key to because
        SpreadsheetColumnFixture column = ssPanel.column(varName);
        column.header().click();
        mainFrameFixture.menuItemWithPath("Spreadsheet", "New Cell").click();

        // 4. Test Jogging back and forth.
        for (int i = 0; i < 5; i++) {
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD3);
        }
        Assert.assertEquals("00:00:05:000", fix.getCurrentTime());

        for (int i = 0; i < 2; i++) {
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD1);
        }
        Assert.assertEquals(fix.getCurrentTime(), "00:00:03:000");

        // 5. Test Create New Cell with Onset.
        mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD0);
        SpreadsheetCellFixture cell1 = column.cell(1);
        SpreadsheetCellFixture cell2 = column.cell(2);

        Assert.assertEquals(column.numOfCells(), 2);
        Assert.assertEquals(cell1.onsetTimestamp().text(), "00:00:00:000");
        Assert.assertEquals(cell1.offsetTimestamp().text(), "00:00:02:999");

        Assert.assertEquals(cell2.onsetTimestamp().text(), "00:00:03:000");
        Assert.assertEquals(cell2.offsetTimestamp().text(), "00:00:00:000");

        // 6. Insert text into both cells.
        cell1.cellValue().enterText(testInputArray[0]);
        cell2.cellValue().enterText(testInputArray[1]);

        Assert.assertEquals(cell1.cellValue().text(), testExpectedArray[0]);
        Assert.assertEquals(cell2.cellValue().text(), testExpectedArray[1]);
        cell2.selectCell();

        // 7. Jog forward 5 times and change cell onset.
        for (int i = 0; i < 5; i++) {
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD3);
        }
        Assert.assertEquals(fix.getCurrentTime(), "00:00:08:000");

        mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD3);
        mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_DIVIDE);
        Assert.assertEquals(cell2.onsetTimestamp().text(), "00:00:09:000");

        // 8. Change cell offset.
        fix.pressSetOffsetButton();
        Assert.assertEquals(cell2.offsetTimestamp().text(), "00:00:09:000");

        // 9. Jog back and forward, then create a new cell with onset
        for (int i = 0; i < 2; i++) {
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD1);
        }

        Assert.assertEquals(fix.getCurrentTime(), "00:00:07:000");
        mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD0);

        SpreadsheetCellFixture cell3 = column.cell(3);
        Assert.assertEquals(column.numOfCells(), 3);
        Assert.assertEquals(cell2.offsetTimestamp().text(), "00:00:09:000");
        Assert.assertEquals(cell3.offsetTimestamp().text(), "00:00:00:000");
        Assert.assertEquals(cell3.onsetTimestamp().text(), "00:00:07:000");

        // 10. Test data controller view onset, offset and find.
        for (int cellId = 1; cellId <= column.numOfCells(); cellId++) {
            cell1 = column.cell(cellId);
            ssPanel.deselectAll();
            cell1.selectCell();
            Assert.assertEquals(fix.getFindOnset(), cell1.onsetTimestamp()
                    .text());
            Assert.assertEquals(fix.getFindOffset(), cell1.offsetTimestamp()
                    .text());
            fix.pressFindButton();
            Assert.assertEquals(fix.getCurrentTime(), cell1.onsetTimestamp()
                    .text());
            fix.pressShiftFindButton();
            Assert.assertEquals(fix.getCurrentTime(), cell1.offsetTimestamp()
                    .text());
        }

        fix.close();
    }

    /**
     * Runs standardsequence1 for different variable types (except matrix and
     * predicate), side by side.
     * 
     * @throws Exception
     *             any exception
     */
    @Test
    public void testStandardSequence1() throws Exception {
        // Text
        standardSequence1("t", "text", textTestInput, textTestInput);
        // Integer
        standardSequence1("i", "integer", integerTestInput,
                expectedIntegerTestOutput);
        // Float
        standardSequence1("f", "float", floatTestInput, expectedFloatTestOutput);
        // Nominal
        standardSequence1("n", "nominal", nominalTestInput,
                expectedNominalTestOutput);
    }
}
