package org.openshapa.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;

/**
 * An instance of UndefinedDataValue is used as a place holder for an untyped
 * formal argument until a value is assigned.
 */
public final class UndefinedDataValue extends DataValue {

    /** String containing the name of the formal argument. */
    String itsValue = "<val>";

    /**
     * Constructor
     *
     * @param db The database that this undefined data value belongs too.
     *
     * @date 2007/08/16
     */
    public UndefinedDataValue(Database db)
        throws SystemErrorException
    {
        super(db);
    }

    /**
     * Constructor.
     *
     * @param db The database that this undefined data value belongs too.
     * @param fargID The ID for the formal argument that this DataValue resides
     * within.
     * @param value The value of the Undefined Data Value.
     *
     * @throws SystemErrorException If unable to create the Undefined Data
     * Value.
     *
     * @date 2007/08/16
     *
     * Changes:
     *
     *    - Deleted value parameter.    -- 9/27/09
     */

    public UndefinedDataValue(Database db,
                              long fargID)
        throws SystemErrorException
    {
        super(db);

        final String mName =
                "UndefinedDataValue::UndefinedDataValue(db, fargID, value): ";

        this.setItsFargID(fargID);

    } /* UndefinedDataValue(db, fargID, value) */


    /**
     * Copy Constructor.
     *
     * @param dv The UndefinedDataValue to clone.
     * @throws SystemErrorException If unable to create a copy of the supplied
     * UndefinedDataValue.
     *
     * @date 2007/08/16
     */

    public UndefinedDataValue(UndefinedDataValue dv)
        throws SystemErrorException
    {
        super(dv);

        this.itsValue  = new String(dv.itsValue);

    }

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
        UndefinedDataValue clone = (UndefinedDataValue) super.clone();

        try {
            clone = new UndefinedDataValue(this);
        } catch (SystemErrorException e) {
            clone = null;
        }

        return clone;
    }

    /**
     * @return A string containing a copy of the current value of the data
     * value.
     *
     * @date 2007/08/16
     */
    public String getItsValue()
    {
        return new String(this.itsValue);
    } /* UndefinedDataValue::getItsValue() */

    /**
     * Set itsValue to the specified value.  In the case of an undefined
     * data value, the value must be the name of the associated untyped
     * formal argument, or any valid formal argument name if itsFargID
     * is undefined.
     *
     * Changes:
     * <ul>
     *   <li>
     *     With the advent of column predicates and the prospect of
     *     implementing the old MacSHAPA query language in OpenSHAPA,
     *     the requirement that undefined data values only be used to
     *     replace untyped formal arguments is removed. -- 2008/12/12
     *   </li>
     *   <li>
     *     Able to set values of undefined datavalues to things other than
     *     valid formal arguments. --2009/06/09
     *   </li>
     * </ul>
     *
     * @param value The value to use with this undefined data value.
     *
     * @throws SystemErrorException If unable to set the value to specified
     * input.
     *
     * @date 2007/08/16
     */
    public void setItsValue(String value)
        throws SystemErrorException
    {
        final String mName = "UndefinedDataValue::setItsValue(): ";

        if ( this.itsFargID != DBIndex.INVALID_ID )
        {
            DBElement dbe;
            FormalArgument fa;

            if ( itsFargType == FormalArgument.FArgType.UNDEFINED )
            {
                throw new SystemErrorException(mName +
                                               "itsFargType == UNDEFINED");
            }

            dbe = this.getDB().idx.getElement(this.itsFargID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName +
                                               "itsFargID has no referent");
            }

            if ( ! ( dbe instanceof FormalArgument ) )
            {
                throw new SystemErrorException(mName +
                        "itsFargID doesn't refer to a formal arg");
            }

            fa = (FormalArgument)dbe;

            if ( fa.getFargName().compareTo(value) != 0 )
            {
                throw new SystemErrorException(mName +
                        "value doesn't match farg name");
            }
        }

        this.valueSet();
        this.itsValue = new String(value);

        return;
    }

    /**
     * @return true if the value equals the default value
     */
    @Override
    public boolean isDefault() {
        return true;
    }


    // toString()
    /**
     * @return A String representation of the DBValue for display.
     *
     * @date 2007/08/15
     */

    public String toString()
    {
        return new String(this.itsValue);
    }


    // toDBString()
    /**
     * @return a database String representation of the DBValue for comparison
     * against the database's expected value.<br> <i>This function is intended
     * for debugging purposses.</i>
     *
     * @date 2007/08/15
     */

    public String toDBString()
    {
        return ("(UndefinedDataValue (id " + this.getID() +
                ") (itsFargID " + this.itsFargID +
                ") (itsFargType " + this.itsFargType +
                ") (itsCellID " + this.itsCellID +
                ") (itsValue " + new String(this.itsValue) +
                ") (subRange " + this.subRange + "))");
    }


    // toMODBFile()
    /**
     * Write the MacSHAPA ODB file style definition of itsValue to the
     * supplied file in MacSHAPA ODB file format.
     *
     * The output of this method will an instantiation of <formal_arg>
     * (as defined in the grammar defining the MacSHAPA ODB file format).
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @param output The stream that that this UndefinedDataValue is being
     * dumped too (in MacSHAPA ODB file format).
     *
     * @date 2009/24/01
     */

    protected void toMODBFile(java.io.PrintStream output)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "UndefinedDataValue::toMODBFile()";

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( this.itsValue == null )
        {
            throw new SystemErrorException(mName + "itsValue is null");
        }

        output.printf("|%s| ", this.itsValue);

        return;

    } /* UndefinedDataValue::toMODBFile() */


    // updateForFargChange()
    /**
     * Update for a change in the formal argument name, and/or subrange.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @param fargNameChanged Has the formal argument name changed?
     * @param fargSubRangeChanged Has the formal argument sub-range changed?
     * @param fargRangeChanged Has the formal argument range changed?
     * @param oldFA The old formal argument (pre change).
     * @param newFA The new formal argument (post change).
     *
     * @date 2008/03/22
     */

    public void updateForFargChange(boolean fargNameChanged,
                                    boolean fargSubRangeChanged,
                                    boolean fargRangeChanged,
                                    FormalArgument oldFA,
                                    FormalArgument newFA)
        throws SystemErrorException
    {
        final String mName = "UndefinedDataValue::updateForFargChange(): ";

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

        if ( fargNameChanged )
        {
            this.setItsValue(newFA.getFargName());
        }

        return;
        
    } /* UndefinedDataValue::updateForFargChange() */
    


    // updateSubRange()
    /**
     * Nominally, this function should determine if the formal argument
     * associated with the data value is subranged, and if it is, update
     * the data values representation of the subrange (if any) accordingly.  
     * In passing, it would coerce the value of the datavalue into the subrange
     * if necessary.  Typically, this happens when the range of the underlying
     * formal argument changes, but it also applies when the data value is 
     * assigned a formal argument ID.
     *
     * The concept doesn't really fit undefined data values, however if we
     * observe that the value of an undefined data value must always be the
     * name of the associated formal argument (if any), a reasonable
     * approximation comes to mind.
     *
     * Specifically, in this method we force the value of the undefined data
     * value to equal the name of the specified formal argument.
     * the data values representation of the subrange (if any) accordingly.
     * In passing, it would coerce the value ofthe datavalue into the subrange
     * if necessary.
     *
     * This is meaningless for an undefine data value, as it never has a
     * value, and it is only associated with untyped formal arguments.
     *
     * Note that this is a complete re-write of this method, and that this
     * change was occasioned by the decision to allow undefined data values
     * to be assigned to formal arguments of all types.
     *
     * The original date of this method was 8/16/07, but given the complete
     * re-write, I am changing it to the current date
     *
     * @param fa  A reference to the current representation of the
     * formal argument associated with the data value.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @date 2009/06/09
     */

    protected void updateSubRange(FormalArgument fa)
        throws SystemErrorException
    {
        final String mName = "UndefinedDataValue::updateSubRange(): ";

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");
        }
        
        this.setItsValue(fa.getFargName());

        return;

    } /* UndefinedDataValue::updateSubRange() */


    // coerceToRange()
    /**
     * The value of an UndefinedDataValue must be a valid formal argument name.
     * In addition, if the data value is associated with a formal argument
     * (always an UnTypedFormalArgument), its value must be the name of the
     * formal argument.
     *
     * Thus, coerce to the name of the associated UnTypedFormalArg if defined.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @throws SystemErrorException When the value is not a valid formal
     * argument name.
     *
     * @date 2007/08/15
     */

    public String coerceToRange(String value) throws SystemErrorException {
        final String mName = "UndefinedDataValue::coerceToRange(): ";

        if (!getDB().IsValidFargName(value)) {
            throw new SystemErrorException(mName + 
                    "value not a valid formal argument name");
        }

        if (this.itsFargID != DBIndex.INVALID_ID) {
            DBElement dbe = this.getDB().idx.getElement(this.itsFargID);

            if (dbe == null) {
                throw new SystemErrorException(mName +
                                               "itsFargID has no referent");
            }

            FormalArgument fa = (FormalArgument) dbe;

            if (fa.getFargName().compareTo(value) != 0) {
                return new String(fa.getFargName());
            }
        }

        return value;
    } /* UndefinedDataValue::coerceToRange() */

    /**
     * Compares this UndefinedDataValue against another object.
     *
     * @param obj The object to compare this against.
     *
     * @return true if the Object obj is logically equal to this
     * UndefinedDataValue, or false otherwise.
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
        UndefinedDataValue u = (UndefinedDataValue) obj;
        return u.itsValue.equals(this.itsValue) && super.equals(obj);
    }

    /**
     * @return A hash value for this object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += HashUtils.Obj2H(itsValue) * Constants.SEED1;

        return hash;
    }


    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Construct instance of FloatDataValue with the specified initialization.
     *
     * Return reference to the newly constructed FloatDataValue if successful.
     * Throws a system error exception on failure.
     *
     *                                               -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static UndefinedDataValue Construct(Database db,
                                               String s)
        throws SystemErrorException
    {
        final String mName = "UndefinedDataValue::Construct(db, s)";
        UndefinedDataValue udv = null;

        udv = new UndefinedDataValue(db);

        udv.setItsValue(s);

        return udv;

    } /* UndefinedDataValue::Construct(db, s) */

} /* UndefinedDataValue */
