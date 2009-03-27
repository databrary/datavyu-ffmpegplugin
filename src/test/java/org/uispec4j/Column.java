package org.uispec4j;

import au.com.nicta.openshapa.views.discrete.SpreadsheetColumn;
import au.com.nicta.openshapa.views.discrete.ColumnHeaderPanel;
import java.awt.Component;
import junit.framework.Assert;



/**
 *
 * @author mmuthukrishna
 */
public class Column extends AbstractUIComponent {
    /**
     * UISpec4J convention to declare type.
     */
    public static final String TYPE_NAME = "SpreadsheetColumn";
    /**
     * UISpec4J convention to declare associated class.
     */
    public static final Class[] SWING_CLASSES = {SpreadsheetColumn.class};

    /**
     * Since this is an Adaptor class, this is the class being adapted.
     */
    private SpreadsheetColumn ssColumn;

    /**
     * Column constructor.
     * @param spreadsheetColumn actual spreadsheetColumn class being adapted
     */
    public Column(final SpreadsheetColumn spreadsheetColumn) {
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
     * @return String name of column
     */
    public final String getHeaderName() {
        String headerText = ((ColumnHeaderPanel)
                (ssColumn.getHeaderPanel())).getText();
        String headerName = headerText.substring(0,
                headerText.lastIndexOf("  ("));
        return headerName;
    }

    /**
     * @return String type of column
     */
    public final String getHeaderType() {
        String headerText = ((ColumnHeaderPanel)
                (ssColumn.getHeaderPanel())).getText();
        String headerType = headerText.substring(
                headerText.lastIndexOf("(") + 1, headerText.length() - 1);
        return headerType;
    }

    /**
     * returns cells in header - based on type, start time, endtime or ID
     */


}
