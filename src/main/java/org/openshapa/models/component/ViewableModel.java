package org.openshapa.models.component;

/**
 * This model provides parameters used for determining what can be viewed on the
 * tracks information panel.
 */
public final class ViewableModel implements Viewport {

    /** The end time of the longest track (offset included) in milliseconds */
    private final long maxEnd;

    /** The pixel width of a time interval */
    private final double viewWidth;

    /** The start time of the zoomed window */
    private final long viewStart;

    /** The end time of the zoomed window */
    private final long viewEnd;

    public ViewableModel(final long maxEnd, final double viewWidth,
        final long viewStart, final long viewEnd) {
        this.maxEnd = maxEnd;
        this.viewWidth = viewWidth;
        this.viewStart = viewStart;
        this.viewEnd = viewEnd;
    }

    /**
     * @return The end time of the zoomed window
     */
    @Override public long getViewEnd() {
        return viewEnd;
    }

    /**
     * Set the start time of the zoomed window.
     *
     * @return The start time of the zoomed window
     */
    @Override public long getViewStart() {
        return viewStart;
    }

    @Override public long getMaxEnd() {
        return maxEnd;
    }

    @Override public double getResolution() {
        return (viewWidth > 0) ? ((viewEnd - viewStart) / viewWidth)
                               : Double.NaN;
    }

    @Override public double getViewWidth() {
        return viewWidth;
    }

    @Override public long getViewDuration() {
        return getViewEnd() - getViewStart() + 1;
    }

    @Override public double computePixelXOffset(final long time) {
        return (int) Math.round((time - getViewStart()) / getResolution());
    }

    @Override public long computeTimeFromXOffset(final double offset) {
        return (long) (Math.round(offset * getResolution()));
    }

    @Override public boolean isOffsetInViewport(final double offset) {
        return (computePixelXOffset(getViewStart()) <= offset)
            && (offset <= computePixelXOffset(getViewEnd()));
    }

    @Override public boolean isTimeInViewport(final long time) {
        return (getViewStart() <= time) && (time <= getViewEnd());
    }

    @Override public double getZoomLevel() {
        return getZoomSettingFor(getResolution());
    }

    public ViewableModel zoomViewport(final double zoomLevel,
        final long centerTime) {
        final double millisecondsPerPixel = getMillisecondsPerPixelFor(
                zoomLevel);

        // preserve the needle position
        long centerPositionTime = Math.min(Math.max(centerTime, 0),
                getMaxEnd());

        long zoomCenterTime = 0;
        double dxZoomCenterRatio = 0.5;

        if ((centerTime >= 0) && isTimeInViewport(centerTime)) {

            long zoomWindowStart = getViewStart();

            dxZoomCenterRatio = (double) (centerPositionTime - zoomWindowStart)
                / getViewDuration();

            zoomCenterTime = centerPositionTime;
        } else {
            zoomCenterTime = (getViewStart() + getViewEnd()) / 2;
        }

        dxZoomCenterRatio = Math.min(Math.max(dxZoomCenterRatio, 0.0), 1.0);

        long newZoomWindowTimeRange = Math.round(millisecondsPerPixel
                * getViewWidth());
        newZoomWindowTimeRange = Math.max(newZoomWindowTimeRange, 1);
        newZoomWindowTimeRange = Math.min(newZoomWindowTimeRange,
                getMaxEnd() + 1);

        assert (newZoomWindowTimeRange >= 1)
            && (newZoomWindowTimeRange <= (getMaxEnd() + 1));

        long newStart = Math.round(zoomCenterTime
                - (dxZoomCenterRatio * newZoomWindowTimeRange));

        if ((newStart + newZoomWindowTimeRange) > getMaxEnd()) {
            newStart = getMaxEnd() - newZoomWindowTimeRange + 1;
        }

        if (newStart < 0) {
            newStart = 0;
        }

        assert (0 <= newStart) && (newStart <= getMaxEnd());

        long newEnd = newStart + newZoomWindowTimeRange - 1;
        assert (0 <= newEnd) && (newEnd <= getMaxEnd());

        return new ViewableModel(getMaxEnd(), getViewWidth(), newStart, newEnd);
    }

    private double getMillisecondsPerPixelFor(final double zoomSettingValue) {
        double value = (lowerMsPerPixel()
                * Math.exp(
                    Math.log(upperMsPerPixel() / lowerMsPerPixel())
                    * (1.0 - zoomSettingValue)));

        return Math.min(Math.max(value, lowerMsPerPixel()), upperMsPerPixel());
    }

    /**
     * @return Lower bound on the number of milliseconds per pixel.
     */
    private double lowerMsPerPixel() {
        return 1.0;
    }

    /**
     * @return Upper bound on the number of milliseconds per pixel.
     */
    private double upperMsPerPixel() {

        final long maxTimeMilliseconds = (getMaxEnd() > 0)
            ? getMaxEnd() : (24 * 60 * 60 * 1000);

        return Math.ceil((double) maxTimeMilliseconds / getViewWidth());
    }

    private double getZoomSettingFor(final double millisecondsPerPixel) {

        if (millisecondsPerPixel >= upperMsPerPixel()) {
            return 0;
        }

        final double value = 1
            - (Math.log(millisecondsPerPixel / lowerMsPerPixel())
                / Math.log(upperMsPerPixel() / lowerMsPerPixel()));

        return Math.min(Math.max(value, 0), 1.0);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ViewableModel [getMaxEnd()=");
        builder.append(getMaxEnd());
        builder.append(", getResolution()=");
        builder.append(getResolution());
        builder.append(", getViewDuration()=");
        builder.append(getViewDuration());
        builder.append(", getViewEnd()=");
        builder.append(getViewEnd());
        builder.append(", getViewStart()=");
        builder.append(getViewStart());
        builder.append(", getViewWidth()=");
        builder.append(getViewWidth());
        builder.append(", getZoomLevel()=");
        builder.append(getZoomLevel());
        builder.append("]");

        return builder.toString();
    }

}
