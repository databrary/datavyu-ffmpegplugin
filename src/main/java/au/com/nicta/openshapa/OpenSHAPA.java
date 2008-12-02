package au.com.nicta.openshapa;

import au.com.nicta.openshapa.views.OpenSHAPAView;
import au.com.nicta.openshapa.views.QTVideoController;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
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
    @Override
    public boolean dispatchKeyEvent(KeyEvent evt) {
        boolean result = true;

        if (evt.getID() != KeyEvent.KEY_PRESSED) {
            return result;
        }

        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ASTERISK:
                qtVideoController.setCellOffsetAction(evt);
                break;
            case KeyEvent.VK_NUMPAD7:
                qtVideoController.rewindAction(evt);
                break;
            case KeyEvent.VK_NUMPAD8:
                qtVideoController.playAction(evt);
                break;
            case KeyEvent.VK_NUMPAD9:
                qtVideoController.forwardAction(evt);
                break;
            case KeyEvent.VK_MINUS:
                qtVideoController.goBackAction(evt);
                break;
            case KeyEvent.VK_NUMPAD4:
                qtVideoController.shuttleBackAction(evt);
                break;
            case KeyEvent.VK_NUMPAD5:
                qtVideoController.pauseAction(evt);
                break;
            case KeyEvent.VK_NUMPAD6:
                qtVideoController.shuttleForwardAction(evt);
                break;
            case KeyEvent.VK_PLUS:
                qtVideoController.findAction(evt);
                break;
            case KeyEvent.VK_NUMPAD1:
                qtVideoController.jogBackAction(evt);
                break;
            case KeyEvent.VK_NUMPAD2:
                qtVideoController.stopAction(evt);
                break;
            case KeyEvent.VK_NUMPAD3:
                qtVideoController.jogForwardAction(evt);
                break;
            case KeyEvent.VK_NUMPAD0:
                qtVideoController.createNewCellAction(evt);
                break;
            default:
                // Do nothing with the key.
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
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new OpenSHAPAView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
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
     * Main method launching the application.
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");

        logger.info("Starting OpenSHAPA.");
        launch(OpenSHAPA.class, args);
    }

    /*
     * Johns regression tests.
    public static void main(String[] args)
    throws au.com.nicta.openshapa.db.SystemErrorException {
        au.com.nicta.openshapa.db.Database.TestDatabase(System.out);
    }
     */

    /** The logger for OpenSHAPA. */
    private static Logger logger = Logger.getLogger(OpenSHAPA.class);

    /** The view to use for the quick time video controller. */
    private QTVideoController qtVideoController;
}
