package org.openshapa.controllers;

import com.usermetrix.jclient.UserMetrix;

import java.io.File;
import java.io.FileInputStream;

import java.util.zip.ZipInputStream;

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
    private UserMetrix logger = UserMetrix.getInstance(OpenC.class);

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
            FileInputStream fis = new FileInputStream(archiveFile);
            ZipInputStream zis = new ZipInputStream(fis);

            zis.getNextEntry();

            OpenProjectFileC opc = new OpenProjectFileC();
            project = opc.open(zis);

            zis.getNextEntry();

            OpenDatabaseFileC odc = new OpenDatabaseFileC();
            database = odc.openAsCSV(zis);

            // BugzID:1806
            for (ViewerSetting vs : project.getViewerSettings()) {

                if (vs.getSettingsId() != null) {
                    zis.getNextEntry();
                    vs.copySettings(zis);
                }
            }

            fis.close();
            zis.close();
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
