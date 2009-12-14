package org.openshapa.uitests;


import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;




/**
 * Test for the New Variable window.
 *
 */
public final class UINewVariableTest extends UISpecTestCase {

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
     * Test creating a new TEXT variable.
     * @throws java.lang.Exception on any error
     */
    public void testTextVariable() throws Exception {
        String varName = "text var";
        String varType = "TEXT";
        String varRadio = "text";

        //check that column has no cells
        validateVariableType(varName, varType, varRadio);
    }

    /**
     * Test creating a new PREDICATE variable.
     * @throws java.lang.Exception on any error
     */
    public void testPredicateVariable() throws Exception {
        String varName = "pred var";
        String varType = "PREDICATE";
        String varRadio = "predicate";

        //check that column has no cells
        validateVariableType(varName, varType, varRadio);
    }

    /**
     * Test creating a new INTEGER variable.
     * @throws java.lang.Exception on any error
     */
    public void testIntegerVariable() throws Exception {
        String varName = "int var";
        String varType = "INTEGER";
        String varRadio = "integer";

        //check that column has no cells
        validateVariableType(varName, varType, varRadio);
    }

    /**
     * Test creating a new NOMINAL variable.
     * @throws java.lang.Exception on any error
     */
    public void testNominalVariable() throws Exception {
        String varName = "nom var";
        String varType = "NOMINAL";
        String varRadio = "nominal";

        //check that column has no cells
        validateVariableType(varName, varType, varRadio);
    }

    /**
     * Test creating a new MATRIX variable.
     * @throws java.lang.Exception on any error
     */
    public void testMatrixVariable() throws Exception {
        String varName = "matrix var";
        String varType = "MATRIX";
        String varRadio = "matrix";

        //check that column has no cells
        validateVariableType(varName, varType, varRadio);
    }

    /**
     * Test creating a new FLOAT variable.
     * @throws java.lang.Exception on any error
     */
    public void testFloatVariable() throws Exception {
        String varName = "float var";
        String varType = "FLOAT";
        String varRadio = "float";

        //check that column has no cells
        validateVariableType(varName, varType, varRadio);
    }

    /**
     * Creates a new variable and checks that it has been created.
     * @param varName String for variable name
     * @param varType String for variable type
     * @param varRadio String for corresponding radio button for varType
     * @throws java.lang.Exception on any error
     */
    private void validateVariableType(final String varName,
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
    }
}