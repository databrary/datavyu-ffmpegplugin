package org.openshapa.uitests;

import java.io.File;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.views.discrete.SpreadsheetPanel;
import java.util.Vector;
import junitx.util.PrivateAccessor;
import org.openshapa.Configuration;
import org.openshapa.util.ConfigProperties;
import org.uispec4j.Cell;
import org.uispec4j.Key;
import org.uispec4j.MenuBar;
import org.uispec4j.OpenSHAPAUISpecTestCase;
import org.uispec4j.Spreadsheet;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;

/**
 * Bug 583.
 * Highlighting float value and pressing zero changes to "."
 */
public final class UIBug583Test extends OpenSHAPAUISpecTestCase {

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
     * Bug 583 test with a range of values, including 0.
     *
     * @throws java.lang.Exception on any error
     */
    public void testBug583() throws Exception {
        String varName = "float";
        String varType = "FLOAT";

        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        assertTrue(demoFile.exists());

         // Retrieve the components
        Window window = getMainWindow();
        MenuBar menuBar = window.getMenuBar();

        // 1. Open and run script to populate database
        WindowInterceptor
                .init(menuBar.getMenu("Script").getSubMenu("Run script")
                    .triggerClick())
                .process(FileChooserHandler.init()
                    .assertIsOpenDialog()
                    .assertAcceptsFilesOnly()
                    .select(demoFile))
                .process(new WindowHandler() {
                    public Trigger process(Window console) {
                        return console.getButton("Close").triggerClick();
                    }
                })
                .run();


        // 2. Get float column
        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
                (window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));

        Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
        assertTrue(cells.size() > 0);
        // 3. Select all and press 0 for each cell.
        for (Cell c : cells) {
            c.selectAllAndTypeKey(Cell.VALUE, Key.d0);
            TextBox t = c.getValue();
            //BugzID:747 - assertTrue(t.getText().equalsIgnoreCase("0.0"));
        }
    }
}

