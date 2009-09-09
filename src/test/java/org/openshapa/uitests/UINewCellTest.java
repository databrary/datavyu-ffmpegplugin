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
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.TextBox;
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
     * Test input and expected output.
     */
    /**
      * Nominal test input.
      */
     private String[] nominalTestInput = {"Subject stands )up ", "$10,432",
            "Hand me (the manual!", "Tote_that_bale", "Jeune; fille celebre",
            "If x>7 then x|2"};

     /**
      * Nominal expected output.
      */
     private String[] expectedNominalTestOutput = {"Subject stands up ",
            "$10432", "Hand me the manual!", "Tote_that_bale",
            "Jeune fille celebre", "If x7 then x2"};

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
            "If x?7 then x? 2 ", "99999999999999999999", "000389.5"
            /* BugzId:485 , "-", "-0" */};

     /**
      * Float test input.
      */
     private String[] floatTestInput = {"1a.9", "10-43.2",
            "!289(", "178.&", "0~~~)",
            "If x?7 then. x? 2 ", "589.138085638", "000389.5"
            /*BugzID: 486 "-0.1", "0.2", "-0.0", "-", "-0"*/};

    static {
      UISpec4J.init();
    }

     /**
     * Test creating a new NOMINAL cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewNominalCell() throws Exception {
        String varName = "nomVar";
        String varType = "NOMINAL";
        String varRadio = "nominal";

        String [] testInput = nominalTestInput;
        String [] expectedTestOutput = expectedNominalTestOutput;

        int numOfTests = nominalTestInput.length;


        //1. Create new variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varType, varRadio);

        runStandardTest(numOfTests, varName, testInput, expectedTestOutput);
    }

     /**
     * Test pasting in Nominal cell.
     * @throws java.lang.Exception on any error
     */
    public void testNominalPasting() throws Exception {
        String varName = "nomVar";
        String varType = "NOMINAL";
        String varRadio = "nominal";

        int numOfTests = nominalTestInput.length;

        pasteTest(varName, varType, varRadio, numOfTests, nominalTestInput,
                expectedNominalTestOutput);
    }

     /**
     * Test creating a new NOMINAL cell with more advanced input.
     * @throws java.lang.Exception on any error
     */
    public void testNewAdvancedNominalCell() throws Exception {
        String varName = "nomVar";
        String varType = "NOMINAL";
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


        String [] testInput = nominalTestInput;
        int numOfTests = nominalTestInput.length;

        String[] expectedTestOutput = {"Subject stands u$10432p ",
        "$1043Hand me the manual!2", "Hand me the manuaTote_that_balel",
        "Tote_that_aJeune fille celebrel", "If x7 then x2"};


        //1. Create new TEXT variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varType, varRadio);

        runAdvancedTest(numOfTests, varName, testInput, advancedInput,
                expectedTestOutput);
    }

    /**
     * Test creating a new TEXT cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewTextCell() throws Exception {
        String varName = "textVar";
        String varType = "TEXT";
        String varRadio = "text";

        String [] testInput = textTestInput;

        int numOfTests = textTestInput.length;

        String[] expectedTestOutput = textTestInput;

        //1. Create new TEXT variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varType, varRadio);

        runStandardTest(numOfTests, varName, testInput, expectedTestOutput);
    }

    /**
     * Test pasting in TEXT cell.
     * @throws java.lang.Exception on any error
     */
    public void testTextPasting() throws Exception {
        String varName = "textVar";
        String varType = "TEXT";
        String varRadio = "text";

        int numOfTests = textTestInput.length;

        String[] expectedTestOutput = textTestInput;
        pasteTest(varName, varType, varRadio, numOfTests, textTestInput,
                expectedTestOutput);
    }

     /**
     * Test pasting in INTEGER cell.
     * @throws java.lang.Exception on any error
     */
    public void testIntegerPasting() throws Exception {
        String varName = "intVar";
        String varType = "INTEGER";
        String varRadio = "integer";

        int numOfTests = integerTestInput.length;

        String[] expectedTestOutput = {"19", "-43210", "289", "178", "<val>",
            "72", "999999999999999999", "3895"/* BugzID:485,  "0", "0" */};
        pasteTest(varName, varType, varRadio, numOfTests, integerTestInput,
                expectedTestOutput);
    }

    /**
     * Test creating a new TEXT cell with more advanced input.
     * @throws java.lang.Exception on any error
     */
    public void testNewAdvancedTextCell() throws Exception {
        String varName = "textVar";
        String varType = "TEXT";
        String varRadio = "text";

        //advanced Input will be provided between testInput
        Key[][] advancedInput = {{Key.LEFT, Key.LEFT},
            {Key.LEFT, Key.LEFT, Key.RIGHT}, {Key.BACKSPACE, Key.LEFT},
            {Key.BACKSPACE, Key.LEFT, Key.LEFT, Key.LEFT, Key.DELETE,
                Key.RIGHT}, {Key.LEFT, Key.RIGHT}};


        String [] testInput = textTestInput;
        int numOfTests = textTestInput.length;

        String[] advancedExpectedOutput = {"Subject stands u$10,432p ",
            "$10,43Hand me the manual!2", "hand me the manuaTote_that_balel",
            "Tote_that_aJeune fille celebrel",
            "Jeune fille celebreIf x?7 then x? 2"};

        //1. Create new TEXT variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varType, varRadio);

        runAdvancedTest(numOfTests, varName, testInput, advancedInput,
                advancedExpectedOutput);
    }

    /**
     * Test creating a new FLOAT cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewFloatCell() throws Exception {
        String varName = "floatVar";
        String varType = "FLOAT";
        String varRadio = "float";

        int numOfTests = floatTestInput.length;

        double[] expectedTestOutput = {1.9, -43.21, 289, 178, 0, 7.2,
        589.138080, 389.5, -0.1, 0.2, 0, 0, 0};

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variable
        createNewVariable(varName, varType, varRadio);


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
        String varType = "FLOAT";
        String varRadio = "float";

        int numOfTests = floatTestInput.length;

        double[] expectedTestOutput = {1.9, -43.21, 289, 178, 0, 7.2,
        589.138080, 389.5, -0.1, 0.2, 0, 0, 0};

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        //1. Create new TEXT variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varType, varRadio);
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
        String varType = "FLOAT";
        String varRadio = "float";

        String[] testInput = {"1a.9", "10-43.2",
            "!289(", "178.&", "0~~~)",
            "If x?7 then.- x? 2",  "589.138085638"/*BugzID422:"()12.3"*/};

        int numOfTests = testInput.length;

         //advanced Input will be provided between testInput
        Key[][] advancedInput = {{Key.LEFT, Key.LEFT},
            {Key.LEFT, Key.LEFT, Key.RIGHT}, {Key.BACKSPACE, Key.LEFT},
            {Key.BACKSPACE, Key.LEFT, Key.LEFT, Key.LEFT, Key.DELETE,
                Key.RIGHT}, {Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                Key.BACKSPACE, Key.BACKSPACE}, {Key.RIGHT}};

        double[] expectedTestOutput = {-43.21019, -43.282100, 2178.8, 7, -27,
        -27589.138080};

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variable
        createNewVariable(varName, varType, varRadio);


        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));

        //2. Create 6 new cell, check that they have been created
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

            c.enterText(Cell.VALUE, testInput[i], advancedInput[i],
                    testInput[i + 1]);

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
        String varType = "INTEGER";
        String varRadio = "integer";

        String [] testInput = integerTestInput;
        int numOfTests = integerTestInput.length;

        String[] expectedTestOutput = {"19", "-43210", "289", "178", "<val>",
            "72", "999999999999999999", "3895"/* BugzID:485,  "0", "0" */};

        //1. Create new variable
        createNewVariable(varName, varType, varRadio);
        runStandardTest(numOfTests, varName, testInput, expectedTestOutput);
    }

    /**
     * Test creating a new INTEGER cell with advanced input.
     * @throws java.lang.Exception on any error
     */
    public void testNewAdvancedIntegerCell() throws Exception {
        String varName = "intVar";
        String varType = "INTEGER";
        String varRadio = "integer";

        String[] testInput = {"1a9", "10-432",
            "!289(", "178&", "If x?7. then x? 2", "17-8&", "()12.3"};

        int numOfTests = testInput.length;


         //advanced Input will be provided between testInput
        Key[][] advancedInput = {{Key.LEFT, Key.LEFT},
            {Key.LEFT, Key.LEFT, Key.RIGHT}, {Key.BACKSPACE, Key.LEFT},
            {Key.BACKSPACE, Key.LEFT, Key.LEFT, Key.LEFT, Key.DELETE,
                Key.RIGHT}, {Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE},
            {Key.LEFT, Key.LEFT, Key.LEFT, Key.LEFT}};

        String[] expectedTestOutput = {"-4321019", "-43289210", "21788", "772",
        "-817", "-817"};

        //1. Create new variable
        createNewVariable(varName, varType, varRadio);
        runAdvancedTest(numOfTests, varName, testInput, advancedInput,
                expectedTestOutput);
    }

    /**
     * Test creating a new MATRIX cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewMatrixCell() throws Exception {
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

        //2. Test all single cell types
        //2a. Test nominal
        String varName = "matrixNominal1";

        String [] testInput = nominalTestInput;
        String[] expectedTestOutput = expectedNominalTestOutput;

        int numOfTests = nominalTestInput.length;

        runStandardTest(numOfTests, varName, testInput, expectedTestOutput,
                "<nominal>");

        //2b. Test float
        varName = "matrixFloat1";

        testInput = floatTestInput;

        numOfTests = floatTestInput.length;

        double[] expectedFloatTestOutput = {1.9, -43.21, 289, 178, 0, 7.2,
        589.138080, 389.5, -0.1, 0.2, 0, 0, 0};

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

            /*BugzID:579assertTrue(Double.parseDouble(t.getText())
                    == (expectedFloatTestOutput[i]));*/
        }
        //2c. Test integer
        varName = "matrixInteger1";

        testInput = integerTestInput;

        String [] expectedIntTestOutput = {"19", "-43210", "289", "178", "<int>",
        "72", "999999999999999999", "3895"/* BugzID:485,  "0", "0" */};

        numOfTests = integerTestInput.length;

        runStandardTest(numOfTests, varName, testInput, expectedIntTestOutput,
                "<int>");

        //3. Test all double cell types
        //3a. Test nominal
        varName = "matrixNominal2";

        testInput = nominalTestInput;

        expectedTestOutput = expectedNominalTestOutput;

        numOfTests = nominalTestInput.length;

        for (int i = 0; i < numOfTests; i++) {
            expectedTestOutput[i] = "(" + expectedTestOutput[i] + ", <nominal2>)";
        }

        runStandardTest(numOfTests, varName, testInput, expectedTestOutput,
                "(<nominal1>, <nominal2>)");

        //Need to add test to cycle through tests for second argument
        //3b. Test float
        // Bit more complicated because have to convert argument to float

        //2c. Test integer
        varName = "matrixInteger2";

        testInput = integerTestInput;

        String [] expectedInt2TestOutput = {"19", "-43210", "289", "178", "<int1>",
        "72", "999999999999999999", "3895"/* BugzID:485,  "0", "0" */};

        numOfTests = integerTestInput.length;

         for (int i = 0; i < numOfTests; i++) {
            expectedInt2TestOutput[i] = "(" + expectedInt2TestOutput[i] + ", <int2>)";
        }

        runStandardTest(numOfTests, varName, testInput, expectedInt2TestOutput,
                "(<int1>, <int2>)");

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
     * @param varType String for the variable type
     * @param varRadio String for the corresponding radio button to click
     * @throws java.lang.Exception on any error
     */
    private void createNewVariable(final String varName,
            final String varType,
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
     * @param numOfTests number of tests
     * @param testInput test input values
     * @param expectedTestOutput expected test output values
     * @throws java.lang.Exception on any exception
     */
    private void pasteTest(final String varName, final String varType,
            final String varRadio, final int numOfTests,
            final String[] testInput, final String[] expectedTestOutput)
            throws Exception {
        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        //1. Create new TEXT variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varType, varRadio);
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
     * @param numOfTests number of test arguments
     * @param varName name of variable and therefore column header name
     * @param testInput array of test input
     * @param advancedInput extra advanced input
     * @param expectedTestOutput expected test output
     */
    private void runAdvancedTest(final int numOfTests, final String varName,
            final String[] testInput, final Key[][] advancedInput,
            final String[] expectedTestOutput) {
        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
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
            c.enterText(Cell.VALUE, testInput[i], advancedInput[i],
                    testInput[i + 1]);
            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]));
        }
    }

    /**
     * Runs standard tests without advanced input, default custom blank used.
     * @param numOfTests number of test arguments
     * @param varName name of variable and therefore column header name
     * @param testInput array of test input
     * @param expectedTestOutput expected test output
     */
    private void runStandardTest(final int numOfTests, final String varName,
            final String[] testInput, final String[] expectedTestOutput) {
        runStandardTest(numOfTests, varName, testInput, expectedTestOutput,
                "<val>");
    }

    /**
     * Runs standard tests without advanced input.
     * @param numOfTests number of test arguments
     * @param varName name of variable and therefore column header name
     * @param testInput array of test input
     * @param expectedTestOutput expected test output
     * @param customBlank the placeholder if a value is blank
     */
     private void runStandardTest(final int numOfTests, final String varName,
             final String[] testInput, final String[] expectedTestOutput,
             final String customBlank) {
        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
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

}

