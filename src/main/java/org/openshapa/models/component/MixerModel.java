package org.openshapa.models.component;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public final class MixerModel implements MixerView {

    private final PropertyChangeSupport change;

    private volatile ViewableModel viewport;

    public MixerModel() {
        change = new PropertyChangeSupport(this);

        resetViewport();
    }

    @Override public Viewport getViewport() {
        return viewport;
    }

    @Override public void addPropertyChangeListener(
        final PropertyChangeListener listener) {
        change.addPropertyChangeListener(listener);
    }

    @Override public void addPropertyChangeListener(final String property,
        final PropertyChangeListener listener) {
        change.addPropertyChangeListener(property, listener);
    }

    @Override public void removePropertyChangeListener(
        final PropertyChangeListener listener) {
        change.removePropertyChangeListener(listener);

    }

    @Override public void removePropertyChangeListener(final String property,
        final PropertyChangeListener listener) {
        change.removePropertyChangeListener(property, listener);
    }

    @Override public void resizeViewport(final long start, final double width) {

        synchronized (this) {

            long newEnd = (long) (Math.ceil(viewport.getResolution() * width)
                    + start);
            ViewableModel newView = new ViewableModel(viewport.getMaxEnd(),
                    width, start, newEnd);

            repOK(newView);

            viewport = newView;
        }

        change.firePropertyChange(Viewport.NAME, null, null);
    }

    @Override public void setViewport(final long start, final long end,
        final long maxEnd, final double width) {

        synchronized (this) {
            ViewableModel newView = new ViewableModel(maxEnd, width, start,
                    end);
            repOK(newView);
            viewport = newView;
        }

        change.firePropertyChange(Viewport.NAME, null, null);
    }

    @Override public void setViewportMaxEnd(final long maxEnd) {

        if (maxEnd == 0) {
            resetViewport();

            return;
        }

        synchronized (this) {
            ViewableModel newView = new ViewableModel(maxEnd,
                    viewport.getViewWidth(), viewport.getViewStart(), maxEnd);
            repOK(newView);
            viewport = newView;
        }

        change.firePropertyChange(Viewport.NAME, null, null);
    }

    @Override public void setViewportWindow(final long start, final long end) {

        synchronized (this) {
            ViewableModel newView = new ViewableModel(viewport.getMaxEnd(),
                    viewport.getViewWidth(), start, end);
            repOK(newView);
            viewport = newView;
        }

        change.firePropertyChange(Viewport.NAME, null, null);
    }

    @Override public void setViewportZoom(final double zoomLevel,
        final long centerTime) {

        synchronized (this) {
            viewport = viewport.zoomViewport(zoomLevel, centerTime);
        }

        change.firePropertyChange(Viewport.NAME, null, null);
    }

    public void resetViewport() {
        setViewport(0, MixerConstants.DEFAULT_DURATION,
            MixerConstants.DEFAULT_DURATION,
            (viewport != null) ? viewport.getViewWidth() : 0);
    }

    private void repOK(final Viewport viewport) {
        assert viewport.getViewStart() <= viewport.getViewEnd();
        assert viewport.getMaxEnd() >= 0;
        assert viewport.getViewWidth() >= 0;

        if (viewport.getViewStart() > viewport.getViewEnd()) {
            throw new IllegalArgumentException("viewStart must be <= viewEnd");
        }

        if (viewport.getMaxEnd() < 0) {
            throw new IllegalArgumentException("maxEnd must be >= 0");
        }

        if (viewport.getViewWidth() < 0) {
            throw new IllegalArgumentException("viewWidth must be >= 0");
        }
    }


}
