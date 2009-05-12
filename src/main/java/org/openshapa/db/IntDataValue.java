/*
 * IntDataValue.java
 *
 * Created on August 16, 2007, 7:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;


/**
 * An instance of IntDataValue is used to store an integer value
 * assigned to a formal argument.
 */
public final class IntDataValue extends DataValue
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * ItsDefault:  Constant containing the value to be assigned to all
     *      integer data values unless otherwise specified.
     *
     * itsValue:   Long containing the value assigned to the formal argument.
     *
     * minVal:  If subRange is true, this field contains the minimum value
     *      that may be assigned to the formal argument associated with the
     *      data value.   This value should always be the same as the minVal
     *      of the associated instance of OmtFormalArg.
     *
     *      Note that this data value may be used to hold an integer
     *      value assigned to an untype formal argument, in which case
     *      subrange will always be false.
     *
     * maxVal:  If subRange is true, this field contains the maximum value
     *      that may be assigned to the formal argument associated with the
     *      data value.   This value should always be the same as the maxVal
     *      of the associated instance of IntFormalArg.
     *
     *      Note that this data value may be used to hold an integer
     *      value assigned to an untype formal argument, in which case
     *      subrange will always be false.
     */

    /** default value for integers. */
    final long ItsDefault = 0;

    /** the value assigned to the associated formal argument in this case. */
    long itsValue = ItsDefault;

    /** the minimum value -- if subrange is true. */
    long minVal = 0;

    /** the maximum value -- if subrange is true. */
    long maxVal = 0;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * IntDataValue()
     *
     * Constructor for instances of IntDataValue.
     *
     * Four versions of this constructor.
     *
     * The first takes a reference to a database as its parameter and just
     * calls the super() constructor.
     *
     * The second takes a reference to a database, and a formal argument ID,
     * and attempts to set the itsFargID field of the data value accordingly.
     *
     * The third takes a reference to a database, a formal argument ID, and
     * a value as arguments, and attempts to set the itsFargID and itsValue
     * of the data value accordingly.
     *
     * The fourth takes a reference to an instance of IntDataValue as an
     * argument, and uses it to create a copy.
     *
     *                                               -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public IntDataValue(Database db)
        throws SystemErrorException {
        super(db);

    } /* IntDataValue::IntDataValue(db) */

    public IntDataValue(Database db,
                        long fargID)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

    } /* IntDataValue::IntDataValue(db, fargID) */

    public IntDataValue(Database db,
                        long fargID,
                        long value)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

        this.setItsValue(value);

    } /* IntDataValue::IntDataValue(db, fargID, value) */

    public IntDataValue(IntDataValue dv)
        throws SystemErrorException
    {
        super(dv);

        this.itsValue  = dv.itsValue;
        this.minVal    = dv.minVal;
        this.maxVal    = dv.maxVal;

    } /* IntDataValue::IntDataValue(dv) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getItsValue()
     *
     * Return the current value of the data value.
     *
     *                           -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    public long getItsValue()
    {

        return this.itsValue;

    } /* IntDataValue::getItsValue() */

    /**
     * setItsValue()
     *
     * Set itsValue to the specified value.  If subrange is true, coerce the
     * value into the subrange.
     *
     *                                               -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    public void setItsValue(long value)
    {
        if ( this.subRange )
        {
            if ( value > this.maxVal )
            {
                this.itsValue = this.maxVal;
            }
            else if ( value < this.minVal )
            {
                this.itsValue = this.minVal;
            }
            else
            {
                this.itsValue = value;
            }
        }
        else
        {
            this.itsValue = value;
        }

        this.valueSet();
        return;

    } /* IntDataValue::setItsValue() */

    /**
     * @return true if the value equals the default value
     */
    @Override
    public boolean isDefault() {
        return itsValue == ItsDefault;
    }


    /*************************************************************************/
    /*************************** Overrides: **********************************/
    /*************************************************************************/

    /**
     * constructEmptyArg()  Override of abstract method in FormalArgument
     *
     * Return an instance of IntDataValue initialized as appropriate for
     * an argument that has not had any value assigned to it by the user.
     *
     * Changes:
     *
     *    - None.
     */

     public DataValue constructEmptyArg()
        throws SystemErrorException
     {

         return new IntDataValue(this.getDB(), this.getID());

     } /* IntFormalArg::constructEmptyArg() */


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
    @Override
    public String toString()
    {
        return ("" + this.itsValue);
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
    @Override
    public String toDBString()
    {
        return ("(IntDataValue (id " + this.getID() +
                ") (itsFargID " + this.itsFargID +
                ") (itsFargType " + this.itsFargType +
                ") (itsCellID " + this.itsCellID +
                ") (itsValue " + this.itsValue +
                ") (subRange " + this.subRange +
                ") (minVal " + this.minVal +
                ") (maxVal " + this.maxVal + "))");
    }


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
    @Override
    public void updateForFargChange(boolean fargNameChanged,
                                    boolean fargSubRangeChanged,
                                    boolean fargRangeChanged,
                                    FormalArgument oldFA,
                                    FormalArgument newFA)
        throws SystemErrorException
    {
        final String mName = "IntDataValue::updateForFargChange(): ";

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

    } /* IntDataValue::updateForFargChange() */


    /**
     * updateSubRange()
     *
     * Determine if the formal argument associated with the data value is
     * subranged, and if it is, updates the data values representation of
     * the subrange (if ant) accordingly.  In passing, coerce the value of
     * the datavalue into the subrange if necessary.
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
    @Override
    protected void updateSubRange(FormalArgument fa)
        throws SystemErrorException
    {
        final String mName = "IntDataValue::updateSubRange(): ";

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");
        }

        if ( fa instanceof IntFormalArg )
        {
            IntFormalArg ifa = (IntFormalArg)fa;

            this.subRange = ifa.getSubRange();

            if ( this.subRange )
            {
                this.maxVal = ifa.getMaxVal();
                this.minVal = ifa.getMinVal();

                if ( minVal >= maxVal )
                {
                    throw new SystemErrorException(mName + "minVal >= maxVal");
                }

                if ( this.itsValue > this.maxVal )
                {
                    this.itsValue = this.maxVal;
                }
                else if ( this.itsValue < this.minVal )
                {
                    this.itsValue = this.minVal;
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

    } /* IntDataValue::updateSubRange() */


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

    public long coerceToRange(long value)
    {
        if ( this.subRange )
        {
            if ( value > this.maxVal )
            {
                return maxVal;
            }
            else if ( value < this.minVal )
            {
                return minVal;
            }
        }

        return value;

    } /* IntDataValue::coerceToRange() */


    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Construct an instance of IntDataValue with the specified initialization.
     *
     * Returns a reference to the newly constructed IntDataValue if successful.
     * Throws a system error exception on failure.
     *
     *                                               -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static IntDataValue Construct(Database db,
                                         long i)
        throws SystemErrorException
    {
        final String mName = "IntDataValue::Construct(db, i)";
        IntDataValue idv = null;

        idv = new IntDataValue(db);

        idv.setItsValue(i);

        return idv;

    } /* IntDataValue::Construct(db, i) */

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += HashUtils.Long2H(itsValue) * Constants.SEED1;
        hash += HashUtils.Long2H(maxVal) * Constants.SEED2;
        hash += HashUtils.Long2H(minVal) * Constants.SEED3;

        return hash;
    }


    /**
     * Compares this FloatDataValue against another object.
     *
     * @param obj The object to compare this against.
     *
     * @return true if the Object obj is logically equal to this FloatDataValue.
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
        IntDataValue i = (IntDataValue) obj;
        return i.itsValue == this.itsValue
            && i.maxVal == this.maxVal
            && i.minVal == this.minVal
            && super.equals(obj);
    }

} /* IntDataValue */
