/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.datavyu.models.component;

import java.beans.PropertyChangeEvent;
import org.datavyu.models.component.MixerModel;
import org.datavyu.models.component.RegionModel;
import org.datavyu.models.component.RegionState;
import org.datavyu.models.component.ViewportState;

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
    	if (evt.getSource() == mixerModel.getViewportModel()) {
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
