package org.openshapa.uitests;


import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
import java.util.Vector;
import org.uispec4j.Cell;
import org.uispec4j.Clipboard;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.TextBox;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;




/**
 * Test for the New Cells.
 * @author mmuthukrishna
 */
public final class UINewCellTest extends UISpecTestCase {

    /**
     * Initialiser called before each unit test
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

    /**
     * Different cell variable types.
     */
    private static final String[] VAR_TYPES = {"TEXT", "PREDICATE", "INTEGER",
        "NOMINAL", "MATRIX", "FLOAT"
    };

    /**
     * Test creating a new INTEGER cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewTextCell() throws Exception {
        String varName = "textVar";
        String varType = "TEXT";
        String varRadio = "text";

        String[] testInput = {"Subject stands up", "$10,432",
            "Hand me the manual!", "Tote_that_bale", "Jeune fille celebre",
            "If x?7 then x? 2"};

        String[] expectedTestOutput = testInput;

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new TEXT variable,
        //open spreadsheet and check that it's there
        newVariable(varName, varType, varRadio);

        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel) 
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));

        //3. Create 6 new cell, check that they have been created
        for (int i = 0; i < 6; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        assertTrue(cells.size() == 6);

        for (int i = 0; i < 6; i++) {
            assertTrue(cells.elementAt(i).getOrd() == i + 1);
            assertTrue((cells.elementAt(i).getOnsetTime().toString())
                    .equals("00:00:00:000"));
            assertTrue((cells.elementAt(i).getOffsetTime().toString())
                    .equals("00:00:00:000"));
            assertTrue(cells.elementAt(i).getTextBox(0).getText()
                    .equals("<val>"));

            //4. Test different inputs as per specifications
            Cell c = cells.elementAt(i);
            TextBox t = c.getTextBox(0);

            c.enterEditorText(0, testInput[i]);

            System.err.println(t.getText());
            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]));
        }

        //5. Check copy pasting
        Clipboard c = null;
        for (int i = 1; i < 7; i++) {
            int j = i % 6;
            TextBox t = cells.elementAt(i - 1).getTextBox(0);
            c.putText(testInput[j]);
            t.setText("");
            t.pasteFromClipboard();
           // System.err.println(t.getText());
        //assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[j]));
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
            "!289(", "178&", "~~~)",
            "If x?7 then x? 2"};

        String[] expectedTestOutput = {"19", "-43210", "289", "178", "<val>",
            "72"};

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        //1. Create new variable
        newVariable(varName, varType, varRadio);


        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel) 
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));

        //2. Create 6 new cell, check that they have been created
        for (int i = 0; i < 6; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        assertTrue(cells.size() == 6);

        for (int i = 0; i < 6; i++) {
            assertTrue(cells.elementAt(i).getOrd() == i + 1);
            assertTrue((cells.elementAt(i).getOnsetTime().toString())
                    .equals("00:00:00:000"));
            assertTrue((cells.elementAt(i).getOffsetTime().toString())
                    .equals("00:00:00:000"));
            assertTrue(cells.elementAt(i).getTextBox(0).getText()
                    .equals("<val>"));

            //4. Test different inputs as per specifications
            Cell c = cells.elementAt(i);
            TextBox t = c.getTextBox(0);

            c.enterEditorText(0, testInput[i]);

            //System.err.println(t.getText());
            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]));
        }

       //5. Check copy pasting
        Clipboard c = null;
        for (int i = 1; i < 7; i++) {
            int j = i % 6;
            TextBox t = cells.elementAt(i-1).getTextBox(0);
            c.putText(testInput[j]);
            t.setText("");
            t.pasteFromClipboard();
            //System.err.println(t.getText());
            //assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[j]));
        }
    }

    /**
     * Test creating a new cell.
     * Instead of clicking new cell, just press Enter.
     * @throws java.lang.Exception on any error
     */
    public void testEnterInsteadOfClickingNewCell() throws Exception {
        String varName = "textVar";
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
        /*assertTrue(cells.size() == 1);
        assertTrue(cells.elementAt(0).getOrd() == 1);
        assertTrue((cells.elementAt(0).getOnsetTime().toString())
                .equals("00:00:00:000"));
        assertTrue((cells.elementAt(0).getOffsetTime().toString())
                .equals("00:00:00:000"));
        assertTrue(cells.elementAt(0).getTextBox(0).getText().equals("<val>"));*/
    }

    private void newVariable(final String varName,
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
