/*
 * ReferenceColumn.java
 *
 * Created on December 14, 2006, 7:06 PM
 *
 */

package org.openshapa.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;
import java.util.Vector;

/**
 * Class ReferenceColumn
 *
 * Instances of ReferenceColumn are used to implement spreadsheet variables
 * whose cells are mirrors of other cells in the database.
 *
 *                                               -- 8/30/07
 *
 * @author FGA
 */
public class ReferenceColumn extends Column
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * itsCells: Reference to a vector of ReferenceCell containing the cells
     *      of the column.  This Vector is created when the column is inserted
     *      into the column list, and not copied in the copy constructor.
     */

    /** Vector of ReferenceCells for Column */
    private Vector<ReferenceCell> itsCells = null;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * ReferenceColumn()
     *
     * Constructors for instances of ReferenceColumn.
     *
     * Three versions of this constructor.
     *
     * The first takes only a reference to a database, and a name
     * as its parameters.  This is the constructor that will typically be
     * used when a new column is created in the spreadsheet.
     *
     * The second takes a reference to a database, a name, and initial values
     * for the  hidden and readOnly field.  This constructor is intended for
     * use when loading a Database from file.
     *
     *  The third takes an instance of ReferenceColum as its parameter, and returns
     *  a copy.  Note that the itsCells field is NOT copied.
     *
     *                                               -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public ReferenceColumn(Database db,
                           String name)
        throws SystemErrorException, LogicErrorException
    {
        super(db);

        final String mName = "ReferenceColumn::ReferenceColumn(db, name): ";

        this.setName(name);

    } /* ReferenceColumn::ReferenceColumn(db, name, type) */

    public ReferenceColumn(Database db,
                      String name,
                      boolean hidden,
                      boolean readOnly)
        throws SystemErrorException, LogicErrorException
    {
        super(db, name, hidden, readOnly);

    } /* ReferenceColumn::ReferenceColumn(db, name, hidden, readOnly) */

    public ReferenceColumn(ReferenceColumn rc)
        throws SystemErrorException
    {

        super((Column)rc);

    } /* ReferenceColumn::ReferenceColumn(rc) */

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


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getItsCells() & setItsCells()
     *
     * Get and set the current value of itsCells.  Note that these methods
     * are protected and should only be called from within the openshapa.db
     * package.  We will use them to transfer the vector of cells from one
     * incarnation of the ReferencesColumn header to the next.
     *
     * Update numCells in passing.
     *
     *                                               -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */

    protected Vector<ReferenceCell> getItsCells()
    {

        return this.itsCells;

    } /* ReferenceColumn::getItsCells() */

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


    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/

   /**
     * toDBString()
     *
     * Returns a String representation of the ReferenceColumn for comparison
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


    /**
     * toString()
     *
     * Returns a String representation of the ReferenceColumn for display.
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
            s = "(" + this.getName()  + ", " + this.itsCellsToString() + ")";
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return (s);

    } /* ReferenceColumn::toString() */



    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/

    /**
     * constructItsCells()
     *
     * Allocate the Vector of ReferenceCell used to store cells.  This method
     * should only be called when the ReferenceColumn is being inserted in the
     * column list.
     *
     *                                           -- 8/30/07
     *
     * Changes:
     *
     *    - None.
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


    /**
     * itsCellsToDBString()
     *
     * Construct a string containing the values of the cells in a
     * format that displays the full status of the arguments and
     * facilitates debugging.
     *                                           -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     *
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


    /**
     * itsCellsToString()
     *
     * Construct a string containing the values of the cells in the column.
     *
     *                                           -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     *
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


    /*************************************************************************/
    /************************* Cells Management: *****************************/
    /*************************************************************************/

    /**
     * appendCell()
     *
     * Append the supplied DataCell to the end of the vector of cells.
     *
     *                                           -- 8/30/07
     *
     * Changes:
     *
     *    - None.
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


    /**
     * getCell()
     *
     * Get the cell at the specified ord.  Note that this function returns
     * the actual cell -- not a copy.  For almost all purposes, the returned
     * cell should be treated as read only.
     *
     *                                               -- 8/30/07
     *
     * Changes:
     *
     *    - None.
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


    /**
     * getCellCopy()
     *
     * Return a copy of the cell at the specified ord.
     *
     *                                           -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */

    protected ReferenceCell getCellCopy(int ord)
        throws SystemErrorException
    {

        return new ReferenceCell(this.getCell(ord));

    } /* ReferenceColumn::getCell() */


    /**
     * insertCell()
     *
     * Insert the supplied ReferenceCell in the indicated location in the vector
     * of ReferenceCells.  Update the ords of the cells after the insertion point.
     *
     *                                               -- 8/30/07
     *
     * Changes:
     *
     *    - None.
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


    /**
     * removeCell()
     *
     * Remove the cell indicated by the supplied ord from itsCells.  As a
     * sanity check, verify that the target cell has the indicated ID.
     * After the removal, update the ords of the remaining cells.
     *
     * Return a reference to the ReferenceCell removed from itsCells.
     *
     *                                       -- 8/30/07
     *
     * Changes:
     *
     *    - None.
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


    /**
     * replaceCell()
     *
     * Replace the ReferenceCell at targetOrd in this.itsCells with the supplied
     * ReferenceCell.  Return the old ReferenceCell.
     *                                               -- 8/30/07
     *
     * Changes:
     *
     *    - None.
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


    /**
     * sortItsCells()
     *
     *
     * Sort itsCells by cell onset.
     *                                               -- 3/20/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void sortItsCells()
        throws SystemErrorException
    {
        final String mName = "ReferenceColumn::sortItsCells(): ";

        throw new SystemErrorException(mName + "method not implemented");

    } /* ReferenceColumn::sortItsCells() */


    /**
     * validCell()
     *
     * Verify that a cell has been correctly initialized for insertion into
     * itsCells.  Return true if it has been, and false otherwise.
     *
     *                                               -- 8/30/07
     *
     * Changes:
     *
     *    - None.
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

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode() * Constants.SEED1;
        hash += HashUtils.Obj2H(itsCells) * Constants.SEED2;

        return hash;
    }

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
