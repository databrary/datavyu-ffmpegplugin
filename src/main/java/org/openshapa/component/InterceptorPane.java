package org.openshapa.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;
import org.openshapa.event.InterceptedEvent;
import org.openshapa.event.InterceptedEvent.EventType;
import org.openshapa.event.InterceptedEventListener;

/**
 * This pane is used to intercept input events at whatever z-layer this
 * component sits at. Other components may register with an instance of this
 * pane to be notified about intercepted events. Registered components will
 * receive a copy of these intercepted events, thus simulating events
 * propagation across components sitting on different z-layers. Events are
 * propagated as-is; listeners are responsible for performing any necessary
 * events re-processing or translation.
 * @seealso InterceptedEvent
 * @seealso InterceptedEventListener
 */
public class InterceptorPane extends Component {

    /** Listeners interested in needle painter events */
    private EventListenerList listenerList;

    public InterceptorPane() {
        super();
        listenerList = new EventListenerList();
        InterceptorMouseListener mouseListener = new InterceptorMouseListener();
        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);
    }

//    @Override
//    public void paint(Graphics g) {
//        Dimension size = getSize();
//
//        g.setColor(Color.gray);
//        g.fillRect(0, 0, size.width, size.height);
//    }

    /**
     * Register the listener to be notified of intercepted events.
     * @param listener
     */
    public synchronized void addInterceptedEventListener(
            InterceptedEventListener listener) {
        listenerList.add(InterceptedEventListener.class, listener);
    }

    /**
     * Unregister the listener of intercepted events.
     * @param listener
     */
    public synchronized void removeInterceptedEventListner(
            InterceptedEventListener listener) {
        listenerList.remove(InterceptedEventListener.class, listener);
    }

    /**
     * Notify listeners about an intercepted event.
     * @param event
     * @param interceptedEvent
     */
    private synchronized void fireInterceptedEvent(EventType event,
            EventObject interceptedEvent) {
        InterceptedEvent e = new InterceptedEvent(this, event, interceptedEvent);
        Object[] listeners = listenerList.getListenerList();
        /* The listener list contains the listening class and then the listener
         * instance.
         */
        for (int i = 0; i < listeners.length; i += 2) {
           if (listeners[i] == InterceptedEventListener.class) {
               ((InterceptedEventListener)listeners[i+1]).eventIntercepted(e);
           }
        }
    }

    /**
     * Inner class used for intercepting mouse input events.
     */
    private class InterceptorMouseListener extends MouseInputAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {
            fireInterceptedEvent(EventType.MOUSE_ENTERED, e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            fireInterceptedEvent(EventType.MOUSE_MOVED, e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            fireInterceptedEvent(EventType.MOUSE_PRESSED, e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            fireInterceptedEvent(EventType.MOUSE_DRAGGED, e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            fireInterceptedEvent(EventType.MOUSE_RELEASED, e);
        }

    }

}
