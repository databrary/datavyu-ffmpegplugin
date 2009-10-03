package org.openshapa.views.continuous;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.log4j.Logger;

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
                // Build the list of unitTests to perform.
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL resource = loader.getResource("");
            if (resource == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }

            // The classloader references a jar - open the jar file up and
            // iterate through all the entries and add the entries that are
            // concrete unit tests and add them to our list of tests to perform.
            if (resource.getFile().contains(".jar!")) {
                String file = resource.getFile();
                file = file.substring(0, file.indexOf("!"));
                URI uri = new URI(file);
                File f = new File(uri);
                JarFile jar = new JarFile(f);

                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    //addTest(unitTests, entries.nextElement().getName());
                }


            // The classloader references a bunch of .class files on disk,
            // recusively inspect contents of each of the resources. If it is
            // a directory at it to our workStack, otherwise check to see if it
            // is a concrete unit tests and add it to our list of tests to
            // perform.
            } else {
                Stack<File> workStack = new Stack<File>();
                workStack.push(new File(resource.getFile()));

                Stack<String> packages = new Stack<String>();
                packages.push("");

                while (!workStack.empty()) {
                    File dir = workStack.pop();
                    String pkgName = packages.pop();

                    // For each of the children of the directory - look for
                    // tests or more directories to recurse inside.
                    String[] files = dir.list();
                    for (int i = 0; i < files.length; i++) {
                        File file = new File(dir.getAbsolutePath() + "/"
                                             + files[i]);
                        if (file == null) {
                            throw new ClassNotFoundException("Null file");
                        }

                        // If the file is a directory - add it to our work list.
                        if (file.isDirectory()) {
                            workStack.push(file);
                            packages.push(pkgName + file.getName() + ".");

                        // If the file ends with Test.class - it is a unit test,
                        // add it to our list of tests.
                        } else {
                            //addTest(unitTests, pkgName.concat(files[i]));
                        }
                    }
                }
            }

        // Whoops - something went bad. Chuck a spaz.
        } catch (ClassNotFoundException e) {
            logger.error("Unable to build unit test", e);
        } catch (IOException ie) {
            logger.error("Unable to load jar file", ie);
        } catch (URISyntaxException se) {
            logger.error("Unable to build path to jar file", se);
        }
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
    private List<Plugin> supportedFileTypes;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(PluginManager.class);
}
