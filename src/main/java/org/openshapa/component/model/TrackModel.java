package org.openshapa.component.model;

/**
 * This model provides data feed information used to render a carriage on the
 * tracks interface.
 */
public class TrackModel {
    /** The duration of the track in milliseconds */
    private long duration;
    /** The offset of the track in milliseconds */
    private long offset;
    /** Is there an error with track information */
    private boolean erroneous;
    /** Track identifier, this is currently just the track's absolute file path */
    private String trackId;

    public TrackModel() {
    }

    protected TrackModel(TrackModel other) {
        duration    = other.duration;
        offset      = other.offset;
        erroneous  = other.erroneous;
        trackId     = other.trackId;
    }

    /**
     * @return The duration of the track in milliseconds
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Set the duration of the track in milliseconds
     * @param duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * @return The offset of the track in milliseconds
     */
    public long getOffset() {
        return offset;
    }

    /**
     * Set the offset of the track in milliseconds
     * @param offset
     */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    /**
     * @return Track identifier, this is currently just the track's absolute file path
     */
    public String getTrackId() {
        return trackId;
    }

    /**
     * Sets the track identifier, this is currently just the track's absolute file path
     * @param trackId
     */
    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    /**
     * @return Is there an error with track information
     */
    public boolean isErroneous() {
        return erroneous;
    }

    /**
     * Set track information error state.
     * @param erroneous
     */
    public void setErroneous(boolean erroneous) {
        this.erroneous = erroneous;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TrackModel other = (TrackModel) obj;
        if (this.duration != other.duration) {
            return false;
        }
        if (this.offset != other.offset) {
            return false;
        }
        if (this.erroneous != other.erroneous) {
            return false;
        }
        if ((this.trackId == null) ? (other.trackId != null) : !this.trackId.equals(other.trackId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (this.duration ^ (this.duration >>> 32));
        hash = 79 * hash + (int) (this.offset ^ (this.offset >>> 32));
        hash = 79 * hash + (this.erroneous ? 1 : 0);
        hash = 79 * hash + (this.trackId != null ? this.trackId.hashCode() : 0);
        return hash;
    }

    @Override
    public Object clone() {
        return new TrackModel(this);
    }
}
