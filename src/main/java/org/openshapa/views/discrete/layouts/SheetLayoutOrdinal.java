package org.openshapa.views.discrete.layouts;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import org.openshapa.views.discrete.ColumnDataPanel;
import org.openshapa.views.discrete.SpreadsheetCell;
import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.SpreadsheetView;

/**
 * SheetLayoutOrdinal implements the ordinal style layout of SpreadsheetCells
 * in the spreadsheet.
 */
public class SheetLayoutOrdinal extends SheetLayout {
    // The of the right hand margin.
    private int marginSize;

    /**
     * SheetLayoutOrdinal constructor.
     * @param cols Reference to the SpreadsheetColumns in the spreadsheet.
     */
    public SheetLayoutOrdinal(final int margin) {
        marginSize = margin;
    }

    
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Dimension result = new Dimension(1000, 1000);

        // This layout can only be applied to Column Data Panels.
        /*
        ColumnDataPanel panel = (ColumnDataPanel) parent;

        for (Component c : panel.getCells()) {
            int width = Math.max(c.getWidth(), (int) result.getWidth());
            int height = ((int) result.getHeight()) + c.getHeight();

            result.setSize(width, height);
        }

        int width = Math.max(panel.getNewCellButton().getWidth(), (int) result.getWidth());
        int height = ((int) result.getHeight()) + panel.getNewCellButton().getHeight();
        int containerHeight = parent.getParent().getParent().getHeight();   
        result.setSize(width, Math.max(height, containerHeight));*/

        return result;
    }

    @Override
    public void layoutContainer(Container parent) {
        super.layoutContainer(parent);
        System.err.println("****************************");

        /*
        // This layout must be applied to a Spreadsheet panel.
        JScrollPane pane = (JScrollPane) parent;
        SpreadsheetView mainView = (SpreadsheetView) pane.getViewport().getView();

        for (SpreadsheetColumn col : mainView.getColumns()) {
            System.err.println("Laying Column - ordinal");
            int ord = 1;
            int currentHeight = 0;
            for (SpreadsheetCell cell : col.getCells()) {
                Dimension d = cell.getPreferredSize();
                System.err.println("Laying Cell: [" + cell.getBounds().x + ", " + cell.getBounds().y + ", " + cell.getBounds().width + ", " + cell.getBounds().height + "]");
                cell.setBounds(0, currentHeight, col.getWidth(), (int) d.getHeight());
                System.err.println("Laid Cell: [" + cell.getBounds().x + ", " + cell.getBounds().y + ", " + cell.getBounds().width + ", " + cell.getBounds().height + "]");
                cell.setOrdinal(ord);
                cell.repaint();
                ord++;
                currentHeight += d.getHeight();
            }

            // Put the new cell button at the end of the column.
            Dimension d = col.getDataPanel().getNewCellButton().getPreferredSize();
            col.getDataPanel().getNewCellButton().setBounds(0, currentHeight, parent.getWidth(), (int) d.getHeight());
            currentHeight += (int) d.getHeight();

            col.getDataPanel().setBounds(col.getDataPanel().getX(),
                                         col.getDataPanel().getY(),
                                         col.getDataPanel().getWidth(),
                                         currentHeight);

            System.err.println("View: [" + mainView.getBounds().x + ", " + mainView.getBounds().y + ", " + mainView.getBounds().width + ", " + mainView.getBounds().height + "]");

            //col.repaint();
            System.err.println("Data Col: [" + col.getDataPanel().getX() + ", " + col.getDataPanel().getY() + ", " + col.getDataPanel().getWidth() + ", " + currentHeight + "]");
            System.err.println("Col: [" + col.getBounds().x + ", " + col.getBounds().y + ", " + col.getBounds().width + ", " + col.getBounds().height + "]");
        }*/
        
        //padColumn(parent, panel, currentHeight, 1.0);
    }
}
