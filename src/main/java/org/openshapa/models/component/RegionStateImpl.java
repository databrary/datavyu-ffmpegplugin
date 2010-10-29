package org.openshapa.models.component;

/**
 * This model provides information used to render a visualisation of the
 * viewable playback window on the tracks interface.
 */
public final class RegionStateImpl implements RegionState {
    public RegionStateImpl(final long regionStart, final long regionEnd) {
    	assert regionStart >= 0 && regionStart <= regionEnd;
    	
    	if (regionStart < 0) {
    		throw new IllegalArgumentException("regionStart must be >= 0");
    	} else if (regionStart > regionEnd) {
    		throw new IllegalArgumentException("regionStart must be <= regionEnd");
    	}
    	
    	this.regionStart = regionStart;
    	this.regionEnd = regionEnd;
    }
    
    /** Start of the custom playback region (milliseconds) */
    private final long regionStart;

    /** End of the custom playback region (milliseconds) */
    private final long regionEnd;

    /**
     * {@inheritDoc}
     */
    public long getRegionStart() {
        return regionStart;
    }
    
    /**
     * {@inheritDoc}
     */
    public long getRegionEnd() {
        return regionEnd;
    }
    
    /**
     * {@inheritDoc}
     */
    public long getRegionDuration() {
    	return regionEnd - regionStart + 1;
    }

    /*
     * {@inheritDoc}
     */
    @Override public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final RegionStateImpl other = (RegionStateImpl) obj;

        if (regionStart != other.regionStart)
            return false;

        if (regionEnd != other.regionEnd)
            return false;

        return true;
    }

    /*
     * {@inheritDoc}
     */
    @Override public String toString() {
    	return String.format("[regionStart=%d, regionEnd=%d]", regionStart, regionEnd);
    }

    /**
     * {@inheritDoc}
     */
    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (int) (regionEnd ^ (regionEnd >>> 32));
        result = (prime * result) + (int) (regionStart ^ (regionStart >>> 32));

        return result;
    }
}
