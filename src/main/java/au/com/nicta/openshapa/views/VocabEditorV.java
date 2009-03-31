package au.com.nicta.openshapa.views;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.LogicErrorException;
import au.com.nicta.openshapa.db.MatrixVocabElement;
import au.com.nicta.openshapa.db.MatrixVocabElement.MatrixType;
import au.com.nicta.openshapa.db.PredicateVocabElement;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.VocabElement;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author cfreeman
 */
public class VocabEditorV extends OpenSHAPADialog {

    /** The table model of the current vocab listing. */
    private DefaultTableModel tableModel;

    /** The database that this vocab editor is manipulating. */
    private Database db;

    /** The logger for OpenSHAPA. */
    private static Logger logger = Logger.getLogger(VocabEditorV.class);

    /** The ID of the column that holds delta information. */
    private static final int DELTA_COLUMN_ID = 0;

    /** The ID of the column that holds type information. */
    private static final int TYPE_COLUMN_ID = 1;

    /** The ID of the column that holds the actual vocab element. */
    private static final int VOCAB_COLUMN_ID = 2;

    /** The number of columns in the table model. */
    private static final int NUM_COLUMNS = 3;

    private static final String DELTA = new String(".");

    private static final String NO_DELTA = new String("");

    private static final String P_TYPE = new String("P");

    private static final String M_TYPE = new String("M");

    private boolean containsChange;

    /** Creates new form VocabEditorV */
    public VocabEditorV(java.awt.Frame parent, boolean modal,
                        final ActionListener listener) {
        super(parent, modal);

        db = OpenSHAPA.getDatabase();

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Delta");
        tableModel.addColumn("Type");
        tableModel.addColumn("vocab");

        containsChange = false;

        initComponents();

        // Populate current vocab list with vocab data from the database.
        try {
             Vector<PredicateVocabElement> predVEs = db.getPredVEs();
            for (PredicateVocabElement pve : predVEs) {
                Object row[] = new Object[NUM_COLUMNS];
                row[DELTA_COLUMN_ID] = NO_DELTA;
                row[TYPE_COLUMN_ID] = P_TYPE;
                row[VOCAB_COLUMN_ID] = pve;
                tableModel.addRow(row);
            }

            Vector<MatrixVocabElement> matVEs = db.getMatrixVEs();
            for (MatrixVocabElement mve : matVEs) {
                Object row[] = new Object[NUM_COLUMNS];
                row[DELTA_COLUMN_ID] = NO_DELTA;
                row[TYPE_COLUMN_ID] = M_TYPE;
                row[VOCAB_COLUMN_ID] = mve;
                tableModel.addRow(row);
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to populate current vocab list", e);
        }
    }

    @Action
    public void addPredicate() {
        Object row[] = new Object[NUM_COLUMNS];
        
        row[DELTA_COLUMN_ID] = DELTA;
        row[TYPE_COLUMN_ID] = P_TYPE;
        try {
            row[VOCAB_COLUMN_ID] = new PredicateVocabElement(db, "predicate");
        } catch (SystemErrorException e) {
            logger.error("Unable to create predicate vocab element", e);
        }

        tableModel.addRow(row);
        containsChange = true;
        this.updateDialogState();
    }

    @Action
    public void addMatrix() {
        Object row[] = new Object[NUM_COLUMNS];

        row[DELTA_COLUMN_ID] = DELTA;
        row[TYPE_COLUMN_ID] = M_TYPE;
        try {
            MatrixVocabElement ve = new MatrixVocabElement(db, "matrix");
            ve.setType(MatrixType.MATRIX);
            row[VOCAB_COLUMN_ID] = ve;
        } catch (SystemErrorException e) {
            logger.error("Unable to create matrix vocab element", e);
        }

        tableModel.addRow(row);
        containsChange = true;
        this.updateDialogState();
    }

    private void updateDialogState() {
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(VocabEditorV.class);

        if (containsChange) {
            closeButton.setText(rMap.getString("closeButton.cancelText"));
            closeButton.setToolTipText(rMap.getString("closeButton.cancelTip"));

            revertButton.setEnabled(true);
            applyButton.setEnabled(true);
            okButton.setEnabled(true);
        } else {
            closeButton.setText(rMap.getString("closeButton.closeText"));
            closeButton.setToolTipText(rMap.getString("closeButton.closeTip"));

            revertButton.setEnabled(false);
            applyButton.setEnabled(false);
            okButton.setEnabled(false);
        }
    }

    @Action
    public void revertChanges() {
        int tableSize = tableModel.getRowCount();
        int curPos = 0;
        for (int i = 0; i < tableSize; i++) {
            if (tableModel.getValueAt(curPos, DELTA_COLUMN_ID).equals(DELTA)) {
                tableModel.removeRow(curPos);
            } else {
                curPos++;
            }
        }

        containsChange = false;
        this.updateDialogState();
    }

    @Action
    public void applyChanges() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, DELTA_COLUMN_ID).equals(DELTA)) {
                // If the row is an element that has changed - we need to push
                // this into the database.
                try {
                    db.addVocabElement((VocabElement) tableModel.getValueAt(i,
                                                              VOCAB_COLUMN_ID));
                    tableModel.setValueAt(NO_DELTA, i, DELTA_COLUMN_ID);
                    containsChange = false;
                    this.updateDialogState();
                } catch (SystemErrorException e) {
                    logger.error("Unable to apply vocab changes", e);
                } catch (LogicErrorException le) {
                    OpenSHAPA.getApplication().showWarningDialog(le);
                }
            }            
        }        
    }

    @Action
    public void ok() {
        applyChanges();
        setVisible(false);
    }

    @Action
    public void closeWindow() {
        setVisible(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        addArgButton = new javax.swing.JButton();
        argTypeComboBox = new javax.swing.JComboBox();
        varyArgCheckBox = new javax.swing.JCheckBox();
        deleteButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        revertButton = new javax.swing.JButton();
        applyButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("au/com/nicta/openshapa/views/resources/VocabEditorV"); // NOI18N
        setTitle(bundle.getString("window.title")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(au.com.nicta.openshapa.OpenSHAPA.class).getContext().getActionMap(VocabEditorV.class, this);
        jButton1.setAction(actionMap.get("addPredicate")); // NOI18N
        jButton1.setText(bundle.getString("addPredicateButton.text")); // NOI18N
        jButton1.setToolTipText(bundle.getString("addPredicateButton.tip")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        getContentPane().add(jButton1, new java.awt.GridBagConstraints());

        jButton2.setAction(actionMap.get("addMatrix")); // NOI18N
        jButton2.setText(bundle.getString("addMatrixButton.text")); // NOI18N
        jButton2.setToolTipText(bundle.getString("addMatrixButton.tip")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        getContentPane().add(jButton2, new java.awt.GridBagConstraints());

        jButton3.setText(bundle.getString("moveArgLeftButton.text")); // NOI18N
        jButton3.setToolTipText(bundle.getString("moveArgLeftButton.tip")); // NOI18N
        jButton3.setEnabled(false);
        jButton3.setName("jButton3"); // NOI18N
        getContentPane().add(jButton3, new java.awt.GridBagConstraints());

        jButton4.setText(bundle.getString("moveArgRightButton.text")); // NOI18N
        jButton4.setToolTipText(bundle.getString("moveArgRightButton.tip")); // NOI18N
        jButton4.setEnabled(false);
        jButton4.setName("jButton4"); // NOI18N
        getContentPane().add(jButton4, new java.awt.GridBagConstraints());

        addArgButton.setText(bundle.getString("addArgButton.text")); // NOI18N
        addArgButton.setToolTipText(bundle.getString("addArgButton.tip")); // NOI18N
        addArgButton.setEnabled(false);
        addArgButton.setName("addArgButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(addArgButton, gridBagConstraints);

        argTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        argTypeComboBox.setToolTipText(bundle.getString("argTypeComboBox.tip")); // NOI18N
        argTypeComboBox.setEnabled(false);
        argTypeComboBox.setName("argTypeComboBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(argTypeComboBox, gridBagConstraints);

        varyArgCheckBox.setText(bundle.getString("varyArgCheckBox.text")); // NOI18N
        varyArgCheckBox.setToolTipText(bundle.getString("varyArgCheckBox.tip")); // NOI18N
        varyArgCheckBox.setEnabled(false);
        varyArgCheckBox.setName("varyArgCheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(varyArgCheckBox, gridBagConstraints);

        deleteButton.setText(bundle.getString("deleteButton.text")); // NOI18N
        deleteButton.setToolTipText(bundle.getString("deleteButton.tip")); // NOI18N
        deleteButton.setEnabled(false);
        deleteButton.setName("deleteButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(deleteButton, gridBagConstraints);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(tableModel);
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jScrollPane1, gridBagConstraints);

        revertButton.setAction(actionMap.get("revertChanges")); // NOI18N
        revertButton.setText(bundle.getString("revertButton.text")); // NOI18N
        revertButton.setToolTipText(bundle.getString("revertButton.tip")); // NOI18N
        revertButton.setName("revertButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(revertButton, gridBagConstraints);

        applyButton.setAction(actionMap.get("applyChanges")); // NOI18N
        applyButton.setText(bundle.getString("applyButton.text")); // NOI18N
        applyButton.setToolTipText(bundle.getString("applyButton.tip")); // NOI18N
        applyButton.setName("applyButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(applyButton, gridBagConstraints);

        okButton.setAction(actionMap.get("ok")); // NOI18N
        okButton.setText(bundle.getString("okButton.text")); // NOI18N
        okButton.setToolTipText(bundle.getString("okButton.tip")); // NOI18N
        okButton.setEnabled(false);
        okButton.setName("okButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(okButton, gridBagConstraints);

        closeButton.setAction(actionMap.get("closeWindow")); // NOI18N
        closeButton.setText(bundle.getString("closeButton.closeText")); // NOI18N
        closeButton.setToolTipText(bundle.getString("closeButton.closeTip")); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(closeButton, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addArgButton;
    private javax.swing.JButton applyButton;
    private javax.swing.JComboBox argTypeComboBox;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton okButton;
    private javax.swing.JButton revertButton;
    private javax.swing.JCheckBox varyArgCheckBox;
    // End of variables declaration//GEN-END:variables
}
