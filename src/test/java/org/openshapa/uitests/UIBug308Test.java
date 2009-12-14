package org.openshapa.uitests;

import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowHandler;

/**
 * Bug 308 Test
 * When an error occurs (such as a duplicate variable name),
 * the New Variable window just disappears rather than
 * allowing the user to fix the problem.
 */
public final class UIBug308Test extends UISpecTestCase {

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
        OpenSHAPA.getApplication().cleanUpForTests();
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
    public void testDuplicateName() throws Exception {
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
        // 3. Create variable with same name
        //Test should fail regardless of variable type, so select new variable
        //type
        varType = VAR_TYPES[(int) (Math.random() * VAR_TYPES.length)];
        varRadio = varType.toLowerCase();
        newVarWindow = WindowInterceptor.run(menuBar.getMenu("Spreadsheet")
                .getSubMenu("New Variable").triggerClick());
        newVarWindow.getTextBox("nameField").insertText(varName, 0);
        newVarWindow.getRadioButton(varRadio).click();
        assertTrue(newVarWindow.getRadioButton(varRadio).isSelected());
        WindowInterceptor.init(newVarWindow.getButton("Ok").triggerClick())
                .process(new WindowHandler() {

            public Trigger process(Window dialog) {
                assertTrue(dialog.titleContains("Warning:"));
                return dialog.getButton("OK").triggerClick();
            }
        }).run();
    }
}