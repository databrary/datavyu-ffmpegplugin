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
package org.openshapa.controllers.project;

import java.io.File;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.openshapa.OpenSHAPA;

import org.openshapa.controllers.component.MixerController;
import org.openshapa.controllers.id.IDController;

import org.openshapa.models.component.TrackModel;
import org.openshapa.models.project.Project;
import org.openshapa.models.project.TrackSettings;
import org.openshapa.models.project.ViewerSetting;

import org.openshapa.plugins.PluginManager;

import org.openshapa.util.OFileUtils;
import org.openshapa.util.HashUtils;

import org.openshapa.views.DataControllerV;

import com.google.common.collect.Lists;

import org.openshapa.models.db.Cell;
import org.openshapa.models.db.Datastore;
import org.openshapa.models.db.DeprecatedDatabase;

import org.openshapa.models.db.Variable;
import org.openshapa.plugins.DataViewer;
import org.openshapa.plugins.Plugin;


/**
 * This class is responsible for managing a project.
 */
public final class ProjectController {

    /** The current project we are working on. */
    private Project project;

    /** The current database we are working on. */
    private Datastore db = new DeprecatedDatabase();

    /** The id of the last datacell that was created. */
    @Deprecated private long lastCreatedCellID;

    /** The id of the last selected cell. */
    @Deprecated private long lastSelectedCellID;

    /** The id of the last datacell that was created. */
    @Deprecated private long lastCreatedColID;

    /** The last cell that was created. */
    private Cell lastCreatedCell;

    /** The last cell that was selected. */
    private Cell lastSelectedCell;

    /** The last variable that was created. */
    private Variable lastCreatedVariable;

    /**
     * Controller state
     */
    /** has the project been changed since it was created. */
    private boolean changed;

    /** Is the project new? */
    private boolean newProject;

    /** Last option used for saving. */
    private FileFilter lastSaveOption;

    /**
     * Default constructor.
     */
    public ProjectController() {
        db.setTitleNotifier(OpenSHAPA.getApplication());
        project = new Project();
        changed = false;
        newProject = true;
        lastCreatedCell = null;
        lastSelectedCell = null;
        lastCreatedVariable = null;
    }

    public ProjectController(final Project project) {
        db.setTitleNotifier(OpenSHAPA.getApplication());
        this.project = project;
        changed = false;
        newProject = false;
        lastCreatedCell = null;
        lastSelectedCell = null;
        lastCreatedVariable = null;
    }

    /**
     * Set the last save option used. This affects the "Save" functionality.
     *
     * @param saveOption The latest option used for "saving".
     */
    public void setLastSaveOption(final FileFilter saveOption) {
        lastSaveOption = saveOption;
    }

    /**
     * @return The last "saved" option used when saving.
     */
    public FileFilter getLastSaveOption() {
        return lastSaveOption;
    }

    public void createNewProject(final String name) {
        project = new Project();
        setProjectName(name);
        changed = false;
        newProject = true;
    }

    /**
     * Sets the name of the project.
     *
     * @param newProjectName
     *            The new name to use for this project.
     */
    public void setProjectName(final String newProjectName) {
        project.setProjectName(newProjectName);
    }

    /**
     * Gets the MacshapaDatabase associated with this project. Should eventually
     * be replaced with a Datastore.
     *
     * @return The single database to use with this project.
     */
    public Datastore getDB() {
        return db;
    }

    /**
     * @return The deprecated database.
     *
     * @deprecated Should be using getDB - we are moving away from the legacy
     * database.
     */
    @Deprecated public DeprecatedDatabase getLegacyDB() {
        return (DeprecatedDatabase) db;
    }

    /**
     * Sets the datastore to use with this project. This is used when loading a
     * database from file.
     * @param newDS
     */
    public void setDatastore(final Datastore newDS) {
        db = newDS;
    }

    /**
     * @return The last cell created for the datastore.
     */
    public Cell getLastCreatedCell() {
        return lastCreatedCell;
    }

    /**
     * Sets the last created cell to the specified parameter.
     *
     * @param newCell The newly created cell.
     */
    public void setLastCreatedCell(final Cell newCell) {
        lastCreatedCell = newCell;
    }

    /**
     * @return The last selected cell.
     */
    public Cell getLastSelectedCell() {
        return lastSelectedCell;
    }

    /**
     * Sets the last selected cell to the specified cell.
     *
     * @param newCell The newly selected cell.
     */
    public void setLastSelectedCell(final Cell newCell) {
        lastSelectedCell = newCell;
    }

    /**
     * @return The last variable created for the datastore.
     */
    public Variable getLastCreatedVariable() {
        return lastCreatedVariable;
    }

    /**
     * Sets the newly created variable to the specified parameter.
     *
     * @param newVariable The newly created variable.
     */
    public void setLastCreatedVariable(final Variable newVariable) {
        lastCreatedVariable = newVariable;
    }

    /**
     * @return The id of the last created cell.
     */
    @Deprecated public long getLastCreatedCellId() {
        return lastCreatedCellID;
    }

    /**
     * Sets the id of the last created cell to the specified parameter.
     *
     * @param newId
     *            The Id of the newly created cell.
     */
    @Deprecated public void setLastCreatedCellId(final long newId) {
        lastCreatedCellID = newId;
    }

    /**
     * @return The id of the last selected cell.
     */
    @Deprecated public long getLastSelectedCellId() {
        return lastSelectedCellID;
    }

    /**
     * Sets the id of the last selected cell to the specified parameter.
     *
     * @param newId The id of the newly selected cell.
     */
    @Deprecated public void setLastSelectedCellId(final long newId) {
        lastSelectedCellID = newId;
    }

    /**
     * @return The id of the last created column.
     */
    @Deprecated public long getLastCreatedColId() {
        return lastCreatedColID;
    }

    /**
     * Sets the id of the last created column to the specified parameter.
     *
     * @param newId
     *            The Id of the newly created column.
     */
    @Deprecated public void setLastCreatedColId(final long newId) {
        lastCreatedColID = newId;
    }

    /**
     * @return the changed
     */
    public boolean isChanged() {
        if (OpenSHAPA.getApplication().getCanSetUnsaved()) {
            return (changed || ((db != null) && db.isChanged()));
        } else {
            return false;
        }
    }

    /**
     * @return the newProject
     */
    public boolean isNewProject() {
        return newProject;
    }

    /**
     * @return the project name
     */
    public String getProjectName() {
        return project.getProjectName();
    }

    /**
     * Set the database file name, directory not included.
     *
     * @param fileName
     */
    public void setDatabaseFileName(final String fileName) {
        project.setDatabaseFileName(fileName);
    }

    /**
     * @return the database file name, directory not included.
     */
    public String getDatabaseFileName() {
        return project.getDatabaseFileName();
    }

    /**
     * Set the directory the project file (and all project specific resources)
     * resides in.
     *
     * @param directory
     */
    public void setProjectDirectory(final String directory) {
        project.setProjectDirectory(directory);
    }

    /**
     * @return the directory the project file (and all project specific
     *         resources) resides in.
     */
    public String getProjectDirectory() {
        return project.getProjectDirectory();
    }

    public void setOriginalProjectDirectory(final String directory) {
        project.setOriginalProjectDirectory(directory);
    }

    public String getOriginalProjectDirectory() {
        return project.getOriginalProjectDirectory();
    }

    /**
     * Load the settings from the current project.
     */
    public void loadProject() {

        // Use the plugin manager to load up the data viewers
        PluginManager pm = PluginManager.getInstance();
        DataControllerV dataController = OpenSHAPA.getDataController();

        // Load the plugins required for each media file
        boolean showController = false;

        List<String> missingFilesList = Lists.newLinkedList();
        List<String> missingPluginList = Lists.newLinkedList();

        final MixerController mixerController =
            dataController.getMixerController();

        // Load the viewer settings.
        for (ViewerSetting setting : project.getViewerSettings()) {
            showController = true;

            File file = new File(setting.getFilePath());

            File currentDir = new File(project.getProjectDirectory());
            String dataFileName = FilenameUtils.getName(setting.getFilePath());

            if (!file.exists()) {

                // Look for a file by generating OS-independent paths.
                File searchedFile = genRelative(
                        project.getOriginalProjectDirectory(),
                        setting.getFilePath(), project.getProjectDirectory());

                if (searchedFile != null) {
                    file = searchedFile;
                }
            }

            if (!file.exists()) {

                // Look for a file that _might_ be the file we are looking for.
                // This is a brute force search. Ideally we would never want to
                // do this.
                File searchedFile = huntForFile(currentDir, dataFileName);

                if (searchedFile != null) {
                    file = searchedFile;
                }
            }

            // The file is actually missing.
            if (!file.exists()) {
                missingFilesList.add(setting.getFilePath());

                continue;
            }

            // BugzID:2482
            // A file has been found. Now validate that it is the correct file.
            String originalDigest = setting.getDigest();
            if (originalDigest != null &! originalDigest.equals(HashUtils.computeDigest(file))) {
                missingFilesList.add(setting.getFilePath());
                continue;
            }
            
            Plugin plugin = pm.getAssociatedPlugin(setting.getPluginName());

            // BugzID:2110
            if ((plugin == null) && (setting.getPluginClassifier() != null)) {
                plugin = pm.getCompatiblePlugin(setting.getPluginClassifier(),
                        file);
            }

            if (plugin == null) {

                // Record missing plugin.
                missingPluginList.add(setting.getPluginName());

                continue;
            }

            final DataViewer viewer = plugin.getNewDataViewer(OpenSHAPA
                    .getApplication().getMainFrame(), false);
            viewer.setIdentifier(IDController.generateIdentifier());
            viewer.setDataFeed(file);
            viewer.setDatastore(db);

            if (setting.getSettingsId() != null) {

                // new project file
                viewer.loadSettings(setting.getSettingsInputStream());
            } else {

                // old project file
                viewer.setOffset(setting.getOffset());
            }

            dataController.addViewer(viewer, viewer.getOffset());

            dataController.addTrack(viewer.getIdentifier(),
                plugin.getTypeIcon(), file.getAbsolutePath(), file.getName(),
                viewer.getDuration(), viewer.getOffset(),
                viewer.getTrackPainter());

            if (setting.getTrackSettings() != null) {
                final TrackSettings ts = setting.getTrackSettings();
                mixerController.setTrackInterfaceSettings(viewer
                    .getIdentifier(), ts.getBookmarkPositions(), ts.isLocked());
            }

            mixerController.bindTrackActions(viewer.getIdentifier(),
                viewer.getCustomActions());
            viewer.addViewerStateListener(
                mixerController.getTracksEditorController()
                    .getViewerStateListener(viewer.getIdentifier()));
        }

        // Do not remove; this is here for backwards compatibility.
        for (TrackSettings setting : project.getTrackSettings()) {
            File file = new File(setting.getFilePath());

            if (!file.exists()) {

                // Look for a file by generating OS-independent paths.
                // This is not guaranteed for older project file formats.
                File searchedFile = genRelative(
                        project.getOriginalProjectDirectory(),
                        setting.getFilePath(), project.getProjectDirectory());

                if (searchedFile != null) {
                    file = searchedFile;
                }
            }

            if (!file.exists()) {

                // BugzID:1804 - If absolute path does not find the file, look
                // in the relative path (as long as we are dealing with a newer
                // project file type).
                if (project.getOriginalProjectDirectory() != null) {

                    File searchedFile = huntForFile(new File(
                                project.getProjectDirectory()), file.getName());

                    if (searchedFile != null) {
                        file = searchedFile;
                    }
                }
            }

            if (!file.exists()) {
                missingFilesList.add(setting.getFilePath());

                continue;
            }

            mixerController.setTrackInterfaceSettings(setting.getFilePath(),
                setting.getBookmarkPositions(), setting.isLocked());
        }

        if (!missingFilesList.isEmpty() || !missingPluginList.isEmpty()) {
            JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
            ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                .getContext().getResourceMap(OpenSHAPA.class);

            StringBuilder sb = new StringBuilder();

            if (!missingFilesList.isEmpty()) {
                sb.append("The following files are missing:\n\n");

                for (String filePath : missingFilesList) {
                    sb.append(filePath);
                    sb.append('\n');
                }
            }

            if (!missingPluginList.isEmpty()) {

                if (sb.length() != 0) {
                    sb.append('\n');
                }

                sb.append("The following plugins are missing:\n\n");

                for (String pluginName : missingPluginList) {
                    sb.append(pluginName);
                    sb.append('\n');
                }
            }

            JOptionPane.showMessageDialog(mainFrame, sb.toString(),
                rMap.getString("ProjectLoadError.title"),
                JOptionPane.WARNING_MESSAGE);

            showController = true;
        }

        // Show the data controller
        if (showController) {
            OpenSHAPA.getApplication().showDataController();
        }

    }

    /**
     * Gather and update the various project specific settings.
     */
    public void updateProject() {

        DataControllerV dataController = OpenSHAPA.getDataController();

        // Gather the data viewer settings
        List<ViewerSetting> viewerSettings = new LinkedList<ViewerSetting>();

        int settingsId = 1;

        for (DataViewer viewer : dataController.getDataViewers()) {
            ViewerSetting vs = new ViewerSetting();
            vs.setFilePath(viewer.getDataFeed().getAbsolutePath());
            vs.setPluginName(viewer.getClass().getName());

            // BugzID:2482
            String digest = HashUtils.computeDigest(new File(vs.getFilePath()));
            if (digest != null) {
                vs.setDigest(digest);
            }

            // BugzID:2108
            Plugin p = PluginManager.getInstance().getAssociatedPlugin(
                    vs.getPluginName());
            assert p.getClassifier() != null;
            assert !"".equals(p.getClassifier());

            vs.setPluginClassifier(p.getClassifier());

            // BugzID:1806
            vs.setSettingsId(Integer.toString(settingsId++));
            viewer.storeSettings(vs.getSettingsOutputStream());

            // BugzID:2107
            TrackModel tm = dataController.getMixerController().getTrackModel(
                    viewer.getIdentifier());
            TrackSettings ts = new TrackSettings();
            ts.setBookmarkPositions(tm.getBookmarks());
            ts.setLocked(tm.isLocked());

            vs.setTrackSettings(ts);

            viewerSettings.add(vs);
        }

        project.setViewerSettings(viewerSettings);
    }

    /**
     * Marks the project state as being saved.
     */
    public void markProjectAsUnchanged() {
        changed = false;
        newProject = false;
    }

    /**
     * Marks the project as being changed. This method will not trigger a
     * project state update.
     */
    public void projectChanged() {
        changed = true;

        OpenSHAPA.getApplication().updateTitle();
    }

    /**
     * @return the current project model.
     */
    public Project getProject() {
        return project;
    }

    private File genRelative(final String originalDir,
        final String originalFilePath, final String currentDir) {

        // 1. Find the longest common directory for the original dir and
        // original file path.
        String baseLCD = OFileUtils.longestCommonDir(originalDir,
                originalFilePath);

        if (baseLCD == null) {
            return null;
        }

        // 2. Use the longest common directory to find the difference in
        // directory levels with the original directory. The LCD is the original
        // base dir.
        int diff = OFileUtils.levelDifference(baseLCD, originalDir);

        if (diff == -1) {
            return null;
        }

        // 3. Use the difference in levels to generate a new base directory
        // using the current directory.
        File newBase = new File(currentDir);

        while (diff > 0) {
            newBase = newBase.getParentFile();

            if (newBase == null) {
                return null;
            }

            diff--;
        }

        // 4. Find the path relative to the original base directory for the
        // original file path.
        String rel = OFileUtils.relativeToBase(baseLCD, originalFilePath);

        if (rel == null) {
            return null;
        }

        // 5. Combine the relative path with the current base dir and return
        // that as the file to try.
        return new File(newBase, rel);
    }

    private File huntForFile(final File workingDir, final String fileName) {
        // If we can't find the file, we will start looking for the file
        // using the easiest solution first and bump up the complexity as
        // we go along.

        // Solution 1: It is in the same directory as the project file.
        File file = new File(workingDir, fileName);

        if (file.exists()) {
            return file;
        }

        IOFileFilter fileNameFilter = FileFilterUtils.nameFileFilter(fileName);

        // Solution 2: It is in a sub-directory of the project file.
        {
            Iterator<File> subFiles = FileUtils.iterateFiles(workingDir,
                    fileNameFilter, TrueFileFilter.TRUE);

            if (subFiles.hasNext()) {
                file = subFiles.next();
            }

            if (file.exists()) {
                return file;
            }
        }


        // Solution 3: It is in the parent of the current directory.
        {
            Iterator<File> subFiles = FileUtils.iterateFiles(
                    workingDir.getParentFile(), fileNameFilter, null);

            if (subFiles.hasNext()) {
                file = subFiles.next();
            }

            if (file.exists()) {
                return file;
            }
        }


        // Solution 4: It is in an adjacent directory.
        {
            File parent = workingDir.getParentFile();

            if (parent != null) {
                Iterator<File> subFiles = FileUtils.iterateFiles(parent,
                        fileNameFilter, TrueFileFilter.TRUE);

                if (subFiles.hasNext()) {
                    file = subFiles.next();
                }
            }

            if (file.exists()) {
                return file;
            }
        }

        return null;
    }

}
