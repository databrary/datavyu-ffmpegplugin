package org.openshapa.util;

import com.google.common.collect.Iterables;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class NativeLoader {

    /** The size of the buffer to use when un zipping native libraries. */
    private static final int BUFFER = 2048;

    /** The list of all the native loaded odds and ends that need unpacking. */
    private static final ArrayList<File> loadedLibs = new ArrayList<File>();

    /**
     * unpacks a native application to a temporary location so that it can be
     * utilized from within java code.
     *
     * @param appJar The jar containing the native app that you want to unpack.
     * @return The path of the native app as unpacked to a temporary location.
     * @throws Exception If unable to unpack the native app to a temporary
     * location.
     */
    static public String unpackNativeApp(final String appJar) throws Exception {
        Enumeration<URL> resources = NativeLoader.class.getClassLoader().getResources(appJar);
        int count;
        byte[] data = new byte[BUFFER];

        while (resources.hasMoreElements()) {
            URL u = resources.nextElement();

            JarFile jar = new JarFile(u.getFile().split("!")[0].substring(5));

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry inFile = entries.nextElement();

                File outFile = new File(System.getProperty("java.io.tmpdir")
                                  + File.separator + inFile.getName());

                // if its a directory, create it
                if (inFile.isDirectory()) {
                    outFile.mkdir();

                } else {
                    InputStream in = jar.getInputStream(inFile);

                    // Create a temporary output location for the library.
                    FileOutputStream out = new FileOutputStream(outFile);
                    BufferedOutputStream dest = new BufferedOutputStream(out,
                                                                         BUFFER);
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
        }

        return System.getProperty("java.io.tmpdir") + appJar;
    }

    /**
     * Cleans all the temporary files created by the native loader.
     */
    static public void cleanAllTmpFiles() {
        for (File loadedLib : Iterables.reverse(loadedLibs)) {
            if (!loadedLib.delete()) {
                System.err.println("Unable to delete temp file: " + loadedLib);
            }
        }
    }

}
