package org.openshapa.models.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;

/**
 * A reference cell.
 */
public class ReferenceCell extends Cell
{
    /**
     * ID of the DataCell that this reference cell is mirroring. This ID should
     * be set on construction, and not changed thereafter.
     */
    private long targetID = DBIndex.INVALID_ID;


    /**
     * Constructor.
     *
     * @param db The parent database that this reference cell will belong too.
     * @param colID The ID of the parent column that this reference cell will
     * belong too.
     * @param targetID The id of the cell that the reference cell is pointing
     * too.
     *
     * @throws SystemErrorException If unable to create the reference cell.
     *
     * @date 2007/08/29
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


    // ReferenceCell()
    /**
     * Constructor.
     *
     * @param db The parent database that this reference cell will belong too.
     * @param comment The comment to use with this reference cell.
     * @param colID The id of the parent column that this reference cell will
     * belong too.
     * @param targetID The id of the cell that this reference cell is pointing
     * too.
     *
     * @throws SystemErrorException If unable to create reference cell.
     *
     * @date 2007/08/29
     */
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


    // ReferenceCell()
    /**
     * Copy Constructor.
     *
     * @param rc The reference cell that we are creating a duplicate from.
     *
     * @throws SystemErrorException If unable to create reference cell.
     *
     * @date 2007/08/29
     */
    public ReferenceCell(ReferenceCell rc)
        throws SystemErrorException
    {
        super((Cell)rc);

        // TODO:  add sanity checking
        this.itsColID = rc.itsColID;
        this.targetID = rc.targetID;

    } /* ReferenceCell::ReferenceCell(rc) */


    // getTargetID()
    /**
     * @return The ID of the target DataCell.
     *
     * @date 2007/08/29
     */

    public long getTargetID()
    {
        return targetID;
    } /* ReferenceCell::getTargetID() */


    // toDBString()
    /**
     * @return A String representation of the DataCell for comparison against
     * the expected value.<br>
     *
     * <i>This function is intended for debugging purposses.</i>
     */
    @Override
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


    // toString()
    /**
     * @return A String representation of the DataCell for display.
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


    // lookupTargetCell()
    /**
     * Attempt to look up the target of the reference cell, and return a
     * reference to the DataCell if it exists.  If there is no such DataCell,
     * throw a system error.
     *
     * @throws SystemErrorException If unable to lookup target cell.
     *
     * @date 2007/08/30
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


    // hashCode()
    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode() * Constants.SEED1;
        hash += HashUtils.Long2H(targetID) * Constants.SEED2;

        return hash;
    }


    // equals()
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
