/*
 * TimeStampFormalArg.java
 *
 * Created on February 11, 2007, 11:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Class TimeStampFormalArg
 *
 * Intance of this class are used for formal arguments which have been strongly
 * typed to time stamps.
 *
 * @author mainzer
 */
public class TimeStampFormalArg extends FormalArgument
{

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**
     *
     * subRange: Boolean flag indicating whether the formal argument can be
     *      replaced by any time stamp, or only by some time stamp that lies
     *      within the closed interval defined by the minVal and maxVal fields
     *      discussed below
     *
     * minVal:  Timestamp field used to specify the minimum time stamp that
     *      can be used to replace the formal argument if subRange is true.
     *      If subRange is false, this field is ignored and should be set to
     *      null.
     *
     * maxVal:  Timestamp field used to specify the maximum time stamp that
     *      can be used to replace the formal argument if subRange is
     *      true.  If subRange is false, this field is ignored and should be
     *      set to null.
     */

    boolean subRange = false;
    TimeStamp minVal = null;
    TimeStamp maxVal = null;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * TimeStampFormalArg()
     *
     * Constructors for time stamp typed formal arguments.
     *
     * Four versions of this constructor -- one that takes a Database reference
     * as its only parameter, one that takes a database reference and a formal
     * argument name as a parameters, and one that takes a Database reference,
     * a formal argument name, minimum value, and maximum value as parameters,
     * and finally one that take an instance of TimeStampFormalArg as its only
     * parameter and returs a copy.
     *
     *                                          JRM -- 2/12/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public TimeStampFormalArg(Database db)
        throws SystemErrorException
    {

        super(db);

        this.fargType = fArgType.TIME_STAMP;

    } /* TimeStampFormalArg() -- one parameter */

    public TimeStampFormalArg(Database db,
                              String name)
        throws SystemErrorException
    {
        super(db, name);

        this.fargType = fArgType.TIME_STAMP;

    } /* TimeStampFormalArg() -- two parameter s*/

    public TimeStampFormalArg(Database db,
                              String name,
                              TimeStamp minVal,
                              TimeStamp maxVal)
          throws SystemErrorException
    {
        super(db, name);

        final String mName = "TimeStampFormalArg::TimeStampFormalArg(): ";

        this.fargType = fArgType.TIME_STAMP;

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
        else if ( ( ! ( minVal instanceof TimeStamp ) ) ||
                  ( ! ( maxVal instanceof TimeStamp ) ) )
        {
            /* I'm not sure this can happen, but check it anyway */

            throw new SystemErrorException(mName +
                                           "minVal or maxVal not a TimeStamp");
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

    public TimeStampFormalArg(TimeStampFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        this.fargType = fArgType.TIME_STAMP;

        // copy over fields.
        this.setRange(fArg.getMinVal(), fArg.getMaxVal());

    } /* TimeStampFormalArg() -- make copy */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * setRange()
     *
     * Set the range of legal values that this formal arguement can assume.
     *
     * If the new minVal and maxVal are null, set subRange to false.
     *
     * Otherwise, set subRange to true, and set the new minVal and maxVal.
     *
     *                                          JRM -- 2/5/07
     *
     * Changes:
     *
     *    - None.
     *
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
        else if ( ( ! ( minVal instanceof TimeStamp ) ) ||
                  ( ! ( maxVal instanceof TimeStamp ) ) )
        {
            /* I'm not sure this can happen, but check it anyway */

            throw new SystemErrorException(mName +
                                           "minVal or maxVal not a TimeStamp");
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

    /**
     * getSubRange(), getMinVal(), and getMaxVal()
     *
     * Accessor routines used to obtain the current values of the subRange,
     * minVal, and maxVal fields.
     *                                          JRM -- 2/5/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public boolean getSubRange()
    {
        return subRange;
    }

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


    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/

    /**
     * constructArgWithSalvage()  Override of abstract method in FormalArgument
     *
     * Return an instance of TimeStampDataValue initialized from salvage if
     * possible, and to the default for newly created instances of
     * TimeStampDataValue otherwise.
     *
     * Changes:
     *
     *    - None.
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


    /**
     * constructEmptyArg()  Override of abstract method in FormalArgument
     *
     * Return an instance of TimeStampDataValue initialized as appropriate for
     * an argument that has not had any value assigned to it by the user.
     *
     * Changes:
     *
     *    - None.
     */

     public DataValue constructEmptyArg()
        throws SystemErrorException
     {

         return new TimeStampDataValue(this.getDB(), this.getID());

     } /* TimeStampFormalArg::constructEmptyArg() */


    /**
     * toDBString() -- Override of abstract method in DataValue
     *
     * Returns a database String representation of the DBValue for comparison
     * against the database's expected value.<br>
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


    /**
     * isValidValue() -- Override of abstract method in FormalArgument
     *
     * Boolean metho that returns true iff the provided value is an acceptable
     * value to be assigned to this formal argument.
     *
     *                                             JRM -- 2/5/07
     *
     * Changes:
     *
     *    - None.
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

} /* class TimeStampFormalArg */
