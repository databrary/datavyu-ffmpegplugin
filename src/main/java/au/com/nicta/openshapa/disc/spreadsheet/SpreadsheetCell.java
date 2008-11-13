/*
 * SpreadsheetCell.java
 *
 * Created on Feb 2, 2007, 1:14 PM
 */

package au.com.nicta.openshapa.disc.spreadsheet;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;
import java.util.*;
import au.com.nicta.openshapa.db.*;
import au.com.nicta.openshapa.disc.*;
import au.com.nicta.openshapa.disc.editors.*;
import au.com.nicta.openshapa.util.*;

/**
 *
 * @author  felix
 */
public class SpreadsheetCell
    extends     javax.swing.JPanel
    implements  MouseListener, ExternalDataCellListener
{
  protected DataViewLabel ord;
  protected DataViewLabel onset;
  protected DataViewLabel offset;
  protected DataViewLabel value;;

  protected SpreadsheetColumn column;
  protected long              cellID;

  protected boolean selected = false;
  protected static UIConfiguration uiconfig = new UIConfiguration();

  protected Dimension userDimensions = new Dimension(0,0);

  
  
  public final static int MILLISECONDS  = 1; //Calendar.MILLISECOND;
  public final static int SECONDS       = 2; //Calendar.SECOND;
  public final static int MINUTES       = 3; //Calendar.MINUTE;
  public final static int HOURS         = 4; //Calendar.HOUR_OF_DAY;
  public final static int DAYS          = 5; //Calendar.DAY_OF_YEAR;

  public final static int HEIGHT_MULT   = 50;

  public final static String DATEFORMAT = "MM/dd/yyyy HH:mm:ss:SSS";
  public final static SimpleDateFormat DATEFORMATER =
                                               new SimpleDateFormat(DATEFORMAT);

  int     width     = SpreadsheetColumn.DEFAULT_WIDTH;
  int     height    = SpreadsheetColumn.DEFAULT_HEIGHT;
  int     divType   = SECONDS;
  double  divValue  = 1;

  boolean temporalSizeChanged = false;
  
  
  /** Creates new form SpreadsheetCell */
  public SpreadsheetCell(SpreadsheetColumn column, Cell cell)
    throws SystemErrorException
  {
    if (column == null) {
      throw (new NullPointerException("Column can not be NULL!"));
    }

    if (cell == null) {
      throw (new NullPointerException("Cell can not be NULL!"));
    }
    
    IntDataValue ordDV =
        new IntDataValue(column.getSpreadsheet().getDatabase());
    TimeStampDataValue onsetDV =
        new TimeStampDataValue(column.getSpreadsheet().getDatabase());
    TimeStampDataValue offsetDV =
        new TimeStampDataValue(column.getSpreadsheet().getDatabase());
    
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
    this.column = column;

    this.addMouseListener(this);
    this.topPanel.addMouseListener(this);
    this.dataPanel.addMouseListener(this);

    this.updateDimensions();

    DataCell dc = null;
    if (cell instanceof DataCell) {
      dc = (DataCell)cell;
    } else {
      dc = (DataCell)this.column.getSpreadsheet().getDatabase().getCell(((ReferenceCell)cell).getTargetID());
    }
    this.column.getSpreadsheet().getDatabase().registerDataCellListener(dc.getID(), this);
    this.setOrdinal(dc.getOrd());
    this.setOnset(dc.getOnset());
    this.setOffset(dc.getOffset());
  }

  private void setOrdinal(int ord)
  {
    ((IntDataValue)this.ord.getValue()).setItsValue(ord);
    this.ord.updateStrings();
    this.repaint();
  }

  private void setOnset(TimeStamp newonset)
    throws SystemErrorException
  {
    ((TimeStampDataValue)this.onset.getValue()).setItsValue(newonset);
    this.onset.updateStrings();
    this.repaint();
  }
  
  private void setOffset(TimeStamp newoffset)
    throws SystemErrorException
  {
    ((TimeStampDataValue)this.offset.getValue()).setItsValue(newoffset);
    this.offset.updateStrings();
    this.repaint();
  }

  public final static double getTime(int type, long time)
  {
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

  public void setDivision(int type, double value)
      throws au.com.nicta.openshapa.db.SystemErrorException
  {
    this.divType = type;
    this.divValue = value;

    // Calculate the height given the division
    double diff = getTime(type, this.onset) - getTime(type, this.offset);
    this.setHeight((int)Math.round((diff*this.HEIGHT_MULT)/value));
  }

  public double getDivisionValue()
  {
    return (this.divValue);
  }

  public double getDivisionType()
  {
    return (this.divType);
  }

  public final static double getTime(int type, DataViewLabel timeLabel)
       throws au.com.nicta.openshapa.db.SystemErrorException
  {
    long t = ((TimeStampDataValue)timeLabel.getValue()).getItsValue().getTime();
    return (getTime(type, t));
  }

  public final static double getTime(int type, Date time)
  {
    return (getTime(type, time.getTime()));
  }

  public double getOnsetTime() throws au.com.nicta.openshapa.db.SystemErrorException
  {
    return (getTime(this.divType,
         ((TimeStampDataValue)this.onset.getValue()).getItsValue().getTime()));
  }

  public double getOffsetTime() throws au.com.nicta.openshapa.db.SystemErrorException
  {
    return (getTime(this.divType,
         ((TimeStampDataValue)this.offset.getValue()).getItsValue().getTime()));
  }

  private void setValue(DataValue dv)
  {
    this.value.setValue(dv);
  }

  public IntDataValue getOrdinal()
  {
    return ((IntDataValue)this.ord.getValue());
  }

  public TimeStampDataValue getOnset()
    throws SystemErrorException
  {
    return ((TimeStampDataValue)this.onset.getValue());
  }
  
  public TimeStampDataValue getOffset()
    throws SystemErrorException
  {
    return ((TimeStampDataValue)this.offset.getValue());
  }

  public DataValue getValue()
  {
    return (this.value.getValue());
  }

  public void setSize(int width, int height)
  {
    super.setSize(width, height);
    this.userDimensions = new Dimension(width, height);
  }

  public void setWidth(int width)
  {
    this.setSize(width, this.getHeight());
  }

  public void setHeight(int height)
  {
    this.setSize(this.getWidth(), height);
  }
  
  public int getMinimumHeight()
  {
    FontMetrics fm = this.getFontMetrics(uiconfig.spreadsheetTimeStampFont);
    FontMetrics fm1 = this.getFontMetrics(uiconfig.spreadsheetDataFont);
    return(fm.getHeight() + fm1.getHeight());
  }
  
  public Dimension getPreferredSize()
  {
    if ((this.userDimensions.width > 0) &&
        (this.userDimensions.height > 0)) {
      return (this.userDimensions);
    }
    
    return (super.getPreferredSize());
  }

  public Dimension getMinimumSize()
  {
    if ((this.userDimensions.width > 0) &&
        (this.userDimensions.height > 0)) {
      return (this.userDimensions);
    }
    
    return (super.getMinimumSize());
  }
  
  public Dimension getMaximumSize()
  {
    if ((this.userDimensions.width > 0) &&
        (this.userDimensions.height > 0)) {
      return (this.userDimensions);
    }
    
    return (super.getMaximumSize());
  }
  
  public void updateDimensions()
  {
    Rectangle r = this.getBounds();
    FontMetrics fm = this.getFontMetrics(uiconfig.spreadsheetTimeStampFont);

    int totalWidth = 4;
    int totalHeight = 4;

    totalWidth += this.ord.getMinimumSize().width;
    totalWidth += this.onset.getMinimumSize().width;
    totalWidth += this.offset.getMinimumSize().width;

    if (this.column.showOrdinal() ||
        this.column.showOnset() ||
        this.column.showOffset()) {
      totalHeight += fm.getHeight();
    }
    if (this.column.showOrdinal()) {
      this.ord.setVisible(true);
    } else {
      this.ord.setVisible(false);
    }
    if (this.column.showOnset()) {
      this.onset.setVisible(true);
    } else {
      this.onset.setVisible(false);
    }
    if (this.column.showOffset()) {
      this.offset.setVisible(true);
    } else {
      this.offset.setVisible(false);
    }
    
    if ((this.userDimensions.width > 0) &&
        (this.userDimensions.height > 0)) {
      totalWidth = this.userDimensions.width;
      totalHeight = this.userDimensions.height;
    }

    this.value.setWrapWidth(totalWidth);
    Dimension d = this.value.getMaximumSize();
    if (this.column.showData()) {
      totalHeight += d.getHeight();
      this.value.setVisible(true);
    } else {
      this.value.setVisible(false);
    }
  }

  public void paintComponent(Graphics g)
  {
    this.updateDimensions();
    if (this.selected) {
      this.topPanel.setBackground(uiconfig.spreadsheetSelectedColor);
      this.dataPanel.setBackground(uiconfig.spreadsheetSelectedColor);
    } else {
      this.topPanel.setBackground(uiconfig.spreadsheetBackgroundColor);
      this.dataPanel.setBackground(uiconfig.spreadsheetBackgroundColor);
    }
    super.paintComponent(g);
  }

  public void mouseEntered(MouseEvent me)
  {
  }

  public void mouseExited(MouseEvent me)
  {
  }

  public void mousePressed(MouseEvent me)
  {
  }

  public void mouseReleased(MouseEvent me)
  {
  }

  public void mouseClicked(MouseEvent me)
  {
    try {
      this.selected = !this.selected;
      Cell cell = this.column.getSpreadsheet().getDatabase().getCell(this.cellID);
      DataCell dc = null;
      if (cell instanceof DataCell) {
        dc = (DataCell)cell.getDB().getCell(cell.getID());
      } else {
        dc = (DataCell)this.column.getSpreadsheet().getDatabase().getCell(((ReferenceCell)cell).getTargetID());
      }
      dc.setSelected(this.selected);
      cell.getDB().replaceCell(dc);
    } catch (SystemErrorException see) {
      
    }
    this.repaint();
  }

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
                           String     newComment)
  {
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

      /* getArg is now protected in the database.
      if (valChanged) {
        this.value.setValue(newVal.getArg(0));
      }
       */

      if (selectedChanged) {
        this.selected = newSelected;
      }
    } catch (SystemErrorException see) {
      
    }
    
    this.repaint();
  }


  public void DCellDeleted(Database  db,
                           long      colID,
                           long      cellID)
  {
  }

  /*
  public final static void main(String[] args)
  {
    try {
      String userDir = System.getProperty("user.home");
      java.io.File userConfig =
          new java.io.File(userDir, au.com.nicta.openshapa.Executive.USER_CONFIG_FILE);

      // Open configuration
      Configuration config = new Configuration(userConfig);

      JFrame jf = new JFrame();
      //GridLayout gl = new GridLayout(0,1);
      jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
      jf.setLayout(new FlowLayout());//new BorderLayout());
      //JPanel jp = new JPanel();
      //jp.setLayout(gl);
      //JScrollPane jsp = new JScrollPane(jp);

      ODBCDatabase db = new ODBCDatabase();
      Spreadsheet sp = new Spreadsheet(null, db);
      MatrixVocabElement mve = new MatrixVocabElement(db);
      DataColumn column = new DataColumn(db, "TestColumn", MatrixVocabElement.matrixType.TEXT);
      db.addColumn(column);
      column = db.getDataColumn("TestColumn");
      mve = db.getMatrixVE(column.getItsMveID());
      SpreadsheetColumn col = new SpreadsheetColumn(sp, column);
      col.setOrdinalVisible(true);
      col.setOnsetVisible(true);
      col.setOffsetVisible(true);
      col.setDataVisible(true);

      SpreadsheetCell.uiconfig.parseConfiguration(config);
      DataCell[] cells = new DataCell[4];
      for (int i=0; i<cells.length; i++) {
        cells[i] = new DataCell(db, column.getID(), mve.getID());
        long cid = db.appendCell(cells[i]);
        cells[i] = (DataCell)db.getCell(cid);
        SpreadsheetCell sc = new SpreadsheetCell(col, cells[i]);
//        sc.setValue(tsdv);
//        sc.setOrdinal(i);
//        sc.setOnset(new TimeStamp(60, i*60));
//        sc.setOffset(new TimeStamp(60, i*60 + 59));
        sc.setWidth(30);
        sc.setSize(200,50);
        jf.getContentPane().add(sc);
      }

      //jf.getContentPane().add(jsp);//, BorderLayout.CENTER);
      jf.setSize(new Dimension(50,400));
      //jf.pack();

      jf.setVisible(true);
      
      for (int i=0; i<cells.length; i++) {
        Matrix m = new Matrix(db, mve.getID());
        TextStringDataValue tsdv = new TextStringDataValue(sp.getDatabase());
        tsdv.setItsValue("Testing. This is some more data. " +
                         "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                         "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                         "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        m.replaceArg(0, tsdv);
        //cells[i].setVal(m);
        DataCell dc = new DataCell(db, column.getID(), mve.getID());
        dc.setID(cells[i].getID());
        dc.setVal(m);
        dc.setOnset(new TimeStamp(60, i*60));
        dc.setOffset(new TimeStamp(60, i*60 + 59));
        db.replaceCell(dc);
      }
      
      for (int i=0; i<cells.length; i++) {
        DataCell dc = (DataCell)db.getCell(cells[i].getID());
        
        System.out.println(dc);
      }
    } catch (Exception e) {
      System.err.println("An exception occurred: " + e);
      e.printStackTrace();
      System.exit(-1);
    }
  }
   */


  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents()
  {

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
