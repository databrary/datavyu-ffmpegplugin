package org.openshapa.models.project;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a project in OpenSHAPA. A project manages the different
 * files used by OpenSHAPA, such as database files and media files.
 */
public class Project {

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
    /**
     * Key   : file path
     * Value : viewer settings for the medial file
     */
    private Map<String, ViewerSetting> viewerSettings;
    /** has this project been changed since it was created. */
    private boolean changed;
    /** Is this a new project? */
    private boolean newProject;

    public Project() {
        viewerSettings = new HashMap();
        changed = false;
        newProject = true;
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

    public String getProjectName() {
        return projectName;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void setProjectName(String projectName) {
        assert(projectName != null);
        String name = projectName;
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

    public boolean isNewProject() {
        return newProject;
    }

    public void setNewProject(boolean newProject) {
        this.newProject = newProject;
    }

    public void saveProject() {
        changed = false;
        newProject = false;
    }

}
