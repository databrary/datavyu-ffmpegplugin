/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.cont;
import au.com.nicta.openshapa.db.*;

/**
 *
 * @author FGA
 */
public interface ContinuousDataController
{
    public void setCurrentLocation(TimeStamp ts);
    public TimeStamp getCurrentLocation();
    public void shutdown(ContinuousDataViewer viewer);
}
