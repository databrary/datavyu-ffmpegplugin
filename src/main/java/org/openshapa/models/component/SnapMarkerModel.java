package org.openshapa.models.component;

/**
 * This model provides information used to render a snap marker on the tracks
 * interface.
 */
public class SnapMarkerModel {

    /** Amount of padding for this component from the top */
    private int paddingTop;
    /** Amount of padding for this component from the left */
    private int paddingLeft;
    /** Current time represented by the marker */
    private long markerTime;

    public SnapMarkerModel() {
    }

    protected SnapMarkerModel(SnapMarkerModel other) {
        paddingTop = other.paddingTop;
        paddingLeft = other.paddingLeft;
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

    /**
     * @return Amount of padding for this component from the left
     */
    public int getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * Set the amount of padding for this component from the left
     * 
     * @param paddingLeft
     */
    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    /**
     * @return Amount of padding for this component from the top
     */
    public int getPaddingTop() {
        return paddingTop;
    }

    /**
     * Set the amount of padding for this component from the top
     * 
     * @param paddingTop
     */
    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SnapMarkerModel other = (SnapMarkerModel) obj;
        if (paddingTop != other.paddingTop) {
            return false;
        }
        if (paddingLeft != other.paddingLeft) {
            return false;
        }
        if (markerTime != other.markerTime) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + paddingTop;
        hash = 83 * hash + paddingLeft;
        hash = 83 * hash + (int) (markerTime ^ (markerTime >>> 32));
        return hash;
    }

    @Override
    public Object clone() {
        return new SnapMarkerModel(this);
    }

}
