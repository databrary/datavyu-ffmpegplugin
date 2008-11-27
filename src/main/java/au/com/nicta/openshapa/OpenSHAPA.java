package au.com.nicta.openshapa;

import au.com.nicta.openshapa.views.OpenSHAPAView;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.application.Application;
import org.jdesktop.application.SessionStorage;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class OpenSHAPA extends SingleFrameApplication {

    /** The logger for OpenSHAPA. */
    private static Logger logger = Logger.getLogger(OpenSHAPA.class);

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
}
