/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openshapa.db;

import java.io.PrintStream;
import java.util.Vector;
import junitx.util.PrivateAccessor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
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
     *                                           -- 3/03/07
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

    /** Utility PrivateAccess method for tests below. */
    private static long addMatrixVEPrivate(Database db, MatrixVocabElement mve)
                        throws SystemErrorException {
        long mveid = DBIndex.INVALID_ID;

        try {
            Long mve_id = (Long) PrivateAccessor.invoke(db, "addMatrixVE",
                    new Class[]{MatrixVocabElement.class},
                    new Object[]{mve});
            mveid = mve_id;
        } catch (SystemErrorException e) {
            throw(e);
        } catch (Throwable th) {
            fail("Problem in PrivateAccessor utility addMatrixVEPrivate.");
        }
        return mveid;
    }

    /** Utility PrivateAccess method for tests below. */
    private static void removeMatrixVEPrivate(Database db, long mveid)
                        throws SystemErrorException {
        try {
            Long mve_id = (Long) PrivateAccessor.invoke(db, "removeMatrixVE",
                    new Class[]{long.class},
                    new Object[]{mveid});
        } catch (SystemErrorException e) {
            throw(e);
        } catch (Throwable th) {
            fail("Problem in PrivateAccessor utility removeMatrixVEPrivate.");
        }
    }

    /*************************************************************************/
    /******************* Demo Database populate Code: ************************/
    /*************************************************************************/

    /**
     * Populates db with demo data items.
     * Doesn't gracefully recover if any errors encountered. Data input
     * just stops.
     *
     * TODO - null values in Predicates and Matrices should display args.
     * TODO - Partition demo better to show making columns then finding and
     * adding data to those columns.
     * TODO - add try catches around the relevant points to recover gracefully
     * in various situations. e.g. Column already exists with that name.
     *
     * @throws SystemErrorException if trouble.
     */
    @Test
    public void populateDemoData() throws SystemErrorException {

        Database db = new MacshapaDatabase();
        DataColumn dc;
        long colid;

        // Float column
        dc = new DataColumn(db, "float",
                MatrixVocabElement.MatrixType.FLOAT);
        colid = db.addColumn(dc);

        FloatDataValue fdv = new FloatDataValue(db);
        for (long onset = 0; onset < 20000; onset += 2000 ) {
            fdv.setItsValue(1.2345 * onset);
            AddDataValue(db, colid, onset, onset + 1000, fdv);
        }

        // Int column
        dc = new DataColumn(db, "int",
                MatrixVocabElement.MatrixType.INTEGER);
        colid = db.addColumn(dc);

        IntDataValue idv = new IntDataValue(db);
        for (long onset = 0; onset < 20000; onset += 2000 ) {
            AddDataValue(db, colid, onset, onset + 1000, idv);
            idv.setItsValue(onset * 2);
        }

        // Text column
        dc = new DataColumn(db, "text",
                MatrixVocabElement.MatrixType.TEXT);
        colid = db.addColumn(dc);

        TextStringDataValue tdv = new TextStringDataValue(db);
        for (long onset = 0; onset < 20000; onset += 2000 ) {
            AddDataValue(db, colid, onset, onset + 1000, tdv);
            tdv.setItsValue("Testing string -- " + onset);
        }

        // Nominal column
        dc = new DataColumn(db, "nominal",
                MatrixVocabElement.MatrixType.NOMINAL);
        colid = db.addColumn(dc);

        NominalDataValue ndv = new NominalDataValue(db);
        for (long onset = 0; onset < 20000; onset += 2000 ) {
            AddDataValue(db, colid, onset, onset + 1000, ndv);
            ndv.setItsValue("Nom -- " + onset);
        }

        // Predicate column
        dc = new DataColumn(db, "predicate",
                MatrixVocabElement.MatrixType.PREDICATE);
        colid = db.addColumn(dc);

        PredDataValue pdv = new PredDataValue(db);
        Predicate p = MakeDemoPredicate(db);

        for (long onset = 0; onset < 20000; onset += 2000 ) {
            AddDataValue(db, colid, onset, onset + 1000, pdv);
            pdv.setItsValue(p);
        }

        // Matrix column
        String name = "matrix";
        dc = new DataColumn(db, name,
                MatrixVocabElement.MatrixType.MATRIX);
        colid = db.addColumn(dc);

        Matrix m = MakeDemoMatrix(db, name);

        for (long onset = 0; onset < 20000; onset += 2000 ) {
            AddMatrix(db, colid, onset, onset + 1000, m);
        }
    }

    /**
     * Adds a datavalue to a database.
     *
     * Creates a one argument matrix containing the datavalue and adds it
     * to the column identified.
     * @param db Database to add datavalue to.
     * @param colid ID of the column to use.
     * @param onset Onset value.
     * @param offset Offset value.
     * @param dv Datavalue to add.
     * @throws SystemErrorException if add doesn't work.
     */
    private static void AddDataValue(Database db,
                        long colid, long onset, long offset, DataValue dv)
            throws SystemErrorException {

        DataColumn dc = db.getDataColumn(colid);
        MatrixVocabElement mve = db.getMatrixVE(dc.getItsMveID());
        // get the first (the only in a one arg matrix) formal arg
        FormalArgument fa = mve.getFormalArg(0);

        dv.setItsFargID(fa.getID());

        Vector<DataValue> vec = new Vector<DataValue>();
        vec.add(dv);

        Matrix mat = new Matrix(db, mve.getID(), vec);

        AddMatrix(db, colid, onset, offset, mat);
    }

    /**
     * Add a matrix item to a database.
     * @param db Database to add the matrix item to.
     * @param colid ID of the column to use.
     * @param onset Onset.
     * @param offset Offset.
     * @param mat Matrix item to add.
     * @throws SystemErrorException
     */
    private static void AddMatrix(Database db,
                        long colid, long onset, long offset, Matrix mat)
            throws SystemErrorException {

        DataCell cell = new DataCell(db, "Datacell", colid, mat.getMveID(),
                        new TimeStamp(1000, onset),
                        new TimeStamp(1000, offset),
                        mat);

        db.appendCell(cell);
    }

    private static Predicate MakeDemoPredicate(Database db)
    throws SystemErrorException {
        PredicateVocabElement pve0;
        FormalArgument farg;

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
        farg = new UnTypedFormalArg(db, "<untyped>");
        pve0.appendFormalArg(farg);
//            farg = new TimeStampFormalArg(db, "<timestamp>");
//            pve0.appendFormalArg(farg);

        long predID0 = db.addPredVE(pve0);

        // get a copy of the databases version of pve0 with ids assigned
        pve0 = db.getPredVE(predID0);

        Vector<DataValue> argList0 = new Vector<DataValue>();

        long fargID = pve0.getFormalArg(0).getID();
        DataValue arg = new FloatDataValue(db, fargID, 1.0);
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
        arg = new UndefinedDataValue(db, fargID,
                                     pve0.getFormalArg(5).getFargName());
        argList0.add(arg);
//            fargID = pve0.getFormalArg(6).getID();
//            arg = new TimeStampDataValue(db, fargID,
//                                         new TimeStamp(db.getTicks()));
//            argList0.add(arg);

        return new Predicate(db, predID0, argList0);
    }

    private static Matrix MakeDemoMatrix(Database db, String mvename)
                    throws SystemErrorException {
        MatrixVocabElement mve0;
        FormalArgument farg;

        mve0 = (MatrixVocabElement)(db.vl.getVocabElement(mvename));
        mve0 = new MatrixVocabElement(mve0);

        mve0.deleteFormalArg(0);

        farg = new FloatFormalArg(db, "<float>");
        mve0.appendFormalArg(farg);
        farg = new IntFormalArg(db, "<int>");
        mve0.appendFormalArg(farg);
        farg = new NominalFormalArg(db, "<nominal>");
        mve0.appendFormalArg(farg);
        farg = new QuoteStringFormalArg(db, "<qstring>");
        mve0.appendFormalArg(farg);
        farg = new UnTypedFormalArg(db, "<untyped>");
        mve0.appendFormalArg(farg);

        db.vl.replaceVocabElement(mve0);
        long matID0 = mve0.getID();

        // get a copy of the databases version of mve0 with ids assigned
        mve0 = db.getMatrixVE(matID0);

        Vector<DataValue> argList0 = new Vector<DataValue>();

        long fargID = mve0.getFormalArg(0).getID();
        DataValue arg = new FloatDataValue(db, fargID, 1.234);
        argList0.add(arg);
        fargID = mve0.getFormalArg(1).getID();
        arg = new IntDataValue(db, fargID, 2);
        argList0.add(arg);
        fargID = mve0.getFormalArg(2).getID();
        arg = new NominalDataValue(db, fargID, "a_nominal");
        argList0.add(arg);
        fargID = mve0.getFormalArg(3).getID();
        arg = new QuoteStringDataValue(db, fargID, "q-string");
        argList0.add(arg);
        fargID = mve0.getFormalArg(4).getID();
        arg = new UndefinedDataValue(db, fargID,
                                     mve0.getFormalArg(4).getFargName());
        argList0.add(arg);

        long mveid = mve0.getID();
        return new Matrix(db, mveid, argList0);
    }

    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /**
     * The following test methods should probably go somewhere else.  However
     * here will do for now.
     *
     * Since Database is an abstract class, the test code tests only the
     * class methods.
     *
     *                                           3/05/07
     */

    /**
     * TestClassDatabase()
     *
     * Main routine for all test code for the Database class proper.
     *
     *                                           3/03/07
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestClassDatabase()
    throws SystemErrorException {
        PrintStream outStream = System.out;
        boolean verbose = true;

        boolean pass = true;
        int failures = 0;

        outStream.print("Testing class Database:\n");

        if ( ! TestIsValidFargName(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestIsValidFloat(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestIsValidInt(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestIsValidNominal(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestIsValidPredName(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestIsValidSVarName(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestIsValidTextString(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestIsValidTimeStamp(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestIsValidQuoteString(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestAddMatrixVE(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestGetMatrixVE(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestGetMatrixVEs(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestMatrixNameInUse(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestMatrixVEExists(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestRemoveMatrixVE(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestReplaceMatrixVE(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestAddArgToPredVE(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestAddPredVE(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestGetPredVE(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestGetPredVEs(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestPredNameInUse(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestPredVEExists(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestRemovePredVE(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestReplacePredVE(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestGetVocabElement(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestVocabElementExists(outStream, verbose) )
        {
            failures++;
        }

//        if ( ! AdHocTest(outStream, verbose) )
//        {
//            failures++;
//        }

        if ( failures > 0 )
        {
            pass = false;
            outStream.printf("%d failures in tests for class Database.\n\n",
                              failures);
        }
        else
        {
            outStream.print("All tests passed for class Database.\n\n");
        }

        assertTrue(pass);

    } /* Database::TestClassDatabase() */


    /**
     * TestDatabase()
     *
     * Main routine for all OpenSHAPA database test code.
     *
     *                                           3/03/05
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestDatabase()
    throws SystemErrorException {
        PrintStream outStream = System.out;
        boolean verbose = true;

        boolean pass = true;
        int failures = 0;

        outStream.print("Testing OpenSHAPA database:\n\n");

        if ( ! TestInternalListeners(outStream, verbose) )
        {
            failures++;
        }

        if ( failures > 0 )
        {
            pass = false;
            outStream.printf(
                    "%d groups of tests failed for OpenSHAPA database.\n",
                    failures);
        }
        else
        {
            outStream.print("All tests passed for OpenSHAPA database.\n");
        }
        assertTrue(pass);

    } /* Database::TestDatabase() */


    /**
     * testIsValidFargName
     *
     * Run a variety of valid and invalid strings past IsValidFArgName, and
     * see it it gets the right answer.
     *
     *                                           -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidFargName(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing IsValidFargName()                                        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestStrings = 15;
        String[] testStrings = new String[]
        {
            /* test  0 -- should return false */ "<>",
            /* test  1 -- should return false */ "<",
            /* test  2 -- should return true  */ "<a>",
            /* test  3 -- should return false */ "<a(b>",
            /* test  4 -- should return false */ "<)a>",
            /* test  5 -- should return false */ "<a<b>",
            /* test  6 -- should return false */ "<>>",
            /* test  7 -- should return false */ "<a,b>",
            /* test  8 -- should return false */ "<\"a>",
            /* test  9 -- should return true  */ "<!#$%&'*+-./>",
            /* test 10 -- should return true  */ "<0123456789\072;=?>",
            /* test 11 -- should return true  */ "<@ABCDEFGHIJKLMNO>",
            /* test 12 -- should return true  */ "<PQRSTUVWXYZ[\\]^_>",
            /* test 13 -- should return true  */ "<`abcdefghijklmno>",
            /* test 14 -- should return true  */ "<pqrstuvwxyz{\174}~>"
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ false,
            /* test  1 should return */ false,
            /* test  2 should return */ true,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ true,
            /* test 10 should return */ true,
            /* test 11 should return */ true,
            /* test 12 should return */ true,
            /* test 13 should return */ true,
            /* test 14 should return */ true,
        };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestStrings )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidFargName(\"%s\") --> %b: ",
                        testNum, testStrings[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = Database.IsValidFargName(testStrings[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
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

        /* Now verify that we throw a system error exception when
         * IsValidFargName is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidFargName(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = Database.IsValidFargName(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;

        /* It seems that the compiler will not let me pass a non-string
         * to IsValidFargName(), so we will not bother to test that way
         * of generating a system error.
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

    } /* Database::TestIsValidFarg() */


    /**
     * TestIsValidFloat
     *
     * Run a variety of valid and invalid objects past IsValidFloat, and
     * see if it gets the right answer.
     *
     *                                           -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidFloat(java.io.PrintStream outStream,
                                           boolean verbose)
    {
        String testBanner =
            "Testing IsValidFloat()                                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 10;
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return false */ new String("a string"),
            /* test  1 -- should return false */ new Float(0.0),
            /* test  2 -- should return true  */ new Double(0.0),
            /* test  3 -- should return false */ new Integer(0),
            /* test  4 -- should return false */ new Long(0),
            /* test  5 -- should return false */ new Boolean(false),
            /* test  6 -- should return false */ new Character('c'),
            /* test  7 -- should return false */ new Byte((byte)'b'),
            /* test  8 -- should return false */ new Short((short)0),
            /* test  9 -- should return false */ new Double[] {0.0, 1.0},
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return false */ "new String(\"a string\")",
            /* test  1 -- should return false */ "new Float(0.0)",
            /* test  2 -- should return true  */ "new Double(0.0)",
            /* test  3 -- should return false */ "new Integer(0)",
            /* test  4 -- should return false */ "new Long(0)",
            /* test  5 -- should return false */ "new Boolean(false)",
            /* test  6 -- should return false */ "new Character('c')",
            /* test  7 -- should return false */ "new Byte((byte)'b')",
            /* test  8 -- should return false */ "new Short((short)0)",
            /* test  9 -- should return false */ "new Double[] {0.0, 1.0}",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ false,
            /* test  1 should return */ false,
            /* test  2 should return */ true,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
        };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestObjects )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidFloat(%s) --> %b: ",
                        testNum, testDesc[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = Database.IsValidFloat(testObjects[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
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

        /* Now verify that we throw a system error exception when
         * IsValidFloat is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidFloat(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = Database.IsValidFloat(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;

        /* It seems that the compiler will not let me pass a non-string
         * to IsValidFargName(), so we will not bother to test that way
         * of generating a system error.
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

    } /* Database::TestIsValidFloat() */


    /**
     * TestIsValidInt
     *
     * Run a variety of valid and invalid objects past IsValidInt, and
     * see if it gets the right answer.
     *
     *                                           -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */


    public static boolean TestIsValidInt(java.io.PrintStream outStream,
                                         boolean verbose)
    {
        String testBanner =
            "Testing IsValidInt()                                             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 10;
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return false */ new String("a string"),
            /* test  1 -- should return false */ new Float(0.0),
            /* test  2 -- should return true  */ new Double(0.0),
            /* test  3 -- should return false */ new Integer(0),
            /* test  4 -- should return false */ new Long(0),
            /* test  5 -- should return false */ new Boolean(false),
            /* test  6 -- should return false */ new Character('c'),
            /* test  7 -- should return false */ new Byte((byte)'b'),
            /* test  8 -- should return false */ new Short((short)0),
            /* test  9 -- should return false */ new Double[] {0.0, 1.0},
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return false */ "new String(\"a string\")",
            /* test  1 -- should return false */ "new Float(0.0)",
            /* test  2 -- should return false */ "new Double(0.0)",
            /* test  3 -- should return false */ "new Integer(0)",
            /* test  4 -- should return true  */ "new Long(0)",
            /* test  5 -- should return false */ "new Boolean(false)",
            /* test  6 -- should return false */ "new Character('c')",
            /* test  7 -- should return false */ "new Byte((byte)'b')",
            /* test  8 -- should return false */ "new Short((short)0)",
            /* test  9 -- should return false */ "new Double[] {0.0, 1.0}",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ false,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ true,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
        };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestObjects )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidInt(%s) --> %b: ",
                        testNum, testDesc[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = Database.IsValidInt(testObjects[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
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

        /* Now verify that we throw a system error exception when
         * IsValidFloat is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidInt(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = Database.IsValidInt(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;

        /* It seems that the compiler will not let me pass a non-string
         * to IsValidFargName(), so we will not bother to test that way
         * of generating a system error.
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

    } /* Database::TestIsValidInt() */


    /**
     * testIsValidNominal
     *
     * Run a variety of objects and valid and invalid strings past
     * IsValidNominal, and see it it gets the right answer.
     *
     *                                           -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidNominal(java.io.PrintStream outStream,
                                             boolean verbose)
    {
        String testBanner =
            "Testing IsValidNominal()                                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 29;
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return true  */ "A Valid Nominal",
            /* test  1 -- should return false */ new Float(0.0),
            /* test  2 -- should return false */ new Double(0.0),
            /* test  3 -- should return false */ new Integer(0),
            /* test  4 -- should return false */ new Long(0),
            /* test  5 -- should return false */ new Boolean(false),
            /* test  6 -- should return false */ new Character('c'),
            /* test  7 -- should return false */ new Byte((byte)'b'),
            /* test  8 -- should return false */ new Short((short)0),
            /* test  9 -- should return false */ new Double[] {0.0, 1.0},
            /* test 10 -- should return false */ "(",
            /* test 11 -- should return false */ ")",
            /* test 12 -- should return false */ "<",
            /* test 13 -- should return false */ ">",
            /* test 14 -- should return false */ ",",
            /* test 15 -- should return false */ " leading white space",
            /* test 16 -- should return false */ "trailing while space ",
            /* test 17 -- should return true  */ "!#$%&'*+-./",
            /* test 18 -- should return true  */ "0123456789\072;=?",
            /* test 19 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 20 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 21 -- should return true  */ "`abcdefghijklmno",
            /* test 22 -- should return true  */ "pqrstuvwxyz{\174}~",
            /* test 23 -- should return false */ "horizontal\ttab",
            /* test 24 -- should return false */ "embedded\bback space",
            /* test 25 -- should return false */ "embedded\nnew line",
            /* test 26 -- should return false */ "embedded\fform feed",
            /* test 27 -- should return false */ "embedded\rcarriage return",
            /* test 28 -- should return true  */ "a",
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return true  */ "A Valid Nominal",
            /* test  1 -- should return false */ "new Float(0.0)",
            /* test  2 -- should return false */ "new Double(0.0)",
            /* test  3 -- should return false */ "new Integer(0)",
            /* test  4 -- should return false */ "new Long(0)",
            /* test  5 -- should return false */ "new Boolean(false)",
            /* test  6 -- should return false */ "new Character('c')",
            /* test  7 -- should return false */ "new Byte((byte)'b')",
            /* test  8 -- should return false */ "new Short((short)0)",
            /* test  9 -- should return false */ "new Double[] {0.0, 1.0}",
            /* test 10 -- should return false */ "(",
            /* test 11 -- should return false */ ")",
            /* test 12 -- should return false */ "<",
            /* test 13 -- should return false */ ">",
            /* test 14 -- should return false */ ",",
            /* test 15 -- should return false */ " leading white space",
            /* test 16 -- should return false */ "trailing while space ",
            /* test 17 -- should return true  */ "!#$%&'*+-./",
            /* test 18 -- should return true  */ "0123456789\072;=?",
            /* test 19 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 20 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 21 -- should return true  */ "`abcdefghijklmno",
            /* test 22 -- should return true  */ "pqrstuvwxyz{\174}~",
            /* test 23 -- should return false */ "horizontal\ttab",
            /* test 24 -- should return false */ "embedded\bback space",
            /* test 25 -- should return false */ "embedded\nnew line",
            /* test 26 -- should return false */ "embedded\fform feed",
            /* test 27 -- should return false */ "embedded\rcarriage return",
            /* test 28 -- should return true  */ "a",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ true,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ false,
            /* test 11 should return */ false,
            /* test 12 should return */ false,
            /* test 13 should return */ false,
            /* test 14 should return */ false,
            /* test 15 should return */ false,
            /* test 16 should return */ false,
            /* test 17 should return */ true,
            /* test 18 should return */ true,
            /* test 19 should return */ true,
            /* test 20 should return */ true,
            /* test 21 should return */ true,
            /* test 22 should return */ true,
            /* test 23 should return */ false,
            /* test 24 should return */ false,
            /* test 25 should return */ false,
            /* test 26 should return */ false,
            /* test 27 should return */ false,
            /* test 28 should return */ true,
       };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestObjects )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidNominal(\"%s\") --> %b: ",
                        testNum, testDesc[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = Database.IsValidNominal(testObjects[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
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

        /* Now verify that we throw a system error exception when
         * IsValidNominal is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidNominal(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = Database.IsValidNominal(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;

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

    } /* Database::TestIsValidNominal() */


    /**
     * testIsValidPredName
     *
     * Run a variety of objects and valid and invalid strings past
     * IsValidPredName, and see it it gets the right answer.
     *
     *                                           -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidPredName(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing IsValidPredName()                                        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestStrings = 21;
        String[] testStrings = new String[]
        {
            /* test  0 -- should return true  */ "A_Valid_Predicate_Name",
            /* test  1 -- should return false */ "(",
            /* test  2 -- should return false */ ")",
            /* test  3 -- should return false */ "<",
            /* test  4 -- should return false */ ">",
            /* test  5 -- should return false */ ",",
            /* test  6 -- should return false */ " leading white space",
            /* test  7 -- should return false */ "trailing while space ",
            /* test  8 -- should return true  */ "!#$%&'*+-./",
            /* test  9 -- should return true  */ "0123456789\072;=?",
            /* test 10 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 11 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 12 -- should return true  */ "`abcdefghijklmno",
            /* test 13 -- should return true  */ "pqrstuvwxyz{\174}~",
            /* test 14 -- should return true  */ "a",
            /* test 15 -- should return false */ "embedded space",
            /* test 16 -- should return false */ "horizontal\ttab",
            /* test 17 -- should return false */ "embedded\bback_space",
            /* test 18 -- should return false */ "embedded\nnew_line",
            /* test 19 -- should return false */ "embedded\fform_feed",
            /* test 20 -- should return false */ "embedded\rcarriage_return",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ true,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ true,
            /* test  9 should return */ true,
            /* test 10 should return */ true,
            /* test 11 should return */ true,
            /* test 12 should return */ true,
            /* test 13 should return */ true,
            /* test 14 should return */ true,
            /* test 15 should return */ false,
            /* test 16 should return */ false,
            /* test 17 should return */ false,
            /* test 18 should return */ false,
            /* test 19 should return */ false,
            /* test 20 should return */ false,
        };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestStrings )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidPredName(\"%s\") --> %b: ",
                        testNum, testStrings[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = Database.IsValidPredName(testStrings[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
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

        /* Now verify that we throw a system error exception when
         * IsValidPredName is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidPredName(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = Database.IsValidPredName(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;

        /* It seems that the compiler will not let me pass a non-string
         * to IsValidPredName(), so we will not bother to test that way
         * of generating a system error.
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

    } /* Database::TestIsValidPredName() */


    /**
     * testIsValidSVarName
     *
     * Run a variety of objects and valid and invalid strings past
     * IsValidSVarName, and see it it gets the right answer.
     *
     *                                           -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidSVarName(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing IsValidSVarName()                                        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestStrings = 21;
        String[] testStrings = new String[]
        {
            /* test  0 -- should return true  */ "A Valid S-Var Name",
            /* test  1 -- should return false */ "(",
            /* test  2 -- should return false */ ")",
            /* test  3 -- should return false */ "<",
            /* test  4 -- should return false */ ">",
            /* test  5 -- should return false */ ",",
            /* test  6 -- should return false */ " leading white space",
            /* test  7 -- should return false */ "trailing while space ",
            /* test  8 -- should return true  */ "!#$%&'*+-./",
            /* test  9 -- should return true  */ "0123456789\072;=?",
            /* test 10 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 11 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 12 -- should return true  */ "`abcdefghijklmno",
            /* test 13 -- should return true  */ "pqrstuvwxyz{\174}~",
            /* test 14 -- should return true  */ "a",
            /* test 15 -- should return true  */ "embedded space",
            /* test 16 -- should return false */ "horizontal\ttab",
            /* test 17 -- should return false */ "embedded\bback_space",
            /* test 18 -- should return false */ "embedded\nnew_line",
            /* test 19 -- should return false */ "embedded\fform_feed",
            /* test 20 -- should return false */ "embedded\rcarriage_return",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ true,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ true,
            /* test  9 should return */ true,
            /* test 10 should return */ true,
            /* test 11 should return */ true,
            /* test 12 should return */ true,
            /* test 13 should return */ true,
            /* test 14 should return */ true,
            /* test 15 should return */ true,
            /* test 16 should return */ false,
            /* test 17 should return */ false,
            /* test 18 should return */ false,
            /* test 19 should return */ false,
            /* test 20 should return */ false,
        };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestStrings )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidSVarName(\"%s\") --> %b: ",
                        testNum, testStrings[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = Database.IsValidSVarName(testStrings[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
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

        /* Now verify that we throw a system error exception when
         * IsValidPredName is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidSVarName(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = Database.IsValidSVarName(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;

        /* It seems that the compiler will not let me pass a non-string
         * to IsValidSVarName(), so we will not bother to test that way
         * of generating a system error.
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

    } /* Database::TestIsValidSVarName() */


    /**
     * testIsValidTextString
     *
     * Run a variety of objects and valid and invalid strings past
     * IsValidTextString(), and see it it gets the right answer.
     *
     *                                           -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidTextString(java.io.PrintStream outStream,
                                                boolean verbose)
    {
        String testBanner =
            "Testing IsValidTextString()                                      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 23;
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return true  */ "A Valid Text String",
            /* test  1 -- should return false */ new Float(0.0),
            /* test  2 -- should return false */ new Double(0.0),
            /* test  3 -- should return false */ new Integer(0),
            /* test  4 -- should return false */ new Long(0),
            /* test  5 -- should return false */ new Boolean(false),
            /* test  6 -- should return false */ new Character('c'),
            /* test  7 -- should return false */ new Byte((byte)'b'),
            /* test  8 -- should return false */ new Short((short)0),
            /* test  9 -- should return false */ new Double[] {0.0, 1.0},
            /* test 10 -- should return false */ "an invalid text \b string",
            /* test 11 -- should return true  */ "",
            /* test 12 -- should return true  */ "/0/1/2/3/4/5/6/7/11/12/13",
            /* test 13 -- should return true  */ "/14/15/16/17/20/21/22/23",
            /* test 14 -- should return true  */ "/24/25/26/27/30/31/32/33",
            /* test 15 -- should return true  */ "/34/35/36/37 ",
            /* test 16 -- should return true  */ "!\"#$%&\'()*+,-./",
            /* test 17 -- should return true  */ "0123456789\072;<=>?",
            /* test 18 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 19 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 20 -- should return true  */ "`abcdefghijklmno",
            /* test 21 -- should return true  */ "pqrstuvwxyz{\174}~\177",
            /* test 22 -- should return false */ "\200",
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return true  */ "A Valid Text String",
            /* test  1 -- should return false */ "new Float(0.0)",
            /* test  2 -- should return false */ "new Double(0.0)",
            /* test  3 -- should return false */ "new Integer(0)",
            /* test  4 -- should return false */ "new Long(0)",
            /* test  5 -- should return false */ "new Boolean(false)",
            /* test  6 -- should return false */ "new Character('c')",
            /* test  7 -- should return false */ "new Byte((byte)'b')",
            /* test  8 -- should return false */ "new Short((short)0)",
            /* test  9 -- should return false */ "new Double[] {0.0, 1.0}",
            /* test 10 -- should return false */ "an invalid text \b string",
            /* test 11 -- should return true  */ "",
            /* test 12 -- should return true  */ "/0/1/2/3/4/5/6/7/11/12/13",
            /* test 13 -- should return true  */ "/14/15/16/17/20/21/22/23",
            /* test 14 -- should return true  */ "/24/25/26/27/30/31/32/33",
            /* test 15 -- should return true  */ "/34/35/36/37 ",
            /* test 16 -- should return true  */ "!\"#$%&\'()*+,-./",
            /* test 17 -- should return true  */ "0123456789\072;<=>?",
            /* test 18 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 19 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 20 -- should return true  */ "`abcdefghijklmno",
            /* test 21 -- should return true  */ "pqrstuvwxyz{\174}~\177",
            /* test 22 -- should return false */ "\200",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ true,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ false,
            /* test 11 should return */ true,
            /* test 12 should return */ true,
            /* test 13 should return */ true,
            /* test 14 should return */ true,
            /* test 15 should return */ true,
            /* test 16 should return */ true,
            /* test 17 should return */ true,
            /* test 18 should return */ true,
            /* test 19 should return */ true,
            /* test 20 should return */ true,
            /* test 21 should return */ true,
            /* test 22 should return */ false,
       };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestObjects )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidTextString(\"%s\") --> %b: ",
                        testNum, testDesc[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = Database.IsValidTextString(testObjects[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
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

        /* Now verify that we throw a system error exception when
         * IsValidTextString is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidTextString(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = Database.IsValidTextString(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;

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

    } /* Database::TestIsValidTextString() */


    /**
     * testIsValidTimeStamp
     *
     * Run a variety of objects and valid and invalid strings past
     * IsValidTextString(), and see it it gets the right answer.
     *
     *                                           -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidTimeStamp(java.io.PrintStream outStream,
                                               boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing IsValidTimeStamp()                                       ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 12;

        /* the tests with a TimeStamp object are a bit slim, but the
         * TimeStamp class is supposed to prevent creation of an invalid
         * time stamp.
         */
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return false */ new String("a string"),
            /* test  1 -- should return false */ new Float(0.0),
            /* test  2 -- should return false */ new Double(0.0),
            /* test  3 -- should return false */ new Integer(0),
            /* test  4 -- should return false */ new Long(0),
            /* test  5 -- should return false */ new Boolean(false),
            /* test  6 -- should return false */ new Character('c'),
            /* test  7 -- should return false */ new Byte((byte)'b'),
            /* test  8 -- should return false */ new Short((short)0),
            /* test  9 -- should return false */ new Double[] {0.0, 1.0},
            /* test 10 -- should return true  */ new TimeStamp(60),
            /* test 11 -- should return true  */ new TimeStamp(60,120),
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return false */ "new String(\"a string\")",
            /* test  1 -- should return false */ "new Float(0.0)",
            /* test  2 -- should return false */ "new Double(0.0)",
            /* test  3 -- should return false */ "new Integer(0)",
            /* test  4 -- should return false */ "new Long(0)",
            /* test  5 -- should return false */ "new Boolean(false)",
            /* test  6 -- should return false */ "new Character('c')",
            /* test  7 -- should return false */ "new Byte((byte)'b')",
            /* test  8 -- should return false */ "new Short((short)0)",
            /* test  9 -- should return false */ "new Double[] {0.0, 1.0}",
            /* test 10 -- should return true  */ "new TimeStamp(60)",
            /* test 11 -- should return true  */ "new TimeStamp(60,120)",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ false,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ true,
            /* test 11 should return */ true,
        };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestObjects )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidTimeStamp(%s) --> %b: ",
                        testNum, testDesc[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = Database.IsValidTimeStamp(testObjects[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
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

        /* Now verify that we throw a system error exception when
         * IsValidFloat is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidTimeStamp(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = Database.IsValidTimeStamp(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;

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

    } /* Database::TestIsValidTimeStamp() */


    /**
     * testIsValidQuoteString
     *
     * Run a variety of objects and valid and invalid strings past
     * IsValidQuoteString, and see it it gets the right answer.
     *
     *                                           -- 3/03/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestIsValidQuoteString(java.io.PrintStream outStream,
                                                 boolean verbose)
    {
        String testBanner =
            "Testing IsValidQuoteString()                                     ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        boolean result;
        boolean threwException;
        int failures = 0;
        int testNum = 0;
        final int numTestObjects = 30;
        Object[] testObjects = new Object[]
        {
            /* test  0 -- should return true  */ "A Valid Quote String",
            /* test  1 -- should return false */ new Float(0.0),
            /* test  2 -- should return false */ new Double(0.0),
            /* test  3 -- should return false */ new Integer(0),
            /* test  4 -- should return false */ new Long(0),
            /* test  5 -- should return false */ new Boolean(false),
            /* test  6 -- should return false */ new Character('c'),
            /* test  7 -- should return false */ new Byte((byte)'b'),
            /* test  8 -- should return false */ new Short((short)0),
            /* test  9 -- should return false */ new Double[] {0.0, 1.0},
            /* test 10 -- should return true  */ "(",
            /* test 11 -- should return true  */ ")",
            /* test 12 -- should return true  */ "<",
            /* test 13 -- should return true  */ ">",
            /* test 14 -- should return true  */ ",",
            /* test 15 -- should return true  */ " leading white space",
            /* test 16 -- should return true  */ "trailing while space ",
            /* test 17 -- should return true  */ "!#$%&\'()*+,-./",
            /* test 18 -- should return true  */ "0123456789\072;<=>?",
            /* test 19 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 20 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 21 -- should return true  */ "`abcdefghijklmno",
            /* test 22 -- should return true  */ "pqrstuvwxyz{\174}~",
            /* test 23 -- should return false */ "\177",
            /* test 24 -- should return false */ "horizontal\ttab",
            /* test 25 -- should return false */ "embedded\bback space",
            /* test 26 -- should return false */ "embedded\nnew line",
            /* test 27 -- should return false */ "embedded\fform feed",
            /* test 28 -- should return false */ "embedded\rcarriage return",
            /* test 29 -- should return true  */ "a",
        };
        String[] testDesc = new String[]
        {
            /* test  0 -- should return true  */ "A Valid Nominal",
            /* test  1 -- should return false */ "new Float(0.0)",
            /* test  2 -- should return false */ "new Double(0.0)",
            /* test  3 -- should return false */ "new Integer(0)",
            /* test  4 -- should return false */ "new Long(0)",
            /* test  5 -- should return false */ "new Boolean(false)",
            /* test  6 -- should return false */ "new Character('c')",
            /* test  7 -- should return false */ "new Byte((byte)'b')",
            /* test  8 -- should return false */ "new Short((short)0)",
            /* test  9 -- should return false */ "new Double[] {0.0, 1.0}",
            /* test 10 -- should return true  */ "(",
            /* test 11 -- should return true  */ ")",
            /* test 12 -- should return true  */ "<",
            /* test 13 -- should return true  */ ">",
            /* test 14 -- should return true  */ ",",
            /* test 15 -- should return true  */ " leading white space",
            /* test 16 -- should return true  */ "trailing while space ",
            /* test 17 -- should return true  */ "!#$%&\'()*+,-./",
            /* test 18 -- should return true  */ "0123456789\072;<=>?",
            /* test 19 -- should return true  */ "@ABCDEFGHIJKLMNO",
            /* test 20 -- should return true  */ "PQRSTUVWXYZ[\\]^_",
            /* test 21 -- should return true  */ "`abcdefghijklmno",
            /* test 22 -- should return true  */ "pqrstuvwxyz{\174}~",
            /* test 23 -- should return false */ "\177",
            /* test 24 -- should return false */ "horizontal\ttab",
            /* test 25 -- should return false */ "embedded\bback space",
            /* test 26 -- should return false */ "embedded\nnew line",
            /* test 27 -- should return false */ "embedded\fform feed",
            /* test 28 -- should return false */ "embedded\rcarriage return",
            /* test 29 -- should return true  */ "a",
        };
        boolean[] expectedResult = new boolean[]
        {
            /* test  0 should return */ true,
            /* test  1 should return */ false,
            /* test  2 should return */ false,
            /* test  3 should return */ false,
            /* test  4 should return */ false,
            /* test  5 should return */ false,
            /* test  6 should return */ false,
            /* test  7 should return */ false,
            /* test  8 should return */ false,
            /* test  9 should return */ false,
            /* test 10 should return */ true,
            /* test 11 should return */ true,
            /* test 12 should return */ true,
            /* test 13 should return */ true,
            /* test 14 should return */ true,
            /* test 15 should return */ true,
            /* test 16 should return */ true,
            /* test 17 should return */ true,
            /* test 18 should return */ true,
            /* test 19 should return */ true,
            /* test 20 should return */ true,
            /* test 21 should return */ true,
            /* test 22 should return */ true,
            /* test 23 should return */ false,
            /* test 24 should return */ false,
            /* test 25 should return */ false,
            /* test 26 should return */ false,
            /* test 27 should return */ false,
            /* test 28 should return */ false,
            /* test 29 should return */ true,
       };

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        while ( testNum < numTestObjects )
        {
            if ( verbose )
            {
                outStream.printf("test %d: IsValidQuoteString(\"%s\") --> %b: ",
                        testNum, testDesc[testNum],
                        expectedResult[testNum]);
            }

            threwException = false;
            result = false;

            try
            {
                result = Database.IsValidQuoteString(testObjects[testNum]);
            }
            catch (SystemErrorException e)
            {
                threwException = true;
            }

            if ( ( threwException ) ||
                 ( result != expectedResult[testNum] ) )
            {
                failures++;
                if ( verbose )
                {
                    if ( threwException )
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

        /* Now verify that we throw a system error exception when
         * IsValidQuoteString is called with a null parameter.
         */

        result = true;
        threwException = false;

        if ( verbose )
        {
            outStream.printf("test %d: IsValidQuoteString(null) --> exception: ",
                    testNum);
        }

        try
        {
            result = Database.IsValidQuoteString(null);
        }

        catch (SystemErrorException e)
        {
            threwException = true;
        }

        if ( ( ! result ) || ( ! threwException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( threwException )
                {
                    outStream.print("failed - threw exception and returned/n");
                }
                else
                {
                    outStream.print("failed - didn't threw exception./n");
                }
            }
        }
        else if ( verbose )
        {
            outStream.print("passes.\n");
        }

        testNum++;

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

    } /* Database::TestIsValidQuoteString() */

    /**
     * TestAddMatrixVE()
     *
     * Test the addMatrixVE() method.  Only cursory testing is needed, as
     * most functionality is provided via a call to one of the VocabList
     * methods.
     *
     *                                               -- 7/16/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestAddMatrixVE(java.io.PrintStream outStream,
                                          boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing addMatrixVE()                                            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* setup for test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.INTEGER,
                        alpha, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Now run a cursory set of tests:
         *
         * addMatrixVE() should succeed with mve, and fail with
         * null.  Passing pve should fail at compile time.  Since
         * addMatrixVE() is otherwise just a call to ve.addElement(),
         * no further testing is needed.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                mve_id = addMatrixVEPrivate(db,mve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( mve_id == DBIndex.INVALID_ID ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "addMatrixVE(mve) failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("addMatrixVE(mve) threw " +
                                "unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo(
                    "((VocabList) (vl_contents: (matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
            else if ( db.vl.getVocabElement(mve_id) == mve )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("addMatrixVE() failed to copy.\n");
                }
            }
        }

        /* now try to pass null to addMatrixVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                addMatrixVEPrivate(db,null);
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
                        outStream.print("addMatrixVE(null) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("addMatrixVE(null) failed to " +
                                "a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo(
                    "((VocabList) (vl_contents: (matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestAddMatrixVE() */


    /**
     * TestGetMatrixVE()
     *
     * Test the getMatrixVE() method.  Only cursory testing is needed, as
     * most functionality is provided via a call to one of the VocabList
     * methods.
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestGetMatrixVE(java.io.PrintStream outStream,
                                          boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing getMatrixVE()                                            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        VocabElement ve0 = null;
        VocabElement ve1 = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* setup for test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            ve0 = null;
            ve1 = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.INTEGER,
                        alpha, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* Now run a cursory set of tests:
         *
         * getMatrixVE(mve_id) and getMatrixVE("matrix") should both return
         * copies of mve, and getMatrixVE(pve_id) and getMatrixVE(INVALID_ID)
         * should both throw a system errors.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = db.getMatrixVE(mve_id);
                ve1 = db.getMatrixVE("matrix");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( ve0 == null ) ||
                 ( ! ( ve0 instanceof MatrixVocabElement ) ) ||
                 ( ve0.getName().compareTo("matrix") != 0 ) ||
                 ( ve0.getID() != mve_id ) ||
                 ( ve1 == null ) ||
                 ( ! ( ve1 instanceof MatrixVocabElement ) ) ||
                 ( ve1.getName().compareTo("matrix") != 0 ) ||
                 ( ve1.getID() != mve_id ) ||
                 ( ve0 == ve1 ) ||
                 ( ve0 == db.vl.getVocabElement(mve_id) ) ||
                 ( ve1 == db.vl.getVocabElement("matrix") ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("getMatrixVE(valid) test failed " +
                                "to complete.\n");
                    }

                    if ( ( ve0 == null ) ||
                         ( ! ( ve0 instanceof MatrixVocabElement ) ) ||
                         ( ve0.getName().compareTo("matrix") != 0 ) ||
                         ( ve0.getID() != mve_id ) ||
                         ( ve0 == db.vl.getVocabElement(mve_id) ) )
                    {
                        outStream.print("unexpected ve0.\n");
                    }

                    if ( ( ve1 == null ) ||
                         ( ! ( ve1 instanceof MatrixVocabElement ) ) ||
                         ( ve1.getName().compareTo("matrix") != 0 ) ||
                         ( ve1.getID() != mve_id ) ||
                         ( ve1 == db.vl.getVocabElement(mve_id) ) )
                    {
                        outStream.print("unexpected ve1.\n");
                    }

                    if ( ve0 == ve1 )
                    {
                        outStream.print("ve0 == ve1.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("getMatrixVE(valid) threw " +
                                "unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass pve_id to getMatrixVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = db.getMatrixVE(pve_id);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("getMatrixVE(pve_id) completed.\n");
                    }

                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("getMatrixVE(pve_id) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass "pred" to getMatrixVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = db.getMatrixVE("pred");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("getMatrixVE(\"pred\") completed.\n");
                    }

                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(2).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("getMatrixVE(\"pred\") failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the INVALID_ID to getMatrixVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = db.getMatrixVE(DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "getMatrixVE(INVALID_ID) completed.\n");
                    }

                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(3).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("getMatrixVE(INVALID_ID) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the string "nonesuch" to getMatrixVE() --
         * should fail
         */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = db.getMatrixVE("nonesuch");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "getMatrixVE(\"nonesuch\") completed.\n");
                    }

                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(4).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("getMatrixVE(\"nonesuch\") failed " +
                                "to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestGetMatrixVE() */

    /**
     * TestGetMatrixVEs()
     *
     * Test the getMatrixVEs() method.  Only cursory testing is needed, as
     * getMatrixVEs() just calls vl.getMatricies().
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestGetMatrixVEs(java.io.PrintStream outStream,
                                           boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing getMatrixVEs()                                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement inserted_mve = null;
        PredicateVocabElement pve = null;
        UnTypedFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        java.util.Vector<MatrixVocabElement> mves0;
        java.util.Vector<MatrixVocabElement> mves1;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* run the test:  Create a database and matrix and predicate, but
         * don't add them to the database at first.  Run getMatrixVEs() --
         * should return null.  Add the matrix and predicate and run
         * getMatrixVEs() again.  Should return a vector containing a
         * copy of the matrix, but not the predicate.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            mves0 = new java.util.Vector<MatrixVocabElement>();
            mves1 = null;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new UnTypedFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mves0 = db.getMatrixVEs();
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.MATRIX,
                        alpha, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                mves1 = db.getMatrixVEs();
                /* need a copy of the inserted mve for later testing, as
                 * adding the matix vocab element to the vocab list will
                 * assign ids to the ve and all its formal arguments.
                 */
                inserted_mve = db.getMatrixVE(mve_id);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( !mves0.isEmpty() ) ||
                 ( mves1 == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( !mves0.isEmpty() )
                    {
                        outStream.print("mves0 != null.\n");
                    }

                    if ( mves1 == null )
                    {
                        outStream.print("mves1 == null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
            else
            {
                MatrixVocabElement values[] = {inserted_mve};

                if ( ! VocabListTest.VerifyVectorContents(mves1, 1, values,
                                                      outStream, verbose, 1) )
                {
                    failures++;
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

    } /* Database::TestGetMatrixVEs() */


    /**
     * TestMatrixNameInUse()
     *
     * Test the matrixNameInUse() method.  Only cursory testing is needed, as
     * matrixNameInUse() just calls vl.inVocabList().
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestMatrixNameInUse(java.io.PrintStream outStream,
                                              boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing matrixNameInUse()                                        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        /* initialize the inUse booleans to the oposite of the expected values */
        boolean inUse0 = true;
        boolean inUse1 = true;
        boolean inUse2 = true;
        boolean inUse3 = false;
        boolean inUse4 = false;
        boolean inUse5 = true;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        UnTypedFormalArg alpha = null;
        UnTypedFormalArg bravo = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* run the test:  Create a database and matrix and predicate, but
         * don't add them to the database at first.  Run matrixNameInUse()
         * on the predicate and matrix name, along with an unused valid name.
         * All should return false.  Add the matrix and predicate and run
         * the set of calls to matrixNameInUse() again.  Should return true,
         * true and false respectively.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            inUse0 = true;
            inUse1 = true;
            inUse2 = true;
            inUse3 = false;
            inUse4 = false;
            inUse5 = true;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new UnTypedFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.MATRIX,
                        alpha, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                inUse0 = db.matrixNameInUse("matrix");
                inUse1 = db.matrixNameInUse("pred");
                inUse2 = db.matrixNameInUse("nonesuch");
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                inUse3 = db.matrixNameInUse("matrix");
                inUse4 = db.matrixNameInUse("pred");
                inUse5 = db.matrixNameInUse("nonesuch");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( inUse0 != false ) ||
                 ( inUse1 != false ) ||
                 ( inUse2 != false ) ||
                 ( inUse3 != true ) ||
                 ( inUse4 != true ) ||
                 ( inUse5 != false ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( ( inUse0 != false ) ||
                         ( inUse1 != false ) ||
                         ( inUse2 != false ) ||
                         ( inUse3 != true ) ||
                         ( inUse4 != true ) ||
                         ( inUse5 != false ) )
                    {
                        outStream.print(
                                "unexpected result(s) from mattrixNameInUse().\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the invalid string "<<invalid" to matrixNameInUse() --
         * should fail
         */
        if ( failures == 0 )
        {
            completed = false;
            inUse0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                inUse0 = db.matrixNameInUse("<<invalid");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( inUse0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "matrixNameInUse(\"<<invalid\") completed.\n");
                    }

                    if ( inUse0 != true )
                    {
                        outStream.print("inUse0 != true.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("matrixNameInUse(\"<<invalid\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestMatrixNameInUse() */

    /**
     * TestMatrixVEExists()
     *
     * Test the matrixVEExists() method.  Only cursory testing is needed, as
     * the function just calls vl.matrixInVocabList().
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestMatrixVEExists(java.io.PrintStream outStream,
                                             boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing matrixVEExists()                                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean mveExists0 = true;
        boolean mveExists1 = true;
        boolean mveExists2 = true;
        boolean mveExists3 = true;
        boolean mveExists4 = false;
        boolean mveExists5 = false;
        boolean mveExists6 = true;
        boolean mveExists7 = true;
        boolean mveExists8 = false;
        boolean mveExists9 = false;
        boolean mveExists10 = true;
        boolean mveExists11 = true;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* run a test with valid data */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            mve = null;
            smve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                smve = VocabListTest.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.MatrixType.INTEGER,
                        alpha, null, null, null);
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.MATRIX,
                        bravo, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                mveExists0 = db.matrixVEExists("s-matrix");
                mveExists1 = db.matrixVEExists("matrix");
                mveExists2 = db.matrixVEExists("pred");
                mveExists3 = db.matrixVEExists("nonesuch");
                smve_id = addMatrixVEPrivate(db,smve);
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                mveExists4 = db.matrixVEExists("s-matrix");
                mveExists5 = db.matrixVEExists("matrix");
                mveExists6 = db.matrixVEExists("pred");
                mveExists7 = db.matrixVEExists("nonesuch");
                mveExists8 = db.matrixVEExists(smve_id);
                mveExists9 = db.matrixVEExists(mve_id);
                mveExists10 = db.matrixVEExists(pve_id);
                mveExists11 = db.matrixVEExists(1024);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( mveExists0 != false ) ||
                 ( mveExists1 != false ) ||
                 ( mveExists2 != false ) ||
                 ( mveExists3 != false ) ||
                 ( mveExists4 != true ) ||
                 ( mveExists5 != true ) ||
                 ( mveExists6 != false ) ||
                 ( mveExists7 != false ) ||
                 ( mveExists8 != true ) ||
                 ( mveExists9 != true ) ||
                 ( mveExists10 != false ) ||
                 ( mveExists11 != false ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( ( mveExists0 != false ) ||
                         ( mveExists1 != false ) ||
                         ( mveExists2 != false ) ||
                         ( mveExists3 != false ) ||
                         ( mveExists4 != true ) ||
                         ( mveExists5 != true ) ||
                         ( mveExists6 != false ) ||
                         ( mveExists7 != false ) ||
                         ( mveExists8 != true ) ||
                         ( mveExists9 != true ) ||
                         ( mveExists10 != false ) ||
                         ( mveExists11 != false ) )
                    {
                        outStream.print(
                            "unexpected result(s) from matrixVEEsists().\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* Verify that matrixVEExists("<<invalid") and matrixVEExists(INVALID_ID)
         * throw system errors.
         */
        if ( failures == 0 )
        {
            completed = false;
            mveExists0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                mveExists0 = db.matrixVEExists("<<invalid");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( mveExists0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "matrixVEExists(\"<<invalid\") completed.\n");
                    }

                    if ( mveExists0 != true )
                    {
                        outStream.print("mveExists0 != true(1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("matrixVEExists(\"<<invalid\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            completed = false;
            mveExists0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                mveExists0 = db.matrixVEExists(DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( mveExists0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "matrixVEExists(INVALID_ID) completed.\n");
                    }

                    if ( mveExists0 != true )
                    {
                        outStream.print("mveExists0 != true(2).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("matrixVEExists(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestMatrixVEExists() */


    /**
     * TestRemoveMatrixVE()
     *
     * Test the removeMatrixVE() method.  Only cursory testing is needed, as
     * the function does little more than calls vl.removeVocabElement().
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestRemoveMatrixVE(java.io.PrintStream outStream,
                                             boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing removeMatrixVE()                                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* setup the test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            mve = null;
            smve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                smve = VocabListTest.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.MatrixType.INTEGER,
                        alpha, null, null, null);
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.MATRIX,
                        bravo, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                smve_id = addMatrixVEPrivate(db,smve);
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now call removeMatrixVE(mve_id).  Should succeed */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                removeMatrixVEPrivate(db, mve_id);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
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
                                "removeMatrixVE(mve_id) failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(2): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* Verify that removeMatrixVE(pve_id) throws a system error.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                removeMatrixVEPrivate(db, pve_id);
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
                        outStream.print("removeMatrixVE(pve_id) completed.\n");
                    }


                    if ( threwSystemErrorException )
                    {
                        outStream.print("removeMatrixVE(pve_id) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestRemoveMatrixVE() */


    /**
     * TestReplaceMatrixVE()
     *
     * Test the replaceMatrixVE() method.  Only cursory testing is needed, as
     * the function does little more than call vl.replaceVocabElement().
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestReplaceMatrixVE(java.io.PrintStream outStream,
                                              boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing replaceMatrixVE()                                        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement mod_mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* setup the test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            mve = null;
            smve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                smve = VocabListTest.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.MatrixType.INTEGER,
                        alpha, null, null, null);
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.MATRIX,
                        bravo, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                smve_id = addMatrixVEPrivate(db,smve);
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now get a copy of mve, modify it, and then call replaceMatrixVE()
         * with the modified version.  Should succeed
         */
        if ( failures == 0 )
        {
            completed = false;
            delta = null;
            mod_mve = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                mod_mve = db.getMatrixVE(mve_id);
                mod_mve.setName("mod_matrix");
                delta = new UnTypedFormalArg(db, "<delta>");
                mod_mve.appendFormalArg(delta);
                db.replaceMatrixVE(mod_mve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( mod_mve == null ) ||
                 ( delta == null ) ||
                 ( mod_mve == db.vl.getVocabElement(mod_mve.getID()) ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "test 1 failed to complete.\n");
                    }

                    if ( mod_mve == null )
                    {
                        outStream.print(
                                "getMatrixVE(mve_id) returned null.\n");
                    }

                    if ( delta == null )
                    {
                        outStream.print("couldn't allocate delta.\n");
                    }

                    if ( mod_mve == db.vl.getVocabElement(mod_mve.getID()) )
                    {
                        outStream.print("replacement isn't a copy.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(2): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (mod_matrix(<bravo>, <delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* Verify that replaceMatrixVE(null) throws a system error.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                db.replaceMatrixVE(null);
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
                        outStream.print("replaceMatrixVE(null) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("replaceMatrixVE(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (mod_matrix(<bravo>, <delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestReplaceMatrixVE() */


    /**
     * TestAddArgToPredVE()
     *
     * Test the addArgToPredVE() method.  The testing must be a bit more
     * involved that usual, as the target method creates a new argument for
     * the variable length predicate indicated, and then uses
     * VocabList.replaceVocabElement() to make the desired change.
     *
     *                                               -- 7/26/09
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestAddArgToPredVE(java.io.PrintStream outStream,
                                             boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing addArgToPredVE()                                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        String s0 = null;
        String s1 = null;
        String s2 = null;
        String s3 = null;
        String expected_string_0 =
        "(Undefined " +
          "((VocabList) " +
            "(vl_contents: " +
              "(pred data column(<val>), " +
               "vl_sys_pve(<echo>), " +
               "fl_sys_pve(<delta>), " +
               "vl_usr_pve(<charlie>), " +
               "fl_usr_pve(<bravo>)))) " +
          "((ColumnList) " +
            "(cl_contents: " +
              "((pred data column, " +
                "((1, 00:00:00:000, 00:00:01:000, (fl_usr_pve(1))), " +
                 "(2, 00:00:01:000, 00:00:02:000, (vl_usr_pve(2))), " +
                 "(3, 00:00:02:000, 00:00:03:000, (fl_sys_pve(3))), " +
                 "(4, 00:00:03:000, 00:00:04:000, (vl_sys_pve(4)))))))))";
        String expected_string_1 = "vl_usr_pve(<charlie>, <arg0>)";
        String expected_string_2 = "vl_usr_pve(<charlie>, <arg0>, <arg1>)";
        String expected_string_3 = "vl_sys_pve(<echo>, <arg0>)";
        String expected_string_4 = "vl_sys_pve(<echo>, <arg0>, <arg1>)";
        String expected_string_5 =
        "(Undefined " +
          "((VocabList) " +
            "(vl_contents: " +
              "(pred data column(<val>), " +
               "vl_sys_pve(<echo>), " +
               "fl_sys_pve(<delta>), " +
               "vl_usr_pve(<charlie>, <arg0>), " +
               "fl_usr_pve(<bravo>)))) " +
          "((ColumnList) " +
            "(cl_contents: " +
              "((pred data column, " +
                "((1, 00:00:00:000, 00:00:01:000, (fl_usr_pve(1))), " +
                 "(2, 00:00:01:000, 00:00:02:000, (vl_usr_pve(2, <arg0>))), " +
                 "(3, 00:00:02:000, 00:00:03:000, (fl_sys_pve(3))), " +
                 "(4, 00:00:03:000, 00:00:04:000, (vl_sys_pve(4)))))))))";
        String expected_string_6 = 
        "(Undefined " +
          "((VocabList) " +
            "(vl_contents: " +
              "(pred data column(<val>), " +
               "vl_sys_pve(<echo>), " +
               "fl_sys_pve(<delta>), " +
               "vl_usr_pve(<charlie>, <arg0>, <arg1>), " +
               "fl_usr_pve(<bravo>)))) " +
          "((ColumnList) " +
            "(cl_contents: " +
              "((pred data column, " +
                "((1, 00:00:00:000, 00:00:01:000, (fl_usr_pve(1))), " +
                 "(2, 00:00:01:000, 00:00:02:000, (vl_usr_pve(2, <arg0>, <arg1>))), " +
                 "(3, 00:00:02:000, 00:00:03:000, (fl_sys_pve(3))), " +
                 "(4, 00:00:03:000, 00:00:04:000, (vl_sys_pve(4)))))))))";
        String expected_string_7 = 
        "(Undefined " +
          "((VocabList) " +
            "(vl_contents: " +
              "(pred data column(<val>), " +
               "vl_sys_pve(<echo>, <arg0>), " +
               "fl_sys_pve(<delta>), " +
               "vl_usr_pve(<charlie>, <arg0>, <arg1>), " +
               "fl_usr_pve(<bravo>)))) " +
          "((ColumnList) " +
            "(cl_contents: " +
              "((pred data column, " +
                "((1, 00:00:00:000, 00:00:01:000, (fl_usr_pve(1))), " +
                 "(2, 00:00:01:000, 00:00:02:000, (vl_usr_pve(2, <arg0>, <arg1>))), " +
                 "(3, 00:00:02:000, 00:00:03:000, (fl_sys_pve(3))), " +
                 "(4, 00:00:03:000, 00:00:04:000, (vl_sys_pve(4, <arg0>)))))))))";
        String expected_string_8 = 
        "(Undefined " +
          "((VocabList) " +
            "(vl_contents: " +
              "(pred data column(<val>), " +
               "vl_sys_pve(<echo>, <arg0>, <arg1>), " +
               "fl_sys_pve(<delta>), " +
               "vl_usr_pve(<charlie>, <arg0>, <arg1>), " +
               "fl_usr_pve(<bravo>)))) " +
          "((ColumnList) " +
            "(cl_contents: " +
              "((pred data column, " +
                "((1, 00:00:00:000, 00:00:01:000, (fl_usr_pve(1))), " +
                 "(2, 00:00:01:000, 00:00:02:000, (vl_usr_pve(2, <arg0>, <arg1>))), " +
                 "(3, 00:00:02:000, 00:00:03:000, (fl_sys_pve(3))), " +
                 "(4, 00:00:03:000, 00:00:04:000, (vl_sys_pve(4, <arg0>, <arg1>)))))))))";
        String expected_db_string_0 =
        "(Undefined " +
          "((VocabList) " +
            "(vl_size: 5) " +
            "(vl_contents: " +
              "(((MatrixVocabElement: 9 pred data column) " +
                 "(system: true) " +
                 "(type: PREDICATE) " +
                 "(varLen: false) " +
                 "(fArgList: " +
                   "((PredFormalArg 10 <val> false ()))), " +
               "((PredicateVocabElement: 7 vl_sys_pve) " +
                 "(system: true) " +
                 "(varLen: true) " +
                 "(fArgList: ((UnTypedFormalArg 8 <echo>))), " +
               "((PredicateVocabElement: 5 fl_sys_pve) " +
                 "(system: true) " +
                 "(varLen: false) " +
                 "(fArgList: ((UnTypedFormalArg 6 <delta>))), " +
               "((PredicateVocabElement: 3 vl_usr_pve) " +
                 "(system: false) " +
                 "(varLen: true) " +
                 "(fArgList: ((UnTypedFormalArg 4 <charlie>))), " +
               "((PredicateVocabElement: 1 fl_usr_pve) " +
                 "(system: false) " +
                 "(varLen: false) " +
                 "(fArgList: ((UnTypedFormalArg 2 <bravo>)))))) " +
          "((ColumnList) " +
            "(cl_size: 1) " +
            "(cl_contents: " +
              "((DataColumn " +
                "(name pred data column) " +
                "(id 15) " +
                "(hidden false) " +
                "(readOnly false) " +
                "(itsMveID 9) " +
                "(itsMveType PREDICATE) " +
                "(varLen false) " +
                "(numCells 4) " +
                "(itsCells " +
                  "((DataCell " +
                     "(id 16) " +
                     "(itsColID 15) " +
                     "(itsMveID 9) " +
                     "(itsMveType PREDICATE) " +
                     "(ord 1) " +
                     "(onset (60,00:00:00:000)) " +
                     "(offset (60,00:00:01:000)) " +
                     "(val " +
                       "(Matrix " +
                         "(mveID 9) " +
                         "(varLen false) " +
                         "(argList " +
                           "((PredDataValue " +
                             "(id 17) " +
                             "(itsFargID 10) " +
                             "(itsFargType PREDICATE) " +
                             "(itsCellID 16) " +
                             "(itsValue " +
                               "(predicate " +
                                 "(id 18) " +
                                 "(predID 1) " +
                                 "(predName fl_usr_pve) " +
                                 "(varLen false) " +
                                 "(argList " +
                                   "((IntDataValue " +
                                     "(id 19) " +
                                     "(itsFargID 2) " +
                                     "(itsFargType UNTYPED) " +
                                     "(itsCellID 16) " +
                                     "(itsValue 1) " +
                                     "(subRange false) " +
                                     "(minVal 0) " +
                                     "(maxVal 0))))))) " +
                          "(subRange false)))))))), " +
                    "(DataCell " +
                      "(id 20) " +
                      "(itsColID 15) " +
                      "(itsMveID 9) " +
                      "(itsMveType PREDICATE) " +
                      "(ord 2) " +
                      "(onset (60,00:00:01:000)) " +
                      "(offset (60,00:00:02:000)) " +
                      "(val " +
                        "(Matrix " +
                          "(mveID 9) " +
                          "(varLen false) " +
                          "(argList " +
                            "((PredDataValue " +
                              "(id 21) " +
                              "(itsFargID 10) " +
                              "(itsFargType PREDICATE) " +
                              "(itsCellID 20) " +
                              "(itsValue " +
                                "(predicate " +
                                  "(id 22) " +
                                  "(predID 3) " +
                                  "(predName vl_usr_pve) " +
                                  "(varLen true) " +
                                  "(argList " +
                                    "((IntDataValue " +
                                      "(id 23) " +
                                      "(itsFargID 4) " +
                                      "(itsFargType UNTYPED) " +
                                      "(itsCellID 20) " +
                                      "(itsValue 2) " +
                                      "(subRange false) " +
                                      "(minVal 0) (maxVal 0))))))) " +
                          "(subRange false)))))))), " +
                    "(DataCell " +
                      "(id 24) " +
                      "(itsColID 15) " +
                      "(itsMveID 9) " +
                      "(itsMveType PREDICATE) " +
                      "(ord 3) " +
                      "(onset (60,00:00:02:000)) " +
                      "(offset (60,00:00:03:000)) " +
                      "(val " +
                        "(Matrix " +
                          "(mveID 9) " +
                          "(varLen false) " +
                          "(argList " +
                            "((PredDataValue " +
                              "(id 25) " +
                              "(itsFargID 10) " +
                              "(itsFargType PREDICATE) " +
                              "(itsCellID 24) " +
                              "(itsValue " +
                                "(predicate " +
                                  "(id 26) " +
                                  "(predID 5) " +
                                  "(predName fl_sys_pve) " +
                                  "(varLen false) " +
                                  "(argList " +
                                    "((IntDataValue " +
                                      "(id 27) " +
                                      "(itsFargID 6) " +
                                      "(itsFargType UNTYPED) " +
                                      "(itsCellID 24) " +
                                      "(itsValue 3) " +
                                      "(subRange false) " +
                                      "(minVal 0) " +
                                      "(maxVal 0))))))) " +
                          "(subRange false)))))))), " +
                    "(DataCell " +
                      "(id 28) " +
                      "(itsColID 15) " +
                      "(itsMveID 9) " +
                      "(itsMveType PREDICATE) " +
                      "(ord 4) " +
                      "(onset (60,00:00:03:000)) " +
                      "(offset (60,00:00:04:000)) " +
                      "(val " +
                        "(Matrix " +
                          "(mveID 9) " +
                          "(varLen false) " +
                          "(argList " +
                            "((PredDataValue " +
                              "(id 29) " +
                              "(itsFargID 10) " +
                              "(itsFargType PREDICATE) " +
                              "(itsCellID 28) " +
                              "(itsValue " +
                                "(predicate " +
                                  "(id 30) " +
                                  "(predID 7) " +
                                  "(predName vl_sys_pve) " +
                                  "(varLen true) " +
                                  "(argList " +
                                    "((IntDataValue " +
                                      "(id 31) " +
                                      "(itsFargID 8) " +
                                      "(itsFargType UNTYPED) " +
                                      "(itsCellID 28) " +
                                      "(itsValue 4) " +
                                      "(subRange false) " +
                                      "(minVal 0) " +
                                      "(maxVal 0))))))) " +
                          "(subRange false))))))))))))))))";
        String expected_db_string_1 = 
        "((PredicateVocabElement: 3 vl_usr_pve) " +
          "(system: false) " +
          "(varLen: true) " +
          "(fArgList: " +
            "((UnTypedFormalArg 4 <charlie>), " +
            "(UnTypedFormalArg 32 <arg0>)))";
        String expected_db_string_2 = 
        "((PredicateVocabElement: 3 vl_usr_pve) " +
          "(system: false) " +
          "(varLen: true) " +
          "(fArgList: " +
            "((UnTypedFormalArg 4 <charlie>), " +
             "(UnTypedFormalArg 32 <arg0>), " +
             "(UnTypedFormalArg 34 <arg1>)))";
        String expected_db_string_3 = 
        "((PredicateVocabElement: 7 vl_sys_pve) " +
          "(system: true) " +
          "(varLen: true) " +
          "(fArgList: " +
            "((UnTypedFormalArg 8 <echo>), " +
             "(UnTypedFormalArg 36 <arg0>)))";
        String expected_db_string_4 = 
        "((PredicateVocabElement: 7 vl_sys_pve) " +
          "(system: true) " +
          "(varLen: true) " +
          "(fArgList: " +
            "((UnTypedFormalArg 8 <echo>), " +
             "(UnTypedFormalArg 36 <arg0>), " +
             "(UnTypedFormalArg 38 <arg1>)))";
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long fl_usr_pve_ID = DBIndex.INVALID_ID;
        long vl_usr_pve_ID = DBIndex.INVALID_ID;
        long fl_sys_pve_ID = DBIndex.INVALID_ID;
        long vl_sys_pve_ID = DBIndex.INVALID_ID;
        long pred_mve_ID = DBIndex.INVALID_ID;
        long pred_dc_ID = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement pred_mve = null;
        PredicateVocabElement fl_usr_pve = null;
        PredicateVocabElement vl_usr_pve = null;
        PredicateVocabElement vl_usr_pve_1 = null;
        PredicateVocabElement vl_usr_pve_2 = null;
        PredicateVocabElement fl_sys_pve = null;
        PredicateVocabElement vl_sys_pve = null;
        PredicateVocabElement vl_sys_pve_1 = null;
        PredicateVocabElement vl_sys_pve_2 = null;
        PredicateVocabElement pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta = null;
        UnTypedFormalArg echo = null;
        UnTypedFormalArg foxtrot = null;
        DataColumn pred_dc = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* setup for test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            delta = null;
            echo = null;
            foxtrot = null;
            pred_dc = null;
            pred_mve = null;
            fl_usr_pve = null;
            vl_usr_pve = null;
            fl_sys_pve = null;
            vl_sys_pve = null;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();

                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                delta = new UnTypedFormalArg(db, "<delta>");
                echo = new UnTypedFormalArg(db, "<echo>");
                foxtrot = new UnTypedFormalArg(db, "<foxtrot>");

                fl_usr_pve = VocabListTest.ConstructTestPred(db, "fl_usr_pve",
                        bravo, null, null, null);
                fl_usr_pve.setVarLen(false);

                vl_usr_pve = VocabListTest.ConstructTestPred(db, "vl_usr_pve",
                        charlie, null, null, null);
                vl_usr_pve.setVarLen(true);

                fl_sys_pve = VocabListTest.ConstructTestPred(db, "fl_sys_pve",
                        delta, null, null, null);
                fl_sys_pve.setVarLen(false);
                fl_sys_pve.setSystem();

                vl_sys_pve = VocabListTest.ConstructTestPred(db, "vl_sys_pve",
                        echo, null, null, null);
                vl_sys_pve.setVarLen(true);
                vl_sys_pve.setSystem();

                fl_usr_pve_ID = db.addPredVE(fl_usr_pve);
                vl_usr_pve_ID = db.addPredVE(vl_usr_pve);

                fl_sys_pve_ID = db.addSystemPredVE(fl_sys_pve);
                vl_sys_pve_ID = db.addSystemPredVE(vl_sys_pve);

                pred_dc = new DataColumn(db,
                        "pred data column",
                        MatrixVocabElement.MatrixType.PREDICATE);

                pred_dc_ID = db.addColumn(pred_dc);

                pred_dc = db.getDataColumn(pred_dc_ID);

                pred_mve_ID = pred_dc.getItsMveID();

                pred_mve = db.getMatrixVE(pred_mve_ID);

                // cells for pred_dc
                db.appendCell(
                    DataCell.Construct(
                        db,
                        pred_dc_ID,
                        pred_mve_ID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            pred_mve_ID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    fl_usr_pve_ID,
                                    IntDataValue.Construct(db, 1))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        pred_dc_ID,
                        pred_mve_ID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            pred_mve_ID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    vl_usr_pve_ID,
                                    IntDataValue.Construct(db, 2))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        pred_dc_ID,
                        pred_mve_ID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            pred_mve_ID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    fl_sys_pve_ID,
                                    IntDataValue.Construct(db, 3))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        pred_dc_ID,
                        pred_mve_ID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            pred_mve_ID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    vl_sys_pve_ID,
                                    IntDataValue.Construct(db, 4))))));

                pred_dc = db.getDataColumn(pred_dc_ID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( delta == null ) ||
                 ( echo == null ) ||
                 ( foxtrot == null ) ||
                 ( pred_dc == null ) ||
                 ( pred_mve == null ) ||
                 ( fl_usr_pve == null ) ||
                 ( vl_usr_pve == null ) ||
                 ( fl_sys_pve == null ) ||
                 ( vl_sys_pve == null ) ||
                 ( pred_dc_ID == DBIndex.INVALID_ID ) ||
                 ( pred_mve_ID == DBIndex.INVALID_ID ) ||
                 ( fl_usr_pve_ID == DBIndex.INVALID_ID ) ||
                 ( vl_usr_pve_ID == DBIndex.INVALID_ID ) ||
                 ( fl_sys_pve_ID == DBIndex.INVALID_ID ) ||
                 ( vl_sys_pve_ID == DBIndex.INVALID_ID ) ||
                 ( pred_dc.getNumCells() != 4 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null 1.\n");
                    }

                    if ( charlie == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null 2.\n");
                    }

                    if ( delta == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null 3.\n");
                    }

                    if ( echo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null 4.\n");
                    }

                    if ( foxtrot == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null 5.\n");
                    }

                    if ( pred_dc == null )
                    {
                        outStream.print("couldn't construct pred_dc.\n");
                    }

                    if ( pred_mve == null )
                    {
                        outStream.print("couldn't obtain pred_mve.\n");
                    }

                    if ( fl_usr_pve == null )
                    {
                        outStream.print("couldn't construct fl_usr_pve.\n");
                    }

                    if ( vl_usr_pve == null )
                    {
                        outStream.print("couldn't construct vl_usr_pve.\n");
                    }

                    if ( fl_sys_pve == null )
                    {
                        outStream.print("couldn't construct fl_sys_pve.\n");
                    }

                    if ( vl_sys_pve == null )
                    {
                        outStream.print("couldn't construct vl_sys_pve.\n");
                    }

                    if ( pred_dc_ID == DBIndex.INVALID_ID )
                    {
                        outStream.print("couldn't insert pred_dc.\n");
                    }

                    if ( pred_mve_ID == DBIndex.INVALID_ID )
                    {
                        outStream.print("couldn't obtain pred_mve_ID.\n");
                    }

                    if ( fl_usr_pve_ID == DBIndex.INVALID_ID )
                    {
                        outStream.print("couldn't insert fl_usr_pve.\n");
                    }

                    if ( vl_usr_pve_ID == DBIndex.INVALID_ID )
                    {
                        outStream.print("couldn't insert vl_usr_pve.\n");
                    }

                    if ( fl_sys_pve_ID == DBIndex.INVALID_ID )
                    {
                        outStream.print("couldn't insert fl_sys_pve.\n");
                    }

                    if ( vl_sys_pve_ID == DBIndex.INVALID_ID )
                    {
                        outStream.print("couldn't insert vl_sys_pve.\n");
                    }

                    if ( pred_dc.getNumCells() != 4 )
                    {
                        outStream.print(
                            "pred_dc contains unexpected number of cells.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.toString().compareTo(expected_string_0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "unexpected db.toString() after setup: \"%s\"\n",
                            db.toString());
                }
            }
            else if ( db.toDBString().compareTo(expected_db_string_0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "unexpected db.toDBString() after setup: \"%s\"\n",
                            db.toDBString());
                }
            }
        }

        /* test setup is complete.
         *
         * Start with calls that should succeed -- specifically attempts to
         * add arguments to variable length user and system predicates.
         */
        if ( failures == 0 )
        {
            s0 = null;
            s1 = null;
            s2 = null;
            s3 = null;
            vl_usr_pve_1 = null;
            vl_usr_pve_2 = null;
            vl_sys_pve_1 = null;
            vl_sys_pve_2 = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl_usr_pve_1 = db.addArgToPredVE(vl_usr_pve_ID);
                s0 = db.toString();

                vl_usr_pve_2 = db.addArgToPredVE(vl_usr_pve_ID);
                s1 = db.toString();

                vl_sys_pve_1 = db.addArgToPredVE(vl_sys_pve_ID);
                s2 = db.toString();

                vl_sys_pve_2 = db.addArgToPredVE(vl_sys_pve_ID);
                s3 = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( vl_usr_pve_1 == null ) ||
                 ( vl_usr_pve_2 == null ) ||
                 ( vl_sys_pve_1 == null ) ||
                 ( vl_sys_pve_2 == null ) ||
                 ( s0 == null ) ||
                 ( s1 == null ) ||
                 ( s2 == null ) ||
                 ( s3 == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( vl_usr_pve_1 == null )
                    {
                        outStream.print("vl_usr_pve_1 == null.\n");
                    }

                    if ( vl_usr_pve_2 == null )
                    {
                        outStream.print("vl_usr_pve_2 == null.\n");
                    }

                    if ( vl_sys_pve_1 == null )
                    {
                        outStream.print("vl_sys_pve_1 == null.\n");
                    }

                    if ( vl_sys_pve_2 == null )
                    {
                        outStream.print("vl_sys_pve_2 == null.\n");
                    }

                    if ( ( s0 == null ) ||
                         ( s1 == null ) ||
                         ( s2 == null ) ||
                         ( s3 == null ) )
                    {
                        outStream.print("one or more test strings is null.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print("sequence of call to addArgToPredVE " +
                                        "failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("addArgToPredVE(id) threw " +
                                "unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( vl_usr_pve_1.toString().compareTo(expected_string_1)
                      != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "uexpected vl_usr_pve_1.toString(): \"%s\"\n",
                            vl_usr_pve_1.toString());
                }
            }
            else if ( vl_usr_pve_2.toString().compareTo(expected_string_2)
                      != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "uexpected vl_usr_pve_2.toString(): \"%s\"\n",
                            vl_usr_pve_2.toString());
                }
            }
            else if ( vl_sys_pve_1.toString().compareTo(expected_string_3)
                      != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "uexpected vl_sys_pve_1.toString(): \"%s\"\n",
                            vl_sys_pve_1.toString());
                }
            }
            else if ( vl_sys_pve_2.toString().compareTo(expected_string_4)
                      != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "uexpected vl_sys_pve_2.toString(): \"%s\"\n",
                            vl_sys_pve_2.toString());
                }
            }
            else if ( vl_usr_pve_1.toDBString().compareTo(expected_db_string_1)
                      != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "unexpected vl_usr_pve_1.toDBString(): \"%s\"\n",
                            vl_usr_pve_1.toDBString());
                }
            }
            else if ( vl_usr_pve_2.toDBString().compareTo(expected_db_string_2)
                      != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "unexpected vl_usr_pve_2.toDBString(): \"%s\"\n",
                            vl_usr_pve_2.toDBString());
                }
            }
            else if ( vl_sys_pve_1.toDBString().compareTo(expected_db_string_3)
                      != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "unexpected vl_sys_pve_1.toDBString(): \"%s\"\n",
                            vl_sys_pve_1.toDBString());
                }
            }
            else if ( vl_sys_pve_2.toDBString().compareTo(expected_db_string_4)
                      != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "unexpected vl_sys_pve_2.toDBString(): \"%s\"\n",
                            vl_sys_pve_2.toDBString());
                }
            }
            else if ( s0.compareTo(expected_string_5) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("unexpected test string s0: \"%s\"\n", s0);
                }
            }
            else if ( s1.compareTo(expected_string_6) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("unexpected test string s1: \"%s\"\n", s1);
                }
            }
            else if ( s2.compareTo(expected_string_7) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("unexpected test string s2: \"%s\"\n", s2);
                }
            }
            else if ( s3.compareTo(expected_string_8) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("unexpected test string s3: \"%s\"\n", s3);
                }
            }
        }


        /* pass the invalid ID to db.addArgToSystemPred -- should fail */
        if ( failures == 0 )
        {
            pve = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pve = db.addArgToPredVE(DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
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
                        outStream.print(
                                "addArgToPredVE(invalid) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("addArgToPredVE(invalid) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("addArgToPredVE(invalid) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else
            {
                s0 = db.toString();

                if ( s0 == null )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("db.toString() returned null after " +
                                        "addArgToPredVE(invalid).\n");
                    }
                }
                else if ( s0.compareTo(expected_string_8) != 0 )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("db.toString() returned unexpected " +
                                "string after addArgToPredVE(invalid): " +
                                "\"%s\"\n", s0);
                    }
                }
            }
        }


        /* pass an unused ID to db.addArgToSystemPred -- should fail */
        if ( failures == 0 )
        {
            pve = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pve = db.addArgToPredVE(1066);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
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
                        outStream.print(
                                "addArgToPredVE(1066) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print("addArgToPredVE(1066) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("addArgToPredVE(1066) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else
            {
                s0 = db.toString();

                if ( s0 == null )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("db.toString() returned null after " +
                                        "addArgToPredVE(1066).\n");
                    }
                }
                else if ( s0.compareTo(expected_string_8) != 0 )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("db.toString() returned unexpected " +
                                "string after addArgToPredVE(1066): " +
                                "\"%s\"\n", s0);
                    }
                }
            }
        }


        /* pass the ID of a fixed length user pve db.addArgToSystemPred
         * -- should fail
         */
        if ( failures == 0 )
        {
            pve = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pve = db.addArgToPredVE(fl_usr_pve_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
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
                        outStream.print(
                            "addArgToPredVE(fl_usr_pve_ID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print(
                                "addArgToPredVE(fl_usr_pve_ID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("addArgToPredVE(fl_usr_pve_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else
            {
                s0 = db.toString();

                if ( s0 == null )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("db.toString() returned null after " +
                                        "addArgToPredVE(fl_usr_pve_ID).\n");
                    }
                }
                else if ( s0.compareTo(expected_string_8) != 0 )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("db.toString() returned unexpected " +
                                "string after addArgToPredVE(fl_usr_pve_ID): " +
                                "\"%s\"\n", s0);
                    }
                }
            }
        }


        /* pass the ID of a fixed length system pve db.addArgToSystemPred
         *              -- should fail
         */
        if ( failures == 0 )
        {
            pve = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pve = db.addArgToPredVE(fl_sys_pve_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
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
                        outStream.print(
                            "addArgToPredVE(fl_sys_pve_ID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print(
                                "addArgToPredVE(fl_sys_pve_ID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("addArgToPredVE(fl_sys_pve_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else
            {
                s0 = db.toString();

                if ( s0 == null )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("db.toString() returned null after " +
                                        "addArgToPredVE(fl_sys_pve_ID).\n");
                    }
                }
                else if ( s0.compareTo(expected_string_8) != 0 )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("db.toString() returned unexpected " +
                                "string after addArgToPredVE(fl_sys_pve_ID): " +
                                "\"%s\"\n", s0);
                    }
                }
            }
        }


        /* pass the ID of the predicate column mve to db.addArgToPredVE()
         *              -- should fail
         */
        if ( failures == 0 )
        {
            pve = null;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pve = db.addArgToPredVE(pred_mve_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
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
                        outStream.print(
                            "addArgToPredVE(pred_mve_ID) returned non-null.\n");
                    }

                    if ( completed )
                    {
                        outStream.print(
                                "addArgToPredVE(pred_mve_ID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("addArgToPredVE(pred_mve_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else
            {
                s0 = db.toString();

                if ( s0 == null )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.print("db.toString() returned null after " +
                                        "addArgToPredVE(pred_mve_ID).\n");
                    }
                }
                else if ( s0.compareTo(expected_string_8) != 0 )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf("db.toString() returned unexpected " +
                                "string after addArgToPredVE(pred_mve_ID): " +
                                "\"%s\"\n", s0);
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

    } /* Database::TestAddArgToPredVE() */


    /**
     * TestAddPredVE()
     *
     * Test the addPredVE() method.  Only cursory testing is needed, as
     * most functionality is provided via a call to one of the VocabList
     * methods.
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestAddPredVE(java.io.PrintStream outStream,
                                        boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing addPredVE()                                              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* setup for test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.INTEGER,
                        alpha, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Now run a cursory set of tests:
         *
         * addPredVE() should succeed with mve, and fail with
         * null.  Passing pve should fail at compile time.  Since
         * addPredVE() is otherwise just a call to ve.addElement(),
         * no further testing is needed.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pve_id = db.addPredVE(pve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( pve_id == DBIndex.INVALID_ID ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( ! completed )
                    {
                        outStream.print(
                                "addPredVE(pve) failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("addPredVE(pve) threw " +
                                "unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo(
                    "((VocabList) (vl_contents: (pred(<bravo>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
            else if ( db.vl.getVocabElement(pve_id) == pve )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("addPredVE() failed to copy.\n");
                }
            }
        }

        /* now try to pass null to addPredVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                db.addPredVE(null);
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
                        outStream.print("addPredVE(null) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("addPredVE(null) failed to " +
                                "a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo(
                    "((VocabList) (vl_contents: (pred(<bravo>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestAddPredVE() */


    /**
     * TestGetPredVE()
     *
     * Test the getPredVE() method.  Only cursory testing is needed, as
     * most functionality is provided via a call to one of the VocabList
     * methods.
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestGetPredVE(java.io.PrintStream outStream,
                                        boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing getPredVE()                                              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        VocabElement ve0 = null;
        VocabElement ve1 = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* setup for test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            ve0 = null;
            ve1 = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.INTEGER,
                        alpha, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* Now run a cursory set of tests:
         *
         * getPredVE(pve_id) and getPredVE("pred") should both return
         * copies of pve, and getPredVE(mve_id) and getPredVE("matrix")
         * should both throw a system errors.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = db.getPredVE(pve_id);
                ve1 = db.getPredVE("pred");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( ve0 == null ) ||
                 ( ! ( ve0 instanceof PredicateVocabElement ) ) ||
                 ( ve0.getName().compareTo("pred") != 0 ) ||
                 ( ve0.getID() != pve_id ) ||
                 ( ve1 == null ) ||
                 ( ! ( ve1 instanceof PredicateVocabElement ) ) ||
                 ( ve1.getName().compareTo("pred") != 0 ) ||
                 ( ve1.getID() != pve_id ) ||
                 ( ve0 == ve1 ) ||
                 ( ve0 == db.vl.getVocabElement(pve_id) ) ||
                 ( ve1 == db.vl.getVocabElement("pred") ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("getPredVE(valid) test failed " +
                                "to complete.\n");
                    }

                    if ( ( ve0 == null ) ||
                         ( ! ( ve0 instanceof PredicateVocabElement ) ) ||
                         ( ve0.getName().compareTo("pred") != 0 ) ||
                         ( ve0.getID() != pve_id ) ||
                         ( ve0 == db.vl.getVocabElement(pve_id) ) )
                    {
                        outStream.print("unexpected ve0.\n");
                    }

                    if ( ( ve1 == null ) ||
                         ( ! ( ve1 instanceof PredicateVocabElement ) ) ||
                         ( ve1.getName().compareTo("pred") != 0 ) ||
                         ( ve1.getID() != pve_id ) ||
                         ( ve1 == db.vl.getVocabElement("pred") ) )
                    {
                        outStream.print("unexpected ve1.\n");
                    }

                    if ( ve0 == ve1 )
                    {
                        outStream.print("ve0 == ve1.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("getPredVE(valid) threw " +
                                "unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass mve_id to getPredVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = db.getPredVE(mve_id);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("getPredVE(mve_id) completed.\n");
                    }

                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("getPredVE(mve_id) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass "matrix" to getPredVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = db.getPredVE("matrix");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("getPredVE(\"matrix\") completed.\n");
                    }

                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(2).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("getPredVE(\"matrix\") failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the INVALID_ID to getPredVE() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = db.getPredVE(DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "getPredVE(INVALID_ID) completed.\n");
                    }

                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(3).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("getPredVE(INVALID_ID) failed to " +
                                "throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the string "nonesuch" to getPredVE() --
         * should fail
         */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = db.getPredVE("nonesuch");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "getPredVE(\"nonesuch\") completed.\n");
                    }

                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(4).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("getPredVE(\"nonesuch\") failed " +
                                "to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestGetPredVE() */

    /**
     * TestGetPredVEs()
     *
     * Test the getPredVEs() method.  Only cursory testing is needed, as
     * getPredVEs() just calls vl.getMatricies().
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestGetPredVEs(java.io.PrintStream outStream,
                                         boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing getPredVEs()                                             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        PredicateVocabElement inserted_pve = null;
        UnTypedFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        java.util.Vector<PredicateVocabElement> pves0;
        java.util.Vector<PredicateVocabElement> pves1;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* run the test:  Create a database and matrix and predicate, but
         * don't add them to the database at first.  Run getPredVEs() --
         * should return null.  Add the matrix and predicate and run
         * getPredVEs() again.  Should return a vector containing a
         * copy of the predicate, but not the matrix.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            pves0 = new java.util.Vector<PredicateVocabElement>();
            pves1 = null;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new UnTypedFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.MATRIX,
                        alpha, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                pves0 = db.getPredVEs();
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                pves1 = db.getPredVEs();
                /* need a copy of the inserted pve for later testing, as
                 * adding the predicate vocab element to the vocab list will
                 * assign ids to the ve and all its formal arguments.
                 */
                inserted_pve = db.getPredVE(pve_id);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( !pves0.isEmpty() ) ||
                 ( pves1 == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( !pves0.isEmpty() )
                    {
                        outStream.print("pves0 != null.\n");
                    }

                    if ( pves1 == null )
                    {
                        outStream.print("pves1 == null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
            else
            {
                PredicateVocabElement values[] = {inserted_pve};

                if ( ! VocabListTest.VerifyVectorContents(pves1, 1, values,
                                                      outStream, verbose, 1) )
                {
                    failures++;
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

    } /* Database::TestGetPredVEs() */


    /**
     * TestPredNameInUse()
     *
     * Test the predNameInUse() method.  Only cursory testing is needed, as
     * predNameInUse() just calls vl.inVocabList().
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestPredNameInUse(java.io.PrintStream outStream,
                                            boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing predNameInUse()                                          ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        /* initialize the inUse booleans to the opposite of the expected values */
        boolean inUse0 = true;
        boolean inUse1 = true;
        boolean inUse2 = true;
        boolean inUse3 = false;
        boolean inUse4 = false;
        boolean inUse5 = true;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        UnTypedFormalArg alpha = null;
        UnTypedFormalArg bravo = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* run the test:  Create a database and matrix and predicate, but
         * don't add them to the database at first.  Run matrixNameInUse()
         * on the predicate and matrix name, along with an unused valid name.
         * All should return false.  Add the matrix and predicate and run
         * the set of calls to matrixNameInUse() again.  Should return true,
         * true and false respectively.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            inUse0 = true;
            inUse1 = true;
            inUse2 = true;
            inUse3 = false;
            inUse4 = false;
            inUse5 = true;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new UnTypedFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.MATRIX,
                        alpha, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                inUse0 = db.predNameInUse("matrix");
                inUse1 = db.predNameInUse("pred");
                inUse2 = db.predNameInUse("nonesuch");
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                inUse3 = db.predNameInUse("matrix");
                inUse4 = db.predNameInUse("pred");
                inUse5 = db.predNameInUse("nonesuch");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( inUse0 != false ) ||
                 ( inUse1 != false ) ||
                 ( inUse2 != false ) ||
                 ( inUse3 != true ) ||
                 ( inUse4 != true ) ||
                 ( inUse5 != false ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( ( inUse0 != false ) ||
                         ( inUse1 != false ) ||
                         ( inUse2 != false ) ||
                         ( inUse3 != true ) ||
                         ( inUse4 != true ) ||
                         ( inUse5 != false ) )
                    {
                        outStream.print(
                                "unexpected result(s) from predNameInUse().\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the invalid string "<<invalid" to predNameInUse() --
         * should fail
         */
        if ( failures == 0 )
        {
            completed = false;
            inUse0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                inUse0 = db.predNameInUse("<<invalid");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( inUse0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "predNameInUse(\"<<invalid\") completed.\n");
                    }

                    if ( inUse0 != true )
                    {
                        outStream.print("inUse0 != true.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("predNameInUse(\"<<invalid\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestPredNameInUse() */

    /**
     * TestPredVEExists()
     *
     * Test the predVEExists() method.  Only cursory testing is needed, as
     * the function just calls vl.predInVocabList().
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     * <ul>
     *   <li>
     *      Modified test to use addSystemPredVE() when creating a system
     *      predicate.  This change was necessitated by my tightening up
     *      who can create and/or modify system predicates.
     *
     *                                              JRM -- 7/26/09
     *   </li>
     * </ul>
     */

    public static boolean TestPredVEExists(java.io.PrintStream outStream,
                                           boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing predVEExists()                                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pveExists0 = true;
        boolean pveExists1 = true;
        boolean pveExists2 = true;
        boolean pveExists3 = true;
        boolean pveExists4 = true;
        boolean pveExists5 = true;
        boolean pveExists6 = true;
        boolean pveExists7 = false;
        boolean pveExists8 = false;
        boolean pveExists9 = true;
        boolean pveExists10 = true;
        boolean pveExists11 = true;
        boolean pveExists12 = false;
        boolean pveExists13 = false;
        boolean pveExists14 = true;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        long spve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        PredicateVocabElement spve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* run a test with valid data */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            delta = null;
            mve = null;
            smve = null;
            pve = null;
            spve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            spve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                delta = new UnTypedFormalArg(db, "<delta>");
                smve = VocabListTest.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.MatrixType.INTEGER,
                        alpha, null, null, null);
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.MATRIX,
                        bravo, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                spve = VocabListTest.ConstructTestPred(db, "s-pred", delta, null,
                                                  null, null);
                spve.setSystem();
                pveExists0 = db.predVEExists("s-matrix");
                pveExists1 = db.predVEExists("matrix");
                pveExists2 = db.predVEExists("pred");
                pveExists3 = db.predVEExists("s-pred");
                pveExists4 = db.predVEExists("nonesuch");
                smve_id = addMatrixVEPrivate(db,smve);
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                spve_id = db.addSystemPredVE(spve);
                pveExists5 = db.predVEExists("s-matrix");
                pveExists6 = db.predVEExists("matrix");
                pveExists7 = db.predVEExists("pred");
                pveExists8 = db.predVEExists("s-pred");
                pveExists9 = db.predVEExists("nonesuch");
                pveExists10 = db.predVEExists(smve_id);
                pveExists11 = db.predVEExists(mve_id);
                pveExists12 = db.predVEExists(pve_id);
                pveExists13 = db.predVEExists(spve_id);
                pveExists14 = db.predVEExists(1024);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( pveExists0 != false ) ||
                 ( pveExists1 != false ) ||
                 ( pveExists2 != false ) ||
                 ( pveExists3 != false ) ||
                 ( pveExists4 != false ) ||
                 ( pveExists5 != false ) ||
                 ( pveExists6 != false ) ||
                 ( pveExists7 != true ) ||
                 ( pveExists8 != true ) ||
                 ( pveExists9 != false ) ||
                 ( pveExists10 != false ) ||
                 ( pveExists11 != false ) ||
                 ( pveExists12 != true ) ||
                 ( pveExists13 != true ) ||
                 ( pveExists14 != false ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( ( pveExists0 != false ) ||
                         ( pveExists1 != false ) ||
                         ( pveExists2 != false ) ||
                         ( pveExists3 != false ) ||
                         ( pveExists4 != false ) ||
                         ( pveExists5 != false ) ||
                         ( pveExists6 != false ) ||
                         ( pveExists7 != true ) ||
                         ( pveExists8 != true ) ||
                         ( pveExists9 != false ) ||
                         ( pveExists10 != false ) ||
                         ( pveExists11 != false ) ||
                         ( pveExists12 != true ) ||
                         ( pveExists13 != true ) ||
                         ( pveExists14 != false ) )
                    {
                        outStream.print(
                            "unexpected result(s) from predVEEsists().\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), s-pred(<delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* Verify that predVEExists("<<invalid") and predVEExists(INVALID_ID)
         * throw system errors.
         */
        if ( failures == 0 )
        {
            completed = false;
            pveExists0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pveExists0 = db.predVEExists("<<invalid");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( pveExists0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "predVEExists(\"<<invalid\") completed.\n");
                    }

                    if ( pveExists0 != true )
                    {
                        outStream.print("pveExists0 != true(1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("predVEExists(\"<<invalid\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), s-pred(<delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            completed = false;
            pveExists0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                pveExists0 = db.predVEExists(DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( pveExists0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "predVEExists(INVALID_ID) completed.\n");
                    }

                    if ( pveExists0 != true )
                    {
                        outStream.print("pveExists0 != true(2).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("predVEExists(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), s-pred(<delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestPredVEExists() */

    /**
     * TestRemovePredVE()
     *
     * Test the removePredVE() method.  Only cursory testing is needed, as
     * the function does little more than calls vl.removeVocabElement().
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestRemovePredVE(java.io.PrintStream outStream,
                                           boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing removePredVE()                                           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* setup the test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            mve = null;
            smve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                smve = VocabListTest.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.MatrixType.INTEGER,
                        alpha, null, null, null);
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.MATRIX,
                        bravo, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                smve_id = addMatrixVEPrivate(db,smve);
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now call removePredVE(pve_id).  Should succeed */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                db.removePredVE(pve_id);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
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
                                "removePredVE(pve_id) failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(2): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* Verify that removePredVE(mve_id) throws a system error.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                db.removePredVE(mve_id);
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
                        outStream.print("removePredVE(mve_id) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("removePredVE(mve_id) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestRemovePredVE() */


    /**
     * TestReplacePredVE()
     *
     * Test the replaceMatrixVE() method.  Only cursory testing is needed, as
     * the function does little more than call vl.replaceVocabElement().
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestReplacePredVE(java.io.PrintStream outStream,
                                            boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing replacePredVE()                                          ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        PredicateVocabElement mod_pve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* setup the test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            mve = null;
            smve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                smve = VocabListTest.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.MatrixType.INTEGER,
                        alpha, null, null, null);
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.MATRIX,
                        bravo, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                smve_id = addMatrixVEPrivate(db,smve);
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), pred(<charlie>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now get a copy of pve, modify it, and then call replacePredVE()
         * with the modified version.  Should succeed
         */
        if ( failures == 0 )
        {
            completed = false;
            delta = null;
            mod_pve = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                mod_pve = db.getPredVE(pve_id);
                mod_pve.setName("mod_pred");
                delta = new UnTypedFormalArg(db, "<delta>");
                mod_pve.appendFormalArg(delta);
                db.replacePredVE(mod_pve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( mod_pve == null ) ||
                 ( delta == null ) ||
                 ( mod_pve == db.vl.getVocabElement(mod_pve.getID()) ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "test 1 failed to complete.\n");
                    }

                    if ( mod_pve == null )
                    {
                        outStream.print(
                                "getPredVE(pve_id) returned null.\n");
                    }

                    if ( delta == null )
                    {
                        outStream.print("couldn't allocate delta.\n");
                    }

                    if ( mod_pve == db.vl.getVocabElement(mod_pve.getID()) )
                    {
                        outStream.print("replacement isn't a copy.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(2): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), " +
                    "mod_pred(<charlie>, <delta>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* Verify that replacePredVE(null) throws a system error.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                db.replacePredVE(null);
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
                        outStream.print("replacePredVE(null) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("replacePredVE(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }

            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), " +
                    "mod_pred(<charlie>, <delta>), " +
                    "s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestReplacePredVE() */

    /**
     * TestGetVocabElement()
     *
     * Test the getVocabElement() method.  Only cursory testing is needed, as
     * most functionality is provided via a call to vl.getVocabElement().
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestGetVocabElement(java.io.PrintStream outStream,
                                              boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing getVocabElement()                                        ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        PredicateVocabElement pve = null;
        VocabElement ve0 = null;
        VocabElement ve1 = null;
        VocabElement ve2 = null;
        VocabElement ve3 = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* setup for test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            mve = null;
            pve = null;
            mve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.INTEGER,
                        alpha, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", bravo, null,
                                                  null, null);
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( mve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test setup failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( bravo == null )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* Now run a cursory set of tests:
         *
         * getVocabElement(pve_id) and getVocabElement("pred") should both
         * return copies of pve, and getVocabElement(mve_id) and
         * getVocabElement("matrix") should both return copies of mve.
         */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            ve1 = null;
            ve2 = null;
            ve3 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = db.getVocabElement(pve_id);
                if ( ve0 == null )
                    outStream.print("it: ve0 == null\n");
                ve1 = db.getVocabElement("pred");
                ve2 = db.getVocabElement(mve_id);
                ve3 = db.getVocabElement("matrix");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( ve0 == null ) ||
                 ( ! ( ve0 instanceof PredicateVocabElement ) ) ||
                 ( ve0.getName().compareTo("pred") != 0 ) ||
                 ( ve0.getID() != pve_id ) ||
                 ( ve1 == null ) ||
                 ( ! ( ve1 instanceof PredicateVocabElement ) ) ||
                 ( ve1.getName().compareTo("pred") != 0 ) ||
                 ( ve1.getID() != pve_id ) ||
                 ( ve0 == ve1 ) ||
                 ( ve0 == db.vl.getVocabElement(pve_id) ) ||
                 ( ve1 == db.vl.getVocabElement("pred") ) ||
                 ( ve2 == null ) ||
                 ( ! ( ve2 instanceof MatrixVocabElement ) ) ||
                 ( ve2.getName().compareTo("matrix") != 0 ) ||
                 ( ve2.getID() != mve_id ) ||
                 ( ve3 == null ) ||
                 ( ! ( ve3 instanceof MatrixVocabElement ) ) ||
                 ( ve3.getName().compareTo("matrix") != 0 ) ||
                 ( ve3.getID() != mve_id ) ||
                 ( ve2 == ve3 ) ||
                 ( ve2 == db.vl.getVocabElement(mve_id) ) ||
                 ( ve3 == db.vl.getVocabElement("matrix") ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("getPredVE(valid) test failed " +
                                "to complete.\n");
                    }

                    if ( ( ve0 == null ) ||
                         ( ! ( ve0 instanceof PredicateVocabElement ) ) ||
                         ( ve0.getName().compareTo("pred") != 0 ) ||
                         ( ve0.getID() != pve_id ) ||
                         ( ve0 == db.vl.getVocabElement(pve_id) ) )
                    {
                        outStream.print("unexpected ve0.\n");
                    }

                    if ( ( ve1 == null ) ||
                         ( ! ( ve1 instanceof PredicateVocabElement ) ) ||
                         ( ve1.getName().compareTo("pred") != 0 ) ||
                         ( ve1.getID() != pve_id ) ||
                         ( ve1 == db.vl.getVocabElement("pred") ) )
                    {
                        outStream.print("unexpected ve1.\n");
                    }

                    if ( ve0 == ve1 )
                    {
                        outStream.print("ve0 == ve1.\n");
                    }


                    if ( ( ve2 == null ) ||
                         ( ! ( ve2 instanceof MatrixVocabElement ) ) ||
                         ( ve2.getName().compareTo("matrix") != 0 ) ||
                         ( ve2.getID() != mve_id ) ||
                         ( ve2 == db.vl.getVocabElement(mve_id) ) )
                    {
                        outStream.print("unexpected ve2.\n");
                    }

                    if ( ( ve3 == null ) ||
                         ( ! ( ve3 instanceof PredicateVocabElement ) ) ||
                         ( ve3.getName().compareTo("matrix") != 0 ) ||
                         ( ve3.getID() != mve_id ) ||
                         ( ve3 == db.vl.getVocabElement("matrix") ) )
                    {
                        outStream.print("unexpected ve3.\n");
                    }

                    if ( ve2 == ve3 )
                    {
                        outStream.print("ve2 == ve3.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("getPredVE(valid) threw " +
                                "unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the INVALID_ID to getVocabElement() -- should fail */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = db.getVocabElement(DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "getVocabElement(INVALID_ID) completed.\n");
                    }

                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("getVocabElement(INVALID_ID) failed " +
                                "to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* now try to pass the string "nonesuch" to getVocabElement() --
         * should fail
         */
        if ( failures == 0 )
        {
            completed = false;
            ve0 = null;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = db.getVocabElement("nonesuch");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( ve0 != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "getVocabElement(\"nonesuch\") completed.\n");
                    }

                    if ( ve0 != null )
                    {
                        outStream.print("ve0 != null(2).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("getPredVE(\"nonesuch\") failed " +
                                "to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (pred(<bravo>), matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(4): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestGetVocabElement() */


    /**
     * TestVocabElementExists()
     *
     * Test the vocabElementxists() method.  Only cursory testing is needed,
     * as the function just calls vl.inVocabList().
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - Modified test to use addSystemPredVE() when adding a system
     *      predicate.  This was made necessary by my tightening up on
     *      restrictions on who can create or modify a system predicate.
     * 
     *                                              JRM -- 7/26/09
     */

    public static boolean TestVocabElementExists(java.io.PrintStream outStream,
                                                 boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing vocabElementExists()                                     ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean completed = false;
        boolean veExists0 = true;
        boolean veExists1 = true;
        boolean veExists2 = true;
        boolean veExists3 = true;
        boolean veExists4 = true;
        boolean veExists5 = false;
        boolean veExists6 = false;
        boolean veExists7 = false;
        boolean veExists8 = false;
        boolean veExists9 = true;
        boolean veExists10 = false;
        boolean veExists11 = false;
        boolean veExists12 = false;
        boolean veExists13 = false;
        boolean veExists14 = true;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mve_id = DBIndex.INVALID_ID;
        long smve_id = DBIndex.INVALID_ID;
        long pve_id = DBIndex.INVALID_ID;
        long spve_id = DBIndex.INVALID_ID;
        Database db = null;
        MatrixVocabElement mve = null;
        MatrixVocabElement smve = null;
        PredicateVocabElement pve = null;
        PredicateVocabElement spve = null;
        IntFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* run a test with valid data */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            alpha = null;
            bravo = null;
            charlie = null;
            delta = null;
            mve = null;
            smve = null;
            pve = null;
            spve = null;
            mve_id = DBIndex.INVALID_ID;
            smve_id = DBIndex.INVALID_ID;
            pve_id = DBIndex.INVALID_ID;
            spve_id = DBIndex.INVALID_ID;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new UnTypedFormalArg(db, "<bravo>");
                charlie = new UnTypedFormalArg(db, "<charlie>");
                delta = new UnTypedFormalArg(db, "<delta>");
                smve = VocabListTest.ConstructTestMatrix(db, "s-matrix",
                        MatrixVocabElement.MatrixType.INTEGER,
                        alpha, null, null, null);
                mve = VocabListTest.ConstructTestMatrix(db, "matrix",
                        MatrixVocabElement.MatrixType.MATRIX,
                        bravo, null, null, null);
                pve = VocabListTest.ConstructTestPred(db, "pred", charlie, null,
                                                  null, null);
                spve = VocabListTest.ConstructTestPred(db, "s-pred", delta, null,
                                                  null, null);
                spve.setSystem();
                veExists0 = db.vocabElementExists("s-matrix");
                veExists1 = db.vocabElementExists("matrix");
                veExists2 = db.vocabElementExists("pred");
                veExists3 = db.vocabElementExists("s-pred");
                veExists4 = db.vocabElementExists("nonesuch");
                smve_id = addMatrixVEPrivate(db,smve);
                mve_id = addMatrixVEPrivate(db,mve);
                pve_id = db.addPredVE(pve);
                spve_id = db.addSystemPredVE(spve);
                veExists5 = db.vocabElementExists("s-matrix");
                veExists6 = db.vocabElementExists("matrix");
                veExists7 = db.vocabElementExists("pred");
                veExists8 = db.vocabElementExists("s-pred");
                veExists9 = db.vocabElementExists("nonesuch");
                veExists10 = db.vocabElementExists(smve_id);
                veExists11 = db.vocabElementExists(mve_id);
                veExists12 = db.vocabElementExists(pve_id);
                veExists13 = db.vocabElementExists(spve_id);
                veExists14 = db.vocabElementExists(1024);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( mve == null ) ||
                 ( smve == null ) ||
                 ( pve == null ) ||
                 ( mve_id == DBIndex.INVALID_ID ) ||
                 ( smve_id == DBIndex.INVALID_ID ) ||
                 ( pve_id == DBIndex.INVALID_ID ) ||
                 ( veExists0 != false ) ||
                 ( veExists1 != false ) ||
                 ( veExists2 != false ) ||
                 ( veExists3 != false ) ||
                 ( veExists4 != false ) ||
                 ( veExists5 != true ) ||
                 ( veExists6 != true ) ||
                 ( veExists7 != true ) ||
                 ( veExists8 != true ) ||
                 ( veExists9 != false ) ||
                 ( veExists10 != true ) ||
                 ( veExists11 != true ) ||
                 ( veExists12 != true ) ||
                 ( veExists13 != true ) ||
                 ( veExists14 != false ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( alpha == null )
                    {
                        outStream.print("new IntFormalArg() returned null.\n");
                    }

                    if ( ( bravo == null ) || ( charlie == null ) )
                    {
                        outStream.print("new UnTypedFormalArg() returned null.\n");
                    }

                    if ( mve == null )
                    {
                        outStream.print("couldn't construct mve.\n");
                    }

                    if ( smve == null )
                    {
                        outStream.print("couldn't construct smve.\n");
                    }

                    if ( pve == null )
                    {
                        outStream.print("couldn't construct pve.\n");
                    }

                    if ( mve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("mve_id == INVALID_ID.\n");
                    }

                    if ( smve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("smve_id == INVALID_ID.\n");
                    }

                    if ( pve_id == DBIndex.INVALID_ID )
                    {
                        outStream.print("pve_id == INVALID_ID.\n");
                    }

                    if ( ( veExists0 != false ) ||
                         ( veExists1 != false ) ||
                         ( veExists2 != false ) ||
                         ( veExists3 != false ) ||
                         ( veExists4 != false ) ||
                         ( veExists5 != true ) ||
                         ( veExists6 != true ) ||
                         ( veExists7 != true ) ||
                         ( veExists8 != true ) ||
                         ( veExists9 != false ) ||
                         ( veExists10 != true ) ||
                         ( veExists11 != true ) ||
                         ( veExists12 != true ) ||
                         ( veExists13 != true ) ||
                         ( veExists14 != false ) )
                    {
                        outStream.print("unexpected result(s) from " +
                                        "vocabElementExists().\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(1): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), s-pred(<delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(1): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        /* Verify that vocabElementExists("<<invalid") and
         * vocabElementExists(INVALID_ID) throw system errors.
         */
        if ( failures == 0 )
        {
            completed = false;
            veExists0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                veExists0 = db.vocabElementExists("<<invalid");
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( veExists0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print("vocabElementExists(\"<<invalid\") " +
                                        "completed.\n");
                    }

                    if ( veExists0 != true )
                    {
                        outStream.print("veExists0 != true(1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("vocabElementExists(\"<<invalid\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), s-pred(<delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(2): \"%s\"\n",
                            db.vl.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            completed = false;
            veExists0 = true;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                veExists0 = db.vocabElementExists(DBIndex.INVALID_ID);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( completed ) ||
                 ( veExists0 != true ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( completed )
                    {
                        outStream.print(
                                "vocabElementExists(INVALID_ID) completed.\n");
                    }

                    if ( veExists0 != true )
                    {
                        outStream.print("veExists0 != true(2).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("vocabElementExists(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
            else if ( db.vl.toString().compareTo("((VocabList) " +
                    "(vl_contents: (matrix(<bravo>), s-pred(<delta>), " +
                    "pred(<charlie>), s-matrix(<alpha>))))") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Uexpected vl string(3): \"%s\"\n",
                            db.vl.toString());
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

    } /* Database::TestVocabElementExists() */



    /**
     * AdHocTest()
     *
     * Run Felix's test.
     *
     *                                               -- 7/17/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean AdHocTest(java.io.PrintStream outStream,
                                    boolean verbose)
    throws SystemErrorException {
      // Create a database instance

      ODBCDatabase db = new ODBCDatabase();



      // Create a data column

      DataColumn column = new DataColumn(db, "TestColumn",
                                         MatrixVocabElement.MatrixType.TEXT);

      //Add it to the database

      db.addColumn(column);

      // Not sure why this is necessary, column fields not set otherwise, so
      //have to retrieve a db copy of the column

      // Felix:  The addColumn call assigns IDs and creates the initial mve.

      column = db.getDataColumn("TestColumn");



     //Get the matrix vocab element for the column

      MatrixVocabElement mve = db.getMatrixVE(column.getItsMveID());



      // Create some data cells and add them to the database

      DataCell[] cells = new DataCell[4];

      for (int i=0; i<cells.length; i++) {

        cells[i] = new DataCell(db, column.getID(), mve.getID());

        long cid = db.appendCell(cells[i]);

        cells[i] = (DataCell)db.getCell(cid);

        System.out.printf("Initial cell[%d] = %s.\n", i, cells[i].toString());
      }



      // Modify the cells' data

      for (int i=0; i<cells.length; i++) {

        Matrix m = new Matrix(db, mve.getID());

        TextStringDataValue tsdv = new TextStringDataValue(db);

        tsdv.setItsValue("Testing. This is some more data. " +

                         "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +

                         "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +

                         "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + i);

        m.replaceArg(0, tsdv);

        // Hi Felix,
        //
        // I expected you to just modify your copy and send it back to the
        // database, as below.  The Database should make its own copy of your
        // cell, possibly making some additional notations.
        //
        // without modifying it.
        //
        // See example below.  Note that I have commented out your old code
        // as appropriate.

        //DataCell dc = new DataCell(db, column.getID(), mve.getID());
        DataCell dc = cells[i];

        //dc.setID(cells[i].getID());

        dc.setVal(m);

        dc.setOnset(new TimeStamp(60, i*60));

        dc.setOffset(new TimeStamp(60, i*60 + 59));

        db.replaceCell(dc);

        cells[i] = (DataCell)db.getCell(dc.getID());

        System.out.printf("mod 1 cell[%d] = %s.\n", i, cells[i].toString());
      }



      // Modify the cells' data again

      for (int i=0; i<cells.length; i++) {

        Matrix m = new Matrix(db, mve.getID());

        TextStringDataValue tsdv = new TextStringDataValue(db);

        tsdv.setItsValue("Testing " + i);

        m.replaceArg(0, tsdv);

        DataCell dc = cells[i];


        dc.setVal(m);

        db.replaceCell(dc);

        cells[i] = (DataCell)db.getCell(dc.getID());

        System.out.printf("mod 2 cell[%d] = %s.\n", i, cells[i].toString());
      }


        return true;
    }


    /*************************************************************************/
    /*********************** Listener Test Code: *****************************/
    /*************************************************************************/

    /**
     * TestInternalListeners()
     *
     * Main routine for all test code testing internal propagation of changes
     * through the database via the internal listeners.
     *
     *                                           3/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestInternalListeners(java.io.PrintStream outStream,
                                                boolean verbose)
    throws SystemErrorException {
        boolean pass = true;
        int failures = 0;

        outStream.print("Testing Internal Listeners:\n");

        if ( ! TestPVEModListeners(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestPVEDeletionListeners(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestMVEModListeners(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestDataCellDeletionListeners(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestDataCellInsertionListeners(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestDataCellModListeners(outStream, verbose) )
        {
            failures++;
        }

        if ( failures > 0 )
        {
            pass = false;
            outStream.printf("%d failures in internal listener tests.\n\n",
                              failures);
        }
        else
        {
            outStream.print("All internal listener tests passed.\n\n");
        }

        return pass;

    } /* Database::TestInternalListeners() */


    /**
     * TestDataCellDeletionListeners()
     *
     * Verify that data cell deletions propogate through the database as
     * expected.
     *
     *                                               -- 3/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestDataCellDeletionListeners(
                                                java.io.PrintStream outStream,
                                                 boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing data cell deletion listeners                             ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // run tests here

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

    } /* Database::TestDataCellDeletionListeners() */


    /**
     * TestDataCellDeletionListeners()
     *
     * Verify that data cell deletions propogate through the database as
     * expected.
     *
     *                                               -- 3/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestDataCellInsertionListeners(
                                                java.io.PrintStream outStream,
                                                 boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing data cell insertion listeners                            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // run tests here

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

    } /* Database::TestDataCellInsertionListeners() */


    /**
     * TestDataCellModListeners()
     *
     * Verify that modifications to data cells propogate through
     * the database as expected.
     *
     *                                               -- 3/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestDataCellModListeners(java.io.PrintStream outStream,
                                                   boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing data cell modification listeners                         ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        // run tests here

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

    } /* Database::TestDataCellModListeners() */


    /**
     * TestMVEModListeners()
     *
     * Verify that modifications in matrix vocab elements propogate through
     * the database as expected.
     *
     *                                               -- 3/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestMVEModListeners(java.io.PrintStream outStream,
                                              boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing matrix vocab element modification listeners              ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        failures += TestMVEModListeners__test_01(outStream, verbose);

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

    } /* Database::TestMVEModListeners() */


    /**
     * TestPVEModListeners__test_01()
     *
     * Initial smoke check on the PVE mod listeners:
     *
     * Allocate a data base, and create several predicates and matrix data
     * columns.  Insert a selection of cells in the columns with various
     * predicate values.
     *
     * Add, & delete formal arguments in the matrix vocab elements associated
     * with the matrix data columns.  Verify that the changes are reflected
     * correctly in the cells.
     *
     * Re-arrange formal arguemnst and verify that the changes are reflected
     * correctly in the cells.
     *
     * Combine the above and verify the expected results.
     *
     * Return the number of failures.
     *
     *                                               -- 4/25/08
     *
     * Changes:
     *
     *    - None.
     */

    private static int TestMVEModListeners__test_01(
            java.io.PrintStream outStream,
            boolean verbose)
    throws SystemErrorException {
        final String header = "test 01: ";
        String systemErrorExceptionString = "";
        String expectedString0 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<val>), " +
                 "mdc3(<val>), " +
                 "mdc1(<val>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (\"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<val>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString1 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<val>, <arg1>), " +
                 "mdc3(<val>), " +
                 "mdc1(<val>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (\"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<val>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "<arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString2 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <val>, <arg1>), " +
                 "mdc3(<val>), " +
                 "mdc1(<val>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (\"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<val>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(<arg-1>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(<arg-1>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(<arg-1>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(<arg-1>, " +
                      "pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "<arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString3 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <arg0.5>, <val>, <arg1>), " +
                 "mdc3(<val>), " +
                 "mdc1(<val>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (\"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<val>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(<arg-1>, <arg0.5>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(<arg-1>, <arg0.5>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(<arg-1>, <arg0.5>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(<arg-1>, " +
                      "<arg0.5>, " +
                      "pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "<arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString4 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <arg0.5>, <val>, <arg1>), " +
                 "mdc3(<val>), " +
                 "mdc1(<val>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (\"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<val>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(<arg-1>, <arg0.5>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(<arg-1>, <arg0.5>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(<arg-1>, <arg0.5>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(<arg-1>, " +
                      "<arg0.5>, " +
                      "pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "<arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(pve0(alpha), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(alpha, bravo), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(alpha, bravo, charlie), " +
                      "0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "0.000000, 0, , (), \"\", 00:00:00:000, <arg7>))))))))";
        String expectedString5 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <arg0.5>, <val>, <arg1>), " +
                 "mdc3(<val>), " +
                 "mdc1(<val>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                     "(<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, " +
                     "(<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                     "(<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, <val>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(<arg-1>, <arg0.5>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(<arg-1>, <arg0.5>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(<arg-1>, <arg0.5>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(<arg-1>, " +
                      "<arg0.5>, " +
                      "pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "<arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(pve0(alpha), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(alpha, bravo), " +
                       "0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(alpha, bravo, charlie), " +
                       "0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)), " +
                      "0.000000, 0, , (), \"\", 00:00:00:000, <arg7>))))))))";
        String expectedString6 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <arg0.5>, <val>, <arg1>), " +
                 "mdc3(<val>), " +
                 "mdc1(<val>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, <val>)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg-1>, <arg0.5>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg-1>, <arg0.5>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg-1>, <arg0.5>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg-1>, <arg0.5>, pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), <arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, " +
                     "(pve0(alpha), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(alpha, bravo), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(alpha, bravo, charlie), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), " +
                      "0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                     "(11, 10.000000, 10, TEN, pve0(<arg0>), \"ten\", 00:01:00:000, 11.000000)), " +
                   "(6, 00:00:06:000, 00:00:07:000, " +
                     "(TWENTY-ONE, 20.000000, 20, TWENTY, " +
                      "pve1(<arg0>, <arg1>), " +
                      "\"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString7 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <arg0.5>, <val>, <arg1>), " +
                 "mdc3(<val>), " +
                 "mdc1(<val>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, <val>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.000000, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.000000)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.000000, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg-1>, <arg0.5>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg-1>, <arg0.5>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg-1>, <arg0.5>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg-1>, <arg0.5>, pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), <arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (11, 10.000000, 10, TEN, pve0(<arg0>), \"ten\", 00:01:00:000, 11.000000)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (TWENTY-ONE, 20.000000, 20, TWENTY, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString8 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<arg-1>, <val>, <arg1>), " +
                 "mdc3(<val>), " +
                 "mdc1(<val>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, <val>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.000000, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.000000)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.000000, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg-1>, pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg-1>, pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg-1>, pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg-1>, pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), <arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (11, 10.000000, 10, TEN, pve0(<arg0>), \"ten\", 00:01:00:000, 11.000000)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (TWENTY-ONE, 20.000000, 20, TWENTY, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString9 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<val>, <arg1>), " +
                 "mdc3(<val>), " +
                 "mdc1(<val>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, <val>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.000000, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.000000)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.000000, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1), <arg1>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2), <arg1>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3), <arg1>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), <arg1>)))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (11, 10.000000, 10, TEN, pve0(<arg0>), \"ten\", 00:01:00:000, 11.000000)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (TWENTY-ONE, 20.000000, 20, TWENTY, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString10 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<val>), " +
                 "mdc3(<val>), " +
                 "mdc1(<val>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, <val>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.000000, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.000000)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.000000, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), 0.000000, 0, , (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (11, 10.000000, 10, TEN, pve0(<arg0>), \"ten\", 00:01:00:000, 11.000000)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (TWENTY-ONE, 20.000000, 20, TWENTY, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString11 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<val>), " +
                 "mdc3(<val>), " +
                 "mdc1(<val>, <arg1>, <arg2>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, <val>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.000000, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.000000)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.000000, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha), 0.000000, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo), 0.000000, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie), 0.000000, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)), 0.000000, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (11, 10.000000, 10, pve0(<arg0>), \"ten\", 00:01:00:000, 11.000000)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (TWENTY-ONE, 20.000000, 20, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString12 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<val>), " +
                 "mdc3(<val>), " +
                 "mdc1(<arg1>, <arg2>, <arg4>, <arg5>, <arg6>, <arg7>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, <val>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.000000, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.000000)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.000000, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (0.000000, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (0.000000, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (0.000000, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (0.000000, 0, (), \"\", 00:00:00:000, <arg7>)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (10.000000, 10, pve0(<arg0>), \"ten\", 00:01:00:000, 11.000000)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (20.000000, 20, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000, \"twentry-one\"))))))))";
        String expectedString13 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<val>), " +
                 "mdc3(<val>), " +
                 "mdc1(<arg1>, <arg2>, <arg4>, <arg5>, <arg6>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, <val>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.000000, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.000000)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.000000, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (0.000000, 0, (), \"\", 00:00:00:000)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (0.000000, 0, (), \"\", 00:00:00:000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (0.000000, 0, (), \"\", 00:00:00:000)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (0.000000, 0, (), \"\", 00:00:00:000)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (10.000000, 10, pve0(<arg0>), \"ten\", 00:01:00:000)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (20.000000, 20, pve1(<arg0>, <arg1>), \"twenty\", 00:02:00:000))))))))";
        String expectedString14 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<val>), " +
                 "mdc3(<val>), " +
                 "mdc1(<arg4>, <arg1>, <arg2>, <arg5>, <arg6>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, <val>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.000000, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.000000)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.000000, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, ((), 0.000000, 0, \"\", 00:00:00:000)), " +
                   "(2, 00:00:01:000, 00:00:02:000, ((), 0.000000, 0, \"\", 00:00:00:000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, ((), 0.000000, 0, \"\", 00:00:00:000)), " +
                   "(4, 00:00:03:000, 00:00:04:000, ((), 0.000000, 0, \"\", 00:00:00:000)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (pve0(<arg0>), 10.000000, 10, \"ten\", 00:01:00:000)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (pve1(<arg0>, <arg1>), 20.000000, 20, \"twenty\", 00:02:00:000))))))))";
        String expectedString15 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<val>), " +
                 "mdc3(<val>), " +
                 "mdc1(<arg6>, <arg5>, <arg2>, <arg4>, <arg1>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg0>, <arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <val>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 1)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 2.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, THREE)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, pve0(4))), " +
                   "(5, 00:00:04:000, 00:00:05:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, \"five\")), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, 00:01:00:000)), " +
                   "(7, 00:00:06:000, 00:00:07:000, (<arg0>, 0.000000, 0, , (), \"\", 00:00:00:000, <val>)), " +
                   "(8, 00:00:04:000, 00:00:05:000, (110, 100.000000, 100, HUNDRED, pve0(<arg0>), \"hundred\", 00:10:00:000, 110.000000)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (TWO-HUNDRED-ONE, 200.000000, 200, TWO-HUNDRED, pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, \"two-hundred-one\")))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (00:00:00:000, \"\", 0, (), 0.000000)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (00:00:00:000, \"\", 0, (), 0.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (00:00:00:000, \"\", 0, (), 0.000000)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (00:00:00:000, \"\", 0, (), 0.000000)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (00:01:00:000, \"ten\", 10, pve0(<arg0>), 10.000000)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (00:02:00:000, \"twenty\", 20, pve1(<arg0>, <arg1>), 20.000000))))))))";
        String expectedString16 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc0(<val>), " +
                 "mdc3(<val>), " +
                 "mdc1(<arg6>, <arg5>, <arg2>, <arg4>, <arg1>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "mdc2(<arg1>, <arg2>, <new_arg>, <arg4>, <arg5>, <arg6>, <arg3>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc2, " +
                  "((1, 00:00:00:000, 00:00:01:000, (0.000000, 0, , (), \"\", 00:00:00:000, )), " +
                   "(2, 00:00:01:000, 00:00:02:000, (0.000000, 0, , (), \"\", 00:00:00:000, )), " +
                   "(3, 00:00:02:000, 00:00:03:000, (0.000000, 0, , (), \"\", 00:00:00:000, )), " +
                   "(4, 00:00:03:000, 00:00:04:000, (0.000000, 0, , (), \"\", 00:00:00:000, )), " +
                   "(5, 00:00:04:000, 00:00:05:000, (0.000000, 0, , (), \"\", 00:00:00:000, )), " +
                   "(6, 00:00:05:000, 00:00:06:000, (0.000000, 0, , (), \"\", 00:00:00:000, )), " +
                   "(7, 00:00:06:000, 00:00:07:000, (0.000000, 0, , (), \"\", 00:00:00:000, )), " +
                   "(8, 00:00:04:000, 00:00:05:000, (100.000000, 100, , pve0(<arg0>), \"hundred\", 00:10:00:000, HUNDRED)), " +
                   "(9, 00:00:06:000, 00:00:07:000, (200.000000, 200, , pve1(<arg0>, <arg1>), \"two-hundred\", 00:20:00:000, TWO-HUNDRED)))), " +
                "(mdc0, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), pve1(<arg0>, <arg1>), pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(mdc3, ()), " +
                "(mdc1, " +
                  "((1, 00:00:00:000, 00:00:01:000, (00:00:00:000, \"\", 0, (), 0.000000)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (00:00:00:000, \"\", 0, (), 0.000000)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (00:00:00:000, \"\", 0, (), 0.000000)), " +
                   "(4, 00:00:03:000, 00:00:04:000, (00:00:00:000, \"\", 0, (), 0.000000)), " +
                   "(5, 00:00:04:000, 00:00:05:000, (00:01:00:000, \"ten\", 10, pve0(<arg0>), 10.000000)), " +
                   "(6, 00:00:06:000, 00:00:07:000, (00:02:00:000, \"twenty\", 20, pve1(<arg0>, <arg1>), 20.000000))))))))";
        String testStringA = null;
        String testStringB = null;
        String testStringC = null;
        boolean completed;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mdc0ID = DBIndex.INVALID_ID;
        long mdc1ID = DBIndex.INVALID_ID;
        long mdc2ID = DBIndex.INVALID_ID;
        long mdc3ID = DBIndex.INVALID_ID;
        long mdc0_mveID = DBIndex.INVALID_ID;
        long mdc1_mveID = DBIndex.INVALID_ID;
        long mdc2_mveID = DBIndex.INVALID_ID;
        long mdc3_mveID = DBIndex.INVALID_ID;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        Database db = null;
        DataColumn mdc0 = null;
        DataColumn mdc1 = null;
        DataColumn mdc2 = null;
        DataColumn mdc3 = null;
        MatrixVocabElement mve0 = null;
        MatrixVocabElement mve1 = null;
        MatrixVocabElement mve2 = null;
        MatrixVocabElement mve3 = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        FormalArgument farg = null;
        DataCell m_cell0 = null;
        DataCell p_cell0 = null;

        /* setup test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;

            try
            {
                // allocate a new database

                db = new ODBCDatabase();


                // create a selection of predicates

                pve0 = new PredicateVocabElement(db, "pve0");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve0.appendFormalArg(farg);
                pve0ID = db.addPredVE(pve0);
                pve0 = db.getPredVE(pve0ID);

                pve1 = new PredicateVocabElement(db, "pve1");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve1.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve1.appendFormalArg(farg);
                pve1ID = db.addPredVE(pve1);
                pve1 = db.getPredVE(pve1ID);

                pve2 = new PredicateVocabElement(db, "pve2");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve2.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve2.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg2>");
                pve2.appendFormalArg(farg);
                pve2ID = db.addPredVE(pve2);
                pve2 = db.getPredVE(pve2ID);


                // create Data columns

                mdc0 = new DataColumn(db, "mdc0",
                                     MatrixVocabElement.MatrixType.MATRIX);
                mdc0ID = db.addColumn(mdc0);
                mdc0 = db.getDataColumn(mdc0ID);
                mdc0_mveID = mdc0.getItsMveID();
                mve0 = db.getMatrixVE(mdc0_mveID);

                mdc1 = new DataColumn(db, "mdc1",
                                     MatrixVocabElement.MatrixType.MATRIX);
                mdc1ID = db.addColumn(mdc1);
                mdc1 = db.getDataColumn(mdc1ID);
                mdc1_mveID = mdc1.getItsMveID();
                mve1 = db.getMatrixVE(mdc1_mveID);

                mdc2 = new DataColumn(db, "mdc2",
                                     MatrixVocabElement.MatrixType.MATRIX);
                mdc2ID = db.addColumn(mdc2);
                mdc2 = db.getDataColumn(mdc2ID);
                mdc2_mveID = mdc2.getItsMveID();
                mve2 = db.getMatrixVE(mdc2_mveID);

                mdc3 = new DataColumn(db, "mdc3",
                                     MatrixVocabElement.MatrixType.MATRIX);
                mdc3ID = db.addColumn(mdc3);
                mdc3 = db.getDataColumn(mdc3ID);
                mdc3_mveID = mdc3.getItsMveID();
                mve3 = db.getMatrixVE(mdc3_mveID);


                // create a selection of cells

                // cells for mdc0
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc0ID,
                        mdc0_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            mdc0_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    IntDataValue.Construct(db, 1))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc0ID,
                        mdc0_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            mdc0_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    IntDataValue.Construct(db, 1),
                                    IntDataValue.Construct(db, 2))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc0ID,
                        mdc0_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            mdc0_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    IntDataValue.Construct(db, 1),
                                    IntDataValue.Construct(db, 2),
                                    IntDataValue.Construct(db, 3))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc0ID,
                        mdc0_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            mdc0_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            null,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            null,
                                            null,
                                            null)))))));


                // cells for mdc1
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc1ID,
                        mdc1_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            mdc1_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    NominalDataValue.Construct(db, "alpha"))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc1ID,
                        mdc1_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            mdc1_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    NominalDataValue.Construct(db, "alpha"),
                                    NominalDataValue.Construct(db, "bravo"))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc1ID,
                        mdc1_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            mdc1_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    NominalDataValue.Construct(db, "alpha"),
                                    NominalDataValue.Construct(db, "bravo"),
                                    NominalDataValue.Construct(db, "charlie"))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc1ID,
                        mdc1_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            mdc1_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            null,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            null,
                                            null,
                                            null)))))));


                // cells for mdc2
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc2ID,
                        mdc2_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            IntDataValue.Construct(db, 1))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc2ID,
                        mdc2_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            FloatDataValue.Construct(db, 2.0))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc2ID,
                        mdc2_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            NominalDataValue.Construct(db, "THREE"))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc2ID,
                        mdc2_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    IntDataValue.Construct(db, 4))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc2ID,
                        mdc2_mveID,
                        240,
                        300,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            QuoteStringDataValue.Construct(db, "five"))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc2ID,
                        mdc2_mveID,
                        300,
                        360,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            TimeStampDataValue.Construct(db, 3600))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc2ID,
                        mdc2_mveID,
                        360,
                        420,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            new UndefinedDataValue(db))));


                // cells for mdc3 -- none or now


                // create the test string
                testStringA = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( expectedString0.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test setup failed to complete.\n",
                                         header);
                    }

                    if ( db == null )
                    {
                        outStream.printf(
                                "%s new ODBCDatabase() returned null.\n",
                                header);
                    }

                    if ( expectedString0.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString0.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test setup: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        /* try adding some arguments -- all untyped for now */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;

            try
            {
                mve0.appendFormalArg(new UnTypedFormalArg(db, "<arg1>"));
                db.replaceMatrixVE(mve0);
                mve0 = db.getMatrixVE(mdc0_mveID);

                // create the test string
                testStringA = db.toString();

                mve0.insertFormalArg(new UnTypedFormalArg(db, "<arg-1>"), 0);
                db.replaceMatrixVE(mve0);
                mve0 = db.getMatrixVE(mdc0_mveID);

                // create the test string
                testStringB = db.toString();

                mve0.insertFormalArg(new UnTypedFormalArg(db, "<arg0.5>"), 1);
                db.replaceMatrixVE(mve0);
                mve0 = db.getMatrixVE(mdc0_mveID);

                testStringC = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( expectedString1.compareTo(testStringA) != 0 ) ||
                 ( expectedString2.compareTo(testStringB) != 0 ) ||
                 ( expectedString3.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 1 failed to complete.\n",
                                         header);
                    }

                    if ( expectedString1.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString1.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }

                    if ( expectedString2.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString2.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }

                    if ( expectedString3.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString3.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 1: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        /* add some more arguments -- this time typed. */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            completed = false;
            threwSystemErrorException = false;

            try
            {
                mve1.appendFormalArg(new FloatFormalArg(db, "<arg1>"));
                mve1.appendFormalArg(new IntFormalArg(db, "<arg2>"));
                mve1.appendFormalArg(new NominalFormalArg(db, "<arg3>"));
                mve1.appendFormalArg(new PredFormalArg(db, "<arg4>"));
                mve1.appendFormalArg(new QuoteStringFormalArg(db, "<arg5>"));
                mve1.appendFormalArg(new TimeStampFormalArg(db, "<arg6>"));
                mve1.appendFormalArg(new UnTypedFormalArg(db, "<arg7>"));
                db.replaceMatrixVE(mve1);
                mve1 = db.getMatrixVE(mdc1_mveID);

                // create the test string
                testStringA = db.toString();


                mve2.insertFormalArg(new UnTypedFormalArg(db, "<arg0>"), 0);
                mve2.insertFormalArg(new FloatFormalArg(db, "<arg1>"), 1);
                mve2.insertFormalArg(new IntFormalArg(db, "<arg2>"), 2);
                mve2.insertFormalArg(new NominalFormalArg(db, "<arg3>"), 3);
                mve2.insertFormalArg(new PredFormalArg(db, "<arg4>"), 4);
                mve2.insertFormalArg(new QuoteStringFormalArg(db, "<arg5>"), 5);
                mve2.insertFormalArg(new TimeStampFormalArg(db, "<arg6>"), 6);

                db.replaceMatrixVE(mve2);
                mve2 = db.getMatrixVE(mdc2_mveID);

                // create the test string
                testStringB = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( expectedString4.compareTo(testStringA) != 0 ) ||
                 ( expectedString5.compareTo(testStringB) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 2 failed to complete.\n",
                                         header);
                    }

                    if ( expectedString4.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString4.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }

                    if ( expectedString5.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString5.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 2: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        /* In preparation for further tests, create some new cells and
         * insert them in the data columns.
         */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            completed = false;
            threwSystemErrorException = false;

            try
            {
                /* add some cells to mdc1 */
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc1ID,
                        mdc1_mveID,
                        240,
                        300,
                        Matrix.Construct(
                            db,
                            mdc1_mveID,
                            IntDataValue.Construct(db, 11),
                            FloatDataValue.Construct(db, 10.0),
                            IntDataValue.Construct(db, 10),
                            NominalDataValue.Construct(db, "TEN"),
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    null)),
                            QuoteStringDataValue.Construct(db, "ten"),
                            TimeStampDataValue.Construct(db, 3600),
                            FloatDataValue.Construct(db, 11.0))));

                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc1ID,
                        mdc1_mveID,
                        360,
                        420,
                        Matrix.Construct(
                            db,
                            mdc1_mveID,
                            NominalDataValue.Construct(db, "TWENTY-ONE"),
                            FloatDataValue.Construct(db, 20.0),
                            IntDataValue.Construct(db, 20),
                            NominalDataValue.Construct(db, "TWENTY"),
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    null,
                                    null)),
                            QuoteStringDataValue.Construct(db, "twenty"),
                            TimeStampDataValue.Construct(db, 7200),
                            QuoteStringDataValue.Construct(db, "twentry-one"))));

                // create the test string
                testStringA = db.toString();


                /* add some cells to mdc2 */
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc2ID,
                        mdc2_mveID,
                        240,
                        300,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            IntDataValue.Construct(db, 110),
                            FloatDataValue.Construct(db, 100.0),
                            IntDataValue.Construct(db, 100),
                            NominalDataValue.Construct(db, "HUNDRED"),
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    null)),
                            QuoteStringDataValue.Construct(db, "hundred"),
                            TimeStampDataValue.Construct(db, 36000),
                            FloatDataValue.Construct(db, 110.0))));

                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdc2ID,
                        mdc2_mveID,
                        360,
                        420,
                        Matrix.Construct(
                            db,
                            mdc2_mveID,
                            NominalDataValue.Construct(db, "TWO-HUNDRED-ONE"),
                            FloatDataValue.Construct(db, 200.0),
                            IntDataValue.Construct(db, 200),
                            NominalDataValue.Construct(db, "TWO-HUNDRED"),
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    null,
                                    null)),
                            QuoteStringDataValue.Construct(db, "two-hundred"),
                            TimeStampDataValue.Construct(db, 72000),
                            QuoteStringDataValue.Construct(db, "two-hundred-one"))));

                // create the test string
                testStringB = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( expectedString6.compareTo(testStringA) != 0 ) ||
                 ( expectedString7.compareTo(testStringB) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 3 failed to complete.\n",
                                         header);
                    }

                    if ( expectedString6.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString6.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }

                    if ( expectedString7.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString7.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 3: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        /* try deleting some arguments -- all untyped and all unset */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;

            try
            {
                mve0.deleteFormalArg(1);
                db.replaceMatrixVE(mve0);
                mve0 = db.getMatrixVE(mdc0_mveID);

                // create the test string
                testStringA = db.toString();

                mve0.deleteFormalArg(0);
                db.replaceMatrixVE(mve0);
                mve0 = db.getMatrixVE(mdc0_mveID);

                // create the test string
                testStringB = db.toString();

                mve0.deleteFormalArg(1);
                db.replaceMatrixVE(mve0);
                mve0 = db.getMatrixVE(mdc0_mveID);

                // create the test string
                testStringC = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( expectedString8.compareTo(testStringA) != 0 ) ||
                 ( expectedString9.compareTo(testStringB) != 0 ) ||
                 ( expectedString10.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 4 failed to complete.\n",
                                         header);
                    }

                    if ( expectedString8.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString8.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }

                    if ( expectedString9.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString9.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }

                    if ( expectedString10.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString10.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 4: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        /* try deleting more arguments -- all typed and/or set */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;

            try
            {
                mve1.deleteFormalArg(3);
                db.replaceMatrixVE(mve1);
                mve1 = db.getMatrixVE(mdc1_mveID);

                // create the test string
                testStringA = db.toString();

                mve1.deleteFormalArg(0);
                db.replaceMatrixVE(mve1);
                mve1 = db.getMatrixVE(mdc1_mveID);

                // create the test string
                testStringB = db.toString();

                mve1.deleteFormalArg(5);
                db.replaceMatrixVE(mve1);
                mve1 = db.getMatrixVE(mdc1_mveID);

                // create the test string
                testStringC = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( expectedString11.compareTo(testStringA) != 0 ) ||
                 ( expectedString12.compareTo(testStringB) != 0 ) ||
                 ( expectedString13.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 5 failed to complete.\n",
                                         header);
                    }

                    if ( expectedString11.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString11.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }

                    if ( expectedString12.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString12.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }

                    if ( expectedString13.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString13.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 5: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        /* try moving some arguments around */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;

            try
            {
                farg = mve1.getFormalArg(2);
                mve1.deleteFormalArg(2);
                mve1.insertFormalArg(farg, 0);
                db.replaceMatrixVE(mve1);
                mve1 = db.getMatrixVE(mdc1_mveID);

                // create the test string
                testStringA = db.toString();


                farg = mve1.getFormalArg(4);
                mve1.deleteFormalArg(4);
                mve1.insertFormalArg(farg, 0);
                farg = mve1.getFormalArg(4);
                mve1.deleteFormalArg(4);
                mve1.insertFormalArg(farg, 1);
                farg = mve1.getFormalArg(4);
                mve1.deleteFormalArg(4);
                mve1.insertFormalArg(farg, 2);
                db.replaceMatrixVE(mve1);
                mve1 = db.getMatrixVE(mdc1_mveID);

                // create the test string
                testStringB = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( expectedString14.compareTo(testStringA) != 0 ) ||
                 ( expectedString15.compareTo(testStringB) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 6 failed to complete.\n",
                                         header);
                    }

                    if ( expectedString14.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString14.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }

                    if ( expectedString15.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString15.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 6: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        /* finally, mix things up a bit and see if we get confused */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;

            try
            {
                /* move arg 3 to the end of the argument list */
                farg = mve2.getFormalArg(3);
                mve2.deleteFormalArg(3);
                mve2.insertFormalArg(farg, 7);

                /* insert an argument into the list */
                mve2.insertFormalArg(new NominalFormalArg(db, "<new_arg>"), 3);

                /* delete the old first & final arguments in the matrix */
                mve2.deleteFormalArg(7);
                mve2.deleteFormalArg(0);

                // apply the modified version to the db
                db.replaceMatrixVE(mve2);
                mve2 = db.getMatrixVE(mdc2_mveID);

                // create the test string
                testStringA = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( expectedString16.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 7 failed to complete.\n",
                                         header);
                    }

                    if ( expectedString16.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString16.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 7: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }

        return failures;

    } /* Datavase::TestMVEModListeners__test_01() */


    /**
     * TestPVEDeletionListeners()
     *
     * Verify that deletions of predicate vocab elements propagate through
     * the database as expected.
     *
     *                                               -- 3/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestPVEDeletionListeners(java.io.PrintStream outStream,
                                                     boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing predicate vocab element deletion listeners               ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }


        failures += TestPVEDeletionListeners__test_01(outStream, verbose);

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

    } /* Database::TestPVEDeletionListeners() */


    /**
     * TestPVEDeletionListeners__test_01()
     *
     * Initial smoke check on the PVE deletion listeners:
     *
     * Allocate a data base, create a predicate column and a matrix column,
     * several predicates, and a selection of cells in the column with various
     * predicate values.
     *
     * Delete several predicates to see if the deletions are reflected
     * correctly in the cells.
     *
     * Return the number of failures.
     *
     *                                               -- 4/18/08
     *
     * Changes:
     *
     *    - None.
     */

    private static int TestPVEDeletionListeners__test_01(
            java.io.PrintStream outStream,
            boolean verbose)
    throws SystemErrorException {
        final String header = "test 01: ";
        String systemErrorExceptionString = null;
        String expectedString0 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pve3(<arg0>, <arg1>, <arg2>, <arg4>), " +
                 "pdc(<val>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve4(<arg0>, <arg1>, <arg2>, <arg4>, <arg5>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                     "(pve4(pve0(1.000000), " +
                           "pve1(2.000000, 2), " +
                           "pve2(3.000000, 3, THREE), " +
                           "pve3(4.000000, 4, FOUR, \"quarte\"), " +
                           "\"quint\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, " +
                     "(pve0(pve0(pve1(pve0(<arg0>), " +
                           "pve1(pve0(<arg0>), <arg1>)))))), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                     "(pve2(pve0(pve1(pve0(<arg0>), " +
                                "pve1(pve0(<arg0>), " +
                                "<arg1>))), " +
                           "pve1(pve0(pve1(pve0(<arg0>), " +
                                     "pve1(pve0(<arg0>), <arg1>))), " +
                                "pve1(pve0(<arg0>), <arg1>)), " +
                           "\"septime\"))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                     "(pve4(pve0(10.000000), " +
                           "pve1(20.000000, 20), " +
                           "pve2(30.000000, 30, THIRTY), " +
                           "pve3(40.000000, 40, FOURTY, \"forty\"), " +
                           "\"fifty\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, " +
                     "(pve0(pve0(pve1(pve0(<arg0>), pve1(pve0(<arg0>), <arg1>)))))), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                     "(pve2(pve0(pve1(pve0(<arg0>), pve1(pve0(<arg0>), <arg1>))), " +
                           "pve1(pve0(pve1(pve0(<arg0>), pve1(pve0(<arg0>), <arg1>))), " +
                                "pve1(pve0(<arg0>), <arg1>)), " +
                           "\"seventy\")))))))))";
        String expectedString1 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pve3(<arg0>, <arg1>, <arg2>, <arg4>), " +
                 "pdc(<val>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve4(<arg0>, <arg1>, <arg2>, <arg4>, <arg5>), " +
                 "pve1(<arg0>, <arg1>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<val>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(<arg0>, " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                    "(pve4(<arg0>, " +
                          "pve1(2.000000, 2), " +
                          "pve2(3.000000, 3, THREE), " +
                          "pve3(4.000000, 4, FOUR, \"quarte\"), " +
                          "\"quint\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<val>)), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                    "(pve2(<arg0>, " +
                          "pve1(<arg0>, pve1(<arg0>, <arg1>)), " +
                          "\"septime\"))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (())), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                    "(pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(<arg0>, " +
                          "pve1(<arg0>, <arg1>), " +
                          "pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                    "(pve4(<arg0>, " +
                          "pve1(20.000000, 20), " +
                          "pve2(30.000000, 30, THIRTY), " +
                          "pve3(40.000000, 40, FOURTY, \"forty\"), " +
                          "\"fifty\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, (())), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                    "(pve2(<arg0>, " +
                          "pve1(<arg0>, pve1(<arg0>, <arg1>)), " +
                          "\"seventy\")))))))))";
        String expectedString2 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pve3(<arg0>, <arg1>, <arg2>, <arg4>), " +
                 "pdc(<val>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve4(<arg0>, <arg1>, <arg2>, <arg4>, <arg5>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<val>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<val>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(<arg0>, <arg1>, pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                    "(pve4(<arg0>, " +
                          "<arg1>, " +
                          "pve2(3.000000, 3, THREE), " +
                          "pve3(4.000000, 4, FOUR, \"quarte\"), " +
                          "\"quint\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<val>)), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                    "(pve2(<arg0>, <arg1>, \"septime\"))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (())), " +
                   "(2, 00:00:01:000, 00:00:02:000, (())), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                    "(pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(<arg0>, <arg1>, pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                    "(pve4(<arg0>, " +
                          "<arg1>, " +
                          "pve2(30.000000, 30, THIRTY), " +
                          "pve3(40.000000, 40, FOURTY, \"forty\"), " +
                          "\"fifty\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, (())), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                    "(pve2(<arg0>, <arg1>, \"seventy\")))))))))";
        String expectedString3 =
          "(Undefined " +
            "((VocabList) " +
                "(vl_contents: " +
                  "(mdc(<val>), " +
                   "pdc(<val>), " +
                   "pve2(<arg0>, <arg1>, <arg2>), " +
                   "pve4(<arg0>, <arg1>, <arg2>, <arg4>, <arg5>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (<val>)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (<val>)), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(<arg0>, <arg1>, pve2(<arg0>, <arg1>, <arg2>)))), " +
                   "(5, 00:00:04:000, 00:00:05:000, " +
                    "(pve4(<arg0>, " +
                          "<arg1>, " +
                          "pve2(3.000000, 3, THREE), " +
                          "<arg4>, " +
                          "\"quint\"))), " +
                   "(6, 00:00:05:000, 00:00:06:000, (<val>)), " +
                   "(7, 00:00:06:000, 00:00:07:000, " +
                    "(pve2(<arg0>, <arg1>, \"septime\"))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (())), " +
                  "(2, 00:00:01:000, 00:00:02:000, (())), " +
                  "(3, 00:00:02:000, 00:00:03:000, " +
                    "(pve2(alpha, bravo, charlie))), " +
                  "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(<arg0>, <arg1>, pve2(<arg0>, <arg1>, <arg2>)))), " +
                  "(5, 00:00:04:000, 00:00:05:000, " +
                    "(pve4(<arg0>, " +
                          "<arg1>, " +
                          "pve2(30.000000, 30, THIRTY), " +
                          "<arg4>, " +
                         "\"fifty\"))), " +
                  "(6, 00:00:05:000, 00:00:06:000, (())), " +
                  "(7, 00:00:06:000, 00:00:07:000, " +
                   "(pve2(<arg0>, <arg1>, \"seventy\")))))))))";
        String testStringA = null;
        String testStringB = null;
        String testStringC = null;
        boolean completed;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mdcID = DBIndex.INVALID_ID;
        long mdc_mveID = DBIndex.INVALID_ID;
        long pdcID = DBIndex.INVALID_ID;
        long pdc_mveID = DBIndex.INVALID_ID;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        long pve3ID = DBIndex.INVALID_ID;
        long pve4ID = DBIndex.INVALID_ID;
        Database db = null;
        DataColumn mdc = null;
        DataColumn pdc = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        PredicateVocabElement pve3 = null;
        PredicateVocabElement pve4 = null;
        FormalArgument farg = null;
        DataCell m_cell0 = null;
        DataCell p_cell0 = null;

        /* setup test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;

            try
            {
                // allocate a new database

                db = new ODBCDatabase();


                // create a selection of predicates

                pve0 = new PredicateVocabElement(db, "pve0");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve0.appendFormalArg(farg);
                pve0ID = db.addPredVE(pve0);
                pve0 = db.getPredVE(pve0ID);

                pve1 = new PredicateVocabElement(db, "pve1");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve1.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve1.appendFormalArg(farg);
                pve1ID = db.addPredVE(pve1);
                pve1 = db.getPredVE(pve1ID);

                pve2 = new PredicateVocabElement(db, "pve2");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve2.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve2.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg2>");
                pve2.appendFormalArg(farg);
                pve2ID = db.addPredVE(pve2);
                pve2 = db.getPredVE(pve2ID);

                pve3 = new PredicateVocabElement(db, "pve3");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve3.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve3.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg2>");
                pve3.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg4>");
                pve3.appendFormalArg(farg);
                pve3ID = db.addPredVE(pve3);
                pve3 = db.getPredVE(pve3ID);

                pve4 = new PredicateVocabElement(db, "pve4");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve4.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve4.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg2>");
                pve4.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg4>");
                pve4.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg5>");
                pve4.appendFormalArg(farg);
                pve4ID = db.addPredVE(pve4);
                pve4 = db.getPredVE(pve4ID);


                // create a couple of Data columns

                mdc = new DataColumn(db, "mdc",
                                     MatrixVocabElement.MatrixType.MATRIX);
                mdcID = db.addColumn(mdc);
                mdc = db.getDataColumn(mdcID);
                mdc_mveID = mdc.getItsMveID();

                pdc = new DataColumn(db, "pdc",
                                     MatrixVocabElement.MatrixType.PREDICATE);
                pdcID = db.addColumn(pdc);
                pdc = db.getDataColumn(pdcID);
                pdc_mveID = pdc.getItsMveID();


                // create a selection of cells
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdcID,
                        mdc_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    IntDataValue.Construct(db, 1))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdcID,
                        mdc_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    IntDataValue.Construct(db, 1),
                                    IntDataValue.Construct(db, 2))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdcID,
                        mdc_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    IntDataValue.Construct(db, 1),
                                    IntDataValue.Construct(db, 2),
                                    IntDataValue.Construct(db, 3))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdcID,
                        mdc_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            null,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            null,
                                            null,
                                            null)))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdcID,
                        mdc_mveID,
                        240,
                        300,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve4ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            FloatDataValue.Construct(db, 1.0))),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            FloatDataValue.Construct(db, 2.0),
                                            IntDataValue.Construct(db, 2))),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            FloatDataValue.Construct(db, 3.0),
                                            IntDataValue.Construct(db, 3),
                                            NominalDataValue.Construct(
                                                db,
                                                "THREE"))),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve3ID,
                                            FloatDataValue.Construct(db, 4.0),
                                            IntDataValue.Construct(db, 4),
                                            NominalDataValue.Construct(
                                                db,
                                                "FOUR"),
                                            QuoteStringDataValue.Construct(
                                                db, "quarte"))),
                                    QuoteStringDataValue.Construct(
                                        db, "quint"))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdcID,
                        mdc_mveID,
                        300,
                        360,
                        Matrix.Construct(
                          db,
                          mdc_mveID,
                          PredDataValue.Construct(
                            db,
                            Predicate.Construct(
                              db,
                              pve0ID,
                              PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                  db,
                                  pve0ID,
                                  PredDataValue.Construct(
                                    db,
                                    Predicate.Construct(
                                      db,
                                      pve1ID,
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve0ID,
                                          null)),
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve1ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                          null)))))))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdcID,
                        mdc_mveID,
                        360,
                        420,
                        Matrix.Construct(
                          db,
                          mdc_mveID,
                          PredDataValue.Construct(
                            db,
                            Predicate.Construct(
                              db,
                              pve2ID,
                              PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                  db,
                                  pve0ID,
                                  PredDataValue.Construct(
                                    db,
                                    Predicate.Construct(
                                      db,
                                      pve1ID,
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve0ID,
                                          null)),
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve1ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                          null)))))),
                                  PredDataValue.Construct(
                                    db,
                                    Predicate.Construct(
                                      db,
                                      pve1ID,
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve0ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                              db,
                                              pve1ID,
                                              PredDataValue.Construct(
                                                db,
                                                Predicate.Construct(
                                                  db,
                                                  pve0ID,
                                                  null)),
                                              PredDataValue.Construct(
                                                db,
                                                Predicate.Construct(
                                                  db,
                                                  pve1ID,
                                                 PredDataValue.Construct(
                                                    db,
                                                    Predicate.Construct(
                                                    db,
                                                    pve0ID,
                                                    null)),
                                                  null)))))),
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve1ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                          null)))),
                                  QuoteStringDataValue.Construct(
                                    db, "septime"))))));

                db.appendCell(
                    DataCell.Construct(
                        db,
                        pdcID,
                        pdc_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    NominalDataValue.Construct(db, "alpha"))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        pdcID,
                        pdc_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    NominalDataValue.Construct(db, "alpha"),
                                    NominalDataValue.Construct(db, "bravo"))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        pdcID,
                        pdc_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    NominalDataValue.Construct(db, "alpha"),
                                    NominalDataValue.Construct(db, "bravo"),
                                    NominalDataValue.Construct(db, "charlie"))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        pdcID,
                        pdc_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            null,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            null,
                                            null,
                                            null)))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        pdcID,
                        pdc_mveID,
                        240,
                        300,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve4ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            FloatDataValue.Construct(
                                                db,
                                                10.0))),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            FloatDataValue.Construct(db, 20.0),
                                            IntDataValue.Construct(db, 20))),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            FloatDataValue.Construct(db, 30.0),
                                            IntDataValue.Construct(db, 30),
                                            NominalDataValue.Construct(
                                                db,
                                                "THIRTY"))),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve3ID,
                                            FloatDataValue.Construct(db, 40.0),
                                            IntDataValue.Construct(db, 40),
                                            NominalDataValue.Construct(
                                                db,
                                                "FOURTY"),
                                            QuoteStringDataValue.Construct(
                                                db, "forty"))),
                                    QuoteStringDataValue.Construct(
                                        db, "fifty"))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        pdcID,
                        pdc_mveID,
                        300,
                        360,
                        Matrix.Construct(
                          db,
                          pdc_mveID,
                          PredDataValue.Construct(
                            db,
                            Predicate.Construct(
                              db,
                              pve0ID,
                              PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                  db,
                                  pve0ID,
                                  PredDataValue.Construct(
                                    db,
                                    Predicate.Construct(
                                      db,
                                      pve1ID,
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve0ID,
                                          null)),
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve1ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                          null)))))))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        pdcID,
                        pdc_mveID,
                        360,
                        420,
                        Matrix.Construct(
                          db,
                          pdc_mveID,
                          PredDataValue.Construct(
                            db,
                            Predicate.Construct(
                              db,
                              pve2ID,
                              PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                  db,
                                  pve0ID,
                                  PredDataValue.Construct(
                                    db,
                                    Predicate.Construct(
                                      db,
                                      pve1ID,
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve0ID,
                                          null)),
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve1ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                          null)))))),
                                  PredDataValue.Construct(
                                    db,
                                    Predicate.Construct(
                                      db,
                                      pve1ID,
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve0ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                              db,
                                              pve1ID,
                                              PredDataValue.Construct(
                                                db,
                                                Predicate.Construct(
                                                  db,
                                                  pve0ID,
                                                  null)),
                                              PredDataValue.Construct(
                                                db,
                                                Predicate.Construct(
                                                  db,
                                                  pve1ID,
                                                 PredDataValue.Construct(
                                                    db,
                                                    Predicate.Construct(
                                                    db,
                                                    pve0ID,
                                                    null)),
                                                  null)))))),
                                      PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                          db,
                                          pve1ID,
                                          PredDataValue.Construct(
                                            db,
                                            Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                          null)))),
                                  QuoteStringDataValue.Construct(
                                    db, "seventy"))))));

                // create the test string
                testStringA = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( expectedString0.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test setup failed to complete.\n",
                                         header);
                    }

                    if ( db == null )
                    {
                        outStream.printf(
                                "%s new ODBCDatabase() returned null.\n",
                                header);
                    }

                    if ( expectedString0.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString0.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test setup: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        /* Now delete some predicate vocab elements */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;

            try
            {
                db.removePredVE(pve0ID);

                // create the test string
                testStringA = db.toString();


                db.removePredVE(pve1ID);;

                // create the test string
                testStringB = db.toString();


                db.removePredVE(pve3ID);;

                // create the test string
                testStringC = db.toString();


                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( expectedString1.compareTo(testStringA) != 0 ) ||
                 ( expectedString2.compareTo(testStringB) != 0 ) ||
                 ( expectedString3.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 1 failed to complete.\n",
                                         header);
                    }

                    if ( expectedString1.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString1.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }

                    if ( expectedString2.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString2.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }

                    if ( expectedString3.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString3.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 1: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }

        return failures;

    } /* Datavase::TestPVEDeletionListeners__test_01() */


    /**
     * TestPVEModListeners()
     *
     * Verify that modifications in predicate vocab elements propogate through
     * the database as expected.
     *
     *
     * Tests are as follows.  Note that we start with relatively simple tests,
     * and add to the complexity.
     *
     * 1) Allocate a data base, create a predicate column, several predicates,
     *    and a selection of cells in the column with various predicate values.
     *    Add, & delete formal arguments to some of the predicates, and verify
     *    that the changes are reflected correctly in the cells.  Re-arrange
     *    formal arguemnst and verify that the changes are reflected correctly
     *    in the cells.  Combine the above and verify the expected results.
     *
     *                                               -- 3/25/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestPVEModListeners(java.io.PrintStream outStream,
                                              boolean verbose)
    throws SystemErrorException {
        String testBanner =
            "Testing predicate vocab element modification listeners           ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        failures += TestPVEModListeners__test_01(outStream, verbose);

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

    } /* Database::TestPVEModListeners() */


    /**
     * TestPVEModListeners__test_01()
     *
     * Initial smoke check on the PVE mod listeners:
     *
     * Allocate a data base, create a predicate column, several predicates,
     * and a selection of cells in the column with various predicate values.
     * Add, & delete formal arguments to some of the predicates, and verify
     * that the changes are reflected correctly in the cells.  Re-arrange
     * formal arguemnst and verify that the changes are reflected correctly
     * in the cells.  Combine the above and verify the expected results.
     *
     * Return the number of failures.
     *
     *                                               -- 3/31/08
     *
     * Changes:
     *
     *    - None.
     */

    private static int TestPVEModListeners__test_01(
            java.io.PrintStream outStream,
            boolean verbose)
        throws SystemErrorException {
        final String header = "test 01: ";
        String systemErrorExceptionString = "";
        String expectedString0 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pdc(<val>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, (pve2(pve0(<arg0>), " +
                        "pve1(<arg0>, <arg1>), " +
                        "pve2(<arg0>, <arg1>, <arg2>)))))), " +
                 "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString1 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pdc(<val>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "pve0(<arg0>, <arg1>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>, <arg1>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))))), " +
                 "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>, <arg1>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString2 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pdc(<val>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg1>), " +
                 "pve0(<arg-1>, <arg0>, <arg1>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(<arg-1>, 1, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg-1>, <arg0>, <arg1>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))))), " +
                 "(pdc, " +
                   "((1, 00:00:00:000, 00:00:01:000, (pve0(<arg-1>, alpha, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg-1>, <arg0>, <arg1>), " +
                           "pve1(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString3 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pdc(<val>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg0.5>, <arg1>), " +
                 "pve0(<arg-1>, <arg0>, <arg1>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(<arg-1>, 1, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, <arg0.5>, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 2, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg-1>, <arg0>, <arg1>), " +
                           "pve1(<arg0>, <arg0.5>, <arg1>), " +
                           "pve2(<arg0>, <arg1>, <arg2>)))))), " +
                 "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(<arg-1>, alpha, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, <arg0.5>, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, bravo, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(pve0(<arg-1>, <arg0>, <arg1>), " +
                          "pve1(<arg0>, <arg0.5>, <arg1>), " +
                          "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString4 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pdc(<val>), " +
                 "pve2(<arg0>, <arg2>), " +
                 "pve1(<arg0>, <arg0.5>, <arg1>), " +
                 "pve0(<arg-1>, <arg0>, <arg1>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(<arg-1>, 1, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, <arg0.5>, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg-1>, <arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg2>)))))), " +
                 "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(<arg-1>, alpha, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, <arg0.5>, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg-1>, <arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg2>))))))))))";
        String expectedString5 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pdc(<val>), " +
                 "pve2(<arg0>, <arg2>), " +
                 "pve1(<arg0>, <arg0.5>, <arg1>), " +
                 "pve0(<arg0>, <arg1>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, <arg0.5>, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>, <arg1>), " +
                           "pve2(<arg0>, <arg2>)))))), " +
                 "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha, <arg1>))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(alpha, <arg0.5>, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(pve0(<arg0>, <arg1>), pve2(<arg0>, <arg2>))))))))))";
        String expectedString6 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pdc(<val>), " +
                 "pve2(<arg0>, <arg2>), " +
                 "pve1(<arg0>, <arg0.5>, <arg1>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, <arg0.5>, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), pve2(<arg0>, <arg2>)))))), " +
                 "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(alpha, <arg0.5>, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), pve2(<arg0>, <arg2>))))))))))";
        String expectedString7 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pdc(<val>), " +
                 "pve2(<arg0>, <arg1>, <arg2>), " +
                 "pve1(<arg0>, <arg0.5>, <arg1>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(1, <arg0.5>, 2))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, <arg1>, 3))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                    "(pve2(pve0(<arg0>), " +
                          "<arg1>, " +
                          "pve2(<arg0>, <arg1>, <arg2>)))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(alpha, <arg0.5>, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(alpha, <arg1>, charlie))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "<arg1>, " +
                           "pve2(<arg0>, <arg1>, <arg2>))))))))))";
        String expectedString8 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pdc(<val>), " +
                 "pve2(<arg0>, <arg2>, <arg1>), " +
                 "pve1(<arg0>, <arg0.5>, <arg1>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                    "(2, 00:00:01:000, 00:00:02:000, (pve1(1, <arg0.5>, 2))), " +
                    "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 3, <arg1>))), " +
                    "(4, 00:00:03:000, 00:00:04:000, " +
                      "(pve2(pve0(<arg0>), " +
                            "pve2(<arg0>, <arg2>, <arg1>), " +
                            "<arg1>))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(alpha, <arg0.5>, bravo))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(alpha, charlie, <arg1>))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve2(<arg0>, <arg2>, <arg1>), " +
                           "<arg1>)))))))))";
        String expectedString9 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pdc(<val>), " +
                 "pve2(<arg0>, <arg2>, <arg1>), " +
                 "pve1(<arg0.5>, <arg1>, <arg0>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(<arg0.5>, 2, 1))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(1, 3, <arg1>))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve2(<arg0>, <arg2>, <arg1>), " +
                           "<arg1>))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(<arg0.5>, bravo, alpha))), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(alpha, charlie, <arg1>))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(pve0(<arg0>), " +
                           "pve2(<arg0>, <arg2>, <arg1>), " +
                           "<arg1>)))))))))";
        String expectedString10 =
          "(Undefined " +
            "((VocabList) " +
              "(vl_contents: " +
                "(mdc(<val>), " +
                 "pdc(<val>), " +
                 "pve2(<arg3>, <arg1>, <arg0>), " +
                 "pve1(<arg0.5>, <arg1>, <arg0>), " +
                 "pve0(<arg0>)))) " +
            "((ColumnList) " +
              "(cl_contents: " +
                "((mdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(1))), " +
                   "(2, 00:00:01:000, 00:00:02:000, (pve1(<arg0.5>, 2, 1))), " +
                   "(3, 00:00:02:000, 00:00:03:000, (pve2(<arg3>, <arg1>, 1))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(<arg3>, <arg1>, pve0(<arg0>)))))), " +
                "(pdc, " +
                  "((1, 00:00:00:000, 00:00:01:000, (pve0(alpha))), " +
                   "(2, 00:00:01:000, 00:00:02:000, " +
                     "(pve1(<arg0.5>, bravo, alpha))), " +
                   "(3, 00:00:02:000, 00:00:03:000, " +
                     "(pve2(<arg3>, <arg1>, alpha))), " +
                   "(4, 00:00:03:000, 00:00:04:000, " +
                     "(pve2(<arg3>, <arg1>, pve0(<arg0>))))))))))";
        String testStringA = null;
        String testStringB = null;
        String testStringC = null;
        boolean completed;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long mdcID = DBIndex.INVALID_ID;
        long mdc_mveID = DBIndex.INVALID_ID;
        long pdcID = DBIndex.INVALID_ID;
        long pdc_mveID = DBIndex.INVALID_ID;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        Database db = null;
        DataColumn mdc = null;
        DataColumn pdc = null;
        PredicateVocabElement pve0 = null;
        PredicateVocabElement pve1 = null;
        PredicateVocabElement pve2 = null;
        FormalArgument farg = null;
        DataCell m_cell0 = null;
        DataCell p_cell0 = null;

        /* setup test */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;

            try
            {
                // allocate a new database

                db = new ODBCDatabase();


                // create a selection of predicates

                pve0 = new PredicateVocabElement(db, "pve0");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve0.appendFormalArg(farg);
                pve0ID = db.addPredVE(pve0);
                pve0 = db.getPredVE(pve0ID);

                pve1 = new PredicateVocabElement(db, "pve1");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve1.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve1.appendFormalArg(farg);
                pve1ID = db.addPredVE(pve1);
                pve1 = db.getPredVE(pve1ID);

                pve2 = new PredicateVocabElement(db, "pve2");
                farg = new UnTypedFormalArg(db, "<arg0>");
                pve2.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg1>");
                pve2.appendFormalArg(farg);
                farg = new UnTypedFormalArg(db, "<arg2>");
                pve2.appendFormalArg(farg);
                pve2ID = db.addPredVE(pve2);
                pve2 = db.getPredVE(pve2ID);


                // create a couple of Data columns

                mdc = new DataColumn(db, "mdc",
                                     MatrixVocabElement.MatrixType.MATRIX);
                mdcID = db.addColumn(mdc);
                mdc = db.getDataColumn(mdcID);
                mdc_mveID = mdc.getItsMveID();

                pdc = new DataColumn(db, "pdc",
                                     MatrixVocabElement.MatrixType.PREDICATE);
                pdcID = db.addColumn(pdc);
                pdc = db.getDataColumn(pdcID);
                pdc_mveID = pdc.getItsMveID();


                // create a selection of cells
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdcID,
                        mdc_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    IntDataValue.Construct(db, 1))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdcID,
                        mdc_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    IntDataValue.Construct(db, 1),
                                    IntDataValue.Construct(db, 2))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdcID,
                        mdc_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    IntDataValue.Construct(db, 1),
                                    IntDataValue.Construct(db, 2),
                                    IntDataValue.Construct(db, 3))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        mdcID,
                        mdc_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            mdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            null,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            null,
                                            null,
                                            null)))))));

                db.appendCell(
                    DataCell.Construct(
                        db,
                        pdcID,
                        pdc_mveID,
                        0,
                        60,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve0ID,
                                    NominalDataValue.Construct(db, "alpha"))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        pdcID,
                        pdc_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve1ID,
                                    NominalDataValue.Construct(db, "alpha"),
                                    NominalDataValue.Construct(db, "bravo"))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        pdcID,
                        pdc_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    NominalDataValue.Construct(db, "alpha"),
                                    NominalDataValue.Construct(db, "bravo"),
                                    NominalDataValue.Construct(db, "charlie"))))));
                db.appendCell(
                    DataCell.Construct(
                        db,
                        pdcID,
                        pdc_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            pdc_mveID,
                            PredDataValue.Construct(
                                db,
                                Predicate.Construct(
                                    db,
                                    pve2ID,
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve0ID,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve1ID,
                                            null,
                                            null)),
                                    PredDataValue.Construct(
                                        db,
                                        Predicate.Construct(
                                            db,
                                            pve2ID,
                                            null,
                                            null,
                                            null)))))));

                // create the test string
                testStringA = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( expectedString0.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test setup failed to complete.\n",
                                         header);
                    }

                    if ( db == null )
                    {
                        outStream.printf(
                                "%s new ODBCDatabase() returned null.\n",
                                header);
                    }

                    if ( expectedString0.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString0.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test setup: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        /* try adding some arguments */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;

            try
            {
                pve0.appendFormalArg(new UnTypedFormalArg(db, "<arg1>"));
                db.replacePredVE(pve0);
                pve0 = db.getPredVE(pve0ID);

                // create the test string
                testStringA = db.toString();

                pve0.insertFormalArg(new UnTypedFormalArg(db, "<arg-1>"), 0);
                db.replacePredVE(pve0);
                pve0 = db.getPredVE(pve0ID);

                // create the test string
                testStringB = db.toString();

                pve1.insertFormalArg(new UnTypedFormalArg(db, "<arg0.5>"), 1);
                db.replacePredVE(pve1);
                pve1 = db.getPredVE(pve1ID);

                testStringC = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( expectedString1.compareTo(testStringA) != 0 ) ||
                 ( expectedString2.compareTo(testStringB) != 0 ) ||
                 ( expectedString3.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 1 failed to complete.\n",
                                         header);
                    }

                    if ( expectedString1.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString1.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }

                    if ( expectedString2.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString2.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }

                    if ( expectedString3.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString3.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 1: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        /* try deleting some arguments */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;

            try
            {
                pve2.deleteFormalArg(1);
                db.replacePredVE(pve2);
                pve2 = db.getPredVE(pve2ID);

                // create the test string
                testStringA = db.toString();

                pve0.deleteFormalArg(0);
                db.replacePredVE(pve0);
                pve0 = db.getPredVE(pve0ID);

                // create the test string
                testStringB = db.toString();

                pve0.deleteFormalArg(1);
                db.replacePredVE(pve0);
                pve0 = db.getPredVE(pve0ID);

                testStringC = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( expectedString4.compareTo(testStringA) != 0 ) ||
                 ( expectedString5.compareTo(testStringB) != 0 ) ||
                 ( expectedString6.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 2 failed to complete.\n",
                                         header);
                    }

                    if ( expectedString4.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString4.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }

                    if ( expectedString5.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString5.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }

                    if ( expectedString6.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString6.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 2: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        /* try moving some arguments around -- will have to add first */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;

            try
            {
                pve2.insertFormalArg(new UnTypedFormalArg(db, "<arg1>"), 1);
                db.replacePredVE(pve2);
                pve2 = db.getPredVE(pve2ID);

                // create the test string
                testStringA = db.toString();


                farg = pve2.getFormalArg(2);
                pve2.deleteFormalArg(2);
                pve2.insertFormalArg(farg, 1);
                db.replacePredVE(pve2);
                pve2 = db.getPredVE(pve2ID);

                // create the test string
                testStringB = db.toString();


                farg = pve1.getFormalArg(0);
                pve1.deleteFormalArg(0);
                pve1.insertFormalArg(farg, 2);
                db.replacePredVE(pve1);
                pve1 = db.getPredVE(pve1ID);

                // create the test string
                testStringC = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( expectedString7.compareTo(testStringA) != 0 ) ||
                 ( expectedString8.compareTo(testStringB) != 0 ) ||
                 ( expectedString9.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 3 failed to complete.\n",
                                         header);
                    }

                    if ( expectedString7.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testString doesn't match expectedString7.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }

                    if ( expectedString8.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString8.\n" +
                             "testStringB = \"%s\".\n", header, testStringB);
                    }

                    if ( expectedString9.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString9.\n" +
                             "testStringC = \"%s\".\n", header, testStringC);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 3: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        /* finally, mix things up a bit and see if we get confused */
        if ( failures == 0 )
        {
            testStringA = "";
            testStringB = "";
            testStringC = "";
            completed = false;
            threwSystemErrorException = false;

            try
            {
                // move arg0 to the end of the arg list
                farg = pve2.getFormalArg(0);
                pve2.deleteFormalArg(0);
                pve2.insertFormalArg(farg, 2);

                // insert arg4 at the beginning of the arg list
                pve2.insertFormalArg(new UnTypedFormalArg(db, "<arg3>"), 0);

                // delete arg2
                pve2.deleteFormalArg(1);

                // apply the modified version to the db
                db.replacePredVE(pve2);
                pve2 = db.getPredVE(pve2ID);

                // create the test string
                testStringA = db.toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( expectedString10.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s test 4 failed to complete.\n",
                                         header);
                    }

                    if ( expectedString10.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString10.\n" +
                             "testStringA = \"%s\".\n", header, testStringA);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in test 4: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }

        return failures;

    } /* Datavase::TestPVEModListeners__test_01() */

}