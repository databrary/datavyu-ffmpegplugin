/*
 * IntDataValue.java
 *
 * Created on August 16, 2007, 7:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;


/**
 * An instance of IntDataValue is used to store an integer value
 * assigned to a formal argument.
 *
 * @author mainzer
 */
public class IntDataValue extends DataValue
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * itsDefault:  Constant containing the value to be assigned to all 
     *      integer data values unless otherwise specified.
     *
     * itsValue:   Long containing the value assigned to the formal argument.
     *
     * minVal:  If subRange is true, this field contains the minimum value
     *      that may be assigned to the formal argument associated with the
     *      data value.   This value should always be the same as the minVal
     *      of the associated instance of OmtFormalArg.
     *
     *      Note that this data value may be used to hold an integer
     *      value assigned to an untype formal argument, in which case 
     *      subrange will always be false.
     *
     * maxVal:  If subRange is true, this field contains the maximum value
     *      that may be assigned to the formal argument associated with the
     *      data value.   This value should always be the same as the maxVal
     *      of the associated instance of IntFormalArg.
     *
     *      Note that this data value may be used to hold an integer
     *      value assigned to an untype formal argument, in which case 
     *      subrange will always be false.
     */
    
    /** default value for integers */
    final long ItsDefault = 0;
    
    /** the value assigned to the associated formal argument in this case */
    long itsValue = ItsDefault;
    
    /** the minimum value -- if subrange is true */
    long minVal = 0;
    
    /** the maximum value -- if subrange is true */
    long maxVal = 0;
  
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    
    /** 
     * IntDataValue()
     *
     * Constructor for instances of IntDataValue.  
     * 
     * Four versions of this constructor.  
     * 
     * The first takes a reference to a database as its parameter and just 
     * calls the super() constructor.
     *
     * The second takes a reference to a database, and a formal argument ID,
     * and attempts to set the itsFargID field of the data value accordingly.
     *
     * The third takes a reference to a database, a formal argument ID, and 
     * a value as arguments, and attempts to set the itsFargID and itsValue 
     * of the data value accordingly.
     *
     * The fourth takes a reference to an instance of IntDataValue as an
     * argument, and uses it to create a copy.
     *
     *                                              JRM -- 8/16/07  
     *
     * Changes:
     *
     *    - None.
     *      
     */
 
    public IntDataValue(Database db)
        throws SystemErrorException
    {
        super(db);
        
    } /* IntDataValue::IntDataValue(db) */
    
    public IntDataValue(Database db,
                        long fargID)
        throws SystemErrorException
    {
        super(db);
        
        this.setItsFargID(fargID);
    
    } /* IntDataValue::IntDataValue(db, fargID) */
    
    public IntDataValue(Database db,
                        long fargID,
                        long value)
        throws SystemErrorException
    {
        super(db);
        
        this.setItsFargID(fargID);
        
        this.setItsValue(value);
    
    } /* IntDataValue::IntDataValue(db, fargID, value) */
    
    public IntDataValue(IntDataValue dv)
        throws SystemErrorException
    {
        super(dv);
        
        this.itsValue  = dv.itsValue;
        this.minVal    = dv.minVal;
        this.maxVal    = dv.maxVal;
        
    } /* IntDataValue::IntDataValue(dv) */
    
        
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
    
    public long getItsValue()
    {
        
        return this.itsValue;
    
    } /* IntDataValue::getItsValue() */
    
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
    
    public void setItsValue(long value)
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
        
    } /* IntDataValue::setItsValue() */
  
        
    /*************************************************************************/
    /*************************** Overrides: **********************************/
    /*************************************************************************/
    
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
        return ("(IntDataValue (id " + this.id +
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
        final String mName = "IntDataValue::updateForFargChange(): ";
        
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
        
    } /* IntDataValue::updateForFargChange() */
    
    
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
        final String mName = "IntDataValue::updateSubRange(): ";
        
        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");    
        }
        
        if ( fa instanceof IntFormalArg )
        {
            IntFormalArg ifa = (IntFormalArg)fa;
            
            this.subRange = ifa.getSubRange();
            
            if ( this.subRange )
            {
                this.maxVal = ifa.getMaxVal();
                this.minVal = ifa.getMinVal();
                
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
            this.subRange = false;
        }
        else
        {
            throw new SystemErrorException(mName + "Unexpected fa type");    
        }
        
        return;
        
    } /* IntDataValue::updateSubRange() */
  
        
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
    
    public long coerceToRange(long value)
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
        
    } /* IntDataValue::coerceToRange() */
  
    
    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/
    
    /**
     * Construct()
     *
     * Construct an instance of IntDataValue with the specified initialization.
     *
     * Returns a reference to the newly constructed IntDataValue if successful.
     * Throws a system error exception on failure.
     *
     *                                              JRM -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */
    
    public static IntDataValue Construct(Database db,
                                         long i)
        throws SystemErrorException
    {
        final String mName = "IntDataValue::Construct(db, i)";
        IntDataValue idv = null;
        
        idv = new IntDataValue(db);
        
        idv.setItsValue(i);
        
        return idv;
        
    } /* IntDataValue::Construct(db, i) */

    
    /**
     * IntDataValuesAreLogicallyEqual()
     *
     * Given two instances of IntDataValue, return true if they contain 
     * identical data, and false otherwise.
     *
     * Note that this method does only tests specific to this subclass of 
     * DataValue -- the presumption is that this method has been called by 
     * DataValue.DataValuesAreLogicallyEqual() which has already done all
     * generic tests.
     *                                              JRM -- 2/7/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected static boolean IntDataValuesAreLogicallyEqual(IntDataValue idv0,
                                                            IntDataValue idv1)
        throws SystemErrorException
    {
        final String mName = "IntDataValue::IntDataValuesAreLogicallyEqual()";
        boolean dataValuesAreEqual = true;
        
        if ( ( idv0 == null ) || ( idv1 == null ) )
        {
            throw new SystemErrorException(mName + 
                                           ": idv0 or idv1 null on entry.");
        }
        
        if ( idv0 != idv1 )
        {
            if ( ( idv0.itsValue != idv1.itsValue ) ||
                 ( idv0.maxVal != idv1.maxVal ) ||
                 ( idv0.minVal != idv1.minVal ) )
            {
                dataValuesAreEqual = false;
            }
        }

        return dataValuesAreEqual;
        
    } /* IntDataValue::IntDataValuesAreLogicallyEqual() */

    
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
     *         argument constructor for IntDataValue.  Verify that all
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
     *         Construct a IntDataValue for the formal argument of the mve
     *         by passing a reference to the database and the id of the formal
     *         argument.  Verify that the IntDataValue's itsFargID, 
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
     *      IntDataValue -- perhaps after havign been modified to match
     *      the subrange.
     *              
     * 4) Copy constructor:
     *
     *      a) Construct a database and possibly a mve (matrix vocab element) 
     *         and such formal arguments as are necessary.  If an mve is 
     *         created, insert it into the database, and make note of the IDs 
     *         assigned.  Then create a  IntDataValue (possibly using 
     *         the using a formal argument ID).
     *
     *         Now use the copy constructor to create a copy of the 
     *         IntDataValue, and verify that the copy is correct. 
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
     * TestClassIntDataValue()
     *
     * Main routine for tests of class IntDataValue.
     *
     *                                      JRM -- 10/15/07
     *
     * Changes:
     *
     *    - Non.
     */
    
    public static boolean TestClassIntDataValue(java.io.PrintStream outStream,
                                                  boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing class IntDataValue:\n");
        
        if ( ! TestAccessors(outStream, verbose) )
        {
            failures++;
        }
       
        if ( failures > 0 )
        {
            pass = false;
            outStream.printf("%d failures in tests for class IntDataValue.\n\n",
                             failures);
        }
        else
        {
            outStream.print("All tests passed for class IntDataValue.\n\n");
        }
        
        return pass;
        
    } /* IntDataValue::TestClassIntDataValue() */  
    
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
            "Testing class IntDataValue accessors                             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement int_mve = null;
        MatrixVocabElement matrix_mve = null;
        IntFormalArg ifa = null;
        UnTypedFormalArg ufa = null;
        IntDataValue idv0 = null;
        IntDataValue idv1 = null;
        IntDataValue idv2 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        db = null;
        idv0 = null;
        idv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();
            
            int_mve = new MatrixVocabElement(db, "int_mve");
            int_mve.setType(MatrixVocabElement.matrixType.INTEGER);
            ifa = new IntFormalArg(db);
            ifa.setRange(-1000, +1000);
            int_mve.appendFormalArg(ifa);
            db.vl.addElement(int_mve);

            idv0 = new IntDataValue(db, ifa.getID(), 200);
            
            matrix_mve = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.matrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve);

            idv1 = new IntDataValue(db, ufa.getID(), 2000);
            idv2 = new IntDataValue(db, ufa.getID(), 999);
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( int_mve == null ) ||
             ( ifa == null ) ||
             ( idv0 == null ) ||
             ( matrix_mve == null ) ||
             ( ufa == null ) ||
             ( idv1 == null ) ||
             ( idv2 == null ) ||
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
                
                if ( int_mve == null )
                {
                    outStream.print("allocation of int_mve failed.\n");
                }
                
                if ( ifa == null )
                {
                    outStream.print("allocation of ifa failed.\n");
                }

                if ( idv0 == null )
                {
                    outStream.print("new IntDataValue(db, ifa.getID(), " +
                                    "200) returned null.\n");
                }
                
                if ( matrix_mve == null )
                {
                    outStream.print("allocation of matrix_mve failed.\n");
                }
                
                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( idv1 == null )
                {
                    outStream.print("new IntDataValue(db, ufa.getID(), " +
                                    "2000) returned null.\n");
                }

                if ( idv2 == null )
                {
                    outStream.print("new IntDataValue(db, ufa.getID(), " +
                                    "999) returned null.\n");
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
            failures += DataValue.TestAccessors(db, ifa, matrix_mve, ufa,
                                                idv0, outStream, verbose);
            
            if ( idv0.getSubRange() != false )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("idv0.getSubRange() != false");
                }
            }
            
            if ( idv0.getItsValue() != 200 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("idv.getItsValue() != 200\n");
                }
            }
            
            idv0.setItsValue(3);

            
            if ( idv0.getItsValue() != 3 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("idv0.getItsValue() != 3\n");
                }
            }
            
            /************************************/

            if ( idv1.getSubRange() != false )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("idv1.getSubRange() != false\n");
                }
            }
            
            if ( idv1.getItsValue() != 2000 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("idv1.getItsValue() != 2000\n");
                }
            }
            
            failures += DataValue.TestAccessors(db, ufa, int_mve, ifa,
                                                idv1, outStream, verbose);

            if ( idv1.getSubRange() != true )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("idv1.getSubRange() != true\n");
                }
            }
            
            if ( idv1.getItsValue() != 1000 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("idv1.getItsValue() != 1000\n");
                }
            }
            
            idv1.setItsValue(-50000);
            
            if ( idv1.getItsValue() != -1000 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("idv1.getItsValue() != -1000\n");
                }
            }
            
            if ( ( idv1.coerceToRange(1001) != 1000 ) ||
                 ( idv1.coerceToRange(1000) != 1000 ) ||
                 ( idv1.coerceToRange(999) != 999 ) ||
                 ( idv1.coerceToRange(998) != 998 ) ||
                 ( idv1.coerceToRange(47) != 47 ) ||
                 ( idv1.coerceToRange(-25) != -25 ) ||
                 ( idv1.coerceToRange(-999) != -999 ) ||
                 ( idv1.coerceToRange(-1000) != -1000 ) ||
                 ( idv1.coerceToRange(-1001) != -1000 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "unexpected results from idv1.coerceToRange()\n");
                }
            }
            
            /************************************/
            
            failures += DataValue.TestAccessors(db, ufa, int_mve, ifa,
                                                idv2, outStream, verbose);

            if ( idv2.getItsValue() != 999 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("idv2.getItsValue() != 999\n");
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
        
    } /* IntDataValue::TestAccessors() */
} /* IntDataValue */
