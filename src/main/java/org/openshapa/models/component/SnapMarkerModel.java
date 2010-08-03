package org.openshapa.models.component;

/**
 * This model provides information used to render a snap marker on the tracks
 * interface.
 */
public final class SnapMarkerModel {

    /** Current time represented by the marker */
    private long markerTime;

    public SnapMarkerModel() {
    }

    protected SnapMarkerModel(final SnapMarkerModel other) {
        markerTime = other.markerTime;
    }

    /**
     * @return time represented by the marker
     */
    public long getMarkerTime() {
        return markerTime;
    }

    /**
     * Set the time represented by the marker
     *
     * @param currentTime
     */
    public void setMarkerTime(final long markerTime) {
        this.markerTime = markerTime;
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

        SnapMarkerModel other = (SnapMarkerModel) obj;

        if (markerTime != other.markerTime)
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
        result = (prime * result) + (int) (markerTime ^ (markerTime >>> 32));

        return result;
    }

    public SnapMarkerModel copy() {
        return new SnapMarkerModel(this);
    }

}
