/*
 * ReferenceCell.java
 *
 * Created on December 7, 2006, 5:46 PM
 *
 */

package au.com.nicta.openshapa.db;

import au.com.nicta.openshapa.util.Constants;
import au.com.nicta.openshapa.util.HashUtils;

/**
 * A reference cell
 *
 * @author FGA
 */
public class ReferenceCell extends Cell
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**
     * targetID  ID of the DataCell that this reference cell is mirroring.
     *      This ID should be set on construction, and not changed thereafter.
     */

    /** ID of referenced cell. */
    private long targetID = DBIndex.INVALID_ID;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * DataCell()
     *
     * Constructor for instances of ReferenceCell.
     *
     * Only three versions of this constructor.
     *
     * The first takes only a reference to a database, a column ID, and the
     * ID of a target DataCell.
     *
     * The second is the same as the first, with the addition of comment
     * parameter.
     *
     * The third takes a reference to an instance of ReferenceCell as its
     * parameter, and returns a copy.
     *
     *                                              JRM -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public ReferenceCell(Database db,
                         long colID,
                         long targetID)
        throws SystemErrorException
    {
        super(db);

        // TODO:  add sanity checking
        this.itsColID = colID;
        this.targetID = targetID;

    } /* ReferenceCell::ReferenceCell(db, colID, targetID) */

    public ReferenceCell(Database db,
                         String comment,
                         long colID,
                         long targetID)
        throws SystemErrorException
    {
        super(db, comment);

        // TODO:  add sanity checking
        this.itsColID = colID;
        this.targetID = targetID;

    } /* ReferenceCell::ReferenceCell(db, colID, targetID) */


    public ReferenceCell(ReferenceCell rc)
        throws SystemErrorException
    {
        super((Cell)rc);

        // TODO:  add sanity checking
        this.itsColID = rc.itsColID;
        this.targetID = rc.targetID;

    } /* ReferenceCell::ReferenceCell(rc) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getTargetID
     *
     * Return the ID of the target DataCell.
     *
     *                          JRM -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */

    public long getTargetID()
    {

        return targetID;

    } /* ReferenceCell::getTargetID() */


    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/

   /**
     * toDBString()
     *
     * Returns a String representation of the DataCell for comparison
     * against the expected value.<br>
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
        String s;

        try
        {
            s = this.lookuptargetCell().toDBString();
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return s;

    } /* ReferenceCell::toDBString() */


    /**
     * toString()
     *
     * Returns a String representation of the DataCell for display.
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
        String s;

        try
        {
            s = this.lookuptargetCell().toString();
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return (s);

    } /* ReferenceCell::toString() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/

    /**
     * lookupTargetCell()
     *
     * Attempt to look up the target of the reference cell, and return a
     * reference to the DataCell if it exists.  If there is no such DataCell,
     * throw a system error.
     *                                              JRM -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */

    private DataCell lookuptargetCell()
        throws SystemErrorException
    {
        final String mName = "referenceCell::lookuptargetCell(): ";
        DBElement dbe;
        DataCell dc;

        if ( this.targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }

        dbe = this.getDB().idx.getElement(this.targetID);

        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "targetID has no referent");
        }

        if ( ! ( dbe instanceof DataCell ) )
        {
            throw new SystemErrorException(mName +
                    "targetID doesn't refer to a data cell");
        }

        dc = (DataCell)dbe;

        return dc;

    } /* referenceCell::lookuptargetCell() */

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode() * Constants.SEED1;
        hash += HashUtils.Long2H(targetID) * Constants.SEED2;

        return hash;
    }

    /**
     * Compares this reference cell against a object.
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

        ReferenceCell rc = (ReferenceCell) obj;
        return super.equals(obj) && targetID == rc.targetID;
    }
} // Class ReferenceCell
