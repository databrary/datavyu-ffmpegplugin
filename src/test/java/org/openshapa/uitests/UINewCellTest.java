package org.openshapa.uitests;

import java.io.File;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
import java.util.Vector;
import org.uispec4j.Cell;
import org.uispec4j.Clipboard;
import org.uispec4j.Key;
import org.uispec4j.KeysItem;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.StringItem;
import org.uispec4j.TextBox;
import org.uispec4j.TextItem;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;

/**
 * Test for the New Cells.
 */
public final class UINewCellTest extends UISpecTestCase {

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
     * Different cell variable types.
     */
     private static final String[] VAR_TYPES = {"TEXT", "PREDICATE", "INTEGER",
        "NOMINAL", "MATRIX", "FLOAT"
    };

    /**
      * Nominal test input.
      */
     private String[] nominalTestInput = {"Subject stands )up ", "$10,432",
            "Hand me (the manual!", "Tote_that_bale", "Jeune; fille celebre",
            "If x>7 then x|2"};

     /**
      * Text test input.
      */
     private String[] textTestInput = {"Subject stands up ", "$10,432",
            "Hand me the manual!", "Tote_that_bale", "Jeune fille celebre",
            "If x?7 then x? 2"};

     /**
      * Integer test input.
      */
     private String[] integerTestInput = {"1a9", "10-432",
            "!28.9(", "178&", "~~~)",
            "If x?7 then x? 2 ", "99999999999999999999", "000389.5", "-", "-0",
            "-123"};

     /**
      * Float test input.
      */
     private String[] floatTestInput = {"1a.9", "10-43.2",
            "!289(", "178.&", "0~~~)",
            "If x?7 then. x? 2 ", "589.138085638", "000389.5",
            "-0.1", "0.2", "-0.0", "-", "-0", "-.34", "-23.34", ".34", "12.34",
            /*"-123"*/};

    static {
      UISpec4J.init();
    }

    /**
     * Test creating a new NOMINAL cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewNominalCell() throws Exception {
        String varName = "nomVar";
        String varRadio = "nominal";

        String[] expectedNominalTestOutput = {"Subject stands up",
            "$10432", "Hand me the manual!", "Tote_that_bale",
            "Jeune fille celebre", "If x7 then x2"};


        //1. Create new variable,
        createNewVariable(varName, varRadio);

        runStandardTest(varName, nominalTestInput, expectedNominalTestOutput);
    }

     /**
     * Test pasting in Nominal cell.
     * @throws java.lang.Exception on any error
     */
    public void testNominalPasting() throws Exception {
        String varName = "nomVar";
        String varRadio = "nominal";

        String[] expectedNominalTestOutput = {"Subject stands up ",
            "$10432", "Hand me the manual!", "Tote_that_bale",
            "Jeune fille celebre", "If x7 then x2"};

        pasteTest(varName, varRadio, nominalTestInput, expectedNominalTestOutput);
    }

     /**
     * Test creating a new NOMINAL cell with more advanced input.
     * @throws java.lang.Exception on any error
     */
    public void testNewAdvancedNominalCell() throws Exception {
        String varName = "nomVar";
        String varRadio = "nominal";

        //advanced Input will be provided between testInput
        Key[][] advancedInput = {{Key.LEFT, Key.LEFT},
            {Key.LEFT, Key.LEFT, Key.RIGHT}, {Key.BACKSPACE, Key.LEFT},
            {Key.BACKSPACE, Key.LEFT, Key.LEFT, Key.LEFT, Key.DELETE,
                Key.RIGHT}, {Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                Key.BACKSPACE, Key.BACKSPACE}};

        String[] expectedTestOutput = {"Subject stands u$10432p ",
        "$1043Hand me the manual!2", "Hand me the manuaTote_that_balel",
        "Tote_that_aJeune fille celebrel", "If x7 then x2"};

        createNewVariable(varName, varRadio);

        runAdvancedTest(varName, nominalTestInput, advancedInput,
                expectedTestOutput);
    }

    /**
     * Test creating a new TEXT cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewTextCell() throws Exception {
        String varName = "textVar";
        String varRadio = "text";

        String[] expectedTestOutput = textTestInput;

        //1. Create new TEXT variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varRadio);

        runStandardTest(varName, textTestInput, expectedTestOutput);
    }

    /**
     * Test pasting in TEXT cell.
     * @throws java.lang.Exception on any error
     */
    public void testTextPasting() throws Exception {
        String varName = "textVar";
        String varRadio = "text";

        String[] expectedTestOutput = textTestInput;
        pasteTest(varName, varRadio, textTestInput, expectedTestOutput);
    }

     /**
     * Test pasting in INTEGER cell.
     * @throws java.lang.Exception on any error
     */
    public void testIntegerPasting() throws Exception {
        String varName = "intVar";
        String varRadio = "integer";

        String[] expectedTestOutput = {"19", "-43210", "289", "178", "<val>",
            "72", "999999999999999999", "3895", "-", "0", "-123"};
        pasteTest(varName, varRadio, integerTestInput, expectedTestOutput);
    }

    /**
     * Test creating a new TEXT cell with more advanced input.
     * @throws java.lang.Exception on any error
     */
    public void testNewAdvancedTextCell() throws Exception {
        String varName = "textVar";
        String varRadio = "text";

        //advanced Input will be provided between testInput
        Key[][] advancedInput = {{Key.LEFT, Key.LEFT},
            {Key.LEFT, Key.LEFT, Key.RIGHT}, {Key.BACKSPACE, Key.LEFT},
            {Key.BACKSPACE, Key.LEFT, Key.LEFT, Key.LEFT, Key.DELETE,
                Key.RIGHT}, {Key.LEFT, Key.RIGHT}};

        String[] advancedExpectedOutput = {"Subject stands u$10,432p ",
            "$10,43Hand me the manual!2", "hand me the manuaTote_that_balel",
            "Tote_that_aJeune fille celebrel",
            "Jeune fille celebreIf x?7 then x? 2"};

        createNewVariable(varName, varRadio);

        runAdvancedTest(varName, textTestInput, advancedInput,
                advancedExpectedOutput);
    }

    /**
     * Test creating a new FLOAT cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewFloatCell() throws Exception {
        String varName = "floatVar";
        String varRadio = "float";

        int numOfTests = floatTestInput.length;

        double[] expectedTestOutput = {1.9, -43.21, 289, 178, 0, 7.2,
        589.138085, 389.5, -0.1, 0.2, 0, 0, 0, -0.34, -23.34, 0.34, 12.34};

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variable
        createNewVariable(varName, varRadio);


        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));

        //2. Create 6 new cell, check that they have been created
        for (int i = 0; i < numOfTests; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        assertTrue(cells.size() == numOfTests);

        for (int i = 0; i < numOfTests; i++) {
            assertTrue(cells.elementAt(i).getOrd() == i + 1);
            assertTrue((cells.elementAt(i).getOnset().getText())
                    .equals("00:00:00:000"));
            assertTrue((cells.elementAt(i).getOffset().getText())
                    .equals("00:00:00:000"));
            assertTrue(cells.elementAt(i).getValue().getText()
                    .equals("<val>"));

            //4. Test different inputs as per specifications
            Cell c = cells.elementAt(i);
            TextBox t = c.getValue();

            c.enterText(Cell.VALUE, floatTestInput[i]);

            assertTrue(Double.parseDouble(t.getText())
                    == (expectedTestOutput[i]));
        }
    }

     /**
     * Test pasting with INTEGER cell.
     * @throws java.lang.Exception on any error
     */
    public void testFloatPasting() throws Exception {
       String varName = "floatVar";
        String varRadio = "float";

        int numOfTests = floatTestInput.length;

        double[] expectedTestOutput = {1.9, -43.21, 289, 178, 0, 7.2,
        589.138085, 389.5, -0.1, 0.2, 0, 0, 0, -0.34, -23.34, 0.34, 12.34};

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        //1. Create new TEXT variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varRadio);
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
        //3. Create 6 new cell, check that they have been created
        for (int i = 0; i < numOfTests; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        //5. Check copy pasting
        for (int i = 0; i < numOfTests; i++) {
            int j = i % numOfTests;
            Clipboard.putText(floatTestInput[j]);

            // Delete existing cell contents.
            Cell c = cells.elementAt(i);
            c.selectAllAndTypeKey(Cell.VALUE, Key.DELETE);

            // Paste new contents.
            TextBox t = c.getValue();
            t.pasteFromClipboard();
            assertTrue(Double.parseDouble(t.getText())
                    == expectedTestOutput[j]);
        }
    }

    /**
     * Test creating a new FLOAT cell with advanced input.
     * @throws java.lang.Exception on any error
     */
    public void testNewAdvancedFloatCell() throws Exception {
        String varName = "floatVar";
        String varRadio = "float";

        String[] testInput = {"1a.9", "10-43.2",
            "!289(", "178.&", "0~~~)",
            "If x?7 then.- x? 8",  "-589.138085638", "12.3"};

        int numOfTests = testInput.length;

         //advanced Input will be provided between testInput
        Key[][] advancedInput = {{Key.LEFT, Key.LEFT},
            {Key.LEFT, Key.LEFT, Key.RIGHT}, {Key.BACKSPACE, Key.LEFT},
            {Key.BACKSPACE, Key.LEFT, Key.LEFT, Key.LEFT, Key.DELETE,
                Key.RIGHT}, {Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                Key.BACKSPACE, Key.BACKSPACE}, {Key.RIGHT},
                {Key.LEFT, Key.LEFT, Key.LEFT, Key.LEFT, Key.LEFT, Key.LEFT,
                         Key.LEFT, Key.LEFT, Key.LEFT, Key.LEFT, Key.LEFT,
                         Key.LEFT, Key.LEFT, Key.LEFT, Key.LEFT, Key.LEFT,
                         Key.LEFT, Key.LEFT}};

        double[] expectedTestOutput = {-43.21019, -43.289210, 2178.8, 7, -87,
        589.138086 /*BugzID612:Previous should actually be -589.138085*/,
        -589.138085};

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variable
        createNewVariable(varName, varRadio);


        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));

        //2. Create new cells, check that they have been created
        for (int i = 0; i < numOfTests; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        assertTrue(cells.size() == numOfTests);

        for (int i = 0; i < numOfTests - 1; i++) {
            assertTrue(cells.elementAt(i).getOrd() == i + 1);
            assertTrue((cells.elementAt(i).getOnset().getText())
                    .equals("00:00:00:000"));
            assertTrue((cells.elementAt(i).getOffset().getText())
                    .equals("00:00:00:000"));
            assertTrue(cells.elementAt(i).getValue().getText()
                    .equals("<val>"));

            //4. Test different inputs as per specifications
            Cell c = cells.elementAt(i);
            TextBox t = c.getValue();

            Vector<TextItem> vti = new Vector<TextItem>();
            vti.add(new StringItem(testInput[i]));
            vti.add(new KeysItem(advancedInput[i]));
            vti.add(new StringItem(testInput[i + 1]));

            c.enterText(Cell.VALUE, vti);

            assertTrue(Double.parseDouble(t.getText())
                    == expectedTestOutput[i]);
        }
    }

    /**
     * Test creating a new INTEGER cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewIntegerCell() throws Exception {
        String varName = "intVar";
        String varRadio = "integer";

        String[] expectedTestOutput = {"19", "-43210", "289", "178", "<val>",
            "72", "999999999999999999", "3895", "<val>", "0", "-123"};

        createNewVariable(varName, varRadio);

        runStandardTest(varName, integerTestInput, expectedTestOutput);
    }

    /**
     * Test creating a new INTEGER cell with advanced input.
     * @throws java.lang.Exception on any error
     */
    public void testNewAdvancedIntegerCell() throws Exception {
        String varName = "intVar";
        String varRadio = "integer";

        String[] testInput = {"1a9", "10-432",
            "!289(", "178&", "If x?7. then x? 2", "17-8&", "()12.3"};

         //advanced Input will be provided between testInput
        Key[][] advancedInput = {{Key.LEFT, Key.LEFT},
            {Key.LEFT, Key.LEFT, Key.RIGHT}, {Key.BACKSPACE, Key.LEFT},
            {Key.BACKSPACE, Key.LEFT, Key.LEFT, Key.LEFT, Key.DELETE,
                Key.RIGHT}, {Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE},
            {Key.LEFT, Key.LEFT, Key.LEFT, Key.LEFT}};

        String[] expectedTestOutput = {"-4321019", "-43289210", "21788", "772",
        "-817", "-817"};

        createNewVariable(varName, varRadio);

        runAdvancedTest(varName, testInput, advancedInput, expectedTestOutput);
    }

    /**
     * Test creating a new MATRIX cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewMatrixCellSingleArgNominal() throws Exception {
        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/matrix_tests.rb");
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

        // 1a. Check that database is populated
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
              (window.getUIComponents(Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(ss.getColumns().size() > 0);

        //2. Test single cell types
        //Test nominal
        String varName = "matrixNominal1";

        String[] expectedNominalTestOutput = {"Subject stands up",
            "$10432", "Hand me the manual!", "Tote_that_bale",
            "Jeune fille celebre", "If x7 then x2"};

        runStandardTest(varName, expectedNominalTestOutput,
                expectedNominalTestOutput, "<nominal>");
    }

    /**
     * Test creating a new MATRIX cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewMatrixCellSingleArgFloat() throws Exception {
        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/matrix_tests.rb");
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

        // 1a. Check that database is populated
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
              (window.getUIComponents(Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(ss.getColumns().size() > 0);

        //2. Test single cell type
        //Test float
        String varName = "matrixFloat1";

        int numOfTests = floatTestInput.length;

        double[] expectedFloatTestOutput = {1.9, -43.21, 289, 178, 0, 7.2,
        589.138085, 389.5, -0.1, 0.2, 0, 0, 0, -0.34, -23.34, 0.34, 12.34};

        //Create new cells, check that they have been created
        ss.getSpreadsheetColumn(varName).requestFocus();
        for (int i = 0; i < numOfTests; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        assertTrue(cells.size() == numOfTests);

        for (int i = 0; i < numOfTests; i++) {
            /*BugzID:578 assertTrue(cells.elementAt(i).getOrd() == i + 1);*/
            assertTrue((cells.elementAt(i).getOnset().getText())
                    .equals("00:00:00:000"));
            assertTrue((cells.elementAt(i).getOffset().getText())
                    .equals("00:00:00:000"));
            assertTrue(cells.elementAt(i).getValue().getText()
                    .equals("<float>"));

            //4. Test different inputs as per specifications
            Cell c = cells.elementAt(i);
            TextBox t = c.getValue();

            c.enterText(Cell.VALUE, floatTestInput[i]);

            assertTrue(Double.parseDouble(t.getText())
                    == (expectedFloatTestOutput[i]));
        }
    }

    /**
     * Test creating a new MATRIX cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewMatrixCellSingleArgInteger() throws Exception {
        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/matrix_tests.rb");
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

        // 1a. Check that database is populated
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
              (window.getUIComponents(Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(ss.getColumns().size() > 0);

        //2. Test single cell types
        //Test integer
        String varName = "matrixInteger1";

        String [] expectedIntTestOutput = {"19", "-43210", "289", "178", 
        "<int>", "72", "999999999999999999", "3895", "<int>", "0", "-123"};

        runStandardTest(varName, integerTestInput,
                expectedIntTestOutput, "<int>");
    }

    /**
     * Test creating a new MATRIX cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewMatrixCellDoubleArgInteger() throws Exception {
        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/matrix_tests.rb");
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

        // 1a. Check that database is populated
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
              (window.getUIComponents(Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(ss.getColumns().size() > 0);

        //2. Test double cell type
        String varName = "matrixInteger2";

        String [] expectedInt2TestOutput = {"19", "-43210", "289", "178",
        "<int1>", "72", "999999999999999999",
        "3895", "<int1>", "0", "-123"};

        int numOfTests = integerTestInput.length;

        //2a. Test integer, only first arg
        for (int i = 0; i < numOfTests; i++) {
            expectedInt2TestOutput[i] = "(" + expectedInt2TestOutput[i]
                    + ", <int2>)";
        }

        runStandardTest(varName, integerTestInput, expectedInt2TestOutput,
                "(<int1>, <int2>)");

        //2b. Recursively test all permutations of test input
        String [][][] testInput = new String [expectedInt2TestOutput.length]
                [expectedInt2TestOutput.length][2];

        String [] expectedInt2bTempOutput = {"19", "-43210", "289", "178",
        "<int1>", "72", "999999999999999999",
        "3895", "<int1>", "0", "-123"};

        String [][] expectedInt2bTestOutput =
                new String [expectedInt2TestOutput.length]
                [expectedInt2TestOutput.length];

        for (int i = 0; i < numOfTests; i++) {
            for (int j = 0; j < numOfTests; j++) {
                testInput[i][j][0] = integerTestInput[i];
                testInput[i][j][1] = integerTestInput[j];
                if (expectedInt2bTempOutput[i].equals("<int2>")) {
                    expectedInt2bTestOutput[i][j] = "(<int1>"
                        + ", " + expectedInt2bTempOutput[j] + ")";
                } else if (expectedInt2bTempOutput[j].equals("<int1>")) {
                    expectedInt2bTestOutput[i][j] = "(" + expectedInt2bTempOutput[i]
                        + ", <int2>)";
                } else {
                expectedInt2bTestOutput[i][j] = "(" + expectedInt2bTempOutput[i]
                        + ", " + expectedInt2bTempOutput[j] + ")";
                }
            }
        }

        for (int i = 0; i < numOfTests; i++) {
            runMatrixTest(varName, testInput[i], expectedInt2bTestOutput[i]);
        }
    }

/**
     * Test creating a new MATRIX cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewMatrixCellDoubleArgNominal() throws Exception {
        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/matrix_tests.rb");
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

        // 1a. Check that database is populated
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
              (window.getUIComponents(Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(ss.getColumns().size() > 0);

        //2. Test double cell type
        //2a. Test nominal
        String varName = "matrixNominal2";

        String [] expectedTestOutput = {"Subject stands up",
            "$10432", "Hand me the manual!", "Tote_that_bale",
            "Jeune fille celebre", "If x7 then x2"};

        int numOfTests = expectedTestOutput.length;

        for (int i = 0; i < numOfTests; i++) {
            expectedTestOutput[i] = "(" + expectedTestOutput[i]
                    + ", <nominal2>)";
        }

        runStandardTest(varName, nominalTestInput, expectedTestOutput,
                "(<nominal1>, <nominal2>)");

        //2b. Recursively test all permutations of test input
        String [][][] testInput = new String [nominalTestInput.length]
                [nominalTestInput.length][2];

        String [] expectedNominal2bTempOutput = {"Subject stands up",
            "$10432", "Hand me the manual!", "Tote_that_bale",
            "Jeune fille celebre", "If x7 then x2"};

        String [][] expectedNominal2bTestOutput =
                new String [expectedNominal2bTempOutput.length]
                [expectedNominal2bTempOutput.length];

        for (int i = 0; i < numOfTests; i++) {
            for (int j = 0; j < numOfTests; j++) {
                testInput[i][j][0] = nominalTestInput[i];
                testInput[i][j][1] = nominalTestInput[j];
                if (expectedNominal2bTempOutput[i].equals("<nominal2>")) {
                    expectedNominal2bTestOutput[i][j] = "(<nominal1>"
                        + ", " + expectedNominal2bTempOutput[j] + ")";
                } else if (expectedTestOutput[j].equals("<nominal1>")) {
                    expectedNominal2bTestOutput[i][j] = "("
                            + expectedNominal2bTempOutput[i] + ", <nominal2>)";
                } else {
                expectedNominal2bTestOutput[i][j] = "("
                        + expectedNominal2bTempOutput[i]
                        + ", " + expectedNominal2bTempOutput[j] + ")";
                }
            }
        }

        numOfTests = nominalTestInput.length;
        for (int i = 0; i < numOfTests; i++) {
            runMatrixTest(varName, testInput[i], expectedNominal2bTestOutput[i]);
        }
    }

    /**
     * Test creating a new MATRIX cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewMatrixCellDoubleArgFloat() throws Exception {
        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/matrix_tests.rb");
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

        // 1a. Check that database is populated
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
              (window.getUIComponents(Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(ss.getColumns().size() > 0);

        //2. Test double cell type
        //2a. Test float
        String varName = "matrixFloat2";

        int numOfTests = floatTestInput.length;

        //Create new cells, check that they have been created
        ss.getSpreadsheetColumn(varName).requestFocus();
        for (int i = 0; i < numOfTests; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        assertTrue(cells.size() == numOfTests);

        for (int i = 0; i < numOfTests; i++) {
            /*BugzID:578 assertTrue(cells.elementAt(i).getOrd() == i + 1);*/
            assertTrue((cells.elementAt(i).getOnset().getText())
                    .equals("00:00:00:000"));
            assertTrue((cells.elementAt(i).getOffset().getText())
                    .equals("00:00:00:000"));
            assertTrue(cells.elementAt(i).getValue().getText()
                    .equals("(<float1>, <float2>)"));

            //4. Test different inputs as per specifications
            Cell c = cells.elementAt(i);
            TextBox t = c.getValue();

            c.enterText(Cell.VALUE, floatTestInput[i]);

            /*BugzID579:assertTrue(Double.parseDouble(getArgFromMatrix(t.getText(), 0))
                    == (expectedFloatTestOutput[i]));*/
        }
        //2b. Need to write double argument tests, but difficult to write with bug 579. Must do later
    }

    /**
     * Test creating a new MATRIX cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewMatrixCellMixed() throws Exception {
        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/matrix_tests.rb");
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

        // 1a. Check that database is populated
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
              (window.getUIComponents(Spreadsheet.class)[0].getAwtComponent()));
        assertTrue(ss.getColumns().size() > 0);

        //4. Test mixed cell types



//        //2. Create new cells, check that they have been created
//        for (int i = 0; i < numOfTests; i++) {
//            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
//        }
//        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
//
//        assertTrue(cells.size() == numOfTests);
//
//        for (int i = 0; i < numOfTests - 1; i++) {
//            assertTrue(cells.elementAt(i).getOrd() == i + 1);
//            assertTrue((cells.elementAt(i).getOnset().getText())
//                    .equals("00:00:00:000"));
//            assertTrue((cells.elementAt(i).getOffset().getText())
//                    .equals("00:00:00:000"));
//            assertTrue(cells.elementAt(i).getValue().getText()
//                    .equals("<val>"));
//
//            //4. Test different inputs as per specifications
//            Cell c = cells.elementAt(i);
//            TextBox t = c.getValue();
//
//            c.enterText(Cell.VALUE, testInput[i], advancedInput[i],
//                    testInput[i + 1]);
//
//            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]));
//        }
    }

    /**
     * Test creating a new cell by pressing enter instead of clicking.
     * @throws java.lang.Exception on any error
     */
    public void testCreateNewCellWithEnter() throws Exception {
        String varName = "testVar";
        String varType = VAR_TYPES[(int) (Math.random() * VAR_TYPES.length)];
        String varRadio = varType.toLowerCase();

        // 1. Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        // 2. Create new variable,
        Window newVarWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("New Variable").triggerClick());
        newVarWindow.getTextBox("nameField").insertText(varName, 0);
        newVarWindow.getRadioButton(varRadio).click();
        assertTrue(newVarWindow.getRadioButton(varRadio).isSelected());
        newVarWindow.getButton("Ok").click();

        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        //Create new cell
        //Instead of clicking, just press "Enter"
        /* Code to be written
         * Must click the column title
         * Then press enter on it
         */
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
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
     * Tests for pasting.
     * @param varName variable name
     * @param varType variable type
     * @param varRadio radio for variable
     * @param testInput test input values
     * @param expectedTestOutput expected test output values
     * @throws java.lang.Exception on any exception
     */
    private void pasteTest(final String varName,
            final String varRadio, final String[] testInput,
            final String[] expectedTestOutput) throws Exception {
        // Retrieve the components and set variables
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        int numOfTests = testInput.length;
        //1. Create new TEXT variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varRadio);
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
        //3. Create 6 new cell, check that they have been created
        for (int i = 0; i < numOfTests; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
        //5. Check copy pasting
        for (int i = 0; i < numOfTests; i++) {
            int j = i % numOfTests;
            Clipboard.putText(testInput[j]);
            // Delete existing cell contents.
            Cell c = cells.elementAt(i);
            c.selectAllAndTypeKey(Cell.VALUE, Key.DELETE);
            //Check that it actually was deleted
            assertTrue(c.getValueText().equals("<val>")
                    || c.getValueText().equals(""));
            // Paste new contents.
            TextBox t = c.getValue();
            t.pasteFromClipboard();
            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]));
        }
    }

    /**
     * Runs advanced tests.
     * @param varName name of variable and therefore column header name
     * @param testInput array of test input
     * @param advancedInput extra advanced input
     * @param expectedTestOutput expected test output
     */
    private void runAdvancedTest(final String varName,
            final String[] testInput, final Key[][] advancedInput,
            final String[] expectedTestOutput) {
        // Retrieve the components and set variable
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        int numOfTests = testInput.length;
        //2. Create new cells, check that they have been created
        for (int i = 0; i < numOfTests; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
        assertTrue(cells.size() == numOfTests);
        for (int i = 0; i < numOfTests - 1; i++) {
            assertTrue(cells.elementAt(i).getOrd() == i + 1);
            assertTrue((cells.elementAt(i).getOnset().getText()).equals(
                    "00:00:00:000"));
            assertTrue((cells.elementAt(i).getOffset().getText()).equals(
                    "00:00:00:000"));
            assertTrue(cells.elementAt(i).getValue().getText().equals("<val>"));
            //4. Test different inputs as per specifications
            Cell c = cells.elementAt(i);
            TextBox t = c.getValue();

            Vector<TextItem> vti = new Vector<TextItem>();
            vti.add(new StringItem(testInput[i]));
            vti.add(new KeysItem(advancedInput[i]));
            vti.add(new StringItem(testInput[i + 1]));

            c.enterText(Cell.VALUE, vti);

            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]));
        }
    }

    /**
     * Runs a matrix test.
     * @param varName name of variable and therefore column header name
     * @param testInput Array of arguments for matrix
     * @param expectedTestOutput expected test output
     */
    private void runMatrixTest(final String varName,
            final String[][] testInput, final String[] expectedTestOutput) {
        // Retrieve the components and set variable
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        int numOfTests = testInput.length;
        //2. Create new cells, check that they have been created
        for (int i = 0; i < numOfTests; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
        for (int i = 0; i < numOfTests - 1; i++) {
            assertTrue((cells.elementAt(i).getOnset().getText()).equals(
                    "00:00:00:000"));
            assertTrue((cells.elementAt(i).getOffset().getText()).equals(
                    "00:00:00:000"));
            //4. Test different inputs as per specifications
            Cell c = cells.elementAt(i);
            TextBox t = c.getValue();
            c.enterMatrixText(testInput[i]);
            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]));
        }
    }

    /**
     * Runs standard tests without advanced input, default custom blank used.
     * @param varName name of variable and therefore column header name
     * @param testInput array of test input
     * @param expectedTestOutput expected test output
     */
    private void runStandardTest(final String varName, final String[] testInput,
            final String[] expectedTestOutput) {
        runStandardTest(varName, testInput, expectedTestOutput, "<val>");
    }

    /**
     * Runs standard tests without advanced input.
     * @param varName name of variable and therefore column header name
     * @param testInput array of test input
     * @param expectedTestOutput expected test output
     * @param customBlank the placeholder if a value is blank
     */
     private void runStandardTest(final String varName,
             final String[] testInput, final String[] expectedTestOutput,
             final String customBlank) {
        // Retrieve the components and set variable
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        int numOfTests = testInput.length;
        //2. Create new cell, check that they have been created
        ss.getSpreadsheetColumn(varName).requestFocus();
        for (int i = 0; i < numOfTests; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
        assertTrue(cells.size() == numOfTests);
        for (int i = 0; i < numOfTests; i++) {
            /*BugzID:578 assertTrue(cells.elementAt(i).getOrd() == i + 1);*/
            assertTrue((cells.elementAt(i).getOnset().getText()).equals(
                    "00:00:00:000"));
            assertTrue((cells.elementAt(i).getOffset().getText()).equals(
                    "00:00:00:000"));
            assertTrue(cells.elementAt(i).getValue().getText().equals(
                    customBlank));
            //4. Test different inputs as per specifications
            Cell c = cells.elementAt(i);
            TextBox t = c.getValue();
            c.enterText(Cell.VALUE, testInput[i]);
            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]));
        }
    }

     /**
      * Parses a matrix value and returns an arg.
      * @param matrixCellValue matrix cell value
      * @param arg argument number
      * @return argument as a string
      */
     private String getArgFromMatrix(final String matrixCellValue,
             final int arg) {
         String argList = matrixCellValue.substring(1,
                 matrixCellValue.length() - 2);

         String [] tokens = argList.split(", ");

         return tokens[arg];
     }

     /**
      * Parses a matrix value and returns array of arguments.
      * @param matrixCellValue matrix cell value
      * @return arguments in an array
      */
     private String [] getArgFromMatrix(final String matrixCellValue) {
         String argList = matrixCellValue.substring(1,
                 matrixCellValue.length() - 2);

         String [] tokens = argList.split(", ");

         return tokens;
     }
}

