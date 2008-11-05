/*
 * QuoteStringDataValue.java
 *
 * Created on August 18, 2007, 3:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * An instance of QuoteStringDataValue is used to store a quote string value
 * assigned to a formal argument.
 *
 * @author mainzer
 */

public class QuoteStringDataValue extends DataValue
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * itsDefault:  Constant containing the value to be assigned to all 
     *      float data values unless otherwise specified.
     *
     * itsValue:   Long containing the value assigned to the formal argument.
     *
     * minVal & maxVal don't appear in QuoteStringDataValue as at present, 
     *      we don't support subranging in quote strings
     */
    
    /** default value for quote strings */
    final String ItsDefault = null;
    
    /** the value assigned to the associated formal argument in this case */
    String itsValue = ItsDefault;
      
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    
    /** 
     * QuoteStringDataValue()
     *
     * Constructor for instances of QuoteStringDataValue.  
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
     * The fourth takes a reference to an instance of QuoteStringDataValue as an
     * argument, and uses it to create a copy.
     *
     *                                              JRM -- 8/16/07  
     *
     * Changes:
     *
     *    - None.
     *      
     */
 
    public QuoteStringDataValue(Database db)
        throws SystemErrorException
    {
        
        super(db);
        
    } /* QuoteStringDataValue::QuoteStringDataValue(db) */
    
    public QuoteStringDataValue(Database db,
                                long fargID)
        throws SystemErrorException
    {
        super(db);
        
        this.setItsFargID(fargID);
        
    } /* QuoteStringDataValue::QuoteStringDataValue(db, fargID) */
    
    public QuoteStringDataValue(Database db,
                                long fargID,
                                String value)
        throws SystemErrorException
    {
        super(db);
        
        this.setItsFargID(fargID);
        
        this.setItsValue(value);
        
    } /* QuoteStringDataValue::QuoteStringDataValue(db, fargID, value) */
    
    public QuoteStringDataValue(QuoteStringDataValue dv)
        throws SystemErrorException
    {
        
        super(dv);
        
        if ( dv.itsValue != null )
        {
            this.itsValue = new String(dv.itsValue);
        }
        else
        {
            this.itsValue = null;
        }
    
    } /* QuoteStringDataValue::QuoteStringDataValue(dv) */
    
        
    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getItsValue()
     *
     * If the data value is currently defined, return a string containing a 
     * copy of the the current value of the data value.  Otherwise return null.
     *
     *                          JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public String getItsValue()
    {
        
        if ( this.itsValue == null )
        {
            return null;
        }
        else
        {
            return (new String(this.itsValue));
        }
        
    } /* QuoteStringDataValue::getItsValue() */
    

    /**
     * setItsValue()
     *
     * Set itsValue to the specified value. 
     *
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public void setItsValue(String value)
        throws SystemErrorException
    {
        final String mName = "QuoteStringDataValue::setItsValue(): ";
        
        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            this.itsValue = null;
        }
        else if ( ! ( db.IsValidQuoteString(value) ) )
        {
            throw new SystemErrorException(mName + 
                                           "value not valid quote string");
        }
        else
        {
            this.itsValue = new String(value);
        }
        
        return;
        
    } /* QuoteStringDataValue::setItsValue() */
  
        
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
        if ( this.itsValue == null )
        {
            return "\"\"";
        }
        else
        {
            return "\"" + new String(this.itsValue) + "\"";
        }
        
    } /* QuoteStringDataValue::toString() */


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
        if ( this.itsValue == null )
        {
            return ("(QuoteStringDataValue (id " + this.id +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + "<null>" +
                    ") (subRange " + this.subRange + "))");
        }
        else
        {
            return ("(QuoteStringDataValue (id " + this.id +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + new String(this.itsValue) +
                    ") (subRange " + this.subRange + "))");
        }

    } /* QuoteStringDataValue::toDBString() */
    
    
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
        final String mName = "QuoteStringDataValue::updateForFargChange(): ";
        
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
        
    } /* QuoteStringDataValue::updateForFargChange() */
    
    
    /**
     * updateSubRange()
     *
     * Nominally, this method should determine if the formal argument 
     * associated with the data value is subranged, and if it is, update
     * the data values representation of  the subrange (if ant) accordingly.  
     * In passing, it should coerce the value of  the datavalue into the 
     * subrange if necessary.
     *
     * However, quote strings can't be subranged at present, so all we do 
     * is verify that the formal argument doesn't think otherwise.
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
        final String mName = "QuoteStringDataValue::updateSubRange(): ";
        
        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");    
        }
        
        if ( fa instanceof QuoteStringFormalArg )
        {
            QuoteStringFormalArg qfa = (QuoteStringFormalArg)fa;
            
            if ( qfa.getSubRange() != false ) 
            {
                throw new SystemErrorException(mName +
                                               "qfa.getSubRange() != FALSE"); 
            }
            
            this.subRange = false;
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
        
    } /* QuoteStringDataValue::updateSubRange() */
  
        
    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/
    
    /**
     * coerceToRange()
     *
     * Nominally, this function tests to see if the supplied value is 
     * in range for the associated formal argument, returns it if it
     * is, and coerces it into range if it isn't.
     *
     * However, we don't support subranges for quote strings.
     *
     * Thus we simply check to see if the value is valid, and return the 
     * value if it is.  If it isn't, throw a system error.
     *
     *                                              JRM -- 07/08/18
     *
     * Changes:
     *
     *    - None.
     */
    
    public String coerceToRange(String value)
        throws SystemErrorException
    {
        final String mName = "QuoteStringDataValue::coerceToRange(): ";

        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            return value;
        }
        
        if ( ! this.db.IsValidQuoteString(value) )
        {
            throw new SystemErrorException(mName + 
                                           "value isn't valid quote string"); 
        }
        
        return value;
        
    } /* QuoteStringDataValue::coerceToRange() */
  
    
    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/
    
    /**
     * Construct()
     *
     * Construct an instance of QuoteStringDataValue with the specified 
     * initialization.
     *
     * Returns a reference to the newly constructed QuoteStringDataValue if 
     * successful.  Throws a system error exception on failure.
     *
     *                                              JRM -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */
    
    public static QuoteStringDataValue Construct(Database db,
                                                 String qs)
        throws SystemErrorException
    {
        final String mName = "QuoteStringDataValue::Construct(db, qs)";
        QuoteStringDataValue qsdv = null;
        
        qsdv = new QuoteStringDataValue(db);
        
        qsdv.setItsValue(qs);
        
        return qsdv;
        
    } /* QuoteStringDataValue::Construct(db, qs) */
      
      
    /**
     * QuoteStringDataValuesAreLogicallyEqual()
     *
     * Given two instances of QuoteStringDataValue, return true if they contain 
     * identical data, and false otherwise.
     *
     * Note that this method does only tests specific to this subclass of 
     * DataValue -- the presumption is that this method has been called by 
     * DataValue.DataValuesAreLogicallyEqual() which has already done all
     * generic tests.
     * 
     *                                              JRM -- 2/7/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected static boolean QuoteStringDataValuesAreLogicallyEqual
            (QuoteStringDataValue qsdv0,
             QuoteStringDataValue qsdv1)
        throws SystemErrorException
    {
        final String mName = 
            "QuoteStringDataValue::QuoteStringDataValuesAreLogicallyEqual()";
        boolean dataValuesAreEqual = true;
        
        if ( ( qsdv0 == null ) || ( qsdv1 == null ) )
        {
            throw new SystemErrorException(mName + 
                                           ": qsdv0 or qsdv1 null on entry.");
        }
        
        if ( qsdv0 != qsdv1 )
        {
            if ( qsdv0.itsValue != qsdv1.itsValue )
            {
                if ( ( ( qsdv0.itsValue == null ) 
                       &&
                       ( qsdv1.itsValue != null ) 
                     )
                     ||
                     ( ( qsdv0.itsValue != null ) 
                       &&
                       ( qsdv1.itsValue == null ) 
                     )
                   )
                {
                    dataValuesAreEqual = false;
                }
                // due to the above tests, if we get this far, we know
                // that both qsdv0.itsValue and qsdv1.itsValue are non-null.
                else if ( qsdv0.itsValue.compareTo(qsdv1.itsValue) != 0 )
                {
                    dataValuesAreEqual = false;
                }
            }
        }

        return dataValuesAreEqual;
        
    } /* QuoteStringDataValue::QuoteStringDataValuesAreLogicallyEqual() */


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
     *         argument constructor for QuoteStringDataValue.  Verify that all
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
     *         Construct a QuoteStringDataValue for the formal argument of the 
     *         mve by passing a reference to the database and the id of the 
     *         formal argument.  Verify that the QuoteStringDataValue's 
     *         itsFargID, and itsFargType fields matches those of the formal 
     *         argument, and that all other fields are set to the expected 
     *         defaults.
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
     *      QuoteStringDataValue.
     *              
     * 4) Copy constructor:
     *
     *      a) Construct a database and possibly a mve (matrix vocab element) 
     *         and such formal arguments as are necessary.  If an mve is 
     *         created, insert it into the database, and make note of the IDs 
     *         assigned.  Then create a  QuoteStringDataValue (possibly using 
     *         the using a formal argument ID).
     *
     *         Now use the copy constructor to create a copy of the 
     *         QuoteStringDataValue, and verify that the copy is correct. 
     *
     *         Repeat the test for a variety of instances of FloatFormalArg.
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad 
     *         value that has to be tested.
     *
     * 5) Accessors:
     *
     *      Verify that the getItsValue(), and setItsValue() methods perform 
     *      correctly.  Verify that the inherited accessors function correctly 
     *      via calls to the DataValue.TestAccessors() method.
     *
     *      Given compiler error checking, there isn't any way to feed
     *      invalid data to the getItsValue(), and setItsValue().
     *
     * 6) toString methods:
     *
     *      Verify that all fields are displayed correctly by the toString
     *      and toDBString() methods. 
     *
     * 
     *************************************************************************/

    /**
     * TestClassQuoteStringDataValue()
     *
     * Main routine for tests of class QuoteStringDataValue.
     *
     *                                      JRM -- 10/15/07
     *
     * Changes:
     *
     *    - Non.
     */
    
    public static boolean TestClassQuoteStringDataValue(
                                                  java.io.PrintStream outStream,
                                                  boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing class QuoteStringDataValue:\n");
        
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
                    "%d failures in tests for class QuoteStringDataValue.\n\n",
                    failures);
        }
        else
        {
            outStream.print(
                    "All tests passed for class QuoteStringDataValue.\n\n");
        }
        
        return pass;
        
    } /* QuoteStringDataValue::TestClassQuoteStringDataValue() */
    
    
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
            "Testing 1 argument constructor for class QuoteStringDataValue    ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        QuoteStringDataValue qsdv = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        db = null;
        qsdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();
            qsdv = new QuoteStringDataValue(db);
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( qsdv == null ) ||
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

                if ( qsdv == null )
                {
                    outStream.print(
                            "new QuoteStringDataValue(db) returned null.\n");
                }
                
                if ( ! completed )
                {
                    outStream.printf(
                        "new QuoteStringDataValue(db) failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("new QuoteStringDataValue(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.Verify1ArgInitialization(db, qsdv, outStream, 
                                                           verbose);

            if ( qsdv.ItsDefault != null )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(" qsdv.ItsDefault != null.\n");
                }
            }
            
            if ( qsdv.itsValue != qsdv.ItsDefault )
            {
                failures++;
                
                if ( verbose )
                {
                    String s1;
                    String s2;
                    
                    if ( qsdv.itsValue == null )
                        s1 = new String("<null>");
                    else
                        s1 = qsdv.itsValue;
                    
                    if ( qsdv.ItsDefault == null )
                        s2 = new String("<null>");
                    else
                        s2 = qsdv.ItsDefault;
                    
                    outStream.printf(
                            "qsdv.itsValue = %s != qsdv.ItsDefault = %s.\n",
                            s1, s2);
                }
            }
        }
         
        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue((Database)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(null) returned.\n");
                    }

                    if ( qsdv != null )
                    {
                        outStream.print(
                                "new QuoteStringDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new QuoteStringDataValue(null) failed to throw " +
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
        
    } /* QuoteStringDataValue::Test1ArgConstructor() */
    
    
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
            "Testing 2 argument constructor for class QuoteStringDataValue    ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement qs_mve = null;
        QuoteStringFormalArg qsfa = null;
        QuoteStringDataValue qsdv = null;

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
            
            qs_mve = new MatrixVocabElement(db, "qs_mve");
            qs_mve.setType(MatrixVocabElement.matrixType.MATRIX);
            qsfa = new QuoteStringFormalArg(db);
            qs_mve.appendFormalArg(qsfa);
            db.vl.addElement(qs_mve);

            qsdv = new QuoteStringDataValue(db, qsfa.getID());
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( qs_mve == null ) ||
             ( qsfa == null ) ||
             ( qsdv == null ) ||
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
                
                if ( qs_mve == null )
                {
                    outStream.print("allocation of qs_mve failed.\n");
                }
                
                if ( qsfa == null )
                {
                    outStream.print("allocation of qsfa failed.");
                }

                if ( qsdv == null )
                {
                    outStream.print("new QuoteStringDataValue(db, " +
                                    "qsfa.getID()) returned null.\n");
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
                                                               qsfa, 
                                                               qsdv,  
                                                               outStream, 
                                                               verbose,
                                                               "qsdv");
            
            if ( qsdv.ItsDefault != null )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("qsdv.ItsDefault != null.\n");
                }
            }

            if ( qsdv.subRange != qsfa.getSubRange() )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "qsdv.subRange doesn't match qsfa.getSubRange().\n");
                }
            }
            
            if ( qsdv.itsValue != qsdv.ItsDefault )
            {
                failures++;
                
                if ( verbose )
                {
                    String s1;
                    String s2;
                    
                    if ( qsdv.itsValue == null )
                        s1 = new String("<null>");
                    else
                        s1 = qsdv.itsValue;
                    
                    if ( qsdv.ItsDefault == null )
                        s2 = new String("<null>");
                    else
                        s2 = qsdv.ItsDefault;
                    
                    outStream.printf(
                            "qsdv.itsValue = %s != qsdv.ItsDefault = %s.\n",
                            s1, s2);
                }
            }
        }
         
        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue((Database)null, qsfa.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(null, " +
                                        "qsfa.getID()) returned.\n");
                    }

                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(null, " +
                                        "qsfa.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new QuoteStringDataValue(null, " +
                                "qsfa.getID()) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }
         
        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue(db, DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                        "INVALID_ID) returned.\n");
                    }

                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                        "INVALID_ID) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new QuoteStringDataValue(db, INVALID_ID) " +
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
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue(db, qs_mve.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                        "qs_mve.getID()) returned.\n");
                    }

                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                "qs_mve.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                            "new QuoteStringDataValue(db, qs_mve.getID()) " +
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
        
    } /* QuoteStringDataValue::Test2ArgConstructor() */
    
    
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
            "Testing 3 argument constructor for class QuoteStringDataValue    ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement qs_mve = null;
        QuoteStringFormalArg qsfa = null;
        QuoteStringDataValue qsdv = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        db = null;
        qsdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();
            
            qs_mve = new MatrixVocabElement(db, "qs_mve");
            qs_mve.setType(MatrixVocabElement.matrixType.MATRIX);
            qsfa = new QuoteStringFormalArg(db);
            qs_mve.appendFormalArg(qsfa);
            db.vl.addElement(qs_mve);

            qsdv = new QuoteStringDataValue(db, qsfa.getID(), "echo");
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( qs_mve == null ) ||
             ( qsfa == null ) ||
             ( qsdv == null ) ||
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
                
                if ( qs_mve == null )
                {
                    outStream.print("allocation of qs_mve failed.\n");
                }
                
                if ( qsfa == null )
                {
                    outStream.print("allocation of qsfa failed.");
                }

                if ( qsdv == null )
                {
                    outStream.print(
                            "new QuoteStringDataValue(db, qsfa.getID(), " +
                            "\"echo\") returned null.\n");
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
                                                               qsfa, 
                                                               qsdv,  
                                                               outStream, 
                                                               verbose,
                                                               "qsdv");

            if ( qsdv.subRange != qsfa.getSubRange() )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "qsdv.subRange doesn't match qsfa.getSubRange().\n");
                }
            }
            
            if ( ( qsdv.itsValue == null ) ||
                 ( qsdv.itsValue.compareTo("echo") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("qsdv.itsValue != \"echo\".\n");
                }
            }
        }
         
        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue((Database)null, qsfa.getID(), 
                                                "alpha");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(null, " +
                                "qsfa.getID(), \"alpha\") returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(null, " +
                                        "qsfa.getID(), \"alpha\") returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new QuoteStringDataValue(null, qsfa.getID(), " +
                                "\"alpha\") failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }
         
        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue(db, DBIndex.INVALID_ID, "alpha");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                "INVALID_ID, \"alpha\") returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                        "INVALID_ID, \"alpha\") returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new QuoteStringDataValue(db, INVALID_ID, " +
                                "\"alpha\") failed to throw a system error " +
                                "exception.\n");
                    }
                }
            }
        }
         
        /* verify that the constructor fails when given an ID that does not
         * refer to a formal argument.
         */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue(db, qs_mve.getID(), "alpha");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                "qs_mve.getID(), \"alpha\") returned.\n");
                    }

                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                            "qs_mve.getID(), \"alpha\") returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                                "qs_mve.getID(), \"alpha\") failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }
         
        /* verify that the constructor fails when given an invalid quote string.
         */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue(db, qsfa.getID(), 
                                                "invalid \" string");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                            "qsfa.getID(), \"invalid \\\" string\") returned.\n");
                    }

                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                            "qs_mve.getID(), \"invalid \\\" string\") returned " +
                            "non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new QuoteStringDataValue(db, " +
                            "qs_mve.getID(), \"invalid \\\" string\") failed " +
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
        
    } /* QuoteStringDataValue::Test3ArgConstructor() */
    
    
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
            "Testing class QuoteStringDataValue accessors                     ";
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
        QuoteStringFormalArg qsfa = null;
        UnTypedFormalArg ufa = null;
        QuoteStringDataValue qsdv0 = null;
        QuoteStringDataValue qsdv1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        db = null;
        qsdv0 = null;
        qsdv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();
            
            matrix_mve0 = new MatrixVocabElement(db, "matrix_mve0");
            matrix_mve0.setType(MatrixVocabElement.matrixType.MATRIX);
            qsfa = new QuoteStringFormalArg(db);
            matrix_mve0.appendFormalArg(qsfa);
            db.vl.addElement(matrix_mve0);

            qsdv0 = new QuoteStringDataValue(db, qsfa.getID(), "bravo");
            
            matrix_mve1 = new MatrixVocabElement(db, "matrix_mve1");
            matrix_mve1.setType(MatrixVocabElement.matrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve1.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve1);

            qsdv1 = new QuoteStringDataValue(db, ufa.getID(), "delta");
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( matrix_mve0 == null ) ||
             ( qsfa == null ) ||
             ( qsdv0 == null ) ||
             ( matrix_mve1 == null ) ||
             ( ufa == null ) ||
             ( qsdv1 == null ) ||
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
                
                if ( qsfa == null )
                {
                    outStream.print("allocation of qsfa failed.\n");
                }

                if ( qsdv0 == null )
                {
                    outStream.print("new QuoteStringDataValue(db, qsfa.getID(), " +
                                    "\"bravo\") returned null.\n");
                }
                
                if ( matrix_mve1 == null )
                {
                    outStream.print("allocation of matrix_mve1 failed.\n");
                }
                
                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( qsdv1 == null )
                {
                    outStream.print("new QuoteStringDataValue(db, ufa.getID(), " +
                                    "\"delta\") returned null.\n");
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
            failures += DataValue.TestAccessors(db, qsfa, matrix_mve1, ufa,
                                                qsdv0, outStream, verbose);
            
            if ( qsdv0.getSubRange() != false )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("qsdv0.getSubRange() != false");
                }
            }
            
            if ( ( qsdv0.getItsValue() == null ) ||
                 ( qsdv0.getItsValue().compareTo("bravo") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("qsdv.getItsValue() != \"bravo\"\n");
                }
            }
            
            qsdv0.setItsValue("echo");

            
            if ( ( qsdv0.getItsValue() == null ) ||
                 ( qsdv0.getItsValue().compareTo("echo") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("qsdv0.getItsValue() != \"echo\"\n");
                }
            }
            
            /************************************/

            if ( qsdv1.getSubRange() != false )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("qsdv1.getSubRange() != false (1)\n");
                }
            }
            
            if ( ( qsdv1.getItsValue() == null ) ||
                 ( qsdv1.getItsValue().compareTo("delta") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("qsdv1.getItsValue() != \"delta\"(1)\n");
                }
            }
            
            failures += DataValue.TestAccessors(db, ufa, matrix_mve0, qsfa,
                                                qsdv1, outStream, verbose);

            if ( qsdv1.getSubRange() != false )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("qsdv1.getSubRange() != false (2)\n");
                }
            }
            
            if ( ( qsdv1.getItsValue() == null ) ||
                 ( qsdv1.getItsValue().compareTo("delta") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("qsdv1.getItsValue() != \"delta\"(2)\n");
                }
            }
            
            qsdv1.setItsValue("alpha");
            
            if ( ( qsdv1.getItsValue() == null ) ||
                 ( qsdv1.getItsValue().compareTo("alpha") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("qsdv1.getItsValue() != \"alpha\".\n");
                }
            }
        }
        
        /* verify that the setItsValue method fails when fed an invalid value */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv0.setItsValue("invalid \"quote\" string");
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
                        outStream.print("qsdv0.setItsValue(\"invalid " +
                                "\\\"quote\\\" string\") completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("qsdv0.setItsValue(\"invalid " +
                                "\\\"quote\\\" string\") failed " +
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
        
    } /* QuoteStringDataValue::TestAccessors() */

    
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
            "Testing copy constructor for class QuoteStringDataValue          ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement qs_mve = null;
        QuoteStringFormalArg qsfa = null;
        QuoteStringDataValue qsdv = null;
        QuoteStringDataValue qsdv0 = null;
        QuoteStringDataValue qsdv1 = null;
        QuoteStringDataValue qsdv2 = null;
        QuoteStringDataValue qsdv0_copy = null;
        QuoteStringDataValue qsdv1_copy = null;
        QuoteStringDataValue qsdv2_copy = null;

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
            
            qsdv0 = new QuoteStringDataValue(db);
            
            qs_mve = new MatrixVocabElement(db, "qs_mve");
            qs_mve.setType(MatrixVocabElement.matrixType.MATRIX);
            qsfa = new QuoteStringFormalArg(db);
            qs_mve.appendFormalArg(qsfa);
            db.vl.addElement(qs_mve);

            qsdv1 = new QuoteStringDataValue(db, qsfa.getID());
            qsdv2 = new QuoteStringDataValue(db, qsfa.getID(), "foxtrot");
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( qs_mve == null ) ||
             ( qsfa == null ) ||
             ( qsdv0 == null ) ||
             ( qsdv1 == null ) ||
             ( qsdv2 == null ) ||
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
                
                if ( qs_mve == null )
                {
                    outStream.print("allocation of qs_mve failed.\n");
                }
                
                if ( qsfa == null )
                {
                    outStream.print("allocation of qsfa failed.");
                }

                if ( qsdv0 == null )
                {
                    outStream.print(
                            "new QuoteStringDataValue(db) returned null.\n");
                }
                
                if ( qsdv1 == null )
                {
                    outStream.print("new QuoteStringDataValue(db, " +
                            "qsfa.getID()) returned null.\n");
                }
                
                if ( qsdv2 == null )
                {
                    outStream.print(
                            "new QuoteStringDataValue(db, qsfa.getID(), " +
                             "\"foxtrot\") returned null.\n");
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
                qsdv0_copy = new QuoteStringDataValue(qsdv0);
                qsdv1_copy = new QuoteStringDataValue(qsdv1);
                qsdv2_copy = new QuoteStringDataValue(qsdv2);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
        
            if ( ( qsdv0_copy == null ) ||
                 ( qsdv1_copy == null ) ||
                 ( qsdv2_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( qsdv0_copy == null )
                    {
                        outStream.print(
                            "new QuoteStringDataValue(qsdv0) returned null.\n");
                    }

                    if ( qsdv1_copy == null )
                    {
                        outStream.print(
                            "new QuoteStringDataValue(qsdv1) returned null.\n");
                    }

                    if ( qsdv2_copy == null )
                    {
                        outStream.print(
                            "new QuoteStringDataValue(qsdv2) returned null.\n");
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
            failures += DataValue.VerifyDVCopy(qsdv0, qsdv0_copy, outStream, 
                                               verbose, "qsdv0", "qsdv0_copy");

            failures += DataValue.VerifyDVCopy(qsdv1, qsdv1_copy, outStream, 
                                               verbose, "qsdv1", "qsdv1_copy");

            failures += DataValue.VerifyDVCopy(qsdv2, qsdv2_copy, outStream, 
                                               verbose, "qsdv2", "qsdv2_copy");
        }
        
        
        /* verify that the constructor fails when given an invalid dv */
        if ( failures == 0 )
        {
            qsdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                qsdv = new QuoteStringDataValue((QuoteStringDataValue)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( qsdv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "new QuoteStringDataValue(null) completed.\n");
                    }

                    if ( qsdv != null )
                    {
                        outStream.print("new QuoteStringDataValue(null) " +
                                "returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new QuoteStringDataValue(null) " +
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
        
    } /* QuoteStringDataValue::TestCopyConstructor() */
    
    
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
        String testString0 = "\"bravo\"";
        String testDBString0 = "(QuoteStringDataValue (id 100) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType QUOTE_STRING) " +
                                    "(itsCellID 500) " +
                                    "(itsValue bravo) " +
                                    "(subRange false))";
        String testString1 = "\"nero\"";
        String testDBString1 = "(QuoteStringDataValue (id 101) " +
                                    "(itsFargID 4) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 501) " +
                                    "(itsValue nero) " +
                                    "(subRange false))";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement qs_mve = null;
        MatrixVocabElement matrix_mve = null;
        QuoteStringFormalArg qsfa = null;
        UnTypedFormalArg ufa = null;
        QuoteStringDataValue qsdv0 = null;
        QuoteStringDataValue qsdv1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        db = null;
        qsdv0 = null;
        qsdv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();
            
            qs_mve = new MatrixVocabElement(db, "qs_mve");
            qs_mve.setType(MatrixVocabElement.matrixType.MATRIX);
            qsfa = new QuoteStringFormalArg(db);
            qs_mve.appendFormalArg(qsfa);
            db.vl.addElement(qs_mve);

            qsdv0 = new QuoteStringDataValue(db, qsfa.getID(), "bravo");
            qsdv0.id = 100;        // invalid value for print test
            qsdv0.itsCellID = 500; // invalid value for print test
            
            matrix_mve = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.matrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve);

            qsdv1 = new QuoteStringDataValue(db, ufa.getID(), "nero");
            qsdv1.id = 101;        // invalid value for print test
            qsdv1.itsCellID = 501; // invalid value for print test
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( qs_mve == null ) ||
             ( qsfa == null ) ||
             ( qsdv0 == null ) ||
             ( matrix_mve == null ) ||
             ( ufa == null ) ||
             ( qsdv1 == null ) ||
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
                
                if ( qs_mve == null )
                {
                    outStream.print("allocation of qs_mve failed.\n");
                }
                
                if ( qsfa == null )
                {
                    outStream.print("allocation of qsfa failed.\n");
                }

                if ( qsdv0 == null )
                {
                    outStream.print(
                            "new QuoteStringDataValue(db, qsfa.getID(), " +
                            "\"bravo\") returned null.\n");
                }
                
                if ( matrix_mve == null )
                {
                    outStream.print("allocation of matrix_mve failed.\n");
                }
                
                if ( ufa == null )
                {
                    outStream.print("allocation of ufa failed.\n");
                }

                if ( qsdv1 == null )
                {
                    outStream.print(
                            "new QuoteStringDataValue(db, ufa.getID(), " +
                            "\"nero\") returned null.\n");
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
            if ( qsdv0.toString().compareTo(testString0) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected qsdv0.toString(): \"%s\".\n",
                                     qsdv0.toString());
                }
            }
            
            if ( qsdv0.toDBString().compareTo(testDBString0) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected qsdv0.toDBString(): \"%s\".\n",
                                     qsdv0.toDBString());
                }
            }
            
            if ( qsdv1.toString().compareTo(testString1) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected qsdv1.toString(): \"%s\".\n",
                                     qsdv1.toString());
                }
            }
            
            if ( qsdv1.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected qsdv1.toDBString(): \"%s\".\n",
                                     qsdv1.toDBString());
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
        
    } /* QuoteStringDataValue::TestToStringMethods() */

    
    /**
     * VerifyQuoteStringDVCopy()
     *
     * Verify that the supplied instances of QuoteStringDataValue are distinct, 
     * that they contain no common references (other than db), and that they 
     * have the same value.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyQuoteStringDVCopy(QuoteStringDataValue base,
                                              QuoteStringDataValue copy,
                                              java.io.PrintStream outStream,
                                              boolean verbose,
                                              String baseDesc,
                                              String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyQuoteStringDVCopy: %s null on entry.\n", 
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyQuoteStringDVCopy: %s null on entry.\n", 
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
                outStream.printf("%s and %s share a string.\n", 
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.itsValue == null ) &&
                  ( copy.itsValue != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( ( base.itsValue != null ) &&
                  ( copy.itsValue == null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        copyDesc, baseDesc);
            }
        }
        else if ( ( base.itsValue != null ) &&
                  ( base.itsValue.compareTo(copy.itsValue) != 0 ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                    "%sitsValue and %s.itsValue represent different values.\n", 
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

    } /* QuoteStringDataValue::VerifyQuoteStringDVCopy() */

} /* QuoteStringDataValue */

