/*
 * PredDataValue.java
 *
 * Created on August 19, 2007, 5:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;


/**
 * An instance of PredDataValue is used to store a predicate value
 * assigned to a formal argument.
 */
public final class PredDataValue extends DataValue
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
     *                                               -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public PredDataValue(Database db)
        throws SystemErrorException {
        super(db);

        // calling setItsValue(null) actually sets it to an empty predicate
        this.setItsValue(null);
        this.clearValue();

    } /* PredDataValue::PredDataValue(db) */

    public PredDataValue(Database db,
                        long fargID)
        throws SystemErrorException
    {
        super(db);

        this.setItsValue(null);

        this.setItsFargID(fargID);
        this.clearValue();

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
        PredDataValue clone;
        try {
            clone = new PredDataValue(this);
        } catch (SystemErrorException e) {
            clone = null;
        }

        return clone;
    }

    /**
     * Creates a new copy of the DataValue without performing any additional
     * sainity checking.
     *
     * @return A copy of the DataValue object
     *
     * @throws java.lang.CloneNotSupportedException If blindCopy is not
     * supported by this class.
     */
    @Override
    public Object blindClone() throws CloneNotSupportedException {
        PredDataValue clone;
        try {
            clone = new PredDataValue(this, true);
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
     *                           -- 8/16/07
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
     *                           -- 8/16/07
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
     *                                       -- 4/24/08
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
     *                                               -- 8/16/07
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
             ( value.getDB() != this.getDB() ) )
        {
            throw new SystemErrorException(mName + "value.getDB() != this.db");
        }

        if ( ( value != null ) &&
             ( value.getPveID() != DBIndex.INVALID_ID ) &&
             ( ! getDB().vl.predInVocabList(value.getPveID()) ) )
        {
            throw new SystemErrorException(mName +
                    "! db.vl.predInVocabList(value.getPredID())");
        }

        if ( ( value == null ) ||
             ( value.getPveID() == DBIndex.INVALID_ID ) )
        {
            this.itsValue = new Predicate(this.getDB());
        }
        else if ( this.subRange )
        {
            if ( this.itsFargID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName +
                                      "subRange && (itsFargID == INVALID_ID)");
            }
            else if ( itsFargType != FormalArgument.FArgType.PREDICATE )
            {
                throw new SystemErrorException(mName +
                                               "itsFargType != PREDICATE");
            }

            dbe = this.getDB().idx.getElement(this.itsFargID);

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
                this.itsValue = new Predicate(this.getDB());
            }
        }
        else
        {
            this.itsValue = new Predicate(value);
        }

        this.valueSet();
        return;

    } /* PredDataValue::setItsValue() */

    /**
     * @return true if the value equals the default value
     */
    @Override
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
     * message on to the associated predicate, if any.
     *
     *                                               2/19/08
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
     *                                               -- 2/19/08
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
     *                                               -- 2/19/08
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
     *                                           -- 2/20/08
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
     *                                   -- 8/15/07
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
     *                                       -- 8/15/07
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
            return ("(PredDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue ()" +
                    ") (subRange " + this.subRange + "))");
        }
        else
        {
            return ("(PredDataValue (id " + this.getID() +
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
     *                                           -- 8/26/08
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
     *                                           -- 8/30/08
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

    } /* PredDataValue::updateForPVEDeletion() */


    /**
     * updateForPVEDefChange()
     *
     * Scan the list of data values in the matrix, and pass an update for
     * predicate vocab element definition change message to the predicate
     * (if defined).
     *                                           -- 3/23/08
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

    } /* PredDataValue::updateForPVEDefChange() */


    /**
     * updateForPVEDeletion()
     *
     * Scan the list of data values in the matrix, and pass an update for
     * predicate vocab element definition change message to any predicate
     * data values.
     *                                           -- 3/23/08
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

    } /* PredDataValue::updateForPVEDeletion() */


    /**
     * updateForFargChange()
     *
     * Update for a change in the formal argument name, and/or subrange.
     *
     *                                           -- 3/22/08
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
     *                                           -- 8/16/07
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
                        this.itsValue = new Predicate(this.getDB());
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
     *                                               -- 070815
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
            retVal = new Predicate(this.getDB());
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
            else if ( itsFargType != FormalArgument.FArgType.PREDICATE )
            {
                throw new SystemErrorException(mName +
                                               "itsFargType != PREDICATE");
            }

            dbe = this.getDB().idx.getElement(this.itsFargID);

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
                retVal = new Predicate(this.getDB());
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
     *                                               -- 3/24/08
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
     *                                               -- 3/24/08
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
     *                                               -- 3/31/08
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

} /* PredDataValue */
