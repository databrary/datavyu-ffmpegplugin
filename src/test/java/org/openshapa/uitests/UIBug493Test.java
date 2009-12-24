package org.openshapa.uitests;

import org.openshapa.views.discrete.SpreadsheetPanel;
import java.util.Vector;
import org.openshapa.util.UIUtils;
import org.uispec4j.Cell;
import org.uispec4j.Clipboard;
import org.uispec4j.Key;
import org.uispec4j.MenuBar;
import org.uispec4j.OpenSHAPAUISpecTestCase;
import org.uispec4j.Spreadsheet;
import org.uispec4j.TextBox;
import org.uispec4j.Window;

/**
 * Test for Bug 493: Pasting and pressing Enter used to create 2 new lines
 * instead of one.
 */
public final class UIBug493Test extends OpenSHAPAUISpecTestCase {
    /**
     * Bug 493 test.
     * @throws java.lang.Exception on any error
     */
    public void testBug493() throws Exception {
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
        UIUtils.createNewVariable(window, varName, varRadio);
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
            // Paste new contents.
            TextBox t = c.getValue();
            t.pasteFromClipboard();
            t.pressKey(Key.ENTER);
            assertTrue(t.getText().equalsIgnoreCase(expectedTestOutput[i]
                    + "\n"));
        }
    }
}

