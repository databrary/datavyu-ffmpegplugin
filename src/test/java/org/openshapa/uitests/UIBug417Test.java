package org.openshapa.uitests;

import junitx.util.PrivateAccessor;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.openshapa.Configuration;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.Column;
import org.openshapa.util.ConfigProperties;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.uispec4j.MenuBar;
import org.uispec4j.OpenSHAPAUISpecTestCase;
import org.uispec4j.Spreadsheet;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.BasicHandler;

/**
 * Bug 417 Test
 * Check that reserved vocab variable names give a different error message to
 * already existing variables.
 * Also make sure variations of reserved vocabulary are allowed.
 */
public final class UIBug417Test extends OpenSHAPAUISpecTestCase {

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
     * @throws Exception on any error
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
     * Different cell variable types.
     */
    private static final String [] VAR_TYPES = {"TEXT", "PREDICATE", "INTEGER",
        "NOMINAL", "MATRIX", "FLOAT"
        };

    /**
     * Resource map to access error messages in resources.
     */
    private ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(Column.class);


    /**
     * Test creating a variable with the same name
     * Type is selected randomly since it should not affect this.
     * @throws java.lang.Exception on any error
     */
    public void testDuplicateName() throws Exception {
        final String varName = "textVar";
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
        WindowInterceptor
                .init(newVarWindow.getButton("Ok").triggerClick())
                .process(BasicHandler.init()
                    .assertContainsText(rMap.getString("Error.exists", varName))
                    .triggerButtonClick("OK"))
                .run();
    }

    /**
     * Test creating a variable with a reserved name.
     * @throws java.lang.Exception on any error
     */
    public void testReservedName() throws Exception {
        final String varName = "ge";
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
        WindowInterceptor
                .init(newVarWindow.getButton("Ok").triggerClick())
                .process(BasicHandler.init()
                    .assertContainsText(rMap.getString("Error.system", varName))
                    .triggerButtonClick("OK"))
                .run();
    }

    /**
     * Test invalid column name.
     * @throws java.lang.Exception on any error
     */
    public void testInvalidColumnName() throws Exception {
        final String varName = "(hello)";
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
        WindowInterceptor
                .init(newVarWindow.getButton("Ok").triggerClick())
                .process(BasicHandler.init()
                    .assertContainsText(rMap.getString(
                    "Error.invalid", varName))
                    .triggerButtonClick("OK"))
                .run();

        window.dispose();
    }
}