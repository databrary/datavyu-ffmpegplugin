package org.openshapa.controllers.component;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

import org.openshapa.models.component.MixerModel;
import org.openshapa.models.component.RegionState;
import org.openshapa.models.component.RegionConstants;
import org.openshapa.models.component.RegionModel;
import org.openshapa.models.component.ViewportState;

import org.openshapa.views.component.RegionView;

/**
 * RegionController is responsible for managing a RegionPainter.
 */
public final class RegionController implements PropertyChangeListener {
    private final RegionView view;
    private final MixerModel mixerModel;

    public RegionController(final MixerModel mixer) {
        this.mixerModel = mixer;
        mixer.getViewportModel().addPropertyChangeListener(this);

        view = new RegionView(mixer);

        final RegionMarkerMouseListener markerListener = new RegionMarkerMouseListener();
        view.addMouseListener(markerListener);
        view.addMouseMotionListener(markerListener);
    }
    
    public RegionModel getModel() {
    	return mixerModel.getRegionModel();
    }
    
    /**
     * @return View used by the controller
     */
    public JComponent getView() {
        return view;
    }

    @Override public void propertyChange(final PropertyChangeEvent evt) {
        if (ViewportState.NAME.equals(evt.getPropertyName())) {
            view.repaint();
        }
    }

    /**
     * Inner class used to handle intercepted events.
     */
    private final class RegionMarkerMouseListener extends MouseInputAdapter {
        private boolean onStartMarker;
        private boolean onEndMarker;
        /** offset in pixels from the region marker position to where the marker was "picked up" for dragging */
        private double offset;

        private ViewportState viewport;

        private final Cursor eastResizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
        private final Cursor defaultCursor = Cursor.getDefaultCursor();

        public RegionMarkerMouseListener() {
            super();
            onStartMarker = false;
            onEndMarker = false;
        }

        @Override public void mouseEntered(final MouseEvent e) {
            final JComponent source = (JComponent) e.getSource();
            if (view.contains(e.getPoint())) {
                source.setCursor(eastResizeCursor);
            }
        }

        @Override public void mousePressed(final MouseEvent e) {
            final Component source = (Component) e.getSource();
            final GeneralPath startMarker = view.getStartMarkerPolygon();
            final GeneralPath endMarker = view.getEndMarkerPolygon();
            final RegionState regionState = mixerModel.getRegionModel().getRegion();
            viewport = mixerModel.getViewportModel().getViewport();
            onStartMarker = startMarker.contains(e.getPoint());
            onEndMarker = endMarker.contains(e.getPoint());
            assert !(onStartMarker && onEndMarker); // can't be on both markers at the same time
            source.setCursor((onStartMarker || onEndMarker) ? eastResizeCursor : defaultCursor);
          	offset = e.getX() - viewport.computePixelXOffset(onStartMarker ? regionState.getRegionStart() : regionState.getRegionEnd()) - RegionConstants.RMARKER_WIDTH;
        }

        @Override public void mouseDragged(final MouseEvent e) {
            if (onStartMarker || onEndMarker) {
            	assert viewport != null;
                assert !(onStartMarker && onEndMarker);
            	
                double x = Math.min(Math.max(e.getX() - offset, 0), view.getSize().width) - RegionConstants.RMARKER_WIDTH;
                double newTime = viewport.computeTimeFromXOffset(x) + viewport.getViewStart();
                newTime = Math.round(Math.min(Math.max(newTime, viewport.getViewStart()), viewport.getViewEnd()));
                
                final RegionModel regionModel = mixerModel.getRegionModel();
                final RegionState region = regionModel.getRegion();
                
                if (onStartMarker) {
                	newTime = Math.min(newTime, region.getRegionEnd());
                	regionModel.setPlaybackRegionStart((long) newTime);
                } else {
                	newTime = Math.max(newTime, region.getRegionStart());
                	regionModel.setPlaybackRegionEnd((long) newTime);
                }
            }
        }

        @Override public void mouseReleased(final MouseEvent e) {
            onStartMarker = false;
            onEndMarker = false;
            viewport = null;
            offset = 0;

            final JComponent source = (JComponent) e.getSource();
            source.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
