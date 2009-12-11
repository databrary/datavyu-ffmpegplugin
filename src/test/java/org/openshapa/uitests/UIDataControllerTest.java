package org.openshapa.uitests;

import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
import java.util.Vector;
import org.openshapa.util.FloatUtils;
import org.openshapa.views.discrete.SpreadsheetCell;
import org.uispec4j.Cell;
import org.uispec4j.Key;
import org.uispec4j.KeyItem;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.TextItem;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;

/**
 * Test for the DataController.
 */
public final class UIDataControllerTest extends UISpecTestCase {

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
     * @throws Exception When unable to tear down
     */
    @Override
    protected void tearDown() throws Exception {
        getMainWindow().dispose();
        super.tearDown();
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
            "-123"};

    static {
      UISpec4J.setWindowInterceptionTimeLimit(120000);
      UISpec4J.init();
    }

    /**
     * Standard test sequence focussing on jogging
     * @throws Exception any exception
     */
    public void testStandardSequence1() throws Exception {
        // Retrieve the components and set variable
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        //1. Create a new variable of random type
        String varName = "testVar";
        String varRadio = "text";

        createNewVariable(varName, varRadio);

        //2. Open Data Viewer Controller
        Window dvc = WindowInterceptor.run(menuBar.getMenu("Controller")
                .getSubMenu("Data Viewer Controller").triggerClick());


        //3. Create new cell - so we have something to send key to because
        // no focus handling
        ss.getSpreadsheetColumn(varName).requestFocus();
        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        //4. Test jogging back and forth
        Cell c = cells.elementAt(0);

        Vector<TextItem> ti = new Vector<TextItem>();
        //ti.add(new StringItem("hello"));
        ti.add(new KeyItem(Key.NUM3));

        // Jog forward 5 times
        for (int i = 0; i < 5; i++) {
            c.enterText(Cell.VALUE, ti);            
        }

        assertTrue(dvc.getTextBox("timestampLabel").getText()
                .equalsIgnoreCase("00:00:05:000"));

        ti.removeAllElements();
        ti.add(new KeyItem(Key.NUM1));
        //Jog back 2 times
        for (int i = 0; i < 2; i++) {
            c.enterText(Cell.VALUE, ti);
        }

        assertTrue(dvc.getTextBox("timestampLabel").getText()
                .equalsIgnoreCase("00:00:03:000"));

        //5. Test Create New Cell with Onset
        ti.removeAllElements();
        ti.add(new KeyItem(Key.NUM0));
        c.enterText(Cell.VALUE, ti);

        cells = ss.getSpreadsheetColumn(varName).getCells();

        assertTrue(cells.size() == 2);
        assertTrue(cells.elementAt(0).getOffsetTime().toString()
                .equals("00:00:02:999"));
        assertTrue(cells.elementAt(1).getOnsetTime().toString()
                .equals("00:00:03:000"));
        assertTrue(cells.elementAt(1).getOffsetTime().toString()
                .equals("00:00:00:000"));

        //6. Insert value into both cells
        cells.elementAt(0).enterText(Cell.VALUE, textTestInput[0]);
        cells.elementAt(1).enterText(Cell.VALUE, textTestInput[1]);

        assertTrue(cells.elementAt(0).getValueText().equals(textTestInput[0]));
        assertTrue(cells.elementAt(1).getValueText().equals(textTestInput[1]));

        //7. Jog forward 60 times and change cell onset
        ti.removeAllElements();
        ti.add(new KeyItem(Key.NUM3));

        for (int i = 0; i < 60; i++) {
            c.enterText(Cell.ONSET, ti);
        }

        //Set cell onset
        ti.removeAllElements();
        ti.add(new KeyItem(Key.NUM_DIVIDE));

        // Select cell 2 - cheating, should be resolved when
        // focus handling is implemented
        ((SpreadsheetCell) cells.elementAt(1).getAwtComponent())
                .setSelected(true);
        //Mouse.click(cells.elementAt(1));

        c.enterText(Cell.OFFSET, ti);
        assertTrue(cells.elementAt(1).getOnsetTime().toString()
                .equals("00:01:03:000"));

        //8. Change cell offset
        assertTrue(cells.elementAt(0).getOffsetTime().toString()
                .equals("00:00:02:999"));

        ti.removeAllElements();
        ti.add(new KeyItem(Key.NUM_ASTERISK));
        // Select cell 2 - cheating, should be resolved when
        // focus handling is implemented
        ((SpreadsheetCell) cells.elementAt(1).getAwtComponent())
                .setSelected(false);
        ((SpreadsheetCell) cells.elementAt(0).getAwtComponent())
                .setSelected(true);
        //Mouse.click(cells.elementAt(1));

        c.enterText(Cell.OFFSET, ti);
        assertTrue(cells.elementAt(0).getOffsetTime().toString()
                .equals("00:01:03:000"));

        //9. Jog back and forward, then create a new cell with onset
        ti.removeAllElements();
        ti.add(new KeyItem(Key.NUM1));
        //Jog back 2 times
        for (int i = 0; i < 21; i++) {
            c.enterText(Cell.VALUE, ti);
        }
        assertTrue(dvc.getTextBox("timestampLabel").getText()
                .equalsIgnoreCase("00:00:42:000"));

        ti.removeAllElements();
        ti.add(new KeyItem(Key.NUM3));
        //Jog back 2 times
        for (int i = 0; i < 99; i++) {
            c.enterText(Cell.VALUE, ti);
        }
        assertTrue(dvc.getTextBox("timestampLabel").getText()
                .equalsIgnoreCase("00:02:21:000"));

        //Create new cell with offset
        ti.removeAllElements();
        ti.add(new KeyItem(Key.NUM0));
        c.enterText(Cell.VALUE, ti);

        cells = ss.getSpreadsheetColumn(varName).getCells();

        assertTrue(cells.size() == 3);
        /*BugzID:892 - assertTrue(cells.elementAt(1).getOffsetTime().toString()
                .equals("00:02:20:999"));*/
        assertTrue(cells.elementAt(2).getOnsetTime().toString()
                .equals("00:02:21:000"));
        assertTrue(cells.elementAt(2).getOffsetTime().toString()
                .equals("00:00:00:000"));
    }

    /**
     * Asserts true is two cell values are equal.
     * @param value1 first cell value
     * @param value2 second cell value
     */
    private void assertTrueEqualValues(final String value1,
            final String value2) {
        if ((value1.startsWith("<") && value1.endsWith(">"))
                || (value2.startsWith("<") && value2.endsWith(">"))) {
            assertTrue(value1.equalsIgnoreCase(value2));
        } else {
            try {
                //Handle doubles
                assertTrue(FloatUtils.closeEnough(Double.parseDouble(value1),
                        Double.parseDouble(value2)));
            } catch (NumberFormatException nfe) {
                //Handle other variable types
                assertTrue(value1.equalsIgnoreCase(value2));
            }
        }
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
      * Parses a matrix value and returns an arg.
      * @param matrixCellValue matrix cell value
      * @return int number of arguments
      */
     private int getNumberofArgFromMatrix(final String matrixCellValue) {
         String argList = matrixCellValue.substring(1,
                 matrixCellValue.length() - 1);

         String [] tokens = argList.split(", ");

         return tokens.length;
     }

     /**
      * Parses a matrix value and returns array of arguments.
      * @param matrixCellValue matrix cell value
      * @return arguments in an array
      */
     private String [] getArgsFromMatrix(final String matrixCellValue) {
         String argList = matrixCellValue.substring(1,
                 matrixCellValue.length() - 1);

         String [] tokens = argList.split(", ");

         return tokens;
     }
}

