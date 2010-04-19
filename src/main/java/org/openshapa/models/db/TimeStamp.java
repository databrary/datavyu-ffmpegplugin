package org.openshapa.models.db;

import java.text.NumberFormat;
import org.openshapa.util.Constants;
import org.openshapa.util.HashUtils;

/**
 * This is the timestamp primitive class.
 *
 * Changes:
 * <ul>
 *   <li>
 *      Changed the name of the class from Timestamp to TimeStamp and reworked
 *      it to refer to ticks per second instead of frames per second.  Neither
 *      of these changes were logically significant -- the first was to
 *      regularize the spelling with the rest of the database code, and the
 *      second was to conform to the historical notion of referring to time
 *      in ticks.
 *
 *      Also added the MIN_TICKS, MAX_TICKS, MIN_TPS, and MAX_TPS class
 *      constants. --2007/02/12
 *   </li>
 *   <li>
 *      Added override of equals() method.  Added comparison methods
 *      (gt(), ge(), lt(), le(), eq(), & ne()).  Added copy constructor.
 *      -- 2007/03/13
 *   </li>
 *   <li>
 *      Reworked the class again.  This time, I turned it into a proper
 *      primative class, so it is no longer a subclass of DataValue.  Also
 *      removed all callback facilities -- supporting such callbacks as are
 *      necessary will now be the responsibility of the containing object.
 *      Similarly, advising the TimeStamp of changes in the number of ticks
 *      per second is also the responsibility of the containing object.
 *      -- 2007/08/18
 *   </li>
 *   <li>
 *      Added the insane_lt() insane_gt() methods -- needed because the Compare
 *      interface specifies compareTo() without this exception, and insists
 *      that I do likewise in my implementation.  Bottom line -- must implement
 *      my own sort and bypass this crap.  But it will have to do for now.
 *      -- 2008/01/22
 *   </li>
 * </ul>
 */
public class TimeStamp {

    /** The minimum number of ticks possible. */
    public static final long MIN_TICKS = 0L;

    /** The maximum number of ticks possible. */
    public static final long MAX_TICKS = Long.MAX_VALUE;

    /** The minimum permitted value of ticks per second. */
    public static final int MIN_TPS = 1;

    /**
     * The maximum permitted value of ticks per second - this value has been
     * chosen somewhat arbitrarily -- it restricts us to no better than a
     * milisecond resolution.  From a database perspective, it can be changed to
     * any positive value.  However, if it is increased beyond 1000, there will
     * be implication for time stamp display in the GUI.
     *
     * @date 2008/02/12
     */
    public static final int MAX_TPS = 1000;

    /** Conversion factor for converting hours to ticks. */
    private static final long HH_TO_TICKS = 3600000;

    /** Array index for hourse. */
    private static final int HH = 0;

    /** Array index for minutes.  */
    private static final int MM = 1;

    /** Array index for seconds. */
    private static final int SS = 2;

    /** Array index for milliseconds. */
    private static final int MMM = 3;

    /** Conversion factor for converting minutes to ticks. */
    private static final long MM_TO_TICKS = 60000;

    /** Conversion factor for converting seconds to ticks. */
    private static final int SS_TO_TICKS = 1000;

    /** Number formatters. **/
    private static final NumberFormatter NUMFORM = new NumberFormatter();

    /** The number of ticks per second for this timestamp. **/
    private int  tps  = 0;

    /** The value of the timestamp in ticks. **/
    private long ticks = 0;


    /**
     * Creates a new instance of Timestamp with the given ticks per second
     * and the given time.
     *
     * @param tps  Ticks per second.
     * @param ticks The value of the timestamp in ticks.
     *
     * @throws SystemErrorException If unable to build the timestamp from the
     * supplied parameters.
     */
    public TimeStamp(int tps, long ticks) throws SystemErrorException {
        this.setTPS(tps);
        this.setTime(ticks);
    }


    /**
     * Creates a timestamp from a string in the format HH:MM:SS:mmm, where
     * HH = hours
     * MM = minutes
     * SS = seconds
     * mmm = milliseconds
     *
     * @param timeStamp A string in the format HH:MM:SS:mmm
     * @throws SystemErrorException If unable to build the timestamp from the
     * supplied string.
     */
    public TimeStamp(final String timeStamp) throws SystemErrorException {
        long parsedTicks = 0;

        String[] timeChunks = timeStamp.split(":");
        parsedTicks += (new Long(timeChunks[HH]) * HH_TO_TICKS);
        parsedTicks += (new Long(timeChunks[MM]) * MM_TO_TICKS);
        parsedTicks += (new Long(timeChunks[SS]) * SS_TO_TICKS);
        parsedTicks += (new Long(timeChunks[MMM]));

        this.setTPS(SS_TO_TICKS);
        this.setTime(parsedTicks);
    }


    /**
     * Copy constructor - creates a new timestamp from an existing timestamp.
     *
     * @param t The timestamp to copy.
     * @throws SystemErrorException If unable to create a copy of the supplied
     * timestamp.
     */
    public TimeStamp(TimeStamp t) throws SystemErrorException {
        super();

        final String mName = "TimeStamp::TimeStamp(): ";

        if ( t == null ) {
          throw new SystemErrorException(mName + "t is null");
        }

        this.setTPS(t.getTPS());
        this.setTime(t.getTime());
    }


    /**
     * Creates a new instance of Timestamp with the given ticks per second.
     * Defaults timestamp to 0 ticks;
     * @param tps Ticks per second.
     */
    public TimeStamp(int tps)
        throws SystemErrorException
    {
        this(tps, 0);
    } //End of TimeStamp() constructor


    /**
     * @return the timestamp value in ticks.
     */
    public long getTime()
    {
        return (this.ticks);
    } //End of getTime() method


    /**
     * Sets the current timestamp value.
     *
     * @param time The timestamp value in ticks.
     *
     * @throws SystemErrorException If the supplied ticks are outside the valid
     * range.
     */
    public void setTime(long ticks)
        throws SystemErrorException
    {
        final String mName = "TimeStamp::setTime(): ";

        if ( ( ticks < MIN_TICKS ) || ( ticks > MAX_TICKS ) )
        {
            throw new SystemErrorException(mName + "new ticks out of range.");
        }

        this.ticks = ticks;
    } //End of setTime() method


    /**
     * @return the timescale in timescale in ticks per second.
     */
    public int getTPS()
    {
        return (this.tps);
    } //End of getTPS() method


    /**
     * Sets the timestamps timescale in ticks per second.
     * <br/>
     * This will also force a conversion of the current timestamp value to the
     * new ticks per second scale.
     * <br/><br/>
     * <em><b>This could cause loss of precision or rounding errors!</b></em>
     *
     * @param tps Ticks per second.
     *
     * @throws SystemErrorException If the supplied ticks per second is outside
     * the valid range.
     *
     * @see #convertTime(long time, int origTPS, int newTPS)
     */
    public void setTPS(int tps)
        throws SystemErrorException
    {
        final String mName = "TimeStamp::setTPS(): ";

        if ( ( tps < MIN_TPS ) || ( tps > MAX_TPS ) )
        {
            throw new SystemErrorException(mName + "new tps out of range.");
        }
        long newTicks = convertTime(this.ticks, this.tps, tps);
        this.setTime(newTicks);
        this.tps = tps;
    } //End of setTPS() method


    /**
     * Converts the timestamp value from one timescale to another.
     * <br/><br/>
     * <em><b>This could cause loss of precision or rounding errors!</b></em>
     *
     * @param ticks TimeStamp value.
     * @param origTPS Original timescale ticks per second.
     * @param newTPS New timescale ticks per second.
     *
     * @return the converted timestamp value.
     */
    public final static long convertTime(long ticks, int origTPS, int newTPS)
    {
        double d = ((double)ticks*newTPS)/((double)origTPS);
        return (Math.round(d));
    } //End of convertTicks() method


    /**
     * Returns the number of whole hours.
     *
     * @param ticks The time in ticks.
     * @param tps The clock rate in ticks per second.
     *
     * @return the number of whole hours.
     */
    public final static int getHours(int tps, long ticks)
    {
        return ((int)((ticks/tps)/3600));
    } //End of getHours() method

    /**
     * Returns the number of whole hours.
     * <br/>
     * Uses static methods to compute values.
     *
     * @return the number of whole hours.
     *
     * @see #getHours(int tps, long ticks)
     */
    public int getHours()
    {
        return (getHours(this.tps, this.ticks));
    } //End of getHours() method


    /**
     * Returns the number of whole minutes.
     *
     * @param ticks The time in ticks.
     * @param tps The clock rate in ticks per second.
     *
     * @return the numbre of whole minutes
     */
    public final static int getMinutes(int tps, long ticks)
    {
        int hh = getHours(tps, ticks);
        return ((int)(((ticks-(3600*hh*tps))/tps)/60));
    } //End of getMinutes() method


    /**
     * Returns the number of whole minutes.
     * <br/>
     * Uses static methods to compute values.
     * @return the number of whole minutes.
     * @see #getMinutes(int tps, long ticks)
     */
    public int getMinutes()
    {
        return (getMinutes(this.tps, this.ticks));
    } //End of getMinutes() method


    /**
     * Returns the number of whole seconds.
     *
     * @param ticks The time in ticks.
     * @param tps The clock rate in ticks per second.
     *
     * @return the number of whole seconds.
     */
    public final static int getSeconds(int tps, long ticks)
    {
        int hh = getHours(tps, ticks);
        int mm = getMinutes(tps, ticks);
        return ((int)((ticks-(3600*hh*tps)-(60*mm*tps))/tps));
    } //End of getSeconds() method


    /**
     * Returns the number of whole seconds.
     * <br/>
     * Uses static methods to compute values.
     *
     * @return the number of whole seconds.
     * @see #getSeconds(int tps, long ticks)
     */
    public int getSeconds()
    {
        return (getSeconds(this.tps, this.ticks));
    } //End of getSeconds() method


    /**
     * Returns the number of ticks to display in the ticks field of the text
     * representation of the timestamp.  This is simply total ticks modulo
     * the current ticks per second.  It will be a value in the closed
     * interval [0-(tps-1)].
     *
     * @param time The time in ticks.
     * @param tps The clock rate in ticks per second.
     *
     * @return ticks modulo tps.
     */
    public final static int getTicks(int tps, long ticks)
    {
        return ((int)(ticks%tps));
    } //End of getTicks() method


    /**
     * Returns the number of ticks for purposes of displaying the timestamp
     * in text format.  This is simply total ticks modulo ticks per second.  It
     * will be a value in the closed interval [0-(tps-1)].
     * <br/>
     * Uses static methods to compute values.
     *
     * @return the number of ticks (0-(tps-1)).
     * @see #getTicks(int tps, long time)
     */
    public int getTicks()
    {
        return (getTicks(this.tps, this.ticks));
    } //End of getTicks() method


    /**
     * Returns a string representation of the timestamp in hh:mm:ss:ttt format.
     *
     * @param time The time in ticks.
     * @param tps The clock rate in ticks per second.
     * @return a string representation of the timestamp in hh:mm:ss:fff format.
     */
    public final static String toHMSFString(int tps, long time)
    {
        int hh  = getHours(tps, time);
        int mm  = getMinutes(tps, time);
        int ss  = getSeconds(tps, time);
        int ttt = getTicks(tps, time);

        return (NUMFORM.numF2.format(hh) + ":" + NUMFORM.numF2.format(mm)
               + ":" + NUMFORM.numF2.format(ss) + ":"
               + NUMFORM.numF3.format(ttt));
    } //End of toHMSFString() method


    /**
     * Returns a string representation of the timestamp in hh:mm:ss:ttt format.
     * <br/>
     * Uses static methods to compute values.
     * @return a string representation of the timestamp in hh:mm:ss:ttt format.
     * @see #toHMSFString(int tps, long time)
     */
    public String toHMSFString()
    {
        return (toHMSFString(this.tps, this.ticks));
    } //End of toHMSFString() method


    /**
     * Overwrites default toString method.
     *
     * Calls toHMSFString()
     * @see #toHMSFString()
     */
    public String toString()
    {
        return (toHMSFString());
    }


    /**
     * Returns a string representation of the timestamp.
     * @return string representation of timestamp
     */
    public String toTPSString()
    {
        return (this.ticks + " @ " + this.tps);
    } // End of toString() method


    /**
     * Returns a database String representation of the DBValue for comparison
     * against the database's expected value.<br>
     * <i>This function is floatended for debugging purposses.</i>
     * @return the string value.
     */
    public String toDBString() {
        return ("("+this.tps+","+this.toHMSFString()+")");
    } //End of toDBString() method


    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = HashUtils.Long2H(ticks) * Constants.SEED1;
        hash += tps * Constants.SEED2;

        return hash;
    }


    /**
     * Compares this TimeStamp against another object.
     *
     * @param obj The object to compare this against.
     *
     * @return true if the Object obj is logically equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        // Must be this class to be here
        TimeStamp t = (TimeStamp) obj;
        return tps == t.tps && ticks == t.ticks;
    }


    // eq()
    /**
     * Equality testing method for TimeStamps.  Unlike equals(), this method
     * (and all the other TimeStamp comparison mthods) throws a system error
     * if the tps fields of the instances to be compared are not equal.
     *
     * @param t The timestamp that we are comparing this against.
     *
     * @throws SystemErrorException If the supplied timestamp is null or if
     * the ticks per second don't match.
     *
     * @date 2007/03/13
     */

    public boolean eq(TimeStamp t)
        throws SystemErrorException
    {
        final String mName = "TimeStamp::eq(): ";

        if ( t == null )
        {
            throw new SystemErrorException(mName + "t null on entry");
        }
        else if ( t.getTPS() != this.tps )
        {
            throw new SystemErrorException(mName + "t.getTPS()!=this.getTPS()");
        }

        return( this.ticks == t.getTime() );

    } /* TimeStamp::eq() */


    // ge()
    /**
     * Method comparing this TimeStamp with another.  Returns true iff the
     * value of this TimeStamp is greater than or equal to that of the
     * test TimeStamp
     *
     * @param t The timestamp that we are comparing against.
     *
     * @throws SystemErrorException If the supplied timestamp is null if
     * the ticks per second don't match.
     *
     * @date 2007/03/13
     */

    public boolean ge(TimeStamp t)
        throws SystemErrorException
    {
        final String mName = "TimeStamp::ge(): ";

        if ( t == null )
        {
            throw new SystemErrorException(mName + "t null on entry");
        }
        else if ( t.getTPS() != this.tps )
        {
            throw new SystemErrorException(mName + "t.getTPS()!=this.getTPS()");
        }

        return( this.ticks >= t.getTime() );
    } /* TimeStamp::ge() */


    // gt()
    /**
     * Method comparing this TimeStamp with another.  Returns true iff the
     * value of this TimeStamp is greater than that of the test TimeStamp
     *
     * @param t The timestamp that we are comparing against.
     *
     * @throws SystemsErrorException If the supplied timestamp is null of the
     * ticks per second don't match.
     *
     * @date 2007/03/13
     */

    public boolean gt(TimeStamp t)
        throws SystemErrorException
    {
        final String mName = "TimeStamp::gt(): ";

        if ( t == null )
        {
            throw new SystemErrorException(mName + "t null on entry");
        }
        else if ( t.getTPS() != this.tps )
        {
            throw new SystemErrorException(mName + "t.getTPS()!=this.getTPS()");
        }

        return( this.ticks > t.getTime() );

    } /* TimeStamp::gt() */


    // insane_gt()
    /**
     * Just like gt() but without the sanity checks -- needed to implement
     * Comparator.
     *
     * @param t The timestamp we are comparing against.
     *
     * @date 2008/01/22
     */

     public boolean insane_gt(TimeStamp t)
     {
        final String mName = "TimeStamp::insane_gt(): ";

        return( this.ticks > t.getTime() );

     } /* TimeStamp::insane_gt() */


    // le()
    /**
     * Method comparing this TimeStamp with another.  Returns true iff the
     * value of this TimeStamp is less than or equal to that of the
     * test TimeStamp
     *
     * @param t The timestamp we are comparing this against.
     *
     * @throws SystemErrorException If the supplied timestamp is NULL or the
     * ticks per second don't match.
     *
     * @date 2007/03/13
     */

    public boolean le(TimeStamp t)
        throws SystemErrorException
    {
        final String mName = "TimeStamp::le(): ";

        if ( t == null )
        {
            throw new SystemErrorException(mName + "t null on entry");
        }
        else if ( t.getTPS() != this.tps )
        {
            throw new SystemErrorException(mName + "t.getTPS()!=this.getTPS()");
        }

        return( this.ticks <= t.getTime() );

    } /* TimeStamp::le() */


    // lt()
    /**
     * Method comparing this TimeStamp with another.  Returns true iff the
     * value of this TimeStamp is less than that of the test TimeStamp
     *
     * @param t The timestamp we are comparing this agains.
     *
     * @throws SystemErrorException If the supplied timestamp is NULL, or the
     * ticks per second don't match.
     *
     * @date 2007/03/13
     */

    public boolean lt(TimeStamp t)
        throws SystemErrorException
    {
        final String mName = "TimeStamp::lt(): ";

        if ( t == null )
        {
            throw new SystemErrorException(mName + "t null on entry");
        }
        else if ( t.getTPS() != this.tps )
        {
            throw new SystemErrorException(mName + "t.getTPS()!=this.getTPS()");
        }

        return( this.ticks < t.getTime() );

    } /* TimeStamp::lt() */


    // insane_lt()
    /**
     * Just like lt(), but without the sanity checks.  Needed to implement
     * Comparator.
     *
     * @param t The timestamp that we are comparing against.
     *
     * @date 2008/01/22
     */

    public boolean insane_lt(TimeStamp t)
    {
        final String mName = "TimeStamp::insane_lt(): ";

        return( this.ticks < t.getTime() );

    } /* TimeStamp::insane_lt() */


    // ne()
    /**
     * Method comparing this TimeStamp with another.  Returns true iff the
     * value of this TimeStamp is not equal to that of the test TimeStamp
     *
     * This method (and all the other TimeStamp comparison mthods) throws a
     * system error if the tps fields of the instances to be compared are
     * not equal.
     *
     * @param t The timestamp that we are comparing this against.
     *
     * @throws SystemErrorException If the supplied timestamp is NULL, or the
     * ticks per second don't match.
     *
     * @date 2007/03/13
     */

    public boolean ne(TimeStamp t)
        throws SystemErrorException
    {
        final String mName = "TimeStamp::ne(): ";

        if ( t == null )
        {
            throw new SystemErrorException(mName + "t null on entry");
        }
        else if ( t.getTPS() != this.tps )
        {
            throw new SystemErrorException(mName + "t.getTPS()!=this.getTPS()");
        }

        return( this.ticks != t.getTime() );

     } /* TimeStamp::ne() */

} //End of Timestamp class definition

/**
 * This internal class is used to hold the number formatting
 * classes used by all instances of timestamp.
 */
class NumberFormatter
{
  /** Static instances for use accross classes **/
  public static NumberFormat numF2 = NumberFormat.getInstance();
  public static NumberFormat numF3 = NumberFormat.getInstance();

  /**
   * Creates a new instance of NumberFormatter
   */
  public NumberFormatter()
  {
    numF2.setMaximumFractionDigits(0);
    numF2.setMaximumIntegerDigits(2);
    numF2.setMinimumIntegerDigits(2);
    numF3.setMaximumFractionDigits(0);
    numF3.setMaximumIntegerDigits(3);
    numF3.setMinimumIntegerDigits(3);
  } //End of NumberFormatter() constructor
} //End of NumberFormatter class definition
