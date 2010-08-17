package org.openshapa.views;

import com.usermetrix.jclient.Logger;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.Column;
import org.openshapa.models.db.DBIndex;
import org.openshapa.models.db.DataColumn;
import org.openshapa.models.db.Database;
import org.openshapa.models.db.FloatFormalArg;
import org.openshapa.models.db.FormalArgument;
import org.openshapa.models.db.IntFormalArg;
import org.openshapa.models.db.LogicErrorException;
import org.openshapa.models.db.MatrixVocabElement;
import org.openshapa.models.db.NominalFormalArg;
import org.openshapa.models.db.PredicateVocabElement;
import org.openshapa.models.db.QuoteStringFormalArg;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.db.UnTypedFormalArg;
import org.openshapa.models.db.VocabElement;
import org.openshapa.models.db.MatrixVocabElement.MatrixType;
import org.openshapa.views.discrete.datavalues.vocabelements.FormalArgEditor;
import org.openshapa.views.discrete.datavalues.vocabelements.VocabElementV;

import com.usermetrix.jclient.UserMetrix;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import org.openshapa.models.db.ExternalColumnListListener;
import org.openshapa.models.db.ExternalDataColumnListener;
import org.openshapa.views.discrete.datavalues.vocabelements.VocabElementRootView;

/**
 * A view for editing the database vocab.
 */
public final class VocabEditorV extends OpenSHAPADialog implements
        ExternalColumnListListener, ExternalDataColumnListener{

    /** The database that this vocab editor is manipulating. */
    private Database db;
    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(VocabEditorV.class);
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
    /** Array of veViews used to track changes for the undo function */
    private Vector<Vector<VocabElementV>> changes;
    /** the current location within the changes array */
    private int changeIndex;

   // private static Color lightBlue = new Color(224,248,255,255);

    /**
     * Constructor.
     * 
     * @param parent
     *            The parent frame for the vocab editor.
     * @param modal
     *            Is this dialog to be modal or not?
     */
    public VocabEditorV(final Frame parent, final boolean modal) {
        super(parent, modal);

        logger.usage("vocEd - show");

        db = OpenSHAPA.getProjectController().getDB();
        initComponents();
        componentListnersInit();
        setName(this.getClass().getSimpleName());
        selectedVocabElement = null;
        selectedArgument = null;
        selectedArgumentI = -1;
        numNewPreds = 1;
        numNewMats = 1;

        changes = new Vector<Vector<VocabElementV>>();
        changeIndex = -1;

        // Populate current vocab list with vocab data from the database.
        veViews = new Vector<VocabElementV>();
        veViewsToDeleteCompletely = new Vector<VocabElementV>();
        verticalFrame = new JPanel();
        verticalFrame.setName("verticalFrame");
        verticalFrame.setLayout(new BoxLayout(verticalFrame, BoxLayout.Y_AXIS));
        
        saveState();

        try {
            Vector<MatrixVocabElement> matVEs = db.getMatrixVEs();
            for (int i = matVEs.size() - 1; i >= 0; i--) {
                MatrixVocabElement mve = matVEs.elementAt(i);
                VocabElementV matrixV = new VocabElementV(mve, this);
                verticalFrame.add(matrixV);
                veViews.add(matrixV);
                numNewMats++;
            }

            Vector<PredicateVocabElement> predVEs = db.getPredVEs();
            for (int i = predVEs.size() - 1; i >= 0; i--) {
                PredicateVocabElement pve = predVEs.elementAt(i);
                VocabElementV predicateV = new VocabElementV(pve, this);
                verticalFrame.add(predicateV);
                veViews.add(predicateV);
                numNewPreds++;
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to populate current vocab list", e);
        }

        // Add a pad cell to fill out the bottom of the vertical frame.
        JPanel holdPanel = new JPanel();
        holdPanel.setBackground(Color.white);
        holdPanel.setLayout(new BorderLayout());
        holdPanel.add(verticalFrame, BorderLayout.NORTH);
        currentVocabList.setViewportView(holdPanel);
        updateDialogState();
        getRootPane().setDefaultButton(okButton);

        // Hide all the broken stuff.
        varyArgCheckBox.setVisible(false);
    }

    /**
     * The action to invoke when the user clicks on the add predicate button.
     */
    @Action
    public void addPredicate() {
        try {
            logger.usage("vocEd - add predicate");
            PredicateVocabElement pve =
                    new PredicateVocabElement(db, "predicate" + numNewPreds);
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
            logger.usage("vocEd - add matrix");
            MatrixVocabElement mve =
                    new MatrixVocabElement(db, "matrix" + numNewMats);
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
     * @param ve
     *            The vocab element to add to the vocab editor.
     * @throws SystemErrorException
     *             If unable to add the vocab element to the vocab editor.
     */
    public void addVocabElement(final VocabElement ve)
            throws SystemErrorException {
        // The database dictates that vocab elements must have a single argument
        // add a default to get started.
        
        ve.appendFormalArg(new NominalFormalArg(db, "<arg0>"));

        VocabElementV vev = new VocabElementV(ve, this);
        vev.setHasChanged(true);
        verticalFrame.add(vev);
        verticalFrame.validate();
        veViews.add(vev);

        vev.getDataView().addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent ke){
                if(ke.isShiftDown()){
                    if(ke.getKeyCode()==KeyEvent.VK_GREATER|| ke.getKeyCode()==KeyEvent.VK_LESS){
                        addArgument();
                    }
                }
                else if(ke.getKeyCode()==KeyEvent.VK_COMMA){
                    addArgument();
                }
                if(ke.getKeyCode()== KeyEvent.VK_LEFT){
                    if(ke.isControlDown() && moveArgLeftButton.isEnabled()){
                        moveArgumentLeft();
                    }
                }
                if(ke.getKeyCode()==KeyEvent.VK_RIGHT){
                    if(ke.isControlDown() && moveArgRightButton.isEnabled()){
                        moveArgumentRight();
                    }
                }
            }
        });

        saveState();

        // add the current vocab list state to an array of states for undo/redo
        
        vev.requestFocus();
    }

    /**
     * The action to invoke when the user clicks on the move arg left button.
     */
    @Action
    public void moveArgumentLeft() {
        try {
            logger.error("vocEd - move argument left");
            VocabElement ve = selectedVocabElement.getModel();
            ve.deleteFormalArg(selectedArgumentI);
            ve.insertFormalArg(selectedArgument.getModel(),
                    (selectedArgumentI - 1));
            selectedVocabElement.setHasChanged(true);
            selectedVocabElement.rebuildContents();

            saveState();
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
            logger.error("vocEd - move argument right");
            VocabElement ve = selectedVocabElement.getModel();
            ve.deleteFormalArg(selectedArgumentI);
            ve.insertFormalArg(selectedArgument.getModel(),
                    (selectedArgumentI + 1));
            selectedVocabElement.setHasChanged(true);
            selectedVocabElement.rebuildContents();

            saveState();
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
            
            VocabElement ve = selectedVocabElement.getModel();
            String type = (String) argTypeComboBox.getSelectedItem();
            logger.usage("vocEd - add argument:" + type);
            String newArgName = "<arg" + ve.getNumFormalArgs() + ">";
            FormalArgument fa;

            if (type.equals("Text")) {
                fa = new QuoteStringFormalArg(db, newArgName);
            } else if (type.equals("Nominal")) {
                fa = new NominalFormalArg(db, newArgName);
            } else if (type.equals("Integer")) {
                fa = new IntFormalArg(db, newArgName);
            } else {
                fa = new FloatFormalArg(db, newArgName);
            }

            ve.appendFormalArg(fa);
            selectedVocabElement.setHasChanged(true);
            selectedVocabElement.rebuildContents();

            saveState();

            // Select the contents of the newly created formal argument.

            //FormalArgEditor faV = selectedVocabElement.getArgumentView(fa);
            //faV.requestFocus();
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
        logger.usage("vocEd - toggle vary args");
        if (selectedVocabElement != null) {
            try {
                selectedVocabElement.getModel().setVarLen(
                        varyArgCheckBox.isSelected());
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
            logger.usage("vocEd - delete element");
            veViewsToDeleteCompletely.add(selectedVocabElement);
            selectedVocabElement.setDeleted(true);

            // User has argument selected - delete it from the vocab element.
        } else if (selectedArgument != null) {
            logger.usage("vocEd - delete argument");
            VocabElement ve = selectedVocabElement.getModel();
            try {
                ve.deleteFormalArg(selectedArgumentI);
                selectedVocabElement.setHasChanged(true);
                selectedVocabElement.rebuildContents();
            } catch (SystemErrorException e) {
                logger.error("Unable to selected argument", e);
            }
        }
        saveState();
        updateDialogState();
    }

    /**
     * The action to invoke when the user presses the revert button.
     */
    @Action
    public void revertChanges() {
        try {
            logger.usage("vocEd - revert");
            ArrayList<VocabElementV> toDelete = new ArrayList<VocabElementV>();

            for (VocabElementV view : veViews) {
                if (view.hasChanged()) {
                    // If the change is an existing vocab element - discard the
                    // changes in the view.
                    VocabElement ve = view.getModel();
                    if (ve.getID() != DBIndex.INVALID_ID) {
                        view.setModel(db.getVocabElement(ve.getID()));

                        // If the change is a new vocab element - mark the view
                        // for
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
        logger.usage("vocEd - apply");
        try {

            for (VocabElementV vev : veViews) {
                if (vev.hasChanged()) {

                    VocabElement ve = vev.getModel();
                    if (ve.getID() == DBIndex.INVALID_ID) {
                        if ((db.colNameInUse(ve.getName()) || (db
                                .predNameInUse(ve.getName())))) {

                            // the string passed to the exception probably
                            // should be modified to allow localization.
                            throw new LogicErrorException("ve name in use");
                        }

                        // If the new vocab element is a matrix vocab element,
                        // we actually need to create a column.
                        if (ve.getClass() == MatrixVocabElement.class) {
                            Column.isValidColumnName(OpenSHAPA
                                    .getProjectController().getDB(), ve
                                    .getName());
                            DataColumn dc =
                                    new DataColumn(
                                            db,
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
            /** trying to delete an unapplied ve results in errors
             *  as it cant be found in db
             */
            for (VocabElementV view : veViewsToDeleteCompletely) {
                db.removeVocabElement(view.getModel().getID());
                verticalFrame.remove(view);
                veViews.remove(view);
            }

            veViewsToDeleteCompletely.clear();
            updateDialogState();
            ((OpenSHAPAView) OpenSHAPA.getApplication().getMainView())
                    .showSpreadsheet();

            changeIndex =-1;
            for(Vector<VocabElementV> veView : changes){
                veView.clear();
                veView = null;
            }

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
        logger.usage("vocEd - ok");
        applyChanges();
        try {
            dispose();
            finalize();
        } catch (Throwable e) {
            logger.error("Unable to destroy vocab editor view.", e);
        }
    }

    /**
     * The action to invoke when the user presses the cancel button.
     */
    @Action
    public void closeWindow() {
        logger.usage("vocEd - close");
        try {
            dispose();
            finalize();
        } catch (Throwable e) {
            logger.error("Unable to destroy vocab editor view.", e);
        }
    }

    /**
     * Returns vector of VocabElementVs
     * 
     * @return veViews Vector of VocabElementVs
     */
    public Vector<VocabElementV> getVocabElements() {
        return veViews;
    }

    /**
     * Method to update the visual state of the dialog to match the underlying
     * model.
     */
    public void updateDialogState() {
        ResourceMap rMap =
                Application.getInstance(OpenSHAPA.class).getContext()
                        .getResourceMap(VocabEditorV.class);

        boolean containsC = false;
        //selectedVocabElement = null;
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

        // Determine if undo/redo buttons are enabled
        if(changeIndex >= 0){
            undoButton.setEnabled(true);
        }else{
            undoButton.setEnabled(false);
        }
        if(changeIndex<changes.size()-1){
            redoButton.setEnabled(true);
        }else{
            redoButton.setEnabled(false);
        }

        if (containsC) {
            closeButton.setText(rMap.getString("closeButton.cancelText"));
            closeButton.setToolTipText(rMap.getString("closeButton.cancelTip"));



            revertButton.setEnabled(true);
            applyButton.setEnabled(true);
            okButton.setEnabled(true);
            
        } else {
            closeButton.setText(rMap.getString("closeButton.cancelText"));
            closeButton.setToolTipText(rMap.getString("closeButton.cancelTip"));
            
            undoButton.setEnabled(false);
            redoButton.setEnabled(false);
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
                argTypeComboBox.setSelectedItem("Integer");
            } else if (fa.getClass().equals(FloatFormalArg.class)) {
                argTypeComboBox.setSelectedItem("Float");
            } else if (fa.getClass().equals(NominalFormalArg.class)) {
                argTypeComboBox.setSelectedItem("Nominal");
            } else if (fa.getClass().equals(QuoteStringFormalArg.class)) {
                argTypeComboBox.setSelectedItem("Text");
            } else {
                argTypeComboBox.setSelectedItem("Untyped");
            }

            // W00t - argument is selected - populate the index so that the user
            // can shift the argument around.
            selectedArgumentI =
                    selectedVocabElement.getModel().findFormalArgIndex(
                            selectedArgument.getModel());

            if (selectedArgumentI > 0) {
                moveArgLeftButton.setEnabled(true);
            } else {
                moveArgLeftButton.setEnabled(false);
            }

            try {
                if (selectedArgumentI < (selectedVocabElement.getModel()
                        .getNumFormalArgs() - 1)) {
                    moveArgRightButton.setEnabled(true);
                } else {
                    moveArgRightButton.setEnabled(false);
                }
            } catch (SystemErrorException e) {
                logger.error("Unable to get num of formal args", e);
            }
        } else {
            moveArgLeftButton.setEnabled(false);
            moveArgRightButton.setEnabled(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
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
        statusBar = new javax.swing.JLabel();
        statusSeperator = new javax.swing.JSeparator();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        getContentPane().add(addPredicateButton, gridBagConstraints);

        addMatrixButton.setAction(actionMap.get("addMatrix")); // NOI18N
        addMatrixButton.setText(bundle.getString("addMatrixButton.text")); // NOI18N
        addMatrixButton.setToolTipText(bundle.getString("addMatrixButton.tip")); // NOI18N
        addMatrixButton.setName("addMatrixButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(addMatrixButton, gridBagConstraints);

        moveArgLeftButton.setAction(actionMap.get("moveArgumentLeft")); // NOI18N
        moveArgLeftButton.setText(bundle.getString("moveArgLeftButton.text")); // NOI18N
        moveArgLeftButton.setToolTipText(bundle.getString("moveArgLeftButton.tip")); // NOI18N
        moveArgLeftButton.setMaximumSize(new java.awt.Dimension(120, 23));
        moveArgLeftButton.setMinimumSize(new java.awt.Dimension(120, 23));
        moveArgLeftButton.setName("moveArgLeftButton"); // NOI18N
        moveArgLeftButton.setPreferredSize(new java.awt.Dimension(120, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        getContentPane().add(moveArgLeftButton, gridBagConstraints);

        moveArgRightButton.setAction(actionMap.get("moveArgumentRight")); // NOI18N
        moveArgRightButton.setText(bundle.getString("moveArgRightButton.text")); // NOI18N
        moveArgRightButton.setToolTipText(bundle.getString("moveArgRightButton.tip")); // NOI18N
        moveArgRightButton.setMaximumSize(new java.awt.Dimension(120, 23));
        moveArgRightButton.setMinimumSize(new java.awt.Dimension(120, 23));
        moveArgRightButton.setName("moveArgRightButton"); // NOI18N
        moveArgRightButton.setPreferredSize(new java.awt.Dimension(120, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
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

        argTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nominal", "Text", "Integer", "Float" }));
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
        deleteButton.setMaximumSize(new java.awt.Dimension(120, 25));
        deleteButton.setMinimumSize(new java.awt.Dimension(120, 25));
        deleteButton.setName("deleteButton"); // NOI18N
        deleteButton.setPreferredSize(new java.awt.Dimension(110, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 5);
        getContentPane().add(deleteButton, gridBagConstraints);

        currentVocabList.setMinimumSize(new java.awt.Dimension(23, 200));
        currentVocabList.setName("currentVocabList"); // NOI18N
        currentVocabList.setPreferredSize(new java.awt.Dimension(200, 200));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
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
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        getContentPane().add(okButton, gridBagConstraints);

        closeButton.setAction(actionMap.get("closeWindow")); // NOI18N
        closeButton.setText(bundle.getString("closeButton.closeText")); // NOI18N
        closeButton.setToolTipText(bundle.getString("closeButton.closeTip")); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        getContentPane().add(closeButton, gridBagConstraints);

        statusBar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(org.openshapa.OpenSHAPA.class).getContext().getResourceMap(VocabEditorV.class);
        statusBar.setText(resourceMap.getString("statusBar.text")); // NOI18N
        statusBar.setDoubleBuffered(true);
        statusBar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        statusBar.setMaximumSize(new java.awt.Dimension(100, 14));
        statusBar.setMinimumSize(new java.awt.Dimension(10, 14));
        statusBar.setName("statusBar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(statusBar, gridBagConstraints);
        statusBar.getAccessibleContext().setAccessibleName(resourceMap.getString("statusBar.AccessibleContext.accessibleName")); // NOI18N

        statusSeperator.setMinimumSize(new java.awt.Dimension(100, 10));
        statusSeperator.setName("statusSeperator"); // NOI18N
        statusSeperator.setPreferredSize(new java.awt.Dimension(2, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(statusSeperator, gridBagConstraints);

        undoButton.setAction(actionMap.get("undoChange")); // NOI18N
        undoButton.setText(resourceMap.getString("undoButton.text")); // NOI18N
        undoButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        undoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoButton.setMaximumSize(new java.awt.Dimension(60, 23));
        undoButton.setMinimumSize(new java.awt.Dimension(60, 23));
        undoButton.setName("undoButton"); // NOI18N
        undoButton.setPreferredSize(new java.awt.Dimension(60, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(undoButton, gridBagConstraints);

        redoButton.setAction(actionMap.get("redoChange")); // NOI18N
        redoButton.setText(resourceMap.getString("redoButton.text")); // NOI18N
        redoButton.setName("redoButton"); // NOI18N
        redoButton.setPreferredSize(new java.awt.Dimension(60, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(redoButton, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The action to invoke when the user changes the formal argument dropdown.
     * 
     * @param evt
     *            The event that triggered this action.
     */
    private void argTypeComboBoxItemStateChanged(
            final java.awt.event.ItemEvent evt) {// GEN-FIRST:event_argTypeComboBoxItemStateChanged
        if (selectedVocabElement != null && selectedArgument != null
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

                selectedVocabElement.getModel().replaceFormalArg(newArg,
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
    }// GEN-LAST:event_argTypeComboBoxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addArgButton;
    private javax.swing.JButton addMatrixButton;
    private javax.swing.JButton addPredicateButton;
    private javax.swing.JButton applyButton;
    private javax.swing.JComboBox argTypeComboBox;
    private javax.swing.JButton closeButton;
    private javax.swing.JScrollPane currentVocabList;
    private javax.swing.JButton deleteButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton moveArgLeftButton;
    private javax.swing.JButton moveArgRightButton;
    private javax.swing.JButton okButton;
    private javax.swing.JButton redoButton;
    private javax.swing.JButton revertButton;
    private javax.swing.JLabel statusBar;
    private javax.swing.JSeparator statusSeperator;
    private javax.swing.JButton undoButton;
    private javax.swing.JCheckBox varyArgCheckBox;
    // End of variables declaration//GEN-END:variables


    @Override
    @SuppressWarnings("element-type-mismatch")
    public void colDeletion(Database newdb, long colID, Vector<Long> old_cov, Vector<Long> new_cov) {
        try {

            long veID = newdb.getDataColumn(colID).getItsMveID();
            if(db.vocabElementExists(veID)){
                VocabElement ve = db.getVocabElement(veID);
                //db.removeVocabElement(veID);
                for(int i=ve.getNumFormalArgs()-1; i>= 0; i--){
                    ve.deleteFormalArg(i);
                }
                int delIndex=0;
                for(VocabElementV view: veViews){
                    long vID = view.getModel().getID();
                    if(vID == veID){
                        verticalFrame.remove(delIndex);
                        verticalFrame.validate();
                        veViews.remove(delIndex);
                        break;  // only ever delete one element & avoid breaking loop
                    }
                    delIndex++;
                }
                db = newdb;
            }
        } catch (SystemErrorException ex) {
            java.util.logging.Logger.getLogger(VocabEditorV.class.getName()).log(Level.SEVERE, null, ex);
        }
        updateDialogState();
    }

    @Override
    public void colInsertion(Database newdb, long colID, Vector<Long> old_cov, Vector<Long> new_cov) {
        try {
            boolean alreadyExists = false;
            for(VocabElementV view: veViews){
                if(view.getModel().getName().contentEquals(newdb.getDataColumn(colID).getName())){
                    alreadyExists = true;
                }
            }
            if(!alreadyExists){
                db = newdb;
                long veID = newdb.getDataColumn(colID).getItsMveID();
                VocabElement ve = newdb.getVocabElement(veID);
                ve.deleteFormalArg(0);
                ve.appendFormalArg(new NominalFormalArg(db,"<arg0>"));
                VocabElementV vev = new VocabElementV(ve, this);
                verticalFrame.add(vev);
                verticalFrame.validate();
                veViews.add(vev);
            }
        } catch (SystemErrorException ex) {
            java.util.logging.Logger.getLogger(VocabEditorV.class.getName()).log(Level.SEVERE, null, ex);
        }
        updateDialogState();
    }

    @Override
    public void colOrderVectorEdited(Database db, Vector<Long> old_cov, Vector<Long> new_cov) {
    }

    private void componentListnersInit() {

        MouseAdapter ma = new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent me){
                String component = me.getComponent().getName();
                if(component.equals("closeButton")){
                    statusBar.setText("Cancel all changes and close the editor");
                }
                else if(component.equals("addPredicateButton")){
                    statusBar.setText("Add a new predicate variable. Hotkey: ctrl + p");
                }
                else if(component.equals("addMatrixButton")){
                    statusBar.setText("Add a new matrix variable. Hotkey: ctrl + m");
                }
                else if(component.equals("undoButton")){
                    statusBar.setText("Undo a series of changes. Hotkey: ctrl + z");
                }
                else if(component.equals("redoButton")){
                    statusBar.setText("Redo any undone changes. Hotkey: ctrl + y");
                }
                else if(component.equals("addArgButton")){
                    statusBar.setText("Add a new argument to a variable. Hotkey: ctrl + a");
                }
                else if(component.equals("deleteButton")){
                    statusBar.setText("Delete an argument or variable. Hotkey: delete");
                }
                else if(component.equals("moveArgLeftButton")){
                    statusBar.setText("Move an argument left within a variable. Hotkey: something");
                }
                else if(component.equals("applyButton")){
                    statusBar.setText("Apply changes to the vocab elements. Hotkey: ctrl + s");
                }
                else if(component.equals("revertButton")){
                    statusBar.setText("Remove all unsaved changes. Hotkey: ctrl + d");
                }
                else if(component.equals("varyArgCheckBox")){
                    statusBar.setText("Make the variable varying args.");
                }
                else if(component.equals("moveArgRightButton")){
                    statusBar.setText("Move an argument right within a variable. Hotkey: something");
                }
                else if(component.equals("okButton")){
                    statusBar.setText("Save changes and close the window.");
                }
                else if(component.equals("argTypeComboBox")){
                    statusBar.setText("Select the argument type.");
                }
                
            }
            @Override
            public void mouseExited(MouseEvent me){
                statusBar.setText(" ");
                //String component = me.getComponent().getName();
                //if(component.equals("currentVocabList")){
                //   veViews.get(0).setBackground(Color.white);
                //}
            }
            /*@Override
            public void mouseClicked(MouseEvent me){
                if(me.getComponent().getName().equals("currentVocabList")){
                    int a = (int) currentVocabList.getViewport().getViewPosition().getY();
                    int x = me.getY() + a;
                    int y = x/29;
                    int z = (int)Math.floor(y);
                    
                    boolean veBlue = veViews.get(z).getBackground().equals(lightBlue);
                    veViews.get(z).requestFocus();
                    for(VocabElementV vev: veViews){
                        vev.setBackground(Color.WHITE);
                    }
                    if(!veBlue){
                        veViews.get(z).setBackground(lightBlue);
                    }
                }
                updateDialogState();
            }*/
        };
        
        KeyAdapter ka = new KeyAdapter(){

            @Override
            public void keyReleased(KeyEvent ke){
                
                int code = ke.getKeyCode();
                if(code==KeyEvent.VK_M){
                    if(ke.isControlDown()){
                        addMatrix();
                    }
                }

                if(code==KeyEvent.VK_P){
                    if(ke.isControlDown()){
                        addPredicate();
                    }
                }

                if(code==KeyEvent.VK_A){
                    if(addArgButton.isEnabled()){
                        if(ke.isControlDown()){
                            addArgument();
                        }
                    }
                }

                if(code==KeyEvent.VK_S){
                    if(ke.isControlDown()){
                        applyChanges();
                    }
                }
                if(code==KeyEvent.VK_D){
                    if(ke.isControlDown()){
                        revertChanges();
                    }
                }
                if(code==KeyEvent.VK_Z){
                    if(ke.isControlDown()){
                        undoChange();
                    }
                }
                if(code==KeyEvent.VK_Y){
                    if(ke.isControlDown()){
                        redoChange();
                    }
                }
                if(code==KeyEvent.VK_DELETE){
                    delete();
                }

                ke.setKeyChar(' ');
            }
        };

        addPredicateButton.addKeyListener(ka);
        addMatrixButton.addKeyListener(ka);
        closeButton.addKeyListener(ka);
        okButton.addKeyListener(ka);
        applyButton.addKeyListener(ka);
        addArgButton.addKeyListener(ka);

        argTypeComboBox.addMouseListener(ma);
        currentVocabList.addMouseListener(ma);
        addMatrixButton.addMouseListener(ma);
        addPredicateButton.addMouseListener(ma);
        deleteButton.addMouseListener(ma);
        okButton.addMouseListener(ma);
        closeButton.addMouseListener(ma);
        revertButton.addMouseListener(ma);
        applyButton.addMouseListener(ma);
        undoButton.addMouseListener(ma);
        redoButton.addMouseListener(ma);
        addArgButton.addMouseListener(ma);
        moveArgLeftButton.addMouseListener(ma);
        moveArgRightButton.addMouseListener(ma);
        varyArgCheckBox.addMouseListener(ma);

    }

    @Override
    public void DColCellDeletion(Database db, long colID, long cellID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void DColCellInsertion(Database newdb, long colID, long cellID) {
        db = newdb;
        verticalFrame.validate();
        updateDialogState();
    }

    @Override
    public void DColConfigChanged(Database db, long colID, boolean nameChanged, String oldName, String newName, boolean hiddenChanged, boolean oldHidden, boolean newHidden, boolean readOnlyChanged, boolean oldReadOnly, boolean newReadOnly, boolean varLenChanged, boolean oldVarLen, boolean newVarLen, boolean selectedChanged, boolean oldSelected, boolean newSelected) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void DColDeleted(Database db, long colID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Action
    public void undoChange() {
        veViews = (Vector<VocabElementV>) changes.get(--changeIndex).clone();
        verticalFrame.removeAll();
        for(VocabElementV vev: veViews){
            verticalFrame.add(vev);
            verticalFrame.validate();
        }
        updateDialogState();
    }

    @Action
    public void redoChange() {
        veViews = (Vector<VocabElementV>) changes.get(++changeIndex).clone();
        verticalFrame.removeAll();
        for(VocabElementV vev: veViews){
            verticalFrame.add(vev);
            verticalFrame.validate();
        }
        updateDialogState();
    }

    private void saveState(){
        
        int index = changeIndex+1;
        //remove all elements after current insertion so that it is most recent
        for(;index<changes.size();index++){
            changes.remove(index);
        }
        changes.add((Vector<VocabElementV>) veViews.clone());
        changeIndex++;
    }

}
