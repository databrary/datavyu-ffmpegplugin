/*
 * DBElement.java
 *
 * Abstract class from which most classes in the database are descended.
 * This class exists to allow us to track the last user to modify the data
 * contained in the instance.
 *
 * Created on November 9, 2006, 4:22 PM
 *
 */

package au.com.nicta.openshapa.db;

/**
 * Class DBElement
 *
 * Abstract class from which most classes in the database are descended.
 * This class exists to allow us to track the last user to modify the data
 * contained in the instance.
 *
 * Changes:
 *
 *    - Re-worked the class heavily.
 *
 *                         JRM -- 4/11/07
 *
 * @author FGA
 */
public abstract class DBElement implements Cloneable
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**
     * db:      Reference to the database with which this instance is
     *          associated.  Note that this instance need not be an actual
     *          part of the target database, as copies of database elements
     *          will be provided to the user interface code on request, and
     *          the user interface code may create database elements to be
     *          copied into the target database.
     *
     * id:      Unique ID assigned to this database element.  To support an
     *          undo capability, and to help insure database integrity,
     *          the blocks of memory used to store database element will change
     *          frequently.
     *
     *          To allow continuing references to database elements, a unique
     *          ID is assigned which is maintained for the life of the element.
     *          Thus, the ID is copied from one representation of the database
     *          element to the next.
     *
     * lastModUID:    ID of the last user to touch this database element.
     *
     *          This field is initialized to zero, and will remain at that
     *          value until the setLastModUID() method is called.  At that
     *          point, the database will be queried for the current user ID,
     *          and that ID will be loaded into the lastModUID field.
     */

    /** database with which this element is associated */
    protected Database db;

    /** unique ID of this element */
    protected long id = DBIndex.INVALID_ID;

    /** ID of last user to modify this element */
    protected int lastModUID = 0;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * DBElement()
     *
     * Constructor for instances of DBelement.
     *
     * Two versions of this constructor -- one which sets the db field of the
     * new database element, and one which creates a copy of the instance of
     * DBElement provided as a parameter.
     *
     *                                              JRM -- 4/11/07
     *
     * Changes:
     *
     *    - None.
     *
     */

     public DBElement(Database db)
        throws SystemErrorException
     {
         super();

         final String mName = "DBElement::DBElement(db): ";

         if ( ( db == null ) ||
              ( ! ( db instanceof Database ) ) )
         {
             throw new SystemErrorException(mName + "Bad db param");
         }

         this.db = db;

     } /* DBElement::DBElement(db) */

     public DBElement(DBElement dbe)
        throws SystemErrorException
     {
         super();

         final String mName = "DBElement::DBElement(dbe): ";

         if ( ( dbe == null ) ||
              ( ! ( dbe instanceof DBElement )  ) ||
              ( dbe.db == null ) ||
              ( ! ( dbe.db instanceof Database ) ) ||
              ( ! ( dbe.db.isValidUID(dbe.lastModUID) ) ) )
         {
             throw new SystemErrorException(mName + "Bad dbe param");
         }

         this.db = dbe.db;
         this.id = dbe.id;
         this.lastModUID = dbe.lastModUID;

     } /* DBElement::DBElement(dbe) */


    /*************************************************************************/
    /****************************** Methods: *********************************/
    /*************************************************************************/

    /**
     * toDBString()
     *
     * Subclasses should override this method, which constructs a string
     * containing the contents of the instance with sufficient detail for
     * debugging.
     *
     * Changes:
     *
     *    - None.
     *
     */

    public String toDBString()
        throws SystemErrorException
    {
        return "toDBString() not implemented";
    };


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * clearID()
     *
     * Set the id to INVALID_ID.
     *
     *                                      JRM -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void clearID()
        throws SystemErrorException
    {
        this.id = DBIndex.INVALID_ID;

        return;

    } /* DBElement::clearID() */

    /**
     * getDB()
     *
     * Return a reference to the database with which this DBElement is
     * associated.
     *                                     JRM -- 4/11/07
     *
     * @return Reference to asociated instance of Database.
     *
     * Changes:
     *
     *   - None.
     */

    public Database getDB()
    {
        return this.db;

    } /* DBElement::getDB() */

    /**
     * getID()
     *
     * Return the unique ID assigned to this DBElement.
     *
     *                                     JRM -- 4/11/07
     *
     * @return ID of this DBElement.
     *
     * Changes:
     *
     *   - None.
     */

    public long getID()
    {
        return this.id;

    } /* DBElement::getID() */

    /**
     * getLastModUID()
     *
     * Return the user ID associated with the last change to this database
     * element, or zero if no last mod UID is specified.
     *
     *                                         JRM -- 4/11/07
     *
     * @return last mod UID
     *
     * Changes:
     *
     *   - None.
     */

    public int getLastModUID()
    {
        return this.lastModUID;

    } /* DBElement::getLastModUID() */

    /**
     * setID()
     *
     * Set the unique ID of this DBElement.
     *
     *                                     JRM -- 4/11/07
     *
     * @param  id  ID assigned to this DBElement.
     *
     * Changes:
     *
     *   - None.
     */

    public void setID(long id)
       throws SystemErrorException
    {
        final String mName = "DBElement::setID(id): ";

        if ( id == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "invalid id");
        }
        else
        {
           this.id = id;
        }

        return;

    } /* DBElement::setID() */


    /**
     * setLastModUID()
     *
     * Query the associated database, and set lastModUID to the current user
     * ID.
     *                                                 JRM -- 4/11/07
     *
     * Changes:
     *
     *   - None.
     */

    public void setLastModUID()
       throws SystemErrorException
    {
        final String mName = "DBElement::setLastModUID(uid): ";

        if ( ( this.db == null ) ||
             ( ! ( this.db instanceof Database ) ) )
        {
            throw new SystemErrorException(mName + "Bad this.db on entry");
        }
        else
        {
            this.lastModUID = this.db.getCurUID();
        }

        return;

    } /* DBElement::setLastModUID() */

    /**
     * setLastModUID(uid)
     *
     * Set the UID of the last user to modify this database element.  The UID
     * must be valid.
     *
     * This version of setLastModUID() is intended for use reloading databases
     * from file.  In the normal runtime case, user the version of
     * setLastModUID that takes no parameters.  This version just queries
     * the database for the current user ID, and uses that value to set
     * lastModUID.
     *
     *                                         JRM -- 4/11/07
     *
     * @param  uid UID associated with the last user to modify this
     *             database element.
     *
     * Changes:
     *
     *   - None.
     */

    public void setLastModUID(int uid)
       throws SystemErrorException
    {
        final String mName = "DBElement::setLastModUID(uid): ";

        if ( ( this.db == null ) ||
             ( ! ( this.db instanceof Database ) ) )
        {
            throw new SystemErrorException(mName + "Bad this.db on entry");

        }
        else if ( ! ( this.db.isValidUID(uid) ) )
        {
            throw new SystemErrorException(mName + "invalid uid");
        }
        else
        {
            this.lastModUID = uid;
        }

        return;

    } /* DBElement::setLastModUID(uid) */



    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /**
     * ResetID(EBElement dpe)
     *
     * This is a test function that should not be called outside of test
     * code.
     *
     * Reset the id field to its initial value DBIndex.INVALID_ID.
     *
     *                                         JRM -- 4/29/07
     *
     * @param  dbe reference to the instance of DBElement whose id field is
     *             to be reset.
     *
     * @return current id.
     *
     * Changes:
     *
     *   - None.
     */


    protected static long ResetID(DBElement dbe)
    {
        long old_id = dbe.id;

        dbe.id = DBIndex.INVALID_ID;

        return old_id;

    } /* DBElement::ResetID(dbe) */

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // Perform a shallow copy of this object.
        DBElement clone = (DBElement) super.clone();

        // Any deep copies, need to be performed within child classes as needed
        // by overriding this method.
        return clone;
    }

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        // Assuming id is unique or nearly so, return an int based off it.
        // >>> is unsigned right shift
        return (int)(id ^ (id >>> 32));
    }

    /**
     * Compares this DBElement against another object.
     * Assumption: DBElements are not equal just because their id fields match.
     * This function will test that db, id and lastModUID all match.
     * If id can be proved to be enough for testing equality we should
     * implement a simpler, faster version.
     *
     * @param obj The object to compare this against.
     * @return true if the Object obj is logically equal to this.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        // Must be this class to be here
        DBElement e = (DBElement) obj;
        return id == e.id
            && lastModUID == e.lastModUID
            && (db == e.db || (db != null && db.equals(e.db)));
    }


} /* class DBElement */
