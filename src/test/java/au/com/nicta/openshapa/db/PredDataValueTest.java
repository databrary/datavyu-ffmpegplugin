package au.com.nicta.openshapa.db;

import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test cases for the class PredDataValue.
 *
 * @author cfreeman
 */
public class PredDataValueTest extends DataValueTest {
    private static Database db;
    private MatrixVocabElement predMVE;
    private PredFormalArg pfa;
    private PredDataValue pdv;

    private MatrixVocabElement predMVE2;
    private PredFormalArg pfa2;

    private PrintStream outStream;
    private boolean verbose;

    @Override
    public DataValue getInstance() {
        return pdv;
    }

    /**
     * Default test constructor.
     */
    public PredDataValueTest() {
    }

    /**
     * Sets up the test fixture (i.e. the data available to all tests), this is
     * performed before each test case.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException on failure.
     */
    @Before
    public void setUp() throws SystemErrorException {
        db = new ODBCDatabase();
        predMVE = new MatrixVocabElement(db, "pred_mve");
        predMVE.setType(MatrixVocabElement.MatrixType.PREDICATE);
        pfa = new PredFormalArg(db);
        predMVE.appendFormalArg(pfa);
        db.vl.addElement(predMVE);

        predMVE2 = new MatrixVocabElement(db, "pred_mve2");
        predMVE2.setType(MatrixVocabElement.MatrixType.PREDICATE);
        pfa2 = new PredFormalArg(db);
        pfa2.setSubRange(true);

        PredicateVocabElement pve1 = new PredicateVocabElement(db, "pve1");
        pve1.appendFormalArg(new IntFormalArg(db, "<int>"));
        pve1.appendFormalArg(new UnTypedFormalArg(db, "<arg2>"));
        db.addPredVE(pve1);

        predMVE2.appendFormalArg(pfa2);
        db.vl.addElement(predMVE2);

        pdv = new PredDataValue(db, pfa.getID());

        outStream = System.out;
        verbose = true;
    }

    /**
     * Tears down the test fixture (i.e. the data available to all tests), this
     * is performed after each test case.
     */
    @After
    public void tearDown() {
    }

        /**
     * Test of updateForFargChange method, of class IntDataValue.
     */
    @Test
    @Override
    public void testUpdateForFargChange() throws Exception {
    }

    /**
     * Test of updateSubRange method, of class IntDataValue.
     */
    @Test
    @Override
    public void testUpdateSubRange() throws Exception {
    }

    @Test
    @Override
    public void testGetItsFargType() {
        assertEquals(pdv.itsFargType, FormalArgument.FArgType.PREDICATE);
    }

    @Test
    @Override
    public void testGetItsFargID() {
        assertEquals(pdv.itsFargID, 2);
    }

    @Test
    public void testHashCode() throws SystemErrorException {
        PredDataValue value0 = new PredDataValue(db, pfa.getID());
        PredDataValue value1 = new PredDataValue(db, pfa.getID());
        PredDataValue value2 = new PredDataValue(db, pfa2.getID());

        super.testHashCode(value0, value1, value2);
    }

    @Test
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        PredDataValue value0 = new PredDataValue(db, pfa.getID());
        PredDataValue value1 = new PredDataValue(db, pfa.getID());
        PredDataValue value2 = new PredDataValue(db, pfa.getID());
        PredDataValue value3 = new PredDataValue(db, pfa2.getID());

        super.testEquals(value0, value1, value2, value3);
    }

    /**
     * Test 1 arg constructor of class PredDataValue.
     *
     * @throws SystemErrorException on failure.
     */
    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        PredDataValue value = new PredDataValue(db);

        assertNotNull(db);
        assertNotNull(value);

        assertEquals(value.getDB(), db);

        DataValueTest.verify1ArgInitialization(db, value);

        // When building a PredDataValue, at one point setItsValue(null)
        // is called - this actually sets it to an empty predicate
        assertNotNull(value.itsValue);
        assertTrue(value.itsValue.getPveID() == DBIndex.INVALID_ID);

    } /* PredDataValue::Test1ArgConstructor() */

    /*************************************************************************
     *
     *                             Test Spec:
     *
     * 1) One argument constructor:
     *
     *      a) Construct a database.  Using this database, call the one
     *         argument constructor for PredDataValue.  Verify that all
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
     *         Construct a PredDataValue for the formal argument of the mve
     *         by passing a reference to the database and the id of the formal
     *         argument.  Verify that the PredDataValue's itsFargID,
     *         itsFargType, and subRange fields match those of the formal
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
     *      PredDataValue -- perhaps after having been modified to match
     *      the subrange.
     *
     * 4) Copy constructor:
     *
     *      a) Construct a database and possibly a mve (matrix vocab element)
     *         and such formal arguments as are necessary.  If an mve is
     *         created, insert it into the database, and make note of the IDs
     *         assigned.  Then create a PredDataValue (possibly using
     *         the using a formal argument ID).
     *
     *         Now use the copy constructor to create a copy of the
     *         PredDataValue, and verify that the copy is correct.
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
     *      Verify that setItsValue() and coerceToRange() fail on invalid
     *      input.
     *
     * 6) toString methods:
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
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test1ArgConstructor() {
        String testBanner =
            "Testing 1 argument constructor for class PredDataValue           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        PredDataValue pdv = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        pdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();
            pdv = new PredDataValue(db);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db == null ) ||
             ( pdv == null ) ||
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

                if ( pdv == null )
                {
                    outStream.print(
                            "new PredDataValue(db) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "new PredDataValue(db) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new PredDataValue(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.Verify1ArgInitialization(db, pdv, outStream,
                                                           verbose);

            if ( pdv.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv.itsValue is null.\n");
                }
            }
            else if ( pdv.itsValue.getPveID() != DBIndex.INVALID_ID )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "pdv.itsValue.getPredID() != INVALID_ID.\n");
                }
            }
       }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue((Database)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new PredDataValue(null) returned.\n");
                    }

                    if ( pdv != null )
                    {
                        outStream.print(
                                "new PredDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(null) failed to throw " +
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
    } /* PredDataValue::Test1ArgConstructor() */


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
    @Test
    public void Test2ArgConstructor() throws SystemErrorException {
        String testBanner =
            "Testing 2 argument constructor for class PredDataValue           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        long pve4ID = DBIndex.INVALID_ID;
        long pve5ID = DBIndex.INVALID_ID;
        long pve6ID = DBIndex.INVALID_ID;
        long pve7ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        PredicateVocabElement pve4 = null;
        PredicateVocabElement pve5 = null;
        PredicateVocabElement pve6 = null;
        PredicateVocabElement pve7 = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement pred_mve_sr = null;
        FormalArgument farg = null;
        PredFormalArg pfa = null;
        PredFormalArg pfa_sr = null;
        PredDataValue pdv = null;
        PredDataValue pdv_sr = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database, and pve's
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
            farg = new UnTypedFormalArg(db, "<arg>");
            pve5.appendFormalArg(farg);

            pve5ID = db.addPredVE(pve5);

            // get a copy of the databases version of pve5 with ids assigned
            pve5 = db.getPredVE(pve5ID);


            pve6 = new PredicateVocabElement(db, "pve6");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve6.appendFormalArg(farg);

            pve6ID = db.addPredVE(pve6);

            // get a copy of the databases version of pve6 with ids assigned
            pve6 = db.getPredVE(pve6ID);


            pve7 = new PredicateVocabElement(db, "pve7");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve7.appendFormalArg(farg);

            pve7ID = db.addPredVE(pve7);

            // get a copy of the databases version of pve7 with ids assigned
            pve7 = db.getPredVE(pve7ID);


            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
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
                    outStream.print("creation of pve4 failed.\n");
                }

                if ( pve4ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve4ID not initialized.\n");
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

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete (1).\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            systemErrorExceptionString);
                }
            }
        }


        /* Now allocate mve's & PredFormalArg's with and without subranges.
         * Use the ID's of these PredFormalArgs to test the two argument
         * constructor.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pred_mve = new MatrixVocabElement(db, "pred_mve");
                pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa = new PredFormalArg(db);
                pred_mve.appendFormalArg(pfa);
                db.vl.addElement(pred_mve);

                pdv = new PredDataValue(db, pfa.getID());

                pred_mve_sr = new MatrixVocabElement(db, "pred_mve_sr");
                pred_mve_sr.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa_sr = new PredFormalArg(db);
                pfa_sr.setSubRange(true);
                pfa_sr.addApproved(pve0ID);
                pfa_sr.addApproved(pve2ID);
                pfa_sr.addApproved(pve4ID);
                pfa_sr.addApproved(pve6ID);
                pred_mve_sr.appendFormalArg(pfa_sr);
                db.vl.addElement(pred_mve_sr);

                pdv_sr = new PredDataValue(db, pfa_sr.getID());

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( db == null ) ||
                 ( pred_mve == null ) ||
                 ( pfa == null ) ||
                 ( pdv == null ) ||
                 ( pred_mve_sr == null ) ||
                 ( pfa_sr == null ) ||
                 ( pdv_sr == null ) ||
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

                    if ( pred_mve == null )
                    {
                        outStream.print("allocation of pred_mve failed.\n");
                    }

                    if ( pfa == null )
                    {
                        outStream.print("allocation of pfa failed.");
                    }

                    if ( pdv == null )
                    {
                        outStream.print(
                            "new PredDataValue(db, pfa.getID()) returned null.\n");
                    }

                    if ( pred_mve_sr == null )
                    {
                        outStream.print("allocation of pred_mve_sr failed.\n");
                    }

                    if ( pfa_sr == null )
                    {
                        outStream.print("allocation of pfa_sr failed.");
                    }

                    if ( pdv_sr == null )
                    {
                        outStream.print("new PredDataValue(db, pfa_sr.getID()) " +
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
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               pfa,
                                                               pdv,
                                                               outStream,
                                                               verbose,
                                                              "pdv");

            if ( pdv.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv.itsValue == null.\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(new Predicate(db),
                                                          pdv.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)",
                                                          "pdv.itsValue");
            }

            if ( pdv.subRange != pfa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "pdv.subRange doesn't match pfa.getSubRange().\n");
                }
            }

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               pfa_sr,
                                                               pdv_sr,
                                                               outStream,
                                                               verbose,
                                                               "pdv_sr");

            if ( pdv_sr.subRange != pfa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv_sr.subRange doesn't match " +
                                     "pfa_sr.getSubRange().\n");
                }
            }

            if ( pdv_sr.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv_sr.itsValue == null.\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(new Predicate(db),
                                                          pdv_sr.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)",
                                                          "pdv_sr.itsValue");
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue((Database)null, pfa.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new PredDataValue(null, " +
                                        "pfa.getID()) returned.\n");
                    }

                    if ( pdv != null )
                    {
                        outStream.print("new PredDataValue(null, " +
                                        "pfa.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(null, pfa.getID())" +
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
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue(db, DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "INVALID_ID) returned.\n");
                    }

                    if ( pdv != null )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "INVALID_ID) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(db, INVALID_ID)" +
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
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue(db, pred_mve.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "pred_mve.getID()) returned.\n");
                    }

                    if ( pdv != null )
                    {
                        outStream.print("new PredDataValue(db, " +
                                "pred_mve.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new PredDataValue(db, pred_mve.getID()) " +
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

        assertTrue(pass);
    } /* PredDataValue::Test2ArgConstructor() */


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
    @Test
    public void Test3ArgConstructor() throws SystemErrorException {
        String testBanner =
            "Testing 3 argument constructor for class PredDataValue           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        long pve4ID = DBIndex.INVALID_ID;
        long pve5ID = DBIndex.INVALID_ID;
        long pve6ID = DBIndex.INVALID_ID;
        long pve7ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        PredicateVocabElement pve4 = null;
        PredicateVocabElement pve5 = null;
        PredicateVocabElement pve6 = null;
        PredicateVocabElement pve7 = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement pred_mve_sr = null;
        FormalArgument farg = null;
        PredFormalArg pfa = null;
        PredFormalArg pfa_sr = null;
        PredDataValue pdv = null;
        PredDataValue pdv_sr0 = null;
        PredDataValue pdv_sr1 = null;
        Predicate p0 = null;
        Predicate p1 = null;
        Predicate p2 = null;
        Predicate p3 = null;
        Predicate p4 = null;
        Predicate p5 = null;
        Predicate p6 = null;
        Predicate p7 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }


        // Start by setting up the needed database, pve's, and preds
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

            p0 = new Predicate(db, pve0ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);

            p1 = new Predicate(db, pve1ID);


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

            p2 = new Predicate(db, pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve3.appendFormalArg(farg);
            pve3.setVarLen(true);

            pve3ID = db.addPredVE(pve3);

            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);

            p3 = new Predicate(db, pve3ID);


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

            p4 = new Predicate(db, pve4ID);


            pve5 = new PredicateVocabElement(db, "pve5");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve5.appendFormalArg(farg);

            pve5ID = db.addPredVE(pve5);

            // get a copy of the databases version of pve5 with ids assigned
            pve5 = db.getPredVE(pve5ID);

            p5 = new Predicate(db, pve5ID);


            pve6 = new PredicateVocabElement(db, "pve6");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve6.appendFormalArg(farg);

            pve6ID = db.addPredVE(pve6);

            // get a copy of the databases version of pve6 with ids assigned
            pve6 = db.getPredVE(pve6ID);

            p6 = new Predicate(db, pve6ID);


            pve7 = new PredicateVocabElement(db, "pve7");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve7.appendFormalArg(farg);

            pve7ID = db.addPredVE(pve7);

            // get a copy of the databases version of pve7 with ids assigned
            pve7 = db.getPredVE(pve7ID);

            p7 = new Predicate(db, pve7ID);


            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( p0 == null ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
             ( p1 == null ) ||
             ( pve2 == null ) ||
             ( pve2ID == DBIndex.INVALID_ID ) ||
             ( p2 == null ) ||
             ( pve3 == null ) ||
             ( pve3ID == DBIndex.INVALID_ID ) ||
             ( p3 == null ) ||
             ( pve4 == null ) ||
             ( pve4ID == DBIndex.INVALID_ID ) ||
             ( p4 == null ) ||
             ( pve5 == null ) ||
             ( pve5ID == DBIndex.INVALID_ID ) ||
             ( p5 == null ) ||
             ( pve6 == null ) ||
             ( pve6ID == DBIndex.INVALID_ID ) ||
             ( p6 == null ) ||
             ( pve7 == null ) ||
             ( pve7ID == DBIndex.INVALID_ID ) ||
             ( p7 == null ) ||
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

                if ( p0 == null )
                {
                    outStream.print("creation of p0 failed.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( p1 == null )
                {
                    outStream.print("creation of p1 failed.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve2ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve2ID not initialized.\n");
                }

                if ( p2 == null )
                {
                    outStream.print("creation of p2 failed.\n");
                }

                if ( pve3 == null )
                {
                    outStream.print("creation of pve3 failed.\n");
                }

                if ( pve3ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve3ID not initialized.\n");
                }

                if ( p3 == null )
                {
                    outStream.print("creation of p3 failed.\n");
                }

                if ( pve4 == null )
                {
                    outStream.print("creation of pve4 failed.\n");
                }

                if ( pve4ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve4ID not initialized.\n");
                }

                if ( p4 == null )
                {
                    outStream.print("creation of p4 failed.\n");
                }

                if ( pve5 == null )
                {
                    outStream.print("creation of pve5 failed.\n");
                }

                if ( pve5ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve5ID not initialized.\n");
                }

                if ( p5 == null )
                {
                    outStream.print("creation of p5 failed.\n");
                }

                if ( pve6 == null )
                {
                    outStream.print("creation of pve6 failed.\n");
                }

                if ( pve6ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve6ID not initialized.\n");
                }

                if ( p6 == null )
                {
                    outStream.print("creation of p6 failed.\n");
                }

                if ( pve7 == null )
                {
                    outStream.print("creation of pve7 failed.\n");
                }

                if ( pve7ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve7ID not initialized.\n");
                }

                if ( p7 == null )
                {
                    outStream.print("creation of p7 failed.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete (1).\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            systemErrorExceptionString);
                }
            }
        }


        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pred_mve = new MatrixVocabElement(db, "pred_mve");
                pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa = new PredFormalArg(db);
                pred_mve.appendFormalArg(pfa);
                db.vl.addElement(pred_mve);

                pdv = new PredDataValue(db, pfa.getID(), p0);

                pred_mve_sr = new MatrixVocabElement(db, "pred_mve_sr");
                pred_mve_sr.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa_sr = new PredFormalArg(db);
                pfa_sr.setSubRange(true);
                pfa_sr.addApproved(pve0ID);
                pfa_sr.addApproved(pve2ID);
                pfa_sr.addApproved(pve4ID);
                pfa_sr.addApproved(pve6ID);
                pred_mve_sr.appendFormalArg(pfa_sr);
                db.vl.addElement(pred_mve_sr);

                pdv_sr0 = new PredDataValue(db, pfa_sr.getID(), p2);
                pdv_sr1 = new PredDataValue(db, pfa_sr.getID(), p3);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( db == null ) ||
                 ( pred_mve == null ) ||
                 ( pfa == null ) ||
                 ( pdv == null ) ||
                 ( pred_mve_sr == null ) ||
                 ( pfa_sr == null ) ||
                 ( pdv_sr0 == null ) ||
                 ( pdv_sr1 == null ) ||
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

                    if ( pred_mve == null )
                    {
                        outStream.print("allocation of pred_mve failed.\n");
                    }

                    if ( pfa == null )
                    {
                        outStream.print("allocation of pfa failed.");
                    }

                    if ( pdv == null )
                    {
                        outStream.print("allocation of pdv failed.\n");
                    }

                    if ( pred_mve_sr == null )
                    {
                        outStream.print("allocation of pred_mve_sr failed.\n");
                    }

                    if ( pfa_sr == null )
                    {
                        outStream.print("allocation of pfa_sr failed.");
                    }

                    if ( pdv_sr0 == null )
                    {
                        outStream.print("allocation of pdv_sr0 failed.\n");
                    }

                    if ( pdv_sr1 == null )
                    {
                        outStream.print("allocation of pdv_sr1 failed.\n");
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
            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               pfa,
                                                               pdv,
                                                               outStream,
                                                               verbose,
                                                               "pdv");

            if ( pdv.subRange != pfa.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "pdv.subRange doesn't match pfa.getSubRange().\n");
                }
            }

            if ( pdv.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv.itsValue null.\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(p0,
                                                          pdv.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p0",
                                                          "pdv.itsValue");
            }

            /**********************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               pfa_sr,
                                                               pdv_sr0,
                                                               outStream,
                                                               verbose,
                                                               "pdv_sr0");

            if ( pdv_sr0.subRange != pfa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv_sr0.subRange doesn't match " +
                                     "pfa_sr.getSubRange().\n");
                }
            }

            if ( pdv_sr0.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv_sr0.itsValue == null.\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(p2,
                                                          pdv_sr0.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p2",
                                                          "pdv_sr0.itsValue");
            }

            /***************************************/

            failures += DataValueTest.Verify2PlusArgInitialization(db,
                                                               pfa_sr,
                                                               pdv_sr1,
                                                               outStream,
                                                               verbose,
                                                               "pdv_sr1");

            if ( pdv_sr1.subRange != pfa_sr.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv_sr0.subRange doesn't match " +
                                     "pfa_sr.getSubRange().\n");
                }
            }

            if ( pdv_sr1.itsValue == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv_sr1.itsValue == null.\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(new Predicate(db),
                                                          pdv_sr1.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)",
                                                          "pdv_sr1.itsValue");
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue((Database)null, pfa.getID(), p4);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pdv != null )
                    {
                        outStream.print("new PredDataValue(null, " +
                                "pfa.getID(), p4) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new PredDataValue(null, " +
                                        "pfa.getID(), p4) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(null, pfa.getID()," +
                                       " p4) failed to throw a system error " +
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
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue(db, DBIndex.INVALID_ID, p5);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pdv != null )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "INVALID_ID, p5) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "INVALID_ID, p5) returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(db, INVALID_ID, " +
                                        "p5) failed to throw a system error " +
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
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue(db, pred_mve.getID(), p6);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "pred_mve.getID(), p6) returned.\n");
                    }

                    if ( pdv != null )
                    {
                        outStream.print("new PredDataValue(db, " +
                                "pred_mve.getID(), p6) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(db, " +
                                        "pred_mve.getID(), p6) failed to " +
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

        assertTrue(pass);
    } /* PredDataValue::Test3ArgConstructor() */


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
    @Test
    public void TestAccessors() throws SystemErrorException {
        String testBanner =
            "Testing class PredDataValue accessors                            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        long pve4ID = DBIndex.INVALID_ID;
        long pve5ID = DBIndex.INVALID_ID;
        long pve6ID = DBIndex.INVALID_ID;
        long pve7ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        PredicateVocabElement pve4 = null;
        PredicateVocabElement pve5 = null;
        PredicateVocabElement pve6 = null;
        PredicateVocabElement pve7 = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement matrix_mve = null;
        FormalArgument farg = null;
        PredFormalArg pfa = null;
        UnTypedFormalArg ufa = null;
        PredDataValue pdv0 = null;
        PredDataValue pdv1 = null;
        PredDataValue pdv2 = null;
        Predicate p0 = null;
        Predicate p1 = null;
        Predicate p2 = null;
        Predicate p3 = null;
        Predicate p4 = null;
        Predicate p5 = null;
        Predicate p6 = null;
        Predicate p7 = null;
        Database alt_db = null;
        long alt_pve0ID = DBIndex.INVALID_ID;
        long alt_pve1ID = DBIndex.INVALID_ID;
        PredicateVocabElement alt_pve0 = null;
        PredicateVocabElement alt_pve1 = null;
        MatrixVocabElement alt_mve = null;
        PredFormalArg alt_pfa = null;
        UnTypedFormalArg alt_ufa = null;
        Predicate alt_p0 = null;
        Predicate alt_p1 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database, pve's, and preds
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

            p0 = new Predicate(db, pve0ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);

            p1 = new Predicate(db, pve1ID);


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

            p2 = new Predicate(db, pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve3.appendFormalArg(farg);
            pve3.setVarLen(true);

            pve3ID = db.addPredVE(pve3);

            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);

            p3 = new Predicate(db, pve3ID);


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

            p4 = new Predicate(db, pve4ID);


            pve5 = new PredicateVocabElement(db, "pve5");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve5.appendFormalArg(farg);

            pve5ID = db.addPredVE(pve5);

            // get a copy of the databases version of pve5 with ids assigned
            pve5 = db.getPredVE(pve5ID);

            p5 = new Predicate(db, pve5ID);


            pve6 = new PredicateVocabElement(db, "pve6");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve6.appendFormalArg(farg);

            pve6ID = db.addPredVE(pve6);

            // get a copy of the databases version of pve6 with ids assigned
            pve6 = db.getPredVE(pve6ID);

            p6 = new Predicate(db, pve6ID);


            pve7 = new PredicateVocabElement(db, "pve7");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve7.appendFormalArg(farg);

            pve7ID = db.addPredVE(pve7);

            // get a copy of the databases version of pve7 with ids assigned
            pve7 = db.getPredVE(pve7ID);

            p7 = new Predicate(db, pve7ID);


            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( p0 == null ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
             ( p1 == null ) ||
             ( pve2 == null ) ||
             ( pve2ID == DBIndex.INVALID_ID ) ||
             ( p2 == null ) ||
             ( pve3 == null ) ||
             ( pve3ID == DBIndex.INVALID_ID ) ||
             ( p3 == null ) ||
             ( pve4 == null ) ||
             ( pve4ID == DBIndex.INVALID_ID ) ||
             ( p4 == null ) ||
             ( pve5 == null ) ||
             ( pve5ID == DBIndex.INVALID_ID ) ||
             ( p5 == null ) ||
             ( pve6 == null ) ||
             ( pve6ID == DBIndex.INVALID_ID ) ||
             ( p6 == null ) ||
             ( pve7 == null ) ||
             ( pve7ID == DBIndex.INVALID_ID ) ||
             ( p7 == null ) ||
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

                if ( p0 == null )
                {
                    outStream.print("creation of p0 failed.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( p1 == null )
                {
                    outStream.print("creation of p1 failed.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve2ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve2ID not initialized.\n");
                }

                if ( p2 == null )
                {
                    outStream.print("creation of p2 failed.\n");
                }

                if ( pve3 == null )
                {
                    outStream.print("creation of pve3 failed.\n");
                }

                if ( pve3ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve3ID not initialized.\n");
                }

                if ( p3 == null )
                {
                    outStream.print("creation of p3 failed.\n");
                }

                if ( pve4 == null )
                {
                    outStream.print("creation of pve4 failed.\n");
                }

                if ( pve4ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve4ID not initialized.\n");
                }

                if ( p4 == null )
                {
                    outStream.print("creation of p4 failed.\n");
                }

                if ( pve5 == null )
                {
                    outStream.print("creation of pve5 failed.\n");
                }

                if ( pve5ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve5ID not initialized.\n");
                }

                if ( p5 == null )
                {
                    outStream.print("creation of p5 failed.\n");
                }

                if ( pve6 == null )
                {
                    outStream.print("creation of pve6 failed.\n");
                }

                if ( pve6ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve6ID not initialized.\n");
                }

                if ( p6 == null )
                {
                    outStream.print("creation of p6 failed.\n");
                }

                if ( pve7 == null )
                {
                    outStream.print("creation of pve7 failed.\n");
                }

                if ( pve7ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve7ID not initialized.\n");
                }

                if ( p7 == null )
                {
                    outStream.print("creation of p7 failed.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete (1).\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            systemErrorExceptionString);
                }
            }
        }

        /* now allocate test mve's, formal arguments, and data values for
         * the test proper.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pred_mve = new MatrixVocabElement(db, "pred_mve");
                pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa = new PredFormalArg(db);
                pfa.setSubRange(true);
                pfa.addApproved(pve0ID);
                pfa.addApproved(pve2ID);
                pfa.addApproved(pve4ID);
                pfa.addApproved(pve6ID);
                pred_mve.appendFormalArg(pfa);
                db.vl.addElement(pred_mve);

                pdv0 = new PredDataValue(db, pfa.getID(), p2);

                matrix_mve = new MatrixVocabElement(db, "matrix_mve");
                matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
                ufa = new UnTypedFormalArg(db, "<untyped>");
                matrix_mve.appendFormalArg(ufa);
                db.vl.addElement(matrix_mve);

                pdv1 = new PredDataValue(db, ufa.getID(), p5);
                pdv2 = new PredDataValue(db, ufa.getID(), p6);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( db == null ) ||
                 ( pred_mve == null ) ||
                 ( pfa == null ) ||
                 ( pdv0 == null ) ||
                 ( matrix_mve == null ) ||
                 ( ufa == null ) ||
                 ( pdv1 == null ) ||
                 ( pdv2 == null ) ||
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

                    if ( pred_mve == null )
                    {
                        outStream.print("allocation of pred_mve failed.\n");
                    }

                    if ( pfa == null )
                    {
                        outStream.print("allocation of pfa failed.\n");
                    }

                    if ( pdv0 == null )
                    {
                        outStream.print("allocation of pdv0 failed.\n");
                    }

                    if ( matrix_mve == null )
                    {
                        outStream.print("allocation of matrix_mve failed.\n");
                    }

                    if ( ufa == null )
                    {
                        outStream.print("allocation of ufa failed.\n");
                    }

                    if ( pdv1 == null )
                    {
                        outStream.print("allocation of pdv1 failed.\n");
                    }

                    if ( pdv2 == null )
                    {
                        outStream.print("allocation of pdv2 failed.\n");
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
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.TestAccessors(db, pfa, matrix_mve, ufa,
                                                pdv0, outStream, verbose);

            if ( pdv0.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getSubRange() != false");
                }
            }

            if ( pdv0.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (1).\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(p2,
                                                          pdv0.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p2",
                                                          "pdv0.itsValue");
            }

            pdv0.setItsValue(null);

            if ( pdv0.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (2).\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(new Predicate(db),
                                                          pdv0.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)1",
                                                          "pdv0.itsValue");
            }

            pdv0.setItsValue(p3);

            if ( pdv0.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (3).\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(p3,
                                                          pdv0.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p3",
                                                          "pdv0.itsValue");
            }

            pdv0.setItsValue(new Predicate(db));

            if ( pdv0.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (4).\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(new Predicate(db),
                                                          pdv0.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)2",
                                                          "pdv0.itsValue");
            }

            /************************************/

            if ( pdv1.getSubRange() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv1.getSubRange() != false\n");
                }
            }

            if ( pdv1.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv1.getItsValue() == null (1).\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(p5,
                                                          pdv1.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p5",
                                                          "pdv1.itsValue");
            }

            failures += DataValueTest.TestAccessors(db, ufa, pred_mve, pfa,
                                                pdv1, outStream, verbose);

            if ( pdv1.getSubRange() != true )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv1.getSubRange() != true\n");
                }
            }

            if ( pdv1.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv1.getItsValue() == null (2)\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(new Predicate(db),
                                                          pdv1.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)1",
                                                          "pdv1.itsValue");
            }

            pdv1.setItsValue(p4);

            if ( pdv1.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv1.getItsValue() == null (3).\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(p4,
                                                          pdv1.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p4",
                                                          "pdv1.itsValue");
            }

            pdv1.setItsValue(p7);

            if ( pdv1.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv1.getItsValue() == null (4)\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(new Predicate(db),
                                                          pdv1.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)2",
                                                          "pdv1.itsValue");
            }

            if ( ( pdv1.coerceToRange(p0) != p0 ) ||
                 ( pdv1.coerceToRange(p1) == null ) ||
                 ( pdv1.coerceToRange(p2) != p2 ) ||
                 ( pdv1.coerceToRange(p3) == null ) ||
                 ( pdv1.coerceToRange(p4) != p4 ) ||
                 ( pdv1.coerceToRange(p5) == null ) ||
                 ( pdv1.coerceToRange(p6) != p6 ) ||
                 ( pdv1.coerceToRange(p7) == null ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "unexpected results from pdv1.coerceToRange()\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(new Predicate(db),
                                                      pdv1.coerceToRange(p1),
                                                      outStream,
                                                      verbose,
                                                      "new Predicate(db)",
                                                      "pdv1.coerceToRange(p1)");
                failures += PredicateTest.VerifyPredicateCopy(new Predicate(db),
                                                      pdv1.coerceToRange(p3),
                                                      outStream,
                                                      verbose,
                                                      "new Predicate(db)",
                                                      "pdv1.coerceToRange(p3)");
                failures += PredicateTest.VerifyPredicateCopy(new Predicate(db),
                                                      pdv1.coerceToRange(p5),
                                                      outStream,
                                                      verbose,
                                                      "new Predicate(db)",
                                                      "pdv1.coerceToRange(p5)");
                failures += PredicateTest.VerifyPredicateCopy(new Predicate(db),
                                                      pdv1.coerceToRange(p7),
                                                      outStream,
                                                      verbose,
                                                      "new Predicate(db)",
                                                      "pdv1.coerceToRange(p7)");
            }

            /*********************************/

            failures += DataValueTest.TestAccessors(db, ufa, pred_mve, pfa,
                                                pdv2, outStream, verbose);

            if ( pdv2.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv2.getItsValue() == null(1).\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(p6,
                                                          pdv2.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p6",
                                                          "pdv2.itsValue");
            }

            pdv2.setItsValue(null);

            if ( pdv2.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (2).\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(new Predicate(db),
                                                          pdv2.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)1",
                                                          "pdv2.itsValue");
            }

            pdv2.setItsValue(p0);

            if ( pdv2.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (3).\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(p0,
                                                          pdv2.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "p0",
                                                          "pdv2.itsValue");
            }

            pdv2.setItsValue(new Predicate(db));

            if ( pdv2.getItsValue() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("pdv0.getItsValue() == null (4).\n");
                }
            }
            else
            {
                failures += PredicateTest.VerifyPredicateCopy(new Predicate(db),
                                                          pdv2.itsValue,
                                                          outStream,
                                                          verbose,
                                                          "new Predicate(db)2",
                                                          "pdv2.itsValue");
            }
        }

        /* For now at least, there is no real need to test setItsValue with
         * invalid values.  The compiler requires that the supplied parameter
         * is an instance of Predicate, and the value supplied (if not null or
         * an empty Predicate) is passed through to the target formal arguments
         * isValidValue routine.  Since we already have tests for these
         * routines, there is no need to test them here.
         *
         * That said, against changes in the code, it is probably worth while
         * to pass through an invalid predicate or two just to be sure.
         *
         * Start with setup for test:
         */

        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            completed = false;

            try
            {
                alt_db = new ODBCDatabase();

                alt_pve0 = new PredicateVocabElement(alt_db, "alt_pve0");
                farg = new UnTypedFormalArg(alt_db, "<arg1>");
                alt_pve0.appendFormalArg(farg);
                farg = new UnTypedFormalArg(alt_db, "<arg2>");
                alt_pve0.appendFormalArg(farg);

                alt_pve0ID = alt_db.addPredVE(alt_pve0);

                // get a copy alt_pve0 with ids assigned
                alt_pve0 = alt_db.getPredVE(alt_pve0ID);

                alt_p0 = new Predicate(alt_db, alt_pve0ID);


                alt_pve1 = new PredicateVocabElement(alt_db, "alt_pve1");
                farg = new IntFormalArg(alt_db, "<int>");
                alt_pve1.appendFormalArg(farg);
                farg = new UnTypedFormalArg(alt_db, "<arg2>");
                alt_pve1.appendFormalArg(farg);

                alt_pve1ID = alt_db.addPredVE(alt_pve1);

                // get a copy of pve1 with ids assigned
                alt_pve1 = alt_db.getPredVE(alt_pve1ID);

                alt_p1 = new Predicate(alt_db, alt_pve1ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( alt_db == null ) ||
                 ( alt_pve0 == null ) ||
                 ( alt_pve0ID == DBIndex.INVALID_ID ) ||
                 ( alt_p0 == null ) ||
                 ( alt_pve1 == null ) ||
                 ( alt_pve1ID == DBIndex.INVALID_ID ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( alt_db == null )
                    {
                        outStream.print("creation of alt_db failedl.\n");
                    }

                    if ( alt_pve0 == null )
                    {
                        outStream.print("creation of alt_pve0 failed.\n");
                    }

                    if ( alt_pve0ID == DBIndex.INVALID_ID )
                    {
                        outStream.print("alt_pve0ID not initialized.\n");
                    }

                    if ( alt_p0 == null )
                    {
                        outStream.print("creation of alt_p0 failed.\n");
                    }

                    if ( alt_pve1 == null )
                    {
                        outStream.print("creation of alt_pve1 failed.\n");
                    }

                    if ( alt_pve1ID == DBIndex.INVALID_ID )
                    {
                        outStream.print("alt_pve1ID not initialized.\n");
                    }

                    if ( alt_p1 == null )
                    {
                        outStream.print("creation of alt_p1 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete (1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("alt allocations threw a " +
                                "SystemErrorException: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            completed = false;

            try
            {
                pdv0.setItsValue(alt_p0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.printf(
                                "pdv0.setItsValue(alt_p0) completed.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("pdv0.setItsValue(alt_p0) failed " +
                                         "to thow a system error.\n");
                    }
                }
            }


            threwSystemErrorException = false;
            completed = false;

            try
            {
                pdv1.setItsValue(alt_p1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.printf(
                                "pdv1.setItsValue(alt_p1) completed.\n");
                    }

                    if ( completed )
                    {
                        outStream.printf("pdv1.setItsValue(alt_p1) failed " +
                                         "to thow a system error.\n");
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
    } /* PredDataValue::TestAccessors() */


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
    @Test
    public void TestCopyConstructor() throws SystemErrorException {
        String testBanner =
            "Testing copy constructor for class PredDataValue                 ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        long pve4ID = DBIndex.INVALID_ID;
        long pve5ID = DBIndex.INVALID_ID;
        long pve6ID = DBIndex.INVALID_ID;
        long pve7ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        PredicateVocabElement pve4 = null;
        PredicateVocabElement pve5 = null;
        PredicateVocabElement pve6 = null;
        PredicateVocabElement pve7 = null;
        MatrixVocabElement matrix_mve = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement pred_mve_sr = null;
        FormalArgument farg = null;
        PredFormalArg pfa = null;
        UnTypedFormalArg ufa = null;
        PredFormalArg pfa_sr = null;
        PredDataValue pdv = null;
        PredDataValue pdv0 = null;
        PredDataValue pdv0_copy = null;
        PredDataValue pdv1 = null;
        PredDataValue pdv1_copy = null;
        PredDataValue pdv2 = null;
        PredDataValue pdv2_copy = null;
        PredDataValue pdv3 = null;
        PredDataValue pdv3_copy = null;
        PredDataValue pdv4 = null;
        PredDataValue pdv4_copy = null;
        PredDataValue pdv_sr0 = null;
        PredDataValue pdv_sr0_copy = null;
        PredDataValue pdv_sr1 = null;
        PredDataValue pdv_sr1_copy = null;
        Predicate p0 = null;
        Predicate p1 = null;
        Predicate p2 = null;
        Predicate p3 = null;
        Predicate p4 = null;
        Predicate p5 = null;
        Predicate p6 = null;
        Predicate p7 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }


        // Start by setting up the needed database, pve's, and preds
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

            p0 = new Predicate(db, pve0ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);

            p1 = new Predicate(db, pve1ID);


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

            p2 = new Predicate(db, pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve3.appendFormalArg(farg);
            pve3.setVarLen(true);

            pve3ID = db.addPredVE(pve3);

            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);

            p3 = new Predicate(db, pve3ID);


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

            p4 = new Predicate(db, pve4ID);


            pve5 = new PredicateVocabElement(db, "pve5");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve5.appendFormalArg(farg);

            pve5ID = db.addPredVE(pve5);

            // get a copy of the databases version of pve5 with ids assigned
            pve5 = db.getPredVE(pve5ID);

            p5 = new Predicate(db, pve5ID);


            pve6 = new PredicateVocabElement(db, "pve6");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve6.appendFormalArg(farg);

            pve6ID = db.addPredVE(pve6);

            // get a copy of the databases version of pve6 with ids assigned
            pve6 = db.getPredVE(pve6ID);

            p6 = new Predicate(db, pve6ID);


            pve7 = new PredicateVocabElement(db, "pve7");
            farg = new UnTypedFormalArg(db, "<arg>");
            pve7.appendFormalArg(farg);

            pve7ID = db.addPredVE(pve7);

            // get a copy of the databases version of pve7 with ids assigned
            pve7 = db.getPredVE(pve7ID);

            p7 = new Predicate(db, pve7ID);


            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( p0 == null ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
             ( p1 == null ) ||
             ( pve2 == null ) ||
             ( pve2ID == DBIndex.INVALID_ID ) ||
             ( p2 == null ) ||
             ( pve3 == null ) ||
             ( pve3ID == DBIndex.INVALID_ID ) ||
             ( p3 == null ) ||
             ( pve4 == null ) ||
             ( pve4ID == DBIndex.INVALID_ID ) ||
             ( p4 == null ) ||
             ( pve5 == null ) ||
             ( pve5ID == DBIndex.INVALID_ID ) ||
             ( p5 == null ) ||
             ( pve6 == null ) ||
             ( pve6ID == DBIndex.INVALID_ID ) ||
             ( p6 == null ) ||
             ( pve7 == null ) ||
             ( pve7ID == DBIndex.INVALID_ID ) ||
             ( p7 == null ) ||
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

                if ( p0 == null )
                {
                    outStream.print("creation of p0 failed.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( p1 == null )
                {
                    outStream.print("creation of p1 failed.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve2ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve2ID not initialized.\n");
                }

                if ( p2 == null )
                {
                    outStream.print("creation of p2 failed.\n");
                }

                if ( pve3 == null )
                {
                    outStream.print("creation of pve3 failed.\n");
                }

                if ( pve3ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve3ID not initialized.\n");
                }

                if ( p3 == null )
                {
                    outStream.print("creation of p3 failed.\n");
                }

                if ( pve4 == null )
                {
                    outStream.print("creation of pve4 failed.\n");
                }

                if ( pve4ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve4ID not initialized.\n");
                }

                if ( p4 == null )
                {
                    outStream.print("creation of p4 failed.\n");
                }

                if ( pve5 == null )
                {
                    outStream.print("creation of pve5 failed.\n");
                }

                if ( pve5ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve5ID not initialized.\n");
                }

                if ( p5 == null )
                {
                    outStream.print("creation of p5 failed.\n");
                }

                if ( pve6 == null )
                {
                    outStream.print("creation of pve6 failed.\n");
                }

                if ( pve6ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve6ID not initialized.\n");
                }

                if ( p6 == null )
                {
                    outStream.print("creation of p6 failed.\n");
                }

                if ( pve7 == null )
                {
                    outStream.print("creation of pve7 failed.\n");
                }

                if ( pve7ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve7ID not initialized.\n");
                }

                if ( p7 == null )
                {
                    outStream.print("creation of p7 failed.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete (1).\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            systemErrorExceptionString);
                }
            }
        }

        /* Now create the instances of PredDataValue to be copied. */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            /* setup the base entries for the copy test */
            try
            {
                pdv0 = new PredDataValue(db);

                pred_mve = new MatrixVocabElement(db, "pred_mve");
                pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa = new PredFormalArg(db);
                pred_mve.appendFormalArg(pfa);
                db.vl.addElement(pred_mve);

                pdv1 = new PredDataValue(db, pfa.getID());
                pdv2 = new PredDataValue(db, pfa.getID(), p3);


                matrix_mve = new MatrixVocabElement(db, "matrix_mve");
                matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
                ufa = new UnTypedFormalArg(db);
                matrix_mve.appendFormalArg(ufa);
                db.vl.addElement(matrix_mve);

                pdv3 = new PredDataValue(db, ufa.getID());
                pdv4 = new PredDataValue(db, ufa.getID(), p4);


                pred_mve_sr = new MatrixVocabElement(db, "pred_mve_sr");
                pred_mve_sr.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa_sr = new PredFormalArg(db);
                pfa_sr.setSubRange(true);
                pfa_sr.addApproved(pve0ID);
                pfa_sr.addApproved(pve2ID);
                pfa_sr.addApproved(pve4ID);
                pfa_sr.addApproved(pve6ID);
                pred_mve_sr.appendFormalArg(pfa_sr);
                db.vl.addElement(pred_mve_sr);

                pdv_sr0 = new PredDataValue(db, pfa_sr.getID());
                pdv_sr1 = new PredDataValue(db, pfa_sr.getID(), p0);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( db == null ) ||
                 ( pdv0 == null ) ||
                 ( pred_mve == null ) ||
                 ( pfa == null ) ||
                 ( pdv1 == null ) ||
                 ( pdv2 == null ) ||
                 ( matrix_mve == null ) ||
                 ( ufa == null ) ||
                 ( pdv3 == null ) ||
                 ( pdv4 == null ) ||
                 ( pred_mve_sr == null ) ||
                 ( pfa_sr == null ) ||
                 ( pdv_sr0 == null ) ||
                 ( pdv_sr1 == null ) ||
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

                    if ( pdv0 == null )
                    {
                        outStream.print("allocation of pdv0 failed\n");
                    }

                    if ( pred_mve == null )
                    {
                        outStream.print("allocation of pred_mve failed.\n");
                    }

                    if ( pfa == null )
                    {
                        outStream.print("allocation of pfa failed.");
                    }

                    if ( pdv1 == null )
                    {
                        outStream.print("allocation of pdv1 failed\n");
                    }

                    if ( pdv2 == null )
                    {
                        outStream.print("allocation of pdv2 failed\n");
                    }

                    if ( matrix_mve == null )
                    {
                        outStream.print("allocation of matrix_mve failed.\n");
                    }

                    if ( ufa == null )
                    {
                        outStream.print("allocation of ufa failed.");
                    }

                    if ( pdv3 == null )
                    {
                        outStream.print("allocation of pdv3 failed\n");
                    }

                    if ( pdv4 == null )
                    {
                        outStream.print("allocation of pdv4 failed\n");
                    }

                    if ( pred_mve_sr == null )
                    {
                        outStream.print("allocation of pred_mve_sr failed.\n");
                    }

                    if ( pfa_sr == null )
                    {
                        outStream.print("allocation of pfa_sr failed.");
                    }

                    if ( pdv_sr0 == null )
                    {
                        outStream.print("allocation of pdv_sr0 failed.\n");
                    }

                    if ( pdv_sr1 == null )
                    {
                        outStream.print("allocation of pdv_sr1 failed.\n");
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
        }

        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            /* setup the base entries for the copy test */
            try
            {
                pdv0_copy = new PredDataValue(pdv0);
                pdv1_copy = new PredDataValue(pdv1);
                pdv2_copy = new PredDataValue(pdv2);
                pdv3_copy = new PredDataValue(pdv3);
                pdv4_copy = new PredDataValue(pdv4);
                pdv_sr0_copy = new PredDataValue(pdv_sr0);
                pdv_sr1_copy = new PredDataValue(pdv_sr1);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv0_copy == null ) ||
                 ( pdv1_copy == null ) ||
                 ( pdv2_copy == null ) ||
                 ( pdv3_copy == null ) ||
                 ( pdv4_copy == null ) ||
                 ( pdv_sr0_copy == null ) ||
                 ( pdv_sr1_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pdv0_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv0) returned null.\n");
                    }

                    if ( pdv1_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv1) returned null.\n");
                    }

                    if ( pdv2_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv2) returned null.\n");
                    }

                    if ( pdv3_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv3) returned null.\n");
                    }

                    if ( pdv4_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv4) returned null.\n");
                    }

                    if ( pdv_sr0_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv_sr0) returned null.\n");
                    }

                    if ( pdv_sr1_copy == null )
                    {
                        outStream.print(
                            "new PredDataValue(pdv_sr1) returned null.\n");
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
            failures += DataValueTest.VerifyDVCopy(pdv0, pdv0_copy, outStream,
                                               verbose, "pdv0", "pdv0_copy");

            failures += DataValueTest.VerifyDVCopy(pdv1, pdv1_copy, outStream,
                                               verbose, "pdv1", "pdv1_copy");

            failures += DataValueTest.VerifyDVCopy(pdv2, pdv2_copy, outStream,
                                               verbose, "pdv2", "pdv2_copy");

            failures += DataValueTest.VerifyDVCopy(pdv3, pdv3_copy, outStream,
                                               verbose, "pdv3", "pdv3_copy");

            failures += DataValueTest.VerifyDVCopy(pdv4, pdv4_copy, outStream,
                                               verbose, "pdv4", "pdv4_copy");

            failures += DataValueTest.VerifyDVCopy(pdv_sr0, pdv_sr0_copy, outStream,
                                            verbose, "pdv_sr0", "pdv_sr0_copy");

            failures += DataValueTest.VerifyDVCopy(pdv_sr1, pdv_sr1_copy, outStream,
                                            verbose, "pdv_sr1", "pdv_sr1_copy");
        }


        /* verify that the constructor fails when given an invalid dv */
        if ( failures == 0 )
        {
            pdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pdv = new PredDataValue((PredDataValue)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "new PredDataValue(null) completed.\n");
                    }

                    if ( pdv != null )
                    {
                        outStream.print(
                            "new PredDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new PredDataValue(null) failed " +
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

        assertTrue(pass);
    } /* PredDataValue::TestCopyConstructor() */


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
    @Test
    public void TestToStringMethods() throws SystemErrorException {
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String testString0 = "pve0(<arg1>, <arg2>)";
        String testDBString0 =
                "(PredDataValue (id 100) " +
                            "(itsFargID 14) " +
                            "(itsFargType PREDICATE) " +
                            "(itsCellID 500) " +
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
                            "(subRange true))";
        String testString1 = "pve3(<arg1>)";
        String testDBString1 =
                "(PredDataValue (id 101) " +
                            "(itsFargID 20) " +
                            "(itsFargType UNTYPED) " +
                            "(itsCellID 501) " +
                            "(itsValue " +
                                "(predicate (id 0) " +
                                    "(predID 11) " +
                                    "(predName pve3) " +
                                    "(varLen true) " +
                                    "(argList " +
                                        "((UndefinedDataValue (id 0) " +
                                            "(itsFargID 12) " +
                                            "(itsFargType UNTYPED) " +
                                            "(itsCellID 0) " +
                                            "(itsValue <arg1>) " +
                                            "(subRange false))))))) " +
                                    "(subRange false))";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        Database db = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        MatrixVocabElement pred_mve = null;
        MatrixVocabElement matrix_mve = null;
        FormalArgument farg = null;
        PredFormalArg pfa = null;
        UnTypedFormalArg ufa = null;
        PredDataValue pdv0 = null;
        PredDataValue pdv1 = null;
        Predicate p0 = null;
        Predicate p1 = null;
        Predicate p2 = null;
        Predicate p3 = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // Start by setting up the needed database, pve's, and preds
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

            p0 = new Predicate(db, pve0ID);


            pve1 = new PredicateVocabElement(db, "pve1");
            farg = new IntFormalArg(db, "<int>");
            pve1.appendFormalArg(farg);
            farg = new UnTypedFormalArg(db, "<arg2>");
            pve1.appendFormalArg(farg);

            pve1ID = db.addPredVE(pve1);

            // get a copy of the databases version of pve1 with ids assigned
            pve1 = db.getPredVE(pve1ID);

            p1 = new Predicate(db, pve1ID);


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

            p2 = new Predicate(db, pve2ID);


            pve3 = new PredicateVocabElement(db, "pve3");
            farg = new UnTypedFormalArg(db, "<arg1>");
            pve3.appendFormalArg(farg);
            pve3.setVarLen(true);

            pve3ID = db.addPredVE(pve3);

            // get a copy of the databases version of pve3 with ids assigned
            pve3 = db.getPredVE(pve3ID);

            p3 = new Predicate(db, pve3ID);


            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.toString();
        }

        if ( ( db == null ) ||
             ( pve0 == null ) ||
             ( pve0ID == DBIndex.INVALID_ID ) ||
             ( p0 == null ) ||
             ( pve1 == null ) ||
             ( pve1ID == DBIndex.INVALID_ID ) ||
             ( p1 == null ) ||
             ( pve2 == null ) ||
             ( pve2ID == DBIndex.INVALID_ID ) ||
             ( p2 == null ) ||
             ( pve3 == null ) ||
             ( pve3ID == DBIndex.INVALID_ID ) ||
             ( p3 == null ) ||
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

                if ( p0 == null )
                {
                    outStream.print("creation of p0 failed.\n");
                }

                if ( pve1 == null )
                {
                    outStream.print("creation of pve1 failed.\n");
                }

                if ( pve1ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve1ID not initialized.\n");
                }

                if ( p1 == null )
                {
                    outStream.print("creation of p1 failed.\n");
                }

                if ( pve2 == null )
                {
                    outStream.print("creation of pve2 failed.\n");
                }

                if ( pve2ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve2ID not initialized.\n");
                }

                if ( p2 == null )
                {
                    outStream.print("creation of p2 failed.\n");
                }

                if ( pve3 == null )
                {
                    outStream.print("creation of pve3 failed.\n");
                }

                if ( pve3ID == DBIndex.INVALID_ID )
                {
                    outStream.print("pve3ID not initialized.\n");
                }

                if ( p3 == null )
                {
                    outStream.print("creation of p3 failed.\n");
                }

                if ( ! completed )
                {
                    outStream.print("test setup failed to complete (1).\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("pve allocations threw a " +
                            "SystemErrorException: \"%s\".\n",
                            systemErrorExceptionString);
                }
            }
        }


        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pred_mve = new MatrixVocabElement(db, "pred_mve");
                pred_mve.setType(MatrixVocabElement.MatrixType.PREDICATE);
                pfa = new PredFormalArg(db);
                pfa.setSubRange(true);
                pfa.addApproved(pve0ID);
                pfa.addApproved(pve2ID);
                pred_mve.appendFormalArg(pfa);
                db.vl.addElement(pred_mve);

                pdv0 = new PredDataValue(db, pfa.getID(), p0);
                pdv0.setID(100);        // invalid value for print test
                pdv0.itsCellID = 500; // invalid value for print test

                matrix_mve = new MatrixVocabElement(db, "matrix_mve");
                matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
                ufa = new UnTypedFormalArg(db, "<untyped>");
                matrix_mve.appendFormalArg(ufa);
                db.vl.addElement(matrix_mve);

                pdv1 = new PredDataValue(db, ufa.getID(), p3);
                pdv1.setID(101);      // invalid value for print test
                pdv1.itsCellID = 501; // invalid value for print test

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( db == null ) ||
                 ( pred_mve == null ) ||
                 ( pfa == null ) ||
                 ( pdv0 == null ) ||
                 ( matrix_mve == null ) ||
                 ( ufa == null ) ||
                 ( pdv1 == null ) ||
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

                    if ( pred_mve == null )
                    {
                        outStream.print("allocation of pred_mve failed.\n");
                    }

                    if ( pfa == null )
                    {
                        outStream.print("allocation of pfa failed.\n");
                    }

                    if ( pdv0 == null )
                    {
                        outStream.print("allocation of pdv0 failed.\n");
                    }

                    if ( matrix_mve == null )
                    {
                        outStream.print("allocation of matrix_mve failed.\n");
                    }

                    if ( ufa == null )
                    {
                        outStream.print("allocation of ufa failed.\n");
                    }

                    if ( pdv1 == null )
                    {
                        outStream.print("allocation of pdv1 failed.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.printf("Test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Test setup threw a system error " +
                                "exception: \"%s\"",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( pdv0.toString().compareTo(testString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pdv0.toString(): \"%s\".\n",
                                     pdv0.toString());
                }
            }

            if ( pdv0.toDBString().compareTo(testDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pdv0.toDBString(): \"%s\".\n",
                                     pdv0.toDBString());
                }
            }

            if ( pdv1.toString().compareTo(testString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pdv1.toString(): \"%s\".\n",
                                     pdv1.toString());
                }
            }

            if ( pdv1.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected pdv1.toDBString(): \"%s\".\n",
                                     pdv1.toDBString());
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
    } /* PredDataValue::TestToStringMethods() */


    /**
     * VerifyPredDVCopy()
     *
     * Verify that the supplied instances of PredDataValue are distinct, that
     * they contain no common references (other than db), and that they have
     * the same value.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyPredDVCopy(PredDataValue base,
                                       PredDataValue copy,
                                       java.io.PrintStream outStream,
                                       boolean verbose,
                                       String baseDesc,
                                       String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyPredDVCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyPredDVCopy: %s null on entry.\n",
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
        else if ( ( base.itsValue == copy.itsValue ) &&
                  ( base.itsValue != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s share a Predicate.\n",
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
                  ( base.toString().compareTo(copy.toString()) != 0 ) )
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
        else if ( base.itsValue != null )
        {
            failures += PredicateTest.VerifyPredicateCopy(base.itsValue,
                                                      copy.itsValue,
                                                      outStream, verbose,
                                                      baseDesc + ".itsValue",
                                                      copyDesc + "itsValue");
        }

        return failures;

    } /* PredDataValue::VerifyPredDVCopy() */

}
