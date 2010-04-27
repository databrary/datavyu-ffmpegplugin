package org.openshapa.uitests;

import static org.fest.reflect.core.Reflection.method;

import java.awt.Frame;
import java.awt.Point;

import java.io.File;

import java.util.Iterator;

import javax.swing.filechooser.FileFilter;

import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.PlaybackVFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;

import org.openshapa.OpenSHAPA;

import org.openshapa.util.UIUtils;

import org.openshapa.views.OpenSHAPAFileChooser;
import org.openshapa.views.PlaybackV;
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
    @Test public void testBug686() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Open video
        // a. Get Spreadsheet
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        // b. Open Data Viewer Controller
        mainFrameFixture.clickMenuItemWithPath("Controller",
            "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(0, 100));

        final PlaybackVFixture pvf = new PlaybackVFixture(
                mainFrameFixture.robot,
                (PlaybackV) mainFrameFixture.dialog().component());

        // c. Open video
        String root = System.getProperty("testPath");
        final File videoFile = new File(root + "/ui/head_turns.mov");
        Assert.assertTrue(videoFile.exists());

        if (Platform.isOSX()) {
            final PluginManager pm = PluginManager.getInstance();

            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);

            for (FileFilter f : pm.getPluginFileFilters()) {
                fc.addChoosableFileFilter(f);
            }

            fc.setSelectedFile(videoFile);
            method("openVideo").withParameterTypes(OpenSHAPAFileChooser.class)
                .in(OpenSHAPA.getPlaybackController()).invoke(fc);
        } else {
            pvf.button("addDataButton").click();

            JFileChooserFixture jfcf = pvf.fileChooser(Timeout.timeout(30000));
            jfcf.selectFile(videoFile).approve();
        }

        // 2. Get window
        Iterator it = pvf.getDataViewers().iterator();

        Frame vid = ((Frame) it.next());
        FrameFixture vidWindow = new FrameFixture(mainFrameFixture.robot, vid);

        vidWindow.moveTo(new Point(pvf.component().getWidth() + 10, 100));

        // 2. Jog forward and check
        pvf.pressJogForwardButton();
        pvf.label("timestampLabel").requireText("00:00:00:040");
        pvf.pressJogForwardButton();
        pvf.label("timestampLabel").requireText("00:00:00:080");

        // 3. Jog back and check
        pvf.pressJogBackButton();
        pvf.label("timestampLabel").requireText("00:00:00:040");
        pvf.pressJogBackButton();
        pvf.label("timestampLabel").requireText("00:00:00:000");
    }
}
