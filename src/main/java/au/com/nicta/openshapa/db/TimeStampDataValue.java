/*
 * TimeStampDataValue.java
 *
 * Created on August 19, 2007, 6:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * An instance of TimeStampDataValue is used to store a time stamp value
 * assigned to a formal argument.
 *
 * @author mainzer
 */
public final class TimeStampDataValue extends DataValue
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * itsDefault:  Constant containing the value to be assigned to all
     *      time stamp data values unless otherwise specified.  Note that we
     *      only take the ticks from this value -- ticks per second is drawn
     *      from the current value in the host database.
     *
     * itsValue:   TimeStamp containing the value assigned to the formal
     *      argument.
     *
     * minVal:  If subRange is true, this field contains the minimum value
     *      that may be assigned to the formal argument associated with the
     *      data value.   This value should always be the same as the minVal
     *      of the associated instance of TimeStampFormalArg.
     *
     *      To save space, when subRange is false, this field should be null.
     *
     *      Note that this data value may be used to hold an time stamp
     *      value assigned to an untype formal argument, in which case
     *      subrange will always be false.
     *
     * maxVal:  If subRange is true, this field contains the maximum value
     *      that may be assigned to the formal argument associated with the
     *      data value.   This value should always be the same as the maxVal
     *      of the associated instance of TimeStampFormalArg.
     *
     *      To save space, when subRange is false, this field should be null.
     *
     *      Note that this data value may be used to hold an integer
     *      value assigned to an untype formal arhasgument, in which case
     *      subrange will always be false.
     */

    /** default value for time stamps */
    final TimeStamp ItsDefault = new TimeStamp(Database.DEFAULT_TPS, 0);

    /** the value assigned to the associated formal argument in this case */
    TimeStamp itsValue = new TimeStamp(ItsDefault);

    /** the minimum value -- if subrange is true.  null otherwise */
    TimeStamp minVal = null;

    /** the maximum value -- if subrange is true.  null otherwise */
    TimeStamp maxVal = null;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * TimeStampDataValue()
     *
     * Constructor for instances of TimeStampDataValue.
     *
     * Four versions of this constructor.
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
     * The fourth takes a reference to an instance of TimeStampDataValue as an
     * argument, and uses it to create a copy.
     *
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public TimeStampDataValue(Database db)
        throws SystemErrorException
    {
        super(db);

        this.itsValue.setTPS(db.getTicks());

    } /* TimeStampDataValue::TimeStampDataValue(db) */

    public TimeStampDataValue(Database db,
                              long fargID)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

    } /* TimeStampDataValue::TimeStampDataValue(db, fargID) */

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

    public TimeStamp getItsValue()
        throws SystemErrorException
    {

        return new TimeStamp(this.itsValue);

    } /* TimeStampDataValue::getItsValue() */

    /**
     * setItsValue()
     *
     * Set itsValue to the specified value.  If subrange is true, coerce the
     * value into the subrange.  Note that the time base of the supplied
     * value must equal the time base of the current value of the data value.
     *
     * Observe that we don't bother to verify that the supplied value is in
     * range for a TimeStamp.  The TimeStamp class will throw a system error
     * if this is the case.
     *
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
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

        return;

    } /* TimeStampDataValue::setItsValue() */


    /*************************************************************************/
    /*************************** Overrides: **********************************/
    /*************************************************************************/

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

        return this.itsValue.toString();

    } /* TimeStampDataValue::toString() */


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


    /**
     * updateSubRange()
     *
     * Determine if the formal argument associated with the data value is
     * subranged, and if it is, updates the data values representation of
     * the subrange (if any) accordingly.  In passing, coerce the value of
     * the datavalue into the subrange if necessary.
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


    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /**
     * Construct()
     *
     * Construct an instance of TimeStampDataValue with the specified
     * initialization.  Use the supplied database to provide tps.
     *
     * Returns a reference to the newly constructed TimeStampDataValue if
     * successful.  Throws a system error exception on failure.
     *
     *                                              JRM -- 3/31/08
     *
     * Changes:
     *
     *    - None.
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

    /** Seed value for generating hash codes. */
    private final static int SEED1 = 3;
    /** Seed value for generating hash codes. */
    private final static int SEED2 = 7;
    /** Seed value for generating hash codes. */
    private final static int SEED3 = 11;
    /** Seed value for generating hash codes. */
    private final static int SEED4 = 13;

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += itsValue == null ? 0 : itsValue.hashCode() * SEED1;
        hash += maxVal == null ? 0 : maxVal.hashCode() * SEED2;
        hash += minVal == null ? 0 : minVal.hashCode() * SEED3;
        hash += ItsDefault == null ? 0 : ItsDefault.hashCode() * SEED4;

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
