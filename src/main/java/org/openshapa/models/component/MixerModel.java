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

    @Override public void resizeViewport(final long newStart, final double newWidth) {

        synchronized (this) {

            long newEnd = (long) (Math.ceil(viewport.getResolution() * newWidth)
                    + newStart);
            ViewableModel newView = new ViewableModel(viewport.getMaxEnd(),
                    newWidth, newStart, newEnd);

            validateConstraints(newView);

            viewport = newView;
        }

        change.firePropertyChange(Viewport.NAME, null, null);
    }

    @Override public void setViewport(final long newStart, final long newEnd,
        final long newMaxEnd, final double newWidth) {

        synchronized (this) {
            ViewableModel newView = new ViewableModel(newMaxEnd, newWidth, newStart,
                    newEnd);
            validateConstraints(newView);
            viewport = newView;
        }

        change.firePropertyChange(Viewport.NAME, null, null);
    }

    @Override public void setViewportMaxEnd(final long newMaxEnd, final boolean resetViewportWindow) {

        if (newMaxEnd <= 1) {
            resetViewport();

            return;
        }

        synchronized (this) {
            final long viewStart = viewport.getViewStart() <= newMaxEnd ? viewport.getViewStart() : newMaxEnd - 1;
            final long viewEnd = viewport.getViewEnd() <= newMaxEnd ? viewport.getViewEnd() : newMaxEnd;
            ViewableModel newView = new ViewableModel(newMaxEnd,
                    viewport.getViewWidth(), resetViewportWindow ? 0 : viewStart, resetViewportWindow ? newMaxEnd : viewEnd);
            validateConstraints(newView);
            viewport = newView;
        }

        change.firePropertyChange(Viewport.NAME, null, null);
    }

    @Override public void setViewportWindow(final long newStart, final long newEnd) {

        synchronized (this) {
            ViewableModel newView = new ViewableModel(viewport.getMaxEnd(),
                    viewport.getViewWidth(), newStart, newEnd);
            validateConstraints(newView);
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

    private void validateConstraints(final Viewport viewport) {
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
