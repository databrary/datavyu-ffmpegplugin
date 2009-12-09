package org.openshapa.uitests;

import java.io.File;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.Table;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.BasicHandler;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;

/**
 * Test the creation of a new database.
 *
 */
public final class UIVariableListTest extends UISpecTestCase {

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

     /**
     * Different cell variable types.
     */
    private static final String[] VAR_TYPES = {"TEXT", "PREDICATE", "INTEGER",
        "NOMINAL", "MATRIX", "FLOAT"
    };


    static {
        UISpec4J.setWindowInterceptionTimeLimit(120000);
        UISpec4J.init();
    }

     /**
     * Test adding new variables with a script.
     * @throws java.lang.Exception on any error
     */
    public void testAddingVariablesWithScript() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Open and run script to populate database
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsOpenDialog()
                    .assertAcceptsFilesOnly()
                    .select(demoFile.getAbsolutePath()))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();


        // 1a. Check that variable list is populated with correct data
        Window varListWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Variable List").triggerClick());
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
        assertTrue(varListWindow.getTable().getRowCount()
                == ss.getColumns().size());
            for (int j = 0; j < ss.getColumns().size(); j++) {
                assertTrue(inTable(ss.getColumns().elementAt(j).getHeaderName(),
                        varListWindow.getTable(), 1));
                assertTrue(inTable(ss.getColumns().elementAt(j).getHeaderType(),
                        varListWindow.getTable(), 2));
            }
    }

    /**
     * Test adding new variables manually.
     * @throws java.lang.Exception on any error
     */
    public void testAddingVariablesManually() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        String [] varNames = {"text", "predicate", "integer", "nominal",
        "matrix", "float"};
        String [] varTypes = {"TEXT", "PREDICATE", "INTEGER",
        "NOMINAL", "MATRIX", "FLOAT"};

        // 1. Create a new variable, then check that the variable list
        // is populated with correct data.
        for (int i = 0; i < varNames.length; i++) {
            createNewVariable(varNames[i], varTypes[i]);

            // 1a. Check that variable list is populated with correct data
            Window varListWindow = WindowInterceptor.run(menuBar.getMenu(
                    "Spreadsheet").getSubMenu("Variable List").triggerClick());
            Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                    window.getUIComponents(Spreadsheet.class)[0]
                    .getAwtComponent()));
            assertTrue(varListWindow.getTable().getRowCount() == ss.getColumns()
                    .size());
            for (int j = 0; j < ss.getColumns().size(); j++) {
                assertTrue(inTable(ss.getColumns().elementAt(j).getHeaderName(),
                        varListWindow.getTable(), 1));
                assertTrue(inTable(ss.getColumns().elementAt(j).getHeaderType(),
                        varListWindow.getTable(), 2));
            }
        }
    }

    /**
     * Test removal with new database.
     * @throws java.lang.Exception on any error
     */
    public void testRemovalWithNewDatabase() throws Exception {
        //Preparation
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Open and run script to populate database
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

                WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsOpenDialog()
                    .assertAcceptsFilesOnly()
                    .select(demoFile.getAbsolutePath()))
                .process(new WindowHandler() {
                    public Trigger process(final Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();

        // 1a. Check that variable list is populated
         Window varListWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Variable List").triggerClick());
         assertTrue(varListWindow.getTable().getRowCount() > 0);

        // 2. Create new database (and discard unsaved changes)
        WindowInterceptor
            .init(menuBar.getMenu("File").getSubMenu("New").triggerClick())
            .process(BasicHandler.init().triggerButtonClick("OK"))
            .process(new WindowHandler() {
                public Trigger process(final Window newDBWindow) {
                    newDBWindow.
                            getTextBox("nameField").setText("newDB");
                    return newDBWindow.getButton("Ok").triggerClick();
                }
             })
             .run();

        // 2b. Check that variable list is empty
        //BugzID:430
        //assertTrue(varListWindow.getTable().getRowCount() == 0);
                 varListWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("Variable List").triggerClick());
         assertTrue(varListWindow.getTable().getRowCount() == 0);
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
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
        assertNotNull(ss.getSpreadsheetColumn(varName));
        assertTrue(ss.getSpreadsheetColumn(varName).getHeaderName()
                .equals(varName));
        assertTrue(ss.getSpreadsheetColumn(varName).getHeaderType()
                .equals(varType));
        //check that column has no cells
        assertTrue(ss.getSpreadsheetColumn(varName).getCells().isEmpty());
    }

    /**
     * Create a new variable.
     * @param varName String for the name of the variable
     * @param varType String for the variable type
     * @throws java.lang.Exception on any error
     */
    private void createNewVariable(final String varName,
            final String varType) throws Exception {
        String varRadio = varType.toLowerCase();
        // 1. Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        // 2a. Create new variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("New Variable").triggerClick());
        newVarWindow.getTextBox("nameField").insertText(varName, 0);
        newVarWindow.getRadioButton(varRadio).click();
        newVarWindow.getButton("Ok").click();
    }

    /**
     * Checks if String is in a Table column.
     * @param item String to find
     * @param t Table to look in
     * @param column Column number
     * @return true if found, else false
     */
    private Boolean inTable(final String item, final Table t,
            final int column) {
        for (int i = 0; i < t.getRowCount(); i++) {
            if (item.equals(t.getContentAt(i, column))) {
                return true;
            }
        }
        return false;
    }
}