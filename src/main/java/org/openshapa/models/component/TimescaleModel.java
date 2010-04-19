package org.openshapa.models.component;

/**
 * This model provides information used to render the time scale on the tracks
 * interface.
 */
public class TimescaleModel implements Cloneable {

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

    protected TimescaleModel(final TimescaleModel other) {
        majorIntervals = other.majorIntervals;
        paddingLeft = other.paddingLeft;
        paddingRight = other.paddingRight;
        majorWidth = other.majorWidth;
        effectiveWidth = other.effectiveWidth;
    }

    /**
     * @return The width in pixels between major interval markings
     */
    public float getMajorWidth() {
        return majorWidth;
    }

    /**
     * Sets the width in pixels between major interval markings
     * 
     * @param majorWidth
     */
    public void setMajorWidth(final float majorWidth) {
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
     * 
     * @param effectiveWidth
     */
    public void setEffectiveWidth(final int effectiveWidth) {
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
     * 
     * @param majorIntervals
     */
    public void setMajorIntervals(final int majorIntervals) {
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
     * 
     * @param paddingLeft
     */
    public void setPaddingLeft(final int paddingLeft) {
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
     * 
     * @param paddingRight
     */
    public void setPaddingRight(final int paddingRight) {
        this.paddingRight = paddingRight;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimescaleModel other = (TimescaleModel) obj;
        if (majorIntervals != other.majorIntervals) {
            return false;
        }
        if (paddingLeft != other.paddingLeft) {
            return false;
        }
        if (paddingRight != other.paddingRight) {
            return false;
        }
        if (majorWidth != other.majorWidth) {
            return false;
        }
        if (effectiveWidth != other.effectiveWidth) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + majorIntervals;
        hash = 71 * hash + paddingLeft;
        hash = 71 * hash + paddingRight;
        hash = 71 * hash + Float.floatToIntBits(majorWidth);
        hash = 71 * hash + effectiveWidth;
        return hash;
    }

    @Override
    public TimescaleModel clone() {
        return new TimescaleModel(this);
    }

}
