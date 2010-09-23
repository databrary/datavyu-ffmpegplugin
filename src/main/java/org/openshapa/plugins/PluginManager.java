package org.openshapa.plugins;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import com.usermetrix.jclient.Logger;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.Method;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.filechooser.FileFilter;

import org.jdesktop.application.LocalStorage;

import org.openshapa.OpenSHAPA;

import org.openshapa.plugins.quicktime.QTPlugin;

import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.Filter;
import org.openshapa.views.continuous.Plugin;

import com.usermetrix.jclient.UserMetrix;


/**
 * This class manages and wrangles all the viewer plugins currently availble to
 * OpenSHAPA. It is implemented as a singleton, so only one instance is
 * available to OpenSHAPA - this single instance will hunt down and load all
 * plugins that implement the Plugin interface.
 */
public final class PluginManager {

    /** A reference to the interface that plugins must override. */
    private static final Class<?> PLUGIN_CLASS;

    static {
        PLUGIN_CLASS = Plugin.class;
    }

    //
    // WARNING: instance must be last static !!!
    //

    /** The single instance of the PluginManager for OpenSHAPA. */
    private static final PluginManager INSTANCE = new PluginManager();

    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(PluginManager.class);

    /** Set of plugins. */
    private Set<Plugin> plugins;

    /** Mapping between plugin classifiers and plugins. */
    private Multimap<String, Plugin> pluginClassifiers;

    /** The list of plugins associated with data viewer class name. */
    private Map<String, Plugin> pluginLookup;

    /** Merged file filters for plugins of the same name. */
    private Map<String, GroupFileFilter> filters;

    /**
     * Default constructor. Searches for valid plugins ... currently scans the
     * classpath looking for classes that implement the plugin interface.
     */
    private PluginManager() {
        plugins = Sets.newLinkedHashSet();
        pluginLookup = Maps.newHashMap();
        pluginClassifiers = HashMultimap.create();
        filters = Maps.newLinkedHashMap();
        initialize();
    }

    // --------------------------------------------------------------------------
    //
    //

    /**
     * @return The single instance of the PluginManager object in OpenSHAPA.
     */
    public static PluginManager getInstance() {
        return INSTANCE;
    }

    /**
     * Initalizes the plugin manager by searching for valid plugins to insert
     * into the manager.
     */
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
                // recusively inspect contents of each of the resources. If it
                // is a directory add it to our workStack, otherwise check to
                // see if it is a concrete plugin.
            } else {

                // If we are running from a test we need to look in more than
                // one place for classes - add all these places to the workstack
                Enumeration<URL> resources = loader.getResources("");
                Stack<File> workStack = new Stack<File>();
                Stack<String> packages = new Stack<String>();

                while (resources.hasMoreElements()) {
                    workStack.clear();
                    workStack.push(new File(resources.nextElement().getFile()));

                    packages.clear();
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

                            // If the file is a directory, add to work list.
                            if (file.isDirectory()) {
                                workStack.push(file);
                                packages.push(pkgName + file.getName() + ".");

                                // If we are dealling with a class file -
                                // attempt to add it to our list of plugins.
                            } else if (files[i].endsWith(".class")) {
                                addPlugin(pkgName.concat(files[i]));
                            }
                        }
                    }
                }
            }

            // We have scanned the OpenSHAPA classpath - but we should also look
            // in the "plugins" directory for jar files that correctly conform
            // to the OpenSHAPA plugin interface.
            LocalStorage ls = OpenSHAPA.getApplication().getContext()
                .getLocalStorage();
            File pluginDir = new File(ls.getDirectory().toString()
                    + "/plugins");

            // Unable to find plugin directory or any entries within the plugin
            // directory - don't bother attempting to add more plugins to
            // OpenSHAPA.
            if ((pluginDir == null) || (pluginDir.list() == null)) {
                return;
            }

            // For each of the files in the plugin directory - check to see if
            // they conform to the plugin interface.
            for (String file : pluginDir.list()) {
                File f = new File(pluginDir.getAbsolutePath() + "/" + file);

                if (file == null) {
                    throw new ClassNotFoundException("Null file");

                    // File is a jar file - crack it open and look for plugins!
                } else if (file.endsWith(".jar")) {
                    injectPlugin(f);

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

            // Whoops, something went bad. Chuck a spaz.
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
     * @param f
     *            The jar file to inject into the classpath.
     * @throws IOException
     *             If unable to inject the plugin into the class path.
     */
    private void injectPlugin(final File f) throws IOException {
        URLClassLoader sysLoader = (URLClassLoader) ClassLoader
            .getSystemClassLoader();
        Class<?> sysclass = URLClassLoader.class;

        try {
            Class<?>[] parameters = new Class[] { URL.class };
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysLoader, new Object[] { f.toURL() });
        } catch (Throwable t) {
            logger.error("Unable to inject class into class path.", t);
        }
    }

    /**
     * Attempts to add an instance of the supplied class name as a plugin to the
     * plugin manager. Will only add the class if it implements the plugin
     * interface.
     *
     * @param className
     *            The fully qualified class name to attempt to add to the list
     *            of plugins.
     */
    private void addPlugin(final String className) {

        try {
            String cName = className.replaceAll("\\.class$", "").replace('/',
                    '.');

            // Ignore UI tests - when they load they mess everything up (the
            // uispec4j interceptor kicks in and the UI stops working.
            if (!cName.contains("org.openshapa.uitests")
                    && !cName.contains("org.gstreamer")) {

                Class<?> testClass = Class.forName(cName);

                if (PLUGIN_CLASS.isAssignableFrom(testClass)) {
                    Plugin p = (Plugin) testClass.newInstance();

                    buildGroupFilter(p);

                    // Just make sure that we have at least one file filter.
                    assert p.getFilters() != null;
                    assert p.getFilters().length > 0;
                    assert p.getFilters()[0] != null;

                    plugins.add(p);

                    // BugzID:2110
                    pluginClassifiers.put(p.getClassifier(), p);

                    // We call this with no parent frame because asking
                    // OpenSHAPA for its mainframe before it is created ruins
                    // all the dialogs (and menus).
                    final DataViewer newDataViewer;

                    try {
                        newDataViewer = p.getNewDataViewer(null, false);

                        if (newDataViewer != null) {
                            pluginLookup.put(newDataViewer.getClass().getName(),
                                p);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        logger.error("Unable to load plugin "
                            + p.getClass().getName(), e);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            logger.error("Unable to find plugin.", e);
        } catch (ClassFormatError e) {
            logger.error("Plugin with bad class format.", e);
        } catch (Throwable e) {
            logger.error("Unable to instantiate plugin", e);
        }
    }

    private void buildGroupFilter(final Plugin p) {

        for (Filter f : p.getFilters()) {
            GroupFileFilter g;

            if (filters.containsKey(f.getName())) {
                g = filters.get(f.getName());
            } else {
                g = new GroupFileFilter(f.getName());
                filters.put(f.getName(), g);
            }

            g.addFileFilter(f);
        }
    }

    public Iterable<? extends FileFilter> getFileFilters() {
        return filters.values();
    }

    public Iterable<Plugin> getPlugins() {
        List<Plugin> p = Lists.newArrayList(plugins);
        Collections.sort(p, new Comparator<Plugin>() {
                @Override public int compare(final Plugin o1, final Plugin o2) {

                    // Want the QuickTime video plugin to always be first.
                    if ("QuickTime Video".equals(o1.getPluginName())) {
                        return -1;
                    }

                    if ("QuickTime Video".equals(o2.getPluginName())) {
                        return 1;
                    }

                    return o1.getPluginName().compareTo(o2.getPluginName());
                }
            });

        return p;
    }

    /**
     * Searches for and returns a plugin compatible with the given classifier
     * and data file.
     *
     * @param classifier
     *            Plugin classifier string.
     * @param file
     *            The data file to open.
     * @return The first compatible plugin that is found, null otherwise.
     */
    public Plugin getCompatiblePlugin(final String classifier,
        final File file) {

        for (Plugin candidate : pluginClassifiers.get(classifier)) {

            for (Filter filter : candidate.getFilters()) {

                if (filter.getFileFilter().accept(file)) {
                    return candidate;
                }
            }
        }

        return null;
    }

    /**
     * @param dataViewer
     *            The fully-qualified class name of the data viewer
     *            implementation
     * @return The {@link Plugin} used to build the data viewer if it exists,
     *         {@code null} otherwise.
     */
    public Plugin getAssociatedPlugin(final String dataViewer) {
        return pluginLookup.get(dataViewer);
    }

}
