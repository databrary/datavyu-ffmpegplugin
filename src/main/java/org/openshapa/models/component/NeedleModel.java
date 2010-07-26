package org.openshapa.models.component;

/**
 * This model provides information used to render a timing needle on the tracks
 * interface.
 */
public final class NeedleModel {

    /** Amount of padding for this component from the top */
    private int paddingTop;

    /** Amount of padding for this component from the left */
    private int paddingLeft;

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
        paddingTop = other.paddingTop;
        paddingLeft = other.paddingLeft;
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

    /**
     * Set the amount of padding for this component from the top
     *
     * @param paddingTop
     */
    public void setPaddingTop(final int paddingTop) {
        this.paddingTop = paddingTop;
    }

    @Override public boolean equals(final Object obj) {

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final NeedleModel other = (NeedleModel) obj;

        if (paddingTop != other.paddingTop) {
            return false;
        }

        if (paddingLeft != other.paddingLeft) {
            return false;
        }

        if (currentTime != other.currentTime) {
            return false;
        }

        if (needleWidth != other.needleWidth) {
            return false;
        }

        if (needleHeadWidth != other.needleHeadWidth) {
            return false;
        }

        if (needleHeadHeight != other.needleHeadHeight) {
            return false;
        }

        return true;
    }

    @Override public int hashCode() {
        int hash = 7;
        hash = (83 * hash) + paddingTop;
        hash = (83 * hash) + paddingLeft;
        hash = (83 * hash) + (int) Double.doubleToLongBits(needleWidth);
        hash = (83 * hash) + (int) Double.doubleToLongBits(needleHeadWidth);
        hash = (83 * hash) + (int) Double.doubleToLongBits(needleHeadHeight);
        hash = (83 * hash) + (int) (currentTime ^ (currentTime >>> 32));

        return hash;
    }

    public NeedleModel copy() {
        return new NeedleModel(this);
    }

}
