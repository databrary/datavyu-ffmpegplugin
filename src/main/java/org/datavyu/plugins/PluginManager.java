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
package org.datavyu.plugins;

import com.google.common.collect.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.Datavyu;
import org.datavyu.plugins.javafx.JavaFxPlugin;
import org.datavyu.plugins.nativeosxplayer.NativeOSXPlayerFactory;
import org.datavyu.plugins.quicktime.QTDataViewer;
import org.datavyu.plugins.quicktime.java.QTPlugin;
import org.datavyu.util.MacHandler;
import org.jdesktop.application.LocalStorage;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * This class manages and wrangles all the viewer plugins currently availble to
 * Datavyu. It is implemented as a singleton, so only one instance is
 * available to Datavyu - this single instance will hunt down and load all
 * plugins that implement the Plugin interface.
 */
public final class PluginManager {

    /**
     * A reference to the interface that plugins must override.
     */

    /* WARNING: PLUGIN_CLASS, static { PLUGIN_CLASS }, and LOGGER and INSTANCE MUST APPEAR IN THIS ORDER */
    private static final Class<?> PLUGIN_CLASS;

    //
    //
    // !!! WARNING: instance must be last static - or Datavyu will crash !!!
    //
    //

    static {
        PLUGIN_CLASS = Plugin.class;
    }
    /**
     * The logger for this class.
     */
    private static Logger LOGGER = LogManager.getLogger(PluginManager.class.getName());
    /**
     * The single instance of the PluginManager for Datavyu.
     */
    private static final PluginManager INSTANCE = new PluginManager();


    /**
     * Set of plugins.
     */
    private Set<Plugin> plugins;

    /**
     * Set of names of the plugins we have added.
     */
    private Set<String> pluginNames;

    /**
     * Mapping between plugin classifiers and plugins.
     */
    private Multimap<String, Plugin> pluginClassifiers;

    /**
     * The list of plugins associated with data viewer class name.
     */
    private Map<String, Plugin> pluginLookup;

    /**
     * Merged file filters for plugins of the same name.
     */
    private Map<String, GroupFileFilter> filters;

    /**
     * Default constructor. Searches for valid plugins ... currently scans the
     * classpath looking for classes that implement the plugin interface.
     */
    private PluginManager() {
        plugins = Sets.newLinkedHashSet();
        pluginNames = Sets.newHashSet();
        pluginLookup = Maps.newHashMap();
        pluginClassifiers = HashMultimap.create();
        filters = Maps.newLinkedHashMap();
        initialize();
    }

    // --------------------------------------------------------------------------
    //
    //

    /**
     * @return The single instance of the PluginManager object in Datavyu.
     */
    public static PluginManager getInstance() {
        return INSTANCE;
    }

    /**
     * Initalizes the plugin manager by searching for valid plugins to insert
     * into the manager.
     */
    private void initialize() {

        System.out.println("Initializing plugin Manager");
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
//            ClassLoader loader = Datavyu.class.getClassLoader();
            URL resource = loader.getResource("");

            // Quaqua workaround
            if ((resource != null)
                    && resource.toString().equals(
                    "file:/System/Library/Java/")) {
                resource = null;
            }

            if (resource != null && resource.getPath().endsWith("lib/")) {
                resource = null;
            }

            // Windows being stupid workaround
//            if(resource == null) {
//                CodeSource src = Datavyu.class.getProtectionDomain().getCodeSource();
//                System.out.println("SRC LOCATION: " + src.getLocation().getPath());
//                resource = src.getLocation();
//            }
//            System.out.println("RESOURCE: " + resource);

            // The classloader references a jar - open the jar file up and
            // iterate through all the entries and add the entries that are
            // concrete Plugins.
            if (resource == null) {
                System.out.println("Loading plugins from jar");
                resource = loader.getResource("org/datavyu");

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

                    File res = new File(resources.nextElement().getFile());

                    // Dirty hack for Quaqua.
                    if (res.equals(new File("/System/Library/Java"))) {
                        continue;
                    }

                    workStack.push(res);

                    packages.clear();
                    packages.push("");

                    while (!workStack.empty()) {

                        // We must handle spaces in the directory name
                        File f = workStack.pop();
                        String s = f.getCanonicalPath();
                        s = s.replaceAll("%20", " ");

                        File dir = new File(s);

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

            // We have scanned the Datavyu classpath - but we should also look
            // in the "plugins" directory for jar files that correctly conform
            // to the Datavyu plugin interface.
            LocalStorage ls = Datavyu.getApplication().getContext()
                    .getLocalStorage();
            File pluginDir = new File(ls.getDirectory().toString()
                    + "/plugins");

            // Unable to find plugin directory or any entries within the plugin
            // directory - don't bother attempting to add more plugins to
            // Datavyu.
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
     * @throws IOException If unable to inject the plugin into the class path.
     */
    private void injectPlugin(final File f) throws IOException {
        URLClassLoader sysLoader = (URLClassLoader) ClassLoader
                .getSystemClassLoader();
        Class<?> sysclass = URLClassLoader.class;

        try {
            Class<?>[] parameters = new Class[]{URL.class};
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysLoader, new Object[]{f.toURL()});
        } catch (Throwable t) {
            LOGGER.error("Unable to inject class into class path.", t);
        }
    }

    /**
     * Attempts to add an instance of the supplied class name as a plugin to the
     * plugin manager. Will only add the class if it implements the plugin
     * interface.
     *
     * @param className The fully qualified class name to attempt to add to the list
     *                  of plugins.
     */
    private void addPlugin(final String className) {

        try {
            String cName = className.replaceAll("\\.class$", "").replace('/',
                    '.');

            /*
             * Ignore classes that: - Belong to the UITests (traditionally this
             * was because of the UISpec4J interceptor, which interrupted the
             * UI. We still ignore UITest classes as these will not be plugins)
             * - Are part of GStreamer, or JUnitX (these cause issues and are
             * certainly not going to be plugins either)
             */
            if (!cName.contains("org.datavyu.uitests")
                    && !cName.contains("org.gstreamer")
                    && !cName.contains("ch.randelshofer")
                    && !cName.startsWith("junitx")) {

                Class<?> testClass = Class.forName(cName);

                if (PLUGIN_CLASS.isAssignableFrom(testClass) && 
                        (testClass.getModifiers() & (Modifier.ABSTRACT | Modifier.INTERFACE)) == 0)
                {
                    Plugin p = (Plugin) testClass.newInstance();

                    if(!p.getValidPlatforms().contains(Datavyu.getPlatform())) {
                        // Not valid for this operating system
                        return;
                    }

                    if(Datavyu.getPlatform() == Datavyu.Platform.MAC) {
                        if(!p.getValidVersions().checkInRange(MacHandler.getOSVersion())) {
                            return;
                        }
                    }

                    String pluginName = p.getPluginName();

                    if (pluginNames.contains(p.getPluginName())) {

                        // We already have this plugin; stop processing it
                        return;
                    }

                    pluginNames.add(pluginName);

                    buildGroupFilter(p);

                    // Just make sure that we have at least one file filter.
                    assert p.getFilters() != null;
                    assert p.getFilters().length > 0;
                    assert p.getFilters()[0] != null;

                    plugins.add(p);

                    // BugzID:2110
                    pluginClassifiers.put(p.getClassifier(), p);

                    final Class<? extends DataViewer> cdv = p.getViewerClass();

                    if (cdv != null) {
                        pluginLookup.put(cdv.getName(), p);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("Unable to find plugin.", e);
            e.printStackTrace();
        } catch (ClassFormatError e) {
            LOGGER.error("Plugin with bad class format.", e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.error("Unable to instantiate plugin", e);
            e.printStackTrace();
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
        if (Datavyu.getPlatform() == Datavyu.Platform.MAC) {
            Collections.sort(p, new Comparator<Plugin>() {
                @Override
                public int compare(final Plugin o1, final Plugin o2) {

                    if ("Native OSX Video".equals(o1.getPluginName())) {
                        return -1;
                    }

                    if ("Native OSX Video".equals(o2.getPluginName())) {
                        return 1;
                    }

                    if ("QTKit Video".equals(o1.getPluginName())) {
                        return -1;
                    }

                    if ("QTKit Video".equals(o2.getPluginName())) {
                        return 1;
                    }

                    return o1.getPluginName().compareTo(o2.getPluginName());
                }
            });
            for (int i = 0; i < p.size(); i++) {
                if (p.get(i).getPluginName().equals("QuickTime Video") || p.get(i).getPluginName().equals("VLC Video")) {
                    p.remove(i);
                    break;
                }
            }
        } else if (Datavyu.getPlatform() == Datavyu.Platform.WINDOWS) {
            Collections.sort(p, new Comparator<Plugin>() {
                @Override
                public int compare(final Plugin o1, final Plugin o2) {

                    if(QTDataViewer.librariesFound) {
                        if ("QuickTime Video".equals(o1.getPluginName())) {
                            return -1;
                        }

                        if ("QuickTime Video".equals(o2.getPluginName())) {
                            return 1;
                        }
                    } else {
                        if ("JavaFX Video".equals(o1.getPluginName())) {
                            return -1;
                        }

                        if ("JavaFX Video".equals(o2.getPluginName())) {
                            return 1;
                        }
                    }

                    return o1.getPluginName().compareTo(o2.getPluginName());
                }
            });
            for (int i = 0; i < p.size(); i++) {
                if (p.get(i).getPluginName().equals("QTKit Video")) {
                    p.remove(i);
                    break;
                }
            }
        } else {
            Collections.sort(p, new Comparator<Plugin>() {
                @Override
                public int compare(final Plugin o1, final Plugin o2) {

                    if ("JavaFX Video".equals(o1.getPluginName())) {
                        return -1;
                    }

                    if ("JavaFX Video".equals(o2.getPluginName())) {
                        return 1;
                    }

                    return o1.getPluginName().compareTo(o2.getPluginName());
                }
            });
        }

        return p;
    }

    /**
     * Searches for and returns a plugin compatible with the given classifier
     * and data file.
     *
     * @param classifier Plugin classifier string.
     * @param file       The data file to open.
     * @return The first compatible plugin that is found, null otherwise.
     */
    public Plugin getCompatiblePlugin(final String classifier,
                                      final File file) {

        // Shortcircuit this for the preferred new plugins for Windows and OSX
        if (classifier.equals("datavyu.video")) {
            if (Datavyu.getPlatform() == Datavyu.Platform.MAC) {
                return NativeOSXPlayerFactory.getNativeOSXPlugin();
            }

            if (Datavyu.getPlatform() == Datavyu.Platform.WINDOWS) {
                return new QTPlugin();
            }

            if (Datavyu.getPlatform() == Datavyu.Platform.LINUX) {
                return new JavaFxPlugin();
            }
        }

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
     * @param dataViewer The fully-qualified class name of the data viewer
     *                   implementation
     * @return The {@link Plugin} used to build the data viewer if it exists,
     * {@code null} otherwise.
     */
    public Plugin getAssociatedPlugin(final String dataViewer) {
        return pluginLookup.get(dataViewer);
    }

}
