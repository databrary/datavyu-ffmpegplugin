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

         if (db == null) {
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
        ColPred clone = (ColPred) super.clone();
        try {
            clone = new ColPred(this);
        } catch (SystemErrorException e) {
            clone = null;
        }

        return clone;
    }


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

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

        if ( this.getDB() != db )
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

        dbe = this.getDB().idx.getElement(this.cellID);

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

        if ( this.getDB() != db )
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

        dbe = this.getDB().idx.getElement(this.cellID);

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

        this.getDB().idx.addElement(this);

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
                dv.setItsPredID(this.getID());
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

        this.getDB().idx.removeElement(this.getID());

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
            s = "(colPred (id " + this.getID() +
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

        if ( this.getDB() == null )
        {
            throw new SystemErrorException(mName + "this.db is null?!?");
        }

        if ( this.getDB().idx.getElement(this.getID()) != this )
        {
            throw new SystemErrorException(mName +
                    "not the cannonical incarnation of the ColPred");
        }

        if ( this.mveID != DBIndex.INVALID_ID )
        {
            if ( ( ! cascadeMveDel ) ||
                 ( cascadeMveID != this.mveID ) ) // must de-register
            {

                dbe = this.getDB().idx.getElement(this.mveID);

                if ( ! ( dbe instanceof PredicateVocabElement ) )
                {
                    throw new SystemErrorException(mName +
                            "mveID doesn't refer to a mve.");
                }

                mve = (MatrixVocabElement)dbe;

                mve.deregisterInternalListener(this.getID());
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

        if ( this.getID() == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "id not set?!?");
        }

        if ( this.getDB() == null )
        {
            throw new SystemErrorException(mName + "this.db is null?!?");
        }

        if ( this.getDB().idx.getElement(this.getID()) != this )
        {
            System.out.println(this.toString());
            System.out.println(this.toDBString());
            System.out.println(((ColPred)(this.getDB().idx.getElement(this.getID()))).toString());
            System.out.println(((ColPred)(this.getDB().idx.getElement(this.getID()))).toDBString());
            int j = 1/0;
            throw new SystemErrorException(mName +
                    "not the cannonical incarnation of the predicate");
        }

        if ( this.mveID != DBIndex.INVALID_ID ) // we have work to do
        {

            dbe = this.getDB().idx.getElement(this.mveID);

            if ( ! ( dbe instanceof MatrixVocabElement ) )
            {
                throw new SystemErrorException(mName +
                                               "mveID doesn't refer to a pve.");
            }

            mve = (MatrixVocabElement)dbe;

            mve.registerInternalListener(this.getID());


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

        if ( this.getDB() != db )
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

                            dv.setItsPredID(this.getID());

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

        if ( this.getDB() != db )
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

                            dv.setItsPredID(this.getID());

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

        if ( oldColPred.getID() == DBIndex.INVALID_ID )
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

        if ( ( this.getID() != DBIndex.INVALID_ID ) &&
             ( this.getID() != oldColPred.getID() ) )
        {
            throw new SystemErrorException(mName +
                    "this.id not invalid and not equal to oldColPred.id");
        }


        if ( this.mveID == DBIndex.INVALID_ID )
        {
            if ( oldColPred.mveID == DBIndex.INVALID_ID )
            {
                this.getDB().idx.replaceElement(this);
            }
            else
            {
                // we are replacing a column predicate with an undefined predicate.
                if ( this.getID() == DBIndex.INVALID_ID )
                {
                    oldColPred.removeFromIndex(DCID);
                    this.insertInIndex(DCID);
                }
                else
                {
                    assert ( this.getID() == oldColPred.getID() );

                    // Remove the arguments of the old predicate from the
                    // index.
                    for ( DataValue dv : oldColPred.argList )
                    {
                        dv.removeFromIndex(DCID);
                    }

                    // replace the old Predicate with the new in the index.
                    this.getDB().idx.replaceElement(this);
                }
            }
        }
        else if ( this.mveID != oldColPred.mveID )
        {
            if ( oldColPred.mveID == DBIndex.INVALID_ID )
            {
                // we are replacing an undefined column predicate with a
                // new column predicate
                if ( this.getID() == DBIndex.INVALID_ID )
                {
                    oldColPred.removeFromIndex(DCID);
                    this.insertInIndex(DCID);
                }
                else
                {
                    assert( this.getID() == oldColPred.getID() );

                    this.getDB().idx.replaceElement(this);

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
                if ( this.getID() == DBIndex.INVALID_ID )
                {
                    oldColPred.removeFromIndex(DCID);
                    this.insertInIndex(DCID);
                }
                else
                {
                    assert( this.getID() == oldColPred.getID() );

                    this.getDB().idx.replaceElement(this);

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
        else if ( this.getID() == DBIndex.INVALID_ID )
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
            assert( this.getID() == oldColPred.getID() );
            assert( this.mveID == oldColPred.mveID );
            assert( this.mveID != DBIndex.INVALID_ID );

            this.getDB().idx.replaceElement(this);

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
            assert( this.getID() == oldColPred.getID() );
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

            this.getDB().idx.replaceElement(this);

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
                    assert( this.getID() != DBIndex.INVALID_ID );

                    newArg.setItsPredID(this.getID());
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
            if ( this.getID() != DBIndex.INVALID_ID )
            {
                int j = 1/0;
                throw new SystemErrorException(mName +
                        "id set when invalid ID required.");
            }
        }
        else if ( this.getID() == DBIndex.INVALID_ID )
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

        if ( this.getID() == DBIndex.INVALID_ID )
        {
            idMustBeInvalid = true;
        }
        else if ( this.getID() != oldColPred.getID() )
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

    /** Seed value for generating hash codes. */
    private final static int SEED1 = 3;

    /** Seed value for generating hash codes. */
    private final static int SEED2 = 7;

    /** Seed value for generating hash codes. */
    private final static int SEED3 = 11;

    /** Seed value for generating hash codes. */
    private final static int SEED4 = 13;

    /** Seed value for generating hash codes. */
    private final static int SEED5 = 17;

    /** Seed value for generating hash codes. */
    private final static int SEED6 = 19;

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += this.getDB() == null ? 0 : this.getDB().hashCode() * SEED1;
        hash += (getID() ^ (getID() >>> 32)) * SEED2;
        hash += (mveID ^ (mveID >>> 32)) * SEED3;
        hash += new Boolean(this.varLen).hashCode() * SEED4;
        hash += this.mveName == null ? 0 : this.mveName.hashCode() * SEED5;
        hash += this.argList == null ? 0 : this.argList.hashCode() * SEED6;

        return hash; /*(int) (hash ^ (hash >>> 32));*/
    }

    /**
     * Compares this ColPred against another object.
     * Assumption: ColPreds are not equal just because their id fields match.
     * This function will test that db, id and lastModUID all match.
     * If id can be proved to be enough for testing equality we should
     * implement a simpler, faster version.
     *
     * @param obj The object to compare this against.
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

        ColPred c = (ColPred) obj;

        return super.equals(obj)
            && (c.getDB() == this.getDB())
            && (c.getID() == this.getID())
            && (c.mveID == this.mveID)
            && (c.varLen == this.varLen)
            && (this.mveName == null ? c.mveName == null
                                     : c.mveName.equals(this.mveName))
            && (this.argList == null ? c.argList == null
                                     : c.argList.equals(this.argList));
    }

} /* class ColPred */
