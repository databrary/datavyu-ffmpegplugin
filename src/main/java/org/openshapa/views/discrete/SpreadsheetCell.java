package org.openshapa.views.discrete;

import com.usermetrix.jclient.Logger;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Box.Filler;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.openshapa.Configuration;
import org.openshapa.OpenSHAPA;

import org.openshapa.models.db.legacy.Cell;
import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.Database;
import org.openshapa.models.db.legacy.ExternalDataCellListener;
import org.openshapa.models.db.legacy.Matrix;
import org.openshapa.models.db.legacy.ReferenceCell;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.models.db.legacy.TimeStamp;

import org.openshapa.views.discrete.datavalues.MatrixRootView;
import org.openshapa.views.discrete.datavalues.TimeStampTextField;
import org.openshapa.views.discrete.datavalues.TimeStampDataValueEditor.TimeStampSource;

import com.usermetrix.jclient.UserMetrix;


/**
 * Visual representation of a spreadsheet cell.
 */
public class SpreadsheetCell extends JPanel implements ExternalDataCellListener,
    MouseListener, FocusListener {

    /** Width of spacer between onset and offset timestamps. */
    private static final int TIME_SPACER = 5;

    /** Border to use when a cell is highlighted. */
    private static final Border HIGHLIGHT_BORDER = new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(175, 175, 175)),
            new MatteBorder(3, 3, 3, 3,
                Configuration.getInstance().getSSSelectedColour()));

    /** Border to use when a cell is highlighted and overlapping cell. */
    private static final Border HIGHLIGHT_OVERLAP_BORDER = new CompoundBorder(
            new CompoundBorder(
                new MatteBorder(0, 0, 1, 0,
                    new Color(175, 175, 175)),
                new MatteBorder(0, 0, 3, 0,
                    Configuration.getInstance().getSSOverlapColour())),
            new MatteBorder(3,
                3, 0, 3, Configuration.getInstance().getSSSelectedColour()));

    /** Border to use when a cell is selected. */
    private static final Border FILL_BORDER = new CompoundBorder(
            new CompoundBorder(
                new MatteBorder(0, 0, 1, 0,
                    new Color(175, 175, 175)),
                new MatteBorder(0, 0, 3, 0,
                    Configuration.getInstance().getSSSelectedColour())),
            new MatteBorder(3,
                3, 0, 3, Configuration.getInstance().getSSSelectedColour()));

    /** Border to use when a cell is selected. */
    private static final Border FILL_OVERLAP_BORDER = HIGHLIGHT_OVERLAP_BORDER;

    /** Border to use for normal cell. No extra information to show. */
    private static final Border NORMAL_BORDER = new CompoundBorder(
            new CompoundBorder(
                new MatteBorder(0, 0, 1, 0,
                    new Color(175, 175, 175)),
                new MatteBorder(0, 0, 3, 0,
                    Configuration.getInstance().getSSBackgroundColour())),
            new MatteBorder(
                3, 3, 0, 3,
                Configuration.getInstance().getSSBackgroundColour()));

    /** Border to use if cell overlaps with another. */
    public static final Border OVERLAP_BORDER = new CompoundBorder(
            new CompoundBorder(
                new MatteBorder(0, 0, 1, 0,
                    new Color(175, 175, 175)),
                new MatteBorder(0, 0, 3, 0,
                    Configuration.getInstance().getSSOverlapColour())),
            new MatteBorder(3,
                3, 0, 3, Configuration.getInstance().getSSBackgroundColour()));

    /** Border to use for normal cell if there is no strut (abuts prev cell). */
    public static final Border STRUT_BORDER = BorderFactory.createMatteBorder(0,
            0, 1, 0, new Color(175, 175, 175));

    /** The panel that displays the cell. */
    private JPanel cellPanel;

    /** A panel for holding the header to the cell. */
    private JPanel topPanel;

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

    /** Highlighted state of cell. */
    private boolean highlighted = false;

    /** Component that sets the width of the cell. */
    private Filler stretcher;

    /** strut creates the gap between this cell and the previous cell. */
    private Filler strut;

    /**
     * The Y location of the visible portion of the cell requested by the active
     * SheetLayout.
     */
    private int layoutPreferredY;

    /**
     * The height of the visible portion of the cell requested by the active
     * SheetLayout.
     */
    private int layoutPreferredHeight;

    /** Onset has been processed and layout position calculated. */
    private boolean onsetProcessed = false;

    /** Does this cell overlap another? */
    private boolean cellOverlap = false;

    /** The spreadsheet cell selection listener. */
    private CellSelectionListener cellSelL;

    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(SpreadsheetCell.class);

    /**
     * Creates new form SpreadsheetCell.
     *
     * @param cellDB Database the cell is in
     * @param cell Cell to display
     * @param listener The spreadsheet cell selection listener to notify of
     * changes to cell selection.
     * @throws SystemErrorException If trouble with db calls
     */
    public SpreadsheetCell(final Database cellDB,
                           final Cell cell,
                           final CellSelectionListener listener)
    throws SystemErrorException {
        db = cellDB;
        cellID = cell.getID();
        setName(this.getClass().getSimpleName());

        ResourceMap rMap = Application.getInstance(OpenSHAPA.class).getContext()
            .getResourceMap(SpreadsheetCell.class);

        // Register this view with the database so that we can get updates when
        // the cell within the database changes.
        DataCell dc = (DataCell) db.getCell(cellID);

        // Check the selected state of the datacell
        // If it is already selected in the database, we need to inform
        // the selector, but not trigger a selection change or deselect others.
        selected = dc.getSelected();
        cellSelL = listener;

        cellPanel = new JPanel();
        cellPanel.addMouseListener(this);
        strut = new Filler(new Dimension(0, 0), new Dimension(0, 0),
                new Dimension(Short.MAX_VALUE, 0));

        setLayout(new BorderLayout());
        this.add(strut, BorderLayout.NORTH);
        this.add(cellPanel, BorderLayout.CENTER);

        // Build components used for the spreadsheet cell.
        topPanel = new JPanel();
        topPanel.addMouseListener(this);
        ord = new JLabel();
        ord.setFont(Configuration.getInstance().getSSLabelFont());
        ord.setForeground(Configuration.getInstance().getSSOrdinalColour());
        ord.setToolTipText(rMap.getString("ord.tooltip"));
        ord.addMouseListener(this);
        ord.setFocusable(true);

        setOrdinal(dc.getOrd());

        onset = new TimeStampTextField(dc, TimeStampSource.Onset);
        onset.setFont(Configuration.getInstance().getSSLabelFont());
        onset.setForeground(Configuration.getInstance().getSSTimestampColour());
        onset.setToolTipText(rMap.getString("onset.tooltip"));
        onset.addFocusListener(this);
        onset.addMouseListener(this);
        onset.setName("onsetTextField");

        offset = new TimeStampTextField(dc, TimeStampSource.Offset);
        offset.setFont(Configuration.getInstance().getSSLabelFont());
        offset.setForeground(Configuration.getInstance()
            .getSSTimestampColour());
        offset.setToolTipText(rMap.getString("offset.tooltip"));
        offset.addFocusListener(this);
        offset.addMouseListener(this);
        offset.setName("offsetTextField");

        dataPanel = new MatrixRootView(dc, null);
        dataPanel.setFont(Configuration.getInstance().getSSDataFont());
        dataPanel.setForeground(Configuration.getInstance()
            .getSSForegroundColour());

        dataPanel.setMatrix(dc.getVal());
        dataPanel.setOpaque(false);
        dataPanel.addFocusListener(this);
        dataPanel.addMouseListener(this);
        dataPanel.setName("cellValue");

        // Set the appearance of the spreadsheet cell.
        cellPanel.setBackground(Configuration.getInstance()
            .getSSBackgroundColour());
        cellPanel.setBorder(HIGHLIGHT_BORDER);
        cellPanel.setLayout(new BorderLayout());

        // Set the apperance of the top panel and add child elements (ord, onset
        // and offset).
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
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

        // Set the apperance of the data panel - add elements for dis6playing
        // the actual data of the panel.
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
    private void setOrdinal(final Integer ordInt) {
        ord.setText(ordInt.toString());
    }

    /**
     * Get the onset ticks
     *
     * @return Onset time as a long.
     * @throws SystemErrorException
     */
    public long getOnsetTicks() throws SystemErrorException {
        DataCell dc = (DataCell) db.getCell(cellID);

        return dc.getOnset().getTime();
    }

    /**
     * Get the offset ticks
     *
     * @return Offset ticks as a long.
     * @throws SystemErrorException
     */
    public long getOffsetTicks() throws SystemErrorException {
        DataCell dc = (DataCell) db.getCell(cellID);

        return dc.getOffset().getTime();
    }

    /**
     * @return Return the Ordinal value of the datacell as an IntDataValue.
     */
    public long getOrdinal() throws SystemErrorException {
        DataCell dc = (DataCell) db.getCell(cellID);

        return dc.getOrd();
    }

    /**
     * Allow a layout to set a preferred y location for the cell. Set to 0 if
     * layout wants default behaviour. (e.g. Ordinal layout)
     *
     * @param y Preferred Y location for the visible portion of the cell.
     */
    public final void setLayoutPreferredY(final int y) {
        layoutPreferredY = y;
        setOnsetvGap(0);
    }

    /**
     * Allow a layout to set a preferred y location for the cell.
     *
     * @param y Preferred Y location for the visible portion of the cell.
     * @param prev SpreadsheetCell previous to this cell.
     */
    public final void setLayoutPreferredY(final int y,
                                          final SpreadsheetCell prev) {
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
     * @return the calculated preferred height for the SpreadsheetCell not
     * including the strut component.
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
     *
     * @return the maximum size of the cell.
     */
    @Override public final Dimension getMaximumSize() {
        Dimension mysize = super.getPreferredSize();

        if ((mysize != null)
                && (mysize.height
                    < (layoutPreferredHeight + strut.getHeight()))) {
            mysize = new Dimension(mysize.width,
                    (layoutPreferredHeight + strut.getHeight()));
        }

        return mysize;
    }

    /**
     * Override Preferred size to return the maximum size (which takes into
     * account the super.preferred size. See getMaximumSize.
     *
     * @return the preferred size of the cell.
     */
    @Override public final Dimension getPreferredSize() {
        return getMaximumSize();
    }

    /**
     * Override Minimum size to return the maximum size (which takes into
     * account the super.preferred size. See getMaximumSize.
     *
     * @return the minimum size of the cell.
     */
    @Override public final Dimension getMinimumSize() {
        return getMaximumSize();
    }

    /**
     * Set the width of the SpreadsheetCell.
     *
     * @param width New width of the SpreadsheetCell.
     */
    public void setWidth(final int width) {
        Dimension d = new Dimension(width, 0);
        stretcher.changeShape(d, d, d);
    }

    /**
     * Mark the cell as selected in the database.
     *
     * @param sel The selection state to use when marking the cell. True if the
     * cell is selected, false otherwise.
     */
    public void selectCellInDB(final boolean sel) {

        // Set the selection within the database.
        try {
            Cell cell = db.getCell(cellID);
            DataCell dcell = null;

            if (cell instanceof DataCell) {
                dcell = (DataCell) db.getCell(cell.getID());
            } else {
                dcell = (DataCell) db.getCell(((ReferenceCell) cell)
                        .getTargetID());
            }

            dcell.setSelected(sel);
            cell.getDB().replaceCell(dcell);

            if (sel) {

                // method names don't reflect usage - we didn't really create
                // this cell just now.
                OpenSHAPA.getProjectController().setLastCreatedColId(
                    cell.getItsColID());
                OpenSHAPA.getProjectController().setLastSelectedCellId(
                    cell.getID());
                OpenSHAPA.getDataController().setFindTime(
                    dcell.getOnset().getTime());
                OpenSHAPA.getDataController().setFindOffsetField(
                    dcell.getOffset().getTime());
            }
        } catch (SystemErrorException e) {
            logger.error("Failed selected cell in SpreadsheetCell.", e);
        }
    }

    /**
     * Set this cell as highlighted, a highlighted cell has a difference
     * appearance to unselected (or fill selected) cell.
     *
     * @param isHighlighted The highlighted state of the cell, true when the
     * cell is highlighted false otherwise.
     */
    public void setHighlighted(final boolean sel) {
        highlighted = sel;
        selectCellInDB(highlighted);

        // Update the visual representation of the SpreadsheetCell.
        if (highlighted) {

            if (cellOverlap) {
                cellPanel.setBorder(HIGHLIGHT_OVERLAP_BORDER);
            } else {
                cellPanel.setBorder(HIGHLIGHT_BORDER);
            }
        } else {

            if (cellOverlap) {
                cellPanel.setBorder(OVERLAP_BORDER);
            } else {
                cellPanel.setBorder(NORMAL_BORDER);
            }
        }
    }

    /**
     * @return True if the cell is highlighted, false otherwise.
     */
    public boolean isHighlighted() {
        return highlighted;
    }

    /**
     * @return True if the cell is filled, false otherwise.
     */
    public boolean isFilled() {

        if (!highlighted && selected) {
            return true;
        }

        return false;
    }

    /**
     * Set this cell as selected, a selected cell has a different appearance to
     * an unselcted one (typically colour).
     *
     * @param sel  The selection state of the cell, when true the cell is
     * selected false otherwise.
     */
    public void setSelected(final boolean sel) {
        selected = sel;
        selectCellInDB(selected);

        // Update the visual representation of the SpreadsheetCell.
        if (selected) {

            if (cellOverlap) {
                cellPanel.setBorder(FILL_OVERLAP_BORDER);
            } else {
                cellPanel.setBorder(FILL_BORDER);
            }

            cellPanel.setBackground(Configuration.getInstance()
                .getSSSelectedColour());
        } else {

            if (cellOverlap) {
                cellPanel.setBorder(OVERLAP_BORDER);
            } else {
                cellPanel.setBorder(NORMAL_BORDER);
            }

            cellPanel.setBackground(Configuration.getInstance()
                .getSSBackgroundColour());
        }
    }

    /**
     * @return True if the cell is selected, false otherwise.
     */
    public boolean isSelected() {
        return (selected || highlighted);
    }

    /**
     * Called if the DataCell of interest is changed. see
     * ExternalDataCellListener.
     */
    public void DCellChanged(final Database db, final long colID,
        final long cellID, final boolean ordChanged, final int oldOrd,
        final int newOrd, final boolean onsetChanged,
        final TimeStamp oldOnset, final TimeStamp newOnset,
        final boolean offsetChanged, final TimeStamp oldOffset,
        final TimeStamp newOffset, final boolean valChanged,
        final Matrix oldVal, final Matrix newVal,
        final boolean selectedChanged, final boolean oldSelected,
        final boolean newSelected, final boolean commentChanged,
        final String oldComment, final String newComment) {

        if (ordChanged) {
            setOrdinal(newOrd);
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
            selected = newSelected;
        }

        revalidate();
    }

    /**
     * Called if the DataCell of interest is deleted.
     */
    public void DCellDeleted(final Database db, final long colID,
        final long cellID) {
        // TODO - Figure out how to work with cells that are deleted.
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
    }

    /**
     * The action to invoke when a mouse button is pressed.
     *
     * @param me The mouse event that triggered this action.
     */
    public void mousePressed(final MouseEvent me) {
        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        boolean groupSel = (me.getModifiers() & keyMask) != 0;
        boolean contSel = (me.getModifiers() & ActionEvent.SHIFT_MASK) != 0;

        Class source = me.getSource().getClass();
        boolean isEditorSrc = (source.equals(TimeStampTextField.class)
                || (source.equals(MatrixRootView.class)));

        // User has clicked in magic spot, without modifier. Clear
        // currently selected cells and select this cell.
        if (!isEditorSrc && !groupSel && !contSel) {
            ord.requestFocus();
            cellSelL.clearCellSelection();
            setSelected(!isSelected());

            if (isSelected()) {
                cellSelL.addCellToSelection(this);
            }

            // User has clicked on editor or magic spot with modifier. Add
            // this cell to the current selection.
        } else if (groupSel && !contSel) {
            ord.requestFocus();
            setSelected(!isSelected());

            if (isSelected()) {
                cellSelL.addCellToSelection(this);
            }

            // User has clicked on editor or magic spot with shift modifier.
            // Add this cell and everything in between the current selection.
        } else if (contSel) {
            ord.requestFocus();
            cellSelL.addCellToContinousSelection(this);

            // User has clicked somewhere in the cell without modifier. This
            // cell needs to be highlighted.
        } else {

            // BugzID:320 - Deselect cells before selected cell contents.
            cellSelL.clearCellSelection();
            setHighlighted(true);
            cellSelL.setHighlightedCell(this);
        }
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
     * The action to invoke when the focus is gained on this component.
     *
     * @param e The focus event that triggered this action.
     */
    public void focusGained(final FocusEvent e) {

        if (highlighted
                && (cellPanel.getBorder().equals(NORMAL_BORDER)
                    || cellPanel.getBorder().equals(OVERLAP_BORDER))) {
            selectCellInDB(true);
        }
    }

    /**
     * The action to invoke when the focus is lost from this component.
     *
     * @param e The focus event that triggered this action.
     */
    public void focusLost(final FocusEvent e) {

        // BugzID: 718 - Make sure content is deselected.
        dataPanel.select(0, 0);
    }

    /**
     * @return True if this matrix view is the current focus owner, false
     * otherwise.
     */
    @Override public boolean isFocusOwner() {
        return (onset.isFocusOwner() || offset.isFocusOwner()
                || dataPanel.isFocusOwner());
    }

    /**
     * Request to focus this cell.
     */
    @Override public void requestFocus() {
        dataPanel.requestFocus();
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
     *
     * @param onsetProcessed True to mark that the onset has been processed.
     * False otherwise.
     */
    public void setOnsetProcessed(final boolean onsetProcessed) {
        this.onsetProcessed = onsetProcessed;

        if (!onsetProcessed) {
            setStrutHeight(0);
        }
    }

    /**
     * Set the vertical location for the SpreadsheetCell. Sets the
     * onsetProcessed flag also. Used in the temporal layout algorithm.
     *
     * @param vPos The vertical location in pixels for this cell.
     */
    public void setOnsetvGap(final int vGap) {
        setStrutHeight(vGap);
        setOnsetProcessed(true);
    }

    /**
     * Set the strut height for the SpreadsheetCell.
     */
    private void setStrutHeight(final int height) {

        if (height == 0) {
            strut.setBorder(null);
        } else {
            strut.setBorder(STRUT_BORDER);
        }

        strut.changeShape(new Dimension(0, height), new Dimension(0, height),
            new Dimension(Short.MAX_VALUE, height));
        validate();
    }

    /**
     * Set the border of the cell.
     *
     * @param overlap true if the cell overlaps with the following cell, false
     * otherwise.
     */
    public void setOverlapBorder(final boolean overlap) {
        cellOverlap = overlap;

        if (cellOverlap) {

            if (highlighted) {
                cellPanel.setBorder(HIGHLIGHT_OVERLAP_BORDER);
            } else {
                cellPanel.setBorder(OVERLAP_BORDER);
            }
        } else {

            if (highlighted) {
                cellPanel.setBorder(HIGHLIGHT_BORDER);
            } else {
                cellPanel.setBorder(NORMAL_BORDER);
            }
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
     *
     * @param g
     */
    @Override public void paint(final Graphics g) {
        // BugzID:474 - Set the size at paint time - somewhere else may have
        // altered the font.
        dataPanel.setFont(Configuration.getInstance().getSSDataFont());
        super.paint(g);
    }
}
