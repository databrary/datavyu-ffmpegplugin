package org.openshapa.db;

import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class MacshapaDatabaseTest {

    public MacshapaDatabaseTest() {
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
     * TestClassMacshapaDatabase()
     *
     * Main routine for all test code for the MacshapaDatabase class proper.
     *
     * For the most part, everything should be tested already by the time
     * we get this far, so the only major testing needed here is verification
     * that the restrictions on MacSHAPA databases are in fact being enforced.
     *
     * The code to do this doesn't exist at this point, so there is nothing to
     * test in that department.  However, this is also a good place to test
     * the API example in the context of a MacSHAPA database.
     *
     *                                           -- 11/18/08
     *
     * Changes:
     *
     *    - None.
     */
    @Test
    public void TestClassMacshapaDatabase()
    throws SystemErrorException {
        PrintStream outStream = System.out;
        boolean verbose = true;

        boolean pass = true;
        int failures = 0;

        outStream.print("Testing class MacshapaDatabase:\n");

        if ( ! TestAPIExamples(outStream, verbose) )
        {
            failures++;
        }

        if ( failures > 0 )
        {
            pass = false;
            outStream.printf(
                    "%d failures in tests for class MacshapaDatabase.\n\n",
                    failures);
        }
        else
        {
            outStream.print("All tests passed for class MacshapaDatabase.\n\n");
        }

        assertTrue(pass);

    } /* Database::TestClassMacshapaDatabase() */

    /**
     * TestAPIExamples()
     *
     * Main routine for testing the MacSHAPA database API examples.
     *
     *                                           11/18/08
     *
     * Changes:
     *
     *    - None.
     */

    public static boolean TestAPIExamples(java.io.PrintStream outStream,
                                          boolean verbose)
        throws SystemErrorException {
        String testBanner =
            "Testing API examples                                             ";
         String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean pass = true;
        int failures = 0;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        failures += TestAPIExample_01(outStream, verbose);


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

    } /* MacshapaDatabase::TestAPIExamples() */


    /**
     * TestAPIExample_01()
     *
     * MacSHAPA Database API example test 01:
     *
     * 1) Allocate a MacSHAPA data base.  Set its name to "API test 01".
     *    Set the description.  Use db.toString() to verify that the database
     *    is initialized as it should be.
     *
     * 2) Call the type and version check method to verify that we get the
     *    expected values.  Also call getName() and getDescription() to
     *    verify that they perform as expected.
     *
     * 3) Create float, int, nominal, and text data columns.  Use db.toString()
     *    to verify that the creations succeeded.
     *
     * 4) Get copies of some of the data column headers using both column name
     *    and ID based methods.  Modify the column headers, and then use
     *    db.toString() to verify that the changes took.
     *
     * 5) Append some cells to one of the columns in out of time sequence order.
     *    Verify that the cells appear in append order.
     *
     *    Turn on temporal ordering.  Verify that the cells are now sorted
     *    in increasing onset order.  Turn temporal ordering back off.
     *
     * 6) Insert some cells into one of the columns out of temporal sequence
     *    order.  Verify that the cells were inserted in the expected locations.
     *
     *    Turn on temporal ordering.  Verify that the cells are now sorted.
     *
     *    Try to insert a cell out of temporal order.  Verify that the cell
     *    gets inserted in temporal order.  Turn temporal ordering back off.
     *
     * 7) Delete several cells, and verify that the expected cells were deleted.
     *    Note that here it doesn't matter whether temporal ordering is turned
     *    on or off.
     *
     * 8) Edit several cells, including onsets and offsets with temporal ordering
     *    turned off.  Verify that the cells don't change location in the column.
     *
     *    Turn temporal ordering on.  Verify that the cells are now sorted in
     *    temporal order.
     *
     *    Edit several more cells, including their onsets and offsets.  Verify
     *    that the cells appear in temporal order in the column.
     *
     * 9) Delete two columns -- one with cells and one without.  Add a column
     *    and insert some cells.  Verify that the database contains the
     *    expected data.  Demonstrate the use of db.getColumns() in
     *    passing.
     *
     * 10) Delete all columns.  Verify that the database is empty.
     *
     *
     * Return the number of failures.
     *
     *                                               -- 4/25/08
     *
     * Changes:
     *
     *    - None.
     */

    private static int TestAPIExample_01(java.io.PrintStream outStream,
                                         boolean verbose)
        throws SystemErrorException {
        final String header = "test 01: ";
        final String description = "This is a test MacSHAPA database created " +
                "for purposes of demonstrating the API.";
        final String fdcName0 = "float data column";
        final String idcName0 = "int data column";
        final String ndcName0 = "nominal data column";
        final String tdcName0 = "text data column";
        final String fdcName1 = "float_data_column";
        final String idcName1 = "int_data_column";
        final String ndcName1 = "nominal_data_column";
        final String tdcName1 = "text_data_column";
        final String fdcName2 = "float_data_col_2";
        String systemErrorExceptionString = "";
        // this is a toString() dump of a newly created empty MacshapaDatabase.
        // Only the name and description have been modified -- all else is
        // as the database was created.
        // Note the long list of predicates in the vocab list -- these predicates
        // are all needed by the Query Language.  Expect this list to change a
        // bit as I get to implementing it.
        String expectedString0 =
        "(API test 1 " +
          "(Description: This is a test MacSHAPA database created for " +
                        "purposes of demonstrating the API.) " +
          "((VocabList) " +
            "(vl_contents: " +
              "(previnstbt(<base-var>, <predecessor-var>), " +
               "cmean(<val>), " +
               "cmax(<val>), " +
               "previnstbo(<base-var>, <predecessor-var>), " +
               "cmin(<val>), " +
               "nextt(<base-var>, <successor-var>), " +
               "deselect(<var>), " +
               "select(<var>), " +
               "nexto(<base-var>, <successor-var>), " +
               "delete(<var>), " +
               "nextinstt(<base-var>, <successor-var>), " +
               "modify(<var>), " +
               "sum(<addend>), " +
               "nextinsto(<base-var>, <successor-var>), " +
               "insert(<var>), " +
               "nextinstbt(<base-var>, <successor-var>), " +
               "count(<form>), " +
               "nextinstbo(<base-var>, <successor-var>), " +
               "during3(<base-var>, <contemporary-var>), " +
               "print(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "during2(<base-var>, <contemporary-var>), " +
               "isempty(<arg>), " +
               "during1(<base-var>, <contemporary-var>), " +
               "ispred(<arg>), " +
               "istext(<arg>), " +
               "isqstring(<arg>), " +
               "beforet(<base-var>, <predecessor-var>, <min-time>, <max-time>), " +
               "isnominal(<arg>), " +
               "isfloat(<arg>), " +
               "beforeo(<base-var>, <predecessor-var>, <min-ord>, <max-ord>), " +
               "istime(<arg>), isinteger(<arg>), " +
               "selected(<var>), " +
               "aftert(<base-var>, <successor-var>, <min-time>, <max-time>), " +
               "substring(<substr>, <string>), " +
               "aftero(<base-var>, <successor-var>, <min-ord>, <max-ord>), " +
               "ne(<x>, <y>), " +
               "rem(<x>, <y>), " +
               "eq(<x>, <y>), " +
               "mod(<x>, <y>), " +
               "ge(<x>, <y>), " +
               "abs(<x>), " +
               "le(<x>, <y>), " +
               "atan(<x>), " +
               "acos(<x>), " +
               "gt(<x>, <y>), " +
               "asin(<x>), " +
               "lt(<x>, <y>), " +
               "tan(<x>), " +
               "cos(<x>), " +
               "assign(<query-var>, <val>), " +
               "sin(<x>), " +
               "not(<arg1>), " +
               "concatq(<str1>, <str2>), " +
               "concat(<str1>, <str2>), " +
               "and(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "min(<x>, <y>), " +
               "max(<x>, <y>), " +
               "prevt(<base-var>, <predecessor-var>), " +
               "clear(<void>), " +
               "prevo(<base-var>, <predecessor-var>), " +
               "or(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "minus(<x>, <y>), " +
               "previnstt(<base-var>, <predecessor-var>), " +
               "query(<condition>, <action>), " +
               "divide(<x>, <y>), " +
               "previnsto(<base-var>, <predecessor-var>), " +
               "rule(<condition>, <action>), " +
               "times(<x>, <y>)))) " +
          "((ColumnList) (cl_contents: ())))";
        // This is a toString() dump of the MacSHAPA database after we have
        // added float, int, nominal, and text data columns.  Note that not
        // only do the new columns appear in the ColumnList, but the associated
        // matrix vocab elements appear in the VocabList.  With the exception
        // of the matrix vocab element associated with matrix data columns,
        // matrix vocab elements may not be edited by users, and are thus
        // marked as system.
        String expectedString1 =
        "(API test 1 " +
          "(Description: This is a test MacSHAPA database created for purposes of demonstrating the API.) " +
          "((VocabList) " +
            "(vl_contents: " +
              "(previnstbt(<base-var>, <predecessor-var>), " +
               "previnstbo(<base-var>, <predecessor-var>), " +
               "nextt(<base-var>, <successor-var>), " +
               "nexto(<base-var>, <successor-var>), " +
               "nextinstt(<base-var>, <successor-var>), " +
               "nextinsto(<base-var>, <successor-var>), " +
               "nextinstbt(<base-var>, <successor-var>), " +
               "nextinstbo(<base-var>, <successor-var>), " +
               "during3(<base-var>, <contemporary-var>), " +
               "during2(<base-var>, <contemporary-var>), " +
               "during1(<base-var>, <contemporary-var>), " +
               "beforet(<base-var>, <predecessor-var>, <min-time>, <max-time>), " +
               "beforeo(<base-var>, <predecessor-var>, <min-ord>, <max-ord>), " +
               "aftert(<base-var>, <successor-var>, <min-time>, <max-time>), " +
               "aftero(<base-var>, <successor-var>, <min-ord>, <max-ord>), " +
               "rem(<x>, <y>), " +
               "mod(<x>, <y>), " +
               "abs(<x>), " +
               "atan(<x>), " +
               "acos(<x>), " +
               "asin(<x>), " +
               "tan(<x>), " +
               "cos(<x>), " +
               "sin(<x>), " +
               "concatq(<str1>, <str2>), " +
               "concat(<str1>, <str2>), " +
               "min(<x>, <y>), " +
               "max(<x>, <y>), " +
               "clear(<void>), " +
               "minus(<x>, <y>), " +
               "divide(<x>, <y>), " +
               "times(<x>, <y>), " +
               "cmean(<val>), " +
               "cmax(<val>), " +
               "cmin(<val>), " +
               "deselect(<var>), " +
               "select(<var>), " +
               "delete(<var>), " +
               "modify(<var>), " +
               "sum(<addend>), " +
               "insert(<var>), " +
               "count(<form>), " +
               "print(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "isempty(<arg>), " +
               "ispred(<arg>), " +
               "istext(<arg>), " +
               "isqstring(<arg>), " +
               "isnominal(<arg>), " +
               "isfloat(<arg>), " +
               "istime(<arg>), " +
               "isinteger(<arg>), " +
               "selected(<var>), " +
               "substring(<substr>, <string>), " +
               "ne(<x>, <y>), " +
               "eq(<x>, <y>), " +
               "ge(<x>, <y>), " +
               "le(<x>, <y>), " +
               "text data column(<val>), " +    // this is a matrix vocab element
               "gt(<x>, <y>), lt(<x>, <y>), " +
               "nominal data column(<val>), " + // this is a matrix vocab element
               "assign(<query-var>, <val>), " +
               "not(<arg1>), " +
               "int data column(<val>), " +     // this is a matrix vocab element
               "and(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "float data column(<val>), " +   // this is a matrix vocab element
               "prevt(<base-var>, <predecessor-var>), " +
               "or(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "prevo(<base-var>, <predecessor-var>), " +
               "query(<condition>, <action>), " +
               "previnstt(<base-var>, <predecessor-var>), " +
               "rule(<condition>, <action>), " +
               "previnsto(<base-var>, <predecessor-var>)))) " +
          "((ColumnList) " +
            "(cl_contents: " +
              "((text data column, ()), " +
               "(int data column, ()), " +
               "(nominal data column, ()), " +
               "(float data column, ())))))";
        // This is the database after we have changed the names of the new
        // columns.  We also marked some of the collumns as hidden, but this
        // doesn't show in db.toString(), and we really don't want to mess
        // with db.toDBString(), as the amount of output would be breath
        // taking.
        String expectedString2 =
        "(API test 1 " +
          "(Description: This is a test MacSHAPA database created for purposes of demonstrating the API.) " +
          "((VocabList) " +
            "(vl_contents: " +
              "(previnstbt(<base-var>, <predecessor-var>), " +
               "previnstbo(<base-var>, <predecessor-var>), " +
               "nextt(<base-var>, <successor-var>), " +
               "nexto(<base-var>, <successor-var>), " +
               "nextinstt(<base-var>, <successor-var>), " +
               "nextinsto(<base-var>, <successor-var>), " +
               "nextinstbt(<base-var>, <successor-var>), " +
               "nextinstbo(<base-var>, <successor-var>), " +
               "during3(<base-var>, <contemporary-var>), " +
               "during2(<base-var>, <contemporary-var>), " +
               "during1(<base-var>, <contemporary-var>), " +
               "beforet(<base-var>, <predecessor-var>, <min-time>, <max-time>), " +
               "beforeo(<base-var>, <predecessor-var>, <min-ord>, <max-ord>), " +
               "aftert(<base-var>, <successor-var>, <min-time>, <max-time>), " +
               "aftero(<base-var>, <successor-var>, <min-ord>, <max-ord>), " +
               "rem(<x>, <y>), " +
               "mod(<x>, <y>), " +
               "abs(<x>), " +
               "atan(<x>), " +
               "acos(<x>), " +
               "asin(<x>), " +
               "tan(<x>), " +
               "cos(<x>), " +
               "sin(<x>), " +
               "concatq(<str1>, <str2>), " +
               "concat(<str1>, <str2>), " +
               "min(<x>, <y>), " +
               "max(<x>, <y>), " +
               "clear(<void>), " +
               "minus(<x>, <y>), " +
               "divide(<x>, <y>), " +
               "times(<x>, <y>), " +
               "cmean(<val>), " +
               "cmax(<val>), " +
               "cmin(<val>), " +
               "deselect(<var>), " +
               "select(<var>), " +
               "delete(<var>), " +
               "modify(<var>), " +
               "sum(<addend>), " +
               "insert(<var>), " +
               "count(<form>), " +
               "print(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "isempty(<arg>), " +
               "ispred(<arg>), " +
               "istext(<arg>), " +
               "isqstring(<arg>), " +
               "isnominal(<arg>), " +
               "isfloat(<arg>), " +
               "istime(<arg>), " +
               "isinteger(<arg>), " +
               "selected(<var>), " +
               "substring(<substr>, <string>), " +
               "ne(<x>, <y>), " +
               "eq(<x>, <y>), " +
               "ge(<x>, <y>), " +
               "le(<x>, <y>), " +
               "text_data_column(<val>), " +    // this is a matrix vocab element
               "gt(<x>, <y>), lt(<x>, <y>), " +
               "nominal_data_column(<val>), " + // this is a matrix vocab element
               "assign(<query-var>, <val>), " +
               "not(<arg1>), " +
               "int_data_column(<val>), " +     // this is a matrix vocab element
               "and(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "float_data_column(<val>), " +   // this is a matrix vocab element
               "prevt(<base-var>, <predecessor-var>), " +
               "or(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "prevo(<base-var>, <predecessor-var>), " +
               "query(<condition>, <action>), " +
               "previnstt(<base-var>, <predecessor-var>), " +
               "rule(<condition>, <action>), " +
               "previnsto(<base-var>, <predecessor-var>)))) " +
          "((ColumnList) " +
            "(cl_contents: " +
              "((text_data_column, ()), " +
               "(int_data_column, ()), " +
               "(nominal_data_column, ()), " +
               "(float_data_column, ())))))";
        // This is a toString() dump of fdc after we have added three cells
        // with temporal ording off.
        // Note that a toString dump of just a data column is not available
        // at the user level.
        String expectedString3 =
                "(float_data_column, " +
                    "((1, 00:00:03:000, 00:00:04:000, (3.000000)), " +
                     "(2, 00:00:02:000, 00:00:03:000, (2.000000)), " +
                     "(3, 00:00:01:000, 00:00:02:000, (1.000000))))";
        // This is a toString dump of fdc without further modifications after
        // we have turned temporal ordering on.
        String expectedString4 =
                "(float_data_column, " +
                    "((1, 00:00:01:000, 00:00:02:000, (1.000000)), " +
                     "(2, 00:00:02:000, 00:00:03:000, (2.000000)), " +
                     "(3, 00:00:03:000, 00:00:04:000, (3.000000))))";
        // This is a toString dump of fdc after inserting three cells
        // out of temporal order with temporal ordering turned off.
        String expectedString5 =
                "(float_data_column, " +
                    "((1, 00:00:04:000, 00:00:05:000, (4.000000)), " +
                     "(2, 00:00:01:000, 00:00:02:000, (1.000000)), " +
                     "(3, 00:00:05:000, 00:00:06:000, (5.000000)), " +
                     "(4, 00:00:02:000, 00:00:03:000, (2.000000)), " +
                     "(5, 00:00:03:000, 00:00:04:000, (3.000000)), " +
                     "(6, 00:00:00:000, 00:00:10:000, (0.000000))))";
        // This is a toString dump of fdc without further modifications after
        // we have turned temporal ordering back on.
        String expectedString6 =
                "(float_data_column, " +
                    "((1, 00:00:00:000, 00:00:10:000, (0.000000)), " +
                     "(2, 00:00:01:000, 00:00:02:000, (1.000000)), " +
                     "(3, 00:00:02:000, 00:00:03:000, (2.000000)), " +
                     "(4, 00:00:03:000, 00:00:04:000, (3.000000)), " +
                     "(5, 00:00:04:000, 00:00:05:000, (4.000000)), " +
                     "(6, 00:00:05:000, 00:00:06:000, (5.000000))))";
        // This is a toString dump of fdc after several cells have been inserted
        // out of temporal order while temporal ordering is turned off.
        String expectedString7 =
                "(float_data_column, " +
                    "((1, 00:00:00:000, 00:00:10:000, (0.000000)), " +
                     "(2, 00:00:01:000, 00:00:02:000, (1.000000)), " +
                     "(3, 00:00:02:000, 00:00:03:000, (2.000000)), " +
                     "(4, 00:00:03:000, 00:00:04:000, (3.000000)), " +
                     "(5, 00:00:04:000, 00:00:05:000, (4.000000)), " +
                     "(6, 00:00:05:000, 00:00:06:000, (5.000000)), " +
                     "(7, 00:00:06:000, 00:00:07:000, (6.000000)), " +
                     "(8, 00:00:07:000, 00:00:08:000, (7.000000)), " +
                     "(9, 00:00:08:000, 00:00:09:000, (8.000000))))";
        // This is a toString dump of fdc after we have removed cells with
        // ords 9, 5, and 1 in that order.
        String expectedString8 =
                "(float_data_column, " +
                    "((1, 00:00:01:000, 00:00:02:000, (1.000000)), " +
                     "(2, 00:00:02:000, 00:00:03:000, (2.000000)), " +
                     "(3, 00:00:03:000, 00:00:04:000, (3.000000)), " +
                     "(4, 00:00:05:000, 00:00:06:000, (5.000000)), " +
                     "(5, 00:00:06:000, 00:00:07:000, (6.000000)), " +
                     "(6, 00:00:07:000, 00:00:08:000, (7.000000))))";
        // This is a toString dump of fdc after several cell edits with
        // temporal ordering turned off
        String expectedString9 =
                "(float_data_column, " +
                    "((1, 00:00:10:000, 00:00:11:000, (9.000000)), " +
                     "(2, 00:00:02:000, 00:00:03:000, (2.000000)), " +
                     "(3, 00:00:04:000, 00:00:05:000, (4.000000)), " +
                     "(4, 00:00:05:000, 00:00:06:000, (5.000000)), " +
                     "(5, 00:00:06:000, 00:00:07:000, (6.000000)), " +
                     "(6, 00:00:00:000, 00:00:01:000, (0.000000))))";
        // This is a toString dump of fdc without further modifications after
        // we have turned temporal ordering back on.
        String expectedString10 =
                "(float_data_column, " +
                    "((1, 00:00:00:000, 00:00:01:000, (0.000000)), " +
                     "(2, 00:00:02:000, 00:00:03:000, (2.000000)), " +
                     "(3, 00:00:04:000, 00:00:05:000, (4.000000)), " +
                     "(4, 00:00:05:000, 00:00:06:000, (5.000000)), " +
                     "(5, 00:00:06:000, 00:00:07:000, (6.000000)), " +
                     "(6, 00:00:10:000, 00:00:11:000, (9.000000))))";
        // This is a toString dump of fdc after editing several cells with
        // temporal ordering turned on.
        String expectedString11 =
                "(float_data_column, " +
                    "((1, 00:00:00:000, 00:00:01:000, (0.000000)), " +
                     "(2, 00:00:01:000, 00:00:02:000, (1.000000)), " +
                     "(3, 00:00:03:000, 00:00:04:000, (3.000000)), " +
                     "(4, 00:00:04:000, 00:00:05:000, (4.000000)), " +
                     "(5, 00:00:10:000, 00:00:11:000, (9.000000)), " +
                     "(6, 00:00:11:000, 00:00:12:000, (10.000000))))";
        // This is a toString dump of the database before column deletions
        String expectedString12 =
        "(API test 1 " +
          "(Description: This is a test MacSHAPA database created for purposes of demonstrating the API.) " +
          "((VocabList) " +
            "(vl_contents: " +
              "(previnstbt(<base-var>, <predecessor-var>), " +
               "previnstbo(<base-var>, <predecessor-var>), " +
               "nextt(<base-var>, <successor-var>), " +
               "nexto(<base-var>, <successor-var>), " +
               "nextinstt(<base-var>, <successor-var>), " +
               "nextinsto(<base-var>, <successor-var>), " +
               "nextinstbt(<base-var>, <successor-var>), " +
               "nextinstbo(<base-var>, <successor-var>), " +
               "during3(<base-var>, <contemporary-var>), " +
               "during2(<base-var>, <contemporary-var>), " +
               "during1(<base-var>, <contemporary-var>), " +
               "beforet(<base-var>, <predecessor-var>, <min-time>, <max-time>), " +
               "beforeo(<base-var>, <predecessor-var>, <min-ord>, <max-ord>), " +
               "aftert(<base-var>, <successor-var>, <min-time>, <max-time>), " +
               "aftero(<base-var>, <successor-var>, <min-ord>, <max-ord>), " +
               "rem(<x>, <y>), " +
               "mod(<x>, <y>), " +
               "abs(<x>), " +
               "atan(<x>), " +
               "acos(<x>), " +
               "asin(<x>), " +
               "tan(<x>), " +
               "cos(<x>), " +
               "sin(<x>), " +
               "concatq(<str1>, <str2>), " +
               "concat(<str1>, <str2>), min(<x>, <y>), " +
               "max(<x>, <y>), " +
               "clear(<void>), " +
               "minus(<x>, <y>), " +
               "divide(<x>, <y>), " +
               "times(<x>, <y>), " +
               "cmean(<val>), " +
               "cmax(<val>), " +
               "cmin(<val>), " +
               "deselect(<var>), " +
               "select(<var>), " +
               "delete(<var>), " +
               "modify(<var>), " +
               "sum(<addend>), " +
               "insert(<var>), " +
               "count(<form>), " +
               "print(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "isempty(<arg>), " +
               "ispred(<arg>), " +
               "istext(<arg>), " +
               "isqstring(<arg>), " +
               "isnominal(<arg>), " +
               "isfloat(<arg>), " +
               "istime(<arg>), " +
               "isinteger(<arg>), " +
               "selected(<var>), " +
               "substring(<substr>, <string>), " +
               "ne(<x>, <y>), " +
               "eq(<x>, <y>), " +
               "ge(<x>, <y>), " +
               "le(<x>, <y>), " +
               "text_data_column(<val>), " +
               "gt(<x>, <y>), " +
               "lt(<x>, <y>), " +
               "nominal_data_column(<val>), " +
               "assign(<query-var>, <val>), " +
               "not(<arg1>), " +
               "int_data_column(<val>), " +
               "and(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "float_data_column(<val>), " +
               "prevt(<base-var>, <predecessor-var>), " +
               "or(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "prevo(<base-var>, <predecessor-var>), " +
               "query(<condition>, <action>), " +
               "previnstt(<base-var>, <predecessor-var>), " +
               "rule(<condition>, <action>), " +
               "previnsto(<base-var>, <predecessor-var>)))) " +
          "((ColumnList) " +
            "(cl_contents: " +
              "((text_data_column, ()), " +
                "(int_data_column, ()), " +
                "(nominal_data_column, ()), " +
                "(float_data_column, " +
                  "((1, 00:00:00:000, 00:00:01:000, (0.000000)), " +
                   "(2, 00:00:01:000, 00:00:02:000, (1.000000)), " +
                   "(3, 00:00:03:000, 00:00:04:000, (3.000000)), " +
                   "(4, 00:00:04:000, 00:00:05:000, (4.000000)), " +
                   "(5, 00:00:10:000, 00:00:11:000, (9.000000)), " +
                   "(6, 00:00:11:000, 00:00:12:000, (10.000000))))))))";
        // this is a toString dump of the vector returned by db.getColumns()
        // before any column deletions.
        String expectedString13 =
                "[(text_data_column, ()), " +
                 "(int_data_column, ()), " +
                 "(nominal_data_column, ()), " +
                 "(float_data_column, ())]";
        // this is a toString dump of the database after the deletion of
        // two columns -- fdc and ndc.
        String expectedString14 =
        "(API test 1 " +
          "(Description: This is a test MacSHAPA database created for purposes of demonstrating the API.) " +
          "((VocabList) " +
            "(vl_contents: " +
              "(previnstbt(<base-var>, <predecessor-var>), " +
               "previnstbo(<base-var>, <predecessor-var>), " +
               "nextt(<base-var>, <successor-var>), " +
               "nexto(<base-var>, <successor-var>), " +
               "nextinstt(<base-var>, <successor-var>), " +
               "nextinsto(<base-var>, <successor-var>), " +
               "nextinstbt(<base-var>, <successor-var>), " +
               "nextinstbo(<base-var>, <successor-var>), " +
               "during3(<base-var>, <contemporary-var>), " +
               "during2(<base-var>, <contemporary-var>), " +
               "during1(<base-var>, <contemporary-var>), " +
               "beforet(<base-var>, <predecessor-var>, <min-time>, <max-time>), " +
               "beforeo(<base-var>, <predecessor-var>, <min-ord>, <max-ord>), " +
               "aftert(<base-var>, <successor-var>, <min-time>, <max-time>), " +
               "aftero(<base-var>, <successor-var>, <min-ord>, <max-ord>), " +
               "rem(<x>, <y>), " +
               "mod(<x>, <y>), " +
               "abs(<x>), " +
               "atan(<x>), " +
               "acos(<x>), " +
               "asin(<x>), " +
               "tan(<x>), " +
               "cos(<x>), " +
               "sin(<x>), " +
               "concatq(<str1>, <str2>), " +
               "concat(<str1>, <str2>), " +
               "min(<x>, <y>), " +
               "max(<x>, <y>), " +
               "clear(<void>), " +
               "minus(<x>, <y>), " +
               "divide(<x>, <y>), " +
               "times(<x>, <y>), " +
               "cmean(<val>), " +
               "cmax(<val>), " +
               "cmin(<val>), " +
               "deselect(<var>), " +
               "select(<var>), " +
               "delete(<var>), " +
               "modify(<var>), " +
               "sum(<addend>), " +
               "insert(<var>), " +
               "count(<form>), " +
               "print(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "isempty(<arg>), " +
               "ispred(<arg>), " +
               "istext(<arg>), " +
               "isqstring(<arg>), " +
               "isnominal(<arg>), " +
               "isfloat(<arg>), " +
               "istime(<arg>), " +
               "isinteger(<arg>), " +
               "selected(<var>), " +
               "substring(<substr>, <string>), " +
               "ne(<x>, <y>), " +
               "eq(<x>, <y>), " +
               "ge(<x>, <y>), " +
               "le(<x>, <y>), " +
               "text_data_column(<val>), " +
               "gt(<x>, <y>), " +
               "lt(<x>, <y>), " +
               "assign(<query-var>, <val>), " +
               "not(<arg1>), " +
               "int_data_column(<val>), " +
               "and(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "prevt(<base-var>, <predecessor-var>), " +
               "or(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "prevo(<base-var>, <predecessor-var>), " +
               "query(<condition>, <action>), " +
               "previnstt(<base-var>, <predecessor-var>), " +
               "rule(<condition>, <action>), " +
               "previnsto(<base-var>, <predecessor-var>)))) " +
          "((ColumnList) " +
            "(cl_contents: " +
              "((text_data_column, ()), " +
               "(int_data_column, ())))))";
        // this is a toString dump of the vector returned by db.getColumns()
        // after the deletion of two columns -- fdc and ndc.
        String expectedString15 = "[(text_data_column, ()), (int_data_column, ())]";
        // this is a toString dump of the database after the adding the fdc2
        // column and appending several cells to tdc.
        String expectedString16 =
        "(API test 1 " +
          "(Description: This is a test MacSHAPA database created for purposes of demonstrating the API.) " +
          "((VocabList) " +
            "(vl_contents: " +
              "(previnstbt(<base-var>, <predecessor-var>), " +
               "previnstbo(<base-var>, <predecessor-var>), " +
               "nextt(<base-var>, <successor-var>), " +
               "nexto(<base-var>, <successor-var>), " +
               "nextinstt(<base-var>, <successor-var>), " +
               "nextinsto(<base-var>, <successor-var>), " +
               "nextinstbt(<base-var>, <successor-var>), " +
               "nextinstbo(<base-var>, <successor-var>), " +
               "during3(<base-var>, <contemporary-var>), " +
               "during2(<base-var>, <contemporary-var>), " +
               "during1(<base-var>, <contemporary-var>), " +
               "beforet(<base-var>, <predecessor-var>, <min-time>, <max-time>), " +
               "beforeo(<base-var>, <predecessor-var>, <min-ord>, <max-ord>), " +
               "aftert(<base-var>, <successor-var>, <min-time>, <max-time>), " +
               "aftero(<base-var>, <successor-var>, <min-ord>, <max-ord>), " +
               "rem(<x>, <y>), " +
               "mod(<x>, <y>), " +
               "abs(<x>), " +
               "atan(<x>), " +
               "acos(<x>), " +
               "asin(<x>), " +
               "tan(<x>), " +
               "cos(<x>), " +
               "sin(<x>), " +
               "concatq(<str1>, <str2>), " +
               "concat(<str1>, <str2>), " +
               "min(<x>, <y>), " +
               "max(<x>, <y>), " +
               "clear(<void>), " +
               "minus(<x>, <y>), " +
               "divide(<x>, <y>), " +
               "times(<x>, <y>), " +
               "cmean(<val>), " +
               "cmax(<val>), " +
               "cmin(<val>), " +
               "deselect(<var>), " +
               "select(<var>), " +
               "delete(<var>), " +
               "modify(<var>), " +
               "sum(<addend>), " +
               "insert(<var>), " +
               "count(<form>), " +
               "print(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "isempty(<arg>), " +
               "float_data_col_2(<val>), " +
               "ispred(<arg>), " +
               "istext(<arg>), " +
               "isqstring(<arg>), " +
               "isnominal(<arg>), " +
               "isfloat(<arg>), " +
               "istime(<arg>), " +
               "isinteger(<arg>), " +
               "selected(<var>), " +
               "substring(<substr>, <string>), " +
               "ne(<x>, <y>), " +
               "eq(<x>, <y>), " +
               "ge(<x>, <y>), " +
               "le(<x>, <y>), " +
               "text_data_column(<val>), " +
               "gt(<x>, <y>), " +
               "lt(<x>, <y>), " +
               "assign(<query-var>, <val>), " +
               "not(<arg1>), " +
               "int_data_column(<val>), " +
               "and(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "prevt(<base-var>, <predecessor-var>), " +
               "or(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "prevo(<base-var>, <predecessor-var>), " +
               "query(<condition>, <action>), " +
               "previnstt(<base-var>, <predecessor-var>), " +
               "rule(<condition>, <action>), " +
               "previnsto(<base-var>, <predecessor-var>)))) " +
          "((ColumnList) " +
            "(cl_contents: " +
              "((text_data_column, " +
                "((1, 00:00:03:000, 00:00:04:000, (three)), " +
                 "(2, 00:00:02:000, 00:00:03:000, (two)), " +
                 "(3, 00:00:01:000, 00:00:02:000, (one)))), " +
               "(float_data_col_2, ()), " +
               "(int_data_column, ())))))";
        // this is a toString() dump of the vector returned by db.getColumns()
        // after the insertion of fdc2.
        String expectedString17 =
                "[(text_data_column, ()), " +
                 "(float_data_col_2, ()), " +
                 "(int_data_column, ())]";
        String expectedString18 =
        "(API test 1 " +
          "(Description: This is a test MacSHAPA database created for purposes of demonstrating the API.) " +
          "((VocabList) " +
            "(vl_contents: " +
              "(previnstbt(<base-var>, <predecessor-var>), " +
               "previnstbo(<base-var>, <predecessor-var>), " +
               "nextt(<base-var>, <successor-var>), " +
               "nexto(<base-var>, <successor-var>), " +
               "nextinstt(<base-var>, <successor-var>), " +
               "nextinsto(<base-var>, <successor-var>), " +
               "nextinstbt(<base-var>, <successor-var>), " +
               "nextinstbo(<base-var>, <successor-var>), " +
               "during3(<base-var>, <contemporary-var>), " +
               "during2(<base-var>, <contemporary-var>), " +
               "during1(<base-var>, <contemporary-var>), " +
               "beforet(<base-var>, <predecessor-var>, <min-time>, <max-time>), " +
               "beforeo(<base-var>, <predecessor-var>, <min-ord>, <max-ord>), " +
               "aftert(<base-var>, <successor-var>, <min-time>, <max-time>), " +
               "aftero(<base-var>, <successor-var>, <min-ord>, <max-ord>), " +
               "rem(<x>, <y>), " +
               "mod(<x>, <y>), " +
               "abs(<x>), " +
               "atan(<x>), " +
               "acos(<x>), " +
               "asin(<x>), " +
               "tan(<x>), " +
               "cos(<x>), " +
               "sin(<x>), " +
               "concatq(<str1>, <str2>), " +
               "concat(<str1>, <str2>), " +
               "min(<x>, <y>), " +
               "max(<x>, <y>), " +
               "clear(<void>), " +
               "minus(<x>, <y>), " +
               "divide(<x>, <y>), " +
               "times(<x>, <y>), " +
               "cmean(<val>), " +
               "cmax(<val>), " +
               "cmin(<val>), " +
               "deselect(<var>), " +
               "select(<var>), " +
               "delete(<var>), " +
               "modify(<var>), " +
               "sum(<addend>), " +
               "insert(<var>), " +
               "count(<form>), " +
               "print(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "isempty(<arg>), " +
               "ispred(<arg>), " +
               "istext(<arg>), " +
               "isqstring(<arg>), " +
               "isnominal(<arg>), " +
               "isfloat(<arg>), " +
               "istime(<arg>), " +
               "isinteger(<arg>), " +
               "selected(<var>), " +
               "substring(<substr>, <string>), " +
               "ne(<x>, <y>), " +
               "eq(<x>, <y>), " +
               "ge(<x>, <y>), " +
               "le(<x>, <y>), " +
               "gt(<x>, <y>), " +
               "lt(<x>, <y>), " +
               "assign(<query-var>, <val>), " +
               "not(<arg1>), " +
               "and(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "prevt(<base-var>, <predecessor-var>), " +
               "or(<arg1>, <arg2>, <arg3>, <arg4>, <arg5>, <arg6>, <arg7>), " +
               "prevo(<base-var>, <predecessor-var>), " +
               "query(<condition>, <action>), " +
               "previnstt(<base-var>, <predecessor-var>), " +
               "rule(<condition>, <action>), " +
               "previnsto(<base-var>, <predecessor-var>)))) " +
          "((ColumnList) (cl_contents: ())))";
        String testStringA = null;
        String testStringB = null;
        String testStringC = null;
        String testStringD = null;
        String testStringE = null;
        String testStringF = null;
        boolean completed;
        boolean threwSystemErrorException = false;
        int failures = 0;
        int i;
        int numCells;
        long fdcID = DBIndex.INVALID_ID;
        long fdc2ID = DBIndex.INVALID_ID;
        long idcID = DBIndex.INVALID_ID;
        long ndcID = DBIndex.INVALID_ID;
        long tdcID = DBIndex.INVALID_ID;
        long fdc_mveID = DBIndex.INVALID_ID;
        long fdc2_mveID = DBIndex.INVALID_ID;
        long idc_mveID = DBIndex.INVALID_ID;
        long ndc_mveID = DBIndex.INVALID_ID;
        long tdc_mveID = DBIndex.INVALID_ID;
        long pve0ID = DBIndex.INVALID_ID;
        long pve1ID = DBIndex.INVALID_ID;
        long pve2ID = DBIndex.INVALID_ID;
        Database db = null;
        DataColumn fdc = null;
        DataColumn fdc2 = null;
        DataColumn idc = null;
        DataColumn ndc = null;
        DataColumn tdc = null;
        DataCell dc0 = null;
        DataCell dc1 = null;
        DataCell dc2 = null;

        // 1) Allocate a MacSHAPA data base.  Set its name to "API test 01".
        //    Set the description.  Use db.toString() to verify that the database
        //    is initialized as it should be.
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            testStringA = null;

            try
            {
                // allocate a new database

                db = new MacshapaDatabase();

                db.setName("API test 1");
                db.setDescription(description);

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
                 ( testStringA == null ) ||
                 ( expectedString0.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("%s db allocation failed to complete.\n",
                                         header);
                    }

                    if ( db == null )
                    {
                        outStream.printf(
                                "%s new MacshapaDatabase() returned null.\n",
                                header);
                    }

                    if ( testStringA == null )
                    {
                        outStream.printf("%s testStringA is null.\n", header);
                    }
                    else if ( expectedString0.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString0.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in db allocation: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        // 2) Call the type and version check method to verify that we get the
        //    expected values.  Also call getName() and getDescription() to
        //    verify that they perform as expected.
        if ( failures == 0 )
        {
            if ( db.getType().compareTo("MacSHAPA Database") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "%s db.getType() returns unexpected string \"%s\"\n",
                        header, db.getType());
                }
            }

            if ( db.getVersion() != 1.0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "%s db.getVersion() returns unexpected value: %f\n",
                        header, db.getVersion());
                }
            }

            if ( db.getName().compareTo("API test 1") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "%s db.getName() returns unexpected string \"%s\"\n",
                        header, db.getName());
                }
            }

            if ( db.getDescription().compareTo(description) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s db.getDescription() returns " +
                            "unexpected string \"%s\"\n",
                            header, db.getDescription());
                }
            }
        }


        // 3) Create float, int, nominal, and text data columns.  Use db.toString()
        //    to verify that the creations succeeded.
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            testStringA = null;

            try
            {
                // allocate and insert a float data column
                fdc = new DataColumn(db, fdcName0,
                        MatrixVocabElement.MatrixType.FLOAT);
                fdcID = db.addColumn(fdc);

                // allocate and insert an integer data column
                idc = new DataColumn(db, idcName0,
                        MatrixVocabElement.MatrixType.INTEGER);
                idcID = db.addColumn(idc);

                // allocate and insert a nominal data column
                ndc = new DataColumn(db, ndcName0,
                        MatrixVocabElement.MatrixType.INTEGER);
                ndcID = db.addColumn(ndc);

                // allocate and insert a text data column
                tdc = new DataColumn(db, tdcName0,
                        MatrixVocabElement.MatrixType.TEXT);
                tdcID = db.addColumn(tdc);


                // create the test string
                testStringA = db.toString();

                completed = true;
            }

            catch (Exception e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( fdc == null ) ||
                 ( fdcID == DBIndex.INVALID_ID ) ||
                 ( idc == null ) ||
                 ( idcID == DBIndex.INVALID_ID ) ||
                 ( ndc == null ) ||
                 ( ndcID == DBIndex.INVALID_ID ) ||
                 ( tdc == null ) ||
                 ( tdcID == DBIndex.INVALID_ID ) ||
                 ( testStringA == null ) ||
                 ( expectedString1.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf(
                                "%s column allocation failed to complete.\n",
                                header);
                    }

                    if ( fdc == null )
                    {
                        outStream.printf("%s allocation of fdc failed.\n",
                                header);
                    }
                    else if ( fdcID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("%s fdcID is invalid!?!\n",
                                header);
                    }

                    if ( idc == null )
                    {
                        outStream.printf("%s allocation of idc failed.\n",
                                header);
                    }
                    else if ( idcID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("%s idcID is invalid?!?\n",
                                header);
                    }

                    if ( ndc == null )
                    {
                        outStream.printf("%s allocation of ndc failed.\n",
                                header);
                    }
                    else if ( ndcID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("%s ndcID is invalid?!?.\n",
                                header);
                    }

                    if ( tdc == null )
                    {
                        outStream.printf("%s allocation of tdc failed.\n",
                                header);
                    }
                    else if ( tdcID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("%s tdcID is invalid?!?\n",
                                header);
                    }

                    if ( testStringA == null )
                    {
                        outStream.printf("%s testStringA is null.\n", header);
                    }
                    else if ( expectedString1.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString1.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in column allocation: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        // 4) Get copies of some of the data column headers using both column name
        //    and ID based methods.  Modify the column headers, and then use
        //    db.toString() to verify that the changes took.
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            testStringA = null;

            try
            {
                // get copies of the current values of the data columns in
                // the database.  Since we have the IDs of all the columns,
                // we could use those, but just to be interesting, get some
                // using the column name instead.  Also, use both getColumn
                // and getDataColumn.  For MacSHAPA database, and for all
                // databases until reference columns are implemented, all
                // columns are DataColumns.

                fdc = db.getDataColumn(fdcID);
                idc = db.getDataColumn(idcName0);
                ndc = (DataColumn)db.getColumn(ndcID);
                tdc = (DataColumn)db.getColumn(tdcName0);

                // set the names of all the data columns to versions of their
                // names with the qhite space replaced with underscores.

                fdc.setName(fdcName1);
                idc.setName(idcName1);
                ndc.setName(ndcName1);
                tdc.setName(tdcName1);

                // mark idc and tdc as hidden
                idc.setHidden(true);
                tdc.setHidden(true);

                // replace the old version with the new
                db.replaceColumn(fdc);
                db.replaceColumn(idc);
                db.replaceColumn(ndc);
                db.replaceColumn(tdc);

                // get fresh copies of the columns for test purposes
                fdc = db.getDataColumn(fdcID);
                idc = db.getDataColumn(idcID);
                ndc = db.getDataColumn(ndcID);
                tdc = db.getDataColumn(tdcID);

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
                 ( testStringA == null ) ||
                 ( expectedString2.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf(
                                "%s column mods failed to complete.\n",
                                header);
                    }

                    if ( testStringA == null )
                    {
                        outStream.printf("%s testStringA is null.\n", header);
                    }
                    else if ( expectedString2.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString2.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in column mods: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
            else if ( ( fdc.getHidden() != false ) ||
                      ( idc.getHidden() != true ) ||
                      ( ndc.getHidden() != false ) ||
                      ( tdc.getHidden() != true ) )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s unexpected column hidden settings.\n" +
                            "   fdc.getHidden() = %b (false expected)\n" +
                            "   idc.getHidden() = %b (true expected)\n" +
                            "   ndc.getHidden() = %b (false expected)\n" +
                            "   tdc.getHidden() = %b (true expected)\n",
                            header,
                            fdc.getHidden(),
                            idc.getHidden(),
                            ndc.getHidden(),
                            tdc.getHidden());
                }
            }
        }

        // 5) Append some cells to one of the columns in out of time sequence order.
        //    Verify that the cells appear in append order.
        //
        //    Turn on temporal ordering.  Verify that the cells are now sorted
        //    in increasing onset order.  Turn temporal ordering back off.
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            testStringA = null;

            try
            {
                fdc_mveID = fdc.getItsMveID();

                db.appendCell(
                    DataCell.Construct(
                        db,
                        fdcID,
                        fdc_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            fdc_mveID,
                            FloatDataValue.Construct(db, 3.0))));

                db.appendCell(
                    DataCell.Construct(
                        db,
                        fdcID,
                        fdc_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            fdc_mveID,
                            FloatDataValue.Construct(db, 2.0))));

                db.appendCell(
                    DataCell.Construct(
                        db,
                        fdcID,
                        fdc_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            fdc_mveID,
                            FloatDataValue.Construct(db, 1.0))));

                // create the test string -- we cheat a bit here by getting only
                // the string for fdc.  This isn't available at the user level.
                testStringA = db.cl.getColumn(fdcID).toString();

                // turn on temporal ordering
                db.setTemporalOrdering(true);

                // get another test string
                testStringB = db.cl.getColumn(fdcID).toString();

                // turn off temporal ordering
                db.setTemporalOrdering(false);

                // get fresh copy of fdc for test purposes
                fdc = db.getDataColumn(fdcID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( testStringA == null ) ||
                 ( expectedString3.compareTo(testStringA) != 0 ) ||
                 ( testStringB == null ) ||
                 ( expectedString4.compareTo(testStringB) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf(
                                "%s cell appends 1 failed to complete.\n",
                                header);
                    }

                    if ( testStringA == null )
                    {
                        outStream.printf("%s testStringA is null.\n", header);
                    }
                    else if ( expectedString3.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString3.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }

                    if ( testStringB == null )
                    {
                        outStream.printf("%s testStringB is null.\n", header);
                    }
                    else if ( expectedString4.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString4.\n" +
                             "testString = \"%s\".\n", header, testStringB);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in cell appends 1: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
            else if ( fdc.getNumCells() != 3 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s fdc.getNumCells() returned " +
                            "unexpected value %d (3).\n",
                            header,
                            fdc.getNumCells());
                }
            }
        }


        // 6) Insert some cells into one of the columns out of temporal sequence
        //    order.  Verify that the cells were inserted in the expected locations.
        //
        //    Turn on temporal ordering.  Verify that the cells are now sorted.
        //
        //    Try to insert a cell out of temporal order.  Verify that the cell
        //    gets inserted in temporal order.  Turn temporal ordering back off.
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            testStringA = null;
            testStringB = null;
            testStringC = null;

            try
            {
                fdc_mveID = fdc.getItsMveID();

                db.insertdCell(
                        DataCell.Construct(
                            db,
                            fdcID,
                            fdc_mveID,
                            240,
                            300,
                            Matrix.Construct(
                                db,
                                fdc_mveID,
                                FloatDataValue.Construct(db, 4.0))),
                        1);

                db.insertdCell(
                        DataCell.Construct(
                            db,
                            fdcID,
                            fdc_mveID,
                            300,
                            360,
                            Matrix.Construct(
                                db,
                                fdc_mveID,
                                FloatDataValue.Construct(db, 5.0))),
                        3);

                db.insertdCell(
                        DataCell.Construct(
                            db,
                            fdcID,
                            fdc_mveID,
                            0,
                            600,
                            Matrix.Construct(
                                db,
                                fdc_mveID,
                                FloatDataValue.Construct(db, 0.0))),
                        6);

                // create the test string -- we cheat a bit here by getting only
                // the string for fdc.  This isn't available at the user level.
                testStringA = db.cl.getColumn(fdcID).toString();

                // turn on temporal ordering
                db.setTemporalOrdering(true);

                // get another test string
                testStringB = db.cl.getColumn(fdcID).toString();

                // Insert several more cells with temporal ordering turned on.

                db.insertdCell(
                        DataCell.Construct(
                            db,
                            fdcID,
                            fdc_mveID,
                            360,
                            420,
                            Matrix.Construct(
                                db,
                                fdc_mveID,
                                FloatDataValue.Construct(db, 6.0))),
                        7);

                db.insertdCell(
                        DataCell.Construct(
                            db,
                            fdcID,
                            fdc_mveID,
                            420,
                            480,
                            Matrix.Construct(
                                db,
                                fdc_mveID,
                                FloatDataValue.Construct(db, 7.0))),
                        4);

                db.insertdCell(
                        DataCell.Construct(
                            db,
                            fdcID,
                            fdc_mveID,
                            480,
                            540,
                            Matrix.Construct(
                                db,
                                fdc_mveID,
                                FloatDataValue.Construct(db, 8.0))),
                        1);

                // get another test string
                testStringC = db.cl.getColumn(fdcID).toString();

                // turn off temporal ordering
                db.setTemporalOrdering(false);

                // get fresh copy of fdc for test purposes
                fdc = db.getDataColumn(fdcID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( testStringA == null ) ||
                 ( expectedString5.compareTo(testStringA) != 0 ) ||
                 ( testStringB == null ) ||
                 ( expectedString6.compareTo(testStringB) != 0 ) ||
                 ( testStringC == null ) ||
                 ( expectedString7.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf(
                                "%s cell inserts 1 failed to complete.\n",
                                header);
                    }

                    if ( testStringA == null )
                    {
                        outStream.printf("%s testStringA is null.\n", header);
                    }
                    else if ( expectedString5.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString5.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }

                    if ( testStringB == null )
                    {
                        outStream.printf("%s testStringB is null.\n", header);
                    }
                    else if ( expectedString6.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString6.\n" +
                             "testString = \"%s\".\n", header, testStringB);
                    }

                    if ( testStringC == null )
                    {
                        outStream.printf("%s testStringC is null.\n", header);
                    }
                    else if ( expectedString7.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString7.\n" +
                             "testString = \"%s\".\n", header, testStringC);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in cell inserts 1: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
            else if ( fdc.getNumCells() != 9 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s fdc.getNumCells() returned " +
                            "unexpected value %d (9).\n",
                            header,
                            fdc.getNumCells());
                }
            }
        }


        // 7) Delete several cells, and verify that the expected cells were
        //    deleted.  Note that here it doesn't matter whether temporal
        //    ordering is turned on or off.
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            testStringA = null;

            try
            {
                fdc_mveID = fdc.getItsMveID();

                db.removeCell(db.getCell(fdcID, 9).getID());
                db.removeCell(db.getCell(fdcID, 5).getID());
                db.removeCell(db.getCell(fdcID, 1).getID());

                // create the test string -- we cheat a bit here by getting only
                // the string for fdc.  This isn't available at the user level.
                testStringA = db.cl.getColumn(fdcID).toString();

                // get fresh copy of fdc for test purposes
                fdc = db.getDataColumn(fdcID);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( testStringA == null ) ||
                 ( expectedString8.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf(
                                "%s cell removes 1 failed to complete.\n",
                                header);
                    }

                    if ( testStringA == null )
                    {
                        outStream.printf("%s testStringA is null.\n", header);
                    }
                    else if ( expectedString8.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString8.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in cell removes 1: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
            else if ( fdc.getNumCells() != 6 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s fdc.getNumCells() returned " +
                            "unexpected value %d (6).\n",
                            header,
                            fdc.getNumCells());
                }
            }
        }


        // 8) Edit several cells, including onsets and offsets with temporal
        //    ordering turned off.  Verify that the cells don't change location
        //    the column.
        //
        //    Turn temporal ordering on.  Verify that the cells are now sorted
        //    in temporal order.
        //
        //    Edit several more cells, including their onsets and offsets.
        //    Verify that the cells appear in temporal order in the column.
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            testStringA = null;
            testStringB = null;
            testStringC = null;

            try
            {
                fdc_mveID = fdc.getItsMveID();

                // do a few cell edits
                dc0 = (DataCell)db.getCell(fdcID, 1);
                dc1 = (DataCell)db.getCell(fdcID, 3);
                dc2 = (DataCell)db.getCell(fdcID, 6);

                dc0.setOnset(new TimeStamp(db.getTicks(), 600));
                dc0.setOffset(new TimeStamp(db.getTicks(), 660));
                dc0.setVal(Matrix.Construct(db, fdc_mveID,
                                FloatDataValue.Construct(db, 9.0)));

                dc1.setOnset(new TimeStamp(db.getTicks(), 240));
                dc1.setOffset(new TimeStamp(db.getTicks(), 300));
                dc1.setVal(Matrix.Construct(db, fdc_mveID,
                                FloatDataValue.Construct(db, 4.0)));

                dc2.setOnset(new TimeStamp(db.getTicks(), 0));
                dc2.setOffset(new TimeStamp(db.getTicks(), 60));
                dc2.setVal(Matrix.Construct(db, fdc_mveID,
                                FloatDataValue.Construct(db, 0.0)));

                db.replaceCell(dc0);
                db.replaceCell(dc1);
                db.replaceCell(dc2);

                // create the test string -- we cheat a bit here by getting only
                // the string for fdc.  This isn't available at the user level.
                testStringA = db.cl.getColumn(fdcID).toString();

                // turn on temporal ordering
                db.setTemporalOrdering(true);

                // get another test string
                testStringB = db.cl.getColumn(fdcID).toString();

                // do some more cell edits, this time with temporal ordering on
                dc0 = (DataCell)db.getCell(fdcID, 2);
                dc1 = (DataCell)db.getCell(fdcID, 4);
                dc2 = (DataCell)db.getCell(fdcID, 5);

                dc0.setOnset(new TimeStamp(db.getTicks(), 60));
                dc0.setOffset(new TimeStamp(db.getTicks(), 120));
                dc0.setVal(Matrix.Construct(db, fdc_mveID,
                                FloatDataValue.Construct(db, 1.0)));

                dc1.setOnset(new TimeStamp(db.getTicks(), 660));
                dc1.setOffset(new TimeStamp(db.getTicks(), 720));
                dc1.setVal(Matrix.Construct(db, fdc_mveID,
                                FloatDataValue.Construct(db, 10.0)));

                dc2.setOnset(new TimeStamp(db.getTicks(), 180));
                dc2.setOffset(new TimeStamp(db.getTicks(), 240));
                dc2.setVal(Matrix.Construct(db, fdc_mveID,
                                FloatDataValue.Construct(db, 3.0)));

                db.replaceCell(dc0);
                db.replaceCell(dc1);
                db.replaceCell(dc2);

                // get another test string
                testStringC = db.cl.getColumn(fdcID).toString();

                // get fresh copy of fdc for test purposes
                fdc = db.getDataColumn(fdcID);

                // turn off temporal ordering
                db.setTemporalOrdering(false);

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( testStringA == null ) ||
                 ( expectedString9.compareTo(testStringA) != 0 ) ||
                 ( testStringB == null ) ||
                 ( expectedString10.compareTo(testStringB) != 0 ) ||
                 ( testStringC == null ) ||
                 ( expectedString11.compareTo(testStringC) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf(
                                "%s cell edits 1 failed to complete.\n",
                                header);
                    }

                    if ( testStringA == null )
                    {
                        outStream.printf("%s testStringA is null.\n", header);
                    }
                    else if ( expectedString9.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString9.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }

                    if ( testStringB == null )
                    {
                        outStream.printf("%s testStringB is null.\n", header);
                    }
                    else if ( expectedString10.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString10.\n" +
                             "testString = \"%s\".\n", header, testStringB);
                    }

                    if ( testStringC == null )
                    {
                        outStream.printf("%s testStringC is null.\n", header);
                    }
                    else if ( expectedString11.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString11.\n" +
                             "testString = \"%s\".\n", header, testStringC);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in cell edits 1: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
            else if ( fdc.getNumCells() != 6 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s fdc.getNumCells() returned " +
                            "unexpected value %d (6).\n",
                            header,
                            fdc.getNumCells());
                }
            }
        }


        // 9) Delete two columns -- one with cells and one without.  Add a
        //    column and insert some cells.  Verify that the database contains
        //    the expected data.  Demonstrate the use of db.getColumns() in
        //    passing.
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            testStringA = null;
            testStringB = null;
            testStringC = null;
            testStringD = null;
            testStringE = null;
            testStringF = null;

            try
            {
                // get some test strings
                testStringA = db.toString();
                testStringB = db.getColumns().toString();

                // Delete two columns -- one with cells, one without.  Note
                // that we must delete all cells in a column before we delete
                // it.
                numCells = db.getColumn(fdcID).getNumCells();
                for ( i = 1; i <= numCells; i++ )
                {
                    db.removeCell(db.getCell(fdcID, 1).getID());
                }
                db.removeColumn(fdcID);
                // we know that ndc has no cells, so just remove it.
                db.removeColumn(ndcID);

                // get some more test strings
                testStringC = db.toString();
                testStringD = db.getColumns().toString();

                // allocate and insert a new float data column
                fdc2 = new DataColumn(db, fdcName2,
                        MatrixVocabElement.MatrixType.FLOAT);
                fdc2ID = db.addColumn(fdc2);

                // append some cells to the text data column -- note that
                // temporal ordering is off
                tdc = db.getDataColumn(tdcID);
                tdc_mveID = tdc.getItsMveID();

                db.appendCell(
                    DataCell.Construct(
                        db,
                        tdcID,
                        tdc_mveID,
                        180,
                        240,
                        Matrix.Construct(
                            db,
                            tdc_mveID,
                            TextStringDataValue.Construct(db, "three"))));

                db.appendCell(
                    DataCell.Construct(
                        db,
                        tdcID,
                        tdc_mveID,
                        120,
                        180,
                        Matrix.Construct(
                            db,
                            tdc_mveID,
                            TextStringDataValue.Construct(db, "two"))));

                db.appendCell(
                    DataCell.Construct(
                        db,
                        tdcID,
                        tdc_mveID,
                        60,
                        120,
                        Matrix.Construct(
                            db,
                            tdc_mveID,
                            TextStringDataValue.Construct(db, "one"))));

                // get some more test strings
                testStringE = db.toString();
                testStringF = db.getColumns().toString();

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( fdc2 == null ) ||
                 ( fdc2ID == DBIndex.INVALID_ID ) ||
                 ( testStringA == null ) ||
                 ( expectedString12.compareTo(testStringA) != 0 ) ||
                 ( testStringB == null ) ||
                 ( expectedString13.compareTo(testStringB) != 0 ) ||
                 ( testStringC == null ) ||
                 ( expectedString14.compareTo(testStringC) != 0 ) ||
                 ( testStringD == null ) ||
                 ( expectedString15.compareTo(testStringD) != 0 ) ||
                 ( testStringE == null ) ||
                 ( expectedString16.compareTo(testStringE) != 0 ) ||
                 ( testStringF == null ) ||
                 ( expectedString17.compareTo(testStringF) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf(
                                "%s col deletions 1 failed to complete.\n",
                                header);
                    }

                    if ( fdc2 == null )
                    {
                        outStream.printf("%s allocation of fdc2 failed.\n",
                                header);
                    }
                    else if ( fdc2ID == DBIndex.INVALID_ID )
                    {
                        outStream.printf("%s fdc2ID is invalid!?!\n",
                                header);
                    }

                    if ( testStringA == null )
                    {
                        outStream.printf("%s testStringA is null.\n", header);
                    }
                    else if ( expectedString12.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString12.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }

                    if ( testStringB == null )
                    {
                        outStream.printf("%s testStringB is null.\n", header);
                    }
                    else if ( expectedString13.compareTo(testStringB) != 0 )
                    {
                        outStream.printf(
                             "%s testStringB doesn't match expectedString13.\n" +
                             "testString = \"%s\".\n", header, testStringB);
                    }

                    if ( testStringC == null )
                    {
                        outStream.printf("%s testStringC is null.\n", header);
                    }
                    else if ( expectedString14.compareTo(testStringC) != 0 )
                    {
                        outStream.printf(
                             "%s testStringC doesn't match expectedString14.\n" +
                             "testString = \"%s\".\n", header, testStringC);
                    }

                    if ( testStringD == null )
                    {
                        outStream.printf("%s testStringD is null.\n", header);
                    }
                    else if ( expectedString15.compareTo(testStringD) != 0 )
                    {
                        outStream.printf(
                             "%s testStringD doesn't match expectedString15.\n" +
                             "testString = \"%s\".\n", header, testStringD);
                    }

                    if ( testStringE == null )
                    {
                        outStream.printf("%s testStringE is null.\n", header);
                    }
                    else if ( expectedString16.compareTo(testStringE) != 0 )
                    {
                        outStream.printf(
                             "%s testStringE doesn't match expectedString16.\n" +
                             "testString = \"%s\".\n", header, testStringE);
                    }

                    if ( testStringF == null )
                    {
                        outStream.printf("%s testStringF is null.\n", header);
                    }
                    else if ( expectedString17.compareTo(testStringF) != 0 )
                    {
                        outStream.printf(
                             "%s testStringF doesn't match expectedString17.\n" +
                             "testString = \"%s\".\n", header, testStringF);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in col deletions 1: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }


        // 10) Delete all columns.  Verify that the database is empty.
        if ( failures == 0 )
        {
            completed = false;
            threwSystemErrorException = false;
            testStringA = null;

            try
            {
                // Delete the remaining columns.
                numCells = db.getColumn(tdcID).getNumCells();
                for ( i = 1; i <= numCells; i++ )
                {
                    db.removeCell(db.getCell(tdcID, 1).getID());
                }
                db.removeColumn(tdcID);

                // we know that ndc and fdc2 have no cells, so just remove them.
                db.removeColumn(idcID);
                db.removeColumn(fdc2ID);

                // get a test strings
                testStringA = db.toString();


                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( testStringA == null ) ||
                 ( expectedString18.compareTo(testStringA) != 0 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf(
                                "%s col deletions 2 failed to complete.\n",
                                header);
                    }

                    if ( testStringA == null )
                    {
                        outStream.printf("%s testStringA is null.\n", header);
                    }
                    else if ( expectedString18.compareTo(testStringA) != 0 )
                    {
                        outStream.printf(
                             "%s testStringA doesn't match expectedString18.\n" +
                             "testString = \"%s\".\n", header, testStringA);
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("%s unexpected system error " +
                                "exception in col deletions 2: \"%s\".\n",
                                header, systemErrorExceptionString);
                    }
                }
            }
        }

        return failures;

    } /* MacshapaDatavase::TestAPIExample_01() */


}