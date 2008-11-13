/*
 * TimeStampFormalArg.java
 *
 * Created on February 11, 2007, 11:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Class TimeStampFormalArg
 *
 * Intance of this class are used for formal arguments which have been strongly
 * typed to time stamps.  
 *
 * @author mainzer
 */
public class TimeStampFormalArg extends FormalArgument
{
    
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**     
     *
     * subRange: Boolean flag indicating whether the formal argument can be 
     *      replaced by any time stamp, or only by some time stamp that lies 
     *      within the closed interval defined by the minVal and maxVal fields 
     *      discussed below
     *
     * minVal:  Timestamp field used to specify the minimum time stamp that 
     *      can be used to replace the formal argument if subRange is true.  
     *      If subRange is false, this field is ignored and should be set to
     *      null.
     *
     * maxVal:  Timestamp field used to specify the maximum time stamp that 
     *      can be used to replace the formal argument if subRange is 
     *      true.  If subRange is false, this field is ignored and should be
     *      set to null.
     */
    
    boolean subRange = false;
    TimeStamp minVal = null;
    TimeStamp maxVal = null;
    
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    
    /** 
     * TimeStampFormalArg()
     *
     * Constructors for time stamp typed formal arguments.  
     *
     * Four versions of this constructor -- one that takes a Database reference
     * as its only parameter, one that takes a database reference and a formal 
     * argument name as a parameters, and one that takes a Database reference,
     * a formal argument name, minimum value, and maximum value as parameters,
     * and finally one that take an instance of TimeStampFormalArg as its only
     * parameter and returs a copy.
     *
     *                                          JRM -- 2/12/07
     *
     * Changes:
     *
     *    - None.
     *      
     */

    public TimeStampFormalArg(Database db) 
        throws SystemErrorException
    { 
        
        super(db);
        
        this.fargType = fArgType.TIME_STAMP;
        
    } /* TimeStampFormalArg() -- one parameter */
    
    public TimeStampFormalArg(Database db,
                              String name) 
        throws SystemErrorException
    {
        super(db, name);
        
        this.fargType = fArgType.TIME_STAMP;
         
    } /* TimeStampFormalArg() -- two parameter s*/
    
    public TimeStampFormalArg(Database db, 
                              String name, 
                              TimeStamp minVal, 
                              TimeStamp maxVal)
          throws SystemErrorException
    {
        super(db, name);
        
        final String mName = "TimeStampFormalArg::TimeStampFormalArg(): "; 
        
        this.fargType = fArgType.TIME_STAMP;
        
        if ( ( minVal == null ) && ( maxVal == null ) )
        {
            this.subRange = false;
            this.minVal = null;
            this.maxVal = null;
        }
        else if ( ( minVal == null ) || ( maxVal == null ) )
        {
            throw new SystemErrorException(mName + "minVal xor maxVal is null");
        }
        else if ( ( ! ( minVal instanceof TimeStamp ) ) || 
                  ( ! ( maxVal instanceof TimeStamp ) ) )
        {
            /* I'm not sure this can happen, but check it anyway */
            
            throw new SystemErrorException(mName + 
                                           "minVal or maxVal not a TimeStamp");
        }
        else if ( minVal.getTPS() != maxVal.getTPS() )
        {
            throw new SystemErrorException(mName + "Inconsistant tick size");
        }
        else if ( ( minVal.getTPS() < TimeStamp.MIN_TPS ) || 
                  ( minVal.getTPS() > TimeStamp.MAX_TPS ) )
        {
            throw new SystemErrorException(mName + "Invalid tick size");
        }
        else if ( ( minVal.getTime() < 0 ) ||
                  ( maxVal.getTime() > Long.MAX_VALUE ) )
        {
            throw new SystemErrorException(mName + 
                                           "minVal or maxVal out of range");
        }
        else if ( minVal.getTime() >= maxVal.getTime() )
        {
            throw new SystemErrorException(mName +  "minVal >= maxVal");
        }
        else
        {
            this.subRange = true;
            this.minVal = new TimeStamp(minVal.getTPS(), minVal.getTime());
            this.maxVal = new TimeStamp(maxVal.getTPS(), maxVal.getTime());
        }
    } /* TimeStampFormalArg() -- three parameters */
     
    public TimeStampFormalArg(TimeStampFormalArg fArg) 
        throws SystemErrorException    
    {
        super(fArg);

        final String mName = "TimeStampFormalArg::TimeStampFormalArg(): ";  
        
        this.fargType = fArgType.TIME_STAMP;
        
        if ( ! ( fArg instanceof TimeStampFormalArg ) )
        {
            throw new SystemErrorException(mName + "fArg not a TimeStampFormalArg");
        }
        
        // copy over fields.
        this.setRange(fArg.getMinVal(), fArg.getMaxVal());

    } /* TimeStampFormalArg() -- make copy */
     
        
    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/
      
    /**
     * setRange()
     *
     * Set the range of legal values that this formal arguement can assume.
     *
     * If the new minVal and maxVal are null, set subRange to false.  
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
    public void setRange(TimeStamp minVal, TimeStamp maxVal)
        throws SystemErrorException
    {
        final String mName = "IntFormalArg::setRange(): ";
        
        if ( ( minVal == null ) && ( maxVal == null ) )
        {
            this.subRange = false;
            this.minVal = null;
            this.maxVal = null;
        }
        else if ( ( minVal == null ) || ( maxVal == null ) )
        {
            throw new SystemErrorException(mName + "minVal xor maxVal is null");
        }
        else if ( ( ! ( minVal instanceof TimeStamp ) ) || 
                  ( ! ( maxVal instanceof TimeStamp ) ) )
        {
            /* I'm not sure this can happen, but check it anyway */
            
            throw new SystemErrorException(mName + 
                                           "minVal or maxVal not a TimeStamp");
        }
        else if ( minVal.getTPS() != maxVal.getTPS() )
        {
            throw new SystemErrorException(mName + "Inconsistant tick size");
        }
        else if ( ( minVal.getTPS() < TimeStamp.MIN_TPS ) || 
                  ( minVal.getTPS() > TimeStamp.MAX_TPS ) )
        {
            throw new SystemErrorException(mName + "Invalid tick size");
        }
        else if ( ( minVal.getTime() < 0 ) ||
                  ( maxVal.getTime() > Long.MAX_VALUE ) )
        {
            throw new SystemErrorException(mName + 
                                           "minVal or maxVal out of range");
        }
        else if ( minVal.getTime() >= maxVal.getTime() )
        {
            throw new SystemErrorException(mName +  "minVal >= maxVal");
        }
        else
        {
            this.subRange = true;
            this.minVal = new TimeStamp(minVal.getTPS(), minVal.getTime());
            this.maxVal = new TimeStamp(maxVal.getTPS(), maxVal.getTime());
        }
        
        /* must notify listeners here */
        
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
    
    public TimeStamp getMinVal()
        throws SystemErrorException
    {
        if ( this.minVal == null )
        {
            return null;
        }
        else
        {
            return new TimeStamp(this.minVal);
        }
    }
    
    public TimeStamp getMaxVal()
        throws SystemErrorException
    {
        if ( this.minVal == null )
        {
            return null;
        }
        else
        {
            return new TimeStamp(this.maxVal);
        }
    }
     
        
    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/
    
    /**
     * constructArgWithSalvage()  Override of abstract method in FormalArgument
     *
     * Return an instance of TimeStampDataValue initialized from salvage if 
     * possible, and to the default for newly created instances of 
     * TimeStampDataValue otherwise.
     *
     * Changes:
     *
     *    - None.
     */
    
    DataValue constructArgWithSalvage(DataValue salvage)
        throws SystemErrorException
    {
        TimeStampDataValue retVal;
        
        if ( ( salvage == null ) ||
             ( salvage.getItsFargID() == DBIndex.INVALID_ID ) )
        {
            retVal = new TimeStampDataValue(this.db, this.id);
        }
        else if ( salvage instanceof IntDataValue )
        {
            retVal = new TimeStampDataValue(this.db, this.id,
                                       new TimeStamp(db.getTicks(), 
                                       ((IntDataValue)salvage).getItsValue()));
        }
        else if ( ( salvage instanceof TimeStampDataValue ) &&
                  ( this.db.IsValidTimeStamp(
                            ((TimeStampDataValue)salvage).getItsValue()) ) )
        {
            retVal = new TimeStampDataValue(this.db, this.id, 
                            ((TimeStampDataValue)salvage).getItsValue());
        }
        else
        {
            retVal = new TimeStampDataValue(this.db, this.id); 
        }
        
        return retVal;
        
    } /* TimeStampDataValue::constructArgWithSalvage(salvage) */
    
    
    /**
     * constructEmptyArg()  Override of abstract method in FormalArgument
     *
     * Return an instance of TimeStampDataValue initialized as appropriate for 
     * an argument that has not had any value assigned to it by the user.
     *
     * Changes:
     *
     *    - None.
     */
    
     public DataValue constructEmptyArg()
        throws SystemErrorException
     {
         
         return new TimeStampDataValue(this.db, this.id);
         
     } /* TimeStampFormalArg::constructEmptyArg() */


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
        
        if ( subRange )
        {
            return ("(TimeStampFormalArg " + getID() + " " + 
                    getFargName() + " " + subRange + " " + 
                    minVal.toDBString() + " " + maxVal.toDBString() +")");
        }
        else
        {
            return ("(TimeStampFormalArg " + getID() + " " + getFargName() + 
                    " " + subRange + " " + "null" + " " + "null" +")");
        }
        
    } /* TimeStampFormalArg::toDBString() */
    
   
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
        final String mName = "IntFormalArg::isValidValue(): ";
        
        if ( ! Database.IsValidTimeStamp(obj) )
        {
            return false;
        }
        
        /* If we get this far, obj must be a time stamp */
        
        TimeStamp t = (TimeStamp)obj;
        
        if ( subRange )
        {
            if ( ( t.getTPS() != minVal.getTPS() ) ||
                 ( t.getTPS() != maxVal.getTPS() ) )
            {
                throw new SystemErrorException(mName +  "TPS mismatch");
            }
            else if ( ( t.getTime() < minVal.getTime() ) ||
                      ( t.getTime() > maxVal.getTime() ) )
            {
                return false;
            }
        } 
        
        return true;
        
    } /* TimeStampFormalArg::isValidValue() */

    
    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/
    
    /*** TODO: Review test code ***/
    
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
        throws SystemErrorException
    {
        String testBanner =
            "Testing class TimeStampFormalArg accessors                       ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean threwSystemErrorExceptionInTest = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        TimeStampFormalArg arg = null;

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
            arg = new TimeStampFormalArg(new ODBCDatabase());
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
                            "new TimeStampFormalArg(db) returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("new TimeStampFormalArg(db) threw " +
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
                    outStream.print("AbstractFormalArgument.TestAccessors()" +
                            " threw a SystemErrorException.\n");
                }
            }
        }
        
        /* Now test accessors specific to TimeStampFormalArg. */
        
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
            if ( arg.getMinVal() != null )
            {
                failures++;
            
                if ( verbose )
                {
                    outStream.printf("Unexpected minVal(1): %s.\n",
                                       arg.getMinVal().toString());
                }
            }
        }
         
        if ( failures == 0 )
        {            
            if ( arg.getMaxVal() != null )
            {
                failures++;
            
                if ( verbose )
                {
                    outStream.printf("Unexpected maxVal(1): %s.\n",
                                       arg.getMaxVal().toString());
                }
            }
        }
        
        /* now set the subRange... */
        if ( failures == 0 )
        {
            TimeStamp newMinVal = new TimeStamp(60, 0);
            TimeStamp newMaxVal = new TimeStamp(60, 6000);
            threwSystemErrorException = false;
            threwSystemErrorExceptionInTest = false;

            try
            {
                arg.setRange(newMinVal, newMaxVal);
            }
            
            catch ( SystemErrorException e)
            {
                threwSystemErrorException = true;
            }
            
            try
            {
                if ( ( threwSystemErrorException ) ||
                      ( arg.getSubRange() != true ) ||
                      ( arg.minVal == newMinVal ) ||
                      ( arg.getMinVal().ne(newMinVal) ) ||
                      ( arg.maxVal == newMaxVal ) ||
                      ( arg.getMaxVal().ne(newMaxVal) ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        if ( threwSystemErrorException )
                        {
                            outStream.printf("\"arg.setRange(%s, %s)\" threw a " +
                                              "SystemErrorException.\n",
                                              newMinVal.toString(),
                                              newMaxVal.toString());
                        }

                        if ( arg.getSubRange() != true )
                        {
                            outStream.printf("Unexpected subRange(2): %b.\n",
                                              arg.getSubRange());
                        }
                        
                        if ( arg.minVal == newMinVal )
                        {
                            outStream.printf("arg.minVal == newMinVal.\n");
                        }
                        
                        if ( arg.getMinVal().ne(newMinVal) )
                        {
                            outStream.printf("Unexpected minVal(2): %s.\n",
                                              arg.getMinVal().toString());
                        }
                        
                        if ( arg.maxVal == newMaxVal )
                        {
                            outStream.printf("arg.maxVal == newMaxVal.\n");
                        }

                        if ( arg.getMaxVal().ne(newMaxVal) )
                        {
                            outStream.printf("Unexpected maxVal(2): %s.\n",
                                              arg.getMaxVal().toString());
                        }
                    }
                }
            }
            catch ( SystemErrorException e)
            {
                threwSystemErrorExceptionInTest = true;
            }
            
            if ( threwSystemErrorException )
            {
                outStream.printf("Threw system error in test(1).\n");
            }
        }

        /* ... and then set it back. */
        if ( failures == 0 )
        {
            try
            {
                arg.setRange(null, null);
            }
            
            catch ( SystemErrorException e)
            {
                threwSystemErrorException = true;
            }
            
            if ( ( threwSystemErrorException ) ||
                  ( arg.getSubRange() != false ) ||
                  ( arg.getMinVal() != null ) ||
                  ( arg.getMaxVal() != null ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "\"arg.setRange(null, null)\""
                                + " threw a SystemErrorException.\n");
                    }
                    
                    if ( arg.getSubRange() != false )
                    {
                        outStream.printf("Unexpected subRange(3): %b.\n",
                                          arg.getSubRange());
                    }
                    
                    if ( arg.getMinVal() != null )
                    {
                        outStream.printf("Unexpected minVal(3): %s.\n",
                                          arg.getMinVal().toString());
                    }
                    
                    if ( arg.getMaxVal() != null )
                    {
                        outStream.printf("Unexpected maxVal(3): %s.\n",
                                          arg.getMaxVal().toString());
                    }
                }
            }
        }
        
        /* Now attempt to set an invalid subrange with equal ticks... */
        if ( failures == 0 )
        {
            TimeStamp newMinVal = new TimeStamp(60, 0);
            TimeStamp newMaxVal = new TimeStamp(60, 0);
            threwSystemErrorException = false;
            
            try
            {
                arg.setRange(newMinVal, newMaxVal);
            }
            
            catch ( SystemErrorException e)
            {
                threwSystemErrorException = true;
            }
            
            if ( ( ! threwSystemErrorException ) ||
                  ( arg.getSubRange() != false ) ||
                  ( arg.getMinVal() != null ) ||
                  ( arg.getMaxVal() != null ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("\"arg.setRange(%s, %s)\""
                                + " didn't throw a SystemErrorException.\n",
                                newMinVal.toString(), newMaxVal.toString());
                    }
                    
                    if ( arg.getSubRange() != false )
                    {
                        outStream.printf("Unexpected subRange(4): %b.\n",
                                          arg.getSubRange());
                    }
                    
                    if ( arg.getMinVal() != null )
                    {
                        outStream.printf("Unexpected minVal(4): %s.\n",
                                          arg.getMinVal(), 
                                          arg.getMinVal().toString());
                    }
                    
                    if ( arg.getMaxVal() != null )
                    {
                        outStream.printf("Unexpected maxVal(4): %s.\n",
                                          arg.getMaxVal(),
                                          arg.getMaxVal().toString());
                    }
                }
            }
        }
        
        /* ...and attempt to set an invalid subrange with inconsistant tps */
        if ( failures == 0 )
        {
            TimeStamp newMinVal = new TimeStamp(30, 0);
            TimeStamp newMaxVal = new TimeStamp(60, 600);
            threwSystemErrorException = false;
            
            try
            {
                arg.setRange(newMinVal, newMaxVal);
            }
            
            catch ( SystemErrorException e)
            {
                threwSystemErrorException = true;
            }
            
            if ( ( ! threwSystemErrorException ) ||
                  ( arg.getSubRange() != false ) ||
                  ( arg.getMinVal() != null ) ||
                  ( arg.getMaxVal() != null ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("\"arg.setRange(%s, %s)\""
                                + " didn't throw a SystemErrorException.\n",
                                newMinVal.toString(), newMaxVal.toString());
                    }
                    
                    if ( arg.getSubRange() != false )
                    {
                        outStream.printf("Unexpected subRange(5): %b.\n",
                                          arg.getSubRange());
                    }
                    
                    if ( arg.getMinVal() != null )
                    {
                        outStream.printf("Unexpected minVal(5): %s.\n",
                                          arg.getMinVal(), 
                                          arg.getMinVal().toString());
                    }
                    
                    if ( arg.getMaxVal() != null )
                    {
                        outStream.printf("Unexpected maxVal(5): %s.\n",
                                          arg.getMaxVal(),
                                          arg.getMaxVal().toString());
                    }
                }
            }
        }
        
        /* ...and attempt to set an invalid subrange with one null time stamp */
        if ( failures == 0 )
        {
            TimeStamp newMinVal = null;
            TimeStamp newMaxVal = new TimeStamp(60, 600);
            threwSystemErrorException = false;
            
            try
            {
                arg.setRange(newMinVal, newMaxVal);
            }
            
            catch ( SystemErrorException e)
            {
                threwSystemErrorException = true;
            }
            
            if ( ( ! threwSystemErrorException ) ||
                  ( arg.getSubRange() != false ) ||
                  ( arg.getMinVal() != null ) ||
                  ( arg.getMaxVal() != null ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("\"arg.setRange(null, %s)\""
                                + " didn't throw a SystemErrorException.\n",
                                newMaxVal.toString());
                    }
                    
                    if ( arg.getSubRange() != false )
                    {
                        outStream.printf("Unexpected subRange(6): %b.\n",
                                          arg.getSubRange());
                    }
                    
                    if ( arg.getMinVal() != null )
                    {
                        outStream.printf("Unexpected minVal(6): %s.\n",
                                          arg.getMinVal(), 
                                          arg.getMinVal().toString());
                    }
                    
                    if ( arg.getMaxVal() != null )
                    {
                        outStream.printf("Unexpected maxVal(6): %s.\n",
                                          arg.getMaxVal(),
                                          arg.getMaxVal().toString());
                    }
                }
            }
        }
        
        /* TODO:
         *
         * Several other ways of setting invalid ranges are possible, but we 
         * access to TimeStamp private fields to set them up.  That will have
         * to wait until class TimeStamp is a bit more mature.
         */
        
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
        
    } /* TimeStampFormalArg::TestAccessors() */
    
    
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
            "Testing class TimeStampFormalArg itsVocabElement accessors       ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        TimeStampFormalArg arg = null;

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
            arg = new TimeStampFormalArg(new ODBCDatabase());
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
                            "new TimeStampFormalArg(db) returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("new TimeStampFormalArg(db) threw " +
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
        
    } /* TimeStampFormalArg::TestVEAccessors() */

    
    /**
     * TestClassTimeStampFormalArg()
     *
     * Main routine for tests of class TimeStampFormalArg.
     *
     *                                      JRM -- 3/12/07
     *
     * Changes:
     *
     *    - Non.
     */
    
    public static boolean TestClassTimeStampFormalArg(
            java.io.PrintStream outStream, 
            boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing class TimeStampFormalArg:\n");
        
        if ( ! Test1argConstructor(outStream, verbose) )
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
            outStream.printf(
                    "%d failures in tests for class TimeStampFormalArg.\n\n",
                     failures);
        }
        else
        {
            outStream.print("All tests passed for class TimeStampFormalArg.\n\n");
        }
        
        return pass;
        
    } /* Database::TestClassTimeStampFormalArg() */

    
    /**
     * Test1argConstructor()
     * 
     * Run a battery of tests on the one argument constructor for this 
     * class, and on the instance returned.
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public static boolean Test1argConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing 1 argument constructor for class TimeStampFormalArg      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        TimeStampFormalArg arg = null;

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
            arg = new TimeStampFormalArg(new ODBCDatabase());
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
                            "new TimeStampFormalArg(db) returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf("new TimeStampFormalArg(db) threw " +
                                      "system error exception: \"%s\"\n",
                                      systemErrorExceptionString);
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( arg.getFargName().compareTo("<arg>") != 0 )
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
            if ( arg.getMinVal() != null )
            {
                failures++;
            
                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of minVal: %s.\n",
                                       arg.getMinVal().toString());
                }
            }
        }
         
        if ( failures == 0 )
        {            
            if ( arg.getMaxVal() != null )
            {
                failures++;
            
                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of maxVal: %s.\n",
                                       arg.getMaxVal().toString());
                }
            }
        }
        
        /* verify that the constructor fails when passed an invalid db */
        if ( failures == 0 )
        {
            arg = null;
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                arg = new TimeStampFormalArg((Database)null);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;

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
                                "new TimeStampFormalArg(null) returned.\n");
                   }
                    
                    if ( arg != null )
                    {
                        outStream.print("new TimeStampFormalArg(null) " +
                                         "returned non-null.\n");
                    }

                    if ( !threwSystemErrorException )
                    {
                        outStream.print(
                                "new TimeStampFormalArg(null) didn't " +
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
        
    } /* TimeStampFormalArg::Test0ArgConstrucor() */
    
    /**
     * Test2ArgConstructor()
     * 
     * Run a battery of tests on the two argument Constructor for this 
     * class, and on the instance returned.
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
            "Testing 2 argument constructor for class TimeStampFormalArg      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        TimeStampFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        try
        {
            arg = new TimeStampFormalArg(new ODBCDatabase(), "<valid>");
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
                    outStream.print("new TimeStampFormalArg(db, " + 
                                     "\"<valid>\")\" returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.print(
                            "new TimeStampFormalArg(db, \"<valid>\")\" " +
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
            if ( arg.getMinVal() != null )
            {
                failures++;
            
                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of minVal: %s.\n",
                                       arg.getMinVal().toString());
                }
            }
        }
         
        if ( failures == 0 )
        {            
            if ( arg.getMaxVal() != null )
            {
                failures++;
            
                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of maxVal: %s.\n",
                                       arg.getMaxVal().toString());
                }
            }
        }
        
        /* verify that the constructor fails when passed an invalid db. */
        arg = null;
        threwSystemErrorException = false;
        
        try
        {
            arg = new TimeStampFormalArg(null, "<valid>");
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
                        "new TimeStampFormalArg(db, \"<valid>\")\" != null.\n");
                }
                
                if ( ! threwSystemErrorException )
                {
                    outStream.print("new TimeStampFormalArg(null, \"<valid>\")"
                                     + " didn't throw a SystemErrorException.\n");
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
            arg = new TimeStampFormalArg(new ODBCDatabase(), "<<invalid>>");
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
                        "new TimeStampFormalArg(db, \"<<invalid>>\")\" != null.\n");
                }
                
                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "\"new TimeStampFormalArg(db, \"<<invalid>>\")\"" +
                            " didn't throw a SystemErrorException.\n");
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
        
    } /* TimeStampFormalArg::Test2ArgConstructor() */

    
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
        throws SystemErrorException
    {
        String testBanner =
            "Testing 3 argument constructor for class TimeStampFormalArg      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean threwSystemErrorException = false;
        boolean threwSystemErrorExceptionInTest = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        TimeStampFormalArg arg = null;
        TimeStamp minVal = new TimeStamp(60, 0);
        TimeStamp maxVal = new TimeStamp(60, 600);

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* verify that the constructor works when passed a valid name and a 
         * valid minVal, maxVal pair.
         */
        try
        {
            arg = new TimeStampFormalArg(new ODBCDatabase(), "<valid>", 
                                         minVal, maxVal);
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
                    outStream.printf(
                        "\"new TimeStampFormalArg(db, \"<valid>\", %s, %s)\" " +
                        "returned null.\n", 
                         minVal.toString(), maxVal.toString());
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf(
                        "\"new TimeStampFormalArg(db, \"<valid>\", %s, %s)\" " +
                        "threw a SystemErrorException.\n",
                        minVal.toString(), maxVal.toString());
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
                    outStream.printf(
                            "Unexpected initial fArgName(1): \"%s\".\n",
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
                    outStream.printf(
                            "Unexpected initial value of hidden(1): %b.\n",
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
                    outStream.printf(
                            "itsVocabElement not initialzed to null(1).\n");
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
                    outStream.printf(
                            "Unexpected initial value of subRange(1): %b.\n",
                            arg.getSubRange());
                }
            }
        }
         
        if ( failures == 0 )
        {   
            threwSystemErrorExceptionInTest = false;
            
            try
            {
                if ( arg.getMinVal().ne(minVal) )
                {
                    failures++;
            
                    if ( verbose )
                    {
                        outStream.printf(
                                "Unexpected initial value of minVal(1): %s.\n",
                                arg.getMinVal().toString());
                    }
                }
            }
            
            catch (SystemErrorException e)
            {
                threwSystemErrorExceptionInTest = true;
            }
            
            if ( threwSystemErrorExceptionInTest )
            {
                outStream.print("Threw system error in test(1).\n");
            }
        }
         
        if ( failures == 0 )
        {            
            threwSystemErrorExceptionInTest = false;
            
            try
            {
                if ( arg.getMaxVal().ne(maxVal) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "Unexpected initial value of maxVal(1): %s.\n",
                                arg.getMaxVal().toString());
                    }
                }
            }
            
            catch (SystemErrorException e)
            {
                threwSystemErrorExceptionInTest = true;
            }
            
            if ( threwSystemErrorExceptionInTest )
            {
                outStream.print("Threw system error in test(2).\n");
            }
        }
        
        /* verify that the constructor works when passed a valid name and null 
         * for minVal & maxVal.
         */
        try
        {
            arg = new TimeStampFormalArg(new ODBCDatabase(), "<valid>",
                                         null, null);
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
                        "new TimeStampFormalArg(db, \"<valid>\", null, null) " +
                        "returned null.\n");
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.print(
                        "new TimeStampFormalArg(db, \"<valid>\", null, null) " +
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
                    outStream.printf(
                            "Unexpected initial fArgName(2): \"%s\".\n",
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
                    outStream.printf(
                            "Unexpected initial value of hidden(2): %b.\n",
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
                    outStream.printf(
                            "itsVocabElement not initialzed to null(2).\n");
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
                    outStream.printf(
                            "Unexpected initial value of subRange(2): %b.\n",
                            arg.getSubRange());
                }
            }
        }
         
        if ( failures == 0 )
        {   
            threwSystemErrorExceptionInTest = false;
            
            if ( arg.getMinVal() != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of minVal(2): %s.\n",
                            arg.getMinVal().toString());
                }
            }
        }
         
        if ( failures == 0 )
        {            
            if ( arg.getMaxVal() != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of maxVal(2): %s.\n",
                            arg.getMaxVal().toString());
                }
            }
        }
         
        
        /* verify that the constructor fails when passed an invalid db. */
        arg = null;
        threwSystemErrorException = false;
        
        try
        {
            arg = new TimeStampFormalArg(null, "<<invalid>>", 
                                         minVal, maxVal);
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
                    outStream.printf(
                        "new TimeStampFormalArg(null, \"<valid>\", %s, %s)" 
                        + " != null.\n", minVal.toString(), maxVal.toString());
                }
                
                if ( ! threwSystemErrorException )
                {
                    outStream.printf(
                        "new TimeStampFormalArg(db, \"<valid>\", %s, %s)"
                        + " didn't throw a SystemErrorException.\n",
                        minVal.toString(), maxVal.toString());
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
            arg = new TimeStampFormalArg(new ODBCDatabase(), "<<invalid>>", 
                                         minVal, maxVal);
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
                    outStream.printf(
                        "new TimeStampFormalArg(db, \"<<invalid>>\", %s, %s)" 
                        + " != null.\n", minVal.toString(), maxVal.toString());
                }
                
                if ( ! threwSystemErrorException )
                {
                    outStream.printf(
                        "new TimeStampFormalArg(db, \"<<invalid>>\", %s, %s)"
                        + " didn't throw a SystemErrorException.\n",
                        minVal.toString(), maxVal.toString());
                }
            }
        }
        
        
        /* verify that the constructor fails when passed an invalid 
         * minVal, maxVal pair.   Several ways of doing this:
         */
        
        /*** equal minVal and maxVal ***/
        arg = null;
        threwSystemErrorException = false;
        minVal = new TimeStamp(60, 0);
        maxVal = new TimeStamp(60, 0);
        
        try
        {
            arg = new TimeStampFormalArg(new ODBCDatabase(), "<valid>", 
                                         minVal, maxVal);
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
                    outStream.printf(
                       "new TimeStampFormalArg(db, \"%s\", %s, %s) != null.\n",
                        "<valid>", minVal.toString(), maxVal.toString());
                }
                
                if ( ! threwSystemErrorException )
                {
                    outStream.printf(
                        "new TimeStampFormalArg(db, %s, %s, %s) " +
                        "didn't throw a SystemErrorException.\n",
                        "<valid>", minVal.toString(), maxVal.toString());
                }
            }
        }
        
        /*** minVal.tps != maxVal.tps ***/
        arg = null;
        threwSystemErrorException = false;
        minVal = new TimeStamp(60, 0);
        maxVal = new TimeStamp(10, 0);
        
        try
        {
            arg = new TimeStampFormalArg(new ODBCDatabase(), "<valid>", 
                                        minVal, maxVal);
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
                    outStream.printf(
                       "new TimeStampFormalArg(db, \"%s\", %s, %s) != null.\n",
                        "<valid>", minVal.toString(), maxVal.toString());
                }
                
                if ( ! threwSystemErrorException )
                {
                    outStream.printf(
                        "new TimeStampFormalArg(db, %s, %s, %s) " +
                        "didn't throw a SystemErrorException.\n",
                        "<valid>", minVal.toString(), maxVal.toString());
                }
            }
        }
        
        /* TODO:
         *
         * There are several other ways in which the time stamps passed to the 
         * constructor can be invalid.  However, setting them up requires 
         * corrupting data in instance of TimeStamp.  This will have to wait 
         * until TimeStamp is a bit mroe mature.
         */
        
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
        
    } /* TimeStampFormalArg::Test4ArgConstructor() */

    
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
        throws SystemErrorException
    {
        String testBanner =
            "Testing copy constructor for class TimeStampFormalArg            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        TimeStampFormalArg arg = null;
        TimeStampFormalArg copyArg = null;
        TimeStampFormalArg munged = null;
        TimeStamp minVal = new TimeStamp(60, 0);
        TimeStamp maxVal = new TimeStamp(60, 600);

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        /* first set up the instance of TimeStampFormalArg to be copied: */
        threwSystemErrorException = false;
        
        try
        {
            arg = new TimeStampFormalArg(new ODBCDatabase(), "<copy_this>", minVal, maxVal);
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
                    outStream.printf(
                        "new TimeStampFormalArg(db, \"<copy_this>\", %s, %s) " +
                        "returned null.\n", 
                        minVal.toString(), maxVal.toString());
                }
                
                if ( threwSystemErrorException )
                {
                    outStream.printf(
                        "new TimeStampFormalArg(db, \"<copy_this>\", %s, %s) " +
                        "threw a SystemErrorException.\n",
                        minVal.toString(), maxVal.toString());
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
                copyArg = new TimeStampFormalArg(arg);
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
                            "\"new TimeStampFormalArg(arg)\" returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("\"new TimeStampFormalArg(arg)\" " +
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
            if ( arg.getMinVal().ne(copyArg.getMinVal()) )
            {
                failures++;
                        
                if ( verbose )
                {
                    outStream.printf("arg.minVal = %s != " +
                            "copyArg.minVal = %s.\n", arg.minVal.toString(),
                            copyArg.minVal.toString());
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( arg.getMaxVal().ne(copyArg.getMaxVal()) )
            {
                failures++;
                        
                if ( verbose )
                {
                    outStream.printf("arg.maxVal = %s != " +
                            "copyArg.maxVal = %s.\n", arg.maxVal.toString(),
                            copyArg.maxVal.toString());
                }
            }
        }

        /* now verify that we fail when we should */
        
        /* first ensure that the copy constructor fails when passed null */
        if ( failures == 0 )
        {
            munged = copyArg; /* save the copy for later */
            copyArg = null;
            threwSystemErrorException = false;

            try
            {
                copyArg = new TimeStampFormalArg(copyArg);
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
                            "\"new TimeStampFormalArg(null)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("\"new TimeStampFormalArg(null)\" " +
                                       "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* now corrupt the fargName field of an instance of TimeStampFormalArg, 
         * and verify that this causes a copy to fail.  Start with corrupting
         * the fargName field.
         */
        if ( failures == 0 )
        {
            copyArg = null;
            threwSystemErrorException = false;
            
            munged.fargName = "<an invalid name>";

            try
            {
                copyArg = new TimeStampFormalArg(munged);
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
                            "\"new TimeStampFormalArg(munged1)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "\"new TimeStampFormalArg(munged1)\" " +
                                "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* now corrupt the minVal & maxVal fields of an instance of 
         * TimeStampFormalArg, and verify that this causes a copy to fail.
         * Lots of ways of doing this.
         */
        /*** minVal == maxVal ***/
        if ( failures == 0 )
        {
            copyArg = null;
            threwSystemErrorException = false;
            
            munged.fargName = "<a_valid_name>";
            munged.maxVal = new TimeStamp(munged.minVal);

            try
            {
                copyArg = new TimeStampFormalArg(munged);
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
                            "\"new TimeStampFormalArg(munged2)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("\"new TimeStampFormalArg(munged2)\" " +
                                     "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /*** minVal.tps != maxVal.tps ***/
        if ( failures == 0 )
        {
            copyArg = null;
            threwSystemErrorException = false;
            
            munged.fargName = "<a_valid_name>";
            munged.minVal = new TimeStamp(30, 0);
            munged.maxVal = new TimeStamp(60, 6000);

            try
            {
                copyArg = new TimeStampFormalArg(munged);
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
                            "\"new TimeStampFormalArg(munged3)\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("\"new TimeStampFormalArg(munged3)\" " +
                                     "didn't throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* TODO:
         *
         * There are may other ways in which invalid input should cause the 
         * TimeStampFormalArgument constructor to fail.  However, they require
         * corrupting internal fields in instances of TimeStamp.  That will have
         * to wait until that code is more mature.
         */
        
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
        
    } /* TimeStampFormalArg::TestCopyConstructor() */
    
    
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
            /* test  2 -- should return false */ new Long(0),
            /* test  3 -- should return false */ "A Valid Nominal",
            /* test  4 -- should return false */ " A Valid Quote String ",
            /* test  5 -- should return false */ new TimeStamp(60),
            /* test  6 -- should return true  */ new TimeStamp(60, 300),
            /* test  7 -- should return false */ "an invalid text \b string",
            /* test  8 -- should return false */ new Float(0.0),
            /* test  9 -- should return false */ new Integer(0),
            /* test 10 -- should return false */ " An Invalid Nominal \b ",
            /* test 11 -- should return false */ " An Invalid \t Quote string ",
            /* test 12 -- should return false */ new TimeStamp(60),
            /* test 13 -- should return false */ new TimeStamp(60, 99),
            /* test 14 -- should return true  */ new TimeStamp(60, 100),
            /* test 15 -- should return true  */ new TimeStamp(60, 101),
            /* test 16 -- should return true  */ new TimeStamp(60, 300),
            /* test 17 -- should return true  */ new TimeStamp(60, 600),
            /* test 18 -- should return false */ new TimeStamp(60, 1001),
       };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return false */ " A Valid Text String ",
            /* test  1 -- should return false */ "new Double(0.0)",
            /* test  2 -- should return false */ "new Long(0)",
            /* test  3 -- should return false */ "A Valid Nominal",
            /* test  4 -- should return false */ " A Valid Quote String ",
            /* test  5 -- should return false */ "new TimeStamp(60)",
            /* test  6 -- should return true  */ "new TimeStamp(60, 300)",
            /* test  7 -- should return false */ "an invalid text \b string",
            /* test  8 -- should return false */ "new Float(0.0)",
            /* test  9 -- should return false */ "new Integer(0)",
            /* test 10 -- should return false */ " An Invalid \t Nominal \b ",
            /* test 11 -- should return false */ " An Invalid \t Quote string ",
            /* test 12 -- should return false */ "new TimeStamp(60)",
            /* test 13 -- should return false */ "new TimeStamp(60, 99)",
            /* test 14 -- should return true  */ "new TimeStamp(60, 100)",
            /* test 15 -- should return true  */ "new TimeStamp(60, 101)",
            /* test 16 -- should return true  */ "new TimeStamp(60, 300)",
            /* test 17 -- should return true  */ "new TimeStamp(60, 600)",
            /* test 18 -- should return false */ "new TimeStamp(60, 1001)",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ false,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ true,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ false,
            /* test 11 should return */ false,
            /* test 12 should return */ false,
            /* test 13 should return */ false,
            /* test 14 should return */ true,
            /* test 15 should return */ true,
            /* test 16 should return */ true,
            /* test 17 should return */ true,
            /* test 18 should return */ false,
        };
        TimeStamp minVal = new TimeStamp(60, 100);
        TimeStamp maxVal = new TimeStamp(60, 1000);
        TimeStampFormalArg arg = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        try
        {
            arg = new TimeStampFormalArg(new ODBCDatabase(), "<arg>", 
                                         minVal, maxVal);
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
                outStream.print("\"new TimeStampFormalArg()\" returned null.\n");
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
        
        /* Verify that isValidValue() throws a system error when passed 
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
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("failed -- unexpected return.\n");
                    }
                    
                    if ( result )
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
        
        /* Verify that isValidValue() throws a system error when passed 
         * a time stamp whose tps doesn't agree with that of the instance of
         * TimeStampFormalArg.
         */

        if ( arg != null )
        {
            TimeStamp test = new TimeStamp(30, 200);
            
            if ( verbose )
            {
                outStream.printf(
                        "test %d: arg.isValidValue(%s) --> exception: ",
                        testNum, test.toString());
            }

            methodReturned = false;
            threwSystemErrorException = false;
            result = false;

            try
            {
                result = arg.isValidValue(test);
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
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("failed -- unexpected exception.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("failed -- unexpected return.\n");
                    }
                    
                    if ( result )
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
        
    } /* TimeStampFormalArg::TestIsValidValue() */
    
    
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
        TimeStamp minVal = new TimeStamp(10, 10);
        TimeStamp maxVal = new TimeStamp(10, 100);
        TimeStampFormalArg arg = null;

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
                arg = new TimeStampFormalArg(new ODBCDatabase(), "<test>", 
                                             minVal, maxVal);
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
                        outStream.printf(
                            "new TimeStampFormalArg(db, \"<test>\", %s, %s) " +
                            "returned null.\n",
                             minVal.toString(), maxVal.toString());
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print(
                                "new TimeStampFormalArg(db, \"<test>\", 0.1, 10.1)" +
                                " threw a SystemErrorException.\n");
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
            if ( arg.toDBString().compareTo("(TimeStampFormalArg 0 <test> " +
                    "true (10,00:00:01:000) (10,00:00:10:000))") != 0 )
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
        
    } /* TimeStampFormalArg::TestToStringMethods() */
    
} /* class TimeStampFormalArg */
