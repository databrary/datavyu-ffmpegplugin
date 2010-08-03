package org.openshapa.models.component;

/**
 * This model provides information used to render a timing needle on the tracks
 * interface.
 */
public final class NeedleModel {

    /** Width of the needle line in pixels */
    private double needleWidth;

    /** Width of the needle head triangle in pixels */
    private double needleHeadWidth;

    /** Height of the needle head triangle in pixels */
    private double needleHeadHeight;

    /** Current time represented by the needle */
    private long currentTime;

    public NeedleModel() {
    }

    protected NeedleModel(final NeedleModel other) {
        needleWidth = other.needleWidth;
        needleHeadWidth = other.needleHeadWidth;
        needleHeadHeight = other.needleHeadHeight;
        currentTime = other.currentTime;
    }

    /**
     * @return Current time represented by the needle
     */
    public long getCurrentTime() {
        return currentTime;
    }

    /**
     * Set the current time represented by the needle
     *
     * @param currentTime
     */
    public void setCurrentTime(final long currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * @return Width of the needle line
     */
    public double getNeedleWidth() {
        return needleWidth;
    }

    /**
     * Sets the width of the needle line.
     */
    public void setNeedleWidth(final double needleWidth) {
        this.needleWidth = needleWidth;
    }

    /**
     * @return Width of the needle head triangle in pixels
     */
    public double getNeedleHeadWidth() {
        return needleHeadWidth;
    }

    /**
     * Sets the width of the needle head triangle in pixels.
     */
    public void setNeedleHeadWidth(final double needleHeadWidth) {
        this.needleHeadWidth = needleHeadWidth;
    }

    /**
     * @return Height of the needle head triangle in pixels.
     */
    public double getNeedleHeadHeight() {
        return needleHeadHeight;
    }

    /**
     * Sets the height of the needle head triangle in pixels.
     */
    public void setNeedleHeadHeight(final double needleHeadHeight) {
        this.needleHeadHeight = needleHeadHeight;
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

        NeedleModel other = (NeedleModel) obj;

        if (currentTime != other.currentTime)
            return false;

        if (Double.doubleToLongBits(needleHeadHeight)
                != Double.doubleToLongBits(other.needleHeadHeight))
            return false;

        if (Double.doubleToLongBits(needleHeadWidth)
                != Double.doubleToLongBits(other.needleHeadWidth))
            return false;

        if (Double.doubleToLongBits(needleWidth)
                != Double.doubleToLongBits(other.needleWidth))
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
        result = (prime * result) + (int) (currentTime ^ (currentTime >>> 32));

        long temp;
        temp = Double.doubleToLongBits(needleHeadHeight);
        result = (prime * result) + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(needleHeadWidth);
        result = (prime * result) + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(needleWidth);
        result = (prime * result) + (int) (temp ^ (temp >>> 32));

        return result;
    }

    public NeedleModel copy() {
        return new NeedleModel(this);
    }

}
