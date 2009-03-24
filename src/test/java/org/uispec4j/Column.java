/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uispec4j;

import au.com.nicta.openshapa.views.discrete.SpreadsheetColumn;
import au.com.nicta.openshapa.views.discrete.ColumnHeaderPanel;
import java.awt.Component;
import javax.swing.JLabel;
import junit.framework.Assert;
import org.uispec4j.finder.ComponentFinder;



/**
 *
 * @author uqul
 */
public class Column extends AbstractUIComponent {
    public static final String TYPE_NAME = "spreadsheet column";
    public static final Class[] SWING_CLASSES = {JLabel.class};

    private SpreadsheetColumn ssColumn;

    private ComponentFinder finder;

    public Column(SpreadsheetColumn spreadsheetColumn) {
        Assert.assertNotNull(spreadsheetColumn);
        this.ssColumn = spreadsheetColumn;
    }

    public Component getAwtComponent() {
        return ssColumn.getHeaderPanel();
    }

    public String getDescriptionTypeName() {
        return TYPE_NAME;
    }

    /**
     * returns column header name
     */
    public String getHeaderName() {
        String headerText = ((ColumnHeaderPanel)(ssColumn.getHeaderPanel())).getText();
        String headerName = headerText.substring(0, headerText.lastIndexOf("  ("));
        return headerName;
    }

    /**
     * returns column header type
     */
    public String getHeaderType() {
        String headerText = ((ColumnHeaderPanel)(ssColumn.getHeaderPanel())).getText();
        String headerType = headerText.substring(headerText.lastIndexOf("(")+1, headerText.length()-1);
        return headerType;
    }

    /**
     * returns cells in header - based on type, start time, endtime or ID
     */


}
