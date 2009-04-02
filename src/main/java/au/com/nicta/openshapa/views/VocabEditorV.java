package au.com.nicta.openshapa.views;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.LogicErrorException;
import au.com.nicta.openshapa.db.MatrixVocabElement;
import au.com.nicta.openshapa.db.PredicateVocabElement;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.views.discrete.datavalues.vocabelements.MatrixVEV;
import au.com.nicta.openshapa.views.discrete.datavalues.vocabelements.PredicateVEV;
import au.com.nicta.openshapa.views.discrete.datavalues.vocabelements.VocabElementV;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author cfreeman
 */
public class VocabEditorV extends OpenSHAPADialog {

    /** The database that this vocab editor is manipulating. */
    private Database db;

    /** The logger for OpenSHAPA. */
    private static Logger logger = Logger.getLogger(VocabEditorV.class);

    private boolean containsChange;

    private JTextField componentToSelect;

    private Vector<VocabElementV> veViews;

    /** Creates new form VocabEditorV */
    public VocabEditorV(java.awt.Frame parent,
                        boolean modal,
                        final ActionListener listener) {
        super(parent, modal);

        db = OpenSHAPA.getDatabase();
        containsChange = false;
        initComponents();
        componentToSelect = null;
        veViews = new Vector<VocabElementV>();

        // Populate current vocab list with vocab data from the database.
        try {
            Vector<PredicateVocabElement> predVEs = db.getPredVEs();
            for (PredicateVocabElement pve : predVEs) {
                veViews.add(new PredicateVEV(pve, this));
            }

            Vector<MatrixVocabElement> matVEs = db.getMatrixVEs();
            for (MatrixVocabElement mve : matVEs) {
                veViews.add(new MatrixVEV(mve, this));
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to populate current vocab list", e);
        }

        this.updateDialogState();
    }

    @Action
    public void addPredicate() {        
        try {
            PredicateVocabElement pve = new PredicateVocabElement(db, "predicate");
            PredicateVEV pvev = new PredicateVEV(pve, this);
            pvev.setHasChanged(true);
            veViews.add(pvev);
            componentToSelect = pvev.getNameComponent();

        } catch (SystemErrorException e) {
            logger.error("Unable to create predicate vocab element", e);
        }

        containsChange = true;
        this.updateDialogState();
    }

    @Action
    public void addMatrix() {
        try {
            MatrixVocabElement mve = new MatrixVocabElement(db, "matrix");
            MatrixVEV mvev = new MatrixVEV(mve, this);
            mvev.setHasChanged(true);
            veViews.add(mvev);
            componentToSelect = mvev.getNameComponent();

        } catch (SystemErrorException e) {
            logger.error("Unable to create predicate vocab element", e);
        }

        containsChange = true;
        this.updateDialogState();
    }

    public void updateDialogState() {
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

        jPanel1.removeAll();
        Box vertical = Box.createVerticalBox();

        for (VocabElementV vev : veViews) {
            vertical.add(vev);
        }

        // Add a pad cell, add the box to the current vocab list and revalidate.
        vertical.add(new JPanel());
        jPanel1.add(vertical, BorderLayout.NORTH);
        validate();

        // Select new component if freshly created.
        if (componentToSelect != null) {
            componentToSelect.requestFocus();
            componentToSelect = null;
        }
    }

    @Action
    public void revertChanges() {
        int tableSize = veViews.size();
        int curPos = 0;
        for (int i = 0; i < tableSize; i++) {
            if (veViews.get(curPos).hasChanged()) {
                veViews.remove(curPos);
            } else {
                curPos++;
            }
        }

        containsChange = false;
        updateDialogState();
    }

    @Action
    public void applyChanges() {

        try {
            for (VocabElementV vev : veViews) {
                if (vev.hasChanged()) {
                    db.addVocabElement(vev.getVocabElement());
                    vev.setHasChanged(false);
                    containsChange = false;
                    updateDialogState();
                }
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to apply vocab changes", e);
        } catch (LogicErrorException le) {
            OpenSHAPA.getApplication().showWarningDialog(le);
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

        addPredicateButton = new javax.swing.JButton();
        addMatrixButton = new javax.swing.JButton();
        moveArgLeftButton = new javax.swing.JButton();
        moveArgRightButton = new javax.swing.JButton();
        addArgButton = new javax.swing.JButton();
        argTypeComboBox = new javax.swing.JComboBox();
        varyArgCheckBox = new javax.swing.JCheckBox();
        deleteButton = new javax.swing.JButton();
        currentVocabList = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
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
        addPredicateButton.setAction(actionMap.get("addPredicate")); // NOI18N
        addPredicateButton.setText(bundle.getString("addPredicateButton.text")); // NOI18N
        addPredicateButton.setToolTipText(bundle.getString("addPredicateButton.tip")); // NOI18N
        addPredicateButton.setName("addPredicateButton"); // NOI18N
        getContentPane().add(addPredicateButton, new java.awt.GridBagConstraints());

        addMatrixButton.setAction(actionMap.get("addMatrix")); // NOI18N
        addMatrixButton.setText(bundle.getString("addMatrixButton.text")); // NOI18N
        addMatrixButton.setToolTipText(bundle.getString("addMatrixButton.tip")); // NOI18N
        addMatrixButton.setName("addMatrixButton"); // NOI18N
        getContentPane().add(addMatrixButton, new java.awt.GridBagConstraints());

        moveArgLeftButton.setText(bundle.getString("moveArgLeftButton.text")); // NOI18N
        moveArgLeftButton.setToolTipText(bundle.getString("moveArgLeftButton.tip")); // NOI18N
        moveArgLeftButton.setEnabled(false);
        moveArgLeftButton.setName("moveArgLeftButton"); // NOI18N
        getContentPane().add(moveArgLeftButton, new java.awt.GridBagConstraints());

        moveArgRightButton.setText(bundle.getString("moveArgRightButton.text")); // NOI18N
        moveArgRightButton.setToolTipText(bundle.getString("moveArgRightButton.tip")); // NOI18N
        moveArgRightButton.setEnabled(false);
        moveArgRightButton.setName("moveArgRightButton"); // NOI18N
        getContentPane().add(moveArgRightButton, new java.awt.GridBagConstraints());

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

        currentVocabList.setName("currentVocabList"); // NOI18N
        currentVocabList.setPreferredSize(new java.awt.Dimension(4, 400));

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridLayout(1, 1));
        currentVocabList.setViewportView(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(currentVocabList, gridBagConstraints);

        revertButton.setAction(actionMap.get("revertChanges")); // NOI18N
        revertButton.setText(bundle.getString("revertButton.text")); // NOI18N
        revertButton.setToolTipText(bundle.getString("revertButton.tip")); // NOI18N
        revertButton.setEnabled(false);
        revertButton.setName("revertButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(revertButton, gridBagConstraints);

        applyButton.setAction(actionMap.get("applyChanges")); // NOI18N
        applyButton.setText(bundle.getString("applyButton.text")); // NOI18N
        applyButton.setToolTipText(bundle.getString("applyButton.tip")); // NOI18N
        applyButton.setEnabled(false);
        applyButton.setName("applyButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(applyButton, gridBagConstraints);

        okButton.setAction(actionMap.get("ok")); // NOI18N
        okButton.setText(bundle.getString("okButton.text")); // NOI18N
        okButton.setToolTipText(bundle.getString("okButton.tip")); // NOI18N
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
    private javax.swing.JButton addMatrixButton;
    private javax.swing.JButton addPredicateButton;
    private javax.swing.JButton applyButton;
    private javax.swing.JComboBox argTypeComboBox;
    private javax.swing.JButton closeButton;
    private javax.swing.JScrollPane currentVocabList;
    private javax.swing.JButton deleteButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton moveArgLeftButton;
    private javax.swing.JButton moveArgRightButton;
    private javax.swing.JButton okButton;
    private javax.swing.JButton revertButton;
    private javax.swing.JCheckBox varyArgCheckBox;
    // End of variables declaration//GEN-END:variables
}
