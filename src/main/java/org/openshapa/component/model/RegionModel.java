package org.openshapa.component.model;

/**
 * This model provides information used to render a visualisation of the
 * viewable playback window on the tracks interface.
 */
public class RegionModel {

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

    protected RegionModel(RegionModel other) {
        paddingTop  = other.paddingTop;
        paddingLeft = other.paddingLeft;
        regionStart = other.regionStart;
        regionEnd   = other.regionEnd;
    }

    /**s
     * @return Amount of padding for this component from the left
     */
    public int getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * Set the amount of padding for this component from the left
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
     * @param paddingTop
     */
    public void setPaddingTop(int paddingTop) {
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
     * @param regionEnd
     */
    public void setRegionEnd(long regionEnd) {
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
     * @param regionStart
     */
    public void setRegionStart(long regionStart) {
        this.regionStart = regionStart;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RegionModel other = (RegionModel) obj;
        if (this.paddingTop != other.paddingTop) {
            return false;
        }
        if (this.paddingLeft != other.paddingLeft) {
            return false;
        }
        if (this.regionStart != other.regionStart) {
            return false;
        }
        if (this.regionEnd != other.regionEnd) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.paddingTop;
        hash = 79 * hash + this.paddingLeft;
        hash = 79 * hash + (int) (this.regionStart ^ (this.regionStart >>> 32));
        hash = 79 * hash + (int) (this.regionEnd ^ (this.regionEnd >>> 32));
        return hash;
    }

    @Override
    public Object clone() {
        return new RegionModel(this);
    }

}
