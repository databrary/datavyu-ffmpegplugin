package org.openshapa.uitests;


import java.util.Vector;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
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
        "4.43", "127893999", "12:78:93:999"};

        int numOfTests = testInput.length;

        String[] expectedTestOutput = {"12:34:56:789", "68:29:00:000",
        "13:00:00:000", "12:34:56:789", "44:30:00:000", "13:19:33:999",
        "13:19:33:999"};

        Vector <Cell> c = createNewCells(numOfTests);

        for (int i = 0; i < numOfTests; i++) {
            onset = c.elementAt(i).getOnset();
            offset = c.elementAt(i).getOffset();
            c.elementAt(i).enterOnsetText(testInput[i]);
            c.elementAt(i).enterOffsetText(testInput[i]);

            assertTrue(c.elementAt(i).getOnset().getText().equals(expectedTestOutput[i]));
            assertTrue(c.elementAt(i).getOffset().getText().equals(expectedTestOutput[i]));
        }
    }

    public void testTimestampPasting() throws Exception {
        TextBox onset, offset;

        String[] testInput = {"123456789", "6789", "a13", "12:34:56:789",
        "4.43", "127893999", "12:78:93:999"};

        int numOfTests = testInput.length;

        String[] expectedTestOutput = {"12:34:56:789", "68:29:00:000",
        "00:00:00:000", "12:34:56:789", "00:00:00:000", "13:19:33:999",
        "13:19:33:999"};

        Vector <Cell> c = createNewCells(numOfTests);

        Clipboard clip = null;
        for (int i = 0; i < numOfTests; i++) {
            onset = c.elementAt(i).getOnset();
            offset = c.elementAt(i).getOffset();
            clip.putText(testInput[i]);

            // Paste doesn't seem to request focus correctly.
            onset.pasteFromClipboard();
            offset.pasteFromClipboard();
            assertTrue(onset.getText().equalsIgnoreCase(expectedTestOutput[i]));
            assertTrue(offset.getText().equalsIgnoreCase(expectedTestOutput[i]));
        }
    }

    /**
     * Create a new cell.
     * @throws java.lang.Exception on any error
     */
    private Vector <Cell> createNewCells(int amount) throws Exception {
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
        for (int i=0; i < amount; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }
        return ss.getSpreadsheetColumn(varName).getCells();
    }

}
