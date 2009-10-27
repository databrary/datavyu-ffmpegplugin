package org.openshapa.uitests;

import java.io.File;
import java.util.Vector;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.openshapa.views.discrete.datavalues.vocabelements.VocabElementV;
import org.uispec4j.Cell;
import org.uispec4j.Key;
import org.uispec4j.KeyItem;
import org.uispec4j.MenuBar;
import org.uispec4j.Panel;
import org.uispec4j.Spreadsheet;
import org.uispec4j.StringItem;
import org.uispec4j.TextBox;
import org.uispec4j.TextItem;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.VocabElement;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;

/**
 * Test the creation of a new database.
 *
 */
public final class UIVocabEditorTest extends UISpecTestCase {

    /**
     * Initialiser called before each unit test.
     *
     * @throws java.lang.Exception When unable to initialise test
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setAdapter(new MainClassAdapter(OpenSHAPA.class, new String[0]));
    }

    static {
        UISpec4J.init();
    }

    /** Test vocab editor is being populated.
     * @throws java.lang.Exception on any error
     */
    public void testLoading() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Check that vocab editor is not populated
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");


        int numVocElements = vocElementsPanel.getUIComponents(
                VocabElement.class).length;

        assertTrue(numVocElements == 0);

        //Close vocab editor window - BugzID641
        vocEdWindow.getButton("Close").click();

        // 2. Run script to populate
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


        // 3. Check that vocab editor is populated.
        vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");
        numVocElements = vocElementsPanel.getUIComponents(
                VocabElement.class).length;
        assertTrue(numVocElements == 2);
    }

    /** Test vocab editor creating new predicate.
     * @throws java.lang.Exception on any error
     */
    public void testNewPredicateNoEdit() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Create a new predicate
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        vocEdWindow.getButton("Add Predicate()").click();

        VocabElement ve = new VocabElement(((VocabElementV) (vocElementsPanel
            .getUIComponents(VocabElement.class)[0].getAwtComponent())));

        String veName = ve.getVEName();
        vocEdWindow.getButton("OK").click();

        // 2. Create new predicate variable and cell
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        String varName = "predicate";
        createNewVariable(varName, varName.toUpperCase());

        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();

        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        Cell c = cells.elementAt(0);
        TextBox t = c.getValue();

        Vector<TextItem> vti = new Vector<TextItem>();
        vti.add(new StringItem(veName));

        c.enterText(Cell.VALUE, vti);

        assertTrue(t.getText().equalsIgnoreCase("predicate1(<arg0>)"));
    }

    /** Test vocab editor creating new predicate and replacing VE name.
     * @throws java.lang.Exception on any error
     */
    public void testNewPredicateReplaceVEName() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Create a new predicate
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        vocEdWindow.getButton("Add Predicate()").click();

        VocabElement ve = new VocabElement(((VocabElementV) (vocElementsPanel
            .getUIComponents(VocabElement.class)[0].getAwtComponent())));

        String oldVEName = ve.getVEName();

        Vector<TextItem> vti = new Vector<TextItem>();

        vti.add(new StringItem("newName"));
        ve.replaceTextInName(vti);

        String veName = ve.getVEName();

        assertFalse(oldVEName.equals(veName));

        vocEdWindow.getButton("OK").click();

        // 2. Create new predicate variable and cell
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        String varName = "predicate";
        createNewVariable(varName, varName.toUpperCase());

        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();

        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        Cell c = cells.elementAt(0);
        TextBox t = c.getValue();

        c.enterText(Cell.VALUE, vti);

        assertTrue(t.getText().equalsIgnoreCase(veName + "(<arg0>)"));
    }

    /** Test vocab editor creating new predicate and adding to VE name.
     * @throws java.lang.Exception on any error
     */
    public void testNewPredicateAddingVEName() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Create a new predicate
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        vocEdWindow.getButton("Add Predicate()").click();

        VocabElement ve = new VocabElement(((VocabElementV) (vocElementsPanel
            .getUIComponents(VocabElement.class)[0].getAwtComponent())));

        String oldVEName = ve.getVEName();

        Vector<TextItem> vti = new Vector<TextItem>();

        vti.add(new StringItem("newName"));
        ve.enterText(vti);

        String veName = ve.getVEName();

        assertFalse(oldVEName.equals(veName));

        vocEdWindow.getButton("OK").click();

        // 2. Create new predicate variable and cell
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        String varName = "predicate";
        createNewVariable(varName, varName.toUpperCase());

        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();

        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        Cell c = cells.elementAt(0);
        TextBox t = c.getValue();

        vti.add(new StringItem("predicate1"));

        c.enterText(Cell.VALUE, vti);

        assertTrue(t.getText().equalsIgnoreCase(veName + "(<arg0>)"));
    }

    /** Test vocab editor creating new predicate and adding VE argument.
     * @throws java.lang.Exception on any error
     */
    public void testNewPredicateAddingVEArgument() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Create a new predicate
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        vocEdWindow.getButton("Add Predicate()").click();

        VocabElement ve = new VocabElement(((VocabElementV) (vocElementsPanel
            .getUIComponents(VocabElement.class)[0].getAwtComponent())));

        String oldVEArgName = ve.getArgument(0);

        Vector<TextItem> vti = new Vector<TextItem>();

        vti.add(new StringItem("newName"));
        ve.enterTextInArg(0, vti);

        String veArgName = ve.getArgument(0);

        assertFalse(oldVEArgName.equals(veArgName));

        vocEdWindow.getButton("OK").click();

        // 2. Create new predicate variable and cell
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        String varName = "predicate";
        createNewVariable(varName, varName.toUpperCase());

        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();

        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        Cell c = cells.elementAt(0);
        TextBox t = c.getValue();

        vti.clear();
        vti.add(new StringItem("predicate1"));

        c.enterText(Cell.VALUE, vti);

        assertTrue(t.getText().equalsIgnoreCase(
                "predicate1(<" + veArgName + ">)"));
    }

    /** Test vocab editor creating new predicate and replacing VE argument.
     * @throws java.lang.Exception on any error
     */
    public void testNewPredicateReplaceVEArgument() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Create a new predicate
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        vocEdWindow.getButton("Add Predicate()").click();

        VocabElement ve = new VocabElement(((VocabElementV) (vocElementsPanel
            .getUIComponents(VocabElement.class)[0].getAwtComponent())));

        String oldVEArgName = ve.getArgument(0);

        Vector<TextItem> vti = new Vector<TextItem>();

        vti.add(new StringItem("newName"));
        ve.replaceTextInArg(0, vti);

        String veArgName = ve.getArgument(0);

        assertFalse(oldVEArgName.equals(veArgName));

        vocEdWindow.getButton("OK").click();

        // 2. Create new predicate variable and cell
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        String varName = "predicate";
        createNewVariable(varName, varName.toUpperCase());

        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();

        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        Cell c = cells.elementAt(0);
        TextBox t = c.getValue();

        vti.clear();
        vti.add(new StringItem("predicate1"));

        c.enterText(Cell.VALUE, vti);

        assertTrue(t.getText().equalsIgnoreCase(
                "predicate1(<" + veArgName + ">)"));
    }

    /** Test vocab editor reverting with multiple changes.
     * @throws java.lang.Exception on any error
     */
    public void testRevertButton1() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //Test input
        String[] testInputArray = {"Subject stands )up ", "$10,432",
            "Hand me (the manual!", "Tote_that_bale", "Jeune; fille celebre",
            "If x>7 then x|2"};

        Vector<Vector<TextItem>> testInput = new Vector<Vector<TextItem>>();
        for (int i = 0; i < testInputArray.length; i++) {
            testInput.add(new Vector<TextItem>());
            testInput.lastElement().add(new StringItem(testInputArray[i]));
        }

        Vector<TextItem> returnHome = new Vector<TextItem>();
        returnHome.add(new KeyItem(Key.HOME));

        Vector<TextItem> backSpace = new Vector<TextItem>();
        backSpace.add(new KeyItem(Key.BACKSPACE));

        Vector<TextItem> deleteKey = new Vector<TextItem>();
        deleteKey.add(new KeyItem(Key.DELETE));

        Vector<TextItem> rightKey = new Vector<TextItem>();
        rightKey.add(new KeyItem(Key.RIGHT));

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


        // 2. Get current data.
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");
        int numVocElements = vocElementsPanel.getUIComponents(
                VocabElement.class).length;

        String [] originalData = new String[numVocElements];
        Vector<VocabElement> vve = new Vector<VocabElement>();
        for (int i = 0; i < numVocElements; i++) {
            vve.add(new VocabElement(((VocabElementV) (vocElementsPanel
                    .getUIComponents(VocabElement.class)[i]
                    .getAwtComponent()))));
            originalData[i] = vve.lastElement().getValueText();
        }

        // TEST 1: Make multiple addition changes
        for (VocabElement ve : vve) {
            ve.enterText(testInput.elementAt(4));
            ve.enterTextInArg(0, testInput.elementAt(0));
            ve.enterTextInArg(1, testInput.elementAt(1));
            ve.enterTextInArg(2, testInput.elementAt(2));
            ve.enterTextInArg(3, testInput.elementAt(3));
        }

        // Check that change occurred
        for (int i = 0; i < vve.size(); i++) {
            assertFalse(vve.elementAt(i).getValueText()
                    .equals(originalData[i]));
        }

        // Revert change
        vocEdWindow.getButton("Revert").click();

        // Check that change has been reverted
        for (int i = 0; i < vve.size(); i++) {
            assertTrue(vve.elementAt(i).getValueText()
                    .equals(originalData[i]));
        }
    }

    /** Test vocab editor reverting with single addition changes.
     * @throws java.lang.Exception on any error
     */
    public void testRevertButton2() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //Test input
        String[] testInputArray = {"Subject stands )up ", "$10,432",
            "Hand me (the manual!", "Tote_that_bale", "Jeune; fille celebre",
            "If x>7 then x|2"};

        Vector<Vector<TextItem>> testInput = new Vector<Vector<TextItem>>();
        for (int i = 0; i < testInputArray.length; i++) {
            testInput.add(new Vector<TextItem>());
            testInput.lastElement().add(new StringItem(testInputArray[i]));
        }

        Vector<TextItem> returnHome = new Vector<TextItem>();
        returnHome.add(new KeyItem(Key.HOME));

        Vector<TextItem> backSpace = new Vector<TextItem>();
        backSpace.add(new KeyItem(Key.BACKSPACE));

        Vector<TextItem> deleteKey = new Vector<TextItem>();
        deleteKey.add(new KeyItem(Key.DELETE));

        Vector<TextItem> rightKey = new Vector<TextItem>();
        rightKey.add(new KeyItem(Key.RIGHT));

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


        // 2. Get current data.
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");
        int numVocElements = vocElementsPanel.getUIComponents(
                VocabElement.class).length;

        String [] originalData = new String[numVocElements];
        Vector<VocabElement> vve = new Vector<VocabElement>();
        for (int i = 0; i < numVocElements; i++) {
            vve.add(new VocabElement(((VocabElementV) (vocElementsPanel
                    .getUIComponents(VocabElement.class)[i]
                    .getAwtComponent()))));
            originalData[i] = vve.lastElement().getValueText();
        }

        // TEST 2: Make single addition changes
        for (VocabElement ve : vve) {
            ve.enterText(returnHome);
            ve.enterText(testInput.elementAt(4));
        }
        // Check that change occurred
        for (int i = 0; i < vve.size(); i++) {
            assertFalse(vve.elementAt(i).getValueText()
                    .equals(originalData[i]));
        }
        // Revert change
        vocEdWindow.getButton("Revert").click();
        // Check that change has been reverted
        for (int i = 0; i < vve.size(); i++) {
            assertTrue(vve.elementAt(i).getValueText()
                    .equals(originalData[i]));
        }

        for (int j = 0; j < 4; j++) {
            for (VocabElement ve : vve) {
                ve.enterTextInArg(j, testInput.elementAt(j));
            }
            // Check that change occurred
            for (int i = 0; i < vve.size(); i++) {
                assertFalse(vve.elementAt(i).getValueText()
                        .equals(originalData[i]));
            }
            // Revert change
            vocEdWindow.getButton("Revert").click();
            // Check that change has been reverted
            for (int i = 0; i < vve.size(); i++) {
                assertTrue(vve.elementAt(i).getValueText()
                        .equals(originalData[i]));
            }
        }

        // TEST 3: Make single replacement changes
        for (VocabElement ve : vve) {
            ve.enterText(returnHome);
            ve.replaceTextInName(testInput.elementAt(4));
        }
        // Check that change occurred
        for (int i = 0; i < vve.size(); i++) {
            assertFalse(vve.elementAt(i).getValueText()
                    .equals(originalData[i]));
        }
        // Revert change
        vocEdWindow.getButton("Revert").click();
        // Check that change has been reverted
        for (int i = 0; i < vve.size(); i++) {
            assertTrue(vve.elementAt(i).getValueText()
                    .equals(originalData[i]));
        }

        for (int j = 0; j < 4; j++) {
            for (VocabElement ve : vve) {
                ve.replaceTextInArg(j, testInput.elementAt(j));
            }
            // Check that change occurred
            for (int i = 0; i < vve.size(); i++) {
                assertFalse(vve.elementAt(i).getValueText()
                        .equals(originalData[i]));
            }
            // Revert change
            vocEdWindow.getButton("Revert").click();
            // Check that change has been reverted
            for (int i = 0; i < vve.size(); i++) {
                assertTrue(vve.elementAt(i).getValueText()
                        .equals(originalData[i]));
            }
        }
    }

    /** Test vocab editor reverting with standard deleting.
     * @throws java.lang.Exception on any error
     */
    public void testRevertButton3a() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //Test input
        String[] testInputArray = {"Subject stands )up ", "$10,432",
            "Hand me (the manual!", "Tote_that_bale", "Jeune; fille celebre",
            "If x>7 then x|2"};

        Vector<Vector<TextItem>> testInput = new Vector<Vector<TextItem>>();
        for (int i = 0; i < testInputArray.length; i++) {
            testInput.add(new Vector<TextItem>());
            testInput.lastElement().add(new StringItem(testInputArray[i]));
        }

        Vector<TextItem> returnHome = new Vector<TextItem>();
        returnHome.add(new KeyItem(Key.HOME));

        Vector<TextItem> backSpace = new Vector<TextItem>();
        backSpace.add(new KeyItem(Key.BACKSPACE));

        Vector<TextItem> deleteKey = new Vector<TextItem>();
        deleteKey.add(new KeyItem(Key.DELETE));

        Vector<TextItem> rightKey = new Vector<TextItem>();
        rightKey.add(new KeyItem(Key.RIGHT));

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


        // 2. Get current data.
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");
        int numVocElements = vocElementsPanel.getUIComponents(
                VocabElement.class).length;

        String [] originalData = new String[numVocElements];
        Vector<VocabElement> vve = new Vector<VocabElement>();
        for (int i = 0; i < numVocElements; i++) {
            vve.add(new VocabElement(((VocabElementV) (vocElementsPanel
                    .getUIComponents(VocabElement.class)[i]
                    .getAwtComponent()))));
            originalData[i] = vve.lastElement().getValueText();
        }

       // TEST 3a: Delete: delete key all
        for (VocabElement ve : vve) {
            ve.enterText(returnHome);
            for (int i = 0; i < ve.getVEName().length(); i++) {
                ve.enterText(deleteKey);
            }
        }
        // Check that change occurred
        for (int i = 0; i < vve.size(); i++) {
            assertFalse(vve.elementAt(i).getValueText()
                    .equals(originalData[i]));
        }
        // Revert change
        vocEdWindow.getButton("Revert").click();
        // Check that change has been reverted
        for (int i = 0; i < vve.size(); i++) {
            assertTrue(vve.elementAt(i).getValueText()
                    .equals(originalData[i]));
        }

        for (int j = 0; j < 4; j++) {
            for (VocabElement ve : vve) {
                for (int i = 0; i < ve.getArgument(j).length(); i++) {
                    ve.enterTextInArg(j, deleteKey);
                }
            }
            // Check that change occurred
            for (int i = 0; i < vve.size(); i++) {
                assertFalse(vve.elementAt(i).getValueText()
                        .equals(originalData[i]));
            }
            // Revert change
            vocEdWindow.getButton("Revert").click();
            // Check that change has been reverted
            for (int i = 0; i < vve.size(); i++) {
                assertTrue(vve.elementAt(i).getValueText()
                        .equals(originalData[i]));
            }
        }

        // TEST 3b: Delete: backspace all
        for (VocabElement ve : vve) {
            ve.enterText(returnHome);
            for (int i = 0; i < ve.getVEName().length(); i++) {
                ve.enterText(rightKey);
            }
            for (int i = 0; i < ve.getVEName().length(); i++) {
                ve.enterText(backSpace);
            }
        }
        // Check that change occurred
        for (int i = 0; i < vve.size(); i ++) {
            assertFalse(vve.elementAt(i).getValueText()
                    .equals(originalData[i]));
        }
        // Revert change
        vocEdWindow.getButton("Revert").click();
        // Check that change has been reverted
        for (int i = 0; i < vve.size(); i ++) {
            assertTrue(vve.elementAt(i).getValueText()
                    .equals(originalData[i]));
        }

        for (int j = 0; j < 4; j++) {
            for (VocabElement ve : vve) {
                for (int i = 0; i < ve.getArgument(j).length(); i++) {
                    ve.enterTextInArg(j, rightKey);
                }
                for (int i = 0; i < ve.getArgument(j).length(); i++) {
                    ve.enterText(backSpace);
                }
            }
            // Check that change occurred
            for (int i = 0; i < vve.size(); i++) {
                assertFalse(vve.elementAt(i).getValueText()
                        .equals(originalData[i]));
            }
            // Revert change
            vocEdWindow.getButton("Revert").click();
            // Check that change has been reverted
            for (int i = 0; i < vve.size(); i++) {
                assertTrue(vve.elementAt(i).getValueText()
                        .equals(originalData[i]));
            }
        }
    }

    /** Test vocab editor reverting with select all deleting.
     * @throws java.lang.Exception on any error
     */
    public void testRevertButton3b() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //Test input
        String[] testInputArray = {"Subject stands )up ", "$10,432",
            "Hand me (the manual!", "Tote_that_bale", "Jeune; fille celebre",
            "If x>7 then x|2"};

        Vector<Vector<TextItem>> testInput = new Vector<Vector<TextItem>>();
        for (int i = 0; i < testInputArray.length; i++) {
            testInput.add(new Vector<TextItem>());
            testInput.lastElement().add(new StringItem(testInputArray[i]));
        }

        Vector<TextItem> returnHome = new Vector<TextItem>();
        returnHome.add(new KeyItem(Key.HOME));

        Vector<TextItem> backSpace = new Vector<TextItem>();
        backSpace.add(new KeyItem(Key.BACKSPACE));

        Vector<TextItem> deleteKey = new Vector<TextItem>();
        deleteKey.add(new KeyItem(Key.DELETE));

        Vector<TextItem> rightKey = new Vector<TextItem>();
        rightKey.add(new KeyItem(Key.RIGHT));

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


        // 2. Get current data.
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");
        int numVocElements = vocElementsPanel.getUIComponents(
                VocabElement.class).length;

        String [] originalData = new String[numVocElements];
        Vector<VocabElement> vve = new Vector<VocabElement>();
        for (int i = 0; i < numVocElements; i++) {
            vve.add(new VocabElement(((VocabElementV) (vocElementsPanel
                    .getUIComponents(VocabElement.class)[i]
                    .getAwtComponent()))));
            originalData[i] = vve.lastElement().getValueText();
        }

        /*BugzID:636// TEST 3c: Delete: select all delete key
        for (VocabElement ve : vve) {
            ve.replaceTextInName(deleteKey);
        }
        // Check that change occurred
        for (int i = 0; i < vve.size(); i ++) {
            assertFalse(vve.elementAt(i).getValueText().equals(originalData[i]));
        }
        // Revert change
        vocEdWindow.getButton("Revert").click();
        // Check that change has been reverted
        for (int i = 0; i < vve.size(); i ++) {
            assertTrue(vve.elementAt(i).getValueText().equals(originalData[i]));
        }

        for (int j = 0; j < 4; j++) {
            for (VocabElement ve : vve) {
                ve.replaceTextInArg(j, deleteKey);
            }
            // Check that change occurred
            for (int i = 0; i < vve.size(); i++) {
                assertFalse(vve.elementAt(i).getValueText().equals(originalData[i]));
            }
            // Revert change
            vocEdWindow.getButton("Revert").click();
            // Check that change has been reverted
            for (int i = 0; i < vve.size(); i++) {
                assertTrue(vve.elementAt(i).getValueText().equals(originalData[i]));
            }
        }

        // TEST 3d: Delete: backspace all
        for (VocabElement ve : vve) {
            ve.replaceTextInName(backSpace);
        }
        // Check that change occurred
        for (int i = 0; i < vve.size(); i ++) {
            assertFalse(vve.elementAt(i).getValueText().equals(originalData[i]));
        }
        // Revert change
        vocEdWindow.getButton("Revert").click();
        // Check that change has been reverted
        for (int i = 0; i < vve.size(); i ++) {
            assertTrue(vve.elementAt(i).getValueText().equals(originalData[i]));
        }

        for (int j = 0; j < 4; j++) {
            for (VocabElement ve : vve) {
                ve.replaceTextInArg(j, backSpace);
            }
            // Check that change occurred
            for (int i = 0; i < vve.size(); i++) {
                assertFalse(vve.elementAt(i).getValueText().equals(originalData[i]));
            }
            // Revert change
            vocEdWindow.getButton("Revert").click();
            // Check that change has been reverted
            for (int i = 0; i < vve.size(); i++) {
                assertTrue(vve.elementAt(i).getValueText().equals(originalData[i]));
            }
        } */
    }

    /**
     * Test adding new variables manually.
     * @throws java.lang.Exception on any error
     */
//    public void testAddingVariablesManually() throws Exception {
//        //Preparation
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//        String [] varNames = {"text", "predicate", "integer", "nominal",
//        "matrix", "float"};
//        String [] varTypes = {"TEXT", "PREDICATE", "INTEGER",
//        "NOMINAL", "MATRIX", "FLOAT"};
//
//        // 1. Create a new variable, then check that the variable list
//        // is populated with correct data.
//        for (int i = 0; i < varNames.length; i++) {
//            createNewVariable(varNames[i], varTypes[i]);
//
//            // 1a. Check that variable list is populated with correct data
//            Window varListWindow = WindowInterceptor.run(menuBar.getMenu(
//                    "Spreadsheet").getSubMenu("Variable List").triggerClick());
//            Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
//                    window.getUIComponents(Spreadsheet.class)[0]
//                    .getAwtComponent()));
//            assertTrue(varListWindow.getTable().getRowCount() == ss.getColumns()
//                    .size());
//            for (int j = 0; j < ss.getColumns().size(); j++) {
//                assertTrue(inTable(ss.getColumns().elementAt(j).getHeaderName(),
//                        varListWindow.getTable(), 1));
//                assertTrue(inTable(ss.getColumns().elementAt(j).getHeaderType(),
//                        varListWindow.getTable(), 2));
//            }
//        }
//    }
//
//    /**
//     * Test removal with new database.
//     * @throws java.lang.Exception on any error
//     */
//    public void testRemovalWithNewDatabase() throws Exception {
//        //Preparation
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//
//        // 1. Open and run script to populate database
//        String root = System.getProperty("testPath");
//        File demoFile = new File(root + "/ui/demo_data.rb");
//        assertTrue(demoFile.exists());
//
//                WindowInterceptor
//                .init(menuBar.getMenu("Script").getSubMenu("Run script")
//                    .triggerClick())
//                .process(FileChooserHandler.init()
//                    .assertIsOpenDialog()
//                    .assertAcceptsFilesOnly()
//                    .select(demoFile.getAbsolutePath()))
//                .process(new WindowHandler() {
//                    public Trigger process(Window console) {
//                        return console.getButton("Close").triggerClick();
//                    }
//                })
//                .run();
//
//
//        // 1a. Check that variable list is populated
//         Window varListWindow = WindowInterceptor.run(menuBar.getMenu(
//                "Spreadsheet").getSubMenu("Variable List").triggerClick());
//         assertTrue(varListWindow.getTable().getRowCount() > 0);
//
//        // 2. Create new database
//        WindowInterceptor
//                .init(menuBar.getMenu("File").getSubMenu("New").triggerClick())
//                .process(new WindowHandler() {
//                    public Trigger process (Window newDBWindow) {
//                        newDBWindow.getTextBox("nameField").setText("newDB");
//                        return newDBWindow.getButton("Ok").triggerClick();
//                    }
//                 })
//                 .run();
//
//        // 2b. Check that variable list is empty
//        //BugzID:430
//        //assertTrue(varListWindow.getTable().getRowCount() == 0);
//                 varListWindow = WindowInterceptor.run(menuBar.getMenu(
//                "Spreadsheet").getSubMenu("Variable List").triggerClick());
//         assertTrue(varListWindow.getTable().getRowCount() == 0);
//    }
//
//    /**
//     * Creates a new variable and checks that it has been created.
//     * @param varName String for variable name
//     * @param varType String for variable type
//     * @param varRadio String for corresponding radio button for varType
//     * @throws java.lang.Exception on any error
//     */
//    private void validateVariableType(final String varName,
//            final String varType,
//            final String varRadio) throws Exception {
//        // 1. Retrieve the components
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//        // 2a. Create new variable,
//        //open spreadsheet and check that it's there
//        Window newVarWindow = WindowInterceptor.run(menuBar.getMenu(
//                "Spreadsheet").getSubMenu("New Variable").triggerClick());
//        newVarWindow.getTextBox("nameField").insertText(varName, 0);
//        newVarWindow.getRadioButton(varRadio).click();
//        assertTrue(newVarWindow.getRadioButton(varRadio).isSelected());
//        newVarWindow.getButton("Ok").click();
//        //check that correct column has been created
//        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
//                window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//        assertNotNull(ss.getSpreadsheetColumn(varName));
//        assertTrue(ss.getSpreadsheetColumn(varName).getHeaderName()
//                .equals(varName));
//        assertTrue(ss.getSpreadsheetColumn(varName).getHeaderType()
//                .equals(varType));
//        //check that column has no cells
//        assertTrue(ss.getSpreadsheetColumn(varName).getCells().isEmpty());
//    }
//
//    /**
//     * Create a new variable.
//     * @param varName String for the name of the variable
//     * @param varType String for the variable type
//     * @throws java.lang.Exception on any error
//     */
//    private void createNewVariable(final String varName,
//            final String varType) throws Exception {
//        String varRadio = varType.toLowerCase();
//        // 1. Retrieve the components
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//        // 2a. Create new variable,
//        //open spreadsheet and check that it's there
//        Window newVarWindow = WindowInterceptor.run(menuBar.getMenu(
//                "Spreadsheet").getSubMenu("New Variable").triggerClick());
//        newVarWindow.getTextBox("nameField").insertText(varName, 0);
//        newVarWindow.getRadioButton(varRadio).click();
//        newVarWindow.getButton("Ok").click();
//    }
//
//    /**
//     * Checks if String is in a Table column.
//     * @param item String to find
//     * @param t Table to look in
//     * @param column Column number
//     * @return true if found, else false
//     */
//    private Boolean inTable(final String item, final Table t,
//            final int column) {
//        for (int i = 0; i < t.getRowCount(); i++) {
//            if (item.equals(t.getContentAt(i, column))) {
//                return true;
//            }
//        }
//        return false;
//    }

//    //CODE FOR ADDING AND REVERTING//
//    /** Test vocab editor creating new predicate and reverting.
//     * @throws java.lang.Exception on any error
//     */
//    public void testAddNewPredicateAndRevert() throws Exception {
//        //Preparation
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//
//        // 1. Create a new predicate
//        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
//                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());
//
//        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
//                .getPanel("verticalFrame");
//
//        vocElementsPanel.getUIComponents(VocabElement.class);
//
//        //VocabElement [] vocabElements = new
//
//        VocabElement oldVE = new VocabElement(((VocabElementV) (vocElementsPanel
//            .getUIComponents(VocabElement.class)[0].getAwtComponent())));
//
//        vocEdWindow.getButton("Add Predicate()").click();
//
//        VocabElement oldVE = new VocabElement(((VocabElementV) (vocElementsPanel
//            .getUIComponents(VocabElement.class)[0].getAwtComponent())));
//
//        //Check that VE exists
//        //Revert
//        //Check that vocab element has been removed
//
//        vocEdWindow.getButton("OK").click();
//
//        // 2. Create new predicate variable and cell
//        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
//                window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//
//        String varName = "predicate";
//        createNewVariable(varName, varName.toUpperCase());
//
//        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
//
//        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
//
//        Cell c = cells.elementAt(0);
//        TextBox t = c.getValue();
//
//        Vector<TextItem> vti = new Vector<TextItem>();
//        vti.add(new StringItem(veName));
//
//        c.enterText(Cell.VALUE, vti);
//
//        assertTrue(t.getText().equalsIgnoreCase("predicate1(<arg0>)"));
//    }
//
//    /** Test vocab editor creating new predicate and replacing VE name.
//     * @throws java.lang.Exception on any error
//     */
//    public void testNewPredicateReplaceVEName() throws Exception {
//        //Preparation 
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//
//        // 1. Create a new predicate
//        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
//                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());
//
//        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
//                .getPanel("verticalFrame");
//
//        vocEdWindow.getButton("Add Predicate()").click();
//
//        VocabElement ve = new VocabElement(((VocabElementV) (vocElementsPanel
//            .getUIComponents(VocabElement.class)[0].getAwtComponent())));
//
//        String oldVEName = ve.getVEName();
//
//        Vector<TextItem> vti = new Vector<TextItem>();
//
//        vti.add(new StringItem("newName"));
//        ve.replaceTextInName(vti);
//
//        String veName = ve.getVEName();
//
//        assertFalse(oldVEName.equals(veName));
//
//        vocEdWindow.getButton("OK").click();
//
//        // 2. Create new predicate variable and cell
//        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
//                window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//
//        String varName = "predicate";
//        createNewVariable(varName, varName.toUpperCase());
//
//        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
//
//        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
//
//        Cell c = cells.elementAt(0);
//        TextBox t = c.getValue();
//
//        c.enterText(Cell.VALUE, vti);
//
//        assertTrue(t.getText().equalsIgnoreCase(veName + "(<arg0>)"));
//    }
//
//    /** Test vocab editor creating new predicate and adding to VE name.
//     * @throws java.lang.Exception on any error
//     */
//    public void testNewPredicateAddingVEName() throws Exception {
//        //Preparation
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//
//        // 1. Create a new predicate
//        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
//                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());
//
//        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
//                .getPanel("verticalFrame");
//
//        vocEdWindow.getButton("Add Predicate()").click();
//
//        VocabElement ve = new VocabElement(((VocabElementV) (vocElementsPanel
//            .getUIComponents(VocabElement.class)[0].getAwtComponent())));
//
//        String oldVEName = ve.getVEName();
//
//        Vector<TextItem> vti = new Vector<TextItem>();
//
//        vti.add(new StringItem("newName"));
//        ve.enterText(vti);
//
//        String veName = ve.getVEName();
//
//        assertFalse(oldVEName.equals(veName));
//
//        vocEdWindow.getButton("OK").click();
//
//        // 2. Create new predicate variable and cell
//        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
//                window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//
//        String varName = "predicate";
//        createNewVariable(varName, varName.toUpperCase());
//
//        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
//
//        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
//
//        Cell c = cells.elementAt(0);
//        TextBox t = c.getValue();
//
//        vti.add(new StringItem("predicate1"));
//
//        c.enterText(Cell.VALUE, vti);
//
//        assertTrue(t.getText().equalsIgnoreCase(veName + "(<arg0>)"));
//    }
//
//    /** Test vocab editor creating new predicate and adding VE argument.
//     * @throws java.lang.Exception on any error
//     */
//    public void testNewPredicateAddingVEArgument() throws Exception {
//        //Preparation
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//
//        // 1. Create a new predicate
//        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
//                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());
//
//        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
//                .getPanel("verticalFrame");
//
//        vocEdWindow.getButton("Add Predicate()").click();
//
//        VocabElement ve = new VocabElement(((VocabElementV) (vocElementsPanel
//            .getUIComponents(VocabElement.class)[0].getAwtComponent())));
//
//        String oldVEArgName = ve.getArgument(0);
//
//        Vector<TextItem> vti = new Vector<TextItem>();
//
//        vti.add(new StringItem("newName"));
//        ve.enterTextInArg(0, vti);
//
//        String veArgName = ve.getArgument(0);
//
//        assertFalse(oldVEArgName.equals(veArgName));
//
//        vocEdWindow.getButton("OK").click();
//
//        // 2. Create new predicate variable and cell
//        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
//                window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//
//        String varName = "predicate";
//        createNewVariable(varName, varName.toUpperCase());
//
//        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
//
//        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
//
//        Cell c = cells.elementAt(0);
//        TextBox t = c.getValue();
//
//        vti.clear();
//        vti.add(new StringItem("predicate1"));
//
//        c.enterText(Cell.VALUE, vti);
//
//        assertTrue(t.getText().equalsIgnoreCase(
//                "predicate1(<" + veArgName + ">)"));
//    }
//
//    /** Test vocab editor creating new predicate and replacing VE argument.
//     * @throws java.lang.Exception on any error
//     */
//    public void testNewPredicateReplaceVEArgument() throws Exception {
//        //Preparation
//        Window window = getMainWindow();
//        MenuBar menuBar = window.getMenuBar();
//
//        // 1. Create a new predicate
//        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
//                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());
//
//        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
//                .getPanel("verticalFrame");
//
//        vocEdWindow.getButton("Add Predicate()").click();
//
//        VocabElement ve = new VocabElement(((VocabElementV) (vocElementsPanel
//            .getUIComponents(VocabElement.class)[0].getAwtComponent())));
//
//        String oldVEArgName = ve.getArgument(0);
//
//        Vector<TextItem> vti = new Vector<TextItem>();
//
//        vti.add(new StringItem("newName"));
//        ve.replaceTextInArg(0, vti);
//
//        String veArgName = ve.getArgument(0);
//
//        assertFalse(oldVEArgName.equals(veArgName));
//
//        vocEdWindow.getButton("OK").click();
//
//        // 2. Create new predicate variable and cell
//        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
//                window.getUIComponents(Spreadsheet.class)[0]
//                .getAwtComponent()));
//
//        String varName = "predicate";
//        createNewVariable(varName, varName.toUpperCase());
//
//        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
//
//        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
//
//        Cell c = cells.elementAt(0);
//        TextBox t = c.getValue();
//
//        vti.clear();
//        vti.add(new StringItem("predicate1"));
//
//        c.enterText(Cell.VALUE, vti);
//
//        assertTrue(t.getText().equalsIgnoreCase(
//                "predicate1(<" + veArgName + ">)"));
//    }
//
    /**
     * Create a new variable.
     * @param varName String for the name of the variable
     * @param varRadio String for the corresponding radio button to click
     * @throws java.lang.Exception on any error
     */
    private void createNewVariable(final String varName,
            final String varRadio) throws Exception {
        // 1. Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        // 2a. Create new variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("New Variable").triggerClick());
        newVarWindow.getTextBox("nameField").insertText(varName, 0);
        newVarWindow.getRadioButton(varRadio).click();
        newVarWindow.getButton("Ok").click();
    }
}