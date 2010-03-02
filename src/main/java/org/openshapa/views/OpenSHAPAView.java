package org.openshapa.views;

import java.awt.Component;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.openshapa.Configuration;
import org.openshapa.OpenSHAPA;
import org.openshapa.OpenSHAPA.Platform;
import org.openshapa.controllers.CreateNewCellC;
import org.openshapa.controllers.DeleteCellC;
import org.openshapa.controllers.DeleteColumnC;
import org.openshapa.controllers.NewDatabaseC;
import org.openshapa.controllers.NewProjectC;
import org.openshapa.controllers.NewVariableC;
import org.openshapa.controllers.OpenDatabaseC;
import org.openshapa.controllers.OpenProjectC;
import org.openshapa.controllers.RunScriptC;
import org.openshapa.controllers.SaveC;
import org.openshapa.controllers.SetSheetLayoutC;
import org.openshapa.controllers.VocabEditorC;
import org.openshapa.models.db.MacshapaDatabase;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.project.Project;
import org.openshapa.models.project.ViewerSetting;
import org.openshapa.util.ArrayDirection;
import org.openshapa.util.Constants;
import org.openshapa.util.FileFilters.CSVFilter;
import org.openshapa.util.FileFilters.MODBFilter;
import org.openshapa.util.FileFilters.SHAPAFilter;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.Plugin;
import org.openshapa.views.continuous.PluginManager;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;

import com.usermetrix.jclient.UserMetrix;

/**
 * The main FrameView, representing the interface for OpenSHAPA the user will
 * initially see.
 */
public final class OpenSHAPAView extends FrameView {

    /**
     * Constructor.
     * 
     * @param app
     *            The SingleFrameApplication that invoked this main FrameView.
     */
    public OpenSHAPAView(final SingleFrameApplication app) {
        super(app);
        KeyboardFocusManager manager =
                KeyboardFocusManager.getCurrentKeyboardFocusManager();

        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
            /**
             * Dispatches the keystroke to the correct action.
             * 
             * @param evt
             *            The event that triggered this action.
             * @return true if the KeyboardFocusManager should take no further
             *         action with regard to the KeyEvent; false otherwise.
             */
            public boolean dispatchKeyEvent(final KeyEvent evt) {
                // Pass the keyevent onto the keyswitchboard so that it can
                // route it to the correct action.
                spreadsheetMenuMenuSelected(null);
                return OpenSHAPA.getApplication().dispatchKeyEvent(evt);
            }
        });

        // generated GUI builder code
        initComponents();

        // BugzID:492 - Set the shortcut for new cell, so a keystroke that won't
        // get confused for the "carriage return". The shortcut for new cells
        // is handled in OpenSHAPA.java
        newCellMenuItem.setAccelerator(KeyStroke.getKeyStroke('\u21A9'));

        // BugzID:521 + 468 - Define accelerator keys based on Operating system.
        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        weakTemporalOrderMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_T, keyMask));

        strongTemporalOrderMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_T, InputEvent.SHIFT_MASK | keyMask));

        // This just sets the visual appearance of the accelerator - the actual
        // short cut is handled in org.openshapa.OpenSHAPA.dispatchKeyEvent.
        // Set zoom in to keyMask + '+'
        zoomInMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS,
                keyMask));

        // Set zoom out to keyMask + '-'
        zoomOutMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_MINUS, keyMask));

        // Set reset zoom to keyMask + '0'
        resetZoomMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0,
                keyMask));

        // Set the save accelerator to keyMask + 'S'
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                keyMask));

        // Set the open accelerator to keyMask + 'o';
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                keyMask));

        // Set the new accelerator to keyMask + 'N';
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                keyMask));

        // Set the new accelerator to keyMask + 'L';
        newCellLeftMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, keyMask));

        // Set the new accelerator to keyMask + 'R';
        newCellRightMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, keyMask));

        panel = new SpreadsheetPanel(OpenSHAPA.getProject().getDB());
        setComponent(panel);

    }

    /**
     * Update the title of the application.
     */
    public void updateTitle() {
        // BugzID:449 - Update the name of the window to include the default
        // name of the database.

        // Show the project name instead of database.
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        ResourceMap rMap =
                OpenSHAPA.getApplication().getContext().getResourceMap(
                        OpenSHAPA.class);
        String postFix = "";
        Project project = OpenSHAPA.getProject();

        if (project.isChanged() || project.isNewProject()) {
            postFix = "*";
        }

        String extension = "";
        SaveC saveController = SaveC.getInstance();
        final FileFilter lastSaveOption = saveController.getLastSaveOption();
        if (lastSaveOption instanceof SHAPAFilter) {
            extension = ".shapa";
        } else if (lastSaveOption instanceof CSVFilter) {
            extension = ".csv";
        } else if (lastSaveOption instanceof MODBFilter) {
            extension = ".odb";
        }

        String projectName = project.getProjectName();
        if (projectName != null) {
            mainFrame.setTitle(rMap.getString("Application.title") + " - "
                    + projectName + extension + postFix);
        } else {
            mainFrame.setTitle(rMap.getString("Application.title") + " - "
                    + "Project1" + extension + postFix);
        }
    }

    /**
     * Action for creating a new database.
     */
    @Action
    public void showNewDatabaseForm() {
        if (OpenSHAPA.getApplication().safeQuit()) {
            new NewDatabaseC();
        }
    }

    /**
     * Action for creating a new project.
     */
    @Action
    public void showNewProjectForm() {
        if (OpenSHAPA.getApplication().safeQuit()) {
            new NewProjectC();
        }
    }

    /**
     * Action for saving the current database as a file.
     */
    @Action
    public void save() {
        // If the user has not saved before - invoke the saveAs() controller to
        // force the user to nominate a destination file.
        Project openShapaProject = OpenSHAPA.getProject();
        if (openShapaProject.isNewProject()
                || openShapaProject.getProjectName() == null) {
            saveAs();
        } else if (openShapaProject.getDatabaseFile() == null) {
            saveAs();
        } else {
            SaveC.getInstance().save();
        }
    }

    private void save(final OpenSHAPAFileChooser fc) {
        SaveC saveC = SaveC.getInstance();

        FileFilter filter = fc.getFileFilter();
        // Save as a project
        if (filter instanceof SHAPAFilter) {
            // Build the project file name
            String projectFileName = fc.getSelectedFile().getName();
            if (!projectFileName.endsWith(".shapa")) {
                projectFileName = projectFileName.concat(".shapa");
            }
            // Send it off to the controller
            saveC.saveAsProject(fc.getSelectedFile().getParent(),
                    projectFileName);
            // Save as a CSV database
        } else if (filter instanceof CSVFilter) {
            String databaseFileName = fc.getSelectedFile().getName();
            if (!databaseFileName.endsWith(".csv")) {
                databaseFileName = databaseFileName.concat(".csv");
            }
            saveC.saveAsDatabase(fc.getSelectedFile().getParent(),
                    databaseFileName, filter);
            // Save as a ODB database
        } else if (filter instanceof MODBFilter) {
            String databaseFileName = fc.getSelectedFile().getName();
            if (!databaseFileName.endsWith(".odb")) {
                databaseFileName = databaseFileName.concat(".odb");
            }
            saveC.saveAsDatabase(fc.getSelectedFile().getParent(),
                    databaseFileName, filter);
        }
    }

    /**
     * Action for saving the current project as a particular file.
     */
    @Action
    public void saveAs() {
        OpenSHAPAFileChooser jd = new OpenSHAPAFileChooser();

        jd.addChoosableFileFilter(new MODBFilter());
        jd.addChoosableFileFilter(new CSVFilter());
        jd.addChoosableFileFilter(new SHAPAFilter());

        int result = jd.showSaveDialog(getComponent());

        if (result == JFileChooser.APPROVE_OPTION) {
            save(jd);
        }
    }

    /**
     * Action for loading an OpenSHAPA project from disk.
     */
    @Action
    public void open() {
        if (OpenSHAPA.getApplication().safeQuit()) {
            OpenSHAPAFileChooser jd = new OpenSHAPAFileChooser();

            jd.addChoosableFileFilter(new MODBFilter());
            jd.addChoosableFileFilter(new CSVFilter());
            jd.addChoosableFileFilter(new SHAPAFilter());

            int result = jd.showOpenDialog(getComponent());

            if (result == JFileChooser.APPROVE_OPTION) {
                OpenSHAPA.getApplication().closeOpenedWindows();

                FileFilter filter = jd.getFileFilter();

                // Opening a project file
                if (filter instanceof SHAPAFilter) {
                    SaveC.getInstance().setLastSaveOption(filter);
                    openProject(jd);
                    // Opening a database file
                } else {
                    SaveC.getInstance().setLastSaveOption(filter);
                    openDatabase(jd);
                }
            }
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
     * Action for showing the about window.
     */
    @Action
    public void showAboutWindow() {
        OpenSHAPA.getApplication().showAboutWindow();
    }

    /**
     * Action for showing the spreadsheet.
     */
    @Action
    public void showSpreadsheet() {
        weakTemporalOrderMenuItem.setSelected(false);
        strongTemporalOrderMenuItem.setSelected(false);
        panel.removeAll();

        // Create a fresh spreadsheet component and redraw the component.
        panel = new SpreadsheetPanel(OpenSHAPA.getProject().getDB());
        setComponent(panel);
        getComponent().revalidate();
        getComponent().resetKeyboardActions();
    }

    /**
     * Action for invoking a script.
     */
    @Action
    public void runScript() {
        new RunScriptC();
    }

    /**
     * Action for removing columns from the database.
     */
    @Action
    public void deleteColumn() {
        new DeleteColumnC(panel.getSelectedCols());
    }

    /**
     * Action for removing cells from the database.
     */
    @Action
    public void deleteCells() {
        new DeleteCellC(panel.getSelectedCells());
    }

    private void openDatabase(final OpenSHAPAFileChooser jd) {
        // Make a project for the new database.
        Project newProject = new Project();
        OpenSHAPA.setProject(newProject);

        try {
            MacshapaDatabase newDB = new MacshapaDatabase();
            OpenSHAPA.getProject().setDatabase(newDB);
            OpenSHAPAView s =
                    (OpenSHAPAView) OpenSHAPA.getApplication().getMainView();
            s.showSpreadsheet();
            // TODO- BugzID:79 This needs to move above showSpreadsheet,
            // when setTicks is fully implemented.
            newDB.setTicks(Constants.TICKS_PER_SECOND);
        } catch (SystemErrorException se) {
            logger.error("Unable to create new database on open", se);
        }
        new OpenDatabaseC(jd.getSelectedFile());

        String dir = jd.getSelectedFile().getAbsolutePath();
        int match = dir.lastIndexOf(jd.getSelectedFile().getName());
        dir = dir.substring(0, match);
        newProject.setDatabaseDir(dir);
        newProject.setDatabaseFile(jd.getSelectedFile().getName());
        newProject.setProjectName(OpenSHAPA.getProject().getDB().getName());

        OpenSHAPA.getApplication().updateTitle();
    }

    private void openProject(final OpenSHAPAFileChooser jd) {
        Project newProject = new Project();
        OpenSHAPA.setProject(newProject);
        OpenProjectC opc = new OpenProjectC();

        if (opc.open(jd.getSelectedFile())) {
            // Successfully loaded the project file
            Project openedProject = OpenSHAPA.getProject();

            try {
                MacshapaDatabase newDB = new MacshapaDatabase();
                OpenSHAPA.getProject().setDatabase(newDB);
                OpenSHAPAView s =
                        (OpenSHAPAView) OpenSHAPA.getApplication()
                                .getMainView();
                s.showSpreadsheet();
                newDB.setTicks(Constants.TICKS_PER_SECOND);
            } catch (SystemErrorException ex) {
                logger.error("Unable to create new database on open", ex);
            }

            // Load the database
            new OpenDatabaseC(new File(openedProject.getDatabaseDir(),
                    openedProject.getDatabaseFile()));

            // Use the plugin manager to load up the data viewers
            PluginManager pm = PluginManager.getInstance();
            DataControllerV dataController = OpenSHAPA.getDataController();

            // Load the plugins required for each media file
            Iterable<ViewerSetting> viewerSettings =
                    openedProject.getMediaViewerSettings();
            boolean showController = false;
            for (ViewerSetting setting : viewerSettings) {
                showController = true;

                File file = new File(setting.getFilePath());
                Plugin plugin = pm.getAssociatedPlugin(setting.getPluginName());
                if (plugin == null) {
                    continue;
                }

                DataViewer viewer = plugin.getNewDataViewer();
                viewer.setDataFeed(file);
                viewer.setOffset(setting.getOffset());

                dataController.addViewer(viewer, setting.getOffset());
                dataController.addTrack(plugin.getTypeIcon(), file
                        .getAbsolutePath(), file.getName(), viewer
                        .getDuration(), setting.getOffset(), setting
                        .getBookmark());
            }

            // Show the data controller
            if (showController) {
                OpenSHAPA.getApplication().showQTVideoController();
            }
        }
    }

    /**
     * Set the SheetLayoutType for the spreadsheet.
     */
    private void setSheetLayout() {
        try {
            SheetLayoutType type = SheetLayoutType.Ordinal;
            OpenSHAPA.getProject().getDB().setTemporalOrdering(false);

            if (weakTemporalOrderMenuItem.isSelected()) {
                type = SheetLayoutType.WeakTemporal;
                OpenSHAPA.getProject().getDB().setTemporalOrdering(true);
            } else if (strongTemporalOrderMenuItem.isSelected()) {
                type = SheetLayoutType.StrongTemporal;
                OpenSHAPA.getProject().getDB().setTemporalOrdering(true);
            }

            new SetSheetLayoutC(type);
        } catch (SystemErrorException e) {
            logger.error("Unable to perform temporal ordering", e);
        }
    }

    /**
     * Checks if changes should be discarded, if so (or no changes) then quits.
     */
    @Action
    public void safeQuit() {
        OpenSHAPA.getApplication().exit();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator fileMenuSeparator = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        spreadsheetMenu = new javax.swing.JMenu();
        showSpreadsheetMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem1 = new javax.swing.JMenuItem();
        newVariableMenuItem = new javax.swing.JMenuItem();
        vocabEditorMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        newCellMenuItem = new javax.swing.JMenuItem();
        newCellLeftMenuItem = new javax.swing.JMenuItem();
        newCellRightMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        deleteColumnMenuItem = new javax.swing.JMenuItem();
        deleteCellMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        weakTemporalOrderMenuItem = new javax.swing.JCheckBoxMenuItem();
        strongTemporalOrderMenuItem = new javax.swing.JCheckBoxMenuItem();
        zoomMenu = new javax.swing.JMenu();
        zoomInMenuItem = new javax.swing.JMenuItem();
        zoomOutMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        resetZoomMenuItem = new javax.swing.JMenuItem();
        controllerMenu = new javax.swing.JMenu();
        qtControllerItem = new javax.swing.JMenuItem();
        scriptMenu = new javax.swing.JMenu();
        runScriptMenuItem = new javax.swing.JMenuItem();
        runRecentScriptMenu = new javax.swing.JMenu();
        recentScriptsHeader = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        favScripts = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        jLabel1.setName("jLabel1"); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout =
                new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(mainPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                mainPanelLayout.createSequentialGroup().add(119, 119, 119).add(
                        jLabel1).addContainerGap(149, Short.MAX_VALUE)));
        mainPanelLayout.setVerticalGroup(mainPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                mainPanelLayout.createSequentialGroup().add(55, 55, 55).add(
                        jLabel1).addContainerGap(184, Short.MAX_VALUE)));
        org.jdesktop.application.ResourceMap resourceMap =
                org.jdesktop.application.Application.getInstance(
                        org.openshapa.OpenSHAPA.class).getContext()
                        .getResourceMap(OpenSHAPAView.class);
        resourceMap.injectComponents(mainPanel);

        menuBar.setName("menuBar"); // NOI18N

        javax.swing.ActionMap actionMap =
                org.jdesktop.application.Application.getInstance(
                        org.openshapa.OpenSHAPA.class).getContext()
                        .getActionMap(OpenSHAPAView.class, this);
        fileMenu.setAction(actionMap.get("saveAs")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        newMenuItem.setAction(actionMap.get("showNewProjectForm")); // NOI18N
        java.util.ResourceBundle bundle =
                java.util.ResourceBundle
                        .getBundle("org/openshapa/views/resources/OpenSHAPAView"); // NOI18N
        newMenuItem.setText(bundle.getString("file_new.text")); // NOI18N
        newMenuItem.setName("newMenuItem"); // NOI18N
        fileMenu.add(newMenuItem);

        openMenuItem.setAction(actionMap.get("open")); // NOI18N
        openMenuItem.setText(bundle.getString("file_open.text")); // NOI18N
        openMenuItem.setName(bundle.getString("file_open.text")); // NOI18N
        fileMenu.add(openMenuItem);

        jSeparator7.setName("jSeparator7"); // NOI18N
        fileMenu.add(jSeparator7);

        saveMenuItem.setAction(actionMap.get("save")); // NOI18N
        saveMenuItem.setName("saveMenuItem"); // NOI18N
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setAction(actionMap.get("saveAs")); // NOI18N
        saveAsMenuItem.setName("saveAsMenuItem"); // NOI18N
        fileMenu.add(saveAsMenuItem);

        fileMenuSeparator.setName("fileMenuSeparator"); // NOI18N
        if (OpenSHAPA.getPlatform() != Platform.MAC) {
            fileMenu.add(fileMenuSeparator);
        }

        exitMenuItem.setAction(actionMap.get("safeQuit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        if (OpenSHAPA.getPlatform() != Platform.MAC) {
            fileMenu.add(exitMenuItem);
        }

        menuBar.add(fileMenu);

        spreadsheetMenu.setAction(actionMap.get("showQTVideoController")); // NOI18N
        spreadsheetMenu.setName("spreadsheetMenu"); // NOI18N
        spreadsheetMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(final javax.swing.event.MenuEvent evt) {
            }

            public void menuDeselected(final javax.swing.event.MenuEvent evt) {
            }

            public void menuSelected(final javax.swing.event.MenuEvent evt) {
                spreadsheetMenuMenuSelected(evt);
            }
        });

        showSpreadsheetMenuItem.setAction(actionMap.get("showSpreadsheet")); // NOI18N
        showSpreadsheetMenuItem.setName("showSpreadsheetMenuItem"); // NOI18N
        spreadsheetMenu.add(showSpreadsheetMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        spreadsheetMenu.add(jSeparator1);

        jMenuItem1.setAction(actionMap.get("showNewVariableForm")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        spreadsheetMenu.add(jMenuItem1);

        newVariableMenuItem.setAction(actionMap.get("showVariableList")); // NOI18N
        newVariableMenuItem.setName("newVariableMenuItem"); // NOI18N
        spreadsheetMenu.add(newVariableMenuItem);

        vocabEditorMenuItem.setAction(actionMap.get("showVocabEditor")); // NOI18N
        vocabEditorMenuItem.setName("vocabEditorMenuItem"); // NOI18N
        spreadsheetMenu.add(vocabEditorMenuItem);

        jSeparator2.setName("jSeparator2"); // NOI18N
        spreadsheetMenu.add(jSeparator2);

        newCellMenuItem.setName("newCellMenuItem"); // NOI18N
        newCellMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                newCellMenuItemActionPerformed(evt);
            }
        });
        spreadsheetMenu.add(newCellMenuItem);

        newCellLeftMenuItem.setName("newCellLeftMenuItem"); // NOI18N
        newCellLeftMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        newCellLeftMenuItemActionPerformed(evt);
                    }
                });
        spreadsheetMenu.add(newCellLeftMenuItem);

        newCellRightMenuItem.setName("newCellRightMenuItem"); // NOI18N
        newCellRightMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        newCellRightMenuItemActionPerformed(evt);
                    }
                });
        spreadsheetMenu.add(newCellRightMenuItem);

        jSeparator3.setName("jSeparator3"); // NOI18N
        spreadsheetMenu.add(jSeparator3);

        deleteColumnMenuItem.setAction(actionMap.get("deleteColumn")); // NOI18N
        deleteColumnMenuItem.setName("deleteColumnMenuItem"); // NOI18N
        spreadsheetMenu.add(deleteColumnMenuItem);

        deleteCellMenuItem.setAction(actionMap.get("deleteCells")); // NOI18N
        deleteCellMenuItem.setName("deleteCellMenuItem"); // NOI18N
        spreadsheetMenu.add(deleteCellMenuItem);

        jSeparator6.setName("jSeparator6"); // NOI18N
        spreadsheetMenu.add(jSeparator6);

        weakTemporalOrderMenuItem.setName("weakTemporalOrderMenuItem"); // NOI18N
        weakTemporalOrderMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        weakTemporalMenuItemActionPerformed(evt);
                    }
                });
        spreadsheetMenu.add(weakTemporalOrderMenuItem);

        strongTemporalOrderMenuItem.setName("strongTemporalOrderMenuItem"); // NOI18N
        strongTemporalOrderMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        strongTemporalMenuItemActionPerformed(evt);
                    }
                });
        spreadsheetMenu.add(strongTemporalOrderMenuItem);

        zoomMenu.setName("zoomMenu"); // NOI18N

        zoomInMenuItem.setName("zoomInMenuItem"); // NOI18N
        zoomInMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                zoomInMenuItemActionPerformed(evt);
            }
        });
        zoomMenu.add(zoomInMenuItem);

        zoomOutMenuItem.setName("zoomOutMenuItem"); // NOI18N
        zoomOutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                zoomOutMenuItemActionPerformed(evt);
            }
        });
        zoomMenu.add(zoomOutMenuItem);

        jSeparator5.setName("jSeparator5"); // NOI18N
        zoomMenu.add(jSeparator5);

        resetZoomMenuItem.setName("resetZoomMenuItem"); // NOI18N
        resetZoomMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        resetZoomMenuItemActionPerformed(evt);
                    }
                });
        zoomMenu.add(resetZoomMenuItem);

        spreadsheetMenu.add(zoomMenu);

        menuBar.add(spreadsheetMenu);

        controllerMenu.setName("controllerMenu"); // NOI18N

        qtControllerItem.setAction(actionMap.get("showQTVideoController")); // NOI18N
        qtControllerItem.setName("qtControllerItem"); // NOI18N
        controllerMenu.add(qtControllerItem);

        menuBar.add(controllerMenu);

        scriptMenu.setName("scriptMenu"); // NOI18N
        scriptMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(final javax.swing.event.MenuEvent evt) {
            }

            public void menuDeselected(final javax.swing.event.MenuEvent evt) {
            }

            public void menuSelected(final javax.swing.event.MenuEvent evt) {
                populateFavourites(evt);
            }
        });

        runScriptMenuItem.setAction(actionMap.get("runScript")); // NOI18N
        runScriptMenuItem.setName("runScriptMenuItem"); // NOI18N
        scriptMenu.add(runScriptMenuItem);

        runRecentScriptMenu.setName("runRecentScriptMenu"); // NOI18N
        runRecentScriptMenu
                .addMenuListener(new javax.swing.event.MenuListener() {
                    public void menuCanceled(
                            final javax.swing.event.MenuEvent evt) {
                    }

                    public void menuDeselected(
                            final javax.swing.event.MenuEvent evt) {
                    }

                    public void menuSelected(
                            final javax.swing.event.MenuEvent evt) {
                        populateRecentScripts(evt);
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

        helpMenu.setAction(actionMap.get("showVariableList")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutWindow")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        if (OpenSHAPA.getPlatform() != Platform.MAC) {
            helpMenu.add(aboutMenuItem);
        }

        menuBar.add(helpMenu);
        resourceMap.injectComponents(menuBar);

        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The action to invoke when the user selects 'strong temporal ordering'.
     * 
     * @param evt
     *            The event that fired this action.
     */
    private void strongTemporalMenuItemActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_strongTemporalMenuItemActionPerformed
        weakTemporalOrderMenuItem.setSelected(false);
        setSheetLayout();
    }// GEN-LAST:event_strongTemporalMenuItemActionPerformed

    /**
     * The action to invoke when the user selects 'weak temporal ordering'.
     * 
     * @param evt
     *            The event that fired this action.
     */
    private void weakTemporalMenuItemActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_weakTemporalMenuItemActionPerformed
        strongTemporalOrderMenuItem.setSelected(false);
        setSheetLayout();
    }// GEN-LAST:event_weakTemporalMenuItemActionPerformed

    /**
     * The action to invoke when the user selects 'recent scripts' from the
     * scripting menu.
     * 
     * @param evt
     *            The event that fired this action.
     */
    private void populateRecentScripts(final javax.swing.event.MenuEvent evt) {// GEN-FIRST:event_populateRecentScripts
        // Flush the menu - excluding the top menu item.
        int size = runRecentScriptMenu.getMenuComponentCount();
        for (int i = 1; i < size; i++) {
            runRecentScriptMenu.remove(1);
        }

        LinkedList<File> lastScripts = OpenSHAPA.getLastScriptsExecuted();
        for (File f : lastScripts) {
            runRecentScriptMenu.add(createScriptMenuItemFromFile(f));
        }
    }// GEN-LAST:event_populateRecentScripts

    /** The directory holding a users favourite scripts. */
    static final String FAV_DIR = "favourites";

    /**
     * The action to invoke when the user selects 'scripts' from the main menu.
     * 
     * @param evt
     *            The event that fired this action.
     */
    private void populateFavourites(final javax.swing.event.MenuEvent evt) {// GEN-FIRST:event_populateFavourites
        // Favourite script list starts after the 'favScripts' menu item - which
        // is just a stub for a starting point. Search for the favScripts as the
        // starting point for deleting existing scripts from the menu.
        Component[] list = scriptMenu.getMenuComponents();
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
    }// GEN-LAST:event_populateFavourites

    /**
     * = Function to 'zoom out' (make font size smaller) by ZOOM_INTERVAL
     * points.
     * 
     * @param evt
     *            The event that triggered this action.
     */
    private void zoomInMenuItemActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_zoomInMenuItemActionPerformed
        changeFontSize(ZOOM_INTERVAL);
    }// GEN-LAST:event_zoomInMenuItemActionPerformed

    /**
     * Function to 'zoom out' (make font size smaller) by ZOOM_INTERVAL points.
     * 
     * @param evt
     */
    private void zoomOutMenuItemActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_zoomOutMenuItemActionPerformed
        changeFontSize(-ZOOM_INTERVAL);
    }// GEN-LAST:event_zoomOutMenuItemActionPerformed

    /**
     * Function to reset the zoom level to the default size.
     * 
     * @param evt
     *            The event that triggered this action.
     */
    private void resetZoomMenuItemActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_resetZoomMenuItemActionPerformed

        Configuration config = Configuration.getInstance();
        Font f = config.getSSDataFont();

        changeFontSize(ZOOM_DEFAULT_SIZE - f.getSize());
    }// GEN-LAST:event_resetZoomMenuItemActionPerformed

    /**
     * The method to invoke when the use selects the spreadsheet menu item.
     * 
     * @param evt
     *            The event that triggered this action.
     */
    private void spreadsheetMenuMenuSelected(
            final javax.swing.event.MenuEvent evt) {// GEN-FIRST:event_spreadsheetMenuMenuSelected

        ResourceMap rMap =
                Application.getInstance(OpenSHAPA.class).getContext()
                        .getResourceMap(OpenSHAPAView.class);
        if (panel.getSelectedCols().size() == 0) {
            deleteColumnMenuItem.setEnabled(false);
        } else if (panel.getSelectedCols().size() == 1) {
            deleteColumnMenuItem.setText(rMap
                    .getString("deleteColumnMenuItemSingle.text"));
            deleteColumnMenuItem.setEnabled(true);
        } else {
            deleteColumnMenuItem.setText(rMap
                    .getString("deleteColumnMenuItemPlural.text"));
            deleteColumnMenuItem.setEnabled(true);
        }

        if (panel.getSelectedCells().size() == 0) {
            deleteCellMenuItem.setEnabled(false);
        } else if (panel.getSelectedCells().size() == 1) {
            deleteCellMenuItem.setText(rMap
                    .getString("deleteCellMenuItemSingle.text"));
            deleteCellMenuItem.setEnabled(true);
        } else {
            deleteCellMenuItem.setText(rMap
                    .getString("deleteCellMenuItemPlural.text"));
            deleteCellMenuItem.setEnabled(true);
        }

        if (panel.getAdjacentSelectedCells(ArrayDirection.LEFT) == 0) {
            newCellLeftMenuItem.setEnabled(false);
        } else if (panel.getAdjacentSelectedCells(ArrayDirection.LEFT) == 1) {
            newCellLeftMenuItem.setText(rMap
                    .getString("newCellLeftMenuItemSingle.text"));
            newCellLeftMenuItem.setEnabled(true);
        } else {
            newCellLeftMenuItem.setText(rMap
                    .getString("newCellLeftMenuItemPlural.text"));
            newCellLeftMenuItem.setEnabled(true);
        }

        if (panel.getAdjacentSelectedCells(ArrayDirection.RIGHT) == 0) {
            newCellRightMenuItem.setEnabled(false);
        } else if (panel.getAdjacentSelectedCells(ArrayDirection.RIGHT) == 1) {
            newCellRightMenuItem.setText(rMap
                    .getString("newCellRightMenuItemSingle.text"));
            newCellRightMenuItem.setEnabled(true);
        } else {
            newCellRightMenuItem.setText(rMap
                    .getString("newCellRightMenuItemPlural.text"));
            newCellRightMenuItem.setEnabled(true);
        }
    }// GEN-LAST:event_spreadsheetMenuMenuSelected

    /**
     * The action to invoke when the user selects new cell from the menu.
     * 
     * @param evt
     *            The event that fired this action.
     */
    private void newCellMenuItemActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newCellMenuItemActionPerformed
        new CreateNewCellC();
    }// GEN-LAST:event_newCellMenuItemActionPerformed

    /**
     * The action to invoke when the user selects new cell to the left from the
     * menu.
     * 
     * @param evt
     *            The event that fired this action.
     */
    private void newCellLeftMenuItemActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newCellLeftMenuItemActionPerformed
        new CreateNewCellC(panel.getSelectedCells(), ArrayDirection.LEFT);
    }// GEN-LAST:event_newCellLeftMenuItemActionPerformed

    /**
     * The action to invoke when the user selects new cell to the right from the
     * menu.
     * 
     * @param evt
     *            The event that fired this action.
     */
    private void newCellRightMenuItemActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newCellRightMenuItemActionPerformed
        new CreateNewCellC(panel.getSelectedCells(), ArrayDirection.RIGHT);
    }// GEN-LAST:event_newCellRightMenuItemActionPerformed

    /**
     * Changes the font size by adding sizeDif to the current size. Then it
     * creates and revalidates a new panel to show the font update. This will
     * not make the font smaller than smallestSize.
     * 
     * @param sizeDif
     *            The number to add to the current font size.
     */
    public void changeFontSize(final int sizeDif) {
        Configuration config = Configuration.getInstance();
        Font f = config.getSSDataFont();
        int size = f.getSize();
        size = size + sizeDif;

        if (size < ZOOM_MIN_SIZE) {
            size = ZOOM_MIN_SIZE;
        } else if (size > ZOOM_MAX_SIZE) {
            size = ZOOM_MAX_SIZE;
        }

        Font biggerFont = new Font(f.getFontName(), f.getStyle(), size);

        config.setSSDataFont(biggerFont);

        // Create and redraw fresh window pane so all of the fonts are new
        // again.
        panel.revalidate();
        panel.repaint();
    }

    /**
     * Creates a new menu item for running a named script.
     * 
     * @param f
     *            The file to run when menu item is selected.
     * @return The jmenuitem that can be added to a menu.
     */
    public JMenuItem createScriptMenuItemFromFile(final File f) {
        JMenuItem menuItem = new JMenuItem();
        menuItem.setText(f.toString());
        menuItem.setName(f.toString());
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                runRecentScript(evt);
            }
        });

        return menuItem;
    }

    /**
     * The action to invoke when the user selects a recent script to run.
     * 
     * @param evt
     *            The event that triggered this action.
     */
    private void runRecentScript(final java.awt.event.ActionEvent evt) {
        new RunScriptC(evt.getActionCommand());
    }

    /**
     * Returns SpreadsheetPanel
     * 
     * @return SpreadsheetPanel panel
     */
    public SpreadsheetPanel getSpreadsheetPanel() {
        return panel;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu controllerMenu;
    private javax.swing.JMenuItem deleteCellMenuItem;
    private javax.swing.JMenuItem deleteColumnMenuItem;
    private javax.swing.JMenuItem favScripts;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newCellLeftMenuItem;
    private javax.swing.JMenuItem newCellMenuItem;
    private javax.swing.JMenuItem newCellRightMenuItem;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem newVariableMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem qtControllerItem;
    private javax.swing.JMenuItem recentScriptsHeader;
    private javax.swing.JMenuItem resetZoomMenuItem;
    private javax.swing.JMenu runRecentScriptMenu;
    private javax.swing.JMenuItem runScriptMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenu scriptMenu;
    private javax.swing.JMenuItem showSpreadsheetMenuItem;
    private javax.swing.JMenu spreadsheetMenu;
    private javax.swing.JCheckBoxMenuItem strongTemporalOrderMenuItem;
    private javax.swing.JMenuItem vocabEditorMenuItem;
    private javax.swing.JCheckBoxMenuItem weakTemporalOrderMenuItem;
    private javax.swing.JMenuItem zoomInMenuItem;
    private javax.swing.JMenu zoomMenu;
    private javax.swing.JMenuItem zoomOutMenuItem;
    // End of variables declaration//GEN-END:variables

    // Variable for the amount to raise the font size by when zooming.
    public static final int ZOOM_INTERVAL = 4;
    public static final int ZOOM_DEFAULT_SIZE = 14;

    // Variables to set the maximum zoom and minimum zoom.
    public static final int ZOOM_MAX_SIZE = 42;
    public static final int ZOOM_MIN_SIZE = 8;

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(OpenSHAPAView.class);

    /** The spreadsheet panel for this view. */
    private SpreadsheetPanel panel;
}
