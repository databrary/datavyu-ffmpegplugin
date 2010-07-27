package org.fest.swing.fixture;

import static org.fest.reflect.core.Reflection.field;

import java.util.Vector;
import javax.swing.JButton;

import org.fest.swing.core.Robot;
import org.openshapa.views.discrete.ColumnDataPanel;

import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.SpreadsheetColumn;


/**
 * Fixture for Spreadsheet Column.
 */
public class SpreadsheetColumnFixture extends JLabelFixture {

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
        super(robot, spreadsheetColumn);
        ssColumn = spreadsheetColumn;
        r = robot;
    }

    /**
     * @return Returns the column name.
     */
    public final String getColumnName() {
        String headerText = ssColumn.getText();
        String headerName = headerText.substring(0,
                headerText.lastIndexOf("  ("));

        return headerName;
    }

    /**
     * @return String type of column
     */
    public final String getColumnType() {
        String headerText = ssColumn.getText();
        String headerType = headerText.substring(headerText.lastIndexOf("(")
                + 1,
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
        SpreadsheetCellFixture result = new SpreadsheetCellFixture(r,
                colCells.elementAt(id - 1));

        if (result.getID() == id) {
            return result;
        } else {
            int count = 1;
            int i = id - 2;
            int j = id;

            while (count < numOfCells()) {

                if (i > -1) {
                    result = new SpreadsheetCellFixture(r,
                            colCells.elementAt(i));

                    if (result.getID() == id) {
                        return result;
                    }

                    count++;
                    i--;
                }

                if (j < numOfCells()) {
                    result = new SpreadsheetCellFixture(r,
                            colCells.elementAt(j));

                    if (result.getID() == id) {
                        return result;
                    }

                    count++;
                    j++;
                }
            }
        }

        return new SpreadsheetCellFixture(r, colCells.elementAt(id - 1));
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

    public final void pressNewCellButton() {
        new JButtonFixture(robot,
                (JButton) field("newCellButton").ofType(JButton.class).in(
                    (ColumnDataPanel)ssColumn.getDataPanel()).get()).click();
    }

    /**
     * @return true if column is selected, else false
     */
    public boolean isSelected() {
        return ssColumn.getSelected();
    }
}
