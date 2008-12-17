/*
 * DBIndex.java
 *
 * Created on April 22, 2007, 9:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

import au.com.nicta.openshapa.util.OpenHashtable;
import java.util.Vector;

/**
 * class DBIndex
 *
 * A single instance of DBIndex is used to construct and maintain an index 
 * of all DBElements in the host database.  This instance also assigns 
 * IDs, and handles the matter of replacing one representation of a DBElement
 * with the next.
 *
 *                                          JRM -- 4/22/07
 *
 * @author mainzer
 */
public class DBIndex
{
    
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**     
     *
     * INVALID_ID: Constant specifying the value assigned to an ID when it has
     *      not been set.
     *
     * nextID:  Long field used to store the next ID to be assigned to 
     *      a DBElement that is added to the index.
     *
     * db:  Reference to the instance of Database of which this index is part.
     *
     * index: Hashtable containg references to all instances of DBElement that
     *      reside in db.
     */
    
     /** Value assigned to an ID that has not been specified */
     static final long INVALID_ID = 0;
     
     /** The next ID to be assigned */
     private long nextID = 1;
     
     /** Reference to the Database of which this instance is part */
     protected Database db = null;
     
     /** Index of all instances of DBelement in the Database */
     protected OpenHashtable<Long, DBElement> index =
             new OpenHashtable<Long, DBElement>();
    
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
     
    /** 
     * DBIndex()
     *
     * Constructor for DBIndex.
     *
     * Construct an instance of DBIndex.  All that happens is that we store
     * the db parameter.
     *
     * Changes:
     *
     *    - None.
     */
     
    public DBIndex(Database db)
         throws SystemErrorException
    {
        super();
         
        final String mName = "DBIndex::DBIndex(db): ";
        
        if ( ( db == null ) || 
             ( ! ( db instanceof Database ) ) )
        {
            throw new SystemErrorException(mName + "Bad db param");
        }
                      
        this.db = db;
         
        return;
        
    } /* DBIndex::DBIndex(db) */
     
     
    /*************************************************************************/
    /***************************** Overrides: ********************************/
    /*************************************************************************/
    
    /**
     * toString() -- overrride 
     * 
     * Returns a String representation of the contents of the index.<br>
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
    public String toString() 
    {
        boolean first = true;
        String s;
        DBElement dbe;
        java.util.Enumeration<DBElement> entries;
        
        s = "((DBIndex) (index_contents: (";
        entries = index.elements();
        while ( entries.hasMoreElements() )
        {
            if ( first )
            {
                first = false;
            }
            else
            {
                s += ", ";
            }
            dbe = entries.nextElement();
            s += dbe.toString();
        }
        s += ")))";
        
        return s;
        
    } /* DBIndex::toDBString() */
     
     
    /*************************************************************************/
    /****************************** Methods: *********************************/
    /*************************************************************************/
     
    /**
     * addElement()
     *
     * Assign the provided DBElement an id, and insert it into the index.
     *
     *                                                 JRM -- 4/23/07
     *
     * Changes:
     *
     *   - None.
     */
     
    public void addElement(DBElement dbe)
       throws SystemErrorException
    {
        final String mName = "DBIndex::addElement(dbe): ";
          
        if ( ( dbe == null ) ||
             ( ! ( dbe instanceof DBElement ) ) )
        {
            throw new SystemErrorException(mName + "Bad dbe param");
        }
        else if ( dbe.getDB() != db )
        {
            throw new SystemErrorException(mName + "dbe.getDB() != db");
        }
        else if ( dbe.getID() != INVALID_ID )
        {
            throw new SystemErrorException(mName +
                                           "dbe.getID() != INVALID_ID");
        }
        else if ( index.containsReference(dbe) )
        {
            throw new SystemErrorException(mName + "dbe alread in index?!?");
        }
        else if ( index.containsKey(this.nextID) )
        {
            throw new SystemErrorException(mName + "nextID already in use?!?");
        }
         
        dbe.setID(this.nextID);
        index.put(this.nextID, dbe);
        this.nextID++;
          
        if ( this.nextID == INVALID_ID )
        {
            throw new SystemErrorException(mName + "nextID wrapped around!?!");
        }
          
        return;
       
    } /* DBIndex::addElement(dbe) */
     
    /**
     * getElement()
     *
     * Get the instance of DBElement corresponding with the supplied id.
     *
     *                                                 JRM -- 4/23/07
     *
     * Changes:
     *
     *   - None.
     */
     
    public DBElement getElement(long targetID)
       throws SystemErrorException
    {
        final String mName = "DBIndex::getElement(targetID): ";
        DBElement dbe = null;
          
        if ( targetID == INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
         
        dbe = index.get(targetID);
         
        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "target doesn't exist.");
        }
         
        return dbe;
         
    } /* DBIndex::getElement(targetID) */
    
    /**
     * inIndex(targetID)
     *
     * Return true if the index contains an entry matching the provided id.
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean inIndex(long targetID)
       throws SystemErrorException
    {
        final String mName = "DBIndex::inIndex(targetID): ";
        boolean inIndex = false;
        
        if ( targetID == INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
        else if ( index.containsKey(targetID) )
        {
            inIndex = true;
        }
        
        return inIndex;
        
    } /* DBIndex::inIndex(targetID) */
     
    /**
     * removeElement()
     *
     * Remove the instance of DBElement with the specified id from the index.
     *
     *                                                 JRM -- 4/23/07
     *
     * Changes:
     *
     *   - None.
     */
     
    void removeElement(long targetID)
       throws SystemErrorException
    {
        final String mName = "DBIndex::removeElement(targetID): ";
          
        if ( targetID == INVALID_ID )
        {
            throw new SystemErrorException(mName + "targetID == INVALID_ID");
        }
        else if ( ! index.containsKey(targetID) )
        {
            throw new SystemErrorException(mName + "targetID not in index.");
        }
        else if ( index.remove(targetID) == null )
        {
            throw new SystemErrorException(mName + "index.remove() failed.");
        }
         
        return;
     
    } /* DBIndex::removeElement(targetID) */
     
    /**
     * replaceElement()
     *
     * Search the index for an instance of DBElement with the same id as that 
     * of the supplied instance.  Remove the instance from the index, and 
     * replace it with the supplied instance.
     *
     *                                                 JRM -- 4/23/07
     *
     * Changes:
     *
     *   - None.
     */

    void replaceElement(DBElement dbe)
       throws SystemErrorException
    {
        final String mName = "DBIndex::replaceElement(dbe): ";
        DBElement old_dbe = null;
          
        if ( ( dbe == null ) ||
             ( ! ( dbe instanceof DBElement ) ) )
        {
            throw new SystemErrorException(mName + "Bad dbe param");
        }
        else if ( dbe.getDB() != db )
        {
            throw new SystemErrorException(mName + "dbe.getDB() != db");
        }
        else if ( dbe.getID() == INVALID_ID )
        {
            throw new SystemErrorException(mName +
                                           "dbe.getID() == INVALID_ID");
        }
         
        old_dbe = index.get(dbe.getID());
         
        if ( old_dbe == null )
        {
            throw new SystemErrorException(mName + 
                                           "can't replace -- not in index.");
        }
        else if ( dbe.getClass() != old_dbe.getClass() )
        {
            throw new SystemErrorException(mName + "type mis-match.");
        }

        if ( index.remove(dbe.getID()) == null )
        {
            throw new SystemErrorException(mName + "remove failed.");
        }
         
        index.put(dbe.getID(), dbe);
         
        return;
         
    } /* replaceElement(dbe) */
    
    /**
     * toDBString()
     * 
     * Returns a String representation of the contents of the index.<br>
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
    public String toDBString() 
    {
        boolean first = true;
        String s;
        DBElement dbe;
        java.util.Enumeration<DBElement> entries;
        
        try
        {
            s = "((DBIndex) (nextID: ";
            s += nextID;
            s += ") (index_size: ";
            s += index.size();
            s += ") (index_contents: (";
            entries = index.elements();
            while ( entries.hasMoreElements() )
            {
                if ( first )
                {
                    first = false;
                }
                else
                {
                    s += ", ";
                }
                dbe = entries.nextElement();
                s += dbe.toDBString();
            }
            s += ")))";
        }
        
        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }
       
        return s;
        
    } /* DBIndex::toDBString() */

    
    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/
    
    /**
     * GetIndexSize()
     *
     * Test function that returns the number of entries in the supplied
     * instance of DBIndex.
     *                                          JRM - 5/21/07
     * 
     * Changes:
     *
     *    - None.
     */
    
    public static int GetIndexSize(DBIndex idx)
    {
        return idx.index.size();
        
    } /* DBIndex::GetIndexSize() */

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
            "Testing 1 argument constructor for class DBIndex                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        DBIndex idx = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            db = null;
            idx = null;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                idx = new DBIndex(db);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! methodReturned ) ||
                 ( db == null ) ||
                 ( idx == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "new DBIndex(db) failed to return.\n");
                    }
                    
                    if ( db == null )
                    {
                        outStream.print(
                                "new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( idx == null )
                    {
                        outStream.print(
                                "new DBIndex(db) returned null.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DBIndex(db) threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {            
            if ( idx.db != db )
            {
                failures++;
            
                if ( verbose )
                {
                    outStream.print("Unexpected initial idx.db != db.\n");
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( idx.nextID != 1 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of nextID: %l.\n",
                                       idx.nextID);
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( ( idx.index == null ) ||
                 ( ! idx.index.isEmpty() ) )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("index null or non-empty on creation.\n");
                }
            }
        }
        
        
        /* Verify that the constructor fails when passed an invalid db. */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            idx = null;
            systemErrorExceptionString = null;
                    
            try
            {
                idx = new DBIndex((Database)null);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( idx != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("new DBIndex(null) returned.\n");
                    }
                    
                    if ( idx != null )
                    {
                        outStream.print(
                             "new DBIndex(null) returned non-null.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("new DBIndex(null) failed to " +
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
        
    } /* DBIndex::Test1ArgConstructor() */
    
   
    /**
     * TestClassDBIndex()
     *
     * Main routine for tests of class DBIndex.
     *
     *                                  JRM -- 4/24/07
     *
     * Changes:
     *
     *    - Non.
     */
    
    public static boolean TestClassDBIndex(java.io.PrintStream outStream,
                                           boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing class DBIndex:\n");
        
        if ( ! Test1ArgConstructor(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestIndexManagement(outStream, verbose) )
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
            outStream.printf("%d failures in tests for class DBIndex.\n\n",
                             failures);
        }
        else
        {
            outStream.print("All tests passed for class DBIndex.\n\n");
        }
        
        return pass;
        
    } /* Database::TestClassDBIndex() */
    
   
    /**
     * TestIndexManagement()
     *
     * Run a battery of tests on index management.
     *
     *                                  JRM -- 4/24/07
     *
     * Changes:
     *
     *    - Non.
     */
    
    public static boolean TestIndexManagement(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing Index management for class DBIndex                       ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean contentsVerified;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        Database another_db = null;
        DBIndex idx = null;
        DBElement dbe = null;
        UnTypedFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta = null;
        UnTypedFormalArg echo = null;
        UnTypedFormalArg foxtrot = null;
        UnTypedFormalArg golf = null;
        UnTypedFormalArg hotel = null;
        UnTypedFormalArg india = null;
        UnTypedFormalArg juno = null;
        UnTypedFormalArg kilo = null;
        UnTypedFormalArg lima = null;
        FloatFormalArg mike = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* Start by allocating the index and database that we will be 
         * using in the test.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            db = null;
            idx = null;
            systemErrorExceptionString = null;
                    
            try
            {
                db = new ODBCDatabase();
                another_db = new ODBCDatabase();
                idx = new DBIndex(db);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! methodReturned ) ||
                 ( db == null ) ||
                 ( another_db == null ) ||
                 ( idx == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "new DBIndex(db) failed to return.\n");
                    }
                    
                    if ( ( db == null ) || ( another_db == null ) )
                    {
                        outStream.print(
                                "new ODBCDatabase() returned null.\n");
                    }
                    
                    if ( idx == null )
                    {
                        outStream.print(
                                "new DBIndex(db) returned null.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new DBIndex(db) threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Allocate a bunch of instances of UnTypedFormalArg.  These are just
         * convenient DBElements for use in testing.
         */
        if ( failures == 0 )
        {
            alpha   = null;
            bravo   = null;
            charlie = null;
            delta   = null;
            echo    = null;
            foxtrot = null;
            hotel   = null;
            india   = null;
            juno    = null;
            kilo    = null;
            lima    = null;
            mike    = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                alpha   = new UnTypedFormalArg(db, "<alpha>");
                bravo   = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                delta   = new UnTypedFormalArg(db, "<delta>");
                echo    = new UnTypedFormalArg(db, "<echo>");
                foxtrot = new UnTypedFormalArg(db, "<foxtrot>");
                hotel   = new UnTypedFormalArg(db, "<hotel>");
                india   = new UnTypedFormalArg(db, "<india>");
                juno    = new UnTypedFormalArg(db, "<juno>");
                kilo    = new UnTypedFormalArg(another_db, "<kilo>");
                lima    = new UnTypedFormalArg(db, "<lima>");
                mike    = new FloatFormalArg(db, "<mike>");
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! methodReturned ) ||
                 ( alpha == null ) || ( bravo == null ) || 
                 ( charlie == null ) || ( delta == null ) ||
                 ( echo == null ) || ( foxtrot == null ) ||
                 ( hotel == null ) || ( india == null ) ||  
                 ( juno == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "DBElement allocations failed to complete.\n");
                    }
                    
                    if ( ( alpha == null ) || ( bravo == null ) || 
                         ( charlie == null ) || ( delta == null ) ||
                         ( echo == null ) || ( foxtrot == null ) ||
                         ( hotel == null ) ) 
                    {
                        outStream.print(
                                "one or more DBElement allocations failed.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("DBElement allocations threw an " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Now try to add several DBElements to the index, and verify that
         * they are there.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                idx.addElement(alpha);
                idx.addElement(bravo);
                idx.addElement(charlie);
                idx.addElement(delta);
                idx.addElement(echo);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("Calls to idx.addElement() failed " +
                                        "to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("idx.addElement() threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {1, 2, 3, 4, 5};
            DBElement values[] = {alpha, bravo, charlie, delta, echo};

            if ( ! VerifyIndexContents(5, keys, values, idx, outStream, 
                                       verbose, 1) )
            {
                failures++;
            }
        }
        
        /* Now delete several entries from the index, and see if we get the
         * expected results.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                idx.removeElement(3);
                idx.removeElement(1);
                idx.removeElement(5);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("Calls to idx.removeElement() failed " +
                                        "to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("idx.removeElement() threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4};
            DBElement values[] = {bravo, delta};

            if ( ! VerifyIndexContents(2, keys, values, idx, outStream, 
                                       verbose, 2) )
            {
                failures++;
            }
        }
        
        /* Now add a couple of entries and replace a couple of entries. 
         * In passing verify that getElement() and inIndex() work as they
         * should with valid data*/
        if ( failures == 0 )
        {
            boolean inIndex1 = true;
            boolean inIndex2 = false;
            
            dbe = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {  
                dbe = idx.getElement(2); /* should be bravo at this point */
                inIndex1 = idx.inIndex(6); /* should be false at present */
                idx.addElement(foxtrot);
                india.setID(2);
                idx.replaceElement(india);
                juno.setID(4);
                idx.replaceElement(juno);
                idx.addElement(hotel);
                inIndex2 = idx.inIndex(6); /* should be true now */
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( dbe != bravo ) || 
                 ( inIndex1 != false ) ||
                 ( inIndex2 != true ) ||
                 ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( inIndex1 )
                    {
                        outStream.print("Unexpected return from first " +
                                        "idx.inIndex(6).\n");
                    }
                    
                    if ( inIndex2 )
                    {
                        outStream.print("Unexpected return from second " +
                                        "idx.inIndex(6).\n");
                    }
                    
                    if ( dbe != bravo )
                    {
                        outStream.print(
                                "Unexpected return from idx.getElement(2).\n");
                    }
                    
                    if ( ! methodReturned )
                    {
                        outStream.print("Calls to idx.removeElement() and " +
                                "idx.replaceElement failed to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("idx.removeElement() threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 3) )
            {
                failures++;
            }
        }

        /* At this point we have tested functionality with valid input.
         *
         * Start by verifying that addElement() generates the expected
         * errors.
         */
        
        /* Start by trying to insert a DBElement whose ID has already been
         * defined.
         *
         * alpha's id was set the first time we inserted it, so we will use
         * it as a test element.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                idx.addElement(alpha);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to idx.addElement() with bad " +
                                        "id completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("idx.addElement(bad id) failed to " +
                                "throw a system error exception:.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 4) )
            {
                failures++;
            }
        }
        
        /* Now try to add an element with a database reference that doesn't
         * match that of the index.
         */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                idx.addElement(kilo);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to idx.addElement() with bad " +
                                        "db completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("idx.addElement(bad db) failed to " +
                                "throw a system error exception:.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 5) )
            {
                failures++;
            }
        }
        
        /* Now try to add an element to the index that is already in the index.
         * To avoid triggering the ID aleady set error we will have to set
         * the id to INVALID_ID
         */

        if ( failures == 0 )
        {
            long old_id = INVALID_ID;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                old_id = DBElement.ResetID(india);
                idx.addElement(india);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to idx.addElement() with dbe " +
                                        "already in index completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("idx.addElement(bad db) failed to " +
                                "throw a system error exception:.\n");
                    }
                }
            }
            else /* fix up india's ID so we dont' confuse the index */
            {
                methodReturned = false;
                threwSystemErrorException = false;
                
                try
                {
                    india.setID(old_id);
                    methodReturned = true;
                }
                
                catch (SystemErrorException e)
                {
                    threwSystemErrorException = true;
                    systemErrorExceptionString = e.getMessage();
                }
                
                if ( ( ! methodReturned ) ||
                     ( threwSystemErrorException ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        if ( ! methodReturned )
                        {
                            outStream.print(
                                    "india.setID() failed to complete.\n");
                        }
                        
                        if ( threwSystemErrorException )
                        {
                            outStream.printf("india.setID() threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                        }
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 6) )
            {
                failures++;
            }
        }
        
        /* Now trick the index into trying to assign an id that is already 
         * in use.  This should fail with a system error.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                idx.nextID--;
                idx.addElement(lima);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to idx.addElement() with next " +
                                        "id already in use completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("idx.addElement() with next id in use " +
                                "failed to throw a system error exception:.\n");
                    }
                }
            }
            else /* fix up ide.nextID so we can continue using the instance */
            {
                idx.nextID++;
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 7) )
            {
                failures++;
            }
        }

        /*
         * Next, verify that getElement() fails as expected.
         */
        /* Start by verifying that getElement fails when passed the invalid ID.
         */
        if ( failures == 0 )
        {
            dbe = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                dbe = idx.getElement(INVALID_ID);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( dbe != null ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ( dbe != null ) || ( methodReturned ) )
                    {
                        outStream.print("Call to idx.getElement(INVALID_ID) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("idx.getElement(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 8) )
            {
                failures++;
            }
        }
        
        /* Likewise verify that calling getElement with an un-used ID will 
         * generate a system error.
         */
        if ( failures == 0 )
        {
            dbe = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                dbe = idx.getElement(1000);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to idx.getElement(1000) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("idx.getElement(1000) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 9) )
            {
                failures++;
            }
        }
        
        /* 
         * Next, verify that inIndex() fails where expected.  This is pretty 
         * easy, as the only way inIndex() should fail is if you pass it the
         * INVALID_ID.
         */
        if ( failures == 0 )
        {
            boolean isInIndex = false;
            
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                isInIndex = idx.inIndex(INVALID_ID);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( isInIndex ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( isInIndex )
                    {
                        outStream.print("Call to idx.inIndex(INVALID_ID) " +
                                        "returned true.\n");
                    }
                    
                    if ( methodReturned )
                    {
                        outStream.print("Call to idx.inIndex(INVALID_ID) " +
                                        "completed.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.print("idx.inIndex(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 10) )
            {
                failures++;
            }
        }

        /*
         * Next, verify that removeElement() in the expected places.
         */
        /* Start by feeding removeElement the INVALID_ID */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                idx.removeElement(INVALID_ID);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to idx.removeElement(INVALID_ID) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("idx.removeElement(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 11) )
            {
                failures++;
            }
        }
        
        /* now try to remove a non-existant element.  Note that the method
         * should also fail if the target element isn't in the index.  However
         * we test to see if the ID is in use first, and thus this error will
         * only appear if there is a bug in the hash table.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                idx.removeElement(1000);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to idx.removeElement(1000) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("idx.removeElement(1000) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 12) )
            {
                failures++;
            }
        }
        
        /* 
         * Finally, verify that replaceElement fails in the expected places.
         */
        /* Start by feeding it a null dbe */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                idx.replaceElement(null);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to idx.replaceElement(null) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("idx.replaceElement(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 13) )
            {
                failures++;
            }
        }
        
        /* Next, feed replaceElement a DBElement with a db field that doesn't
         * match that of idx.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                idx.replaceElement(kilo);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to idx.replaceElement(bad db) " +
                                        "completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("idx.replaceElement(bad db) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 14) )
            {
                failures++;
            }
        }
        
        /* Next, feed replaceElement a DBElement with a id field set to 
         * INVALID_ID.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                idx.replaceElement(lima);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to idx.replaceElement" +
                                "(INVALID_ID) completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("idx.replaceElement(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 15) )
            {
                failures++;
            }
        }
        
        /* next, try to replace an element that isn't in the index */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                lima.setID(1000);
                idx.replaceElement(lima);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to idx.replaceElement" +
                                "(no_such_id) completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("idx.replaceElement(no_such_id) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 16) )
            {
                failures++;
            }
        }
        
        /* Finally, try to replace an index entry with an DBElement of a 
         * different sub-class.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;
                    
            try
            {
                mike.setID(hotel.getID());
                idx.replaceElement(mike);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("Call to idx.replaceElement" +
                                "(type mismatch) completed.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("idx.replaceElement(type mismatch) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            long keys[] = {2, 4, 6, 7};
            DBElement values[] = {india, juno, foxtrot, hotel};

            if ( ! VerifyIndexContents(4, keys, values, idx, outStream, 
                                       verbose, 17) )
            {
                failures++;
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
        
    } /* DBIndex::TestIndexManagement() */
    
    
    /**
     * TestToStringMethods()
     *
     * Test the toString() and toDBString() methods.
     *
     *              JRM -- 5/31/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean TestToStringMethods(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        final String expectedString0 = "((DBIndex) (index_contents: ()))";
        final String expectedString1 = "((DBIndex) (index_contents: (<val>, " +
                "<echo>, <delta>, <charlie>, <bravo>, <alpha>)))";
        final String expectedDBString0 = 
            "((DBIndex) (nextID: 1) (index_size: 0) (index_contents: ()))";
        final String expectedDBString1 = 
            "((DBIndex) (nextID: 7) (index_size: 6) " +
             "(index_contents: " +
                "((TextStringFormalArg 6 <val>), " +
                 "(TimeStampFormalArg 5 <echo> false null null), " +
                 "(QuoteStringFormalArg 4 <delta>), " +
                 "(NominalFormalArg 3 <charlie> false ()), " +
                 "(IntFormalArg 2 <bravo> true 0 1), " +
                 "(FloatFormalArg 1 <alpha> true 0.0 1.0))))";
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        Database db = null;
        DBIndex idx = null;
        FloatFormalArg alpha = null;
        IntFormalArg bravo = null;
        NominalFormalArg charlie = null;
        QuoteStringFormalArg delta = null;
        TimeStampFormalArg echo = null;
        TextStringFormalArg foxtrot = null;
        UnTypedFormalArg golf = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;

            try
            {
                db = new ODBCDatabase();
                idx = new DBIndex(db);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( idx == null ) ||
                 ( ! completed ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( idx == null )
                    {
                        outStream.print("idx null after setup?!?\n");
                    }
                    
                    if ( ! completed )
                    {
                        outStream.print(
                            "Setup for strings test failed to complete(1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException in " +
                                "setup for strings test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* first see if an enpty index generates the expected string and debug
         * string.
         */
        
        if ( failures == 0 )
        {
            if ( idx.toString().compareTo(expectedString0) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                        "idx.toString() returned unexpected value(1): \"%s\".\n",
                        idx.toString());
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( idx.toDBString().compareTo(expectedDBString0) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("idx.toDBString() returned unexpected " +
                        "value(1): \"%s\".\n", idx.toDBString());
                }
            }
        }
        
        /* now allocate a bunch of formal arguments, insert them in the 
         * index, and test the to string methods again.
         */
        
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;

            try
            {
                alpha = new FloatFormalArg(db, "<alpha>", 0.0, 1.0);
                bravo = new IntFormalArg(db, "<bravo>", 0, 1);
                charlie = new NominalFormalArg(db, "<charlie>");
                delta = new QuoteStringFormalArg(db, "<delta>");
                echo = new TimeStampFormalArg(db, "<echo>");
                foxtrot = new TextStringFormalArg(db);
                idx.addElement(alpha);
                idx.addElement(bravo);
                idx.addElement(charlie);
                idx.addElement(delta);
                idx.addElement(echo);
                idx.addElement(foxtrot);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! completed ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                            "Setup for strings test failed to complete(2).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException in " +
                                "setup for strings test(2): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( idx.toString().compareTo(expectedString1) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                        "idx.toString() returned unexpected value(2): \"%s\".\n",
                        idx.toString());

                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( idx.toDBString().compareTo(expectedDBString1) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("idx.toDBString() returned unexpected " +
                        "value(2): \"%s\".\n", idx.toDBString());
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
        
    } /* DBIndex::TestToStringMethods() */
    
    
    /**
     * VerifyIndexContents()
     *
     * Verify that the supplied instance of DBIndex contains the key value pairs
     * contained in the keys and values vectors, and no others.
     *
     * Return true if this holds, and false otherwise. 
     *
     *                                                  JRM -- 4/24/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected static boolean VerifyIndexContents(int numEntries,
                                                 long keys[],
                                                 DBElement values[],
                                                 DBIndex idx,
                                                 java.io.PrintStream outStream,
                                                 boolean verbose,
                                                 int testNum)
        throws SystemErrorException
    {
        final String mName = "DBIndex::VerifyIndexContents(): ";
        boolean verified = true; /* will set to false if necessary */
        int i = 0;
        
        if ( ( idx == null ) || ( outStream == null ) )
        {
            throw new SystemErrorException(mName + "null param(s) on entry");
        }
        
        if ( numEntries != idx.index.size() )
        {
            verified = false;
            
            if ( verbose )
            {
                outStream.printf("test %d: bad index size %d (%d expected).\n",
                                     testNum, idx.index.size(), numEntries);
            }
        }
        
        while ( i < numEntries )
        {
            if ( idx.index.get(keys[i]) != values[i] )
            {
                verified = false;
                
                if ( verbose )
                {
                    outStream.printf("test %d: unexpected value for key %d.\n",
                                     testNum, keys[i]);
                }
            }
            i++;
        }
       
        return verified;
        
    } /** DBIndex::VerifyIndexContents() */
    
} /* class DBIndex */
