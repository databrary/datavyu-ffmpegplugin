package org.openshapa.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;

/**
 * Class DBElement
 *
 * Abstract class from which most classes in the database are descended.
 * This class exists to allow us to track the last user to modify the data
 * contained in the instance.
 */
public abstract class DBElement implements Cloneable {

    /**
     * Reference to the database with which this instance is associated.  Note
     * that this instance need not be an actual part of the target database, as
     * copies of database elements will be provided to the user interface code
     * on request, and the user interface code may create database elements to
     * be copied into the target database.
     */
    private Database db;

    /**
     * Unique ID assigned to this database element. To support an undo
     * capability, and to help insure database integrity, the blocks of memory
     * used to store database element will change frequently.
     *
     * To allow continuing references to database elements, a unique ID is
     * assigned which is maintained for the life of the element. Thus, the ID is
     * copied from one representation of the database element to the next.
     */
    private long id = DBIndex.INVALID_ID;

    /**
     * ID of the last user to touch this database element.
     *
     * This field is initialized to zero, and will remain at that value until
     * the setLastModUID() method is called.  At that point, the database will
     * be queried for the current user ID, and that ID will be loaded into the
     * lastModUID field.
     */
    private int lastModUID = 0;

    /**
     * Constructor for instances of DBelement.
     *
     * @param db The database that this DBElement will be used for.
     *
     * @throws SystemErrorException If the supplied database is null.
     *
     * @date 2007/04/11
     */
     public DBElement(Database db) throws SystemErrorException {
         super();

         if (db == null) {
             final String mName = "DBElement::DBElement(db): ";
             throw new SystemErrorException(mName + "Bad db param");
         }

         this.db = db;
     }

     /**
      * Copy constructor.
      *
      * @param dbe The DBElement To copy.
      *
      * @throws SystemErrorException If the DBElement or dbe database is null,
      * or if the lastModUID of the dbe is invalid.
      *
      * @date 2007/04/11
      */
     public DBElement(final DBElement dbe) throws SystemErrorException {
         super();

         if (dbe == null || dbe.db == null ||
             !( dbe.db.isValidUID(dbe.lastModUID))) {
             final String mName = "DBElement::DBElement(dbe): ";
             throw new SystemErrorException(mName + "Bad dbe param");
         }

         this.db = dbe.db;
         this.id = dbe.id;
         this.lastModUID = dbe.lastModUID;
     }

    /**
     * Subclasses should override this method, which constructs a string
     * containing the contents of the instance with sufficient detail for
     * debugging.
     */
    public String toDBString()
        throws SystemErrorException
    {

        return "toDBString() not implemented";
    
    } /* DBElement::toDBString() */

    /**
     * Set the id to INVALID_ID.
     *
     * @date 2008/02/19
     */
    protected void clearID() 
        throws SystemErrorException
    {
        this.id = DBIndex.INVALID_ID;
        return;
    }

    /**
     * @return A reference to the database with which this DBElement is
     * associated with.
     *
     * @date 2007/04/11
     */
    public Database getDB() 
    {

        return this.db;
    
    } /* DBElement::getDB() */

    /**
     * @return The unique ID assigned to this DBElement.
     *
     * @date 2007/04/11
     */
    public long getID()
    {

        return this.id;
    
    } /* DBElement::getID() */

    /**
     * Return the user ID associated with the last change to this database
     * element, or zero if no last mod UID is specified.
     *
     * @return last mod UID
     *
     * @date 2007/04/11
     */
    public int getLastModUID() 
    {

        return this.lastModUID;
    
    } /* DBElement::getLastModUID() */

    /**
     * Set the unique ID of this DBElement.
     *
     * @param id ID assigned to this DBElement.
     *
     * @throws SystemErrorException If the supplied id is invalid.
     *
     * @date 2007/04/11
     *
     */
    public void setID(long id)
        throws SystemErrorException
    {
        if (id == DBIndex.INVALID_ID)
        {
            final String mName = "DBElement::setID(id): ";
            throw new SystemErrorException(mName + "invalid id");
        } 
        else
        {
           this.id = id;
        }

        return;

    } /* DBElement::setID() */

    /**
     * Query the associated database, and set lastModUID to the current user
     * ID.
     *
     * @throws SystemErrorException If the database this element references is
     * null.
     *
     * @date 2007/04/11
     */
    public void setLastModUID() 
        throws SystemErrorException
    {
        if (this.db == null)
        {
            final String mName = "DBElement::setLastModUID(uid): ";
            throw new SystemErrorException(mName + "Bad this.db on entry");
        } 
        else
        {
            this.lastModUID = this.db.getCurUID();
        }

        return;
    
    } /* DBElement::setLastModUID() */

    /**
     * Set the UID of the last user to modify this database element.  The UID
     * must be valid.
     *
     * This version of setLastModUID() is intended for use reloading databases
     * from file.  In the normal runtime case, user the version of
     * setLastModUID that takes no parameters.  This version just queries
     * the database for the current user ID, and uses that value to set
     * lastModUID.
     *
     * @param  uid UID associated with the last user to modify this database
     * element.
     *
     * @throws SystemErrorException if The database this element references is
     * null, or if the supplied userid is invalid.
     *
     * @date 2007/04/11
     */
    public void setLastModUID(int uid) throws SystemErrorException {
        final String mName = "DBElement::setLastModUID(uid): ";

        if (this.db == null)
        {
            throw new SystemErrorException(mName + "Bad this.db on entry");
        } 
        else if ( !(this.db.isValidUID(uid)) )
        {
            throw new SystemErrorException(mName + "invalid uid");
        } 
        else
        {
            this.lastModUID = uid;
        }

        return;
    
    } /* DBElement::setLastModUID() */

    /**
     * Creates a new copy of the object.
     *
     * @return A duplicate of this object.
     *
     * @throws java.lang.CloneNotSupportedException If the clone interface has
     * not been implemented.
     */
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
    public int hashCode()
    {
        int hash = lastModUID * Constants.SEED1;
        hash += HashUtils.Long2H(id) * Constants.SEED2;
        hash += HashUtils.Obj2H(db) * Constants.SEED3;

        return hash;
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
    public boolean equals(final Object obj)
    {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        // Must be this class to be here
        DBElement e = (DBElement) obj;
        return (id == e.id)
            && (lastModUID == e.lastModUID)
            && (db == null ? e.db == null : db.equals(e.db));
    }

} /* class DBElement */
