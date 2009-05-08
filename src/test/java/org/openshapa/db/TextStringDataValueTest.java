package org.openshapa.db;

import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test cases for the class TextStringDataValue.
 *
 * @author cfreeman
 */
public class TextStringDataValueTest extends DataValueTest {
    Database db;

    MatrixVocabElement txt_mve;
    TextStringFormalArg tfa;
    TextStringDataValue tsdv;

    MatrixVocabElement txt_mve2;
    TextStringFormalArg tfa2;

    private PrintStream outStream;
    private boolean verbose;

    @Override
    public DataValue getInstance() {
        return tsdv;
    }

    /**
     * Default test constructor.
     */
    public TextStringDataValueTest() {
    }

    /**
     * Sets up the test fixture (i.e. the data available to all tests), this is
     * performed before each test case.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Before
    public void setUp() throws SystemErrorException, LogicErrorException {
        db = new ODBCDatabase();

        txt_mve = new MatrixVocabElement(db, "txt_mve");
        txt_mve.setType(MatrixVocabElement.MatrixType.TEXT);
        tfa = new TextStringFormalArg(db);
        txt_mve.appendFormalArg(tfa);
        db.vl.addElement(txt_mve);

        txt_mve2 = new MatrixVocabElement(db, "txt_mve2");
        txt_mve2.setType(MatrixVocabElement.MatrixType.TEXT);
        tfa2 = new TextStringFormalArg(db);
        txt_mve2.appendFormalArg(tfa2);
        db.vl.addElement(txt_mve2);
        tsdv = new TextStringDataValue(db);

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
        TextStringDataValue t_value = new TextStringDataValue(db, tfa.getID(),
                                                              "bravo");
        assertEquals(t_value.getSubRange(), false);
    }

    /**
     * Test 1 arg constructor, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test1ArgConstructor() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db);

        assertNotNull(db);
        assertNotNull(t_value);

        assertNull(t_value.ItsDefault);
        assertEquals(t_value.itsValue, t_value.ItsDefault);
    }

    /**
     * Test 1 argument constructor failure, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test1ArgConstructorFailure() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue((Database) null);
    }

    /**
     * Test 2 argument constructor, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test2ArgConstructor() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db, tfa.getID());

        assertNotNull(db);
        assertNotNull(txt_mve);
        assertNotNull(tfa);
        assertNotNull(t_value);

        assertNull(t_value.ItsDefault);
        assertEquals(t_value.subRange, tfa.getSubRange());
        assertEquals(t_value.itsValue, t_value.ItsDefault);
    }

    /**
     * Test0 of 2 arg constructor failre, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure0() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue((Database) null,
                                                              tfa.getID());
    }

    /**
     * Test1 of 2 arg constructor failre, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure1() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db,
                                                            DBIndex.INVALID_ID);
    }

    /**
     * Test2 of 2 arg constructor failre, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test2ArgConstructorFailure2() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db,
                                                              txt_mve.getID());
    }

    /**
     * Test of 3 argument constructor, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void test3ArgConstructor() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db, tfa.getID(),
                                                              "echo");

        assertNotNull(db);
        assertNotNull(txt_mve);
        assertNotNull(tfa);
        assertNotNull(t_value);

        assertEquals(t_value.subRange, tfa.getSubRange());
        assertNotNull(t_value.itsValue);
        assertEquals(t_value.itsValue, "echo");
    }

    /**
     * Test0 of 3 argument constructor failure, of class TextStringDataValue
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure0() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue((Database) null,
                                                              tfa.getID(),
                                                              "alpha");
    }

    /**
     * Test1 of 3 argument constructor failure, of class TextStringDataValue
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure1() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db,
                                                             DBIndex.INVALID_ID,
                                                             "alpha");
    }

    /**
     * Test2 of 3 argument constructor failure, of class TextStringDataValue
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure2() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db,
                                                              txt_mve.getID(),
                                                              "alpha");
    }

    /**
     * Test3 of 3 argument constructor failure, of class TextStringDataValue
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void test3ArgConstructorFailure3() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db,
                                                              tfa.getID(),
                                                      "invalid \b text string");
    }

    /**
     * Test of copy constructor, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testCopyConstructor() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db, tfa.getID(),
                                                               "foxtrot");
        TextStringDataValue t_copy = new TextStringDataValue(t_value);

        assertNotSame(t_value, t_copy);
        assertEquals(t_value.getDB(), t_copy.getDB());
        assertEquals(t_value.itsFargID, t_copy.itsFargID);
        assertEquals(t_value.itsFargType, t_copy.itsFargType);
        assertEquals(t_value.subRange, t_copy.subRange);
        assertEquals(t_value.toString(), t_copy.toString());
        assertEquals(t_value.toDBString(), t_copy.toDBString());
        assertEquals(t_value.getClass(), t_copy.getClass());
    }

    /**
     * Test of copy constructor failure, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test (expected = SystemErrorException.class)
    public void testCopyConstructorFailure() throws SystemErrorException {
        TextStringDataValue t_value =
                            new TextStringDataValue((TextStringDataValue) null);
    }

    /**
     * Test of getItsValue method, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testGetItsValue() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db, tfa.getID(),
                                                              "bravo");

        assertEquals(t_value.getItsValue(), "bravo");
    }

    /**
     * Test of setItsValue method, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testSetItsValue() throws SystemErrorException {
        TextStringDataValue t_value = new TextStringDataValue(db, tfa.getID(),
                                                              "bravo");

        t_value.setItsValue("echo");
        assertEquals(t_value.getItsValue(), "echo");
    }

    /**
     * Test of toString method, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testToString() throws SystemErrorException {
        TextStringDataValue t_value0 = new TextStringDataValue(db, tfa.getID(),
                                                               "bravo");

        TextStringDataValue t_value1 = new TextStringDataValue(db, tfa.getID(),
                                                               "nero");

        assertEquals(t_value0.toString(), "bravo");
        assertEquals(t_value1.toString(), "nero");
    }

    /**
     * Test of toDBString method, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    /*
    @Test
    public void testToDBString() throws SystemErrorException {
        String testDBString0 = "(TextStringDataValue (id 0) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue bravo) " +
                                    "(subRange false))";

        String testDBString1 = "(TextStringDataValue (id 0) " +
                                    "(itsFargID 8) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 0) " +
                                    "(itsValue nero) " +
                                    "(subRange false))";

        TextStringDataValue t_value0 = new TextStringDataValue(db, tfa.getID(),
                                                               "bravo");

        TextStringDataValue t_value1 = new TextStringDataValue(db, tfa.getID(),
                                                               "nero");

        assertEquals(t_value0.toDBString(), testDBString0);
        assertEquals(t_value1.toDBString(), testDBString1);
    }
     */

    /**
     * Test of Construct method, of class TextStringDataValue.
     *
     * @throws org.openshapa.db.SystemErrorException on failure.
     */
    @Test
    public void testConstruct() throws SystemErrorException {
        TextStringDataValue test = TextStringDataValue.Construct(db, "alpha");
        assertEquals(test.getItsValue(), "alpha");
    }

    @Test
    public void testHashCode() throws SystemErrorException {
        TextStringDataValue value0 =
                            new TextStringDataValue(db, tfa.getID(), "bravo");
        TextStringDataValue value1 =
                            new TextStringDataValue(db, tfa.getID(), "bravo");
        TextStringDataValue value2 =
                          new TextStringDataValue(db, tfa.getID(), "charlie");

        super.testHashCode(value0, value1, value2);
    }

    @Test
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        TextStringDataValue value0 =
                            new TextStringDataValue(db, tfa.getID(), "bravo");
        TextStringDataValue value1 =
                            new TextStringDataValue(db, tfa.getID(), "bravo");
        TextStringDataValue value2 =
                            new TextStringDataValue(db, tfa.getID(), "bravo");
        TextStringDataValue value3 =
                          new TextStringDataValue(db, tfa.getID(), "charlie");

        super.testEquals(value0, value1, value2, value3);
    }

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
            "Testing 1 argument constructor for class TextStringDataValue     ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db_l = null;
        TextStringDataValue tdv = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db_l = null;
        tdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db_l = new ODBCDatabase();
            tdv = new TextStringDataValue(db_l);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( db_l == null ) ||
             ( tdv == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db_l == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( tdv == null )
                {
                    outStream.print(
                            "new TextStringDataValue(db) returned null.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                        "new TextStringDataValue(db) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("new TextStringDataValue(db) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( failures == 0 )
        {
            failures += DataValueTest.Verify1ArgInitialization(db_l, tdv, outStream,
                                                           verbose);

            if ( tdv.ItsDefault != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(" tdv.ItsDefault != null.\n");
                }
            }

            if ( tdv.itsValue != tdv.ItsDefault )
            {
                failures++;

                if ( verbose )
                {
                    String s1;
                    String s2;

                    if ( tdv.itsValue == null )
                        s1 = new String("<null>");
                    else
                        s1 = tdv.itsValue;

                    if ( tdv.ItsDefault == null )
                        s2 = new String("<null>");
                    else
                        s2 = tdv.ItsDefault;

                    outStream.printf(
                            "tdv.itsValue = %s != tdv.ItsDefault = %s.\n",
                            s1, s2);
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            tdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tdv = new TextStringDataValue((Database)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TextStringDataValue(null) returned.\n");
                    }

                    if ( tdv != null )
                    {
                        outStream.print(
                                "new TextStringDataValue(null) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TextStringDataValue(null) failed to throw " +
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
    } /* TextStringDataValue::Test1ArgConstructor() */


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
            "Testing 2 argument constructor for class TextStringDataValue     ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db_l = null;
        MatrixVocabElement txt_mve_l = null;
        TextStringFormalArg tfa_l = null;
        TextStringDataValue tdv = null;

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
            db_l = new ODBCDatabase();

            txt_mve_l = new MatrixVocabElement(db_l, "txt_mve");
            txt_mve_l.setType(MatrixVocabElement.MatrixType.TEXT);
            tfa_l = new TextStringFormalArg(db_l);
            txt_mve_l.appendFormalArg(tfa_l);
            db_l.vl.addElement(txt_mve_l);

            tdv = new TextStringDataValue(db_l, tfa_l.getID());

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db_l == null ) ||
             ( txt_mve_l == null ) ||
             ( tfa_l == null ) ||
             ( tdv == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db_l == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( txt_mve_l == null )
                {
                    outStream.print("allocation of txt_mve failed.\n");
                }

                if ( tfa_l == null )
                {
                    outStream.print("allocation of tfa failed.");
                }

                if ( tdv == null )
                {
                    outStream.print("new TextStringDataValue(db, " +
                                    "tfa.getID()) returned null.\n");
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
            failures += DataValueTest.Verify2PlusArgInitialization(db_l,
                                                               tfa_l,
                                                               tdv,
                                                               outStream,
                                                               verbose,
                                                               "tdv");

            if ( tdv.ItsDefault != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tdv.ItsDefault != null.\n");
                }
            }

            if ( tdv.subRange != tfa_l.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tdv.subRange doesn't match tfa.getSubRange().\n");
                }
            }

            if ( tdv.itsValue != tdv.ItsDefault )
            {
                failures++;

                if ( verbose )
                {
                    String s1;
                    String s2;

                    if ( tdv.itsValue == null )
                        s1 = new String("<null>");
                    else
                        s1 = tdv.itsValue;

                    if ( tdv.ItsDefault == null )
                        s2 = new String("<null>");
                    else
                        s2 = tdv.ItsDefault;

                    outStream.printf(
                            "tdv.itsValue = %s != tdv.ItsDefault = %s.\n",
                            s1, s2);
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            tdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tdv = new TextStringDataValue((Database)null, tfa_l.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TextStringDataValue(null, " +
                                        "tfa.getID()) returned.\n");
                    }

                    if ( tdv != null )
                    {
                        outStream.print("new TextStringDataValue(null, " +
                                        "tfa.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TextStringDataValue(null, " +
                                "tfa.getID()) failed to throw a system " +
                                "error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid formal
         * argument id.
         */
        if ( failures == 0 )
        {
            tdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tdv = new TextStringDataValue(db_l, DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TextStringDataValue(db, " +
                                        "INVALID_ID) returned.\n");
                    }

                    if ( tdv != null )
                    {
                        outStream.print("new TextStringDataValue(db, " +
                                        "INVALID_ID) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new TextStringDataValue(db, INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an ID that does not
         * refer to a formal argument.
         */
        if ( failures == 0 )
        {
            tdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tdv = new TextStringDataValue(db_l, txt_mve_l.getID());
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TextStringDataValue(db, " +
                                        "txt_mve.getID()) returned.\n");
                    }

                    if ( tdv != null )
                    {
                        outStream.print("new TextStringDataValue(db, " +
                                "txt_mve.getID()) returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                            "new TextStringDataValue(db, txt_mve.getID()) " +
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
    } /* TextStringDataValue::Test2ArgConstructor() */


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
            "Testing 3 argument constructor for class TextStringDataValue     ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db_l = null;
        MatrixVocabElement txt_mve_l = null;
        TextStringFormalArg tfa_l = null;
        TextStringDataValue tdv = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db_l = null;
        tdv = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db_l = new ODBCDatabase();

            txt_mve_l = new MatrixVocabElement(db_l, "txt_mve");
            txt_mve_l.setType(MatrixVocabElement.MatrixType.TEXT);
            tfa_l = new TextStringFormalArg(db_l);
            txt_mve_l.appendFormalArg(tfa_l);
            db_l.vl.addElement(txt_mve_l);

            tdv = new TextStringDataValue(db_l, tfa_l.getID(), "echo");

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db_l == null ) ||
             ( txt_mve_l == null ) ||
             ( tfa_l == null ) ||
             ( tdv == null ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( db_l == null )
                {
                    outStream.print(
                            "new ODBCDatabase() returned null.\n");
                }

                if ( txt_mve_l == null )
                {
                    outStream.print("allocation of txt_mve failed.\n");
                }

                if ( tfa_l == null )
                {
                    outStream.print("allocation of tfa failed.");
                }

                if ( tdv == null )
                {
                    outStream.print(
                            "new TextStringDataValue(db, tfa.getID(), " +
                            "\"echo\") returned null.\n");
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
            failures += DataValueTest.Verify2PlusArgInitialization(db_l,
                                                               tfa_l,
                                                               tdv,
                                                               outStream,
                                                               verbose,
                                                               "tdv");

            if ( tdv.subRange != tfa_l.getSubRange() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "tdv.subRange doesn't match tfa.getSubRange().\n");
                }
            }

            if ( ( tdv.itsValue == null ) ||
                 ( tdv.itsValue.compareTo("echo") != 0 ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("tdv.itsValue != \"echo\".\n");
                }
            }
        }

        /* verify that the constructor fails when given an invalid db */
        if ( failures == 0 )
        {
            tdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tdv = new TextStringDataValue((Database)null, tfa_l.getID(),
                                                "alpha");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( tdv != null )
                    {
                        outStream.print("new TextStringDataValue(null, " +
                                "tfa.getID(), \"alpha\") returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new TextStringDataValue(null, " +
                                        "tfa.getID(), \"alpha\") returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new TextStringDataValue(null, tfa.getID(), " +
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
            tdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tdv = new TextStringDataValue(db_l, DBIndex.INVALID_ID, "alpha");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( tdv != null )
                    {
                        outStream.print("new TextStringDataValue(db, " +
                                "INVALID_ID, \"alpha\") returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("new TextStringDataValue(db, " +
                                        "INVALID_ID, \"alpha\") returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "new TextStringDataValue(db, INVALID_ID, " +
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
            tdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tdv = new TextStringDataValue(db_l, txt_mve_l.getID(), "alpha");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TextStringDataValue(db, " +
                                "txt_mve.getID(), \"alpha\") returned.\n");
                    }

                    if ( tdv != null )
                    {
                        outStream.print("new TextStringDataValue(db, " +
                            "txt_mve.getID(), \"alpha\") returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TextStringDataValue(db, " +
                                "txt_mve.getID(), \"alpha\") failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
        }

        /* verify that the constructor fails when given an invalid quote string.
         */
        if ( failures == 0 )
        {
            tdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tdv = new TextStringDataValue(db_l, tfa_l.getID(),
                                                "invalid \b text string");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("new TextStringDataValue(db, " +
                                "tfa.getID(), \"invalid \\b text string\") " +
                                "returned.\n");
                    }

                    if ( tdv != null )
                    {
                        outStream.print("new TextStringDataValue(db, " +
                            "txt_mve.getID(), \"invalid \\b text string\") " +
                            "returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TextStringDataValue(db, " +
                            "txt_mve.getID(), \"invalid \\b text string\") " +
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
    } /* TextStringDataValue::Test3ArgConstructor() */


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
            txt_mve0.setType(MatrixVocabElement.MatrixType.TEXT);
            tfa0 = new TextStringFormalArg(db);
            txt_mve0.appendFormalArg(tfa0);
            db.vl.addElement(txt_mve0);

            tdv0 = new TextStringDataValue(db, tfa0.getID(), "bravo");

            txt_mve1 = new MatrixVocabElement(db, "txt_mve1");
            txt_mve1.setType(MatrixVocabElement.MatrixType.TEXT);
            tfa1 = new TextStringFormalArg(db);
            txt_mve1.appendFormalArg(tfa1);
            db.vl.addElement(txt_mve1);

            tdv1 = new TextStringDataValue(db, tfa1.getID(), "delta");

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
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
            failures += DataValueTest.TestAccessors(db, tfa0, txt_mve1, tfa1,
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

            failures += DataValueTest.TestAccessors(db, tfa1, txt_mve0, tfa0,
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

        assertTrue(pass);
    } /* TextStringDataValue::TestAccessors() */


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
            "Testing copy constructor for class TextStringDataValue           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        MatrixVocabElement txt_mve = null;
        TextStringFormalArg tfa = null;
        TextStringDataValue tdv = null;
        TextStringDataValue tdv0 = null;
        TextStringDataValue tdv1 = null;
        TextStringDataValue tdv2 = null;
        TextStringDataValue tdv0_copy = null;
        TextStringDataValue tdv1_copy = null;
        TextStringDataValue tdv2_copy = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        db = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        /* setup the base entries for the copy test */
        try
        {
            db = new ODBCDatabase();

            tdv0 = new TextStringDataValue(db);

            txt_mve = new MatrixVocabElement(db, "txt_mve");
            txt_mve.setType(MatrixVocabElement.MatrixType.TEXT);
            tfa = new TextStringFormalArg(db);
            txt_mve.appendFormalArg(tfa);
            db.vl.addElement(txt_mve);

            tdv1 = new TextStringDataValue(db, tfa.getID());
            tdv2 = new TextStringDataValue(db, tfa.getID(), "foxtrot");

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
        }

        if ( ( db == null ) ||
             ( txt_mve == null ) ||
             ( tfa == null ) ||
             ( tdv0 == null ) ||
             ( tdv1 == null ) ||
             ( tdv2 == null ) ||
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

                if ( txt_mve == null )
                {
                    outStream.print("allocation of txt_mve failed.\n");
                }

                if ( tfa == null )
                {
                    outStream.print("allocation of tfa failed.");
                }

                if ( tdv0 == null )
                {
                    outStream.print(
                            "new TextStringDataValue(db) returned null.\n");
                }

                if ( tdv1 == null )
                {
                    outStream.print("new TextStringDataValue(db, " +
                            "tfa.getID()) returned null.\n");
                }

                if ( tdv2 == null )
                {
                    outStream.print(
                            "new TextStringDataValue(db, tfa.getID(), " +
                             "\"foxtrot\") returned null.\n");
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
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            /* setup the base entries for the copy test */
            try
            {
                tdv0_copy = new TextStringDataValue(tdv0);
                tdv1_copy = new TextStringDataValue(tdv1);
                tdv2_copy = new TextStringDataValue(tdv2);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tdv0_copy == null ) ||
                 ( tdv1_copy == null ) ||
                 ( tdv2_copy == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( tdv0_copy == null )
                    {
                        outStream.print(
                            "new TextStringDataValue(tdv0) returned null.\n");
                    }

                    if ( tdv1_copy == null )
                    {
                        outStream.print(
                            "new TextStringDataValue(tdv1) returned null.\n");
                    }

                    if ( tdv2_copy == null )
                    {
                        outStream.print(
                            "new TextStringDataValue(tdv2) returned null.\n");
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
            failures += DataValueTest.VerifyDVCopy(tdv0, tdv0_copy, outStream,
                                               verbose, "tdv0", "tdv0_copy");

            failures += DataValueTest.VerifyDVCopy(tdv1, tdv1_copy, outStream,
                                               verbose, "tdv1", "tdv1_copy");

            failures += DataValueTest.VerifyDVCopy(tdv2, tdv2_copy, outStream,
                                               verbose, "tdv2", "tdv2_copy");
        }


        /* verify that the constructor fails when given an invalid dv */
        if ( failures == 0 )
        {
            tdv = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                tdv = new TextStringDataValue((TextStringDataValue)null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( tdv != null ) ||
                 ( completed ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "new TextStringDataValue(null) completed.\n");
                    }

                    if ( tdv != null )
                    {
                        outStream.print("new TextStringDataValue(null) " +
                                "returned non-null.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new TextStringDataValue(null) " +
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
    } /* TextStringDataValue::TestCopyConstructor() */


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
        String testString0 = "bravo";
        String testDBString0 = "(TextStringDataValue (id 100) " +
                                    "(itsFargID 2) " +
                                    "(itsFargType TEXT) " +
                                    "(itsCellID 500) " +
                                    "(itsValue bravo) " +
                                    "(subRange false))";
        String testString1 = "nero";
        String testDBString1 = "(TextStringDataValue (id 101) " +
                                    "(itsFargID 8) " +
                                    "(itsFargType TEXT) " +
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
        tdv0 = null;
        tdv1 = null;
        completed = false;
        threwSystemErrorException = false;
        systemErrorExceptionString = null;

        try
        {
            db = new ODBCDatabase();

            txt_mve0 = new MatrixVocabElement(db, "txt_mve0");
            txt_mve0.setType(MatrixVocabElement.MatrixType.TEXT);
            tfa0 = new TextStringFormalArg(db);
            txt_mve0.appendFormalArg(tfa0);
            db.vl.addElement(txt_mve0);

            tdv0 = new TextStringDataValue(db, tfa0.getID(), "bravo");
            tdv0.setID(100);      // invalid value for print test
            tdv0.itsCellID = 500; // invalid value for print test

            txt_mve1 = new MatrixVocabElement(db, "txt_mve1");
            txt_mve1.setType(MatrixVocabElement.MatrixType.TEXT);
            tfa1 = new TextStringFormalArg(db);
            txt_mve1.appendFormalArg(tfa1);
            db.vl.addElement(txt_mve1);

            tdv1 = new TextStringDataValue(db, tfa1.getID(), "nero");
            tdv1.setID(101);      // invalid value for print test
            tdv1.itsCellID = 501; // invalid value for print test

            completed = true;
        } catch (SystemErrorException e) {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        } catch (LogicErrorException le) {
            threwSystemErrorException = true;
            systemErrorExceptionString = le.toString();
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
                    outStream.print("new TextStringDataValue(db, tfa.getID(), " +
                                    "\"bravo\") returned null.\n");
                }

                if ( txt_mve1 == null )
                {
                    outStream.print("allocation of txt_mve failed.\n");
                }

                if ( tfa1 == null )
                {
                    outStream.print("allocation of tfa1 failed.\n");
                }

                if ( tdv1 == null )
                {
                    outStream.print("new TextStringDataValue(db, tfa1.getID(), " +
                                    "\"nero\") returned null.\n");
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
            if ( tdv0.toString().compareTo(testString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected tdv0.toString(): \"%s\".\n",
                                     tdv0.toString());
                }
            }

            if ( tdv0.toDBString().compareTo(testDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected tdv0.toDBString(): \"%s\".\n",
                                     tdv0.toDBString());
                }
            }

            if ( tdv1.toString().compareTo(testString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected tdv1.toString(): \"%s\".\n",
                                     tdv1.toString());
                }
            }

            if ( tdv1.toDBString().compareTo(testDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected tdv1.toDBString(): \"%s\".\n",
                                     tdv1.toDBString());
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
    } /* TextStringDataValue::TestToStringMethods() */


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

}