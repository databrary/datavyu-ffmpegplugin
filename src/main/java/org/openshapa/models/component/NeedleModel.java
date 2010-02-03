package org.openshapa.models.component;

/**
 * This model provides information used to render a timing needle on the tracks
 * interface.
 */
public class NeedleModel {

    /** Amount of padding for this component from the top */
    private int paddingTop;
    /** Amount of padding for this component from the left */
    private int paddingLeft;
    /** Current time represented by the needle */
    private long currentTime;

    public NeedleModel() {
    }

    protected NeedleModel(NeedleModel other) {
        paddingTop  = other.paddingTop;
        paddingLeft = other.paddingLeft;
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
     * @param currentTime
     */
    public void setCurrentTime(long currentTime) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NeedleModel other = (NeedleModel) obj;
        if (this.paddingTop != other.paddingTop) {
            return false;
        }
        if (this.paddingLeft != other.paddingLeft) {
            return false;
        }
        if (this.currentTime != other.currentTime) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.paddingTop;
        hash = 83 * hash + this.paddingLeft;
        hash = 83 * hash + (int) (this.currentTime ^ (this.currentTime >>> 32));
        return hash;
    }

    @Override
    public Object clone() {
        return new NeedleModel(this);
    }
    
}
