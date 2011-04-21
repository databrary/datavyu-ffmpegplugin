package org.openshapa.views.discrete;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.HeadlessException;

import java.util.logging.Level;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;


import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.openshapa.Configuration;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.Datastore;
import org.openshapa.models.db.DeprecatedDatabase;
import org.openshapa.models.db.DeprecatedVariable;
import org.openshapa.models.db.Variable;

import org.openshapa.models.db.legacy.Column;
import org.openshapa.models.db.legacy.DataColumn;
import org.openshapa.models.db.legacy.Database;
import org.openshapa.models.db.legacy.ExternalCascadeListener;
import org.openshapa.models.db.legacy.ExternalDataColumnListener;
import org.openshapa.models.db.legacy.LogicErrorException;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.util.Constants;

/**
 * This class maintains the visual representation of the column in the
 * Spreadsheet window.
 */
public final class SpreadsheetColumn extends JLabel
implements ExternalDataColumnListener,
           ExternalCascadeListener,
           MouseListener,
           MouseMotionListener {

    /** Default column width. */
    public static final int DEFAULT_COLUMN_WIDTH = 230;

    /** Default column height. */
    public static final int DEFAULT_HEADER_HEIGHT = 16;

    /** Database reference. */
    private Datastore datastore;

    /** Reference to the variable. */
    private Variable variable;

    /** ColumnDataPanel this column manages. */
    private ColumnDataPanel datapanel;

    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(SpreadsheetColumn.class);

    /** Records changes to column during a cascade. */
    private ColumnChanges colChanges;

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


    /** Layout state: The ordinal we are working on for this column. */
    private int workingOrd = 0;

    /** Layout state: The height of the column data area in pixels. */
    private int dataHeight = 0;

    /** Layout state: The padding to apply to the onset of the current working cell. */
    private int onsetPadding = 0;

    /** Layout state: The padding to apply to the offset of the current working cell. */
    private int offsetPadding = 0;

    /**
     * Creates new SpreadsheetColumn.
     *
     * @param db Database reference.
     * @param colID the variable this column displays.
     * @param cellSelL Spreadsheet cell selection listener to notify
     * @param colSelL Column selection listener to notify.
     */
    public SpreadsheetColumn(final Datastore db,
                             final Variable var,
                             final CellSelectionListener cellSelL,
                             final ColumnSelectionListener colSelL) {
        this.datastore = db;
        this.variable = var;
        this.cellSelList = cellSelL;
        this.columnSelList = colSelL;

        try {
            DataColumn dbColumn = getLegacyDatabase().getDataColumn(getLegacyVariableID());

            setOpaque(true);
            setHorizontalAlignment(JLabel.CENTER);
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, Constants.BORDER_SIZE, Color.black));
            backColor = getBackground();
            setMinimumSize(this.getHeaderSize());
            setPreferredSize(this.getHeaderSize());
            setMaximumSize(this.getHeaderSize());
            addMouseListener(this);
            addMouseMotionListener(this);
            setText(var.getName() + "  (" + dbColumn.getItsMveType() + ")");

            datapanel = new ColumnDataPanel(width, var, cellSelL);
            this.setVisible(!dbColumn.getHidden());
            datapanel.setVisible(!dbColumn.getHidden());

        } catch (SystemErrorException e) {
            logger.error("Problem retrieving DataColumn", e);
        }

        colChanges = new ColumnChanges();
    }

    /**
     * @param padding The working onset padding to use for cells in this column.
     */
    public void setWorkingOnsetPadding(final int padding) {
        onsetPadding = padding;
    }

    /**
     * @param padding The working offset padding to use for cells in this column.
     */
    public void setWorkingOffsetPadding(final int padding) {
        offsetPadding = padding;
    }

    /**
     * @return The onset padding to use for the next cell you are laying in this
     * column
     */
    public int getWorkingOnsetPadding() {
        return onsetPadding;
    }

    /**
     * @return The offset padding to use for the next cell you are laying in
     * this column.
     */
    public int getWorkingOffsetPadding() {
        return offsetPadding;
    }

    /**
     * @return The height in pixels of the cells that have been laid in this
     * column
     */
    public int getWorkingHeight() {
        return dataHeight;
    }

    /**
     * @param newHeight The height in pixels of the cells that have been laid
     * in this column.
     */
    public void setWorkingHeight(final int newHeight) {
        dataHeight = newHeight;
    }

    /**
     * @return The ordinal of the current cell that is being laid in this
     * column.
     */
    public int getWorkingOrd() {
        return workingOrd;
    }

    /**
     * @param newOrd Set the ordinal value of the current cell that we are about
     * to lay.
     */
    public void setWorkingOrd(final int newOrd) {
        workingOrd = newOrd;
    }

    /**
     * @return The next cell that needs to be laid in the column.
     */
    public SpreadsheetCell getWorkingTemporalCell() {
        if (workingOrd < datapanel.getNumCells()) {
            return datapanel.getCellTemporally(workingOrd);
        }

        return null;
    }

    public Variable getModel() {
        return variable;
    }

    @Deprecated public long getLegacyVariableID() {
        return ((DeprecatedVariable) variable).getLegacyVariable().getID();
    }

    /**
     * @return The legacy database.
     */
    @Deprecated public Database getLegacyDatabase() {
        return ((DeprecatedDatabase) datastore).getDatabase();
    }

    /**
     * Registers this spreadsheet column with everything that needs to notify
     * this class of events.
     */
    public void registerListeners() {
        try {
            getLegacyDatabase().registerDataColumnListener(getLegacyVariableID(), this);
            getLegacyDatabase().registerCascadeListener(this);
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
            getLegacyDatabase().deregisterDataColumnListener(getLegacyVariableID(), this);
            getLegacyDatabase().deregisterCascadeListener(this);
            datapanel.deregisterListeners();
        } catch (SystemErrorException e) {
            logger.error("Unable to register listeners for the column.", e);
        }
    }

    /**
     * Opens an input dialog to change a variable name.
     * @throws HeadlessException
     */
    public void showChangeVarNameDialog() throws HeadlessException {
        //Edit variable name on double click
        String newName = "";

        while (newName != null) {
            newName = (String) JOptionPane.showInputDialog(null, null,
                    "New variable name", JOptionPane.PLAIN_MESSAGE, null, null,
                    getColumnName());

            if (newName != null) {

                try {
                    setColumnName(newName);

                    break;
                } catch (LogicErrorException ex) {
                    continue;
                } catch (SystemErrorException ex) {
                    continue;
                }
            } else {
                break;
            }
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
        logger.usage("set column width");
        width = colWidth;

        Dimension dim = getHeaderSize();
        setPreferredSize(dim);
        setMaximumSize(dim);
        revalidate();

        datapanel.setWidth(width);
        datapanel.revalidate();
    }

    /**
     * @return Column Width in pixels.
     */
    @Override public int getWidth() {
        return width;
    }

    /**
     * @return selected status.
     */
    public boolean getSelected() {
        return selected;
    }

    /**
     * @return The datapanel.
     */
    public ColumnDataPanel getDataPanel() {
        return datapanel;
    }

    /**
     * @return The column ID of the datacolumn being displayed.
     */
    public long getColID() {
        return getLegacyVariableID();
    }

    /**
     * Set the selected state for the DataColumn this displays.
     *
     * @param isSelected Selected state.
     */
    public void setSelected(final boolean isSelected) {
        try {
            logger.usage("select column");

            DataColumn dc = getLegacyDatabase().getDataColumn(getLegacyVariableID());
            this.selected = isSelected;

            dc.setSelected(isSelected);
            getLegacyDatabase().replaceColumn(dc);

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
            dc = getLegacyDatabase().getDataColumn(getLegacyVariableID());
        } catch (SystemErrorException e) {
            logger.error("Unable to get selected columns", e);
        }

        return dc.getSelected();
    }

    /**
     * Called at the beginning of a cascade of changes through the database.
     * @param db The database.
     */
    @Override
    public void beginCascade(final Database db) {
        colChanges.reset();
    }

    /**
     * Called at the end of a cascade of changes through the database.
     * @param db The database.
     */
    @Override
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
                datapanel.insertCellByID(db, cellID, cellSelList);
            }
        }

        if (colChanges.nameChanged) {

            try {
                DataColumn dbColumn = db.getDataColumn(getLegacyVariableID());
                this.setText(dbColumn.getName() + "  ("
                    + dbColumn.getItsMveType() + ")");
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
    @Override
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
    @Override
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
    @Override
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
        setVisible(!newHidden);
        this.datapanel.setVisible(!newHidden);
    }

    /**
     * Called when the DataColumn of interest is deleted.
     * @param db The database.
     * @param colID The ID assigned to the DataColumn.
     */
    @Override
    public void DColDeleted(final Database db, final long colID) {
        colChanges.colDeleted = true;
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
    public List<SpreadsheetCell> getCells() {
        return datapanel.getCells();
    }

    public SpreadsheetCell getCellTemporally(final int index) {
        return datapanel.getCellTemporally(index);
    }

    /**
     * @return The Spreadsheet cells in this column temporally.
     */
    public List<SpreadsheetCell> getCellsTemporally() {
        return datapanel.getCellsTemporally();
    }

    /**
     * Request focus for this column. It will request focus for the first
     * SpreadsheetCell in the column if one exists. If no cells exist it
     * will request focus for the datapanel of the column.
     */
    @Override
    public void requestFocus() {
        if (datapanel.getCells().size() > 0) {
            datapanel.getCells().get(0).requestFocusInWindow();
        } else {
            datapanel.requestFocusInWindow();
        }
    }

    /**
     * The action to invoke when the mouse enters this component.
     *
     * @param me The mouse event that triggered this action.
     */
    @Override
    public void mouseEntered(final MouseEvent me) {
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    /**
     * The action to invoke when the mouse exits this component.
     *
     * @param me The mouse event that triggered this action.
     */
    @Override
    public void mouseExited(final MouseEvent me) {
        setCursor(Cursor.getDefaultCursor());
    }

    /**
     * The action to invoke when a mouse button is pressed.
     *
     * @param me The mouse event that triggered this action.
     */
    @Override
    public void mousePressed(final MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is released.
     *
     * @param me The mouse event that triggered this action.
     */
    @Override
    public void mouseReleased(final MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is clicked.
     *
     * @param me The mouse event that triggered this action.
     */
    @Override
    public void mouseClicked(final MouseEvent me) {
        if (me.getClickCount() == 2) {
            showChangeVarNameDialog();
        } else {
            int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

            boolean groupSel = (((me.getModifiers() & ActionEvent.SHIFT_MASK)
                        != 0) || ((me.getModifiers() & keyMask) != 0));
            boolean curSelected = this.isSelected();

            if (!groupSel) {
                this.columnSelList.clearColumnSelection();
            }

            this.setSelected(!curSelected);
            this.columnSelList.addColumnToSelection(this);
        }

        me.consume();
    }

    /**
    * Returns the header name of this SpreadsheetColumn.
    * @param col SpreadsheetColumn
    * @return header name of col
    */
    public String getColumnName() {
        try {
            return getLegacyDatabase().getDataColumn(getLegacyVariableID()).getName();
        } catch (SystemErrorException ex) {
            java.util.logging.Logger.getLogger(SpreadsheetColumn.class
                .getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public void setColumnName(final String newName)
    throws LogicErrorException, SystemErrorException {
        try {
            DataColumn dc = getLegacyDatabase().getDataColumn(getLegacyVariableID());

            if ((!dc.getName().equals(newName)
                        && (DataColumn.isValidColumnName(getLegacyDatabase(), newName)))) {
                dc.setName(newName);
                getLegacyDatabase().replaceColumn(dc);
            }
        } catch (LogicErrorException fe) {
            OpenSHAPA.getApplication().showWarningDialog(fe);
            throw new LogicErrorException(fe.getMessage(), fe);
        } catch (SystemErrorException see) {
            OpenSHAPA.getApplication().showErrorDialog();
            throw new SystemErrorException(see.getMessage(), see);
        }

        OpenSHAPA.getView().showSpreadsheet();
    }

    /**
     * The action to invoke when the mouse is dragged.
     *
     * @param me The mouse event that triggered this action
     */
    @Override
    public void mouseDragged(final MouseEvent me) {
        // BugzID:660 - Implements columns dragging.
        if (draggable) {
            int newWidth = me.getX();

            if (newWidth >= this.getMinimumSize().width) {
                this.setWidth(newWidth);
            }
        }

        if (moveable) {
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

            final int columnWidth = this.getWidth();

            if (me.getX() > columnWidth) {
                int positions = Math.round((me.getX() * 1F) / (columnWidth * 1F));

                SpreadsheetPanel sp = (SpreadsheetPanel) OpenSHAPA.getApplication().getMainView().getComponent();
                sp.moveColumnRight(this.getColID(), positions);

            } else if (me.getX() < 0) {
                int positions = Math.round((me.getX() * -1F) / (columnWidth * 1F));
                SpreadsheetPanel sp = (SpreadsheetPanel) OpenSHAPA.getApplication().getMainView().getComponent();

                sp.moveColumnLeft(this.getColID(), positions);
            }
        }
    }

    /**
     * The action to invoke when the mouse is moved.
     *
     * @param me The mouse event that triggered this action
     */
    @Override
    public void mouseMoved(final MouseEvent me) {
        final int xCoord = me.getX();
        final int componentWidth = this.getSize().width;
        final int rangeStart = Math.round(componentWidth / 4F);
        final int rangeEnd = Math.round(3F * componentWidth / 4F);

        // BugzID:660 - Implements columns dragging.
        if ((componentWidth - xCoord) < 4) {
            setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            draggable = true;

            // BugzID:128 - Implements moveable columns
        } else if ((rangeStart <= xCoord) && (xCoord <= rangeEnd)) {
            moveable = true;
        } else {
            draggable = false;
            moveable = false;
        }
    }

    @Action public void addNewCellToVar() {
//        new NewVariableC();
    }

    /**
    * Private class for recording the changes reported by the listener
    * callbacks on this column.
    */
    private final class ColumnChanges {

        /** nameChanged. */
        private boolean nameChanged;

        /** List of cell IDs of newly inserted cells. */
        private List<Long> cellInserted;

        /** List of cell IDs of deleted cells. */
        private List<Long> cellDeleted;

        /** colDeleted. */
        private boolean colDeleted;

        /**
         * ColumnChanges constructor.
         */
        private ColumnChanges() {
            cellInserted = new ArrayList<Long>();
            cellDeleted = new ArrayList<Long>();
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
}
