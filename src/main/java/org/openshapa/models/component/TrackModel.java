package org.openshapa.models.component;

/**
 * This model provides data feed information used to render a carriage on the
 * tracks interface.
 */
public class TrackModel {
    /** The duration of the track in milliseconds */
    private long duration;
    /** The offset of the track in milliseconds */
    private long offset;
    /** Track bookmark location in milliseconds */
    private long bookmark;
    /** Is there an error with track information */
    private boolean erroneous;
    /** Track identifier, this is currently just the track's absolute file path */
    private String trackId;
    /** Name of this track */
    private String trackName;
    /** Is this track selected */
    private boolean selected;

    public TrackModel() {
    }

    protected TrackModel(TrackModel other) {
        duration = other.duration;
        offset = other.offset;
        bookmark = other.bookmark;
        erroneous = other.erroneous;
        trackId = other.trackId;
        trackName = other.trackName;
    }

    /**
     * @return The duration of the track in milliseconds
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Set the duration of the track in milliseconds
     * 
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
     * 
     * @param offset
     */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    /**
     * @return Track identifier, this is currently just the track's absolute
     *         file path
     */
    public String getTrackId() {
        return trackId;
    }

    /**
     * Sets the track identifier, this is currently just the track's absolute
     * file path
     * 
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
     * 
     * @param erroneous
     */
    public void setErroneous(boolean erroneous) {
        this.erroneous = erroneous;
    }

    /**
     * @return the bookmark
     */
    public long getBookmark() {
        return bookmark;
    }

    /**
     * @param bookmark
     *            the bookmark to set
     */
    public void setBookmark(long bookmark) {
        this.bookmark = bookmark;
    }

    /**
     * @return the trackName
     */
    public String getTrackName() {
        return trackName;
    }

    /**
     * @param trackName
     *            the trackName to set
     */
    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected
     *            the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (bookmark ^ (bookmark >>> 32));
        result = prime * result + (int) (duration ^ (duration >>> 32));
        result = prime * result + (erroneous ? 1231 : 1237);
        result = prime * result + (int) (offset ^ (offset >>> 32));
        result = prime * result + (selected ? 1231 : 1237);
        result = prime * result + ((trackId == null) ? 0 : trackId.hashCode());
        result =
                prime * result
                        + ((trackName == null) ? 0 : trackName.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TrackModel other = (TrackModel) obj;
        if (bookmark != other.bookmark) {
            return false;
        }
        if (duration != other.duration) {
            return false;
        }
        if (erroneous != other.erroneous) {
            return false;
        }
        if (offset != other.offset) {
            return false;
        }
        if (selected != other.selected) {
            return false;
        }
        if (trackId == null) {
            if (other.trackId != null) {
                return false;
            }
        } else if (!trackId.equals(other.trackId)) {
            return false;
        }
        if (trackName == null) {
            if (other.trackName != null) {
                return false;
            }
        } else if (!trackName.equals(other.trackName)) {
            return false;
        }
        return true;
    }

    @Override
    public Object clone() {
        return new TrackModel(this);
    }
}
