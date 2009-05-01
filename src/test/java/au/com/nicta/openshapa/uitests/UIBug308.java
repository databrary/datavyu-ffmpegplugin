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
 * Bug 308 Test
 * When an error occurs (such as a duplicate variable name),
 * the New Variable window just disappears rather than
 * allowing the user to fix the problem.
 * @author mmuthukrishna
 */
public final class UIBug308 extends UISpecTestCase {

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

    private static final String [] varTypes = {"TEXT", "PREDICATE", "INTEGER",
        "NOMINAL", "MATRIX", "FLOAT"
        };

    /**
     * Test creating a new variable.
     * Then try to create variable with same name.
     * Type is selected randomly since it should not affect this.
     * @throws java.lang.Exception on any error
     */
    public void duplicateNameTest() throws Exception {
        String varName = "textVar";
        String varType = varTypes[(int)(Math.random()*5)];
        String varRadio = varType.toLowerCase();

        // 1. Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();
        // 2. Create new variable,
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
        // 3. Create variable with same name
        varType = varTypes[(int)(Math.random()*5)];
        varRadio = varType.toLowerCase();
        newVarWindow = WindowInterceptor.run(menuBar.getMenu("Spreadsheet").getSubMenu("New Variable").triggerClick());
        newVarWindow.getTextBox("nameField").insertText(varName, 0);
        newVarWindow.getRadioButton(varRadio).click();
        assertTrue(newVarWindow.getRadioButton(varRadio).isSelected());
        Window dialog = WindowInterceptor.run(newVarWindow.getButton("Ok").triggerClick());
        assertTrue(dialog.titleContains("Warning:"));
        /*WindowInterceptor.init(newVarWindow.getButton("Ok").triggerClick()).process(new WindowHandler() {
        Trigger process(Window dialog) {
        assertTrue(dialog.titleEquals("Warning:"));
        return dialog.getButton("OK").triggerClick();
        }
        }).run();*/
    }
}