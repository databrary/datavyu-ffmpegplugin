package org.openshapa.db;

import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;

/**
 * Class TimeStampFormalArg
 *
 * Intance of this class are used for formal arguments which have been strongly
 * typed to time stamps.
 */
public class TimeStampFormalArg extends FormalArgument
{
    /**
     * Boolean flag indicating whether the formal argument can be replaced by
     * any time stamp, or only by some time stamp that lies within the closed
     * interval defined by the minVal and maxVal fields discussed below.
     */
    boolean subRange = false;


    /**
     * Timestamp field used to specify the minimum time stamp that can be used
     * to replace the formal argument if subRange is true. If subRange is false,
     * this field is ignored and should be set to null.
     */
    TimeStamp minVal = null;


    /**
     * Timestamp field used to specify the maximum time stamp that can be used
     * to replace the formal argument if subRange is true.  If subRange is
     * false, this field is ignored and should be set to null.
     */
    TimeStamp maxVal = null;


    /**
     * Constructor.
     *
     * @param db The parent database that this TimeStampFormalArg will belong
     * too.
     *
     * @throws SystemErrorException If the supplied database is NULL.
     *
     * @date 2007/02/12
     */

    public TimeStampFormalArg(Database db)
        throws SystemErrorException
    {

        super(db);

        this.fargType = FArgType.TIME_STAMP;

    } /* TimeStampFormalArg() -- one parameter */


    /**
     * Constructor.
     *
     * @param db The parent database that this TimeStampFormalArg will belong
     * too.
     * @param name The name to use for the new TimeStampFormalArg.
     *
     * @throws SystemErrorException If the supplied database is NULL.
     *
     * @date 2007/02/12
     */

    public TimeStampFormalArg(Database db,
                              String name)
        throws SystemErrorException
    {
        super(db, name);

        this.fargType = FArgType.TIME_STAMP;

    } /* TimeStampFormalArg() -- two parameter s*/


    /**
     * Constructor.
     *
     * @param db The parent database that this TimeStampFormalArg will belong
     * too.
     * @param name The name to use for the TimeStampFormalArg.
     * @param minVal The minimum possible value that can be assigned to this
     * formal argument.
     * @param maxVal The maximum possible value that can be assigned to this
     * formal argument.
     *
     * @throws SystemErrorException If the supplied database is NULL.
     *
     * @date 2007/02/12
     */

    public TimeStampFormalArg(Database db,
                              String name,
                              TimeStamp minVal,
                              TimeStamp maxVal)
          throws SystemErrorException
    {
        super(db, name);

        final String mName = "TimeStampFormalArg::TimeStampFormalArg(): ";

        this.fargType = FArgType.TIME_STAMP;

        if ( ( minVal == null ) && ( maxVal == null ) )
        {
            this.subRange = false;
            this.minVal = null;
            this.maxVal = null;
        }
        else if ( ( minVal == null ) || ( maxVal == null ) )
        {
            throw new SystemErrorException(mName + "minVal xor maxVal is null");
        }
        else if ( minVal.getTPS() != maxVal.getTPS() )
        {
            throw new SystemErrorException(mName + "Inconsistant tick size");
        }
        else if ( ( minVal.getTPS() < TimeStamp.MIN_TPS ) ||
                  ( minVal.getTPS() > TimeStamp.MAX_TPS ) )
        {
            throw new SystemErrorException(mName + "Invalid tick size");
        }
        else if ( ( minVal.getTime() < 0 ) ||
                  ( maxVal.getTime() > Long.MAX_VALUE ) )
        {
            throw new SystemErrorException(mName +
                                           "minVal or maxVal out of range");
        }
        else if ( minVal.getTime() >= maxVal.getTime() )
        {
            throw new SystemErrorException(mName +  "minVal >= maxVal");
        }
        else
        {
            this.subRange = true;
            this.minVal = new TimeStamp(minVal.getTPS(), minVal.getTime());
            this.maxVal = new TimeStamp(maxVal.getTPS(), maxVal.getTime());
        }
    } /* TimeStampFormalArg() -- three parameters */


    /**
     * Copy Constructor.
     *
     * @param fArg The TimeStampFormalArg to copy.
     *
     * @throws SystemErrorException If the supplied formal argument is null, or
     * the parent database of the supplied formal argument is null.
     *
     * @date 2007/02/12
     */

    public TimeStampFormalArg(TimeStampFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        this.fargType = FArgType.TIME_STAMP;

        // copy over fields.
        this.setRange(fArg.getMinVal(), fArg.getMaxVal());

    } /* TimeStampFormalArg() -- make copy */


    // setRange()
    /**
     * Set the range of legal values that this formal arguement can assume.
     *
     * If the new minVal and maxVal are null, set subRange to false.
     *
     * Otherwise, set subRange to true, and set the new minVal and maxVal.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @param minVal The minimum legal value for this TimeStampFormalArg.
     * @param maxVal The maximum legal value for this TimeStampFormalArg.
     *
     * @throws SystemErrorException If the supplied minVal or maxVal timestamps
     * are null or have a different tick system.
     *
     * @date 2007/02/05
     */

    public void setRange(TimeStamp minVal, TimeStamp maxVal)
        throws SystemErrorException
    {
        final String mName = "IntFormalArg::setRange(): ";

        if ( ( minVal == null ) && ( maxVal == null ) )
        {
            this.subRange = false;
            this.minVal = null;
            this.maxVal = null;
        }
        else if ( ( minVal == null ) || ( maxVal == null ) )
        {
            throw new SystemErrorException(mName + "minVal xor maxVal is null");
        }
        else if ( minVal.getTPS() != maxVal.getTPS() )
        {
            throw new SystemErrorException(mName + "Inconsistant tick size");
        }
        else if ( ( minVal.getTPS() < TimeStamp.MIN_TPS ) ||
                  ( minVal.getTPS() > TimeStamp.MAX_TPS ) )
        {
            throw new SystemErrorException(mName + "Invalid tick size");
        }
        else if ( ( minVal.getTime() < 0 ) ||
                  ( maxVal.getTime() > Long.MAX_VALUE ) )
        {
            throw new SystemErrorException(mName +
                                           "minVal or maxVal out of range");
        }
        else if ( minVal.getTime() >= maxVal.getTime() )
        {
            throw new SystemErrorException(mName +  "minVal >= maxVal");
        }
        else
        {
            this.subRange = true;
            this.minVal = new TimeStamp(minVal.getTPS(), minVal.getTime());
            this.maxVal = new TimeStamp(maxVal.getTPS(), maxVal.getTime());
        }

        /* must notify listeners here */

        return;

    } /* IntFormalArg::setRange() */


    // getSubRange()
    /**
     * @return True if the TimeStampFormalArg has a defined minimum and maximum
     * range, false otherwise.
     *
     * @date 2007/02/05
     */

    public boolean getSubRange()
    {
        return subRange;
    }


    // getMinVal()
    /**
     * @return A copy of the minimum possible value for this TimeStampFormalArg.
     *
     * @throws SystemErrorException If unable to create a copy of the minimum
     * timestamp value.
     *
     * @date 2007/02/05
     */

    public TimeStamp getMinVal()
        throws SystemErrorException
    {
        if ( this.minVal == null )
        {
            return null;
        }
        else
        {
            return new TimeStamp(this.minVal);
        }
    }


    // getMaxVal()
    /**
     * @return A copy of the maximum possible value for this TimeStampFormalArg.
     *
     * @throws SystemErrorException If unable to create a copy of the maximum
     * timestamp value.
     *
     * @date 2007/02/05
     */

    public TimeStamp getMaxVal()
        throws SystemErrorException
    {
        if ( this.minVal == null )
        {
            return null;
        }
        else
        {
            return new TimeStamp(this.maxVal);
        }
    }


    // constructArgWithSalvage() - Override of abstract method in FormalArgument
    /**
     * Builds a instance of DataValue initialized from salvage.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @param The DataValue to salvage when constructing a formal argument.
     *
     * @return An a instance of DataValue initialized from salvage.
     * Return an instance of TimeStampDataValue initialized from salvage if
     * possible, and to the default for newly created instances of
     * TimeStampDataValue otherwise.
     *
     * @throws SystemErrorException If unable to create a data value.
     */

    DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException
    {
        TimeStampDataValue retVal;

        if ( ( salvage == null ) ||
             ( salvage.getItsFargID() == DBIndex.INVALID_ID ) )
        {
            retVal = new TimeStampDataValue(this.getDB(), this.getID());
        }
        else if ( salvage instanceof IntDataValue )
        {
            retVal = new TimeStampDataValue(this.getDB(), this.getID(),
                                       new TimeStamp(getDB().getTicks(),
                                       ((IntDataValue)salvage).getItsValue()));
        }
        else if ( ( salvage instanceof TimeStampDataValue ) &&
                  ( this.getDB().IsValidTimeStamp(
                            ((TimeStampDataValue)salvage).getItsValue()) ) )
        {
            retVal = new TimeStampDataValue(this.getDB(), this.getID(),
                            ((TimeStampDataValue)salvage).getItsValue());
        }
        else
        {
            retVal = new TimeStampDataValue(this.getDB(), this.getID());
        }

        return retVal;

    } /* TimeStampDataValue::constructArgWithSalvage(salvage) */


    // constructEmptyArg()  Override of abstract method in FormalArgument
    /**
     * @return An instance of TimeStampDataValue initialized as appropriate for
     * an argument that has not had any value assigned to it by the user.
     */

     public DataValue constructEmptyArg()
        throws SystemErrorException
     {

         return new TimeStampDataValue(this.getDB(), this.getID());

     } /* TimeStampFormalArg::constructEmptyArg() */


    // toDBString()
    /**
     * Returns a database String representation of the DBValue for comparison
     * against the database's expected value.<br>
     *
     * <i>This function is intended for debugging purposses.</i>
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @return A database string representation of the TimeStampFormalArg.
     */

    public String toDBString() {

        if ( subRange )
        {
            return ("(TimeStampFormalArg " + getID() + " " +
                    getFargName() + " " + subRange + " " +
                    minVal.toDBString() + " " + maxVal.toDBString() +")");
        }
        else
        {
            return ("(TimeStampFormalArg " + getID() + " " + getFargName() +
                    " " + subRange + " " + "null" + " " + "null" +")");
        }

    } /* TimeStampFormalArg::toDBString() */


    // isValidValue() -- Override of abstract method in FormalArgument
    /**
     * Checks if the supplied object is valid value to be assigned to this
     * formal argument.
     *
     * Changes:
     * <ul>
     *   <li>
     *     None.
     *   </li>
     * </ul>
     *
     * @param Object The object to check if it is a valid value.
     *
     * @return True if the supplied object can be assigned to this
     * TimeStampFormalArg, false otherwise.
     *
     * Boolean metho that returns true iff the provided value is an acceptable
     * value to be assigned to this formal argument.
     *
     * @throws SystemErrorException If the ticks per second of the supplied
     * object fails to match the min and max permitted values.
     *
     * @date 2007/02/05
     */

    public boolean isValidValue(Object obj)
        throws SystemErrorException
    {
        final String mName = "IntFormalArg::isValidValue(): ";

        if ( ! Database.IsValidTimeStamp(obj) )
        {
            return false;
        }

        /* If we get this far, obj must be a time stamp */

        TimeStamp t = (TimeStamp)obj;

        if ( subRange )
        {
            if ( ( t.getTPS() != minVal.getTPS() ) ||
                 ( t.getTPS() != maxVal.getTPS() ) )
            {
                throw new SystemErrorException(mName +  "TPS mismatch");
            }
            else if ( ( t.getTime() < minVal.getTime() ) ||
                      ( t.getTime() > maxVal.getTime() ) )
            {
                return false;
            }
        }

        return true;

    } /* TimeStampFormalArg::isValidValue() */


    // hashCode()
    /**
     * @return A hash code value for the object.
     */

    @Override
    public int hashCode() {
        int hash = super.hashCode() * Constants.SEED1;
        hash += (new Boolean(subRange)).hashCode() * Constants.SEED2;
        hash += HashUtils.Obj2H(minVal) * Constants.SEED3;
        hash += HashUtils.Obj2H(maxVal) * Constants.SEED4;

        return hash;
    }


    // equals()
    /**
     * Compares this TimeStampFormalArg against another object.
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

        TimeStampFormalArg tsfa = (TimeStampFormalArg) obj;
        return super.equals(obj) && subRange == tsfa.subRange
               && (minVal == null ? tsfa.minVal == null
                                  : minVal.equals(tsfa.minVal))
               && (maxVal == null ? tsfa.maxVal == null
                                  : maxVal.equals(tsfa.maxVal));
    }
} /* class TimeStampFormalArg */
