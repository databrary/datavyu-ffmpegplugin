package org.openshapa.controllers;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import org.openshapa.util.HashUtils;

import com.usermetrix.jclient.UserMetrix;
import org.openshapa.models.db.LogicErrorException;
import org.openshapa.models.db.MacshapaDatabase;
import org.openshapa.models.project.Project;

/**
 * Master controller for handling project and database file saving logic.
 */
public final class SaveC {

    /** The logger for this class. */
    private final UserMetrix logger = UserMetrix.getInstance(SaveC.class);

    /** The length of the SHA-1 sum to put at the end of CSV files. */
    private static final int HASH_LENGTH = 10;

    /**
     * Saves only a database to disk.
     *
     * @param databaseFile The location to save the database too.
     * @param database The database to save to disk.
     *
     * @throws LogicErrorException If unable to save the database.
     */
    public void saveDatabase(final String databaseFile,
                             final MacshapaDatabase database)
    throws LogicErrorException {
        this.saveDatabase(new File(databaseFile), database);
    }

    /**
     * Saves only a database to disk.
     *
     * @param databaseFile The location to save the database too.
     * @param database The database to save to disk.
     *
     * @throws LogicErrorException If unable to save the database.
     */
    public void saveDatabase(final File databaseFile,
                             final MacshapaDatabase database)
    throws LogicErrorException {
        SaveDatabaseFileC saveDBC = new SaveDatabaseFileC();
            saveDBC.saveDatabase(databaseFile, database);

    }

    /**
     * Saves an entire project, including database to disk.
     *
     * @param projectDir The destination directory to save the project too.
     * @param projectFile The destination to save the project too.
     * @param project The project to save to disk.
     * @param database The database to save to disk.
     *
     * @throws LogicErrorException If unable to save the entire project to
     * disk.
     */
    public void saveProject(final String projectDir,
                            final String projectFile,
                            final Project project,
                            final MacshapaDatabase database)
    throws LogicErrorException {
        this.saveProject(new File(projectDir),
                         new File(projectFile),
                         project,
                         database);
    }

    /**
     * Saves an entire project, including database to disk.
     *
     * @param projectDir The destination directory to save the project too.
     * @param projectFile The destination to save the project too.
     * @param project The project to save to disk.
     * @param database The database to save to disk.
     *
     * @throws LogicErrorException If unable to save the entire project to
     * disk.
     */
    public void saveProject(final File projectDir,
                            final File projectFile,
                            final Project project,
                            final MacshapaDatabase database)
    throws LogicErrorException {
        project.setProjectDirectory(projectDir.toString());
        project.setProjectName(projectFile.toString());

        // Compute the database file name
        String databaseFileName = project.getProjectName();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(project.getProjectName().getBytes());
            byte[] digest = md.digest();
            String stringDigest = HashUtils.convertToHex(digest);
            databaseFileName += "-" + stringDigest.substring(0, HASH_LENGTH);
            databaseFileName = databaseFileName.concat(".csv");

        } catch (NoSuchAlgorithmException ex) {
            logger.error("Could not get SHA-1 implementation", ex);
            // Abort. Shouldn't happen. If it does, don't risk overwriting.
            return;
        }

        File dbFile = new File(project.getProjectDirectory() + "/"
                               + databaseFileName);
        project.setDatabaseFileName(databaseFileName);

        new SaveDatabaseFileC().saveDatabase(dbFile, database);
        new SaveProjectFileC().save(project.getProjectDirectory() + "/"
                                    + projectFile, project);
    }
}
