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
package org.datavyu.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

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
public class NativeLibraryLoader {

    /** Logger for this native library loader */
    private static Logger logger = LogManager.getLogger(NativeLibraryLoader.class);

    /**
     * Folder where we unzip libraries must be current working directory for the library loader to find dependent
     * libraries.
     */
    private static File libraryFolder = new File(System.getProperty("user.dir"));

    private static boolean isMacOs = System.getProperty("os.name").contains("Mac");

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
        ClassLoader classLoader = NativeLibraryLoader.class.getClassLoader();
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
            if (!NativeLibraryLoader.class.getClassLoader()
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
        URL url = getResource(destName);
        InputStream in = url.openStream();
        File outfile = new File(libraryFolder, destName + getExtension(destName));
        FileOutputStream out = new FileOutputStream(outfile);
        BufferedOutputStream dest = new BufferedOutputStream(out, 16*1024);
        int count;
        byte[] data = new byte[16*1024];
        while ((count = in.read(data, 0, 16*1024)) != -1) {
            dest.write(data, 0, count);
        }
        dest.close();  // close flushes
        out.close();
        in.close();
        return outfile;
    }
}
