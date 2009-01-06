package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.ExternalColumnListListener;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.views.OpenSHAPADialog;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;

/**
 * The main spreadsheet window. Displays the database it refers
 * to, showing the database columns and cells within.
 * @author swhitcher
 */
public class Spreadsheet extends OpenSHAPADialog
        implements ExternalColumnListListener, MouseListener {

    /** Scrollable view inserted into the JScrollPane. */
    private SpreadsheetView mainView;
    /** View showing the Column titles. */
    private JPanel headerView;

    /** The Database being viewed. */
    private Database database;

    /** Mapping between database column id to the Spreadsheetcolumn. */
    private HashMap<Long, SpreadsheetColumn> columns;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(Spreadsheet.class);

    /**
     * Creates new, empty Spreadsheet. No database referred to as yet
     *
     * @param parent The parent frame for this dialog.
     * @param modal Is this dialog to be modal (true), or not.
     */
    public Spreadsheet(final java.awt.Frame parent,
                       final boolean modal) {
        super(parent, modal);
        initComponents();

        setName(this.getClass().getSimpleName());

        this.setLayout(new BorderLayout());

        mainView = new SpreadsheetView();
        mainView.setLayout(new BoxLayout(mainView, BoxLayout.X_AXIS));

        headerView = new JPanel();
        headerView.setLayout(new BoxLayout(headerView, BoxLayout.X_AXIS));

        columns = new HashMap<Long, SpreadsheetColumn>();

        JScrollPane jScrollPane3 = new JScrollPane();
        this.add(jScrollPane3, BorderLayout.CENTER);
        jScrollPane3.setViewportView(mainView);
        jScrollPane3.setColumnHeaderView(headerView);
    }

    /**
     * Creates new Spreadsheet.
     *
     * @param parent The parent frame for this dialog.
     * @param modal Is this dialog to be modal (true), or not.
     * @param db The database to display.
     */
    public Spreadsheet(final java.awt.Frame parent,
                       final boolean modal,
                       final Database db) {
        this(parent, modal);

        this.setDatabase(db);

        this.updateComponents();
    }

    /**
     * Populate from the database.
     */
    private void updateComponents() {
        clearAll();
        try {
            Vector <DataColumn> dbColumns = getDatabase().getDataColumns();

            for (int i = 0; i < dbColumns.size(); i++) {
                DataColumn dbColumn = dbColumns.elementAt(i);

                addColumn(getDatabase(), dbColumn.getID());
            }
        } catch (SystemErrorException e) {
           logger.error("Failed to populate Spreadsheet.", e);
        }
    }

    /**
     * Clear all previous panels and references.
     */
    private void clearAll() {
        columns.clear();
        mainView.removeAll();
        headerView.removeAll();
    }

    /**
     * Add a column panel to the scroll panel.
     * @param db database.
     * @param colID ID of the column to add.
     */
    private void addColumn(final Database db, final long colID) {
        // make the SpreadsheetColumn
        SpreadsheetColumn col = new SpreadsheetColumn(db, colID);
        // add the datapanel to the scrollpane viewport
        mainView.add(col.getDataPanel());
        // add the headerpanel to the scrollpane headerviewport
        headerView.add(col.getHeaderPanel());

        col.getHeaderPanel().addMouseListener(this);

        // and add it to our maintained ref collection
        columns.put(colID, col);
    }

    /**
     * Remove a column panel from the scroll panel viewport.
     * @param colID ID of column to remove
     */
    private void removeColumn(final long colID) {

        SpreadsheetColumn foundcol = columns.get(colID);
        if (foundcol != null) {
            mainView.remove(foundcol.getDataPanel());
            headerView.remove(foundcol.getHeaderPanel());
            columns.remove(colID);
        } else {
            logger.warn("Did not find column to delete by id = " + colID);
        }
    }

    /**
     * Set Database.
     * @param db Database to set
     */
    public final void setDatabase(final Database db) {
        // check if we need to deregister
        if ((database != null) && (database != db)) {
            try {
                database.deregisterColumnListListener(this);
            } catch (SystemErrorException e) {
                logger.warn("deregisterColumnListListener failed", e);
            }
        }

        // set the database
        this.database = db;

        // register as a columnListListener
        try {
            db.registerColumnListListener(this);
        } catch (SystemErrorException e) {
            logger.error("registerColumnListListener failed", e);
        }

        // setName to remember screen locations
        setName(this.getClass().getSimpleName() + db.getName());

        // show database name in title bar
        setTitle(db.getName());
    }

    /**
     * @return Database this spreadsheet displays
     */
    public final Database getDatabase() {
        return (this.database);
    }

    /**
     * ExternalColumnListListener overrides
     */

    /**
     * Action to invoke when a column is removed from a database.
     *
     * @param db The database that the column has been removed from.
     * @param colID The id of the freshly removed column.
     */
    public final void colDeletion(final Database db, final long colID) {
        removeColumn(colID);
        validate();
    }

    /**
     * Action to invoke when a column is added to a database.
     *
     * @param db The database that the column has been added to.
     * @param colID The id of the newly added column.
     */
    public final void colInsertion(final Database db, final long colID) {
        addColumn(db, colID);
        validate();
    }

    /**
     * Invoked when the mouse enters a component. No function.
     * @param me event detail
     */
    public void mouseEntered(final MouseEvent me) {
    }

    /**
     * Invoked when the mouse exits a component. No function.
     * @param me event detail
     */
    public void mouseExited(final MouseEvent me) {
    }

    /**
     * Invoked when the mouse is pressed in a component. No function.
     * @param me event detail
     */
    public void mousePressed(final MouseEvent me) {
    }

    /**
     * Invoked when the mouse is released in a component. No function.
     * @param me event detail
     */
    public void mouseReleased(final MouseEvent me) {
    }

    /**
     * Invoked when the mouse is clicked in a cell.
     * Toggles the selection state of the cell and notifies the database cell
     * referred to.
     * @param me event detail
     */
    public void mouseClicked(final MouseEvent me) {
        // A column header has been clicked
        ColumnHeaderPanel clickedcol = (ColumnHeaderPanel) me.getComponent();

        Iterator<SpreadsheetColumn> it = columns.values().iterator();
        // deselect the others and toggle the clicked one
        while (it.hasNext()) {
            ColumnHeaderPanel col =
                                (ColumnHeaderPanel) it.next().getHeaderPanel();
            if (col == clickedcol) {
                col.toggleSelected();
                col.repaint();
            } else {
                if (col.isSelected()) {
                    col.toggleSelected();
                    col.repaint();
                }
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 587, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 402, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
