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
package org.datavyu.controllers;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import org.datavyu.Datavyu;
import org.datavyu.RecentFiles;
import org.datavyu.models.db.*;
import org.datavyu.util.FileFilters.RBFilter;
import org.datavyu.views.ConsoleV;
import org.datavyu.views.DatavyuFileChooser;
import rcaller.RCaller;
import rcaller.RCode;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.*;
import java.io.*;
import java.util.*;


/**
 * Controller for running scripts.
 */
public final class RunScriptC extends SwingWorker<Object, String> {

    /**
     * the maximum size of the recently ran script list.
     */
    private static final int MAX_RECENT_SCRIPT_SIZE = 5;

    /**
     * The path to the script file we are executing.
     */
    private final File scriptFile;

    /**
     * The View that the results of the scripting engine are displayed too.
     */
    private JTextArea console = null;

    /**
     * The logger for this class.
     */
    private static Logger LOGGER = UserMetrix.getLogger(RunScriptC.class);

    /**
     * output stream for messages coming from the scripting engine.
     */
    private PipedInputStream consoleOutputStream;
    private PipedInputStream consoleOutputStreamAfter;

    /**
     * input stream for displaying messages from the scripting engine.
     */
    private OutputStreamWriter consoleWriter;
    private OutputStreamWriter consoleWriterAfter;

    private OutputStream sIn;
    private OutputStream sIn2;

    private String outString = "";

    /**
     * Constructs and invokes the runscript controller.
     *
     * @throws IOException If Unable to create the run script controller.
     */
    public RunScriptC() throws IOException {
        DatavyuFileChooser jd = new DatavyuFileChooser();
        //jd.addChoosableFileFilter(RFilter.INSTANCE);
        jd.addChoosableFileFilter(RBFilter.INSTANCE);
        jd.setFileFilter(RBFilter.INSTANCE);

        int result = jd.showOpenDialog(Datavyu.getApplication()
                .getMainFrame());

        if (result == JFileChooser.APPROVE_OPTION) {
            scriptFile = jd.getSelectedFile();
            init();
        } else {
            scriptFile = null;
        }
    }

    public RunScriptC(File scriptFile) throws IOException {
        this.scriptFile = scriptFile;
        init();
    }

    /**
     * Constructs and invokes the runscript controller.
     *
     * @param file The absolute path to the script file you wish to invoke.
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
     *                     scripts.
     */
    private void init() throws IOException {
        Datavyu.getApplication().show(ConsoleV.getInstance());
        console = ConsoleV.getInstance().getConsole();

        consoleOutputStream = new PipedInputStream();
        consoleOutputStreamAfter = new PipedInputStream();
        sIn = new PipedOutputStream(consoleOutputStream);
        sIn2 = new PipedOutputStream(consoleOutputStreamAfter);
        consoleWriter = new OutputStreamWriter(sIn);
        consoleWriterAfter = new OutputStreamWriter(sIn2);
    }

    @Override
    protected Object doInBackground() {
        LOGGER.event("run script");

        ReaderThread t = new ReaderThread();
        t.start();

        RecentFiles.rememberScript(scriptFile);

        if (scriptFile.getName().endsWith(".rb")) {
            runRubyScript(scriptFile);
        } else if (scriptFile.getName().endsWith(".r") || scriptFile.getName().endsWith(".R")) {
            runRScript(scriptFile);
        }

        // Close the output stream to kill our reader thread
        try {
            consoleWriterAfter.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return null;
    }

    private void runRubyScript(File scriptFile) {
        outString = "";
        ScriptEngine rubyEngine = Datavyu.getScriptingEngine();

        try {
            try {
                consoleWriter.write("\n*************************");
                consoleWriter.write("\nRunning Script: " + scriptFile.getName() + " on project: " +  Datavyu.getProjectController().getProjectNamePretty());
                consoleWriter.write("\n*************************\n");

                consoleWriter.flush();

                // Place reference to various Datavyu functionality.
                rubyEngine.put("db", Datavyu.getProjectController().getDB());
                rubyEngine.put("pj", Datavyu.getProjectController().getProject());
                rubyEngine.put("mixer", Datavyu.getDataController().getMixerController());
                rubyEngine.put("viewers", Datavyu.getDataController());
                String path = System.getProperty("user.dir") + File.separator;
                
                rubyEngine.put("path", path);

                FileReader reader = new FileReader(scriptFile);
                String wholeScript = fileReaderIntoString(reader);
                //System.out.println(wholeScript);

                rubyEngine.getContext().setWriter(consoleWriter);
                rubyEngine.getContext().setErrorWriter(consoleWriter);
                rubyEngine.eval(wholeScript);
                //System.out.println("SCRIPT OVER");
                consoleWriter.close();

                reader = null;

                // Remove references.
                rubyEngine.put("db", null);
                rubyEngine.put("pj", null);
                rubyEngine.put("mixer", null);
                rubyEngine.put("viewers", null);

                consoleWriterAfter.write("\nScript completed successfully.");
                consoleWriterAfter.flush();
                consoleWriterAfter.close();

            } catch (ScriptException e) {
                consoleWriter.close();

                String msg = makeFriendlyRubyErrorMsg(outString, e);
                consoleWriterAfter.write("\n\n***** SCRIPT ERROR *****\n");
                consoleWriterAfter.write(msg);
                consoleWriterAfter.write("\n*************************\n");
                consoleWriterAfter.flush();

                System.out.println("Script Error");

                LOGGER.error("Unable to execute script: ", e);
            } catch (FileNotFoundException e) {
                consoleWriter.close();
                consoleWriterAfter.write("File not found: " + e.getMessage());;
                
                consoleWriterAfter.flush();
                LOGGER.error("Unable to execute script: ", e);
            }
        } catch (IOException ioe) {
            System.out.println("IOEXCEPTION!!!! " + ioe.getMessage());
            ioe.printStackTrace();
        }

    }

    private String fileReaderIntoString(FileReader fr) throws IOException {
        BufferedReader br = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder("");
        
        String cur = br.readLine();
        while(cur != null)
        {
            sb.append(cur);
            sb.append('\n'); //newlines in string are always '\n', never '\r'. Bug 193
            cur = br.readLine();
        }
        return sb.toString();
    }

    private String makeFriendlyRubyErrorMsg(String out, ScriptException e) {
        try {
            String s = "";
            //s should begin with ruby-relevant error portion, NOT full java stack
            //which would be of little interest to the user and only obscures what matters
            int endIndex = out.indexOf("org.jruby.embed.EvalFailedException:");
            if (endIndex == -1) s = out.substring(out.lastIndexOf('*') + 1);
            else s = out.substring(out.lastIndexOf('*') + 1, endIndex);

            //for each script error print the relevant line number. these are listed
            //in OPPOSITE of stack order so that the top of the stack (most likely to be
            //where the actual error lies), is the last thing shown and most apparent to user
            String linesOut = "";
            StringTokenizer outputTokenizer = new StringTokenizer(out, "\n");
            while (outputTokenizer.hasMoreTokens()) {
                String curLine = outputTokenizer.nextToken();
                int scriptTagIndex = curLine.lastIndexOf("<script>:");
                if (scriptTagIndex != -1) {
                    int errorLine = Integer.parseInt(curLine.substring(scriptTagIndex).replaceAll("[^0-9]", ""));
                    LineNumberReader scriptLNR = new LineNumberReader(new FileReader(scriptFile));
                    while (scriptLNR.getLineNumber() < errorLine - 1) scriptLNR.readLine(); //advance to errorLine
                    linesOut = "\nSee line " + errorLine + " of " + scriptFile + ":" + "\n" + scriptLNR.readLine() + linesOut;
                }
            }
            s += linesOut;
            return s;
        } catch (Exception e2) //if <script>: is not found in previous output, or other error occurs, default to exception's message
        {
            return e.getMessage();
        }
    }

    private void runRScript(File scriptFile) {
        // Initialize RCaller and tell it where the rscript application is
        RCaller caller = new RCaller();
        try {


            if (System.getProperty("os.name").startsWith("Windows")) {
                // We have to find it because Windows doesn't keep
                // anything in reasonable places.

                // We will check in two places: Program Files and then
                // Program Files (x86)


                String program_files = System.getenv("ProgramFiles");
                String program_files_x86 = System.getenv("ProgramFiles(x86)");

                if (new File(program_files + "/R/").exists()) {
                    // Loop over directories in here
                } else if (program_files_x86 != null && new File(program_files_x86 + "/R/").exists()) {
                    // Loop over directories in here
                }

                caller.setRscriptExecutable(null);
            } else {
                caller.setRscriptExecutable("/usr/bin/rscript");
            }
        } catch (Exception e) {
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
            Map.Entry pairs = (Map.Entry) it.next();

            // Now write this out to a temporary file
            File outfile = new File(System.getProperty("java.io.tmpdir"), (String) pairs.getKey());
            try {
                BufferedWriter output = new BufferedWriter(new FileWriter(outfile));
                output.write((String) pairs.getValue());
                output.close();

                temp_files.put((String) pairs.getKey(), outfile);
            } catch (Exception e) {
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
                Map.Entry pairs = (Map.Entry) it.next();

                String load = "db[[\"" + ((String) pairs.getKey()).toLowerCase() + "\"]] <- read.csv(\"" + ((File) pairs.getValue()).getPath() + "\",header=TRUE, sep=',')";
                code.addRCode(load);

                it.remove(); // avoids a ConcurrentModificationException
            }
        } catch (Exception e) {
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
            if (plt.length() > 0) {
                code.showPlot(plt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO
    private void updateDbFromR() {

    }

    /**
     * Convert the Datavyu database to a csv file per column.
     * This allows for easy reading into R.
     */
    private HashMap<String, String> convertDbToColStrings() {
        Datastore db = Datavyu.getProjectController().getDB();
        HashMap<String, String> str_db = new HashMap<String, String>();

        String str_var;
        for (Variable v : db.getAllVariables()) {
            str_var = "ordinal,onset,offset";
            if (v.getRootNode().type == Argument.Type.MATRIX) {
                for (Argument a : v.getRootNode().childArguments) {
                    str_var += "," + a.name;
                }
            } else {
                str_var += ",arg";
            }
            str_var += "\n";
            for (int i = 0; i < v.getCellsTemporally().size(); i++) {
                Cell c = v.getCellsTemporally().get(i);

                String row = String.format("%d,%d,%d", i + 1, c.getOnset(), c.getOffset());
                if (v.getRootNode().type == Argument.Type.MATRIX) {
                    for (Value val : ((MatrixValue) c.getValue()).getArguments()) {
                        row += ",";
                        if (!val.isEmpty())
                            row += val.toString();
                    }
                } else {
                    row += ",";
                    if (!c.getValue().isEmpty()) {
                        row += c.getValue().toString();
                    }
                }
                str_var += row + "\n";
            }
            str_db.put(v.getName(), str_var);
        }

        return str_db;
    }

    @Override
    protected void done() {

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
     * Separate thread for polling the incoming data from the scripting engine.
     * The data from the scripting engine gets placed directly into the
     * consoleOutput and also kept in outString for revisiting during
     * error reporting
     */
    class ReaderThread extends Thread {

        /**
         * The size of the buffer to use while ingesting data.
         */
        private static final int BUFFER_SIZE = 32 * 1024;

        /**
         * The method to invoke when the thread is started.
         */
        @Override
        public void run() {
            final byte[] buf = new byte[BUFFER_SIZE];

            int len;

            try {
                while ((len = consoleOutputStream.read(buf)) != -1) {
                    if (len > 0) {
                        // Publish output from script in the console.
                        String s = new String(buf, 0, len);
                        outString += s;
                        //System.out.println(s);
                        publish(s);
                    }

                    // Allow other threads to do stuff.
                    Thread.yield();

                }
                consoleOutputStream.close();
                //System.out.println("while switch");
                while ((len = consoleOutputStreamAfter.read(buf)) != -1) {
                    if (len > 0) {
                        // Publish output from script in the console.
                        String s = new String(buf, 0, len);
                        outString += s;
                        //System.out.println(s);
                        publish(s);
                    }

                    // Allow other threads to do stuff.
                    Thread.yield();


                }
                consoleOutputStreamAfter.close();
            } catch (IOException e) {
                LOGGER.error("Unable to run console thread.", e);
                e.printStackTrace();
            }
        }
    }
}
