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
        // 1. Retrieve the components
        Window window = getMainWindow();

        MenuBar menuBar = window.getMenuBar();

        // 2a. Create new TEXT variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(
                menuBar.getMenu("Spreadsheet").getSubMenu("New Variable")
                .triggerClick());
        newVarWindow.getTextBox("nameField").insertText("textVar", 0);
        newVarWindow.getRadioButton("text").click();
        assertTrue(newVarWindow.getRadioButton("text").isSelected());
        newVarWindow.getButton("Ok").click();

        //Window spreadsheetWindow = WindowInterceptor.run(
        //menuBar.getMenu("Spreadsheet").getSubMenu("Show Spreadsheet")
        //.triggerClick());
        //UIComponent[] ucArr = window.getUIComponents(Spreadsheet.class);
        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));
        assertNotNull(ss.getSpreadsheetColumn("textVar"));
        assertTrue(ss.getSpreadsheetColumn("textVar").getHeaderName()
                .equals("textVar"));
        assertTrue(ss.getSpreadsheetColumn("textVar").getHeaderType()
                .equals("TEXT"));
        
       /* // 2b. Create new TEXT variable, open spreadsheet and check that it's there

        newVarWindow = WindowInterceptor.run(menuBar.getMenu("Spreadsheet").getSubMenu("New Variable").triggerClick());
        newVarWindow.getTextBox("nameField").insertText("textVar", 0);
        newVarWindow.getRadioButton("text").click();
        assertTrue(newVarWindow.getRadioButton("text").isSelected());
        newVarWindow.getButton("Ok").click();

        //Window spreadsheetWindow = WindowInterceptor.run(menuBar.getMenu("Spreadsheet").getSubMenu("Show Spreadsheet").triggerClick());
        //UIComponent[] ucArr = window.getUIComponents(Spreadsheet.class);
        assertNotNull(ss.getSpreadsheetColumn("textVar"));
        assertTrue(ss.getSpreadsheetColumn("textVar").getHeaderName().equals("textVar"));
        assertTrue(ss.getSpreadsheetColumn("textVar").getHeaderType().equals("TEXT"));*/
    }

    /**
     * Test creating a new PREDICATE variable.
     * @throws java.lang.Exception on any error
     */
    public void testPredicateVariable() throws Exception {
        // 1. Retrieve the components
        Window window = getMainWindow();

        MenuBar menuBar = window.getMenuBar();

        // 2a. Create new PREDICATE variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(
                menuBar.getMenu("Spreadsheet").getSubMenu("New Variable")
                .triggerClick());
        newVarWindow.getTextBox("nameField").insertText("pred var", 0);
        newVarWindow.getRadioButton("predicate").click();
        assertTrue(newVarWindow.getRadioButton("predicate").isSelected());
        newVarWindow.getButton("Ok").click();


        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));
        assertNotNull(ss.getSpreadsheetColumn("pred var"));
        assertTrue(ss.getSpreadsheetColumn("pred var").getHeaderName()
                .equals("pred var"));
        assertTrue(ss.getSpreadsheetColumn("pred var").getHeaderType()
                .equals("PREDICATE"));
    }

    /**
     * Test creating a new INTEGER variable.
     * @throws java.lang.Exception on any error
     */
    public void testIntegerVariable() throws Exception {
        // 1. Retrieve the components
        Window window = getMainWindow();

        MenuBar menuBar = window.getMenuBar();

        // 2a. Create new INTEGER variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(
                menuBar.getMenu("Spreadsheet").getSubMenu("New Variable")
                .triggerClick());
        newVarWindow.getTextBox("nameField").insertText("int var", 0);
        newVarWindow.getRadioButton("integer").click();
        assertTrue(newVarWindow.getRadioButton("integer").isSelected());
        newVarWindow.getButton("Ok").click();


        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));
        assertNotNull(ss.getSpreadsheetColumn("int var"));
        assertTrue(ss.getSpreadsheetColumn("int var").getHeaderName()
                .equals("int var"));
        assertTrue(ss.getSpreadsheetColumn("int var").getHeaderType()
                .equals("INTEGER"));
    }

    /**
     * Test creating a new NOMINAL variable.
     * @throws java.lang.Exception on any error
     */
    public void testNominalVariable() throws Exception {
        // 1. Retrieve the components
        Window window = getMainWindow();

        MenuBar menuBar = window.getMenuBar();

        // 2a. Create new NOMINAL variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(
                menuBar.getMenu("Spreadsheet").getSubMenu("New Variable")
                .triggerClick());
        newVarWindow.getTextBox("nameField").insertText("nom var", 0);
        newVarWindow.getRadioButton("nominal").click();
        assertTrue(newVarWindow.getRadioButton("nominal").isSelected());
        newVarWindow.getButton("Ok").click();


        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));
        assertNotNull(ss.getSpreadsheetColumn("nom var"));
        assertTrue(ss.getSpreadsheetColumn("nom var").getHeaderName()
                .equals("nom var"));
        assertTrue(ss.getSpreadsheetColumn("nom var").getHeaderType()
                .equals("NOMINAL"));
    }

    /**
     * Test creating a new MATRIX variable.
     * @throws java.lang.Exception on any error
     */
    public void testMatrixVariable() throws Exception {
        // 1. Retrieve the components
        Window window = getMainWindow();

        MenuBar menuBar = window.getMenuBar();

        // 2a. Create new MATRIX variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(
                menuBar.getMenu("Spreadsheet").getSubMenu("New Variable")
                .triggerClick());
        newVarWindow.getTextBox("nameField").insertText("matrix var", 0);
        newVarWindow.getRadioButton("matrix").click();
        assertTrue(newVarWindow.getRadioButton("matrix").isSelected());
        newVarWindow.getButton("Ok").click();


        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));
        assertNotNull(ss.getSpreadsheetColumn("matrix var"));
        assertTrue(ss.getSpreadsheetColumn("matrix var").getHeaderName()
                .equals("matrix var"));
        assertTrue(ss.getSpreadsheetColumn("matrix var").getHeaderType()
                .equals("MATRIX"));
    }

    /**
     * Test creating a new FLOAT variable.
     * @throws java.lang.Exception on any error
     */
    public void testFloatVariable() throws Exception {
        // 1. Retrieve the components
        Window window = getMainWindow();

        MenuBar menuBar = window.getMenuBar();

        // 2a. Create new FLOAT variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(
                menuBar.getMenu("Spreadsheet").getSubMenu("New Variable")
                .triggerClick());
        newVarWindow.getTextBox("nameField").insertText("float var", 0);
        newVarWindow.getRadioButton("float").click();
        assertTrue(newVarWindow.getRadioButton("float").isSelected());
        newVarWindow.getButton("Ok").click();


        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));
        assertNotNull(ss.getSpreadsheetColumn("float var"));
        assertTrue(ss.getSpreadsheetColumn("float var").getHeaderName()
                .equals("float var"));
        assertTrue(ss.getSpreadsheetColumn("float var").getHeaderType()
                .equals("FLOAT"));
    }
}