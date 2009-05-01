package au.com.nicta.openshapa.uitests;


import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.views.discrete.SpreadsheetPanel;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;




/**
 * Bug 309 Test
 * The Ok button on dialogs should probably be defaulted
 * (ie. respond to Enter/Return key)
 * @author mmuthukrishna
 */
public final class UIBug309 extends UISpecTestCase {

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
    public void enterInsteadOfClicking() throws Exception {
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
        //newVarWindow.getButton("Ok").click();
        //Instead of clicking, just press "Enter"
        Robot r = new Robot();
        r.keyPress(KeyEvent.VK_ENTER);
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