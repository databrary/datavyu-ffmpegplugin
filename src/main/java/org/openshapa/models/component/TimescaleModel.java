package org.openshapa.models.component;

import java.awt.Color;

/**
 * This model provides information used to render the time scale on the tracks
 * interface.
 */
public class TimescaleModel implements Cloneable {

	/** The number of nanoseconds represented by one pixel on the time scale */
	private long nanosecondsPerPixel;
    /** Pad the scale from the left by this many pixels */
    private int paddingLeft;
    /** Pad the scale from the right by this many pixels */
    private int paddingRight;
    /** Height of the zoom window indicator in pixels */
    private int zoomWindowIndicatorHeight;
    /** Height of the transition between the zoom window indicator and the track in pixels */
    private int zoomWindowToTrackTransitionHeight;
    /** This is the effective width of the scale, i.e. after padding applied */
    private int effectiveWidth;
    /** This is the height of the scale */
    private int height;
    /** Color of the zoom window indicator bar */
    private Color zoomWindowIndicatorColor = Color.black;
    /** Background color of the time scale */
    private Color timescaleBackgroundColor = Color.black;
    /** Color of the hourly interval markers */
    private Color hoursMarkerColor = Color.black;
    /** Color of the minutes interval markers */
    private Color minutesMarkerColor = Color.black;
    /** Color of the seconds interval markers */
    private Color secondsMarkerColor = Color.black;
    /** Color of the milliseconds interval markers */
    private Color millisecondsMarkerColor = Color.black;
    
    public TimescaleModel() {
    }

    protected TimescaleModel(final TimescaleModel other) {
    	nanosecondsPerPixel = other.nanosecondsPerPixel;
        paddingLeft = other.paddingLeft;
        paddingRight = other.paddingRight;
        zoomWindowIndicatorHeight = other.zoomWindowIndicatorHeight;
        zoomWindowToTrackTransitionHeight = other.zoomWindowToTrackTransitionHeight;
        effectiveWidth = other.effectiveWidth;
        height = other.height;
        zoomWindowIndicatorColor = other.zoomWindowIndicatorColor;
        timescaleBackgroundColor = other.timescaleBackgroundColor;
        hoursMarkerColor = other.hoursMarkerColor;
        minutesMarkerColor = other.minutesMarkerColor;
        secondsMarkerColor = other.secondsMarkerColor;
        millisecondsMarkerColor = other.millisecondsMarkerColor;
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
     * @return The number of nanoseconds represented by one pixel on the time scale
     */
    public long getNanosecondsPerPixel() {
        return nanosecondsPerPixel;
    }

    /**
     * Sets the number of nanoseconds represented by one pixel on the time scale
     * 
     * @param nanosecondsPerPixel
     */
    public void setNanosecondsPerPixel(final long nanosecondsPerPixel) {
    	assert nanosecondsPerPixel > 0;
        this.nanosecondsPerPixel = nanosecondsPerPixel;
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

    /**
     * @return height of the zoom window indicator in pixels
     */
    public int getZoomWindowIndicatorHeight() {
    	return zoomWindowIndicatorHeight;
    }
    
    /**
     * Set the height of the zoom window indicator in pixels
     */
    public void setZoomWindowIndicatorHeight(int zoomWindowIndicatorHeight) {
    	assert zoomWindowIndicatorHeight >= 0;
    	this.zoomWindowIndicatorHeight = zoomWindowIndicatorHeight;
    }
    
    /**
     * @return height of the transition from the zoom window to the track
     */
    public int getZoomWindowToTrackTransitionHeight() {
    	return zoomWindowToTrackTransitionHeight;
    }
    
    /**
     * Set the height of the transition from teh zoom window to the track
     */
    public void setZoomWindowToTrackTransitionHeight(int zoomWindowToTrackTransitionHeight) {
    	assert zoomWindowToTrackTransitionHeight >= 0;
    	this.zoomWindowToTrackTransitionHeight = zoomWindowToTrackTransitionHeight;
    }
    
    /**
     * @return height of the timescale track
     */
    public int getHeight() {
    	return height;
    }
    
    /**
     * Set the height of the timescale track
     */
    public void setHeight(int height) {
    	assert height > 0;
    	this.height = height;
    }
    
    public Color getZoomWindowIndicatorColor() {
    	return zoomWindowIndicatorColor;
    }
    
    public void setZoomWindowIndicatorColor(Color zoomWindowIndicatorColor) {
    	this.zoomWindowIndicatorColor = zoomWindowIndicatorColor;
    }
    
    public Color getTimescaleBackgroundColor() {
    	return timescaleBackgroundColor;
    }
    
    public void setTimescaleBackgroundColor(Color timescaleBackgroundColor) {
    	this.timescaleBackgroundColor = timescaleBackgroundColor;
    }
    
    public Color getHoursMarkerColor() {
    	return hoursMarkerColor;
    }
    
    public void setHoursMarkerColor(Color hoursMarkerColor) {
    	this.hoursMarkerColor = hoursMarkerColor;
    }
    
    public Color getMinutesMarkerColor() {
    	return minutesMarkerColor;
    }
    
    public void setMinutesMarkerColor(Color minutesMarkerColor) {
    	this.minutesMarkerColor = minutesMarkerColor;
    }
    
    public Color getSecondsMarkerColor() {
    	return secondsMarkerColor;
    }
    
    public void setSecondsMarkerColor(Color secondsMarkerColor) {
    	this.secondsMarkerColor = secondsMarkerColor;
    }
    
    public Color getMillisecondsMarkerColor() {
    	return millisecondsMarkerColor;
    }
    
    public void setMillisecondsMarkerColor(Color millisecondsMarkerColor) {
    	this.millisecondsMarkerColor = millisecondsMarkerColor;
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
        return 
        	(nanosecondsPerPixel == other.nanosecondsPerPixel) &&
        	(paddingLeft == other.paddingLeft) &&
        	(paddingRight == other.paddingRight) &&
        	(zoomWindowIndicatorHeight == other.zoomWindowIndicatorHeight) &&
        	(zoomWindowToTrackTransitionHeight == other.zoomWindowToTrackTransitionHeight) &&
        	(effectiveWidth == other.effectiveWidth) &&
        	(height == other.height) &&
        	zoomWindowIndicatorColor.equals(other.zoomWindowIndicatorColor) &&
        	timescaleBackgroundColor.equals(other.timescaleBackgroundColor) &&
        	hoursMarkerColor.equals(other.hoursMarkerColor) &&
        	minutesMarkerColor.equals(other.minutesMarkerColor) &&
        	secondsMarkerColor.equals(other.secondsMarkerColor) &&
        	millisecondsMarkerColor.equals(other.millisecondsMarkerColor);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (int) nanosecondsPerPixel;
        hash = 71 * hash + paddingLeft;
        hash = 71 * hash + paddingRight;
        hash = 71 * hash + zoomWindowIndicatorHeight;
        hash = 71 * hash + zoomWindowToTrackTransitionHeight;
        hash = 71 * hash + effectiveWidth;
        hash = 71 * hash + height;
        hash = 71 * hash + zoomWindowIndicatorColor.hashCode();
        hash = 71 * hash + timescaleBackgroundColor.hashCode();
        hash = 71 * hash + hoursMarkerColor.hashCode();
        hash = 71 * hash + minutesMarkerColor.hashCode();
        hash = 71 * hash + secondsMarkerColor.hashCode();
        hash = 71 * hash + millisecondsMarkerColor.hashCode();
        return hash;
    }

    @Override
    public TimescaleModel clone() {
        return new TimescaleModel(this);
    }
}
