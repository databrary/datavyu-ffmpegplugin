/*
 * SpreadsheetCell.java
 *
 * Created on Feb 2, 2007, 1:14 PM
 */

package au.com.nicta.openshapa.disc.spreadsheet;

import au.com.nicta.openshapa.db.Cell;
import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.DataValue;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.ExternalDataCellListener;
import au.com.nicta.openshapa.db.IntDataValue;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.ReferenceCell;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.TimeStamp;
import au.com.nicta.openshapa.db.TimeStampDataValue;
import au.com.nicta.openshapa.util.UIConfiguration;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author  felix
 */
public class SpreadsheetCell
    extends     javax.swing.JPanel
    implements  MouseListener, ExternalDataCellListener {

    /** The Ordinal display component. */
    private DataViewLabel ord;
    /** The Ordinal display component. */
    private DataViewLabel onset;
    /** The Ordinal display component. */
    private DataViewLabel offset;
    /** The Ordinal display component. */
    private DataViewLabel value;

    /** The Database the cell belongs to. */
    private Database db;

    /** The cellID for retrieving from db. */
    private long cellID;

    /** selected state of cell. */
    private boolean selected = false;
    /** Config. */
    private static UIConfiguration uiconfig = new UIConfiguration();

    /** User dimensions. */
    private Dimension userDimensions = new Dimension(0, 0);

    /** Used in GetTime calls. */
    public static final int MILLISECONDS  = 1;
    /** Used in GetTime calls. */
    public static final int SECONDS       = 2;
    /** Used in GetTime calls. */
    public static final int MINUTES       = 3;
    /** Used in GetTime calls. */
    public static final int HOURS         = 4;
    /** Used in GetTime calls. */
    public static final int DAYS          = 5;

    /** TBA. */
    public static final int HEIGHT_MULT   = 50;

    /** TBA. */
    public static final String DATEFORMAT = "MM/dd/yyyy HH:mm:ss:SSS";
    /** TBA. */
    public static final SimpleDateFormat DATEFORMATER =
                                               new SimpleDateFormat(DATEFORMAT);

    /** TBA. */
    private int divType = SECONDS;
    /** TBA. */
    private double divValue = 1;

    /** TBA. */
    private boolean temporalSizeChanged = false;

    /** Show the Ordinal. */
    private boolean showOrd = true;
    /** Show the Onset. */
    private boolean showOnset = true;
    /** Show the Offset. */
    private boolean showOffset = true;
    /** Show the Data. */
    private boolean showData = true;


    /**
     * Creates new form SpreadsheetCell.
     * @param db Database the cell is in
     * @param cell Cell to display
     * @throws SystemErrorException if trouble with db calls
     */
    public SpreadsheetCell(final Database db, final Cell cell)
                                                throws SystemErrorException {
        IntDataValue ordDV = new IntDataValue(db);
        TimeStampDataValue onsetDV = new TimeStampDataValue(db);
        TimeStampDataValue offsetDV = new TimeStampDataValue(db);

        this.db = db;
        this.cellID = cell.getID();

        this.ord    = new DataViewLabel(ordDV,  false, false, false);
        this.onset  = new DataViewLabel(onsetDV, true, false, false);
        this.offset = new DataViewLabel(offsetDV, true, false, false);
        this.value  = new DataViewLabel(null, true, true, true);

        initComponents();

        this.topPanel.add(ord);
        this.topPanel.add(onset);
        this.topPanel.add(offset);
        this.dataPanel.add(value, BorderLayout.CENTER);
        //    this.column = column;

        this.addMouseListener(this);
        this.topPanel.addMouseListener(this);
        this.dataPanel.addMouseListener(this);

        this.updateDimensions();

        DataCell dc = null;
        if (cell instanceof DataCell) {
            dc = (DataCell)cell;
        } else {
            dc = (DataCell)db.getCell(((ReferenceCell)cell).getTargetID());
        }
        db.registerDataCellListener(dc.getID(), this);
        this.setOrdinal(dc.getOrd());
        this.setOnset(dc.getOnset());
        this.setOffset(dc.getOffset());
        this.setValue(dc.getVal());
    }

    /** Set the ordinal value. */
    private void setOrdinal(int ord)
    {
        ((IntDataValue)this.ord.getValue()).setItsValue(ord);
        this.ord.updateStrings();
        this.repaint();
    }

    /** Set the Onset value. */
    private void setOnset(TimeStamp newonset) throws SystemErrorException {
        ((TimeStampDataValue)this.onset.getValue()).setItsValue(newonset);
        this.onset.updateStrings();
        this.repaint();
    }

    /** Set the offset value */
    private void setOffset(TimeStamp newoffset) throws SystemErrorException {
        ((TimeStampDataValue)this.offset.getValue()).setItsValue(newoffset);
        this.offset.updateStrings();
        this.repaint();
    }

    /** Get the Time value. */
    public final static double getTime(int type, long time) {
        switch (type) {
            case MILLISECONDS: {
                return (time);
            }
            case SECONDS: {
                return (time/1000.0);
            }
            case MINUTES: {
                return (time/(1000*60));
            }
            case HOURS: {
                return (time/(1000*60*60));
            }
            case DAYS: {
                return (time/(1000*60*60*24));
            }
        }

        return (-1);
    }

    /** Change in the div type causes change to height of cell. */
    public void setDivision(int type, double value)
                                                   throws SystemErrorException {
        this.divType = type;
        this.divValue = value;

        // Calculate the height given the division
        double diff = getTime(type, this.onset) - getTime(type, this.offset);
        this.setHeight((int)Math.round((diff * SpreadsheetCell.HEIGHT_MULT)
                                        / value));
    }

    /**
     * Get division value
     * @return division value for the cell
     */
    public double getDivisionValue()
    {
        return (this.divValue);
    }

    /**
     * Get division type
     * @return division type for the cell
     */
    public double getDivisionType()
    {
        return (this.divType);
    }

    /**
     * Get the time of the cell
     * @param type type of the time value
     * @param timeLabel label containing the time value
     * @return time value for the cell
     * @throws SystemErrorException if db cals fail
     */
    public final static double getTime(int type, DataViewLabel timeLabel)
       throws SystemErrorException
    {
        long t = ((TimeStampDataValue)timeLabel
                                        .getValue()).getItsValue().getTime();
        return (getTime(type, t));
    }

    /**
     * Convert a time value
     * @param type type of the time value
     * @param time time to convert
     * @return double time value
     */
    public final static double getTime(int type, Date time)
    {
        return (getTime(type, time.getTime()));
    }

    /**
     * Get the onset time
     * @return Onset time as a double
     * @throws SystemErrorException
     */
    public double getOnsetTime() throws SystemErrorException {
        return (getTime(this.divType,
          ((TimeStampDataValue)this.onset.getValue()).getItsValue().getTime()));
    }

    /**
     *
     * @return
     * @throws SystemErrorException
     */
    public double getOffsetTime() throws SystemErrorException {
        return (getTime(this.divType,
         ((TimeStampDataValue)this.offset.getValue()).getItsValue().getTime()));
    }

    /**
     *
     * @param dv
     */
    private void setValue(DataValue dv) {
        this.value.setValue(dv);
    }

    /**
     *
     * @param dv
     */
    private void setValue(Matrix m) throws SystemErrorException {
        for (int i = 0; i < m.getNumArgs(); i++) {
            setValue(m.getArgCopy(i));
        }
    }

    /**
     *
     * @return
     */
    public IntDataValue getOrdinal() {
        return ((IntDataValue)this.ord.getValue());
    }

    /**
     *
     * @return
     * @throws au.com.nicta.openshapa.db.SystemErrorException
     */
    public TimeStampDataValue getOnset() throws SystemErrorException {
        return ((TimeStampDataValue)this.onset.getValue());
    }

    /**
     *
     * @return
     * @throws au.com.nicta.openshapa.db.SystemErrorException
     */
    public TimeStampDataValue getOffset() throws SystemErrorException {
        return ((TimeStampDataValue)this.offset.getValue());
    }

    /**
     *
     * @return
     */
    public DataValue getValue() {
        return (this.value.getValue());
    }

    /**
     *
     * @param width
     * @param height
     */
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.userDimensions = new Dimension(width, height);
    }

    /**
     *
     * @param width
     */
    public void setWidth(int width) {
        this.setSize(width, this.getHeight());
    }

    /**
     *
     * @param height
     */
    public void setHeight(int height) {
        this.setSize(this.getWidth(), height);
    }

    /**
     *
     * @return
     */
    public int getMinimumHeight() {
        FontMetrics fm =
                this.getFontMetrics(UIConfiguration.spreadsheetTimeStampFont);
        FontMetrics fm1 =
                this.getFontMetrics(UIConfiguration.spreadsheetDataFont);
        return(fm.getHeight() + fm1.getHeight());
    }

    /**
     *
     * @return
     */
    @Override
    public Dimension getPreferredSize()
    {
        if ((this.userDimensions.width > 0)
                && (this.userDimensions.height > 0)) {
            return (this.userDimensions);
        }

        return (super.getPreferredSize());
    }

    /**
     *
     * @return
     */
    @Override
    public Dimension getMinimumSize() {
        if ((this.userDimensions.width > 0)
                && (this.userDimensions.height > 0)) {
        return (this.userDimensions);
        }

        return (super.getMinimumSize());
    }

    /**
     *
     * @return
     */
    @Override
    public Dimension getMaximumSize()
    {
        if ((this.userDimensions.width > 0)
                && (this.userDimensions.height > 0)) {
            return (this.userDimensions);
        }

        return (super.getMaximumSize());
    }

    /**
     *
     */
    public void updateDimensions() {
        Rectangle r = this.getBounds();
        FontMetrics fm =
                this.getFontMetrics(UIConfiguration.spreadsheetTimeStampFont);

        int totalWidth = 4;
        int totalHeight = 4;

        totalWidth += this.ord.getMinimumSize().width;
        totalWidth += this.onset.getMinimumSize().width;
        totalWidth += this.offset.getMinimumSize().width;

        if (showOrd || showOnset || showOffset) {
            totalHeight += fm.getHeight();
        }
        this.ord.setVisible(showOrd);
        this.onset.setVisible(showOnset);
        this.offset.setVisible(showOffset);

        if ((this.userDimensions.width > 0)
                && (this.userDimensions.height > 0)) {
            totalWidth = this.userDimensions.width;
            totalHeight = this.userDimensions.height;
        }

        this.value.setWrapWidth(totalWidth);
        Dimension d = this.value.getMaximumSize();
        if (showData) {
            totalHeight += d.getHeight();
        }
        this.value.setVisible(showData);
    }

    /**
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        this.updateDimensions();
        if (selected) {
            topPanel.setBackground(UIConfiguration.spreadsheetSelectedColor);
            dataPanel.setBackground(UIConfiguration.spreadsheetSelectedColor);
        } else {
            topPanel.setBackground(UIConfiguration.spreadsheetBackgroundColor);
            dataPanel.setBackground(UIConfiguration.spreadsheetBackgroundColor);
        }
        super.paintComponent(g);
    }

    /**
     *
     * @param me
     */
    @Override
    public void mouseEntered(MouseEvent me) {
    }

    /**
     *
     * @param me
     */
    @Override
    public void mouseExited(MouseEvent me) {
    }

    /**
     *
     * @param me
     */
    @Override
    public void mousePressed(MouseEvent me) {
    }

    /**
     *
     * @param me
     */
    @Override
    public void mouseReleased(MouseEvent me) {
    }

    /**
     *
     * @param me
     */
    @Override
    public void mouseClicked(MouseEvent me) {
        try {
            selected = !selected;
            Cell cell = db.getCell(this.cellID);
            DataCell dc = null;
            if (cell instanceof DataCell) {
                dc = (DataCell)db.getCell(cell.getID());
            } else {
                dc = (DataCell)db.getCell(((ReferenceCell)cell).getTargetID());
            }
            dc.setSelected(selected);
            cell.getDB().replaceCell(dc);
        } catch (SystemErrorException see) {

        }
        this.repaint();
    }

    /**
     *
     */
    @Override
    public void DCellChanged(Database   db,
                           long       colID,
                           long       cellID,
                           boolean    ordChanged,
                           int        oldOrd,
                           int        newOrd,
                           boolean    onsetChanged,
                           TimeStamp  oldOnset,
                           TimeStamp  newOnset,
                           boolean    offsetChanged,
                           TimeStamp  oldOffset,
                           TimeStamp  newOffset,
                           boolean    valChanged,
                           Matrix     oldVal,
                           Matrix     newVal,
                           boolean    selectedChanged,
                           boolean    oldSelected,
                           boolean    newSelected,
                           boolean    commentChanged,
                           String     oldComment,
                           String     newComment) {
        try {
            if (ordChanged) {
                this.setOrdinal(newOrd);
            }

            if (onsetChanged) {
                this.setOnset(newOnset);
            }

            if (offsetChanged) {
                this.setOffset(newOffset);
            }

            if (valChanged) {
                this.value.setValue(newVal.getArgCopy(0));
            }

            if (selectedChanged) {
                this.selected = newSelected;
            }
        } catch (SystemErrorException see) {

        }

        this.repaint();
    }

    /**
     *
     */
    @Override
    public void DCellDeleted(Database  db,
                           long      colID,
                           long      cellID) {
    }


  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        dataPanel = new javax.swing.JPanel();

        setBackground(java.awt.SystemColor.window);
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setLayout(new java.awt.BorderLayout());

        topPanel.setBackground(java.awt.SystemColor.window);
        topPanel.setLayout(new java.awt.GridLayout(1, 3));
        add(topPanel, java.awt.BorderLayout.NORTH);

        dataPanel.setBackground(java.awt.SystemColor.window);
        dataPanel.setLayout(new java.awt.BorderLayout(1, 1));
        add(dataPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dataPanel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

}
