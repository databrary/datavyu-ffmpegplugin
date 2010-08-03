package org.openshapa.models.component;

/**
 * This model provides information used to render a visualisation of the
 * viewable playback window on the tracks interface.
 */
public final class RegionModel {

    /** Start of the custom playback region */
    private long regionStart;

    /** End of the custom playback region */
    private long regionEnd;

    public RegionModel() {
    }

    protected RegionModel(final RegionModel other) {
        regionStart = other.regionStart;
        regionEnd = other.regionEnd;
    }

    /**
     * @return End of the custom playback region
     */
    public long getRegionEnd() {
        return regionEnd;
    }

    /**
     * Set the end of the custom playback region
     *
     * @param regionEnd
     */
    public void setRegionEnd(final long regionEnd) {
        this.regionEnd = regionEnd;
    }

    /**
     * @return Start of the custom playback region
     */
    public long getRegionStart() {
        return regionStart;
    }

    /**
     * Sets the start of the custom playback region
     *
     * @param regionStart
     */
    public void setRegionStart(final long regionStart) {
        this.regionStart = regionStart;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override public boolean equals(final Object obj) {

        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        RegionModel other = (RegionModel) obj;

        if (regionEnd != other.regionEnd)
            return false;

        if (regionStart != other.regionStart)
            return false;

        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (int) (regionEnd ^ (regionEnd >>> 32));
        result = (prime * result) + (int) (regionStart ^ (regionStart >>> 32));

        return result;
    }

    public RegionModel copy() {
        return new RegionModel(this);
    }

}
