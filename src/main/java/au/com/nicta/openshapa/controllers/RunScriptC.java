package au.com.nicta.openshapa.controllers;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.views.ConsoleV;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import javax.script.ScriptException;
import org.apache.log4j.Logger;

/**
 * Controller for running scripts.
 *
 * @author cfreeman
 */
public class RunScriptC {

    /**
     * Constructs and invokes the runscript controller.
     */
    public RunScriptC() {
        FileDialog c = new FileDialog(OpenSHAPA.getApplication().getMainFrame(),
                                      "Select ruby script file:",
                                      FileDialog.LOAD);
        c.setVisible(true);

        if (c.getFile() != null && c.getDirectory() != null) {
            File rubyFile = new File(c.getDirectory() + c.getFile());

            runScript(rubyFile);
        }
    }

    /**
     * Constructs and invokes the runscript controller.
     *
     * @param file The absolute path to the script file you wish to invoke.
     */
    public RunScriptC(final String file) {
        File rubyFile = new File(file);
        runScript(rubyFile);
    }

    /** the maximum size of the recently ran script list. */
    private static final int MAX_RECENT_SCRIPT_SIZE = 5;

    /**
     * Action for running a script.
     *
     * @param rubyFile The file of the ruby script to run.
     */
    private void runScript(final File rubyFile) {
        // Update the list of most recently used scripts.
        LinkedList<File> lastScripts = OpenSHAPA.getLastScriptsExecuted();
        // If the lastscripts is full - pull the last one off to make room.
        if (lastScripts.size() >= MAX_RECENT_SCRIPT_SIZE) {
            lastScripts.removeLast();
        }
        // Add the script to the list.
        lastScripts.addFirst(rubyFile);
        OpenSHAPA.setLastScriptsExecuted(lastScripts);

        try {
            OpenSHAPA.getApplication().show(ConsoleV.getInstance());

            // Place a reference to the database within the scripting engine.
            OpenSHAPA.getRubyEngine().put("db", OpenSHAPA.getDatabase());

            FileReader reader = new FileReader(rubyFile);
            OpenSHAPA.getRubyEngine().eval(reader);
        } catch (ScriptException e) {
            PrintWriter consoleWriter = OpenSHAPA.getConsoleWriter();
            consoleWriter.println("***** SCRIPT ERRROR *****");
            consoleWriter.println("@Line " + e.getLineNumber() + ":'"
                                 + e.getMessage() + "'");
            consoleWriter.println("*************************");
            consoleWriter.flush();

            logger.error("Unable to execute script: ", e);
        } catch (FileNotFoundException e) {
            logger.error("Unable to execute script: ", e);
        }
    }

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(RunScriptC.class);
}
