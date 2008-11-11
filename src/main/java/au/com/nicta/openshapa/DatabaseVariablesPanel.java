/*
 * DatabaseVariablesPanel.java
 *
 * Created on February 17, 2008, 3:08 PM
 */
package au.com.nicta.openshapa;
 
import au.com.nicta.openshapa.db.Column;
import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.MatrixVocabElement;
import au.com.nicta.openshapa.db.ODBCDatabase;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.util.ConfigurationObject;
import au.com.nicta.openshapa.util.UIConfiguration;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author  Felix
 */
public class DatabaseVariablesPanel
        extends javax.swing.JPanel
{
  protected Executive exec   = null;
  protected JDialog   dialog = new JDialog();

  public static UIConfiguration uiconfig = new UIConfiguration();
  
  /** Creates new form DatabaseVariablesPanel */
  public DatabaseVariablesPanel(Executive exec)
          throws SystemErrorException
  {
    this.exec = exec;
    this.setBackground(uiconfig.dialogBackgroundColor);
    this.setForeground(this.uiconfig.dialogForegroundColor);
    this.setFont(this.uiconfig.dialogFont);
    initComponents();

    this.setupDatabases();

    ConfigurationObject[] co =
        this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.title");
    if (co != null) {
      this.variablesLabel.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.dbTitle");
    if (co != null) {
      this.databaseLabel.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.selectADatabase");
    if (co != null) {
      ((DefaultComboBoxModel)this.databaseComboBox.getModel()).removeElementAt(0);
      ((DefaultComboBoxModel)this.databaseComboBox.getModel()).insertElementAt(co[0].getValue(), 0);
      this.databaseComboBox.setSelectedIndex(0);
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.selectAVariable");
    if (co != null) {
      ((DefaultComboBoxModel)this.insertComboBox.getModel()).removeElementAt(0);
      ((DefaultComboBoxModel)this.insertComboBox.getModel()).insertElementAt(co[0].getValue(), 0);
      ((DefaultComboBoxModel)this.borrowComboBox.getModel()).removeElementAt(0);
      ((DefaultComboBoxModel)this.borrowComboBox.getModel()).insertElementAt(co[0].getValue(), 0);
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.variableName");
    if (co != null) {
      this.nameLabel.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.insertAfter");
    if (co != null) {
      this.insertLabel.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.borrowTimesFrom");
    if (co != null) {
      this.borrowLabel.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.type");
    if (co != null) {
      this.bottomLeftVariablesPanel.setBorder(BorderFactory.createTitledBorder(co[0].getValue()));
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.general.types.text");
    if (co != null) {
      this.textTypeRadioButton.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.general.types.predicate");
    if (co != null) {
      this.predicatTypeRadioButton.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.general.types.nominal");
    if (co != null) {
      this.nominalTypeRadioButton.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.general.types.integer");
    if (co != null) {
      this.integerTypeRadioButton.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.general.types.float");
    if (co != null) {
      this.floatTypeRadioButton.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.general.types.matrix");
    if (co != null) {
      this.matrixTypeRadioButton.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.general.types.timestamp");
    if (co != null) {
      this.timestampTypeRadioButton.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.warn");
    if (co != null) {
      this.warnCheckBox.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.deleteAllCells");
    if (co != null) {
      this.deleteAllCellsButton.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.deleteCells");
    if (co != null) {
      this.deleteCellsButton.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.deleteVariable");
    if (co != null) {
      this.deleteVariableButton.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.general.dialog.ok");
    if (co != null) {
      this.okButton.setText(co[0].getValue());
    }

    co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.general.dialog.cancel");
    if (co != null) {
      this.cancelButton.setText(co[0].getValue());
    }

    this.borrowComboBox.setBackground(this.uiconfig.dialogBackgroundColor);
    this.borrowComboBox.setForeground(this.uiconfig.dialogForegroundColor);
    this.borrowComboBox.setFont(this.uiconfig.dialogFont);
    this.borrowLabel.setBackground(this.uiconfig.dialogBackgroundColor);
    this.borrowLabel.setForeground(this.uiconfig.dialogForegroundColor);
    this.borrowLabel.setFont(this.uiconfig.dialogFont);
    this.bottomLeftVariablesPanel.setBackground(this.uiconfig.dialogBackgroundColor);
    this.bottomLeftVariablesPanel.setForeground(this.uiconfig.dialogForegroundColor);
    this.bottomLeftVariablesPanel.setFont(this.uiconfig.dialogFont);
    this.bottomRightButtonPanel.setBackground(this.uiconfig.dialogBackgroundColor);
    this.bottomRightButtonPanel.setForeground(this.uiconfig.dialogForegroundColor);
    this.bottomRightButtonPanel.setFont(this.uiconfig.dialogFont);
    this.cancelButton.setBackground(this.uiconfig.dialogBackgroundColor);
    this.cancelButton.setForeground(this.uiconfig.dialogForegroundColor);
    this.cancelButton.setFont(this.uiconfig.dialogFont);
    this.databaseComboBox.setBackground(this.uiconfig.dialogBackgroundColor);
    this.databaseComboBox.setForeground(this.uiconfig.dialogForegroundColor);
    this.databaseComboBox.setFont(this.uiconfig.dialogFont);
    this.databaseLabel.setBackground(this.uiconfig.dialogBackgroundColor);
    this.databaseLabel.setForeground(this.uiconfig.dialogForegroundColor);
    this.databaseLabel.setFont(this.uiconfig.dialogFont);
    this.databasePanel.setBackground(this.uiconfig.dialogBackgroundColor);
    this.databasePanel.setForeground(this.uiconfig.dialogForegroundColor);
    this.databasePanel.setFont(this.uiconfig.dialogFont);
    this.deleteAllCellsButton.setBackground(this.uiconfig.dialogBackgroundColor);
    this.deleteAllCellsButton.setForeground(this.uiconfig.dialogForegroundColor);
    this.deleteAllCellsButton.setFont(this.uiconfig.dialogFont);
    this.deleteCellsButton.setBackground(this.uiconfig.dialogBackgroundColor);
    this.deleteCellsButton.setForeground(this.uiconfig.dialogForegroundColor);
    this.deleteCellsButton.setFont(this.uiconfig.dialogFont);
    this.deleteVariableButton.setBackground(this.uiconfig.dialogBackgroundColor);
    this.deleteVariableButton.setForeground(this.uiconfig.dialogForegroundColor);
    this.deleteVariableButton.setFont(this.uiconfig.dialogFont);
    this.floatTypeRadioButton.setBackground(this.uiconfig.dialogBackgroundColor);
    this.floatTypeRadioButton.setForeground(this.uiconfig.dialogForegroundColor);
    this.floatTypeRadioButton.setFont(this.uiconfig.dialogFont);
    this.insertComboBox.setBackground(this.uiconfig.dialogBackgroundColor);
    this.insertComboBox.setForeground(this.uiconfig.dialogForegroundColor);
    this.insertComboBox.setFont(this.uiconfig.dialogFont);
    this.insertLabel.setBackground(this.uiconfig.dialogBackgroundColor);
    this.insertLabel.setForeground(this.uiconfig.dialogForegroundColor);
    this.insertLabel.setFont(this.uiconfig.dialogFont);
    this.integerTypeRadioButton.setBackground(this.uiconfig.dialogBackgroundColor);
    this.integerTypeRadioButton.setForeground(this.uiconfig.dialogForegroundColor);
    this.integerTypeRadioButton.setFont(this.uiconfig.dialogFont);
    this.leftVariablesPanel.setBackground(this.uiconfig.dialogBackgroundColor);
    this.leftVariablesPanel.setForeground(this.uiconfig.dialogForegroundColor);
    this.leftVariablesPanel.setFont(this.uiconfig.dialogFont);
    this.matrixTypeRadioButton.setBackground(this.uiconfig.dialogBackgroundColor);
    this.matrixTypeRadioButton.setForeground(this.uiconfig.dialogForegroundColor);
    this.matrixTypeRadioButton.setFont(this.uiconfig.dialogFont);
    this.nameLabel.setBackground(this.uiconfig.dialogBackgroundColor);
    this.nameLabel.setForeground(this.uiconfig.dialogForegroundColor);
    this.nameLabel.setFont(this.uiconfig.dialogFont);
    this.nameTextField.setBackground(this.uiconfig.dialogBackgroundColor);
    this.nameTextField.setForeground(this.uiconfig.dialogForegroundColor);
    this.nameTextField.setFont(this.uiconfig.dialogFont);
    this.nominalTypeRadioButton.setBackground(this.uiconfig.dialogBackgroundColor);
    this.nominalTypeRadioButton.setForeground(this.uiconfig.dialogForegroundColor);
    this.nominalTypeRadioButton.setFont(this.uiconfig.dialogFont);
    this.okButton.setBackground(this.uiconfig.dialogBackgroundColor);
    this.okButton.setForeground(this.uiconfig.dialogForegroundColor);
    this.okButton.setFont(this.uiconfig.dialogFont);
    this.predicatTypeRadioButton.setBackground(this.uiconfig.dialogBackgroundColor);
    this.predicatTypeRadioButton.setForeground(this.uiconfig.dialogForegroundColor);
    this.predicatTypeRadioButton.setFont(this.uiconfig.dialogFont);
    this.rightVariablePanel.setBackground(this.uiconfig.dialogBackgroundColor);
    this.rightVariablePanel.setForeground(this.uiconfig.dialogForegroundColor);
    this.rightVariablePanel.setFont(this.uiconfig.dialogFont);
    this.textTypeRadioButton.setBackground(this.uiconfig.dialogBackgroundColor);
    this.textTypeRadioButton.setForeground(this.uiconfig.dialogForegroundColor);
    this.textTypeRadioButton.setFont(this.uiconfig.dialogFont);
    this.timestampTypeRadioButton.setBackground(this.uiconfig.dialogBackgroundColor);
    this.timestampTypeRadioButton.setForeground(this.uiconfig.dialogForegroundColor);
    this.timestampTypeRadioButton.setFont(this.uiconfig.dialogFont);
    this.topLeftVariablesPanel.setBackground(this.uiconfig.dialogBackgroundColor);
    this.topLeftVariablesPanel.setForeground(this.uiconfig.dialogForegroundColor);
    this.topLeftVariablesPanel.setFont(this.uiconfig.dialogFont);
    this.topRightButtonPanel.setBackground(this.uiconfig.dialogBackgroundColor);
    this.topRightButtonPanel.setForeground(this.uiconfig.dialogForegroundColor);
    this.topRightButtonPanel.setFont(this.uiconfig.dialogFont);
    this.variablesLabel.setBackground(this.uiconfig.dialogBackgroundColor);
    this.variablesLabel.setForeground(this.uiconfig.dialogForegroundColor);
    this.variablesLabel.setFont(this.uiconfig.dialogFont);
    this.variablesPanel.setBackground(this.uiconfig.dialogBackgroundColor);
    this.variablesPanel.setForeground(this.uiconfig.dialogForegroundColor);
    this.variablesPanel.setFont(this.uiconfig.dialogFont);
    this.warnCheckBox.setBackground(this.uiconfig.dialogBackgroundColor);
    this.warnCheckBox.setForeground(this.uiconfig.dialogForegroundColor);
    this.warnCheckBox.setFont(this.uiconfig.dialogFont);

  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    typesButtonGroup = new javax.swing.ButtonGroup();
    databasePanel = new javax.swing.JPanel();
    databaseComboBox = new javax.swing.JComboBox();
    variablesLabel = new javax.swing.JLabel();
    databaseLabel = new javax.swing.JLabel();
    variablesPanel = new javax.swing.JPanel();
    leftVariablesPanel = new javax.swing.JPanel();
    topLeftVariablesPanel = new javax.swing.JPanel();
    nameLabel = new javax.swing.JLabel();
    nameTextField = new javax.swing.JTextField();
    insertLabel = new javax.swing.JLabel();
    insertComboBox = new javax.swing.JComboBox();
    borrowLabel = new javax.swing.JLabel();
    borrowComboBox = new javax.swing.JComboBox();
    bottomLeftVariablesPanel = new javax.swing.JPanel();
    textTypeRadioButton = new javax.swing.JRadioButton();
    predicatTypeRadioButton = new javax.swing.JRadioButton();
    nominalTypeRadioButton = new javax.swing.JRadioButton();
    timestampTypeRadioButton = new javax.swing.JRadioButton();
    integerTypeRadioButton = new javax.swing.JRadioButton();
    floatTypeRadioButton = new javax.swing.JRadioButton();
    matrixTypeRadioButton = new javax.swing.JRadioButton();
    rightVariablePanel = new javax.swing.JPanel();
    topRightButtonPanel = new javax.swing.JPanel();
    deleteAllCellsButton = new javax.swing.JButton();
    deleteCellsButton = new javax.swing.JButton();
    deleteVariableButton = new javax.swing.JButton();
    warnCheckBox = new javax.swing.JCheckBox();
    bottomRightButtonPanel = new javax.swing.JPanel();
    cancelButton = new javax.swing.JButton();
    okButton = new javax.swing.JButton();

    setBackground(java.awt.Color.white);
    setLayout(new java.awt.BorderLayout(10, 5));

    databasePanel.setBackground(java.awt.Color.white);
    databasePanel.setLayout(new java.awt.BorderLayout(5, 5));

    databaseComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Select a Database --" }));
    databaseComboBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        databaseComboBoxActionPerformed(evt);
      }
    });
    databasePanel.add(databaseComboBox, java.awt.BorderLayout.EAST);

    variablesLabel.setText("Variables");
    databasePanel.add(variablesLabel, java.awt.BorderLayout.WEST);

    databaseLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    databaseLabel.setText("Database:");
    databaseLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    databasePanel.add(databaseLabel, java.awt.BorderLayout.CENTER);

    add(databasePanel, java.awt.BorderLayout.PAGE_START);

    variablesPanel.setBackground(java.awt.Color.white);
    variablesPanel.setLayout(new java.awt.BorderLayout(15, 5));

    leftVariablesPanel.setLayout(new java.awt.BorderLayout());

    topLeftVariablesPanel.setBackground(java.awt.Color.white);
    topLeftVariablesPanel.setLayout(new java.awt.GridLayout(3, 2, 10, 5));

    nameLabel.setBackground(java.awt.Color.white);
    nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    nameLabel.setText("Variable Name:");
    topLeftVariablesPanel.add(nameLabel);
    topLeftVariablesPanel.add(nameTextField);

    insertLabel.setBackground(java.awt.Color.white);
    insertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    insertLabel.setText("Insert after:");
    topLeftVariablesPanel.add(insertLabel);

    insertComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Select a variable --" }));
    topLeftVariablesPanel.add(insertComboBox);

    borrowLabel.setBackground(java.awt.Color.white);
    borrowLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    borrowLabel.setText("Borrow times from:");
    topLeftVariablesPanel.add(borrowLabel);

    borrowComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Select a variable --" }));
    topLeftVariablesPanel.add(borrowComboBox);

    leftVariablesPanel.add(topLeftVariablesPanel, java.awt.BorderLayout.CENTER);

    bottomLeftVariablesPanel.setBackground(java.awt.Color.white);
    bottomLeftVariablesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Type"));
    bottomLeftVariablesPanel.setLayout(new java.awt.GridLayout(2, 0, 1, 1));

    textTypeRadioButton.setBackground(java.awt.Color.white);
    typesButtonGroup.add(textTypeRadioButton);
    textTypeRadioButton.setText("Text");
    bottomLeftVariablesPanel.add(textTypeRadioButton);

    predicatTypeRadioButton.setBackground(java.awt.Color.white);
    typesButtonGroup.add(predicatTypeRadioButton);
    predicatTypeRadioButton.setText("Predicate");
    bottomLeftVariablesPanel.add(predicatTypeRadioButton);

    nominalTypeRadioButton.setBackground(java.awt.Color.white);
    typesButtonGroup.add(nominalTypeRadioButton);
    nominalTypeRadioButton.setText("Nominal");
    bottomLeftVariablesPanel.add(nominalTypeRadioButton);

    timestampTypeRadioButton.setBackground(java.awt.Color.white);
    typesButtonGroup.add(timestampTypeRadioButton);
    timestampTypeRadioButton.setText("Timestamp");
    bottomLeftVariablesPanel.add(timestampTypeRadioButton);

    integerTypeRadioButton.setBackground(java.awt.Color.white);
    typesButtonGroup.add(integerTypeRadioButton);
    integerTypeRadioButton.setText("Integer");
    bottomLeftVariablesPanel.add(integerTypeRadioButton);

    floatTypeRadioButton.setBackground(java.awt.Color.white);
    typesButtonGroup.add(floatTypeRadioButton);
    floatTypeRadioButton.setText("Float");
    bottomLeftVariablesPanel.add(floatTypeRadioButton);

    matrixTypeRadioButton.setBackground(java.awt.Color.white);
    typesButtonGroup.add(matrixTypeRadioButton);
    matrixTypeRadioButton.setText("Matrix");
    bottomLeftVariablesPanel.add(matrixTypeRadioButton);

    leftVariablesPanel.add(bottomLeftVariablesPanel, java.awt.BorderLayout.SOUTH);

    variablesPanel.add(leftVariablesPanel, java.awt.BorderLayout.CENTER);

    rightVariablePanel.setBackground(java.awt.Color.white);
    rightVariablePanel.setLayout(new java.awt.BorderLayout(0, 15));

    topRightButtonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    topRightButtonPanel.setLayout(new java.awt.GridLayout(4, 1, 2, 2));

    deleteAllCellsButton.setBackground(java.awt.Color.white);
    deleteAllCellsButton.setText("Delete All Cells");
    deleteAllCellsButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteAllCellsButtonActionPerformed(evt);
      }
    });
    topRightButtonPanel.add(deleteAllCellsButton);

    deleteCellsButton.setBackground(java.awt.Color.white);
    deleteCellsButton.setText("Delete Cells");
    deleteCellsButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteCellsButtonActionPerformed(evt);
      }
    });
    topRightButtonPanel.add(deleteCellsButton);

    deleteVariableButton.setBackground(java.awt.Color.white);
    deleteVariableButton.setText("Delete Variable");
    deleteVariableButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteVariableButtonActionPerformed(evt);
      }
    });
    topRightButtonPanel.add(deleteVariableButton);

    warnCheckBox.setBackground(java.awt.Color.white);
    warnCheckBox.setSelected(true);
    warnCheckBox.setText("Warn");
    warnCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    topRightButtonPanel.add(warnCheckBox);

    rightVariablePanel.add(topRightButtonPanel, java.awt.BorderLayout.CENTER);

    bottomRightButtonPanel.setLayout(new java.awt.GridLayout(2, 1, 0, 5));

    cancelButton.setBackground(java.awt.Color.white);
    cancelButton.setText("CANCEL");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelButtonActionPerformed(evt);
      }
    });
    bottomRightButtonPanel.add(cancelButton);

    okButton.setBackground(java.awt.Color.white);
    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        okButtonActionPerformed(evt);
      }
    });
    bottomRightButtonPanel.add(okButton);

    rightVariablePanel.add(bottomRightButtonPanel, java.awt.BorderLayout.SOUTH);

    variablesPanel.add(rightVariablePanel, java.awt.BorderLayout.EAST);

    add(variablesPanel, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

  protected void setupDatabases()
          throws SystemErrorException
  {
    this.databaseComboBox.removeAllItems();

    ConfigurationObject[] co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.selectADatabase");
    if (co != null) {
      ((DefaultComboBoxModel)this.databaseComboBox.getModel()).insertElementAt(co[0].getValue(), 0);
    }

    Vector databases = exec.getOpenDatabases();
    if (databases != null) {
      if (databases.size() <= 0) {
        throw (new SystemErrorException("No open databases!"));
      }
      for (int i=0; i<databases.size(); i++) {
        this.databaseComboBox.addItem(databases.elementAt(i));
      }
      if (this.exec.getFocusedDatabase() != null) {
        Database db = this.exec.getFocusedDatabase();
        this.databaseComboBox.getModel().setSelectedItem(db);
      }
    } else {
      throw (new SystemErrorException("No open databases!"));
    }
  }

  private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    try {
      Database db = (Database)this.databaseComboBox.getSelectedItem();
      if (db.colNameInUse(this.nameTextField.getText().trim())) {
        JOptionPane.showMessageDialog(this, "Variable already exists!");
        return;
      }

      MatrixVocabElement.matrixType type = MatrixVocabElement.matrixType.UNDEFINED;
      if (this.textTypeRadioButton.isSelected()) {
        type = MatrixVocabElement.matrixType.TEXT;
      } else if (this.nominalTypeRadioButton.isSelected()) {
        type = MatrixVocabElement.matrixType.NOMINAL;
      } else if (this.predicatTypeRadioButton.isSelected()) {
        type = MatrixVocabElement.matrixType.PREDICATE;
      } else if (this.timestampTypeRadioButton.isSelected()) {
//        type = MatrixVocabElement.matrixType.TIMESTAMP;
      } else if (this.integerTypeRadioButton.isSelected()) {
        type = MatrixVocabElement.matrixType.INTEGER;
      } else if (this.floatTypeRadioButton.isSelected()) {
        type = MatrixVocabElement.matrixType.FLOAT;
      } else if (this.matrixTypeRadioButton.isSelected()) {
        type = MatrixVocabElement.matrixType.MATRIX;
      }
      
      if (type == MatrixVocabElement.matrixType.UNDEFINED) {
        JOptionPane.showMessageDialog(this, "Please select a variable type.");
        return;
      }

      DataColumn dc = new DataColumn(db,this.nameTextField.getText(), type);
      db.addColumn(dc);

      this.databaseComboBox.setSelectedIndex(1);
    } catch (SystemErrorException see) {
      JOptionPane.showMessageDialog(this, see, "Exception adding variable.", JOptionPane.ERROR_MESSAGE);
    }

    this.dialog.setVisible(false);
  }//GEN-LAST:event_okButtonActionPerformed

  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    // TODO add your handling code here:
    this.nameTextField.setText("");
    this.insertComboBox.removeAllItems();
    this.borrowComboBox.removeAllItems();
    this.databaseComboBox.setSelectedIndex(0);
    this.dialog.setVisible(false);
  }//GEN-LAST:event_cancelButtonActionPerformed

  private void deleteVariableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteVariableButtonActionPerformed
    // TODO add your handling code here:
    JOptionPane.showMessageDialog(this, "Not Implemented!");
  }//GEN-LAST:event_deleteVariableButtonActionPerformed

  private void deleteCellsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCellsButtonActionPerformed
    // TODO add your handling code here:
    JOptionPane.showMessageDialog(this, "Not Implemented!");//GEN-LAST:event_deleteCellsButtonActionPerformed
  }

  private void deleteAllCellsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllCellsButtonActionPerformed
    // TODO add your handling code here:
    JOptionPane.showMessageDialog(this, "Not Implemented!");
  }//GEN-LAST:event_deleteAllCellsButtonActionPerformed

  private void databaseComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_databaseComboBoxActionPerformed
    // TODO add your handling code here:

    this.insertComboBox.removeAllItems();
    this.borrowComboBox.removeAllItems();

    ConfigurationObject[] co = this.exec.getLangConfiguration().getElements("OpenSHAPALang.executive.variablesDialog.selectAVariable");
    if (co != null) {
      ((DefaultComboBoxModel)this.insertComboBox.getModel()).insertElementAt(co[0].getValue(), 0);
      this.insertComboBox.setSelectedIndex(0);
      ((DefaultComboBoxModel)this.borrowComboBox.getModel()).insertElementAt(co[0].getValue(), 0);
      this.borrowComboBox.setSelectedIndex(0);
    }
    
    if (this.databaseComboBox.getSelectedIndex() <= 0) {
      return;
    }

    Database db = (Database)this.databaseComboBox.getSelectedItem();
    try {
      Vector<Column> columns = db.getColumns();
      if (columns != null) {
        for (int i=0; i<columns.size(); i++) {
          this.insertComboBox.addItem(columns.elementAt(i));
          this.borrowComboBox.addItem(columns.elementAt(i));
        }
      }
    } catch (SystemErrorException see) {
      
    }
  }//GEN-LAST:event_databaseComboBoxActionPerformed

  public void showDialog()
          throws SystemErrorException
  {
    this.nameTextField.setText("");
    this.insertComboBox.removeAllItems();
    this.borrowComboBox.removeAllItems();
    this.setupDatabases();

    this.dialog.setSize(new Dimension(475, 200));
    this.dialog.getContentPane().setLayout(new BorderLayout());
    this.dialog.getContentPane().add(this, BorderLayout.CENTER);
    this.dialog.setVisible(true);
  }

  /*
  public final static void main(String[] argv)
  {
    try {
      Executive exec = new Executive();
      ODBCDatabase db = new ODBCDatabase();
      exec.openDatabases.add(db);
      exec.selectDatabase(db);
      DatabaseVariablesPanel dvp = new DatabaseVariablesPanel(exec);
      dvp.showDialog();
    } catch (Exception e) {
      System.err.println(e);
      e.printStackTrace();
    }
  }
   */
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox borrowComboBox;
  private javax.swing.JLabel borrowLabel;
  private javax.swing.JPanel bottomLeftVariablesPanel;
  private javax.swing.JPanel bottomRightButtonPanel;
  private javax.swing.JButton cancelButton;
  private javax.swing.JComboBox databaseComboBox;
  private javax.swing.JLabel databaseLabel;
  private javax.swing.JPanel databasePanel;
  private javax.swing.JButton deleteAllCellsButton;
  private javax.swing.JButton deleteCellsButton;
  private javax.swing.JButton deleteVariableButton;
  private javax.swing.JRadioButton floatTypeRadioButton;
  private javax.swing.JComboBox insertComboBox;
  private javax.swing.JLabel insertLabel;
  private javax.swing.JRadioButton integerTypeRadioButton;
  private javax.swing.JPanel leftVariablesPanel;
  private javax.swing.JRadioButton matrixTypeRadioButton;
  private javax.swing.JLabel nameLabel;
  private javax.swing.JTextField nameTextField;
  private javax.swing.JRadioButton nominalTypeRadioButton;
  private javax.swing.JButton okButton;
  private javax.swing.JRadioButton predicatTypeRadioButton;
  private javax.swing.JPanel rightVariablePanel;
  private javax.swing.JRadioButton textTypeRadioButton;
  private javax.swing.JRadioButton timestampTypeRadioButton;
  private javax.swing.JPanel topLeftVariablesPanel;
  private javax.swing.JPanel topRightButtonPanel;
  private javax.swing.ButtonGroup typesButtonGroup;
  private javax.swing.JLabel variablesLabel;
  private javax.swing.JPanel variablesPanel;
  private javax.swing.JCheckBox warnCheckBox;
  // End of variables declaration//GEN-END:variables
  
}
