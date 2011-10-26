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
package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import database.MacshapaDatabase;
import org.openshapa.models.project.Project;
import org.openshapa.models.project.ViewerSetting;

import com.usermetrix.jclient.UserMetrix;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.openshapa.OpenSHAPA;
import org.openshapa.RecentFiles;
import org.openshapa.models.db.UserWarningException;


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
     * @throws UswerWarningException If unable to save the database.
     */
    public void saveDatabase(final String databaseFile,
                             final MacshapaDatabase database)
    throws UserWarningException {
        this.saveDatabase(new File(databaseFile), database);
    }

    /**
     * Saves only a database to disk.
     *
     * @param databaseFile The location to save the database too.
     * @param database The database to save to disk.
     *
     * @throws UserWarningException If unable to save the database.
     */
    public void saveDatabase(final File databaseFile,
                             final MacshapaDatabase database)
    throws UserWarningException {
            saveDatabase(databaseFile, database, true);
    }

    /**
     * Saves only a database to disk.
     *
     * @param databaseFile The location to save the database too.
     * @param database The database to save to disk.
     * @param remember Add this project to the rememberProject list.
     * @throws UserWarningException If unable to save the database.
     */
    public void saveDatabase(final File databaseFile,
                             final MacshapaDatabase database,
                             boolean remember)
    throws UserWarningException {
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
     * @throws UserWarningException If unable to save the entire project to
     * disk.
     */
    public void saveProject(final File projectFile,
                            final Project project,
                            final MacshapaDatabase database)
    throws UserWarningException {
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
     * @throws UserWarningException If unable to save the entire project to
     * disk.
     */
    public void saveProject(final File projectFile,
                            final Project project,
                            final MacshapaDatabase database,
                            boolean remember) throws UserWarningException {

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
            throw new UserWarningException(rMap.getString("UnableToSave.message", projectFile), e);
        } catch (IOException e) {
            ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                          .getContext().getResourceMap(OpenSHAPA.class);
            throw new UserWarningException(rMap.getString("UnableToSave.message", projectFile), e);
        }
    }
    
}
