package org.openshapa.views.discrete;

import com.usermetrix.jclient.UserMetrix;
import java.awt.Color;
import java.awt.Cursor;
import org.openshapa.models.db.DataColumn;
import org.openshapa.models.db.Database;
import org.openshapa.models.db.SystemErrorException;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.openshapa.Configuration;
import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;

/**
 * This class maintains the visual representation of the column in the
 * Spreadsheet window.
 */
public final class SpreadsheetColumn extends JLabel
implements MouseListener, MouseMotionListener {

    /** Database reference. */
    private Database database;

    /** Database reference colID of the DataColumn this column displays. */
    private long dbColID;

    /** ColumnDataPanel this column manages. */
    private ColumnDataPanel datapanel;

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(SpreadsheetColumn.class);

    /** Default column width. */
    private static final int DEFAULT_COLUMN_WIDTH = 230;

    /** Default column height. */
    public static final int DEFAULT_HEADER_HEIGHT = 16;

    /** Width of the column in pixels. */
    private int width = DEFAULT_COLUMN_WIDTH;

    /** Selected state. */
    private boolean selected = false;

    /** Background color of the header when unselected. */
    private Color backColor;

    /** Can the column be dragged? */
    private boolean draggable;

    /** Can the column be moved? */
    private boolean moveable;

    /** cell selection listener to notify of cell selection changes. */
    private CellSelectionListener cellSelList;

    /** column selection listener to notify of column selection changes. */
    private ColumnSelectionListener columnSelList;

    /**
     * Creates new SpreadsheetColumn.
     *
     * @param db Database reference.
     * @param colID the database colID this column displays.
     * @param cellSelL Spreadsheet cell selection listener to notify
     * @param colSelL Column selection listener to notify.
     */
    public SpreadsheetColumn(final Database db,
                             final long colID,
                             final CellSelectionListener cellSelL,
                             final ColumnSelectionListener colSelL) {
        this.database = db;
        this.dbColID = colID;
        this.cellSelList = cellSelL;
        this.columnSelList = colSelL;

        try {
            DataColumn dbColumn = database.getDataColumn(dbColID);

            setOpaque(true);
            setHorizontalAlignment(JLabel.CENTER);
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.black));
            backColor = getBackground();
            setMinimumSize(this.getHeaderSize());
            setPreferredSize(this.getHeaderSize());
            setMaximumSize(this.getHeaderSize());
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            this.setText(dbColumn.getName() + "  ("
                         + dbColumn.getItsMveType() + ")");

            datapanel = new ColumnDataPanel(width, dbColumn, cellSelL);

        } catch (SystemErrorException e) {
            logger.error("Problem retrieving DataColumn", e);
        }
    }

    /**
     * Registers this spreadsheet column with everything that needs to notify
     * this class of events.
     */
    public void registerListeners() {
        datapanel.registerListeners();
    }

    /**
     * Deregisters this spreadsheet column with everything that is currently
     * notiying it of events.
     */
    public void deregisterListeners() {
        datapanel.deregisterListeners();
    }

    /**
     * @return Column Header size as a dimension.
     */
    public Dimension getHeaderSize() {
        return new Dimension(getWidth(), DEFAULT_HEADER_HEIGHT);
    }

    /**
     * Clears the display components from the spreadsheet column.
     */
    public void clear() {
        datapanel.clear();
    }

    /**
     * @param colWidth Column width to set in pixels.
     */
    public void setWidth(final int colWidth) {
        width = colWidth;
        Dimension dim = getHeaderSize();
        this.setPreferredSize(dim);
        this.setMaximumSize(dim);
        Dimension dim2 = getHeaderSize();
        dim2.height = Integer.MAX_VALUE;

        datapanel.setWidth(width);
        for (SpreadsheetCell cell : getCells()) {
            cell.setWidth(width);
        }
        this.revalidate();
        datapanel.revalidate();
        // Whereever we resize we will need to spreadsheetPanel.relayoutCells();
    }

    /**
     * @return Column Width in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return The datapanel.
     */
    public JComponent getDataPanel() {
        return datapanel;
    }

    /**
     * @return The column ID of the datacolumn being displayed.
     */
    public long getColID() {
        return dbColID;
    }

    /**
     * Set the selected state for the DataColumn this displays.
     * @param isSelected Selected state.
     */
    public void setSelected(final boolean isSelected) {
        try {
            DataColumn dc = database.getDataColumn(dbColID);
            this.selected = isSelected;

            dc.setSelected(isSelected);
            database.replaceColumn(dc);

        } catch (SystemErrorException e) {
           logger.error("Failed setting column select state.", e);
        }

        if (selected) {
            setBackground(Configuration.getInstance().getSSSelectedColour());
        } else {
            setBackground(backColor);
        }
        repaint();
    }

    /**
     * @return selection status.
     */
    public boolean isSelected() {
        DataColumn dc = null;
        try {
            dc = database.getDataColumn(dbColID);
        } catch (SystemErrorException e) {
            logger.error("Unable to get selected columns", e);
        }
        return dc.getSelected();
    }

    /**
     * Removes a cell view from the spreadsheet.
     *
     * @param cellID The id of the cell view to remove from the column.
     */
    public void deleteCellByID(final long cellID) {
        datapanel.deleteCellByID(cellID);
    }

    /**
     * Inserts a cell view into the spreadsheet.
     *
     * @param cellID The ID of the cell to add a view for into the spreadsheet.
     */
    public void insertCellByID(final long cellID) {
        datapanel.insertCellByID(database, cellID, cellSelList);
    }

    /**
     * resetLayout changes the layout manager depending on the SheetLayoutType.
     * @param type SheetLayoutType
     */
    public void resetLayoutManager(final SheetLayoutType type) {
        datapanel.resetLayoutManager(type);
    }

    /**
     * Set the preferred size of the column.
     * @param bottom Number of pixels to set.
     */
    public void setBottomBound(final int bottom) {
        if (bottom < 0) {
            datapanel.setPreferredSize(null);
        } else {
            datapanel.setPreferredSize(new Dimension(this.getWidth(), bottom));
        }
    }

    /**
     * @return The SpreadsheetCells in this column.
     */
    public Vector<SpreadsheetCell> getCells() {
        return datapanel.getCells();
    }

    /**
     * Request focus for this column. It will request focus for the first
     * SpreadsheetCell in the column if one exists. If no cells exist it
     * will request focus for the datapanel of the column.
     */
    public void requestFocus() {
        if (datapanel.getCells().size() > 0) {
            datapanel.getCells().firstElement().requestFocusInWindow();
        } else {
            datapanel.requestFocusInWindow();
        }
    }

    /**
     * The action to invoke when the mouse enters this component.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseEntered(final MouseEvent me) {
    }

    /**
     * The action to invoke when the mouse exits this component.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseExited(final MouseEvent me) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * The action to invoke when a mouse button is pressed.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mousePressed(final MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is released.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseReleased(final MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is clicked.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mouseClicked(final MouseEvent me) {
        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        boolean groupSel = ((me.getModifiers() & ActionEvent.SHIFT_MASK) != 0
                       || (me.getModifiers() & keyMask) != 0);
        boolean curSelected = this.isSelected();

        if (!groupSel) {
            this.columnSelList.clearColumnSelection();
        }
        this.setSelected(!curSelected);
        this.columnSelList.addColumnToSelection(this);
    }

    /**
     * The action to invoke when the mouse is dragged.
     *
     * @param me The mouse event that triggered this action
     */
    public void mouseDragged(final MouseEvent me) {
        // BugzID:660 - Implements columns dragging.
        if (draggable) {
            int newWidth = me.getX();
            if (newWidth >= this.getMinimumSize().width) {
                this.setWidth(newWidth);
            }
        }
        if (moveable) {
             setCursor(new Cursor(Cursor.MOVE_CURSOR));
             final int columnWidth = this.getSize().width;
             if (me.getX() > columnWidth) {
                 int positions = Math.round((me.getX() * 1F) / (columnWidth * 1F));
                 SpreadsheetPanel sp = (SpreadsheetPanel) OpenSHAPA
                         .getApplication().getMainView().getComponent();
                 sp.moveColumnRight(this.getColID(), positions);
             } else if (me.getX() < 0) {
                 int positions = Math.round((me.getX() * -1F) / (columnWidth * 1F));
                 SpreadsheetPanel sp = (SpreadsheetPanel) OpenSHAPA
                         .getApplication().getMainView().getComponent();
                 sp.moveColumnLeft(this.getColID(), positions);
             }
        }
    }

    /**
     * The action to invoke when the mouse is moved.
     *
     * @param me The mouse event that triggered this action
     */
    public void mouseMoved(final MouseEvent me) {
        final int xCoord = me.getX();
        final int componentWidth = this.getSize().width;
        final int rangeStart = Math.round(componentWidth / 4F);
        final int rangeEnd = Math.round(3F * componentWidth / 4F);

        // BugzID:660 - Implements columns dragging.
        if (componentWidth - xCoord < 4) {
            setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            draggable = true;
        // BugzID:128 - Implements moveable columns
        } else if ((rangeStart <= xCoord) && (xCoord <= rangeEnd)) {
            moveable = true;
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            draggable = false;
            moveable = false;
        }
    }
}
