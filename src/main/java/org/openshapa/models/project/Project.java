package org.openshapa.models.project;

import java.util.HashMap;
import java.util.Map;
import org.openshapa.models.db.MacshapaDatabase;

/**
 * This class represents a project in OpenSHAPA. A project manages the different
 * files used by OpenSHAPA, such as database files and media files.
 */
public final class Project {

    /** Project specification version. */
    public static final int VERSION = 1;
    /** Name of this project. */
    private String projectName;
    /** Project description. */
    private String projectDescription;
    /** Directory the database and project is stored. */
    private String databaseDir;
    /** Database file name. */
    private String databaseFile;
    /** The current database we are working on. */
    private MacshapaDatabase db;
    /** The id of the last datacell that was created. */
    private long lastCreatedCellID;
    /** The id of the last selected cell. */
    private long lastSelectedCellID;
    /** The id of the last datacell that was created. */
    private long lastCreatedColID;

    /**
     * Key   : file path
     * Value : viewer settings for the medial file
     */
    private Map<String, ViewerSetting> viewerSettings;
    /** has this project been changed since it was created. */
    private boolean changed;
    /** Is this a new project? */
    private boolean newProject;

    /**
     * Constructor.
     */
    public Project() {
        viewerSettings = new HashMap();
        changed = false;
        newProject = true;
        lastCreatedCellID = 0;
        lastCreatedColID = 0;
        lastSelectedCellID = 0;
    }

    /**
     * Sets the database associated with this project.
     *
     * @param newDB The new database to use with this project.
     */
    public void setDatabase(final MacshapaDatabase newDB) {
        this.db = newDB;
    }

    /**
     * Gets the database associated with this project.
     *
     * @return The single database to use with this project.
     */
    public MacshapaDatabase getDB() {
        return this.db;
    }

    public void setDatabasePath(String directory, String filename) {
        this.databaseDir = directory;
        this.databaseFile = filename;
        changed = true;
    }

    public String getDatabaseDir() {
        return databaseDir;
    }

    public String getDatabaseFile() {
        return databaseFile;
    }

    public void setDatabaseDir(String databaseDir) {
        this.databaseDir = databaseDir;
        changed = true;
    }

    public void setDatabaseFile(String databaseFile) {
        this.databaseFile = databaseFile;
        changed = true;
    }

    /**
     * @return The name of this project.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @return True if a change has been made to either the project or
     * the database, false otherwise.
     */
    public boolean isChanged() {
        return (changed || (db != null && db.isChanged()));
    }

    /**
     * Mark the project as changed.
     *
     * @param isChanged The state to use for the change status of the project.
     * true = project has changed, false = project is unchanged.
     */
    public void setChanged(final boolean isChanged) {
        this.changed = isChanged;
    }

    /**
     * Sets the name of the project.
     *
     * @param newProjectName The new name to use for this project.
     */
    public void setProjectName(final String newProjectName) {
        // Check Pre-conditions.
        assert (newProjectName != null);

        // Set the name of the project.
        String name = newProjectName;
        int match = name.lastIndexOf(".shapa");
        if (match != -1) {
            name = name.substring(0, match);
        }
        if (name.equals("")) {
            name = "Project1";
        }
        this.projectName = name;
        changed = true;
    }

    public void setViewerSettings(Map<String, ViewerSetting> viewerSettings) {
        if (viewerSettings != null) {
            this.viewerSettings = viewerSettings;
            changed = true;
        }
    }

    /**
     * @return The media files being managed by OpenSHAPA.
     */
    public Iterable<String> getMediaFiles() {
        return viewerSettings.keySet();
    }

    /**
     * @return Collection of viewer settings used for each media file being
     * managed by OpenSHAPA.
     */
    public Iterable<ViewerSetting> getMediaViewerSettings() {
       return viewerSettings.values();
    }

    public Map<String, ViewerSetting> getViewerSettings() {
        return viewerSettings;
    }

    /**
     * @return The description of this project.
     */
    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    /**
     * Add a new viewer for the project to manage.
     *
     * @param pluginName The name of the plugin used to manage the media file
     * @param filePath The absolute path to the media file
     * @param offset The playback offset of the media file
     */
    public void addViewerSetting(final String pluginName, final String filePath,
            final long offset) {
        ViewerSetting vs = new ViewerSetting();
        vs.setPluginName(pluginName);
        vs.setFilePath(filePath);
        vs.setOffset(offset);

        viewerSettings.put(filePath, vs);

        changed = true;
    }

    /**
     * Remove a media file from the project.
     *
     * @param filePath The absolute path to the media file
     * @return the removed media file's viewer settings if it exists, null
     * otherwise.
     */
    public ViewerSetting removeViewerSetting(final String filePath) {
        ViewerSetting vs = viewerSettings.remove(filePath);
        if (vs != null) {
            changed = true;
        }
        return vs;
    }

    /**
     * @return True if the project is new, false otherwise.
     */
    public boolean isNewProject() {
        return newProject;
    }

    /**
     * Sets the new state of this project.
     *
     * @param isNewProject The new state to use for this project: True if the
     * project is new, false otherwise.
     */
    public void setNewProject(final boolean isNewProject) {
        this.newProject = isNewProject;
    }

    public void saveProject() {
        changed = false;
        newProject = false;
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
     * @param newId The Id of the newly created cell.
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
     * @param newId The id of hte newly selected cell.
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
     * @param newId The Id of the newly created column.
     */
    public void setLastCreatedColId(final long newId) {
        lastCreatedColID = newId;
    }
}
