/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uispec4j;

import au.com.nicta.openshapa.views.discrete.SpreadsheetColumn;
import au.com.nicta.openshapa.views.discrete.ColumnHeaderPanel;
import au.com.nicta.openshapa.views.discrete.SpreadsheetPanel;
import java.awt.Component;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JPanel;
import junit.framework.Assert;



/**
 *
 * @author uqul
 */
public class Spreadsheet extends AbstractUIComponent {
    public static final String TYPE_NAME = "SpreadsheetPanel";
    public static final Class[] SWING_CLASSES = {SpreadsheetPanel.class};

    private SpreadsheetPanel ssPanel;

    public Spreadsheet(SpreadsheetPanel spreadsheetPanel) {
        Assert.assertNotNull(spreadsheetPanel);
        this.ssPanel = spreadsheetPanel;
    }

    public Component getAwtComponent() {
        return ssPanel;
    }

    public String getDescriptionTypeName() {
        return TYPE_NAME;
    }

    /* Returns the SpreadSheetColumn with the given name. Returns null if not found. */
    public Column getSpreadsheetColumn(String name) {
        Vector<SpreadsheetColumn> columns = ssPanel.getColumns();

        //Iterate over columns to find column and return it
        Iterator it = columns.iterator();
        while(it.hasNext()) {
            SpreadsheetColumn col = (SpreadsheetColumn)it.next();
            ColumnHeaderPanel header = (ColumnHeaderPanel)col.getHeaderPanel();
            if(header.getText().startsWith(name)) {
                return new Column(col);
            }
        }
        return null;


    }

}
