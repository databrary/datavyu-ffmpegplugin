/*
 * PredDataValue.java
 *
 * Created on August 19, 2007, 5:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;


/**
 * An instance of PredDataValue is used to store a predicate value
 * assigned to a formal argument.
 *
 * @author mainzer
 */
public class PredDataValue extends DataValue
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * itsValue: Instance of Predicate containing a representation of the
     *      current value assigned to the predicate.
     *
     * minVal & maxVal don't appear in PredDataValue as a subrange of
     *      predicates is expressed as a set of allowed predicates.  Given the
     *      potential size of this set, we don't keep a copy of it here --
     *      referring directly to the associated formal argument when needed
     *      instead.
     */

    /** ID of the represented predicate. */
    protected Predicate itsValue = null;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * PredDataValue()
     *
     * Constructor for instances of PredDataValue.
     *
     * Five versions of this constructor.
     *
     * The first takes a reference to a database as its parameter and just
     * calls the super() constructor.
     *
     * The second takes a reference to a database, and a formal argument ID, and
     * attempts to set the itsFargID field of the data value accordingly.
     *
     * The third takes a reference to a database, a formal argument ID, and
     * a value as arguments, and attempts to set the itsFargID and itsValue
     * of the data value accordingly.
     *
     * The fourth takes a reference to an instance of PredDataValue as an
     * argument, and uses it to create a copy.
     *
     * The fifth is much the same as the fourth, save that it takes the
     * additional blindCopy parameter.  If this parameter is true, the
     * PredDataValue is copied without reference to the pve's underlying
     * any predicates.  This is necessary if a pve has changed, and we need
     * a copy of the old predicate so we can touch it up for changes in the
     * associated pve.
     *
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public PredDataValue(Database db)
        throws SystemErrorException {
        super(db);

        this.setItsValue(null);

    } /* PredDataValue::PredDataValue(db) */

    public PredDataValue(Database db,
                        long fargID)
        throws SystemErrorException
    {
        super(db);

        this.setItsValue(null);

        this.setItsFargID(fargID);

    } /* PredDataValue::PredDataValue(db, fargID) */

    public PredDataValue(Database db,
                         long fargID,
                         Predicate value)
        throws SystemErrorException
    {
        super(db);

        this.setItsValue(value);

        this.setItsFargID(fargID);

    } /* PredDataValue::PredDataValue(db, fargID, value) */

    public PredDataValue(PredDataValue dv)
        throws SystemErrorException
    {
        super(dv);

        if ( dv.itsValue != null )
        {
            this.itsValue  = new Predicate(dv.itsValue);
        }

    } /* PredDataValue::PredDataValue(dv) */

    protected PredDataValue(PredDataValue dv,
                            boolean blindCopy)
        throws SystemErrorException
    {
        super(dv);

        if ( dv.itsValue != null )
        {
            this.itsValue  = new Predicate(dv.itsValue, blindCopy);
        }

    } /* PredDataValue::PredDataValue(dv, blindCopy) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getItsValue()
     *
     * Return a copy of the current value of the data value.
     *
     *                          JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    public Predicate getItsValue()
        throws SystemErrorException
    {

        return new Predicate(this.itsValue);

    } /* PredDataValue::getItsValue() */


    /**
     * getItsValueBlind()
     *
     * Return a blind copy of the current value of the data value.
     *
     * This is necessary if we are adjusting for a change in the definition
     * of a predicate, as a change in a predicate's argument list may cause
     * the normal sanity checks to fail.
     *
     *                          JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    protected Predicate getItsValueBlind()
        throws SystemErrorException
    {

        return new Predicate(this.itsValue, true);

    } /* PredDataValue::getItsValueBlind() */


    /**
     * getItsValuePveID()
     *
     * If itsValue is an instance of a predicate vocab element, return the
     * ID assigned to that pve, or the INVALID_ID if it is not.
     *
     *                                      JRM -- 4/24/08
     *
     * Changes:
     *
     *    - None.
     */

    protected long getItsValuePveID()
        throws SystemErrorException
    {
        long pveID = DBIndex.INVALID_ID;

        if ( this.itsValue != null )
        {
            pveID = this.itsValue.getPveID();
        }

        return pveID;

    } /* PredDataValue::getItsValuePveID() */


    /**
     * setItsValue()
     *
     * Set itsValue to the specified value.  If subrange is true, coerce the
     * value into the subrange.
     *
     * Coercing a predicate into the specified doesn't make much sense, so
     * at least for now, if the predicate is out of range, we will simply
     * replace it with an empty predicate.
     *
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    public void setItsValue(Predicate value)
        throws SystemErrorException
    {
        final String mName = "PredDataValue::setItsValue(): ";
        DBElement dbe;
        PredFormalArg pfa;

        if ( ( value != null ) &&
             ( value.getDB() != this.db ) )
        {
            throw new SystemErrorException(mName + "value.getDB() != this.db");
        }

        if ( ( value != null ) &&
             ( value.getPveID() != DBIndex.INVALID_ID ) &&
             ( ! db.vl.predInVocabList(value.getPveID()) ) )
        {
            throw new SystemErrorException(mName +
                    "! db.vl.predInVocabList(value.getPredID())");
        }

        if ( ( value == null ) ||
             ( value.getPveID() == DBIndex.INVALID_ID ) )
        {
            this.itsValue = new Predicate(this.db);
        }
        else if ( this.subRange )
        {
            if ( this.itsFargID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName +
                                      "subRange && (itsFargID == INVALID_ID)");
            }
            else if ( itsFargType != FormalArgument.fArgType.PREDICATE )
            {
                throw new SystemErrorException(mName +
                                               "itsFargType != PREDICATE");
            }

            dbe = this.db.idx.getElement(this.itsFargID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName +
                                               "itsFargID has no referent");
            }

            if ( ! ( dbe instanceof PredFormalArg ) )
            {
                throw new SystemErrorException(mName +
                                      "itsFargID doesn't refer to a predicate");
            }

            pfa = (PredFormalArg)dbe;

            if ( pfa.isValidValue(value) )
            {
                itsValue = new Predicate(value);
            }
            else // coerce to the undefined state
            {
                this.itsValue = new Predicate(this.db);
            }
        }
        else
        {
            this.itsValue = new Predicate(value);
        }

        return;

    } /* PredDataValue::setItsValue() */


    /*************************************************************************/
    /*************************** Overrides: **********************************/
    /*************************************************************************/

    /**
     * clearID()
     *
     * Call the superclass version of the method, and then pass the clear id
     * message on to the associated predicate, if any.
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
        super.clearID();

        if ( this.itsValue != null )
        {
            this.itsValue.clearID();
        }

        return;

    } /* PredDataValue::clearID() */


    /**
     * insertInIndex()
     *
     * Call the super, and then pass the insert in index message down to the
     * predicate.
     *
     *                                              JRM -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    @Override
    protected void insertInIndex(long DCID)
        throws SystemErrorException
    {
        final String mName = "PredDataValue::insertInIndex(): ";

        super.insertInIndex(DCID);

        if ( this.itsValue == null )
        {
            throw new SystemErrorException(mName + "itsValue is null?!?");
        }

        this.itsValue.insertInIndex(DCID);

        return;

    } /* PredDataValue::insertInIndex(DCID) */

    /**
     * removeFromIndex()
     *
     * Call the super, and then pass the remove from index message down to the
     * predicate.
     *
     *                                              JRM -- 2/19/08
     *
     * Changes:
     *
     *    - None.
     */

    @Override
    protected void removeFromIndex(long DCID)
        throws SystemErrorException
    {
        final String mName = "PredDataValue::removeFromIndex(): ";

        super.removeFromIndex(DCID);

        if ( this.itsValue == null )
        {
            throw new SystemErrorException(mName + "itsValue is null?!?");
        }

        this.itsValue.removeFromIndex(DCID);

        return;

    } /* PredDataValue::removeFromIndex(DCID) */


    /**
     * replaceInIndex()
     *
     * Call the super, and then pass an update index for replacement message
     * down to the predicate.
     *
     *                                          JRM -- 2/20/08
     *
     * Changes:
     *
     *    - None.
     */

    @Override
    protected void replaceInIndex(DataValue old_dv,
                                  long DCID,
                                  boolean cascadeMveMod,
                                  boolean cascadeMveDel,
                                  long cascadeMveID,
                                  boolean cascadePveMod,
                                  boolean cascadePveDel,
                                  long cascadePveID)
        throws SystemErrorException
    {
        final String mName = "PredDataValue::replaceInIndex(): ";
        PredDataValue old_pdv;

        super.replaceInIndex(old_dv,
                             DCID,
                             cascadeMveMod,
                             cascadeMveDel,
                             cascadeMveID,
                             cascadePveMod,
                             cascadePveDel,
                             cascadePveID);

        if ( this.itsValue == null )
        {
            throw new SystemErrorException(mName + "itsValue is null?!?");
        }

        if ( ! ( old_dv instanceof PredDataValue ) )
        {
            throw new SystemErrorException(mName + "old_dv not a pred dv?!?");
        }

        old_pdv = (PredDataValue)old_dv;

        if ( old_pdv.itsValue == null )
        {
            throw new SystemErrorException(mName +
                    "old_pdv.itsValue is null?!?");
        }

        this.itsValue.updateIndexForReplacement(old_pdv.itsValue,
                                                DCID,
                                                cascadeMveMod,
                                                cascadeMveDel,
                                                cascadeMveID,
                                                cascadePveMod,
                                                cascadePveDel,
                                                cascadePveID);

        return;

    } /* PredDataValue::replaceInIndex() */


    /**
     * toString()
     *
     * Returns a String representation of the DBValue for display.
     *
     *                                  JRM -- 8/15/07
     *
     * @return the string value.
     *
     * Changes:
     *
     *     - None.
     */

    public String toString()
    {
        if ( this.itsValue == null )
        {
            return("()");
        }
        else
        {
            return (this.itsValue.toString());
        }
    }


    /**
     * toDBString()
     *
     * Returns a database String representation of the DBValue for comparison
     * against the database's expected value.<br>
     * <i>This function is intended for debugging purposses.</i>
     *
     *                                      JRM -- 8/15/07
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     */

    public String toDBString()
    {
        final String mName = "PredDataValue::toDBString(): ";

        if ( ( this.itsValue == null ) ||
             ( this.itsValue.getPveID() == DBIndex.INVALID_ID ) )
        {
            return ("(PredDataValue (id " + this.id +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue ()" +
                    ") (subRange " + this.subRange + "))");
        }
        else
        {
            return ("(PredDataValue (id " + this.id +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + this.itsValue.toDBString() +
                    ") (subRange " + this.subRange + "))");
        }

    } /* PredDataValue::toDBString() */


    /**
     * updateForMVEDefChange()
     *
     * Scan the list of data values in the matrix, and pass an update for
     * matrix vocab element definition change message to the predicate
     * (if defined).
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
        final String mName = "PredDataValue::updateForMVEDefChange(): ";
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


        if ( this.itsValue != null )
        {
            this.itsValue.updateForMVEDefChange(db,
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

        return;

    } /* PredDataValue::updateForMVEDefChange() */


    /**
     * updateForMVEDeletion()
     *
     * Scan the list of data values in the matrix, and pass an update for
     * matrix vocab element definition change message to any column predicate
     * or predicate data values.
     *                                          JRM -- 8/30/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForMVEDeletion(Database db,
                                        long mveID)
        throws SystemErrorException
    {
        final String mName = "Matrix::updateForMVEDeletion(): ";

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( mveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "mveID invalid.");
        }

        if ( this.itsValue != null )
        {
            this.itsValue.updateForMVEDeletion(db, mveID);
        }

        return;

    } /* PredDataValue::updateForPVEDeletion() */


    /**
     * updateForPVEDefChange()
     *
     * Scan the list of data values in the matrix, and pass an update for
     * predicate vocab element definition change message to the predicate
     * (if defined).
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
        final String mName = "PredDataValue::updateForPVEDefChange(): ";
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


        if ( this.itsValue != null )
        {
            this.itsValue.updateForPVEDefChange(db,
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

        return;

    } /* PredDataValue::updateForPVEDefChange() */


    /**
     * updateForPVEDeletion()
     *
     * Scan the list of data values in the matrix, and pass an update for
     * predicate vocab element definition change message to any predicate
     * data values.
     *                                          JRM -- 3/23/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForPVEDeletion(Database db,
                                        long pveID)
        throws SystemErrorException
    {
        final String mName = "Matrix::updateForPVEDeletion(): ";

        if ( this.db != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( pveID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "pveID invalid.");
        }

        if ( this.itsValue != null )
        {
            this.itsValue.updateForPVEDeletion(db, pveID);
        }

        return;

    } /* PredDataValue::updateForPVEDeletion() */


    /**
     * updateForFargChange()
     *
     * Update for a change in the formal argument name, and/or subrange.
     *
     *                                          JRM -- 3/22/08
     *
     * Changes:
     *
     *    - None.
     */

    public void updateForFargChange(boolean fargNameChanged,
                                    boolean fargSubRangeChanged,
                                    boolean fargRangeChanged,
                                    FormalArgument oldFA,
                                    FormalArgument newFA)
        throws SystemErrorException
    {
        final String mName = "PredDataValue::updateForFargChange(): ";

        if ( ( oldFA == null ) || ( newFA == null ) )
        {
            throw new SystemErrorException(mName +
                                           "null old and/or new FA on entry.");
        }

        if ( oldFA.getID() != newFA.getID() )
        {
            throw new SystemErrorException(mName + "old/new FA ID mismatch.");
        }

        if ( oldFA.getItsVocabElementID() != newFA.getItsVocabElementID() )
        {
            throw new SystemErrorException(mName + "old/new FA veID mismatch.");
        }

        if ( oldFA.getFargType() != newFA.getFargType() )
        {
            throw new SystemErrorException(mName + "old/new FA type mismatch.");
        }

        if ( this.itsFargID != newFA.getID() )
        {
            throw new SystemErrorException(mName + "FA/DV faID mismatch.");
        }

        if ( this.itsFargType != newFA.getFargType() )
        {
            throw new SystemErrorException(mName + "FA/DV FA type mismatch.");
        }

        if ( ( fargSubRangeChanged ) || ( fargRangeChanged ) )
        {
            this.updateSubRange(newFA);
        }

        return;

    } /* PredDataValue::updateForFargChange() */


    /**
     * updateSubRange()
     *
     * Determine if the formal argument associated with the data value is
     * subranged, and if it is, updates the data values representation of
     * the subrange (if any) accordingly.  In passing, coerce the value of
     * the datavalue into the subrange if necessary.  For now, we simply
     * coerce to the empty predicate if subrange is true and the current
     * value isn't in the list of approved predicates.
     *
     * The fa argument is a reference to the current representation of the
     * formal argument associated with the data value.
     *
     *                                          JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateSubRange(FormalArgument fa)
        throws SystemErrorException
    {
        final String mName = "PredDataValue::updateSubRange(): ";

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");
        }

        if ( fa instanceof PredFormalArg )
        {
            PredFormalArg pfa = (PredFormalArg)fa;

            this.subRange = pfa.getSubRange();

            if ( this.subRange )
            {
                if ( this.itsValue.getPveID() != DBIndex.INVALID_ID )
                {
                    if ( ! ( pfa.isValidValue(this.itsValue) ) )
                    {
                        this.itsValue = new Predicate(this.db);
                    }
                }
            }
        }
        else if ( fa instanceof UnTypedFormalArg )
        {
            this.subRange = false;
        }
        else
        {
            throw new SystemErrorException(mName + "Unexpected fa type");
        }

        return;

    } /* PredDataValue::updateSubRange() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/

    /**
     * coerceToRange()
     *
     * If the supplied value is in range for the associated formal argument,
     * simply return it.  Otherwise, coerce it to the nearest value that is
     * in range.
     *                                              JRM -- 070815
     *
     * Changes:
     *
     *    - None.
     */

    public Predicate coerceToRange(Predicate value)
        throws SystemErrorException
    {
        final String mName = "PredDataValue::coerceToRange(): ";
        DBElement dbe;
        PredFormalArg pfa;
        Predicate retVal;

        if ( value == null )
        {
            retVal = new Predicate(this.db);
        }
        else if ( value.getPveID() == DBIndex.INVALID_ID )
        {
            retVal = value;
        }
        else if ( this.subRange )
        {
            if ( this.itsFargID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName +
                                      "subRange && (itsFargID == INVALID_ID)");
            }
            else if ( itsFargType != FormalArgument.fArgType.PREDICATE )
            {
                throw new SystemErrorException(mName +
                                               "itsFargType != PREDICATE");
            }

            dbe = this.db.idx.getElement(this.itsFargID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName +
                                               "itsFargID has no referent");
            }

            if ( ! ( dbe instanceof PredFormalArg ) )
            {
                throw new SystemErrorException(mName +
                                      "itsFargID doesn't refer to a predicate");
            }

            pfa = (PredFormalArg)dbe;

            if ( pfa.isValidValue(value) )
            {
                retVal = value;
            }
            else // coerce to the undefined state
            {
                retVal = new Predicate(this.db);
            }
        }
        else
        {
            retVal = value;
        }

        return retVal;

    } /* PredDataValue::coerceToRange() */


    /**
     * deregisterPreds()
     *
     * If this.itsValue isn't null, call its deregisterWithPve() method to
     * cause it to deregister with its associated PVE (if any), and pass
     * the message down to any column predicates or predicates that may
     * appear in its argument list.
     *                                              JRM -- 3/24/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregisterPreds(boolean cascadeMveDel,
                                   long cascadeMveID,
                                   boolean cascadePveDel,
                                   long cascadePveID)
        throws SystemErrorException
    {
        if ( this.itsValue != null )
        {
            this.itsValue.deregisterWithPve(cascadeMveDel,
                                            cascadeMveID,
                                            cascadePveDel,
                                            cascadePveID);
        }

        return;

    } /* PredDataValue::deregisterPreds() */


    /**
     * registerPreds()
     *
     * If this.itsValue isn't null, call its registerWithPve() method to
     * cause it to register with its associated PVE (if any), and pass
     * the message down to any predicates that may appear in its argument
     * list.
     *                                              JRM -- 3/24/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void registerPreds()
        throws SystemErrorException
    {
        if ( this.itsValue != null )
        {
            this.itsValue.registerWithPve();
        }

        return;

    } /* PredDataValue::registerPreds() */


    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * PredDataValuesAreLogicallyEqual()
     *
     * Given two instances of PredDataValue, return true if they contain
     * identical data, and false otherwise.
     *
     * Note that this method does only tests specific to this subclass of
     * DataValue -- the presumption is that this method has been called by
     * DataValue.DataValuesAreLogicallyEqual() which has already done all
     * generic tests.
     *                                              JRM -- 2/7/08
     *
     * Changes:
     *
     *    - None.
     */

    protected static boolean PredDataValuesAreLogicallyEqual(PredDataValue pdv0,
                                                             PredDataValue pdv1)
        throws SystemErrorException
    {
        final String mName = "PredDataValue::PredDataValuesAreLogicallyEqual()";
        boolean dataValuesAreEqual = true;

        if ( ( pdv0 == null ) || ( pdv1 == null ) )
        {
            throw new SystemErrorException(mName +
                                           ": pdv0 or pdv1 null on entry.");
        }
        else if ( ( pdv0.itsValue == null ) || ( pdv1.itsValue == null ) )
        {
            throw new SystemErrorException(mName +
                    ": pdv0.itsValue or pdv1.itsValue null on entry.");
        }

        if ( pdv0 != pdv1 )
        {
            if ( ( pdv0.itsValue != pdv1.itsValue ) &&
                 ( ! Predicate.PredicatesAreLogicallyEqual(pdv0.itsValue,
                                                           pdv1.itsValue) ) )
            {
                dataValuesAreEqual = false;
            }
        }

        return dataValuesAreEqual;

    } /* PredDataValue::PredDataValuesAreLogicallyEqual() */

    /** Seed value for generating hash codes. */
    private final static int SEED1 = 3;

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += (this.itsValue == null ? 0 : this.itsValue.hashCode()) * SEED1;

        return hash;
    }

    /**
     * Compares this PredDataValue against another object.
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
        // Must be this class to be here
        PredDataValue p = (PredDataValue) obj;
        return (itsValue == p.itsValue
                           || (itsValue != null && itsValue.equals(p.itsValue)))
                           && super.equals(obj);
    }


    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Construct an instance of PredDataValue with the specified initialization.
     *
     * Returns a reference to the newly constructed PredDataValue if successful.
     * Throws a system error exception on failure.
     *
     *                                              JRM -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static PredDataValue Construct(Database db,
                                          Predicate p)
        throws SystemErrorException
    {
        final String mName = "PredDataValue::Construct(db, p)";
        PredDataValue pdv = null;

        pdv = new PredDataValue(db);

        if ( p != null )
        {
            pdv.setItsValue(p);
        }

        return pdv;

    } /* PredDataValue::Construct(db, p) */


    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) One argument constructor:
     *
     *      a) Construct a database.  Using this database, call the one
     *         argument constructor for PredDataValue.  Verify that all
     *         fields are set to the expected defaults.
     *
     *      b) Verify that the one argument constructor fails on invalid
     *         input.  Given the compiler checks, this probably just means
     *         verifying that the constructor fails on null.
     *
     * 2) Two argument constructor:
     *
     *      a) Construct a database, and a mve (matrix vocab element) with one
     *         formal argument.  Insert the mve into the database, and make
     *         note of the IDs assigned to them (including the formal argument).
     *
     *         Construct a PredDataValue for the formal argument of the mve
     *         by passing a reference to the database and the id of the formal
     *         argument.  Verify that the PredDataValue's itsFargID,
     *         itsFargType, and subRange fields match those of the formal
     *         argument, and that all other fields are set to the expected
     *         defaults.
     *
     *         Repeat for a variety of formal argument types and settings.
     *
     *      b) Verify that the constructor fails when passed and invalid
     *         db or an invalid mve id.
     *
     * 3) Three argument constructor:
     *
     *      As per two argument constructor, save that a value is supplied
     *      to the constructor.  Verify that this value appears in the
     *      PredDataValue -- perhaps after having been modified to match
     *      the subrange.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and possibly a mve (matrix vocab element)
     *         and such formal arguments as are necessary.  If an mve is
     *         created, insert it into the database, and make note of the IDs
     *         assigned.  Then create a PredDataValue (possibly using
     *         the using a formal argument ID).
     *
     *         Now use the copy constructor to create a copy of the
     *         PredDataValue, and verify that the copy is correct.
     *
     *         Repeat the test for a variety of instances of FloatFormalArg.
     *
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad
     *         value that has to be tested.
     *
     * 5) Accessors:
     *
     *      Verify that the getItsValue(), setItsValue() and coerceToRange()
     *      methods perform correctly.  Verify that the inherited accessors
     *      function correctly via calls to the DataValue.TestAccessors()
     *      method.
     *
     *      Verify that setItsValue() and coerceToRange() fail on invalid
     *      input.
     *
     * 6) toString methods:
     *
     *      Verify that all fields are displayed correctly by the toString
     *      and toDBString() methods.
     *
     *
     *************************************************************************/

    /**
     * TestClassPredDataValue()
     *
     * Main routine for tests of class PredDataValue.
     *
     *                                      JRM -- 11/23/07
     *
     * Changes:
     *
     *    - Non.
     */

    public static boolean TestClassPredDataValue(java.io.PrintStream outStream,
                                                 boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;

        outStream.print("Testing class PredDataValue:\n");

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

        if ( ! TestToStringMethods(outStream, verbose) )
        {
            failures++;
        }

        if ( failures > 0 )
        {
            pass = false;
            outStream.printf(
                    "%d failures in tests for class PredDataValue.\n\n",
                    failures);
        }
        else
        {
            outStream.print("All tests passed for class PredDataValue.\n\n");
        }

        return pass;

    } /* PredDataValue::TestClassPredDataValue() */


    /**
     * Test1ArgConstructor()
     *
     * Run a battery of tests on the one argument constructor for this
     * class, and on the instance returned.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test1ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing 1 argument constructor for class PredDataValue           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        PredDataValue pdv = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        pdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();
            pdv = new PredDataValue(db);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( pdv == null ) ||
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

                if ( pdv == null )
                {
                    outStream.print(
                            "new PredDataValue(db) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "new PredDataValue(db) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new PredDataValue(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.Verify1ArgInitialization(db, pdv, outStream,
                                                           verbose);

            if ( pdv.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv.itsValue is null.\n");
                }
            }
            else if ( pdv.itsValue.getPveID() != DBIndex.INVALID_ID )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "pdv.itsValue.getPredID() != INVALID_ID.\n");
                }
            }
       }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue((Database)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new PredDataValue(null) returned.\n");
                    }

                    if ( pdv != null )
                    {
                        outStream.print(
                                "new PredDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(null) failed to throw " +
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

    } /* PredDataValue::Test1ArgConstructor() */


    /**
     * Test2ArgConstructor()
     *
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instance returned.
     *
     *                                              JRM -- 11/13/07
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
            "Testing 2 argument constructor for class PredDataValue           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        long pve4ID = DBIndex.INVALID_ID;
        long pve5ID = DBIndex.INVALID_ID;
        long pve6ID = DBIndex.INVALID_ID;
        long pve7ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        PredicateVocabElement pve4 = null;
        PredicateVocabElement pve5 = null;
        PredicateVocabElement pve6 = null;
        PredicateVocabElement pve7 = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement pred_mve_sr = null;
        FormalArgument farg = null;
        PredFormalArg pfa = null;
        PredFormalArg pfa_sr = null;
        PredDataValue pdv = null;
        PredDataValue pdv_sr = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database, and pve's
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
            farg = new UnTypedFormalArg(db, "<arg>");
            pve5.appendFormalArg(farg);

            pve5ID = db.addPredVE(pve5);

            // get a copy of the databases version of pve5 with ids assigned
            pve5 = db.getPredVE(pve5ID);


            pve6 = new PredicateVocabElement(db, "pve6");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve6.appendFormalArg(farg);

            pve6ID = db.addPredVE(pve6);

            // get a copy of the databases version of pve6 with ids assigned
            pve6 = db.getPredVE(pve6ID);


            pve7 = new PredicateVocabElement(db, "pve7");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve7.appendFormalArg(farg);

            pve7ID = db.addPredVE(pve7);

            // get a copy of the databases version of pve7 with ids assigned
            pve7 = db.getPredVE(pve7ID);


            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
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
                    outStream.print("creation of pve4 failed.\n");
                }

                if ( pve4ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve4ID not initialized.\n");
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

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete (1).\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            systemErrorExceptionString);
                }
            }
        }


        /* Now allocate mve's & PredFormalArg's with and without subranges.
         * Use the ID's of these PredFormalArgs to test the two argument
         * constructor.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pred_mve = new MatrixVocabElement(db, "pred_mve");
                pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa = new PredFormalArg(db);
                pred_mve.appendFormalArg(pfa);
                db.vl.addElement(pred_mve);

                pdv = new PredDataValue(db, pfa.getID());

                pred_mve_sr = new MatrixVocabElement(db, "pred_mve_sr");
                pred_mve_sr.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa_sr = new PredFormalArg(db);
                pfa_sr.setSubRange(true);
                pfa_sr.addApproved(pve0ID);
                pfa_sr.addApproved(pve2ID);
                pfa_sr.addApproved(pve4ID);
                pfa_sr.addApproved(pve6ID);
                pred_mve_sr.appendFormalArg(pfa_sr);
                db.vl.addElement(pred_mve_sr);

                pdv_sr = new PredDataValue(db, pfa_sr.getID());

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( db == null ) ||
                 ( pred_mve == null ) ||
                 ( pfa == null ) ||
                 ( pdv == null ) ||
                 ( pred_mve_sr == null ) ||
                 ( pfa_sr == null ) ||
                 ( pdv_sr == null ) ||
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

                    if ( pred_mve == null )
                    {
                        outStream.print("allocation of pred_mve failed.\n");
                    }

                    if ( pfa == null )
                    {
                        outStream.print("allocation of pfa failed.");
                    }

                    if ( pdv == null )
                    {
                        outStream.print(
                            "new PredDataValue(db, pfa.getID()) returned null.\n");
                    }

                    if ( pred_mve_sr == null )
                    {
                        outStream.print("allocation of pred_mve_sr failed.\n");
                    }

                    if ( pfa_sr == null )
                    {
                        outStream.print("allocation of pfa_sr failed.");
                    }

                    if ( pdv_sr == null )
                    {
                        outStream.print("new PredDataValue(db, pfa_sr.getID()) " +
                                        "returned null.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("Test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Test threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               pfa,
                                                               pdv,
                                                               outStream,
                                                               verbose,
                                                              "pdv");

            if ( pdv.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv.itsValue == null.\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(new Predicate(db),
                                                          pdv.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)",
                                                          "pdv.itsValue");
            }

            if ( pdv.subRange != pfa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "pdv.subRange doesn't match pfa.getSubRange().\n");
                }
            }

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               pfa_sr,
                                                               pdv_sr,
                                                               outStream,
                                                               verbose,
                                                               "pdv_sr");

            if ( pdv_sr.subRange != pfa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv_sr.subRange doesn't match " +
                                     "pfa_sr.getSubRange().\n");
                }
            }

            if ( pdv_sr.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv_sr.itsValue == null.\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(new Predicate(db),
                                                          pdv_sr.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)",
                                                          "pdv_sr.itsValue");
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue((Database)null, pfa.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new PredDataValue(null, " +
                                        "pfa.getID()) returned.\n");
                    }

                    if ( pdv != null )
                    {
                        outStream.print("new PredDataValue(null, " +
                                        "pfa.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(null, pfa.getID())" +
                                " failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue(db, DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "INVALID_ID) returned.\n");
                    }

                    if ( pdv != null )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "INVALID_ID) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(db, INVALID_ID)" +
                                " failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an ID that does not
         *refer to a formal argument.
         */
        if ( failures == 0 )
        {
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue(db, pred_mve.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "pred_mve.getID()) returned.\n");
                    }

                    if ( pdv != null )
                    {
                        outStream.print("new PredDataValue(db, " +
                                "pred_mve.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new PredDataValue(db, pred_mve.getID()) " +
                                "failed to throw a system error exception.\n");
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

    } /* PredDataValue::Test2ArgConstructor() */


    /**
     * Test3ArgConstructor()
     *
     * Run a battery of tests on the three argument constructor for this
     * class, and on the instances returned.
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
            "Testing 3 argument constructor for class PredDataValue           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        long pve4ID = DBIndex.INVALID_ID;
        long pve5ID = DBIndex.INVALID_ID;
        long pve6ID = DBIndex.INVALID_ID;
        long pve7ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        PredicateVocabElement pve4 = null;
        PredicateVocabElement pve5 = null;
        PredicateVocabElement pve6 = null;
        PredicateVocabElement pve7 = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement pred_mve_sr = null;
        FormalArgument farg = null;
        PredFormalArg pfa = null;
        PredFormalArg pfa_sr = null;
        PredDataValue pdv = null;
        PredDataValue pdv_sr0 = null;
        PredDataValue pdv_sr1 = null;
        Predicate p0 = null;
        Predicate p1 = null;
        Predicate p2 = null;
        Predicate p3 = null;
        Predicate p4 = null;
        Predicate p5 = null;
        Predicate p6 = null;
        Predicate p7 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }


        // Start by setting up the needed database, pve's, and preds
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

            p0 = new Predicate(db, pve0ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);

            p1 = new Predicate(db, pve1ID);


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

            p2 = new Predicate(db, pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve3.appendFormalArg(farg);
            pve3.setVarLen(true);

            pve3ID = db.addPredVE(pve3);

            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);

            p3 = new Predicate(db, pve3ID);


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

            p4 = new Predicate(db, pve4ID);


            pve5 = new PredicateVocabElement(db, "pve5");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve5.appendFormalArg(farg);

            pve5ID = db.addPredVE(pve5);

            // get a copy of the databases version of pve5 with ids assigned
            pve5 = db.getPredVE(pve5ID);

            p5 = new Predicate(db, pve5ID);


            pve6 = new PredicateVocabElement(db, "pve6");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve6.appendFormalArg(farg);

            pve6ID = db.addPredVE(pve6);

            // get a copy of the databases version of pve6 with ids assigned
            pve6 = db.getPredVE(pve6ID);

            p6 = new Predicate(db, pve6ID);


            pve7 = new PredicateVocabElement(db, "pve7");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve7.appendFormalArg(farg);

            pve7ID = db.addPredVE(pve7);

            // get a copy of the databases version of pve7 with ids assigned
            pve7 = db.getPredVE(pve7ID);

            p7 = new Predicate(db, pve7ID);


            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( p0 == null ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
             ( p1 == null ) ||
             ( pve2 == null ) ||
             ( pve2ID == DBIndex.INVALID_ID ) ||
             ( p2 == null ) ||
             ( pve3 == null ) ||
             ( pve3ID == DBIndex.INVALID_ID ) ||
             ( p3 == null ) ||
             ( pve4 == null ) ||
             ( pve4ID == DBIndex.INVALID_ID ) ||
             ( p4 == null ) ||
             ( pve5 == null ) ||
             ( pve5ID == DBIndex.INVALID_ID ) ||
             ( p5 == null ) ||
             ( pve6 == null ) ||
             ( pve6ID == DBIndex.INVALID_ID ) ||
             ( p6 == null ) ||
             ( pve7 == null ) ||
             ( pve7ID == DBIndex.INVALID_ID ) ||
             ( p7 == null ) ||
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

                if ( p0 == null )
                {
                    outStream.print("creation of p0 failed.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( p1 == null )
                {
                    outStream.print("creation of p1 failed.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve2ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve2ID not initialized.\n");
                }

                if ( p2 == null )
                {
                    outStream.print("creation of p2 failed.\n");
                }

                if ( pve3 == null )
                {
                    outStream.print("creation of pve3 failed.\n");
                }

                if ( pve3ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve3ID not initialized.\n");
                }

                if ( p3 == null )
                {
                    outStream.print("creation of p3 failed.\n");
                }

                if ( pve4 == null )
                {
                    outStream.print("creation of pve4 failed.\n");
                }

                if ( pve4ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve4ID not initialized.\n");
                }

                if ( p4 == null )
                {
                    outStream.print("creation of p4 failed.\n");
                }

                if ( pve5 == null )
                {
                    outStream.print("creation of pve5 failed.\n");
                }

                if ( pve5ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve5ID not initialized.\n");
                }

                if ( p5 == null )
                {
                    outStream.print("creation of p5 failed.\n");
                }

                if ( pve6 == null )
                {
                    outStream.print("creation of pve6 failed.\n");
                }

                if ( pve6ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve6ID not initialized.\n");
                }

                if ( p6 == null )
                {
                    outStream.print("creation of p6 failed.\n");
                }

                if ( pve7 == null )
                {
                    outStream.print("creation of pve7 failed.\n");
                }

                if ( pve7ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve7ID not initialized.\n");
                }

                if ( p7 == null )
                {
                    outStream.print("creation of p7 failed.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete (1).\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
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
                pred_mve = new MatrixVocabElement(db, "pred_mve");
                pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa = new PredFormalArg(db);
                pred_mve.appendFormalArg(pfa);
                db.vl.addElement(pred_mve);

                pdv = new PredDataValue(db, pfa.getID(), p0);

                pred_mve_sr = new MatrixVocabElement(db, "pred_mve_sr");
                pred_mve_sr.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa_sr = new PredFormalArg(db);
                pfa_sr.setSubRange(true);
                pfa_sr.addApproved(pve0ID);
                pfa_sr.addApproved(pve2ID);
                pfa_sr.addApproved(pve4ID);
                pfa_sr.addApproved(pve6ID);
                pred_mve_sr.appendFormalArg(pfa_sr);
                db.vl.addElement(pred_mve_sr);

                pdv_sr0 = new PredDataValue(db, pfa_sr.getID(), p2);
                pdv_sr1 = new PredDataValue(db, pfa_sr.getID(), p3);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( db == null ) ||
                 ( pred_mve == null ) ||
                 ( pfa == null ) ||
                 ( pdv == null ) ||
                 ( pred_mve_sr == null ) ||
                 ( pfa_sr == null ) ||
                 ( pdv_sr0 == null ) ||
                 ( pdv_sr1 == null ) ||
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

                    if ( pred_mve == null )
                    {
                        outStream.print("allocation of pred_mve failed.\n");
                    }

                    if ( pfa == null )
                    {
                        outStream.print("allocation of pfa failed.");
                    }

                    if ( pdv == null )
                    {
                        outStream.print("allocation of pdv failed.\n");
                    }

                    if ( pred_mve_sr == null )
                    {
                        outStream.print("allocation of pred_mve_sr failed.\n");
                    }

                    if ( pfa_sr == null )
                    {
                        outStream.print("allocation of pfa_sr failed.");
                    }

                    if ( pdv_sr0 == null )
                    {
                        outStream.print("allocation of pdv_sr0 failed.\n");
                    }

                    if ( pdv_sr1 == null )
                    {
                        outStream.print("allocation of pdv_sr1 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("Test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Test threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               pfa,
                                                               pdv,
                                                               outStream,
                                                               verbose,
                                                               "pdv");

            if ( pdv.subRange != pfa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "pdv.subRange doesn't match pfa.getSubRange().\n");
                }
            }

            if ( pdv.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv.itsValue null.\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(p0,
                                                          pdv.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p0",
                                                          "pdv.itsValue");
            }

            /**********************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               pfa_sr,
                                                               pdv_sr0,
                                                               outStream,
                                                               verbose,
                                                               "pdv_sr0");

            if ( pdv_sr0.subRange != pfa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv_sr0.subRange doesn't match " +
                                     "pfa_sr.getSubRange().\n");
                }
            }

            if ( pdv_sr0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv_sr0.itsValue == null.\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(p2,
                                                          pdv_sr0.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p2",
                                                          "pdv_sr0.itsValue");
            }

            /***************************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               pfa_sr,
                                                               pdv_sr1,
                                                               outStream,
                                                               verbose,
                                                               "pdv_sr1");

            if ( pdv_sr1.subRange != pfa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv_sr0.subRange doesn't match " +
                                     "pfa_sr.getSubRange().\n");
                }
            }

            if ( pdv_sr1.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv_sr1.itsValue == null.\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(new Predicate(db),
                                                          pdv_sr1.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)",
                                                          "pdv_sr1.itsValue");
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue((Database)null, pfa.getID(), p4);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pdv != null )
                    {
                        outStream.print("new PredDataValue(null, " +
                                "pfa.getID(), p4) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new PredDataValue(null, " +
                                        "pfa.getID(), p4) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(null, pfa.getID()," +
                                       " p4) failed to throw a system error " +
                                       "exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue(db, DBIndex.INVALID_ID, p5);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pdv != null )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "INVALID_ID, p5) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "INVALID_ID, p5) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(db, INVALID_ID, " +
                                        "p5) failed to throw a system error " +
                                        "exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an ID that does not
         * refer to a formal argument.
         */
        if ( failures == 0 )
        {
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue(db, pred_mve.getID(), p6);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "pred_mve.getID(), p6) returned.\n");
                    }

                    if ( pdv != null )
                    {
                        outStream.print("new PredDataValue(db, " +
                                "pred_mve.getID(), p6) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "pred_mve.getID(), p6) failed to " +
                                        "throw a system error exception.\n");
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

    } /* PredDataValue::Test3ArgConstructor() */


    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors supported by this class.
     *
     *                                              JRM -- 11/13/07
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
            "Testing class PredDataValue accessors                            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        long pve4ID = DBIndex.INVALID_ID;
        long pve5ID = DBIndex.INVALID_ID;
        long pve6ID = DBIndex.INVALID_ID;
        long pve7ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        PredicateVocabElement pve4 = null;
        PredicateVocabElement pve5 = null;
        PredicateVocabElement pve6 = null;
        PredicateVocabElement pve7 = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement matrix_mve = null;
        FormalArgument farg = null;
        PredFormalArg pfa = null;
        UnTypedFormalArg ufa = null;
        PredDataValue pdv0 = null;
        PredDataValue pdv1 = null;
        PredDataValue pdv2 = null;
        Predicate p0 = null;
        Predicate p1 = null;
        Predicate p2 = null;
        Predicate p3 = null;
        Predicate p4 = null;
        Predicate p5 = null;
        Predicate p6 = null;
        Predicate p7 = null;
        Database alt_db = null;
        long alt_pve0ID = DBIndex.INVALID_ID;
        long alt_pve1ID = DBIndex.INVALID_ID;
        PredicateVocabElement alt_pve0 = null;
        PredicateVocabElement alt_pve1 = null;
        MatrixVocabElement alt_mve = null;
        PredFormalArg alt_pfa = null;
        UnTypedFormalArg alt_ufa = null;
        Predicate alt_p0 = null;
        Predicate alt_p1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database, pve's, and preds
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

            p0 = new Predicate(db, pve0ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);

            p1 = new Predicate(db, pve1ID);


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

            p2 = new Predicate(db, pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve3.appendFormalArg(farg);
            pve3.setVarLen(true);

            pve3ID = db.addPredVE(pve3);

            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);

            p3 = new Predicate(db, pve3ID);


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

            p4 = new Predicate(db, pve4ID);


            pve5 = new PredicateVocabElement(db, "pve5");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve5.appendFormalArg(farg);

            pve5ID = db.addPredVE(pve5);

            // get a copy of the databases version of pve5 with ids assigned
            pve5 = db.getPredVE(pve5ID);

            p5 = new Predicate(db, pve5ID);


            pve6 = new PredicateVocabElement(db, "pve6");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve6.appendFormalArg(farg);

            pve6ID = db.addPredVE(pve6);

            // get a copy of the databases version of pve6 with ids assigned
            pve6 = db.getPredVE(pve6ID);

            p6 = new Predicate(db, pve6ID);


            pve7 = new PredicateVocabElement(db, "pve7");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve7.appendFormalArg(farg);

            pve7ID = db.addPredVE(pve7);

            // get a copy of the databases version of pve7 with ids assigned
            pve7 = db.getPredVE(pve7ID);

            p7 = new Predicate(db, pve7ID);


            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( p0 == null ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
             ( p1 == null ) ||
             ( pve2 == null ) ||
             ( pve2ID == DBIndex.INVALID_ID ) ||
             ( p2 == null ) ||
             ( pve3 == null ) ||
             ( pve3ID == DBIndex.INVALID_ID ) ||
             ( p3 == null ) ||
             ( pve4 == null ) ||
             ( pve4ID == DBIndex.INVALID_ID ) ||
             ( p4 == null ) ||
             ( pve5 == null ) ||
             ( pve5ID == DBIndex.INVALID_ID ) ||
             ( p5 == null ) ||
             ( pve6 == null ) ||
             ( pve6ID == DBIndex.INVALID_ID ) ||
             ( p6 == null ) ||
             ( pve7 == null ) ||
             ( pve7ID == DBIndex.INVALID_ID ) ||
             ( p7 == null ) ||
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

                if ( p0 == null )
                {
                    outStream.print("creation of p0 failed.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( p1 == null )
                {
                    outStream.print("creation of p1 failed.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve2ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve2ID not initialized.\n");
                }

                if ( p2 == null )
                {
                    outStream.print("creation of p2 failed.\n");
                }

                if ( pve3 == null )
                {
                    outStream.print("creation of pve3 failed.\n");
                }

                if ( pve3ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve3ID not initialized.\n");
                }

                if ( p3 == null )
                {
                    outStream.print("creation of p3 failed.\n");
                }

                if ( pve4 == null )
                {
                    outStream.print("creation of pve4 failed.\n");
                }

                if ( pve4ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve4ID not initialized.\n");
                }

                if ( p4 == null )
                {
                    outStream.print("creation of p4 failed.\n");
                }

                if ( pve5 == null )
                {
                    outStream.print("creation of pve5 failed.\n");
                }

                if ( pve5ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve5ID not initialized.\n");
                }

                if ( p5 == null )
                {
                    outStream.print("creation of p5 failed.\n");
                }

                if ( pve6 == null )
                {
                    outStream.print("creation of pve6 failed.\n");
                }

                if ( pve6ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve6ID not initialized.\n");
                }

                if ( p6 == null )
                {
                    outStream.print("creation of p6 failed.\n");
                }

                if ( pve7 == null )
                {
                    outStream.print("creation of pve7 failed.\n");
                }

                if ( pve7ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve7ID not initialized.\n");
                }

                if ( p7 == null )
                {
                    outStream.print("creation of p7 failed.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete (1).\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            systemErrorExceptionString);
                }
            }
        }

        /* now allocate test mve's, formal arguments, and data values for
         * the test proper.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pred_mve = new MatrixVocabElement(db, "pred_mve");
                pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa = new PredFormalArg(db);
                pfa.setSubRange(true);
                pfa.addApproved(pve0ID);
                pfa.addApproved(pve2ID);
                pfa.addApproved(pve4ID);
                pfa.addApproved(pve6ID);
                pred_mve.appendFormalArg(pfa);
                db.vl.addElement(pred_mve);

                pdv0 = new PredDataValue(db, pfa.getID(), p2);

                matrix_mve = new MatrixVocabElement(db, "matrix_mve");
                matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
                ufa = new UnTypedFormalArg(db, "<untyped>");
                matrix_mve.appendFormalArg(ufa);
                db.vl.addElement(matrix_mve);

                pdv1 = new PredDataValue(db, ufa.getID(), p5);
                pdv2 = new PredDataValue(db, ufa.getID(), p6);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( db == null ) ||
                 ( pred_mve == null ) ||
                 ( pfa == null ) ||
                 ( pdv0 == null ) ||
                 ( matrix_mve == null ) ||
                 ( ufa == null ) ||
                 ( pdv1 == null ) ||
                 ( pdv2 == null ) ||
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

                    if ( pred_mve == null )
                    {
                        outStream.print("allocation of pred_mve failed.\n");
                    }

                    if ( pfa == null )
                    {
                        outStream.print("allocation of pfa failed.\n");
                    }

                    if ( pdv0 == null )
                    {
                        outStream.print("allocation of pdv0 failed.\n");
                    }

                    if ( matrix_mve == null )
                    {
                        outStream.print("allocation of matrix_mve failed.\n");
                    }

                    if ( ufa == null )
                    {
                        outStream.print("allocation of ufa failed.\n");
                    }

                    if ( pdv1 == null )
                    {
                        outStream.print("allocation of pdv1 failed.\n");
                    }

                    if ( pdv2 == null )
                    {
                        outStream.print("allocation of pdv2 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("Test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Test setup threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.TestAccessors(db, pfa, matrix_mve, ufa,
                                                pdv0, outStream, verbose);

            if ( pdv0.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getSubRange() != false");
                }
            }

            if ( pdv0.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (1).\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(p2,
                                                          pdv0.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p2",
                                                          "pdv0.itsValue");
            }

            pdv0.setItsValue(null);

            if ( pdv0.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (2).\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(new Predicate(db),
                                                          pdv0.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)1",
                                                          "pdv0.itsValue");
            }

            pdv0.setItsValue(p3);

            if ( pdv0.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (3).\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(p3,
                                                          pdv0.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p3",
                                                          "pdv0.itsValue");
            }

            pdv0.setItsValue(new Predicate(db));

            if ( pdv0.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (4).\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(new Predicate(db),
                                                          pdv0.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)2",
                                                          "pdv0.itsValue");
            }

            /************************************/

            if ( pdv1.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv1.getSubRange() != false\n");
                }
            }

            if ( pdv1.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv1.getItsValue() == null (1).\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(p5,
                                                          pdv1.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p5",
                                                          "pdv1.itsValue");
            }

            failures += DataValue.TestAccessors(db, ufa, pred_mve, pfa,
                                                pdv1, outStream, verbose);

            if ( pdv1.getSubRange() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv1.getSubRange() != true\n");
                }
            }

            if ( pdv1.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv1.getItsValue() == null (2)\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(new Predicate(db),
                                                          pdv1.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)1",
                                                          "pdv1.itsValue");
            }

            pdv1.setItsValue(p4);

            if ( pdv1.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv1.getItsValue() == null (3).\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(p4,
                                                          pdv1.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p4",
                                                          "pdv1.itsValue");
            }

            pdv1.setItsValue(p7);

            if ( pdv1.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv1.getItsValue() == null (4)\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(new Predicate(db),
                                                          pdv1.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)2",
                                                          "pdv1.itsValue");
            }

            if ( ( pdv1.coerceToRange(p0) != p0 ) ||
                 ( pdv1.coerceToRange(p1) == null ) ||
                 ( pdv1.coerceToRange(p2) != p2 ) ||
                 ( pdv1.coerceToRange(p3) == null ) ||
                 ( pdv1.coerceToRange(p4) != p4 ) ||
                 ( pdv1.coerceToRange(p5) == null ) ||
                 ( pdv1.coerceToRange(p6) != p6 ) ||
                 ( pdv1.coerceToRange(p7) == null ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "unexpected results from pdv1.coerceToRange()\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(new Predicate(db),
                                                      pdv1.coerceToRange(p1),
                                                      outStream,
                                                      verbose,
                                                      "new Predicate(db)",
                                                      "pdv1.coerceToRange(p1)");
                failures += Predicate.VerifyPredicateCopy(new Predicate(db),
                                                      pdv1.coerceToRange(p3),
                                                      outStream,
                                                      verbose,
                                                      "new Predicate(db)",
                                                      "pdv1.coerceToRange(p3)");
                failures += Predicate.VerifyPredicateCopy(new Predicate(db),
                                                      pdv1.coerceToRange(p5),
                                                      outStream,
                                                      verbose,
                                                      "new Predicate(db)",
                                                      "pdv1.coerceToRange(p5)");
                failures += Predicate.VerifyPredicateCopy(new Predicate(db),
                                                      pdv1.coerceToRange(p7),
                                                      outStream,
                                                      verbose,
                                                      "new Predicate(db)",
                                                      "pdv1.coerceToRange(p7)");
            }

            /*********************************/

            failures += DataValue.TestAccessors(db, ufa, pred_mve, pfa,
                                                pdv2, outStream, verbose);

            if ( pdv2.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv2.getItsValue() == null(1).\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(p6,
                                                          pdv2.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p6",
                                                          "pdv2.itsValue");
            }

            pdv2.setItsValue(null);

            if ( pdv2.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (2).\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(new Predicate(db),
                                                          pdv2.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)1",
                                                          "pdv2.itsValue");
            }

            pdv2.setItsValue(p0);

            if ( pdv2.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (3).\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(p0,
                                                          pdv2.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p0",
                                                          "pdv2.itsValue");
            }

            pdv2.setItsValue(new Predicate(db));

            if ( pdv2.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (4).\n");
                }
            }
            else
            {
                failures += Predicate.VerifyPredicateCopy(new Predicate(db),
                                                          pdv2.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)2",
                                                          "pdv2.itsValue");
            }
        }

        /* For now at least, there is no real need to test setItsValue with
         * invalid values.  The compiler requires that the supplied parameter
         * is an instance of Predicate, and the value supplied (if not null or
         * an empty Predicate) is passed through to the target formal arguments
         * isValidValue routine.  Since we already have tests for these
         * routines, there is no need to test them here.
         *
         * That said, against changes in the code, it is probably worth while
         * to pass through an invalid predicate or two just to be sure.
         *
         * Start with setup for test:
         */

        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            completed = false;

            try
            {
                alt_db = new ODBCDatabase();

                alt_pve0 = new PredicateVocabElement(alt_db, "alt_pve0");
                farg = new UnTypedFormalArg(alt_db, "<arg1>");
                alt_pve0.appendFormalArg(farg);
                farg = new UnTypedFormalArg(alt_db, "<arg2>");
                alt_pve0.appendFormalArg(farg);

                alt_pve0ID = alt_db.addPredVE(alt_pve0);

                // get a copy alt_pve0 with ids assigned
                alt_pve0 = alt_db.getPredVE(alt_pve0ID);

                alt_p0 = new Predicate(alt_db, alt_pve0ID);


                alt_pve1 = new PredicateVocabElement(alt_db, "alt_pve1");
                farg = new IntFormalArg(alt_db, "<int>");
                alt_pve1.appendFormalArg(farg);
                farg = new UnTypedFormalArg(alt_db, "<arg2>");
                alt_pve1.appendFormalArg(farg);

                alt_pve1ID = alt_db.addPredVE(alt_pve1);

                // get a copy of pve1 with ids assigned
                alt_pve1 = alt_db.getPredVE(alt_pve1ID);

                alt_p1 = new Predicate(alt_db, alt_pve1ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( alt_db == null ) ||
                 ( alt_pve0 == null ) ||
                 ( alt_pve0ID == DBIndex.INVALID_ID ) ||
                 ( alt_p0 == null ) ||
                 ( alt_pve1 == null ) ||
                 ( alt_pve1ID == DBIndex.INVALID_ID ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( alt_db == null )
                    {
                        outStream.print("creation of alt_db failedl.\n");
                    }

                    if ( alt_pve0 == null )
                    {
                        outStream.print("creation of alt_pve0 failed.\n");
                    }

                    if ( alt_pve0ID == DBIndex.INVALID_ID )
                    {
                        outStream.print("alt_pve0ID not initialized.\n");
                    }

                    if ( alt_p0 == null )
                    {
                        outStream.print("creation of alt_p0 failed.\n");
                    }

                    if ( alt_pve1 == null )
                    {
                        outStream.print("creation of alt_pve1 failed.\n");
                    }

                    if ( alt_pve1ID == DBIndex.INVALID_ID )
                    {
                        outStream.print("alt_pve1ID not initialized.\n");
                    }

                    if ( alt_p1 == null )
                    {
                        outStream.print("creation of alt_p1 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete (1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("alt allocations threw a " +
                                "SystemErrorException: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            completed = false;

            try
            {
                pdv0.setItsValue(alt_p0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                                "pdv0.setItsValue(alt_p0) completed.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("pdv0.setItsValue(alt_p0) failed " +
                                         "to thow a system error.\n");
                    }
                }
            }


            threwSystemErrorException = false;
            completed = false;

            try
            {
                pdv1.setItsValue(alt_p1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
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
                                "pdv1.setItsValue(alt_p1) completed.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("pdv1.setItsValue(alt_p1) failed " +
                                         "to thow a system error.\n");
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

    } /* PredDataValue::TestAccessors() */


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
            "Testing copy constructor for class PredDataValue                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        long pve4ID = DBIndex.INVALID_ID;
        long pve5ID = DBIndex.INVALID_ID;
        long pve6ID = DBIndex.INVALID_ID;
        long pve7ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        PredicateVocabElement pve4 = null;
        PredicateVocabElement pve5 = null;
        PredicateVocabElement pve6 = null;
        PredicateVocabElement pve7 = null;
        MatrixVocabElement matrix_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement pred_mve_sr = null;
        FormalArgument farg = null;
        PredFormalArg pfa = null;
        UnTypedFormalArg ufa = null;
        PredFormalArg pfa_sr = null;
        PredDataValue pdv = null;
        PredDataValue pdv0 = null;
        PredDataValue pdv0_copy = null;
        PredDataValue pdv1 = null;
        PredDataValue pdv1_copy = null;
        PredDataValue pdv2 = null;
        PredDataValue pdv2_copy = null;
        PredDataValue pdv3 = null;
        PredDataValue pdv3_copy = null;
        PredDataValue pdv4 = null;
        PredDataValue pdv4_copy = null;
        PredDataValue pdv_sr0 = null;
        PredDataValue pdv_sr0_copy = null;
        PredDataValue pdv_sr1 = null;
        PredDataValue pdv_sr1_copy = null;
        Predicate p0 = null;
        Predicate p1 = null;
        Predicate p2 = null;
        Predicate p3 = null;
        Predicate p4 = null;
        Predicate p5 = null;
        Predicate p6 = null;
        Predicate p7 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }


        // Start by setting up the needed database, pve's, and preds
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

            p0 = new Predicate(db, pve0ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);

            p1 = new Predicate(db, pve1ID);


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

            p2 = new Predicate(db, pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve3.appendFormalArg(farg);
            pve3.setVarLen(true);

            pve3ID = db.addPredVE(pve3);

            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);

            p3 = new Predicate(db, pve3ID);


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

            p4 = new Predicate(db, pve4ID);


            pve5 = new PredicateVocabElement(db, "pve5");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve5.appendFormalArg(farg);

            pve5ID = db.addPredVE(pve5);

            // get a copy of the databases version of pve5 with ids assigned
            pve5 = db.getPredVE(pve5ID);

            p5 = new Predicate(db, pve5ID);


            pve6 = new PredicateVocabElement(db, "pve6");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve6.appendFormalArg(farg);

            pve6ID = db.addPredVE(pve6);

            // get a copy of the databases version of pve6 with ids assigned
            pve6 = db.getPredVE(pve6ID);

            p6 = new Predicate(db, pve6ID);


            pve7 = new PredicateVocabElement(db, "pve7");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve7.appendFormalArg(farg);

            pve7ID = db.addPredVE(pve7);

            // get a copy of the databases version of pve7 with ids assigned
            pve7 = db.getPredVE(pve7ID);

            p7 = new Predicate(db, pve7ID);


            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( p0 == null ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
             ( p1 == null ) ||
             ( pve2 == null ) ||
             ( pve2ID == DBIndex.INVALID_ID ) ||
             ( p2 == null ) ||
             ( pve3 == null ) ||
             ( pve3ID == DBIndex.INVALID_ID ) ||
             ( p3 == null ) ||
             ( pve4 == null ) ||
             ( pve4ID == DBIndex.INVALID_ID ) ||
             ( p4 == null ) ||
             ( pve5 == null ) ||
             ( pve5ID == DBIndex.INVALID_ID ) ||
             ( p5 == null ) ||
             ( pve6 == null ) ||
             ( pve6ID == DBIndex.INVALID_ID ) ||
             ( p6 == null ) ||
             ( pve7 == null ) ||
             ( pve7ID == DBIndex.INVALID_ID ) ||
             ( p7 == null ) ||
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

                if ( p0 == null )
                {
                    outStream.print("creation of p0 failed.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( p1 == null )
                {
                    outStream.print("creation of p1 failed.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve2ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve2ID not initialized.\n");
                }

                if ( p2 == null )
                {
                    outStream.print("creation of p2 failed.\n");
                }

                if ( pve3 == null )
                {
                    outStream.print("creation of pve3 failed.\n");
                }

                if ( pve3ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve3ID not initialized.\n");
                }

                if ( p3 == null )
                {
                    outStream.print("creation of p3 failed.\n");
                }

                if ( pve4 == null )
                {
                    outStream.print("creation of pve4 failed.\n");
                }

                if ( pve4ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve4ID not initialized.\n");
                }

                if ( p4 == null )
                {
                    outStream.print("creation of p4 failed.\n");
                }

                if ( pve5 == null )
                {
                    outStream.print("creation of pve5 failed.\n");
                }

                if ( pve5ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve5ID not initialized.\n");
                }

                if ( p5 == null )
                {
                    outStream.print("creation of p5 failed.\n");
                }

                if ( pve6 == null )
                {
                    outStream.print("creation of pve6 failed.\n");
                }

                if ( pve6ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve6ID not initialized.\n");
                }

                if ( p6 == null )
                {
                    outStream.print("creation of p6 failed.\n");
                }

                if ( pve7 == null )
                {
                    outStream.print("creation of pve7 failed.\n");
                }

                if ( pve7ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve7ID not initialized.\n");
                }

                if ( p7 == null )
                {
                    outStream.print("creation of p7 failed.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete (1).\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            systemErrorExceptionString);
                }
            }
        }

        /* Now create the instances of PredDataValue to be copied. */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            /* setup the base entries for the copy test */
            try
            {
                pdv0 = new PredDataValue(db);

                pred_mve = new MatrixVocabElement(db, "pred_mve");
                pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa = new PredFormalArg(db);
                pred_mve.appendFormalArg(pfa);
                db.vl.addElement(pred_mve);

                pdv1 = new PredDataValue(db, pfa.getID());
                pdv2 = new PredDataValue(db, pfa.getID(), p3);


                matrix_mve = new MatrixVocabElement(db, "matrix_mve");
                matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
                ufa = new UnTypedFormalArg(db);
                matrix_mve.appendFormalArg(ufa);
                db.vl.addElement(matrix_mve);

                pdv3 = new PredDataValue(db, ufa.getID());
                pdv4 = new PredDataValue(db, ufa.getID(), p4);


                pred_mve_sr = new MatrixVocabElement(db, "pred_mve_sr");
                pred_mve_sr.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa_sr = new PredFormalArg(db);
                pfa_sr.setSubRange(true);
                pfa_sr.addApproved(pve0ID);
                pfa_sr.addApproved(pve2ID);
                pfa_sr.addApproved(pve4ID);
                pfa_sr.addApproved(pve6ID);
                pred_mve_sr.appendFormalArg(pfa_sr);
                db.vl.addElement(pred_mve_sr);

                pdv_sr0 = new PredDataValue(db, pfa_sr.getID());
                pdv_sr1 = new PredDataValue(db, pfa_sr.getID(), p0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( db == null ) ||
                 ( pdv0 == null ) ||
                 ( pred_mve == null ) ||
                 ( pfa == null ) ||
                 ( pdv1 == null ) ||
                 ( pdv2 == null ) ||
                 ( matrix_mve == null ) ||
                 ( ufa == null ) ||
                 ( pdv3 == null ) ||
                 ( pdv4 == null ) ||
                 ( pred_mve_sr == null ) ||
                 ( pfa_sr == null ) ||
                 ( pdv_sr0 == null ) ||
                 ( pdv_sr1 == null ) ||
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

                    if ( pdv0 == null )
                    {
                        outStream.print("allocation of pdv0 failed\n");
                    }

                    if ( pred_mve == null )
                    {
                        outStream.print("allocation of pred_mve failed.\n");
                    }

                    if ( pfa == null )
                    {
                        outStream.print("allocation of pfa failed.");
                    }

                    if ( pdv1 == null )
                    {
                        outStream.print("allocation of pdv1 failed\n");
                    }

                    if ( pdv2 == null )
                    {
                        outStream.print("allocation of pdv2 failed\n");
                    }

                    if ( matrix_mve == null )
                    {
                        outStream.print("allocation of matrix_mve failed.\n");
                    }

                    if ( ufa == null )
                    {
                        outStream.print("allocation of ufa failed.");
                    }

                    if ( pdv3 == null )
                    {
                        outStream.print("allocation of pdv3 failed\n");
                    }

                    if ( pdv4 == null )
                    {
                        outStream.print("allocation of pdv4 failed\n");
                    }

                    if ( pred_mve_sr == null )
                    {
                        outStream.print("allocation of pred_mve_sr failed.\n");
                    }

                    if ( pfa_sr == null )
                    {
                        outStream.print("allocation of pfa_sr failed.");
                    }

                    if ( pdv_sr0 == null )
                    {
                        outStream.print("allocation of pdv_sr0 failed.\n");
                    }

                    if ( pdv_sr1 == null )
                    {
                        outStream.print("allocation of pdv_sr1 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("Test setup failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Test setup threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            /* setup the base entries for the copy test */
            try
            {
                pdv0_copy = new PredDataValue(pdv0);
                pdv1_copy = new PredDataValue(pdv1);
                pdv2_copy = new PredDataValue(pdv2);
                pdv3_copy = new PredDataValue(pdv3);
                pdv4_copy = new PredDataValue(pdv4);
                pdv_sr0_copy = new PredDataValue(pdv_sr0);
                pdv_sr1_copy = new PredDataValue(pdv_sr1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv0_copy == null ) ||
                 ( pdv1_copy == null ) ||
                 ( pdv2_copy == null ) ||
                 ( pdv3_copy == null ) ||
                 ( pdv4_copy == null ) ||
                 ( pdv_sr0_copy == null ) ||
                 ( pdv_sr1_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pdv0_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv0) returned null.\n");
                    }

                    if ( pdv1_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv1) returned null.\n");
                    }

                    if ( pdv2_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv2) returned null.\n");
                    }

                    if ( pdv3_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv3) returned null.\n");
                    }

                    if ( pdv4_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv4) returned null.\n");
                    }

                    if ( pdv_sr0_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv_sr0) returned null.\n");
                    }

                    if ( pdv_sr1_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv_sr1) returned null.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("Test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Test threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.VerifyDVCopy(pdv0, pdv0_copy, outStream,
                                               verbose, "pdv0", "pdv0_copy");

            failures += DataValue.VerifyDVCopy(pdv1, pdv1_copy, outStream,
                                               verbose, "pdv1", "pdv1_copy");

            failures += DataValue.VerifyDVCopy(pdv2, pdv2_copy, outStream,
                                               verbose, "pdv2", "pdv2_copy");

            failures += DataValue.VerifyDVCopy(pdv3, pdv3_copy, outStream,
                                               verbose, "pdv3", "pdv3_copy");

            failures += DataValue.VerifyDVCopy(pdv4, pdv4_copy, outStream,
                                               verbose, "pdv4", "pdv4_copy");

            failures += DataValue.VerifyDVCopy(pdv_sr0, pdv_sr0_copy, outStream,
                                            verbose, "pdv_sr0", "pdv_sr0_copy");

            failures += DataValue.VerifyDVCopy(pdv_sr1, pdv_sr1_copy, outStream,
                                            verbose, "pdv_sr1", "pdv_sr1_copy");
        }


        /* verify that the constructor fails when given an invalid dv */
        if ( failures == 0 )
        {
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue((PredDataValue)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "new PredDataValue(null) completed.\n");
                    }

                    if ( pdv != null )
                    {
                        outStream.print(
                            "new PredDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(null) failed " +
                                "to throw a system error exception.\n");
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

    } /* PredDataValue::TestCopyConstructor() */


    /**
     * TestToStringMethods()
     *
     * Run a battery of tests on the toString methods supported by
     * this class.
     *
     *                                              JRM -- 11/13/07
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
        String testString0 = "pve0(<arg1>, <arg2>)";
        String testDBString0 =
                "(PredDataValue (id 100) " +
                            "(itsFargID 14) " +
                            "(itsFargType PREDICATE) " +
                            "(itsCellID 500) " +
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
                            "(subRange true))";
        String testString1 = "pve3(<arg1>)";
        String testDBString1 =
                "(PredDataValue (id 101) " +
                            "(itsFargID 20) " +
                            "(itsFargType UNTYPED) " +
                            "(itsCellID 501) " +
                            "(itsValue " +
                                "(predicate (id 0) " +
                                    "(predID 11) " +
                                    "(predName pve3) " +
                                    "(varLen true) " +
                                    "(argList " +
                                        "((UndefinedDataValue (id 0) " +
                                            "(itsFargID 12) " +
                                            "(itsFargType UNTYPED) " +
                                            "(itsCellID 0) " +
                                            "(itsValue <arg1>) " +
                                            "(subRange false))))))) " +
                                    "(subRange false))";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement matrix_mve = null;
        FormalArgument farg = null;
        PredFormalArg pfa = null;
        UnTypedFormalArg ufa = null;
        PredDataValue pdv0 = null;
        PredDataValue pdv1 = null;
        Predicate p0 = null;
        Predicate p1 = null;
        Predicate p2 = null;
        Predicate p3 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database, pve's, and preds
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

            p0 = new Predicate(db, pve0ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);

            p1 = new Predicate(db, pve1ID);


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

            p2 = new Predicate(db, pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve3.appendFormalArg(farg);
            pve3.setVarLen(true);

            pve3ID = db.addPredVE(pve3);

            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);

            p3 = new Predicate(db, pve3ID);


            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( p0 == null ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
             ( p1 == null ) ||
             ( pve2 == null ) ||
             ( pve2ID == DBIndex.INVALID_ID ) ||
             ( p2 == null ) ||
             ( pve3 == null ) ||
             ( pve3ID == DBIndex.INVALID_ID ) ||
             ( p3 == null ) ||
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

                if ( p0 == null )
                {
                    outStream.print("creation of p0 failed.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( p1 == null )
                {
                    outStream.print("creation of p1 failed.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve2ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve2ID not initialized.\n");
                }

                if ( p2 == null )
                {
                    outStream.print("creation of p2 failed.\n");
                }

                if ( pve3 == null )
                {
                    outStream.print("creation of pve3 failed.\n");
                }

                if ( pve3ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve3ID not initialized.\n");
                }

                if ( p3 == null )
                {
                    outStream.print("creation of p3 failed.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete (1).\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
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
                pred_mve = new MatrixVocabElement(db, "pred_mve");
                pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa = new PredFormalArg(db);
                pfa.setSubRange(true);
                pfa.addApproved(pve0ID);
                pfa.addApproved(pve2ID);
                pred_mve.appendFormalArg(pfa);
                db.vl.addElement(pred_mve);

                pdv0 = new PredDataValue(db, pfa.getID(), p0);
                pdv0.id = 100;        // invalid value for print test
                pdv0.itsCellID = 500; // invalid value for print test

                matrix_mve = new MatrixVocabElement(db, "matrix_mve");
                matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
                ufa = new UnTypedFormalArg(db, "<untyped>");
                matrix_mve.appendFormalArg(ufa);
                db.vl.addElement(matrix_mve);

                pdv1 = new PredDataValue(db, ufa.getID(), p3);
                pdv1.id = 101;        // invalid value for print test
                pdv1.itsCellID = 501; // invalid value for print test

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( db == null ) ||
                 ( pred_mve == null ) ||
                 ( pfa == null ) ||
                 ( pdv0 == null ) ||
                 ( matrix_mve == null ) ||
                 ( ufa == null ) ||
                 ( pdv1 == null ) ||
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

                    if ( pred_mve == null )
                    {
                        outStream.print("allocation of pred_mve failed.\n");
                    }

                    if ( pfa == null )
                    {
                        outStream.print("allocation of pfa failed.\n");
                    }

                    if ( pdv0 == null )
                    {
                        outStream.print("allocation of pdv0 failed.\n");
                    }

                    if ( matrix_mve == null )
                    {
                        outStream.print("allocation of matrix_mve failed.\n");
                    }

                    if ( ufa == null )
                    {
                        outStream.print("allocation of ufa failed.\n");
                    }

                    if ( pdv1 == null )
                    {
                        outStream.print("allocation of pdv1 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("Test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Test setup threw a system error " +
                                "exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pdv0.toString().compareTo(testString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pdv0.toString(): \"%s\".\n",
                                     pdv0.toString());
                }
            }

            if ( pdv0.toDBString().compareTo(testDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pdv0.toDBString(): \"%s\".\n",
                                     pdv0.toDBString());
                }
            }

            if ( pdv1.toString().compareTo(testString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pdv1.toString(): \"%s\".\n",
                                     pdv1.toString());
                }
            }

            if ( pdv1.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pdv1.toDBString(): \"%s\".\n",
                                     pdv1.toDBString());
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

    } /* PredDataValue::TestToStringMethods() */


    /**
     * VerifyPredDVCopy()
     *
     * Verify that the supplied instances of PredDataValue are distinct, that
     * they contain no common references (other than db), and that they have
     * the same value.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyPredDVCopy(PredDataValue base,
                                       PredDataValue copy,
                                       java.io.PrintStream outStream,
                                       boolean verbose,
                                       String baseDesc,
                                       String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyPredDVCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyPredDVCopy: %s null on entry.\n",
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
        else if ( ( base.itsValue == copy.itsValue ) &&
                  ( base.itsValue != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s share a Predicate.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.itsValue == null ) &&
                  ( copy.itsValue != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( ( base.itsValue != null ) &&
                  ( copy.itsValue == null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        copyDesc, baseDesc);
            }
        }
        else if ( ( base.itsValue != null ) &&
                  ( base.toString().compareTo(copy.toString()) != 0 ) )
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
        else if ( base.itsValue != null )
        {
            failures += Predicate.VerifyPredicateCopy(base.itsValue,
                                                      copy.itsValue,
                                                      outStream, verbose,
                                                      baseDesc + ".itsValue",
                                                      copyDesc + "itsValue");
        }

        return failures;

    } /* PredDataValue::VerifyPredDVCopy() */

} /* PredDataValue */
