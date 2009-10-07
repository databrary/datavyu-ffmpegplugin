package org.openshapa.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.log4j.Logger;
import org.openshapa.OpenSHAPA;

/**
 * Ok this curly piece of work is a bit body of reflection to basically achieve
 * the following snippet of code that will ultimately compile on any platform.
 *
 * public class MacOSAboutHandler extends Application {
 *
 *  public MacOSAboutHandler() {
 *    addApplicationListener(new AboutBoxHandler());
 *  }
 *
 *  class AboutBoxHandler extends ApplicationAdapter {
 *    public void handleAbout(ApplicationEvent event) {
 *      OpenSHAPA.getApplication().showAboutWindow();
 *      event.setHandled(true);
 *    }
 *  }
 * }
 */
public class AboutHandler {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(AboutHandler.class);

    /**
     * Default constructor.
     */
    public AboutHandler() {
        try {
            Class appc = Class.forName("com.apple.eawt.Application");
            Object app = appc.newInstance();

            Class applc = Class.forName("com.apple.eawt.ApplicationListener");
            Object listener = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{applc},
                new HandlerForApplicationAdapter());

            // Add the listener to the application.
            Method m = appc.getMethod("addApplicationListener", applc);
            m.invoke(app, listener);
        } catch (ClassNotFoundException e) {
            logger.error("Unable to find apple classes", e);
        } catch (InstantiationException e) {
            logger.error("Unable to instantiate apple application", e);
        } catch (IllegalAccessException e) {
            logger.error("Unable to access application excapeion", e);
        } catch (NoSuchMethodException e) {
            logger.error("Unable to access method in application", e);
        } catch (InvocationTargetException e) {
            logger.error("Unable to invocate target", e);
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
         * @param proxy The object we are proxying.
         * @param method The method that is being invoked.
         * @param args The arguments being supplied to the method.
         *
         *
         * @return Value for the method being invoked.
         */
        public Object invoke(Object proxy, Method method, Object[] args) {
            try {
                if (method.getName().equals("handleAbout")) {
                    OpenSHAPA.getApplication().showAboutWindow();

                    Class ae = Class.forName("com.apple.eawt.ApplicationEvent");
                    Method setHandled = ae.getMethod("setHandled",
                                                     boolean.class);
                    setHandled.invoke(args[0], true);
                }
            } catch (NoSuchMethodException e) {
                logger.error("Unable to access method in application", e);
            } catch (IllegalAccessException e) {
                logger.error("Unable to access application excapeion", e);
            }  catch (InvocationTargetException e) {
                logger.error("Unable to invocate target", e);
            } catch (ClassNotFoundException e) {
              logger.error("Unable to find apple classes", e);
            }

            return null;
        }
    }
}
