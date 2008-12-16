/*
 * ColPredFormalArg.java
 *
 * Created on July 20, 2001, 10:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Class PredFormalArg
 *
 * Instances of this class are used for formal arguments that have been 
 * strongly typed to column predicates.  Note that this class is quite similar
 * to the PredFormalArg class, differing mainly in the lack of any facility
 * for subranging, and in the fact that instances of it must be replaces with
 * column predicates (the predicate implied by columns), instead of regular
 * predicates.
 *
 * @author mainzer
 */
public class ColPredFormalArg extends FormalArgument
{
        
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    
    /*** None ***/
    
    
    
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    
    /** 
     * ColPredFormalArg()
     *
     * Constructors for column predicate typed formal arguments.  
     *
     * Three versions of this constructor -- one that takes only a database 
     * referenece, one that takes a database reference and the formal argument
     * name as a parameters, and one that takes a reference to an instance of 
     * ColPredFormalArg and uses it to create a copy.
     *
     *                                          JRM -- 8/6/08
     *
     * Changes:
     *
     *    - None.
     *      
     */

    public ColPredFormalArg(Database db) 
        throws SystemErrorException
    {
        
        super(db);
        
        this.fargType = fArgType.COL_PREDICATE;
        
    } /* NominalFormalArg() -- no parameters */
    
    public ColPredFormalArg(Database db,
                            String name) 
        throws SystemErrorException
    {
        
        super(db, name);
        
        this.fargType = fArgType.COL_PREDICATE;
        
    } /* NominalFormalArg() -- one parameter */
    
    public ColPredFormalArg(ColPredFormalArg fArg)
        throws SystemErrorException    
    {
        super(fArg);

        final String mName = "ColPredFormalArg::ColPredFormalArg(): ";  
        
        this.fargType = fArgType.COL_PREDICATE;

    } /* PredFormalArg() -- make copy */
    
    
        
    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /*** None ***/
     
        
    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/
    
    /**
     * constructArgWithSalvage()  Override of abstract method in FormalArgument
     *
     * Return an instance of ColPredDataValue initialized from salvage if 
     * possible, and to the default for newly created instances of 
     * PredDataValue otherwise.
     *                                      JRM -- 8/6/08
     *
     * Changes:
     *
     *    - None.
     */
    
    DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException
    {
        ColPredDataValue retVal;
        
        if ( ( salvage == null ) ||
             ( salvage.getItsFargID() == DBIndex.INVALID_ID ) )
        {
            retVal = new ColPredDataValue(this.db, this.id); 
        }
        else if ( salvage instanceof ColPredDataValue )
        {
            retVal = new ColPredDataValue(this.db, this.id,
                    ((ColPredDataValue)salvage).getItsValue());
        }
        else
        {
            retVal = new ColPredDataValue(this.db, this.id); 
        }
        
        return retVal;
        
    } /* ColPredDataValue::constructArgWithSalvage(salvage) */
    
    
    /**
     * constructEmptyArg()  Override of abstract method in FormalArgument
     *
     * Return an instance of ColPredDataValue initialized as appropriate for 
     * an argument that has not had any value assigned to it by the user.
     *
     *                                      JRM -- 8/6/08
     *
     * Changes:
     *
     *    - None.
     */
    
     public DataValue constructEmptyArg()
        throws SystemErrorException
     {
         
         return new ColPredDataValue(this.db, this.id);
         
     } /* PredFormalArg::constructEmptyArg() */

     
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
     *                                      JRM -- 8/6/08
     *
     * Changes:
     *
     *    - None.
     *      
     */
    public String toDBString() {
        
        return ("(ColPredFormalArg " + getID() + " " + getFargName() + ")");
        
    } /* ColPredFormalArg::toDBString() */
    
     
    /**
     * isValidValue() -- Override of abstract method in FormalArgument
     * 
     * Boolean method that returns true iff the provided value is an acceptable 
     * value to be assigned to this formal argument.
     * 
     *                                      JRM -- 8/6/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public boolean isValidValue(Object obj)
        throws SystemErrorException
    {
        final String mName = "PredFormalArg::isValidValue(): ";
        ColPred cPred = null;
        
        if ( obj instanceof ColPred )
        {
            cPred = (ColPred)obj;
            
            if ( cPred.getDB() != this.db )
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        return false;
        
    } /*  ColPredFormalArg::isValidValue() */

    
    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/
    
    /*** TODO: Review test code. ***/
    
    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors for this class.  At present,
     * there are none particular to this class, so we just call the inherited
     * accessor test code.
     *
     *                                      JRM -- 8/6/08
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean TestAccessors(java.io.PrintStream outStream,
                                        boolean verbose)
    {
        String testBanner =
            "Testing class PredFormalArg accessors                            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean testFinished = false;
        boolean threwInvalidFargNameException = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        ColPredFormalArg arg = null;
        Database db = null;

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
            db = new ODBCDatabase();
            arg = new ColPredFormalArg(db);
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
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
                    outStream.print("new ODBCDatabase() returned null.\n");
                }
                
                if ( arg == null )
                {
                    outStream.print("new ColPredFormalArg(db) returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("new ColPredFormalArg(db) threw " +
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
                    outStream.print("FormalArgument.TestAccessors." +
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
        
    } /* ColPredFormalArg::TestAccessors() */
    
    
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
            "Testing class PredFormalArg itsVocabElement accessors            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        ColPredFormalArg arg = null;

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
            arg = new ColPredFormalArg(new ODBCDatabase());
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
                            "new ColPredFormalArg(db) returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("new ColPredFormalArg(db) threw " +
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
        
    } /* ColPredFormalArg::TestVEAccessors() */

    
    /**
     * TestClassColPredFormalArg()
     *
     * Main routine for tests of class ColPredFormalArg.
     *
     *                                      JRM -- 9/13/08
     *
     * Changes:
     *
     *    - Non.
     */
    
    public static boolean TestClassColPredFormalArg(java.io.PrintStream outStream,
                                                    boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing class ColPredFormalArg:\n");
        
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
            outStream.printf(
                    "%d failures in tests for class ColPredFormalArg.\n\n",
                    failures);
        }
        else
        {
            outStream.print("All tests passed for class ColPredFormalArg.\n\n");
        }
        
        return pass;
        
    } /* ColPredFormalArg::TestClassPredFormalArg() */
    
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
            "Testing 1 argument constructor for class ColPredFormalArg        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        ColPredFormalArg arg = null;

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
            arg = new ColPredFormalArg(new ODBCDatabase());
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
                    outStream.print("new ColPredFormalArg(db) returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("new ColPredFormalArg(db) threw " +
                                      "system error exception: \"%s\"\n",
                                      systemErrorExceptionString);
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
        
        /* Verify that the constructor fails if passed a bad db */
        if ( failures == 0 )
        {
            arg = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                arg = new ColPredFormalArg((Database)null);
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
                                "new ColPredFormalArg(null) returned.\n");
                    }

                    if ( arg != null )
                    {
                        outStream.print(
                            "new ColPredFormalArg(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new ColPredFormalArg(null) didn't " +
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
        
    } /* ColPredFormalArg::Test1ArgConstructor() */
    
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
            "Testing 2 argument constructor for class ColPredFormalArg        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        ColPredFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        try
        {
            arg = new ColPredFormalArg(new ODBCDatabase(), "<valid>");
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
                    outStream.print("new ColPredFormalArg(db, \"<valid>\") " +
                                    "returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.print("new ColPredFormalArg(db, \"<valid>\") " +
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
        threwSystemErrorException = false;
        
        try
        {
            arg = new ColPredFormalArg(null, "<valid>");
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
                        "new ColPredFormalArg(null, \"<alid>>\") != null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.print("new ColPredFormalArg(null, \"<valid>\") "
                                    + "didn't throw a SystemErrorException.\n");
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
            arg = new ColPredFormalArg(new ODBCDatabase(), "<<invalid>>");
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
                        "new ColPredFormalArg(db, \"<<valid>>\") != null.\n");
                }
                
                if ( ! threwSystemErrorException )
                {
                    outStream.print("new ColPredFormalArg(db, \"<<invalid>>\") "
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
        
    } /* ColPredFormalArg::Test2ArgConstructor() */

    
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
            "Testing copy constructor for class ColPredFormalArg              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        int progress;
        String s = null;
        String systemErrorExceptionString = null;
        ColPredFormalArg arg0 = null;
        ColPredFormalArg arg1 = null;
        ColPredFormalArg copyArg0 = null;
        ColPredFormalArg copyArg1 = null;
        ColPredFormalArg munged = null;
        UnTypedFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        Database db = null;
        PredicateVocabElement p0 = null;
        MatrixVocabElement m0 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* first set up the instance of ColPredFormalArg to be copied: */
        p0      = null;
        m0      = null;
        progress = 0;
        completed = false;
        threwSystemErrorException = false;
        
        try
        {
            db = new ODBCDatabase();
            
            progress++;
            
            alpha   = new UnTypedFormalArg(db, "<alpha>");
            bravo   = new UnTypedFormalArg(db, "<bravo>");
            charlie = new UnTypedFormalArg(db, "<charlie>");
            
            progress++;

            p0 = VocabList.ConstructTestPred(db, "p0", 
                                             alpha, null, null, null);
            m0 = VocabList.ConstructTestMatrix(db, "m0",
                    MatrixVocabElement.MatrixType.MATRIX,
                    bravo, charlie, null, null);
            
            progress++;

            db.vl.addElement(p0);
            db.vl.addElement(m0);
            
            progress++;

            arg0 = new ColPredFormalArg(db, "<copy_this_0>");
            arg0.setHidden(true);
            p0.appendFormalArg(arg0);
            
            progress++;
            
            arg1 = new ColPredFormalArg(db, "<copy_this_1>");
            m0.appendFormalArg(arg1);
            
            progress++;
            
            completed = true;
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( ! completed ) ||
             ( db == null ) ||
             ( arg0 == null ) || 
             ( arg1 == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.printf("progress = %s\n", progress);
                
                if ( ! completed )
                {
                    outStream.print("test setup failed to complete.\n");
                }
                
                if ( db == null )
                {
                    outStream.print("new ODBCDatabase() returned null.\n");
                }
                
                if ( arg0 == null )
                {
                    outStream.print(
                        "new ColPredFormalArg(\"<copy_this_0>\") returned null.\n");
                }
                
                if ( arg1 == null )
                {
                    outStream.print(
                        "new ColPredFormalArg(\"<copy_this_1>\") returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw an unexpected system " +
                            "error exception: %s", systemErrorExceptionString);
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( ! arg0.getHidden() )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.print("Unexpected value of arg0.hidden.\n");
                }
            }
        }
        
        
        /* Now, try to make a copy of arg0 */
        
        if ( failures == 0 )
        {
            copyArg0 = null;
            copyArg1 = null;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                copyArg0 = new ColPredFormalArg(arg0);
                copyArg1 = new ColPredFormalArg(arg1);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( copyArg0 == null ) || ( copyArg1 == null ) ||
                 ( completed == false ) || ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg0 == null )
                    {
                        outStream.print(
                            "new ColPredFormalArg(arg0)\" returned null.\n");
                    }
                    
                    if ( copyArg1 == null )
                    {
                        outStream.print(
                            "new ColPredFormalArg(arg1)\" returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("col pred copy constructor threw an " +
                                "unexpected SystemErrorException: %s\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* verify that the copies are good */
        
        if ( failures == 0 )
        {
            if ( arg0 == copyArg0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.print("(arg0 == copyArg0) ==> " +
                            "same object, not duplicates.\n");
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( arg1 == copyArg1 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.print("(arg1 == copyArg1) ==> " +
                            "same object, not duplicates.\n");
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( arg0.toDBString().compareTo(copyArg0.toDBString()) != 0 )
            {
                failures++;
                        
                if ( verbose )
                {
                    outStream.printf("arg0.toDBString() = \"%s\" != " +
                            "copyArg0.toDBString() = \"%s\".\n", 
                            arg0.toDBString(), copyArg0.toDBString());
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( arg1.toDBString().compareTo(copyArg1.toDBString()) != 0 )
            {
                failures++;
                        
                if ( verbose )
                {
                    outStream.printf("arg1.toDBString() = \"%s\" != " +
                            "copyArg1.toDBString() = \"%s\".\n", 
                            arg0.toDBString(), copyArg0.toDBString());
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( arg0.getHidden() != copyArg0.getHidden() )
            {
                failures++;
                        
                if ( verbose )
                {
                    outStream.printf("arg0.hidden = %b != " +
                            "copyArg0.hidden = %b.\n", arg0.hidden,
                            copyArg0.hidden);
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( arg0.getHidden() != copyArg0.getHidden() )
            {
                failures++;
                        
                if ( verbose )
                {
                    outStream.printf("arg0.hidden = %b != " +
                            "copyArg0.hidden = %b.\n", arg0.hidden,
                            copyArg0.hidden);
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( arg0.getItsVocabElement() != copyArg0.getItsVocabElement() )
            {
                failures++;
                        
                if ( verbose )
                {
                    outStream.printf("arg0.getItsVocabElement() != " +
                            "copyArg0.getItsVocabElement().\n");
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( arg1.getItsVocabElement() != copyArg1.getItsVocabElement() )
            {
                failures++;
                        
                if ( verbose )
                {
                    outStream.printf("arg1.getItsVocabElement() != " +
                            "copyArg1.getItsVocabElement().\n");
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( arg0.getItsVocabElementID() != copyArg0.getItsVocabElementID() )
            {
                failures++;
                        
                if ( verbose )
                {
                    outStream.printf("arg0.getItsVocabElementID() != " +
                            "copyArg0.getItsVocabElementID().\n");
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( arg1.getItsVocabElementID() != copyArg1.getItsVocabElementID() )
            {
                failures++;
                        
                if ( verbose )
                {
                    outStream.printf("arg1.getItsVocabElementID() != \" " +
                            "copyArg1.getItsVocabElementID().\n");
                }
            }
        }

        /* now verify that we fail when we should */
        
        /* first ensure that the copy constructor failes when passed null */
        if ( failures == 0 )
        {
            munged = copyArg0; /* save the copy for later */
            copyArg0 = null;
            threwSystemErrorException = false;

            try
            {
                copyArg0 = null;
                copyArg0 = new ColPredFormalArg(copyArg0);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( copyArg0 != null ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg0 != null )
                    {
                        outStream.print(
                            "new ColPredFormalArg(null) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredFormalArg(null) " +
                                "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* now corrupt the fargName field of and instance of ColPredFormalArg, 
         * and verify that this causes a copy to fail.
         */
        if ( failures == 0 )
        {
            copyArg0 = null;
            threwSystemErrorException = false;
            
            munged.fargName = "<an invalid name>";

            try
            {
                copyArg0 = new ColPredFormalArg(munged);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( copyArg0 != null ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( copyArg0 != null )
                    {
                        outStream.print(
                            "new ColPredFormalArg(munged)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new ColPredFormalArg(munged)\" " +
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
        
    } /* ColPredFormalArg::TestCopyConstructor() */
    
    
    /**
     * TestIsValidValue()
     *
     * Verify that isValidValue() does the right thing.
     *
     *                                          JRM -- 11/22/07
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
        boolean cp0_is_valid;
        boolean cp1_is_valid;
        boolean cp2_is_valid;
        boolean cp3_is_valid;
        boolean cp4_is_valid;
        boolean cp5_is_valid;
        boolean cp6_is_valid;
        boolean cp7_is_valid;
        boolean alt_cp_is_valid;
        int failures = 0;
        int testNum = 0;
//        long mve0ID = DBIndex.INVALID_ID;
//        long mve1ID = DBIndex.INVALID_ID;
//        long mve2ID = DBIndex.INVALID_ID;
//        long mve3ID = DBIndex.INVALID_ID;
//        long mve4ID = DBIndex.INVALID_ID;
//        long mve5ID = DBIndex.INVALID_ID;
//        long mve6ID = DBIndex.INVALID_ID;
//        long mve7ID = DBIndex.INVALID_ID;
//        long alt_mveID = DBIndex.INVALID_ID;
//        Database db = null;
//        Database alt_db = null;
//        FormalArgument farg = null;
//        PredicateVocabElement pve0 = null;
//        PredicateVocabElement pve1 = null;
//        PredicateVocabElement pve2 = null;
//        PredicateVocabElement pve3 = null;
//        PredicateVocabElement pve4 = null;
//        PredicateVocabElement pve5 = null;
//        PredicateVocabElement pve6 = null;
//        PredicateVocabElement pve7 = null;
//        PredicateVocabElement alt_pve = null;
//        PredFormalArg pfa = null;
//        PredFormalArg pfa_sr = null;
//        ColPred p0 = null;
//        ColPred p1 = null;
//        ColPred p2 = null;
//        ColPred p3 = null;
//        ColPred p4 = null;
//        ColPred p5 = null;
//        ColPred p6 = null;
//        ColPred p7 = null;
//        ColPred alt_p = null;
//
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
//        
//        // Start by setting up the needed database, pve's, and preds
//        threwSystemErrorException = false;
//        completed = false;
//
//        try
//        {
//            db = new ODBCDatabase();
//            
//            pve0 = new PredicateVocabElement(db, "pve0");
//            farg = new UnTypedFormalArg(db, "<arg1>");
//            pve0.appendFormalArg(farg);
//            farg = new UnTypedFormalArg(db, "<arg2>");
//            pve0.appendFormalArg(farg);
//            
//            pve0ID = db.addPredVE(pve0);
//            
//            // get a copy of the databases version of pve0 with ids assigned
//            pve0 = db.getPredVE(pve0ID);
//            
//            p0 = new Predicate(db, pve0ID);
//            
//            
//            pve1 = new PredicateVocabElement(db, "pve1");
//            farg = new IntFormalArg(db, "<int>");
//            pve1.appendFormalArg(farg);
//            farg = new UnTypedFormalArg(db, "<arg2>");
//            pve1.appendFormalArg(farg);
//            
//            pve1ID = db.addPredVE(pve1);
//            
//            // get a copy of the databases version of pve1 with ids assigned
//            pve1 = db.getPredVE(pve1ID);
//            
//            p1 = new Predicate(db, pve1ID);
//            
//            
//            pve2 = new PredicateVocabElement(db, "pve2");
//            farg = new UnTypedFormalArg(db, "<arg1>");
//            pve2.appendFormalArg(farg);
//            farg = new NominalFormalArg(db, "<nominal>");
//            pve2.appendFormalArg(farg);
//            farg = new UnTypedFormalArg(db, "<arg3>");
//            pve2.appendFormalArg(farg);
//            
//            pve2ID = db.addPredVE(pve2);
//            
//            // get a copy of the databases version of pve1 with ids assigned
//            pve2 = db.getPredVE(pve2ID);
//            
//            p2 = new Predicate(db, pve2ID);
//            
//            
//            pve3 = new PredicateVocabElement(db, "pve3");
//            farg = new UnTypedFormalArg(db, "<arg1>");
//            pve3.appendFormalArg(farg);
//            pve3.setVarLen(true);
//            
//            pve3ID = db.addPredVE(pve3);
//            
//            // get a copy of the databases version of pve3 with ids assigned
//            pve3 = db.getPredVE(pve3ID);
//            
//            p3 = new Predicate(db, pve3ID);
//
//            
//            pve4 = new PredicateVocabElement(db, "pve4");
//            
//            farg = new FloatFormalArg(db, "<float>");
//            pve4.appendFormalArg(farg);
//            farg = new IntFormalArg(db, "<int>");
//            pve4.appendFormalArg(farg);
//            farg = new NominalFormalArg(db, "<nominal>");
//            pve4.appendFormalArg(farg);
//            farg = new PredFormalArg(db, "<pred>");
//            pve4.appendFormalArg(farg);
//            farg = new QuoteStringFormalArg(db, "<qstring>");
//            pve4.appendFormalArg(farg);
//            farg = new TimeStampFormalArg(db, "<timestamp>");
//            pve4.appendFormalArg(farg);
//            farg = new UnTypedFormalArg(db, "<untyped>");
//            pve4.appendFormalArg(farg);
//            
//            pve4ID = db.addPredVE(pve4);
//            
//            // get a copy of the databases version of pve4 with ids assigned
//            pve4 = db.getPredVE(pve4ID);
//            
//            p4 = new Predicate(db, pve4ID);
//
//            
//            pve5 = new PredicateVocabElement(db, "pve5");
//            farg = new UnTypedFormalArg(db, "<arg>");
//            pve5.appendFormalArg(farg);
//            
//            pve5ID = db.addPredVE(pve5);
//            
//            // get a copy of the databases version of pve5 with ids assigned
//            pve5 = db.getPredVE(pve5ID);
//            
//            p5 = new Predicate(db, pve5ID);
//
//            
//            pve6 = new PredicateVocabElement(db, "pve6");
//            farg = new UnTypedFormalArg(db, "<arg>");
//            pve6.appendFormalArg(farg);
//            
//            pve6ID = db.addPredVE(pve6);
//            
//            // get a copy of the databases version of pve6 with ids assigned
//            pve6 = db.getPredVE(pve6ID);
//            
//            p6 = new Predicate(db, pve6ID);
//
//            
//            pve7 = new PredicateVocabElement(db, "pve7");
//            farg = new UnTypedFormalArg(db, "<arg>");
//            pve7.appendFormalArg(farg);
//            
//            pve7ID = db.addPredVE(pve7);
//            
//            // get a copy of the databases version of pve7 with ids assigned
//            pve7 = db.getPredVE(pve7ID);
//            
//            p7 = new Predicate(db, pve7ID);
//            
//            
//            
//            alt_db = new ODBCDatabase();
//
//            
//            alt_pve = new PredicateVocabElement(alt_db, "alt_pve");
//            farg = new UnTypedFormalArg(alt_db, "<alt_pve>");
//            alt_pve.appendFormalArg(farg);
//            
//            alt_pveID = alt_db.addPredVE(alt_pve);
//            
//            // get a copy of the alt_db's version of alt_pve with ids assigned
//            alt_pve = db.getPredVE(alt_pveID);
//            
//            alt_p = new Predicate(alt_db, alt_pveID);
//
//            
//            completed = true;
//        }
//
//        catch (SystemErrorException e)
//        {
//            threwSystemErrorException = true;
//            systemErrorExceptionString = e.toString();
//        }
//        
//        if ( ( db == null ) ||
//             ( pve0 == null ) ||
//             ( pve0ID == DBIndex.INVALID_ID ) ||
//             ( p0 == null ) ||
//             ( pve1 == null ) ||
//             ( pve1ID == DBIndex.INVALID_ID ) ||
//             ( p1 == null ) ||
//             ( pve2 == null ) ||
//             ( pve2ID == DBIndex.INVALID_ID ) ||
//             ( p2 == null ) ||
//             ( pve3 == null ) ||
//             ( pve3ID == DBIndex.INVALID_ID ) ||
//             ( p3 == null ) ||
//             ( pve4 == null ) ||
//             ( pve4ID == DBIndex.INVALID_ID ) ||
//             ( p4 == null ) ||
//             ( pve5 == null ) ||
//             ( pve5ID == DBIndex.INVALID_ID ) ||
//             ( p5 == null ) ||
//             ( pve6 == null ) ||
//             ( pve6ID == DBIndex.INVALID_ID ) ||
//             ( p6 == null ) ||
//             ( pve7 == null ) ||
//             ( pve7ID == DBIndex.INVALID_ID ) ||
//             ( p7 == null ) ||
//             ( alt_pve == null ) ||
//             ( alt_pveID == DBIndex.INVALID_ID ) ||
//             ( alt_p == null ) ||
//             ( ! completed ) || 
//             ( threwSystemErrorException ) ) 
//        {
//            failures++;
//                    
//            if ( verbose )
//            {
//                if ( db == null )
//                {
//                    outStream.print("new Database() returned null.\n");
//                }
//                
//                if ( pve0 == null )
//                {
//                    outStream.print("creation of pve0 failed.\n");
//                }
//                
//                if ( pve0ID == DBIndex.INVALID_ID )
//                {
//                    outStream.print("pve0ID not initialized.\n");
//                }
//                
//                if ( p0 == null )
//                {
//                    outStream.print("creation of p0 failed.\n");
//                }
//                
//                if ( pve1 == null )
//                {
//                    outStream.print("creation of pve1 failed.\n");
//                }
//                
//                if ( pve1ID == DBIndex.INVALID_ID )
//                {
//                    outStream.print("pve1ID not initialized.\n");
//                }
//                
//                if ( p1 == null )
//                {
//                    outStream.print("creation of p1 failed.\n");
//                }
//                
//                if ( pve2 == null )
//                {
//                    outStream.print("creation of pve2 failed.\n");
//                }
//                
//                if ( pve2ID == DBIndex.INVALID_ID )
//                {
//                    outStream.print("pve2ID not initialized.\n");
//                }
//                
//                if ( p2 == null )
//                {
//                    outStream.print("creation of p2 failed.\n");
//                }
//                
//                if ( pve3 == null )
//                {
//                    outStream.print("creation of pve3 failed.\n");
//                }
//                
//                if ( pve3ID == DBIndex.INVALID_ID )
//                {
//                    outStream.print("pve3ID not initialized.\n");
//                }
//                
//                if ( p3 == null )
//                {
//                    outStream.print("creation of p3 failed.\n");
//                }
//                
//                if ( pve4 == null )
//                {
//                    outStream.print("creation of pve4 failed.\n");
//                }
//                
//                if ( pve4ID == DBIndex.INVALID_ID )
//                {
//                    outStream.print("pve4ID not initialized.\n");
//                }
//                
//                if ( p4 == null )
//                {
//                    outStream.print("creation of p4 failed.\n");
//                }
//                
//                if ( pve5 == null )
//                {
//                    outStream.print("creation of pve5 failed.\n");
//                }
//                
//                if ( pve5ID == DBIndex.INVALID_ID )
//                {
//                    outStream.print("pve5ID not initialized.\n");
//                }
//                
//                if ( p5 == null )
//                {
//                    outStream.print("creation of p5 failed.\n");
//                }
//                
//                if ( pve6 == null )
//                {
//                    outStream.print("creation of pve6 failed.\n");
//                }
//                
//                if ( pve6ID == DBIndex.INVALID_ID )
//                {
//                    outStream.print("pve6ID not initialized.\n");
//                }
//                
//                if ( p6 == null )
//                {
//                    outStream.print("creation of p6 failed.\n");
//                }
//                
//                if ( pve7 == null )
//                {
//                    outStream.print("creation of pve7 failed.\n");
//                }
//                
//                if ( pve7ID == DBIndex.INVALID_ID )
//                {
//                    outStream.print("pve7ID not initialized.\n");
//                }
//                
//                if ( p7 == null )
//                {
//                    outStream.print("creation of p7 failed.\n");
//                }
//                
//                if ( alt_pve == null )
//                {
//                    outStream.print("creation of alt_pve failed.\n");
//                }
//                
//                if ( alt_pveID == DBIndex.INVALID_ID )
//                {
//                    outStream.print("alt_pveID not initialized.\n");
//                }
//                
//                if ( alt_p == null )
//                {
//                    outStream.print("creation of alt_p failed.\n");
//                }
//                
//                if ( ! completed )
//                {
//                    outStream.print("test setup failed to complete (1).\n");
//                }
//
//                if ( threwSystemErrorException )
//                {
//                    outStream.printf("pve allocations threw a " +
//                            "SystemErrorException: \"%s\".\n", 
//                            systemErrorExceptionString);
//                }
//            }
//        }
//        
//        /* Now set up the test formal arguments */
//        if ( failures == 0 )
//        {
//            threwSystemErrorException = false;
//            completed = false;
//
//            try
//            {
//                pfa = new PredFormalArg(db, "<pfa>");
//                
//                pfa_sr = new PredFormalArg(db, "<pfa_sr>");
//                pfa_sr.setSubRange(true);
//                pfa_sr.addApproved(pve0ID);
//                pfa_sr.addApproved(pve2ID);
//                pfa_sr.addApproved(pve4ID);
//                pfa_sr.addApproved(pve6ID);
//
//                completed = true;
//            }
//
//            catch (SystemErrorException e)
//            {
//                threwSystemErrorException = true;
//                systemErrorExceptionString = e.toString();
//            }
//            
//            if ( ( pfa == null ) ||
//                 ( pfa_sr == null ) ||
//                 ( ! completed ) ||
//                 ( threwSystemErrorException ) )
//            {
//                failures++;
//                
//                if ( verbose )
//                {
//                    if ( pfa == null )
//                    {
//                        outStream.print("creation of pfa failed.\n");
//                    }
//                    
//                    if ( pfa_sr == null  )
//                    {
//                        outStream.print("creation of pfa_sr failed.\n");
//                    }
//                
//                    if ( ! completed )
//                    {
//                        outStream.print("test setup failed to complete (2).\n");
//                    }
//
//                    if ( threwSystemErrorException )
//                    {
//                        outStream.printf("pfa allocations threw a " +
//                                "SystemErrorException: \"%s\".\n", 
//                                systemErrorExceptionString);
//                    }
//                }
//            }
//            else
//            {
//                p0_is_valid = pfa.isValidValue(p0);
//                p1_is_valid = pfa.isValidValue(p1);
//                p2_is_valid = pfa.isValidValue(p2);
//                p3_is_valid = pfa.isValidValue(p3);
//                p4_is_valid = pfa.isValidValue(p4);
//                p5_is_valid = pfa.isValidValue(p5);
//                p6_is_valid = pfa.isValidValue(p6);
//                p7_is_valid = pfa.isValidValue(p7);
//                alt_p_is_valid = pfa.isValidValue(alt_p);
//                
//                if ( ( ! p0_is_valid ) ||
//                     ( ! p1_is_valid ) ||
//                     ( ! p2_is_valid ) ||
//                     ( ! p3_is_valid ) ||
//                     ( ! p4_is_valid ) ||
//                     ( ! p5_is_valid ) ||
//                     ( ! p6_is_valid ) ||
//                     ( ! p7_is_valid ) ||
//                     ( alt_p_is_valid ) )
//                {
//                    failures++;
//                    
//                    if ( verbose )
//                    {
//                        outStream.printf("Unexpected results from " +
//                            "pfa.isValidValue: %b %b %b %b %b %b %b %b %b\n",
//                            p0_is_valid,
//                            p1_is_valid,
//                            p2_is_valid,
//                            p3_is_valid,
//                            p4_is_valid,
//                            p5_is_valid,
//                            p6_is_valid,
//                            p7_is_valid,
//                            alt_p_is_valid);
//                    }
//                }
//                
//                p0_is_valid = pfa_sr.isValidValue(p0);
//                p1_is_valid = pfa_sr.isValidValue(p1);
//                p2_is_valid = pfa_sr.isValidValue(p2);
//                p3_is_valid = pfa_sr.isValidValue(p3);
//                p4_is_valid = pfa_sr.isValidValue(p4);
//                p5_is_valid = pfa_sr.isValidValue(p5);
//                p6_is_valid = pfa_sr.isValidValue(p6);
//                p7_is_valid = pfa_sr.isValidValue(p7);
//                alt_p_is_valid = pfa_sr.isValidValue(alt_p);
//                
//                if ( ( ! p0_is_valid ) ||
//                     ( p1_is_valid ) ||
//                     ( ! p2_is_valid ) ||
//                     ( p3_is_valid ) ||
//                     ( ! p4_is_valid ) ||
//                     ( p5_is_valid ) ||
//                     ( ! p6_is_valid ) ||
//                     ( p7_is_valid ) ||
//                     ( alt_p_is_valid ) )
//                {
//                    failures++;
//                    
//                    if ( verbose )
//                    {
//                        outStream.printf("Unexpected results from " +
//                            "pfa_sr.isValidValue: %b %b %b %b %b %b %b %b %b\n",
//                            p0_is_valid,
//                            p1_is_valid,
//                            p2_is_valid,
//                            p3_is_valid,
//                            p4_is_valid,
//                            p5_is_valid,
//                            p6_is_valid,
//                            p7_is_valid,
//                            alt_p_is_valid);
//                    }
//                }
//                 
//                if ( ( pfa.isValidValue(1.0) ) ||
//                     ( pfa.isValidValue(1) ) ||
//                     ( pfa.isValidValue("a string") ) ||
//                     ( pfa.isValidValue(new TimeStamp(db.getTicks(), 0)) ) )
//                {
//                    failures++;
//                    
//                    if ( verbose )
//                    {
//                        outStream.print("pfa.isValidValue() accepted one or " +
//                                        "more non-Predicates.\n");
//                    }
//                }
//                
//                if ( ( pfa_sr.isValidValue(1.0) ) ||
//                     ( pfa_sr.isValidValue(1) ) ||
//                     ( pfa_sr.isValidValue("a string") ) ||
//                     ( pfa_sr.isValidValue(new TimeStamp(db.getTicks(), 0)) ) )
//                {
//                    failures++;
//                    
//                    if ( verbose )
//                    {
//                        outStream.print("pfa_sr.isValidValue() accepted one " +
//                                        "or more non-Predicates.\n");
//                    }
//                }
//            }
//        }
        
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
        
        outStream.printf("          --- TEST NOT IMPLEMENTED ---\n");
        
        return pass;
        
    } /* PredFormalArg::TestIsValidValue() */
    
    
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
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        ColPredFormalArg arg = null;
        Database db = null;

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
                db = new ODBCDatabase();
                arg = new ColPredFormalArg(db, "<test>");
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
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( arg == null )
                    {
                        outStream.print("new ColPredFormalArg(db, \"<test>\")" +
                                        "returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("new ColPredFormalArg(db, \"<test>\") " +
                                         "threw a SystemErrorException.\n");
                    }
                }
                
                arg = null;
            }
        }
        
        if ( failures == 0 )
        {
            if ( arg != null )
            {
                if ( arg.toString().compareTo("<test>") != 0 )
                {
                    failures++;
                    outStream.printf(
                        "arg.toString() returned unexpected value(1): \"%s\".\n",
                        arg.toString());
                }
            }

            if ( arg != null )
            {
                if ( arg.toDBString().compareTo(
                        "(ColPredFormalArg 0 <test>)") != 0 )
                {
                    failures++;
                    outStream.printf(
                        "arg.toDBString() returned unexpected value(1): \"%s\".\n",
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
        
    } /* PredFormalArg::TestToStringMethods() */
    
} /* class PredFormalArg */
