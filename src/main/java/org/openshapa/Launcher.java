package org.openshapa;

import java.awt.SplashScreen;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
     * essentially unpacks and installs the required native libraries.
     *
     * @params args The arguments passed to the application.
     */
    public static void main(final String[] args) {
        int returnStatus = 0;
        final boolean dumpOutputStreams = true /* added for easier debugging */ | "true".equals(System.getProperty("openshapa.debug"));
        final boolean dumpLaunchCommandForDebugging = true;
        File splashScreenImage = null;
        
        try {
            // Build a process for running the actual OpenSHAPA application, rather
            // than this launcher stub which performs configuration necessary for
            // OpenSHAPA to execute correctly.
            String classPath = System.getProperty("java.class.path");

            // Unpack and install applications necessary for OpenSHAPA to
            // function correctly.
            final String gstreamerLibraryPath = NativeLoader.unpackNativeApp("openshapa-nativelibs-gstreamer-osx64-0.10.29.a");
            final String openshapaNativeLibraryPath = NativeLoader.unpackNativeApp("openshapa-nativelibs-osx64-0.1");
            
            final String splashFileLocation = gstreamerLibraryPath + "/splash.png";
            splashScreenImage = extractResource(gstreamerLibraryPath, "icons/splash.png", splashFileLocation);
            
            ProcessBuilder builder = new ProcessBuilder("java",
                    "-splash:" + splashFileLocation,
                    "-cp", classPath,
                    "-Djna.library.path=" + openshapaNativeLibraryPath,
                    "org.openshapa.OpenSHAPA");

            if (dumpLaunchCommandForDebugging) {
            	final StringBuilder sb = new StringBuilder();
            	for (String s : builder.command()) {
            		sb.append(s);
            		sb.append(' ');
            	}
            	final String launchString = sb.toString();
            	System.err.println("OpenSHAPA launch command (" + launchString.length() + " characters): " + launchString);
            }
            
            // Build up environment variables required to execute OpenSHAPA
            // correctly, this includes variables required for our native
            // applications to function correctly (i.e. gstreamer).
            Map<String, String> env = builder.environment();

            String path = gstreamerLibraryPath + ":" + env.get("PATH");
            env.put("PATH", path);
            env.put("GST_PLUGIN_SCANNER", gstreamerLibraryPath);
            env.put("GST_PLUGIN_PATH", gstreamerLibraryPath + "/gstreamer-0.10");
            env.put("DYLD_LIBRARY_PATH", gstreamerLibraryPath);

            // Start the OpenSHAPA process.
            Process p = builder.start();
            if (dumpOutputStreams) {
            	connect(p.getInputStream(), System.out);
            	connect(p.getErrorStream(), System.err);
            }
            if (SplashScreen.getSplashScreen() != null) {
            	// wait for the OpenSHAPA splash screen to be visible (overlaid on top of the Launcher's splash screen)
            	Thread.sleep(3000);
            	// remove the Launcher's splash screen (while the OpenSHAPA splash screen is visible)
            	SplashScreen.getSplashScreen().close();
            }
            p.waitFor();
        } catch (Exception e) {
            System.err.println("Unable to start OpenSHAPA: ");
        	JOptionPane.showMessageDialog(null, "Unable to start OpenSHAPA: " + e.getMessage());
            e.printStackTrace();
            returnStatus = 1;
        } finally {
        	if (splashScreenImage != null) {
        		splashScreenImage.delete();
        	}
            NativeLoader.cleanAllTmpFiles();
            System.exit(returnStatus);
        }
    }
    
    private static void connect(final InputStream in, final OutputStream out) {
    	new Thread() {
    		public void run() {
    			try {
	    			final byte [] buffer = new byte [1024];
	    			while (true) {
	    				final int bytesRead = in.read(buffer, 0, buffer.length);
	    				if (bytesRead == -1) {
	    					break;
	    				}
	    				out.write(buffer, 0, bytesRead);
	    			}
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}.start();
    }
    
    private static File extractResource(String directory, String resourceName, String targetFilename) {
    	try {
	    	final InputStream resourceStream = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName);
	    	if (resourceStream == null) {
	    		return null;
	    	}
	    	final FileOutputStream exportedFile = new FileOutputStream(targetFilename);
	    	final byte [] buffer = new byte [1024];
	    	int bytesRead;
	    	while ((bytesRead = resourceStream.read(buffer, 0, buffer.length)) != -1) {
	    		exportedFile.write(buffer, 0, bytesRead);
	    	}
	    	exportedFile.close();
	    	return new File(targetFilename);
    	} catch (IOException e) {    	
    		e.printStackTrace();
    	}
    	return null;
    }
}
