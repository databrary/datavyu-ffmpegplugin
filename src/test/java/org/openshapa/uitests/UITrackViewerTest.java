package org.openshapa.uitests;

import java.awt.Frame;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.fest.swing.fixture.NeedleFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.util.Platform;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.util.UIUtils;
import org.openshapa.views.DataControllerV;
import org.openshapa.views.OpenSHAPAFileChooser;
import org.openshapa.views.continuous.PluginManager;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.openshapa.models.db.TimeStamp;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for the Track View in the Data Controller.
 */
public final class UITrackViewerTest extends OpenSHAPATestClass {
    
     /**
     * Test needle movement to ensure needle time is the same as the clock time.
     */
    @Test
    public void testNeedleMovement() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        // 1. Get Spreadsheet
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        // 2. Open Data Viewer Controller and get starting time
        mainFrameFixture.clickMenuItemWithPath("Controller",
                "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(0, 100));
        final DataControllerFixture dcf =
                new DataControllerFixture(mainFrameFixture.robot,
                        (DataControllerV) mainFrameFixture.dialog()
                        .component());

        //3. Open track view
        dcf.pressShowTracksButton();

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

        // 2. Get window
        Iterator it = dcf.getDataViewers().iterator();

        Frame vid = ((Frame) it.next());
        FrameFixture vidWindow = new FrameFixture(mainFrameFixture.robot, vid);

        vidWindow.moveTo(new Point(dcf.component().getWidth() + 10, 100));

        //4. Move needle to 6 seconds on data controller time.
        boolean lessThan6seconds = true;
        while (lessThan6seconds) {
            TimeStamp currTS;
            try {
                dcf.getTrackMixerController().getNeedle().drag(1);
                currTS = new TimeStamp(dcf.getCurrentTime());
                lessThan6seconds = currTS.lt(new TimeStamp("00:00:06:000"));
            } catch (SystemErrorException ex) {
                Logger.getLogger(UITrackViewerTest.class.getName())
                        .log(Level.SEVERE, null, ex);
            }            
        }
        
        Assert.assertEquals(dcf.getCurrentTime(), 
                dcf.getTrackMixerController()
                .getNeedle().getCurrentTimeAsTimeStamp());
    }

    /**
     * Test needle movement to ensure needle can't go beyond start or end.
     */
    @Test
    public void testRangeOfNeedleMovement() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        // 1. Get Spreadsheet
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        // 2. Open Data Viewer Controller and get starting time
        mainFrameFixture.clickMenuItemWithPath("Controller",
                "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(0, 100));
        final DataControllerFixture dcf =
                new DataControllerFixture(mainFrameFixture.robot,
                        (DataControllerV) mainFrameFixture.dialog()
                        .component());

        //3. Open track view
        dcf.pressShowTracksButton();

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

        // 2. Get window
        Iterator it = dcf.getDataViewers().iterator();

        Frame vid = ((Frame) it.next());
        FrameFixture vidWindow = new FrameFixture(mainFrameFixture.robot, vid);

        vidWindow.moveTo(new Point(dcf.component().getWidth() + 10, 100));

        //4. Move needle beyond end time
        NeedleFixture needle = dcf.getTrackMixerController().getNeedle();
        needle.drag(dcf.getTrackMixerController().getTracksEditor().getTrack(0)
                .getWidthInPixels());
        Assert.assertEquals(needle.getCurrentTimeAsTimeStamp(), "00:01:00:000");

        //5. Move needle beyond start time
        needle.drag(-1 * dcf.getTrackMixerController().getTracksEditor()
                .getTrack(0).getWidthInPixels());
        Assert.assertEquals(needle.getCurrentTimeAsTimeStamp(), "00:00:00:000");
    }
    
}
