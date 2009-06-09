package org.openshapa;

import org.openshapa.controllers.CreateNewCellC;
import org.openshapa.db.Database;
import org.openshapa.db.LogicErrorException;
import org.openshapa.db.MacshapaDatabase;
import org.openshapa.db.SystemErrorException;
import org.openshapa.util.Constants;
import org.openshapa.views.ListVariables;
import org.openshapa.views.OpenSHAPAView;
import org.openshapa.views.QTVideoController;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
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
        if (evt.getID() != KeyEvent.KEY_PRESSED
            || evt.getKeyLocation() != KeyEvent.KEY_LOCATION_NUMPAD) {
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
                //this.createNewCell();
                new CreateNewCellC();
                break;
            default:
                // Do nothing with the key.
                result = false;
                break;
        }

        return result;
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
     * Show a warning dialog to the user.
     *
     * @param e The LogicErrorException to present to the user.
     */
    public void showWarningDialog(LogicErrorException e) {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(OpenSHAPA.class);

        JOptionPane.showMessageDialog(mainFrame,
                                      e.getMessage(),
                                      rMap.getString("WarningDialog.title"),
                                      JOptionPane.WARNING_MESSAGE);        
    }

    /**
     * Show a fatal error dialog to the user.
     *
     * @param e The SystemErrorException that caused this problem.
     */
    public void showErrorDialog() {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(OpenSHAPA.class);

        JOptionPane.showMessageDialog(mainFrame,
                                      rMap.getString("ErrorDialog.message"),
                                      rMap.getString("ErrorDialog.title"),
                                      JOptionPane.ERROR_MESSAGE);
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        try {            
            // Initalise DB
            db = new MacshapaDatabase();            

            // Initalise last created values
            lastCreatedCellID = 0;
            lastCreatedColID = 0;


            // Build output streams for the scripting engine.
            consoleOutputStream = new PipedInputStream();
            PipedOutputStream sIn = new PipedOutputStream(consoleOutputStream);
            consoleWriter = new PrintWriter(sIn);
            lastScriptsExecuted = new LinkedList<File>();

            // TODO- BugzID:79 This needs to move above showSpreadsheet,
            // when setTicks is fully implemented.
            db.setTicks(Constants.TICKS_PER_SECOND);
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
     * Sets the single instance of the database assocaited with the currently
     * running OpenSHAPA to the defined parameter.
     *
     * @param newDB The new database to use for this instance of OpenSHAPA.
     */
    public static void setDatabase(Database newDB) {
        OpenSHAPA.getApplication().db = newDB;
    }

    /**
     * @return The id of the last created cell.
     */
    public static long getLastCreatedCellId() {
        return OpenSHAPA.getApplication().lastCreatedCellID;
    }

    /**
     * Sets the id of the last created cell to the specified parameter.
     *
     * @param newId The Id of the newly created cell.
     */
    public static void setLastCreatedCellId(final long newId) {
        OpenSHAPA.getApplication().lastCreatedCellID = newId;
    }

    /**
     * @return The id of the last created column.
     */
    public static long getLastCreatedColId() {
        return OpenSHAPA.getApplication().lastCreatedColID;
    }

    /**
     * Sets the id of the last created column to the specified parameter.
     *
     * @param newId The Id of the newly created column.
     */
    public static void setLastCreatedColId(final long newId) {
        OpenSHAPA.getApplication().lastCreatedColID = newId;
    }

    /**
     * @return The list of last scripts that have been executed.
     */
    public static LinkedList<File> getLastScriptsExecuted() {
        return OpenSHAPA.getApplication().lastScriptsExecuted;
    }

    /**
     * Sets the list of scripts that were last executed.
     *
     * @param list
     */
    public static void setLastScriptsExecuted(final LinkedList<File> list) {
        OpenSHAPA.getApplication().lastScriptsExecuted = list;
    }

    /**
     * @return The console writer for OpenSHAPA.
     */
    public static PrintWriter getConsoleWriter() {
        return OpenSHAPA.getApplication().consoleWriter;
    }

    /**
     * @return The consoleoutput stream for OpenSHAPA.
     */
    public static PipedInputStream getConsoleOutputStream() {
        return OpenSHAPA.getApplication().consoleOutputStream;
    }

    /** All the supported platforms that OpenSHAPA runs on. */
    public enum Platform {MAC, WINDOWS, UNKNOWN};

    /**
     * @return The platform that OpenSHAPA is running on.
     */
    public static Platform getPlatform() {
        String os = System.getProperty("os.name");
        if (os.contains("Mac")) {
            return Platform.MAC;
        }

        if (os.contains("Win")) {
            return Platform.WINDOWS;
        }

        return Platform.UNKNOWN;
    }

    /**
     * Main method launching the application.
     *
     * @param args The command line arguments passed to OpenSHAPA.
     */
    public static void main(String[] args) {
        // Configure logger - and start logging things.
        PropertyConfigurator.configure("log4j.properties");
        logger.info("Starting OpenSHAPA.");

        // If we are running on a MAC set some additional properties:
        if (OpenSHAPA.getPlatform() == Platform.MAC) {
            try {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "OpenSHAPA");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException cnfe) {
                logger.error("Unable to start OpenSHAPA", cnfe);
            } catch (InstantiationException ie) {
                logger.error("Unable to start OpenSHAPA", ie);
            } catch (IllegalAccessException iae) {
                logger.error("Unable to start OpenSHAPA", iae);
            } catch (UnsupportedLookAndFeelException ulafe) {
                logger.error("Unable to start OpenSHAPA", ulafe);
            }
        }

        launch(OpenSHAPA.class, args);
    }

    /** The logger for OpenSHAPA. */
    private static Logger logger = Logger.getLogger(OpenSHAPA.class);

    /** The current database we are working on. */
    private Database db;

    /** output stream for messages coming from the scripting engine. */
    private PipedInputStream consoleOutputStream;

    /** input stream for displaying messages from the scripting engine. */
    private PrintWriter consoleWriter;

    /** The id of the last datacell that was created. */
    private long lastCreatedCellID;

    /** The id of the last datacell that was created. */
    private long lastCreatedColID;

    /** The list of scripts that the user has last invoked. */
    private LinkedList<File> lastScriptsExecuted;

    /** The view to use when listing all variables in the database. */
    private ListVariables listVarView;

    /** The view to use for the quick time video controller. */
    private QTVideoController qtVideoController;
}
