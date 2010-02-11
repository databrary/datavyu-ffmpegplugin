package org.fest.swing.fixture;

import java.util.Vector;

import javax.swing.JLabel;

import org.fest.swing.core.Robot;
import org.openshapa.views.discrete.ColumnHeaderPanel;
import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.SpreadsheetColumn;

/**
 * @author mmuthukrishna
 */
public class SpreadsheetColumnFixture {
    private SpreadsheetColumn ssColumn;
    Robot r;

    public SpreadsheetColumnFixture(Robot robot,
            SpreadsheetColumn spreadsheetColumn) {
        ssColumn = spreadsheetColumn;
        r = robot;
    }

    public String getColumnName() {
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

    public SpreadsheetCellFixture cell(int id) {
        Vector<SpreadsheetCell> colCells = ssColumn.getCells();
        return new SpreadsheetCellFixture(r, colCells.elementAt(id - 1));
    }

    public JLabelFixture header() {
        return new JLabelFixture(r, (JLabel) ssColumn.getHeaderPanel());
    }

    public Vector<SpreadsheetCellFixture> allCells() {
        Vector<SpreadsheetCell> cells = ssColumn.getCells();
        Vector<SpreadsheetCellFixture> result =
                new Vector<SpreadsheetCellFixture>();

        for (SpreadsheetCell c : cells) {
            result.add(new SpreadsheetCellFixture(r, c));
        }
        return result;
    }

    public int numOfCells() {
        return ssColumn.getCells().size();
    }

    /**
     * Click on the column header.
     */
    public void clickHeader() {
        JLabelFixture labelFixture = header();
        labelFixture.click();
    }

}
