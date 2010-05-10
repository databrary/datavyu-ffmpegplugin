package org.openshapa.controllers.project;

import com.usermetrix.jclient.UserMetrix;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.openshapa.OpenSHAPA;

import org.openshapa.controllers.PlaybackController;

import org.openshapa.models.component.TrackModel;
import org.openshapa.models.db.DataColumn;
import org.openshapa.models.db.Database;
import org.openshapa.models.db.ExternalCascadeListener;
import org.openshapa.models.db.ExternalColumnListListener;
import org.openshapa.models.db.ExternalDataColumnListener;
import org.openshapa.models.db.MacshapaDatabase;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.project.Project;
import org.openshapa.models.project.TrackSettings;
import org.openshapa.models.project.ViewerSetting;

import org.openshapa.views.MixerControllerV;
import org.openshapa.views.OpenSHAPAView;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.Plugin;
import org.openshapa.views.continuous.PluginManager;
import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.SpreadsheetPanel;


/**
 * This class is responsible for managing a project.
 */
public final class ProjectController implements ExternalColumnListListener,
    ExternalDataColumnListener, ExternalCascadeListener {

    /** The current project we are working on. */
    private Project project;

    /** The current database we are working on. */
    private MacshapaDatabase db;

    /** The id of the last datacell that was created. */
    private long lastCreatedCellID;

    /** The id of the last selected cell. */
    private long lastSelectedCellID;

    /** The id of the last datacell that was created. */
    private long lastCreatedColID;

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(ProjectController.class);

    /**
     * Controller state
     */
    /** has the project been changed since it was created. */
    private boolean changed;

    /** Is the project new? */
    private boolean newProject;

    /** Last option used for saving. */
    private FileFilter lastSaveOption;

    /** The changes made to the column. */
    private ColumnChanges colChanges;

    /**
     * Default constructor.
     */
    public ProjectController() {
        project = new Project();
        changed = false;
        newProject = true;
        colChanges = new ColumnChanges();
    }

    /**
     * Constructor.
     *
     * @param projectModel The project model that this controller marshalls.
     */
    public ProjectController(final Project projectModel) {
        this.project = projectModel;
        changed = false;
        newProject = false;
        colChanges = new ColumnChanges();
    }

    /**
     * Set the last save option used. This affects the "Save" functionality.
     *
     * @param saveOption The latest option used for "saving".
     */
    public void setLastSaveOption(final FileFilter saveOption) {
        lastSaveOption = saveOption;
    }

    /**
     * @return The last "saved" option used when saving.
     */
    public FileFilter getLastSaveOption() {
        return lastSaveOption;
    }

    /**
     * Creates a new project that replaces the model that this controller
     * marshalls.
     *
     * @param name The name of the new project that this controller will
     * marshall.
     */
    public void createNewProject(final String name) {
        project = new Project();
        setProjectName(name);
        changed = false;
        newProject = true;
    }

    /**
     * Sets the name of the project.
     *
     * @param newProjectName
     *            The new name to use for this project.
     */
    public void setProjectName(final String newProjectName) {
        project.setProjectName(newProjectName);
    }

    /**
     * Sets the database associated with this project.
     *
     * @param newDB
     *            The new database to use with this project.
     */
    public void setDatabase(final MacshapaDatabase newDB) {
        db = newDB;

        try {
            db.registerCascadeListener(this);
            db.registerColumnListListener(this);
        } catch (SystemErrorException e) {
            logger.error("registerColumnListListener failed", e);
        }
    }

    /**
     * Gets the database associated with this project.
     *
     * @return The single database to use with this project.
     */
    public MacshapaDatabase getDB() {
        return db;
    }

    /**
     * @return The id of the last created cell.
     */
    public long getLastCreatedCellId() {
        return lastCreatedCellID;
    }

    /**
     * Sets the id of the last created cell to the specified parameter.
     *
     * @param newId
     *            The Id of the newly created cell.
     */
    public void setLastCreatedCellId(final long newId) {
        lastCreatedCellID = newId;
    }

    /**
     * @return The id of the last selected cell.
     */
    public long getLastSelectedCellId() {
        return lastSelectedCellID;
    }

    /**
     * Sets the id of the last selected cell to the specified parameter.
     *
     * @param newId
     *            The id of hte newly selected cell.
     */
    public void setLastSelectedCellId(final long newId) {
        lastSelectedCellID = newId;
    }

    /**
     * @return The id of the last created column.
     */
    public long getLastCreatedColId() {
        return lastCreatedColID;
    }

    /**
     * Sets the id of the last created column to the specified parameter.
     *
     * @param newId
     *            The Id of the newly created column.
     */
    public void setLastCreatedColId(final long newId) {
        lastCreatedColID = newId;
    }

    /**
     * @return the changed
     */
    public boolean isChanged() {
        return (changed || ((db != null) && db.isChanged()));
    }

    /**
     * @return the newProject
     */
    public boolean isNewProject() {
        return newProject;
    }

    /**
     * @return the project name
     */
    public String getProjectName() {
        return project.getProjectName();
    }

    /**
     * Set the database file name, directory not included.
     *
     * @param fileName The file name of the database that the project
     * model references.
     */
    public void setDatabaseFileName(final String fileName) {
        project.setDatabaseFileName(fileName);
    }

    /**
     * @return the database file name, directory not included.
     */
    public String getDatabaseFileName() {
        return project.getDatabaseFileName();
    }

    /**
     * Set the directory the project file (and all project specific resources)
     * resides in.
     *
     * @param directory The directory that the project file resides within.
     */
    public void setProjectDirectory(final String directory) {
        project.setProjectDirectory(directory);
    }

    /**
     * @return the directory the project file (and all project specific
     *         resources) resides in.
     */
    public String getProjectDirectory() {
        return project.getProjectDirectory();
    }

    /**
     * Load the settings from the current project.
     */
    public void loadProject() {

        // Use the plugin manager to load up the data viewers
        PluginManager pm = PluginManager.getInstance();

        PlaybackController playbackController = OpenSHAPA
            .getPlaybackController();

        // Load the plugins required for each media file
        boolean showController = false;

        for (ViewerSetting setting : project.getViewerSettings()) {
            showController = true;

            File file = new File(setting.getFilePath());
            Plugin plugin = pm.getAssociatedPlugin(setting.getPluginName());

            if (plugin == null) {
                continue;
            }

            DataViewer viewer = plugin.getNewDataViewer();
            viewer.setDataFeed(file);
            viewer.setOffset(setting.getOffset());

            playbackController.addViewer(viewer, setting.getOffset());
            playbackController.addTrack(plugin.getTypeIcon(),
                file.getAbsolutePath(), file.getName(), viewer.getDuration(),
                setting.getOffset(), viewer.getTrackPainter());
        }

        MixerControllerV mixerController =
            playbackController.getMixerController();

        for (TrackSettings setting : project.getTrackSettings()) {
            mixerController.setTrackInterfaceSettings(setting.getFilePath(),
                setting.getBookmarkPosition(), setting.isLocked());
        }

        // Show the data controller
        if (showController) {
            OpenSHAPA.getApplication().showDataController();
        }
    }

    /**
     * Gather and update the various project specific settings.
     */
    public void updateProject() {

        if (!changed && !newProject) {
            return;
        }

        PlaybackController playbackController = OpenSHAPA
            .getPlaybackController();

        // Gather the data viewer settings
        List<ViewerSetting> viewerSettings = new LinkedList<ViewerSetting>();

        for (DataViewer viewer : playbackController.getDataViewers()) {
            ViewerSetting vs = new ViewerSetting();
            vs.setFilePath(viewer.getDataFeed().getAbsolutePath());
            vs.setOffset(viewer.getOffset());
            vs.setPluginName(viewer.getClass().getName());

            viewerSettings.add(vs);
        }

        project.setViewerSettings(viewerSettings);

        // Gather the user interface settings
        List<TrackSettings> trackSettings = new LinkedList<TrackSettings>();

        for (TrackModel model
            : playbackController.getMixerController().getAllTrackModels()) {
            TrackSettings ts = new TrackSettings();
            ts.setFilePath(model.getTrackId());
            ts.setBookmarkPosition(model.getBookmark());
            ts.setLocked(model.isLocked());

            trackSettings.add(ts);
        }

        project.setTrackSettings(trackSettings);
    }

    /**
     * Marks the project state as being saved.
     */
    public void markProjectAsUnchanged() {
        changed = false;
        newProject = false;
    }

    /**
     * Marks the project as being changed. This method will not trigger a
     * project state update.
     */
    public void projectChanged() {
        changed = true;
    }

    /**
     * @return a deep-copy clone of the current project.
     */
    public Project getProject() {
        return project;
    }


    // ------------------------------------------------------------------------
    // ExternalColumnListListener Implementation
    //
    // ------------------------------------------------------------------------

    /**
     * Action to invoke when a column is removed from a database.
     *
     * @param theDB The database that the column has been removed from.
     * @param colID The id of the freshly removed column.
     * @param oldCov The column order vector prior to the deletion.
     * @param newCov The column order vector after to the deletion.
     */
    public void colDeletion(final Database theDB, final long colID,
        final Vector<Long> oldCov, final Vector<Long> newCov) {
        final ExternalDataColumnListener listener = this;

        Runnable edtTask = new Runnable() {
                public void run() {

                    try {
                        OpenSHAPAView s = (OpenSHAPAView) OpenSHAPA
                            .getApplication().getMainView();
                        s.getSpreadsheetPanel().deselectAll();
                        s.getSpreadsheetPanel().removeColumn(colID);
                        db.deregisterDataColumnListener(colID, listener);
                        s.getSpreadsheetPanel().relayoutCells();
                    } catch (SystemErrorException se) {
                        logger.error("Unable to remove column", se);
                    }
                }
            };
        SwingUtilities.invokeLater(edtTask);
    }

    /**
     * Action to invoke when a column is added to a database.
     *
     * @param theDB The database that the column has been added to.
     * @param colID The id of the newly added column.
     * @param oldCov The column order vector prior to the insertion.
     * @param newCov The column order vector after to the insertion.
     */
    public void colInsertion(final Database theDB, final long colID,
        final Vector<Long> oldCov, final Vector<Long> newCov) {
        final ExternalDataColumnListener listener = this;

        Runnable edtTask = new Runnable() {
                public void run() {

                    try {
                        OpenSHAPAView s = (OpenSHAPAView) OpenSHAPA
                            .getApplication().getMainView();
                        s.getSpreadsheetPanel().deselectAll();
                        s.getSpreadsheetPanel().addColumn(theDB, colID);
                        db.registerDataColumnListener(colID, listener);
                        s.getSpreadsheetPanel().relayoutCells();
                    } catch (SystemErrorException se) {
                        logger.error("Unable to insert column.", se);
                    }
                }
            };
        SwingUtilities.invokeLater(edtTask);
    }

    /**
     * Action to invoke when the column order vector is edited (i.e, the order
     * of the columns is changed without any insertions or deletions).
     *
     * @param theDB The database that the column has been added to.
     * @param oldCov The column order vector prior to the insertion.
     * @param newCov The column order vector after to the insertion.
     */
    public void colOrderVectorEdited(final Database theDB,
        final Vector<Long> oldCov, final Vector<Long> newCov) {

        // Do nothing for now
        return;
    }


    // ------------------------------------------------------------------------
    // ExternalDataColumnListener Implementation
    //
    // ------------------------------------------------------------------------

    /**
     * Called when a DataCell is deleted from the DataColumn.
     * @param db The database the column belongs to.
     * @param colID The ID assigned to the DataColumn.
     * @param cellID ID of the DataCell that is being deleted.
     */
    public void DColCellDeletion(final Database db, final long colID,
        final long cellID) {

        colChanges.cellDeleted.add(new Change(colID, cellID));
    }


    /**
     * Called when a DataCell is inserted in the vocab list.
     * @param db The database the column belongs to.
     * @param colID The ID assigned to the DataColumn.
     * @param cellID ID of the DataCell that is being inserted.
     */
    public void DColCellInsertion(final Database db, final long colID,
        final long cellID) {

        colChanges.cellInserted.add(new Change(colID, cellID));
    }

    /**
     * Called when one fields of the target DataColumn are changed.
     * @param db The database.
     * @param colID The ID assigned to the DataColumn.
     * @param nameChanged indicates whether the name changed.
     * @param oldName reference to oldName.
     * @param newName reference to newName.
     * @param hiddenChanged indicates the hidden field changed.
     * @param oldHidden Old Hidden value.
     * @param newHidden New Hidden value.
     * @param readOnlyChanged indicates the readOnly field changed.
     * @param oldReadOnly Old ReadOnly value.
     * @param newReadOnly New ReadOnly value.
     * @param varLenChanged indicates the varLen field changed.
     * @param oldVarLen Old varLen value.
     * @param newVarLen New varLen value.
     * @param selectedChanged indicates the selection status of the DataColumn
     * has changed.
     * @param oldSelected Old Selected value.
     * @param newSelected New Selected value.
     */
    public void DColConfigChanged(final Database db, final long colID,
        final boolean nameChanged, final String oldName, final String newName,
        final boolean hiddenChanged, final boolean oldHidden,
        final boolean newHidden, final boolean readOnlyChanged,
        final boolean oldReadOnly, final boolean newReadOnly,
        final boolean varLenChanged, final boolean oldVarLen,
        final boolean newVarLen, final boolean selectedChanged,
        final boolean oldSelected, final boolean newSelected) {


        Change c = new Change(colID, 0);
        c.changed = true;
        colChanges.nameChanged.add(c);
    }

    /**
     * Called when the DataColumn of interest is deleted.
     * @param db The database.
     * @param colID The ID assigned to the DataColumn.
     */
    public void DColDeleted(final Database db, final long colID) {
        // Not handled - should be handled by ColumnListener in spreadsheet.
    }

    /**
     * Called at the beginning of a cascade of changes through the database.
     * @param db The database.
     */
    public void beginCascade(final Database db) {
        colChanges.reset();
    }

    /**
     * Called at the end of a cascade of changes through the database.
     * @param db The database.
     */
    public void endCascade(final Database db) {
        Runnable edtTask = new Runnable() {
                public void run() {

                    if (colChanges.cellDeleted.size() > 0) {

                        for (Change c : colChanges.cellDeleted) {
                            OpenSHAPAView view = (OpenSHAPAView) OpenSHAPA
                                .getApplication().getMainView();
                            SpreadsheetPanel spreadsheet =
                                view.getSpreadsheetPanel();
                            SpreadsheetColumn col = spreadsheet.getColumn(
                                    c.getCollID());
                            col.deleteCellByID(c.getCellID());
                        }
                    }

                    if (colChanges.cellInserted.size() > 0) {

                        for (Change c : colChanges.cellInserted) {
                            OpenSHAPAView view = (OpenSHAPAView) OpenSHAPA
                                .getApplication().getMainView();
                            SpreadsheetPanel spreadsheet =
                                view.getSpreadsheetPanel();
                            SpreadsheetColumn col = spreadsheet.getColumn(
                                    c.getCollID());
                            col.insertCellByID(c.getCellID());

                            // Force the update of the spreadsheet.
                            spreadsheet.deselectAll();
                            spreadsheet.relayoutCells();
                            spreadsheet.highlightCell(c.getCellID());
                        }
                    }

                    if (colChanges.nameChanged.size() > 0) {

                        for (Change c : colChanges.nameChanged) {
                            if (c.isColChanged()) {
                                try {
                                    DataColumn dbColumn = db.getDataColumn(
                                            c.getCollID());
                                    OpenSHAPAView view = (OpenSHAPAView)
                                        OpenSHAPA.getApplication()
                                        .getMainView();
                                    SpreadsheetPanel spreadsheet =
                                        view.getSpreadsheetPanel();
                                    SpreadsheetColumn col =
                                        spreadsheet.getColumn(c.getCollID());
                                    col.setText(dbColumn.getName() + "  ("
                                        + dbColumn.getItsMveType() + ")");
                                } catch (SystemErrorException e) {
                                    logger.error("Problem getting data column",
                                        e);
                                }
                            }
                        }
                    }

                    colChanges.reset();
                }
            };

        try {
            SwingUtilities.invokeAndWait(edtTask);
        } catch (InvocationTargetException ie) {
            logger.error("Event Dispatch Thread - failed", ie);
        } catch (InterruptedException ie) {
            logger.error("Event Dispatch Thread - failed", ie);
        }
    }

    /**
     * Private class for recording information about a particular change to
     * the spreadsheet view.
     */
    private final class Change {

        /** The ID of the column we are performing a change for. */
        private long collID;

        /** The ID of the cell we are manipulating. */
        private long cellID;

        /** Has the name of the column changed? */
        private boolean changed;

        /**
         * Constructor.
         *
         * @param newColID The ID of the column we are changing.
         * @param newCelID the ID of the cell we are manipulating.
         */
        private Change(final long newColID, final long newCelID) {
            collID = newColID;
            cellID = newCelID;
        }

        /**
         * @return The ID of the column we are changing.
         */
        public long getCollID() {
            return collID;
        }

        /**
         * @return The ID of the cell we are changing.
         */
        public long getCellID() {
            return cellID;
        }

        /**
         * @return has the column changed or not.
         */
        public boolean isColChanged() {
            return changed;
        }
    }

    /**
     * Private class for recording the changes reported by the listener
     * callbacks on this column.
     */
    private final class ColumnChanges {

        /** The list of column name changes. */
        private Vector<Change> nameChanged;

        /** List of cell IDs of newly inserted cells. */
        private Vector<Change> cellInserted;

        /** List of cell IDs of deleted cells. */
        private Vector<Change> cellDeleted;

        /**
         * ColumnChanges constructor.
         */
        private ColumnChanges() {
            cellInserted = new Vector<Change>();
            cellDeleted = new Vector<Change>();
            nameChanged = new Vector<Change>();
            reset();
        }

        /**
         * Reset the ColumnChanges flags and lists.
         */
        private void reset() {
            nameChanged.clear();
            cellInserted.clear();
            cellDeleted.clear();
        }
    }
}
