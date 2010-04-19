package org.openshapa.models.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;

/**
 * Abstract class for OpenSHAPA data and reference cells.  Data and reference
 * cells don't have all that much in common, so this class is a bit sparse.
 *
 * @date 2007/08/24
 */
public abstract class Cell extends DBElement {

    /** ID of the instance column within which the cell resides. */
    protected long itsColID = DBIndex.INVALID_ID;

    /** arbitrary comment associated with the cell. */
    protected String comment = null;

    /** Number of this cell in its host column. This number should be 1 + the
     *  index of the cell in the column's vector of cells. It is set to -1
     *  initially as it is an invalid value. */
    int ord = -1;

    /** Flag indicating whether the cell is currently selected. */
    boolean selected = false;

//    /* TODO -- revisit this field and the associated code */
//    /**
//     * CellChange listener list
//     */
//    Vector<CellChangeListener> cellListeners = new Vector<CellChangeListener>();

    /**
     * Creates an undefined instance of the cell (that is an instance that is
     * not yet assocated with some column).
     *
     * Does not accept a column ID - this is best handled by the subclasses.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param db The parent database that this cell will reside within.
     *
     * @throws org.openshapa.db.SystemErrorException When unable to create the
     * cell from the supplied database.
     *
     * @date 2007/08/24
     */
    public Cell(Database db) throws SystemErrorException {
        super(db);
    }

    /**
     * Creates an undefined instance of the cell (that is an instance that is
     * not yet assocaited with some column).
     *
     * Does not accept a column ID - this is best handled by the subclasses.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param db The parent database that this cell will reside within.
     * @param comment The comment that is copied, and the comment field is used
     * to refer to the copy.
     *
     * @throws org.openshapa.db.SystemErrorException When unable to create the
     * cell from the supplied arguments.
     *
     * @date 2007/08/24
     */
    public Cell(Database db, String comment) throws SystemErrorException {
        super(db);

        if ( comment != null )
        {
            this.comment = new String(comment);
        }
        else
        {
            comment = null;
        }
    }

    /**
     * Copy constructor. Creates a new cell from the supplied cell.
     *
     * Does not accept a column ID - this is best handled by the subclasses.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param c The cell to
     * @throws org.openshapa.db.SystemErrorException When unable to create the
     * cell from the supplied arguments.
     *
     * @date 2007/08/24
     */
    public Cell(Cell c) throws SystemErrorException {
        super((DBElement)c);

        this.itsColID = c.itsColID;

        this.ord = c.ord;

        this.selected = c.selected;

        if ( c.comment != null )
        {
            this.comment = new String(c.comment);
        }
    }

    /**
     * @return The ID of the parent column that this cell resides within.
     *
     * @date 2007/08/29
     */
    public long getItsColID()
    {

        return this.itsColID;

    }

    /**
     * @return A copy of the comment that is currently attached to the cell.
     *
     * @date 2007/08/24
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
    }

    /**
     * Attaches a new comment to the cell. Note it attaches a copy of the
     * comment, not the original reference.
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param comment The new comment to attach to the cell.
     *
     * @date 2007/08/24
     */
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

    }

    /**
     * @return The ordinal value of the cell (i.e. 1 + the index within the
     * cell's parent column).
     *
     * @date 2007/08/29
     */
    public int getOrd()
    {

        return this.ord;

    }

    /**
     * Sets the ordinal value of the cell (i.e. 1 + the index within the cell's
     * parent column).
     *
     * Changes:
     * <ul>
     *   <li>None.</li>
     * </ul>
     *
     * @param newOrd The new ordinal value to use for the cell.
     *
     * @throws org.openshapa.db.SystemErrorException If the supplied newOrd
     * is not a valid value (less than 1).
     *
     * @date 2007/08/29
     */
    public void setOrd(int newOrd) throws SystemErrorException {
        final String mName = "Cell::setOrd(newOrd): ";

        if ( newOrd < 1 )
        {
            throw new SystemErrorException(mName + "newOrd < 1");
        }

        this.ord = newOrd;

        return;

    }

    /**
     * @return A boolean flag - is the cell selected (true) or not (false).
     *
     * @date 2008/08/02
     */
    public boolean getSelected()
    {

        return this.selected;

    }

    /**
     * Sets the selected state of the cell.
     *
     * @param selected The new selected state of the cell, true if selected,
     * false otherwise.
     *
     * @date 2008/08/02
     */
    public void setSelected(boolean selected)
    {

        this.selected = selected;

        return;

    }

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

//    TODO -- Revisit code.
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
