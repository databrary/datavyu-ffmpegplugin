/*
 * SpreadsheetColumn.java
 *
 * Created on Feb 2, 2007, 1:13 PM
 */

package au.com.nicta.openshapa.disc.spreadsheet;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import au.com.nicta.openshapa.db.*;
import au.com.nicta.openshapa.disc.editors.*;

/**
 *
 * @author  FGA
 */
public class SpreadsheetColumn extends JPanel
{
  protected Spreadsheet spreadsheet;
  protected Column column;

  protected Vector<SpreadsheetCell> ordCells = // Cells sorted by oridinal
      new Vector<SpreadsheetCell>();

  protected Vector<SpreadsheetCell> timeCells = // Cells sorted by onset
      new Vector<SpreadsheetCell>();

  protected boolean displayOrdinal = true;
  protected boolean displayOnset   = true;
  protected boolean displayOffset  = true;
  protected boolean displayData    = true;

  protected boolean columnChanged = false;

  protected long lastMinTimeStamp = Long.MAX_VALUE;
  protected long lastMaxTimeStamp = Long.MIN_VALUE;

  boolean temporalOrdering = false;

  final static int DEFAULT_HEIGHT = 50;
  final static int DEFAULT_WIDTH = 100;
  final static int HEADER_HEIGHT = 20;

  boolean changedMin = false;

  int    cachedMinType = SpreadsheetCell.MILLISECONDS;
  double cachedMinValue = -1;
  long   cachedMinSpan = -1;

  long offset = 0;

  long maxTime = Long.MIN_VALUE;
  long minTime = Long.MAX_VALUE;

  /** Creates new form SpreadsheetColumn */
  public SpreadsheetColumn(Spreadsheet spreadsheet, Column col)
  {
    this.column = col;
    initComponents();
    this.spreadsheet = spreadsheet;
  }
  
  public Column getColumn()
  {
    return (this.column);
  }

  public Spreadsheet getSpreadsheet()
  {
    return (this.spreadsheet);
  }

  public void addCell(SpreadsheetCell cell)
    throws au.com.nicta.openshapa.db.SystemErrorException
  {
    this.ordCells.add(cell);
    this.addCell(cell,0,this.timeCells.size());
    this.add(cell);
    this.updateColumn();
    this.spreadsheet.updateSpreadsheet();
  }

  private void addCell(SpreadsheetCell newCell, int start, int end)
    throws au.com.nicta.openshapa.db.SystemErrorException
  {
    if (start >= end) {
      this.changedMin = true;
      int tSize = this.timeCells.size();
      if (start >= tSize) {
        this.timeCells.add(newCell);
        this.add(newCell);
        this.offset = ((SpreadsheetCell)this.timeCells.
                       elementAt(0)).getOnset().getItsValue().getTime();
        return;
      }
      SpreadsheetCell t = (SpreadsheetCell)this.timeCells.elementAt(start);
      if (newCell.getOnset().getItsValue().getTime() <
          t.getOnset().getItsValue().getTime()) {
          this.timeCells.add(start, newCell);
      } else {
        this.timeCells.add(start+1, newCell);
      }
      this.offset =
          this.timeCells.elementAt(0).getOnset().getItsValue().getTime();
    } else {
      int mid = start + (end-start)/2;
      SpreadsheetCell t = (SpreadsheetCell)this.timeCells.elementAt(mid);
      if (newCell.getOnset().getItsValue().getTime() <
          t.getOnset().getItsValue().getTime()) {
        this.addCell(newCell, start, mid-1);
      } else {
        this.addCell(newCell, mid+1, end);
      }
    }
  }
  
  public void removeCell(SpreadsheetCell cell)
    throws SystemErrorException
  {
    this.ordCells.remove(cell);
    this.timeCells.remove(cell);
    if (this.timeCells.size() > 0) {
      this.offset = this.timeCells.elementAt(this.timeCells.size()).getOffset().getItsValue().getTime();
    }
    this.updateColumn();
    this.spreadsheet.updateSpreadsheet();
  }


  public int getTemporalHeight()
    throws au.com.nicta.openshapa.db.SystemErrorException
  {
    long minSpan = this.getMinSpan();
    int minSpanType = this.getMinSpanType();
    double minSpanValue = this.getMinSpanValue();

    long diff = (this.maxTime - this.offset);
    int secs = (int) (diff / 1000.0);
    int days = (int) (diff / 1000.0 / (60 * 60 * 24));
    int hours = (int) (diff / 1000.0 / (60 * 60));
    int mins = (int) (diff / 1000.0 / (60));
    switch (minSpanType) {
    case SpreadsheetCell.DAYS: {
        return (days);
    }
    case SpreadsheetCell.HOURS: {
        return (hours);
    }
    case SpreadsheetCell.MINUTES: {
        return (mins);
    }
    case SpreadsheetCell.SECONDS: {
        return (secs);
    }
    case SpreadsheetCell.MILLISECONDS: {
        return ((int) diff);
    }
    }

    return ( -1);
  }


  public int getMinSpanType()
    throws au.com.nicta.openshapa.db.SystemErrorException
  {
    if (!changedMin && (cachedMinValue != -1)) {
        return (this.cachedMinType);
    }
    double minVal = this.getMinSpan() / 1000.0;
    int days = (int) (minVal / (60 * 60 * 24));
    int hours = (int) (minVal / (60 * 60));
    int mins = (int) (minVal / (60));
    int secs = (int) minVal;

    if (days > 0) {
        this.cachedMinType = SpreadsheetCell.DAYS;
    } else if (hours > 0) {
        this.cachedMinType = SpreadsheetCell.HOURS;
    } else if (mins > 0) {
        this.cachedMinType = SpreadsheetCell.MINUTES;
    } else if (secs > 0) {
        this.cachedMinType = SpreadsheetCell.SECONDS;
    } else {
        this.cachedMinType = SpreadsheetCell.MILLISECONDS;
    }

    this.changedMin = false;
    return (this.cachedMinType);
  }

  public long getMinSpan()
    throws au.com.nicta.openshapa.db.SystemErrorException
  {
    if (!changedMin && (cachedMinSpan != -1)) {
        return (this.cachedMinSpan);
    }
    long minVal = Long.MAX_VALUE;
    SpreadsheetCell prevTEP = null;
    for (int i = 0; i < this.timeCells.size(); i++) {
        SpreadsheetCell t = this.timeCells.elementAt(i);
        long l = Math.abs(t.getOffset().getItsValue().getTime() -
                          t.getOnset().getItsValue().getTime());
        if ((l < minVal) && (l>0)) {
            minVal = l;
        }
        if (prevTEP != null) {
            l = Math.abs(t.getOnset().getItsValue().getTime() -
                         prevTEP.getOffset().getItsValue().getTime());
        }
        prevTEP = t;

        if (t.getOnset().getItsValue().getTime() < this.minTime) {
            this.minTime = t.getOnset().getItsValue().getTime();
        }
        if (t.getOffset().getItsValue().getTime() < this.minTime) {
            this.minTime = t.getOffset().getItsValue().getTime();
        }
        if (t.getOnset().getItsValue().getTime() > this.maxTime) {
            this.maxTime = t.getOnset().getItsValue().getTime();
        }
        if (t.getOffset().getItsValue().getTime() > this.maxTime) {
            this.maxTime = t.getOffset().getItsValue().getTime();
        }
    }

    this.cachedMinSpan = minVal;
    this.changedMin = false;
    return (minVal);
  }

  public double getMinSpanValue()
    throws au.com.nicta.openshapa.db.SystemErrorException
  {
    if (!changedMin && (cachedMinValue != -1)) {
        return (this.cachedMinValue);
    }
    double mills = this.getMinSpan();
    double days = (mills / (1000 * 60 * 60 * 24));
    double hours = (mills / (1000 * 60 * 60));
    double mins = (mills / (1000 * 60));
    double secs = mills / 1000;
    switch (this.getMinSpanType()) {
    case SpreadsheetCell.DAYS: {
        this.cachedMinValue = days;
        break;
    }
    case SpreadsheetCell.HOURS: {
        this.cachedMinValue = hours;
        break;
    }
    case SpreadsheetCell.MINUTES: {
        this.cachedMinValue = mins;
        break;
    }
    case SpreadsheetCell.SECONDS: {
        this.cachedMinValue = secs;
        break;
    }
    default: {
        this.cachedMinValue = mills;
        break;
    }
    }

    this.changedMin = false;
    return (this.cachedMinValue);
  }

  public void updateComponents()
    throws au.com.nicta.openshapa.db.SystemErrorException
  {
    int nc = this.getComponentCount();
    int overallHeight = 0;

    if (temporalOrdering) {
        int minSpanType = this.getMinSpanType();
        double minSpanValue = this.getMinSpanValue();
        long minSpan = this.getMinSpan();
        for (int i = 0; i < nc; i++) {
            SpreadsheetCell tep = (SpreadsheetCell)this.getComponent(i);
            tep.setDivision(minSpanType, minSpanValue);
            double y =
                (tep.getTime(minSpanType,
                             tep.getOnset().getItsValue().getTime() -
                             this.offset) *
                 tep.HEIGHT_MULT / minSpanValue);

            tep.setLocation(0, (int) Math.round(y));
            tep.setWidth(this.DEFAULT_WIDTH);
            //tep.repaint();
        }
        overallHeight = (int) Math.round((this.getTemporalHeight() *
                                          SpreadsheetCell.HEIGHT_MULT) /
                                          minSpanValue) + 2*HEADER_HEIGHT;
        Dimension d = new Dimension(DEFAULT_WIDTH, overallHeight);
        this.setSize(d);
        this.setMinimumSize(d);
        this.setMaximumSize(d);
        this.setPreferredSize(d);
        //this.getParent().validate();
        this.setBackground(Color.lightGray);
    } else {
        for (int i = 0; i < nc; i++) {
            SpreadsheetCell tep = (SpreadsheetCell)this.
                                       getComponent(i);
            int y = (i * DEFAULT_HEIGHT);
            tep.setLocation(0, y);
            tep.setHeight(DEFAULT_HEIGHT);
            //tep.repaint();
            overallHeight += tep.getHeight();
        }
        Dimension d = new Dimension(DEFAULT_WIDTH, overallHeight);
        this.setSize(d);
        this.setMinimumSize(d);
        this.setMaximumSize(d);
        this.setPreferredSize(d);
        //this.getParent().validate();
        this.setBackground(Color.lightGray);
    }
  }


  public boolean showOrdinal()
  {
    return (this.displayOrdinal);
  }

  public boolean showOnset()
  {
    return (this.displayOnset);
  }

  public boolean showOffset()
  {
    return (this.displayOffset);
  }

  public boolean showData()
  {
    return (this.displayData);
  }

  public void setOrdinalVisible(boolean value)
  {
    this.displayOrdinal = value;
  }

  public void setOnsetVisible(boolean value)
  {
    this.displayOnset = value;
  }

  public void setOffsetVisible(boolean value)
  {
    this.displayOffset = value;
  }

  public void setDataVisible(boolean value)
  {
    this.displayData = value;
  }

  public long getMinOnset()
    throws SystemErrorException
  {
    if (!this.columnChanged) {
      return (this.lastMinTimeStamp);
    }
    
    if (this.timeCells.size() <= 0) {
      return (-1);
    }

    return (this.timeCells.elementAt(0).getOnset().getItsValue().getTime());
  }

  public long getMaxOnset()
    throws SystemErrorException
  {
    if (!this.columnChanged) {
      return (this.lastMaxTimeStamp);
    }

    if (this.timeCells.size() <= 0) {
      return (-1);
    }

    return (this.timeCells.elementAt(this.timeCells.size()).getOnset().getItsValue().getTime());
  }

  public void updateColumn()
    throws SystemErrorException
  {
    this.columnChanged = true;
    this.getMinOnset();
    this.getMaxOnset();
    this.columnChanged = false;
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents()
  {

    setLayout(null);

    setBackground(java.awt.SystemColor.window);
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables
  
}
