package org.uispec4j;

import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.ColumnHeaderPanel;
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
            ColumnHeaderPanel header = (ColumnHeaderPanel) col.getHeaderPanel();
            if (header.getText().startsWith(name)) {
                return new Column(col);
            }
        }
        return null;
    }

     /**
     * Returns all Columns in the Spreadsheet.
     * Returns empty vector if not found.
     * @param name name (label) of column spreadsheet
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

}
