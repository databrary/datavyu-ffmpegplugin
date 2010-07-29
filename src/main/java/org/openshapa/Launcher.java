package org.openshapa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import javax.swing.JOptionPane;

import org.openshapa.util.NativeLoader;

/**
 * This class performs necessary installation and configuration before spawning
 * the OpenSHAPA application.
 */
public class Launcher {

    /**
     * The main entry point for the OpenSHAPA Launcher on OSX platform - this
     * essentially unpacks and installs the required native gstreamer libs.
     *
     * @params args The arguments passed to the application.
     */
    public static void main(final String[] args) {
        int returnStatus = 0;
        final boolean dumpErrorStream = "true".equals(System.getProperty("openshapa.debug"));

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
            String gstreamer = NativeLoader.unpackNativeApp("gstreamer-osx64-1.4B");

            // Build up environment variables required to execute OpenSHAPA
            // correctly, this includes variables required for our native
            // applications to function correctly (i.e. gstreamer).
            Map<String, String> env = builder.environment();

            String path = env.get("PATH") + ":" + gstreamer;
            env.put("PATH", path);
            env.put("GST_PLUGIN_SCANNER", gstreamer);
            env.put("GST_PLUGIN_PATH", gstreamer + "/gstreamer-0.10");
            env.put("DYLD_LIBRARY_PATH", gstreamer);

            // Start the OpenSHAPA process.
            Process p = builder.start();
            p.waitFor();

            if (dumpErrorStream) {
                String s;
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((s = br.readLine()) != null) {
                    System.err.println(s);
                }
            }
        } catch (Exception e) {
            System.err.println("Unable to start OpenSHAPA: ");
        	JOptionPane.showMessageDialog(null, "Unable to start OpenSHAPA: " + e.getMessage());
            e.printStackTrace();
            returnStatus = 1;

        } finally {
            NativeLoader.cleanAllTmpFiles();
            System.exit(returnStatus);
        }
    }
}
