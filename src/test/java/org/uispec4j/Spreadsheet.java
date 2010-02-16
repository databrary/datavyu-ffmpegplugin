package org.uispec4j;

import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.SpreadsheetPanel;
import java.awt.Component;
import java.util.Iterator;
import java.util.Vector;
import junit.framework.Assert;



/**
 *
 */
public class Spreadsheet extends AbstractUIComponent {
    /**
     * UISpec4J convention to declare type.
     */
    public static final String TYPE_NAME = "SpreadsheetPanel";
    /**
     * UISpec4J convention to declare associated class.
     */
    public static final Class[] SWING_CLASSES = {SpreadsheetPanel.class};

    /**
     * Since this is an Adaptor class, this is the class being adapted.
     */
    private SpreadsheetPanel ssPanel;

    /**
     * Spreadsheet constructor.
     * @param spreadsheetPanel actual spreadsheetPanel class being adapted
     */
    public Spreadsheet(final SpreadsheetPanel spreadsheetPanel) {
        Assert.assertNotNull(spreadsheetPanel);
        this.ssPanel = spreadsheetPanel;
    }

    public Component getAwtComponent() {
        return ssPanel;
    }

    public String getDescriptionTypeName() {
        return TYPE_NAME;
    }

    /**
     * Returns the SpreadSheetColumn with the given name.
     * Returns null if not found.
     * @param name name (label) of column spreadsheet
     * @return Column with column name, null if doesn't exist
     */
    public final Column getSpreadsheetColumn(final String name) {
        Vector<SpreadsheetColumn> columns = ssPanel.getColumns();

        //Iterate over columns to find column and return it
        Iterator it = columns.iterator();
        while (it.hasNext()) {
            SpreadsheetColumn col = (SpreadsheetColumn) it.next();

            if (col.getText().startsWith(name)) {
                return new Column(col);
            }
        }
        return null;
    }

     /**
     * Returns all Columns in the Spreadsheet.
     * Returns empty vector if not found.
     * @return Vector<Column> , empty vector if not found
     */
    public final Vector<Column> getColumns() {
        Vector<SpreadsheetColumn> columns = ssPanel.getColumns();
        Vector<Column> returnColumns = new Vector<Column>();
        for (SpreadsheetColumn sc : columns) {
            returnColumns.add(new Column(sc));
        }

        return returnColumns;
    }

    /**
     * Deselect all cells and columns.
     */
    public final void deselectAll() {
        for (Column col : getColumns()) {
            col.deselect();
            for (Cell cell : col.getCells()) {
                cell.setSelected(false);
            }
        }
    }

    /**
     * Returns true if Spreadsheet ss is equal, including order to this.
     * @param ss Spreadsheet to compare to
     * @return true if equal
     */
    public boolean equals(Spreadsheet ss) {
        //Check that all columns equal, including order
        if (ss.getColumns().size() != getColumns().size()) {
            return false;
        }
        for (int i = 0; i < ss.getColumns().size(); i++) {
            Column c1 = ss.getColumns().elementAt(i);
            Column c2 = getColumns().elementAt(i);
            if (!c1.getHeaderName().equals(c2.getHeaderName())) {
                return false;
            }
            if (!c1.getHeaderType().equals(c2.getHeaderType())) {
                return false;
            }

            //Check that all cells within column are equal
            if (c1.getCells().size() != c2.getCells().size()) {
                return false;
            }
            for (int j = 0; j < c1.getCells().size(); j++) {
                Cell cell1 = c1.getCells().elementAt(j);
                Cell cell2 = c2.getCells().elementAt(j);
                if (!cell1.getValueText().equals(cell2.getValueText())) {
                    return false;
                }
                if (!cell1.getOnsetTime().equals(cell2.getOnsetTime())) {
                    return false;
                }
                if (!cell1.getOffsetTime().equals(cell2.getOffsetTime())) {
                    return false;
                }
            }
        }
        return true;
    }

}
