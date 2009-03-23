/*
 * Cell.java
 *
 * Created on December 7, 2006, 3:15 PM
 *
 */

package au.com.nicta.openshapa.db;

import au.com.nicta.openshapa.util.Constants;
import au.com.nicta.openshapa.util.HashUtils;

/**
 * Class Cell
 *
 * Abstract class for OpenSHAPA data and reference cells.  Data and reference
 * cells don't have all that much in common, so this class is a bit sparse.
 *
 *                                                  JRM -- 8/24/07j
 *
 *
 * @author FGA
 */
public abstract class Cell extends DBElement
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * itsColID: ID of the instance of Column within which the cell resides.
     *
     * comment: String intended to allow the user to attach a comment to the
     *      cell.
     *
     * ord: Number of this cell in its host column.  This number should
     *      be 1 + the index of the cell in the column's Vector of cells.
     *
     * selected:  Boolean flag indicating whether the cell is currently
     *      selected.
     */

    /** ID of the column within which the cell resides. */
    protected long itsColID = DBIndex.INVALID_ID;

    /** arbitrary comment associated with the cell */
    protected String comment = null;

    /** ord of cell */
    int ord = -1; /* a convenient invalid value */

    /** whether the cell is selected */
    boolean selected = false;

//    /* TODO -- revisit this field and the associated code */
//    /**
//     * CellChange listener list
//     */
//    Vector<CellChangeListener> cellListeners = new Vector<CellChangeListener>();


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * Cell()
     *
     * Constructor for instances of Cell.
     *
     * Only three versions of this constructor, as the management of itsColID
     * varies between the subclasses.
     *
     * The first takes only a reference to a database as its parameter and
     * constructs an undefined instance of Cell (that is, an instance
     * that is not yet associated with some column).
     *
     * The second takes a reference to a database, and a comment.  The comment
     * is copied, and the comment field is used to refer to the copy.
     *
     *  The third takes and instance of Cell as its parameter, and returns
     *  a copy.
     *
     *  Observe that none of the constructor accept a column ID, as this is
     *  best handled by the subclasses.
     *
     *                                              JRM -- 8/24/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public Cell(Database db)
        throws SystemErrorException
    {

        super(db);

    } /* Cell::Cell(db) */


    public Cell(Database db,
                String comment)
        throws SystemErrorException
    {
        super(db);

        if ( comment != null )
        {
            this.comment = new String(comment);
        }
        else
        {
            comment = null;
        }
    } /* Cell::Cell(db, comment) */

    public Cell(Cell c)
        throws SystemErrorException
    {
        super((DBElement)c);

        this.itsColID = c.itsColID;

        this.ord = c.ord;

        this.selected = c.selected;

        if ( c.comment != null )
        {
            this.comment = new String(c.comment);
        }
    } /* Cell::Cell(c) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getItsColID()
     *
     * Return the current value of the itsColID field.
     *
     *                                          JRM -- 8/29/07
     *
     * Changes:
     *
     *    - none.
     */

    public long getItsColID()
    {

        return this.itsColID;

    } /* Cell::getItsColID() */


    /**
     * getComment() & setComment()
     *
     * Get and set the comment field.  Observe that a copy is made in both
     * cases.  In both cases,
     *                                          JRM -- 8/24/07
     *
     * Changes:
     *
     *    - None.
     */

    public String getComment()
    {
        if ( this.comment != null )
        {
            return new String(this.comment);
        }
        else
        {
            return null;
        }
    } /* Cell::getComment() */

    public void setComment(String comment)
    {
        if ( comment != null )
        {
            this.comment = new String(comment);
        }
        else
        {
            this.comment = null;
        }

        return;

    } /* /* Cell::getComment() */


    /**
     * getOrd() & setOrd()
     *
     * Get and set the ord of the cell.
     *
     *                      JRM -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */

    public int getOrd()
    {

        return this.ord;

    } /* Cell::getOrd() */

    public void setOrd(int newOrd)
        throws SystemErrorException
    {
        final String mName = "Cell::setOrd(newOrd): ";

        if ( newOrd < 1 )
        {
            throw new SystemErrorException(mName + "newOrd < 1");
        }

        this.ord = newOrd;

        return;

    } /* Cell::setOrd(newOrd) */


    /**
     * getSelected() & setSelected()
     *
     * Get and set the value of the selected field.
     *
     *                              JRM -- 2/8/08
     *
     * Changes:
     *
     *    - None.
     */

    public boolean getSelected()
    {

        return this.selected;

    } /* Cell::getSelected() */

    public void setSelected(boolean selected)
    {

        this.selected = selected;

        return;

    } /* Cell::setSelected() */

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += this.itsColID * Constants.SEED1;
        hash += HashUtils.Obj2H(this.comment) * Constants.SEED2;
        hash += this.ord * Constants.SEED3;
        hash += new Boolean(this.selected).hashCode() * Constants.SEED4;

        return hash;
    }

    /**
     * Compares this Cell against another object.
     *
     * @param obj The object to compare this against.
     *
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

        Cell c = (Cell) obj;
        return super.equals(c)
            && (this.itsColID == c.itsColID)
            && (this.comment == null ? c.comment == null
                                     : this.comment.equals(c.comment))
            && (this.ord == c.ord)
            && (this.selected == c.selected);
    }



//    /**
//     * Adds a cell listener reference to cell
//     * @param cellListener The cell listener to add
//     */
//    public void addCellChangeListener(CellChangeListener cellListener)
//    {
//      if (this.cellListeners != null) {
//        this.cellListeners.add(cellListener);
//      }
//    } //End of addCellChangeListener() method
//
//    /**
//     * Removes a cell listener reference from a cell
//     */
//    public void removeCellChangeListener(CellChangeListener cellListener)
//    {
//    if (this.cellListeners != null) {
//        this.cellListeners.remove(cellListener);
//      }
//    } //End of removeCellChangeListener() method

} /* class Cell */
