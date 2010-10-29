package org.openshapa.controllers.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import org.openshapa.event.component.TimescaleEvent;
import org.openshapa.event.component.TimescaleListener;

import org.openshapa.models.component.MixerModel;
import org.openshapa.models.component.TimescaleConstants;
import org.openshapa.models.component.TimescaleModel;
import org.openshapa.models.component.Viewport;

import org.openshapa.views.component.TimescalePainter;


/**
 * Timescale controller is responsible for managing a TimescalePainter
 */
public final class TimescaleController implements PropertyChangeListener {

    /** View */
    private final TimescalePainter view;

    /** Models */
    private final TimescaleModel timescaleModel;
    private final MixerModel mixer;

    /** Listeners interested in needle painter events */
    private final EventListenerList listenerList;

    public TimescaleController(final MixerModel mixer) {
        view = new TimescalePainter();

        timescaleModel = new TimescaleModel();
        timescaleModel.setZoomWindowIndicatorHeight(8);
        timescaleModel.setZoomWindowToTrackTransitionHeight(20);
        timescaleModel.setHeight(50
            + timescaleModel.getZoomWindowIndicatorHeight()
            + timescaleModel.getZoomWindowToTrackTransitionHeight());
        timescaleModel.setZoomWindowIndicatorColor(new Color(192, 192, 192));
        timescaleModel.setTimescaleBackgroundColor(new Color(237, 237, 237));

        timescaleModel.setHoursMarkerColor(TimescaleConstants.HOURS_COLOR);
        timescaleModel.setMinutesMarkerColor(TimescaleConstants.MINUTES_COLOR);
        timescaleModel.setSecondsMarkerColor(TimescaleConstants.SECONDS_COLOR);
        timescaleModel.setMillisecondsMarkerColor(
            TimescaleConstants.MILLISECONDS_COLOR);

        final TimescaleEventListener listener = new TimescaleEventListener();
        view.addMouseListener(listener);
        view.addMouseMotionListener(listener);

        this.mixer = mixer;

        view.setMixerView(mixer);
        view.setTimescaleModel(timescaleModel);

        mixer.addPropertyChangeListener(this);

        listenerList = new EventListenerList();
    }

    public TimescaleModel getTimescaleModel() {
        return timescaleModel;
    }

    /**
     * @return View used by the controller
     */
    public JComponent getView() {
        return view;
    }

    @Override public void propertyChange(final PropertyChangeEvent evt) {

        if (Viewport.NAME.equals(evt.getPropertyName())) {
            view.repaint();
        }
    }

    public void addTimescaleEventListener(final TimescaleListener listener) {

        synchronized (this) {
            listenerList.add(TimescaleListener.class, listener);
        }
    }

    public void removeTimescaleEventListener(final TimescaleListener listener) {

        synchronized (this) {
            listenerList.remove(TimescaleListener.class, listener);
        }
    }

    /**
     * Used to fire a new event informing listeners about the new needle time.
     *
     * @param newTime
     */
    private void fireJumpEvent(final long jumpTime,
        final boolean togglePlaybackMode) {

        synchronized (this) {
            TimescaleEvent e = new TimescaleEvent(this, jumpTime,
                    togglePlaybackMode);
            Object[] listeners = listenerList.getListenerList();

            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {

                if (listeners[i] == TimescaleListener.class) {
                    ((TimescaleListener) listeners[i + 1]).jumpToTime(e);
                }
            }
        }
    }

    /**
     * Inner class used to handle intercepted events.
     */
    private class TimescaleEventListener extends MouseInputAdapter {
        private Viewport viewport;

        private final Cursor crosshairCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        private final Cursor defaultCursor = Cursor.getDefaultCursor();
        private boolean isDraggingOnTimescale = false;
        private boolean isDraggingOnZoomWindowIndicator = false;
        
        @Override public void mouseEntered(final MouseEvent e) {
            JComponent source = (JComponent) e.getSource();
            source.setCursor(crosshairCursor);
        }

        @Override public void mouseExited(final MouseEvent e) {
            JComponent source = (JComponent) e.getSource();
            source.setCursor(defaultCursor);
        }

        @Override public void mouseMoved(final MouseEvent e) {
       		mouseEntered(e);
        }
        
        @Override public void mousePressed(final MouseEvent e) {
            viewport = mixer.getViewport();

            if (view.isPointInTimescale(e.getX(), e.getY())) {
	            if (e.getButton() == MouseEvent.BUTTON1) {
                    fireJumpEvent(calculateNewNeedlePositionOnTimescale(e), false);
                    isDraggingOnTimescale = true;
                }
            } else if (view.isPointInZoomWindowIndicator(e.getX(), e.getY())) {
	            if (e.getButton() == MouseEvent.BUTTON1) {
                    fireJumpEvent(calculateNewNeedlePositionOnZoomWindow(e), false);
                    isDraggingOnZoomWindowIndicator = true;
                }
            }
        }

        @Override public void mouseReleased(final MouseEvent e) {
        	isDraggingOnTimescale = false;
        	isDraggingOnZoomWindowIndicator = false;
        }

        @Override public void mouseClicked(final MouseEvent e) {
            if ((e.getButton() == MouseEvent.BUTTON1) && ((e.getClickCount() % 2) == 0)) {
            	if (view.isPointInTimescale(e.getX(), e.getY())) {
	                fireJumpEvent(calculateNewNeedlePositionOnTimescale(e), true);
	        	} else if (view.isPointInZoomWindowIndicator(e.getX(), e.getY())) {
	                fireJumpEvent(calculateNewNeedlePositionOnZoomWindow(e), true);
	        	}
        	}
        }

        @Override public void mouseDragged(final MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
            	if (isDraggingOnTimescale) {
            		fireJumpEvent(calculateNewNeedlePositionOnTimescale(e), false);
	        	} else if (isDraggingOnZoomWindowIndicator) {
            		fireJumpEvent(calculateNewNeedlePositionOnZoomWindow(e), false);
	        	}
        	}
        }

        private long calculateNewNeedlePositionOnTimescale(final MouseEvent e) {
            final int dx = Math.min(Math.max(e.getX(), 0), view.getSize().width);
            final long newTime = viewport.computeTimeFromXOffset(dx) + viewport.getViewStart();
            return Math.min(Math.max(newTime, viewport.getViewStart()), viewport.getViewEnd());
        }
        
        private long calculateNewNeedlePositionOnZoomWindow(final MouseEvent e) {
            final int dx = Math.min(Math.max(e.getX(), 0), view.getSize().width);
            final long newTime = Math.round((double) dx * viewport.getMaxEnd() / view.getSize().width);
            return Math.min(Math.max(newTime, 0), viewport.getMaxEnd());
        }
    }

}
