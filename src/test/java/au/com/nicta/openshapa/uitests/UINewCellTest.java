package au.com.nicta.openshapa.uitests;


import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.views.discrete.SpreadsheetPanel;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JComponent;
import org.uispec4j.Cell;
import org.uispec4j.Key;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.TextBox;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;




/**
 * Test for the New Cells.
 * @author mmuthukrishna
 */
public final class UINewCellTest extends UISpecTestCase {

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
     * Test creating a new INTEGER cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewTextCell() throws Exception {
        String varName = "textVar";
        String varType = "TEXT";
        String varRadio = "text";

        String [] testInput = {"Subject stands up", "$10,432",
        "Hand me the manual!", "Tote_that_bale",  "Jeune fille celebre",
        "If x?7 then x? 2" };

        // 1. Retrieve the components
        Window window = getMainWindow();

        MenuBar menuBar = window.getMenuBar();

        // 2a. Create new TEXT variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(
                menuBar.getMenu("Spreadsheet").getSubMenu("New Variable")
                .triggerClick());
        newVarWindow.getTextBox("nameField").insertText(varName, 0);
        newVarWindow.getRadioButton(varRadio).click();
        newVarWindow.getButton("Ok").click();

        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));

        //3. Create 6 new cell, check that it has been created
        for (int i=0; i < 6; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }

        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        for (int i = 0; i < 6; i++) {
            //4. Test different inputs as per specifications
            Cell c = cells.elementAt(i);
            TextBox t = c.getTextBox(0);

            /*c.pressEditorKey(0, Key.d1);
            c.pressEditorKey(0, Key.A);
            c.pressEditorKey(0, Key.d9);*/
            c.enterEditorText(0, testInput[i].toLowerCase());

            System.err.println(t.getText());
            //assertTrue(t.getText().equalsIgnoreCase(testInput[i]));
        }
    }

    /**
     * Test creating a new INTEGER cell.
     * @throws java.lang.Exception on any error
     */
    public void testNewIntegerCell() throws Exception {
        String varName = "intVar";
        String varType = "INTEGER";
        String varRadio = "integer";

        // 1. Retrieve the components
        Window window = getMainWindow();

        MenuBar menuBar = window.getMenuBar();

        // 2a. Create new TEXT variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(
                menuBar.getMenu("Spreadsheet").getSubMenu("New Variable")
                .triggerClick());
        newVarWindow.getTextBox("nameField").insertText(varName, 0);
        newVarWindow.getRadioButton(varRadio).click();
        newVarWindow.getButton("Ok").click();

        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent())));

        //3. Create 6 new cell, check that it has been created
        for (int i=0; i < 6; i++) {
            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
        }

        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();

        for (int i = 0; i < 6; i++) {
            //4. Test different inputs as per specifications
            Cell c = cells.elementAt(i);
            TextBox t = c.getTextBox(0);

            c.enterEditorText(0, "1a9");

            System.err.println(t.getText());
            assertTrue(t.getText().equalsIgnoreCase("19"));
        }
    } 

    public void simulateKey(KeyEvent e, JComponent c) {
        MouseEvent event = new MouseEvent(c, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 1, false);
        try {
        java.lang.reflect.Field f = AWTEvent.class.getDeclaredField("focusManagerIsDispatching");
        f.setAccessible(true);
        f.set(event, Boolean.TRUE);
        ((Component) c).dispatchEvent(event);
        } catch (Exception e2) {
            
        }
    }

}