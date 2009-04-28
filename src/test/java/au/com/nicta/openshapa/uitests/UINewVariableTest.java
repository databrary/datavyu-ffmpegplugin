package au.com.nicta.openshapa.uitests;


import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.views.discrete.SpreadsheetPanel;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;




/**
 * Test for the New Variable window.
 * @author mmuthukrishna
 */
public final class UINewVariableTest extends UISpecTestCase {

    @Override
    /**
     * @throws java.lang.Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
        setAdapter(new MainClassAdapter(OpenSHAPA.class, new String[0]));
    }

    static {
      UISpec4J.init();
    }

    /**
     * Test creating a new TEXT variable.
     * @throws java.lang.Exception on any error
     */
    public void testTextVariable() throws Exception {
        String varName = "textVar";
        String varType = "TEXT";

        // 1. Retrieve the components
        Window window = getMainWindow();

        MenuBar menuBar = window.getMenuBar();

        // 2a. Create new TEXT variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(
                menuBar.getMenu("Spreadsheet").getSubMenu("New Variable")
                .triggerClick());
        newVarWindow.getTextBox("nameField").insertText(varName, 0);
        newVarWindow.getRadioButton("text").click();
        assertTrue(newVarWindow.getRadioButton("text").isSelected());
        newVarWindow.getButton("Ok").click();


        //check that correct column has been created
        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));
        assertNotNull(ss.getSpreadsheetColumn(varName));
        assertTrue(ss.getSpreadsheetColumn(varName).getHeaderName()
                .equals(varName));
        assertTrue(ss.getSpreadsheetColumn(varName).getHeaderType()
                .equals(varType));

        //check that column has no cells
        assertTrue(ss.getSpreadsheetColumn(varName).getCells().isEmpty());

        //3. Create new cell, check that it has been created and is empty
        //ss.getSpreadsheetColumn(varName).requestFocus();
        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        assertTrue(ss.getSpreadsheetColumn(varName).getCells().size() == 1);
        assertTrue(ss.getSpreadsheetColumn(varName).getCells().elementAt(0).
                getOrd() == 1);
        assertTrue((ss.getSpreadsheetColumn(varName).getCells().elementAt(0).
                getOnsetTime().toString()).equals("00:00:00:000"));
        assertTrue((ss.getSpreadsheetColumn(varName).getCells().elementAt(0).
                getOffsetTime().toString()).equals("00:00:00:000"));
        String value = ss.getSpreadsheetColumn(varName).getCells().elementAt(0).getDataValueV().getChildren().elementAt(0).toString();
        assertTrue(value.equals("<val>"));
        System.err.println(ss.getSpreadsheetColumn(varName).getCells().elementAt(0).getDataValueV().getChildren().elementAt(0).getValue());
        System.err.println(ss.getSpreadsheetColumn(varName).getCells().elementAt(0).getDataValueV().getChildren().size());
        System.err.println(ss.getSpreadsheetColumn(varName).getCells().size());
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

    private void validateVariableType(String varName,
                                      String varType,
                                      String varRadio) throws Exception {
        // 1. Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        // 2a. Create new PREDICATE variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(menuBar.getMenu("Spreadsheet").getSubMenu("New Variable").triggerClick());
        newVarWindow.getTextBox("nameField").insertText(varName, 0);
        newVarWindow.getRadioButton(varRadio).click();
        assertTrue(newVarWindow.getRadioButton(varRadio).isSelected());
        newVarWindow.getButton("Ok").click();
        //check that correct column has been created
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
        assertNotNull(ss.getSpreadsheetColumn(varName));
        assertTrue(ss.getSpreadsheetColumn(varName).getHeaderName().equals(varName));
        assertTrue(ss.getSpreadsheetColumn(varName).getHeaderType().equals(varType));
        //check that column has no cells
        assertTrue(ss.getSpreadsheetColumn(varName).getCells().isEmpty());
    }
}