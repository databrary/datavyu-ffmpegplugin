package org.openshapa.models.component;

import java.beans.PropertyChangeEvent;

/**
 * Concrete implementation of the {@link RegionModel} interface.
 */
public class RegionModelImpl extends MixerComponentModelImpl implements RegionModel {
    private volatile RegionStateImpl regionState;

    public RegionModelImpl(final MixerModel mixer) {
    	super(mixer);
        regionState = new RegionStateImpl(0, mixer.getViewportModel().getViewport().getMaxEnd());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override public void wireListeners() {
    	mixerModel.getViewportModel().addPropertyChangeListener(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public synchronized RegionState getRegion() {
        return regionState;
    }
    
    /**
     * {@inheritDoc}
     */
    public void resetPlaybackRegion() {
    	setPlaybackRegion(0, mixerModel.getViewportModel().getViewport().getMaxEnd());
    }
    
    /**
     * {@inheritDoc}
     */
    public void setPlaybackRegion(final long regionStart, final long regionEnd) {
    	final RegionStateImpl oldRegionState;
    	final RegionStateImpl newRegionState;
    	
    	synchronized(this) {
    		oldRegionState = regionState;
    		newRegionState = new RegionStateImpl(regionStart, regionEnd);
    		if (newRegionState.equals(oldRegionState)) {
    			return;
    		}
	        this.regionState = newRegionState;
    	}
    	
        firePropertyChange(RegionState.NAME, oldRegionState, newRegionState);
    }

    /**
     * {@inheritDoc}
     */
    public void setPlaybackRegionStart(final long regionStart) {
    	final long regionEnd;
    	synchronized(this) {
    		regionEnd = regionState.getRegionEnd();
    	}
    	setPlaybackRegion(regionStart, regionEnd);
    }

    /**
     * {@inheritDoc}
     */
    public void setPlaybackRegionEnd(final long regionEnd) {
    	final long regionStart;
    	synchronized(this) {
    		regionStart = regionState.getRegionStart();
    	}
    	setPlaybackRegion(regionStart, regionEnd);
    }
    
    @Override public void propertyChange(PropertyChangeEvent evt) {
    	if (evt.getSource() == mixerModel) {
        	final ViewportState oldViewport = evt.getOldValue() instanceof ViewportState ? (ViewportState) evt.getOldValue() : null;
        	final ViewportState newViewport = evt.getNewValue() instanceof ViewportState ? (ViewportState) evt.getNewValue() : null;
        	final boolean maxEndChanged = oldViewport == null || newViewport == null || oldViewport.getMaxEnd() != newViewport.getMaxEnd();
        	if (maxEndChanged) {
        		final RegionState region = mixerModel.getRegionModel().getRegion();
                final boolean updateEndRegionMarker = oldViewport == null || oldViewport.getMaxEnd() == region.getRegionEnd();
                final long newMaxEnd = newViewport.getMaxEnd();
                final boolean regionStartExceedsMaxEnd = region.getRegionStart() >= newMaxEnd;
                final boolean regionEndExceedsMaxEnd = region.getRegionEnd() > newMaxEnd;
                
                if (regionStartExceedsMaxEnd || regionEndExceedsMaxEnd || updateEndRegionMarker) {
                	final long newRegionStart = regionStartExceedsMaxEnd ? 0 : region.getRegionStart();
                	final long newRegionEnd = newMaxEnd;
                	setPlaybackRegion(newRegionStart, newRegionEnd);
                }
        	}    	
    	}
    }
}
