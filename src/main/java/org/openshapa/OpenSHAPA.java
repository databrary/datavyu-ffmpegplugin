package org.openshapa;

import java.awt.KeyEventDispatcher;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.File;

import java.util.EventObject;
import java.util.List;
import java.util.Stack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.application.Application;
import org.jdesktop.application.LocalStorage;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SessionStorage;
import org.jdesktop.application.SingleFrameApplication;

import org.openshapa.controllers.project.ProjectController;

import org.openshapa.models.db.LogicErrorException;
import org.openshapa.models.db.MacshapaDatabase;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.project.Project;

import org.openshapa.util.Constants;
import org.openshapa.util.MacHandler;

import org.openshapa.views.AboutV;
import org.openshapa.views.DataControllerV;
import org.openshapa.views.ListVariables;
import org.openshapa.views.OpenSHAPAView;
import org.openshapa.views.UserMetrixV;
import org.openshapa.views.continuous.PluginManager;

import com.sun.script.jruby.JRubyScriptEngineManager;
import com.usermetrix.jclient.Logger;

import com.usermetrix.jclient.UserMetrix;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.LinkedList;
import javax.swing.SwingUtilities;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.swing.VideoComponent;
import org.openshapa.util.NativeLoader;


/**
 * The main class of the application.
 */
public final class OpenSHAPA extends SingleFrameApplication
    implements KeyEventDispatcher {

    /** The desired minimum initial width. */
    private static final int INITMINX = 600;

    /** The desired minimum initial height. */
    private static final int INITMINY = 700;

    /**
     * Constant variable for the OpenSHAPA main panel. This is so we can send
     * keyboard shortcuts to it while the QTController is in focus. It actually
     * get initialized in startup().
     */
    private static OpenSHAPAView VIEW;

    /** the maximum size of the recently ran script list. */
    private static final int MAX_RECENT_SCRIPT_SIZE = 5;

    /** the maximum size of the recently opened files list. */
    private static final int MAX_RECENT_FILE_SIZE = 5;

    /** All the supported platforms that OpenSHAPA runs on. */
    public enum Platform {
        /** Generic Mac platform. I.e. Tiger, Leopard, Snow Leopard. */
        MAC,

        /** Generic windows platform. I.e. XP, vista, etc. */
        WINDOWS,

        /** Unknown platform. */
        UNKNOWN
    }

    /** The scripting engine that we use with OpenSHAPA. */
    private ScriptEngine rubyEngine;

    /** The scripting engine manager that we use with OpenSHAPA. */
    private ScriptEngineManager m2;

    /** The JRuby scripting engine manager that we use with OpenSHAPA. */
    private JRubyScriptEngineManager m;

    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(OpenSHAPA.class);

    /** The list of scripts that the user has last invoked. */
    private List<File> lastScriptsExecuted;

    /** The list of files that the user last opened. */
    private List<File> lastFilesOpened;

    /** The view to use when listing all variables in the database. */
    private ListVariables listVarView;

    /** The view to use for the quick time video controller. */
    private DataControllerV dataController;

    /** The view to use when displaying information about OpenSHAPA. */
    private AboutV aboutWindow;

    /** Tracks if a NumPad key has been pressed. */
    private boolean numKeyDown = false;

    /** Tracks whether or not databases are allowed to set unsaved status. */
    private boolean canSetUnsaved = false;

    /** The current project. */
    private ProjectController projectController;

    /** Opened windows. */
    private Stack<Window> windows;

    /**
     * Dispatches the keystroke to the correct action.
     *
     * @param evt
     *            The event that triggered this action.
     * @return true if the KeyboardFocusManager should take no further action
     *         with regard to the KeyEvent; false otherwise
     */
    public boolean dispatchKeyEvent(final KeyEvent evt) {

        /**
         * This switch is for hot keys that are on the main section of the
         * keyboard.
         */
        int modifiers = evt.getModifiers();

        // BugzID:468 - Define accelerator keys based on OS.
        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();


        // If we are typing a key that is a shortcut - we consume it straight
        // away.
        if ((evt.getID() == KeyEvent.KEY_TYPED) && (modifiers == keyMask)) {

            switch (evt.getKeyChar()) {

            case '+':
            case '-':
            case 'o':
            case 's':
            case 'n':
            case 'l':
            case 'r':
                evt.consume();
                return true;

            default:
                break;
            }
        }

        if ((evt.getID() == KeyEvent.KEY_PRESSED)
                && (evt.getKeyLocation() == KeyEvent.KEY_LOCATION_STANDARD)) {

            switch (evt.getKeyCode()) {

            /**
             * This case is because VK_PLUS is not linked to a key on the
             * English keyboard. So the GUI is bound to VK_PLUS and VK_SUBTACT.
             * VK_SUBTRACT is on the numpad, but this is short-circuited above.
             * The cases return true to let the KeyboardManager know that there
             * is nothing left to be done with these keys.
             */
            case KeyEvent.VK_EQUALS:

                if (modifiers == keyMask) {
                    VIEW.changeFontSize(OpenSHAPAView.ZOOM_INTERVAL);
                }

                return true;

            case KeyEvent.VK_MINUS:

                if (modifiers == keyMask) {
                    VIEW.changeFontSize(-OpenSHAPAView.ZOOM_INTERVAL);
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
        if ((evt.getID() == KeyEvent.KEY_PRESSED)
                && (evt.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD)) {
            numKeyDown = true;
        } else if (numKeyDown && (evt.getID() == KeyEvent.KEY_TYPED)) {
            return true;
        }

        if ((evt.getID() == KeyEvent.KEY_RELEASED)
                && (evt.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD)) {
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

            // We don't do the press Jog thing for jogging - as users often
            // just hold the button down... Which causes weird problems when
            // attempting to do multiple presses.
            dataController.jogBackAction();

            break;

        case KeyEvent.VK_NUMPAD5:
            dataController.pressStop();

            break;

        case KeyEvent.VK_NUMPAD3:

            // We don't do the press Jog thing for jogging - as users often
            // just hold the button down... Which causes weird problems when
            // attempting to do multiple presses.
            dataController.jogForwardAction();

            break;

        case KeyEvent.VK_NUMPAD0:
            dataController.pressCreateNewCellSettingOffset();

            break;

        case KeyEvent.VK_DECIMAL:
            dataController.pressSetNewCellOnset();

            break;

        case KeyEvent.VK_SUBTRACT:
            if (modifiers == InputEvent.CTRL_MASK) {
                dataController.clearRegionOfInterestAction();
            } else {
                dataController.pressGoBack();
            }

            break;

        case KeyEvent.VK_ADD:

            if (modifiers == InputEvent.SHIFT_MASK) {
                dataController.pressFind();
                dataController.findOffsetAction();
            } else if (modifiers == InputEvent.CTRL_MASK) {
                dataController.pressFind();
                dataController.setRegionOfInterestAction();
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
    public void showDataController() {
        OpenSHAPA.getApplication().show(dataController);
    }

    /**
     * Action for showing the variable list.
     */
    public void showVariableList() {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        listVarView = new ListVariables(mainFrame, false,
                projectController.getDB());

        try {
            projectController.getDB().registerColumnListListener(listVarView);
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
     * @param e
     *            The LogicErrorException to present to the user.
     */
    public void showWarningDialog(final LogicErrorException e) {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class).getContext()
            .getResourceMap(OpenSHAPA.class);

        JOptionPane.showMessageDialog(mainFrame, e.getMessage(),
            rMap.getString("WarningDialog.title"), JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show a fatal error dialog to the user.
     */
    public void showErrorDialog() {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class).getContext()
            .getResourceMap(OpenSHAPA.class);

        JOptionPane.showMessageDialog(mainFrame,
            rMap.getString("ErrorDialog.message"),
            rMap.getString("ErrorDialog.title"), JOptionPane.ERROR_MESSAGE);
    }

    /**
     * User quits- check for save needed. Note that this can be used even in
     * situations when the application is not truly "quitting", but just the
     * database information is being lost (e.g. on an "open" or "new"
     * instruction). In all interpretations, "true" indicates that all unsaved
     * changes are to be discarded.
     *
     * @return True for quit, false otherwise.
     */
    public boolean safeQuit() {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class).getContext()
            .getResourceMap(OpenSHAPA.class);

        if (projectController.isChanged()) {

            String cancel = "Cancel";
            String ok = "OK";

            String[] options = new String[2];

            if (getPlatform() == Platform.MAC) {
                options[0] = cancel;
                options[1] = ok;
            } else {
                options[0] = ok;
                options[1] = cancel;
            }

            int selection = JOptionPane.showOptionDialog(mainFrame,
                    rMap.getString("UnsavedDialog.message"),
                    rMap.getString("UnsavedDialog.title"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, cancel);

            // Button behaviour is platform dependent.
            return (getPlatform() == Platform.MAC) ? (selection == 1)
                                                   : (selection == 0);
        } else {

            // Project hasn't been changed.
            return true;
        }
    }

    /**
     * Action to call when the application is exiting.
     *
     * @param event
     *            The event that triggered this action.
     */
    @Override protected void end() {
        OpenSHAPA.getApplication().getMainFrame().setVisible(false);
        UserMetrix.shutdown();
        super.end();
    }

    /**
     * If the user is trying to save over an existing file, prompt them whether
     * they they wish to continue.
     *
     * @return True for overwrite, false otherwise.
     */
    public boolean overwriteExisting() {
        JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class).getContext()
            .getResourceMap(OpenSHAPA.class);
        String defaultOpt = "Cancel";
        String altOpt = "Overwrite";

        String[] a = new String[2];

        if (getPlatform() == Platform.MAC) {
            a[0] = defaultOpt; // This has int value 0 if selected
            a[1] = altOpt; // This has int value 1 if selected.
        } else {
            a[1] = defaultOpt; // This has int value 1 if selected
            a[0] = altOpt; // This has int value 0 if selected.
        }

        int sel =

            JOptionPane.showOptionDialog(mainFrame,
                rMap.getString("OverwriteDialog.message"),
                rMap.getString("OverwriteDialog.title"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, a, defaultOpt);

        // Button depends on platform now.
        if (getPlatform() == Platform.MAC) {
            return (sel == 1);
        } else {
            return (sel == 0);
        }
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        windows = new Stack<Window>();

        try {

            // Initalise the logger (UserMetrix).
            LocalStorage ls = OpenSHAPA.getApplication().getContext()
                .getLocalStorage();
            ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                .getContext().getResourceMap(OpenSHAPA.class);

            com.usermetrix.jclient.Configuration config =
                new com.usermetrix.jclient.Configuration(2);
            config.setTmpDirectory(ls.getDirectory().toString()
                + File.separator);
            config.addMetaData("build",
                rMap.getString("Application.version") + ":"
                + rMap.getString("Application.build"));
            UserMetrix.initalise(config);
            logger = UserMetrix.getLogger(OpenSHAPA.class);

            // If the user hasn't specified, we don't send error logs.
            if (Configuration.getInstance().getCanSendLogs() == null) {
                UserMetrix.setCanSendLogs(false);
            } else {
                UserMetrix.setCanSendLogs(Configuration.getInstance()
                    .getCanSendLogs());
            }

            // Initalise scripting engine
            rubyEngine = null;

            // we need to avoid using the
            // javax.script.ScriptEngineManager, so that OpenSHAPA can work in
            // java 1.5. Instead we use the JRubyScriptEngineManager BugzID: 236
            m = new JRubyScriptEngineManager();

            // Whoops - JRubyScriptEngineManager may have failed, if that does
            // not construct engines for jruby correctly, switch to
            // javax.script.ScriptEngineManager
            if (m.getEngineFactories().size() == 0) {
                m2 = new ScriptEngineManager();
                rubyEngine = m2.getEngineByName("jruby");
            } else {
                rubyEngine = m.getEngineByName("jruby");
            }

            // Make a new project
            projectController = new ProjectController();
            lastScriptsExecuted = new LinkedList<File>();
            lastFilesOpened = new LinkedList<File>();

            // Initalise DB
            MacshapaDatabase db = new MacshapaDatabase(
                    Constants.TICKS_PER_SECOND);

            // BugzID:449 - Set default database name.
            db.setName("Database1");
            projectController.setDatabase(db);

            // Initialize plugin manager
            PluginManager.getInstance();

        } catch (SystemErrorException e) {
            logger.error("Unable to create MacSHAPADatabase", e);
        }

        // Make view the new view so we can keep track of it for hotkeys.
        VIEW = new OpenSHAPAView(this);
        show(VIEW);

        // Now that openshapa is up - we may need to ask the user if can send
        // gather logs.
        if (Configuration.getInstance().getCanSendLogs() == null) {
            logger.usage("show usermetrix dialog");
            show(new UserMetrixV(VIEW.getFrame(), true));
        }

        // BugzID:435 - Correct size if a small size is detected.
        int width = (int) getMainFrame().getSize().getWidth();
        int height = (int) getMainFrame().getSize().getHeight();

        if ((width < INITMINX) || (height < INITMINY)) {
            int x = Math.max(width, INITMINX);
            int y = Math.max(height, INITMINY);
            getMainFrame().setSize(x, y);
        }

        updateTitle();

        // Allow changes to the database to propagate up and signify db modified
        canSetUnsaved = true;

        getApplication().addExitListener(new ExitListenerImpl());

        // Create video controller.
        dataController = new DataControllerV(OpenSHAPA.getApplication()
                .getMainFrame(), false);

    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     *
     * @param root
     *            The parent window.
     */
    @Override protected void configureWindow(final java.awt.Window root) {
    }

    /**
     * Add a recently opened script file to the list of recently opened scripts.
     *
     * @param file The file to add.
     */
    public void addScriptFile(final File file) {

        if (!lastScriptsExecuted.contains(file)) {

            if (lastScriptsExecuted.size() == MAX_RECENT_SCRIPT_SIZE) {
                lastScriptsExecuted.remove(MAX_RECENT_SCRIPT_SIZE - 1);
            }

            lastScriptsExecuted.add(0, file);
        }
    }

    /**
     * Add a recently opened project file to the list of recently opened files.
     * @param file
     */
    public void addProjectFile(final File file) {

        if (!lastFilesOpened.contains(file)) {
            if (lastFilesOpened.size() == MAX_RECENT_FILE_SIZE) {
                lastFilesOpened.remove(MAX_RECENT_FILE_SIZE - 1);
            }

            lastFilesOpened.add(0, file);
        }
    }

    /**
     * Asks the main frame to update its title.
     */
    public void updateTitle() {

        if (VIEW != null) {
            VIEW.updateTitle();
        }
    }

    /** @return canSetUnsaved */
    public boolean getCanSetUnsaved() {
        return canSetUnsaved;
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
     * @return The single instance of the scripting engine we use with
     *         OpenSHAPA.
     */
    public static ScriptEngine getScriptingEngine() {
        return OpenSHAPA.getApplication().rubyEngine;
    }

    /**
     * Gets the single instance project associated with the currently running
     * with OpenSHAPA.
     *
     * @return The single project in use with this instance of OpenSHAPA
     */
    public static ProjectController getProjectController() {
        return OpenSHAPA.getApplication().projectController;
    }

    /**
     * Creates a new project controller.
     */
    public static void newProjectController() {
        OpenSHAPA.getApplication().projectController = new ProjectController();
    }

    /**
     * Creates a new project controller, using the given project as the
     * underlying project.
     *
     * @param project
     */
    public static void newProjectController(final Project project) {
        OpenSHAPA.getApplication().projectController = new ProjectController(
                project);
    }

    /**
     * Gets the single instance of the data controller that is currently used
     * with OpenSHAPA.
     *
     * @return The single data controller in use with this instance of
     *         OpenSHAPA.
     */
    public static DataControllerV getDataController() {
        return OpenSHAPA.getApplication().dataController;
    }

    /**
     * @return The list of last scripts that have been executed, ordered by the
     *  most recent first.
     */
    public static Iterable<File> getLastScriptsExecuted() {
        return OpenSHAPA.getApplication().lastScriptsExecuted;
    }

    /**
     * @return The list of last opened files, ordered by the most recent first.
     */
    public static Iterable<File> getLastFilesOpened() {
        return OpenSHAPA.getApplication().lastFilesOpened;
    }

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

    private static Pipeline pipe;

    /**
     * Main method launching the application.
     *
     * @param args
     *            The command line arguments passed to OpenSHAPA.
     */
    public static void main(final String[] args) {
        System.setProperty("jna.library.path", "/var/folders/f9/f98AOhp5Fv4Ib4z8cafuXU+++TI/-Tmp-/gstreamer-macosx-1.4");

        Gst.init("VideoTest", args);
        /*
        pipe = new Pipeline("VideoTest");
        final Element videosrc = ElementFactory.make("videotestsrc", "source");
        final Element videofilter = ElementFactory.make("capsfilter", "filter");
        videofilter.setCaps(Caps.fromString("video/x-raw-yuv, width=720, height=576"
                + ", bpp=32, depth=32, framerate=25/1"));
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                VideoComponent videoComponent = new VideoComponent();
                Element videosink = videoComponent.getElement();
                pipe.addMany(videosrc, videofilter, videosink);
                Element.linkMany(videosrc, videofilter, videosink);

                // Now create a JFrame to display the video output
                JFrame frame = new JFrame("Swing Video Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(videoComponent, BorderLayout.CENTER);
                videoComponent.setPreferredSize(new Dimension(720, 576));
                frame.pack();
                frame.setVisible(true);

                // Start the pipeline processing
                pipe.setState(State.PLAYING);
            }
        });
        */

        // If we are running on a MAC set some additional properties:
        if (OpenSHAPA.getPlatform() == Platform.MAC) {

            try {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name",
                    "OpenSHAPA");
                UIManager.setLookAndFeel(UIManager
                    .getSystemLookAndFeelClassName());
                new MacHandler();
            } catch (ClassNotFoundException cnfe) {
                System.err.println("Unable to start OpenSHAPA");
                // logger.error("Unable to start OpenSHAPA", cnfe);
            } catch (InstantiationException ie) {
                System.err.println("Unable to instantiate OpenSHAPA");
            } catch (IllegalAccessException iae) {
                System.err.println("Unable to access OpenSHAPA");
            } catch (UnsupportedLookAndFeelException ulafe) {
                System.err.println("Unsupporter look and feel exception");
            }
        }

        launch(OpenSHAPA.class, args);
    }

    @Override public void show(final JDialog dialog) {

        if (windows == null) {
            windows = new Stack<Window>();
        }

        windows.push(dialog);
        super.show(dialog);
    }

    @Override public void show(final JFrame frame) {

        if (windows == null) {
            windows = new Stack<Window>();
        }

        windows.push(frame);
        super.show(frame);
    }

    public void resetApp() {
        closeOpenedWindows();
        this.dataController.dispose();
        this.dataController = new DataControllerV(OpenSHAPA.getApplication()
                .getMainFrame(), false);
    }

    public void closeOpenedWindows() {

        if (windows == null) {
            windows = new Stack<Window>();
        }

        while (!windows.empty()) {
            Window window = windows.pop();
            window.setVisible(false);
            window.dispose();
        }
    }

    public static OpenSHAPAView getView() {
        return VIEW;
    }

    /**
     * Handles exit requests.
     */
    private class ExitListenerImpl implements ExitListener {

        /**
         * Default constructor.
         */
        public ExitListenerImpl() {
        }

        /**
         * Calls safeQuit to check if we can exit.
         *
         * @param arg0
         *            The event generating the quit call.
         * @return True if the application can quit, false otherwise.
         */
        public boolean canExit(final EventObject arg0) {
            return safeQuit();
        }

        /**
         * Cleanup would occur here, but we choose to do nothing for now.
         *
         * @param arg0
         *            The event generating the quit call.
         */
        public void willExit(final EventObject arg0) {
        }
    }
}
