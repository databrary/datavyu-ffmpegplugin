package org.openshapa.models.db;

import java.io.PrintStream;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

/**
 * Junit test cases for the class NominalDataValue.
 */
public class NominalDataValueTest extends DataValueTest {
    private Database db;
    private MatrixVocabElement nom_mve;
    private NominalFormalArg nfa;
    private NominalDataValue ndv;

    private MatrixVocabElement nom_mve2;
    private NominalFormalArg nfa2;

    private MatrixVocabElement matrix_mve;
    private UnTypedFormalArg ufa;

    private PrintStream outStream;
    private boolean verbose;

    /**
     * Default test constructor.
     */
    public NominalDataValueTest() {
    }

    @Override
    public DataValue getInstance() {
        return ndv;
    }

    /**
     * Sets up the test fixture (i.e. the data available to all tests), this is
     * performed before each test case.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @BeforeMethod
    public void setUp() throws SystemErrorException {
        db = new ODBCDatabase();
        nom_mve = new MatrixVocabElement(db, "nom_mve");
        nom_mve.setType(MatrixVocabElement.MatrixType.NOMINAL);
        nfa = new NominalFormalArg(db);
        nom_mve.appendFormalArg(nfa);
        db.vl.addElement(nom_mve);

        nom_mve2 = new MatrixVocabElement(db, "nom_mve2");
        nom_mve2.setType(MatrixVocabElement.MatrixType.NOMINAL);
        nfa2 = new NominalFormalArg(db);
        nfa2.setSubRange(true);
        nfa2.addApproved("alpha");
        nfa2.addApproved("bravo");
        nfa2.addApproved("charlie");
        nom_mve2.appendFormalArg(nfa2);
        db.vl.addElement(nom_mve2);

        matrix_mve = new MatrixVocabElement(db, "matrix_mve");
        matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
        ufa = new UnTypedFormalArg(db, "<untyped>");
        matrix_mve.appendFormalArg(ufa);
        db.vl.addElement(matrix_mve);
        ndv = new NominalDataValue(db);

        outStream = System.out;
        verbose = true;
    }

    /**
     * Tears down the test fixture (i.e. the data available to all tests), this
     * is performed after each test case.
     */
    @AfterMethod
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

    /**
     * Test 1 arg constructor, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db);

        assertNotNull(db);
        assertNotNull(n_value);

        assertEquals(n_value.getDB(), db);
        assertEquals(n_value.itsValue, n_value.ItsDefault);
    }

    /**
     * Test 1 argument constructor failure, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test1ArgConstructorFailure() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue((Database) null);
    }

    /**
     * Test 2 argument constructor, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void test2ArgConstructor() throws SystemErrorException {
        NominalDataValue n_value0 = new NominalDataValue(db, nfa.getID());
        NominalDataValue n_value1 = new NominalDataValue(db, nfa2.getID());

        assertNotNull(db);
        assertNotNull(nom_mve);
        assertNotNull(nfa);
        assertNotNull(nom_mve2);
        assertNotNull(nfa2);

        assertNotNull(n_value0);
        assertNotNull(n_value1);

        assertEquals(n_value0.ItsDefault, null);
        assertEquals(n_value0.subRange, nfa.subRange);
        assertEquals(n_value0.itsValue, n_value0.ItsDefault);

        assertEquals(n_value1.ItsDefault, null);
        assertEquals(n_value1.subRange, nfa2.getSubRange());
        assertEquals(n_value1.itsValue, n_value1.ItsDefault);
    }

    /**
     * Test0 of 2 arg constructor failre, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue((Database)null,
                                                        nfa.getID());
    }

    /**
     * Test1 of 2 arg constructor failre, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, DBIndex.INVALID_ID);
    }

    /**
     * Test2 of 2 arg constructor failre, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, nom_mve.getID());
    }

    /**
     * Test of 3 argument constructor, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void test3ArgConstructor() throws SystemErrorException {
        NominalDataValue n_value0 = new NominalDataValue(db, nfa.getID(),
                                                         "echo");
        NominalDataValue n_value1 = new NominalDataValue(db, nfa2.getID(),
                                                         "alpha");
        NominalDataValue n_value2 = new NominalDataValue(db, nfa2.getID(),
                                                         "delta");

        assertNotNull(db);
        assertNotNull(nom_mve);
        assertNotNull(nfa);
        assertNotNull(nom_mve2);
        assertNotNull(nfa2);

        assertNotNull(n_value0);
        assertNotNull(n_value1);
        assertNotNull(n_value2);

        assertEquals(n_value0.subRange, nfa.getSubRange());
        assertEquals(n_value0.itsValue, "echo");

        assertEquals(n_value1.subRange, nfa2.getSubRange());
        assertEquals(n_value1.itsValue, "alpha");

        assertEquals(n_value2.subRange, nfa2.getSubRange());
        assertNull(n_value2.itsValue);
    }

    /**
     * Test0 of 3 argument constructor failure, of class NominalDataValue
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test3ArgConstructorFailure0() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue((Database) null,
                                                        nfa.getID(), "alpha");
    }

    /**
     * Test1 of 3 argument constructor failure, of class NominalDataValue
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test3ArgConstructorFailure1() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, DBIndex.INVALID_ID,
                                                        "alpha");
    }

    /**
     * Test2 of 3 argument constructor failure, of class NominalDataValue
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void test3ArgConstructorFailure2() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, nom_mve.getID(),
                                                        "alpha");
    }

    /**
     * Test of copy constructor, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void testCopyConstructor() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, nfa.getID(),
                                                        "foxtrot");
        NominalDataValue n_copy = new NominalDataValue(n_value);

        assertNotSame(n_value, n_copy);
        assertEquals(n_value.getDB(), n_copy.getDB());
        assertEquals(n_value.itsFargID, n_copy.itsFargID);
        assertEquals(n_value.itsFargType, n_copy.itsFargType);
        assertEquals(n_value.subRange, n_copy.subRange);
        assertEquals(n_value.toString(), n_copy.toString());
        assertEquals(n_value.toDBString(), n_copy.toDBString());
        assertEquals(n_value.getClass(), n_copy.getClass());
    }

    /**
     * Test of copy constructor failure, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test (expectedExceptions = SystemErrorException.class)
    public void testCopyConstructorFailure() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue((NominalDataValue)null);
    }

    /**
     * Test of getItsValue method, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void testGetItsValue() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, nfa.getID(),
                                                        "bravo");

        assertEquals(n_value.getItsValue(), "bravo");
    }

    /**
     * Test of setItsValue method, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void testSetItsValue() throws SystemErrorException {
        NominalDataValue n_value = new NominalDataValue(db, nfa.getID(),
                                                        "bravo");

        n_value.setItsValue("echo");
        assertEquals(n_value.getItsValue(), "echo");
    }

    /**
     * Test of toString method, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void testToString() throws SystemErrorException {
        NominalDataValue n_value0 = new NominalDataValue(db, nfa.getID(),
                                                         "bravo");
        NominalDataValue n_value1 = new NominalDataValue(db, ufa.getID(),
                                                         "nero");

        assertEquals(n_value0.toString(), "bravo");
        assertEquals(n_value1.toString(), "nero");
    }

    /**
     * Test of toDBString method, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    /*
    @Test
    public void testToDBString() throws SystemErrorException {
        String testDBString0 = "(NominalDataValue (id 0) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType NOMINAL) " +
                                    "(itsCellID 0) " +
                                    "(itsValue bravo) " +
                                    "(subRange true))";

        String testDBString1 = "(NominalDataValue (id 0) " +
                                    "(itsFargID 8) " +
                                    "(itsFargType UNTYPED) " +
                                    "(itsCellID 0) " +
                                    "(itsValue nero) " +
                                    "(subRange false))";

        NominalDataValue n_value0 = new NominalDataValue(db, nfa2.getID(),
                                                         "bravo");

        NominalDataValue n_value1 = new NominalDataValue(db, ufa.getID(),
                                                         "nero");

        assertEquals(n_value0.toDBString(), testDBString0);
        assertEquals(n_value1.toDBString(), testDBString1);
    }
     */

    /**
     * Test of coerceToRange method, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void testCoerceToRange() throws Exception {
        NominalDataValue n_value = new NominalDataValue(db, nfa2.getID(),
                                                        "bravo");

        assertEquals(n_value.coerceToRange("alpha"), "alpha");
        assertEquals(n_value.coerceToRange("bravo"), "bravo");
        assertEquals(n_value.coerceToRange("charlie"), "charlie");
        assertNull(n_value.coerceToRange("echo"));
        assertNull(n_value.coerceToRange("alph"));
        assertNull(n_value.coerceToRange("alphaa"));
        assertNull(n_value.coerceToRange("charly"));
    }

    // TODO: Delete this eventually
//    /**
//     * Test of isQueryVar method, of class NominalDataValue.
//     *
//     * @throws org.openshapa.models.db.SystemErrorException on failure.
//     */
//    @Test
//    public void testIsQueryVar() throws SystemErrorException {
//        NominalDataValue n_value0 = new NominalDataValue(db, ufa.getID(),
//                                                         "?query_var");
//        NominalDataValue n_value1 = new NominalDataValue(db, ufa.getID(),
//                                                         "!?query_var");
//        NominalDataValue n_value2 = new NominalDataValue(db, ufa.getID(), "?");
//
//        assertEquals(n_value0.isQueryVar(), true);
//        assertEquals(n_value1.isQueryVar(), false);
//        assertEquals(n_value2.isQueryVar(), true);
//    }

    /**
     * Test of Construct method, of class NominalDataValue.
     *
     * @throws org.openshapa.models.db.SystemErrorException on failure.
     */
    @Test
    public void testConstruct() throws SystemErrorException {
        NominalDataValue test = NominalDataValue.Construct(db, "alpha");
        assertEquals(test.getItsValue(), "alpha");
    }

    @Test
    public void testHashCode() throws SystemErrorException {
        NominalDataValue value0 =
                                new NominalDataValue(db, nfa.getID(), "bravo");
        NominalDataValue value1 =
                                new NominalDataValue(db, nfa.getID(), "bravo");
        NominalDataValue value2 =
                              new NominalDataValue(db, nfa.getID(), "charlie");

        super.testHashCode(value0, value1, value2);
    }

    @Test
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        NominalDataValue value0 =
                                new NominalDataValue(db, nfa.getID(), "bravo");
        NominalDataValue value1 =
                                new NominalDataValue(db, nfa.getID(), "bravo");
        NominalDataValue value2 =
                                new NominalDataValue(db, nfa.getID(), "bravo");
        NominalDataValue value3 =
                              new NominalDataValue(db, nfa.getID(), "charlie");

        super.testEquals(value0, value1, value2, value3);
    }

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
     * Test1ArgConstructor()
     *
     * Run a battery of tests on the one argument constructor for this
     * class, and on the instance returned.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test1ArgConstructor() {
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
            failures += DataValueTest.Verify1ArgInitialization(db, ndv, outStream,
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

        assertTrue(pass);
    } /* NominalDataValue::Test1ArgConstructor() */


    /**
     * Test2ArgConstructor()
     *
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instance returned.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test2ArgConstructor() throws SystemErrorException {
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
        } catch (SystemErrorException e) {
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
            failures += DataValueTest.Verify2PlusArgInitialization(db,
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

            failures += DataValueTest.Verify2PlusArgInitialization(db,
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

        assertTrue(pass);
    } /* NominalDataValue::Test2ArgConstructor() */


    /**
     * Test3ArgConstructor()
     *
     * Run a battery of tests on the three argument constructor for this
     * class, and on the instances returned.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void Test3ArgConstructor() throws SystemErrorException {
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
        } catch (SystemErrorException e) {
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
            failures += DataValueTest.Verify2PlusArgInitialization(db,
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

            failures += DataValueTest.Verify2PlusArgInitialization(db,
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

            failures += DataValueTest.Verify2PlusArgInitialization(db,
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

        assertTrue(pass);
    } /* NominalDataValue::Test3ArgConstructor() */


    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors supported by this class.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - Added test code for the new isQueryVar() method.
     *
     *                                               -- 10/20/08
     */
    @Test
    public void TestAccessors() throws SystemErrorException {
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
        } catch (SystemErrorException e) {
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
            failures += DataValueTest.TestAccessors(db, nfa, matrix_mve, ufa,
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

            failures += DataValueTest.TestAccessors(db, ufa, nom_mve, nfa,
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

            failures += DataValueTest.TestAccessors(db, ufa, nom_mve, nfa,
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

        // TODO: remove this eventually
//        if ( failures == 0 )
//        {
//            if ( ( ndv3.isQueryVar() != true ) ||
//                 ( ndv4.isQueryVar() != false ) ||
//                 ( ndv5.isQueryVar() != true ) )
//            {
//                failures++;
//
//                if ( verbose )
//                {
//                    if ( ndv3.isQueryVar() != true )
//                    {
//                        outStream.printf(
//                                "ndv3.isQueryVar() != true for val = \"%s\"\n",
//                                ndv3.getItsValue());
//                    }
//
//                    if ( ndv4.isQueryVar() != false )
//                    {
//                        outStream.printf(
//                                "ndv4.isQueryVar() != false for val = \"%s\"\n",
//                                ndv4.getItsValue());
//                    }
//
//                    if ( ndv5.isQueryVar() != true )
//                    {
//                        outStream.printf(
//                                "ndv5.isQueryVar() != true for val = \"%s\"\n",
//                                ndv5.getItsValue());
//                    }
//                }
//            }
//
//            completed = false;
//            threwSystemErrorException = false;
//            systemErrorExceptionString = null;
//
//            try
//            {
//                ndv3.setItsValue("charlie");
//                ndv4.setItsValue("?ord");
//                ndv5.setItsValue("?1");
//
//                completed = true;
//            }
//
//            catch (SystemErrorException e)
//            {
//                threwSystemErrorException = true;
//                systemErrorExceptionString = e.getMessage();
//            }
//
//            if ( ( ndv3.isQueryVar() != false ) ||
//                 ( ndv4.isQueryVar() != true ) ||
//                 ( ndv5.isQueryVar() != true ) ||
//                 ( ! completed ) ||
//                 ( threwSystemErrorException ) )
//            {
//                failures++;
//
//                if ( verbose )
//                {
//                    if ( ndv3.isQueryVar() != false )
//                    {
//                        outStream.printf(
//                                "ndv3.isQueryVar() != false for val = \"%s\"\n",
//                                ndv3.getItsValue());
//                    }
//
//                    if ( ndv4.isQueryVar() != true )
//                    {
//                        outStream.printf(
//                                "ndv4.isQueryVar() != true for val = \"%s\"\n",
//                                ndv4.getItsValue());
//                    }
//
//                    if ( ndv5.isQueryVar() != true )
//                    {
//                        outStream.printf(
//                                "ndv5.isQueryVar() != true for val = \"%s\"\n",
//                                ndv5.getItsValue());
//                    }
//
//                    if ( ! completed )
//                    {
//                        outStream.printf("Query var setItsValue test " +
//                                         "failed to complete.\n");
//                    }
//
//                    if ( threwSystemErrorException )
//                    {
//                        outStream.printf("Query var setItsValue test threw " +
//                                "a system error exception: \"%s\"",
//                                systemErrorExceptionString);
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

        assertTrue(pass);
    } /* NominalDataValue::TestAccessors() */


    /**
     * TestCopyConstructor()
     *
     * Run a battery of tests on the copy constructor for this
     * class, and on the instances returned.
     *
     *                                               -- 11/13/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestCopyConstructor() throws SystemErrorException {
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
        } catch (SystemErrorException e) {
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
            failures += DataValueTest.VerifyDVCopy(ndv, ndv_copy, outStream,
                                               verbose, "ndv", "ndv_copy");

            failures += DataValueTest.VerifyDVCopy(ndv_sr0, ndv_sr0_copy, outStream,
                                            verbose, "ndv_sr0", "ndv_sr0_copy");

            failures += DataValueTest.VerifyDVCopy(ndv_sr1, ndv_sr1_copy, outStream,
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

        assertTrue(pass);
    } /* NominalDataValue::TestCopyConstructor() */


    /**
     * TestToStringMethods()
     *
     * Run a battery of tests on the toString methods supported by
     * this class.
     *
     *                                               -- 11/13/07
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
            ndv0.setID(100);        // invalid value for print test
            ndv0.itsCellID = 500; // invalid value for print test

            matrix_mve = new MatrixVocabElement(db, "matrix_mve");
            matrix_mve.setType(MatrixVocabElement.MatrixType.MATRIX);
            ufa = new UnTypedFormalArg(db, "<untyped>");
            matrix_mve.appendFormalArg(ufa);
            db.vl.addElement(matrix_mve);

            ndv1 = new NominalDataValue(db, ufa.getID(), "nero");
            ndv1.setID(101);        // invalid value for print test
            ndv1.itsCellID = 501; // invalid value for print test

            completed = true;
        } catch (SystemErrorException e) {
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

        assertTrue(pass);
    } /* NominalDataValue::TestToStringMethods() */


    /**
     * VerifyNominalDVCopy()
     *
     * Verify that the supplied instances of NominalDataValue are distinct,
     * that they contain no common references (other than db), and that they
     * have the same value.
     *                                               -- 11/8/07
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

}