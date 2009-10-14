package org.openshapa.views;

import org.openshapa.OpenSHAPA;
import org.openshapa.db.DBIndex;
import org.openshapa.db.Database;
import org.openshapa.db.FloatFormalArg;
import org.openshapa.db.FormalArgument;
import org.openshapa.db.IntFormalArg;
import org.openshapa.db.LogicErrorException;
import org.openshapa.db.MatrixVocabElement;
import org.openshapa.db.MatrixVocabElement.MatrixType;
import org.openshapa.db.NominalFormalArg;
import org.openshapa.db.PredicateVocabElement;
import org.openshapa.db.QuoteStringFormalArg;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.UnTypedFormalArg;
import org.openshapa.db.VocabElement;
import org.openshapa.views.discrete.datavalues.vocabelements.FormalArgEditor;
import org.openshapa.views.discrete.datavalues.vocabelements.VocabElementV;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.openshapa.db.Column;
import org.openshapa.db.DataColumn;

/**
 * A view for editing the database vocab.
 */
public final class VocabEditorV extends OpenSHAPADialog {

    /** The database that this vocab editor is manipulating. */
    private Database db;
    /** The logger for the vocab editor. */
    private static Logger logger = Logger.getLogger(VocabEditorV.class);
    /** The currently selected vocab element. */
    private VocabElementV selectedVocabElement;
    /** The currently selected formal argument. */
    private FormalArgEditor selectedArgument;
    /** Index of the currently selected formal argument within the element. */
    private int selectedArgumentI;
    /** The collection of vocab element views in the current vocab listing. */
    private Vector<VocabElementV> veViews;
    /** Collection of vocab element views that are to be deleted completely. */
    private Vector<VocabElementV> veViewsToDeleteCompletely;
    /** Vertical frame for holding the current listing of Vocab elements. */
    private JPanel verticalFrame;
    /** Counter used to increment the new predicate name. */
    private int numNewPreds;
    /** Counter used to increment the new matrix name. */
    private int numNewMats;

    /**
     * Constructor.
     *
     * @param parent The parent frame for the vocab editor.
     * @param modal Is this dialog to be modal or not?
     */
    public VocabEditorV(final Frame parent, final boolean modal) {
        super(parent, modal);

        db = OpenSHAPA.getDatabase();
        initComponents();
        setName(this.getClass().getSimpleName());
        selectedVocabElement = null;
        selectedArgument = null;
        selectedArgumentI = -1;
        numNewPreds = 1;
        numNewMats = 1;

        // Populate current vocab list with vocab data from the database.
        veViews = new Vector<VocabElementV>();
        veViewsToDeleteCompletely = new Vector<VocabElementV>();
        verticalFrame = new JPanel();
        verticalFrame.setName("verticalFrame");
        verticalFrame.setLayout(new BoxLayout(verticalFrame, BoxLayout.Y_AXIS));
        try {
            Vector<MatrixVocabElement> matVEs = db.getMatrixVEs();
            for (int i = matVEs.size() - 1; i >= 0; i--) {
                MatrixVocabElement mve = matVEs.elementAt(i);
                VocabElementV matrixV = new VocabElementV(mve, this);
                verticalFrame.add(matrixV);
                veViews.add(matrixV);
            }

            Vector<PredicateVocabElement> predVEs = db.getPredVEs();
            for (int i = predVEs.size() - 1; i >= 0; i--) {
                PredicateVocabElement pve = predVEs.elementAt(i);
                VocabElementV predicateV = new VocabElementV(pve, this);
                verticalFrame.add(predicateV);
                veViews.add(predicateV);
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to populate current vocab list", e);
        }

        // Add a pad cell to fill out the bottom of the vertical frame.
//        verticalFrame.add(new JPanel());
        JPanel holdPanel = new JPanel();
        holdPanel.setBackground(Color.white);
        holdPanel.setLayout(new BorderLayout());
        holdPanel.add(verticalFrame, BorderLayout.NORTH);
        currentVocabList.setViewportView(holdPanel);
//        jPanel1.add(verticalFrame, BorderLayout.NORTH);
        updateDialogState();
        this.getRootPane().setDefaultButton(okButton);

        // Hide all the broken stuff.
        this.deleteButton.setVisible(false);
        this.argTypeComboBox.setVisible(false);
        this.varyArgCheckBox.setVisible(false);
        this.addArgButton.setVisible(false);
    }

    /**
     * The action to invoke when the user clicks on the add predicate button.
     */
    @Action
    public void addPredicate() {
        try {
            PredicateVocabElement pve = new PredicateVocabElement(db,
                                        "predicate" + numNewPreds);
            addVocabElement(pve);
            numNewPreds++;

        } catch (SystemErrorException e) {
            logger.error("Unable to create predicate vocab element", e);
        }

        updateDialogState();
    }

    /**
     * The action to invoke when the user clicks on the add matrix button.
     */
    @Action
    public void addMatrix() {
        try {
            MatrixVocabElement mve = new MatrixVocabElement(db, "matrix"
                                                                + numNewMats);
            mve.setType(MatrixType.MATRIX);
            addVocabElement(mve);
            numNewMats++;
        } catch (SystemErrorException e) {
            logger.error("Unable to create matrix vocab element", e);
        }

        updateDialogState();
    }

    /**
     * Adds a vocab element to the vocab editor panel.
     *
     * @param ve The vocab element to add to the vocab editor.
     *
     * @throws SystemErrorException If unable to add the vocab element to the
     * vocab editor.
     */
    public void addVocabElement(VocabElement ve) throws SystemErrorException {
        // The database dictates that vocab elements must have a single argument
        // add a default to get started.
        ve.appendFormalArg(new IntFormalArg(db, "<integer>"));

        VocabElementV vev = new VocabElementV(ve, this);
        vev.setHasChanged(true);
        verticalFrame.add(vev);
        verticalFrame.validate();
        veViews.add(vev);
        // vev.getNameComponent().requestFocus();
    }

    /**
     * The action to invoke when the user clicks on the move arg left button.
     */
    @Action
    public void moveArgumentLeft() {
        try {
            VocabElement ve = selectedVocabElement.getModel();
            ve.deleteFormalArg(selectedArgumentI);
            ve.insertFormalArg(selectedArgument.getModel(),
                    (selectedArgumentI - 1));
            selectedVocabElement.setHasChanged(true);
            selectedVocabElement.rebuildContents();

            updateDialogState();
        } catch (SystemErrorException e) {
            logger.error("Unable to move formal argument left", e);
        }
    }

    /**
     * The action to invoke when the user clicks on the move arg right button.
     */
    @Action
    public void moveArgumentRight() {
        try {
            VocabElement ve = selectedVocabElement.getModel();
            ve.deleteFormalArg(selectedArgumentI);
            ve.insertFormalArg(selectedArgument.getModel(),
                    (selectedArgumentI + 1));
            selectedVocabElement.setHasChanged(true);
            selectedVocabElement.rebuildContents();

            updateDialogState();
        } catch (SystemErrorException e) {
            logger.error("Unable to move formal argument right", e);
        }
    }

    /**
     * The action to invoke when the user clicks on the add argument button.
     */
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

            VocabElement ve = selectedVocabElement.getModel();
            ve.appendFormalArg(fa);

            // Store the selectedVocabElement in a temp variable - rebuilding
            // contents may alter the currently selected vocab element.
            VocabElementV temp = selectedVocabElement;
            temp.setHasChanged(true);
            temp.rebuildContents();

            // Select the contents of the newly created formal argument.
            FormalArgEditor faV = temp.getArgumentView(fa);
//            faV.requestFocus();

            updateDialogState();

        } catch (SystemErrorException e) {
            logger.error("Unable to create formal argument.", e);
        }
    }

    /**
     * The action to invoke when the user toggles the varying argument state.
     */
    @Action
    public void setVaryingArgs() {
        if (selectedVocabElement != null) {
            try {
                selectedVocabElement.getModel()
                                    .setVarLen(varyArgCheckBox.isSelected());
                selectedVocabElement.rebuildContents();
            } catch (SystemErrorException e) {
                logger.error("Unable to set varying arguments.", e);
            }
        }
    }

    /**
     * The action to invoke when the user presses the delete button.
     */
    @Action
    public void delete() {
        // User has vocab element selected - mark it for deletion.
        if (selectedVocabElement != null && selectedArgument == null) {
            veViewsToDeleteCompletely.add(selectedVocabElement);
            selectedVocabElement.setDeleted(true);

            // User has argument selected - delete it from the vocab element.
        } else if (selectedArgument != null) {
            VocabElement ve = selectedVocabElement.getModel();
            try {
                ve.deleteFormalArg(selectedArgumentI);
                selectedVocabElement.setHasChanged(true);
                selectedVocabElement.rebuildContents();
            } catch (SystemErrorException e) {
                logger.error("Unable to selected argument", e);
            }
        }

        updateDialogState();
    }

    /**
     * The action to invoke when the user presses the revert button.
     */
    @Action
    public void revertChanges() {
        try {
            ArrayList<VocabElementV> toDelete = new ArrayList<VocabElementV>();

            for (VocabElementV view : veViews) {
                if (view.hasChanged()) {
                    // If the change is an existing vocab element - discard the
                    // changes in the view.
                    VocabElement ve = view.getModel();
                    if (ve.getID() != DBIndex.INVALID_ID) {
                        view.setModel(db.getVocabElement(ve.getID()));

                    // If the change is a new vocab element - mark the view for
                    // deletion.
                    } else {
                        toDelete.add(view);
                    }

                    // If the change is a delete - discard the delete change.

                    // Mark the view as unchanged.
                    view.setHasChanged(false);
                }
            }

            // Perform the removal of the vocab elements.
            for (VocabElementV view : toDelete) {
                verticalFrame.remove(view);
                veViews.remove(view);
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to revert changes in vocab editor.", e);
        }

        updateDialogState();
    }

    /**
     * The action to invoke when the user presses the apply button.
     */
    @Action
    public void applyChanges() {

        /* OLD apply:
        try {
            for (VocabElementV view : veViewsToDeleteCompletely) {
                db.removeVE(view.getModel().getID());
                verticalFrame.remove(view);
                veViews.remove(view);
            }

            for (VocabElementV vev : veViews) {
                if (vev.hasChanged()) {

                    VocabElement ve = vev.getModel();
                    if (ve.getID() == DBIndex.INVALID_ID) {
                        VocabList.isValidElement(db.getVocabList(), ve);
                        long id = db.addVocabElement(ve);
                        vev.setModel(db.getVocabElement(id));

                    } else {
                        db.replaceVocabElement(ve);
                    }
                    vev.setHasChanged(false);
                }
            }

            veViewsToDeleteCompletely.clear();
            updateDialogState();

        } catch (SystemErrorException e) {
            logger.error("Unable to apply vocab changes", e);
        } catch (LogicErrorException le) {
            OpenSHAPA.getApplication().showWarningDialog(le);
        }
        */

        try {
            for (VocabElementV view : veViewsToDeleteCompletely) {
                //db.removePredVE(view.getModel().getID());
                //this needs to be db.removeVocabElement()
                verticalFrame.remove(view);
                veViews.remove(view);
            }

            for (VocabElementV vev : veViews) {
                if (vev.hasChanged()) {

                    VocabElement ve = vev.getModel();
                    if (ve.getID() == DBIndex.INVALID_ID) {
                        if ((db.colNameInUse(ve.getName()) ||
                            (db.predNameInUse(ve.getName())))) {

                            // the string passed to the exception probably
                            // should be modified to allow localization.
                            throw new LogicErrorException("ve name in use");
                        }

                        // If the new vocab element is a matrix vocab element,
                        // we actually need to create a column.
                        if (ve.getClass() == MatrixVocabElement.class) {
                            Column.isValidColumnName(OpenSHAPA.getDatabase(),
                                                     ve.getName());
                            DataColumn dc = new DataColumn(db,
                                                           ve.getName(),
                                          MatrixVocabElement.MatrixType.MATRIX);
                            long colID = db.addColumn(dc);
                            dc = db.getDataColumn(colID);
                            long mveID = dc.getItsMveID();
                            MatrixVocabElement mve = db.getMatrixVE(mveID);
                            // Delete default formal argument.
                            mve.deleteFormalArg(0);

                            // Add the formal arguments from the editor into
                            // the database vocab element.
                            for (int i = 0; i < ve.getNumFormalArgs(); i++) {
                                mve.appendFormalArg(ve.getFormalArgCopy(i));
                            }
                            db.replaceVocabElement(mve);
                            vev.setModel(mve);

                        // Otherwise just a predicate - add the new vocab
                        // element to the database.
                        } else {
                            long id = db.addVocabElement(ve);
                            vev.setModel(db.getVocabElement(id));
                        }

                    } else {
                        db.replaceVocabElement(ve);
                    }
                    vev.setHasChanged(false);
                }
            }

            veViewsToDeleteCompletely.clear();
            updateDialogState();
            ((OpenSHAPAView) OpenSHAPA.getApplication().getMainView())
                                      .showSpreadsheet();

        } catch (SystemErrorException e) {
            logger.error("Unable to apply vocab changes", e);
        } catch (LogicErrorException le) {
            OpenSHAPA.getApplication().showWarningDialog(le);
        }
    }

    /**
     * The action to invoke when the user presses the OK button.
     */
    @Action
    public void ok() {
        applyChanges();
        try {
            this.dispose();
            this.finalize();
        } catch (Throwable e) {
            logger.error("Unable to destroy vocab editor view.", e);
        }
    }

    /**
     * The action to invoke when the user presses the cancel button.
     */
    @Action
    public void closeWindow() {
        try {
            this.dispose();
            this.finalize();
        } catch (Throwable e) {
            logger.error("Unable to destroy vocab editor view.", e);
        }
    }

    /**
     * Method to update the visual state of the dialog to match the underlying
     * model.
     */
    public void updateDialogState() {
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(VocabEditorV.class);

        boolean containsC = false;
        selectedVocabElement = null;
        selectedArgument = null;
        for (VocabElementV vev : veViews) {
            // A vocab element has focus - enable certain things.
            if (vev.hasFocus()) {
                selectedVocabElement = vev;
                selectedArgument = vev.getArgWithFocus();
            }

            // A vocab element contains a change - enable certain things.
            if (vev.hasChanged() || vev.isDeletable()) {
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
            varyArgCheckBox.setSelected(selectedVocabElement.getModel()
                                                            .getVarLen());
        } else {
            addArgButton.setEnabled(false);
            argTypeComboBox.setEnabled(false);
            varyArgCheckBox.setEnabled(false);
            deleteButton.setEnabled(false);
        }

        if (selectedArgument != null) {
            FormalArgument fa = selectedArgument.getModel();

            if (fa.getClass().equals(IntFormalArg.class)) {
                this.argTypeComboBox.setSelectedItem("Integer");
            } else if (fa.getClass().equals(FloatFormalArg.class)) {
                this.argTypeComboBox.setSelectedItem("Float");
            } else if (fa.getClass().equals(NominalFormalArg.class)) {
                this.argTypeComboBox.setSelectedItem("Nominal");
            } else if (fa.getClass().equals(QuoteStringFormalArg.class)) {
                this.argTypeComboBox.setSelectedItem("Text");
            } else {
                this.argTypeComboBox.setSelectedItem("Untyped");
            }

            // W00t - argument is selected - populate the index so that the user
            // can shift the argument around.
            selectedArgumentI = selectedVocabElement.getModel().findFormalArgIndex(selectedArgument.getModel());

            if (selectedArgumentI > 0) {
                this.moveArgLeftButton.setEnabled(true);
            } else {
                this.moveArgLeftButton.setEnabled(false);
            }

            try {
                if (selectedArgumentI < (selectedVocabElement.getModel().getNumFormalArgs() - 1)) {
                    this.moveArgRightButton.setEnabled(true);
                } else {
                    this.moveArgRightButton.setEnabled(false);
                }
            } catch (SystemErrorException e) {
                logger.error("Unable to get num of formal args", e);
            }
        } else {
            this.moveArgLeftButton.setEnabled(false);
            this.moveArgRightButton.setEnabled(false);
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
        revertButton = new javax.swing.JButton();
        applyButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        spacer = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/openshapa/views/resources/VocabEditorV"); // NOI18N
        setTitle(bundle.getString("window.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(org.openshapa.OpenSHAPA.class).getContext().getActionMap(VocabEditorV.class, this);
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

        moveArgLeftButton.setAction(actionMap.get("moveArgumentLeft")); // NOI18N
        moveArgLeftButton.setText(bundle.getString("moveArgLeftButton.text")); // NOI18N
        moveArgLeftButton.setToolTipText(bundle.getString("moveArgLeftButton.tip")); // NOI18N
        moveArgLeftButton.setName("moveArgLeftButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(moveArgLeftButton, gridBagConstraints);

        moveArgRightButton.setAction(actionMap.get("moveArgumentRight")); // NOI18N
        moveArgRightButton.setText(bundle.getString("moveArgRightButton.text")); // NOI18N
        moveArgRightButton.setToolTipText(bundle.getString("moveArgRightButton.tip")); // NOI18N
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
        argTypeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                argTypeComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(argTypeComboBox, gridBagConstraints);

        varyArgCheckBox.setAction(actionMap.get("setVaryingArgs")); // NOI18N
        varyArgCheckBox.setText(bundle.getString("varyArgCheckBox.text")); // NOI18N
        varyArgCheckBox.setToolTipText(bundle.getString("varyArgCheckBox.tip")); // NOI18N
        varyArgCheckBox.setName("varyArgCheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(varyArgCheckBox, gridBagConstraints);

        deleteButton.setAction(actionMap.get("delete")); // NOI18N
        deleteButton.setText(bundle.getString("deleteButton.text")); // NOI18N
        deleteButton.setToolTipText(bundle.getString("deleteButton.tip")); // NOI18N
        deleteButton.setName("deleteButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        getContentPane().add(deleteButton, gridBagConstraints);

        currentVocabList.setName("currentVocabList"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
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

        spacer.setName("spacer"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(spacer, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The action to invoke when the user changes the formal argument dropdown.
     *
     * @param evt The event that triggered this action.
     */
    private void argTypeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_argTypeComboBoxItemStateChanged
        if (selectedVocabElement != null
            && selectedArgument != null
            && evt.getStateChange() == ItemEvent.SELECTED) {

            // Need to change the type of the selected argument.
            FormalArgument oldArg = selectedArgument.getModel();
            FormalArgument newArg = null;

            try {
                if (evt.getItem().equals("Untyped")) {
                    newArg = new UnTypedFormalArg(db, oldArg.getFargName());
                } else if (evt.getItem().equals("Text")) {
                    newArg = new QuoteStringFormalArg(db, oldArg.getFargName());
                } else if (evt.getItem().equals("Nominal")) {
                    newArg = new NominalFormalArg(db, oldArg.getFargName());
                } else if (evt.getItem().equals("Integer")) {
                    newArg = new IntFormalArg(db, oldArg.getFargName());
                } else {
                    newArg = new FloatFormalArg(db, oldArg.getFargName());
                }

                if (oldArg.getFargType().equals(newArg.getFargType())) {
                    return;
                }

                selectedVocabElement.getModel()
                                    .replaceFormalArg(newArg,
                                                  selectedArgument.getArgPos());
                selectedVocabElement.setHasChanged(true);

                // Store the selectedVocabElement in a temp variable -
                // rebuilding contents may alter the currently selected vocab
                // element.
                VocabElementV temp = selectedVocabElement;
                temp.rebuildContents();

                // Select the contents of the newly created formal argument.
                FormalArgEditor faV = temp.getArgumentView(newArg);
                temp.requestArgFocus(faV);

                updateDialogState();

            } catch (SystemErrorException se) {
                logger.error("Unable to alter selected argument.", se);
            }
        }
    }//GEN-LAST:event_argTypeComboBoxItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addArgButton;
    private javax.swing.JButton addMatrixButton;
    private javax.swing.JButton addPredicateButton;
    private javax.swing.JButton applyButton;
    private javax.swing.JComboBox argTypeComboBox;
    private javax.swing.JButton closeButton;
    private javax.swing.JScrollPane currentVocabList;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton moveArgLeftButton;
    private javax.swing.JButton moveArgRightButton;
    private javax.swing.JButton okButton;
    private javax.swing.JButton revertButton;
    private javax.swing.JPanel spacer;
    private javax.swing.JCheckBox varyArgCheckBox;
    // End of variables declaration//GEN-END:variables
}
