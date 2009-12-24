package org.openshapa.uitests;

import java.io.File;
import java.util.Vector;
import org.uispec4j.interception.WindowInterceptor;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.uispec4j.Cell;
import org.uispec4j.Column;
import org.uispec4j.MenuBar;
import org.uispec4j.OpenSHAPAUISpecTestCase;
import org.uispec4j.Spreadsheet;
import org.uispec4j.Trigger;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;

/**
 * Tests creating cells to left and right of a cell.
 */
public final class UILeftRightCreateCellTest extends OpenSHAPAUISpecTestCase {

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
        UISpec4J.init();
    }

    /**
     * Test creating a cell to the left and right.
     * @throws java.lang.Exception on any error
     */
    public void testCreateCellLeftRight() throws Exception {
        // Retrieve the components and set variable
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

        Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
                window.getUIComponents(Spreadsheet.class)[0]
                .getAwtComponent()));
        Vector<Column> cols = ss.getColumns();

        //2. Select 4th left most cell
        Cell c0 = cols.elementAt(0).getCells().elementAt(3);
        String onset = c0.getOnsetTime().toString();
        String offset = c0.getOffsetTime().toString();
        c0.setSelected(true);

        //Click create right
        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell to Right").click();
        c0.setSelected(false);

        //Check cell exists
        //At the moment this checks the last cell. In the future, may need to
        //change to check actual position
        Cell newCell = cols.elementAt(1).getCells().lastElement();
        assertTrue(newCell.getOnsetTime().equals(onset));
        assertTrue(newCell.getOffsetTime().equals(offset));

        //Do left and right for middle columns
        for (int i = 1; i < cols.size() - 1; i++) {
            Cell thisCell = cols.elementAt(i).getCells().elementAt(3);
            onset = thisCell.getOnsetTime().toString();
            offset = thisCell.getOffsetTime().toString();
            thisCell.setSelected(true);

            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell to Left")
                    .click();
            Cell leftCell = cols.elementAt(i - 1).getCells().lastElement();
            assertTrue(leftCell.getOnsetTime().equals(onset));
            assertTrue(leftCell.getOffsetTime().equals(offset));

            menuBar.getMenu("Spreadsheet").getSubMenu("New Cell to Right")
                    .click();
            Cell rightCell = cols.elementAt(i - 1).getCells().lastElement();
            assertTrue(rightCell.getOnsetTime().equals(onset));
            assertTrue(rightCell.getOffsetTime().equals(offset));

            thisCell.setSelected(false);
        }

        //Do right most cell
        //2. Select 4th left most cell
        Cell cLast = cols.lastElement().getCells().elementAt(3);
        onset = cLast.getOnsetTime().toString();
        offset = cLast.getOffsetTime().toString();
        cLast.setSelected(true);

        //Click create right
        menuBar.getMenu("Spreadsheet").getSubMenu("New Cell to Left").click();
        cLast.setSelected(false);

        //Check cell exists
        //At the moment this checks the last cell. In the future, may need to
        //change to check actual position
        newCell = cols.elementAt(cols.size() - 2).getCells().lastElement();
        assertTrue(newCell.getOnsetTime().equals(onset));
        assertTrue(newCell.getOffsetTime().equals(offset));
    }
}