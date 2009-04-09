/*
 * DBIndex.java
 *
 * Created on April 22, 2007, 9:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

import au.com.nicta.openshapa.util.Constants;
import au.com.nicta.openshapa.util.HashUtils;
import org.openshapa.util.OpenHashtable;


/**
 * class DBIndex
 *
 * A single instance of DBIndex is used to construct and maintain an index
 * of all DBElements in the host database.  This instance also assigns
 * IDs, and handles the matter of replacing one representation of a DBElement
 * with the next.
 *
 *                                          JRM -- 4/22/07
 *
 * @author mainzer
 */
public class DBIndex
{

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**
     *
     * INVALID_ID: Constant specifying the value assigned to an ID when it has
     *      not been set.
     *
     * nextID:  Long field used to store the next ID to be assigned to
     *      a DBElement that is added to the index.
     *
     * db:  Reference to the instance of Database of which this index is part.
     *
     * index: Hashtable containg references to all instances of DBElement that
     *      reside in db.
     */

     /** Value assigned to an ID that has not been specified */
     public static final long INVALID_ID = 0;

     /** The next ID to be assigned */
     private long nextID = 1;

     /** Reference to the Database of which this instance is part */
     protected Database db = null;

     /** Index of all instances of DBelement in the Database */
     protected OpenHashtable<Long, DBElement> index =
             new OpenHashtable<Long, DBElement>();

    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * DBIndex()
     *
     * Constructor for DBIndex.
     *
     * Construct an instance of DBIndex.  All that happens is that we store
     * the db parameter.
     *
     * Changes:
     *
     *    - None.
     */

    public DBIndex(Database db)
         throws SystemErrorException
    {
        super();

        final String mName = "DBIndex::DBIndex(db): ";

        if (db == null) {
            throw new SystemErrorException(mName + "Bad db param");
        }

        this.db = db;

        return;

    } /* DBIndex::DBIndex(db) */

    /**
     * Compares this column against another.
     *
     * @param obj The object to compare this against.
     *
     * @return true if the Object obj is logically equal to this, false
     * otherwise.
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
        DBIndex i = (DBIndex) obj;
        return super.equals(i)
            && nextID == i.nextID
            && db == null ? i.db == null : db.equals(i.db)
            && index == null ? i.index == null : index.equals(i.index);
    }

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += HashUtils.Long2H(nextID) * Constants.SEED1;
        hash += HashUtils.Obj2H(db) * Constants.SEED2;
        hash += HashUtils.Obj2H(index) * Constants.SEED3;

        return hash;
    }

    /**
     * toString() -- overrride
     *
     * Returns a String representation of the contents of the index.<br>
     *
     * <i>This function is intended for debugging purposses.</i>
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     *
     */
    public String toString()
    {
        boolean first = true;
        String s;
        DBElement dbe;
        java.util.Enumeration<DBElement> entries;

        s = "((DBIndex) (index_contents: (";
        entries = index.elements();
        while ( entries.hasMoreElements() )
        {
            if ( first )
            {
                first = false;
            }
            else
            {
                s += ", ";
            }
            dbe = entries.nextElement();
            s += dbe.toString();
        }
        s += ")))";

        return s;

    } /* DBIndex::toDBString() */


    /*************************************************************************/
    /****************************** Methods: *********************************/
    /*************************************************************************/

    /**
     * addElement()
     *
     * Assign the provided DBElement an id, and insert it into the index.
     *
     *                                                 JRM -- 4/23/07
     *
     * Changes:
     *
     *   - None.
     */

    public void addElement(DBElement dbe)
       throws SystemErrorException
    {
        final String mName = "DBIndex::addElement(dbe): ";

        if (dbe == null) {
            throw new SystemErrorException(mName + "Bad dbe param");
        }
        else if ( dbe.getDB() != db )
        {
            throw new SystemErrorException(mName + "dbe.getDB() != db");
        }
        else if ( dbe.getID() != INVALID_ID )
        {
            throw new SystemErrorException(mName +
                                           "dbe.getID() != INVALID_ID");
        }
        else if ( index.containsReference(dbe) )
        {
            throw new SystemErrorException(mName + "dbe alread in index?!?");
        }
        else if ( index.containsKey(this.nextID) )
        {
            throw new SystemErrorException(mName + "nextID already in use?!?");
        }

        dbe.setID(this.nextID);
        index.put(this.nextID, dbe);
        this.nextID++;

        if ( this.nextID == INVALID_ID )
        {
            throw new SystemErrorException(mName + "nextID wrapped around!?!");
        }

        return;

    } /* DBIndex::addElement(dbe) */

    /**
     * getElement()
     *
     * Get the instance of DBElement corresponding with the supplied id.
     *
     *                                                 JRM -- 4/23/07
     *
     * Changes:
     *
     *   - None.
     */

    public DBElement getElement(long targetID)
       throws SystemErrorException
    {
        final String mName = "DBIndex::getElement(targetID): ";
        DBElement dbe = null;

        if ( targetID == INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }

        dbe = index.get(targetID);

        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "target doesn't exist.");
        }

        return dbe;

    } /* DBIndex::getElement(targetID) */

    /**
     * inIndex(targetID)
     *
     * Return true if the index contains an entry matching the provided id.
     *
     * Changes:
     *
     *    - None.
     */

    public boolean inIndex(long targetID)
       throws SystemErrorException
    {
        final String mName = "DBIndex::inIndex(targetID): ";
        boolean inIndex = false;

        if ( targetID == INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
        else if ( index.containsKey(targetID) )
        {
            inIndex = true;
        }

        return inIndex;

    } /* DBIndex::inIndex(targetID) */

    /**
     * removeElement()
     *
     * Remove the instance of DBElement with the specified id from the index.
     *
     *                                                 JRM -- 4/23/07
     *
     * Changes:
     *
     *   - None.
     */

    void removeElement(long targetID)
       throws SystemErrorException
    {
        final String mName = "DBIndex::removeElement(targetID): ";

        if ( targetID == INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
        else if ( ! index.containsKey(targetID) )
        {
            throw new SystemErrorException(mName + "targetID not in index.");
        }
        else if ( index.remove(targetID) == null )
        {
            throw new SystemErrorException(mName + "index.remove() failed.");
        }

        return;

    } /* DBIndex::removeElement(targetID) */

    /**
     * replaceElement()
     *
     * Search the index for an instance of DBElement with the same id as that
     * of the supplied instance.  Remove the instance from the index, and
     * replace it with the supplied instance.
     *
     *                                                 JRM -- 4/23/07
     *
     * Changes:
     *
     *   - None.
     */

    void replaceElement(DBElement dbe)
       throws SystemErrorException
    {
        final String mName = "DBIndex::replaceElement(dbe): ";
        DBElement old_dbe = null;

        if (dbe == null) {
            throw new SystemErrorException(mName + "Bad dbe param");
        }
        else if ( dbe.getDB() != db )
        {
            throw new SystemErrorException(mName + "dbe.getDB() != db");
        }
        else if ( dbe.getID() == INVALID_ID )
        {
            throw new SystemErrorException(mName +
                                           "dbe.getID() == INVALID_ID");
        }

        old_dbe = index.get(dbe.getID());

        if ( old_dbe == null )
        {
            throw new SystemErrorException(mName +
                                           "can't replace -- not in index.");
        }
        else if ( dbe.getClass() != old_dbe.getClass() )
        {
            throw new SystemErrorException(mName + "type mis-match.");
        }

        if ( index.remove(dbe.getID()) == null )
        {
            throw new SystemErrorException(mName + "remove failed.");
        }

        index.put(dbe.getID(), dbe);

        return;

    } /* replaceElement(dbe) */

    /**
     * toDBString()
     *
     * Returns a String representation of the contents of the index.<br>
     *
     * <i>This function is intended for debugging purposses.</i>
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     *
     */
    public String toDBString()
    {
        boolean first = true;
        String s;
        DBElement dbe;
        java.util.Enumeration<DBElement> entries;

        try
        {
            s = "((DBIndex) (nextID: ";
            s += nextID;
            s += ") (index_size: ";
            s += index.size();
            s += ") (index_contents: (";
            entries = index.elements();
            while ( entries.hasMoreElements() )
            {
                if ( first )
                {
                    first = false;
                }
                else
                {
                    s += ", ";
                }
                dbe = entries.nextElement();
                s += dbe.toDBString();
            }
            s += ")))";
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return s;

    } /* DBIndex::toDBString() */

} /* class DBIndex */
