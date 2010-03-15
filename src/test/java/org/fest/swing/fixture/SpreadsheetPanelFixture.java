package org.fest.swing.fixture;

import java.util.Vector;

import org.fest.swing.core.Robot;
import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.SpreadsheetPanel;

/**
 * Fixture for the Spreadsheet panel.
 */
public class SpreadsheetPanelFixture extends JPanelFixture {

    /** Underlying Spreadsheet panel. */
    private SpreadsheetPanel ssPanel;

    /**
     * Constructor.
     * @param robot main frame fixture robot
     * @param target underlying spreadsheet panel
     */
    public SpreadsheetPanelFixture(final Robot robot,
            final SpreadsheetPanel target) {
        super(robot, target);
        ssPanel = (SpreadsheetPanel)this.target;
    }

    /**
     * Returns fixture for Spreadsheet Column based on column order.
     * (left to right).
     * @param column int of column number, starting at 0 from left.
     * @return SpreadsheetColumnFixture for column, null if not found.
     */
    public final SpreadsheetColumnFixture column(final int column) {
        Vector<SpreadsheetColumn> ssCols = ssPanel.getColumns();

        int count = 0;
        for (SpreadsheetColumn c : ssCols) {            
            if (count == column) {
                return new SpreadsheetColumnFixture(robot, c);
            }
            count++;
        }
        return null;
    }

    /**
     * Returns fixture for Spreadsheet Column based on column variable name.
     * @param columnName name of column variable
     * @return SpreadsheetColumnFixture for column, null if not found.
     */
    public final SpreadsheetColumnFixture column(final String columnName) {
        Vector<SpreadsheetColumn> ssCols = ssPanel.getColumns();        

        for (SpreadsheetColumn c : ssCols) {
            String headerText = c.getText();
            String headerName =
                    headerText.substring(0, headerText.lastIndexOf("  ("));
            if (headerName.equalsIgnoreCase(columnName)) {
                return new SpreadsheetColumnFixture(robot, c);
            }
        }
        return null;
    }

    /**
     * Vector of fixtures for all spreadsheet columns.
     * @return Vector all Spreadsheet columns
     */
    public final Vector<SpreadsheetColumnFixture> allColumns() {
        Vector<SpreadsheetColumn> ssCols = ssPanel.getColumns();
        Vector<SpreadsheetColumnFixture> result =
                new Vector<SpreadsheetColumnFixture>();

        for (SpreadsheetColumn c : ssCols) {
            result.add(new SpreadsheetColumnFixture(robot, c));
        }
        return result;
    }

    /**
     * number of columns in spreadsheet.
     * @return int of number of columns in spreadsheet.
     */
    public final int numOfColumns() {
        return ssPanel.getColumns().size();
    }

    /**
     * Deselects all cells in spreadsheet.
     */
    public final void deselectAll() {
        ssPanel.deselectAll();
    }
}
