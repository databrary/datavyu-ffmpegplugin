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

import com.google.common.collect.Iterables;
import org.apache.commons.io.FileUtils;
import org.datavyu.Datavyu;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class NativeLoader {

    /**
     * The size of the buffer to use when un zipping native libraries.
     */
    private static final int BUFFER = 16384;

    /**
     * The list of all the native loaded odds and ends that need unpacking.
     */
    private static final ArrayList<File> loadedLibs = new ArrayList<File>();

    /**
     * The folder that we stash all our native libs within.
     */
    private static File nativeLibFolder;

    /**
     * Load the given native library.
     *
     * @param libName Name of the library, extensions (dll, jnilib) removed. OSX
     *                libraries should have the "lib" suffix removed.
     * @throws Exception If the library cannot be loaded.
     */
    public static void LoadNativeLib(final String libName) throws Exception {
        Enumeration<URL> resources;
        String extension;

        if (System.getProperty("os.name").contains("Mac")) {
            extension = ".jnilib";
            resources = NativeLoader.class.getClassLoader()
                    .getResources("lib" + libName + extension);

        } else {
            extension = ".dll";
            resources = NativeLoader.class.getClassLoader()
                    .getResources(libName + extension);
        }

        File outfile;
        while (resources.hasMoreElements()) {
            outfile = copyFileToTmp((libName + extension), resources.nextElement());
        }
    }

    private static File copyFileToTmp(final String destName, final URL u) throws Exception {
        System.err.println("Attempting to load: " + u.toString());

        InputStream in = u.openStream();
        File outfile = new File(System.getProperty("java.io.tmpdir"), destName);

        // Create a temporary output location for the library.
        FileOutputStream out = new FileOutputStream(outfile);
        BufferedOutputStream dest = new BufferedOutputStream(out, BUFFER);

        int count;
        byte[] data = new byte[BUFFER];
        while ((count = in.read(data, 0, BUFFER)) != -1) {
            dest.write(data, 0, count);
        }

        dest.flush();
        dest.close();
        out.close();
        in.close();

        loadedLibs.add(outfile);
        System.err.println("Temp File:" + outfile);
        System.load(outfile.toString());
        System.err.println("Extracted lib: " + outfile);
        return outfile;
    }

    public static void unpackNativeLib(final String libName) throws Exception {
        Enumeration<URL> resources = NativeLoader.class.getClassLoader().getResources(libName);

        while (resources.hasMoreElements()) {
            copyFileToTmp(libName, resources.nextElement());
        }
    }

    /**
     * Unpacks a native application to a temporary location so that it can be
     * utilized from within java code.
     *
     * @param appJar The jar containing the native app that you want to unpack.
     * @return The path of the native app as unpacked to a temporary location.
     * @throws Exception If unable to unpack the native app to a temporary
     *                   location.
     */
    public static String unpackNativeApp(final String appJar) throws Exception {
        final String nativeLibraryPath;

        if (nativeLibFolder == null) {
            nativeLibraryPath = System.getProperty("java.io.tmpdir")
                    + UUID.randomUUID().toString() + "nativelibs";
            nativeLibFolder = new File(nativeLibraryPath);

            if (!nativeLibFolder.exists()) {
                nativeLibFolder.mkdir();
            }
        }

        // Search the class path for the application jar.
        JarFile jar = null;

        // BugID: 26178921 -- We need to inspect the surefire test class path as
        // well as the regular class path property so that we can scan dependencies
        // during tests.

        String searchPath = System.getProperty("surefire.test.class.path")
                + File.pathSeparator
                + System.getProperty("java.class.path");

        // This is done for the Windows build so it can find the dependencies
        // if the cwd is different from the program dir
        if (Datavyu.getPlatform() == Datavyu.Platform.WINDOWS) {
            String path = new File(Datavyu.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath()).getParentFile().getAbsolutePath();
            String[] splitPath = searchPath.split(File.pathSeparator);
            searchPath = "";
            for (int i = 0; i < splitPath.length; i++) {
                if (!splitPath[i].startsWith("C:\\")) {
                    searchPath += path;
                }
                searchPath += File.separator + splitPath[i] + File.pathSeparator;
            }
        }

        for (String s : searchPath.split(File.pathSeparator)) {
            // Success! We found a matching jar.
            if (s.endsWith(appJar + ".jar") || s.endsWith(appJar)) {
                jar = new JarFile(s);
            }
        }

        // If we found a jar - it should contain the desired application.
        // decompress as needed.
        if (jar != null) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry inFile = entries.nextElement();
                File outFile = new File(nativeLibFolder, inFile.getName());

                // If the file from the jar is a directory, create it.
                if (inFile.isDirectory()) {
                    outFile.mkdir();

                    // The file from the jar is regular - decompress it.
                } else {
                    InputStream in = jar.getInputStream(inFile);

                    // Create a temporary output location for the library.
                    FileOutputStream out = new FileOutputStream(outFile);
                    BufferedOutputStream dest = new BufferedOutputStream(out,
                            BUFFER);
                    int count;
                    byte[] data = new byte[BUFFER];

                    while ((count = in.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }

                    dest.flush();
                    dest.close();
                    out.close();
                    in.close();
                }

                loadedLibs.add(outFile);
            }

            // Unable to find jar file - abort decompression.
        } else {
            System.err.println("Unable to find jar file for unpacking: "
                    + appJar + ". Java classpath is:");

            for (String s : System.getProperty("java.class.path").split(File.pathSeparator)) {
                System.err.println("    " + s);
            }

            throw new Exception("Unable to find '" + appJar + "' for unpacking.");
        }

        return nativeLibFolder.getAbsolutePath();
    }

    /**
     * Cleans all the temporary files created by the native loader.
     */
    public static void cleanAllTmpFiles() {
        System.err.println("cleaning temp files");

        for (File loadedLib : Iterables.reverse(loadedLibs)) {

            if (!loadedLib.delete()) {
                System.err.println("Unable to delete temp file: " + loadedLib);
            }
        }

        // Delete all of the other files that mongo has created
        try {
            FileUtils.deleteDirectory(nativeLibFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ((nativeLibFolder != null) && !nativeLibFolder.delete()) {
            System.err.println("Unable to delete temp folder: + " + nativeLibFolder);
        }
    }

}
