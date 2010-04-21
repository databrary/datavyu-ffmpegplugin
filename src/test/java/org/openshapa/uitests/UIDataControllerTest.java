package org.openshapa.uitests;

import java.awt.Frame;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.fest.reflect.core.Reflection.method;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import java.io.File;

import java.util.Iterator;

import javax.swing.filechooser.FileFilter;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.DataControllerFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;

import org.openshapa.models.db.SystemErrorException;

import org.openshapa.util.UIUtils;

import org.openshapa.views.DataControllerV;
import org.openshapa.views.OpenSHAPAFileChooser;
import org.openshapa.views.continuous.PluginManager;
import org.openshapa.views.discrete.SpreadsheetPanel;

import org.openshapa.models.db.TimeStamp;

import org.openshapa.util.UIImageUtils;
import org.openshapa.views.continuous.quicktime.QTDataViewer;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test for the DataController.
 */
public final class UIDataControllerTest extends OpenSHAPATestClass {

    /**
     * Nominal test input.
     */
    private String[] nominalTestInput = {"Subject stands )up ", "$10,432"};

    /**
     * Nominal test output.
     */
    private String[] expectedNominalTestOutput = {
            "Subject stands up", "$10432"
        };

    /**
     * Text test input.
     */
    private String[] textTestInput = {"Subject stands up ", "$10,432"};

    /**
     * Integer test input.
     */
    private String[] integerTestInput = {"1a9", "10-432"};

    /**
     * Integer test output.
     */
    private String[] expectedIntegerTestOutput = {"19", "-43210"};

    /**
     * Float test input.
     */
    private String[] floatTestInput = {"1a.9", "10-43.2"};

    /**
     * Float test output.
     */
    private String[] expectedFloatTestOutput = {"1.90", "-43.2100"};

    /**
     * Standard test sequence focussing on jogging.
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
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());
        ssPanel.deselectAll();
        UIUtils.createNewVariable(mainFrameFixture, varName, varType);

        // 2. Open Data Viewer Controller and get starting time
        mainFrameFixture.clickMenuItemWithPath("Controller",
            "Data Viewer Controller");

        DataControllerFixture dcf = new DataControllerFixture(
                mainFrameFixture.robot,
                (DataControllerV) mainFrameFixture.dialog().component());

        // 3. Create new cell - so we have something to send key to because
        SpreadsheetColumnFixture column = ssPanel.column(varName);
        column.click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        // 4. Test Jogging back and forth.
        for (int i = 0; i < 5; i++) {
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD3);
        }

        Assert.assertEquals(dcf.getCurrentTime(), "00:00:00:200");

        for (int i = 0; i < 2; i++) {
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD1);
        }

        Assert.assertEquals(dcf.getCurrentTime(), "00:00:00:120");

        // 5. Test Create New Cell with Onset.
        mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD0);

        SpreadsheetCellFixture cell1 = column.cell(1);
        SpreadsheetCellFixture cell2 = column.cell(2);

        Assert.assertEquals(column.numOfCells(), 2);
        Assert.assertEquals(cell1.onsetTimestamp().text(), "00:00:00:000");
        Assert.assertEquals(cell1.offsetTimestamp().text(), "00:00:00:119");

        Assert.assertEquals(cell2.onsetTimestamp().text(), "00:00:00:120");
        Assert.assertEquals(cell2.offsetTimestamp().text(), "00:00:00:000");

        // 6. Insert text into both cells.
        cell1.cellValue().enterText(testInputArray[0]);
        cell2.cellValue().enterText(testInputArray[1]);

        Assert.assertEquals(cell1.cellValue().text(), testExpectedArray[0]);
        Assert.assertEquals(cell2.cellValue().text(), testExpectedArray[1]);
        cell2.fillSelectCell(true);

        // 7. Jog forward 5 times and change cell onset.
        for (int i = 0; i < 5; i++) {
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD3);
        }

        Assert.assertEquals(dcf.getCurrentTime(), "00:00:00:320");

        mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD3);
        mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_DIVIDE);
        Assert.assertEquals(cell2.onsetTimestamp().text(), "00:00:00:360");

        // 8. Change cell offset.
        dcf.pressSetOffsetButton();
        Assert.assertEquals(cell2.offsetTimestamp().text(), "00:00:00:360");

        // 9. Jog back and forward, then create a new cell with onset
        for (int i = 0; i < 2; i++) {
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD1);
        }

        Assert.assertEquals(dcf.getCurrentTime(), "00:00:00:280");
        mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD0);

        SpreadsheetCellFixture cell3 = column.cell(3);
        Assert.assertEquals(column.numOfCells(), 3);
        Assert.assertEquals(cell2.offsetTimestamp().text(), "00:00:00:360");
        Assert.assertEquals(cell3.offsetTimestamp().text(), "00:00:00:000");
        Assert.assertEquals(cell3.onsetTimestamp().text(), "00:00:00:280");

        // 10. Test data controller view onset, offset and find.
        for (int cellId = 1; cellId <= column.numOfCells(); cellId++) {
            cell1 = column.cell(cellId);

            // ssPanel.deselectAll();
            column.click();
            cell1.fillSelectCell(true);
            Assert.assertEquals(dcf.getFindOnset(),
                cell1.onsetTimestamp().text());
            Assert.assertEquals(dcf.getFindOffset(),
                cell1.offsetTimestamp().text());
            dcf.pressFindButton();
            Assert.assertEquals(dcf.getCurrentTime(),
                cell1.onsetTimestamp().text());
            dcf.pressShiftFindButton();
            Assert.assertEquals(dcf.getCurrentTime(),
                cell1.offsetTimestamp().text());
        }

        dcf.close();
    }

    /**
     * Runs standardsequence1 for different variable types (except matrix and
     * predicate), side by side.
     * @throws Exception
     *             any exception
     */
    /*@Test*/ public void testStandardSequence1() throws Exception {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        mainFrameFixture.clickMenuItemWithPath("Controller",
            "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(300, 300));

        final DataControllerFixture dcf = new DataControllerFixture(
                mainFrameFixture.robot,
                (DataControllerV) mainFrameFixture.dialog().component());

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

            JFileChooserFixture jfcf = dcf.fileChooser(Timeout.timeout(30000));
            jfcf.selectFile(videoFile).approve();
        }

        dcf.close();

        // Text
        standardSequence1("t", "text", textTestInput, textTestInput);

        // Integer
        standardSequence1("i", "integer", integerTestInput,
            expectedIntegerTestOutput);

        // Float
        standardSequence1("f", "float", floatTestInput,
            expectedFloatTestOutput);

        // Nominal
        standardSequence1("n", "nominal", nominalTestInput,
            expectedNominalTestOutput);
    }

    /**
     * Bug720.
     * Go Back should contain default value of 00:00:05:000.
     */
    /*@Test*/ public void testBug720() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Get Spreadsheet
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        // 2. Open Data Viewer Controller and get starting time
        mainFrameFixture.clickMenuItemWithPath("Controller",
            "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(0, 100));

        DataControllerFixture dcf = new DataControllerFixture(
                mainFrameFixture.robot,
                (DataControllerV) mainFrameFixture.dialog().component());

        // 3. Confirm that Go Back text field is 00:00:05:000
        Assert.assertEquals("00:00:05:000",
            dcf.textBox("goBackTextField").text());
    }

    /**
     * Bug778.
     * If you are playing a movie, and you shuttle backwards (such that you
     * have a negative speed), your speed hits 0 when you reach the start of
     * the file. The stored shuttle speed does not get reset to zero though,
     * resulting in multiple forward shuttle presses being necessary to get
     * a positive playback speed again.
     */
    /*@Test*/ public void testBug778() throws IOException {
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

        vidWindow.resizeHeightTo(600 + vid.getInsets().bottom
            + vid.getInsets().top);
        vid.setAlwaysOnTop(true);

        File refImageFile = new File(root + "/ui/head_turns600h0t.png");

        BufferedImage vidImage = UIImageUtils.captureAsScreenshot(vid);
        Assert.assertTrue(UIImageUtils.areImagesEqual(vidImage,
                refImageFile));

        // 3. Play movie for 5 seconds
        dcf.pressPlayButton();

        // Using Thread.sleep to wait for 5 seconds.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UIDataControllerTest.class.getName()).log(
                Level.SEVERE, null, ex);
        }

        // 3. Press shuttle back 7 times and ensure its negative
        for (int i = 0; i < 7; i++) {
            dcf.pressShuttleBackButton();
        }

        Assert.assertEquals(dcf.getSpeed(), "-2");

        // Wait 2 seconds
        // Using Thread.sleep to wait for 2 seconds.
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UIDataControllerTest.class.getName()).log(
                Level.SEVERE, null, ex);
        }

        // 4. Check that speed has returned to 0 and time is 0
        Assert.assertEquals(dcf.getCurrentTime(), "00:00:00:000");
        vidImage = UIImageUtils.captureAsScreenshot(vid);
        Assert.assertTrue(UIImageUtils.areImagesEqual(vidImage,
                refImageFile));

        Assert.assertEquals(dcf.getSpeed(), "0");

        // 5. Press forward shuttle once and confirm that it's positive
        dcf.pressShuttleForwardButton();
        Assert.assertEquals(dcf.getSpeed(), "1/32");
    }

    /**
     * Bug794.
     * Steps to reproduce: open a movie, shuttle forwards to a rate of say 4x,
     * pause the movie. Now instead of pressing unpause (as you might normally
     * do), press shuttle forward again. I often see this going to 1/16x for
     * some reason.
     */
    /*@Test*/ public void testBug794() {
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
            Logger.getLogger(UIDataControllerTest.class.getName()).log(
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
     * Bug798.
     * Set playback speed to any value, using say shuttle to 4x.
     * Pause the movie. Now rewind it past zero (causing a forced stop).
     * Pressing the pause/unpause button will now restore the saved speed;
     * this is bad! If you for example save a negative playback speed,
     * pause/unpause will not work at all. To reproduce this behaviour:
     * play the movie as per normal, then shuttle to a negative speed.
     * Pause the movie. Rewind past zero (forcing a stop).
     * Unpause/play the movie, voila, cannot play the movie
     * using that button anymore.
     */
    /*@Test*/ public void testBug798() throws IOException {
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
        vidWindow.resizeHeightTo(600 + vid.getInsets().bottom
            + vid.getInsets().top);
        vid.setAlwaysOnTop(true);

        File refImageFile = new File(root + "/ui/head_turns600h0t.png");

        BufferedImage vidImage = UIImageUtils.captureAsScreenshot(vid);
        Assert.assertTrue(UIImageUtils.areImagesEqual(vidImage,
                refImageFile));

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
            Logger.getLogger(UIDataControllerTest.class.getName()).log(
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
            Logger.getLogger(UIDataControllerTest.class.getName()).log(
                Level.SEVERE, null, ex);
        }

        // Check that its 0 time and speed
        Assert.assertEquals(dcf.getCurrentTime(), "00:00:00:000");
        Assert.assertEquals(dcf.getSpeed(), "0");
        vidImage = UIImageUtils.captureAsScreenshot(vid);
        Assert.assertTrue(UIImageUtils.areImagesEqual(vidImage,
                refImageFile));

        // 5. Press pause and ensure it does nothing
        dcf.pressPauseButton();
        Assert.assertEquals(dcf.getCurrentTime(), "00:00:00:000");
        Assert.assertEquals(dcf.getSpeed(), "0");
        vidImage = UIImageUtils.captureAsScreenshot(vid);
        Assert.assertTrue(UIImageUtils.areImagesEqual(vidImage,
                refImageFile));
    }

    /**
     * Bug464.
     * When a video finishes playing, hitting play does nothing.
     * I expected it to play again.
     */
    @Test public void testBug464() throws Exception {
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
        QTDataViewer vid = ((QTDataViewer) it.next());


        FrameFixture vidWindow = new FrameFixture(mainFrameFixture.robot, vid);


        vidWindow.moveTo(new Point(dcf.component().getWidth() + 10, 100));

        vidWindow.resizeHeightTo(600 + vid.getInsets().bottom
            + vid.getInsets().top);
        vid.setAlwaysOnTop(true);
        vid.seekTo(0);

        File refImageFile = new File(root + "/ui/head_turns600h0t.png");

        BufferedImage vidImage = UIImageUtils.captureAsScreenshot(vid);
        Assert.assertTrue(UIImageUtils.areImagesEqual(vidImage,
                refImageFile, 0.14, 0.1));

        // 2. Fast forward video to end and confirm you've reached end (1min)
        dcf.pressFastForwardButton();

        // Using Thread.sleep to wait for 5 seconds.
        try {
            Thread.sleep(8000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UIDataControllerTest.class.getName()).log(
                Level.SEVERE, null, ex);
        }

        // Check time
        Assert.assertEquals(dcf.getCurrentTime(), "00:01:00:000");

        vid.setVisible(true);
        vid.toFront();
        refImageFile = new File(root + "/ui/head_turns600h1mt.png");
        vidImage = UIImageUtils.captureAsScreenshot(vid);
        Assert.assertTrue(UIImageUtils.areImagesEqual(vidImage,
                refImageFile, 0.14, 0.1));

        // 3. Press play, should start playing again
        dcf.pressPlayButton();

        String currTime = dcf.getCurrentTime();

        try {
            TimeStamp currTS = new TimeStamp(currTime);
            TimeStamp oneMin = new TimeStamp("00:01:00:000");
            Assert.assertTrue(currTS.le(oneMin));
            vidImage = UIImageUtils.captureAsScreenshot(vid);
            dcf.pressPauseButton();
            Assert.assertFalse(UIImageUtils.areImagesEqual(vidImage,
                    refImageFile, 0.02, 0.1));
        } catch (SystemErrorException ex) {
            Logger.getLogger(UIDataControllerTest.class.getName()).log(
                Level.SEVERE, null, ex);
        }
    }

    /**
     * Bug1204.
     * Steps to recreate:
     * Create a new cell using NUM_0.
     * Delete cell
     * Create a new cell to replace deleted cell using NUM_0
     *
     * Expect: New cell created.
     * Actual: Dang nabbit error
     */
    /*Possible EDT issues@Test*/ public void testBug1204() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Get Spreadsheet
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        // 2. Open Data Viewer Controller
        mainFrameFixture.clickMenuItemWithPath("Controller",
            "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(300, 300));

        final DataControllerFixture dcf = new DataControllerFixture(
                mainFrameFixture.robot,
                (DataControllerV) mainFrameFixture.dialog().component());

        // 3. Create a new variable
        UIUtils.createNewVariable(mainFrameFixture, "t",
            UIUtils.VAR_TYPES[(int) (Math.random() * UIUtils.VAR_TYPES.length)]);

        // 3. Open video
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

        // Get video window
        Iterator it = dcf.getDataViewers().iterator();

        Frame vid = ((Frame) it.next());
        FrameFixture vidWindow = new FrameFixture(mainFrameFixture.robot, vid);

        vidWindow.moveTo(new Point(dcf.component().getWidth() + 10, 300));

        // 5. Play video then create a new cell using Num0
        // Play video
        dcf.pressPlayButton();

        // 4. Create a new cell using Num0
        // The first line is really just to delay things.
        ssPanel.column("t").click();
        ssPanel.column("t").pressAndReleaseKeys(KeyEvent.VK_NUMPAD0);

        // Check that cell exists
        Assert.assertEquals(ssPanel.column("t").allCells().size(), 1);

        // 5. Delete cell
        ssPanel.column(0).cell(1).borderSelectCell(true);
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Delete Cell");

        jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        ssPanel = new SpreadsheetPanelFixture(mainFrameFixture.robot,
                (SpreadsheetPanel) jPanel.component());

        // Check deleted
        Assert.assertEquals(ssPanel.column("t").allCells().size(), 0);

        // 6. Create cell with NUM0
        ssPanel.column("t").click();
        ssPanel.column("t").pressAndReleaseKeys(KeyEvent.VK_NUMPAD0);

        // Check that cell exists
        Assert.assertEquals(ssPanel.column("t").allCells().size(), 1);
    }

    /**
     * Bug891.
     * Set New Cell Offset changes offset of selected cell rather than
     * last created cell
     */
    /*BugzID1652:@Test*/ public void testBug891() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Get Spreadsheet
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(
                mainFrameFixture.robot, (SpreadsheetPanel) jPanel.component());

        // 2. Open Data Viewer Controller
        mainFrameFixture.clickMenuItemWithPath("Controller",
            "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(300, 300));

        final DataControllerFixture dcf = new DataControllerFixture(
                mainFrameFixture.robot,
                (DataControllerV) mainFrameFixture.dialog().component());

        // 3. Open video
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

        // Get video window
        Iterator it = dcf.getDataViewers().iterator();

        Frame vid = ((Frame) it.next());
        FrameFixture vidWindow = new FrameFixture(mainFrameFixture.robot, vid);

        vidWindow.moveTo(new Point(dcf.component().getWidth() + 10, 300));

        // 4. Create a new variable
        UIUtils.createNewVariable(mainFrameFixture, "p",
            UIUtils.VAR_TYPES[(int) (Math.random() * UIUtils.VAR_TYPES.length)]);

        // 5. Play video then create a new cell using Num0
        // Play video
        dcf.pressPlayButton();

        // Create new cell
        ssPanel.column("p").click();
        ssPanel.column("p").pressAndReleaseKeys(KeyEvent.VK_NUMPAD0);

        // Check that cell exists
        Assert.assertEquals(ssPanel.column("p").allCells().size(), 1);

        // 6. Create another cell in another column
        UIUtils.createNewVariable(mainFrameFixture, "q",
            UIUtils.VAR_TYPES[(int) (Math.random() * UIUtils.VAR_TYPES.length)]);
        ssPanel.column("q").click();
        ssPanel.column("q").pressAndReleaseKeys(KeyEvent.VK_NUMPAD0);

        // Check that cell exists
        Assert.assertEquals(ssPanel.column("q").allCells().size(), 1);

        // 7. Pause video
        dcf.pressPauseButton();

        // 8. Select first cell and press Set New Cell Offset
        String offsetTime = dcf.getCurrentTime();

        SpreadsheetCellFixture firstCell = ssPanel.column("p").cell(1);
        firstCell.borderSelectCell(true);
        Assert.assertEquals(firstCell.offsetTimestamp().text(), "00:00:00:000");

        SpreadsheetCellFixture secondCell = ssPanel.column("q").cell(1);
        Assert.assertEquals(secondCell.offsetTimestamp().text(),
            "00:00:00:000");

        Assert.assertTrue(firstCell.isSelected());
        dcf.pressSetNewCellOffsetButton();
        Assert.assertEquals(firstCell.offsetTimestamp().text(), "00:00:00:000");
        Assert.assertEquals(secondCell.offsetTimestamp().text(),
            offsetTime);

    }
}
