package org.openshapa.uitests;

import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.uispec4j.Key;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.utils.KeyUtils;

/**
 * Bug 309 Test
 * The Ok button on dialogs should probably be defaulted
 * (ie. respond to Enter/Return key)
 */
public final class UIBug309Test extends UISpecTestCase {

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
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        getMainWindow().dispose();
        super.tearDown();
    }

    static {
      UISpec4J.init();
    }

    /**
     * Different cell variable types.
     */
    private static final String [] VAR_TYPES = {"TEXT", "PREDICATE", "INTEGER",
        "NOMINAL", "MATRIX", "FLOAT"
        };

    /**
     * Test creating a new variable.
     * Then try to create variable with same name.
     * Type is selected randomly since it should not affect this.
     * @throws java.lang.Exception on any error
     */
    public void testEnterInsteadOfClicking() throws Exception {
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
        //Instead of clicking, just press "Enter"
        KeyUtils.pressKey(newVarWindow.getAwtComponent(), Key.ENTER);
        //check that correct column has been created
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
        assertNotNull(ss.getSpreadsheetColumn(varName));
        assertTrue(ss.getSpreadsheetColumn(varName).getHeaderName()
                .equals(varName));
        assertTrue(ss.getSpreadsheetColumn(varName).getHeaderType()
                .equals(varType));
        //check that column has no cells
        assertTrue(ss.getSpreadsheetColumn(varName).getCells().isEmpty());
    }
}
