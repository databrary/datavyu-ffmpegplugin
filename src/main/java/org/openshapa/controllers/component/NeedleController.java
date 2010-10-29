package org.openshapa.controllers.component;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.SimpleTimeZone;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

import org.openshapa.models.component.MixerModel;
import org.openshapa.models.component.NeedleConstants;
import org.openshapa.models.component.NeedleModel;
import org.openshapa.models.component.NeedleModelImpl;
import org.openshapa.models.component.RegionState;
import org.openshapa.models.component.ViewportState;

import org.openshapa.views.component.NeedlePainter;


/**
 * NeedleController is responsible for managing a NeedlePainter
 */
public final class NeedleController implements PropertyChangeListener {

    /** View */
    private final NeedlePainter view;

    /** Models */
    private final NeedleModelImpl needleModel;
    private final MixerModel mixerModel;

    public NeedleController(final MixerModel mixer) {
    	assert mixer.getNeedleModel() instanceof NeedleModelImpl; // UGLY HACK until this is fixed properly
        needleModel = (NeedleModelImpl) mixer.getNeedleModel();

        this.mixerModel = mixer;

        view = new NeedlePainter(needleModel);

        mixer.getViewportModel().addPropertyChangeListener(this);

        final NeedleListener needleListener = new NeedleListener();
        view.addMouseListener(needleListener);
        view.addMouseMotionListener(needleListener);
    }

    /**
     * Set the current time to be represented by the needle.
     *
     * @param currentTime
     */
    public void setCurrentTime(final long currentTime) {
        /** Format for representing time. */
        DateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");
        df.setTimeZone(new SimpleTimeZone(0, "NO_ZONE"));
        needleModel.setCurrentTime(currentTime);
        view.setToolTipText(df.format(new Date(currentTime)));
    }

    /**
     * @see NeedleModelImpl#setTimescaleTransitionHeight(int)
     */
    public void setTimescaleTransitionHeight(int newHeight) {
    	needleModel.setTimescaleTransitionHeight(newHeight);
    	view.repaint();
    }
    
    /**
     * @see NeedleModelImpl#setZoomIndicatorHeight(int)
     */
    public void setZoomIndicatorHeight(int newHeight) {
    	needleModel.setZoomIndicatorHeight(newHeight);
    	view.repaint();
    }
    
    public void resetNeedlePosition() {
    	final RegionState region = mixerModel.getRegionModel().getRegion();
    	setCurrentTime(region.getRegionStart());
    }
    
    public NeedleModel getNeedleModel() {
        return needleModel;
    }

    @Override public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getSource() == mixerModel.getViewportModel()) {
            view.repaint();
        }
    }

    /**
     * @return View used by the controller
     */
    public JComponent getView() {
        return view;
    }
    
    /**
     * Inner class used to handle intercepted events.
     */
    private final class NeedleListener extends MouseInputAdapter {
        private final Cursor eastResizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
        private final Cursor defaultCursor = Cursor.getDefaultCursor();
        private ViewportState viewport = null;
        /** offset in pixels from the needle position to where the needle head was "picked up" for dragging */
        private double needlePositionOffsetX = 0.0;
        
        @Override public void mouseEntered(final MouseEvent e) {
            JComponent source = (JComponent) e.getSource();
            source.setCursor(eastResizeCursor);
        }

        @Override public void mouseExited(final MouseEvent e) {
            JComponent source = (JComponent) e.getSource();
            source.setCursor(defaultCursor);
        }

        @Override public void mouseMoved(final MouseEvent e) {
       		mouseEntered(e);
        }

        @Override public void mouseDragged(final MouseEvent e) {
        	if (viewport != null) {
	            final double dx = Math.min(Math.max(e.getX() - NeedleConstants.NEEDLE_HEAD_WIDTH - needlePositionOffsetX, 0), view.getWidth());
	            long newTime = viewport.computeTimeFromXOffset(dx) + viewport.getViewStart();
	            newTime = Math.min(Math.max(newTime, viewport.getViewStart()), viewport.getViewEnd());
	            needleModel.setCurrentTime(newTime);
        	}
        }
        
        @Override public void mousePressed(MouseEvent e) {
            viewport = mixerModel.getViewportModel().getViewport();
            final double currentNeedleX = viewport.computePixelXOffset(needleModel.getCurrentTime()) + NeedleConstants.NEEDLE_HEAD_WIDTH;
            final int mousePressedX = e.getX();
            needlePositionOffsetX = mousePressedX - currentNeedleX;
        }

        @Override public void mouseReleased(MouseEvent e) {
        	viewport = null;
        	needlePositionOffsetX = 0.0;
        }
    }
}
