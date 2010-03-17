package org.openshapa.uitests;

import static org.fest.reflect.core.Reflection.method;

import java.awt.Frame;
import java.awt.Point;
import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.filechooser.FileFilter;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.DataControllerFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.util.Platform;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.db.TimeStamp;
import org.openshapa.util.UIUtils;
import org.openshapa.views.DataControllerV;
import org.openshapa.views.OpenSHAPAFileChooser;
import org.openshapa.views.continuous.PluginManager;
import org.openshapa.views.discrete.SpreadsheetPanel;
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
                        (DataControllerV) mainFrameFixture.dialog().component());

        // 3. Open track view
        dcf.pressShowTracksButton();

        // c. Open video
        String root = System.getProperty("testPath");
        final File videoFile = new File(root + "/ui/head_turns.mov");
        Assert.assertTrue(videoFile.exists());

        if (Platform.isOSX()) {
            final PluginManager pm = PluginManager.getInstance();

            GuiActionRunner.execute(new GuiTask() {
                @Override
                public void executeInEDT() {
                    OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
                    fc.setVisible(false);
                    for (FileFilter f : pm.getPluginFileFilters()) {
                        fc.addChoosableFileFilter(f);
                    }
                    fc.setSelectedFile(videoFile);
                    method("openVideo").withParameterTypes(
                            OpenSHAPAFileChooser.class).in(dcf.component())
                            .invoke(fc);
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

        // 4. Move needle to 9 seconds on data controller time.
        boolean lessThan9seconds = true;
        while (lessThan9seconds) {
            TimeStamp currTS;
            try {
                dcf.getTrackMixerController().getNeedle().drag(1);
                currTS = new TimeStamp(dcf.getCurrentTime());
                lessThan9seconds = currTS.lt(new TimeStamp("00:00:09:000"));
            } catch (SystemErrorException ex) {
                Logger.getLogger(UITrackViewerTest.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }

        Assert.assertEquals(dcf.getCurrentTime(), dcf.getTrackMixerController()
                .getNeedle().getCurrentTimeAsTimeStamp());
    }

    /**
     * Bug794. Steps to reproduce: open a movie, shuttle forwards to a rate of
     * say 4x, pause the movie. Now instead of pressing unpause (as you might
     * normally do), press shuttle forward again. I often see this going to
     * 1/16x for some reason.
     */
    // @Test
    public void testBug794() {
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
                        (DataControllerV) mainFrameFixture.dialog().component());

        // c. Open video
        String root = System.getProperty("testPath");
        final File videoFile = new File(root + "/ui/head_turns.mov");
        Assert.assertTrue(videoFile.exists());

        if (Platform.isOSX()) {
            final PluginManager pm = PluginManager.getInstance();

            GuiActionRunner.execute(new GuiTask() {
                @Override
                public void executeInEDT() {
                    OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
                    fc.setVisible(false);
                    for (FileFilter f : pm.getPluginFileFilters()) {
                        fc.addChoosableFileFilter(f);
                    }
                    fc.setSelectedFile(videoFile);
                    method("openVideo").withParameterTypes(
                            OpenSHAPAFileChooser.class).in(dcf.component())
                            .invoke(fc);
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

        // 3. Shuttle forward to 4x
        dcf.pressPlayButton();
        // Wait for it to actually start playing
        while (dcf.getCurrentTime().equals("00:00:00:000")) {
            System.err.println("Waiting...");
        }
        while (!dcf.getSpeed().equals("4")) {
            String preSpeed = dcf.getSpeed();
            dcf.pressShuttleForwardButton();
            String postSpeed = dcf.getSpeed();

            Assert.assertNotSame(preSpeed, postSpeed);
        }
        // Using Thread.sleep to wait for 4 seconds.
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UITrackViewerTest.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        // 3. Press pause
        dcf.pressPauseButton();
        Assert.assertEquals(dcf.getSpeed(), "[4]");

        // 4. Press shuttle and check that it continues at 8
        dcf.pressShuttleForwardButton();
        Assert.assertEquals(dcf.getSpeed(), "8");
    }

    /**
     * Bug798. Set playback speed to any value, using say shuttle to 4x. Pause
     * the movie. Now rewind it past zero (causing a forced stop). Pressing the
     * pause/unpause button will now restore the saved speed; this is bad! If
     * you for example save a negative playback speed, pause/unpause will not
     * work at all. To reproduce this behaviour: play the movie as per normal,
     * then shuttle to a negative speed. Pause the movie. Rewind past zero
     * (forcing a stop). Unpause/play the movie, voila, cannot play the movie
     * using that button anymore.
     */
    // @Test
    public void testBug798() {
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
                        (DataControllerV) mainFrameFixture.dialog().component());

        // c. Open video
        String root = System.getProperty("testPath");
        final File videoFile = new File(root + "/ui/head_turns.mov");
        Assert.assertTrue(videoFile.exists());

        if (Platform.isOSX()) {
            final PluginManager pm = PluginManager.getInstance();

            GuiActionRunner.execute(new GuiTask() {
                @Override
                public void executeInEDT() {
                    OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
                    fc.setVisible(false);
                    for (FileFilter f : pm.getPluginFileFilters()) {
                        fc.addChoosableFileFilter(f);
                    }
                    fc.setSelectedFile(videoFile);
                    method("openVideo").withParameterTypes(
                            OpenSHAPAFileChooser.class).in(dcf.component())
                            .invoke(fc);
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

        // 2. Shuttle forward to 4x
        dcf.pressPlayButton();
        // Wait for it to actually start playing
        while (dcf.getCurrentTime().equals("00:00:00:000")) {
            System.err.println("Waiting...");
        }
        while (!dcf.getSpeed().equals("4")) {
            String preSpeed = dcf.getSpeed();
            dcf.pressShuttleForwardButton();
            String postSpeed = dcf.getSpeed();

            Assert.assertNotSame(preSpeed, postSpeed);
        }
        // Using Thread.sleep to wait for 4 seconds.
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UITrackViewerTest.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        // 3. Press pause
        dcf.pressPauseButton();
        Assert.assertEquals(dcf.getSpeed(), "[4]");

        // 4. Press rewind to zero
        dcf.pressRewindButton();
        // Using Thread.sleep to wait for 4 seconds.
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UITrackViewerTest.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        // Check that its 0 time and speed
        Assert.assertEquals(dcf.getCurrentTime(), "00:00:00:000");
        Assert.assertEquals(dcf.getSpeed(), "0");

        // 5. Press pause and ensure it does nothing
        dcf.pressPauseButton();
        Assert.assertEquals(dcf.getCurrentTime(), "00:00:00:000");
        Assert.assertEquals(dcf.getSpeed(), "0");
    }

    /**
     * Bug464. When a video finishes playing, hitting play does nothing. I
     * expected it to play again.
     */
    // @Test
    public void testBug464() {
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
                        (DataControllerV) mainFrameFixture.dialog().component());

        // c. Open video
        String root = System.getProperty("testPath");
        final File videoFile = new File(root + "/ui/head_turns.mov");
        Assert.assertTrue(videoFile.exists());

        if (Platform.isOSX()) {
            final PluginManager pm = PluginManager.getInstance();

            GuiActionRunner.execute(new GuiTask() {
                @Override
                public void executeInEDT() {
                    OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
                    fc.setVisible(false);
                    for (FileFilter f : pm.getPluginFileFilters()) {
                        fc.addChoosableFileFilter(f);
                    }
                    fc.setSelectedFile(videoFile);
                    method("openVideo").withParameterTypes(
                            OpenSHAPAFileChooser.class).in(dcf.component())
                            .invoke(fc);
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

        // 2. Fast forward video to end and confirm you've reached end (1min)
        dcf.pressFastForwardButton();
        // Using Thread.sleep to wait for 5 seconds.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UITrackViewerTest.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        // Check time
        Assert.assertEquals(dcf.getCurrentTime(), "00:01:00:000");

        // 3. Press play, should start playing again
        dcf.pressPlayButton();
        String currTime = dcf.getCurrentTime();
        try {
            TimeStamp currTS = new TimeStamp(currTime);
            TimeStamp oneMin = new TimeStamp("00:01:00:000");
            Assert.assertTrue(currTS.le(oneMin));
        } catch (SystemErrorException ex) {
            Logger.getLogger(UITrackViewerTest.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }
}
