package org.openshapa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map;
import org.openshapa.util.NativeLoader;

/**
 * This class performs necessary installation and configuration before spawning
 * the OpenSHAPA application.
 */
public class Launcher {

    /**
     * The main entry point for the OpenSHAPA Launcher.
     *
     * @params args The arguments passed to the application.
     */
    public static void main(final String[] args) {
        int returnStatus = 0;

        try {
            // Build a process for running the actual OpenSHAPA application, rather
            // than this launcher stub which performs configuration necessary for
            // OpenSHAPA to execute correctly.
            String classPath = System.getProperty("java.class.path");
            ProcessBuilder builder = new ProcessBuilder("java",
                                                        "-cp", classPath,
                                                        "org.openshapa.OpenSHAPA");

            // Unpack and install applications necessary for OpenSHAPA to
            // function correctly.
            String xuggle = NativeLoader.unpackNativeApp("xuggler-osx64-3.4");

            // Build up environment variables required to execute OpenSHAPA
            // correctly, this includes variables required for our native
            // applications to function correctly (i.e. xuggler).
            Map<String, String> env = builder.environment();
            String path = env.get("PATH")
                          + ":" + xuggle + File.separator + "bin"
                          + ":" + xuggle + File.separator + "lib";
            env.put("PATH", path);
            env.put("XUGGLE_HOME", xuggle);
            env.put("LD_LIBRARY_PATH", xuggle + File.separator + "lib");
            env.put("DYLD_LIBRARY_PATH", xuggle + File.separator + "lib");

            // Start the OpenSHAPA process.
            Process p = builder.start();
            p.waitFor();

        } catch (Exception e) {
            System.err.println("Unable to start OpenSHAPA: ");
            System.err.println(e);
            returnStatus = 1;

        } finally {
            NativeLoader.cleanAllTmpFiles();
            System.exit(returnStatus);
        }
    }
}
