package org.openshapa.views.continuous;

import java.io.FileFilter;
import java.util.List;

/**
 *
 */
public class PluginManager {

    /**
     * @return The single instance of the PluginManager object in OpenSHAPA.
     */
    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }

        return instance;
    }

    /**
     * Default constructor.
     */
    private PluginManager() {
        // Perform reflection - searching for valid plugins... Should look for
        // jar files in a plugin directory inside OpenSHAPA and the classpath
        // looking for packages that contain classes that implement the plugin
        // interface files.
        //
        // Populates the internal list of supported file types.
    }

    /**
     * @return A list of all the filefilters representing viewer plugins.
     */
    //List<FileFilter> getPluginFileFilters();

    /**
     * Creates the correct kind of viewer from the supplied file. This will
     * prompt the user to clarify, if multiple viewers are availble for a single
     * file type (i.e. many viewers may support CSV files.)
     *
     * @param dataFile The dataFile that you wish to create a viewer for.
     *
     * @return A valid data viewer for the supplied file.
     */
    //DataViewer buildViewerFromFile(final File dataFile);

    /** The single instance of the PluginManager for OpenSHAPA. */
    private static PluginManager instance = null;

    /** The list of supported file types. */
    private List<FileFilter> supportedFileTypes;
}
