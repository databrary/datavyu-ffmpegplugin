/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.datavyu.models.project;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;


/**
 * This class represents a project in Datavyu. A project manages the different
 * files used by Datavyu, such as database files and media files.
 */
public final class Project {

    /** Project specification version. */
    public static final int VERSION = 5;

    /** Name of this project. */
    private String projectName;

    /** Database file name. */
    private String databaseFileName;

    /** The directory that the project file resides in. */
    private String projectDirectory;

    /** The directory that the project file was saved to. Could be the same as
     * the project directory, and could importantly be blank in the case of
     * loading older project files. */
    private String originalProjectDirectory;

    private List<ViewerSetting> viewerSettings;

    @Deprecated private List<TrackSettings> interfaceSettings;

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
        originalProjectDirectory = other.originalProjectDirectory;

        viewerSettings = new LinkedList<ViewerSetting>();

        for (ViewerSetting vs : other.viewerSettings) {
            viewerSettings.add(vs.copy());
        }

        interfaceSettings = new LinkedList<TrackSettings>();

        for (TrackSettings is : other.interfaceSettings) {
            interfaceSettings.add(is.copy());
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

        // Check Pre-conditions.
        assert (newProjectName != null);

        // Set the name of the project.
        String name = FilenameUtils.removeExtension(FilenameUtils.getName(
                    newProjectName));

        if ("".equals(name)) {
            name = "Project1";
        }

        projectName = name;
    }

    public void setViewerSettings(
        final Iterable<ViewerSetting> viewerSettings) {

        if (viewerSettings != null) {
            this.viewerSettings = new LinkedList<ViewerSetting>();

            for (ViewerSetting viewerSetting : viewerSettings) {
                this.viewerSettings.add(viewerSetting);
            }
        }
    }

    @Deprecated public void setTrackSettings(
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
     *         Datavyu.
     */
    public Iterable<ViewerSetting> getViewerSettings() {
        return viewerSettings;
    }

    @Deprecated public Iterable<TrackSettings> getTrackSettings() {
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

    /** @return the directory the project file was saved to. */
    public String getOriginalProjectDirectory() {
        return originalProjectDirectory;
    }

    /** @param originalProjectDirectory sets the directory the project file was
     * saved to.
     */
    public void setOriginalProjectDirectory(
        final String originalProjectDirectory) {
        this.originalProjectDirectory = originalProjectDirectory;
    }

    public Project copy() {
        return new Project(this);
    }

}
