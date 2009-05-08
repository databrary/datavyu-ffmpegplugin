/*
 * ColColPredDataValue.java
 *
 * Created on August 10, 2008, 4:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;

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

    /**
     * Creates a new copy of the object.
     *
     * @return A duplicate of this object.
     *
     * @throws java.lang.CloneNotSupportedException If the clone interface has
     * not been implemented.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        ColPredDataValue clone; // = (ColPredDataValue) super.clone();
        try {
            clone = new ColPredDataValue(this);
        } catch (SystemErrorException e) {
            clone = null;
        }

        return clone;
    }


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

        this.valueSet();
        return;

    } /* ColPredDataValue::setItsValue() */

    /**
     * @return true if the value equals the default value
     */
    public boolean isDefault() {
        return itsValue == null;
    }

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

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += HashUtils.Obj2H(itsValue) * Constants.SEED1;

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

} /* ColPredDataValue */
