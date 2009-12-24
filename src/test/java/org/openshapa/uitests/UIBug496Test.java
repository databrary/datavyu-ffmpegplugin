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
 * Bug 496:
 * Pasting "2398392310820831" into a new cell, shows 23983 as highlighted.
 * Expected: Nothing highlighted or entire value highlighted.
 */
public final class UIBug496Test extends OpenSHAPAUISpecTestCase {
    /**
     * Bug 496 test.
     *
     * @throws java.lang.Exception on any error
     */
    public void testBug496() throws Exception {
        String varName = "intVar";
        String varType = "INTEGER";
        String varRadio = "integer";

        String testInput = "2398392310820831";

         // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        //1. Create new INTEGER variable,
        //open spreadsheet and check that it's there
        UIUtils.createNewVariable(window, varName, varRadio);
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
        //3. Create new cell, check that they have been created
        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();

        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
        //5. Check copy pasting
        Clipboard.putText(testInput);
        // Delete existing cell contents.
        Cell c = cells.elementAt(0);
        c.selectAllAndTypeKey(Cell.VALUE, Key.DELETE);
        // Paste new contents.
        TextBox t = c.getValue();
        t.pasteFromClipboard();
        assertTrue(t.getText().equalsIgnoreCase("2398392310820831"));
        assertTrue(t.getSelectedText() == null);
    }
}

