package org.openshapa.util;

import java.io.File;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.openshapa.OpenSHAPA;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;


/**
 * Ok this curly piece of work is a bit body of reflection to basically achieve
 * the following snippet of code that will ultimately compile on any platform.
 *
 * public class MacOSAboutHandler extends Application {
 *
 * public MacOSAboutHandler() { addApplicationListener(new AboutBoxHandler()); }
 *
 * class AboutBoxHandler extends ApplicationAdapter { public void
 * handleAbout(ApplicationEvent event) {
 * OpenSHAPA.getApplication().showAboutWindow(); event.setHandled(true); } } }
 */
public class MacHandler {

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(MacHandler.class);

    /**
     * Default constructor.
     */
    public MacHandler() {

        try {
            Class appc = Class.forName("com.apple.eawt.Application");
            Object app = appc.newInstance();

            Class applc = Class.forName("com.apple.eawt.ApplicationListener");
            Object listener = Proxy.newProxyInstance(Thread.currentThread()
                    .getContextClassLoader(), new Class[] { applc },
                    new HandlerForApplicationAdapter());

            // Add the listener to the application.
            Method m = appc.getMethod("addApplicationListener", applc);
            m.invoke(app, listener);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Unable to find apple classes", e);
        } catch (InstantiationException e) {
            LOGGER.error("Unable to instantiate apple application", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Unable to access application excapeion", e);
        } catch (NoSuchMethodException e) {
            LOGGER.error("Unable to access method in application", e);
        } catch (InvocationTargetException e) {
            LOGGER.error("Unable to invocate target", e);
        }
    }

    /**
     * InvocationHandler for the ApplicationAdapter... So we can override some
     * methods.
     */
    class HandlerForApplicationAdapter implements InvocationHandler {

        /**
         * Called when a method in the proxy object is being invoked.
         *
         * @param proxy
         *            The object we are proxying.
         * @param method
         *            The method that is being invoked.
         * @param args
         *            The arguments being supplied to the method.
         *
         *
         * @return Value for the method being invoked.
         */
        public Object invoke(final Object proxy, final Method method,
            final Object[] args) {

            try {
                Class ae = Class.forName("com.apple.eawt.ApplicationEvent");

                if (method.getName().equals("handleAbout")) {
                    OpenSHAPA.getApplication().showAboutWindow();

                    Method setHandled = ae.getMethod("setHandled",
                            boolean.class);
                    setHandled.invoke(args[0], true);
                } else if (method.getName().equals("handleQuit")) {
                    // Accept the quit request.

                    boolean shouldQuit = OpenSHAPA.getApplication().safeQuit();

                    if (shouldQuit) {
                        OpenSHAPA.getApplication().getMainFrame().setVisible(
                            false);
                        NativeLoader.cleanAllTmpFiles();
                        UserMetrix.shutdown();
                    }

                    Method setHandled = ae.getMethod("setHandled",
                            boolean.class);

                    setHandled.invoke(args[0], shouldQuit);
                } else if ("handleOpenFile".equals(method.getName())) {
                    Method getFilename = ae.getMethod("getFilename", null);

                    String fileName = (String) getFilename.invoke(args[0],
                            null);

                    OpenSHAPA.getApplication().getView().open(new File(
                            fileName));

                    Method setHandled = ae.getMethod("setHandled",
                            boolean.class);
                    setHandled.invoke(args[0], true);
                }
            } catch (NoSuchMethodException e) {
                LOGGER.error("Unable to access method in application", e);
            } catch (IllegalAccessException e) {
                LOGGER.error("Unable to access application excapeion", e);
            } catch (InvocationTargetException e) {
                LOGGER.error("Unable to invocate target", e);
            } catch (ClassNotFoundException e) {
                LOGGER.error("Unable to find apple classes", e);
            }

            return null;
        }
    }
}
