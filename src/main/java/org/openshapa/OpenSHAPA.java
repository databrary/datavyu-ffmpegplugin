package org.openshapa;

import org.jdesktop.application.Application.ExitListener;
import org.openshapa.db.LogicErrorException;
import org.openshapa.db.MacshapaDatabase;
import org.openshapa.db.SystemErrorException;
import org.openshapa.util.Constants;
import org.openshapa.views.ListVariables;
import org.openshapa.views.OpenSHAPAView;
import org.openshapa.views.DataControllerV;
import java.awt.KeyEventDispatcher;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.application.Application;
import org.jdesktop.application.LocalStorage;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SessionStorage;
import org.jdesktop.application.SingleFrameApplication;
import org.openshapa.util.MacHandler;
import org.openshapa.views.AboutV;

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
     * with regard to the KeyEvent; false otherwise
     */
    public boolean dispatchKeyEvent(final KeyEvent evt) {
        /**
         * This switch is for hot keys that are on the main section of
         * the keyboard.
         */
        int modifiers = evt.getModifiers();
        if (evt.getID() == KeyEvent.KEY_PRESSED
            && evt.getKeyLocation() == KeyEvent.KEY_LOCATION_STANDARD) {

            // BugzID:468 - Define accelerator keys based on OS.
            int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
            switch (evt.getKeyCode()) {
                /**
                 * This case is because VK_PLUS is not linked to a key on the
                 * English keyboard.  So the GUI is bound to VK_PLUS and
                 * VK_SUBTACT.  VK_SUBTRACT is on the numpad, but this is
                 * short-circuited above.
                 * The cases return true to let the KeyboardManager know
                 * that there is nothing left to be done with these keys.
                 */
                case KeyEvent.VK_EQUALS:
                    if (modifiers == keyMask) {
                        view.changeFontSize(OpenSHAPAView.ZOOM_INTERVAL);
                    }
                    return true;
                case KeyEvent.VK_MINUS:
                    if (modifiers == keyMask) {
                        view.changeFontSize(-OpenSHAPAView.ZOOM_INTERVAL);
                    }
                    return true;
                default:
                    break;
            }
        }

        // BugzID:784 - Shift key is passed to Data Controller.
        if (evt.getKeyCode() == KeyEvent.VK_SHIFT) {
                if (evt.getID() == KeyEvent.KEY_PRESSED) {
                    dataController.setShiftMask(true);
                } else {
                    dataController.setShiftMask(false);
                }
        }

        // BugzID:736 - Control key is passed to Data Controller.
        if (evt.getKeyCode() == KeyEvent.VK_CONTROL) {
                if (evt.getID() == KeyEvent.KEY_PRESSED) {
                    dataController.setCtrlMask(true);
                } else {
                    dataController.setCtrlMask(false);
                }
        }

        /**
         * The following cases handle numpad keystrokes.
         */
        if (evt.getID() == KeyEvent.KEY_PRESSED
            && evt.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
            numKeyDown = true;
        } else if (numKeyDown && evt.getID() == KeyEvent.KEY_TYPED) {
            return true;
        }
        if (evt.getID() == KeyEvent.KEY_RELEASED
            && evt.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
            numKeyDown = false;
        }
        if (!numKeyDown) {
            return false;
        }

        boolean result = true;

        switch (evt.getKeyCode()) {
            case KeyEvent.VK_DIVIDE:
                dataController.pressSetCellOnset();
                break;
            case KeyEvent.VK_ASTERISK:
            case KeyEvent.VK_MULTIPLY:
                dataController.pressSetCellOffset();
                break;
            case KeyEvent.VK_NUMPAD7:
                dataController.pressRewind();
                break;
            case KeyEvent.VK_NUMPAD8:
                dataController.pressPlay();
                break;
            case KeyEvent.VK_NUMPAD9:
                dataController.pressForward();
                break;
            case KeyEvent.VK_NUMPAD4:
                dataController.pressShuttleBack();
                break;
            case KeyEvent.VK_NUMPAD2:
                dataController.pressPause();
                break;
            case KeyEvent.VK_NUMPAD6:
                dataController.pressShuttleForward();
                break;
            case KeyEvent.VK_NUMPAD1:
                dataController.pressJogBack();
                break;
            case KeyEvent.VK_NUMPAD5:
                dataController.pressStop();
                break;
            case KeyEvent.VK_NUMPAD3:
                dataController.pressJogForward();
                break;
            case KeyEvent.VK_NUMPAD0:
                dataController.pressCreateNewCellSettingOffset();
                break;
            case KeyEvent.VK_DECIMAL:
                dataController.pressSetNewCellOnset();
                break;
            case KeyEvent.VK_SUBTRACT:
                dataController.pressGoBack();
                break;
            case KeyEvent.VK_ADD:
                if (modifiers == KeyEvent.SHIFT_MASK) {
                    dataController.pressFind();
                    dataController.findOffsetAction();
                } else {
                    dataController.pressFind();
                }
                break;
            case KeyEvent.VK_ENTER:
                dataController.pressCreateNewCell();
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
        OpenSHAPA.getApplication().show(dataController);
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
     * Action for showing the about window.
     */
    public void showAboutWindow() {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        aboutWindow = new AboutV(mainFrame, false);
        OpenSHAPA.getApplication().show(aboutWindow);
    }

    /**
     * Show a warning dialog to the user.
     *
     * @param e The LogicErrorException to present to the user.
     */
    public void showWarningDialog(final LogicErrorException e) {
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
     * User quits- check for save needed. Note that this can be used even in
     * situations when the application is not truly "quitting", but just the
     * database information is being lost (e.g. on an "open" or "new"
     * instruction). In all interpretations, "true" indicates that all unsaved
     * changes are to be discarded.
     * @return True for quit, false otherwise.
     */
    public boolean safeQuit() {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(OpenSHAPA.class);
        if (db.getHasChanged()) {
            UIManager.put("OptionPane.cancelButtonText", "OK");
            UIManager.put("OptionPane.okButtonText", "Cancel");

            int sel =
            JOptionPane.showConfirmDialog(mainFrame,
                                      rMap.getString("ConfirmDialog.message"),
                                      rMap.getString("ConfirmDialog.title"),
                                      JOptionPane.OK_CANCEL_OPTION,
                                      JOptionPane.QUESTION_MESSAGE);


            UIManager.put("OptionPane.cancelButtonText", "Cancel");
            UIManager.put("OptionPane.okButtonText", "OK");

            // Note that this is actually "OK_OPTION", since we flipped
            // the buttons with UI manager.
            return (sel == JOptionPane.CANCEL_OPTION);

        } else {
            return true;
        }
    }


    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        try {

            // Configure logger - and start logging things.
            Properties logProps = new Properties();
            logProps.setProperty("log4j.rootLogger",
                                 "DEBUG, A1");
            logProps.setProperty("log4j.appender.A1",
                                 "org.apache.log4j.FileAppender");
            LocalStorage ls = OpenSHAPA.getApplication()
                                       .getContext().getLocalStorage();
            String logPath = ls.getDirectory().toString() + File.separator
                             + "OpenSHAPA.log";
            logProps.setProperty("log4j.appender.A1.file", logPath);

            logProps.setProperty("log4j.appender.A1.layout",
                                 "org.apache.log4j.PatternLayout");
            logProps.setProperty("log4j.appender.A1.layout.ConversionPattern",
                                 "%-4r [%t] %-5p %c %x - %m%n");
            PropertyConfigurator.configure(logProps);
            logger.info("Starting OpenSHAPA.");

            // Initalise DB
            db = new MacshapaDatabase();

            // BugzID:449 - Set default database name.
            db.setName("Database1");

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

        // Make view the new view so we can keep track of it for hotkeys.
        view = new OpenSHAPAView(this);
        show(view);

        // Simply clears the "unsaved" status, does not actually save.
        db.saveDatabase();

        getApplication().addExitListener(new ExitListenerImpl());

        // Create video controller.
        dataController = new DataControllerV(OpenSHAPA.getApplication().
                getMainFrame(), false);

        

    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     * @param root The parent window.
     */
    @Override
    protected void configureWindow(final java.awt.Window root) {
    }

    /**
     * Asks the main frame to update its title.
     */
    public void updateTitle() {
        if (view != null) {
            view.updateTitle();
        }
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
    public static MacshapaDatabase getDatabase() {
        return OpenSHAPA.getApplication().db;
    }

    /**
     * Gets the single instance of the data controller that is currently used
     * with OpenSHAPA.
     *
     * @return The single data controller in use with this instance of
     * OpenSHAPA.
     */
    public static DataControllerV getDataController() {
        return OpenSHAPA.getApplication().dataController;
    }

    /**
     * Sets the single instance of the database assocaited with the currently
     * running OpenSHAPA to the defined parameter.
     *
     * @param newDB The new database to use for this instance of OpenSHAPA.
     */
    public static void setDatabase(final MacshapaDatabase newDB) {
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
     * @param list List of scripts.
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
    public enum Platform { MAC, WINDOWS, UNKNOWN };

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
    public static void main(final String[] args) {
        // If we are running on a MAC set some additional properties:
        if (OpenSHAPA.getPlatform() == Platform.MAC) {
            try {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty(
                        "com.apple.mrj.application.apple.menu.about.name",
                        "OpenSHAPA");
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
                new MacHandler();
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
    private MacshapaDatabase db;

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
    private DataControllerV dataController;

    /** The view to use when displaying information about OpenSHAPA. */
    private AboutV aboutWindow;

    /** Tracks if a NumPad key has been pressed. */
    private boolean numKeyDown = false;

    /**
     * Constant variable for the OpenSHAPA main panel.  This is so we
     * can send keyboard shortcuts to it while the QTController is in focus.
     * It actually get initialized in startup().
     */
    private OpenSHAPAView view;

    /**
     * Handles exit requests.
     */
    private class ExitListenerImpl implements ExitListener {

        /**
         * Default constructor.
         */
        public ExitListenerImpl() {
        }

        /** Calls safeQuit to check if we can exit.
         *  @param arg0 The event generating the quit call.
         *  @return True if the application can quit, false otherwise.
         */
        public boolean canExit(final EventObject arg0) {
            return safeQuit();
        }

        /** Cleanup would occur here, but we choose to do nothing for now.
         *  @param arg0 The event generating the quit call.
         */
        public void willExit(final EventObject arg0) {
        }
    }
}
