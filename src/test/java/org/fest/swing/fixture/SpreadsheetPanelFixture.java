/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fest.swing.fixture;

import java.util.Vector;

import org.fest.swing.core.Robot;
import org.openshapa.views.discrete.ColumnHeaderPanel;
import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.SpreadsheetPanel;

/**
 * @author mmuthukrishna
 */
public class SpreadsheetPanelFixture extends JPanelFixture {

    SpreadsheetPanel ssPanel;

    public SpreadsheetPanelFixture(Robot robot, SpreadsheetPanel target) {
        super(robot, target);
        ssPanel = target;
    }

    public SpreadsheetColumnFixture column(int column) {
        Vector<SpreadsheetColumn> ssCols = ssPanel.getColumns();

        int count = 0;
        for (SpreadsheetColumn c : ssCols) {
            count++;
            if (count == column) {
                return new SpreadsheetColumnFixture(robot, c);
            }
        }
        return null;
    }

    public SpreadsheetColumnFixture column(String columnName) {
        Vector<SpreadsheetColumn> ssCols = ssPanel.getColumns();

        for (SpreadsheetColumn c : ssCols) {
            String headerText =
                    ((ColumnHeaderPanel) (c.getHeaderPanel())).getText();
            String headerName =
                    headerText.substring(0, headerText.lastIndexOf("  ("));
            if (headerName.equalsIgnoreCase(columnName)) {
                return new SpreadsheetColumnFixture(robot, c);
            }
        }
        return null;
    }

    public Vector<SpreadsheetColumnFixture> allColumns() {
        Vector<SpreadsheetColumn> ssCols = ssPanel.getColumns();
        Vector<SpreadsheetColumnFixture> result =
                new Vector<SpreadsheetColumnFixture>();

        for (SpreadsheetColumn c : ssCols) {
            result.add(new SpreadsheetColumnFixture(robot, c));
        }
        return result;
    }

    public int numOfColumns() {
        return ssPanel.getColumns().size();
    }

    public void deselectAll() {
        ssPanel.deselectAll();
    }
}
