/*
 * FloatDataValue.java
 *
 * Created on August 15, 2007, 10:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * An instance of FloatDataValue is used to store a floating point value
 * assigned to a formal argument.
 *
 * @author mainzer
 */
public final class FloatDataValue extends DataValue {
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * itsDefault:  Constant containing the value to be assigned to all
     *      float data values unless otherwise specified.
     *
     * itsValue:   Double containing the value assigned to the formal argument.
     *
     * minVal:  If subRange is true, this field contains the minimum value
     *      that may be assigned to the formal argument associated with the
     *      data value.   This value should always be the same as the minVal
     *      of the associated instance of FloatFormalArg.
     *
     *      Note that this data value may be used to hold a floating point
     *      value assigned to an untype formal argument, in which case
     *      subrange will always be false.
     *
     * maxVal:  If subRange is true, this field contains the maximum value
     *      that may be assigned to the formal argument associated with the
     *      data value.   This value should always be the same as the maxVal
     *      of the associated instance of FloatFormalArg.
     *
     *      Note that this data value may be used to hold a floating point
     *      value assigned to an untype formal argument, in which case
     *      subrange will always be false.
     */

    /** default value for floats. */
    private final double itsDefault = 0.0;

    /** the value assigned to the associated formal argument in this case. */
    private double itsValue = itsDefault;

    /** the minimum value -- if subrange is true. */
    private double minVal = 0.0;

    /** the maximum value -- if subrange is true. */
    private double maxVal = 0.0;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * FloatDataValue()
     *
     * Constructor for instances of FloatDataValue.
     *
     * Four versions of this constructor.
     *
     * The first takes a reference to a database as its parameter and just
     * calls the super() constructor.
     *
     * The second takes a reference to a database, and a formal argument ID
     * as arguments, and attempts to set the itsFargID field of the data value
     * accordingly.  ItsValue is set to the default.
     *
     * The third takes a reference to a database, a formal argument ID, and
     * a value as arguments, and attempts to set the itsFargID and itsValue
     * of the data value accordingly.
     *
     * The fourth takes a reference to an instance of FloatDataValue as an
     * argument, and uses it to create a copy.
     *
     *                                              JRM -- 8/16/07
     * @param db Database associated
     * @throws SystemErrorException if superclass throws
     */

    public FloatDataValue(Database db) throws SystemErrorException {

        super(db);

    } /* FloatDataValue::FloatDataValue(db) */

    public FloatDataValue(Database db,
                          long fargID)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

    } /* FloatDataValue::FloatDataValue(db, fargID) */

    public FloatDataValue(Database db,
                          long fargID,
                          double value)
        throws SystemErrorException
    {
        super(db);

        this.setItsFargID(fargID);

        this.setItsValue(value);

    } /* FloatDataValue::FloatDataValue(db, fargID, value) */

    public FloatDataValue(FloatDataValue dv)
        throws SystemErrorException
    {
        super(dv);

        this.itsValue  = dv.itsValue;
        this.minVal    = dv.minVal;
        this.maxVal    = dv.maxVal;

    } /* FloatDataValue::FloatDataValue(dv) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getItsValue()
     *
     * Return the current value of the data value.
     *
     *                          JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    public double getItsValue()
    {
        return this.itsValue;

    } /* FloatDataValue::getItsValue() */

    /**
     * setItsValue()
     *
     * Set itsValue to the specified value.  If subrange is true, coerce the
     * value into the subrange.
     *
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    public void setItsValue(double value)
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

        return;

    } /* FloatDataValue::setItsValue() */

    /** @return the max value. */
    public double getMaxVal() {
        return this.maxVal;
    }

    /** @return the min value. */
    public double getMinVal() {
        return this.minVal;
    }

    /** @return the default value. */
    public double getDefault() {
        return this.itsDefault;
    }


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
        return ("" + this.itsValue);
    }


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
        return ("(FloatDataValue (id " + this.id +
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
        final String mName = "FloatDataValue::updateForFargChange(): ";

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

    } /* FloatDataValue::updateForFargChange() */


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
     *                                          JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */

    protected void updateSubRange(FormalArgument fa)
        throws SystemErrorException
    {
        final String mName = "FloatDataValue::updateSubRange(): ";

        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");
        }

        if ( fa instanceof FloatFormalArg )
        {
            FloatFormalArg ffa = (FloatFormalArg)fa;

            if ( this.itsFargType != FormalArgument.fArgType.FLOAT )
            {
                throw new SystemErrorException(mName +
                        "Unexpected this.itsFargType(1).");
            }

            this.subRange = ffa.getSubRange();

            if ( this.subRange )
            {
                this.maxVal = ffa.getMaxVal();
                this.minVal = ffa.getMinVal();

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
            if ( this.itsFargType != FormalArgument.fArgType.UNTYPED )
            {
                throw new SystemErrorException(mName +
                        "Unexpected this.itsFargType(2).");
            }

            this.subRange = false;
        }
        else
        {
            throw new SystemErrorException(mName + "Unexpected fa type");
        }

        return;

    } /* FloatDataValue::updateSubRange() */


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
    public double coerceToRange(double value)
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

    } /* FloatDataValue::coerceToRange() */


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
     *                                              JRM -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    public static FloatDataValue Construct(Database db,
                                           double f)
        throws SystemErrorException
    {
        final String mName = "FloadDataValue::Construct(db, f)";
        FloatDataValue fdv = null;

        fdv = new FloatDataValue(db);

        fdv.setItsValue(f);

        return fdv;

    } /* FloatDataValue::Construct(db, f) */

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
        double hash = super.hashCode();
        hash += Math.floor(this.itsValue) * SEED1;
        hash += Math.floor(this.maxVal) * SEED2;
        hash += Math.floor(this.minVal) * SEED3;
        long val = Double.doubleToLongBits(hash);

        return (int) (val ^ (val >>> 32));
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
        FloatDataValue f = (FloatDataValue) obj;
        return closeEnough(f.itsValue, this.itsValue)
            && closeEnough(f.maxVal, this.maxVal)
            && closeEnough(f.minVal, this.minVal)
            && super.equals(obj);
    }

//<<<<<<< HEAD:src/main/java/au/com/nicta/openshapa/db/FloatDataValue.java

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
     *         argument constructor for FloatDataValue.  Verify that all
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
     *         Construct a FloatDataValue for the formal argument of the mve
     *         by passing a reference to the database and the id of the formal
     *         argument.  Verify that the FloatDataValue's itsFargID,
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
     *      FloatDataValue -- perhaps after havign been modified to match
     *      the subrange.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and possibly a mve (matrix vocab element)
     *         and such formal arguments as are necessary.  If an mve is
     *         created, insert it into the database, and make note of the IDs
     *         assigned.  Then create a  FloatDataValue (possibly using
     *         the using a formal argument ID).
     *
     *         Now use the copy constructor to create a copy of the
     *         FloatDataValue, and verify that the copy is correct.
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
     * TestClassFloatDataValue()
     *
     * Main routine for tests of class FloatDataValue.
     *
     *                                      JRM -- 10/15/07
     *
     * Changes:
     *
     *    - Non.
     */

    public static boolean TestClassFloatDataValue(java.io.PrintStream outStream,
                                                  boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;

        outStream.print("Testing class FloatDataValue:\n");

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
            outStream.printf("%d failures in tests for class FloatDataValue.\n\n",
                             failures);
        }
        else
        {
            outStream.print("All tests passed for class FloatDataValue.\n\n");
        }

        return pass;

    } /* FloatDataValue::TestClassFloatDataValue() */


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
    {
        String testBanner =
            "Testing 1 argument constructor for class FloatDataValue          ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        FloatDataValue fdv = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        fdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();
            fdv = new FloatDataValue(db);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( fdv == null ) ||
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

                if ( fdv == null )
                {
                    outStream.print(
                            "new FloatDataValue(db) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "new FloatDataValue(db) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new FloatDataValue(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.Verify1ArgInitialization(db, fdv, outStream,
                                                           verbose);

            if ( fdv.itsValue != fdv.itsDefault )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "fdv.itsValue = %f != fdv.ItsDefault = %f.\n",
                            fdv.itsValue, fdv.itsDefault);
                }
            }

            if ( fdv.maxVal != 0.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of fdv.maxVal: %f.\n",
                                     fdv.maxVal);
                }
            }

            if ( fdv.minVal != 0.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("bad initial value of fdv.minVal: %f.\n",
                                     fdv.minVal);
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue((Database)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(null) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print(
                                "new FloatDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new FloatDataValue(null) failed to throw " +
                                        "a system error exception.\n");
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

    } /* FloatDataValue::Test1ArgConstructor() */


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
            "Testing 2 argument constructor for class FloatDataValue          ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement float_mve_sr = null;
        FloatFormalArg ffa = null;
        FloatFormalArg ffa_sr = null;
        FloatDataValue fdv = null;
        FloatDataValue fdv_sr = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        fdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa = new FloatFormalArg(db);
            float_mve.appendFormalArg(ffa);
            db.vl.addElement(float_mve);

            fdv = new FloatDataValue(db, ffa.getID());

            float_mve_sr = new MatrixVocabElement(db, "float_mve_sr");
            float_mve_sr.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa_sr = new FloatFormalArg(db);
            ffa_sr.setRange(-100.0, 100.0);
            float_mve_sr.appendFormalArg(ffa_sr);
            db.vl.addElement(float_mve_sr);

            fdv_sr = new FloatDataValue(db, ffa_sr.getID());

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( float_mve == null ) ||
             ( ffa == null ) ||
             ( fdv == null ) ||
             ( float_mve_sr == null ) ||
             ( ffa_sr == null ) ||
             ( fdv_sr == null ) ||
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

                if ( float_mve == null )
                {
                    outStream.print("allocation of float_mve failed.\n");
                }

                if ( ffa == null )
                {
                    outStream.print("allocation of ffa failed.");
                }

                if ( fdv == null )
                {
                    outStream.print(
                        "new FloatDataValue(db, ffa.getID()) returned null.\n");
                }

                if ( float_mve_sr == null )
                {
                    outStream.print("allocation of float_mve_sr failed.\n");
                }

                if ( ffa_sr == null )
                {
                    outStream.print("allocation of ffa_sr failed.");
                }

                if ( fdv_sr == null )
                {
                    outStream.print("new FloatDataValue(db, ffa_sr.getID()) " +
                                    "returned null.\n");
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
                                                               ffa,
                                                               fdv,
                                                               outStream,
                                                               verbose,
                                                              "fdv");

            if ( fdv.subRange != ffa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "fdv.subRange doesn't match ffa.getSubRange().\n");
                }
            }

            if ( fdv.itsValue != fdv.itsDefault )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "fdv.itsValue = %f != fdv.ItsDefault = %f.\n",
                            fdv.itsValue, fdv.itsDefault);
                }
            }

            if ( fdv.maxVal != 0.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of fdv.maxVal: %f (0.0).\n",
                            fdv.maxVal);
                }
            }

            if ( fdv.minVal != 0.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of fdv.minVal: %f (0.0).\n",
                            fdv.minVal);
                }
            }

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               ffa_sr,
                                                               fdv_sr,
                                                               outStream,
                                                               verbose,
                                                               "fdv_sr");

            if ( fdv_sr.subRange != ffa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv_sr.subRange doesn't match " +
                                     "ffa_sr.getSubRange().\n");
                }
            }

            if ( fdv_sr.itsValue != fdv_sr.itsDefault )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "fdv_sr.itsValue = %f != fdv_sr.ItsDefault = %f.\n",
                            fdv_sr.itsValue, fdv_sr.itsDefault);
                }
            }

            if ( fdv_sr.maxVal != ffa_sr.getMaxVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of fdv_sr.maxVal: %f (%f).\n",
                            fdv_sr.maxVal, ffa_sr.getMaxVal());
                }
            }

            if ( fdv_sr.minVal != ffa_sr.getMinVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of fdv_sr.minVal: %f (%f).\n",
                            fdv_sr.minVal, ffa_sr.getMinVal());
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue((Database)null, ffa.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(null, " +
                                        "ffa.getID()) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print("new FloatDataValue(null, " +
                                        "ffa.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new FloatDataValue(null, ffa.getID())" +
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
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue(db, DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                        "INVALID_ID) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                        "INVALID_ID) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new FloatDataValue(db, INVALID_ID)" +
                                " failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an ID that does not
         * refer to a formal argument.
         */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue(db, float_mve.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                        "float_mve.getID()) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                "float_mve.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new FloatDataValue(db, float_mve.getID()) " +
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

    } /* FloatDataValue::Test2ArgConstructor() */


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
            "Testing 3 argument constructor for class FloatDataValue          ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement float_mve_sr = null;
        FloatFormalArg ffa = null;
        FloatFormalArg ffa_sr = null;
        FloatDataValue fdv = null;
        FloatDataValue fdv_sr0 = null;
        FloatDataValue fdv_sr1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        fdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa = new FloatFormalArg(db);
            float_mve.appendFormalArg(ffa);
            db.vl.addElement(float_mve);

            fdv = new FloatDataValue(db, ffa.getID(), 200.0);

            float_mve_sr = new MatrixVocabElement(db, "float_mve_sr");
            float_mve_sr.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa_sr = new FloatFormalArg(db);
            ffa_sr.setRange(-100.0, 100.0);
            float_mve_sr.appendFormalArg(ffa_sr);
            db.vl.addElement(float_mve_sr);

            fdv_sr0 = new FloatDataValue(db, ffa_sr.getID(), 1.0);
            fdv_sr1 = new FloatDataValue(db, ffa_sr.getID(), 200.0);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( float_mve == null ) ||
             ( ffa == null ) ||
             ( fdv == null ) ||
             ( float_mve_sr == null ) ||
             ( ffa_sr == null ) ||
             ( fdv_sr0 == null ) ||
             ( fdv_sr1 == null ) ||
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

                if ( float_mve == null )
                {
                    outStream.print("allocation of float_mve failed.\n");
                }

                if ( ffa == null )
                {
                    outStream.print("allocation of ffa failed.");
                }

                if ( fdv == null )
                {
                    outStream.print("new FloatDataValue(db, ffa.getID(), " +
                                    "200.0) returned null.\n");
                }

                if ( float_mve_sr == null )
                {
                    outStream.print("allocation of float_mve_sr failed.\n");
                }

                if ( ffa_sr == null )
                {
                    outStream.print("allocation of ffa_sr failed.");
                }

                if ( fdv_sr0 == null )
                {
                    outStream.print("new FloatDataValue(db, ffa_sr.getID(), " +
                                    "1.0) returned null.\n");
                }

                if ( fdv_sr1 == null )
                {
                    outStream.print("new FloatDataValue(db, ffa_sr.getID(), " +
                                    "200.0) returned null.\n");
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
                                                               ffa,
                                                               fdv,
                                                               outStream,
                                                               verbose,
                                                               "fdv");

            if ( fdv.subRange != ffa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "fdv.subRange doesn't match ffa.getSubRange().\n");
                }
            }

            if ( fdv.itsValue != 200.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv.itsValue = %f != 200.0.\n",
                                     fdv.itsValue);
                }
            }

            if ( fdv.maxVal != 0.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of fdv.maxVal: %f (0.0).\n",
                            fdv.maxVal);
                }
            }

            if ( fdv.minVal != 0.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of fdv.minVal: %f (0.0).\n",
                            fdv.minVal);
                }
            }

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               ffa_sr,
                                                               fdv_sr0,
                                                               outStream,
                                                               verbose,
                                                               "fdv_sr0");

            if ( fdv_sr0.subRange != ffa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv_sr0.subRange doesn't match " +
                                     "ffa_sr.getSubRange().\n");
                }
            }

            if ( fdv_sr0.itsValue != 1.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv_sr.itsValue = %f != 1.0.\n",
                                     fdv_sr0.itsValue);
                }
            }

            if ( fdv_sr0.maxVal != ffa_sr.getMaxVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of fdv_sr0.maxVal: %f (%f).\n",
                            fdv_sr0.maxVal, ffa_sr.getMaxVal());
                }
            }

            if ( fdv_sr0.minVal != ffa_sr.getMinVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of fdv_sr0.minVal: %f (%f).\n",
                            fdv_sr0.minVal, ffa_sr.getMinVal());
                }
            }

            failures += DataValue.Verify2PlusArgInitialization(db,
                                                               ffa_sr,
                                                               fdv_sr1,
                                                               outStream,
                                                               verbose,
                                                               "fdv_sr1");

            if ( fdv_sr1.subRange != ffa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv_sr0.subRange doesn't match " +
                                     "ffa_sr.getSubRange().\n");
                }
            }

            if ( fdv_sr1.itsValue != ffa_sr.getMaxVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv_sr1.itsValue = %f != %f.\n",
                                     fdv_sr1.itsValue, ffa_sr.getMaxVal());
                }
            }

            if ( fdv_sr1.maxVal != ffa_sr.getMaxVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of fdv_sr1.maxVal: %f (%f).\n",
                            fdv_sr1.maxVal, ffa_sr.getMaxVal());
                }
            }

            if ( fdv_sr1.minVal != ffa_sr.getMinVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "bad initial value of fdv_sr1.minVal: %f (%f).\n",
                            fdv_sr1.minVal, ffa_sr.getMinVal());
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue((Database)null, ffa.getID(), 1.0);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(null, " +
                                        "ffa.getID(), 1.0) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print("new FloatDataValue(null, " +
                                "ffa.getID(), 1.0) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new FloatDataValue(null, ffa.getID(), 1.0) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue(db, DBIndex.INVALID_ID, 1.0);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                        "INVALID_ID, 1.0) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                "INVALID_ID, 1.0) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new FloatDataValue(db, INVALID_ID, 1.0) " +
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
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue(db, float_mve.getID(), 1.0);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                        "float_mve.getID(), 1.0) returned.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print("new FloatDataValue(db, " +
                                "float_mve.getID(), 1.0) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                            "new FloatDataValue(db, float_mve.getID(), 1.0) " +
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

    } /* FloatDataValue::Test3ArgConstructor() */


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
            "Testing class FloatDataValue accessors                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement matrix_mve = null;
        FloatFormalArg ffa = null;
        UnTypedFormalArg ufa = null;
        FloatDataValue fdv0 = null;
        FloatDataValue fdv1 = null;
        FloatDataValue fdv2 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        fdv0 = null;
        fdv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa = new FloatFormalArg(db);
            ffa.setRange(-1000.0, +1000.0);
            float_mve.appendFormalArg(ffa);
            db.vl.addElement(float_mve);

            fdv0 = new FloatDataValue(db, ffa.getID(), 200.0);

            matrix_mve = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve);

            fdv1 = new FloatDataValue(db, ufa.getID(), 2000.0);
            fdv2 = new FloatDataValue(db, ufa.getID(), 999.999);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( float_mve == null ) ||
             ( ffa == null ) ||
             ( fdv0 == null ) ||
             ( matrix_mve == null ) ||
             ( ufa == null ) ||
             ( fdv1 == null ) ||
             ( fdv2 == null ) ||
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

                if ( float_mve == null )
                {
                    outStream.print("allocation of float_mve failed.\n");
                }

                if ( ffa == null )
                {
                    outStream.print("allocation of ffa failed.\n");
                }

                if ( fdv0 == null )
                {
                    outStream.print("new FloatDataValue(db, ffa.getID(), " +
                                    "200.0) returned null.\n");
                }

                if ( matrix_mve == null )
                {
                    outStream.print("allocation of matrix_mve failed.\n");
                }

                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( fdv1 == null )
                {
                    outStream.print("new FloatDataValue(db, ufa.getID(), " +
                                    "2000.0) returned null.\n");
                }

                if ( fdv2 == null )
                {
                    outStream.print("new FloatDataValue(db, ufa.getID(), " +
                                    "999.999) returned null.\n");
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
            failures += DataValue.TestAccessors(db, ffa, matrix_mve, ufa,
                                                fdv0, outStream, verbose);


            if ( fdv0.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv0.getSubRange() != false");
                }
            }

            if ( fdv0.getItsValue() != 200.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv.getItsValue() != 200.0\n");
                }
            }

            fdv0.setItsValue(3.14159);


            if ( fdv0.getItsValue() != 3.14159 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv0.getItsValue() != 3.14159\n");
                }
            }

            /************************************/

            if ( fdv1.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv1.getSubRange() != false\n");
                }
            }

            if ( fdv1.getItsValue() != 2000.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv1.getItsValue() != 2000.0\n");
                }
            }


            failures += DataValue.TestAccessors(db, ufa, float_mve, ffa,
                                                fdv1, outStream, verbose);

            if ( fdv1.getSubRange() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv1.getSubRange() != true\n");
                }
            }

            if ( fdv1.getItsValue() != 1000.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv1.getItsValue() != 1000.0\n");
                }
            }

            fdv1.setItsValue(-50000.0);

            if ( fdv1.getItsValue() != -1000.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv1.getItsValue() != -1000.0\n");
                }
            }

            if ( ( fdv1.coerceToRange(1000.0001) != 1000.0 ) ||
                 ( fdv1.coerceToRange(1000.0) != 1000.0 ) ||
                 ( fdv1.coerceToRange(999.9999) != 999.9999 ) ||
                 ( fdv1.coerceToRange(999.0) != 999.0 ) ||
                 ( fdv1.coerceToRange(47.0) != 47.0 ) ||
                 ( fdv1.coerceToRange(-25.5) != -25.5 ) ||
                 ( fdv1.coerceToRange(-999.999) != -999.999 ) ||
                 ( fdv1.coerceToRange(-1000.0) != -1000.0 ) ||
                 ( fdv1.coerceToRange(-1000.00001) != -1000.0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "unexpected results from fdv1.coerceToRange()\n");
                }
            }

            /************************************/

            failures += DataValue.TestAccessors(db, ufa, float_mve, ffa,
                                                fdv2, outStream, verbose);

            if ( fdv2.getItsValue() != 999.999 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("fdv2.getItsValue() != 999.999\n");
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

    } /* FloatDataValue::TestAccessors() */


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
            "Testing copy constructor for class FloatDataValue                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement float_mve_sr = null;
        FloatFormalArg ffa = null;
        FloatFormalArg ffa_sr = null;
        FloatDataValue fdv = null;
        FloatDataValue fdv_copy = null;
        FloatDataValue fdv_sr0 = null;
        FloatDataValue fdv_sr0_copy = null;
        FloatDataValue fdv_sr1 = null;
        FloatDataValue fdv_sr1_copy = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        fdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* setup the base entries for the copy test */
        try
        {
            db = new ODBCDatabase();

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa = new FloatFormalArg(db);
            float_mve.appendFormalArg(ffa);
            db.vl.addElement(float_mve);

            fdv = new FloatDataValue(db, ffa.getID(), 200.0);

            float_mve_sr = new MatrixVocabElement(db, "float_mve_sr");
            float_mve_sr.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa_sr = new FloatFormalArg(db);
            ffa_sr.setRange(-100.0, 100.0);
            float_mve_sr.appendFormalArg(ffa_sr);
            db.vl.addElement(float_mve_sr);

            fdv_sr0 = new FloatDataValue(db, ffa_sr.getID(), 1.0);
            fdv_sr1 = new FloatDataValue(db, ffa_sr.getID(), 200.0);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( float_mve == null ) ||
             ( ffa == null ) ||
             ( fdv == null ) ||
             ( float_mve_sr == null ) ||
             ( ffa_sr == null ) ||
             ( fdv_sr0 == null ) ||
             ( fdv_sr1 == null ) ||
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

                if ( float_mve == null )
                {
                    outStream.print("allocation of float_mve failed.\n");
                }

                if ( ffa == null )
                {
                    outStream.print("allocation of ffa failed.");
                }

                if ( fdv == null )
                {
                    outStream.print("new FloatDataValue(db, ffa.getID(), " +
                                    "200.0) returned null.\n");
                }

                if ( float_mve_sr == null )
                {
                    outStream.print("allocation of float_mve_sr failed.\n");
                }

                if ( ffa_sr == null )
                {
                    outStream.print("allocation of ffa_sr failed.");
                }

                if ( fdv_sr0 == null )
                {
                    outStream.print("new FloatDataValue(db, ffa_sr.getID(), " +
                                    "1.0) returned null.\n");
                }

                if ( fdv_sr1 == null )
                {
                    outStream.print("new FloatDataValue(db, ffa_sr.getID(), " +
                                    "200.0) returned null.\n");
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
            fdv_copy = null;
            fdv_sr0_copy = null;
            fdv_sr1_copy = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            /* setup the base entries for the copy test */
            try
            {
                fdv_copy = new FloatDataValue(fdv);
                fdv_sr0_copy = new FloatDataValue(fdv_sr0);
                fdv_sr1_copy = new FloatDataValue(fdv_sr1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv_copy == null ) ||
                 ( fdv_sr0_copy == null ) ||
                 ( fdv_sr1_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( fdv_copy == null )
                    {
                        outStream.print(
                                "new FloatDataValue(fdv) returned null.\n");
                    }

                    if ( fdv_sr0_copy == null )
                    {
                        outStream.print(
                                "new FloatDataValue(fdv_sr0) returned null.\n");
                    }

                    if ( fdv_sr1_copy == null )
                    {
                        outStream.print(
                                "new FloatDataValue(fdv_sr1) returned null.\n");
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
            failures += DataValue.VerifyDVCopy(fdv, fdv_copy, outStream,
                                               verbose, "fdv", "fdv_copy");

            failures += DataValue.VerifyDVCopy(fdv_sr0, fdv_sr0_copy, outStream,
                                            verbose, "fdv_sr0", "fdv_sr0_copy");

            failures += DataValue.VerifyDVCopy(fdv_sr1, fdv_sr1_copy, outStream,
                                            verbose, "fdv_sr1", "fdv_sr1_copy");
        }


        /* verify that the constructor fails when given an invalid dv */
        if ( failures == 0 )
        {
            fdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                fdv = new FloatDataValue((FloatDataValue)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( fdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new FloatDataValue(null) completed.\n");
                    }

                    if ( fdv != null )
                    {
                        outStream.print(
                            "new FloatDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new FloatDataValue(null) " +
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

    } /* FloatDataValue::TestCopyConstructor() */


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
        String testString0 = "200.0";
        String testDBString0 = "(FloatDataValue (id 100) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType FLOAT) " +
                                    "(itsCellID 500) " +
                                    "(itsValue 200.0) " +
                                    "(subRange true) " +
                                    "(minVal -1000.0) " +
                                    "(maxVal 1000.0))";
        String testString1 = "2000.0";
        String testDBString1 = "(FloatDataValue (id 101) " +
                                    "(itsFargID 8) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 501) " +
                                    "(itsValue 2000.0) " +
                                    "(subRange false) " +
                                    "(minVal 0.0) " +
                                    "(maxVal 0.0))";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement float_mve = null;
        MatrixVocabElement matrix_mve = null;
        FloatFormalArg ffa = null;
        UnTypedFormalArg ufa = null;
        FloatDataValue fdv0 = null;
        FloatDataValue fdv1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        fdv0 = null;
        fdv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            float_mve = new MatrixVocabElement(db, "float_mve");
            float_mve.setType(MatrixVocabElement.MatrixType.FLOAT);
            ffa = new FloatFormalArg(db);
            ffa.setRange(-1000.0, +1000.0);
            float_mve.appendFormalArg(ffa);
            db.vl.addElement(float_mve);

            fdv0 = new FloatDataValue(db, ffa.getID(), 200.0);
            fdv0.id = 100;        // invalid value for print test
            fdv0.itsCellID = 500; // invalid value for print test

            matrix_mve = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve);

            fdv1 = new FloatDataValue(db, ufa.getID(), 2000.0);
            fdv1.id = 101;        // invalid value for print test
            fdv1.itsCellID = 501; // invalid value for print test

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( float_mve == null ) ||
             ( ffa == null ) ||
             ( fdv0 == null ) ||
             ( matrix_mve == null ) ||
             ( ufa == null ) ||
             ( fdv1 == null ) ||
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

                if ( float_mve == null )
                {
                    outStream.print("allocation of float_mve failed.\n");
                }

                if ( ffa == null )
                {
                    outStream.print("allocation of ffa failed.\n");
                }

                if ( fdv0 == null )
                {
                    outStream.print("new FloatDataValue(db, ffa.getID(), " +
                                    "200.0) returned null.\n");
                }

                if ( matrix_mve == null )
                {
                    outStream.print("allocation of matrix_mve failed.\n");
                }

                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( fdv1 == null )
                {
                    outStream.print("new FloatDataValue(db, ufa.getID(), " +
                                    "100.0) returned null.\n");
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
            if ( fdv0.toString().compareTo(testString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected fdv0.toString(): \"%s\".\n",
                                     fdv0.toString());
                }
            }

            if ( fdv0.toDBString().compareTo(testDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected fdv0.toDBString(): \"%s\".\n",
                                     fdv0.toDBString());
                }
            }

            if ( fdv1.toString().compareTo(testString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected fdv1.toString(): \"%s\".\n",
                                     fdv1.toString());
                }
            }

            if ( fdv1.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected fdv1.toDBString(): \"%s\".\n",
                                     fdv1.toDBString());
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

    } /* FloatDataValue::TestToStringMethods() */


    /**
     * VerifyFloatDVCopy()
     *
     * Verify that the supplied instances of FloatDataValue are distinct, that
     * they contain no common references (other than db), and that they have
     * the same value.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyFloatDVCopy(FloatDataValue base,
                                        FloatDataValue copy,
                                        java.io.PrintStream outStream,
                                        boolean verbose,
                                        String baseDesc,
                                        String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyFloatDVCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyFloatDVCopy: %s null on entry.\n",
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
        else if ( base.itsValue != copy.itsValue )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.itsValue != %s.itsValue.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( base.maxVal != copy.maxVal )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.maxVal != %s.maxVal.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( base.minVal != copy.minVal )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.minVal != %s.minVal.\n",
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
                outStream.printf(
                        "%s.toDBString() = \"%s\".\n%s.toDBString() = \"%s\".\n",
                        baseDesc, base.toDBString(),
                        copyDesc, copy.toDBString());
            }
        }

        return failures;

    } /* FloatDataValue::VerifyFloatDVCopy() */

    /** tolerance value for comparing two doubles for equality */
    private final static double delta = 0.000001;

    /**
     * Compare two doubles and return true if close enough
     * @param d1 first double
     * @param d2 second double
     */
    private boolean closeEnough(double d1, double d2) {
        return (Math.abs(d1 - d2) < delta);
    }
} /* FloatDataValue */
