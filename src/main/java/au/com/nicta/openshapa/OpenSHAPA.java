package au.com.nicta.openshapa;

import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.MacshapaDatabase;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.views.ListVariables;
import au.com.nicta.openshapa.views.NewDatabase;
import au.com.nicta.openshapa.views.NewVariable;
import au.com.nicta.openshapa.views.OpenSHAPAView;
import au.com.nicta.openshapa.views.QTVideoController;
import au.com.nicta.openshapa.views.ScriptOutput;
import au.com.nicta.openshapa.views.discrete.Spreadsheet;
import com.sun.script.jruby.JRubyScriptEngineManager;
import java.awt.KeyEventDispatcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.JFrame;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.application.Application;
import org.jdesktop.application.SessionStorage;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public final class OpenSHAPA extends SingleFrameApplication
implements KeyEventDispatcher {

    /**
     * Dispatches the keystroke to the correct action.
     *
     * @param evt The event that triggered this action.
     *
     * @return true if the KeyboardFocusManager should take no further action
     * with regard to the KeyEvent; false  otherwise
     */
    public boolean dispatchKeyEvent(final KeyEvent evt) {
        if (evt.getID() != KeyEvent.KEY_PRESSED) {
            return false;
        }

        boolean result = true;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ASTERISK:
                qtVideoController.setCellOffsetAction();
                break;
            case KeyEvent.VK_NUMPAD7:
                qtVideoController.rewindAction();
                break;
            case KeyEvent.VK_NUMPAD8:
                qtVideoController.playAction();
                break;
            case KeyEvent.VK_NUMPAD9:
                qtVideoController.forwardAction();
                break;
/*            case KeyEvent.VK_MINUS:
                qtVideoController.goBackAction();
                break;
 */
            case KeyEvent.VK_NUMPAD4:
                qtVideoController.shuttleBackAction();
                break;
            case KeyEvent.VK_NUMPAD5:
                qtVideoController.pauseAction();
                break;
            case KeyEvent.VK_NUMPAD6:
                qtVideoController.shuttleForwardAction();
                break;
            case KeyEvent.VK_PLUS:
                qtVideoController.findAction();
                break;
            case KeyEvent.VK_NUMPAD1:
                qtVideoController.jogBackAction();
                break;
            case KeyEvent.VK_NUMPAD2:
                qtVideoController.stopAction();
                break;
            case KeyEvent.VK_NUMPAD3:
                qtVideoController.jogForwardAction();
                break;
            case KeyEvent.VK_NUMPAD0:
                qtVideoController.createNewCellAction();
                break;
            case KeyEvent.VK_ENTER:
                this.createNewCell();
                break;
            default:
                // Do nothing with the key.
                result = false;
                break;
        }

        return result;
    }

    /**
     * Action for running a script.
     *
     * @param rubyFile The file of the ruby script to run.
     */
    public void runScript(final File rubyFile) {
        try {
            JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
            if (scriptOutputView == null) {
                scriptOutputView = new ScriptOutput(mainFrame,
                                                    false,
                                                    scriptOutputStream);
            }
            OpenSHAPA.getApplication().show(scriptOutputView);

            // Place a reference to the database within the scripting engine.
            rubyEngine.put("db", db);

            FileReader reader = new FileReader(rubyFile);
            rubyEngine.eval(reader);
        } catch (ScriptException e) {
            scriptWriter.println("***** SCRIPT ERRROR *****");
            scriptWriter.println("@Line " + e.getLineNumber() + ":'"
                                 + e.getMessage() + "'");
            scriptWriter.println("*************************");
            scriptWriter.flush();

            logger.error("Unable to execute script: ", e);
        } catch (FileNotFoundException e) {
            logger.error("Unable to execute script: ", e);
        }
    }

    /**
     * Action for showing the quicktime video controller.
     */
    public void showQTVideoController() {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        qtVideoController = new QTVideoController(mainFrame, false);
        OpenSHAPA.getApplication().show(qtVideoController);
    }

    /**
     * Action for creating a new database.
     */
    public void showNewDatabaseForm() {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        newDBView = new NewDatabase(mainFrame, false,
                                    new NewDatabaseAction());
        OpenSHAPA.getApplication().show(newDBView);
    }

    /**
     * Action for creating a new variable.
     */
    public void showNewVariableForm() {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        newVarView = new NewVariable(mainFrame, false,
                                         new NewVariableAction());
        OpenSHAPA.getApplication().show(newVarView);

    }

    /**
     * Action for showing the variable list.
     */
    public void showVariableList() {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        listVarView = new ListVariables(mainFrame, false, db);
        try {
            db.registerColumnListListener(listVarView);
        } catch (SystemErrorException e) {
            logger.error("Unable register column list listener: ", e);
        }
        OpenSHAPA.getApplication().show(listVarView);
    }

    /**
     * Action for showing the spreadsheet.
     */
    public void showSpreadsheet() {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        spreadsheetView = new Spreadsheet(mainFrame, false, db);

        OpenSHAPA.getApplication().show(spreadsheetView);
    }

    /**
     * Creates new cell(s) based on the current "selections" in the database.
     * For each cell found selected, it creates a new cell with same onset
     * and offset. It's ordinal is set so that it comes immediately after.
     * If a column is found selected, it adds a blank cell at ordinal 1.
     */
    public void createNewCell() {
        spreadsheetView.createNewCell(-1);
    }

    /**
     * Create a new cell with given onset. Currently just appends to the
     * selected column or the column that last had a cell added to it.
     *
     * @param milliseconds The number of milliseconds since the origin of the
     * spreadsheet to create a new cell from.
     */
    public void createNewCell(final long milliseconds) {
        spreadsheetView.createNewCell(milliseconds);
    }

    /**
     * Sets the stop time of the last cell that was created.
     *
     * @param milliseconds The number of milliseconds since the origin of the
     * spreadsheet to set the stop time for.
     */
    public void setNewCellStopTime(final long milliseconds) {
        spreadsheetView.setNewCellStopTime(milliseconds);
    }

    /**
     * Run John's older test suit.
     *
     * @throws SystemErrorException If unable to run John's older test suit.
     */
    public void runRegressionTests() throws SystemErrorException {
        // TODO: is there a way to call the test package if it exists
        // or inform the user if it is not?
//        DatabaseTest.TestDatabase(System.out);
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        try {
            db = new MacshapaDatabase();

            // Build the ruby scripting engine - we need to avoid using the
            // javax.script.ScriptEngineManager, so that OpenSHAPA can work in
            // java 1.5. Instead we use the JRubyScriptEngineManager BugzID: 236
            JRubyScriptEngineManager m = new JRubyScriptEngineManager();
            rubyEngine = m.getEngineByName("jruby");

            // Build output streams for the scripting engine.
            scriptOutputStream = new PipedInputStream();
            PipedOutputStream sIn = new PipedOutputStream(scriptOutputStream);
            scriptWriter = new PrintWriter(sIn);
            rubyEngine.getContext().setWriter(scriptWriter);

            // TODO- BugzID:79 This needs to move above showSpreadsheet,
            // when setTicks is fully implemented.
            db.setTicks(TICKS_PER_SECOND);
        } catch (SystemErrorException e) {
            logger.error("Unable to create MacSHAPADatabase", e);
        } catch (IOException e) {
            logger.error("Unable to create scripting output streams", e);
        }

        show(new OpenSHAPAView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     *
     * @return The instance of the OpenSHAPA application.
     */
    public static OpenSHAPA getApplication() {
        return Application.getInstance(OpenSHAPA.class);
    }

    /**
     * A convenient static getter for the application session storage.
     *
     * @return The SessionStorage for the OpenSHAPA application.
     */
    public static SessionStorage getSessionStorage() {
        return OpenSHAPA.getApplication().getContext().getSessionStorage();
    }

    /**
     * Gets the single instance database associated with the currently running
     * OpenSHAPA.
     *
     * @return The single database in use with this instance of OpenSHAPA
     */
    public static Database getDatabase() {
        return OpenSHAPA.getApplication().db;
    }

    /**
     * Main method launching the application.
     *
     * @param args The command line arguments passed to OpenSHAPA.
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");

        logger.info("Starting OpenSHAPA.");
        launch(OpenSHAPA.class, args);
    }

    /** The logger for OpenSHAPA. */
    private static Logger logger = Logger.getLogger(OpenSHAPA.class);

    /** The current database we are working on. */
    private Database db;

    /** Ruby scripting engine to use for this instance of openshapa. */
    private ScriptEngine rubyEngine;

    /** output stream for messages coming from the scripting engine. */
    private PipedInputStream scriptOutputStream;

    /** input stream for displaying messages from the scripting engine. */
    private PrintWriter scriptWriter;

    /** The current spreadsheet view. */
    private Spreadsheet spreadsheetView;

    /** The view to use when creating new databases. */
    private NewDatabase newDBView;

    /** The view to use when creating a new variable. */
    private NewVariable newVarView;

    /** The view to use when listing all variables in the database. */
    private ListVariables listVarView;

    /** The view to use for the quick time video controller. */
    private QTVideoController qtVideoController;

    /** The view to use when displaying the output of a user invoked script. */
    private ScriptOutput scriptOutputView;

    /** The default number of ticks per second to use. */
    private final static int TICKS_PER_SECOND = 1000;

    /**
     * The action (controller) to invoke when a user creates a new database.
     */
    public class NewDatabaseAction implements ActionListener {
        /**
         * Action to invoke when a new database is created.
         *
         * @param evt The event that triggered this action.
         */
        public void actionPerformed(final ActionEvent evt) {
            try {
                db = new MacshapaDatabase();
                db.setName(newDBView.getDatabaseName());
                db.setDescription(newDBView.getDatabaseDescription());

                showSpreadsheet();

                // TODO- BugzID:79 This needs to move above showSpreadsheet,
                // when setTicks is fully implemented.
                db.setTicks(TICKS_PER_SECOND);
            } catch (SystemErrorException e) {
                logger.error("Unable to create new database", e);
            }
        }
    }

    /**
     * The action (controller) to invoke when a user adds a new variable to a
     * database.
     */
    class NewVariableAction implements ActionListener {
        /**
         * Action to invoke when a new variable is added to the database.
         *
         * @param evt The event that triggered this action.
         */
        public void actionPerformed(final ActionEvent evt) {
            try {
                DataColumn dc = new DataColumn(db, newVarView.getVariableName(),
                                               newVarView.getVariableType());
                db.addColumn(dc);
            } catch (SystemErrorException e) {
                logger.error("Unable to add variable to database", e);
            }
        }
    }
}
