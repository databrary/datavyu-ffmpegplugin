/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author swhitcher
 */
public abstract class DatabaseTest {

    public abstract Database getInstance();

    public DatabaseTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws SystemErrorException {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getType method, of class Database.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        Database instance = getInstance();
        String expResult = "";
        String result = instance.getType();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        // SJW - hasn't failed yet because overridden by subclasses I guess
        fail("The test case is a prototype.");
    }

    /**
     * testIsValidFargName
     *
     * Run a variety of valid and invalid strings past IsValidFArgName, and
     * see it it gets the right answer.
     *
     */

    @Test
    public void TestIsValidFargName() throws SystemErrorException {

        assertFalse(Database.IsValidFargName("<>"));
        assertFalse(Database.IsValidFargName("<"));

        assertTrue(Database.IsValidFargName("<a>"));

        assertFalse(Database.IsValidFargName("<a(b>"));
        assertFalse(Database.IsValidFargName("<)a>"));
        assertFalse(Database.IsValidFargName("<a<b>"));
        assertFalse(Database.IsValidFargName("<>>"));
        assertFalse(Database.IsValidFargName("<a,b>"));
        assertFalse(Database.IsValidFargName("<\"a>"));

        assertTrue(Database.IsValidFargName("<!#$%&'*+-./>"));
        assertTrue(Database.IsValidFargName("<0123456789\072;=?>"));
        assertTrue(Database.IsValidFargName("<@ABCDEFGHIJKLMNO>"));
        assertTrue(Database.IsValidFargName("<PQRSTUVWXYZ[\\]^_>"));
        assertTrue(Database.IsValidFargName("<`abcdefghijklmno>"));
        assertTrue(Database.IsValidFargName("<pqrstuvwxyz{\174}~>"));
    }

    @Test (expected = SystemErrorException.class)
    public void TestIsValidFargNameNull() throws SystemErrorException {

        assertFalse(Database.IsValidFargName(null));
    }

     /**
     * TestIsValidFloat
     *
     * Run a variety of valid and invalid objects past IsValidFloat, and
     * see if it gets the right answer.
     *
     */

    @Test
    public void TestIsValidFloat() throws SystemErrorException {

        assertFalse(Database.IsValidFloat(new String("a string")));
        assertFalse(Database.IsValidFloat(new Float(0.0)));

        assertTrue(Database.IsValidFloat(new Double(0.0)));

        assertFalse(Database.IsValidFloat(new Integer(0)));
        assertFalse(Database.IsValidFloat(new Long(0)));
        assertFalse(Database.IsValidFloat(new Boolean(false)));
        assertFalse(Database.IsValidFloat(new Character('c')));
        assertFalse(Database.IsValidFloat(new Byte((byte)'b')));
        assertFalse(Database.IsValidFloat(new Short((short)0)));
        assertFalse(Database.IsValidFloat(new Double[] {0.0, 1.0}));
    }

    @Test (expected = SystemErrorException.class)
    public void TestIsValidFloatNull() throws SystemErrorException {

        assertFalse(Database.IsValidFloat(null));
    }


     /**
     * TestIsValidInt
     *
     * Run a variety of valid and invalid objects past IsValidInt, and
     * see if it gets the right answer.
     *
     *                                          JRM -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    @Test
    public void TestIsValidInt() throws SystemErrorException {

        assertFalse(Database.IsValidInt(new String("a string")));
        assertFalse(Database.IsValidInt(new Float(0.0)));
        assertFalse(Database.IsValidInt(new Double(0.0)));
        assertFalse(Database.IsValidInt(new Integer(0)));

        assertTrue(Database.IsValidInt(new Long(0)));

        assertFalse(Database.IsValidInt(new Boolean(false)));
        assertFalse(Database.IsValidInt(new Character('c')));
        assertFalse(Database.IsValidInt(new Byte((byte)'b')));
        assertFalse(Database.IsValidInt(new Short((short)0)));
        assertFalse(Database.IsValidInt(new Double[] {0.0, 1.0}));
    }

    @Test (expected = SystemErrorException.class)
    public void TestIsValidIntNull() throws SystemErrorException {

        assertFalse(Database.IsValidInt(null));
    }


    /**
     * testIsValidNominal
     *
     * Run a variety of objects and valid and invalid strings past
     * IsValidNominal, and see it it gets the right answer.
     *
     */

    @Test
    public void TestIsValidNominal() throws SystemErrorException {

        assertTrue(Database.IsValidNominal("A Valid Nominal"));

        assertFalse(Database.IsValidNominal(new Float(0.0)));
        assertFalse(Database.IsValidNominal(new Double(0.0)));
        assertFalse(Database.IsValidNominal(new Integer(0)));
        assertFalse(Database.IsValidNominal(new Long(0)));
        assertFalse(Database.IsValidNominal(new Boolean(false)));
        assertFalse(Database.IsValidNominal(new Character('c')));
        assertFalse(Database.IsValidNominal(new Byte((byte)'b')));
        assertFalse(Database.IsValidNominal(new Short((short)0)));
        assertFalse(Database.IsValidNominal(new Double[] {0.0, 1.0}));
        assertFalse(Database.IsValidNominal("("));
        assertFalse(Database.IsValidNominal(")"));
        assertFalse(Database.IsValidNominal("<"));
        assertFalse(Database.IsValidNominal(">"));
        assertFalse(Database.IsValidNominal(","));
        assertFalse(Database.IsValidNominal(" leading white space"));
        assertFalse(Database.IsValidNominal("trailing while space "));

        assertTrue(Database.IsValidNominal("!#$%&'*+-./"));
        assertTrue(Database.IsValidNominal("0123456789\072;=?"));
        assertTrue(Database.IsValidNominal("@ABCDEFGHIJKLMNO"));
        assertTrue(Database.IsValidNominal("PQRSTUVWXYZ[\\]^_"));
        assertTrue(Database.IsValidNominal("`abcdefghijklmno"));
        assertTrue(Database.IsValidNominal("pqrstuvwxyz{\174}~"));

        assertFalse(Database.IsValidNominal("horizontal\ttab"));
        assertFalse(Database.IsValidNominal("embedded\bback space"));
        assertFalse(Database.IsValidNominal("embedded\nnew line"));
        assertFalse(Database.IsValidNominal("embedded\fform feed"));
        assertFalse(Database.IsValidNominal("embedded\rcarriage return"));

        assertTrue(Database.IsValidNominal("a"));
    }

    @Test (expected = SystemErrorException.class)
    public void TestIsValidNominalNull() throws SystemErrorException {

        assertFalse(Database.IsValidNominal(null));
    }


    /**
     * testIsValidPredName
     *
     * Run a variety of objects and valid and invalid strings past
     * IsValidPredName, and see it it gets the right answer.
     *
     */

    @Test
    public void TestIsValidPredName() throws SystemErrorException {

        assertTrue(Database.IsValidPredName("A_Valid_Predicate_Name"));

        assertFalse(Database.IsValidPredName("("));
        assertFalse(Database.IsValidPredName(")"));
        assertFalse(Database.IsValidPredName("<"));
        assertFalse(Database.IsValidPredName(">"));
        assertFalse(Database.IsValidPredName(","));
        assertFalse(Database.IsValidPredName(" leading white space"));
        assertFalse(Database.IsValidPredName("trailing while space "));

        assertTrue(Database.IsValidPredName("!#$%&'*+-./"));
        assertTrue(Database.IsValidPredName("0123456789\072;=?"));
        assertTrue(Database.IsValidPredName("@ABCDEFGHIJKLMNO"));
        assertTrue(Database.IsValidPredName("PQRSTUVWXYZ[\\]^_"));
        assertTrue(Database.IsValidPredName("`abcdefghijklmno"));
        assertTrue(Database.IsValidPredName("pqrstuvwxyz{\174}~"));
        assertTrue(Database.IsValidPredName("a"));

        assertFalse(Database.IsValidPredName("embedded space"));
        assertFalse(Database.IsValidPredName("horizontal\ttab"));
        assertFalse(Database.IsValidPredName("embedded\bback_space"));
        assertFalse(Database.IsValidPredName("embedded\nnew_line"));
        assertFalse(Database.IsValidPredName("embedded\fform_feed"));
        assertFalse(Database.IsValidPredName("embedded\rcarriage_return"));
    }

    @Test (expected = SystemErrorException.class)
    public void TestIsValidPredNameNull() throws SystemErrorException {

        assertFalse(Database.IsValidPredName(null));
    }


    /**
     * testIsValidSVarName
     *
     * Run a variety of objects and valid and invalid strings past
     * IsValidSVarName, and see it it gets the right answer.
     *
     */

    @Test
    public void TestIsValidSVarName() throws SystemErrorException {

        assertTrue(Database.IsValidSVarName("A Valid S-Var Name"));

        assertFalse(Database.IsValidSVarName("("));
        assertFalse(Database.IsValidSVarName(")"));
        assertFalse(Database.IsValidSVarName("<"));
        assertFalse(Database.IsValidSVarName(">"));
        assertFalse(Database.IsValidSVarName(","));
        assertFalse(Database.IsValidSVarName(" leading white space"));
        assertFalse(Database.IsValidSVarName("trailing while space "));

        assertTrue(Database.IsValidSVarName("!#$%&'*+-./"));
        assertTrue(Database.IsValidSVarName("0123456789\072;=?"));
        assertTrue(Database.IsValidSVarName("@ABCDEFGHIJKLMNO"));
        assertTrue(Database.IsValidSVarName("PQRSTUVWXYZ[\\]^_"));
        assertTrue(Database.IsValidSVarName("`abcdefghijklmno"));
        assertTrue(Database.IsValidSVarName("pqrstuvwxyz{\174}~"));
        assertTrue(Database.IsValidSVarName("a"));
        assertTrue(Database.IsValidSVarName("embedded space"));

        assertFalse(Database.IsValidSVarName("horizontal\ttab"));
        assertFalse(Database.IsValidSVarName("embedded\bback_space"));
        assertFalse(Database.IsValidSVarName("embedded\nnew_line"));
        assertFalse(Database.IsValidSVarName("embedded\fform_feed"));
        assertFalse(Database.IsValidSVarName("embedded\rcarriage_return"));
    }


    @Test (expected = SystemErrorException.class)
    public void TestIsValidSVarNameNull() throws SystemErrorException {

        assertFalse(Database.IsValidSVarName(null));
    }


    /**
     * testIsValidTextString
     *
     * Run a variety of objects and valid and invalid strings past
     * IsValidTextString(), and see it it gets the right answer.
     *
     */

    @Test
    public void TestIsValidTextString() throws SystemErrorException {

        assertTrue(Database.IsValidTextString("A Valid Text String"));

        assertFalse(Database.IsValidTextString(new Float(0.0)));
        assertFalse(Database.IsValidTextString(new Double(0.0)));
        assertFalse(Database.IsValidTextString(new Integer(0)));
        assertFalse(Database.IsValidTextString(new Long(0)));
        assertFalse(Database.IsValidTextString(new Boolean(false)));
        assertFalse(Database.IsValidTextString(new Character('c')));
        assertFalse(Database.IsValidTextString(new Byte((byte)'b')));
        assertFalse(Database.IsValidTextString(new Short((short)0)));
        assertFalse(Database.IsValidTextString(new Double[] {0.0, 1.0}));
        assertFalse(Database.IsValidTextString("an invalid text \b string"));

        assertTrue(Database.IsValidTextString(""));
        assertTrue(Database.IsValidTextString("/0/1/2/3/4/5/6/7/11/12/13"));
        assertTrue(Database.IsValidTextString("/14/15/16/17/20/21/22/23"));
        assertTrue(Database.IsValidTextString("/24/25/26/27/30/31/32/33"));
        assertTrue(Database.IsValidTextString("/34/35/36/37 "));
        assertTrue(Database.IsValidTextString("!\"#$%&\'()*+,-./"));
        assertTrue(Database.IsValidTextString("0123456789\072;<=>?"));
        assertTrue(Database.IsValidTextString("@ABCDEFGHIJKLMNO"));
        assertTrue(Database.IsValidTextString("PQRSTUVWXYZ[\\]^_"));
        assertTrue(Database.IsValidTextString("`abcdefghijklmno"));
        assertTrue(Database.IsValidTextString("pqrstuvwxyz{\174}~\177"));

        assertFalse(Database.IsValidTextString("\200"));
    }


    @Test (expected = SystemErrorException.class)
    public void TestIsValidTextStringNull() throws SystemErrorException {

        assertFalse(Database.IsValidTextString(null));
    }


    /**
     * testIsValidTimeStamp
     *
     * Run a variety of objects and valid and invalid strings past
     * IsValidTextString(), and see it it gets the right answer.
     *
     */

    @Test
    public void TestIsValidTimeStamp() throws SystemErrorException {

        /* the tests with a TimeStamp object are a bit slim, but the
         * TimeStamp class is supposed to prevent creation of an invalid
         * time stamp.
         */
        assertFalse(Database.IsValidTimeStamp(new String("a string")));
        assertFalse(Database.IsValidTimeStamp(new Float(0.0)));
        assertFalse(Database.IsValidTimeStamp(new Double(0.0)));
        assertFalse(Database.IsValidTimeStamp(new Integer(0)));
        assertFalse(Database.IsValidTimeStamp(new Long(0)));
        assertFalse(Database.IsValidTimeStamp(new Boolean(false)));
        assertFalse(Database.IsValidTimeStamp(new Character('c')));
        assertFalse(Database.IsValidTimeStamp(new Byte((byte)'b')));
        assertFalse(Database.IsValidTimeStamp(new Short((short)0)));
        assertFalse(Database.IsValidTimeStamp(new Double[] {0.0, 1.0}));

        assertTrue(Database.IsValidTimeStamp(new TimeStamp(60)));
        assertTrue(Database.IsValidTimeStamp(new TimeStamp(60,120)));
    }

    @Test (expected = SystemErrorException.class)
    public void TestIsValidTimeStampNull() throws SystemErrorException {

        assertFalse(Database.IsValidTimeStamp(null));
    }


     /**
     * testIsValidQuoteString
     *
     * Run a variety of objects and valid and invalid strings past
     * IsValidQuoteString, and see it it gets the right answer.
     *
     */

    @Test
    public void TestIsValidQuoteString() throws SystemErrorException {

        assertTrue(Database.IsValidQuoteString("A Valid Quote String"));

        assertFalse(Database.IsValidQuoteString(new Float(0.0)));
        assertFalse(Database.IsValidQuoteString(new Double(0.0)));
        assertFalse(Database.IsValidQuoteString(new Integer(0)));
        assertFalse(Database.IsValidQuoteString(new Long(0)));
        assertFalse(Database.IsValidQuoteString(new Boolean(false)));
        assertFalse(Database.IsValidQuoteString(new Character('c')));
        assertFalse(Database.IsValidQuoteString(new Byte((byte)'b')));
        assertFalse(Database.IsValidQuoteString(new Short((short)0)));
        assertFalse(Database.IsValidQuoteString(new Double[] {0.0, 1.0}));

        assertTrue(Database.IsValidQuoteString("("));
        assertTrue(Database.IsValidQuoteString(")"));
        assertTrue(Database.IsValidQuoteString("<"));
        assertTrue(Database.IsValidQuoteString(">"));
        assertTrue(Database.IsValidQuoteString(","));
        assertTrue(Database.IsValidQuoteString(" leading white space"));
        assertTrue(Database.IsValidQuoteString("trailing while space "));
        assertTrue(Database.IsValidQuoteString("!#$%&\'()*+,-./"));
        assertTrue(Database.IsValidQuoteString("0123456789\072;<=>?"));
        assertTrue(Database.IsValidQuoteString("@ABCDEFGHIJKLMNO"));
        assertTrue(Database.IsValidQuoteString("PQRSTUVWXYZ[\\]^_"));
        assertTrue(Database.IsValidQuoteString("`abcdefghijklmno"));
        assertTrue(Database.IsValidQuoteString("pqrstuvwxyz{\174}~"));

        assertFalse(Database.IsValidQuoteString("\177"));
        assertFalse(Database.IsValidQuoteString("horizontal\ttab"));
        assertFalse(Database.IsValidQuoteString("embedded\bback space"));
        assertFalse(Database.IsValidQuoteString("embedded\nnew line"));
        assertFalse(Database.IsValidQuoteString("embedded\fform feed"));
        assertFalse(Database.IsValidQuoteString("embedded\rcarriage return"));

        assertTrue(Database.IsValidQuoteString("a"));
    }


    @Test (expected = SystemErrorException.class)
    public void TestIsValidQuoteStringNull() throws SystemErrorException {

        assertFalse(Database.IsValidQuoteString(null));
    }




}