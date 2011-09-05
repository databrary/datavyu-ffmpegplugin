package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import database.LogicErrorException;
import database.MacshapaDatabase;
import org.openshapa.models.project.Project;
import org.openshapa.models.project.ViewerSetting;

import com.usermetrix.jclient.UserMetrix;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.openshapa.OpenSHAPA;
import org.openshapa.RecentFiles;


/**
 * Master controller for handling project and database file saving logic.
 */
public final class SaveC {

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(SaveC.class);

    /**
     * Saves only a database to disk.
     *
     * @param databaseFile The location to save the database too.
     * @param database The database to save to disk.
     *
     * @throws LogicErrorException If unable to save the database.
     */
    public void saveDatabase(final String databaseFile,
        final MacshapaDatabase database) throws LogicErrorException {
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
        final MacshapaDatabase database) throws LogicErrorException {
            saveDatabase(databaseFile, database, true);
    }

    /**
     * Saves only a database to disk.
     *
     * @param databaseFile The location to save the database too.
     * @param database The database to save to disk.
     * @param remember Add this project to the rememberProject list.
     * @throws LogicErrorException If unable to save the database.
     */
    public void saveDatabase(final File databaseFile,
        final MacshapaDatabase database, boolean remember) throws LogicErrorException {
        LOGGER.event("saving database");

        SaveDatabaseFileC saveDBC = new SaveDatabaseFileC();
        saveDBC.saveDatabase(databaseFile, database);
        if (remember) {
            RecentFiles.rememberProject(databaseFile);
        }    
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
    public void saveProject(final File projectFile, final Project project,
        final MacshapaDatabase database) throws LogicErrorException {
        saveProject(projectFile, project, database, true);
    }

    /**
     * Saves an entire project, including database to disk.
     *
     * @param projectFile The destination to save the project too.
     * @param project The project to save to disk.
     * @param database The database to save to disk.
     * @param remember Add this project to the rememberProject list.
     * 
     * @throws LogicErrorException If unable to save the entire project to
     * disk.
     */
    public void saveProject(final File projectFile, final Project project,
        final MacshapaDatabase database, boolean remember) throws LogicErrorException {

        try {
            LOGGER.event("save project");

            FileOutputStream fos = new FileOutputStream(projectFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            ZipEntry projectEntry = new ZipEntry("project");
            zos.putNextEntry(projectEntry);
            new SaveProjectFileC().save(zos, project);
            zos.closeEntry();

            ZipEntry dbEntry = new ZipEntry("db");
            zos.putNextEntry(dbEntry);
            new SaveDatabaseFileC().saveAsCSV(zos, database);
            zos.closeEntry();

            // BugzID:1806
            for (ViewerSetting vs : project.getViewerSettings()) {
                ZipEntry vsEntry = new ZipEntry(vs.getSettingsId());
                zos.putNextEntry(vsEntry);
                vs.writeSettings(zos);
            }

            zos.finish();
            zos.close();

            fos.flush();
            fos.close();

            if (remember) {
                RecentFiles.rememberProject(projectFile);
            }
            
        } catch (FileNotFoundException e) {
            ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                .getContext().getResourceMap(OpenSHAPA.class);
            throw new LogicErrorException(rMap.getString("UnableToSave.message",
                    projectFile), e);
        } catch (IOException e) {
            ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                .getContext().getResourceMap(OpenSHAPA.class);
            throw new LogicErrorException(rMap.getString("UnableToSave.message",
                    projectFile), e);
        }
    }
    
}
