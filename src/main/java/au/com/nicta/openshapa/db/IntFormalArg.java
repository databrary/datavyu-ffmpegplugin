/*
 * IntFormalArg.java
 *
 * Created on January 26, 2007, 12:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Class IntFormalArg
 *
 * Intance of this class are used for formal arguments which have been strongly
 * typed to integer.  Note that we implement the float type with a long, so at
 * first blush, it would make more sense to call this class "LongFormalArg".
 * However, for historical reasons, and because integer values are
 * referred to as "ints" in the user interface, it seems to make more sense
 * to use the "IntFormalArg" name.
 *
 *                                                      JRM -- 1/26/07
 *
 * @author mainzer
 */
public class IntFormalArg extends FormalArgument
{

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**
     *
     * subRange: Boolean flag indicating whether the formal argument can be
     *      replaced by any integer, or only by some integer that lies within
     *      the closed interval defined by the minVal and maxVal fields
     *      discussed below
     *
     * minVal:  Long integer field used to specify the minimum integer value
     *      that can be used to replace the formal argument if subRange is
     *      true.  If subRange is false, this field is ignored.
     *
     * maxVal:  Long integer field used to specify the maximum integer value
     *      that can be used to replace the formal argument if subRange is
     *      true.  If subRange is false, this field is ignored.
     */

    boolean subRange = false;
    long minVal = Long.MIN_VALUE;
    long maxVal = Long.MAX_VALUE;



    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * IntFormalArg()
     *
     * Constructors for integer typed formal arguments.
     *
     * Four versions of this constructor -- one that takes only a database
     * reference arguments, one that takes a database reference and the formal
     * argument name as a parameters, one that takes a database reference,
     * aformal argument name, a minimum value, and a maximum value as parameters,
     * and one that takes a reference to an instance of IntFormalArg and uses
     * it to create a copy.
     *
     *                                          JRM -- 1/25/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public IntFormalArg(Database db)
        throws SystemErrorException
    {

        super(db);

        this.fargType = fArgType.INTEGER;

    } /* IntFormalArg() -- one parameters */

    public IntFormalArg(Database db,
                        String name)
        throws SystemErrorException
    {

        super(db, name);

        this.fargType = fArgType.INTEGER;

    } /* IntFormalArg() -- two parameters */

    public IntFormalArg(Database db,
                        String name,
                        long minVal,
                        long maxVal)
          throws SystemErrorException
    {
        super(db, name);

        final String mName = "IntFormalArg::IntFormalArg(): ";

        this.fargType = fArgType.INTEGER;

        if ( minVal >= maxVal )

        {
            throw new SystemErrorException(mName + "minVal >= maxVal");
        }
        else if ( ( minVal < Long.MIN_VALUE ) || ( maxVal > Long.MAX_VALUE ) )
        {
            /* I don't think this can happen, but we will test for it anyway */
            throw new SystemErrorException(mName + "minVal or maxVal out of range");
        }
        else
        {
            this.subRange = true;
            this.maxVal = maxVal;
            this.minVal = minVal;
        }
    } /* IntFormalArg() -- three parameters */

    public IntFormalArg(IntFormalArg fArg)
        throws SystemErrorException
    {
        super(fArg);

        this.fargType = fArgType.INTEGER;

        // copy over fields.
        this.setRange(fArg.getMinVal(), fArg.getMaxVal());

    } /* IntFormalArg() -- make copy */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * setRange()
     *
     * Set the range of legal values that this formal arguement can assume.
     *
     * If the new minVal and maxVal describe the full range of the underlying
     * type (i.e.  minVal == Long.MIN_VALUE and maxVal == Long.MAX_VALUE), set
     * subRange to false.
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
    public void setRange(long minVal, long maxVal)
        throws SystemErrorException
    {
        final String mName = "IntFormalArg::setRange(): ";
        if ( minVal >= maxVal )

        {
            throw new SystemErrorException(mName + "minVal >= maxVal");
        }
        else if ( ( minVal < Long.MIN_VALUE ) || ( maxVal > Long.MAX_VALUE ) )
        {
            /* I don't think this can happen, but we will test for it anyway */
            throw new SystemErrorException(mName + "minVal or maxVal out of range");
        }
        else if ( ( minVal == Long.MIN_VALUE ) && ( maxVal == Long.MAX_VALUE ) )
        {
            this.subRange = false;
            this.maxVal = Long.MAX_VALUE;
            this.minVal = Long.MIN_VALUE;
        }
        else
        {
            this.subRange = true;
            this.maxVal = maxVal;
            this.minVal = minVal;
        }

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

    public long getMinVal()
    {
        return minVal;
    }

    public long getMaxVal()
    {
        return maxVal;
    }


    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/

    /**
     * constructArgWithSalvage()  Override of abstract method in FormalArgument
     *
     * Return an instance of IntDataValue initialized from salvage if
     * possible, and to the default for newly created instances of
     * IntDataValue otherwise.
     *
     * Changes:
     *
     *    - None.
     */

    DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException
    {
        IntDataValue retVal;

        if ( ( salvage == null ) ||
             ( salvage.getItsFargID() == DBIndex.INVALID_ID ) )
        {
            retVal = new IntDataValue(this.db, this.id);
        }
        else if ( salvage instanceof IntDataValue )
        {
            retVal = new IntDataValue(this.db, this.id,
                    ((IntDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof FloatDataValue )
        {
            retVal = new IntDataValue(this.db, this.id,
                ((Double)(((FloatDataValue)salvage).getItsValue())).longValue());
        }
        else
        {
            retVal = new IntDataValue(this.db, this.id);
        }

        return retVal;

    } /* IntDataValue::constructArgWithSalvage(salvage) */


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

         return new IntDataValue(this.db, this.id);

     } /* IntFormalArg::constructEmptyArg() */


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

        return ("(IntFormalArg " + getID() + " " + getFargName() + " " +
                subRange + " " + minVal + " " + maxVal +")");

    } /* IntFormalArg::toDBString() */


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
        if ( ! Database.IsValidInt(obj) )
        {
            return false;
        }

        /* If we get this far, obj must be a long */

        if ( ( subRange ) && ( ( (Long)obj < minVal ) || ( (Long)obj > maxVal ) ) )
        {
            return false;
        }

        return true;

    } /* IntFormalArg::isValidValue() */


    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessor methods for this class.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestAccessors(java.io.PrintStream outStream,
                                        boolean verbose)
    {
        String testBanner =
            "Testing class IntFormalArg accessors                             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        arg = null;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase());
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( arg == null ) || ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print(
                            "new IntFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new IntFormalArg(db) threw " +
                                      "system error exception: \"%s\"\n",
                                      systemErrorExceptionString);
                }
            }
        }

        /* test the inherited accessors */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                failures +=
                        FormalArgument.TestAccessors(arg, outStream, verbose);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("AbstractFormalArgument.TestAccessors." +
                            " threw a SystemErrorException.\n");
                }
            }
        }

        /* Now test accessors specific to IntFormalArg. */

        /* start by verifying the default values */
        if ( failures == 0 )
        {
            if ( arg.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected subRange(1): %b.\n",
                                       arg.getSubRange());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMinVal() != Long.MIN_VALUE )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected minVal(1): %d.\n",
                                       arg.getMinVal());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMaxVal() != Long.MAX_VALUE )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected maxVal(1): %d.\n",
                                       arg.getMaxVal());
                }
            }
        }

        /* now set the subRange... */
        if ( failures == 0 )
        {
            try
            {
                arg.setRange(1, 100);
            }

            catch ( SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( threwSystemErrorException ) ||
                  ( arg.getSubRange() != true ) ||
                  ( arg.getMinVal() != 1 ) ||
                  ( arg.getMaxVal() != 100 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("\"arg.setRange(1, 100)\" threw a " +
                                          "SystemErrorException.\n");
                    }

                    if ( arg.getSubRange() != true )
                    {
                        outStream.printf("Unexpected subRange(2): %b.\n",
                                          arg.getSubRange());
                    }

                    if ( arg.getMinVal() != 1 )
                    {
                        outStream.printf("Unexpected minVal(2): %d.\n",
                                          arg.getMinVal());
                    }

                    if ( arg.getMaxVal() != 100 )
                    {
                        outStream.printf("Unexpected maxVal(2): %d.\n",
                                          arg.getMaxVal());
                    }
                }
            }
        }

        /* ... and then set it back. */
        if ( failures == 0 )
        {
            try
            {
                arg.setRange(Long.MIN_VALUE, Long.MAX_VALUE);
            }

            catch ( SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( threwSystemErrorException ) ||
                  ( arg.getSubRange() != false ) ||
                  ( arg.getMinVal() != Long.MIN_VALUE ) ||
                  ( arg.getMaxVal() != Long.MAX_VALUE ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("\"arg.setRange(MIN_VALUE, MAX_VALUE)\""
                                + " threw a SystemErrorException.\n");
                    }

                    if ( arg.getSubRange() != false )
                    {
                        outStream.printf("Unexpected subRange(3): %b.\n",
                                          arg.getSubRange());
                    }

                    if ( arg.getMinVal() != Long.MIN_VALUE )
                    {
                        outStream.printf("Unexpected minVal(3): %d.\n",
                                          arg.getMinVal());
                    }

                    if ( arg.getMaxVal() != Long.MAX_VALUE )
                    {
                        outStream.printf("Unexpected maxVal(3): %d.\n",
                                          arg.getMaxVal());
                    }
                }
            }
        }

        /* Now attempt to set an invalid subrange */
        if ( failures == 0 )
        {
            try
            {
                arg.setRange(0, 0);
            }

            catch ( SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( ! threwSystemErrorException ) ||
                  ( arg.getSubRange() != false ) ||
                  ( arg.getMinVal() != Long.MIN_VALUE ) ||
                  ( arg.getMaxVal() != Long.MAX_VALUE ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("\"arg.setRange(0, 0)\""
                                + " didn't throw a SystemErrorException.\n");
                    }

                    if ( arg.getSubRange() != false )
                    {
                        outStream.printf("Unexpected subRange(4): %b.\n",
                                          arg.getSubRange());
                    }

                    if ( arg.getMinVal() != Long.MIN_VALUE )
                    {
                        outStream.printf("Unexpected minVal(4): %d.\n",
                                          arg.getMinVal());
                    }

                    if ( arg.getMaxVal() != Long.MAX_VALUE )
                    {
                        outStream.printf("Unexpected maxVal(4): %d.\n",
                                          arg.getMaxVal());
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

    } /* IntFormalArg::TestAccessors() */


    /**
     * TestVEAccessors()
     *
     * Run a battery of tests on the itsVocabElement and itsVocabElementID
     * accessor methods for this class.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestVEAccessors(java.io.PrintStream outStream,
                                          boolean verbose)
    {
        String testBanner =
            "Testing class IntFormalArg itsVocabElement accessors             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        arg = null;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase());
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( arg == null ) || ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print(
                            "new IntFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new IntFormalArg(db) threw " +
                                      "system error exception: \"%s\"\n",
                                      systemErrorExceptionString);
                }
            }
        }

        /* test the itsVocabElement & itsVocabElementID accessors */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                failures += FormalArgument.TestVEAccessors(arg, outStream,
                                                           verbose);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("FormalArgument.TestVEAccessors()" +
                            " threw a SystemErrorException.\n");
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

    } /* IntFormalArg::TestVEAccessors() */


    /**
     * TestClassIntFormalArg()
     *
     * Main routine for tests of class IntFormalArg.
     *
     *                                      JRM -- 3/10/07
     *
     * Changes:
     *
     *    - Non.
     */

    public static boolean TestClassIntFormalArg(java.io.PrintStream outStream,
                                                boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;

        outStream.print("Testing class IntFormalArg:\n");

        if ( ! Test1ArgConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! Test2ArgConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! Test4ArgConstructor(outStream, verbose) )
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

        if ( ! TestVEAccessors(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestIsValidValue(outStream, verbose) )
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
            outStream.printf("%d failures in tests for class IntFormalArg.\n\n",
                              failures);
        }
        else
        {
            outStream.print("All tests passed for class IntFormalArg.\n\n");
        }

        return pass;

    } /* Database::TestClassIntFormalArg() */


    /**
     * Test1ArgConstructor()
     *
     * Run a battery of tests on the one argument constructor for this
     * class, and on the instance returned.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test1ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing 1 argument constructor for class IntFormalArg            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        arg = null;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase());
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( arg == null ) || ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print(
                            "new IntFormalArg(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new IntFormalArg(db) threw " +
                                      "system error exception: \"%s\"\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if (failures == 0 )
        {
            if ( arg.getFargName().compareTo("<val>") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial fArgName \"%s\".\n",
                                       arg.getFargName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getHidden() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of hidden: %b.\n",
                                       arg.getHidden());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getItsVocabElement() != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("itsVocabElement not initialzed to null.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of subRange: %b.\n",
                                       arg.getSubRange());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMinVal() != Long.MIN_VALUE )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of minVal: %d.\n",
                                       arg.getMinVal());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMaxVal() != Long.MAX_VALUE )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of maxVal: %d.\n",
                                       arg.getMaxVal());
                }
            }
        }

        /* Verify that the constructor fails if passed a bad db */
        if ( failures == 0 )
        {
            arg = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                arg = new IntFormalArg((Database)null);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( methodReturned ) ||
                 ( arg != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("new IntFormalArg(null) returned.\n");
                    }

                    if ( arg != null )
                    {
                        outStream.print(
                                "new IntFormalArg(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new IntFormalArg(null) didn't " +
                                         "throw a system error exception.\n");
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

    } /* IntFormalArg::Test1ArgConstructor() */

    /**
     * Test2ArgConstructor()
     *
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instance returned.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test2ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing 2 argument constructor for class IntFormalArg            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<valid>");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print(
                        "new IntFormalArg(db, \"<valid>\") returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(db, \"<valid>\") " +
                                     "threw a SystemErrorException.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getFargName().compareTo("<valid>") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial fArgName \"%s\".\n",
                                       arg.getFargName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getHidden() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of hidden: %b.\n",
                                       arg.getHidden());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getItsVocabElement() != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("itsVocabElement not initialzed to null.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of subRange: %b.\n",
                                       arg.getSubRange());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMinVal() != Long.MIN_VALUE )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of minVal: %d.\n",
                                       arg.getMinVal());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMaxVal() != Long.MAX_VALUE )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of maxVal: %d.\n",
                                       arg.getMaxVal());
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db */
        arg = null;
        threwSystemErrorException = false;

        try
        {
            arg = new IntFormalArg(null, "<valid>");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( arg != null )
                {
                    outStream.print(
                        "new IntFormalArg(null, \"<valid>\") != null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(null, \"<valid>\") " +
                                     "didn't throw  a SystemErrorException.\n");
                }
            }
        }

        /* now verify that the constructor fails when passed an invalid
         * formal argument name.
         */
        arg = null;
        threwSystemErrorException = false;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<<invalid>>");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( arg != null )
                {
                    outStream.print(
                        "new IntFormalArg(db, \"<<invalid>>\") != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(db, \"<<invalid>>\") " +
                                    "failed to throw a SystemErrorException.\n");
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

    } /* IntFormalArg::Test2ArgConstructor() */


    /**
     * Test4ArgConstructor()
     *
     * Run a battery of tests on the four argument constructor for this
     * class, and on the instance returned.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test4ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing 4 argument constructor for class IntFormalArg            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<valid>", 0, 19);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print("new IntFormalArg(db, \"<valid>\", 0, 19) "
                                      + "returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(db, \"<valid>\", 0, 19) "
                                     + "threw a SystemErrorException.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getFargName().compareTo("<valid>") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial fArgName: \"%s\".\n",
                                       arg.getFargName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getHidden() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of hidden: %b.\n",
                                       arg.getHidden());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getItsVocabElement() != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("itsVocabElement not initialzed to null.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getSubRange() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of subRange: %b.\n",
                                       arg.getSubRange());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMinVal() != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of minVal: %d.\n",
                                       arg.getMinVal());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMaxVal() != 19 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of maxVal: %d.\n",
                                       arg.getMaxVal());
                }
            }
        }

        /* verify that the constructor fails when passed an invalid
         * formal argument name.
         */
        arg = null;
        threwSystemErrorException = false;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<<invalid>>", 0, 99);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( arg != null )
                {
                    outStream.print("new IntFormalArg(db, \"<<invalid>>\", " +
                            "0, 99) != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(db, \"<<invalid>>\", " +
                            "0, 99) failed to throw a SystemErrorException.\n");
                }
            }
        }


        /* verify that the constructor fails when passed an invalid
         * minVal, maxVal pair.
         */
        arg = null;
        threwSystemErrorException = false;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<valid>", 0, 0);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( arg != null )
                {
                    outStream.print(
                        "new IntFormalArg(db, \"<valid>\", 0, 0) != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(db, \"<valid>\", 0, 99)\""
                                  + " didn't throw a SystemErrorException.\n");
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

    } /* IntFormalArg::Test4ArgConstructor() */


    /**
     * TestCopyConstructor()
     *
     * Run a battery of tests on the copy constructor for this
     * class, and on the instance returned.
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestCopyConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing copy constructor for class IntFormalArg                  ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        IntFormalArg arg = null;
        IntFormalArg copyArg = null;
        IntFormalArg munged = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* first set up the instance of IntFormalArg to be copied: */
        threwSystemErrorException = false;

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<copy_this>", -10, 10);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( arg == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( arg == null )
                {
                    outStream.print("new IntFormalArg(db, \"<copy_this>\", " +
                                     "-10, 10)\" returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new IntFormalArg(db, \"<copy_this>\", -10, " +
                                     "10)\" threw a SystemErrorException.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                arg.setHidden(true);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( threwSystemErrorException )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("\"arg.setHidden(true)\" threw a " +
                                     "SystemErrorException.\n");
                }
            }
            else if ( ! arg.getHidden() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("Unexpected value of arg.hidden.\n");
                }
            }
        }


        /* Now, try to make a copy of arg */

        if ( failures == 0 )
        {
            copyArg = null;
            threwSystemErrorException = false;

            try
            {
                copyArg = new IntFormalArg(arg);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( copyArg == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg == null )
                    {
                        outStream.print(
                            "new IntFormalArg(arg)\" returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("new IntFormalArg(arg)\" " +
                                         "threw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* verify that the copy is good */

        if ( failures == 0 )
        {
            if ( arg == copyArg )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("(arg == copyArg) ==> " +
                            "same object, not duplicates.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getFargName().compareTo(copyArg.getFargName()) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg.fargName = \"%s\" != \" " +
                            "copyArg.fArgName = \"%s\".\n", arg.fargName,
                            copyArg.fargName);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getHidden() != copyArg.getHidden() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg.hidden = %b != " +
                            "copyArg.hidden = %b.\n", arg.hidden,
                            copyArg.hidden);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getItsVocabElement() != copyArg.getItsVocabElement() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg.getItsVocabElement() != \" " +
                            "copyArg.getItsVocabElement().\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getSubRange() != copyArg.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg.subRange = %b != " +
                            "copyArg.subRange = %b.\n", arg.subRange,
                            copyArg.subRange);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMinVal() != copyArg.getMinVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg.minVal = %d != " +
                            "copyArg.minVal = %d.\n", arg.minVal,
                            copyArg.minVal);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( arg.getMaxVal() != copyArg.getMaxVal() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("arg.maxVal = %d != " +
                            "copyArg.maxnVal = %d.\n", arg.maxVal,
                            copyArg.maxVal);
                }
            }
        }

        /* now verify that we fail when we should */

        /* first ensure that the copy constructor failes when passed null */
        if ( failures == 0 )
        {
            munged = copyArg; /* save the copy for later */
            copyArg = null;
            threwSystemErrorException = false;

            try
            {
                copyArg = null;
                copyArg = new IntFormalArg(copyArg);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( copyArg != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg != null )
                    {
                        outStream.print(
                            "new IntFormalArg(null)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new IntFormalArg(null)\" " +
                                       "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* now corrupt the fargName field of and instance of IntFormalArg,
         * and verify that this causes a copy to fail.
         */
        if ( failures == 0 )
        {
            copyArg = null;
            threwSystemErrorException = false;

            munged.fargName = "<an invalid name>";

            try
            {
                copyArg = new IntFormalArg(munged);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( copyArg != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg != null )
                    {
                        outStream.print(
                            "new IntFormalArg(munged1)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new IntFormalArg(munged1)\" " +
                                "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }

        /* now corrupt the minVal & maxVal fields of an instance of IntFormalArg,
         * and verify that this causes a copy to fail.
         */
        if ( failures == 0 )
        {
            copyArg = null;
            threwSystemErrorException = false;

            munged.fargName = "<a_valid_name>";
            munged.minVal = 0;
            munged.maxVal = 0;

            try
            {
                copyArg = new IntFormalArg(munged);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( copyArg != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg != null )
                    {
                        outStream.print(
                            "new IntFormalArg(munged2)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new IntFormalArg(munged1)\" " +
                                     "didn't throw a SystemErrorException.\n");
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

    } /* IntFormalArg::TestCopyConstructor() */


    /**
     * TestIsValidValue()
     *
     * Verify that isValidValue() does more or less the right thing.
     *
     * Since isValidValue() uses the type tests defined in class Database,
     * and since those methods are tested extensively elsewhere, we only
     * need to verify that they are called correctly.
     *
     *                                          JRM -- 3/11/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidValue(java.io.PrintStream outStream,
                                           boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing isValidValue()                                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        boolean result;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 19;
        /* TODO -- must add predicates to this test */
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return false */ " A Valid \t Text String ",
            /* test  1 -- should return false */ new Double(0.0),
            /* test  2 -- should return true  */ new Long(0),
            /* test  3 -- should return false */ "A Valid Nominal",
            /* test  4 -- should return false */ " A Valid Quote String ",
            /* test  5 -- should return false */ new TimeStamp(60),
            /* test  6 -- should return false */ new TimeStamp(30, 300),
            /* test  7 -- should return false */ "an invalid text \b string",
            /* test  8 -- should return false */ new Float(0.0),
            /* test  9 -- should return false */ new Integer(0),
            /* test 10 -- should return false */ " An Invalid Nominal \b ",
            /* test 11 -- should return false */ " An Invalid \t Quote string ",
            /* test 12 -- should return false */ new Long(-1),
            /* test 13 -- should return true  */ new Long(1),
            /* test 14 -- should return true  */ new Long(2),
            /* test 15 -- should return true  */ new Long(3),
            /* test 16 -- should return true  */ new Long(4),
            /* test 17 -- should return true  */ new Long(5),
            /* test 18 -- should return false */ new Long(6),
       };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return false */ " A Valid Text String ",
            /* test  1 -- should return false */ "new Double(0.0)",
            /* test  2 -- should return true  */ "new Long(0)",
            /* test  3 -- should return false */ "A Valid Nominal",
            /* test  4 -- should return false */ " A Valid Quote String ",
            /* test  5 -- should return false */ "new TimeStamp(60)",
            /* test  6 -- should return false */ "new TimeStamp(30, 300)",
            /* test  7 -- should return false */ "an invalid text \b string",
            /* test  8 -- should return false */ "new Float(0.0)",
            /* test  9 -- should return false */ "new Integer(0)",
            /* test 10 -- should return false */ " An Invalid \t Nominal \b ",
            /* test 11 -- should return false */ " An Invalid \t Quote string ",
            /* test 12 -- should return false */ "new Long(-1)",
            /* test 13 -- should return true  */ "new Long(1)",
            /* test 14 -- should return true  */ "new Long(2)",
            /* test 15 -- should return true  */ "new Long(3)",
            /* test 16 -- should return true  */ "new Long(4)",
            /* test 17 -- should return true  */ "new Long(5)",
            /* test 18 -- should return false */ "new Long(6)",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ false,
            /* test  1 should return */ false,
            /* test  2 should return */ true,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ false,
            /* test 11 should return */ false,
            /* test 12 should return */ false,
            /* test 13 should return */ true,
            /* test 14 should return */ true,
            /* test 15 should return */ true,
            /* test 16 should return */ true,
            /* test 17 should return */ true,
            /* test 18 should return */ false,
        };
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            arg = new IntFormalArg(new ODBCDatabase(), "<arg>", 0, 5);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( arg == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("new IntFormalArg()\" returned null.\n");
            }
        }

        if ( arg != null )
        {
            while ( testNum < numTestObjects )
            {
                if ( verbose )
                {
                    outStream.printf("test %d: arg.isValidValue(%s) --> %b: ",
                            testNum, testDesc[testNum],
                            expectedResult[testNum]);
                }

                threwSystemErrorException = false;
                result = false;

                try
                {
                    result = arg.isValidValue(testObjects[testNum]);
                }
                catch (SystemErrorException e)
                {
                    threwSystemErrorException = true;
                }

                if ( ( threwSystemErrorException ) ||
                     ( result != expectedResult[testNum] ) )
                {
                    failures++;
                    if ( verbose )
                    {
                        if ( threwSystemErrorException )
                        {
                            outStream.print("failed -- unexpected exception.\n");
                        }
                        else
                        {
                            outStream.print("failed.\n");
                        }
                    }
                }
                else if ( verbose )
                {
                    outStream.print("passed.\n");
                }

                testNum++;
            }
        }

        /* Now verify that isValidValue() throws a system error when passed
         * a null.
         */

        if ( arg != null )
        {
            if ( verbose )
            {
                outStream.printf(
                        "test %d: arg.isValidValue(null) --> exception: ",
                        testNum);
            }

            methodReturned = false;
            threwSystemErrorException = false;
            result = false;

            try
            {
                result = arg.isValidValue(null);
                methodReturned = true;
            }
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( result != false ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( threwSystemErrorException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    else if ( methodReturned )
                    {
                        outStream.print("failed -- unexpected return.\n");
                    }
                    else
                    {
                        outStream.print("failed -- unexpected result.\n");
                    }
                }
            }
            else if ( verbose )
            {
                outStream.print("passed.\n");
            }

            testNum++;
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

    } /* IntFormalArg::TestIsValidValue() */


    /**
     * TestToStringMethods()
     *
     * Test the toString() and toDBString() methods.
     *
     *              JRM -- 3/11/07
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
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        IntFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        if ( failures == 0 )
        {
            threwSystemErrorException = false;

            try
            {
                arg = new IntFormalArg(new ODBCDatabase(), "<test>", 0, 10);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( arg == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( arg == null )
                    {
                        outStream.print(
                                "new UnTypedFormalArg(db, \"<test>\", 0, 10) " +
                                "returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print(
                                "new UnTypedFormalArg(db, \"<test>\", 0, 10) " +
                                "threw a SystemErrorException.\n");
                    }
                }

                arg = null;
            }
        }

        if ( arg != null )
        {
            if ( arg.toString().compareTo("<test>") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "arg.toString() returned unexpected value: \"%s\".\n",
                        arg.toString());
                }
            }
        }

        if ( arg != null )
        {
            if ( arg.toDBString().compareTo("(IntFormalArg 0 <test> true 0 10)")
                != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "arg.toDBString() returned unexpected value: \"%s\".\n",
                        arg.toDBString());
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

    } /* IntFormalArg::TestToStringMethods() */

} /* class IntFormalArg */
