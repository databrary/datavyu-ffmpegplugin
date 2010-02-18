package org.fest.swing.fixture;

import java.util.Vector;

import javax.swing.JLabel;

import org.fest.swing.core.Robot;
import org.openshapa.views.discrete.ColumnHeaderPanel;
import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.SpreadsheetColumn;

/**
 * Fixture for Spreadsheet Column.
 */
public class SpreadsheetColumnFixture {
    /** Underlying Spreadsheet Column class. */
    private SpreadsheetColumn ssColumn;

    /** Robot stored here because this fixture does not extend from a FEST.
     * fixture.
     */
    private Robot r;

    /**
     * Constructor.
     * @param robot main frame fixture robot
     * @param spreadsheetColumn underlying class
     */
    public SpreadsheetColumnFixture(final Robot robot,
            final SpreadsheetColumn spreadsheetColumn) {
        ssColumn = spreadsheetColumn;
        r = robot;
    }

    /**
     * @return Returns the column name.
     */
    public final String getColumnName() {
        String headerText =
                ((ColumnHeaderPanel) (ssColumn.getHeaderPanel())).getText();
        String headerName =
                headerText.substring(0, headerText.lastIndexOf("  ("));
        return headerName;
    }

    /**
     * @return String type of column
     */
    public final String getColumnType() {
        String headerText =
                ((ColumnHeaderPanel) (ssColumn.getHeaderPanel())).getText();
        String headerType =
                headerText.substring(headerText.lastIndexOf("(") + 1,
                        headerText.length() - 1);
        return headerType;
    }

    /**
     * Returns fixture for spreadsheet cell based on cell ID number.
     * (starts at 1).
     * @param id cell id number
     * @return Returns fixture for spreadsheet cell based on cell ID number.
     */
    public final SpreadsheetCellFixture cell(final int id) {
        Vector<SpreadsheetCell> colCells = ssColumn.getCells();
        return new SpreadsheetCellFixture(r, colCells.elementAt(id - 1));
    }

    /**
     * @return JLabel Fixture for entire header.
     */
    public final JLabelFixture header() {
        return new JLabelFixture(r, (JLabel) ssColumn.getHeaderPanel());
    }

    /**
     * Returns vector of fixtures for all cells in column.
     * @return Vector of SpreadsheetCellFixture for all cells in column
     */
    public final Vector<SpreadsheetCellFixture> allCells() {
        Vector<SpreadsheetCell> cells = ssColumn.getCells();
        Vector<SpreadsheetCellFixture> result =
                new Vector<SpreadsheetCellFixture>();

        for (SpreadsheetCell c : cells) {
            result.add(new SpreadsheetCellFixture(r, c));
        }
        return result;
    }

    /**
     * @return number of cells in column
     */
    public final int numOfCells() {
        return ssColumn.getCells().size();
    }

    /**
     * Clicks on the column header.
     */
    public final void clickHeader() {
        JLabelFixture labelFixture = header();
        labelFixture.click();
    }

}
