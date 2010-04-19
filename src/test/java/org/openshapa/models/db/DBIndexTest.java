package org.openshapa.models.db;

import java.io.PrintStream;
import junitx.util.PrivateAccessor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

/**
 *
 */
public class DBIndexTest {

    private PrintStream outStream;
    private boolean verbose;

    public DBIndexTest() {
    }

    @BeforeClass
    public void setUpClass() {
        outStream = System.out;
        verbose = true;
    }

    @AfterClass
    public void tearDownClass() {
    }

    @Test
    public void DummyTest() {
        
    }

    /**
     * GetIndexSize()
     *
     * Test function that returns the number of entries in the supplied
     * instance of DBIndex.
     *                                           - 5/21/07
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
    @Test
    public void Test1ArgConstructor() {
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
            try {
                Long idxnextID = (Long) PrivateAccessor.getField(idx, "nextID");
                if ( !idxnextID.equals(new Long(1)))
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("Unexpected initial value of nextID: %l.\n",
                                           idxnextID);
                    }
                }
            } catch (Throwable th) {
                failures++;
                        outStream.printf("Problem with PrivateAccessor on idx");
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

        assertTrue(pass);

    } /* DBIndex::Test1ArgConstructor() */

    /**
     * TestIndexManagement()
     *
     * Run a battery of tests on index management.
     *
     *                                   -- 4/24/07
     *
     * Changes:
     *
     *    - Non.
     */
    @Test
    public void TestIndexManagement() throws SystemErrorException {
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
            long old_id = DBIndex.INVALID_ID;

            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                old_id = DBElementTest.ResetID(india);
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
                Long idxnextID = (Long) PrivateAccessor.getField(idx, "nextID");
                idxnextID--;
                PrivateAccessor.setField(idx, "nextID", idxnextID);
                idx.addElement(lima);
                methodReturned = true;
            }
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            catch (Throwable th) {
                failures++;
                if ( verbose ) {
                    outStream.print("Problem with PrivateAccessor to idx.\n");
                }
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
                try
                {
                    Long idxnextID = (Long) PrivateAccessor.getField(idx, "nextID");
                    idxnextID++;
                    PrivateAccessor.setField(idx, "nextID", idxnextID);
                }
                catch (Throwable th) {
                    failures++;
                    if ( verbose ) {
                        outStream.print("Problem with PrivateAccessor to idx.\n");
                    }
                }
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
                dbe = idx.getElement(DBIndex.INVALID_ID);
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
                isInIndex = idx.inIndex(DBIndex.INVALID_ID);
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
                idx.removeElement(DBIndex.INVALID_ID);
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

        assertTrue(pass);
    } /* DBIndex::TestIndexManagement() */


    /**
     * TestToStringMethods()
     *
     * Test the toString() and toDBString() methods.
     *
     *               -- 5/31/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestToStringMethods() throws SystemErrorException {
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

        assertTrue(pass);
    } /* DBIndex::TestToStringMethods() */


    /**
     * VerifyIndexContents()
     *
     * Verify that the supplied instance of DBIndex contains the key value pairs
     * contained in the keys and values vectors, and no others.
     *
     * Return true if this holds, and false otherwise.
     *
     *                                                   -- 4/24/07
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

}