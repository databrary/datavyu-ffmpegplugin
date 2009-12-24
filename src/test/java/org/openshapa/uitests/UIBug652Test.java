package org.openshapa.uitests;

import java.io.File;
import org.openshapa.util.UIUtils;
import org.uispec4j.interception.WindowInterceptor;
import org.uispec4j.MenuBar;
import org.uispec4j.OpenSHAPAUISpecTestCase;
import org.uispec4j.Panel;
import org.uispec4j.Trigger;
import org.uispec4j.VocabElement;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;

/**
 * Test for Bug 652.
 * Bug 652:
 * Everytime VocEditor window is reopened, the predicate counter goes back to 1
 *
 */
public final class UIBug652Test extends OpenSHAPAUISpecTestCase {
    /** Test for closing window and creating new predicate.
     * @throws java.lang.Exception on any error
     */
    public void testNewPredicateAfterClose() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Create a new predicate
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        vocEdWindow.getButton("Add Predicate()").click();

        VocabElement ve = UIUtils.getVocabElements(vocElementsPanel)[0];

        String [] veName = new String[2];
        veName[0] = ve.getVEName();
        assertTrue(veName[0].equals("predicate1"));
        vocEdWindow.getButton("OK").click();


        // 2. Create new predicate, check number has been incremented
        vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());
        vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        vocEdWindow.getButton("Add Predicate()").click();

        ve = UIUtils.getVocabElements(vocElementsPanel)[1];

        veName[1] = ve.getVEName();
        assertTrue(veName[1].equals("predicate2"));
        vocEdWindow.getButton("OK").click();
    }

    /** Test for running script, then creating predicates.
     * @throws java.lang.Exception on any error
     */
    public void testNewPredicateAfterScript() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Run script to populate
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsOpenDialog()
                    .assertAcceptsFilesOnly()
                    .select(demoFile.getAbsolutePath()))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        // 2. Create a new predicate
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        vocEdWindow.getButton("Add Predicate()").click();

        VocabElement ve = UIUtils.getVocabElements(vocElementsPanel)[2];

        String [] veName = new String[2];
        veName[0] = ve.getVEName();
        //Seems to behin at "predicate2", which is acceptable
        assertTrue(veName[0].equals("predicate2"));
        vocEdWindow.getButton("OK").click();


        // 3. Create new predicate, check number has been incremented
        vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());
        vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        vocEdWindow.getButton("Add Predicate()").click();

        ve = UIUtils.getVocabElements(vocElementsPanel)[3];

        veName[1] = ve.getVEName();
        assertTrue(veName[1].equals("predicate3"));
        vocEdWindow.getButton("OK").click();
    }
}
