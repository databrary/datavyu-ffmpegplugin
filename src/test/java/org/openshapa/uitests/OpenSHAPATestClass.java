/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

package org.openshapa.uitests;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.launcher.ApplicationLauncher;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.NewProjectV;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

//@GUITest
public class OpenSHAPATestClass {

    protected FrameFixture mainFrameFixture;

    public OpenSHAPATestClass() {
        mainFrameFixture = null;
    }

    @BeforeSuite
    public void startApplication() {
        System.err.println("Starting Application.");
        FrameFixture fixture;

        // Launch OpenSHAPA, this happens once per test class.
        ApplicationLauncher.application(OpenSHAPA.class).start();
        fixture = new FrameFixture("mainFrame");
        fixture.robot.waitForIdle();
        fixture.requireVisible();
        fixture.maximize();
        fixture.moveToFront();

        OpenSHAPAInstance.setFixture(fixture);
        // ScreenshotOnFailureListener sofl = new ScreenshotOnFailureListener();
    }

    @AfterMethod
    public void restartApplication() {
        System.err.println("restarting Application.");
        FrameFixture mainFrameFixture = OpenSHAPAInstance.getFixture();

        // Create a new project, this is for the discard changes dialog.
        if (Platform.isOSX()) {
            mainFrameFixture.pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_N).modifiers(KeyEvent.META_MASK));
        } else {
            mainFrameFixture.menuItemWithPath("File", "New").click();
        }

        try {
            JOptionPaneFixture warning = mainFrameFixture.optionPane();
            warning.requireTitle("Unsaved changes");
            warning.buttonWithText("OK").click();
        } catch (Exception e) {
            // Do nothing
        }

//         DialogFixture newDatabaseDialog = mainFrameFixture.dialog();

        // Get New Database dialog
        DialogFixture newDatabaseDialog = mainFrameFixture.dialog(
                new GenericTypeMatcher<JDialog>(JDialog.class) {
                    @Override
                    protected boolean isMatching(JDialog dialog) {
                        return dialog.getClass().equals(NewProjectV.class);
                    }
                }, Timeout.timeout(5, TimeUnit.SECONDS));

        newDatabaseDialog.textBox("nameField").enterText("n");

        newDatabaseDialog.button("okButton").click();
    }

    @AfterSuite
    public void endApplication() {
        mainFrameFixture.cleanUp();
    }

    @BeforeClass
    public void newTestClass() {
        System.err.println("Class starting");
        if (mainFrameFixture == null) {
            mainFrameFixture = OpenSHAPAInstance.getFixture();
        }
    }
}
