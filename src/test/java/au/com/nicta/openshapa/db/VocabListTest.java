package au.com.nicta.openshapa.db;

import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author swhitcher
 */
public class VocabListTest {

    public VocabListTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /**
     * TestClassVocabList()
     *
     * Main routine for tests of class VocabList.
     *
     *                                  JRM -- 5/8/07
     *
     * Changes:
     *
     *    - Non.
     */
    @Test
    public void TestClassVocabList() throws SystemErrorException {
        PrintStream outStream = System.out;
        boolean verbose = true;

        boolean pass = true;
        int failures = 0;

        outStream.print("Testing class VocabList:\n");

        if ( ! Test1ArgConstructor(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestVLManagement(outStream, verbose) )
        {
            failures++;
        }

        if ( ! TestGetPredsAndMatricies(outStream, verbose) )
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
            outStream.printf("%d failures in tests for class VocabList.\n\n",
                             failures);
        }
        else
        {
            outStream.print("All tests passed for class VocabList.\n\n");
        }

        assertTrue(pass);

    } /* Database::TestClassVocabList() */

    /**
     * ConstructTestMatrix()
     *
     * Construct a matrix vocab element with the supplied type and formal
     * arguments (if any).  Don't bother to catch system errors -- that will
     * be done at a higher level.
     *                                          JRM - 5/10/07
     *
     * Changes:
     *
     *    - None.
     */

    public static MatrixVocabElement
            ConstructTestMatrix(Database db,
                                String name,
                                MatrixVocabElement.MatrixType type,
                                FormalArgument arg0,
                                FormalArgument arg1,
                                FormalArgument arg2,
                                FormalArgument arg3)
       throws SystemErrorException
    {
        MatrixVocabElement matrix = null;

        matrix = new MatrixVocabElement(db, name);

        matrix.setType(type);

        if ( arg0 != null )
        {
            matrix.appendFormalArg(arg0);
        }

        if ( arg1 != null )
        {
            matrix.appendFormalArg(arg1);
        }

        if ( arg2 != null )
        {
            matrix.appendFormalArg(arg2);
        }

        if ( arg3 != null )
        {
            matrix.appendFormalArg(arg3);
        }

        return matrix;

    } /* VocabList::ConstructTestMatrix() */

    /**
     * ConstructTestPred()
     *
     * Construct a predicate vocab element with the supplied formal arguments
     * (if any).  Don't bother to catch system errors -- that will be done
     * at a higher level.
     *                                          JRM - 5/10/07
     *
     * Changes:
     *
     *    - None.
     */

    public static PredicateVocabElement ConstructTestPred(Database db,
                                                          String name,
                                                          FormalArgument arg0,
                                                          FormalArgument arg1,
                                                          FormalArgument arg2,
                                                          FormalArgument arg3)
       throws SystemErrorException
    {
        PredicateVocabElement pred = null;

        pred = new PredicateVocabElement(db, name);

        if ( arg0 != null )
        {
            pred.appendFormalArg(arg0);
        }

        if ( arg1 != null )
        {
            pred.appendFormalArg(arg1);
        }

        if ( arg2 != null )
        {
            pred.appendFormalArg(arg2);
        }

        if ( arg3 != null )
        {
            pred.appendFormalArg(arg3);
        }

        return pred;

    } /* VocabList::ConstructTestPred() */


    /**
     * Test1ArgConstructor()
     *
     * Run a battery of tests on the one argument constructor for this
     * class, and on the instance returned.
     *
     *                                              JRM - 5/8/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean Test1ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing 1 argument constructor for class VocabList               ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        VocabList vl = null;

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
            vl = null;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                vl = new VocabList(db);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! methodReturned ) ||
                 ( db == null ) ||
                 ( vl == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("One or more class allocations " +
                                        "failed to complete.\n");
                    }

                    if ( db == null )
                    {
                        outStream.print(
                                "new ODBCDatabase() returned null.\n");
                    }

                    if ( vl == null )
                    {
                        outStream.print(
                                "new VocabList(db) returned null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new VocabList(db) threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            if ( vl.db != db )
            {
                failures++;

                if ( verbose )
                {
                    outStream.print("Unexpected initial vl.db != db.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ( vl.vl == null ) ||
                 ( ! vl.vl.isEmpty() ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("vl null or non-empty on creation.\n");
                }
            }
        }


        /* Verify that the constructor fails when passed an invalid db. */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            vl = null;
            systemErrorExceptionString = null;

            try
            {
                vl = new VocabList((Database)null);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( methodReturned ) ||
                 ( vl != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("new VocabList(null) returned.\n");
                    }

                    if ( vl != null )
                    {
                        outStream.print(
                             "new VocabList(null) returned non-null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("new VocabList(null) failed to " +
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
     * TestGetPredAndMatricies()
     *
     * Run a battery of tests on the getMatricies() and getPreds() methods.
     *
     *                                              JRM -- 6/19/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestGetPredsAndMatricies(java.io.PrintStream outStream,
                                                   boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing getPreds() and getMatricies()                            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long p0_id = DBIndex.INVALID_ID;
        long p1_id = DBIndex.INVALID_ID;
        long p2_id = DBIndex.INVALID_ID;
        long p3_id = DBIndex.INVALID_ID;
        long p4_id = DBIndex.INVALID_ID;
        long p5_id = DBIndex.INVALID_ID;
        long p6_id = DBIndex.INVALID_ID;
        long p7_id = DBIndex.INVALID_ID;
        long m0_id = DBIndex.INVALID_ID;
        long m1_id = DBIndex.INVALID_ID;
        long m2_id = DBIndex.INVALID_ID;
        long m3_id = DBIndex.INVALID_ID;
        long m4_id = DBIndex.INVALID_ID;
        long m5_id = DBIndex.INVALID_ID;
        long m6_id = DBIndex.INVALID_ID;
        long m7_id = DBIndex.INVALID_ID;
        Database db = null;
        VocabList vl = null;
        PredicateVocabElement p0 = null;
        PredicateVocabElement p1 = null;
        PredicateVocabElement p2 = null;
        PredicateVocabElement p3 = null;
        PredicateVocabElement p4 = null;
        PredicateVocabElement p5 = null;
        PredicateVocabElement p6 = null;
        PredicateVocabElement p7 = null;
        MatrixVocabElement m0 = null;
        MatrixVocabElement m1 = null;
        MatrixVocabElement m2 = null;
        MatrixVocabElement m3 = null;
        MatrixVocabElement m4 = null;
        MatrixVocabElement m5 = null;
        MatrixVocabElement m6 = null;
        MatrixVocabElement m7 = null;
        IntFormalArg alpha = null;
        FloatFormalArg bravo = null;
        NominalFormalArg charlie = null;
        TextStringFormalArg delta = null;
        PredFormalArg echo = null;
        UnTypedFormalArg foxtrot = null;
        UnTypedFormalArg golf = null;
        UnTypedFormalArg hotel = null;
        UnTypedFormalArg hotela = null;
        UnTypedFormalArg india = null;
        UnTypedFormalArg juno = null;
        UnTypedFormalArg kilo = null;
        UnTypedFormalArg lima = null;
        UnTypedFormalArg mike = null;
        UnTypedFormalArg nero = null;
        UnTypedFormalArg oscar = null;
        UnTypedFormalArg papa = null;
        UnTypedFormalArg quebec = null;
        UnTypedFormalArg reno = null;
        UnTypedFormalArg sierra = null;
        UnTypedFormalArg tango = null;
        FormalArgument m0_ord = null;
        FormalArgument m0_onset = null;
        FormalArgument m0_offset = null;
        FormalArgument m1_ord = null;
        FormalArgument m1_onset = null;
        FormalArgument m1_offset = null;
        FormalArgument m2_ord = null;
        FormalArgument m2_onset = null;
        FormalArgument m2_offset = null;
        FormalArgument m3_ord = null;
        FormalArgument m3_onset = null;
        FormalArgument m3_offset = null;
        FormalArgument m4_ord = null;
        FormalArgument m4_onset = null;
        FormalArgument m4_offset = null;
        FormalArgument m5_ord = null;
        FormalArgument m5_onset = null;
        FormalArgument m5_offset = null;
        FormalArgument m6_ord = null;
        FormalArgument m6_onset = null;
        FormalArgument m6_offset = null;
        FormalArgument m7_ord = null;
        FormalArgument m7_onset = null;
        FormalArgument m7_offset = null;
        FormalArgument cp_alpha = null;
        FormalArgument cp_bravo = null;
        FormalArgument cp_charlie = null;
        FormalArgument cp_delta = null;
        FormalArgument cp_echo = null;
        FormalArgument cp_foxtrot = null;
        FormalArgument cp_golf = null;
        FormalArgument cp_hotel = null;
        FormalArgument cp_india = null;
        FormalArgument cp_juno = null;
        FormalArgument cp_kilo = null;
        FormalArgument cp_lima = null;
        FormalArgument cp_mike = null;
        FormalArgument cp_nero = null;
        FormalArgument cp_oscar = null;
        FormalArgument cp_papa = null;
        FormalArgument cp_quebec = null;
        FormalArgument cp_reno = null;
        FormalArgument cp_sierra = null;
        java.util.Vector<MatrixVocabElement> matricies = null;
        java.util.Vector<PredicateVocabElement> preds = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* Allocate a database, and verify that getPreds() and getMatricies()
         * return null when run on an empty vocab list.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            db = null;
            vl = null;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                vl = db.vl;
                matricies = vl.getMatricies();
                preds = vl.getPreds();
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( vl == null ) ||
                 ( matricies != null ) ||
                 ( preds != null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete(1).\n");
                    }

                    if ( db == null )
                    {
                        outStream.print("new ODBCDatabase() returned null.\n");
                    }

                    if ( vl == null )
                    {
                        outStream.print("vl not allocated with db?!?!?l.\n");
                    }

                    if ( preds != null )
                    {
                        outStream.print(
                                "unexpected return from getPreds()(1).\n");
                    }

                    if ( matricies != null )
                    {
                        outStream.print(
                                "unexpected return from getMatricies()(1).\n");
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

        /* Insert several system and/or non matrixType.MATRIX matricies,
         * and run getMatricies() & getPreds() again.  They should still return
         * null.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                alpha   = new IntFormalArg(db, "<alpha>");
                bravo   = new FloatFormalArg(db, "<bravo>");
                charlie = new NominalFormalArg(db, "<charlie>");
                delta   = new TextStringFormalArg(db);
                echo    = new PredFormalArg(db, "<echo>");
                foxtrot = new UnTypedFormalArg(db, "<foxtrot>");
                golf    = new UnTypedFormalArg(db, "<golf>");
                hotel   = new UnTypedFormalArg(db, "<hotel>");
                india   = new UnTypedFormalArg(db, "<india>");
                juno    = new UnTypedFormalArg(db, "<juno>");
                kilo    = new UnTypedFormalArg(db, "<kilo>");
                lima    = new UnTypedFormalArg(db, "<lima>");
                mike    = new UnTypedFormalArg(db, "<mike>");
                nero    = new UnTypedFormalArg(db, "<nero>");
                oscar   = new UnTypedFormalArg(db, "<oscar>");
                papa    = new UnTypedFormalArg(db, "<papa>");
                quebec  = new UnTypedFormalArg(db, "<quebec>");
                reno    = new UnTypedFormalArg(db, "<reno>");
                sierra  = new UnTypedFormalArg(db, "<sierra>");
                tango   = new UnTypedFormalArg(db, "<tango>");

                p0 = ConstructTestPred(db, "p0", lima, null, null, null);
                p0.setSystem();
                p1 = ConstructTestPred(db, "p1", mike, nero, null, null);
                p1.setSystem();
                p2 = ConstructTestPred(db, "p2", oscar, null, null, null);
                p3 = ConstructTestPred(db, "p3", papa, null, null, null);
                p4 = ConstructTestPred(db, "p4", quebec, null, null, null);
                p5 = ConstructTestPred(db, "p5", reno, null, null, null);
                p6 = ConstructTestPred(db, "p6", sierra, null, null, null);
                p7 = ConstructTestPred(db, "p7", tango, null, null, null);

                m0 = ConstructTestMatrix(db, "m0",
                                         MatrixVocabElement.MatrixType.INTEGER,
                                         alpha, null, null, null);
                m1 = ConstructTestMatrix(db, "m1",
                                         MatrixVocabElement.MatrixType.FLOAT,
                                         bravo, null, null, null);
                m2 = ConstructTestMatrix(db, "m2",
                                         MatrixVocabElement.MatrixType.NOMINAL,
                                         charlie, null, null, null);
                m3 = ConstructTestMatrix(db, "m3",
                                         MatrixVocabElement.MatrixType.TEXT,
                                         delta, null, null, null);
                m4 = ConstructTestMatrix(db, "m4",
                                         MatrixVocabElement.MatrixType.PREDICATE,
                                         echo, null, null, null);
                m5 = ConstructTestMatrix(db, "m5",
                                         MatrixVocabElement.MatrixType.MATRIX,
                                         foxtrot, golf, hotel, null);
                m5.setSystem();
                m6 = ConstructTestMatrix(db, "m6",
                                         MatrixVocabElement.MatrixType.MATRIX,
                                         india, juno, null, null);
                m7 = ConstructTestMatrix(db, "m7",
                                         MatrixVocabElement.MatrixType.MATRIX,
                                         kilo, null, null, null);

                vl.addElement(m0);

                m0_id = m0.getID();
                m0_ord = m0.getCPFormalArg(0);
                m0_onset = m0.getCPFormalArg(1);
                m0_offset = m0.getCPFormalArg(2);
                cp_alpha = m0.getCPFormalArg(3);

                vl.addElement(m1);

                m1_id = m1.getID();
                m1_ord = m1.getCPFormalArg(0);
                m1_onset = m1.getCPFormalArg(1);
                m1_offset = m1.getCPFormalArg(2);
                cp_bravo = m1.getCPFormalArg(3);

                vl.addElement(m2);

                m2_id = m2.getID();
                m2_ord = m2.getCPFormalArg(0);
                m2_onset = m2.getCPFormalArg(1);
                m2_offset = m2.getCPFormalArg(2);
                cp_charlie = m2.getCPFormalArg(3);

                vl.addElement(m3);

                m3_id = m3.getID();
                m3_ord = m3.getCPFormalArg(0);
                m3_onset = m3.getCPFormalArg(1);
                m3_offset = m3.getCPFormalArg(2);
                cp_delta = m3.getCPFormalArg(3);

                vl.addElement(m4);

                m4_id = m4.getID();
                m4_ord = m4.getCPFormalArg(0);
                m4_onset = m4.getCPFormalArg(1);
                m4_offset = m4.getCPFormalArg(2);
                cp_echo = m4.getCPFormalArg(3);

                vl.addElement(m5);

                m5_id = m5.getID();
                m5_ord = m5.getCPFormalArg(0);
                m5_onset = m5.getCPFormalArg(1);
                m5_offset = m5.getCPFormalArg(2);
                cp_foxtrot = m5.getCPFormalArg(3);
                cp_golf = m5.getCPFormalArg(4);
                cp_hotel = m5.getCPFormalArg(5);

                vl.addElement(p0);
                p0_id = p0.getID();
                vl.addElement(p1);
                p1_id = p1.getID();

                matricies = vl.getMatricies();
                preds = vl.getPreds();
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( alpha == null ) || ( bravo == null ) ||
                 ( charlie == null ) || ( delta == null ) ||
                 ( echo == null ) || ( foxtrot == null ) ||
                 ( golf == null ) || ( hotel == null ) ||
                 ( india == null ) || ( juno == null ) ||
                 ( kilo == null ) || ( lima == null ) ||
                 ( mike == null ) || ( nero == null ) ||
                 ( oscar == null ) || ( papa == null ) ||
                 ( quebec == null ) || ( reno == null ) ||
                 ( sierra == null ) || ( tango == null ) ||
                 ( p0 == null ) || ( p1 == null ) || ( p2 == null ) ||
                 ( p3 == null ) || ( p4 == null ) || ( p5 == null ) ||
                 ( p6 == null ) || ( p7 == null ) ||
                 ( m0 == null ) || ( m1 == null ) || ( m2 == null ) ||
                 ( m3 == null ) || ( m4 == null ) || ( m5 == null ) ||
                 ( m6 == null ) || ( m7 == null ) ||
                 ( m0_id == DBIndex.INVALID_ID ) ||
                 ( m1_id == DBIndex.INVALID_ID ) ||
                 ( m2_id == DBIndex.INVALID_ID ) ||
                 ( m3_id == DBIndex.INVALID_ID ) ||
                 ( m4_id == DBIndex.INVALID_ID ) ||
                 ( m5_id == DBIndex.INVALID_ID ) ||
                 ( p0_id == DBIndex.INVALID_ID ) ||
                 ( p1_id == DBIndex.INVALID_ID ) ||
                 ( m0_ord == null ) ||
                 ( m0_onset == null ) ||
                 ( m0_offset == null ) ||
                 ( m1_ord == null ) ||
                 ( m1_onset == null ) ||
                 ( m1_offset == null ) ||
                 ( m2_ord == null ) ||
                 ( m2_onset == null ) ||
                 ( m2_offset == null ) ||
                 ( m3_ord == null ) ||
                 ( m3_onset == null ) ||
                 ( m3_offset == null ) ||
                 ( m4_ord == null ) ||
                 ( m4_onset == null ) ||
                 ( m4_offset == null ) ||
                 ( m5_ord == null ) ||
                 ( m5_onset == null ) ||
                 ( m5_offset == null ) ||
                 ( cp_alpha == null ) ||
                 ( cp_bravo == null ) ||
                 ( cp_charlie == null ) ||
                 ( cp_delta == null ) ||
                 ( cp_echo == null ) ||
                 ( cp_foxtrot == null ) ||
                 ( cp_golf == null ) ||
                 ( cp_hotel == null ) ||
                 ( matricies != null ) ||
                 ( preds != null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete(2).\n");
                    }

                    if ( ( alpha == null ) || ( bravo == null ) ||
                         ( charlie == null ) || ( delta == null ) ||
                         ( echo == null ) || ( foxtrot == null ) ||
                         ( golf == null ) || ( hotel == null ) ||
                         ( india == null ) || ( juno == null ) ||
                         ( kilo == null ) || ( lima == null ) ||
                         ( mike == null ) || ( nero == null ) ||
                         ( oscar == null ) || ( papa == null ) ||
                         ( quebec == null ) || ( reno == null ) ||
                         ( sierra == null ) || ( tango == null ) )
                    {
                        outStream.print("formal arg alloc(s) failed.\n");
                    }

                    if ( ( p0 == null ) || ( p1 == null ) || ( p2 == null ) ||
                         ( p3 == null ) || ( p4 == null ) || ( p5 == null ) ||
                         ( p6 == null ) || ( p7 == null ) )
                    {
                        outStream.print("predicate alloc(s) failed.\n");
                    }

                    if ( ( m0 == null ) || ( m1 == null ) || ( m2 == null ) ||
                         ( m3 == null ) || ( m4 == null ) || ( m5 == null ) ||
                         ( m6 == null ) || ( m7 == null ) )
                    {
                        outStream.print("matrix alloc(s) failed.\n");
                    }

                    if ( ( m0_id == DBIndex.INVALID_ID ) ||
                         ( m1_id == DBIndex.INVALID_ID ) ||
                         ( m2_id == DBIndex.INVALID_ID ) ||
                         ( m3_id == DBIndex.INVALID_ID ) ||
                         ( m4_id == DBIndex.INVALID_ID ) ||
                         ( m5_id == DBIndex.INVALID_ID ) ||
                         ( p0_id == DBIndex.INVALID_ID ) ||
                         ( p1_id == DBIndex.INVALID_ID ) )
                    {
                        outStream.print("bad ID assignment(s).\n");
                    }

                    if ( ( m0_ord == null ) ||
                         ( m0_onset == null ) ||
                         ( m0_offset == null ) ||
                         ( m1_ord == null ) ||
                         ( m1_onset == null ) ||
                         ( m1_offset == null ) ||
                         ( m2_ord == null ) ||
                         ( m2_onset == null ) ||
                         ( m2_offset == null ) ||
                         ( m3_ord == null ) ||
                         ( m3_onset == null ) ||
                         ( m3_offset == null ) ||
                         ( m4_ord == null ) ||
                         ( m4_onset == null ) ||
                         ( m4_offset == null ) ||
                         ( m5_ord == null ) ||
                         ( m5_onset == null ) ||
                         ( m5_offset == null ) ||
                         ( cp_alpha == null ) ||
                         ( cp_bravo == null ) ||
                         ( cp_charlie == null ) ||
                         ( cp_delta == null ) ||
                         ( cp_echo == null ) ||
                         ( cp_foxtrot == null ) ||
                         ( cp_golf == null ) ||
                         ( cp_hotel == null ) )
                    {
                        outStream.print("bad col pred fArg(s).\n");
                    }

                    if ( preds != null )
                    {
                        outStream.print(
                                "unexpected return from getPreds()(2).\n");
                    }

                    if ( matricies != null )
                    {
                        outStream.print(
                                "unexpected return from getMatricies()(2).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(2): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }


        if ( failures == 0 )
        {
            long keys[] = {m0_id, m1_id, m2_id, m3_id, m4_id, m5_id,
                           p0_id, p1_id};
            VocabElement values[] = {m0, m1, m2, m3, m4, m5, p0, p1};
            long idxKeys[] = { 1,  2,  3,  4,  5,  6,
                               7,  8,  9, 10, 11, 12,
                              13, 14, 15, 16, 17, 18,
                              19, 20, 21, 22, 23, 24,
                              25, 26, 27, 28, 29, 30,
                              31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                              41, 42,
                              43, 44, 45};
            DBElement idxValues[] =
                    {m0, alpha, m0_ord, m0_onset, m0_offset, cp_alpha,
                     m1, bravo, m1_ord, m1_onset, m1_offset, cp_bravo,
                     m2, charlie, m2_ord, m2_onset, m2_offset, cp_charlie,
                     m3, delta, m3_ord, m3_onset, m3_offset, cp_delta,
                     m4, echo, m4_ord, m4_onset, m4_offset, cp_echo,
                     m5, foxtrot, golf, hotel, m5_ord, m5_onset, m5_offset,
                                               cp_foxtrot, cp_golf, cp_hotel,
                     p0, lima,
                     p1, mike, nero};

            if ( ! VerifyVLContents(8, keys, values, vl, outStream,
                                    verbose, 11) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(45, idxKeys, idxValues,
                                               db.idx, outStream,
                                               verbose, 111) )
            {
                failures++;
            }
       }


        /* Insert one non-system predicate and one non-system matrixType.MATRIX
         * predicate, and run getMatricies() & getPreds() again.  Copies should
         * show up in the returned vectors.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl.addElement(m6);

                m6_id = m6.getID();
                m6_ord = m6.getCPFormalArg(0);
                m6_onset = m6.getCPFormalArg(1);
                m6_offset = m6.getCPFormalArg(2);
                cp_india = m6.getCPFormalArg(3);
                cp_juno = m6.getCPFormalArg(4);

                vl.addElement(p2);

                p2_id = p2.getID();

                matricies = vl.getMatricies();
                preds = vl.getPreds();
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( m6_ord == null ) ||
                 ( m6_onset == null ) ||
                 ( m6_offset == null ) ||
                 ( cp_india == null ) ||
                 ( cp_juno == null ) ||
                 ( m6_id == DBIndex.INVALID_ID ) ||
                 ( p2_id == DBIndex.INVALID_ID ) ||
                 ( matricies == null ) ||
                 ( preds == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete(3).\n");
                    }

                    if ( ( m6_ord == null ) ||
                         ( m6_onset == null ) ||
                         ( m6_offset == null ) ||
                         ( cp_india == null ) ||
                         ( cp_juno == null ) )
                    {
                        outStream.print("bad col pred fArg(s).\n");
                    }

                    if ( ( m6_id == DBIndex.INVALID_ID ) ||
                         ( p2_id == DBIndex.INVALID_ID ) )
                    {
                        outStream.print("bad ID assignment(s).\n");
                    }

                    if ( preds == null )
                    {
                        outStream.print(
                                "unexpected return from getPreds()(3).\n");
                    }

                    if ( matricies == null )
                    {
                        outStream.print(
                                "unexpected return from getMatricies()(3).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(3): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }


        if ( failures == 0 )
        {
            long keys[] = {m0_id, m1_id, m2_id, m3_id, m4_id, m5_id,
                           p0_id, p1_id, m6_id, p2_id};
            VocabElement values[] = {m0, m1, m2, m3, m4, m5, p0, p1, m6, p2};
            long idxKeys[] = { 1,  2,  3,  4,  5,  6,
                               7,  8,  9, 10, 11, 12,
                              13, 14, 15, 16, 17, 18,
                              19, 20, 21, 22, 23, 24,
                              25, 26, 27, 28, 29, 30,
                              31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                              41, 42,
                              43, 44, 45,
                              46, 47, 48, 49, 50, 51, 52, 53,
                              54, 55};
            DBElement idxValues[] =
                    {m0, alpha, m0_ord, m0_onset, m0_offset, cp_alpha,
                     m1, bravo, m1_ord, m1_onset, m1_offset, cp_bravo,
                     m2, charlie, m2_ord, m2_onset, m2_offset, cp_charlie,
                     m3, delta, m3_ord, m3_onset, m3_offset, cp_delta,
                     m4, echo, m4_ord, m4_onset, m4_offset, cp_echo,
                     m5, foxtrot, golf, hotel, m5_ord, m5_onset, m5_offset,
                                               cp_foxtrot, cp_golf, cp_hotel,
                     p0, lima,
                     p1, mike, nero,
                     m6, india, juno, m6_ord, m6_onset, m6_offset,
                                      cp_india, cp_juno,
                     p2, oscar};
            MatrixVocabElement matrixValues[] = {m6};
            PredicateVocabElement predValues[] = {p2};

            if ( ! VerifyVLContents(10, keys, values, vl, outStream,
                                    verbose, 2) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(55, idxKeys, idxValues,
                                               db.idx, outStream,
                                               verbose, 2) )
            {
                failures++;
            }

            if ( ! VerifyVectorContents(matricies, 1, matrixValues,
                                        outStream, verbose, 2) )
            {
                failures++;
            }

            if ( ! VerifyVectorContents(preds, 1, predValues,
                                        outStream, verbose, 2) )
            {
                failures++;
            }
        }

        /* Insert more non-systems preds and matricies.  Check the output
         * yet again.
         */
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl.addElement(p3);

                p3_id = p3.getID();

                vl.addElement(p4);

                p4_id = p4.getID();

                vl.addElement(p5);

                p5_id = p5.getID();

                vl.addElement(m7);

                m7_id = m7.getID();
                m7_ord = m7.getCPFormalArg(0);
                m7_onset = m7.getCPFormalArg(1);
                m7_offset = m7.getCPFormalArg(2);
                cp_kilo = m7.getCPFormalArg(3);

                vl.addElement(p6);

                p6_id = p6.getID();

                vl.addElement(p7);

                p7_id = p7.getID();

                matricies = vl.getMatricies();
                preds = vl.getPreds();
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( p3_id == DBIndex.INVALID_ID ) ||
                 ( p4_id == DBIndex.INVALID_ID ) ||
                 ( p5_id == DBIndex.INVALID_ID ) ||
                 ( p6_id == DBIndex.INVALID_ID ) ||
                 ( p7_id == DBIndex.INVALID_ID ) ||
                 ( m7_id == DBIndex.INVALID_ID ) ||
                 ( m7_ord == null ) ||
                 ( m7_onset == null ) ||
                 ( m7_offset == null ) ||
                 ( cp_kilo == null ) ||
                 ( matricies == null ) ||
                 ( matricies.size() != 2 ) ||
                 ( preds == null ) ||
                 ( preds.size() != 6 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("test failed to complete(4).\n");
                    }

                    if ( ( p3_id == DBIndex.INVALID_ID ) ||
                         ( p4_id == DBIndex.INVALID_ID ) ||
                         ( p5_id == DBIndex.INVALID_ID ) ||
                         ( p6_id == DBIndex.INVALID_ID ) ||
                         ( p7_id == DBIndex.INVALID_ID ) ||
                         ( m7_id == DBIndex.INVALID_ID ) )
                    {
                        outStream.print("bad ID assignment(s).\n");
                    }

                    if ( ( m7_ord == null ) ||
                        ( m7_onset == null ) ||
                        ( m7_offset == null ) ||
                        ( cp_kilo == null ) )
                    {
                        outStream.print("bad col pred fArg(s).\n");
                    }

                    if ( ( preds == null ) ||
                         ( preds.size() != 1 ) ||
                         ( preds.get(0) == p2 ) ||
                         ( preds.get(0).toDBString().
                            compareTo(p2.toDBString()) != 0 ) )
                    {
                        outStream.print(
                                "unexpected return from getPreds()(4).\n");
                    }

                    if ( ( matricies == null ) ||
                         ( matricies.size() != 2 ) ||
                         ( matricies.get(0) == m6 ) ||
                         ( matricies.get(0).toDBString().
                            compareTo(m6.toDBString()) != 0 ) )
                    {
                        outStream.print(
                                "unexpected return from getMatricies()(4).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(4): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {m0_id, m1_id, m2_id, m3_id, m4_id, m5_id,
                           p0_id, p1_id, m6_id, p2_id, p3_id, p4_id, p5_id,
                           m7_id, p6_id, p7_id};
            VocabElement values[] = {m0, m1, m2, m3, m4, m5, p0, p1, m6, p2,
                                     p3, p4, p5, m7, p6, p7};
            long idxKeys[] = { 1,  2,  3,  4,  5,  6,
                               7,  8,  9, 10, 11, 12,
                              13, 14, 15, 16, 17, 18,
                              19, 20, 21, 22, 23, 24,
                              25, 26, 27, 28, 29, 30,
                              31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                              41, 42,
                              43, 44, 45,
                              46, 47, 48, 49, 50, 51, 52, 53,
                              54, 55,
                              56, 57,
                              58, 59,
                              60, 61,
                              62, 63, 64, 65, 66, 67,
                              68, 69,
                              70, 71};
            DBElement idxValues[] =
                    {m0, alpha, m0_ord, m0_onset, m0_offset, cp_alpha,
                     m1, bravo, m1_ord, m1_onset, m1_offset, cp_bravo,
                     m2, charlie, m2_ord, m2_onset, m2_offset, cp_charlie,
                     m3, delta, m3_ord, m3_onset, m3_offset, cp_delta,
                     m4, echo, m4_ord, m4_onset, m4_offset, cp_echo,
                     m5, foxtrot, golf, hotel, m5_ord, m5_onset, m5_offset,
                                               cp_foxtrot, cp_golf, cp_hotel,
                     p0, lima,
                     p1, mike, nero,
                     m6, india, juno, m6_ord, m6_onset, m6_offset,
                                      cp_india, cp_juno,
                     p2, oscar,
                     p3, papa,
                     p4, quebec,
                     p5, reno,
                     m7, kilo, m7_ord, m7_onset, m7_offset, cp_kilo,
                     p6, sierra,
                     p7, tango};
            MatrixVocabElement matrixValues[] = {m6, m7};
            PredicateVocabElement predValues[] = {p2, p3, p4, p5, p6, p7};

            if ( ! VerifyVLContents(16, keys, values, vl, outStream,
                                    verbose, 3) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(71, idxKeys, idxValues,
                                               db.idx, outStream,
                                               verbose, 3) )
            {
                failures++;
            }

            if ( ! VerifyVectorContents(matricies, 2, matrixValues,
                                        outStream, verbose, 4) )
            {
                failures++;
            }

            if ( ! VerifyVectorContents(preds, 6, predValues,
                                        outStream, verbose, 4) )
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

    } /* DBIndex::TestGetPredsAndMatricies() */


    /**
     * TestVLManagement()
     *
     * Run a battery of tests on vocab list management.
     *
     *                                  JRM -- 5/8/07
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestVLManagement(java.io.PrintStream outStream,
                                           boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing vocab list management for class VocabList                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean inVL0;
        boolean inVL1;
        boolean inVL2;
        boolean inVL3;
        boolean inVL4;
        boolean mInVL0;
        boolean mInVL1;
        boolean mInVL2;
        boolean mInVL3;
        boolean mInVL4;
        boolean mInVL5;
        boolean pInVL0;
        boolean pInVL1;
        boolean pInVL2;
        boolean pInVL3;
        boolean pInVL4;
        boolean pInVL5;
        boolean methodReturned = false;
        boolean contentsVerified;
        boolean pass = true;
        boolean threwSystemErrorException = false;
        int failures = 0;
        String s = null;
        Database db = null;
        Database another_db = null;
        VocabList vl = null;
        DBElement dbe = null;
        PredicateVocabElement p0 = null;
        PredicateVocabElement p1 = null;
        PredicateVocabElement p1a = null;
        PredicateVocabElement p2 = null;
        PredicateVocabElement p3 = null;
        PredicateVocabElement p3a = null;
        PredicateVocabElement p3dup = null;
        PredicateVocabElement p4 = null;
        PredicateVocabElement p5 = null;
        PredicateVocabElement p6 = null;
        MatrixVocabElement m0 = null;
        MatrixVocabElement m1 = null;
        MatrixVocabElement m1a = null;
        MatrixVocabElement m2 = null;
        MatrixVocabElement m2a = null;
        MatrixVocabElement m3 = null;
        MatrixVocabElement m3a = null;
        VocabElement ve0;
        VocabElement ve1;
        VocabElement ve2;
        VocabElement ve3;
        VocabElement ve4;
        VocabElement ve5;
        VocabElement ve6;
        VocabElement ve7;
        UnTypedFormalArg alpha = null;
        UnTypedFormalArg bravo = null;
        UnTypedFormalArg bravoa = null;
        UnTypedFormalArg charlie = null;
        UnTypedFormalArg delta = null;
        UnTypedFormalArg echo = null;
        UnTypedFormalArg echoa = null;
        UnTypedFormalArg foxtrot = null;
        UnTypedFormalArg foxtrota = null;
        UnTypedFormalArg golf = null;
        UnTypedFormalArg golfa = null;
        UnTypedFormalArg hotel = null;
        UnTypedFormalArg hotela = null;
        UnTypedFormalArg india = null;
        UnTypedFormalArg juno = null;
        UnTypedFormalArg kilo = null;
        UnTypedFormalArg lima = null;
        FloatFormalArg mike = null;
        FloatFormalArg mikea = null;
        UnTypedFormalArg nero = null;
        UnTypedFormalArg oscar = null;
        UnTypedFormalArg papa = null;
        NominalFormalArg quebec = null;
        FormalArgument m0_ord = null;
        FormalArgument m0_onset = null;
        FormalArgument m0_offset = null;
        FormalArgument m1_ord = null;
        FormalArgument m1_onset = null;
        FormalArgument m1_offset = null;
        FormalArgument m1a_ord = null;
        FormalArgument m1a_onset = null;
        FormalArgument m1a_offset = null;
        FormalArgument m2_ord = null;
        FormalArgument m2_onset = null;
        FormalArgument m2_offset = null;
        FormalArgument m2a_ord = null;
        FormalArgument m2a_onset = null;
        FormalArgument m2a_offset = null;
        FormalArgument m3_ord = null;
        FormalArgument m3_onset = null;
        FormalArgument m3_offset = null;
        FormalArgument m3a_ord = null;
        FormalArgument m3a_onset = null;
        FormalArgument m3a_offset = null;
        FormalArgument cp_alpha = null;
        FormalArgument cp_bravo = null;
        FormalArgument cp_charlie = null;
        FormalArgument cp_delta = null;
        FormalArgument cp_echo = null;
        FormalArgument cp_echoa = null;
        FormalArgument cp_foxtrot = null;
        FormalArgument cp_foxtrota = null;
        FormalArgument cp_golf = null;
        FormalArgument cp_hotel = null;
        FormalArgument cp_hotela = null;
        FormalArgument cp_india = null;
        FormalArgument cp_juno = null;
        FormalArgument cp_kilo = null;
        FormalArgument cp_lima = null;
        FormalArgument cp_mike = null;
        FormalArgument cp_mikea = null;
        FormalArgument cp_nero = null;
        FormalArgument cp_oscar = null;
        FormalArgument cp_papa = null;
        FormalArgument cp_quebec = null;
        FormalArgument cp_reno = null;
        FormalArgument cp_sierra = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        /* Start by allocating the vocab list and database that we will be
         * using in the test.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            db = null;
            vl = null;
            systemErrorExceptionString = null;

            try
            {
                db = new ODBCDatabase();
                another_db = new ODBCDatabase();
                /* For test purposes, use the vl allocated as part of the db .
                 * This will prevent a bunch of sanity check failures.
                 */
                vl = db.vl;
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
                 ( vl == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "db & vl allocations failed to complete.\n");
                    }

                    if ( ( db == null ) || ( another_db == null ) )
                    {
                        outStream.print(
                                "new ODBCDatabase() returned null.\n");
                    }

                    if ( vl == null )
                    {
                        outStream.print(
                                "vl not allocated with db?!?!?l.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("db & vl allocations threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Allocate a bunch of vocab elements.  These are just
         * convenient instances for use in testing.
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
            nero    = null;
            oscar   = null;
            papa    = null;
            quebec  = null;
            m0      = null;
            m1      = null;
            m2      = null;
            m3      = null;
            p0      = null;
            p1      = null;
            p2      = null;
            p3      = null;
            p3dup   = null;
            p4      = null;
            p5      = null;
            p6      = null;
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
                kilo    = new UnTypedFormalArg(db, "<kilo>");
                lima    = new UnTypedFormalArg(db, "<lima>");
                mike    = new FloatFormalArg(db, "<mike>");
                nero    = new UnTypedFormalArg(db, "<nero>");
                oscar   = new UnTypedFormalArg(db, "<oscar>");
                papa    = new UnTypedFormalArg(another_db, "<papa>");
                quebec  = new NominalFormalArg(db, "<quebec>");

                p0 = ConstructTestPred(db, "p0", alpha, null, null, null);
                p1 = ConstructTestPred(db, "p1", bravo, charlie, null, null);
                p2 = ConstructTestPred(db, "p2", null, null, null, null);
                p3 = ConstructTestPred(db, "p3", india, null, null, null);
                p3dup = ConstructTestPred(db, "p3", null, null, null, null);
                p4 = ConstructTestPred(another_db, "p4", papa, null, null, null);
                p5 = ConstructTestPred(another_db, "p5", null, null, null, null);
                p6 = ConstructTestPred(db, "p6", null, null, null, null);

                m0 = ConstructTestMatrix(db, "m0",
                                         MatrixVocabElement.MatrixType.MATRIX,
                                         delta, null, null, null);
                m1 = ConstructTestMatrix(db, "m1",
                                         MatrixVocabElement.MatrixType.MATRIX,
                                         echo, foxtrot, hotel, null);
                m2 = ConstructTestMatrix(db, "m2",
                                         MatrixVocabElement.MatrixType.MATRIX,
                                         juno, kilo, lima, null);
                m3 = ConstructTestMatrix(db, "m3",
                                         MatrixVocabElement.MatrixType.FLOAT,
                                         mike, null, null, null);


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
                 ( juno == null ) || ( kilo == null ) ||
                 ( lima == null ) || ( mike == null ) ||
                 ( nero == null ) || ( oscar == null ) ||
                 ( papa == null ) || ( quebec == null ) ||
                 ( p0 == null ) || ( p1 == null ) ||
                 ( p2 == null ) || ( p3 == null ) ||
                 ( p4 == null ) || ( p5 == null ) ||
                 ( p6 == null ) ||
                 ( m0 == null ) || ( m1 == null ) ||
                 ( m2 == null ) || ( m3 == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("Allocations failed to complete.\n");
                    }

                    if ( ( alpha == null ) || ( bravo == null ) ||
                         ( charlie == null ) || ( delta == null ) ||
                         ( echo == null ) || ( foxtrot == null ) ||
                         ( hotel == null ) || ( india == null ) ||
                         ( juno == null ) || ( kilo == null ) ||
                         ( lima == null ) || ( mike == null ) ||
                         ( nero == null ) || ( oscar == null ) ||
                         ( papa == null ) )
                    {
                        outStream.print(
                                "one or more formal arg allocations failed.\n");
                    }

                    if ( ( p0 == null ) || ( p1 == null ) ||
                         ( p2 == null ) || ( p3 == null ) ||
                         ( p4 == null ) || ( p5 == null ) ||
                         ( p6 == null ) )

                    {
                        outStream.print(
                                "one or more pred ve allocations failed.\n");
                    }

                    if ( ( m0 == null ) || ( m1 == null ) ||
                         ( m2 == null ) || ( m3 == null ) )
                    {
                        outStream.print(
                                "one or more matrix ve allocations failed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Now try to add several vocab elements to the vocab list, and
         * verify that they are there.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl.addElement(p0);
                vl.addElement(m0);
                vl.addElement(p1);
                vl.addElement(m1);
                vl.addElement(p2);

                m0_ord = m0.getCPFormalArg(0);
                m0_onset = m0.getCPFormalArg(1);
                m0_offset = m0.getCPFormalArg(2);
                cp_delta = m0.getCPFormalArg(3);

                m1_ord = m1.getCPFormalArg(0);
                m1_onset = m1.getCPFormalArg(1);
                m1_offset = m1.getCPFormalArg(2);
                cp_echo = m1.getCPFormalArg(3);
                cp_foxtrot = m1.getCPFormalArg(4);
                cp_hotel = m1.getCPFormalArg(5);

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
                        outStream.print("Calls to vl.addElement() failed " +
                                        "to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("vl.addElement() threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {1, 3, 9, 12, 22};
            VocabElement values[] = {p0, m0, p1, m1, p2};
            long idxKeys[] = { 1,  2,
                               3,  4,  5,  6,  7,  8,
                               9, 10, 11,
                              12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                              22};
            DBElement idxValues[] =
                    {p0, alpha,
                     m0, delta, m0_ord, m0_onset, m0_offset, cp_delta,
                     p1, bravo, charlie,
                     m1, echo, foxtrot, hotel, m1_ord, m1_onset, m1_offset,
                                               cp_echo, cp_foxtrot, cp_hotel,
                     p2};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 1) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(22, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 1) )
            {
                failures++;
            }
        }

        /* Now delete several entries from the vocab list, and see if we get the
         * expected results.
         *
         * Note that we remove one entry from either end, and one from somewhere
         * near the middle.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl.removeVocabElement(3);
                vl.removeVocabElement(22);
                vl.removeVocabElement(1);
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
                        outStream.print("Calls to vl.removeVocabElement() " +
                                        "failed to complete.\n");
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
            long keys[] = {9, 12};
            VocabElement values[] = {p1, m1};
            long idxKeys[] = { 9, 10, 11,
                              12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
            DBElement idxValues[] =
                    {p1, bravo, charlie,
                     m1, echo, foxtrot, hotel, m1_ord, m1_onset, m1_offset,
                                               cp_echo, cp_foxtrot, cp_hotel};

            if ( ! VerifyVLContents(2, keys, values, vl, outStream,
                                    verbose, 2) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(13, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 2) )
            {
                failures++;
            }
        }

        /* Now add three more entries just to bring us back up to 5.
         * In passing, verify that inVocabList() and getVocabElement()
         * work as they should with valid input.  Also verify that the
         * matrix and predicate specific versions work correctly with
         * valid input.
         */
        if ( failures == 0 )
        {
            inVL0 = true;
            inVL1 = false;
            inVL2 = true;
            inVL3 = false;
            mInVL0 = true;
            mInVL1 = true;
            mInVL2 = true;
            mInVL3 = true;
            mInVL4 = false;
            mInVL5 = false;
            pInVL0 = true;
            pInVL1 = false;
            pInVL2 = true;
            pInVL3 = false;
            pInVL4 = true;
            pInVL5 = true;
            ve0 = null;
            ve1 = null;
            ve2 = null;
            ve3 = null;
            ve4 = null;
            ve5 = null;
            ve6 = null;
            ve7 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl.addElement(m2);

                m2_ord = m2.getCPFormalArg(0);
                m2_onset = m2.getCPFormalArg(1);
                m2_offset = m2.getCPFormalArg(2);
                cp_juno = m2.getCPFormalArg(3);
                cp_kilo = m2.getCPFormalArg(4);
                cp_lima = m2.getCPFormalArg(5);

                inVL0 = vl.inVocabList(1);
                inVL1 = vl.inVocabList(9);
                inVL2 = vl.inVocabList("p0");
                inVL3 = vl.inVocabList("p1");
                mInVL0 = vl.matrixInVocabList(1);
                mInVL1 = vl.matrixInVocabList(9);
                mInVL2 = vl.matrixInVocabList("p0");
                mInVL3 = vl.matrixInVocabList("p1");
                mInVL4 = vl.matrixInVocabList(12);
                mInVL5 = vl.matrixInVocabList("m1");
                pInVL0 = vl.predInVocabList(1);
                pInVL1 = vl.predInVocabList(9);
                pInVL2 = vl.predInVocabList("p0");
                pInVL3 = vl.predInVocabList("p1");
                pInVL4 = vl.predInVocabList(12);
                pInVL5 = vl.predInVocabList("m1");

                vl.addElement(m3);

                m3_ord = m3.getCPFormalArg(0);
                m3_onset = m3.getCPFormalArg(1);
                m3_offset = m3.getCPFormalArg(2);
                cp_mike = m3.getCPFormalArg(3);

                ve0 = vl.getVocabElement(12);
                ve1 = vl.getVocabElement("m1");

                vl.addElement(p3);

                ve2 = vl.getVocabElement(9);
                ve3 = vl.getVocabElement("p1");

                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( inVL0 != false ) || ( inVL1 != true ) ||
                 ( inVL2 != false ) || ( inVL3 != true ) ||
                 ( mInVL0 != false ) || ( mInVL1 != false ) ||
                 ( mInVL2 != false ) || ( mInVL3 != false ) ||
                 ( mInVL4 != true ) || ( mInVL5 != true ) ||
                 ( pInVL0 != false ) || ( pInVL1 != true ) ||
                 ( pInVL2 != false ) || ( pInVL3 != true ) ||
                 ( pInVL4 != false ) || ( pInVL5 != false ) ||
                 ( ve0 != m1 ) || ( ve1 != m1 ) ||
                 ( ve2 != p1 ) || ( ve3 != p1 ) ||
                 ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( inVL0 != false )
                    {
                        outStream.print("vl.inVocabList(1) returned true.\n");
                    }

                    if ( inVL1 != true )
                    {
                        outStream.print("vl.inVocabList(9) returned false.\n");
                    }

                    if ( inVL2 != false )
                    {
                        outStream.print(
                                "vl.inVocabList(\"p0\") returned true.\n");
                    }

                    if ( inVL3 != true )
                    {
                        outStream.print(
                                "vl.inVocabList(\"p1\") returned false.\n");
                    }


                    if ( mInVL0 != false )
                    {
                        outStream.print(
                                "vl.matrixInVocabList(1) returned true.\n");
                    }

                    if ( mInVL1 != false )
                    {
                        outStream.print(
                                "vl.matrixInVocabList(9) returned true.\n");
                    }

                    if ( mInVL2 != false )
                    {
                        outStream.print(
                                "vl.matrixInVocabList(\"p0\") returned true.\n");
                    }

                    if ( mInVL3 != false )
                    {
                        outStream.print(
                                "vl.matrixInVocabList(\"p1\") returned true.\n");
                    }

                    if ( mInVL4 != true )
                    {
                        outStream.print(
                                "vl.matrixInVocabList(12) returned false.\n");
                    }

                    if ( mInVL5 != true )
                    {
                        outStream.print(
                                "vl.matrixInVocabList(\"m1\") returned false.\n");
                    }


                    if ( pInVL0 != false )
                    {
                        outStream.print(
                                "vl.predInVocabList(1) returned true.\n");
                    }

                    if ( pInVL1 != true )
                    {
                        outStream.print(
                                "vl.predInVocabList(9) returned false.\n");
                    }

                    if ( pInVL2 != false )
                    {
                        outStream.print(
                                "vl.predInVocabList(\"p0\") returned true.\n");
                    }

                    if ( pInVL3 != true )
                    {
                        outStream.print(
                                "vl.predInVocabList(\"p1\") returned false.\n");
                    }

                    if ( pInVL4 != false )
                    {
                        outStream.print(
                                "vl.predInVocabList(12) returned true.\n");
                    }

                    if ( pInVL5 != false )
                    {
                        outStream.print(
                                "vl.predInVocabList(\"m1\") returned true.\n");
                    }


                    if ( ve0 != m1 )
                    {
                        outStream.print("vl.getVocabElement(12) != p1\n");
                    }

                    if ( ve1 != m1 )
                    {
                        outStream.print("vl.getVocabElement(\"m1\") != m1\n");
                    }

                    if ( ve2 != p1 )
                    {
                        outStream.print("vl.getVocabElement(9) != p1\n");
                    }

                    if ( ve3 != p1 )
                    {
                        outStream.print("vl.getVocabElement(\"p1\") != p1\n");
                    }

                    if ( ! methodReturned )
                    {
                        outStream.print("test failed to complete(1).\n");
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

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1, m1, m2, m3, p3};
            long idxKeys[] = { 9, 10, 11,
                              12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1, bravo, charlie,
                     m1, echo, foxtrot, hotel, m1_ord, m1_onset, m1_offset,
                                               cp_echo, cp_foxtrot, cp_hotel,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3, mike, m3_ord, m3_onset, m3_offset, cp_mike,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 3) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(31, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 3) )
            {
                failures++;
            }
        }

        /* Now simulate a bunch of edits to the entries on the vocab list.
         * This will be done by obtaining a copy of the entry, modifying it
         * and then replacing the old entry with the new.
         */

        if ( failures == 0 )
        {
            FormalArgument fArg;

            bravoa = null;
            p1a = null;
            inVL0 = false;
            inVL1 = false;
            inVL2 = false;
            inVL3 = false;
            inVL4 = true;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                /* modify a predicate */
                p1a = (PredicateVocabElement)(vl.getVocabElement("p1"));
                p1a = new PredicateVocabElement(p1a);
                /* add a formal arg, modify a formal arg, and delete a
                 * formal arg
                 */
                p1a.appendFormalArg(nero);
                bravoa = (UnTypedFormalArg)(p1a.copyFormalArg(0));
                bravoa.setFargName("<bravoa>");
                p1a.replaceFormalArg(bravoa, 0);
                p1a.deleteFormalArg(1);
                /* change name */
                p1a.setName("p1a");
                /* replace the current entry with the modified entry */
                vl.replaceVocabElement(p1a);
                /* verify that inVocabList() works on the revised entry */
                inVL0 = vl.inVocabList(9);
                inVL1 = vl.inVocabList("p1a");
                inVL4 = vl.inVocabList("p1");

                /* now modify a matrix */
                m1a = (MatrixVocabElement)(vl.getVocabElement(12));
                m1a = new MatrixVocabElement(m1a);
                m1a.setName("m1a");
                /* This time, just add a formal argument, and change
                 * the order of the existing arguments.
                 */
                echoa = (UnTypedFormalArg)m1a.copyFormalArg(0);
                echoa.setFargName("<echoa>");
                foxtrota = (UnTypedFormalArg)m1a.getFormalArg(1);
                hotela = (UnTypedFormalArg)m1a.getFormalArg(2);
                /* make echo the second formal argument */
                m1a.deleteFormalArg(0);
                m1a.insertFormalArg(echoa, 1);
                /* Insert oscar at the head of the formal argument list */
                m1a.insertFormalArg(oscar, 0);
                /* replace the current entry with the modified entry */
                vl.replaceVocabElement(m1a);
                m1a_ord = m1a.getCPFormalArg(0);
                m1a_onset = m1a.getCPFormalArg(1);
                m1a_offset = m1a.getCPFormalArg(2);
                cp_oscar = m1a.getCPFormalArg(3);
                cp_foxtrota = m1a.getCPFormalArg(4);
                cp_echoa = m1a.getCPFormalArg(5);
                cp_hotela = m1a.getCPFormalArg(6);

                /* Modify a Float matrix */
                m3a = (MatrixVocabElement)vl.getVocabElement(33);
                m3a = new MatrixVocabElement(m3a);
                mikea = (FloatFormalArg)m3a.copyFormalArg(0);
                mikea.setFargName("<mikea>");
                m3a.replaceFormalArg(mikea, 0);
                /* replace the current entry with the modified entry */
                vl.replaceVocabElement(m3a);
                m3a_ord = m3a.getCPFormalArg(0);
                m3a_onset = m3a.getCPFormalArg(1);
                m3a_offset = m3a.getCPFormalArg(2);
                cp_mikea = m3a.getCPFormalArg(3);


                /* verify that the modified version of m3 is detectable by
                 * inVocabList()
                 */
                inVL2 = vl.inVocabList(39);
                inVL3 = vl.inVocabList("m3");

                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( p1a == null ) || ( p1a == p1 ) ||
                 ( bravoa == null ) || ( bravoa == bravo ) ||
                 ( inVL0 == false ) || ( inVL1 == false ) ||
                 ( inVL2 == false ) || ( inVL3 == false ) ||
                 ( inVL4 == true ) || ( ! methodReturned ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ( p1a == null ) || ( p1a == p1 ) )
                    {
                        outStream.print("p1a allocation failed.\n");
                    }

                    if ( ( bravoa == null ) || ( bravoa == bravo ) )
                    {
                        outStream.print("bravoa allocation failed.\n");
                    }

                    if ( ( inVL0 == false ) || ( inVL1 == false ) ||
                         ( inVL2 == false ) || ( inVL3 == false ) ||
                         ( inVL4 == true ) )
                    {
                        outStream.print("Bad inVocabList() result(s).\n");
                    }

                    if ( ! methodReturned )
                    {
                        outStream.print("test failed to complete(2).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("unexpected system error " +
                                "exception(2): \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 4) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 4) )
            {
                failures++;
            }
        }


        /* At this point we have tested functionality with valid input.
         *
         * Start by verifying that addElement() generates the expected
         * errors.
         */

        /* Start by trying to insert a VocabElement whose ID has already been
         * defined.
         *
         * p0's id was set the first time we inserted it, so we will use
         * it as a test element.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl.addElement(p0);
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
                        outStream.print("Call to vl.addElement() with bad " +
                                        "id completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.addElement(bad id) failed to " +
                                "throw a system error exception:.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 5) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 5) )
            {
                failures++;
            }
        }


        /* Now try to add a VocabElement with a database reference that doesn't
         * match that of the index.
         */

        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl.addElement(p4);
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
                        outStream.print("Call to vl.addElement() with bad " +
                                        "db completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.addElement(bad db) failed to " +
                                "throw a system error exception:.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 6) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 6) )
            {
                failures++;
            }
        }


        /* Now try to add a vocab element to the vocab list that is already
         * in the vocab list.
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
                old_id = DBElementTest.ResetID(p3);
                vl.addElement(p3);
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
                        outStream.print("Call to vl.addElement() with ve " +
                                        "already in vl completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.addElement(bad ve) failed to " +
                                "throw a system error exception:.\n");
                    }
                }
            }
            else /* fix up p3's ID so we dont' confuse the index */
            {
                methodReturned = false;
                threwSystemErrorException = false;

                try
                {
                    p3.setID(old_id);
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
                            outStream.print("p3.setID() failed to complete.\n");
                        }

                        if ( threwSystemErrorException )
                        {
                            outStream.printf("p3.setID() threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                        }
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 7) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 7) )
            {
                failures++;
            }
        }

        /* Now pass a null to addElement().  This should fail with a
         * system error.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl.addElement(null);
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
                        outStream.print("Call to vl.addElement(null) " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.addElement(null) failed to throw " +
                                        "a system error exception:.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 8) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 8) )
            {
                failures++;
            }
        }

        /* Try to add an element with a name that is already in use. */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl.addElement(p3dup);
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
                        outStream.print("Call to vl.addElement() with name " +
                                        "in use completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.addElement(name in use) failed " +
                                "to throw a system error exception:.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 9) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 9) )
            {
                failures++;
            }
        }


        /*
         * Next, verify that getElement() fails as expected.
         */
        /* Start by verifying that getVocabElement fails when passed the
         * invalid ID.
         */
        if ( failures == 0 )
        {
            ve0 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = vl.getVocabElement(DBIndex.INVALID_ID);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ve0 != null ) ||
                 ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ( ve0 != null ) || ( methodReturned ) )
                    {
                        outStream.print("Call to vl.getVocabElement" +
                                        "(INVALID_ID) completed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.print("vl.getElement(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 10) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 10) )
            {
                failures++;
            }
        }


        /* Likewise verify that calling getVocabElement with an ID that is
         * not in the vocab list will generate a system error.
         */
        if ( failures == 0 )
        {
            ve0 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = vl.getVocabElement(1);
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
                        outStream.print("Call to vl.getVocabElement(1) " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.getVocabElement(1) failed " +
                                        "to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 11) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 11) )
            {
                failures++;
            }
        }


        /* Also verify that calling getVocabElement with a null vocab element
         * name, an empty name, and invalid name, or a vocab element name
         * that is not in the vocab list will generate a system error.
         */

        /* pass getVocabElement() a null */
        if ( failures == 0 )
        {
            ve0 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = vl.getVocabElement(null);
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
                        outStream.print("Call to vl.getVocabElement(null) " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.getVocabElement(null) failed " +
                                        "to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 12) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 12) )
            {
                failures++;
            }
        }

        /* pass getVocabElement() the empty name */
        if ( failures == 0 )
        {
            ve0 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = vl.getVocabElement("");
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
                        outStream.print("Call to vl.getVocabElement(\"\") " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.getVocabElement(\"\") failed " +
                                        "to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 13) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 13) )
            {
                failures++;
            }
        }


        /* pass getVocabElement() an invalid name */
        if ( failures == 0 )
        {
            ve0 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = vl.getVocabElement("<invalid>");
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
                        outStream.print("Call to vl.getVocabElement" +
                                        "(\"<invalid>\") completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.getVocabElement(\"<invalid>\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 14) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 14) )
            {
                failures++;
            }
        }

        /* pass getVocabElement() a name that isn't in the vocab list */
        if ( failures == 0 )
        {
            ve0 = null;
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                ve0 = vl.getVocabElement("p0");
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
                        outStream.print("Call to vl.getVocabElement(\"p0\") " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.getVocabElement(\"p0\") failed " +
                                        "to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 15) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 15) )
            {
                failures++;
            }
        }


        /*
         * Next, verify that inVocabList() fails where expected.  This is
         * pretty easy, as the only ways inVocabList() should fail are if you
         * pass it the INVALID_ID, null, the empty string, or an invalid name.
         */
        if ( failures == 0 )
        {
            boolean isInIndex = false;

            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                isInIndex = vl.inVocabList(DBIndex.INVALID_ID);
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
                        outStream.print("Call to vl.inVocabList(INVALID_ID) " +
                                        "returned true.\n");
                    }

                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.inVocabList(INVALID_ID) " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.inVocabList(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 16) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 16) )
            {
                failures++;
            }
        }

        /* pass inVocabList() a null */
        if ( failures == 0 )
        {
            boolean isInIndex = false;

            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                isInIndex = vl.inVocabList(null);
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
                        outStream.print("Call to vl.inVocabList(null) " +
                                        "returned true.\n");
                    }

                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.inVocabList(null) " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.inVocabList(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 17) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 17) )
            {
                failures++;
            }
        }

        /* pass inVocabList() the empty string */
        if ( failures == 0 )
        {
            boolean isInIndex = false;

            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                isInIndex = vl.inVocabList("");
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
                        outStream.print("Call to vl.inVocabList(\"\") " +
                                        "returned true.\n");
                    }

                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.inVocabList(\"\") " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.inVocabList(\"\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 18) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 18) )
            {
                failures++;
            }
        }

        /* pass inVocabList() an invalid name */
        if ( failures == 0 )
        {
            boolean isInIndex = false;

            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                isInIndex = vl.inVocabList("<invalid>");
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
                        outStream.print("Call to vl.inVocabList" +
                                        "(\"<invalid>\") returned true.\n");
                    }

                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.inVocabList" +
                                        "(\"<invalid>\") completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.inVocabList(\"<invalid>\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 19) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 19) )
            {
                failures++;
            }
        }


        /*
         * Next, verify that matrixInVocabList() fails where expected.  This is
         * pretty easy, as the only ways matrixInVocabList() should fail are if
         * you pass it the INVALID_ID, null, the empty string, or an invalid
         * name.
         */
        if ( failures == 0 )
        {
            boolean isInIndex = false;

            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                isInIndex = vl.matrixInVocabList(DBIndex.INVALID_ID);
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
                        outStream.print("Call to vl.matrixInVocabList" +
                                        "(INVALID_ID) returned true.\n");
                    }

                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.matrixInVocabList" +
                                        "(INVALID_ID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.matrixInVocabList(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 20) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 20) )
            {
                failures++;
            }
        }

        /* pass matrixInVocabList() a null */
        if ( failures == 0 )
        {
            boolean isInIndex = false;

            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                isInIndex = vl.matrixInVocabList(null);
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
                        outStream.print("Call to vl.matrixInVocabList(null) " +
                                        "returned true.\n");
                    }

                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.matrixInVocabList(null) " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.matrixInVocabList(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 21) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 21) )
            {
                failures++;
            }
        }

        /* pass matrixInVocabList() the empty string */
        if ( failures == 0 )
        {
            boolean isInIndex = false;

            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                isInIndex = vl.matrixInVocabList("");
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
                        outStream.print("Call to vl.matrixInVocabList(\"\") " +
                                        "returned true.\n");
                    }

                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.matrixInVocabList(\"\") " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.matrixInVocabList(\"\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 22) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 22) )
            {
                failures++;
            }
        }

        /* pass matrixInVocabList() an invalid name */
        if ( failures == 0 )
        {
            boolean isInIndex = false;

            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                isInIndex = vl.matrixInVocabList("<invalid>");
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
                        outStream.print("Call to vl.matrixInVocabList" +
                                        "(\"<invalid>\") returned true.\n");
                    }

                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.matrixInVocabList" +
                                        "(\"<invalid>\") completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.matrixInVocabList(\"<invalid>\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 23) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 23) )
            {
                failures++;
            }
        }


        /*
         * Next, verify that predInVocabList() fails where expected.  This is
         * pretty easy, as the only ways predInVocabList() should fail are if
         * you pass it the INVALID_ID, null, the empty string, or an invalid
         * name.
         */
        if ( failures == 0 )
        {
            boolean isInIndex = false;

            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                isInIndex = vl.predInVocabList(DBIndex.INVALID_ID);
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
                        outStream.print("Call to vl.predInVocabList" +
                                        "(INVALID_ID) returned true.\n");
                    }

                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.predInVocabList" +
                                        "(INVALID_ID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.predInVocabList(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 24) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 24) )
            {
                failures++;
            }
        }

        /* pass predInVocabList() a null */
        if ( failures == 0 )
        {
            boolean isInIndex = false;

            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                isInIndex = vl.predInVocabList(null);
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
                        outStream.print("Call to vl.predInVocabList(null) " +
                                        "returned true.\n");
                    }

                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.predInVocabList(null) " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.predInVocabList(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 25) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 25) )
            {
                failures++;
            }
        }

        /* pass predInVocabList() the empty string */
        if ( failures == 0 )
        {
            boolean isInIndex = false;

            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                isInIndex = vl.predInVocabList("");
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
                        outStream.print("Call to vl.predInVocabList(\"\") " +
                                        "returned true.\n");
                    }

                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.predInVocabList(\"\") " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.predInVocabList(\"\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 26) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 26) )
            {
                failures++;
            }
        }

        /* pass predInVocabList() an invalid name */
        if ( failures == 0 )
        {
            boolean isInIndex = false;

            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                isInIndex = vl.predInVocabList("<invalid>");
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
                        outStream.print("Call to vl.predInVocabList" +
                                        "(\"<invalid>\") returned true.\n");
                    }

                    if ( methodReturned )
                    {
                        outStream.print("Call to vl.predInVocabList" +
                                        "(\"<invalid>\") completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.predInVocabList(\"<invalid>\") " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 27) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 27) )
            {
                failures++;
            }
        }


        /*
         * Next, verify that removeVocabElement() in the expected places.
         */
        /* Start by feeding removeVocabElement the INVALID_ID */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl.removeVocabElement(DBIndex.INVALID_ID);
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
                        outStream.print("Call to vl.removeVocabElement" +
                                        "(INVALID_ID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.removeVocabElement(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 28) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 28) )
            {
                failures++;
            }
        }


        /* now try to remove a non-existant element.  Note that the method
         * should also fail if the target element isn't in the vocab list.
         * However we test to see if the ID exists in the vocab list first,
         * and thus this error will only appear if there is a bug in the
         * hash table.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl.removeVocabElement(1);
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
                        outStream.print("Call to vl.removeVocabElement(1) " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.removeElement(1) failed to " +
                                        "throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 29) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 29) )
            {
                failures++;
            }
        }

        /* now try to remove an element with a formal argument that isnt
         * in the index.  Set this up by adding a formal argument to p1a.
         * To avoid confusing the database, we will have to remove it when
         * we are done.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                p1a.appendFormalArg(alpha);
                vl.removeVocabElement(5);
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
                        outStream.print("Call to vl.removeVocabElement(5) " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.removeElement(5) failed to " +
                                        "throw a system error exception.\n");
                    }
                }
            }
            else /* tidy up */
            {
                methodReturned = false;
                threwSystemErrorException = false;
                systemErrorExceptionString = null;

                try
                {
                    p1a.deleteFormalArg(2);
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
                            outStream.print("p1a.deleteFormalArg(2) failed " +
                                            "to complete.\n");
                        }
                        if ( threwSystemErrorException )
                        {
                            outStream.printf("Unexpected system error in " +
                                    "tidy after removeElement test: %s\n",
                                    systemErrorExceptionString);
                        }
                    }

                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 30) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 30) )
            {
                failures++;
            }
        }


        /*
         * Finally, verify that replaceVocabElement fails in the expected places.
         */
        /* Start by feeding it a null ve */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl.replaceVocabElement(null);
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
                        outStream.print("Call to vl.replaceVocabElement(null) " +
                                        "completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.replaceVocabElement(null) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 31) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 31) )
            {
                failures++;
            }
        }


        /* Next, feed replaceVocabElement a DBElement with a db field that
         * doesn't match that of vl.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                p5.setID(5);
                vl.replaceVocabElement(p5);
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
                        outStream.print("Call to vl.replaceVocabElement" +
                                        "(bad db) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.replaceVocabElement(bad db) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 32) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 32) )
            {
                failures++;
            }
        }


        /* Next, feed replaceVocabElement a VocabElement with a id field set to
         * INVALID_ID.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                vl.replaceVocabElement(p6);
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
                        outStream.print("Call to vl.replaceVocabElement" +
                                "(INVALID_ID) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.replaceVocabElement(INVALID_ID) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 33) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 33) )
            {
                failures++;
            }
        }


        /* next, try to replace an element that isn't in the vocab list */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                p6.setID(1);
                vl.replaceVocabElement(p6);
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
                        outStream.print("Call to vl.replaceVocabElement" +
                                "(no_such_id) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.replaceVocabElement(no_such_id) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 34) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 34) )
            {
                failures++;
            }
        }


        /* Try to replace an vocab list entry with an VocabElement
         * of a different sub-class.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                p6.setID(m3a.getID());
                vl.replaceVocabElement(p6);
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
                        outStream.print("Call to vl.replaceVocabElement" +
                                "(type mismatch) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.replaceVocabElement(type mismatch) " +
                                "failed to throw a system error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 35) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 35) )
            {
                failures++;
            }
        }

        /* Finally, try to replace a formal argument in a vocab element
         * with a formal argument of a different subcless.  This should
         * throw a system error.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                m2a = (MatrixVocabElement)vl.getVocabElement(13);
                m2a = new MatrixVocabElement(m2a);
                quebec.setID(m2a.getFormalArg(1).getID());
                m2a.replaceFormalArg(quebec, 1);
                vl.replaceVocabElement(m2a);
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
                        outStream.print("Call to vl.replaceVocabElement" +
                                "(farg type mismatch) completed.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("vl.replaceVocabElement(farg type " +
                                        "mismatch) failed to throw a system " +
                                        "error exception.\n");
                    }
                }
            }
        }

        if ( failures == 0 )
        {
            long keys[] = {9, 12, 23, 33, 39};
            VocabElement values[] = {p1a, m1a, m2, m3a, p3};
            long idxKeys[] = { 9, 10, 41,
                              12, 13, 14, 15, 16, 17, 18, 44, 20, 21, 42, 43,
                              23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                              33, 34, 35, 36, 37, 38,
                              39, 40};
            DBElement idxValues[] =
                    {p1a, bravoa, nero,
                     m1a, echoa, foxtrota, hotela,
                         m1a_ord, m1a_onset, m1a_offset,
                         cp_echoa, cp_foxtrota, cp_hotela, oscar, cp_oscar,
                     m2, juno, kilo, lima, m2_ord, m2_onset, m2_offset,
                                           cp_juno, cp_kilo, cp_lima,
                     m3a, mikea, m3a_ord, m3a_onset, m3a_offset, cp_mikea,
                     p3, india};

            if ( ! VerifyVLContents(5, keys, values, vl, outStream,
                                    verbose, 36) )
            {
                failures++;
            }

            if ( ! DBIndexTest.VerifyIndexContents(33, idxKeys, idxValues,
                                               vl.db.idx, outStream,
                                               verbose, 36) )
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

    } /* DBIndex::TestVLManagement() */


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
        final String expectedVLString0 = "((VocabList) (vl_contents: ()))";
        final String expectedVLString1 =
                "((VocabList) " +
                 "(vl_contents: " +
                  "(p1(<bravo>, <charlie>), " +
                   "m0(<val>), " +
                   "m2(<india>), " +
                   "m1(<echo>, <foxtrot>, <hotel>), " +
                   "p0(<alpha>), " +
                   "p2())))";
        final String expectedIDXString0 = "((DBIndex) (index_contents: ()))";
        final String expectedIDXString1 =
                "((DBIndex) " +
                 "(index_contents: " +
                  "(<india>, " +
                   "<offset>, " +
                   "<onset>, " +
                   "<ord>, " +
                   "<india>, " +
                   "m2(<india>), " +
                   "p2(), " +
                   "<hotel>, " +
                   "<foxtrot>, " +
                   "<echo>, " +
                   "<offset>, " +
                   "<onset>, " +
                   "<ord>, " +
                   "<hotel>, " +
                   "<foxtrot>, " +
                   "<echo>, " +
                   "m1(<echo>, <foxtrot>, <hotel>), " +
                   "<charlie>, " +
                   "<bravo>, " +
                   "p1(<bravo>, <charlie>), " +
                   "<val>, " +
                   "<offset>, " +
                   "<onset>, " +
                   "<ord>, " +
                   "<val>, " +
                   "m0(<val>), " +
                   "<alpha>, " +
                   "p0(<alpha>))))";
        final String expectedVLDBString0 =
                "((VocabList) (vl_size: 0) (vl_contents: ()))";
        final String expectedVLDBString1 =
                  "((VocabList) " +
                    "(vl_size: 6) " +
                    "(vl_contents: " +
                     "(((PredicateVocabElement: 9 p1) " +
                       "(system: false) " +
                       "(varLen: false) " +
                       "(fArgList: " +
                        "((IntFormalArg 10 <bravo> false " +
                          "-9223372036854775808 9223372036854775807), " +
                         "(NominalFormalArg 11 <charlie> false ()))), " +
                      "((MatrixVocabElement: 3 m0) " +
                       "(system: false) " +
                       "(type: TEXT) " +
                       "(varLen: false) " +
                       "(fArgList: " +
                        "((TextStringFormalArg 4 <val>))), " +
                      "((MatrixVocabElement: 23 m2) " +
                       "(system: false) " +
                       "(type: NOMINAL) " +
                       "(varLen: false) " +
                       "(fArgList: " +
                        "((NominalFormalArg 24 <india> false ()))), " +
                      "((MatrixVocabElement: 12 m1) " +
                       "(system: false) " +
                       "(type: MATRIX) " +
                       "(varLen: false) " +
                       "(fArgList: " +
                        "((TimeStampFormalArg 13 <echo> false null null), " +
                         "(NominalFormalArg 14 <foxtrot> false ()), " +
                         "(UnTypedFormalArg 15 <hotel>))), " +
                      "((PredicateVocabElement: 1 p0) " +
                       "(system: false) " +
                       "(varLen: false) " +
                       "(fArgList: " +
                        "((FloatFormalArg 2 <alpha> false " +
                         "-1.7976931348623157E308 1.7976931348623157E308))), " +
                      "((PredicateVocabElement: 22 p2) " +
                       "(system: false) " +
                       "(varLen: false) " +
                       "(fArgList: ()))))";
        final String expectedIDXDBString0 =
                "((DBIndex) (nextID: 1) (index_size: 0) (index_contents: ()))";
        final String expectedIDXDBString1 =
                "((DBIndex) " +
                  "(nextID: 29) " +
                  "(index_size: 28) " +
                  "(index_contents: " +
                   "((NominalFormalArg 28 <india> false ()), " +
                    "(TimeStampFormalArg 27 <offset> false null null), " +
                    "(TimeStampFormalArg 26 <onset> false null null), " +
                    "(IntFormalArg 25 <ord> false -9223372036854775808 9223372036854775807), " +
                    "(NominalFormalArg 24 <india> false ()), " +
                    "((MatrixVocabElement: 23 m2) " +
                     "(system: false) " +
                     "(type: NOMINAL) " +
                     "(varLen: false) " +
                     "(fArgList: ((NominalFormalArg 24 <india> false ()))), " +
                    "((PredicateVocabElement: 22 p2) " +
                     "(system: false) " +
                     "(varLen: false) " +
                     "(fArgList: ()), " +
                    "(UnTypedFormalArg 21 <hotel>), " +
                    "(NominalFormalArg 20 <foxtrot> false ()), " +
                    "(TimeStampFormalArg 19 <echo> false null null), " +
                    "(TimeStampFormalArg 18 <offset> false null null), " +
                    "(TimeStampFormalArg 17 <onset> false null null), " +
                    "(IntFormalArg 16 <ord> false -9223372036854775808 9223372036854775807), " +
                    "(UnTypedFormalArg 15 <hotel>), " +
                    "(NominalFormalArg 14 <foxtrot> false ()), " +
                    "(TimeStampFormalArg 13 <echo> false null null), " +
                    "((MatrixVocabElement: 12 m1) " +
                     "(system: false) " +
                     "(type: MATRIX) " +
                     "(varLen: false) " +
                     "(fArgList: " +
                      "((TimeStampFormalArg 13 <echo> false null null), " +
                       "(NominalFormalArg 14 <foxtrot> false ()), " +
                       "(UnTypedFormalArg 15 <hotel>))), " +
                     "(NominalFormalArg 11 <charlie> false ()), " +
                     "(IntFormalArg 10 <bravo> false -9223372036854775808 9223372036854775807), " +
                     "((PredicateVocabElement: 9 p1) " +
                      "(system: false) " +
                      "(varLen: false) " +
                      "(fArgList: " +
                       "((IntFormalArg 10 <bravo> false -9223372036854775808 9223372036854775807), " +
                        "(NominalFormalArg 11 <charlie> false ()))), " +
                      "(TextStringFormalArg 8 <val>), " +
                      "(TimeStampFormalArg 7 <offset> false null null), " +
                      "(TimeStampFormalArg 6 <onset> false null null), " +
                      "(IntFormalArg 5 <ord> false -9223372036854775808 9223372036854775807), " +
                      "(TextStringFormalArg 4 <val>), " +
                      "((MatrixVocabElement: 3 m0) " +
                       "(system: false) " +
                       "(type: TEXT) " +
                       "(varLen: false) " +
                       "(fArgList: ((TextStringFormalArg 4 <val>))), " +
                      "(FloatFormalArg 2 <alpha> false -1.7976931348623157E308 1.7976931348623157E308), " +
                      "((PredicateVocabElement: 1 p0) " +
                       "(system: false) " +
                       "(varLen: false) " +
                       "(fArgList: " +
                        "((FloatFormalArg 2 <alpha> false -1.7976931348623157E308 1.7976931348623157E308))))))";
        String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        int progress = 0;
        Database db = null;
        VocabList vl = null;
        FloatFormalArg alpha = null;
        IntFormalArg bravo = null;
        NominalFormalArg charlie = null;
        TextStringFormalArg delta = null;
        TimeStampFormalArg echo = null;
        NominalFormalArg foxtrot = null;
        NominalFormalArg golf = null;
        UnTypedFormalArg hotel = null;
        NominalFormalArg india = null;
        UnTypedFormalArg juno = null;
        PredicateVocabElement p0 = null;
        PredicateVocabElement p1 = null;
        PredicateVocabElement p2 = null;
        MatrixVocabElement m0 = null;
        MatrixVocabElement m1 = null;
        MatrixVocabElement m2 = null;

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
                vl = db.vl;
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( db == null ) ||
                 ( vl == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( db == null )
                    {
                        outStream.print("db null after setup?!?\n");
                    }

                    if ( vl == null )
                    {
                        outStream.print("vl null after setup?!?\n");
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

        /* first see if an enpty vl and index generates the expected string and
         * debug string.
         */

        if ( failures == 0 )
        {
            if ( vl.toString().compareTo(expectedVLString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("vl.toString() returned unexpected " +
                            "value(0): \"%s\".\n", vl.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( vl.toDBString().compareTo(expectedVLDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("vl.toDBString() returned unexpected " +
                            "value(0): \"%s\".\n", vl.toDBString());

                }
            }
        }

        if ( failures == 0 )
        {
            if ( db.idx.toString().compareTo(expectedIDXString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("db.idx.toString() returned unexpected " +
                            "value(0): \"%s\".\n", db.idx.toString());

                }
            }
        }

        if ( failures == 0 )
        {
            if ( db.idx.toDBString().compareTo(expectedIDXDBString0) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("db.idx.toDBString() returned unexpected " +
                            "value(0): \"%s\".\n", db.idx.toDBString());

                }
            }
        }

        /* Now allocate and insert a bunch of entries in the vocab list. */

        if ( failures == 0 )
        {
            progress = 0;
            completed = false;
            threwSystemErrorException = false;
            systemErrorExceptionString = null;

            try
            {
                alpha   = new FloatFormalArg(db, "<alpha>");
                bravo   = new IntFormalArg(db, "<bravo>");
                charlie = new NominalFormalArg(db, "<charlie>");
                delta   = new TextStringFormalArg(db);
                echo    = new TimeStampFormalArg(db, "<echo>");
                foxtrot = new NominalFormalArg(db, "<foxtrot>");
                hotel   = new UnTypedFormalArg(db, "<hotel>");
                india   = new NominalFormalArg(db, "<india>");
                juno    = new UnTypedFormalArg(db, "<juno>");

                progress++;

                p0 = ConstructTestPred(db, "p0", alpha, null, null, null);
                p1 = ConstructTestPred(db, "p1", bravo, charlie, null, null);
                p2 = ConstructTestPred(db, "p2", null, null, null, null);

                m0 = ConstructTestMatrix(db, "m0",
                                         MatrixVocabElement.MatrixType.TEXT,
                                         delta, null, null, null);
                m1 = ConstructTestMatrix(db, "m1",
                                         MatrixVocabElement.MatrixType.MATRIX,
                                         echo, foxtrot, hotel, null);
                m2 = ConstructTestMatrix(db, "m2",
                                         MatrixVocabElement.MatrixType.NOMINAL,
                                         india, null, null, null);

                progress++;

                vl.addElement(p0);
                progress++;
                vl.addElement(m0);
                progress++;
                vl.addElement(p1);
                progress++;
                vl.addElement(m1);
                progress++;
                vl.addElement(p2);
                progress++;
                vl.addElement(m2);

                progress++;

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( alpha == null ) || ( bravo == null ) ||
                 ( charlie == null ) || ( delta == null ) ||
                 ( echo == null ) || ( foxtrot == null ) ||
                 ( hotel == null ) || ( india == null ) ||
                 ( p0 == null ) || ( p1 == null ) ||
                 ( p2 == null ) ||
                 ( m0 == null ) || ( m1 == null ) ||
                 ( m2 == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("progress = %d\n", progress);

                    if ( ! completed )
                    {
                        outStream.print("Setup for strings test failed " +
                                        "to complete().\n");
                    }

                    if ( ( alpha == null ) || ( bravo == null ) ||
                         ( charlie == null ) || ( delta == null ) ||
                         ( echo == null ) || ( foxtrot == null ) ||
                         ( hotel == null ) || ( india == null ) )
                    {
                        outStream.print(
                                "one or more formal arg allocations failed.\n");
                    }

                    if ( ( p0 == null ) || ( p1 == null ) ||
                         ( p2 == null ) )

                    {
                        outStream.print(
                                "one or more pred ve allocations failed.\n");
                    }

                    if ( ( m0 == null ) || ( m1 == null ) ||
                         ( m2 == null ) )
                    {
                        outStream.print(
                                "one or more matrix ve allocations failed.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }

        /* Now test again to see if we generate the expected strings after the
         * additions to the vocab list.
         */

        if ( failures == 0 )
        {
            if ( vl.toString().compareTo(expectedVLString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("vl.toString() returned unexpected " +
                            "value(1): \"%s\".\n", vl.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( vl.toDBString().compareTo(expectedVLDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("vl.toDBString() returned unexpected " +
                            "value(1): \"%s\".\n", vl.toDBString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( db.idx.toString().compareTo(expectedIDXString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("db.idx.toString() returned unexpected " +
                            "value(1): \"%s\".\n", db.idx.toString());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( db.idx.toDBString().compareTo(expectedIDXDBString1) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("db.idx.toDBString() returned unexpected " +
                            "value(1): \"%s\".\n", db.idx.toDBString());
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

    } /* VocabList::TestToStringMethods() */


    /**
     * VerifyVectorContents()
     *
     * Verify that the supplied Vector contains the specified number of
     * elements, and that a copy of each entry in values[] is in the
     * Vector.
     *
     * Return true if this holds, and false otherwise.
     *
     * Two near identical versions of this method -- one for vectors of
     * MatrixVocabElements, and one for vectors of PredicateVocabElement.
     * This is sort of stupid, but I can't get java to let me do the
     * type casting required to avoid this.
     *
     *                                                  JRM -- 6/19/07
     *
     * Changes:
     *
     *    - none.
     */

    protected static boolean VerifyVectorContents(java.util.Vector<MatrixVocabElement> v,
                                                  int numEntries,
                                                  MatrixVocabElement values[],
                                                  java.io.PrintStream outStream,
                                                  boolean verbose,
                                                  int testNum)
        throws SystemErrorException
    {
        final String mName = "VocabList::VerifyVectorContents(matrix): ";
        String mveString = null;
        boolean matchFound = false;
        boolean verified = true; /* will set to false if necessary */
        int expected_idx_size = 0;
        int i = 0;
        int j = 0;
        MatrixVocabElement mve = null;

        if ( ( v == null ) || ( outStream == null ) )
        {
            throw new SystemErrorException(mName + "null param(s) on entry");
        }

        if ( numEntries != v.size() )
        {
            verified = false;

            if ( verbose )
            {
                outStream.printf("mtest %d: bad v size %d (%d expected).\n",
                                     testNum, v.size(), numEntries);
            }
        }

        while ( i < numEntries )
        {
            mve = values[i];
            mveString = mve.toDBString();
            j = 0;
            matchFound = false;

            while ( ( j < numEntries ) && ( ! matchFound ) )
            {
                if ( mveString.compareTo(v.get(j).toDBString()) == 0 )
                {
                    if ( mve == v.get(i) )
                    {
                        verified = false;

                        if ( verbose )
                        {
                            outStream.printf(
                                    "mtest %d:match (%d, %d) is not a copy.\n",
                                    testNum, i, j);
                        }
                    }
                    else
                    {
                        matchFound = true;
                    }
                }
                j++;
            }

            if ( ! matchFound )
            {
                verified = false;

                if ( verbose )
                {
                    outStream.printf("mtest %d: no match found for %d.\n",
                                      testNum, i);
                }
            }

            i++;
        }

        return verified;

    } /* VocabList::VerifyVectorContents(matrix) */


    protected static boolean VerifyVectorContents(java.util.Vector<PredicateVocabElement> v,
                                                  int numEntries,
                                                  PredicateVocabElement values[],
                                                  java.io.PrintStream outStream,
                                                  boolean verbose,
                                                  int testNum)
        throws SystemErrorException
    {
        final String mName = "VocabList::VerifyVectorContents(pred): ";
        String pveString = null;
        boolean matchFound = false;
        boolean verified = true; /* will set to false if necessary */
        int expected_idx_size = 0;
        int i = 0;
        int j = 0;
        PredicateVocabElement pve = null;

        if ( ( v == null ) || ( outStream == null ) )
        {
            throw new SystemErrorException(mName + "null param(s) on entry");
        }

        if ( numEntries != v.size() )
        {
            verified = false;

            if ( verbose )
            {
                outStream.printf("ptest %d: bad v size %d (%d expected).\n",
                                     testNum, v.size(), numEntries);
            }
        }

        while ( i < numEntries )
        {
            pve = values[i];
            pveString = pve.toDBString();
            j = 0;
            matchFound = false;

            while ( ( j < numEntries ) && ( ! matchFound ) )
            {
                if ( pveString.compareTo(v.get(j).toDBString()) == 0 )
                {
                    if ( pve == v.get(i) )
                    {
                        verified = false;

                        if ( verbose )
                        {
                            outStream.printf(
                                    "ptest %d:match (%d, %d) is not a copy.\n",
                                    testNum, i, j);
                        }
                    }
                    else
                    {
                        matchFound = true;
                    }
                }
                j++;
            }

            if ( ! matchFound )
            {
                verified = false;

                if ( verbose )
                {
                    outStream.printf("ptest %d: no match found for %d.\n",
                                      testNum, i);
                }
            }

            i++;
        }

        return verified;

    } /* VocabList::VerifyVectorContents(pred) */


    /**
     * VerifyVLContents()
     *
     * Verify that the supplied instance of VocabList contains the key value
     * pairs contained in the keys and values vectors, and no others.
     *
     * Also verify that the entries in the vocab list and all their associated
     * formal arguements are in the index.
     *
     * Return true if this holds, and false otherwise.
     *
     *                                                  JRM -- 5/8/07
     *
     * Changes:
     *
     *    - None.
     */

    protected static boolean VerifyVLContents(int numEntries,
                                              long keys[],
                                              VocabElement values[],
                                              VocabList vl,
                                              java.io.PrintStream outStream,
                                              boolean verbose,
                                              int testNum)
        throws SystemErrorException
    {
        final String mName = "VocabList::VerifyVLContents(): ";
        boolean verified = true; /* will set to false if necessary */
        int expected_idx_size = 0;
        int i = 0;
        int j = 0;
        MatrixVocabElement mve;

        if ( ( vl == null ) || ( outStream == null ) )
        {
            throw new SystemErrorException(mName + "null param(s) on entry");
        }

        if ( numEntries != vl.vl.size() )
        {
            verified = false;

            if ( verbose )
            {
                outStream.printf("test %d: bad vl size %d (%d expected).\n",
                                     testNum, vl.vl.size(), numEntries);
            }
        }

        while ( i < numEntries )
        {
            if ( vl.vl.get(keys[i]) != values[i] )
            {
                verified = false;

                if ( verbose )
                {
                    outStream.printf("test %d: unexpected value for key %d.\n",
                                     testNum, keys[i]);
                }
            }

            expected_idx_size++;

            /* now verify that all the formal arguments of the vocab
             * element are in the index.
             */
            for ( j = 0; j < values[i].getNumFormalArgs(); j++ )
            {
                if ( values[i].getFormalArg(j) !=
                        vl.db.idx.getElement(values[i].getFormalArg(j).getID()) )
                {
                    verified = false;

                    if ( verbose )
                    {
                        outStream.printf("test %d: formal arg (%d, %d) " +
                                "id = %d not in idx.\n",
                                testNum, i, j,
                                values[i].getFormalArg(j).getID());
                    }
                }
                else
                {
                    expected_idx_size++;
                }
            }

            /* also, for matrix vocab elements, verify that all the column
             * predicate formal arguments are in the index,
             */
            if ( values[i] instanceof MatrixVocabElement )
            {
                mve = (MatrixVocabElement)values[i];

                for ( j = 0; j < mve.getNumCPFormalArgs(); j++ )
                {
                    if ( mve.getCPFormalArg(j) !=
                            vl.db.idx.getElement(mve.getCPFormalArg(j).getID()) )
                    {
                        verified = false;

                        if ( verbose )
                        {
                            outStream.printf("test %d: cp formal arg " +
                                    "(%d, %d) id = %d not in idx.\n",
                                    testNum, i, j,
                                    mve.getCPFormalArg(j).getID());
                        }
                    }
                    else
                    {
//                        outStream.printf("test %d: cp formal arg " +
//                                    "(%d, %d) id = %d in idx.\n",
//                                    testNum, i, j,
//                                    mve.getCPFormalArg(j).getID());
                        expected_idx_size++;
                    }
                }
            }

            i++;
        }

        if ( ( verified ) &&
             ( DBIndexTest.GetIndexSize(vl.db.idx) != expected_idx_size ) )
        {
            verified = false;

            if ( verbose )
            {
                outStream.printf(
                        "test %d: idx size = %d != %d = expected idx size.\n",
                        testNum, DBIndexTest.GetIndexSize(vl.db.idx),
                        expected_idx_size);
            }
        }

        return verified;

    } /** DBIndex::VerifyVLContents() */

}