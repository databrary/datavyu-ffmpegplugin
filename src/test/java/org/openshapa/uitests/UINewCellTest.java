package org.openshapa.uitests;

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
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;

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

        String[] testInput = {"Subject stands )up ", "$10,432",
            "Hand me (the manual!", "Tote_that_bale", "Jeune; fille celebre",
            "If x>7 then x|2"};

        int numOfTests = testInput.length;

        String[] expectedTestOutput = {"Subject stands up ", "$10432",
            "Hand me the manual!", "Tote_that_bale", "Jeune fille celebre",
            "If x7 then x2"};

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varType, varRadio);

        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));

        //3. Create 6 new cell, check that they have been created
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
            assertTrue(cells.elementAt(i).getValueText().equals("<val>"));

            //4. Test different inputs as per specifications
            Cell c = cells.elementAt(i);
            TextBox t = c.getValue();

            c.enterText(Cell.VALUE, testInput[i]);

            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]));
        }

        //5. Check copy pasting
         for (int i = 1; i < numOfTests + 1; i++) {
            int j = i % numOfTests;
            TextBox t = cells.elementAt(i - 1).getValue();
            Clipboard.putText(testInput[j]);
            t.setText("");
            t.pasteFromClipboard();
            //BugzID383: assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[j]));
        }
    }

     /**
     * Test creating a new NOMINAL cell with more advanced input.
     * @throws java.lang.Exception on any error
     */
    public void testNewAdvancedNominalCell() throws Exception {
        String varName = "nomVar";
        String varType = "NOMINAL";
        String varRadio = "nominal";

        String[] testInput = {"Subject stands )up", "$10,432",
            "Hand me (the manual!", "Tote_that_bale", "Jeune; fille celebre",
            "If x>7 then x|2"};

        //advanced Input will be provided between testInput
        Key[][] advancedInput = {{Key.LEFT, Key.LEFT},
            {Key.LEFT, Key.LEFT, Key.RIGHT}, {Key.BACKSPACE, Key.LEFT},
            {Key.BACKSPACE, Key.LEFT, Key.LEFT, Key.LEFT, Key.DELETE, Key.RIGHT},
            {Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                     Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                     Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                     Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                     Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                     Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                     Key.BACKSPACE, }};


        int numOfTests = testInput.length;

        String[] expectedTestOutput = {"Subject stands $10432up",
        "$1043Hand me the manual!2", "Hand me the manuaTote_that_balel",
        "Tote_that_aJeune fille celebrel", "If x7 then x2"};

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new TEXT variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varType, varRadio);

        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel) (
                window.getUIComponents(
                Spreadsheet.class)[0].getAwtComponent())));

        //3. Create 6 new cell, check that they have been created
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

            c.enterText(0, testInput[i], advancedInput[i], testInput[i + 1]);
            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]));
        }
    }

    /**
     * Test creating a new TEXT cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewTextCell() throws Exception {
        String varName = "textVar";
        String varType = "TEXT";
        String varRadio = "text";

        String[] testInput = {"Subject stands up ", "$10,432",
            "Hand me the manual!", "Tote_that_bale", "Jeune fille celebre",
            "If x?7 then x? 2"};


        int numOfTests = testInput.length;

        String[] expectedTestOutput = testInput;

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new TEXT variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varType, varRadio);

        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));

        //3. Create 6 new cell, check that they have been created
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

            c.enterText(Cell.VALUE, testInput[i]);
            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]));
        }
    }

    /**
     * Test creating a new TEXT cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewTextPasting() throws Exception {
        String varName = "textVar";
        String varType = "TEXT";
        String varRadio = "text";

        String[] testInput = {"Subject stands up ", "$10,432",
            "Hand me the manual!", "Tote_that_bale", "Jeune fille celebre",
            "If x?7 then x? 2"};


        int numOfTests = testInput.length;

        String[] expectedTestOutput = testInput;

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new TEXT variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varType, varRadio);

        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));

        //3. Create 6 new cell, check that they have been created
        for (int i = 0; i < numOfTests; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        //5. Check copy pasting
        for (int i = 1; i < numOfTests + 1; i++) {
            int j = i % numOfTests;
            TextBox t = cells.elementAt(i - 1).getValue();
            Clipboard.putText(testInput[j]);
            t.setText("");
            t.pasteFromClipboard();
            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[j]));
        }
    }


    /**
     * Test creating a new TEXT cell with more advanced input.
     * @throws java.lang.Exception on any error
     */
    public void testNewAdvancedTextCell() throws Exception {
        String varName = "textVar";
        String varType = "TEXT";
        String varRadio = "text";

        String[] testInput = {"Subject stands up", "$10,432",
            "Hand me the manual!", "Tote_that_bale", "Jeune fille celebre",
            "If x?7 then x? 2"};

        //advanced Input will be provided between testInput
        Key[][] advancedInput = {{Key.LEFT, Key.LEFT},
            {Key.LEFT, Key.LEFT, Key.RIGHT}, {Key.BACKSPACE, Key.LEFT},
            {Key.BACKSPACE, Key.LEFT, Key.LEFT, Key.LEFT, Key.DELETE, Key.RIGHT},
            {Key.LEFT, Key.RIGHT}};


        int numOfTests = testInput.length;

        String[] expectedTestOutput = testInput;

        String[] advancedExpectedOutput = {"Subject stands $10,432up",
            "$10,43Hand me the manual!2", "hand me the manuaTote_that_balel",
            "Tote_that_aJeune fille celebrel",
            "Jeune fille celebreIf x?7 then x? 2"};

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new TEXT variable,
        //open spreadsheet and check that it's there
        createNewVariable(varName, varType, varRadio);

        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel) (
                window.getUIComponents(
                Spreadsheet.class)[0].getAwtComponent())));

        //3. Create 6 new cell, check that they have been created
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
            assertTrue(t.getText().equalsIgnoreCase(advancedExpectedOutput[i]));
        }
    }

    /**
     * Test creating a new FLOAT cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewFloatCell() throws Exception {
        String varName = "floatVar";
        String varType = "FLOAT";
        String varRadio = "float";

        String[] testInput = {"1a.9", "10-43.2",
            "!289(", "178.&", "0~~~)",
            "If x?7 then. x? 2 ", /*BugzID: 486 "-0.1", "0.2", "-0.0", "-", "-0"*/};

        int numOfTests = testInput.length;

        double[] expectedTestOutput = {1.9, -43.21, 289, 178, 0, 7.2, -0.1, 0.2,
        0, 0, 0};

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

            c.enterText(Cell.VALUE, testInput[i]);

            assertTrue(Double.parseDouble(t.getText())
                    == (expectedTestOutput[i]));
        }

       //5. Check copy pasting
        for (int i = 1; i < numOfTests + 1; i++) {
            int j = i % numOfTests;
            TextBox t = cells.elementAt(i - 1).getValue();
            Clipboard.putText(testInput[j]);
            t.setText("");
            t.pasteFromClipboard();
            //BugzID:384: assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[j]));
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
            "If x?7 then.- x? 2",  /*BugzID422:"()12.3"*/};

        int numOfTests = testInput.length;

         //advanced Input will be provided between testInput
        Key[][] advancedInput = {{Key.LEFT, Key.LEFT},
            {Key.LEFT, Key.LEFT, Key.RIGHT}, {Key.BACKSPACE, Key.LEFT},
            {Key.BACKSPACE, Key.LEFT, Key.LEFT, Key.LEFT, Key.DELETE,
                Key.RIGHT}, {Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                Key.BACKSPACE, Key.BACKSPACE}, {Key.LEFT, Key.LEFT,
                Key.LEFT, Key.LEFT}};

        double[] expectedTestOutput = {-43.21019, -43.282100, 2178.8, 7, -27,
        -27.3};

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

        String[] testInput = {"1a9", "10-432",
            "!28.9(", "178&", "~~~)",
            "If x?7 then x? 2 ", "99999999999999999999"/* BugzId:485 , "-", "-0" */};

        int numOfTests = testInput.length;

        String[] expectedTestOutput = {"19", "-43210", "289", "178", "<val>",
            "72", "999999999999999999"/* BugzID:485,  "0", "0" */};

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

            c.enterText(Cell.VALUE, testInput[i]);

            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]));
        }

               //5. Check copy pasting
        for (int i = 1; i < numOfTests + 1; i++) {
            int j = i % numOfTests;
            TextBox t = cells.elementAt(i - 1).getValue();
            Clipboard.putText(testInput[j]);
            t.setText("");
            t.pasteFromClipboard();
            //BugzID369: assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[j]));
        }
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

            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]));
        }
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

}
