package au.com.nicta.openshapa.disc.spreadsheet;

import au.com.nicta.openshapa.Executive;
import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.SystemErrorException;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Vector;
import javax.swing.JFrame;


/**
 * Spreadsheet Viewer main component.
 *
 * @author  FGA
 */
public class Spreadsheet extends JFrame {
  
    /** Linear View orientation. */
    public static final int LINEAR_VIEW        = 1;
    /** Semi-temporal View orientation. */
    public static final int SEMI_TEMPORAL_VIEW = 2;
    /** Temporal View orientation. */
    public static final int TEMPORAL_VIEW      = 3;

    /** The Executive linked with the spreadsheet. */
    private Executive executive;
    /** The Database being viewed. */
    private Database  database;

    /** Columns of the spreadsheet. */
    private Vector < SpreadsheetColumn > columns;

    /** Dirty flag for spreadsheet. */
    private boolean spreadsheetChanged = false;

    /** Last minimum timestamp. */
    private long lastMinTimeStamp = Long.MAX_VALUE;
    /** Last maximum timestamp. */
    private long lastMaxTimeStamp = Long.MIN_VALUE;

    /** Current spreadsheet view orientation. */
    private int spreadsheetView = LINEAR_VIEW;

    /** Creates new form Spreadsheet. */
    public Spreadsheet() {

    initComponents();
  }

    /**
     * Creates new form Spreadsheet.
     *
     * @param exec Executive linked to the spreadsheet.
     * @param db Database this spreadsheet displays.
     *
     * @throws SystemErrorException if the db does not create
     */
  public Spreadsheet(Executive exec, Database db)
                                                throws SystemErrorException {
    this();
    this.setExecutive(exec);
    this.setDatabase(db);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new FlowLayout());

        this.Populate();
        this.setSize(new Dimension(50,400));
  }

    public void Populate() {
        // Populate table with a variable listing from the database
        try {
            Vector<DataColumn> dbColumns = getDatabase().getDataColumns();

            for (int i = 0; i < dbColumns.size(); i++) {
                DataColumn dbColumn = dbColumns.elementAt(i);

                SpreadsheetColumn col = new SpreadsheetColumn(this, dbColumn);
                col.setOrdinalVisible(true);
                col.setOnsetVisible(true);
                col.setOffsetVisible(true);
                col.setDataVisible(true);

                for(int j=0; j < dbColumn.getNumCells(); j++) {
                    DataCell dc = (DataCell)getDatabase()
                                            .getCell(dbColumn.getID(), j);

                    SpreadsheetCell sc = new SpreadsheetCell(col, dc);
                    sc.setWidth(30);
                    sc.setSize(200,50);
                    this.getContentPane().add(sc);
                }
            }
        } catch (SystemErrorException e) {
           // TODO: bug #18 Log the nature of the error to log4j.
        }

    }

    public void setExecutive(Executive exec) {
    this.executive = exec;
  }

    public void setDatabase(Database db) {
    this.database = db;
  }

    public Executive getExecutive() {
    return (this.executive);
  }

    public Database getDatabase() {
    return (this.database);
  }

    public void addColumn(SpreadsheetColumn col) throws SystemErrorException {
    this.columns.addElement(col);
    this.add(col);
    this.updateSpreadsheet();
  }
  
  public void removeColumn(SpreadsheetColumn col)
                                                throws SystemErrorException {
    this.columns.removeElement(col);
    this.remove(col);
    this.updateSpreadsheet();
  }

    public long getScale() {
    return (1);
  }

    public long getMinTimeStamp() throws SystemErrorException {
    if (!spreadsheetChanged) {
      return (lastMinTimeStamp);
    }

    long min = Long.MAX_VALUE;
    long lm;

    for (int i=0; i<this.columns.size(); i++) {
      lm = columns.elementAt(i).getMinOnset();
      if (lm < min) {
        min = lm;
      }
    }

    this.lastMinTimeStamp = min;
    return (min);
  }

    public long getMaxTimeStamp() throws SystemErrorException {
    if (!spreadsheetChanged) {
      return (lastMaxTimeStamp);
    }

    long max = Long.MIN_VALUE;
    long lm;

    for (int i=0; i<this.columns.size(); i++) {
      lm = columns.elementAt(i).getMinOnset();
      if (lm > max) {
        max = lm;
      }
    }

    this.lastMaxTimeStamp = max;
    return (max);
  }

    public int getColumnHeight() throws SystemErrorException {
    long diff = (this.getMaxTimeStamp()-this.getMinTimeStamp())/this.getScale();
    if (diff >= Integer.MAX_VALUE) {
      return (Integer.MAX_VALUE-1);
    }

    return ((int)diff);
  }

    public void updateSpreadsheet() throws SystemErrorException {
    this.spreadsheetChanged = true;
    this.getMinTimeStamp();
    this.getMaxTimeStamp();
    this.spreadsheetChanged = false;
        Populate();
    this.repaint();
  }

    public int getSpreadsheetView() {
    return (this.spreadsheetView);
  }

    public void setSpreadsheetView(int view) {
        if ((view >= 1) && (view <= 3)) {
      this.spreadsheetView = view;
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents()
  {

    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(0, 400, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(0, 300, Short.MAX_VALUE)
    );
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables
  
}
