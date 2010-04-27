package org.openshapa.uitests;

import java.awt.Point;
import java.awt.event.KeyEvent;

import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.PlaybackVFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;

import org.openshapa.util.UIUtils;

import org.openshapa.views.PlaybackV;
import org.openshapa.views.discrete.SpreadsheetPanel;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test for Bug 784. When using the alternative find (shift + find) it doesn't
 * work when hitting: shift + clicking with the mouse.
 * Should behave the same as shift and hitting the '+' key on the numpad.
 */
public final class UIBug784Test extends OpenSHAPATestClass {

    /**
     * Test for Bug784.
     */
    @Test public void testBug784() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        String varName = "v";
        String varType =
            UIUtils.VAR_TYPES[(int) (Math.random() * UIUtils.VAR_TYPES.length)];
        String varRadio = varType.toLowerCase() + "TypeButton";
        UIUtils.createNewVariable(mainFrameFixture, varName, varRadio);

        // 2. Check that a column has been created
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);

        // Find our new column header
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());
        Assert.assertNotNull(ssPanel.column(varName));

        // 3. Create cell
        ssPanel.column("v").click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        //4. Set onset and offset
        SpreadsheetCellFixture cell = ssPanel.column("v").cell(1);
        cell.onsetTimestamp().enterText("111111111");
        cell.offsetTimestamp().enterText("222222222");

        //5. Open Dataviewer Controller
        mainFrameFixture.clickMenuItemWithPath("Controller",
            "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(300, 300));

        final PlaybackVFixture pvf = new PlaybackVFixture(
                mainFrameFixture.robot,
                (PlaybackV) mainFrameFixture.dialog().component());

        //*. Highlight select cell because of BugzID:1430
        cell.fillSelectCell(true);

        //6. Test Find by clicking button
        Assert.assertEquals(pvf.getCurrentTime(), "00:00:00:000");
        pvf.pressFindButton();
        Assert.assertEquals(pvf.getCurrentTime(), "11:11:11:111");

        //7. Test Shift-Find by clicking button
        mainFrameFixture.pressKey(KeyEvent.VK_SHIFT);
        pvf.pressFindButton();
        mainFrameFixture.releaseKey(KeyEvent.VK_SHIFT);
        Assert.assertEquals(pvf.getCurrentTime(), "22:22:22:222");

        //8. Test Find again by clicking button
        pvf.pressFindButton();
        Assert.assertEquals(pvf.getCurrentTime(), "11:11:11:111");
    }
}
