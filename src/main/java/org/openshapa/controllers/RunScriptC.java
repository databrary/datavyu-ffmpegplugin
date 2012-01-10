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
package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;

import org.openshapa.OpenSHAPA;
import org.openshapa.RecentFiles;

import org.openshapa.util.FileFilters.RBFilter;

import org.openshapa.views.ConsoleV;
import org.openshapa.views.OpenSHAPAFileChooser;
import org.openshapa.views.OpenSHAPAView;

import com.usermetrix.jclient.UserMetrix;

import javax.swing.SwingWorker;


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
    private static Logger LOGGER = UserMetrix.getLogger(RunScriptC.class);

    /** output stream for messages coming from the scripting engine. */
    private PipedInputStream consoleOutputStream;

    /** input stream for displaying messages from the scripting engine. */
    private PrintWriter consoleWriter;

    /**
     * Constructs and invokes the runscript controller.
     *
     * @throws IOException If Unable to create the run script controller.
     */
    public RunScriptC() throws IOException {
        OpenSHAPAFileChooser jd = new OpenSHAPAFileChooser();
        jd.addChoosableFileFilter(RBFilter.INSTANCE);

        int result = jd.showOpenDialog(OpenSHAPA.getApplication()
                .getMainFrame());

        if (result == JFileChooser.APPROVE_OPTION) {
            scriptFile = jd.getSelectedFile();
            init();
        } else {
            scriptFile = null;
        }
    }

    /**
     * Constructs and invokes the runscript controller.
     *
     * @param file The absolute path to the script file you wish to invoke.
     *
     * @throws IOException If unable to create the run script controller.
     */
    public RunScriptC(final String file) throws IOException {
        scriptFile = new File(file);
        init();
    }
    
    public String getScriptFilePath() {
        return this.scriptFile.getAbsolutePath();
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

    @Override protected Object doInBackground() {
        LOGGER.event("run script");

        ReaderThread t = new ReaderThread();
        t.start();

        ScriptEngine rubyEngine = OpenSHAPA.getScriptingEngine();

        RecentFiles.rememberScript(scriptFile);

        try {
            rubyEngine.getContext().setWriter(consoleWriter);

            // Place reference to various OpenSHAPA functionality.
            rubyEngine.put("db", OpenSHAPA.getProjectController().getDB());
            rubyEngine.put("pj", OpenSHAPA.getProjectController().getProject());
            rubyEngine.put("mixer", OpenSHAPA.getDataController().getMixerController());
            rubyEngine.put("viewers", OpenSHAPA.getDataController());

            FileReader reader = new FileReader(scriptFile);
            rubyEngine.eval(reader);
            reader = null;

            // Remove references.
            rubyEngine.put("db", null);
            rubyEngine.put("pj", null);
            rubyEngine.put("mixer", null);
            rubyEngine.put("viewers", null);

        } catch (ScriptException e) {
            consoleWriter.println("***** SCRIPT ERRROR *****");
            consoleWriter.println("@Line " + e.getLineNumber() + ":'"
                + e.getMessage() + "'");
            consoleWriter.println("*************************");
            consoleWriter.flush();

            LOGGER.error("Unable to execute script: ", e);
        } catch (FileNotFoundException e) {
            LOGGER.error("Unable to execute script: ", e);
        }

        running = false;

        return null;
    }

    @Override protected void done() {

        // Display any changes.
        OpenSHAPAView view = (OpenSHAPAView) OpenSHAPA.getApplication()
            .getMainView();
        view.showSpreadsheet();
    }

    @Override protected void process(final List<String> chunks) {

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
        @Override public void run() {
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
                LOGGER.error("Unable to run console thread.", e);
            }
        }
    }
}
