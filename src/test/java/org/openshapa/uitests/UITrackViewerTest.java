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
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.NeedleFixture;
import org.fest.swing.fixture.RegionFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.fixture.TrackFixture;
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
    @Test public void testNeedleMovement() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Get Spreadsheet
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        // 2. Open Data Viewer Controller and get starting time
        mainFrameFixture.clickMenuItemWithPath("Controller",
            "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(0, 100));

        final DataControllerFixture dcf = new DataControllerFixture(
                mainFrameFixture.robot,
                (DataControllerV) mainFrameFixture.dialog().component());

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
                            OpenSHAPAFileChooser.class).in(
                            (DataControllerV) dcf.component()).invoke(fc);
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
                Logger.getLogger(UITrackViewerTest.class.getName()).log(
                    Level.SEVERE, null, ex);
            }
        }

        Assert.assertEquals(dcf.getCurrentTime(),
            dcf.getTrackMixerController().getNeedle()
                .getCurrentTimeAsTimeStamp());
    }

    /**
     * Test needle movement to ensure needle can't go beyond start or end.
     */
    @Test public void testRangeOfNeedleMovement() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Get Spreadsheet
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        // 2. Open Data Viewer Controller and get starting time
        mainFrameFixture.clickMenuItemWithPath("Controller",
            "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(0, 100));

        final DataControllerFixture dcf = new DataControllerFixture(
                mainFrameFixture.robot,
                (DataControllerV) mainFrameFixture.dialog().component());

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
                            OpenSHAPAFileChooser.class).in(
                            (DataControllerV) dcf.component()).invoke(fc);
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
        int widthOfTrack = dcf.getTrackMixerController().getTracksEditor()
            .getTrack(0).getWidthInPixels();

        while (needle.getCurrentTimeAsLong() <= 0) {
            needle.drag(widthOfTrack);
        }

        Assert.assertEquals(needle.getCurrentTimeAsTimeStamp(), "00:01:00:000");

        //5. Move needle beyond start time
        needle.drag(-1 * widthOfTrack);
        Assert.assertEquals(needle.getCurrentTimeAsTimeStamp(), "00:00:00:000");
    }

    /**
     * Test region movement and effect on needle.
     * The following cases are tested:
     * 1. Right region beyond start + needle moves with it
     * 2. Right region beyond end
     * 3. Left region beyond start
     * 4. Left region beyond end + needle moves with it
     * 5. Right region to middle + needle moves with it
     * 6. Left region can't cross (go beyond) right
     * 7. Right region can't cross left
     */
    @Test public void testRegionMovement() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Get Spreadsheet
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        // 2. Open Data Viewer Controller and get starting time
        mainFrameFixture.clickMenuItemWithPath("Controller",
            "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(0, 100));

        final DataControllerFixture dcf = new DataControllerFixture(
                mainFrameFixture.robot,
                (DataControllerV) mainFrameFixture.dialog().component());

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
                            OpenSHAPAFileChooser.class).in(
                            (DataControllerV) dcf.component()).invoke(fc);
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

        RegionFixture region = dcf.getTrackMixerController().getRegion();
        NeedleFixture needle = dcf.getTrackMixerController().getNeedle();
        int widthOfTrack = dcf.getTrackMixerController().getTracksEditor()
            .getTrack(0).getWidthInPixels();

        // TEST1. Right region beyond start + needle stays same
        while (region.getEndTimeAsLong() > 0) {
            region.dragEndMarker(-1 * widthOfTrack);
        }

        Assert.assertEquals(region.getEndTimeAsTimeStamp(), "00:00:00:000");
        Assert.assertEquals(needle.getCurrentTimeAsTimeStamp(), "00:00:00:000");

        // TEST2. Right region beyond end + needle stays same
        while (region.getEndTimeAsLong() <= 0) {
            region.dragEndMarker(widthOfTrack);
        }

        Assert.assertEquals(region.getEndTimeAsTimeStamp(), "00:01:00:000");
        Assert.assertEquals(needle.getCurrentTimeAsTimeStamp(), "00:00:00:000");

        // TEST3. Left region beyond end + needle moves with it
        while (region.getStartTimeAsLong() <= 0) {
            region.dragStartMarker(widthOfTrack);
        }

        Assert.assertEquals(region.getStartTimeAsTimeStamp(), "00:01:00:000");
        Assert.assertEquals(needle.getCurrentTimeAsTimeStamp(), "00:01:00:000");

        // TEST4. Left region beyond start + needle stays same
        while (region.getStartTimeAsLong() > 0) {
            region.dragStartMarker(-1 * widthOfTrack);
        }

        Assert.assertEquals(region.getStartTimeAsTimeStamp(), "00:00:00:000");
        Assert.assertEquals(needle.getCurrentTimeAsTimeStamp(), "00:01:00:000");

        // TEST5. Right region to middle + needle moves with it
        region.dragEndMarker(-1 * widthOfTrack / 4);

        TimeStamp endTS = null;

        try {
            endTS = new TimeStamp(region.getEndTimeAsTimeStamp());
            Assert.assertTrue((endTS.ge(new TimeStamp("00:00:30:000"))) &&
                (endTS.le(new TimeStamp("00:00:50:000"))));
        } catch (SystemErrorException ex) {
            Logger.getLogger(UITrackViewerTest.class.getName()).log(
                Level.SEVERE, null, ex);
        }

        Assert.assertEquals(needle.getCurrentTimeAsTimeStamp(),
            endTS.toHMSFString());

        // TEST6. Left region can't cross (go beyond) right
        region.dragStartMarker(widthOfTrack);
        Assert.assertEquals(region.getStartTimeAsTimeStamp(),
            endTS.toHMSFString());
        Assert.assertEquals(needle.getCurrentTimeAsTimeStamp(),
            endTS.toHMSFString());

        region.dragStartMarker(-1 * widthOfTrack / 2);

        TimeStamp startTS = null;

        try {
            startTS = new TimeStamp(region.getStartTimeAsTimeStamp());
            Assert.assertTrue((startTS.ge(new TimeStamp("00:00:00:000"))) &&
                (startTS.le(new TimeStamp("00:00:40:000"))));
        } catch (SystemErrorException ex) {
            Logger.getLogger(UITrackViewerTest.class.getName()).log(
                Level.SEVERE, null, ex);
        }

        Assert.assertEquals(needle.getCurrentTimeAsTimeStamp(),
            endTS.toHMSFString());

        // TEST7. Right region can't cross left
        region.dragEndMarker(-1 * widthOfTrack);
        Assert.assertEquals(region.getEndTimeAsTimeStamp(),
            startTS.toHMSFString());
        Assert.assertEquals(needle.getCurrentTimeAsTimeStamp(),
            startTS.toHMSFString());
    }

    /**
     * Test moving track while locked and unlocked.
     */
    @Test public void testLockUnlockTrack() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Get Spreadsheet
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        // 2. Open Data Viewer Controller and get starting time
        mainFrameFixture.clickMenuItemWithPath("Controller",
            "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(0, 100));

        final DataControllerFixture dcf = new DataControllerFixture(
                mainFrameFixture.robot,
                (DataControllerV) mainFrameFixture.dialog().component());

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
                            OpenSHAPAFileChooser.class).in(
                            (DataControllerV) dcf.component()).invoke(fc);
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

        //4. Drag track
        TrackFixture track = dcf.getTrackMixerController().getTracksEditor()
            .getTrack(0);
        Assert.assertEquals(track.getOffsetTimeAsLong(), 0);

        while (track.getOffsetTimeAsLong() <= 0) {
            track.drag(150);
        }

        long offset = track.getOffsetTimeAsLong();
        Assert.assertTrue(offset > 0, "offset=" + offset);

        //5. Lock track
        track.pressLockButton();

        //6. Try to drag track, shouldn't be able to.
        track.drag(150);
        Assert.assertEquals(track.getOffsetTimeAsLong(), offset);
        track.drag(-100);
        Assert.assertEquals(track.getOffsetTimeAsLong(), offset);
    }

    /**
     * Test snapping tracks.
     */
    /*@Test*/ public void testTrackSnapping() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Get Spreadsheet
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        // 2. Open Data Viewer Controller and get starting time
        mainFrameFixture.clickMenuItemWithPath("Controller",
            "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(0, 100));

        final DataControllerFixture dcf = new DataControllerFixture(
                mainFrameFixture.robot,
                (DataControllerV) mainFrameFixture.dialog().component());

        //3. Open track view
        dcf.pressShowTracksButton();

        // c. Open first video
        String root = System.getProperty("testPath");
        final File videoFile1 = new File(root + "/ui/head_turns.mov");
        Assert.assertTrue(videoFile1.exists());

        if (Platform.isOSX()) {
            final PluginManager pm = PluginManager.getInstance();

            GuiActionRunner.execute(new GuiTask() {
                    public void executeInEDT() {
                        OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
                        fc.setVisible(false);

                        for (FileFilter f : pm.getPluginFileFilters()) {
                            fc.addChoosableFileFilter(f);
                        }

                        fc.setSelectedFile(videoFile1);
                        method("openVideo").withParameterTypes(
                            OpenSHAPAFileChooser.class).in(
                            (DataControllerV) dcf.component()).invoke(fc);
                    }
                });
        } else {
            boolean worked = false;
            JFileChooserFixture jfcf = null;

            do {
                dcf.button("addDataButton").click();

                try {
                    jfcf = dcf.fileChooser();
                    jfcf.selectFile(videoFile1).approve();
                    worked = true;
                } catch (Exception e) {
                    // keep trying
                }
            } while (worked == false);
        }

        // 2. Get first window
        Iterator it = dcf.getDataViewers().iterator();

        Frame vid1 = ((Frame) it.next());
        FrameFixture vidWindow1 = new FrameFixture(mainFrameFixture.robot,
                vid1);

        vidWindow1.moveTo(new Point(dcf.component().getWidth() + 10, 100));

        // c. Open second video
        final File videoFile2 = new File(root + "/ui/head_turns_copy.mov");
        Assert.assertTrue(videoFile2.exists());

        if (Platform.isOSX()) {
            final PluginManager pm = PluginManager.getInstance();

            GuiActionRunner.execute(new GuiTask() {
                    public void executeInEDT() {
                        OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
                        fc.setVisible(false);

                        for (FileFilter f : pm.getPluginFileFilters()) {
                            fc.addChoosableFileFilter(f);
                        }

                        fc.setSelectedFile(videoFile2);
                        method("openVideo").withParameterTypes(
                            OpenSHAPAFileChooser.class).in(
                            (DataControllerV) dcf.component()).invoke(fc);
                    }
                });
        } else {
            boolean worked = false;
            JFileChooserFixture jfcf = null;

            do {
                dcf.button("addDataButton").click();

                try {
                    jfcf = dcf.fileChooser();
                    jfcf.selectFile(videoFile2).approve();
                    worked = true;
                } catch (Exception e) {
                    // keep trying
                }
            } while (worked == false);
        }

        // 2. Get second window
        it = dcf.getDataViewers().iterator();

        Frame vid2 = ((Frame) it.next());
        FrameFixture vidWindow2 = new FrameFixture(mainFrameFixture.robot,
                vid2);

        vidWindow2.moveTo(new Point(0, dcf.component().getHeight() + 130));

        //3. Move needle 50 pixels
        NeedleFixture needle = dcf.getTrackMixerController().getNeedle();

        while (needle.getCurrentTimeAsLong() <= 0) {
            needle.drag(100);
        }

        long snapPoint1 = needle.getCurrentTimeAsLong();

        Assert.assertTrue(snapPoint1 > 0);

        //4. Add bookmark to Track 1 using button
        //a. Click track 1 to select
        TrackFixture track1 = dcf.getTrackMixerController().getTracksEditor()
            .getTrack(0);

        while (!track1.isSelected()) {
            track1.click();
        }

        Assert.assertTrue(track1.isSelected());

        //b. Click add bookmark button
        dcf.getTrackMixerController().pressBookmarkButton();
        Assert.assertEquals(snapPoint1, track1.getBookmarkTimeAsLong());

        //c. Click track 1 to deselect
        while (track1.isSelected()) {
            track1.click();
        }

        Assert.assertFalse(track1.isSelected());

        //5. Move needle another 50 pixels
        while (needle.getCurrentTimeAsLong() <= (1.9 * snapPoint1)) {
            dcf.getTrackMixerController().getNeedle().drag(100);
        }

        Assert.assertTrue(needle.getCurrentTimeAsLong() > snapPoint1);

        long snapPoint2 = dcf.getTrackMixerController().getNeedle()
            .getCurrentTimeAsLong();
        Assert.assertTrue(snapPoint2 > (1.9 * snapPoint1));

        //6. Add bookmark to Track 2 using right click popup menu
        TrackFixture track2 = dcf.getTrackMixerController().getTracksEditor()
            .getTrack(1);
        JPopupMenuFixture popup = track2.showPopUpMenu();
        popup.menuItemWithPath("Set bookmark").click();
        Assert.assertEquals(snapPoint2, track2.getBookmarkTimeAsLong());

        while (track2.isSelected()) {
            track2.click();
        }

        Assert.assertFalse(track2.isSelected());

        //Move needle away
        while (needle.getCurrentTimeAsLong() <= snapPoint2) {
            needle.drag(100);
        }

        Assert.assertTrue(needle.getCurrentTimeAsLong() > snapPoint2);

        //7. Drag track 1 to snap
        //a. Drag 1 pixel to see what 1 pixel equals in time
        track1.drag(1);

        long onePixelTime = track1.getOffsetTimeAsLong();

        //b. Drag away from start marker
        track1.drag(20);

        //c. Drag 1 pixel at a time until it snaps
        //Turn on snap button
        dcf.getTrackMixerController().getSnapToggleButton().check();
        dcf.getTrackMixerController().getSnapToggleButton().requireSelected();

        long newTime = onePixelTime;
        long oldTime = 0;

        while ((!dcf.getTrackMixerController().getTracksEditor().getSnapMarker()
                    .isVisible()) ||
                (Math.abs((newTime - oldTime) - onePixelTime) < 2)) {

            //Check if we've snapped
            if (
                dcf.getTrackMixerController().getTracksEditor().getSnapMarker()
                    .isVisible() && (newTime > (10 * onePixelTime))) {
                System.err.println("Snapped while moving");
                System.err.println("New time=" + newTime);
                System.err.println("Old time=" + oldTime);
                System.err.println("onePixelTime=" + onePixelTime);

                break;
            }

            //Check we haven't gone too far
            if (newTime > snapPoint1) {
                track1.releaseLeftMouse();
                Assert.assertTrue(false, "passed snap point");
            }

            oldTime = track1.getOffsetTimeAsLong();
            track1.dragWithoutReleasing(1);
            newTime = track1.getOffsetTimeAsLong();
        }

        System.err.println("Snapped?");
        System.err.println("New time=" + newTime);
        System.err.println("Old time=" + oldTime);
        System.err.println("onePixelTime=" + onePixelTime);
        System.err.println("snapPoint2=" + snapPoint2);
        System.err.println("snapPoint1=" + snapPoint1);
        System.err.println("trackOffset=" + track1.getOffsetTimeAsLong());

        //d. Check if snapped
        Assert.assertEquals(track1.getOffsetTimeAsLong(),
            snapPoint2 - snapPoint1);
        Assert.assertTrue(dcf.getTrackMixerController().getTracksEditor()
            .getSnapMarker().isVisible());
        track1.releaseLeftMouse();

        //8. Drag track 1 to snap from the other direction
        //a. Drag track away
        track1.drag(35);

        //b. Drag 1 pixel at a time until it snaps
        newTime = track1.getOffsetTimeAsLong();
        oldTime = newTime + onePixelTime;

        dcf.getTrackMixerController().getSnapToggleButton().requireSelected();

        while (Math.abs((oldTime - newTime) - onePixelTime) < 2) {

            //Check if we've snapped
            if (
                dcf.getTrackMixerController().getTracksEditor().getSnapMarker()
                    .isVisible() && (newTime > (10 * onePixelTime))) {
                break;
            }

            //Check we haven't gone too far
            if (newTime < 0) {
                track1.releaseLeftMouse();
                Assert.assertTrue(false, "passed snap point");
            }

            oldTime = track1.getOffsetTimeAsLong();
            track1.dragWithoutReleasing(-1);
            newTime = track1.getOffsetTimeAsLong();
        }

        System.err.print(newTime - oldTime + "," + onePixelTime);

        //b. Check if snapped
        Assert.assertEquals(track1.getOffsetTimeAsLong(),
            snapPoint2 - snapPoint1);
        Assert.assertTrue(dcf.getTrackMixerController().getTracksEditor()
            .getSnapMarker().isVisible());
        track2.releaseLeftMouse();
    }
}
