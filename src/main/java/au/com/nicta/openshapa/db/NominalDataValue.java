/*
 * NominalDataValue.java
 *
 * Created on August 17, 2007, 5:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;


/**
 * An instance of NominalDataValue is used to store a nominal value
 * assigned to a formal argument.
 *
 * @author mainzer
 */
public class NominalDataValue extends DataValue
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
     * queryVar: Boolean that is set to true iff the the value of the nominal
     *      is a valid query variable -- that is if the nominal starts with a 
     *      '?', and is at least two characters long.
     *
     *      The concept of a query variable is a hold over from MacSHAPA, that
     *      I was hoping to get rid of in OpenSHAPA -- but it seems that I am
     *      stuck with it afterall.  However, by implementing it in this way,
     *      we make it easy to ignore it in contexts other than the MacSHAPA
     *      query column variable.
     *
     * minVal & maxVal don't appear in NominalDataValue as a subrange of 
     *      nominals is expressed as a set of allowed values.  Given the
     *      potential size of this set, we don't keep a copy of it here -- 
     *      referring directly to the associated formal argument when needed
     *      instead.  
     */
    
    /** default value for nominals */
    final String ItsDefault = null;
    
    /** the value assigned to the associated formal argument in this case */
    String itsValue = ItsDefault;
    
    /** whether the value currently assigned to the Nominal is a valid query
     *  variable name.
     */
    boolean queryVar = false;
      
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    
    /** 
     * NominalDataValue()
     *
     * Constructor for instances of NominalDataValue.  
     * 
     * Four versions of this constructor.  
     * 
     * The first takes a reference to a database as its parameter and just 
     * calls the super() constructor.
     *
     * The second takes a reference to a database, and a formal argument ID, and 
     * ttempts to set the itsFargID field of the data value accordingly.
     *
     * The third takes a reference to a database, a formal argument ID, and 
     * a value as arguments, and attempts to set the itsFargID and itsValue 
     * of the data value accordingly.
     *
     * The fourth takes a reference to an instance of NominalDataValue as an
     * argument, and uses it to create a copy.
     *
     *                                              JRM -- 8/16/07  
     *
     * Changes:
     *
     *    - None.
     *      
     */
 
    public NominalDataValue(Database db)
        throws SystemErrorException
    {
        
        super(db);
        
    } /* NominalDataValue::NominalDataValue(db) */
    
    public NominalDataValue(Database db,
                           long fargID)
        throws SystemErrorException
    {
        super(db);
        
        this.setItsFargID(fargID);
        
    } /* NominalDataValue::NominalDataValue(db, fargID) */
    
    public NominalDataValue(Database db,
                           long fargID,
                           String value)
        throws SystemErrorException
    {
        super(db);
        
        this.setItsFargID(fargID);
        
        this.setItsValue(value);
        
    } /* NominalDataValue::NominalDataValue(db, fargID, value) */
    
    public NominalDataValue(NominalDataValue dv)
        throws SystemErrorException
    {
        
        super(dv);
        
        if ( dv.itsValue == null )
        {
            this.itsValue = null;
        }
        else
        {
            this.itsValue = new String(dv.itsValue);
        }
    
    } /* NominalDataValue::NominalDataValue(dv) */
    
        
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
        
    } /* NominlDataValue::getItsValue() */
    
    /**
     * setItsValue()
     *
     * Set itsValue to the specified value.  If subrange is true, coerce the
     * value into the subrange.  That is hard to do with nominals, so for the 
     * nonce, we just set itsValue to null -- indicating that the nominal 
     * data value is undefined.
     *
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - Added code to maintain this.isQueryVar. JRM -- 10/20/08
     */
    
    public void setItsValue(String value)
        throws SystemErrorException
    {
        final String mName = "NominalDataValue::setItsValue(): ";
        DBElement dbe;
        NominalFormalArg nfa;
        
        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            this.itsValue = null;
        }
        else if ( ! this.subRange ) // Just verify that value is a valid nominal
        {
            if ( db.IsValidNominal(value) )
            {
                this.itsValue = (new String(value));
            }
            else
            {
                throw new SystemErrorException(mName + 
                                               "value not valid nominal");
            }
        }
        else // must lookup formal argument, an validate against it
        {
            if ( this.itsFargID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName + 
                                      "subRange && (itsFargID == INVALID_ID)");
            }
            else if ( itsFargType != FormalArgument.fArgType.NOMINAL )
            {
                throw new SystemErrorException(mName + 
                                               "itsFargType != NOMINAL");
            }
            
            dbe = this.db.idx.getElement(this.itsFargID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName + 
                                               "itsFargID has no referent");
            }
            
            if ( ! ( dbe instanceof NominalFormalArg ) )
            {
                throw new SystemErrorException(mName + 
                                       "itsFargID doesn't refer to a nominal");
            }
            
            nfa = (NominalFormalArg)dbe;
            
            if ( nfa.approved(value) )
            {
                itsValue = new String(value);
            }
            else // coerce to the undefined state
            {
                this.itsValue = null;
            }
        }
        
        if ( ( this.itsValue != null ) &&
             ( this.itsValue.length() >= 1 ) &&
             ( this.itsValue.charAt(0) == '?' ) )
        {
            this.queryVar = true;
        }
        else
        {
            this.queryVar = false;
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
            return "";
        }
        else
        {
            return new String(this.itsValue);
        }
        
    } /* NominalDataValue::toString() */


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
            return ("(NominalDataValue (id " + this.id +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + "<null>" +
                    ") (subRange " + this.subRange + "))");
        }
        else
        {
            return ("(NominalDataValue (id " + this.id +
                    ") (itsFargID " + this.itsFargID +
                    ") (itsFargType " + this.itsFargType +
                    ") (itsCellID " + this.itsCellID +
                    ") (itsValue " + new String(this.itsValue) +
                    ") (subRange " + this.subRange + "))");
        }

    } /* NominalDataValue::toDBString() */
    
    
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
        final String mName = "NominalDataValue::updateForFargChange(): ";
        
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
        
    } /* NominalDataValue::updateForFargChange() */
    
    
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
        final String mName = "NominalDataValue::updateSubRange(): ";
        
        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");    
        }
        
        if ( fa instanceof NominalFormalArg )
        {
            NominalFormalArg nfa = (NominalFormalArg)fa;
            
            this.subRange = nfa.getSubRange();
            
            if ( this.subRange )
            {
                if ( ( this.itsValue != null ) &&
                     ( this.itsValue.length() > 0 ) &&
                     ( ! ( nfa.approved(this.itsValue) ) ) )
                {
                    this.itsValue = null;
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
        
    } /* NominalDataValue::updateSubRange() */
  
        
    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/
    
    /**
     * coerceToRange()
     *
     * If the supplied value is in range for the associated formal argument,
     * simply return it.  Otherwise, coerce it to the nearest value that is
     * in range.
     *
     * Coercing to the nearest valid value doesn't doesn't have an obvious 
     * meaning in the case of nominals, so in this case, if subrange is true
     * and value contains a valid nominal that is not in the permitted list
     * for the associaged formal argument, just return false.
     *
     * This method should never be passed an invalid nominal, so if it 
     * ever receives one, it will throw a system error exception.
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
        final String mName = "NominalDataValue::coerceToRange(): ";
        DBElement dbe;
        NominalFormalArg nfa;

        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            return value;
        }
        
        if ( ! this.db.IsValidNominal(value) )
        {
            throw new SystemErrorException(mName + "value isn't valid nominal"); 
        }

        if ( this.subRange )
        {
            if ( this.itsFargID == DBIndex.INVALID_ID )
            {
                throw new SystemErrorException(mName + 
                                      "subRange && (itsFargID == INVALID_ID)");
            }
            else if ( itsFargType != FormalArgument.fArgType.NOMINAL )
            {
                throw new SystemErrorException(mName + 
                                               "itsFargType != NOMINAL");
            }
            
            dbe = this.db.idx.getElement(this.itsFargID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName + 
                                               "itsFargID has no referent");
            }
            
            if ( ! ( dbe instanceof NominalFormalArg ) )
            {
                throw new SystemErrorException(mName + 
                                       "itsFargID doesn't refer to a nominal");
            }
            
            nfa = (NominalFormalArg)dbe;
            
            if ( nfa.approved(value) )
            {
                return (new String(value));
            }
            else // coerce to the undefined state
            {
                return null;
            }
        }
        
        return value;
        
    } /* NominalDataValue::coerceToRange() */
    
    
    /**
     * isQueryVar()
     *
     * Return true if the current value of the nominal is a valid MacSHAPA 
     * style query variable name, and false otherwise.
     *
     *                                              JRM -- 10/20/08
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean isQueryVar()
    
    {
        
        return this.queryVar;
        
    } /* NominalDataValue::isQueryVar() */
  
    
    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/
    
    /**
     * Construct()
     *
     * Construct an instance of NominalDataValue with the specified 
     * initialization.
     *
     * Returns a reference to the newly constructed NominalDataValue if 
     * successful.  Throws a system error exception on failure.
     *
     *                                              JRM -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */
    
    public static NominalDataValue Construct(Database db,
                                             String n)
        throws SystemErrorException
    {
        final String mName = "NominalDataValue::Construct(db, n)";
        NominalDataValue ndv = null;
        
        ndv = new NominalDataValue(db);
        
        ndv.setItsValue(n);
        
        return ndv;
        
    } /* NominalDataValue::Construct(db, n) */
      
    
    /**
     * NominalDataValuesAreLogicallyEqual()
     *
     * Given two instances of NominalDataValue, return true if they contain 
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
    
    protected static boolean NominalDataValuesAreLogicallyEqual
            (NominalDataValue ndv0,
             NominalDataValue ndv1)
        throws SystemErrorException
    {
        final String mName = 
                "NominalDataValue::NominalDataValuesAreLogicallyEqual()";
        boolean dataValuesAreEqual = true;
        
        if ( ( ndv0 == null ) || ( ndv1 == null ) )
        {
            throw new SystemErrorException(mName + 
                                           ": ndv0 or ndv1 null on entry.");
        }
        
        if ( ndv0 != ndv1 )
        {
            if ( ndv0.itsValue != ndv1.itsValue )
            {
                if ( ( ( ndv0.itsValue == null ) 
                       &&
                       ( ndv1.itsValue != null ) 
                     )
                     ||
                     ( ( ndv0.itsValue != null ) 
                       &&
                       ( ndv1.itsValue == null ) 
                     )
                   )
                {
                    dataValuesAreEqual = false;
                }
                // due to the above tests, if we get this far, we know
                // that both ndv0.itsValue and ndv1.itsValue are non-null.
                else if ( ndv0.itsValue.compareTo(ndv1.itsValue) != 0 )
                {
                    dataValuesAreEqual = false;
                }
            }
        }

        return dataValuesAreEqual;
        
    } /* NominalDataValue::NominalDataValuesAreLogicallyEqual() */


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
     *         argument constructor for NominalDataValue.  Verify that all
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
     *         Construct a NominalDataValue for the formal argument of the mve
     *         by passing a reference to the database and the id of the formal
     *         argument.  Verify that the NominalDataValue's itsFargID, 
     *         itsFargType, subRange, minVal, and maxVal fields matches
     *         those of the formal argument, and that all other fields are set
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
     *      NominalDataValue -- perhaps after having been modified to match
     *      the subrange.
     *              
     * 4) Copy constructor:
     *
     *      a) Construct a database and possibly a mve (matrix vocab element) 
     *         and such formal arguments as are necessary.  If an mve is 
     *         created, insert it into the database, and make note of the IDs 
     *         assigned.  Then create a  NominalDataValue (possibly using 
     *         the using a formal argument ID).
     *
     *         Now use the copy constructor to create a copy of the 
     *         NominalDataValue, and verify that the copy is correct. 
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
     * TestClassNominalDataValue()
     *
     * Main routine for tests of class NominalDataValue.
     *
     *                                      JRM -- 10/15/07
     *
     * Changes:
     *
     *    - Non.
     */
    
    public static boolean TestClassNominalDataValue(java.io.PrintStream outStream,
                                                    boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing class NominalDataValue:\n");
        
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
                    "%d failures in tests for class NominalDataValue.\n\n",
                    failures);
        }
        else
        {
            outStream.print("All tests passed for class NominalDataValue.\n\n");
        }
        
        return pass;
        
    } /* NominalDataValue::TestClassNominalDataValue() */
    
    
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
            "Testing 1 argument constructor for class NominalDataValue        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        NominalDataValue ndv = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        db = null;
        ndv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();
            ndv = new NominalDataValue(db);
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( ndv == null ) ||
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

                if ( ndv == null )
                {
                    outStream.print(
                            "new NominalDataValue(db) returned null.\n");
                }
                
                if ( ! completed )
                {
                    outStream.printf(
                            "new NominalDataValue(db) failed to complete.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("new NominalDataValue(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValue.Verify1ArgInitialization(db, ndv, outStream, 
                                                           verbose);

            if ( ndv.ItsDefault != null )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(" ndv.ItsDefault != null.\n");
                }
            }
            
            if ( ndv.itsValue != ndv.ItsDefault )
            {
                failures++;
                
                if ( verbose )
                {
                    String s1;
                    String s2;
                    
                    if ( ndv.itsValue == null )
                        s1 = new String("<null>");
                    else
                        s1 = ndv.itsValue;
                    
                    if ( ndv.ItsDefault == null )
                        s2 = new String("<null>");
                    else
                        s2 = ndv.ItsDefault;
                    
                    outStream.printf(
                            "ndv.itsValue = %s != ndv.ItsDefault = %s.\n",
                            s1, s2);
                }
            }
        }
         
        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            ndv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ndv = new NominalDataValue((Database)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ndv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new NominalDataValue(null) returned.\n");
                    }

                    if ( ndv != null )
                    {
                        outStream.print(
                                "new NominalDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new NominalDataValue(null) failed to throw " +
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
        
    } /* NominalDataValue::Test1ArgConstructor() */
    
    
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
            "Testing 2 argument constructor for class NominalDataValue        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement nom_mve = null;
        MatrixVocabElement nom_mve_sr = null;
        NominalFormalArg nfa = null;
        NominalFormalArg nfa_sr = null;
        NominalDataValue ndv = null;
        NominalDataValue ndv_sr = null;

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
            
            nom_mve = new MatrixVocabElement(db, "nom_mve");
            nom_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            nfa = new NominalFormalArg(db);
            nom_mve.appendFormalArg(nfa);
            db.vl.addElement(nom_mve);

            ndv = new NominalDataValue(db, nfa.getID());
            
            nom_mve_sr = new MatrixVocabElement(db, "nom_mve_sr");
            nom_mve_sr.setType(MatrixVocabElement.MatrixType.NOMINAL);
            nfa_sr = new NominalFormalArg(db);
            nfa_sr.setSubRange(true);
            nfa_sr.addApproved("alpha");
            nfa_sr.addApproved("bravo");
            nfa_sr.addApproved("charlie");
            nom_mve_sr.appendFormalArg(nfa_sr);
            db.vl.addElement(nom_mve_sr);

            ndv_sr = new NominalDataValue(db, nfa_sr.getID());
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( nom_mve == null ) ||
             ( nfa == null ) ||
             ( ndv == null ) ||
             ( nom_mve_sr == null ) ||
             ( nfa_sr == null ) ||
             ( ndv_sr == null ) ||
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
                
                if ( nom_mve == null )
                {
                    outStream.print("allocation of nom_mve failed.\n");
                }
                
                if ( nfa == null )
                {
                    outStream.print("allocation of nfa failed.");
                }

                if ( ndv == null )
                {
                    outStream.print(
                        "new NominalDataValue(db, nfa.getID()) returned null.\n");
                }
                
                if ( nom_mve_sr == null )
                {
                    outStream.print("allocation of nom_mve_sr failed.\n");
                }
                
                if ( nfa_sr == null )
                {
                    outStream.print("allocation of nfa_sr failed.");
                }

                if ( ndv_sr == null )
                {
                    outStream.print("new NominalDataValue(db, nfa_sr.getID()) " +
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
                                                               nfa, 
                                                               ndv,  
                                                               outStream, 
                                                               verbose,
                                                              "ndv");
            
            if ( ndv.ItsDefault != null )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv.ItsDefault != null.\n");
                }
            }

            if ( ndv.subRange != nfa.getSubRange() )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "ndv.subRange doesn't match nfa.getSubRange().\n");
                }
            }
            
            if ( ndv.itsValue != ndv.ItsDefault )
            {
                failures++;
                
                if ( verbose )
                {
                    String s1;
                    String s2;
                    
                    if ( ndv.itsValue == null )
                        s1 = new String("<null>");
                    else
                        s1 = ndv.itsValue;
                    
                    if ( ndv.ItsDefault == null )
                        s2 = new String("<null>");
                    else
                        s2 = ndv.ItsDefault;
                    
                    outStream.printf(
                            "ndv.itsValue = %s != ndv.ItsDefault = %s.\n",
                            s1, s2);
                }
            }

            failures += DataValue.Verify2PlusArgInitialization(db, 
                                                               nfa_sr, 
                                                               ndv_sr,  
                                                               outStream, 
                                                               verbose,
                                                               "ndv_sr");

            if ( ndv_sr.subRange != nfa_sr.getSubRange() )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv_sr.subRange doesn't match " +
                                     "nfa_sr.getSubRange().\n");
                }
            }
            
            if ( ndv_sr.ItsDefault != null )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv_sr.ItsDefault != null.\n");
                }
            }
            
            if ( ndv_sr.itsValue != ndv_sr.ItsDefault )
            {
                failures++;
                
                if ( verbose )
                {
                    String s1;
                    String s2;
                    
                    if ( ndv_sr.itsValue == null )
                        s1 = new String("<null>");
                    else
                        s1 = ndv_sr.itsValue;
                    
                    if ( ndv_sr.ItsDefault == null )
                        s2 = new String("<null>");
                    else
                        s2 = ndv_sr.ItsDefault;
                    
                    outStream.printf(
                            "ndv_sr.itsValue = %s != ndv_sr.ItsDefault = %s.\n",
                            s1, s2);
                }
            }
        }
         
        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            ndv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ndv = new NominalDataValue((Database)null, nfa.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ndv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new NominalDataValue(null, " +
                                        "nfa.getID()) returned.\n");
                    }

                    if ( ndv != null )
                    {
                        outStream.print("new NominalDataValue(null, " +
                                        "nfa.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new NominalDataValue(null, nfa.getID())" +
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
            ndv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ndv = new NominalDataValue(db, DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ndv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new NominalDataValue(db, " +
                                        "INVALID_ID) returned.\n");
                    }

                    if ( ndv != null )
                    {
                        outStream.print("new NominalDataValue(db, " +
                                        "INVALID_ID) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new NominalDataValue(db, INVALID_ID)" +
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
            ndv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ndv = new NominalDataValue(db, nom_mve.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ndv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new NominalDataValue(db, " +
                                        "nom_mve.getID()) returned.\n");
                    }

                    if ( ndv != null )
                    {
                        outStream.print("new NominalDataValue(db, " +
                                "nom_mve.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new NominalDataValue(db, nom_mve.getID()) " +
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
        
    } /* NominalDataValue::Test2ArgConstructor() */
    
    
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
            "Testing 3 argument constructor for class NominalDataValue        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement nom_mve = null;
        MatrixVocabElement nom_mve_sr = null;
        NominalFormalArg nfa = null;
        NominalFormalArg nfa_sr = null;
        NominalDataValue ndv = null;
        NominalDataValue ndv_sr0 = null;
        NominalDataValue ndv_sr1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        db = null;
        ndv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();
            
            nom_mve = new MatrixVocabElement(db, "nom_mve");
            nom_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            nfa = new NominalFormalArg(db);
            nom_mve.appendFormalArg(nfa);
            db.vl.addElement(nom_mve);

            ndv = new NominalDataValue(db, nfa.getID(), "echo");
            
            nom_mve_sr = new MatrixVocabElement(db, "nom_mve_sr");
            nom_mve_sr.setType(MatrixVocabElement.MatrixType.NOMINAL);
            nfa_sr = new NominalFormalArg(db);
            nfa_sr.setSubRange(true);
            nfa_sr.addApproved("alpha");
            nfa_sr.addApproved("bravo");
            nfa_sr.addApproved("charlie");
            nom_mve_sr.appendFormalArg(nfa_sr);
            db.vl.addElement(nom_mve_sr);

            ndv_sr0 = new NominalDataValue(db, nfa_sr.getID(), "alpha");
            ndv_sr1 = new NominalDataValue(db, nfa_sr.getID(), "delta");
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( nom_mve == null ) ||
             ( nfa == null ) ||
             ( ndv == null ) ||
             ( nom_mve_sr == null ) ||
             ( nfa_sr == null ) ||
             ( ndv_sr0 == null ) ||
             ( ndv_sr1 == null ) ||
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
                
                if ( nom_mve == null )
                {
                    outStream.print("allocation of nom_mve failed.\n");
                }
                
                if ( nfa == null )
                {
                    outStream.print("allocation of nfa failed.");
                }

                if ( ndv == null )
                {
                    outStream.print("new NominalDataValue(db, nfa.getID(), " +
                                    "\"echo\") returned null.\n");
                }
                
                if ( nom_mve_sr == null )
                {
                    outStream.print("allocation of nom_mve_sr failed.\n");
                }
                
                if ( nfa_sr == null )
                {
                    outStream.print("allocation of nfa_sr failed.");
                }

                if ( ndv_sr0 == null )
                {
                    outStream.print("new NominalDataValue(db, nfa_sr.getID(), " +
                                    "\"alpha\") returned null.\n");
                }

                if ( ndv_sr1 == null )
                {
                    outStream.print("new NominalDataValue(db, nfa_sr.getID(), " +
                                    "\"delta\") returned null.\n");
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
                                                               nfa, 
                                                               ndv,  
                                                               outStream, 
                                                               verbose,
                                                               "ndv");

            if ( ndv.subRange != nfa.getSubRange() )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "ndv.subRange doesn't match nfa.getSubRange().\n");
                }
            }
            
            if ( ( ndv.itsValue == null ) ||
                 ( ndv.itsValue.compareTo("echo") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv.itsValue != \"echo\".\n");
                }
            }

            failures += DataValue.Verify2PlusArgInitialization(db, 
                                                               nfa_sr, 
                                                               ndv_sr0,  
                                                               outStream, 
                                                               verbose,
                                                               "ndv_sr0");

            if ( ndv_sr0.subRange != nfa_sr.getSubRange() )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv_sr0.subRange doesn't match " +
                                     "nfa_sr.getSubRange().\n");
                }
            }
            
            if ( ( ndv_sr0.itsValue != null ) &&
                 ( ndv_sr0.itsValue.compareTo("alpha") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv_sr.itsValue = \"%s\" != \"alpha\".\n",
                                     ndv_sr0.itsValue);
                }
            }

            failures += DataValue.Verify2PlusArgInitialization(db, 
                                                               nfa_sr, 
                                                               ndv_sr1,  
                                                               outStream, 
                                                               verbose,
                                                               "ndv_sr1");

            if ( ndv_sr1.subRange != nfa_sr.getSubRange() )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv_sr0.subRange doesn't match " +
                                     "nfa_sr.getSubRange().\n");
                }
            }
            
            if ( ndv_sr1.itsValue != null )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv_sr1.itsValue = \"%s\" != <null>.\n",
                                     ndv_sr1.itsValue);
                }
            }
        }
         
        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            ndv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ndv = new NominalDataValue((Database)null, nfa.getID(), "alpha");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ndv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( ndv != null )
                    {
                        outStream.print("new NominalDataValue(null, " +
                                "nfa.getID(), \"alpha\") returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new NominalDataValue(null, " +
                                        "nfa.getID(), \"alpha\") returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new NominalDataValue(null, nfa.getID(), " +
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
            ndv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ndv = new NominalDataValue(db, DBIndex.INVALID_ID, "alpha");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ndv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( ndv != null )
                    {
                        outStream.print("new NominalDataValue(db, " +
                                "INVALID_ID, \"alpha\") returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new NominalDataValue(db, " +
                                        "INVALID_ID, \"alpha\") returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new NominalDataValue(db, INVALID_ID, " +
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
            ndv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ndv = new NominalDataValue(db, nom_mve.getID(), "alpha");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ndv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new NominalDataValue(db, " +
                                "nom_mve.getID(), \"alpha\") returned.\n");
                    }

                    if ( ndv != null )
                    {
                        outStream.print("new NominalDataValue(db, " +
                            "nom_mve.getID(), \"alpha\") returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new NominalDataValue(db, " +
                                "nom_mve.getID(), \"alpha\") failed to " +
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
        
    } /* NominalDataValue::Test3ArgConstructor() */
    
    
    /**
     * TestAccessors()
     * 
     * Run a battery of tests on the accessors supported by this class.
     * 
     *                                              JRM -- 11/13/07
     * 
     * Changes:
     * 
     *    - Added test code for the new isQueryVar() method.
     *
     *                                              JRM -- 10/20/08
     */
    
    public static boolean TestAccessors(java.io.PrintStream outStream,
                                        boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing class NominalDataValue accessors                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement nom_mve = null;
        MatrixVocabElement matrix_mve = null;
        NominalFormalArg nfa = null;
        UnTypedFormalArg ufa = null;
        NominalDataValue ndv0 = null;
        NominalDataValue ndv1 = null;
        NominalDataValue ndv2 = null;
        NominalDataValue ndv3 = null;
        NominalDataValue ndv4 = null;
        NominalDataValue ndv5 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        db = null;
        ndv0 = null;
        ndv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();
            
            nom_mve = new MatrixVocabElement(db, "nom_mve");
            nom_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            nfa = new NominalFormalArg(db);
            nfa.setSubRange(true);
            nfa.addApproved("alpha");
            nfa.addApproved("bravo");
            nfa.addApproved("charlie");
            nom_mve.appendFormalArg(nfa);
            db.vl.addElement(nom_mve);

            ndv0 = new NominalDataValue(db, nfa.getID(), "bravo");
            
            matrix_mve = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve);

            ndv1 = new NominalDataValue(db, ufa.getID(), "delta");
            ndv2 = new NominalDataValue(db, ufa.getID(), "charlie");
            ndv3 = new NominalDataValue(db, ufa.getID(), "?query_var");
            ndv4 = new NominalDataValue(db, ufa.getID(), "!?query_var");
            ndv5 = new NominalDataValue(db, ufa.getID(), "?");
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( nom_mve == null ) ||
             ( nfa == null ) ||
             ( ndv0 == null ) ||
             ( matrix_mve == null ) ||
             ( ufa == null ) ||
             ( ndv1 == null ) ||
             ( ndv2 == null ) ||
             ( ndv3 == null ) ||
             ( ndv4 == null ) ||
             ( ndv5 == null ) ||
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
                
                if ( nom_mve == null )
                {
                    outStream.print("allocation of nom_mve failed.\n");
                }
                
                if ( nfa == null )
                {
                    outStream.print("allocation of nfa failed.\n");
                }

                if ( ndv0 == null )
                {
                    outStream.print("new NominalDataValue(db, nfa.getID(), " +
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

                if ( ndv1 == null )
                {
                    outStream.print("new NominalDataValue(db, ufa.getID(), " +
                                    "\"delta\") returned null.\n");
                }

                if ( ndv2 == null )
                {
                    outStream.print("new NominalDataValue(db, ufa.getID(), " +
                                    "\"charlie\") returned null.\n");
                }

                if ( ndv3 == null )
                {
                    outStream.print("new NominalDataValue(db, ufa.getID(), " +
                                    "\"?query_var\") returned null.\n");
                }

                if ( ndv4 == null )
                {
                    outStream.print("new NominalDataValue(db, ufa.getID(), " +
                                    "\"!?query_var\") returned null.\n");
                }

                if ( ndv5 == null )
                {
                    outStream.print("new NominalDataValue(db, ufa.getID(), " +
                                    "\"?\") returned null.\n");
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
            failures += DataValue.TestAccessors(db, nfa, matrix_mve, ufa,
                                                ndv0, outStream, verbose);
            
            if ( ndv0.getSubRange() != false )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv0.getSubRange() != false");
                }
            }
            
            if ( ( ndv0.getItsValue() == null ) ||
                 ( ndv0.getItsValue().compareTo("bravo") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv.getItsValue() != \"bravo\"\n");
                }
            }
            
            ndv0.setItsValue("echo");

            
            if ( ( ndv0.getItsValue() == null ) ||
                 ( ndv0.getItsValue().compareTo("echo") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv0.getItsValue() != \"echo\"\n");
                }
            }
            
            /************************************/

            if ( ndv1.getSubRange() != false )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv1.getSubRange() != false\n");
                }
            }
            
            if ( ( ndv1.getItsValue() == null ) ||
                 ( ndv1.getItsValue().compareTo("delta") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv1.getItsValue() != \"delta\"\n");
                }
            }
            
            failures += DataValue.TestAccessors(db, ufa, nom_mve, nfa,
                                                ndv1, outStream, verbose);

            if ( ndv1.getSubRange() != true )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv1.getSubRange() != true\n");
                }
            }
            
            if ( ndv1.getItsValue() != null )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv1.getItsValue() != null (1)\n");
                }
            }
            
            ndv1.setItsValue("foxtrot");
            
            if ( ndv1.getItsValue() != null )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv1.getItsValue() != null (2)\n");
                }
            }
            
            ndv1.setItsValue("alpha");
            
            if ( ( ndv1.getItsValue() == null ) ||
                 ( ndv1.getItsValue().compareTo("alpha") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv1.getItsValue() != \"alpha\".\n");
                }
            }
            
            if ( ( ndv1.coerceToRange("alpha") == null ) ||
                 ( ndv1.coerceToRange("alpha").compareTo("alpha") != 0 ) ||
                 ( ndv1.coerceToRange("bravo") == null ) ||
                 ( ndv1.coerceToRange("bravo").compareTo("bravo") != 0 ) ||
                 ( ndv1.coerceToRange("charlie") == null ) ||
                 ( ndv1.coerceToRange("charlie").compareTo("charlie") != 0 ) ||
                 ( ndv1.coerceToRange("echo") != null ) ||
                 ( ndv1.coerceToRange("alph") != null ) ||
                 ( ndv1.coerceToRange("alphaa") != null ) ||
                 ( ndv1.coerceToRange("charly") != null ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "unexpected results from ndv1.coerceToRange()\n");
                }
            }
            
            /*********************************/
            
            failures += DataValue.TestAccessors(db, ufa, nom_mve, nfa,
                                                ndv2, outStream, verbose);
            
            if ( ( ndv2.getItsValue() == null ) ||
                 ( ndv2.getItsValue().compareTo("charlie") != 0 ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("ndv2.getItsValue() != \"charlie\".\n");
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( ( ndv3.isQueryVar() != true ) ||
                 ( ndv4.isQueryVar() != false ) ||
                 ( ndv5.isQueryVar() != true ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ndv3.isQueryVar() != true )
                    {
                        outStream.printf(
                                "ndv3.isQueryVar() != true for val = \"%s\"\n",
                                ndv3.getItsValue());
                    }
                    
                    if ( ndv4.isQueryVar() != false )
                    {
                        outStream.printf(
                                "ndv4.isQueryVar() != false for val = \"%s\"\n",
                                ndv4.getItsValue());
                    }
                    
                    if ( ndv5.isQueryVar() != true )
                    {
                        outStream.printf(
                                "ndv5.isQueryVar() != true for val = \"%s\"\n",
                                ndv5.getItsValue());
                    }
                }
            }
            
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ndv3.setItsValue("charlie");
                ndv4.setItsValue("?ord");
                ndv5.setItsValue("?1");

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ndv3.isQueryVar() != false ) ||
                 ( ndv4.isQueryVar() != true ) ||
                 ( ndv5.isQueryVar() != true ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( ndv3.isQueryVar() != false )
                    {
                        outStream.printf(
                                "ndv3.isQueryVar() != false for val = \"%s\"\n",
                                ndv3.getItsValue());
                    }
                    
                    if ( ndv4.isQueryVar() != true )
                    {
                        outStream.printf(
                                "ndv4.isQueryVar() != true for val = \"%s\"\n",
                                ndv4.getItsValue());
                    }
                    
                    if ( ndv5.isQueryVar() != true )
                    {
                        outStream.printf(
                                "ndv5.isQueryVar() != true for val = \"%s\"\n",
                                ndv5.getItsValue());
                    }

                    if ( ! completed )
                    {
                        outStream.printf("Query var setItsValue test " +
                                         "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Query var setItsValue test threw " +
                                "a system error exception: \"%s\"",
                                systemErrorExceptionString);
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
        
    } /* NominalDataValue::TestAccessors() */

    
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
            "Testing copy constructor for class NominalDataValue              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement nom_mve = null;
        MatrixVocabElement nom_mve_sr = null;
        NominalFormalArg nfa = null;
        NominalFormalArg nfa_sr = null;
        NominalDataValue ndv = null;
        NominalDataValue ndv_copy = null;
        NominalDataValue ndv_sr0 = null;
        NominalDataValue ndv_sr0_copy = null;
        NominalDataValue ndv_sr1 = null;
        NominalDataValue ndv_sr1_copy = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        db = null;
        ndv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        /* setup the base entries for the copy test */
        try
        {
            db = new ODBCDatabase();
            
            nom_mve = new MatrixVocabElement(db, "nom_mve");
            nom_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            nfa = new NominalFormalArg(db);
            nom_mve.appendFormalArg(nfa);
            db.vl.addElement(nom_mve);

            ndv = new NominalDataValue(db, nfa.getID(), "foxtrot");
            
            nom_mve_sr = new MatrixVocabElement(db, "nom_mve_sr");
            nom_mve_sr.setType(MatrixVocabElement.MatrixType.NOMINAL);
            nfa_sr = new NominalFormalArg(db);
            nfa_sr.setSubRange(true);
            nfa_sr.addApproved("alpha");
            nfa_sr.addApproved("bravo");
            nfa_sr.addApproved("charlie");
            nom_mve_sr.appendFormalArg(nfa_sr);
            db.vl.addElement(nom_mve_sr);

            ndv_sr0 = new NominalDataValue(db, nfa_sr.getID(), "charlie");
            ndv_sr1 = new NominalDataValue(db, nfa_sr.getID(), "mike");
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( nom_mve == null ) ||
             ( nfa == null ) ||
             ( ndv == null ) ||
             ( nom_mve_sr == null ) ||
             ( nfa_sr == null ) ||
             ( ndv_sr0 == null ) ||
             ( ndv_sr1 == null ) ||
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
                
                if ( nom_mve == null )
                {
                    outStream.print("allocation of nom_mve failed.\n");
                }
                
                if ( nfa == null )
                {
                    outStream.print("allocation of nfa failed.");
                }

                if ( ndv == null )
                {
                    outStream.print("new NominalDataValue(db, nfa.getID(), " +
                                    "\"foxtrot\") returned null.\n");
                }
                
                if ( nom_mve_sr == null )
                {
                    outStream.print("allocation of nom_mve_sr failed.\n");
                }
                
                if ( nfa_sr == null )
                {
                    outStream.print("allocation of nfa_sr failed.");
                }

                if ( ndv_sr0 == null )
                {
                    outStream.print("new NominalDataValue(db, nfa_sr.getID(), " +
                                    "\"charlie\") returned null.\n");
                }

                if ( ndv_sr1 == null )
                {
                    outStream.print("new NominalDataValue(db, nfa_sr.getID(), " +
                                    "\"mike\") returned null.\n");
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
            ndv_copy = null;
            ndv_sr0_copy = null;
            ndv_sr1_copy = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            /* setup the base entries for the copy test */
            try
            {
                ndv_copy = new NominalDataValue(ndv);
                ndv_sr0_copy = new NominalDataValue(ndv_sr0);
                ndv_sr1_copy = new NominalDataValue(ndv_sr1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
        
            if ( ( ndv_copy == null ) ||
                 ( ndv_sr0_copy == null ) ||
                 ( ndv_sr1_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( ndv_copy == null )
                    {
                        outStream.print(
                            "new NominalDataValue(ndv) returned null.\n");
                    }

                    if ( ndv_sr0_copy == null )
                    {
                        outStream.print(
                            "new NominalDataValue(ndv_sr0) returned null.\n");
                    }

                    if ( ndv_sr1_copy == null )
                    {
                        outStream.print(
                            "new NominalDataValue(ndv_sr1) returned null.\n");
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
            failures += DataValue.VerifyDVCopy(ndv, ndv_copy, outStream, 
                                               verbose, "ndv", "ndv_copy");

            failures += DataValue.VerifyDVCopy(ndv_sr0, ndv_sr0_copy, outStream, 
                                            verbose, "ndv_sr0", "ndv_sr0_copy");

            failures += DataValue.VerifyDVCopy(ndv_sr1, ndv_sr1_copy, outStream, 
                                            verbose, "ndv_sr1", "ndv_sr1_copy");
        }
        
        
        /* verify that the constructor fails when given an invalid dv */
        if ( failures == 0 )
        {
            ndv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ndv = new NominalDataValue((NominalDataValue)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ndv != null ) || 
                 ( completed ) ||
                 ( ! threwSystemErrorException ) ) 
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "new NominalDataValue(null) completed.\n");
                    }

                    if ( ndv != null )
                    {
                        outStream.print(
                            "new NominalDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new NominalDataValue(null) failed " +
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
        
    } /* NominalDataValue::TestCopyConstructor() */
    
    
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
        String testString0 = "bravo";
        String testDBString0 = "(NominalDataValue (id 100) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 500) " +
                                    "(itsValue bravo) " +
                                    "(subRange true))";
        String testString1 = "nero";
        String testDBString1 = "(NominalDataValue (id 101) " +
                                    "(itsFargID 8) " +
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
        MatrixVocabElement nom_mve = null;
        MatrixVocabElement matrix_mve = null;
        NominalFormalArg nfa = null;
        UnTypedFormalArg ufa = null;
        NominalDataValue ndv0 = null;
        NominalDataValue ndv1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        db = null;
        ndv0 = null;
        ndv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;
        
        try
        {
            db = new ODBCDatabase();
            
            nom_mve = new MatrixVocabElement(db, "nom_mve");
            nom_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
            nfa = new NominalFormalArg(db);
            nfa.setSubRange(true);
            nfa.addApproved("alpha");
            nfa.addApproved("bravo");
            nfa.addApproved("charlie");
            nom_mve.appendFormalArg(nfa);
            db.vl.addElement(nom_mve);

            ndv0 = new NominalDataValue(db, nfa.getID(), "bravo");
            ndv0.id = 100;        // invalid value for print test
            ndv0.itsCellID = 500; // invalid value for print test
            
            matrix_mve = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve);

            ndv1 = new NominalDataValue(db, ufa.getID(), "nero");
            ndv1.id = 101;        // invalid value for print test
            ndv1.itsCellID = 501; // invalid value for print test
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( db == null ) ||
             ( nom_mve == null ) ||
             ( nfa == null ) ||
             ( ndv0 == null ) ||
             ( matrix_mve == null ) ||
             ( ufa == null ) ||
             ( ndv1 == null ) ||
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
                
                if ( nom_mve == null )
                {
                    outStream.print("allocation of nom_mve failed.\n");
                }
                
                if ( nfa == null )
                {
                    outStream.print("allocation of nfa failed.\n");
                }

                if ( ndv0 == null )
                {
                    outStream.print("new NominalDataValue(db, nfa.getID(), " +
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

                if ( ndv1 == null )
                {
                    outStream.print("new NominalDataValue(db, ufa.getID(), " +
                                    "100) returned null.\n");
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
            if ( ndv0.toString().compareTo(testString0) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected ndv0.toString(): \"%s\".\n",
                                     ndv0.toString());
                }
            }
            
            if ( ndv0.toDBString().compareTo(testDBString0) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected ndv0.toDBString(): \"%s\".\n",
                                     ndv0.toDBString());
                }
            }
            
            if ( ndv1.toString().compareTo(testString1) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected ndv1.toString(): \"%s\".\n",
                                     ndv1.toString());
                }
            }
            
            if ( ndv1.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected ndv1.toDBString(): \"%s\".\n",
                                     ndv1.toDBString());
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
        
    } /* NominalDataValue::TestToStringMethods() */

    
    /**
     * VerifyNominalDVCopy()
     *
     * Verify that the supplied instances of NominalDataValue are distinct, 
     * that they contain no common references (other than db), and that they 
     * have the same value.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyNominalDVCopy(NominalDataValue base,
                                          NominalDataValue copy,
                                          java.io.PrintStream outStream,
                                          boolean verbose,
                                          String baseDesc,
                                          String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyNominalDVCopy: %s null on entry.\n", 
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyNominalDVCopy: %s null on entry.\n", 
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
                        "%s.itsValue is null---, and %s.itsValue isn't.\n",
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
                    "%s.itsValue and %s.itsValue contain different values.\n", 
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

    } /* NominalDataValue::VerifyNominalDVCopy() */

} /* NominalDataValue */

