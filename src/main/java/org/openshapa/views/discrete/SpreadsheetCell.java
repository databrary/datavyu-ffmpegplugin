package org.openshapa.views.discrete;

import java.awt.BorderLayout;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.Database;
import org.openshapa.models.db.ExternalDataCellListener;
import org.openshapa.models.db.Matrix;
import org.openshapa.models.db.ReferenceCell;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.db.TimeStamp;
import org.openshapa.Configuration;
import org.openshapa.views.discrete.datavalues.MatrixRootView;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Box.Filler;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.border.Border;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.openshapa.models.db.VocabElement;
import org.openshapa.views.discrete.datavalues.TimeStampDataValueEditor.TimeStampSource;
import org.openshapa.views.discrete.datavalues.TimeStampTextField;

/**
 * Visual representation of a spreadsheet cell.
 */
public class SpreadsheetCell extends SpreadsheetElementPanel
implements ExternalDataCellListener, Selectable {

    /** The panel that displays the cell. */
    private SpreadsheetElementPanel cellPanel;

    /** Width of spacer between onset and offset timestamps. */
    private static final int TIME_SPACER = 5;

    /** A panel for holding the header to the cell. */
    private SpreadsheetElementPanel topPanel;

    /** A panel for holding the value of the cell. */
    private MatrixRootView dataPanel;

    /** The Ordinal display component. */
    private JLabel ord;

    /** The Onset display component. */
    private TimeStampTextField onset;

    /** The Offset display component. */
    private TimeStampTextField offset;

    /** The Database the cell belongs to. */
    private Database db;

    /** The cellID for retrieving the cell from the database. */
    private long cellID;

    /** selected state of cell. */
    private boolean selected = false;

    /** The parent selection that could include this cell. */
    private Selector selection;

    /** Component that sets the width of the cell. */
    private Filler stretcher;

    /** strut creates the gap between this cell and the previous cell. */
    private Filler strut;

    /** The Y location of the visible portion of the cell requested by
     * the active SheetLayout. */
    private int layoutPreferredY;

    /** The height of the visible portion of the cell requested by
     * the active SheetLayout. */
    private int layoutPreferredHeight;

    /** Onset has been processed and layout position calculated. */
    private boolean onsetProcessed = false;

    /** Border to use for normal cell. No extra information to show. */
    public static final Border NORMAL_BORDER =
        BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(0, 3, 1, 2));

    /** Border to use for normal cell if there is no strut (abuts prev cell). */
    public static final Border STRUT_BORDER =
        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK);

    /** Border to use if cell overlaps with another. */
    public static final Border OVERLAP_BORDER =
        BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK),
                    BorderFactory.createMatteBorder(0, 0, 3, 0,
                    Configuration.getInstance().getSSOverlapColour())),
                BorderFactory.createEmptyBorder(0, 3, 1, 2));

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
        DataCell dc = (DataCell) OpenSHAPA.getProject().getDB().getCell(cellID);

        // Check the selected state of the datacell
        // If it is already selected in the database, we need to inform
        // the selector, but not trigger a selection change or deselect others.
        selected = dc.getSelected();
        if (selected) {
            selection.addSelectionSilent(this);
        }

        cellPanel = new SpreadsheetElementPanel();
        strut = new Filler(new Dimension(0,0), new Dimension(0,0),
                                            new Dimension(Short.MAX_VALUE, 0));

        this.setLayout(new BorderLayout());
        this.add(strut, BorderLayout.NORTH);
        this.add(cellPanel, BorderLayout.CENTER);

        // Build components used for the spreadsheet cell.
        topPanel = new SpreadsheetElementPanel();
        ord = new JLabel();
        ord.setToolTipText(rMap.getString("ord.tooltip"));

        setOrdinal(dc.getOrd());

        onset = new TimeStampTextField(selection,
                              dc,
                              TimeStampSource.Onset);
        onset.setToolTipText(rMap.getString("onset.tooltip"));

        offset = new TimeStampTextField(selection,
                              dc,
                              TimeStampSource.Offset);
        offset.setToolTipText(rMap.getString("offset.tooltip"));

        dataPanel = new MatrixRootView(selection, dc, null);
        dataPanel.setFont(Configuration.getInstance().getSSDataFont());
        dataPanel.setMatrix(dc.getVal());
        dataPanel.setOpaque(false);

        // Set the appearance of the spreadsheet cell.
        cellPanel.setBackground(Configuration.getInstance()
                                                      .getSSBackgroundColour());
        cellPanel.setBorder(NORMAL_BORDER);
        cellPanel.setLayout(new BorderLayout());

        // Set the apperance of the top panel and add child elements (ord, onset
        // and offset).
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0,0,2,0));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        cellPanel.add(topPanel, BorderLayout.NORTH);
        topPanel.add(ord);
        Component strut1 = Box.createHorizontalStrut(TIME_SPACER);
        topPanel.add(strut1);
        Component glue = Box.createGlue();
        topPanel.add(glue);

        topPanel.add(onset);
        Component strut2 = Box.createHorizontalStrut(TIME_SPACER);
        topPanel.add(strut2);
        topPanel.add(offset);

        // Set the apperance of the data panel - add elements for displaying the
        // actual data of the panel.
        cellPanel.add(dataPanel, BorderLayout.CENTER);
        Dimension d = new Dimension(229, 0);
        stretcher = new Filler(d, d, d);
        cellPanel.add(stretcher, BorderLayout.SOUTH);
    }

    /**
     * @return CellID of the SpreadsheetCell.
     */
    public long getCellID() {
        return cellID;
    }

    /**
     * @return onset view
     */
    public TimeStampTextField getOnset() {
        return onset;
    }

    /**
     * @return offset view
     */
    public TimeStampTextField getOffset() {
        return offset;
    }

    /**
     * Set the ordinal value.
     *
     * @param ord The new ordinal value to use with this cell.
     */
    private void setOrdinal(Integer ordInt) {
        ord.setText(ordInt.toString());
    }

    /**
     * Get the onset ticks
     * @return Onset time as a long.
     * @throws SystemErrorException
     */
    public long getOnsetTicks() throws SystemErrorException {
        DataCell dc = (DataCell) OpenSHAPA.getProject().getDB().getCell(cellID);
        return dc.getOnset().getTime();
    }

    /**
     * Get the offset ticks
     * @return Offset ticks as a long.
     * @throws SystemErrorException
     */
    public long getOffsetTicks() throws SystemErrorException {
        DataCell dc = (DataCell) OpenSHAPA.getProject().getDB().getCell(cellID);
        return dc.getOffset().getTime();
    }

    /**
     * @return Return the Ordinal value of the datacell as an IntDataValue.
     */
    public long getOrdinal() throws SystemErrorException {
        DataCell dc = (DataCell) OpenSHAPA.getProject().getDB().getCell(cellID);
        return dc.getOrd();
    }

    /**
     * Allow a layout to set a preferred y location for the cell.
     * Set to 0 if layout wants default behaviour. (e.g. Ordinal layout)
     * @param y Preferred Y location for the visible portion of the cell.
     */
    public final void setLayoutPreferredY(final int y) {
        layoutPreferredY = y;
        setOnsetvGap(0);
    }

    /**
     * Allow a layout to set a preferred y location for the cell.
     * @param y Preferred Y location for the visible portion of the cell.
     * @param prev SpreadsheetCell previous to this cell.
     */
    public final void setLayoutPreferredY(final int y, SpreadsheetCell prev) {
        layoutPreferredY = y;
        int strutHeight = y;
        if (prev != null) {
            strutHeight -= (prev.getLayoutPreferredY()
                                            + prev.getLayoutPreferredHeight());
        }
        setOnsetvGap(strutHeight);
    }

    /**
     * @return The active layouts preferred Y location for the visible portion
     * of this cell.
     */
    public final int getLayoutPreferredY() {
        return layoutPreferredY;
    }

    /**
     * @return The active layouts preferred height for the visible portion of
     * this cell.
     */
    public final int getLayoutPreferredHeight() {
        return layoutPreferredHeight;
    }

    /**
     * Set a preferred height for the visible portion of the cell.
     */
    public final void setLayoutPreferredHeight(final int height) {
        layoutPreferredHeight = height;
    }

    /**
     * @return the calculated preferred height for the SpreadsheetCell
     * not including the strut component.
     */
    public final int getPreferredHeight() {
        Dimension mysize = super.getPreferredSize();
        int myheight = mysize.height;
        myheight -= strut.getHeight();
        return myheight;
    }

    /**
     * Override Maximum size to return the preferred size or the active layouts
     * preferred height, whichever is greater.
     * @return the maximum size of the cell.
     */
    @Override
    public final Dimension getMaximumSize() {
        Dimension mysize = super.getPreferredSize();
        if (mysize != null && mysize.height <
                                  (layoutPreferredHeight + strut.getHeight())) {
            mysize = new Dimension(mysize.width,
                                   (layoutPreferredHeight + strut.getHeight()));
        }
        return mysize;
    }

    /**
     * Override Preferred size to return the maximum size (which takes into
     * account the super.preferred size. See getMaximumSize.
     * @return the preferred size of the cell.
     */
    @Override
    public final Dimension getPreferredSize() {
        return getMaximumSize();
    }

    /**
     * Override Minimum size to return the maximum size (which takes into
     * account the super.preferred size. See getMaximumSize.
     * @return the minimum size of the cell.
     */
    @Override
    public final Dimension getMinimumSize() {
        return getMaximumSize();
    }

    /**
     * Set the width of the SpreadsheetCell.
     * @param width New width of the SpreadsheetCell.
     */
    public void setWidth(int width) {
        Dimension d = new Dimension(width, 0);
        stretcher.changeShape(d, d, d);
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

            if (selected) {
                // method names don't reflect usage - we didn't really create
                // this cell just now.
                OpenSHAPA.getProject().setLastCreatedColId(cell.getItsColID());
                OpenSHAPA.getProject().setLastSelectedCellId(cell.getID());
                OpenSHAPA.getDataController()
                         .setFindTime(dcell.getOnset().getTime());
                OpenSHAPA.getDataController()
                         .setFindOffsetField(dcell.getOffset().getTime());
            }
        } catch (SystemErrorException e) {
           logger.error("Failed clicking on SpreadsheetCell.", e);
        }

        // Update the visual representation of the SpreadsheetCell.
        if (selected) {
            cellPanel.setBackground(Configuration.getInstance()
                                                 .getSSSelectedColour());
        } else {
            cellPanel.setBackground(Configuration.getInstance()
                                                 .getSSBackgroundColour());
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
            onset.setValue();
        }

        if (offsetChanged) {
            offset.setValue();
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
        // TODO - Figure out how to work with cells that are deleted.
    }

    /**
     * Action to perform on a mouseClick.
     */
    @Override
    public void mousePressed(MouseEvent me) {
        // The cell includes a strut component that keeps it a set distance
        // from the previous cell in the column. A click in that area should
        // not cause a selection
        if (me.getPoint().y > cellPanel.getY()) {
            selection.addToSelection(me, this);
            requestFocusInWindow();
            me.consume();
        }
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
     * Request to focus this cell.
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
        if (!onsetProcessed) {
            setStrutHeight(0);
        }
    }

    /**
     * Set the vertical location for the SpreadsheetCell. Sets the
     * onsetProcessed flag also. Used in the temporal layout algorithm.
     * @param vPos The vertical location in pixels for this cell.
     */
    public void setOnsetvGap(int vGap) {
        setStrutHeight(vGap);
        setOnsetProcessed(true);
    }

    /**
     * Set the strut height for the SpreadsheetCell.
     */
    private void setStrutHeight(int height) {
        if (height == 0) {
            strut.setBorder(null);
        } else {
            strut.setBorder(STRUT_BORDER);
        }
        strut.changeShape(new Dimension(0, height),
                            new Dimension(0, height),
                            new Dimension(Short.MAX_VALUE, height));
        this.validate();
    }

    /**
     * Set the border of the cell.
     * @param overlap true if the cell overlaps with the following cell.
     */
    public void setOverlapBorder(boolean overlap) {
        if (overlap) {
            cellPanel.setBorder(OVERLAP_BORDER);
        } else {
            cellPanel.setBorder(NORMAL_BORDER);
        }
    }

    /**
     * @return The MatrixRootView of this cell.
     */
    public final MatrixRootView getDataView() {
        return dataPanel;
    }

    /**
     * Method to call when painting the component.
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        // BugzID:474 - Set the size at paint time - somewhere else may have
        // altered the font.
        dataPanel.setFont(Configuration.getInstance().getSSDataFont());
        super.paint(g);
    }
}
