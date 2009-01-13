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
     *      value assigned to an untype formal argument, in which case
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
        this.itsValue.setTPS(db.getTicks());

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
            return ("(TimeStampDataValue (id " + this.id +
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
            return ("(TimeStampDataValue (id " + this.id +
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

    /**
     * @return A hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += this.itsValue.hashCode() * SEED1;
        hash += this.maxVal.hashCode() * SEED2;
        hash += this.minVal.hashCode() * SEED3;

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
            && super.equals(obj);
    }


    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) One argument constructor:
     *
     *      a) Construct a database.  Using this database, call the one
     *         argument constructor for TimeStampDataValue.  Verify that all
     *         fields are set to the expected defaults.
     *
     *      b) Verify that the one argument constructor fails on invalid
     *         input.  Given the compiler checks, this probably just means
     *         verifying that the constructor fails on null.
     *
     * 2) Two argument constructor:
     *
     *      a) Construct a database, and a mve (matrix vocab element) with one
     *         formal argument.  Insert the mve into the database, and make
     *         note of the IDs assigned to them (including the formal argument).
     *
     *         Construct a TimeStampDataValue for the formal argument of the mve
     *         by passing a reference to the database and the id of the formal
     *         argument.  Verify that the TimeStampDataValue's itsFargID,
     *         itsFargType, subRange, minVal, and maxVal fields matches
     *         thos of the formal argument, and that all other fields are set
     *         to the expected defaults.
     *
     *         Repeat for a variety of formal argument types and settings.
     *
     *      b) Verify that the constructor fails when passed and invalid
     *         db or an invalid mve id.
     *
     * 3) Three argument constructor:
     *
     *      As per two argument constructor, save that a value is supplied
     *      to the constructor.  Verify that this value appears in the
     *      TimeStampDataValue -- perhaps after havign been modified to match
     *      the subrange.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and possibly a mve (matrix vocab element)
     *         and such formal arguments as are necessary.  If an mve is
     *         created, insert it into the database, and make note of the IDs
     *         assigned.  Then create a  TimeStampDataValue (possibly using
     *         the using a formal argument ID).
     *
     *         Now use the copy constructor to create a copy of the
     *         TimeStampDataValue, and verify that the copy is correct.
     *
     *         Repeat the test for a variety of instances of FloatFormalArg.
     *
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad
     *         value that has to be tested.
     *
     * 5) Accessors:
     *
     *      Verify that the getItsValue(), setItsValue() and coerceToRange()
     *      methods perform correctly.  Verify that the inherited accessors
     *      function correctly via calls to the DataValue.TestAccessors()
     *      method.
     *
     *      Given compiler error checking, there isn't any way to feed
     *      invalid data to the getItsValue(), setItsValue() and coerceToRange()
     *
     * 6) toString methods:
     *
     *      Verify that all fields are displayed correctly by the toString
     *      and toDBString() methods.
     *
     *
     *************************************************************************/

    /**
     * TestClassTimeStampDataValue()
     *
     * Main routine for tests of class TimeStampDataValue.
     *
     *                                      JRM -- 10/15/07
     *
     * Changes:
     *
     *    - Non.
     */

    public static boolean TestClassTimeStampDataValue(
                                                java.io.PrintStream outStream,
                                                boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;

        outStream.print("Testing class TimeStampDataValue:\n");

        if ( ! Test1ArgConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! Test2ArgConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! Test3ArgConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestCopyConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestAccessors(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestToStringMethods(outStream, verbose) )
        {
            failures++;
        }

        if ( failures > 0 )
        {
            pass = false;
            outStream.printf(
                    "%d failures in tests for class TimeStampDataValue.\n\n",
                    failures);
        }
        else
        {
            outStream.print(
                    "All tests passed for class TimeStampDataValue.\n\n");
        }

        return pass;

    } /* TimeStampDataValue::TestClassTimeStampDataValue() */


    /**
     * Test1ArgConstructor()
     *
     * Run a battery of tests on the one argument constructor for this
     * class, and on the instance returned.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test1ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing 1 argument constructor for class TimeStampDataValue      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        TimeStampDataValue tsdv = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        tsdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();
            tsdv = new TimeStampDataValue(db);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( tsdv == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( tsdv == null )
                {
                    outStream.print(
                            "new TimeStampDataValue(db) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "new TimeStampDataValue(db) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new TimeStampDataValue(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.Verify1ArgInitialization(db, tsdv, outStream,
                                                           verbose);

            if ( ! tsdv.itsValue.eq(tsdv.ItsDefault) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv.itsValue = (%d,%d) != " +
                            "tsdv.ItsDefault = (%d,%d).\n",
                            tsdv.itsValue.getTPS(),
                            tsdv.itsValue.getTicks(),
                            tsdv.ItsDefault.getTPS(),
                            tsdv.ItsDefault.getTicks());
                }
            }

            if ( tsdv.maxVal != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv.maxVal: %d.\n",
                                     tsdv.maxVal);
                }
            }

            if ( tsdv.minVal != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv.minVal: %d.\n",
                                     tsdv.minVal);
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            tsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv = new TimeStampDataValue((Database)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "new TimeStampDataValue(null) returned.\n");
                    }

                    if ( tsdv != null )
                    {
                        outStream.print("new TimeStampDataValue(null) " +
                                        "returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(null) failed " +
                                "to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* TimeStampDataValue::Test1ArgConstructor() */


    /**
     * Test2ArgConstructor()
     *
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instance returned.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test2ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing 2 argument constructor for class TimeStampDataValue      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement ts_mve = null;
        MatrixVocabElement ts_mve_sr = null;
        TimeStampFormalArg tsfa = null;
        TimeStampFormalArg tsfa_sr = null;
        TimeStampDataValue tsdv = null;
        TimeStampDataValue tsdv_sr = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            ts_mve = new MatrixVocabElement(db, "ts_mve");
            ts_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa = new TimeStampFormalArg(db);
            ts_mve.appendFormalArg(tsfa);
            db.vl.addElement(ts_mve);

            tsdv = new TimeStampDataValue(db, tsfa.getID());

            ts_mve_sr = new MatrixVocabElement(db, "ts_mve_sr");
            ts_mve_sr.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa_sr = new TimeStampFormalArg(db);
            tsfa_sr.setRange(new TimeStamp(db.getTicks(), 10 * db.getTicks()),
                         new TimeStamp(db.getTicks(), 60 * 60 * db.getTicks()));
            ts_mve_sr.appendFormalArg(tsfa_sr);
            db.vl.addElement(ts_mve_sr);

            tsdv_sr = new TimeStampDataValue(db, tsfa_sr.getID());

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( ts_mve == null ) ||
             ( tsfa == null ) ||
             ( tsdv == null ) ||
             ( ts_mve_sr == null ) ||
             ( tsfa_sr == null ) ||
             ( tsdv_sr == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( ts_mve == null )
                {
                    outStream.print("allocation of ts_mve failed.\n");
                }

                if ( tsfa == null )
                {
                    outStream.print("allocation of tsfa failed.");
                }

                if ( tsdv == null )
                {
                    outStream.print("new TimeStampDataValue(db, tsfa.getID())" +
                                    "returned null.\n");
                }

                if ( ts_mve_sr == null )
                {
                    outStream.print("allocation of ts_mve_sr failed.\n");
                }

                if ( tsfa_sr == null )
                {
                    outStream.print("allocation of tsfa_sr failed.");
                }

                if ( tsdv_sr == null )
                {
                    outStream.print("new TimeStampDataValue(db, " +
                            "tsfa_sr.getID()) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("Test failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "Test threw a system error exception: \"%s\"",
                            systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               tsfa,
                                                               tsdv,
                                                               outStream,
                                                               verbose,
                                                              "tsdv");

            if ( tsdv.subRange != tsfa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv.subRange doesn't match tsfa.getSubRange().\n");
                }
            }

            if ( ! tsdv.itsValue.eq(tsdv.ItsDefault) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv.itsValue = (%d,%d) != " +
                            "tsdv.ItsDefault = (%d,%d).\n",
                            tsdv.itsValue.getTPS(),
                            tsdv.itsValue.getTicks(),
                            tsdv.ItsDefault.getTPS(),
                            tsdv.ItsDefault.getTicks());
                }
            }

            if ( tsdv.maxVal != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv.maxVal: " +
                            "(%d,%d) (null expected).\n",
                            tsdv.maxVal.getTPS(), tsdv.maxVal.getTicks());
                }
            }

            if ( tsdv.minVal != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv.minVal: " +
                            "(%d,%d) (null expected).\n",
                            tsdv.minVal.getTPS(), tsdv.minVal.getTicks());
                }
            }

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               tsfa_sr,
                                                               tsdv_sr,
                                                               outStream,
                                                               verbose,
                                                               "tsdv_sr");

            if ( tsdv_sr.subRange != tsfa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv_sr.subRange doesn't match " +
                                     "tsfa_sr.getSubRange().\n");
                }
            }

            if ( tsdv_sr.itsValue.ne(tsfa_sr.getMinVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv_sr.itsValue = (%d,%d) != " +
                            "tsfa_sr.getMinVal() = (%d,%d).\n",
                            tsdv_sr.itsValue.getTPS(),
                            tsdv_sr.itsValue.getTicks(),
                            tsfa_sr.getMinVal().getTPS(),
                            tsfa_sr.getMinVal().getTicks());
                }
            }

            if ( tsdv_sr.maxVal.ne(tsfa_sr.getMaxVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of fdv_sr.maxVal: " +
                            "(%d,%d), (%d,%d) expected.\n",
                            tsdv_sr.maxVal.getTPS(),
                            tsdv_sr.maxVal.getTicks(),
                            tsfa_sr.getMaxVal().getTPS(),
                            tsfa_sr.getMaxVal().getTicks());
                }
            }

            if ( tsdv_sr.minVal.ne(tsfa_sr.getMinVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of fdv_sr.minVal: " +
                            "(%d,%d), (%d,%d) expected.\n",
                            tsdv_sr.minVal.getTPS(),
                            tsdv_sr.minVal.getTicks(),
                            tsfa_sr.getMinVal().getTPS(),
                            tsfa_sr.getMinVal().getTicks());
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            tsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv = new TimeStampDataValue((Database)null, tsfa.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(null, " +
                                        "tsfa.getID()) returned.\n");
                    }

                    if ( tsdv != null )
                    {
                        outStream.print("new TimeStampDataValue(null, " +
                                        "tsfa.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(null, tsfa.getID())" +
                                " failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            tsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv = new TimeStampDataValue(db, DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                                        "INVALID_ID) returned.\n");
                    }

                    if ( tsdv != null )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                                        "INVALID_ID) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new TimeStampDataValue(db, INVALID_ID)" +
                                " failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an ID that does not
         *refer to a formal argument.
         */
        if ( failures == 0 )
        {
            tsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv = new TimeStampDataValue(db, ts_mve.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                                        "ts_mve.getID()) returned.\n");
                    }

                    if ( tsdv != null )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                                "ts_mve.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new TimeStampDataValue(db, ts_mve.getID()) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* TimeStampDataValue::Test2ArgConstructor() */


    /**
     * Test3ArgConstructor()
     *
     * Run a battery of tests on the three argument constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test3ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing 3 argument constructor for class TimeStampDataValue      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement ts_mve = null;
        MatrixVocabElement ts_mve_sr = null;
        TimeStampFormalArg tsfa = null;
        TimeStampFormalArg tsfa_sr = null;
        TimeStampDataValue tsdv = null;
        TimeStampDataValue tsdv_sr0 = null;
        TimeStampDataValue tsdv_sr1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        tsdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            ts_mve = new MatrixVocabElement(db, "ts_mve");
            ts_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa = new TimeStampFormalArg(db);
            ts_mve.appendFormalArg(tsfa);
            db.vl.addElement(ts_mve);

            tsdv = new TimeStampDataValue(db, tsfa.getID(),
                                          new TimeStamp(db.getTicks(), 60));

            ts_mve_sr = new MatrixVocabElement(db, "ts_mve_sr");
            ts_mve_sr.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa_sr = new TimeStampFormalArg(db);
            tsfa_sr.setRange(new TimeStamp(db.getTicks(), 0),
                         new TimeStamp(db.getTicks(), 60 * 60 * db.getTicks()));
            ts_mve_sr.appendFormalArg(tsfa_sr);
            db.vl.addElement(ts_mve_sr);

            tsdv_sr0 = new TimeStampDataValue(db, tsfa_sr.getID(),
                     new TimeStamp(db.getTicks(), 60 * db.getTicks()));
            tsdv_sr1 = new TimeStampDataValue(db, tsfa_sr.getID(),
                  new TimeStamp(db.getTicks(), (60 * 60 * db.getTicks()) + 1));

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( ts_mve == null ) ||
             ( tsfa == null ) ||
             ( tsdv == null ) ||
             ( ts_mve_sr == null ) ||
             ( tsfa_sr == null ) ||
             ( tsdv_sr0 == null ) ||
             ( tsdv_sr1 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( ts_mve == null )
                {
                    outStream.print("allocation of ts_mve failed.\n");
                }

                if ( tsfa == null )
                {
                    outStream.print("allocation of tsfa failed.");
                }

                if ( tsdv == null )
                {
                    outStream.print("allocation of tsdv failed.\n");
                }

                if ( ts_mve_sr == null )
                {
                    outStream.print("allocation of ts_mve_sr failed.\n");
                }

                if ( tsfa_sr == null )
                {
                    outStream.print("allocation of tsfa_sr failed.");
                }

                if ( tsdv_sr0 == null )
                {
                    outStream.print("allocation of tsdv_sr0 failed.\n");
                }

                if ( tsdv_sr1 == null )
                {
                    outStream.print("allocation of tsdv_sr1 failed.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("Test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "Test threw a system error exception: \"%s\"",
                            systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               tsfa,
                                                               tsdv,
                                                               outStream,
                                                               verbose,
                                                               "tsdv");

            if ( tsdv.subRange != tsfa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv.subRange doesn't match tsfa.getSubRange().\n");
                }
            }

            if ( ! tsdv.itsValue.eq(new TimeStamp(db.getTicks(), 60)) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv.itsValue = (%d,%d) != (%d,60).\n",
                                     tsdv.itsValue.getTPS(),
                                     tsdv.itsValue.getTicks(),
                                     db.getTicks());
                }
            }

            if ( tsdv.maxVal != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv.maxVal: " +
                            "(%d,%d) (null expected).\n",
                            tsdv.maxVal.getTPS(), tsdv.maxVal.getTicks());
                }
            }

            if ( tsdv.minVal != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv.minVal: " +
                            "(%d,%d) (null expected).\n",
                            tsdv.minVal.getTPS(), tsdv.minVal.getTicks());
                }
            }

            /**************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               tsfa_sr,
                                                               tsdv_sr0,
                                                               outStream,
                                                               verbose,
                                                               "tsdv_sr0");

            if ( tsdv_sr0.subRange != tsfa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv_sr0.subRange doesn't match " +
                                     "tsfa_sr.getSubRange().\n");
                }
            }

            if ( ! tsdv_sr0.itsValue.eq(new TimeStamp(db.getTicks(),
                                                      60 * db.getTicks())) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv_sr0.itsValue = (%d,%d) != (%d,%d).\n",
                                     tsdv_sr0.itsValue.getTPS(),
                                     tsdv_sr0.itsValue.getTicks(),
                                     db.getTicks(),
                                     60 * db.getTicks());
                }
            }

            if ( ! tsdv_sr0.maxVal.eq(tsfa_sr.getMaxVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv_sr0.maxVal: " +
                            "(%d,%d) -- (%d,%d) expected.\n",
                            tsdv_sr0.maxVal.getTPS(),
                            tsdv_sr0.maxVal.getTicks(),
                            tsfa_sr.getMaxVal().getTPS(),
                            tsfa_sr.getMaxVal().getTicks());
                }
            }

            if ( ! tsdv_sr0.minVal.eq(tsfa_sr.getMinVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv_sr0.minVal: " +
                            "(%d,%d) -- (%d,%d) expected.\n",
                            tsdv_sr0.minVal.getTPS(),
                            tsdv_sr0.minVal.getTicks(),
                            tsfa_sr.getMinVal().getTPS(),
                            tsfa_sr.getMinVal().getTicks());
                }
            }

            /*************************/

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               tsfa_sr,
                                                               tsdv_sr1,
                                                               outStream,
                                                               verbose,
                                                               "tsdv_sr1");

            if ( tsdv_sr1.subRange != tsfa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv_sr1.subRange doesn't match " +
                                     "tsfa_sr.getSubRange().\n");
                }
            }

            if ( ! tsdv_sr1.itsValue.eq(tsfa_sr.getMaxVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv_sr1.itsValue = (%d,%d) != (%d,%d).\n",
                                     tsdv_sr1.itsValue.getTPS(),
                                     tsdv_sr1.itsValue.getTicks(),
                                     tsfa_sr.getMaxVal().getTPS(),
                                     tsfa_sr.getMaxVal().getTicks());
                }
            }

            if ( ! tsdv_sr1.maxVal.eq(tsfa_sr.getMaxVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv_sr1.maxVal: " +
                            "(%d,%d) -- (%d,%d) expected.\n",
                            tsdv_sr1.maxVal.getTPS(),
                            tsdv_sr1.maxVal.getTicks(),
                            tsfa_sr.getMaxVal().getTPS(),
                            tsfa_sr.getMaxVal().getTicks());
                }
            }

            if ( ! tsdv_sr1.minVal.eq(tsfa_sr.getMinVal()) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of tsdv_sr1.minVal: " +
                            "(%d,%d) -- (%d,%d) expected.\n",
                            tsdv_sr1.minVal.getTPS(),
                            tsdv_sr1.minVal.getTicks(),
                            tsfa_sr.getMinVal().getTPS(),
                            tsfa_sr.getMinVal().getTicks());
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            tsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv = new TimeStampDataValue((Database)null, tsfa.getID(),
                                              new TimeStamp(db.getTicks(), 0));
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( tsdv != null )
                    {
                        outStream.print("new TimeStampDataValue(null, " +
                                "tsfa.getID(), new TimeStamp(db.getTicks(), " +
                                "0)) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(null, " +
                                "tsfa.getID(), new TimeStamp(db.getTicks(), " +
                                "0)) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(null, " +
                            "tsfa.getID(), new TimeStamp(db.getTicks(), " +
                            "0)) failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            tsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv = new TimeStampDataValue(db, DBIndex.INVALID_ID,
                                              new TimeStamp(db.getTicks(), 0));
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( tsdv != null )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                                "INVALID_ID, new TimeStamp(db.getTicks(), 0))" +
                                " returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                                "INVALID_ID, new TimeStamp(db.getTicks(), 0))" +
                                " returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "INVALID_ID, new TimeStamp(db.getTicks(), 0)) " +
                            "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an ID that does not
         * refer to a formal argument.
         */
        if ( failures == 0 )
        {
            tsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv = new TimeStampDataValue(db, ts_mve.getID(),
                                              new TimeStamp(db.getTicks(), 0));
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "ts_mve.getID(), new TimeStamp(db.getTicks(), " +
                            "0)) returned.\n");
                    }

                    if ( tsdv != null )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "ts_mve.getID(), new TimeStamp(db.getTicks(), " +
                            "0)) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "ts_mve.getID(), new TimeStamp(db.getTicks(), 0)) " +
                            "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when supplied an invalid initial
         * value.
         */
        if ( failures == 0 )
        {
            tsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv = new TimeStampDataValue(db, tsfa.getID(),
                                           new TimeStamp(db.getTicks() + 1, 0));
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "tsfa.getID(), new TimeStamp(db.getTicks() + 1, " +
                            "0)) returned.\n");
                    }

                    if ( tsdv != null )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "tsfa.getID(), new TimeStamp(db.getTicks() + 1, " +
                            "0)) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(db, " +
                            "tsfa.getID(), new TimeStamp(db.getTicks() + 1, " +
                            "0)) failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* TimeStampDataValue::Test3ArgConstructor() */


    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors supported by this class.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestAccessors(java.io.PrintStream outStream,
                                        boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing class TimeStampDataValue accessors                       ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement matrix_mve0 = null;
        MatrixVocabElement matrix_mve1 = null;
        TimeStampFormalArg tsfa = null;
        UnTypedFormalArg ufa = null;
        TimeStampDataValue tsdv0 = null;
        TimeStampDataValue tsdv1 = null;
        TimeStampDataValue tsdv2 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa = new TimeStampFormalArg(db);
            tsfa.setRange(new TimeStamp(db.getTicks(), 10 * db.getTicks()),
                         new TimeStamp(db.getTicks(), 60 * 60 * db.getTicks()));
            matrix_mve0.appendFormalArg(tsfa);
            db.vl.addElement(matrix_mve0);

            tsdv0 = new TimeStampDataValue(db, tsfa.getID(),
                    new TimeStamp(db.getTicks(), 60 * db.getTicks()));

            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve1.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve1.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve1);

            tsdv1 = new TimeStampDataValue(db, ufa.getID(),
                    new TimeStamp(db.getTicks(), (60 * 60 * db.getTicks()) + 1));
            tsdv2 = new TimeStampDataValue(db, ufa.getID(),
                              new TimeStamp(db.getTicks(), 60 * db.getTicks()));

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( matrix_mve0 == null ) ||
             ( tsfa == null ) ||
             ( tsdv0 == null ) ||
             ( matrix_mve1 == null ) ||
             ( ufa == null ) ||
             ( tsdv1 == null ) ||
             ( tsdv2 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( matrix_mve0 == null )
                {
                    outStream.print("allocation of matrix_mve0 failed.\n");
                }

                if ( tsfa == null )
                {
                    outStream.print("allocation of tsfa failed.\n");
                }

                if ( tsdv0 == null )
                {
                    outStream.print("allocation of tsdv0 failed.\n");
                }

                if ( matrix_mve0 == null )
                {
                    outStream.print("allocation of matrix_mve0 failed.\n");
                }

                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( tsdv1 == null )
                {
                    outStream.print("allocation of tsdv1 failed.\n");
                }

                if ( tsdv2 == null )
                {
                    outStream.print("allocation of tsdv2 failed.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("Test failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "Test setup threw a system error exception: \"%s\"",
                            systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.TestAccessors(db, tsfa, matrix_mve1, ufa,
                                                tsdv0, outStream, verbose);

            if ( tsdv0.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv0.getSubRange() != false");
                }
            }

            if ( ! tsdv0.getItsValue().eq(new TimeStamp(db.getTicks(),
                                                        60 * db.getTicks())) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv0.itsValue = (%d,%d) -- (%d,%d) expected.\n",
                            tsdv0.itsValue.getTPS(),
                            tsdv0.itsValue.getTicks(),
                            db.getTicks(),
                            60 * db.getTicks());
                }
            }

            tsdv0.setItsValue(new TimeStamp(db.getTicks(), 30 * db.getTicks()));


            if ( ! tsdv0.getItsValue().eq(new TimeStamp(db.getTicks(),
                                                        30 * db.getTicks())) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv0.itsValue = (%d,%d) -- (%d,%d) expected.\n",
                            tsdv0.itsValue.getTPS(),
                            tsdv0.itsValue.getTicks(),
                            db.getTicks(),
                            60 * db.getTicks());
                }
            }

            /************************************/

            if ( tsdv1.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv1.getSubRange() != false\n");
                }
            }

            if ( ! tsdv1.getItsValue().eq(new TimeStamp(db.getTicks(),
                                               (60 * 60 * db.getTicks()) + 1)) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv1.itsValue = (%d,%d) -- (%d,%d) expected.\n",
                            tsdv1.itsValue.getTPS(),
                            tsdv1.itsValue.getTicks(),
                            db.getTicks(),
                            (60 * 60 * db.getTicks()) + 1);
                }
            }

            failures += DataValue.TestAccessors(db, ufa, matrix_mve0, tsfa,
                                                tsdv1, outStream, verbose);

            if ( tsdv1.getSubRange() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv1.getSubRange() != true\n");
                }
            }

            if ( ! tsdv1.getItsValue().eq(new TimeStamp(db.getTicks(),
                                                     60 * 60 * db.getTicks())) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv1.itsValue = (%d,%d) -- (%d,%d) expected.\n",
                            tsdv1.itsValue.getTPS(),
                            tsdv1.itsValue.getTicks(),
                            db.getTicks(),
                            60 * 60 * db.getTicks());
                }
            }

            tsdv1.setItsValue(new TimeStamp(db.getTicks(), 9 * db.getTicks()));

            if ( ! tsdv1.getItsValue().eq(new TimeStamp(db.getTicks(),
                                                        10 * db.getTicks())) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv1.itsValue = (%d,%d) -- (%d,%d) expected.\n",
                            tsdv1.itsValue.getTPS(),
                            tsdv1.itsValue.getTicks(),
                            db.getTicks(),
                            10 * db.getTicks());
                }
            }

            if ( ( tsdv1.coerceToRange(new TimeStamp(db.getTicks(),
                    (60 * 60 * db.getTicks()) + 1)).ne(new TimeStamp(db.getTicks(),
                    (60 * 60 * db.getTicks()))) ) ||
                 ( tsdv1.coerceToRange(new TimeStamp(db.getTicks(),
                    (60 * 60 * db.getTicks()))).ne(new TimeStamp(db.getTicks(),
                    (60 * 60 * db.getTicks()))) ) ||
                 ( tsdv1.coerceToRange(new TimeStamp(db.getTicks(),
                    (60 * 60 * db.getTicks()) - 1)).ne(new TimeStamp(db.getTicks(),
                    (60 * 60 * db.getTicks()) - 1)) ) ||
                 ( tsdv1.coerceToRange(new TimeStamp(db.getTicks(),
                    (30 * 60 * db.getTicks()))).ne(new TimeStamp(db.getTicks(),
                    (30 * 60 * db.getTicks()))) ) ||
                 ( tsdv1.coerceToRange(new TimeStamp(db.getTicks(),
                    (10 * db.getTicks()) + 1)).ne(new TimeStamp(db.getTicks(),
                    (10 * db.getTicks()) + 1)) ) ||
                 ( tsdv1.coerceToRange(new TimeStamp(db.getTicks(),
                    (10 * db.getTicks()))).ne(new TimeStamp(db.getTicks(),
                    (10 * db.getTicks()))) ) ||
                 ( tsdv1.coerceToRange(new TimeStamp(db.getTicks(),
                    (10 * db.getTicks()) - 1)).ne(new TimeStamp(db.getTicks(),
                    (10 * db.getTicks()))) ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "unexpected results from tsdv1.coerceToRange()\n");
                }
            }

            /************************************/

            failures += DataValue.TestAccessors(db, ufa, matrix_mve0, tsfa,
                                                tsdv2, outStream, verbose);

            if ( tsdv2.getSubRange() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tsdv2.getSubRange() != true\n");
                }
            }

            if ( tsdv2.getItsValue().ne(new TimeStamp(db.getTicks(),
                                                      60 * db.getTicks())) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tsdv2.itsValue = (%d,%d) -- (%d,%d) expected.\n",
                            tsdv2.itsValue.getTPS(),
                            tsdv2.itsValue.getTicks(),
                            db.getTicks(),
                            60 * db.getTicks());
                }
            }
        }

        /* verivy that setItsValue() fails when provided an invalid value */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv2.setItsValue(new TimeStamp(db.getTicks() - 1,
                                                30 * db.getTicks()));
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.printf(
                                "tsdv2.setItsValue(invalid_ts) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("tsdv2.setItsValue(invalid_ts) " +
                                "failed to throw a system error.\n");
                    }
                }
            }

        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* TimeStampDataValue::TestAccessors() */


    /**
     * TestCopyConstructor()
     *
     * Run a battery of tests on the copy constructor for this
     * class, and on the instances returned.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestCopyConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing copy constructor for class TimeStampDataValue            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement matrix_mve = null;
        MatrixVocabElement matrix_mve_sr = null;
        TimeStampFormalArg tsfa = null;
        TimeStampFormalArg tsfa_sr = null;
        TimeStampDataValue tsdv = null;
        TimeStampDataValue tsdv0 = null;
        TimeStampDataValue tsdv0_copy = null;
        TimeStampDataValue tsdv1 = null;
        TimeStampDataValue tsdv1_copy = null;
        TimeStampDataValue tsdv2 = null;
        TimeStampDataValue tsdv2_copy = null;
        TimeStampDataValue tsdv_sr0 = null;
        TimeStampDataValue tsdv_sr0_copy = null;
        TimeStampDataValue tsdv_sr1 = null;
        TimeStampDataValue tsdv_sr1_copy = null;
        TimeStampDataValue tsdv_sr2 = null;
        TimeStampDataValue tsdv_sr2_copy = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* setup the base entries for the copy test */
        try
        {
            db = new ODBCDatabase();

            tsdv0 = new TimeStampDataValue(db);

            matrix_mve = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa = new TimeStampFormalArg(db);
            matrix_mve.appendFormalArg(tsfa);
            db.vl.addElement(matrix_mve);

            tsdv1 = new TimeStampDataValue(db, tsfa.getID());
            tsdv2 = new TimeStampDataValue(db, tsfa.getID(),
                    new TimeStamp(db.getTicks(), 24 * 60 * 60 * db.getTicks()));

            matrix_mve_sr = new MatrixVocabElement(db, "matrix_mve_sr");
            matrix_mve_sr.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa_sr = new TimeStampFormalArg(db);
            tsfa.setRange(new TimeStamp(db.getTicks(), 10 * db.getTicks()),
                         new TimeStamp(db.getTicks(), 60 * 60 * db.getTicks()));
            matrix_mve_sr.appendFormalArg(tsfa_sr);
            db.vl.addElement(matrix_mve_sr);

            tsdv_sr0 = new TimeStampDataValue(db, tsfa_sr.getID());
            tsdv_sr1 = new TimeStampDataValue(db, tsfa_sr.getID(),
                    new TimeStamp(db.getTicks(), 12 * db.getTicks()));
            tsdv_sr2 = new TimeStampDataValue(db, tsfa_sr.getID(),
                    new TimeStamp(db.getTicks(), 12 * 60 * 60 * db.getTicks()));

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( tsdv0 == null ) ||
             ( matrix_mve == null ) ||
             ( tsfa == null ) ||
             ( tsdv1 == null ) ||
             ( tsdv2 == null ) ||
             ( matrix_mve_sr == null ) ||
             ( tsfa_sr == null ) ||
             ( tsdv_sr0 == null ) ||
             ( tsdv_sr1 == null ) ||
             ( tsdv_sr2 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( tsdv0 == null )
                {
                    outStream.print("allocation of tsdv0 failed.\n");
                }

                if ( matrix_mve == null )
                {
                    outStream.print("allocation of ts_mve failed.\n");
                }

                if ( tsfa == null )
                {
                    outStream.print("allocation of tsfa failed.");
                }

                if ( tsdv1 == null )
                {
                    outStream.print("allocation of tsdv1 failed.\n");
                }

                if ( tsdv2 == null )
                {
                    outStream.print("allocation of tsdv2 failed.\n");
                }

                if ( matrix_mve_sr == null )
                {
                    outStream.print("allocation of ts_mve_sr failed.\n");
                }

                if ( tsfa_sr == null )
                {
                    outStream.print("allocation of tsfa_sr failed.");
                }

                if ( tsdv_sr0 == null )
                {
                    outStream.print("allocation of tsdv_sr0 failed.\n");
                }

                if ( tsdv_sr2 == null )
                {
                    outStream.print("allocation of tsdv_sr1 failed.\n");
                }

                if ( tsdv_sr2 == null )
                {
                    outStream.print("allocation of tsdv_sr2 failed.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("Test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "Test setup threw a system error exception: \"%s\"",
                            systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            /* setup the base entries for the copy test */
            try
            {
                tsdv0_copy = new TimeStampDataValue(tsdv0);
                tsdv1_copy = new TimeStampDataValue(tsdv1);
                tsdv2_copy = new TimeStampDataValue(tsdv2);
                tsdv_sr0_copy = new TimeStampDataValue(tsdv_sr0);
                tsdv_sr1_copy = new TimeStampDataValue(tsdv_sr1);
                tsdv_sr2_copy = new TimeStampDataValue(tsdv_sr2);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv0_copy == null ) ||
                 ( tsdv1_copy == null ) ||
                 ( tsdv2_copy == null ) ||
                 ( tsdv_sr0_copy == null ) ||
                 ( tsdv_sr1_copy == null ) ||
                 ( tsdv_sr2_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( tsdv0_copy == null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(tsdv0) returned null.\n");
                    }

                    if ( tsdv1_copy == null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(tsdv1) returned null.\n");
                    }

                    if ( tsdv2_copy == null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(tsdv2) returned null.\n");
                    }

                    if ( tsdv_sr0_copy == null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(tsdv_sr0) returned null.\n");
                    }

                    if ( tsdv_sr1_copy == null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(tsdv_sr1) returned null.\n");
                    }

                    if ( tsdv_sr2_copy == null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(tsdv_sr2) returned null.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("Test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Test threw a system error exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.VerifyDVCopy(tsdv0, tsdv0_copy, outStream,
                                               verbose, "tsdv0", "tsdv0_copy");

            failures += DataValue.VerifyDVCopy(tsdv1, tsdv1_copy, outStream,
                                               verbose, "tsdv1", "tsdv1_copy");

            failures += DataValue.VerifyDVCopy(tsdv2, tsdv2_copy, outStream,
                                               verbose, "tsdv2", "tsdv2_copy");

            failures += DataValue.VerifyDVCopy(tsdv_sr0, tsdv_sr0_copy, outStream,
                                            verbose, "tsdv_sr0", "tsdv_sr0_copy");

            failures += DataValue.VerifyDVCopy(tsdv_sr1, tsdv_sr1_copy, outStream,
                                            verbose, "tsdv_sr1", "tsdv_sr1_copy");

            failures += DataValue.VerifyDVCopy(tsdv_sr2, tsdv_sr2_copy, outStream,
                                            verbose, "tsdv_sr2", "tsdv_sr2_copy");
        }


        /* verify that the constructor fails when given an invalid dv */
        if ( failures == 0 )
        {
            tsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tsdv = new TimeStampDataValue((TimeStampDataValue)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tsdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TimeStampDataValue(null) completed.\n");
                    }

                    if ( tsdv != null )
                    {
                        outStream.print(
                                "new TimeStampDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TimeStampDataValue(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }


        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* TimeStampDataValue::TestCopyConstructor() */


    /**
     * TestToStringMethods()
     *
     * Run a battery of tests on the toString methods supported by
     * this class.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestToStringMethods(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String testString0 = "00:00:12:000";
        String testDBString0 = "(TimeStampDataValue (id 100) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType TIME_STAMP) " +
                                    "(itsCellID 500) " +
                                    "(itsValue (60,00:00:12:000)) " +
                                    "(subRange true) " +
                                    "(minVal (60,00:00:10:000)) " +
                                    "(maxVal (60,01:00:00:000)))";
        String testString1 = "12:10:05:012";
        String testDBString1 = "(TimeStampDataValue (id 101) " +
                                    "(itsFargID 8) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 501) " +
                                    "(itsValue (60,12:10:05:012)) " +
                                    "(subRange false))";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement matrix_mve_sr = null;
        MatrixVocabElement matrix_mve = null;
        TimeStampFormalArg tsfa = null;
        UnTypedFormalArg ufa = null;
        TimeStampDataValue tsdv0 = null;
        TimeStampDataValue tsdv1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        tsdv0 = null;
        tsdv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            matrix_mve_sr = new MatrixVocabElement(db, "matrix_mve_sr");
            matrix_mve_sr.setType(MatrixVocabElement.MatrixType.MATRIX);
            tsfa = new TimeStampFormalArg(db);
            tsfa.setRange(new TimeStamp(db.getTicks(), 10 * db.getTicks()),
                         new TimeStamp(db.getTicks(), 60 * 60 * db.getTicks()));
            matrix_mve_sr.appendFormalArg(tsfa);
            db.vl.addElement(matrix_mve_sr);

            tsdv0 = new TimeStampDataValue(db, tsfa.getID(),
                    new TimeStamp(db.getTicks(), 12 * db.getTicks()));
            tsdv0.id = 100;        // invalid value for print test
            tsdv0.itsCellID = 500; // invalid value for print test

            matrix_mve = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve);

            tsdv1 = new TimeStampDataValue(db, ufa.getID(),
                    new TimeStamp(db.getTicks(), 12 * 60 * 60 * db.getTicks()
                                                    + 10 * 60 * db.getTicks()
                                                          + 5 * db.getTicks()
                                                              + 12));
            tsdv1.id = 101;        // invalid value for print test
            tsdv1.itsCellID = 501; // invalid value for print test

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( matrix_mve_sr == null ) ||
             ( tsfa == null ) ||
             ( tsdv0 == null ) ||
             ( matrix_mve == null ) ||
             ( ufa == null ) ||
             ( tsdv1 == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( matrix_mve_sr == null )
                {
                    outStream.print("allocation of matrix_mve_sr failed.\n");
                }

                if ( tsfa == null )
                {
                    outStream.print("allocation of tsfa failed.\n");
                }

                if ( tsdv0 == null )
                {
                    outStream.print("allocation of tsdv0 failed.\n");
                }

                if ( matrix_mve == null )
                {
                    outStream.print("allocation of matrix_mve failed.\n");
                }

                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( tsdv1 == null )
                {
                    outStream.print("allocation of tsdv1 failed.\n");
                }

                if ( ! completed )
                {
                    outStream.printf("Test failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf(
                            "Test setup threw a system error exception: \"%s\"",
                            systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( tsdv0.toString().compareTo(testString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected tsdv0.toString(): \"%s\".\n",
                                     tsdv0.toString());
                }
            }

            if ( tsdv0.toDBString().compareTo(testDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected tsdv0.toDBString(): \"%s\".\n",
                                     tsdv0.toDBString());
                }
            }

            if ( tsdv1.toString().compareTo(testString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected tsdv1.toString(): \"%s\".\n",
                                     tsdv1.toString());
                }
            }

            if ( tsdv1.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected tsdv1.toDBString(): \"%s\".\n",
                                     tsdv1.toDBString());
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* TimeStampDataValue::TestToStringMethods() */


    /**
     * VerifyTimeStampDVCopy()
     *
     * Verify that the supplied instances of TimeStampDataValue are distinct,
     * that they contain no common references (other than db), and that they
     * have the same value.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyTimeStampDVCopy(TimeStampDataValue base,
                                            TimeStampDataValue copy,
                                            java.io.PrintStream outStream,
                                            boolean verbose,
                                            String baseDesc,
                                            String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyTimeStampDVCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyTimeStampDVCopy: %s null on entry.\n",
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
        else if ( base.db != copy.db )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.db != %s.db.\n", baseDesc, copyDesc);
            }
        }
        else if ( ( base.itsValue == copy.itsValue ) &&
                  ( base.itsValue != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s share a value TimeStamp.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( base.itsValue == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( copy.itsValue == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        copyDesc, baseDesc);
            }
        }
        else if ( ! base.itsValue.equals(copy.itsValue) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.itsValue and %s.itsValue are different.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.maxVal == copy.maxVal ) &&
                  ( base.maxVal != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s share a maxVal TimeStamp.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.maxVal == null ) &&
                  ( copy.maxVal != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.maxVal is null, and %s.maxVal isn't.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( ( base.maxVal != null ) &&
                  ( copy.maxVal == null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.maxVal is null, and %s.maxVal isn't.\n",
                        copyDesc, baseDesc);
            }
        }
        else if ( ( base.maxVal != null ) &&
                  ( ! base.maxVal.equals(copy.maxVal) ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.maxVal and %s.maxVal are different.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.minVal == copy.minVal ) &&
                  ( base.minVal != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s share a minVal TimeStamp.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.minVal == null ) &&
                  ( copy.minVal != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.minVal is null, and %s.minVal isn't.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( ( base.minVal != null ) &&
                  ( copy.minVal == null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.minVal is null, and %s.minVal isn't.\n",
                        copyDesc, baseDesc);
            }
        }
        else if ( ( base.minVal != null ) &&
                  ( ! base.minVal.equals(copy.minVal) ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.minVal and %s.minVal are different.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( base.toString().compareTo(copy.toString()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.toString() doesn't match %s.toString().\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.toDBString().compareTo(copy.toDBString()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.toDBString() doesn't match %s.toDBString().\n",
                        baseDesc, copyDesc);
            }
        }

        return failures;

    } /* TimeStampDataValue::VerifyTimeStampDVCopy() */

} /* TimeStampDataValue */
