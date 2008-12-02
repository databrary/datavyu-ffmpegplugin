/*
 * Spreadsheet.java
 *
 * Created on 26/11/2008, 2:14:43 PM
 */

package au.com.nicta.openshapa.disc.spreadsheet;

import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.SystemErrorException;
import java.awt.BorderLayout;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;

/**
 *
 * @author swhitcher
 */
public class Spreadsheet extends javax.swing.JFrame {

    /** the mainview. */
    private SpreadsheetView mainview;
    /** the columnheader. */
    private SpreadsheetColumnHeader rowView;

    /** The Database being viewed. */
    private Database  database;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(Spreadsheet.class);

    /** Creates new form Spreadsheet. */
    public Spreadsheet() {
        initComponents();

        this.setLayout(new BorderLayout());

        mainview = new SpreadsheetView();
        mainview.setLayout(new BoxLayout(mainview, BoxLayout.X_AXIS));
        //mainview.setPreferredSize(new Dimension(400,300));
        rowView = new SpreadsheetColumnHeader();

        JScrollPane jScrollPane3 = new JScrollPane();
        this.add(jScrollPane3, BorderLayout.CENTER);
        jScrollPane3.setViewportView(mainview);
        jScrollPane3.setColumnHeaderView(rowView);
    }

    /**
     * Creates new form Spreadsheet.
     * @param db The databse to display
     */
    public Spreadsheet(final Database db) {
        this();

//        fakeDB(db);

        this.setDatabase(db);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

//        db.registerCascadeListener(this);
//        db.registerColumnListListener(this);

        this.updateComponents();
//        this.setSize(new Dimension(50,400));
    }

    /*
    private void fakeDB(final Database adb) {

        try {
            MatrixVocabElement mve = new MatrixVocabElement(adb);

            DataColumn column = new DataColumn(adb, "TestColumn",
                                        MatrixVocabElement.matrixType.TEXT);

            adb.addColumn(column);
            column = adb.getDataColumn("TestColumn");
            mve = adb.getMatrixVE(column.getItsMveID());

            DataCell[] cells = new DataCell[200];
            for (int i=0; i<cells.length; i++) {
                cells[i] = new DataCell(adb, column.getID(), mve.getID());
                long cid = adb.appendCell(cells[i]);
                cells[i] = (DataCell)adb.getCell(cid);
            }

          for (int i=0; i<cells.length; i++) {
            Matrix m = new Matrix(adb, mve.getID());
            // TextString
            TextStringDataValue dv = new TextStringDataValue(adb);
            dv.setItsValue("Testing. This is some data. " + i +
                             " ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                             "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            m.replaceArg(0, dv);
            //cells[i].setVal(m);
            DataCell dc = new DataCell(adb, column.getID(), mve.getID());
            dc.setID(cells[i].getID());
            dc.setVal(m);
            dc.setOnset(new TimeStamp(60, i*60));
            dc.setOffset(new TimeStamp(60, i*60 + 59));
            adb.replaceCell(dc);
          }

            column = new DataColumn(adb, "TestColumn2",
                                        MatrixVocabElement.matrixType.INTEGER);

            adb.addColumn(column);
            column = adb.getDataColumn("TestColumn2");
            mve = adb.getMatrixVE(column.getItsMveID());

            for (int i=0; i<5; i++) {
                cells[i] = new DataCell(adb, column.getID(), mve.getID());
                long cid = adb.appendCell(cells[i]);
                cells[i] = (DataCell)adb.getCell(cid);
            }

          for (int i=0; i<5; i++) {
            Matrix m = new Matrix(adb, mve.getID());
            // Integer
            IntDataValue dv = new IntDataValue(adb);
            dv.setItsValue(i);
            m.replaceArg(0, dv);
            //cells[i].setVal(m);
            DataCell dc = new DataCell(adb, column.getID(), mve.getID());
            dc.setID(cells[i].getID());
            dc.setVal(m);
            dc.setOnset(new TimeStamp(60, i*60));
            dc.setOffset(new TimeStamp(60, i*60 + 59));
            adb.replaceCell(dc);
          }

        } catch (Exception e) {}
    }
*/

    /**
     * Populate from the database.
     */
    private void updateComponents() {
        try {
            Vector < DataColumn > dbColumns = getDatabase().getDataColumns();

            for (int i = 0; i < dbColumns.size(); i++) {
                DataColumn dbColumn = dbColumns.elementAt(i);

                SpreadsheetColumn col = new SpreadsheetColumn(dbColumn);

                addColumn(col, dbColumn.getName());

            }
        } catch (SystemErrorException e) {
           logger.error("Failed to populate Spreadsheet.", e);
        }

    }

    /**
     * Add a column panel to the scroll panel.
     * @param col Column to add
     * @param name Name of column
     */
    private void addColumn(final SpreadsheetColumn col, final String name) {
        mainview.add(col);

        rowView.addColumn(name);
    }

    /**
     * Set Database.
     * @param db Database to set
     */
    public final void setDatabase(final Database db) {
        this.database = db;
    }

    /**
     * @return Database this spreadsheet displays
     */
    public final Database getDatabase() {
        return (this.database);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 587, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 402, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
