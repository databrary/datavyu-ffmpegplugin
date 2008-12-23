/*
 * ColPred.java
 *
 * Created on August 7, 2008, 11:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

import java.util.Vector;


/**
 * Class ColPred
 *
 * Primitive class for column predicates.  Instances of this class are used to
 * store column predicates in a database.  Recall that column predicates
 * are defined by implication when matrix vocab elements.  Thus instance of
 * this class are tightly bound to their host database and its vocab list.
 *
 *                                                  JRM -- 8/7/08
 *
 * @author mainzer
 */
public class ColPred extends DBElement
        implements InternalMatrixVocabElementListener
{

    /*************************************************************************/
    /************************** Type Definitions: ****************************/
    /*************************************************************************/

    /**
     * expectedResult:  Private enumerated type used to specify the expected
     *      result of a test.
     */

    private enum expectedResult
        {succeed, system_error, return_null};


    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /* mveID:   Long containing the ID of the MatrixVocabElement whose
     *      implied column predicate is represented in this instance of
     *      ColPred, or INVALID_ID if the ColPred is undefined.
     *
     * mveName: String containing the name of the column represented in
     *      this instance of ColPred, or the empty string if the ColPred
     *      is undefined.
     *
     *      Note that this is also the name of the associated
     *      MatrixVocabElement -- if any.
     *
     * argList: Vector of data values representing the arguments of the
     *      ColPred represented in this data value, or null if the ColPred
     *      is undefined.
     *
     * varLen:  Boolean flag indicating whether the argument list is of
     *      variable length.
     *
     * cellID:  Long containing the ID of the DataCell in which this instance
     *      of ColPred appears (if any).
     *
     * queryVarOK: Boolean flag used to indicate whether parameters can be
     *      be query variables.
     *
     */

    /** ID of the represented ColPred */
    protected long mveID = DBIndex.INVALID_ID;

    /** Name of the represented mve / ColPred */
    protected String mveName = null;

    /** Argument list of the ColPred */
    protected Vector<DataValue> argList = null;

    /** Whether the ColPred has a variable length argument list */
    protected boolean varLen = false;

    /** ID of cell in which this col pred appears, if any */
    protected long cellID = DBIndex.INVALID_ID;

    /** whether parameters can be query variables */
    protected boolean queryVarOK = false;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * ColPred()
     *
     * Constructor for instances of ColPred.
     *
     * Five versions of this constructor.
     *
     * The first takes only a reference to a database as its parameter and
     * constructs an undefined instance of ColPred (that is, an instance
     * that is not yet an instance of some ColPred in the vocab list).
     *
     * The second takes a reference to a database, and a MatrixVocabElement
     * ID, and constructs a representation of the specified column predicate
     * with an empty/undefined argument list.
     *
     * The third takes a reference to a database, a MatrixVocabElementID,
     * and a vector of DataValue specifying the values assigned to each of the
     * column predicate's arguments, and then constructs an instance of
     * ColPred representing the specified column predicate with the indicated
     * values as its arguments.
     *
     * The fourth takes a reference to an instance of ColPred as an
     * argument, and uses it to create a copy.
     *
     * The fifth is much the same as the fourth, save that if the blindCopy
     * parameter is true, it creates a copy of the supplied ColPred without
     * making reference to the underlying MatrixVocabElement.  This is
     * necessary if the mve has changed, and we need to make a copy of the
     * old version of the ColPred so we can touch it up for changes in
     * the pve.
     *
     *                                              JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     *
     */

    public ColPred(Database db)
        throws SystemErrorException
    {
         super(db);

         final String mName = "ColPred::ColPred(db): ";

         if ( ( db == null ) ||
              ( ! ( db instanceof Database ) ) )
         {
             throw new SystemErrorException(mName + "Bad db param");
         }

         this.mveName = new String("");

    } /* ColPred::ColPred(db) */

    public ColPred(Database db,
                   long mveID)
        throws SystemErrorException
    {
        super(db);

        final String mName = "ColPred::ColPred(db, predID): ";
        DBElement dbe;
        MatrixVocabElement mve;

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID == INVALID_ID");
        }

        if ( mveID != DBIndex.INVALID_ID )
        {
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

            this.mveID = mveID;

            mve = (MatrixVocabElement)dbe;

            this.mveName = mve.getName();

            this.varLen = mve.getVarLen();

            this.argList = this.constructEmptyArgList(mve);
        }
    } /* ColPred::ColPred(db, mveID) */


    public ColPred(Database db,
                   long mveID,
                   java.util.Vector<DataValue> argList)
        throws SystemErrorException
    {
        super(db);

        final String mName = "ColPred::ColPred(db, mveID, argList): ";
        DBElement dbe;
        MatrixVocabElement mve;

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID == INVALID_ID");
        }
        else
        {
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

            this.mveID = mveID;

            mve = (MatrixVocabElement)dbe;

            this.mveName = mve.getName();

            this.varLen = mve.getVarLen();

            this.argList = this.copyArgList(argList, true);
        }
    } /* ColPred::ColPred(db, pveID, argList) */


    public ColPred(ColPred colPred)
        throws SystemErrorException
    {
        super(colPred);

        final String mName = "ColPred::ColPred(colPred): ";

        if ( colPred == null )
        {
            throw new SystemErrorException(mName + "colPred null on entry");
        }

        this.mveID    = colPred.mveID;
        this.mveName  = new String(colPred.mveName);
        this.varLen   = colPred.varLen;
        this.cellID   = colPred.cellID;

        if ( colPred.argList == null )
        {
            this.argList = null;
        }
        else
        {
            this.argList = this.copyArgList(colPred.argList, false);
        }

    } /* ColPred::ColPred(pred) */


    protected ColPred(ColPred colPred,
                      boolean blindCopy)
        throws SystemErrorException
    {
        super(colPred);

        final String mName = "ColPred::ColPred(pred, blindCopy): ";

        if ( colPred == null )
        {
            throw new SystemErrorException(mName + "pred null on entry");
        }

        this.mveID    = colPred.mveID;
        this.mveName  = new String(colPred.mveName);
        this.varLen   = colPred.varLen;
        this.cellID   = colPred.cellID;

        if ( colPred.argList == null )
        {
            this.argList = null;
        }
        else if ( blindCopy )
        {
            this.argList = this.blindCopyArgList(colPred.argList);
        }
        else
        {
            this.argList = this.copyArgList(colPred.argList, false);
        }
    }/* ColPred::ColPred(colPred, blindCopy) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getDB()
     *
     * Return the current value of the db field.
     *
     *                          JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    public Database getDB()
    {

        return this.db;

    } /* ColPred::getdb() */


    /**
     * getCellID()
     *
     * Return the current value of the cellID field.
     *
     *                          JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    public long getCellID()
    {

        return this.cellID;

    } /* ColPred::getCellID() */


    /**
     *
     * getMveID()
     *
     * Return the current value of the mveID field.
     *
     *                          JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    public long getMveID()
    {

        return this.mveID;

    } /* ColPred::getMveID() */


    /**
     * getMveName()
     *
     * Return a copy of the current value of the mveName field.
     *
     *                                      JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    public String getMveName()
    {

        return new String(this.mveName);

    } /* ColPred::getMveName() */


    /**
     * setMveID()
     *
     * Set the ID of the mve of which this instance of ColPred will contain a
     * value.  If requested, try to salvage the argument list (if any).
     *
     *                                          JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    public void setMveID(long mveID,
                         boolean salvage)
        throws SystemErrorException
    {
        final String mName = "ColPred::setMveID(mveID, preserveArgs): ";
        int i;
        int newNumArgs;
        int oldNumArgs;
        FormalArgument fa;
        DataValue ndv;
        DataValue odv;
        MatrixVocabElement mve = null;
        Vector<DataValue> oldArgList;

        if ( mveID == DBIndex.INVALID_ID )
        {
            this.mveID = DBIndex.INVALID_ID;
            this.mveName = "";
            this.argList = null;
            this.varLen = false;
        }
        else
        {
            mve = this.lookupMatrixVE(mveID);

            this.mveID = mveID;

            this.mveName = mve.getName();

            this.varLen = mve.getVarLen();

            if ( ( salvage ) && ( this.argList != null ) )
            {
                newNumArgs = mve.getNumCPFormalArgs();

                if ( newNumArgs <= 0 )
                {
                    throw new SystemErrorException(mName + "newNumArgs <= 0");
                }

                oldNumArgs = this.argList.size();

                if ( oldNumArgs <= 0 )
                {
                    throw new SystemErrorException(mName + "oldNumArgs <= 0");
                }

                oldArgList = this.argList;
                this.argList = new Vector<DataValue>();

                for ( i = 0; i < newNumArgs; i++ )
                {
                    // get the i'th formal argument of the column predicate.
                    // Observe that getCPFormaArg() returns a reference to the
                    // actual formal argument in the MatrixVocabElement data
                    // structure, so we must be careful not to modify it in
                    // any way, or expose the reference to the user.
                    fa = mve.getCPFormalArg(i);

                    if ( fa == null )
                    {
                        throw new SystemErrorException(mName + "no " + i +
                                "th formal argument?!?!");
                    }

                    if ( i < oldNumArgs )
                    {
                        odv = oldArgList.get(i);

                        if ( odv == null )
                        {
                            throw new SystemErrorException(mName +
                                                           "odv == null!?!");
                        }

                        ndv = fa.constructArgWithSalvage(odv);

                    }
                    else /* just create new argument */
                    {
                        ndv = fa.constructEmptyArg();
                    }

                    if ( ndv == null )
                    {
                        throw new SystemErrorException(mName + "ndv == null?!");
                    }

                    this.argList.add(ndv);
                }

            }
            else
            {
                this.argList = this.constructEmptyArgList(mve);
            }

        }

        return;

    } /* ColPred::setPredID(mveID, salvage) */

    /**
     * getVarLen()
     *
     * Return the current value of the varLen field.
     *
     *                          JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    public boolean getVarLen()
    {

        return this.varLen;

    } /* ColPred::getVarLen() */


    /*************************************************************************/
    /********************* MVE Change Management: ****************************/
    /*************************************************************************/

    /**
     * MVEChanged()
     *
     * Needed to implement the InternalMatrixVocabElementListener interface.
     *
     * Advise the host data cell that it contains a column predicate whose
     * associated mve definition has changed.
     *
     *                                            JRM -- 8/8/08
     *
     * Changes:
     *
     *    - None.
     */

    public void MVEChanged(Database db,
                          long MVEID,
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
        final String mName = "ColPred::MVEChanged(): ";
        DBElement dbe = null;
        DataCell dc = null;

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( this.mveID != MVEID )
        {
            throw new SystemErrorException(mName + "mveID mismatch.");
        }

        if ( this.cellID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "this.cellID invalid?!?!");
        }

        dbe = this.db.idx.getElement(this.cellID);

        if ( ! ( dbe instanceof DataCell ) )
        {
            throw new SystemErrorException(mName +
                    "this.cellID doesn't refer to a DataCell?!?!");
        }

        dc = (DataCell)dbe;

        // todo -- fix this
        dc.cascadeUpdateForMVEDefChange(db,
                                        MVEID,
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

    } /* ColPred::MVEChanged() */


    /**
     * MVEDeleted()
     *
     * Needed to implement the InternalMatrixVocabElementListener interface.
     *
     * Advise the host data cell that it contains a column predicate whose
     * associated column and mve has been deleted.
     *
     *                                  JRM -- 8/8/08
     *
     * Changes:
     *
     *    - None.
     */

    public void MVEDeleted(Database db,
                           long MVEID)
        throws SystemErrorException
    {
        final String mName = "ColPred::MVEDeleted(): ";
        DBElement dbe = null;
        DataCell dc = null;

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( this.mveID != MVEID )
        {
            throw new SystemErrorException(mName + "mveID mismatch.");
        }

        if ( this.cellID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "this.cellID invalid?!?!");
        }

        dbe = this.db.idx.getElement(this.cellID);

        if ( ! ( dbe instanceof DataCell ) )
        {
            throw new SystemErrorException(mName +
                    "this.cellID doesn't refer to a DataCell?!?!");
        }

        dc = (DataCell)dbe;

        dc.cascadeUpdateForMVEDeletion(db, MVEID);

        return;

    } /* ColPred::MVEDeleted() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/

    /**
     * argListToDBString()
     *
     * Construct a string containing the values of the arguments in a
     * format that displays the full status of the arguments and
     * facilitates debugging.
     *                                          JRM -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    protected String argListToDBString()
        throws SystemErrorException
    {
        final String mName = "Predicate::argListToDBString(): ";
        int i = 0;
        int numArgs = 0;
        String s;

        if ( this.argList == null )
        {
            s = "(argList ())";
        }
        else
        {
            s = new String("(argList (");

            if ( this.argList == null )
            {
                /* fArgList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!?!");
            }

            numArgs = this.argList.size();

            if ( numArgs <= 0 )
            {
                throw new SystemErrorException(mName + "numArgs <= 0");
            }

            while ( i < (numArgs - 1) )
            {
                s += this.getArg(i).toDBString() + ", ";
                i++;
            }

            s += this.getArg(i).toDBString();

            s += "))";
        }

        return s;

    } /* ColPred::argListToDBString() */


    /**
     * argListToString()
     *
     * Construct a string containing the values of the arguments in the
     * format: (value0, value1, ... value).
     *                                          JRM -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    protected String argListToString()
        throws SystemErrorException
    {
        final String mName = "ColPred::argListToString(): ";
        int i = 0;
        int numArgs = 0;
        String s;

        if ( this.mveID == DBIndex.INVALID_ID )
        {
            s = "()";
        }
        else
        {
            if ( argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!?!");
            }

            numArgs = this.argList.size();

            if ( numArgs <= 0 )
            {
                throw new SystemErrorException(mName + "numArgs <= 0");
            }

            s = new String("(");

            while ( i < (numArgs - 1) )
            {
                s += this.getArg(i).toString() + ", ";
                i++;
            }

            s += getArg(i).toString();

            s += ")";
        }

        return s;

    } /* ColPred::argListToString() */


    /**
     * insertInIndex()
     *
     * This method is called when the DataCell whose value contains this
     * instance of ColPred is first inserted in the database and becomes the
     * first cannonical version of the DataCell.
     *
     * Insert the instance of ColPred in the index and make note of the
     * DataCell ID.
     *
     * If the instance of ColPred is a representation of some mve, register
     * as a listener with the mve, and pass the insert in index message down
     * to the argument list.
     *
     *                                              JRM -- 8/8/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void insertInIndex(long DCID)
        throws SystemErrorException
    {
        final String mName = "ColPred::insertInIndex(): ";

        this.db.idx.addElement(this);

        // this should have been checked well before we were called,
        // so no sanity checks.
        this.cellID = DCID;

        if ( this.mveID != DBIndex.INVALID_ID )
        {
            // TODO: register as a listener for this.mveID

            if ( this.argList == null )
            {
                throw new SystemErrorException(mName + "argList is null?!?!");
            }

            for ( DataValue dv : this.argList )
            {
                dv.setItsPredID(this.id);
                dv.insertInIndex(DCID);
            }
        }

        return;

    } /* ColPred::insertInIndex(DCID) */


    /**
     * lookupMatrixVE()
     *
     * Given an ID, attempt to look up the associated MatrixVocabElement
     * in the database associated with the instance of ColPred.  Return a
     * reference to same.  If there is no such MatrixVocabElement, throw
     * a system error.
     *                                              JRM -- 8/20/07
     *
     * Changes:
     *
     *    - None.
     */

    private MatrixVocabElement lookupMatrixVE(long mveID)
        throws SystemErrorException
    {
        final String mName = "ColPred::lookupMatrixVE(mveID): ";
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

    } /* ColPred::lookupMatrixVE(mveID) */


    /**
     * removeFromIndex()
     *
     * This method is called when the DataCell whose value contains this
     * instance of ColPred is deleted from the database, and thus all the
     * DBElements that constitute its value must be removed from the
     * index.
     *
     * Remove the ColPred from the index.
     *
     * If the instance of ColPred is a representation of some MVE, deregister
     * as a listener with the MVE, and pass the remove from index message down
     * to the argument list.
     *
     *                                              JRM -- 8/8/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void removeFromIndex(long DCID)
        throws SystemErrorException
    {
        final String mName = "ColPred::removeFromIndex(): ";

        if ( this.cellID != DCID )
        {
            throw new SystemErrorException(mName + "cell id mismatch");
        }

        this.db.idx.removeElement(this.id);

        // if the ColPred is not associated with some mve, it doesn't need
        // an ID.
        if ( this.mveID != DBIndex.INVALID_ID )
        {
            // TODO: de-register as a listener for this.mveID

            if ( this.argList == null )
            {
                throw new SystemErrorException(mName + "argList is null?!?!");
            }

            for ( DataValue dv : this.argList )
            {
                dv.removeFromIndex(DCID);
            }
        }

        return;

    } /* ColPred::removeFromIndex(DCID) */


   /**
     * toDBString()
     *
     * Returns a database String representation of the ColPred for comparison
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

    // TODO:  Added cellID to DB string

    public String toDBString()
    {
        String s;

        try
        {
            s = "(colPred (id " + this.id +
                ") (mveID " + this.mveID +
                ") (mveName " + this.mveName +
                ") (varLen " + this.varLen + ") " +
                this.argListToDBString() + "))";
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return s;

    } /* ColPred::toDBString() */


    /**
     * toString()
     *
     * Returns a String representation of the ColPred for display.
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
            s = this.mveName + this.argListToString();
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return (s);

    } /* ColPred::toString() */


    /*************************************************************************/
    /********************* Argument List Management: *************************/
    /*************************************************************************/


    /**
     * blindCopyArgList()
     *
     * Given a reference to a Vector containing an argument list for the
     * ColPred indicated by the current value of mveID, copy the argument
     * list without attempting any sanity checks against the mve.
     *
     * This is necessary if the definition of the mve has changed, and we
     * need a copy of the ColPred to modify into accordance with the new
     * version.
     *
     * Throw a system error if any errors are detected.  Otherwise, return the
     * copy.
     *
     *                                              JRM -- 8/8/08
     *
     * Changes:
     *
     *    - None.
     */

    private Vector<DataValue> blindCopyArgList(Vector<DataValue> srcArgList)
        throws SystemErrorException
    {
        final String mName = "ColPred::blindCopyArgList(srcArgList): ";
        int i;
        int numArgs;
        Vector<DataValue> newArgList = new Vector<DataValue>();
        DataValue dv;
        DataValue cdv = null;

        if ( srcArgList == null )
        {
            throw new SystemErrorException(mName + "srcArgList null on entry");
        }

        numArgs = srcArgList.size();

        if ( numArgs <= 0 )
        {
            throw new SystemErrorException(mName + "numArgs <= 0");
        }

        for ( i = 0; i < numArgs; i++ )
        {
            // get the i'th argument from the argument list. This
            // is the actual argument -- must be careful not to modify it
            // in any way.
            dv = srcArgList.get(i);

            if ( dv == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th source argument?!?!");
            }

            cdv = DataValue.Copy(dv, true);

            newArgList.add(cdv);
        }

        if ( newArgList.size() != numArgs )
        {
            throw new SystemErrorException(mName + "bad arg list len");
        }

        return newArgList;

    } /* ColPred::blindCopyArgList(srcArgList) */


    /**
     * constructEmptyArgList()
     *
     * Given a reference to a MatrixVocabElement, construct an empty
     * argument list as directed by the column predicate formal argument list
     * of the supplied MatrixVocabElement.
     *
     * Return the newly constructed argument list.
     *
     *                                              JRM -- 8/8/08
     *
     * Changes:
     *
     *    - None.
     */

    private Vector<DataValue> constructEmptyArgList(MatrixVocabElement mve)
        throws SystemErrorException
    {
        final String mName = "ColPred::constructEmptyArgList(pve): ";
        int i;
        int numArgs;
        Vector<DataValue> argList = new Vector<DataValue>();
        FormalArgument fa;
        DataValue dv;

        if ( mve == null )
        {
            throw new SystemErrorException(mName + "mve == null");
        }

        if ( ! ( mve instanceof MatrixVocabElement ) )
        {
            throw new SystemErrorException(mName + "mve not a matrixVE");
        }

        numArgs = mve.getNumCPFormalArgs();

        if ( numArgs <= 0 )
        {
            throw new SystemErrorException(mName + "numArgs <= 0");
        }

        for ( i = 0; i < numArgs; i++ )
        {
            // get the i'th formal argument of the column predicate.  Observe
            // that getFormaArg() returns a reference to the actual formal
            // argument in the MatrixVocabElement data structure, so we
            // must be careful not to modify it in any way, or expose the
            // reference to the user.
            fa = mve.getCPFormalArg(i);

            if ( fa == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th formal argument?!?!");
            }

            dv = fa.constructEmptyArg();

            if ( dv == null )
            {
                throw new SystemErrorException(mName + "dv == null?!?!");
            }

            argList.add(dv);
        }

        if ( argList.size() != numArgs )
        {
            throw new SystemErrorException(mName + "bad arg list len");
        }

        return argList;

    } /* ColPred::constructEmptyArgList(pve) */


    /**
     * copyArgList()
     *
     * Given a reference to a Vector containing an argument list for the
     * ColPred indicated by the current value of mveID, attempt to make a
     * copy of that argument list.  Throw a system error if any errors are
     * detected.  Otherwise, return the copy.
     *
     *                                              JRM -- 8/08/08
     *
     * Changes:
     *
     *    - None
     */

    private Vector<DataValue> copyArgList(Vector<DataValue> srcArgList,
                                          boolean clearID)
        throws SystemErrorException
    {
        final String mName = "ColPred::copyArgList(srcArgList, clearID): ";
        int i;
        int numArgs;
        MatrixVocabElement mve;
        Vector<DataValue> newArgList = new Vector<DataValue>();
        FormalArgument fa;
        DataValue dv;
        DataValue cdv = null;

        if ( srcArgList == null )
        {
            throw new SystemErrorException(mName + "srcArgList null on entry");
        }

        if ( this.mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "predID undefined");
        }

        mve = this.lookupMatrixVE(this.mveID);

        numArgs = mve.getNumCPFormalArgs();

        if ( srcArgList.size() != numArgs )
        {
// TODO: delete this eventually
//            System.out.printf("actual/expected num args = %d/%d.\n",
//                              srcArgList.size(), numArgs);
//            int j = 1/0;
            throw new SystemErrorException(mName + "arg list size mis-match");
        }

        if ( numArgs <= 0 )
        {
            throw new SystemErrorException(mName + "numArgs <= 0");
        }

        for ( i = 0; i < numArgs; i++ )
        {
            // get the i'th formal argument of the column predicate.  Observe
            // that getFormaArg() returns a reference to the actual formal
            // argument in the MatrixVocabElement data structure, so we
            // must be careful not to modify it in any way, or expose the
            // reference to the user.
            fa = mve.getCPFormalArg(i);

            if ( fa == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th formal argument?!?!");
            }

            // get the i'th argument from the argument list.  Again, this
            // is the actual argument -- must be careful not to modify it
            // in any way.
            dv = srcArgList.get(i);

            if ( dv == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th source argument?!?!");
            }

            switch (fa.getFargType())
            {
                case COL_PREDICATE:
                    if ( dv instanceof ColPredDataValue )
                    {
                        cdv = new ColPredDataValue((ColPredDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
                    }
                    else if ( ( this.queryVarOK ) &&
                              ( dv instanceof NominalDataValue ) &&
                              ( ((NominalDataValue)dv).isQueryVar() ) )
                    {
                        cdv = new NominalDataValue((NominalDataValue)dv);
                    }
                    else if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i +
                                ": column predicate, undefined DV, " +
                                "or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i +
                                ": column predicate, or undefined DV " +
                                "expected.");
                    }
                    break;

                case FLOAT:
                    if ( dv instanceof FloatDataValue )
                    {
                        cdv = new FloatDataValue((FloatDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
                    }
                    else if ( ( this.queryVarOK ) &&
                              ( dv instanceof NominalDataValue ) &&
                              ( ((NominalDataValue)dv).isQueryVar() ) )
                    {
                        cdv = new NominalDataValue((NominalDataValue)dv);
                    }
                    else if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": float DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": float DV, " +
                                "or undefined DV expected.");
                    }
                    break;

                case INTEGER:
                    if ( dv instanceof IntDataValue )
                    {
                        cdv = new IntDataValue((IntDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
                    }
                    else if ( ( this.queryVarOK ) &&
                              ( dv instanceof NominalDataValue ) &&
                              ( ((NominalDataValue)dv).isQueryVar() ) )
                    {
                        cdv = new NominalDataValue((NominalDataValue)dv);
                    }
                    else if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": integer " +
                                "DV, undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": integer " +
                                "DV, or undefined DV expected.");
                    }
                    break;

                case NOMINAL:
                    if ( dv instanceof NominalDataValue )
                    {
                        cdv = new NominalDataValue((NominalDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
                    }
                    else if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": nominal DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": nominal DV, " +
                                "or undefined DV expected.");
                    }
                    break;

                case PREDICATE:
                    if ( dv instanceof PredDataValue )
                    {
                        cdv = new PredDataValue((PredDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
                    }
                    else if ( ( this.queryVarOK ) &&
                              ( dv instanceof NominalDataValue ) &&
                              ( ((NominalDataValue)dv).isQueryVar() ) )
                    {
                        cdv = new NominalDataValue((NominalDataValue)dv);
                    }
                    else if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": predicate " +
                                "DV, undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": predicate " +
                                "DV, or undefined DV expected.");
                    }
                    break;

                case TIME_STAMP:
                    if ( dv instanceof TimeStampDataValue )
                    {
                        cdv = new TimeStampDataValue((TimeStampDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
                    }
                    else if ( ( this.queryVarOK ) &&
                              ( dv instanceof NominalDataValue ) &&
                              ( ((NominalDataValue)dv).isQueryVar() ) )
                    {
                        cdv = new NominalDataValue((NominalDataValue)dv);
                    }
                    else if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": time stamp " +
                                "DV, undefined DV, or query var DV expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": time stamp " +
                                "DV, or undefined DV expected.");
                    }
                    break;

                case QUOTE_STRING:
                    if ( dv instanceof QuoteStringDataValue )
                    {
                        cdv = new QuoteStringDataValue((QuoteStringDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
                    }
                    else if ( ( this.queryVarOK ) &&
                              ( dv instanceof NominalDataValue ) &&
                              ( ((NominalDataValue)dv).isQueryVar() ) )
                    {
                        cdv = new NominalDataValue((NominalDataValue)dv);
                    }
                    else if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": quote " +
                                "string DV, undefined DV, or query var " +
                                "expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": quote " +
                                "string DV, or undefined DV expected.");
                    }
                    break;

                case TEXT:
                    if ( dv instanceof TextStringDataValue )
                    {
                        cdv = new TextStringDataValue((TextStringDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
                    }
                    else if ( ( this.queryVarOK ) &&
                              ( dv instanceof NominalDataValue ) &&
                              ( ((NominalDataValue)dv).isQueryVar() ) )
                    {
                        cdv = new NominalDataValue((NominalDataValue)dv);
                    }
                    else if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": text " +
                                "string DV, undefined DV, or query var " +
                                "expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": text " +
                                "string DV, or undefined DV expected.");
                    }
                    break;

                case UNTYPED:
                    if ( dv instanceof ColPredDataValue )
                    {
                        cdv = new ColPredDataValue((ColPredDataValue)dv);
                    }
                    else if ( dv instanceof FloatDataValue )
                    {
                        cdv = new FloatDataValue((FloatDataValue)dv);
                    }
                    else if ( dv instanceof IntDataValue )
                    {
                        cdv = new IntDataValue((IntDataValue)dv);
                    }
                    else if ( dv instanceof NominalDataValue )
                    {
                        cdv = new NominalDataValue((NominalDataValue)dv);
                    }
                    else if ( dv instanceof PredDataValue )
                    {
                        cdv = new PredDataValue((PredDataValue)dv);
                    }
                    else if ( dv instanceof TimeStampDataValue )
                    {
                        cdv = new TimeStampDataValue((TimeStampDataValue)dv);
                    }
                    else if ( dv instanceof QuoteStringDataValue )
                    {
                        cdv = new QuoteStringDataValue((QuoteStringDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
                    }
                    else if ( dv instanceof TextStringDataValue )
                    {
                        throw new SystemErrorException(mName +
                                "Text string appeared in an untyped argument.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName +
                                "Unknown subtype of DataValue");
                    }
                    break;

                case UNDEFINED:
                    throw new SystemErrorException(mName +
                            "formal arg type undefined???");
                    /* break statement commented out to keep the compiler happy */
                    // break;

                default:
                    throw new SystemErrorException(mName +
                                                   "Unknown Formal Arg Type");
                    /* break statement commented out to keep the compiler happy */
                    // break;
            }

            if ( clearID )
            {
                cdv.clearID();
            }

            if ( dv.getItsFargID() == DBIndex.INVALID_ID )
            {
                cdv.setItsFargID(fa.getID());
            }
            else if ( dv.getItsFargID() != fa.getID() )
            {
                throw new SystemErrorException(mName + "fargID mismatch");
            }

            newArgList.add(cdv);
        }

        if ( newArgList.size() != numArgs )
        {
            throw new SystemErrorException(mName + "bad arg list len");
        }

        this.argList = newArgList;

        return newArgList;

    } /* ColPred::copyArgList(srcArgList, clearID) */


    /**
     * deregisterWithMve()
     *
     * If the ColPred is defined (i.e. this.mveID is not DBIndex.INVALID_ID,
     * deregister the ColPred with its matrix vocab element as an internal
     * vocal element listener.  Also pass the deregister predicates and/or
     * deregister column predicates messages down to any predicate or column
     * predicate data values that may appear in the column predicate's
     * argument list.
     *
     * This method should only be called if this instance of the column
     * predicate is the cannonical instance -- that is the instance listed
     * in the index.
     *                                              JRM -- 8/8/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterWithMve(boolean cascadeMveDel,
                                     long cascadeMveID,
                                     boolean cascadePveDel,
                                     long cascadePveID)
        throws SystemErrorException
    {
        final String mName = "ColPred::deregisterWithPve(): ";
        DBElement dbe = null;
        MatrixVocabElement mve = null;

        if ( this.db == null )
        {
            throw new SystemErrorException(mName + "this.db is null?!?");
        }

        if ( this.db.idx.getElement(this.id) != this )
        {
            throw new SystemErrorException(mName +
                    "not the cannonical incarnation of the ColPred");
        }

        if ( this.mveID != DBIndex.INVALID_ID )
        {
            if ( ( ! cascadeMveDel ) ||
                 ( cascadeMveID != this.mveID ) ) // must de-register
            {

                dbe = this.db.idx.getElement(this.mveID);

                if ( ! ( dbe instanceof PredicateVocabElement ) )
                {
                    throw new SystemErrorException(mName +
                            "mveID doesn't refer to a mve.");
                }

                mve = (MatrixVocabElement)dbe;

                mve.deregisterInternalListener(this.id);
            }

            // pass the deregister message to the argument list regardless
            for ( DataValue dv : this.argList )
            {
                if ( dv instanceof ColPredDataValue )
                {
                    ((ColPredDataValue)dv).deregisterPreds(cascadeMveDel,
                                                           cascadeMveID,
                                                           cascadePveDel,
                                                           cascadePveID);
                }
                else if ( dv instanceof PredDataValue )
                {
                    ((PredDataValue)dv).deregisterPreds(cascadeMveDel,
                                                        cascadeMveID,
                                                        cascadePveDel,
                                                        cascadePveID);
                }
            }
        }

        return;

    } /* ColPred::deregisterWithMve() */


    /**
     * getArg()
     *
     * Return a reference to the n-th argument if it exists, or null if it
     * doesn't.
     *
     *                                      JRM -- 8/23/07
     */

    protected DataValue getArg(int n)
        throws SystemErrorException
    {
        final String mName = "ColPred::getArg(): ";
        int numArgs;
        DataValue arg = null;

        if ( mveID == DBIndex.INVALID_ID )
        {
            arg = null;
        }
        else if ( argList == null )
        {
            /* argList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "argList unitialized?!?!");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= argList.size() )
        {
            /* n-th formal argument doesn't exist -- return null */
            arg = null;
        }
        else /* we have work to do */
        {
            arg = argList.get(n);

            if ( arg == null )
            {
                throw new SystemErrorException(mName + "arg is null?!?");
            }

            if ( ! ( ( arg instanceof ColPredDataValue ) ||
                     ( arg instanceof FloatDataValue ) ||
                     ( arg instanceof IntDataValue ) ||
                     ( arg instanceof NominalDataValue ) ||
                     ( arg instanceof PredDataValue ) ||
                     ( arg instanceof TimeStampDataValue ) ||
                     ( arg instanceof QuoteStringDataValue ) ||
                     ( arg instanceof TextStringDataValue ) ||
                     ( arg instanceof UndefinedDataValue ) ) )
            {
                throw new SystemErrorException(mName + "arg of unknown type");
            }
        }

        return arg;

    } /* ColPred::getArg() */


    /**
     * getArgCopy()
     *
     * Return a reference to a copy of the n-th argument if it exists, or
     * null if it doesn't.
     *                                      JRM -- 10/3/08
     *
     * Changes:
     *
     *    - None.
     */

    public DataValue getArgCopy(int n)
        throws SystemErrorException
    {
        final String mName = "ColPred::getArgCopy(): ";
        DataValue arg = null;
        DataValue argCopy = null;

        arg = this.getArg(n);

        if ( arg != null )
        {
            argCopy = DataValue.Copy(arg, false);
        }

        return argCopy;

    } /* ColPred::getArgCopy() */


    /**
     * getNumArgs()
     *
     * Return the number of arguments.  Return 0 if the mveID hasn't been
     * specified yet.
     *                                      JRM -- 8/11/08
     *
     * Changes:
     *
     *    - None.
     */

    public int getNumArgs()
        throws SystemErrorException
    {
        final String mName = "ColPred::getNumArgs(): ";
        int numArgs = 0;

        if ( mveID != DBIndex.INVALID_ID )
        {
            if ( argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!?!");
            }

            numArgs = this.argList.size();

            if ( numArgs < 4 )
            {
                throw new SystemErrorException(mName + "numArgs < 4");
            }
        }

        return numArgs;

    } /* ColPred::getNumArgs() */


    /**
     * registerWithMve()
     *
     * If the column predicate is defined (i.e. this.itsMveID is not
     * DBIndex.INVALID_ID, register the column predicate with the matrix
     * vocab element that implies it as an internal vocal element listener.
     * Also pass the register predicates message down to any column predicate
     * or predicate data values that may appear in the column predicate's
     * argument list.
     *
     * This method should only be called if this instance of the column
     * predicate is the cannonical instance -- that is the instance listed
     * in the index.
     *                                              JRM -- 8/11/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerWithMve()
        throws SystemErrorException
    {
        final String mName = "ColPred::registerWithMve(): ";
        DBElement dbe = null;
        MatrixVocabElement mve = null;

        if ( this.id == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "id not set?!?");
        }

        if ( this.db == null )
        {
            throw new SystemErrorException(mName + "this.db is null?!?");
        }

        if ( this.db.idx.getElement(this.id) != this )
        {
            System.out.println(this.toString());
            System.out.println(this.toDBString());
            System.out.println(((ColPred)(this.db.idx.getElement(this.id))).toString());
            System.out.println(((ColPred)(this.db.idx.getElement(this.id))).toDBString());
            int j = 1/0;
            throw new SystemErrorException(mName +
                    "not the cannonical incarnation of the predicate");
        }

        if ( this.mveID != DBIndex.INVALID_ID ) // we have work to do
        {

            dbe = this.db.idx.getElement(this.mveID);

            if ( ! ( dbe instanceof MatrixVocabElement ) )
            {
                throw new SystemErrorException(mName +
                                               "mveID doesn't refer to a pve.");
            }

            mve = (MatrixVocabElement)dbe;

            mve.registerInternalListener(this.id);


            for ( DataValue dv : this.argList )
            {
                if ( dv instanceof PredDataValue )
                {
                    ((PredDataValue)dv).registerPreds();
                }
                else if ( dv instanceof ColPredDataValue )
                {
                    ((ColPredDataValue)dv).registerPreds();
                }
            }
        }

        return;

    } /* Predicate::registerWithPve() */


    /**
     * replaceArg()
     *
     * Replace the argument specified by n with the supplied datavalue.  Throw
     * a system error if any errors are detected.
     *
     *                                              JRM -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */

    public void replaceArg(int n,
                           DataValue newArg)
        throws SystemErrorException
    {
        final String mName = "Predicate::replaceArg(n, newArg): ";
        int i;
        int numArgs;
        MatrixVocabElement mve;
        Vector<DataValue> newArgList = new Vector<DataValue>();
        FormalArgument fa;
        DataValue oldArg = null;

        if ( newArg == null )
        {
            throw new SystemErrorException(mName + "newArg null on entry");
        }
        else if ( this.mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "predID == INVALID_ID");
        }
        else if ( argList == null )
        {
            /* argList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "argList unitialized?!?!");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= argList.size() )
        {
            /* n-th formal argument doesn't exist -- scream and die */
            throw new SystemErrorException(mName + n +
                    "th argument doesn't exist");
        }

        mve = this.lookupMatrixVE(this.mveID);

        // get the n'th formal argument of the predicate.  Observe that
        // getCPFormaArg() returns a reference to the actual formal
        // argument in the column predicate formal argument list in the
        // matrix vocab element data structure, so we must be careful not
        // to modify it in any way, or expose the reference to the user.
        fa = mve.getCPFormalArg(n);

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "no " + n +
                    "th formal argument?!?!");
        }

        // get the n'th argument from the argument list.  Again, this
        // is the actual argument -- must be careful not to modify it
        // in any way.
        oldArg = this.argList.get(n);

        if ( oldArg == null )
        {
            throw new SystemErrorException(mName + "no " + n +
                    "th source argument?!?!");
        }

        this.validateArgAsignment(fa, newArg);
        // delete this eventually.
//        switch (fa.getFargType())
//        {
//            case COL_PREDICATE:
//                if ( ! ( ( newArg instanceof ColPredDataValue ) ||
//                         ( newArg instanceof UndefinedDataValue ) ||
//                         ( ( this.queryVarOK ) ||
//                           ( newArg instanceof NominalDataValue ) &&
//                           ( ((NominalDataValue)newArg).isQueryVar() ) ) ) )
//                {
//                    throw new SystemErrorException(mName +
//                            "Type mismatch: column predicate DV, undefined " +
//                            "DV, or query var expected.");
//                }
//                break;
//
//            case FLOAT:
//                if ( ! ( ( newArg instanceof FloatDataValue ) ||
//                         ( newArg instanceof UndefinedDataValue ) ||
//                         ( ( this.queryVarOK ) ||
//                           ( newArg instanceof NominalDataValue ) &&
//                           ( ((NominalDataValue)newArg).isQueryVar() ) ) ) )
//                {
//                    throw new SystemErrorException(mName +
//                            "Type mismatch: float DV, undefined DV, or query " +
//                            "variable expected.");
//                }
//                break;
//
//            case INTEGER:
//                if ( ! ( ( newArg instanceof IntDataValue ) ||
//                         ( newArg instanceof UndefinedDataValue ) ||
//                         ( ( this.queryVarOK ) ||
//                           ( newArg instanceof NominalDataValue ) &&
//                           ( ((NominalDataValue)newArg).isQueryVar() ) ) ) )
//                {
//                    throw new SystemErrorException(mName +
//                            "Type mismatch: integer DV, undefined DV, or " +
//                            "query variable expected.");
//                }
//                break;
//
//            case NOMINAL:
//                if ( ! ( ( newArg instanceof NominalDataValue ) ||
//                         ( newArg instanceof UndefinedDataValue ) ) )
//                {
//                    throw new SystemErrorException(mName + "Type mismatch: " +
//                            "Nominal DV, or undefined DV expected.");
//                }
//                break;
//
//            case PREDICATE:
//                if ( ! ( ( newArg instanceof PredDataValue ) ||
//                         ( newArg instanceof UndefinedDataValue ) ||
//                         ( ( this.queryVarOK ) ||
//                           ( newArg instanceof NominalDataValue ) &&
//                           ( ((NominalDataValue)newArg).isQueryVar() ) ) ) )
//                {
//                    throw new SystemErrorException(mName +
//                            "Type mismatch: predicate DV, undefined DV, or " +
//                            "query variable expected.");
//                }
//                break;
//
//            case TIME_STAMP:
//                if ( ! ( ( newArg instanceof TimeStampDataValue ) ||
//                         ( newArg instanceof UndefinedDataValue ) ||
//                         ( ( this.queryVarOK ) ||
//                           ( newArg instanceof NominalDataValue ) &&
//                           ( ((NominalDataValue)newArg).isQueryVar() ) ) ) )
//                {
//                    throw new SystemErrorException(mName +
//                            "Type mismatch: time stamp DV, undefined DV, or " +
//                            "query variable expected.");
//                }
//                break;
//
//            case QUOTE_STRING:
//                if ( ! ( ( newArg instanceof QuoteStringDataValue ) ||
//                         ( newArg instanceof UndefinedDataValue ) ||
//                         ( ( this.queryVarOK ) ||
//                           ( newArg instanceof NominalDataValue ) &&
//                           ( ((NominalDataValue)newArg).isQueryVar() ) ) ) )
//                {
//                    throw new SystemErrorException(mName +
//                            "Type mismatch: quote string DV, undefined DV, " +
//                            "or query variable expected.");
//                }
//                break;
//
//            case TEXT:
//                if ( ! ( ( newArg instanceof TextStringDataValue ) ||
//                         ( newArg instanceof UndefinedDataValue ) ||
//                         ( ( this.queryVarOK ) ||
//                           ( newArg instanceof NominalDataValue ) &&
//                           ( ((NominalDataValue)newArg).isQueryVar() ) ) ) )
//                {
//                    throw new SystemErrorException(mName +
//                            "Type mismatch: text string DV, undefined DV, " +
//                            "or query variable expected.");
//                }
//                break;
//
//            case UNTYPED:
//                if ( ! ( ( newArg instanceof ColPredDataValue ) ||
//                         ( newArg instanceof FloatDataValue ) ||
//                         ( newArg instanceof IntDataValue ) ||
//                         ( newArg instanceof NominalDataValue ) ||
//                         ( newArg instanceof PredDataValue ) ||
//                         ( newArg instanceof TimeStampDataValue ) ||
//                         ( newArg instanceof QuoteStringDataValue ) ||
//                         ( newArg instanceof TextStringDataValue ) ||
//                         ( newArg instanceof UndefinedDataValue ) ) )
//                {
//                    throw new SystemErrorException(mName +
//                            "Unknown subtype of DataValue");
//                }
//
//                if ( newArg instanceof TextStringDataValue )
//                {
//                    throw new SystemErrorException(mName + "Type mismatch: " +
//                            "TextStringDataValue can't replace an untyped " +
//                            "formal argument.");
//                }
//                break;
//
//            case UNDEFINED:
//                throw new SystemErrorException(mName +
//                        "formal arg type undefined???");
//                /* break statement commented out to keep the compiler happy */
//                // break;
//
//            default:
//                throw new SystemErrorException(mName +
//                                               "Unknown Formal Arg Type");
//                /* break statement commented out to keep the compiler happy */
//                // break;
//        }

        if ( newArg.getItsFargID() == DBIndex.INVALID_ID )
        {
            newArg.setItsFargID(fa.getID());
        }
        else if ( newArg.getItsFargID() != fa.getID() )
        {
            throw new SystemErrorException(mName + "formal arg ID mismatch");
        }

        if ( this.argList.set(n, newArg) != oldArg )
        {
            throw new SystemErrorException(mName + "replaced wrong arg?!?");
        }

        return;

    } /* Predicate::replaceArg(n, newArg) */


    /**
     * updateForMVEDefChange()
     *
     * If this column predicate is defined by the indicated matrix vocab element,
     * update it for any changes in the implied column predicate.
     *
     * Then, scan the list of data values in the column predicate, and pass an
     * update for matrix vocab element definition change message to any col pred
     * or pred data values that may appear in the argument list.
     *
     *                                          JRM -- 8/11/08
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
        final String mName = "ColPred::updateForMVEDefChange(): ";
        DBElement dbe = null;
        MatrixVocabElement mve = null;

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

        mve = (MatrixVocabElement)dbe;

        if ( this.mveID == mveID )
        {
            if ( nameChanged )
            {
                if ( this.mveName.compareTo(oldName) != 0 )
                {
                    throw new SystemErrorException(mName +
                            "unexpected old name.");
                }
                this.mveName = new String(newName);
            }

            if ( varLenChanged )
            {
                if ( this.varLen != oldVarLen )
                {
                    throw new SystemErrorException(mName +
                            "unexpected old varLen.");
                }
                this.varLen = newVarLen;
            }

            if ( fargListChanged )
            {
                int i;
                int j;
                int numOldArgs;
                int numNewArgs;
                DataValue dv = null;
                Vector<DataValue> newArgList = null;

                newArgList = this.constructEmptyArgList(mve);
                numOldArgs = oldFargList.size();
                numNewArgs = newFargList.size();

                for ( i = 0; i < numOldArgs; i++ )
                {
                    if ( ! cpFargDeleted[i] )
                    {
                        dv = DataValue.Copy(this.getArg(i), true);

                        j = (int)cpo2n[i];

                        if ( ( cpFargNameChanged[j] ) ||
                             ( cpFargSubRangeChanged[j]) ||
                             ( cpFargRangeChanged[j] ) )
                        {
                            // Update the data value for the formal argument
                            // change.
                            dv.updateForFargChange(cpFargNameChanged[j],
                                                   cpFargSubRangeChanged[j],
                                                   cpFargRangeChanged[j],
                                                   oldFargList.get(i),
                                                   newFargList.get(j));
                        }

                        newArgList.set(j, dv);
                    }
                }

                this.argList = newArgList;
            }
        }

        for ( DataValue dv : this.argList )
        {
            if ( dv instanceof ColPredDataValue )
            {
                ((ColPredDataValue)dv).updateForMVEDefChange(db,
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
                                                          fargInserted,
                                                          oldCPFargList,
                                                          newCPFargList);
            }
            else if ( dv instanceof PredDataValue )
            {
                ((PredDataValue)dv).updateForMVEDefChange(db,
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
                                                          fargInserted,
                                                          oldCPFargList,
                                                          newCPFargList);
            }
        }

        return;

    } /* ColPred::updateForMVEDefChange() */


    /**
     * updateForMVEDeletion()
     *
     * It the supplied mveID matches this.mveID, set this.mveID to INVALID_ID.
     *
     * Otherwise, if the column predicate is defined, scan its argument list
     * and pass the update for mve deletion message to any predicate or
     * column predicate data values that may appear in the argument list.
     *
     *                                          JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForMVEDeletion(Database db,
                                        long deletedMveID)
        throws SystemErrorException
    {
        final String mName = "ColPred::updateForMVEDeletion(): ";
        int i;
        int numArgs;
        DBElement dbe = null;
        MatrixVocabElement mve = null;
        FormalArgument fa = null;
        DataValue dv = null;
        ColPredDataValue cpdv = null;
        PredDataValue pdv = null;

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( deletedMveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "deletedMveID invalid.");
        }

        if ( this.mveID == deletedMveID )
        {
            this.setMveID(DBIndex.INVALID_ID, false);
        }
        else if ( this.mveID != DBIndex.INVALID_ID )
        {
            numArgs = this.argList.size();

            if ( numArgs <= 0 )
            {
                throw new SystemErrorException(mName + "numArgs <= 0");
            }

            i = 0;

            while ( i < numArgs )
            {
                dv = this.getArg(i);

                if ( dv == null )
                {
                    throw new SystemErrorException(mName + "arg " + i +
                                                   " is null?!?!");
                }

                if ( dv instanceof ColPredDataValue )
                {
                    cpdv = (ColPredDataValue)dv;

                    if ( cpdv.getItsValueMveID() == deletedMveID )
                    {
                        if ( dv.getItsFargType() ==
                                FormalArgument.fArgType.UNTYPED )
                        {
                            if ( mve == null )
                            {
                                mve = this.lookupMatrixVE(this.mveID);
                            }

                            fa = mve.getCPFormalArg(i);

                            if ( fa == null )
                            {
                                throw new SystemErrorException(mName + "no " +
                                        i + "th CP formal argument?!?!");
                            }

                            dv = fa.constructEmptyArg();

                            dv.setItsPredID(this.id);

                            this.replaceArg(i, dv);
                        }
                        else if ( dv.getItsFargType() ==
                                FormalArgument.fArgType.COL_PREDICATE )
                        {
                            ((ColPredDataValue)dv).updateForMVEDeletion(db, mveID);
                        }
                        else
                        {
                            throw new SystemErrorException(mName + "arg " + i +
                                    " has unexpected fArgType.");
                        }
                    }
                    else
                    {
                        cpdv.updateForMVEDeletion(db, deletedMveID);
                    }
                }
                else if ( dv instanceof PredDataValue )
                {
                    pdv = (PredDataValue)dv;

                    pdv.updateForMVEDeletion(db, deletedMveID);
                }

                i++;
            }
        }

        return;

    } /* PredCol::updateForMVEDeletion() */


    /**
     * updateForPVEDefChange()
     *
     * Scan the list of data values in the column predicate, and pass an
     * update for predicate vocab element definition change message to any
     * column predicate or predicate data values.
     *
     *                                          JRM -- 8/11/08
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
        final String mName = "ColPred::updateForPVEDefChange(): ";
        DBElement dbe = null;
        PredicateVocabElement pve = null;

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

        pve = (PredicateVocabElement)dbe;

        for ( DataValue dv : this.argList )
        {
            if ( dv instanceof PredDataValue )
            {
                ((PredDataValue)dv).updateForPVEDefChange(db,
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
            else if ( dv instanceof ColPredDataValue )
            {
                ((ColPredDataValue)dv).updateForPVEDefChange(db,
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
        }

        return;

    } /* Predicate::updateForPVEDefChange() */


    /**
     * updateForPVEDeletion()
     *
     * If the column predicate is defined, scan its argument list and
     * pass the update for pve deletion message to any predicate or column
     * predicate data values that may appear in the argument list.
     *                                          JRM -- 8/08/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForPVEDeletion(Database db,
                                        long deletedPveID)
        throws SystemErrorException
    {
        final String mName = "ColPred::updateForPVEDeletion(): ";
        int i;
        int numArgs;
        DBElement dbe = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        FormalArgument fa = null;
        DataValue dv = null;
        ColPredDataValue cpdv = null;
        PredDataValue pdv = null;

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( deletedPveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "deletedPveID invalid.");
        }

        if ( this.mveID != DBIndex.INVALID_ID )
        {
            numArgs = this.argList.size();

            if ( numArgs <= 0 )
            {
                throw new SystemErrorException(mName + "numArgs <= 0");
            }

            i = 0;

            while ( i < numArgs )
            {
                dv = this.getArg(i);

                if ( dv == null )
                {
                    throw new SystemErrorException(mName + "arg " + i +
                                                   " is null?!?!");
                }

                if ( dv instanceof PredDataValue )
                {
                    pdv = (PredDataValue)dv;

                    if ( pdv.getItsValuePveID() == deletedPveID )
                    {
                        if ( dv.getItsFargType() ==
                                FormalArgument.fArgType.UNTYPED )
                        {
                            if ( mve == null )
                            {
                                mve = this.lookupMatrixVE(this.mveID);
                            }

                            fa = mve.getCPFormalArg(i);

                            if ( fa == null )
                            {
                                throw new SystemErrorException(mName + "no " +
                                        i + "th CP formal argument?!?!");
                            }

                            dv = fa.constructEmptyArg();

                            dv.setItsPredID(this.id);

                            this.replaceArg(i, dv);
                        }
                        else if ( dv.getItsFargType() ==
                                FormalArgument.fArgType.PREDICATE )
                        {
                            ((PredDataValue)dv).
                                    updateForPVEDeletion(db, deletedPveID);
                        }
                        else
                        {
                            throw new SystemErrorException(mName + "arg " + i +
                                    " has unexpected fArgType.");
                        }
                    }
                    else
                    {
                        pdv.updateForPVEDeletion(db, deletedPveID);
                    }
                }
                else if ( dv instanceof ColPredDataValue )
                {
                    cpdv = (ColPredDataValue)dv;

                    cpdv.updateForPVEDeletion(db, deletedPveID);
                }

                i++;
            }
        }

        return;

    } /* Predicate::updateForPVEDeletion() */


    /**
     * updateIndexForReplacement()
     *
     * When the old incarnation of the canonnical version of a DataCell is
     * replaced with the new, we must update the index so that DataValues,
     * column predicates, and predicates that don't appear in the new
     * incarnation are removed from the index, DataValues, column predicates,
     * and Predicates that are introduced in the new incarnation are inserted
     * in the index, and the index is updated to point to the new versions of
     * DataValues, column predicates, and Predicates that appear in both.
     *
     * If there is no structural change in the underlying mve's and pve's,
     * this task relatively straight forward, as continuing objects will
     * reside in the same location in the old and new argument lists, and
     * will share IDs.  New items will reside in the new version, and have
     * invalid IDs, and items that will cease to exist will reside in the
     * old version, and not have a cognate with the same ID in the same
     * location in the new verson.
     *
     * If there is structural change, things get much more complicated --
     * however we limit the complexity by allowing at most one mve or pve
     * to be modified or deleted in any one cycle.  Thus we are given that
     * at most one of the cascadeMveMod, cascadeMveDel, cascadePveMod, and
     * cascadePveDel parameters will be true.
     *
     *
     * 1) cascadeMveMod == true
     *
     * If cascadeMveMod is true, then a mve has been modified, and the ID of
     * the modified mve is in cascadeMveID.
     *
     * If cascadeMveID == this.mveID, then the definition of the mve that
     * implies the column predicate represented by this instance of ColPred
     * has changed.
     *
     * Thus it is possible that formal arguments have been deleted and/or
     * re-arranged.  Thus instead of looking just in the corresponding location
     * in the old argument list for the old version of an argument in the new
     * list, we must scan the entire old argument list for the old version.
     * Similarly for each item in the old argument list, we must scan the
     * new argument list to verify that there is no new version, and the
     * old argument (and all its descendants -- if any) must be removed from
     * the index.
     *
     * If cascadeMveID != this.mveID, then we can proceed as per the no
     * structural change case -- for this col pred at least.
     *
     *
     * 2) cascadeMveDel == true
     *
     * If cascadeMveDel is true, the a mve has been deleted, and the ID of
     * the deleted mve is in cascadeMveID.
     *
     * In this case, verify that this.mveID != cascadeMveID, and then proceed
     * as per the no structural change case.
     *
     *
     * 3) cascadePveMod == true
     *
     * If cascadePveMod is true, then a pve has been modified, and the ID of
     * the modified pve is in cascadeMveID.
     *
     * Proceed as per the no structural change case.
     *
     *
     * 4) cascadePveDel == true
     *
     * If cascadePveDel is true, then a pve has been deleted, and the ID of
     * the deleted pve is in cascadePveID.
     *
     * Proceed as per the no structural change case.
     *
     *                                      JRM -- 8/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public void updateIndexForReplacement(ColPred oldColPred,
                                          long DCID,
                                          boolean cascadeMveMod,
                                          boolean cascadeMveDel,
                                          long cascadeMveID,
                                          boolean cascadePveMod,
                                          boolean cascadePveDel,
                                          long cascadePveID)
        throws SystemErrorException
    {
        final String mName = "ColPred::updateIndexForReplacement(): ";
        int i = 0;
        MatrixVocabElement mve;
        FormalArgument fa;
        DataValue oldArg = null;
        DataValue newArg = null;
        ColPredFormalArg        cpfa;
        FloatFormalArg          ffa;
        IntFormalArg            ifa;
        NominalFormalArg        nfa;
        PredFormalArg           pfa;
        TimeStampFormalArg      tsfa;
        QuoteStringFormalArg    qsfa;
        TextStringFormalArg     tfa;
        UnTypedFormalArg        ufa;
        ColPredDataValue        new_cpdv;
        FloatDataValue          new_fdv;
        IntDataValue            new_idv;
        NominalDataValue        new_ndv;
        PredDataValue           new_pdv;
        TimeStampDataValue      new_tsdv;
        QuoteStringDataValue    new_qsdv;
        TextStringDataValue     new_tdv;
        ColPredDataValue        old_cpdv;
        FloatDataValue          old_fdv;
        IntDataValue            old_idv;
        NominalDataValue        old_ndv;
        PredDataValue           old_pdv;
        TimeStampDataValue      old_tsdv;
        QuoteStringDataValue    old_qsdv;
        TextStringDataValue     old_tdv;

        if ( oldColPred == null )
        {
            throw new SystemErrorException(mName + "oldColPred is null");
        }

        if ( oldColPred.cellID != DCID )
        {
            throw new SystemErrorException(mName + "oldColPred DCID mismatch.");
        }

        if ( oldColPred.mveID == DBIndex.INVALID_ID )
        {
            // old col pred is undefined -- verify that it was
            // correctly initialized

            if ( ( oldColPred.mveName == null ) ||
                 ( oldColPred.mveName.compareTo("") != 0 ) ||
                 ( oldColPred.argList != null ) ||
                 ( oldColPred.varLen != false ) )
            {
                throw new SystemErrorException(mName +
                        "undefined old col pred with incorrect values");
            }
        }
        else if ( ( cascadeMveDel ) && ( this.mveID == cascadeMveID ) )
        {
            throw new SystemErrorException(mName +
                    "this.mveID == deleted mve ID?!?!?");
        }
        else if ( oldColPred.argList == null )
        {
            throw new SystemErrorException(mName +
                    "oldColPred.argList == null?!?!");
        }

        if ( oldColPred.id == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "oldColPred.id is invalid.");
        }


        if ( this.cellID != DCID )
        {
            throw new SystemErrorException(mName + "DCID mismatch.");
        }

        if ( this.mveID == DBIndex.INVALID_ID )
        {
            // undefined column predicate -- verify that it is
            // correctly initialized
            if ( ( this.mveName == null ) ||
                 ( this.mveName.compareTo("") != 0 ) ||
                 ( this.argList != null ) ||
                 ( this.varLen != false ) )
            {
                throw new SystemErrorException(mName +
                        "undefined col pred with incorrect values");
            }
        }
        else if ( this.argList == null )
        {
            throw new SystemErrorException(mName + "this.argList == null?!?");
        }

        if ( ( this.id != DBIndex.INVALID_ID ) &&
             ( this.id != oldColPred.id ) )
        {
            throw new SystemErrorException(mName +
                    "this.id not invalid and not equal to oldColPred.id");
        }


        if ( this.mveID == DBIndex.INVALID_ID )
        {
            if ( oldColPred.mveID == DBIndex.INVALID_ID )
            {
                this.db.idx.replaceElement(this);
            }
            else
            {
                // we are replacing a column predicate with an undefined predicate.
                if ( this.id == DBIndex.INVALID_ID )
                {
                    oldColPred.removeFromIndex(DCID);
                    this.insertInIndex(DCID);
                }
                else
                {
                    assert ( this.id == oldColPred.id );

                    // Remove the arguments of the old predicate from the
                    // index.
                    for ( DataValue dv : oldColPred.argList )
                    {
                        dv.removeFromIndex(DCID);
                    }

                    // replace the old Predicate with the new in the index.
                    this.db.idx.replaceElement(this);
                }
            }
        }
        else if ( this.mveID != oldColPred.mveID )
        {
            if ( oldColPred.mveID == DBIndex.INVALID_ID )
            {
                // we are replacing an undefined column predicate with a
                // new column predicate
                if ( this.id == DBIndex.INVALID_ID )
                {
                    oldColPred.removeFromIndex(DCID);
                    this.insertInIndex(DCID);
                }
                else
                {
                    assert( this.id == oldColPred.id );

                    this.db.idx.replaceElement(this);

                    // Insert the argument list of the new predicate in the
                    // index.
                    for ( DataValue dv : this.argList )
                    {
                        dv.insertInIndex(DCID);
                    }
                }
            }
            else // oldColPred is defined, and referrs to some other mve
            {
                if ( this.id == DBIndex.INVALID_ID )
                {
                    oldColPred.removeFromIndex(DCID);
                    this.insertInIndex(DCID);
                }
                else
                {
                    assert( this.id == oldColPred.id );

                    this.db.idx.replaceElement(this);

                    for ( DataValue dv : oldColPred.argList )
                    {
                        dv.removeFromIndex(DCID);
                    }

                    for ( DataValue dv : this.argList )
                    {
                        dv.insertInIndex(DCID);
                    }
                }
            }
        }
        else if ( this.id == DBIndex.INVALID_ID )
        {
            // we have a new column predicate that refers to the same mve as
            // the old.  Just remove the old column predicate and all its
            // parameters from the index, and insert the new column predicate
            // and all its parameters.
            assert( this.mveID == oldColPred.mveID );
            assert( this.mveID != DBIndex.INVALID_ID );

            oldColPred.removeFromIndex(DCID);
            this.insertInIndex(DCID);

            for ( DataValue dv : oldColPred.argList )
            {
                dv.removeFromIndex(DCID);
            }

            for ( DataValue dv : this.argList )
            {
                dv.insertInIndex(DCID);
            }
        }
        else if ( ( ! cascadeMveMod ) || ( cascadeMveID != this.mveID ) )
            // note that from previous if statements, we also have:
            // ( this.id == oldColPred.id ) &&
            // ( this.mveID == oldColPred.mveID ) &&
            // ( this.mveID != DBIndex.INVALID_ID )
       {
            assert( this.id == oldColPred.id );
            assert( this.mveID == oldColPred.mveID );
            assert( this.mveID != DBIndex.INVALID_ID );

            this.db.idx.replaceElement(this);

            mve = this.lookupMatrixVE(this.mveID);

            while ( i < this.getNumArgs() )
            {
                // get the i-th column predicate formal argument.  This is the
                // mve's actual argument,  so be careful not to modify it in
                // any way.
                fa = mve.getCPFormalArg(i);

                if ( fa == null )
                {
                    throw new SystemErrorException(mName + "no " + i +
                            "th formal argument?!?!");
                }

                // get the i'th arguments from the old and new argument
                // lists.  Again, these are the actual arguments -- must be
                // careful not to modify them in any way.
                newArg = this.argList.get(i);
                oldArg = oldColPred.argList.get(i);

                if ( newArg == null )
                {
                    throw new SystemErrorException(mName + "no new" + i +
                            "th argument?!?!");
                }

                if ( oldArg == null )
                {
                    throw new SystemErrorException(mName + "no old" + i +
                            "th argument?!?!");
                }

                if ( fa.getID() != newArg.getItsFargID() )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != newArg.getItsFargID()");
                }

                if ( fa.getID() != oldArg.getItsFargID() )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != oldArg.getItsFargID()");
                }

                if ( oldArg.getID() == DBIndex.INVALID_ID )
                {
                    throw new SystemErrorException(mName + i +
                            "th old argument not in index?!?!");
                }

                if ( ( newArg.getID() != DBIndex.INVALID_ID ) &&
                     ( newArg.getID() != oldArg.getID() ) )
                {
                    throw new SystemErrorException(mName + i +
                            "th argument id mismatch");
                }

                this.validateReplacementArg(fa, oldArg, newArg,
                        cascadeMveMod, cascadeMveDel, cascadeMveID,
                        cascadePveMod, cascadePveDel, cascadePveID);

                // todo: delete this eventually
//                switch (fa.getFargType())
//                {
//                    case COL_PREDICATE:
//                        cpfa = (ColPredFormalArg)fa;
//
//                        if ( ! ( ( oldArg instanceof ColPredDataValue ) ||
//                                 ( oldArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( oldArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)oldArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "Old arg " +
//                                    "type mismatch: column predicate DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ! ( ( newArg instanceof ColPredDataValue ) ||
//                                 ( newArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( newArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)newArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "New arg " +
//                                    "type mismatch: column predicate DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ( newArg.getClass() != oldArg.getClass() ) &&
//                             ( newArg.getID() != DBIndex.INVALID_ID ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "dv type change and id set");
//                        }
//
//                        if ( newArg instanceof ColPredDataValue )
//                        {
//                            new_cpdv = (ColPredDataValue)newArg;
//
//                            if ( oldArg instanceof ColPredDataValue )
//                            {
//                                old_cpdv = (ColPredDataValue)oldArg;
//
//                                if ( ( cascadeMveMod ) || ( cascadeMveDel ) ||
//                                     ( cascadePveMod ) || ( cascadePveDel ) )
//                                {
//                                    new_cpdv.getItsValue().
//                                            validateReplacementColPred(
//                                                old_cpdv.getItsValueBlind(),
//                                                cascadeMveMod,
//                                                cascadeMveDel,
//                                                cascadeMveID,
//                                                cascadePveMod,
//                                                cascadePveDel,
//                                                cascadePveID);
//                                }
//                                else
//                                {
//                                    new_cpdv.getItsValue().
//                                            validateReplacementColPred(
//                                                old_cpdv.getItsValue(),
//                                                cascadeMveMod,
//                                                cascadeMveDel,
//                                                cascadeMveID,
//                                                cascadePveMod,
//                                                cascadePveDel,
//                                                cascadePveID);
//                                }
//                            }
//                            else
//                            {
//                                new_cpdv.getItsValue().
//                                        validateColumnPredicate(true);
//                            }
//                        }
//                        break;
//
//                    case FLOAT:
//                        ffa = (FloatFormalArg)fa;
//                        if ( ! ( ( oldArg instanceof FloatDataValue ) ||
//                                 ( oldArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( oldArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)oldArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "Old arg " +
//                                    "type mismatch: float DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ! ( ( newArg instanceof FloatDataValue ) ||
//                                 ( newArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( newArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)newArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "New arg " +
//                                    "type mismatch: float DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ( newArg.getClass() != oldArg.getClass() ) &&
//                             ( newArg.getID() != DBIndex.INVALID_ID ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "dv type change and id set");
//                        }
//
//                        if ( newArg instanceof FloatDataValue )
//                        {
//                            new_fdv = (FloatDataValue)newArg;
//                            old_fdv = (FloatDataValue)oldArg;
//
//                            if ( new_fdv.getSubRange() != ffa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                                 "new_fdv.getSubRange() != ffa.getSubRange().");
//                            }
//
//                            if ( new_fdv.getSubRange() )
//                            {
//                                if ( ( ffa.getMinVal() >
//                                        new_fdv.getItsValue() ) ||
//                                     ( ffa.getMaxVal() <
//                                        new_fdv.getItsValue() ) )
//                                {
//                                    throw new SystemErrorException(mName +
//                                        "new_fdv.getItsValue() out of range.");
//                                }
//                            }
//                        }
//                        break;
//
//                    case INTEGER:
//                        ifa = (IntFormalArg)fa;
//
//                        if ( ! ( ( oldArg instanceof IntDataValue ) ||
//                                 ( oldArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( oldArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)oldArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "Old arg " +
//                                    "type mismatch: integer DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ! ( ( newArg instanceof IntDataValue ) ||
//                                 ( newArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( newArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)newArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "New arg " +
//                                    "type mismatch: integer DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ( newArg.getClass() != oldArg.getClass() ) &&
//                             ( newArg.getID() != DBIndex.INVALID_ID ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "dv type change and id set");
//                        }
//
//                        if ( newArg instanceof IntDataValue )
//                        {
//                            new_idv = (IntDataValue)newArg;
//                            old_idv = (IntDataValue)oldArg;
//
//                            if ( new_idv.getSubRange() != ifa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                                 "new_idv.getSubRange() != ifa.getSubRange().");
//                            }
//
//                            if ( new_idv.getSubRange() )
//                            {
//                                if ( ( ifa.getMinVal() >
//                                        new_idv.getItsValue() ) ||
//                                     ( ifa.getMaxVal() <
//                                        new_idv.getItsValue() ) )
//                                {
//                                    throw new SystemErrorException(mName +
//                                        "new_idv.getItsValue() out of range.");
//                                }
//                            }
//                        }
//                        break;
//
//                    case NOMINAL:
//                        nfa = (NominalFormalArg)fa;
//
//                        if ( ! ( ( oldArg instanceof NominalDataValue ) ||
//                                 ( oldArg instanceof UndefinedDataValue )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "Old arg " +
//                                    "type mismatch: nominal DV, or " +
//                                    "undefined DV expected.");
//                        }
//
//                        if ( ! ( ( newArg instanceof NominalDataValue ) ||
//                                 ( newArg instanceof UndefinedDataValue )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "New arg " +
//                                    "type mismatch: nominaal DV, or " +
//                                    "undefined DV expected.");
//                        }
//
//                        if ( ( newArg.getClass() != oldArg.getClass() ) &&
//                             ( newArg.getID() != DBIndex.INVALID_ID ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "dv type change and id set");
//                        }
//
//                        if ( newArg instanceof NominalDataValue )
//                        {
//                            new_ndv = (NominalDataValue)newArg;
//                            old_ndv = (NominalDataValue)oldArg;
//
//                            if ( new_ndv.getSubRange() != nfa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                                 "new_ndv.getSubRange() != nfa.getSubRange().");
//                            }
//
//                            if ( ( new_ndv.getSubRange() ) &&
//                                 ( new_ndv.getItsValue() != null ) )
//                            {
//                                if ( ! nfa.approved(new_ndv.getItsValue()) )
//                                {
//                                    throw new SystemErrorException(mName +
//                                        "new_ndv.getItsValue() out of range.");
//                                }
//                            }
//                        }
//                        break;
//
//                    case PREDICATE:
//                        pfa = (PredFormalArg)fa;
//
//                        if ( ! ( ( oldArg instanceof PredDataValue ) ||
//                                 ( oldArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( oldArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)oldArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "Old arg " +
//                                    "type mismatch: predicate DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ! ( ( newArg instanceof PredDataValue ) ||
//                                 ( newArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( newArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)newArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "New arg " +
//                                    "type mismatch: predicate DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ( newArg.getClass() != oldArg.getClass() ) &&
//                             ( newArg.getID() != DBIndex.INVALID_ID ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "dv type change and id set");
//                        }
//
//                        if ( newArg instanceof PredDataValue )
//                        {
//                            new_pdv = (PredDataValue)newArg;
//                            old_pdv = (PredDataValue)oldArg;
//
//                            if ( new_pdv.getSubRange() != pfa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                                 "new_pdv.getSubRange() != pfa.getSubRange().");
//                            }
//
//                            if ( ( new_pdv.getItsValue().getPveID() !=
//                                    DBIndex.INVALID_ID ) &&
//                                 ( new_pdv.getSubRange() ) &&
//                                 ( ! pfa.approved(new_pdv.getItsValue().
//                                            getPveID()) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "new_pdv.getItsValue() out of range.");
//                            }
//
//                            if ( ( new_pdv.getID() == DBIndex.INVALID_ID ) ||
//                                 ( new_pdv.getItsValue().getID() ==
//                                    DBIndex.INVALID_ID ) )
//                            {
//                                new_pdv.getItsValue().validatePredicate(true);
//                            }
//                            else if ( ( ! cascadeMveMod ) &&
//                                      ( ! cascadeMveDel ) &&
//                                      ( ! cascadePveMod ) &&
//                                      ( ! cascadePveDel ) )
//                            {
//                                new_pdv.getItsValue().
//                                        validateReplacementPredicate(
//                                            old_pdv.getItsValue(),
//                                            cascadeMveMod,
//                                            cascadeMveDel,
//                                            cascadeMveID,
//                                            cascadePveMod,
//                                            cascadePveDel,
//                                            cascadePveID);
//                            }
//                            else
//                            {
//                                new_pdv.getItsValue().
//                                        validateReplacementPredicate(
//                                            old_pdv.getItsValueBlind(),
//                                            cascadeMveMod,
//                                            cascadeMveDel,
//                                            cascadeMveID,
//                                            cascadePveMod,
//                                            cascadePveDel,
//                                            cascadePveID);
//                            }
//                        }
//                        break;
//
//                    case TIME_STAMP:
//                        tsfa = (TimeStampFormalArg)fa;
//
//                        if ( ! ( ( oldArg instanceof TimeStampDataValue ) ||
//                                 ( oldArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( oldArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)oldArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "Old arg " +
//                                    "type mismatch: time stamp DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ! ( ( newArg instanceof TimeStampDataValue ) ||
//                                 ( newArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( newArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)newArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "New arg " +
//                                    "type mismatch: time stamp DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ( newArg.getClass() != oldArg.getClass() ) &&
//                             ( newArg.getID() != DBIndex.INVALID_ID ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "dv type change and id set");
//                        }
//
//                        if ( newArg instanceof TimeStampDataValue )
//                        {
//                            new_tsdv = (TimeStampDataValue)newArg;
//                            old_tsdv = (TimeStampDataValue)oldArg;
//
//                            if ( new_tsdv.getSubRange() != tsfa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                               "new_tsdv.getSubRange() != tsfa.getSubRange().");
//                            }
//
//                            if ( new_tsdv.getSubRange() )
//                            {
//                                if ( ( tsfa.getMinVal().
//                                        gt(new_tsdv.getItsValue()) ) ||
//                                     ( tsfa.getMaxVal().
//                                        lt(new_tsdv.getItsValue()) ) )
//                                {
//                                    throw new SystemErrorException(mName +
//                                            "new_tsdv.getItsValue() out of range.");
//                                }
//                            }
//                        }
//                        break;
//
//                    case QUOTE_STRING:
//                        if ( ! ( ( oldArg instanceof QuoteStringDataValue ) ||
//                                 ( oldArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( oldArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)oldArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "Old arg " +
//                                    "type mismatch: quote string DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ! ( ( newArg instanceof QuoteStringDataValue ) ||
//                                 ( newArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( newArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)newArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "New arg " +
//                                    "type mismatch: quote string DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ( newArg.getClass() != oldArg.getClass() ) &&
//                             ( newArg.getID() != DBIndex.INVALID_ID ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "dv type change and id set");
//                        }
//                        break;
//
//                    case TEXT:
//                        if ( ! ( ( oldArg instanceof TextStringDataValue ) ||
//                                 ( oldArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( oldArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)oldArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "Old arg " +
//                                    "type mismatch: text string DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ! ( ( newArg instanceof TextStringDataValue ) ||
//                                 ( newArg instanceof UndefinedDataValue ) ||
//                                 ( ( this.queryVarOK ) ||
//                                   ( newArg instanceof NominalDataValue ) &&
//                                   ( ((NominalDataValue)newArg).isQueryVar() )
//                                 )
//                               )
//                           )
//                        {
//                            throw new SystemErrorException(mName + "New arg " +
//                                    "type mismatch: text string DV, " +
//                                    "undefined DV, or query var expected.");
//                        }
//
//                        if ( ( newArg.getClass() != oldArg.getClass() ) &&
//                             ( newArg.getID() != DBIndex.INVALID_ID ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "dv type change and id set");
//                        }
//                        break;
//
//                    case UNTYPED:
//                        if ( ( newArg instanceof TextStringDataValue ) ||
//                             ( oldArg instanceof TextStringDataValue ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "Type mismatch: Text String can't be " +
//                                    "substituted for untyped arguments.");
//                        }
//                        else if ( ! ( ( newArg instanceof ColPredDataValue ) ||
//                                      ( newArg instanceof FloatDataValue ) ||
//                                      ( newArg instanceof IntDataValue ) ||
//                                      ( newArg instanceof NominalDataValue ) ||
//                                      ( newArg instanceof PredDataValue ) ||
//                                      ( newArg instanceof TimeStampDataValue ) ||
//                                      ( newArg instanceof QuoteStringDataValue ) ||
//                                      ( newArg instanceof UndefinedDataValue ) ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "Unknown subtype of DataValue");
//                        }
//
//                        if ( ( newArg.getClass() != oldArg.getClass() ) &&
//                             ( newArg.getID() != DBIndex.INVALID_ID ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "dv type change and id set");
//                        }
//
//                        if ( newArg instanceof ColPredDataValue )
//                        {
//                            new_cpdv = (ColPredDataValue)newArg;
//
//                            if ( oldArg instanceof ColPredDataValue )
//                            {
//                                old_cpdv = (ColPredDataValue)oldArg;
//
//                                if ( ( cascadeMveMod ) || ( cascadeMveDel ) ||
//                                     ( cascadePveMod ) || ( cascadePveDel ) )
//                                {
//                                    new_cpdv.getItsValue().
//                                            validateReplacementColPred(
//                                                old_cpdv.getItsValueBlind(),
//                                                cascadeMveMod,
//                                                cascadeMveDel,
//                                                cascadeMveID,
//                                                cascadePveMod,
//                                                cascadePveDel,
//                                                cascadePveID);
//                                }
//                                else
//                                {
//                                    new_cpdv.getItsValue().
//                                            validateReplacementColPred(
//                                                old_cpdv.getItsValue(),
//                                                cascadeMveMod,
//                                                cascadeMveDel,
//                                                cascadeMveID,
//                                                cascadePveMod,
//                                                cascadePveDel,
//                                                cascadePveID);
//                                }
//                            }
//                            else
//                            {
//                                new_cpdv.getItsValue().
//                                        validateColumnPredicate(true);
//                            }
//                        }
//                        else if ( newArg instanceof PredDataValue )
//                        {
//                            new_pdv = (PredDataValue)newArg;
//
//                            if ( oldArg instanceof PredDataValue )
//                            {
//                                old_pdv = (PredDataValue)oldArg;
//
//                                if ( ( cascadeMveMod ) || ( cascadeMveDel ) ||
//                                     ( cascadePveMod ) || ( cascadePveDel ) )
//                                {
//                                    new_pdv.getItsValue().
//                                            validateReplacementPredicate(
//                                                old_pdv.getItsValueBlind(),
//                                                cascadeMveMod,
//                                                cascadeMveDel,
//                                                cascadeMveID,
//                                                cascadeMveMod,
//                                                cascadePveDel,
//                                                cascadePveID);
//                                }
//                                else
//                                {
//                                    new_pdv.getItsValue().
//                                            validateReplacementPredicate(
//                                                old_pdv.getItsValue(),
//                                                cascadeMveMod,
//                                                cascadeMveDel,
//                                                cascadeMveID,
//                                                cascadeMveMod,
//                                                cascadePveDel,
//                                                cascadePveID);
//                                }
//                            }
//                            else
//                            {
//                                new_pdv.getItsValue().
//                                        validatePredicate(true);
//                            }
//                        }
//                        break;
//
//                    case UNDEFINED:
//                        throw new SystemErrorException(mName +
//                                "formal arg type undefined???");
//                        /* break statement commented out to keep the
//                         * compiler happy
//                         */
//                        // break;
//
//                    default:
//                        throw new SystemErrorException(mName +
//
//                                "Unknown Formal Arg Type");
//                        /* break statement commented out to keep the
//                         * compiler happy
//                         */
//                        // break;
//                }

                // Sanity checks pass.  If the ID's of the old and new versions of
                // the argument match, replace the old incarnation of the formal
                // argument with the new in the index.
                //
                // Otherwise, remove the old from the index, and insert the new.
                if ( newArg.getID() == oldArg.getID() )
                {
                    newArg.replaceInIndex(oldArg, DCID, cascadeMveMod,
                                          cascadeMveDel, cascadeMveID,
                                          cascadePveMod, cascadePveDel,
                                          cascadePveID);
                }
                else /* new_fdv.getID() == DBIndex.INVALID_ID */
                {
                    oldArg.removeFromIndex(DCID);
                    newArg.insertInIndex(DCID);
                }

                i++;

            } /* while */
        }
        else // From previous if statements, we have that:
             // ( cascadeMveMod ) &&
             // ( cascadeMveID == this.pveID ) &&
             // ( this.id == oldColPred.id ) &&
             // ( this.mveID == oldColPred.mveID ) &&
             // ( this.mveID != DBIndex.INVALID_ID )
       {
            assert( cascadeMveMod );
            assert( cascadeMveID == this.mveID );
            assert( this.id == oldColPred.id );
            assert( this.mveID == oldColPred.mveID );
            assert( this.mveID != DBIndex.INVALID_ID );

            // the mve whose definition underlies the old and new incarnations
            // of the column predicate has changed -- thus it is possible that
            // formal arguments have shifted location, been removed, or added.
            // We must update the index accordingly.
            //
            // Fortunately, we can count on the following:
            //
            // 1) If the formal argument associated with an argument has been
            //    removed, then the new version of the column predicate will
            //    contain no argument with the same ID as that associated with
            //    the formal argument that has been removed.
            //
            // 2) If a formal argument has been added, then the argument
            //    associated with the formal argument in the new predicate
            //    will have the invalid id.
            //
            // With these two assurances in hand, we can process the two
            // argument lists as follows:
            //
            // First, scan the old list for IDs that don't exist in the new
            // list.  Delete the associated entries from the index.
            //
            // Second scan the new list.  If an entry has invalid ID, just
            // insert it in the index.  If it has valid id, use it to replace
            // the entry in the old list with the same ID.  If no such old
            // argument exists, scream and die.

            this.db.idx.replaceElement(this);

            mve = this.lookupMatrixVE(this.mveID);

            // first remove unmatched old arguments from the index
            i = 0;
            while ( i < oldColPred.getNumArgs() )
            {
                int j = 0;
                boolean foundMatch = false;

                oldArg = oldColPred.argList.get(i);

                while ( j < this.getNumArgs() )
                {
                    newArg = this.argList.get(j);

                    if ( newArg.getID() == oldArg.getID() )
                    {
                        if ( foundMatch )
                        {
                            throw new SystemErrorException(mName +
                                                   "found duplicate match?!?");
                        }
                        else
                        {
                            foundMatch = true;
                        }
                    }
                    j++;
                }

                if ( ! foundMatch )
                {
                    oldArg.removeFromIndex(DCID);
                }

                i++;
            }

            // now scan the new argument list.  Any arguments with
            // valid IDs must have a previous version with the same ID
            // in the old argument list -- verify this.
            i = 0;
            while ( i < this.getNumArgs() )
            {
                // get the i-th formal argument.  This is the mve's actual
                // column predicate argument,  so be careful not to modify
                // it in any way.
                fa = mve.getCPFormalArg(i);

                if ( fa == null )
                {
                    throw new SystemErrorException(mName + "no " + i +
                            "th formal argument?!?!");
                }

                // get the i'th argument from the new argument list, and
                // the matching argument (if any) from the old argument list.
                // Again, these are the actual arguments -- must be
                // careful not to modify them in any way.
                newArg = this.argList.get(i);
                oldArg = null;

                if ( newArg == null )
                {
                    throw new SystemErrorException(mName + "no new" + i +
                            "th argument?!?!");
                }

                if ( newArg.getID() != DBIndex.INVALID_ID )
                {
                    // the old argument list must contain an argument
                    // with the same ID.  Scan the list to find it.
                    int j = 0;

                    while ( ( j < oldColPred.getNumArgs() ) &&
                            ( oldArg == null ) )
                    {
                        oldArg = oldColPred.argList.get(j);

                        if ( oldArg.getID() == DBIndex.INVALID_ID )
                        {
                            throw new SystemErrorException(mName + i +
                                    "th old argument not in index?!?!");
                        }

                        if ( oldArg.getID() != newArg.getID() )
                        {
                            oldArg = null;
                        }

                        j++;
                    }

                    if ( oldArg == null )
                    {
                        throw new SystemErrorException(mName +
                            "new arg has valid ID but no matching old arg.");
                    }
                }

                if ( ( oldArg != null ) &&
                     ( fa.getID() != oldArg.getItsFargID() ) )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != oldArg.getItsFargID()");
                }

                if ( fa.getID() != newArg.getItsFargID() )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != newArg.getItsFargID()");
                }

                if ( ( oldArg != null ) &&
                     ( fa.getID() != oldArg.getItsFargID() ) )
                {
                    throw new SystemErrorException(mName +
                                    "fa.getID() != oldArg.getItsFargID()");
                }

                this.validateReplacementArg(fa, oldArg, newArg,
                        cascadeMveMod, cascadeMveDel, cascadeMveID,
                        cascadePveMod, cascadePveDel, cascadePveID);

                // todo: delete this eventually
//                switch (fa.getFargType())
//                {
//                    case COL_PREDICATE:
//                        if ( ( ! ( newArg instanceof ColPredDataValue ) ) ||
//                             ( ( oldArg != null ) &&
//                               ( ! ( oldArg instanceof ColPredDataValue ) ) ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                "Type mismatch: Column Predicate(s) expected.");
//                        }
//
//                        cpfa = (ColPredFormalArg)fa;
//                        new_cpdv = (ColPredDataValue)newArg;
//
//                        if ( oldArg != null )
//                        {
//                            old_cpdv = (ColPredDataValue)oldArg;
//                        }
//                        else
//                        {
//                            old_cpdv = null;
//                        }
//
//                        if ( ( new_cpdv.getID() == DBIndex.INVALID_ID ) ||
//                             ( new_cpdv.getItsValue().getID() ==
//                                DBIndex.INVALID_ID ) )
//                        {
//                            new_cpdv.getItsValue().validateColumnPredicate(true);
//                        }
//                        else if ( old_cpdv != null )
//                        {
//                            new_cpdv.getItsValue().
//                                    validateReplacementColPred(
//                                        old_cpdv.getItsValueBlind(),
//                                        cascadeMveMod,
//                                        cascadeMveDel,
//                                        cascadeMveID,
//                                        cascadePveMod,
//                                        cascadePveDel,
//                                        cascadePveID);
//                        }
//                        else
//                        {
//                            throw new SystemErrorException(mName +
//                            "new_cpdv has valid ID but old_cpdv is null?!?");
//                        }
//                        break;
//
//                    case FLOAT:
//                        if ( ( ! ( newArg instanceof FloatDataValue ) ) ||
//                             ( ( oldArg != null ) &&
//                               ( ! ( oldArg instanceof FloatDataValue ) ) ) )
//                        {
//                           throw new SystemErrorException(mName +
//                                    "Type mismatch: float(s) expected.");
//                        }
//
//                        ffa = (FloatFormalArg)fa;
//                        new_fdv = (FloatDataValue)newArg;
//
//                        if ( new_fdv.getSubRange() != ffa.getSubRange() )
//                        {
//                           throw new SystemErrorException(mName +
//                             "new_fdv.getSubRange() != ffa.getSubRange().");
//                        }
//
//                        if ( new_fdv.getSubRange() )
//                        {
//                            if ( ( ffa.getMinVal() >
//                                    new_fdv.getItsValue() ) ||
//                                 ( ffa.getMaxVal() <
//                                    new_fdv.getItsValue() ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                    "new_fdv.getItsValue() out of range.");
//                            }
//                        }
//                        break;
//
//                    case INTEGER:
//                        if ( ( ! ( newArg instanceof IntDataValue ) ) ||
//                             ( ( oldArg != null ) &&
//                               ( ! ( oldArg instanceof IntDataValue ) ) ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "Type mismatch: integer(s) expected.");
//                        }
//
//                        ifa = (IntFormalArg)fa;
//                        new_idv = (IntDataValue)newArg;
//
//                        if ( new_idv.getSubRange() != ifa.getSubRange() )
//                        {
//                           throw new SystemErrorException(mName +
//                             "new_idv.getSubRange() != ifa.getSubRange().");
//                        }
//
//                        if ( new_idv.getSubRange() )
//                        {
//                            if ( ( ifa.getMinVal() >
//                                    new_idv.getItsValue() ) ||
//                                 ( ifa.getMaxVal() <
//                                    new_idv.getItsValue() ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                    "new_idv.getItsValue() out of range.");
//                            }
//                        }
//                        break;
//
//                    case NOMINAL:
//                        if ( ( ! ( newArg instanceof NominalDataValue ) ) ||
//                             ( ( oldArg != null ) &&
//                               ( ! ( oldArg instanceof NominalDataValue ) )
//                             )
//                           )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "Type mismatch: nominal(s) expected.");
//                        }
//
//                        nfa = (NominalFormalArg)fa;
//                        new_ndv = (NominalDataValue)newArg;
//
//                        if ( new_ndv.getSubRange() != nfa.getSubRange() )
//                        {
//                           throw new SystemErrorException(mName +
//                             "new_ndv.getSubRange() != nfa.getSubRange().");
//                        }
//
//                        if ( ( new_ndv.getSubRange() ) &&
//                             ( new_ndv.getItsValue() != null ) )
//                        {
//                            if ( ! nfa.approved(new_ndv.getItsValue()) )
//                            {
//                                throw new SystemErrorException(mName +
//                                    "new_ndv.getItsValue() out of range.");
//                            }
//                        }
//                        break;
//
//                    case PREDICATE:
//                        if ( ( ! ( newArg instanceof PredDataValue ) ) ||
//                             ( ( oldArg != null ) &&
//                               ( ! ( oldArg instanceof PredDataValue ) ) ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "Type mismatch: Predicate(s) expected.");
//                        }
//
//                        pfa = (PredFormalArg)fa;
//                        new_pdv = (PredDataValue)newArg;
//
//                        if ( oldArg != null )
//                        {
//                            old_pdv = (PredDataValue)oldArg;
//                        }
//                        else
//                        {
//                            old_pdv = null;
//                        }
//
//                        if ( new_pdv.getSubRange() != pfa.getSubRange() )
//                        {
//                           throw new SystemErrorException(mName +
//                             "new_pdv.getSubRange() != pfa.getSubRange().");
//                        }
//
//                        if ( ( new_pdv.getItsValue().getPveID() !=
//                                DBIndex.INVALID_ID ) &&
//                             ( new_pdv.getSubRange() ) &&
//                             ( ! pfa.approved(new_pdv.getItsValue().
//                                        getPveID()) ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "new_pdv.getItsValue() out of range.");
//                        }
//
//                        if ( ( new_pdv.getID() == DBIndex.INVALID_ID ) ||
//                             ( new_pdv.getItsValue().getID() ==
//                                DBIndex.INVALID_ID ) )
//                        {
//                            new_pdv.getItsValue().validatePredicate(true);
//                        }
//                        else if ( old_pdv != null )
//                        {
//                            new_pdv.getItsValue().
//                                    validateReplacementPredicate(
//                                        old_pdv.getItsValueBlind(),
//                                        cascadeMveMod,
//                                        cascadeMveDel,
//                                        cascadeMveID,
//                                        cascadePveMod,
//                                        cascadePveDel,
//                                        cascadePveID);
//                        }
//                        else
//                        {
//                            throw new SystemErrorException(mName +
//                            "new_pdv has valid ID but old_pdv is null?!?");
//                        }
//                        break;
//
//                    case TIME_STAMP:
//                        if ( ( ! ( newArg instanceof
//                                    TimeStampDataValue ) ) ||
//                             ( ( oldArg != null ) &&
//                               ( ! ( oldArg instanceof
//                                      TimeStampDataValue ) ) ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                "Type mismatch: time stamp(s) expected.");
//                        }
//
//                        tsfa = (TimeStampFormalArg)fa;
//                        new_tsdv = (TimeStampDataValue)newArg;
//
//                        if ( new_tsdv.getSubRange() != tsfa.getSubRange() )
//                        {
//                           throw new SystemErrorException(mName +
//                           "new_tsdv.getSubRange() != tsfa.getSubRange().");
//                        }
//
//                        if ( new_tsdv.getSubRange() )
//                        {
//                            if ( ( tsfa.getMinVal().
//                                    gt(new_tsdv.getItsValue()) ) ||
//                                 ( tsfa.getMaxVal().
//                                    lt(new_tsdv.getItsValue()) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "new_tsdv.getItsValue() out of range.");
//                            }
//                        }
//                        break;
//
//                    case QUOTE_STRING:
//                        if ( ( ! ( newArg instanceof
//                                    QuoteStringDataValue ) ) ||
//                             ( ( oldArg != null ) &&
//                               ( ! ( oldArg instanceof
//                                      QuoteStringDataValue ) ) ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "Type mismatch: quote string(s) expected.");
//                        }
//                        break;
//
//                    case TEXT:
//                        if ( ( ! ( newArg instanceof
//                                    TextStringDataValue ) ) ||
//                             ( ( oldArg != null ) &&
//                               ( ! ( oldArg instanceof
//                                      TextStringDataValue ) ) ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "Type mismatch: text string(s) expected.");
//                        }
//                        break;
//
//                    case UNTYPED:
//                        if ( ( newArg instanceof TextStringDataValue ) ||
//                             ( ( oldArg != null ) &&
//                               ( oldArg instanceof TextStringDataValue ) ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                "Type mismatch: Text String(s) can't be " +
//                                "substituted for untyped arguments.");
//                        }
//                        else if ( ! ( ( newArg instanceof
//                                        ColPredDataValue ) ||
//                                      ( newArg instanceof
//                                        FloatDataValue ) ||
//                                      ( newArg instanceof
//                                        IntDataValue ) ||
//                                      ( newArg instanceof
//                                        NominalDataValue ) ||
//                                      ( newArg instanceof
//                                        PredDataValue ) ||
//                                      ( newArg instanceof
//                                        TimeStampDataValue ) ||
//                                      ( newArg instanceof
//                                        QuoteStringDataValue ) ||
//                                      ( newArg instanceof
//                                        UndefinedDataValue ) ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "Unknown subtype of DataValue");
//                        }
//
//                        if ( ( ( oldArg == null )
//                               ||
//                               ( newArg.getClass() != oldArg.getClass() )
//                             )
//                             &&
//                             ( newArg.getID() != DBIndex.INVALID_ID ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "dv type change and id set(2)");
//                        }
//
//                        if ( newArg instanceof ColPredDataValue )
//                        {
//                            new_cpdv = (ColPredDataValue)newArg;
//
//                            if ( ( oldArg != null ) &&
//                                 ( oldArg instanceof ColPredDataValue ) )
//                            {
//                                old_cpdv = (ColPredDataValue)oldArg;
//
//                                assert( cascadeMveMod );
//
//                                new_cpdv.getItsValue().
//                                        validateReplacementColPred(
//                                            old_cpdv.getItsValueBlind(),
//                                            cascadeMveMod,
//                                            cascadeMveDel,
//                                            cascadeMveID,
//                                            cascadePveMod,
//                                            cascadePveDel,
//                                            cascadePveID);
//                            }
//                            else
//                            {
//                                new_cpdv.getItsValue().
//                                        validateColumnPredicate(true);
//                            }
//                        }
//                        else if ( newArg instanceof PredDataValue )
//                        {
//                            new_pdv = (PredDataValue)newArg;
//
//                            if ( ( oldArg != null ) &&
//                                 ( oldArg instanceof PredDataValue ) )
//                            {
//                                old_pdv = (PredDataValue)oldArg;
//
//                                assert( cascadeMveMod );
//
//                                new_pdv.getItsValue().
//                                        validateReplacementPredicate(
//                                            old_pdv.getItsValueBlind(),
//                                            cascadeMveMod,
//                                            cascadeMveDel,
//                                            cascadeMveID,
//                                            cascadePveMod,
//                                            cascadePveDel,
//                                            cascadePveID);
//                            }
//                            else
//                            {
//                                new_pdv.getItsValue().
//                                        validatePredicate(true);
//                            }
//                        }
//                        break;
//
//                    case UNDEFINED:
//                        throw new SystemErrorException(mName +
//                                "formal arg type undefined???");
//                        /* break statement commented out to keep the
//                         * compiler happy
//                         */
//                        // break;
//
//                    default:
//                        throw new SystemErrorException(mName +
//
//                                "Unknown Formal Arg Type");
//                        /* break statement commented out to keep the
//                         * compiler happy
//                         */
//                        // break;
//                }

                // Sanity checks pass.  If oldArg is defined, the IDs must
                // match and we replace the old version with the new in the
                // index.  Otherwise, just insert the new argument in the
                // index.
                if ( oldArg != null )
                {
                    assert( newArg.getID() == oldArg.getID() );

                    newArg.replaceInIndex(oldArg, DCID, cascadeMveMod,
                                          cascadeMveDel, cascadeMveID,
                                          cascadePveMod, cascadePveDel,
                                          cascadePveID);
                }
                else
                {
                    assert( newArg.getID() == DBIndex.INVALID_ID );
                    assert( this.id != DBIndex.INVALID_ID );

                    newArg.setItsPredID(this.id);
                    newArg.insertInIndex(DCID);
                }

                i++;

            } /* while */
        }

        return;

    } /* ColPred::updateIndexForReplacement() */


    /**
     * validateArgAsignment()
     *
     * Verify that the supplied data value is an acceptable value to assign
     * to the supplied formal argument.  Throw a system error if it is not.
     * This method is a pure sanity checking method -- it should always pass.
     *
     *                                              JRM -- 10/28/08
     *
     * Changes:
     *
     *    - None.
     */

    private void validateArgAsignment(FormalArgument fa,
                                      DataValue arg)
        throws SystemErrorException
    {
        final String mName = "ColPred::validateArgAsignment(): ";
        ColPredFormalArg        cpfa;
        FloatFormalArg          ffa;
        IntFormalArg            ifa;
        NominalFormalArg        nfa;
        PredFormalArg           pfa;
        TimeStampFormalArg      tsfa;
        QuoteStringFormalArg    qsfa;
        TextStringFormalArg     tfa;
        UnTypedFormalArg        ufa;
        ColPredDataValue        cpdv;
        FloatDataValue          fdv;
        IntDataValue            idv;
        NominalDataValue        ndv;
        PredDataValue           pdv;
        TimeStampDataValue      tsdv;

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry.");
        }
        else if ( fa.getItsVocabElementID() != this.mveID )
        {
            throw new SystemErrorException(mName + "fa has unexpected veID.");
        }

        if ( arg == null )
        {
            throw new SystemErrorException(mName + "arg null on entry.");
        }
        else if ( ( arg.getItsFargID() != DBIndex.INVALID_ID) &&
                  ( arg.getItsFargID() != fa.getID() ) )
        {
            throw new SystemErrorException(mName +
                    "arg.getItsFargID() defined and != fa.getID()");
        }


        switch (fa.getFargType())
        {
            case COL_PREDICATE:
                cpfa = (ColPredFormalArg)fa;

                if ( ! ( ( arg instanceof ColPredDataValue ) ||
                         ( arg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( arg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)arg).isQueryVar() )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "Arg " +
                                "type mismatch: column predicate DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "Arg " +
                                "type mismatch: column predicate DV, " +
                                "or undefined DV expected.");
                    }
                }

                if ( arg instanceof ColPredDataValue )
                {
                    cpdv = (ColPredDataValue)arg;

                    cpdv.getItsValue().validateColumnPredicate(false);
                }
                break;

            case FLOAT:
                ffa = (FloatFormalArg)fa;

                if ( ! ( ( arg instanceof FloatDataValue ) ||
                         ( arg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( arg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)arg).isQueryVar() )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "Arg " +
                                "type mismatch: float DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "Arg " +
                                "type mismatch: float DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( arg instanceof FloatDataValue )
                {
                    fdv = (FloatDataValue)arg;

                    if ( fdv.getSubRange() != ffa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                         "fdv.getSubRange() != ffa.getSubRange().");
                    }

                    if ( fdv.getSubRange() )
                    {
                        if ( ( ffa.getMinVal() > fdv.getItsValue() ) ||
                             ( ffa.getMaxVal() < fdv.getItsValue() ) )
                        {
                            throw new SystemErrorException(mName +
                                "fdv.getItsValue() out of range.");
                        }
                    }
                }
                break;

            case INTEGER:
                ifa = (IntFormalArg)fa;

                if ( ! ( ( arg instanceof IntDataValue ) ||
                         ( arg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( arg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)arg).isQueryVar() )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "Arg " +
                                "type mismatch: integer DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "Arg " +
                                "type mismatch: integer DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( arg instanceof IntDataValue )
                {
                    idv = (IntDataValue)arg;

                    if ( idv.getSubRange() != ifa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                         "idv.getSubRange() != ifa.getSubRange().");
                    }

                    if ( idv.getSubRange() )
                    {
                        if ( ( ifa.getMinVal() > idv.getItsValue() ) ||
                             ( ifa.getMaxVal() < idv.getItsValue() ) )
                        {
                            throw new SystemErrorException(mName +
                                "idv.getItsValue() out of range.");
                        }
                    }
                }
                break;

            case NOMINAL:
                nfa = (NominalFormalArg)fa;

                if ( ! ( ( arg instanceof NominalDataValue ) ||
                         ( arg instanceof UndefinedDataValue )
                       )
                   )
                {
                    throw new SystemErrorException(mName + "Arg " +
                            "type mismatch: nominaal DV, or " +
                            "undefined DV expected.");
                }

                if ( arg instanceof NominalDataValue )
                {
                    ndv = (NominalDataValue)arg;

                    if ( ndv.getSubRange() != nfa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                         "ndv.getSubRange() != nfa.getSubRange().");
                    }

                    if ( ( ndv.getSubRange() ) &&
                         ( ndv.getItsValue() != null ) )
                    {
                        if ( ( ! nfa.approved(ndv.getItsValue()) ) &&
                             ( ( ! this.queryVarOK ) ||
                               ( ! ndv.isQueryVar() ) ) )
                        {
                            throw new SystemErrorException(mName +
                                "ndv.getItsValue() out of range.");
                        }
                    }
                }
                break;

            case PREDICATE:
                pfa = (PredFormalArg)fa;

                if ( ! ( ( arg instanceof PredDataValue ) ||
                         ( arg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( arg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)arg).isQueryVar() )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "Arg " +
                                "type mismatch: predicate DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "Arg " +
                                "type mismatch: predicate DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( arg instanceof PredDataValue )
                {
                    pdv = (PredDataValue)arg;

                    if ( pdv.getSubRange() != pfa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                         "pdv.getSubRange() != pfa.getSubRange().");
                    }

                    if ( ( pdv.getItsValue().getPveID() != DBIndex.INVALID_ID ) &&
                         ( pdv.getSubRange() ) &&
                         ( ! pfa.approved(pdv.getItsValue().getPveID()) ) )
                    {
                        throw new SystemErrorException(mName +
                                "new_pdv.getItsValue() out of range.");
                    }

                    pdv.getItsValue().validatePredicate(false);
                }
                break;

            case TIME_STAMP:
                tsfa = (TimeStampFormalArg)fa;

                if ( ! ( ( arg instanceof TimeStampDataValue ) ||
                         ( arg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( arg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)arg).isQueryVar() )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "arg " +
                                "type mismatch: time stamp DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "arg " +
                                "type mismatch: time stamp DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( arg instanceof TimeStampDataValue )
                {
                    tsdv = (TimeStampDataValue)arg;

                    if ( tsdv.getSubRange() != tsfa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                       "tsdv.getSubRange() != tsfa.getSubRange().");
                    }

                    if ( tsdv.getSubRange() )
                    {
                        if ( ( tsfa.getMinVal().gt(tsdv.getItsValue()) ) ||
                             ( tsfa.getMaxVal().lt(tsdv.getItsValue()) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "tsdv.getItsValue() out of range.");
                        }
                    }
                }
                break;

            case QUOTE_STRING:
                if ( ! ( ( arg instanceof QuoteStringDataValue ) ||
                         ( arg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( arg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)arg).isQueryVar() )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "Arg " +
                                "type mismatch: quote string DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "Arg " +
                                "type mismatch: quote string DV, or " +
                                "undefined DV expected.");
                    }
                }
                break;

            case TEXT:
                if ( ! ( ( arg instanceof TextStringDataValue ) ||
                         ( arg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( arg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)arg).isQueryVar() )
                         )
                       )
                   )
                {
                    throw new SystemErrorException(mName + "Arg " +
                            "type mismatch: text string DV, " +
                            "undefined DV, or query var expected.");
                }
                break;

            case UNTYPED:
                if ( arg instanceof TextStringDataValue )
                {
                    throw new SystemErrorException(mName +
                            "Type mismatch: Text Strings can't be " +
                            "substituted for untyped arguments.");
                }
                else if ( ! ( ( arg instanceof ColPredDataValue ) ||
                              ( arg instanceof FloatDataValue ) ||
                              ( arg instanceof IntDataValue ) ||
                              ( arg instanceof NominalDataValue ) ||
                              ( arg instanceof PredDataValue ) ||
                              ( arg instanceof TimeStampDataValue ) ||
                              ( arg instanceof QuoteStringDataValue ) ||
                              ( arg instanceof UndefinedDataValue ) ) )
                {
                    throw new SystemErrorException(mName +
                            "Unknown subtype of DataValue");
                }

                if ( arg instanceof ColPredDataValue )
                {
                    cpdv = (ColPredDataValue)arg;

                    cpdv.getItsValue().validateColumnPredicate(false);
                }
                else if ( arg instanceof PredDataValue )
                {
                    pdv = (PredDataValue)arg;

                    pdv.getItsValue().validatePredicate(false);
                }
                break;

            case UNDEFINED:
                throw new SystemErrorException(mName +
                        "formal arg type undefined???");
                /* break statement commented out to keep the
                 * compiler happy
                 */
                // break;

            default:
                throw new SystemErrorException(mName +

                        "Unknown Formal Arg Type");
                /* break statement commented out to keep the
                 * compiler happy
                 */
                // break;
        }

        return;

    } /* ColPred::validateArgAsignment() */


    /**
     * validateColumnPredicate()
     *
     * Verify that the column predicate is consistant with the target
     * MatrixVocabElement (if any).  This is purely
     * a sanity checking routine.  The test should always pass.
     *
     * The idMustBeInvalid parameter is used to inforce the requirement that
     * if any DataValue or Predicate has not been inserted in the index, then
     * none of its descendant may have been inserted in the index either.
     *
     *                                              JRM -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    public void validateColumnPredicate(boolean idMustBeInvalid)
        throws SystemErrorException
    {
        final String mName = "ColPred::validateColumnPredicate(): ";
        int i = 0;
        MatrixVocabElement mve;
        FormalArgument fa;
        DataValue arg = null;

        if ( idMustBeInvalid )
        {
            if ( this.id != DBIndex.INVALID_ID )
            {
                int j = 1/0;
                throw new SystemErrorException(mName +
                        "id set when invalid ID required.");
            }
        }
        else if ( this.id == DBIndex.INVALID_ID )
        {
            idMustBeInvalid = true;
        }

        if ( this.mveID == DBIndex.INVALID_ID )
        {
            // undefined column predicate -- verify that it is
            // correctly initialized
            if ( ( this.mveName == null ) ||
                 ( this.mveName.compareTo("") != 0 ) ||
                 ( this.argList != null ) ||
                 ( this.varLen != false ) )
            {
                throw new SystemErrorException(mName +
                        "undefined col pred with incorrect values");
            }
        }
        else
        {
            if ( argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!?!");
            }
            else if ( this.getNumArgs() <= 0 )
            {
                throw new SystemErrorException(mName + "this.getNumArgs() <= 0");
            }

            mve = this.lookupMatrixVE(this.mveID);

            if ( mve.getDB() != this.getDB() )
            {
                throw new SystemErrorException(mName +
                                               "mve.getDB() != this.getDB()");
            }

            if ( mve.getNumCPFormalArgs() != this.getNumArgs() )
            {
                throw new SystemErrorException(mName +
                        "mve.getNumCPFormalArgs() != this.getNumArgs()");
            }

            if ( mve.getVarLen() != this.getVarLen() )
            {
                throw new SystemErrorException(mName +
                                     "mve.getVarLen() != this.getValLen()");
            }


            // Now scan the argument list
            this.validateColPredArgList(mve, idMustBeInvalid);
        }

        return;

    } /* ColPred::validateColumnPredicate() */


    /**
     * validateColPredArgList()
     *
     * Verify that the arguments of the column predicate are of type and value
     * consistant with the target MatrixVocabElement.  This is purely
     * a sanity checking routine.  The test should always pass.
     *
     * The idMustBeInvalid parameter is used to inforce the requirement that
     * it any DataValue or Column Predicate has not been inserted in the index,
     * then none of its descendant may have been inserted in the index either.
     *
     *                                              JRM -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    private void validateColPredArgList(MatrixVocabElement mve,
                                        boolean idMustBeInvalid)
        throws SystemErrorException
    {
        final String mName = "ColPred::validateColPredArgList(): ";
        int i = 0;
        FormalArgument fa;
        DataValue arg = null;
        ColPredFormalArg cpfa;
        FloatFormalArg ffa;
        IntFormalArg ifa;
        NominalFormalArg nfa;
        PredFormalArg pfa;
        TimeStampFormalArg tsfa;
        ColPredDataValue cpdv;
        FloatDataValue fdv;
        IntDataValue idv;
        NominalDataValue ndv;
        PredDataValue pdv;
        TimeStampDataValue tsdv;

        if ( argList == null )
        {
            /* argList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "argList unitialized?!?!");
        }
        else if ( this.getNumArgs() <= 0 )
        {
            throw new SystemErrorException(mName + "this.getNumArgs() <= 0");
        }


        // Now scan the argument list
        while ( i < this.getNumArgs() )
        {

            // get the i-th column predicate formal argument.  This is the
            // mve's actual column predicate argument, so be careful not to
            // modify it in any way.
            fa = mve.getCPFormalArg(i);

            if ( fa == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th formal argument?!?!");
            }

            // get the i'th argument from the argument list.  Again, this
            // is the actual argument -- must be careful not to modify it
            // in any way.
            arg = this.argList.get(i);

            if ( arg == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th argument?!?!");
            }

            if ( fa.getID() != arg.getItsFargID() )
            {
                throw new SystemErrorException(mName +
                                "fa.getID() != arg.getItsFargID()");
            }

            if ( ( idMustBeInvalid ) &&
                 ( arg.getID() != DBIndex.INVALID_ID ) )
            {
                throw new SystemErrorException(mName + "arg " + i +
                        " id set when invalid id required");
            }

            this.validateArgAsignment(fa, arg);

            // todo: delete this eventually
//            switch (fa.getFargType())
//            {
//                case COL_PREDICATE:
//                    if ( ! ( arg instanceof ColPredDataValue ) )
//                    {
//                        throw new SystemErrorException(mName +
//                                "Type mismatch: col pred expected");
//                    }
//
//                    cpfa = (ColPredFormalArg)fa;
//                    cpdv = (ColPredDataValue)arg;
//
//                    cpdv.getItsValue().validateColumnPredicate(idMustBeInvalid);
//                    break;
//
//                case FLOAT:
//                    if ( ! ( arg instanceof FloatDataValue ) )
//                    {
//                       throw new SystemErrorException(mName +
//                                "Type mismatch: float expected.");
//                    }
//
//                    ffa = (FloatFormalArg)fa;
//                    fdv = (FloatDataValue)arg;
//
//                    if ( fdv.getSubRange() != ffa.getSubRange() )
//                    {
//                       throw new SystemErrorException(mName +
//                                "fdv.getSubRange() != ffa.getSubRange().");
//                    }
//
//                    if ( fdv.getSubRange() )
//                    {
//                        if ( ( ffa.getMinVal() > fdv.getItsValue() ) ||
//                             ( ffa.getMaxVal() < fdv.getItsValue() ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "fdv.getItsValue() out of range.");
//                        }
//                    }
//                    break;
//
//                case INTEGER:
//                    if ( ! ( arg instanceof IntDataValue ) )
//                    {
//                        throw new SystemErrorException(mName +
//                                "Type mismatch: integer expected.");
//                    }
//
//                    ifa = (IntFormalArg)fa;
//                    idv = (IntDataValue)arg;
//
//                    if ( idv.getSubRange() != ifa.getSubRange() )
//                    {
//                       throw new SystemErrorException(mName +
//                                "idv.getSubRange() != ifa.getSubRange().");
//                    }
//
//                    if ( idv.getSubRange() )
//                    {
//                        if ( ( ifa.getMinVal() > idv.getItsValue() ) ||
//                             ( ifa.getMaxVal() < idv.getItsValue() ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "idv.getItsValue() out of range.");
//                        }
//                    }
//                    break;
//
//                case NOMINAL:
//                    if ( ! ( arg instanceof NominalDataValue ) )
//                    {
//                        throw new SystemErrorException(mName +
//                                "Type mismatch: nominal expected.");
//                    }
//
//                    nfa = (NominalFormalArg)fa;
//                    ndv = (NominalDataValue)arg;
//
//                    if ( ndv.getSubRange() != nfa.getSubRange() )
//                    {
//                       throw new SystemErrorException(mName +
//                                "ndv.getSubRange() != nfa.getSubRange().");
//                    }
//
//                    if ( ( ndv.getSubRange() ) &&
//                         ( ndv.getItsValue() != null ) )
//                    {
//                        if ( ! nfa.approved(ndv.getItsValue()) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "ndv.getItsValue() out of range.");
//                        }
//                    }
//                    break;
//
//                case PREDICATE:
//                    if ( ! ( arg instanceof PredDataValue ) )
//                    {
//                        throw new SystemErrorException(mName +
//                                "Type mismatch: Predicate expected.");
//                    }
//
//                    pfa = (PredFormalArg)fa;
//                    pdv = (PredDataValue)arg;
//
//                    if ( pdv.getSubRange() != pfa.getSubRange() )
//                    {
//                       throw new SystemErrorException(mName +
//                                "pdv.getSubRange() != pfa.getSubRange().");
//                    }
//
//                    if ( ( pdv.getItsValue().getPveID() !=
//                            DBIndex.INVALID_ID ) &&
//                         ( pdv.getSubRange() ) &&
//                         ( ! pfa.approved(pdv.getItsValue().getPveID()) ) )
//                    {
//                        throw new SystemErrorException(mName +
//                                "pdv.getItsValue() out of range.");
//                    }
//
//                    pdv.getItsValue().validatePredicate(idMustBeInvalid);
//
//                    break;
//
//                case TIME_STAMP:
//                    if ( ! ( arg instanceof TimeStampDataValue ) )
//                    {
//                        throw new SystemErrorException(mName +
//                                "Type mismatch: time stamp expected.");
//                    }
//
//                    tsfa = (TimeStampFormalArg)fa;
//                    tsdv = (TimeStampDataValue)arg;
//
//                    if ( tsdv.getSubRange() != tsfa.getSubRange() )
//                    {
//                       throw new SystemErrorException(mName +
//                                "tsdv.getSubRange() != tsfa.getSubRange().");
//                    }
//
//                    if ( tsdv.getSubRange() )
//                    {
//                        if ( ( tsfa.getMinVal().gt(tsdv.getItsValue()) ) ||
//                             ( tsfa.getMaxVal().lt(tsdv.getItsValue()) ) )
//                        {
//                            throw new SystemErrorException(mName +
//                                    "tsdv.getItsValue() out of range.");
//                        }
//                    }
//                    break;
//
//                case QUOTE_STRING:
//                    if ( ! ( arg instanceof QuoteStringDataValue ) )
//                    {
//                        throw new SystemErrorException(mName +
//                                "Type mismatch: quote string expected.");
//                    }
//                    break;
//
//                case TEXT:
//                    if ( ! ( arg instanceof TextStringDataValue ) )
//                    {
//                        throw new SystemErrorException(mName +
//                                "Type mismatch: text string expected.");
//                    }
//                    break;
//
//                case UNTYPED:
//                    if ( arg instanceof TextStringDataValue )
//                    {
//                        throw new SystemErrorException(mName +
//                                "Type mismatch: Text String can't be " +
//                                "substituted for untyped arguments.");
//                    }
//                    else if ( ! ( ( arg instanceof ColPredDataValue ) ||
//                                  ( arg instanceof FloatDataValue ) ||
//                                  ( arg instanceof IntDataValue ) ||
//                                  ( arg instanceof NominalDataValue ) ||
//                                  ( arg instanceof PredDataValue ) ||
//                                  ( arg instanceof TimeStampDataValue ) ||
//                                  ( arg instanceof QuoteStringDataValue ) ||
//                                  ( arg instanceof UndefinedDataValue ) ) )
//                    {
//                        throw new SystemErrorException(mName +
//                                "Unknown subtype of DataValue");
//                    }
//
//                    if ( arg instanceof ColPredDataValue )
//                    {
//                        cpdv = (ColPredDataValue)arg;
//
//                        cpdv.getItsValue().validateColumnPredicate(
//                                idMustBeInvalid);
//                    }
//                    else if ( arg instanceof PredDataValue )
//                    {
//                        pdv = (PredDataValue)arg;
//
//                        pdv.getItsValue().validatePredicate(idMustBeInvalid);
//                    }
//                    break;
//
//                case UNDEFINED:
//                    throw new SystemErrorException(mName +
//                            "formal arg type undefined???");
//                    /* break statement commented out to keep the
//                     * compiler happy
//                     */
//                    // break;
//
//                default:
//                    throw new SystemErrorException(mName +
//                                                   "Unknown Formal Arg Type");
//                    /* break statement commented out to keep the
//                     * compiler happy
//                     */
//                    // break;
//            }

            i++;

        } /* while */

        return;

    } /* Predicate::validateColPredArgList() */


    /**
     * validateReplacementArg()
     *
     * Given a reference to a formal argument, the old value of that argument,
     * and a proposed replacement argument, verify that the new argument is a
     * valid replacement value.  Note that the old argument may be null if
     * cascadeMveMod is true, and cascadeMveID == this.mveID.  The old argument
     * must be defined in all other cases.
     *
     * This method will typically be called during a cascade, and thus the
     * cascade parameters are passed in as they may be needed.  The only ones
     * used in this function are cascadeMveMod and cascadeMveID, which are
     * used to sanity check oldArg.
     *
     * Note that the method must take care to avoid modifying the fa, oldArg,
     * and newArg parameters.
     *
     * The method does nothing if all is as it should be, and throws a system
     * error if any problems are detected.
     *
     *                                              JRM -- 10/28/08
     *
     * Changes:
     *
     *    - None.
     */

    public void validateReplacementArg(FormalArgument fa,
                                       DataValue oldArg,
                                       DataValue newArg,
                                       boolean cascadeMveMod,
                                       boolean cascadeMveDel,
                                       long cascadeMveID,
                                       boolean cascadePveMod,
                                       boolean cascadePveDel,
                                       long cascadePveID)
        throws SystemErrorException
    {
        final String mName = "ColPred::validateReplacementArg(): ";
        boolean idMustBeInvalid = false;
        ColPredFormalArg        cpfa;
        FloatFormalArg          ffa;
        IntFormalArg            ifa;
        NominalFormalArg        nfa;
        PredFormalArg           pfa;
        TimeStampFormalArg      tsfa;
        QuoteStringFormalArg    qsfa;
        TextStringFormalArg     tfa;
        UnTypedFormalArg        ufa;
        ColPredDataValue        new_cpdv;
        FloatDataValue          new_fdv;
        IntDataValue            new_idv;
        NominalDataValue        new_ndv;
        PredDataValue           new_pdv;
        TimeStampDataValue      new_tsdv;
        QuoteStringDataValue    new_qsdv;
        TextStringDataValue     new_tdv;
        ColPredDataValue        old_cpdv;
        FloatDataValue          old_fdv;
        IntDataValue            old_idv;
        NominalDataValue        old_ndv;
        PredDataValue           old_pdv;
        TimeStampDataValue      old_tsdv;
        QuoteStringDataValue    old_qsdv;
        TextStringDataValue     old_tdv;

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry.");
        }
        else if ( fa.getItsVocabElementID() != this.mveID )
        {
            throw new SystemErrorException(mName + "fa has unexpected veID.");
        }

        if ( ( oldArg == null ) &&
             ( ( ! cascadeMveMod ) || ( cascadeMveID != this.mveID ) ) )
        {
            throw new SystemErrorException(mName + "oldArg null unexpectedly.");
        }
        else if ( ( oldArg != null ) &&
                  ( oldArg.getItsFargID() != fa.getID() ) )
        {
            throw new SystemErrorException(mName +
                    "oldArg.getItsFargID() != fa.getID()");
        }

        if ( newArg == null )
        {
            throw new SystemErrorException(mName + "newArg null on entry.");
        }
        else if ( newArg.getItsFargID() != fa.getID() )
        {
            throw new SystemErrorException(mName +
                    "newArg.getItsFargID() != fa.getID()");
        }

        if ( oldArg == null )
        {
            if ( newArg.getID() != DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName +
                        "new arg with id set.");
            }
        }
        else if ( ( newArg.getClass() != oldArg.getClass() ) &&
                  ( newArg.getID() != DBIndex.INVALID_ID ) )
        {
            throw new SystemErrorException(mName +
                    "dv type change and id set");
        }
        else if ( ( oldArg.getClass() == newArg.getClass() ) &&
                  ( newArg.getID() != DBIndex.INVALID_ID ) &&
                  ( newArg.getID() != oldArg.getID() ) )
        {
            throw new SystemErrorException(mName +
                    "dv type match with id set but not matching");
        }

        switch (fa.getFargType())
        {
            case COL_PREDICATE:
                cpfa = (ColPredFormalArg)fa;

                if ( ( oldArg != null ) &&
                     ( ! ( ( oldArg instanceof ColPredDataValue ) ||
                           ( oldArg instanceof UndefinedDataValue ) ||
                           ( ( this.queryVarOK ) ||
                             ( oldArg instanceof NominalDataValue ) &&
                             ( ((NominalDataValue)oldArg).isQueryVar() )
                           )
                         )
                     )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: column predicate DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: column predicate DV, or " +
                                "undefined DV expected.");
                   }
                }

                if ( ! ( ( newArg instanceof ColPredDataValue ) ||
                         ( newArg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( newArg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)newArg).isQueryVar() )
                         )
                       )
                   )
                {
                    if (this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: column predicate DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: column predicate DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( newArg instanceof ColPredDataValue )
                {
                    new_cpdv = (ColPredDataValue)newArg;

                    if ( ( oldArg != null ) &&
                         ( oldArg instanceof ColPredDataValue ) )
                    {
                        old_cpdv = (ColPredDataValue)oldArg;

                        if ( ( cascadeMveMod ) || ( cascadeMveDel ) ||
                             ( cascadePveMod ) || ( cascadePveDel ) )
                        {
                            new_cpdv.getItsValue().
                                    validateReplacementColPred(
                                        old_cpdv.getItsValueBlind(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        else
                        {
                            new_cpdv.getItsValue().
                                    validateReplacementColPred(
                                        old_cpdv.getItsValue(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                    }
                    else
                    {
                        new_cpdv.getItsValue().
                                validateColumnPredicate(true);
                    }
                }
                break;

            case FLOAT:
                ffa = (FloatFormalArg)fa;

                if ( ( oldArg != null ) &&
                     ( ! ( ( oldArg instanceof FloatDataValue ) ||
                           ( oldArg instanceof UndefinedDataValue ) ||
                           ( ( this.queryVarOK ) ||
                             ( oldArg instanceof NominalDataValue ) &&
                             ( ((NominalDataValue)oldArg).isQueryVar() )
                           )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: float DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: float DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( ! ( ( newArg instanceof FloatDataValue ) ||
                         ( newArg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( newArg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)newArg).isQueryVar() )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: float DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: float DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( newArg instanceof FloatDataValue )
                {
                    new_fdv = (FloatDataValue)newArg;

                    if ( new_fdv.getSubRange() != ffa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                         "new_fdv.getSubRange() != ffa.getSubRange().");
                    }

                    if ( new_fdv.getSubRange() )
                    {
                        if ( ( ffa.getMinVal() >
                                new_fdv.getItsValue() ) ||
                             ( ffa.getMaxVal() <
                                new_fdv.getItsValue() ) )
                        {
                            throw new SystemErrorException(mName +
                                "new_fdv.getItsValue() out of range.");
                        }
                    }
                }
                break;

            case INTEGER:
                ifa = (IntFormalArg)fa;

                if ( ( oldArg != null ) &&
                     ( ! ( ( oldArg instanceof IntDataValue ) ||
                           ( oldArg instanceof UndefinedDataValue ) ||
                           ( ( this.queryVarOK ) ||
                             ( oldArg instanceof NominalDataValue ) &&
                             ( ((NominalDataValue)oldArg).isQueryVar() )
                           )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: integer DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: integer DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( ! ( ( newArg instanceof IntDataValue ) ||
                         ( newArg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( newArg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)newArg).isQueryVar() )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: integer DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: integer DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( newArg instanceof IntDataValue )
                {
                    new_idv = (IntDataValue)newArg;

                    if ( new_idv.getSubRange() != ifa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                         "new_idv.getSubRange() != ifa.getSubRange().");
                    }

                    if ( new_idv.getSubRange() )
                    {
                        if ( ( ifa.getMinVal() >
                                new_idv.getItsValue() ) ||
                             ( ifa.getMaxVal() <
                                new_idv.getItsValue() ) )
                        {
                            throw new SystemErrorException(mName +
                                "new_idv.getItsValue() out of range.");
                        }
                    }
                }
                break;

            case NOMINAL:
                nfa = (NominalFormalArg)fa;

                if ( ( oldArg != null ) &&
                     ( ! ( ( oldArg instanceof NominalDataValue ) ||
                         ( oldArg instanceof UndefinedDataValue )
                       )
                     )
                   )
                {
                    throw new SystemErrorException(mName + "Old arg " +
                            "type mismatch: nominal DV, or " +
                            "undefined DV expected.");
                }

                if ( ! ( ( newArg instanceof NominalDataValue ) ||
                         ( newArg instanceof UndefinedDataValue )
                       )
                   )
                {
                    throw new SystemErrorException(mName + "New arg " +
                            "type mismatch: nominaal DV, or " +
                            "undefined DV expected.");
                }

                if ( newArg instanceof NominalDataValue )
                {
                    new_ndv = (NominalDataValue)newArg;

                    if ( new_ndv.getSubRange() != nfa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                         "new_ndv.getSubRange() != nfa.getSubRange().");
                    }

                    if ( ( new_ndv.getSubRange() ) &&
                         ( new_ndv.getItsValue() != null ) )
                    {
                        if ( ( ! nfa.approved(new_ndv.getItsValue()) ) &&
                             ( ( ! this.queryVarOK ) ||
                               ( ! new_ndv.isQueryVar() ) ) )
                        {
                            throw new SystemErrorException(mName +
                                "new_ndv.getItsValue() out of range.");
                        }
                    }
                }
                break;

            case PREDICATE:
                pfa = (PredFormalArg)fa;

                if ( ( oldArg != null ) &&
                     ( ! ( ( oldArg instanceof PredDataValue ) ||
                           ( oldArg instanceof UndefinedDataValue ) ||
                           ( ( this.queryVarOK ) ||
                             ( oldArg instanceof NominalDataValue ) &&
                             ( ((NominalDataValue)oldArg).isQueryVar() )
                           )
                       )
                     )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: predicate DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: predicate DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( ! ( ( newArg instanceof PredDataValue ) ||
                         ( newArg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( newArg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)newArg).isQueryVar() )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: predicate DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: predicate DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( newArg instanceof PredDataValue )
                {
                    new_pdv = (PredDataValue)newArg;

                    if ( new_pdv.getSubRange() != pfa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                         "new_pdv.getSubRange() != pfa.getSubRange().");
                    }

                    if ( ( new_pdv.getItsValue().getPveID() !=
                            DBIndex.INVALID_ID ) &&
                         ( new_pdv.getSubRange() ) &&
                         ( ! pfa.approved(new_pdv.getItsValue().
                                    getPveID()) ) )
                    {
                        throw new SystemErrorException(mName +
                                "new_pdv.getItsValue() out of range.");
                    }

                    if ( ( oldArg != null ) &&
                         ( oldArg instanceof PredDataValue ) )
                    {
                        old_pdv = (PredDataValue)oldArg;

                        if ( ( ! cascadeMveMod ) &&
                             ( ! cascadeMveDel ) &&
                             ( ! cascadePveMod ) &&
                             ( ! cascadePveDel ) )
                        {
                            new_pdv.getItsValue().
                                    validateReplacementPredicate(
                                        old_pdv.getItsValue(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        else
                        {
                            new_pdv.getItsValue().
                                    validateReplacementPredicate(
                                        old_pdv.getItsValueBlind(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                    }
                    else
                    {
                        new_pdv.getItsValue().validatePredicate(true);
                    }
                }
                break;

            case TIME_STAMP:
                tsfa = (TimeStampFormalArg)fa;

                if ( ( oldArg != null ) &&
                     ( ! ( ( oldArg instanceof TimeStampDataValue ) ||
                           ( oldArg instanceof UndefinedDataValue ) ||
                           ( ( this.queryVarOK ) ||
                             ( oldArg instanceof NominalDataValue ) &&
                             ( ((NominalDataValue)oldArg).isQueryVar() )
                           )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: time stamp DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: time stamp DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( ! ( ( newArg instanceof TimeStampDataValue ) ||
                         ( newArg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( newArg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)newArg).isQueryVar() )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: time stamp DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: time stamp DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( ( newArg.getClass() != oldArg.getClass() ) &&
                     ( newArg.getID() != DBIndex.INVALID_ID ) )
                {
                    throw new SystemErrorException(mName +
                            "dv type change and id set");
                }

                if ( newArg instanceof TimeStampDataValue )
                {
                    new_tsdv = (TimeStampDataValue)newArg;

                    if ( new_tsdv.getSubRange() != tsfa.getSubRange() )
                    {
                       throw new SystemErrorException(mName +
                       "new_tsdv.getSubRange() != tsfa.getSubRange().");
                    }

                    if ( new_tsdv.getSubRange() )
                    {
                        if ( ( tsfa.getMinVal().
                                gt(new_tsdv.getItsValue()) ) ||
                             ( tsfa.getMaxVal().
                                lt(new_tsdv.getItsValue()) ) )
                        {
                            throw new SystemErrorException(mName +
                                    "new_tsdv.getItsValue() out of range.");
                        }
                    }
                }
                break;

            case QUOTE_STRING:
                if ( ( oldArg != null ) &&
                     ( ! ( ( oldArg instanceof QuoteStringDataValue ) ||
                           ( oldArg instanceof UndefinedDataValue ) ||
                           ( ( this.queryVarOK ) ||
                             ( oldArg instanceof NominalDataValue ) &&
                             ( ((NominalDataValue)oldArg).isQueryVar() )
                           )
                         )
                     )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: quote string DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: quote string DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( ! ( ( newArg instanceof QuoteStringDataValue ) ||
                         ( newArg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( newArg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)newArg).isQueryVar() )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: quote string DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: quote string DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( ( newArg.getClass() != oldArg.getClass() ) &&
                     ( newArg.getID() != DBIndex.INVALID_ID ) )
                {
                    throw new SystemErrorException(mName +
                            "dv type change and id set");
                }
                break;

            case TEXT:
                if ( ( oldArg != null ) &&
                     ( ! ( ( oldArg instanceof TextStringDataValue ) ||
                           ( oldArg instanceof UndefinedDataValue ) ||
                           ( ( this.queryVarOK ) ||
                             ( oldArg instanceof NominalDataValue ) &&
                             ( ((NominalDataValue)oldArg).isQueryVar() )
                           )
                         )
                     )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: text string DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "Old arg " +
                                "type mismatch: text string DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( ! ( ( newArg instanceof TextStringDataValue ) ||
                         ( newArg instanceof UndefinedDataValue ) ||
                         ( ( this.queryVarOK ) ||
                           ( newArg instanceof NominalDataValue ) &&
                           ( ((NominalDataValue)newArg).isQueryVar() )
                         )
                       )
                   )
                {
                    if ( this.queryVarOK )
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: text string DV, " +
                                "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName + "New arg " +
                                "type mismatch: text string DV, or " +
                                "undefined DV expected.");
                    }
                }

                if ( ( newArg.getClass() != oldArg.getClass() ) &&
                     ( newArg.getID() != DBIndex.INVALID_ID ) )
                {
                    throw new SystemErrorException(mName +
                            "dv type change and id set");
                }
                break;

            case UNTYPED:
                if ( ( ( oldArg != null ) &&
                       ( oldArg instanceof TextStringDataValue ) ) ||
                     ( newArg instanceof TextStringDataValue ) )
                {
                    throw new SystemErrorException(mName +
                            "Type mismatch: Text String can't be " +
                            "substituted for untyped arguments.");
                }
                else if ( ! ( ( newArg instanceof ColPredDataValue ) ||
                              ( newArg instanceof FloatDataValue ) ||
                              ( newArg instanceof IntDataValue ) ||
                              ( newArg instanceof NominalDataValue ) ||
                              ( newArg instanceof PredDataValue ) ||
                              ( newArg instanceof TimeStampDataValue ) ||
                              ( newArg instanceof QuoteStringDataValue ) ||
                              ( newArg instanceof UndefinedDataValue ) ) )
                {
                    throw new SystemErrorException(mName +
                            "Unknown subtype of DataValue");
                }

                if ( newArg instanceof ColPredDataValue )
                {
                    new_cpdv = (ColPredDataValue)newArg;

                    if ( ( oldArg != null ) &&
                         ( oldArg instanceof ColPredDataValue ) )
                    {
                        old_cpdv = (ColPredDataValue)oldArg;

                        if ( ( cascadeMveMod ) || ( cascadeMveDel ) ||
                             ( cascadePveMod ) || ( cascadePveDel ) )
                        {
                            new_cpdv.getItsValue().
                                    validateReplacementColPred(
                                        old_cpdv.getItsValueBlind(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadeMveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        else
                        {
                            new_cpdv.getItsValue().
                                    validateReplacementColPred(
                                        old_cpdv.getItsValue(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadePveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                    }
                    else
                    {
                        new_cpdv.getItsValue().
                                validateColumnPredicate(true);
                    }
                }
                else if ( newArg instanceof PredDataValue )
                {
                    new_pdv = (PredDataValue)newArg;

                    if ( ( oldArg != null ) &&
                         ( oldArg instanceof PredDataValue ) )
                    {
                        old_pdv = (PredDataValue)oldArg;

                        if ( ( cascadeMveMod ) || ( cascadeMveDel ) ||
                             ( cascadePveMod ) || ( cascadePveDel ) )
                        {
                            new_pdv.getItsValue().
                                    validateReplacementPredicate(
                                        old_pdv.getItsValueBlind(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadeMveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                        else
                        {
                            new_pdv.getItsValue().
                                    validateReplacementPredicate(
                                        old_pdv.getItsValue(),
                                        cascadeMveMod,
                                        cascadeMveDel,
                                        cascadeMveID,
                                        cascadeMveMod,
                                        cascadePveDel,
                                        cascadePveID);
                        }
                    }
                    else
                    {
                        new_pdv.getItsValue().
                                validatePredicate(true);
                    }
                }
                break;

            case UNDEFINED:
                throw new SystemErrorException(mName +
                        "formal arg type undefined???");
                /* break statement commented out to keep the
                 * compiler happy
                 */
                // break;

            default:
                throw new SystemErrorException(mName +

                        "Unknown Formal Arg Type");
                /* break statement commented out to keep the
                 * compiler happy
                 */
                // break;
        }

        return;

    } /* ColPred::validateReplacementArg() */


    /**
     * validateReplacementColPred()
     *
     * Verify that this Column Predicate is a valid replacement for the supplied
     * old Column Predicate.  This method is called when a new version of a
     * DataCell is about to replace an old version as the cannonical incarnation
     * of the DataCell.  This is purely a sanity checking routine.  The test
     * should always pass.
     *
     * In all cases, this requires that we verify that if the column predicate
     * is defined, the argument list of the column predicate is congruent with
     * the column predicate formal argument list supplied by the target mveID.
     *
     * Further, if oldColPred is defined, has this same ID as this, and has the
     * same target mve as this, verify that all arguments either have invalid ID,
     * or have an argument of matching type in oldColPred with the same ID.
     * Unless the target mve has been modified (i.e. cascadeMveMod == true and
     * cascadeMveID == this.mveID), these matching arguments must be in the same
     * location in oldColPred's argument list.
     *
     * If oldColPred is either undefined, or has a different ID or target mve,
     * verify that the column predicate and all its arguments have invalid ID.
     *
     * Further processing depends on the values of the remaining arguments:
     *
     * The test assumes that at most one of the cascadeMveMod, cascadeMveDel,
     * cascadePveMod, and cascadePveDel parameters will be true.
     *
     *
     * 1) cascadeMveMod == true
     *
     * If cascadeMveMod is true, then a mve has been modified, and the ID of
     * the modified mve is in cascadeMveID.
     *
     * If cascadeMveID == this.mveID, then the definition of the mve that
     * implies the column predicate represented by this instance of ColPred
     * has changed.  Processing is as described above.
     *
     *
     * 2) cascadeMveDel == true
     *
     * If cascadeMveDel is true, the a mve has been deleted, and the ID of
     * the deleted mve is in cascadeMveID.
     *
     * In this case, verify that this.mveID != cascadeMveID, and then proceed
     * as above.
     *
     *
     * 3) cascadePveMod == true
     *
     * If cascadePveMod is true, then a pve has been modified, and the ID of
     * the modified pve is in cascadeMveID.
     *
     * Proceed as above.
     *
     *
     * 4) cascadePveDel == true
     *
     * If cascadePveDel is true, then a pve has been deleted, and teh ID of
     * the deleted pve is in cascadePveID.
     *
     * Proceed as above.
     *
     *                                              JRM -- 8/30/08
     *
     * Changes:
     *
     *    - None.
     */

    public void validateReplacementColPred(ColPred oldColPred,
                                           boolean cascadeMveMod,
                                           boolean cascadeMveDel,
                                           long cascadeMveID,
                                           boolean cascadePveMod,
                                           boolean cascadePveDel,
                                           long cascadePveID)
        throws SystemErrorException
    {
        final String mName = "ColPred::validateReplacementColPred(): ";
        int i = 0;
        boolean idMustBeInvalid = false;
        MatrixVocabElement mve;
        FormalArgument fa;
        DataValue oldArg = null;
        DataValue newArg = null;
        ColPredFormalArg        cpfa;
        FloatFormalArg          ffa;
        IntFormalArg            ifa;
        NominalFormalArg        nfa;
        PredFormalArg           pfa;
        TimeStampFormalArg      tsfa;
        QuoteStringFormalArg    qsfa;
        TextStringFormalArg     tfa;
        UnTypedFormalArg        ufa;
        ColPredDataValue        new_cpdv;
        FloatDataValue          new_fdv;
        IntDataValue            new_idv;
        NominalDataValue        new_ndv;
        PredDataValue           new_pdv;
        TimeStampDataValue      new_tsdv;
        QuoteStringDataValue    new_qsdv;
        TextStringDataValue     new_tdv;
        ColPredDataValue        old_cpdv;
        FloatDataValue          old_fdv;
        IntDataValue            old_idv;
        NominalDataValue        old_ndv;
        PredDataValue           old_pdv;
        TimeStampDataValue      old_tsdv;
        QuoteStringDataValue    old_qsdv;
        TextStringDataValue     old_tdv;

        if ( oldColPred == null )
        {
            throw new SystemErrorException(mName + "oldColPred is null");
        }
        else if ( oldColPred.mveID == DBIndex.INVALID_ID )
        {
            // old pred is undefined -- verify that it was correctly initialized

            if ( ( oldColPred.mveName == null ) ||
                 ( oldColPred.mveName.compareTo("") != 0 ) ||
                 ( oldColPred.argList != null ) ||
                 ( oldColPred.varLen != false ) )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName +
                        "undefined old col pred with incorrect values");
            }

            idMustBeInvalid = true;
        }

        if ( this.id == DBIndex.INVALID_ID )
        {
            idMustBeInvalid = true;
        }
        else if ( this.id != oldColPred.id )
        {
            throw new SystemErrorException(mName +
                    "this.id set and this.id != oldColPred.id");
        }


        if ( this.mveID == DBIndex.INVALID_ID )
        {
            // undefined column predicate -- verify that it is
            // correctly initialized
            if ( ( this.mveName == null ) ||
                 ( this.mveName.compareTo("") != 0 ) ||
                 ( this.argList != null ) ||
                 ( this.varLen != false ) )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName +
                        "undefined col pred with incorrect values");
            }
        }
        else if ( ( cascadeMveDel ) && ( this.mveID == cascadeMveID ) )
        {
            throw new SystemErrorException(mName +
                    "this.mveID == deleted mve ID?!?!?");
        }
        else
        {
            if ( argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!?!");
            }
            else if ( this.getNumArgs() <= 3 )
            {
                throw new SystemErrorException(mName + "this.getNumArgs() <= 3");
            }

            if ( oldColPred.mveID == this.mveID )
            {
                 if ( ( oldColPred.getNumArgs() != this.getNumArgs() ) &&
                      ( ( ! cascadeMveMod ) || ( cascadeMveID != this.mveID ) ) )
                 {
                     throw new SystemErrorException(mName + "num args mismatch");
                 }
            }
            else // target mve changed
            {
                idMustBeInvalid = true;
            }

            mve = this.lookupMatrixVE(this.mveID);

            if ( mve.getDB() != this.getDB() )
            {
                throw new SystemErrorException(mName +
                                               "mve.getDB() != this.getDB()");
            }

            if ( mve.getNumCPFormalArgs() != this.getNumArgs() )
            {
                throw new SystemErrorException(mName +
                        "mve.getNumCPFormalArgs() != this.getNumArgs()");
            }

            if ( mve.getVarLen() != this.getVarLen() )
            {
                throw new SystemErrorException(mName +
                        "mve.getVarLen() != this.getValLen()");
            }


            // Now scan the argument list
            if ( idMustBeInvalid )
            {
                this.validateColPredArgList(mve, true);
            }
            else if ( ( ! cascadeMveMod ) || ( cascadeMveID != this.mveID ) )
            {
                while ( i < this.getNumArgs() )
                {
                    // get the i-th formal argument.  This is the mve's actual
                    // column predicate argument, so be careful not to modify
                    // it in any way.
                    fa = mve.getCPFormalArg(i);

                    if ( fa == null )
                    {
                        throw new SystemErrorException(mName + "no " + i +
                                "th formal argument?!?!");
                    }

                    // get the i'th arguments from the old and new argument
                    // lists.  Again, these are the actual arguments -- must be
                    // careful not to modify them in any way.
                    newArg = this.argList.get(i);
                    oldArg = oldColPred.argList.get(i);

                    if ( newArg == null )
                    {
                        throw new SystemErrorException(mName + "no new" + i +
                                "th argument?!?!");
                    }

                    if ( oldArg == null )
                    {
                        throw new SystemErrorException(mName + "no old" + i +
                                "th argument?!?!");
                    }

                    if ( fa.getID() != newArg.getItsFargID() )
                    {
                        throw new SystemErrorException(mName +
                                        "fa.getID() != newArg.getItsFargID()");
                    }

                    if ( fa.getID() != oldArg.getItsFargID() )
                    {
                        throw new SystemErrorException(mName +
                                        "fa.getID() != oldArg.getItsFargID()");
                    }

                    if ( oldArg.getID() == DBIndex.INVALID_ID )
                    {
                        throw new SystemErrorException(mName + i +
                                "th old argument not in index?!?!");
                    }

                    if ( ( newArg.getID() != DBIndex.INVALID_ID ) &&
                         ( newArg.getID() != oldArg.getID() ) )
                    {
                        throw new SystemErrorException(mName + i +
                                "th argument id mismatch");
                    }

                    this.validateReplacementArg(fa, oldArg, newArg,
                            cascadeMveMod, cascadeMveDel, cascadeMveID,
                            cascadePveMod, cascadePveDel, cascadePveID);

                    // todo: delete this eventually
//                    switch (fa.getFargType())
//                    {
//                        case COL_PREDICATE:
//                            if ( ( ! ( newArg instanceof ColPredDataValue ) ) ||
//                                 ( ! ( oldArg instanceof ColPredDataValue ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                    "Type mismatch: Column Predicate expected.");
//                            }
//
//                            cpfa = (ColPredFormalArg)fa;
//                            new_cpdv = (ColPredDataValue)newArg;
//                            old_cpdv = (ColPredDataValue)oldArg;
//
//                            if ( ( new_cpdv.getID() == DBIndex.INVALID_ID ) ||
//                                 ( new_cpdv.getItsValue().getID() ==
//                                    DBIndex.INVALID_ID ) )
//                            {
//                                new_cpdv.getItsValue().validateColumnPredicate(true);
//                            }
//                            else if ( ( cascadeMveMod ) || ( cascadeMveDel ) ||
//                                      ( cascadePveMod ) || ( cascadePveDel ) )
//                            {
//                                new_cpdv.getItsValue().
//                                        validateReplacementColPred(
//                                                old_cpdv.getItsValueBlind(),
//                                                cascadeMveMod,
//                                                cascadeMveDel,
//                                                cascadeMveID,
//                                                cascadePveMod,
//                                                cascadePveDel,
//                                                cascadePveID);
//                            }
//                            else
//                            {
//                                new_cpdv.getItsValue().
//                                        validateReplacementColPred(
//                                                old_cpdv.getItsValue(),
//                                                cascadeMveMod,
//                                                cascadeMveDel,
//                                                cascadeMveID,
//                                                cascadePveMod,
//                                                cascadePveDel,
//                                                cascadePveID);
//                            }
//                            break;
//
//                        case FLOAT:
//                            if ( ( ! ( newArg instanceof FloatDataValue ) ) ||
//                                 ( ! ( oldArg instanceof FloatDataValue ) ) )
//                            {
//                               throw new SystemErrorException(mName +
//                                        "Type mismatch: float expected.");
//                            }
//
//                            ffa = (FloatFormalArg)fa;
//                            new_fdv = (FloatDataValue)newArg;
//                            old_fdv = (FloatDataValue)oldArg;
//
//                            if ( new_fdv.getSubRange() != ffa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                                 "new_fdv.getSubRange() != ffa.getSubRange().");
//                            }
//
//                            if ( new_fdv.getSubRange() )
//                            {
//                                if ( ( ffa.getMinVal() >
//                                        new_fdv.getItsValue() ) ||
//                                     ( ffa.getMaxVal() <
//                                        new_fdv.getItsValue() ) )
//                                {
//                                    throw new SystemErrorException(mName +
//                                        "new_fdv.getItsValue() out of range.");
//                                }
//                            }
//                            break;
//
//                        case INTEGER:
//                            if ( ( ! ( newArg instanceof IntDataValue ) ) ||
//                                 ( ! ( oldArg instanceof IntDataValue ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Type mismatch: integer expected.");
//                            }
//
//                            ifa = (IntFormalArg)fa;
//                            new_idv = (IntDataValue)newArg;
//                            old_idv = (IntDataValue)oldArg;
//
//                            if ( new_idv.getSubRange() != ifa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                                 "new_idv.getSubRange() != ifa.getSubRange().");
//                            }
//
//                            if ( new_idv.getSubRange() )
//                            {
//                                if ( ( ifa.getMinVal() >
//                                        new_idv.getItsValue() ) ||
//                                     ( ifa.getMaxVal() <
//                                        new_idv.getItsValue() ) )
//                                {
//                                    throw new SystemErrorException(mName +
//                                        "new_idv.getItsValue() out of range.");
//                                }
//                            }
//                            break;
//
//                        case NOMINAL:
//                            if ( ( ! ( newArg instanceof NominalDataValue ) ) ||
//                                 ( ! ( oldArg instanceof NominalDataValue ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Type mismatch: nominal expected.");
//                            }
//
//                            nfa = (NominalFormalArg)fa;
//                            new_ndv = (NominalDataValue)newArg;
//                            old_ndv = (NominalDataValue)oldArg;
//
//                            if ( new_ndv.getSubRange() != nfa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                                 "new_ndv.getSubRange() != nfa.getSubRange().");
//                            }
//
//                            if ( ( new_ndv.getSubRange() ) &&
//                                 ( new_ndv.getItsValue() != null ) )
//                            {
//                                if ( ! nfa.approved(new_ndv.getItsValue()) )
//                                {
//                                    throw new SystemErrorException(mName +
//                                        "new_ndv.getItsValue() out of range.");
//                                }
//                            }
//                            break;
//
//                        case PREDICATE:
//                            if ( ( ! ( newArg instanceof PredDataValue ) ) ||
//                                 ( ! ( oldArg instanceof PredDataValue ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Type mismatch: Predicate expected.");
//                            }
//
//                            pfa = (PredFormalArg)fa;
//                            new_pdv = (PredDataValue)newArg;
//                            old_pdv = (PredDataValue)oldArg;
//
//                            if ( new_pdv.getSubRange() != pfa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                                 "new_pdv.getSubRange() != pfa.getSubRange().");
//                            }
//
//                            if ( ( new_pdv.getItsValue().getPveID() !=
//                                    DBIndex.INVALID_ID ) &&
//                                 ( new_pdv.getSubRange() ) &&
//                                 ( ! pfa.approved(new_pdv.getItsValue().
//                                            getPveID()) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "new_pdv.getItsValue() out of range.");
//                            }
//
//                            if ( ( new_pdv.getID() == DBIndex.INVALID_ID ) ||
//                                 ( new_pdv.getItsValue().getID() ==
//                                    DBIndex.INVALID_ID ) )
//                            {
//                                new_pdv.getItsValue().validatePredicate(true);
//                            }
//                            else if ( ( cascadeMveMod ) || ( cascadeMveDel ) ||
//                                      ( cascadePveMod ) || ( cascadePveDel ) )
//                            {
//                                new_pdv.getItsValue().
//                                        validateReplacementPredicate(
//                                                old_pdv.getItsValueBlind(),
//                                                cascadeMveMod,
//                                                cascadeMveDel,
//                                                cascadeMveID,
//                                                cascadePveMod,
//                                                cascadePveMod,
//                                                cascadePveID);
//                            }
//                            else
//                            {
//                                new_pdv.getItsValue().
//                                        validateReplacementPredicate(
//                                                old_pdv.getItsValue(),
//                                                cascadeMveMod,
//                                                cascadeMveDel,
//                                                cascadeMveID,
//                                                cascadePveMod,
//                                                cascadePveMod,
//                                                cascadePveID);
//                            }
//                            break;
//
//                        case TIME_STAMP:
//                            if ( ( ! ( newArg instanceof
//                                        TimeStampDataValue ) ) ||
//                                 ( ! ( oldArg instanceof
//                                        TimeStampDataValue ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Type mismatch: time stamp expected.");
//                            }
//
//                            tsfa = (TimeStampFormalArg)fa;
//                            new_tsdv = (TimeStampDataValue)newArg;
//                            old_tsdv = (TimeStampDataValue)oldArg;
//
//                            if ( new_tsdv.getSubRange() != tsfa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                               "new_tsdv.getSubRange() != tsfa.getSubRange().");
//                            }
//
//                            if ( new_tsdv.getSubRange() )
//                            {
//                                if ( ( tsfa.getMinVal().
//                                        gt(new_tsdv.getItsValue()) ) ||
//                                     ( tsfa.getMaxVal().
//                                        lt(new_tsdv.getItsValue()) ) )
//                                {
//                                    throw new SystemErrorException(mName +
//                                            "new_tsdv.getItsValue() out of range.");
//                                }
//                            }
//                            break;
//
//                        case QUOTE_STRING:
//                            if ( ( ! ( newArg instanceof
//                                        QuoteStringDataValue ) ) ||
//                                 ( ! ( oldArg instanceof
//                                        QuoteStringDataValue ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Type mismatch: quote string expected.");
//                            }
//                            break;
//
//                        case TEXT:
//                            if ( ( ! ( newArg instanceof
//                                        TextStringDataValue ) ) ||
//                                 ( ! ( oldArg instanceof
//                                        TextStringDataValue ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Type mismatch: text string expected.");
//                            }
//                            break;
//
//                        case UNTYPED:
//                            if ( ( newArg instanceof TextStringDataValue ) ||
//                                 ( oldArg instanceof TextStringDataValue ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Type mismatch: Text String can't be " +
//                                        "substituted for untyped arguments.");
//                            }
//                            else if ( ! ( ( newArg instanceof
//                                            ColPredDataValue ) ||
//                                          ( newArg instanceof
//                                            FloatDataValue ) ||
//                                          ( newArg instanceof
//                                            IntDataValue ) ||
//                                          ( newArg instanceof
//                                            NominalDataValue ) ||
//                                          ( newArg instanceof
//                                            PredDataValue ) ||
//                                          ( newArg instanceof
//                                            TimeStampDataValue ) ||
//                                          ( newArg instanceof
//                                            QuoteStringDataValue ) ||
//                                          ( newArg instanceof
//                                            UndefinedDataValue ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Unknown subtype of DataValue");
//                            }
//
//                            if ( ( newArg.getClass() != oldArg.getClass() ) &&
//                                 ( newArg.getID() != DBIndex.INVALID_ID ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "dv type change and id set");
//                            }
//
//                            if ( newArg instanceof ColPredDataValue )
//                            {
//                                new_cpdv = (ColPredDataValue)newArg;
//
//                                if ( oldArg instanceof ColPredDataValue )
//                                {
//                                    old_cpdv = (ColPredDataValue)oldArg;
//
//                                    if ( ( ! cascadeMveMod ) &&
//                                         ( ! cascadeMveDel ) &&
//                                         ( ! cascadePveMod ) &&
//                                         ( ! cascadePveDel ) )
//                                    {
//                                        new_cpdv.getItsValue().
//                                                validateReplacementColPred(
//                                                    old_cpdv.getItsValue(),
//                                                    cascadeMveMod,
//                                                    cascadeMveDel,
//                                                    cascadeMveID,
//                                                    cascadePveMod,
//                                                    cascadePveDel,
//                                                    cascadePveID);
//                                    }
//                                    else
//                                    {
//                                        new_cpdv.getItsValue().
//                                                validateReplacementColPred(
//                                                    old_cpdv.getItsValueBlind(),
//                                                    cascadeMveMod,
//                                                    cascadeMveDel,
//                                                    cascadeMveID,
//                                                    cascadePveMod,
//                                                    cascadePveDel,
//                                                    cascadePveID);
//                                    }
//                                }
//                                else
//                                {
//                                    new_cpdv.getItsValue().
//                                            validateColumnPredicate(true);
//                                }
//                            }
//                            else if ( newArg instanceof PredDataValue )
//                            {
//                                new_pdv = (PredDataValue)newArg;
//
//                                if ( oldArg instanceof PredDataValue )
//                                {
//                                    old_pdv = (PredDataValue)oldArg;
//
//                                    if ( ( ! cascadeMveMod ) &&
//                                         ( ! cascadeMveDel ) &&
//                                         ( ! cascadePveMod ) &&
//                                         ( ! cascadePveDel ) )
//                                    {
//                                        new_pdv.getItsValue().
//                                                validateReplacementPredicate(
//                                                    old_pdv.getItsValue(),
//                                                    cascadeMveMod,
//                                                    cascadeMveDel,
//                                                    cascadeMveID,
//                                                    cascadePveMod,
//                                                    cascadePveDel,
//                                                    cascadePveID);
//                                    }
//                                    else
//                                    {
//                                        new_pdv.getItsValue().
//                                                validateReplacementPredicate(
//                                                    old_pdv.getItsValueBlind(),
//                                                    cascadeMveMod,
//                                                    cascadeMveDel,
//                                                    cascadeMveID,
//                                                    cascadePveMod,
//                                                    cascadePveDel,
//                                                    cascadePveID);
//                                    }
//                                }
//                                else
//                                {
//                                    new_pdv.getItsValue().
//                                            validatePredicate(true);
//                                }
//                            }
//                            break;
//
//                        case UNDEFINED:
//                            throw new SystemErrorException(mName +
//                                    "formal arg type undefined???");
//                            /* break statement commented out to keep the
//                             * compiler happy
//                             */
//                            // break;
//
//                        default:
//                            throw new SystemErrorException(mName +
//                                    "Unknown Formal Arg Type");
//                            /* break statement commented out to keep the
//                             * compiler happy
//                             */
//                            // break;
//                    }

                    i++;

                } /* while */
            }
            else if ( ( cascadeMveMod ) && ( cascadeMveID == this.mveID ) )
            {
                /* The definition of the mve defining both the old and
                 * new versions of the column predicate has changed.  Thus it
                 * is possible that the formal argument list has changed
                 * as well.
                 *
                 * Verify that each of the arguments in the new predicate
                 * match the mve.  Further, for each argument in the new
                 * predicate with a valid id, verify that there is an
                 * argument in the old predicate with the same id and type.
                 */
                while ( i < this.getNumArgs() )
                {
                    // get the i-th formal argument.  This is the mve's actual
                    // argument,  so be careful not to modify it in any way.
                    fa = mve.getCPFormalArg(i);

                    if ( fa == null )
                    {
                        throw new SystemErrorException(mName + "no " + i +
                                "th formal argument?!?!");
                    }

                    // get the i'th argument from the new argument list.
                    // Again, this is the actual argument -- must be
                    // careful not to modify them in any way.
                    newArg = this.argList.get(i);
                    oldArg = null;

                    if ( newArg == null )
                    {
                        throw new SystemErrorException(mName + "no new" + i +
                                "th argument?!?!");
                    }

                    if ( fa.getID() != newArg.getItsFargID() )
                    {
                        throw new SystemErrorException(mName +
                                        "fa.getID() != newArg.getItsFargID()");
                    }

                    if ( newArg.getID() != DBIndex.INVALID_ID )
                    {
                        // the old argument list must contain an argument
                        // with the same ID.  Scan the list to find it.
                        int j = 0;

                        while ( ( j < oldColPred.getNumArgs() ) &&
                                ( oldArg == null ) )
                        {
                            oldArg = oldColPred.argList.get(j);

                            if ( oldArg.getID() == DBIndex.INVALID_ID )
                            {
                                throw new SystemErrorException(mName + i +
                                        "th old argument not in index?!?!");
                            }

                            if ( oldArg.getID() != newArg.getID() )
                            {
                                oldArg = null;
                            }

                            j++;
                        }

                        if ( oldArg == null )
                        {
                            throw new SystemErrorException(mName +
                                "new arg has valid ID but no matching old arg.");
                        }
                    }

                    if ( ( oldArg != null ) &&
                         ( fa.getID() != oldArg.getItsFargID() ) )
                    {
                        throw new SystemErrorException(mName +
                                        "fa.getID() != oldArg.getItsFargID()");
                    }

                    this.validateReplacementArg(fa, oldArg, newArg,
                            cascadeMveMod, cascadeMveDel, cascadeMveID,
                            cascadePveMod, cascadePveDel, cascadePveID);

                    // todo: delete this eventually
//                    switch (fa.getFargType())
//                    {
//                        case COL_PREDICATE:
//                            if ( ( ! ( newArg instanceof ColPredDataValue ) ) ||
//                                 ( ( oldArg != null ) &&
//                                   ( ! ( oldArg instanceof ColPredDataValue ) )
//                                 )
//                               )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Type mismatch: col pred(s) expected.");
//                            }
//
//                            cpfa = (ColPredFormalArg)fa;
//                            new_cpdv = (ColPredDataValue)newArg;
//
//                            if ( oldArg != null )
//                            {
//                                old_cpdv = (ColPredDataValue)oldArg;
//                            }
//                            else
//                            {
//                                old_cpdv = null;
//                            }
//
//                            if ( ( new_cpdv.getID() == DBIndex.INVALID_ID ) ||
//                                 ( new_cpdv.getItsValue().getID() ==
//                                    DBIndex.INVALID_ID ) )
//                            {
//                                new_cpdv.getItsValue().validateColumnPredicate(true);
//                            }
//                            else if ( old_cpdv != null )
//                            {
//                                new_cpdv.getItsValue().
//                                        validateReplacementColPred(
//                                            old_cpdv.getItsValueBlind(),
//                                            cascadeMveMod,
//                                            cascadeMveDel,
//                                            cascadeMveID,
//                                            cascadePveMod,
//                                            cascadePveDel,
//                                            cascadePveID);
//                            }
//                            else
//                            {
//                                throw new SystemErrorException(mName +
//                                "new_cpdv has valid ID but old_cpdv is null?!?");
//                            }
//                            break;
//
//                        case FLOAT:
//                            if ( ( ! ( newArg instanceof FloatDataValue ) ) ||
//                                 ( ( oldArg != null ) &&
//                                   ( ! ( oldArg instanceof FloatDataValue ) ) ) )
//                            {
//                               throw new SystemErrorException(mName +
//                                        "Type mismatch: float(s) expected.");
//                            }
//
//                            ffa = (FloatFormalArg)fa;
//                            new_fdv = (FloatDataValue)newArg;
//
//                            if ( new_fdv.getSubRange() != ffa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                                 "new_fdv.getSubRange() != ffa.getSubRange().");
//                            }
//
//                            if ( new_fdv.getSubRange() )
//                            {
//                                if ( ( ffa.getMinVal() >
//                                        new_fdv.getItsValue() ) ||
//                                     ( ffa.getMaxVal() <
//                                        new_fdv.getItsValue() ) )
//                                {
//                                    throw new SystemErrorException(mName +
//                                        "new_fdv.getItsValue() out of range.");
//                                }
//                            }
//                            break;
//
//                        case INTEGER:
//                            if ( ( ! ( newArg instanceof IntDataValue ) ) ||
//                                 ( ( oldArg != null ) &&
//                                   ( ! ( oldArg instanceof IntDataValue ) ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Type mismatch: integer(s) expected.");
//                            }
//
//                            ifa = (IntFormalArg)fa;
//                            new_idv = (IntDataValue)newArg;
//
//                            if ( new_idv.getSubRange() != ifa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                                 "new_idv.getSubRange() != ifa.getSubRange().");
//                            }
//
//                            if ( new_idv.getSubRange() )
//                            {
//                                if ( ( ifa.getMinVal() >
//                                        new_idv.getItsValue() ) ||
//                                     ( ifa.getMaxVal() <
//                                        new_idv.getItsValue() ) )
//                                {
//                                    throw new SystemErrorException(mName +
//                                        "new_idv.getItsValue() out of range.");
//                                }
//                            }
//                            break;
//
//                        case NOMINAL:
//                            if ( ( ! ( newArg instanceof NominalDataValue ) ) ||
//                                 ( ( oldArg != null ) &&
//                                   ( ! ( oldArg instanceof NominalDataValue ) )
//                                 )
//                               )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Type mismatch: nominal(s) expected.");
//                            }
//
//                            nfa = (NominalFormalArg)fa;
//                            new_ndv = (NominalDataValue)newArg;
//
//                            if ( new_ndv.getSubRange() != nfa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                                 "new_ndv.getSubRange() != nfa.getSubRange().");
//                            }
//
//                            if ( ( new_ndv.getSubRange() ) &&
//                                 ( new_ndv.getItsValue() != null ) )
//                            {
//                                if ( ! nfa.approved(new_ndv.getItsValue()) )
//                                {
//                                    throw new SystemErrorException(mName +
//                                        "new_ndv.getItsValue() out of range.");
//                                }
//                            }
//                            break;
//
//                        case PREDICATE:
//                            if ( ( ! ( newArg instanceof PredDataValue ) ) ||
//                                 ( ( oldArg != null ) &&
//                                   ( ! ( oldArg instanceof PredDataValue ) ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Type mismatch: Predicate(s) expected.");
//                            }
//
//                            pfa = (PredFormalArg)fa;
//                            new_pdv = (PredDataValue)newArg;
//
//                            if ( oldArg != null )
//                            {
//                                old_pdv = (PredDataValue)oldArg;
//                            }
//                            else
//                            {
//                                old_pdv = null;
//                            }
//
//                            if ( new_pdv.getSubRange() != pfa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                                 "new_pdv.getSubRange() != pfa.getSubRange().");
//                            }
//
//                            if ( ( new_pdv.getItsValue().getPveID() !=
//                                    DBIndex.INVALID_ID ) &&
//                                 ( new_pdv.getSubRange() ) &&
//                                 ( ! pfa.approved(new_pdv.getItsValue().
//                                            getPveID()) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "new_pdv.getItsValue() out of range.");
//                            }
//
//                            if ( ( new_pdv.getID() == DBIndex.INVALID_ID ) ||
//                                 ( new_pdv.getItsValue().getID() ==
//                                    DBIndex.INVALID_ID ) )
//                            {
//                                new_pdv.getItsValue().validatePredicate(true);
//                            }
//                            else if ( old_pdv != null )
//                            {
//                                new_pdv.getItsValue().
//                                        validateReplacementPredicate(
//                                            old_pdv.getItsValueBlind(),
//                                            cascadeMveMod,
//                                            cascadeMveDel,
//                                            cascadeMveID,
//                                            cascadePveMod,
//                                            cascadePveDel,
//                                            cascadePveID);
//                            }
//                            else
//                            {
//                                throw new SystemErrorException(mName +
//                                "new_pdv has valid ID but old_pdv is null?!?");
//                            }
//                            break;
//
//                        case TIME_STAMP:
//                            if ( ( ! ( newArg instanceof
//                                        TimeStampDataValue ) ) ||
//                                 ( ( oldArg != null ) &&
//                                   ( ! ( oldArg instanceof
//                                          TimeStampDataValue ) ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                    "Type mismatch: time stamp(s) expected.");
//                            }
//
//                            tsfa = (TimeStampFormalArg)fa;
//                            new_tsdv = (TimeStampDataValue)newArg;
//
//                            if ( new_tsdv.getSubRange() != tsfa.getSubRange() )
//                            {
//                               throw new SystemErrorException(mName +
//                               "new_tsdv.getSubRange() != tsfa.getSubRange().");
//                            }
//
//                            if ( new_tsdv.getSubRange() )
//                            {
//                                if ( ( tsfa.getMinVal().
//                                        gt(new_tsdv.getItsValue()) ) ||
//                                     ( tsfa.getMaxVal().
//                                        lt(new_tsdv.getItsValue()) ) )
//                                {
//                                    throw new SystemErrorException(mName +
//                                            "new_tsdv.getItsValue() out of range.");
//                                }
//                            }
//                            break;
//
//                        case QUOTE_STRING:
//                            if ( ( ! ( newArg instanceof
//                                        QuoteStringDataValue ) ) ||
//                                 ( ( oldArg != null ) &&
//                                   ( ! ( oldArg instanceof
//                                          QuoteStringDataValue ) ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Type mismatch: quote string(s) expected.");
//                            }
//                            break;
//
//                        case TEXT:
//                            if ( ( ! ( newArg instanceof
//                                        TextStringDataValue ) ) ||
//                                 ( ( oldArg != null ) &&
//                                   ( ! ( oldArg instanceof
//                                          TextStringDataValue ) ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Type mismatch: text string(s) expected.");
//                            }
//                            break;
//
//                        case UNTYPED:
//                            if ( ( newArg instanceof TextStringDataValue ) ||
//                                 ( ( oldArg != null ) &&
//                                   ( oldArg instanceof TextStringDataValue ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                    "Type mismatch: Text String(s) can't be " +
//                                    "substituted for untyped arguments.");
//                            }
//                            else if ( ! ( ( newArg instanceof
//                                            ColPredDataValue ) ||
//                                          ( newArg instanceof
//                                            FloatDataValue ) ||
//                                          ( newArg instanceof
//                                            IntDataValue ) ||
//                                          ( newArg instanceof
//                                            NominalDataValue ) ||
//                                          ( newArg instanceof
//                                            PredDataValue ) ||
//                                          ( newArg instanceof
//                                            TimeStampDataValue ) ||
//                                          ( newArg instanceof
//                                            QuoteStringDataValue ) ||
//                                          ( newArg instanceof
//                                            UndefinedDataValue ) ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "Unknown subtype of DataValue");
//                            }
//
//                            if ( ( ( oldArg == null )
//                                   ||
//                                   ( newArg.getClass() != oldArg.getClass() )
//                                 )
//                                 &&
//                                 ( newArg.getID() != DBIndex.INVALID_ID ) )
//                            {
//                                throw new SystemErrorException(mName +
//                                        "dv type change and id set(2)");
//                            }
//
//                            if ( newArg instanceof ColPredDataValue )
//                            {
//                                new_cpdv = (ColPredDataValue)newArg;
//
//                                if ( ( oldArg != null ) &&
//                                     ( oldArg instanceof ColPredDataValue ) )
//                                {
//                                    old_cpdv = (ColPredDataValue)oldArg;
//
//                                    assert(cascadeMveMod);
//
//                                    new_cpdv.getItsValue().
//                                            validateReplacementColPred(
//                                                old_cpdv.getItsValueBlind(),
//                                                cascadeMveMod,
//                                                cascadeMveDel,
//                                                cascadeMveID,
//                                                cascadePveMod,
//                                                cascadePveDel,
//                                                cascadePveID);
//                                }
//                                else
//                                {
//                                    new_cpdv.getItsValue().
//                                            validateColumnPredicate(true);
//                                }
//                            }
//                            else if ( newArg instanceof PredDataValue )
//                            {
//                                new_pdv = (PredDataValue)newArg;
//
//                                if ( ( oldArg != null ) &&
//                                     ( oldArg instanceof PredDataValue ) )
//                                {
//                                    old_pdv = (PredDataValue)oldArg;
//
//                                    assert(cascadeMveMod);
//
//                                    new_pdv.getItsValue().
//                                            validateReplacementPredicate(
//                                                old_pdv.getItsValueBlind(),
//                                                cascadeMveMod,
//                                                cascadeMveDel,
//                                                cascadeMveID,
//                                                cascadeMveMod,
//                                                cascadePveDel,
//                                                cascadePveID);
//                                }
//                                else
//                                {
//                                    new_pdv.getItsValue().
//                                            validatePredicate(true);
//                                }
//                            }
//                            break;
//
//                        case UNDEFINED:
//                            throw new SystemErrorException(mName +
//                                    "formal arg type undefined???");
//                            /* break statement commented out to keep the
//                             * compiler happy
//                             */
//                            // break;
//
//                        default:
//                            throw new SystemErrorException(mName +
//
//                                    "Unknown Formal Arg Type");
//                            /* break statement commented out to keep the
//                             * compiler happy
//                             */
//                            // break;
//                    }

                    i++;

                } /* while */
            }
            else
            {
                throw new SystemErrorException(mName +
                        "this code should be unreachable.");
            }
        }

        return;

    } /* Predicate::validateReplacementPredicate() */


    /*************************************************************************/
    /*************************** Overrides: **********************************/
    /*************************************************************************/

    /**
     * clearID()
     *
     * Call the superclass version of the method, and then pass the clear id
     * message on to the argument list.
     *
     *                                              JRM 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void clearID()
        throws SystemErrorException
    {
        final String mName = "Predicate::clearID()";
        super.clearID();

        if ( this.mveID != DBIndex.INVALID_ID )
        {
            if ( this.argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!?!");
            }

            for ( DataValue dv : this.argList )
            {
                dv.clearID();
            }
        }

        return;

    } /* Predicate::clearID() */


    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Several versions of this class method, all with the objective of
     * constructing instances of ColPred.
     *
     * Returns a reference to the newly constructed predicate if successful.
     * Throws a system error exception on failure.
     *
     *                                              JRM -- 8/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static ColPred Construct(Database db,
                                    long mveID,
                                    DataValue arg0)
        throws SystemErrorException
    {
        final String mName = "ColPred::Construct(db, mveID, arg0)";
        ColPred cp = null;

        cp = new ColPred(db, mveID);

        if ( arg0 != null )
        {
            cp.replaceArg(0, arg0);
        }

        return cp;

    } /* ColPred::Construct(db, mveID, arg0) */


    public static ColPred Construct(Database db,
                                    long mveID,
                                    DataValue arg0,
                                    DataValue arg1)
        throws SystemErrorException
    {
        final String mName = "ColPred::Construct(db, mveID, arg0, arg1)";
        ColPred cp = null;

        cp = ColPred.Construct(db, mveID, arg0);

        if ( arg1 != null )
        {
            cp.replaceArg(1, arg1);
        }

        return cp;

    } /* ColPred::Construct(db, mveID, arg0, arg1) */


    public static ColPred Construct(Database db,
                                    long mveID,
                                    DataValue arg0,
                                    DataValue arg1,
                                    DataValue arg2)
        throws SystemErrorException
    {
        final String mName = "ColPred::Construct(db, mveID, arg0, arg1, arg2)";
        ColPred cp = null;

        cp = ColPred.Construct(db, mveID, arg0, arg1);

        if ( arg2 != null )
        {
            cp.replaceArg(2, arg2);
        }

        return cp;

    } /* ColPred::Construct(db, mveID, arg0, arg1, arg2) */


    public static ColPred Construct(Database db,
                                    long mveID,
                                    DataValue arg0,
                                    DataValue arg1,
                                    DataValue arg2,
                                    DataValue arg3)
        throws SystemErrorException
    {
        final String mName =
                "ColPred::Construct(db, mveID, arg0, arg1, arg2, arg3)";
        ColPred cp = null;

        cp = ColPred.Construct(db, mveID, arg0, arg1, arg2);

        if ( arg3 != null )
        {
            cp.replaceArg(3, arg3);
        }

        return cp;

    } /* ColPred::Construct(db, mveID, arg0, arg1, arg2, arg3) */


    public static ColPred Construct(Database db,
                                    long mveID,
                                    DataValue arg0,
                                    DataValue arg1,
                                    DataValue arg2,
                                    DataValue arg3,
                                    DataValue arg4)
        throws SystemErrorException
    {
        final String mName =
                "ColPred::Construct(db, mveID, arg0, arg1, arg2, arg3, arg4)";
        ColPred cp = null;

        cp = ColPred.Construct(db, mveID, arg0, arg1, arg2, arg3);

        if ( arg4 != null )
        {
            cp.replaceArg(4, arg4);
        }

        return cp;

    } /* ColPred::Construct(db, mveID, arg0, arg1, arg2, arg3, arg4) */


    public static ColPred Construct(Database db,
                                    long mveID,
                                    DataValue arg0,
                                    DataValue arg1,
                                    DataValue arg2,
                                    DataValue arg3,
                                    DataValue arg4,
                                    DataValue arg5)
        throws SystemErrorException
    {
        final String mName = "ColPred::Construct(db, mveID, arg0, arg1, " +
                                               "arg2, arg3, arg4, arg5)";
        ColPred cp = null;

        cp = ColPred.Construct(db, mveID, arg0, arg1, arg2, arg3, arg4);

        if ( arg5 != null )
        {
            cp.replaceArg(5, arg5);
        }

        return cp;

    } /* ColPred::Construct(db, pveID, arg0, arg1, arg2, arg3, arg4, arg5) */


    /**
     * ColPredsAreLogicallyEqual()
     *
     * Given two instances of ColPred, return true if they contain
     * identical data, and false otherwise.  Observe that the cp0 and
     * cp1 may be null, as this indicates an undefined predicate value.
     *
     *                                              JRM -- 8/11/08
     *
     * Changes:
     *
     *    - None.
     */

    protected static boolean ColPredsAreLogicallyEqual(ColPred cp0,
                                                       ColPred cp1)
        throws SystemErrorException
    {
        final String mName = "ColPred::ColPredsAreLogicallyEqual()";
        boolean colPredsAreEqual = true;

        if ( ( cp0 != null ) && ( cp0.mveName == null ) )
        {
            throw new SystemErrorException(mName + ": cp0.mveName is null.");
        }

        if ( ( cp1 != null ) && ( cp1.mveName == null ) )
        {
            throw new SystemErrorException(mName + ": cp1.mveName is null.");
        }

        if ( cp0 != cp1 )
        {
            if ( ( ( cp0 == null ) && ( cp1 != null ) ) ||
                 ( ( cp0 != null ) && ( cp1 == null ) ) )
            {
                colPredsAreEqual = false;
            }
            else if ( ( cp0.db != cp1.db ) ||
                      ( cp0.id != cp1.id ) ||
                      ( cp0.mveID != cp1.mveID ) ||
                      ( cp0.varLen != cp1.varLen ) )
            {
                colPredsAreEqual = false;
            }
            else if ( ( cp0.mveName != cp1.mveName ) &&
                      ( cp0.mveName.compareTo(cp1.mveName) != 0 ) )
            {
                colPredsAreEqual = false;
            }
            else if ( ( ( cp0.argList == null ) && ( cp1.argList != null ) ) ||
                      ( ( cp0.argList != null ) && ( cp1.argList == null ) ) )
            {
                colPredsAreEqual = false;
            }
            else if ( ( cp0.argList != null ) && ( cp1.argList != null ) )
            {
                if ( cp0.argList.size() != cp1.argList.size() )
                {
                    colPredsAreEqual = false;
                }
                else
                {
                    int i = 0;
                    int num_args = cp0.argList.size();

                    while ( ( i < num_args ) && ( colPredsAreEqual ) )
                    {
                        colPredsAreEqual =
                                DataValue.DataValuesAreLogicallyEqual
                                         (cp0.argList.get(i),
                                          cp1.argList.get(i));
                        i++;
                    }
                }
            }
        }

        return colPredsAreEqual;

    } /* ColPred::colPredsAreLogicallyEqual() */


    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/
    // TODO: Must add tests to verify corrct management of undefined data values
    //       and query variables.  A lot of this will be tested in MacSHAPA file
    //       save reload, and query language -- so perhaps I can get away with
    //       holding off for a while.
    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) One argument constructor:
     *
     *      a) Verify that constructor completes when passed a valid instance
     *         of Database, and returns an instance of ColPred.  Verify that:
     *
     *              colPred.db matches supplied value
     *              colPred.mveID == DBIndex.INVALID_ID
     *              colPred.mveName == ""
     *              colPred.argList == NULL
     *              colPred.varLen == false
     *
     *      b) Verify that constructor fails when passed an invalid db.
     *
     * 2) Two argument constructor:
     *
     *      a) Construct a database and a mve (matrix vocab element).
     *         Insert the mve in the database, and make note of the id
     *         assigned to the mve.  Construct a ColPred passing a reference
     *         to the database and the id of the mve.  Verify that:
     *
     *              colPred.db matches the suplied value
     *              colPred.mveID matches the supplied value
     *              colPred.mveName matches the name of the pve
     *              pred.argList reflects the column predidate formal argument
     *                  list of the mve
     *              colPred.varLen matches the varLen field of the mve.
     *
     *          Do this with various types of mves (i.e. TEXT, INTEGER, MATRIX,
     *          etc.) and with both a fixed length and a variable length mve's.
     *
     *      b) Verify that the constructor fails when passed and invalid
     *         db or an invalid mve id.
     *
     * 3) Three argument constructor:
     *
     *      a) Construct a database and a mve (matrix vocab element).
     *         Insert the mve in the database, and make note of the id
     *         assigned to the mve.  Construct an argument list with valuse
     *         matching the column predicate formal arg list of the mve.
     *         Construct a ColPred, passing the db, the id of the mve, and the
     *         arg list.  Verify that:
     *
     *              colPred.db matches the suplied value
     *              colPred.mveID matches the supplied value
     *              colPred.mveName matches the name of the pve
     *              colpred.argList reflects both the column predicate formal
     *                  argument list of the mve and the supplied argument list.
     *              colPred.varLen matches the varLen field of the mve.
     *
     *          Do this with various types of mves (i.e. TEXT, INTEGER, MATRIX,
     *          etc.) and with both a fixed length and a variable length mve's.
     *
     *      b) Verify that the constructor fails when passed an invalid db,
     *         an invalid mve id, or an invalid argument list.  Note that
     *         we must test argument lists that are null, too short, too long,
     *         and which contain type mis-matches.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and several mve's (matrix vocab
     *         elements). Insert the mve's in the database, and make note
     *         of the id's assigned to the mve's.  Using these mve's, construct
     *         a selection of column predicates with and without argument lists,
     *         and with and without initializations to arguments.
     *
     *         Now use the copy constructor to make copies of these predicates.
     *         Verify that the copies are correct.
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad
     *         value that has to be tested.
     *
     * 5) Accessors:
     *
     *      Verify that the getMveID(), setMveID(), getDB(), getNumArgs(),
     *      getVarLen(), and getMveName() methods perform correctly.
     *
     *      Do this by creating a database and a selection of matrix vocab
     *      elements.  Then create a selection of column predicates, and verify
     *      that the get methods return the expected values.  Then use
     *      setveID() to change the mve ID associated with the column predicates,
     *      and verify that values returned by the get methods have changed
     *      accordingly.
     *
     *      Verify that setMveID() fails when given invalid input.
     *
     *      lookupMatrixVE() is an internal method that has been exercised
     *      already.  Verify that it fails on invalid input.
     *
     * 6) ArgList management:
     *
     *      Verify that argument lists are converted properly when the mveID
     *      is changed.  If salvage is true, must convert values to fit new
     *      formal argument list if possible.
     *
     *      Verify that the getArg() and replaceArg() methods perform as
     *      expected.
     *
     *      Verify that getArg() and replaceArg() methods fail on invalid
     *      input.
     *
     * 7) toString methods:
     *
     *      Verify that all fields are displayed correctly by the toString
     *      and toDBString() methods.
     *
     *
     *************************************************************************/


    /**
     * TestClassColPred()
     *
     * Main routine for tests of class ColPred.
     *
     *                                      JRM -- 9/10/08
     *
     * Changes:
     *
     *    - Non.
     */

    public static boolean TestClassColPred(java.io.PrintStream outStream,
                                           boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;

        outStream.print("Testing class ColPred:\n");

        if ( ! Test1ArgConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! Test2ArgConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! Test3ArgConstructor(outStream, verbose) )
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

        if ( ! TestArgListManagement(outStream, verbose) )
        {
            failures++;
        }

        // TODO:  Add test for validateColPred

        if ( ! TestToStringMethods(outStream, verbose) )
        {
            failures++;
        }

        if ( failures > 0 )
        {
            pass = false;
            outStream.printf(
                    "%d failures in tests for class Predicate.\n\n",
                    failures);
        }
        else
        {
            outStream.print(
                    "All tests passed for class Predicate.\n\n");
        }

        return pass;

    } /* ColPred::TestClassColPred() */


    /**
     * Test1ArgConstructor()
     *
     * Run a battery of tests on the one argument constructor for this
     * class, and on the instance returned.
     *
     *                                              JRM -- 9/10/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test1ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing 1 argument constructor for class ColPred                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        ColPred cp = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        cp = null;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();
            cp = new ColPred(db);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( cp == null ) ||
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

                if ( cp == null )
                {
                    outStream.print(
                            "new ColPred(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new ColPred(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( cp.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("db not initialized correctly.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( cp.getMveID() != DBIndex.INVALID_ID )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of mveID: %ld.\n",
                            cp.getMveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( cp.getMveName().compareTo("") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of mveName: \"%s\".\n",
                            cp.getMveName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( cp.argList != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( cp.getVarLen() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of varLen.\n");
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            cp = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                cp = new ColPred((Database)null);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cp != null ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("new ColPred(null) returned.\n");
                    }

                    if ( cp != null )
                    {
                        outStream.print(
                                "new ColPred(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new ColPred(null) failed to throw " +
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

    } /* ColPred::Test1ArgConstructor() */


    /**
     * Test2ArgConstructor()
     *
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 9/10/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test2ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing 2 argument constructor for class ColPred                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        ColPred float_cp = null;
        ColPred int_cp = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp2 = null;
        ColPred nominal_cp = null;
        ColPred pred_cp = null;
        ColPred text_cp = null;
        ColPred cp0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 7 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 7 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // having set up a selection of test mve's, now try to allocate some
        // matricies.  Use toString and toDBString to verify that they are
        // initialized correctly.
        if ( failures == 0 )
        {
            String float_cp_string =
                "float_mve(0, 00:00:00:000, 00:00:00:000, 0.0)";
            String int_cp_string =
                "int_mve(0, 00:00:00:000, 00:00:00:000, 0)";
            String matrix_cp0_string =
                "matrix_mve0(0, 00:00:00:000, 00:00:00:000, " +
                    "0.0, 0, , (), \"\", 00:00:00:000, <untyped>)";
            String matrix_cp1_string =
                "matrix_mve1(0, 00:00:00:000, 00:00:00:000, " +
                    "<arg1>, <arg2>, <arg3>)";
            String matrix_cp2_string =
                "matrix_mve2(0, 00:00:00:000, 00:00:00:000, <arg1>)";
            String nominal_cp_string =
                "nominal_mve(0, 00:00:00:000, 00:00:00:000, )";
            String pred_cp_string =
                "pred_mve(0, 00:00:00:000, 00:00:00:000, ())";
            String text_cp_string =
                "text_mve(0, 00:00:00:000, 00:00:00:000, )";
            String float_cp_DBstring =
                "(colPred (id 0) (mveID 1) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 3) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 4) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 5) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 6) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 1) " +
//                    "(mveName float_mve) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 3) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 4) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 5) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(FloatDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 6) " +
//                            "(itsFargType FLOAT) " +
//                            "(itsCellID 0) " +
//                            "(itsValue 0.0) " +
//                            "(subRange false) " +
//                            "(minVal 0.0) " +
//                            "(maxVal 0.0))))))";
            String int_cp_DBstring =
                "(colPred (id 0) (mveID 7) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 9) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 11) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 12) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 7) " +
//                    "(mveName int_mve) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 9) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 10) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 11) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(IntDataValue (id 0) " +
//                            "(itsFargID 12) " +
//                            "(itsFargType INTEGER) " +
//                            "(itsCellID 0) " +
//                            "(itsValue 0) " +
//                            "(subRange false) " +
//                            "(minVal 0) " +
//                            "(maxVal 0))))))";
            String matrix_cp0_DBstring =
                "(colPred (id 0) (mveID 13) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 21) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 22) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 23) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 24) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 25) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 26) (itsFargType NOMINAL) (itsCellID 0) (itsValue <null>) (subRange false)), (PredDataValue (id 0) (itsFargID 27) (itsFargType PREDICATE) (itsCellID 0) (itsValue ()) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 28) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue <null>) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 29) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 30) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 13) " +
//                    "(mveName matrix_mve0) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 21) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 22) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 23) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(FloatDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 24) " +
//                            "(itsFargType FLOAT) " +
//                            "(itsCellID 0) " +
//                            "(itsValue 0.0) " +
//                            "(subRange false) " +
//                            "(minVal 0.0) " +
//                            "(maxVal 0.0)), " +
//                        "(IntDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 25) " +
//                            "(itsFargType INTEGER) " +
//                            "(itsCellID 0) " +
//                            "(itsValue 0) " +
//                            "(subRange false) " +
//                            "(minVal 0) " +
//                            "(maxVal 0)), " +
//                        "(NominalDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 26) " +
//                            "(itsFargType NOMINAL) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <null>) " +
//                            "(subRange false)), " +
//                        "(PredDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 27) " +
//                            "(itsFargType PREDICATE) " +
//                            "(itsCellID 0) " +
//                            "(itsValue ()) " +
//                            "(subRange false)), " +
//                        "(QuoteStringDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 28) " +
//                            "(itsFargType QUOTE_STRING) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <null>) " +
//                            "(subRange false)), " +
//                        "(TimeStampDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 29) " +
//                            "(itsFargType TIME_STAMP) " +
//                            "(itsCellID 0) " +
//                            "(itsValue (60,00:00:00:000)) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 30) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <untyped>) " +
//                            "(subRange false))))))";
            String matrix_cp1_DBstring =
                "(colPred (id 0) (mveID 31) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 35) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 36) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 37) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 38) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 39) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 40) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg3>) (subRange false))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 31) " +
//                    "(mveName matrix_mve1) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 35) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 36) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue (id 0) " +
//                            "(itsFargID 37) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 38) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <arg1>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 39) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <arg2>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 40) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <arg3>) " +
//                            "(subRange false))))))";
            String matrix_cp2_DBstring =
                "(colPred (id 0) (mveID 41) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 43) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 44) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 45) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 46) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 41) " +
//                    "(mveName matrix_mve2) " +
//                    "(varLen true) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 43) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 44) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 45) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 46) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <arg1>) " +
//                            "(subRange false))))))";
            String nominal_cp_DBstring =
                "(colPred (id 0) (mveID 47) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 49) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 50) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 51) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 52) (itsFargType NOMINAL) (itsCellID 0) (itsValue <null>) (subRange false))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 47) " +
//                    "(mveName nominal_mve) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 49) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 50) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 51) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(NominalDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 52) " +
//                            "(itsFargType NOMINAL) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <null>) " +
//                            "(subRange false))))))";
            String pred_cp_DBstring =
                "(colPred (id 0) (mveID 53) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 55) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 56) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 57) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 58) (itsFargType PREDICATE) (itsCellID 0) (itsValue ()) (subRange false))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 53) " +
//                    "(mveName pred_mve) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 55) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 56) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 57) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(PredDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 58) " +
//                            "(itsFargType PREDICATE) " +
//                            "(itsCellID 0) " +
//                            "(itsValue ()) " +
//                            "(subRange false))))))";
            String text_cp_DBstring =
                "(colPred (id 0) (mveID 59) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 61) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 62) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 63) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 64) (itsFargType TEXT) (itsCellID 0) (itsValue <null>) (subRange false))))))";
//                "(colPred " +
//                    "(id 0) " +
//                    "(mveID 59) " +
//                    "(mveName text_mve) " +
//                    "(varLen false) " +
//                    "(argList " +
//                        "((UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 61) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <ord>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 62) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <onset>) " +
//                            "(subRange false)), " +
//                        "(UndefinedDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 63) " +
//                            "(itsFargType UNTYPED) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <offset>) " +
//                            "(subRange false)), " +
//                        "(TextStringDataValue " +
//                            "(id 0) " +
//                            "(itsFargID 64) " +
//                            "(itsFargType TEXT) " +
//                            "(itsCellID 0) " +
//                            "(itsValue <null>) " +
//                            "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                float_cp = new ColPred(db, float_mve_ID);
                int_cp = new ColPred(db, int_mve_ID);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID);
                matrix_cp1 = new ColPred(db, matrix_mve1_ID);
                matrix_cp2 = new ColPred(db, matrix_mve2_ID);
                nominal_cp = new ColPred(db, nominal_mve_ID);
                pred_cp = new ColPred(db, pred_mve_ID);
                text_cp = new ColPred(db, text_mve_ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp == null ) ||
                 ( int_cp == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp == null ) ||
                 ( pred_cp == null ) ||
                 ( text_cp == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp == null )
                    {
                        outStream.printf("allocation of float_cp failed.\n");
                    }

                    if ( int_cp == null )
                    {
                        outStream.printf("allocation of int_cp failed.\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed.\n");
                    }

                    if ( matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp1 failed.\n");
                    }

                    if ( matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp2 failed.\n");
                    }

                    if ( nominal_cp == null )
                    {
                        outStream.printf("allocation of nominal_cp failed.\n");
                    }

                    if ( pred_cp == null )
                    {
                        outStream.printf("allocation of pred_cp failed.\n");
                    }

                    if ( text_cp == null )
                    {
                        outStream.printf("allocation of text_cp failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "Creation of test ColPreds failed to complete");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "ColPred creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_cp.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( int_cp.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( matrix_cp0.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp1.toString().
                        compareTo(matrix_cp1_string) != 0 ) ||
                      ( matrix_cp2.toString().
                        compareTo(matrix_cp2_string) != 0 ) ||
                      ( nominal_cp.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( pred_cp.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( text_cp.toString().
                        compareTo(text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toString().
                         compareTo(float_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toString(): %s\n",
                                float_cp.toString());
                    }

                    if ( int_cp.toString().
                         compareTo(int_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toString(): %s\n",
                                int_cp.toString());
                    }

                    if ( matrix_cp0.toString().
                         compareTo(matrix_cp0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toString(): %s\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp1.toString().
                         compareTo(matrix_cp1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toString(): %s\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().
                         compareTo(matrix_cp2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toString(): %s\n",
                                matrix_cp2.toString());
                    }

                    if ( nominal_cp.toString().
                         compareTo(nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toString(): %s\n",
                                nominal_cp.toString());
                    }

                    if ( pred_cp.toString().
                         compareTo(pred_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toString(): %s\n",
                                pred_cp.toString());
                    }

                    if ( text_cp.toString().
                         compareTo(text_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toString(): %s\n",
                                text_cp.toString());
                    }
                }
            }
            else if ( ( float_cp.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( int_cp.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( matrix_cp0.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp1.toDBString().
                        compareTo(matrix_cp1_DBstring) != 0 ) ||
                      ( matrix_cp2.toDBString().
                        compareTo(matrix_cp2_DBstring) != 0 ) ||
                      ( nominal_cp.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( pred_cp.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( text_cp.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toDBString().
                         compareTo(float_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toDBString(): %s\n",
                                float_cp.toDBString());
                    }

                    if ( int_cp.toDBString().
                         compareTo(int_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toDBString(): %s\n",
                                int_cp.toDBString());
                    }

                    if ( matrix_cp0.toDBString().
                         compareTo(matrix_cp0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toDBString(): %s\n",
                                matrix_cp0.toDBString());
                    }

                    if ( matrix_cp1.toDBString().
                         compareTo(matrix_cp1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toDBString(): %s\n",
                                matrix_cp1.toDBString());
                    }

                    if ( matrix_cp2.toDBString().
                         compareTo(matrix_cp2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toDBString(): %s\n",
                                matrix_cp2.toDBString());
                    }

                    if ( nominal_cp.toDBString().
                         compareTo(nominal_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toDBString(): %s\n",
                                nominal_cp.toDBString());
                    }

                    if ( pred_cp.toDBString().
                         compareTo(pred_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toDBString(): %s\n",
                                pred_cp.toDBString());
                    }

                    if ( text_cp.toDBString().
                         compareTo(text_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toDBString(): %s\n",
                                text_cp.toDBString());
                    }
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db */
        cp0 = null;
        completed = false;
        threwSystemErrorException = false;

        try
        {
            cp0 = new ColPred(null, float_mve_ID);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( cp0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( cp0 != null )
                {
                    outStream.print(
                            "\"new ColPred(null, float_mve_ID) != null.\n");
                }

                if ( completed )
                {
                    outStream.print(
                            "\"new ColPred(null, float_mve_ID) completed.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "\"new ColPred(null, float_mve_ID) " +
                            "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now verify that the constructor fails when passed an invalid
         * matrix vocab element ID.
         */

        cp0 = null;
        completed = false;
        threwSystemErrorException = false;

        try
        {
            cp0 = new ColPred(new ODBCDatabase(), float_mve_ID);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( cp0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( cp0 != null )
                {
                    outStream.print("new ColPred(new ODBCDatabase(), " +
                                    "float_mve_ID) != null.\n");
                }

                if ( completed )
                {
                    outStream.print("new ColPred(new ODBCDatabase(), " +
                                    "float_mve_ID) completed.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "new ColPred(new ODBCDatabase(), float_mve_ID) " +
                            "didn't throw a SystemErrorException.\n");
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

    } /* ColPred::Test2ArgConstructor() */


    /**
     * Test3ArgConstructor()
     *
     * Run a battery of tests on the three argument constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 9/11/08
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
            "Testing 3 argument constructor for class ColPred                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long fargID;
        long pve0_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        DataValue arg = null;
        DataValue qstring_arg = null;
        Vector<DataValue> empty_arg_list = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> float_cp_arg_list1 = null;
        Vector<DataValue> int_cp_arg_list = null;
        Vector<DataValue> int_cp_arg_list1 = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list1 = null;
        Vector<DataValue> matrix_cp1_arg_list = null;
        Vector<DataValue> matrix_cp1_arg_list1 = null;
        Vector<DataValue> matrix_cp2_arg_list = null;
        Vector<DataValue> matrix_cp2_arg_list1 = null;
        Vector<DataValue> nominal_cp_arg_list = null;
        Vector<DataValue> nominal_cp_arg_list1 = null;
        Vector<DataValue> pred_cp_arg_list = null;
        Vector<DataValue> pred_cp_arg_list1 = null;
        Vector<DataValue> text_cp_arg_list = null;
        Vector<DataValue> text_cp_arg_list1 = null;
        Vector<DataValue> quote_string_arg_list = null;
        ColPred float_cp = null;
        ColPred int_cp = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp2 = null;
        ColPred nominal_cp = null;
        ColPred pred_cp = null;
        ColPred text_cp = null;
        ColPred cp0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 7 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 7 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // having set up a selection of test mve's, now try to allocate some
        // column predicates.  Use toString and toDBString to verify that they
        // are initialized correctly.

        if ( failures == 0 )
        {
            String float_cp_string =
                    "float_mve(11, 00:00:00:011, 00:00:11:000, 11.0)";
            String int_cp_string =
                    "int_mve(22, 00:00:00:022, 00:00:22:000, 22)";
            String matrix_cp0_string =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, " +
                                "a_nominal, pve0(<arg1>, <arg2>), " +
                                "\"q-string\", 00:00:01:000, <untyped>)";
            String matrix_cp1_string =
                    "matrix_mve1(34, 00:00:00:034, 00:00:34:000, " +
                                "\" a q string \", <arg2>, 88)";
            String matrix_cp2_string =
                    "matrix_mve2(35, 00:00:00:035, 00:00:35:000, <arg1>)";
            String nominal_cp_string =
                    "nominal_mve(44, 00:00:00:044, 00:00:44:000, " +
                                "another_nominal)";
            String pred_cp_string =
                    "pred_mve(55, 00:00:00:055, 00:00:55:000, " +
                                "pve0(<arg1>, <arg2>))";
            String text_cp_string =
                    "text_mve(66, 00:00:01:006, 00:01:06:000, a text string)";
            String float_cp_DBstring =
                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType INTEGER) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType UNTYPED) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(Matrix (mveID 4) " +
//                            "(varLen false) " +
//                            "(argList ((FloatDataValue (id 0) " +
//                                                      "(itsFargID 5) " +
//                                                      "(itsFargType FLOAT) " +
//                                                      "(itsCellID 0) " +
//                                                      "(itsValue 11.0) " +
//                                                      "(subRange false) " +
//                                                      "(minVal 0.0) " +
//                                                      "(maxVal 0.0))))))";
            String int_cp_DBstring =
                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType UNTYPED) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 10) " +
//                            "(varLen false) " +
//                            "(argList ((IntDataValue (id 0) " +
//                                                    "(itsFargID 11) " +
//                                                    "(itsFargType INTEGER) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue 22) " +
//                                                    "(subRange false) " +
//                                                    "(minVal 0) " +
//                                                    "(maxVal 0))))))";
            String matrix_cp0_DBstring =
                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType INTEGER) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType UNTYPED) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(Matrix (mveID 16) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((FloatDataValue (id 0) " +
//                                    "(itsFargID 17) " +
//                                    "(itsFargType FLOAT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 1.0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0.0) " +
//                                    "(maxVal 0.0)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 18) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 2) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0)), " +
//                                "(NominalDataValue (id 0) " +
//                                    "(itsFargID 19) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a_nominal) " +
//                                    "(subRange false)), " +
//                                "(PredDataValue (id 0) " +
//                                    "(itsFargID 20) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg1>) " +
//                                                    "(subRange false)), " +
//                                                "(UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 3) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg2>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false)), " +
//                                "(QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 21) " +
//                                    "(itsFargType QUOTE_STRING) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue q-string) " +
//                                    "(subRange false)), " +
//                                "(TimeStampDataValue (id 0) " +
//                                    "(itsFargID 22) " +
//                                    "(itsFargType TIME_STAMP) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue (60,00:00:01:000)) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 23) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <untyped>) " +
//                                    "(subRange false))))))";
            String matrix_cp1_DBstring =
                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType INTEGER) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType UNTYPED) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 34) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 35) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue  a q string ) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 36) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg2>) " +
//                                    "(subRange false)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 37) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 88) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0))))))";
            String matrix_cp2_DBstring =
                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType INTEGER) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType UNTYPED) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(Matrix (mveID 44) " +
//                            "(varLen true) " +
//                            "(argList " +
//                                "((UndefinedDataValue (id 0) " +
//                                    "(itsFargID 45) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg1>) " +
//                                    "(subRange false))))))";
            String nominal_cp_DBstring =
                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType INTEGER) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType UNTYPED) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(Matrix (mveID 50) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((NominalDataValue (id 0) " +
//                                    "(itsFargID 51) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue another_nominal) " +
//                                    "(subRange false))))))";
            String pred_cp_DBstring =
                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType INTEGER) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType UNTYPED) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false))))))";
//                    "(Matrix (mveID 56) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((PredDataValue (id 0) " +
//                                    "(itsFargID 57) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg1>) " +
//                                                    "(subRange false)), " +
//                                                "(UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 3) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg2>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false))))))";
            String text_cp_DBstring =
                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType INTEGER) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType UNTYPED) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(Matrix (mveID 62) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((TextStringDataValue (id 0) " +
//                                    "(itsFargID 63) " +
//                                    "(itsFargType TEXT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a text string) " +
//                                    "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                empty_arg_list = new Vector<DataValue>();


                float_cp_arg_list = new Vector<DataValue>();
                fargID = float_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 11);
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_cp_arg_list.add(arg);
                float_cp = new ColPred(db, float_mve_ID, float_cp_arg_list);


                int_cp_arg_list = new Vector<DataValue>();
                fargID = int_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22 * db.getTicks()));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(3).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                int_cp = new ColPred(db, int_mve_ID, int_cp_arg_list);


                matrix_cp0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 33);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                qstring_arg = arg; // save to construct quote_string_arg_list
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_cp0_arg_list.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);


                matrix_cp1_arg_list = new Vector<DataValue>();
                fargID = matrix_mve1.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 34);
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34 * db.getTicks()));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(3).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(4).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(1).getFargName());
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_cp1_arg_list.add(arg);
                matrix_cp1 = new ColPred(db, matrix_mve1_ID,
                                         matrix_cp1_arg_list);


                matrix_cp2_arg_list = new Vector<DataValue>();
                fargID = matrix_mve2.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 35);
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35 * db.getTicks()));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(3).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getFormalArg(0).getFargName());
                matrix_cp2_arg_list.add(arg);
                matrix_cp2 = new ColPred(db, matrix_mve2_ID,
                                         matrix_cp2_arg_list);


                nominal_cp_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 44);
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44 * db.getTicks()));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(3).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_cp_arg_list.add(arg);
                nominal_cp = new ColPred(db, nominal_mve_ID,
                                         nominal_cp_arg_list);


                pred_cp_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 55);
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55 * db.getTicks()));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_cp_arg_list.add(arg);
                pred_cp = new ColPred(db, pred_mve_ID, pred_cp_arg_list);


                text_cp_arg_list = new Vector<DataValue>();
                fargID = text_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 66);
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66 * db.getTicks()));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(3).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_cp_arg_list.add(arg);
                text_cp = new ColPred(db, text_mve_ID, text_cp_arg_list);

                quote_string_arg_list = new Vector<DataValue>();
                quote_string_arg_list.add(qstring_arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( empty_arg_list == null ) ||
                 ( float_cp_arg_list == null ) ||
                 ( float_cp == null ) ||
                 ( int_cp_arg_list == null ) ||
                 ( int_cp == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1_arg_list == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2_arg_list == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp_arg_list == null ) ||
                 ( nominal_cp == null ) ||
                 ( pred_cp_arg_list == null ) ||
                 ( pred_cp == null ) ||
                 ( text_cp_arg_list == null ) ||
                 ( text_cp == null ) ||
                 ( quote_string_arg_list == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( empty_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of empty_arg_list failed.\n");
                    }

                    if ( float_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed.\n");
                    }

                    if ( float_cp == null )
                    {
                        outStream.printf("allocation of float_cp failed.\n");
                    }

                    if ( int_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_cp_arg_list failed.\n");
                    }

                    if ( int_cp == null )
                    {
                        outStream.printf("allocation of int_cp failed.\n");
                    }

                    if ( matrix_cp0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp0_arg_list failed.\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed.\n");
                    }

                    if ( matrix_cp1_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp1_arg_list failed.\n");
                    }

                    if ( matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp1 failed.\n");
                    }

                    if ( matrix_cp2_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp2_arg_list failed.\n");
                    }

                    if ( matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp2 failed.\n");
                    }

                    if ( nominal_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_cp_arg_list failed.\n");
                    }

                    if ( nominal_cp == null )
                    {
                        outStream.printf(
                                "allocation of nominal_cp failed.\n");
                    }

                    if ( pred_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp_arg_list failed.\n");
                    }

                    if ( pred_cp == null )
                    {
                        outStream.printf("allocation of pred_cp failed.\n");
                    }

                    if ( text_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_cp_arg_list failed.\n");
                    }

                    if ( text_cp == null )
                    {
                        outStream.printf("allocation of text_cp failed.\n");
                    }

                    if ( quote_string_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of quote_string_arg_list failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                            "Creation of test column preds failed to complete\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "col pred creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_cp.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( int_cp.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( matrix_cp0.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp1.toString().
                        compareTo(matrix_cp1_string) != 0 ) ||
                      ( matrix_cp2.toString().
                        compareTo(matrix_cp2_string) != 0 ) ||
                      ( nominal_cp.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( pred_cp.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( text_cp.toString().
                        compareTo(text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toString().
                         compareTo(float_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toString(): %s\n",
                                float_cp.toString());
                    }

                    if ( int_cp.toString().
                         compareTo(int_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toString(): %s\n",
                                int_cp.toString());
                    }

                    if ( matrix_cp0.toString().
                         compareTo(matrix_cp0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toString(): %s\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp1.toString().
                         compareTo(matrix_cp1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toString(): %s\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().
                         compareTo(matrix_cp2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toString(): %s\n",
                                matrix_cp2.toString());
                    }

                    if ( nominal_cp.toString().
                         compareTo(nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toString(): %s\n",
                                nominal_cp.toString());
                    }

                    if ( pred_cp.toString().
                         compareTo(pred_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toString(): %s\n",
                                pred_cp.toString());
                    }

                    if ( text_cp.toString().
                         compareTo(text_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toString(): %s\n",
                                text_cp.toString());
                    }
                }
            }
            else if ( ( float_cp.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( int_cp.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( matrix_cp0.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp1.toDBString().
                        compareTo(matrix_cp1_DBstring) != 0 ) ||
                      ( matrix_cp2.toDBString().
                        compareTo(matrix_cp2_DBstring) != 0 ) ||
                      ( nominal_cp.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( pred_cp.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( text_cp.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toDBString().
                         compareTo(float_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toDBString(): %s\n",
                                float_cp.toDBString());
                    }

                    if ( int_cp.toDBString().
                         compareTo(int_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_c[.toDBString(): %s\n",
                                int_cp.toDBString());
                    }

                    if ( matrix_cp0.toDBString().
                         compareTo(matrix_cp0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toDBString(): %s\n",
                                matrix_cp0.toDBString());
                    }

                    if ( matrix_cp1.toDBString().
                         compareTo(matrix_cp1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toDBString(): %s\n",
                                matrix_cp1.toDBString());
                    }

                    if ( matrix_cp2.toDBString().
                         compareTo(matrix_cp2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toDBString(): %s\n",
                                matrix_cp2.toDBString());
                    }

                    if ( nominal_cp.toDBString().
                         compareTo(nominal_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toDBString(): %s\n",
                                nominal_cp.toDBString());
                    }

                    if ( pred_cp.toDBString().
                         compareTo(pred_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toDBString(): %s\n",
                                pred_cp.toDBString());
                    }

                    if ( text_cp.toDBString().
                         compareTo(text_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toDBString(): %s\n",
                                text_cp.toDBString());
                    }
                }
            }

            /* Now repeat the above test, only without setting the fargIDs
             * on the entries in the argument list passed to the constructor.
             */
            float_cp_arg_list1 = null;
            float_cp = null;
            int_cp_arg_list1 = null;
            int_cp = null;
            matrix_cp0_arg_list1 = null;
            matrix_cp0 = null;
            matrix_cp1_arg_list1 = null;
            matrix_cp1 = null;
            matrix_cp2_arg_list1 = null;
            matrix_cp2 = null;
            nominal_cp_arg_list1 = null;
            nominal_cp = null;
            pred_cp_arg_list1 = null;
            pred_cp = null;
            text_cp_arg_list1 = null;
            text_cp = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                float_cp_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(11);
                float_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list1.add(arg);
                arg = new FloatDataValue(db);
                ((FloatDataValue)arg).setItsValue(11.0);
                float_cp_arg_list1.add(arg);
                float_cp = new ColPred(db, float_mve_ID,
                                       float_cp_arg_list1);


                int_cp_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(22);
                int_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 22));
                int_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 22 * db.getTicks()));
                int_cp_arg_list1.add(arg);
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(22);
                int_cp_arg_list1.add(arg);
                int_cp = new ColPred(db, int_mve_ID, int_cp_arg_list1);


                matrix_cp0_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(33);
                matrix_cp0_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list1.add(arg);
                arg = new FloatDataValue(db);
                ((FloatDataValue)arg).setItsValue(1.0);
                matrix_cp0_arg_list1.add(arg);
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(2);
                matrix_cp0_arg_list1.add(arg);
                arg = new NominalDataValue(db);
                ((NominalDataValue)arg).setItsValue("a_nominal");
                matrix_cp0_arg_list1.add(arg);
                arg = new PredDataValue(db);
                ((PredDataValue)arg).setItsValue(new Predicate(db, pve0_ID));
                matrix_cp0_arg_list1.add(arg);
                arg = new QuoteStringDataValue(db);
                ((QuoteStringDataValue)arg).setItsValue("q-string");
                matrix_cp0_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list1.add(arg);
                arg = new UndefinedDataValue(db);
                ((UndefinedDataValue)arg).setItsValue(
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_cp0_arg_list1.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list1);


                matrix_cp1_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(34);
                matrix_cp1_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 34));
                matrix_cp1_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 34 * db.getTicks()));
                matrix_cp1_arg_list1.add(arg);
                arg = new QuoteStringDataValue(db);
                ((QuoteStringDataValue)arg).setItsValue(" a q string ");
                matrix_cp1_arg_list1.add(arg);
                arg = new UndefinedDataValue(db);
                ((UndefinedDataValue)arg).setItsValue(
                                     matrix_mve1.getFormalArg(1).getFargName());
                matrix_cp1_arg_list1.add(arg);
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(88);
                matrix_cp1_arg_list1.add(arg);
                matrix_cp1 = new ColPred(db, matrix_mve1_ID,
                                         matrix_cp1_arg_list1);


                matrix_cp2_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(35);
                matrix_cp2_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 35));
                matrix_cp2_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 35 * db.getTicks()));
                matrix_cp2_arg_list1.add(arg);
                arg = new UndefinedDataValue(db);
                ((UndefinedDataValue)arg).setItsValue(
                                     matrix_mve2.getFormalArg(0).getFargName());
                matrix_cp2_arg_list1.add(arg);
                matrix_cp2 = new ColPred(db, matrix_mve2_ID,
                                         matrix_cp2_arg_list1);


                nominal_cp_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(44);
                nominal_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 44));
                nominal_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 44 * db.getTicks()));
                nominal_cp_arg_list1.add(arg);
                arg = new NominalDataValue(db);
                ((NominalDataValue)arg).setItsValue("another_nominal");
                nominal_cp_arg_list1.add(arg);
                nominal_cp = new ColPred(db, nominal_mve_ID,
                                         nominal_cp_arg_list1);


                pred_cp_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(55);
                pred_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 55));
                pred_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 55 * db.getTicks()));
                pred_cp_arg_list1.add(arg);
                arg = new PredDataValue(db);
                ((PredDataValue)arg).setItsValue(new Predicate(db, pve0_ID));
                pred_cp_arg_list1.add(arg);
                pred_cp = new ColPred(db, pred_mve_ID, pred_cp_arg_list1);


                text_cp_arg_list1 = new Vector<DataValue>();
                arg = new IntDataValue(db);
                ((IntDataValue)arg).setItsValue(66);
                text_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 66));
                text_cp_arg_list1.add(arg);
                arg = new TimeStampDataValue(db);
                ((TimeStampDataValue)arg).setItsValue(
                        new TimeStamp(db.getTicks(), 66 * db.getTicks()));
                text_cp_arg_list1.add(arg);
                arg = new TextStringDataValue(db);
                ((TextStringDataValue)arg).setItsValue("a text string");
                text_cp_arg_list1.add(arg);
                text_cp = new ColPred(db, text_mve_ID, text_cp_arg_list1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list1 == null ) ||
                 ( float_cp == null ) ||
                 ( int_cp_arg_list1 == null ) ||
                 ( int_cp == null ) ||
                 ( matrix_cp0_arg_list1 == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1_arg_list1 == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2_arg_list1 == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp_arg_list1 == null ) ||
                 ( nominal_cp == null ) ||
                 ( pred_cp_arg_list1 == null ) ||
                 ( pred_cp == null ) ||
                 ( text_cp_arg_list1 == null ) ||
                 ( text_cp == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp_arg_list1 == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed(2).\n");
                    }

                    if ( float_cp == null )
                    {
                        outStream.printf(
                                "allocation of float_cp failed(2).\n");
                    }

                    if ( int_cp_arg_list1 == null )
                    {
                        outStream.printf(
                            "allocation of int_cp_arg_list failed(2).\n");
                    }

                    if ( int_cp == null )
                    {
                        outStream.printf(
                                "allocation of int_cp failed(2).\n");
                    }

                    if ( matrix_cp0_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "matrix_cp0_arg_list failed(2).\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed(2).\n");
                    }

                    if ( matrix_cp1_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "matrix_cp1_arg_list failed(2).\n");
                    }

                    if ( matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp1 failed(2).\n");
                    }

                    if ( matrix_cp2_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "matrix_cp2_arg_list failed(2).\n");
                    }

                    if ( matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp2 failed(2).\n");
                    }

                    if ( nominal_cp_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "nominal_cp_arg_list failed(2).\n");
                    }

                    if ( nominal_cp == null )
                    {
                        outStream.printf(
                                "allocation of nominal_cp failed(2).\n");
                    }

                    if ( pred_cp_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "pred_cp_arg_list failed(2).\n");
                    }

                    if ( pred_cp == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp failed(2).\n");
                    }

                    if ( text_cp_arg_list1 == null )
                    {
                        outStream.printf("allocation of " +
                                         "text_cp_arg_list failed(2).\n");
                    }

                    if ( text_cp == null )
                    {
                        outStream.printf(
                                "allocation of text_cp failed(2).\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("Creation of test col preds " +
                                        "failed to complete(2).");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("col pred creation threw a " +
                                "SystemErrorException(2): %s.\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_cp.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( int_cp.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( matrix_cp0.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp1.toString().
                        compareTo(matrix_cp1_string) != 0 ) ||
                      ( matrix_cp2.toString().
                        compareTo(matrix_cp2_string) != 0 ) ||
                      ( nominal_cp.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( pred_cp.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( text_cp.toString().
                        compareTo(text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toString().
                         compareTo(float_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toString()(2): %s\n",
                                float_cp.toString());
                    }

                    if ( int_cp.toString().
                         compareTo(int_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toString()(2): %s\n",
                                int_cp.toString());
                    }

                    if ( matrix_cp0.toString().
                         compareTo(matrix_cp0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toString()(2): %s\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp1.toString().
                         compareTo(matrix_cp1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toString()(2): %s\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().
                         compareTo(matrix_cp2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toString()(2): %s\n",
                                matrix_cp2.toString());
                    }

                    if ( nominal_cp.toString().
                         compareTo(nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toString()(2): %s\n",
                                nominal_cp.toString());
                    }

                    if ( pred_cp.toString().
                         compareTo(pred_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toString()(2): %s\n",
                                pred_cp.toString());
                    }

                    if ( text_cp.toString().
                         compareTo(text_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toString()(2): %s\n",
                                text_cp.toString());
                    }
                }
            }
            else if ( ( float_cp.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( int_cp.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( matrix_cp0.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp1.toDBString().
                        compareTo(matrix_cp1_DBstring) != 0 ) ||
                      ( matrix_cp2.toDBString().
                        compareTo(matrix_cp2_DBstring) != 0 ) ||
                      ( nominal_cp.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( pred_cp.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( text_cp.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toDBString().
                         compareTo(float_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toDBString()(2): %s\n",
                                float_cp.toDBString());
                    }

                    if ( int_cp.toDBString().
                         compareTo(int_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toDBString()(2): %s\n",
                                int_cp.toDBString());
                    }

                    if ( matrix_cp0.toDBString().
                         compareTo(matrix_cp0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toDBString()(2): %s\n",
                                matrix_cp0.toDBString());
                    }

                    if ( matrix_cp1.toDBString().
                         compareTo(matrix_cp1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toDBString()(2): %s\n",
                                matrix_cp1.toDBString());
                    }

                    if ( matrix_cp2.toDBString().
                         compareTo(matrix_cp2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toDBString()(2): %s\n",
                                matrix_cp2.toDBString());
                    }

                    if ( nominal_cp.toDBString().
                         compareTo(nominal_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toDBString()(2): %s\n",
                                nominal_cp.toDBString());
                    }

                    if ( pred_cp.toDBString().
                         compareTo(pred_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toDBString()(2): %s\n",
                                pred_cp.toDBString());
                    }

                    if ( text_cp.toDBString().
                         compareTo(text_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toDBString()(2): %s\n",
                                text_cp.toDBString());
                    }
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db */
        failures += Verify3ArgConstructorFailure(null,
                                                 float_mve_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "null",
                                                 "float_mve_ID",
                                                 "float_cp_arg_list");

        /* now verify that the constructor fails when passed an invalid
         * predicate vocab element ID.
         */
        failures += Verify3ArgConstructorFailure(new ODBCDatabase(),
                                                 float_mve_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "new ODBCDatabase()",
                                                 "float_mve_ID",
                                                 "float_cp_arg_list");



        // finally, verify that the constructor fails when passed an invalid
        // arg list.  Many possibilities...
        //
        // In the following, we do the obvious and try to construct instances
        // of all the mve's defined above, but using all the wrong arg lists.
        // All these attempts should fail when the farg ID mis-matches are
        // detected.
        //
        // In theory, there is also the possiblility of a type mis-match
        // between the formal argument and a datavalue in the argument list.
        // However, the datavalues should throw a system error if a datavalue
        // is created for a formal argument that doesn't match the type of that
        // formarl argument.
        //
        // Even with this, one could suppose that an datavalue was created,
        // and then the type of the formal argument was changed out from under
        // it.  However, in this case, we should be assigning a new ID to the
        // formal argument, causing a farg ID mismatch failure.
        //
        // Assuming we do our part in the rest of the library, the following
        // tests should be sufficient.
        //
        // Start with a float mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 float_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "float_mve_ID",
                                                 "quote_string_arg_list");

        // Now choose an int mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 int_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "int_mve_ID",
                                                 "quote_string_arg_list");

        // Now choose a 7 argument matrix mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve0_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve0_ID",
                                                 "quote_string_arg_list");


        // Now choose a 3 argument matrix mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve1_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve1_ID",
                                                 "quote_string_arg_list");


        // Now choose a 1 argument matrix mve as the target.  Since its only
        // argument is untyped, one would expect few possible failures.
        // However in this case, the farg IDs don't match, so we get the
        // usual failures.
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 matrix_mve2_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "matrix_mve2_ID",
                                                 "quote_string_arg_list");


        // Now choose a nominal mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 nominal_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "nominal_mve_ID",
                                                 "quote_string_arg_list");


        // Now choose a predicate mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 text_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "text_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 pred_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "pred_mve_ID",
                                                 "quote_string_arg_list");


        // Now choose a text mve as the target:
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 null,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "null");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 empty_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "empty_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 float_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "float_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 int_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "int_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 matrix_cp0_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "matrix_cp0_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 matrix_cp1_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "matrix_cp1_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 matrix_cp2_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "matrix_cp2_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 nominal_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "nominal_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 pred_cp_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "pred_cp_arg_list");
        failures += Verify3ArgConstructorFailure(db,
                                                 text_mve_ID,
                                                 quote_string_arg_list,
                                                 outStream,
                                                 verbose,
                                                 "db",
                                                 "text_mve_ID",
                                                 "quote_string_arg_list");

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

    } /* ColPred::Test3ArgConstructor() */


    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors for this class.
     *
     *                                      JRM -- 10/01/08
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
            "Testing class ColPred accessors                                  ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        Database db = null;
        long fargID;
        long pve0_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        DataValue arg = null;
        DataValue qstring_arg = null;
        Vector<DataValue> empty_arg_list = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> int_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        Vector<DataValue> matrix_cp1_arg_list = null;
        Vector<DataValue> matrix_cp2_arg_list = null;
        Vector<DataValue> nominal_cp_arg_list = null;
        Vector<DataValue> pred_cp_arg_list = null;
        Vector<DataValue> text_cp_arg_list = null;
        Vector<DataValue> quote_string_arg_list = null;
        ColPred float_cp = null;
        ColPred int_cp = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp2 = null;
        ColPred nominal_cp = null;
        ColPred pred_cp = null;
        ColPred text_cp = null;
        ColPred cp0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 7 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 7 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }


        // having set up a selection of test mve's, now allocate some
        // matricies.  Use toString and toDBString to verify that they are
        // initialized correctly.

        if ( failures == 0 )
        {
            String float_cp_string =
                    "float_mve(11, 00:00:00:011, 00:00:11:000, 11.0)";
            String int_cp_string =
                    "int_mve(22, 00:00:00:022, 00:00:22:000, 22)";
            String matrix_cp0_string =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, " +
                                "a_nominal, pve0(<arg1>, <arg2>), " +
                                "\"q-string\", 00:00:01:000, <untyped>)";
            String matrix_cp1_string =
                    "matrix_mve1(34, 00:00:00:034, 00:00:34:000, " +
                                "\" a q string \", <arg2>, 88)";
            String matrix_cp2_string =
                    "matrix_mve2(35, 00:00:00:035, 00:00:35:000, <arg1>)";
            String nominal_cp_string =
                    "nominal_mve(44, 00:00:00:044, 00:00:44:000, " +
                                "another_nominal)";
            String pred_cp_string =
                    "pred_mve(55, 00:00:00:055, 00:00:55:000, " +
                            "pve0(<arg1>, <arg2>))";
            String text_cp_string =
                    "text_mve(66, 00:00:01:006, 00:01:06:000, a text string)";
            String float_cp_DBstring =
                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType INTEGER) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType UNTYPED) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(Matrix (mveID 4) " +
//                            "(varLen false) " +
//                            "(argList ((FloatDataValue (id 0) " +
//                                                      "(itsFargID 5) " +
//                                                      "(itsFargType FLOAT) " +
//                                                      "(itsCellID 0) " +
//                                                      "(itsValue 11.0) " +
//                                                      "(subRange false) " +
//                                                      "(minVal 0.0) " +
//                                                      "(maxVal 0.0))))))";
            String int_cp_DBstring =
                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType UNTYPED) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 10) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((IntDataValue (id 0) " +
//                                    "(itsFargID 11) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 22) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0))))))";
            String matrix_cp0_DBstring =
                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType INTEGER) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType UNTYPED) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(Matrix (mveID 16) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((FloatDataValue (id 0) " +
//                                    "(itsFargID 17) " +
//                                    "(itsFargType FLOAT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 1.0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0.0) " +
//                                    "(maxVal 0.0)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 18) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 2) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0)), " +
//                                "(NominalDataValue (id 0) " +
//                                    "(itsFargID 19) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a_nominal) " +
//                                    "(subRange false)), " +
//                                "(PredDataValue (id 0) " +
//                                    "(itsFargID 20) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg1>) " +
//                                                    "(subRange false)), " +
//                                                "(UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 3) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg2>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false)), " +
//                                "(QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 21) " +
//                                    "(itsFargType QUOTE_STRING) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue q-string) " +
//                                    "(subRange false)), " +
//                                "(TimeStampDataValue (id 0) " +
//                                    "(itsFargID 22) " +
//                                    "(itsFargType TIME_STAMP) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue (60,00:00:01:000)) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 23) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <untyped>) " +
//                                    "(subRange false))))))";
            String matrix_cp1_DBstring =
                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType INTEGER) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType UNTYPED) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 34) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((QuoteStringDataValue (id 0) " +
//                                "(itsFargID 35) " +
//                                "(itsFargType UNTYPED) " +
//                                "(itsCellID 0) " +
//                                "(itsValue  a q string ) " +
//                                "(subRange false)), " +
//                            "(UndefinedDataValue (id 0) " +
//                                "(itsFargID 36) " +
//                                "(itsFargType UNTYPED) " +
//                                "(itsCellID 0) " +
//                                "(itsValue <arg2>) " +
//                                "(subRange false)), " +
//                            "(IntDataValue (id 0) " +
//                                "(itsFargID 37) " +
//                                "(itsFargType UNTYPED) " +
//                                "(itsCellID 0) " +
//                                "(itsValue 88) " +
//                                "(subRange false) " +
//                                "(minVal 0) " +
//                                "(maxVal 0))))))";
            String matrix_cp2_DBstring =
                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType INTEGER) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType UNTYPED) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(Matrix (mveID 44) " +
//                            "(varLen true) " +
//                            "(argList " +
//                                "((UndefinedDataValue (id 0) " +
//                                    "(itsFargID 45) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg1>) " +
//                                    "(subRange false))))))";
            String nominal_cp_DBstring =
                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType INTEGER) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType UNTYPED) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(Matrix (mveID 50) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((NominalDataValue (id 0) " +
//                                    "(itsFargID 51) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue another_nominal) " +
//                                    "(subRange false))))))";
            String pred_cp_DBstring =
                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType INTEGER) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType UNTYPED) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false))))))";
//                    "(Matrix (mveID 56) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((PredDataValue (id 0) " +
//                                    "(itsFargID 57) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg1>) " +
//                                                    "(subRange false)), " +
//                                                "(UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 3) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg2>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false))))))";
            String text_cp_DBstring =
                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType INTEGER) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType UNTYPED) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(Matrix (mveID 62) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((TextStringDataValue (id 0) " +
//                                    "(itsFargID 63) " +
//                                    "(itsFargType TEXT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a text string) " +
//                                    "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                empty_arg_list = new Vector<DataValue>();


                float_cp_arg_list = new Vector<DataValue>();
                fargID = float_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 11);
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_cp_arg_list.add(arg);
                float_cp = new ColPred(db, float_mve_ID, float_cp_arg_list);


                int_cp_arg_list = new Vector<DataValue>();
                fargID = int_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22 * db.getTicks()));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(3).getID();
                 arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                int_cp = new ColPred(db, int_mve_ID, int_cp_arg_list);


                matrix_cp0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 33);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                qstring_arg = arg; // save to construct quote_string_arg_list
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getCPFormalArg(9).getFargName());
                matrix_cp0_arg_list.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);


                matrix_cp1_arg_list = new Vector<DataValue>();
                fargID = matrix_mve1.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 34);
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34 * db.getTicks()));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(3).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(4).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getCPFormalArg(4).getFargName());
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_cp1_arg_list.add(arg);
                matrix_cp1 = new ColPred(db, matrix_mve1_ID,
                                         matrix_cp1_arg_list);


                matrix_cp2_arg_list = new Vector<DataValue>();
                fargID = matrix_mve2.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 35);
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35 * db.getTicks()));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(3).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve2.getCPFormalArg(3).getFargName());
                matrix_cp2_arg_list.add(arg);
                matrix_cp2 = new ColPred(db, matrix_mve2_ID,
                                         matrix_cp2_arg_list);


                nominal_cp_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 44);
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44 * db.getTicks()));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(3).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_cp_arg_list.add(arg);
                nominal_cp = new ColPred(db, nominal_mve_ID,
                                            nominal_cp_arg_list);


                pred_cp_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 55);
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55 * db.getTicks()));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_cp_arg_list.add(arg);
                pred_cp = new ColPred(db, pred_mve_ID, pred_cp_arg_list);


                text_cp_arg_list = new Vector<DataValue>();
                fargID = text_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 66);
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66 * db.getTicks()));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(3).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_cp_arg_list.add(arg);
                text_cp = new ColPred(db, text_mve_ID, text_cp_arg_list);

                quote_string_arg_list = new Vector<DataValue>();
                quote_string_arg_list.add(qstring_arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( empty_arg_list == null ) ||
                 ( float_cp_arg_list == null ) ||
                 ( float_cp == null ) ||
                 ( int_cp_arg_list == null ) ||
                 ( int_cp == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1_arg_list == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2_arg_list == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp_arg_list == null ) ||
                 ( nominal_cp == null ) ||
                 ( pred_cp_arg_list == null ) ||
                 ( pred_cp == null ) ||
                 ( text_cp_arg_list == null ) ||
                 ( text_cp == null ) ||
                 ( quote_string_arg_list == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( empty_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of empty_arg_list failed.\n");
                    }

                    if ( float_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed.\n");
                    }

                    if ( float_cp == null )
                    {
                        outStream.printf("allocation of float_cp failed.\n");
                    }

                    if ( int_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_cp_arg_list failed.\n");
                    }

                    if ( int_cp == null )
                    {
                        outStream.printf("allocation of int_cp failed.\n");
                    }

                    if ( matrix_cp0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp0_arg_list failed.\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed.\n");
                    }

                    if ( matrix_cp1_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp1_arg_list failed.\n");
                    }

                    if ( matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp1 failed.\n");
                    }

                    if ( matrix_cp2_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp2_arg_list failed.\n");
                    }

                    if ( matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp2 failed.\n");
                    }

                    if ( nominal_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_cp_arg_list failed.\n");
                    }

                    if ( nominal_cp == null )
                    {
                        outStream.printf(
                                "allocation of nominal_cp failed.\n");
                    }

                    if ( pred_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp_arg_list failed.\n");
                    }

                    if ( pred_cp == null )
                    {
                        outStream.printf("allocation of pred_cp failed.\n");
                    }

                    if ( text_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_cp_arg_list failed.\n");
                    }

                    if ( text_cp == null )
                    {
                        outStream.printf("allocation of text_cp failed.\n");
                    }

                    if ( quote_string_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of quote_string_arg_list failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "Creation of test matricies failed to complete");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_cp.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( int_cp.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( matrix_cp0.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp1.toString().
                        compareTo(matrix_cp1_string) != 0 ) ||
                      ( matrix_cp2.toString().
                        compareTo(matrix_cp2_string) != 0 ) ||
                      ( nominal_cp.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( pred_cp.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( text_cp.toString().
                        compareTo(text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toString().
                         compareTo(float_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toString(): %s\n",
                                float_cp.toString());
                    }

                    if ( int_cp.toString().
                         compareTo(int_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toString(): %s\n",
                                int_cp.toString());
                    }

                    if ( matrix_cp0.toString().
                         compareTo(matrix_cp0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toString(): %s\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp1.toString().
                         compareTo(matrix_cp1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toString(): %s\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().
                         compareTo(matrix_cp2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toString(): %s\n",
                                matrix_cp2.toString());
                    }

                    if ( nominal_cp.toString().
                         compareTo(nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toString(): %s\n",
                                nominal_cp.toString());
                    }

                    if ( pred_cp.toString().
                         compareTo(pred_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toString(): %s\n",
                                pred_cp.toString());
                    }

                    if ( text_cp.toString().
                         compareTo(text_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toString(): %s\n",
                                text_cp.toString());
                    }
                }
            }
            else if ( ( float_cp.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( int_cp.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( matrix_cp0.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp1.toDBString().
                        compareTo(matrix_cp1_DBstring) != 0 ) ||
                      ( matrix_cp2.toDBString().
                        compareTo(matrix_cp2_DBstring) != 0 ) ||
                      ( nominal_cp.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( pred_cp.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( text_cp.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toDBString().
                         compareTo(float_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toDBString(): %s\n",
                                float_cp.toDBString());
                    }

                    if ( int_cp.toDBString().
                         compareTo(int_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toDBString(): %s\n",
                                int_cp.toDBString());
                    }

                    if ( matrix_cp0.toDBString().
                         compareTo(matrix_cp0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toDBString(): %s\n",
                                matrix_cp0.toDBString());
                    }

                    if ( matrix_cp1.toDBString().
                         compareTo(matrix_cp1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toDBString(): %s\n",
                                matrix_cp1.toDBString());
                    }

                    if ( matrix_cp2.toDBString().
                         compareTo(matrix_cp2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toDBString(): %s\n",
                                matrix_cp2.toDBString());
                    }

                    if ( nominal_cp.toDBString().
                         compareTo(nominal_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toDBString(): %s\n",
                                nominal_cp.toDBString());
                    }

                    if ( pred_cp.toDBString().
                         compareTo(pred_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toDBString(): %s\n",
                                pred_cp.toDBString());
                    }

                    if ( text_cp.toDBString().
                         compareTo(text_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toDBString(): %s\n",
                                text_cp.toDBString());
                    }
                }
            }
        }


        // Verify that getDB() works as expected.  There is not much to
        // do here, as the db field is set on creation and never changed.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            if ( ( float_cp.getDB() != db ) ||
                 ( int_cp.getDB() != db ) ||
                 ( matrix_cp0.getDB() != db ) ||
                 ( matrix_cp1.getDB() != db ) ||
                 ( matrix_cp2.getDB() != db ) ||
                 ( nominal_cp.getDB() != db ) ||
                 ( pred_cp.getDB() != db ) ||
                 ( text_cp.getDB() != db ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                            "*_cp.getDB() returned an unexpected value.\n");
                }
            }
        }


        // Verify that getMveID() works as expected.  There is not much to
        // do here either, as the mveID field is set on creation and never
        // changed.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            if ( ( float_cp.getMveID() != float_mve_ID ) ||
                 ( int_cp.getMveID() != int_mve_ID ) ||
                 ( matrix_cp0.getMveID() != matrix_mve0_ID ) ||
                 ( matrix_cp1.getMveID() != matrix_mve1_ID ) ||
                 ( matrix_cp2.getMveID() != matrix_mve2_ID ) ||
                 ( nominal_cp.getMveID() != nominal_mve_ID ) ||
                 ( pred_cp.getMveID() != pred_mve_ID ) ||
                 ( text_cp.getMveID() != text_mve_ID ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                        "*_cp.getMveID() returned an unexpected value.\n");
                }
            }
        }


        // Verify that getNumArgs() works as expected.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            if ( ( float_cp.getNumArgs() != 4 ) ||
                 ( int_cp.getNumArgs() != 4 ) ||
                 ( matrix_cp0.getNumArgs() != 10 ) ||
                 ( matrix_cp1.getNumArgs() != 6 ) ||
                 ( matrix_cp2.getNumArgs() != 4 ) ||
                 ( nominal_cp.getNumArgs() != 4 ) ||
                 ( pred_cp.getNumArgs() != 4 ) ||
                 ( text_cp.getNumArgs() != 4 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                        "*_cp.getNumArgs() returned an unexpected value.\n");
                }
            }
        }


        // Verify that getVarLen() works as expected.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            if ( ( float_cp.getVarLen() != false ) ||
                 ( int_cp.getVarLen() != false ) ||
                 ( matrix_cp0.getVarLen() != false ) ||
                 ( matrix_cp1.getVarLen() != false ) ||
                 ( matrix_cp2.getVarLen() != true ) ||
                 ( nominal_cp.getVarLen() != false ) ||
                 ( pred_cp.getVarLen() != false ) ||
                 ( text_cp.getVarLen() != false ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print(
                        "*_cp.getNumArgs() returned an unexpected value.\n");
                }
            }
        }


        // finally, verify that lookupMatrixVE() throws a system error on
        // invalid input.  Start with the valid id that does not refer to a
        // matrix vocab element

        threwSystemErrorException = false;
        completed = false;
        fargID = DBIndex.INVALID_ID;
        mve = null;

        if ( failures == 0 )
        {
            try
            {
                fargID = pve0.getFormalArg(0).getID();

                mve = float_cp.lookupMatrixVE(fargID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( fargID == DBIndex.INVALID_ID ) ||
                 ( mve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( fargID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("fargID == DBIndex.INVALID_ID (2).\n");
                    }

                    if ( mve != null )
                    {
                        outStream.printf("mve != null (1)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                            "float_cp.lookupMatrixVE(fargID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf(
                                "float_cp.lookupPredicateVE(fargID) " +
                                "failed to thow a system error.\n");
                    }
                }
            }
        }

        // now try an unused ID
        mve = null;
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                mve = int_cp.lookupMatrixVE(500);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( mve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( mve != null )
                    {
                        outStream.printf("mve != null (2)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "int_cp.lookupMatrixVE(500) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("int_cp.lookupMatrixVE(500) " +
                                         "failed to thow a system error.\n");
                    }
                }
            }
        }

        // finally, try the invalid ID
        mve = null;
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                mve = matrix_cp0.lookupMatrixVE(DBIndex.INVALID_ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( mve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( mve != null )
                    {
                        outStream.printf("mve != null (3)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("matrix_cp0.lookupMatrixVE" +
                                         "(DBIndex.INVALID_ID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("matrix_cp0.lookupMatrixVE" +
                                         "(DBIndex.INVALID_ID) " +
                                         "failed to thow a system error.\n");
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

    } /* ColPred::TestAccessors() */



    /**
     * TestArgListManagement()
     *
     * Run a battery of tests on the arg list management facilities.
     *
     *                                              JRM -- 10/01/08
     *
     * Changes:
     *
     *    - None.
     */

    private static boolean TestArgListManagement(java.io.PrintStream outStream,
                                                 boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing class ColPred argument list management                   ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long fargID;
        long pve0_ID = DBIndex.INVALID_ID;
        long pve1_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long matrix_mve3_ID = DBIndex.INVALID_ID;
        long matrix_mve4_ID = DBIndex.INVALID_ID;
        long matrix_mve5_ID = DBIndex.INVALID_ID;
        long matrix_mve6_ID = DBIndex.INVALID_ID;
        long matrix_mve7_ID = DBIndex.INVALID_ID;
        long matrix_mve8_ID = DBIndex.INVALID_ID;
        long matrix_mve9_ID = DBIndex.INVALID_ID;
        long matrix_mve10_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement matrix_mve3 = null;
        MatrixVocabElement matrix_mve4 = null;
        MatrixVocabElement matrix_mve5 = null;
        MatrixVocabElement matrix_mve6 = null;
        MatrixVocabElement matrix_mve7 = null;
        MatrixVocabElement matrix_mve8 = null;
        MatrixVocabElement matrix_mve9 = null;
        MatrixVocabElement matrix_mve10 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        DataValue arg = null;
        DataValue goodArg = null;
        DataValue badArg = null;
        FloatDataValue floatArg = null;
        IntDataValue intArg = null;
        NominalDataValue nomArg = null;
        PredDataValue predArg = null;
        QuoteStringDataValue qsArg = null;
        TextStringDataValue textArg = null;
        TimeStampDataValue tsArg = null;
        UndefinedDataValue undefArg = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> int_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        Vector<DataValue> matrix_cp16_arg_list = null;
        Vector<DataValue> matrix_cp17_arg_list = null;
        Vector<DataValue> nominal_cp_arg_list = null;
        Vector<DataValue> pred_cp_arg_list = null;
        Vector<DataValue> text_cp_arg_list = null;
        ColPred float_cp0 = null;
        ColPred float_cp1 = null;
        ColPred int_cp0 = null;
        ColPred int_cp1 = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp2 = null;
        ColPred matrix_cp3 = null;
        ColPred matrix_cp4 = null;
        ColPred matrix_cp5 = null;
        ColPred matrix_cp6 = null;
        ColPred matrix_cp7 = null;
        ColPred matrix_cp8 = null;
        ColPred matrix_cp9 = null;
        ColPred matrix_cp10 = null;
        ColPred matrix_cp11 = null;
        ColPred matrix_cp12 = null;
        ColPred matrix_cp13 = null;
        ColPred matrix_cp14 = null;
        ColPred matrix_cp15 = null;
        ColPred matrix_cp16 = null;
        ColPred matrix_cp17 = null;
        ColPred nominal_cp0 = null;
        ColPred nominal_cp1 = null;
        ColPred pred_cp0 = null;
        ColPred pred_cp1 = null;
        ColPred text_cp0 = null;
        ColPred text_cp1 = null;
        ColPred m0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);
            pve1_ID = db.addPredVE(pve1);
            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve1.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve1.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve1.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve1.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve1.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve1.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve2.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve2.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve2.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve2.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve2.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve2.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve2.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve2.appendFormalArg(farg);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            matrix_mve3 = new MatrixVocabElement(db, "matrix_mve3");
            matrix_mve3.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve3.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve3.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve3.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve3.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve3.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve3.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve3.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve3.appendFormalArg(farg);
            db.vl.addElement(matrix_mve3);
            matrix_mve3_ID = matrix_mve3.getID();

            matrix_mve4 = new MatrixVocabElement(db, "matrix_mve4");
            matrix_mve4.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve4.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve4.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve4.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve4.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve4.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve4.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve4.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve4.appendFormalArg(farg);
            db.vl.addElement(matrix_mve4);
            matrix_mve4_ID = matrix_mve4.getID();

            matrix_mve5 = new MatrixVocabElement(db, "matrix_mve5");
            matrix_mve5.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve5.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve5.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve5.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve5.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve5.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve5.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve5.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve5.appendFormalArg(farg);
            db.vl.addElement(matrix_mve5);
            matrix_mve5_ID = matrix_mve5.getID();

            matrix_mve6 = new MatrixVocabElement(db, "matrix_mve6");
            matrix_mve6.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve6.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve6.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve6.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve6.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve6.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve6.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve6.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve6.appendFormalArg(farg);
            db.vl.addElement(matrix_mve6);
            matrix_mve6_ID = matrix_mve6.getID();

            matrix_mve7 = new MatrixVocabElement(db, "matrix_mve7");
            matrix_mve7.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve7.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve7.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve7.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve7.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve7.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve7.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve7.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve7.appendFormalArg(farg);
            db.vl.addElement(matrix_mve7);
            matrix_mve7_ID = matrix_mve7.getID();

            matrix_mve8 = new MatrixVocabElement(db, "matrix_mve8");
            matrix_mve8.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve8.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve8.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve8.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve8.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve8.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve8.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve8.appendFormalArg(farg);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve8.appendFormalArg(farg);
            db.vl.addElement(matrix_mve8);
            matrix_mve8_ID = matrix_mve8.getID();


            matrix_mve9 = new MatrixVocabElement(db, "matrix_mve9");
            matrix_mve9.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve9.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve9.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve9.appendFormalArg(farg);
            db.vl.addElement(matrix_mve9);
            matrix_mve9_ID = matrix_mve9.getID();

            matrix_mve10 = new MatrixVocabElement(db, "matrix_mve10");
            matrix_mve10.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve10.appendFormalArg(farg);
            matrix_mve10.setVarLen(true);
            db.vl.addElement(matrix_mve10);
            matrix_mve10_ID = matrix_mve10.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( pve1 == null ) ||
             ( pve1_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 8 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 8 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 8 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve3 == null ) ||
             ( matrix_mve3.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve3.getNumFormalArgs() != 8 ) ||
             ( matrix_mve3_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve4 == null ) ||
             ( matrix_mve4.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve4.getNumFormalArgs() != 8 ) ||
             ( matrix_mve4_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve5 == null ) ||
             ( matrix_mve5.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve5.getNumFormalArgs() != 8 ) ||
             ( matrix_mve5_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve6 == null ) ||
             ( matrix_mve6.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve6.getNumFormalArgs() != 8 ) ||
             ( matrix_mve6_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve7 == null ) ||
             ( matrix_mve7.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve7.getNumFormalArgs() != 8 ) ||
             ( matrix_mve7_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve8 == null ) ||
             ( matrix_mve8.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve8.getNumFormalArgs() != 8 ) ||
             ( matrix_mve8_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve9 == null ) ||
             ( matrix_mve9.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve9.getNumFormalArgs() != 3 ) ||
             ( matrix_mve9_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve10 == null ) ||
             ( matrix_mve10.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve10.getNumFormalArgs() != 1 ) ||
             ( matrix_mve10_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve2.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve2.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2_ID == INVALID_ID.\n");
                }


                if ( matrix_mve3 == null )
                {
                    outStream.print("creation of matrix_mve3 failed.\n");
                }
                else if ( matrix_mve3.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve3.getType().\n");
                }
                else if ( matrix_mve3.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve3.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve3.getNumFormalArgs());
                }

                if ( matrix_mve3_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve3_ID == INVALID_ID.\n");
                }


                if ( matrix_mve4 == null )
                {
                    outStream.print("creation of matrix_mve4 failed.\n");
                }
                else if ( matrix_mve4.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve4.getType().\n");
                }
                else if ( matrix_mve4.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve4.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve4.getNumFormalArgs());
                }

                if ( matrix_mve4_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve4_ID == INVALID_ID.\n");
                }


                if ( matrix_mve5 == null )
                {
                    outStream.print("creation of matrix_mve5 failed.\n");
                }
                else if ( matrix_mve5.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve5.getType().\n");
                }
                else if ( matrix_mve5.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve5.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve5.getNumFormalArgs());
                }

                if ( matrix_mve5_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve5_ID == INVALID_ID.\n");
                }


                if ( matrix_mve6 == null )
                {
                    outStream.print("creation of matrix_mve6 failed.\n");
                }
                else if ( matrix_mve6.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve6.getType().\n");
                }
                else if ( matrix_mve6.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve6.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve6.getNumFormalArgs());
                }

                if ( matrix_mve6_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve6_ID == INVALID_ID.\n");
                }


                if ( matrix_mve7 == null )
                {
                    outStream.print("creation of matrix_mve7 failed.\n");
                }
                else if ( matrix_mve7.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve7.getType().\n");
                }
                else if ( matrix_mve7.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve7.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve7.getNumFormalArgs());
                }

                if ( matrix_mve7_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve7_ID == INVALID_ID.\n");
                }


                if ( matrix_mve8 == null )
                {
                    outStream.print("creation of matrix_mve8 failed.\n");
                }
                else if ( matrix_mve8.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve8.getType().\n");
                }
                else if ( matrix_mve8.getNumFormalArgs() != 8 )
                {
                    outStream.printf("matrix_mve8.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve8.getNumFormalArgs());
                }

                if ( matrix_mve8_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve8_ID == INVALID_ID.\n");
                }


                if ( matrix_mve9 == null )
                {
                    outStream.print("creation of matrix_mve9 failed.\n");
                }
                else if ( matrix_mve9.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve9.getType().\n");
                }
                else if ( matrix_mve9.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve9.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve9.getNumFormalArgs());
                }

                if ( matrix_mve9_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve9_ID == INVALID_ID.\n");
                }


                if ( matrix_mve10 == null )
                {
                    outStream.print("creation of matrix_mve10 failed.\n");
                }
                else if ( matrix_mve10.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve10.getType().\n");
                }
                else if ( matrix_mve10.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve10.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve10.getNumFormalArgs());
                }

                if ( matrix_mve10_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve10 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // Start with a set of tests to verify that an argument list is
        // converted properly when the mveID of an instance of ColPred
        // is changed.
        //
        // Start by creating the necessary set of test instances of ColPred.
        // In passing, also create instances of ColPred for later use.

        if ( failures == 0 )
        {
            String float_cp_string =
                    "float_mve(11, 00:00:00:011, 00:00:11:000, 11.0)";
            String int_cp_string =
                    "int_mve(22, 00:00:00:022, 00:00:22:000, 22)";
            String matrix_cp0_string =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, " +
                                "a_nominal, pve0(<arg>), \"q-string\", " +
                                "00:00:01:000, <untyped>, " +
                                "float_mve(0, 00:00:00:000, 00:00:00:000, 0.0))";
//                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, " +
//                                "a_nominal, pve0(<arg>), \"q-string\", " +
//                                "00:00:01:000, <untyped>, " +
//                                "float_mve(<ord>, <onset>, <offset>, 0.0))";
            String matrix_cp16_string =
                    "matrix_mve9(34, 00:00:00:034, 00:00:34:000, " +
                                "\" a q string \", <arg2>, 88)";
            String matrix_cp17_string =
                    "matrix_mve10(35, 00:00:00:035, 00:00:35:000, <arg1>)";
            String nominal_cp_string =
                    "nominal_mve(44, 00:00:00:044, 00:00:44:000, " +
                                "another_nominal)";
            String pred_cp_string =
                    "pred_mve(55, 00:00:00:055, 00:00:55:000, pve0(<arg>))";
            String text_cp_string =
                    "text_mve(66, 00:00:01:006, 00:01:06:000, a text string)";
            String float_cp_DBstring =
                    "(colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 8) (itsFargType INTEGER) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 9) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 9) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(Matrix (mveID 6) " +
//                            "(varLen false) " +
//                            "(argList ((FloatDataValue (id 0) " +
//                                                      "(itsFargID 7) " +
//                                                      "(itsFargType FLOAT) " +
//                                                      "(itsCellID 0) " +
//                                                      "(itsValue 11.0) " +
//                                                      "(subRange false) " +
//                                                      "(minVal 0.0) " +
//                                                      "(maxVal 0.0))))))";
            String int_cp_DBstring =
                    "(colPred (id 0) (mveID 12) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 14) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 15) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 16) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 17) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 12) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 14) (itsFargType UNTYPED) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 15) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 16) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 17) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 12) " +
//                            "(varLen false) " +
//                            "(argList ((IntDataValue (id 0) " +
//                                                    "(itsFargID 13) " +
//                                                    "(itsFargType INTEGER) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue 22) " +
//                                                    "(subRange false) " +
//                                                    "(minVal 0) " +
//                                                    "(maxVal 0))))))";
            String matrix_cp0_DBstring =
                    "(colPred (id 0) (mveID 18) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 27) (itsFargType INTEGER) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 28) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 29) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 30) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 31) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 32) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 33) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 34) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 35) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 36) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false)), (ColPredDataValue (id 0) (itsFargID 37) (itsFargType COL_PREDICATE) (itsCellID 0) (itsValue (colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 8) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 9) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 18) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 27) (itsFargType UNTYPED) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 28) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 29) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 30) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 31) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 32) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 33) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 34) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 35) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 36) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false)), (ColPredDataValue (id 0) (itsFargID 37) (itsFargType COL_PREDICATE) (itsCellID 0) (itsValue (colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 9) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 10) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 18) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 26) (itsFargType UNTYPED) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 27) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 28) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 29) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 30) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 31) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 32) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 33) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 34) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 35) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(Matrix (mveID 18) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((FloatDataValue (id 0) " +
//                                    "(itsFargID 19) " +
//                                    "(itsFargType FLOAT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 1.0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0.0) " +
//                                    "(maxVal 0.0)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 20) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 2) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0)), " +
//                                "(NominalDataValue (id 0) " +
//                                    "(itsFargID 21) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a_nominal) " +
//                                    "(subRange false)), " +
//                                "(PredDataValue (id 0) " +
//                                    "(itsFargID 22) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false)), " +
//                                "(QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 23) " +
//                                    "(itsFargType QUOTE_STRING) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue q-string) " +
//                                    "(subRange false)), " +
//                                "(TimeStampDataValue (id 0) " +
//                                    "(itsFargID 24) " +
//                                    "(itsFargType TIME_STAMP) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue (60,00:00:01:000)) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 25) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <untyped>) " +
//                                    "(subRange false))))))";
            String matrix_cp16_DBstring =
                    "(colPred (id 0) (mveID 198) (mveName matrix_mve9) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 202) (itsFargType INTEGER) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 203) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 204) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 205) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 206) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 207) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 198) (mveName matrix_mve9) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 202) (itsFargType UNTYPED) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 203) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 204) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 205) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 206) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 207) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 36) (mveName matrix_mve9) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 40) (itsFargType UNTYPED) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 44) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 45) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 36) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 37) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue  a q string ) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 38) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg2>) " +
//                                    "(subRange false)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 39) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 88) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0))))))";
            String matrix_cp17_DBstring =
                    "(colPred (id 0) (mveID 208) (mveName matrix_mve10) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 210) (itsFargType INTEGER) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 211) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 212) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 213) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 208) (mveName matrix_mve10) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 210) (itsFargType UNTYPED) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 211) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 212) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 213) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 46) (mveName matrix_mve10) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 48) (itsFargType UNTYPED) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 50) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 51) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(Matrix (mveID 46) " +
//                            "(varLen true) " +
//                            "(argList " +
//                                "((UndefinedDataValue (id 0) " +
//                                    "(itsFargID 47) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg1>) " +
//                                    "(subRange false))))))";
            String nominal_cp_DBstring =
                    "(colPred (id 0) (mveID 214) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 216) (itsFargType INTEGER) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 217) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 218) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 219) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(colPred (id 0) (mveID 214) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 216) (itsFargType UNTYPED) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 217) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 218) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 219) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(colPred (id 0) (mveID 52) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 54) (itsFargType UNTYPED) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 55) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 56) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 57) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(Matrix (mveID 52) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((NominalDataValue (id 0) " +
//                                    "(itsFargID 53) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue another_nominal) " +
//                                    "(subRange false))))))";
            String pred_cp_DBstring =
                    "(colPred (id 0) (mveID 220) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 222) (itsFargType INTEGER) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 223) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 224) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 225) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 220) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 222) (itsFargType UNTYPED) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 223) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 224) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 225) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 58) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 60) (itsFargType UNTYPED) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 61) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 62) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 63) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false))))))";
//                    "(Matrix (mveID 58) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((PredDataValue (id 0) " +
//                                    "(itsFargID 59) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false))))))";
            String text_cp_DBstring =
                "(colPred (id 0) (mveID 226) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 228) (itsFargType INTEGER) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 229) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 230) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 231) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                "(colPred (id 0) (mveID 226) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 228) (itsFargType UNTYPED) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 229) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 230) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 231) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                "(colPred (id 0) (mveID 64) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 66) (itsFargType UNTYPED) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 67) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 68) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 69) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(Matrix (mveID 64) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((TextStringDataValue (id 0) " +
//                                    "(itsFargID 65) " +
//                                    "(itsFargType TEXT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a text string) " +
//                                    "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                float_cp_arg_list = new Vector<DataValue>();
                fargID = float_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 11);
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_cp_arg_list.add(arg);
                float_cp0 = new ColPred(db, float_mve_ID, float_cp_arg_list);
                float_cp1 = new ColPred(db, float_mve_ID, float_cp_arg_list);


                int_cp_arg_list = new Vector<DataValue>();
                fargID = int_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22 * db.getTicks()));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(3).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                int_cp0 = new ColPred(db, int_mve_ID, int_cp_arg_list);
                int_cp1 = new ColPred(db, int_mve_ID, int_cp_arg_list);


                matrix_cp0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 33);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getFormalArg(6).getFargName());
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(10).getID();
                arg = new ColPredDataValue(db, fargID,
                                     new ColPred(db, float_mve_ID));
                matrix_cp0_arg_list.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp1 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp2 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp3 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp4 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp5 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp6 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp7 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp8 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp9 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);
                matrix_cp10 = new ColPred(db, matrix_mve0_ID,
                                          matrix_cp0_arg_list);
                matrix_cp11 = new ColPred(db, matrix_mve0_ID,
                                          matrix_cp0_arg_list);
                matrix_cp12 = new ColPred(db, matrix_mve0_ID,
                                          matrix_cp0_arg_list);
                matrix_cp13 = new ColPred(db, matrix_mve0_ID,
                                          matrix_cp0_arg_list);
                matrix_cp14 = new ColPred(db, matrix_mve0_ID,
                                          matrix_cp0_arg_list);
                matrix_cp15 = new ColPred(db, matrix_mve0_ID,
                                          matrix_cp0_arg_list);


                matrix_cp16_arg_list = new Vector<DataValue>();
                fargID = matrix_mve9.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 34);
                matrix_cp16_arg_list.add(arg);
                fargID = matrix_mve9.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34));
                matrix_cp16_arg_list.add(arg);
                fargID = matrix_mve9.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34 * db.getTicks()));
                matrix_cp16_arg_list.add(arg);
                fargID = matrix_mve9.getCPFormalArg(3).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_cp16_arg_list.add(arg);
                fargID = matrix_mve9.getCPFormalArg(4).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve9.getFormalArg(1).getFargName());
                matrix_cp16_arg_list.add(arg);
                fargID = matrix_mve9.getCPFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_cp16_arg_list.add(arg);
                matrix_cp16 = new ColPred(db, matrix_mve9_ID,
                                         matrix_cp16_arg_list);


                matrix_cp17_arg_list = new Vector<DataValue>();
                fargID = matrix_mve10.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 35);
                matrix_cp17_arg_list.add(arg);
                fargID = matrix_mve10.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35));
                matrix_cp17_arg_list.add(arg);
                fargID = matrix_mve10.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35 * db.getTicks()));
                matrix_cp17_arg_list.add(arg);
                fargID = matrix_mve10.getCPFormalArg(3).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve10.getFormalArg(0).getFargName());
                matrix_cp17_arg_list.add(arg);
                matrix_cp17 = new ColPred(db, matrix_mve10_ID,
                                          matrix_cp17_arg_list);


                nominal_cp_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 44);
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44 * db.getTicks()));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(3).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_cp_arg_list.add(arg);
                nominal_cp0 = new ColPred(db, nominal_mve_ID,
                                          nominal_cp_arg_list);
                nominal_cp1 = new ColPred(db, nominal_mve_ID,
                                          nominal_cp_arg_list);


                pred_cp_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 55);
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55 * db.getTicks()));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_cp_arg_list.add(arg);
                pred_cp0 = new ColPred(db, pred_mve_ID, pred_cp_arg_list);
                pred_cp1 = new ColPred(db, pred_mve_ID, pred_cp_arg_list);


                text_cp_arg_list = new Vector<DataValue>();
                fargID = text_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 66);
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66 * db.getTicks()));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(3).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_cp_arg_list.add(arg);
                text_cp0 = new ColPred(db, text_mve_ID, text_cp_arg_list);
                text_cp1 = new ColPred(db, text_mve_ID, text_cp_arg_list);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list == null ) ||
                 ( float_cp0 == null ) ||
                 ( float_cp1 == null ) ||
                 ( int_cp_arg_list == null ) ||
                 ( int_cp0 == null ) ||
                 ( int_cp1 == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( matrix_cp3 == null ) ||
                 ( matrix_cp4 == null ) ||
                 ( matrix_cp5 == null ) ||
                 ( matrix_cp6 == null ) ||
                 ( matrix_cp7 == null ) ||
                 ( matrix_cp8 == null ) ||
                 ( matrix_cp9 == null ) ||
                 ( matrix_cp10 == null ) ||
                 ( matrix_cp11 == null ) ||
                 ( matrix_cp12 == null ) ||
                 ( matrix_cp13 == null ) ||
                 ( matrix_cp14 == null ) ||
                 ( matrix_cp15 == null ) ||
                 ( matrix_cp16_arg_list == null ) ||
                 ( matrix_cp16 == null ) ||
                 ( matrix_cp17_arg_list == null ) ||
                 ( matrix_cp17 == null ) ||
                 ( nominal_cp_arg_list == null ) ||
                 ( nominal_cp0 == null ) ||
                 ( nominal_cp1 == null ) ||
                 ( pred_cp_arg_list == null ) ||
                 ( pred_cp0 == null ) ||
                 ( pred_cp1 == null ) ||
                 ( text_cp_arg_list == null ) ||
                 ( text_cp0 == null ) ||
                 ( text_cp1 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed.\n");
                    }

                    if ( ( float_cp0 == null ) || ( float_cp1 == null ) )
                    {
                        outStream.printf("allocation of float_cp? failed.\n");
                    }

                    if ( int_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_cp_arg_list failed.\n");
                    }

                    if ( ( int_cp0 == null ) || ( int_cp1 == null ) )
                    {
                        outStream.printf("allocation of int_cp? failed.\n");
                    }

                    if ( matrix_cp0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp0_arg_list failed.\n");
                    }

                    if ( ( matrix_cp0 == null ) ||
                         ( matrix_cp1 == null ) ||
                         ( matrix_cp2 == null ) ||
                         ( matrix_cp3 == null ) ||
                         ( matrix_cp4 == null ) ||
                         ( matrix_cp5 == null ) ||
                         ( matrix_cp6 == null ) ||
                         ( matrix_cp7 == null ) ||
                         ( matrix_cp8 == null ) ||
                         ( matrix_cp9 == null ) ||
                         ( matrix_cp10 == null ) ||
                         ( matrix_cp11 == null ) ||
                         ( matrix_cp12 == null ) ||
                         ( matrix_cp13 == null ) ||
                         ( matrix_cp14 == null ) ||
                         ( matrix_cp15 == null ) )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0-15 failed.\n");
                    }

                    if ( matrix_cp16_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp16_arg_list failed.\n");
                    }

                    if ( matrix_cp16 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp16 failed.\n");
                    }

                    if ( matrix_cp17_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp17_arg_list failed.\n");
                    }

                    if ( matrix_cp17 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp17 failed.\n");
                    }

                    if ( nominal_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_cp_arg_list failed.\n");
                    }

                    if ( ( nominal_cp0 == null ) || ( nominal_cp1 == null ) )
                    {
                        outStream.printf(
                                "allocation of nominal_cp failed.\n");
                    }

                    if ( pred_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp_arg_list failed.\n");
                    }

                    if ( ( pred_cp0 == null ) || ( pred_cp1 == null ) )
                    {
                        outStream.printf("allocation of pred_cp? failed.\n");
                    }

                    if ( text_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_cp_arg_list failed.\n");
                    }

                    if ( ( text_cp0 == null ) || ( text_cp1 == null ) )
                    {
                        outStream.printf("allocation of text_cp? failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                            "Creation of test matricies failed to complete\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_cp0.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( float_cp1.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( int_cp0.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( int_cp1.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( matrix_cp0.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp1.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp2.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp3.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp4.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp5.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp6.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp7.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp8.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp9.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp10.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp11.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp12.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp13.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp14.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp15.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp16.toString().
                        compareTo(matrix_cp16_string) != 0 ) ||
                      ( matrix_cp17.toString().
                        compareTo(matrix_cp17_string) != 0 ) ||
                      ( nominal_cp0.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( nominal_cp1.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( pred_cp0.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( pred_cp1.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( text_cp0.toString().
                        compareTo(text_cp_string) != 0 ) ||
                      ( text_cp1.toString().
                        compareTo(text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ( float_cp0.toString().
                           compareTo(float_cp_string) != 0 ) ||
                         ( float_cp1.toString().
                           compareTo(float_cp_string) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected float_cp?.toString(): %s\n",
                                float_cp0.toString());
                    }

                    if ( ( int_cp0.toString().
                           compareTo(int_cp_string) != 0 ) ||
                         ( int_cp1.toString().
                           compareTo(int_cp_string) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected int_cp?.toString(): %s\n",
                                int_cp0.toString());
                    }

                    if ( ( matrix_cp0.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp1.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp2.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp3.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp4.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp5.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp6.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp7.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp8.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp9.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp10.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp11.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp12.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp13.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp14.toString().
                            compareTo(matrix_cp0_string) != 0 ) ||
                         ( matrix_cp15.toString().
                            compareTo(matrix_cp0_string) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0-15.toString(): %s\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp16.toString().
                         compareTo(matrix_cp16_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp16.toString(): %s\n",
                                matrix_cp16.toString());
                    }

                    if ( matrix_cp17.toString().
                         compareTo(matrix_cp17_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp17.toString(): %s\n",
                                matrix_cp17.toString());
                    }

                    if ( ( nominal_cp0.toString().
                           compareTo(nominal_cp_string) != 0 ) ||
                         ( nominal_cp1.toString().
                           compareTo(nominal_cp_string) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected nominal_cp?.toString(): %s\n",
                                nominal_cp0.toString());
                    }

                    if ( ( pred_cp0.toString().
                           compareTo(pred_cp_string) != 0 ) ||
                         ( pred_cp1.toString().
                           compareTo(pred_cp_string) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected pred_cp?.toString(): %s\n",
                                pred_cp0.toString());
                    }

                    if ( ( text_cp0.toString().
                           compareTo(text_cp_string) != 0 ) ||
                         ( text_cp1.toString().
                           compareTo(text_cp_string) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected text_cp?.toString(): %s\n",
                                text_cp0.toString());
                    }
                }
            }
            else if ( ( float_cp0.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( float_cp1.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( int_cp0.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( int_cp1.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( matrix_cp0.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp1.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp2.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp3.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp4.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp5.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp6.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp7.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp8.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp9.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp10.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp11.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp12.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp13.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp14.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp15.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp16.toDBString().
                        compareTo(matrix_cp16_DBstring) != 0 ) ||
                      ( matrix_cp17.toDBString().
                        compareTo(matrix_cp17_DBstring) != 0 ) ||
                      ( nominal_cp0.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( nominal_cp1.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( pred_cp0.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( pred_cp1.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( text_cp0.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) ||
                      ( text_cp1.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ( float_cp0.toDBString().
                           compareTo(float_cp_DBstring) != 0 ) ||
                         ( float_cp1.toDBString().
                           compareTo(float_cp_DBstring) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected float_cp?.toDBString(): %s\n",
                                float_cp0.toDBString());
                    }

                    if ( ( int_cp0.toDBString().
                           compareTo(int_cp_DBstring) != 0 ) ||
                         ( int_cp1.toDBString().
                           compareTo(int_cp_DBstring) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected int_cp.toDBString(): %s\n",
                                int_cp0.toDBString());
                    }

                    if ( ( matrix_cp0.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp1.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp2.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp3.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp4.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp5.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp6.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp7.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp8.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp9.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp10.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp11.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp12.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp13.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp14.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) ||
                         ( matrix_cp15.toDBString().
                            compareTo(matrix_cp0_DBstring) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0-15.toDBString(): %s\n",
                                matrix_cp0.toDBString());
                    }

                    if ( matrix_cp16.toDBString().
                         compareTo(matrix_cp16_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp16.toDBString(): %s\n",
                                matrix_cp16.toDBString());
                    }

                    if ( matrix_cp17.toDBString().
                         compareTo(matrix_cp17_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp17.toDBString(): %s\n",
                                matrix_cp17.toDBString());
                    }

                    if ( ( nominal_cp0.toDBString().
                           compareTo(nominal_cp_DBstring) != 0 ) ||
                         ( nominal_cp1.toDBString().
                           compareTo(nominal_cp_DBstring) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected nominal_cp?.toDBString(): %s\n",
                                nominal_cp0.toDBString());
                    }

                    if ( ( pred_cp0.toDBString().
                           compareTo(pred_cp_DBstring) != 0 ) ||
                         ( pred_cp1.toDBString().
                           compareTo(pred_cp_DBstring) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected pred_cp?.toDBString(): %s\n",
                                pred_cp0.toDBString());
                    }

                    if ( ( text_cp0.toDBString().
                           compareTo(text_cp_DBstring) != 0 ) ||
                         ( text_cp1.toDBString().
                           compareTo(text_cp_DBstring) != 0 ) )
                    {
                        outStream.printf(
                                "unexpected text_cp?.toDBString(): %s\n",
                                text_cp0.toDBString());
                    }
                }
            }
        }

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            // float, int, nominal, pred, q-string, timestamp, untyped
            String testString0 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, " +
                                "a_nominal, pve0(<arg>), \"q-string\", " +
                                "00:00:01:000, <untyped>, " +
                                "float_mve(0, 00:00:00:000, 00:00:00:000, 0.0))";
//                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, " +
//                                "a_nominal, pve0(<arg>), \"q-string\", " +
//                                "00:00:01:000, <untyped>, " +
//                                "float_mve(<ord>, <onset>, <offset>, 0.0))";
            // untyped, float, int, nominal, pred, q-string, timestamp
            String testString1 =
                    "matrix_mve1(33, 00:00:00:033, 00:00:33:000, 1, , (), \"\", 00:00:00:000, 00:00:01:000, (), 0.0)";
//                    "pve5(1.0, 2.0, 0, , (), \"\", 00:00:00:000)";
            // timestamp, untyped, float, int, nominal, pred, q-string
            String testString2 =
                    "matrix_mve2(33, 00:00:00:033, 00:00:33:000, , (), \"a_nominal\", 00:00:00:000, \"q-string\", (), 0.0, 0)";
//                    "pve6(00:00:00:000, 2, 0.0, 0, q-string, (), \"\")";
            // q-string, timestamp, untyped, float, int, nominal, pred
            String testString3 =
                    "matrix_mve3(33, 00:00:00:033, 00:00:33:000, (), \"\", 00:00:00:000, pve0(<arg>), (), 0.0, 0, )";
//                    "pve7(\"\", 00:00:00:002, a_nominal, 0.0, 0, , ())";
            // pred, q-string, timestamp, untyped, float, int, nominal
            String testString4 =
                    "matrix_mve4(33, 00:00:00:033, 00:00:33:000, \"\", 00:00:00:002, a_nominal, (), 0.0, 0, , ())";
//                    "pve8((), \"\", 00:00:00:000, pve0(<arg1>, <arg2>), 0.0, 0, )";
            // nominal, pred, q-string, timestamp, untyped, float, int
            String testString5 =
                    "matrix_mve5(33, 00:00:00:033, 00:00:33:000, 00:00:00:000, 2, (), 0.0, 0, , (), \"\")";
//                    "pve9(, (), \"a_nominal\", 00:00:00:000, \"q-string\", 0.0, 0)";
            // int, nominal, pred, q-string, timestamp, untyped, float
            String testString6 =
                    "matrix_mve6(33, 00:00:00:033, 00:00:33:000, 1.0, (), 0.0, 0, q-string, (), \"\", 00:00:00:000)";
//                    "pve10(1, , (), \"\", 00:00:00:000, 00:00:00:000, 0.0)";
            // float, int, nominal, pred, q-string, timestamp, untyped
            String testString7 =
                    "matrix_mve7(33, 00:00:00:033, 00:00:33:000, (), 2.0, 0, , (), \"\", 00:00:00:000, float_mve(0, 00:00:00:000, 00:00:00:000, 0.0))";
//                    "matrix_mve7(33, 00:00:00:033, 00:00:33:000, (), 2.0, 0, , (), \"\", 00:00:00:000, float_mve(<ord>, <onset>, <offset>, 0.0))";
//                    "pve11(1.0, 2, a_nominal, pve0(<arg1>, <arg2>), " +
//                          "\"q-string\", 00:00:00:000, <untyped>)";
            String testString8 =
                    "matrix_mve8(33, 00:00:00:033, 00:00:33:000, 1.0, 2, a_nominal, pve0(<arg>), \"q-string\", 00:00:01:000, <untyped>, float_mve(0, 00:00:00:000, 00:00:00:000, 0.0))";
//                    "matrix_mve8(33, 00:00:00:033, 00:00:33:000, 1.0, 2, a_nominal, pve0(<arg>), \"q-string\", 00:00:01:000, <untyped>, float_mve(<ord>, <onset>, <offset>, 0.0))";
//                    "pve3(1.0)";
            String testString9 = "matrix_mve9(33, 00:00:00:033, 00:00:33:000, 1.0, 2, a_nominal)";
            String testString10 = "matrix_mve10(33, 00:00:00:033, 00:00:33:000, 1.0)";
            String testString11 = "float_mve(33, 00:00:00:033, 00:00:33:000, 1.0)";
            String testString12 = "int_mve(33, 00:00:00:033, 00:00:33:000, 1)";
            String testString13 = "nominal_mve(33, 00:00:00:033, 00:00:33:000, )";
            String testString14 = "pred_mve(33, 00:00:00:033, 00:00:33:000, ())";
            String testString15 = "text_mve(33, 00:00:00:033, 00:00:33:000, )";
            String testString16 =
                    "matrix_mve9(11, 00:00:00:011, 00:00:11:000, 11.0, <arg2>, <arg3>)";
            String testString17 =
                    "matrix_mve9(22, 00:00:00:022, 00:00:22:000, 22, <arg2>, <arg3>)";
            String testString18 =
                    "matrix_mve9(44, 00:00:00:044, 00:00:44:000, another_nominal, <arg2>, <arg3>)";
            String testString19 =
                    "matrix_mve9(55, 00:00:00:055, 00:00:55:000, pve0(<arg>), <arg2>, <arg3>)";
            String testString20 =
                    "matrix_mve9(66, 00:00:01:006, 00:01:06:000, \"a text string\", <arg2>, <arg3>)";

            try
            {
                matrix_cp0.setMveID(matrix_mve0_ID, true);
                matrix_cp1.setMveID(matrix_mve1_ID, true);
                matrix_cp2.setMveID(matrix_mve2_ID, true);
                matrix_cp3.setMveID(matrix_mve3_ID, true);
                matrix_cp4.setMveID(matrix_mve4_ID, true);
                matrix_cp5.setMveID(matrix_mve5_ID, true);
                matrix_cp6.setMveID(matrix_mve6_ID, true);
                matrix_cp7.setMveID(matrix_mve7_ID, true);
                matrix_cp8.setMveID(matrix_mve8_ID, true);

                matrix_cp9.setMveID(matrix_mve9_ID, true);
                matrix_cp10.setMveID(matrix_mve10_ID, true);
                matrix_cp11.setMveID(float_mve_ID, true);
                matrix_cp12.setMveID(int_mve_ID, true);
                matrix_cp13.setMveID(nominal_mve_ID, true);
                matrix_cp14.setMveID(pred_mve_ID, true);
                matrix_cp15.setMveID(text_mve_ID, true);

                float_cp1.setMveID(matrix_mve9_ID, true);
                int_cp1.setMveID(matrix_mve9_ID, true);
                nominal_cp1.setMveID(matrix_mve9_ID, true);
                pred_cp1.setMveID(matrix_mve9_ID, true);
                text_cp1.setMveID(matrix_mve9_ID, true);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test of setMveID(?, true) " +
                                        "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test of setMveID(?, true) threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            } else if ( ( matrix_cp0.toString().compareTo(testString0) != 0 ) ||
                        ( matrix_cp1.toString().compareTo(testString1) != 0 ) ||
                        ( matrix_cp2.toString().compareTo(testString2) != 0 ) ||
                        ( matrix_cp3.toString().compareTo(testString3) != 0 ) ||
                        ( matrix_cp4.toString().compareTo(testString4) != 0 ) ||
                        ( matrix_cp5.toString().compareTo(testString5) != 0 ) ||
                        ( matrix_cp6.toString().compareTo(testString6) != 0 ) ||
                        ( matrix_cp7.toString().compareTo(testString7) != 0 ) ||
                        ( matrix_cp8.toString().compareTo(testString8) != 0 ) ||
                        ( matrix_cp9.toString().compareTo(testString9) != 0 ) ||
                        ( matrix_cp10.toString().compareTo(testString10) != 0 ) ||
                        ( matrix_cp11.toString().compareTo(testString11) != 0 ) ||
                        ( matrix_cp12.toString().compareTo(testString12) != 0 ) ||
                        ( matrix_cp13.toString().compareTo(testString13) != 0 ) ||
                        ( matrix_cp14.toString().compareTo(testString14) != 0 ) ||
                        ( matrix_cp15.toString().compareTo(testString15) != 0 ) ||
                        ( float_cp1.toString().compareTo(testString16) != 0 ) ||
                        ( int_cp1.toString().compareTo(testString17) != 0 ) ||
                        ( nominal_cp1.toString().compareTo(testString18) != 0 ) ||
                        ( pred_cp1.toString().compareTo(testString19) != 0 ) ||
                        ( text_cp1.toString().compareTo(testString20) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( matrix_cp0.toString().compareTo(testString0) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp0.toString(1): \"%s\".\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp1.toString().compareTo(testString1) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp1.toString(1): \"%s\".\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().compareTo(testString2) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp2.toString(1): \"%s\".\n",
                                matrix_cp2.toString());
                    }

                    if ( matrix_cp3.toString().compareTo(testString3) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp3.toString(1): \"%s\".\n",
                                matrix_cp3.toString());
                    }

                    if ( matrix_cp4.toString().compareTo(testString4) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp4.toString(1): \"%s\".\n",
                                matrix_cp4.toString());
                    }

                    if ( matrix_cp5.toString().compareTo(testString5) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp5.toString(1): \"%s\".\n",
                                matrix_cp5.toString());
                    }

                    if ( matrix_cp6.toString().compareTo(testString6) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp6.toString(1): \"%s\".\n",
                                matrix_cp6.toString());
                    }

                    if ( matrix_cp7.toString().compareTo(testString7) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matix_cp7.toString(1): \"%s\".\n",
                                matrix_cp7.toString());
                    }

                    if ( matrix_cp8.toString().compareTo(testString8) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp8.toString(1): \"%s\".\n",
                                matrix_cp8.toString());
                    }

                    if ( matrix_cp9.toString().compareTo(testString9) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp9.toString(1): \"%s\".\n",
                                matrix_cp9.toString());
                    }

                    if ( matrix_cp10.toString().compareTo(testString10) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp10.toString(1): \"%s\".\n",
                                matrix_cp10.toString());
                    }


                    if ( matrix_cp11.toString().compareTo(testString11) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp11.toString(1): \"%s\".\n",
                                matrix_cp11.toString());
                    }


                    if ( matrix_cp12.toString().compareTo(testString12) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp12.toString(1): \"%s\".\n",
                                matrix_cp12.toString());
                    }


                    if ( matrix_cp13.toString().compareTo(testString13) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp13.toString(1): \"%s\".\n",
                                matrix_cp13.toString());
                    }


                    if ( matrix_cp14.toString().compareTo(testString14) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp14.toString(1): \"%s\".\n",
                                matrix_cp14.toString());
                    }


                    if ( matrix_cp15.toString().compareTo(testString15) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp15.toString(1): \"%s\".\n",
                                matrix_cp15.toString());
                    }

                    if ( float_cp1.toString().compareTo(testString16) != 0 )
                    {
                        outStream.printf(
                                "Unexpected float_cp1.toString(1): \"%s\".\n",
                                float_cp1.toString());
                    }

                    if ( int_cp1.toString().compareTo(testString17) != 0 )
                    {
                        outStream.printf(
                                "Unexpected int_cp1.toString(1): \"%s\".\n",
                                int_cp1.toString());
                    }

                    if ( nominal_cp1.toString().compareTo(testString18) != 0 )
                    {
                        outStream.printf(
                                "Unexpected nominal_cp1.toString(1): \"%s\".\n",
                                nominal_cp1.toString());
                    }

                    if ( pred_cp1.toString().compareTo(testString19) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred_cp1.toString(1): \"%s\".\n",
                                pred_cp1.toString());
                    }

                    if ( text_cp1.toString().compareTo(testString20) != 0 )
                    {
                        outStream.printf(
                                "Unexpected text_cp1.toString(1): \"%s\".\n",
                                text_cp1.toString());
                    }
                }
            }
        }


        // set matrix_cp1-8 to a variety of mve id's -- all with salvage
        // set to FALSE.  Should get empty column predicates of type congruent
        // with the mve id supplied.  Don't touch matrix_cp0, as we will need
        // it later for other tests.
        //
        // set matrix_cp9-15 back to the original mve id -- some data
        // should be lost, but not all in all cases.
        //
        // set float_cp1, int_cp1, nominal_cp1, pred_cp1, and text_cp1 back
        // to their original mve IDs.  All should be as it was.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            String testString1 =
                    "matrix_mve9(0, 00:00:00:000, 00:00:00:000, <arg1>, <arg2>, <arg3>)";
            String testString2 =
                    "matrix_mve10(0, 00:00:00:000, 00:00:00:000, <arg1>)";
            String testString3 =
                    "float_mve(0, 00:00:00:000, 00:00:00:000, 0.0)";
            String testString4 =
                    "int_mve(0, 00:00:00:000, 00:00:00:000, 0)";
            String testString5 =
                    "nominal_mve(0, 00:00:00:000, 00:00:00:000, )";
            String testString6 =
                    "pred_mve(0, 00:00:00:000, 00:00:00:000, ())";
            String testString7 =
                    "text_mve(0, 00:00:00:000, 00:00:00:000, )";
            String testString8 =
                    "matrix_mve8(0, 00:00:00:000, 00:00:00:000, 0.0, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString9 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, a_nominal, (), \"\", 00:00:00:000, <untyped>, ())";
            String testString10 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString11 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString12 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString13 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 0.0, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString14 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 0.0, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString15 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 0.0, 0, , (), \"\", 00:00:00:000, <untyped>, ())";
            String testString16 =
                    "float_mve(11, 00:00:00:011, 00:00:11:000, 11.0)";
            String testString17 =
                    "int_mve(22, 00:00:00:022, 00:00:22:000, 22)";
            String testString18 =
                    "nominal_mve(44, 00:00:00:044, 00:00:44:000, another_nominal)";
            String testString19 =
                    "pred_mve(55, 00:00:00:055, 00:00:55:000, pve0(<arg>))";
            String testString20 =
                    "text_mve(66, 00:00:01:006, 00:01:06:000, a text string)";

            try
            {
                matrix_cp1.setMveID(matrix_mve9_ID, false);
                matrix_cp2.setMveID(matrix_mve10_ID, false);
                matrix_cp3.setMveID(float_mve_ID, false);
                matrix_cp4.setMveID(int_mve_ID, false);
                matrix_cp5.setMveID(nominal_mve_ID, false);
                matrix_cp6.setMveID(pred_mve_ID, false);
                matrix_cp7.setMveID(text_mve_ID, false);
                matrix_cp8.setMveID(matrix_mve8_ID, false);

                matrix_cp9.setMveID(matrix_mve0_ID, true);
                matrix_cp10.setMveID(matrix_mve0_ID, true);
                matrix_cp11.setMveID(matrix_mve0_ID, true);
                matrix_cp12.setMveID(matrix_mve0_ID, true);
                matrix_cp13.setMveID(matrix_mve0_ID, true);
                matrix_cp14.setMveID(matrix_mve0_ID, true);
                matrix_cp15.setMveID(matrix_mve0_ID, true);

                float_cp1.setMveID(float_mve_ID, true);
                int_cp1.setMveID(int_mve_ID, true);
                nominal_cp1.setMveID(nominal_mve_ID, true);
                pred_cp1.setMveID(pred_mve_ID, true);
                text_cp1.setMveID(text_mve_ID, true);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test 2 of setMveID(?, true) " +
                                        "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test 2 of setMveID(?, true) threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         systemErrorExceptionString);
                    }
                }
            } else if ( ( matrix_cp1.toString().compareTo(testString1) != 0 ) ||
                        ( matrix_cp2.toString().compareTo(testString2) != 0 ) ||
                        ( matrix_cp3.toString().compareTo(testString3) != 0 ) ||
                        ( matrix_cp4.toString().compareTo(testString4) != 0 ) ||
                        ( matrix_cp5.toString().compareTo(testString5) != 0 ) ||
                        ( matrix_cp6.toString().compareTo(testString6) != 0 ) ||
                        ( matrix_cp7.toString().compareTo(testString7) != 0 ) ||
                        ( matrix_cp8.toString().compareTo(testString8) != 0 ) ||
                        ( matrix_cp9.toString().compareTo(testString9) != 0 ) ||
                        ( matrix_cp10.toString().compareTo(testString10) != 0 ) ||
                        ( matrix_cp11.toString().compareTo(testString11) != 0 ) ||
                        ( matrix_cp12.toString().compareTo(testString12) != 0 ) ||
                        ( matrix_cp13.toString().compareTo(testString13) != 0 ) ||
                        ( matrix_cp14.toString().compareTo(testString14) != 0 ) ||
                        ( matrix_cp15.toString().compareTo(testString15) != 0 ) ||
                        ( float_cp1.toString().compareTo(testString16) != 0 ) ||
                        ( int_cp1.toString().compareTo(testString17) != 0 ) ||
                        ( nominal_cp1.toString().compareTo(testString18) != 0 ) ||
                        ( pred_cp1.toString().compareTo(testString19) != 0 ) ||
                        ( text_cp1.toString().compareTo(testString20) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( matrix_cp1.toString().compareTo(testString1) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp1.toString(2): \"%s\".\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().compareTo(testString2) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp2.toString(2): \"%s\".\n",
                                matrix_cp2.toString());
                    }

                    if ( matrix_cp3.toString().compareTo(testString3) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp3.toString(1): \"%s\".\n",
                                matrix_cp3.toString());
                    }

                    if ( matrix_cp4.toString().compareTo(testString4) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp4.toString(2): \"%s\".\n",
                                matrix_cp4.toString());
                    }

                    if ( matrix_cp5.toString().compareTo(testString5) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp5.toString(2): \"%s\".\n",
                                matrix_cp5.toString());
                    }

                    if ( matrix_cp6.toString().compareTo(testString6) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp6.toString(2): \"%s\".\n",
                                matrix_cp6.toString());
                    }

                    if ( matrix_cp7.toString().compareTo(testString7) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matix_cp7.toString(2): \"%s\".\n",
                                matrix_cp7.toString());
                    }

                    if ( matrix_cp8.toString().compareTo(testString8) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp8.toString(2): \"%s\".\n",
                                matrix_cp8.toString());
                    }

                    if ( matrix_cp9.toString().compareTo(testString9) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp9.toString(2): \"%s\".\n",
                                matrix_cp9.toString());
                    }

                    if ( matrix_cp10.toString().compareTo(testString10) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp10.toString(2): \"%s\".\n",
                                matrix_cp10.toString());
                    }


                    if ( matrix_cp11.toString().compareTo(testString11) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp11.toString(2): \"%s\".\n",
                                matrix_cp11.toString());
                    }


                    if ( matrix_cp12.toString().compareTo(testString12) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp12.toString(2): \"%s\".\n",
                                matrix_cp12.toString());
                    }


                    if ( matrix_cp13.toString().compareTo(testString13) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp13.toString(2): \"%s\".\n",
                                matrix_cp13.toString());
                    }


                    if ( matrix_cp14.toString().compareTo(testString14) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp14.toString(2): \"%s\".\n",
                                matrix_cp14.toString());
                    }


                    if ( matrix_cp15.toString().compareTo(testString15) != 0 )
                    {
                        outStream.printf(
                                "Unexpected matrix_cp15.toString(2): \"%s\".\n",
                                matrix_cp15.toString());
                    }

                    if ( float_cp1.toString().compareTo(testString16) != 0 )
                    {
                        outStream.printf(
                                "Unexpected float_cp1.toString(2): \"%s\".\n",
                                float_cp1.toString());
                    }

                    if ( int_cp1.toString().compareTo(testString17) != 0 )
                    {
                        outStream.printf(
                                "Unexpected int_cp1.toString(2): \"%s\".\n",
                                int_cp1.toString());
                    }

                    if ( nominal_cp1.toString().compareTo(testString18) != 0 )
                    {
                        outStream.printf(
                                "Unexpected nominal_cp1.toString(2): \"%s\".\n",
                                nominal_cp1.toString());
                    }

                    if ( pred_cp1.toString().compareTo(testString19) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred_cp1.toString(2): \"%s\".\n",
                                pred_cp1.toString());
                    }

                    if ( text_cp1.toString().compareTo(testString20) != 0 )
                    {
                        outStream.printf(
                                "Unexpected text_cp1.toString(2): \"%s\".\n",
                                text_cp1.toString());
                    }
                }
            }
        }


        /* Now do a battery of tests of getArgCopy() -- objective is to
         * verify that output of getArgCopy() is a valid copy of the target
         * argument, or that the method fails appropriately if the target
         * doesn't exist.
         */

        failures += ColPred.TestGetArgCopy(float_cp0, -1, 1,
                expectedResult.system_error, "float_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(float_cp0,  0, 2,
                expectedResult.succeed, "float_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(float_cp0,  1, 3,
                expectedResult.succeed, "float_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(float_cp0,  2, 4,
                expectedResult.succeed, "float_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(float_cp0,  3, 5,
                expectedResult.succeed, "float_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(float_cp0,  4, 6,
                expectedResult.return_null, "float_cp0", outStream, verbose);

        failures += ColPred.TestGetArgCopy(int_cp0, -1, 100,
                expectedResult.system_error, "int_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(int_cp0,  0, 101,
                expectedResult.succeed, "int_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(int_cp0,  1, 102,
                expectedResult.succeed, "int_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(int_cp0,  2, 103,
                expectedResult.succeed, "int_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(int_cp0,  3, 104,
                expectedResult.succeed, "int_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(int_cp0,  4, 105,
                expectedResult.return_null, "int_cp0", outStream, verbose);

        failures += ColPred.TestGetArgCopy(matrix_cp0, -1, 200,
                expectedResult.system_error, "matrix_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp0,  0, 201,
                expectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp0,  1, 202,
                expectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp0,  2, 203,
                expectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp0,  3, 204,
                expectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp0,  4, 205,
                expectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp0,  5, 206,
                expectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp0,  6, 207,
                expectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp0,  7, 208,
                expectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp0,  8, 209,
                expectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp0,  9, 210,
                expectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp0,  10, 211,
                expectedResult.succeed, "matrix_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp0,  11, 212,
                expectedResult.return_null, "matrix_cp0", outStream, verbose);

        failures += ColPred.TestGetArgCopy(matrix_cp8, -1, 300,
                expectedResult.system_error, "matrix_cp8", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp8,  0, 301,
                expectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp8,  1, 302,
                expectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp8,  2, 303,
                expectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp8,  3, 304,
                expectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp8,  4, 305,
                expectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp8,  5, 306,
                expectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp8,  6, 307,
                expectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp8,  7, 308,
                expectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp8,  8, 309,
                expectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp8,  9, 310,
                expectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp8,  10, 311,
                expectedResult.succeed, "matrix_cp8", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp8,  11, 312,
                expectedResult.return_null, "matrix_cp8", outStream, verbose);

        failures += ColPred.TestGetArgCopy(matrix_cp16, -1, 400,
                expectedResult.system_error, "matrix_cp16", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp16,  0, 401,
                expectedResult.succeed, "matrix_cp16", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp16,  1, 402,
                expectedResult.succeed, "matrix_cp16", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp16,  2, 403,
                expectedResult.succeed, "matrix_cp16", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp16,  3, 404,
                expectedResult.succeed, "matrix_cp16", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp16,  4, 405,
                expectedResult.succeed, "matrix_cp16", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp16,  5, 406,
                expectedResult.succeed, "matrix_cp16", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp16,  6, 407,
                expectedResult.return_null, "matrix_cp16", outStream, verbose);

        failures += ColPred.TestGetArgCopy(matrix_cp17, -1, 500,
                expectedResult.system_error, "matrix_cp17", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp17,  0, 501,
                expectedResult.succeed, "matrix_cp17", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp17,  1, 502,
                expectedResult.succeed, "matrix_cp17", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp17,  2, 503,
                expectedResult.succeed, "matrix_cp17", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp17,  3, 504,
                expectedResult.succeed, "matrix_cp17", outStream, verbose);
        failures += ColPred.TestGetArgCopy(matrix_cp17,  4, 505,
                expectedResult.return_null, "matrix_cp17", outStream, verbose);

        failures += ColPred.TestGetArgCopy(nominal_cp0, -1, 600,
                expectedResult.system_error, "nominal_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(nominal_cp0,  0, 601,
                expectedResult.succeed, "nominal_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(nominal_cp0,  1, 602,
                expectedResult.succeed, "nominal_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(nominal_cp0,  2, 603,
                expectedResult.succeed, "nominal_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(nominal_cp0,  3, 604,
                expectedResult.succeed, "nominal_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(nominal_cp0,  4, 605,
                expectedResult.return_null, "nominal_cp0", outStream, verbose);

        failures += ColPred.TestGetArgCopy(pred_cp0, -1, 700,
                expectedResult.system_error, "pred_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(pred_cp0,  0, 701,
                expectedResult.succeed, "pred_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(pred_cp0,  1, 702,
                expectedResult.succeed, "pred_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(pred_cp0,  2, 703,
                expectedResult.succeed, "pred_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(pred_cp0,  3, 704,
                expectedResult.succeed, "pred_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(pred_cp0,  4, 705,
                expectedResult.return_null, "pred_cp0", outStream, verbose);

        failures += ColPred.TestGetArgCopy(text_cp0, -1, 800,
                expectedResult.system_error, "text_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(text_cp0,  0, 801,
                expectedResult.succeed, "text_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(text_cp0,  1, 802,
                expectedResult.succeed, "text_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(text_cp0,  2, 803,
                expectedResult.succeed, "text_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(text_cp0,  3, 804,
                expectedResult.succeed, "text_cp0", outStream, verbose);
        failures += ColPred.TestGetArgCopy(text_cp0,  4, 805,
                expectedResult.return_null, "text_cp0", outStream, verbose);


        /* Now test argument replacement.
         *
         * Start with attempts to replace the value of a float column pred.
         */

        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 0.0);
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(fdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(float_cp0,
                                                    floatArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "float_cp0",
                                                    "floatArg");

                failures += VerifyArgListAsgnmntFails(float_cp0,
                                                      intArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "intArg");

                failures += VerifyArgListAsgnmntFails(float_cp0,
                                                      nomArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "nomArg");

                failures += VerifyArgListAsgnmntFails(float_cp0,
                                                      predArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "predArg");

                failures += VerifyArgListAsgnmntFails(float_cp0,
                                                      textArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "textArg");

                failures += VerifyArgListAsgnmntFails(float_cp0,
                                                      qsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(float_cp0,
                                                      tsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "tsArg");

                failures += VerifyArgListAssignment(float_cp0,
                                                    arg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "float_cp0",
                                                    "arg");
            }
        }


        /* now the value of an int column predicate */
        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = int_mve.getCPFormalArg(3).getID();
                arg = new IntDataValue(db, fargID, 0);
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(idv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(int_cp0,
                                                      floatArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "floatArg");

                failures += VerifyArgListAssignment(int_cp0,
                                                    intArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "int_cp0",
                                                    "intArg");

                failures += VerifyArgListAsgnmntFails(int_cp0,
                                                      nomArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "nomArg");

                failures += VerifyArgListAsgnmntFails(int_cp0,
                                                      predArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "predArg");

                failures += VerifyArgListAsgnmntFails(int_cp0,
                                                      textArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "textArg");

                failures += VerifyArgListAsgnmntFails(int_cp0,
                                                      qsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(int_cp0,
                                                      tsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "tsArg");

                failures += VerifyArgListAssignment(int_cp0,
                                                    arg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "int_cp0",
                                                    "arg");
            }
        }


        /* now the value of a nominal column */
        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = nominal_mve.getCPFormalArg(3).getID();
                arg = new NominalDataValue(db, fargID, "whatever");
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(ndv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(nominal_cp0,
                                                      floatArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "floatArg");

                failures += VerifyArgListAsgnmntFails(nominal_cp0,
                                                      intArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "intArg");

                failures += VerifyArgListAssignment(nominal_cp0,
                                                    nomArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "nominal_cp0",
                                                    "nomArg");

                failures += VerifyArgListAsgnmntFails(nominal_cp0,
                                                      predArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "predArg");

                failures += VerifyArgListAsgnmntFails(nominal_cp0,
                                                      textArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "textArg");

                failures += VerifyArgListAsgnmntFails(nominal_cp0,
                                                      qsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(nominal_cp0,
                                                      tsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "tsArg");

                failures += VerifyArgListAssignment(nominal_cp0,
                                                    arg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "nominal_cp0",
                                                    "arg");
            }
        }


        /* now the value of a predicate column predicate */
        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pred_mve.getCPFormalArg(3).getID();
                arg = new PredDataValue(db, fargID,
                        new Predicate(db, pve0_ID));
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(pdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(pred_cp0,
                                                      floatArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "floatArg");

                failures += VerifyArgListAsgnmntFails(pred_cp0,
                                                      intArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "intArg");

                failures += VerifyArgListAsgnmntFails(pred_cp0,
                                                      nomArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "nomArg");

                failures += VerifyArgListAssignment(pred_cp0,
                                                    predArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "pred_cp0",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(pred_cp0,
                                                      textArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "textArg");

                failures += VerifyArgListAsgnmntFails(pred_cp0,
                                                      qsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(pred_cp0,
                                                      tsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "tsArg");

                failures += VerifyArgListAssignment(pred_cp0,
                                                    arg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "pred_cp0",
                                                    "arg");
            }
        }


        /* now the value of a text column predicate */
        if ( failures == 0 )
        {
            arg = null;
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = text_mve.getCPFormalArg(3).getID();
                arg = new TextStringDataValue(db, fargID,
                                              "yet another text string");
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(tsdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(text_cp0,
                                                      floatArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "floatArg");

                failures += VerifyArgListAsgnmntFails(text_cp0,
                                                      intArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "intArg");

                failures += VerifyArgListAsgnmntFails(text_cp0,
                                                      nomArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "nomArg");

                failures += VerifyArgListAsgnmntFails(text_cp0,
                                                      predArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "predArg");

                failures += VerifyArgListAssignment(text_cp0,
                                                    textArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "text_cp0",
                                                    "textArg");

                failures += VerifyArgListAsgnmntFails(text_cp0,
                                                      qsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "qsArg");

                failures += VerifyArgListAsgnmntFails(text_cp0,
                                                      tsArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "tsArg");

                failures += VerifyArgListAssignment(text_cp0,
                                                    arg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "text_cp0",
                                                    "arg");
            }
        }


        /* we have save matrix column predicates for last -- in theory
         * only need to test the single entry case below.  However,
         * we will start with that, and then do some spot checks on
         * multi-entry matrix column predicates.
         *
         * First use new args without fargIDs set:
         */
        if ( failures == 0 )
        {
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(m2adv)";

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_cp17,
                                                    floatArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "floatArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    intArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "intArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    nomArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "nomArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    predArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(matrix_cp17,
                                                      textArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp17",
                                                      "textArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    qsArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "qsArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    tsArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "tsArg");
            }
        }

        /* repeat the above test, only with fargIDs set:
         */
        if ( failures == 0 )
        {
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = matrix_mve10.getCPFormalArg(3).getID();

                floatArg = new FloatDataValue(db, fargID, 1066.0);
                intArg = new IntDataValue(db, fargID, 1903);
                nomArg = new NominalDataValue(db, fargID, "yan");
                predArg = new PredDataValue(db, fargID,
                            new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db, fargID, "yats");
                qsArg = new QuoteStringDataValue(db, fargID, "yaqs");
                tsArg = new TimeStampDataValue(db, fargID,
                            new TimeStamp(db.getTicks(), 60));
                undefArg = new UndefinedDataValue(db, fargID,
                            matrix_mve10.getFormalArg(0).getFargName());

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(m2dv)";

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_cp17,
                                                    floatArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "floatArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    intArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "intArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    nomArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "nomArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    predArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(matrix_cp17,
                                                      textArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp17",
                                                      "textArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    qsArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "qsArg");

                failures += VerifyArgListAssignment(matrix_cp17,
                                                    tsArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp17",
                                                    "tsArg");
            }
        }


        /* finally, do some spot checks of replaceArg()/getArg() on column
         * predicates implied by vme's of type matrix and with length greater
         * than one -- in the first pass, we will not assign fargIDs.
         */
        if ( failures == 0 )
        {
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                floatArg = new FloatDataValue(db);
                floatArg.setItsValue(1066.0);
                intArg = new IntDataValue(db);
                intArg.setItsValue(1903);
                nomArg = new NominalDataValue(db);
                nomArg.setItsValue("yan");
                predArg = new PredDataValue(db);
                predArg.setItsValue(new Predicate(db, pve1_ID));
                textArg = new TextStringDataValue(db);
                textArg.setItsValue("yats");
                qsArg = new QuoteStringDataValue(db);
                qsArg.setItsValue("yaqs");
                tsArg = new TimeStampDataValue(db);
                tsArg.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg= new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(m0adv)";

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_cp0,
                                                    floatArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "floatArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    intArg,
                                                    4,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "intArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    nomArg,
                                                    5,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "nomArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    predArg,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(matrix_cp0,
                                                      textArg,
                                                      7,
                                                      outStream,
                                                      verbose,
                                                      "text_cp",
                                                      "textArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    qsArg,
                                                    7,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "qsArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    tsArg,
                                                    8,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "tsArg");
            }
        }

        /* and a simlar test, with fargIDs set */

        if ( failures == 0 )
        {
            floatArg = null;
            intArg = null;
            nomArg = null;
            predArg = null;
            textArg = null;
            qsArg = null;
            tsArg = null;
            undefArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                floatArg = new FloatDataValue(db, fargID, 1066.0);

                fargID = matrix_mve0.getCPFormalArg(4).getID();
                intArg = new IntDataValue(db, fargID, 1903);

                fargID = matrix_mve0.getCPFormalArg(5).getID();
                nomArg = new NominalDataValue(db, fargID, "yan");

                fargID = matrix_mve0.getCPFormalArg(6).getID();
                predArg = new PredDataValue(db, fargID,
                            new Predicate(db, pve1_ID));

                fargID = matrix_mve0.getCPFormalArg(9).getID();
                textArg = new TextStringDataValue(db, fargID, "yats");

                fargID = matrix_mve0.getCPFormalArg(7).getID();
                qsArg = new QuoteStringDataValue(db, fargID, "yaqs");

                fargID = matrix_mve0.getCPFormalArg(8).getID();
                tsArg = new TimeStampDataValue(db, fargID,
                            new TimeStamp(db.getTicks(), 360));

                fargID = matrix_mve0.getCPFormalArg(9).getID();
                undefArg = new UndefinedDataValue(db, fargID,
                            matrix_mve0.getCPFormalArg(9).getFargName());

                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new FloatDataValue(db, fargID, 10.0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( floatArg == null ) ||
                 ( intArg == null ) ||
                 ( nomArg == null ) ||
                 ( predArg == null ) ||
                 ( textArg == null ) ||
                 ( qsArg == null ) ||
                 ( tsArg == null ) ||
                 ( undefArg == null ) ||
                 ( arg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(m0dv)";

                    if ( floatArg == null )
                    {
                        outStream.printf("%s: Allocation of floatArg failed.\n",
                                         testTag);
                    }

                    if ( intArg == null )
                    {
                        outStream.printf("%s: Allocation of intArg failed.\n",
                                         testTag);
                    }

                    if ( nomArg == null )
                    {
                        outStream.printf("%s: Allocation of nomArg failed.\n",
                                         testTag);
                    }

                    if ( predArg == null )
                    {
                        outStream.printf("%s: Allocation of predArg failed.\n",
                                         testTag);
                    }

                    if ( textArg == null )
                    {
                        outStream.printf("%s: Allocation of textArg failed.\n",
                                         testTag);
                    }

                    if ( qsArg == null )
                    {
                        outStream.printf("%s: Allocation of qsArg failed.\n",
                                         testTag);
                    }

                    if ( tsArg == null )
                    {
                        outStream.printf("%s: Allocation of tsArg failed.\n",
                                         testTag);
                    }

                    if ( undefArg == null )
                    {
                        outStream.printf("%s: Allocation of undefArg failed.\n",
                                         testTag);
                    }

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_cp0,
                                                    floatArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "floatArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    intArg,
                                                    4,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp1",
                                                    "intArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    nomArg,
                                                    5,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "nomArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    predArg,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "predArg");

                failures += VerifyArgListAsgnmntFails(matrix_cp0,
                                                      textArg,
                                                      9,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp0",
                                                      "textArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    qsArg,
                                                    7,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "qsArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    tsArg,
                                                    8,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "tsArg");

                failures += VerifyArgListAssignment(matrix_cp0,
                                                    arg,
                                                    9,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp0",
                                                    "arg");
            }
        }

        /* we have now tested replaceArg() and getArgCopy() against all
         * type combinations.  Must now go through the rest of the
         * cases in which failures are expected.
         */

        /* verify failure on a farg ID mismatch. */

        if ( failures == 0 )
        {
            goodArg = null;
            badArg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = matrix_mve9.getCPFormalArg(3).getID();
                goodArg = new NominalDataValue(db, fargID, "good_fargID");

                fargID = matrix_mve9.getCPFormalArg(4).getID();
                badArg = new NominalDataValue(db, fargID, "bad_fargID");

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( badArg == null ) ||
                 ( goodArg == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(fID)";

                    if ( goodArg == null )
                    {
                        outStream.printf("%s: Allocation of goodArg failed.\n",
                                         testTag);
                    }

                    if ( badArg == null )
                    {
                        outStream.printf("%s: Allocation of badArg failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(matrix_cp16,
                                                    goodArg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp16",
                                                    "goodArg");

                failures += VerifyArgListAsgnmntFails(matrix_cp16,
                                                      badArg,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp16",
                                                      "badArg");

                failures += VerifyArgListAssignment(matrix_cp16,
                                                    badArg,
                                                    4,
                                                    outStream,
                                                    verbose,
                                                    "matrix_cp16",
                                                    "badArg");
            }
        }


        /* next, verify that getArg() and replaceArg() fail when supplied
         * invalid indexes.
         */
        /* replaceArg() with negative index */
        if ( failures == 0 )
        {
            arg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                arg = new FloatDataValue(db);
                ((FloatDataValue)arg).setItsValue(28.0);

                float_cp0.replaceArg(-1, arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(bad_idx0)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( completed )
                    {
                        outStream.printf(
                            "%s: float_cp0.replaceArg(-1, arg) completed.\n",
                            testTag);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("%s: float_cp0.replaceArg(-1, " +
                                "arg) failed to throw a system error.\n",
                                testTag);
                    }
                }
            }
        }

        /* replaceArg() with index too big */
        if ( failures == 0 )
        {
            arg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                arg = new FloatDataValue(db);
                ((FloatDataValue)arg).setItsValue(28.0);

                matrix_cp16.replaceArg(6, arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(bad_idx1)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( completed )
                    {
                        outStream.printf(
                            "%s: matrix_cp16.replaceArg(6, arg) completed.\n",
                            testTag);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("%s: matrix_cp16.replaceArg(6, " +
                                "arg) failed to throw a system error.\n",
                                testTag);
                    }
                }
            }
        }

        /* getArg() with negative index */
        if ( failures == 0 )
        {
            arg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                arg = matrix_cp16.getArg(-1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(bad_idx2)";

                    if ( arg != null )
                    {
                        outStream.printf(
                                "%s: matrix_cp16.getArg(-1) returned.\n",
                                testTag);
                    }

                    if ( completed )
                    {
                        outStream.printf(
                            "%s: matrix_cp16.getArg(-1) completed.\n",
                            testTag);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("%s: matrix_cp16.getArg(-1) " +
                                "failed to throw a system error.\n",
                                testTag);
                    }
                }
            }
        }

        /* getArg() with index to big */
        if ( failures == 0 )
        {
            arg = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                arg = float_cp0.getArg(4);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( arg != null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(bad_idx3)";

                    if ( arg != null )
                    {
                        outStream.printf("%s: float_cp0.getArg(4) " +
                                "returned non-null.\n", testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf(
                            "%s: float_cp0.getArg(4) failed to complete.\n",
                            testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s: float_cp0.getArg(4) " +
                                "threw a system error: \"%s\".\n",
                                testTag, systemErrorExceptionString);
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

    } /* ColPred::TestArgListManagement() */


    /**
     * TestCopyConstructor()
     *
     * Run a battery of tests on the copy constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 10/01/08
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
            "Testing copy constructor for class ColPred                       ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long fargID;
        long pve0_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long int_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        long nominal_mve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long text_mve_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        DataValue arg = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> int_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        Vector<DataValue> matrix_cp1_arg_list = null;
        Vector<DataValue> matrix_cp2_arg_list = null;
        Vector<DataValue> nominal_cp_arg_list = null;
        Vector<DataValue> pred_cp_arg_list = null;
        Vector<DataValue> text_cp_arg_list = null;
        ColPred float_cp = null;
        ColPred float_cp_copy = null;
        ColPred empty_float_cp = null;
        ColPred empty_float_cp_copy = null;
        ColPred int_cp = null;
        ColPred int_cp_copy = null;
        ColPred empty_int_cp = null;
        ColPred empty_int_cp_copy = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp0_copy = null;
        ColPred empty_matrix_cp0 = null;
        ColPred empty_matrix_cp0_copy = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp1_copy = null;
        ColPred empty_matrix_cp1 = null;
        ColPred empty_matrix_cp1_copy = null;
        ColPred matrix_cp2 = null;
        ColPred matrix_cp2_copy = null;
        ColPred empty_matrix_cp2 = null;
        ColPred empty_matrix_cp2_copy = null;
        ColPred nominal_cp = null;
        ColPred nominal_cp_copy = null;
        ColPred empty_nominal_cp = null;
        ColPred empty_nominal_cp_copy = null;
        ColPred pred_cp = null;
        ColPred pred_cp_copy = null;
        ColPred empty_pred_cp = null;
        ColPred empty_pred_cp_copy = null;
        ColPred text_cp = null;
        ColPred text_cp_copy = null;
        ColPred empty_text_cp = null;
        ColPred empty_text_cp_copy = null;
        ColPred cp0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's
        completed = false;
        threwSystemErrorException = false;
        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);
            pve0_ID = db.addPredVE(pve0);
            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0_ID);

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            farg = new FloatFormalArg(db);
            float_mve.appendFormalArg(farg);
            db.vl.addElement(float_mve);
            float_mve_ID = float_mve.getID();

            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db);
            int_mve.appendFormalArg(farg);
            db.vl.addElement(int_mve);
            int_mve_ID = int_mve.getID();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            matrix_mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            matrix_mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            matrix_mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            matrix_mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            matrix_mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            matrix_mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve0.appendFormalArg(farg);
            db.vl.addElement(matrix_mve0);
            matrix_mve0_ID = matrix_mve0.getID();

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            matrix_mve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new UnTypedFormalArg(db, "<arg1>");
            matrix_mve2.appendFormalArg(farg);
            matrix_mve2.setVarLen(true);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();

            nominal_mve = new MatrixVocabElement(db, "nominal_mve");
            nominal_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            farg = new NominalFormalArg(db);
            nominal_mve.appendFormalArg(farg);
            db.vl.addElement(nominal_mve);
            nominal_mve_ID = nominal_mve.getID();

            pred_mve = new MatrixVocabElement(db, "pred_mve");
            pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
            farg = new PredFormalArg(db);
            pred_mve.appendFormalArg(farg);
            db.vl.addElement(pred_mve);
            pred_mve_ID = pred_mve.getID();

            text_mve = new MatrixVocabElement(db, "text_mve");
            text_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            farg = new TextStringFormalArg(db);
            text_mve.appendFormalArg(farg);
            db.vl.addElement(text_mve);
            text_mve_ID = text_mve.getID();

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0_ID == DBIndex.INVALID_ID ) ||
             ( float_mve == null ) ||
             ( float_mve.getType() != MatrixVocabElement.MatrixType.FLOAT ) ||
             ( float_mve_ID == DBIndex.INVALID_ID ) ||
             ( int_mve == null ) ||
             ( int_mve.getType() != MatrixVocabElement.MatrixType.INTEGER ) ||
             ( int_mve_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 7 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 3 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
             ( nominal_mve == null ) ||
             ( nominal_mve.getType() != MatrixVocabElement.MatrixType.NOMINAL ) ||
             ( nominal_mve_ID == DBIndex.INVALID_ID ) ||
             ( pred_mve == null ) ||
             ( pred_mve.getType() != MatrixVocabElement.MatrixType.PREDICATE ) ||
             ( pred_mve_ID == DBIndex.INVALID_ID ) ||
             ( text_mve == null ) ||
             ( text_mve.getType() != MatrixVocabElement.MatrixType.TEXT ) ||
             ( text_mve_ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }


                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0_ID == INVALID_ID.\n");
                }


                if ( float_mve == null )
                {
                    outStream.print("creation of float_mve failed.\n");
                }
                else if ( float_mve.getType() !=
                        MatrixVocabElement.MatrixType.FLOAT )
                {
                    outStream.print("unexpected float_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("float_mve_ID == INVALID_ID.\n");
                }


                if ( int_mve == null )
                {
                    outStream.print("creation of int_mve failed.\n");
                }
                else if ( int_mve.getType() !=
                        MatrixVocabElement.MatrixType.INTEGER )
                {
                    outStream.print("unexpected int_mve.getType().\n");
                }

                if ( float_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("int_mve_ID == INVALID_ID.\n");
                }


                if ( matrix_mve0 == null )
                {
                    outStream.print("creation of matrix_mve0 failed.\n");
                }
                else if ( matrix_mve0.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve0.getType().\n");
                }
                else if ( matrix_mve0.getNumFormalArgs() != 7 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve0_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve0_ID == INVALID_ID.\n");
                }


                if ( matrix_mve1 == null )
                {
                    outStream.print("creation of matrix_mve1 failed.\n");
                }
                else if ( matrix_mve1.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve1.getType().\n");
                }
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
                }


                if ( matrix_mve2 == null )
                {
                    outStream.print("creation of matrix_mve2 failed.\n");
                }
                else if ( matrix_mve2.getType() !=
                        MatrixVocabElement.MatrixType.MATRIX )
                {
                    outStream.print("unexpected matrix_mve2.getType().\n");
                }
                else if ( matrix_mve2.getNumFormalArgs() != 1 )
                {
                    outStream.printf("matrix_mve0.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve0.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2 == INVALID_ID.\n");
                }


                if ( nominal_mve == null )
                {
                    outStream.print("creation of nominal_mve failed.\n");
                }
                else if ( nominal_mve.getType() !=
                        MatrixVocabElement.MatrixType.NOMINAL )
                {
                    outStream.print("unexpected nominal_mve.getType().\n");
                }

                if ( nominal_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("nominal_mve_ID == INVALID_ID.\n");
                }


                if ( pred_mve == null )
                {
                    outStream.print("creation of pred_mve failed.\n");
                }
                else if ( pred_mve.getType() !=
                        MatrixVocabElement.MatrixType.PREDICATE )
                {
                    outStream.print("unexpected pred_mve.getType().\n");
                }

                if ( pred_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pred_mve_ID == INVALID_ID.\n");
                }


                if ( text_mve == null )
                {
                    outStream.print("creation of text_mve failed.\n");
                }
                else if ( text_mve.getType() !=
                        MatrixVocabElement.MatrixType.TEXT )
                {
                    outStream.print("unexpected text_mve.getType().\n");
                }

                if ( text_mve_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("text_mve_ID == INVALID_ID.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of test mve's failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "mve setup threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // having set up a selection of test mve's, now try to allocate some
        // matricies.  Use toString and toDBString to verify that they are
        // initialized correctly.

        if ( failures == 0 )
        {
            String float_cp_string =
                    "float_mve(11, 00:00:00:011, 00:00:11:000, 11.0)";
            String int_cp_string =
                    "int_mve(22, 00:00:00:022, 00:00:22:000, 22)";
            String matrix_cp0_string =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, " +
                                "a_nominal, pve0(<arg1>, <arg2>), " +
                                "\"q-string\", 00:00:01:000, <untyped>)";
            String matrix_cp1_string =
                    "matrix_mve1(34, 00:00:00:034, 00:00:34:000, " +
                                "\" a q string \", <arg2>, 88)";
            String matrix_cp2_string =
                    "matrix_mve2(35, 00:00:00:035, 00:00:35:000, <arg1>)";
            String nominal_cp_string =
                    "nominal_mve(44, 00:00:00:044, 00:00:44:000, another_nominal)";
            String pred_cp_string =
                    "pred_mve(55, 00:00:00:055, 00:00:55:000, pve0(<arg1>, <arg2>))";
            String text_cp_string =
                    "text_mve(66, 00:00:01:006, 00:01:06:000, a text string)";
            String float_cp_DBstring =
                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType INTEGER) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType UNTYPED) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(Matrix (mveID 4) " +
//                            "(varLen false) " +
//                            "(argList ((FloatDataValue (id 0) " +
//                                                      "(itsFargID 5) " +
//                                                      "(itsFargType FLOAT) " +
//                                                      "(itsCellID 0) " +
//                                                      "(itsValue 11.0) " +
//                                                      "(subRange false) " +
//                                                      "(minVal 0.0) " +
//                                                      "(maxVal 0.0))))))";
            String int_cp_DBstring =
                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType UNTYPED) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:022)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:22:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 22) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 10) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((IntDataValue (id 0) " +
//                                    "(itsFargID 11) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 22) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0))))))";
            String matrix_cp0_DBstring =
                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType INTEGER) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType UNTYPED) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(Matrix (mveID 16) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((FloatDataValue (id 0) " +
//                                    "(itsFargID 17) " +
//                                    "(itsFargType FLOAT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 1.0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0.0) " +
//                                    "(maxVal 0.0)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 18) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 2) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0)), " +
//                                "(NominalDataValue (id 0) " +
//                                    "(itsFargID 19) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a_nominal) " +
//                                    "(subRange false)), " +
//                                "(PredDataValue (id 0) " +
//                                    "(itsFargID 20) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg1>) " +
//                                                    "(subRange false)), " +
//                                                "(UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 3) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg2>) " +
//                                                    "(subRange false))))))) " +
//                                            "(subRange false)), " +
//                                "(QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 21) " +
//                                    "(itsFargType QUOTE_STRING) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue q-string) " +
//                                    "(subRange false)), " +
//                                "(TimeStampDataValue (id 0) " +
//                                    "(itsFargID 22) " +
//                                    "(itsFargType TIME_STAMP) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue (60,00:00:01:000)) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 23) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <untyped>) " +
//                                    "(subRange false))))))";
            String matrix_cp1_DBstring =
                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType INTEGER) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType UNTYPED) (itsCellID 0) (itsValue 34) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:034)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:34:000)) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue  a q string ) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (IntDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue 88) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 34) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 35) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue  a q string ) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 36) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg2>) " +
//                                    "(subRange false)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 37) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 88) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0))))))";
            String matrix_cp2_DBstring =
                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType INTEGER) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType UNTYPED) (itsCellID 0) (itsValue 35) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:035)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:35:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(Matrix (mveID 44) " +
//                            "(varLen true) " +
//                            "(argList " +
//                                "((UndefinedDataValue (id 0) " +
//                                    "(itsFargID 45) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg1>) " +
//                                    "(subRange false))))))";
            String nominal_cp_DBstring =
                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType INTEGER) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType UNTYPED) (itsCellID 0) (itsValue 44) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:044)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:44:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue another_nominal) (subRange false))))))";
//                    "(Matrix (mveID 50) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((NominalDataValue (id 0) " +
//                                    "(itsFargID 51) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue another_nominal) " +
//                                    "(subRange false))))))";
            String pred_cp_DBstring =
                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType INTEGER) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false))))))";
//                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType UNTYPED) (itsCellID 0) (itsValue 55) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:055)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:55:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 3) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false))))))) (subRange false))))))";
//                    "(Matrix (mveID 56) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((PredDataValue (id 0) " +
//                                    "(itsFargID 57) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue " +
//                                        "(predicate (id 0) " +
//                                            "(predID 1) " +
//                                            "(predName pve0) " +
//                                            "(varLen false) " +
//                                            "(argList " +
//                                                "((UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 2) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg1>) " +
//                                                    "(subRange false)), " +
//                                                "(UndefinedDataValue (id 0) " +
//                                                    "(itsFargID 3) " +
//                                                    "(itsFargType UNTYPED) " +
//                                                    "(itsCellID 0) " +
//                                                    "(itsValue <arg2>) " +
//                                                    "(subRange false))))))) " +
//                                    "(subRange false))))))";
            String text_cp_DBstring =
                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType INTEGER) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType UNTYPED) (itsCellID 0) (itsValue 66) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:01:006)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:01:06:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue a text string) (subRange false))))))";
//                    "(Matrix (mveID 62) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((TextStringDataValue (id 0) " +
//                                    "(itsFargID 63) " +
//                                    "(itsFargType TEXT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue a text string) " +
//                                    "(subRange false))))))";
            String empty_float_cp_string =
                    "float_mve(0, 00:00:00:000, 00:00:00:000, 0.0)";
            String empty_int_cp_string =
                    "int_mve(0, 00:00:00:000, 00:00:00:000, 0)";
            String empty_matrix_cp0_string =
                    "matrix_mve0(0, 00:00:00:000, 00:00:00:000, " +
                            "0.0, 0, , (), \"\", 00:00:00:000, <untyped>)";
            String empty_matrix_cp1_string =
                    "matrix_mve1(0, 00:00:00:000, 00:00:00:000, " +
                            "<arg1>, <arg2>, <arg3>)";
            String empty_matrix_cp2_string =
                    "matrix_mve2(0, 00:00:00:000, 00:00:00:000, <arg1>)";
            String empty_nominal_cp_string =
                    "nominal_mve(0, 00:00:00:000, 00:00:00:000, )";
            String empty_pred_cp_string =
                    "pred_mve(0, 00:00:00:000, 00:00:00:000, ())";
            String empty_text_cp_string =
                    "text_mve(0, 00:00:00:000, 00:00:00:000, )";
            String empty_float_cp_DBstring =
                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 6) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 7) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 8) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(colPred (id 0) (mveID 4) (mveName float_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 6) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 7) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (FloatDataValue (id 0) (itsFargID 9) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))";
//                    "(Matrix (mveID 4) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((FloatDataValue (id 0) " +
//                                    "(itsFargID 5) " +
//                                    "(itsFargType FLOAT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 0.0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0.0) " +
//                                    "(maxVal 0.0))))))";
            String empty_int_cp_DBstring =
                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 12) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 13) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 14) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(colPred (id 0) (mveID 10) (mveName int_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 12) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 13) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 14) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (IntDataValue (id 0) (itsFargID 15) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0))))))";
//                    "(Matrix (mveID 10) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((IntDataValue (id 0) " +
//                                    "(itsFargID 11) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0))))))";
            String empty_matrix_cp0_DBstring =
                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 24) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 25) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 26) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue <null>) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue ()) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue <null>) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 16) (mveName matrix_mve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 24) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 25) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 26) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (FloatDataValue (id 0) (itsFargID 27) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 28) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 29) (itsFargType NOMINAL) (itsCellID 0) (itsValue <null>) (subRange false)), (PredDataValue (id 0) (itsFargID 30) (itsFargType PREDICATE) (itsCellID 0) (itsValue ()) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 31) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue <null>) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 32) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false))))))";
//                    "(Matrix (mveID 16) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((FloatDataValue (id 0) " +
//                                    "(itsFargID 17) " +
//                                    "(itsFargType FLOAT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 0.0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0.0) " +
//                                    "(maxVal 0.0)), " +
//                                "(IntDataValue (id 0) " +
//                                    "(itsFargID 18) " +
//                                    "(itsFargType INTEGER) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue 0) " +
//                                    "(subRange false) " +
//                                    "(minVal 0) " +
//                                    "(maxVal 0)), " +
//                                "(NominalDataValue (id 0) " +
//                                    "(itsFargID 19) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <null>) " +
//                                    "(subRange false)), " +
//                                "(PredDataValue (id 0) " +
//                                    "(itsFargID 20) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue ()) " +
//                                    "(subRange false)), " +
//                                "(QuoteStringDataValue (id 0) " +
//                                    "(itsFargID 21) " +
//                                    "(itsFargType QUOTE_STRING) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <null>) " +
//                                    "(subRange false)), " +
//                                "(TimeStampDataValue (id 0) " +
//                                    "(itsFargID 22) " +
//                                    "(itsFargType TIME_STAMP) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue (60,00:00:00:000)) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 23) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <untyped>) " +
//                                    "(subRange false))))))";
            String empty_matrix_cp1_DBstring =
                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 38) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 39) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 40) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg3>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 34) (mveName matrix_mve1) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 38) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 39) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 40) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 41) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 42) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg2>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 43) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg3>) (subRange false))))))";
//                    "(Matrix (mveID 34) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((UndefinedDataValue (id 0) " +
//                                    "(itsFargID 35) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg1>) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 36) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg2>) " +
//                                    "(subRange false)), " +
//                                "(UndefinedDataValue (id 0) " +
//                                    "(itsFargID 37) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg3>) " +
//                                    "(subRange false))))))";
            String empty_matrix_cp2_DBstring =
                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((IntDataValue (id 0) (itsFargID 46) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 47) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 48) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 44) (mveName matrix_mve2) (varLen true) (argList ((UndefinedDataValue (id 0) (itsFargID 46) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 47) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 48) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 49) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg1>) (subRange false))))))";
//                    "(Matrix (mveID 44) " +
//                            "(varLen true) " +
//                            "(argList " +
//                                "((UndefinedDataValue (id 0) " +
//                                    "(itsFargID 45) " +
//                                    "(itsFargType UNTYPED) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <arg1>) " +
//                                    "(subRange false))))))";
            String empty_nominal_cp_DBstring =
                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 52) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 53) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 54) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue <null>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 50) (mveName nominal_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 52) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 53) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 54) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (NominalDataValue (id 0) (itsFargID 55) (itsFargType NOMINAL) (itsCellID 0) (itsValue <null>) (subRange false))))))";
//                    "(Matrix (mveID 50) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((NominalDataValue (id 0) " +
//                                    "(itsFargID 51) " +
//                                    "(itsFargType NOMINAL) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <null>) " +
//                                    "(subRange false))))))";
            String empty_pred_cp_DBstring =
                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 58) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 59) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 60) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue ()) (subRange false))))))";
//                    "(colPred (id 0) (mveID 56) (mveName pred_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 58) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 59) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 60) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (PredDataValue (id 0) (itsFargID 61) (itsFargType PREDICATE) (itsCellID 0) (itsValue ()) (subRange false))))))";
//                    "(Matrix (mveID 56) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((PredDataValue (id 0) " +
//                                    "(itsFargID 57) " +
//                                    "(itsFargType PREDICATE) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue ()) " +
//                                    "(subRange false))))))";
            String empty_text_cp_DBstring =
                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 64) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 65) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 66) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue <null>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 62) (mveName text_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 64) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 65) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 66) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (TextStringDataValue (id 0) (itsFargID 67) (itsFargType TEXT) (itsCellID 0) (itsValue <null>) (subRange false))))))";
//                    "(Matrix (mveID 62) " +
//                            "(varLen false) " +
//                            "(argList " +
//                                "((TextStringDataValue (id 0) " +
//                                    "(itsFargID 63) " +
//                                    "(itsFargType TEXT) " +
//                                    "(itsCellID 0) " +
//                                    "(itsValue <null>) " +
//                                    "(subRange false))))))";

            completed = false;
            threwSystemErrorException = false;
            try
            {
                float_cp_arg_list = new Vector<DataValue>();
                fargID = float_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 11);
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 11 * db.getTicks()));
                float_cp_arg_list.add(arg);
                fargID = float_mve.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 11.0);
                float_cp_arg_list.add(arg);
                float_cp = new ColPred(db, float_mve_ID, float_cp_arg_list);


                int_cp_arg_list = new Vector<DataValue>();
                fargID = int_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 22 * db.getTicks()));
                int_cp_arg_list.add(arg);
                fargID = int_mve.getCPFormalArg(3).getID();
                arg = new IntDataValue(db, fargID, 22);
                int_cp_arg_list.add(arg);
                int_cp = new ColPred(db, int_mve_ID, int_cp_arg_list);


                matrix_cp0_arg_list = new Vector<DataValue>();
                fargID = matrix_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 33);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 33 * db.getTicks()));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 60));
                matrix_cp0_arg_list.add(arg);
                fargID = matrix_mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve0.getCPFormalArg(9).getFargName());
                matrix_cp0_arg_list.add(arg);
                matrix_cp0 = new ColPred(db, matrix_mve0_ID,
                                         matrix_cp0_arg_list);


                matrix_cp1_arg_list = new Vector<DataValue>();
                fargID = matrix_mve1.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 34);
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 34 * db.getTicks()));
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(3).getID();
                arg = new QuoteStringDataValue(db, fargID, " a q string ");
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(4).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve1.getCPFormalArg(4).getFargName());
                matrix_cp1_arg_list.add(arg);
                fargID = matrix_mve1.getCPFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                matrix_cp1_arg_list.add(arg);
                matrix_cp1 = new ColPred(db, matrix_mve1_ID,
                                         matrix_cp1_arg_list);


                matrix_cp2_arg_list = new Vector<DataValue>();
                fargID = matrix_mve2.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 35);
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 35 * db.getTicks()));
                matrix_cp2_arg_list.add(arg);
                fargID = matrix_mve2.getCPFormalArg(3).getID();
                arg = new UndefinedDataValue(db, fargID,
                                     matrix_mve2.getCPFormalArg(3).getFargName());
                matrix_cp2_arg_list.add(arg);
                matrix_cp2 = new ColPred(db, matrix_mve2_ID,
                                         matrix_cp2_arg_list);


                nominal_cp_arg_list = new Vector<DataValue>();
                fargID = nominal_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 44);
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 44 * db.getTicks()));
                nominal_cp_arg_list.add(arg);
                fargID = nominal_mve.getCPFormalArg(3).getID();
                arg = new NominalDataValue(db, fargID, "another_nominal");
                nominal_cp_arg_list.add(arg);
                nominal_cp = new ColPred(db, nominal_mve_ID,
                                         nominal_cp_arg_list);


                pred_cp_arg_list = new Vector<DataValue>();
                fargID = pred_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 55);
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 55 * db.getTicks()));
                pred_cp_arg_list.add(arg);
                fargID = pred_mve.getCPFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0_ID));
                pred_cp_arg_list.add(arg);
                pred_cp = new ColPred(db, pred_mve_ID, pred_cp_arg_list);


                text_cp_arg_list = new Vector<DataValue>();
                fargID = text_mve.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 66);
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 66 * db.getTicks()));
                text_cp_arg_list.add(arg);
                fargID = text_mve.getCPFormalArg(3).getID();
                arg = new TextStringDataValue(db, fargID, "a text string");
                text_cp_arg_list.add(arg);
                text_cp = new ColPred(db, text_mve_ID, text_cp_arg_list);


                empty_float_cp   = new ColPred(db, float_mve_ID);
                empty_int_cp     = new ColPred(db, int_mve_ID);
                empty_matrix_cp0 = new ColPred(db, matrix_mve0_ID);
                empty_matrix_cp1 = new ColPred(db, matrix_mve1_ID);
                empty_matrix_cp2 = new ColPred(db, matrix_mve2_ID);
                empty_nominal_cp = new ColPred(db, nominal_mve_ID);
                empty_pred_cp    = new ColPred(db, pred_mve_ID);
                empty_text_cp    = new ColPred(db, text_mve_ID);


                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list == null ) ||
                 ( float_cp == null ) ||
                 ( int_cp_arg_list == null ) ||
                 ( int_cp == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1_arg_list == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2_arg_list == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp_arg_list == null ) ||
                 ( nominal_cp == null ) ||
                 ( pred_cp_arg_list == null ) ||
                 ( pred_cp == null ) ||
                 ( text_cp_arg_list == null ) ||
                 ( text_cp == null ) ||
                 ( empty_float_cp == null ) ||
                 ( empty_int_cp == null ) ||
                 ( empty_matrix_cp0 == null ) ||
                 ( empty_matrix_cp1 == null ) ||
                 ( empty_matrix_cp2 == null ) ||
                 ( empty_nominal_cp == null ) ||
                 ( empty_pred_cp == null ) ||
                 ( empty_text_cp == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of float_cp_arg_list failed.\n");
                    }

                    if ( float_cp == null )
                    {
                        outStream.printf("allocation of float_cp failed*.\n");
                    }

                    if ( int_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_cp_arg_list failed.\n");
                    }

                    if ( int_cp == null )
                    {
                        outStream.printf("allocation of int_cp failed.\n");
                    }

                    if ( matrix_cp0_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp0_arg_list failed.\n");
                    }

                    if ( matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp0 failed.\n");
                    }

                    if ( matrix_cp1_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp1_arg_list failed.\n");
                    }

                    if ( matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp1 failed.\n");
                    }

                    if ( matrix_cp2_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of matrix_cp2_arg_list failed.\n");
                    }

                    if ( matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of matrix_cp2 failed.\n");
                    }

                    if ( nominal_cp_arg_list == null )
                    {
                        outStream.printf(
                            "allocation of nominal_cp_arg_list failed.\n");
                    }

                    if ( nominal_cp == null )
                    {
                        outStream.printf(
                                "allocation of nominal_cp failed.\n");
                    }

                    if ( pred_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp_arg_list failed.\n");
                    }

                    if ( pred_cp == null )
                    {
                        outStream.printf("allocation of pred_cp failed.\n");
                    }

                    if ( text_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_cp_arg_list failed.\n");
                    }

                    if ( text_cp == null )
                    {
                        outStream.printf("allocation of text_cp failed.\n");
                    }

                    if ( empty_float_cp == null )
                    {
                        outStream.printf(
                                "allocation of empty_float_cp failed.\n");
                    }

                    if ( empty_int_cp == null )
                    {
                        outStream.printf(
                                "allocation of empty_int_cp failed.\n");
                    }

                    if ( empty_matrix_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of empty_matrix_cp0 failed.\n");
                    }

                    if ( empty_matrix_cp1 == null )
                    {
                        outStream.printf(
                                "allocation of empty_matrix_cp1 failed.\n");
                    }

                    if ( empty_matrix_cp2 == null )
                    {
                        outStream.printf(
                                "allocation of empty_matrix_cp2 failed.\n");
                    }

                    if ( empty_nominal_cp == null )
                    {
                        outStream.printf(
                                "allocation of empty_nominal_cp failed.\n");
                    }

                    if ( empty_pred_cp == null )
                    {
                        outStream.printf(
                                "allocation of empty_pred_cp failed.\n");
                    }

                    if ( empty_text_cp == null )
                    {
                        outStream.printf(
                                "allocation of empty_text_cp failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                            "Creation of test col preds failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("col pred creation threw a " +
                                         "SystemErrorException: %s.\n",
                                         systemErrorExceptionString);
                    }
                }
            }
            else if ( ( float_cp.toString().
                        compareTo(float_cp_string) != 0 ) ||
                      ( int_cp.toString().
                        compareTo(int_cp_string) != 0 ) ||
                      ( matrix_cp0.toString().
                        compareTo(matrix_cp0_string) != 0 ) ||
                      ( matrix_cp1.toString().
                        compareTo(matrix_cp1_string) != 0 ) ||
                      ( matrix_cp2.toString().
                        compareTo(matrix_cp2_string) != 0 ) ||
                      ( nominal_cp.toString().
                        compareTo(nominal_cp_string) != 0 ) ||
                      ( pred_cp.toString().
                        compareTo(pred_cp_string) != 0 ) ||
                      ( text_cp.toString().
                        compareTo(text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toString().
                         compareTo(float_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toString(): %s\n",
                                float_cp.toString());
                    }

                    if ( int_cp.toString().
                         compareTo(int_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toString(): %s\n",
                                int_cp.toString());
                    }

                    if ( matrix_cp0.toString().
                         compareTo(matrix_cp0_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toString(): %s\n",
                                matrix_cp0.toString());
                    }

                    if ( matrix_cp1.toString().
                         compareTo(matrix_cp1_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toString(): %s\n",
                                matrix_cp1.toString());
                    }

                    if ( matrix_cp2.toString().
                         compareTo(matrix_cp2_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toString(): %s\n",
                                matrix_cp2.toString());
                    }

                    if ( nominal_cp.toString().
                         compareTo(nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toString(): %s\n",
                                nominal_cp.toString());
                    }

                    if ( pred_cp.toString().
                         compareTo(pred_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toString(): %s\n",
                                pred_cp.toString());
                    }

                    if ( text_cp.toString().
                         compareTo(text_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toString(): %s\n",
                                text_cp.toString());
                    }
                }
            }
            else if ( ( float_cp.toDBString().
                        compareTo(float_cp_DBstring) != 0 ) ||
                      ( int_cp.toDBString().
                        compareTo(int_cp_DBstring) != 0 ) ||
                      ( matrix_cp0.toDBString().
                        compareTo(matrix_cp0_DBstring) != 0 ) ||
                      ( matrix_cp1.toDBString().
                        compareTo(matrix_cp1_DBstring) != 0 ) ||
                      ( matrix_cp2.toDBString().
                        compareTo(matrix_cp2_DBstring) != 0 ) ||
                      ( nominal_cp.toDBString().
                        compareTo(nominal_cp_DBstring) != 0 ) ||
                      ( pred_cp.toDBString().
                        compareTo(pred_cp_DBstring) != 0 ) ||
                      ( text_cp.toDBString().
                        compareTo(text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( float_cp.toDBString().
                         compareTo(float_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected float_cp.toDBString(): %s\n",
                                float_cp.toDBString());
                    }

                    if ( int_cp.toDBString().
                         compareTo(int_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected int_cp.toDBString(): %s\n",
                                int_cp.toDBString());
                    }

                    if ( matrix_cp0.toDBString().
                         compareTo(matrix_cp0_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp0.toDBString(): %s\n",
                                matrix_cp0.toDBString());
                    }

                    if ( matrix_cp1.toDBString().
                         compareTo(matrix_cp1_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp1.toDBString(): %s\n",
                                matrix_cp1.toDBString());
                    }

                    if ( matrix_cp2.toDBString().
                         compareTo(matrix_cp2_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected matrix_cp2.toDBString(): %s\n",
                                matrix_cp2.toDBString());
                    }

                    if ( nominal_cp.toDBString().
                         compareTo(nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected nominal_cp.toDBString(): %s\n",
                                nominal_cp.toDBString());
                    }

                    if ( pred_cp.toDBString().
                         compareTo(pred_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected pred_cp.toDBString(): %s\n",
                                pred_cp.toDBString());
                    }

                    if ( text_cp.toDBString().
                         compareTo(text_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected text_cp.toDBString(): %s\n",
                                text_cp.toDBString());
                    }
                }
            }
            else if ( ( empty_float_cp.toString().
                        compareTo(empty_float_cp_string) != 0 ) ||
                      ( empty_int_cp.toString().
                        compareTo(empty_int_cp_string) != 0 ) ||
                      ( empty_matrix_cp0.toString().
                        compareTo(empty_matrix_cp0_string) != 0 ) ||
                      ( empty_matrix_cp1.toString().
                        compareTo(empty_matrix_cp1_string) != 0 ) ||
                      ( empty_matrix_cp2.toString().
                        compareTo(empty_matrix_cp2_string) != 0 ) ||
                      ( empty_nominal_cp.toString().
                        compareTo(empty_nominal_cp_string) != 0 ) ||
                      ( empty_pred_cp.toString().
                        compareTo(empty_pred_cp_string) != 0 ) ||
                      ( empty_text_cp.toString().
                        compareTo(empty_text_cp_string) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( empty_float_cp.toString().
                         compareTo(empty_float_cp_string) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_float_cp.toString(): %s\n",
                            empty_float_cp.toString());
                    }

                    if ( empty_int_cp.toString().
                         compareTo(empty_int_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_int_cp.toString(): %s\n",
                                empty_int_cp.toString());
                    }

                    if ( empty_matrix_cp0.toString().
                         compareTo(empty_matrix_cp0_string) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_cp0.toString(): %s\n",
                            empty_matrix_cp0.toString());
                    }

                    if ( empty_matrix_cp1.toString().
                         compareTo(empty_matrix_cp1_string) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_cp1.toString(): %s\n",
                            empty_matrix_cp1.toString());
                    }

                    if ( empty_matrix_cp2.toString().
                         compareTo(empty_matrix_cp2_string) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_cp2.toString(): %s\n",
                            empty_matrix_cp2.toString());
                    }

                    if ( empty_nominal_cp.toString().
                         compareTo(empty_nominal_cp_string) != 0 )
                    {
                        outStream.printf(
                             "unexpected empty_nominal_cp.toString(): %s\n",
                             empty_nominal_cp.toString());
                    }

                    if ( empty_pred_cp.toString().
                         compareTo(empty_pred_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_pred_cp.toString(): %s\n",
                                empty_pred_cp.toString());
                    }

                    if ( empty_text_cp.toString().
                         compareTo(empty_text_cp_string) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_text_cp.toString(): %s\n",
                                empty_text_cp.toString());
                    }
                }
            }
            else if ( ( empty_float_cp.toDBString().
                        compareTo(empty_float_cp_DBstring) != 0 ) ||
                      ( empty_int_cp.toDBString().
                        compareTo(empty_int_cp_DBstring) != 0 ) ||
                      ( empty_matrix_cp0.toDBString().
                        compareTo(empty_matrix_cp0_DBstring) != 0 ) ||
                      ( empty_matrix_cp1.toDBString().
                        compareTo(empty_matrix_cp1_DBstring) != 0 ) ||
                      ( empty_matrix_cp2.toDBString().
                        compareTo(empty_matrix_cp2_DBstring) != 0 ) ||
                      ( empty_nominal_cp.toDBString().
                        compareTo(empty_nominal_cp_DBstring) != 0 ) ||
                      ( empty_pred_cp.toDBString().
                        compareTo(empty_pred_cp_DBstring) != 0 ) ||
                      ( empty_text_cp.toDBString().
                        compareTo(empty_text_cp_DBstring) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( empty_float_cp.toDBString().
                         compareTo(empty_float_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_float_cp.toDBString(): %s\n",
                            empty_float_cp.toDBString());
                    }

                    if ( empty_int_cp.toDBString().
                         compareTo(empty_int_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_int_cp.toDBString(): %s\n",
                                empty_int_cp.toDBString());
                    }

                    if ( empty_matrix_cp0.toDBString().
                         compareTo(empty_matrix_cp0_DBstring) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_cp0.toDBString(): %s\n",
                            empty_matrix_cp0.toDBString());
                    }

                    if ( empty_matrix_cp1.toDBString().
                         compareTo(empty_matrix_cp1_DBstring) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_cp1.toDBString(): %s\n",
                            empty_matrix_cp1.toDBString());
                    }

                    if ( empty_matrix_cp2.toDBString().
                         compareTo(empty_matrix_cp2_DBstring) != 0 )
                    {
                        outStream.printf(
                            "unexpected empty_matrix_cp2.toDBString(): %s\n",
                            empty_matrix_cp2.toDBString());
                    }

                    if ( empty_nominal_cp.toDBString().
                         compareTo(empty_nominal_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                             "unexpected empty_nominal_cp.toDBString(): %s\n",
                             empty_nominal_cp.toDBString());
                    }

                    if ( empty_pred_cp.toDBString().
                         compareTo(empty_pred_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_pred_cp.toDBString(): %s\n",
                                empty_pred_cp.toDBString());
                    }

                    if ( empty_text_cp.toDBString().
                         compareTo(empty_text_cp_DBstring) != 0 )
                    {
                        outStream.printf(
                                "unexpected empty_text_cp.toDBString(): %s\n",
                                empty_text_cp.toDBString());
                    }
                }
            }
        }

        // setup is complete -- now try to make the copies
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            try
            {
                empty_float_cp_copy   = new ColPred(empty_float_cp);
                empty_int_cp_copy     = new ColPred(empty_int_cp);
                empty_matrix_cp0_copy = new ColPred(empty_matrix_cp0);
                empty_matrix_cp1_copy = new ColPred(empty_matrix_cp1);
                empty_matrix_cp2_copy = new ColPred(empty_matrix_cp2);
                empty_nominal_cp_copy = new ColPred(empty_nominal_cp);
                empty_pred_cp_copy    = new ColPred(empty_pred_cp);
                empty_text_cp_copy    = new ColPred(empty_text_cp);

                float_cp_copy   = new ColPred(float_cp);
                int_cp_copy     = new ColPred(int_cp);
                matrix_cp0_copy = new ColPred(matrix_cp0);
                matrix_cp1_copy = new ColPred(matrix_cp1);
                matrix_cp2_copy = new ColPred(matrix_cp2);
                nominal_cp_copy = new ColPred(nominal_cp);
                pred_cp_copy    = new ColPred(pred_cp);
                text_cp_copy    = new ColPred(text_cp);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( empty_float_cp_copy == null ) ||
                 ( empty_int_cp_copy == null ) ||
                 ( empty_matrix_cp0_copy == null ) ||
                 ( empty_matrix_cp1_copy == null ) ||
                 ( empty_matrix_cp2_copy == null ) ||
                 ( empty_nominal_cp_copy == null ) ||
                 ( empty_pred_cp_copy == null ) ||
                 ( empty_text_cp_copy == null ) ||
                 ( float_cp_copy == null ) ||
                 ( int_cp_copy == null ) ||
                 ( matrix_cp0_copy == null ) ||
                 ( matrix_cp1_copy == null ) ||
                 ( matrix_cp2_copy == null ) ||
                 ( nominal_cp_copy == null ) ||
                 ( pred_cp_copy == null ) ||
                 ( text_cp_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( empty_float_cp_copy == null )
                {
                    outStream.printf(
                            "empty_float_cp_copy allocation failed.\n");
                }

                if ( empty_int_cp_copy == null )
                {
                    outStream.printf(
                            "empty_int_cp_copy allocation failed.\n");
                }

                if ( empty_matrix_cp0_copy == null )
                {
                    outStream.printf(
                            "empty_matrix_cp0_copy allocation failed.\n");
                }

                if ( empty_matrix_cp1_copy == null )
                {
                    outStream.printf(
                            "empty_matrix_cp1_copy allocation failed.\n");
                }

                if ( empty_matrix_cp2_copy == null )
                {
                    outStream.printf(
                            "empty_matrix_cp2_copy allocation failed.\n");
                }

                if ( empty_nominal_cp_copy == null )
                {
                    outStream.printf(
                            "empty_nominal_cp_copy allocation failed.\n");
                }

                if ( empty_pred_cp_copy == null )
                {
                    outStream.printf(
                            "empty_pred_cp_copy allocation failed.\n");
                }

                if ( empty_text_cp_copy == null )
                {
                    outStream.printf(
                            "empty_text_cp_copy allocation failed.\n");
                }

                if ( float_cp_copy == null )
                {
                    outStream.printf(
                            "float_cp_copy allocation failed.\n");
                }

                if ( int_cp_copy == null )
                {
                    outStream.printf(
                            "int_cp_copy allocation failed.\n");
                }

                if ( matrix_cp0_copy == null )
                {
                    outStream.printf(
                            "matrix_cp0_copy allocation failed.\n");
                }

                if ( matrix_cp1_copy == null )
                {
                    outStream.printf(
                            "matrix_cp1_copy allocation failed.\n");
                }

                if ( matrix_cp2_copy == null )
                {
                    outStream.printf(
                            "matrix_cp2_copy allocation failed.\n");
                }

                if ( nominal_cp_copy == null )
                {
                    outStream.printf(
                            "nominal_cp_copy allocation failed.\n");
                }

                if ( pred_cp_copy == null )
                {
                    outStream.printf(
                            "pred_cp_copy allocation failed.\n");
                }

                if ( text_cp_copy == null )
                {
                    outStream.printf(
                            "text_cp_copy allocation failed.\n");
                }

                if ( ! completed )
                {
                    outStream.print("Creation of copies failed to complete");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "matrix copy threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                }
            }
        }

        // if failures == 0, check to see if the copies are valid */
        if ( failures == 0 )
        {
            failures += VerifyColPredCopy(empty_float_cp,
                                          empty_float_cp_copy,
                                          outStream,
                                          verbose,
                                          "empty_float_cp",
                                          "empty_float_cp_copy");

            failures += VerifyColPredCopy(empty_int_cp,
                                          empty_int_cp_copy,
                                          outStream,
                                          verbose,
                                          "empty_int_cp",
                                          "empty_int_cp_copy");

            failures += VerifyColPredCopy(empty_matrix_cp0,
                                          empty_matrix_cp0_copy,
                                          outStream,
                                          verbose,
                                          "empty_matrix_cp0",
                                          "empty_matrix_cp0_copy");

            failures += VerifyColPredCopy(empty_matrix_cp1,
                                          empty_matrix_cp1_copy,
                                          outStream,
                                          verbose,
                                          "empty_matrix_cp1",
                                          "empty_matrix_cp1_copy");

            failures += VerifyColPredCopy(empty_matrix_cp2,
                                          empty_matrix_cp2_copy,
                                          outStream,
                                          verbose,
                                          "empty_matrix_cp2",
                                          "empty_matrix_cp2_copy");

            failures += VerifyColPredCopy(empty_nominal_cp,
                                          empty_nominal_cp_copy,
                                          outStream,
                                          verbose,
                                          "empty_nominal_cp",
                                          "empty_nominal_cp_copy");

            failures += VerifyColPredCopy(empty_pred_cp,
                                          empty_pred_cp_copy,
                                          outStream,
                                          verbose,
                                          "empty_pred_cp",
                                          "empty_pred_cp_copy");

            failures += VerifyColPredCopy(empty_text_cp,
                                          empty_text_cp_copy,
                                          outStream,
                                          verbose,
                                          "empty_text_cp",
                                          "empty_text_cp_copy");

            failures += VerifyColPredCopy(float_cp,
                                          float_cp_copy,
                                          outStream,
                                          verbose,
                                          "float_cp",
                                          "float_cp_copy");

            failures += VerifyColPredCopy(int_cp,
                                          int_cp_copy,
                                          outStream,
                                          verbose,
                                          "int_cp",
                                          "int_cp_copy");

            failures += VerifyColPredCopy(matrix_cp0,
                                          matrix_cp0_copy,
                                          outStream,
                                          verbose,
                                          "matrix_cp0",
                                          "matrix_cp0_copy");

            failures += VerifyColPredCopy(matrix_cp1,
                                          matrix_cp1_copy,
                                          outStream,
                                          verbose,
                                          "matrix_cp1",
                                          "matrix_cp1_copy");

            failures += VerifyColPredCopy(matrix_cp2,
                                          matrix_cp2_copy,
                                          outStream,
                                          verbose,
                                          "matrix_cp2",
                                          "matrix_cp2_copy");

            failures += VerifyColPredCopy(nominal_cp,
                                          nominal_cp_copy,
                                          outStream,
                                          verbose,
                                          "nominal_cp",
                                          "nominal_cp_copy");

            failures += VerifyColPredCopy(pred_cp,
                                          pred_cp_copy,
                                          outStream,
                                          verbose,
                                          "pred_cp",
                                          "pred_cp_copy");

            failures += VerifyColPredCopy(text_cp,
                                          text_cp_copy,
                                          outStream,
                                          verbose,
                                          "text_cp",
                                          "text_cp_copy");
        }

        /* now verify that the copy constructor fails when passed an invalid
         * reference to a ColPred.  For now, this just means passing in a
         * null.
         */
        cp0 = null;
        completed = false;
        threwSystemErrorException = false;

        try
        {
            cp0 = new ColPred((ColPred)null);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( cp0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( cp0 != null )
                {
                    outStream.print("new ColPred(null) != null.\n");
                }

                if ( completed )
                {
                    outStream.print("new ColPred(null) completed.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new ColPred(null) " +
                                    "didn't throw a SystemErrorException.\n");
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

    } /* ColPred::TestCopyConstructor() */


    /**
     * TestGetArgCopy()
     *
     * Given a ColPred, and an argument number, verify that getArgCopy()
     * returns a copy of the target argument if the argNum parameter refers
     * to a parameter, returns null if argNum is greater than the number
     * of parameters, and fails with a system error is argNum is negative.
     *
     * Return the number of failures detected.
     *
     *                                              JRM -- 10/3/08
     *
     * Changes:
     *
     *    - None.
     */

    private static int TestGetArgCopy(ColPred cp,
                                     int argNum,
                                     int testNum,
                                     expectedResult er,
                                     String cpName,
                                     java.io.PrintStream outStream,
                                     boolean verbose)
        throws SystemErrorException
    {
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        DataValue copy = null;

        try
        {
            copy = cp.getArgCopy(argNum);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( argNum < 0 )
        {
            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.printf("%d: %s.getArgCopy(%d) completed.\n",
                                         testNum, cpName, argNum);
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("%d: %s.getArgCopy(%d) failed to throw " +
                                "a system error exception.\n",
                                testNum, cpName, argNum);
                    }
                }
            }
            else if ( er != expectedResult.system_error )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%d: expected/actual result mismatch (%s/%s).\n",
                            testNum, er.toString(),
                            expectedResult.system_error.toString());
                }
            }
        }
        else if ( argNum >= cp.getNumArgs() )
        {
            if ( ( copy != null ) ||
                 ( ! completed  ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copy != null )
                    {
                        outStream.printf("%d: %s.getArgCopy(%d >= numArgs) " +
                                "failed to return null.\n",
                                testNum, cpName, argNum);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%d: %s.getArgCopy(%d >= numArgs) " +
                                "failed to completed.\n",
                                testNum, cpName, argNum);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "%d: %s.getArgCopy(%d >= numArgs) threw " +
                            "an unexpected system error exception: \"%s\".\n",
                            testNum, cpName, argNum, systemErrorExceptionString);
                    }
                }
            }
            else if ( er != expectedResult.return_null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%d: expected/actual result mismatch (%s/%s).\n",
                            testNum, er.toString(),
                            expectedResult.return_null.toString());
                }
            }
        }
        else
        {
            failures += DataValue.VerifyDVCopy(cp.argList.get(argNum),
                                               copy,
                                               outStream,
                                               verbose,
                                               cpName + "(" + argNum + ")",
                                               cpName + "(" + argNum + ") copy");

            if ( er != expectedResult.succeed )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%d: expected/actual result mismatch (%s/%s).\n",
                            testNum, er.toString(),
                            expectedResult.succeed.toString());
                }
            }
        }

        return failures;

    } /* ColPred::TestGetArgCopy() */


    /**
     * TestToStringMethods()
     *
     * Run a battery of tests on the to string methods for this
     * class.
     *
     *                                              JRM -- 10/3/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestToStringMethods(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures                 = 0;
        Database db                  = null;
        PredicateVocabElement pve    = null;
        MatrixVocabElement mve0      = null;
        MatrixVocabElement mve1      = null;
        long fargID                  = DBIndex.INVALID_ID;
        long pveID                   = DBIndex.INVALID_ID;
        long mve0ID                  = DBIndex.INVALID_ID;
        long mve1ID                  = DBIndex.INVALID_ID;
        FormalArgument farg          = null;
        Vector<DataValue> argList0   = null;
        Vector<DataValue> argList1   = null;
        DataValue arg                = null;
        FloatDataValue floatArg0     = null;
        FloatDataValue floatArg1     = null;
        IntDataValue intArg0         = null;
        IntDataValue intArg1         = null;
        IntDataValue intArg2         = null;
        NominalDataValue nominalArg0 = null;
        NominalDataValue nominalArg1 = null;
        PredDataValue predArg0       = null;
        PredDataValue predArg1       = null;
        QuoteStringDataValue qsArg0  = null;
        QuoteStringDataValue qsArg1  = null;
        TimeStampDataValue tsArg0    = null;
        TimeStampDataValue tsArg1    = null;
        UndefinedDataValue undefArg0 = null;
        UndefinedDataValue undefArg1 = null;
        ColPred cp0                  = null;
        ColPred cp1                  = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database, mve's and pve's
        threwSystemErrorException = false;
        completed = false;

        try
        {
            db = new ODBCDatabase();

            pve = new PredicateVocabElement(db, "pve");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve.appendFormalArg(farg);
            pveID = db.addPredVE(pve);

            // get a copy of the databases version of pve with ids assigned
            pve = db.getPredVE(pveID);

            mve0 = new MatrixVocabElement(db, "mve0");
            mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new FloatFormalArg(db, "<float>");
            mve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            mve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            mve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            mve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            mve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            mve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            mve0.appendFormalArg(farg);
            mve0.setVarLen(true);
            db.vl.addElement(mve0);
            mve0ID = mve0.getID();


            mve1 = new MatrixVocabElement(db, "mve1");
            mve1.setType(MatrixVocabElement.MatrixType.INTEGER);
            farg = new IntFormalArg(db, "<arg>");
            mve1.appendFormalArg(farg);
            db.vl.addElement(mve1);
            mve1ID = mve1.getID();

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve == null ) ||
             ( pveID == DBIndex.INVALID_ID ) ||
             ( mve0 == null ) ||
             ( mve0ID == DBIndex.INVALID_ID ) ||
             ( mve1 == null ) ||
             ( mve1ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }

                if ( pve == null )
                {
                    outStream.print("creation of pve failed.\n");
                }

                if ( pveID == DBIndex.INVALID_ID )
                {
                    outStream.print("pveID not initialized.\n");
                }

                if ( mve0 == null )
                {
                    outStream.print("creation of mve0 failed.\n");
                }

                if ( mve0ID == DBIndex.INVALID_ID )
                {
                    outStream.print("mve0ID not initialized.\n");
                }

                if ( mve1 == null )
                {
                    outStream.print("creation of mve1 failed.\n");
                }

                if ( mve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("mve1ID not initialized.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("mve or pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            SystemErrorExceptionString);
                }
            }
        }

        // Setup the matricies that we will used for the toString and
        // toDBString tests.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            String testString0 =
                    "mve0(1, 00:00:00:001, 00:00:01:000, 1.0, 2, a_nominal, " +
                            "pve(<arg>), \"q-string\", 00:00:00:000, <untyped>)";
            String testDBString0 =
                    "(colPred (id 0) (mveID 3) (mveName mve0) (varLen true) (argList ((IntDataValue (id 100) (itsFargID 11) (itsFargType INTEGER) (itsCellID 500) (itsValue 1) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 101) (itsFargID 12) (itsFargType TIME_STAMP) (itsCellID 500) (itsValue (60,00:00:00:001)) (subRange false)), (TimeStampDataValue (id 102) (itsFargID 13) (itsFargType TIME_STAMP) (itsCellID 500) (itsValue (60,00:00:01:000)) (subRange false)), (FloatDataValue (id 103) (itsFargID 14) (itsFargType FLOAT) (itsCellID 500) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 104) (itsFargID 15) (itsFargType INTEGER) (itsCellID 500) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 105) (itsFargID 16) (itsFargType NOMINAL) (itsCellID 500) (itsValue a_nominal) (subRange false)), (PredDataValue (id 106) (itsFargID 17) (itsFargType PREDICATE) (itsCellID 500) (itsValue (predicate (id 0) (predID 1) (predName pve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 107) (itsFargID 18) (itsFargType QUOTE_STRING) (itsCellID 500) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 108) (itsFargID 19) (itsFargType TIME_STAMP) (itsCellID 500) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 109) (itsFargID 20) (itsFargType UNTYPED) (itsCellID 500) (itsValue <untyped>) (subRange false))))))";
//                    "(colPred (id 0) (mveID 3) (mveName mve0) (varLen true) (argList ((IntDataValue (id 100) (itsFargID 11) (itsFargType UNTYPED) (itsCellID 500) (itsValue 1) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 101) (itsFargID 12) (itsFargType UNTYPED) (itsCellID 500) (itsValue (60,00:00:00:001)) (subRange false)), (TimeStampDataValue (id 102) (itsFargID 13) (itsFargType UNTYPED) (itsCellID 500) (itsValue (60,00:00:01:000)) (subRange false)), (FloatDataValue (id 103) (itsFargID 14) (itsFargType FLOAT) (itsCellID 500) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 104) (itsFargID 15) (itsFargType INTEGER) (itsCellID 500) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 105) (itsFargID 16) (itsFargType NOMINAL) (itsCellID 500) (itsValue a_nominal) (subRange false)), (PredDataValue (id 106) (itsFargID 17) (itsFargType PREDICATE) (itsCellID 500) (itsValue (predicate (id 0) (predID 1) (predName pve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 107) (itsFargID 18) (itsFargType QUOTE_STRING) (itsCellID 500) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 108) (itsFargID 19) (itsFargType TIME_STAMP) (itsCellID 500) (itsValue (60,00:00:00:000)) (subRange false)), (UndefinedDataValue (id 109) (itsFargID 20) (itsFargType UNTYPED) (itsCellID 500) (itsValue <untyped>) (subRange false))))))";
//                "(Matrix (mveID 3) " +
//                        "(varLen true) " +
//                        "(argList " +
//                            "((FloatDataValue (id 100) " +
//                                "(itsFargID 4) " +
//                                "(itsFargType FLOAT) " +
//                                "(itsCellID 500) " +
//                                "(itsValue 1.0) " +
//                                "(subRange false) " +
//                                "(minVal 0.0) " +
//                                "(maxVal 0.0)), " +
//                            "(IntDataValue (id 101) " +
//                                "(itsFargID 5) " +
//                                "(itsFargType INTEGER) " +
//                                "(itsCellID 500) " +
//                                "(itsValue 2) " +
//                                "(subRange false) " +
//                                "(minVal 0) " +
//                                "(maxVal 0)), " +
//                            "(NominalDataValue (id 102) " +
//                                "(itsFargID 6) " +
//                                "(itsFargType NOMINAL) " +
//                                "(itsCellID 500) " +
//                                "(itsValue a_nominal) " +
//                                "(subRange false)), " +
//                            "(PredDataValue (id 103) " +
//                                "(itsFargID 7) " +
//                                "(itsFargType PREDICATE) " +
//                                "(itsCellID 500) " +
//                                "(itsValue " +
//                                    "(predicate (id 0) " +
//                                        "(predID 1) " +
//                                        "(predName pve) " +
//                                        "(varLen false) " +
//                                        "(argList " +
//                                            "((UndefinedDataValue (id 0) " +
//                                                "(itsFargID 2) " +
//                                                "(itsFargType UNTYPED) " +
//                                                "(itsCellID 0) " +
//                                                "(itsValue <arg>) " +
//                                                "(subRange false))))))) " +
//                                "(subRange false)), " +
//                            "(QuoteStringDataValue (id 104) " +
//                                "(itsFargID 8) " +
//                                "(itsFargType QUOTE_STRING) " +
//                                "(itsCellID 500) " +
//                                "(itsValue q-string) " +
//                                "(subRange false)), " +
//                            "(TimeStampDataValue (id 105) " +
//                                "(itsFargID 9) " +
//                                "(itsFargType TIME_STAMP) " +
//                                "(itsCellID 500) " +
//                                "(itsValue (60,00:00:00:000)) " +
//                                "(subRange false)), " +
//                            "(UndefinedDataValue (id 106) " +
//                                "(itsFargID 10) " +
//                                "(itsFargType UNTYPED) " +
//                                "(itsCellID 500) " +
//                                "(itsValue <untyped>) " +
//                                "(subRange false))))))";

            String testString1 = "mve1(2, 00:00:00:002, 00:00:02:000, 99)";
            String testDBString1 =
                "(colPred (id 0) (mveID 21) (mveName mve1) (varLen false) (argList ((IntDataValue (id 110) (itsFargID 23) (itsFargType INTEGER) (itsCellID 501) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 111) (itsFargID 24) (itsFargType TIME_STAMP) (itsCellID 501) (itsValue (60,00:00:00:002)) (subRange false)), (TimeStampDataValue (id 112) (itsFargID 25) (itsFargType TIME_STAMP) (itsCellID 501) (itsValue (60,00:00:02:000)) (subRange false)), (IntDataValue (id 113) (itsFargID 26) (itsFargType INTEGER) (itsCellID 501) (itsValue 99) (subRange false) (minVal 0) (maxVal 0))))))";
//                "(colPred (id 0) (mveID 21) (mveName mve1) (varLen false) (argList ((IntDataValue (id 110) (itsFargID 23) (itsFargType UNTYPED) (itsCellID 501) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 111) (itsFargID 24) (itsFargType UNTYPED) (itsCellID 501) (itsValue (60,00:00:00:002)) (subRange false)), (TimeStampDataValue (id 112) (itsFargID 25) (itsFargType UNTYPED) (itsCellID 501) (itsValue (60,00:00:02:000)) (subRange false)), (IntDataValue (id 113) (itsFargID 26) (itsFargType INTEGER) (itsCellID 501) (itsValue 99) (subRange false) (minVal 0) (maxVal 0))))))";
//                "(Matrix (mveID 21) " +
//                        "(varLen false) " +
//                        "(argList " +
//                            "((IntDataValue (id 107) " +
//                                "(itsFargID 22) " +
//                                "(itsFargType INTEGER) " +
//                                "(itsCellID 501) " +
//                                "(itsValue 99) " +
//                                "(subRange false) " +
//                                "(minVal 0) " +
//                                "(maxVal 0))))))";

            try
            {
                argList0 = new Vector<DataValue>();

                fargID = mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 1);
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 1));
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 1 * db.getTicks()));
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(4).getID();
                arg = new IntDataValue(db, fargID, 2);
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(5).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(6).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pveID));
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(7).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(8).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks()));
                argList0.add(arg);
                fargID = mve0.getCPFormalArg(9).getID();
                arg = new UndefinedDataValue(db, fargID,
                                             mve0.getCPFormalArg(9).getFargName());
                argList0.add(arg);

                cp0 = new ColPred(db, mve0ID, argList0);

                // set argument IDs to dummy values to test toDBString()
                cp0.argList.get(0).setID(100);
                cp0.argList.get(1).setID(101);
                cp0.argList.get(2).setID(102);
                cp0.argList.get(3).setID(103);
                cp0.argList.get(4).setID(104);
                cp0.argList.get(5).setID(105);
                cp0.argList.get(6).setID(106);
                cp0.argList.get(7).setID(107);
                cp0.argList.get(8).setID(108);
                cp0.argList.get(9).setID(109);

                // set argument cellIDs to dummy values to test toDBString()
                cp0.argList.get(0).itsCellID = 500;
                cp0.argList.get(1).itsCellID = 500;
                cp0.argList.get(2).itsCellID = 500;
                cp0.argList.get(3).itsCellID = 500;
                cp0.argList.get(4).itsCellID = 500;
                cp0.argList.get(5).itsCellID = 500;
                cp0.argList.get(6).itsCellID = 500;
                cp0.argList.get(7).itsCellID = 500;
                cp0.argList.get(8).itsCellID = 500;
                cp0.argList.get(9).itsCellID = 500;

                argList1 = new Vector<DataValue>();
                fargID = mve1.getCPFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 2);
                argList1.add(arg);
                fargID = mve1.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 2));
                argList1.add(arg);
                fargID = mve1.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(db, fargID,
                        new TimeStamp(db.getTicks(), 2 * db.getTicks()));
                argList1.add(arg);
                fargID = mve1.getCPFormalArg(3).getID();
                arg = new IntDataValue(db, fargID, 99);
                argList1.add(arg);

                cp1 = new ColPred(db, mve1ID, argList1);

                // set argument IDs to dummy values to test toDBString()
                cp1.argList.get(0).setID(110);
                cp1.argList.get(1).setID(111);
                cp1.argList.get(2).setID(112);
                cp1.argList.get(3).setID(113);

                // set argument cellIDs to dummy values to test toDBString()
                cp1.argList.get(0).itsCellID = 501;
                cp1.argList.get(1).itsCellID = 501;
                cp1.argList.get(2).itsCellID = 501;
                cp1.argList.get(3).itsCellID = 501;

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( argList0 == null ) ||
                 ( argList0.size() != 10 ) ||
                 ( cp0 == null ) ||
                 ( argList1 == null ) ||
                 ( argList1.size() != 4 ) ||
                 ( cp1 == null ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( argList0 == null )
                    {
                        outStream.print("argList0 allocation failed.\n");
                    }
                    else if ( argList0.size() != 10 )
                    {
                        outStream.printf("unexpected argList0.size(): %d (10).\n",
                                         argList0.size());
                    }

                    if ( argList1 == null )
                    {
                        outStream.print("argList1 allocation failed.\n");
                    }
                    else if ( argList1.size() != 4 )
                    {
                        outStream.printf("unexpected argList1.size(): %d (4).\n",
                                         argList1.size());
                    }

                    if ( ( cp0 == null ) ||
                         ( cp1 == null ) )
                    {
                        outStream.print("one or more ColPred allocation(s) " +
                                        "failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("test col pred allocation failed " +
                                        "to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test col pred allocation threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         SystemErrorExceptionString);
                    }
                }
            }
            else if ( ( cp0.argList.get(0).getID() != 100 ) ||
                      ( cp0.argList.get(1).getID() != 101 ) ||
                      ( cp0.argList.get(2).getID() != 102 ) ||
                      ( cp0.argList.get(3).getID() != 103 ) ||
                      ( cp0.argList.get(4).getID() != 104 ) ||
                      ( cp0.argList.get(5).getID() != 105 ) ||
                      ( cp0.argList.get(6).getID() != 106 ) ||
                      ( cp0.argList.get(7).getID() != 107 ) ||
                      ( cp0.argList.get(8).getID() != 108 ) ||
                      ( cp0.argList.get(9).getID() != 109 ) ||
                      ( cp1.argList.get(0).getID() != 110 ) ||
                      ( cp1.argList.get(1).getID() != 111 ) ||
                      ( cp1.argList.get(2).getID() != 112 ) ||
                      ( cp1.argList.get(3).getID() != 113 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected cp?.argList arg ID(s): " +
                            "%d %d %d %d %d %d %d %d %d %d - %d %d %d %d\n",
                            cp0.argList.get(0).getID(),
                            cp0.argList.get(1).getID(),
                            cp0.argList.get(2).getID(),
                            cp0.argList.get(3).getID(),
                            cp0.argList.get(4).getID(),
                            cp0.argList.get(5).getID(),
                            cp0.argList.get(6).getID(),
                            cp0.argList.get(7).getID(),
                            cp0.argList.get(8).getID(),
                            cp0.argList.get(9).getID(),
                            cp1.argList.get(0).getID(),
                            cp1.argList.get(1).getID(),
                            cp1.argList.get(2).getID(),
                            cp1.argList.get(3).getID());
                }
            }
            else if ( ( cp0.toString().compareTo(testString0) != 0 ) ||
                      ( cp1.toString().compareTo(testString1) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cp0.toString().compareTo(testString0) != 0 )
                    {
                       outStream.printf("Unexpected cp0.toString)(): \"%s\"\n",
                                         cp0.toString());
                    }

                    if ( cp1.toString().compareTo(testString1) != 0 )
                    {
                       outStream.printf("Unexpected cp1.toString)(): \"%s\"\n",
                                         cp1.toString());
                    }
                }
            }
            else if ( ( cp0.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( cp1.toDBString().compareTo(testDBString1) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cp0.toDBString().compareTo(testDBString0) != 0 )
                    {
                       outStream.printf(
                               "Unexpected cp0.toDBString)(): \"%s\"\n",
                               cp0.toDBString());
                    }

                    if ( cp1.toDBString().compareTo(testDBString1) != 0 )
                    {
                       outStream.printf(
                               "Unexpected cp1.toDBString)(): \"%s\"\n",
                               cp1.toDBString());
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

    } /* ColPred::TestToStringMethods() */


    /**
     * Verify3ArgConstructorFailure()
     *
     * Verify that the three argument constructor for this class fails with
     * a system error when supplied the given parameters.
     *
     * Return 0 if the constructor fails as expected, and 1 if it does not.
     *
     *                                              JRM -- 9/30/08
     *
     * Changes:
     *
     *    - None.
     */

    public static int Verify3ArgConstructorFailure(Database db,
                                                   long mve_id,
                                                   Vector<DataValue> arg_list,
                                                   java.io.PrintStream outStream,
                                                   boolean verbose,
                                                   String db_desc,
                                                   String mve_id_desc,
                                                   String arg_list_desc)
    {
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        ColPred cp0 = null;

        try
        {
            cp0 = new ColPred(db, mve_id, arg_list);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( cp0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( cp0 != null )
                {
                    outStream.printf("new ColPred(%s, %s, %s) != null.\n",
                                     db_desc, mve_id_desc, arg_list_desc);
                }

                if ( completed )
                {
                    outStream.printf("new ColPred(%s, %s, %s) completed.\n",
                                     db_desc, mve_id_desc, arg_list_desc);
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.printf("new ColPred(%s, %s, %s) didn't throw " +
                                     "a SystemErrorException.\n",
                                     db_desc, mve_id_desc, arg_list_desc);
                }
            }
        }

        return failures;

    } /* ColPred::Verify3ArgConstructorFailure() */


    /**
     * VerifyArgListAssignment()
     *
     * Verify that the specified replacement of an argument list
     * entry succeeds.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    private static int VerifyArgListAssignment(ColPred target,
                                       DataValue newArg,
                                       int idx,
                                       java.io.PrintStream outStream,
                                       boolean verbose,
                                       String targetDesc,
                                       String newArgDesc)
    {
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        int progress = 0;
        DataValue old_dv = null;
        DataValue new_dv = null;

        try
        {
            old_dv = target.getArg(idx);

            progress++;

            target.replaceArg(idx, newArg);

            progress++;

            new_dv = target.getArg(idx);

            progress++;
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( old_dv == null ) ||
             ( old_dv.getItsFargID() == DBIndex.INVALID_ID ) ||
             ( new_dv == null ) ||
             ( new_dv != newArg ) ||
             ( old_dv.getItsFargID() != new_dv.getItsFargID() ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( old_dv == null )
                {
                    outStream.printf(
                            "initial %s.getArg(%d) failed to complete.\n",
                            targetDesc, idx);
                }

                if ( old_dv.getItsFargID() == DBIndex.INVALID_ID )
                {
                    outStream.printf("initial %s.getArg(%d).getItsFargID() " +
                            "returned INVALID_ID.\n",
                            targetDesc, idx);
                }

                if ( new_dv == null )
                {
                    outStream.printf("%s.replaceArg(%d, %s) failed to " +
                            "complete (progress = %d).\n",
                            targetDesc, idx, newArgDesc, progress);
                }

                if ( new_dv != newArg )
                {
                    outStream.printf(
                        "unexpected getArg(%d) after %s.replaceArg(%d, %s).\n",
                        idx, targetDesc, idx, newArgDesc);
                }

                if ( old_dv.getItsFargID() != new_dv.getItsFargID() )
                {
                    outStream.printf("unexpected itsFargID after %s.replace" +
                            "Arg(%d, %s). old = %d, new = %d\n",
                            targetDesc, idx, newArgDesc,
                            old_dv.getItsFargID(), new_dv.getItsFargID());
                }

                if ( ! completed )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test failed to " +
                            "complete (progress = %d).\n",
                            targetDesc, idx, newArgDesc, progress);

                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test threw a " +
                            "system error(1): \"%s\"\n",
                            targetDesc, idx, newArgDesc,
                            systemErrorExceptionString);

                }
            }
        }

        if ( new_dv instanceof UndefinedDataValue )
        {
            long target_mve_ID = DBIndex.INVALID_ID;
            String old_dv_val = null;
            String new_dv_val = null;
            String farg_name = null;
            MatrixVocabElement target_mve = null;

            try
            {
                if ( old_dv instanceof UndefinedDataValue )
                {
                    old_dv_val = ((UndefinedDataValue)old_dv).getItsValue();
                }
                new_dv_val = ((UndefinedDataValue)new_dv).getItsValue();
                target_mve_ID = target.getMveID();
                target_mve = target.db.getMatrixVE(target_mve_ID);
                farg_name = target_mve.getFormalArg(idx).getFargName();
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( threwSystemErrorException )
            {
                failures++;

                outStream.printf("%s.replaceArg(%d, %s) test threw a " +
                                "system error(2): \"%s\"\n",
                                targetDesc, idx, newArgDesc,
                                systemErrorExceptionString);
            }

            if ( ( old_dv instanceof UndefinedDataValue ) &&
                 ( old_dv_val == null ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test started " +
                            "with null value in undefined arg.\n",
                            targetDesc, idx, newArgDesc);
                }
            }

            if ( new_dv_val == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                            "with null value in undefined arg.\n",
                            targetDesc, idx, newArgDesc);
                }
            }

            if ( ( old_dv_val != null ) && ( old_dv_val == new_dv_val ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                        "with old and new undefined args sharing a string.\n",
                        targetDesc, idx, newArgDesc);
                }
            }

            if ( ( old_dv_val != null ) &&
                 ( old_dv_val.compareTo(new_dv_val) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                        "with old and new undefined args with different " +
                        "values: \"%s\", \"%s\".\n",
                        targetDesc, idx, newArgDesc, old_dv_val, new_dv_val);
                }
            }

            if ( farg_name == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test couldn't " +
                            "get untyped arg name.\n",
                            targetDesc, idx, newArgDesc);
                }
            }

            if ( ( farg_name != null ) &&
                 ( old_dv_val != null ) &&
                 ( farg_name.compareTo(old_dv_val) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test started " +
                        "with undefined arg name different from farg name: " +
                        "\"%s\", \"%s\".\n",
                        targetDesc, idx, newArgDesc, farg_name, old_dv_val);
                }
            }

            if ( ( farg_name != null ) &&
                 ( farg_name.compareTo(new_dv_val) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                        "with undefined arg name different from farg name: " +
                        "\"%s\", \"%s\".\n",
                        targetDesc, idx, newArgDesc, farg_name, new_dv_val);
                }
            }
        }

        return failures;

    } /* ColPred::VerifyArgListAssignment() */


    /**
     * VerifyArgListAsgnmntFails()
     *
     * Verify that the specified replacement of an argument list
     * entry fails.
     *                                              JRM -- 10/3/08
     *
     * Changes:
     *
     *    - None
     */

    private static int VerifyArgListAsgnmntFails(ColPred target,
                                       DataValue newArg,
                                       int idx,
                                       java.io.PrintStream outStream,
                                       boolean verbose,
                                       String targetDesc,
                                       String newArgDesc)
    {
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        DataValue old_dv = null;
        DataValue new_dv = null;

        try
        {
            old_dv = target.getArg(idx);

            target.replaceArg(idx, newArg);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( old_dv == null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( old_dv == null )
                {
                    outStream.printf(
                            "%s.getArg(%d) failed to complete.\n",
                            targetDesc, idx);
                }

                if ( completed )
                {
                    outStream.printf(
                        "%s.replaceArg(%d, %s) test completed.\n",
                        targetDesc, idx, newArgDesc);

                }

                if ( ! threwSystemErrorException )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test " +
                            "failed to throw a system error.\n",
                            targetDesc, idx, newArgDesc);

                }
            }
        }

        completed = false;
        threwSystemErrorException = false;

        try
        {
            new_dv = target.getArg(idx);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( new_dv == null ) ||
             ( new_dv != old_dv ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( new_dv == null )
                {
                    outStream.printf(
                            "%s.getArg(%d) failed to complete.\n",
                            targetDesc, idx);
                }

                if ( new_dv != old_dv )
                {
                    outStream.printf(
                        "unexpected getArg(%d) after %s.replaceArg(%d, %s).\n",
                        idx, targetDesc, idx, newArgDesc);
                }

                if ( ! completed )
                {
                    outStream.printf(
                        "%s.getArg(%d) test failed to complete.\n",
                        targetDesc, idx);

                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("%s.getArg(%d) test threw " +
                            "system error: \"%s\"\n",
                            targetDesc, idx,
                            systemErrorExceptionString);

                }
            }
        }

        return failures;

    } /* ColPred::VerifyArgListAsgnmntFails() */


    /**
     * VerifyColPredCopy()
     *
     * Verify that the supplied instances of ColPred are distinct,
     * that they contain no common references (other than db), and that they
     * have the same value.
     *                                              JRM -- 10/01/08
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyColPredCopy(ColPred base,
                                        ColPred copy,
                                        java.io.PrintStream outStream,
                                        boolean verbose,
                                        String baseDesc,
                                        String copyDesc)
    {
        int failures = 0;
        int i;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyPredicateCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyPredicateCopy: %s null on entry.\n",
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
        else if ( base.db != copy.db )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.db != %s.db.\n", baseDesc, copyDesc);
            }
        }
        else if ( base.mveID != copy.mveID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.mveID == %d != %s.mveID == %d.\n",
                                 baseDesc, base.mveID, copyDesc, copy.mveID);
            }
        }
        else if ( ( base.mveName == copy.mveName ) &&
                  ( base.mveName != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                     "%s.mveName and %s.mveName refer to the same string.\n",
                      baseDesc, copyDesc);
            }
        }
        else if ( base.varLen != copy.varLen )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.varLen != %s.varLen.\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.toString().compareTo(copy.toString()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.toString() doesn't match %s.toString().\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.toDBString().compareTo(copy.toDBString()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.toDBString() doesn't match %s.toDBString().\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( ( base.argList == copy.argList ) &&
                  ( base.argList != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s share an argList.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.argList == null ) &&
                  ( copy.argList != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.argList is null and %s.argList isn't.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.argList != null ) &&
                  ( copy.argList == null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.argList is null and %s.argList isn't.\n",
                                  copyDesc, baseDesc);
            }
        }
        else if ( base.argList != null )
        {
            i = 0;
            while ( ( i < base.argList.size() ) && ( failures == 0 ) )
            {
                failures += DataValue.VerifyDVCopy(base.argList.get(i),
                                          copy.argList.get(i),
                                          outStream,
                                          verbose,
                                          baseDesc + "argList.get(" + i + ")",
                                          copyDesc + "argList.get(" + i + ")");
                i++;
            }
        }

        return failures;

    } /* ColPred::VerifyColPredCopy() */

} /* class ColPred */
