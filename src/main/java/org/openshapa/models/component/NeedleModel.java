package org.openshapa.models.component;

import java.awt.Color;

/**
 * This model provides information used to render a timing needle on the tracks
 * interface.
 */
public final class NeedleModel {

    /** Current time represented by the needle */
    private long currentTime;

    /** Color of the needle line to be drawn on screen */
    private Color needleColor = new Color(250, 0, 0, 100);

    public NeedleModel() {
    }

    protected NeedleModel(final NeedleModel other) {
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
     * @return color of the needle line
     */
    public Color getNeedleColor() {
    	return needleColor;
    }
    
    /**
     * Sets the color of the needle line.
     */
    public void setNeedleColor(final Color needleColor) {
    	this.needleColor = needleColor;
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

        return result;
    }

    public NeedleModel copy() {
        return new NeedleModel(this);
    }

}
