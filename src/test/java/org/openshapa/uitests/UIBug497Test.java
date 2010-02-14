package org.openshapa.uitests;

import org.openshapa.views.discrete.SpreadsheetPanel;
import java.util.Vector;
import junitx.util.PrivateAccessor;
import org.openshapa.Configuration;
import org.openshapa.util.ConfigProperties;
import org.openshapa.util.UIUtils;
import org.uispec4j.Cell;
import org.uispec4j.Clipboard;
import org.uispec4j.Key;
import org.uispec4j.MenuBar;
import org.uispec4j.OpenSHAPAUISpecTestCase;
import org.uispec4j.Spreadsheet;
import org.uispec4j.TextBox;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;

/**
 * Bug 497:
 * Allows you to paste values greate than max integer and also multiple "-".
 * Example: 999999999999999999-239839231-2398392310820831
 * Pressing -ve on such a number results in unpredictable behaviour.
 */
public final class UIBug497Test extends OpenSHAPAUISpecTestCase {

    /**
     * Initialiser called before each unit test.
     *
     * @throws java.lang.Exception When unable to initialise test
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

     /**
     * Called after each test.
     * @throws Exception on exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    static {
        try {
            ConfigProperties p = (ConfigProperties) PrivateAccessor.getField(Configuration.getInstance(), "properties");
            p.setCanSendLogs(false);
        } catch (Exception e) {
            System.err.println("Unable to overide sending usage logs");
        }
        UISpec4J.init();
    }

    /**
     * Bug 497 test.
     *
     * @throws java.lang.Exception on any error
     */
    public void testBug497Integer() throws Exception {
        String varName = "intVar";
        String varType = "INTEGER";
        String varRadio = "integer";

        String testInput = "999999999999999999-239839231-2398392310820831";

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
        assertTrue(t.getText().equalsIgnoreCase("2999999999999999999"));
    }

    /**
     * Bug 497 test.
     *
     * @throws java.lang.Exception on any error
     */
    public void testBug497Float() throws Exception {
        String varName = "floatVar";
        String varType = "FLOAT";
        String varRadio = "float";

        String testInput = "999999999999999999-239839231-2398392310820831";

        // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        //1. Create new FLOAT variable,
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
        assertTrue(t.getText().equalsIgnoreCase("999999999999999.0"));
    }

}

