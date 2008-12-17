/*
 * DataCell.java
 *
 * Created on December 7, 2006, 5:25 PM
 *
 */

package au.com.nicta.openshapa.db;

import java.util.Collections;
import java.util.Vector;
import au.com.nicta.openshapa.db.MatrixVocabElement;

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
 *                                          JRM -- 8/24/07
 * Regular cell definition
 * @author FGA
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
     *                                              JRM -- 8/24/07  
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
     *                                      JRM -- 8/29/07
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
     *                                      JRM -- 8/29/07
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
     *                                      JRM -- 8/29/07
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
        this.offset.setTPS(db.getTicks());
        
        return;
        
    } /* DataCell::setOffset(newOffset) */

    
    /**
     * getOnset() & setOnset()
     *
     * Get and set the value of the onset field.
     *
     *                                      JRM -- 8/29/07
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
        this.onset.setTPS(db.getTicks());
        
        return;
        
    } /* DataCell::setOnset(newOnset) */

    /**
     * getVal()
     *
     * Return a copy of the current value of the data cell.
     *
     *                              JRM -- 8/29/07
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
     *                              JRM -- 8/29/07
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
     *                                              JRM -- 8/29/07
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
            this.val = new Matrix(db, this.itsMveID);
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
     *                                          JRM -- 3/15/08
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
     *                                          JRM -- 3/15/08
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
     *                                          JRM -- 3/15/08
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
     *                                          JRM -- 3/15/08
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
     *                                          JRM -- 3/15/08
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
     *                                          JRM -- 3/15/08
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
     *                                          JRM -- 3/15/08
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
     *                                          JRM -- 3/15/08
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
     *                                          JRM -- 3/15/08
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
     *                                          JRM -- 3/20/08
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
     *                                          JRM -- 3/15/08
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
     *                                              JRM -- 3/20/08
     *
     * Changes:
     *
     *    - Added the column predicate related parameters to the method's
     *      argument list.
     *                                              JRM -- 8/26/08
     */
    
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
        
        dc = new DataCell(this.db, this.itsColID, this.itsMveID);
        dc.setID(this.id);
        dc.setOrd(this.ord);
        dc.setOnset(this.onset);
        dc.setOffset(this.offset);
        
        numOldArgs = oldFargList.size();
        numNewArgs = newFargList.size();
        
        for ( i = 0; i < numOldArgs; i++ )
        {
            if ( ! fargDeleted[i] )
            {
                dv = DataValue.Copy(this.val.getArg(i), false);
              
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
     *                                          JRM -- 8/26/08
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
        final String mName = "DataCell::cascadeUpdateForPVEDefChange(): ";
        DBElement dbe = null;
        
        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }
        
        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID invalid.");
        }
        
        dbe = this.db.idx.getElement(mveID);
        
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

                dc = new DataCell(this.db, this.itsColID, this.itsMveID);
                dc.setID(this.id);
                dc.setOrd(this.ord);
                dc.setOnset(this.onset);
                dc.setOffset(this.offset);

                numOldArgs = oldFargList.size();
                numNewArgs = newFargList.size();

                for ( i = 0; i < numOldArgs; i++ )
                {
                    if ( ! fargDeleted[i] )
                    {
                        dv = DataValue.Copy(this.val.getArg(i), false);

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
                  ( this.cascadePveID != mveID ) ||
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
     * pve deletion.
     *
     * If we are already in a cascade, throw a system error unless the 
     * we have already made note of our being in the cascade due to the 
     * deletion the MVE indicated by the pveID parameter.  In this latter case, 
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
        
        if ( this.db != db )
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
        
        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }
        
        if ( pveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "pveID invalid.");
        }
        
        dbe = this.db.idx.getElement(pveID);
        
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
        
        if ( this.db != db )
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
     *                                          JRM -- 3/15/08
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
     *                                          JRM -- 5/15/08
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
     *                                          JRM -- 5/15/08
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
     *              JRM -- 3/20/08
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
     *                                          JRM -- 2/5/08
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
     *                                          JRM -- 2/5/08
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
     *                                          JRM -- 2/5/08
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
     *                                          JRM -- 2/5/08
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
     *                                          JRM -- 2/5/08
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
     *                                          JRM -- 2/5/08
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
     *                                          JRM -- 2/5/08
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
     *                                          JRM -- 2/5/08
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
     *                                          JRM -- 2/5/08
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
     *                                              JRM -- 3/24/08
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
     *                                              JRM -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void insertValInIndex()
        throws SystemErrorException
    {
        final String mName = "DataCell::insertValInIndex(): ";

        if ( this.id == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "ID not assigned?!?");
        }
        
        if ( this.val == null )
        {
            throw new SystemErrorException(mName + "val is null?!?");
        }
        
        this.val.insertInIndex(this.id);
        
        return;
        
    } /* DataCell::insertValInIndex() */
    
    /**
     * lookupDataCol()
     *
     * Given an ID, attempt to look up the associated DataColumn
     * in the database associated with the DataCell.  If there is no 
     * such DataColumn, throw  a system error.
     *
     *                                              JRM -- 11/28/07
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

        dbe = this.db.idx.getElement(colID);

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
     *                                              JRM -- 8/24/07
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

        dbe = this.db.idx.getElement(mveID);

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
     *                                              JRM -- 3/24/08
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
     *                                              JRM -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void removeValFromIndex()
        throws SystemErrorException
    {
        final String mName = "DataCell::removeValFromIndex(): ";

        if ( this.id == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "ID not assigned?!?");
        }
        
        if ( this.val == null )
        {
            throw new SystemErrorException(mName + "val is null?!?");
        }
        
        this.val.removeFromIndex(this.id);
        
        return;
        
    } /* DataCell::removeValFromIndex() */
    
    
    /**
     * validateNewCell()
     *
     * Verify that the value of a cell that is about to be inserted in 
     * the database as the first incarnation of the cell is valid for the 
     * target data column / matrix vocab element.
     *
     *                                      JRM -- 2/19/08
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
     *                                      JRM -- 2/19/08
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
     *                                              JRM -- 8/26/08
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
        
        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }
        
        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID invalid.");
        }
        
        dbe = this.db.idx.getElement(mveID);
        
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
        
        if ( this.db != db )
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
     *                                          JRM -- 8/26/08     
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
        
        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }
        
        if ( pveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "pveID invalid.");
        }
        
        dbe = this.db.idx.getElement(pveID);
        
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
        
        if ( this.db != db )
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
                                              this.id, 
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
     *                                              JRM -- 3/31/08
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

    
    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/
   
    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) Three argument constructor:
     *
     *      a) Construct a database.  Construct a column and matching matrix,
     *         and then pass the database and column and matix IDs to the
     *         three argument constructor.  Verify that all fields of the 
     *         DataCell are initialized as expected.
     *
     *         Repeat for all types of columns.
     *
     *      b) Verify that the three argument constructor fails on invalid
     *         input.  
     *
     * 2) Four argument constructor:
     *
     *      a) Construct a database.  Construct a column and matching matrix,
     *         and then pass the database and column and matix IDs along with 
     *         a comment to the four argument constructor.  Verify that all 
     *         fields of the DataCell are initialized as expected.
     *
     *         Repeat for all types of columns.
     *
     *      b) Verify that the constructor fails when passed invalid data.
     *
     * 3) Seven argument constructor:
     *
     *      As per four argument constructor, save that an onset, offset and 
     *      a cell value value are supplied to the constructor.  Verify that 
     *      the onset, offset, and value appear in the DataCell.
     *
     *      Verify that the constructor fails when passed invalid data.
     *              
     * 4) Copy constructor:
     *
     *      a) Construct DataCells of all types using the 3, 4, and 7 argument
     *         constructors with a variety of values.  
     *
     *         Now use the copy constructor to create a copies of the 
     *         DataCells, and verify that the copies are correct. 
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad 
     *         value that can be tested unless we go an manualy break some 
     *         DataCells created with the other constructors.
     *
     * 5) Accessors:
     *
     *      Verify that the getItsMveID(), getItsMveType() getOnset(), 
     *      getOffset(), getVal(), setOnset(), setOffset(), and setVal() 
     *      methods perform correctly.  Verify that the inherited accessors
     *      function correctly via calls to the Cell.TestAccessorMethods() 
     *      method.
     *
     *      Verify that the accessors fail on invalid data.
     *
     * 6) toString methods:
     *
     *      Verify that all fields are displayed correctly by the toString
     *      and toDBString() methods. 
     *
     * 
     *************************************************************************/

    /**
     * TestClassDataCell()
     *
     * Main routine for tests of class DataCell.
     *
     *                                      JRM -- 10/15/07
     *
     * Changes:
     *
     *    - Non.
     */
    
    public static boolean TestClassDataCell(java.io.PrintStream outStream,
                                                  boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing class DataCell:\n");
        
        if ( ! Test3ArgConstructor(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! Test4ArgConstructor(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! Test7ArgConstructor(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestCopyConstructor(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestAccessors(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestToStringMethods(outStream, verbose) )
        {
            failures++;
        }
       
        if ( failures > 0 )
        {
            pass = false;
            outStream.printf("%d failures in tests for class DataCell.\n\n",
                             failures);
        }
        else
        {
            outStream.print("All tests passed for class DataCell.\n\n");
        }
        
        return pass;
        
    } /* DataCell::TestClassDataCell() */
    
    
    /**
     * Test3ArgConstructor()
     * 
     * Run a battery of tests on the three argument constructor for this 
     * class, and on the instance returned.
     * 
     *                                              JRM -- 11/13/07
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public static boolean Test3ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing 3 argument constructor for class DataCell                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long f_colID = DBIndex.INVALID_ID;
        long i_colID = DBIndex.INVALID_ID;
        long m_colID = DBIndex.INVALID_ID;
        long n_colID = DBIndex.INVALID_ID;
        long p_colID = DBIndex.INVALID_ID;
        long t_colID = DBIndex.INVALID_ID;
        long f_mveID = DBIndex.INVALID_ID;
        long i_mveID = DBIndex.INVALID_ID;
        long m_mveID = DBIndex.INVALID_ID;
        long n_mveID = DBIndex.INVALID_ID;
        long p_mveID = DBIndex.INVALID_ID;
        long t_mveID = DBIndex.INVALID_ID;
        MatrixVocabElement f_mve = null;
        MatrixVocabElement i_mve = null;
        MatrixVocabElement m_mve = null;
        MatrixVocabElement n_mve = null;
        MatrixVocabElement p_mve = null;
        MatrixVocabElement t_mve = null;
        DataColumn f_col = null;
        DataColumn i_col = null;
        DataColumn m_col = null;
        DataColumn n_col = null;
        DataColumn p_col = null;
        DataColumn t_col = null;
        DataCell f_cell = null;
        DataCell i_cell = null;
        DataCell m_cell = null;
        DataCell n_cell = null;
        DataCell p_cell = null;
        DataCell t_cell = null;
        DataCell c = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();
            
            f_col = new DataColumn(db, "f_col", 
                                   MatrixVocabElement.MatrixType.FLOAT);
            f_colID = db.addColumn(f_col);
            f_col = db.getDataColumn(f_colID);
            f_mveID = f_col.getItsMveID();
            f_mve = db.getMatrixVE(f_mveID);
            
            
            i_col = new DataColumn(db, "i_col", 
                                   MatrixVocabElement.MatrixType.INTEGER);
            i_colID = db.addColumn(i_col);
            i_col = db.getDataColumn(i_colID);
            i_mveID = i_col.getItsMveID();
            i_mve = db.getMatrixVE(i_mveID);
            
            
            m_col = new DataColumn(db, "m_col", 
                                   MatrixVocabElement.MatrixType.MATRIX);
            m_colID = db.addColumn(m_col);
            m_col = db.getDataColumn(m_colID);
            m_mveID = m_col.getItsMveID();
            m_mve = db.getMatrixVE(m_mveID);
            
            
            n_col = new DataColumn(db, "n_col", 
                                   MatrixVocabElement.MatrixType.NOMINAL);
            n_colID = db.addColumn(n_col);
            n_col = db.getDataColumn(n_colID);
            n_mveID = n_col.getItsMveID();
            n_mve = db.getMatrixVE(n_mveID);
            
            
            p_col = new DataColumn(db, "p_col", 
                                   MatrixVocabElement.MatrixType.PREDICATE);
            p_colID = db.addColumn(p_col);
            p_col = db.getDataColumn(p_colID);
            p_mveID = p_col.getItsMveID();
            p_mve = db.getMatrixVE(p_mveID);
            
            
            t_col = new DataColumn(db, "t_col", 
                                   MatrixVocabElement.MatrixType.TEXT);
            t_colID = db.addColumn(t_col);
            t_col = db.getDataColumn(t_colID);
            t_mveID = t_col.getItsMveID();
            t_mve = db.getMatrixVE(t_mveID);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( f_colID == DBIndex.INVALID_ID ) ||
             ( f_col == null ) ||
             ( f_mveID == DBIndex.INVALID_ID ) ||
             ( f_mve == null ) ||
             ( i_colID == DBIndex.INVALID_ID ) ||
             ( i_col == null ) ||
             ( i_mveID == DBIndex.INVALID_ID ) ||
             ( i_mve == null ) ||
             ( m_colID == DBIndex.INVALID_ID ) ||
             ( m_col == null ) ||
             ( m_mveID == DBIndex.INVALID_ID ) ||
             ( m_mve == null ) ||
             ( n_colID == DBIndex.INVALID_ID ) ||
             ( n_col == null ) ||
             ( n_mveID == DBIndex.INVALID_ID ) ||
             ( n_mve == null ) ||
             ( p_colID == DBIndex.INVALID_ID ) ||
             ( p_col == null ) ||
             ( p_mveID == DBIndex.INVALID_ID ) ||
             ( p_mve == null ) ||
             ( t_colID == DBIndex.INVALID_ID ) ||
             ( t_col == null ) ||
             ( t_mveID == DBIndex.INVALID_ID ) ||
             ( t_mve == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) ) 
        {
            failures++;
            
            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }
                
                if ( ( f_colID == DBIndex.INVALID_ID ) ||
                     ( f_col == null ) ||
                     ( f_mveID == DBIndex.INVALID_ID ) ||
                     ( f_mve == null ) )
                {
                    outStream.printf("Errors allocating f_col. f_colID = %d, " +
                                     "f_mveID = %d.\n", f_colID, f_mveID);
                }
                
                if ( ( i_colID == DBIndex.INVALID_ID ) ||
                     ( i_col == null ) ||
                     ( i_mveID == DBIndex.INVALID_ID ) ||
                     ( i_mve == null ) )
                {
                    outStream.printf("Errors allocating i_col. i_colID = %d, " +
                                     "i_mveID = %d.\n", i_colID, i_mveID);
                }
                
                if ( ( m_colID == DBIndex.INVALID_ID ) ||
                     ( m_col == null ) ||
                     ( m_mveID == DBIndex.INVALID_ID ) ||
                     ( m_mve == null ) )
                {
                    outStream.printf("Errors allocating m_col. m_colID = %d, " +
                                     "m_mveID = %d.\n", m_colID, m_mveID);
                }
                
                if ( ( n_colID == DBIndex.INVALID_ID ) ||
                     ( n_col == null ) ||
                     ( n_mveID == DBIndex.INVALID_ID ) ||
                     ( n_mve == null ) )
                {
                    outStream.printf("Errors allocating n_col. n_colID = %d, " +
                                     "n_mveID = %d.\n", n_colID, n_mveID);
                }
                
                if ( ( p_colID == DBIndex.INVALID_ID ) ||
                     ( p_col == null ) ||
                     ( p_mveID == DBIndex.INVALID_ID ) ||
                     ( p_mve == null ) )
                {
                    outStream.printf("Errors allocating p_col. p_colID = %d, " +
                                     "p_mveID = %d.\n", p_colID, p_mveID);
                }
                
                if ( ( t_colID == DBIndex.INVALID_ID ) ||
                     ( t_col == null ) ||
                     ( t_mveID == DBIndex.INVALID_ID ) ||
                     ( t_mve == null ) )
                {
                    outStream.printf("Errors allocating t_col. t_colID = %d, " +
                                     "t_mveID = %d.\n", t_colID, t_mveID);
                }
                
                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_cell = new DataCell(db, f_colID, f_mveID);
                i_cell = new DataCell(db, i_colID, i_mveID);
                m_cell = new DataCell(db, m_colID, m_mveID);
                n_cell = new DataCell(db, n_colID, n_mveID);
                p_cell = new DataCell(db, p_colID, p_mveID);
                t_cell = new DataCell(db, t_colID, t_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_cell == null ) ||
                 ( i_cell == null ) ||
                 ( m_cell == null ) ||
                 ( n_cell == null ) ||
                 ( p_cell == null ) ||
                 ( t_cell == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( f_cell == null )
                    {
                        outStream.printf("f_cell allocation failed.\n");
                    }
                    
                    if ( i_cell == null )
                    {
                        outStream.printf("i_cell allocation failed.\n");
                    }
                    
                    if ( m_cell == null )
                    {
                        outStream.printf("m_cell allocation failed.\n");
                    }
                    
                    if ( n_cell == null )
                    {
                        outStream.printf("n_cell allocation failed.\n");
                    }
                    
                    if ( p_cell == null )
                    {
                        outStream.printf("p_cell allocation failed.\n");
                    }
                    
                    if ( t_cell == null )
                    {
                        outStream.printf("t_cell allocation failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyInitialization(db,
                                     f_cell,
                                     "f_cell",
                                     null,
                                     f_colID,
                                     f_mveID,
                                     MatrixVocabElement.MatrixType.FLOAT,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, f_mveID),
                                     outStream,
                                     verbose);
                
                failures += VerifyInitialization(db,
                                     i_cell,
                                     "i_cell",
                                     null,
                                     i_colID,
                                     i_mveID,
                                     MatrixVocabElement.MatrixType.INTEGER,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, i_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     m_cell,
                                     "m_cell",
                                     null,
                                     m_colID,
                                     m_mveID,
                                     MatrixVocabElement.MatrixType.MATRIX,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, m_mveID),
                                     outStream,
                                     verbose);
                
                failures += VerifyInitialization(db,
                                     n_cell,
                                     "n_cell",
                                     null,
                                     n_colID,
                                     n_mveID,
                                     MatrixVocabElement.MatrixType.NOMINAL,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, n_mveID),
                                     outStream,
                                     verbose);
                
                failures += VerifyInitialization(db,
                                     p_cell,
                                     "p_cell",
                                     null,
                                     p_colID,
                                     p_mveID,
                                     MatrixVocabElement.MatrixType.PREDICATE,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, p_mveID),
                                     outStream,
                                     verbose);
                
                failures += VerifyInitialization(db,
                                     t_cell,
                                     "t_cell",
                                     null,
                                     t_colID,
                                     t_mveID,
                                     MatrixVocabElement.MatrixType.TEXT,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, t_mveID),
                                     outStream,
                                     verbose);
            }
        }
        
        /* Now verify that the constructor fails on invalid input */

        /* verify that it fails on a null db */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(null, f_colID, f_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(null, f_colID, " +
                                         "f_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(null, f_colID, " +
                                         "f_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(null, f_colID, " +
                                "f_mveID) failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on a db that doesn't match the supplied IDs */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(new ODBCDatabase(), f_colID, f_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "f_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "f_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                "f_mveID) failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an invalid colID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, DBIndex.INVALID_ID, f_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, invalid, " +
                                         "f_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, invalid, " +
                                         "f_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, invalid, " +
                                "f_mveID) failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an invalid mveID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, f_colID, DBIndex.INVALID_ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "invalid) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "invalid) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                "invalid) failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }
        
        /* verify that it fails on an ID mismatch */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, f_colID, i_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "i_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "i_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                "i_mveID) failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }
        
        /* verify that it fails on a bad colID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, f_mveID, i_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, f_mveID, " +
                                         "i_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, f_mveID, " +
                                         "i_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, f_mveID, " +
                                "i_mveID) failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }
        
        /* verify that it fails on a bad mveID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, f_colID, i_colID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "i_colID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                         "i_colID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, f_colID, " +
                                "i_colID) failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }
        
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }
        
        return pass;
        
    } /* DataCell::Test3ArgConstructor() */
    
    
    /**
     * Test4ArgConstructor()
     * 
     * Run a battery of tests on the four argument constructor for this 
     * class, and on the instance returned.
     * 
     *                                              JRM -- 11/13/07
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public static boolean Test4ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing 4 argument constructor for class DataCell                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long f_colID = DBIndex.INVALID_ID;
        long i_colID = DBIndex.INVALID_ID;
        long m_colID = DBIndex.INVALID_ID;
        long n_colID = DBIndex.INVALID_ID;
        long p_colID = DBIndex.INVALID_ID;
        long t_colID = DBIndex.INVALID_ID;
        long f_mveID = DBIndex.INVALID_ID;
        long i_mveID = DBIndex.INVALID_ID;
        long m_mveID = DBIndex.INVALID_ID;
        long n_mveID = DBIndex.INVALID_ID;
        long p_mveID = DBIndex.INVALID_ID;
        long t_mveID = DBIndex.INVALID_ID;
        MatrixVocabElement f_mve = null;
        MatrixVocabElement i_mve = null;
        MatrixVocabElement m_mve = null;
        MatrixVocabElement n_mve = null;
        MatrixVocabElement p_mve = null;
        MatrixVocabElement t_mve = null;
        DataColumn f_col = null;
        DataColumn i_col = null;
        DataColumn m_col = null;
        DataColumn n_col = null;
        DataColumn p_col = null;
        DataColumn t_col = null;
        DataCell f_cell = null;
        DataCell i_cell = null;
        DataCell m_cell = null;
        DataCell n_cell = null;
        DataCell p_cell = null;
        DataCell t_cell = null;
        DataCell c = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();
            
            f_col = new DataColumn(db, "f_col", 
                                   MatrixVocabElement.MatrixType.FLOAT);
            f_colID = db.addColumn(f_col);
            f_col = db.getDataColumn(f_colID);
            f_mveID = f_col.getItsMveID();
            f_mve = db.getMatrixVE(f_mveID);
            
            
            i_col = new DataColumn(db, "i_col", 
                                   MatrixVocabElement.MatrixType.INTEGER);
            i_colID = db.addColumn(i_col);
            i_col = db.getDataColumn(i_colID);
            i_mveID = i_col.getItsMveID();
            i_mve = db.getMatrixVE(i_mveID);
            
            
            m_col = new DataColumn(db, "m_col", 
                                   MatrixVocabElement.MatrixType.MATRIX);
            m_colID = db.addColumn(m_col);
            m_col = db.getDataColumn(m_colID);
            m_mveID = m_col.getItsMveID();
            m_mve = db.getMatrixVE(m_mveID);
            
            
            n_col = new DataColumn(db, "n_col", 
                                   MatrixVocabElement.MatrixType.NOMINAL);
            n_colID = db.addColumn(n_col);
            n_col = db.getDataColumn(n_colID);
            n_mveID = n_col.getItsMveID();
            n_mve = db.getMatrixVE(n_mveID);
            
            
            p_col = new DataColumn(db, "p_col", 
                                   MatrixVocabElement.MatrixType.PREDICATE);
            p_colID = db.addColumn(p_col);
            p_col = db.getDataColumn(p_colID);
            p_mveID = p_col.getItsMveID();
            p_mve = db.getMatrixVE(p_mveID);
            
            
            t_col = new DataColumn(db, "t_col", 
                                   MatrixVocabElement.MatrixType.TEXT);
            t_colID = db.addColumn(t_col);
            t_col = db.getDataColumn(t_colID);
            t_mveID = t_col.getItsMveID();
            t_mve = db.getMatrixVE(t_mveID);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( f_colID == DBIndex.INVALID_ID ) ||
             ( f_col == null ) ||
             ( f_mveID == DBIndex.INVALID_ID ) ||
             ( f_mve == null ) ||
             ( i_colID == DBIndex.INVALID_ID ) ||
             ( i_col == null ) ||
             ( i_mveID == DBIndex.INVALID_ID ) ||
             ( i_mve == null ) ||
             ( m_colID == DBIndex.INVALID_ID ) ||
             ( m_col == null ) ||
             ( m_mveID == DBIndex.INVALID_ID ) ||
             ( m_mve == null ) ||
             ( n_colID == DBIndex.INVALID_ID ) ||
             ( n_col == null ) ||
             ( n_mveID == DBIndex.INVALID_ID ) ||
             ( n_mve == null ) ||
             ( p_colID == DBIndex.INVALID_ID ) ||
             ( p_col == null ) ||
             ( p_mveID == DBIndex.INVALID_ID ) ||
             ( p_mve == null ) ||
             ( t_colID == DBIndex.INVALID_ID ) ||
             ( t_col == null ) ||
             ( t_mveID == DBIndex.INVALID_ID ) ||
             ( t_mve == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) ) 
        {
            failures++;
            
            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }
                
                if ( ( f_colID == DBIndex.INVALID_ID ) ||
                     ( f_col == null ) ||
                     ( f_mveID == DBIndex.INVALID_ID ) ||
                     ( f_mve == null ) )
                {
                    outStream.printf("Errors allocating f_col. f_colID = %d, " +
                                     "f_mveID = %d.\n", f_colID, f_mveID);
                }
                
                if ( ( i_colID == DBIndex.INVALID_ID ) ||
                     ( i_col == null ) ||
                     ( i_mveID == DBIndex.INVALID_ID ) ||
                     ( i_mve == null ) )
                {
                    outStream.printf("Errors allocating i_col. i_colID = %d, " +
                                     "i_mveID = %d.\n", i_colID, i_mveID);
                }
                
                if ( ( m_colID == DBIndex.INVALID_ID ) ||
                     ( m_col == null ) ||
                     ( m_mveID == DBIndex.INVALID_ID ) ||
                     ( m_mve == null ) )
                {
                    outStream.printf("Errors allocating m_col. m_colID = %d, " +
                                     "m_mveID = %d.\n", m_colID, m_mveID);
                }
                
                if ( ( n_colID == DBIndex.INVALID_ID ) ||
                     ( n_col == null ) ||
                     ( n_mveID == DBIndex.INVALID_ID ) ||
                     ( n_mve == null ) )
                {
                    outStream.printf("Errors allocating n_col. n_colID = %d, " +
                                     "n_mveID = %d.\n", n_colID, n_mveID);
                }
                
                if ( ( p_colID == DBIndex.INVALID_ID ) ||
                     ( p_col == null ) ||
                     ( p_mveID == DBIndex.INVALID_ID ) ||
                     ( p_mve == null ) )
                {
                    outStream.printf("Errors allocating p_col. p_colID = %d, " +
                                     "p_mveID = %d.\n", p_colID, p_mveID);
                }
                
                if ( ( t_colID == DBIndex.INVALID_ID ) ||
                     ( t_col == null ) ||
                     ( t_mveID == DBIndex.INVALID_ID ) ||
                     ( t_mve == null ) )
                {
                    outStream.printf("Errors allocating t_col. t_colID = %d, " +
                                     "t_mveID = %d.\n", t_colID, t_mveID);
                }
                
                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, null, f_colID, f_mveID);
                f_cell = new DataCell(db, "f_cell", f_colID, f_mveID);
                i_cell = new DataCell(db, "i_cell", i_colID, i_mveID);
                m_cell = new DataCell(db, "m_cell", m_colID, m_mveID);
                n_cell = new DataCell(db, "n_cell", n_colID, n_mveID);
                p_cell = new DataCell(db, "p_cell", p_colID, p_mveID);
                t_cell = new DataCell(db, "t_cell", t_colID, t_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c == null ) ||
                 ( f_cell == null ) ||
                 ( i_cell == null ) ||
                 ( m_cell == null ) ||
                 ( n_cell == null ) ||
                 ( p_cell == null ) ||
                 ( t_cell == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c == null )
                    {
                        outStream.printf("c allocation failed.\n");
                    }

                    if ( f_cell == null )
                    {
                        outStream.printf("f_cell allocation failed.\n");
                    }
                    
                    if ( i_cell == null )
                    {
                        outStream.printf("i_cell allocation failed.\n");
                    }
                    
                    if ( m_cell == null )
                    {
                        outStream.printf("m_cell allocation failed.\n");
                    }
                    
                    if ( n_cell == null )
                    {
                        outStream.printf("n_cell allocation failed.\n");
                    }
                    
                    if ( p_cell == null )
                    {
                        outStream.printf("p_cell allocation failed.\n");
                    }
                    
                    if ( t_cell == null )
                    {
                        outStream.printf("t_cell allocation failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyInitialization(db,
                                     c,
                                     "c",
                                     null,
                                     f_colID,
                                     f_mveID,
                                     MatrixVocabElement.MatrixType.FLOAT,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, f_mveID),
                                     outStream,
                                     verbose);
                
                failures += VerifyInitialization(db,
                                     f_cell,
                                     "f_cell",
                                     "f_cell",
                                     f_colID,
                                     f_mveID,
                                     MatrixVocabElement.MatrixType.FLOAT,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, f_mveID),
                                     outStream,
                                     verbose);
                
                failures += VerifyInitialization(db,
                                     i_cell,
                                     "i_cell",
                                     "i_cell",
                                     i_colID,
                                     i_mveID,
                                     MatrixVocabElement.MatrixType.INTEGER,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, i_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     m_cell,
                                     "m_cell",
                                     "m_cell",
                                     m_colID,
                                     m_mveID,
                                     MatrixVocabElement.MatrixType.MATRIX,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, m_mveID),
                                     outStream,
                                     verbose);

                failures += VerifyInitialization(db,
                                     n_cell,
                                     "n_cell",
                                     "n_cell",
                                     n_colID,
                                     n_mveID,
                                     MatrixVocabElement.MatrixType.NOMINAL,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, n_mveID),
                                     outStream,
                                     verbose);
                
                failures += VerifyInitialization(db,
                                     p_cell,
                                     "p_cell",
                                     "p_cell",
                                     p_colID,
                                     p_mveID,
                                     MatrixVocabElement.MatrixType.PREDICATE,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, p_mveID),
                                     outStream,
                                     verbose);
                
                failures += VerifyInitialization(db,
                                     t_cell,
                                     "t_cell",
                                     "t_cell",
                                     t_colID,
                                     t_mveID,
                                     MatrixVocabElement.MatrixType.TEXT,
                                     -1,
                                     new TimeStamp(db.getTicks(), 0),
                                     new TimeStamp(db.getTicks(), 0),
                                     new Matrix(db, t_mveID),
                                     outStream,
                                     verbose);
            }
        }
        
        /* Now verify that the constructor fails on invalid input */

        /* verify that it fails on a null db */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(null, "valid", f_colID, f_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(null, \"valid\", " +
                                "f_colID, f_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(null, \"valid\", " +
                                "f_colID, f_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(null, \"valid\", " +
                                "f_colID, f_mveID) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on a db that doesn't match the supplied IDs */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(new ODBCDatabase(), "valid", f_colID, f_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, f_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, f_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, f_mveID) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an invalid colID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, "valid", DBIndex.INVALID_ID, f_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "invalid, f_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "invalid, f_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "invalid, f_mveID) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }

        /* verify that it fails on an invalid mveID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, "valid", f_colID, DBIndex.INVALID_ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, invalid) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, invalid) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, invalid) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }
        
        /* verify that it fails on an ID mismatch */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, "valid", f_colID, i_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, i_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, i_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, i_mveID) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }
        
        /* verify that it fails on a bad colID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, "valid", f_mveID, i_mveID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_mveID, i_mveID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_mveID, i_mveID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_mveID, i_mveID) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }
        
        /* verify that it fails on a bad mveID */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(db, "valid", f_colID, i_colID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, i_colID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, i_colID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(inv_db, \"valid\", " +
                                "f_colID, i_colID) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }
        
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }
        
        return pass;
        
    } /* DataCell::Test4ArgConstructor() */
    
    
    /**
     * Test7ArgConstructor()
     * 
     * Run a battery of tests on the seven argument constructor for this 
     * class, and on the instance returned.
     * 
     *                                              JRM -- 11/13/07
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public static boolean Test7ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing 7 argument constructor for class DataCell                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long pve0ID = DBIndex.INVALID_ID;
        long f_colID = DBIndex.INVALID_ID;
        long i_colID = DBIndex.INVALID_ID;
        long m_colID = DBIndex.INVALID_ID;
        long n_colID = DBIndex.INVALID_ID;
        long p_colID = DBIndex.INVALID_ID;
        long t_colID = DBIndex.INVALID_ID;
        long f_mveID = DBIndex.INVALID_ID;
        long i_mveID = DBIndex.INVALID_ID;
        long m_mveID = DBIndex.INVALID_ID;
        long n_mveID = DBIndex.INVALID_ID;
        long p_mveID = DBIndex.INVALID_ID;
        long t_mveID = DBIndex.INVALID_ID;
        long fargID;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement f_mve = null;
        MatrixVocabElement i_mve = null;
        MatrixVocabElement m_mve = null;
        MatrixVocabElement n_mve = null;
        MatrixVocabElement p_mve = null;
        MatrixVocabElement t_mve = null;
        DataColumn f_col = null;
        DataColumn i_col = null;
        DataColumn m_col = null;
        DataColumn n_col = null;
        DataColumn p_col = null;
        DataColumn t_col = null;
        TimeStamp f_onset = null;
        TimeStamp f_offset = null;
        TimeStamp i_onset = null;
        TimeStamp i_offset = null;
        TimeStamp m_onset = null;
        TimeStamp m_offset = null;
        TimeStamp n_onset = null;
        TimeStamp n_offset = null;
        TimeStamp p_onset = null;
        TimeStamp p_offset = null;
        TimeStamp t_onset = null;
        TimeStamp t_offset = null;
        FormalArgument farg = null;
        DataValue arg = null;
        Vector<DataValue> f_arg_list = null;
        Vector<DataValue> i_arg_list = null;
        Vector<DataValue> m_arg_list = null;
        Vector<DataValue> n_arg_list = null;
        Vector<DataValue> p_arg_list = null;
        Vector<DataValue> t_arg_list = null;
        Matrix f_matrix = null;
        Matrix i_matrix = null;
        Matrix m_matrix = null;
        Matrix n_matrix = null;
        Matrix p_matrix = null;
        Matrix t_matrix = null;
        DataCell f_cell = null;
        DataCell i_cell = null;
        DataCell m_cell = null;
        DataCell n_cell = null;
        DataCell p_cell = null;
        DataCell t_cell = null;
        DataCell c = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0ID);
            
            f_col = new DataColumn(db, "f_col", 
                                   MatrixVocabElement.MatrixType.FLOAT);
            f_colID = db.addColumn(f_col);
            f_col = db.getDataColumn(f_colID);
            f_mveID = f_col.getItsMveID();
            f_mve = db.getMatrixVE(f_mveID);
            f_onset = new TimeStamp(db.getTicks(), 60);
            f_offset = new TimeStamp(db.getTicks(), 120);
            f_arg_list = new Vector<DataValue>();
            fargID = f_mve.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            f_arg_list.add(arg);
            f_matrix = new Matrix(db, f_mveID, f_arg_list);
            
            
            i_col = new DataColumn(db, "i_col", 
                                   MatrixVocabElement.MatrixType.INTEGER);
            i_colID = db.addColumn(i_col);
            i_col = db.getDataColumn(i_colID);
            i_mveID = i_col.getItsMveID();
            i_mve = db.getMatrixVE(i_mveID);
            i_onset = new TimeStamp(db.getTicks(), 180);
            i_offset = new TimeStamp(db.getTicks(), 240);
            i_arg_list = new Vector<DataValue>();
            fargID = i_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 22);
            i_arg_list.add(arg);
            i_matrix = new Matrix(db, i_mveID, i_arg_list);
            
            
            m_col = new DataColumn(db, "m_col", 
                                   MatrixVocabElement.MatrixType.MATRIX);
            m_colID = db.addColumn(m_col);
            m_col = db.getDataColumn(m_colID);
            m_mveID = m_col.getItsMveID();
            m_mve = db.getMatrixVE(m_mveID);
            m_onset = new TimeStamp(db.getTicks(), 300);
            m_offset = new TimeStamp(db.getTicks(), 360);
            m_arg_list = new Vector<DataValue>();
            fargID = m_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 33);
            m_arg_list.add(arg);
            m_matrix = new Matrix(db, m_mveID, m_arg_list);
            
            
            n_col = new DataColumn(db, "n_col", 
                                   MatrixVocabElement.MatrixType.NOMINAL);
            n_colID = db.addColumn(n_col);
            n_col = db.getDataColumn(n_colID);
            n_mveID = n_col.getItsMveID();
            n_mve = db.getMatrixVE(n_mveID);
            n_onset = new TimeStamp(db.getTicks(), 420);
            n_offset = new TimeStamp(db.getTicks(), 480);
            n_arg_list = new Vector<DataValue>();
            fargID = n_mve.getFormalArg(0).getID();
            arg = new NominalDataValue(db, fargID, "a_nominal");
            n_arg_list.add(arg);
            n_matrix = new Matrix(db, n_mveID, n_arg_list);
            
            
            p_col = new DataColumn(db, "p_col", 
                                   MatrixVocabElement.MatrixType.PREDICATE);
            p_colID = db.addColumn(p_col);
            p_col = db.getDataColumn(p_colID);
            p_mveID = p_col.getItsMveID();
            p_mve = db.getMatrixVE(p_mveID);
            p_onset = new TimeStamp(db.getTicks(), 540);
            p_offset = new TimeStamp(db.getTicks(), 600);
            p_arg_list = new Vector<DataValue>();
            fargID = p_mve.getFormalArg(0).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
            p_arg_list.add(arg);
            p_matrix = new Matrix(db, p_mveID, p_arg_list);
            
            
            t_col = new DataColumn(db, "t_col", 
                                   MatrixVocabElement.MatrixType.TEXT);
            t_colID = db.addColumn(t_col);
            t_col = db.getDataColumn(t_colID);
            t_mveID = t_col.getItsMveID();
            t_mve = db.getMatrixVE(t_mveID);
            t_onset = new TimeStamp(db.getTicks(), 660);
            t_offset = new TimeStamp(db.getTicks(), 720);
            t_arg_list = new Vector<DataValue>();
            fargID = t_mve.getFormalArg(0).getID();
            arg = new TextStringDataValue(db, fargID, "a text string");
            t_arg_list.add(arg);
            t_matrix = new Matrix(db, t_mveID, t_arg_list);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( f_colID == DBIndex.INVALID_ID ) ||
             ( f_col == null ) ||
             ( f_mveID == DBIndex.INVALID_ID ) ||
             ( f_mve == null ) ||
             ( f_onset == null ) ||
             ( f_offset == null ) ||
             ( f_arg_list == null ) ||
             ( f_matrix == null ) ||
             ( i_colID == DBIndex.INVALID_ID ) ||
             ( i_col == null ) ||
             ( i_mveID == DBIndex.INVALID_ID ) ||
             ( i_mve == null ) ||
             ( i_onset == null ) ||
             ( i_offset == null ) ||
             ( i_arg_list == null ) ||
             ( i_matrix == null ) ||
             ( m_colID == DBIndex.INVALID_ID ) ||
             ( m_col == null ) ||
             ( m_mveID == DBIndex.INVALID_ID ) ||
             ( m_mve == null ) ||
             ( m_onset == null ) ||
             ( m_offset == null ) ||
             ( m_arg_list == null ) ||
             ( m_matrix == null ) ||
             ( n_colID == DBIndex.INVALID_ID ) ||
             ( n_col == null ) ||
             ( n_mveID == DBIndex.INVALID_ID ) ||
             ( n_mve == null ) ||
             ( n_onset == null ) ||
             ( n_offset == null ) ||
             ( n_arg_list == null ) ||
             ( n_matrix == null ) ||
             ( p_colID == DBIndex.INVALID_ID ) ||
             ( p_col == null ) ||
             ( p_mveID == DBIndex.INVALID_ID ) ||
             ( p_mve == null ) ||
             ( p_onset == null ) ||
             ( p_offset == null ) ||
             ( p_arg_list == null ) ||
             ( p_matrix == null ) ||
             ( t_colID == DBIndex.INVALID_ID ) ||
             ( t_col == null ) ||
             ( t_mveID == DBIndex.INVALID_ID ) ||
             ( t_mve == null ) ||
             ( t_onset == null ) ||
             ( t_offset == null ) ||
             ( t_arg_list == null ) ||
             ( t_matrix == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) ) 
        {
            failures++;
            
            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( ( pve0 == null ) ||
                     ( pve0ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve0.  pve0ID = %d.\n",
                                     pve0ID);
                }
                
                if ( ( f_colID == DBIndex.INVALID_ID ) ||
                     ( f_col == null ) ||
                     ( f_mveID == DBIndex.INVALID_ID ) ||
                     ( f_mve == null ) )
                {
                    outStream.printf("Errors allocating f_col. f_colID = %d, " +
                                     "f_mveID = %d.\n", f_colID, f_mveID);
                }
                
                if ( ( f_onset == null ) || ( f_offset == null ) )
                {
                    outStream.printf(
                            "allocation of f_onset and/or f_offset failed.\n");
                }
                
                if ( ( f_arg_list == null ) || ( f_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of f_matrix.\n");
                }
                
                if ( ( i_colID == DBIndex.INVALID_ID ) ||
                     ( i_col == null ) ||
                     ( i_mveID == DBIndex.INVALID_ID ) ||
                     ( i_mve == null ) )
                {
                    outStream.printf("Errors allocating i_col. i_colID = %d, " +
                                     "i_mveID = %d.\n", i_colID, i_mveID);
                }
                
                if ( ( i_onset == null ) || ( i_offset == null ) )
                {
                    outStream.printf(
                            "allocation of i_onset and/or i_offset failed.\n");
                }
                
                if ( ( i_arg_list == null ) || ( i_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of i_matrix.\n");
                }
                
                if ( ( m_colID == DBIndex.INVALID_ID ) ||
                     ( m_col == null ) ||
                     ( m_mveID == DBIndex.INVALID_ID ) ||
                     ( m_mve == null ) )
                {
                    outStream.printf("Errors allocating m_col. m_colID = %d, " +
                                     "m_mveID = %d.\n", m_colID, m_mveID);
                }
                
                if ( ( m_onset == null ) || ( m_offset == null ) )
                {
                    outStream.printf(
                            "allocation of m_onset and/or m_offset failed.\n");
                }
                
                if ( ( m_arg_list == null ) || ( m_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of m_matrix.\n");
                }
                
                if ( ( n_colID == DBIndex.INVALID_ID ) ||
                     ( n_col == null ) ||
                     ( n_mveID == DBIndex.INVALID_ID ) ||
                     ( n_mve == null ) )
                {
                    outStream.printf("Errors allocating n_col. n_colID = %d, " +
                                     "n_mveID = %d.\n", n_colID, n_mveID);
                }
                
                if ( ( n_onset == null ) || ( n_offset == null ) )
                {
                    outStream.printf(
                            "allocation of n_onset and/or n_offset failed.\n");
                }
                
                if ( ( n_arg_list == null ) || ( n_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of n_matrix.\n");
                }
                
                if ( ( p_colID == DBIndex.INVALID_ID ) ||
                     ( p_col == null ) ||
                     ( p_mveID == DBIndex.INVALID_ID ) ||
                     ( p_mve == null ) )
                {
                    outStream.printf("Errors allocating p_col. p_colID = %d, " +
                                     "p_mveID = %d.\n", p_colID, p_mveID);
                }
                
                if ( ( p_onset == null ) || ( p_offset == null ) )
                {
                    outStream.printf(
                            "allocation of p_onset and/or p_offset failed.\n");
                }
                
                if ( ( p_arg_list == null ) || ( p_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of p_matrix.\n");
                }
                
                if ( ( t_colID == DBIndex.INVALID_ID ) ||
                     ( t_col == null ) ||
                     ( t_mveID == DBIndex.INVALID_ID ) ||
                     ( t_mve == null ) )
                {
                    outStream.printf("Errors allocating t_col. t_colID = %d, " +
                                     "t_mveID = %d.\n", t_colID, t_mveID);
                }
                
                if ( ( t_onset == null ) || ( t_offset == null ) )
                {
                    outStream.printf(
                            "allocation of t_onset and/or t_offset failed.\n");
                }
                
                if ( ( t_arg_list == null ) || ( t_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of t_matrix.\n");
                }
                
                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                // The tests here may seem a bit cursory.  However, we have 
                // tested the TimeStamp and Matrix classes extensively
                // elsewhere.  Thus all that we need to do here is verify that
                // the desirecd initializations are being passed through 
                // correctly.
                
                f_cell = new DataCell(db, "f_cell", f_colID, f_mveID, 
                                      f_onset, f_offset, f_matrix);
                i_cell = new DataCell(db, "i_cell", i_colID, i_mveID, 
                                      i_onset, i_offset, i_matrix);
                m_cell = new DataCell(db, "m_cell", m_colID, m_mveID,
                                      m_onset, m_offset, m_matrix);
                n_cell = new DataCell(db, "n_cell", n_colID, n_mveID,
                                      n_onset, n_offset, n_matrix);
                p_cell = new DataCell(db, "p_cell", p_colID, p_mveID,
                                      p_onset, p_offset, p_matrix);
                t_cell = new DataCell(db, "t_cell", t_colID, t_mveID,
                                      t_onset, t_offset, t_matrix);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_cell == null ) ||
                 ( i_cell == null ) ||
                 ( m_cell == null ) ||
                 ( n_cell == null ) ||
                 ( p_cell == null ) ||
                 ( t_cell == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( f_cell == null )
                    {
                        outStream.printf("f_cell allocation failed.\n");
                    }
                    
                    if ( i_cell == null )
                    {
                        outStream.printf("i_cell allocation failed.\n");
                    }
                    
                    if ( m_cell == null )
                    {
                        outStream.printf("m_cell allocation failed.\n");
                    }
                    
                    if ( n_cell == null )
                    {
                        outStream.printf("n_cell allocation failed.\n");
                    }
                    
                    if ( p_cell == null )
                    {
                        outStream.printf("p_cell allocation failed.\n");
                    }
                    
                    if ( t_cell == null )
                    {
                        outStream.printf("t_cell allocation failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyInitialization(db,
                                       f_cell,
                                       "f_cell",
                                       "f_cell",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       f_onset,
                                       f_offset,
                                       f_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       i_cell,
                                       "i_cell",
                                       "i_cell",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       i_onset,
                                       i_offset,
                                       i_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell,
                                       "m_cell",
                                       "m_cell",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       m_onset,
                                       m_offset,
                                       m_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       n_cell,
                                       "n_cell",
                                       "n_cell",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       n_onset,
                                       n_offset,
                                       n_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                      p_cell,
                                      "p_cell",
                                      "p_cell",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       p_onset,
                                       p_offset,
                                       p_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       t_cell,
                                       "t_cell",
                                       "t_cell",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       t_onset,
                                       t_offset,
                                       t_matrix,
                                       outStream,
                                       verbose);
            }
        }
        
        /* Now verify that the constructor fails on invalid input */

        if ( failures == 0 )
        {
            /* verify that it fails on a null db */
            failures += Verify7ArgConstructorFailure(null, "null",
                                                     "valid", "\"valid\"",
                                                     f_colID, "f_colID",
                                                     f_mveID, "f_mveID",
                                                     f_onset, "f_onset",
                                                     f_offset, "f_offset",
                                                     f_matrix, "f_matrix",
                                                     outStream, verbose);

            /* verify that it fails on a db that doesn't match the supplied IDs */
            failures += Verify7ArgConstructorFailure(new ODBCDatabase(), "alt_db",
                                                     "valid", "\"valid\"",
                                                     f_colID, "f_colID",
                                                     f_mveID, "f_mveID",
                                                     f_onset, "f_onset",
                                                     f_offset, "f_offset",
                                                     f_matrix, "f_matrix",
                                                     outStream, verbose);

            /* verify that it fails on an invalid colID */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             DBIndex.INVALID_ID, "invalid_id",
                                             f_mveID, "f_mveID",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on an invalid mveID */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             DBIndex.INVALID_ID, "invalid_id",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on an ID mismatch */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             i_mveID, "i_mveID",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on a bad colID */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_mveID, "f_mveID",
                                             i_mveID, "i_mveID",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on a bad mveID */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             i_colID, "i_colID",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on a null onset */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             f_mveID, "f_mveID",
                                             null, "null",
                                             f_offset, "f_offset",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on a null offset */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             f_mveID, "f_mveID",
                                             f_onset, "f_onset",
                                             null, "null",
                                             f_matrix, "f_matrix",
                                             outStream, verbose);

            /* verify that it fails on a null value */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             f_mveID, "f_mveID",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             null, "null",
                                             outStream, verbose);

            /* finally, verify failure on a fargID mismatch */
            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             f_colID, "f_colID",
                                             f_mveID, "f_mveID",
                                             f_onset, "f_onset",
                                             f_offset, "f_offset",
                                             i_matrix, "i_matrix",
                                             outStream, verbose);

            failures += Verify7ArgConstructorFailure(db, "db",
                                             "valid", "\"valid\"",
                                             m_colID, "m_colID",
                                             m_mveID, "m_mveID",
                                             m_onset, "m_onset",
                                             m_offset, "m_offset",
                                             p_matrix, "p_matrix",
                                             outStream, verbose);
        }
        
        
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }
        
        return pass;
        
    } /* DataCell::Test7ArgConstructor() */
    
    
    /**
     * TestAccessorMethods()
     *
     * Verify that the accessors supported by the DataCell class function
     * correctly when run on the supplied instance of DataCell.
     *
     *                                              JRM -- 12/4/07
     *
     * Changes:
     *
     *    - None
     */
    
    public static int TestAccessorMethods(DataCell testCell,
                                  Database initDB,
                                  String initComment,
                                  long initItsColID,
                                  long initItsMveID,
                                  MatrixVocabElement.MatrixType initItsMveType,
                                  int initOrd,
                                  TimeStamp initOnset,
                                  TimeStamp initOffset,
                                  Matrix initVal,
                                  java.io.PrintStream outStream,
                                  boolean verbose,
                                  String desc)
        throws SystemErrorException
    {
        int failures = 0;
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        TimeStamp newOnset1 = null;
        TimeStamp newOnset2 = null;
        TimeStamp newOffset1 = null;
        TimeStamp newOffset2 = null;
        FloatDataValue fdv = null;
        IntDataValue idv = null;
        NominalDataValue ndv = null;
        PredDataValue pdv = null;
        QuoteStringDataValue qdv = null;
        TextStringDataValue tdv = null;
        TimeStampDataValue tsdv = null;
        UndefinedDataValue udv = null;
        Matrix newVal1 = null;
        Matrix newVal2 = null;
        Matrix newVal3 = null;
        Matrix newVal4 = null;
        Matrix newVal5 = null;
        Matrix newVal6 = null;
        Matrix newVal7 = null;
        FormalArgument farg = null;
        long pve10ID = DBIndex.INVALID_ID;
        long pve11ID = DBIndex.INVALID_ID;
        long pve12ID = DBIndex.INVALID_ID;
        PredicateVocabElement pve10 = null;
        PredicateVocabElement pve11 = null;
        PredicateVocabElement pve12 = null;
        Predicate p0 = null;
        Predicate p1 = null;
        Predicate p2 = null;
        
        if ( testCell == null )
        {
            failures++;
            
            outStream.printf(
                    "DataCell::TestAccessors(): testCell null on entry.\n");
        }
        
        if ( desc == null )
        {
            failures++;
            
            outStream.printf("DataCell::TestAccessors(): desc null on entry.\n");
        }
        
        failures += Cell.TestAccessorMethods(testCell, initDB, initComment,
                                             initItsColID, initOrd,
                                             outStream, verbose, desc);
        
        
        /* test getItsMveID() */
        
        if ( testCell.getItsMveID() != initItsMveID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                    "%s.getItsMveID() = %d != %d = initItsMveID.\n",
                    desc, testCell.getItsMveID(), initItsMveID);
            }
        }
        
        
        /* test getItsMveType() */
        
        if ( testCell.getItsMveType() != initItsMveType )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.getItsMveType() = %s != %s = initItsMveType.\n",
                        desc, testCell.getItsMveType().toString(), 
                        initItsMveType.toString());
            }
        }
        
        
        /* test getOnset() / setOnset() */
        
        failures += TimeStamp.VerifyTimeStampCopy(testCell.getOnset(),
                                                  initOnset,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOnset()",
                                                  "initOnset");
        
        newOnset1 = new TimeStamp(testCell.db.getTicks(), 
                                  60 * testCell.db.getTicks());
        
        completed = false;
        threwSystemErrorException = false;
        
        try
        {
            testCell.setOnset(newOnset1);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;
            
            if ( verbose )
            {
                if ( ! completed )
                {
                    outStream.printf(
                            "testCell.setOnset(newOnset1) failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOnset(newOnset1) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        
        failures += TimeStamp.VerifyTimeStampCopy(testCell.getOnset(),
                                                  newOnset1,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOnset()",
                                                  "newOnset1");
        
        /* here we try to set the onset, using a time stamp with a different
         * tps setting.  We should do the conversion automatically.
         */
        newOnset2 = new TimeStamp(testCell.getDB().getTicks() * 2,
                                  120 * testCell.db.getTicks() * 2);
        
        completed = false;
        threwSystemErrorException = false;
        
        try
        {
            testCell.setOnset(newOnset2);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;
            
            if ( verbose )
            {
                if ( ! completed )
                {
                    outStream.printf(
                            "testCell.setOnset(newOnset2) failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOnset(newOnset2) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        
        /* do the conversion on our test time stamp */
        newOnset2.setTPS(testCell.getDB().getTicks());
        
        failures += TimeStamp.VerifyTimeStampCopy(testCell.getOnset(),
                                                  newOnset2,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOnset()",
                                                  "newOnset2(1)");
        
        completed = false;
        threwSystemErrorException = false;
        
        try
        {
            testCell.setOnset(null);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;
            
            if ( verbose )
            {
                if ( ! completed )
                {
                    outStream.printf("testCell.setOnset(null) completed.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOnset(null) failed to " +
                                      "throw a system error exception.\n");
                }
            }
        }
        
        failures += TimeStamp.VerifyTimeStampCopy(testCell.getOnset(),
                                                  newOnset2,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOnset()",
                                                  "newOnset2(2)");
        
         
        
        /* test getOffset() / setOffset() */
        
        failures += TimeStamp.VerifyTimeStampCopy(testCell.getOffset(),
                                                  initOffset,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOffset()",
                                                  "initOffset");
        
        newOffset1 = new TimeStamp(testCell.db.getTicks(), 
                                   60 * 60 * testCell.db.getTicks());
        
        completed = false;
        threwSystemErrorException = false;
        
        try
        {
            testCell.setOffset(newOffset1);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;
            
            if ( verbose )
            {
                if ( ! completed )
                {
                    outStream.printf("testCell.setOffset(newOffset1) " +
                                     "failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOffset(newOffset1) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        
        failures += TimeStamp.VerifyTimeStampCopy(testCell.getOffset(),
                                                  newOffset1,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOffset()",
                                                  "newOffset1");
        
        /* here we try to set the offset, using a time stamp with a different
         * tps setting.  We should do the conversion automatically.
         */
        newOffset2 = new TimeStamp(testCell.getDB().getTicks() / 2,
                                   240 * testCell.db.getTicks() / 2);
        
        completed = false;
        threwSystemErrorException = false;
        
        try
        {
            testCell.setOffset(newOffset2);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;
            
            if ( verbose )
            {
                if ( ! completed )
                {
                    outStream.printf("testCell.setOffset(newOffset2) " +
                                     "failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOffset(newOffset2) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        
        /* do the conversion on our test time stamp */
        newOffset2.setTPS(testCell.getDB().getTicks());
        
        failures += TimeStamp.VerifyTimeStampCopy(testCell.getOffset(),
                                                  newOffset2,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOffset()",
                                                  "newOffset2(1)");
        
        completed = false;
        threwSystemErrorException = false;
        
        try
        {
            testCell.setOffset(null);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;
            
            if ( verbose )
            {
                if ( ! completed )
                {
                    outStream.printf("testCell.setOffset(null) completed.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOffset(null) failed to " +
                                      "throw a system error exception.\n");
                }
            }
        }
        
        failures += TimeStamp.VerifyTimeStampCopy(testCell.getOffset(),
                                                  newOffset2,
                                                  outStream,
                                                  verbose,
                                                  desc + ".getOffset()",
                                                  "newOffset2(2)");
        
       /* Now test getVal() / setVal().  
        * 
        * At first glance, the tests here are not as exhaustive as one might
        * think they should be.  However, the value passed to the setVal()
        * routine must be a matrix, and the Matrix class has extensive error
        * checking on the replaceArg() routine, which should prevent 
        * construction of an invalid value to pass to the setVal() method.
        * This code is tested extensively in the Matrix test code.  
        *
        * However, it is also possible that a change has been made in the 
        * target MVE, and that this change has not been propagated to this
        * particular DataCell.  To detect this, setVal() calls 
        * Matrix.validateMatrix() before assigning the value.
        *
        * Assuming that Matrix.validateMatrix() works correctly, it will throw
        * a system error if an invalid value is received.  
        *
        * Given the difficulty of constructing invalid value matricies, we don't
        * bother with such tests here -- the real testing is done in Matrix.
        */
        
        /* start by setting up some test predicates that we may need */
        
        completed = false;
        threwSystemErrorException = false;
        
        try
        {
            if ( testCell.getDB().predVEExists("pve10") )
            {
                pve10 = testCell.getDB().getPredVE("pve10");
                pve10ID = pve10.getID();
            }
            else
            {
                pve10 = new PredicateVocabElement(testCell.getDB(), "pve10");
                farg = new UnTypedFormalArg(testCell.getDB(), "<arg1>");
                pve10.appendFormalArg(farg);
                farg = new UnTypedFormalArg(testCell.getDB(), "<arg2>");
                pve10.appendFormalArg(farg);
                pve10ID = testCell.getDB().addPredVE(pve10);
                // get a copy of the databases version of pve10 with ids assigned
                pve10 = testCell.getDB().getPredVE(pve10ID);
            }
            
            if ( testCell.getDB().predVEExists("pve11") )
            {
                pve11 = testCell.getDB().getPredVE("pve11");
                pve11ID = pve11.getID();
            }
            else
            {
                pve11 = new PredicateVocabElement(testCell.getDB(), "pve11");
                farg = new UnTypedFormalArg(testCell.getDB(), "<arg>");
                pve11.appendFormalArg(farg);
                pve11ID = testCell.getDB().addPredVE(pve11);
                // get a copy of the databases version of pve11 with ids assigned
                pve11 = testCell.getDB().getPredVE(pve11ID);
            }
            
            if ( testCell.getDB().predVEExists("pve12") )
            {
                pve12 = testCell.getDB().getPredVE("pve12");
                pve12ID = pve12.getID();
            }
            else
            {
                pve12 = new PredicateVocabElement(testCell.getDB(), "pve12");
                farg = new UnTypedFormalArg(testCell.getDB(), "<arg>");
                pve12.appendFormalArg(farg);
                pve12ID = testCell.getDB().addPredVE(pve12);
                // get a copy of the databases version of pve12 with ids assigned
                pve12 = testCell.getDB().getPredVE(pve12ID);
            }
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( pve10 == null ) ||
             ( pve10ID == DBIndex.INVALID_ID ) ||
             ( pve11 == null ) ||
             ( pve11ID == DBIndex.INVALID_ID ) ||
             ( pve12 == null ) ||
             ( pve12ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;
            
            if ( verbose )
            {
                if ( ( pve10 == null ) ||
                     ( pve10ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("Error(s) allocating pve10.\n");
                }
                
                if ( ( pve11 == null ) ||
                     ( pve11ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("Error(s) allocating pve11.\n");
                }
                
                if ( ( pve12 == null ) ||
                     ( pve12ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("Error(s) allocating pve12.\n");
                }
                
                if ( ! completed )
                {
                    outStream.printf(
                            "testCell.setOnset(newOnset1) failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOnset(newOnset1) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        
        failures =+ Matrix.VerifyMatrixCopy(testCell.getVal(), initVal,
                                            outStream, verbose, 
                                            desc + ".getVal()", "initVal");
        
        if ( testCell.getVal() == testCell.getVal() )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf(
                        "%s.getVal() isn't creating unique copies of val.\n",
                        desc);
            }
        }
        
        switch(testCell.getItsMveType())
        {
            case FLOAT:
                newVal1 = testCell.getVal();
                fdv = (FloatDataValue)(newVal1.getArg(0));
                fdv.setItsValue(3.14159);
                newVal1.replaceArg(0, fdv);
                testCell.setVal(newVal1);
                failures =+ Matrix.VerifyMatrixCopy(testCell.getVal(), newVal1,
                                            outStream, verbose, 
                                            desc + ".getVal()", "newVal1(f)");
                break;
                
            case INTEGER:
                newVal1 = testCell.getVal();
                idv = (IntDataValue)newVal1.getArg(0);
                idv.setItsValue(28);
                newVal1.replaceArg(0, idv);
                testCell.setVal(newVal1);
                failures =+ Matrix.VerifyMatrixCopy(testCell.getVal(), newVal1,
                                            outStream, verbose, 
                                            desc + ".getVal()", "newVal1(i)");
                break;
                
            case MATRIX:
                newVal1 = testCell.getVal();
                fdv = new FloatDataValue(testCell.getDB(), 
                                         newVal1.getArg(0).getItsFargID(),
                                         22.2);
                newVal1.replaceArg(0, fdv);
                testCell.setVal(newVal1);
                failures =+ Matrix.VerifyMatrixCopy(testCell.getVal(), newVal1,
                                            outStream, verbose, 
                                            desc + ".getVal()", "newVal1(m-f)");
                
                newVal2 = testCell.getVal();
                idv = new IntDataValue(testCell.getDB(),
                                       newVal2.getArg(0).getItsFargID(),
                                       33);
                newVal2.replaceArg(0, idv);
                testCell.setVal(newVal2);
                failures =+ Matrix.VerifyMatrixCopy(testCell.getVal(), newVal2,
                                            outStream, verbose, 
                                            desc + ".getVal()", "newVal2(m-i)");
                
                newVal3 = testCell.getVal();
                ndv = new NominalDataValue(testCell.getDB(),
                                           newVal3.getArg(0).getItsFargID(),
                                           "another_nominal");
                newVal3.replaceArg(0, ndv);
                testCell.setVal(newVal3);
                failures =+ Matrix.VerifyMatrixCopy(testCell.getVal(), newVal3,
                                            outStream, verbose, 
                                            desc + ".getVal()", "newVal3(m-n)");
                
                // construct the predicate:
                //
                //      pve10(pve11(pve12(j_q_nominal)), "just a quote string")
                //
                p2 = new Predicate(testCell.getDB(), pve12ID);
                ndv = new NominalDataValue(testCell.getDB(), 
                                           p2.getArg(0).getItsFargID(), 
                                           "j_q_nominal");
                p2.replaceArg(0, ndv);
                
                p1 = new Predicate(testCell.getDB(), pve11ID);
                pdv = new PredDataValue(testCell.getDB(), 
                                        p1.getArg(0).getItsFargID(), 
                                        p2);
                p1.replaceArg(0, pdv);
                
                p0 = new Predicate(testCell.getDB(), pve10ID);
                pdv = new PredDataValue(testCell.getDB(), 
                                        p0.getArg(0).getItsFargID(), 
                                        p1);
                p0.replaceArg(0, pdv);
                qdv = new QuoteStringDataValue(testCell.getDB(), 
                                               p0.getArg(1).getItsFargID(), 
                                               "just a quote string");
                p0.replaceArg(1, qdv);
                
                newVal4 = testCell.getVal();
                pdv = new PredDataValue(testCell.getDB(), 
                                        newVal4.getArg(0).getItsFargID(), 
                                        p0);
                newVal4.replaceArg(0, pdv);
                testCell.setVal(newVal4);
                failures =+ Matrix.VerifyMatrixCopy(testCell.getVal(), newVal4,
                                            outStream, verbose, 
                                            desc + ".getVal()", "newVal4(m-p)");
                
                newVal5 = testCell.getVal();
                tsdv = new TimeStampDataValue(testCell.getDB(),
                          newVal5.getArg(0).getItsFargID(),
                          new TimeStamp(testCell.getDB().getTicks(), 60 * 60));
                newVal5.replaceArg(0, tsdv);
                testCell.setVal(newVal5);
                failures =+ Matrix.VerifyMatrixCopy(testCell.getVal(), newVal5,
                                            outStream, verbose, 
                                            desc + ".getVal()", "newVal5(m-ts)");
                
                newVal6 = testCell.getVal();
                qdv = new QuoteStringDataValue(testCell.getDB(),
                                               newVal6.getArg(0).getItsFargID(),
                                               "another q-string");
                newVal6.replaceArg(0, qdv);
                testCell.setVal(newVal6);
                failures =+ Matrix.VerifyMatrixCopy(testCell.getVal(), newVal6,
                                            outStream, verbose, 
                                            desc + ".getVal()", "newVal6(m-qs)");
                
                newVal7 = testCell.getVal();
                farg = (FormalArgument)(testCell.getDB().idx.getElement(
                                             newVal7.getArg(0).getItsFargID()));
                udv = new UndefinedDataValue(testCell.getDB(),
                                             newVal7.getArg(0).getItsFargID(),
                                             farg.getFargName());
                newVal7.replaceArg(0, udv);
                testCell.setVal(newVal7);
                failures =+ Matrix.VerifyMatrixCopy(testCell.getVal(), newVal7,
                                            outStream, verbose, 
                                            desc + ".getVal()", "newVal7(m-u)");
                break;
                
            case NOMINAL:
                newVal1 = testCell.getVal();
                ndv = (NominalDataValue)newVal1.getArg(0);
                ndv.setItsValue("an_unlikely_nominal");
                newVal1.replaceArg(0, ndv);
                testCell.setVal(newVal1);
                failures =+ Matrix.VerifyMatrixCopy(testCell.getVal(), newVal1,
                                            outStream, verbose, 
                                            desc + ".getVal()", "newVal1(n)");
                break;
                
            case PREDICATE:
                // start by constructing the predicate:
                //
                //      pve10(pve11(pve12(33)), 11.1)
                //
                p2 = new Predicate(testCell.getDB(), pve12ID);
                idv = new IntDataValue(testCell.getDB(), 
                                       p2.getArg(0).getItsFargID(), 
                                       33);
                p2.replaceArg(0, idv);
                
                p1 = new Predicate(testCell.getDB(), pve11ID);
                pdv = new PredDataValue(testCell.getDB(), 
                                        p1.getArg(0).getItsFargID(), 
                                        p2);
                p1.replaceArg(0, pdv);
                
                p0 = new Predicate(testCell.getDB(), pve10ID);
                pdv = new PredDataValue(testCell.getDB(), 
                                        p0.getArg(0).getItsFargID(), 
                                        p1);
                p0.replaceArg(0, pdv);
                fdv = new FloatDataValue(testCell.getDB(), 
                                         p0.getArg(1).getItsFargID(), 
                                         11.1);
                p0.replaceArg(1, fdv);
                
                newVal1 = testCell.getVal();
                pdv = new PredDataValue(testCell.getDB(), 
                                        newVal1.getArg(0).getItsFargID(), 
                                        p0);
                newVal1.replaceArg(0, pdv);
                testCell.setVal(newVal1);
                failures =+ Matrix.VerifyMatrixCopy(testCell.getVal(), newVal1,
                                            outStream, verbose, 
                                            desc + ".getVal()", "newVal1(p)");
                break;
                
            case TEXT:
                newVal1 = testCell.getVal();
                tdv = (TextStringDataValue)newVal1.getArg(0);
                tdv.setItsValue("a random text string.");
                newVal1.replaceArg(0, tdv);
                testCell.setVal(newVal1);
                failures =+ Matrix.VerifyMatrixCopy(testCell.getVal(), newVal1,
                                            outStream, verbose, 
                                            desc + ".getVal()", "newVal1(t15)");
                break;
                
        }
        
        return failures;
        
    } /* DataCell::TestAccessorMethods() */
    
    
    /**
     * TestAccessors()
     * 
     * Run a battery of tests on the accessor methods for this class. 
     * 
     *                                              JRM -- 12/4/07
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public static boolean TestAccessors(java.io.PrintStream outStream,
                                        boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing class DataCell accessors                                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long pve0ID = DBIndex.INVALID_ID;
        long f_colID = DBIndex.INVALID_ID;
        long i_colID = DBIndex.INVALID_ID;
        long m_colID = DBIndex.INVALID_ID;
        long n_colID = DBIndex.INVALID_ID;
        long p_colID = DBIndex.INVALID_ID;
        long t_colID = DBIndex.INVALID_ID;
        long f_mveID = DBIndex.INVALID_ID;
        long i_mveID = DBIndex.INVALID_ID;
        long m_mveID = DBIndex.INVALID_ID;
        long n_mveID = DBIndex.INVALID_ID;
        long p_mveID = DBIndex.INVALID_ID;
        long t_mveID = DBIndex.INVALID_ID;
        long fargID;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement f_mve = null;
        MatrixVocabElement i_mve = null;
        MatrixVocabElement m_mve = null;
        MatrixVocabElement n_mve = null;
        MatrixVocabElement p_mve = null;
        MatrixVocabElement t_mve = null;
        DataColumn f_col = null;
        DataColumn i_col = null;
        DataColumn m_col = null;
        DataColumn n_col = null;
        DataColumn p_col = null;
        DataColumn t_col = null;
        TimeStamp f_onset = null;
        TimeStamp f_offset = null;
        TimeStamp i_onset = null;
        TimeStamp i_offset = null;
        TimeStamp m_onset = null;
        TimeStamp m_offset = null;
        TimeStamp n_onset = null;
        TimeStamp n_offset = null;
        TimeStamp p_onset = null;
        TimeStamp p_offset = null;
        TimeStamp t_onset = null;
        TimeStamp t_offset = null;
        FormalArgument farg = null;
        DataValue arg = null;
        Vector<DataValue> f_arg_list = null;
        Vector<DataValue> i_arg_list = null;
        Vector<DataValue> m_arg_list = null;
        Vector<DataValue> n_arg_list = null;
        Vector<DataValue> p_arg_list = null;
        Vector<DataValue> t_arg_list = null;
        Matrix f_matrix = null;
        Matrix i_matrix = null;
        Matrix m_matrix = null;
        Matrix n_matrix = null;
        Matrix p_matrix = null;
        Matrix t_matrix = null;
        DataCell f_cell0 = null;
        DataCell f_cell1 = null;
        DataCell f_cell2 = null;
        DataCell i_cell0 = null;
        DataCell i_cell1 = null;
        DataCell i_cell2 = null;
        DataCell m_cell0 = null;
        DataCell m_cell1 = null;
        DataCell m_cell2 = null;
        DataCell n_cell0 = null;
        DataCell n_cell1 = null;
        DataCell n_cell2 = null;
        DataCell p_cell0 = null;
        DataCell p_cell1 = null;
        DataCell p_cell2 = null;
        DataCell t_cell0 = null;
        DataCell t_cell1 = null;
        DataCell t_cell2 = null;
        DataCell c = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0ID);
            
            f_col = new DataColumn(db, "f_col", 
                                   MatrixVocabElement.MatrixType.FLOAT);
            f_colID = db.addColumn(f_col);
            f_col = db.getDataColumn(f_colID);
            f_mveID = f_col.getItsMveID();
            f_mve = db.getMatrixVE(f_mveID);
            f_onset = new TimeStamp(db.getTicks(), 60);
            f_offset = new TimeStamp(db.getTicks(), 120);
            f_arg_list = new Vector<DataValue>();
            fargID = f_mve.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            f_arg_list.add(arg);
            f_matrix = new Matrix(db, f_mveID, f_arg_list);
            
            
            i_col = new DataColumn(db, "i_col", 
                                   MatrixVocabElement.MatrixType.INTEGER);
            i_colID = db.addColumn(i_col);
            i_col = db.getDataColumn(i_colID);
            i_mveID = i_col.getItsMveID();
            i_mve = db.getMatrixVE(i_mveID);
            i_onset = new TimeStamp(db.getTicks(), 180);
            i_offset = new TimeStamp(db.getTicks(), 240);
            i_arg_list = new Vector<DataValue>();
            fargID = i_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 22);
            i_arg_list.add(arg);
            i_matrix = new Matrix(db, i_mveID, i_arg_list);
            
            
            m_col = new DataColumn(db, "m_col", 
                                   MatrixVocabElement.MatrixType.MATRIX);
            m_colID = db.addColumn(m_col);
            m_col = db.getDataColumn(m_colID);
            m_mveID = m_col.getItsMveID();
            m_mve = db.getMatrixVE(m_mveID);
            m_onset = new TimeStamp(db.getTicks(), 300);
            m_offset = new TimeStamp(db.getTicks(), 360);
            m_arg_list = new Vector<DataValue>();
            fargID = m_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 33);
            m_arg_list.add(arg);
            m_matrix = new Matrix(db, m_mveID, m_arg_list);
            
            
            n_col = new DataColumn(db, "n_col", 
                                   MatrixVocabElement.MatrixType.NOMINAL);
            n_colID = db.addColumn(n_col);
            n_col = db.getDataColumn(n_colID);
            n_mveID = n_col.getItsMveID();
            n_mve = db.getMatrixVE(n_mveID);
            n_onset = new TimeStamp(db.getTicks(), 420);
            n_offset = new TimeStamp(db.getTicks(), 480);
            n_arg_list = new Vector<DataValue>();
            fargID = n_mve.getFormalArg(0).getID();
            arg = new NominalDataValue(db, fargID, "a_nominal");
            n_arg_list.add(arg);
            n_matrix = new Matrix(db, n_mveID, n_arg_list);
            
            
            p_col = new DataColumn(db, "p_col", 
                                   MatrixVocabElement.MatrixType.PREDICATE);
            p_colID = db.addColumn(p_col);
            p_col = db.getDataColumn(p_colID);
            p_mveID = p_col.getItsMveID();
            p_mve = db.getMatrixVE(p_mveID);
            p_onset = new TimeStamp(db.getTicks(), 540);
            p_offset = new TimeStamp(db.getTicks(), 600);
            p_arg_list = new Vector<DataValue>();
            fargID = p_mve.getFormalArg(0).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
            p_arg_list.add(arg);
            p_matrix = new Matrix(db, p_mveID, p_arg_list);
            
            
            t_col = new DataColumn(db, "t_col", 
                                   MatrixVocabElement.MatrixType.TEXT);
            t_colID = db.addColumn(t_col);
            t_col = db.getDataColumn(t_colID);
            t_mveID = t_col.getItsMveID();
            t_mve = db.getMatrixVE(t_mveID);
            t_onset = new TimeStamp(db.getTicks(), 660);
            t_offset = new TimeStamp(db.getTicks(), 720);
            t_arg_list = new Vector<DataValue>();
            fargID = t_mve.getFormalArg(0).getID();
            arg = new TextStringDataValue(db, fargID, "a text string");
            t_arg_list.add(arg);
            t_matrix = new Matrix(db, t_mveID, t_arg_list);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( f_colID == DBIndex.INVALID_ID ) ||
             ( f_col == null ) ||
             ( f_mveID == DBIndex.INVALID_ID ) ||
             ( f_mve == null ) ||
             ( f_onset == null ) ||
             ( f_offset == null ) ||
             ( f_arg_list == null ) ||
             ( f_matrix == null ) ||
             ( i_colID == DBIndex.INVALID_ID ) ||
             ( i_col == null ) ||
             ( i_mveID == DBIndex.INVALID_ID ) ||
             ( i_mve == null ) ||
             ( i_onset == null ) ||
             ( i_offset == null ) ||
             ( i_arg_list == null ) ||
             ( i_matrix == null ) ||
             ( m_colID == DBIndex.INVALID_ID ) ||
             ( m_col == null ) ||
             ( m_mveID == DBIndex.INVALID_ID ) ||
             ( m_mve == null ) ||
             ( m_onset == null ) ||
             ( m_offset == null ) ||
             ( m_arg_list == null ) ||
             ( m_matrix == null ) ||
             ( n_colID == DBIndex.INVALID_ID ) ||
             ( n_col == null ) ||
             ( n_mveID == DBIndex.INVALID_ID ) ||
             ( n_mve == null ) ||
             ( n_onset == null ) ||
             ( n_offset == null ) ||
             ( n_arg_list == null ) ||
             ( n_matrix == null ) ||
             ( p_colID == DBIndex.INVALID_ID ) ||
             ( p_col == null ) ||
             ( p_mveID == DBIndex.INVALID_ID ) ||
             ( p_mve == null ) ||
             ( p_onset == null ) ||
             ( p_offset == null ) ||
             ( p_arg_list == null ) ||
             ( p_matrix == null ) ||
             ( t_colID == DBIndex.INVALID_ID ) ||
             ( t_col == null ) ||
             ( t_mveID == DBIndex.INVALID_ID ) ||
             ( t_mve == null ) ||
             ( t_onset == null ) ||
             ( t_offset == null ) ||
             ( t_arg_list == null ) ||
             ( t_matrix == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) ) 
        {
            failures++;
            
            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( ( pve0 == null ) ||
                     ( pve0ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve0.  pve0ID = %d.\n",
                                     pve0ID);
                }
                
                if ( ( f_colID == DBIndex.INVALID_ID ) ||
                     ( f_col == null ) ||
                     ( f_mveID == DBIndex.INVALID_ID ) ||
                     ( f_mve == null ) )
                {
                    outStream.printf("Errors allocating f_col. f_colID = %d, " +
                                     "f_mveID = %d.\n", f_colID, f_mveID);
                }
                
                if ( ( f_onset == null ) || ( f_offset == null ) )
                {
                    outStream.printf(
                            "allocation of f_onset and/or f_offset failed.\n");
                }
                
                if ( ( f_arg_list == null ) || ( f_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of f_matrix.\n");
                }
                
                if ( ( i_colID == DBIndex.INVALID_ID ) ||
                     ( i_col == null ) ||
                     ( i_mveID == DBIndex.INVALID_ID ) ||
                     ( i_mve == null ) )
                {
                    outStream.printf("Errors allocating i_col. i_colID = %d, " +
                                     "i_mveID = %d.\n", i_colID, i_mveID);
                }
                
                if ( ( i_onset == null ) || ( i_offset == null ) )
                {
                    outStream.printf(
                            "allocation of i_onset and/or i_offset failed.\n");
                }
                
                if ( ( i_arg_list == null ) || ( i_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of i_matrix.\n");
                }
                
                if ( ( m_colID == DBIndex.INVALID_ID ) ||
                     ( m_col == null ) ||
                     ( m_mveID == DBIndex.INVALID_ID ) ||
                     ( m_mve == null ) )
                {
                    outStream.printf("Errors allocating m_col. m_colID = %d, " +
                                     "m_mveID = %d.\n", m_colID, m_mveID);
                }
                
                if ( ( m_onset == null ) || ( m_offset == null ) )
                {
                    outStream.printf(
                            "allocation of m_onset and/or m_offset failed.\n");
                }
                
                if ( ( m_arg_list == null ) || ( m_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of m_matrix.\n");
                }
                
                if ( ( n_colID == DBIndex.INVALID_ID ) ||
                     ( n_col == null ) ||
                     ( n_mveID == DBIndex.INVALID_ID ) ||
                     ( n_mve == null ) )
                {
                    outStream.printf("Errors allocating n_col. n_colID = %d, " +
                                     "n_mveID = %d.\n", n_colID, n_mveID);
                }
                
                if ( ( n_onset == null ) || ( n_offset == null ) )
                {
                    outStream.printf(
                            "allocation of n_onset and/or n_offset failed.\n");
                }
                
                if ( ( n_arg_list == null ) || ( n_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of n_matrix.\n");
                }
                
                if ( ( p_colID == DBIndex.INVALID_ID ) ||
                     ( p_col == null ) ||
                     ( p_mveID == DBIndex.INVALID_ID ) ||
                     ( p_mve == null ) )
                {
                    outStream.printf("Errors allocating p_col. p_colID = %d, " +
                                     "p_mveID = %d.\n", p_colID, p_mveID);
                }
                
                if ( ( p_onset == null ) || ( p_offset == null ) )
                {
                    outStream.printf(
                            "allocation of p_onset and/or p_offset failed.\n");
                }
                
                if ( ( p_arg_list == null ) || ( p_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of p_matrix.\n");
                }
                
                if ( ( t_colID == DBIndex.INVALID_ID ) ||
                     ( t_col == null ) ||
                     ( t_mveID == DBIndex.INVALID_ID ) ||
                     ( t_mve == null ) )
                {
                    outStream.printf("Errors allocating t_col. t_colID = %d, " +
                                     "t_mveID = %d.\n", t_colID, t_mveID);
                }
                
                if ( ( t_onset == null ) || ( t_offset == null ) )
                {
                    outStream.printf(
                            "allocation of t_onset and/or t_offset failed.\n");
                }
                
                if ( ( t_arg_list == null ) || ( t_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of t_matrix.\n");
                }
                
                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        
        /* now allocate the base cells for the accessor tests */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_cell0 = new DataCell(db, f_colID, f_mveID);
                f_cell1 = new DataCell(db, "f_cell1", f_colID, f_mveID);
                f_cell2 = new DataCell(db, "f_cell2", f_colID, f_mveID, 
                                       f_onset, f_offset, f_matrix);
                i_cell0 = new DataCell(db, i_colID, i_mveID);
                i_cell1 = new DataCell(db, "i_cell1", i_colID, i_mveID);
                i_cell2 = new DataCell(db, "i_cell2", i_colID, i_mveID, 
                                       i_onset, i_offset, i_matrix);
                m_cell0 = new DataCell(db, m_colID, m_mveID);
                m_cell1 = new DataCell(db, "m_cell1", m_colID, m_mveID);
                m_cell2 = new DataCell(db, "m_cell2", m_colID, m_mveID,
                                       m_onset, m_offset, m_matrix);
                n_cell0 = new DataCell(db, n_colID, n_mveID);
                n_cell1 = new DataCell(db, "n_cell1", n_colID, n_mveID);
                n_cell2 = new DataCell(db, "n_cell2", n_colID, n_mveID,
                                       n_onset, n_offset, n_matrix);
                p_cell0 = new DataCell(db, p_colID, p_mveID);
                p_cell1 = new DataCell(db, "p_cell1", p_colID, p_mveID);
                p_cell2 = new DataCell(db, "p_cell2", p_colID, p_mveID,
                                       p_onset, p_offset, p_matrix);
                t_cell0 = new DataCell(db, t_colID, t_mveID);
                t_cell1 = new DataCell(db, "t_cell1", t_colID, t_mveID);
                t_cell2 = new DataCell(db, "t_cell2", t_colID, t_mveID,
                                       t_onset, t_offset, t_matrix);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_cell0 == null ) ||
                 ( f_cell1 == null ) ||
                 ( f_cell2 == null ) ||
                 ( i_cell0 == null ) ||
                 ( i_cell1 == null ) ||
                 ( i_cell2 == null ) ||
                 ( m_cell0 == null ) ||
                 ( m_cell1 == null ) ||
                 ( m_cell2 == null ) ||
                 ( n_cell0 == null ) ||
                 ( n_cell1 == null ) ||
                 ( n_cell2 == null ) ||
                 ( p_cell0 == null ) ||
                 ( p_cell1 == null ) ||
                 ( p_cell2 == null ) ||
                 ( t_cell0 == null ) ||
                 ( t_cell1 == null ) ||
                 ( t_cell2 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( ( f_cell0 == null ) || 
                         ( f_cell1 == null ) ||
                         ( f_cell2 == null ) )
                    {
                        outStream.printf("f_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( i_cell0 == null ) || 
                         ( i_cell1 == null ) || 
                         ( i_cell2 == null ) )
                    {
                        outStream.printf("i_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( m_cell0 == null ) ||
                         ( m_cell1 == null ) || 
                         ( m_cell2 == null ) )
                    {
                        outStream.printf("m_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( n_cell0 == null ) ||
                         ( n_cell1 == null ) || 
                         ( n_cell2 == null ) )
                    {
                        outStream.printf("n_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( p_cell0 == null ) || 
                         ( p_cell1 == null ) ||
                         ( p_cell2 == null ) )
                    {
                        outStream.printf("p_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( t_cell0 == null ) ||
                         ( t_cell1 == null ) ||
                         ( t_cell2 == null ) )
                    {
                        outStream.printf("t_cell allocation(s) failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyInitialization(db,
                                       f_cell0,
                                       "f_cell0",
                                       null,
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, f_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       f_cell1,
                                       "f_cell1",
                                       "f_cell1",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, f_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       f_cell2,
                                       "f_cell2",
                                       "f_cell2",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       f_onset,
                                       f_offset,
                                       f_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       i_cell0,
                                       "i_cell0",
                                       null,
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, i_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell1,
                                       "i_cell1",
                                       "i_cell1",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, i_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell2,
                                       "i_cell2",
                                       "i_cell2",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       i_onset,
                                       i_offset,
                                       i_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell0,
                                       "m_cell0",
                                       null,
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, m_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell1,
                                       "m_cell1",
                                       "m_cell1",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, m_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell2,
                                       "m_cell2",
                                       "m_cell2",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       m_onset,
                                       m_offset,
                                       m_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       n_cell0,
                                       "n_cell0",
                                       null,
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, n_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       n_cell1,
                                       "n_cell1",
                                       "n_cell1",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, n_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       n_cell2,
                                       "n_cell2",
                                       "n_cell2",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       n_onset,
                                       n_offset,
                                       n_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       p_cell0,
                                       "p_cell0",
                                       null,
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, p_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       p_cell1,
                                       "p_cell1",
                                       "p_cell1",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, p_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       p_cell2,
                                       "p_cell2",
                                       "p_cell2",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       p_onset,
                                       p_offset,
                                       p_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       t_cell0,
                                       "t_cell0",
                                       null,
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, t_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       t_cell1,
                                       "t_cell1",
                                       "t_cell1",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, t_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       t_cell2,
                                       "t_cell2",
                                       "t_cell2",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       t_onset,
                                       t_offset,
                                       t_matrix,
                                       outStream,
                                       verbose);
            }
        }
        
        if ( failures == 0 )
        {
            /* run accessor tests on accessors */
    
            failures += TestAccessorMethods(f_cell0, db, null, f_colID, 
                    f_mveID, MatrixVocabElement.MatrixType.FLOAT, -1,
                    new TimeStamp(db.getTicks(), 0), 
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, f_mveID),
                    outStream, verbose, "f_cell0");
                    
            failures += TestAccessorMethods(f_cell1, db, "f_cell1", f_colID, 
                    f_mveID, MatrixVocabElement.MatrixType.FLOAT, -1,
                    new TimeStamp(db.getTicks(), 0), 
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, f_mveID),
                    outStream, verbose, "f_cell1");
                    
            failures += TestAccessorMethods(f_cell2, db, "f_cell2", f_colID, 
                    f_mveID, MatrixVocabElement.MatrixType.FLOAT, -1,
                    f_onset, f_offset, f_matrix,
                    outStream, verbose, "f_cell2");
            
    
            failures += TestAccessorMethods(i_cell0, db, null, i_colID, 
                    i_mveID, MatrixVocabElement.MatrixType.INTEGER, -1,
                    new TimeStamp(db.getTicks(), 0), 
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, i_mveID),
                    outStream, verbose, "i_cell0");
                    
            failures += TestAccessorMethods(i_cell1, db, "i_cell1", i_colID, 
                    i_mveID, MatrixVocabElement.MatrixType.INTEGER, -1,
                    new TimeStamp(db.getTicks(), 0), 
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, i_mveID),
                    outStream, verbose, "i_cell1");
                    
            failures += TestAccessorMethods(i_cell2, db, "i_cell2", i_colID, 
                    i_mveID, MatrixVocabElement.MatrixType.INTEGER, -1,
                    i_onset, i_offset, i_matrix,
                    outStream, verbose, "i_cell2");
            
    
            failures += TestAccessorMethods(m_cell0, db, null, m_colID, 
                    m_mveID, MatrixVocabElement.MatrixType.MATRIX, -1,
                    new TimeStamp(db.getTicks(), 0), 
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, m_mveID),
                    outStream, verbose, "m_cell0");
                    
            failures += TestAccessorMethods(m_cell1, db, "m_cell1", m_colID, 
                    m_mveID, MatrixVocabElement.MatrixType.MATRIX, -1,
                    new TimeStamp(db.getTicks(), 0), 
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, m_mveID),
                    outStream, verbose, "m_cell1");
                    
            failures += TestAccessorMethods(m_cell2, db, "m_cell2", m_colID, 
                    m_mveID, MatrixVocabElement.MatrixType.MATRIX, -1,
                    m_onset, m_offset, m_matrix,
                    outStream, verbose, "m_cell2");
            
    
            failures += TestAccessorMethods(n_cell0, db, null, n_colID, 
                    n_mveID, MatrixVocabElement.MatrixType.NOMINAL, -1,
                    new TimeStamp(db.getTicks(), 0), 
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, n_mveID),
                    outStream, verbose, "n_cell0");
                    
            failures += TestAccessorMethods(n_cell1, db, "n_cell1", n_colID, 
                    n_mveID, MatrixVocabElement.MatrixType.NOMINAL, -1,
                    new TimeStamp(db.getTicks(), 0), 
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, n_mveID),
                    outStream, verbose, "n_cell1");
                    
            failures += TestAccessorMethods(n_cell2, db, "n_cell2", n_colID, 
                    n_mveID, MatrixVocabElement.MatrixType.NOMINAL, -1,
                    n_onset, n_offset, n_matrix,
                    outStream, verbose, "n_cell2");
            
    
            failures += TestAccessorMethods(p_cell0, db, null, p_colID, 
                    p_mveID, MatrixVocabElement.MatrixType.PREDICATE, -1,
                    new TimeStamp(db.getTicks(), 0), 
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, p_mveID),
                    outStream, verbose, "p_cell0");
                    
            failures += TestAccessorMethods(p_cell1, db, "p_cell1", p_colID, 
                    p_mveID, MatrixVocabElement.MatrixType.PREDICATE, -1,
                    new TimeStamp(db.getTicks(), 0), 
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, p_mveID),
                    outStream, verbose, "p_cell1");
                    
            failures += TestAccessorMethods(p_cell2, db, "p_cell2", p_colID, 
                    p_mveID, MatrixVocabElement.MatrixType.PREDICATE, -1,
                    p_onset, p_offset, p_matrix,
                    outStream, verbose, "p_cell2");
            
    
            failures += TestAccessorMethods(t_cell0, db, null, t_colID, 
                    t_mveID, MatrixVocabElement.MatrixType.TEXT, -1,
                    new TimeStamp(db.getTicks(), 0), 
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, t_mveID),
                    outStream, verbose, "t_cell0");
                    
            failures += TestAccessorMethods(t_cell1, db, "t_cell1", t_colID, 
                    t_mveID, MatrixVocabElement.MatrixType.TEXT, -1,
                    new TimeStamp(db.getTicks(), 0), 
                    new TimeStamp(db.getTicks(), 0), new Matrix(db, t_mveID),
                    outStream, verbose, "t_cell1");
                    
            failures += TestAccessorMethods(t_cell2, db, "t_cell2", t_colID, 
                    t_mveID, MatrixVocabElement.MatrixType.TEXT, -1,
                    t_onset, t_offset, t_matrix,
                    outStream, verbose, "t_cell2");
        }

        
        /* verify that setVal() fails on an mveID mismatch. */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                m_cell0.setVal(m_matrix);
                m_cell0.setVal(f_matrix);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.printf(
                                "m_cell0.setVal(f_matrix) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("m_cell0.setVal(f_matrix) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else
            {
                failures =+ Matrix.VerifyMatrixCopy(m_cell0.getVal(), m_matrix,
                                            outStream, verbose, 
                                            "m_cell0.getVal()", "m_matrix");
            }
        }
        
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }
        
        return pass;
        
    } /* DataCell::TestAccessors() */
    
    
    /**
     * TestCopyConstructor()
     * 
     * Run a battery of tests on the copy constructor for this 
     * class, and on the instances returned.
     * 
     *                                              JRM -- 11/13/07
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public static boolean TestCopyConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing copy constructor for class DataCell                      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long pve0ID = DBIndex.INVALID_ID;
        long f_colID = DBIndex.INVALID_ID;
        long i_colID = DBIndex.INVALID_ID;
        long m_colID = DBIndex.INVALID_ID;
        long n_colID = DBIndex.INVALID_ID;
        long p_colID = DBIndex.INVALID_ID;
        long t_colID = DBIndex.INVALID_ID;
        long f_mveID = DBIndex.INVALID_ID;
        long i_mveID = DBIndex.INVALID_ID;
        long m_mveID = DBIndex.INVALID_ID;
        long n_mveID = DBIndex.INVALID_ID;
        long p_mveID = DBIndex.INVALID_ID;
        long t_mveID = DBIndex.INVALID_ID;
        long fargID;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement f_mve = null;
        MatrixVocabElement i_mve = null;
        MatrixVocabElement m_mve = null;
        MatrixVocabElement n_mve = null;
        MatrixVocabElement p_mve = null;
        MatrixVocabElement t_mve = null;
        DataColumn f_col = null;
        DataColumn i_col = null;
        DataColumn m_col = null;
        DataColumn n_col = null;
        DataColumn p_col = null;
        DataColumn t_col = null;
        TimeStamp f_onset = null;
        TimeStamp f_offset = null;
        TimeStamp i_onset = null;
        TimeStamp i_offset = null;
        TimeStamp m_onset = null;
        TimeStamp m_offset = null;
        TimeStamp n_onset = null;
        TimeStamp n_offset = null;
        TimeStamp p_onset = null;
        TimeStamp p_offset = null;
        TimeStamp t_onset = null;
        TimeStamp t_offset = null;
        FormalArgument farg = null;
        DataValue arg = null;
        Vector<DataValue> f_arg_list = null;
        Vector<DataValue> i_arg_list = null;
        Vector<DataValue> m_arg_list = null;
        Vector<DataValue> n_arg_list = null;
        Vector<DataValue> p_arg_list = null;
        Vector<DataValue> t_arg_list = null;
        Matrix f_matrix = null;
        Matrix i_matrix = null;
        Matrix m_matrix = null;
        Matrix n_matrix = null;
        Matrix p_matrix = null;
        Matrix t_matrix = null;
        DataCell f_cell0 = null;
        DataCell f_cell1 = null;
        DataCell f_cell2 = null;
        DataCell f_cell0_copy = null;
        DataCell f_cell1_copy = null;
        DataCell f_cell2_copy = null;
        DataCell i_cell0 = null;
        DataCell i_cell1 = null;
        DataCell i_cell2 = null;
        DataCell i_cell0_copy = null;
        DataCell i_cell1_copy = null;
        DataCell i_cell2_copy = null;
        DataCell m_cell0 = null;
        DataCell m_cell1 = null;
        DataCell m_cell2 = null;
        DataCell m_cell0_copy = null;
        DataCell m_cell1_copy = null;
        DataCell m_cell2_copy = null;
        DataCell n_cell0 = null;
        DataCell n_cell1 = null;
        DataCell n_cell2 = null;
        DataCell n_cell0_copy = null;
        DataCell n_cell1_copy = null;
        DataCell n_cell2_copy = null;
        DataCell p_cell0 = null;
        DataCell p_cell1 = null;
        DataCell p_cell2 = null;
        DataCell p_cell0_copy = null;
        DataCell p_cell1_copy = null;
        DataCell p_cell2_copy = null;
        DataCell t_cell0 = null;
        DataCell t_cell1 = null;
        DataCell t_cell2 = null;
        DataCell t_cell0_copy = null;
        DataCell t_cell1_copy = null;
        DataCell t_cell2_copy = null;
        DataCell c = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0ID);
            
            f_col = new DataColumn(db, "f_col", 
                                   MatrixVocabElement.MatrixType.FLOAT);
            f_colID = db.addColumn(f_col);
            f_col = db.getDataColumn(f_colID);
            f_mveID = f_col.getItsMveID();
            f_mve = db.getMatrixVE(f_mveID);
            f_onset = new TimeStamp(db.getTicks(), 60);
            f_offset = new TimeStamp(db.getTicks(), 120);
            f_arg_list = new Vector<DataValue>();
            fargID = f_mve.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            f_arg_list.add(arg);
            f_matrix = new Matrix(db, f_mveID, f_arg_list);
            
            
            i_col = new DataColumn(db, "i_col", 
                                   MatrixVocabElement.MatrixType.INTEGER);
            i_colID = db.addColumn(i_col);
            i_col = db.getDataColumn(i_colID);
            i_mveID = i_col.getItsMveID();
            i_mve = db.getMatrixVE(i_mveID);
            i_onset = new TimeStamp(db.getTicks(), 180);
            i_offset = new TimeStamp(db.getTicks(), 240);
            i_arg_list = new Vector<DataValue>();
            fargID = i_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 22);
            i_arg_list.add(arg);
            i_matrix = new Matrix(db, i_mveID, i_arg_list);
            
            
            m_col = new DataColumn(db, "m_col", 
                                   MatrixVocabElement.MatrixType.MATRIX);
            m_colID = db.addColumn(m_col);
            m_col = db.getDataColumn(m_colID);
            m_mveID = m_col.getItsMveID();
            m_mve = db.getMatrixVE(m_mveID);
            m_onset = new TimeStamp(db.getTicks(), 300);
            m_offset = new TimeStamp(db.getTicks(), 360);
            m_arg_list = new Vector<DataValue>();
            fargID = m_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 33);
            m_arg_list.add(arg);
            m_matrix = new Matrix(db, m_mveID, m_arg_list);
            
            
            n_col = new DataColumn(db, "n_col", 
                                   MatrixVocabElement.MatrixType.NOMINAL);
            n_colID = db.addColumn(n_col);
            n_col = db.getDataColumn(n_colID);
            n_mveID = n_col.getItsMveID();
            n_mve = db.getMatrixVE(n_mveID);
            n_onset = new TimeStamp(db.getTicks(), 420);
            n_offset = new TimeStamp(db.getTicks(), 480);
            n_arg_list = new Vector<DataValue>();
            fargID = n_mve.getFormalArg(0).getID();
            arg = new NominalDataValue(db, fargID, "a_nominal");
            n_arg_list.add(arg);
            n_matrix = new Matrix(db, n_mveID, n_arg_list);
            
            
            p_col = new DataColumn(db, "p_col", 
                                   MatrixVocabElement.MatrixType.PREDICATE);
            p_colID = db.addColumn(p_col);
            p_col = db.getDataColumn(p_colID);
            p_mveID = p_col.getItsMveID();
            p_mve = db.getMatrixVE(p_mveID);
            p_onset = new TimeStamp(db.getTicks(), 540);
            p_offset = new TimeStamp(db.getTicks(), 600);
            p_arg_list = new Vector<DataValue>();
            fargID = p_mve.getFormalArg(0).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
            p_arg_list.add(arg);
            p_matrix = new Matrix(db, p_mveID, p_arg_list);
            
            
            t_col = new DataColumn(db, "t_col", 
                                   MatrixVocabElement.MatrixType.TEXT);
            t_colID = db.addColumn(t_col);
            t_col = db.getDataColumn(t_colID);
            t_mveID = t_col.getItsMveID();
            t_mve = db.getMatrixVE(t_mveID);
            t_onset = new TimeStamp(db.getTicks(), 660);
            t_offset = new TimeStamp(db.getTicks(), 720);
            t_arg_list = new Vector<DataValue>();
            fargID = t_mve.getFormalArg(0).getID();
            arg = new TextStringDataValue(db, fargID, "a text string");
            t_arg_list.add(arg);
            t_matrix = new Matrix(db, t_mveID, t_arg_list);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( f_colID == DBIndex.INVALID_ID ) ||
             ( f_col == null ) ||
             ( f_mveID == DBIndex.INVALID_ID ) ||
             ( f_mve == null ) ||
             ( f_onset == null ) ||
             ( f_offset == null ) ||
             ( f_arg_list == null ) ||
             ( f_matrix == null ) ||
             ( i_colID == DBIndex.INVALID_ID ) ||
             ( i_col == null ) ||
             ( i_mveID == DBIndex.INVALID_ID ) ||
             ( i_mve == null ) ||
             ( i_onset == null ) ||
             ( i_offset == null ) ||
             ( i_arg_list == null ) ||
             ( i_matrix == null ) ||
             ( m_colID == DBIndex.INVALID_ID ) ||
             ( m_col == null ) ||
             ( m_mveID == DBIndex.INVALID_ID ) ||
             ( m_mve == null ) ||
             ( m_onset == null ) ||
             ( m_offset == null ) ||
             ( m_arg_list == null ) ||
             ( m_matrix == null ) ||
             ( n_colID == DBIndex.INVALID_ID ) ||
             ( n_col == null ) ||
             ( n_mveID == DBIndex.INVALID_ID ) ||
             ( n_mve == null ) ||
             ( n_onset == null ) ||
             ( n_offset == null ) ||
             ( n_arg_list == null ) ||
             ( n_matrix == null ) ||
             ( p_colID == DBIndex.INVALID_ID ) ||
             ( p_col == null ) ||
             ( p_mveID == DBIndex.INVALID_ID ) ||
             ( p_mve == null ) ||
             ( p_onset == null ) ||
             ( p_offset == null ) ||
             ( p_arg_list == null ) ||
             ( p_matrix == null ) ||
             ( t_colID == DBIndex.INVALID_ID ) ||
             ( t_col == null ) ||
             ( t_mveID == DBIndex.INVALID_ID ) ||
             ( t_mve == null ) ||
             ( t_onset == null ) ||
             ( t_offset == null ) ||
             ( t_arg_list == null ) ||
             ( t_matrix == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) ) 
        {
            failures++;
            
            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( ( pve0 == null ) ||
                     ( pve0ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve0.  pve0ID = %d.\n",
                                     pve0ID);
                }
                
                if ( ( f_colID == DBIndex.INVALID_ID ) ||
                     ( f_col == null ) ||
                     ( f_mveID == DBIndex.INVALID_ID ) ||
                     ( f_mve == null ) )
                {
                    outStream.printf("Errors allocating f_col. f_colID = %d, " +
                                     "f_mveID = %d.\n", f_colID, f_mveID);
                }
                
                if ( ( f_onset == null ) || ( f_offset == null ) )
                {
                    outStream.printf(
                            "allocation of f_onset and/or f_offset failed.\n");
                }
                
                if ( ( f_arg_list == null ) || ( f_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of f_matrix.\n");
                }
                
                if ( ( i_colID == DBIndex.INVALID_ID ) ||
                     ( i_col == null ) ||
                     ( i_mveID == DBIndex.INVALID_ID ) ||
                     ( i_mve == null ) )
                {
                    outStream.printf("Errors allocating i_col. i_colID = %d, " +
                                     "i_mveID = %d.\n", i_colID, i_mveID);
                }
                
                if ( ( i_onset == null ) || ( i_offset == null ) )
                {
                    outStream.printf(
                            "allocation of i_onset and/or i_offset failed.\n");
                }
                
                if ( ( i_arg_list == null ) || ( i_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of i_matrix.\n");
                }
                
                if ( ( m_colID == DBIndex.INVALID_ID ) ||
                     ( m_col == null ) ||
                     ( m_mveID == DBIndex.INVALID_ID ) ||
                     ( m_mve == null ) )
                {
                    outStream.printf("Errors allocating m_col. m_colID = %d, " +
                                     "m_mveID = %d.\n", m_colID, m_mveID);
                }
                
                if ( ( m_onset == null ) || ( m_offset == null ) )
                {
                    outStream.printf(
                            "allocation of m_onset and/or m_offset failed.\n");
                }
                
                if ( ( m_arg_list == null ) || ( m_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of m_matrix.\n");
                }
                
                if ( ( n_colID == DBIndex.INVALID_ID ) ||
                     ( n_col == null ) ||
                     ( n_mveID == DBIndex.INVALID_ID ) ||
                     ( n_mve == null ) )
                {
                    outStream.printf("Errors allocating n_col. n_colID = %d, " +
                                     "n_mveID = %d.\n", n_colID, n_mveID);
                }
                
                if ( ( n_onset == null ) || ( n_offset == null ) )
                {
                    outStream.printf(
                            "allocation of n_onset and/or n_offset failed.\n");
                }
                
                if ( ( n_arg_list == null ) || ( n_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of n_matrix.\n");
                }
                
                if ( ( p_colID == DBIndex.INVALID_ID ) ||
                     ( p_col == null ) ||
                     ( p_mveID == DBIndex.INVALID_ID ) ||
                     ( p_mve == null ) )
                {
                    outStream.printf("Errors allocating p_col. p_colID = %d, " +
                                     "p_mveID = %d.\n", p_colID, p_mveID);
                }
                
                if ( ( p_onset == null ) || ( p_offset == null ) )
                {
                    outStream.printf(
                            "allocation of p_onset and/or p_offset failed.\n");
                }
                
                if ( ( p_arg_list == null ) || ( p_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of p_matrix.\n");
                }
                
                if ( ( t_colID == DBIndex.INVALID_ID ) ||
                     ( t_col == null ) ||
                     ( t_mveID == DBIndex.INVALID_ID ) ||
                     ( t_mve == null ) )
                {
                    outStream.printf("Errors allocating t_col. t_colID = %d, " +
                                     "t_mveID = %d.\n", t_colID, t_mveID);
                }
                
                if ( ( t_onset == null ) || ( t_offset == null ) )
                {
                    outStream.printf(
                            "allocation of t_onset and/or t_offset failed.\n");
                }
                
                if ( ( t_arg_list == null ) || ( t_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of t_matrix.\n");
                }
                
                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        
        /* now allocate the base cells for the copy tests */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_cell0 = new DataCell(db, f_colID, f_mveID);
                f_cell0.setOrd(10);
                f_cell1 = new DataCell(db, "f_cell1", f_colID, f_mveID);
                f_cell2 = new DataCell(db, "f_cell2", f_colID, f_mveID, 
                                       f_onset, f_offset, f_matrix);
                i_cell0 = new DataCell(db, i_colID, i_mveID);
                i_cell1 = new DataCell(db, "i_cell1", i_colID, i_mveID);
                i_cell2 = new DataCell(db, "i_cell2", i_colID, i_mveID, 
                                       i_onset, i_offset, i_matrix);
                m_cell0 = new DataCell(db, m_colID, m_mveID);
                m_cell1 = new DataCell(db, "m_cell1", m_colID, m_mveID);
                m_cell2 = new DataCell(db, "m_cell2", m_colID, m_mveID,
                                       m_onset, m_offset, m_matrix);
                n_cell0 = new DataCell(db, n_colID, n_mveID);
                n_cell1 = new DataCell(db, "n_cell1", n_colID, n_mveID);
                n_cell2 = new DataCell(db, "n_cell2", n_colID, n_mveID,
                                       n_onset, n_offset, n_matrix);
                p_cell0 = new DataCell(db, p_colID, p_mveID);
                p_cell1 = new DataCell(db, "p_cell1", p_colID, p_mveID);
                p_cell2 = new DataCell(db, "p_cell2", p_colID, p_mveID,
                                       p_onset, p_offset, p_matrix);
                t_cell0 = new DataCell(db, t_colID, t_mveID);
                t_cell1 = new DataCell(db, "t_cell1", t_colID, t_mveID);
                t_cell2 = new DataCell(db, "t_cell2", t_colID, t_mveID,
                                       t_onset, t_offset, t_matrix);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_cell0 == null ) ||
                 ( f_cell1 == null ) ||
                 ( f_cell2 == null ) ||
                 ( i_cell0 == null ) ||
                 ( i_cell1 == null ) ||
                 ( i_cell2 == null ) ||
                 ( m_cell0 == null ) ||
                 ( m_cell1 == null ) ||
                 ( m_cell2 == null ) ||
                 ( n_cell0 == null ) ||
                 ( n_cell1 == null ) ||
                 ( n_cell2 == null ) ||
                 ( p_cell0 == null ) ||
                 ( p_cell1 == null ) ||
                 ( p_cell2 == null ) ||
                 ( t_cell0 == null ) ||
                 ( t_cell1 == null ) ||
                 ( t_cell2 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( ( f_cell0 == null ) || 
                         ( f_cell1 == null ) ||
                         ( f_cell2 == null ) )
                    {
                        outStream.printf("f_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( i_cell0 == null ) || 
                         ( i_cell1 == null ) || 
                         ( i_cell2 == null ) )
                    {
                        outStream.printf("i_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( m_cell0 == null ) ||
                         ( m_cell1 == null ) || 
                         ( m_cell2 == null ) )
                    {
                        outStream.printf("m_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( n_cell0 == null ) ||
                         ( n_cell1 == null ) || 
                         ( n_cell2 == null ) )
                    {
                        outStream.printf("n_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( p_cell0 == null ) || 
                         ( p_cell1 == null ) ||
                         ( p_cell2 == null ) )
                    {
                        outStream.printf("p_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( t_cell0 == null ) ||
                         ( t_cell1 == null ) ||
                         ( t_cell2 == null ) )
                    {
                        outStream.printf("t_cell allocation(s) failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyInitialization(db,
                                       f_cell0,
                                       "f_cell0",
                                       null,
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       10,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, f_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       f_cell1,
                                       "f_cell1",
                                       "f_cell1",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, f_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       f_cell2,
                                       "f_cell2",
                                       "f_cell2",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       f_onset,
                                       f_offset,
                                       f_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       i_cell0,
                                       "i_cell0",
                                       null,
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, i_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell1,
                                       "i_cell1",
                                       "i_cell1",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, i_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell2,
                                       "i_cell2",
                                       "i_cell2",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       i_onset,
                                       i_offset,
                                       i_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell0,
                                       "m_cell0",
                                       null,
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, m_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell1,
                                       "m_cell1",
                                       "m_cell1",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, m_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell2,
                                       "m_cell2",
                                       "m_cell2",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       m_onset,
                                       m_offset,
                                       m_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       n_cell0,
                                       "n_cell0",
                                       null,
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, n_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       n_cell1,
                                       "n_cell1",
                                       "n_cell1",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, n_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       n_cell2,
                                       "n_cell2",
                                       "n_cell2",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       n_onset,
                                       n_offset,
                                       n_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       p_cell0,
                                       "p_cell0",
                                       null,
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, p_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       p_cell1,
                                       "p_cell1",
                                       "p_cell1",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, p_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       p_cell2,
                                       "p_cell2",
                                       "p_cell2",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       p_onset,
                                       p_offset,
                                       p_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       t_cell0,
                                       "t_cell0",
                                       null,
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, t_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       t_cell1,
                                       "t_cell1",
                                       "t_cell1",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, t_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       t_cell2,
                                       "t_cell2",
                                       "t_cell2",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       t_onset,
                                       t_offset,
                                       t_matrix,
                                       outStream,
                                       verbose);
            }
        }
        
        /* now create the copies */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_cell0_copy = new DataCell(f_cell0);
                f_cell1_copy = new DataCell(f_cell1);
                f_cell2_copy = new DataCell(f_cell2);
                
                i_cell0_copy = new DataCell(i_cell0);
                i_cell1_copy = new DataCell(i_cell1);
                i_cell2_copy = new DataCell(i_cell2);
                
                m_cell0_copy = new DataCell(m_cell0);
                m_cell1_copy = new DataCell(m_cell1);
                m_cell2_copy = new DataCell(m_cell2);
                
                n_cell0_copy = new DataCell(n_cell0);
                n_cell1_copy = new DataCell(n_cell1);
                n_cell2_copy = new DataCell(n_cell2);
                
                p_cell0_copy = new DataCell(p_cell0);
                p_cell1_copy = new DataCell(p_cell1);
                p_cell2_copy = new DataCell(p_cell2);
                
                t_cell0_copy = new DataCell(t_cell0);
                t_cell1_copy = new DataCell(t_cell1);
                t_cell2_copy = new DataCell(t_cell2);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_cell0_copy == null ) ||
                 ( f_cell1_copy == null ) ||
                 ( f_cell2_copy == null ) ||
                 ( i_cell0_copy == null ) ||
                 ( i_cell1_copy == null ) ||
                 ( i_cell2_copy == null ) ||
                 ( m_cell0_copy == null ) ||
                 ( m_cell1_copy == null ) ||
                 ( m_cell2_copy == null ) ||
                 ( n_cell0_copy == null ) ||
                 ( n_cell1_copy == null ) ||
                 ( n_cell2_copy == null ) ||
                 ( p_cell0_copy == null ) ||
                 ( p_cell1_copy == null ) ||
                 ( p_cell2_copy == null ) ||
                 ( t_cell0_copy == null ) ||
                 ( t_cell1_copy == null ) ||
                 ( t_cell2_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( ( f_cell0_copy == null ) || 
                         ( f_cell1_copy == null ) ||
                         ( f_cell2_copy == null ) )
                    {
                        outStream.printf("f_cell copy allocation(s) failed.\n");
                    }
                    
                    if ( ( i_cell0_copy == null ) || 
                         ( i_cell1_copy == null ) || 
                         ( i_cell2_copy == null ) )
                    {
                        outStream.printf("i_cell copy allocation(s) failed.\n");
                    }
                    
                    if ( ( m_cell0_copy == null ) ||
                         ( m_cell1_copy == null ) || 
                         ( m_cell2_copy == null ) )
                    {
                        outStream.printf("m_cell copy allocation(s) failed.\n");
                    }
                    
                    if ( ( n_cell0_copy == null ) ||
                         ( n_cell1_copy == null ) || 
                         ( n_cell2_copy == null ) )
                    {
                        outStream.printf("n_cell copy allocation(s) failed.\n");
                    }
                    
                    if ( ( p_cell0_copy == null ) || 
                         ( p_cell1_copy == null ) ||
                         ( p_cell2_copy == null ) )
                    {
                        outStream.printf("p_cell copy allocation(s) failed.\n");
                    }
                    
                    if ( ( t_cell0_copy == null ) ||
                         ( t_cell1_copy == null ) ||
                         ( t_cell2_copy == null ) )
                    {
                        outStream.printf("t_cell copy allocation(s) failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyDataCellCopy(f_cell0,
                                               f_cell0_copy,
                                               outStream,
                                               verbose,
                                               "f_cell0",
                                               "f_cell0_copy");

                failures += VerifyDataCellCopy(f_cell1,
                                               f_cell1_copy,
                                               outStream,
                                               verbose,
                                               "f_cell1",
                                               "f_cell1_copy");

                failures += VerifyDataCellCopy(f_cell2,
                                               f_cell2_copy,
                                               outStream,
                                               verbose,
                                               "f_cell2",
                                               "f_cell2_copy");

                failures += VerifyDataCellCopy(i_cell0,
                                               i_cell0_copy,
                                               outStream,
                                               verbose,
                                               "i_cell0",
                                               "i_cell0_copy");

                failures += VerifyDataCellCopy(i_cell1,
                                               i_cell1_copy,
                                               outStream,
                                               verbose,
                                               "i_cell1",
                                               "i_cell1_copy");

                failures += VerifyDataCellCopy(i_cell2,
                                               i_cell2_copy,
                                               outStream,
                                               verbose,
                                               "i_cell2",
                                               "i_cell2_copy");

                failures += VerifyDataCellCopy(m_cell0,
                                               m_cell0_copy,
                                               outStream,
                                               verbose,
                                               "m_cell0",
                                               "m_cell0_copy");

                failures += VerifyDataCellCopy(m_cell1,
                                               m_cell1_copy,
                                               outStream,
                                               verbose,
                                               "m_cell1",
                                               "m_cell1_copy");

                failures += VerifyDataCellCopy(m_cell2,
                                               m_cell2_copy,
                                               outStream,
                                               verbose,
                                               "m_cell2",
                                               "m_cell2_copy");

                failures += VerifyDataCellCopy(n_cell0,
                                               n_cell0_copy,
                                               outStream,
                                               verbose,
                                               "n_cell0",
                                               "n_cell0_copy");

                failures += VerifyDataCellCopy(n_cell1,
                                               n_cell1_copy,
                                               outStream,
                                               verbose,
                                               "n_cell1",
                                               "n_cell1_copy");

                failures += VerifyDataCellCopy(n_cell2,
                                               n_cell2_copy,
                                               outStream,
                                               verbose,
                                               "n_cell2",
                                               "n_cell2_copy");

                failures += VerifyDataCellCopy(p_cell0,
                                               p_cell0_copy,
                                               outStream,
                                               verbose,
                                               "p_cell0",
                                               "p_cell0_copy");

                failures += VerifyDataCellCopy(p_cell1,
                                               p_cell1_copy,
                                               outStream,
                                               verbose,
                                               "p_cell1",
                                               "p_cell1_copy");

                failures += VerifyDataCellCopy(p_cell2,
                                               p_cell2_copy,
                                               outStream,
                                               verbose,
                                               "p_cell2",
                                               "p_cell2_copy");

                failures += VerifyDataCellCopy(t_cell0,
                                               t_cell0_copy,
                                               outStream,
                                               verbose,
                                               "t_cell0",
                                               "t_cell0_copy");

                failures += VerifyDataCellCopy(t_cell1,
                                               t_cell1_copy,
                                               outStream,
                                               verbose,
                                               "t_cell1",
                                               "t_cell1_copy");

                failures += VerifyDataCellCopy(t_cell2,
                                               t_cell2_copy,
                                               outStream,
                                               verbose,
                                               "t_cell2",
                                               "t_cell2_copy");
            }
        }
        
        
        /* verify that copy constructor fails on null input */
        if ( failures == 0 )
        {
            c = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                c = new DataCell(null);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( c != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( c != null )
                    {
                        outStream.printf(
                                "new DataCell(null) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("new DataCell(null) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DataCell(null) failed to throw " +
                                "a system error exception.\n");
                    }
                }
            }
        }
        
        
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }
        
        return pass;
        
    } /* DataCell::TestCopyConstructor() */
    
    
    /**
     * TestToStringMethods()
     * 
     * Run a battery of tests on the accessor methods for this class. 
     * 
     *                                              JRM -- 12/4/07
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public static boolean TestToStringMethods(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        Database db = null;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        long f_colID = DBIndex.INVALID_ID;
        long i_colID = DBIndex.INVALID_ID;
        long m_colID = DBIndex.INVALID_ID;
        long n_colID = DBIndex.INVALID_ID;
        long p_colID = DBIndex.INVALID_ID;
        long t_colID = DBIndex.INVALID_ID;
        long f_mveID = DBIndex.INVALID_ID;
        long i_mveID = DBIndex.INVALID_ID;
        long m_mveID = DBIndex.INVALID_ID;
        long n_mveID = DBIndex.INVALID_ID;
        long p_mveID = DBIndex.INVALID_ID;
        long t_mveID = DBIndex.INVALID_ID;
        long fargID;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        Predicate p0 = null;
        Predicate p1 = null;
        Predicate p2 = null;
        MatrixVocabElement f_mve = null;
        MatrixVocabElement i_mve = null;
        MatrixVocabElement m_mve = null;
        MatrixVocabElement n_mve = null;
        MatrixVocabElement p_mve = null;
        MatrixVocabElement t_mve = null;
        DataColumn f_col = null;
        DataColumn i_col = null;
        DataColumn m_col = null;
        DataColumn n_col = null;
        DataColumn p_col = null;
        DataColumn t_col = null;
        TimeStamp f_onset = null;
        TimeStamp f_offset = null;
        TimeStamp i_onset = null;
        TimeStamp i_offset = null;
        TimeStamp m_onset = null;
        TimeStamp m_offset = null;
        TimeStamp n_onset = null;
        TimeStamp n_offset = null;
        TimeStamp p_onset = null;
        TimeStamp p_offset = null;
        TimeStamp t_onset = null;
        TimeStamp t_offset = null;
        FormalArgument farg = null;
        DataValue arg = null;
        Vector<DataValue> f_arg_list = null;
        Vector<DataValue> i_arg_list = null;
        Vector<DataValue> m_arg_list = null;
        Vector<DataValue> n_arg_list = null;
        Vector<DataValue> p_arg_list = null;
        Vector<DataValue> t_arg_list = null;
        Matrix f_matrix = null;
        Matrix i_matrix = null;
        Matrix m_matrix = null;
        Matrix n_matrix = null;
        Matrix p_matrix = null;
        Matrix t_matrix = null;
        DataCell f_cell0 = null;
        DataCell f_cell1 = null;
        DataCell f_cell2 = null;
        DataCell i_cell0 = null;
        DataCell i_cell1 = null;
        DataCell i_cell2 = null;
        DataCell m_cell0 = null;
        DataCell m_cell1 = null;
        DataCell m_cell2 = null;
        DataCell n_cell0 = null;
        DataCell n_cell1 = null;
        DataCell n_cell2 = null;
        DataCell p_cell0 = null;
        DataCell p_cell1 = null;
        DataCell p_cell2 = null;
        DataCell t_cell0 = null;
        DataCell t_cell1 = null;
        DataCell t_cell2 = null;
        DataCell c = null;
        NominalDataValue ndv = null;
        PredDataValue pdv = null;
        QuoteStringDataValue qdv = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0ID);
            
            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);
            pve1ID = db.addPredVE(pve1);
            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);

            pve2 = new PredicateVocabElement(db, "pve2");
            farg = new UnTypedFormalArg(db, "<val>");
            pve2.appendFormalArg(farg);
            pve2ID = db.addPredVE(pve2);
            // get a copy of the databases version of pve2 with ids assigned
            pve2 = db.getPredVE(pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<val>");
            pve3.appendFormalArg(farg);
            pve3ID = db.addPredVE(pve3);
            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);
            
            f_col = new DataColumn(db, "f_col", 
                                   MatrixVocabElement.MatrixType.FLOAT);
            f_colID = db.addColumn(f_col);
            f_col = db.getDataColumn(f_colID);
            f_mveID = f_col.getItsMveID();
            f_mve = db.getMatrixVE(f_mveID);
            f_onset = new TimeStamp(db.getTicks(), 60);
            f_offset = new TimeStamp(db.getTicks(), 120);
            f_arg_list = new Vector<DataValue>();
            fargID = f_mve.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            f_arg_list.add(arg);
            f_matrix = new Matrix(db, f_mveID, f_arg_list);
            
            
            i_col = new DataColumn(db, "i_col", 
                                   MatrixVocabElement.MatrixType.INTEGER);
            i_colID = db.addColumn(i_col);
            i_col = db.getDataColumn(i_colID);
            i_mveID = i_col.getItsMveID();
            i_mve = db.getMatrixVE(i_mveID);
            i_onset = new TimeStamp(db.getTicks(), 180);
            i_offset = new TimeStamp(db.getTicks(), 240);
            i_arg_list = new Vector<DataValue>();
            fargID = i_mve.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 22);
            i_arg_list.add(arg);
            i_matrix = new Matrix(db, i_mveID, i_arg_list);
            
            
            m_col = new DataColumn(db, "m_col", 
                                   MatrixVocabElement.MatrixType.MATRIX);
            m_colID = db.addColumn(m_col);
            m_col = db.getDataColumn(m_colID);
            m_mveID = m_col.getItsMveID();
            m_mve = db.getMatrixVE(m_mveID);
            m_onset = new TimeStamp(db.getTicks(), 300);
            m_offset = new TimeStamp(db.getTicks(), 360);
            // construct the predicate:
            //
            //      pve1(pve2(pve3(j_q_nominal)), "just a quote string")
            //
            p2 = new Predicate(db, pve3ID);
            ndv = new NominalDataValue(db,  p2.getArg(0).getItsFargID(), 
                                      "j_q_nominal");
            p2.replaceArg(0, ndv);

            p1 = new Predicate(db, pve2ID);
            pdv = new PredDataValue(db, p1.getArg(0).getItsFargID(), p2);
            p1.replaceArg(0, pdv);

            p0 = new Predicate(db, pve1ID);
            pdv = new PredDataValue(db, p0.getArg(0).getItsFargID(), p1);
            p0.replaceArg(0, pdv);
            qdv = new QuoteStringDataValue(db, p0.getArg(1).getItsFargID(), 
                                          "just a quote string");
            p0.replaceArg(1, qdv);
            m_arg_list = new Vector<DataValue>();
            fargID = m_mve.getFormalArg(0).getID();
            pdv = new PredDataValue(db, fargID, p0);
            m_arg_list.add(pdv);
            m_matrix = new Matrix(db, m_mveID, m_arg_list);
            
            
            n_col = new DataColumn(db, "n_col", 
                                   MatrixVocabElement.MatrixType.NOMINAL);
            n_colID = db.addColumn(n_col);
            n_col = db.getDataColumn(n_colID);
            n_mveID = n_col.getItsMveID();
            n_mve = db.getMatrixVE(n_mveID);
            n_onset = new TimeStamp(db.getTicks(), 420);
            n_offset = new TimeStamp(db.getTicks(), 480);
            n_arg_list = new Vector<DataValue>();
            fargID = n_mve.getFormalArg(0).getID();
            arg = new NominalDataValue(db, fargID, "a_nominal");
            n_arg_list.add(arg);
            n_matrix = new Matrix(db, n_mveID, n_arg_list);
            
            
            p_col = new DataColumn(db, "p_col", 
                                   MatrixVocabElement.MatrixType.PREDICATE);
            p_colID = db.addColumn(p_col);
            p_col = db.getDataColumn(p_colID);
            p_mveID = p_col.getItsMveID();
            p_mve = db.getMatrixVE(p_mveID);
            p_onset = new TimeStamp(db.getTicks(), 540);
            p_offset = new TimeStamp(db.getTicks(), 600);
            p_arg_list = new Vector<DataValue>();
            fargID = p_mve.getFormalArg(0).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
            p_arg_list.add(arg);
            p_matrix = new Matrix(db, p_mveID, p_arg_list);
            
            
            t_col = new DataColumn(db, "t_col", 
                                   MatrixVocabElement.MatrixType.TEXT);
            t_colID = db.addColumn(t_col);
            t_col = db.getDataColumn(t_colID);
            t_mveID = t_col.getItsMveID();
            t_mve = db.getMatrixVE(t_mveID);
            t_onset = new TimeStamp(db.getTicks(), 660);
            t_offset = new TimeStamp(db.getTicks(), 720);
            t_arg_list = new Vector<DataValue>();
            fargID = t_mve.getFormalArg(0).getID();
            arg = new TextStringDataValue(db, fargID, "a text string");
            t_arg_list.add(arg);
            t_matrix = new Matrix(db, t_mveID, t_arg_list);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
             ( pve2 == null ) ||
             ( pve2ID == DBIndex.INVALID_ID ) ||
             ( pve3 == null ) ||
             ( pve3ID == DBIndex.INVALID_ID ) ||
             ( f_colID == DBIndex.INVALID_ID ) ||
             ( f_col == null ) ||
             ( f_mveID == DBIndex.INVALID_ID ) ||
             ( f_mve == null ) ||
             ( f_onset == null ) ||
             ( f_offset == null ) ||
             ( f_arg_list == null ) ||
             ( f_matrix == null ) ||
             ( i_colID == DBIndex.INVALID_ID ) ||
             ( i_col == null ) ||
             ( i_mveID == DBIndex.INVALID_ID ) ||
             ( i_mve == null ) ||
             ( i_onset == null ) ||
             ( i_offset == null ) ||
             ( i_arg_list == null ) ||
             ( i_matrix == null ) ||
             ( m_colID == DBIndex.INVALID_ID ) ||
             ( m_col == null ) ||
             ( m_mveID == DBIndex.INVALID_ID ) ||
             ( m_mve == null ) ||
             ( m_onset == null ) ||
             ( m_offset == null ) ||
             ( m_arg_list == null ) ||
             ( m_matrix == null ) ||
             ( n_colID == DBIndex.INVALID_ID ) ||
             ( n_col == null ) ||
             ( n_mveID == DBIndex.INVALID_ID ) ||
             ( n_mve == null ) ||
             ( n_onset == null ) ||
             ( n_offset == null ) ||
             ( n_arg_list == null ) ||
             ( n_matrix == null ) ||
             ( p_colID == DBIndex.INVALID_ID ) ||
             ( p_col == null ) ||
             ( p_mveID == DBIndex.INVALID_ID ) ||
             ( p_mve == null ) ||
             ( p_onset == null ) ||
             ( p_offset == null ) ||
             ( p_arg_list == null ) ||
             ( p_matrix == null ) ||
             ( t_colID == DBIndex.INVALID_ID ) ||
             ( t_col == null ) ||
             ( t_mveID == DBIndex.INVALID_ID ) ||
             ( t_mve == null ) ||
             ( t_onset == null ) ||
             ( t_offset == null ) ||
             ( t_arg_list == null ) ||
             ( t_matrix == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) ) 
        {
            failures++;
            
            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( ( pve0 == null ) ||
                     ( pve0ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve0.  pve0ID = %d.\n",
                                     pve0ID);
                }

                if ( ( pve1 == null ) ||
                     ( pve1ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve1.  pve1ID = %d.\n",
                                     pve1ID);
                }

                if ( ( pve2 == null ) ||
                     ( pve2ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve2.  pve2ID = %d.\n",
                                     pve2ID);
                }

                if ( ( pve3 == null ) ||
                     ( pve3ID == DBIndex.INVALID_ID ) )
                {
                    outStream.printf("error allocating pve3.  pve3ID = %d.\n",
                                     pve3ID);
                }
                
                if ( ( f_colID == DBIndex.INVALID_ID ) ||
                     ( f_col == null ) ||
                     ( f_mveID == DBIndex.INVALID_ID ) ||
                     ( f_mve == null ) )
                {
                    outStream.printf("Errors allocating f_col. f_colID = %d, " +
                                     "f_mveID = %d.\n", f_colID, f_mveID);
                }
                
                if ( ( f_onset == null ) || ( f_offset == null ) )
                {
                    outStream.printf(
                            "allocation of f_onset and/or f_offset failed.\n");
                }
                
                if ( ( f_arg_list == null ) || ( f_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of f_matrix.\n");
                }
                
                if ( ( i_colID == DBIndex.INVALID_ID ) ||
                     ( i_col == null ) ||
                     ( i_mveID == DBIndex.INVALID_ID ) ||
                     ( i_mve == null ) )
                {
                    outStream.printf("Errors allocating i_col. i_colID = %d, " +
                                     "i_mveID = %d.\n", i_colID, i_mveID);
                }
                
                if ( ( i_onset == null ) || ( i_offset == null ) )
                {
                    outStream.printf(
                            "allocation of i_onset and/or i_offset failed.\n");
                }
                
                if ( ( i_arg_list == null ) || ( i_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of i_matrix.\n");
                }
                
                if ( ( m_colID == DBIndex.INVALID_ID ) ||
                     ( m_col == null ) ||
                     ( m_mveID == DBIndex.INVALID_ID ) ||
                     ( m_mve == null ) )
                {
                    outStream.printf("Errors allocating m_col. m_colID = %d, " +
                                     "m_mveID = %d.\n", m_colID, m_mveID);
                }
                
                if ( ( m_onset == null ) || ( m_offset == null ) )
                {
                    outStream.printf(
                            "allocation of m_onset and/or m_offset failed.\n");
                }
                
                if ( ( m_arg_list == null ) || ( m_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of m_matrix.\n");
                }
                
                if ( ( n_colID == DBIndex.INVALID_ID ) ||
                     ( n_col == null ) ||
                     ( n_mveID == DBIndex.INVALID_ID ) ||
                     ( n_mve == null ) )
                {
                    outStream.printf("Errors allocating n_col. n_colID = %d, " +
                                     "n_mveID = %d.\n", n_colID, n_mveID);
                }
                
                if ( ( n_onset == null ) || ( n_offset == null ) )
                {
                    outStream.printf(
                            "allocation of n_onset and/or n_offset failed.\n");
                }
                
                if ( ( n_arg_list == null ) || ( n_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of n_matrix.\n");
                }
                
                if ( ( p_colID == DBIndex.INVALID_ID ) ||
                     ( p_col == null ) ||
                     ( p_mveID == DBIndex.INVALID_ID ) ||
                     ( p_mve == null ) )
                {
                    outStream.printf("Errors allocating p_col. p_colID = %d, " +
                                     "p_mveID = %d.\n", p_colID, p_mveID);
                }
                
                if ( ( p_onset == null ) || ( p_offset == null ) )
                {
                    outStream.printf(
                            "allocation of p_onset and/or p_offset failed.\n");
                }
                
                if ( ( p_arg_list == null ) || ( p_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of p_matrix.\n");
                }
                
                if ( ( t_colID == DBIndex.INVALID_ID ) ||
                     ( t_col == null ) ||
                     ( t_mveID == DBIndex.INVALID_ID ) ||
                     ( t_mve == null ) )
                {
                    outStream.printf("Errors allocating t_col. t_colID = %d, " +
                                     "t_mveID = %d.\n", t_colID, t_mveID);
                }
                
                if ( ( t_onset == null ) || ( t_offset == null ) )
                {
                    outStream.printf(
                            "allocation of t_onset and/or t_offset failed.\n");
                }
                
                if ( ( t_arg_list == null ) || ( t_matrix == null ) )
                {
                    outStream.printf("error(s) in allocation of t_matrix.\n");
                }
                
                if ( ! completed )
                {
                    outStream.printf(
                            "test setup failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }
        
        /* now allocate the base cells for the to string tests */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                f_cell0 = new DataCell(db, f_colID, f_mveID);
                f_cell1 = new DataCell(db, "f_cell1", f_colID, f_mveID);
                f_cell2 = new DataCell(db, "f_cell2", f_colID, f_mveID, 
                                       f_onset, f_offset, f_matrix);
                i_cell0 = new DataCell(db, i_colID, i_mveID);
                i_cell1 = new DataCell(db, "i_cell1", i_colID, i_mveID);
                i_cell2 = new DataCell(db, "i_cell2", i_colID, i_mveID, 
                                       i_onset, i_offset, i_matrix);
                m_cell0 = new DataCell(db, m_colID, m_mveID);
                m_cell1 = new DataCell(db, "m_cell1", m_colID, m_mveID);
                m_cell2 = new DataCell(db, "m_cell2", m_colID, m_mveID,
                                       m_onset, m_offset, m_matrix);
                n_cell0 = new DataCell(db, n_colID, n_mveID);
                n_cell1 = new DataCell(db, "n_cell1", n_colID, n_mveID);
                n_cell2 = new DataCell(db, "n_cell2", n_colID, n_mveID,
                                       n_onset, n_offset, n_matrix);
                p_cell0 = new DataCell(db, p_colID, p_mveID);
                p_cell1 = new DataCell(db, "p_cell1", p_colID, p_mveID);
                p_cell2 = new DataCell(db, "p_cell2", p_colID, p_mveID,
                                       p_onset, p_offset, p_matrix);
                t_cell0 = new DataCell(db, t_colID, t_mveID);
                t_cell1 = new DataCell(db, "t_cell1", t_colID, t_mveID);
                t_cell2 = new DataCell(db, "t_cell2", t_colID, t_mveID,
                                       t_onset, t_offset, t_matrix);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( f_cell0 == null ) ||
                 ( f_cell1 == null ) ||
                 ( f_cell2 == null ) ||
                 ( i_cell0 == null ) ||
                 ( i_cell1 == null ) ||
                 ( i_cell2 == null ) ||
                 ( m_cell0 == null ) ||
                 ( m_cell1 == null ) ||
                 ( m_cell2 == null ) ||
                 ( n_cell0 == null ) ||
                 ( n_cell1 == null ) ||
                 ( n_cell2 == null ) ||
                 ( p_cell0 == null ) ||
                 ( p_cell1 == null ) ||
                 ( p_cell2 == null ) ||
                 ( t_cell0 == null ) ||
                 ( t_cell1 == null ) ||
                 ( t_cell2 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( ( f_cell0 == null ) || 
                         ( f_cell1 == null ) ||
                         ( f_cell2 == null ) )
                    {
                        outStream.printf("f_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( i_cell0 == null ) || 
                         ( i_cell1 == null ) || 
                         ( i_cell2 == null ) )
                    {
                        outStream.printf("i_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( m_cell0 == null ) ||
                         ( m_cell1 == null ) || 
                         ( m_cell2 == null ) )
                    {
                        outStream.printf("m_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( n_cell0 == null ) ||
                         ( n_cell1 == null ) || 
                         ( n_cell2 == null ) )
                    {
                        outStream.printf("n_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( p_cell0 == null ) || 
                         ( p_cell1 == null ) ||
                         ( p_cell2 == null ) )
                    {
                        outStream.printf("p_cell allocation(s) failed.\n");
                    }
                    
                    if ( ( t_cell0 == null ) ||
                         ( t_cell1 == null ) ||
                         ( t_cell2 == null ) )
                    {
                        outStream.printf("t_cell allocation(s) failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                                "cell allocations failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("cell allocations threw " +
                                          "system error exception: \"%s\".\n",
                                          systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyInitialization(db,
                                       f_cell0,
                                       "f_cell0",
                                       null,
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, f_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       f_cell1,
                                       "f_cell1",
                                       "f_cell1",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, f_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       f_cell2,
                                       "f_cell2",
                                       "f_cell2",
                                       f_colID,
                                       f_mveID,
                                       MatrixVocabElement.MatrixType.FLOAT,
                                       -1,
                                       f_onset,
                                       f_offset,
                                       f_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       i_cell0,
                                       "i_cell0",
                                       null,
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, i_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell1,
                                       "i_cell1",
                                       "i_cell1",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, i_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       i_cell2,
                                       "i_cell2",
                                       "i_cell2",
                                       i_colID,
                                       i_mveID,
                                       MatrixVocabElement.MatrixType.INTEGER,
                                       -1,
                                       i_onset,
                                       i_offset,
                                       i_matrix,
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell0,
                                       "m_cell0",
                                       null,
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, m_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell1,
                                       "m_cell1",
                                       "m_cell1",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, m_mveID),
                                       outStream,
                                       verbose);

                failures += VerifyInitialization(db,
                                       m_cell2,
                                       "m_cell2",
                                       "m_cell2",
                                       m_colID,
                                       m_mveID,
                                       MatrixVocabElement.MatrixType.MATRIX,
                                       -1,
                                       m_onset,
                                       m_offset,
                                       m_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       n_cell0,
                                       "n_cell0",
                                       null,
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, n_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       n_cell1,
                                       "n_cell1",
                                       "n_cell1",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, n_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       n_cell2,
                                       "n_cell2",
                                       "n_cell2",
                                       n_colID,
                                       n_mveID,
                                       MatrixVocabElement.MatrixType.NOMINAL,
                                       -1,
                                       n_onset,
                                       n_offset,
                                       n_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       p_cell0,
                                       "p_cell0",
                                       null,
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, p_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       p_cell1,
                                       "p_cell1",
                                       "p_cell1",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, p_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       p_cell2,
                                       "p_cell2",
                                       "p_cell2",
                                       p_colID,
                                       p_mveID,
                                       MatrixVocabElement.MatrixType.PREDICATE,
                                       -1,
                                       p_onset,
                                       p_offset,
                                       p_matrix,
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       t_cell0,
                                       "t_cell0",
                                       null,
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, t_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       t_cell1,
                                       "t_cell1",
                                       "t_cell1",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       new TimeStamp(db.getTicks(), 0),
                                       new TimeStamp(db.getTicks(), 0),
                                       new Matrix(db, t_mveID),
                                       outStream,
                                       verbose);
                
                failures += VerifyInitialization(db,
                                       t_cell2,
                                       "t_cell2",
                                       "t_cell2",
                                       t_colID,
                                       t_mveID,
                                       MatrixVocabElement.MatrixType.TEXT,
                                       -1,
                                       t_onset,
                                       t_offset,
                                       t_matrix,
                                       outStream,
                                       verbose);
            }
        }
        
        if ( failures == 0 )
        {
            /* Run the to string tests:
             *
             * The tests here are both rigorous and lax.  
             *
             * The are rigorous in  the sense that we running tests on every 
             * type of cell, produced by all forms of the DataCell constructors.
             *
             * They are lax in that we don't concern outselves with creating
             * cells whose values are representative of the entire range of 
             * values that can be represented.  This should be OK, as we 
             * test this extensively in the DataValue classes.
             */
            String f_cell0_string = "(-1, 00:00:00:000, 00:00:00:000, (0.0))";
            String f_cell1_string = "(-1, 00:00:00:000, 00:00:00:000, (0.0))";
            String f_cell2_string = "(-1, 00:00:01:000, 00:00:02:000, (11.0))";
    
            String i_cell0_string = "(-1, 00:00:00:000, 00:00:00:000, (0))";
            String i_cell1_string = "(-1, 00:00:00:000, 00:00:00:000, (0))";
            String i_cell2_string = "(-1, 00:00:03:000, 00:00:04:000, (22))";
    
            String m_cell0_string = "(-1, 00:00:00:000, 00:00:00:000, (<val>))";
            String m_cell1_string = "(-1, 00:00:00:000, 00:00:00:000, (<val>))";
            String m_cell2_string = "(-1, 00:00:05:000, 00:00:06:000, " +
                    "(pve1(pve2(pve3(j_q_nominal)), \"just a quote string\")))";
    
            String n_cell0_string = "(-1, 00:00:00:000, 00:00:00:000, ())";
            String n_cell1_string = "(-1, 00:00:00:000, 00:00:00:000, ())";
            String n_cell2_string = "(-1, 00:00:07:000, 00:00:08:000, " +
                                    "(a_nominal))";
    
            String p_cell0_string = "(-1, 00:00:00:000, 00:00:00:000, (()))";
            String p_cell1_string = "(-1, 00:00:00:000, 00:00:00:000, (()))";
            String p_cell2_string = "(-1, 00:00:09:000, 00:00:10:000, " +
                                    "(pve0(<arg1>, <arg2>)))";
    
            String t_cell0_string = "(-1, 00:00:00:000, 00:00:00:000, ())";
            String t_cell1_string = "(-1, 00:00:00:000, 00:00:00:000, ())";
            String t_cell2_string = "(-1, 00:00:11:000, 00:00:12:000, " +
                                    "(a text string))";
            
            
            String f_cell0_DBstring = 
                "(DataCell (id 0) " +
                        "(itsColID 17) " +
                        "(itsMveID 11) " +
                        "(itsMveType FLOAT) " +
                        "(ord -1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val (Matrix (mveID 11) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 0) " +
                                            "(itsFargID 12) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 0.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0))))))))";
            String f_cell1_DBstring =
                "(DataCell (id 0) " +
                        "(itsColID 17) " +
                        "(itsMveID 11) " +
                        "(itsMveType FLOAT) " +
                        "(ord -1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val (Matrix (mveID 11) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 0) " +
                                            "(itsFargID 12) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 0.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0))))))))";
            String f_cell2_DBstring =
                "(DataCell (id 0) " +
                        "(itsColID 17) " +
                        "(itsMveID 11) " +
                        "(itsMveType FLOAT) " +
                        "(ord -1) " +
                        "(onset (60,00:00:01:000)) " +
                        "(offset (60,00:00:02:000)) " +
                        "(val (Matrix (mveID 11) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((FloatDataValue (id 0) " +
                                            "(itsFargID 12) " +
                                            "(itsFargType FLOAT) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 11.0) " +
                                            "(subRange false) " +
                                            "(minVal 0.0) " +
                                            "(maxVal 0.0))))))))";
    
            String i_cell0_DBstring = 
                "(DataCell (id 0) " +
                        "(itsColID 24) " +
                        "(itsMveID 18) " +
                        "(itsMveType INTEGER) " +
                        "(ord -1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val (Matrix (mveID 18) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((IntDataValue (id 0) " +
                                            "(itsFargID 19) " +
                                            "(itsFargType INTEGER) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 0) " +
                                            "(subRange false) " +
                                            "(minVal 0) " +
                                            "(maxVal 0))))))))";
            String i_cell1_DBstring = 
                "(DataCell (id 0) " +
                        "(itsColID 24) " +
                        "(itsMveID 18) " +
                        "(itsMveType INTEGER) " +
                        "(ord -1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val (Matrix (mveID 18) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((IntDataValue (id 0) " +
                                            "(itsFargID 19) " +
                                            "(itsFargType INTEGER) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 0) " +
                                            "(subRange false) " +
                                            "(minVal 0) " +
                                            "(maxVal 0))))))))";
            String i_cell2_DBstring = 
                "(DataCell (id 0) " +
                        "(itsColID 24) " +
                        "(itsMveID 18) " +
                        "(itsMveType INTEGER) " +
                        "(ord -1) " +
                        "(onset (60,00:00:03:000)) " +
                        "(offset (60,00:00:04:000)) " +
                        "(val (Matrix (mveID 18) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((IntDataValue (id 0) " +
                                            "(itsFargID 19) " +
                                            "(itsFargType INTEGER) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 22) " +
                                            "(subRange false) " +
                                            "(minVal 0) " +
                                            "(maxVal 0))))))))";
    
            String m_cell0_DBstring = 
                "(DataCell (id 0) " +
                        "(itsColID 31) " +
                        "(itsMveID 25) " +
                        "(itsMveType MATRIX) " +
                        "(ord -1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val (Matrix (mveID 25) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((UndefinedDataValue (id 0) " +
                                            "(itsFargID 26) " +
                                            "(itsFargType UNTYPED) " +
                                            "(itsCellID 0) " +
                                            "(itsValue <val>) " +
                                            "(subRange false))))))))";
            String m_cell1_DBstring =
                "(DataCell (id 0) " +
                        "(itsColID 31) " +
                        "(itsMveID 25) " +
                        "(itsMveType MATRIX) " +
                        "(ord -1) " +
                        "(onset (60,00:00:00:000)) " +
                        "(offset (60,00:00:00:000)) " +
                        "(val (Matrix (mveID 25) " +
                                    "(varLen false) " +
                                    "(argList " +
                                        "((UndefinedDataValue (id 0) " +
                                            "(itsFargID 26) " +
                                            "(itsFargType UNTYPED) " +
                                            "(itsCellID 0) " +
                                            "(itsValue <val>) " +
                                            "(subRange false))))))))";
            String m_cell2_DBstring =
                "(DataCell (id 0) " +
                  "(itsColID 31) " +
                  "(itsMveID 25) " +
                  "(itsMveType MATRIX) " +
                  "(ord -1) " +
                  "(onset (60,00:00:05:000)) " +
                  "(offset (60,00:00:06:000)) " +
                  "(val " +
                    "(Matrix " +
                      "(mveID 25) " +
                      "(varLen false) " +
                      "(argList " +
                        "((PredDataValue (id 0) " +
                          "(itsFargID 26) " +
                          "(itsFargType UNTYPED) " +
                          "(itsCellID 0) " +
                          "(itsValue " +
                            "(predicate (id 0) " +
                              "(predID 4) " +
                              "(predName pve1) " +
                              "(varLen false) " +
                              "(argList " +
                                "((PredDataValue (id 0) " +
                                  "(itsFargID 5) " +
                                  "(itsFargType UNTYPED) " +
                                  "(itsCellID 0) " +
                                  "(itsValue " +
                                    "(predicate (id 0) " +
                                      "(predID 7) " +
                                      "(predName pve2) " +
                                      "(varLen false) " +
                                      "(argList " +
                                        "((PredDataValue (id 0) " +
                                          "(itsFargID 8) " +
                                          "(itsFargType UNTYPED) " +
                                          "(itsCellID 0) " +
                                          "(itsValue " +
                                            "(predicate (id 0) " +
                                              "(predID 9) " +
                                              "(predName pve3) " +
                                              "(varLen false) " +
                                              "(argList " +
                                                "((NominalDataValue (id 0) " +
                                                  "(itsFargID 10) " +
                                                  "(itsFargType UNTYPED) " +
                                                  "(itsCellID 0) " +
                                                  "(itsValue j_q_nominal) " +
                                                  "(subRange false))))))) " +
                                          "(subRange false))))))) " +
                                  "(subRange false)), " +
                        "(QuoteStringDataValue (id 0) " +
                          "(itsFargID 6) " +
                          "(itsFargType UNTYPED) " +
                          "(itsCellID 0) " +
                          "(itsValue just a quote string) " +
                          "(subRange false))))))) " +
                "(subRange false))))))))";
    
            String n_cell0_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 38) (" +
                    "itsMveID 32) " +
                    "(itsMveType NOMINAL) " +
                    "(ord -1) " +
                    "(onset (60,00:00:00:000)) " +
                    "(offset (60,00:00:00:000)) " +
                    "(val " +
                        "(Matrix (mveID 32) " +
                            "(varLen false) " +
                            "(argList " +
                                "((NominalDataValue (id 0) " +
                                    "(itsFargID 33) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false))))))))";
            String n_cell1_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 38) (" +
                    "itsMveID 32) " +
                    "(itsMveType NOMINAL) " +
                    "(ord -1) " +
                    "(onset (60,00:00:00:000)) " +
                    "(offset (60,00:00:00:000)) " +
                    "(val " +
                        "(Matrix (mveID 32) " +
                            "(varLen false) " +
                            "(argList " +
                                "((NominalDataValue (id 0) " +
                                    "(itsFargID 33) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false))))))))";
            String n_cell2_DBstring = 
                "(DataCell (id 0) " +
                    "(itsColID 38) (" +
                    "itsMveID 32) " +
                    "(itsMveType NOMINAL) " +
                    "(ord -1) " +
                    "(onset (60,00:00:07:000)) " +
                    "(offset (60,00:00:08:000)) " +
                    "(val " +
                        "(Matrix (mveID 32) " +
                            "(varLen false) " +
                            "(argList " +
                                "((NominalDataValue (id 0) " +
                                    "(itsFargID 33) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue a_nominal) " +
                                    "(subRange false))))))))";
    
            String p_cell0_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 45) " +
                    "(itsMveID 39) " +
                    "(itsMveType PREDICATE) " +
                    "(ord -1) " +
                    "(onset (60,00:00:00:000)) " +
                    "(offset (60,00:00:00:000)) " +
                    "(val " +
                        "(Matrix (mveID 39) " +
                            "(varLen false) " +
                            "(argList " +
                                "((PredDataValue (id 0) " +
                                    "(itsFargID 40) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue ()) " +
                                    "(subRange false))))))))";
            String p_cell1_DBstring = 
                "(DataCell (id 0) " +
                    "(itsColID 45) " +
                    "(itsMveID 39) " +
                    "(itsMveType PREDICATE) " +
                    "(ord -1) " +
                    "(onset (60,00:00:00:000)) " +
                    "(offset (60,00:00:00:000)) " +
                    "(val " +
                        "(Matrix (mveID 39) " +
                            "(varLen false) " +
                            "(argList " +
                                "((PredDataValue (id 0) " +
                                    "(itsFargID 40) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue ()) " +
                                    "(subRange false))))))))";
            String p_cell2_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 45) " +
                    "(itsMveID 39) " +
                    "(itsMveType PREDICATE) " +
                    "(ord -1) " +
                    "(onset (60,00:00:09:000)) " +
                    "(offset (60,00:00:10:000)) " +
                    "(val " +
                        "(Matrix (mveID 39) " +
                            "(varLen false) " +
                            "(argList " +
                                "((PredDataValue (id 0) " +
                                    "(itsFargID 40) " +
                                    "(itsFargType PREDICATE) " +
                                    "(itsCellID 0) " +
                                    "(itsValue " +
                                        "(predicate (id 0) " +
                                            "(predID 1) " +
                                            "(predName pve0) " +
                                            "(varLen false) " +
                                            "(argList " +
                                                "((UndefinedDataValue (id 0) " +
                                                    "(itsFargID 2) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg1>) " +
                                                    "(subRange false)), " +
                                                "(UndefinedDataValue (id 0) " +
                                                    "(itsFargID 3) " +
                                                    "(itsFargType UNTYPED) " +
                                                    "(itsCellID 0) " +
                                                    "(itsValue <arg2>) " +
                                                    "(subRange false))))))) " +
                                    "(subRange false))))))))";
    
            String t_cell0_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 52) " +
                    "(itsMveID 46) " +
                    "(itsMveType TEXT) " +
                    "(ord -1) " +
                    "(onset (60,00:00:00:000)) " +
                    "(offset (60,00:00:00:000)) " +
                    "(val " +
                        "(Matrix (mveID 46) " +
                            "(varLen false) " +
                            "(argList " +
                                "((TextStringDataValue (id 0) " +
                                    "(itsFargID 47) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false))))))))";
            String t_cell1_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 52) " +
                    "(itsMveID 46) " +
                    "(itsMveType TEXT) " +
                    "(ord -1) " +
                    "(onset (60,00:00:00:000)) " +
                    "(offset (60,00:00:00:000)) " +
                    "(val " +
                        "(Matrix (mveID 46) " +
                            "(varLen false) " +
                            "(argList " +
                                "((TextStringDataValue (id 0) " +
                                    "(itsFargID 47) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue <null>) " +
                                    "(subRange false))))))))";
            String t_cell2_DBstring =
                "(DataCell (id 0) " +
                    "(itsColID 52) " +
                    "(itsMveID 46) " +
                    "(itsMveType TEXT) " +
                    "(ord -1) " +
                    "(onset (60,00:00:11:000)) " +
                    "(offset (60,00:00:12:000)) " +
                    "(val " +
                        "(Matrix (mveID 46) " +
                            "(varLen false) " +
                            "(argList " +
                                "((TextStringDataValue (id 0) " +
                                    "(itsFargID 47) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue a text string) " +
                                    "(subRange false))))))))";
            
            if ( f_cell0.toString().compareTo(f_cell0_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected f_cell0.toString(): \"%s\".\n", 
                                     f_cell0.toString());
                }
            }
            
            if ( f_cell0.toDBString().compareTo(f_cell0_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected f_cell0.toDBString(): \"%s\".\n", 
                                     f_cell0.toDBString());
                }
            }
            
            if ( f_cell1.toString().compareTo(f_cell1_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected f_cell1.toString(): \"%s\".\n", 
                                     f_cell1.toString());
                }
            }
            
            if ( f_cell1.toDBString().compareTo(f_cell1_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected f_cell1.toDBString(): \"%s\".\n", 
                                     f_cell1.toDBString());
                }
            }
            
            if ( f_cell2.toString().compareTo(f_cell2_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected f_cell2.toString(): \"%s\".\n", 
                                     f_cell2.toString());
                }
            }
            
            if ( f_cell2.toDBString().compareTo(f_cell2_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected f_cell2.toDBString(): \"%s\".\n", 
                                     f_cell2.toDBString());
                }
            }
            
            /*******************************************************/
            
            if ( i_cell0.toString().compareTo(i_cell0_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected i_cell0.toString(): \"%s\".\n", 
                                     i_cell0.toString());
                }
            }
            
            if ( i_cell0.toDBString().compareTo(i_cell0_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected i_cell0.toDBString(): \"%s\".\n", 
                                     i_cell0.toDBString());
                }
            }
            
            if ( i_cell1.toString().compareTo(i_cell1_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected i_cell1.toString(): \"%s\".\n", 
                                     i_cell1.toString());
                }
            }
            
            if ( i_cell1.toDBString().compareTo(i_cell1_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected i_cell1.toDBString(): \"%s\".\n", 
                                     i_cell1.toDBString());
                }
            }
            
            if ( i_cell2.toString().compareTo(i_cell2_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected i_cell2.toString(): \"%s\".\n", 
                                     i_cell2.toString());
                }
            }
            
            if ( i_cell2.toDBString().compareTo(i_cell2_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected i_cell2.toDBString(): \"%s\".\n", 
                                     i_cell2.toDBString());
                }
            }
            
            /*******************************************************/
            
            if ( m_cell0.toString().compareTo(m_cell0_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected m_cell0.toString(): \"%s\".\n", 
                                     m_cell0.toString());
                }
            }
            
            if ( m_cell0.toDBString().compareTo(m_cell0_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected m_cell0.toDBString(): \"%s\".\n", 
                                     m_cell0.toDBString());
                }
            }
            
            if ( m_cell1.toString().compareTo(m_cell1_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected m_cell1.toString(): \"%s\".\n", 
                                     m_cell1.toString());
                }
            }
            
            if ( m_cell1.toDBString().compareTo(m_cell1_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected m_cell1.toDBString(): \"%s\".\n", 
                                     m_cell1.toDBString());
                }
            }
            
            if ( m_cell2.toString().compareTo(m_cell2_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected m_cell2.toString(): \"%s\".\n", 
                                     m_cell2.toString());
                }
            }
            
            if ( m_cell2.toDBString().compareTo(m_cell2_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected m_cell2.toDBString(): \"%s\".\n", 
                                     m_cell2.toDBString());
                }
            }
            
            /*******************************************************/
            
            if ( n_cell0.toString().compareTo(n_cell0_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected n_cell0.toString(): \"%s\".\n", 
                                     n_cell0.toString());
                }
            }
            
            if ( n_cell0.toDBString().compareTo(n_cell0_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected n_cell0.toDBString(): \"%s\".\n", 
                                     n_cell0.toDBString());
                }
            }
            
            if ( n_cell1.toString().compareTo(n_cell1_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected n_cell1.toString(): \"%s\".\n", 
                                     n_cell1.toString());
                }
            }
            
            if ( n_cell1.toDBString().compareTo(n_cell1_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected n_cell1.toDBString(): \"%s\".\n", 
                                     n_cell1.toDBString());
                }
            }
            
            if ( n_cell2.toString().compareTo(n_cell2_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected n_cell2.toString(): \"%s\".\n", 
                                     n_cell2.toString());
                }
            }
            
            if ( n_cell2.toDBString().compareTo(n_cell2_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected n_cell2.toDBString(): \"%s\".\n", 
                                     n_cell2.toDBString());
                }
            }
            
            /*******************************************************/
            
            if ( p_cell0.toString().compareTo(p_cell0_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected p_cell0.toString(): \"%s\".\n", 
                                     p_cell0.toString());
                }
            }
            
            if ( p_cell0.toDBString().compareTo(p_cell0_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected p_cell0.toDBString(): \"%s\".\n", 
                                     p_cell0.toDBString());
                }
            }
            
            if ( p_cell1.toString().compareTo(p_cell1_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected p_cell1.toString(): \"%s\".\n", 
                                     p_cell1.toString());
                }
            }
            
            if ( p_cell1.toDBString().compareTo(p_cell1_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected p_cell1.toDBString(): \"%s\".\n", 
                                     p_cell1.toDBString());
                }
            }
            
            if ( p_cell2.toString().compareTo(p_cell2_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected p_cell2.toString(): \"%s\".\n", 
                                     p_cell2.toString());
                }
            }
            
            if ( p_cell2.toDBString().compareTo(p_cell2_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected p_cell2.toDBString(): \"%s\".\n", 
                                     p_cell2.toDBString());
                }
            }
            
            /*******************************************************/
            
            if ( t_cell0.toString().compareTo(t_cell0_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected t_cell0.toString(): \"%s\".\n", 
                                     t_cell0.toString());
                }
            }
            
            if ( t_cell0.toDBString().compareTo(t_cell0_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected t_cell0.toDBString(): \"%s\".\n", 
                                     t_cell0.toDBString());
                }
            }
            
            if ( t_cell1.toString().compareTo(t_cell1_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected t_cell1.toString(): \"%s\".\n", 
                                     t_cell1.toString());
                }
            }
            
            if ( t_cell1.toDBString().compareTo(t_cell1_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected t_cell1.toDBString(): \"%s\".\n", 
                                     t_cell1.toDBString());
                }
            }
            
            if ( t_cell2.toString().compareTo(t_cell2_string) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected t_cell2.toString(): \"%s\".\n", 
                                     t_cell2.toString());
                }
            }
            
            if ( t_cell2.toDBString().compareTo(t_cell2_DBstring) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected t_cell2.toDBString(): \"%s\".\n", 
                                     t_cell2.toDBString());
                }
            }
        }

        
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }
        
        return pass;
        
    } /* DataCell::TestToStringMethods() */
    
        
    /**
     * Verify7ArgConstructorFailure()
     *
     * Verify that the 7 argument constructor fails with the supplied 
     * argument.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None
     */
    
    public static int Verify7ArgConstructorFailure(Database db,
                                   String db_desc,
                                   String comment,
                                   String comment_desc,
                                   long colID,
                                   String colID_desc,
                                   long mveID,
                                   String mveID_desc,
                                   TimeStamp onset,
                                   String onset_desc,
                                   TimeStamp offset,
                                   String offset_desc,
                                   Matrix val,
                                   String val_desc,
                                   java.io.PrintStream outStream,
                                   boolean verbose)
    {
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String systemErrorExceptionString = null;
        DataCell c = null;


        try
        {
            c = new DataCell(db, comment, colID, mveID, onset, offset, val);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( c != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) ) 
        {
            failures++;

            if ( verbose )
            {
                if ( c != null )
                {
                    outStream.printf("new DataCell(%s, %s, %s, %s, %s, %s, " +
                            "%s) returned non-null.\n",
                            db_desc, comment_desc, colID_desc, mveID_desc,
                            onset_desc, offset_desc, val_desc);
                }

                if ( completed )
                {
                    outStream.printf("new DataCell(%s, %s, %s, %s, %s, %s, " +
                            "%s) completed.\n",
                            db_desc, comment_desc, colID_desc, mveID_desc,
                            onset_desc, offset_desc, val_desc);
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.printf("new DataCell(%s, %s, %s, %s, %s, %s, " +
                            "%s) failed to throw a system error exception.\n",
                            db_desc, comment_desc, colID_desc, mveID_desc,
                            onset_desc, offset_desc, val_desc);
                }
            }
        }

        return failures;
        
    } /* DataCell::Verify7ArgConstructorFailure() */

    
    /**
     * VerifyDataCellCopy()
     *
     * Verify that the supplied instances of DataCell are distinct, that they
     * contain no common references (other than db), and that they have the
     * same value.
     *
     *                                              JRM -- 12/3/07
     *
     * Changes:
     *
     *    - None
     */
    
    public static int VerifyDataCellCopy(DataCell base,
                                         DataCell copy,
                                         java.io.PrintStream outStream,
                                         boolean verbose,
                                         String baseDesc,
                                         String copyDesc)
    {
        int failures = 0;
        
        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyDataCellCopy: %s null on entry.\n", 
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyDataCellCopy: %s null on entry.\n", 
                             copyDesc);
        }
        else if ( base == copy )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s == %s.\n", baseDesc, copyDesc);
            }
        }
        
        failures += Cell.VerifyCellCopy((Cell)base, (Cell)copy, outStream, 
                                        verbose, baseDesc, copyDesc);
        
        if ( base.itsMveID != copy.itsMveID )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s.itsMveID == %d != %s.itsMveID == %d.\n", 
                                 baseDesc, base.itsMveID, 
                                 copyDesc, copy.itsMveID);
            }
        }
        
        if ( base.itsMveType != copy.itsMveType )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("%s.itsMveType == %s != %s.itsMveType == %s.\n", 
                                 baseDesc, base.itsMveType.toString(), 
                                 copyDesc, copy.itsMveType.toString());
            }
        }
        
        failures += TimeStamp.VerifyTimeStampCopy(base.onset,
                                                  copy.onset,
                                                  outStream,
                                                  verbose,
                                                  baseDesc + ".onset",
                                                  copyDesc + ".onset");
        
        failures += TimeStamp.VerifyTimeStampCopy(base.offset,
                                                  copy.offset,
                                                  outStream,
                                                  verbose,
                                                  baseDesc + ".offset",
                                                  copyDesc + ".offset");

        failures += Matrix.VerifyMatrixCopy(base.val,
                                            copy.val,
                                            outStream,
                                            verbose,
                                            baseDesc + ".val",
                                            copyDesc + ".val");
        
        return failures;
        
    } /* DataCell::VerifyDataCellCopy() */

    
    /**
     * VerifyInitialization()
     *
     * Verify that the supplied instance of Cell has been correctly 
     * initialized by a constructor.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None
     */
    
    public static int VerifyInitialization(Database db,
                                   DataCell c,
                                   String desc,
                                   String expectedComment,
                                   long expectedColID,
                                   long expectedMveID,
                                   MatrixVocabElement.MatrixType expectedMveType,
                                   int expectedOrd,
                                   TimeStamp expectedOnset,
                                   TimeStamp expectedOffset,
                                   Matrix expectedVal,
                                   java.io.PrintStream outStream,
                                   boolean verbose)
    {
        int failures = 0;
        
        if ( db == null )
        {
            failures++;
            outStream.printf(
                    "DataCell::VerifyInitialization: db null on entry.\n");
        }
        
        if ( c == null )
        {
            failures++;
            outStream.printf(
                    "DataCell::VerifyInitialization: c null on entry.\n");
        }
        
        if ( desc == null )
        {
            failures++;
            outStream.printf(
                    "DataCell::VerifyInitialization: c null on entry.\n");
        }

        if ( c.db != db )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("c.db not initialized correctly.\n");
            }
        }
        
        failures += VerifyInitialization(db, c, desc, expectedColID,
                                         expectedComment, outStream, verbose);
        
        if ( c.itsMveID != expectedMveID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s: c.itsMveID not initialized correctly: %d (%d).\n",
                        desc, c.itsMveID, expectedMveID);
            }
        }
        
        if ( c.itsMveType != expectedMveType )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s: c.itsMveType not initialized correctly: %s (%s).\n",
                        desc, c.itsMveType.toString(), expectedMveType.toString());
            }
        }
        
        if ( c.ord != expectedOrd )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf(
                        "%s: c.ord not initialized correctly: %d (%d).\n",
                        desc, c.ord, expectedOrd);
            }
        }

        failures += TimeStamp.VerifyTimeStampCopy(c.onset,
                                                  expectedOnset,
                                                  outStream,
                                                  verbose,
                                                  desc + ".onset",
                                                  "expectedOnset");

        failures += TimeStamp.VerifyTimeStampCopy(c.offset,
                                                  expectedOffset,
                                                  outStream,
                                                  verbose,
                                                  desc + ".offset",
                                                  "expectedOffset");

        failures += Matrix.VerifyMatrixCopy(c.val,
                                            expectedVal,
                                            outStream,
                                            verbose,
                                            desc + ".val",
                                            "expectedVal");
                
        return failures;
        
    } /* DataCell::VerifyInitialization() */

} // End of DataCell class definition
