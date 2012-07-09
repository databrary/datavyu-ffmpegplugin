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
import org.openshapa.util.FileFilters.RFilter;

import org.openshapa.views.ConsoleV;
import org.openshapa.views.OpenSHAPAFileChooser;
import org.openshapa.views.OpenSHAPAView;

import com.usermetrix.jclient.UserMetrix;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.SwingWorker;
import org.openshapa.models.db.Argument;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.Datastore;
import org.openshapa.models.db.MatrixValue;
import org.openshapa.models.db.Value;
import org.openshapa.models.db.Variable;
import rcaller.RCaller;
import rcaller.RCode;


/**
 * Controller for running scripts.
 */
public final class RunScriptC extends SwingWorker<Object, String> {

    /** the maximum size of the recently ran script list. */
    private static final int MAX_RECENT_SCRIPT_SIZE = 5;

    /** The path to the script file we are executing. */
    private final File scriptFile;

    /** The View that the results of the scripting engine are displayed too. */
    private JTextArea console = null;

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(RunScriptC.class);

    /** output stream for messages coming from the scripting engine. */
    private PipedInputStream consoleOutputStream;

    /** input stream for displaying messages from the scripting engine. */
    private PrintWriter consoleWriter;
    
    private OutputStream sIn;

    /**
     * Constructs and invokes the runscript controller.
     *
     * @throws IOException If Unable to create the run script controller.
     */
    public RunScriptC() throws IOException {
        OpenSHAPAFileChooser jd = new OpenSHAPAFileChooser();
        jd.addChoosableFileFilter(RFilter.INSTANCE);
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

        sIn = new PipedOutputStream(consoleOutputStream);
        consoleWriter = new PrintWriter(sIn);
    }

    @Override protected Object doInBackground() {
        LOGGER.event("run script");

        ReaderThread t = new ReaderThread();
        t.start();

        RecentFiles.rememberScript(scriptFile);
        
        if(scriptFile.getName().endsWith(".rb")) {
            runRubyScript(scriptFile);
        }
        else if(scriptFile.getName().endsWith(".r") || scriptFile.getName().endsWith(".R")) {
            runRScript(scriptFile);
        }
	
	// Close the output stream to kill our reader thread
	try {
	    consoleOutputStream.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
                
        return null;
    }
    
    private void runRubyScript(File scriptFile) {
	    ScriptEngine rubyEngine = OpenSHAPA.getScriptingEngine();
	    rubyEngine.getContext().setWriter(consoleWriter);
            try {
			
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
		
		consoleWriter.println("\nScript completed successfully.");

            } catch (ScriptException e) {
                consoleWriter.println("***** SCRIPT ERRROR *****");
                consoleWriter.println("@Line " + e.getLineNumber() + ":\n   '"
                    + e.getMessage() + "'");
                consoleWriter.println("*************************");
                consoleWriter.flush();

                LOGGER.error("Unable to execute script: ", e);
            } catch (FileNotFoundException e) {
		consoleWriter.println("File not found script");
                LOGGER.error("Unable to execute script: ", e);
	    }
    }
    
    private void runRScript(File scriptFile) {
            // Initialize RCaller and tell it where the rscript application is
	    RCaller caller = new RCaller();
            try {
		
		
		if(System.getProperty("os.name").startsWith("Windows")) {
			// We have to find it because Windows doesn't keep
			// anything in reasonable places.
			
			// We will check in two places: Program Files and then
			// Program Files (x86)
			
			
			String program_files = System.getenv("ProgramFiles");
			String program_files_x86 = System.getenv("ProgramFiles(x86)");
			
			if(new File(program_files + "/R/").exists()) {
				// Loop over directories in here
			} else if (program_files_x86 != null && new File(program_files_x86 + "/R/").exists()) {
				// Loop over directories in here
			}
			
			caller.setRscriptExecutable(null);
		} else {
			caller.setRscriptExecutable("/usr/bin/rscript");
		}
            }
            catch(Exception e) {
                // Ut oh, R isn't installed.
		e.printStackTrace();
            }
            caller.redirectROutputToStream(sIn);
            
            // Initialize our code buffer and database string representation
            RCode code = new RCode();
            HashMap db = convertDbToColStrings();
            HashMap<String, File> temp_files = new HashMap<String, File>();
            
            // Write the database out to tempory files
            Iterator it = db.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                
                // Now write this out to a temporary file
                File outfile = new File(System.getProperty("java.io.tmpdir"), (String)pairs.getKey());
                try {
                    BufferedWriter output = new BufferedWriter(new FileWriter(outfile));
                    output.write((String)pairs.getValue());
                    output.close();
                    
                    temp_files.put((String)pairs.getKey(), outfile);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                                
                it.remove(); // avoids a ConcurrentModificationException
            }
            
            // Create the R code to read in the temporary db files into a structure
            // called db
            code.addRCode("db <- list()");
            try {
                // Load each of the temporary files created above into R
                it = temp_files.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    
                    String load = "db[[\"" + ((String)pairs.getKey()).toLowerCase() + "\"]] <- read.csv(\"" + ((File)pairs.getValue()).getPath() + "\",header=TRUE, sep=',')";
                    code.addRCode(load);

                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            
            // Set up plotting. If something gets plotted, display it.
            // Otherwise, just run the code.
            try {
                File plt = code.startPlot();
                code.R_source(scriptFile.getPath());
                caller.setRCode(code);
                caller.runOnly();
                code.endPlot();
                if(plt.length() > 0) {
                    code.showPlot(plt);
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
    }
    
    // TODO
    private void updateDbFromR() {
        
    }
    
    /** Convert the OpenSHAPA database to a csv file per column.
        This allows for easy reading into R. */
    private HashMap<String, String> convertDbToColStrings() {
        Datastore db = OpenSHAPA.getProjectController().getDB();
        HashMap<String, String> str_db = new HashMap<String, String>();

        String str_var;
        for(Variable v : db.getAllVariables()) {
            str_var = "ordinal,onset,offset";
            if(v.getVariableType().type == Argument.Type.MATRIX) {
                for(Argument a : v.getVariableType().childArguments) {
                    str_var += "," + a.name;
                }
            }
            else {
                str_var += ",arg";
            }
            str_var += "\n";
            for(int i = 0; i < v.getCellsTemporally().size(); i++) {
                Cell c = v.getCellsTemporally().get(i);
                
                String row = String.format("%d,%d,%d", i+1, c.getOnset(), c.getOffset());
                if(v.getVariableType().type == Argument.Type.MATRIX) {
                    for(Value val : ((MatrixValue)c.getValue()).getArguments()) {
                        row += ",";
                        if(!val.isEmpty())
                            row += val.toString();
                    }
                }
                else {
                    row += ",";
                    if(!c.getValue().isEmpty()) {
                        row += c.getValue().toString();
                    }
                }
                str_var += row + "\n";
            }
            str_db.put(v.getName(), str_var);
        }
        
        return str_db;
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
	    
	    int len;

            try {
                while ((len = consoleOutputStream.read(buf)) != -1) {
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
