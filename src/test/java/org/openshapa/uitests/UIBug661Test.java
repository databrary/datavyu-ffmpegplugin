package org.openshapa.uitests;

import java.io.File;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.uispec4j.MenuBar;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;

/**
 * Bug 661 Test
 * Make sure that the open dialog remembers previous open location.
 */
public final class UIBug661Test extends UISpecTestCase {

    /**
     * Initialiser called before each unit test.
     *
     * @throws java.lang.Exception When unable to initialise test
     */
    @Override
    protected void setUp() throws Exception {
        /*
        super.setUp();
        setAdapter(new MainClassAdapter(OpenSHAPA.class, new String[0]));
         */
    }

     /**
     * Called after each test.
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        getMainWindow().dispose();
        super.tearDown();
    }

    static {
      UISpec4J.init();
    }

    /**
     * Open a file and maybe check what open dialog opens to.
     * @param openFile file to open
     * @param currDirectory currDirectory or null if not testing
     * @throws Exception on any error
     */
    private void openFileAndTest(final String openFile,
            final File currDirectory) throws Exception {
        // 1. Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        String root = System.getProperty("testPath") + "ui/";
        File openCSV = new File(root + openFile);
        assertTrue(openCSV.exists());

        // 2. Open file in new location
        if (currDirectory == null) {
            WindowInterceptor
                    .init(menuBar.getMenu("File").getSubMenu("Open...")
                    .triggerClick())
                    .process(FileChooserHandler.init()
                        .assertIsOpenDialog()
                        .select(openCSV))
                    .run();
        } else {
            WindowInterceptor
                    .init(menuBar.getMenu("File").getSubMenu("Open...")
                    .triggerClick())
                    .process(FileChooserHandler.init()
                        .assertIsOpenDialog()
                        .assertCurrentDirEquals(currDirectory)
                        .select(openCSV))
                    .run();
        }
    }

    /**
     * Tests open dialog location.
     * @throws Exception on any error
     */
    public void testOpenDialogLocation() throws Exception {
        /*
        String root = System.getProperty("testPath");
        File location1 = new File(root + "ui/location1");
        File location2 = new File(root + "ui/location2");

        openFileAndTest("location1/test.shapa", null);
        //At this point it should remember location1
        openFileAndTest("location2/test.shapa", location1);
        //At this point it should remember location2
        openFileAndTest("location1/test.shapa", location2);
         */
    }
}