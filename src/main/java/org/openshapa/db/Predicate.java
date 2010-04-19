/*
 * Predicate.java
 *
 * Created on August 19, 2007, 10:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;
import java.util.Vector;


/**
 * Class Predicate
 *
 * Primitive class for predicates.  Instances of this class are used to store
 * predicates in a database.  Since predicates must be defined in the vocab
 * list before they can be created, instances of this class are tightly
 * bound to their host database and its vocab list.
 *
 *                                                  -- 8/19/07
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

    /** ID of the represented predicate, or the INVALID_ID if the predicate
     *  is undefined.
     */
    protected long pveID = DBIndex.INVALID_ID;

    /** Name of the represented predicate, or the empty string if the predicate
     *  is undefined.
     */
    protected String predName = null;

    /** Argument list of the predicate, or null if the predicate is undefined */
    protected Vector<DataValue> argList = null;

    /** Whether the predicate has a variable length argument list */
    protected boolean varLen = false;

    /** ID of cell in which this col pred appears, if any */
    protected long cellID = DBIndex.INVALID_ID;

    /** whether parameters can be query variables */
    private boolean queryVarOK = false;



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
     *                                               -- 8/16/07
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
            dbe = this.getDB().idx.getElement(predID);

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
            dbe = this.getDB().idx.getElement(predID);

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
     * getCellID()
     *
     * Return the current value of the cellID field.
     *
     *                          -- 4/4/08
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
     *                          -- 8/23/07
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
     *                                      -- 8/23/07
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
     *                                          -- 8/23/07
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
     *                          -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     */

    public boolean getVarLen()
    {

        return this.varLen;

    } /* Predicate::getVarLen() */


    // getQueryVarOK()
    /**
     * Return the current value of the queryVarOK field.
     *
     *                                      -- 10/05/09
     *
     * Changes:
     *
     *    - None.
     */

    public boolean getQueryVarOK()
    {
        return this.queryVarOK;
    }

    // setQueryVarOK()
    /**
     * Set the queryVarOK field to true.
     *
     *                                      -- 10/05/09
     *
     * Changes:
     *
     *    - None.
     */

    public void setQueryVarOK()
    {
        this.queryVarOK = true;
    }


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
     *                                            -- 3/20/08
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

        if ( this.getDB() != db )
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

        dbe = this.getDB().idx.getElement(this.cellID);

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
     *                                  -- 3/20/08
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

        if ( this.getDB() != db )
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

        dbe = this.getDB().idx.getElement(this.cellID);

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
     *                                          -- 8/23/07
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
     * Construct an escaped string containing the values of the arguments in the
     * format: (value0, value1, ... value).
     *                                          -- 8/23/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    protected String argListToEscapedString()
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
                s += this.getArg(i).toEscapedString() + ",";
                i++;
            }

            s += getArg(i).toEscapedString();

            s += ")";
        }

        return s;

    } /* Predicate::argListToEscapedString() */

    /**
     * argListToString()
     *
     * Construct a string containing the values of the arguments in the
     * format: (value0, value1, ... value).
     *                                          -- 8/23/07
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
     *                                              -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void insertInIndex(long DCID)
        throws SystemErrorException
    {
        final String mName = "Predicate::insertInIndex(): ";

        this.getDB().idx.addElement(this);

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
                dv.setItsPredID(this.getID());
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
     *                                              -- 8/20/07
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

        dbe = this.getDB().idx.getElement(predID);

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
     *                                              -- 2/19/08
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

        this.getDB().idx.removeElement(this.getID());

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
            s = "(predicate (id " + this.getID() +
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
     * toMODBFile()
     *
     * Write the MacSHAPA ODB style definition of the predicate and its
     * arguments to the supplied file in MacSHAPA ODB file format.  The output
     * of this method is an instantiation of <pred_value> or <pred_cell_value>
     * (as defined in the grammar defining the MacSHAPA ODB file format).
     *
     *                                              1/30/09
     *
     * Changes:
     *
     *    - Added the inPred argument to allow correct display of null
     *      predicates in both the context of a cell and a predicate or
     *      column predicate argument list.
     *
     *      This is necessary to allow databases with typed formal arguments
     *      in predicates and matrix columns to be written to an ODB file in
     *      such a fashion that the typing of formal arguments is lost
     *      cleanly.
     *                                                      11/3/09
     */

    protected void toMODBFile(java.io.PrintStream output,
                              boolean inPred,
                              String fargName)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "Predicate::toMODBFile()";
        int i = 0;
        int numArgs;
        int numArgsToDisplay;
        DataValue arg;

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( fargName == null )
        {
            throw new SystemErrorException(mName + "fargName null on entry.");
        }

        if ( this.pveID != DBIndex.INVALID_ID )
        {
            if ( ( this.predName == null ) ||
                 ( this.predName.length() == 0 ) )
            {
                /* predicate name undefined or of zero length!! */
                throw new SystemErrorException(mName + "predName undefined");
            }

            if ( this.argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!?!");
            }

            numArgs = this.argList.size();

            if ( numArgs <= 0 )
            {
                throw new SystemErrorException(mName + "numArgs <= 0");
            }

            if ( this.varLen )
            {
                numArgsToDisplay = 0;

                i = 0;

                /* scan the argument list to determine the number of the
                 * last defined argument in the argument list, as set
                 * numArgsToDisplay accordingly.
                 */
                while ( i < numArgs )
                {
                    arg = this.getArg(i);

                    if ( ! ( arg instanceof UndefinedDataValue ) )
                    {
                        numArgsToDisplay = i;
                    }

                    i++;
                }

                numArgsToDisplay++;
            }
            else
            {
                numArgsToDisplay = numArgs;
            }

            if ( ( numArgsToDisplay < 1 ) || ( numArgsToDisplay > numArgs ) )
            {
                throw new SystemErrorException(mName +
                        "numArgsToDisplay out of range.");
            }

            output.printf("( |%s| ", this.predName);

            i = 0;

            while ( i < numArgsToDisplay )
            {
                arg = this.getArg(i);

                arg.toMODBFile(output);

                i++;
            }

            output.printf(") ");

        }
        else
        {
            if ( inPred )
            {
                output.printf("|%s| ", fargName);
            }
            else
            {
                output.print("() ");
            }
        }

        return;

    } /* Predicate::toMODBFile() */


    /**
     * toMODBFile_update_local_vocab_list()
     *
     * If the predicate is defined, call dc.toMODBFile_update_local_vocab_list()
     * with the ID of the base predicate vocab element.  Then pass the
     * toMODBFile_update_local_vocab_list() message on to all the arguments
     * of the predicate.
     *
     * Otherwise do nothing.
     *
     *                                      7/2/09
     *
     * Changes;
     *
     *    - None.
     *
     * @param dc -- reference to the data column containing this predicate.
     *
     * @throws org.openshapa.db.SystemErrorException
     */

    protected void
    toMODBFile_update_local_vocab_list(DataColumn dc)
        throws SystemErrorException
    {
        final String mName = "Pred::toMODBFile_update_local_vocab_list(): ";
        int i = 0;
        int numArgs;
        DataValue arg;

        if ( dc == null )
        {
            throw new SystemErrorException(mName + "dc null on entry.");
        }

        if ( this.pveID != DBIndex.INVALID_ID )
        {
            dc.toMODBFile_update_local_vocab_list(this.pveID);

            if ( this.argList == null )
            {
                /* argList hasn't been instantiated yet -- scream and die */
                throw new SystemErrorException(mName + "argList unitialized?!?!");
            }

            numArgs = this.argList.size();

            if ( numArgs <= 0 )
            {
                throw new SystemErrorException(mName + "numArgs <= 0");
            }

            while ( i < numArgs )
            {
                arg = this.getArg(i);

                arg.toMODBFile_update_local_vocab_list(dc);

                i++;
            }

        }

        return;

    } /* Pred::toMODBFile_update_local_vocab_list() */

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
    public String toEscapedString()
    {
        String s;

        try
        {
            s = this.predName + this.argListToEscapedString();
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }

        return (s);

    } /* Predicate::toString() */

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
     *                                              -- 4/6/08
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

            try {
                cdv = (DataValue) dv.blindClone();
            } catch (CloneNotSupportedException e) {
                throw new SystemErrorException("Unable to clone DataValue.");
            }

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
     *                                              -- 8/20/07
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
     *                                              -- 8/20/07
     *
     * Changes:
     *
     *    - Added the clearID parameter and supporting code.
     *                                              -- 2/19/08
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
             // TODO: delete these print statements eventually
             System.out.printf("srcArgList.size() = %d, numArgs = %d\n", srcArgList.size(), numArgs);
             System.out.printf("pve.toString() = %s\n", pve.toString());
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
                    else if ( dv instanceof QueryVarDataValue )
                    {
                        cdv = new QueryVarDataValue((QueryVarDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
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
                    // TODO: delete this eventually
                    // assert( cdv != null );
                    break;

                case FLOAT:
                    if ( dv instanceof FloatDataValue )
                    {
                        cdv = new FloatDataValue((FloatDataValue)dv);
                    }
                    else if ( dv instanceof QueryVarDataValue )
                    {
                        cdv = new QueryVarDataValue((QueryVarDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
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
                    // TODO: delete this eventually
                    // assert( cdv != null );
                    break;

                case INTEGER:
                    if ( dv instanceof IntDataValue )
                    {
                        cdv = new IntDataValue((IntDataValue)dv);
                    }
                    else if ( dv instanceof QueryVarDataValue )
                    {
                        cdv = new QueryVarDataValue((QueryVarDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
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
                    // TODO: delete this eventually
                    // assert( cdv != null );
                    break;

                case NOMINAL:
                    if ( dv instanceof NominalDataValue )
                    {
                        cdv = new NominalDataValue((NominalDataValue)dv);
                    }
                    else if ( dv instanceof QueryVarDataValue )
                    {
                        cdv = new QueryVarDataValue((QueryVarDataValue)dv);
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
                    // TODO: delete this eventually
                    // assert( cdv != null );
                    break;

                case PREDICATE:
                    if ( dv instanceof PredDataValue )
                    {
                        cdv = new PredDataValue((PredDataValue)dv);
                    }
                    else if ( dv instanceof QueryVarDataValue )
                    {
                        cdv = new QueryVarDataValue((QueryVarDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
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
                    // TODO: delete this eventually
                    // assert( cdv != null );
                    break;

                case TIME_STAMP:
                    if ( dv instanceof TimeStampDataValue )
                    {
                        cdv = new TimeStampDataValue((TimeStampDataValue)dv);
                    }
                    else if ( dv instanceof QueryVarDataValue )
                    {
                        cdv = new QueryVarDataValue((QueryVarDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
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
                    // TODO: Delete this eventually
                    // assert( cdv != null );
                    break;

                case QUOTE_STRING:
                    if ( dv instanceof QuoteStringDataValue )
                    {
                        cdv =
                             new QuoteStringDataValue((QuoteStringDataValue)dv);
                    }
                    else if ( dv instanceof QueryVarDataValue )
                    {
                        cdv = new QueryVarDataValue((QueryVarDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
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
                    // TODO: delete this eventually
                    // assert( cdv != null );
                    break;

                case TEXT:
                    if ( dv instanceof TextStringDataValue )
                    {
                        cdv = new TextStringDataValue((TextStringDataValue)dv);
                    }
                    else if ( dv instanceof QueryVarDataValue )
                    {
                        cdv = new QueryVarDataValue((QueryVarDataValue)dv);
                    }
                    else if ( dv instanceof UndefinedDataValue )
                    {
                        cdv = new UndefinedDataValue((UndefinedDataValue)dv);
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
                    // TODO: delete this eventually
                    // assert( cdv != null );
                    break;

                case UNTYPED:
                    try {
                        cdv = (DataValue) dv.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new SystemErrorException("Unable to clone dv.");
                    }
                    // TODO: delete this eventually
                    if ( cdv == null ) // TODO: delete this eventually
                    {
                        System.out.printf("null cdv.  dv.toDBString = %s\ndv.toString = %s\n",
                                          dv.toDBString(), dv.toString());
                        cdv = new PredDataValue((PredDataValue)dv);
                    }
                    assert( cdv != null );
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

            // TODO: delete this eventually
            // assert ( cdv != null );

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
     *                                              -- 3/24/08
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

        if ( this.getDB() == null )
        {
            throw new SystemErrorException(mName + "this.db is null?!?");
        }

        if ( this.getDB().idx.getElement(this.getID()) != this )
        {
            throw new SystemErrorException(mName +
                    "not the cannonical incarnation of the predicate");
        }

        if ( this.pveID != DBIndex.INVALID_ID )
        {
            if ( ( ! cascadePveDel ) ||
                 ( cascadePveID != this.pveID ) ) // must de-register
            {

                dbe = this.getDB().idx.getElement(this.pveID);

                if ( ! ( dbe instanceof PredicateVocabElement ) )
                {
                    throw new SystemErrorException(mName +
                                               "pveID doesn't refer to a pve.");
                }

                pve = (PredicateVocabElement)dbe;

                pve.deregisterInternalListener(this.getID());
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
     * Gets the n-th argument of the predicate
     *
     * @n The index of the argument within the predicate that you wish to copy.
     *
     * @return A copy of the n-th argument if it exists, or null if it doesn't.
     *
     * @throws SystemErrorException If unable to create a copy of the n-th
     * predicate argument.
     *
     * @date 2009/04/29
     */
    public DataValue getArgCopy(int n) throws SystemErrorException {
        DataValue arg = this.getArg(n);
        DataValue argCopy = null;

        if (arg != null) {
            try {
                argCopy = (DataValue) arg.clone();
            } catch (CloneNotSupportedException e) {
                throw new SystemErrorException("Unable to clone DataValue");
            }
        }

        return argCopy;
    }

    /**
     * Gets the n-th argument of the predicate.
     *
     * @param n The index of the argument that you wish to fish a reference for.
     *
     * @return A reference to the n-th argument if it exists, or null if it
     * doesn't.
     *
     * @throws SystemErrorException If unable to create a copy
     *
     * @date 2007/08/23
     */
    protected DataValue getArg(int n) throws SystemErrorException {
        final String mName = "Predicate::getArg(): ";
        DataValue arg = null;

        if (pveID == DBIndex.INVALID_ID) {
            arg = null;

        // argList hasn't been instantiated yet -- scream and die
        } else if (argList == null) {
            throw new SystemErrorException(mName + "argList unitialized?!?!");

        // Can't have a negative index -- scream and die
        } else if (n < 0) {
            throw new SystemErrorException(mName + "negative index supplied");

        // n-th formal argument doesn't exist -- return null
        } else if (n >= argList.size()) {
            arg = null;

        // we have work to do
        } else {
            arg = argList.get(n);

            if (arg == null) {
                throw new SystemErrorException(mName + "arg is null?!?");
            }

            if (arg instanceof TextStringDataValue) {
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
     *                                      -- 8/23/07
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
     *                                              -- 3/24/08
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
            System.out.println((
                    (Predicate)(this.getDB().idx.getElement(this.getID()))).toString());
            System.out.println((
                    (Predicate)(this.getDB().idx.getElement(this.getID()))).toDBString());
            int j = 1/0;
            throw new SystemErrorException(mName +
                    "not the cannonical incarnation of the predicate");
        }

        if ( this.pveID != DBIndex.INVALID_ID ) // we have work to do
        {

            dbe = this.getDB().idx.getElement(this.pveID);

            if ( ! ( dbe instanceof PredicateVocabElement ) )
            {
                throw new SystemErrorException(mName +
                                               "pveID doesn't refer to a pve.");
            }

            pve = (PredicateVocabElement)dbe;

            pve.registerInternalListener(this.getID());


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
     *                                              -- 8/23/07
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
     *                                          -- 8/26/08
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
                                           "mveID doesn't refer to a pve.");
        }

        if ( this.pveID != DBIndex.INVALID_ID ) // the predicate is defined
        {
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
     *                                          -- 8/26/08
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

        if ( this.getDB() != db )
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
     *                                          -- 3/23/08
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
                        try {
                            dv = (DataValue) this.getArg(i).blindClone();
                        } catch (CloneNotSupportedException e) {
                            throw new SystemErrorException("Unable to clone DataValue.");
                        }

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

        if ( this.pveID != DBIndex.INVALID_ID ) // i.e. the predicate is defined.
        {
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
     *                                          -- 3/23/08
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

        if ( this.getDB() != db )
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
                                FormalArgument.FArgType.UNTYPED )
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

                            dv.setItsPredID(this.getID());

                            this.replaceArg(i, dv);
                        }
                        else if ( dv.getItsFargType() ==
                                FormalArgument.FArgType.PREDICATE )
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
     *                                      -- 2/20/08
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

        if ( oldPred.getID() == DBIndex.INVALID_ID )
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

        if ( ( this.getID() != DBIndex.INVALID_ID ) &&
             ( this.getID() != oldPred.getID() ) )
        {
            throw new SystemErrorException(mName +
                    "this.id not invalid and not equal to oldPred.id");
        }


        if ( this.pveID == DBIndex.INVALID_ID )
        {
            if ( oldPred.pveID == DBIndex.INVALID_ID )
            {
                this.getDB().idx.replaceElement(this);
            }
            else
            {
                // we are replacing a predicate with an undefined predicate.
                if ( this.getID() == DBIndex.INVALID_ID )
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
                    this.getDB().idx.replaceElement(this);
                }
            }
        }
        else if ( this.pveID != oldPred.pveID )
        {
            if ( oldPred.pveID == DBIndex.INVALID_ID )
            {
                // we are replacing an undefined predicate with a new predicate
                if ( this.getID() == DBIndex.INVALID_ID )
                {
                    oldPred.removeFromIndex(DCID);
                    this.insertInIndex(DCID);
                }
                else // this.id == oldPred.id
                {
                    this.getDB().idx.replaceElement(this);

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
                if ( this.getID() == DBIndex.INVALID_ID )
                {
                    oldPred.removeFromIndex(DCID);
                    this.insertInIndex(DCID);
                }
                else // this.id == oldPred.id
                {
                    this.getDB().idx.replaceElement(this);

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
        else if ( this.getID() == DBIndex.INVALID_ID )
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
            assert( this.getID() == oldPred.getID() );
            assert( this.pveID == oldPred.pveID );
            assert( this.pveID != DBIndex.INVALID_ID );

            this.getDB().idx.replaceElement(this);

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
            assert( this.getID() == oldPred.getID() );
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

            this.getDB().idx.replaceElement(this);

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
                    assert( this.getID() != DBIndex.INVALID_ID );

                    newArg.setItsPredID(this.getID());
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
     *                                              -- 10/28/08
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
                         ( arg instanceof QueryVarDataValue ) ||
                         ( arg instanceof UndefinedDataValue )
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
                         ( arg instanceof QueryVarDataValue ) ||
                         ( arg instanceof UndefinedDataValue ) 
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
                         ( arg instanceof QueryVarDataValue ) ||
                         ( arg instanceof UndefinedDataValue ) 
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
                         ( arg instanceof QueryVarDataValue ) ||
                         ( arg instanceof UndefinedDataValue )
                       )
                   )
                {
                    throw new SystemErrorException(mName + "Arg " +
                            "type mismatch: nominal DV, query var DV, or " +
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
                        if ( ! nfa.approved(ndv.getItsValue()) )
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
                         ( arg instanceof QueryVarDataValue ) ||
                         ( arg instanceof UndefinedDataValue ) 
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
                         ( arg instanceof QueryVarDataValue ) ||
                         ( arg instanceof UndefinedDataValue ) 
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
                         ( arg instanceof QueryVarDataValue ) ||
                         ( arg instanceof UndefinedDataValue ) 
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
                              ( arg instanceof QueryVarDataValue ) ||
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
     *                                              -- 2/19/08
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
     *                                              -- 2/19/08
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
     *                                              -- 10/28/08
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
                           ( oldArg instanceof QueryVarDataValue ) ||
                           ( oldArg instanceof UndefinedDataValue ) 
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
                         ( newArg instanceof QueryVarDataValue ) ||
                         ( newArg instanceof UndefinedDataValue ) 
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
                                "type mismatch: column predicate DV, or" +
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
                           ( newArg instanceof QueryVarDataValue ) ||
                           ( oldArg instanceof UndefinedDataValue ) 
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
                         ( newArg instanceof QueryVarDataValue ) ||
                         ( newArg instanceof UndefinedDataValue ) 
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
                           ( oldArg instanceof QueryVarDataValue ) ||
                           ( oldArg instanceof UndefinedDataValue ) 
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
                         ( newArg instanceof QueryVarDataValue ) ||
                         ( newArg instanceof UndefinedDataValue ) 
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
                           ( oldArg instanceof QueryVarDataValue ) ||
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
                         ( newArg instanceof QueryVarDataValue ) ||
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
                        if ( ! nfa.approved(new_ndv.getItsValue()) ) 
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
                           ( oldArg instanceof QueryVarDataValue ) ||
                           ( oldArg instanceof UndefinedDataValue ) 
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
                         ( newArg instanceof QueryVarDataValue ) ||
                         ( newArg instanceof UndefinedDataValue ) 
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
                           ( oldArg instanceof QueryVarDataValue ) ||
                           ( oldArg instanceof UndefinedDataValue ) 
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
                         ( newArg instanceof QueryVarDataValue ) ||
                         ( newArg instanceof UndefinedDataValue ) 
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
                           ( oldArg instanceof QueryVarDataValue ) ||
                           ( oldArg instanceof UndefinedDataValue ) 
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
                         ( newArg instanceof QueryVarDataValue ) ||
                         ( newArg instanceof UndefinedDataValue ) 
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
                              ( newArg instanceof QueryVarDataValue ) ||
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
     *                                              -- 2/19/08
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

        if ( this.getID() == DBIndex.INVALID_ID )
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
     *                                              -- 2/19/08
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
     *                                              -- 3/31/08
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

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += HashUtils.Long2H(pveID) * Constants.SEED1;
        hash += HashUtils.Obj2H(predName) * Constants.SEED2;
        hash += HashUtils.Obj2H(argList) * Constants.SEED3;
        hash += (varLen ? 1 : 0) * Constants.SEED4;
        hash += HashUtils.Long2H(cellID) * Constants.SEED5;
        hash += (queryVarOK ? 1 : 0) * Constants.SEED6;

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

} /* class Predicate */
