package org.openshapa.views.continuous;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;
import org.jdesktop.application.LocalStorage;
import org.openshapa.OpenSHAPA;

/**
 * This class manages and wrangles all the viewer plugins currently availble to
 * OpenSHAPA. It is implemented as a singleton, so only one instance is
 * available to OpenSHAPA - this single instance will hunt down and load all
 * plugins that implement the Plugin interface.
 */
public final class PluginManager {

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
        // Search for valid plugins... Current scans the classpath looking for
        // classes that implement the plugin interface.
        try {
            this.availablePlugins = new ArrayList<Plugin>();
            plugin = Class.forName("org.openshapa.views.continuous.Plugin");

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL resource = loader.getResource("");

            // The classloader references a jar - open the jar file up and
            // iterate through all the entries and add the entries that are
            // concrete Plugins.
            if (resource == null) {
                resource = loader.getResource("org/openshapa");
                if (resource == null) {
                    throw new ClassNotFoundException("Can't get class loader.");
                }

                String file = resource.getFile();
                file = file.substring(0, file.indexOf("!"));
                URI uri = new URI(file);
                File f = new File(uri);
                JarFile jar = new JarFile(f);

                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    String name = entries.nextElement().getName();
                    if (name.endsWith(".class")) {
                        addPlugin(name);
                    }
                }

            // The classloader references a bunch of .class files on disk,
            // recusively inspect contents of each of the resources. If it is
            // a directory at it to our workStack, otherwise check to see if it
            // is a concrete plugin.
            } else {
                Stack<File> workStack = new Stack<File>();
                workStack.push(new File(resource.getFile()));

                Stack<String> packages = new Stack<String>();
                packages.push("");

                while (!workStack.empty()) {
                    File dir = workStack.pop();
                    String pkgName = packages.pop();

                    // For each of the children of the directory - look for
                    // Plugins or more directories to recurse inside.
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

                        // If we are dealling with a class file - attempt to add
                        // it to our list of plugins.
                        } else if (files[i].endsWith(".class")) {
                            addPlugin(pkgName.concat(files[i]));
                        }
                    }
                }
            }

            // We have scaned the OpenSHAPA classpath - but we should also look
            // in the "plugins" directory for jar files that correctly conform
            // to the OpenSHAPA plugin interface.
            LocalStorage ls = OpenSHAPA.getApplication()
                                       .getContext().getLocalStorage();
            File pluginDir = new File(ls.getDirectory().toString()
                                      + "/plugins");
            // Unable to find plugin directory or any entries within the plugin
            // directory - don't bother attempting to add more plugins to
            // OpenSHAPA.
            if (pluginDir == null || pluginDir.list() == null) {
                return;
            }

            // For each of the files in the plugin directory - check to see if
            // they confirm to the plugin interface.
            for (String file : pluginDir.list()) {
                File f = new File(pluginDir.getAbsolutePath() + "/" + file);
                if (file == null) {
                    throw new ClassNotFoundException("Null file");

                // File is a jar file - crack it open and look for plugins!
                } else if (file.endsWith(".jar")) {
                    this.injectPlugin(f);
                    JarFile jar = new JarFile(f);

                    // For each file in the jar file check to see if it could be
                    // a plugin.
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement().getName();

                        // Found a class file - attempt to add it as a plugin.
                        if (name.endsWith(".class")) {
                            addPlugin(name);
                        }
                    }
                }
            }

        // Whoops - something went bad. Chuck a spaz.
        } catch (ClassNotFoundException e) {
            logger.error("Unable to build Plugin", e);
        } catch (IOException ie) {
            logger.error("Unable to load jar file", ie);
        } catch (URISyntaxException se) {
            logger.error("Unable to build path to jar file", se);
        }
    }

    /**
     * Injects A plugin into the classpath.
     *
     * @param f The jar file to inject into the classpath.
     *
     * @throws IOException If unable to inject the plugin into the class path.
     */
    private void injectPlugin(final File f) throws IOException {
        URLClassLoader sysLoader = (URLClassLoader) ClassLoader
                                                    .getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Class[] parameters = new Class[]{URL.class};
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysLoader, new Object[] {f.toURL()});
        } catch (Throwable t) {
            logger.error("Unable to inject class into class path.", t);
        }
    }

    /**
     * Attempts to add an instance of the supplied class name as a plugin to
     * the plugin manager. Will only add the class if it implements the plugin
     * interface.
     *
     * @param className The fully qualified class name to attempt to add to
     * the list of plugins.
     */
    private void addPlugin(final String className) {
        try {
            String cName = className.substring(0,
                                        className.length() - ".class".length());
            cName = cName.replace('/', '.');

            // Ignore UI tests - when they load they mess everything up (the
            // uispec4j interceptor kicks in and the UI stops working.
            if (!cName.contains("org.uispec4j")
                && !cName.contains("org.openshapa.uitests")) {

                Class testClass = Class.forName(cName);
                Class[] implInterfaces = testClass.getInterfaces();
                for (Class c : implInterfaces) {
                    if (c.equals(plugin)) {
                        Plugin p = (Plugin) testClass.newInstance();
                        this.availablePlugins.add(p);
                        break;
                    }
                }
             }
        } catch (InstantiationException e) {
            logger.error("Unable to instantiate plugin", e);
        } catch (IllegalAccessException e) {
            logger.error("Unable to instantiate plugin", e);
        } catch (ClassNotFoundException e) {
            logger.error("Unable to find plugin.", e);
        }
    }

    /**
     * @return A list of all the filefilters representing viewer plugins.
     */
    public List<FileFilter> getPluginFileFilters() {
        List<FileFilter> result = new ArrayList<FileFilter>();
        for (Plugin p : this.availablePlugins) {
            result.add(p.getFileFilter());
        }

        return result;
    }

    /**
     * Creates the correct kind of viewer from the supplied file. This will
     * prompt the user to clarify, if multiple viewers are availble for a single
     * file type (i.e. many viewers may support CSV files.)
     *
     * @param dataFile The dataFile that you wish to create a viewer for.
     * @param usedFilter The file filter that was used to select dataFile.
     *
     * @return A valid data viewer for the supplied file.
     */
    public DataViewer buildViewerFromFile(final File dataFile,
                                          final FileFilter usedFilter) {
        for (Plugin p : this.availablePlugins) {
            if (usedFilter.equals(p.getFileFilter())) {
                if (p.getFileFilter().accept(dataFile)) {
                    return p.getNewDataViewer();
                }
            }
        }

        // Ah-oh - no appropriate viewer found :(
        return null;
    }

    /** The single instance of the PluginManager for OpenSHAPA. */
    private static PluginManager instance = null;

    /** A reference to the interface that plugins must override. */
    private Class plugin;

    /** The list of supported file types. */
    private List<Plugin> availablePlugins;

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(PluginManager.class);
}
