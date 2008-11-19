/*
 * TextStringDataValue.java
 *
 * Created on August 18, 2007, 5:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * An instance of TextStringDataValue is used to store a quote string value
 * assigned to a formal argument.
 *
 * @author mainzer
 */

public class TextStringDataValue extends DataValue
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
     * minVal & maxVal don't appear in TextStringDataValue as at present, 
     *      we don't support subranging in quote strings
     */
    
    /** default value for text strings */
    final String ItsDefault = null;
    
    /** the value assigned to the associated formal argument in this case */
    String itsValue = ItsDefault;
      
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    
    /** 
     * TextStringDataValue()
     *
     * Constructor for instances of TextStringDataValue.  
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
     * The fourth takes a reference to an instance of TextStringDataValue as an
     * argument, and uses it to create a copy.
     *
     *                                              JRM -- 8/16/07  
     *
     * Changes:
     *
     *    - None.
     *      
     */
 
    public TextStringDataValue(Database db)
        throws SystemErrorException
    {
        
        super(db);
        
    } /* TextStringDataValue::TextStringDataValue(db) */
    
    public TextStringDataValue(Database db,
                               long fargID)
        throws SystemErrorException
    {
        super(db);
        
        this.setItsFargID(fargID);
        
    } /* TextStringDataValue::TextStringDataValue(db, fargID) */
    
    public TextStringDataValue(Database db,
                               long fargID,
                               String value)
        throws SystemErrorException
    {
        super(db);
        
        this.setItsFargID(fargID);
        
        this.setItsValue(value);
        
    } /* TextStringDataValue::TextStringDataValue(db, fargID, value) */
    
    public TextStringDataValue(TextStringDataValue dv)
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
    
    } /* TextStringDataValue::TextStringDataValue(dv) */
    
        
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
        
    } /* TextStringDataValue::getItsValue() */
    

    /**
     * setItsValue()
     *
     * Set itsValue to the specified value if it is valid.  Otherwise
     * throw a system error.
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
        final String mName = "TextStringDataValue::setItsValue(): ";
        
        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            this.itsValue = null;
        }
        else if ( ! ( db.IsValidTextString(value) ) )
        {
            throw new SystemErrorException(mName + 
                                           "value not valid quote string");
        }
        else
        {
            this.itsValue = new String(value);
        }
        
        return;
        
    } /* TextStringDataValue::setItsValue() */
  
        
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
            return "";
        }
        else
        {
            return new String(this.itsValue);
        }
        
    } /* TextStringDataValue::toString() */


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
            return ("(TextStringDataValue (id " + this.id +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + "<null>" +
                    ") (subRange " + this.subRange + "))");
        }
        else
        {
            return ("(TextStringDataValue (id " + this.id +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + new String(this.itsValue) +
                    ") (subRange " + this.subRange + "))");
        }

    } /* TextStringDataValue::toDBString() */
    
    
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
        final String mName = "TextStringDataValue::updateForFargChange(): ";
        
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
        
    } /* TextStringDataValue::updateForFargChange() */
    
    
    /**
     * updateSubRange()
     *
     * Nominally, this method should determine if the formal argument 
     * associated with the data value is subranged, and if it is, update
     * the data values representation of  the subrange (if ant) accordingly.  
     * In passing, it should coerce the value of  the datavalue into the 
     * subrange if necessary.
     *
     * However, text strings can't be subranged at present, so all we do 
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
        final String mName = "TextStringDataValue::updateSubRange(): ";
        
        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");    
        }
        
        if ( fa instanceof TextStringFormalArg )
        {
            TextStringFormalArg tfa = (TextStringFormalArg)fa;
            
            if ( tfa.getSubRange() != false ) 
            {
                throw new SystemErrorException(mName +
                                               "tfa.getSubRange() != FALSE"); 
            }
            
            this.subRange = false;
        }
        else
        {
            throw new SystemErrorException(mName + "Unexpected fa type");    
        }
        
        return;
        
    } /* TextStringDataValue::updateSubRange() */
  
        
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
     * However, we don't support subranges for text strings.
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
        final String mName = "TextStringDataValue::coerceToRange(): ";

        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            return value;
        }
        
        if ( ! this.db.IsValidTextString(value) )
        {
            throw new SystemErrorException(mName + 
                                           "value isn't valid quote string"); 
        }
        
        return value;
        
    } /* TextStringDataValue::coerceToRange() */
  
    
    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/
    
    /**
     * Construct()
     *
     * Construct an instance of TextStringDataValue with the specified 
     * initialization.
     *
     * Returns a reference to the newly constructed TextStringDataValue if 
     * successful.  Throws a system error exception on failure.
     *
     *                                              JRM -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */
    
    public static TextStringDataValue Construct(Database db,
                                                String t)
        throws SystemErrorException
    {
        final String mName = "TextStringDataValue::Construct(db, t)";
        TextStringDataValue tdv = null;
        
        tdv = new TextStringDataValue(db);
        
        tdv.setItsValue(t);
        
        return tdv;
        
    } /* TextStringDataValue::Construct(db, t) */
      
      
    /**
     * TextStringDataValuesAreLogicallyEqual()
     *
     * Given two instances of TextStringDataValue, return true if they contain 
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
    
    protected static boolean TextStringDataValuesAreLogicallyEqual
            (TextStringDataValue tdv0,
             TextStringDataValue tdv1)
        throws SystemErrorException
    {
        final String mName = 
            "TextStringDataValue::TextStringDataValuesAreLogicallyEqual()";
        boolean dataValuesAreEqual = true;
        
        if ( ( tdv0 == null ) || ( tdv1 == null ) )
        {
            throw new SystemErrorException(mName + 
                                           ": tdv0 or tdv1 null on entry.");
        }
        
        if ( tdv0 != tdv1 )
        {
            if ( tdv0.itsValue != tdv1.itsValue )
            {
                if ( ( ( tdv0.itsValue == null ) 
                       &&
                       ( tdv1.itsValue != null ) 
                     )
                     ||
                     ( ( tdv0.itsValue != null ) 
                       &&
                       ( tdv1.itsValue == null ) 
                     )
                   )
                {
                    dataValuesAreEqual = false;
                }
                // due to the above tests, if we get this far, we know
                // that both tdv0.itsValue and tdv1.itsValue are non-null.
                else if ( tdv0.itsValue.compareTo(tdv1.itsValue) != 0 )
                {
                    dataValuesAreEqual = false;
                }
            }
        }

        return dataValuesAreEqual;
        
    } /* TextStringDataValue::TextStringDataValuesAreLogicallyEqual() */


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
     *         argument constructor for TextStringDataValue.  Verify that all
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
     *         Construct a TextStringDataValue for the formal argument of the 
     *         mve by passing a reference to the database and the id of the 
     *         formal argument.  Verify that the TextStringDataValue's 
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
     *      TextStringDataValue.
     *              
     * 4) Copy constructor:
     *
     *      a) Construct a database and possibly a mve (matrix vocab element) 
     *         and such formal arguments as are necessary.  If an mve is 
     *         created, insert it into the database, and make note of the IDs 
     *         assigned.  Then create a  TextStringDataValue (possibly using 
     *         the using a formal argument ID).
     *
     *         Now use the copy constructor to create a copy of the 
     *         TextStringDataValue, and verify that the copy is correct. 
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
     * TestClassTextStringDataValue()
     *
     * Main routine for tests of class TextStringDataValue.
     *
     *                                      JRM -- 10/15/07
     *
     * Changes:
     *
     *    - Non.
     */
    
    public static boolean TestClassTextStringDataValue(
                                                  java.io.PrintStream outStream,
                                                  boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing class TextStringDataValue:\n");
      
        if ( ! TestAccessors(outStream, verbose) )
        {
            failures++;
        }
       
        if ( failures > 0 )
        {
            pass = false;
            outStream.printf(
                    "%d failures in tests for class TextStringDataValue.\n\n",
                    failures);
        }
        else
        {
            outStream.print(
                    "All tests passed for class TextStringDataValue.\n\n");
        }
        
        return pass;
        
    } /* TextStringDataValue::TestClassTextStringDataValue() */
    
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
            "Testing class TextStringDataValue accessors                      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement txt_mve0 = null;
        MatrixVocabElement txt_mve1 = null;
        TextStringFormalArg tfa0 = null;
        TextStringFormalArg tfa1 = null;
        TextStringDataValue tdv0 = null;
        TextStringDataValue tdv1 = null;

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
            
            txt_mve0 = new MatrixVocabElement(db, "txt_mve0");
            txt_mve0.setType(MatrixVocabElement.matrixType.TEXT);
            tfa0 = new TextStringFormalArg(db);
            txt_mve0.appendFormalArg(tfa0);
            db.vl.addElement(txt_mve0);

            tdv0 = new TextStringDataValue(db, tfa0.getID(), "bravo");
            
            txt_mve1 = new MatrixVocabElement(db, "txt_mve1");
            txt_mve1.setType(MatrixVocabElement.matrixType.TEXT);
            tfa1 = new TextStringFormalArg(db);
            txt_mve1.appendFormalArg(tfa1);
            db.vl.addElement(txt_mve1);

            tdv1 = new TextStringDataValue(db, tfa1.getID(), "delta");
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( txt_mve0 == null ) ||
             ( tfa0 == null ) ||
             ( tdv0 == null ) ||
             ( txt_mve1 == null ) ||
             ( tfa1 == null ) ||
             ( tdv1 == null ) ||
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
                
                if ( txt_mve0 == null )
                {
                    outStream.print("allocation of txt_mve0 failed.\n");
                }
                
                if ( tfa0 == null )
                {
                    outStream.print("allocation of tfa0 failed.\n");
                }

                if ( tdv0 == null )
                {
                    outStream.print(
                            "new TextStringDataValue(db, tfao.getID(), " +
                            "\"bravo\") returned null.\n");
                }
                
                if ( txt_mve1 == null )
                {
                    outStream.print("allocation of txt_mve1 failed.\n");
                }
                
                if ( tfa1 == null )
                {
                    outStream.print("allocation of tfa1 failed.\n");
                }

                if ( tdv1 == null )
                {
                    outStream.print(
                            "new TextStringDataValue(db, tfa1.getID(), " +
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
            failures += DataValue.TestAccessors(db, tfa0, txt_mve1, tfa1,
                                                tdv0, outStream, verbose);
            
            if ( tdv0.getSubRange() != false )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("tdv0.getSubRange() != false");
                }
            }
            
            if ( ( tdv0.getItsValue() == null ) ||
                 ( tdv0.getItsValue().compareTo("bravo") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("tdv.getItsValue() != \"bravo\"\n");
                }
            }
            
            tdv0.setItsValue("echo");

            
            if ( ( tdv0.getItsValue() == null ) ||
                 ( tdv0.getItsValue().compareTo("echo") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("tdv0.getItsValue() != \"echo\"\n");
                }
            }
            
            /************************************/

            if ( tdv1.getSubRange() != false )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("tdv1.getSubRange() != false (1)\n");
                }
            }
            
            if ( ( tdv1.getItsValue() == null ) ||
                 ( tdv1.getItsValue().compareTo("delta") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("tdv1.getItsValue() != \"delta\"(1)\n");
                }
            }
            
            failures += DataValue.TestAccessors(db, tfa1, txt_mve0, tfa0,
                                                tdv1, outStream, verbose);

            if ( tdv1.getSubRange() != false )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("tdv1.getSubRange() != false (2)\n");
                }
            }
            
            if ( ( tdv1.getItsValue() == null ) ||
                 ( tdv1.getItsValue().compareTo("delta") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("tdv1.getItsValue() != \"delta\"(2)\n");
                }
            }
            
            tdv1.setItsValue("alpha");
            
            if ( ( tdv1.getItsValue() == null ) ||
                 ( tdv1.getItsValue().compareTo("alpha") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("tdv1.getItsValue() != \"alpha\".\n");
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
                tdv0.setItsValue("invalid \b text string -- has back space");
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
                        outStream.print("tdv0.setItsValue(\"invalid " +
                                "\\b text string -- has back space\")" +
                                "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("tdv0.setItsValue(\"invalid " +
                                "\\b text string -- has back space\") failed " +
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
        
    } /* TextStringDataValue::TestAccessors() */

    /**
     * VerifyTextStringDVCopy()
     *
     * Verify that the supplied instances of IntDataValue are distinct, that 
     * they contain no common references (other than db), and that they have 
     * the same value.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyTextStringDVCopy(TextStringDataValue base,
                                             TextStringDataValue copy,
                                             java.io.PrintStream outStream,
                                              boolean verbose,
                                             String baseDesc,
                                             String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyTextStringDVCopy: %s null on entry.\n", 
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyTextStringDVCopy: %s null on entry.\n", 
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
                    "%s.itsValue and %s.itsValue represent different values.\n", 
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

    } /* TextStringDataValue::VerifyTextStringDVCopy() */

} /* TextStringDataValue */

