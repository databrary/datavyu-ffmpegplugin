/*
 * ColColPredDataValue.java
 *
 * Created on August 10, 2008, 4:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

import java.util.Vector;

/**
 * An instance of ColPredDataValue is used to store a column predicate data
 * value that has been assigned to a formal argument.
 *
 * Recall that a column predicate is the predicate implied by a
 * MatrixVocabElement.
 *
 * @author mainzer
 */
public final class ColPredDataValue extends DataValue
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * itsValue: Instance of ColPred containing a representation of the
     *      current value assigned to the predicate.
     *
     * minVal & maxVal don't appear in PredDataValue as a subrange of
     *      predicates is expressed as a set of allowed predicates.  Given the
     *      potential size of this set, we don't keep a copy of it here --
     *      referring directly to the associated formal argument when needed
     *      instead.
     */

    /** ID of the represented predicate */
    protected ColPred itsValue = null;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * ColPredDataValue()
     *
     * Constructor for instances of ColPredDataValue.
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
     * The fourth takes a reference to an instance of ColPredDataValue as an
     * argument, and uses it to create a copy.
     *
     * The fifth is much the same as the fourth, save that it takes the
     * additional blindCopy parameter.  If this parameter is true, the
     * PredDataValue is copied without reference to the pve's underlying
     * any predicates.  This is necessary if a pve has changed, and we need
     * a copy of the old predicate so we can touch it up for changes in the
     * associated pve.
     *
     *                                              JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     *
     */

    public ColPredDataValue(Database db)
        throws SystemErrorException
    {
        super(db);

        this.setItsValue(null);

    } /* ColPredDataValue::ColPredDataValue(db) */

    public ColPredDataValue(Database db,
                            long fargID)
        throws SystemErrorException
    {
        super(db);

        this.setItsValue(null);

        this.setItsFargID(fargID);

    } /* ColPredDataValue::ColPredDataValue(db, fargID) */

    public ColPredDataValue(Database db,
                            long fargID,
                            ColPred value)
        throws SystemErrorException
    {
        super(db);

        this.setItsValue(value);

        this.setItsFargID(fargID);

    } /* ColPredDataValue::ColPredDataValue(db, fargID, value) */

    public ColPredDataValue(ColPredDataValue dv)
        throws SystemErrorException
    {
        super(dv);

        if ( dv.itsValue != null )
        {
            this.itsValue  = new ColPred(dv.itsValue);
        }

    } /* ColPredDataValue::ColPredDataValue(dv) */

    protected ColPredDataValue(ColPredDataValue dv,
                               boolean blindCopy)
        throws SystemErrorException
    {
        super(dv);

        if ( dv.itsValue != null )
        {
            this.itsValue  = new ColPred(dv.itsValue, blindCopy);
        }

    } /* ColPredDataValue::ColPredDataValue(dv, blindCopy) */


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

    public ColPred getItsValue()
        throws SystemErrorException
    {

        return new ColPred(this.itsValue);

    } /* ColPredDataValue::getItsValue() */


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

    protected ColPred getItsValueBlind()
        throws SystemErrorException
    {

        return new ColPred(this.itsValue, true);

    } /* ColPredDataValue::getItsValueBlind() */


    /**
     * getItsValueMveID()
     *
     * If itsValue is an instance of the column predicate implied by a
     * MatrixVocabElement, return the ID assigned to that mve, or the
     * INVALID_ID if it is not.
     *
     *                                      JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    protected long getItsValueMveID()
        throws SystemErrorException
    {
        long pveID = DBIndex.INVALID_ID;

        if ( this.itsValue != null )
        {
            pveID = this.itsValue.getMveID();
        }

        return pveID;

    } /* ColPredDataValue::getItsValueMveID() */


    /**
     * setItsValue()
     *
     * Set itsValue to the specified value.
     *
     *                                              JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    public void setItsValue(ColPred value)
        throws SystemErrorException
    {
        final String mName = "ColPredDataValue::setItsValue(): ";
        DBElement dbe;
        ColPredFormalArg cpfa;

        if ( ( value != null ) &&
             ( value.getDB() != this.getDB() ) )
        {
            throw new SystemErrorException(mName + "value.getDB() != this.db");
        }

        if ( ( value != null ) &&
             ( value.getMveID() != DBIndex.INVALID_ID ) &&
             ( ! getDB().vl.matrixInVocabList(value.getMveID()) ) )
        {
            throw new SystemErrorException(mName +
                    "! db.vl.matrixInVocabList(value.getMatrixID())");
        }

        if ( ( value == null ) ||
             ( value.getMveID() == DBIndex.INVALID_ID ) )
        {
            this.itsValue = new ColPred(this.getDB());
        }
        else
        {
            this.itsValue = new ColPred(value);
        }

        return;

    } /* ColPredDataValue::setItsValue() */


    /*************************************************************************/
    /*************************** Overrides: **********************************/
    /*************************************************************************/

    /**
     * clearID()
     *
     * Call the superclass version of the method, and then pass the clear id
     * message on to the associated column predicate, if any.
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
        super.clearID();

        if ( this.itsValue != null )
        {
            this.itsValue.clearID();
        }

        return;

    } /* ColPredDataValue::clearID() */


    /**
     * insertInIndex()
     *
     * Call the super, and then pass the insert in index message down to the
     * column predicate.
     *
     *                                              JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void insertInIndex(long DCID)
        throws SystemErrorException
    {
        final String mName = "ColPredDataValue::insertInIndex(): ";

        super.insertInIndex(DCID);

        if ( this.itsValue == null )
        {
            throw new SystemErrorException(mName + "itsValue is null?!?");
        }

        this.itsValue.insertInIndex(DCID);

        return;

    } /* ColPredDataValue::insertInIndex(DCID) */

    /**
     * removeFromIndex()
     *
     * Call the super, and then pass the remove from index message down to the
     * predicate.
     *
     *                                              JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void removeFromIndex(long DCID)
        throws SystemErrorException
    {
        final String mName = "ColPredDataValue::removeFromIndex(): ";

        super.removeFromIndex(DCID);

        if ( this.itsValue == null )
        {
            throw new SystemErrorException(mName + "itsValue is null?!?");
        }

        this.itsValue.removeFromIndex(DCID);

        return;

    } /* ColPredDataValue::removeFromIndex(DCID) */


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
        final String mName = "ColPredDataValue::replaceInIndex(): ";
        ColPredDataValue old_cpdv;

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

        if ( ! ( old_dv instanceof ColPredDataValue ) )
        {
            throw new SystemErrorException(mName + "old_dv not a pred dv?!?");
        }

        old_cpdv = (ColPredDataValue)old_dv;

        if ( old_cpdv.itsValue == null )
        {
            throw new SystemErrorException(mName +
                    "old_cpdv.itsValue is null?!?");
        }

        this.itsValue.updateIndexForReplacement(old_cpdv.itsValue,
                                                DCID,
                                                cascadeMveMod,
                                                cascadeMveDel,
                                                cascadeMveID,
                                                cascadePveMod,
                                                cascadePveDel,
                                                cascadePveID);

        return;

    } /* ColPredDataValue::replaceInIndex() */


    /**
     * toString()
     *
     * Returns a String representation of the DBValue for display.
     *
     *                                  JRM -- 8/10/08
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
        final String mName = "ColPredDataValue::toDBString(): ";

        if ( ( this.itsValue == null ) ||
             ( this.itsValue.getMveID() == DBIndex.INVALID_ID ) )
        {
            return ("(ColPredDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue ()" +
                    ") (subRange " + this.subRange + "))");
        }
        else
        {
            return ("(ColPredDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + this.itsValue.toDBString() +
                    ") (subRange " + this.subRange + "))");
        }

    } /* ColPredDataValue::toDBString() */


    /**
     * updateForMVEDefChange()
     *
     * If the associated column predicate is defined, pass an update for
     * matrix vocab element definition change message to it.
     *
     *                                          JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForMVEDefChange(
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
        final String mName = "ColPredDataValue::updateForMVEDefChange(): ";
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


        if ( this.itsValue != null )
        {
            this.itsValue.updateForMVEDefChange(db,
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

    } /* ColPredDataValue::updateForPVEDefChange() */


    /**
     * updateForMVEDeletion()
     *
     * If the associated column predicate is defined, pass an update for
     * matrix vocab element deletion message to it.
     *
     *                                          JRM -- 8/10/08
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

        if ( this.getDB() != db )
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

    } /* ColPredDataValue::updateForMVEDeletion() */


    /**
     * updateForMVEDefChange()
     *
     * If the associated column predicate is defined, pass an update for
     * predicate vocab element definition change message to it.
     *
     *                                          JRM -- 8/10/08
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
        final String mName = "ColPredDataValue::updateForPVEDefChange(): ";
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

    } /* ColPredDataValue::updateForPVEDefChange() */


    /**
     * updateForPVEDeletion()
     *
     * If the associated column predicate is defined, pass an update for
     * predicate vocab element deletion message to it.
     *
     *                                          JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateForPVEDeletion(Database db,
                                        long pveID)
        throws SystemErrorException
    {
        final String mName = "Matrix::updateForMVEDeletion(): ";

        if ( this.getDB() != db )
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

    } /* ColPredDataValue::updateForPVEDeletion() */


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
        final String mName = "ColPredDataValue::updateForFargChange(): ";

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

    } /* ColPredDataValue::updateForFargChange() */


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
        final String mName = "ColPredDataValue::updateSubRange(): ";

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");
        }

        if ( fa instanceof ColPredFormalArg )
        {
            this.subRange = false;
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

    } /* ColPredDataValue::updateSubRange() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/

    /**
     * coerceToRange()
     *
     * If the supplied value is in range for the associated formal argument,
     * simply return it.  Otherwise, coerce it to the nearest value that is
     * in range.
     *                                              JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */

    public ColPred coerceToRange(ColPred value)
        throws SystemErrorException
    {
        final String mName = "ColPredDataValue::coerceToRange(): ";
        DBElement dbe;
        ColPredFormalArg pfa;
        ColPred retVal;

        if ( value == null )
        {
            retVal = new ColPred(this.getDB());
        }
        else if ( value.getMveID() == DBIndex.INVALID_ID )
        {
            retVal = value;
        }
        else
        {
            retVal = value;
        }

        return retVal;

    } /* ColPredDataValue::coerceToRange() */


    /**
     * deregisterPreds()
     *
     * If this.itsValue isn't null, call its deregisterWithMve() method to
     * cause it to deregister with its associated MVE (if any), and pass
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
            this.itsValue.deregisterWithMve(cascadeMveDel,
                                            cascadeMveID,
                                            cascadePveDel,
                                            cascadePveID);
        }

        return;

    } /* ColPredDataValue::deregisterPreds() */


    /**
     * registerPreds()
     *
     * If this.itsValue isn't null, call its registerWithMve() method to
     * cause it to register with its associated MVE (if any), and pass
     * the message down to any column predicates or predicates that may
     * appear in its argument list.
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
            this.itsValue.registerWithMve();
        }

        return;

    } /* ColPredDataValue::registerPreds() */

    /** Seed value for generating hash codes. */
    private final static int SEED1 = 3;

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += this.itsValue == null ? 0 : this.itsValue.hashCode() * SEED1;

        return hash;
    }

    /**
     * Compares this ColPredDataValue against another object.
     * Assumption: ColPredDataValues are not equal just because their id fields
     * match. This function will test that db, id and lastModUID all match.
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

        ColPredDataValue c = (ColPredDataValue) obj;
        return super.equals(obj)
            && (this.itsValue == null ? c.itsValue == null
                                      : this.itsValue.equals(c.itsValue));
    }

    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Construct an instance of ColPredDataValue with the specified
     * initialization.
     *
     * Returns a reference to the newly constructed ColPredDataValue if
     * successful.  Throws a system error exception on failure.
     *
     *                                              JRM -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static ColPredDataValue Construct(Database db,
                                             ColPred cp)
        throws SystemErrorException
    {
        final String mName = "ColPredDataValue::Construct(db, cp)";
        ColPredDataValue cpdv = null;

        cpdv = new ColPredDataValue(db);

        if ( cpdv != null )
        {
            cpdv.setItsValue(cp);
        }

        return cpdv;

    } /* ColPredDataValue::Construct(db, cp) */


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
     *         argument constructor for ColPredDataValue.  Verify that all
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
     *         Construct a ColPredDataValue for the formal argument of the mve
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
     * TestClassColPredDataValue()
     *
     * Main routine for tests of class ColPredDataValue.
     *
     *                                      JRM -- 10/10/08
     *
     * Changes:
     *
     *    - Non.
     */

    public static boolean TestClassColPredDataValue(java.io.PrintStream outStream,
                                                    boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;

        outStream.print("Testing class ColPredDataValue:\n");

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
                    "%d failures in tests for class ColPredDataValue.\n\n",
                    failures);
        }
        else
        {
            outStream.print("All tests passed for class ColPredDataValue.\n\n");
        }

        return pass;

    } /* PredDataValue::TestClassColPredDataValue() */


    /**
     * Test3ArgConstructor()
     *
     * Run a battery of tests on the three argument constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 10/10/08
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
            "Testing 3 argument constructor for class ColPredDataValue        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long fargID = DBIndex.INVALID_ID;
        long untyped_farg_ID = DBIndex.INVALID_ID;
        long col_pred_farg_ID = DBIndex.INVALID_ID;
        long pve0_ID = DBIndex.INVALID_ID;
        long pve1_ID = DBIndex.INVALID_ID;
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
        PredicateVocabElement pve1 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        FormalArgument untyped_farg = null;
        FormalArgument col_pred_farg = null;
        DataValue arg = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> int_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        Vector<DataValue> matrix_cp1_arg_list = null;
        Vector<DataValue> matrix_cp2_arg_list = null;
        Vector<DataValue> nominal_cp_arg_list = null;
        Vector<DataValue> pred_cp_arg_list = null;
        Vector<DataValue> text_cp_arg_list = null;
        ColPred float_cp0 = null;
        ColPred int_cp0 = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp2 = null;
        ColPred nominal_cp0 = null;
        ColPred pred_cp0 = null;
        ColPred text_cp0 = null;
        ColPredDataValue cpdv = null;
        ColPredDataValue float_cpdv0 = null;
        ColPredDataValue int_cpdv0 = null;
        ColPredDataValue matrix_cpdv0 = null;
        ColPredDataValue matrix_cpdv1 = null;
        ColPredDataValue matrix_cpdv2 = null;
        ColPredDataValue nominal_cpdv0 = null;
        ColPredDataValue pred_cpdv0 = null;
        ColPredDataValue text_cpdv0 = null;


        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's needed for testing.
        //
        // For now, at least, the selection of mve's and cp's used in this
        // test is overkill.  But since I didn't figure this out until I had
        // already prepared them, I may as well leave them and use them all.
        // The day may come when they actually do something useful.

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
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mv1_ID == INVALID_ID.\n");
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
                    outStream.printf("matrix_mve2.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve2.getNumFormalArgs());
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


        // now create a selection of column predicates for testing
        if ( failures == 0 )
        {
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
                                     matrix_mve2.getFormalArg(0).getFargName());
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
                nominal_cp0 = new ColPred(db, nominal_mve_ID,
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

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list == null ) ||
                 ( float_cp0 == null ) ||
                 ( int_cp_arg_list == null ) ||
                 ( int_cp0 == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp_arg_list == null ) ||
                 ( nominal_cp0 == null ) ||
                 ( pred_cp_arg_list == null ) ||
                 ( pred_cp0 == null ) ||
                 ( text_cp_arg_list == null ) ||
                 ( text_cp0 == null ) ||
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

                    if ( float_cp0 == null )
                    {
                        outStream.printf("allocation of float_cp0 failed.\n");
                    }

                    if ( int_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_cp_arg_list failed.\n");
                    }

                    if ( int_cp0 == null )
                    {
                        outStream.printf("allocation of int_cp0 failed.\n");
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

                    if ( nominal_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of nominal_cp0 failed.\n");
                    }

                    if ( pred_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp_arg_list failed.\n");
                    }

                    if ( pred_cp0 == null )
                    {
                        outStream.printf("allocation of pred_cp0 failed.\n");
                    }

                    if ( text_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_cp_arg_list failed.\n");
                    }

                    if ( text_cp0 == null )
                    {
                        outStream.printf("allocation of text_cp0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("Creation of test column predicates " +
                                        "failed to complete\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            String testString0 =
                    "()";
            String testString1 =
                    "float_mve(11, 00:00:00:011, 00:00:11:000, 11.0)";
            String testString2 =
                    "int_mve(22, 00:00:00:022, 00:00:22:000, 22)";
            String testString3 =
                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, a_nominal, pve0(<arg>), \"q-string\", 00:00:01:000, <untyped>, float_mve(0, 00:00:00:000, 00:00:00:000, 0.0))";
//                    "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, a_nominal, pve0(<arg>), \"q-string\", 00:00:01:000, <untyped>, float_mve(<ord>, <onset>, <offset>, 0.0))";
            String testString4 =
                    "matrix_mve1(34, 00:00:00:034, 00:00:34:000, \" a q string \", <arg2>, 88)";
            String testString5 =
                    "matrix_mve2(35, 00:00:00:035, 00:00:35:000, <arg1>)";
            String testString6 =
                    "nominal_mve(44, 00:00:00:044, 00:00:44:000, another_nominal)";
            String testString7 =
                    "pred_mve(55, 00:00:00:055, 00:00:55:000, pve0(<arg>))";
            String testString8 =
                    "text_mve(66, 00:00:01:006, 00:01:06:000, a text string)";

            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                untyped_farg = matrix_mve0.getFormalArg(6);
                assert( farg.getFargType() == FormalArgument.fArgType.UNTYPED );
                untyped_farg_ID = untyped_farg.getID();
                assert( untyped_farg_ID != DBIndex.INVALID_ID );

                col_pred_farg = matrix_mve0.getFormalArg(7);
                assert( col_pred_farg.getFargType() ==
                        FormalArgument.fArgType.COL_PREDICATE );
                col_pred_farg_ID = col_pred_farg.getID();
                assert( col_pred_farg_ID != DBIndex.INVALID_ID );

                cpdv = new ColPredDataValue(db, untyped_farg_ID, null);
                float_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, float_cp0);
                int_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, int_cp0);
                matrix_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, matrix_cp0);
                matrix_cpdv1 = new ColPredDataValue(db, untyped_farg_ID, matrix_cp1);
                matrix_cpdv2 = new ColPredDataValue(db, col_pred_farg_ID, matrix_cp2);
                nominal_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, nominal_cp0);
                pred_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, pred_cp0);
                text_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, text_cp0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv == null ) ||
                 ( float_cpdv0 == null ) ||
                 ( int_cpdv0 == null ) ||
                 ( matrix_cpdv0 == null ) ||
                 ( matrix_cpdv1 == null ) ||
                 ( matrix_cpdv2 == null ) ||
                 ( nominal_cpdv0 == null ) ||
                 ( pred_cpdv0 == null ) ||
                 ( text_cpdv0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv == null )
                    {
                        outStream.print("allocation of cpdv failed.\n");
                    }

                    if ( float_cpdv0 == null )
                    {
                        outStream.print("allocation of float_cpdv0 failed.");
                    }

                    if ( int_cpdv0 == null )
                    {
                        outStream.print("allocation of int_cpdv0 failed.\n");
                    }

                    if ( matrix_cpdv0 == null )
                    {
                        outStream.print("allocation of matrix_cpdv0 failed.\n");
                    }

                    if ( matrix_cpdv1 == null )
                    {
                        outStream.print("allocation of matrix_cpdv1 failed.\n");
                    }

                    if ( matrix_cpdv2 == null )
                    {
                        outStream.print("allocation of matrix_cpdv2 failed.\n");
                    }

                    if ( nominal_cpdv0 == null )
                    {
                        outStream.print("allocation of nominal_cpdv0 failed.");
                    }

                    if ( pred_cpdv0 == null )
                    {
                        outStream.print("allocation of pred_cpdv0 failed.\n");
                    }

                    if ( text_cpdv0 == null )
                    {
                        outStream.print("allocation of text_cpdv0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("col pred data value allocation test " +
                                         "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Test threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( ( cpdv.toString().compareTo(testString0) != 0 ) ||
                      ( float_cpdv0.toString().compareTo(testString1) != 0 ) ||
                      ( int_cpdv0.toString().compareTo(testString2) != 0 ) ||
                      ( matrix_cpdv0.toString().compareTo(testString3) != 0 ) ||
                      ( matrix_cpdv1.toString().compareTo(testString4) != 0 ) ||
                      ( matrix_cpdv2.toString().compareTo(testString5) != 0 ) ||
                      ( nominal_cpdv0.toString().compareTo(testString6) != 0 ) ||
                      ( pred_cpdv0.toString().compareTo(testString7) != 0 ) ||
                      ( text_cpdv0.toString().compareTo(testString8) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv.toString().compareTo(testString0) != 0 )
                    {
                       outStream.printf("Unexpected cpdv.toString)(): \"%s\"\n",
                                         cpdv.toString());
                    }

                    if ( float_cpdv0.toString().compareTo(testString1) != 0 )
                    {
                       outStream.printf(
                               "Unexpected float_cpdv0.toString)(): \"%s\"\n",
                               float_cpdv0.toString());
                    }

                    if ( int_cpdv0.toString().compareTo(testString2) != 0 )
                    {
                       outStream.printf(
                               "Unexpected int_cpdv0.toString)(): \"%s\"\n",
                               int_cpdv0.toString());
                    }

                    if ( matrix_cpdv0.toString().compareTo(testString3) != 0 )
                    {
                       outStream.printf(
                               "Unexpected matrix_cpdv0.toString)(): \"%s\"\n",
                               matrix_cpdv0.toString());
                    }

                    if ( matrix_cpdv1.toString().compareTo(testString4) != 0 )
                    {
                       outStream.printf(
                               "Unexpected matrix_cpdv1.toString)(): \"%s\"\n",
                               matrix_cpdv1.toString());
                    }

                    if ( matrix_cpdv2.toString().compareTo(testString5) != 0 )
                    {
                       outStream.printf(
                               "Unexpected matrix_cpdv2.toString)(): \"%s\"\n",
                               matrix_cpdv2.toString());
                    }

                    if ( nominal_cpdv0.toString().compareTo(testString6) != 0 )
                    {
                       outStream.printf(
                               "Unexpected nominal_cpdv0.toString)(): \"%s\"\n",
                               nominal_cpdv0.toString());
                    }

                    if ( pred_cpdv0.toString().compareTo(testString7) != 0 )
                    {
                       outStream.printf(
                               "Unexpected pred_cpdv0.toString)(): \"%s\"\n",
                               pred_cpdv0.toString());
                    }

                    if ( text_cpdv0.toString().compareTo(testString8) != 0 )
                    {
                       outStream.printf(
                               "Unexpected text_cpdv0.toString)(): \"%s\"\n",
                               text_cpdv0.toString());
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               untyped_farg,
                                                               cpdv,
                                                               outStream,
                                                               verbose,
                                                               "cpdv");

            if ( cpdv.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("cpdv.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPred.VerifyColPredCopy(new ColPred(db),
                                                      cpdv.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "new ColPred(db)",
                                                      "cpdv.itsValue");
            }

            /**********************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               col_pred_farg,
                                                               float_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "float_cpdv0");

            if ( float_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("float_cpdv0.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPred.VerifyColPredCopy(float_cp0,
                                                      float_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "float_cpdv0.itsValue");
            }

            /**********************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               untyped_farg,
                                                               int_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "int_cpdv0");

            if ( int_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("int_cpdv0.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPred.VerifyColPredCopy(int_cp0,
                                                      int_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "int_cp0",
                                                      "int_cpdv0.itsValue");
            }

            /**********************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               col_pred_farg,
                                                               matrix_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "matrix_cpdv0");

            if ( matrix_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("matrix_cpdv0.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPred.VerifyColPredCopy(matrix_cp0,
                                                      matrix_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp0",
                                                      "matrix_cpdv0.itsValue");
            }

            /**********************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               untyped_farg,
                                                               matrix_cpdv1,
                                                               outStream,
                                                               verbose,
                                                               "matrix_cpdv1");

            if ( matrix_cpdv1.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("matrix_cpdv1.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPred.VerifyColPredCopy(matrix_cp1,
                                                      matrix_cpdv1.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp1",
                                                      "matrix_cpdv1.itsValue");
            }

            /**********************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               col_pred_farg,
                                                               matrix_cpdv2,
                                                               outStream,
                                                               verbose,
                                                               "matrix_cpdv2");

            if ( matrix_cpdv2.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("matrix_cpdv2.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPred.VerifyColPredCopy(matrix_cp2,
                                                      matrix_cpdv2.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp2",
                                                      "matrix_cpdv2.itsValue");
            }

            /**********************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               untyped_farg,
                                                               nominal_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "nominal_cpdv0");

            if ( nominal_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("nominal_cpdv0.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPred.VerifyColPredCopy(nominal_cp0,
                                                      nominal_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "nominal_cp0",
                                                      "nominal_cpdv0.itsValue");
            }

            /**********************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               col_pred_farg,
                                                               pred_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "pred_cpdv0");

            if ( pred_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pred_cpdv0.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPred.VerifyColPredCopy(pred_cp0,
                                                      pred_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "pred_cp0",
                                                      "pred_cpdv0.itsValue");
            }

            /**********************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               untyped_farg,
                                                               text_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "text_cpdv0");

            if ( text_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("text_cpdv0.itsValue null.\n");
                }
            }
            else
            {
                failures += ColPred.VerifyColPredCopy(text_cp0,
                                                      text_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "text_cp0",
                                                      "text_cpdv0.itsValue");
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            cpdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                cpdv = new ColPredDataValue((Database)null, untyped_farg_ID,
                                            float_cp0);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv != null )
                    {
                        outStream.print("new ColPredDataValue(null, " +
                                "untyped_farg_ID, float_cp0) returned " +
                                "non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new PredDataValue(null, " +
                                        "pfa.getID(), p4) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(null, " +
                                        "untyped_farg_ID, float_cp0) failed " +
                                        "to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            cpdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                cpdv = new ColPredDataValue(db, DBIndex.INVALID_ID, float_cp0);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv != null )
                    {
                        outStream.print("new PredDataValue(db, INVALID_ID, " +
                                        "float_cp0) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "INVALID_ID, float_cp0) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(db, INVALID_ID, " +
                                        "float_cp0) failed to throw a " +
                                        "system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an ID that does not
         * refer to a formal argument.
         */
        if ( failures == 0 )
        {
            cpdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                cpdv = new ColPredDataValue(db, pred_mve.getID(), float_cp0);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new PredDataValue(db, " +
                                "pred_mve.getID(), float_cp0) returned.\n");
                    }

                    if ( cpdv != null )
                    {
                        outStream.print("new PredDataValue(db, " +
                                "pred_mve.getID(), float_cp0) returned " +
                                "non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(db, " +
                                 "pred_mve.getID(), float_cp0) failed to " +
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
            "Testing class ColPredDataValue accessors                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long fargID = DBIndex.INVALID_ID;
        long untyped_farg_ID = DBIndex.INVALID_ID;
        long col_pred_farg_ID = DBIndex.INVALID_ID;
        long pve0_ID = DBIndex.INVALID_ID;
        long pve1_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        FormalArgument farg = null;
        FormalArgument untyped_farg = null;
        FormalArgument col_pred_farg = null;
        DataValue arg = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        ColPred float_cp0 = null;
        ColPred matrix_cp0 = null;
        ColPredDataValue cpdv = null;
        ColPredDataValue float_cpdv0 = null;
        ColPredDataValue matrix_cpdv0 = null;
        Database alt_db = null;
        long alt_float_mve0_ID = DBIndex.INVALID_ID;
        MatrixVocabElement alt_float_mve0 = null;
        Vector<DataValue> alt_float_cp0_arg_list = null;
        ColPred alt_float_cp0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's needed for testing.

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
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();
            matrix_mve1 = db.getMatrixVE(matrix_mve1_ID);

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve2.appendFormalArg(farg);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();
            matrix_mve2 = db.getMatrixVE(matrix_mve2_ID);

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
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 8 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 1 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
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

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
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
                else if ( matrix_mve1.getNumFormalArgs() != 1 )
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
                    outStream.printf("matrix_mve2.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve2.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2_ID == INVALID_ID.\n");
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


        // now create a selection of column predicates for testing
        if ( failures == 0 )
        {
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

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list == null ) ||
                 ( float_cp0 == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
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

                    if ( float_cp0 == null )
                    {
                        outStream.printf("allocation of float_cp0 failed.\n");
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

                    if ( ! completed )
                    {
                        outStream.print("Creation of test column predicates " +
                                        "failed to complete\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix creation threw a SystemErrorException: %s.\n",
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

            try
            {
                untyped_farg = matrix_mve1.getFormalArg(0);
                assert( untyped_farg.getFargType() == FormalArgument.fArgType.UNTYPED );
                untyped_farg_ID = untyped_farg.getID();
                assert( untyped_farg_ID != DBIndex.INVALID_ID );

                col_pred_farg = matrix_mve2.getFormalArg(0);
                assert( col_pred_farg.getFargType() ==
                        FormalArgument.fArgType.COL_PREDICATE );
                col_pred_farg_ID = col_pred_farg.getID();
                assert( col_pred_farg_ID != DBIndex.INVALID_ID );

                cpdv = new ColPredDataValue(db, untyped_farg_ID, null);
                float_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, float_cp0);
                matrix_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, matrix_cp0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv == null ) ||
                 ( float_cpdv0 == null ) ||
                 ( matrix_cpdv0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv == null )
                    {
                        outStream.print("allocation of cpdv failed.\n");
                    }

                    if ( float_cpdv0 == null )
                    {
                        outStream.print("allocation of float_cpdv0 failed.");
                    }

                    if ( matrix_cpdv0 == null )
                    {
                        outStream.print("allocation of matrix_cpdv0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("col pred data value allocation test " +
                                         "failed to complete.\n");
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
                                                               untyped_farg,
                                                               cpdv,
                                                               outStream,
                                                               verbose,
                                                               "cpdv");

            if ( cpdv.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("cpdv.itsValue null (1).\n");
                }
            }
            else
            {
                failures += ColPred.VerifyColPredCopy(new ColPred(db),
                                                      cpdv.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "new ColPred(db)",
                                                      "cpdv.itsValue");

                failures += DataValue.TestAccessors(db, untyped_farg,
                        matrix_mve2, col_pred_farg, cpdv, outStream, verbose);
            }

            if ( failures == 0 )
            {
                cpdv.setItsValue(float_cp0);

                if ( cpdv.getItsValue() == null )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("cpdv.getItsValue() == null (2).\n");
                    }
                }
                else
                {
                    failures += ColPred.VerifyColPredCopy(float_cp0,
                                                          cpdv.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "float_cp0",
                                                          "cpdv.itsValue");
                }
            }

            /**********************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               col_pred_farg,
                                                               float_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "float_cpdv0");

            if ( float_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("float_cpdv0.itsValue null (1).\n");
                }
            }
            else
            {
                failures += ColPred.VerifyColPredCopy(float_cp0,
                                                      float_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "float_cp0",
                                                      "float_cpdv0.itsValue");

                failures += DataValue.TestAccessors(db, col_pred_farg,
                        matrix_mve1, untyped_farg, float_cpdv0, outStream,
                        verbose);
            }

            if ( failures == 0 )
            {
                float_cpdv0.setItsValue(matrix_cp0);

                if ( float_cpdv0.getItsValue() == null )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "float_cpdv0.getItsValue() == null (2).\n");
                    }
                }
                else
                {
                    failures += ColPred.VerifyColPredCopy(matrix_cp0,
                                                          float_cpdv0.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "matrix_cp0",
                                                          "float_cpdv0.itsValue");
                }
            }

            /**********************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               untyped_farg,
                                                               matrix_cpdv0,
                                                               outStream,
                                                               verbose,
                                                               "matrix_cpdv0");

            if ( matrix_cpdv0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("matrix_cpdv0.itsValue null (1).\n");
                }
            }
            else
            {
                failures += ColPred.VerifyColPredCopy(matrix_cp0,
                                                      matrix_cpdv0.itsValue,
                                                      outStream,
                                                      verbose,
                                                      "matrix_cp0",
                                                      "matrix_cpdv0.itsValue");

                failures += DataValue.TestAccessors(db, untyped_farg,
                        matrix_mve2, col_pred_farg, matrix_cpdv0, outStream,
                        verbose);
            }

            if ( failures == 0 )
            {
                matrix_cpdv0.setItsValue(null);

                if ( matrix_cpdv0.getItsValue() == null )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "matrix_cpdv0.getItsValue() == null (2).\n");
                    }
                }
                else
                {
                    failures += ColPred.VerifyColPredCopy(new ColPred(db),
                                                          matrix_cpdv0.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new ColPred(db)",
                                                          "matrix_cpdv0.itsValue");
                }
            }
        }

        /* For now at least, there is no real need to test setItsValue with
         * invalid values.  The compiler requires that the supplied parameter
         * is an instance of ColPred, and the value supplied (if not null or
         * an empty ColPred) is passed through to the target formal arguments
         * isValidValue routine.  Since we already have tests for these
         * routines, there is no need to test them here.
         *
         * That said, against changes in the code, it is probably worth while
         * to pass through an invalid ColPred or two just to be sure.
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

                alt_float_mve0 = new MatrixVocabElement(alt_db, "alt_float_mve0");
                alt_float_mve0.setType(MatrixVocabElement.MatrixType.FLOAT);
                farg = new FloatFormalArg(alt_db);
                alt_float_mve0.appendFormalArg(farg);
                alt_db.vl.addElement(alt_float_mve0);
                alt_float_mve0_ID = alt_float_mve0.getID();


                alt_float_cp0_arg_list = new Vector<DataValue>();
                fargID = alt_float_mve0.getCPFormalArg(0).getID();
                arg = new IntDataValue(alt_db, fargID, 11);
                alt_float_cp0_arg_list.add(arg);
                fargID = alt_float_mve0.getCPFormalArg(1).getID();
                arg = new TimeStampDataValue(alt_db, fargID,
                        new TimeStamp(alt_db.getTicks(), 11));
                alt_float_cp0_arg_list.add(arg);
                fargID = alt_float_mve0.getCPFormalArg(2).getID();
                arg = new TimeStampDataValue(alt_db, fargID,
                        new TimeStamp(alt_db.getTicks(), 11 * alt_db.getTicks()));
                alt_float_cp0_arg_list.add(arg);
                fargID = alt_float_mve0.getCPFormalArg(3).getID();
                arg = new FloatDataValue(alt_db, fargID, 11.0);
                alt_float_cp0_arg_list.add(arg);
                alt_float_cp0 = new ColPred(alt_db, alt_float_mve0_ID,
                                            alt_float_cp0_arg_list);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( alt_db == null ) ||
                 ( alt_float_mve0 == null ) ||
                 ( alt_float_mve0_ID == DBIndex.INVALID_ID ) ||
                 ( alt_float_cp0_arg_list == null ) ||
                 ( alt_float_cp0 == null ) ||
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

                    if ( alt_float_mve0 == null )
                    {
                        outStream.print("creation of alt_float_mve0 failed.\n");
                    }

                    if ( alt_float_mve0_ID == DBIndex.INVALID_ID )
                    {
                        outStream.print("alt_float_mve0_ID not initialized.\n");
                    }

                    if ( alt_float_cp0_arg_list == null )
                    {
                        outStream.print(
                                "creation of alt_float_cp0_arg_list failed.\n");
                    }

                    if ( alt_float_cp0 == null )
                    {
                        outStream.print("creation of alt_float_cp0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "alt float cp setup failed to complete (1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("alt float cp setup threw a " +
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
                cpdv.setItsValue(alt_float_cp0);

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
                                "cpdv.setItsValue(alt_float_cp0) completed.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("cpdv.setItsValue(alt_float_cp0) " +
                                         "failed  to thow a system error.\n");
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
            "Testing copy constructor for class ColPredDataValue              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long fargID = DBIndex.INVALID_ID;
        long untyped_farg_ID = DBIndex.INVALID_ID;
        long col_pred_farg_ID = DBIndex.INVALID_ID;
        long pve0_ID = DBIndex.INVALID_ID;
        long pve1_ID = DBIndex.INVALID_ID;
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
        PredicateVocabElement pve1 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        MatrixVocabElement nominal_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement text_mve = null;
        FormalArgument farg = null;
        FormalArgument untyped_farg = null;
        FormalArgument col_pred_farg = null;
        DataValue arg = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> int_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        Vector<DataValue> matrix_cp1_arg_list = null;
        Vector<DataValue> matrix_cp2_arg_list = null;
        Vector<DataValue> nominal_cp_arg_list = null;
        Vector<DataValue> pred_cp_arg_list = null;
        Vector<DataValue> text_cp_arg_list = null;
        ColPred float_cp0 = null;
        ColPred int_cp0 = null;
        ColPred matrix_cp0 = null;
        ColPred matrix_cp1 = null;
        ColPred matrix_cp2 = null;
        ColPred nominal_cp0 = null;
        ColPred pred_cp0 = null;
        ColPred text_cp0 = null;
        ColPredDataValue cpdv = null;
        ColPredDataValue cpdv_copy = null;
        ColPredDataValue float_cpdv0 = null;
        ColPredDataValue float_cpdv0_copy = null;
        ColPredDataValue int_cpdv0 = null;
        ColPredDataValue int_cpdv0_copy = null;
        ColPredDataValue matrix_cpdv0 = null;
        ColPredDataValue matrix_cpdv0_copy = null;
        ColPredDataValue matrix_cpdv1 = null;
        ColPredDataValue matrix_cpdv1_copy = null;
        ColPredDataValue matrix_cpdv2 = null;
        ColPredDataValue matrix_cpdv2_copy = null;
        ColPredDataValue nominal_cpdv0 = null;
        ColPredDataValue nominal_cpdv0_copy = null;
        ColPredDataValue pred_cpdv0 = null;
        ColPredDataValue pred_cpdv0_copy = null;
        ColPredDataValue text_cpdv0 = null;
        ColPredDataValue text_cpdv0_copy = null;


        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's needed for testing.
        //
        // For now, at least, the selection of mve's and cp's used in this
        // test is overkill.  But since I didn't figure this out until I had
        // already prepared them, I may as well leave them and use them all.
        // The day may come when they actually do something useful.

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
                else if ( matrix_mve1.getNumFormalArgs() != 3 )
                {
                    outStream.printf("matrix_mve1.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve1.getNumFormalArgs());
                }

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mv1_ID == INVALID_ID.\n");
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
                    outStream.printf("matrix_mve2.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve2.getNumFormalArgs());
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


        // now create a selection of column predicates for testing
        if ( failures == 0 )
        {
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
                                     matrix_mve2.getFormalArg(0).getFargName());
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
                nominal_cp0 = new ColPred(db, nominal_mve_ID,
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

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list == null ) ||
                 ( float_cp0 == null ) ||
                 ( int_cp_arg_list == null ) ||
                 ( int_cp0 == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
                 ( matrix_cp1 == null ) ||
                 ( matrix_cp2 == null ) ||
                 ( nominal_cp_arg_list == null ) ||
                 ( nominal_cp0 == null ) ||
                 ( pred_cp_arg_list == null ) ||
                 ( pred_cp0 == null ) ||
                 ( text_cp_arg_list == null ) ||
                 ( text_cp0 == null ) ||
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

                    if ( float_cp0 == null )
                    {
                        outStream.printf("allocation of float_cp0 failed.\n");
                    }

                    if ( int_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of int_cp_arg_list failed.\n");
                    }

                    if ( int_cp0 == null )
                    {
                        outStream.printf("allocation of int_cp0 failed.\n");
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

                    if ( nominal_cp0 == null )
                    {
                        outStream.printf(
                                "allocation of nominal_cp0 failed.\n");
                    }

                    if ( pred_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of pred_cp_arg_list failed.\n");
                    }

                    if ( pred_cp0 == null )
                    {
                        outStream.printf("allocation of pred_cp0 failed.\n");
                    }

                    if ( text_cp_arg_list == null )
                    {
                        outStream.printf(
                                "allocation of text_cp_arg_list failed.\n");
                    }

                    if ( text_cp0 == null )
                    {
                        outStream.printf("allocation of text_cp0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("Creation of test column predicates " +
                                        "failed to complete\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix creation threw a SystemErrorException: %s.\n",
                            systemErrorExceptionString);
                    }
                }
            }
        }


        // now create a set of column predicate data values for copying:
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                untyped_farg = matrix_mve0.getFormalArg(6);
                assert( farg.getFargType() == FormalArgument.fArgType.UNTYPED );
                untyped_farg_ID = untyped_farg.getID();
                assert( untyped_farg_ID != DBIndex.INVALID_ID );

                col_pred_farg = matrix_mve0.getFormalArg(7);
                assert( col_pred_farg.getFargType() ==
                        FormalArgument.fArgType.COL_PREDICATE );
                col_pred_farg_ID = col_pred_farg.getID();
                assert( col_pred_farg_ID != DBIndex.INVALID_ID );

                cpdv = new ColPredDataValue(db, untyped_farg_ID, null);
                float_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, float_cp0);
                int_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, int_cp0);
                matrix_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, matrix_cp0);
                matrix_cpdv1 = new ColPredDataValue(db, untyped_farg_ID, matrix_cp1);
                matrix_cpdv2 = new ColPredDataValue(db, col_pred_farg_ID, matrix_cp2);
                nominal_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, nominal_cp0);
                pred_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, pred_cp0);
                text_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, text_cp0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv == null ) ||
                 ( float_cpdv0 == null ) ||
                 ( int_cpdv0 == null ) ||
                 ( matrix_cpdv0 == null ) ||
                 ( matrix_cpdv1 == null ) ||
                 ( matrix_cpdv2 == null ) ||
                 ( nominal_cpdv0 == null ) ||
                 ( pred_cpdv0 == null ) ||
                 ( text_cpdv0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv == null )
                    {
                        outStream.print("allocation of cpdv failed.\n");
                    }

                    if ( float_cpdv0 == null )
                    {
                        outStream.print("allocation of float_cpdv0 failed.");
                    }

                    if ( int_cpdv0 == null )
                    {
                        outStream.print("allocation of int_cpdv0 failed.\n");
                    }

                    if ( matrix_cpdv0 == null )
                    {
                        outStream.print("allocation of matrix_cpdv0 failed.\n");
                    }

                    if ( matrix_cpdv1 == null )
                    {
                        outStream.print("allocation of matrix_cpdv1 failed.\n");
                    }

                    if ( matrix_cpdv2 == null )
                    {
                        outStream.print("allocation of matrix_cpdv2 failed.\n");
                    }

                    if ( nominal_cpdv0 == null )
                    {
                        outStream.print("allocation of nominal_cpdv0 failed.");
                    }

                    if ( pred_cpdv0 == null )
                    {
                        outStream.print("allocation of pred_cpdv0 failed.\n");
                    }

                    if ( text_cpdv0 == null )
                    {
                        outStream.print("allocation of text_cpdv0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("col pred data value allocation " +
                                         "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("col pred data value allocation " +
                                "threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }


        // use the copy constructor to create copies of the col pred data values:

        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                cpdv_copy = new ColPredDataValue(cpdv);
                float_cpdv0_copy = new ColPredDataValue(float_cpdv0);
                int_cpdv0_copy = new ColPredDataValue(int_cpdv0);
                matrix_cpdv0_copy = new ColPredDataValue(matrix_cpdv0);
                matrix_cpdv1_copy = new ColPredDataValue(matrix_cpdv1);
                matrix_cpdv2_copy = new ColPredDataValue(matrix_cpdv2);
                nominal_cpdv0_copy = new ColPredDataValue(nominal_cpdv0);
                pred_cpdv0_copy = new ColPredDataValue(pred_cpdv0);
                text_cpdv0_copy = new ColPredDataValue(text_cpdv0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv_copy == null ) ||
                 ( float_cpdv0_copy == null ) ||
                 ( int_cpdv0_copy == null ) ||
                 ( matrix_cpdv0_copy == null ) ||
                 ( matrix_cpdv1_copy == null ) ||
                 ( matrix_cpdv2_copy == null ) ||
                 ( nominal_cpdv0_copy == null ) ||
                 ( pred_cpdv0_copy == null ) ||
                 ( text_cpdv0_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv == null )
                    {
                        outStream.print("allocation of cpdv_copy failed.\n");
                    }

                    if ( float_cpdv0_copy == null )
                    {
                        outStream.print("allocation of float_cpdv0_copy failed.");
                    }

                    if ( int_cpdv0_copy == null )
                    {
                        outStream.print("allocation of int_cpdv0_copy failed.\n");
                    }

                    if ( matrix_cpdv0_copy == null )
                    {
                        outStream.print("allocation of matrix_cpdv0_copy failed.\n");
                    }

                    if ( matrix_cpdv1_copy == null )
                    {
                        outStream.print("allocation of matrix_cpdv1_copy failed.\n");
                    }

                    if ( matrix_cpdv2_copy == null )
                    {
                        outStream.print("allocation of matrix_cpdv2_copy failed.\n");
                    }

                    if ( nominal_cpdv0_copy == null )
                    {
                        outStream.print("allocation of nominal_cpdv0_copy failed.");
                    }

                    if ( pred_cpdv0_copy == null )
                    {
                        outStream.print("allocation of pred_cpdv0_copy failed.\n");
                    }

                    if ( text_cpdv0_copy == null )
                    {
                        outStream.print("allocation of text_cpdv0_copy failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("col pred data value copy constructor " +
                                         "test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("col pred data value copy constructor " +
                                "test threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.VerifyDVCopy(cpdv, cpdv_copy,
                    outStream, verbose, "cpdv", "cpdv_copy");

            failures += DataValue.VerifyDVCopy(float_cpdv0, float_cpdv0_copy,
                    outStream, verbose, "float_cpdv0", "float_cpdv0_copy");

            failures += DataValue.VerifyDVCopy(int_cpdv0, int_cpdv0_copy,
                    outStream, verbose, "int_cpdv0", "int_cpdv0_copy");

            failures += DataValue.VerifyDVCopy(matrix_cpdv0, matrix_cpdv0_copy,
                    outStream, verbose, "matrix_cpdv0", "matrix_cpdv0_copy");

            failures += DataValue.VerifyDVCopy(matrix_cpdv1, matrix_cpdv1_copy,
                    outStream, verbose, "matrix_cpdv1", "matrix_cpdv1_copy");

            failures += DataValue.VerifyDVCopy(matrix_cpdv2, matrix_cpdv2_copy,
                    outStream, verbose, "matrix_cpdv2", "matrix_cpdv2_copy");

            failures += DataValue.VerifyDVCopy(nominal_cpdv0, nominal_cpdv0_copy,
                    outStream, verbose, "nominal_cpdv0", "nominal_cpdv0_copy");

            failures += DataValue.VerifyDVCopy(pred_cpdv0, pred_cpdv0_copy,
                    outStream, verbose, "pred_cpdv0", "pred_cpdv0_copy");

            failures += DataValue.VerifyDVCopy(text_cpdv0, text_cpdv0_copy,
                    outStream, verbose, "text_cpdv0", "text_cpdv0_copy");
        }

        /* verify that the constructor fails when given an invalid dv */
        if ( failures == 0 )
        {
            cpdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                cpdv = new ColPredDataValue((ColPredDataValue)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "new ColPredDataValue(null) completed.\n");
                    }

                    if ( cpdv != null )
                    {
                        outStream.print(
                            "new ColPredDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new ColPredDataValue(null) failed " +
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
     *                                              JRM -- 10/11/08
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
        String testString0 = "()";
        String testDBString0 =
                "(ColPredDataValue (id 1000) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 1001) (itsValue ()) (subRange false))";
        String testString1 = "float_mve(11, 00:00:00:011, 00:00:11:000, 11.0)";
        String testDBString1 =
                "(ColPredDataValue (id 2000) (itsFargID 39) (itsFargType COL_PREDICATE) (itsCellID 2001) (itsValue (colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 8) (itsFargType INTEGER) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 9) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))) (subRange false))";
//                "(ColPredDataValue (id 2000) (itsFargID 39) (itsFargType COL_PREDICATE) (itsCellID 2001) (itsValue (colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue 11) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 9) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:011)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:11:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 11.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))) (subRange false))";
        String testString2 =
                "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, a_nominal, pve0(<arg>), \"q-string\", 00:00:01:000, <untyped>, float_mve(0, 00:00:00:000, 00:00:00:000, 0.0))";
//                "matrix_mve0(33, 00:00:00:033, 00:00:33:000, 1.0, 2, a_nominal, pve0(<arg>), \"q-string\", 00:00:01:000, <untyped>, float_mve(<ord>, <onset>, <offset>, 0.0))";
        String testDBString2 =
                "(ColPredDataValue (id 3000) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 3001) (itsValue (colPred (id 0) (mveID 12) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 21) (itsFargType INTEGER) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 22) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 23) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 24) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 25) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 26) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 27) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 28) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 29) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 30) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false)), (ColPredDataValue (id 0) (itsFargID 31) (itsFargType COL_PREDICATE) (itsCellID 0) (itsValue (colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 8) (itsFargType INTEGER) (itsCellID 0) (itsValue 0) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 9) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 10) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:00:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))) (subRange false))))))) (subRange false))";
//                "(ColPredDataValue (id 3000) (itsFargID 33) (itsFargType UNTYPED) (itsCellID 3001) (itsValue (colPred (id 0) (mveID 12) (mveName matrix_mve0) (varLen false) (argList ((IntDataValue (id 0) (itsFargID 21) (itsFargType UNTYPED) (itsCellID 0) (itsValue 33) (subRange false) (minVal 0) (maxVal 0)), (TimeStampDataValue (id 0) (itsFargID 22) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:00:033)) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 23) (itsFargType UNTYPED) (itsCellID 0) (itsValue (60,00:00:33:000)) (subRange false)), (FloatDataValue (id 0) (itsFargID 24) (itsFargType FLOAT) (itsCellID 0) (itsValue 1.0) (subRange false) (minVal 0.0) (maxVal 0.0)), (IntDataValue (id 0) (itsFargID 25) (itsFargType INTEGER) (itsCellID 0) (itsValue 2) (subRange false) (minVal 0) (maxVal 0)), (NominalDataValue (id 0) (itsFargID 26) (itsFargType NOMINAL) (itsCellID 0) (itsValue a_nominal) (subRange false)), (PredDataValue (id 0) (itsFargID 27) (itsFargType PREDICATE) (itsCellID 0) (itsValue (predicate (id 0) (predID 1) (predName pve0) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 2) (itsFargType UNTYPED) (itsCellID 0) (itsValue <arg>) (subRange false))))))) (subRange false)), (QuoteStringDataValue (id 0) (itsFargID 28) (itsFargType QUOTE_STRING) (itsCellID 0) (itsValue q-string) (subRange false)), (TimeStampDataValue (id 0) (itsFargID 29) (itsFargType TIME_STAMP) (itsCellID 0) (itsValue (60,00:00:01:000)) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 30) (itsFargType UNTYPED) (itsCellID 0) (itsValue <untyped>) (subRange false)), (ColPredDataValue (id 0) (itsFargID 31) (itsFargType COL_PREDICATE) (itsCellID 0) (itsValue (colPred (id 0) (mveID 6) (mveName float_mve) (varLen false) (argList ((UndefinedDataValue (id 0) (itsFargID 8) (itsFargType UNTYPED) (itsCellID 0) (itsValue <ord>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 9) (itsFargType UNTYPED) (itsCellID 0) (itsValue <onset>) (subRange false)), (UndefinedDataValue (id 0) (itsFargID 10) (itsFargType UNTYPED) (itsCellID 0) (itsValue <offset>) (subRange false)), (FloatDataValue (id 0) (itsFargID 11) (itsFargType FLOAT) (itsCellID 0) (itsValue 0.0) (subRange false) (minVal 0.0) (maxVal 0.0))))))) (subRange false))))))) (subRange false))";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long fargID = DBIndex.INVALID_ID;
        long untyped_farg_ID = DBIndex.INVALID_ID;
        long col_pred_farg_ID = DBIndex.INVALID_ID;
        long pve0_ID = DBIndex.INVALID_ID;
        long pve1_ID = DBIndex.INVALID_ID;
        long float_mve_ID = DBIndex.INVALID_ID;
        long matrix_mve0_ID = DBIndex.INVALID_ID;
        long matrix_mve1_ID = DBIndex.INVALID_ID;
        long matrix_mve2_ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        MatrixVocabElement matrix_mve2 = null;
        FormalArgument farg = null;
        FormalArgument untyped_farg = null;
        FormalArgument col_pred_farg = null;
        DataValue arg = null;
        Vector<DataValue> float_cp_arg_list = null;
        Vector<DataValue> matrix_cp0_arg_list = null;
        ColPred float_cp0 = null;
        ColPred matrix_cp0 = null;
        ColPredDataValue cpdv = null;
        ColPredDataValue float_cpdv0 = null;
        ColPredDataValue matrix_cpdv0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by creating a selection of mve's needed for testing.

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
            farg = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve1.appendFormalArg(farg);
            db.vl.addElement(matrix_mve1);
            matrix_mve1_ID = matrix_mve1.getID();
            matrix_mve1 = db.getMatrixVE(matrix_mve1_ID);

            matrix_mve2 = new MatrixVocabElement(db, "matrix_mve2");
            matrix_mve2.setType(MatrixVocabElement.MatrixType.MATRIX);
            farg = new ColPredFormalArg(db, "<colpred>");
            matrix_mve2.appendFormalArg(farg);
            db.vl.addElement(matrix_mve2);
            matrix_mve2_ID = matrix_mve2.getID();
            matrix_mve2 = db.getMatrixVE(matrix_mve2_ID);

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
             ( matrix_mve0 == null ) ||
             ( matrix_mve0.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve0.getNumFormalArgs() != 8 ) ||
             ( matrix_mve0_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve1 == null ) ||
             ( matrix_mve1.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve1.getNumFormalArgs() != 1 ) ||
             ( matrix_mve1_ID == DBIndex.INVALID_ID ) ||
             ( matrix_mve2 == null ) ||
             ( matrix_mve2.getType() != MatrixVocabElement.MatrixType.MATRIX ) ||
             ( matrix_mve2.getNumFormalArgs() != 1 ) ||
             ( matrix_mve2_ID == DBIndex.INVALID_ID ) ||
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

                if ( matrix_mve1_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve1_ID == INVALID_ID.\n");
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
                else if ( matrix_mve1.getNumFormalArgs() != 1 )
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
                    outStream.printf("matrix_mve2.getNumFormalArgs() returned " +
                                     "unexpected value: %d.\n",
                                     matrix_mve2.getNumFormalArgs());
                }

                if ( matrix_mve2_ID == DBIndex.INVALID_ID )
                {
                    outStream.print("matrix_mve2_ID == INVALID_ID.\n");
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


        // now create a selection of column predicates for testing
        if ( failures == 0 )
        {
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

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( float_cp_arg_list == null ) ||
                 ( float_cp0 == null ) ||
                 ( matrix_cp0_arg_list == null ) ||
                 ( matrix_cp0 == null ) ||
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

                    if ( float_cp0 == null )
                    {
                        outStream.printf("allocation of float_cp0 failed.\n");
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

                    if ( ! completed )
                    {
                        outStream.print("Creation of test column predicates " +
                                        "failed to complete\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "matrix creation threw a SystemErrorException: %s.\n",
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

            try
            {
                untyped_farg = matrix_mve1.getFormalArg(0);
                assert( untyped_farg.getFargType() == FormalArgument.fArgType.UNTYPED );
                untyped_farg_ID = untyped_farg.getID();
                assert( untyped_farg_ID != DBIndex.INVALID_ID );

                col_pred_farg = matrix_mve2.getFormalArg(0);
                assert( col_pred_farg.getFargType() ==
                        FormalArgument.fArgType.COL_PREDICATE );
                col_pred_farg_ID = col_pred_farg.getID();
                assert( col_pred_farg_ID != DBIndex.INVALID_ID );

                cpdv = new ColPredDataValue(db, untyped_farg_ID, null);
                cpdv.setID(1000);             // fake value for testing
                cpdv.itsCellID = 1001;        // fake value for testing
                cpdv.itsPredID = 1002;        // fake value for testing

                float_cpdv0 = new ColPredDataValue(db, col_pred_farg_ID, float_cp0);
                float_cpdv0.setID(2000);      // fake value for testing
                float_cpdv0.itsCellID = 2001; // fake value for testing
                float_cpdv0.itsPredID = 2002; // fake value for testing

                matrix_cpdv0 = new ColPredDataValue(db, untyped_farg_ID, matrix_cp0);
                matrix_cpdv0.setID(3000);      // fake value for testing
                matrix_cpdv0.itsCellID = 3001; // fake value for testing
                matrix_cpdv0.itsPredID = 3002; // fake value for testing

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( cpdv == null ) ||
                 ( float_cpdv0 == null ) ||
                 ( matrix_cpdv0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( cpdv == null )
                    {
                        outStream.print("allocation of cpdv failed.\n");
                    }

                    if ( float_cpdv0 == null )
                    {
                        outStream.print("allocation of float_cpdv0 failed.");
                    }

                    if ( matrix_cpdv0 == null )
                    {
                        outStream.print("allocation of matrix_cpdv0 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("col pred data value allocation test " +
                                         "failed to complete.\n");
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
            if ( cpdv.toString().compareTo(testString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected cpdv.toString(): \"%s\".\n",
                                     cpdv.toString());
                }
            }

            if ( cpdv.toDBString().compareTo(testDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected cpdv.toDBString(): \"%s\".\n",
                                     cpdv.toDBString());
                }
            }

            if ( float_cpdv0.toString().compareTo(testString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected float_cpdv0.toString(): \"%s\".\n",
                            float_cpdv0.toString());
                }
            }

            if ( float_cpdv0.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected float_cpdv0.toDBString(): \"%s\".\n",
                            float_cpdv0.toDBString());
                }
            }

            if ( matrix_cpdv0.toString().compareTo(testString2) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected matrix_cpdv0.toString(): \"%s\".\n",
                            matrix_cpdv0.toString());
                }
            }

            if ( matrix_cpdv0.toDBString().compareTo(testDBString2) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected matrix_cpdv0.toDBString(): \"%s\".\n",
                            matrix_cpdv0.toDBString());
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

    } /* ColPredDataValue::TestToStringMethods() */


    /**
     * VerifyColPredDVCopy()
     *
     * Verify that the supplied instances of ColPredDataValue are distinct, that
     * they contain no common references (other than db), and that they have
     * the same value.
     *                                              JRM -- 10/3/08
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyColPredDVCopy(ColPredDataValue base,
                                          ColPredDataValue copy,
                                          java.io.PrintStream outStream,
                                          boolean verbose,
                                          String baseDesc,
                                          String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyColPredDVCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyColPredDVCopy: %s null on entry.\n",
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
        else if ( base.getDB() != copy.getDB() )
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
            failures += ColPred.VerifyColPredCopy(base.itsValue,
                                                  copy.itsValue,
                                                  outStream, verbose,
                                                  baseDesc + ".itsValue",
                                                  copyDesc + ".itsValue");
        }

        return failures;

    } /* ColPredDataValue::VerifyColPredDVCopy() */

} /* ColPredDataValue */
