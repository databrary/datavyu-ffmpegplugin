package org.openshapa.models.component;

public final class MixerModelImpl extends MixerComponentModelImpl implements MixerModel {
    private final RegionModel regionModel;
    private final NeedleModel needleModel;
    private volatile ViewableModel viewport;

    public MixerModelImpl() {
    	super(null);
        resetViewport();
    	regionModel = new RegionModelImpl(this);
    	needleModel = new NeedleModelImpl(this);
    	
    	wireListeners();
    	((MixerComponentModelImpl) regionModel).wireListeners();
    	((MixerComponentModelImpl) needleModel).wireListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override public RegionModel getRegionModel() {
    	return regionModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override public NeedleModel getNeedleModel() {
    	return needleModel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override public Viewport getViewport() {
        return viewport;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void resizeViewport(final long newStart, final double newWidth) {
        final long newEnd = (long) (Math.ceil(viewport.getResolution() * newWidth) + newStart);
        setViewport(newStart, newEnd, viewport.getMaxEnd(), newWidth);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void setViewport(final long newViewStart, final long newViewEnd, final long newMaxEnd, final double newWidth) {
    	final Viewport oldViewport;
    	final Viewport newViewport;

        synchronized (this) {
        	if (viewport != null && viewport.getMaxEnd() == newMaxEnd && viewport.getViewWidth() == newWidth && viewport.getViewStart() == newViewStart && viewport.getViewEnd() == newViewEnd) {
        		return;
        	}
        	
        	oldViewport = viewport;
        	viewport = new ViewableModel(newMaxEnd, newWidth, newViewStart, newViewEnd);
        	newViewport = viewport;
        }

        firePropertyChange(Viewport.NAME, oldViewport, newViewport);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override public void setViewportMaxEnd(final long newMaxEnd, final boolean resetViewportWindow) {
        if (newMaxEnd <= 1) {
            resetViewport();
            return;
        }

    	final Viewport oldViewport;
    	final Viewport newViewport;
    	
        synchronized (this) {
        	final long viewStart = resetViewportWindow ? 0 : viewport.getViewStart();
        	final long viewEnd = resetViewportWindow ? newMaxEnd : viewport.getViewEnd();
        	
        	if (viewport.getMaxEnd() == newMaxEnd && viewport.getViewStart() == viewStart && viewport.getViewEnd() == viewEnd) {
        		return;
        	}
        
        	oldViewport = viewport;
            viewport = new ViewableModel(newMaxEnd, viewport.getViewWidth(), viewStart, viewEnd);
            newViewport = viewport;
        }

        firePropertyChange(Viewport.NAME, oldViewport, newViewport);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void setViewportWindow(final long newStart, final long newEnd) {
    	final Viewport oldViewport;
    	final Viewport newViewport;
    	
        synchronized (this) {
        	if (viewport.getViewStart() == newStart && viewport.getViewEnd() == newEnd) {
        		return;
        	}

        	oldViewport = viewport;
        	viewport = new ViewableModel(viewport.getMaxEnd(), viewport.getViewWidth(), newStart, newEnd);
        	newViewport = viewport;
        }

        firePropertyChange(Viewport.NAME, oldViewport, newViewport);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void setViewportZoom(final double zoomLevel, final long centerTime) {
    	final Viewport oldViewport;
    	final Viewport newViewport;
    	
        synchronized (this) {
        	if (viewport.getZoomLevel() == zoomLevel) {
        		return;
        	}

        	oldViewport = viewport;
            viewport = viewport.zoomViewport(zoomLevel, centerTime);
            newViewport = viewport;
        }

        firePropertyChange(Viewport.NAME, oldViewport, newViewport);
    }

    /**
     * {@inheritDoc}
     */
    public void resetViewport() {
    	final double newWidth;
    	synchronized(this) {
    		newWidth = (viewport != null) ? viewport.getViewWidth() : 0;
    	}
        setViewport(0, MixerConstants.DEFAULT_DURATION, ViewableModel.MINIMUM_MAX_END, newWidth);
    }
}
