package org.openshapa.models.db.legacy;

import java.io.IOException;
import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;
import org.openshapa.util.StringUtils;

/**
 * An instance of TimeStampDataValue is used to store a time stamp value
 * assigned to a formal argument.
 */
public final class TimeStampDataValue extends DataValue
{
    /**
     * Constant containing the value to be assigned to all time stamp data
     * values unless otherwise specified.  Note that we only take the ticks from
     * this value -- ticks per second is drawn from the current value in the
     * host database.
     */
    final TimeStamp ItsDefault = new TimeStamp(Database.DEFAULT_TPS, 0);

    /**
     * TimeStamp containing the value assigned to the formal argument.
     */
    TimeStamp itsValue = new TimeStamp(ItsDefault);

    /**
     * If subRange is true, this field contains the minimum value that may be
     * assigned to the formal argument associated with the data value.  This
     * value should always be the same as the minVal of the associated instance
     * of TimeStampFormalArg.
     *
     * To save space, when subRange is false, this field should be null.
     *
     * Note that this data value may be used to hold an time stamp value
     * assigned to an untype formal argument, in which case subrange will always
     * be false.
     */
    TimeStamp minVal = null;

    /**
     * If subRange is true, this field contains the maximum value that may be
     * assigned to the formal argument associated with the data value.  This
     * value should always be the same as the maxVal of the associated instance
     * of TimeStampFormalArg.
     *
     * To save space, when subRange is false, this field should be null.
     *
     * Note that this data value may be used to hold an integer value assigned
     * to an untype formal arhasgument, in which case subrange will always be
     * false.
     */
    TimeStamp maxVal = null;


    /**
     * Constructor.
     *
     * @param db The parent database for this TimeStampDataValue.
     *
     * @throws SystemErrorException If the supplied database is NULL.
     *
     * @date 2007/08/16
     */
    public TimeStampDataValue(Database db)
        throws SystemErrorException
    {
        super(db);

        this.itsValue.setTPS(db.getTicks());

    } /* TimeStampDataValue::TimeStampDataValue(db) */


    /**
     * Constructor.
     *
     * @param db The parent database for this TimeStampDataValue.
     * @param fargID The ID of the parent formal argument that this DataValue is
     * assigned too.
     *
     * @throws SystemErrorException If the supplied database is NULL.
     *
     * @date 2007/08/16
     */

    public TimeStampDataValue(Database db,
                              long fargID)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

    } /* TimeStampDataValue::TimeStampDataValue(db, fargID) */


    /**
     * Constructor.
     *
     * @param db The parent database for this TimeStampDataValue.
     * @param fargID The ID of the parent formal argument that this DataValue is
     * assigned too.
     * @param value The value to use for this TimeStampDataValue.
     *
     * @throws SystemErrorException If the supplied database is NULL.
     *
     * @date 2007/08/16
     */

    public TimeStampDataValue(Database db,
                              long fargID,
                              TimeStamp value)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

        this.setItsValue(value);

        // It is possible that this time stamp is coming from another
        // database, possibly with a different tps -- thus set the
        // tps to match that of the current data base.  This will almost
        // always be redundant, but better safe then sorry.
        this.itsValue.setTPS(db.getTicks());

    } /* TimeStampDataValue::TimeStampDataValue(db, fargID, value) */


    /**
     * Copy Constructor.
     *
     * @param dv The TimeStampDataValue to copy.
     *
     * @throws SystemErrorException If the supplied TimeStampDataValue is NULL
     * or the parent database of the supplied TimeStampDataValue is NULL.
     *
     * @date 2007/08/16
     */

    public TimeStampDataValue(TimeStampDataValue dv)
        throws SystemErrorException
    {
        super(dv);

        this.itsValue  = new TimeStamp(dv.itsValue);

        // make sure that the tps is correct
        this.itsValue.setTPS(getDB().getTicks());

        if ( this.subRange )
        {
            this.minVal = new TimeStamp(dv.maxVal);
            this.maxVal = new TimeStamp(dv.maxVal);
        }
        else
        {
            this.minVal = null;
            this.maxVal = null;
        }

    } /* TimeStampDataValue::TimeStampDataValue(dv) */

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
        TimeStampDataValue clone = (TimeStampDataValue) super.clone();

        try {
            clone = new TimeStampDataValue(this);
        } catch (SystemErrorException e) {
            clone = null;
        }

        return clone;
    }


    // getItsValue()
    /**
     * @return A copy of the current value of the data value.
     *
     * @throws SystemErrorException If Unable to create a copy of the value
     * stored in this TimeStampDataValue.
     *
     * @date 2007/08/16
     */

    public TimeStamp getItsValue()
        throws SystemErrorException
    {

        return new TimeStamp(this.itsValue);

    } /* TimeStampDataValue::getItsValue() */


    // setItsValue()
    /**
     * Set itsValue to the specified value.  If subrange is true, coerce the
     * value into the subrange.  Note that the time base of the supplied
     * value must equal the time base of the current value of the data value.
     *
     * Observe that we don't bother to verify that the supplied value is in
     * range for a TimeStamp.  The TimeStamp class will throw a system error
     * if this is the case.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @param value The new value to use for this TimeStampDataValue.
     *
     * @throws SystemErrorException If the supplied value is NULL or the
     * supplied time stamp has a mismatched ticks per second value.
     *
     * @param 2007/08/16
     */

    public void setItsValue(TimeStamp value)
        throws SystemErrorException
    {
        final String mName = "TimeStampDataValue::setItsValue(): ";

        if ( value == null )
        {
            throw new SystemErrorException(mName + "value is null");
        }

        if ( value.getTPS() != this.itsValue.getTPS() )
        {
            throw new SystemErrorException(mName + "value.TPS != itsValue.TPS");
        }

        if ( this.subRange )
        {
            if ( ( this.minVal == null ) || ( this.maxVal == null ) )
            {
                throw new SystemErrorException(mName +
                        "subRange && ((minVal == null) || (maxVal == null))");
            }

            if ( ( this.itsValue.getTPS() != this.minVal.getTPS() ) ||
                 ( this.itsValue.getTPS() != this.maxVal.getTPS() ) )
            {
                throw new SystemErrorException(mName + "TPS mismatch");
            }

            if ( this.maxVal.lt(value) )
            {
                this.itsValue.setTime(this.maxVal.getTime());
            }
            else if ( this.minVal.gt(value) )
            {
                this.itsValue.setTime(this.minVal.getTime());
            }
            else
            {
                this.itsValue.setTime(value.getTime());
            }
        }
        else
        {
            this.itsValue.setTime(value.getTime());
        }

        this.valueSet();
        return;

    } /* TimeStampDataValue::setItsValue() */


    // isDefault()
    /**
     * @return true if the value equals the default value
     */

    @Override
    public boolean isDefault() {
        return itsValue.equals(ItsDefault);
    } /* TimeStampDataValue::isDefault() */


    // toString()
    /**
     * @returns A String representation of the DBValue for display.
     *
     * @date 2007/08/15
     */

    public String toString()
    {

        return this.itsValue.toString();

    } /* TimeStampDataValue::toString() */

    public String toEscapedString() {
        return StringUtils.escapeCSV(this.itsValue.toString());

    }

    // toDBString()
    /**
     * Returns a database String representation of the DBValue for comparison
     * against the database's expected value.<br>
     * <i>This function is intended for debugging purposses.</i>
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @return The TimeStampDataValue in a database string format.
     *
     * @date 2007/08/15
     */

    public String toDBString()
    {
        if ( this.subRange )
        {
            return ("(TimeStampDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + this.itsValue.toDBString() +
                    ") (subRange " + this.subRange +
                    ") (minVal " + this.minVal.toDBString() +
                    ") (maxVal " + this.maxVal.toDBString() + "))");
        }
        else
        {
            return ("(TimeStampDataValue (id " + this.getID() +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + this.itsValue.toDBString() +
                    ") (subRange " + this.subRange + "))");
        }
    } /* TimeStampDataValue::toDBString() */


    // toMODBFile()
    /**
     * Write the MacSHAPA ODB file style definition of itsValue to the
     * supplied file in MacSHAPA ODB file format.
     *
     * The output of this method will an instantiation of <time_stamp>
     * (as defined in the grammar defining the MacSHAPA ODB file format).
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @param output The stream that that this TimeStampDataValue is being
     * dumped too (in MacSHAPA ODB file format).
     *
     * @throws SystemErrorException If the supplied output is NULL or the value
     * of the TimeStampDataValue is zero.
     * @throws IOException If unable to write to the output stream.
     *
     * @date 2009/01/24
     */

    protected void toMODBFile(java.io.PrintStream output)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "TimeStampDataValue::toMODBFile()";

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( this.itsValue == null )
        {
            throw new SystemErrorException(mName + "itsValue is null");
        }

        output.printf("( TIME> %d ) ", this.itsValue.getTime());

        return;

    } /* TimeStampDataValue::toMODBFile() */


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
     * @param fargNameChanged true if the formal argument name changed.
     * @param fargSubRangeChanged true if the formal argument sub range changed.
     * @param fargRangeChanged true if the formal argument range changed.
     * @param oldFA The formal argument pre the change.
     * @param newFA the formal argument post the change.
     *
     * @throws SystemErrorException if oldFA or newFA are null.
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
        final String mName = "TimeStampDataValue::updateForFargChange(): ";

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

    } /* TimeStampDataValue::updateForFargChange() */


    // updateSubRange()
    /**
     * Determine if the formal argument associated with the data value is
     * subranged, and if it is, updates the data values representation of
     * the subrange (if any) accordingly.  In passing, coerce the value of
     * the datavalue into the subrange if necessary.
     *
     * @param fa A reference to the current representation of the formal
     * argument associated with the data value.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @date 2007/08/16
     *
     * @throws SystemErrorException if the supplied FA is NULL.
     */

    protected void updateSubRange(FormalArgument fa)
        throws SystemErrorException
    {
        final String mName = "TimeStampDataValue::updateSubRange(): ";

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");
        }

        if ( fa instanceof TimeStampFormalArg )
        {
            TimeStampFormalArg tsfa = (TimeStampFormalArg)fa;

            this.subRange = tsfa.getSubRange();

            if ( this.subRange )
            {
                this.maxVal = tsfa.getMaxVal();
                this.minVal = tsfa.getMinVal();

                if ( this.minVal.ge(this.maxVal) )
                {
                    throw new SystemErrorException(mName + "minVal >= maxVal");
                }

                if ( ( this.maxVal.getTPS() != this.maxVal.getTPS() ) ||
                     ( this.maxVal.getTPS() != this.itsValue.getTPS() ) )
                {
                    throw new SystemErrorException(mName + "TPS mis-match");
                }

                if ( this.itsValue.gt(this.maxVal) )
                {
                    this.itsValue.setTime(this.maxVal.getTime());
                }
                else if ( this.itsValue.lt(this.minVal) )
                {
                    this.itsValue.setTime(this.minVal.getTime());
                }
            }
            else
            {
                this.minVal = null;
                this.maxVal = null;
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

    } /* TimeStampDataValue::updateSubRange() */


    // coerceToRange()
    /**
     * If the supplied value is in range for the associated formal argument,
     * simply return it.  Otherwise, coerce it to the nearest value that is
     * in range.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @param value The TimeStamp value to coerce to the nominated range.
     *
     * @date 2007/08/15
     */

    public TimeStamp coerceToRange(TimeStamp value)
        throws SystemErrorException
    {
        final String mName = "TimeStampDataValue::coerceToRange(): ";

        if ( value == null )
        {
            throw new SystemErrorException(mName + "value null on entry");
        }

        if ( value.getTPS() != this.itsValue.getTPS() )
        {
            throw new SystemErrorException(mName + "TPS mismatch 1");
        }

        if ( this.subRange )
        {
            if ( ( this.minVal == null ) || ( this.maxVal == null ) )
            {
                throw new SystemErrorException(mName +
                        "subRange && ((minVal == null) || (maxVal == null))");
            }

            if ( ( value.getTPS() != this.minVal.getTPS() ) ||
                 ( value.getTPS() != this.maxVal.getTPS() ) )
            {
                throw new SystemErrorException(mName + "TPS mismatch 2");
            }

            if ( this.maxVal.lt(value) )
            {
                return new TimeStamp(this.maxVal);
            }
            else if ( this.minVal.gt(value) )
            {
                return new TimeStamp(this.minVal);
            }
        }

        return value;

    } /* TimeStampDataValue::coerceToRange() */


    // Construct()
    /**
     * Construct an instance of TimeStampDataValue with the specified
     * initialization.  Use the supplied database to provide tps.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @return a Reference to the newly constructed TimeStampDataValue if
     * successful.  Throws a system error exception on failure.
     *
     * @throws SystemErrorException If unable to create a TimeStampDataValue.
     *
     * @date 2008/03/31
     */

    public static TimeStampDataValue Construct(Database db,
                                               long ticks)
        throws SystemErrorException
    {
        final String mName = "TimeStampDataValue::Construct(db, ticks)";
        TimeStampDataValue tsdv = null;

        tsdv = new TimeStampDataValue(db);

        tsdv.setItsValue(new TimeStamp(db.getTicks(), ticks));

        return tsdv;

    } /* TimeStampDataValue::Construct(db, ticks) */


    /**
     * @return A hash code value for the object.
     */

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += HashUtils.Obj2H(itsValue) * Constants.SEED1;
        hash += HashUtils.Obj2H(maxVal) * Constants.SEED2;
        hash += HashUtils.Obj2H(minVal) * Constants.SEED3;
        hash += HashUtils.Obj2H(ItsDefault) * Constants.SEED4;

        return hash;
    }


    /**
     * Compares this TimeStampDataValue against another object.
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
        TimeStampDataValue i = (TimeStampDataValue) obj;
        return (itsValue == i.itsValue
                || (itsValue != null && itsValue.equals(i.itsValue)))
            && (maxVal == i.maxVal
                || (maxVal != null && maxVal.equals(i.maxVal)))
            && (minVal == i.minVal
                || (minVal != null && minVal.equals(i.minVal)))
            && (ItsDefault == i.ItsDefault
                || (ItsDefault != null && ItsDefault.equals(i.ItsDefault)))
            && super.equals(obj);
    }

} /* TimeStampDataValue */
