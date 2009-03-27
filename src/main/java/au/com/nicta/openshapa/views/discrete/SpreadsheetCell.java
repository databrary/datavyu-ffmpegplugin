package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.db.Cell;
import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.ExternalDataCellListener;
import au.com.nicta.openshapa.db.IntDataValue;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.ReferenceCell;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.TimeStamp;
import au.com.nicta.openshapa.db.TimeStampDataValue;
import au.com.nicta.openshapa.util.UIConfiguration;
import au.com.nicta.openshapa.views.discrete.datavalues.DataValueView;
import au.com.nicta.openshapa.views.discrete.datavalues.IntDataValueView;
import au.com.nicta.openshapa.views.discrete.datavalues.MatrixViewLabel;
import au.com.nicta.openshapa.views.discrete.datavalues.OffsetView;
import au.com.nicta.openshapa.views.discrete.datavalues.OnsetView;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author  felix
 */
public class SpreadsheetCell extends SpreadsheetElementPanel
implements ExternalDataCellListener, Selectable {

    /** A panel for holding the header to the cell. */
    private SpreadsheetElementPanel topPanel;

    /** A panel for holding the value of the cell. */
    private MatrixViewLabel dataPanel;

    /** The Ordinal display component. */
    private DataValueView ord;

    /** The Onset display component. */
    private OnsetView onset;

    /** The Offset display component. */
    private OffsetView offset;

    /** The Database the cell belongs to. */
    private Database db;

    /** The data cell that this view represents. */
    private DataCell dc;

    /** The cellID for retrieving the cell from the database. */
    private long cellID;

    /** selected state of cell. */
    private boolean selected = false;

    /** The parent selection that could include this cell. */
    private Selector selection;

    /** Onset has been processed and layout position calculated. */
    private boolean onsetProcessed = false;

    /** Border to use for normal cell. No extra information to show. */
    public static final Border NORMAL_BORDER =
        BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(0, 3, 1, 2));

    /** Border to use if cell overlaps with another. */
    public static final Border OVERLAP_BORDER =
        BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK),
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLUE)),
                BorderFactory.createEmptyBorder(0, 3, 0, 2));

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SpreadsheetCell.class);

    /**
     * Creates new form SpreadsheetCell.
     * @param cellDB Database the cell is in
     * @param cell Cell to display
     * @param selector Selector to register the cell with.
     * @throws SystemErrorException if trouble with db calls
     */
    public SpreadsheetCell(final Database cellDB,
                           final Cell cell,
                           final Selector selector)
    throws SystemErrorException {
        db = cellDB;
        cellID = cell.getID();
        selection = selector;
        setName(this.getClass().getSimpleName());

        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(SpreadsheetCell.class);

        // Register this view with the database so that we can get updates when
        // the cell within the database changes.
        if (cell instanceof DataCell) {
            dc = (DataCell)cell;
        } else {
            dc = (DataCell)db.getCell(((ReferenceCell)cell).getTargetID());
        }
        // Check the selected state of the datacell
        // If it is already selected in the database, we need to inform
        // the selector, but not trigger a selection change or deselect others.
        selected = dc.getSelected();
        if (selected) {
            selection.addSelectionSilent(this);
        }

        db.registerDataCellListener(dc.getID(), this);

        // Build components used for the spreadsheet cell.
        topPanel = new SpreadsheetElementPanel();
        ord = new IntDataValueView(selection,
                                   dc,
                                   new IntDataValue(cellDB),
                                   false);
        ord.setFocusable(false);
        ord.setToolTipText(rMap.getString("ord.tooltip"));
        setOrdinal(dc.getOrd());

        onset = new OnsetView(selection,
                           dc,
                           new TimeStampDataValue(cellDB),
                           true);
        onset.setToolTipText(rMap.getString("onset.tooltip"));
        onset.setValue(dc.getOnset());

        offset = new OffsetView(selection,
                                dc,
                                new TimeStampDataValue(cellDB),
                                true);
        offset.setToolTipText(rMap.getString("offset.tooltip"));
        offset.setValue(dc.getOffset());

        dataPanel = new MatrixViewLabel(selection, dc, null);
        dataPanel.setFont(UIConfiguration.spreadsheetDataFont);

        dataPanel.setMatrix(dc.getVal());

        // Set the appearance of the spreadsheet cell.
        setBackground(java.awt.SystemColor.window);
        this.setBorder(NORMAL_BORDER);
        setLayout(new java.awt.BorderLayout());

        // Set the apperance of the top panel and add child elements (ord, onset
        // and offset).
        topPanel.setBackground(java.awt.SystemColor.window);
        topPanel.setLayout(new java.awt.GridLayout(1, 3, 5, 0));
        add(topPanel, java.awt.BorderLayout.NORTH);
        topPanel.add(ord);
        topPanel.add(onset);
        topPanel.add(offset);

        // Set the apperance of the data panel - add elements for displaying the
        // actual data of the panel.
        dataPanel.setBackground(java.awt.SystemColor.window);
        dataPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 1, 5));
        add(dataPanel, java.awt.BorderLayout.WEST);
    }

    /**
     * @return CellID of the SpreadsheetCell.
     */
    public long getCellID() {
        return cellID;
    }

    /**
     * Set the ordinal value.
     *
     * @param ord The new ordinal value to use with this cell.
     * @deprecated The underlying IntDataValue should be altered, not the view.
     */
    private void setOrdinal(int ord) {
        ((IntDataValue)this.ord.getValue()).setItsValue(ord);
        this.ord.updateStrings();
        this.validate();
    }

    /**
     * Get the onset ticks
     * @return Onset time as a long.
     * @throws SystemErrorException
     */
    public long getOnsetTicks() throws SystemErrorException {
        return dc.getOnset().getTime();
    }

    /**
     * Get the offset ticks
     * @return Offset ticks as a long.
     * @throws SystemErrorException
     */
    public long getOffsetTicks() throws SystemErrorException {
        return dc.getOffset().getTime();
    }

    /**
     * @return Return the Ordinal value of the datacell as an IntDataValue.
     */
    public IntDataValue getOrdinal() {
        return ((IntDataValue)this.ord.getValue());
    }

    /**
     * Set the size of the SpreadsheetCell.
     * @param width New width of the SpreadsheetCell.
     * @param height New height of the SpreadsheetCell.
     */
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        dataPanel.setSize(width, dataPanel.getHeight());
    }

    /**
     * Set the width of the SpreadsheetCell.
     * @param width New width of the SpreadsheetCell.
     */
    public void setWidth(int width) {
        this.setSize(width, this.getHeight());
    }

    /**
     * Set the height of the SpreadsheetCell.
     * @param height New height of the SpreadsheetCell.
     */
    public void setHeight(int height) {
        this.setSize(this.getWidth(), height);
    }

    /**
     * Set this cell as selected, a selected cell has a different appearance to
     * an unselcted one (typically colour).
     *
     * @param sel The selection state of the cell, when true the cell is
     * selected false otherwise.
     */
    public void setSelected(boolean sel) {
        selected = sel;

        // Set the selection within the database.
        try {
            Cell cell = db.getCell(this.cellID);
            DataCell dcell = null;
            if (cell instanceof DataCell) {
                dcell = (DataCell)db.getCell(cell.getID());
            } else {
                dcell = (DataCell)db.getCell(((ReferenceCell)cell)
                                                                .getTargetID());
            }
            dcell.setSelected(selected);
            cell.getDB().replaceCell(dcell);
        } catch (SystemErrorException e) {
           logger.error("Failed clicking on SpreadsheetCell.", e);
        }

        // Update the visual representation of the SpreadsheetCell.
        if (selected) {
            topPanel.setBackground(UIConfiguration.spreadsheetSelectedColor);
            dataPanel.setBackground(UIConfiguration.spreadsheetSelectedColor);
            setBackground(UIConfiguration.spreadsheetSelectedColor);
        } else {
            topPanel.setBackground(UIConfiguration.spreadsheetBackgroundColor);
            dataPanel.setBackground(UIConfiguration.spreadsheetBackgroundColor);
            setBackground(UIConfiguration.spreadsheetBackgroundColor);
        }

        repaint();
    }

    /**
     * @return True if the cell is selected, false otherwise.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Called if the DataCell of interest is changed.
     * see ExternalDataCellListener.
     */
    public void DCellChanged(Database db,
                             long colID,
                             long cellID,
                             boolean ordChanged,
                             int oldOrd,
                             int newOrd,
                             boolean onsetChanged,
                             TimeStamp oldOnset,
                             TimeStamp newOnset,
                             boolean offsetChanged,
                             TimeStamp oldOffset,
                             TimeStamp newOffset,
                             boolean valChanged,
                             Matrix oldVal,
                             Matrix newVal,
                             boolean selectedChanged,
                             boolean oldSelected,
                             boolean newSelected,
                             boolean commentChanged,
                             String oldComment,
                             String newComment) {

        if (ordChanged) {
            this.setOrdinal(newOrd);
        }

        if (onsetChanged) {
            onset.setValue(newOnset);
        }

        if (offsetChanged) {
            offset.setValue(newOffset);
        }

        if (valChanged) {
            dataPanel.setMatrix(newVal);
        }

        if (selectedChanged) {
            this.selected = newSelected;
        }

        this.revalidate();
    }

    /**
     * Called if the DataCell of interest is deleted.
     */
    public void DCellDeleted(Database db,
                             long colID,
                             long cellID) {
        // TODO- Figure out how to work with cells that are deleted.
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        selection.addToSelection(me, this);
        requestFocusInWindow();
        me.consume();
    }

    /**
     * @return True if this matrix view is the current focus owner, false
     * otherwise.
     */
    @Override
    public boolean isFocusOwner() {
        return (this.onset.isFocusOwner() || this.offset.isFocusOwner()
                || this.dataPanel.isFocusOwner());
    }

    /**
     * Request to focus this matrix view label, focus will be set to the first
     * element in the formal argument list.
     */
    @Override
    public void requestFocus() {
        this.dataPanel.requestFocus();
    }

    /**
     * @return True if onset been processed and the layout position calculated.
     * False otherwise.
     */
    public boolean isOnsetProcessed() {
        return onsetProcessed;
    }

    /**
     * Set if onset has been processed. Used in the temporal layout algorithm.
     * @param onsetProcessed True to mark that the onset has been processed.
     * False otherwise.
     */
    public void setOnsetProcessed(boolean onsetProcessed) {
        this.onsetProcessed = onsetProcessed;
    }

    /**
     * Set the vertical location for the SpreadsheetCell. Sets the
     * onsetProcessed flag also. Used in the temporal layout algorithm.
     * @param vPos The vertical location in pixels for this cell.
     */
    public void setOnsetvPos(int vPos) {
        setLocation(getX(), vPos);
        setOnsetProcessed(true);
    }

}
