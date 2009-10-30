package org.openshapa.uitests;

import java.util.Vector;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
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
 *
 * @todo After bugs resolved, add more advanced cell tests involving
 * left/right caret movement
 */
public final class UITimestampTest extends UISpecTestCase {

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
        UISpec4J.setWindowInterceptionTimeLimit(60000);
        UISpec4J.init();
    }
    /**
     * Different cell variable types.
     */
    private static final String[] VAR_TYPES = {"TEXT", "PREDICATE", "INTEGER",
        "NOMINAL", "MATRIX", "FLOAT"
    };

    /**
     * Test editing the onset and offset timestamps.
     * @throws java.lang.Exception on any error
     */
    public void testTimestampEditing() throws Exception {
        TextBox onset, offset;

        String[] testInput = {"123456789", "6789", "a13", "12:34:56:789",
            "4.43", "127893999", "12:78:93:999", "12:34", "12:34:56"};

        int numOfTests = testInput.length;

        String[] expectedTestOutput = {"12:34:56:789", "68:29:00:000",
            "13:00:00:000", "12:34:56:789", "44:30:00:000", "13:19:33:999",
            "13:19:33:999", "12:34:00:000", "12:34:56:000"};

        Vector<Cell> c = createNewCells(numOfTests);

        for (int i = 0; i < numOfTests; i++) {
            onset = c.elementAt(i).getOnset();
            offset = c.elementAt(i).getOffset();
            c.elementAt(i).enterText(Cell.ONSET, testInput[i]);
            c.elementAt(i).enterText(Cell.OFFSET, testInput[i]);

            assertTrue(c.elementAt(i).getOnset().getText().equals(
                    expectedTestOutput[i]));
            assertTrue(c.elementAt(i).getOffset().getText().equals(
                    expectedTestOutput[i]));
        }
    }

    /**
     * Test advanced editing the onset and offset timestamps.
     * @throws java.lang.Exception on any error
     *//* BugzID:540
    public void testTimestampAdvancedEditing() throws Exception {
        String[] testInput = {"123456789", "1234", "a13", "12:34:56:789",
                              "4.43", "12:34", "12:78:93:999", "12:34"};

        int numOfTests = testInput.length;

        //advanced Input will be provided between testInput
        Key[][] advancedInput = {{Key.LEFT, Key.LEFT},
           {Key.LEFT, Key.LEFT, Key.RIGHT}, {Key.BACKSPACE, Key.LEFT},
           {Key.BACKSPACE, Key.LEFT, Key.LEFT, Key.LEFT, Key.DELETE, Key.RIGHT},
           {Key.LEFT, Key.RIGHT}, {Key.BACKSPACE, Key.BACKSPACE, Key.BACKSPACE,
                Key.BACKSPACE, Key.BACKSPACE}, {Key.LEFT, Key.LEFT,
                Key.LEFT, Key.LEFT}};

        String[] expectedTestOutput = {"12:34:56:712", "12:31:30:000",
                                       "12:34:56:789", "12:34:50:744",
                                       "44:31:23:400", "12:78:93:999",
                                       "12:78:91:234"};

        Vector<Cell> c = createNewCells(numOfTests);

        for (int i = 0; i < numOfTests - 1; i++) {
            TextBox onset = c.elementAt(i).getOnset();
            TextBox offset = c.elementAt(i).getOffset();
            c.elementAt(i).enterText(Cell.ONSET, testInput[i], advancedInput[i],
                    testInput[i+1]);
            c.elementAt(i).enterText(Cell.OFFSET, testInput[i],
                    advancedInput[i], testInput[i+1]);

            assertTrue(c.elementAt(i).getOnset().getText().equals(
                    expectedTestOutput[i]));
            assertTrue(c.elementAt(i).getOffset().getText().equals(
                    expectedTestOutput[i]));
        }
    }*/

    /**
     * Test pasting the onset and offset timestamps.
     *
     * @throws java.lang.Exception on any error
     */
    public void testTimestampPasting() throws Exception {
        TextBox onset, offset;

        String[] testInput = {"123456789", "6789", "a13", "12:34:56:789",
            "4.43", "127893999", "12:78:93:999", "12:34", "12:34:56"};

        int numOfTests = testInput.length;

        String[] expectedTestOutput = {"12:34:56:789", "68:29:00:000",
            "13:00:00:000", "12:34:56:789", "44:30:00:000", "13:19:33:999",
            "13:19:33:999", "12:34:00:000", "12:34:56:000"};

        Vector<Cell> c = createNewCells(numOfTests);
        for (int i = 0; i < numOfTests; i++) {
            onset = c.elementAt(i).getOnset();
            offset = c.elementAt(i).getOffset();
            Clipboard.putText(testInput[i]);

            // Paste doesn't seem to request focus correctly.
            onset.selectAll();
            onset.pasteFromClipboard();

            offset.selectAll();
            offset.pasteFromClipboard();

            assertTrue(onset.getText().equalsIgnoreCase(
                    expectedTestOutput[i]));
            assertTrue(offset.getText().equalsIgnoreCase(
                    expectedTestOutput[i]));
       }
    }

    /**
     * Test deleting the onset and offset timestamps.
     *
     * @throws java.lang.Exception on any error
     */
    public void testTimestampDeletion() throws Exception {
        String[] testInput = {"123456789", "12:34:56:789", "127893999",
        "12:78:93:999"};

        int numOfTests = testInput.length;

        Vector<Cell> cells = createNewCells(numOfTests);
        for (int i = 0; i < numOfTests; i++) {
            cells.elementAt(i).enterText(Cell.ONSET, testInput[i]);
            cells.elementAt(i).enterText(Cell.OFFSET, testInput[i]);
        }

        //highlight and backspace test
        Cell c = cells.elementAt(0);
        c.selectAllAndTypeKey(Cell.ONSET, Key.BACKSPACE);
        assertTrue(c.getOnset().getText().equals("00:00:00:000"));
        c.selectAllAndTypeKey(Cell.OFFSET, Key.BACKSPACE);
        assertTrue(c.getOffset().getText().equals("00:00:00:000"));

        //highlight and delete test
        c = cells.elementAt(1);
        c.selectAllAndTypeKey(Cell.ONSET, Key.DELETE);
        assertTrue(c.getOnset().getText().equals("00:00:00:000"));
        c.selectAllAndTypeKey(Cell.OFFSET, Key.DELETE);
        assertTrue(c.getOffset().getText().equals("00:00:00:000"));

        //backspace all
        c = cells.elementAt(2);
        c.pressKeys(Cell.ONSET, new Key [] {Key.END});
        int temp = c.getOnset().getText().length();
        for (int i = 0; i < temp + 1; i++) {
            c.enterText(Cell.ONSET, "\u0008");
        }
        assertTrue(c.getOnset().getText().equals("00:00:00:000"));

        c.pressKeys(Cell.OFFSET, new Key [] {Key.END});
        temp = c.getOnset().getText().length();
        for (int i = 0; i < temp + 1; i++) {
            c.enterText(Cell.OFFSET, "\u0008");
        }
        assertTrue(c.getOffset().getText().equals("00:00:00:000"));

        //delete key all
        c = cells.elementAt(3);
        c.pressKeys(Cell.ONSET, new Key [] {Key.HOME});
        temp = c.getOnset().getText().length();
        for (int i = 0; i < temp + 1; i++) {
            c.enterText(Cell.ONSET, "\u007f");
        }
        assertTrue(c.getOnset().getText().equals("00:00:00:000"));

        c.pressKeys(Cell.OFFSET, new Key [] {Key.HOME});
        temp = c.getOnset().getText().length();
        for (int i = 0; i < temp + 1; i++) {
            c.enterText(Cell.OFFSET, "\u007f");
        }
        assertTrue(c.getOffset().getText().equals("00:00:00:000"));
    }

    /**
     * Create a new cell.
     * @throws java.lang.Exception on any error
     */
    private Vector<Cell> createNewCells(int amount) throws Exception {
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

        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        //Create new cell
        for (int i = 0; i < amount; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        return ss.getSpreadsheetColumn(varName).getCells();
    }
}
