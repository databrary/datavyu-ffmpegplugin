/*
 * DataCell.java
 *
 * Created on December 7, 2006, 5:25 PM
 *
 */

package org.openshapa.models.db.legacy;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;

/**
 * Class DataCell
 *
 * Instances of this class implement MacSHAPA style cells.
 *
 * Warning:  The implementation of the Comparable interface exists to allow
 *           sorting of cells by onset.  Thus, if compare(cell1, cell2) == 0,
 *           this only implies that they have identical onsets -- not that
 *           they are in any way identical.
 *
 *                                          -- 8/24/07
 * Regular cell definition
 */
public class DataCell extends Cell // implements DatabaseChangeListener, DataValueChangeListener
{
//    /****************************** Constant decarations ************************/
//    public final static int DB_TYPE_INTEGER   = 1;
//    public final static int DB_TYPE_FLOAT     = 2;
//    public final static int DB_TYPE_STRING    = 3;
//    public final static int DB_TYPE_NOMINAL   = 4;
//    public final static int DB_TYPE_MATRIX    = 5;
//    public final static int DB_TYPE_PREDICATE = 6;
//    public final static int DB_TYPE_TIMESTAMP = 7;

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * itsMveID:   Long containing the ID of the matrix vocab element
     *      that defines the format of the cell.
     *
     * itsMveType: matrixType indicating the type of the associated
     *      matrix vocab element.
     *
     * onset: TimeStamp containing the onset of the cell.
     *
     * offset: TimeStamp containing the offset of the cell.
     *
     * val: Instance of Matrix containing the values of the formal arguments
     *      of the MatrixVocabElement as specified for this cell.
     *
     * listeners: Instance of DataCellListeners containing references to
     *      internal and external objects that must be notified when the
     *      data cell is modified.
     *
     * Management of cascades of changes:
     *
     * The nature of the OpenSHAPA database is such that a change in one
     * place can cause a cascade of changes to ripple through the database.
     * The beginning and end of such cascades are marked by the beginCascade()
     * endCascade() messages required by the internalCascadeListener interface.
     *
     * Since a database can contain a huge number of cells, and the vast
     * majority will not participate in any single cascade, we don't make
     * data cells internalCascadeListeners.  Instead they are recruited
     * into the cascade as necessary by their host DataColumns and/or
     * constituent data values.
     *
     * It is possible that a given cell will be modified several times in
     * a given cascade.  This is a bit of a problem, as we want to minimize
     * the number of messages flying about in a cascade, and we also want
     * to minimize the number of verisons of a cell that appear in the undo
     * list for a single operation.  To this end, when a cell is recruited
     * into a cascade, it makes a copy of itself, and applies all changes
     * to the copy.  Then, at the end of the cascade, the modified copy
     * replaces the old canonical incarnation of the cell as the new cannonical
     * incarnation.
     *
     * The one exception to this rule is changes to cell ord only.  As cell
     * ord is usually computed and assigned by the database, we will not make
     * an undo list entry when it changes.  Thus, the cell will not create a
     * copy of itself if only the ord is changed.
     *
     * inCascade:  Boolean flag that is used to note whether the cell thinks
     *      it has been recruited into a cascade.  This field should only be
     *      true if the following statements are all true as well:
     *
     *      1) The cell is the current cannonical incarnation.
     *
     *      2) A cascade of changes is in progress.
     *
     *      3) The cell has been modified in the current cascade.
     *
     * oldOrd:  Int used to store the original ord of the cell if only the
     *      cells ord is changed during a cascade.  In all other cases, the
     *      field should be set to 0.
     *
     * pending: Copy (with modifications) of the current incarnation of the
     *      data cell which is used to accumulate all modifications to the
     *      current incarnation during a cascade of changes.  At the end
     *      of the cascade, the pending version replaces the host data cell,
     *      becoming the next incarnation of the cell.
     *
     *      This field must always be null in any instance of DataCell that
     *      is not the current incarnation.  Further, it should only be
     *      non-null during a cascade of changes
     *
     * cascadeMveMod:  Boolean flag indicating whether the cascade was entered
     *      to update the cell value for a change in the definition of the
     *      mve defining the column in which the cell resides, and/or possibly
     *      for a change in an mve which implies a column predicate that appears
     *      in the cells value.
     *
     * cascadeMveID: If the DataCell has entered a cascade to update for
     *      changes to or deletion of a matrixvocab element, the ID of
     *      the mve is stored here, so that we will allow multiple calls
     *      for the update.
     *
     *      This field should be set to  DBIndex.INVALID_ID except when part
     *      of a cascade triggered by modificaiton/deletion of a mve.
     *
     * cascadeMveDel: Boolean flag indicating whether the cascade was entered
     *      to update the cell value for the deletion of a mve from the vocab
     *      list.  In all other cases, this field should be set to false.
     *
     * cascadePveMod:  Boolean flag indicating whether the cascade was entered
     *      to update the cell value for a change in the definition of a pve.
     *      In all other cases, the field should be set to false.
     *
     * cascadePveID:  If the DataCell has entered a cascade to update for
     *      changes to or deletion of a predicate vocab element, the ID of
     *      the pve is stored here, so that we will allow multiple calls
     *      for the update.
     *
     *      This field should be set to DBIndex.INVALID_ID except when part
     *      of a cascade triggered by modificaiton/deletion of a pve.
     *
     * cascadePveDel: Boolean flag indicating whetehr the cascade was entered
     *      to update the cell value for the deletion of a pve from the vocab
     *      list.  In all other cases, this field should be set to false.
     */

    /** ID of associated matrix VE */
    protected long itsMveID = DBIndex.INVALID_ID;

    /** Type of associated matrix VE */
    protected MatrixVocabElement.MatrixType itsMveType =
            MatrixVocabElement.MatrixType.UNDEFINED;

    /** onset of cell */
    protected TimeStamp onset = null;

    /** offset of cell */
    protected TimeStamp offset = null;

    /** value of cell */
    Matrix val = null;

    /**
     * reference to instance of DataCellListeners used to maintain lists of
     * listeners, and notify them as appropriate.
     */
    protected DataCellListeners listeners = null;

    /**
     * boolean flag indicating whether the data cell is participating in a
     * cascade of changes.
     */
    private boolean inCascade = false;

    /**
     * Integer field used to store the old value of the ord if the ord (and
     * only the ord) is changed during a cascade.  In all other cases it should
     * be 0.
     */
    private int oldOrd = 0;

    /**
     * Copy of the cannonical version of the cell used to accumulate changes
     * during a cascade.  null under all other circumstances.
     */
    private DataCell pending = null;

    /**
     * If in a cascade triggered by a modification to the formal argument
     * list of the matrix vocab element defining the structure of cells in
     * the host data column, this field should be set to true.  Should be
     * set to false under all other circumstances.
     */

    private boolean cascadeMveMod = false;

    /**
     * If in a cascade triggered by a modification to the definition of a
     * matrix vocab element, this field will contain the ID of the mve.
     * Should be set to DBIndex.INVALID_ID at all other times.
     */
    private long cascadeMveID = DBIndex.INVALID_ID;

    /**
     * if in a cascade triggered by the deletion of a matrix vocab element,
     * this field should be set of true.  Should be set to false under all
     * other circumstances.
     */
    private boolean cascadeMveDel = false;

    /**
     * If in a cascade triggered by a modification to the definiton of a
     * predicate vocab element, this field should be set to true.  Should be
     * set to false under all other circumstances.
     */
    private boolean cascadePveMod = false;

    /**
     * If in a cascade triggered by a modification to the definition of a
     * predicate vocab element, this field will contain the ID of the pve.
     * Should be set to DBIndex.INVALID_ID at all other times.
     */
    private long cascadePveID = DBIndex.INVALID_ID;

    /**
     * if in a cascade triggered by the deletion of a predicate vocab element,
     * this field should be set of true.  Should be set to false under all
     * other circumstances.
     */
    private boolean cascadePveDel = false;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * DataCell()
     *
     * Constructor for instances of DataCell.
     *
     * Five versions of this constructor.
     *
     * The first takes only a reference to a database, a column, and the
     * column's associate MatrixVocabElement as parameters, and constructs
     * an uninitialized DataCell suitable for insertion into the target
     * column.
     *
     * The second is the same as the first, with the addition of a comment
     * string parameter, that is passed on to the superclass constructor.
     *
     * The third takes all the parameters of the second, with the addition
     * of values for the onset, offset, and val fields.
     *
     * The fourth takes and instance of DataCell as its parameter, and returns
     * a copy.
     *
     * The fifth is much like the fourth, save that it takes the additional
     * blindCopy parameter.  If this parameter is true, the argument list
     * is copied blindly -- without sanity checks against the underlying mve,
     * or pve's of any predicates that may appear in it.  This is necessary
     * when a mve or pve has changes, and we need a copy of the cell to
     * modify into conformance with the modified mve or pve.
     *
     *                                              -- 8/24/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public DataCell(Database db,
                    long colID,
                    long mveID)
        throws SystemErrorException
    {

        super(db);

        final String mName = "DataCell::DataCell(db, colID, mveID): ";
        DataColumn col;
        MatrixVocabElement mve;

        // lookup the colID and me mveID.  We need the mve to do our
        // initialization, but we lookup both to verify the input.
        // If either lookup fails, we will throw a system error.

        col = this.lookupDataCol(colID);
        mve = this.lookupMatrixVE(mveID);

        if ( col.getItsMveID() != mveID )
        {
            throw new SystemErrorException(mName + "col.getItsMveID() != mveID");
        }

        /* TODO -- add sanity checking here */
        this.itsColID = colID;
        this.itsMveID = mveID;

        this.onset = new TimeStamp(db.getTicks(), 0);
        this.offset = new TimeStamp(db.getTicks(), 0);

        this.itsMveType = mve.getType();
        this.val = new Matrix(db, mveID);

    } /* DataCell::DataCell(db, colID, mveID) */


    public DataCell(Database db,
                    String comment,
                    long colID,
                    long mveID)
        throws SystemErrorException
    {

        super(db, comment);

        final String mName = "DataCell::DataCell(db, comment, colID, mveID): ";
        DataColumn col;
        MatrixVocabElement mve;

        // lookup the colID and me mveID.  We need the mve to do our
        // initialization, but we lookup both to verify the input.
        // If either lookup fails, we will throw a system error.

        col = this.lookupDataCol(colID);
        mve = this.lookupMatrixVE(mveID);

        if ( col.getItsMveID() != mveID )
        {
            throw new SystemErrorException(mName + "col.getItsMveID() != mveID");
        }

        this.itsColID = colID;
        this.itsMveID = mveID;

        this.onset = new TimeStamp(db.getTicks(), 0);
        this.offset = new TimeStamp(db.getTicks(), 0);

        this.itsMveType = mve.getType();
        this.val = new Matrix(db, mveID);

    } /* DataCell::DataCell(db, comment, colID, mveID) */


    public DataCell(Database db,
                    String comment,
                    long colID,
                    long mveID,
                    TimeStamp onset,
                    TimeStamp offset,
                    Matrix val)
        throws SystemErrorException
    {

        super(db, comment);

        final String mName = "DataCell::DataCell(db, comment, colID, mveID, " +
                                                 "onset, offset, val): ";
        DataColumn col;
        MatrixVocabElement mve;

        // lookup the colID and me mveID.  We need the mve to do our
        // initialization, but we lookup both to verify the input.
        // If either lookup fails, we will throw a system error.

        col = this.lookupDataCol(colID);
        mve = this.lookupMatrixVE(mveID);

        if ( col.getItsMveID() != mveID )
        {
            throw new SystemErrorException(mName + "col.getItsMveID() != mveID");
        }

        if ( ( onset == null ) || ( offset == null ) )
        {
            throw new SystemErrorException(mName + "null onset and/or offset.");
        }

        if ( val == null )
        {
            throw new SystemErrorException(mName + "val null on entry");
        }

        if ( val.getMveID() != mveID )
        {
            throw new SystemErrorException(mName + "mveID mismatch in val");
        }

        this.itsColID = colID;
        this.itsMveID = mveID;

        this.onset = new TimeStamp(onset);
        this.offset = new TimeStamp(offset);

        // It is possible that the onset and offset are coming from another
        // database, possibly with a different tps -- thus set the
        // tps to match that of the current data base.  This will almost
        // always be redundant, but better safe then sorry.
        this.onset.setTPS(db.getTicks());
        this.offset.setTPS(db.getTicks());

        this.itsMveType = mve.getType();

        this.val = new Matrix(val);

    } /* DataCell::DataCell(db, comment, colID, mveID, onset, offset, val) */


    public DataCell(DataCell dc)
        throws SystemErrorException
    {
        super((Cell)dc);

        this.itsColID = dc.itsColID;
        this.itsMveID = dc.itsMveID;
        this.itsMveType = dc.itsMveType;
        this.onset = new TimeStamp(dc.onset);
        this.offset = new TimeStamp(dc.offset);
        this.val = new Matrix(dc.val);

    } /* DataCell::DataCell(dc) */


    public DataCell(DataCell dc,
                    boolean blindCopy)
        throws SystemErrorException
    {
        super((Cell)dc);

        this.itsColID = dc.itsColID;
        this.itsMveID = dc.itsMveID;
        this.itsMveType = dc.itsMveType;
        this.onset = new TimeStamp(dc.onset);
        this.offset = new TimeStamp(dc.offset);
        this.val = new Matrix(dc.val, blindCopy);

    } /* DataCell::DataCell(dc) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getItsMveID
     *
     * Return the current value of the itsMveID field.
     *
     *                                      -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */

    public long getItsMveID()
    {

        return this.itsMveID;

    } /* DataCell::getItsMveID() */


    /**
     * getItsMveType
     *
     * Return the current value of the itsMveType field.
     *
     *                                      -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */

    public MatrixVocabElement.MatrixType getItsMveType()
    {

        return this.itsMveType;

    } /* DataCell::getItsMveType() */


    /**
     * getOffset() & setOffset()
     *
     * Get and set the value of the offset field.
     *
     *                                      -- 8/29/07
     *
     * Changes:
     *
     *  - None.
     */

    public TimeStamp getOffset()
        throws SystemErrorException
    {

        return new TimeStamp(this.offset);

    } /* DataCell::getOffset() */

    public void setOffset(TimeStamp newOffset)
        throws SystemErrorException
    {
        final String mName = "DataCell::setOffset(newOffset): ";

        if ( newOffset == null )
        {
            throw new SystemErrorException(mName + "newOffset null on entry");
        }

        this.offset = new TimeStamp(newOffset);

        /* just in case, coerce tps to current value for db */
        this.offset.setTPS(getDB().getTicks());

        return;

    } /* DataCell::setOffset(newOffset) */


    /**
     * getOnset() & setOnset()
     *
     * Get and set the value of the onset field.
     *
     *                                      -- 8/29/07
     *
     * Changes:
     *
     *  - None.
     */

    public TimeStamp getOnset()
        throws SystemErrorException
    {

        return new TimeStamp(this.onset);

    } /* DataCell::getOnset() */

    public void setOnset(TimeStamp newOnset)
        throws SystemErrorException
    {
        final String mName = "DataCell::setOnset(newOnset): ";

        if ( newOnset == null )
        {
            throw new SystemErrorException(mName + "newOnset null on entry");
        }

        this.onset = new TimeStamp(newOnset);

        /* just in case, coerce tps to current value for db */
        this.onset.setTPS(getDB().getTicks());

        return;

    } /* DataCell::setOnset(newOnset) */

    /**
     * getVal()
     *
     * Return a copy of the current value of the data cell.
     *
     *                              -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */

    public Matrix getVal()
        throws SystemErrorException
    {

        return new Matrix(this.val);

    } /* DataCell::getVal() */


    /**
     * getValBlind()
     *
     * Return a copy of the current value of the data cell.  Skip any checks
     * with the matrix vocab element, or with the predicate vocab elements
     * associated with any predicates that may appear in the matrix.
     *
     *                              -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */

    protected Matrix getValBlind()
        throws SystemErrorException
    {

        return new Matrix(this.val, true);

    } /* DataCell::getValBlind() */

    /**
     * setVal()
     *
     * Set itsValue to the specified value.  If the val parameter is null,
     * set this.val to an empty argument list.  Otherwise, validate the
     * val parameter, and if no errors are detected, create a copy and
     * store it in this.val
     *
     *                                              -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */

    public void setVal(Matrix val)
        throws SystemErrorException
    {
        final String mName = "DataCell::setVal(): ";
        DBElement dbe;
        PredFormalArg pfa;

        if ( val == null )
        {
            this.val = new Matrix(getDB(), this.itsMveID);
        }
        else if ( val.getMveID() != this.itsMveID )
        {
            throw new SystemErrorException(mName + "mveID mismatch");
        }
        else
        {
            // this will thow a system error if there are problems.
            val.validateMatrix(false);

            this.val = new Matrix(val);
        }

        return;

    } /* PredDataValue::setVal() */
    
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

//        try
//        {
            s = "(DataCell (id " + this.getID() +
                ") (itsColID " + this.itsColID +
                ") (itsMveID " + this.itsMveID +
                ") (itsMveType " + this.itsMveType +
                ") (ord " + this.ord +
                ") (onset " + this.onset.toDBString() +
                ") (offset " + this.offset.toDBString() +
                ") (val " + this.val.toDBString() + "))";
//        }
//
//        catch (SystemErrorException e)
//        {
//             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
//        }

        return s;

    } /* DataCell::toDBString() */


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

//        try
//        {
            s = "(" + this.ord + ", " + this.onset.toString() + ", " +
                    this.offset.toString() + ", " + this.val.toString() + ")";
//        }
//
//        catch (SystemErrorException e)
//        {
//             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
//        }

        return (s);

    } /* DataCell::toString() */


    /*************************************************************************/
    /************************* Cascade Management: ***************************/
    /*************************************************************************/

    /* cascadeGetComment()
     *
     * If we are in a cascade, and this.pending != null, return the value of
     * this.pending.getComment().  Otherwise return the value of
     * this.getComment().
     *
     *                                          -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */

    protected String cascadeGetComment()
        throws SystemErrorException
    {
        String retVal = null;
        final String mName = "DataCell::cascadeGetComment()";

        if ( ( this.inCascade ) && ( this.pending != null ) )
        {
            retVal = this.pending.getComment();
        }
        else
        {
            retVal = this.getComment();
        }

        return retVal;

    } /* DataCell::cascadeGetComment() */


    /* cascadeGetOffset()
     *
     * If we are in a cascade, and this.pending != null, return the value of
     * this.pending.getOnset().  Otherwise return the value of this.getOnset().
     *
     *                                          -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */

    protected TimeStamp cascadeGetOffset()
        throws SystemErrorException
    {
        TimeStamp retVal = null;
        final String mName = "DataCell::cascadeGetOffset()";

        if ( ( this.inCascade ) && ( this.pending != null ) )
        {
            retVal = this.pending.getOffset();
        }
        else
        {
            retVal = this.getOffset();
        }

        return retVal;

    } /* DataCell::cascadeGetOffset() */


    /* cascadeGetOnset()
     *
     * If we are in a cascade, and this.pending != null, return the value of
     * this.pending.getOnset().  Otherwise return the value of this.getOnset().
     *
     *                                          -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */

    protected TimeStamp cascadeGetOnset()
        throws SystemErrorException
    {
        TimeStamp retVal = null;
        final String mName = "DataCell::cascadeGetOnset()";

        if ( ( this.inCascade ) && ( this.pending != null ) )
        {
            retVal = this.pending.getOnset();
        }
        else
        {
            retVal = this.getOnset();
        }

        return retVal;

    } /* DataCell::cascadeGetOnset() */


    /* cascadeGetOrd()
     *
     * If we are in a cascade, and this.pending != null, return the value of
     * this.pending.getOrd().  Otherwise return the value of this.getOrd().
     *
     *                                          -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */

    protected int cascadeGetOrd()
        throws SystemErrorException
    {
        int retVal = 0; /* a convenient, invalid value */
        final String mName = "DataCell::cascadeGetOrd()";

        if ( ( this.inCascade ) && ( this.pending != null ) )
        {
            retVal = this.pending.getOrd();
        }
        else
        {
            retVal = this.getOrd();
        }

        return retVal;

    } /* DataCell::cascadeGetOrd() */


    /* cascadeGetVal()
     *
     * If we are in a cascade, and this.pending != null, return the value of
     * this.pending.getVal().  Otherwise return the value of this.getVal().
     *
     *                                          -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */

    protected Matrix cascadeGetVal()
        throws SystemErrorException
    {
        Matrix retVal = null;
        final String mName = "DataCell::cascadeGetVal()";

        if ( ( this.inCascade ) && ( this.pending != null ) )
        {
            retVal = this.pending.getVal();
        }
        else
        {
            retVal = this.getVal();
        }

        return retVal;

    } /* DataCell::cascadeGetVal() */


    /* cascadeSetComment()
     *
     * If we are not in a cascade, enter one.
     *
     * If this.pending is null, call createPending().
     *
     * Pass the supplied comment on to this.pending.setComment().
     *
     *                                          -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void cascadeSetComment(String comment)
        throws SystemErrorException
    {
        final String mName = "DataCell::cascadeSetComment()";

        if ( ! this.inCascade )
        {
            this.enterCascade();
        }

        if ( this.pending == null )
        {
            this.createPending(false);
        }

        this.pending.setComment(comment);

        return;

    } /* DataCell::cascadeSetComment() */


    /* cascadeSetOffset()
     *
     * If we are not in a cascade, enter one.
     *
     * If this.pending is null, call createPending().
     *
     * Pass the supplied offset on to this.pending.setOffset().
     *
     *                                          -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void cascadeSetOffset(TimeStamp newOffset)
        throws SystemErrorException
    {
        final String mName = "DataCell::cascadeSetOffset()";

        if ( ! this.inCascade )
        {
            this.enterCascade();
        }

        if ( this.pending == null )
        {
            this.createPending(false);
        }

        this.pending.setOnset(newOffset);

        return;

    } /* DataCell::cascadeSetOffset() */


    /* cascadeSetOnset()
     *
     * If we are not in a cascade, enter one.
     *
     * If this.pending is null, call createPending().
     *
     * Pass the supplied onset on to this.pending.setOnset().
     *
     *                                          -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void cascadeSetOnset(TimeStamp newOnset)
        throws SystemErrorException
    {
        final String mName = "DataCell::cascadeSetOnset()";

        if ( ! this.inCascade )
        {
            this.enterCascade();
        }

        if ( this.pending == null )
        {
            this.createPending(false);
        }

        this.pending.setOnset(newOnset);

        return;

    } /* DataCell::cascadeSetOnset() */


    /* cascadeSetOrd()
     *
     * If we are not in a cascade, enter one, and set this.oldOrd to this.ord.
     *
     * If this.pending != null, pass the supplied ord through to
     * this.pending.setOrd().  Otherwise, pass the supplied value on to
     * this.setOrd().
     *
     *                                           -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void cascadeSetOrd(int newOrd)
        throws SystemErrorException
    {
        final String mName = "DataCell::cascadeSetOrd()";

        if ( ! this.inCascade )
        {
            /* If we are not already in the cascade on entry, we will fail
             * sanity check in DataColumn.addPending() as our ord on entry
             * will not equal 1 + our index in the cells vector.  (Recall we
             * modify the cells vector first, and then touch up the ords of
             * the entries).  Thus we must play a few games here to make
             * things work.
             */
            int oldOrd = this.ord;
            this.setOrd(newOrd);
            this.enterCascade();
            this.oldOrd = oldOrd;
        }
        else
        {
            if ( this.pending == null )
            {
                this.setOrd(newOrd);
            }
            else
            {
                this.pending.setOrd(newOrd);
            }
        }

        return;

    } /* DataCell::cascadeSetOrd() */


    /**
     * cascadeSetPending()
     *
     * If the target cell is currently part of the cascade, throw a system
     * error.  Otherwise, enter the cascade, verify that the supplied data
     * cell is an acceptable replacement value, and set this.pending to
     * refer to the supplied data cell.
     *
     *                                           -- 3/20/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void cascadeSetPending(DataCell newCell)
        throws SystemErrorException
    {
        final String mName = "DataCell::cascadeSetPending()";

        if ( this.inCascade )
        {
            throw new SystemErrorException(mName + " Already in cascade.");
        }

        if ( newCell == null )
        {
            throw new SystemErrorException(mName + "newCell is null.");
        }

        newCell.validateReplacementCell(this);

        this.enterCascade();

        this.pending = newCell;

        this.oldOrd = 0;

        return;

    } /* DataCell::cascadeSetPending() */


    /* cascadeSetVal()
     *
     * If we are not in a cascade, enter one.
     *
     * If this.pending is null, call createPending().
     *
     * Pass the supplied val on to this.pending.setVal().
     *
     *                                           -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void cascadeSetVal(Matrix newVal)
        throws SystemErrorException
    {
        final String mName = "DataCell::cascadeSetVal()";

        if ( ! this.inCascade )
        {
            this.enterCascade();
        }

        if ( this.pending == null )
        {
            this.createPending(false);
        }

        this.pending.setVal(newVal);

        return;

    } /* DataCell::cascadeSetVal() */


    /**
     * cascadeUpdateForFargListChange()
     *
     * Enter the cascade, and create a pending version of the data cell with
     * value update to reflect the specified changes in the formal argument
     * list.
     *
     * Start by verifying that the data cell has not yet entered the cascade.
     * Throw a system error if it has.
     *
     * The new version of the mve should have replace the old in the vocab
     * list by the time this method is called, so create a new cell with an
     * empty argument list, then set the ID, onset, offset, and ord to match
     * the existing cell.
     *
     * Then scan the old argument list, and copy all arguments whose formal
     * arguments still exist into the argument list of the new data cell after
     * updating the copy for any changes in the formal argument list.
     *
     * Finally, set this.pending to reference to newly created data cell.
     *
     * Note that all parameters to this routine must be treated as read only,
     * as for efficiency reasons, the same copy is passed to all the listeners
     * on any given vocab element changed call.
     *
     *                                               -- 3/20/08
     *
     * Changes:
     *
     *    - Added the column predicate related parameters to the method's
     *      argument list.
     *                                               -- 8/26/08
     */

    // Note:  This method isn't used at present.  It was created as part of
    //        listener code prior to the addition of support for column
    //        predicates.
    //
    //        At present, the work that used to be done by this method is
    //        being done by cascadeUpdateForMVEDefChange() -- however this
    //        routine is a bit heavy weight, and does a bit more processing
    //        than is strictly necessary.
    //
    //        If this becomes a problem, we may want to re-visit this
    //        method and use it in cases where we are sure that the column
    //        predicate implied by an MVE does not appear in a cell of the
    //        Data column associated with the MVE.
    //
    //                                              JRM -- 9/20/09

    protected void cascadeUpdateForFargListChange(
                                long[] n2o,
                                long[] o2n,
                                boolean[] fargNameChanged,
                                boolean[] fargSubRangeChanged,
                                boolean[] fargRangeChanged,
                                boolean[] fargDeleted,
                                boolean[] fargInserted,
                                java.util.Vector<FormalArgument> oldFargList,
                                java.util.Vector<FormalArgument> newFargList,
                                long[] cpn2o,
                                long[] cpo2n,
                                boolean[] cpFargNameChanged,
                                boolean[] cpFargSubRangeChanged,
                                boolean[] cpFargRangeChanged,
                                boolean[] cpFargDeleted,
                                boolean[] cpFargInserted,
                                java.util.Vector<FormalArgument> oldCPFargList,
                                java.util.Vector<FormalArgument> newCPFargList)
        throws SystemErrorException
    {
        final String mName = "DataCell::cascadeUpdateForFargListChange(): ";
        int i;
        int j;
        int numOldArgs;
        int numNewArgs;
        DataCell dc = null;
        DataValue dv = null;

        if ( this.inCascade )
        {
            throw new SystemErrorException(mName + "Already in cascade.");
        }

        this.enterCascade();

        this.cascadeMveMod = true;
        this.cascadeMveDel = false;
        this.cascadeMveID = this.itsMveID;
        this.cascadePveMod = false;
        this.cascadePveDel = false;
        this.cascadePveID = DBIndex.INVALID_ID;

        // since we have a change in the formal argument list of the mve,
        // don't use createPending() -- instead create an empty cell and
        // copy over arguments as appropriate.

        dc = new DataCell(this.getDB(), this.itsColID, this.itsMveID);
        dc.setID(this.getID());
        dc.setOrd(this.ord);
        dc.setOnset(this.onset);
        dc.setOffset(this.offset);

        numOldArgs = oldFargList.size();
        numNewArgs = newFargList.size();

        for ( i = 0; i < numOldArgs; i++ )
        {
            if ( ! fargDeleted[i] )
            {
                try {
                    dv = (DataValue) this.val.getArg(i).clone();
                } catch (CloneNotSupportedException e) {
                    throw new SystemErrorException("Unable to clone DataValue.");
                }

                j = (int)o2n[i];

                if ( ( fargNameChanged[j] ) ||
                     ( fargSubRangeChanged[j]) ||
                     ( fargRangeChanged[j] ) )
                {
                    // Update the data value for the formal argument change
                    dv.updateForFargChange(fargNameChanged[j],
                                           fargSubRangeChanged[j],
                                           fargRangeChanged[j],
                                           oldFargList.get(i),
                                           newFargList.get(j));
                }

                dc.val.replaceArg((int)o2n[i], dv);
            }
        }

        this.pending = dc;

        return;

    } /* DataCell::cascadeUpdateForFargListChange() */


    /**
     * cascadeUpdateForMVEDefChange()
     *
     * Update the value of the cell for a change in the definition of the
     * specified matrix vocab element.
     *
     * This call should be triggered either by an instance of column predicate
     * somewhere in the value of the cell receiving an MVEChanged() message
     * from the matrix vocab element that implies the column predicate, or
     * from the data column in which the cell resides, if the data column is
     * defined by the MVE in question.
     *
     * On entry, test to see if we are in a cascade.
     *
     * If we are not, enter the cascade, create the pending copy, and tell
     * it to update for the matrix vocab element change.  Note that we create
     * the pending copy whether or not the formal argument list has changed --
     * as the host data column will only call this method if it has, and a
     * component data value will only call if it contains a column predicate
     * that has changed due to the mve definition change.
     *
     * Make note of the supplied mveID, and of the fact that we entered the
     * cascade due to a mve modification.
     *
     * If we are already in a cascade, throw a system error unless the
     * we have already made note of our being in the cascade due to a change
     * in the MVE indicated by the mveID parameter.  In this latter case,
     * exit without taking any action.
     *                                           -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void cascadeUpdateForMVEDefChange(
                                 Database db,
                                 long mveID,
                                 boolean nameChanged,
                                 String oldName,
                                 String newName,
                                 boolean varLenChanged,
                                 boolean oldVarLen,
                                 boolean newVarLen,
                                 boolean fargListChanged,
                                 long[] n2o,
                                 long[] o2n,
                                 boolean[] fargNameChanged,
                                 boolean[] fargSubRangeChanged,
                                 boolean[] fargRangeChanged,
                                 boolean[] fargDeleted,
                                 boolean[] fargInserted,
                                 java.util.Vector<FormalArgument> oldFargList,
                                 java.util.Vector<FormalArgument> newFargList,
                                 long[] cpn2o,
                                 long[] cpo2n,
                                 boolean[] cpFargNameChanged,
                                 boolean[] cpFargSubRangeChanged,
                                 boolean[] cpFargRangeChanged,
                                 boolean[] cpFargDeleted,
                                 boolean[] cpFargInserted,
                                 java.util.Vector<FormalArgument> oldCPFargList,
                                 java.util.Vector<FormalArgument> newCPFargList)
        throws SystemErrorException
    {
        final String mName = "DataCell::cascadeUpdateForMVEDefChange(): ";
        DBElement dbe = null;

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID invalid.");
        }

        dbe = this.getDB().idx.getElement(mveID);

        if ( ! ( dbe instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName +
                                           "mveID doesn't refer to a mve.");
        }

        if ( ! this.inCascade )
        {
            this.enterCascade();

            if ( ( this.itsMveID == mveID ) && ( fargListChanged ) )
            {
                int i;
                int j;
                int numOldArgs;
                int numNewArgs;
                DataCell dc = null;
                DataValue dv = null;

                // since we have a change in the formal argument list of the mve,
                // don't use createPending() -- instead create an empty cell and
                // copy over arguments as appropriate.

                dc = new DataCell(this.getDB(), this.itsColID, this.itsMveID);
                dc.setID(this.getID());
                dc.setOrd(this.ord);
                dc.setOnset(this.onset);
                dc.setOffset(this.offset);

                numOldArgs = oldFargList.size();
                numNewArgs = newFargList.size();

                for ( i = 0; i < numOldArgs; i++ )
                {
                    if ( ! fargDeleted[i] )
                    {
                        try
                        {
                            dv = (DataValue) this.val.getArg(i).clone();
                        } 
                        
                        catch ( CloneNotSupportedException e )
                        {
                            throw new SystemErrorException(mName +
                                    "Unable to clone DataValue.");
                        }

                        j = (int)o2n[i];

                        if ( ( fargNameChanged[j] ) ||
                             ( fargSubRangeChanged[j]) ||
                             ( fargRangeChanged[j] ) )
                        {
                            // Update the data value for the formal argument change
                            dv.updateForFargChange(fargNameChanged[j],
                                                   fargSubRangeChanged[j],
                                                   fargRangeChanged[j],
                                                   oldFargList.get(i),
                                                   newFargList.get(j));
                        }

                        dc.val.replaceArg((int)o2n[i], dv);
                    }
                }

                this.pending = dc;
            }
            else
            {
                this.createPending(true);
            }

            this.cascadeMveMod = true;
            this.cascadeMveID = mveID;
            this.cascadeMveDel = false;
            this.cascadePveMod = false;
            this.cascadePveID = DBIndex.INVALID_ID;
            this.cascadePveDel = false;
            // todo: delete this eventually
//            System.out.printf("cascade mve mod/del/id = %s/%s/%d.\n",
//                              ((Boolean)(this.cascadeMveMod)).toString(),
//                              ((Boolean)(this.cascadeMveDel)).toString(),
//                              this.cascadeMveID);
//            System.out.printf("cascade pve mod/del/id = %s/%s/%d.\n",
//                           ((Boolean)(this.cascadePveMod)).toString(),
//                           ((Boolean)(this.cascadePveDel)).toString(),
//                           this.cascadePveID);
//            System.out.flush();

            this.pending.updateForMVEDefChange(db,
                                               mveID,
                                               nameChanged,
                                               oldName,
                                               newName,
                                               varLenChanged,
                                               oldVarLen,
                                               newVarLen,
                                               fargListChanged,
                                               n2o,
                                               o2n,
                                               fargNameChanged,
                                               fargSubRangeChanged,
                                               fargRangeChanged,
                                               fargDeleted,
                                               fargInserted,
                                               oldFargList,
                                               newFargList,
                                               cpn2o,
                                               cpo2n,
                                               cpFargNameChanged,
                                               cpFargSubRangeChanged,
                                               cpFargRangeChanged,
                                               cpFargDeleted,
                                               cpFargInserted,
                                               oldCPFargList,
                                               newCPFargList);
        }
        else if ( ( this.cascadeMveMod != true ) ||
                  ( this.cascadeMveID != mveID ) ||
                  ( this.cascadePveMod != false ) ||
                  ( this.cascadePveID != DBIndex.INVALID_ID ) ||
                  ( this.cascadePveDel != false ) )
        {
            throw new SystemErrorException(mName +
                    "already in cascade for other reasons?!?");
        }

        return;

    } /* DataCell::cascadeUpdateForMVEDefChange() */


    /**
     * cascadeUpdateForMVEDeletion()
     *
     * Update the value of the cell for the deletion of the indicated matrix
     * vocab element.
     *
     * This call should be triggered by an instance of co;umn predicate
     * somewhere in the value of the cell receiving a MVEDeleted()
     * message from the matrix vocab element in question.
     *
     * On entry, test to see if we are in a cascade.
     *
     * If we are not, enter the cascade, create the pending copy, and tell
     * it to update for the predicate vocab element deletion.  Make note of the
     * supplied mveID, and of the fact that we entered the cascade due to a
     * mve deletion.
     *
     * If we are already in a cascade, throw a system error unless the
     * we have already made note of our being in the cascade due to the
     * deletion the MVE indicated by the mveID parameter.  In this latter case,
     * exit without taking any action.
     *
     * Note that this method should never be called if the supplied mveID
     * is equal to this.itsMveID.  If this ever happens, throw a system
     * error excpetion.
     *
     * Changes:
     *
     *    - None.
     */

    protected void cascadeUpdateForMVEDeletion(Database db,
                                               long mveID)
        throws SystemErrorException
    {
        final String mName = "DataCell::cascadeUpdateForMVEDeletion(): ";

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID invalid.");
        }

        if ( mveID == this.itsMveID )
        {
            throw new SystemErrorException(mName + "mveID == this.itsMveID");
        }

        if ( ! this.inCascade )
        {
            this.enterCascade();
            this.createPending(true);
            this.cascadeMveMod = false;
            this.cascadeMveDel = true;
            this.cascadeMveID = mveID;
            this.cascadePveMod = false;
            this.cascadePveDel = false;
            this.pending.updateForMVEDeletion(db, mveID);
        }
        else if ( ( this.cascadeMveMod != false ) ||
                  ( this.cascadeMveDel != true ) ||
                  ( this.cascadeMveID != mveID ) ||
                  ( this.cascadePveMod != false ) ||
                  ( this.cascadePveDel != false ) )
        {
            throw new SystemErrorException(mName +
                    "already in cascade for other reasons?!?");
        }

        return;

    } /* DataCell::cascadeUpdateForMVEDeletion() */


    /**
     * cascadeUpdateForPVEDefChange()
     *
     * Update the value of the cell for a change in the definition of the
     * specified predicate vocab element.  This call should be triggered by an
     * instance of predicate somewhere in the value of the cell receiving a
     * VEChanged() message from the predicate vocab element in question.
     *
     * On entry, test to see if we are in a cascade.
     *
     * If we are not, enter the cascade, create the pending copy, and tell
     * it to update for the predicate vocab element change.  Make note of the
     * supplied pveID, and of the fact that we entered the cascade due to a
     * pve modification.
     *
     * If we are already in a cascade, throw a system error unless the
     * we have already made note of our being in the cascade due to a change
     * in the PVE indicated by the pveID parameter.  In this latter case,
     * exit without taking any action.
     *
     * Changes:
     *
     *    - None.
     */

    protected void cascadeUpdateForPVEDefChange(
                                 Database db,
                                 long pveID,
                                 boolean nameChanged,
                                 String oldName,
                                 String newName,
                                 boolean varLenChanged,
                                 boolean oldVarLen,
                                 boolean newVarLen,
                                 boolean fargListChanged,
                                 long[] n2o,
                                 long[] o2n,
                                 boolean[] fargNameChanged,
                                 boolean[] fargSubRangeChanged,
                                 boolean[] fargRangeChanged,
                                 boolean[] fargDeleted,
                                 boolean[] fargInserted,
                                 java.util.Vector<FormalArgument> oldFargList,
                                 java.util.Vector<FormalArgument> newFargList)
        throws SystemErrorException
    {
        final String mName = "DataCell::cascadeUpdateForPVEDefChange(): ";
        DBElement dbe = null;

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( pveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "pveID invalid.");
        }

        dbe = this.getDB().idx.getElement(pveID);

        if ( ! ( dbe instanceof PredicateVocabElement ) )
        {
            throw new SystemErrorException(mName +
                                           "pveID doesn't refer to a pve.");
        }

        if ( ! this.inCascade )
        {
            this.enterCascade();
            this.createPending(true);
            this.cascadeMveMod = false;
            this.cascadeMveDel = false;
            this.cascadePveID = pveID;
            this.cascadePveMod = true;
            this.cascadePveDel = false;
            this.pending.updateForPVEDefChange(db,
                                               pveID,
                                               nameChanged,
                                               oldName,
                                               newName,
                                               varLenChanged,
                                               oldVarLen,
                                               newVarLen,
                                               fargListChanged,
                                               n2o,
                                               o2n,
                                               fargNameChanged,
                                               fargSubRangeChanged,
                                               fargRangeChanged,
                                               fargDeleted,
                                               fargInserted,
                                               oldFargList,
                                               newFargList);
        }
        else if ( ( this.cascadeMveMod != false ) ||
                  ( this.cascadeMveDel = false ) ||
                  ( this.cascadePveID != pveID ) ||
                  ( this.cascadePveMod != true ) ||
                  ( this.cascadePveDel != false ) )
        {
            throw new SystemErrorException(mName +
                    "already in cascade for other reasons?!?");
        }

        return;

    } /* DataCell::cascadeUpdateForPVEDefChange() */


    /**
     * cascadeUpdateForPVEDeletion()
     *
     * Update the value of the cell for the deletion of the predicate vocab
     * element.  This call should be triggered by an instance of predicate
     * somewhere in the value of the cell receiving a VEDeleted()
     * message from the predicate vocab element in question.
     *
     * On entry, test to see if we are in a cascade.
     *
     * If we are not, enter the cascade, create the pending copy, and tell
     * it to update for the predicate vocab element deletion.  Make note of the
     * supplied pveID, and of the fact that we entered the cascade due to a
     * pve deletion.
     *
     * If we are already in a cascade, throw a system error unless the
     * we have already made note of our being in the cascade due to the
     * deletion the PVE indicated by the pveID parameter.  In this latter case,
     * exit without taking any action.
     *
     * Changes:
     *
     *    - None.
     */

    protected void cascadeUpdateForPVEDeletion(Database db,
                                               long pveID)
        throws SystemErrorException
    {
        final String mName = "DataCell::cascadeUpdateForPVEDeletion(): ";

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( pveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "pveID invalid.");
        }

        if ( ! this.inCascade )
        {
            this.enterCascade();
            this.createPending(true);
            this.cascadeMveMod = false;
            this.cascadeMveDel = false;
            this.cascadePveID = pveID;
            this.cascadePveMod = false;
            this.cascadePveDel = true;
            this.pending.updateForPVEDeletion(db, pveID);
        }
        else if ( ( this.cascadeMveMod != false ) ||
                  ( this.cascadeMveDel = false ) ||
                  ( this.cascadePveID != pveID ) ||
                  ( this.cascadePveMod != false ) ||
                  ( this.cascadePveDel != true ) )
        {
            throw new SystemErrorException(mName +
                    "already in cascade for other reasons?!?");
        }

        return;

    } /* DataCell::cascadeUpdateForPVEDeletion() */


    /**
     * createPending()
     *
     * Verify that this.inCascade is true.  Throw a system error if it isn't.
     *
     * Then create a copy of this cell, and store its reference in this.pending.
     *
     * Finally, if this.oldOrd is not zero, set this.ord to this.oldOrd, and
     * set this.oldOrd to zero.
     *
     *                                           -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */

    private void createPending(boolean blindCopy)
        throws SystemErrorException
    {
        final String mName = "DataCell::createPending()";

        if ( ! this.inCascade )
        {
            throw new SystemErrorException(mName +
                "Attempt to create a pending copy when not part of a cascade.");
        }

        if ( this.pending != null )
        {
            throw new SystemErrorException(mName + "this.pending != null?!?!?");
        }

        this.pending = new DataCell(this, blindCopy);

        if ( this.oldOrd != 0 )
        {
            this.setOrd(oldOrd);
            this.oldOrd = 0;
        }

        return;

    } /* DataCell::createPending() */


    /**
     * enterCascade()
     *
     * Handle the book keeping required when a cell is recruited into a cascade
     * of changes.
     *
     * 1) Test to see if this.listeners is null.  Throw a system error if it is.
     *
     * 2) Test to see if this.inCascade is true.  Throw a system error if it is.
     *
     * 3) Verify that this.pending is null.  Throw a system error if it isn't.
     *
     * 4) Verify that this.oldOrd == 0.  Thow a system error if it isn't.
     *
     * 5) Set this.oldOrd = this.ord.
     *
     * 6) Set this.inCascade to true.
     *
     * 7) Add this cell to the host data column's pending list.
     *
     *                                           -- 5/15/08
     *
     * Changes:
     *
     *    - None.
     */

    private void enterCascade()
        throws SystemErrorException
    {
        final String mName = "DataCell::enterCascade(): ";
        DataColumn dc = null;

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
                "Call to enterCascade() on non-cannonical version.");
        }

        if ( this.inCascade )
        {
            throw new SystemErrorException(mName +
                "Call to enterCascade() when inCascade == true?!?!?");
        }

        if ( this.pending != null )
        {
            throw new SystemErrorException(mName +
                    "this.pending != null on cascade entry?!?!?");
        }

        if ( this.oldOrd != 0 )
        {
            throw new SystemErrorException(mName +
                    "this.oldOrd != 0 on cascade entry?!?!?");
        }

        if ( ( this.cascadeMveMod != false ) ||
             ( this.cascadePveID != DBIndex.INVALID_ID ) ||
             ( this.cascadePveMod != false ) ||
             ( this.cascadePveDel != false ) )
        {
            throw new SystemErrorException(mName +
                    "mve mod/(pve ID/mod/deli) already set?!?");
        }

        dc = this.lookupDataCol(this.itsColID);

        this.oldOrd = this.ord;

        this.inCascade = true;

        dc.addPending(this);

        return;

    } /* DataCell::enterCascade() */


    /**
     * exitCascade()
     *
     * Handle the book keeping required when a cell exits a cascade of changes.
     *
     * 1) Test to see if this.listeners is null.  Throw a system error if it is.
     *
     * 2) Test to see if this.inCascade is false.  Throw a system error if it is.
     *
     * 3) If this.pending is not null, instruct the host data column to replace
     *    this with this.pending as the cannonical incarnation of the data cell.
     *
     *    Otherwise, only the ord has changed.  Note this fact and notify
     *    the listeners.
     *
     * 4) set this.oldOrd = 0;
     *
     * 5) Set this.inCascade to false.
     *
     * This method is called by the host data column when it receives an end
     * cascade message.
     *
     *                                           -- 5/15/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void exitCascade()
        throws SystemErrorException
    {
        final String mName = "DataCell::exitCascade()";
        DataColumn dc = null;

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
                "Call to exitCascade() on non-cannonical version.");
        }

        if ( ! this.inCascade )
        {
            throw new SystemErrorException(mName +
                "Call to exitCascade() when inCascade == false?!?!?.");
        }

        if ( this.pending != null )
        {
            dc = this.lookupDataCol(this.itsColID);

            /* Tell column to replace this with this.pending.  Note that
             * this call will transfer the listeners, and generate messages
             * to all the cell listeners
             */
            dc.cascadeReplaceCell(this, this.pending);
        }
        else /* only ord has changed -- note the change and notify listeners */
        {
            this.listeners.noteOrdChangeOnly(this, this.oldOrd, this.ord);
            this.notifyListenersOfChange();
        }

        this.oldOrd = 0;

        this.inCascade = false;

        this.cascadeMveID  = DBIndex.INVALID_ID;
        this.cascadeMveMod = false;
        this.cascadeMveDel = false;
        this.cascadePveID  = DBIndex.INVALID_ID;
        this.cascadePveMod = false;
        this.cascadePveDel = false;

        return;

    } /* DataCell::exitCascade() */


    /**
     * getInCascade()
     *
     * Return this.inCascade.
     *
     *               -- 3/20/08
     *
     * Changes:
     *
     *    - None.
     */

    protected boolean getInCascade()
    {

        return this.inCascade;

    } /* DataCell getInCascade() */


    /*************************************************************************/
    /*********************** Listener Manipulation: **************************/
    /*************************************************************************/

    /**
     * deregisterExternalChangeListener()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the deregister external change listeners message on to
     * the instance of DataCellListeners pointed to by this.listeners.
     *
     *                                           -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterExternalListener(ExternalDataCellListener el)
        throws SystemErrorException
    {
        final String mName = "DataCell::deregisterExternalListener()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
                "Attempt to add external listener to non-cannonical version.");
        }

        this.listeners.deregisterExternalListener(el);

        return;

    } /* DataCell::deregisterExternalListener() */


    /**
     * deregisterInternalListener()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the deregister internal change listeners message on to
     * the instance of DataCellListeners pointed to by this.listeners.
     *
     *                                           -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterInternalListener(long id)
        throws SystemErrorException
    {
        final String mName = "DataCell::deregisterInternalListener()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
                "Attempt to add internal listener to non-cannonical version.");
        }

        this.listeners.deregisterInternalListener(id);

        return;

    } /* DataCell::deregisterInternalListener() */


    /**
     * getListeners()
     *
     * Return the corrent value of this.listeners.
     *
     *                                           -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected DataCellListeners getListeners()
    {

        return this.listeners;

    } /* DataCell::getListeners() */


    /**
     * noteChanges()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass a note changes message on to the instance of
     * DataCellListeners pointed to by this.listeners.
     *
     *                                           -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void noteChange(DataCell oldDC,
                              DataCell newDC)
        throws SystemErrorException
    {
        final String mName = "DataCell::noteChanges()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
                "Attempt to note changes on non-cannonical version.");
        }

        this.listeners.noteChange(oldDC,
                                  newDC,
                                  oldDC.cascadeMveMod,
                                  oldDC.cascadePveDel,
                                  oldDC.cascadePveMod,
                                  oldDC.cascadePveID);

        return;

    } /* DataCell::noteChange() */


    /**
     * notifyListenersOfChange()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the notify listeners of changes message on to the
     * instance of DataCellListeners pointed to by this.listeners.
     *
     *                                           -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfChange()
        throws SystemErrorException
    {
        final String mName = "DataCell::notifyListenersOfChange()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
            "Attempt to notify listeners of change on non-cannonical version.");
        }

        this.listeners.notifyListenersOfChange();

        return;

    } /* VocabElement::notifyListenersOfChange() */


    /**
     * notifyListenersOfDeletion()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the notify listeners of deletion message on to the
     * instance of VocabElementListeners pointed to by this.listeners.
     *
     *                                           -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void notifyListenersOfDeletion()
        throws SystemErrorException
    {
        final String mName = "DataCell::notifyListenersOfDeletion()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
                    "Attempt to notify listeners of deletion on " +
                    "non-cannonical version.");
        }

        this.listeners.notifyListenersOfDeletion();

        return;

    } /* DataCell::notifyListenersOfDeletion() */


    /**
     * registerExternalChangeListener()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the register external change listeners message on to the
     * instance of DataCellListeners pointed to by this.listeners.
     *
     *                                           -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerExternalListener(ExternalDataCellListener el)
        throws SystemErrorException
    {
        final String mName = "DataCell::registerExternalListener()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
            "Attempt to register external listener to non-cannonical version.");
        }

        this.listeners.registerExternalListener(el);

        return;

    } /* DataCell::registerExternalListener() */


    /**
     * registerInternalChangeListener()
     *
     * If this.listeners is null, thow a system error exception.
     *
     * Otherwise, pass the register internal change listeners message on to the
     * instance of DataCellListeners pointed to by this.listeners.
     *
     *                                           -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerInternalListener(long id)
        throws SystemErrorException
    {
        final String mName = "DataCell::registerInternalListener()";

        if ( this.listeners == null )
        {
            throw new SystemErrorException(mName +
            "Attempt to register internal listener to non-cannonical version.");
        }

        this.listeners.registerInternalListener(id);

        return;

    } /* DataCell::registerInternalListener() */


    /**
     * setListeners()
     *
     * Set the listeners field.  Setting this.listeners to a non-null value
     * signifies that this instance of DataCell is the cannonical current
     * incarnation of the data ce;;.  Setting it back to null indicates
     * that the incarnation has been superceeded.
     *
     * If this.listeners is null, it may be set to reference an instance
     * of DataCellListeners that is associated with this data cell.  If
     * this.listeners is not null, the only permissiable new value is null.
     *
     * In all other cases, throw a system error exception.
     *
     *                                           -- 2/5/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void setListeners(DataCellListeners listeners)
        throws SystemErrorException
    {
        final String mName = "DataCell::setListeners()";

        if ( this.listeners == null )
        {
            if ( listeners == null )
            {
                throw new SystemErrorException(mName +
                        ": this.listeners is already null");
            }

            this.listeners = listeners;
            this.listeners.updateItsCell(this);
        }
        else
        {
            if ( listeners != null )
            {
                throw new SystemErrorException(mName +
                        ": this.listeners is already non-null.");
            }

            this.listeners = null;
        }

        return;

    } /* DataCell::setListeners() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/


    /**
     * deregisterPreds()
     *
     * If the DataCell has mve type MATRIX or PREDICATE, pass a deregister
     * predicates message to this.val.
     *
     * The objective is to get all instances of Predicate that may appear in
     * the value of the cell to deregister as internal vocab element listeners
     * with their associated PVEs.
     *                                               -- 3/24/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterPreds()
        throws SystemErrorException
    {
        if ( ( this.itsMveType == MatrixVocabElement.MatrixType.MATRIX ) ||
             ( this.itsMveType == MatrixVocabElement.MatrixType.PREDICATE ) )
        {
            this.val.deregisterPreds(this.cascadeMveDel,
                                     this.cascadeMveID,
                                     this.cascadePveDel,
                                     this.cascadePveID);
        }

        return;

    } /* DataCell::deregisterPreds() */


    /**
     * insertValInIndex()
     *
     * This method is called when the DataCell is inserted in the database
     * and becomes the first cannonical version of the DataCell.  The method
     * passes the cell's ID down to the instance of Matrix that stores the
     * value of the cell, which in turn passes that ID along to the instances
     * of DataValue (and possibly Predicate) that make up the value of the cell.
     *
     * In addition, these constituents are instructed to insert themselves in
     * the index as appropriate.
     *
     * Note that this method assumes that the DataCell has been inserted in
     * the index and assigned an ID before the method is called.
     *
     *                                               -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void insertValInIndex()
        throws SystemErrorException
    {
        final String mName = "DataCell::insertValInIndex(): ";

        if ( this.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "ID not assigned?!?");
        }

        if ( this.val == null )
        {
            throw new SystemErrorException(mName + "val is null?!?");
        }

        this.val.insertInIndex(this.getID());

        return;

    } /* DataCell::insertValInIndex() */

    /**
     * lookupDataCol()
     *
     * Given an ID, attempt to look up the associated DataColumn
     * in the database associated with the DataCell.  If there is no
     * such DataColumn, throw  a system error.
     *
     *                                               -- 11/28/07
     *
     * Changes:
     *
     *    - None.
     */

    private DataColumn lookupDataCol(long colID)
        throws SystemErrorException
    {
        final String mName = "DataCell::lookupDataCol(colID): ";
        DBElement dbe;
        DataColumn col;

        if ( colID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "colID == INVALID_ID");
        }

        dbe = this.getDB().idx.getElement(colID);

        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "colID has no referent");
        }

        if ( ! ( dbe instanceof DataColumn ) )
        {
            throw new SystemErrorException(mName +
                    "colID doesn't refer to a data column");
        }

        col = (DataColumn)dbe;

        return col;

    } /* DataCell::lookupDataCol(colID) */


    /**
     * lookupMatrixVE()
     *
     * Given an ID, attempt to look up the associated MatrixVocabElement
     * in the database associated with the DataColumn.  If there is no
     * such MatrixVocabElement, throw  a system error.
     *
     *                                               -- 8/24/07
     *
     * Changes:
     *
     *    - None.
     */

    private MatrixVocabElement lookupMatrixVE(long mveID)
        throws SystemErrorException
    {
        final String mName = "DataCell::lookupMatrixVE(mveID): ";
        DBElement dbe;
        MatrixVocabElement mve;

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID == INVALID_ID");
        }

        dbe = this.getDB().idx.getElement(mveID);

        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "mveID has no referent");
        }

        if ( ! ( dbe instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName +
                    "mveID doesn't refer to a matrix vocab element");
        }

        mve = (MatrixVocabElement)dbe;

        return mve;

    } /* DataCell::lookupMatrixVE(mveID) */


    /**
     * registerPreds()
     *
     * If the DataCell has mve type MATRIX, pass a register predicates message
     * to this.val.
     *
     * The objective is to get all instances of Predicate that may appear in
     * the value of the cell to register as internal vocab element listeners
     * with their associated PVEs.
     *                                               -- 3/24/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerPreds()
        throws SystemErrorException
    {
        if ( ( this.itsMveType == MatrixVocabElement.MatrixType.MATRIX ) ||
             ( this.itsMveType == MatrixVocabElement.MatrixType.PREDICATE ) )
        {
            this.val.registerPreds();
        }

        return;

    } /* DataCell::registerPreds() */


    /**
     * removeValFromIndex()
     *
     * This method is called when the DataCell is deleted from the database,
     * and the constituents of its value must be removed from the index.
     *
     * The method passes the remove from index message down to the instance of
     * Matrix that stores the value of the cell, which in turn passes the
     * message along to the instances of DataValue (and possibly Predicate)
     * that make up the value of the cell.
     *
     * In addition, these constituents are instructed to de-register as
     * listeners as appropriate.
     *
     * Note that this method assumes that the DataCell is still in the index
     * at the time of call.
     *
     *                                               -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void removeValFromIndex()
        throws SystemErrorException
    {
        final String mName = "DataCell::removeValFromIndex(): ";

        if ( this.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "ID not assigned?!?");
        }

        if ( this.val == null )
        {
            throw new SystemErrorException(mName + "val is null?!?");
        }

        this.val.removeFromIndex(this.getID());

        return;

    } /* DataCell::removeValFromIndex() */


    /**
     * toMODBFile()
     *
     * Write the MacSHAPA ODB style definition of the data cell
     * to the supplied file in MacSHAPA ODB file format.  The output of this
     * method is the <s_var_cell> in the grammar defining the MacSHAPA ODB
     * file format.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     *                                              12/31/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile(MatrixVocabElement mve,
                              java.io.PrintStream output,
                              String newLine,
                              String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "DataCell::toMODBFile()";

        if ( ( mve == null ) ||
             ( mve.getID() != this.itsMveID ) )
        {
            throw new SystemErrorException(mName + "bad mve on entry");
        }

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( newLine == null )
        {
            throw new SystemErrorException(mName + "newLine null on entry");
        }

        if ( indent == null )
        {
            throw new SystemErrorException(mName + "indent null on entry");
        }

        // opening paren, cell onset and offset ...
        output.printf("%s(%s", indent, newLine);

        output.printf("%s  ( ONSET> %d )%s", indent,
                this.onset.getTime(), newLine);

        output.printf("%s  ( OFFSET> %d )%s", indent,
                this.offset.getTime(), newLine);

        // ... cell value ...
        this.val.toMODBFile(mve, output, newLine, indent + "  ");

        // ... closing paren and new line.
        output.printf("%s)%s", indent, newLine);

        return;

    } /* DataCell::toMODBFile() */
    
    
    /**
     * toMODBFile_update_local_vocab_list()
     * 
     * Pass the toMODBFile_update_local_vocab_list() on to the instance of 
     * Matrix that comprises the value of the cell.
     * 
     *                                                  7/22/09
     * 
     * Changes:
     * 
     *    - None.
     * 
     * @param dc
     * @throws org.openshapa.db.SystemErrorException
     */


    protected void
    toMODBFile_update_local_vocab_list(DataColumn dc)
        throws SystemErrorException
    {
        final String mName = "DataCell::toMODBFile_update_local_vocab_list(): ";

        if ( ( dc == null ) ||
             ( dc.getID() != this.itsColID ) )
        {
            throw new SystemErrorException(mName +
                                           "bad/mismatch dc parameter.");
        }

        if ( this.val == null )
        {
            throw new SystemErrorException(mName + "val is null!?!?");
        }

        this.val.toMODBFile_update_local_vocab_list(dc);

        return;

    } /* DataCell::toMODBFile_update_local_vocab_list() */


    /**
     * validateNewCell()
     *
     * Verify that the value of a cell that is about to be inserted in
     * the database as the first incarnation of the cell is valid for the
     * target data column / matrix vocab element.
     *
     *                                       -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void validateNewCell()
        throws SystemErrorException
    {
        final String mName = "DataCell::validateNewCell()";

        if ( this.val == null )
        {
            throw new SystemErrorException(mName + "val is null?!?");
        }

        this.val.validateNewMatrix();

        return;

    } /* DataCell::validateNewCell() */


    /**
     * validateReplacementCell()
     *
     * Verify that the value of a cell that is about to be inserted in
     * the database as a new incarnation of an existing cell is a valid
     * replacement.  Note that this method assumes no change in the
     * underlying matrix and vocab elements, only changes in the argument
     * list of the cell proper.
     *
     *                                       -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void validateReplacementCell(DataCell oldCell)
        throws SystemErrorException
    {
        final String mName = "DataCell::validateReplacementCell()";

        if ( this.val == null )
        {
            throw new SystemErrorException(mName + "val is null?!?");
        }

        if ( oldCell == null )
        {
            throw new SystemErrorException(mName + "oldCell is null?!?!");
        }

        if ( oldCell.val == null )
        {
            throw new SystemErrorException(mName + "oldCell.val is null?!?");
        }

        // TODO: delete this eventually
//        System.out.printf("oldCell cascade mve mod/del/id = %s/%s/%d.\n",
//                          ((Boolean)(oldCell.cascadeMveMod)).toString(),
//                          ((Boolean)(oldCell.cascadeMveDel)).toString(),
//                          oldCell.cascadeMveID);
//        System.out.printf("oldCell cascade pve mod/del/id = %s/%s/%d.\n",
//                       ((Boolean)(oldCell.cascadePveMod)).toString(),
//                       ((Boolean)(oldCell.cascadePveDel)).toString(),
//                       oldCell.cascadePveID);
//
//        System.out.printf("newCell cascade mve mod/del/id = %s/%s/%d.\n",
//                          ((Boolean)(this.cascadeMveMod)).toString(),
//                          ((Boolean)(this.cascadeMveDel)).toString(),
//                          this.cascadeMveID);
//        System.out.printf("newCell cascade pve mod/del/id = %s/%s/%d.\n",
//                       ((Boolean)(this.cascadePveMod)).toString(),
//                       ((Boolean)(this.cascadePveDel)).toString(),
//                       this.cascadePveID);

        this.val.validateReplacementMatrix(oldCell.val,
                                           oldCell.cascadeMveMod,
                                           oldCell.cascadeMveDel,
                                           oldCell.cascadeMveID,
                                           oldCell.cascadePveMod,
                                           oldCell.cascadePveDel,
                                           oldCell.cascadePveID);

        return;

    } /* DataCell::validateReplacementCell() */


    /**
     * updateForMVEDefChange()
     *
     * Update the value of the cell for a change in the definition of the
     * specified matrix vocab element.  This call should be triggered by
     * either the host data column or an instance of column predicate somewhere
     * in the value of this cell receiving a MVEChanged() message from the
     * matrix vocab element in question.
     *
     * In either case, we should already be in a cascade, and this cell should
     * be the pending cell that will be used to replace the old cell upon
     * cascade termination.
     *
     *                                               -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForMVEDefChange(
                                 Database db,
                                 long mveID,
                                 boolean nameChanged,
                                 String oldName,
                                 String newName,
                                 boolean varLenChanged,
                                 boolean oldVarLen,
                                 boolean newVarLen,
                                 boolean fargListChanged,
                                 long[] n2o,
                                 long[] o2n,
                                 boolean[] fargNameChanged,
                                 boolean[] fargSubRangeChanged,
                                 boolean[] fargRangeChanged,
                                 boolean[] fargDeleted,
                                 boolean[] fargInserted,
                                 java.util.Vector<FormalArgument> oldFargList,
                                 java.util.Vector<FormalArgument> newFargList,
                                 long[] cpn2o,
                                 long[] cpo2n,
                                 boolean[] cpFargNameChanged,
                                 boolean[] cpFargSubRangeChanged,
                                 boolean[] cpFargRangeChanged,
                                 boolean[] cpFargDeleted,
                                 boolean[] cpFargInserted,
                                 java.util.Vector<FormalArgument> oldCPFargList,
                                 java.util.Vector<FormalArgument> newCPFargList)
        throws SystemErrorException
    {
        final String mName = "DataCell::updateForMVEDefChange(): ";
        DBElement dbe = null;

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID invalid.");
        }

        dbe = this.getDB().idx.getElement(mveID);

        if ( ! ( dbe instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName +
                                           "mveID doesn't refer to a mve.");
        }

        this.val.updateForMVEDefChange(db,
                                       mveID,
                                       nameChanged,
                                       oldName,
                                       newName,
                                       varLenChanged,
                                       oldVarLen,
                                       newVarLen,
                                       fargListChanged,
                                       n2o,
                                       o2n,
                                       fargNameChanged,
                                       fargSubRangeChanged,
                                       fargRangeChanged,
                                       fargDeleted,
                                       fargInserted,
                                       oldFargList,
                                       newFargList,
                                       cpn2o,
                                       cpo2n,
                                       cpFargNameChanged,
                                       cpFargSubRangeChanged,
                                       cpFargRangeChanged,
                                       cpFargDeleted,
                                       cpFargInserted,
                                       oldCPFargList,
                                       newCPFargList);

        return;

    } /* DataCell::updateForMVEDefChange() */


    /**
     * updateForMVEDeletion()
     *
     * Update the value of the cell for the deletion of the specified matrix
     * vocab element.
     *
     * This call should be triggered by an instance of column predicate
     * somewhere in the value of the cell receiving an MVEDeleted()
     * message from the matrix vocab element in question.
     *
     * On entry, test to see if we are in a cascade.
     *
     * If we are not, enter the cascade, create the pending copy, and tell
     * it to update for the matrix vocab element deletion.  Make note of the
     * supplied mveID, and of the fact that we entered the cascade due to a
     * mve deletion.
     *
     * If we are already in a cascade, throw a system error unless the
     * we have already made note of our being in the cascade due to the
     * deletion the MVE indicated by the mveID parameter.  In this latter case,
     * exit without taking any action.
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForMVEDeletion(Database db,
                                        long mveID)
        throws SystemErrorException
    {
        final String mName = "DataCell::updateForMVEDeletion(): ";

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID invalid.");
        }

        this.val.updateForMVEDeletion(db, mveID);

        return;

    } /* DataCell::updateForMVEDeletion() */


    /**
     * updateForPVEDefChange()
     *
     * Update the value of the cell for a change in the definition of the
     * specified predicate vocab element.  This call should be triggered by an
     * instance of predicate somewhere in the value of the cell receiving a
     * VEChanged() message from the predicate vocab element in question.
     *
     *                                           -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForPVEDefChange(
                                 Database db,
                                 long pveID,
                                 boolean nameChanged,
                                 String oldName,
                                 String newName,
                                 boolean varLenChanged,
                                 boolean oldVarLen,
                                 boolean newVarLen,
                                 boolean fargListChanged,
                                 long[] n2o,
                                 long[] o2n,
                                 boolean[] fargNameChanged,
                                 boolean[] fargSubRangeChanged,
                                 boolean[] fargRangeChanged,
                                 boolean[] fargDeleted,
                                 boolean[] fargInserted,
                                 java.util.Vector<FormalArgument> oldFargList,
                                 java.util.Vector<FormalArgument> newFargList)
        throws SystemErrorException
    {
        final String mName = "DataCell::updateForPVEDefChange(): ";
        DBElement dbe = null;

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( pveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "pveID invalid.");
        }

        dbe = this.getDB().idx.getElement(pveID);

        if ( ! ( dbe instanceof PredicateVocabElement ) )
        {
            throw new SystemErrorException(mName +
                                           "pveID doesn't refer to a pve.");
        }

        this.val.updateForPVEDefChange(db,
                                       pveID,
                                       nameChanged,
                                       oldName,
                                       newName,
                                       varLenChanged,
                                       oldVarLen,
                                       newVarLen,
                                       fargListChanged,
                                       n2o,
                                       o2n,
                                       fargNameChanged,
                                       fargSubRangeChanged,
                                       fargRangeChanged,
                                       fargDeleted,
                                       fargInserted,
                                       oldFargList,
                                       newFargList);

        return;

    } /* DataCell::updateForPVEDefChange() */


    /**
     * updateForPVEDeletion()
     *
     * Update the value of the cell for the deletion of the predicate vocab
     * element.  This call should be triggered by an instance of predicate
     * somewhere in the value of the cell receiving a VEDeleted()
     * message from the predicate vocab element in question.
     *
     * On entry, test to see if we are in a cascade.
     *
     * If we are not, enter the cascade, create the pending copy, and tell
     * it to update for the predicate vocab element deletion.  Make note of the
     * supplied pveID, and of the fact that we entered the cascade due to a
     * pve deletion.
     *
     * If we are already in a cascade, throw a system error unless the
     * we have already made note of our being in the cascade due to the
     * deletion the PVE indicated by the pveID parameter.  In this latter case,
     * exit without taking any action.
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForPVEDeletion(Database db,
                                        long pveID)
        throws SystemErrorException
    {
        final String mName = "DataCell::updateForPVEDeletion(): ";

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( pveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "pveID invalid.");
        }

        this.val.updateForPVEDeletion(db, pveID);

        return;

    } /* DataCell::updateForPVEDeletion() */


    /**
     * updateIndexForReplacementVal()
     *
     * When the old incarnation of the canonnical version of a DataCell is
     * replaced with the new, we must update the index so that DataValues and
     * predicates that don't appear in the new incarnation are removed from
     * the index, DataValues and Predicates that are introduced in the
     * new incarnation are inserted in the index, and the index is updated
     * to point to the new versions of DataValues and Predicates that appear
     * in both.
     *
     * Doing this in the general case is quite a chore, so we make some
     * simplifying assumptions:
     *
     * 1) The underlying MatrixVocabElement has not changed (we deal with
     *    this case elsewhere).
     *
     * 2) DataValues do not change location in the val matrix -- they are
     *    are either modified or replaced with new DataValues (that is,
     *    instances of DataValue with invalid ID and probably different
     *    type).
     *
     * 3) If a new PredDataValue appears in the val matrix, then its value
     *    is a new Predicate -- i.e. an instance of Predicate with invalid id.
     *
     * 4) If a new Predicate appears in the val matrix, then only new DataValues
     *    and Predicates may appear in its argument list.
     *
     * 5) If a Predicate changes its target PVE, then all its arguments must
     *    be new.
     *
     * Violations of these assumptions should be detected before we get this far,
     * but we should still check for violations where convienient, and throw a
     * system error if one is detected.
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateIndexForReplacementVal(DataCell oldCell)
        throws SystemErrorException
    {
        final String mName = "DataCell::updateIndexForReplacementVal(): ";

        if ( this.val == null )
        {
            throw new SystemErrorException(mName + "val is null?!?");
        }

        if ( oldCell == null )
        {
            throw new SystemErrorException(mName + "oldCell is null?!?!");
        }

        if ( oldCell.val == null )
        {
            throw new SystemErrorException(mName + "oldCell.val is null?!?");
        }

        this.val.updateIndexForReplacementVal(oldCell.val,
                                              this.getID(),
                                              oldCell.cascadeMveMod,
                                              oldCell.cascadeMveDel,
                                              oldCell.cascadeMveID,
                                              oldCell.cascadePveMod,
                                              oldCell.cascadePveDel,
                                              oldCell.cascadePveID);

        return;

    } /* DataCell::updateIndexForReplacementVal() */


    /*************************************************************************/
    /********************* Interface Implementaions: *************************/
    /*************************************************************************/

//  /**
//   * Called when the vocabulary element has been modified
//   * @param vocab the modified vocabulary element
//   */
//  public void vocabModified(VocabElement vocab)
//  {
//    // To Do
//  } //End of vocabModified() method
//
//  /**
//   * Called when a data value has been modified
//   * @param value the DataValue changed
//   */
//  public void dataValueChanged(DataValue value)
//  {
//    // To Do
//
//    // Send change notices up the queue
//    for (int i=0; i<this.cellListeners.size(); i++) {
//      ((CellChangeListener)this.cellListeners.elementAt(i)).cellModified(this);
//    }
//  } // End of dataValueChanged() method


  /**
   * Called when the database ticks per second value has changed
   * @param db the database that has been modified
   * @param oldTPS the previous ticks per second value
   */
  public void databaseTicksChanged(Database db, int oldTPS)
    throws SystemErrorException
  {

      throw new SystemErrorException("databaseTicksChanged() not implemented.");
//    // Modify onset and offset with new tcs value
//    this.onset.setTPS(db.getTicks());
//    this.offset.setTPS(db.getTicks());
//
//    // Send change notices up the queue
//    for (int i=0; i<this.cellListeners.size(); i++) {
//      ((CellChangeListener)this.cellListeners.elementAt(i)).cellModified(this);
//    }
  } //End of dataTicksChanges() method

  /**
   * Called when the database start time value has changed
   * @param db the database that has been modified
   * @param oldST the previous startTime value
   */
  public void databaseStartTimeChanged(Database db, long oldST)
  {
    // To Do
  } //End of databaseStartTimeChanged() method

  /**
   * Deletes the cell and notifies all listeners of deletion
   */
//  public void delete()
//  {
//    // To do
//
//    // Send change notices up the queue
//    for (int i=0; i<this.cellListeners.size(); i++) {
//      ((CellChangeListener)this.cellListeners.elementAt(i)).cellDeleted(this);
//    }
//  } //End of delete() method


    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Construct a data cell with the specified initialization.
     *
     * Returns a reference to the newly constructed DataCell if successful.
     * Throws a system error exception on failure.
     *
     *                                               -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static DataCell Construct(Database db,
                                     long colID,
                                     long mveID,
                                     long on,
                                     long off,
                                     Matrix val)
        throws SystemErrorException
    {
        final String mName =
                "DataCell::Construct(db, colID, mveID, on, off, val)";
        DataCell c = null;

        c = new DataCell(db, colID, mveID);

        if ( on != 0 )
        {
            c.setOnset(new TimeStamp(db.getTicks(), on));
        }

        if ( off != 0 )
        {
            c.setOffset(new TimeStamp(db.getTicks(), off));
        }

        if ( val != null )
        {
            c.setVal(val);
        }

        return c;

    } /* DataCell::Construct(db, colID, mveID, on, off, val) */

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += new Boolean(this.cascadePveDel).hashCode() * Constants.SEED1;
        hash += this.cascadePveID * Constants.SEED2;
        hash += new Boolean(this.cascadePveMod).hashCode() * Constants.SEED3;
        hash += new Boolean(this.cascadeMveDel).hashCode() * Constants.SEED4;
        hash += HashUtils.Long2H(this.cascadeMveID) * Constants.SEED5;        
        hash += new Boolean(this.cascadeMveMod).hashCode() * Constants.SEED6;
        hash += HashUtils.Obj2H(this.pending) * Constants.SEED7;
        hash += this.oldOrd * Constants.SEED8;
        hash += new Boolean(this.inCascade).hashCode() * Constants.SEED9;
        hash += HashUtils.Obj2H(this.listeners) * Constants.SEED10;
        hash += HashUtils.Obj2H(this.val) * Constants.SEED11;
        hash += HashUtils.Obj2H(this.offset) * Constants.SEED12;
        hash += HashUtils.Obj2H(this.onset) * Constants.SEED13;        
        hash += this.itsMveType.ordinal() * Constants.SEED14;
        hash += this.itsMveID * Constants.SEED15;

        return hash;
    }

    /**
     * Compares this DataCell against another object.
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

        DataCell d = (DataCell) obj;
        return super.equals(d)
            && (this.cascadePveDel == d.cascadePveDel)
            && (this.cascadePveID == d.cascadePveID)
            && (this.cascadePveMod == d.cascadePveMod)
            && (this.cascadeMveDel == d.cascadeMveDel)
            && (this.cascadeMveID == d.cascadeMveID)
            && (this.cascadeMveMod == d.cascadeMveMod)
            && (this.pending == null ? d.pending == null
                                     : this.pending.equals(d.pending))
            && (this.oldOrd == d.oldOrd)
            && (this.inCascade == d.inCascade)
            && (this.listeners == null ? d.listeners == null
                                       : this.listeners.equals(d.listeners))
            && (this.val == null ? d.val == null
                                 : this.val.equals(d.val))
            && (this.offset == null ? d.offset == null
                                    : this.offset.equals(d.offset))
            && (this.onset == null ? d.onset == null
                                   : this.onset.equals(d.onset))
            && (this.itsMveType == d.itsMveType)
            && (this.itsMveID == d.itsMveID);
    }

    @Override
    public Object clone() {
        DataCell clone;
        try {
            clone = new DataCell(this);
        } catch (SystemErrorException e) {
            clone = null;
        }

        return clone;
    }
    
    // Method to get Transfer Object for DataCell data
    public DataCellTO getDataCellData() {
      return new DataCellTO(this);
    }
    
  // method to set DataCell values with a Transfer Object
  public void setDataCellData(DataCellTO updatedDataCell) {
        try {
            this.setOnset(updatedDataCell.onset);
            this.setOffset(updatedDataCell.offset);
            int numArgs = val.getNumArgs();
            for (int i=0; i< numArgs; i++) {                        
                DataValue dv = val.getArgCopy(i);
                if ( dv instanceof TimeStampDataValue ) {
                    ((TimeStampDataValue)dv).setItsValue((TimeStamp)updatedDataCell.argList.get(i));    
                }
                else if ( dv instanceof NominalDataValue ) {
                    ((NominalDataValue)dv).setItsValue((String)updatedDataCell.argList.get(i));    
                }
                else if ( dv instanceof TextStringDataValue ) {
                    String value = (String)updatedDataCell.argList.get(i);
                    ((TextStringDataValue)dv).setItsValue(value);
                    if (value == null) {
                        ((TextStringDataValue)dv).clearValue();    
                    }
                }
                else if ( dv instanceof PredDataValue ) {
                    ((PredDataValue)dv).setItsValue((Predicate)updatedDataCell.argList.get(i));    
                }
                else if ( dv instanceof QueryVarDataValue ) {
                    ((QueryVarDataValue)dv).setItsValue((String)updatedDataCell.argList.get(i));       
                }
                else if ( dv instanceof ColPredDataValue ) { 
                    ((ColPredDataValue)dv).setItsValue((ColPred)updatedDataCell.argList.get(i));        
                }
                else if ( dv instanceof QuoteStringDataValue ) {
                    ((QuoteStringDataValue)dv).setItsValue((String)updatedDataCell.argList.get(i));      
                }
                else if ( dv instanceof FloatDataValue ) {
                    ((FloatDataValue)dv).setItsValue(((Double)updatedDataCell.argList.get(i)).doubleValue());        
                }
                else if ( dv instanceof IntDataValue ) {
                    ((IntDataValue)dv).setItsValue(((Long)updatedDataCell.argList.get(i)).longValue());       
                }
                else if ( dv instanceof UndefinedDataValue ) {
                    ((UndefinedDataValue)dv).setItsValue((String)updatedDataCell.argList.get(i));       
                }
                val.replaceArg(i, dv);
            }           
        } catch (SystemErrorException e) {
            Logger.getLogger(DataCell.class.getName()).log(Level.SEVERE, null, e);
        }
    
  }
  
  
  
} // End of DataCell class definition
