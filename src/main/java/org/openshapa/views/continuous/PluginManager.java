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

    //--------------------------------------------------------------------------
    //
    //

 /** The default plugin to present to the user when loading data. */
    private static final String DEFAULT_PLUGIN =
                            "org.openshapa.views.continuous.quicktime.QTPlugin";

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(PluginManager.class);

    /** A reference to the interface that plugins must override. */
    private static final Class PLUGIN_CLASS;
    static {
        try {
            PLUGIN_CLASS
                    = Class.forName("org.openshapa.views.continuous.Plugin");
        } catch (ClassNotFoundException ex) {
            LOGGER.fatal("Unable to initialize Plugin Class", ex);
            throw new RuntimeException(ex);
        }
    }

    //
    // Default File Filter Settings
    //

    /** The Default File Filter. */
    private static final FileFilter DEFAULT_FILE_FILTER = new FileFilter() {
        @Override
        public boolean accept(File f) {
            return
                    f.isDirectory()
                    || DFF_FILETYPE.matcher(f.getName()).matches();
        }
        @Override
        public String getDescription() { return DFF_DESCRIPTION; }
    };

    /** Default File Filter Description. */
    private static final String DFF_DESCRIPTION
            = "OpenSHAPA project files (*.openshapa)";

    /** Default File Filter File Type Pattern. */
    private static final Pattern DFF_FILETYPE
            = Pattern.compile("^.*\\.openshapa$", Pattern.CASE_INSENSITIVE);


    //
    // WARNING: instance must be last static !!!
    //
    
    /** The single instance of the PluginManager for OpenSHAPA. */
    private static final PluginManager INSTANCE = new PluginManager();


    //--------------------------------------------------------------------------
    //
    //

// @todo: remove
//    /** The list of supported file types. */
//    private List<Plugin> availablePlugins = new ArrayList<Plugin>();

    /** */
    private Map<FileFilter, Plugin> plugins = new HashMap<FileFilter, Plugin>();
    {
        plugins.put(DEFAULT_FILE_FILTER, null);
    }


    //--------------------------------------------------------------------------
    //
    //

    /**
     * @return The single instance of the PluginManager object in OpenSHAPA.
     */
    public static PluginManager getInstance() { return INSTANCE; }

    /**
     * Default constructor.
     *
     * Searches for valid plugins ... currently scans the classpath looking for
     * classes that implement the plugin interface.
     */
    private PluginManager() {
        initialize();
    }

    private void initialize() {
        try {
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
            LOGGER.error("Unable to build Plugin", e);
        } catch (IOException ie) {
            LOGGER.error("Unable to load jar file", ie);
        } catch (URISyntaxException se) {
            LOGGER.error("Unable to build path to jar file", se);
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

                        // BugzID:749 - Force movie filter to be default filter.
                        if (cName.equalsIgnoreCase(DEFAULT_PLUGIN)) {
                            this.availablePlugins.add(p);
                        } else {
                            this.availablePlugins.add(0, p);
                        }
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
    public Iterable<FileFilter> getPluginFileFilters() {
        return plugins.keySet();
    }

        return result;
    }

    /**
     * Creates the correct kind of viewer from the supplied file. This will
     * prompt the user to clarify, if multiple viewers are availble for a single
     * file type (i.e. many viewers may support CSV files.)
     *
     * @param ff file filter used to identify plugin type
     * @param f data stream file
     * @return initialized data viewers
     */
    public Iterable<DataViewer> buildDataViewers(
            final FileFilter ff,
            final File f
    ) {
        List<DataViewer> dvs = new ArrayList<DataViewer>();
        if (DEFAULT_FILE_FILTER == ff) {
            ProjectDescriptor pd = new ProjectDescriptor();
            pd.setBasePath(f.getParent());
            try {
                pd.process(new FileReader(f));
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger
                        .getLogger(PluginManager.class.getName())
                        .log(Level.SEVERE, f.getAbsolutePath(), ex);
            }

            for (ProjectDescriptor.Entry pde : pd.getEntries()) {
                Plugin plugin = null;
                try {
                    plugin = (Plugin) pde.plugin.newInstance();
                } catch (InstantiationException ex) {
                    java.util.logging.Logger.getLogger(
                            PluginManager.class.getName())
                                    .log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(
                            PluginManager.class.getName())
                                    .log(Level.SEVERE, null, ex);
                }
                DataViewer dataViewer = plugin.getNewDataViewer();
                dataViewer.setDataFeed(pde.file);
                dvs.add(dataViewer);
            }
        } else {
            Plugin plugin = plugins.get(ff);
            DataViewer dataViewer = plugin.getNewDataViewer();
            dataViewer.setDataFeed(f);
            dvs.add(dataViewer);
        }
        return dvs;
    }
}
