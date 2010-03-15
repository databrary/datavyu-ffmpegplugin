package org.openshapa.models.project;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a project in OpenSHAPA. A project manages the different
 * files used by OpenSHAPA, such as database files and media files.
 */
public final class Project implements Cloneable {

    /** Project specification version. */
    public static final int VERSION = 3;
    /** Name of this project. */
    private String projectName;
    /** Database file name. */
    private String databaseFileName;
    /** The directory that the project file resides in. */
    private String projectDirectory;

    private List<ViewerSetting> viewerSettings;

    private List<TrackSettings> interfaceSettings;

    /**
     * Constructor.
     */
    public Project() {
        viewerSettings = new LinkedList<ViewerSetting>();
        interfaceSettings = new LinkedList<TrackSettings>();
    }

    /**
     * Private copy constructor.
     * 
     * @param other
     */
    private Project(final Project other) {
        projectName = other.projectName;
        databaseFileName = other.databaseFileName;
        projectDirectory = other.projectDirectory;

        viewerSettings = new LinkedList<ViewerSetting>();
        for (ViewerSetting vs : other.viewerSettings) {
            viewerSettings.add(vs.clone());
        }

        interfaceSettings = new LinkedList<TrackSettings>();
        for (TrackSettings is : other.interfaceSettings) {
            interfaceSettings.add(is.clone());
        }
    }

    /**
     * @return The database file name. Does not include directory.
     */
    public String getDatabaseFileName() {
        return databaseFileName;
    }

    /**
     * @param fileName
     *            the database file name. Does not include directory.
     */
    public void setDatabaseFileName(final String fileName) {
        databaseFileName = fileName;
    }

    /**
     * @return The name of this project.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets the name of the project.
     * 
     * @param newProjectName
     *            The new name to use for this project.
     */
    public void setProjectName(final String newProjectName) {
        projectName = newProjectName;
    }

    public void setViewerSettings(final Iterable<ViewerSetting> viewerSettings) {
        if (viewerSettings != null) {
            this.viewerSettings = new LinkedList<ViewerSetting>();
            for (ViewerSetting viewerSetting : viewerSettings) {
                this.viewerSettings.add(viewerSetting);
            }
        }
    }

    public void setTrackSettings(
            final Iterable<TrackSettings> interfaceSettings) {
        if (interfaceSettings != null) {
            this.interfaceSettings = new LinkedList<TrackSettings>();
            for (TrackSettings interfaceSetting : interfaceSettings) {
                this.interfaceSettings.add(interfaceSetting);
            }
        }
    }

    /**
     * @return Viewer settings used for each media file being managed by
     *         OpenSHAPA.
     */
    public Iterable<ViewerSetting> getViewerSettings() {
        return viewerSettings;
    }

    public Iterable<TrackSettings> getTrackSettings() {
        return interfaceSettings;
    }

    /**
     * @return the projectDirectory
     */
    public String getProjectDirectory() {
        return projectDirectory;
    }

    /**
     * @param projectDirectory
     *            the projectDirectory to set
     */
    public void setProjectDirectory(final String projectDirectory) {
        this.projectDirectory = projectDirectory;
    }

    @Override
    public Project clone() {
        return new Project(this);
    }

}
