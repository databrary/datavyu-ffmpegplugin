package org.openshapa.uitests;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.openshapa.views.discrete.datavalues.vocabelements.VocabElementV;
import org.uispec4j.Cell;
import org.uispec4j.Column;
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
        UISpec4J.setWindowInterceptionTimeLimit(4000000);
        UISpec4J.init();
    }

    /** Test vocab editor is being populated.
     * @throws java.lang.Exception on any error
     */
    @SuppressWarnings("empty-statement")
    public void testLoading() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Check that vocab editor is not populated
        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");


        int numVocElements = getVocabElements(vocElementsPanel).length;

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

        numVocElements = getVocabElements(vocElementsPanel).length;
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

        VocabElement ve = getVocabElements(vocElementsPanel)[0];

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

        VocabElement ve = getVocabElements(vocElementsPanel)[0];

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

        VocabElement ve = getVocabElements(vocElementsPanel)[0];

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

        VocabElement ve = getVocabElements(vocElementsPanel)[0];

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

        VocabElement ve = getVocabElements(vocElementsPanel)[0];

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
        Vector<VocabElement> vve = new Vector<VocabElement>(Arrays.asList(
                getVocabElements(vocElementsPanel)));
        for (int i = 0; i < numVocElements; i++) {
            originalData[i] = vve.elementAt(i).getValueText();
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
        Vector<VocabElement> vve = new Vector<VocabElement>(Arrays.asList(
                getVocabElements(vocElementsPanel)));
        for (int i = 0; i < numVocElements; i++) {
            originalData[i] = vve.elementAt(i).getValueText();
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
        Vector<VocabElement> vve = new Vector<VocabElement>(Arrays.asList(
                getVocabElements(vocElementsPanel)));
        for (int i = 0; i < numVocElements; i++) {
            originalData[i] = vve.elementAt(i).getValueText();
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
        Vector<VocabElement> vve = new Vector<VocabElement>(Arrays.asList(
                getVocabElements(vocElementsPanel)));
        for (int i = 0; i < numVocElements; i++) {
            originalData[i] = vve.elementAt(i).getValueText();
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

    /** Test vocab editor creating new predicate and reverting w/o script.
     * @throws java.lang.Exception on any error
     */
    public void testAddNewPredicateAndRevert1() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        VocabElement [] oldVEs = getVocabElements(vocElementsPanel);

        vocEdWindow.getButton("Add Predicate()").click();

        vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        //Check that VE exists
        assertTrue(oldVEs.length < getVocabElements(vocElementsPanel).length);

        //Revert
        vocEdWindow.getButton("Revert").click();

        vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        VocabElement [] finalVocElements = getVocabElements(vocElementsPanel);

        //Check that vocab element has been removed
        assertTrue(oldVEs.length == finalVocElements.length);

        int numElements = oldVEs.length;
        for (int i = 0; i < numElements; i++) {
            assertTrue(oldVEs[i].getValueText().equalsIgnoreCase(
                    finalVocElements[i].getValueText()));
        }
    }

     /** Test vocab editor creating new predicate and reverting w/o script.
     * @throws java.lang.Exception on any error
     */
    public void testAddNewPredicateAndRevert2() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertAcceptsFilesOnly()
                    .select(demoFile.getAbsolutePath()))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        VocabElement [] oldVEs = getVocabElements(vocElementsPanel);

        vocEdWindow.getButton("Add Predicate()").click();

        vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        //Check that VE exists
        assertTrue(oldVEs.length < getVocabElements(vocElementsPanel).length);

        //Revert
        vocEdWindow.getButton("Revert").click();

        vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        VocabElement [] finalVocElements = getVocabElements(vocElementsPanel);

        //Check that vocab element has been removed
        assertTrue(oldVEs.length == finalVocElements.length);

        int numElements = oldVEs.length;
        for (int i = 0; i < numElements; i++) {
            assertTrue(oldVEs[i].getValueText().equalsIgnoreCase(
                    finalVocElements[i].getValueText()));
        }
    }

    /** Test vocab editor creating new predicate and reverting w/o script.
     * @throws java.lang.Exception on any error
     */
    public void testAddNewMatrixAndRevert1() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        VocabElement [] oldVEs = getVocabElements(vocElementsPanel);

        vocEdWindow.getButton("Add Matrix()").click();

        vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        //Check that VE exists
        assertTrue(oldVEs.length < getVocabElements(vocElementsPanel).length);

        //Revert
        vocEdWindow.getButton("Revert").click();

        vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        VocabElement [] finalVocElements = getVocabElements(vocElementsPanel);

        //Check that vocab element has been removed
        assertTrue(oldVEs.length == finalVocElements.length);

        int numElements = oldVEs.length;
        for (int i = 0; i < numElements; i++) {
            assertTrue(oldVEs[i].getValueText().equalsIgnoreCase(
                    finalVocElements[i].getValueText()));
        }
    }

     /** Test vocab editor creating new predicate and reverting w/o script.
     * @throws java.lang.Exception on any error
     */
    public void testAddNewMatrixAndRevert2() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertAcceptsFilesOnly()
                    .select(demoFile.getAbsolutePath()))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        VocabElement [] oldVEs = getVocabElements(vocElementsPanel);

        vocEdWindow.getButton("Add Matrix()").click();

        vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        //Check that VE exists
        assertTrue(oldVEs.length < getVocabElements(vocElementsPanel).length);

        //Revert
        vocEdWindow.getButton("Revert").click();

        vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        VocabElement [] finalVocElements = getVocabElements(vocElementsPanel);

        //Check that vocab element has been removed
        assertTrue(oldVEs.length == finalVocElements.length);

        int numElements = oldVEs.length;
        for (int i = 0; i < numElements; i++) {
            assertTrue(oldVEs[i].getValueText().equalsIgnoreCase(
                    finalVocElements[i].getValueText()));
        }
    }

    /** Test vocab editor creating new predicate and reverting w/o script.
     * @throws java.lang.Exception on any error
     */
    public void testAddNewMatrix() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertAcceptsFilesOnly()
                    .select(demoFile.getAbsolutePath()))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());

        Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        VocabElement [] oldVEs = getVocabElements(vocElementsPanel);

        vocEdWindow.getButton("Add Matrix()").click();

        vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        //Check that VE exists
        assertTrue(oldVEs.length < getVocabElements(vocElementsPanel).length);

        //Click Apply
        vocEdWindow.getButton("Apply").click();

        vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
                .getPanel("verticalFrame");

        VocabElement [] finalVocElements = getVocabElements(vocElementsPanel);

        //Check that new matrix column has been created
         Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        String varName = finalVocElements[finalVocElements.length - 1]
                .getVEName();

        Column matrixColumn = ss.getSpreadsheetColumn(varName);

        assertNotNull(matrixColumn);

        matrixColumn.requestFocus();

        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();

        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        String argName = finalVocElements[finalVocElements.length - 1]
                .getArgument(0);

        assertTrue(cells.elementAt(0).getValueText().equalsIgnoreCase(
                "<" + argName + ">"));
    }


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

     /**
     * returns array of VocabElements from a Panel.
     * @param panel Panel with vocabElements
     * @return array of VocabElements
     */
    public final VocabElement[] getVocabElements(Panel panel) {

        int numOfElements = panel.getUIComponents(VocabElement.class).length;

        VocabElement [] veArray = new VocabElement[numOfElements];

        for (int i = 0; i < numOfElements; i++) {
            veArray[i] = new VocabElement((VocabElementV) (panel.
                    getUIComponents(VocabElement.class)[i].getAwtComponent()));
        }
        return veArray;
    }
}