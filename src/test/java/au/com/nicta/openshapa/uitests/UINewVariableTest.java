/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.uitests;


import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;
import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.views.discrete.SpreadsheetPanel;
import org.uispec4j.MenuBar;
import org.uispec4j.Spreadsheet;
import org.uispec4j.UIComponent;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;




/**
 *
 * @author uqul
 */
public class UINewVariableTest extends UISpecTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        setAdapter(new MainClassAdapter(OpenSHAPA.class, new String[0]));
    }

    static {
      UISpec4J.init();
    }

    public void testTextVariable() throws Exception {
        // 1. Retrieve the components
        Window window = getMainWindow();

        // 2. Create new variable, open spreadsheet and check that it's there. Do for each type.
        MenuBar menuBar = window.getMenuBar();

        Window newVarWindow = WindowInterceptor.run(menuBar.getMenu("Spreadsheet").getSubMenu("New Variable").triggerClick());
        newVarWindow.getTextBox("nameField").insertText("textVar", 0);
        newVarWindow.getRadioButton("text").click();
        assertTrue(newVarWindow.getRadioButton("text").isSelected());
        newVarWindow.getButton("Ok").click();

        //Window spreadsheetWindow = WindowInterceptor.run(menuBar.getMenu("Spreadsheet").getSubMenu("Show Spreadsheet").triggerClick());
        UIComponent[] ucArr = window.getUIComponents(Spreadsheet.class);
        Spreadsheet ss = new Spreadsheet(((SpreadsheetPanel)(ucArr[0].getAwtComponent())));
        assertNotNull(ss.getSpreadsheetColumn("textVar"));

        //assertNotNull(((Spreadsheet)(window.findUIComponent(Spreadsheet.class))).getSpreadsheetColumn("textVar"));
    }

}