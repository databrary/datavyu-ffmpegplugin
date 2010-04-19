package org.openshapa.models.component;

/**
 * This model provides information used to render a visualisation of the
 * viewable playback window on the tracks interface.
 */
public class RegionModel implements Cloneable {

    /** Amount of padding for this component from the top */
    private int paddingTop;
    /** Amount of padding for this component from the left */
    private int paddingLeft;
    /** Start of the custom playback region */
    private long regionStart;
    /** End of the custom playback region */
    private long regionEnd;

    public RegionModel() {
    }

    protected RegionModel(final RegionModel other) {
        paddingTop = other.paddingTop;
        paddingLeft = other.paddingLeft;
        regionStart = other.regionStart;
        regionEnd = other.regionEnd;
    }

    /**
     * s
     * 
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
    public void setPaddingLeft(final int paddingLeft) {
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
    public void setPaddingTop(final int paddingTop) {
        this.paddingTop = paddingTop;
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

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RegionModel other = (RegionModel) obj;
        if (paddingTop != other.paddingTop) {
            return false;
        }
        if (paddingLeft != other.paddingLeft) {
            return false;
        }
        if (regionStart != other.regionStart) {
            return false;
        }
        if (regionEnd != other.regionEnd) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + paddingTop;
        hash = 79 * hash + paddingLeft;
        hash = 79 * hash + (int) (regionStart ^ (regionStart >>> 32));
        hash = 79 * hash + (int) (regionEnd ^ (regionEnd >>> 32));
        return hash;
    }

    @Override
    public RegionModel clone() {
        return new RegionModel(this);
    }

}
