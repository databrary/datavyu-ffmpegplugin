/*
 * DBUser.java
 *
 * Created on December 7, 2006, 12:29 PM
 *
 */

package au.com.nicta.openshapa.db;

/**
 *
 * @author felix
 */
public class DBUser
{
  /** User id */
  private int id;

  /** User name */
  private String username;

  /** Level flags */
  private int flags;

  /**
   * Creates a new instance of DBUser
   * @param id the database userid
   * @param username the user's name
   * @param flags user level flags
   */
  public DBUser(int id, String username, int flags)
  {
    this.id = id;
    this.username = username;
    this.flags = flags;
  } //End of DBUser constructor

} // End of DBUser class definition
