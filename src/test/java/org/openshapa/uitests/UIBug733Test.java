package org.openshapa.uitests;

import java.awt.Frame;
import java.io.IOException;

import static org.fest.reflect.core.Reflection.method;

import java.awt.Point;
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
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;
import org.openshapa.util.UIImageUtils;

import org.openshapa.util.UIUtils;

import org.openshapa.views.DataControllerV;
import org.openshapa.views.OpenSHAPAFileChooser;
import org.openshapa.views.continuous.PluginManager;
import org.openshapa.views.discrete.SpreadsheetPanel;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Bug733 Description: test that aspect ratio of window remains the same after
 * resize.
 */
public final class UIBug733Test extends OpenSHAPATestClass {

    /**
     * Test Bug 733.
     */
    @Test public void testBug733() throws IOException {
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

        // 2. Get window
        Iterator it = dcf.getDataViewers().iterator();

        Frame vid = ((Frame) it.next());
        FrameFixture vidWindow = new FrameFixture(mainFrameFixture.robot, vid);

        vidWindow.moveTo(new Point(dcf.component().getWidth() + 10, 100));

        vidWindow.resizeHeightTo(600);
        vid.setAlwaysOnTop(true);

        File refImageFile = new File(root + "/ui/head_turns600h0t.png");
        vid.toFront();
        BufferedImage vidImage = UIImageUtils.captureAsScreenshot(
                vid);
        Assert.assertTrue(UIImageUtils.areImagesEqual(vidImage,
                refImageFile));

        // 3. Get aspect window dimensions
        int beforeResizeWidth = vidWindow.component().getWidth();
        int beforeResizeHeight = vidWindow.component().getHeight();

        //4. Make window a quarter height
        vidWindow.resizeHeightTo(beforeResizeHeight / 4);

        //a. Check that ratio remains the same
        refImageFile = new File(root + "/ui/head_turns150h0t.png");
        vid.toFront();
        vidImage = UIImageUtils.captureAsScreenshot(
                vid);
        Assert.assertTrue(UIImageUtils.areImagesEqual(vidImage,
                refImageFile));

        Assert.assertTrue(Math.abs(
                vidWindow.component().getWidth() - (beforeResizeWidth / 4)) < 3,
            "" +
            Math.abs(
                vidWindow.component().getWidth() - (beforeResizeWidth / 4)));

        //5. Make window a triple height
        beforeResizeWidth = vidWindow.component().getWidth();
        beforeResizeHeight = vidWindow.component().getHeight();
        vidWindow.resizeHeightTo(beforeResizeHeight * 3);

        //a. Check that ratio remains the same
        refImageFile = new File(root + "/ui/head_turns450h0t.png");
        vid.toFront();
        vidImage = UIImageUtils.captureAsScreenshot(
                vid);
        Assert.assertTrue(UIImageUtils.areImagesEqual(vidImage,
                refImageFile));

        Assert.assertTrue(Math.abs(
                vidWindow.component().getWidth() - (beforeResizeWidth * 3)) < 3,
            "" +
            Math.abs(
                vidWindow.component().getWidth() - (beforeResizeWidth * 3)));

        /* BugzID:1452
        //6. Make window half the width
        beforeResizeWidth = vidWindow.component().getWidth();
        beforeResizeHeight = vidWindow.component().getHeight();
        vidWindow.resizeWidthTo(beforeResizeWidth / 2);
        //a. Check that ratio remains the same
        Assert.assertTrue(Math.abs(
               vidWindow.component().getHeight() - beforeResizeHeight / 2) < 3);

        //7. Make window double the width
        beforeResizeWidth = vidWindow.component().getWidth();
        beforeResizeHeight = vidWindow.component().getHeight();
        vidWindow.resizeWidthTo(beforeResizeWidth * 2);
        //a. Check that ratio remains the same
        Assert.assertTrue(Math.abs(
               vidWindow.component().getHeight() - beforeResizeHeight * 2) < 3);
         */


    }
}
