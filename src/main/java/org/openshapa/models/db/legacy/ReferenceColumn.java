package org.openshapa.models.db.legacy;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;
import java.util.Vector;

/**
 * Instances of ReferenceColumn are used to implement spreadsheet variables
 * whose cells are mirrors of other cells in the database.
 *
 * @date 2007/08/30
 */
public class ReferenceColumn extends Column
{
    /**
     * Reference to a vector of ReferenceCell containing the cells of the
     * column. This vector is created when the column is inserted into the
     * column list, and not copied in the constructor.
     */
    private Vector<ReferenceCell> itsCells = null;


    // ReferenceColumn()
    /**
     * Constructor.
     *
     * @param db The parent database that the new reference column will belong
     * too.
     * @param name The name to use for the new reference column.
     *
     * @throws SystemErrorException If unable to create the new reference
     * column.
     *
     * @date 2007/08/29
     */
    public ReferenceColumn(Database db, String name)
    throws SystemErrorException {
        super(db);

        final String mName = "ReferenceColumn::ReferenceColumn(db, name): ";

        this.setName(name);

    } /* ReferenceColumn::ReferenceColumn(db, name, type) */


    // ReferenceColumn()
    /**
     * Constructor.
     *
     * @param db The parent database to use for this reference column.
     * @param name The name to use for the new reference column.
     * @param hidden Is the column hidden? True if yes, false otherwise.
     * @param readOnly Is the column read-only? True if yes, false otherwise.
     *
     * @throws SystemErrorException If unable to create the new reference
     * column.
     *
     * @date 2007/08/29
     */
    public ReferenceColumn(Database db,
                           String name,
                           boolean hidden,
                           boolean readOnly)
    throws SystemErrorException {
        super(db, name, hidden, readOnly);

    } /* ReferenceColumn::ReferenceColumn(db, name, hidden, readOnly) */


    // ReferenceColumn()
    /**
     * Copy Constructor. Please note: The cells of the column are NOT copied.
     *
     * @param rc The ReferenceColumn that we are creating a duplicate from.
     *
     * @throws SystemErrorException If unable to create a new reference column
     * from the supplied argument.
     *
     * @date 2007/08/29
     */
    public ReferenceColumn(ReferenceColumn rc)
        throws SystemErrorException
    {

        super((Column)rc);

    } /* ReferenceColumn::ReferenceColumn(rc) */


    // clone()
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
        ReferenceColumn clone = (ReferenceColumn) super.clone();

        try {
            clone = new ReferenceColumn(this);
        } catch (SystemErrorException e) {
            clone = null;
        }

        return clone;
    }


    // getItsCells()
    /**
     * @return The list of cells that back this reference column. Note this
     * method is protected and should only be used from within the openshapa.db
     * package.
     *
     * @date 2007/08/30
     */
    protected Vector<ReferenceCell> getItsCells()
    {

        return this.itsCells;

    } /* ReferenceColumn::getItsCells() */


    // setItsCells()
    /**
     * Set the cells this reference column represents. Note this method is
     * protected and should only be used from within the openshapa.db package.
     *
     * @param cells The new list to use for the cells of the column.
     *
     * @date 2007/08/30
     */
    protected void setItsCells(Vector<ReferenceCell> cells)
    {

        this.itsCells = cells;

        if ( this.itsCells == null )
        {
            this.numCells = 0;
        }
        else
        {
            this.numCells = this.itsCells.size();
        }

    } /* ReferenceColumn::setItsCells(cells) */


    // toDBString()
    /**
     * @return A String representation of the ReferenceColumn for comparison
     * against the expected value.<br>
     *
     * <i>This function is intended for debugging purposses.</i>
     */

    @Override
    public String toDBString()
    {
        String s;

        try
        {
            s = "( ReferenceColumn (name " + this.name +
                ") (id " + this.getID() +
                ") (hidden " + this.hidden +
                ") (readOnly " + this.readOnly +
                ") (numCells " + this.numCells +
                this.itsCellsToDBString() +  "))";
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return s;

    } /* ReferenceColumn::toDBString() */


    // toString()
    /**
     * @return A String representation of the ReferenceColumn for display.
     */

    @Override
    public String toString()
    {
        String s;

        try
        {
            s = "(" + this.getName()  + ", " + this.itsCellsToString() + ")";
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return (s);

    } /* ReferenceColumn::toString() */


    // constructItsCells()
    /**
     * Allocate the Vector of ReferenceCell used to store cells.  This method
     * should only be called when the ReferenceColumn is being inserted in the
     * column list.
     *
     * @throws SystemErrorException If unable to construct its cells.
     *
     * @param 2007/08/30
     */

    protected void constructItsCells()
        throws SystemErrorException
    {
        final String mName = "ReferenceColumn::constructItsCells(): ";

        if ( this.itsCells != null )
        {
            throw new SystemErrorException(mName +
                                           "itsCells already allocated?");
        }

        // TODO: add more sanity checks?

        this.itsCells = new Vector<ReferenceCell>();

        return;

    } /* ReferenceColumn::constructItsCells() */


    // itsCellsToDBString()
    /**
     * Construct a string containing the values of the cells in a
     * format that displays the full status of the arguments and
     * facilitates debugging.
     *
     * @return A database string representation of the cells of this reference
     * column.
     *
     * @throws SystemErrorException If unable to convert the contents of the
     * reference column into a DB string.
     *
     * @date 2007/08/30
     */

    protected String itsCellsToDBString()
        throws SystemErrorException
    {
        final String mName = "ReferenceColumn::itsCellsToDBString(): ";
        int i = 0;
        String s;

        if ( this.itsCells == null )
        {
            s = "(itsCells ())";
        }
        else
        {
            this.numCells = this.itsCells.size();

            if ( this.numCells <= 0 )
            {
                throw new SystemErrorException(mName + "numCells <= 0");
            }

            s = new String("(itsCells (");

            while ( i < (this.numCells - 1) )
            {
                s += this.getCell(i).toDBString() + ", ";
                i++;
            }

            s += this.getCell(i).toDBString();

            s += "))";
        }

        return s;

    } /* ReferenceColumn::itsCellsToDBString() */


    // itsCellsToString()
    /**
     * Construct a string containing the values of the cells in the column.
     *
     * @return A string representation of the cells of this reference column.
     *
     * @throws SystemErrorException If unable to convert the contents of its
     * cells into a string.
     *
     * @date 2007/08/30
     */

    protected String itsCellsToString()
        throws SystemErrorException
    {
        final String mName = "ReferenceColumn::itsCellsToString(): ";
        int i = 0;
        String s;

        if ( this.itsCells == null )
        {
            s = "()";
        }
        else
        {
            this.numCells = this.itsCells.size();

            if ( this.numCells <= 0 )
            {
                throw new SystemErrorException(mName + "numCells <= 0");
            }

            s = new String("(");

            while ( i < (numCells - 1) )
            {
                s += this.getCell(i).toString() + ", ";
                i++;
            }

            s += getCell(i).toString();

            s += ")";
        }

        return s;

    } /* ReferenceColumn::itsCellsToString() */


    // appendCell()
    /**
     * Append the supplied DataCell to the end of the vector of cells.
     *
     * @param newCell The new cell to append to the end of the vector of cells.
     *
     * @throws SystemErrorException If unable to append the new cell to the
     * reference column.
     *
     * @date 2007/08/30
     */

    protected void appendCell(ReferenceCell newCell)
        throws SystemErrorException
    {
        final String mName = "ReferenceColumn::appendCell(): ";
        ReferenceCell rc = null;

        if ( this.itsCells == null )
        {
            throw new SystemErrorException(mName +
                                           "itsCells not initialized?!?");
        }

        if ( ! this.validCell(newCell))
        {
            throw new SystemErrorException(mName + "invalid cell");
        }

        // make a copy of the cell for insertion
        rc = new ReferenceCell(newCell);

        this.itsCells.add(rc);
        this.numCells = this.itsCells.size();
        rc.setOrd(this.numCells);

        if ( itsCells.elementAt(rc.getOrd() - 1) != rc )
        {
            throw new SystemErrorException(mName + "bad ord for newCell?!?");
        }

        return;

    } /* ReferenceColumn::appendCell(newCell) */


    // getCell()
    /**
     * Gets the cell at the specified ord. Note that this function returns
     * the actual cell -- not a copy.  For almost all purposes, the returned
     * cell should be treated as read only.
     *
     * @param ord The ordinal position of the cell to get from the database.
     *
     * @return A reference to the actual cell (not a copy) at the nominated
     * ordinal position.
     *
     * @throws SystemErrorException If unable to retrieve the nominated cell
     * from the column.
     *
     * @date 2007/08/30
     */

    protected ReferenceCell getCell(int ord)
        throws SystemErrorException
    {
        final String mName = "ReferenceColumn::getCell(): ";
        ReferenceCell retVal = null;

        if ( ( ord < 1 ) || ( ord > this.numCells ) )
        {
            throw new SystemErrorException(mName + "ord out of range");
        }

        if ( this.itsCells == null )
        {
            throw new SystemErrorException(mName +
                                           "itsCells not initialized?!?");
        }

        retVal = this.itsCells.get(ord - 1);

        if ( retVal.getOrd() != ord )
        {
            throw new SystemErrorException(mName + "unexpected ord");
        }

        return retVal;

    } /* ReferenceColumn::getCell() */


    // getCellCopy()
    /**
     * Gets a copy of the cell at the nominated ordinal location.
     *
     * @param ord The ordinal value of the cell to get a copy of.
     *
     * @return A copy of the cell at the specified ordinal value.
     *
     * @date 2007/08/30
     */

    protected ReferenceCell getCellCopy(int ord)
        throws SystemErrorException
    {

        return new ReferenceCell(this.getCell(ord));

    } /* ReferenceColumn::getCell() */


    // insertCell()
    /**
     * Insert the supplied ReferenceCell in the indicated location in the vector
     * of ReferenceCells. Update the ords of the cells after the insertion
     * point.
     *
     * @param newCell The new cell to insert into the reference column
     * @param ord The location to insert the new cell (ordinal value).
     *
     * @date 2007/08/30
     */

    protected void insertCell(ReferenceCell newCell,
                              int ord)
       throws SystemErrorException
    {
        final String mName = "ReferenceColumn::insertCell(): ";
        int i;
        ReferenceCell rc = null;

        if ( ( ord < 1 ) || ( ( ord > this.numCells ) && ( ord != 1 ) ) )
        {
            throw new SystemErrorException(mName + "ord out of range");
        }

        if ( ! this.validCell(newCell))
        {
            throw new SystemErrorException(mName + "invalid cell");
        }

        if ( this.itsCells == null )
        {
            throw new SystemErrorException(mName +
                                           "itsCells not initialized?!?");
        }

        // make a copy of the cell for insertion & set its ord
        rc = new ReferenceCell(newCell);
        rc.setOrd(ord);

        // insert the cell & update numCells
        this.itsCells.insertElementAt(rc, (ord - 1));
        this.numCells = this.itsCells.size();

        // verify ord of new cell
        if ( itsCells.get(rc.getOrd() - 1) != rc )
        {
            throw new SystemErrorException(mName + "bad ord for newCell?!?");
        }

        // Update ords for insertion
        for ( i = ord; i < this.numCells; i++ )
        {
            rc = itsCells.elementAt(i);

            if ( rc.getOrd() != i )
            {
                throw new SystemErrorException(mName + "unexpected old ord" + i);
            }

            /* update the ord */
            rc.setOrd(i + 1);
        }
    } /* ReferenceColumn::insertCell(newCell, ord) */


    // removeCell()
    /**
     * Remove the cell indicated by the supplied ord from itsCells.  As a
     * sanity check, verify that the target cell has the indicated ID.
     * After the removal, update the ords of the remaining cells.
     *
     * @param targetOrd The ordinal value of the reference cell to be removed.
     * @param targetID The id of the reference cell to be removed.
     *
     * @return A reference to the ReferenceCell removed from itsCells.
     *
     * @date 2007/08/30
     */

    protected ReferenceCell removeCell(int targetOrd,
                                       long targetID)
        throws SystemErrorException
    {
        final String mName = "ReferenceColumn::removeCell(): ";
        int i;
        ReferenceCell rc = null;
        ReferenceCell retVal = null;


        if ( ( targetOrd < 1 ) || ( targetOrd > this.numCells ) )
        {
            throw new SystemErrorException(mName + "targetOrd out of range");
        }

        if ( this.itsCells == null )
        {
            throw new SystemErrorException(mName +
                                           "itsCells not initialized?!?");
        }

        rc = itsCells.elementAt(targetOrd - 1);

        if ( rc == null )
        {
            throw new SystemErrorException(mName + "can't get target cell");
        }

        if ( rc.getID() != targetID )
        {
            throw new SystemErrorException(mName + "target ID mismatch");
        }

        if ( rc != this.itsCells.remove(targetOrd -1) )
        {
            throw new SystemErrorException(mName + "remove failed?!?!");
        }

        retVal = rc;

        this.numCells = this.itsCells.size();

        for ( i = targetOrd - 1; i < this.numCells; i++)
        {
            rc = this.itsCells.get(i);

            if ( rc == null )
            {
                throw new SystemErrorException(mName + "can't get cell" + i);
            }

            if ( rc.getOrd() != i )
            {
                throw new SystemErrorException(mName +
                        "unexpected cell ord " + i);
            }

            rc.setOrd(i + 1);
        }

        return retVal;

    } /* ReferenceColumn::removeCell */


    // replaceCell()
    /**
     * Replace the ReferenceCell at targetOrd in this.itsCells with the supplied
     * ReferenceCell.
     *
     * @param newCell The new cell to use at the supplied targetOrd.
     * @param targetOrd The destination ordinal location for the new cell.
     *
     * @return The old ReferenceCell.
     *
     * @date 2007/08/30
     */
    protected ReferenceCell replaceCell(ReferenceCell newCell,
                                        int targetOrd)
        throws SystemErrorException
    {
        final String mName = "ReferenceColumn::removeCell(): ";
        int i;
        ReferenceCell rc = null;
        ReferenceCell retVal = null;

        if ( ( targetOrd < 1 ) || ( targetOrd > this.numCells ) )
        {
            throw new SystemErrorException(mName + "targetOrd out of range");
        }

        if ( ! this.validCell(newCell))
        {
            throw new SystemErrorException(mName + "invalid cell");
        }

        if ( this.itsCells == null )
        {
            throw new SystemErrorException(mName +
                                           "itsCells not initialized?!?");
        }

        newCell.setOrd(targetOrd);

        rc = this.itsCells.get(targetOrd);

        if ( rc == null )
        {
            throw new SystemErrorException(mName + "can't get old cell.");
        }

        retVal = this.itsCells.set(targetOrd - 1, newCell);

        if ( retVal != rc )
        {
            throw new SystemErrorException(mName +
                                           "unexpected return from set()");
        }

        // verify ord of new cell
        if ( itsCells.get(newCell.getOrd() - 1) != newCell )
        {
            throw new SystemErrorException(mName + "bad ord for newCell?!?");
        }

        return retVal;

    } /* ReferenceColumn::replaceCell() */


    // sortItsCells()
    /**
     * Sort itsCells by cell onset.
     *
     * @throws SystemErrorException Always - currently not implemented.
     *
     * @date 2008/03/20
     */
    protected void sortItsCells()
        throws SystemErrorException
    {
        final String mName = "ReferenceColumn::sortItsCells(): ";

        throw new SystemErrorException(mName + "method not implemented");

    } /* ReferenceColumn::sortItsCells() */


    // validCell()
    /**
     * Verify that a cell has been correctly initialized for insertion into
     * itsCells.  Return true if it has been, and false otherwise.
     *
     * @param cell The cell to determine if it has been correctly initialized
     * for insertion.
     *
     * @return true If the cell is valid, false otherwise.
     *
     * @throws SystemErrorException If unable to determine if the cell is valid.
     *
     * @date 2007/08/30
     */
    private boolean validCell(ReferenceCell cell)
        throws SystemErrorException
    {
        final String mName = "ReferenceColumn::validCell(): ";
        long targetID;
        DBElement dbe;

        if ( cell == null )
        {
            throw new SystemErrorException(mName + "cell null on entry.");
        }

        if ( cell.getDB() != this.getDB() )
        {
            return false;
        }

        if ( cell.getItsColID() != this.getID() )
        {
            return false;
        }

        targetID = cell.getTargetID();

        if ( targetID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "cells target ID invalid");
        }

        dbe = this.getDB().idx.getElement(targetID);

        if ( dbe == null )
        {
            throw new SystemErrorException(mName +
                                           "cells target ID has no referent");
        }

        if ( ! ( dbe instanceof ReferenceCell ) )
        {
            throw new SystemErrorException(mName +
                    "cells target ID doesn't refer to a ReferenceCell");
        }

        // other sanity checks needed?

        return true;

    } /* ReferenceColumn::validCell() */


    // hashCode()
    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode() * Constants.SEED1;
        hash += HashUtils.Obj2H(itsCells) * Constants.SEED2;

        return hash;
    }


    // equals()
    /**
     * Compares this reference column against a object.
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

        ReferenceColumn rc = (ReferenceColumn) obj;
        return super.equals(obj)
               && (itsCells == null ? rc.itsCells == null
                                    : itsCells.equals(rc.itsCells));
    }
} /* Class ReferenceColumn */
