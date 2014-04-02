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
package org.datavyu.controllers;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import org.datavyu.models.db.Datastore;
import org.datavyu.models.project.Project;
import org.datavyu.models.project.ViewerSetting;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * Controller for opening Datavyu databases and project files.
 */
public final class OpenC {

    /**
     * A reference to the database that this controller opened.
     */
    private Datastore database = null;

    /**
     * A reference to the projec that this controller opened.
     */
    private Project project = null;

    /**
     * The logger for this class.
     */
    private static Logger LOGGER = UserMetrix.getLogger(OpenC.class);

    /**
     * Opens a file as a Datavyu database.
     *
     * @param databaseFile The file to use when opening a file as a database.
     */
    public void openDatabase(final File databaseFile) {
        OpenDatabaseFileC odc = new OpenDatabaseFileC();
        database = odc.open(databaseFile);

        database.deselectAll();
    }

    /**
     * Opens a file as a Datavyu database.
     *
     * @param databaseFile The file to use when opening a file as a database.
     */
    public void openDatabase(final String databaseFile) {
        this.openDatabase(new File(databaseFile));
    }

    /**
     * Opens a file as a Datavyu project.
     *
     * @param projectFile The file to use when opening a file as a project.
     */
    public void openProject(final File projectFile) {

        // If project is archive - open it as such.
        if (projectFile.getName().endsWith(".opf")) {
            LOGGER.event("open project archive");
            openProjectArchive(projectFile);

            // Otherwise project is uncompressed.
        } else {
            LOGGER.event("open legacy shapa");

            OpenProjectFileC opc = new OpenProjectFileC();
            project = opc.open(projectFile);

            if (project != null) {
                OpenDatabaseFileC odc = new OpenDatabaseFileC();
                database = odc.open(new File(projectFile.getParent(),
                        project.getDatabaseFileName()));
            }
        }
        database.setName(projectFile.getName());

        database.deselectAll();
    }

    /**
     * Opens a file as a Datavyu archive.
     *
     * @param archiveFile The archive to open as a project.
     */
    private void openProjectArchive(final File archiveFile) {

        try {
            ZipFile zf = new ZipFile(archiveFile);

            String arch = archiveFile.getName().substring(0,
                    archiveFile.getName().lastIndexOf('.'));
            ZipEntry zProj = zf.getEntry("project");

            // BugzID:1941 - Older project files are nested within a directory.
            // Try in the nested location if unable to find a project.
            if (zProj == null) {
                zProj = zf.getEntry(arch + File.separator + "project");
            }

            OpenProjectFileC opc = new OpenProjectFileC();
            project = opc.open(zf.getInputStream(zProj));

            ZipEntry zDb = zf.getEntry("db");

            // BugzID:1941 - Older database files are nested within a directory
            // Try in the nested location if unable to find a project.
            if (zDb == null) {
                zDb = zf.getEntry(arch + File.separator + "db");
            }

            OpenDatabaseFileC odc = new OpenDatabaseFileC();
            database = odc.openAsCSV(zf.getInputStream(zDb));

            // BugzID:1806
            for (ViewerSetting vs : project.getViewerSettings()) {

                if (vs.getSettingsId() != null) {
                    ZipEntry entry = zf.getEntry(vs.getSettingsId());
                    vs.copySettings(zf.getInputStream(entry));
                }
            }

            zf.close();
        } catch (Exception e) {
            LOGGER.error("Unable to open project archive", e);
            e.printStackTrace();
        }

        database.deselectAll();
    }

    /**
     * Opens a file as a Datavyu project.
     *
     * @param projectFile the file to use when opening a file as a project.
     */
    public void openProject(final String projectFile) {
        this.openProject(new File(projectFile));
    }

    /**
     * @return The instance of the datastore that was opened by this controller,
     * returns null if no database opened.
     */
    public Datastore getDatastore() {
        return database;
    }

    /**
     * @return The instance of the project
     */
    public Project getProject() {
        return project;
    }
}
