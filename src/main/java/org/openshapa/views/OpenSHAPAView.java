package org.openshapa.views;

import org.openshapa.OpenSHAPA;
import org.openshapa.OpenSHAPA.Platform;
import org.openshapa.controllers.CreateNewCellC;
import org.openshapa.controllers.NewDatabaseC;
import org.openshapa.controllers.NewVariableC;
import org.openshapa.controllers.RunScriptC;
import org.openshapa.controllers.RunTestsC;
import org.openshapa.controllers.SaveDatabaseC;
import org.openshapa.controllers.SetSheetLayoutC;
import org.openshapa.controllers.VocabEditorC;
import org.openshapa.util.FileFilters.CSVFilter;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.io.File;
import java.util.LinkedList;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.apache.log4j.Logger;
import org.openshapa.controllers.OpenDatabaseC;
import org.openshapa.db.SystemErrorException;

/**
 * This application is a simple text editor. This class displays the main frame
 * of the application and provides much of the logic. This class is called by
 * the main application class, DocumentEditorApp. For an overview of the
 * application see the comments for the DocumentEditorApp class.
 */
public final class OpenSHAPAView extends FrameView
implements KeyEventDispatcher {

    /**
     * Constructor.
     *
     * @param app The SingleFrameApplication that invoked this main FrameView.
     */
    public OpenSHAPAView(SingleFrameApplication app) {
        super(app);
        KeyboardFocusManager key = KeyboardFocusManager
                                   .getCurrentKeyboardFocusManager();
        key.addKeyEventDispatcher(this);

        // generated GUI builder code
        initComponents();

        SpreadsheetPanel panel = new SpreadsheetPanel(OpenSHAPA.getDatabase());
        this.setComponent(panel);
    }

    /**
     * Dispatches the keystroke to the correct action.
     *
     * @param evt The event that triggered this action.
     *
     * @return true if the KeyboardFocusManager should take no further action
     * with regard to the KeyEvent; false  otherwise
     */
    public boolean dispatchKeyEvent(java.awt.event.KeyEvent evt) {
        // Pass the keyevent onto the keyswitchboard so that it can route it
        // to the correct action.
        return OpenSHAPA.getApplication().dispatchKeyEvent(evt);
    }

    /**
     * Action for creating a new database.
     */
    @Action
    public void showNewDatabaseForm() {
        new NewDatabaseC();
    }

    /**
     * Action for saving the current database as a particular file.
     */
    @Action
    public void saveAs() {
        JFileChooser jd = new JFileChooser();
        jd.addChoosableFileFilter(new CSVFilter());
        int result = jd.showSaveDialog(this.getComponent());

        FileFilter ff = jd.getFileFilter();
        if (result == JFileChooser.APPROVE_OPTION) {
            new SaveDatabaseC(jd.getSelectedFile().toString(), ff);
        }
    }

    /**
     * Action for loading a database from a file.
     */
    @Action
    public void open() {
        JFileChooser jd = new JFileChooser();
        jd.addChoosableFileFilter(new CSVFilter());
        int result = jd.showOpenDialog(this.getComponent());

        if (result == JFileChooser.APPROVE_OPTION) {
            new OpenDatabaseC(jd.getSelectedFile());
        }
    }

    /**
     * Action for creating a new variable.
     */
    @Action
    public void showNewVariableForm() {
        new NewVariableC();
    }

    /**
     * Action for editing vocabs.
     */
    @Action
    public void showVocabEditor() {
        new VocabEditorC();
    }

    /**
     * Action for showing the variable list.
     */
    @Action
    public void showVariableList() {
        OpenSHAPA.getApplication().showVariableList();
    }

    /**
     * Action for showing the quicktime video controller.
     */
    @Action
    public void showQTVideoController() {
        OpenSHAPA.getApplication().showQTVideoController();
    }

    /**
     * Action for showing the spreadsheet.
     */
    @Action
    public void showSpreadsheet() {
        weakTemporalOrderMenuItem.setSelected(false);
        strongTemporalOrderMenuItem.setSelected(false);

        // Create a fresh spreadsheet component and redraw the component.
        SpreadsheetPanel panel = new SpreadsheetPanel(OpenSHAPA.getDatabase());
        this.setComponent(panel);
        this.getComponent().revalidate();
    }

    /**
     * Action for running tests.
     */
    @Action
    public void runTests() {
        new RunTestsC();
    }

    /**
     * Action for invoking a script.
     */
    @Action
    public void runScript() {
        new RunScriptC();
    }

    /**
     * Set the SheetLayoutType for the spreadsheet.
     */
    private void setSheetLayout() {
        try {
            SheetLayoutType type = SheetLayoutType.Ordinal;
            OpenSHAPA.getDatabase().setTemporalOrdering(false);

            if (weakTemporalOrderMenuItem.isSelected()) {
                type = SheetLayoutType.WeakTemporal;
                OpenSHAPA.getDatabase().setTemporalOrdering(true);
            } else if (strongTemporalOrderMenuItem.isSelected()) {
                type = SheetLayoutType.StrongTemporal;
                OpenSHAPA.getDatabase().setTemporalOrdering(true);
            }

            new SetSheetLayoutC(type);
        } catch (SystemErrorException e) {
            logger.error("Unable to perform temporal ordering", e);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem newMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator fileMenuSeparator = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        newCellMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        weakTemporalOrderMenuItem = new javax.swing.JCheckBoxMenuItem();
        strongTemporalOrderMenuItem = new javax.swing.JCheckBoxMenuItem();
        jMenu2 = new javax.swing.JMenu();
        qtControllerItem = new javax.swing.JMenuItem();
        scriptMenu = new javax.swing.JMenu();
        runScriptMenuItem = new javax.swing.JMenuItem();
        runRecentScriptMenu = new javax.swing.JMenu();
        recentScriptsHeader = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        favScripts = new javax.swing.JMenuItem();
        helpMenu1 = new javax.swing.JMenu();
        aboutMenuItem1 = new javax.swing.JMenuItem();
        contentsMenuItem = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        jLabel1.setName("jLabel1"); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(119, 119, 119)
                .add(jLabel1)
                .addContainerGap(149, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(55, 55, 55)
                .add(jLabel1)
                .addContainerGap(184, Short.MAX_VALUE))
        );
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(org.openshapa.OpenSHAPA.class).getContext().getResourceMap(OpenSHAPAView.class);
        resourceMap.injectComponents(mainPanel);

        menuBar.setName("menuBar"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(org.openshapa.OpenSHAPA.class).getContext().getActionMap(OpenSHAPAView.class, this);
        fileMenu.setAction(actionMap.get("saveAs")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        openMenuItem.setAction(actionMap.get("open")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/openshapa/views/resources/OpenSHAPAView"); // NOI18N
        openMenuItem.setText(bundle.getString("file_open.text")); // NOI18N
        openMenuItem.setName("openMenuItem"); // NOI18N
        fileMenu.add(openMenuItem);

        newMenuItem.setAction(actionMap.get("showNewDatabaseForm")); // NOI18N
        newMenuItem.setName("newMenuItem"); // NOI18N
        fileMenu.add(newMenuItem);

        saveAsMenuItem.setAction(actionMap.get("saveAs")); // NOI18N
        saveAsMenuItem.setName("saveAsMenuItem"); // NOI18N
        fileMenu.add(saveAsMenuItem);

        fileMenuSeparator.setName("fileMenuSeparator"); // NOI18N
        fileMenu.add(fileMenuSeparator);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        if (OpenSHAPA.getPlatform() != Platform.MAC) {
            fileMenu.add(exitMenuItem);
        }

        menuBar.add(fileMenu);

        jMenu3.setAction(actionMap.get("showQTVideoController")); // NOI18N
        jMenu3.setName("jMenu3"); // NOI18N

        jMenuItem2.setAction(actionMap.get("showSpreadsheet")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenu3.add(jMenuItem2);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jMenu3.add(jSeparator1);

        jMenuItem4.setAction(actionMap.get("showVocabEditor")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        jMenu3.add(jMenuItem4);

        jMenuItem1.setAction(actionMap.get("showNewVariableForm")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenu3.add(jMenuItem1);

        jMenuItem3.setAction(actionMap.get("showVariableList")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenu3.add(jMenuItem3);

        jSeparator2.setName("jSeparator2"); // NOI18N
        jMenu3.add(jSeparator2);

        newCellMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        newCellMenuItem.setName("newCellMenuItem"); // NOI18N
        newCellMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCellMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(newCellMenuItem);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jMenu3.add(jSeparator3);

        weakTemporalOrderMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        weakTemporalOrderMenuItem.setName("weakTemporalOrderMenuItem"); // NOI18N
        weakTemporalOrderMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weakTemporalMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(weakTemporalOrderMenuItem);

        strongTemporalOrderMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        strongTemporalOrderMenuItem.setName("strongTemporalOrderMenuItem"); // NOI18N
        strongTemporalOrderMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strongTemporalMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(strongTemporalOrderMenuItem);

        menuBar.add(jMenu3);

        jMenu2.setName("jMenu2"); // NOI18N

        qtControllerItem.setAction(actionMap.get("showQTVideoController")); // NOI18N
        qtControllerItem.setName("qtControllerItem"); // NOI18N
        jMenu2.add(qtControllerItem);

        menuBar.add(jMenu2);

        scriptMenu.setName("scriptMenu"); // NOI18N
        scriptMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                populateFavourites(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        runScriptMenuItem.setAction(actionMap.get("runScript")); // NOI18N
        runScriptMenuItem.setName("runScriptMenuItem"); // NOI18N
        scriptMenu.add(runScriptMenuItem);

        runRecentScriptMenu.setName("runRecentScriptMenu"); // NOI18N
        runRecentScriptMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                populateRecentScripts(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        recentScriptsHeader.setEnabled(false);
        recentScriptsHeader.setName("recentScriptsHeader"); // NOI18N
        runRecentScriptMenu.add(recentScriptsHeader);

        scriptMenu.add(runRecentScriptMenu);

        jSeparator4.setName("jSeparator4"); // NOI18N
        scriptMenu.add(jSeparator4);

        favScripts.setEnabled(false);
        favScripts.setName("favScripts"); // NOI18N
        scriptMenu.add(favScripts);

        menuBar.add(scriptMenu);

        helpMenu1.setName("helpMenu1"); // NOI18N

        aboutMenuItem1.setName("aboutMenuItem1"); // NOI18N
        if (OpenSHAPA.getPlatform() != Platform.MAC) {
            helpMenu1.add(aboutMenuItem1);
        }

        contentsMenuItem.setAction(actionMap.get("runTests")); // NOI18N
        contentsMenuItem.setName("contentsMenuItem"); // NOI18N
        helpMenu1.add(contentsMenuItem);

        menuBar.add(helpMenu1);
        resourceMap.injectComponents(menuBar);

        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The action to invoke when the user selects new cell from the menu.
     *
     * @param evt The event that fired this action.
     */
    private void newCellMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newCellMenuItemActionPerformed
        new CreateNewCellC();
}//GEN-LAST:event_newCellMenuItemActionPerformed

    /**
     * The action to invoke when the user selects 'strong temporal ordering'.
     *
     * @param evt The event that fired this action.
     */
    private void strongTemporalMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strongTemporalMenuItemActionPerformed
        weakTemporalOrderMenuItem.setSelected(false);
        setSheetLayout();
}//GEN-LAST:event_strongTemporalMenuItemActionPerformed

    /**
     * The action to invoke when the user selects 'weak temporal ordering'.
     *
     * @param evt The event that fired this action.
     */
    private void weakTemporalMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weakTemporalMenuItemActionPerformed
        strongTemporalOrderMenuItem.setSelected(false);
        setSheetLayout();
}//GEN-LAST:event_weakTemporalMenuItemActionPerformed

    /**
     * The action to invoke when the user selects 'recent scripts' from the
     * scripting menu.
     *
     * @param evt The event that fired this action.
     */
    private void populateRecentScripts(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_populateRecentScripts
        // Flush the menu - excluding the top menu item.
        int size = runRecentScriptMenu.getMenuComponentCount();
        for (int i = 1; i < size; i++) {
            runRecentScriptMenu.remove(i);
        }

        LinkedList<File> lastScripts = OpenSHAPA.getLastScriptsExecuted();
        for (File f : lastScripts) {
            runRecentScriptMenu.add(createScriptMenuItemFromFile(f));
        }
}//GEN-LAST:event_populateRecentScripts

    /** The directory holding a users favourite scripts. */
    static final String FAV_DIR = "favourites";

    /**
     * The action to invoke when the user selects 'scripts' from the main menu.
     *
     * @param evt The event that fired this action.
     */
    private void populateFavourites(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_populateFavourites
        // Favourite script list starts after the 'favScripts' menu item - which
        // is just a stub for a starting point. Search for the favScripts as the
        // starting point for deleting existing scripts from the menu.
        Component list[] = scriptMenu.getMenuComponents();
        int start = 0;
        for (Component c : list) {
            start++;
            if (c.getName().equals("favScripts")) {
                break;
            }
        }

        // Delete every menu item from 'favScripts' down to the end of the list.
        // Favscripts are
        int size = scriptMenu.getMenuComponentCount();
        for (int i = start; i < size; i++) {
            scriptMenu.remove(i);
        }

        // Get list of favourite scripts from the favourites folder.
        File favouritesDir = new File(FAV_DIR);
        String[] children = favouritesDir.list();
        if (children != null) {
            for (String s : children) {
                File f = new File(FAV_DIR + File.separatorChar + s);
                scriptMenu.add(createScriptMenuItemFromFile(f));
            }
        }
    }//GEN-LAST:event_populateFavourites

    /**
     * Creates a new menu item for running a named script.
     *
     * @param text The text to display for the menu item for the supplied
     * script.
     * @param f The file to run when menu item is selected.
     * @return The jmenuitem that can be added to a menu.
     */
    public JMenuItem createScriptMenuItemFromFile(final File f) {
        JMenuItem menuItem = new JMenuItem();
        menuItem.setText(f.toString());
        menuItem.setName(f.toString());
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runRecentScript(evt);
            }
        });

        return menuItem;
    }

    /**
     * The action to invoke when the user selects a recent script to run.
     *
     * @param evt The event that triggered this action.
     */
    private void runRecentScript(java.awt.event.ActionEvent evt) {
        new RunScriptC(evt.getActionCommand());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem1;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem favScripts;
    private javax.swing.JMenu helpMenu1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newCellMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem qtControllerItem;
    private javax.swing.JMenuItem recentScriptsHeader;
    private javax.swing.JMenu runRecentScriptMenu;
    private javax.swing.JMenuItem runScriptMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenu scriptMenu;
    private javax.swing.JCheckBoxMenuItem strongTemporalOrderMenuItem;
    private javax.swing.JCheckBoxMenuItem weakTemporalOrderMenuItem;
    // End of variables declaration//GEN-END:variables

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(OpenSHAPAView.class);
}
