package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import java.io.File;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.openshapa.models.db.MacshapaDatabase;
import org.openshapa.models.project.Project;
import org.openshapa.models.project.ViewerSetting;


/**
 * Controller for opening OpenSHAPA databases and project files.
 */
public final class OpenC {

    /** A reference to the database that this controller opened. */
    private MacshapaDatabase database = null;

    /** A reference to the projec that this controller opened. */
    private Project project = null;

    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(OpenC.class);

    /**
     * Opens a file as a OpenSHAPA database.
     *
     * @param databaseFile The file to use when opening a file as a database.
     */
    public void openDatabase(final File databaseFile) {
        OpenDatabaseFileC odc = new OpenDatabaseFileC();
        database = odc.open(databaseFile);
    }

    /**
     * Opens a file as a OpenSHAPA database.
     *
     * @param databaseFile The file to use when opening a file as a database.
     */
    public void openDatabase(final String databaseFile) {
        this.openDatabase(new File(databaseFile));
    }

    /**
     * Opens a file as a OpenSHAPA project.
     *
     * @param projectFile The file to use when opening a file as a project.
     */
    public void openProject(final File projectFile) {

        // If project is archive - open it as such.
        if (projectFile.getName().endsWith(".opf")) {
            logger.usage("open project archive");
            openProjectArchive(projectFile);

            // Otherwise project is uncompressed.
        } else {
            logger.usage("open legacy shapa");

            OpenProjectFileC opc = new OpenProjectFileC();
            project = opc.open(projectFile);

            if (project != null) {
                OpenDatabaseFileC odc = new OpenDatabaseFileC();
                database = odc.open(new File(projectFile.getParent(),
                            project.getDatabaseFileName()));
            }
        }
    }

    /**
     * Opens a file as an OpenSHAPA archive.
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
            logger.error("Unable to open project archive", e);
        }
    }

    /**
     * Opens a file as a OpenSHAPA project.
     *
     * @param projectFile the file to use when opening a file as a project.
     */
    public void openProject(final String projectFile) {
        this.openProject(new File(projectFile));
    }

    /**
     * @return The instance of the macshapa database that was opened by this
     * controller, returns null if no database opened.
     */
    public MacshapaDatabase getDatabase() {
        return database;
    }

    /**
     * @return The instance of the project
     */
    public Project getProject() {
        return project;
    }
}
