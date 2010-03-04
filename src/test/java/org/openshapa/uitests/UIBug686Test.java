package org.openshapa.uitests;

import java.awt.Frame;
import static org.fest.reflect.core.Reflection.method;

import java.awt.Point;
import java.io.File;
import java.util.Iterator;
import javax.swing.filechooser.FileFilter;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.DataControllerFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.util.Platform;
import org.openshapa.util.UIUtils;
import org.openshapa.views.DataControllerV;
import org.openshapa.views.OpenSHAPAFileChooser;
import org.openshapa.views.continuous.PluginManager;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Bug686 Description: Jog doesn't move a single frame at a time.
 */
public final class UIBug686Test extends OpenSHAPATestClass {

    /**
     * Test Bug 686.
     */
    @Test
    public void testBug686() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Open video
        // a. Get Spreadsheet
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        // b. Open Data Viewer Controller
        mainFrameFixture.clickMenuItemWithPath("Controller",
                "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(300, 300));
        final DataControllerFixture dcf =
                new DataControllerFixture(mainFrameFixture.robot,
                        (DataControllerV) mainFrameFixture.dialog()
                        .component());

        // c. Open video
        String root = System.getProperty("testPath");
        final File videoFile = new File(root + "/ui/head_turns.mov");
        Assert.assertTrue(videoFile.exists());

        if (Platform.isOSX()) {
            final PluginManager pm = PluginManager.getInstance();

            GuiActionRunner.execute(new GuiTask() {
                public void executeInEDT() {
                    OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
                    fc.setVisible(false);
                    for (FileFilter f : pm.getPluginFileFilters()) {
                        fc.addChoosableFileFilter(f);
                    }
                    fc.setSelectedFile(videoFile);
                    method("openVideo").withParameterTypes(
                            OpenSHAPAFileChooser.class)
                            .in((DataControllerV) dcf.component()).invoke(fc);
                }
            });
        } else {
            dcf.button("addDataButton").click();

            JFileChooserFixture jfcf = dcf.fileChooser();
            jfcf.selectFile(videoFile).approve();
        }
        while(dcf.getDataViewers().size() < 1) {

        }

        // 2. Jog forward and check
        dcf.button("jogForwardButton").click();
        dcf.label("timestampLabel").requireText("00:00:00:040");
        dcf.button("jogForwardButton").click();
        dcf.label("timestampLabel").requireText("00:00:00:080");

        // 3. Jog back and check
        dcf.button("jogBackButton").click();
        dcf.label("timestampLabel").requireText("00:00:00:040");
        dcf.button("jogBackButton").click();
        dcf.label("timestampLabel").requireText("00:00:00:000");
    }
}
