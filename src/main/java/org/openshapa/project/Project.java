package org.openshapa.project;

import java.util.HashMap;
import java.util.Map;
import org.openshapa.OpenSHAPA;

/**
 * This class represents a project in OpenSHAPA. A project manages the different
 * files used by OpenSHAPA, such as database files and media files.
 */
public class Project {

    /** Project specification version */
    public static final int VERSION = 1;
    /** name of this project */
    private String projectName;
    /** directory the database is stored */
    private String databaseDir;
    /** database file name */
    private String databaseFile;
    /**
     * Key   : file path
     * Value : viewer settings for the medial file
     */
    private Map<String, ViewerSetting> viewerSettings;
    /** has this project been changed since it was created. */
    private boolean changed;
    
    public Project() {
        viewerSettings = new HashMap();
        changed = false;
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
    }

    public void setDatabaseFile(String databaseFile) {
        this.databaseFile = databaseFile;
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
        int match = name.lastIndexOf(".openshapa");
        if (match != -1) {
            name = name.substring(0, match);
        }
        if (name.equals("")) {
            name = "Project1";
        }
        this.projectName = name;
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

    public void saveProject() {
        changed = false;
        OpenSHAPA.getApplication().updateTitle();
    }

}
