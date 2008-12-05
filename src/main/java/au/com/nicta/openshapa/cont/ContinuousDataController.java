package au.com.nicta.openshapa.cont;

import au.com.nicta.openshapa.db.TimeStamp;
import au.com.nicta.openshapa.views.continuous.ContinuousDataViewer;

/**
 * An interface for controlling continuous data.
 *
 * @author FGA
 */
public interface ContinuousDataController {

    /**
     * The method that gets invoked by the viewer when the viewer is refreshed.
     * This can be used to update progress bars, of the users current location
     * within the continuous data stream.
     *
     * @param milliseconds The time of the current position within the
     * datastream, in milliseconds.
     */
    public void setCurrentLocation(final long milliseconds);

    public TimeStamp getCurrentLocation();
    public void shutdown(ContinuousDataViewer viewer);
}
