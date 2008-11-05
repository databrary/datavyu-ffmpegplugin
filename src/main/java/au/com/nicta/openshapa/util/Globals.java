/*
 * Globals.java
 *
 * Created on June 27, 2007, 5:02 PM
 *
 */

package au.com.nicta.openshapa.util;

/**
 * Global static application definitions
 * @author FGA
 */
public final class Globals
{
  /**
   * Application name
   */
  public final static String APPLICATION_NAME = "OpenSHAPA";

  /**
   * Release type
   */
  public final static String RELEASE_TYPE     = "a";

  /**
   * Major version number
   */
  public final static String MAJOR_VERSION    = "1";

  /**
   * Minor version number
   */
  public final static String MINOR_VERSION    = "000";
  
  
  /**
   * Returns a formated version string
   * @return "a1.000"
   */
  public final static String version()
  {
    return (RELEASE_TYPE + MAJOR_VERSION + "." + MINOR_VERSION);
  }
  
  /**
   * Returns a formated application string
   * @return "OpenSHAPA a1.000"
   */
  public final static String appString()
  {
    return (Globals.APPLICATION_NAME + " " + Globals.version());
  }
}//End of Globals class definition
