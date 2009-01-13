/*
 * Predicate.java
 *
 * Created on August 19, 2007, 10:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

import java.util.Vector;


/**
 * Class Predicate
 *
 * Primitive class for predicates.  Instances of this class are used to store
 * predicates in a database.  Since predicates must be defined in the vocab
 * list before they can be created, instances of this class are tightly
 * bound to their host database and its vocab list.
 *
 *                                                  JRM -- 8/19/07
 *
 * @author mainzer
 */
public class Predicate extends DBElement
        implements InternalVocabElementListener
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /* pveID:   Long containing the ID of the PredicateVocabElement represented
     *      in this instance of Predicate, or INVALID_ID if the predicate is
     *      undefined.
     *
     * predName: String containing the name of the predicate represented in
     *      this instance of Predicate, or the empty string if the predicate
     *      is undefined.
     *
     *      Note that this is also the name of the associated
     *      PredicateVocabElement -- if any.
     *
     * argList: Vector of data values representing the arguments of the
     *      predicate represented in this data value, or null if the predicate
     *      is undefined.
     *
     * varLen:  Boolean flag indicating whether the argument list is of
     *      variable length.
     *
     * cellID:  Long containing the ID of the DataCell in which this instance
     *      of predicate appears (if any).
     *
     * queryVarOK: Boolean flag used to indicate whether parameters can be
     *      be query variables.
     *
     */

    /** ID of the represented predicate */
    protected long pveID = DBIndex.INVALID_ID;

    /** Name of the represented predicate */
    protected String predName = null;

    /** Argument list of the predicate */
    protected Vector<DataValue> argList = null;

    /** Whether the predicate has a variable length argument list */
    protected boolean varLen = false;

    /** ID of cell in which this col pred appears, if any */
    protected long cellID = DBIndex.INVALID_ID;

    /** whether parameters can be query variables */
    protected boolean queryVarOK = false;



    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * Predicate()
     *
     * Constructor for instances of Predicate.
     *
     * Five versions of this constructor.
     *
     * The first takes only a reference to a database as its parameter and
     * constructs an undefined instance of Predicate (that is, an instance
     * that is not yet an instance of some predicate in the vocab list).
     *
     * The second takes a reference to a database, and a PredicateVocabElement
     * ID, and constructs a representation of the specified predicate with an
     * empty/undefined argument list.
     *
     * The third takes a reference to a database, a PredicateVocabElementID,
     * and a vector of formal arguments specifying the values assigned to
     * each of the predicates arguments, and then constructs an instance of
     * Predicate representing the specified predicate with the indicated
     * values as its arguments.
     *
     * The fourth takes a reference to an instance of Predicate as an
     * argument, and uses it to create a copy.
     *
     * The fifth is much the same as the fourth, save that if the blindCopy
     * parameter is true, it creates a copy of the supplied predicate without
     * making reference to the underlying PredicateVocabElement.  This is
     * necessary if the pve has changed, and we need to make a copy of the
     * old version of the predicate so we can touch it up for changes in
     * the pve.
     *
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public Predicate(Database db) throws SystemErrorException {
         super(db);

         final String mName = "Predicate::Predicate(db): ";

         if (db == null) {
             throw new SystemErrorException(mName + "Bad db param");
         }

         this.predName = new String("");

    } /* Predicate::Predicate(db) */

    public Predicate(Database db,
                     long predID)
        throws SystemErrorException
    {
        super(db);

        final String mName = "Predicate::Predicate(db, predID): ";
        DBElement dbe;
        PredicateVocabElement pve;

        if ( predID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "predID == INVALID_ID");
        }

        if ( predID != DBIndex.INVALID_ID )
        {
            dbe = this.db.idx.getElement(predID);

            if ( dbe == null )
            {
                throw
                     new SystemErrorException(mName + "predID has no referent");
            }

            if ( ! ( dbe instanceof PredicateVocabElement ) )
            {
                throw new SystemErrorException(mName +
                        "predID doesn't refer to a predicate vocab element");
            }

            this.pveID = predID;

            pve = (PredicateVocabElement)dbe;

            this.predName = pve.getName();

            this.varLen = pve.getVarLen();

            this.argList = this.constructEmptyArgList(pve);
        }
    } /* Predicate::Predicate(db, pveID) */


    public Predicate(Database db,
                     long predID,
                     java.util.Vector<DataValue> argList)
        throws SystemErrorException
    {
        super(db);

        final String mName = "Predicate::Predicate(db, predID, argList): ";
        DBElement dbe;
        PredicateVocabElement pve;

        if ( predID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "predID == INVALID_ID");
        }
        else
        {
            dbe = this.db.idx.getElement(predID);

            if ( dbe == null )
            {
                throw
                     new SystemErrorException(mName + "predID has no referent");
            }

            if ( ! ( dbe instanceof PredicateVocabElement ) )
            {
                throw new SystemErrorException(mName +
                        "predID doesn't refer to a predicate vocab element");
            }

            this.pveID = predID;

            pve = (PredicateVocabElement)dbe;

            this.predName = pve.getName();

            this.varLen = pve.getVarLen();

            this.argList = this.copyArgList(argList, true);
        }
    } /* Predicate::Predicate(db, pveID, argList) */


    public Predicate(Predicate pred)
        throws SystemErrorException
    {
        super(pred);

        final String mName = "Predicate::Predicate(pred): ";

        if ( pred == null )
        {
            throw new SystemErrorException(mName + "pred null on entry");
        }

        this.pveID    = pred.pveID;
        this.predName = new String(pred.predName);
        this.varLen   = pred.varLen;
        this.cellID   = pred.cellID;

        if ( pred.argList == null )
        {
            this.argList = null;
        }
        else
        {
            this.argList = this.copyArgList(pred.argList, false);
        }

    } /* Predicate::Predicate(pred) */


    protected Predicate(Predicate pred,
                        boolean blindCopy)
        throws SystemErrorException
    {
        super(pred);

        final String mName = "Predicate::Predicate(pred, blindCopy): ";

        if ( pred == null )
        {
            throw new SystemErrorException(mName + "pred null on entry");
        }

        this.pveID    = pred.pveID;
        this.predName = new String(pred.predName);
        this.varLen   = pred.varLen;
        this.cellID   = pred.cellID;

        if ( pred.argList == null )
        {
            this.argList = null;
        }
        else if ( blindCopy )
        {
            this.argList = this.blindCopyArgList(pred.argList);
        }
        else
        {
            this.argList = this.copyArgList(pred.argList, false);
        }
    }/* Predicate::Predicate(pred, blindCopy) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getDB()
     *
     * Return the current value of the db field.
     *
     *                          JRM -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */
    @Override
    public Database getDB()
    {

        return this.db;

    } /* Predicate::getdb() */


    /**
     * getCellID()
     *
     * Return the current value of the cellID field.
     *
     *                          JRM -- 4/4/08
     *
     * Changes:
     *
     *    - None.
     */

    public long getCellID()
    {

        return this.cellID;

    } /* Predicate::getCellID() */


    /**
     *
     * getPveID()
     *
     * Return the current value of the pveID field.
     *
     *                          JRM -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */

    public long getPveID()
    {

        return this.pveID;

    } /* Predicate::getPveID() */


    /**
     * getPredName()
     *
     * Return a copy of the current value of the predName field.
     *
     *                                      JRM -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */

    public String getPredName()
    {

        return new String(this.predName);

    } /* Predicate::getPredName() */


    /**
     * setPredID()
     *
     * Set the predicate of which this instance of Predicate will contain a
     * value.  If requested, try to salvage the argument list (if any).
     *
     *                                          JRM -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */

    public void setPredID(long predID,
                          boolean salvage)
        throws SystemErrorException
    {
        final String mName = "Predicate::setPredID(predID, preserveArgs): ";
        int i;
        int newNumArgs;
        int oldNumArgs;
        FormalArgument fa;
        DataValue ndv;
        DataValue odv;
        PredicateVocabElement pve;
        Vector<DataValue> oldArgList;

        if ( predID == DBIndex.INVALID_ID )
        {
            this.pveID = DBIndex.INVALID_ID;
            this.predName = "";
            this.argList = null;
            this.varLen = false;
        }
        else
        {
            pve = this.lookupPredicateVE(predID);

            this.pveID = predID;

            this.predName = pve.getName();

            this.varLen = pve.getVarLen();

            if ( ( salvage ) && ( this.argList != null ) )
            {
                newNumArgs = pve.getNumFormalArgs();

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
                    // get the i'th formal argument of the predicate.  Observe
                    // that getFormaArg() returns a reference to the actual
                    // formal argument in the PredicateVocabElement data
                    // structure, so we must be careful not to modify it in
                    // any way, or expose the reference to the user.
                    fa = pve.getFormalArg(i);

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
                this.argList = this.constructEmptyArgList(pve);
            }

        }

        return;

    } /* Predicate::setPredID(pveID, salvage) */

    /**
     * getVarLen()
     *
     * Return the current value of the varLen field.
     *
     *                          JRM -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */

    public boolean getVarLen()
    {

        return this.varLen;

    } /* Predicate::getVarLen() */


    /*************************************************************************/
    /********************** VE Change Management: ****************************/
    /*************************************************************************/

    /**
     * VEChanged()
     *
     * Needed to implement the InternalVocabElementListener interface.
     *
     * Advise the host data cell that it contains a predicate whose associated
     * pve definition has changed.
     *
     *                                            JRM -- 3/20/08
     *
     * Changes:
     *
     *    - None.
     */
    public void VEChanged(Database db,
                         long VEID,
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
        final String mName = "Predicate::VEChanged(): ";
        DBElement dbe = null;
        DataCell dc = null;

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( this.pveID != VEID )
        {
            throw new SystemErrorException(mName + "pveID mismatch.");
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

        dc.cascadeUpdateForPVEDefChange(db,
                                        VEID,
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

    } /* Predicate::VEChanged() */


    /**
     * VEDeleted()
     *
     * Needed to implement the InternalVocabElementListener interface.
     *
     * Advise the host data cell that it contains a predicate whose associated
     * pve has been deleted.
     *
     *                                  JRM -- 3/20/08
     *
     * Changes:
     *
     *    - None.
     */
    public void VEDeleted(Database db,
                          long VEID)
        throws SystemErrorException
    {
        final String mName = "Predicate::VEDeleted(): ";
        DBElement dbe = null;
        DataCell dc = null;

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( this.pveID != VEID )
        {
            throw new SystemErrorException(mName + "pveID mismatch.");
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

        dc.cascadeUpdateForPVEDeletion(db, VEID);

        return;

    } /* Predicate::VEDeleted() */


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
                throw new SystemErrorException(mName + "argList unitialized?!");
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

    } /* Predicate::argListToDBString() */


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
        final String mName = "Predicate::argListToString(): ";
        int i = 0;
        int numArgs = 0;
        String s;

        if ( this.pveID == DBIndex.INVALID_ID )
        {
            s = "()";
        }
        else
        {
            if ( argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!");
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

    } /* Predicate::argListToString() */


    /**
     * insertInIndex()
     *
     * This method is called when the DataCell whose value contains this
     * instance of predicate is first inserted in the database and becomes the
     * first cannonical version of the DataCell.
     *
     * Insert the instance of Predicate in the index and make note of the
     * DataCell ID.
     *
     * If the instance of predicate is a representation of some PVE, register
     * as a listener with the PVE, and pass the insert in index message down
     * to the argument list.
     *
     *                                              JRM -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void insertInIndex(long DCID)
        throws SystemErrorException
    {
        final String mName = "Predicate::insertInIndex(): ";

        this.db.idx.addElement(this);

        // this should have been checked well before we were called,
        // so no sanity checks.
        this.cellID = DCID;

        if ( this.pveID != DBIndex.INVALID_ID )
        {
            // TODO: register as a listener for this.pveID

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

    } /* Predicate::insertInIndex(DCID) */


    /**
     * lookupPredicateVE()
     *
     * Given an ID, attempt to look up the associated predicateVocabElement
     * in the database associated with the instance of Predicate.  Return a
     * reference to same.  If there is no such PredicateVocabElement, throw
     * a system error.
     *                                              JRM -- 8/20/07
     *
     * Changes:
     *
     *    - None.
     */

    private PredicateVocabElement lookupPredicateVE(long predID)
        throws SystemErrorException
    {
        final String mName = "Predicate::lookupPredicateVE(predID): ";
        DBElement dbe;
        PredicateVocabElement pve;

        if ( predID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "predID == INVALID_ID");
        }

        dbe = this.db.idx.getElement(predID);

        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "predID has no referent");
        }

        if ( ! ( dbe instanceof PredicateVocabElement ) )
        {
            throw new SystemErrorException(mName +
                    "predID doesn't refer to a predicate vocab element");
        }

        pve = (PredicateVocabElement)dbe;

        return pve;

    } /* Predicate::lookupPredicateVE(pveID) */


    /**
     * removeFromIndex()
     *
     * This method is called when the DataCell whose value contains this
     * instance of predicate is deleted from the database, and thus all the
     * DBElements that constitute its value must be removed from the
     * index.
     *
     * Remove the predicate from the index.
     *
     * If the instance of predicate is a representation of some PVE, deregister
     * as a listener with the PVE, and pass the remove from index message down
     * to the argument list.
     *
     *                                              JRM -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void removeFromIndex(long DCID)
        throws SystemErrorException
    {
        final String mName = "Predicate::removeFromIndex(): ";

        if ( this.cellID != DCID )
        {
            throw new SystemErrorException(mName + "cell id mismatch");
        }

        this.db.idx.removeElement(this.id);

        // if the predicate is not associated with some pve, it doesn't need
        // an ID.
        if ( this.pveID != DBIndex.INVALID_ID )
        {
            // TODO: de-register as a listener for this.pveID

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

    } /* Predicate::removeFromIndex(DCID) */


   /**
     * toDBString()
     *
     * Returns a database String representation of the Predicate for comparison
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
    @Override
    public String toDBString()
    {
        String s;

        try
        {
            s = "(predicate (id " + this.id +
                ") (predID " + this.pveID +
                ") (predName " + this.predName +
                ") (varLen " + this.varLen + ") " +
                this.argListToDBString() + "))";
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return s;

    } /* Predicate::toDBString() */


    /**
     * toString()
     *
     * Returns a String representation of the Predicate for display.
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     *
     */
    @Override
    public String toString()
    {
        String s;

        try
        {
            s = this.predName + this.argListToString();
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return (s);

    } /* Predicate::toString() */


    /*************************************************************************/
    /********************* Argument List Management: *************************/
    /*************************************************************************/


    /**
     * blindCopyArgList()
     *
     * Given a reference to a Vector containing an argument list for the
     * predicate indicated by the current value of pveID, copy the argument
     * list without attempting any sanity checks against the pve.
     *
     * This is necessary if the definition of the pve has changed, and we
     * need a copy of the predicate to modify into accordance with the new
     * version.
     *
     * Throw a system error if any errors aredetected.  Otherwise, return the
     * copy.
     *
     *                                              JRM -- 4/6/08
     *
     * Changes:
     *
     *    - None.
     */

    private Vector<DataValue> blindCopyArgList(Vector<DataValue> srcArgList)
        throws SystemErrorException
    {
        final String mName = "Predicate::blindCopyArgList(srcArgList): ";
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

    } /* Predicate::blindCopyArgList(srcArgList) */


    /**
     * constructEmptyArgList()
     *
     * Given a reverence to a PredicateVocabElement, construct an empty
     * argument list as directed by the formal argument list of the supplied
     * PredicateVocabElement.
     *
     * Return the newly constructed argument list.
     *
     *                                              JRM -- 8/20/07
     *
     * Changes:
     *
     *    - None.
     */

    private Vector<DataValue> constructEmptyArgList(PredicateVocabElement pve)
        throws SystemErrorException
    {
        final String mName = "Predicate::constructEmptyArgList(pve): ";
        int i;
        int numArgs;
        Vector<DataValue> newArgList = new Vector<DataValue>();
        FormalArgument fa;
        DataValue dv;

        if ( pve == null )
        {
            throw new SystemErrorException(mName + "pve == null");
        }

        numArgs = pve.getNumFormalArgs();

        if ( numArgs <= 0 )
        {
            throw new SystemErrorException(mName + "numArgs <= 0");
        }

        for ( i = 0; i < numArgs; i++ )
        {
            // get the i'th formal argument of the predicate.  Observe that
            // getFormaArg() returns a reference to the actual formal
            // argument in the PredicateVocabElement data structure, so we
            // must be careful not to modify it in any way, or expose the
            // reference to the user.
            fa = pve.getFormalArg(i);

            if ( fa == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th formal argument?!?!");
            }
            else if ( fa instanceof TextStringFormalArg )
            {
                throw new SystemErrorException(mName +
                        "TextStringFormalArg in pve?!?!");
            }

            dv = fa.constructEmptyArg();

            if ( dv == null )
            {
                throw new SystemErrorException(mName + "dv == null?!?!");
            }

            newArgList.add(dv);
        }

        if ( newArgList.size() != numArgs )
        {
            throw new SystemErrorException(mName + "bad arg list len");
        }

        return newArgList;

    } /* Predicate::constructEmptyArgList(pve) */


    /**
     * copyArgList()
     *
     * Given a reference to a Vector containing an argument list for the
     * predicate indicated by the current value of pveID, attempt to make a
     * copy of that argument list.  Throw a system error if any errors are
     * detected.  Otherwise, return the copy.
     *
     *                                              JRM -- 8/20/07
     *
     * Changes:
     *
     *    - Added the clearID parameter and supporting code.
     *                                              JRM -- 2/19/08
     */

    private Vector<DataValue> copyArgList(Vector<DataValue> srcArgList,
                                          boolean clearID)
        throws SystemErrorException
    {
        final String mName = "Predicate::copyArgList(srcArgList, clearID): ";
        int i;
        int numArgs;
        PredicateVocabElement pve;
        Vector<DataValue> newArgList = new Vector<DataValue>();
        FormalArgument fa;
        DataValue dv;
        DataValue cdv = null;

        if ( srcArgList == null )
        {
            throw new SystemErrorException(mName + "srcArgList null on entry");
        }

        if ( this.pveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "predID undefined");
        }

        pve = this.lookupPredicateVE(this.pveID);

        numArgs = pve.getNumFormalArgs();

        if ( srcArgList.size() != numArgs )
        {
            int j = 1/0;
            throw new SystemErrorException(mName + "arg list size mis-match");
        }

        if ( numArgs <= 0 )
        {
            throw new SystemErrorException(mName + "numArgs <= 0");
        }

        for ( i = 0; i < numArgs; i++ )
        {
            // get the i'th formal argument of the predicate.  Observe that
            // getFormaArg() returns a reference to the actual formal
            // argument in the PredicateVocabElement data structure, so we
            // must be careful not to modify it in any way, or expose the
            // reference to the user.
            fa = pve.getFormalArg(i);

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
                                "Type mismatch for arg " + i + ": nominal DV, "
                                + "undefined DV, or query var expected.");
                    }
                    else
                    {
                        throw new SystemErrorException(mName +
                                "Type mismatch for arg " + i + ": nominal DV, "
                                + "or undefined DV expected.");
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
                        cdv =
                             new QuoteStringDataValue((QuoteStringDataValue)dv);
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
                    try {
                        cdv = (DataValue) dv.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new SystemErrorException("Unable to clone dv.");
                    }
                    break;

                case UNDEFINED:
                    throw new SystemErrorException(mName +
                            "formal arg type undefined???");
                    /* break statement commented out to keep compiler happy */
                    // break;

                default:
                    throw new SystemErrorException(mName +
                                                   "Unknown Formal Arg Type");
                    /* break statement commented out to keep compiler happy */
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

    } /* Predicate::copyArgList(srcArgList, clearID) */


    /**
     * deregisterWithPve()
     *
     * If the predicate is defined (i.e. this.itsPveID != DBIndex.INVALID_ID,
     * deregister the predicate with its predicate vocab element as an internal
     * vocal element listener.  Also pass the deregister predicates message
     * down to any predicate data values that may appear in the predicate's
     * argument list.
     *
     * This method should only be called if this instance of the predicate
     * is the cannonical instance -- that is the instance listed in the
     * index.
     *                                              JRM -- 3/24/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterWithPve(boolean cascadeMveDel,
                                     long cascadeMveID,
                                     boolean cascadePveDel,
                                     long cascadePveID)
        throws SystemErrorException
    {
        final String mName = "Predicate::deregisterWithPve(): ";
        DBElement dbe = null;
        PredicateVocabElement pve = null;

        if ( this.db == null )
        {
            throw new SystemErrorException(mName + "this.db is null?!?");
        }

        if ( this.db.idx.getElement(this.id) != this )
        {
            throw new SystemErrorException(mName +
                    "not the cannonical incarnation of the predicate");
        }

        if ( this.pveID != DBIndex.INVALID_ID )
        {
            if ( ( ! cascadePveDel ) ||
                 ( cascadePveID != this.pveID ) ) // must de-register
            {

                dbe = this.db.idx.getElement(this.pveID);

                if ( ! ( dbe instanceof PredicateVocabElement ) )
                {
                    throw new SystemErrorException(mName +
                                               "pveID doesn't refer to a pve.");
                }

                pve = (PredicateVocabElement)dbe;

                pve.deregisterInternalListener(this.id);
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

    } /* Predicate::deregisterWithPve() */


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
        final String mName = "Predicate::getArg(): ";
        int numArgs;
        DataValue arg = null;

        if ( pveID == DBIndex.INVALID_ID )
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

            if ( arg instanceof TextStringDataValue )
            {
                throw new SystemErrorException(mName +
                        "TextStringDataValue in a pred arg list?!?");
            }
        }

        return arg;

    } /* VocabElement::getArg() */


    /**
     * getNumArgs()
     *
     * Return the number of arguments.  Return 0 if the pveID hasn't been
     * specified yet.
     *                                      JRM -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */

    public int getNumArgs()
        throws SystemErrorException
    {
        final String mName = "Predicate::getNumArgs(): ";
        int numArgs = 0;

        if ( pveID != DBIndex.INVALID_ID )
        {
            if ( argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!");
            }

            numArgs = this.argList.size();

            if ( numArgs <= 0 )
            {
                throw new SystemErrorException(mName + "numArgs <= 0");
            }
        }

        return numArgs;

    } /* Predicate::getNumArgs() */


    /**
     * registerWithPve()
     *
     * If the predicate is defined (i.e. this.itsPveID != DBIndex.INVALID_ID,
     * register the predicate with its predicate vocab element as an internal
     * vocal element listener.  Also pass the register predicates message
     * down to any predicate data values that may appear in the predicate's
     * argument list.
     *
     * This method should only be called if this instance of the predicate
     * is the cannonical instance -- that is the instanced listed in the
     * index.
     *                                              JRM -- 3/24/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerWithPve()
        throws SystemErrorException
    {
        final String mName = "Predicate::registerWithPve(): ";
        DBElement dbe = null;
        PredicateVocabElement pve = null;

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
            System.out.println((
                    (Predicate)(this.db.idx.getElement(this.id))).toString());
            System.out.println((
                    (Predicate)(this.db.idx.getElement(this.id))).toDBString());
            int j = 1/0;
            throw new SystemErrorException(mName +
                    "not the cannonical incarnation of the predicate");
        }

        if ( this.pveID != DBIndex.INVALID_ID ) // we have work to do
        {

            dbe = this.db.idx.getElement(this.pveID);

            if ( ! ( dbe instanceof PredicateVocabElement ) )
            {
                throw new SystemErrorException(mName +
                                               "pveID doesn't refer to a pve.");
            }

            pve = (PredicateVocabElement)dbe;

            pve.registerInternalListener(this.id);


            for ( DataValue dv : this.argList )
            {
                if ( dv instanceof ColPredDataValue )
                {
                    ((ColPredDataValue)dv).registerPreds();
                }
                else if ( dv instanceof PredDataValue )
                {
                    ((PredDataValue)dv).registerPreds();
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
        PredicateVocabElement pve;
        Vector<DataValue> newArgList = new Vector<DataValue>();
        FormalArgument fa;
        DataValue oldArg = null;

        if ( newArg == null )
        {
            throw new SystemErrorException(mName + "newArg null on entry");
        }
        else if ( this.pveID == DBIndex.INVALID_ID )
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

        pve = this.lookupPredicateVE(this.pveID);

        // get the n'th formal argument of the predicate.  Observe that
        // getFormaArg() returns a reference to the actual formal
        // argument in the PredicateVocabElement data structure, so we
        // must be careful not to modify it in any way, or expose the
        // reference to the user.
        fa = pve.getFormalArg(n);

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "no " + n +
                    "th formal argument?!?!");
        }
        else if ( fa instanceof TextStringFormalArg )
        {
            throw new SystemErrorException(mName +
                    "pve contains a text formal arg?!?!");
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
     * Scan the list of data values in the predicate, and pass an update for
     * matrix vocab element definition change message to any column predicate
     * or predicate data values.
     *                                          JRM -- 8/26/08
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
        final String mName = "Predicate::updateForMVEDefChange(): ";
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
                                           "mveID doesn't refer to a pve.");
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
                                                          cpFargInserted,
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
                                                          cpFargInserted,
                                                          oldCPFargList,
                                                          newCPFargList);
            }
        }

        return;

    } /* Predicate::updateForMVEDefChange() */


    /**
     * updateForMVEDeletion()
     *
     * If the predicate is defined, scan its argument list and
     * pass the update for mve deletion message to any column predicates or
     * predicates that may appear in the argument list.
     *
     *                                          JRM -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForMVEDeletion(Database db,
                                        long deletedMveID)
        throws SystemErrorException
    {
        final String mName = "Predicate::updateForMVEDeletion(): ";
        int i;
        int numArgs;
        DBElement dbe = null;
        PredicateVocabElement pve = null;
        FormalArgument fa = null;
        DataValue dv = null;
        PredDataValue pdv = null;

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( deletedMveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "deletedPveID invalid.");
        }

        if ( this.pveID != DBIndex.INVALID_ID )
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
                    ((PredDataValue)dv).updateForPVEDeletion(db, deletedMveID);
                }
                else if ( dv instanceof ColPredDataValue )
                {
                    ((ColPredDataValue)dv)
                            .updateForPVEDeletion(db, deletedMveID);
                }

                i++;
            }
        }

        return;

    } /* Predicate::updateForMVEDeletion() */


    /**
     * updateForPVEDefChange()
     *
     * Scan the list of data values in the predicate, and pass an update for
     * predicate vocab element definition change message to any predicate
     * data values.
     *                                          JRM -- 3/23/08
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
        final String mName = "Predicate::updateForPVEDefChange(): ";
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

        if ( this.pveID == pveID )
        {
            if ( nameChanged )
            {
                if ( this.predName.compareTo(oldName) != 0 )
                {
                    throw new SystemErrorException(mName +
                                                   "unexpected old name.");
                }
                this.predName = new String(newName);
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

                newArgList = this.constructEmptyArgList(pve);
                numOldArgs = oldFargList.size();
                numNewArgs = newFargList.size();

                for ( i = 0; i < numOldArgs; i++ )
                {
                    if ( ! fargDeleted[i] )
                    {
                        dv = DataValue.Copy(this.getArg(i), true);

                        j = (int)o2n[i];

                        if ( ( fargNameChanged[j] ) ||
                             ( fargSubRangeChanged[j]) ||
                             ( fargRangeChanged[j] ) )
                        {
                            // Update the data value for the formal argument
                            // change.
                            dv.updateForFargChange(fargNameChanged[j],
                                                   fargSubRangeChanged[j],
                                                   fargRangeChanged[j],
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
        }

        return;

    } /* Predicate::updateForPVEDefChange() */


    /**
     * updateForPVEDeletion()
     *
     * It the supplied pveID mathes this.pveID, set this.pveID to INVALID_ID.
     *
     * Otherwise, if the predicate is defined, scan its argument list and
     * pass the update for pve deletion message to any column predicates or
     * predicates that may appear in the argument list.
     *
     *                                          JRM -- 3/23/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForPVEDeletion(Database db,
                                        long deletedPveID)
        throws SystemErrorException
    {
        final String mName = "Matrix::updateForPVEDeletion(): ";
        int i;
        int numArgs;
        DBElement dbe = null;
        PredicateVocabElement pve = null;
        FormalArgument fa = null;
        DataValue dv = null;
        PredDataValue pdv = null;

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( deletedPveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "deletedPveID invalid.");
        }

        if ( this.pveID == deletedPveID )
        {
            this.setPredID(DBIndex.INVALID_ID, false);
        }
        else if ( this.pveID != DBIndex.INVALID_ID )
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
                            if ( pve == null )
                            {
                                pve = this.lookupPredicateVE(this.pveID);
                            }

                            fa = pve.getFormalArg(i);

                            if ( fa == null )
                            {
                                throw new SystemErrorException(mName + "no " +
                                        i + "th formal argument?!?!");
                            }

                            dv = fa.constructEmptyArg();

                            dv.setItsPredID(this.id);

                            this.replaceArg(i, dv);
                        }
                        else if ( dv.getItsFargType() ==
                                FormalArgument.fArgType.PREDICATE )
                        {
                            ((PredDataValue)dv).updateForPVEDeletion(db, pveID);
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
                    ((ColPredDataValue)dv)
                            .updateForPVEDeletion(db, deletedPveID);
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
     * Proceed as per the no structural change case.
     *
     *
     * 2) cascadeMveDel == true
     *
     * If cascadeMveDel is true, the a mve has been deleted, and the ID of
     * the deleted mve is in cascadeMveID.
     *
     * Proceed as per the no structural change case.
     *
     *
     * 3) cascadePveMod == true
     *
     * If cascadePveMod is true, then a pve has been modified, and the ID of
     * the modified pve is in cascadeMveID.
     *
     * If cascadePveID == this.pveID, then the definition of the pve that
     * defines the predicate represented by this instance of Predicate
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
     * If cascadePveID != this.pveID, then we can proceed as per the no
     * structural change case -- for this predicate at least.
     *
     *
     * 4) cascadePveDel == true
     *
     * If cascadePveDel is true, then a pve has been deleted, and the ID of
     * the deleted pve is in cascadePveID.
     *
     * In this case, verify that this.pveID != cascadePveID, and then proceed
     * as per the no structural change case.
     *
     *                                      JRM -- 2/20/08
     *
     * Changes:
     *
     *    - None.
     */

    public void updateIndexForReplacement(Predicate oldPred,
                                          long DCID,
                                          boolean cascadeMveMod,
                                          boolean cascadeMveDel,
                                          long cascadeMveID,
                                          boolean cascadePveMod,
                                          boolean cascadePveDel,
                                          long cascadePveID)
        throws SystemErrorException
    {
        final String mName = "Predicate::updateIndexForReplacement(): ";
        int i = 0;
        PredicateVocabElement pve;
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

        if ( oldPred == null )
        {
            throw new SystemErrorException(mName + "oldPred is null");
        }

        if ( oldPred.cellID != DCID )
        {
            throw new SystemErrorException(mName + "oldPred DCID mismatch.");
        }

        if ( oldPred.pveID == DBIndex.INVALID_ID )
        {
            // old pred is undefined -- verify that it was correctly initialized

            if ( ( oldPred.predName == null ) ||
                 ( oldPred.predName.compareTo("") != 0 ) ||
                 ( oldPred.argList != null ) ||
                 ( oldPred.varLen != false ) )
            {
                throw new SystemErrorException(mName +
                        "undefined old pred with incorrect values");
            }
        }
        else if ( oldPred.argList == null )
        {
            throw new SystemErrorException(mName +
                    "oldPred.argList == null?!?!");
        }

        if ( oldPred.id == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "oldPred.id is invalid.");
        }


        if ( this.cellID != DCID )
        {
            throw new SystemErrorException(mName + "DCID mismatch.");
        }

        if ( this.pveID == DBIndex.INVALID_ID )
        {
            // undefined predicate -- verify that it is correctly initialized
            if ( ( this.predName == null ) ||
                 ( this.predName.compareTo("") != 0 ) ||
                 ( this.argList != null ) ||
                 ( this.varLen != false ) )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName +
                        "undefined pred with incorrect values");
            }
        }
        else if ( ( cascadePveDel ) && ( this.pveID == cascadePveID ) )
        {
            throw new SystemErrorException(mName +
                    "this.pveID == deleted pve ID?!?!?");
        }
        else if ( this.argList == null )
        {
            throw new SystemErrorException(mName + "this.argList == null?!?");
        }

        if ( ( this.id != DBIndex.INVALID_ID ) &&
             ( this.id != oldPred.id ) )
        {
            throw new SystemErrorException(mName +
                    "this.id not invalid and not equal to oldPred.id");
        }


        if ( this.pveID == DBIndex.INVALID_ID )
        {
            if ( oldPred.pveID == DBIndex.INVALID_ID )
            {
                this.db.idx.replaceElement(this);
            }
            else
            {
                // we are replacing a predicate with an undefined predicate.
                if ( this.id == DBIndex.INVALID_ID )
                {
                    oldPred.removeFromIndex(DCID);
                    this.insertInIndex(DCID);
                }
                else // this.id == oldPred.id
                {
                    // Remove the arguments of the old predicate from the
                    // index.
                    for ( DataValue dv : oldPred.argList )
                    {
                        dv.removeFromIndex(DCID);
                    }

                    // replace the old Predicate with the new in the index.
                    this.db.idx.replaceElement(this);
                }
            }
        }
        else if ( this.pveID != oldPred.pveID )
        {
            if ( oldPred.pveID == DBIndex.INVALID_ID )
            {
                // we are replacing an undefined predicate with a new predicate
                if ( this.id == DBIndex.INVALID_ID )
                {
                    oldPred.removeFromIndex(DCID);
                    this.insertInIndex(DCID);
                }
                else // this.id == oldPred.id
                {
                    this.db.idx.replaceElement(this);

                    // Insert the argument list of the new predicate in the
                    // index.
                    for ( DataValue dv : this.argList )
                    {
                        dv.insertInIndex(DCID);
                    }
                }
            }
            else // oldPred is defined, and referrs to some other pve
            {
                if ( this.id == DBIndex.INVALID_ID )
                {
                    oldPred.removeFromIndex(DCID);
                    this.insertInIndex(DCID);
                }
                else // this.id == oldPred.id
                {
                    this.db.idx.replaceElement(this);

                    for ( DataValue dv : oldPred.argList )
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
            // we have a new predicate that refers to the same pve as the
            // old.  Just remove the old predicate and all its parameters
            // from the index, and insert the new predicate and all its
            // parameters.
            assert( this.pveID == oldPred.pveID );
            assert( this.pveID != DBIndex.INVALID_ID );

            oldPred.removeFromIndex(DCID);
            this.insertInIndex(DCID);

            for ( DataValue dv : oldPred.argList )
            {
                dv.removeFromIndex(DCID);
            }

            for ( DataValue dv : this.argList )
            {
                dv.insertInIndex(DCID);
            }
        }
        else if ( ( ! cascadePveMod ) || ( cascadePveID != this.pveID ) )
            // note that from previous if statements, we also have:
            // ( this.id == oldPred.id ) &&
            // ( this.pveID == oldPred.pveID ) &&
            // ( this.pveID != DBIndex.INVALID_ID )
       {
            assert( this.id == oldPred.id );
            assert( this.pveID == oldPred.pveID );
            assert( this.pveID != DBIndex.INVALID_ID );

            this.db.idx.replaceElement(this);

            pve = this.lookupPredicateVE(this.pveID);

            while ( i < this.getNumArgs() )
            {
                // get the i-th formal argument.  This is the pve's actual
                // argument,  so be careful not to modify it in any way.
                fa = pve.getFormalArg(i);

                if ( fa == null )
                {
                    throw new SystemErrorException(mName + "no " + i +
                            "th formal argument?!?!");
                }
                else if ( fa instanceof TextStringFormalArg )
                {
                    throw new SystemErrorException(mName +
                            "pve contains a text formal arg?!?!");
                }

                // get the i'th arguments from the old and new argument
                // lists.  Again, these are the actual arguments -- must be
                // careful not to modify them in any way.
                newArg = this.argList.get(i);
                oldArg = oldPred.argList.get(i);

                if ( newArg == null )
                {
                    throw new SystemErrorException(mName + "no new" + i +
                            "th argument?!?!");
                }
                else if ( newArg instanceof TextStringDataValue )
                {
                    throw new SystemErrorException(mName +
                            "new pred contains text arguemnt?!?");
                }

                if ( oldArg == null )
                {
                    throw new SystemErrorException(mName + "no old" + i +
                            "th argument?!?!");
                }
                else if ( oldArg instanceof TextStringDataValue )
                {
                    throw new SystemErrorException(mName +
                            "old pred contains text arguemnt?!?");
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

                // Sanity checks pass.  If the ID's of old and new versions of
                // the argument match, replace the old incarnation of the formal
                // argument with the new in the index.
                //
                // Otherwise, remove the old from the index, and insert the new.
                if ( newArg.getID() == oldArg.getID() )
                {
                    newArg.replaceInIndex(oldArg,
                                          DCID,
                                          cascadeMveMod,
                                          cascadeMveDel,
                                          cascadeMveID,
                                          cascadePveMod,
                                          cascadePveDel,
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
             // ( cascadePveMod ) &&
             // ( cascadePveID == this.pveID ) &&
             // ( this.id == oldPred.id ) &&
             // ( this.pveID == oldPred.pveID ) &&
             // ( this.pveID != DBIndex.INVALID_ID )
       {
            assert( cascadePveMod );
            assert( cascadePveID == this.pveID );
            assert( this.id == oldPred.id );
            assert( this.pveID == oldPred.pveID );
            assert( this.pveID != DBIndex.INVALID_ID );

            // the pve whose definition underlies the old and new incarnations
            // of the predicate has changes -- thus it is possible that formal
            // arguments have shifted location, been removed, or added.  We
            // must update the index accordingly.
            //
            // Fortunately, we can count on the following:
            //
            // 1) If the formal argument associated with an argument has been
            //    removed, then the new version of the predicate will contain
            //    no argument with the same ID as that associated with the
            //    formal argument that has been removed.
            //
            // 2) If a formal argument has been added, then the argument
            //    associated with the formal argument in the new predicate
            //    will have the invalid id.
            //
            // With these two assurances in hand, we can process the two
            // argument lists as follows once the sanity checks pass:
            //
            // First, scan the old list for IDs that don't exist in the new
            // list.  Delete the associated entries from the index.
            //
            // Second scan the new list.  If an entry has invalid ID, just
            // insert it in the index.  If it has valid id, use it to replace
            // the entry in the old list with the same ID.  If no such old
            // argument exists, scream and die.

            this.db.idx.replaceElement(this);

            pve = this.lookupPredicateVE(this.pveID);

            // first remove unmatched old arguments from the index...
            i = 0;
            while ( i < oldPred.getNumArgs() )
            {
                int j = 0;
                boolean foundMatch = false;

                oldArg = oldPred.argList.get(i);

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
                // get the i-th formal argument.  This is the pve's actual
                // argument, so be careful not to modify it in any way.
                fa = pve.getFormalArg(i);

                if ( fa == null )
                {
                    throw new SystemErrorException(mName + "no " + i +
                            "th formal argument?!?!");
                }
                else if ( fa instanceof TextStringFormalArg )
                {
                    throw new SystemErrorException(mName +
                            "pve contains a text formal arg?!?!");
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
                else if ( newArg instanceof TextStringDataValue )
                {
                    throw new SystemErrorException(mName +
                            "new pred contains text arguemnt?!?");
                }

                if ( newArg.getID() != DBIndex.INVALID_ID )
                {
                    // the old argument list must contain an argument
                    // with the same ID.  Scan the list to find it.
                    int j = 0;

                    while ( ( j < oldPred.getNumArgs() ) &&
                            ( oldArg == null ) )
                    {
                        oldArg = oldPred.argList.get(j);

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

                if ( ( oldArg != null ) &&
                     ( oldArg instanceof TextStringDataValue ) )
                {
                    throw new SystemErrorException(mName +
                            "old pred contains text arguemnt?!?");
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
                    assert( this.id != DBIndex.INVALID_ID );

                    newArg.setItsPredID(this.id);
                    newArg.insertInIndex(DCID);
                }

                i++;

            } /* while */
        }

        return;

    } /* Predicate::updateIndexForReplacement() */


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
        final String mName = "Predicate::validateArgAsignment(): ";
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
        else if ( fa.getItsVocabElementID() != this.pveID )
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
                            "type mismatch: nominal DV, or " +
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

                    if ( ( pdv.getItsValue().getPveID() != DBIndex.INVALID_ID )
                            && ( pdv.getSubRange() ) &&
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
                throw new SystemErrorException(mName +
                        "pve contains a text formal arg?!?!");
                /* break statement commented out to keep the
                 * compiler happy
                 */
                // break;

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

    } /* Predicate::validateArgAsignment() */


    /**
     * validatePredicate()
     *
     * Verify that the predicate is consistant with the target
     * PredicateVocabElement (if andy).  This is purely
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

    public void validatePredicate(boolean idMustBeInvalid)
        throws SystemErrorException
    {
        final String mName = "Predicate::validatePredicate(): ";
        int i = 0;
        PredicateVocabElement pve;
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

        if ( this.pveID == DBIndex.INVALID_ID )
        {
            // undefined predicate -- verify that it is correctly initialized
            if ( ( this.predName == null ) ||
                 ( this.predName.compareTo("") != 0 ) ||
                 ( this.argList != null ) ||
                 ( this.varLen != false ) )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName +
                        "undefined pred with incorrect values");
            }
        }
        else
        {
            if ( argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!");
            }
            else if ( this.getNumArgs() <= 0 )
            {
                throw new SystemErrorException(mName
                                                    + "this.getNumArgs() <= 0");
            }

            pve = this.lookupPredicateVE(this.pveID);

            if ( pve.getDB() != this.getDB() )
            {
                throw new SystemErrorException(mName +
                                               "pve.getDB() != this.getDB()");
            }

            if ( pve.getNumFormalArgs() != this.getNumArgs() )
            {
                throw new SystemErrorException(mName +
                                 "pve.getNumFormalArgs() != this.getNumArgs()");
            }

            if ( pve.getVarLen() != this.getVarLen() )
            {
                throw new SystemErrorException(mName +
                                     "pve.getVarLen() != this.getValLen()");
            }


            // Now scan the argument list
            this.validatePredicateArgList(pve, idMustBeInvalid);
        }

        return;

    } /* Predicate::validatePredicate() */


    /**
     * validatePredicateArgList()
     *
     * Verify that the arguments of the predicate are of type and value
     * consistant with the target PredicateVocabElement.  This is purely
     * a sanity checking routine.  The test should always pass.
     *
     * The idMustBeInvalid parameter is used to inforce the requirement that
     * it any DataValue or Predicate has not been inserted in the index, then
     * none of its descendant may have been inserted in the index either.
     *
     *                                              JRM -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    private void validatePredicateArgList(PredicateVocabElement pve,
                                         boolean idMustBeInvalid)
        throws SystemErrorException
    {
        final String mName = "Predicate::validatePredicate(): ";
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

            // get the i-th formal argument.  This is the pve's actual argument,
            // so be careful not to modify it in any way.
            fa = pve.getFormalArg(i);

            if ( fa == null )
            {
                throw new SystemErrorException(mName + "no " + i +
                        "th formal argument?!?!");
            }
            else if ( fa instanceof TextStringFormalArg )
            {
                throw new SystemErrorException(mName +
                        "pve contains a text formal arg?!?!");
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
            else if ( arg instanceof TextStringDataValue )
            {
                throw new SystemErrorException(mName +
                        "pred contains text arguemnt?!?");
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

    } /* Predicate::validatePredicateArgList() */


    /**
     * validateReplacementArg()
     *
     * Given a reference to a formal argument, the old value of that argument,
     * and a proposed replacement argument, verify that the new argument is a
     * valid replacement value.  Note that the old argument may be null if
     * cascadePveMod is true, and cascadePveID == this.pveID.  The old argument
     * must be defined in all other cases.
     *
     * This method will typically be called during a cascade, and thus the
     * cascade parameters are passed in as they may be needed.  The only ones
     * used in this function are cascadePveMod and cascadePveID, which are
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
        final String mName = "Predicate::validateReplacementArg(): ";
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

        // TODO: Delete this eventually
//        System.out.printf("%s: cascade MVE mod/del/id = %b/%b/%d\n",
//                mName, cascadeMveMod, cascadeMveDel, cascadeMveID);
//        System.out.printf("%s: cascade PVE mod/del/id = %b/%b/%d\n",
//                mName, cascadePveMod, cascadePveDel, cascadePveID);
//        System.out.printf("%s: this.pveID = %d\n",
//                mName, this.pveID);

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry.");
        }
        else if ( fa.getItsVocabElementID() != this.pveID )
        {
            throw new SystemErrorException(mName + "fa has unexpected veID.");
        }

        if ( ( oldArg == null ) &&
             ( ( ! cascadePveMod ) || ( cascadePveID != this.pveID ) ) )
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
                throw new SystemErrorException(mName +
                        "pve contains a text formal arg?!?!");
                /* break statement commented out to keep the
                 * compiler happy
                 */
                // break;

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
                                        cascadePveMod,
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
                                        cascadePveMod,
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

    } /* Predicate::validateReplacementArg() */


    /**
     * validateReplacementPredicate()
     *
     * Verify that this Predicate is a valid replacement for the supplied
     * old Predicate.  This method is called when a new version of a
     * DataCell is about to replace an old version as the cannonical incarnation
     * of the DataCell.  This is purely a sanity checking routine.  The test
     * should always pass.
     *
     * In all cases, this requires that we verify that if the predicate
     * is defined, the argument list of the predicate is congruent with
     * the predicate formal argument list supplied by the target pveID.
     *
     * Further, if oldPred is defined, has this same ID as this, and has the
     * same target pve as this, verify that all arguments either have invalid ID
     * or have an argument of matching type in oldPred with the same ID.
     * Unless the target pve has been modified (i.e. cascadePveMod == true and
     * cascadePveID == this.pveID), these matching arguments must be in the same
     * location in oldPred's argument list.
     *
     * If oldPred is either undefined, or has a different ID or target ve,
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
     * Proceed as above.
     *
     *
     * 2) cascadeMveDel == true
     *
     * If cascadeMveDel is true, the a mve has been deleted, and the ID of
     * the deleted mve is in cascadeMveID.
     *
     * Proceed as above.
     *
     *
     * 3) cascadePveMod == true
     *
     * If cascadePveMod is true, then a pve has been modified, and the ID of
     * the modified pve is in cascadeMveID.
     *
     * If cascadePveID == this.pveID, then the definition of the pve that
     * definses the predicate represented by this instance of Predicate
     * has changed.  Processing is as described above.
     *
     *
     * 4) cascadePveDel == true
     *
     * If cascadePveDel is true, then a pve has been deleted, and the ID of
     * the deleted pve is in cascadePveID.
     *
     * In this case, verify that this.pveID != cascadePveID, and then proceed
     * as above.
     *
     *                                              JRM -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    public void validateReplacementPredicate(Predicate oldPred,
                                             boolean cascadeMveMod,
                                             boolean cascadeMveDel,
                                             long cascadeMveID,
                                             boolean cascadePveMod,
                                             boolean cascadePveDel,
                                             long cascadePveID)
        throws SystemErrorException
    {
        final String mName = "Predicate::validateReplacementPredicate(): ";
        int i = 0;
        boolean idMustBeInvalid = false;
        PredicateVocabElement pve;
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

// todo:  delete this eventually
//        System.out
// .printf("ValidateReplacementPredicate: cascade mve mod/del/id = %s/%s/%d.\n",
//                          ((Boolean)cascadeMveMod).toString(),
//                          ((Boolean)cascadeMveDel).toString(),
//                          cascadeMveID);
//        System.out
// .printf("ValidateReplacementPredicate: cascade pve mod/del/id = %s/%s/%d.\n",
//                          ((Boolean)cascadePveMod).toString(),
//                          ((Boolean)cascadePveDel).toString(),
//                          cascadePveID);

        if ( oldPred == null )
        {
            throw new SystemErrorException(mName + "oldPred is null");
        }
        else if ( oldPred.pveID == DBIndex.INVALID_ID )
        {
            // old pred is undefined -- verify that it was correctly initialized

            if ( ( oldPred.predName == null ) ||
                 ( oldPred.predName.compareTo("") != 0 ) ||
                 ( oldPred.argList != null ) ||
                 ( oldPred.varLen != false ) )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName +
                        "undefined old pred with incorrect values");
            }

            idMustBeInvalid = true;
        }

        if ( this.id == DBIndex.INVALID_ID )
        {
            idMustBeInvalid = true;
        }

        if ( this.pveID == DBIndex.INVALID_ID )
        {
            // undefined predicate -- verify that it is correctly initialized
            if ( ( this.predName == null ) ||
                 ( this.predName.compareTo("") != 0 ) ||
                 ( this.argList != null ) ||
                 ( this.varLen != false ) )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName +
                        "undefined pred with incorrect values");
            }
        }
        else if ( ( cascadePveDel ) && ( this.pveID == cascadePveID ) )
        {
            throw new SystemErrorException(mName +
                    "this.pveID == deleted pve ID?!?!?");
        }
        else
        {
            if ( argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!");
            }
            else if ( this.getNumArgs() <= 0 )
            {
                throw
                     new SystemErrorException(mName + "this.getNumArgs() <= 0");
            }

            if ( oldPred.pveID == this.pveID )
            {
                 if ( ( oldPred.getNumArgs() != this.getNumArgs() ) &&
                      ( ( ! cascadePveMod ) || ( cascadePveID != this.pveID ) ))
                 {
                    // todo: delete this eventually
//                     System.out.printf("cascade mve mod/del/id = %s/%s/%d.\n",
//                                       ((Boolean)cascadeMveMod).toString(),
//                                       ((Boolean)cascadeMveDel).toString(),
//                                       cascadeMveID);
//                     System.out.printf("cascade pve mod/del/id = %s/%s/%d.\n",
//                                       ((Boolean)cascadePveMod).toString(),
//                                       ((Boolean)cascadePveDel).toString(),
//                                       cascadePveID);
//                     int q = 1/0;
                     throw
                         new SystemErrorException(mName + "num args mismatch*");
                 }
            }
            else // target pve changed
            {
                idMustBeInvalid = true;
            }

            pve = this.lookupPredicateVE(this.pveID);

            if ( pve.getDB() != this.getDB() )
            {
                throw new SystemErrorException(mName +
                                               "pve.getDB() != this.getDB()");
            }

            if ( pve.getNumFormalArgs() != this.getNumArgs() )
            {
                throw new SystemErrorException(mName +
                                 "pve.getNumFormalArgs() != this.getNumArgs()");
            }

            if ( pve.getVarLen() != this.getVarLen() )
            {
                throw new SystemErrorException(mName +
                                     "pve.getVarLen() != this.getValLen()");
            }


            // Now scan the argument list
            if ( idMustBeInvalid )
            {
                this.validatePredicateArgList(pve, true);
            }
            else if ( ( ! cascadePveMod ) || ( cascadePveID != this.pveID ) )
            {
                while ( i < this.getNumArgs() )
                {
                    // get the i-th formal argument.  This is the pve's actual
                    // argument,  so be careful not to modify it in any way.
                    fa = pve.getFormalArg(i);

                    if ( fa == null )
                    {
                        throw new SystemErrorException(mName + "no " + i +
                                "th formal argument?!?!");
                    }
                    else if ( fa instanceof TextStringFormalArg )
                    {
                        throw new SystemErrorException(mName +
                                "pve contains a text formal arg?!?!");
                    }

                    // get the i'th arguments from the old and new argument
                    // lists.  Again, these are the actual arguments -- must be
                    // careful not to modify them in any way.
                    newArg = this.argList.get(i);
                    oldArg = oldPred.argList.get(i);

                    if ( newArg == null )
                    {
                        throw new SystemErrorException(mName + "no new" + i +
                                "th argument?!?!");
                    }
                    else if ( newArg instanceof TextStringDataValue )
                    {
                        throw new SystemErrorException(mName +
                                "new pred contains text arguemnt?!?");
                    }

                    if ( oldArg == null )
                    {
                        throw new SystemErrorException(mName + "no old" + i +
                                "th argument?!?!");
                    }
                    else if ( oldArg instanceof TextStringDataValue )
                    {
                        throw new SystemErrorException(mName +
                                "old pred contains text arguemnt?!?");
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
            else
            {
                /* The definition of the pve defining both the old and
                 * new versions of the predicate has changed.  Thus it is
                 * possible that the formal argument list has changed
                 * as well.
                 *
                 * Verify that each of the arguments in the new predicate
                 * match the pve.  Further, for each argument in the new
                 * predicate with a valid id, verify that there is an
                 * argument in the old predicate with the same id and type.
                 */
                while ( i < this.getNumArgs() )
                {
                    // get the i-th formal argument.  This is the pve's actual
                    // argument,  so be careful not to modify it in any way.
                    fa = pve.getFormalArg(i);

                    if ( fa == null )
                    {
                        throw new SystemErrorException(mName + "no " + i +
                                "th formal argument?!?!");
                    }
                    else if ( fa instanceof TextStringFormalArg )
                    {
                        throw new SystemErrorException(mName +
                                "pve contains a text formal arg?!?!");
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
                    else if ( newArg instanceof TextStringDataValue )
                    {
                        throw new SystemErrorException(mName +
                                "new pred contains text arguemnt?!?");
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

                        while ( ( j < oldPred.getNumArgs() ) &&
                                ( oldArg == null ) )
                        {
                            oldArg = oldPred.argList.get(j);

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
    @Override
    protected void clearID()
        throws SystemErrorException
    {
        final String mName = "Predicate::clearID()";
        super.clearID();

        if ( this.pveID != DBIndex.INVALID_ID )
        {
            if ( this.argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!");
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
     * constructing instances of Predicate.
     *
     * Returns a reference to the newly constructed predicate if successful.
     * Throws a system error exception on failure.
     *
     *                                              JRM -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static Predicate Construct(Database db,
                                      long pveID,
                                      DataValue arg0)
        throws SystemErrorException
    {
        final String mName = "Predicate::Construct(db, pveID, arg0)";
        Predicate p = null;

        p = new Predicate(db, pveID);

        if ( arg0 != null )
        {
            p.replaceArg(0, arg0);
        }

        return p;

    } /* Predicate::Construct(db, pveID, arg0) */


    public static Predicate Construct(Database db,
                                      long pveID,
                                      DataValue arg0,
                                      DataValue arg1)
        throws SystemErrorException
    {
        final String mName = "Predicate::Construct(db, pveID, arg0, arg1)";
        Predicate p = null;

        p = Predicate.Construct(db, pveID, arg0);

        if ( arg1 != null )
        {
            p.replaceArg(1, arg1);
        }

        return p;

    } /* Predicate::Construct(db, pveID, arg0, arg1) */


    public static Predicate Construct(Database db,
                                      long pveID,
                                      DataValue arg0,
                                      DataValue arg1,
                                      DataValue arg2)
        throws SystemErrorException
    {
        final String mName = "Predicate::Construct(db, pveID, arg0, arg1,arg2)";
        Predicate p = null;

        p = Predicate.Construct(db, pveID, arg0, arg1);

        if ( arg2 != null )
        {
            p.replaceArg(2, arg2);
        }

        return p;

    } /* Predicate::Construct(db, pveID, arg0, arg1, arg2) */


    public static Predicate Construct(Database db,
                                      long pveID,
                                      DataValue arg0,
                                      DataValue arg1,
                                      DataValue arg2,
                                      DataValue arg3)
        throws SystemErrorException
    {
        final String mName =
                "Predicate::Construct(db, pveID, arg0, arg1, arg2, arg3)";
        Predicate p = null;

        p = Predicate.Construct(db, pveID, arg0, arg1, arg2);

        if ( arg3 != null )
        {
            p.replaceArg(3, arg3);
        }

        return p;

    } /* Predicate::Construct(db, pveID, arg0, arg1, arg2, arg3) */


    public static Predicate Construct(Database db,
                                      long pveID,
                                      DataValue arg0,
                                      DataValue arg1,
                                      DataValue arg2,
                                      DataValue arg3,
                                      DataValue arg4)
        throws SystemErrorException
    {
        final String mName =
                "Predicate::Construct(db, pveID, arg0, arg1, arg2, arg3, arg4)";
        Predicate p = null;

        p = Predicate.Construct(db, pveID, arg0, arg1, arg2, arg3);

        if ( arg4 != null )
        {
            p.replaceArg(4, arg4);
        }

        return p;

    } /* Predicate::Construct(db, pveID, arg0, arg1, arg2, arg3, arg4) */


    public static Predicate Construct(Database db,
                                      long pveID,
                                      DataValue arg0,
                                      DataValue arg1,
                                      DataValue arg2,
                                      DataValue arg3,
                                      DataValue arg4,
                                      DataValue arg5)
        throws SystemErrorException
    {
        final String mName = "Predicate::Construct(db, pveID, arg0, arg1, " +
                                                   "arg2, arg3, arg4, arg5)";
        Predicate p = null;

        p = Predicate.Construct(db, pveID, arg0, arg1, arg2, arg3, arg4);

        if ( arg5 != null )
        {
            p.replaceArg(5, arg5);
        }

        return p;

    } /* Predicate::Construct(db, pveID, arg0, arg1, arg2, arg3, arg4, arg5) */

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
        hash += (int)(pveID ^ (pveID >>> 32)) * SEED1;
        hash += (predName == null ? 0 : predName.hashCode()) * SEED2;
        hash += (argList == null ? 0 : argList.hashCode()) * SEED3;
        hash += (varLen ? 1 : 0) * SEED4;
        hash += (int)(cellID ^ (cellID >>> 32)) * SEED5;
        hash += (queryVarOK ? 1 : 0) * SEED6;

        return hash;
    }

    /**
     * Compares this NominalDataValue against another object.
     *
     * @param obj The object to compare this against.
     *
     * @return true if the Object obj is logically equal.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        // Must be this class to be here
        Predicate p = (Predicate) obj;
        return pveID == p.pveID
                && (predName == null ? p.predName == null
                        : predName.equals(p.predName))
                && (argList == null ? p.argList == null
                        : argList.equals(p.argList))
                && varLen == p.varLen
                && cellID == p.cellID
                && queryVarOK == p.queryVarOK
                && super.equals(obj);
    }



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
     *         of Database, and returns an instance of Predicate.  Verify that:
     *
     *              pred.db matches supplied value
     *              pred.predID == DBIndex.INVALID_ID
     *              pred.predName == ""
     *              pred.argList == NULL
     *              pred.varLen == false
     *
     *      b) Verify that constructor fails when passed an invalid db.
     *
     * 2) Two argument constructor:
     *
     *      a) Construct a database and a pve (predicate vocab element).
     *         Insert the pve in the database, and make note of the id
     *         assigned to the pve.  Construct a Predicate passing a reference
     *         to the database and the id of the pve.  Verify that:
     *
     *              pred.db matches the suplied value
     *              pred.predID matches the supplied value
     *              pred.predName matches the name of the pve
     *              pred.argList reflects the formal argument list of the pve
     *              pred.varLen matches the varLen field of the pve.
     *
     *          Do this with both a single entry and a multi-entry predicate,
     *          and with both a fixed length and a variable length predicate
     *
     *      b) Verify that the constructor fails when passed and invalid
     *         db or an invalid pve id.
     *
     * 3) Three argument constructor:
     *
     *      a) Construct a database and a pve (predicate vocab element).
     *         Insert the pve in the database, and make note of the id
     *         assigned to the pve.  Construct an argument list assigned
     *         matching the arg list of the pve.  Construct a Predicate,
     *         passing the db, the id of the pve, and the arg list.  Verify
     *         that:
     *
     *              pred.db matches the suplied value
     *              pred.predID matches the supplied value
     *              pred.predName matches the name of the pve
     *              pred.argList reflects both the formal argument list of
     *                  the pve and the supplied argument list.
     *              pred.varLen matches the varLen field of the pve.
     *
     *          Do this with both a single entry and a multi-entry predicate,
     *          and with both a fixed length and a variable length predicate.
     *
     *      b) Verify that the constructor fails when passed an invalid db,
     *         an invalid pve id, or an invalid argument list.  Note that
     *         we must test argument lists that are null, too short, too long,
     *         and which contain type mis-matches.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and several pve's (predicate vocab
     *         elements). Insert the pve's in the database, and make note
     *         of the id's assigned to the pve's.  Using these pve's, construct
     *         a selection of predicates with and without argument lists, and
     *         with and without initializations to arguments.
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
     *      Verify that the getPredID(), setPredID(), getDB(), getNumArgs(),
     *      getVarLen(), and getPredName() methods perform correctly.
     *
     *      Do this by creating a database and a selection of predicate vocab
     *      elements.  Then create a selection of predicates, and verify that
     *      get methods return the expected values.  Then use setPredID() to
     *      change the pve ID associated with the predicates, and verify that
     *      values returned by the get methods have changed accordingly.
     *
     *      Verify that setPredID() fails when given invalid input.
     *
     *      lookupPredicateVE() is an internal method that has been exercised
     *      already.  Verify that it fails on invalid input.
     *
     * 6) ArgList management:
     *
     *      Verify that argument lists are converted properly when the predID
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
     * TestClassPredicate()
     *
     * Main routine for tests of class Predicate.
     *
     *                                      JRM -- 10/15/07
     *
     * Changes:
     *
     *    - Non.
     */

    public static boolean TestClassPredicate(java.io.PrintStream outStream,
                                             boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;

        outStream.print("Testing class Predicate:\n");

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

        // TODO:  Add test for validatePredicate

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

    } /* Predicate::TestClassPredicate() */


    /**
     * Test1ArgConstructor()
     *
     * Run a battery of tests on the one argument constructor for this
     * class, and on the instance returned.
     *
     *                                              JRM -- 10/15/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test1ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing 1 argument constructor for class Predicate               ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        Predicate pred = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        pred = null;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();
            pred = new Predicate(db);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( pred == null ) ||
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

                if ( pred == null )
                {
                    outStream.print(
                            "new Predicate(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new Predicate(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred.getDB() != db )
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
            if ( pred.getPveID() != DBIndex.INVALID_ID )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predID: %ld.\n",
                            pred.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred.getPredName().compareTo("") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predName: \"%s\".\n",
                            pred.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred.argList != null )
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
            if ( pred.getVarLen() != false )
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
            pred = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pred = new Predicate((Database)null);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pred != null ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("new Predicate(null) returned.\n");
                    }

                    if ( pred != null )
                    {
                        outStream.print(
                                "new Predicate(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new Predicate(null) failed to throw " +
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

    } /* Predicate::Test1ArgConstructor() */


    /**
     * Test2ArgConstructor()
     *
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instance returned.
     *
     *                                              JRM -- 10/15/07
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
            "Testing 2 argument constructor for class Predicate               ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long predID0 = DBIndex.INVALID_ID;
        long predID1 = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        FormalArgument farg = null;
        Predicate pred0 = null;
        Predicate pred1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "test0");

            farg = new FloatFormalArg(db, "<float>");
            pve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve0.appendFormalArg(farg);

            predID0 = db.addPredVE(pve0);

            pred0 = new Predicate(db, predID0);

            pve1 = new PredicateVocabElement(db, "test1");

            farg = new UnTypedFormalArg(db, "<arg>");
            pve1.appendFormalArg(farg);

            pve1.setVarLen(true);

            predID1 = db.addPredVE(pve1);

            pred1 = new Predicate(db, predID1);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( predID0 == DBIndex.INVALID_ID ) ||
             ( pred0 == null ) ||
             ( pve1 == null ) ||
             ( predID1 == DBIndex.INVALID_ID ) ||
             ( pred1 == null ) ||
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

                if ( predID0 == DBIndex.INVALID_ID )
                {
                    outStream.print("insertion of pve0 failed.\n");
                }

                if ( pred0 == null )
                {
                    outStream.print(
                            "new Predicate(db, predID0() returned null.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( predID1 == DBIndex.INVALID_ID )
                {
                    outStream.print("insertion of pve1 failed.\n");
                }

                if ( pred1 == null )
                {
                    outStream.print(
                            "new Predicate(db, predID1() returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print(
                                 "test setup threw a SystemErrorException.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0.db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getPveID() != predID0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predID0: %ld.\n",
                            pred0.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getPredName().compareTo("test0") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predName: \"%s\".\n",
                            pred0.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.argList == null )
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
            if ( pred0.getVarLen() != pve0.getVarLen() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.argListToString().compareTo(
                    "(0.0, 0, , (), \"\", 00:00:00:000, <untyped>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected arg list string: \"%s\".\n",
                                     pred0.argListToString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.argListToDBString().compareTo(
                    "(argList ((FloatDataValue (id 0) " +
                                              "(itsFargID 2) " +
                                              "(itsFargType FLOAT) " +
                                              "(itsCellID 0) " +
                                              "(itsValue 0.0) " +
                                              "(subRange false) " +
                                              "(minVal 0.0) " +
                                              "(maxVal 0.0)), " +
                              "(IntDataValue (id 0) " +
                                            "(itsFargID 3) " +
                                            "(itsFargType INTEGER) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 0) " +
                                            "(subRange false) " +
                                            "(minVal 0) " +
                                            "(maxVal 0)), " +
                              "(NominalDataValue (id 0) " +
                                                "(itsFargID 4) " +
                                                "(itsFargType NOMINAL) " +
                                                "(itsCellID 0) " +
                                                "(itsValue <null>) " +
                                                "(subRange false)), " +
                              "(PredDataValue (id 0) " +
                                             "(itsFargID 5) " +
                                             "(itsFargType PREDICATE) " +
                                             "(itsCellID 0) " +
                                             "(itsValue ()) " +
                                             "(subRange false)), " +
                              "(QuoteStringDataValue (id 0) " +
                                                "(itsFargID 6) " +
                                                "(itsFargType QUOTE_STRING) " +
                                                "(itsCellID 0) " +
                                                "(itsValue <null>) " +
                                                "(subRange false)), " +
                             "(TimeStampDataValue (id 0) " +
                                               "(itsFargID 7) " +
                                               "(itsFargType TIME_STAMP) " +
                                               "(itsCellID 0) " +
                                               "(itsValue (60,00:00:00:000)) " +
                                               "(subRange false)), " +
                             "(UndefinedDataValue (id 0) " +
                                                 "(itsFargID 8) " +
                                                 "(itsFargType UNTYPED) " +
                                                 "(itsCellID 0) " +
                                                 "(itsValue <untyped>) " +
                                                 "(subRange false))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred0 db arg list string: \"%s\".\n",
                            pred0.argListToDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred1.db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getPveID() != predID1 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predID1: %ld.\n",
                            pred1.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getPredName().compareTo("test1") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "Unexpected initial value of pred1.predName: \"%s\".\n",
                        pred1.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.argList == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1.argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getVarLen() != pve1.getVarLen() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1.varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.argListToString().compareTo("(<arg>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1.arg list string: \"%s\".\n",
                            pred1.argListToString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.argListToDBString().compareTo(
                    "(argList ((UndefinedDataValue (id 0) " +
                                                 "(itsFargID 10) " +
                                                 "(itsFargType UNTYPED) " +
                                                 "(itsCellID 0) " +
                                                 "(itsValue <arg>) " +
                                                 "(subRange false))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1 db arg list string: \"%s\".\n",
                            pred1.argListToDBString());
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db */
        pred0 = null;
        completed = false;
        threwSystemErrorException = false;

        try
        {
            pred0 = new Predicate(null, predID0);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( pred0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "new Predicate(null, predID0) != null.\n");
                }

                if ( completed )
                {
                    outStream.print("new Predicate(null, predID0)completed.\n");

                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "new Predicate(null, predID0) " +
                            "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now verify that the constructor fails when passed an invalid
         * predicate vocab element ID.
         */
        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            pred0 = new Predicate(new ODBCDatabase(), predID0);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "\"new Predicate(db, bad_pred_id) "
                            + "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "\"new Predicate(db, bad_pred_id) "
                            + "didn't throw a SystemErrorException.\n");
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

    } /* Predicate::Test2ArgConstructor() */

    /**
     * Test3ArgConstructor()
     *
     * Run a battery of tests on the three argument constructor for this
     * class, and on the instance returned.
     *
     *                                              JRM -- 10/21/07
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
            "Testing 3 argument constructor for class Predicate               ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        String testArgString0 = "(1.0, 2, a_nominal, " +
                    "test0(0.0, 0, , (), \"\", 00:00:00:000, <untyped>), " +
                    "\"q-string\", 00:00:00:000, <untyped>)";
        String testArgDBString0 =
                    "(argList ((FloatDataValue (id 0) " +
                                              "(itsFargID 2) " +
                                              "(itsFargType FLOAT) " +
                                              "(itsCellID 0) " +
                                              "(itsValue 1.0) " +
                                              "(subRange false) " +
                                              "(minVal 0.0) " +
                                              "(maxVal 0.0)), " +
                              "(IntDataValue (id 0) " +
                                            "(itsFargID 3) " +
                                            "(itsFargType INTEGER) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 2) " +
                                            "(subRange false) " +
                                            "(minVal 0) " +
                                            "(maxVal 0)), " +
                              "(NominalDataValue (id 0) " +
                                                "(itsFargID 4) " +
                                                "(itsFargType NOMINAL) " +
                                                "(itsCellID 0) " +
                                                "(itsValue a_nominal) " +
                                                "(subRange false)), " +
                              "(PredDataValue (id 0) " +
                                  "(itsFargID 5) " +
                                  "(itsFargType PREDICATE) " +
                                  "(itsCellID 0) " +
                                  "(itsValue (predicate (id 0) " +
                                     "(predID 1) "+
                                     "(predName test0) " +
                                     "(varLen false) " +
                                     "(argList " +
                                         "((FloatDataValue (id 0) " +
                                             "(itsFargID 2) " +
                                             "(itsFargType FLOAT) " +
                                             "(itsCellID 0) " +
                                             "(itsValue 0.0) " +
                                             "(subRange false) " +
                                             "(minVal 0.0) " +
                                             "(maxVal 0.0)), " +
                                          "(IntDataValue (id 0) " +
                                              "(itsFargID 3) " +
                                              "(itsFargType INTEGER) " +
                                              "(itsCellID 0) " +
                                              "(itsValue 0) " +
                                              "(subRange false) " +
                                              "(minVal 0) " +
                                              "(maxVal 0)), " +
                                          "(NominalDataValue (id 0) " +
                                              "(itsFargID 4) " +
                                              "(itsFargType NOMINAL) " +
                                              "(itsCellID 0) " +
                                              "(itsValue <null>) " +
                                              "(subRange false)), " +
                                          "(PredDataValue (id 0) " +
                                              "(itsFargID 5) " +
                                              "(itsFargType PREDICATE) " +
                                              "(itsCellID 0) " +
                                              "(itsValue ()) " +
                                              "(subRange false)), " +
                                          "(QuoteStringDataValue (id 0) " +
                                              "(itsFargID 6) " +
                                              "(itsFargType QUOTE_STRING) " +
                                              "(itsCellID 0) " +
                                              "(itsValue <null>) " +
                                              "(subRange false)), " +
                                          "(TimeStampDataValue (id 0) " +
                                              "(itsFargID 7) " +
                                              "(itsFargType TIME_STAMP) " +
                                              "(itsCellID 0) " +
                                              "(itsValue (60,00:00:00:000)) " +
                                              "(subRange false)), " +
                                          "(UndefinedDataValue (id 0) " +
                                              "(itsFargID 8) " +
                                              "(itsFargType UNTYPED) " +
                                              "(itsCellID 0) " +
                                              "(itsValue <untyped>) " +
                                              "(subRange false))))))) " +
                                      "(subRange false)), " +
                              "(QuoteStringDataValue (id 0) " +
                                                "(itsFargID 6) " +
                                                "(itsFargType QUOTE_STRING) " +
                                                "(itsCellID 0) " +
                                                "(itsValue q-string) " +
                                                "(subRange false)), " +
                              "(TimeStampDataValue (id 0) " +
                                                "(itsFargID 7) " +
                                                "(itsFargType TIME_STAMP) " +
                                                "(itsCellID 0) " +
                                                "(itsValue (60,00:00:00:000)) "+
                                                "(subRange false)), " +
                              "(UndefinedDataValue (id 0) " +
                                                  "(itsFargID 8) " +
                                                  "(itsFargType UNTYPED) " +
                                                  "(itsCellID 0) " +
                                                  "(itsValue <untyped>) " +
                                                  "(subRange false))))";
        String testArgString1 = "(<val>)";
        String testArgDBString1 = "(argList ((UndefinedDataValue (id 0) " +
                                                 "(itsFargID 10) " +
                                                 "(itsFargType UNTYPED) " +
                                                 "(itsCellID 0) " +
                                                 "(itsValue <val>) " +
                                                 "(subRange false))))";
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long predID0 = DBIndex.INVALID_ID;
        long predID1 = DBIndex.INVALID_ID;
        long fargID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        FormalArgument farg = null;
        Vector<DataValue> argList;
        Vector<DataValue> argList0 = null;
        Vector<DataValue> argList0a = null;
        Vector<DataValue> argList1 = null;
        Vector<DataValue> argList1a = null;
        DataValue arg = null;
        Predicate pred0 = null;
        Predicate pred0a = null;
        Predicate pred1 = null;
        Predicate pred1a = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "test0");

            farg = new FloatFormalArg(db, "<float>");
            pve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve0.appendFormalArg(farg);

            predID0 = db.addPredVE(pve0);

            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(predID0);


            argList0 = new Vector<DataValue>();

            fargID = pve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 1.0);
            argList0.add(arg);
            fargID = pve0.getFormalArg(1).getID();
            arg = new IntDataValue(db, fargID, 2);
            argList0.add(arg);
            fargID = pve0.getFormalArg(2).getID();
            arg = new NominalDataValue(db, fargID, "a_nominal");
            argList0.add(arg);
            fargID = pve0.getFormalArg(3).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, predID0));
            argList0.add(arg);
            fargID = pve0.getFormalArg(4).getID();
            arg = new QuoteStringDataValue(db, fargID, "q-string");
            argList0.add(arg);
            fargID = pve0.getFormalArg(5).getID();
            arg = new TimeStampDataValue(db, fargID,
                                         new TimeStamp(db.getTicks()));
            argList0.add(arg);
            fargID = pve0.getFormalArg(6).getID();
            arg = new UndefinedDataValue(db, fargID,
                                         pve0.getFormalArg(6).getFargName());
            argList0.add(arg);

            pred0 = new Predicate(db, predID0, argList0);


            argList0a = new Vector<DataValue>();

            arg = new FloatDataValue(db);
            ((FloatDataValue)arg).setItsValue(1.0);
            argList0a.add(arg);
            arg = new IntDataValue(db);
            ((IntDataValue)arg).setItsValue(2);
            argList0a.add(arg);
            arg = new NominalDataValue(db);
            ((NominalDataValue)arg).setItsValue("a_nominal");
            argList0a.add(arg);
            arg = new PredDataValue(db);
            ((PredDataValue)arg).setItsValue(new Predicate(db, predID0));
            argList0a.add(arg);
            arg = new QuoteStringDataValue(db);
            ((QuoteStringDataValue)arg).setItsValue("q-string");
            argList0a.add(arg);
            arg = new TimeStampDataValue(db);
            ((TimeStampDataValue)arg).setItsValue(new TimeStamp(db.getTicks()));
            argList0a.add(arg);
            arg = new UndefinedDataValue(db);
            ((UndefinedDataValue)arg).setItsValue(
                    pve0.getFormalArg(6).getFargName());
            argList0a.add(arg);

            pred0a = new Predicate(db, predID0, argList0a);


            pve1 = new PredicateVocabElement(db, "test1");

            farg = new UnTypedFormalArg(db, "<val>");
            pve1.appendFormalArg(farg);

            pve1.setVarLen(true);


            predID1 = db.addPredVE(pve1);

            // get a copy of the databases version of pve0 with ids assigned
            pve1 = db.getPredVE(predID1);


            argList1 = new Vector<DataValue>();

            fargID = pve1.getFormalArg(0).getID();
            arg = new UndefinedDataValue(db, fargID,
                                         pve1.getFormalArg(0).getFargName());
            argList1.add(arg);

            pred1 = new Predicate(db, predID1, argList1);


            argList1a = new Vector<DataValue>();

            arg = new UndefinedDataValue(db);
            argList1a.add(arg);

            pred1a = new Predicate(db, predID1, argList1a);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( predID0 == DBIndex.INVALID_ID ) ||
             ( argList0 == null ) ||
             ( argList0.size() != 7 ) ||
             ( ! ( argList0.elementAt(0) instanceof FloatDataValue ) ) ||
             ( ((FloatDataValue)(argList0.elementAt(0))).getItsValue() !=
                1.0 ) ||
             ( ! ( argList0.elementAt(1) instanceof IntDataValue ) ) ||
             ( ((IntDataValue)(argList0.elementAt(1))).getItsValue() != 2 ) ||
             ( ! ( argList0.elementAt(2) instanceof NominalDataValue ) ) ||
             ( ((NominalDataValue)(argList0.elementAt(2))).getItsValue().
                compareTo("a_nominal") != 0 ) ||
             ( ! ( argList0.elementAt(3) instanceof PredDataValue ) ) ||
             ( ! ( argList0.elementAt(4) instanceof QuoteStringDataValue ) ) ||
             ( ! ( argList0.elementAt(5) instanceof TimeStampDataValue ) ) ||
             ( ! ( argList0.elementAt(6) instanceof UndefinedDataValue ) ) ||
             ( pred0 == null ) ||
             ( argList0a == null ) ||
             ( argList0a.size() != 7 ) ||
             ( ! ( argList0a.elementAt(0) instanceof FloatDataValue ) ) ||
             ( ((FloatDataValue)(argList0a.elementAt(0))).getItsValue() !=
                1.0 ) ||
             ( ! ( argList0a.elementAt(1) instanceof IntDataValue ) ) ||
             ( ((IntDataValue)(argList0a.elementAt(1))).getItsValue() != 2 ) ||
             ( ! ( argList0a.elementAt(2) instanceof NominalDataValue ) ) ||
             ( ((NominalDataValue)(argList0a.elementAt(2))).getItsValue().
                compareTo("a_nominal") != 0 ) ||
             ( ! ( argList0a.elementAt(3) instanceof PredDataValue ) ) ||
             ( ! ( argList0a.elementAt(4) instanceof QuoteStringDataValue ) ) ||
             ( ! ( argList0a.elementAt(5) instanceof TimeStampDataValue ) ) ||
             ( ! ( argList0a.elementAt(6) instanceof UndefinedDataValue ) ) ||
             ( pred0a == null ) ||
             ( pve1 == null ) ||
             ( predID1 == DBIndex.INVALID_ID ) ||
             ( argList1 == null ) ||
             ( argList1.size() != 1 ) ||
             ( ! ( argList1.elementAt(0) instanceof UndefinedDataValue ) ) ||
             ( pred1 == null ) ||
             ( argList1a == null ) ||
             ( argList1a.size() != 1 ) ||
             ( ! ( argList1a.elementAt(0) instanceof UndefinedDataValue ) ) ||
             ( pred1a == null ) ||
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

                if ( predID0 == DBIndex.INVALID_ID )
                {
                    outStream.print("insertion of pve0 failed.\n");
                }

                if ( argList0 == null )
                {
                    outStream.print("creation of argList0 failed.\n");
                }
                else if ( ( argList0.size() != 7 ) ||
                          ( ! ( argList0.elementAt(0) instanceof
                                FloatDataValue ) ) ||
                          ( ((FloatDataValue)
                              (argList0.elementAt(0))).getItsValue() != 1.0 ) ||
                          ( ! ( argList0.elementAt(1) instanceof
                                IntDataValue ) ) ||
                          ( ((IntDataValue)
                             (argList0.elementAt(1))).getItsValue() != 2 ) ||
                          ( ! ( argList0.elementAt(2) instanceof
                                NominalDataValue ) ) ||
                          ( ((NominalDataValue)
                             (argList0.elementAt(2))).getItsValue().
                              compareTo("a_nominal") != 0 ) ||
                          ( ! ( argList0.elementAt(3) instanceof
                                PredDataValue ) ) ||
                          ( ! ( argList0.elementAt(4) instanceof
                                QuoteStringDataValue ) ) ||
                          ( ! ( argList0.elementAt(5) instanceof
                                TimeStampDataValue ) ) ||
                          ( ! ( argList0.elementAt(6) instanceof
                                UndefinedDataValue ) ) )
                {
                    outStream.print("unexpected argList0 structure.\n");
                }

                if ( pred0 == null )
                {
                    outStream.print("new Predicate(db, predID0, argList0) " +
                                    "returned null.\n");
                }

                if ( argList0a == null )
                {
                    outStream.print("creation of argList0a failed.\n");
                }
                else if ( ( argList0a.size() != 7 ) ||
                          ( ! ( argList0a.elementAt(0) instanceof
                                FloatDataValue ) ) ||
                          ( ((FloatDataValue)
                              (argList0a.elementAt(0))).getItsValue() != 1.0 ) ||
                          ( ! ( argList0a.elementAt(1) instanceof
                                IntDataValue ) ) ||
                          ( ((IntDataValue)
                             (argList0a.elementAt(1))).getItsValue() != 2 ) ||
                          ( ! ( argList0a.elementAt(2) instanceof
                                NominalDataValue ) ) ||
                          ( ((NominalDataValue)
                             (argList0a.elementAt(2))).getItsValue().
                              compareTo("a_nominal") != 0 ) ||
                          ( ! ( argList0a.elementAt(3) instanceof
                                PredDataValue ) ) ||
                          ( ! ( argList0a.elementAt(4) instanceof
                                QuoteStringDataValue ) ) ||
                          ( ! ( argList0a.elementAt(5) instanceof
                                TimeStampDataValue ) ) ||
                          ( ! ( argList0a.elementAt(6) instanceof
                                UndefinedDataValue ) ) )
                {
                    outStream.print("unexpected argList0a structure.\n");
                }

                if ( pred0a == null )
                {
                    outStream.print("new Predicate(db, predID0, argList0a) " +
                                    "returned null.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( predID1 == DBIndex.INVALID_ID )
                {
                    outStream.print("insertion of pve1 failed.\n");
                }

                if ( argList1 == null )
                {
                    outStream.print("creation of argList1 failed.\n");
                }
                else if ( ( argList1.size() != 1 ) ||
                          ( ! ( argList1.elementAt(0) instanceof
                               UndefinedDataValue ) ) )
                {
                    outStream.print("unexpected argList1 structure.\n");
                }

                if ( pred1 == null )
                {
                    outStream.print("new Predicate(db, predID1, argList1) " +
                                    "returned null.\n");
                }

                if ( argList1a == null )
                {
                    outStream.print("creation of argList1a failed.\n");
                }
                else if ( ( argList1a.size() != 1 ) ||
                          ( ! ( argList1a.elementAt(0) instanceof
                               UndefinedDataValue ) ) )
                {
                    outStream.print("unexpected argList1a structure.\n");
                }

                if ( pred1a == null )
                {
                    outStream.print("new Predicate(db, predID1, argList1a) " +
                                    "returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw a SystemErrorException:"+
                                     "\"%s\".\n", SystemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0.db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getPveID() != predID0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predID0: %ld.\n",
                            pred0.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getPredName().compareTo("test0") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predName: \"%s\".\n",
                            pred0.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0.argList == null ) || ( pred0.argList == argList0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred0.argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {

            if ( ( pred0.argList.size() != 7 ) ||
                 ( ! ( pred0.argList.elementAt(0) instanceof FloatDataValue ) ) ||
                 ( ! ( pred0.argList.elementAt(1) instanceof IntDataValue ) ) ||
                 ( ! ( pred0.argList.elementAt(2) instanceof NominalDataValue ) ) ||
                 ( ! ( pred0.argList.elementAt(3) instanceof PredDataValue ) ) ||
                 ( ! ( pred0.argList.elementAt(4) instanceof QuoteStringDataValue ) ) ||
                 ( ! ( pred0.argList.elementAt(5) instanceof TimeStampDataValue ) ) ||
                 ( ! ( pred0.argList.elementAt(6) instanceof UndefinedDataValue ) ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial pred0 argList structure.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getVarLen() != pve0.getVarLen() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.argListToString().compareTo(testArgString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0 arg list string: " +
                                     "\"%s\".\n", pred0.argListToString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.argListToDBString().compareTo(testArgDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred0 db arg list string: \"%s\".\n",
                            pred0.argListToDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0a.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0a.db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0a.getPveID() != predID0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred0a.predID0: %ld.\n",
                            pred0a.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0a.getPredName().compareTo("test0") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of " +
                            "pred0a.predName: \"%s\".\n",
                            pred0a.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0a.argList == null ) || ( pred0a.argList == argList0a ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred0a.argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {

            if ( ( pred0a.argList.size() != 7 ) ||
                 ( ! ( pred0a.argList.elementAt(0) instanceof FloatDataValue ) ) ||
                 ( ! ( pred0a.argList.elementAt(1) instanceof IntDataValue ) ) ||
                 ( ! ( pred0a.argList.elementAt(2) instanceof NominalDataValue ) ) ||
                 ( ! ( pred0a.argList.elementAt(3) instanceof PredDataValue ) ) ||
                 ( ! ( pred0a.argList.elementAt(4) instanceof QuoteStringDataValue ) ) ||
                 ( ! ( pred0a.argList.elementAt(5) instanceof TimeStampDataValue ) ) ||
                 ( ! ( pred0a.argList.elementAt(6) instanceof UndefinedDataValue ) ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial pred0a argList structure.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0a.getVarLen() != pve0.getVarLen() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred0a.varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0a.argListToString().compareTo(testArgString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0a arg list string: " +
                                     "\"%s\".\n", pred0a.argListToString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0a.argListToDBString().compareTo(testArgDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred0a db arg list string: \"%s\".\n",
                            pred0a.argListToDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred1.db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getPveID() != predID1 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predID1: %ld.\n",
                            pred1.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getPredName().compareTo("test1") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "Unexpected initial value of pred1.predName: \"%s\".\n",
                        pred1.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred1.argList == null ) || ( pred1.argList == argList1 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1.argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getVarLen() != pve1.getVarLen() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1.varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.argListToString().compareTo(testArgString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1.arg list string: \"%s\".\n",
                            pred1.argListToString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.argListToDBString().compareTo(testArgDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1 db arg list string: \"%s\".\n",
                            pred1.argListToDBString());
                }
            }
        }


        if ( failures == 0 )
        {
            if ( pred1a.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred1a.db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.getPveID() != predID1 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1a.predID1: %ld.\n",
                            pred1a.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.getPredName().compareTo("test1") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "Unexpected initial value of pred1a.predName: \"%s\".\n",
                        pred1a.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred1a.argList == null ) || ( pred1a.argList == argList1a ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1a.argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.getVarLen() != pve1.getVarLen() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1a.varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.argListToString().compareTo(testArgString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1a.arg list string: \"%s\".\n",
                            pred1a.argListToString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.argListToDBString().compareTo(testArgDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1a db arg list string: \"%s\".\n",
                            pred1a.argListToDBString());
                }
            }
        }


        /* Verify that the constructor fails when passed an invalid arg list.
         * Start with a null list.
         */
        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            pred0 = new Predicate(null, predID0, null);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "\"new Predicate(db, predID0, null) "
                            + "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "\"new Predicate(null, predID0, null) " +
                            "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now construct a list that is too long. */

        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            argList = new Vector<DataValue>();

            fargID = pve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            argList.add(arg);
            fargID = pve0.getFormalArg(1).getID();
            arg = new IntDataValue(db, fargID, 22);
            argList.add(arg);
            fargID = pve0.getFormalArg(2).getID();
            arg = new NominalDataValue(db, fargID, "another_nominal");
            argList.add(arg);
            fargID = pve0.getFormalArg(3).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, predID1));
            argList.add(arg);
            fargID = pve0.getFormalArg(4).getID();
            arg = new QuoteStringDataValue(db, fargID, "q-string-2");
            argList.add(arg);
            fargID = pve0.getFormalArg(5).getID();
            arg = new TimeStampDataValue(db, fargID,
                                         new TimeStamp(db.getTicks()));
            argList.add(arg);
            fargID = pve0.getFormalArg(6).getID();
            arg = new UndefinedDataValue(db, fargID,
                                         pve0.getFormalArg(6).getFargName());
            argList.add(arg);
            /* now add an extranious argument */
            arg = new IntDataValue(db, fargID, 33);
            argList.add(arg);


            pred0 = new Predicate(null, predID0, argList);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "new Predicate(db, predID0, too_long_arg_list) " +
                            "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "new Predicate(null, predID0, too_long_arg_list) " +
                            "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now construct a list that is too short. */

        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            argList = new Vector<DataValue>();

            fargID = pve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            argList.add(arg);
            fargID = pve0.getFormalArg(1).getID();
            arg = new IntDataValue(db, fargID, 22);
            argList.add(arg);
            fargID = pve0.getFormalArg(2).getID();
            arg = new NominalDataValue(db, fargID, "another_nominal");
            argList.add(arg);
            fargID = pve0.getFormalArg(3).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, predID1));
            argList.add(arg);
            fargID = pve0.getFormalArg(4).getID();
            arg = new QuoteStringDataValue(db, fargID, "q-string-2");
            argList.add(arg);
            fargID = pve0.getFormalArg(5).getID();
            arg = new TimeStampDataValue(db, fargID,
                                         new TimeStamp(db.getTicks()));
            argList.add(arg);
            fargID = pve0.getFormalArg(6).getID();
            arg = new UndefinedDataValue(db, fargID,
                                         pve0.getFormalArg(6).getFargName());
            /* don't add the last argument */


            pred0 = new Predicate(null, predID0, argList);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "new Predicate(db, predID0, too_short_arg_list) "
                            + "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                        "new Predicate(null, predID0, too_short_arg_list) " +
                        "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now construct a list that contains a type mismatch.  Many type
         * mismatches are possible -- we will just jeck one for now.
         */
        /* TODO: add tests for all possible type mis-matches */

        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            argList = new Vector<DataValue>();

            fargID = pve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            argList.add(arg);
            fargID = pve0.getFormalArg(1).getID();
            arg = new IntDataValue(db, fargID, 22);
            argList.add(arg);
            fargID = pve0.getFormalArg(2).getID();
            arg = new NominalDataValue(db, fargID, "another_nominal");
            argList.add(arg);
            fargID = pve0.getFormalArg(3).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, predID1));
            argList.add(arg);
            fargID = pve0.getFormalArg(4).getID();
            arg = new QuoteStringDataValue(db, fargID, "q-string-2");
            argList.add(arg);

            /* swap arguments for entries 5 & 6 */
            fargID = pve0.getFormalArg(6).getID();
            arg = new UndefinedDataValue(db, fargID,
                                         pve0.getFormalArg(6).getFargName());
            argList.add(arg);
            fargID = pve0.getFormalArg(5).getID();
            arg = new TimeStampDataValue(db, fargID,
                                         new TimeStamp(db.getTicks()));
            argList.add(arg);


            pred0 = new Predicate(null, predID0, argList);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "new Predicate(db, predID0, mis_match_arg_list) " +
                            "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                        "new Predicate(null, predID0, mis_match_arg_list) " +
                        "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now verify that the constructor fails when passed an invalid
         * pve id.
         */
        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            pred0 = new Predicate(new ODBCDatabase(), predID1, argList1);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "\"new Predicate(db, bad_pred_id, argList1) "
                            + "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "\"new Predicate(db, bad_pred_id, argList1) "
                            + "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* finally, verify that the constructor fails when passed an invalid
         * database.
         */
        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            pred0 = new Predicate(null, predID1, argList1);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "\"new Predicate(null, bad_pred_id, argList1) "
                            + "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "\"new Predicate(null, bad_pred_id, argList1) "
                            + "didn't throw a SystemErrorException.\n");
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

    } /* Predicate::Test3ArgConstructor() */


    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors for this class.
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
            "Testing class Predicate accessors                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        Database db = null;
        PredicateVocabElement pve  = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        long fargID = DBIndex.INVALID_ID;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        FormalArgument farg = null;
        Vector<DataValue> argList0 = null;
        Vector<DataValue> argList1 = null;
        Vector<DataValue> argList2 = null;
        Vector<DataValue> argList3 = null;
        DataValue arg = null;
        Predicate pred0 = null;
        Predicate pred1 = null;
        Predicate pred2 = null;
        Predicate pred3 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database and pve's
        threwSystemErrorException = false;
        completed = false;

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
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);


            pve2 = new PredicateVocabElement(db, "pve2");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve2.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve2.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            pve2.appendFormalArg(farg);

            pve2ID = db.addPredVE(pve2);

            // get a copy of the databases version of pve1 with ids assigned
            pve2 = db.getPredVE(pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve3.appendFormalArg(farg);
            pve3.setVarLen(true);

            pve3ID = db.addPredVE(pve3);

            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
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
             ( ! completed ) )
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

                if ( pve0ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0ID not initialized.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve2ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve2ID not initialized.\n");
                }

                if ( pve3 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve3ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve3ID not initialized.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            SystemErrorExceptionString);
                }
            }
        }

        // Verify that getDB() works as expected.  There is not much to
        // do here, as the db field is set on creation and never changed.
        // Thus this test is a repeat tests done in the constructor tests.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                pred0 = new Predicate(db);

                pred1 = new Predicate(db, pve1ID);

                argList3 = new Vector<DataValue>();

                fargID = pve3.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                argList3.add(arg);

                pred3 = new Predicate(db, pve3ID, argList3);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( pred0 == null ) ||
                 ( pred1 == null ) ||
                 ( pred3 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred0 == null )
                    {
                        outStream.print("new Predicate(db) returned null.\n");
                    }

                    if ( pred1 == null )
                    {
                        outStream.print(
                                "new Predicate(db, pve1ID) returned null.\n");
                    }

                    if ( pred3 == null )
                    {
                        outStream.print(
                          "new Predicate(db, pve3ID, argList) returned null.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "setup for getDB() test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("setup for getDB() test threw a " +
                                "SystemErrorException: \"%s\".\n",
                                SystemErrorExceptionString);
                    }
                }

                if ( ( pred0.getDB() != db ) ||
                     ( pred1.getDB() != db ) ||
                     ( pred3.getDB() != db ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "getDB() returned an unexpected value.\n");
                    }
                }
            }
        }

        // Verify that getPveID() / setPredID() work as advertized.  Also test
        // getPredName(), getNumArgs(), and getVarLen in passing.
        //
        // Note that when we set the pveID, we must also re-work the argument
        // list to conform to the new predicate.  We will test this slightly
        // below, but the real test of this feature will be in the argument
        // list management test.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                pred0 = new Predicate(db);

                pred1 = new Predicate(db, pve1ID);

                pred2 = new Predicate(db, pve2ID);

                argList3 = new Vector<DataValue>();

                fargID = pve3.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                argList3.add(arg);

                pred3 = new Predicate(db, pve3ID, argList3);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( pred0 == null ) ||
                 ( pred1 == null ) ||
                 ( pred2 == null ) ||
                 ( pred3 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred0 == null )
                    {
                        outStream.print("new Predicate(db) returned null.\n");
                    }

                    if ( pred1 == null )
                    {
                        outStream.print(
                                "new Predicate(db, pve1ID) returned null.\n");
                    }

                    if ( pred2 == null )
                    {
                        outStream.print(
                                "new Predicate(db, pve2ID) returned null.\n");
                    }

                    if ( pred3 == null )
                    {
                        outStream.print(
                          "new Predicate(db, pve3ID, argList) returned null.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("setup for get/set pred ID test " +
                                        "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("setup for getDB() test threw a " +
                                "SystemErrorException: \"%s\".\n",
                                SystemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0.getPveID() != DBIndex.INVALID_ID ) ||
                 ( pred1.getPveID() != pve1ID ) ||
                 ( pred2.getPveID() != pve2ID ) ||
                 ( pred3.getPveID() != pve3ID ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getPredID() returned an unexpected " +
                        "value 1: %d(%d), %d(%d), %d(%d), %d(%d).\n",
                        pred0.getPveID(), DBIndex.INVALID_ID,
                        pred1.getPveID(), pve1ID,
                        pred2.getPveID(), pve2ID,
                        pred3.getPveID(), pve3ID);
                }
            } else if ( ( pred0.getNumArgs() != 0 ) ||
                        ( pred1.getNumArgs() != 2 ) ||
                        ( pred2.getNumArgs() != 3 ) ||
                        ( pred3.getNumArgs() != 1 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getNumArgs() returned an unexpected " +
                                     "values in get/set pred ID test 1.\n");
                }
            } else if ( ( pred0.getVarLen() != false ) ||
                        ( pred1.getVarLen() != false ) ||
                        ( pred2.getVarLen() != false ) ||
                        ( pred3.getVarLen() != true ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getVarLen() returned an unexpected " +
                                     "value in get/set pred ID test 1.\n");
                }
            } else if ( ( pred0.getPredName().compareTo("") != 0 ) ||
                        ( pred1.getPredName().compareTo("pve1") != 0 ) ||
                        ( pred2.getPredName().compareTo("pve2") != 0 ) ||
                        ( pred3.getPredName().compareTo("pve3") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getPredName() returned an unexpected " +
                                     "value in get/set pred ID test 1.\n");
                }
            }
        }

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                pred0.setPredID(pve1ID, false);
                pred1.setPredID(pve2ID, false);
                pred2.setPredID(pve3ID, false);
                pred3.setPredID(DBIndex.INVALID_ID, false);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "setPredID() test failed to complete 1.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("setup for getDB() test threw a " +
                                "SystemErrorException: \"%s\".\n",
                                SystemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0.getPveID() != pve1ID ) ||
                 ( pred1.getPveID() != pve2ID ) ||
                 ( pred2.getPveID() != pve3ID ) ||
                 ( pred3.getPveID() != DBIndex.INVALID_ID ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getPredID() returned an unexpected " +
                        "value 2: %d(%d), %d(%d), %d(%d), %d(%d).\n",
                        pred0.getPveID(), pve1ID,
                        pred1.getPveID(), pve2ID,
                        pred2.getPveID(), pve3ID,
                        pred3.getPveID(), DBIndex.INVALID_ID);
                }
            } else if ( ( pred0.getNumArgs() != 2 ) ||
                        ( pred1.getNumArgs() != 3 ) ||
                        ( pred2.getNumArgs() != 1 ) ||
                        ( pred3.getNumArgs() != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getNumArgs() returned an unexpected " +
                                     "values in get/set pred ID test 2.\n");
                }
            } else if ( ( pred0.getVarLen() != false ) ||
                        ( pred1.getVarLen() != false ) ||
                        ( pred2.getVarLen() != true ) ||
                        ( pred3.getVarLen() != false ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getVarLen() returned an unexpected " +
                                     "value in get/set pred ID test 2.\n");
                }
            } else if ( ( pred0.getPredName().compareTo("pve1") != 0 ) ||
                        ( pred1.getPredName().compareTo("pve2") != 0 ) ||
                        ( pred2.getPredName().compareTo("pve3") != 0 ) ||
                        ( pred3.getPredName().compareTo("") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getPredName() returned an unexpected " +
                                     "value in get/set pred ID test 2.\n");
                }
            }
        }

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                pred0.setPredID(pve2ID, true);
                pred1.setPredID(pve3ID, true);
                pred2.setPredID(DBIndex.INVALID_ID, true);
                pred3.setPredID(pve1ID, true);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "setPredID() test failed to complete 2.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("setup for getDB() test threw a " +
                                "SystemErrorException: \"%s\".\n",
                                SystemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0.getPveID() != pve2ID ) ||
                 ( pred1.getPveID() != pve3ID ) ||
                 ( pred2.getPveID() != DBIndex.INVALID_ID ) ||
                 ( pred3.getPveID() != pve1ID ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getPredID() returned an unexpected " +
                        "value 3: %d(%d), %d(%d), %d(%d), %d(%d).\n",
                        pred0.getPveID(), pve2ID,
                        pred1.getPveID(), pve3ID,
                        pred2.getPveID(), DBIndex.INVALID_ID,
                        pred3.getPveID(), pve2ID);
                }
            } else if ( ( pred0.getNumArgs() != 3 ) ||
                        ( pred1.getNumArgs() != 1 ) ||
                        ( pred2.getNumArgs() != DBIndex.INVALID_ID ) ||
                        ( pred3.getNumArgs() != 2 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getNumArgs() returned an unexpected " +
                                     "value in get/set pred ID test 3.\n");
                }
            } else if ( ( pred0.getVarLen() != false ) ||
                        ( pred1.getVarLen() != true ) ||
                        ( pred2.getVarLen() != false ) ||
                        ( pred3.getVarLen() != false ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getVarLen() returned an unexpected " +
                                     "value in get/set pred ID test 3.\n");
                }
            } else if ( ( pred0.getPredName().compareTo("pve2") != 0 ) ||
                        ( pred1.getPredName().compareTo("pve3") != 0 ) ||
                        ( pred2.getPredName().compareTo("") != 0 ) ||
                        ( pred3.getPredName().compareTo("pve1") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getPredName() returned an unexpected " +
                                     "value in get/set pred ID test 3.\n");
                }
            }
        }

        // verify that setPredID() fails on invalid input.
        // start with an unused id
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                pred0.setPredID(100, true);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( pred0.getPveID() != pve2ID ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred0.getPveID() != pve2ID )
                    {
                        outStream.printf("pred0.getPredID() != pve2ID (1)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "pred0.setPredID(100, true) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("pred0.setPredID(100, true) failed " +
                                         "to thow a system error.\n");
                    }
                }
            }
        }


        // now use the id of a formal argument -- should fail as well
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                fargID = pve0.getFormalArg(0).getID();

                pred0.setPredID(fargID, true);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( fargID == DBIndex.INVALID_ID ) ||
                 ( pred0.getPveID() != pve2ID ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( fargID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("fargID == DBIndex.INVALID_ID (1).\n");
                    }

                    if ( pred0.getPveID() != pve2ID )
                    {
                        outStream.printf("pred0.getPredID() != pve2ID (2)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "pred0.setPredID(fargID, true) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("pred0.setPredID(fargID, true) " +
                                         "failed to thow a system error.\n");
                    }
                }
            }
        }

        // finally, verify that lookupPredicateVE() throws a system error on
        // invalid input.  Start with the valid id that does not refer to a
        // predicate vocab element

        threwSystemErrorException = false;
        completed = false;
        fargID = DBIndex.INVALID_ID;
        pve = null;

        if ( failures == 0 )
        {
            try
            {
                fargID = pve0.getFormalArg(0).getID();

                pve = pred0.lookupPredicateVE(fargID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( fargID == DBIndex.INVALID_ID ) ||
                 ( pve != null ) ||
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

                    if ( pve != null )
                    {
                        outStream.printf("pve != null (1)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "pred0.lookupPredicateVE(fargID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("pred0.lookupPredicateVE(fargID) " +
                                         "failed to thow a system error.\n");
                    }
                }
            }
        }

        // now try an unused ID
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                pve = pred0.lookupPredicateVE(100);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( pve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pve != null )
                    {
                        outStream.printf("pve != null (2)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "pred0.lookupPredicateVE(100) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("pred0.lookupPredicateVE(100) " +
                                         "failed to thow a system error.\n");
                    }
                }
            }
        }

        // finally, try the invalid ID
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                pve = pred0.lookupPredicateVE(DBIndex.INVALID_ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( pve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pve != null )
                    {
                        outStream.printf("pve != null (3)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("pred0.lookupPredicateVE" +
                                         "(DBIndex.INVALID_ID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("pred0.lookupPredicateVE" +
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

    } /* Predicate::TestAccessors() */

    /**
     * TestArgListManagement()
     *
     * Run a battery of tests on the arg list management facilities.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestArgListManagement(java.io.PrintStream outStream,
                                                boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing class Predicate argument list management                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        Database db = null;
        PredicateVocabElement pve   = null;
        PredicateVocabElement pve0  = null;
        PredicateVocabElement pve1  = null;
        PredicateVocabElement pve2  = null;
        PredicateVocabElement pve3  = null;
        PredicateVocabElement pve4  = null;
        PredicateVocabElement pve5  = null;
        PredicateVocabElement pve6  = null;
        PredicateVocabElement pve7  = null;
        PredicateVocabElement pve8  = null;
        PredicateVocabElement pve9  = null;
        PredicateVocabElement pve10 = null;
        PredicateVocabElement pve11 = null;
        PredicateVocabElement pve12 = null;
        long fargID  = DBIndex.INVALID_ID;
        long pve0ID  = DBIndex.INVALID_ID;
        long pve1ID  = DBIndex.INVALID_ID;
        long pve2ID  = DBIndex.INVALID_ID;
        long pve3ID  = DBIndex.INVALID_ID;
        long pve4ID  = DBIndex.INVALID_ID;
        long pve5ID  = DBIndex.INVALID_ID;
        long pve6ID  = DBIndex.INVALID_ID;
        long pve7ID  = DBIndex.INVALID_ID;
        long pve8ID  = DBIndex.INVALID_ID;
        long pve9ID  = DBIndex.INVALID_ID;
        long pve10ID = DBIndex.INVALID_ID;
        long pve11ID = DBIndex.INVALID_ID;
        long pve12ID = DBIndex.INVALID_ID;
        FormalArgument farg = null;
        Vector<DataValue> argList0 = null;
        Vector<DataValue> argList1 = null;
        Vector<DataValue> argList2 = null;
        Vector<DataValue> argList3 = null;
        Vector<DataValue> argList4 = null;
        Vector<DataValue> argList5 = null;
        Vector<DataValue> argList6 = null;
        Vector<DataValue> argList7 = null;
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
        TextStringDataValue textArg0 = null;
        TimeStampDataValue tsArg0    = null;
        TimeStampDataValue tsArg1    = null;
        UndefinedDataValue undefArg0 = null;
        UndefinedDataValue undefArg1 = null;
        Predicate pred0  = null;
        Predicate pred1  = null;
        Predicate pred2  = null;
        Predicate pred3  = null;
        Predicate pred4  = null;
        Predicate pred5  = null;
        Predicate pred6  = null;
        Predicate pred7  = null;
        Predicate pred8  = null;
        Predicate pred9  = null;
        Predicate pred10 = null;
        Predicate pred11 = null;
        Predicate pred12 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database and pve's
        threwSystemErrorException = false;
        completed = false;

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
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);


            pve2 = new PredicateVocabElement(db, "pve2");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve2.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve2.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            pve2.appendFormalArg(farg);

            pve2ID = db.addPredVE(pve2);

            // get a copy of the databases version of pve1 with ids assigned
            pve2 = db.getPredVE(pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve3.appendFormalArg(farg);
            pve3.setVarLen(true);

            pve3ID = db.addPredVE(pve3);

            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);


            pve4 = new PredicateVocabElement(db, "pve4");

            farg = new FloatFormalArg(db, "<float>");
            pve4.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve4.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve4.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve4.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve4.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve4.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve4.appendFormalArg(farg);

            pve4ID = db.addPredVE(pve4);

            // get a copy of the databases version of pve4 with ids assigned
            pve4 = db.getPredVE(pve4ID);


            pve5 = new PredicateVocabElement(db, "pve5");

            farg = new UnTypedFormalArg(db, "<untyped>");
            pve5.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            pve5.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve5.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve5.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve5.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve5.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve5.appendFormalArg(farg);

            pve5ID = db.addPredVE(pve5);

            // get a copy of the databases version of pve5 with ids assigned
            pve5 = db.getPredVE(pve5ID);


            pve6 = new PredicateVocabElement(db, "pve6");

            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve6.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve6.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            pve6.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve6.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve6.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve6.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve6.appendFormalArg(farg);

            pve6ID = db.addPredVE(pve6);

            // get a copy of the databases version of pve6 with ids assigned
            pve6 = db.getPredVE(pve6ID);


            pve7 = new PredicateVocabElement(db, "pve7");

            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve7.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve7.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve7.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            pve7.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve7.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve7.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve7.appendFormalArg(farg);

            pve7ID = db.addPredVE(pve7);

            // get a copy of the databases version of pve7 with ids assigned
            pve7 = db.getPredVE(pve7ID);


            pve8 = new PredicateVocabElement(db, "pve8");

            farg = new PredFormalArg(db, "<pred>");
            pve8.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve8.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve8.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve8.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            pve8.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve8.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve8.appendFormalArg(farg);

            pve8ID = db.addPredVE(pve8);

            // get a copy of the databases version of pve8 with ids assigned
            pve8 = db.getPredVE(pve8ID);


            pve9 = new PredicateVocabElement(db, "pve9");

            farg = new NominalFormalArg(db, "<nominal>");
            pve9.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve9.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve9.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve9.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve9.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            pve9.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve9.appendFormalArg(farg);

            pve9ID = db.addPredVE(pve9);

            // get a copy of the databases version of pve9 with ids assigned
            pve9 = db.getPredVE(pve9ID);


            pve10 = new PredicateVocabElement(db, "pve10");

            farg = new IntFormalArg(db, "<int>");
            pve10.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve10.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve10.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve10.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve10.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve10.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            pve10.appendFormalArg(farg);

            pve10ID = db.addPredVE(pve10);

            // get a copy of the databases version of pve10 with ids assigned
            pve10 = db.getPredVE(pve10ID);


            pve11 = new PredicateVocabElement(db, "pve11");

            farg = new FloatFormalArg(db, "<float>");
            pve11.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve11.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve11.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve11.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve11.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve11.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve11.appendFormalArg(farg);

            pve11ID = db.addPredVE(pve11);

            // get a copy of the databases version of pve11 with ids assigned
            pve11 = db.getPredVE(pve11ID);


            pve12 = new PredicateVocabElement(db, "pve12");

            farg = new UnTypedFormalArg(db, "<arg1>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg4>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg5>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg6>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg7>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg8>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg9>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg10>");
            pve12.appendFormalArg(farg);

            pve12ID = db.addPredVE(pve12);

            // get a copy of the databases version of pve12 with ids assigned
            pve12 = db.getPredVE(pve12ID);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
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
             ( pve4 == null ) ||
             ( pve4ID == DBIndex.INVALID_ID ) ||
             ( pve5 == null ) ||
             ( pve5ID == DBIndex.INVALID_ID ) ||
             ( pve6 == null ) ||
             ( pve6ID == DBIndex.INVALID_ID ) ||
             ( pve7 == null ) ||
             ( pve7ID == DBIndex.INVALID_ID ) ||
             ( pve8 == null ) ||
             ( pve8ID == DBIndex.INVALID_ID ) ||
             ( pve9 == null ) ||
             ( pve9ID == DBIndex.INVALID_ID ) ||
             ( pve10 == null ) ||
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
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }

                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0ID not initialized.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve2ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve2ID not initialized.\n");
                }

                if ( pve3 == null )
                {
                    outStream.print("creation of pve3 failed.\n");
                }

                if ( pve3ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve3ID not initialized.\n");
                }

                if ( pve4 == null )
                {
                    outStream.print("creation of pve3 failed.\n");
                }

                if ( pve4ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pv4ID not initialized.\n");
                }

                if ( pve5 == null )
                {
                    outStream.print("creation of pve5 failed.\n");
                }

                if ( pve5ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve5ID not initialized.\n");
                }

                if ( pve6 == null )
                {
                    outStream.print("creation of pve6 failed.\n");
                }

                if ( pve6ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve6ID not initialized.\n");
                }

                if ( pve7 == null )
                {
                    outStream.print("creation of pve7 failed.\n");
                }

                if ( pve7ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve7ID not initialized.\n");
                }

                if ( pve8 == null )
                {
                    outStream.print("creation of pve8 failed.\n");
                }

                if ( pve8ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve8ID not initialized.\n");
                }

                if ( pve9 == null )
                {
                    outStream.print("creation of pve9 failed.\n");
                }

                if ( pve9ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve9ID not initialized.\n");
                }

                if ( pve10 == null )
                {
                    outStream.print("creation of pve10 failed.\n");
                }

                if ( pve10ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve10ID not initialized.\n");
                }

                if ( pve11 == null )
                {
                    outStream.print("creation of pve11 failed.\n");
                }

                if ( pve11ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve11ID not initialized.\n");
                }

                if ( pve12 == null )
                {
                    outStream.print("creation of pve12 failed.\n");
                }

                if ( pve12ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve12ID not initialized.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            SystemErrorExceptionString);
                }
            }
        }

        // Start with a set of tests to verify that an argument list is
        // converted properly when the pveID of an instance of Predicate
        // is changed.
        //
        // Start by creating the necessary set of test instances of Predicate.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            String testString0 = "pve4(1.0, 2, a_nominal, " +
                                      "pve0(<arg1>, <arg2>), " +
                                      "\"q-string\", 00:00:00:000, <untyped>)";
            String testDBString0 =
                "(predicate (id 0) " +
                           "(predID 13) " +
                           "(predName pve4) " +
                           "(varLen false) " +
                           "(argList ((FloatDataValue (id 0) " +
                                                     "(itsFargID 14) " +
                                                     "(itsFargType FLOAT) " +
                                                     "(itsCellID 0) " +
                                                     "(itsValue 1.0) " +
                                                     "(subRange false) " +
                                                     "(minVal 0.0) " +
                                                     "(maxVal 0.0)), " +
                                     "(IntDataValue (id 0) " +
                                                   "(itsFargID 15) " +
                                                   "(itsFargType INTEGER) " +
                                                   "(itsCellID 0) " +
                                                   "(itsValue 2) " +
                                                   "(subRange false) " +
                                                   "(minVal 0) " +
                                                   "(maxVal 0)), " +
                                     "(NominalDataValue (id 0) " +
                                                       "(itsFargID 16) " +
                                                       "(itsFargType NOMINAL) " +
                                                       "(itsCellID 0) " +
                                                       "(itsValue a_nominal) " +
                                                       "(subRange false)), " +
                                     "(PredDataValue (id 0) " +
                                       "(itsFargID 17) " +
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
                                             "(subRange false)), " +
                                     "(QuoteStringDataValue (id 0) " +
                                                 "(itsFargID 18) " +
                                                 "(itsFargType QUOTE_STRING) " +
                                                 "(itsCellID 0) " +
                                                 "(itsValue q-string) " +
                                                 "(subRange false)), " +
                                     "(TimeStampDataValue (id 0) " +
                                               "(itsFargID 19) " +
                                               "(itsFargType TIME_STAMP) " +
                                               "(itsCellID 0) " +
                                               "(itsValue (60,00:00:00:000)) " +
                                               "(subRange false)), " +
                                     "(UndefinedDataValue (id 0) " +
                                               "(itsFargID 20) " +
                                               "(itsFargType UNTYPED) " +
                                               "(itsCellID 0) " +
                                               "(itsValue <untyped>) " +
                                               "(subRange false))))))";
            String testString1 = "pve3(99)";
            String testDBString1 = "(predicate (id 0) " +
                                       "(predID 11) " +
                                       "(predName pve3) " +
                                       "(varLen true) " +
                                       "(argList ((IntDataValue (id 0) " +
                                                      "(itsFargID 12) " +
                                                      "(itsFargType UNTYPED) " +
                                                      "(itsCellID 0) " +
                                                      "(itsValue 99) " +
                                                      "(subRange false) " +
                                                      "(minVal 0) " +
                                                      "(maxVal 0))))))";
            String testString2 = "pve12(<arg1>, <arg2>, <arg3>, <arg4>, " +
                                       "<arg5>, <arg6>, <arg7>, <arg8>, " +
                                       "<arg9>, <arg10>)";
            String testDBString2 =
                "(predicate (id 0) " +
                            "(predID 77) " +
                            "(predName pve12) " +
                            "(varLen false) " +
                            "(argList ((UndefinedDataValue (id 0) " +
                                         "(itsFargID 78) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg1>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 79) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg2>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 80) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg3>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 81) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg4>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 82) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg5>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 83) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg6>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 84) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg7>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 85) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg8>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 86) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg9>) " +
                                         "(subRange false)), " +
                                       "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 87) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg10>) " +
                                         "(subRange false))))))";

            try
            {
                argList0 = new Vector<DataValue>();

                fargID = pve4.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                argList0.add(arg);
                fargID = pve4.getFormalArg(1).getID();
                arg = new IntDataValue(db, fargID, 2);
                argList0.add(arg);
                fargID = pve4.getFormalArg(2).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                argList0.add(arg);
                fargID = pve4.getFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
                argList0.add(arg);
                fargID = pve4.getFormalArg(4).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                argList0.add(arg);
                fargID = pve4.getFormalArg(5).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks()));
                argList0.add(arg);
                fargID = pve4.getFormalArg(6).getID();
                arg = new UndefinedDataValue(db, fargID,
                                             pve4.getFormalArg(6).getFargName());
                argList0.add(arg);

                pred0  = new Predicate(db, pve4ID, argList0);
                pred1  = new Predicate(db, pve4ID, argList0);
                pred2  = new Predicate(db, pve4ID, argList0);
                pred3  = new Predicate(db, pve4ID, argList0);
                pred4  = new Predicate(db, pve4ID, argList0);
                pred5  = new Predicate(db, pve4ID, argList0);
                pred6  = new Predicate(db, pve4ID, argList0);
                pred7  = new Predicate(db, pve4ID, argList0);
                pred8  = new Predicate(db, pve4ID, argList0);
                pred9  = new Predicate(db, pve4ID, argList0);
                pred12 = new Predicate(db, pve4ID, argList0);

                argList1 = new Vector<DataValue>();

                fargID = pve3.getFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 99);
                argList1.add(arg);

                pred10 = new Predicate(db, pve3ID, argList1);

                pred11 = new Predicate(db, pve12ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( argList0 == null ) ||
                 ( argList0.size() != 7 ) ||
                 ( pred0 == null ) ||
                 ( pred1 == null ) ||
                 ( pred2 == null ) ||
                 ( pred3 == null ) ||
                 ( pred4 == null ) ||
                 ( pred5 == null ) ||
                 ( pred6 == null ) ||
                 ( pred7 == null ) ||
                 ( pred8 == null ) ||
                 ( pred9 == null ) ||
                 ( pred10 == null ) ||
                 ( pred11 == null ) ||
                 ( pred12 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( argList0 == null )
                    {
                        outStream.print("argList0 allocation failed.\n");
                    }
                    else if ( argList0.size() != 7 )
                    {
                        outStream.printf("unexpected argList0.size(): %d (7).\n",
                                         argList0.size());
                    }

                    if ( ( pred0 == null ) ||
                         ( pred1 == null ) ||
                         ( pred2 == null ) ||
                         ( pred3 == null ) ||
                         ( pred4 == null ) ||
                         ( pred5 == null ) ||
                         ( pred6 == null ) ||
                         ( pred7 == null ) ||
                         ( pred8 == null ) ||
                         ( pred9 == null ) ||
                         ( pred10 == null ) ||
                         ( pred11 == null ) ||
                         ( pred12 == null ) )
                    {
                        outStream.print("one or more Predicate allocation(s) " +
                                        "failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("test predicate allocation failed " +
                                        "to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test predicate allocation threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         SystemErrorExceptionString);
                    }
                }
            }
            else if ( ( pred0.toString().compareTo(testString0) != 0 ) ||
                      ( pred1.toString().compareTo(testString0) != 0 ) ||
                      ( pred2.toString().compareTo(testString0) != 0 ) ||
                      ( pred3.toString().compareTo(testString0) != 0 ) ||
                      ( pred4.toString().compareTo(testString0) != 0 ) ||
                      ( pred5.toString().compareTo(testString0) != 0 ) ||
                      ( pred6.toString().compareTo(testString0) != 0 ) ||
                      ( pred7.toString().compareTo(testString0) != 0 ) ||
                      ( pred8.toString().compareTo(testString0) != 0 ) ||
                      ( pred9.toString().compareTo(testString0) != 0 ) ||
                      ( pred12.toString().compareTo(testString0) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred?.toString)(): \"%s\"\n",
                                     pred0.toString());
                }
            }
            else if ( ( pred0.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred1.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred2.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred3.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred4.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred5.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred6.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred7.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred8.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred9.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred12.toDBString().compareTo(testDBString0) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred?.toDBString)(): \"%s\"\n",
                                     pred0.toDBString());
                }
            }
            else if ( pred10.toString().compareTo(testString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred10.toString)(): \"%s\"\n",
                                     pred10.toString());
                }
            }
            else if ( pred10.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred10.toDBString)(): \"%s\"\n",
                                     pred10.toDBString());
                }
            }
            else if ( pred11.toString().compareTo(testString2) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred11.toString)(): \"%s\"\n",
                                     pred11.toString());
                }
            }
            else if ( pred11.toDBString().compareTo(testDBString2) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred11.toDBString)(): \"%s\"\n",
                                     pred11.toDBString());
                }
            }
        }


        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            // untyped, float, int, nominal, pred, q-string, timestamp
            String testString1 =
                    "pve5(1.0, 2.0, 0, , (), \"\", 00:00:00:000)";
            // timestamp, untyped, float, int, nominal, pred, q-string
            String testString2 =
                    "pve6(00:00:00:000, 2, 0.0, 0, q-string, (), \"\")";
            // q-string, timestamp, untyped, float, int, nominal, pred
            String testString3 =
                    "pve7(\"\", 00:00:00:002, a_nominal, 0.0, 0, , ())";
            // pred, q-string, timestamp, untyped, float, int, nominal
            String testString4 =
                    "pve8((), \"\", 00:00:00:000, pve0(<arg1>, <arg2>), 0.0, 0, )";
            // nominal, pred, q-string, timestamp, untyped, float, int
            String testString5 =
                    "pve9(, (), \"a_nominal\", 00:00:00:000, \"q-string\", 0.0, 0)";
            // int, nominal, pred, q-string, timestamp, untyped, float
            String testString6 =
                    "pve10(1, , (), \"\", 00:00:00:000, 00:00:00:000, 0.0)";
            // float, int, nominal, pred, q-string, timestamp, untyped
            String testString7 =
                    "pve11(1.0, 2, a_nominal, pve0(<arg1>, <arg2>), " +
                          "\"q-string\", 00:00:00:000, <untyped>)";
            String testString8 = "pve3(1.0)";
            String testString9 = "pve1(1, 2)";
            String testString10 =
                    "pve4(99.0, 0, , (), \"\", 00:00:00:000, <untyped>)";

            try
            {
                pred1.setPredID(pve5ID, true);
                pred2.setPredID(pve6ID, true);
                pred3.setPredID(pve7ID, true);
                pred4.setPredID(pve8ID, true);
                pred5.setPredID(pve9ID, true);
                pred6.setPredID(pve10ID, true);
                pred7.setPredID(pve11ID, true);

                pred8.setPredID(pve3ID, true);
                pred9.setPredID(pve1ID, true);

                pred10.setPredID(pve4ID, true);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test of setPredID(?, true) " +
                                        "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test of setPredID(?, true) threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         SystemErrorExceptionString);
                    }
                }
            } else if ( ( pred1.toString().compareTo(testString1) != 0 ) ||
                        ( pred2.toString().compareTo(testString2) != 0 ) ||
                        ( pred3.toString().compareTo(testString3) != 0 ) ||
                        ( pred4.toString().compareTo(testString4) != 0 ) ||
                        ( pred5.toString().compareTo(testString5) != 0 ) ||
                        ( pred6.toString().compareTo(testString6) != 0 ) ||
                        ( pred7.toString().compareTo(testString7) != 0 ) ||
                        ( pred8.toString().compareTo(testString8) != 0 ) ||
                        ( pred9.toString().compareTo(testString9) != 0 ) ||
                        ( pred10.toString().compareTo(testString10) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred1.toString().compareTo(testString1) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred1.toString(): \"%s\".\n",
                                pred1.toString());
                    }

                    if ( pred2.toString().compareTo(testString2) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred2.toString(): \"%s\".\n",
                                pred2.toString());
                    }

                    if ( pred3.toString().compareTo(testString3) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred3.toString(): \"%s\".\n",
                                pred3.toString());
                    }

                    if ( pred4.toString().compareTo(testString4) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred4.toString(): \"%s\".\n",
                                pred4.toString());
                    }

                    if ( pred5.toString().compareTo(testString5) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred5.toString(): \"%s\".\n",
                                pred5.toString());
                    }

                    if ( pred6.toString().compareTo(testString6) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred6.toString(): \"%s\".\n",
                                pred6.toString());
                    }

                    if ( pred7.toString().compareTo(testString7) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred7.toString(): \"%s\".\n",
                                pred7.toString());
                    }

                    if ( pred8.toString().compareTo(testString8) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred8.toString(): \"%s\".\n",
                                pred8.toString());
                    }

                    if ( pred9.toString().compareTo(testString9) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred9.toString(): \"%s\".\n",
                                pred9.toString());
                    }

                    if ( pred10.toString().compareTo(testString10) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred10.toString(): \"%s\".\n",
                                pred10.toString());
                    }
                }
            }
        }

        // Verify that the getArg() and replaceArg() methods perform as
        // expected.

        /* first a float argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 0.0);
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
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

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
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
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(pred12,
                                                    floatArg0,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "floatArgo");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      intArg0,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "intArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      nominalArg0,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "nominalArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      predArg0,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      qsArg0,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "qsArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      tsArg0,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }


        /* next an integer argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(1).getID();
                arg = new IntDataValue(db, fargID, 0);
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
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

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
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
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(pred12,
                                                      floatArg0,
                                                      1,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "floatArgo");

                failures += VerifyArgListAssignment(pred12,
                                                    intArg0,
                                                    1,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "intArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      nominalArg0,
                                                      1,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "nominalArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      predArg0,
                                                      1,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      1,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      qsArg0,
                                                      1,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "qsArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      tsArg0,
                                                      1,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    1,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }


        /* next an nominal argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(2).getID();
                arg = new NominalDataValue(db, fargID, "just_some_nominal");
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
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

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
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
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(pred12,
                                                      floatArg0,
                                                      2,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "floatArgo");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      intArg0,
                                                      2,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "intArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    nominalArg0,
                                                    2,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "nominalArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      predArg0,
                                                      2,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      2,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      qsArg0,
                                                      2,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "qsArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      tsArg0,
                                                      2,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    2,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }


        /* next an predicate argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
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

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
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
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(pred12,
                                                      floatArg0,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "floatArgo");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      intArg0,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "intArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      nominalArg0,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "nominalArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    predArg0,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      qsArg0,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "qsArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      tsArg0,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }


        /* next an quote string argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(4).getID();
                arg = new QuoteStringDataValue(db, fargID, "some quote string");
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(qsdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
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
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(pred12,
                                                      floatArg0,
                                                      4,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "floatArgo");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      intArg0,
                                                      4,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "intArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      nominalArg0,
                                                      4,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "nominalArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      predArg0,
                                                      4,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      4,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    qsArg0,
                                                    4,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "qsArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      tsArg0,
                                                      4,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    4,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }


        /* next a time stamp argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(5).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 360));
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
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

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
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
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(pred12,
                                                      floatArg0,
                                                      5,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "floatArgo");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      intArg0,
                                                      5,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "intArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      nominalArg0,
                                                      5,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "nominalArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      predArg0,
                                                      5,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      5,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      qsArg0,
                                                      5,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "qsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    tsArg0,
                                                    5,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    5,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }


        /* finally, an undefined argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(6).getID();
                arg = new UndefinedDataValue(db, fargID,
                                            pve4.getFormalArg(6).getFargName());
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(udv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
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
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(pred12,
                                                    floatArg0,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "floatArgo");

                failures += VerifyArgListAssignment(pred12,
                                                    intArg0,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "intArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    nominalArg0,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "nominalArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    predArg0,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      6,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    qsArg0,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "qsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    tsArg0,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }

        /* In theory, the above battery of tests should cover everything.
         * However, lets throw in a few more random tests on the likely
         * chance that theory has missed a few cases.
         */

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {   // float, int, nominal, pred, q-string, timestamp, untyped

                floatArg0 = (FloatDataValue)pred0.getArg(0);
                floatArg1 =
                        new FloatDataValue(db, floatArg0.getItsFargID(), 10.0);
                pred0.replaceArg(0, floatArg1);

                intArg0 = (IntDataValue)pred0.getArg(1);
                intArg1 = new IntDataValue(db, intArg0.getItsFargID(), 20);
                pred0.replaceArg(1, intArg1);

                nominalArg0 = (NominalDataValue)pred0.getArg(2);
                nominalArg1 = new NominalDataValue(db,
                                                   nominalArg0.getItsFargID(),
                                                   "another_nominal");
                pred0.replaceArg(2, nominalArg1);

                predArg0 = (PredDataValue)pred0.getArg(3);
                predArg1 = new PredDataValue(db, predArg0.getItsFargID(),
                                             pred9);
                pred0.replaceArg(3, predArg1);

                qsArg0 = (QuoteStringDataValue)pred0.getArg(4);
                qsArg1 = new QuoteStringDataValue(db, qsArg0.getItsFargID(),
                                                  "another_qs");
                pred0.replaceArg(4, qsArg1);

                tsArg0 = (TimeStampDataValue)pred0.getArg(5);
                tsArg1 = new TimeStampDataValue(db, tsArg0.getItsFargID(),
                                           new TimeStamp(db.getTicks(), 3600));
                pred0.replaceArg(5, tsArg1);

                undefArg0 = (UndefinedDataValue)pred0.getArg(6);
                intArg2 = new IntDataValue(db, undefArg0.getItsFargID(), 30);
                pred0.replaceArg(6, intArg2);



                fargID = pve12.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 33.0);
                pred11.replaceArg(0, arg);

                fargID = pve12.getFormalArg(1).getID();
                arg = new IntDataValue(db, fargID, 44);
                pred11.replaceArg(1, arg);

                fargID = pve12.getFormalArg(2).getID();
                arg = new NominalDataValue(db, fargID, "what_ever");
                pred11.replaceArg(2, arg);

                fargID = pve12.getFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
                pred11.replaceArg(3, arg);

                fargID = pve12.getFormalArg(4).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string-3");
                pred11.replaceArg(4, arg);

                fargID = pve12.getFormalArg(5).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks()));
                pred11.replaceArg(5, arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {

                    if ( ! completed )
                    {
                        outStream.print(
                                "get/replace arg test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("get/replace arg test threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         SystemErrorExceptionString);
                    }
                }
            }
            else if ( pred0.toString().compareTo("pve4(10.0, 20, " +
                              "another_nominal, pve1(1, 2), \"another_qs\", " +
                              "00:01:00:000, 30)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                        outStream.printf(
                                "Unexpected pred0.toString(): \"%s\".\n",
                                pred0.toString());
                }
            }
            else if ( pred11.toString().compareTo("pve12(33.0, 44, what_ever, " +
                              "pve0(<arg1>, <arg2>), \"q-string-3\", " +
                              "00:00:00:000, <arg7>, <arg8>, <arg9>, " +
                              "<arg10>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                        outStream.printf(
                                "Unexpected pred11.toString() - 1: \"%s\".\n",
                                pred11.toString());
                }
            }
        }

        // finally, verify that getArg() and replaceArg() fail on invalid input.

        // create a farg ID mis-match

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                fargID = pve12.getFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                pred11.replaceArg(6, arg);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;


                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "replaceArg() with fargID mismatch completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("replaceArg() with fargID mismatch " +
                                        "didn't throw a SystemErrorException.\n");
                    }
                }
            }
            else if ( pred11.toString().compareTo("pve12(33.0, 44, what_ever, " +
                              "pve0(<arg1>, <arg2>), \"q-string-3\", " +
                              "00:00:00:000, <arg7>, <arg8>, <arg9>, " +
                              "<arg10>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                        outStream.printf(
                                "Unexpected pred11.toString() - 2: \"%s\".\n",
                                pred11.toString());
                }
            }
        }


        // negative index

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                fargID = pve12.getFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                pred11.replaceArg(-1, arg);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;


                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "replaceArg() with -1 index completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("replaceArg() with -1 index " +
                                        "didn't throw a SystemErrorException.\n");
                    }
                }
            }
            else if ( pred11.toString().compareTo("pve12(33.0, 44, what_ever, " +
                              "pve0(<arg1>, <arg2>), \"q-string-3\", " +
                              "00:00:00:000, <arg7>, <arg8>, <arg9>, " +
                              "<arg10>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                        outStream.printf(
                                "Unexpected pred11.toString() - 3: \"%s\".\n",
                                pred11.toString());
                }
            }
        }

        // positive unused index

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                fargID = pve12.getFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                pred11.replaceArg(10, arg);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;


                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "replaceArg() with index 10 completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("replaceArg() with index 10 " +
                                        "didn't throw a SystemErrorException.\n");
                    }
                }
            }
            else if ( pred11.toString().compareTo("pve12(33.0, 44, what_ever, " +
                              "pve0(<arg1>, <arg2>), \"q-string-3\", " +
                              "00:00:00:000, <arg7>, <arg8>, <arg9>, " +
                              "<arg10>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                        outStream.printf(
                                "Unexpected pred11.toString() - 4: \"%s\".\n",
                                pred11.toString());
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

    } /* Predicate::TestArgListManagement() */


    /**
     * TestCopyConstructor()
     *
     * Run a battery of tests on the copy constructor for this
     * class, and on the instance returned.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestCopyConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        final String mName = "Predicate::TestCopyConstructor(): ";
        String testBanner =
            "Testing copy constructor for class Predicate                     ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int i;
        int failures = 0;
        long pveID1 = DBIndex.INVALID_ID;
        long pveID2 = DBIndex.INVALID_ID;
        long fargID;
        Database db = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        FormalArgument farg = null;
        Vector<DataValue> argList;
        Vector<DataValue> argList0 = null;
        Vector<DataValue> argList1 = null;
        DataValue arg = null;
        Predicate pred   = null;
        Predicate pred0  = null;
        Predicate pred1  = null;
        Predicate pred1a = null;
        Predicate pred2  = null;
        Predicate pred2a = null;
        Predicate pred0_copy  = null;
        Predicate pred1_copy  = null;
        Predicate pred1a_copy = null;
        Predicate pred2_copy  = null;
        Predicate pred2a_copy = null;
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // setup the test:
        try
        {
            db = new ODBCDatabase();

            pred0 = new Predicate(db);

            pve1 = new PredicateVocabElement(db, "test1");

            farg = new FloatFormalArg(db, "<float>");
            pve1.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve1.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve1.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve1.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve1.appendFormalArg(farg);

            pveID1 = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pveID1);

            pred1 = new Predicate(db, pveID1);

            argList0 = new Vector<DataValue>();

            fargID = pve1.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 1.0);
            argList0.add(arg);
            fargID = pve1.getFormalArg(1).getID();
            arg = new IntDataValue(db, fargID, 2);
            argList0.add(arg);
            fargID = pve1.getFormalArg(2).getID();
            arg = new NominalDataValue(db, fargID, "a_nominal");
            argList0.add(arg);
            fargID = pve1.getFormalArg(3).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, pveID1));
            argList0.add(arg);
            fargID = pve1.getFormalArg(4).getID();
            arg = new QuoteStringDataValue(db, fargID, "q-string");
            argList0.add(arg);
            fargID = pve1.getFormalArg(5).getID();
            arg = new TimeStampDataValue(db, fargID,
                                         new TimeStamp(db.getTicks()));
            argList0.add(arg);
            fargID = pve1.getFormalArg(6).getID();
            arg = new UndefinedDataValue(db, fargID,
                                         pve1.getFormalArg(6).getFargName());
            argList0.add(arg);

            pred1a = new Predicate(db, pveID1, argList0);


            pve2 = new PredicateVocabElement(db, "test2");

            farg = new UnTypedFormalArg(db, "<arg>");
            pve2.appendFormalArg(farg);

            pve2.setVarLen(true);

            pveID2 = db.addPredVE(pve2);

            // get a copy of the databases version of pve2 with ids assigned
            pve2 = db.getPredVE(pveID2);

            pred2 = new Predicate(db, pveID2);

            argList1 = new Vector<DataValue>();

            fargID = pve2.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 43);
            argList1.add(arg);

            pred2a = new Predicate(db, pveID2, argList1);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pred0 == null ) ||
             ( pve1 == null ) ||
             ( pveID1 == DBIndex.INVALID_ID ) ||
             ( argList0 == null ) ||
             ( argList0.size() != 7 ) ||
             ( pred1 == null ) ||
             ( pred1a == null ) ||
             ( pve2 == null ) ||
             ( pveID2 == DBIndex.INVALID_ID ) ||
             ( argList1 == null ) ||
             ( argList1.size() != 1 ) ||
             ( pred2 == null ) ||
             ( pred2a == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }

                if ( pred0 == null )
                {
                    outStream.print("creation of pred0 failed.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pveID1 == DBIndex.INVALID_ID )
                {
                    outStream.print("insertion of pve1 failed.\n");
                }

                if ( argList0 == null )
                {
                    outStream.print("creation of argList0 failed.\n");
                }
                else if ( argList0.size() != 7 )
                {
                    outStream.print("unexpected argList0 length.\n");
                }

                if ( pred1 == null )
                {
                    outStream.print("new Predicate(db, pveID1) " +
                                    "returned null.\n");
                }

                if ( pred1a == null )
                {
                    outStream.print("new Predicate(db, pveID1, argList0) " +
                                    "returned null.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pveID2 == DBIndex.INVALID_ID )
                {
                    outStream.print("insertion of pve2 failed.\n");
                }

                if ( argList1 == null )
                {
                    outStream.print("creation of argList1 failed.\n");
                }
                else if ( argList1.size() != 1 )
                {
                    outStream.print("unexpected argList1 length.\n");
                }

                if ( pred2 == null )
                {
                    outStream.print("new Predicate(db, pveID2) " +
                                    "returned null.\n");
                }

                if ( pred2a == null )
                {
                    outStream.print("new Predicate(db, pveID2, argList1) " +
                                    "returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw a SystemErrorException:" +
                                     "\"%s\".\n", SystemErrorExceptionString);
                }
            }
        }


        // test setup complete -- now for the test proper

        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            completed = false;

            try
            {
                pred0_copy  = new Predicate(pred0);
                pred1_copy  = new Predicate(pred1);
                pred1a_copy = new Predicate(pred1a);
                pred2_copy  = new Predicate(pred2);
                pred2a_copy = new Predicate(pred2a);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( pred0_copy == null ) ||
                 ( pred1_copy == null ) ||
                 ( pred1a_copy == null ) ||
                 ( pred2_copy == null ) ||
                 ( pred2a_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred0_copy == null )
                    {
                        outStream.print("new Predicate(pred0) returned null.\n");
                    }

                    if ( pred1_copy == null )
                    {
                        outStream.print("new Predicate(pred1) returned null.\n");
                    }

                    if ( pred1a_copy == null )
                    {
                        outStream.print("new Predicate(pred1a) returned null.\n");
                    }

                    if ( pred2_copy == null )
                    {
                        outStream.print("new Predicate(pred2) returned null.\n");
                    }

                    if ( pred2a_copy == null )
                    {
                        outStream.print("new Predicate(pred2a) returned null.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("copy constructors failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "a copy constructor threw a SystemErrorException:" +
                            "\"%s\".\n", SystemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0_copy.getDB() != db ) ||
                 ( pred1_copy.getDB() != db ) ||
                 ( pred1a_copy.getDB() != db ) ||
                 ( pred2_copy.getDB() != db ) ||
                 ( pred2a_copy.getDB() != db ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("A copy refers to an unexpected db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0_copy.getPveID() != DBIndex.INVALID_ID ) ||
                 ( pred1_copy.getPveID() != pveID1 ) ||
                 ( pred1a_copy.getPveID() != pveID1 ) ||
                 ( pred2_copy.getPveID() != pveID2 ) ||
                 ( pred2a_copy.getPveID() != pveID2 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "a copy refers to an unexpected pred ID.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0_copy.getPredName().compareTo("") != 0 ) ||
                 ( pred1_copy.getPredName().compareTo("test1") != 0 ) ||
                 ( pred1a_copy.getPredName().compareTo("test1") != 0 ) ||
                 ( pred2_copy.getPredName().compareTo("test2") != 0 ) ||
                 ( pred2a_copy.getPredName().compareTo("test2") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                       "a copy has an unexpected initial value of predName.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0_copy.argList != null ) ||
                 ( pred1_copy.argList == null ) ||
                 ( pred1_copy.argList == pred1.argList ) ||
                 ( pred1_copy.argList.size() != 7 ) ||
                 ( pred1a_copy.argList == null ) ||
                 ( pred1a_copy.argList == pred1.argList ) ||
                 ( pred1a_copy.argList.size() != 7 ) ||
                 ( pred2_copy.argList == null ) ||
                 ( pred2_copy.argList == pred2.argList ) ||
                 ( pred2_copy.argList.size() != 1 ) ||
                 ( pred2a_copy.argList == null ) ||
                 ( pred2a_copy.argList == pred2.argList ) ||
                 ( pred2a_copy.argList.size() != 1 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                       "a copy has an unexpected initial value of argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0_copy.getVarLen() != false ) ||
                 ( pred1_copy.getVarLen() != false ) ||
                 ( pred1a_copy.getVarLen() != false ) ||
                 ( pred2_copy.getVarLen() != true ) ||
                 ( pred2a_copy.getVarLen() != true ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "a copy has an unexpected initial value of varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0_copy.toString().compareTo("()") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0.toString(): \"%s\".\n",
                                     pred0_copy.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0_copy.toDBString().compareTo(
                    "(predicate (id 0) (predID 0) (predName ) (varLen false) " +
                    "(argList ())))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0.toDBString(): \"%s\".\n",
                                     pred0.toDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.toString().compareTo(
                    "test1(0.0, 0, , (), \"\", 00:00:00:000, <untyped>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred1.toString(): \"%s\".\n",
                                     pred1.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.toDBString().compareTo(
                 "(predicate (id 0) " +
                     "(predID 1) " +
                     "(predName test1) " +
                     "(varLen false) " +
                     "(argList ((FloatDataValue (id 0) " +
                                               "(itsFargID 2) " +
                                               "(itsFargType FLOAT) " +
                                               "(itsCellID 0) " +
                                               "(itsValue 0.0) " +
                                               "(subRange false) " +
                                               "(minVal 0.0) " +
                                               "(maxVal 0.0)), " +
                               "(IntDataValue (id 0) " +
                                             "(itsFargID 3) " +
                                             "(itsFargType INTEGER) " +
                                             "(itsCellID 0) " +
                                             "(itsValue 0) " +
                                             "(subRange false) " +
                                             "(minVal 0) " +
                                             "(maxVal 0)), " +
                               "(NominalDataValue (id 0) " +
                                              "(itsFargID 4) " +
                                              "(itsFargType NOMINAL) " +
                                              "(itsCellID 0) " +
                                              "(itsValue <null>) " +
                                              "(subRange false)), " +
                               "(PredDataValue (id 0) " +
                                            "(itsFargID 5) " +
                                            "(itsFargType PREDICATE) " +
                                            "(itsCellID 0) " +
                                            "(itsValue ()) " +
                                            "(subRange false)), " +
                               "(QuoteStringDataValue (id 0) " +
                                         "(itsFargID 6) " +
                                         "(itsFargType QUOTE_STRING) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <null>) " +
                                         "(subRange false)), " +
                               "(TimeStampDataValue (id 0) " +
                                       "(itsFargID 7) " +
                                       "(itsFargType TIME_STAMP) " +
                                       "(itsCellID 0) " +
                                       "(itsValue (60,00:00:00:000)) " +
                                       "(subRange false)), " +
                               "(UndefinedDataValue (id 0) " +
                                        "(itsFargID 8) " +
                                        "(itsFargType UNTYPED) " +
                                        "(itsCellID 0) " +
                                        "(itsValue <untyped>) " +
                                        "(subRange false))))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1.toDBString(): \"%s\".\n",
                            pred1.toDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.toString().compareTo(
                    "test1(1.0, 2, a_nominal, test1(0.0, 0, , (), \"\", " +
                    "00:00:00:000, <untyped>), \"q-string\", 00:00:00:000, " +
                    "<untyped>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred1a.toString(): \"%s\".\n",
                                     pred1a.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.toDBString().compareTo(
                 "(predicate (id 0) " +
                     "(predID 1) " +
                     "(predName test1) " +
                     "(varLen false) " +
                     "(argList ((FloatDataValue (id 0) " +
                                               "(itsFargID 2) " +
                                               "(itsFargType FLOAT) " +
                                               "(itsCellID 0) " +
                                               "(itsValue 1.0) " +
                                               "(subRange false) " +
                                               "(minVal 0.0) " +
                                               "(maxVal 0.0)), " +
                               "(IntDataValue (id 0) " +
                                             "(itsFargID 3) " +
                                             "(itsFargType INTEGER) " +
                                             "(itsCellID 0) " +
                                             "(itsValue 2) " +
                                             "(subRange false) " +
                                             "(minVal 0) " +
                                             "(maxVal 0)), " +
                               "(NominalDataValue (id 0) " +
                                              "(itsFargID 4) " +
                                              "(itsFargType NOMINAL) " +
                                              "(itsCellID 0) " +
                                              "(itsValue a_nominal) " +
                                              "(subRange false)), " +
                               "(PredDataValue (id 0) " +
                                   "(itsFargID 5) " +
                                   "(itsFargType PREDICATE) " +
                                   "(itsCellID 0) " +
                                   "(itsValue (predicate (id 0) " +
                                      "(predID 1) " +
                                      "(predName test1) " +
                                      "(varLen false) " +
                                      "(argList " +
                                          "((FloatDataValue (id 0) " +
                                              "(itsFargID 2) " +
                                              "(itsFargType FLOAT) " +
                                              "(itsCellID 0) " +
                                              "(itsValue 0.0) " +
                                              "(subRange false) " +
                                              "(minVal 0.0) " +
                                              "(maxVal 0.0)), " +
                                           "(IntDataValue (id 0) " +
                                               "(itsFargID 3) " +
                                               "(itsFargType INTEGER) " +
                                               "(itsCellID 0) " +
                                               "(itsValue 0) " +
                                               "(subRange false) " +
                                               "(minVal 0) " +
                                               "(maxVal 0)), " +
                                           "(NominalDataValue (id 0) " +
                                               "(itsFargID 4) " +
                                               "(itsFargType NOMINAL) " +
                                               "(itsCellID 0) " +
                                               "(itsValue <null>) " +
                                               "(subRange false)), " +
                                           "(PredDataValue (id 0) " +
                                               "(itsFargID 5) " +
                                               "(itsFargType PREDICATE) " +
                                               "(itsCellID 0) " +
                                               "(itsValue ()) " +
                                               "(subRange false)), " +
                                           "(QuoteStringDataValue (id 0) " +
                                               "(itsFargID 6) " +
                                               "(itsFargType QUOTE_STRING) " +
                                               "(itsCellID 0) " +
                                               "(itsValue <null>) " +
                                               "(subRange false)), " +
                                           "(TimeStampDataValue (id 0) " +
                                               "(itsFargID 7) " +
                                               "(itsFargType TIME_STAMP) " +
                                               "(itsCellID 0) " +
                                               "(itsValue (60,00:00:00:000)) " +
                                               "(subRange false)), " +
                                           "(UndefinedDataValue (id 0) " +
                                               "(itsFargID 8) " +
                                               "(itsFargType UNTYPED) " +
                                               "(itsCellID 0) " +
                                               "(itsValue <untyped>) " +
                                               "(subRange false))))))) " +
                                   "(subRange false)), " +
                               "(QuoteStringDataValue (id 0) " +
                                         "(itsFargID 6) " +
                                         "(itsFargType QUOTE_STRING) " +
                                         "(itsCellID 0) " +
                                         "(itsValue q-string) " +
                                         "(subRange false)), " +
                               "(TimeStampDataValue (id 0) " +
                                       "(itsFargID 7) " +
                                       "(itsFargType TIME_STAMP) " +
                                       "(itsCellID 0) " +
                                       "(itsValue (60,00:00:00:000)) " +
                                       "(subRange false)), " +
                               "(UndefinedDataValue (id 0) " +
                                        "(itsFargID 8) " +
                                        "(itsFargType UNTYPED) " +
                                        "(itsCellID 0) " +
                                        "(itsValue <untyped>) " +
                                        "(subRange false))))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1a.toDBString(): \"%s\".\n",
                            pred1a.toDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred2.toString().compareTo("test2(<arg>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred2.toString(): \"%s\".\n",
                                     pred2.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred2.toDBString().compareTo(
                "(predicate (id 0) " +
                           "(predID 9) " +
                           "(predName test2) " +
                           "(varLen true) " +
                           "(argList ((UndefinedDataValue (id 0) " +
                                                "(itsFargID 10) " +
                                                "(itsFargType UNTYPED) " +
                                                "(itsCellID 0) " +
                                                "(itsValue <arg>) " +
                                                "(subRange false))))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred2.toDBString(): \"%s\".\n",
                            pred2.toDBString());
                }
            }
        }


        if ( failures == 0 )
        {
            if ( pred2a.toString().compareTo("test2(43)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred2a.toString(): \"%s\".\n",
                                     pred2a.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred2a.toDBString().compareTo(
                "(predicate (id 0) " +
                           "(predID 9) " +
                           "(predName test2) " +
                           "(varLen true) " +
                           "(argList ((IntDataValue (id 0) " +
                                                   "(itsFargID 10) " +
                                                   "(itsFargType UNTYPED) " +
                                                   "(itsCellID 0) " +
                                                   "(itsValue 43) " +
                                                   "(subRange false) " +
                                                   "(minVal 0) " +
                                                   "(maxVal 0))))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred2a.toDBString(): \"%s\".\n",
                            pred2a.toDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyPredicateCopy(pred0, pred0_copy, outStream,
                                            verbose, "pred0", "pred0_copy");

            failures += VerifyPredicateCopy(pred1, pred1_copy, outStream,
                                            verbose, "pred1", "pred1_copy");

            failures += VerifyPredicateCopy(pred1a, pred1a_copy, outStream,
                                            verbose, "pred1a", "pred1a_copy");

            failures += VerifyPredicateCopy(pred2, pred2_copy, outStream,
                                            verbose, "pred2", "pred2_copy");

            failures += VerifyPredicateCopy(pred2a, pred2a_copy, outStream,
                                            verbose, "pred2a", "pred2a_copy");
        }


        /* now verify that the copy constructor fails when passed an invalid
         * reference to a predicate.  For now, this just means passing in a
         * null.
         */
        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            pred = new Predicate((Predicate)null);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( pred != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred != null )
                {
                    outStream.print("new Predicate(null) != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new Predicate(null) " +
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

    } /* Predicate::TestCopyConstructor() */


    /**
     * TestToStringMethods()
     *
     * Run a battery of tests on the to string methods for this
     * class.
     *
     *                                              JRM -- 10/29/07
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
        PredicateVocabElement pve0   = null;
        PredicateVocabElement pve1   = null;
        long fargID                  = DBIndex.INVALID_ID;
        long pve0ID                  = DBIndex.INVALID_ID;
        long pve1ID                  = DBIndex.INVALID_ID;
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
        Predicate pred0              = null;
        Predicate pred1              = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database and pve's
        threwSystemErrorException = false;
        completed = false;

        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");

            farg = new FloatFormalArg(db, "<float>");
            pve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve0.appendFormalArg(farg);

            pve0ID = db.addPredVE(pve0);

            // get a copy of the databases version of pve4 with ids assigned
            pve0 = db.getPredVE(pve0ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve1.appendFormalArg(farg);
            pve1.setVarLen(true);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve3 with ids assigned
            pve1 = db.getPredVE(pve1ID);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
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

                if ( pve0ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0ID not initialized.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            SystemErrorExceptionString);
                }
            }
        }

        // Setup the predicates that we will used for the toString and
        // toDBString tests.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            String testString0 = "pve0(1.0, 2, a_nominal, pve1(<arg1>), " +
                                 "\"q-string\", 00:00:00:000, <untyped>)";
            String testDBString0 =
                "(predicate (id 0) " +
                        "(predID 1) " +
                        "(predName pve0) " +
                        "(varLen false) " +
                        "(argList " +
                            "((FloatDataValue (id 100) " +
                                "(itsFargID 2) " +
                                "(itsFargType FLOAT) " +
                                "(itsCellID 500) " +
                                "(itsValue 1.0) " +
                                "(subRange false) " +
                                "(minVal 0.0) " +
                                "(maxVal 0.0)), " +
                            "(IntDataValue (id 101) " +
                                "(itsFargID 3) " +
                                "(itsFargType INTEGER) " +
                                "(itsCellID 500) " +
                                "(itsValue 2) " +
                                "(subRange false) " +
                                "(minVal 0) " +
                                "(maxVal 0)), " +
                            "(NominalDataValue (id 102) " +
                                "(itsFargID 4) " +
                                "(itsFargType NOMINAL) " +
                                "(itsCellID 500) " +
                                "(itsValue a_nominal) " +
                                "(subRange false)), " +
                            "(PredDataValue (id 103) " +
                                "(itsFargID 5) " +
                                "(itsFargType PREDICATE) " +
                                "(itsCellID 500) " +
                                "(itsValue " +
                                    "(predicate (id 0) " +
                                        "(predID 9) " +
                                        "(predName pve1) " +
                                        "(varLen true) " +
                                        "(argList " +
                                            "((UndefinedDataValue (id 0) " +
                                                "(itsFargID 10) " +
                                                "(itsFargType UNTYPED) " +
                                                "(itsCellID 0) " +
                                                "(itsValue <arg1>) " +
                                                "(subRange false))))))) " +
                                "(subRange false)), " +
                            "(QuoteStringDataValue (id 104) " +
                                "(itsFargID 6) " +
                                "(itsFargType QUOTE_STRING) " +
                                "(itsCellID 500) " +
                                "(itsValue q-string) " +
                                "(subRange false)), " +
                            "(TimeStampDataValue (id 105) " +
                                "(itsFargID 7) " +
                                "(itsFargType TIME_STAMP) " +
                                "(itsCellID 500) " +
                                "(itsValue (60,00:00:00:000)) " +
                                "(subRange false)), " +
                            "(UndefinedDataValue (id 106) " +
                                "(itsFargID 8) " +
                                "(itsFargType UNTYPED) " +
                                "(itsCellID 500) " +
                                "(itsValue <untyped>) " +
                                "(subRange false))))))";

            String testString1 = "pve1(99)";
            String testDBString1 =
                "(predicate (id 0) " +
                        "(predID 9) " +
                        "(predName pve1) " +
                        "(varLen true) " +
                        "(argList " +
                            "((IntDataValue (id 107) " +
                                "(itsFargID 10) " +
                                "(itsFargType UNTYPED) " +
                                "(itsCellID 501) " +
                                "(itsValue 99) " +
                                "(subRange false) " +
                                "(minVal 0) " +
                                "(maxVal 0))))))";

            try
            {
                argList0 = new Vector<DataValue>();

                fargID = pve0.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                argList0.add(arg);
                fargID = pve0.getFormalArg(1).getID();
                arg = new IntDataValue(db, fargID, 2);
                argList0.add(arg);
                fargID = pve0.getFormalArg(2).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                argList0.add(arg);
                fargID = pve0.getFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve1ID));
                argList0.add(arg);
                fargID = pve0.getFormalArg(4).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                argList0.add(arg);
                fargID = pve0.getFormalArg(5).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks()));
                argList0.add(arg);
                fargID = pve0.getFormalArg(6).getID();
                arg = new UndefinedDataValue(db, fargID,
                                             pve0.getFormalArg(6).getFargName());
                argList0.add(arg);

                pred0 = new Predicate(db, pve0ID, argList0);

                // set argument IDs to dummy values to test toDBString()
                pred0.argList.get(0).setID(100);
                pred0.argList.get(1).setID(101);
                pred0.argList.get(2).setID(102);
                pred0.argList.get(3).setID(103);
                pred0.argList.get(4).setID(104);
                pred0.argList.get(5).setID(105);
                pred0.argList.get(6).setID(106);

                // set argument cellIDs to dummy values to test toDBString()
                pred0.argList.get(0).itsCellID = 500;
                pred0.argList.get(1).itsCellID = 500;
                pred0.argList.get(2).itsCellID = 500;
                pred0.argList.get(3).itsCellID = 500;
                pred0.argList.get(4).itsCellID = 500;
                pred0.argList.get(5).itsCellID = 500;
                pred0.argList.get(6).itsCellID = 500;

                argList1 = new Vector<DataValue>();

                fargID = pve1.getFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 99);
                arg.setID(107); // a dummy value to test toDBString() method
                argList1.add(arg);

                pred1 = new Predicate(db, pve1ID, argList1);

                // set argument IDs to dummy values to test toDBString()
                pred1.argList.get(0).setID(107);

                // set argument cellIDs to dummy values to test toDBString()
                pred1.argList.get(0).itsCellID = 501;

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( argList0 == null ) ||
                 ( argList0.size() != 7 ) ||
                 ( pred0 == null ) ||
                 ( argList1 == null ) ||
                 ( argList1.size() != 1 ) ||
                 ( pred1 == null ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( argList0 == null )
                    {
                        outStream.print("argList0 allocation failed.\n");
                    }
                    else if ( argList0.size() != 7 )
                    {
                        outStream.printf("unexpected argList0.size(): %d (7).\n",
                                         argList0.size());
                    }

                    if ( argList1 == null )
                    {
                        outStream.print("argList1 allocation failed.\n");
                    }
                    else if ( argList1.size() != 1 )
                    {
                        outStream.printf("unexpected argList1.size(): %d (1).\n",
                                         argList1.size());
                    }

                    if ( ( pred0 == null ) ||
                         ( pred1 == null ) )
                    {
                        outStream.print("one or more Predicate allocation(s) " +
                                        "failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("test predicate allocation failed " +
                                        "to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test predicate allocation threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         SystemErrorExceptionString);
                    }
                }
            }
            else if ( ( pred0.argList.get(0).getID() != 100 ) ||
                      ( pred0.argList.get(1).getID() != 101 ) ||
                      ( pred0.argList.get(2).getID() != 102 ) ||
                      ( pred0.argList.get(3).getID() != 103 ) ||
                      ( pred0.argList.get(4).getID() != 104 ) ||
                      ( pred0.argList.get(5).getID() != 105 ) ||
                      ( pred0.argList.get(6).getID() != 106 ) ||
                      ( pred1.argList.get(0).getID() != 107 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred.argList arg ID(s): " +
                            "%d %d %d %d %d %d %d - %d\n",
                            pred0.argList.get(0).getID(),
                            pred0.argList.get(1).getID(),
                            pred0.argList.get(2).getID(),
                            pred0.argList.get(3).getID(),
                            pred0.argList.get(4).getID(),
                            pred0.argList.get(5).getID(),
                            pred0.argList.get(6).getID(),
                            pred1.argList.get(0).getID());
                }
            }
            else if ( ( pred0.toString().compareTo(testString0) != 0 ) ||
                      ( pred1.toString().compareTo(testString1) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred0.toString().compareTo(testString0) != 0 )
                    {
                       outStream.printf("Unexpected pred0.toString)(): \"%s\"\n",
                                         pred0.toString());
                    }

                    if ( pred1.toString().compareTo(testString1) != 0 )
                    {
                       outStream.printf("Unexpected pred1.toString)(): \"%s\"\n",
                                         pred1.toString());
                    }
                }
            }
            else if ( ( pred0.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred1.toDBString().compareTo(testDBString1) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred0.toDBString().compareTo(testDBString0) != 0 )
                    {
                       outStream.printf(
                               "Unexpected pred0.toDBString)(): \"%s\"\n",
                               pred0.toDBString());
                    }

                    if ( pred1.toDBString().compareTo(testDBString1) != 0 )
                    {
                       outStream.printf(
                               "Unexpected pred1.toDBString)(): \"%s\"\n",
                               pred1.toDBString());
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

    } /* Predicate::TestToStringMethods() */


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

    public static int VerifyArgListAssignment(Predicate target,
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

            new_dv = target.getArg(idx);

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
                    outStream.printf(
                            "%s.replaceArg(%d, %s) failed to complete.\n",
                            targetDesc, idx, newArgDesc);
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
                    outStream.printf(
                        "%s.replaceArg(%d, %s) test failed to complete.\n",
                        targetDesc, idx, newArgDesc);

                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test threw " +
                            "system error: \"%s\"\n",
                            targetDesc, idx, newArgDesc,
                            systemErrorExceptionString);

                }
            }
        }

        if ( new_dv instanceof UndefinedDataValue )
        {
            long target_pve_ID = DBIndex.INVALID_ID;
            String old_dv_val = null;
            String new_dv_val = null;
            String farg_name = null;
            PredicateVocabElement target_pve = null;

            try
            {
                if ( old_dv instanceof UndefinedDataValue )
                {
                    old_dv_val = ((UndefinedDataValue)old_dv).getItsValue();
                }
                new_dv_val = ((UndefinedDataValue)new_dv).getItsValue();
                target_pve_ID = target.getPveID();
                target_pve = target.db.vl.getPredicateVocabElement(target_pve_ID);
                farg_name = target_pve.getFormalArg(idx).getFargName();
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

    } /* Predicate::VerifyArgListAssignment() */


    /**
     * VerifyArgListAsgnmntFails()
     *
     * Verify that the specified replacement of an argument list
     * entry fails.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyArgListAsgnmntFails(Predicate target,
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

    } /* Predicate::VerifyArgListAsgnmntFails() */


    /**
     * VerifyPredCopy()
     *
     * Verify that the supplied instances of Predicate are distinct,
     * that they contain no common references (other than db), and that they
     * have the same value.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyPredicateCopy(Predicate base,
                                          Predicate copy,
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
        else if ( base.pveID != copy.pveID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.predID == %d != %s.predID == %d.\n",
                                 baseDesc, base.pveID, copyDesc, copy.pveID);
            }
        }
        else if ( ( base.predName == copy.predName ) &&
                  ( base.predName != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                     "%s.predName and %s.predName refer to the same string.\n",
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

    } /* Predicate::VerifyPredCopy() */

} /* class Predicate */
