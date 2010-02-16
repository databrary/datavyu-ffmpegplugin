package org.openshapa.views.discrete;

import com.usermetrix.jclient.UserMetrix;
import java.awt.Color;
import java.awt.Cursor;
import org.openshapa.models.db.DataColumn;
import org.openshapa.models.db.Database;
import org.openshapa.models.db.ExternalCascadeListener;
import org.openshapa.models.db.ExternalDataColumnListener;
import org.openshapa.models.db.SystemErrorException;
import java.awt.Dimension;
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
implements ExternalDataColumnListener, ExternalCascadeListener,
           MouseListener, MouseMotionListener {

    /** Database reference. */
    private Database database;

    /** Database reference colID of the DataColumn this column displays. */
    private long dbColID;

    /** ColumnDataPanel this column manages. */
    private ColumnDataPanel datapanel;

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(SpreadsheetColumn.class);

    /** Records changes to column during a cascade. */
    private ColumnChanges colChanges;

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

    /**
     * Private class for recording the changes reported by the listener
     * callbacks on this column.
     */
    private final class ColumnChanges {
        /** nameChanged. */
        private boolean nameChanged;
        /** List of cell IDs of newly inserted cells. */
        private Vector<Long> cellInserted;
        /** List of cell IDs of deleted cells. */
        private Vector<Long> cellDeleted;
        /** colDeleted. */
        private boolean colDeleted;

        /**
         * ColumnChanges constructor.
         */
        private ColumnChanges() {
            cellInserted = new Vector<Long>();
            cellDeleted = new Vector<Long>();
            reset();
        }

        /**
         * Reset the ColumnChanges flags and lists.
         */
        private void reset() {
            nameChanged = false;
            cellInserted.clear();
            cellDeleted.clear();
            colDeleted = false;
        }
    }

    /**
     * Creates new SpreadsheetColumn.
     *
     * @param db Database reference.
     * @param colID the database colID this column displays.
     */
    public SpreadsheetColumn(final Database db, final long colID) {
        this.database = db;
        this.dbColID = colID;

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

            datapanel = new ColumnDataPanel(width, dbColumn);

        } catch (SystemErrorException e) {
            logger.error("Problem retrieving DataColumn", e);
        }
        colChanges = new ColumnChanges();
    }

    /**
     * Registers this spreadsheet column with everything that needs to notify
     * this class of events.
     */
    public void registerListeners() {
        try {
            database.registerDataColumnListener(dbColID, this);
            database.registerCascadeListener(this);
            datapanel.registerListeners();
        } catch (SystemErrorException e) {
            logger.error("Unable to register listeners for the column.", e);
        }
    }

    /**
     * Deregisters this spreadsheet column with everything that is currently
     * notiying it of events.
     */
    public void deregisterListeners() {
        try {
            database.deregisterDataColumnListener(dbColID, this);
            database.deregisterCascadeListener(this);
            datapanel.deregisterListeners();
        } catch (SystemErrorException e) {
            logger.error("Unable to register listeners for the column.", e);
        }
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
     * Called at the beginning of a cascade of changes through the database.
     * @param db The database.
     */
    public void beginCascade(final Database db) {
        colChanges.reset();
    }

    /**
     * Called at the end of a cascade of changes through the database.
     * @param db The database.
     */
    public void endCascade(final Database db) {
        if (colChanges.colDeleted) {
            // Not tested yet should be handled by ColumnListener in spreadsheet
            return;
        }

        if (colChanges.cellDeleted.size() > 0) {
            for (Long cellID : colChanges.cellDeleted) {
                datapanel.deleteCellByID(cellID);
            }
        }
        if (colChanges.cellInserted.size() > 0) {
            for (Long cellID : colChanges.cellInserted) {
                datapanel.insertCellByID(db, cellID);
            }
        }

        if (colChanges.nameChanged) {
            try {
                DataColumn dbColumn = db.getDataColumn(dbColID);
                this.setText(dbColumn.getName()
                             + "  (" + dbColumn.getItsMveType() + ")");
            } catch (SystemErrorException e) {
                logger.error("Problem getting data column", e);
            }
        }

        colChanges.reset();
    }

    /**
     * Called when a DataCell is deleted from the DataColumn.
     * @param db The database the column belongs to.
     * @param colID The ID assigned to the DataColumn.
     * @param cellID ID of the DataCell that is being deleted.
     */
    public void DColCellDeletion(final Database db,
                                 final long colID,
                                 final long cellID) {
        colChanges.cellDeleted.add(cellID);
    }


    /**
     * Called when a DataCell is inserted in the vocab list.
     * @param db The database the column belongs to.
     * @param colID The ID assigned to the DataColumn.
     * @param cellID ID of the DataCell that is being inserted.
     */
    public void DColCellInsertion(final Database db,
                                  final long colID,
                                  final long cellID) {
        colChanges.cellInserted.add(cellID);
    }

    /**
     * Called when one fields of the target DataColumn are changed.
     * @param db The database.
     * @param colID The ID assigned to the DataColumn.
     * @param nameChanged indicates whether the name changed.
     * @param oldName reference to oldName.
     * @param newName reference to newName.
     * @param hiddenChanged indicates the hidden field changed.
     * @param oldHidden Old Hidden value.
     * @param newHidden New Hidden value.
     * @param readOnlyChanged indicates the readOnly field changed.
     * @param oldReadOnly Old ReadOnly value.
     * @param newReadOnly New ReadOnly value.
     * @param varLenChanged indicates the varLen field changed.
     * @param oldVarLen Old varLen value.
     * @param newVarLen New varLen value.
     * @param selectedChanged indicates the selection status of the DataColumn
     * has changed.
     * @param oldSelected Old Selected value.
     * @param newSelected New Selected value.
     */
    public void DColConfigChanged(final Database db,
                                  final long colID,
                                  final boolean nameChanged,
                                  final String oldName,
                                  final String newName,
                                  final boolean hiddenChanged,
                                  final boolean oldHidden,
                                  final boolean newHidden,
                                  final boolean readOnlyChanged,
                                  final boolean oldReadOnly,
                                  final boolean newReadOnly,
                                  final boolean varLenChanged,
                                  final boolean oldVarLen,
                                  final boolean newVarLen,
                                  final boolean selectedChanged,
                                  final boolean oldSelected,
                                  final boolean newSelected) {
        colChanges.nameChanged = nameChanged;
    }

    /**
     * Called when the DataColumn of interest is deleted.
     * @param db The database.
     * @param colID The ID assigned to the DataColumn.
     */
    public void DColDeleted(final Database db,
                            final long colID) {
        colChanges.colDeleted = true;
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
        this.setSelected(!this.isSelected());
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
