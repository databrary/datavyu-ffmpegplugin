package org.openshapa.uitests;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.util.concurrent.TimeUnit;

import junitx.util.PrivateAccessor;

import org.fest.swing.annotation.GUITest;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.OpenSHAPAFrameFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.launcher.ApplicationLauncher;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;

import org.openshapa.Configuration;
import org.openshapa.OpenSHAPA;

import org.openshapa.util.ConfigProperties;

import org.openshapa.views.DataControllerV;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;


/**
 * GUI Test class for OpenSHAPA. All OpenSHAPA Fest tests must extend this
 * class.
 */
@GUITest public class OpenSHAPATestClass {

    static {

        try {

            ConfigProperties p = (ConfigProperties) PrivateAccessor.getField(
                    Configuration.getInstance(), "properties");
            p.setCanSendLogs(false);
        } catch (Exception e) {
            System.err.println("Unable to overide sending usage logs");
        }
    }

    /** Main Frame fixture for use by all tests. */
    protected OpenSHAPAFrameFixture mainFrameFixture;

    /** Spreadsheet for use by all tests. */
    protected SpreadsheetPanelFixture spreadsheet;

    /** Test folder location. */
    protected String testFolder = System.getProperty("testPath");

    /** Temp folder location. */
    protected String tempFolder = System.getProperty("java.io.tmpdir");

    /** Constructor nulls the mainFrame Fixture. */
    public OpenSHAPATestClass() {
        mainFrameFixture = null;
    }

    /**
     * Starts openSHAPA.
     */
    @BeforeSuite protected final void startApplication() {
        System.err.println("Starting Application.");

        OpenSHAPAFrameFixture fixture;

        // Launch OpenSHAPA, this happens once per test class.
        ApplicationLauncher.application(OpenSHAPA.class).start();
        fixture = new OpenSHAPAFrameFixture("mainFrame");
        fixture.robot.waitForIdle();
        fixture.requireVisible();
        fixture.maximize();
        fixture.moveToFront();

        OpenSHAPAInstance.setFixture(fixture);

        // Close the data controller
        fixture.dialog(DataControllerV.class.getSimpleName()).close();

        // ScreenshotOnFailureListener sofl = new ScreenshotOnFailureListener();
    }

    /**
     * Restarts the application between tests. Achieves this by using File->New
     */
    @AfterMethod protected final void restartApplication() {
        System.err.println("restarting Application.");

        OpenSHAPA.getApplication().closeOpenedWindows();

        mainFrameFixture = OpenSHAPAInstance.getFixture();

        // Try and close any filechoosers that are open
        try {
            JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
            jfcf.cancel();
        } catch (Exception e) {
            // Do nothing
        }

        // Create a new project, this is for the discard changes dialog.
        if (Platform.isOSX()) {
            OpenSHAPA.getView().showNewProjectForm();
            // mainFrameFixture.pressAndReleaseKey(KeyPressInfo.keyCode(
            // KeyEvent.VK_N).modifiers(InputEvent.META_MASK));
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "New");
        }

        try {
            JOptionPaneFixture warning = mainFrameFixture.optionPane();
            warning.requireTitle("Unsaved changes");
            warning.buttonWithText("OK").click();
        } catch (Exception e) {
        }

        // Get New Database dialog
        DialogFixture newProjectDialog = mainFrameFixture.dialog();
        newProjectDialog.textBox("nameField").enterText("n");
        newProjectDialog.button("okButton").click();

        // Set common variables
        // Get Spreadsheet
        spreadsheet = mainFrameFixture.getSpreadsheet();

        // Close the data controller
        mainFrameFixture.dialog(DataControllerV.class.getSimpleName()).close();
    }

    /** Releases application after all tests in suite are finished. */
    @AfterSuite public final void endApplication() {
        mainFrameFixture.cleanUp();
    }

    /** Gets the OpenSHAPA Instance for each test. */
    @BeforeClass public final void newTestClass() {
        System.err.println("Class starting");

        if (mainFrameFixture == null) {
            mainFrameFixture = OpenSHAPAInstance.getFixture();
        }
    }

    public void printTestName() {
        System.err.println(Thread.currentThread().getStackTrace()[2]
            .getMethodName());
    }
}
