/*
 * TimeStamp.java
 *
 * Created on November 9, 2006, 3:41 PM
 *
 */

package au.com.nicta.openshapa.db;

/**
 * This is the timestamp primitive class.
 *
 * @author FGA
 *
 * Changes:
 *
 *    - Changed the name of the class from Timestamp to TimeStamp and reworked
 *      it to refer to ticks per second instead of frames per second.  Neither
 *      of these changes were logically significant -- the first was to 
 *      regularize the spelling with the rest of the database code, and the 
 *      second was to conform to the historical notion of referring to time
 *      in ticks.  
 *
 *      Also added the MIN_TICKS, MAX_TICKS, MIN_TPS, and MAX_TPS class 
 *      constants.
 *
 *                                              JRM -- 2/12/07
 *
 *    - Added override of equals() method.  Added comparison methods 
 *      (gt(), ge(), lt(), le(), eq(), & ne()).  Added copy constructor.
 *
 *                                              JRM -- 3/13/07
 *
 *    - Reworked the class again.  This time, I turned it into a proper 
 *      primative class, so it is no longer a subclass of DataValue.  Also
 *      removed all callback facilities -- supporting such callbacks as are 
 *      necessary will now be the responsibility of the containing object.
 *      Similarly, advising the TimeStamp of changes in the number of ticks
 *      per second is also the responsibility of the containing object.
 *
 *                                              JRM -- 8/18/07
 *
 *    - Added the insane_lt() insane_gt() methods -- needed because the Compare
 *      interface specifies compareTo() without this exception, and insists
 *      that I do likewise in my implementation.  Bottom line -- must implement
 *      my own sort and bypass this crap.  But it will have to do for now.
 *
 *                                              JRM -- 1/22/08
 */
public class TimeStamp
{
  /** Class constants **/
    
  static final long MIN_TICKS = 0L;
  static final long MAX_TICKS = Long.MAX_VALUE;
  
  /* the value for MAX_TPS has been chosen somewhat arbitrarily -- it restricts
   * us to no better than a milisecond resolution.  From a database perspective,
   * it can be changed to any positive value.  However, if it is increased 
   * beyond 1000, there will be implication for time stamp display in the GUI.
   *
   *                                            JRM -- 2/12/07
   */
  static final int MIN_TPS = 1;
  static final int MAX_TPS = 1000;
  
  
  /** Number formatters **/
  private final static NumberFormatter NUMFORM = new NumberFormatter();
  
  /** The number of ticks per second for this timestamp **/
  private int  tps  = 0;

  /** The value of the timestamp in ticks **/
  private long ticks = 0;

  /**
   * Creates a new instance of Timestamp with the given ticks per second
   * and the given time.
   * @param tps  Ticks per second.
   * @param ticks The value of the timestamp in ticks.
   */
  public TimeStamp(int tps, long ticks)
        throws SystemErrorException
  {
    this.setTPS(tps);
    this.setTime(ticks);
  } //End of TimeStamp() constructor
  
  public TimeStamp(TimeStamp t)
        throws SystemErrorException
  {
    super();
 
    final String mName = "TimeStamp::TimeStamp(): ";  
        
    if ( t == null )
    {
      throw new SystemErrorException(mName + "t is null");
    }
    else if ( ! ( t instanceof TimeStamp ) )
    {
      throw new SystemErrorException(mName + "t not a TimeStamp");
    }
     
    this.setTPS(t.getTPS());
    this.setTime(t.getTime());
    
  } /* TimeStamp::TimeStamp() -- copy constructor */
  
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
   * Returns the timestamp value in ticks.
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
     
     // Notify the listeners of the value change
     // this.notifyListeners(); -- now the responsibility of the containing object
     // todo -- delete this eventually
    
   } //End of setTime() method

  /**
   * Returns the timestamp timescale in ticks per second.
   * @return the timescale in tps.
   */
   public int getTPS()
   {
     return (this.tps);
   } //End of getTPS() method

   /**
   * Sets the timestamps timescale in ticks per second.
   * <br/>
   * This will also force a conversion of the current timestamp value
   * to the new ticks per second scale.
   * <br/><br/>
   * <em><b>This could cause loss of precision or rounding errors!</b></em>
   *
   *  @param tps Ticks per second.
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
     // Notify the listeners of the value change
     // this.notifyListeners(); -- now the responsibility of the containing object
     // todo -- delete the above eventually
   } //End of setTPS() method

  /**
   * Converts the timestamp value from one timescale to another.
   * <br/><br/>
   * <em><b>This could cause loss of precision or rounding errors!</b></em>
   *
   *  @param ticks    TimeStamp value.
   *  @param origTPS  Original timescale ticks per second.
   *  @param newTPS   New timescale ticks per second.
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
    * @return the number of whole hours.
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

     return (NUMFORM.numF2.format(hh) + ":" + NUMFORM.numF2.format(mm) + ":" +
         NUMFORM.numF2.format(ss) + ":" + NUMFORM.numF3.format(ttt));
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
    * --FGA 07/18/07
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
   * Returns a database String representation of the DBValue for comparison against
   * the database's expected value.<br>
   * <i>This function is floatended for debugging purposses.</i>
   * @return the string value.
   */
  public String toDBString()
  {
    return ("("+this.tps+","+this.toHMSFString()+")");
  } //End of toDBString() method

   /**
    * Main method for testing purposes.
    */
   public final static void main (String[] argv)
        throws SystemErrorException
   {
     TimeStamp t1 = new TimeStamp(30, 333);
     System.out.println(t1 + "\t" + t1.toHMSFString());
     t1.setTPS(60);
     System.out.println(t1 + "\t" + t1.toHMSFString());
     t1.setTPS(25);
     System.out.println(t1 + "\t" + t1.toHMSFString());
     t1.setTPS(30);
     System.out.println(t1 + "\t" + t1.toHMSFString());
     t1.setTPS(120);
     System.out.println(t1 + "\t" + t1.toHMSFString());
     t1.setTPS(1000);
     System.out.println(t1 + "\t" + t1.toHMSFString());
   } //End of main() method

     
        
    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/
   
    /**
     * equals() -- Override of Object::equals()
     *
     * Return true iff the supplied object is equal to this.
     *
     * Changes:
     *
     *    - None.
     */
   
     public boolean equals(Object obj)
     {
         boolean equal = true;
     
         if ( obj == null )
         {
             equal = false;
         }
         else if ( ! ( obj instanceof TimeStamp ) )
         {
             equal =  false;
         }
         else if ( ( obj != this ) && 
                    ( ( this.tps != ((TimeStamp)obj).getTPS()) ||
                      ( this.ticks != ((TimeStamp)obj).getTime() ) ) )
         {
             equal = false;
         }
         return equal;
         
     } /* TimeStamp::equals() */

     
    /*************************************************************************/
    /************************* Comparison Methods: ***************************/
    /*************************************************************************/
  
    /**
     * eq()
     *
     * Equality testing method for TimeStamps.  Unlike equals(), this method 
     * (and all the other TimeStamp comparison mthods) throws a system error 
     * if the tps fields of the instances to be compared are not equal.
     *
     *                                      JRM -- 3/13/07
     *
     * Changes:
     *
     *    - None.
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
            throw new SystemErrorException(mName + "t.getTPS() != this.getTPS()");
         }
        
        return( this.ticks == t.getTime() );
        
     } /* TimeStamp::eq() */
   
  
    /**
     * ge()
     *
     * Method comparing this TimeStamp with another.  Returns true iff the
     * value of this TimeStamp is greater than or equal to that of the 
     * test TimeStamp
     * 
     * This method (and all the other TimeStamp comparison mthods) throws a 
     * system error if the tps fields of the instances to be compared are
     * not equal.
     *
     *                                      JRM -- 3/13/07
     *
     * Changes:
     *
     *    - None.
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
            throw new SystemErrorException(mName + "t.getTPS() != this.getTPS()");
         }
        
        return( this.ticks >= t.getTime() );
        
     } /* TimeStamp::ge() */
   
  
    /**
     * gt()
     *
     * Method comparing this TimeStamp with another.  Returns true iff the
     * value of this TimeStamp is greater than that of the test TimeStamp
     * 
     * This method (and all the other TimeStamp comparison mthods) throws a 
     * system error if the tps fields of the instances to be compared are
     * not equal.
     *
     *                                      JRM -- 3/13/07
     *
     * Changes:
     *
     *    - None.
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
            throw new SystemErrorException(mName + "t.getTPS() != this.getTPS()");
         }
        
        return( this.ticks > t.getTime() );
        
     } /* TimeStamp::gt() */
   
  
    /**
     * insane_gt()
     *
     * Just like gt() but without the sanity checks -- needed to implement 
     * Comparator.
     *
     *
     *                                      JRM -- 1/22/08
     *
     * Changes:
     *
     *    - None.
     */
  
     public boolean insane_gt(TimeStamp t)
     {
        final String mName = "TimeStamp::insane_gt(): ";

        return( this.ticks > t.getTime() );
        
     } /* TimeStamp::insane_gt() */
   
  
    /**
     * le()
     *
     * Method comparing this TimeStamp with another.  Returns true iff the
     * value of this TimeStamp is less than or equal to that of the 
     * test TimeStamp
     * 
     * This method (and all the other TimeStamp comparison mthods) throws a 
     * system error if the tps fields of the instances to be compared are
     * not equal.
     *
     *                                      JRM -- 3/13/07
     *
     * Changes:
     *
     *    - None.
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
            throw new SystemErrorException(mName + "t.getTPS() != this.getTPS()");
         }
        
        return( this.ticks <= t.getTime() );
        
     } /* TimeStamp::le() */
   
  
    /**
     * lt()
     *
     * Method comparing this TimeStamp with another.  Returns true iff the
     * value of this TimeStamp is less than that of the test TimeStamp
     * 
     * This method (and all the other TimeStamp comparison mthods) throws a 
     * system error if the tps fields of the instances to be compared are
     * not equal.
     *
     *                                      JRM -- 3/13/07
     *
     * Changes:
     *
     *    - None.
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
            throw new SystemErrorException(mName + "t.getTPS() != this.getTPS()");
         }
        
        return( this.ticks < t.getTime() );
        
     } /* TimeStamp::lt() */
   
  
    /**
     * insane_lt()
     *
     * Just like lt(), but without the sanity checks.  Needed to implement
     * Comparator.
     *
     *
     *                                      JRM -- 1/22/08
     *
     * Changes:
     *
     *    - None.
     */
  
     public boolean insane_lt(TimeStamp t)
     {
        final String mName = "TimeStamp::insane_lt(): ";
        
        return( this.ticks < t.getTime() );
        
     } /* TimeStamp::insane_lt() */
   
   
  
    /**
     * ne()
     *
     * Method comparing this TimeStamp with another.  Returns true iff the
     * value of this TimeStamp is not equal to that of the test TimeStamp
     * 
     * This method (and all the other TimeStamp comparison mthods) throws a 
     * system error if the tps fields of the instances to be compared are
     * not equal.
     *
     *                                      JRM -- 3/13/07
     *
     * Changes:
     *
     *    - None.
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
            throw new SystemErrorException(mName + "t.getTPS() != this.getTPS()");
         }
        
        return( this.ticks != t.getTime() );
        
     } /* TimeStamp::ne() */

    
    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

     /**
     * VerifyTimeStampCopy()
     *
     * Verify that the supplied instances of TimeStamp are distinct, 
     * that they contain no common references, and that they 
     * represent the same value.
     *                                              JRM -- 12/1/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyTimeStampCopy(TimeStamp base,
                                          TimeStamp copy,
                                          java.io.PrintStream outStream,
                                          boolean verbose,
                                          String baseDesc,
                                          String copyDesc)
    {
        int failures = 0;
 
        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyPredicateCopy: %s null on entry.\n", 
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyPredicateCopy: %s null on entry.\n", 
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
        else if ( base.tps != copy.tps )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.tps == %d != %s.tps == %d.\n", 
                                 baseDesc, base.tps, copyDesc, copy.tps);
            }
        }
        else if ( base.ticks != copy.ticks )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.ticks == %d != %s.ticks == %d.\n", 
                                 baseDesc, base.ticks, copyDesc, copy.ticks);
            }
        }

        return failures;

    } /* TimeStamp::VerifyTimeStampCopy() */
   
     
} //End of Timestamp class definition

/**
 * This internal class is used to hold the number formatting
 * classes used by all instances of timestamp.
 *
 * @author FGA
 */
class NumberFormatter
{
  /** Static instances for use accross classes **/
  public static java.text.NumberFormat numF2 = java.text.NumberFormat.getInstance();
  public static java.text.NumberFormat numF3 = java.text.NumberFormat.getInstance();  

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
