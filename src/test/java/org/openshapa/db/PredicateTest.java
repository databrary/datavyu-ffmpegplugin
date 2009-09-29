package org.openshapa.db;

import java.io.PrintStream;
import java.util.Vector;
import junitx.util.PrivateAccessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test cases for the class Predicate.
 */
public class PredicateTest {

    /** Database for tests. */
    private Database db;

    /** id for Predicate vocab element. */
    private long pveID;

    private PrintStream outStream;
    private boolean verbose;


    public PredicateTest() {
    }

    @Before
    public void setUp() throws SystemErrorException {
        db = new ODBCDatabase();

        PredicateVocabElement pve0 = new PredicateVocabElement(db, "test0");
        pve0.appendFormalArg(new FloatFormalArg(db, "<float>"));
        pve0.appendFormalArg(new IntFormalArg(db, "<int>"));
        pve0.appendFormalArg(new NominalFormalArg(db, "<nominal>"));
        pve0.appendFormalArg(new PredFormalArg(db, "<pred>"));
        pve0.appendFormalArg(new QuoteStringFormalArg(db, "<qstring>"));
        pve0.appendFormalArg(new TimeStampFormalArg(db, "<timestamp>"));
        pve0.appendFormalArg(new UnTypedFormalArg(db, "<untyped>"));
        pveID = db.addPredVE(pve0);

        outStream = System.out;
        verbose = true;
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of hashCode method, of class Predicate.
     */
    @Test
    public void testHashCode() throws SystemErrorException {
        Predicate p0 = new Predicate(db);
        Predicate p1 = new Predicate(db);
        Predicate p2 = new Predicate(db, pveID);

        assertTrue(p0.hashCode() == p1.hashCode());
        assertTrue(p1.hashCode() != p2.hashCode());
    }

    /**
     * Test of equals method, of class Predicate.
     */
    @Test
    public void testEquals() throws SystemErrorException {
        Predicate p0 = new Predicate(db);
        Predicate p1 = new Predicate(db);
        
        Predicate p2 = new Predicate(db, pveID);
        Predicate p3 = new Predicate(db, pveID);
        Predicate p4 = new Predicate(db, pveID);

        // Reflexive
        assertTrue(p0.equals(p1));
        assertTrue(p2.equals(p3));

        // Symmetric
        assertTrue(p2.equals(p3));
        assertTrue(p3.equals(p2));
        assertTrue(p1.equals(p0));

        // Transitive
        assertTrue(p2.equals(p3));
        assertTrue(p3.equals(p4));
        assertTrue(p4.equals(p2));

        // Not equals
        assertFalse(p2.equals(null));
        assertFalse(p0.equals(p2));
    }

    // TODO: Must add tests to verify corrct management of undefined data values
    //       and query variables.  A lot of this will be tested in MacSHAPA file
    //       save reload, and query language -- so perhaps I can get away with
    //       holding off for a while.
    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) One argument constructor:
     *
     *      a) Verify that constructor completes when passed a valid instance
     *         of Database, and returns an instance of Predicate.  Verify that:
     *
     *              pred.db matches supplied value
     *              pred.predID == DBIndex.INVALID_ID
     *              pred.predName == ""
     *              pred.argList == NULL
     *              pred.varLen == false
     *
     *      b) Verify that constructor fails when passed an invalid db.
     *
     * 2) Two argument constructor:
     *
     *      a) Construct a database and a pve (predicate vocab element).
     *         Insert the pve in the database, and make note of the id
     *         assigned to the pve.  Construct a Predicate passing a reference
     *         to the database and the id of the pve.  Verify that:
     *
     *              pred.db matches the suplied value
     *              pred.predID matches the supplied value
     *              pred.predName matches the name of the pve
     *              pred.argList reflects the formal argument list of the pve
     *              pred.varLen matches the varLen field of the pve.
     *
     *          Do this with both a single entry and a multi-entry predicate,
     *          and with both a fixed length and a variable length predicate
     *
     *      b) Verify that the constructor fails when passed and invalid
     *         db or an invalid pve id.
     *
     * 3) Three argument constructor:
     *
     *      a) Construct a database and a pve (predicate vocab element).
     *         Insert the pve in the database, and make note of the id
     *         assigned to the pve.  Construct an argument list assigned
     *         matching the arg list of the pve.  Construct a Predicate,
     *         passing the db, the id of the pve, and the arg list.  Verify
     *         that:
     *
     *              pred.db matches the suplied value
     *              pred.predID matches the supplied value
     *              pred.predName matches the name of the pve
     *              pred.argList reflects both the formal argument list of
     *                  the pve and the supplied argument list.
     *              pred.varLen matches the varLen field of the pve.
     *
     *          Do this with both a single entry and a multi-entry predicate,
     *          and with both a fixed length and a variable length predicate.
     *
     *      b) Verify that the constructor fails when passed an invalid db,
     *         an invalid pve id, or an invalid argument list.  Note that
     *         we must test argument lists that are null, too short, too long,
     *         and which contain type mis-matches.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and several pve's (predicate vocab
     *         elements). Insert the pve's in the database, and make note
     *         of the id's assigned to the pve's.  Using these pve's, construct
     *         a selection of predicates with and without argument lists, and
     *         with and without initializations to arguments.
     *
     *         Now use the copy constructor to make copies of these predicates.
     *         Verify that the copies are correct.
     *
     *      b) Verify that the constructor fails when passed bad data.  Given
     *         the compiler's error checking, null should be the only bad
     *         value that has to be tested.
     *
     * 5) Accessors:
     *
     *      Verify that the getPredID(), setPredID(), getDB(), getNumArgs(),
     *      getVarLen(), and getPredName() methods perform correctly.
     *
     *      Do this by creating a database and a selection of predicate vocab
     *      elements.  Then create a selection of predicates, and verify that
     *      get methods return the expected values.  Then use setPredID() to
     *      change the pve ID associated with the predicates, and verify that
     *      values returned by the get methods have changed accordingly.
     *
     *      Verify that setPredID() fails when given invalid input.
     *
     *      lookupPredicateVE() is an internal method that has been exercised
     *      already.  Verify that it fails on invalid input.
     *
     * 6) ArgList management:
     *
     *      Verify that argument lists are converted properly when the predID
     *      is changed.  If salvage is true, must convert values to fit new
     *      formal argument list if possible.
     *
     *      Verify that the getArg() and replaceArg() methods perform as
     *      expected.
     *
     *      Verify that getArg() and replaceArg() methods fail on invalid
     *      input.
     *
     * 7) toString methods:
     *
     *      Verify that all fields are displayed correctly by the toString
     *      and toDBString() methods.
     *
     *
     *************************************************************************/

    /**
     * Test1ArgConstructor()
     *
     * Run a battery of tests on the one argument constructor for this
     * class, and on the instance returned.
     *
     *                                               -- 10/15/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test1ArgConstructor() {
        String testBanner =
            "Testing 1 argument constructor for class Predicate               ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        Predicate pred = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        pred = null;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();
            pred = new Predicate(db);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( pred == null ) ||
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

                if ( pred == null )
                {
                    outStream.print(
                            "new Predicate(db) returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new Predicate(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("db not initialized correctly.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred.getPveID() != DBIndex.INVALID_ID )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predID: %ld.\n",
                            pred.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred.getPredName().compareTo("") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predName: \"%s\".\n",
                            pred.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred.argList != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred.getVarLen() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of varLen.\n");
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            pred = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pred = new Predicate((Database)null);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pred != null ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("new Predicate(null) returned.\n");
                    }

                    if ( pred != null )
                    {
                        outStream.print(
                                "new Predicate(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new Predicate(null) failed to throw " +
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

        assertTrue(pass);
    } /* Predicate::Test1ArgConstructor() */


    /**
     * Test2ArgConstructor()
     *
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instance returned.
     *
     *                                               -- 10/15/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test2ArgConstructor() throws SystemErrorException {
        String testBanner =
            "Testing 2 argument constructor for class Predicate               ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long predID0 = DBIndex.INVALID_ID;
        long predID1 = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        FormalArgument farg = null;
        Predicate pred0 = null;
        Predicate pred1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "test0");

            farg = new FloatFormalArg(db, "<float>");
            pve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve0.appendFormalArg(farg);

            predID0 = db.addPredVE(pve0);

            pred0 = new Predicate(db, predID0);

            pve1 = new PredicateVocabElement(db, "test1");

            farg = new UnTypedFormalArg(db, "<arg>");
            pve1.appendFormalArg(farg);

            pve1.setVarLen(true);

            predID1 = db.addPredVE(pve1);

            pred1 = new Predicate(db, predID1);
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( predID0 == DBIndex.INVALID_ID ) ||
             ( pred0 == null ) ||
             ( pve1 == null ) ||
             ( predID1 == DBIndex.INVALID_ID ) ||
             ( pred1 == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }

                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( predID0 == DBIndex.INVALID_ID )
                {
                    outStream.print("insertion of pve0 failed.\n");
                }

                if ( pred0 == null )
                {
                    outStream.print(
                            "new Predicate(db, predID0() returned null.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( predID1 == DBIndex.INVALID_ID )
                {
                    outStream.print("insertion of pve1 failed.\n");
                }

                if ( pred1 == null )
                {
                    outStream.print(
                            "new Predicate(db, predID1() returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print(
                                 "test setup threw a SystemErrorException.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0.db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getPveID() != predID0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predID0: %ld.\n",
                            pred0.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getPredName().compareTo("test0") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predName: \"%s\".\n",
                            pred0.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.argList == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getVarLen() != pve0.getVarLen() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.argListToString().compareTo(
                    "(0.000000, 0, , (), \"\", 00:00:00:000, <untyped>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected arg list string: \"%s\".\n",
                                     pred0.argListToString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.argListToDBString().compareTo(
                    "(argList ((FloatDataValue (id 0) " +
                                              "(itsFargID 2) " +
                                              "(itsFargType FLOAT) " +
                                              "(itsCellID 0) " +
                                              "(itsValue 0.0) " +
                                              "(subRange false) " +
                                              "(minVal 0.0) " +
                                              "(maxVal 0.0)), " +
                              "(IntDataValue (id 0) " +
                                            "(itsFargID 3) " +
                                            "(itsFargType INTEGER) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 0) " +
                                            "(subRange false) " +
                                            "(minVal 0) " +
                                            "(maxVal 0)), " +
                              "(NominalDataValue (id 0) " +
                                                "(itsFargID 4) " +
                                                "(itsFargType NOMINAL) " +
                                                "(itsCellID 0) " +
                                                "(itsValue <null>) " +
                                                "(subRange false)), " +
                              "(PredDataValue (id 0) " +
                                             "(itsFargID 5) " +
                                             "(itsFargType PREDICATE) " +
                                             "(itsCellID 0) " +
                                             "(itsValue ()) " +
                                             "(subRange false)), " +
                              "(QuoteStringDataValue (id 0) " +
                                                "(itsFargID 6) " +
                                                "(itsFargType QUOTE_STRING) " +
                                                "(itsCellID 0) " +
                                                "(itsValue <null>) " +
                                                "(subRange false)), " +
                             "(TimeStampDataValue (id 0) " +
                                               "(itsFargID 7) " +
                                               "(itsFargType TIME_STAMP) " +
                                               "(itsCellID 0) " +
                                               "(itsValue (60,00:00:00:000)) " +
                                               "(subRange false)), " +
                             "(UndefinedDataValue (id 0) " +
                                                 "(itsFargID 8) " +
                                                 "(itsFargType UNTYPED) " +
                                                 "(itsCellID 0) " +
                                                 "(itsValue <untyped>) " +
                                                 "(subRange false))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred0 db arg list string: \"%s\".\n",
                            pred0.argListToDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred1.db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getPveID() != predID1 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predID1: %ld.\n",
                            pred1.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getPredName().compareTo("test1") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "Unexpected initial value of pred1.predName: \"%s\".\n",
                        pred1.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.argList == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1.argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getVarLen() != pve1.getVarLen() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1.varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.argListToString().compareTo("(<arg>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1.arg list string: \"%s\".\n",
                            pred1.argListToString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.argListToDBString().compareTo(
                    "(argList ((UndefinedDataValue (id 0) " +
                                                 "(itsFargID 10) " +
                                                 "(itsFargType UNTYPED) " +
                                                 "(itsCellID 0) " +
                                                 "(itsValue <arg>) " +
                                                 "(subRange false))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1 db arg list string: \"%s\".\n",
                            pred1.argListToDBString());
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db */
        pred0 = null;
        completed = false;
        threwSystemErrorException = false;

        try
        {
            pred0 = new Predicate(null, predID0);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( pred0 != null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "new Predicate(null, predID0) != null.\n");
                }

                if ( completed )
                {
                    outStream.print("new Predicate(null, predID0)completed.\n");

                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "new Predicate(null, predID0) " +
                            "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now verify that the constructor fails when passed an invalid
         * predicate vocab element ID.
         */
        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            pred0 = new Predicate(new ODBCDatabase(), predID0);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "\"new Predicate(db, bad_pred_id) "
                            + "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "\"new Predicate(db, bad_pred_id) "
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

        assertTrue(pass);
    } /* Predicate::Test2ArgConstructor() */

    /**
     * Test3ArgConstructor()
     *
     * Run a battery of tests on the three argument constructor for this
     * class, and on the instance returned.
     *
     *                                               -- 10/21/07
     *
     * Changes:
     *
     *    - None.
     */

    public void Test3ArgConstructor() throws SystemErrorException {
        String testBanner =
            "Testing 3 argument constructor for class Predicate               ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        String testArgString0 = "(1.000000, 2, a_nominal, " +
                    "test0(0.000000, 0, , (), \"\", 00:00:00:000, <untyped>), " +
                    "\"q-string\", 00:00:00:000, <untyped>)";
        String testArgDBString0 =
                    "(argList ((FloatDataValue (id 0) " +
                                              "(itsFargID 2) " +
                                              "(itsFargType FLOAT) " +
                                              "(itsCellID 0) " +
                                              "(itsValue 1.0) " +
                                              "(subRange false) " +
                                              "(minVal 0.0) " +
                                              "(maxVal 0.0)), " +
                              "(IntDataValue (id 0) " +
                                            "(itsFargID 3) " +
                                            "(itsFargType INTEGER) " +
                                            "(itsCellID 0) " +
                                            "(itsValue 2) " +
                                            "(subRange false) " +
                                            "(minVal 0) " +
                                            "(maxVal 0)), " +
                              "(NominalDataValue (id 0) " +
                                                "(itsFargID 4) " +
                                                "(itsFargType NOMINAL) " +
                                                "(itsCellID 0) " +
                                                "(itsValue a_nominal) " +
                                                "(subRange false)), " +
                              "(PredDataValue (id 0) " +
                                  "(itsFargID 5) " +
                                  "(itsFargType PREDICATE) " +
                                  "(itsCellID 0) " +
                                  "(itsValue (predicate (id 0) " +
                                     "(predID 1) "+
                                     "(predName test0) " +
                                     "(varLen false) " +
                                     "(argList " +
                                         "((FloatDataValue (id 0) " +
                                             "(itsFargID 2) " +
                                             "(itsFargType FLOAT) " +
                                             "(itsCellID 0) " +
                                             "(itsValue 0.0) " +
                                             "(subRange false) " +
                                             "(minVal 0.0) " +
                                             "(maxVal 0.0)), " +
                                          "(IntDataValue (id 0) " +
                                              "(itsFargID 3) " +
                                              "(itsFargType INTEGER) " +
                                              "(itsCellID 0) " +
                                              "(itsValue 0) " +
                                              "(subRange false) " +
                                              "(minVal 0) " +
                                              "(maxVal 0)), " +
                                          "(NominalDataValue (id 0) " +
                                              "(itsFargID 4) " +
                                              "(itsFargType NOMINAL) " +
                                              "(itsCellID 0) " +
                                              "(itsValue <null>) " +
                                              "(subRange false)), " +
                                          "(PredDataValue (id 0) " +
                                              "(itsFargID 5) " +
                                              "(itsFargType PREDICATE) " +
                                              "(itsCellID 0) " +
                                              "(itsValue ()) " +
                                              "(subRange false)), " +
                                          "(QuoteStringDataValue (id 0) " +
                                              "(itsFargID 6) " +
                                              "(itsFargType QUOTE_STRING) " +
                                              "(itsCellID 0) " +
                                              "(itsValue <null>) " +
                                              "(subRange false)), " +
                                          "(TimeStampDataValue (id 0) " +
                                              "(itsFargID 7) " +
                                              "(itsFargType TIME_STAMP) " +
                                              "(itsCellID 0) " +
                                              "(itsValue (60,00:00:00:000)) " +
                                              "(subRange false)), " +
                                          "(UndefinedDataValue (id 0) " +
                                              "(itsFargID 8) " +
                                              "(itsFargType UNTYPED) " +
                                              "(itsCellID 0) " +
                                              "(itsValue <untyped>) " +
                                              "(subRange false))))))) " +
                                      "(subRange false)), " +
                              "(QuoteStringDataValue (id 0) " +
                                                "(itsFargID 6) " +
                                                "(itsFargType QUOTE_STRING) " +
                                                "(itsCellID 0) " +
                                                "(itsValue q-string) " +
                                                "(subRange false)), " +
                              "(TimeStampDataValue (id 0) " +
                                                "(itsFargID 7) " +
                                                "(itsFargType TIME_STAMP) " +
                                                "(itsCellID 0) " +
                                                "(itsValue (60,00:00:00:000)) "+
                                                "(subRange false)), " +
                              "(UndefinedDataValue (id 0) " +
                                                  "(itsFargID 8) " +
                                                  "(itsFargType UNTYPED) " +
                                                  "(itsCellID 0) " +
                                                  "(itsValue <untyped>) " +
                                                  "(subRange false))))";
        String testArgString1 = "(<val>)";
        String testArgDBString1 = "(argList ((UndefinedDataValue (id 0) " +
                                                 "(itsFargID 10) " +
                                                 "(itsFargType UNTYPED) " +
                                                 "(itsCellID 0) " +
                                                 "(itsValue <val>) " +
                                                 "(subRange false))))";
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        long predID0 = DBIndex.INVALID_ID;
        long predID1 = DBIndex.INVALID_ID;
        long fargID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        FormalArgument farg = null;
        Vector<DataValue> argList;
        Vector<DataValue> argList0 = null;
        Vector<DataValue> argList0a = null;
        Vector<DataValue> argList1 = null;
        Vector<DataValue> argList1a = null;
        DataValue arg = null;
        Predicate pred0 = null;
        Predicate pred0a = null;
        Predicate pred1 = null;
        Predicate pred1a = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "test0");

            farg = new FloatFormalArg(db, "<float>");
            pve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve0.appendFormalArg(farg);

            predID0 = db.addPredVE(pve0);

            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(predID0);


            argList0 = new Vector<DataValue>();

            fargID = pve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 1.0);
            argList0.add(arg);
            fargID = pve0.getFormalArg(1).getID();
            arg = new IntDataValue(db, fargID, 2);
            argList0.add(arg);
            fargID = pve0.getFormalArg(2).getID();
            arg = new NominalDataValue(db, fargID, "a_nominal");
            argList0.add(arg);
            fargID = pve0.getFormalArg(3).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, predID0));
            argList0.add(arg);
            fargID = pve0.getFormalArg(4).getID();
            arg = new QuoteStringDataValue(db, fargID, "q-string");
            argList0.add(arg);
            fargID = pve0.getFormalArg(5).getID();
            arg = new TimeStampDataValue(db, fargID,
                                         new TimeStamp(db.getTicks()));
            argList0.add(arg);
            fargID = pve0.getFormalArg(6).getID();
            arg = new UndefinedDataValue(db, fargID);
            argList0.add(arg);

            pred0 = new Predicate(db, predID0, argList0);


            argList0a = new Vector<DataValue>();

            arg = new FloatDataValue(db);
            ((FloatDataValue)arg).setItsValue(1.0);
            argList0a.add(arg);
            arg = new IntDataValue(db);
            ((IntDataValue)arg).setItsValue(2);
            argList0a.add(arg);
            arg = new NominalDataValue(db);
            ((NominalDataValue)arg).setItsValue("a_nominal");
            argList0a.add(arg);
            arg = new PredDataValue(db);
            ((PredDataValue)arg).setItsValue(new Predicate(db, predID0));
            argList0a.add(arg);
            arg = new QuoteStringDataValue(db);
            ((QuoteStringDataValue)arg).setItsValue("q-string");
            argList0a.add(arg);
            arg = new TimeStampDataValue(db);
            ((TimeStampDataValue)arg).setItsValue(new TimeStamp(db.getTicks()));
            argList0a.add(arg);
            arg = new UndefinedDataValue(db);
            ((UndefinedDataValue)arg).setItsValue(
                    pve0.getFormalArg(6).getFargName());
            argList0a.add(arg);

            pred0a = new Predicate(db, predID0, argList0a);


            pve1 = new PredicateVocabElement(db, "test1");

            farg = new UnTypedFormalArg(db, "<val>");
            pve1.appendFormalArg(farg);

            pve1.setVarLen(true);


            predID1 = db.addPredVE(pve1);

            // get a copy of the databases version of pve0 with ids assigned
            pve1 = db.getPredVE(predID1);


            argList1 = new Vector<DataValue>();

            fargID = pve1.getFormalArg(0).getID();
            arg = new UndefinedDataValue(db, fargID);
            argList1.add(arg);

            pred1 = new Predicate(db, predID1, argList1);


            argList1a = new Vector<DataValue>();

            arg = new UndefinedDataValue(db);
            argList1a.add(arg);

            pred1a = new Predicate(db, predID1, argList1a);
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( predID0 == DBIndex.INVALID_ID ) ||
             ( argList0 == null ) ||
             ( argList0.size() != 7 ) ||
             ( ! ( argList0.elementAt(0) instanceof FloatDataValue ) ) ||
             ( ((FloatDataValue)(argList0.elementAt(0))).getItsValue() !=
                1.0 ) ||
             ( ! ( argList0.elementAt(1) instanceof IntDataValue ) ) ||
             ( ((IntDataValue)(argList0.elementAt(1))).getItsValue() != 2 ) ||
             ( ! ( argList0.elementAt(2) instanceof NominalDataValue ) ) ||
             ( ((NominalDataValue)(argList0.elementAt(2))).getItsValue().
                compareTo("a_nominal") != 0 ) ||
             ( ! ( argList0.elementAt(3) instanceof PredDataValue ) ) ||
             ( ! ( argList0.elementAt(4) instanceof QuoteStringDataValue ) ) ||
             ( ! ( argList0.elementAt(5) instanceof TimeStampDataValue ) ) ||
             ( ! ( argList0.elementAt(6) instanceof UndefinedDataValue ) ) ||
             ( pred0 == null ) ||
             ( argList0a == null ) ||
             ( argList0a.size() != 7 ) ||
             ( ! ( argList0a.elementAt(0) instanceof FloatDataValue ) ) ||
             ( ((FloatDataValue)(argList0a.elementAt(0))).getItsValue() !=
                1.0 ) ||
             ( ! ( argList0a.elementAt(1) instanceof IntDataValue ) ) ||
             ( ((IntDataValue)(argList0a.elementAt(1))).getItsValue() != 2 ) ||
             ( ! ( argList0a.elementAt(2) instanceof NominalDataValue ) ) ||
             ( ((NominalDataValue)(argList0a.elementAt(2))).getItsValue().
                compareTo("a_nominal") != 0 ) ||
             ( ! ( argList0a.elementAt(3) instanceof PredDataValue ) ) ||
             ( ! ( argList0a.elementAt(4) instanceof QuoteStringDataValue ) ) ||
             ( ! ( argList0a.elementAt(5) instanceof TimeStampDataValue ) ) ||
             ( ! ( argList0a.elementAt(6) instanceof UndefinedDataValue ) ) ||
             ( pred0a == null ) ||
             ( pve1 == null ) ||
             ( predID1 == DBIndex.INVALID_ID ) ||
             ( argList1 == null ) ||
             ( argList1.size() != 1 ) ||
             ( ! ( argList1.elementAt(0) instanceof UndefinedDataValue ) ) ||
             ( pred1 == null ) ||
             ( argList1a == null ) ||
             ( argList1a.size() != 1 ) ||
             ( ! ( argList1a.elementAt(0) instanceof UndefinedDataValue ) ) ||
             ( pred1a == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }

                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( predID0 == DBIndex.INVALID_ID )
                {
                    outStream.print("insertion of pve0 failed.\n");
                }

                if ( argList0 == null )
                {
                    outStream.print("creation of argList0 failed.\n");
                }
                else if ( ( argList0.size() != 7 ) ||
                          ( ! ( argList0.elementAt(0) instanceof
                                FloatDataValue ) ) ||
                          ( ((FloatDataValue)
                              (argList0.elementAt(0))).getItsValue() != 1.0 ) ||
                          ( ! ( argList0.elementAt(1) instanceof
                                IntDataValue ) ) ||
                          ( ((IntDataValue)
                             (argList0.elementAt(1))).getItsValue() != 2 ) ||
                          ( ! ( argList0.elementAt(2) instanceof
                                NominalDataValue ) ) ||
                          ( ((NominalDataValue)
                             (argList0.elementAt(2))).getItsValue().
                              compareTo("a_nominal") != 0 ) ||
                          ( ! ( argList0.elementAt(3) instanceof
                                PredDataValue ) ) ||
                          ( ! ( argList0.elementAt(4) instanceof
                                QuoteStringDataValue ) ) ||
                          ( ! ( argList0.elementAt(5) instanceof
                                TimeStampDataValue ) ) ||
                          ( ! ( argList0.elementAt(6) instanceof
                                UndefinedDataValue ) ) )
                {
                    outStream.print("unexpected argList0 structure.\n");
                }

                if ( pred0 == null )
                {
                    outStream.print("new Predicate(db, predID0, argList0) " +
                                    "returned null.\n");
                }

                if ( argList0a == null )
                {
                    outStream.print("creation of argList0a failed.\n");
                }
                else if ( ( argList0a.size() != 7 ) ||
                          ( ! ( argList0a.elementAt(0) instanceof
                                FloatDataValue ) ) ||
                          ( ((FloatDataValue)
                              (argList0a.elementAt(0))).getItsValue() != 1.0 ) ||
                          ( ! ( argList0a.elementAt(1) instanceof
                                IntDataValue ) ) ||
                          ( ((IntDataValue)
                             (argList0a.elementAt(1))).getItsValue() != 2 ) ||
                          ( ! ( argList0a.elementAt(2) instanceof
                                NominalDataValue ) ) ||
                          ( ((NominalDataValue)
                             (argList0a.elementAt(2))).getItsValue().
                              compareTo("a_nominal") != 0 ) ||
                          ( ! ( argList0a.elementAt(3) instanceof
                                PredDataValue ) ) ||
                          ( ! ( argList0a.elementAt(4) instanceof
                                QuoteStringDataValue ) ) ||
                          ( ! ( argList0a.elementAt(5) instanceof
                                TimeStampDataValue ) ) ||
                          ( ! ( argList0a.elementAt(6) instanceof
                                UndefinedDataValue ) ) )
                {
                    outStream.print("unexpected argList0a structure.\n");
                }

                if ( pred0a == null )
                {
                    outStream.print("new Predicate(db, predID0, argList0a) " +
                                    "returned null.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( predID1 == DBIndex.INVALID_ID )
                {
                    outStream.print("insertion of pve1 failed.\n");
                }

                if ( argList1 == null )
                {
                    outStream.print("creation of argList1 failed.\n");
                }
                else if ( ( argList1.size() != 1 ) ||
                          ( ! ( argList1.elementAt(0) instanceof
                               UndefinedDataValue ) ) )
                {
                    outStream.print("unexpected argList1 structure.\n");
                }

                if ( pred1 == null )
                {
                    outStream.print("new Predicate(db, predID1, argList1) " +
                                    "returned null.\n");
                }

                if ( argList1a == null )
                {
                    outStream.print("creation of argList1a failed.\n");
                }
                else if ( ( argList1a.size() != 1 ) ||
                          ( ! ( argList1a.elementAt(0) instanceof
                               UndefinedDataValue ) ) )
                {
                    outStream.print("unexpected argList1a structure.\n");
                }

                if ( pred1a == null )
                {
                    outStream.print("new Predicate(db, predID1, argList1a) " +
                                    "returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw a SystemErrorException:"+
                                     "\"%s\".\n", SystemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0.db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getPveID() != predID0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predID0: %ld.\n",
                            pred0.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getPredName().compareTo("test0") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predName: \"%s\".\n",
                            pred0.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0.argList == null ) || ( pred0.argList == argList0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred0.argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {

            if ( ( pred0.argList.size() != 7 ) ||
                 ( ! ( pred0.argList.elementAt(0) instanceof FloatDataValue ) ) ||
                 ( ! ( pred0.argList.elementAt(1) instanceof IntDataValue ) ) ||
                 ( ! ( pred0.argList.elementAt(2) instanceof NominalDataValue ) ) ||
                 ( ! ( pred0.argList.elementAt(3) instanceof PredDataValue ) ) ||
                 ( ! ( pred0.argList.elementAt(4) instanceof QuoteStringDataValue ) ) ||
                 ( ! ( pred0.argList.elementAt(5) instanceof TimeStampDataValue ) ) ||
                 ( ! ( pred0.argList.elementAt(6) instanceof UndefinedDataValue ) ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial pred0 argList structure.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.getVarLen() != pve0.getVarLen() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.argListToString().compareTo(testArgString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0 arg list string: " +
                                     "\"%s\".\n", pred0.argListToString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0.argListToDBString().compareTo(testArgDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred0 db arg list string: \"%s\".\n",
                            pred0.argListToDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0a.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0a.db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0a.getPveID() != predID0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred0a.predID0: %ld.\n",
                            pred0a.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0a.getPredName().compareTo("test0") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of " +
                            "pred0a.predName: \"%s\".\n",
                            pred0a.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0a.argList == null ) || ( pred0a.argList == argList0a ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred0a.argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {

            if ( ( pred0a.argList.size() != 7 ) ||
                 ( ! ( pred0a.argList.elementAt(0) instanceof FloatDataValue ) ) ||
                 ( ! ( pred0a.argList.elementAt(1) instanceof IntDataValue ) ) ||
                 ( ! ( pred0a.argList.elementAt(2) instanceof NominalDataValue ) ) ||
                 ( ! ( pred0a.argList.elementAt(3) instanceof PredDataValue ) ) ||
                 ( ! ( pred0a.argList.elementAt(4) instanceof QuoteStringDataValue ) ) ||
                 ( ! ( pred0a.argList.elementAt(5) instanceof TimeStampDataValue ) ) ||
                 ( ! ( pred0a.argList.elementAt(6) instanceof UndefinedDataValue ) ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial pred0a argList structure.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0a.getVarLen() != pve0.getVarLen() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred0a.varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0a.argListToString().compareTo(testArgString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0a arg list string: " +
                                     "\"%s\".\n", pred0a.argListToString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0a.argListToDBString().compareTo(testArgDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred0a db arg list string: \"%s\".\n",
                            pred0a.argListToDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred1.db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getPveID() != predID1 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of predID1: %ld.\n",
                            pred1.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getPredName().compareTo("test1") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "Unexpected initial value of pred1.predName: \"%s\".\n",
                        pred1.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred1.argList == null ) || ( pred1.argList == argList1 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1.argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.getVarLen() != pve1.getVarLen() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1.varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.argListToString().compareTo(testArgString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1.arg list string: \"%s\".\n",
                            pred1.argListToString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.argListToDBString().compareTo(testArgDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1 db arg list string: \"%s\".\n",
                            pred1.argListToDBString());
                }
            }
        }


        if ( failures == 0 )
        {
            if ( pred1a.getDB() != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred1a.db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.getPveID() != predID1 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1a.predID1: %ld.\n",
                            pred1a.getPveID());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.getPredName().compareTo("test1") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "Unexpected initial value of pred1a.predName: \"%s\".\n",
                        pred1a.getPredName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred1a.argList == null ) || ( pred1a.argList == argList1a ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1a.argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.getVarLen() != pve1.getVarLen() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected initial value of pred1a.varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.argListToString().compareTo(testArgString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1a.arg list string: \"%s\".\n",
                            pred1a.argListToString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.argListToDBString().compareTo(testArgDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1a db arg list string: \"%s\".\n",
                            pred1a.argListToDBString());
                }
            }
        }


        /* Verify that the constructor fails when passed an invalid arg list.
         * Start with a null list.
         */
        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            pred0 = new Predicate(null, predID0, null);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "\"new Predicate(db, predID0, null) "
                            + "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "\"new Predicate(null, predID0, null) " +
                            "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now construct a list that is too long. */

        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            argList = new Vector<DataValue>();

            fargID = pve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            argList.add(arg);
            fargID = pve0.getFormalArg(1).getID();
            arg = new IntDataValue(db, fargID, 22);
            argList.add(arg);
            fargID = pve0.getFormalArg(2).getID();
            arg = new NominalDataValue(db, fargID, "another_nominal");
            argList.add(arg);
            fargID = pve0.getFormalArg(3).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, predID1));
            argList.add(arg);
            fargID = pve0.getFormalArg(4).getID();
            arg = new QuoteStringDataValue(db, fargID, "q-string-2");
            argList.add(arg);
            fargID = pve0.getFormalArg(5).getID();
            arg = new TimeStampDataValue(db, fargID,
                                         new TimeStamp(db.getTicks()));
            argList.add(arg);
            fargID = pve0.getFormalArg(6).getID();
            arg = new UndefinedDataValue(db, fargID);
            argList.add(arg);
            /* now add an extranious argument */
            arg = new IntDataValue(db, fargID, 33);
            argList.add(arg);


            pred0 = new Predicate(null, predID0, argList);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "new Predicate(db, predID0, too_long_arg_list) " +
                            "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "new Predicate(null, predID0, too_long_arg_list) " +
                            "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now construct a list that is too short. */

        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            argList = new Vector<DataValue>();

            fargID = pve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            argList.add(arg);
            fargID = pve0.getFormalArg(1).getID();
            arg = new IntDataValue(db, fargID, 22);
            argList.add(arg);
            fargID = pve0.getFormalArg(2).getID();
            arg = new NominalDataValue(db, fargID, "another_nominal");
            argList.add(arg);
            fargID = pve0.getFormalArg(3).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, predID1));
            argList.add(arg);
            fargID = pve0.getFormalArg(4).getID();
            arg = new QuoteStringDataValue(db, fargID, "q-string-2");
            argList.add(arg);
            fargID = pve0.getFormalArg(5).getID();
            arg = new TimeStampDataValue(db, fargID,
                                         new TimeStamp(db.getTicks()));
            argList.add(arg);
            fargID = pve0.getFormalArg(6).getID();
            arg = new UndefinedDataValue(db, fargID);
            /* don't add the last argument */


            pred0 = new Predicate(null, predID0, argList);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "new Predicate(db, predID0, too_short_arg_list) "
                            + "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                        "new Predicate(null, predID0, too_short_arg_list) " +
                        "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now construct a list that contains a type mismatch.  Many type
         * mismatches are possible -- we will just jeck one for now.
         */
        /* TODO: add tests for all possible type mis-matches */

        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            argList = new Vector<DataValue>();

            fargID = pve0.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 11.0);
            argList.add(arg);
            fargID = pve0.getFormalArg(1).getID();
            arg = new IntDataValue(db, fargID, 22);
            argList.add(arg);
            fargID = pve0.getFormalArg(2).getID();
            arg = new NominalDataValue(db, fargID, "another_nominal");
            argList.add(arg);
            fargID = pve0.getFormalArg(3).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, predID1));
            argList.add(arg);
            fargID = pve0.getFormalArg(4).getID();
            arg = new QuoteStringDataValue(db, fargID, "q-string-2");
            argList.add(arg);

            /* swap arguments for entries 5 & 6 */
            fargID = pve0.getFormalArg(6).getID();
            arg = new UndefinedDataValue(db, fargID);
            argList.add(arg);
            fargID = pve0.getFormalArg(5).getID();
            arg = new TimeStampDataValue(db, fargID,
                                         new TimeStamp(db.getTicks()));
            argList.add(arg);


            pred0 = new Predicate(null, predID0, argList);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "new Predicate(db, predID0, mis_match_arg_list) " +
                            "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                        "new Predicate(null, predID0, mis_match_arg_list) " +
                        "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* now verify that the constructor fails when passed an invalid
         * pve id.
         */
        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            pred0 = new Predicate(new ODBCDatabase(), predID1, argList1);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "\"new Predicate(db, bad_pred_id, argList1) "
                            + "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "\"new Predicate(db, bad_pred_id, argList1) "
                            + "didn't throw a SystemErrorException.\n");
                }
            }
        }

        /* finally, verify that the constructor fails when passed an invalid
         * database.
         */
        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            pred0 = new Predicate(null, predID1, argList1);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( pred0 != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred0 != null )
                {
                    outStream.print(
                            "\"new Predicate(null, bad_pred_id, argList1) "
                            + "!= null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "\"new Predicate(null, bad_pred_id, argList1) "
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

        assertTrue(pass);
    } /* Predicate::Test3ArgConstructor() */


    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors for this class.
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestAccessors() throws SystemErrorException {
        String testBanner =
            "Testing class Predicate accessors                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        Database db = null;
        PredicateVocabElement pve  = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        long fargID = DBIndex.INVALID_ID;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        FormalArgument farg = null;
        Vector<DataValue> argList0 = null;
        Vector<DataValue> argList1 = null;
        Vector<DataValue> argList2 = null;
        Vector<DataValue> argList3 = null;
        DataValue arg = null;
        Predicate pred0 = null;
        Predicate pred1 = null;
        Predicate pred2 = null;
        Predicate pred3 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database and pve's
        threwSystemErrorException = false;
        completed = false;

        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);

            pve0ID = db.addPredVE(pve0);

            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);


            pve2 = new PredicateVocabElement(db, "pve2");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve2.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve2.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            pve2.appendFormalArg(farg);

            pve2ID = db.addPredVE(pve2);

            // get a copy of the databases version of pve1 with ids assigned
            pve2 = db.getPredVE(pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve3.appendFormalArg(farg);
            pve3.setVarLen(true);

            pve3ID = db.addPredVE(pve3);

            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
             ( pve2 == null ) ||
             ( pve2ID == DBIndex.INVALID_ID ) ||
             ( pve3 == null ) ||
             ( pve3ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }

                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0ID not initialized.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve2ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve2ID not initialized.\n");
                }

                if ( pve3 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve3ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve3ID not initialized.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            SystemErrorExceptionString);
                }
            }
        }

        // Verify that getDB() works as expected.  There is not much to
        // do here, as the db field is set on creation and never changed.
        // Thus this test is a repeat tests done in the constructor tests.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                pred0 = new Predicate(db);

                pred1 = new Predicate(db, pve1ID);

                argList3 = new Vector<DataValue>();

                fargID = pve3.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                argList3.add(arg);

                pred3 = new Predicate(db, pve3ID, argList3);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( pred0 == null ) ||
                 ( pred1 == null ) ||
                 ( pred3 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred0 == null )
                    {
                        outStream.print("new Predicate(db) returned null.\n");
                    }

                    if ( pred1 == null )
                    {
                        outStream.print(
                                "new Predicate(db, pve1ID) returned null.\n");
                    }

                    if ( pred3 == null )
                    {
                        outStream.print(
                          "new Predicate(db, pve3ID, argList) returned null.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "setup for getDB() test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("setup for getDB() test threw a " +
                                "SystemErrorException: \"%s\".\n",
                                SystemErrorExceptionString);
                    }
                }

                if ( ( pred0.getDB() != db ) ||
                     ( pred1.getDB() != db ) ||
                     ( pred3.getDB() != db ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "getDB() returned an unexpected value.\n");
                    }
                }
            }
        }

        // Verify that getPveID() / setPredID() work as advertized.  Also test
        // getPredName(), getNumArgs(), and getVarLen in passing.
        //
        // Note that when we set the pveID, we must also re-work the argument
        // list to conform to the new predicate.  We will test this slightly
        // below, but the real test of this feature will be in the argument
        // list management test.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                pred0 = new Predicate(db);

                pred1 = new Predicate(db, pve1ID);

                pred2 = new Predicate(db, pve2ID);

                argList3 = new Vector<DataValue>();

                fargID = pve3.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                argList3.add(arg);

                pred3 = new Predicate(db, pve3ID, argList3);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( pred0 == null ) ||
                 ( pred1 == null ) ||
                 ( pred2 == null ) ||
                 ( pred3 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred0 == null )
                    {
                        outStream.print("new Predicate(db) returned null.\n");
                    }

                    if ( pred1 == null )
                    {
                        outStream.print(
                                "new Predicate(db, pve1ID) returned null.\n");
                    }

                    if ( pred2 == null )
                    {
                        outStream.print(
                                "new Predicate(db, pve2ID) returned null.\n");
                    }

                    if ( pred3 == null )
                    {
                        outStream.print(
                          "new Predicate(db, pve3ID, argList) returned null.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("setup for get/set pred ID test " +
                                        "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("setup for getDB() test threw a " +
                                "SystemErrorException: \"%s\".\n",
                                SystemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0.getPveID() != DBIndex.INVALID_ID ) ||
                 ( pred1.getPveID() != pve1ID ) ||
                 ( pred2.getPveID() != pve2ID ) ||
                 ( pred3.getPveID() != pve3ID ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getPredID() returned an unexpected " +
                        "value 1: %d(%d), %d(%d), %d(%d), %d(%d).\n",
                        pred0.getPveID(), DBIndex.INVALID_ID,
                        pred1.getPveID(), pve1ID,
                        pred2.getPveID(), pve2ID,
                        pred3.getPveID(), pve3ID);
                }
            } else if ( ( pred0.getNumArgs() != 0 ) ||
                        ( pred1.getNumArgs() != 2 ) ||
                        ( pred2.getNumArgs() != 3 ) ||
                        ( pred3.getNumArgs() != 1 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getNumArgs() returned an unexpected " +
                                     "values in get/set pred ID test 1.\n");
                }
            } else if ( ( pred0.getVarLen() != false ) ||
                        ( pred1.getVarLen() != false ) ||
                        ( pred2.getVarLen() != false ) ||
                        ( pred3.getVarLen() != true ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getVarLen() returned an unexpected " +
                                     "value in get/set pred ID test 1.\n");
                }
            } else if ( ( pred0.getPredName().compareTo("") != 0 ) ||
                        ( pred1.getPredName().compareTo("pve1") != 0 ) ||
                        ( pred2.getPredName().compareTo("pve2") != 0 ) ||
                        ( pred3.getPredName().compareTo("pve3") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getPredName() returned an unexpected " +
                                     "value in get/set pred ID test 1.\n");
                }
            }
        }

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                pred0.setPredID(pve1ID, false);
                pred1.setPredID(pve2ID, false);
                pred2.setPredID(pve3ID, false);
                pred3.setPredID(DBIndex.INVALID_ID, false);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
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
                                "setPredID() test failed to complete 1.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("setup for getDB() test threw a " +
                                "SystemErrorException: \"%s\".\n",
                                SystemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0.getPveID() != pve1ID ) ||
                 ( pred1.getPveID() != pve2ID ) ||
                 ( pred2.getPveID() != pve3ID ) ||
                 ( pred3.getPveID() != DBIndex.INVALID_ID ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getPredID() returned an unexpected " +
                        "value 2: %d(%d), %d(%d), %d(%d), %d(%d).\n",
                        pred0.getPveID(), pve1ID,
                        pred1.getPveID(), pve2ID,
                        pred2.getPveID(), pve3ID,
                        pred3.getPveID(), DBIndex.INVALID_ID);
                }
            } else if ( ( pred0.getNumArgs() != 2 ) ||
                        ( pred1.getNumArgs() != 3 ) ||
                        ( pred2.getNumArgs() != 1 ) ||
                        ( pred3.getNumArgs() != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getNumArgs() returned an unexpected " +
                                     "values in get/set pred ID test 2.\n");
                }
            } else if ( ( pred0.getVarLen() != false ) ||
                        ( pred1.getVarLen() != false ) ||
                        ( pred2.getVarLen() != true ) ||
                        ( pred3.getVarLen() != false ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getVarLen() returned an unexpected " +
                                     "value in get/set pred ID test 2.\n");
                }
            } else if ( ( pred0.getPredName().compareTo("pve1") != 0 ) ||
                        ( pred1.getPredName().compareTo("pve2") != 0 ) ||
                        ( pred2.getPredName().compareTo("pve3") != 0 ) ||
                        ( pred3.getPredName().compareTo("") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getPredName() returned an unexpected " +
                                     "value in get/set pred ID test 2.\n");
                }
            }
        }

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                pred0.setPredID(pve2ID, true);
                pred1.setPredID(pve3ID, true);
                pred2.setPredID(DBIndex.INVALID_ID, true);
                pred3.setPredID(pve1ID, true);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
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
                                "setPredID() test failed to complete 2.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("setup for getDB() test threw a " +
                                "SystemErrorException: \"%s\".\n",
                                SystemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0.getPveID() != pve2ID ) ||
                 ( pred1.getPveID() != pve3ID ) ||
                 ( pred2.getPveID() != DBIndex.INVALID_ID ) ||
                 ( pred3.getPveID() != pve1ID ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getPredID() returned an unexpected " +
                        "value 3: %d(%d), %d(%d), %d(%d), %d(%d).\n",
                        pred0.getPveID(), pve2ID,
                        pred1.getPveID(), pve3ID,
                        pred2.getPveID(), DBIndex.INVALID_ID,
                        pred3.getPveID(), pve2ID);
                }
            } else if ( ( pred0.getNumArgs() != 3 ) ||
                        ( pred1.getNumArgs() != 1 ) ||
                        ( pred2.getNumArgs() != DBIndex.INVALID_ID ) ||
                        ( pred3.getNumArgs() != 2 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getNumArgs() returned an unexpected " +
                                     "value in get/set pred ID test 3.\n");
                }
            } else if ( ( pred0.getVarLen() != false ) ||
                        ( pred1.getVarLen() != true ) ||
                        ( pred2.getVarLen() != false ) ||
                        ( pred3.getVarLen() != false ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getVarLen() returned an unexpected " +
                                     "value in get/set pred ID test 3.\n");
                }
            } else if ( ( pred0.getPredName().compareTo("pve2") != 0 ) ||
                        ( pred1.getPredName().compareTo("pve3") != 0 ) ||
                        ( pred2.getPredName().compareTo("") != 0 ) ||
                        ( pred3.getPredName().compareTo("pve1") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("getPredName() returned an unexpected " +
                                     "value in get/set pred ID test 3.\n");
                }
            }
        }

        // verify that setPredID() fails on invalid input.
        // start with an unused id
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                pred0.setPredID(100, true);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( pred0.getPveID() != pve2ID ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred0.getPveID() != pve2ID )
                    {
                        outStream.printf("pred0.getPredID() != pve2ID (1)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "pred0.setPredID(100, true) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("pred0.setPredID(100, true) failed " +
                                         "to thow a system error.\n");
                    }
                }
            }
        }


        // now use the id of a formal argument -- should fail as well
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                fargID = pve0.getFormalArg(0).getID();

                pred0.setPredID(fargID, true);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( fargID == DBIndex.INVALID_ID ) ||
                 ( pred0.getPveID() != pve2ID ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( fargID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("fargID == DBIndex.INVALID_ID (1).\n");
                    }

                    if ( pred0.getPveID() != pve2ID )
                    {
                        outStream.printf("pred0.getPredID() != pve2ID (2)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "pred0.setPredID(fargID, true) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("pred0.setPredID(fargID, true) " +
                                         "failed to thow a system error.\n");
                    }
                }
            }
        }

        // finally, verify that lookupPredicateVE() throws a system error on
        // invalid input.  Start with the valid id that does not refer to a
        // predicate vocab element

        threwSystemErrorException = false;
        completed = false;
        fargID = DBIndex.INVALID_ID;
        pve = null;

        if ( failures == 0 )
        {
            try
            {
                fargID = pve0.getFormalArg(0).getID();

             //   pve = pred0.lookupPredicateVE(fargID);

                Object obj = PrivateAccessor.invoke(pred0,
                        "lookupPredicateVE",
                        new Class[]{long.class}, new Object[]{fargID});
                if (obj != null) {
                    pve = (PredicateVocabElement) obj;
                }

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            } catch (Throwable th) {
                outStream.printf("pred0.invoke threw unexpectedly");
                failures++;
            }

            if ( ( fargID == DBIndex.INVALID_ID ) ||
                 ( pve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( fargID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("fargID == DBIndex.INVALID_ID (2).\n");
                    }

                    if ( pve != null )
                    {
                        outStream.printf("pve != null (1)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "pred0.lookupPredicateVE(fargID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("pred0.lookupPredicateVE(fargID) " +
                                         "failed to thow a system error.\n");
                    }
                }
            }
        }

        // now try an unused ID
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
             //   pve = pred0.lookupPredicateVE(100);

                Object obj = PrivateAccessor.invoke(pred0,
                        "lookupPredicateVE",
                        new Class[]{long.class}, new Object[]{100});
                if (obj != null) {
                    pve = (PredicateVocabElement) obj;
                }

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            } catch (Throwable th) {
                outStream.printf("pred0.invoke 100 threw unexpectedly");
                failures++;
            }

            if ( ( pve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pve != null )
                    {
                        outStream.printf("pve != null (2)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf(
                                "pred0.lookupPredicateVE(100) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("pred0.lookupPredicateVE(100) " +
                                         "failed to thow a system error.\n");
                    }
                }
            }
        }

        // finally, try the invalid ID
        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
             //   pve = pred0.lookupPredicateVE(DBIndex.INVALID_ID);

                Object obj = PrivateAccessor.invoke(pred0,
                        "lookupPredicateVE",
                        new Class[]{long.class},
                        new Object[]{DBIndex.INVALID_ID});
                if (obj != null) {
                    pve = (PredicateVocabElement) obj;
                }

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            } catch (Throwable th) {
                outStream.printf("pred0.invoke Invalid_Id threw unexpectedly");
                failures++;
            }

            if ( ( pve != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pve != null )
                    {
                        outStream.printf("pve != null (3)\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("pred0.lookupPredicateVE" +
                                         "(DBIndex.INVALID_ID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("pred0.lookupPredicateVE" +
                                         "(DBIndex.INVALID_ID) " +
                                         "failed to thow a system error.\n");
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
    } /* Predicate::TestAccessors() */

    /**
     * TestArgListManagement()
     *
     * Run a battery of tests on the arg list management facilities.
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestArgListManagement() throws SystemErrorException {
        String testBanner =
            "Testing class Predicate argument list management                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        Database db = null;
        PredicateVocabElement pve   = null;
        PredicateVocabElement pve0  = null;
        PredicateVocabElement pve1  = null;
        PredicateVocabElement pve2  = null;
        PredicateVocabElement pve3  = null;
        PredicateVocabElement pve4  = null;
        PredicateVocabElement pve5  = null;
        PredicateVocabElement pve6  = null;
        PredicateVocabElement pve7  = null;
        PredicateVocabElement pve8  = null;
        PredicateVocabElement pve9  = null;
        PredicateVocabElement pve10 = null;
        PredicateVocabElement pve11 = null;
        PredicateVocabElement pve12 = null;
        long fargID  = DBIndex.INVALID_ID;
        long pve0ID  = DBIndex.INVALID_ID;
        long pve1ID  = DBIndex.INVALID_ID;
        long pve2ID  = DBIndex.INVALID_ID;
        long pve3ID  = DBIndex.INVALID_ID;
        long pve4ID  = DBIndex.INVALID_ID;
        long pve5ID  = DBIndex.INVALID_ID;
        long pve6ID  = DBIndex.INVALID_ID;
        long pve7ID  = DBIndex.INVALID_ID;
        long pve8ID  = DBIndex.INVALID_ID;
        long pve9ID  = DBIndex.INVALID_ID;
        long pve10ID = DBIndex.INVALID_ID;
        long pve11ID = DBIndex.INVALID_ID;
        long pve12ID = DBIndex.INVALID_ID;
        FormalArgument farg = null;
        Vector<DataValue> argList0 = null;
        Vector<DataValue> argList1 = null;
        Vector<DataValue> argList2 = null;
        Vector<DataValue> argList3 = null;
        Vector<DataValue> argList4 = null;
        Vector<DataValue> argList5 = null;
        Vector<DataValue> argList6 = null;
        Vector<DataValue> argList7 = null;
        DataValue arg                = null;
        FloatDataValue floatArg0     = null;
        FloatDataValue floatArg1     = null;
        IntDataValue intArg0         = null;
        IntDataValue intArg1         = null;
        IntDataValue intArg2         = null;
        NominalDataValue nominalArg0 = null;
        NominalDataValue nominalArg1 = null;
        PredDataValue predArg0       = null;
        PredDataValue predArg1       = null;
        QuoteStringDataValue qsArg0  = null;
        QuoteStringDataValue qsArg1  = null;
        TextStringDataValue textArg0 = null;
        TimeStampDataValue tsArg0    = null;
        TimeStampDataValue tsArg1    = null;
        UndefinedDataValue undefArg0 = null;
        UndefinedDataValue undefArg1 = null;
        Predicate pred0  = null;
        Predicate pred1  = null;
        Predicate pred2  = null;
        Predicate pred3  = null;
        Predicate pred4  = null;
        Predicate pred5  = null;
        Predicate pred6  = null;
        Predicate pred7  = null;
        Predicate pred8  = null;
        Predicate pred9  = null;
        Predicate pred10 = null;
        Predicate pred11 = null;
        Predicate pred12 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database and pve's
        threwSystemErrorException = false;
        completed = false;

        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve0.appendFormalArg(farg);

            pve0ID = db.addPredVE(pve0);

            // get a copy of the databases version of pve0 with ids assigned
            pve0 = db.getPredVE(pve0ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);


            pve2 = new PredicateVocabElement(db, "pve2");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve2.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve2.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            pve2.appendFormalArg(farg);

            pve2ID = db.addPredVE(pve2);

            // get a copy of the databases version of pve1 with ids assigned
            pve2 = db.getPredVE(pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve3.appendFormalArg(farg);
            pve3.setVarLen(true);

            pve3ID = db.addPredVE(pve3);

            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);


            pve4 = new PredicateVocabElement(db, "pve4");

            farg = new FloatFormalArg(db, "<float>");
            pve4.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve4.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve4.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve4.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve4.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve4.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve4.appendFormalArg(farg);

            pve4ID = db.addPredVE(pve4);

            // get a copy of the databases version of pve4 with ids assigned
            pve4 = db.getPredVE(pve4ID);


            pve5 = new PredicateVocabElement(db, "pve5");

            farg = new UnTypedFormalArg(db, "<untyped>");
            pve5.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            pve5.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve5.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve5.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve5.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve5.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve5.appendFormalArg(farg);

            pve5ID = db.addPredVE(pve5);

            // get a copy of the databases version of pve5 with ids assigned
            pve5 = db.getPredVE(pve5ID);


            pve6 = new PredicateVocabElement(db, "pve6");

            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve6.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve6.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            pve6.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve6.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve6.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve6.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve6.appendFormalArg(farg);

            pve6ID = db.addPredVE(pve6);

            // get a copy of the databases version of pve6 with ids assigned
            pve6 = db.getPredVE(pve6ID);


            pve7 = new PredicateVocabElement(db, "pve7");

            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve7.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve7.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve7.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            pve7.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve7.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve7.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve7.appendFormalArg(farg);

            pve7ID = db.addPredVE(pve7);

            // get a copy of the databases version of pve7 with ids assigned
            pve7 = db.getPredVE(pve7ID);


            pve8 = new PredicateVocabElement(db, "pve8");

            farg = new PredFormalArg(db, "<pred>");
            pve8.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve8.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve8.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve8.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            pve8.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve8.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve8.appendFormalArg(farg);

            pve8ID = db.addPredVE(pve8);

            // get a copy of the databases version of pve8 with ids assigned
            pve8 = db.getPredVE(pve8ID);


            pve9 = new PredicateVocabElement(db, "pve9");

            farg = new NominalFormalArg(db, "<nominal>");
            pve9.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve9.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve9.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve9.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve9.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            pve9.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve9.appendFormalArg(farg);

            pve9ID = db.addPredVE(pve9);

            // get a copy of the databases version of pve9 with ids assigned
            pve9 = db.getPredVE(pve9ID);


            pve10 = new PredicateVocabElement(db, "pve10");

            farg = new IntFormalArg(db, "<int>");
            pve10.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve10.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve10.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve10.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve10.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve10.appendFormalArg(farg);
            farg = new FloatFormalArg(db, "<float>");
            pve10.appendFormalArg(farg);

            pve10ID = db.addPredVE(pve10);

            // get a copy of the databases version of pve10 with ids assigned
            pve10 = db.getPredVE(pve10ID);


            pve11 = new PredicateVocabElement(db, "pve11");

            farg = new FloatFormalArg(db, "<float>");
            pve11.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve11.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve11.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve11.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve11.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve11.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve11.appendFormalArg(farg);

            pve11ID = db.addPredVE(pve11);

            // get a copy of the databases version of pve11 with ids assigned
            pve11 = db.getPredVE(pve11ID);


            pve12 = new PredicateVocabElement(db, "pve12");

            farg = new UnTypedFormalArg(db, "<arg1>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg3>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg4>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg5>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg6>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg7>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg8>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg9>");
            pve12.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg10>");
            pve12.appendFormalArg(farg);

            pve12ID = db.addPredVE(pve12);

            // get a copy of the databases version of pve12 with ids assigned
            pve12 = db.getPredVE(pve12ID);

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
             ( pve2 == null ) ||
             ( pve2ID == DBIndex.INVALID_ID ) ||
             ( pve3 == null ) ||
             ( pve3ID == DBIndex.INVALID_ID ) ||
             ( pve4 == null ) ||
             ( pve4ID == DBIndex.INVALID_ID ) ||
             ( pve5 == null ) ||
             ( pve5ID == DBIndex.INVALID_ID ) ||
             ( pve6 == null ) ||
             ( pve6ID == DBIndex.INVALID_ID ) ||
             ( pve7 == null ) ||
             ( pve7ID == DBIndex.INVALID_ID ) ||
             ( pve8 == null ) ||
             ( pve8ID == DBIndex.INVALID_ID ) ||
             ( pve9 == null ) ||
             ( pve9ID == DBIndex.INVALID_ID ) ||
             ( pve10 == null ) ||
             ( pve10ID == DBIndex.INVALID_ID ) ||
             ( pve11 == null ) ||
             ( pve11ID == DBIndex.INVALID_ID ) ||
             ( pve12 == null ) ||
             ( pve12ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }

                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0ID not initialized.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve2ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve2ID not initialized.\n");
                }

                if ( pve3 == null )
                {
                    outStream.print("creation of pve3 failed.\n");
                }

                if ( pve3ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve3ID not initialized.\n");
                }

                if ( pve4 == null )
                {
                    outStream.print("creation of pve3 failed.\n");
                }

                if ( pve4ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pv4ID not initialized.\n");
                }

                if ( pve5 == null )
                {
                    outStream.print("creation of pve5 failed.\n");
                }

                if ( pve5ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve5ID not initialized.\n");
                }

                if ( pve6 == null )
                {
                    outStream.print("creation of pve6 failed.\n");
                }

                if ( pve6ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve6ID not initialized.\n");
                }

                if ( pve7 == null )
                {
                    outStream.print("creation of pve7 failed.\n");
                }

                if ( pve7ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve7ID not initialized.\n");
                }

                if ( pve8 == null )
                {
                    outStream.print("creation of pve8 failed.\n");
                }

                if ( pve8ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve8ID not initialized.\n");
                }

                if ( pve9 == null )
                {
                    outStream.print("creation of pve9 failed.\n");
                }

                if ( pve9ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve9ID not initialized.\n");
                }

                if ( pve10 == null )
                {
                    outStream.print("creation of pve10 failed.\n");
                }

                if ( pve10ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve10ID not initialized.\n");
                }

                if ( pve11 == null )
                {
                    outStream.print("creation of pve11 failed.\n");
                }

                if ( pve11ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve11ID not initialized.\n");
                }

                if ( pve12 == null )
                {
                    outStream.print("creation of pve12 failed.\n");
                }

                if ( pve12ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve12ID not initialized.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            SystemErrorExceptionString);
                }
            }
        }

        // Start with a set of tests to verify that an argument list is
        // converted properly when the pveID of an instance of Predicate
        // is changed.
        //
        // Start by creating the necessary set of test instances of Predicate.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            String testString0 = "pve4(1.000000, 2, a_nominal, " +
                                      "pve0(<arg1>, <arg2>), " +
                                      "\"q-string\", 00:00:00:000, <untyped>)";
            String testDBString0 =
                "(predicate (id 0) " +
                           "(predID 13) " +
                           "(predName pve4) " +
                           "(varLen false) " +
                           "(argList ((FloatDataValue (id 0) " +
                                                     "(itsFargID 14) " +
                                                     "(itsFargType FLOAT) " +
                                                     "(itsCellID 0) " +
                                                     "(itsValue 1.0) " +
                                                     "(subRange false) " +
                                                     "(minVal 0.0) " +
                                                     "(maxVal 0.0)), " +
                                     "(IntDataValue (id 0) " +
                                                   "(itsFargID 15) " +
                                                   "(itsFargType INTEGER) " +
                                                   "(itsCellID 0) " +
                                                   "(itsValue 2) " +
                                                   "(subRange false) " +
                                                   "(minVal 0) " +
                                                   "(maxVal 0)), " +
                                     "(NominalDataValue (id 0) " +
                                                       "(itsFargID 16) " +
                                                       "(itsFargType NOMINAL) " +
                                                       "(itsCellID 0) " +
                                                       "(itsValue a_nominal) " +
                                                       "(subRange false)), " +
                                     "(PredDataValue (id 0) " +
                                       "(itsFargID 17) " +
                                       "(itsFargType PREDICATE) " +
                                       "(itsCellID 0) " +
                                       "(itsValue " +
                                         "(predicate (id 0) " +
                                            "(predID 1) " +
                                            "(predName pve0) " +
                                             "(varLen false) " +
                                             "(argList " +
                                               "((UndefinedDataValue (id 0) " +
                                                   "(itsFargID 2) " +
                                                   "(itsFargType UNTYPED) " +
                                                   "(itsCellID 0) " +
                                                   "(itsValue <arg1>) " +
                                                   "(subRange false)), " +
                                                 "(UndefinedDataValue (id 0) " +
                                                   "(itsFargID 3) " +
                                                   "(itsFargType UNTYPED) " +
                                                   "(itsCellID 0) " +
                                                   "(itsValue <arg2>) " +
                                                   "(subRange false))))))) " +
                                             "(subRange false)), " +
                                     "(QuoteStringDataValue (id 0) " +
                                                 "(itsFargID 18) " +
                                                 "(itsFargType QUOTE_STRING) " +
                                                 "(itsCellID 0) " +
                                                 "(itsValue q-string) " +
                                                 "(subRange false)), " +
                                     "(TimeStampDataValue (id 0) " +
                                               "(itsFargID 19) " +
                                               "(itsFargType TIME_STAMP) " +
                                               "(itsCellID 0) " +
                                               "(itsValue (60,00:00:00:000)) " +
                                               "(subRange false)), " +
                                     "(UndefinedDataValue (id 0) " +
                                               "(itsFargID 20) " +
                                               "(itsFargType UNTYPED) " +
                                               "(itsCellID 0) " +
                                               "(itsValue <untyped>) " +
                                               "(subRange false))))))";
            String testString1 = "pve3(99)";
            String testDBString1 = "(predicate (id 0) " +
                                       "(predID 11) " +
                                       "(predName pve3) " +
                                       "(varLen true) " +
                                       "(argList ((IntDataValue (id 0) " +
                                                      "(itsFargID 12) " +
                                                      "(itsFargType UNTYPED) " +
                                                      "(itsCellID 0) " +
                                                      "(itsValue 99) " +
                                                      "(subRange false) " +
                                                      "(minVal 0) " +
                                                      "(maxVal 0))))))";
            String testString2 = "pve12(<arg1>, <arg2>, <arg3>, <arg4>, " +
                                       "<arg5>, <arg6>, <arg7>, <arg8>, " +
                                       "<arg9>, <arg10>)";
            String testDBString2 =
                "(predicate (id 0) " +
                            "(predID 77) " +
                            "(predName pve12) " +
                            "(varLen false) " +
                            "(argList ((UndefinedDataValue (id 0) " +
                                         "(itsFargID 78) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg1>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 79) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg2>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 80) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg3>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 81) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg4>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 82) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg5>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 83) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg6>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 84) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg7>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 85) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg8>) " +
                                         "(subRange false)), " +
                                      "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 86) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg9>) " +
                                         "(subRange false)), " +
                                       "(UndefinedDataValue (id 0) " +
                                         "(itsFargID 87) " +
                                         "(itsFargType UNTYPED) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <arg10>) " +
                                         "(subRange false))))))";

            try
            {
                argList0 = new Vector<DataValue>();

                fargID = pve4.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                argList0.add(arg);
                fargID = pve4.getFormalArg(1).getID();
                arg = new IntDataValue(db, fargID, 2);
                argList0.add(arg);
                fargID = pve4.getFormalArg(2).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                argList0.add(arg);
                fargID = pve4.getFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
                argList0.add(arg);
                fargID = pve4.getFormalArg(4).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                argList0.add(arg);
                fargID = pve4.getFormalArg(5).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks()));
                argList0.add(arg);
                fargID = pve4.getFormalArg(6).getID();
                arg = new UndefinedDataValue(db, fargID);
                argList0.add(arg);

                pred0  = new Predicate(db, pve4ID, argList0);
                pred1  = new Predicate(db, pve4ID, argList0);
                pred2  = new Predicate(db, pve4ID, argList0);
                pred3  = new Predicate(db, pve4ID, argList0);
                pred4  = new Predicate(db, pve4ID, argList0);
                pred5  = new Predicate(db, pve4ID, argList0);
                pred6  = new Predicate(db, pve4ID, argList0);
                pred7  = new Predicate(db, pve4ID, argList0);
                pred8  = new Predicate(db, pve4ID, argList0);
                pred9  = new Predicate(db, pve4ID, argList0);
                pred12 = new Predicate(db, pve4ID, argList0);

                argList1 = new Vector<DataValue>();

                fargID = pve3.getFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 99);
                argList1.add(arg);

                pred10 = new Predicate(db, pve3ID, argList1);

                pred11 = new Predicate(db, pve12ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( argList0 == null ) ||
                 ( argList0.size() != 7 ) ||
                 ( pred0 == null ) ||
                 ( pred1 == null ) ||
                 ( pred2 == null ) ||
                 ( pred3 == null ) ||
                 ( pred4 == null ) ||
                 ( pred5 == null ) ||
                 ( pred6 == null ) ||
                 ( pred7 == null ) ||
                 ( pred8 == null ) ||
                 ( pred9 == null ) ||
                 ( pred10 == null ) ||
                 ( pred11 == null ) ||
                 ( pred12 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( argList0 == null )
                    {
                        outStream.print("argList0 allocation failed.\n");
                    }
                    else if ( argList0.size() != 7 )
                    {
                        outStream.printf("unexpected argList0.size(): %d (7).\n",
                                         argList0.size());
                    }

                    if ( ( pred0 == null ) ||
                         ( pred1 == null ) ||
                         ( pred2 == null ) ||
                         ( pred3 == null ) ||
                         ( pred4 == null ) ||
                         ( pred5 == null ) ||
                         ( pred6 == null ) ||
                         ( pred7 == null ) ||
                         ( pred8 == null ) ||
                         ( pred9 == null ) ||
                         ( pred10 == null ) ||
                         ( pred11 == null ) ||
                         ( pred12 == null ) )
                    {
                        outStream.print("one or more Predicate allocation(s) " +
                                        "failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("test predicate allocation failed " +
                                        "to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test predicate allocation threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         SystemErrorExceptionString);
                    }
                }
            }
            else if ( ( pred0.toString().compareTo(testString0) != 0 ) ||
                      ( pred1.toString().compareTo(testString0) != 0 ) ||
                      ( pred2.toString().compareTo(testString0) != 0 ) ||
                      ( pred3.toString().compareTo(testString0) != 0 ) ||
                      ( pred4.toString().compareTo(testString0) != 0 ) ||
                      ( pred5.toString().compareTo(testString0) != 0 ) ||
                      ( pred6.toString().compareTo(testString0) != 0 ) ||
                      ( pred7.toString().compareTo(testString0) != 0 ) ||
                      ( pred8.toString().compareTo(testString0) != 0 ) ||
                      ( pred9.toString().compareTo(testString0) != 0 ) ||
                      ( pred12.toString().compareTo(testString0) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred?.toString)(): \"%s\"\n",
                                     pred0.toString());
                }
            }
            else if ( ( pred0.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred1.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred2.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred3.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred4.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred5.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred6.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred7.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred8.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred9.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred12.toDBString().compareTo(testDBString0) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred?.toDBString)(): \"%s\"\n",
                                     pred0.toDBString());
                }
            }
            else if ( pred10.toString().compareTo(testString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred10.toString)(): \"%s\"\n",
                                     pred10.toString());
                }
            }
            else if ( pred10.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred10.toDBString)(): \"%s\"\n",
                                     pred10.toDBString());
                }
            }
            else if ( pred11.toString().compareTo(testString2) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred11.toString)(): \"%s\"\n",
                                     pred11.toString());
                }
            }
            else if ( pred11.toDBString().compareTo(testDBString2) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred11.toDBString)(): \"%s\"\n",
                                     pred11.toDBString());
                }
            }
        }


        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            // untyped, float, int, nominal, pred, q-string, timestamp
            String testString1 =
                    "pve5(1.000000, 2.000000, 0, , (), \"\", 00:00:00:000)";
            // timestamp, untyped, float, int, nominal, pred, q-string
            String testString2 =
                    "pve6(00:00:00:000, 2, 0.000000, 0, q-string, (), \"\")";
            // q-string, timestamp, untyped, float, int, nominal, pred
            String testString3 =
                    "pve7(\"\", 00:00:00:002, a_nominal, 0.000000, 0, , ())";
            // pred, q-string, timestamp, untyped, float, int, nominal
            String testString4 =
                    "pve8((), \"\", 00:00:00:000, pve0(<arg1>, <arg2>), 0.000000, 0, )";
            // nominal, pred, q-string, timestamp, untyped, float, int
            String testString5 =
                    "pve9(, (), \"a_nominal\", 00:00:00:000, \"q-string\", 0.000000, 0)";
            // int, nominal, pred, q-string, timestamp, untyped, float
            String testString6 =
                    "pve10(1, , (), \"\", 00:00:00:000, 00:00:00:000, 0.000000)";
            // float, int, nominal, pred, q-string, timestamp, untyped
            String testString7 =
                    "pve11(1.000000, 2, a_nominal, pve0(<arg1>, <arg2>), " +
                          "\"q-string\", 00:00:00:000, <untyped>)";
            String testString8 = "pve3(1.000000)";
            String testString9 = "pve1(1, 2)";
            String testString10 =
                    "pve4(99.000000, 0, , (), \"\", 00:00:00:000, <untyped>)";

            try
            {
                pred1.setPredID(pve5ID, true);
                pred2.setPredID(pve6ID, true);
                pred3.setPredID(pve7ID, true);
                pred4.setPredID(pve8ID, true);
                pred5.setPredID(pve9ID, true);
                pred6.setPredID(pve10ID, true);
                pred7.setPredID(pve11ID, true);

                pred8.setPredID(pve3ID, true);
                pred9.setPredID(pve1ID, true);

                pred10.setPredID(pve4ID, true);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test of setPredID(?, true) " +
                                        "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test of setPredID(?, true) threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         SystemErrorExceptionString);
                    }
                }
            } else if ( ( pred1.toString().compareTo(testString1) != 0 ) ||
                        ( pred2.toString().compareTo(testString2) != 0 ) ||
                        ( pred3.toString().compareTo(testString3) != 0 ) ||
                        ( pred4.toString().compareTo(testString4) != 0 ) ||
                        ( pred5.toString().compareTo(testString5) != 0 ) ||
                        ( pred6.toString().compareTo(testString6) != 0 ) ||
                        ( pred7.toString().compareTo(testString7) != 0 ) ||
                        ( pred8.toString().compareTo(testString8) != 0 ) ||
                        ( pred9.toString().compareTo(testString9) != 0 ) ||
                        ( pred10.toString().compareTo(testString10) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred1.toString().compareTo(testString1) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred1.toString(): \"%s\".\n",
                                pred1.toString());
                    }

                    if ( pred2.toString().compareTo(testString2) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred2.toString(): \"%s\".\n",
                                pred2.toString());
                    }

                    if ( pred3.toString().compareTo(testString3) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred3.toString(): \"%s\".\n",
                                pred3.toString());
                    }

                    if ( pred4.toString().compareTo(testString4) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred4.toString(): \"%s\".\n",
                                pred4.toString());
                    }

                    if ( pred5.toString().compareTo(testString5) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred5.toString(): \"%s\".\n",
                                pred5.toString());
                    }

                    if ( pred6.toString().compareTo(testString6) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred6.toString(): \"%s\".\n",
                                pred6.toString());
                    }

                    if ( pred7.toString().compareTo(testString7) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred7.toString(): \"%s\".\n",
                                pred7.toString());
                    }

                    if ( pred8.toString().compareTo(testString8) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred8.toString(): \"%s\".\n",
                                pred8.toString());
                    }

                    if ( pred9.toString().compareTo(testString9) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred9.toString(): \"%s\".\n",
                                pred9.toString());
                    }

                    if ( pred10.toString().compareTo(testString10) != 0 )
                    {
                        outStream.printf(
                                "Unexpected pred10.toString(): \"%s\".\n",
                                pred10.toString());
                    }
                }
            }
        }

        // Verify that the getArg() and replaceArg() methods perform as
        // expected.

        /* first a float argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 0.0);
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(fdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(pred12,
                                                    floatArg0,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "floatArgo");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      intArg0,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "intArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      nominalArg0,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "nominalArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      predArg0,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      qsArg0,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "qsArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      tsArg0,
                                                      0,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    0,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }


        /* next an integer argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(1).getID();
                arg = new IntDataValue(db, fargID, 0);
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(idv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(pred12,
                                                      floatArg0,
                                                      1,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "floatArgo");

                failures += VerifyArgListAssignment(pred12,
                                                    intArg0,
                                                    1,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "intArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      nominalArg0,
                                                      1,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "nominalArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      predArg0,
                                                      1,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      1,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      qsArg0,
                                                      1,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "qsArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      tsArg0,
                                                      1,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    1,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }


        /* next an nominal argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(2).getID();
                arg = new NominalDataValue(db, fargID, "just_some_nominal");
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(ndv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(pred12,
                                                      floatArg0,
                                                      2,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "floatArgo");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      intArg0,
                                                      2,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "intArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    nominalArg0,
                                                    2,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "nominalArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      predArg0,
                                                      2,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      2,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      qsArg0,
                                                      2,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "qsArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      tsArg0,
                                                      2,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    2,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }


        /* next an predicate argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(pdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(pred12,
                                                      floatArg0,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "floatArgo");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      intArg0,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "intArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      nominalArg0,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "nominalArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    predArg0,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      qsArg0,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "qsArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      tsArg0,
                                                      3,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    3,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }


        /* next an quote string argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(4).getID();
                arg = new QuoteStringDataValue(db, fargID, "some quote string");
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(qsdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(pred12,
                                                      floatArg0,
                                                      4,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "floatArgo");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      intArg0,
                                                      4,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "intArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      nominalArg0,
                                                      4,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "nominalArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      predArg0,
                                                      4,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      4,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    qsArg0,
                                                    4,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "qsArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      tsArg0,
                                                      4,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    4,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }


        /* next a time stamp argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(5).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks(), 360));
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(tsdv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAsgnmntFails(pred12,
                                                      floatArg0,
                                                      5,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "floatArgo");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      intArg0,
                                                      5,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "intArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      nominalArg0,
                                                      5,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "nominalArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      predArg0,
                                                      5,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      5,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      qsArg0,
                                                      5,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "qsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    tsArg0,
                                                    5,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    5,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }


        /* finally, an undefined argument */
        if ( failures == 0 )
        {
            arg = null;
            floatArg0 = null;
            intArg0 = null;
            nominalArg0 = null;
            predArg0 = null;
            textArg0 = null;
            qsArg0 = null;
            tsArg0 = null;
            undefArg0 = null;
            completed = false;
            threwSystemErrorException = false;
            try
            {
                fargID = pve4.getFormalArg(6).getID();
                arg = new UndefinedDataValue(db, fargID);
                floatArg0 = new FloatDataValue(db);
                floatArg0.setItsValue(1066.0);
                intArg0 = new IntDataValue(db);
                intArg0.setItsValue(1903);
                nominalArg0 = new NominalDataValue(db);
                nominalArg0.setItsValue("yan");
                predArg0 = new PredDataValue(db);
                predArg0.setItsValue(new Predicate(db, pve3ID));
                textArg0 = new TextStringDataValue(db);
                textArg0.setItsValue("yats");
                qsArg0 = new QuoteStringDataValue(db);
                qsArg0.setItsValue("yaqs");
                tsArg0 = new TimeStampDataValue(db);
                tsArg0.setItsValue(new TimeStamp(db.getTicks(), 60));
                undefArg0 = new UndefinedDataValue(db);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( arg == null ) ||
                 ( floatArg0 == null ) ||
                 ( intArg0 == null ) ||
                 ( nominalArg0 == null ) ||
                 ( predArg0 == null ) ||
                 ( textArg0 == null ) ||
                 ( qsArg0 == null ) ||
                 ( tsArg0 == null ) ||
                 ( undefArg0 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    String testTag = "(udv)";

                    if ( arg == null )
                    {
                        outStream.printf("%s: Allocation of arg failed.\n",
                                         testTag);
                    }

                    if ( floatArg0 == null )
                    {
                        outStream.printf("%s: Allocation of floatArg0 failed.\n",
                                         testTag);
                    }

                    if ( intArg0 == null )
                    {
                        outStream.printf("%s: Allocation of intArg0 failed.\n",
                                         testTag);
                    }

                    if ( nominalArg0 == null )
                    {
                        outStream.printf(
                                "%s: Allocation of nominalArg0 failed.\n",
                                testTag);
                    }

                    if ( predArg0 == null )
                    {
                        outStream.printf("%s: Allocation of predArg0 failed.\n",
                                         testTag);
                    }

                    if ( textArg0 == null )
                    {
                        outStream.printf("%s: Allocation of textArg0 failed.\n",
                                         testTag);
                    }

                    if ( qsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of qsArg0 failed.\n",
                                         testTag);
                    }

                    if ( tsArg0 == null )
                    {
                        outStream.printf("%s: Allocation of tsArg0 failed.\n",
                                         testTag);
                    }

                    if ( undefArg0 == null )
                    {
                        outStream.printf("%s: Allocation of undefArg0 failed.\n",
                                         testTag);
                    }

                    if ( ! completed )
                    {
                        outStream.printf("%s: arg allocation did not complete.\n",
                                         testTag);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "%s: Arg alloc threw system error: \"%s\".\n",
                                testTag, SystemErrorExceptionString);
                    }
                }
            }
            else
            {
                failures += VerifyArgListAssignment(pred12,
                                                    floatArg0,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "floatArgo");

                failures += VerifyArgListAssignment(pred12,
                                                    intArg0,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "intArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    nominalArg0,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "nominalArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    predArg0,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "predArg0");

                failures += VerifyArgListAsgnmntFails(pred12,
                                                      textArg0,
                                                      6,
                                                      outStream,
                                                      verbose,
                                                      "pred12",
                                                      "textArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    qsArg0,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "qsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    tsArg0,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "tsArg0");

                failures += VerifyArgListAssignment(pred12,
                                                    arg,
                                                    6,
                                                    outStream,
                                                    verbose,
                                                    "pred12",
                                                    "arg");
            }
        }

        /* In theory, the above battery of tests should cover everything.
         * However, lets throw in a few more random tests on the likely
         * chance that theory has missed a few cases.
         */

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {   // float, int, nominal, pred, q-string, timestamp, untyped

                floatArg0 = (FloatDataValue)pred0.getArg(0);
                floatArg1 =
                        new FloatDataValue(db, floatArg0.getItsFargID(), 10.0);
                pred0.replaceArg(0, floatArg1);

                intArg0 = (IntDataValue)pred0.getArg(1);
                intArg1 = new IntDataValue(db, intArg0.getItsFargID(), 20);
                pred0.replaceArg(1, intArg1);

                nominalArg0 = (NominalDataValue)pred0.getArg(2);
                nominalArg1 = new NominalDataValue(db,
                                                   nominalArg0.getItsFargID(),
                                                   "another_nominal");
                pred0.replaceArg(2, nominalArg1);

                predArg0 = (PredDataValue)pred0.getArg(3);
                predArg1 = new PredDataValue(db, predArg0.getItsFargID(),
                                             pred9);
                pred0.replaceArg(3, predArg1);

                qsArg0 = (QuoteStringDataValue)pred0.getArg(4);
                qsArg1 = new QuoteStringDataValue(db, qsArg0.getItsFargID(),
                                                  "another_qs");
                pred0.replaceArg(4, qsArg1);

                tsArg0 = (TimeStampDataValue)pred0.getArg(5);
                tsArg1 = new TimeStampDataValue(db, tsArg0.getItsFargID(),
                                           new TimeStamp(db.getTicks(), 3600));
                pred0.replaceArg(5, tsArg1);

                undefArg0 = (UndefinedDataValue)pred0.getArg(6);
                intArg2 = new IntDataValue(db, undefArg0.getItsFargID(), 30);
                pred0.replaceArg(6, intArg2);



                fargID = pve12.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 33.0);
                pred11.replaceArg(0, arg);

                fargID = pve12.getFormalArg(1).getID();
                arg = new IntDataValue(db, fargID, 44);
                pred11.replaceArg(1, arg);

                fargID = pve12.getFormalArg(2).getID();
                arg = new NominalDataValue(db, fargID, "what_ever");
                pred11.replaceArg(2, arg);

                fargID = pve12.getFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve0ID));
                pred11.replaceArg(3, arg);

                fargID = pve12.getFormalArg(4).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string-3");
                pred11.replaceArg(4, arg);

                fargID = pve12.getFormalArg(5).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks()));
                pred11.replaceArg(5, arg);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
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
                                "get/replace arg test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("get/replace arg test threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         SystemErrorExceptionString);
                    }
                }
            }
            else if ( pred0.toString().compareTo("pve4(10.000000, 20, " +
                              "another_nominal, pve1(1, 2), \"another_qs\", " +
                              "00:01:00:000, 30)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                        outStream.printf(
                                "Unexpected pred0.toString(): \"%s\".\n",
                                pred0.toString());
                }
            }
            else if ( pred11.toString().compareTo("pve12(33.000000, 44, what_ever, " +
                              "pve0(<arg1>, <arg2>), \"q-string-3\", " +
                              "00:00:00:000, <arg7>, <arg8>, <arg9>, " +
                              "<arg10>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                        outStream.printf(
                                "Unexpected pred11.toString() - 1: \"%s\".\n",
                                pred11.toString());
                }
            }
        }

        // finally, verify that getArg() and replaceArg() fail on invalid input.

        // create a farg ID mis-match

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                fargID = pve12.getFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                pred11.replaceArg(6, arg);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;


                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "replaceArg() with fargID mismatch completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("replaceArg() with fargID mismatch " +
                                        "didn't throw a SystemErrorException.\n");
                    }
                }
            }
            else if ( pred11.toString().compareTo("pve12(33.000000, 44, what_ever, " +
                              "pve0(<arg1>, <arg2>), \"q-string-3\", " +
                              "00:00:00:000, <arg7>, <arg8>, <arg9>, " +
                              "<arg10>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                        outStream.printf(
                                "Unexpected pred11.toString() - 2: \"%s\".\n",
                                pred11.toString());
                }
            }
        }


        // negative index

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                fargID = pve12.getFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                pred11.replaceArg(-1, arg);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;


                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "replaceArg() with -1 index completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("replaceArg() with -1 index " +
                                        "didn't throw a SystemErrorException.\n");
                    }
                }
            }
            else if ( pred11.toString().compareTo("pve12(33.000000, 44, what_ever, " +
                              "pve0(<arg1>, <arg2>), \"q-string-3\", " +
                              "00:00:00:000, <arg7>, <arg8>, <arg9>, " +
                              "<arg10>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                        outStream.printf(
                                "Unexpected pred11.toString() - 3: \"%s\".\n",
                                pred11.toString());
                }
            }
        }

        // positive unused index

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            try
            {
                fargID = pve12.getFormalArg(5).getID();
                arg = new IntDataValue(db, fargID, 88);
                pred11.replaceArg(10, arg);
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;


                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "replaceArg() with index 10 completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("replaceArg() with index 10 " +
                                        "didn't throw a SystemErrorException.\n");
                    }
                }
            }
            else if ( pred11.toString().compareTo("pve12(33.000000, 44, what_ever, " +
                              "pve0(<arg1>, <arg2>), \"q-string-3\", " +
                              "00:00:00:000, <arg7>, <arg8>, <arg9>, " +
                              "<arg10>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                        outStream.printf(
                                "Unexpected pred11.toString() - 4: \"%s\".\n",
                                pred11.toString());
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
    } /* Predicate::TestArgListManagement() */


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
    @Test
    public void TestCopyConstructor() throws SystemErrorException {
        final String mName = "Predicate::TestCopyConstructor(): ";
        String testBanner =
            "Testing copy constructor for class Predicate                     ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int i;
        int failures = 0;
        long pveID1 = DBIndex.INVALID_ID;
        long pveID2 = DBIndex.INVALID_ID;
        long fargID;
        Database db = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        FormalArgument farg = null;
        Vector<DataValue> argList;
        Vector<DataValue> argList0 = null;
        Vector<DataValue> argList1 = null;
        DataValue arg = null;
        Predicate pred   = null;
        Predicate pred0  = null;
        Predicate pred1  = null;
        Predicate pred1a = null;
        Predicate pred2  = null;
        Predicate pred2a = null;
        Predicate pred0_copy  = null;
        Predicate pred1_copy  = null;
        Predicate pred1a_copy = null;
        Predicate pred2_copy  = null;
        Predicate pred2a_copy = null;
        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // setup the test:
        try
        {
            db = new ODBCDatabase();

            pred0 = new Predicate(db);

            pve1 = new PredicateVocabElement(db, "test1");

            farg = new FloatFormalArg(db, "<float>");
            pve1.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve1.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve1.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve1.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve1.appendFormalArg(farg);

            pveID1 = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pveID1);

            pred1 = new Predicate(db, pveID1);

            argList0 = new Vector<DataValue>();

            fargID = pve1.getFormalArg(0).getID();
            arg = new FloatDataValue(db, fargID, 1.0);
            argList0.add(arg);
            fargID = pve1.getFormalArg(1).getID();
            arg = new IntDataValue(db, fargID, 2);
            argList0.add(arg);
            fargID = pve1.getFormalArg(2).getID();
            arg = new NominalDataValue(db, fargID, "a_nominal");
            argList0.add(arg);
            fargID = pve1.getFormalArg(3).getID();
            arg = new PredDataValue(db, fargID, new Predicate(db, pveID1));
            argList0.add(arg);
            fargID = pve1.getFormalArg(4).getID();
            arg = new QuoteStringDataValue(db, fargID, "q-string");
            argList0.add(arg);
            fargID = pve1.getFormalArg(5).getID();
            arg = new TimeStampDataValue(db, fargID,
                                         new TimeStamp(db.getTicks()));
            argList0.add(arg);
            fargID = pve1.getFormalArg(6).getID();
            arg = new UndefinedDataValue(db, fargID);
            argList0.add(arg);

            pred1a = new Predicate(db, pveID1, argList0);


            pve2 = new PredicateVocabElement(db, "test2");

            farg = new UnTypedFormalArg(db, "<arg>");
            pve2.appendFormalArg(farg);

            pve2.setVarLen(true);

            pveID2 = db.addPredVE(pve2);

            // get a copy of the databases version of pve2 with ids assigned
            pve2 = db.getPredVE(pveID2);

            pred2 = new Predicate(db, pveID2);

            argList1 = new Vector<DataValue>();

            fargID = pve2.getFormalArg(0).getID();
            arg = new IntDataValue(db, fargID, 43);
            argList1.add(arg);

            pred2a = new Predicate(db, pveID2, argList1);
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pred0 == null ) ||
             ( pve1 == null ) ||
             ( pveID1 == DBIndex.INVALID_ID ) ||
             ( argList0 == null ) ||
             ( argList0.size() != 7 ) ||
             ( pred1 == null ) ||
             ( pred1a == null ) ||
             ( pve2 == null ) ||
             ( pveID2 == DBIndex.INVALID_ID ) ||
             ( argList1 == null ) ||
             ( argList1.size() != 1 ) ||
             ( pred2 == null ) ||
             ( pred2a == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }

                if ( pred0 == null )
                {
                    outStream.print("creation of pred0 failed.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pveID1 == DBIndex.INVALID_ID )
                {
                    outStream.print("insertion of pve1 failed.\n");
                }

                if ( argList0 == null )
                {
                    outStream.print("creation of argList0 failed.\n");
                }
                else if ( argList0.size() != 7 )
                {
                    outStream.print("unexpected argList0 length.\n");
                }

                if ( pred1 == null )
                {
                    outStream.print("new Predicate(db, pveID1) " +
                                    "returned null.\n");
                }

                if ( pred1a == null )
                {
                    outStream.print("new Predicate(db, pveID1, argList0) " +
                                    "returned null.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pveID2 == DBIndex.INVALID_ID )
                {
                    outStream.print("insertion of pve2 failed.\n");
                }

                if ( argList1 == null )
                {
                    outStream.print("creation of argList1 failed.\n");
                }
                else if ( argList1.size() != 1 )
                {
                    outStream.print("unexpected argList1 length.\n");
                }

                if ( pred2 == null )
                {
                    outStream.print("new Predicate(db, pveID2) " +
                                    "returned null.\n");
                }

                if ( pred2a == null )
                {
                    outStream.print("new Predicate(db, pveID2, argList1) " +
                                    "returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("test setup threw a SystemErrorException:" +
                                     "\"%s\".\n", SystemErrorExceptionString);
                }
            }
        }


        // test setup complete -- now for the test proper

        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            completed = false;

            try
            {
                pred0_copy  = new Predicate(pred0);
                pred1_copy  = new Predicate(pred1);
                pred1a_copy = new Predicate(pred1a);
                pred2_copy  = new Predicate(pred2);
                pred2a_copy = new Predicate(pred2a);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( pred0_copy == null ) ||
                 ( pred1_copy == null ) ||
                 ( pred1a_copy == null ) ||
                 ( pred2_copy == null ) ||
                 ( pred2a_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred0_copy == null )
                    {
                        outStream.print("new Predicate(pred0) returned null.\n");
                    }

                    if ( pred1_copy == null )
                    {
                        outStream.print("new Predicate(pred1) returned null.\n");
                    }

                    if ( pred1a_copy == null )
                    {
                        outStream.print("new Predicate(pred1a) returned null.\n");
                    }

                    if ( pred2_copy == null )
                    {
                        outStream.print("new Predicate(pred2) returned null.\n");
                    }

                    if ( pred2a_copy == null )
                    {
                        outStream.print("new Predicate(pred2a) returned null.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("copy constructors failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                            "a copy constructor threw a SystemErrorException:" +
                            "\"%s\".\n", SystemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0_copy.getDB() != db ) ||
                 ( pred1_copy.getDB() != db ) ||
                 ( pred1a_copy.getDB() != db ) ||
                 ( pred2_copy.getDB() != db ) ||
                 ( pred2a_copy.getDB() != db ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("A copy refers to an unexpected db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0_copy.getPveID() != DBIndex.INVALID_ID ) ||
                 ( pred1_copy.getPveID() != pveID1 ) ||
                 ( pred1a_copy.getPveID() != pveID1 ) ||
                 ( pred2_copy.getPveID() != pveID2 ) ||
                 ( pred2a_copy.getPveID() != pveID2 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "a copy refers to an unexpected pred ID.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0_copy.getPredName().compareTo("") != 0 ) ||
                 ( pred1_copy.getPredName().compareTo("test1") != 0 ) ||
                 ( pred1a_copy.getPredName().compareTo("test1") != 0 ) ||
                 ( pred2_copy.getPredName().compareTo("test2") != 0 ) ||
                 ( pred2a_copy.getPredName().compareTo("test2") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                       "a copy has an unexpected initial value of predName.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0_copy.argList != null ) ||
                 ( pred1_copy.argList == null ) ||
                 ( pred1_copy.argList == pred1.argList ) ||
                 ( pred1_copy.argList.size() != 7 ) ||
                 ( pred1a_copy.argList == null ) ||
                 ( pred1a_copy.argList == pred1.argList ) ||
                 ( pred1a_copy.argList.size() != 7 ) ||
                 ( pred2_copy.argList == null ) ||
                 ( pred2_copy.argList == pred2.argList ) ||
                 ( pred2_copy.argList.size() != 1 ) ||
                 ( pred2a_copy.argList == null ) ||
                 ( pred2a_copy.argList == pred2.argList ) ||
                 ( pred2a_copy.argList.size() != 1 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                       "a copy has an unexpected initial value of argList.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( pred0_copy.getVarLen() != false ) ||
                 ( pred1_copy.getVarLen() != false ) ||
                 ( pred1a_copy.getVarLen() != false ) ||
                 ( pred2_copy.getVarLen() != true ) ||
                 ( pred2a_copy.getVarLen() != true ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "a copy has an unexpected initial value of varLen.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0_copy.toString().compareTo("()") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0.toString(): \"%s\".\n",
                                     pred0_copy.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred0_copy.toDBString().compareTo(
                    "(predicate (id 0) (predID 0) (predName ) (varLen false) " +
                    "(argList ())))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred0.toDBString(): \"%s\".\n",
                                     pred0.toDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.toString().compareTo(
                    "test1(0.000000, 0, , (), \"\", 00:00:00:000, <untyped>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred1.toString(): \"%s\".\n",
                                     pred1.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1.toDBString().compareTo(
                 "(predicate (id 0) " +
                     "(predID 1) " +
                     "(predName test1) " +
                     "(varLen false) " +
                     "(argList ((FloatDataValue (id 0) " +
                                               "(itsFargID 2) " +
                                               "(itsFargType FLOAT) " +
                                               "(itsCellID 0) " +
                                               "(itsValue 0.0) " +
                                               "(subRange false) " +
                                               "(minVal 0.0) " +
                                               "(maxVal 0.0)), " +
                               "(IntDataValue (id 0) " +
                                             "(itsFargID 3) " +
                                             "(itsFargType INTEGER) " +
                                             "(itsCellID 0) " +
                                             "(itsValue 0) " +
                                             "(subRange false) " +
                                             "(minVal 0) " +
                                             "(maxVal 0)), " +
                               "(NominalDataValue (id 0) " +
                                              "(itsFargID 4) " +
                                              "(itsFargType NOMINAL) " +
                                              "(itsCellID 0) " +
                                              "(itsValue <null>) " +
                                              "(subRange false)), " +
                               "(PredDataValue (id 0) " +
                                            "(itsFargID 5) " +
                                            "(itsFargType PREDICATE) " +
                                            "(itsCellID 0) " +
                                            "(itsValue ()) " +
                                            "(subRange false)), " +
                               "(QuoteStringDataValue (id 0) " +
                                         "(itsFargID 6) " +
                                         "(itsFargType QUOTE_STRING) " +
                                         "(itsCellID 0) " +
                                         "(itsValue <null>) " +
                                         "(subRange false)), " +
                               "(TimeStampDataValue (id 0) " +
                                       "(itsFargID 7) " +
                                       "(itsFargType TIME_STAMP) " +
                                       "(itsCellID 0) " +
                                       "(itsValue (60,00:00:00:000)) " +
                                       "(subRange false)), " +
                               "(UndefinedDataValue (id 0) " +
                                        "(itsFargID 8) " +
                                        "(itsFargType UNTYPED) " +
                                        "(itsCellID 0) " +
                                        "(itsValue <untyped>) " +
                                        "(subRange false))))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1.toDBString(): \"%s\".\n",
                            pred1.toDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.toString().compareTo(
                    "test1(1.000000, 2, a_nominal, test1(0.000000, 0, , (), \"\", " +
                    "00:00:00:000, <untyped>), \"q-string\", 00:00:00:000, " +
                    "<untyped>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred1a.toString(): \"%s\".\n",
                                     pred1a.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred1a.toDBString().compareTo(
                 "(predicate (id 0) " +
                     "(predID 1) " +
                     "(predName test1) " +
                     "(varLen false) " +
                     "(argList ((FloatDataValue (id 0) " +
                                               "(itsFargID 2) " +
                                               "(itsFargType FLOAT) " +
                                               "(itsCellID 0) " +
                                               "(itsValue 1.0) " +
                                               "(subRange false) " +
                                               "(minVal 0.0) " +
                                               "(maxVal 0.0)), " +
                               "(IntDataValue (id 0) " +
                                             "(itsFargID 3) " +
                                             "(itsFargType INTEGER) " +
                                             "(itsCellID 0) " +
                                             "(itsValue 2) " +
                                             "(subRange false) " +
                                             "(minVal 0) " +
                                             "(maxVal 0)), " +
                               "(NominalDataValue (id 0) " +
                                              "(itsFargID 4) " +
                                              "(itsFargType NOMINAL) " +
                                              "(itsCellID 0) " +
                                              "(itsValue a_nominal) " +
                                              "(subRange false)), " +
                               "(PredDataValue (id 0) " +
                                   "(itsFargID 5) " +
                                   "(itsFargType PREDICATE) " +
                                   "(itsCellID 0) " +
                                   "(itsValue (predicate (id 0) " +
                                      "(predID 1) " +
                                      "(predName test1) " +
                                      "(varLen false) " +
                                      "(argList " +
                                          "((FloatDataValue (id 0) " +
                                              "(itsFargID 2) " +
                                              "(itsFargType FLOAT) " +
                                              "(itsCellID 0) " +
                                              "(itsValue 0.0) " +
                                              "(subRange false) " +
                                              "(minVal 0.0) " +
                                              "(maxVal 0.0)), " +
                                           "(IntDataValue (id 0) " +
                                               "(itsFargID 3) " +
                                               "(itsFargType INTEGER) " +
                                               "(itsCellID 0) " +
                                               "(itsValue 0) " +
                                               "(subRange false) " +
                                               "(minVal 0) " +
                                               "(maxVal 0)), " +
                                           "(NominalDataValue (id 0) " +
                                               "(itsFargID 4) " +
                                               "(itsFargType NOMINAL) " +
                                               "(itsCellID 0) " +
                                               "(itsValue <null>) " +
                                               "(subRange false)), " +
                                           "(PredDataValue (id 0) " +
                                               "(itsFargID 5) " +
                                               "(itsFargType PREDICATE) " +
                                               "(itsCellID 0) " +
                                               "(itsValue ()) " +
                                               "(subRange false)), " +
                                           "(QuoteStringDataValue (id 0) " +
                                               "(itsFargID 6) " +
                                               "(itsFargType QUOTE_STRING) " +
                                               "(itsCellID 0) " +
                                               "(itsValue <null>) " +
                                               "(subRange false)), " +
                                           "(TimeStampDataValue (id 0) " +
                                               "(itsFargID 7) " +
                                               "(itsFargType TIME_STAMP) " +
                                               "(itsCellID 0) " +
                                               "(itsValue (60,00:00:00:000)) " +
                                               "(subRange false)), " +
                                           "(UndefinedDataValue (id 0) " +
                                               "(itsFargID 8) " +
                                               "(itsFargType UNTYPED) " +
                                               "(itsCellID 0) " +
                                               "(itsValue <untyped>) " +
                                               "(subRange false))))))) " +
                                   "(subRange false)), " +
                               "(QuoteStringDataValue (id 0) " +
                                         "(itsFargID 6) " +
                                         "(itsFargType QUOTE_STRING) " +
                                         "(itsCellID 0) " +
                                         "(itsValue q-string) " +
                                         "(subRange false)), " +
                               "(TimeStampDataValue (id 0) " +
                                       "(itsFargID 7) " +
                                       "(itsFargType TIME_STAMP) " +
                                       "(itsCellID 0) " +
                                       "(itsValue (60,00:00:00:000)) " +
                                       "(subRange false)), " +
                               "(UndefinedDataValue (id 0) " +
                                        "(itsFargID 8) " +
                                        "(itsFargType UNTYPED) " +
                                        "(itsCellID 0) " +
                                        "(itsValue <untyped>) " +
                                        "(subRange false))))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred1a.toDBString(): \"%s\".\n",
                            pred1a.toDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred2.toString().compareTo("test2(<arg>)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred2.toString(): \"%s\".\n",
                                     pred2.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred2.toDBString().compareTo(
                "(predicate (id 0) " +
                           "(predID 9) " +
                           "(predName test2) " +
                           "(varLen true) " +
                           "(argList ((UndefinedDataValue (id 0) " +
                                                "(itsFargID 10) " +
                                                "(itsFargType UNTYPED) " +
                                                "(itsCellID 0) " +
                                                "(itsValue <arg>) " +
                                                "(subRange false))))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred2.toDBString(): \"%s\".\n",
                            pred2.toDBString());
                }
            }
        }


        if ( failures == 0 )
        {
            if ( pred2a.toString().compareTo("test2(43)") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred2a.toString(): \"%s\".\n",
                                     pred2a.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pred2a.toDBString().compareTo(
                "(predicate (id 0) " +
                           "(predID 9) " +
                           "(predName test2) " +
                           "(varLen true) " +
                           "(argList ((IntDataValue (id 0) " +
                                                   "(itsFargID 10) " +
                                                   "(itsFargType UNTYPED) " +
                                                   "(itsCellID 0) " +
                                                   "(itsValue 43) " +
                                                   "(subRange false) " +
                                                   "(minVal 0) " +
                                                   "(maxVal 0))))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected pred2a.toDBString(): \"%s\".\n",
                            pred2a.toDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            failures += VerifyPredicateCopy(pred0, pred0_copy, outStream,
                                            verbose, "pred0", "pred0_copy");

            failures += VerifyPredicateCopy(pred1, pred1_copy, outStream,
                                            verbose, "pred1", "pred1_copy");

            failures += VerifyPredicateCopy(pred1a, pred1a_copy, outStream,
                                            verbose, "pred1a", "pred1a_copy");

            failures += VerifyPredicateCopy(pred2, pred2_copy, outStream,
                                            verbose, "pred2", "pred2_copy");

            failures += VerifyPredicateCopy(pred2a, pred2a_copy, outStream,
                                            verbose, "pred2a", "pred2a_copy");
        }


        /* now verify that the copy constructor fails when passed an invalid
         * reference to a predicate.  For now, this just means passing in a
         * null.
         */
        pred0 = null;
        threwSystemErrorException = false;

        try
        {
            pred = new Predicate((Predicate)null);
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( pred != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( pred != null )
                {
                    outStream.print("new Predicate(null) != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new Predicate(null) " +
                                    "didn't throw a SystemErrorException.\n");
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
    } /* Predicate::TestCopyConstructor() */


    /**
     * TestToStringMethods()
     *
     * Run a battery of tests on the to string methods for this
     * class.
     *
     *                                               -- 10/29/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestToStringMethods() {
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures                 = 0;
        Database db                  = null;
        PredicateVocabElement pve    = null;
        PredicateVocabElement pve0   = null;
        PredicateVocabElement pve1   = null;
        long fargID                  = DBIndex.INVALID_ID;
        long pve0ID                  = DBIndex.INVALID_ID;
        long pve1ID                  = DBIndex.INVALID_ID;
        FormalArgument farg          = null;
        Vector<DataValue> argList0   = null;
        Vector<DataValue> argList1   = null;
        DataValue arg                = null;
        FloatDataValue floatArg0     = null;
        FloatDataValue floatArg1     = null;
        IntDataValue intArg0         = null;
        IntDataValue intArg1         = null;
        IntDataValue intArg2         = null;
        NominalDataValue nominalArg0 = null;
        NominalDataValue nominalArg1 = null;
        PredDataValue predArg0       = null;
        PredDataValue predArg1       = null;
        QuoteStringDataValue qsArg0  = null;
        QuoteStringDataValue qsArg1  = null;
        TimeStampDataValue tsArg0    = null;
        TimeStampDataValue tsArg1    = null;
        UndefinedDataValue undefArg0 = null;
        UndefinedDataValue undefArg1 = null;
        Predicate pred0              = null;
        Predicate pred1              = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database and pve's
        threwSystemErrorException = false;
        completed = false;

        try
        {
            db = new ODBCDatabase();

            pve0 = new PredicateVocabElement(db, "pve0");

            farg = new FloatFormalArg(db, "<float>");
            pve0.appendFormalArg(farg);
            farg = new IntFormalArg(db, "<int>");
            pve0.appendFormalArg(farg);
            farg = new NominalFormalArg(db, "<nominal>");
            pve0.appendFormalArg(farg);
            farg = new PredFormalArg(db, "<pred>");
            pve0.appendFormalArg(farg);
            farg = new QuoteStringFormalArg(db, "<qstring>");
            pve0.appendFormalArg(farg);
            farg = new TimeStampFormalArg(db, "<timestamp>");
            pve0.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<untyped>");
            pve0.appendFormalArg(farg);

            pve0ID = db.addPredVE(pve0);

            // get a copy of the databases version of pve4 with ids assigned
            pve0 = db.getPredVE(pve0ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve1.appendFormalArg(farg);
            pve1.setVarLen(true);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve3 with ids assigned
            pve1 = db.getPredVE(pve1ID);

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            SystemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db == null )
                {
                    outStream.print("new Database() returned null.\n");
                }

                if ( pve0 == null )
                {
                    outStream.print("creation of pve0 failed.\n");
                }

                if ( pve0ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve0ID not initialized.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            SystemErrorExceptionString);
                }
            }
        }

        // Setup the predicates that we will used for the toString and
        // toDBString tests.

        threwSystemErrorException = false;
        completed = false;

        if ( failures == 0 )
        {
            String testString0 = "pve0(1.000000, 2, a_nominal, pve1(<arg1>), " +
                                 "\"q-string\", 00:00:00:000, <untyped>)";
            String testDBString0 =
                "(predicate (id 0) " +
                        "(predID 1) " +
                        "(predName pve0) " +
                        "(varLen false) " +
                        "(argList " +
                            "((FloatDataValue (id 100) " +
                                "(itsFargID 2) " +
                                "(itsFargType FLOAT) " +
                                "(itsCellID 500) " +
                                "(itsValue 1.0) " +
                                "(subRange false) " +
                                "(minVal 0.0) " +
                                "(maxVal 0.0)), " +
                            "(IntDataValue (id 101) " +
                                "(itsFargID 3) " +
                                "(itsFargType INTEGER) " +
                                "(itsCellID 500) " +
                                "(itsValue 2) " +
                                "(subRange false) " +
                                "(minVal 0) " +
                                "(maxVal 0)), " +
                            "(NominalDataValue (id 102) " +
                                "(itsFargID 4) " +
                                "(itsFargType NOMINAL) " +
                                "(itsCellID 500) " +
                                "(itsValue a_nominal) " +
                                "(subRange false)), " +
                            "(PredDataValue (id 103) " +
                                "(itsFargID 5) " +
                                "(itsFargType PREDICATE) " +
                                "(itsCellID 500) " +
                                "(itsValue " +
                                    "(predicate (id 0) " +
                                        "(predID 9) " +
                                        "(predName pve1) " +
                                        "(varLen true) " +
                                        "(argList " +
                                            "((UndefinedDataValue (id 0) " +
                                                "(itsFargID 10) " +
                                                "(itsFargType UNTYPED) " +
                                                "(itsCellID 0) " +
                                                "(itsValue <arg1>) " +
                                                "(subRange false))))))) " +
                                "(subRange false)), " +
                            "(QuoteStringDataValue (id 104) " +
                                "(itsFargID 6) " +
                                "(itsFargType QUOTE_STRING) " +
                                "(itsCellID 500) " +
                                "(itsValue q-string) " +
                                "(subRange false)), " +
                            "(TimeStampDataValue (id 105) " +
                                "(itsFargID 7) " +
                                "(itsFargType TIME_STAMP) " +
                                "(itsCellID 500) " +
                                "(itsValue (60,00:00:00:000)) " +
                                "(subRange false)), " +
                            "(UndefinedDataValue (id 106) " +
                                "(itsFargID 8) " +
                                "(itsFargType UNTYPED) " +
                                "(itsCellID 500) " +
                                "(itsValue <untyped>) " +
                                "(subRange false))))))";

            String testString1 = "pve1(99)";
            String testDBString1 =
                "(predicate (id 0) " +
                        "(predID 9) " +
                        "(predName pve1) " +
                        "(varLen true) " +
                        "(argList " +
                            "((IntDataValue (id 107) " +
                                "(itsFargID 10) " +
                                "(itsFargType UNTYPED) " +
                                "(itsCellID 501) " +
                                "(itsValue 99) " +
                                "(subRange false) " +
                                "(minVal 0) " +
                                "(maxVal 0))))))";

            try
            {
                argList0 = new Vector<DataValue>();

                fargID = pve0.getFormalArg(0).getID();
                arg = new FloatDataValue(db, fargID, 1.0);
                argList0.add(arg);
                fargID = pve0.getFormalArg(1).getID();
                arg = new IntDataValue(db, fargID, 2);
                argList0.add(arg);
                fargID = pve0.getFormalArg(2).getID();
                arg = new NominalDataValue(db, fargID, "a_nominal");
                argList0.add(arg);
                fargID = pve0.getFormalArg(3).getID();
                arg = new PredDataValue(db, fargID, new Predicate(db, pve1ID));
                argList0.add(arg);
                fargID = pve0.getFormalArg(4).getID();
                arg = new QuoteStringDataValue(db, fargID, "q-string");
                argList0.add(arg);
                fargID = pve0.getFormalArg(5).getID();
                arg = new TimeStampDataValue(db, fargID,
                                             new TimeStamp(db.getTicks()));
                argList0.add(arg);
                fargID = pve0.getFormalArg(6).getID();
                arg = new UndefinedDataValue(db, fargID);
                argList0.add(arg);

                pred0 = new Predicate(db, pve0ID, argList0);

                // set argument IDs to dummy values to test toDBString()
                pred0.argList.get(0).setID(100);
                pred0.argList.get(1).setID(101);
                pred0.argList.get(2).setID(102);
                pred0.argList.get(3).setID(103);
                pred0.argList.get(4).setID(104);
                pred0.argList.get(5).setID(105);
                pred0.argList.get(6).setID(106);

                // set argument cellIDs to dummy values to test toDBString()
                pred0.argList.get(0).itsCellID = 500;
                pred0.argList.get(1).itsCellID = 500;
                pred0.argList.get(2).itsCellID = 500;
                pred0.argList.get(3).itsCellID = 500;
                pred0.argList.get(4).itsCellID = 500;
                pred0.argList.get(5).itsCellID = 500;
                pred0.argList.get(6).itsCellID = 500;

                argList1 = new Vector<DataValue>();

                fargID = pve1.getFormalArg(0).getID();
                arg = new IntDataValue(db, fargID, 99);
                arg.setID(107); // a dummy value to test toDBString() method
                argList1.add(arg);

                pred1 = new Predicate(db, pve1ID, argList1);

                // set argument IDs to dummy values to test toDBString()
                pred1.argList.get(0).setID(107);

                // set argument cellIDs to dummy values to test toDBString()
                pred1.argList.get(0).itsCellID = 501;

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }

            if ( ( argList0 == null ) ||
                 ( argList0.size() != 7 ) ||
                 ( pred0 == null ) ||
                 ( argList1 == null ) ||
                 ( argList1.size() != 1 ) ||
                 ( pred1 == null ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( argList0 == null )
                    {
                        outStream.print("argList0 allocation failed.\n");
                    }
                    else if ( argList0.size() != 7 )
                    {
                        outStream.printf("unexpected argList0.size(): %d (7).\n",
                                         argList0.size());
                    }

                    if ( argList1 == null )
                    {
                        outStream.print("argList1 allocation failed.\n");
                    }
                    else if ( argList1.size() != 1 )
                    {
                        outStream.printf("unexpected argList1.size(): %d (1).\n",
                                         argList1.size());
                    }

                    if ( ( pred0 == null ) ||
                         ( pred1 == null ) )
                    {
                        outStream.print("one or more Predicate allocation(s) " +
                                        "failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("test predicate allocation failed " +
                                        "to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("test predicate allocation threw a " +
                                         "SystemErrorException: \"%s\".\n",
                                         SystemErrorExceptionString);
                    }
                }
            }
            else if ( ( pred0.argList.get(0).getID() != 100 ) ||
                      ( pred0.argList.get(1).getID() != 101 ) ||
                      ( pred0.argList.get(2).getID() != 102 ) ||
                      ( pred0.argList.get(3).getID() != 103 ) ||
                      ( pred0.argList.get(4).getID() != 104 ) ||
                      ( pred0.argList.get(5).getID() != 105 ) ||
                      ( pred0.argList.get(6).getID() != 106 ) ||
                      ( pred1.argList.get(0).getID() != 107 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pred.argList arg ID(s): " +
                            "%d %d %d %d %d %d %d - %d\n",
                            pred0.argList.get(0).getID(),
                            pred0.argList.get(1).getID(),
                            pred0.argList.get(2).getID(),
                            pred0.argList.get(3).getID(),
                            pred0.argList.get(4).getID(),
                            pred0.argList.get(5).getID(),
                            pred0.argList.get(6).getID(),
                            pred1.argList.get(0).getID());
                }
            }
            else if ( ( pred0.toString().compareTo(testString0) != 0 ) ||
                      ( pred1.toString().compareTo(testString1) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred0.toString().compareTo(testString0) != 0 )
                    {
                       outStream.printf("Unexpected pred0.toString)(): \"%s\"\n",
                                         pred0.toString());
                    }

                    if ( pred1.toString().compareTo(testString1) != 0 )
                    {
                       outStream.printf("Unexpected pred1.toString)(): \"%s\"\n",
                                         pred1.toString());
                    }
                }
            }
            else if ( ( pred0.toDBString().compareTo(testDBString0) != 0 ) ||
                      ( pred1.toDBString().compareTo(testDBString1) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pred0.toDBString().compareTo(testDBString0) != 0 )
                    {
                       outStream.printf(
                               "Unexpected pred0.toDBString)(): \"%s\"\n",
                               pred0.toDBString());
                    }

                    if ( pred1.toDBString().compareTo(testDBString1) != 0 )
                    {
                       outStream.printf(
                               "Unexpected pred1.toDBString)(): \"%s\"\n",
                               pred1.toDBString());
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
    } /* Predicate::TestToStringMethods() */


    /**
     * VerifyArgListAssignment()
     *
     * Verify that the specified replacement of an argument list
     * entry succeeds.
     *                                               -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyArgListAssignment(Predicate target,
                                       DataValue newArg,
                                       int idx,
                                       java.io.PrintStream outStream,
                                       boolean verbose,
                                       String targetDesc,
                                       String newArgDesc)
    {
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        DataValue old_dv = null;
        DataValue new_dv = null;

        try
        {
            old_dv = target.getArg(idx);

            target.replaceArg(idx, newArg);

            new_dv = target.getArg(idx);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( old_dv == null ) ||
             ( old_dv.getItsFargID() == DBIndex.INVALID_ID ) ||
             ( new_dv == null ) ||
             ( new_dv != newArg ) ||
             ( old_dv.getItsFargID() != new_dv.getItsFargID() ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( old_dv == null )
                {
                    outStream.printf(
                            "initial %s.getArg(%d) failed to complete.\n",
                            targetDesc, idx);
                }

                if ( old_dv.getItsFargID() == DBIndex.INVALID_ID )
                {
                    outStream.printf("initial %s.getArg(%d).getItsFargID() " +
                            "returned INVALID_ID.\n",
                            targetDesc, idx);
                }

                if ( new_dv == null )
                {
                    outStream.printf(
                            "%s.replaceArg(%d, %s) failed to complete.\n",
                            targetDesc, idx, newArgDesc);
                }

                if ( new_dv != newArg )
                {
                    outStream.printf(
                        "unexpected getArg(%d) after %s.replaceArg(%d, %s).\n",
                        idx, targetDesc, idx, newArgDesc);
                }

                if ( old_dv.getItsFargID() != new_dv.getItsFargID() )
                {
                    outStream.printf("unexpected itsFargID after %s.replace" +
                            "Arg(%d, %s). old = %d, new = %d\n",
                            targetDesc, idx, newArgDesc,
                            old_dv.getItsFargID(), new_dv.getItsFargID());
                }

                if ( ! completed )
                {
                    outStream.printf(
                        "%s.replaceArg(%d, %s) test failed to complete.\n",
                        targetDesc, idx, newArgDesc);

                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test threw " +
                            "system error: \"%s\"\n",
                            targetDesc, idx, newArgDesc,
                            systemErrorExceptionString);

                }
            }
        }

        if ( new_dv instanceof UndefinedDataValue )
        {
            long target_pve_ID = DBIndex.INVALID_ID;
            String old_dv_val = null;
            String new_dv_val = null;
            String farg_name = null;
            PredicateVocabElement target_pve = null;

            try
            {
                if ( old_dv instanceof UndefinedDataValue )
                {
                    old_dv_val = ((UndefinedDataValue)old_dv).getItsValue();
                }
                new_dv_val = ((UndefinedDataValue)new_dv).getItsValue();
                target_pve_ID = target.getPveID();
                target_pve = target.getDB().vl.getPredicateVocabElement(target_pve_ID);
                farg_name = target_pve.getFormalArg(idx).getFargName();
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( threwSystemErrorException )
            {
                failures++;

                outStream.printf("%s.replaceArg(%d, %s) test threw a " +
                                "system error(2): \"%s\"\n",
                                targetDesc, idx, newArgDesc,
                                systemErrorExceptionString);
            }

            if ( ( old_dv instanceof UndefinedDataValue ) &&
                 ( old_dv_val == null ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test started " +
                            "with null value in undefined arg.\n",
                            targetDesc, idx, newArgDesc);
                }
            }

            if ( new_dv_val == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                            "with null value in undefined arg.\n",
                            targetDesc, idx, newArgDesc);
                }
            }

            if ( ( old_dv_val != null ) && ( old_dv_val == new_dv_val ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                        "with old and new undefined args sharing a string.\n",
                        targetDesc, idx, newArgDesc);
                }
            }

            if ( ( old_dv_val != null ) &&
                 ( old_dv_val.compareTo(new_dv_val) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                        "with old and new undefined args with different " +
                        "values: \"%s\", \"%s\".\n",
                        targetDesc, idx, newArgDesc, old_dv_val, new_dv_val);
                }
            }

            if ( farg_name == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test couldn't " +
                            "get untyped arg name.\n",
                            targetDesc, idx, newArgDesc);
                }
            }

            if ( ( farg_name != null ) &&
                 ( old_dv_val != null ) &&
                 ( farg_name.compareTo(old_dv_val) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test started " +
                        "with undefined arg name different from farg name: " +
                        "\"%s\", \"%s\".\n",
                        targetDesc, idx, newArgDesc, farg_name, old_dv_val);
                }
            }

            if ( ( farg_name != null ) &&
                 ( farg_name.compareTo(new_dv_val) != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test finished " +
                        "with undefined arg name different from farg name: " +
                        "\"%s\", \"%s\".\n",
                        targetDesc, idx, newArgDesc, farg_name, new_dv_val);
                }
            }
        }

        return failures;

    } /* Predicate::VerifyArgListAssignment() */


    /**
     * VerifyArgListAsgnmntFails()
     *
     * Verify that the specified replacement of an argument list
     * entry fails.
     *                                               -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyArgListAsgnmntFails(Predicate target,
                                       DataValue newArg,
                                       int idx,
                                       java.io.PrintStream outStream,
                                       boolean verbose,
                                       String targetDesc,
                                       String newArgDesc)
    {
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        DataValue old_dv = null;
        DataValue new_dv = null;

        try
        {
            old_dv = target.getArg(idx);

            target.replaceArg(idx, newArg);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( old_dv == null ) ||
             ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( old_dv == null )
                {
                    outStream.printf(
                            "%s.getArg(%d) failed to complete.\n",
                            targetDesc, idx);
                }

                if ( completed )
                {
                    outStream.printf(
                        "%s.replaceArg(%d, %s) test completed.\n",
                        targetDesc, idx, newArgDesc);

                }

                if ( ! threwSystemErrorException )
                {
                    outStream.printf("%s.replaceArg(%d, %s) test " +
                            "failed to throw a system error.\n",
                            targetDesc, idx, newArgDesc);

                }
            }
        }

        completed = false;
        threwSystemErrorException = false;

        try
        {
            new_dv = target.getArg(idx);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( new_dv == null ) ||
             ( new_dv != old_dv ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( new_dv == null )
                {
                    outStream.printf(
                            "%s.getArg(%d) failed to complete.\n",
                            targetDesc, idx);
                }

                if ( new_dv != old_dv )
                {
                    outStream.printf(
                        "unexpected getArg(%d) after %s.replaceArg(%d, %s).\n",
                        idx, targetDesc, idx, newArgDesc);
                }

                if ( ! completed )
                {
                    outStream.printf(
                        "%s.getArg(%d) test failed to complete.\n",
                        targetDesc, idx);

                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("%s.getArg(%d) test threw " +
                            "system error: \"%s\"\n",
                            targetDesc, idx,
                            systemErrorExceptionString);

                }
            }
        }

        return failures;

    } /* Predicate::VerifyArgListAsgnmntFails() */


    /**
     * VerifyPredCopy()
     *
     * Verify that the supplied instances of Predicate are distinct,
     * that they contain no common references (other than db), and that they
     * have the same value.
     *                                               -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyPredicateCopy(Predicate base,
                                          Predicate copy,
                                          java.io.PrintStream outStream,
                                          boolean verbose,
                                          String baseDesc,
                                          String copyDesc)
    {
        int failures = 0;
        int i;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyPredicateCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyPredicateCopy: %s null on entry.\n",
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
        else if ( base.getDB() != copy.getDB() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.db != %s.db.\n", baseDesc, copyDesc);
            }
        }
        else if ( base.pveID != copy.pveID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.predID == %d != %s.predID == %d.\n",
                                 baseDesc, base.pveID, copyDesc, copy.pveID);
            }
        }
        else if ( ( base.predName == copy.predName ) &&
                  ( base.predName != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                     "%s.predName and %s.predName refer to the same string.\n",
                      baseDesc, copyDesc);
            }
        }
        else if ( base.varLen != copy.varLen )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.varLen != %s.varLen.\n",
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
        else if ( ( base.argList == copy.argList ) &&
                  ( base.argList != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s share an argList.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.argList == null ) &&
                  ( copy.argList != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.argList is null and %s.argList isn't.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.argList != null ) &&
                  ( copy.argList == null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.argList is null and %s.argList isn't.\n",
                                  copyDesc, baseDesc);
            }
        }
        else if ( base.argList != null )
        {
            i = 0;
            while ( ( i < base.argList.size() ) && ( failures == 0 ) )
            {
                failures += DataValueTest.VerifyDVCopy(base.argList.get(i),
                                          copy.argList.get(i),
                                          outStream,
                                          verbose,
                                          baseDesc + "argList.get(" + i + ")",
                                          copyDesc + "argList.get(" + i + ")");
                i++;
            }
        }

        return failures;

    } /* Predicate::VerifyPredCopy() */

}