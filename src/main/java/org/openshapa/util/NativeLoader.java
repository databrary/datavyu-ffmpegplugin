package org.openshapa.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.collect.Iterables;

import com.sun.jna.Platform;


public class NativeLoader {

    /** The size of the buffer to use when un zipping native libraries. */
    private static final int BUFFER = 16384;

    /** The list of all the native loaded odds and ends that need unpacking. */
    private static final ArrayList<File> loadedLibs = new ArrayList<File>();

    /** The folder that we stash all our native libs within. */
    private static File nativeLibFolder;

    /**
     * Load the given native library.
     *
     * @param libName
     *            Name of the library, extensions (dll, jnilib) removed. OSX
     *            libraries should have the "lib" suffix removed.
     * @throws Exception
     *             If the library cannot be loaded.
     */
    public static void LoadNativeLib(final String libName) throws Exception {
        Enumeration<URL> resources;
        String extension;

        if (System.getProperty("os.name").contains("Mac")) {
            extension = ".jnilib";
            resources = NativeLoader.class.getClassLoader().getResources("lib"
                    + libName + extension);

        } else {
            extension = ".dll";
            resources = NativeLoader.class.getClassLoader().getResources(libName
                    + extension);
        }

        int count;
        byte[] data = new byte[BUFFER];

        while (resources.hasMoreElements()) {
            URL u = resources.nextElement();
            System.err.println("Attempting to load: " + u.toString());

            InputStream in = u.openStream();
            File outfile = new File(System.getProperty("java.io.tmpdir"),
                    libName + extension);

            // Create a temporary output location for the library.
            FileOutputStream out = new FileOutputStream(outfile);
            BufferedOutputStream dest = new BufferedOutputStream(out, BUFFER);

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
        }
    }

    public static void loadLibFromResource(final String libName)
        throws Exception {

        if (!Platform.isMac() && !Platform.isWindows()) {
            throw new UnsupportedOperationException("Unsupported OS");
        }

        String prefix = Platform.isMac() ? "lib" : "";
        String extension = Platform.isMac() ? ".jnilib" : ".dll";

        String lib = prefix + libName + extension;

        InputStream is = NativeLoader.class.getResourceAsStream(lib);

        if (is != null) {
            System.err.println("Loading: " + lib);

            File outFile = new File(System.getProperty("java.io.tmpdir"), lib);
            outFile.deleteOnExit();
            outFile.setWritable(true);

            org.apache.commons.io.FileUtils.copyInputStreamToFile(is, outFile);

            loadedLibs.add(outFile);
            System.err.println("Temp File: " + outFile);
            System.load(outFile.toString());
            System.err.println("Loaded lib: " + lib);
        } else {
            throw new IllegalArgumentException("No such lib:" + lib);
        }
    }

    /**
     * Unpacks a native application to a temporary location so that it can be
     * utilized from within java code.
     *
     * @param appJar
     *            The jar containing the native app that you want to unpack.
     * @return The path of the native app as unpacked to a temporary location.
     *
     * @throws Exception
     *             If unable to unpack the native app to a temporary location.
     */
    static public String unpackNativeApp(final String appJar) throws Exception {
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

        for (String s
            : System.getProperty("java.class.path").split(File.pathSeparator)) {

            // Success! We found a matching jar.
            if (s.endsWith(appJar + ".jar")) {
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

            for (String s
                : System.getProperty("java.class.path").split(
                    File.pathSeparator)) {
                System.err.println("    " + s);
            }

            throw new Exception("Unable to find '" + appJar
                + "' for unpacking.");
        }

        return nativeLibFolder.getAbsolutePath();
    }

    /**
     * Cleans all the temporary files created by the native loader.
     */
    static public void cleanAllTmpFiles() {
        System.err.println("cleaning temp files");

        for (File loadedLib : Iterables.reverse(loadedLibs)) {

            if (!loadedLib.delete()) {
                System.err.println("Unable to delete temp file: " + loadedLib);
            }
        }

        if ((nativeLibFolder != null) && !nativeLibFolder.delete()) {
            System.err.println("Unable to delete temp folder: + "
                + nativeLibFolder);
        }
    }

}
