package org.openshapa.component.model;

/**
 * This model provides information used to render the time scale on the tracks
 * interface.
 */
public class TimescaleModel {

    /** The number of major intervals to paint on the scale */
    private int majorIntervals;
    /** Pad the scale from the left by this many pixels */
    private int paddingLeft;
    /** Pad the scale from the right by this many pixels */
    private int paddingRight;
    /** This is the width in pixels between major interval markings */
    private float majorWidth;
    /** This is the effective width of the scale, i.e. after padding applied */
    private int effectiveWidth;

    public TimescaleModel() {
    }

    protected TimescaleModel(TimescaleModel other) {
        majorIntervals  = other.majorIntervals;
        paddingLeft     = other.paddingLeft;
        paddingRight    = other.paddingRight;
        majorWidth      = other.majorWidth;
        effectiveWidth  = other.effectiveWidth;
    }

    /**
     * @return The width in pixels between major interval markings
     */
    public float getMajorWidth() {
        return majorWidth;
    }

    /**
     * Sets the width in pixels between major interval markings
     * @param majorWidth
     */
    public void setMajorWidth(float majorWidth) {
        this.majorWidth = majorWidth;
    }

    /**
     * @return The effective width of the scale, i.e. after padding applied 
     */
    public int getEffectiveWidth() {
        return effectiveWidth;
    }

    /**
     * Sets the effective width of the scale, i.e. after padding applied
     * @param effectiveWidth
     */
    public void setEffectiveWidth(int effectiveWidth) {
        this.effectiveWidth = effectiveWidth;
    }

    /**
     * @return The number of major intervals to paint on the scale
     */
    public int getMajorIntervals() {
        return majorIntervals;
    }

    /**
     * Sets the number of major intervals to paint on the scale
     * @param majorIntervals
     */
    public void setMajorIntervals(int majorIntervals) {
        this.majorIntervals = majorIntervals;
    }

    /**
     * @return The scale is padded from the left by this many pixels
     */
    public int getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * Set the scale padding from the left by this many pixels
     * @param paddingLeft
     */
    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    /**
     * @return The scale is padded from the right by this many pixels
     */
    public int getPaddingRight() {
        return paddingRight;
    }

    /**
     * Set the scale padding from the right by this many pixels
     * @param paddingRight
     */
    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimescaleModel other = (TimescaleModel) obj;
        if (this.majorIntervals != other.majorIntervals) {
            return false;
        }
        if (this.paddingLeft != other.paddingLeft) {
            return false;
        }
        if (this.paddingRight != other.paddingRight) {
            return false;
        }
        if (this.majorWidth != other.majorWidth) {
            return false;
        }
        if (this.effectiveWidth != other.effectiveWidth) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.majorIntervals;
        hash = 71 * hash + this.paddingLeft;
        hash = 71 * hash + this.paddingRight;
        hash = 71 * hash + Float.floatToIntBits(this.majorWidth);
        hash = 71 * hash + this.effectiveWidth;
        return hash;
    }

    @Override
    public Object clone() {
        return new TimescaleModel(this);
    }
    
}
