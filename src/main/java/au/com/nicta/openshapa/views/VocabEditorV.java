package au.com.nicta.openshapa.views;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.FloatFormalArg;
import au.com.nicta.openshapa.db.FormalArgument;
import au.com.nicta.openshapa.db.IntFormalArg;
import au.com.nicta.openshapa.db.LogicErrorException;
import au.com.nicta.openshapa.db.MatrixVocabElement;
import au.com.nicta.openshapa.db.MatrixVocabElement.MatrixType;
import au.com.nicta.openshapa.db.NominalFormalArg;
import au.com.nicta.openshapa.db.PredicateVocabElement;
import au.com.nicta.openshapa.db.QuoteStringFormalArg;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.UnTypedFormalArg;
import au.com.nicta.openshapa.db.VocabElement;
import au.com.nicta.openshapa.views.discrete.datavalues.vocabelements.MatrixVEV;
import au.com.nicta.openshapa.views.discrete.datavalues.vocabelements.PredicateVEV;
import au.com.nicta.openshapa.views.discrete.datavalues.vocabelements.VocabElementV;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JPanel;
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

    /** The logger for the vocab editor. */
    private static Logger logger = Logger.getLogger(VocabEditorV.class);

    /** The currently selected vocab element. */
    private VocabElementV selectedVocabElement;

    /** The collection of vocab element views in the current vocab listing. */
    private Vector<VocabElementV> veViews;

    /** Vertical frame for holding the current listing of Vocab elements. */
    private Box verticalFrame;

    /** Creates new form VocabEditorV */
    public VocabEditorV(java.awt.Frame parent,
                        boolean modal,
                        final ActionListener listener) {
        super(parent, modal);

        db = OpenSHAPA.getDatabase();
        initComponents();
        selectedVocabElement = null;

        // Populate current vocab list with vocab data from the database.
        veViews = new Vector<VocabElementV>();
        verticalFrame = Box.createVerticalBox();
        try {
            Vector<PredicateVocabElement> predVEs = db.getPredVEs();
            for (PredicateVocabElement pve : predVEs) {
                VocabElementV predicateV = new PredicateVEV(pve, this);
                verticalFrame.add(predicateV);
                veViews.add(predicateV);
            }

            Vector<MatrixVocabElement> matVEs = db.getMatrixVEs();
            for (MatrixVocabElement mve : matVEs) {
                VocabElementV matrixV = new MatrixVEV(mve, this);
                verticalFrame.add(matrixV);
                veViews.add(matrixV);
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to populate current vocab list", e);
        }

        // Add a pad cell to fill out the bottom of the vertical frame.
        verticalFrame.add(new JPanel());
        jPanel1.add(verticalFrame, BorderLayout.NORTH);
        updateDialogState();
    }

    @Action
    public void addPredicate() {        
        try {
            PredicateVocabElement pve = new PredicateVocabElement(db,
                                                                  "predicate");
            PredicateVEV pvev = new PredicateVEV(pve, this);
            pvev.setHasChanged(true);
            verticalFrame.add(pvev, (verticalFrame.getComponentCount() - 1));
            verticalFrame.validate();
            veViews.add(pvev);

            pvev.getNameComponent().selectAll();
            pvev.getNameComponent().requestFocus();

        } catch (SystemErrorException e) {
            logger.error("Unable to create predicate vocab element", e);
        }

        updateDialogState();
    }

    @Action
    public void addMatrix() {
        try {
            MatrixVocabElement mve = new MatrixVocabElement(db, "matrix");
            mve.setType(MatrixType.MATRIX);
            MatrixVEV mvev = new MatrixVEV(mve, this);
            mvev.setHasChanged(true);                       
            verticalFrame.add(mvev, (verticalFrame.getComponentCount() - 1));
            verticalFrame.validate();
            veViews.add(mvev);

            mvev.getNameComponent().selectAll();
            mvev.getNameComponent().requestFocus();

        } catch (SystemErrorException e) {
            logger.error("Unable to create predicate vocab element", e);
        }

        updateDialogState();
    }

    @Action
    public void addArgument() {
        try {
            String type = (String) this.argTypeComboBox.getSelectedItem();
            FormalArgument fa;

            if (type.equals("Untyped")) {
                fa = new UnTypedFormalArg(db, "<untyped>");
            } else if (type.equals("Text")) {
                fa = new QuoteStringFormalArg(db, "<text>");
            } else if (type.equals("Nominal")) {
                fa = new NominalFormalArg(db, "<nominal>");
            } else if (type.equals("Integer")) {
                fa = new IntFormalArg(db, "<integer>");
            } else {
                fa = new FloatFormalArg(db, "<float>");
            }

            VocabElement ve = selectedVocabElement.getVocabElement();
            ve.appendFormalArg(fa);
            selectedVocabElement.rebuildContents();
            updateDialogState();
        } catch (SystemErrorException e) {
            logger.error("Unable to create formal argument.", e);
        }
    }

    @Action
    public void setVaryingArgs() {
        if (selectedVocabElement != null) {
            try {
                selectedVocabElement.getVocabElement()
                                    .setVarLen(varyArgCheckBox.isSelected());
                selectedVocabElement.rebuildContents();
            } catch (SystemErrorException e) {
                logger.error("Unable to set varying arguments.", e);
            }
        }
    }

    @Action
    public void delete() {
        /*
        if (selectedVocabElement != null) {
            db.removePredVE(WIDTH);
        }*/
        updateDialogState();
    }

    public void updateDialogState() {
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(VocabEditorV.class);

        boolean containsC = false;
        selectedVocabElement = null;
        for (VocabElementV vev : veViews) {
            // A vocab element has focus - enable certain things.
            if (vev.hasFocus()) {
                selectedVocabElement = vev;
            }

            // A vocab element contains a change - enable certain things.
            if (vev.hasChanged()) {
                containsC = true;
            }
        }

        if (containsC) {
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

        // If we have a selected vocab element - we can enable additional
        // functionality.
        if (selectedVocabElement != null) {
            addArgButton.setEnabled(true);
            argTypeComboBox.setEnabled(true);
            varyArgCheckBox.setEnabled(true);
            deleteButton.setEnabled(true);
            varyArgCheckBox.setSelected(selectedVocabElement.getVocabElement()
                                                            .getVarLen());
        } else {
            addArgButton.setEnabled(false);
            argTypeComboBox.setEnabled(false);
            varyArgCheckBox.setEnabled(false);
            deleteButton.setEnabled(false);
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

        updateDialogState();
    }

    @Action
    public void applyChanges() {

        try {
            for (VocabElementV vev : veViews) {
                if (vev.hasChanged()) {
                    db.addVocabElement(vev.getVocabElement());
                    vev.setHasChanged(false);
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        getContentPane().add(addPredicateButton, gridBagConstraints);

        addMatrixButton.setAction(actionMap.get("addMatrix")); // NOI18N
        addMatrixButton.setText(bundle.getString("addMatrixButton.text")); // NOI18N
        addMatrixButton.setToolTipText(bundle.getString("addMatrixButton.tip")); // NOI18N
        addMatrixButton.setName("addMatrixButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(addMatrixButton, gridBagConstraints);

        moveArgLeftButton.setText(bundle.getString("moveArgLeftButton.text")); // NOI18N
        moveArgLeftButton.setToolTipText(bundle.getString("moveArgLeftButton.tip")); // NOI18N
        moveArgLeftButton.setEnabled(false);
        moveArgLeftButton.setName("moveArgLeftButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(moveArgLeftButton, gridBagConstraints);

        moveArgRightButton.setText(bundle.getString("moveArgRightButton.text")); // NOI18N
        moveArgRightButton.setToolTipText(bundle.getString("moveArgRightButton.tip")); // NOI18N
        moveArgRightButton.setEnabled(false);
        moveArgRightButton.setName("moveArgRightButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        getContentPane().add(moveArgRightButton, gridBagConstraints);

        addArgButton.setAction(actionMap.get("addArgument")); // NOI18N
        addArgButton.setText(bundle.getString("addArgButton.text")); // NOI18N
        addArgButton.setToolTipText(bundle.getString("addArgButton.tip")); // NOI18N
        addArgButton.setName("addArgButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(addArgButton, gridBagConstraints);

        argTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Untyped", "Text", "Nominal", "Integer", "Float" }));
        argTypeComboBox.setToolTipText(bundle.getString("argTypeComboBox.tip")); // NOI18N
        argTypeComboBox.setEnabled(false);
        argTypeComboBox.setName("argTypeComboBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(argTypeComboBox, gridBagConstraints);

        varyArgCheckBox.setAction(actionMap.get("setVaryingArgs")); // NOI18N
        varyArgCheckBox.setText(bundle.getString("varyArgCheckBox.text")); // NOI18N
        varyArgCheckBox.setToolTipText(bundle.getString("varyArgCheckBox.tip")); // NOI18N
        varyArgCheckBox.setEnabled(false);
        varyArgCheckBox.setName("varyArgCheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(varyArgCheckBox, gridBagConstraints);

        deleteButton.setAction(actionMap.get("delete")); // NOI18N
        deleteButton.setText(bundle.getString("deleteButton.text")); // NOI18N
        deleteButton.setToolTipText(bundle.getString("deleteButton.tip")); // NOI18N
        deleteButton.setEnabled(false);
        deleteButton.setName("deleteButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(currentVocabList, gridBagConstraints);

        revertButton.setAction(actionMap.get("revertChanges")); // NOI18N
        revertButton.setText(bundle.getString("revertButton.text")); // NOI18N
        revertButton.setToolTipText(bundle.getString("revertButton.tip")); // NOI18N
        revertButton.setName("revertButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        getContentPane().add(revertButton, gridBagConstraints);

        applyButton.setAction(actionMap.get("applyChanges")); // NOI18N
        applyButton.setText(bundle.getString("applyButton.text")); // NOI18N
        applyButton.setToolTipText(bundle.getString("applyButton.tip")); // NOI18N
        applyButton.setName("applyButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        getContentPane().add(applyButton, gridBagConstraints);

        okButton.setAction(actionMap.get("ok")); // NOI18N
        okButton.setText(bundle.getString("okButton.text")); // NOI18N
        okButton.setToolTipText(bundle.getString("okButton.tip")); // NOI18N
        okButton.setName("okButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        getContentPane().add(okButton, gridBagConstraints);

        closeButton.setAction(actionMap.get("closeWindow")); // NOI18N
        closeButton.setText(bundle.getString("closeButton.closeText")); // NOI18N
        closeButton.setToolTipText(bundle.getString("closeButton.closeTip")); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
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
