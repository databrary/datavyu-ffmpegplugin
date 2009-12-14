package org.openshapa.controllers;

import com.sun.script.jruby.JRubyScriptEngineManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JFileChooser;
import org.apache.log4j.Logger;
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
     * @return The scripting engine to use when running this script controller.
     */
    private ScriptEngine setupScripting() {
        // Initialise the scripting engine.
        ScriptEngine rubyEngine = null;
        // we need to avoid using the
        // javax.script.ScriptEngineManager, so that OpenSHAPA can work in
        // java 1.5. Instead we use the JRubyScriptEngineManager BugzID: 236
        JRubyScriptEngineManager m = new JRubyScriptEngineManager();

        // Whoops - JRubyScriptEngineManager may have failed, if that does
        // not construct engines for jruby correctly, switch to
        // javax.script.ScriptEngineManager
        if (m.getEngineFactories().size() == 0) {
            ScriptEngineManager m2 = new ScriptEngineManager();
            rubyEngine = m2.getEngineByName("jruby");
        } else {
            rubyEngine = m.getEngineByName("jruby");
        }

        return rubyEngine;
    }

    /**
     * Action for running a script.
     *
     * @param rubyFile The file of the ruby script to run.
     */
    public void runScript(final File rubyFile) {

        ScriptEngine rubyEngine = setupScripting();

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
            rubyEngine.put("db", OpenSHAPA.getDB());

            FileReader reader = new FileReader(rubyFile);
            rubyEngine.eval(reader);

            // Remove the reference to db
            rubyEngine.put("db", null);

            Reader reader1 = new StringReader("");
            rubyEngine.getContext().setReader(reader1);

            // Build output streams for the scripting engine.
            try {
                PipedInputStream consoleOutputStream = new PipedInputStream();
                PipedOutputStream sIn =
                                     new PipedOutputStream(consoleOutputStream);
                PrintWriter consoleWriter = new PrintWriter(sIn);
                rubyEngine.getContext().setWriter(consoleWriter);
            } catch (java.io.IOException e) {
                logger.error("Unable to execute script: ", e);
            }
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
    private static Logger logger = Logger.getLogger(RunScriptC.class);
}
