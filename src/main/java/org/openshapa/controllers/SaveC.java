package org.openshapa.controllers;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.filechooser.FileFilter;

import org.openshapa.OpenSHAPA;
import org.openshapa.controllers.project.ProjectController;
import org.openshapa.util.HashUtils;
import org.openshapa.util.FileFilters.SHAPAFilter;

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
     * @param projectFile The destination to save the project too.
     * @param project The project to save to disk.
     * @param database The database to save to disk.
     *
     * @throws LogicErrorException If unable to save the entire project to
     * disk.
     */
    public void saveProject(final String projectFile,
                            final Project project,
                            final MacshapaDatabase database)
    throws LogicErrorException {
        this.saveProject(new File(projectFile), project, database);
    }

    /**
     * Saves an entire project, including database to disk.
     *
     * @param projectFile The destination to save the project too.
     * @param project The project to save to disk.
     * @param database The database to save to disk.
     *
     * @throws LogicErrorException If unable to save the entire project to
     * disk.
     */
    public void saveProject(final File databaseFile,
                            final Project project,
                            final MacshapaDatabase database)
    throws LogicErrorException {

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

        new SaveDatabaseFileC().saveDatabase(dbFile, database);
        new SaveProjectFileC().save(project.getProjectDirectory() + "/"
                                    + project.getProjectName(), project);
    }

    /**
     * Save the currently opened project and database. Enforce database naming
     * rule: [project name]-[SHA-1(project name).substring(0, 10)].csv
     *
     * @deprecated
     */
    public void saveProject() throws LogicErrorException {
        ProjectController projectController = OpenSHAPA.getProjectController();
        final String projectName = projectController.getProjectName();

        // Compute the database file name
        String databaseFileName = projectName;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(projectName.getBytes());
            byte[] digest = md.digest();
            String stringDigest = HashUtils.convertToHex(digest);
            databaseFileName += "-" + stringDigest.substring(0, HASH_LENGTH);
            databaseFileName = databaseFileName.concat(".csv");
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Could not get SHA-1 implementation", ex);
            /*
             * Stop here, this should not happen, but in case it does don't risk
             * overwriting.\
             */
            return;
        }

        // Update the project data
        projectController.setDatabaseFileName(databaseFileName);


        // Save the database
        File dbFile =
                new File(projectController.getProjectDirectory() + "/"
                        + databaseFileName);
        //new SaveDatabaseC(dbFile, projectController.getDB());
        SaveDatabaseFileC saveDatabaseC = new SaveDatabaseFileC();
        saveDatabaseC.saveDatabase(dbFile, projectController.getDB());
        projectController.getDB().markAsUnchanged();

        // Now save the project
        projectController.updateProject();
        new SaveProjectFileC().save(projectController.getProjectDirectory() + "/"
                + projectName, projectController.getProject());
        projectController.markProjectAsUnchanged();

        // Update the application title
        OpenSHAPA.getApplication().updateTitle();
    }

    /**
     * Save what is being worked on as a new project.
     *
     * @param directory The directory to save the project too.
     * @param file The file to save the project too.
     */
    public void saveAsProject(final String directory, final String file)
    throws LogicErrorException {
        /*
         * First, check if the destination PROJECT file exists. We do not care
         * if the target database exists or not.
         */
        String newProjectName = file;
        /*
         * Find out the new name of the project, build the output project file
         * name.
         */
        if (newProjectName.endsWith(".shapa")) {
            int extensionIndex = newProjectName.lastIndexOf(".shapa");
            newProjectName = newProjectName.substring(0, extensionIndex);
        }
        if (newProjectName.length() == 0) {
            logger.error("Invalid file name supplied.");
            return;
        }
        final String projectFileName = newProjectName.concat(".shapa");
        File projectFile = new File(directory, projectFileName);
        /*
         * Do the save if the project file does not exists or if the user
         * confirms a file overwrite in the case that the file exists.
         */
        boolean doSave =
                (!projectFile.exists() || (projectFile.exists() && OpenSHAPA
                        .getApplication().overwriteExisting()));
        // Stop the save process if user does not want to save.
        if (!doSave) {
            return;
        }

        // We have the new project name, calculate new database file name
        String databaseFileName = newProjectName;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(newProjectName.getBytes());
            byte[] digest = md.digest();
            String stringDigest = HashUtils.convertToHex(digest);
            databaseFileName += "-" + stringDigest.substring(0, HASH_LENGTH);
            databaseFileName = databaseFileName.concat(".csv");
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Could not get SHA-1 implementation", ex);
            /*
             * Stop here, this should not happen, but in case it does don't risk
             * overwriting.\
             */
            return;
        }

        ProjectController projectController = OpenSHAPA.getProjectController();
        projectController.setProjectName(newProjectName);
        projectController.setProjectDirectory(directory);
        projectController.setDatabaseFileName(databaseFileName);
        projectController.setLastSaveOption(new SHAPAFilter());

        // Save the database
        SaveDatabaseFileC saveDatabaseC = new SaveDatabaseFileC();
        saveDatabaseC.saveDatabase(new File(directory, databaseFileName),
                                   projectController.getDB());
        projectController.getDB().markAsUnchanged();

        // Save the project
        projectController.updateProject();
        new SaveProjectFileC().save(directory + "/" + newProjectName,
                                projectController.getProject());
        projectController.markProjectAsUnchanged();
        OpenSHAPA.getApplication().updateTitle();
    }

    /**
     * Save what is worked on as a new database.
     *
     * @param directory The directory to save the database too.
     * @param file The name of the file to save the database too.
     * @param saveFormat The format to use when saving the database.
     *
     * @deprecated
     */
    public void saveAsDatabase(final String directory,
                               final String file,
                               final FileFilter saveFormat)
    throws LogicErrorException {
        /*
         * Even though the user explicitly chooses to save as a database, we
         * will still need to update the project information, just in case the
         * user decides to save as a project. We will only update the project
         * name. The project name will be the file name minus any extension, if
         * applicable.
         */
        String newProjectName = file;
        int extensionIndex = file.lastIndexOf(".");
        if (extensionIndex != -1) {
            newProjectName = newProjectName.substring(0, extensionIndex);
        }

        if (newProjectName.length() == 0) {
            logger.error("Invalid file name supplied.");
            return;
        }

        ProjectController projectController = OpenSHAPA.getProjectController();
        projectController.setProjectName(newProjectName);
        projectController.setProjectDirectory(directory);
        projectController.setDatabaseFileName(file);
        projectController.markProjectAsUnchanged();
        projectController.setLastSaveOption(saveFormat);

        SaveDatabaseFileC saveDBC = new SaveDatabaseFileC();
        saveDBC.saveDatabase(directory + "/" + file,
                             saveFormat,
                             projectController.getDB());
        projectController.getDB().markAsUnchanged();
    }
}
