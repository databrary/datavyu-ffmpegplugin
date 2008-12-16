/*
 * UnTypedFormalArg.java
 *
 * Untyped formal argument in a matrix or predicate argument list.  
 *
 * This is the old style MacSHAPA formal argument that can be replaced
 * with a value of integer, floating point, text, nominal, or predicate 
 * type.
 *
 * Created on January 25, 2007, 4:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 *
 * @author mainzer
 */
public class UnTypedFormalArg 
        extends FormalArgument         
{
       
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    
    // None.
     
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    
    /** 
     * UnTypedFormalArg()
     *
     * Constructors for un-typed formal arguments.  
     *
     * Three versions of this constructor -- one that takes a formal 
     * argument name, one that doesn't, and one that accepts another
     * instance of UnTypedFormalArg and creates a copy.
     *
     * Changes:
     *
     *    - None.
     *
     *                                          JRM -- 1/25/07
     */
    
    public UnTypedFormalArg(Database db) 
        throws SystemErrorException
    {
        super(db);
        
        this.fargType = fArgType.UNTYPED;
        
    } /* UnTypedFormalArg() -- one argument */
    
    public UnTypedFormalArg(Database db,
                            String name)
        throws SystemErrorException
    {
        
        super(db, name);
        
        this.fargType = fArgType.UNTYPED;

    } /* UnTypedFormalArg() -- two arguments */
    
    public UnTypedFormalArg(UnTypedFormalArg fArg)
        throws SystemErrorException    
    {
        super(fArg);

        final String mName = "UnTypedFormalArg::UnTypedFormalArg(): ";  
        
        this.fargType = fArgType.UNTYPED;
        
        if ( ! ( fArg instanceof UnTypedFormalArg ) )
        {
            throw new SystemErrorException(mName + "fArg not a UnTypedFormalArg");
        }
        
        // copy over fields -- none in this case.

    } /* UnTypedFormalArg() -- make copy */
       
        
    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/
    
    // None.
     
        
    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/
    
    /**
     * constructArgWithSalvage()  Override of abstract method in FormalArgument
     *
     * Return an instance of DataValue initialized from salvage if 
     * possible, and an instance UndefinedDataValue initialized with the 
     * formal argument name otherwise.
     *
     * Changes:
     *
     *    - None.
     */
    
    DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException
    {
        final String mName = "UnTypedFormalArg::constructArgWithSalvage(): ";  
        DataValue retVal;
        
        if ( ( salvage == null ) ||
             ( salvage.getItsFargID() == DBIndex.INVALID_ID ) )
        {
            retVal =  new UndefinedDataValue(this.db, this.id, 
                                             this.getFargName());
        }
        else if ( salvage instanceof ColPredDataValue )
        {
            retVal = new ColPredDataValue(this.db, this.id,
                    ((ColPredDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof FloatDataValue )
        {
            retVal = new FloatDataValue(this.db, this.id,
                    ((FloatDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof IntDataValue )
        {
            retVal = new IntDataValue(this.db, this.id,
                    ((IntDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof NominalDataValue )
        {
            retVal = new NominalDataValue(this.db, this.id,
                    ((NominalDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof PredDataValue )
        {
            retVal = new PredDataValue(this.db, this.id,
                    ((PredDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof TextStringDataValue )
        {
            TextStringDataValue textDV = (TextStringDataValue)salvage;
            
            if ( this.db.IsValidQuoteString(textDV.getItsValue()) )
            {
                retVal = new QuoteStringDataValue(this.db, this.id, 
                                                  textDV.getItsValue());
            }
            else
            {
                // todo: Think of coercing the text string into a quote string
                //       instead of just discarding it.
                retVal =  new UndefinedDataValue(this.db, this.id, 
                                             this.getFargName());
            }
        }
        else if ( salvage instanceof TimeStampDataValue )
        {
            retVal = new TimeStampDataValue(this.db, this.id,
                    ((TimeStampDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof QuoteStringDataValue )
        {
            retVal = new QuoteStringDataValue(this.db, this.id,
                    ((QuoteStringDataValue)salvage).getItsValue());
        }
        else if ( salvage instanceof UndefinedDataValue )
        {
            retVal =  new UndefinedDataValue(this.db, this.id, 
                                             this.getFargName());
        }
        else
        {
            throw new SystemErrorException(mName + "salvage of unknown type");
        }
        
        return retVal;
        
    } /* UnTypedDataValue::constructArgWithSalvage(salvage) */
    
    
    /**
     * constructEmptyArg()  Override of abstract method in FormalArgument
     *
     * Return an instance of UndefinedDataValue initialized with the 
     * formal argument name.
     *
     * Changes:
     *
     *    - None.
     */
    
     public DataValue constructEmptyArg()
        throws SystemErrorException
     {
         
         return new UndefinedDataValue(this.db, this.id, this.getFargName());
         
     } /* UnTypedFormalArg::constructEmptyArg() */


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
        
        return ("(UnTypedFormalArg " + getID() + " " + getFargName() + ")");
        
    } /* UnTypedFormalArg::toDBString() */
    
    
    /**
     * isValidValue() -- Overide abstract method in FormalArgument
     * 
     * Boolean method that returns true iff the provided value is an acceptable 
     * value to be assigned to this formal argument. 
     * 
     * Note that the method will not accept valid text strings -- these can
     * only be used to replace instance of class TextStringFormalArg.
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
        String mName = "UnTypedFormalArg::isValidValue()";
        
        if ( ( Database.IsValidInt(obj) ) ||
             ( Database.IsValidFloat(obj) ) ||
             ( Database.IsValidNominal(obj) ) ||
             ( Database.IsValidQuoteString(obj) ) )
        {
            return true;
        }
        
        if ( Database.IsValidTimeStamp(obj) )
        {
            TimeStamp ts;
            
            ts = (TimeStamp)obj;
            
            if ( ts.getTPS() == this.db.getTicks() )
            {
                return true;
            }
        }
        
        if ( obj instanceof Predicate )
        {
            long pveID;
            DBElement dbe = null;
            Predicate pred = null;

            pred = (Predicate)obj;
            
            if ( pred.getDB() != this.db )
            {
                return false;
            }
            
            pveID = pred.getPveID();
            
            if ( pveID != DBIndex.INVALID_ID )
            {
                // lookup the target pve.  Throw a system error if
                // the target pve doesn't exist.
                
                dbe = this.db.idx.getElement(pveID);

                if ( dbe == null )
                {
                    throw new SystemErrorException(mName + 
                                                   "pveID has no referent");
                }

                if ( ! ( dbe instanceof PredicateVocabElement ) )
                {
                    throw new SystemErrorException(mName +
                            "pveID doesn't refer to a predicate vocab element");
                }
            }

            return true;
        }
        
        return false;
        
    } /* UnTypedFormalArg::isValidValue() */

    
    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/
    
    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors for this class.
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean TestAccessors(java.io.PrintStream outStream,
                                        boolean verbose)
    {
        String testBanner =
            "Testing class UnTypedFormalArg accessors                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwInvalidFargNameException = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        UnTypedFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
            arg = null;
            
            try
            {
                arg = new UnTypedFormalArg(new ODBCDatabase());
            }
            
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
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
                                "new UnTypedFormalArg(db) returned null.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new UnTypedFormalArg(db) " +
                                "threw unexpected system error exception: " +
                                "\"%s\".\n", systemErrorExceptionString);
                    }
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
        
        /* UnTypedFormalArgument adds no new fields, so we are done. */
         
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
        
    } /* UnTypedFormalArg::TestAccessors() */
    
    
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
            "Testing class UnTypedFormalArg itsVocabElement accessors         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        UnTypedFormalArg arg = null;

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
            arg = new UnTypedFormalArg(new ODBCDatabase());
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
                            "new UnTypedFormalArg(db) returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("new UnTypedFormalArg(db) threw " +
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
        
    } /* UnTypedFormalArg::TestVEAccessors() */

    
    /**
     * TestClassUnTypedFormalArg()
     *
     * Main routine for tests of class UntypedFormalArg.
     *
     *                                      JRM -- 3/10/07
     *
     * Changes:
     *
     *    - Non.
     */
    
    public static boolean TestClassUnTypedFormalArg(
            java.io.PrintStream outStream,
            boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing class UnTypedFormalArg:\n");
        
        if ( ! Test1ArgConstructor(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! Test2ArgConstructor(outStream, verbose) )
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
            outStream.printf("%d failures in tests for class UnTypedFormalArg.\n\n",
                              failures);
        }
        else
        {
            outStream.print("All tests passed for class UnTypedFormalArg.\n\n");
        }
        
        return pass;
        
    } /* Database::TestDatabase() */
    
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
            "Testing 1 argument constructor for class UnTypedFormalArg        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        UnTypedFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            arg = null;
            systemErrorExceptionString = null;
                    
            try
            {
                arg = new UnTypedFormalArg(new ODBCDatabase());
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! methodReturned ) ||
                 ( arg == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "new UntypedFormalArg(db) failed to return.\n");
                    }
                    
                    if ( arg == null )
                    {
                        outStream.print(
                                "new UntypedFormalArg(db) returned null.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new UntypedFormalArg(db) threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        if ( failures == 0 )
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
        
        
        /* Verify that the constructor fails when passed an invalid db. */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            arg = null;
            systemErrorExceptionString = null;
                    
            try
            {
                arg = new UnTypedFormalArg((Database)null);
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
                        outStream.print(
                                "new UntypedFormalArg(null) returned.\n");
                    }
                    
                    if ( arg != null )
                    {
                        outStream.print(
                             "new UntypedFormalArg(null) returned non-null.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("new UntypedFormalArg(db) failed to " +
                                "throw system error exception.\n");
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
        
    } /* UnTypedFormalArg::Test1ArgConstructor() */
    
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
            "Testing 2 argument constructor for class UnTypedFormalArg        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        UnTypedFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        try
        {
            arg = new UnTypedFormalArg(new ODBCDatabase(), "<valid>");
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
                        "new UnTypedFormalArg(db, \"<valid>\")\" returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.print("new UnTypedFormalArg(db, \"<valid>\")\" " +
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
        
        /* Verify that the constructor fails when passed an invalid db. */
        arg = null;
        systemErrorExceptionString = null;
        threwSystemErrorException = false;
        
        try
        {
            arg = new UnTypedFormalArg(null, "<valid>");
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
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
                        "new UnTypedFormalArg(null, \"<valid>\")\" != null.\n");
                }
                
                if ( ! threwSystemErrorException )
                {
                    outStream.print("new UnTypedFormalArg(null, \"<valid>\")\""
                                   + " didn't throw a SystemErrorException.\n");
                }
            }
        }
        
        /* now verify that the constructor fails when passed an invalid 
         * formal argument name.
         */
        arg = null;
        systemErrorExceptionString = null;
        threwSystemErrorException = false;
        
        try
        {
            arg = new UnTypedFormalArg(new ODBCDatabase(), "<<invalid>>");
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
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
                        "new UnTypedFormalArg(db, \"<<valid>>\") != null.\n");
                }
                
                if ( ! threwSystemErrorException )
                {
                    outStream.print("new UnTypedFormalArg(db, \"<<invalid>>\") "
                        + "didn't throw a SystemErrorException.\n");
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
        
    } /* UnTypedFormalArg::Test2ArgConstructor() */

    
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
            "Testing copy constructor for class UnTypedFormalArg              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        UnTypedFormalArg arg = null;
        UnTypedFormalArg copyArg = null;
        UnTypedFormalArg munged = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* first set up the instance of UnTypedFormalArg to be copied: */
        threwSystemErrorException = false;
        
        try
        {
            arg = new UnTypedFormalArg(new ODBCDatabase(), "<copy_this>");
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
                        "new UnTypedFormalArg(\"<copy_this>\")\" returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.print("new UnTypedFormalArg(\"<copy_this>\")\" " +
                                     "threw a SystemErrorException.\n");
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
                copyArg = new UnTypedFormalArg(arg);
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
                            "new UnTypedFormalArg(arg)\" returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("new UnTypedFormalArg(arg)\" " +
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
                copyArg = new UnTypedFormalArg(copyArg);
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
                            "new UnTypedFormalArg(null)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new UnTypedFormalArg(null)\" " +
                                       "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* now corrupt the fargName field of and instance of UnTypedFormalArg, 
         * and verify that this causes a copy to fail.
         */
        if ( failures == 0 )
        {
            copyArg = null;
            threwSystemErrorException = false;
            
            munged.fargName = "<an invalid name>";

            try
            {
                copyArg = new UnTypedFormalArg(munged);
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
                            "new UnTypedFormalArg(munged)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new UnTypedFormalArg(munged)\" " +
                                "didn't throw an SystemErrorException.\n");
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
        
    } /* UnTypedFormalArg::TestCopyConstructor() */
    
    
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
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        boolean result;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 12;
        /* TODO -- must add predicates to this test */
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return false */ " A Valid \t Text String ",
            /* test  1 -- should return true  */ new Double(0.0),
            /* test  2 -- should return true  */ new Long(0),
            /* test  3 -- should return true  */ "A Valid Nominal",
            /* test  4 -- should return true  */ " A Valid Quote String ",
            /* test  5 -- should return true  */ new TimeStamp(60),
            /* test  6 -- should return false */ new TimeStamp(30, 300),
            /* test  7 -- should return false */ "an invalid text \b string",
            /* test  8 -- should return false */ new Float(0.0),
            /* test  9 -- should return false */ new Integer(0),
            /* test 10 -- should return false */ " An Invalid Nominal \b ",
            /* test 11 -- should return false */ " An Invalid \t Quote string ",
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return false */ " A Valid Text String ",
            /* test  1 -- should return true  */ "new Double(0.0)",
            /* test  2 -- should return true  */ "new Long(0)",
            /* test  3 -- should return true  */ "A Valid Nominal",
            /* test  4 -- should return true  */ " A Valid Quote String ",
            /* test  5 -- should return true  */ "new TimeStamp(60)",
            /* test  6 -- should return false */ "new TimeStamp(30, 300)",
            /* test  7 -- should return false */ "an invalid text \b string",
            /* test  8 -- should return false */ "new Float(0.0)",
            /* test  9 -- should return false */ "new Integer(0)",
            /* test 10 -- should return false */ " An Invalid \t Nominal \b ",
            /* test 11 -- should return false */ " An Invalid \t Quote string ",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ false,
            /* test  1 should return */ true,
            /* test  2 should return */ true,
            /* test  3 should return */ true,
            /* test  4 should return */ true,
            /* test  5 should return */ true,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ false,
            /* test 11 should return */ false,
        };
        long pveID = DBIndex.INVALID_ID;
        long alt_pveID = DBIndex.INVALID_ID;
        Database db = null;
        Database alt_db = null;
        PredicateVocabElement pve = null;
        PredicateVocabElement alt_pve = null;
        FormalArgument farg = null;
        UnTypedFormalArg arg = null;
        Predicate p = null;
        Predicate alt_p = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        arg = null;
        threwSystemErrorException = false;
        
        try
        {
            db = new ODBCDatabase();
            arg = new UnTypedFormalArg(db);
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true; 
        }
        
        if ( ( db == null ) || 
             ( arg == null ) || 
             ( threwSystemErrorException ) )
        {
            failures++;
            
            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.printf("allocation of db failed.\n");
                }
                
                if ( arg == null )
                {
                    outStream.print(
                            "new UnTypedFormalArg(db)\" returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.print("new UnTypedFormalArg(db) threw a system " +
                            "error exception.\n");
                }
            }
        }
        
        if ( failures == 0 )
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
        
        /* we must verify that we handle predicates correctly as well -- 
         * start by allocating some test predicates, and then run the tests
         */
        if ( failures == 0 )
        {
            try
            {
                pve = new PredicateVocabElement(db, "pve");
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg2>");
                pve.appendFormalArg(farg);

                pveID = db.addPredVE(pve);

                // get a copy of the databases version of pve0 with ids assigned
                pve = db.getPredVE(pveID);

                p = new Predicate(db, pveID);

            
                alt_db = new ODBCDatabase();


                alt_pve = new PredicateVocabElement(alt_db, "alt_pve");
                farg = new UnTypedFormalArg(alt_db, "<alt_pve>");
                alt_pve.appendFormalArg(farg);

                alt_pveID = alt_db.addPredVE(alt_pve);

                // get a copy of the alt_db's version of alt_pve with ids assigned
                alt_pve = db.getPredVE(alt_pveID);

                alt_p = new Predicate(alt_db, alt_pveID);


                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( pve == null ) ||
                 ( pveID == DBIndex.INVALID_ID ) ||
                 ( p == null ) ||
                 ( alt_db == null ) ||
                 ( alt_pve == null ) ||
                 ( alt_pveID == DBIndex.INVALID_ID ) ||
                 ( alt_p == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( pve == null )
                    {
                        outStream.printf("allocation of pve failed.\n");
                    }
                    
                    if ( pveID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("pveID == DBIndex.INVALID_ID.\n");
                    }
                    
                    if ( p == null )
                    {
                        outStream.printf("allocation of p failed.\n");
                    }
                    
                    if ( alt_db == null )
                    {
                        outStream.printf("allocation of alt_db failed.\n");
                    }
                    
                    if ( alt_pve == null )
                    {
                        outStream.printf("allocation of alt_pve failed.\n");
                    }
                    
                    if ( alt_pveID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("alt_pveID == DBIndex.INVALID_ID.\n");
                    }
                    
                    if ( alt_p == null )
                    {
                        outStream.printf("allocation of alt_p failed.\n");
                    }
                    
                    if ( ! completed )
                    {
                        outStream.printf(
                                "pred test setup failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("pred test setup threw a " +
                                "SystemErrorException: \"%s\".\n", 
                                systemErrorExceptionString);
                    }
                }
            }
            else
            {
                if ( ! arg.isValidValue(p) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf("arg.isValidValue(p) is false.\n");
                    }
                }
                
                if ( arg.isValidValue(alt_p) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf("arg.isValidValue(alt_p) is true.\n");
                    }
                }
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

            completed = false;
            threwSystemErrorException = false;
            result = false;

            try
            {
                result = arg.isValidValue(null);
                completed = true;
            }
            
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true; 
            }

            if ( ( result != false ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( threwSystemErrorException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    else if ( completed )
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
        
    } /* UnTypedFormalArg::TestIsValidValue() */
    
    
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
        UnTypedFormalArg arg = null;

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
                arg = new UnTypedFormalArg(new ODBCDatabase(), "<test>");
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
                            "new UnTypedFormalArg(\"<test>\")\" returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("new UnTypedFormalArg(\"<test>\")\" " +
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
                outStream.printf(
                        "arg.toString() returned unexpected value: \"%s\".\n",
                        arg.toString());
            }
        }
        
        if ( arg != null )
        {
            if ( arg.toDBString().compareTo("(UnTypedFormalArg 0 <test>)") 
                 != 0 )
            {
                failures++;
                outStream.printf(
                        "arg.toDBString() returned unexpected value: \"%s\".\n",
                        arg.toDBString());
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
        
    } /* UnTypedFormalArg::TestToStringMethods() */
    
} /* class UnTypedFormalArg */
