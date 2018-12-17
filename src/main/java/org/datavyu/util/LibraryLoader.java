package org.datavyu.util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This native library loader loads libraries from jar resources. It resolves the library file extension based on the
 * loading OS automatically. It offers functionality to extract and load libraries. All library files are extracted into
 * the current working directory. This allows the automatic discovery of dependent libraries.
 *
 * Notice: Former versions of this implementation used the trick from the Fahd Shariff's website
 * (http://fahdshariff.blogspot.com/2011/08/changing-java-library-path-at-runtime.html); however, that trick does not
 * work under windows; neither when using System.loadLibrary as described in the blog or System.load.  For this reason
 * I opted to copy the libraries to the current working directory '.' which enables the discovery of dependent
 * libraries automatically.
 */
public class LibraryLoader {

    /**
     * Holds the library name and version
     */
    public static final class LibraryDependency {
        public LibraryDependency(String name, String version) {
            this.name = name;
            this.version = version;
        }
        String getFullName() {
            return name + SEPARATOR + version;
        }
        private final static char SEPARATOR = LibraryLoader.isMacOs ? '.' : '-';
        private String name;
        private String version;
    }

    /** Logger for this native library loader */
    private static Logger logger = LogManager.getLogger(LibraryLoader.class);

    /**
     * Folder where we unzip libraries must be current working directory for the library
     * loader to find dependent libraries.
     */
    private static File libraryFolder = new File(System.getProperty("user.dir"));

    /** True if loading on Mac OS */
    public static boolean isMacOs = System.getProperty("os.name").contains("Mac");

    /** Buffer size when copying files from streams */
    private static final int BUFFER_COPY_SIZE = 16*1024; // 16 kB

    /**
     * Get resource URL for a given library name that is part of the jar.
     *
     * @param libName The library name.
     * @return The URL.
     * @throws Exception Could come from class loader.
     */
    private static URL getResource(String libName) throws Exception {
        Enumeration<URL> resources;
        String extension;
        ClassLoader classLoader = LibraryLoader.class.getClassLoader();
        if (isMacOs) {
            extension = ".jnilib";
            resources = classLoader.getResources("lib" + libName + extension);
            if (!resources.hasMoreElements()) {
                extension = ".dylib";
                resources = classLoader.getResources(libName + extension);
            }
        } else {
            extension = ".dll";
            resources = classLoader.getResources(libName + extension);
        }
        return resources.hasMoreElements() ? resources.nextElement() : null;
    }

    /**
     * Get the file extension for a library name.
     *
     * @param libName The library name.
     * @return The extension as string.
     * @throws Exception Could come from the class loader.
     */
    private static String getExtension(String libName) throws Exception {
        String extension;
        if (isMacOs) {
            extension = ".jnilib";
            if (!LibraryLoader.class.getClassLoader()
                    .getResources("lib" + libName + extension).hasMoreElements()) {
                extension = ".dylib";
            }
        } else {
            extension = ".dll";
        }
        return extension;
    }

    /**
     * Extract the library file from a resource jar and load it.
     *
     * @param destName The destination name when the library has been extracted.
     * @return The file name to the extracted library with extension.
     *
     * @throws Exception When extracting or loading the library.
     */
    public static File extractAndLoad(final String destName) throws Exception {
        File libraryFile = extract(destName);
        System.load(libraryFile.getAbsolutePath());
        return libraryFile;
    }

    public static void load(final String destName) {
        System.loadLibrary(destName);
    }

    /**
     * Extract a library from a resource jar.
     *
     * @param destName The destination name when the library has been extracted.
     * @return The file name to the extracted library with extension.
     *
     * @throws Exception When extracting or loading the library.
     */
    public static File extract(final String destName) throws Exception {
        logger.info("Attempting to extract " + destName);

        File outfile = new File(libraryFolder,destName + getExtension(destName));

        // If the file already exists and is in use aka can't be written
        if (outfile.exists()) {
            logger.info("Attempting to extract an existing file " + destName);
            return outfile;
        }

        URL url = getResource(destName);
        InputStream in = url.openStream();

        FileOutputStream out = new FileOutputStream(outfile);
        BufferedOutputStream dest = new BufferedOutputStream(out, BUFFER_COPY_SIZE);
        int count;
        byte[] data = new byte[BUFFER_COPY_SIZE];
        while ((count = in.read(data, 0, BUFFER_COPY_SIZE)) != -1) {
            dest.write(data, 0, count);
        }
        dest.close();  // close flushes
        out.close();
        in.close();
        return outfile;
    }

    /**
     * Extracts a list of library dependencies.
     *
     * @param dependencies The list of dependencies.
     *
     * @throws Exception Whenever a library can't be loaded an Exception is thrown
     */
    public static void extract(List<LibraryDependency> dependencies) throws Exception {
        for (LibraryDependency libraryDependency : dependencies) {
            LibraryLoader.extract(libraryDependency.getFullName());
        }
    }
}
