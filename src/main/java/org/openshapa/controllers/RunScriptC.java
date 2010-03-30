package org.openshapa.controllers;

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

import com.usermetrix.jclient.UserMetrix;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import javax.swing.JTextArea;
import org.jdesktop.swingworker.SwingWorker;

/**
 * Controller for running scripts.
 */
public final class RunScriptC extends SwingWorker<Object, String> {

    /** the maximum size of the recently ran script list. */
    private static final int MAX_RECENT_SCRIPT_SIZE = 5;

    /** The path to the script file we are executing. */
    private final File scriptFile;

    /** Is the script currently running or not? */
    private boolean running = true;

    /** The View that the results of the scripting engine are displayed too. */
    private JTextArea console = null;

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(RunScriptC.class);

    /** output stream for messages coming from the scripting engine. */
    private PipedInputStream consoleOutputStream;

    /** input stream for displaying messages from the scripting engine. */
    private PrintWriter consoleWriter;

    /**
     * Constructs and invokes the runscript controller.
     */
    public RunScriptC() throws IOException {
        OpenSHAPAFileChooser jd = new OpenSHAPAFileChooser();
        jd.addChoosableFileFilter(new RBFilter());
        int result =
                jd.showOpenDialog(OpenSHAPA.getApplication().getMainFrame());

        if (result == JFileChooser.APPROVE_OPTION) {
            scriptFile = jd.getSelectedFile();
        } else {
            scriptFile = null;
        }

        this.init();
    }

    /**
     * Constructs and invokes the runscript controller.
     *
     * @param file The absolute path to the script file you wish to invoke.
     *
     * @throws IOException If unable to run the script.
     */
    public RunScriptC(final String file) throws IOException {
        scriptFile = new File(file);
        this.init();
    }

    /**
     * Initalises the controller for running scripts.
     *
     * @throws IOException If unable to initalise the controller for running
     * scripts.
     */
    private void init() throws IOException {
        OpenSHAPA.getApplication().show(ConsoleV.getInstance());
        console = ConsoleV.getInstance().getConsole();
        consoleOutputStream = new PipedInputStream();
        PipedOutputStream sIn = new PipedOutputStream(consoleOutputStream);
        consoleWriter = new PrintWriter(sIn);
    }

    @Override
    protected Object doInBackground() {
        ReaderThread t = new ReaderThread();
        t.start();
        ScriptEngine rubyEngine = OpenSHAPA.getScriptingEngine();

        // Update the list of most recently used scripts.
        LinkedList<File> lastScripts = OpenSHAPA.getLastScriptsExecuted();
        // If the lastscripts is full - pull the last one off to make room.
        if (lastScripts.size() >= MAX_RECENT_SCRIPT_SIZE) {
            lastScripts.removeLast();
        }
        // Add the script to the list.
        if (!lastScripts.contains(scriptFile)) {
            lastScripts.addFirst(scriptFile);
            OpenSHAPA.setLastScriptsExecuted(lastScripts);
        }

        try {
            rubyEngine.getContext().setWriter(consoleWriter);

            // Place a reference to the database within the scripting engine.
            rubyEngine.put("db", OpenSHAPA.getProjectController().getDB());

            FileReader reader = new FileReader(scriptFile);
            rubyEngine.eval(reader);
            reader = null;

            // Remove the reference to db
            rubyEngine.put("db", null);

        } catch (ScriptException e) {
            consoleWriter.println("***** SCRIPT ERRROR *****");
            consoleWriter.println("@Line " + e.getLineNumber() + ":'"
                    + e.getMessage() + "'");
            consoleWriter.println("*************************");
            consoleWriter.flush();

            logger.error("Unable to execute script: ", e);
        } catch (FileNotFoundException e) {
            logger.error("Unable to execute script: ", e);
        }

        running = false;
        return null;
    }

    @Override
    protected void done() {
        // Display any changes.
        OpenSHAPAView view = (OpenSHAPAView) OpenSHAPA.getApplication()
                                                      .getMainView();
        view.showSpreadsheet();
    }

    @Override
    protected void process(final List<String> chunks) {
        for (String chunk : chunks) {
            console.append(chunk);
            // Make sure the last line is always visible
            console.setCaretPosition(console.getDocument().getLength());
        }
    }

    /**
     * Seperate thread for polling the incoming data from the scripting engine.
     * The data from the scripting engine gets placed directly into the
     * consoleOutput
     */
    class ReaderThread extends Thread {
        /** The size of the buffer to use while ingesting data. */
        private static final int BUFFER_SIZE = 1024;

        /**
         * The method to invoke when the thread is started.
         */
        @Override
        public void run() {
            final byte[] buf = new byte[BUFFER_SIZE];
            try {
                while (running) {
                    final int len = consoleOutputStream.read(buf);
                    if (len > 0) {
                        // Publish output from script in the console.
                        String s = new String(buf, 0, len);
                        publish(s);
                    }

                    // Allow other threads to do stuff.
                    Thread.yield();
                }
            } catch (IOException e) {
                logger.error("Unable to run console thread.", e);
            }
        }
    }
}
