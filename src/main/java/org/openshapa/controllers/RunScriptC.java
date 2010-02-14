package org.openshapa.controllers;

import com.usermetrix.jclient.UserMetrix;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.JFileChooser;
import org.openshapa.OpenSHAPA;
import org.openshapa.util.FileFilters.RBFilter;
import org.openshapa.views.ConsoleV;
import org.openshapa.views.OpenSHAPAFileChooser;
import org.openshapa.views.OpenSHAPAView;

/**
 * Controller for running scripts.
 */
public final class RunScriptC {

    /** the maximum size of the recently ran script list. */
    private static final int MAX_RECENT_SCRIPT_SIZE = 5;

    /**
     * Constructs and invokes the runscript controller.
     */
    public RunScriptC() {
        OpenSHAPAFileChooser jd = new OpenSHAPAFileChooser();
        jd.addChoosableFileFilter(new RBFilter());
        int result = jd.showOpenDialog(OpenSHAPA.getApplication()
                                                .getMainFrame());

        if (result == JFileChooser.APPROVE_OPTION) {
            runScript(jd.getSelectedFile());
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

    /**
     * Action for running a script.
     *
     * @param rubyFile The file of the ruby script to run.
     */
    public void runScript(final File rubyFile) {

        ScriptEngine rubyEngine = OpenSHAPA.getScriptingEngine();

        // Update the list of most recently used scripts.
        LinkedList<File> lastScripts = OpenSHAPA.getLastScriptsExecuted();
        // If the lastscripts is full - pull the last one off to make room.
        if (lastScripts.size() >= MAX_RECENT_SCRIPT_SIZE) {
            lastScripts.removeLast();
        }
        // Add the script to the list.
        if (!lastScripts.contains(rubyFile)) {
            lastScripts.addFirst(rubyFile);
            OpenSHAPA.setLastScriptsExecuted(lastScripts);
        }

        try {
            OpenSHAPA.getApplication().show(ConsoleV.getInstance());

            rubyEngine.getContext().setWriter(OpenSHAPA.getConsoleWriter());

            // Place a reference to the database within the scripting engine.
            rubyEngine.put("db", OpenSHAPA.getProject().getDB());

            FileReader reader = new FileReader(rubyFile);
            rubyEngine.eval(reader);
            reader = null;

            // Remove the reference to db
            rubyEngine.put("db", null);

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

        // Display any changes.
        OpenSHAPAView view = (OpenSHAPAView) OpenSHAPA.getApplication()
                                                      .getMainView();
        view.showSpreadsheet();
    }

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(RunScriptC.class);
}
